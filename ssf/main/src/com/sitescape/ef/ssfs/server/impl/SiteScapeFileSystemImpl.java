package com.sitescape.ef.ssfs.server.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.EmptyInputData;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystem;

public class SiteScapeFileSystemImpl implements SiteScapeFileSystem {

	private static final String BINDER = "b";
	private static final String ENTRY = "e";
	private static final String DEFINITION = "d";
	private static final String CUSTOM_ATTRIBUTE = "ca";
	private static final String FILE_ATTACHMENT = "fa";
	private static final String ELEMENT_NAME = "en";

	protected final Log logger = LogFactory.getLog(getClass());

	private FolderModule folderModule;
	private DefinitionModule definitionModule;
	private BinderModule binderModule;
	private ProfileModule profileModule;
	private FileModule fileModule;

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

	private boolean objectExists(Map uri, Map objMap) throws NoAccessException {
		if(isInternal(uri)) {
			try {
				// Check folder representing binder id
				Long binderId = getBinderId(uri);
				if(binderId == null)
					return true; // no more checking to do
				
				Binder binder = getBinderModule().getBinder(binderId);
				objMap.put(BINDER, binder);
				
				// Check folder representing entry id
				Long entryId = getEntryId(uri);
				if(entryId == null)
					return true; // no more checking to do
				
				Entry entry = null;
				
				if(binder instanceof Folder)
					entry = getFolderModule().getEntry(binderId, entryId);
				else
					entry = getProfileModule().getEntry(binderId, entryId);
				objMap.put(ENTRY, entry);
				
				// Check folder(s) representing definition item. 
				String itemType = getItemType(uri);
				if(itemType == null)
					return true; // no more checking to do

				Document def = entry.getEntryDef().getDefinition();
				if(def == null) // No definition - Is this actually possible?
					return false; // No item type can be recognized
				objMap.put(DEFINITION, def);
				
				// The following call validates the item type (as a side effect).
				String defItemType = toDefItemType(itemType);
				if(defItemType == null)
					return false; // Unrecognized item type
				
				Element root = def.getRootElement();
				List items = root.selectNodes("//item[@name='" + defItemType + "' and @type='data']");
				if(items.size() == 0)
					return false; // The item does not exist in the definition.
				
				String elementName = null;
				String reposName = null;
				
				// Check definition.
				if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_FILE) ||
						itemType.equals(CrossContextConstants.URI_ITEM_TYPE_GRAPHIC)) {
					// Check folder representing definition element - File or 
					// graphic type items allows multiples.
					
					elementName = getElemName(uri);
					if(elementName == null)
						return true; // no more checking to do
					
					boolean matchFound = false;
					Iterator itItems = items.listIterator();
					while(itItems.hasNext()) {
						Element item = (Element) itItems.next();
						Element nameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
						if(nameProperty != null) {
							String nameValue = nameProperty.attributeValue("value");
							if(nameValue != null && nameValue.equals(elementName)) {
								// Match found
								matchFound = true;
								objMap.put(ELEMENT_NAME, elementName);
								break;
							}
						}
					}
					if(!matchFound)
						return false;
				}
				else if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
					// Check repository name.
					 
					reposName = getReposName(uri);
					if(reposName == null)
						return true; // no more checking to do
					
					Element attachFilesItem = (Element) items.get(0); // only one item in there
					Element nameProperty = (Element) attachFilesItem.selectSingleNode("./properties/property[@name='name']");
					elementName = nameProperty.attributeValue("value");
					objMap.put(ELEMENT_NAME, elementName);
					Element optionElem = (Element) attachFilesItem.selectSingleNode("./properties/property[@name='storage']/option[@name='" + reposName + "']");
					if(optionElem == null)
						return false; // The repository name does not appear in the definition.
				}
				else { // primary
					Element primaryItem = (Element) items.get(0); // only one item in there
					Element nameProperty = (Element) primaryItem.selectSingleNode("./properties/property[@name='name']");
					elementName = nameProperty.attributeValue("value");
					objMap.put(ELEMENT_NAME, elementName);
				}
				
				// Finally check file itself. 
				String fileName = getFileName(uri);
				if(fileName == null)
					return true; // no more checking to do
				
				if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
					// Use FileAttachment directly
					FileAttachment fa = entry.getFileAttachment(reposName, fileName);
					if(fa == null)
						return false; // No matching file
					else {
						objMap.put(FILE_ATTACHMENT, fa);
						return true; // Match found
					}
				}
				else {
					// Use CustomAttribute
					CustomAttribute ca = entry.getCustomAttribute(elementName);
					if(ca == null) {
						// This means that no file has ever been uploaded through
						// this element yet (that is, definition exists, but 
						// data/file doesn't yet). 
						return false; // No file exists for the element. 
					}
					else {
						objMap.put(CUSTOM_ATTRIBUTE, ca);
						// Since all file attachments in this custom attribute
						// have the same value for repository name, we only
						// need to use file name for comparison. 
						Iterator it = ((Set) ca.getValue()).iterator();
						while(it.hasNext()) {
							FileAttachment fa = (FileAttachment) it.next();
							if(fa.getFileItem().getName().equals(fileName)) {
								objMap.put(FILE_ATTACHMENT, fa);
								return true; // File name matches
							}
						}
						return false; // File name didn't match
					}
				}
			}
			catch(NoBinderByTheIdException e) {
				return false;
			}
			catch(NoFolderByTheIdException e) {
				return false;
			}
			catch(AccessControlException e) {
				throw new NoAccessException(e.getLocalizedMessage());
			}
		}
		else { // library is not supported yet
			throw new UnsupportedOperationException();
		}		
	}
	
	public boolean objectExists(Map uri) throws NoAccessException {
		Map objMap = new HashMap();
		return objectExists(uri, objMap);
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
	private void writeResource(Map uri, Map objMap, InputStream in) {
		// Wrap the input stream in a datastructure suitable for our business module. 
		SsfsMultipartFile mf = new SsfsMultipartFile(getFileName(uri), in);
		
		Map fileItems = new HashMap(); // Map of names to file items
		InputDataAccessor inputData;   // Input data other than file
		if(getItemType(uri).equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
			fileItems.put(objMap.get(ELEMENT_NAME), mf); // single file item
			inputData = new EmptyInputData(); // no non-file input data
		}
		else {
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

		try {
			getFolderModule().modifyEntry(getBinderId(uri), getEntryId(uri), inputData, fileItems, null);
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} catch (WriteFilesException e) {
			// I don't feel like creating another exception class just for this...
			throw new RuntimeException(e.getMessage());
		}		
	}
	
	public void createResource(Map uri) throws NoAccessException, AlreadyExistsException {
		Map objMap = new HashMap();
		if(objectExists(uri, objMap))
			throw new AlreadyExistsException("The resource already exists");
		
		// Write the file with empty content. 
		writeResource(uri, objMap, new ByteArrayInputStream(new byte[0]));
	}

	public void setResource(Map uri, InputStream content) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");
		
		// Write the file with the specified content. 
		writeResource(uri, objMap, content);
	}

	public InputStream getResource(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public long getResourceLength(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void removeResource(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		
	}

	public Date getLastModified(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getCreationDate(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getChildrenNames(Map uri) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		return null;
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
	
	private String getFileName(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_FILENAME);
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
		if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_PRIMARY)) {
			return "primary";
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
}
