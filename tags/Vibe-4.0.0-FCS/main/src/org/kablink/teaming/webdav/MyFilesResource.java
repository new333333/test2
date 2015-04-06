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
import java.util.List;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.util.search.Criteria;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.MakeCollectionableResource;
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
public class MyFilesResource extends ContainerResource 
	implements PropFindableResource, GetableResource, CollectionResource, PutableResource, MakeCollectionableResource {

	public MyFilesResource(WebdavResourceFactory factory) {
		super(factory, "/" + factory.getMyFilesPrefix(), factory.getMyFilesPrefix());
	}

	@Override
	public String getUniqueId() {
		return this.factory.getMyFilesPrefix();
	}

	@Override
	public Date getModifiedDate() {
		// $$$We don't have this information for My Files container. So, let's just return
		// current time to force WebDAV client to always come and get the latest view.
		//return new Date();
		
		return getCreateDate();
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		// Share the setting with regular folders.
		return factory.getMaxAgeSecondsFolder();
	}

	@Override
	public Date getCreateDate() {
		return getMiltonSafeDate(ReleaseInfo.getBuildDate()); // This is as good as any other random date
	}

	@Override
	public Resource child(String childName) throws NotAuthorizedException,
			BadRequestException {
        // Get folders contained in My Files view
        Criteria myFoldersCrit = SearchUtils.getMyFilesSearchCriteria(this, RequestContextHolder.getRequestContext().getUser().getWorkspaceId(), true, false, false, false);
        List<BinderIndexData> bidList = getBinderDataFromIndex(myFoldersCrit, false, null);
        for(BinderIndexData bid : bidList) {
        	if(bid.getTitle().equals(childName))
            	return makeResourceFromBinder(bid);
        }
		
		// Get files contained in My Files view
        Criteria myFilesCrit = SearchUtils.getMyFilesSearchCriteria(this, RequestContextHolder.getRequestContext().getUser().getWorkspaceId(), false, false, false, true);
        List<FileIndexData> fidList = getFileModule().getFileDataFromIndex(myFilesCrit);
		for(FileIndexData fid : fidList) {
			if(fid.getName().equals(childName))
				return makeResourceFromFile(fid);
		}

		return null;
	}

	@Override
	public List<? extends Resource> getChildren()
			throws NotAuthorizedException, BadRequestException {
		List<Resource> childrenResources = new ArrayList<Resource>();
		Resource resource;
		
		if(SearchUtils.useHomeAsMyFiles(this)) {
            List<Long> homeFolderIds = SearchUtils.getHomeFolderIds(this, RequestContextHolder.getRequestContext().getUser());
            for (Long id : homeFolderIds) {
            	try {
	                Folder folder = getFolderModule().getFolder(id);
	                getFolderModule().jitSynchronize(folder);
            	}
            	catch(Exception e) {}
            }
		}
		
		// Get folders
        Criteria myFoldersCrit = SearchUtils.getMyFilesSearchCriteria(this, RequestContextHolder.getRequestContext().getUser().getWorkspaceId(), true, false, false, false);
        List<BinderIndexData> bidList = getBinderDataFromIndex(myFoldersCrit, false, null);
        for(BinderIndexData bid : bidList) {
        	resource = makeResourceFromBinder(bid);
        	if(resource != null)
        		childrenResources.add(resource);
        }
		
		// Get files
        Criteria myFilesCrit = SearchUtils.getMyFilesSearchCriteria(this, RequestContextHolder.getRequestContext().getUser().getWorkspaceId(), false, false, false, true);
        List<FileIndexData> fidList = getFileModule().getFileDataFromIndex(myFilesCrit);
		for(FileIndexData fid : fidList) {
			resource = makeResourceFromFile(fid);
			if(resource != null)
				childrenResources.add(resource);
		}
		
		return childrenResources;
	}

	@Override
	public CollectionResource createCollection(String newName)
			throws NotAuthorizedException, ConflictException,
			BadRequestException {
		Binder binder = getMyFilesFolderParent();
		
		return createChildFolder(binder, newName);
	}

	@Override
	public Resource createNew(String newName, InputStream inputStream,
			Long length, String contentType) throws IOException,
			ConflictException, NotAuthorizedException, BadRequestException {
		Folder folder =  getMyFilesFileParent();
		
 		FolderEntry entry = writeFileWithModDate(folder, newName, inputStream, null);
		return makeResourceFromFile(entry.getFileAttachment(newName));
	}

    public Binder getMyFilesFolderParent() throws ConflictException {
    	User user = RequestContextHolder.getRequestContext().getUser();
        if (SearchUtils.useHomeAsMyFiles(this, user)) {
            return getFolder(SearchUtils.getHomeFolderId(this, user.getWorkspaceId())); // user home folder
        } else {
            return getWorkspace(user.getWorkspaceId()); // user workspace
        }
    }

    public Folder getMyFilesFileParent() throws ConflictException {
    	User user = RequestContextHolder.getRequestContext().getUser();
        if (SearchUtils.useHomeAsMyFiles(this, user)) {
            return getFolder(SearchUtils.getHomeFolderId(this, user.getWorkspaceId())); // user home folder
        } else {
            return getFolder(SearchUtils.getMyFilesFolderId(this, user, true)); // user hidden file folder
        }
    }

	private Folder getFolder(Long folderId) throws ConflictException {
		if(folderId == null)
			throw new ConflictException("No folder id");
		Binder binder = getBinderModule().getBinder(folderId);
		if(binder instanceof Folder) {
			Folder folder = (Folder) binder;
			if(folder.isDeleted() || folder.isPreDeleted())
				throw new ConflictException("Folder '" + folderId + "' not found");
			else
				return folder;
		}
		else {
			throw new ConflictException("id '" + folderId + "' does not represent a folder");
		}
	}

	private Workspace getWorkspace(Long workspaceId) throws ConflictException {
		if(workspaceId == null)
			throw new ConflictException("No workspace id");
		Binder binder = getBinderModule().getBinder(workspaceId);
		if(binder instanceof Workspace) {
			Workspace workspace = (Workspace) binder;
			if(workspace.isDeleted() || workspace.isPreDeleted())
				throw new ConflictException("Workspace '" + workspaceId + "' not found");
			else
				return workspace;
		}
		else {
			throw new ConflictException("id '" + workspaceId + "' does not represent a workspace");
		}
	}

}
