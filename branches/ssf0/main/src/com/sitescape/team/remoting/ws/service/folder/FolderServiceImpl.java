/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.remoting.ws.service.folder;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.EmptyInputData;
import com.sitescape.team.remoting.RemotingException;
import com.sitescape.team.remoting.ws.BaseService;
import com.sitescape.team.remoting.ws.model.FolderEntryBrief;
import com.sitescape.team.remoting.ws.model.FolderEntryCollection;
import com.sitescape.team.remoting.ws.util.DomInputData;
import com.sitescape.team.remoting.ws.util.ModelInputData;
import com.sitescape.team.util.DatedMultipartFile;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.util.stringcheck.StringCheckUtil;

public class FolderServiceImpl extends BaseService implements FolderService, FolderServiceInternal {

	
	public String folder_getEntriesAsXML(String accessToken, long binderId) {
		com.sitescape.team.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));

		Document doc = DocumentHelper.createDocument();
		Element folderElement = doc.addElement("folder");
		folderElement.addAttribute("title", binder.getTitle());
		folderElement.addAttribute("id", binder.getId().toString());

			if (binder instanceof Folder) {
			Map options = new HashMap();
			Map folderEntries = getFolderModule().getFullEntries(binder.getId(), options);
			List entrylist = (List)folderEntries.get(ObjectKeys.FULL_ENTRIES);
			Iterator entryIterator = entrylist.listIterator();
			while (entryIterator.hasNext()) {
				FolderEntry entry  = (FolderEntry) entryIterator.next();
				Element entryElem = folderElement.addElement("entry");
				addEntryAttributes(entryElem, entry);
			}
		}
		
		return doc.getRootElement().asXML();
	}

	public String folder_getEntryAsXML(String accessToken, long binderId, long entryId, boolean includeAttachments) {
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		
		// Retrieve the raw entry.
		FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = doc.addElement("entry");

		// Handle structured fields of the entry known at compile time. 
		addEntryAttributes(entryElem, entry);

		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		/*
		System.out.println("*** XML representation for entry " + entry.getId());
		System.out.println(xml);
		System.out.println("*** Pretty XML representation for entry " + entry.getId());
		prettyPrint(doc);
		*/
		
		return xml;
	}
	
	static int count = 0;
	static SimpleProfiler profiler = null;
	
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return new HashMap();
	}
	
	public long folder_addEntryWithXML(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return addFolderEntry(accessToken, binderId, definitionId, inputDataAsXML, attachedFileName, null);
	}
	protected long addFolderEntry(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName, Map options) {

		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		Document doc = getDocument(inputDataAsXML);
		if(profiler == null) {
			profiler = new SimpleProfiler("webServices");
			count = 0;
		}
		SimpleProfiler.setProfiler(profiler);
		try {
			return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		} finally {
			if(++count == 10000) {
				logger.info(SimpleProfiler.toStr());
				profiler = null;
				SimpleProfiler.clearProfiler();
			}
		}
	}
	
	public void folder_modifyEntryWithXML(String accessToken, long binderId, long entryId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
				new DomInputData(doc, getIcalModule()), null, null, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
	}

	public long folder_addReplyWithXML(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return addReply(accessToken, binderId, parentId, definitionId, inputDataAsXML, attachedFileName, null);
	}
	
	protected long addReply(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML, String attachedFileName, Map options) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addReply(new Long(binderId), new Long(parentId), 
				definitionId, new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}

	public void folder_addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId) {
		addEntryWorkflow(accessToken, binderId, entryId, definitionId, null);
	}
	protected void addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId, Map options) {
		getFolderModule().addEntryWorkflow(binderId, entryId, definitionId, options);

	}
	public void folder_modifyWorkflowState(String accessToken, long binderId, long entryId, long stateId, String toState) {
		getFolderModule().modifyWorkflowState(binderId, entryId, stateId, toState);
	}
	public void folder_uploadFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}

	public void folder_uploadFileStaged(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName, String stagedFileRelativePath) {
		uploadFolderFileStaged(accessToken, binderId, entryId, fileUploadDataItemName, fileName, 
				stagedFileRelativePath, null, null, null);
	}
	protected void uploadFolderFileStaged(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName, 
			String stagedFileRelativePath, String modifier, Calendar modificationDate, Map options) {
		boolean enable = SPropsUtil.getBoolean("staging.upload.files.enable", false);
		if(enable) {
			fileUploadDataItemName = StringCheckUtil.check(fileUploadDataItemName);
			stagedFileRelativePath = StringCheckUtil.check(stagedFileRelativePath);
			fileName = StringCheckUtil.check(fileName);
			
			// Get the staged file
			String rootPath = SPropsUtil.getString("staging.upload.files.rootpath", "").trim();
			File file = new File(rootPath, stagedFileRelativePath);
			
			// Wrap it in a datastructure expected by our app.
			DatedMultipartFile mf = new DatedMultipartFile(fileName, file, false, modifier, modificationDate==null?null:modificationDate.getTime());
			
			// Create a map of file item names to items 
			Map fileItems = new HashMap();
			fileItems.put(fileUploadDataItemName, mf);
			
			try {
				// Finally invoke the business method. 
				getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
					new EmptyInputData(), fileItems, null, null, options);
				// If you're here, the transaction was successful. 
				// Check if we need to delete the staged file or not.
				if(SPropsUtil.getBoolean("staging.upload.files.delete.after", false)) {
					file.delete();
				}
			}
			catch(WriteFilesException e) {
				throw new RemotingException(e);
			}
		}
		else {
			throw new UnsupportedOperationException("Staged file upload is disabled: " + binderId + ", " + 
					entryId + ", " + fileUploadDataItemName + ", " + fileName + ", " + stagedFileRelativePath);
		}
	}

	public void folder_synchronizeMirroredFolder(String accessToken, long binderId) {
		getFolderModule().synchronize(binderId, null);
	}

	public com.sitescape.team.remoting.ws.model.FolderEntry folder_getEntry(String accessToken, long binderId, long entryId, boolean includeAttachments) {
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);

		// Retrieve the raw entry.
		FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		com.sitescape.team.remoting.ws.model.FolderEntry entryModel = 
			new com.sitescape.team.remoting.ws.model.FolderEntry(); 

		fillFolderEntryModel(entryModel, entry);
		
		return entryModel;
	}

	public FolderEntryCollection folder_getEntries(String accessToken, long binderId) {
		com.sitescape.team.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));

		List<FolderEntryBrief> entries = new ArrayList<FolderEntryBrief>();

		if (binder instanceof Folder) {
			Map options = new HashMap();
			Map folderEntries = getFolderModule().getFullEntries(binder.getId(), options);
			List entrylist = (List)folderEntries.get(ObjectKeys.FULL_ENTRIES);
			Iterator entryIterator = entrylist.listIterator();
			while (entryIterator.hasNext()) {
				FolderEntry entry  = (FolderEntry) entryIterator.next();
				FolderEntryBrief entryBrief = new FolderEntryBrief();
				entries.add(toFolderEntryBrief(entry));
			}
		}
		
		FolderEntryBrief[] array = new FolderEntryBrief[entries.size()];
		return new FolderEntryCollection(entries.toArray(array));
	}

	public long folder_addEntry(String accessToken, com.sitescape.team.remoting.ws.model.FolderEntry entry, String attachedFileName) {
		return addFolderEntry(accessToken, entry, attachedFileName, null);	
	}
	protected long addFolderEntry(String accessToken, com.sitescape.team.remoting.ws.model.FolderEntry entry, String attachedFileName, Map options) {
		if(profiler == null) {
			profiler = new SimpleProfiler("webServices");
			count = 0;
		}
		SimpleProfiler.setProfiler(profiler);
		try {
			return getFolderModule().addEntry(entry.getParentBinderId(), entry.getDefinitionId(), 
				new ModelInputData(entry), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		} finally {
			if(++count == 10000) {
				logger.info(SimpleProfiler.toStr());
				profiler = null;
				SimpleProfiler.clearProfiler();
			}
		}
	}

	public long folder_addReply(String accessToken, long parentEntryId, com.sitescape.team.remoting.ws.model.FolderEntry reply, String attachedFileName) {
		return addReply(accessToken, parentEntryId, reply, attachedFileName, null);
	}
	protected long addReply(String accessToken, long parentEntryId, com.sitescape.team.remoting.ws.model.FolderEntry reply, String attachedFileName, Map options) {
		try {
			return getFolderModule().addReply(reply.getParentBinderId(), new Long(parentEntryId), 
				reply.getDefinitionId(), new ModelInputData(reply), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}

	public void folder_modifyEntry(String accessToken, com.sitescape.team.remoting.ws.model.FolderEntry entry) {
		try {
			getFolderModule().modifyEntry(entry.getParentBinderId(), entry.getId(), 
				new ModelInputData(entry), null, null, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
	}

	public void folder_deleteEntry(String accessToken, long binderId, long entryId) {
		getFolderModule().deleteEntry(binderId, entryId);
	}
	
}
