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
import com.sitescape.team.remoting.ws.util.DomInputData;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.util.stringcheck.StringCheckUtil;

public class FolderServiceImpl extends BaseService implements FolderService {

	
	public String getFolderEntriesAsXML(String accessToken, long binderId) {
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

	public String getFolderEntryAsXML(String accessToken, long binderId, long entryId, boolean includeAttachments) {
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
	
	public long addFolderEntry(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);
		
		Document doc = getDocument(inputDataAsXML);
		if(profiler == null) {
			profiler = new SimpleProfiler("webServices");
			count = 0;
		}
		SimpleProfiler.setProfiler(profiler);
		try {
			return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), null).longValue();
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
	
	public void modifyFolderEntry(String accessToken, long binderId, long entryId, String inputDataAsXML) {
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

	public long addReply(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML) {
		inputDataAsXML = StringCheckUtil.check(inputDataAsXML);

		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addReply(new Long(binderId), new Long(parentId), 
				definitionId, new DomInputData(doc, getIcalModule()), null, null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}

	public void uploadFolderFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}
	public void addEntryWorkflow(String accessToken, long binderId, long entryId, String definitionId, String startState) {
		getFolderModule().addEntryWorkflow(binderId, entryId, definitionId, startState);
	}

	public void uploadFolderFileStaged(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String stagedFileRelativePath) {
		boolean enable = SPropsUtil.getBoolean("staging.upload.files.enable", false);
		if(enable) {
			fileUploadDataItemName = StringCheckUtil.check(fileUploadDataItemName);
			stagedFileRelativePath = StringCheckUtil.check(stagedFileRelativePath);
			
			// Get the staged file
			String rootPath = SPropsUtil.getString("staging.upload.files.rootpath", "").trim();
			File file = new File(rootPath, stagedFileRelativePath);
			
			// Wrap it in a datastructure expected by our app.
			SimpleMultipartFile mf = new SimpleMultipartFile(file.getName(), file, false);
			
			// Create a map of file item names to items 
			Map fileItems = new HashMap();
			fileItems.put(fileUploadDataItemName, mf);
			
			try {
				// Finally invoke the business method. 
				getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
					new EmptyInputData(), fileItems, null, null, null);
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
					entryId + ", " + fileUploadDataItemName + ", " + stagedFileRelativePath);
		}
	}
	
}
