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
package com.sitescape.team.module.shared;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.DatedMultipartFile;
import com.sitescape.team.util.SpringContextUtil;

public class FolderUtils {

	private static final Map<String,Object> ITEM_NAMES;
	
	static {
		ITEM_NAMES = new HashMap<String,Object>();
		ITEM_NAMES.put("attachFiles", null);
		ITEM_NAMES.put("graphic", null);
		ITEM_NAMES.put("file", null);
		ITEM_NAMES.put("profileEntryPicture", null);
	}
			
	private static final String ATTACH_FILES = "attachFiles";
	
	/**
	 * Create a library entry.
	 * 
	 * @param folder
	 * @param fileName
	 * @param content
	 * @param modDate
	 * @param synchToSourceIfMirrored applicable to mirrored folder only; 
	 * this value is ignored for non-mirrored folder
	 * @return
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public static Long createLibraryEntry(Folder folder, String fileName,
			InputStream content, Date modDate, boolean synchToSourceIfMirrored) 
	throws ConfigurationException, AccessControlException, WriteFilesException {
		if(folder.isLibrary()) {
			if(folder.isMirrored()) {
				return createMirroredEntry(folder, fileName, content, modDate, synchToSourceIfMirrored);
			}
			else {
				return createNonMirroredEntry(folder, fileName, content, modDate);
			}
		}
		else {
			throw new IllegalArgumentException("Folder [" + folder.getPathName() + "] is not a library folder");
		}
	}
	
	/**
	 * Modify a library entry.
	 * 
	 * @param entry
	 * @param fileName
	 * @param content
	 * @param modDate
	 * @param synchToSourceIfMirrored
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public static void modifyLibraryEntry(FolderEntry entry, String fileName,
			InputStream content, Date modDate, boolean synchToSourceIfMirrored)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Folder folder = entry.getParentFolder();
		if(folder.isLibrary()) {
			if(folder.isMirrored()) {
				modifyMirroredEntry(entry, fileName, content, modDate, synchToSourceIfMirrored);
			}
			else {
				modifyNonMirroredEntry(entry, fileName, content, modDate);
			}
		}
		else {
			throw new IllegalArgumentException("Parent folder [" + folder.getPathName() + "] is not a library folder");
		}
	}

	/**
	 * Create a mirrored folder.
	 * 
	 * @param parentBinder
	 * @param folderName
	 * @param resourceDriverName
	 * @param resourcePath
	 * @param synchToSource
	 * @return
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public static Long createMirroredFolder(Binder parentBinder, String folderName, 
			String resourceDriverName, String resourcePath, boolean synchToSource)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderDefinition(parentBinder);
		if(def == null)
			throw new ConfigurationException("There is no folder definition to use");
		
		Map<String,Object> data = new HashMap<String,Object>(); // Input data
		data.put(ObjectKeys.FIELD_ENTITY_TITLE, folderName); 
		data.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.TRUE.toString());
		data.put(ObjectKeys.FIELD_BINDER_MIRRORED, Boolean.TRUE.toString());
		data.put(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, resourceDriverName);
		data.put(ObjectKeys.FIELD_BINDER_RESOURCE_PATH, resourcePath);
		data.put(ObjectKeys.PI_SYNCH_TO_SOURCE, Boolean.toString(synchToSource));
		
		if(parentBinder instanceof Workspace)
			return getWorkspaceModule().addFolder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), new HashMap());
		else
			return getFolderModule().addFolder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), new HashMap());
	}

	/**
	 * Create a new library folder.
	 * 
	 * @param bs
	 * @param parentBinder parent binder; should be either a folder or a workspace
	 * @param folderName folder name
	 * @return
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public static Long createLibraryFolder(Binder parentBinder, String folderName)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderDefinition(parentBinder);
		if(def == null)
			throw new ConfigurationException("There is no folder definition to use");
		
		Map data = new HashMap(); // Input data
		// Title field, not name, is used as the name of the folder. Weird...
		data.put(ObjectKeys.FIELD_ENTITY_TITLE, folderName); 
		//data.put("description", "This folder was created through WebDAV");
		data.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.TRUE.toString());
		
		if(parentBinder instanceof Workspace)
			return getWorkspaceModule().addFolder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), new HashMap());
		else
			return getFolderModule().addFolder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), new HashMap());
	}
	
	public static void deleteMirroredFolder(Folder folder, boolean deleteMirroredSource)
	throws AccessControlException {
		getBinderModule().deleteBinder(folder.getId(), deleteMirroredSource);
	}
	
	public static void deleteMirroredEntry(Folder parentFolder, FolderEntry entry, boolean deleteMirroredSource) {
		getFolderModule().deleteEntry(parentFolder.getId(), entry.getId(), deleteMirroredSource);
	}
	
	public static boolean isMirroredFolder(Binder binder) {
		return ((binder instanceof Folder) && ((Folder) binder).isMirrored());
	}
	
	/**
	 * Creates a new folder entry with an attachment file.
	 * 
	 * @param folder non-mirrored library folder
	 * @param fileName file name
	 * @param content file content
	 * @param modDate (optional) file mod date or <code>null</code> 
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	private static Long createNonMirroredEntry(Folder folder, String fileName, InputStream content, Date modDate)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("There is no folder entry definition to use");
		
		String elementName = getDefinitionElementNameForNonMirroredFile(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null)
			mf = new DatedMultipartFile(fileName, content, modDate);
		else
			mf = new SimpleMultipartFile(fileName, content); 
		
		Map fileItems = new HashMap(); // Map of element names to file items	
		fileItems.put(elementName, mf); // single file item
		
		Map data = new HashMap(); // Input data
		data.put(ObjectKeys.FIELD_ENTITY_TITLE, fileName);
		
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			data.put(ObjectKeys.PI_LAST_MODIFIED, modDate);
		}
		
		return getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems);
	}
	
	private static Long createMirroredEntry(Folder folder, String fileName, 
			InputStream content, Date modDate, boolean synchToSource)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("There is no folder entry definition to use");
		
		String[] elementNameAndRepository = getDefinitionElementNameForMirroredFile(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null)
			mf = new DatedMultipartFile(fileName, content, modDate);
		else
			mf = new SimpleMultipartFile(fileName, content); 
		
		Map fileItems = new HashMap(); // Map of element names to file items	
		fileItems.put(elementNameAndRepository[0], mf); // single file item
		
		Map data = new HashMap(); // Input data
		data.put(ObjectKeys.FIELD_ENTITY_TITLE, fileName);
		data.put(ObjectKeys.PI_SYNCH_TO_SOURCE, Boolean.toString(synchToSource));
		if(elementNameAndRepository[1] != null)
			data.put(elementNameAndRepository[1], ObjectKeys.FI_ADAPTER);
		
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			data.put(ObjectKeys.PI_LAST_MODIFIED, modDate);
		}
		
		return getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems);
	}

	/**
	 * Modifies an existing folder entry with new file content for the
	 * attachment.
	 * 
	 * @param entry 
	 * @param fileName file name
	 * @param content file content
	 * @param modDate (optional) file mod date or <code>null</code> 
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	private static void modifyNonMirroredEntry(FolderEntry entry, String fileName, 
			InputStream content, Date modDate) 
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Folder folder = entry.getParentFolder();
		
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("There is no folder entry definition to use");
		
		String elementName = getDefinitionElementNameForNonMirroredFile(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null)
			mf = new DatedMultipartFile(fileName, content, modDate);
		else
			mf = new SimpleMultipartFile(fileName, content); 
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementName, mf); // single file item
		
		InputDataAccessor inputData;
		
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			Map data = new HashMap();
			data.put(ObjectKeys.PI_LAST_MODIFIED, modDate);
			inputData = new MapInputData(data);
		}
		else {
			inputData = new EmptyInputData(); // No non-file input data
		}

		getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
				inputData, fileItems, null, null);
	}
	
	private static void modifyMirroredEntry(FolderEntry entry, String fileName, 
			InputStream content, Date modDate, boolean synchToSource) 
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Folder folder = entry.getParentFolder();
		
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("There is no folder entry definition to use");
		
		String[] elementNameAndRepository = getDefinitionElementNameForMirroredFile(def);

		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null)
			mf = new DatedMultipartFile(fileName, content, modDate);
		else
			mf = new SimpleMultipartFile(fileName, content); 
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementNameAndRepository[0], mf); // single file item
		
		Map data = new HashMap(); // Input data
		data.put(ObjectKeys.PI_SYNCH_TO_SOURCE, Boolean.toString(synchToSource));
		if(elementNameAndRepository[1] != null)
			data.put(elementNameAndRepository[1], ObjectKeys.FI_ADAPTER);
	
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			data.put(ObjectKeys.PI_LAST_MODIFIED, modDate);
		}

		getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
				new MapInputData(data), fileItems, null, null);
	}

	private static String getDefinitionElementNameForNonMirroredFile(Definition definition) {
		Document defDoc = definition.getDefinition();
		Element root = defDoc.getRootElement();
		Element formItem = (Element) root.selectSingleNode("//item[@type='form']");
		Element item = null;
		Iterator itItems = formItem.selectNodes("//item").iterator();
		while (itItems.hasNext()) {
			//Look for the first file-related element that matches the criteria
			Element itemEle = (Element) itItems.next();
			if (ITEM_NAMES.containsKey(itemEle.attributeValue("name"))) {
				String repositoryName = getStorageValueFromItemElem(itemEle);				
				if(!repositoryName.equals(ObjectKeys.FI_ADAPTER)) {
					item = itemEle;
					break;
				}
			}
		}
		Element nameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
		String elementName = nameProperty.attributeValue("value");
		
		if (item.attributeValue("name").equals(ATTACH_FILES)) {
			// Since attachment element allows uploading multiple files at the
			// same (when done through Aspen UI), each file is identified 
			// uniquely by appending numeric number (1-based) to the element
			// name. When uploaded through WebDAV, there is always exactly one
			// file involed. So we use "1".
			return elementName + "1";
		} else {		
			return elementName;
		}
	}

	private static String[] getDefinitionElementNameForMirroredFile(Definition definition) {
		Document defDoc = definition.getDefinition();
		Element root = defDoc.getRootElement();
		Element formItem = (Element) root.selectSingleNode("//item[@type='form']");
		Element item = null;
		Iterator itItems = formItem.selectNodes("//item").iterator();
		while (itItems.hasNext()) {
			//Look for the first file-related element that matches the criteria
			Element itemEle = (Element) itItems.next();
			String itemName = itemEle.attributeValue("name");
			if (ITEM_NAMES.containsKey(itemName)) {
				if(itemName.equals(ATTACH_FILES)) {
					// In this case, we don't care what repository this element
					// is configured with (for default situation), since attach
					// element is capable of routing files to any repository.
					item = itemEle;
					break;
				}
				else {
					String repositoryName = getStorageValueFromItemElem(itemEle);
					if(repositoryName.equals(ObjectKeys.FI_ADAPTER)) {
						item = itemEle;
						break;
					}
				}
			}
		}
		Element nameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
		String elementName = nameProperty.attributeValue("value");
		
		if (item.attributeValue("name").equals(ATTACH_FILES)) {
			// Since attachment element allows uploading multiple files at the
			// same (when done through Aspen UI), each file is identified 
			// uniquely by appending numeric number (1-based) to the element
			// name. When uploaded through WebDAV, there is always exactly one
			// file involed. So we use "1".
			return new String[] {elementName + "1", elementName + "_repos1"};
		} else {		
			return new String[] {elementName, null};
		}
	}

	private static String getStorageValueFromItemElem(Element itemElem) {
		Element storageElem = (Element) itemElem.selectSingleNode("./properties/property[@name='storage']");
		String value = storageElem.attributeValue("value");
		if(value == null || value.length() == 0) {
			value = storageElem.attributeValue("default");
		}
		if(value == null || value.length() == 0) {
			value = RepositoryUtil.getDefaultRepositoryName();
		}
		return value;
	}
	
	private static Definition getFolderEntryDefinition(Folder folder) {
		Definition def = folder.getDefaultEntryDef();
		if(def == null)
			def = getZoneWideDefaultFolderEntryDefinition();
		return def;
	}
	
	private static Definition getZoneWideDefaultFolderEntryDefinition() {
		List defs = getDefinitionModule().getDefinitions(Definition.FOLDER_ENTRY);
		if(defs != null)
			return (Definition) defs.get(0);
		else
			return null;
	}
	
	private static Definition getFolderDefinition(Binder parentBinder) {
		if(parentBinder instanceof Folder) {
			// If the parent binder in which to create a new library folder
			// happens to be a folder itself, simply re-use the folder
			// definition of the parent. That is, make the sub-directory
			// the same type as its parent.
			return parentBinder.getEntryDef();
		}
		else {
			// The binder must be a workspace.
			return getZoneWideDefaultFolderDefinition();
		}
	}
	
	private static Definition getZoneWideDefaultFolderDefinition() {
		List defs = getDefinitionModule().getDefinitions(Definition.FOLDER_VIEW);
		if(defs != null)
			return (Definition) defs.get(0);
		else
			return null;
	}

	private static FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}
	private static DefinitionModule getDefinitionModule() {
		return (DefinitionModule) SpringContextUtil.getBean("definitionModule");
	}
	private static WorkspaceModule getWorkspaceModule() {
		return (WorkspaceModule) SpringContextUtil.getBean("workspaceModule");
	}
	private static BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}
}
