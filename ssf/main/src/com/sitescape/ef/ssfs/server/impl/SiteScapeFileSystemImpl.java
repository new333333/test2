package com.sitescape.ef.ssfs.server.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.ReservedByAnotherUserException;
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
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.LockException;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystem;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystemException;

public class SiteScapeFileSystemImpl implements SiteScapeFileSystem {

	private static final String BINDER = "b";
	private static final String ENTRY = "e";
	private static final String DEFINITION = "d";
	private static final String FILE_ATTACHMENT = "fa";
	private static final String ELEMENT_NAME = "en";

	protected final Log logger = LogFactory.getLog(getClass());

	private FolderModule folderModule;
	private DefinitionModule definitionModule;
	private BinderModule binderModule;
	private ProfileModule profileModule;
	private FileModule fileModule;
	
	private FileTypeMap mimeTypes;

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
	protected FileTypeMap getMimeTypes() {
		return this.mimeTypes;
	}
	public void setMimeTypes(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	public boolean objectExists(Map uri) throws NoAccessException {
		Map objMap = new HashMap();
		return objectExists(uri, objMap);
	}

	public void createResource(Map uri) throws NoAccessException, AlreadyExistsException {
		Map objMap = new HashMap();
		if(objectExists(uri, objMap))
			throw new AlreadyExistsException("The resource already exists");

		// Write the file with empty content.
		if(isInternal(uri))
			writeResourceInternal(uri, objMap, new ByteArrayInputStream(new byte[0]));
		else
			writeResourceLibrary(uri, objMap, new ByteArrayInputStream(new byte[0]), true);
	}

	public void setResource(Map uri, InputStream content) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");
		
		// Write the file with the specified content. 
		if(isInternal(uri))
			writeResourceInternal(uri, objMap, content);
		else
			writeResourceLibrary(uri, objMap, content, false);
	}

	public void createAndSetResource(Map uri, InputStream content) 
	throws NoAccessException, AlreadyExistsException {
		Map objMap = new HashMap();
		if(objectExists(uri, objMap))
			throw new AlreadyExistsException("The resource already exists");

		// Write the file with the specified content.
		if(isInternal(uri))
			writeResourceInternal(uri, objMap, content);
		else
			writeResourceLibrary(uri, objMap, content, true);
	}
	
	public InputStream getResource(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);

		// Because objectExists always performs "read" access check for the
		// user, we can safely request the file module for the content of
		// the file. 
		return getFileModule().readFile((Binder) objMap.get(BINDER), 
				(Entry) objMap.get(ENTRY), fa);
	}

	public long getResourceLength(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		
		return fa.getFileItem().getLength();
	}

	public void removeResource(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		if(isInternal(uri)) {
			removeResourceInternal(uri, objMap);
		}
		else {
			removeResourceLibrary(uri, objMap);
		}
	}

	private void removeResourceInternal(Map uri, Map objMap) throws NoAccessException {
		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		
		List faId = new ArrayList();
		faId.add(fa.getId());
		
		try {
			getFolderModule().modifyEntry(getBinderId(uri), getEntryId(uri), new EmptyInputData(), null, faId);
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());						
		} catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getMessage());
		}
	}
	
	private void removeResourceLibrary(Map uri, Map objMap) throws NoAccessException {
		try {
			getFolderModule().deleteEntry(getBinderId(uri), ((Entry) objMap.get(ENTRY)).getId());
		}
		catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		}
	}
	
	public Date getLastModified(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");
		
		// This algorithm is not accurate, but I don't think it really matters.
		
		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		if(fa != null)
			return fa.getModification().getDate();
		
		Entry entry = (Entry) objMap.get(ENTRY);
		if(entry != null)
			return entry.getModification().getDate();
		
		Binder binder = (Binder) objMap.get(BINDER);
		if(binder != null)
			return binder.getModification().getDate();
		
		return new Date(0); // ?
	}

	public Date getCreationDate(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		// This algorithm is not accurate, but I don't think it really matters.
		
		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		if(fa != null)
			return fa.getCreation().getDate();
		
		Entry entry = (Entry) objMap.get(ENTRY);
		if(entry != null)
			return entry.getCreation().getDate();
		
		Binder binder = (Binder) objMap.get(BINDER);
		if(binder != null)
			return binder.getCreation().getDate();
		
		return new Date(0); // ?
	}

	public String[] getChildrenNames(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");
		
		if(isInternal(uri))
			return getChildrenNamesInternal(uri, objMap);
		else
			return getChildrenNamesLibrary(uri, objMap);
	}

	public Map getProperties(Map uri) throws NoAccessException,
	NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");
		
		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		
		// The algorithm for computing creation and modification times may
		// not be correct or accurate for folders. Instead we simply use
		// some approximation. But I don't think it will really matter. 
		// We shall see. 
		
		Map<String,Object> props = new HashMap<String,Object>();
		
		if(fa != null) { // This is a file. 
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
			
			return props;
		}

		Entry entry = (Entry) objMap.get(ENTRY);
		if(entry != null) {
			props.put(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE,
					entry.getCreation().getDate());
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED,
					entry.getModification().getDate());
			return props;
		}
		
		Binder binder = (Binder) objMap.get(BINDER);
		if(binder != null) {
			props.put(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE,
					binder.getCreation().getDate());
			props.put(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED,
					binder.getModification().getDate());
			return props;
		}
		
		// Can we reach here?
		
		props.put(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE,
				new Date(0));
		props.put(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED,
				new Date());		
		return props;
	}
	
	public void lockResource(Map uri, String lockId, String lockSubject, 
			Date lockExpirationDate) 
	throws NoAccessException, NoSuchObjectException, LockException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		Entry entry = (Entry) objMap.get(ENTRY);

		// Check if the user has right to modify the entry
		try {
			AccessUtils.modifyCheck(entry);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		
		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		
		try {
			getFileModule().lock(((Binder) objMap.get(BINDER)), entry, 
				((FileAttachment) objMap.get(FILE_ATTACHMENT)), 
				lockId, lockSubject, lockExpirationDate);
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
	NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		Entry entry = (Entry) objMap.get(ENTRY);

		// Check if the user has right to modify the entry
		try {
			AccessUtils.modifyCheck(entry);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		
		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		
		getFileModule().unlock(((Binder) objMap.get(BINDER)), entry, 
				((FileAttachment) objMap.get(FILE_ATTACHMENT)), 
				lockId);
	}

	private String[] getChildrenNamesInternal(Map uri, Map objMap)
			throws NoAccessException, NoSuchObjectException {
		Binder binder = (Binder) objMap.get(BINDER);
		if (binder == null) {
			// Get a list binders (all folders)
			List<String> folderIds = getFolderModule().getFolderIds(null);
			return folderIds.toArray(new String[folderIds.size()]);
		}

		List<String> children = new ArrayList<String>();

		Entry entry = (Entry) objMap.get(ENTRY);
		if (entry == null) {
			// Get a list of entries
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE);
			Map folderEntries = getFolderModule().getEntries(
					binder.getId(), options);
			List entries = (ArrayList) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
			for (int i = 0; i < entries.size(); i++) {
				Map ent = (Map) entries.get(i);
				String entryIdString = (String) ent
						.get(EntityIndexUtils.DOCID_FIELD);
				if (entryIdString != null && !entryIdString.equals(""))
					children.add(entryIdString);
			}
			return children.toArray(new String[children.size()]);
		}

		String itemType = getItemType(uri);
		if (itemType == null) {
			// Get a list of relevent item types from the definition
			Document def = entry.getEntryDef().getDefinition();
			if (def != null) {
				Element root = def.getRootElement();
				if (root.selectNodes(
						"//item[@name='fileEntryTitle' and @type='data']").size() > 0)
					children.add(CrossContextConstants.URI_ITEM_TYPE_LIBRARY);
				if (root.selectNodes("//item[@name='file' and @type='data']")
						.size() > 0)
					children.add(CrossContextConstants.URI_ITEM_TYPE_FILE);
				if (root
						.selectNodes("//item[@name='graphic' and @type='data']")
						.size() > 0)
					children.add(CrossContextConstants.URI_ITEM_TYPE_GRAPHIC);
				if (root.selectNodes(
						"//item[@name='attachFiles' and @type='data']").size() > 0)
					children.add(CrossContextConstants.URI_ITEM_TYPE_ATTACH);
			}
			return children.toArray(new String[children.size()]);
		}

		if (itemType.equals(CrossContextConstants.URI_ITEM_TYPE_LIBRARY)) {
			if (getFilePath(uri) == null) {
				// For library type element, the name of the file is the same
				// as the title of the owning entry. More efficient than 
				// fetching CustomAttribute and/or FileAttachment.
				return new String[] { entry.getTitle() };
			} 
			else {
				// The uri refers to a leaf file which doesn't have children
				// because it's not a folder. In this case we must return
				// null instead of an empty string array to signal the
				// situation.
				return null;
			}
		} else if (itemType.equals(CrossContextConstants.URI_ITEM_TYPE_FILE)
				|| itemType.equals(CrossContextConstants.URI_ITEM_TYPE_GRAPHIC)) {
			if (getElemName(uri) == null) {
				Document def = (Document) objMap.get(DEFINITION);
				Element root = def.getRootElement();
				List items = root.selectNodes("//item[@name='"
						+ toDefItemType(itemType) + "' and @type='data']");
				for (int i = 0; i < items.size(); i++) {
					Element item = (Element) items.get(i);
					Element nameProperty = (Element) item
							.selectSingleNode("./properties/property[@name='name']");
					if (nameProperty != null) {
						String nameValue = nameProperty.attributeValue("value");
						if (nameValue != null && !nameValue.equals("")) {
							children.add(nameValue);
						}
					}

				}
				return children.toArray(new String[children.size()]);
			}

			if (getFilePath(uri) == null) {
				CustomAttribute ca = entry.getCustomAttribute((String) objMap
						.get(ELEMENT_NAME));
				if (ca != null) {
					Iterator it = ((Set) ca.getValueSet()).iterator();
					while (it.hasNext()) {
						FileAttachment fa = (FileAttachment) it.next();
						children.add(fa.getFileItem().getName());
					}
					return children.toArray(new String[children.size()]);
				} else {
					// File was never uploaded through this element yet.
					return new String[0];
				}
			} else {
				return null; // Non-folder resource!
			}

		} else if (itemType.equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
			if (getReposName(uri) == null) {
				Document def = (Document) objMap.get(DEFINITION);
				Element root = def.getRootElement();
				Element attachFilesItem = (Element) root
						.selectSingleNode("//item[@name='attachFiles' and @type='data']");
				Iterator it = attachFilesItem.selectNodes(
						"./properties/property[@name='storage']/option")
						.iterator();
				while (it.hasNext()) {
					Element optionElem = (Element) it.next();
					String optionName = optionElem.attributeValue("name");
					if (optionName != null && !optionName.equals(""))
						children.add(optionName);
				}
				return children.toArray(new String[children.size()]);
			}

			if (getFilePath(uri) == null) {
				Iterator it = entry.getFileAttachments(getReposName(uri))
						.iterator();
				while (it.hasNext()) {
					FileAttachment fa = (FileAttachment) it.next();
					children.add(fa.getFileItem().getName());
				}
				return children.toArray(new String[children.size()]);
			} else {
				return null; // Non-folder resource!
			}

		} else {
			// This can not happen
			throw new InternalException();
		}
	}

	private String[] getChildrenNamesLibrary(Map uri, Map objMap)
	throws NoAccessException, NoSuchObjectException {
		Binder binder = (Binder) objMap.get(BINDER);
		if (binder == null) {
			// Get a list binders (only file folders)
			List<String> folderIds = getFolderModule().getFolderIds(Definition.FILE_FOLDER_VIEW);
			return folderIds.toArray(new String[folderIds.size()]);
		}

		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);

		if(fa == null) {
			List<String> children = new ArrayList<String>();
			
			// Get a list of entries and build a list of titles of those
			// entries (each title represents a file path).
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE);
			Map folderEntries = getFolderModule().getEntries(
					binder.getId(), options);
			List entries = (ArrayList) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
			for (int i = 0; i < entries.size(); i++) {
				Map ent = (Map) entries.get(i);
				String titleString = (String) ent
						.get(EntityIndexUtils.TITLE_FIELD);
				if (titleString != null && !titleString.equals(""))
					children.add(titleString);
			}
			return children.toArray(new String[children.size()]);
		}
		else {
			// The uri represents a non-folder resource. 
			return null;
		}
	}
	
	private String getOriginal(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_ORIGINAL);		
	}
	
	private boolean isInternal(Map uri) {
		if(((String) uri.get(CrossContextConstants.URI_TYPE)).equals(CrossContextConstants.URI_TYPE_INTERNAL))
			return true;
		else
			return false;
	}
	
	private String getZoneName(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_ZONENAME);
	}
	
	private Long getBinderId(Map uri) {
		return (Long) uri.get(CrossContextConstants.URI_BINDER_ID);
	}
	
	private String getFilePath(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_FILEPATH);
	}
	
	private Long getEntryId(Map uri) {
		return (Long) uri.get(CrossContextConstants.URI_ENTRY_ID);
	}
	
	private String getItemType(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_ITEM_TYPE);
	}
	
	private String getElemName(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_ELEMNAME);
	}
	
	private String getReposName(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_REPOS_NAME);		
	}
	
	private String toDefItemType(String itemType) {
		if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_LIBRARY)) {
			return "fileEntryTitle";
		}
		else if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_FILE)) {
			return "file";
		}
		else if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_GRAPHIC)) {
			return "graphic";
		}
		else if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
			return "attachFiles";
		}
		else {
			return null; 
		}
	}
	
	/**
	 * Write the file. If the file already exists, this will create a new 
	 * version of the file. Otherwise it will create a new file with initial
	 * version.  
	 * 
	 * @param uri
	 * @param objMap
	 * @param in
	 */
	private void writeResourceInternal(Map uri, Map objMap, InputStream in) 
		throws NoAccessException {
		// Wrap the input stream in a datastructure suitable for our business module. 
		SsfsMultipartFile mf = new SsfsMultipartFile(getFilePath(uri), in);
		
		Map fileItems = new HashMap(); // Map of names to file items
		InputDataAccessor inputData;   // Input data other than file
		if(getItemType(uri).equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
			// Since attachment element allows uploading multiple files at the 
			// same time, each file is identified uniquely by appending numeric
			// number (1-based) to the element name. 
			fileItems.put((String) objMap.get(ELEMENT_NAME) + "1", mf);
			// Prepare non-file input data map containing repository name. This
			// mechanism allows for dynamic selection of repository name for
			// each file uploaded through attachment element. If repository name
			// is not specified, the statically selected default value (which
			// is stored in the definition) is used as repository name. Since
			// SSFS requires all file manipulation through attachment element
			// to use explicit repository name, this piece of data is always
			// passed. 
			Map source = new HashMap();
			source.put((String) objMap.get(ELEMENT_NAME) + "_repos1", getReposName(uri));
			inputData = new MapInputData(source);
		}
		else {
			fileItems.put(objMap.get(ELEMENT_NAME), mf); // single file item
			inputData = new EmptyInputData(); // no non-file input data
		}

		try {
			getFolderModule().modifyEntry(getBinderId(uri), getEntryId(uri), inputData, fileItems, null);
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} catch (WriteFilesException e) {
			throw new SiteScapeFileSystemException(e.getMessage());
		}		
	}
	
	private void writeResourceLibrary(Map uri, Map objMap, InputStream in, boolean isNew) 
		throws NoAccessException {
		// Before attempting to write the resource, make sure that we have a 
		// metadata needed to do so. Specifically, the file folder must
		// have at least one file entry definition created for it. 
		// Otherwise we wouldn't know how to create an entry.
		
		// All binders referenced by library urls are file folders.
		Folder folder = (Folder) objMap.get(BINDER);
		Definition def = folder.getDefaultEntryDef();
		if(def == null)
			throw new SiteScapeFileSystemException("The file folder does not have a file entry definition associated with it");
		
		String elementName = getLibraryElementName(def);
		
		// Wrap the input stream in a datastructure suitable for our business module. 
		SsfsMultipartFile mf = new SsfsMultipartFile(getFilePath(uri), in);
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementName, mf); // single file item
		
		InputDataAccessor inputData = new EmptyInputData(); // No non-file input data
		
		if(isNew) {
			try {
				getFolderModule().addEntry(getBinderId(uri), def.getId(), inputData, fileItems);
			} catch (AccessControlException e) {
				throw new NoAccessException(e.getLocalizedMessage());			
			} catch (WriteFilesException e) {
				throw new SiteScapeFileSystemException(e.getMessage());
			}
		}
		else {
			try {
				getFolderModule().modifyEntry(getBinderId(uri), 
						((Entry) objMap.get(ENTRY)).getId(),
						inputData, fileItems, null);
			} catch (AccessControlException e) {
				throw new NoAccessException(e.getLocalizedMessage());			
			} catch (WriteFilesException e) {
				throw new SiteScapeFileSystemException(e.getMessage());
			}
		}
	}
	
	private boolean objectExists(Map uri, Map objMap) throws NoAccessException {
		if(isInternal(uri))
			return objectExistsInternal(uri, objMap);
		else
			return objectExistsLibrary(uri, objMap);
	}

	private boolean objectExistsInternal(Map uri, Map objMap)
			throws NoAccessException {
		try {
			// Check folder representing binder id
			Long binderId = getBinderId(uri);
			if (binderId == null)
				return true; // no more checking to do

			Binder binder = getBinderModule().getBinder(binderId);
			objMap.put(BINDER, binder);

			// Check folder representing entry id
			Long entryId = getEntryId(uri);
			if (entryId == null)
				return true; // no more checking to do

			Entry entry = null;

			if (binder instanceof Folder)
				entry = getFolderModule().getEntry(binderId, entryId);
			else
				entry = getProfileModule().getEntry(binderId, entryId);
			objMap.put(ENTRY, entry);

			// Check folder(s) representing definition item. 
			String itemType = getItemType(uri);
			if (itemType == null)
				return true; // no more checking to do

			Document def = entry.getEntryDef().getDefinition();
			if (def == null) // No definition - Is this actually possible?
				return false; // No item type can be recognized
			objMap.put(DEFINITION, def);

			// The following call validates the item type (as a side effect).
			String defItemType = toDefItemType(itemType);
			if (defItemType == null)
				return false; // Unrecognized item type

			Element root = def.getRootElement();
			List items = root.selectNodes("//item[@name='" + defItemType
					+ "' and @type='data']");
			if (items.size() == 0)
				return false; // The item does not exist in the definition.

			String elementName = null;
			String reposName = null;

			// Check definition.
			if (itemType.equals(CrossContextConstants.URI_ITEM_TYPE_FILE)
					|| itemType
							.equals(CrossContextConstants.URI_ITEM_TYPE_GRAPHIC)) {
				// Check folder representing definition element - File or 
				// graphic type items allows multiples.

				elementName = getElemName(uri);
				if (elementName == null)
					return true; // no more checking to do

				boolean matchFound = false;
				Iterator itItems = items.listIterator();
				while (itItems.hasNext()) {
					Element item = (Element) itItems.next();
					Element nameProperty = (Element) item
							.selectSingleNode("./properties/property[@name='name']");
					if (nameProperty != null) {
						String nameValue = nameProperty.attributeValue("value");
						if (nameValue != null && nameValue.equals(elementName)) {
							// Match found
							matchFound = true;
							objMap.put(ELEMENT_NAME, elementName);
							break;
						}
					}
				}
				if (!matchFound)
					return false;
			} else if (itemType
					.equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
				// Check repository name.

				reposName = getReposName(uri);
				if (reposName == null)
					return true; // no more checking to do

				Element attachFilesItem = (Element) items.get(0); // only one item in there
				Element nameProperty = (Element) attachFilesItem
						.selectSingleNode("./properties/property[@name='name']");
				elementName = nameProperty.attributeValue("value");
				objMap.put(ELEMENT_NAME, elementName);
				Element optionElem = (Element) attachFilesItem
						.selectSingleNode("./properties/property[@name='storage']/option[@name='"
								+ reposName + "']");
				if (optionElem == null)
					return false; // The repository name does not appear in the definition.
			} else { // primary
				Element primaryItem = (Element) items.get(0); // only one item in there
				Element nameProperty = (Element) primaryItem
						.selectSingleNode("./properties/property[@name='name']");
				elementName = nameProperty.attributeValue("value");
				objMap.put(ELEMENT_NAME, elementName);
			}

			// Finally check file itself. 
			String filePath = getFilePath(uri);
			if (filePath == null)
				return true; // no more checking to do

			if (itemType.equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
				// Use FileAttachment directly
				FileAttachment fa = entry
						.getFileAttachment(reposName, filePath);
				if (fa == null)
					return false; // No matching file
				else {
					objMap.put(FILE_ATTACHMENT, fa);
					return true; // Match found
				}
			} else {
				// Use CustomAttribute
				CustomAttribute ca = entry.getCustomAttribute(elementName);
				if (ca == null) {
					// This means that no file has ever been uploaded through
					// this element yet (that is, definition exists, but 
					// data/file doesn't yet). 
					return false; // No file exists for the element. 
				} else {
					// Since all file attachments in this custom attribute
					// have the same value for repository name, we only
					// need to use file name for comparison. 
					Iterator it = ((Set) ca.getValueSet()).iterator();
					while (it.hasNext()) {
						FileAttachment fa = (FileAttachment) it.next();
						if (fa.getFileItem().getName().equals(filePath)) {
							objMap.put(FILE_ATTACHMENT, fa);
							return true; // File name matches
						}
					}
					return false; // File name didn't match
				}
			}
		} catch (NoBinderByTheIdException e) {
			return false;
		} catch (NoFolderByTheIdException e) {
			return false;
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
	}
	
	private String getLibraryElementName(Definition definition) {
		Document defDoc = definition.getDefinition();
		String itemType = toDefItemType(CrossContextConstants.URI_ITEM_TYPE_LIBRARY);
		Element root = defDoc.getRootElement();
		Element item = (Element) root.selectSingleNode("//item[@name='" + itemType
				+ "' and @type='data']");
		Element nameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
		String elementName = nameProperty.attributeValue("value");
		return elementName;
	}
	
	private String getLibraryRepositoryName(Definition definition) {
		Document defDoc = definition.getDefinition();
		String itemType = toDefItemType(CrossContextConstants.URI_ITEM_TYPE_LIBRARY);
		Element root = defDoc.getRootElement();
		Element item = (Element) root.selectSingleNode("//item[@name='" + itemType
				+ "' and @type='data']");
		Element storageElem = (Element) item.selectSingleNode("./properties/property[@name='storage']");
		String value = storageElem.attributeValue("value");
		if(value == null)
			value = storageElem.attributeValue("default");
		return value;
	}
	
	private boolean objectExistsLibrary(Map uri, Map objMap) throws NoAccessException {
		try {
			// File folder id
			Long binderId = getBinderId(uri);
			if (binderId == null)
				return true; // no more checking to do

			Binder binder = getBinderModule().getBinder(binderId);
			objMap.put(BINDER, binder);

			// File path 
			String filePath = getFilePath(uri);
			if (filePath == null)
				return true; // no more checking to do
			
			// Locate file folder entry
			FolderEntry fileFolderEntry = getFileModule().findFileFolderEntry(binder, filePath);
			
			if(fileFolderEntry == null)
				return false;
			
			objMap.put(ENTRY, fileFolderEntry);
			
			// Locate file attachment
			Definition definition = fileFolderEntry.getEntryDef();
			
			String repositoryName = getLibraryRepositoryName(definition);
			
			FileAttachment fa = fileFolderEntry.getFileAttachment(repositoryName, filePath);
			
			objMap.put(FILE_ATTACHMENT, fa);
			
			return true;
		} catch (NoBinderByTheIdException e) {
			return false;
		} catch (NoFolderByTheIdException e) {
			return false;
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		
	}
}
