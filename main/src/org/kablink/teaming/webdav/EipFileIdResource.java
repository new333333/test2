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

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.security.AccessControlException;

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
public class EipFileIdResource extends WebdavResource implements PropFindableResource, PutableResource {

	private FileAttachment fa;
	
	public EipFileIdResource(WebdavResourceFactory factory, FileAttachment fa) {
		super(factory, EipResource.WEBDAV_PATH + "/" + fa.getId(), fa.getId());
		this.fa = fa;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return "efi:" + fa.getId();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	@Override
	public Date getModifiedDate() {
		return getMiltonSafeDate(fa.getModification().getDate());
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#child(java.lang.String)
	 */
	@Override
	public Resource child(String childName) throws NotAuthorizedException,
			BadRequestException {
		if(fa.getFileItem().getName().equals(childName))
			return new EipFileNameResource(factory, fa); // just a different view of the same underlying data... 
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#getChildren()
	 */
	@Override
	public List<? extends Resource> getChildren()
			throws NotAuthorizedException, BadRequestException {
		// There is always one and only one child.
		List<Resource> children = new ArrayList<Resource>(1);
		children.add(new EipFileNameResource(factory, fa));
		return children;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PutableResource#createNew(java.lang.String, java.io.InputStream, java.lang.Long, java.lang.String)
	 */
	@Override
	public Resource createNew(String newName, InputStream inputStream,
			Long length, String contentType) throws IOException,
			ConflictException, NotAuthorizedException, BadRequestException {
		// Given the way EIP resources are designed, this method can never be used to create a new
		// file but only to modify an existing file. Also, only files attached to entries can be
		// edited this way.
		DefinableEntity entity = fa.getOwner().getEntity();
		EntityType entityType = entity.getEntityType();
		String filename = fa.getFileItem().getName();
		String dataName = fa.getName();
		if(dataName == null)
			dataName = "ss_attachFile1";
		
		try {
			if(EntityType.folderEntry == entityType) {
				FileUtils.modifyFolderEntryWithFile((FolderEntry)entity, dataName, filename, inputStream, null, null);
			}
			else if(EntityType.user == entityType || EntityType.group == entityType) { // principal	
				FileUtils.modifyPrincipalWithFile((Principal)entity, dataName, filename, inputStream, null, null);
			}
			else if(EntityType.workspace == entityType || EntityType.folder == entityType || EntityType.profiles == entityType) { // binder	
				FileUtils.modifyBinderWithFile((Binder)entity, dataName, filename, inputStream);
			}
			else {		
				throw new ConflictException(this, "This file is attached to an entity whose type is unsupported: " + entity.getEntityIdentifier().toString());
			}
			return new EipFileNameResource(factory, fa);
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
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	@Override
	public Date getCreateDate() {
		return getMiltonSafeDate(fa.getCreation().getDate());
	}

}
