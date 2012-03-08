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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFileByTheIdException;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.security.AccessControlException;

import com.bradmcevoy.common.ContentTypeUtils;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.FileItem;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.LockInfo;
import com.bradmcevoy.http.LockResult;
import com.bradmcevoy.http.LockTimeout;
import com.bradmcevoy.http.LockToken;
import com.bradmcevoy.http.LockableResource;
import com.bradmcevoy.http.PostableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.LockedException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;
import com.bradmcevoy.http.exceptions.PreConditionFailedException;
import com.bradmcevoy.http.http11.PartialGetHelper;
import com.bradmcevoy.io.ReadingException;
import com.bradmcevoy.io.WritingException;

/**
 * @author jong
 *
 */
public class FileResource extends WebdavResource implements PropFindableResource, GetableResource, DeletableResource, LockableResource {

	private static final Log logger = LogFactory.getLog(FileResource.class);
	
	// The following properties are required
	private String name; // file name
	private String id; // file database id
	private Date createdDate; // creation date
	private Date modifiedDate; // last modification date
	
	private String webdavPath;
	
	// lazy resolved for efficiency, so may be null initially
	private FileAttachment fa; 
	
	private FileResource(WebdavResourceFactory factory, String webdavPath, String name, String id, Date createdDate, Date modifiedDate) {
		super(factory);
		this.webdavPath = webdavPath;
		this.name = name;
		this.id = id;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}
	
	public FileResource(WebdavResourceFactory factory, String webdavPath, FileAttachment fa) {
		this(factory, webdavPath, fa.getFileItem().getName(), fa.getId(), fa.getCreation().getDate(), fa.getModification().getDate());
		this.fa = fa; // already resolved
	}

	public FileResource(WebdavResourceFactory factory, String webdavPath, FileIndexData fid) {
		this(factory, webdavPath, fid.getName(), fid.getId(),  fid.getCreatedDate(), fid.getModifiedDate());
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return "fa:" + id;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	@Override
	public Date getCreateDate() {
		return createdDate;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#sendContent(java.io.OutputStream, com.bradmcevoy.http.Range, java.util.Map, java.lang.String)
	 */
	@Override
	public void sendContent(OutputStream out, Range range,
			Map<String, String> params, String contentType) throws IOException,
			NotAuthorizedException, BadRequestException, NotFoundException {
		try {
			resolveFileAttachment();
		}
		catch(NoFileByTheIdException e) {
			throw new NotFoundException(e.getLocalizedMessage());
		}
		
		DefinableEntity owningEntity = fa.getOwner().getEntity();
		InputStream in = getFileModule().readFile(owningEntity.getParentBinder(), owningEntity, fa);

		try {
			if (range != null) {
				if(logger.isDebugEnabled())
					logger.debug("sendContent: ranged content for file " + toString());
				PartialGetHelper.writeRange(in, range, out);
			} else {
				if(logger.isDebugEnabled())
					logger.debug("sendContent: send whole file " + toString());
				IOUtils.copy(in, out);				
			}
			out.flush();
		} catch(ReadingException e) {
			throw new IOException(e);
		} catch(WritingException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType(String accepts) {
		//return new MimetypesFileTypeMap().getContentType(name);
		
		//$$$$$ TODO TBD
		long begin = System.nanoTime();
		
		String mime = ContentTypeUtils.findContentTypes(name);
		String s = ContentTypeUtils.findAcceptableContentType(mime, accepts);
		
		long end = System.nanoTime();
		
		System.out.println("***** Getting content type took " + (end-begin)/1000000.0 + "milliseconds");
		
		if(logger.isTraceEnabled())
			logger.trace("getContentType: preferred: " + accepts + " mime: " + mime + " selected: " + s);
		return s;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentLength()
	 */
	@Override
	public Long getContentLength() {
		return null;
	}
	
	public String getWebdavPath() {
		return webdavPath;
	}
	
	private FileAttachment resolveFileAttachment() throws NoFileByTheIdException {
		if(fa == null) {
			// Load it directly from DAO without further access check, since access check
			// was already performed at the time this instance was created. Resource object
			// is created only after the system determines by looking up the database or
			// Lucene index that the user making request has read access to the resource.
			//fa = getFileModule().getFileAttachmentById(id);
			fa = (FileAttachment) getCoreDao().load(FileAttachment.class, id);
			if(fa == null)
				throw new NoFileByTheIdException(id);
			else if(fa instanceof VersionAttachment)
				throw new NoFileByTheIdException(id, "The specified file id represents a file version rather than a file");
		}
		return fa;
	}
	
	public String toString() {
    	return new StringBuilder().append("[").append(name).append(":").append(id).append("]").toString(); 
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.DeletableResource#delete()
	 */
	@Override
	public void delete() throws NotAuthorizedException, ConflictException,
			BadRequestException {
		try {
			resolveFileAttachment();
		}
		catch(NoFileByTheIdException e) {
			// The file doesn't exist. Nothing to delete.
			if(logger.isDebugEnabled())
				logger.debug("delete: file " + toString() + " does not exist. nothing to delete.");
			return;
		}
		
		DefinableEntity owningEntity = fa.getOwner().getEntity();
		try {
			if(owningEntity.getEntityType() == EntityType.folderEntry) {
				FolderUtils.deleteFileInFolderEntry((FolderEntry) owningEntity, fa);
			}
			else if(owningEntity.getEntityType() == EntityType.folder) {
				List deletes = new ArrayList();
				deletes.add(fa.getId());
				getBinderModule().modifyBinder(owningEntity.getId(), new EmptyInputData(), null, deletes, null);
			}
			else {
				// Our WebDAV service exposes only files stored in a folder, which is
				// attached either to an entry in the folder or to the folder itself.
				// Therefore, this can not and should not occur.
				throw new BadRequestException(this, "Can not delete file '" + id + "' because it is owned by an entity of type '" + owningEntity.getEntityType() + "'");
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
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#lock(com.bradmcevoy.http.LockTimeout, com.bradmcevoy.http.LockInfo)
	 */
	@Override
	public LockResult lock(LockTimeout timeout, LockInfo lockInfo)
			throws NotAuthorizedException, PreConditionFailedException,
			LockedException {
		return factory.getLockManager().lock(timeout, lockInfo, this); // $$$
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#refreshLock(java.lang.String)
	 */
	@Override
	public LockResult refreshLock(String token) throws NotAuthorizedException,
			PreConditionFailedException {
		return factory.getLockManager().refresh(token, this); // $$$
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#unlock(java.lang.String)
	 */
	@Override
	public void unlock(String tokenId) throws NotAuthorizedException,
			PreConditionFailedException {
		factory.getLockManager().unlock(tokenId, this); // $$$$
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#getCurrentLock()
	 */
	@Override
	public LockToken getCurrentLock() {
		if (factory.getLockManager() != null) {
			return factory.getLockManager().getCurrentToken(this);
		} else {
			logger.warn("getCurrentLock called, but no lock manager: file: " + webdavPath);
			return null;
		}

	}
}
