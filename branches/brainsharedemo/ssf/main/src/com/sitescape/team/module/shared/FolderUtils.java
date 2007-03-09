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
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.DatedMultipartFile;

public class FolderUtils {

	private static final String[] ITEM_NAMES = {"attachFiles", "graphic", "file", "profileEntryPicture"};
	
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
	public static void createFolderEntry(AllBusinessServicesInjected bs,
			Folder folder, String fileName, InputStream content, Date modDate)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderEntryDefinition(bs, folder);
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
		
		bs.getFolderModule().addEntry(folder.getId(), def.getId(), new MapInputData(data), fileItems);
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
	public static void modifyFolderEntry(AllBusinessServicesInjected bs,
			FolderEntry entry, String fileName, InputStream content, Date modDate) 
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Folder folder = entry.getParentFolder();
		
		Definition def = getFolderEntryDefinition(bs, folder);
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

		bs.getFolderModule().modifyEntry(folder.getId(), entry.getId(), 
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
	public static Long createLibraryFolder(AllBusinessServicesInjected bs,
			Binder parentBinder, String folderName)
	throws ConfigurationException, AccessControlException, WriteFilesException {
		Definition def = getFolderDefinition(bs, parentBinder);
		if(def == null)
			throw new ConfigurationException("There is no folder definition to use");
		
		Map data = new HashMap(); // Input data
		// Title field, not name, is used as the name of the folder. Weird...
		data.put("title", folderName); 
		//data.put("description", "This folder was created through WebDAV");
		data.put("library", Boolean.TRUE.toString());
		
		if(parentBinder instanceof Workspace)
			return bs.getWorkspaceModule().addFolder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), new HashMap());
		else
			return bs.getFolderModule().addFolder(parentBinder.getId(), def.getId(), 
					new MapInputData(data), new HashMap());
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

	private static Definition getFolderEntryDefinition
		(AllBusinessServicesInjected bs, Folder folder) {
		Definition def = folder.getDefaultEntryDef();
		if(def == null)
			def = getZoneWideDefaultFolderEntryDefinition(bs);
		return def;
	}
	
	private static Definition getZoneWideDefaultFolderEntryDefinition
	(AllBusinessServicesInjected bs) {
		List defs = bs.getDefinitionModule().getDefinitions(Definition.FOLDER_ENTRY);
		if(defs != null)
			return (Definition) defs.get(0);
		else
			return null;
	}
	
	private static Definition getFolderDefinition(AllBusinessServicesInjected bs,
			Binder parentBinder) {
		if(parentBinder instanceof Folder) {
			// If the parent binder in which to create a new library folder
			// happens to be a folder itself, simply re-use the folder
			// definition of the parent. That is, make the sub-directory
			// the same type as its parent.
			return parentBinder.getEntryDef();
		}
		else {
			// The binder must be a workspace.
			return getZoneWideDefaultFolderDefinition(bs);
		}
	}
	
	private static Definition getZoneWideDefaultFolderDefinition
	(AllBusinessServicesInjected bs) {
		List defs = bs.getDefinitionModule().getDefinitions(Definition.FOLDER_VIEW);
		if(defs != null)
			return (Definition) defs.get(0);
		else
			return null;
	}
		
}
