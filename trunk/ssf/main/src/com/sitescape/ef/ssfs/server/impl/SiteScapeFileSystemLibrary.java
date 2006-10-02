package com.sitescape.ef.ssfs.server.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.ReservedByAnotherUserException;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.binder.AccessUtils;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.LockIdMismatchException;
import com.sitescape.ef.module.file.LockedByAnotherUserException;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EmptyInputData;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.LockException;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.TypeMismatchException;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystem;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystemException;

public class SiteScapeFileSystemLibrary implements SiteScapeFileSystem {

	private static final String LAST_ELEM_NAME 		= "len"; // always set
	private static final String PARENT_BINDER_PATH 	= "pbp"; // always set

	private static final String LEAF_BINDER 		= "lb";
	private static final String LEAF_ENTRY 			= "le";
	private static final String PARENT_BINDER 		= "pb";
	private static final String FILE_ATTACHMENT 	= "fa"; 

	private static final String ITEM_NAME = "fileEntryTitle";
	
	protected final Log logger = LogFactory.getLog(getClass());

	private FolderModule folderModule;
	private DefinitionModule definitionModule;
	private BinderModule binderModule;
	private ProfileModule profileModule;
	private FileModule fileModule;
	private WorkspaceModule wsModule;
	
	private FileTypeMap mimeTypes;

	//private CoreDao coreDao;
	
	protected FolderModule getFolderModule() {
		return folderModule;
	}
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	protected BinderModule getBinderModule() {
		return binderModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	protected FileModule getFileModule() {
		return fileModule;
	}
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}
	protected WorkspaceModule getWorkspaceModule() {
		return wsModule;
	}
	public void setWorkspaceModule(WorkspaceModule wsModule) {
		this.wsModule = wsModule;
	}
	protected FileTypeMap getMimeTypes() {
		return this.mimeTypes;
	}
	public void setMimeTypes(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}
	/*
	protected CoreDao getCoreDao() {
		return this.coreDao;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}*/

	public void createResource(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new TypeMismatchException("A folder with the same name already exists");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FILE))
			throw new AlreadyExistsException("A file with the same name already eixsts");
		
		writeResource(uri, objMap, new ByteArrayInputStream(new byte[0]), true);
	}

	public void setResource(Map uri, InputStream content) 
	throws NoAccessException, NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new TypeMismatchException("The name refers to a folder not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");
		
		writeResource(uri, objMap, content, false);
	}

	public void createAndSetResource(Map uri, InputStream content) 
	throws NoAccessException, AlreadyExistsException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new TypeMismatchException("A folder with the same name already exists");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FILE))
			throw new AlreadyExistsException("A file with the same name already eixsts");
		
		writeResource(uri, objMap, content, true);		
	}

	public void createFolder(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FILE))
			throw new TypeMismatchException("A file with the same name already exists");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new AlreadyExistsException("A folder with the same name already eixsts");
		
		createFileFolder(uri, objMap);
	}

	public InputStream getResource(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new TypeMismatchException("The name refers to a folder not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");
		
		return getResource(uri, objMap);
	}
	
	/*
	public long getResourceLength(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new TypeMismatchException("The name refers to a folder not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");
		
		FileAttachment fa = getFileAttachment(objMap);
		
		return fa.getFileItem().getLength();
	}*/

	public void removeObject(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The object does not exist");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER)) {
			Binder binder = getLeafBinder(objMap);
			if(!isFileFolder(binder))
				throw new NoAccessException("Can not remove binder that is not file folder");
			removeFolder(uri, objMap);
		}
		else {
			removeResource(uri, objMap);
		}
	}

	/*
	public Date getLastModified(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		Date result = null;
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER)) {
			Binder binder = getBinder(objMap);
			result = binder.getModification().getDate();
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FILE)) {
			// Shall we use the modification time associated with the file entry
			// or the file itself? They could be different if the file entry
			// has additional fields.
			// Since the only element of the entry that is visible and accessible
			// through WebDAV is the library file itself, it appears to be better
			// to use the time associated with the file itself (we shall see).
			FileAttachment fa = getFileAttachment(objMap);
			result = fa.getModification().getDate();
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) {
			throw new NoSuchObjectException("The object does not exist");
		}
		return result;
	}

	public Date getCreationDate(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		Date result = null;
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER)) {
			Binder binder = getBinder(objMap);
			result = binder.getCreation().getDate();
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FILE)) {
			// Since we can not create a file folder entry without specifying
			// an actual library file, the creation dates for the entry and the
			// file must be equal. So don't bother retrieving file attachment.
			FolderEntry entry = getFolderEntry(objMap);
			result = entry.getCreation().getDate();
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) {
			throw new NoSuchObjectException("The object does not exist");
		}
		return result;
	}*/

	public String[] getChildrenNames(Map uri) throws NoAccessException, 
	NoSuchObjectException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FILE)) {
			// The uri refers to a leaf file which doesn't have children
			// because it's not a folder. In this case we must return
			// null instead of an empty string array to signal the
			// situation (This is because, if we threw an exception to
			// convey the situation, WCK wouldn't behave the way we 
			// expect it to. So we must return null instead, although it
			// is not consistent with the way the rest of the operations
			// work). 
			return null;
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) {
			throw new NoSuchObjectException("The folder does not exist");
		}

		return getChildrenNames(uri, objMap);
	}

	public Map getProperties(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The object does not exist");
		
		Map<String,Object> props = new HashMap<String,Object>();
		
		props.put(CrossContextConstants.OBJECT_INFO, info);
				
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER)) {
			Binder binder = getLeafBinder(objMap);
			
			props.put(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE,
					binder.getCreation().getDate());
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED,
					binder.getModification().getDate());
		}
		else { // file
			FileAttachment fa = getFileAttachment(objMap);
			
			// Get DAV properties
			props.put(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE,
					fa.getCreation().getDate());
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED,
					fa.getModification().getDate());
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_CONTENT_LENGTH,
					fa.getFileItem().getLength());
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_CONTENT_TYPE,
					getMimeTypes().getContentType(fa.getFileItem().getName()));
			
			FileAttachment.FileLock lock = fa.getFileLock();
			if(lock != null) {
				// Get lock properties
				props.put(CrossContextConstants.LOCK_PROPERTIES_ID, lock.getId());
				props.put(CrossContextConstants.LOCK_PROPERTIES_SUBJECT, lock.getSubject());
				props.put(CrossContextConstants.LOCK_PROPERTIES_EXPIRATION_DATE, lock.getExpirationDate());
				props.put(CrossContextConstants.LOCK_PROPERTIES_OWNER_INFO, lock.getOwnerInfo());
			}			
		}

		return props;
	}

	public void lockResource(Map uri, String lockId, String lockSubject, 
			Date lockExpirationDate, String lockOwnerInfo) 
	throws NoAccessException, NoSuchObjectException, LockException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new TypeMismatchException("The name refers to a folder not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");

		FolderEntry entry = getFileFolderEntry(objMap);
		
		// Check if the user has right to modify the entry
		try {
			AccessUtils.modifyCheck(entry);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		
		try {
			getFileModule().lock(getParentBinder(objMap), entry, 
				getFileAttachment(objMap), lockId, lockSubject, 
				lockExpirationDate, lockOwnerInfo);
		}
		catch(ReservedByAnotherUserException e) {
			throw new LockException(e.getLocalizedMessage());
		}
		catch(LockedByAnotherUserException e) {
			throw new LockException(e.getLocalizedMessage());
		}
		catch(LockIdMismatchException e) {
			throw new LockException(e.getLocalizedMessage());			
		}
		
	}

	public void unlockResource(Map uri, String lockId) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new TypeMismatchException("The name refers to a folder not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");

		FolderEntry entry = getFileFolderEntry(objMap);
		
		// Check if the user has right to modify the entry
		try {
			AccessUtils.modifyCheck(entry);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		
		getFileModule().unlock(getParentBinder(objMap), entry, 
				getFileAttachment(objMap), lockId);
	}

	public void copyObject(Map sourceUri, Map targetUri, boolean overwrite, 
			boolean recursive) throws  
			NoAccessException, NoSuchObjectException, AlreadyExistsException, 
			TypeMismatchException {
		Map sourceMap = new HashMap();
		String sourceInfo = objectInfo(sourceUri, sourceMap);
		if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) {
			throw new NoSuchObjectException("The source object does not exist");
		}
		else if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_FOLDER)) {
			if(!isFileFolder(getLeafBinder(sourceMap)))
				throw new TypeMismatchException("Can not copy binder that is not file folder");
		}
		
		Map targetMap = new HashMap();
		String targetInfo = objectInfo(targetUri, targetMap);
		if(!targetInfo.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) { // Target exists
			// Make sure that both the source and the target are same type. 
			if(!sourceInfo.equals(targetInfo))
				throw new TypeMismatchException("The source and target types do not match");
			
			if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_FILE)) {
				copyFileFolderEntry(getFileFolderEntry(sourceMap), getFileFolderEntry(targetMap));
			}
			else {
				if(!isFileFolder(getLeafBinder(targetMap)))
					throw new TypeMismatchException("Can not copy into a binder that is not a file folder");
				
				copyFileFolder((Folder) getLeafBinder(sourceMap), 
						(Folder) getLeafBinder(targetMap), recursive);
			}
		}
		else { // Target doesn't exist
			Binder targetParentBinder = getParentBinder(targetMap);
			
			// Target's parent must exist
			if(targetParentBinder == null)
				throw new NoSuchObjectException("The target's parent binder does not exist");
			
			if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_FILE)) {
				// If the source is a file folder entry, the target's parent binder
				// must be a file folder.
				if(!isFileFolder(targetParentBinder))
					throw new TypeMismatchException("Can not copy file entry into binder that is not file folder");
				
				copyFileFolderEntry(getFileFolderEntry(sourceMap), 
						(Folder) targetParentBinder, getLastElemName(targetMap));
			}
			else {
				copyFileFolder((Folder) getLeafBinder(sourceMap), targetParentBinder, 
						getLastElemName(targetMap), recursive);
			}
		}
	}
	
	public void moveObject(Map sourceUri, Map targetUri, boolean overwrite) 
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException {
		Map sourceMap = new HashMap();
		String sourceInfo = objectInfo(sourceUri, sourceMap);
		
		if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) {
			throw new NoSuchObjectException("The source object does not exist");
		}
		else if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_FOLDER)) {
			if(!isFileFolder(getLeafBinder(sourceMap))) {
				// Important: It is important not to use NoAccessException or 
				// AlreadyExistsException here. 1) If NoAccessException is thrown
				// while user is trying to rename a non-file folder binder
				// through Windows Explorer, the Explorer will try instead to
				// achieve similar 'rename' effect by creating a new folder
				// with the new name followed by deleting the old folder.
				// It that is allowed to proceed, the existing non-file folder
				// binder is deleted from the system, and a new file folder
				// is created. That kind of indirect deletion is NEVER allowed 
				// in Aspen. 2) If AlreadyExistsException is thrown, Explorer
				// will display an error message like the following - "Can not
				// rename <xyz>. A file with the name you specified already
				// exists. Specify a different filename." This error message
				// is mis-leading, so should be avoided. 
				
				throw new SiteScapeFileSystemException("Can not move or rename binder that is not file folder", true);
				
				// throw new AlreadyExistsException("Can not move or rename binder that is not file folder");
				//throw new NoAccessException("Can not move or rename binder that is not file folder");
			}
		}
		
		Map targetMap = new HashMap();
		String targetInfo = objectInfo(targetUri, targetMap);
		
		if(!getParentBinderPath(sourceMap).equals(getParentBinderPath(targetMap))) {
			// Because we only allow "rename" (no change to parent) but not "move"
			// (parent change), we do not allow this operation unless both the 
			// source and the target share the same parent. 
			throw new SiteScapeFileSystemException("Cannot move: It is not allowed", true);
		}
		
		if(!targetInfo.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) {
			// Because we only allow "rename", we can not perform the operation
			// if the target object already exists, regardless of the value of
			// overwrite flag. We don't want to delete the existing target.
			// If that's what the user wants, he will have to explicitly delete
			// the target, and try renaming again. This is consistent with the 
			// behavior of Windows Explorer.
			throw new AlreadyExistsException("Cannot rename: An object with the name you specified already exists");
		}
			
		if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			renameFolder(sourceUri, sourceMap, targetUri, targetMap);
		else
			renameResource(sourceUri, sourceMap, targetUri, targetMap);
	}

	/**
	 * Copy a file folder into the binder creating a new file folder in it. 
	 * (that is, it is assumed that toParentBinder does not contain a child 
	 * of any type whose name is equal to the name of the newly created folder).
	 * 
	 * @param fromFileFolder source file folder
	 * @param toParentBinder target binder in which to create a new file folder.
	 * This binder can be of any type.
	 * @param toFolderTitle the name of the newly created file folder
	 * @throws NoAccessException
	 */
	private void copyFileFolder(Folder fromFileFolder, Binder toParentBinder, 
			String toFolderTitle, boolean recursive) throws NoAccessException {
		Long toFolderId = createFileFolder(toParentBinder, toFolderTitle);
		
		Folder toFolder = getFolderModule().getFolder(toFolderId);
		
		// Copy sub-entries
		Set<String> subEntryNames = getChildFileFolderEntryNames(fromFileFolder);
		FolderEntry subEntry;
		for(String subEntryName : subEntryNames) {
			subEntry = getFolderModule().getFileFolderEntryByTitle(fromFileFolder, subEntryName);
			if(subEntry != null) {
				try {
					copyFileFolderEntry(subEntry, toFolder, subEntryName);
				}
				catch(Exception e) {
					// For whatever reason, we failed to copy the particular
					// sub-entry. Log the error and continue processing with
					// other sub-entries. 
					logger.warn("Error while copying file folder entry [" + fromFileFolder.getPathName() + "/" + subEntryName + "]", e);
					continue;
				}
			}
			else {
				// Normally this shouldn't happen. But in rare cases, it IS possible
				// to occur if the particular sub-entry has been deleted by another
				// user between the time the names of the child entries were requested
				// and the time the particular child entry object itself was requested.
				// Another possibility is that the search index and the database are
				// out of synch of each other.
				logger.warn("Can not find file folder entry [" + fromFileFolder.getPathName() + "/" + subEntryName + "]");
				continue;
			}
		}
		
		if(!recursive)
			return;
		
		// Copy sub-folders
		Set<Folder> subFolders = getChildFileFolders(fromFileFolder);
		for(Folder subFolder : subFolders) {
			try {
				copyFileFolder(subFolder, toFolder, subFolder.getTitle(), recursive);
			}
			catch(Exception e) {
				// For whatever reason, we failed to copy the particular 
				// sub-folder (which works recursively). Log the error and
				// continue processing with other branches.
				logger.warn("Error while copying file folder [" + subFolder.getPathName() + "]", e);
				continue;
			}
		}
	}
	
	/**
	 * Copy a file folder to another existing file folder. It means that the 
	 * children of fromFileFolder are copied into toFileFolder. 
	 * 
	 * @param fromFileFolder
	 * @param toFileFolder
	 * @param recursive
	 * @throws NoAccessException
	 */
	private void copyFileFolder(Folder fromFileFolder, Folder toFileFolder, 
			boolean recursive) throws NoAccessException {
		Set<String> toSubFolderNames = getChildFolderNames(toFileFolder);
		
		Set<String> toSubEntryNames = getChildFileFolderEntryNames(toFileFolder);
		
		// Copy sub-entries
		Set<String> fromSubEntryNames = getChildFileFolderEntryNames(fromFileFolder);
		FolderEntry fromSubEntry;
		for(String fromSubEntryName : fromSubEntryNames) {
			fromSubEntry = getFolderModule().getFileFolderEntryByTitle(fromFileFolder, fromSubEntryName);
			if(fromSubEntry != null) {
				try {
					if(toSubFolderNames.contains(fromSubEntryName)) {
						// The toFolder contains a sub-folder whose name is equal
						// to the name of this sub-entry of fromFolder. When both
						// source and target exist, they must be of the same type.
						logger.warn("Can not copy file folder entry [" + fromFileFolder.getPathName() + "/" + 
								fromSubEntryName + "] because the target exists and it is file folder type");
						continue;
					}
					else if(toSubEntryNames.contains(fromSubEntryName)) {
						// The toFolder contains a sub-entry whose name is equal
						// to the name of this sub-entry of fromFolder.
						FolderEntry toSubEntry = getFolderModule().getFileFolderEntryByTitle(toFileFolder, fromSubEntryName);
						copyFileFolderEntry(fromSubEntry, toSubEntry);
					}
					else {
						// The toFolder contains neither a sub-folder nor a sub-entry that
						// matches the name of this sub-entry of fromFolder. 
						copyFileFolderEntry(fromSubEntry, toFileFolder, fromSubEntryName);
					}
				}
				catch(Exception e) {
					// For whatever reason, we failed to copy the particular
					// sub-entry. Log the error and continue processing with
					// other sub-entries. 
					logger.warn("Error while copying file folder entry [" + fromFileFolder.getPathName() + "/" + fromSubEntryName + "]", e);
					continue;
				}
			}
			else {
				// Normally this shouldn't happen. But in rare cases, it IS possible
				// to occur if the particular sub-entry has been deleted by another
				// user between the time the names of the child entries were requested
				// and the time the particular child entry object itself was requested.
				// Another possibility is that the search index and the database are
				// out of synch of each other.
				logger.warn("Can not find file folder entry [" + fromFileFolder.getPathName() + "/" + fromSubEntryName + "]");
				continue;
			}
		}
	
		if(!recursive)
			return;
		
		// Copy sub-folders
		
		Set<Folder> fromSubFolders = getChildFileFolders(fromFileFolder);
		for(Folder fromSubFolder : fromSubFolders) {
			try {
				if(toSubFolderNames.contains(fromSubFolder.getTitle())) {
					// The toFolder contains a sub-folder whose name is equal
					// to the name of this sub-folder of fromFolder.
					Folder toSubFolder = (Folder) getBinderModule().getBinderByPathName
					(toFileFolder.getPathName() + "/" + fromSubFolder.getTitle());
					copyFileFolder(fromSubFolder, toSubFolder, recursive);
				}
				else if(toSubEntryNames.contains(fromSubFolder.getTitle())) {
					// The toFolder contains a sub-entry whose name is equal
					// to the name of this sub-folder of fromFolder. When both
					// source and target exist, they must be of the same type.
					logger.warn("Can not copy file folder [" + fromSubFolder.getPathName() + 
							"] because the target exists and it is file folder entry type");
					continue;				
				}
				else {
					// The toFolder contains neither a sub-folder nor a sub-entry
					// that matches the name of this sub-folder of fromFolder. 
					copyFileFolder(fromSubFolder, toFileFolder, fromSubFolder.getTitle(), recursive);
				}
			}
			catch(Exception e) {
				// For whatever reason, we failed to copy the particular 
				// sub-folder (which works recursively). Log the error and
				// continue processing with other branches.
				logger.warn("Error while copying file folder [" + fromSubFolder.getPathName() + "]", e);
			}
		}
	
	}
	
	/**
	 * Copy a file folder entry into the file folder creating a new file folder
	 * entry in it (that is, it is assumed that toParentFolder does not 
	 * contain a child of any type whose name is equal to the name of the newly
	 * created entry). The top version of fromEntry is copied to the newly created
	 * entry, and its modification time is set to the modification time of the
	 * top version of fromEntry.
	 * 
	 * @param fromEntry
	 * @param toParentFolder
	 * @param toEntryTitle the title of the newly created entry. Also used for
	 * the file name.
	 * @throws NoAccessException
	 */
	private void copyFileFolderEntry(FolderEntry fromEntry, Folder toParentFolder,
			String toEntryTitle) throws NoAccessException {
		FileAttachment fromFA = getFileAttachment(fromEntry);
		InputStream fromContent = getFileModule().readFile
		(fromEntry.getParentFolder(), fromEntry, fromFA);
	
		createFileFolderEntry(toParentFolder, toEntryTitle, fromContent, 
				fromFA.getModification().getDate());
	}
	
	/**
	 * Copy a file folder entry to antoher existing entry. Only the top 
	 * version of fromEntry is copied to toEntry. The modification time 
	 * (but not creation time) of the top version of fromEntry is carried 
	 * over to the new version created for toEntry.
	 *   
	 * @param fromEntry
	 * @param toEntry
	 * @throws NoAccessException
	 */
	private void copyFileFolderEntry(FolderEntry fromEntry, FolderEntry toEntry)
		throws NoAccessException {
		FileAttachment fromFA = getFileAttachment(fromEntry);
		InputStream fromContent = getFileModule().readFile
		(fromEntry.getParentFolder(), fromEntry, fromFA);
	
		modifyFileFolderEntry(toEntry, fromContent, fromFA.getModification().getDate());
	}
	
	private String objectInfo(Map uri, Map objMap) throws NoAccessException {
		try {
			String libpath = getLibpath(uri);
			
			if(libpath == null) { // uri ends with zone name
				objMap.put(LAST_ELEM_NAME, null);
				objMap.put(PARENT_BINDER_PATH, null);
				return CrossContextConstants.OBJECT_INFO_FOLDER;
			}
			
			int index = libpath.lastIndexOf("/");
			
			String lastElemName = libpath.substring(index + 1);
			String parentBinderPath = null;
			if(index > 0)
				parentBinderPath = libpath.substring(0, index);
			
			objMap.put(LAST_ELEM_NAME, lastElemName); // should be non-null
			objMap.put(PARENT_BINDER_PATH, parentBinderPath); // may be null
			
			// Check if the library path refers to an existing binder.
			// Note: This method performs access check.
			Binder binder = getBinderModule().getBinderByPathName(libpath);

			if(binder != null) { // matching binder exists
				objMap.put(LEAF_BINDER, binder);
				return CrossContextConstants.OBJECT_INFO_FOLDER;
			}
			else { // No matching binder
				// One of the three possibilities:
				// 1. an existing file folder entry (ie file)
				// 2. non-existing binder
				// 3. non-existing file
				// In all three cases, it is helpful to locate and cache the binder
				// that corresponds to the immediate parent element in the path.
	
				if(parentBinderPath != null) {
					Binder parentBinder = getParentBinder(objMap);

					if(parentBinder != null) { // matching parent binder exists
						// Check if the parent binder is a file folder
						if(isFileFolder(parentBinder)) {
							// Since the parent folder is of file folder type, we can
							// check to see if the path refers to a file folder entry.
							// Try locating file folder entry
							// Note: This method performs access check.
							FolderEntry fileFolderEntry = 
								getFolderModule().getFileFolderEntryByTitle((Folder)parentBinder, lastElemName);
							if(fileFolderEntry != null) {
								// The path refers to an existing file
								objMap.put(LEAF_ENTRY, fileFolderEntry);
								return CrossContextConstants.OBJECT_INFO_FILE;
							}
							else {
								// No file folder entry corresponding to the path. In this case, 
								// the path can subsequently be used for either a new file folder 
								// or a new file entry within the parent folder.
								return CrossContextConstants.OBJECT_INFO_NON_EXISTING;
							}
						}
						else {
							// The parent folder is not a file folder. A file entry can only
							// be located inside a file folder, and all other types of entries
							// (and the files within them) are NOT addressable through WebDAV
							// library URIs. Therefore, as far as WebDAV is concerned, this 
							// resource does not exist. 
							return CrossContextConstants.OBJECT_INFO_NON_EXISTING;
							 
						}
					}
					else {
						// No parent binder exists. 
						return CrossContextConstants.OBJECT_INFO_NON_EXISTING;
					}
				}
				else {
					// There is no parent folder element in the library path. Since 
					// file can not be the top-most element in the path, this uri
					// does not refer to an existing object.
					return CrossContextConstants.OBJECT_INFO_NON_EXISTING;
				}
			}
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}		
	}
	
	private String getLibpath(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_LIBPATH);
	}
	
	private FileAttachment getFileAttachment(Map objMap) {
		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		
		if(fa == null) {
			fa = getFileAttachment(getFileFolderEntry(objMap));
			
			objMap.put(FILE_ATTACHMENT, fa); // cache it just in case referenced again
		}
		
		return fa;
	}
	
	private FileAttachment getFileAttachment(FolderEntry entry) {
		Definition definition = entry.getEntryDef();
		
		String repositoryName = getLibraryRepositoryName(definition);
		
		return entry.getFileAttachment(repositoryName, entry.getTitle());		
	}
	
	private FolderEntry getFileFolderEntry(Map objMap) {
		return (FolderEntry) objMap.get(LEAF_ENTRY);
	}
	
	private String getLastElemName(Map objMap) {
		return (String) objMap.get(LAST_ELEM_NAME);
	}
	
	private String getParentBinderPath(Map objMap) {
		return (String) objMap.get(PARENT_BINDER_PATH);
	}
	
	private String getLibraryRepositoryName(Definition definition) {
		Document defDoc = definition.getDefinition();
		Element root = defDoc.getRootElement();
		Element item = (Element) root.selectSingleNode("//item[@name='" + ITEM_NAME
				+ "' and @type='data']");
		Element storageElem = (Element) item.selectSingleNode("./properties/property[@name='storage']");
		String value = storageElem.attributeValue("value");
		if(value == null)
			value = storageElem.attributeValue("default");
		return value;
	}
	
	private boolean isFileFolder(Binder binder) {
		Integer defType = binder.getDefinitionType();
		
		if(defType != null && defType == Definition.FILE_FOLDER_VIEW)
			return true;
		else
			return false;
	}
	
	private Binder getLeafBinder(Map objMap) {
		return (Binder) objMap.get(LEAF_BINDER);
	}
	
	private Binder getParentBinder(Map objMap) throws NoAccessException {
		try {
			if(!objMap.containsKey(PARENT_BINDER)) {
				// Parent binder was never cached
				Binder parentBinder = null;
				Binder leafBinder = getLeafBinder(objMap);
				if(leafBinder != null) {
					parentBinder = leafBinder.getParentBinder();
				}
				else {
					String parentBinderPath = (String) objMap.get(PARENT_BINDER_PATH);
					if(parentBinderPath != null) {
						parentBinder = getBinderModule().getBinderByPathName(parentBinderPath);
					}
				}
				objMap.put(PARENT_BINDER, parentBinder); // parentBinder may be null
			}
				
			return (Binder) objMap.get(PARENT_BINDER);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
	}
	
	private void createFileFolderEntry(Folder folder, String fileName, 
			InputStream content, Date modDate)
	throws NoAccessException {
		Definition def = getFileEntryDefinition(folder);
		if(def == null)
			throw new SiteScapeFileSystemException("There is no file entry definition to use");
		
		String elementName = getLibraryElementName(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		SsfsMultipartFile mf = new SsfsMultipartFile(fileName, content, modDate);
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementName, mf); // single file item
		
		InputDataAccessor inputData;
		
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			Map data = new HashMap();
			data.put("_lastModifiedDate", modDate);
			inputData = new MapInputData(data);
		}
		else {
			inputData = new EmptyInputData(); // No non-file input data
		}
		
		try {
			getFolderModule().addEntry(folder.getId(), def.getId(), inputData, fileItems);
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getMessage());
		}
	}
	
	private void modifyFileFolderEntry(FolderEntry entry, InputStream content, Date modDate) 
	throws NoAccessException {
		Folder folder = entry.getParentFolder();
		Definition def = getFileEntryDefinition(folder);
		if(def == null)
			throw new SiteScapeFileSystemException("There is no file entry definition to use");
		
		String elementName = getLibraryElementName(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		SsfsMultipartFile mf = new SsfsMultipartFile(entry.getTitle(), content, modDate);
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementName, mf); // single file item
		
		InputDataAccessor inputData;
		
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			Map data = new HashMap();
			data.put("_lastModifiedDate", modDate);
			inputData = new MapInputData(data);
		}
		else {
			inputData = new EmptyInputData(); // No non-file input data
		}

		try {
			getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
					inputData, fileItems, null);
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getMessage());
		}	
	}
	
	private void writeResource(Map uri, Map objMap, InputStream content, boolean isNew) 
	throws NoAccessException {
		// We can write a resource (entry/file) only if its parent already exists.
		// In other words, write takes place at one-level at a time.
		
		Binder parentBinder = getParentBinder(objMap);		
		if(parentBinder == null) // No parent binder exists
			throw new NoAccessException("Parent binder does not exist");
		
		// We can create resource only in a file folder, not just any type of binder.
		if(!isFileFolder(parentBinder))
			throw new NoAccessException("Parent binder is not a file folder");
				
		String fileName = getLastElemName(objMap);		
		
		if(isNew) {
			createFileFolderEntry((Folder) parentBinder, fileName, content, null);
		}
		else {
			modifyFileFolderEntry(getFileFolderEntry(objMap), content, null);
		}
	}
	
	/**
	 * Create a file folder. It is not possible to create a folder of any other type. 
	 * 
	 * @param uri
	 * @param objMap
	 * @throws NoAccessException
	 */
	private void createFileFolder(Map uri, Map objMap) throws NoAccessException {
		// We can create a file folder only if its parent already exists.
		// More specifically:
		// 1) We can not create top-level container (ie, workspace)
		// 2) We can not create any container of type other than file folder.
		// 3) Folder creation takes place at one-level at a time. For example,
		// we can not create a/b/c, unless a/b already exists. 
		
		Binder parentBinder = getParentBinder(objMap);
		if(parentBinder == null) // No parent binder exists
			throw new NoAccessException("Parent binder does not exist");
		
		String folderName = getLastElemName(objMap);
		
		createFileFolder(parentBinder, folderName);
	}
	
	private Long createFileFolder(Binder parentBinder, String folderName)
	throws NoAccessException {
		Definition def = getFileFolderDefinition(parentBinder);
		if(def == null)
			throw new SiteScapeFileSystemException("There is no file folder definition to use");
		
		Map data = new HashMap(); // Input data
		// Title field, not name, is used as the name of the folder. Weird...
		data.put("title", folderName); 
		//data.put("description", "This folder was created through WebDAV");
		
		try {
			if(parentBinder instanceof Workspace)
				return getWorkspaceModule().addFolder(parentBinder.getId(), def.getId(), 
						new MapInputData(data), new HashMap());
			else
				return getFolderModule().addFolder(parentBinder.getId(), def.getId(), 
						new MapInputData(data), new HashMap());
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getMessage());
		}
	}
	
	private String[] getChildrenNames(Map uri, Map objMap) {
		Binder binder = getLeafBinder(objMap);
		
		if(binder == null) {
			// This means that the uri ends with zone name.
			// Get the top-level workspace. 
			try {
				Workspace topWorkspace = getWorkspaceModule().getWorkspace();
				return new String[] {topWorkspace.getTitle()};
			}
			catch(AccessControlException e) {
				// The user has no access to the top-level workspace, which is
				// weird...
				return new String[0];
			}
		}
		
		Set<String> titles = null;
		if(binder instanceof Workspace) {
			// The binder is workspace. The children can consist only of other
			// workspaces and/or folders, but no entries. 
			titles = getWorkspaceModule().getChildrenTitles((Workspace) binder);
		}
		else {
			// The binder is a folder. The children can consist of other
			// folders and entries, but no workspaces. However, we do not
			// include entries unless the folder is a file folder. A file
			// folder can only contain file folder entries. 
			titles = getChildFolderNames((Folder)binder);
			
			if(isFileFolder(binder)) {
				Set<String> titles2 = getChildFileFolderEntryNames((Folder)binder);
				titles.addAll(titles2);
			}
		}
		
		return titles.toArray(new String[titles.size()]);
	}

	private Set<String> getChildFolderNames(Folder folder) {
		return getFolderModule().getSubfoldersTitles(folder);
	}
	
	private Set<Folder> getChildFileFolders(Folder fileFolder) {
		return getFolderModule().getSubfolders(fileFolder);
	}
	
	private Set<String> getChildFileFolderEntryNames(Folder fileFolder) {
		// Get the list of file folder entries in the file folder and
		// add their titles to the set. This is done by using search index.
		Set<String> titles = new HashSet<String>();
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE);
		Map folderEntries = getFolderModule().getEntries(fileFolder.getId(), options);
		List entries = (ArrayList) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		for (int i = 0; i < entries.size(); i++) {
			Map ent = (Map) entries.get(i);
			String titleString = (String) ent
					.get(EntityIndexUtils.TITLE_FIELD);
			if (titleString != null)
				titles.add(titleString);
		}
		return titles;
	}
	
	TreeSet<String> titles = new TreeSet<String>();

	
	/**
	 * Remove a folder. The folder could be any type.
	 * 
	 * @param uri
	 * @param objMap
	 * @throws NoAccessException
	 */
	private void removeFolder(Map uri, Map objMap) throws NoAccessException {
		try {
			getBinderModule().deleteBinder(getLeafBinder(objMap).getId());
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());						
		}
	}
	
	private void removeResource(Map uri, Map objMap) throws NoAccessException {
		try {
			getFolderModule().deleteEntry(getParentBinder(objMap).getId(), 
					getFileFolderEntry(objMap).getId());
		}
		catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		}
	}
	
	private Definition getFileEntryDefinition(Folder folder) {
		Definition def = folder.getDefaultEntryDef();
		if(def == null)
			def = getZoneWideDefaultFileEntryDefinition();
		return def;
	}
	
	private Definition getZoneWideDefaultFileEntryDefinition() {
		List defs = getDefinitionModule().getDefinitions(Definition.FILE_ENTRY_VIEW);
		if(defs != null)
			return (Definition) defs.get(0);
		else
			return null;
	}
	
	private Definition getFileFolderDefinition(Binder binder) {
		if(isFileFolder(binder)) {
			// If the parent binder in which to create a new file folder happens
			// to be a file folder itself, simply re-use the file folder definition
			// of the parent. That is, make the sub-directory the same type as
			// its parent. 
			return binder.getEntryDef();
		}
		else {
			return getZoneWideDefaultFileFolderDefinition();
		}
	}
	
	private Definition getZoneWideDefaultFileFolderDefinition() {
		List defs = getDefinitionModule().getDefinitions(Definition.FILE_FOLDER_VIEW);
		if(defs != null)
			return (Definition) defs.get(0);
		else
			return null;
	}
		
	private String getLibraryElementName(Definition definition) {
		Document defDoc = definition.getDefinition();
		Element root = defDoc.getRootElement();
		Element item = (Element) root.selectSingleNode("//item[@name='" + ITEM_NAME
				+ "' and @type='data']");
		Element nameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
		String elementName = nameProperty.attributeValue("value");
		return elementName;
	}

	private InputStream getResource(Map uri, Map objMap) {
		FileAttachment fa = getFileAttachment(objMap);
		
		// We assume that "read" access check was performed by the caller. 
		// So we can safely request the file module for the content of
		// the file. 
		return getFileModule().readFile(getParentBinder(objMap), 
				getFileFolderEntry(objMap), fa);	
	}
	
	private InputStream getResource(FolderEntry entry) {
		return getFileModule().readFile(entry.getParentFolder(), entry, getFileAttachment(entry));
	}
	
	private void renameFolder(Map sourceUri, Map sourceMap, Map targetUri, 
			Map targetMap) throws NoAccessException {
		try {
			Map data = new HashMap();
			data.put("title", getLastElemName(targetMap));
			
			getBinderModule().modifyBinder(getLeafBinder(sourceMap).getId(), new MapInputData(data), null, null);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		} 
		catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getMessage());
		}
	}
	
	private void renameResource(Map sourceUri, Map sourceMap, Map targetUri, 
			Map targetMap) throws NoAccessException {
		try {
			Map data = new HashMap();
			// To request file name change for file folder entry, we use a
			// special key/value pair. Sort of a hack. 
			data.put("_renameFileTo", getLastElemName(targetMap));
			data.put("_renameFileTo_fa", getFileAttachment(sourceMap));
			
			FolderEntry entry = getFileFolderEntry(sourceMap);
			
			getFolderModule().modifyEntry(entry.getParentBinder().getId(), 
					entry.getId(), new MapInputData(data), null, null);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		} 
		catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getMessage());
		}
	}
}
