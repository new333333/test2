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
import java.util.ArrayList;
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
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.DatedMultipartFile;
import com.sitescape.team.util.SpringContextUtil;

public class FolderUtils {

	private static final String[] ITEM_NAMES = {"attachFiles", "graphic", "file", "profileEntryPicture"};
	
	private static final String EXTERNAL_FILE_ITEM_NAME = "_externalFile";
	
	/**
	 * Creates a new folder entry with an attachment file.
	 * 
	 * @param bs
	 * @param folder 
	 * @param fileName file name
	 * @param content file content
	 * @param modDate (optional) file mod date or <code>null</code> 
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public static Long createFolderEntry(Folder folder, String fileName, InputStream content, Date modDate)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("There is no folder entry definition to use");
		
		String elementName = getDefaultElementName(def);
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null)
			mf = new DatedMultipartFile(fileName, content, modDate);
		else
			mf = new SimpleMultipartFile(fileName, content); 
		
		Map fileItems = new HashMap(); // Map of element names to file items	
		fileItems.put(elementName, mf); // single file item
		
		Map data = new HashMap(); // Input data
		data.put("title", fileName);
		
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			data.put("_lastModifiedDate", modDate);
		}
		
		return getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems);
	}
	
	/**
	 * Modifies an existing folder entry with new file content for the
	 * attachment.
	 * 
	 * @param bs
	 * @param entry 
	 * @param fileName file name
	 * @param content file content
	 * @param modDate (optional) file mod date or <code>null</code> 
	 * @throws ConfigurationException
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public static void modifyFolderEntry(FolderEntry entry, String fileName, 
			InputStream content, Date modDate) 
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Folder folder = entry.getParentFolder();
		
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("There is no folder entry definition to use");
		
		String elementName = getDefaultElementName(def);
		
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
			data.put("_lastModifiedDate", modDate);
			inputData = new MapInputData(data);
		}
		else {
			inputData = new EmptyInputData(); // No non-file input data
		}

		getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
				inputData, fileItems, null, null);
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
		data.put("title", folderName); 
		//data.put("description", "This folder was created through WebDAV");
		data.put("library", Boolean.TRUE.toString());
		
		if(parentBinder instanceof Workspace)
			return getWorkspaceModule().addFolder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), new HashMap());
		else
			return getFolderModule().addFolder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), new HashMap());
	}
	
	public static Long createMirroredEntry(Folder folder, String fileName, 
			InputStream content, Date modDate, boolean synchToSource)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderEntryDefinition(folder);
		if(def == null)
			throw new ConfigurationException("There is no folder entry definition to use");
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null)
			mf = new DatedMultipartFile(fileName, content, modDate);
		else
			mf = new SimpleMultipartFile(fileName, content); 
		
		Map fileItems = new HashMap(); // Map of element names to file items	
		fileItems.put(EXTERNAL_FILE_ITEM_NAME, mf); // single file item
		
		Map data = new HashMap(); // Input data
		data.put("title", fileName);
		data.put(ObjectKeys.SYNCH_TO_SOURCE, Boolean.valueOf(synchToSource));
		
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			data.put("_lastModifiedDate", modDate);
		}
		
		return getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems);
	}
	
	public static void modifyMirroredEntry(FolderEntry entry, String fileName, 
			InputStream content, Date modDate, boolean synchToSource) 
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Folder folder = entry.getParentFolder();
		
		// Wrap the input stream in a datastructure suitable for our business module.
		MultipartFile mf;
		if(modDate != null)
			mf = new DatedMultipartFile(fileName, content, modDate);
		else
			mf = new SimpleMultipartFile(fileName, content); 
		
		Map fileItems = new HashMap(); // Map of names to file items	
		fileItems.put(EXTERNAL_FILE_ITEM_NAME, mf); // single file item
		
		Map data = new HashMap(); // Input data
		data.put(ObjectKeys.SYNCH_TO_SOURCE, Boolean.valueOf(synchToSource));
		
		if(modDate != null) {
			// We need to tell the system to use this client-supplied mod date
			// for the newly created entry (instead of current time). 
			data.put("_lastModifiedDate", modDate);
		}

		getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
				new MapInputData(data), fileItems, null, null);
	}

	public static Long createMirroredFolder(Binder parentBinder, String folderName, 
			String resourceDriverName, String resourcePath, boolean synchToSource)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderDefinition(parentBinder);
		if(def == null)
			throw new ConfigurationException("There is no folder definition to use");
		
		Map<String,Object> data = new HashMap<String,Object>(); // Input data
		data.put("title", folderName); 
		data.put("library", Boolean.TRUE.toString());
		data.put(ObjectKeys.MIRRORED, Boolean.TRUE);
		data.put(ObjectKeys.RESOURCE_DRIVER_NAME, resourceDriverName);
		data.put(ObjectKeys.RESOURCE_PATH, resourcePath);
		data.put(ObjectKeys.SYNCH_TO_SOURCE, Boolean.valueOf(synchToSource));
		
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
	
	public static void deleteMirroredEntry(Folder parentFolder, FolderEntry entry) {
		getFolderModule().deleteEntry(parentFolder.getId(), entry.getId());
	}
	
	public static boolean isMirroredFolder(Binder binder) {
		return ((binder instanceof Folder) && ((Folder) binder).isMirrored());
	}
	
	//Routine to get the name of the element that will store the uploaded file
	//  This routine searches the definition looking for the first file element
	private static String getDefaultElementName(Definition definition) {
		Document defDoc = definition.getDefinition();
		Element root = defDoc.getRootElement();
		Element formItem = (Element) root.selectSingleNode("//item[@type='form']");
		Element item = null;
		List fileItems = new ArrayList();
		for (int i = 0; i < ITEM_NAMES.length; i++) fileItems.add(ITEM_NAMES[i]);
		Iterator itItems = formItem.selectNodes("//item").iterator();
		while (itItems.hasNext()) {
			//Look for the first file element
			Element itemEle = (Element) itItems.next();
			if (fileItems.contains(itemEle.attributeValue("name"))) {
				item = itemEle;
				break;
			}
		}
		Element nameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
		String elementName = nameProperty.attributeValue("value");
		
		if (item.attributeValue("name").equals("attachFiles")) {
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
