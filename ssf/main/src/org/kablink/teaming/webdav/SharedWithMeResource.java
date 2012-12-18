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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author jong
 *
 */
public class SharedWithMeResource extends ContainerResource 
	implements PropFindableResource, GetableResource, CollectionResource {

	protected SharedWithMeResource(WebdavResourceFactory factory,
			String webdavPath) {
		super(factory, webdavPath);
	}

	@Override
	public String getUniqueId() {
		return SPropsUtil.getString("wd.sharedwithme.prefix", "shared_with_me");
	}

	@Override
	public String getName() {
		return getUniqueId();
	}

	@Override
	public Date getModifiedDate() {
		return getCreateDate();
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return factory.getMaxAgeSecondsStatic();
	}

	@Override
	public Date getCreateDate() {
		return getMiltonSafeDate(ReleaseInfo.getBuildDate()); // This is as good as any other random date
	}

	@Override
	public String getWebdavPath() {
		return "/" + getUniqueId();
	}

	@Override
	public Resource child(String childName) throws NotAuthorizedException,
			BadRequestException {
		ShareItemSelectSpec spec = getShareItemSelectSpec();
		List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
		Resource resource = null;
		for(ShareItem shareItem:shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
            	FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
        		// Consider the entry only if it's contained in a library folder
            	if(entry.getParentBinder().isLibrary()) {
            		Set<Attachment> attachments = entry.getAttachments();
            		for(Attachment attachment:attachments) {
            			if(attachment instanceof FileAttachment) {
            				FileAttachment fa = (FileAttachment)attachment;
            				if(fa.getFileItem().getName().equals(childName)) {
            					resource = makeResourceFromFile(fa);
            					break;
            				}
            			}
            		}
            	}
            }
            else if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folder) {
            	Folder folder = (Folder) getSharingModule().getSharedEntity(shareItem);
            	if(folder.getTitle().equals(childName)) {		
            		resource = makeResourceFromBinder(folder);
            		break;
            	}
            }
		}
		return resource;
	}

	@Override
	public List<? extends Resource> getChildren()
			throws NotAuthorizedException, BadRequestException {
		ShareItemSelectSpec spec = getShareItemSelectSpec();
		List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
		List<Resource> childrenResources = new ArrayList<Resource>();
		Resource resource;
		for(ShareItem shareItem:shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
            	FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
        		// Expose the file only if it's contained in a library folder
            	if(entry.getParentBinder().isLibrary()) {
            		Set<Attachment> attachments = entry.getAttachments();
            		for(Attachment attachment:attachments) {
            			if(attachment instanceof FileAttachment) {
                    		resource = makeResourceFromFile((FileAttachment)attachment);
                    		if(resource != null)
                    			childrenResources.add(resource);
            			}
            		}
            	}
            }
            else if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folder) {
            	Folder folder = (Folder) getSharingModule().getSharedEntity(shareItem);
            	resource = makeResourceFromBinder(folder);
            	if(resource != null)
            		childrenResources.add(resource);
            }
		}
		return childrenResources;
	}

	protected ShareItemSelectSpec getShareItemSelectSpec() {
		ShareItemSelectSpec spec = new ShareItemSelectSpec();
		spec.setRecipientsFromUserMembership(RequestContextHolder.getRequestContext().getUserId());
		spec.setLatest(true);
		spec.excludeExpired();
		return spec;
	}
	
	
}
