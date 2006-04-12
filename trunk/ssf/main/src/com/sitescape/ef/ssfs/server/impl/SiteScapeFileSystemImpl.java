package com.sitescape.ef.ssfs.server.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.server.SiteScapeFileSystem;

public class SiteScapeFileSystemImpl implements SiteScapeFileSystem {

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

	
	public boolean objectExists(Map uri) throws NoAccessException {
		if(isInternal(uri)) {
			try {
				// Check folder representing binder id
				Long binderId = getBinderId(uri);
				if(binderId == null)
					return true; // no more checking to do
				
				Binder binder = getBinderModule().getBinder(binderId);
				
				// Check folder representing entry id
				Long entryId = getEntryId(uri);
				if(entryId == null)
					return true; // no more checking to do
				
				Entry entry = null;
				
				if(binder instanceof Folder)
					entry = getFolderModule().getEntry(binderId, entryId);
				else
					entry = getProfileModule().getEntry(binderId, entryId);
				
				// Check folder(s) representing definition item. 
				String itemType = getItemType(uri);
				if(itemType == null)
					return true; // no more checking to do

				Document def = entry.getEntryDef().getDefinition();
				if(def == null) // No definition - Is this actually possible?
					return false; // No item type can be recognized
				
				String defItemType = toDefItemType(itemType);
				if(defItemType == null)
					return false; // Unrecognized item type
				
				Element root = def.getRootElement();
				List items = root.selectNodes("//item[@name='" + defItemType + "' and @type='data']");
				if(items.size() == 0)
					return false; // The item does not exist in the definition.
				
				// If relevent, check folder representing definition element.
				if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_FILE) ||
						itemType.equals(CrossContextConstants.URI_ITEM_TYPE_GRAPHIC)) {
					// File or graphic type items allows multiples. 
					String elemName = getElemName(uri);
					if(elemName == null)
						return true; // no more checking to do
					
					boolean matchFound = false;
					Iterator itItems = items.listIterator();
					while(itItems.hasNext()) {
						Element item = (Element) itItems.next();
						Element nameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
						if(nameProperty != null) {
							String nameValue = nameProperty.attributeValue("value");
							if(nameValue != null && nameValue.equals(elemName)) {
								// Match found
								matchFound = true;
							}
						}
					}
					if(!matchFound)
						return false;
				}
				
				// Check file and repository
				if(itemType.equals(CrossContextConstants.URI_ITEM_TYPE_ATTACH)) {
					// Use file attachment objects directly
					
					// Check repository - If there exists at least one file attachment
					// with the specified repository name, the repository is considered
					// existing. This behavior is different from what we saw above
					// regarding definition items/elements. That is, we allow definition
					// items/elements to exist on their own even when there is no data
					// file associated with them (yet). 
					String reposName = getReposName(uri);
					if(reposName == null)
						return true; // no more checking to do
					
					List fatts = entry.getFileAttachments(reposName);
					if(fatts.size() == 0)
						return false; // No file attachment with the repository name
					
					// 
				}
				else {
					// Use custom attribute object
				
				}
				return false; // TBR
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

	public void createResource(Map uri) throws NoAccessException, AlreadyExistsException {
		// TODO Auto-generated method stub
		
	}

	public void setResource(Map uri, InputStream content) throws NoAccessException, NoSuchObjectException {
		// TODO Auto-generated method stub
		
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
