/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.shared;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.DatedMultipartFile;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.springframework.web.multipart.MultipartFile;


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
	public static FolderEntry createLibraryEntry(Folder folder, String fileName,
			InputStream content, Date modDate, boolean synchToSourceIfMirrored) 
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
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
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
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
	public static Binder createMirroredFolder(Binder parentBinder, String folderName, 
			String resourceDriverName, String resourcePath, boolean synchToSource)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Definition def = getFolderDefinition(parentBinder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.folder.definition", (Object[])null);
		
		Map<String,Object> data = new HashMap<String,Object>(); // Input data
		data.put(ObjectKeys.FIELD_ENTITY_TITLE, folderName); 
		data.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.TRUE.toString());
		data.put(ObjectKeys.FIELD_BINDER_MIRRORED, Boolean.TRUE.toString());
		if(resourceDriverName != null)
			data.put(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, resourceDriverName);
		if(resourcePath != null)
			data.put(ObjectKeys.FIELD_BINDER_RESOURCE_PATH, resourcePath);
		data.put(ObjectKeys.PI_SYNCH_TO_SOURCE, Boolean.toString(synchToSource));
		Map params = new HashMap();
		params.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);
		return getBinderModule().addBinder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), null, params);
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
	public static Binder createLibraryFolder(Binder parentBinder, String folderName)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		if((EntityType.folder == parentBinder.getEntityType()) && parentBinder.isMirrored()) {
			return createMirroredFolder(parentBinder, folderName, parentBinder.getResourceDriverName(), null, true);
		}
		else {
			return createNonMirroredFolder(parentBinder, folderName);
		}
	}
	
	private static Binder createNonMirroredFolder(Binder parentBinder, String folderName)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Definition def = getFolderDefinition(parentBinder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.folder.definition", (Object[])null);
		
		Map data = new HashMap(); // Input data
		// Title field, not name, is used as the name of the folder. Weird...
		data.put(ObjectKeys.FIELD_ENTITY_TITLE, folderName); 
		//data.put("description", "This folder was created through WebDAV");
		data.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.TRUE.toString());
		Map params = new HashMap();
		params.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);

		return getBinderModule().addBinder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), null, params);
	}
	
	public static void deleteMirroredFolder(Folder folder, boolean deleteMirroredSource)
	throws AccessControlException {
		getBinderModule().deleteBinder(folder.getId(), deleteMirroredSource, null);
	}
	
	public static void deleteMirroredEntry(Folder parentFolder, FolderEntry entry, boolean deleteMirroredSource) {
		getFolderModule().deleteEntry(parentFolder.getId(), entry.getId(), deleteMirroredSource, null);
	}
	
	public static void deleteMirroredEntry(Long folderId, Long entryId, boolean deleteMirroredSource) {
		getFolderModule().deleteEntry(folderId, entryId, deleteMirroredSource, null);
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
	private static FolderEntry createNonMirroredEntry(Folder folder, String fileName, InputStream content, Date modDate)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.entry.definition", (Object[])null);
		
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
				
		return getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems, null);
	}
	
	private static FolderEntry createMirroredEntry(Folder folder, String fileName, 
			InputStream content, Date modDate, boolean synchToSource)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.entry.definition", (Object[])null);
		
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
		
		return getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems, null);
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
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Folder folder = entry.getParentFolder();
		
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.entry.definition", (Object[])null);
		
		String elementName = getDefinitionElementNameForNonMirroredFile(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null)
			mf = new DatedMultipartFile(fileName, content, modDate);
		else
			mf = new SimpleMultipartFile(fileName, content); 
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementName, mf); // single file item
		
		InputDataAccessor inputData = new EmptyInputData(); // No non-file input data

		getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
				inputData, fileItems, null, null, null);
	}
	
	private static void modifyMirroredEntry(FolderEntry entry, String fileName, 
			InputStream content, Date modDate, boolean synchToSource) 
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Folder folder = entry.getParentFolder();
		
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.entry.definition", (Object[])null);
		
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
	
		getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
				new MapInputData(data), fileItems, null, null, null);
	}

	private static String getDefinitionElementNameForNonMirroredFile(Definition definition) 
	throws ConfigurationException {
		Element item = getDefinitionItemForNonMirroredFile(definition.getDefinition());
		if(item != null) {
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
		else {
			// This means one of the following conditions:
			// 1. The defintion does not contain any of the file related elements.
			// 2. The definition does not contain any file related elements 
			// configured to use a regular repository.
			throw new ConfigurationException("errorcode.no.element.regular.file", new String[]{definition.getName()});
		}
	}


	public static Element getDefinitionItemForNonMirroredFile(Document defDoc) {
		Element root = defDoc.getRootElement();
		Element formItem = (Element) root.selectSingleNode("//item[@type='form']");
		Element item = null;
		Iterator itItems = formItem.selectNodes("//item").iterator();
		while (itItems.hasNext()) {
			//Look for the first file-related element that matches the criteria
			Element itemEle = (Element) itItems.next();
			String itemName = itemEle.attributeValue("name");
			if (ITEM_NAMES.containsKey(itemName)) {
				String repositoryName = getStorageValueFromItemElem(itemEle);				
				if(!repositoryName.equals(ObjectKeys.FI_ADAPTER)) {
					item = itemEle;
					break;
				}
			}
		}
		return item; // This may be null
	}

	private static String[] getDefinitionElementNameForMirroredFile(Definition definition) 
	throws ConfigurationException {
		SimpleProfiler.start("FolderUtils.getDefinitionElementNameForMirroredFile");
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
		if(item != null) {
			Element nameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
			String elementName = nameProperty.attributeValue("value");
			
			String[] result;
			if (item.attributeValue("name").equals(ATTACH_FILES)) {
				// Since attachment element allows uploading multiple files at the
				// same (when done through Aspen UI), each file is identified 
				// uniquely by appending numeric number (1-based) to the element
				// name. When uploaded through WebDAV, there is always exactly one
				// file involed. So we use "1".
				result = new String[] {elementName + "1", elementName + "_repos1"};
			} else {		
				result = new String[] {elementName, null};
			}
			SimpleProfiler.stop("FolderUtils.getDefinitionElementNameForMirroredFile");
			return result;
		}
		else {	
			// This means one of the following conditions:
			// 1. The defintion does not contain any of the file related elements.
			// 2. The definition does not contain attachFiles element (which normally
			// shouldn't happen) and all other file related elements are configured
			// to use regular repositories (as opposed to external resource adapter).
			throw new ConfigurationException("errorcode.no.element.mirrored.file", new String[]{definition.getName()});
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
		if(def == null) {
			def = folder.getDefaultFileEntryDef();
		}
		if(def == null)
			def = getZoneWideDefaultFolderEntryDefinition();
		return def;
	}
	
	private static Definition getZoneWideDefaultFolderEntryDefinition() {
		List<Definition> defs = getDefinitionModule().getDefinitions(null, Boolean.FALSE, Definition.FOLDER_ENTRY);
		for (Definition def:defs) {
			if (ObjectKeys.DEFAULT_FOLDER_ENTRY_DEF.equals(def.getInternalId())) return def;
		}
		for (Definition def:defs) {
			if (!Definition.VISIBILITY_DEPRECATED.equals(def.getVisibility())) return def;
		}
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
		List<Definition> defs = getDefinitionModule().getDefinitions(null, Boolean.FALSE, Definition.FOLDER_ENTRY);
		for (Definition def:defs) {
			if (ObjectKeys.DEFAULT_FOLDER_DEF.equals(def.getInternalId())) return def;
		}
		for (Definition def:defs) {
			if (!Definition.VISIBILITY_DEPRECATED.equals(def.getVisibility())) return def;
		}
		return null;
	}
	
	public static void deleteFileInFolderEntry(FolderEntry entry, FileAttachment fa) throws AccessControlException, ReservedByAnotherUserException, WriteFilesException, WriteEntryDataException {
		Binder parentBinder = entry.getParentBinder();
		if(parentBinder.isMirrored() && fa.getRepositoryName().equals(ObjectKeys.FI_ADAPTER)) {
			// The file being deleted is a mirrored file.
			// In this case, we delete the entire entry regardless of what else the
			// entry might contain, since we don't want to leave an entry that no 
			// longer mirrors any source file 
			FolderUtils.deleteMirroredEntry((Folder)parentBinder, entry, true);
		}
		else {
			if(entry.getFileAttachments().size() > 1) {
				// This file being deleted isn't the only file associated with the entry.
				// Just delete the file only, and leave the entry.
				// This will honor the Bug #632304.
				List faId = new ArrayList();
				faId.add(fa.getId());
				
				getFolderModule().modifyEntry(parentBinder.getId(), entry.getId(), new EmptyInputData(), null, faId, null, null);
			}
			else {
				// This file being deleted is the only file associated with the entry.
				// Delete the entire entry, instead of leaving an empty/dangling entry with no file.
				// This will honor the Bug #554284.
				getFolderModule().deleteEntry(parentBinder.getId(), entry.getId(), true, null);					
			}
		}

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
