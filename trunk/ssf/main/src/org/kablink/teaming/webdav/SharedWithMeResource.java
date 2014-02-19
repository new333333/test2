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
import java.util.Collections;
import java.util.Comparator;
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

	private static final String ENTRY_ID_PREFIX = "1"; // folder entry namespace
	private static final String BINDER_ID_PREFIX = "2"; // binder (folder/workspace) namespace
	
	public SharedWithMeResource(WebdavResourceFactory factory) {
		super(factory, "/" + factory.getSharedWithMePrefix(), factory.getSharedWithMePrefix());
	}

	@Override
	public String getUniqueId() {
		return this.factory.getSharedWithMePrefix();
	}

	@Override
	public Date getModifiedDate() {
		// $$$We don't have this information for Shared With Me container. So, let's just return
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
		ShareItemSelectSpec spec = getShareItemSelectSpec();
		List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
		WebdavResource resource = null;
		for(ShareItem shareItem:shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
            	FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
        		// Consider the entry only if it's contained in a library folder
            	if(entry.getParentBinder().isLibrary()) {
            		Set<Attachment> attachments = entry.getAttachments();
            		for(Attachment attachment:attachments) {
            			if(attachment instanceof FileAttachment) {
            				FileAttachment fa = (FileAttachment)attachment;
            				String fileName = fa.getFileItem().getName();
            				if(childName.equals(fileName)) {
            					resource = makeResourceFromFile(fa);
            					break;
            				}
            				else if(childName.equals(fixedupFileName(fileName, fa.getOwner().getEntity().getId()))) {
            					resource = makeResourceFromFile(fa);
            					resource.fixupName(childName);
            					break;
            				}
            			}
            		}
            	}
            }
            else if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folder) {
            	Folder folder = (Folder) getSharingModule().getSharedEntity(shareItem);
            	String folderName = folder.getTitle();
            	if(childName.equals(folderName)) {		
            		resource = makeResourceFromBinder(folder);
            		break;
            	}
            	else if(childName.equals(fixedupBinderName(folderName, folder.getId()))) {
            		resource = makeResourceFromBinder(folder);
            		resource.fixupName(childName);
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
        		// Expose the file only if it's contained in a library folder AND the enclosing share is not hidden.
            	if(entry.getParentBinder().isLibrary() &&
            			!getSharingModule().isSharedEntityHidden(entry, true)) {
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
            	// Expose the folder only if the enclosing share is not hidden.
            	if(!getSharingModule().isSharedEntityHidden(folder, true)) {
	            	resource = makeResourceFromBinder(folder);
	            	if(resource != null)
	            		childrenResources.add(resource);
            	}
            }
		}
		Collections.sort(childrenResources, new Comparator<Resource>() {
			@Override
			public int compare(Resource r1, Resource r2) {
				return r1.getName().compareToIgnoreCase(r2.getName());
			};			
		});
		int size = childrenResources.size();
		boolean sameAsPrev = false;
		for(int i = 0; i < size; i++) {
			resource = childrenResources.get(i);
			if(i < size-1) {
				if(resource.getName().equalsIgnoreCase(childrenResources.get(i+1).getName())) {
					fixupResourceName(resource);
					// Indicate that the element in the next iteration will be identical to the previous one.
					sameAsPrev = true;
				}
				else {
					if(sameAsPrev) {
						fixupResourceName(resource);
						sameAsPrev = false;
					}
				}
			}
			else {
				if(sameAsPrev) {
					fixupResourceName(resource);
					sameAsPrev = false;
				}				
			}
		}
		return childrenResources;
	}

	private void fixupResourceName(Resource resource) {
		if(resource instanceof FileResource) {
			FileResource fr = (FileResource)resource;
			String newName = fixedupFileName(fr.getName(), fr.getEntryId());
			fr.fixupName(newName);
		}
		else if(resource instanceof BinderResource) {
			BinderResource br = (BinderResource)resource;
			String newName = fixedupBinderName(br.getName(), br.getBinderId());
			br.fixupName(newName);
		}
	}
	
	private String fixedupFileName(String fileName, Long entityId) {
		StringBuilder sb = new StringBuilder();
		int index = fileName.lastIndexOf(".");
		if(index >= 0) {
			sb.append(fileName.subSequence(0, index))
			.append(" (")
			.append(ENTRY_ID_PREFIX)
			.append(entityId)
			.append(")")
			.append(fileName.substring(index));
		}
		else {
			sb.append(fileName)
			.append(" (")
			.append(ENTRY_ID_PREFIX)
			.append(entityId)
			.append(")");
		}
		return sb.toString();
	}
	
	private String fixedupBinderName(String binderName, Long binderId) {
		StringBuilder sb = new StringBuilder();
		return sb.append(binderName)
		.append(" (")
		.append(BINDER_ID_PREFIX)
		.append(binderId)
		.append(")")
		.toString();
	}
	
	protected ShareItemSelectSpec getShareItemSelectSpec() {
		ShareItemSelectSpec spec = new ShareItemSelectSpec();
		spec.setRecipientsFromUserMembership(RequestContextHolder.getRequestContext().getUserId());
		spec.setLatest(true);
		spec.excludeExpired();
		return spec;
	}
	
	
}
