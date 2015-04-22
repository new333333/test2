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
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoWorkspaceByTheIdException;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FolderUtils;
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
public class WorkspaceResource extends BinderResource  
implements PropFindableResource, GetableResource, CollectionResource, PutableResource, MakeCollectionableResource, DeletableResource, CopyableResource, MoveableResource, DeletableCollectionResource {
	
	private static final Log logger = LogFactory.getLog(WorkspaceResource.class);
	
	private static final boolean WORKSPACE_DELETION_ALLOW_DEFAULT = false;
	private static final boolean WORKSPACE_DELETION_PURGE_IMMEDIATELY = false;
	private static final boolean WORKSPACE_COPY_ALLOW_DEFAULT = false;
	private static final boolean WORKSPACE_MOVE_ALLOW_DEFAULT = false;

	// lazy resolved for efficiency, so may be null initially
	private Workspace ws;
	
	public WorkspaceResource(WebdavResourceFactory factory, String webdavPath, Workspace ws) {
		super(factory, webdavPath, ws);
		this.ws = ws; // already resolved
	}

	public WorkspaceResource(WebdavResourceFactory factory, String webdavPath, BinderIndexData bid) {
		super(factory, webdavPath, bid);
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http.Auth)
	 */
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return factory.getMaxAgeSecondsWorkspace();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#child(java.lang.String)
	 */
	@Override
	public Resource child(String childName) throws NotAuthorizedException,
			BadRequestException {
		try {
			Binder child = getBinderModule().getBinderByPathName(path + "/" + childName);
			return makeResourceFromBinder(child);
		}
		catch(AccessControlException e) {
			// The specified child physically exists, but the user has no read access to it.
			// In this case, we treat it as if the child didn't exist in the first place
			// as opposed to throwing an error.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#getChildren()
	 */
	@Override
	public List<? extends Resource> getChildren()
			throws NotAuthorizedException, BadRequestException {
		// A workspace can have other workspaces and/or folders as children
		Map<String,BinderIndexData> childrenMap = getChildrenBinderDataFromIndex(id);
		List<Resource> childrenResources = new ArrayList<Resource>(childrenMap.size());
		Resource resource;
		for(BinderIndexData bid:childrenMap.values()) {
			resource = makeResourceFromBinder(bid);
			if(resource != null)
				childrenResources.add(resource);
		}
		return childrenResources;
	}

	@Override
    public CollectionResource createCollection(String newName) throws NotAuthorizedException, ConflictException, BadRequestException {
		Workspace parentWorkspace = resolveWorkspace();
		
		if(EntityType.profiles == parentWorkspace.getEntityType())
			throw new ConflictException(this, "Can not create a folder in the profiles binder");
		
		try {
			Binder folder = FolderUtils.createLibraryFolder(parentWorkspace, newName);
			
			return new FolderResource(factory, getWebdavPath() + "/" + newName, (Folder) folder);
		} catch (ConfigurationException e) {
			throw e;
		} catch (AccessControlException e) {
			throw new NotAuthorizedException(this);
		} catch (WriteFilesException e) {
			throw new WebdavException(e.getLocalizedMessage());
		} catch (WriteEntryDataException e) {
			throw new WebdavException(e.getLocalizedMessage());			
		}

	}
	
	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		if(Method.DELETE == method) {
			boolean allowWorkspaceDeletion = SPropsUtil.getBoolean("wd.workspace.deletion.allow", WORKSPACE_DELETION_ALLOW_DEFAULT);
			if(allowWorkspaceDeletion) 
				return super.authorise(request, method, auth); // system permits workspace deletion, do regular checking
			else
				return false; // system doesn't permit workspace deletion for anyone on any workspace
		}
		else if(Method.COPY == method) {
			boolean allowWorkspaceCopy = SPropsUtil.getBoolean("wd.workspace.copy.allow", WORKSPACE_COPY_ALLOW_DEFAULT);
			if(allowWorkspaceCopy) 
				return super.authorise(request, method, auth); // system permits workspace copy, do regular checking
			else
				return false; // system doesn't permit workspace copy for anyone on any workspace			
		}
		else if(Method.MOVE == method) {
			boolean allowWorkspaceMove = SPropsUtil.getBoolean("wd.workspace.move.allow", WORKSPACE_MOVE_ALLOW_DEFAULT);
			if(allowWorkspaceMove) 
				return super.authorise(request, method, auth); // system permits workspace move, do regular checking
			else
				return false; // system doesn't permit workspace move for anyone on any folder			
		}
		else {
			return super.authorise(request, method, auth);
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.DeletableResource#delete()
	 */
	@Override
	public void delete() throws NotAuthorizedException, ConflictException,
			BadRequestException {
		boolean purgeImmediately = SPropsUtil.getBoolean("wd.workspace.deletion.purge.immediately", WORKSPACE_DELETION_PURGE_IMMEDIATELY);
		if(purgeImmediately) {
			getBinderModule().deleteBinder(id);
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
			if(toCollection instanceof FolderResource) {
				throw new ConflictException(this, "Can not copy a workspace into a folder");
			}
			else if(toCollection instanceof WorkspaceResource) {
				EntityIdentifier toCollectionEntityIdentifier = ((WorkspaceResource)toCollection).getEntityIdentifier();
				if(EntityType.profiles == toCollectionEntityIdentifier.getEntityType()) {
					throw new ConflictException(this, "Can not copy a workspace into the profiles binder");
				}
				else {
					HashMap options = new HashMap();
					options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, name);
					Binder newBinder = getBinderModule().copyBinder(id, toCollectionEntityIdentifier.getEntityId(), true, options);
				}
			}
			else {
				throw new ConflictException(this, "Destination is an unknown type '" + toCollection.getClass().getName() + "'. Must be a workspace resource.");
			}
		}
		catch(AccessControlException e) {
			throw new NotAuthorizedException(this);
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
			if(rDest instanceof WorkspaceResource) {
				Workspace workspace = resolveWorkspace();
				EntityIdentifier destWorkspaceIdentifier = ((WorkspaceResource) rDest).getEntityIdentifier();
				if(workspace.getParentBinder() != null && workspace.getParentBinder().getId().equals(destWorkspaceIdentifier.getEntityId())) {
					// This is mere renaming of this workspace.
					renameBinder(workspace, name);
				}
				else { // This is a move
					if(EntityType.profiles == destWorkspaceIdentifier.getEntityType()) {
						throw new ConflictException(this, "Can not move a workspace into the profiles binder");
					}
					else {
						HashMap options = new HashMap();
						options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, name);
						getBinderModule().moveBinder(id, destWorkspaceIdentifier.getEntityId(), options);
					}
				}				
			}
			else if(rDest instanceof FolderResource) {
				// Moving a workspace into a folder is not allowed.
				throw new ConflictException(this, "Can not move a workspace into a folder");
			}
			else {
				throw new ConflictException(this, "Destination is an unknown type '" + rDest.getClass().getName() + "'. Must be a workspace resource.");								
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
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PutableResource#createNew(java.lang.String, java.io.InputStream, java.lang.Long, java.lang.String)
	 */
	@Override
	public Resource createNew(String newName, InputStream inputStream,
			Long length, String contentType) throws IOException,
			ConflictException, NotAuthorizedException, BadRequestException {
		// Reject PUT by throwing this exception since Vibe does not support adding files in a workspace.
		// This unfortunate workaround is necessary, because Windows Explorer on Windows 7 ignores
		// the correct "501 Not Implemented" status code returned by Milton when this class did not 
		// implement PutableResource interface. 
		throw new ConflictException(this, "Can not write a file in a workspace");
	}
	
	private Workspace resolveWorkspace() throws NoWorkspaceByTheIdException {
		if(ws == null) {
			// Load it directly from DAO without further access check, since access check
			// was already performed at the time this instance was created. Resource object
			// is created only after the system determines by looking up the database or
			// Lucene index that the user making request has read access to the resource.
			//ws = getWorkspaceModule().getWorkspace(entityIdentifier.getEntityId());
			ws = (Workspace) getCoreDao().loadBinder(id,  RequestContextHolder.getRequestContext().getZoneId());
	        if(ws == null || ws.isDeleted() || ws.isPreDeleted())
	        	throw new NoWorkspaceByTheIdException(id);
		}
		return ws;
	}

}
