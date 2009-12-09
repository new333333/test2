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
package org.kablink.teaming.remoting.ws.service.folder;

import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.eq;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFileByTheNameException;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.modelprocessor.ProcessorManager;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.mail.EmailPoster;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.RemotingException;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.FolderEntryBrief;
import org.kablink.teaming.remoting.ws.model.FolderEntryCollection;
import org.kablink.teaming.remoting.ws.util.DomInputData;
import org.kablink.teaming.remoting.ws.util.ModelInputData;
import org.kablink.teaming.util.DatedMultipartFile;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;

public class FolderServiceImpl extends BaseService implements FolderService, FolderServiceInternal {

	protected ProcessorManager processorManager;
	public void setProcessorManager(ProcessorManager processorManager) {
		this.processorManager = processorManager;
	}

	public void folder_uploadFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}
	@SuppressWarnings("unchecked")
	public void folder_removeFile(String accessToken, long entryId, String fileName) {
		try {
			FolderEntry entry = getFolderModule().getEntry(null, entryId);
			FileAttachment att = entry.getFileAttachment(fileName);
			if (att == null) return;
			List deletes = new ArrayList();
			deletes.add(att.getId());
			getFolderModule().modifyEntry(null, entryId, new EmptyInputData(), null, deletes, null, null);
			
		}	catch(WriteFilesException e) {
			throw new RemotingException(e);
		}	catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}			

	}
	@SuppressWarnings("unchecked")
	public String folder_getEntriesAsXML(String accessToken, long binderId) {
		org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));

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
	
	@SuppressWarnings("unchecked")
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return new HashMap();
	}
	
	public long folder_addEntryWithXML(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return addFolderEntry(accessToken, binderId, definitionId, inputDataAsXML, attachedFileName, null);
	}
	@SuppressWarnings("unchecked")
	protected long addFolderEntry(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName, Map options) {

		Document doc = getDocument(inputDataAsXML);
		if(profiler == null) {
			profiler = new SimpleProfiler("webServices");
			count = 0;
		}
		SimpleProfiler.setProfiler(profiler);
		try {
			return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).getId().longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
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
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
				new DomInputData(doc, getIcalModule()), null, null, null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}			
	}

	public long folder_addReplyWithXML(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return addReply(accessToken, binderId, parentId, definitionId, inputDataAsXML, attachedFileName, null);
	}
	
	@SuppressWarnings("unchecked")
	protected long addReply(String accessToken, long binderId, long parentId, String definitionId, String inputDataAsXML, String attachedFileName, Map options) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addReply(new Long(binderId), new Long(parentId), 
				definitionId, new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).getId().longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	public void folder_addEntryWorkflow(String accessToken, long entryId, String definitionId) {
		getFolderModule().addEntryWorkflow(null, entryId, definitionId, null);
	}
   public void folder_deleteEntryWorkflow(String accessToken, long entryId, String definitionId) {
    	getFolderModule().deleteEntryWorkflow(null, entryId, definitionId);
    }
	public void folder_modifyWorkflowState(String accessToken, long entryId, long stateId, String toState) {
		getFolderModule().modifyWorkflowState(null, entryId, stateId, toState);
	}
	@SuppressWarnings("unchecked")
	public void folder_setWorkflowResponse(String accessToken, long entryId, long stateId, String question, String response) {
		Map params = new HashMap();
		response = StringCheckUtil.check(response);
		question = StringCheckUtil.check(question);
		params.put(question, response);
		getFolderModule().setWorkflowResponse(null, entryId, stateId, new MapInputData(params));
	}
	public void folder_uploadFile(String accessToken, long entryId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}

	public void folder_uploadFileStaged(String accessToken, long entryId, String fileUploadDataItemName, String fileName, String stagedFileRelativePath) {
		uploadFolderFileStaged(accessToken, null, entryId, fileUploadDataItemName, fileName, 
				stagedFileRelativePath, null, null, null);
	}
	@SuppressWarnings("unchecked")
	protected void uploadFolderFileStaged(String accessToken, Long binderId, Long entryId, String fileUploadDataItemName, String fileName, 
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
				getFolderModule().modifyEntry(binderId, entryId, 
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
			catch(WriteEntryDataException e) {
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

	public org.kablink.teaming.remoting.ws.model.FolderEntry folder_getEntryByFileName(String accessToken, long binderId, String fileName, boolean includeAttachments, boolean eventAsIcalString) {
		// Retrieve the raw entry.
		FolderEntry entry = 
			getFolderModule().getLibraryFolderEntryByFileName(getFolderModule().getFolder(binderId), fileName);

		org.kablink.teaming.remoting.ws.model.FolderEntry entryModel = 
			new org.kablink.teaming.remoting.ws.model.FolderEntry(); 
		
		entryModel.setEventAsIcalString(eventAsIcalString);

		fillFolderEntryModel(entryModel, entry);
		
		return entryModel;
		
	}
	public org.kablink.teaming.remoting.ws.model.FolderEntry folder_getEntry(String accessToken, long entryId, boolean includeAttachments, boolean eventAsIcalString) {
		// Retrieve the raw entry.
		FolderEntry entry = 
			getFolderModule().getEntry(null, entryId);

		org.kablink.teaming.remoting.ws.model.FolderEntry entryModel = 
			new org.kablink.teaming.remoting.ws.model.FolderEntry(); 

		entryModel.setEventAsIcalString(eventAsIcalString);

		fillFolderEntryModel(entryModel, entry);
		
		return entryModel;
	}

	@SuppressWarnings("unchecked")
	public FolderEntryCollection folder_getEntries(String accessToken, long binderId, int firstRecord, int maxRecords) {
		org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));

		List<FolderEntryBrief> entries = new ArrayList<FolderEntryBrief>();
		int total = 0;

		if (binder instanceof Folder) {
			Map options = new HashMap();
	    	options.put(ObjectKeys.SEARCH_OFFSET, new Integer(firstRecord));
	    	options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(maxRecords));
			Map folderEntries = getFolderModule().getFullEntries(binder.getId(), options);
			total = ((Integer)folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
			List entrylist = (List)folderEntries.get(ObjectKeys.FULL_ENTRIES);
			Iterator entryIterator = entrylist.listIterator();
			while (entryIterator.hasNext()) {
				FolderEntry entry  = (FolderEntry) entryIterator.next();
				entries.add(toFolderEntryBrief(entry));
			}
		}
		FolderEntryBrief[] array = new FolderEntryBrief[entries.size()];
		return new FolderEntryCollection(firstRecord, total, entries.toArray(array));
	}

	@SuppressWarnings("unchecked")
	public long folder_addEntry(String accessToken, org.kablink.teaming.remoting.ws.model.FolderEntry entry, String attachedFileName) {
		HashMap options = new HashMap();
 		getTimestamps(options, entry);
		return addFolderEntry(accessToken, entry, attachedFileName, options);	
	}
	@SuppressWarnings("unchecked")
	public void folder_addEntryAsMime(String accessToken, long binderId, byte[] mimeData) {
		try {
			Session session = Session.getDefaultInstance(new Properties());
			org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));
			InputStream data = new ByteArrayInputStream(mimeData);
			MimeMessage msgs[] = new MimeMessage[1];
			msgs[0] = new MimeMessage(session, data);
			EmailPoster processor = (EmailPoster)processorManager.getProcessor(binder,EmailPoster.PROCESSOR_KEY);
			List errors = processor.postMessages((Folder)binder, "", msgs, session, RequestContextHolder.getRequestContext().getUser());
			if(errors.size() > 0) {
				Message m = (Message) errors.get(0);
				throw new RemotingException(m.getSubject());
			}
		}
		catch(MessagingException e) {
			throw new RemotingException(e);
		}			
	}
	@SuppressWarnings("unchecked")
	protected long addFolderEntry(String accessToken, org.kablink.teaming.remoting.ws.model.FolderEntry entry, String attachedFileName, Map options) {
		if(profiler == null) {
			profiler = new SimpleProfiler("webServices");
			count = 0;
		}
		SimpleProfiler.setProfiler(profiler);
		try {
			return getFolderModule().addEntry(entry.getParentBinderId(), entry.getDefinitionId(), 
				new ModelInputData(entry), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).getId().longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		} finally {
			if(++count == 10000) {
				logger.info(SimpleProfiler.toStr());
				profiler = null;
				SimpleProfiler.clearProfiler();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public long folder_addReply(String accessToken, long parentEntryId, org.kablink.teaming.remoting.ws.model.FolderEntry reply, String attachedFileName) {
		Map options = new HashMap();
		getTimestamps(options, reply);
		return addReply(accessToken, parentEntryId, reply, attachedFileName, options);
	}
	@SuppressWarnings("unchecked")
	protected long addReply(String accessToken, long parentEntryId, org.kablink.teaming.remoting.ws.model.FolderEntry reply, String attachedFileName, Map options) {
		try {
			return getFolderModule().addReply(reply.getParentBinderId(), new Long(parentEntryId), 
				reply.getDefinitionId(), new ModelInputData(reply), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).getId().longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public void folder_modifyEntry(String accessToken, org.kablink.teaming.remoting.ws.model.FolderEntry entry) {
		try {
			HashMap options = new HashMap();
	 		getTimestamps(options, entry);
			getFolderModule().modifyEntry(entry.getParentBinderId(), entry.getId(), 
				new ModelInputData(entry), null, null, null, options);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	public void folder_deleteEntry(String accessToken, long entryId) {
		getFolderModule().deleteEntry(null, entryId);
	}
	public void folder_preDeleteEntry(String accessToken, long entryId) {
		try {
			FolderEntry fe = getFolderModule().getEntry(null, entryId);
			TrashHelper.preDeleteEntry(
				this,
				fe.getParentBinder().getId(),
				entryId);
		}
		catch (Exception e) {
			throw new RemotingException(e);
		}
	}
	@SuppressWarnings("unchecked")
	public void folder_restoreEntry(String accessToken, long entryId) {
		FolderEntry fe = getFolderModule().getEntry(null, entryId);
		Long binderId = fe.getParentBinder().getId();
		HashMap	hm = new HashMap();
		hm.put("_docId", String.valueOf(entryId));
		hm.put("_docType", "entry");
		hm.put("_binderId", String.valueOf(binderId));
		TrashHelper.restoreEntries(
			this,
			new TrashHelper.TrashEntry(hm));
	}
    public long folder_copyEntry(String accessToken, long entryId, long destinationId) {
    	return getFolderModule().copyEntry(null, entryId, destinationId, null).getId().longValue();
    }
    public void folder_moveEntry(String accessToken, long entryId, long destinationId) {
    	getFolderModule().moveEntry(null, entryId, destinationId, null);
    }
    public void folder_reserveEntry(String accessToken,  long entryId) {
    	getFolderModule().reserveEntry(null, entryId);
    }
    public void folder_unreserveEntry(String accessToken, long entryId) {
    	getFolderModule().unreserveEntry(null, entryId);
    }
	public org.kablink.teaming.remoting.ws.model.Subscription folder_getSubscription(String accessToken, long entryId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		Subscription sub = getFolderModule().getSubscription(entry);
		if (sub == null) return null;
		return toSubscriptionModel(sub);
		
	}
	@SuppressWarnings("unchecked")
	public void folder_setSubscription(String accessToken, long entryId, org.kablink.teaming.remoting.ws.model.Subscription subscription) {
		if (subscription == null || subscription.getStyles().length == 0) {
			getFolderModule().setSubscription(null, entryId, null);
			return;
		}
		Map subMap = new HashMap();
		org.kablink.teaming.remoting.ws.model.SubscriptionStyle[] styles = subscription.getStyles();
		for (int i=0; i<styles.length; ++i) {
			subMap.put(Integer.valueOf(styles[i].getStyle()), styles[i].getEmailTypes());
		}
		getFolderModule().setSubscription(null, entryId, subMap);

	}
	public void folder_deleteEntryTag(String accessToken, long entryId, String tagId) {
		getFolderModule().deleteTag(null, entryId, tagId);
	}
	public void folder_setEntryTag(String accessToken, org.kablink.teaming.remoting.ws.model.Tag tag) {
		getFolderModule().setTag(null, tag.getEntityId(), tag.getName(), tag.isPublic());
	}
	public org.kablink.teaming.remoting.ws.model.Tag[] folder_getEntryTags(String accessToken, long entryId) {
		Collection<Tag>tags = getFolderModule().getTags(getFolderModule().getEntry(null, entryId));
		org.kablink.teaming.remoting.ws.model.Tag[] results = new org.kablink.teaming.remoting.ws.model.Tag[tags.size()];
		int i=0;
		for (Tag tag:tags) {
			results[i++] = toTagModel(tag);
		}
		return results;
	}
	public void folder_setRating(String accessToken, long entryId, long value) {
		getFolderModule().setUserRating(null, entryId, value);
	}
	
	public FileVersions folder_getFileVersions(String accessToken, long entryId, String fileName) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		FileAttachment att = entry.getFileAttachment(fileName);
		if(att != null)
			return toFileVersions(att);
		else
			throw new NoFileByTheNameException(fileName);
	}

	public Long folder_addMicroBlog(String accessToken, String text) {
		return BinderHelper.addMiniBlogEntry(this, text);
	}
	
	public byte[] folder_getAttachmentAsByteArray(String accessToken,
			long entryId, String attachmentId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		return getFileAttachmentAsByteArray(entry.getParentBinder(), entry, attachmentId);
	}
	
	public void folder_uploadFileAsByteArray(String accessToken, long entryId,
			String fileUploadDataItemName, String fileName, byte[] fileContent) {
		if (Validator.isNull(fileUploadDataItemName)) fileUploadDataItemName="ss_attachFile1";
		File originalFile = new File(fileName);
		fileName = originalFile.getName();

		try {
			getFolderModule().modifyEntry(null, entryId, fileUploadDataItemName, fileName, new ByteArrayInputStream(fileContent));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public long[] folder_getCreatedOrUpdatedEntries(String accessToken,
			String family, Calendar startTime, Calendar endTime) {
		Date startDate = (startTime != null) ? startTime.getTime() : new Date(0);
		if (endTime == null)
			throw new IllegalArgumentException("End time must be specified");
		Date endDate = endTime.getTime();
		Criteria crit = new Criteria();
		if (family != null) {
			family = StringCheckUtil.check(family);
			crit.add(eq(Constants.FAMILY_FIELD, family));
		}
		crit.add(eq(Constants.ENTITY_FIELD, "folderEntry"))
			.add(eq(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_ENTRY))
			.add(eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
			.add(between(Constants.MODIFICATION_DATE_FIELD, 
					DateTools .dateToString(startDate, DateTools.Resolution.SECOND), 
					DateTools.dateToString(endDate, DateTools.Resolution.SECOND)))
			.addOrder(new Order(Constants.MODIFICATION_DATE_FIELD, true));
		
		Document query = crit.toQuery();
		
		Map folderEntries = getBinderModule().executeSearchQuery(query, 0, 0);
		List<Map> entryList = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		List<Long> ids = new ArrayList<Long>();
		for(Map entry:entryList) {
			ids.add(Long.valueOf((String) entry.get(Constants.DOCID_FIELD)));
		}
		long[] result = new long[ids.size()];
		for(int i = 0; i < result.length; i++)
			result[i] = ids.get(i);
		return result;
	}

	public long[] folder_getDeletedEntries(String accessToken, String family, Calendar startTime, Calendar endTime) {
		List<Long> ids = getReportModule().getDeletedFolderEntryIds(family, startTime.getTime(), endTime.getTime());
		if(ids != null) {
			long[] result = new long[ids.size()];
			for(int i = 0; i < result.length; i++)
				result[i] = ids.get(i);
			return result;
		}
		else {
			return new long[0];
		}
	}

}
