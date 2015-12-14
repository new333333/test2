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
package org.kablink.teaming.ssfs.server.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.FileTypeMap;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.LockIdMismatchException;
import org.kablink.teaming.module.file.LockedByAnotherUserException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.profile.index.ProfileIndexUtils;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.ssfs.AlreadyExistsException;
import org.kablink.teaming.ssfs.CrossContextConstants;
import org.kablink.teaming.ssfs.LockException;
import org.kablink.teaming.ssfs.NoAccessException;
import org.kablink.teaming.ssfs.NoSuchObjectException;
import org.kablink.teaming.ssfs.TypeMismatchException;
import org.kablink.teaming.ssfs.server.KablinkFileSystem;
import org.kablink.teaming.ssfs.server.KablinkFileSystemException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.ExtendedMultipartFile;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;


public class KablinkFileSystemInternal implements KablinkFileSystem {

	private static final String BINDER = "b";
	private static final String ENTRY = "e";
	private static final String DEFINITION = "d";
	private static final String FILE_ATTACHMENT = "fa";
	private static final String ELEMENT_NAME = "en";

	private AllModulesInjected bs;
	private FileTypeMap mimeTypes;
	private CoreDao coreDao;
	
	KablinkFileSystemInternal(AllModulesInjected bs) {
		this.bs = bs;
	}
	
	protected FileTypeMap getMimeTypes() {
		return this.mimeTypes;
	}
	public void setMimeTypes(FileTypeMap mimeTypes) {
		this.mimeTypes = mimeTypes;
	}

	protected CoreDao getCoreDao() {
		return coreDao;
	}

	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	public void createResource(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		Map objMap = new HashMap();
		if(objectExists(uri, objMap))
			throw new AlreadyExistsException("The resource already exists");

		// Write the file with empty content.
		writeResourceInternal(uri, objMap, new ByteArrayInputStream(new byte[0]));
	}

	public void setResource(Map uri, InputStream content) 
	throws NoAccessException, NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");
		
		// Write the file with the specified content. 
		writeResourceInternal(uri, objMap, content);
	}

	public void createAndSetResource(Map uri, InputStream content) 
	throws NoAccessException, AlreadyExistsException, TypeMismatchException {
		Map objMap = new HashMap();
		if(objectExists(uri, objMap))
			throw new AlreadyExistsException("The resource already exists");

		// Write the file with the specified content.
		writeResourceInternal(uri, objMap, content);
	}
	
	public void createDirectory(Map uri) throws NoAccessException, 
	AlreadyExistsException, TypeMismatchException {
		throw new UnsupportedOperationException("(createDirectory)");
	}
	
	public InputStream getResource(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);

		// Because objectExists always performs "read" access check for the
		// user, we can safely request the file module for the content of
		// the file. 
		return bs.getFileModule().readFile((Binder) objMap.get(BINDER), 
				(Entry) objMap.get(ENTRY), fa);
	}

	/*
	public long getResourceLength(Map uri) throws NoAccessException, 
	NoSuchObjectException, TypeMismatchException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		
		return fa.getFileItem().getLength();
	}*/

	public void removeObject(Map uri) throws NoAccessException, NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		removeResourceInternal(uri, objMap);
	}

	/*
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
	}*/

	public String[] getChildrenNames(Map uri) throws NoAccessException, 
	NoSuchObjectException {
		Map objMap = new HashMap();
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");
		
		return getChildrenNamesInternal(uri, objMap);
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
			props.put(CrossContextConstants.OBJECT_INFO, CrossContextConstants.OBJECT_INFO_FILE);
			
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
			
			return props;
		}
		else { // This is a folder
			props.put(CrossContextConstants.OBJECT_INFO, CrossContextConstants.OBJECT_INFO_DIRECTORY);			
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
			Date lockExpirationDate, String lockOwnerInfo)
	throws NoAccessException, NoSuchObjectException, LockException,
	TypeMismatchException {
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
		
		try {
			bs.getFileModule().lock(((Binder) objMap.get(BINDER)), entry, 
				((FileAttachment) objMap.get(FILE_ATTACHMENT)), 
				lockId, lockSubject, lockExpirationDate, lockOwnerInfo);
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
		if(!objectExists(uri, objMap))
			throw new NoSuchObjectException("The resource does not exist");

		Entry entry = (Entry) objMap.get(ENTRY);

		// Check if the user has right to modify the entry
		
		// Note: It is possible that the owner of the lock has lost the modify right on the entry
		// since she locked the file successfully. It can happen by someone else changing the
		// access control on the entry or by a workflow, etc. In such case, we want the lock
		// owner to be able to graciously release the lock as long as the lock matches, even if 
		// she may no longer have the modify right on the entry itself (see bug 584878).
		// As a side effect of unlocking the file, if she had managed to get any updated content
		// of the file into the system before she lost the modify right on the entry, the updated
		// content will be committed to the system creating a new version of the file. 
		/*
		try {
			AccessUtils.modifyCheck(entry);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		*/
		
		bs.getFileModule().unlock(((Binder) objMap.get(BINDER)), entry, 
				((FileAttachment) objMap.get(FILE_ATTACHMENT)), 
				lockId);
	}

	public void copyObject(Map sourceUri, Map targetUri, boolean overwrite, 
			boolean recursive) throws  
			NoAccessException, NoSuchObjectException, AlreadyExistsException, 
			TypeMismatchException {
		throw new UnsupportedOperationException("(copyObject) " + getOriginal(sourceUri) + " to " + getOriginal(targetUri));
	}
	
	public void moveObject(Map sourceUri, Map targetUri, boolean overwrite) 
	throws NoAccessException, NoSuchObjectException, 
	AlreadyExistsException, TypeMismatchException {
		throw new UnsupportedOperationException("(moveObject) " + getOriginal(sourceUri) + " to " + getOriginal(targetUri));
	}
	
	private void removeResourceInternal(Map uri, Map objMap) throws NoAccessException {
		FileAttachment fa = (FileAttachment) objMap.get(FILE_ATTACHMENT);
		
		List faId = new ArrayList();
		faId.add(fa.getId());
		
		try {
			bs.getFolderModule().modifyEntry(getBinderId(uri), getEntryId(uri), new EmptyInputData(), null, faId, null, null);
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());						
		} catch (WriteFilesException e) {
			throw new KablinkFileSystemException(e.getLocalizedMessage());
		} catch (WriteEntryDataException e) {
			throw new KablinkFileSystemException(e.getLocalizedMessage());
		}
	}
	
	private String[] getChildrenNamesInternal(Map uri, Map objMap)
			throws NoAccessException, NoSuchObjectException {
		Binder binder = (Binder) objMap.get(BINDER);
		if (binder == null) {
			// Get a list binders (all folders)
			Map options = new HashMap();
			//get them all
			options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1);
			Document searchFilter = DocumentHelper.createDocument();
			Element field = searchFilter.addElement(Constants.FIELD_ELEMENT);
			field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.DOC_TYPE_FIELD);
			Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
			child.setText(Constants.DOC_TYPE_BINDER);
			
	    	options.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter);
			
			Map searchResults = bs.getBinderModule().executeSearchQuery(null, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, options);
			List<Map> groups = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);
			List<String> folderIds = new ArrayList();
			for (Map groupMap: groups) {
				String fId = (String)groupMap.get(Constants.DOCID_FIELD);
				if (Validator.isNotNull(fId)) folderIds.add(fId);
				
			}
			return folderIds.toArray(new String[folderIds.size()]);
		}

		Set<String> children = new HashSet<String>();

		Entry entry = (Entry) objMap.get(ENTRY);
		if (entry == null) {
			// Get a list of entries if the binder represents a folder
			if(EntityType.folder.equals(binder.getEntityType())) {
				Map options = new HashMap();
				options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE);
				Map folderEntries = bs.getFolderModule().getEntries(
						binder.getId(), options);
				List entries = (ArrayList) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
				for (int i = 0; i < entries.size(); i++) {
					Map ent = (Map) entries.get(i);
					String entryIdString = (String) ent
							.get(Constants.DOCID_FIELD);
					if (Validator.isNotNull(entryIdString))
						children.add(entryIdString);
				}
			}
			return children.toArray(new String[children.size()]);
		}

		String itemType = getItemType(uri);
		if (itemType == null) {
			// Get a list of relevent item types from the definition
			Document def = entry.getEntryDefDoc();
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
				Iterator it = entry.getFileAttachments().iterator();
				while (it.hasNext()) {
					FileAttachment fa = (FileAttachment) it.next();
					children.add(fa.getRepositoryName());
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

	private String getOriginal(Map uri) {
		return (String) uri.get(CrossContextConstants.URI_ORIGINAL);		
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
		ExtendedMultipartFile mf = new ExtendedMultipartFile(getFilePath(uri), in);
		
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

		// For some reason, the modifyEntry() doesn't seem to check access right correctly when a 
		// workflow is involved (bug 584878). To work around that, explicitly check if the user
		// has the right to modify the entry before invoking the method.
		
		Entry entry = (Entry) objMap.get(ENTRY);
		try {
			AccessUtils.modifyCheck(entry);
		}
		catch(AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());
		}
		
		try {
			bs.getFolderModule().modifyEntry(getBinderId(uri), getEntryId(uri), inputData, fileItems, null, null, null);
		} catch (AccessControlException e) {
			throw new NoAccessException(e.getLocalizedMessage());			
		} catch (WriteFilesException e) {
			throw new KablinkFileSystemException(e.getLocalizedMessage(), true);
		} catch (WriteEntryDataException e) {
			throw new KablinkFileSystemException(e.getLocalizedMessage(), true);
		}		
	}
	
	
	private boolean objectExists(Map uri, Map objMap) throws NoAccessException {
		try {
			// Check folder representing binder id
			Long binderId = getBinderId(uri);
			if (binderId == null)
				return true; // no more checking to do

			Binder binder = bs.getBinderModule().getBinder(binderId);
			objMap.put(BINDER, binder);

			// Check folder representing entry id
			Long entryId = getEntryId(uri);
			if (entryId == null)
				return true; // no more checking to do

			Entry entry = null;

			if (binder instanceof Folder)
				entry = bs.getFolderModule().getEntry(binderId, entryId);
			else
				entry = bs.getProfileModule().getEntry(entryId);
			objMap.put(ENTRY, entry);

			// Check folder(s) representing definition item. 
			String itemType = getItemType(uri);
			if (itemType == null)
				return true; // no more checking to do

			Document def = entry.getEntryDefDoc();
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
//				FileAttachment fa = entry.getFileAttachment(reposName, filePath);
				FileAttachment fa = entry.getFileAttachment(filePath);
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
}
