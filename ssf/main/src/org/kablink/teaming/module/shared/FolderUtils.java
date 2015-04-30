/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.FilesErrors;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ExtendedMultipartFile;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleMultipartFile;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.TrashHelper;
import org.springframework.web.multipart.MultipartFile;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class FolderUtils {
	private static final Map<String,Object> ITEM_NAMES;
	
	private static final String LIBRARY_FOLDER_DEFAULT_TEMPLATE_NAME = "_folder_library";
	
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
			InputStream content, Date modDate, String expectedMd5, boolean synchToSourceIfMirrored)
					throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		return createLibraryEntry(folder, fileName, null, content, null, null, modDate, expectedMd5, synchToSourceIfMirrored, null, null, null, null);
	}
	
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
	public static FolderEntry createLibraryEntry(Folder folder, String fileName, String resourceHandle,
			InputStream content, Long contentLength, Long creatorId, Date modDate, String expectedMd5, boolean synchToSourceIfMirrored, Boolean skipParentModtimeUpdate, Boolean skipFileContentIndexing, Boolean skipDbLog, Boolean skipNotifyStatus)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		if(folder.isLibrary()) {
			if(folder.isMirrored()) {
				return createMirroredEntry(folder, fileName, resourceHandle, content, contentLength, creatorId, modDate, expectedMd5, synchToSourceIfMirrored, skipParentModtimeUpdate, skipFileContentIndexing, skipDbLog, skipNotifyStatus);
			}
			else {
				return createNonMirroredEntry(folder, fileName, content, contentLength, modDate, expectedMd5, skipParentModtimeUpdate);
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
	public static void modifyLibraryEntry(FolderEntry entry, String fileName, String resourceHandle,
			InputStream content, Long contentLength, Date modDate, String expectedMd5, boolean synchToSourceIfMirrored, Boolean skipFileContentIndexing, Boolean skipNotifyStatus)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Folder folder = entry.getParentFolder();
		if(folder.isLibrary()) {
			if(folder.isMirrored()) {
				modifyMirroredEntry(entry, fileName, resourceHandle, content, contentLength, modDate, expectedMd5, synchToSourceIfMirrored, skipFileContentIndexing, skipNotifyStatus);
			}
			else {
				modifyNonMirroredEntry(entry, fileName, content, contentLength, modDate, expectedMd5);
			}
		}
		else {
			throw new IllegalArgumentException("Parent folder [" + folder.getPathName() + "] is not a library folder");
		}
	}

	/**
	 * Create a mirrored folder.
	 * 
	 * This method expects the parent binder to be a mirrored folder as well. Otherwise, this throws an error.
	 * Also, by definition, a mirrored folder is supposed to be a library folder, and therefore, it is
	 * implicit that the newly created folder is a library folder.
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
			String resourceDriverName, String resourcePath, String resourceHandle, 
			Long ownerId, Long creatorId, Date modDate, boolean synchToSource, 
			Boolean skipParentModtimeUpdate, Boolean skipDbLog)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		if(EntityType.folder != parentBinder.getEntityType() || !parentBinder.isMirrored())
			throw new IllegalArgumentException("The parent binder '" + parentBinder.getId() + "' is not a mirrored folder");
				
		Map<String,Object> data = new HashMap<String,Object>(); // Input data
		if(parentBinder.getNetFolderConfigId() != null)
			data.put(ObjectKeys.FIELD_NET_FOLDER_CONFIG_ID, parentBinder.getNetFolderConfigId().toString());
		if(resourceDriverName != null)
			data.put(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, resourceDriverName);
		if(resourcePath != null)
			data.put(ObjectKeys.FIELD_BINDER_RESOURCE_PATH, resourcePath);
		if(resourceHandle != null)
			data.put(ObjectKeys.FIELD_RESOURCE_HANDLE, resourceHandle);
		data.put(ObjectKeys.PI_SYNCH_TO_SOURCE, Boolean.toString(synchToSource));
		
		Map options = new HashMap();
		if(modDate != null) {
			Calendar modCal = Calendar.getInstance();
			modCal.setTime(modDate);
			options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_DATE, modCal);
		}		
		if(ownerId != null)
			options.put(ObjectKeys.INPUT_OPTION_OWNER_ID, ownerId);
		if(creatorId != null) {
			options.put(ObjectKeys.INPUT_OPTION_CREATION_ID, creatorId);
			// For newly created folder, it doesn't make sense if the modifier were different from the creator 
			// (more precisely speaking, there has not been any modification yet).
			options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_ID, creatorId);
		}
		if(skipParentModtimeUpdate != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_PARENT_MODTIME_UPDATE, skipParentModtimeUpdate);
		
		if(skipDbLog != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_DB_LOG, skipDbLog);
		
		TemplateBinder template = getTemplateModule().getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_MIRRORED_FILE);
		
		return getTemplateModule().addBinder(template.getId(), parentBinder.getId(), folderName, "", data, options);
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
		Binder binder;
		if((EntityType.folder == parentBinder.getEntityType()) && parentBinder.isMirrored()) {
			binder = createMirroredFolder(parentBinder, folderName, parentBinder.getResourceDriverName(), null, null, null, null, null, true, null, null);
		}
		else { 
			binder = createNonMirroredLibraryFolder(parentBinder, folderName);
		}
		
		
		if(parentBinder.getEntityType() == EntityType.folder) {
			getBinderModule().setDefinitionsInherited(binder.getId(), true, false);
		}
		return binder;
	}
	
	/**
	 * Creates a non-mirrored library folder.
	 * 
	 *  The parent binder could be any binder, as long as it is not a mirrored folder.
	 * 
	 * @param parentBinder
	 * @param folderName
	 * @return
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 * @throws WriteEntryDataException
	 */
	private static Binder createNonMirroredLibraryFolder(Binder parentBinder, String folderName)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		if(parentBinder.isMirrored())
			throw new IllegalArgumentException("Parent binder '" + parentBinder.getId() + "' is a mirrored one");
		
		Definition def = null;
		
		if(EntityType.folder == parentBinder.getEntityType()) {
			if(parentBinder.isLibrary()) {
				// The parent binder is a library folder such as file folder or photo album.
				// In this case, we want the child folder to be of the same type as the parent.
				def = parentBinder.getEntryDef();		
				if(def == null)
					throw new ConfigurationException("errorcode.no.folder.definition", (Object[])null);
			}
			else {
				// The parent binder is not a library folder. We can't inherit the type of the parent
				// binder in this case. We will create a file folder with default configuration.
			}
		}
		else { // workspace
			// The parent binder is a workspace. We can't inherit the type of the parent binder
			// in this case. We will create a file folder with default configuration.
		}
		
		if(def != null) {
			Map data = new HashMap(); // Input data
			// Title field, not name, is used as the name of the folder. Weird...
			data.put(ObjectKeys.FIELD_ENTITY_TITLE, folderName);
			//data.put("description", "This folder was created through WebDAV");
			data.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.TRUE.toString());
			Map params = new HashMap();
			params.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);
            if (Utils.isWorkareaInProfilesTree(parentBinder) && parentBinder.getOwner()!=null) {
			    params.put(ObjectKeys.INPUT_OPTION_OWNER_ID, parentBinder.getOwner().getId());
            }

			Binder binder = getBinderModule().addBinder(parentBinder.getId(), def.getId(),
						new MapInputData(data), null, params);

			// Inherit configuration.
			inheritAll(binder.getId());

			return binder;
		}
		else {
			String templateName = SPropsUtil.getString("library.folder.default.template.name", LIBRARY_FOLDER_DEFAULT_TEMPLATE_NAME);
			
			TemplateBinder template = getTemplateModule().getTemplateByName(templateName);
			
			Binder binder = getTemplateModule().addBinder(template.getId(), parentBinder.getId(), folderName, "");
			
			return binder;
		}
	}
	
	public static void deleteMirroredFolder(Folder folder, boolean deleteMirroredSource, Boolean skipParentModtimeUpdate)
	throws AccessControlException {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS, true);
		if(skipParentModtimeUpdate != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_PARENT_MODTIME_UPDATE, skipParentModtimeUpdate);
		getBinderModule().deleteBinder(folder.getId(), deleteMirroredSource, options);
	}
	
	public static void deleteMirroredEntry(Folder parentFolder, FolderEntry entry, boolean deleteMirroredSource, 
			Boolean skipParentModtimeUpdate) throws AccessControlException, WriteFilesException {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS, true);
		if(skipParentModtimeUpdate != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_PARENT_MODTIME_UPDATE, skipParentModtimeUpdate);
		getFolderModule().deleteEntry(parentFolder.getId(), entry.getId(), deleteMirroredSource, options);
	}
	
	public static void deleteMirroredEntry(Long folderId, Long entryId, boolean deleteMirroredSource) 
			throws AccessControlException, WriteFilesException {
		Map options = new HashMap();
		options.put(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS, true);
		getFolderModule().deleteEntry(folderId, entryId, deleteMirroredSource, options);
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
	private static FolderEntry createNonMirroredEntry(Folder folder, String fileName, InputStream content, Long contentLength, Date modDate, String expectedMd5, Boolean skipParentModtimeUpdate)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.entry.definition", (Object[])null);
		
		String elementName = getDefinitionElementNameForNonMirroredFile(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null || expectedMd5 != null)
			mf = new ExtendedMultipartFile(fileName, content, contentLength, modDate, expectedMd5);
		else
			mf = new SimpleMultipartFile(fileName, content, contentLength); 
		
		Map fileItems = new HashMap(); // Map of element names to file items	
		fileItems.put(elementName, mf); // single file item
		
		Map data = new HashMap(); // Input data
		data.put(ObjectKeys.FIELD_ENTITY_TITLE, fileName);
		
		Map options = new HashMap();
		if(skipParentModtimeUpdate != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_PARENT_MODTIME_UPDATE, skipParentModtimeUpdate);
				
		return getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems, options);
	}
	
	private static FolderEntry createMirroredEntry(Folder folder, String fileName, String resourceHandle,
			InputStream content, Long contentLength, Long creatorId, Date modDate, String expectedMd5, boolean synchToSource, Boolean skipParentModtimeUpdate, Boolean skipFileContentIndexing, Boolean skipDbLog, Boolean skipNotifyStatus)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.entry.definition", (Object[])null);
		
		String[] elementNameAndRepository = getDefinitionElementNameForMirroredFile(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		Map options = new HashMap();
		if(modDate != null || creatorId != null || expectedMd5!=null) {
			ExtendedMultipartFile dmf = new ExtendedMultipartFile(fileName, content, contentLength);
			if(modDate != null) {
				dmf.setModDate(modDate);
			}
			if(creatorId != null) {
				dmf.setCreatorId(creatorId);
				// For newly created file, it doesn't make sense if the modifier were different from the creator.
				dmf.setModifierId(creatorId);
				options.put(ObjectKeys.INPUT_OPTION_CREATION_ID, creatorId);
				// For newly created entry, it doesn't make sense if the modifier were different from the creator 
				// (more precisely speaking, there has not been any modification yet).
				options.put(ObjectKeys.INPUT_OPTION_MODIFICATION_ID, creatorId);
			}
            dmf.setExpectedMd5(expectedMd5);
			mf = dmf;
		}
		else {
			mf = new SimpleMultipartFile(fileName, content, contentLength); 
		}
		if(skipParentModtimeUpdate != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_PARENT_MODTIME_UPDATE, skipParentModtimeUpdate);
		if(skipFileContentIndexing != null)
			options.put(ObjectKeys.INPUT_OPTION_NO_FILE_CONTENT_INDEX, skipFileContentIndexing);
		
		Map fileItems = new HashMap(); // Map of element names to file items	
		fileItems.put(elementNameAndRepository[0], mf); // single file item
		
		Map data = new HashMap(); // Input data
		data.put(ObjectKeys.FIELD_ENTITY_TITLE, fileName);
		if(resourceHandle != null)
			data.put(ObjectKeys.FIELD_RESOURCE_HANDLE, resourceHandle);
		data.put(ObjectKeys.PI_SYNCH_TO_SOURCE, Boolean.toString(synchToSource));
		if(elementNameAndRepository[1] != null)
			data.put(elementNameAndRepository[1], ObjectKeys.FI_ADAPTER);
		
		if(skipDbLog != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_DB_LOG, skipDbLog);
		
		if(skipNotifyStatus != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_NOTIFY_STATUS, skipNotifyStatus);
		
		return getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems, options);
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
			InputStream content, Long contentLength, Date modDate, String expectedMd5)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Folder folder = entry.getParentFolder();
		
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.entry.definition", (Object[])null);
		
		String elementName = getDefinitionElementNameForNonMirroredFile(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null || expectedMd5!=null)
			mf = new ExtendedMultipartFile(fileName, content, contentLength, modDate, expectedMd5);
		else
			mf = new SimpleMultipartFile(fileName, content, contentLength); 
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementName, mf); // single file item
		
		InputDataAccessor inputData = new EmptyInputData(); // No non-file input data

		getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
				inputData, fileItems, null, null, null);
	}
	
	private static void modifyMirroredEntry(FolderEntry entry, String fileName, String resourceHandle,
			InputStream content, Long contentLength, Date modDate, String expectedMd5, boolean synchToSource, Boolean skipFileContentIndexing, Boolean skipNotifyStatus)
	throws ConfigurationException, AccessControlException, WriteFilesException, WriteEntryDataException {
		Folder folder = entry.getParentFolder();
		
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("errorcode.no.entry.definition", (Object[])null);
		
		String[] elementNameAndRepository = getDefinitionElementNameForMirroredFile(def);

		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null || expectedMd5 != null)
			mf = new ExtendedMultipartFile(fileName, content, contentLength, modDate, expectedMd5);
		else
			mf = new SimpleMultipartFile(fileName, content, contentLength); 
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(elementNameAndRepository[0], mf); // single file item
		
		Map data = new HashMap(); // Input data
		if(resourceHandle != null)
			data.put(ObjectKeys.FIELD_RESOURCE_HANDLE, resourceHandle);
		data.put(ObjectKeys.PI_SYNCH_TO_SOURCE, Boolean.toString(synchToSource));
		if(elementNameAndRepository[1] != null)
			data.put(elementNameAndRepository[1], ObjectKeys.FI_ADAPTER);
	
		Map options = new HashMap();
		if(skipFileContentIndexing != null)
			options.put(ObjectKeys.INPUT_OPTION_NO_FILE_CONTENT_INDEX, skipFileContentIndexing);

		if(skipNotifyStatus != null)
			options.put(ObjectKeys.INPUT_OPTION_SKIP_NOTIFY_STATUS, skipNotifyStatus);

		getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
				new MapInputData(data), fileItems, null, null, options);
	}

	public static String getDefinitionElementNameForNonMirroredFile(Definition definition) 
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

	public static String[] getDefinitionElementNameForMirroredFile(Definition definition) 
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
	
	public static Definition getFolderEntryDefinition(Folder folder) {
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
	
	/*
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
	*/
	
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
	
	public static void deleteFileInFolderEntry(AllModulesInjected bs, FolderEntry entry, FileAttachment fa) 
			throws AccessControlException, ReservedByAnotherUserException, WriteFilesException, WriteEntryDataException {
        // By default, entry is simply moved into trash rather than permanently deleted.
        boolean predelete = SPropsUtil.getBoolean("folderutils.deleteentry.predelete", true);
        deleteFileInFolderEntry(bs, entry, fa, predelete);
    }

	public static void deleteFileInFolderEntry(AllModulesInjected bs, FolderEntry entry, FileAttachment fa, boolean predelete) 
			throws AccessControlException, ReservedByAnotherUserException, WriteFilesException, WriteEntryDataException {
		Binder parentBinder = entry.getParentBinder();
		if(parentBinder.isMirrored() && fa.getRepositoryName().equals(ObjectKeys.FI_ADAPTER)) {
			// The file being deleted is a mirrored file.
			// In this case, we delete the entire entry regardless of what else the
			// entry might contain, since we don't want to leave an entry that no 
			// longer mirrors any source file 
			FolderUtils.deleteMirroredEntry((Folder)parentBinder, entry, true, null);
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
				if(predelete) {
					TrashHelper.preDeleteEntry(bs, parentBinder.getId(), entry.getId());
				} else {
					getFolderModule().deleteEntry(parentBinder.getId(), entry.getId(), true, null);
				}
			}
		}

	}
	
	static void inheritAll(Long folderId) {
        BinderUtils.inheritAll(folderId);
	}

    public static Folder getTopMostMirroredFolder(Folder folder) {
        Folder top = folder;
        Binder parent;
        while(true) {
            parent = top.getParentBinder();
            if(parent == null) break;
            if(!parent.isMirrored()) break;
            if(!parent.getResourceDriverName().equals(top.getResourceDriverName())) break;
            top = (Folder) parent;
        }
        return top;
    }

    public static Long getNetFolderOwnerId(Folder folder) {
    	// TODO $$$$$ This method is needed only for cloud folders. So let's short circuit the 
    	// implementation to minimize overhead since we don't want to slow down NCP/CIFS net folders.
    	
    	return folder.getOwnerId();
    	
    	/*
    	Folder netFolder = getTopMostMirroredFolder(folder);
    	if(netFolder != null)
    		return netFolder.getOwnerId();
    	else
    		return null;
    	*/
    }

    public static FileAttachment moveLibraryFile(FileAttachment fa, Folder destFolder, String name) throws WriteFilesException, WriteEntryDataException {
        if (name==null) {
            name = fa.getFileItem().getName();
        }
        String id = fa.getId();
        DefinableEntity owningEntity = fa.getOwner().getEntity();
        if(EntityType.folderEntry == owningEntity.getEntityType()) {
            FolderEntry entry = (FolderEntry) owningEntity;
            Long destFolderId = destFolder.getId();
            if (entry.getParentFolder().getId().equals(destFolderId)) {
                // The file is being moved within the same parent folder.
                if (fa.getFileItem().getName().equals(name)) {
                    // The target name is the same as the source name. Well, this means renaming to the same name.
                    return fa;
                } else { // Rename it in the current folder.
                    // Make sure that the folder doesn't already contain a file with the new name.
                    if (getFolderModule().getLibraryFolderEntryByFileName(entry.getParentFolder(), name) == null) {
                        // Rename the file
                        renameFile(entry, fa, name);
                    } else {
                        throw new FileExistsException(name);
                    }
                }
            } else { // The file is being moved to another folder.
                // Make sure that the destination is a library folder.
                if (destFolder.isLibrary()) {
                    // Make sure that the destination folder doesn't already contain a file with the new name.
                    if (getFolderModule().getLibraryFolderEntryByFileName(destFolder, name) == null) {
                        if (entry.getFileAttachmentsCount() > 1) {
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
                            throw new IllegalArgumentException("Can not move file '" + id + "' because the enclosing entry represents more than one file");
                        } else {
                            // This is the only file contained in the entry, therefore, we can safely move the entire entry.
                            HashMap options = new HashMap();
                            options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, name);
                            entry = getFolderModule().moveEntry(entry.getParentBinder().getId(), entry.getId(), destFolderId, new String[]{name}, options);
                            id = entry.getPrimaryFileAttachmentId();
                        }
                    } else {
                        throw new FileExistsException(name);
                    }
                } else {
                    // The destination folder is not a library folder.
                    throw new IllegalArgumentException("Can not move file '" + id + "' because the destination folder '" + destFolder.getId() + "' is not a library folder");
                }
            }
        }
        return getFileModule().getFileAttachmentById(id);
    }

    private static void renameFile(FolderEntry entry, FileAttachment fa, String newName) throws WriteFilesException, WriteEntryDataException {
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

        getFolderModule().modifyEntry(entry.getParentBinder().getId(),
                entry.getId(), inputData, null, null, renamesTo, null);
    }

    private static FolderModule getFolderModule() {
		return (FolderModule) SpringContextUtil.getBean("folderModule");
	}
    private static FileModule getFileModule() {
		return (FileModule) SpringContextUtil.getBean("fileModule");
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
	private static TemplateModule getTemplateModule() {
		return (TemplateModule) SpringContextUtil.getBean("templateModule");
	}
}
