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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFileByTheIdException;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.webdav.util.WebdavUtils;
import org.kablink.util.Validator;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.CopyableResource;
import com.bradmcevoy.http.CustomProperty;
import com.bradmcevoy.http.CustomPropertyResource;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.LockInfo;
import com.bradmcevoy.http.LockResult;
import com.bradmcevoy.http.LockTimeout;
import com.bradmcevoy.http.LockToken;
import com.bradmcevoy.http.LockableResource;
import com.bradmcevoy.http.MoveableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Range;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.LockedException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.bradmcevoy.http.exceptions.NotFoundException;
import com.bradmcevoy.http.exceptions.PreConditionFailedException;

/**
 * @author jong
 *
 */
public class FileResource extends WebdavResource implements FileAttachmentResource, PropFindableResource, GetableResource, DeletableResource, LockableResource, CopyableResource, MoveableResource, CustomPropertyResource {

	private static final Log logger = LogFactory.getLog(FileResource.class);

	private static final String WIN_LAST_MODIFIED = "Win32LastModifiedTime";
	
	private static final String MICROSOFT_NAMESPACE_URI = "urn:schemas-microsoft-com:";
	
	private static Set<String> customPropertyNames;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	
	static {
		customPropertyNames = new HashSet<String>();
		customPropertyNames.add(WIN_LAST_MODIFIED);
		customPropertyNames = Collections.unmodifiableSet(customPropertyNames);
	}
	
	// The following properties are required
	private String id; // file database id
	private Long entryId; // owning folder entry id
	private Date createdDate; // creation date
	private Date modifiedDate; // last modification date
	
	// This property may be null. The same information from FileAttachment is preferred.
	private Long size;
	
	// lazy resolved for efficiency, so may be null initially
	private FileAttachment fa; 
	
	private void init(String id, Long entryId, Date createdDate, Date modifiedDate, Long size) {
		this.id = id;
		this.entryId = entryId;
		this.createdDate = getMiltonSafeDate(createdDate);
		this.modifiedDate = getMiltonSafeDate(modifiedDate);
		this.size = size;
	}
	
	private void init(FileAttachment fa) {
		init(fa.getId(), fa.getOwner().getEntity().getId(), fa.getCreation().getDate(), fa.getModification().getDate(), null);		
		this.fa = fa; // already resolved
	}
	
	private void init(FileIndexData fid) {
		init(fid.getId(), fid.getOwningEntityId(), fid.getCreatedDate(), fid.getModifiedDate(), fid.getSize());
	}
	
	public FileResource(WebdavResourceFactory factory, String webdavPath, FileAttachment fa) {
		super(factory, webdavPath, fa.getFileItem().getName());
		init(fa);
	}

	public FileResource(WebdavResourceFactory factory, String webdavPath, FileIndexData fid) {
		super(factory, webdavPath, fid.getName());
		init(fid);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return "fa:" + id;
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
	 * @see com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http.Auth)
	 */
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return factory.getMaxAgeSecondsFile();
	}
	
	public Long getEntryId() {
		return entryId;
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
		
		WebdavUtils.sendFileContent(out, range, fa, logger);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentType(java.lang.String)
	 */
	@Override
	public String getContentType(String accepts) {
		return WebdavUtils.getFileContentType(accepts, getName(), logger);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getContentLength()
	 */
	@Override
	public Long getContentLength() {
		// Although the library can correctly transmit file content to the client without
		// this information, we still need to supply this piece of information. Otherwise,
		// file browsing WebDAV client such as Windows Explorer will display zero for
		// all file sizes. 
		
		// If file attachment object already exists, always get it from the object since
		// the information in the database is most up-to-date.
		if(fa != null)
			return fa.getFileItem().getLength();
		
		if(size != null)
			return size;
		
		// If you're still here, it means that, unfortunately, the Lucene index does not
		// contain the file size information, which means that the index is old and has
		// not been re-indexed after upgrading to Hudson. We have no choice but fetch
		// the information from the database.
		resolveFileAttachment();
		return fa.getFileItem().getLength();
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
				if(logger.isDebugEnabled())
					logger.debug("delete: deleting file " + toString() + " + owned by " + owningEntity.getEntityIdentifier().toString());
				FolderUtils.deleteFileInFolderEntry(this, (FolderEntry) owningEntity, fa);
			}
			else if(owningEntity.getEntityType() == EntityType.folder) {
				List deletes = new ArrayList();
				deletes.add(fa.getId());
				if(logger.isDebugEnabled())
					logger.debug("delete: deleting file " + toString() + " + owned by " + owningEntity.getEntityIdentifier().toString());
				getBinderModule().modifyBinder(owningEntity.getId(), new EmptyInputData(), null, deletes, null);
			}
			else {
				// Our WebDAV service exposes only files stored in a folder, which is
				// attached either to an entry in the folder or to the folder itself.
				// Therefore, this can not and should not occur.
				throw new ConflictException(this, "Can not delete file '" + id + "' because it is owned by an entity of type '" + owningEntity.getEntityType() + "'");
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
		return factory.getLockManager().lock(timeout, lockInfo, this);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#refreshLock(java.lang.String)
	 */
	@Override
	public LockResult refreshLock(String token) throws NotAuthorizedException,
			PreConditionFailedException {
		return factory.getLockManager().refresh(token, this);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#unlock(java.lang.String)
	 */
	@Override
	public void unlock(String tokenId) throws NotAuthorizedException,
			PreConditionFailedException {
		factory.getLockManager().unlock(tokenId, this);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.LockableResource#getCurrentLock()
	 */
	@Override
	public LockToken getCurrentLock() {
		return factory.getLockManager().getCurrentToken(this);
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.webdav.FileAttachmentResource#getFileAttachment()
	 */
	@Override
	public FileAttachment getFileAttachment() {
		return resolveFileAttachment();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CopyableResource#copyTo(com.bradmcevoy.http.CollectionResource, java.lang.String)
	 */
	@Override
	public void copyTo(CollectionResource toCollection, String name)
			throws NotAuthorizedException, BadRequestException,
			ConflictException {
		if(toCollection instanceof FolderResource) {
			FileAttachment fa = getFileAttachment();
			DefinableEntity owningEntity = fa.getOwner().getEntity();
			if(EntityType.folderEntry == owningEntity.getEntityType()) {
				FolderEntry entry = (FolderEntry) owningEntity;
				String sourceFileName = fa.getFileItem().getName();
				Long toFolderId = ((FolderResource)toCollection).getEntityIdentifier().getEntityId();
				if(entry.getParentFolder().getId().equals(toFolderId)) {
					// The file is being copied within the same parent folder. 
					if(sourceFileName.equals(name)) {
						// The target name is the same as the source name. This can not be allowed when copying within the same folder.
						// Note: NEVER throw BadRequestException. It will result in the WebDAV client on Windows repeating the same request a few more times unnecessarily.
						throw new ConflictException(this, "Can not copy file '" + id + "' to the same name within the same folder");  
					}
					else { 
						// Make sure that the current folder doesn't already contain a file with the new name.
						if(getFolderModule().getLibraryFolderEntryByFileName(entry.getParentFolder(), name) == null) {
							// Copy the file in the current folder by creating a new entry with it.
							copyFileToNewEntry(entry, fa, (FolderResource)toCollection);
						}
						else {
							throw new ConflictException(this, "Can not copy file '" + id + "' because there is already a file with the same name in this folder");
						}						
					}
				}
				else { // The file is being copied to another folder.
					Folder destFolder = ((FolderResource)toCollection).getFolder();
					// Make sure that the destination is a library folder.
					if(destFolder.isLibrary()) {
						// Make sure that the destination folder doesn't already contain a file with the new name.
						if(getFolderModule().getLibraryFolderEntryByFileName(destFolder, name) == null) {
							if(entry.getFileAttachmentsCount() > 1) {
								// This entry contains more than one file, therefore, copying the entire entry is no good
								// since it would result in multiple files being copied to the user's surprise.
								// We will create a new entry in the destination and copy the select file.
								copyFileToNewEntry(entry, fa, (FolderResource)toCollection);
							}
							else {
								// This is the only file contained in the entry, therefore, we can safely copy the entire entry.
								HashMap options = new HashMap();
								options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, name);
								try {
									FolderEntry destEntry = getFolderModule().copyEntry(entry.getParentBinder().getId(), entry.getId(), toFolderId, new String[] {name}, options);
								}
								catch (WriteFilesException e) {
									throw new ConflictException(this, "Can not copy file '" + id + "' into the folder '" + toFolderId + "' because of a WriteFilesException");
								}
							}
						}
						else {
							throw new ConflictException(this, "Can not copy file '" + id + "' into the folder '" + toFolderId + "' because there is already a file with the same name in the destination folder");
						}
					}
					else {
						// The destination folder is not a library folder. 
						throw new ConflictException(this, "Can not copy file '" + id + "' because the destination folder '" + destFolder.getId() + "' is not a library folder");						
					}
				}
			}
			else {
				// We allow copy operation only on those files attached to entries.
				// This is because file copy operation indirectly involves entry creation or modification.
				throw new ConflictException(this, "Can not copy file '" + id + "' because it is owned by an entity of type '" + owningEntity.getEntityType() + "'");
			}
		}
		else if(toCollection instanceof WorkspaceResource) {
			throw new ConflictException(this, "It is not allowed to copy a file into a workspace.");
		}
		else {
			throw new ConflictException(this, "Destination is an unknown type '" + toCollection.getClass().getName() + "'. Must be a folder resource.");
		}
	}
	
	private void doMoveTo(String destResourceWebdavPath, Folder destFolder, String name) 
			throws ConflictException, NotAuthorizedException, BadRequestException {
		FileAttachment fa = getFileAttachment();
		DefinableEntity owningEntity = fa.getOwner().getEntity();
		if(EntityType.folderEntry == owningEntity.getEntityType()) {
			FolderEntry entry = (FolderEntry) owningEntity;
			Long destFolderId = destFolder.getId();
			if(entry.getParentFolder().getId().equals(destFolderId)) {
				// The file is being moved within the same parent folder. 
				if(fa.getFileItem().getName().equals(name)) {
					// The target name is the same as the source name. Well, this means renaming to the same name.
					return; // nothing more to do  
				}
				else { // Rename it in the current folder.
					// Make sure that the folder doesn't already contain a file with the new name.
					if(getFolderModule().getLibraryFolderEntryByFileName(entry.getParentFolder(), name) == null) {
						// Rename the file
						renameFile(entry, fa, name);
					}
					else {
						throw new ConflictException(this, "Can not rename file '" + id + "' because there is already a file with the same name in this folder");
					}
				}
			}
			else { // The file is being moved to another folder.
				// Make sure that the destination is a library folder.
				if(destFolder.isLibrary()) {
					// Make sure that the destination folder doesn't already contain a file with the new name.
					if(getFolderModule().getLibraryFolderEntryByFileName(destFolder, name) == null) {
						if(entry.getFileAttachmentsCount() > 1) {
							// This entry contains more than one file, therefore, moving the entire entry is no good
							// since it would result in multiple files being moved to the user's surprise.
							// On top of that, silently moving just one file out of this entry into another doesn't
							// seem as reasonable as the case with copy, because unlike partial copy, partial move alters 
							// the state of the source entry. The fact that multiple files were attached to the same entry
							// (mostly likely through browser interface) implies that there are some logical relationship 
							// between those files, which can not be shown/expressed through WebDAV interface.
							// For that reason, we will not allow partial move through WebDAV interface. If that's 
							// indeed the intention of the user, the user will have to do that through browser interface
							// in the same way that WebDAV interface doesn't allow a way to attach multiple files to
							// the same entry.
							throw new ConflictException(this, "Can not move file '" + id + "' because the enclosing entry represents more than one file");
						}
						else {
							// This is the only file contained in the entry, therefore, we can safely move the entire entry.
							HashMap options = new HashMap();
							options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, name);
							try {
								getFolderModule().moveEntry(entry.getParentBinder().getId(), entry.getId(), destFolderId, new String[] {name}, options);
							}
							catch (WriteFilesException e) {
								throw new ConflictException(this, "Can not move file '" + id + "' into the folder '" + destFolderId + "' because of a WriteFilesException");
							}
							// Finally, we need to adjust the state of this FileResource to properly reflect the post-operation state.
							// Reload file attachment object just in case.
							this.setWebdavPath(destResourceWebdavPath + "/" + name);
							init(getFileModule().getFileAttachmentById(id));
						}
					}
					else {
						throw new ConflictException(this, "Can not move file '" + id + "' into the folder '" + destFolderId + "' because there is already a file with the same name in the destination folder");
					}
				}
				else {
					// The destination folder is not a library folder. 
					throw new ConflictException(this, "Can not move file '" + id + "' because the destination folder '" + destFolder.getId() + "' is not a library folder");
				}
			}
		}
		else {
			// We allow move operation only on those files attached to entries.
			// Moving other files is ill-defined.
			throw new ConflictException(this, "Can not move file '" + id + "' because it is owned by an entity of type '" + owningEntity.getEntityType() + "'");
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.MoveableResource#moveTo(com.bradmcevoy.http.CollectionResource, java.lang.String)
	 */
	@Override
	public void moveTo(CollectionResource rDest, String name)
			throws ConflictException, NotAuthorizedException,
			BadRequestException {
		if(rDest instanceof FolderResource) {
			FolderResource destFolderResource = (FolderResource) rDest;
			doMoveTo(destFolderResource.getWebdavPath(), destFolderResource.getFolder(), name);
		}
		else if(rDest instanceof WorkspaceResource) {
			throw new ConflictException(this, "It is not allowed to move a file into a workspace.");
		}
		else if(rDest instanceof MyFilesResource) {
			MyFilesResource destMyFilesResource = (MyFilesResource) rDest;
			Folder destFolder = destMyFilesResource.getMyFilesFileParent();
			doMoveTo(destMyFilesResource.getWebdavPath(), destFolder, name);
		}
		else {
			throw new ConflictException(this, "Destination is an unknown type '" + rDest.getClass().getName() + "'. Must be a folder resource.");
		}
	}
	
	/*
	//$$$$$
	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		if(Method.MOVE == method) {
			return false;
		}
		else {
			return super.authorise(request, method, auth);
		}
	}
	*/

	
	private void renameFile(FolderEntry entry, FileAttachment fa, String newName) throws NotAuthorizedException, ConflictException {
		Map<FileAttachment,String> renamesTo = new HashMap<FileAttachment,String>();
		renamesTo.put(fa, newName);
		
		InputDataAccessor inputData = null;
		
		if(fa.getFileItem().getName().equals(entry.getTitle())) {
			// This entry's title is identical to the current name of the file.
			// In this case, it's reasonable to change the title to match the new name as well.
			Map data = new HashMap();
			data.put("title", newName);
			inputData = new MapInputData(data);
		}
		else {
			inputData = new EmptyInputData();
		}
		
		try {
			getFolderModule().modifyEntry(entry.getParentBinder().getId(), 
					entry.getId(), inputData, null, null, renamesTo, null);
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
	
	/*
	 * Copy the file by creating a new entry in the folder represented by <code>destFolderResource</code>
	 * and attaching to it.
	 */
	private void copyFileToNewEntry(FolderEntry entry, FileAttachment fa, FolderResource destFolderResource) 
			throws ConflictException, NotAuthorizedException, BadRequestException, WebdavException {
		// Copy it iby creating a new entry using FolderResource.
		InputStream is = getFileModule().readFile(entry.getParentBinder(), entry, fa);
		try {
			((FolderResource)destFolderResource).createNewWithModDate(getName(), is, fa.getModification().getDate());
		} catch (IOException e) {
			throw new WebdavException(e);
		}
		finally {
			try {
				is.close();
			} catch (IOException ignore) {}
		}							
	}
	
	@Override
	public Set<String> getAllPropertyNames() {
		return customPropertyNames;
	}

	@Override
	public CustomProperty getProperty(String name) {
		if(customPropertyNames.contains(name))
			return new FileCustomProperty(name);
		else
			return null;
	}

	@Override
	public String getNameSpaceURI() {
		return MICROSOFT_NAMESPACE_URI;
	}

    public class FileCustomProperty implements CustomProperty {

        private final String key;

        public FileCustomProperty( String key ) {
            this.key = key;
        }

        public Object getTypedValue() {
        	// We use custom property only for write but not for read.
        	// So we simply ignore this request and return an empty string.
            return "";
        }

        public String getFormattedValue() {
        	// We use custom property only for write but not for read.
        	// So we simply ignore this request and return an empty string.
            return "";
        }

        public void setFormattedValue( String s ) {
        	if(WIN_LAST_MODIFIED.equalsIgnoreCase(key)) {
            	if(Validator.isNull(s)) {
            		if(logger.isDebugEnabled())
            			logger.debug("Received null string for last mod time value on file " + FileResource.this.toString());
            		return;
            	}
            	
            	String dateStr = s;
        		int index = s.indexOf(">");
        		if(index >= 0)
        			dateStr = s.substring(index+1);

    			Date lastModDate = parseDateStr(dateStr);
    			if(lastModDate != null) {
        			resolveFileAttachment();
        			// When copying or moving a file from Windows to Vibe through WebDAV, Microsoft performs the following
        			// insane sequence of actions:
        			// 1. Creates an empty file
        			// 2. Update it with file content
        			// 3. Set incorrect mod time on the file
        			// 4. Set correct mod time
        			// This small bit of hack is to mitigate Microsoft inefficiency, specifically the step 3 above.
        			if(lastModDate.before(fa.getModification().getDate())) {
	        			DefinableEntity owningEntity = fa.getOwner().getEntity();
	        			if(EntityType.folderEntry == owningEntity.getEntityType()) {
	            			try {
	            				// Update the file metadata. No changes to the enclosing entry
	            				getFileModule().correctLastModTime(fa, lastModDate);
	            				// Reindex the enclosing entry (hence, a bit of inefficiency).
	            				getFolderModule().indexEntry((FolderEntry)owningEntity, false, false);
							} catch (Exception e) {
								logger.error("Error updating last mod time on file '" + FileResource.this.toString() + "' owned by " + owningEntity.getEntityIdentifier().toString(), e);
							}        				
	        			}
	        			else {
	        				if(logger.isDebugEnabled())
	        					logger.debug("Not correcting last mod time on file '" + FileResource.this.toString() + "' because it is owned by an entity of type '" + owningEntity.getEntityType() + "'");
	        			}
        			}
        			else {
        				if(logger.isDebugEnabled())
        					logger.debug("No need to correct last mod time on file '" + FileResource.this.toString() + "' because the new value seems another garbage");
        			}
        		}
        		else {
        			if(logger.isDebugEnabled())
        				logger.debug("Unrecognized string value '" + s + "' for last mod time on file " + FileResource.this.toString());
        		}
        	}
        	else {
        		if(logger.isDebugEnabled())
        			logger.debug("Received value '" + s + "' for unrecognized key '" + key + " on file " + FileResource.this.toString());
        	}
        }

        public Class getValueClass() {
        	// We currently only use string type custom property
            return String.class;
        }
    }

    Date parseDateStr(String dateStr) {
    	try {
			return dateFormat.parse(dateStr);
		} catch (ParseException e) {
			logger.warn("Error parsing date string '" + dateStr + "': " + e.toString());
			return null;
		}
    }
}
