/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.TrashHelper;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.CopyableResource;
import com.bradmcevoy.http.DeletableCollectionResource;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.MakeCollectionableResource;
import com.bradmcevoy.http.MoveableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.PutableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author jong
 *
 */
public class FolderResource extends BinderResource 
implements PropFindableResource, GetableResource, CollectionResource, PutableResource, MakeCollectionableResource, DeletableResource, CopyableResource, MoveableResource, DeletableCollectionResource {
	
	private static final Log logger = LogFactory.getLog(FolderResource.class);
	
	private static final boolean FOLDER_DELETION_ALLOW_DEFAULT = true;
	private static final boolean FOLDER_DELETION_PURGE_IMMEDIATELY = false;
	private static final boolean FOLDER_COPY_ALLOW_DEFAULT = true;
	private static final boolean FOLDER_MOVE_ALLOW_DEFAULT = true;

	// lazy resolved for efficiency, so may be null initially
	private Folder folder;
	
	public FolderResource(WebdavResourceFactory factory, String webdavPath, Folder folder) {
		super(factory, webdavPath, folder);
	}

	public FolderResource(WebdavResourceFactory factory, String webdavPath, BinderIndexData bid) {
		super(factory, webdavPath, bid);
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http.Auth)
	 */
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return factory.getMaxAgeSecondsFolder();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#child(java.lang.String)
	 */
	@Override
	public Resource child(String childName) throws NotAuthorizedException,
			BadRequestException {
		resolveFolder();
		
		Resource child = childFolder(childName);
		if(child == null)
			child = childFile(childName);
		return child;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#getChildren()
	 */
	@Override
	public List<? extends Resource> getChildren()
			throws NotAuthorizedException, BadRequestException {
		// A folder can have other folders as children. It can also have files as children only if it is a library folder.
		Map<String,BinderIndexData> binderMap = getChildrenBinderDataFromIndex(id);
		
		Map<String,FileIndexData> fileMap = null;
		if(library) 
			fileMap = getFileModule().getChildrenFileDataFromIndex(id);
		else
			fileMap = new HashMap<String,FileIndexData>();
		
		List<Resource> childrenResources = new ArrayList<Resource>(binderMap.size());
		Resource resource;
		for(BinderIndexData bid:binderMap.values()) {
			resource = makeResourceFromBinder(bid);
			if(resource != null)
				childrenResources.add(resource);
		}
		
		for(FileIndexData fid:fileMap.values()) {
			resource = makeResourceFromFile(fid);
			if(resource != null)
				childrenResources.add(resource);
		}
		
		return childrenResources;
	}

	@Override
    public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
		// When you create a brand new binder through WebDAV, it will always be of file folder type. 
		// It can never be a workspace or folder of non-file type, except when created indirectly via copy/move functions.
		Folder parentFolder = resolveFolder();
		
		return createChildFolder(parentFolder, newName);
	}

	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		if(Method.DELETE == method) {
			boolean allowFolderDeletion = SPropsUtil.getBoolean("wd.folder.deletion.allow", FOLDER_DELETION_ALLOW_DEFAULT);
			if(allowFolderDeletion) 
				return super.authorise(request, method, auth); // system permits folder deletion, do regular checking
			else
				return false; // system doesn't permit folder deletion for anyone on any folder
		}
		else if(Method.COPY == method) {
			boolean allowFolderCopy = SPropsUtil.getBoolean("wd.folder.copy.allow", FOLDER_COPY_ALLOW_DEFAULT);
			if(allowFolderCopy) 
				return super.authorise(request, method, auth); // system permits folder copy, do regular checking
			else
				return false; // system doesn't permit folder copy for anyone on any folder			
		}
		else if(Method.MOVE == method) {
			boolean allowFolderMove = SPropsUtil.getBoolean("wd.folder.move.allow", FOLDER_MOVE_ALLOW_DEFAULT);
			if(allowFolderMove) 
				return super.authorise(request, method, auth); // system permits folder move, do regular checking
			else
				return false; // system doesn't permit folder move for anyone on any folder			
		}
		else {
			return super.authorise(request, method, auth);
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PutableResource#createNew(java.lang.String, java.io.InputStream, java.lang.Long, java.lang.String)
	 */
	@Override
	public Resource createNew(String newName, InputStream inputStream,
			Long length, String contentType) throws IOException,
			ConflictException, NotAuthorizedException, BadRequestException {
		FolderEntry entry = createNewWithModDate(newName, inputStream, null);
		
		return makeResourceFromFile(entry.getFileAttachment(newName));
	}
	
	public FolderEntry createNewWithModDate(String newName, InputStream inputStream, Date modDate) throws IOException,
			ConflictException, NotAuthorizedException, BadRequestException {
		resolveFolder();
		
		return writeFileWithModDate(folder, newName, inputStream, modDate);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.DeletableResource#delete()
	 */
	@Override
	public void delete() throws NotAuthorizedException, ConflictException,
			BadRequestException {
		Folder folder = resolveFolder();
		boolean purgeImmediately = SPropsUtil.getBoolean("wd.folder.deletion.purge.immediately", FOLDER_DELETION_PURGE_IMMEDIATELY);
		if(folder.isMirrored() || purgeImmediately) {
			getBinderModule().deleteBinder(id, true, null);
		}
		else {
			try {
				TrashHelper.preDeleteBinder(this, id);
			} catch (Exception e) {
				throw new WebdavException(e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CopyableResource#copyTo(com.bradmcevoy.http.CollectionResource, java.lang.String)
	 */
	@Override
	public void copyTo(CollectionResource toCollection, String name)
			throws NotAuthorizedException, BadRequestException,
			ConflictException {
		try {
			Binder newBinder = null;
			HashMap options = new HashMap();
			options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, name);
			if(toCollection instanceof FolderResource) { // Copy a folder into another folder
				newBinder = getBinderModule().copyBinder(id, ((FolderResource) toCollection).getEntityIdentifier().getEntityId(), true, options);
			}
			else if(toCollection instanceof WorkspaceResource) { // Copy a folder into a workspace
				EntityIdentifier toCollectionEntityIdentifier = ((WorkspaceResource)toCollection).getEntityIdentifier();
				if(EntityType.profiles == toCollectionEntityIdentifier.getEntityType()) {
					throw new ConflictException(this, "Can not copy a folder into the profiles binder");
				}
				else {
					newBinder = getBinderModule().copyBinder(id, toCollectionEntityIdentifier.getEntityId(), true, options);
				}
			}
			else if(toCollection instanceof MyFilesResource) { // Copy a folder into MyFiles container
				newBinder = getBinderModule().copyBinder(id, 
						((MyFilesResource) toCollection).getMyFilesFolderParent().getEntityIdentifier().getEntityId(), true, options);				
			}
			else {
				throw new ConflictException(this, "Destination is an unknown type '" + toCollection.getClass().getName() + "'. Must be a binder resource.");
			}
		}
		catch(AccessControlException e) {
			throw new NotAuthorizedException(this);
		}
	}
	
	private void doMoveTo(EntityIdentifier destBinderIdentifier, String name) 
			throws ConflictException, NotAuthorizedException, BadRequestException,
			AccessControlException, WriteFilesException, WriteEntryDataException {
		Folder folder = resolveFolder();
		if(folder.getParentBinder() != null && folder.getParentBinder().getId().equals(destBinderIdentifier.getEntityId())) {
			// This is mere renaming of this folder.
			renameBinder(folder, name);
		}
		else { // This is a move
			HashMap options = new HashMap();
			options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, name);
			EntityType destBinderType = destBinderIdentifier.getEntityType();
			if(destBinderType == EntityType.folder ||
					destBinderType == EntityType.workspace) {
				getBinderModule().moveBinder(id, destBinderIdentifier.getEntityId(), options);
			}
			else {
				throw new ConflictException(this, "Can not move a folder into a binder of type '" + destBinderType.name() + "'");
			}					
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.MoveableResource#moveTo(com.bradmcevoy.http.CollectionResource, java.lang.String)
	 */
	@Override
	public void moveTo(CollectionResource rDest, String name)
			throws ConflictException, NotAuthorizedException,
			BadRequestException {
		try {
			EntityIdentifier destBinderIdentifier;
			if(rDest instanceof BinderResource) {
				destBinderIdentifier = ((BinderResource) rDest).getEntityIdentifier();
				doMoveTo(destBinderIdentifier, name);
			}
			else if(rDest instanceof MyFilesResource) {
				destBinderIdentifier = ((MyFilesResource)rDest).getMyFilesFolderParent().getEntityIdentifier();
				doMoveTo(destBinderIdentifier, name);
			}
			else {
				throw new ConflictException(this, "Destination is an unknown type '" + rDest.getClass().getName() + "'. Must be a binder resource.");				
			}
		}
		catch(AccessControlException e) {
			throw new NotAuthorizedException(this);
		} catch (WriteFilesException e) {
			throw new WebdavException(e.getLocalizedMessage());
		} catch (WriteEntryDataException e) {
			throw new WebdavException(e.getLocalizedMessage());
		}
	}

	public Folder getFolder() {
		return resolveFolder();
	}
	
	private Resource childFolder(String childName) {
		// Try fetching the child as a sub-folder
		try {
			Binder child = getBinderModule().getBinderByPathName(path + "/" + childName);
			return makeResourceFromBinder(child);
		} catch (AccessControlException e) {
			// The specified child physically exists, but the user has no read access to it. 
			// In this case, we treat it as if the child didn't exist in the first place
			// as opposed to throwing an error.
			return null;
		}
	}

	private Resource childFile(String childName) {
		FolderEntry entry = getFolderModule().getLibraryFolderEntryByFileName(folder, childName);
		if(entry != null)
			return makeResourceFromFile(entry.getFileAttachment(childName));
		else
			return null;
	}
	
	private Folder resolveFolder() throws NoFolderByTheIdException {
		if(folder == null) {
			// Load it directly from DAO without further access check, since access check
			// was already performed at the time this instance was created. Resource object
			// is created only after the system determines by looking up the database or
			// Lucene index that the user making request has read access to the resource.
			//folder = getFolderModule().getFolder(entityIdentifier.getEntityId());
	        folder = getFolderDao().loadFolder(id, RequestContextHolder.getRequestContext().getZoneId());
	        if(folder == null || folder.isDeleted() || folder.isPreDeleted())
	        	throw new NoFolderByTheIdException(id);
		}
		return folder;
	}
	
}
