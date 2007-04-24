/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.ssfs.server.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.file.LockIdMismatchException;
import com.sitescape.team.module.file.LockedByAnotherUserException;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.module.shared.FolderUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.ssfs.AlreadyExistsException;
import com.sitescape.team.ssfs.CrossContextConstants;
import com.sitescape.team.ssfs.LockException;
import com.sitescape.team.ssfs.NoAccessException;
import com.sitescape.team.ssfs.NoSuchObjectException;
import com.sitescape.team.ssfs.TypeMismatchException;
import com.sitescape.team.ssfs.server.SiteScapeFileSystem;
import com.sitescape.team.ssfs.server.SiteScapeFileSystemException;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.util.LibraryPathUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;

public class SiteScapeFileSystemLibrary implements SiteScapeFileSystem {

	private static final String LAST_ELEM_NAME 		= "len"; // always set
	private static final String PARENT_BINDER_PATH 	= "pbp"; // always set

	private static final String LEAF_BINDER 		= "lb";
	private static final String LEAF_FOLDER_ENTRY 	= "lfe";
	private static final String PARENT_BINDER 		= "pb";
	private static final String FILE_ATTACHMENT 	= "fa"; 

	private AllBusinessServicesInjected bs;
	private FileTypeMap mimeTypes;
	
	protected final Log logger = LogFactory.getLog(getClass());
	
	private static final String NONLIBRARY_VIRTUAL_HELP_FILE = "ssfs.nonlibrary.virtual.help.file";
	private static final String NONLIBRARY_VIRTUAL_HELP_FILE_CONTENT_CODE = "ssfs.nonlibrary.virtual.help.content"; 
	private static final String NONLIBRARY_VIRTUAL_HELP_FILE_CONTENT_DEFAULT = 
		"This directory does not represent a library folder in Aspen.\nOnly files in library folder are exposed through WebDAV.";
	
	private String helpFileName;
	private byte[] helpFileContentInUTF8;
	private Date currentDate;

	SiteScapeFileSystemLibrary(AllBusinessServicesInjected bs) {
		this.bs = bs;
		
		this.helpFileName = SPropsUtil.getString(NONLIBRARY_VIRTUAL_HELP_FILE, "").trim();
		if(helpFileName.length() == 0) {
			helpFileName = null;
			helpFileContentInUTF8 = null;
		}
		else {
			String helpFileContent = NLT.get(NONLIBRARY_VIRTUAL_HELP_FILE_CONTENT_CODE,
					NONLIBRARY_VIRTUAL_HELP_FILE_CONTENT_DEFAULT);
			try {
				helpFileContentInUTF8 = helpFileContent.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// This never happens
				helpFileContentInUTF8 = new byte[0];
			}
		}
		
		currentDate = new Date();
	}
	protected FileTypeMap getMimeTypes() {
		return this.mimeTypes;
	}
	public void setMimeTypes(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public void createResource(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY))
			throw new TypeMismatchException("A directory with the same name already exists");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FILE))
			throw new AlreadyExistsException("A file with the same name already eixsts");
		
		writeResource(uri, objMap, new ByteArrayInputStream(new byte[0]), true);
	}

	public void setResource(Map uri, InputStream content) 
	throws NoAccessException, NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY))
			throw new TypeMismatchException("The name refers to a directory not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");
		
		writeResource(uri, objMap, content, false);
	}

	public void createAndSetResource(Map uri, InputStream content) 
	throws NoAccessException, AlreadyExistsException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY))
			throw new TypeMismatchException("A directory with the same name already exists");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FILE))
			throw new AlreadyExistsException("A file with the same name already eixsts");
		
		writeResource(uri, objMap, content, true);		
	}

	public void createDirectory(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FILE))
			throw new TypeMismatchException("A file with the same name already exists");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY))
			throw new AlreadyExistsException("A directory with the same name already eixsts");
		
		createLibraryFolder(uri, objMap);
	}

	public InputStream getResource(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY))
			throw new TypeMismatchException("The name refers to a directory not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE))
			return getHelpFile();
		else 
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
		if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) {
			throw new NoSuchObjectException("The object does not exist");
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY)) {
			throw new NoAccessException("Directory can not be deleted");
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE)) {
			throw new NoAccessException("Virtual help file can not be deleted");
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
			throw new NoSuchObjectException("The object does not exist");
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE)) {
			return null;
		}
		else {
			return getChildrenNames(uri, objMap);
		}
	}

	public Map getProperties(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The object does not exist");
		
		Map<String,Object> props = new HashMap<String,Object>();
		
		if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY)) {
			props.put(CrossContextConstants.OBJECT_INFO, info);
			
			Binder binder = getLeafBinder(objMap);
			
			props.put(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE,
					binder.getCreation().getDate());
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED,
					binder.getModification().getDate());
		}
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FILE)) { // file
			props.put(CrossContextConstants.OBJECT_INFO, info);
			
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
		else if(info.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE)) {
			// Do NOT put the info value. Instead, we put OBJECT_INFO_FILE.
			// The client side need not treat the virtual help file any differently
			// from the regular files.
			props.put(CrossContextConstants.OBJECT_INFO, CrossContextConstants.OBJECT_INFO_FILE);
			
			props.put(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE, currentDate);
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED, currentDate);
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_CONTENT_LENGTH, new Long(helpFileContentInUTF8.length));
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_CONTENT_TYPE, "text/plain");
		}

		return props;
	}

	public void lockResource(Map uri, String lockId, String lockSubject, 
			Date lockExpirationDate, String lockOwnerInfo) 
	throws NoAccessException, NoSuchObjectException, LockException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY))
			throw new TypeMismatchException("The name refers to a directory not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE))
			throw new TypeMismatchException("Virtual help file does not support locking");

		FolderEntry entry = getFolderEntry(objMap);
		
		// Check if the user has right to modify the entry
		try {
			AccessUtils.modifyCheck(entry);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		
		try {
			bs.getFileModule().lock(getParentBinder(objMap), entry, 
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
		if(info.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY))
			throw new TypeMismatchException("The name refers to a folder not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE))
			throw new TypeMismatchException("Virtual help file does not support locking");

		FolderEntry entry = getFolderEntry(objMap);
		
		// Check if the user has right to modify the entry
		try {
			AccessUtils.modifyCheck(entry);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		
		bs.getFileModule().unlock(getParentBinder(objMap), entry, 
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
		else if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY)) {
			throw new TypeMismatchException("Directory can not be copied");
		}
		else if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE)) {
			throw new TypeMismatchException("Virtual help file can not be copied");
		}
		
		Map targetMap = new HashMap();
		String targetInfo = objectInfo(targetUri, targetMap);
		if(!targetInfo.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING)) { // Target exists
			if(targetInfo.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY)) {
				throw new TypeMismatchException("The source and target types do not match");
			}
			else if(targetInfo.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE)) {
				// The target is a virtual help file. This means that its parent 
				// directory is not a library folder. We don't allow copying a file
				// into a non-library folder.
				throw new TypeMismatchException("Can not copy a file into a binder that is not a library folder");
			}
			else {
				// The target is also a file. Copy the file.
				copyFile(getFolderEntry(sourceMap), getFolderEntry(targetMap), getLastElemName(targetMap));
			}
		}
		else { // Target doesn't exist
			Binder targetParentBinder = getParentBinder(targetMap);
			
			// Target's parent must exist
			if(targetParentBinder == null)
				throw new NoSuchObjectException("The target's parent binder does not exist");
			
			// The target's parent binder must be a library folder.
			if(!isLibraryFolder(targetParentBinder))
				throw new TypeMismatchException("It is not allowed to copy a file into a binder that is not a library folder");
			
			copyFile(getFolderEntry(sourceMap), 
					(Folder) targetParentBinder, getLastElemName(targetMap));
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
		else if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY)) {
			if(!isLibraryFolder(getLeafBinder(sourceMap))) {
				// Important: It is important not to use NoAccessException or 
				// AlreadyExistsException here. 1) If NoAccessException is thrown
				// while user is trying to rename a non-library folder binder
				// through Windows Explorer, the Explorer will try instead to
				// achieve similar 'rename' effect by creating a new folder
				// with the new name followed by deleting the old folder.
				// If that is allowed to proceed, the existing non-library folder
				// binder is deleted from the system, and a new library folder
				// is created. That kind of indirect deletion is NEVER allowed 
				// in Aspen. 2) If AlreadyExistsException is thrown, Explorer
				// will display an error message like the following - "Can not
				// rename <xyz>. A file with the name you specified already
				// exists. Specify a different filename." This error message
				// is mis-leading, so should be avoided. 
				
				throw new SiteScapeFileSystemException("Can not move or rename binder that is not library folder", true);
				
				// throw new AlreadyExistsException("Can not move or rename binder that is not file folder");
				//throw new NoAccessException("Can not move or rename binder that is not file folder");
			}
		}
		else if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE)) {
			throw new SiteScapeFileSystemException("Can not move or rename virtual help file", true);
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
			
		if(sourceInfo.equals(CrossContextConstants.OBJECT_INFO_DIRECTORY))
			renameFolder(sourceUri, sourceMap, targetUri, targetMap);
		else
			renameResource(sourceUri, sourceMap, targetUri, targetMap);
	}
	
	/**
	 * Copy a library folder file into the library folder creating a new file
	 * (along with the enclosing entry) in it (that is, it is assumed that 
	 * toParentFolder does not contain a child of any type whose name is equal
	 * to the name of the newly created entry). The top version of fromEntry 
	 * is copied to the newly created entry, and its modification time is set 
	 * to the modification time of the top version of fromEntry.
	 * 
	 * @param fromEntry
	 * @param toParentFolder
	 * @param fileName
	 * @throws NoAccessException
	 */
	private void copyFile(FolderEntry fromEntry, Folder toParentFolder,
			String fileName) throws NoAccessException {
		FileAttachment fromFA = getFileAttachment(fromEntry, fileName);
		InputStream fromContent = bs.getFileModule().readFile
		(fromEntry.getParentFolder(), fromEntry, fromFA);
	
		createLibraryFolderEntry(toParentFolder, fileName, fromContent, 
				fromFA.getModification().getDate());
	}
	
	/**
	 * Copy a library folder file from an entry into another. Only the top 
	 * version of fromEntry is copied to toEntry. The modification time 
	 * (but not creation time) of the top version of fromEntry is carried 
	 * over to the new version created for toEntry.
	 *   
	 * @param fromEntry
	 * @param toEntry
	 * @throws NoAccessException
	 */
	private void copyFile(FolderEntry fromEntry, FolderEntry toEntry,
			String fileName) throws NoAccessException {
		FileAttachment fromFA = getFileAttachment(fromEntry, fileName);
		InputStream fromContent = bs.getFileModule().readFile
		(fromEntry.getParentFolder(), fromEntry, fromFA);
	
		modifyLibraryFolderEntry(toEntry, fileName, fromContent, fromFA.getModification().getDate());
	}
	
	private String objectInfo(Map uri, Map objMap) throws NoAccessException {
		try {
			String libpath = getLibpath(uri);
			
			if(libpath == null) { // uri ends with zone name
				objMap.put(LAST_ELEM_NAME, null);
				objMap.put(PARENT_BINDER_PATH, null);
				return CrossContextConstants.OBJECT_INFO_DIRECTORY;
			}
			
			String lastElemName = LibraryPathUtil.getName(libpath);
			String parentBinderPath = LibraryPathUtil.getParentBinderPath(libpath);
			
			objMap.put(LAST_ELEM_NAME, lastElemName); // should be non-null
			objMap.put(PARENT_BINDER_PATH, parentBinderPath); // may be null
			
			// Check if the library path refers to an existing binder.
			// Note: This method performs access check!
			Binder binder = bs.getBinderModule().getBinderByPathName(libpath);

			if(binder != null) { // matching binder exists
				objMap.put(LEAF_BINDER, binder);
				return CrossContextConstants.OBJECT_INFO_DIRECTORY;
			}
			else { // No matching binder
				// One of the four possibilities:
				// 1. an existing file
				// 2. non-existing binder
				// 3. non-existing file
				// 4. a virtual help file
				// In all four cases, it is helpful to locate and cache the binder
				// that corresponds to the immediate parent element in the path.
	
				if(parentBinderPath != null) {
					Binder parentBinder = getParentBinder(objMap);

					if(parentBinder != null) { // matching parent binder exists
						// Check if the parent binder is a library folder
						if(isLibraryFolder(parentBinder)) {
							// Since the parent folder is of library folder type, we can
							// check to see if the path refers to a file.
							// Try locating library folder entry by the file name.
							// Note: This method performs access check!
							FolderEntry entry = 
								bs.getFolderModule().getLibraryFolderEntryByFileName((Folder)parentBinder, lastElemName);
							if(entry != null) {
								// The path refers to an existing file
								objMap.put(LEAF_FOLDER_ENTRY, entry);
								return CrossContextConstants.OBJECT_INFO_FILE;
							}
							else {
								// No library folder entry corresponding to the path. In this case, 
								// the path can subsequently be used for either a new library folder 
								// or a new file entry within the parent folder.
								return CrossContextConstants.OBJECT_INFO_NON_EXISTING;
							}
						}
						else {
							// The parent folder is not a library folder. 
							if(helpFileName != null && helpFileName.equalsIgnoreCase(lastElemName)) {
								// The URI refers to the virtual help file.
								return CrossContextConstants.OBJECT_INFO_VIRTUAL_HELP_FILE;
							}
							else {
								// A file entry can only be located inside a library folder, 
								// and all other types of entries (and the files within them) 
								// are NOT addressable through WebDAV library URIs. Therefore, 
								// as far as WebDAV is concerned, this resource does not exist. 
								return CrossContextConstants.OBJECT_INFO_NON_EXISTING;
							}
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
			fa = getFileAttachment(getFolderEntry(objMap), getLastElemName(objMap));
			
			objMap.put(FILE_ATTACHMENT, fa); // cache it just in case referenced again
		}
		
		return fa;
	}
	
	private FileAttachment getFileAttachment(FolderEntry entry, String fileName) {
		return entry.getFileAttachment(fileName);
	}
	
	private FolderEntry getFolderEntry(Map objMap) {
		return (FolderEntry) objMap.get(LEAF_FOLDER_ENTRY);
	}
	
	private String getLastElemName(Map objMap) {
		return (String) objMap.get(LAST_ELEM_NAME);
	}
	
	private String getParentBinderPath(Map objMap) {
		return (String) objMap.get(PARENT_BINDER_PATH);
	}
	
	private boolean isFolder(Binder binder) {
		return (binder.getEntityType() == EntityType.folder);		
	}
	
	private boolean isLibraryFolder(Binder binder) {
		return (isFolder(binder) && binder.isLibrary());
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
						parentBinder = bs.getBinderModule().getBinderByPathName(parentBinderPath);
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
	
	private void createLibraryFolderEntry(Folder folder, String fileName, 
			InputStream content, Date modDate)
	throws NoAccessException {
		try {
			FolderUtils.createFolderEntry(folder, fileName, content, modDate);
		}
		catch(ConfigurationException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		}
		catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} 
		catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		}
	}
	
	private void modifyLibraryFolderEntry(FolderEntry entry, String fileName, 
			InputStream content, Date modDate) 
	throws NoAccessException {
		try {
			FolderUtils.modifyFolderEntry(entry, fileName, content, modDate);
		}
		catch(ConfigurationException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		}
		catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} 
		catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		}
	}
	
	private void writeResource(Map uri, Map objMap, InputStream content, boolean isNew) 
	throws NoAccessException {
		// We can write a resource (entry/file) only if its parent already exists.
		// In other words, write takes place at one-level at a time.
		
		Binder parentBinder = getParentBinder(objMap);		
		if(parentBinder == null) // No parent binder exists
			throw new NoAccessException("Parent binder does not exist");
		
		// We can create resource only in a library folder, not just any type of binder.
		if(!isLibraryFolder(parentBinder))
			throw new NoAccessException("Parent binder is not a library folder");
				
		String fileName = getLastElemName(objMap);		
		
		if(isNew) {
			createLibraryFolderEntry((Folder) parentBinder, fileName, content, null);
		}
		else {
			modifyLibraryFolderEntry(getFolderEntry(objMap), fileName, content, null);
		}
	}
	
	/**
	 * Create a library folder (which means it should be a folder whose library 
	 * flag is set). It is not possible to create a binder of any other type. 
	 * 
	 * @param uri
	 * @param objMap
	 * @throws NoAccessException
	 */
	private void createLibraryFolder(Map uri, Map objMap) throws NoAccessException {
		// We can create a library folder only if its parent already exists.
		// More specifically:
		// 1) We can not create top-level container (ie, workspace)
		// 2) We can not create any container of type other than folder.
		// 3) Folder creation takes place at one-level at a time. For example,
		// we can not create a/b/c, unless a/b already exists. 
		
		Binder parentBinder = getParentBinder(objMap);
		if(parentBinder == null) // No parent binder exists
			throw new NoAccessException("Parent binder does not exist");
		
		EntityType parentType = parentBinder.getEntityType();
		if(parentType != EntityType.folder && parentType != EntityType.workspace)
			throw new NoAccessException("Parent binder is neither folder nor workspace");
		
		String folderName = getLastElemName(objMap);
		
		createLibraryFolder(parentBinder, folderName);
	}
	
	private Long createLibraryFolder(Binder parentBinder, String folderName)
	throws NoAccessException {
		try {
			return FolderUtils.createLibraryFolder(parentBinder, folderName);
		}
		catch(ConfigurationException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		}
		catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} 
		catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		}
	}
	
	private String[] getChildrenNames(Map uri, Map objMap) {
		Binder binder = getLeafBinder(objMap);
		
		if(binder == null) {
			// This means that the uri ends with zone name.
			// Get the top-level workspace. 
			try {
				Workspace topWorkspace = bs.getWorkspaceModule().getWorkspace();
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
			titles = bs.getWorkspaceModule().getChildrenTitles((Workspace) binder);
			addHelpFile(titles);
		}
		else {
			// The binder is a folder. The children can consist of other
			// folders and files, but no workspaces. However, only a library 
			// folder can expose its contained files through webdav. 
			titles = getChildFolderNames((Folder)binder);
			
			if(isLibraryFolder(binder)) {
				Set<String> titles2 = getLibraryFolderChildrenFileNames((Folder)binder);
				titles.addAll(titles2);
			}
			else {
				addHelpFile(titles);
			}
		}
		
		return titles.toArray(new String[titles.size()]);
	}

	private void addHelpFile(Set<String> titles) {
		if(helpFileName != null) { // help file feature enabled
			titles.add(helpFileName);
		}
	}
	
	private Set<String> getChildFolderNames(Folder folder) {
		return bs.getFolderModule().getSubfoldersTitles(folder);
	}
	
	private Set<String> getLibraryFolderChildrenFileNames(Folder libraryFolder) {
		return bs.getFileModule().getChildrenFileNames(libraryFolder);
	}
		
	private void removeResource(Map uri, Map objMap) throws NoAccessException {
		FileAttachment fa = getFileAttachment(objMap);
		List faId = new ArrayList();
		faId.add(fa.getId());
		
		try {
			bs.getFolderModule().modifyEntry(getParentBinder(objMap).getId(), getFolderEntry(objMap).getId(), new EmptyInputData(), null, faId, null);
		}
		catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} 
		catch (ReservedByAnotherUserException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		} 
		catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		} 
	}
	
	private InputStream getResource(Map uri, Map objMap) {
		FileAttachment fa = getFileAttachment(objMap);
		
		// We assume that "read" access check was performed by the caller. 
		// So we can safely request the file module for the content of
		// the file. 
		return bs.getFileModule().readFile(getParentBinder(objMap), 
				getFolderEntry(objMap), fa);	
	}
	
	private void renameFolder(Map sourceUri, Map sourceMap, Map targetUri, 
			Map targetMap) throws NoAccessException {
		try {
			Map data = new HashMap();
			data.put("title", getLastElemName(targetMap));
			
			bs.getBinderModule().modifyBinder(getLeafBinder(sourceMap).getId(), new MapInputData(data), null, null);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		} 
		catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		}
	}
	
	private void renameResource(Map sourceUri, Map sourceMap, Map targetUri, 
			Map targetMap) throws NoAccessException {
		try {
			Map<FileAttachment,String> renamesTo = new HashMap<FileAttachment,String>();
			renamesTo.put(getFileAttachment(sourceMap), getLastElemName(targetMap));
			
			FolderEntry entry = getFolderEntry(sourceMap);
			
			bs.getFolderModule().modifyEntry(entry.getParentBinder().getId(), 
					entry.getId(), new EmptyInputData(), null, null, renamesTo);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		} 
		catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getLocalizedMessage());
		}
	}
	
	private InputStream getHelpFile() {
		return new ByteArrayInputStream(helpFileContentInUTF8);
	}
}
