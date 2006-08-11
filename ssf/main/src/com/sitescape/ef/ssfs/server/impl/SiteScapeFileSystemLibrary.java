package com.sitescape.ef.ssfs.server.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private static final String LEAF_BINDER 	= "b";
	private static final String LEAF_ENTRY 		= "e";
	private static final String PARENT_BINDER 	= "p";
	private static final String LAST_ELEM_NAME 	= "l";
	private static final String FILE_ATTACHMENT = "f";

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
		
		createFolder(uri, objMap);
	}

	public InputStream getResource(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		String info = objectInfo(uri, objMap);
		if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			throw new TypeMismatchException("The name refers to a folder not a file");
		else if(info.equals(CrossContextConstants.OBJECT_INFO_NON_EXISTING))
			throw new NoSuchObjectException("The resource does not exist");
		
		FileAttachment fa = getFileAttachment(objMap);
		
		// Because objectInfo always performs "read" access check for the
		// user, we can safely request the file module for the content of
		// the file. 
		return getFileModule().readFile(getParentBinder(objMap), getFileFolderEntry(objMap), fa);
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
		else if(info.equals(CrossContextConstants.OBJECT_INFO_FOLDER))
			removeFolder(uri, objMap);
		else
			removeResource(uri, objMap);
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
			}			
		}

		return props;
	}

	public void lockResource(Map uri, String lockId, String lockSubject, Date lockExpirationDate) 
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
				lockExpirationDate);
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

	private String objectInfo(Map uri, Map objMap) throws NoAccessException {
		try {
			String libpath = getLibpath(uri);
			
			if(libpath == null) // uri ends with zone name
				return CrossContextConstants.OBJECT_INFO_FOLDER;
			
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
				
				int index = libpath.lastIndexOf("/");
				if(index > 0) {
					String folderPath = libpath.substring(0, index);
					String lastElemName = libpath.substring(index + 1);
					
					Binder parentBinder = getBinderModule().getBinderByPathName(folderPath);

					if(parentBinder != null) { // matching parent binder exists
						objMap.put(PARENT_BINDER, parentBinder);
						objMap.put(LAST_ELEM_NAME, lastElemName); 
						
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
			FolderEntry entry = getFileFolderEntry(objMap);
			
			Definition definition = entry.getEntryDef();
			
			String repositoryName = getLibraryRepositoryName(definition);
			
			fa = entry.getFileAttachment(repositoryName, getLastElemName(objMap));
			
			objMap.put(FILE_ATTACHMENT, fa); // cache it just in case referenced again
		}
		
		return fa;
	}
	
	private FolderEntry getFileFolderEntry(Map objMap) {
		return (FolderEntry) objMap.get(LEAF_ENTRY);
	}
	
	private String getLastElemName(Map objMap) {
		return (String) objMap.get(LAST_ELEM_NAME);
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
	
	private Binder getParentBinder(Map objMap) {
		return (Binder) objMap.get(PARENT_BINDER);
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
				
		Definition def = getFileEntryDefinition((Folder) parentBinder);
		if(def == null)
			throw new SiteScapeFileSystemException("There is no file entry definition to use");
		
		String elementName = getLibraryElementName(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		String fileName = getLastElemName(objMap);
		SsfsMultipartFile mf = new SsfsMultipartFile(fileName, content);
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementName, mf); // single file item
		
		InputDataAccessor inputData = new EmptyInputData(); // No non-file input data
		
		if(isNew) {
			try {
				getFolderModule().addEntry(parentBinder.getId(), def.getId(), inputData, fileItems);
			} catch (AccessControlException e) {
				throw new NoAccessException(e.getLocalizedMessage());			
			} catch (WriteFilesException e) {
				throw new SiteScapeFileSystemException(e.getMessage());
			}
		}
		else {
			try {
				getFolderModule().modifyEntry(parentBinder.getId(),
						getFileFolderEntry(objMap).getId(),
						inputData, fileItems, null);
			} catch (AccessControlException e) {
				throw new NoAccessException(e.getLocalizedMessage());			
			} catch (WriteFilesException e) {
				throw new SiteScapeFileSystemException(e.getMessage());
			}
		}
	}
	
	private void createFolder(Map uri, Map objMap) throws NoAccessException {
		// We can create a file folder only if its parent already exists.
		// More specifically:
		// 1) We can not create top-level container (ie, workspace)
		// 2) We can not create any container of type other than file folder.
		// 3) Folder creation takes place at one-level at a time. For example,
		// we can not create a/b/c, unless a/b already exists. 
		
		Binder parentBinder = getParentBinder(objMap);
		if(parentBinder == null) // No parent binder exists
			throw new NoAccessException("Parent binder does not exist");
		
		Definition def = getFileFolderDefinition(parentBinder);
		if(def == null)
			throw new SiteScapeFileSystemException("There is no file folder definition to use");
		
		Map data = new HashMap(); // Input data
		// Title field, not name, is used as the name of the folder. Weird...
		data.put("title", getLastElemName(objMap)); 
		//data.put("description", "This folder was created through WebDAV");
		
		try {
			if(parentBinder instanceof Workspace)
				getWorkspaceModule().addFolder(parentBinder.getId(), def.getId(), 
						new MapInputData(data), new HashMap());
			else
				getFolderModule().addFolder(parentBinder.getId(), def.getId(), 
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
			titles = getFolderModule().getSubfoldersTitles((Folder)binder);
			
			if(isFileFolder(binder)) {
				// Get the list of file folder entries in the file folder and
				// add their titles to the set. This is done by using search index.
				Map options = new HashMap();
				options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE);
				Map folderEntries = getFolderModule().getEntries(
						binder.getId(), options);
				List entries = (ArrayList) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
				for (int i = 0; i < entries.size(); i++) {
					Map ent = (Map) entries.get(i);
					String titleString = (String) ent
							.get(EntityIndexUtils.TITLE_FIELD);
					if (titleString != null)
						titles.add(titleString);
				}
			}
		}
		
		return titles.toArray(new String[titles.size()]);
	}
	
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
}
