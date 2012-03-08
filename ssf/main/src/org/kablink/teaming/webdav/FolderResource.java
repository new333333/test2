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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.security.AccessControlException;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.PutableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author jong
 *
 */
public class FolderResource extends BinderResource implements PropFindableResource, GetableResource, CollectionResource, PutableResource {
	
	private static final Log logger = LogFactory.getLog(FolderResource.class);
	
	// lazy resolved for efficiency, so may be null initially
	private Folder folder;
	
	public FolderResource(WebdavResourceFactory factory, Folder folder) {
		super(factory, folder);
	}

	public FolderResource(WebdavResourceFactory factory, BinderIndexData bid) {
		super(factory, bid);
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
		Map<String,BinderIndexData> binderMap = getBinderModule().getChildrenBinderDataFromIndex(id);
		
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
	
	private Resource makeResourceFromFile(FileAttachment fa) {
		if(fa == null)
			return null;
		else
			return new FileResource(factory, getWebdavPath() + "/" + fa.getFileItem().getName(), fa);
	}
	
	private Resource makeResourceFromFile(FileIndexData file) {
		if(file == null)
			return null;
		else
			return new FileResource(factory, getWebdavPath() + "/" + file.getName(), file);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PutableResource#createNew(java.lang.String, java.io.InputStream, java.lang.Long, java.lang.String)
	 */
	@Override
	public Resource createNew(String newName, InputStream inputStream,
			Long length, String contentType) throws IOException,
			ConflictException, NotAuthorizedException, BadRequestException {
		resolveFolder();
		
		if(!folder.isLibrary())
			throw new BadRequestException(this, "This folder is not a library folder");
		
		FolderEntry entry = getFolderModule().getLibraryFolderEntryByFileName(folder, newName);
		
		try {
			if(entry != null) {
				// An entry containing a file with this name exists.
				if(logger.isDebugEnabled())
					logger.debug("createNew: updating existing file '" + newName + "' + owned by " + entry.getEntityIdentifier().toString() + " in folder " + id);
				FolderUtils.modifyLibraryEntry(entry, newName, inputStream, null, true);
			}
			else {
				// We need to create a new entry
				if(logger.isDebugEnabled())
					logger.debug("createNew: creating new file '" + newName + "' + in folder " + id);
				FolderUtils.createLibraryEntry(folder, newName, inputStream, null, true);
			}
		}
		catch (AccessControlException e) {
			throw new NotAuthorizedException(this);
		} catch (ReservedByAnotherUserException e) {
			throw new ConflictException(this, e.getLocalizedMessage());
		} catch (WriteFilesException e) {
			throw new WebdavException(e.getLocalizedMessage());
		} catch (WriteEntryDataException e) {
			throw new WebdavException(e.getLocalizedMessage());
		}
		
		return childFile(newName);
	}
	
}
