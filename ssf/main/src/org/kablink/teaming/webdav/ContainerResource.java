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
import java.util.Date;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.security.AccessControlException;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author jong
 *
 */
public abstract class ContainerResource extends WebdavCollectionResource implements PropFindableResource, GetableResource, CollectionResource {

	protected String webdavPath;

	protected ContainerResource(WebdavResourceFactory factory, String webdavPath) {
		super(factory);
		this.webdavPath = webdavPath;
	}

	@Override
	public String getWebdavPath() {
		return webdavPath;
	}

	protected Resource makeResourceFromBinder(Binder binder) {
		if(binder == null)
			return null;
		
		if(binder instanceof Workspace) {
			Workspace w = (Workspace) binder;
			if(w.isDeleted() || w.isPreDeleted())
				return null;
			else 
				return new WorkspaceResource(factory, getChildWebdavPath(binder.getTitle()), w);
		}
		else if(binder instanceof Folder) {
			Folder f = (Folder) binder;
			if(f.isDeleted() || f.isPreDeleted())
				return null;
			else 
				return new FolderResource(factory, getChildWebdavPath(binder.getTitle()), f);
		}
		else {
			return null;
		}
	}
	
	protected Resource makeResourceFromBinder(BinderIndexData binder) {
		if(binder == null)
			return null;
		
		EntityType entityType = binder.getEntityType();
		if(EntityType.workspace == entityType) {
			return new WorkspaceResource(factory, getChildWebdavPath(binder.getTitle()), binder);
		}
		else if(EntityType.profiles == entityType) {
			return new WorkspaceResource(factory, getChildWebdavPath(binder.getTitle()), binder);
		}
		else if(EntityType.folder == entityType) {
			return new FolderResource(factory, getChildWebdavPath(binder.getTitle()), binder);
		}
		else {
			return null;
		}
	}
	
	protected Resource makeResourceFromFile(FileAttachment fa) {
		if(fa == null)
			return null;
		else
			return new FileResource(factory, getWebdavPath() + "/" + fa.getFileItem().getName(), fa);
	}
	
	protected Resource makeResourceFromFile(FileIndexData file) {
		if(file == null)
			return null;
		else
			return new FileResource(factory, getWebdavPath() + "/" + file.getName(), file);
	}

	protected CollectionResource createChildFolder(Binder parentBinder, String childFolderName) throws NotAuthorizedException {
		try {
	        org.kablink.teaming.domain.Binder binder = FolderUtils.createLibraryFolder(parentBinder, childFolderName);
	        return new FolderResource(factory,  getWebdavPath() + "/" + childFolderName, (Folder) binder);
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
	
	protected FolderEntry writeFileWithModDate(Folder folder, String newName,
			InputStream inputStream, Date modDate) throws IOException,
			ConflictException, NotAuthorizedException, BadRequestException {
		if (!folder.isLibrary())
			throw new ConflictException(this,
					"This folder is not a library folder");

		if (folder.isDeleted())
			throw new ConflictException(this, "This folder is purged");

		if (folder.isPreDeleted())
			throw new ConflictException(this, "This folder is deleted");

		FolderEntry entry = getFolderModule().getLibraryFolderEntryByFileName(
				folder, newName);

		try {
			if (entry != null) {
				// An entry containing a file with this name exists.
				if (logger.isDebugEnabled())
					logger.debug("createNew: updating existing file '"
							+ newName + "' + owned by "
							+ entry.getEntityIdentifier().toString()
							+ " in folder " + folder.getId());
				FolderUtils.modifyLibraryEntry(entry, newName, inputStream,
						modDate, null, true);
			} else {
				// We need to create a new entry
				if (logger.isDebugEnabled())
					logger.debug("createNew: creating new file '" + newName
							+ "' + in folder " + folder.getId());
				entry = FolderUtils.createLibraryEntry(folder, newName,
						inputStream, modDate, null, true);
			}
		} catch (AccessControlException e) {
			throw new NotAuthorizedException(this);
		} catch (ReservedByAnotherUserException e) {
			throw new ConflictException(this, e.getLocalizedMessage());
		} catch (WriteFilesException e) {
			throw new WebdavException(e.getLocalizedMessage());
		} catch (WriteEntryDataException e) {
			throw new WebdavException(e.getLocalizedMessage());
		}

		return entry;
	}

	private String getChildWebdavPath(String childName) {
		if(webdavPath.endsWith("/"))
			return webdavPath + childName;
		else
			return webdavPath + "/" + childName;
	}
}
