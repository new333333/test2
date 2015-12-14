/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.lucene.document.DateTools;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFileVersionByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.modelprocessor.ProcessorManager;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.notify.Notify;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.mail.EmailFormatter;
import org.kablink.teaming.module.mail.EmailPoster;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.mail.MimeEntryPreparator;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.ws.BaseService;
import org.kablink.teaming.remoting.ws.RemotingException;
import org.kablink.teaming.remoting.ws.model.FileVersions;
import org.kablink.teaming.remoting.ws.model.FolderEntryBrief;
import org.kablink.teaming.remoting.ws.model.FolderEntryCollection;
import org.kablink.teaming.remoting.ws.util.DomInputData;
import org.kablink.teaming.remoting.ws.util.ModelInputData;
import org.kablink.teaming.util.ExtendedMultipartFile;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.EmailHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.Validator;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.api.ApiErrorCodeSupport;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;

import static org.kablink.util.search.Restrictions.between;
import static org.kablink.util.search.Restrictions.eq;

/**
 * ?
 * 
 * @author ?
 */
public class FolderServiceImpl extends BaseService implements FolderService, FolderServiceInternal {
	protected ProcessorManager processorManager;
	public void setProcessorManager(ProcessorManager processorManager) {
		this.processorManager = processorManager;
	}

	@Override
	public void folder_uploadFile(String accessToken, long binderId, long entryId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}
	@Override
	public void folder_removeFile(String accessToken, long entryId, String fileName) {
		try {
			FolderEntry entry = getFolderModule().getEntry(null, entryId);
			FileAttachment att = entry.getFileAttachment(fileName);
			FolderUtils.deleteFileInFolderEntry(this, entry, att);
		}	catch(WriteFilesException e) {
			throw new RemotingException(e);
		}	catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}			

	}
	@Override
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

	@Override
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
	
	@SuppressWarnings("unchecked")
	protected Map getFileAttachments(String fileUploadDataItemName, String[] fileNames)
	{
		return new HashMap();
	}
	
	@Override
	public long folder_addEntryWithXML(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName) {
		return addFolderEntry(accessToken, binderId, definitionId, inputDataAsXML, attachedFileName, null);
	}
	@SuppressWarnings("unchecked")
	protected long addFolderEntry(String accessToken, long binderId, String definitionId, String inputDataAsXML, String attachedFileName, Map options) {

		Document doc = getDocument(inputDataAsXML);
		try {
			return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc, getIcalModule()), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).getId().longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
				throw new RemotingException(e);
		}
	}
	
	@Override
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

	@Override
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

	@Override
	public void folder_addEntryWorkflow(String accessToken, long entryId, String definitionId) {
		getFolderModule().addEntryWorkflow(null, entryId, definitionId, null);
	}
   @Override
public void folder_deleteEntryWorkflow(String accessToken, long entryId, String definitionId) {
    	getFolderModule().deleteEntryWorkflow(null, entryId, definitionId);
    }
	@Override
	public void folder_modifyWorkflowState(String accessToken, long entryId, long stateId, String toState) {
		if (getFolderModule().checkIfManualTransitionAllowed(null, entryId, stateId, toState)) {
			getFolderModule().modifyWorkflowState(null, entryId, stateId, toState);
		}
	}
	@Override
	@SuppressWarnings("unchecked")
	public void folder_setWorkflowResponse(String accessToken, long entryId, long stateId, String question, String response) {
		Map params = new HashMap();
		response = StringCheckUtil.check(response);
		question = StringCheckUtil.check(question);
		params.put(question, response);
		getFolderModule().setWorkflowResponse(null, entryId, stateId, new MapInputData(params));
	}
	@Override
	public void folder_uploadFile(String accessToken, long entryId, String fileUploadDataItemName, String fileName) {
		throw new UnsupportedOperationException();
	}

	@Override
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
			ExtendedMultipartFile mf = new ExtendedMultipartFile(fileName, file, false, modifier, modificationDate==null?null:modificationDate.getTime());
			
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

	@Override
	public void folder_synchronizeMirroredFolder(String accessToken, long binderId) {
		// 5/23/2013 JK - Implementation changed to call enqueueFullSynchronize() method instead of fullSynchronize().
		getFolderModule().enqueueFullSynchronize(binderId);
	}

	@Override
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
	@Override
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
	
	@Override
	@SuppressWarnings("unchecked")
	public byte[] folder_getEntryAsMime(String accessToken, long entryId, boolean includeAttachments) {
		byte[] mimeData = null;
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		EmailFormatter processor = (EmailFormatter)processorManager.getProcessor(entry.getParentBinder(), EmailFormatter.PROCESSOR_KEY);
		User user = RequestContextHolder.getRequestContext().getUser();
		
		Map props = new HashMap();
		List recipients = new ArrayList();
		try {
			recipients.add(new InternetAddress(user.getEmailAddress(), user.getTitle()));
		} catch(Exception ex){}
		props.put(MailModule.TO, recipients);
		props.put(MailModule.SUBJECT, entry.getTitle());
		MimeEntryPreparator helper = new MimeEntryPreparator(processor, entry, props, logger, true);
 		UserPrincipal creator = entry.getCreation().getPrincipal();
 		String from = null;
 		try {
 			InternetAddress addr = new InternetAddress(EmailHelper.getFromEMA(creator), creator.getTitle());
 			from = addr.toString();
 		} catch(Exception ex) {
 			from = (MiscUtil.isFromOverrideForAll() ? MiscUtil.getFromOverride() : null);
 			if (!(MiscUtil.hasString(from))) {
 				from = entry.getPostedBy();
 			}
 		}
		helper.setDefaultFrom(from);		
 		helper.setTimeZone(user.getTimeZone().getID());
 		helper.setLocale(user.getLocale());
		helper.setType(Notify.NotifyType.interactive);
 		helper.setSendAttachments(includeAttachments);
 		
 		Session session = Session.getDefaultInstance(new Properties());
 		MimeMessage msg = new MimeMessage(session);
 		ByteArrayOutputStream strm = new ByteArrayOutputStream();
 		try {
 	 		helper.prepare(msg);
 	 		msg.writeTo(strm);
 	 		mimeData = strm.toByteArray();
 		} catch(Exception e) {
			throw new RemotingException(e);
 		}
		
		return mimeData;
	}

	@Override
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

	@Override
	@SuppressWarnings("unchecked")
	public long folder_addEntry(String accessToken, org.kablink.teaming.remoting.ws.model.FolderEntry entry, String attachedFileName) {
		SimpleProfiler.start("folderService_addEntry");
		HashMap options = new HashMap();
 		getTimestamps(options, entry);
		long entryId = addFolderEntry(accessToken, entry, attachedFileName, options);	
		SimpleProfiler.stop("folderService_addEntry");			
		return entryId;
	}
	@Override
	@SuppressWarnings("unchecked")
	public long folder_addEntryAsMime(String accessToken, long binderId, byte[] mimeData) {
		try {
			long entryId = 0;
			Session session = Session.getDefaultInstance(new Properties());
			org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));
			InputStream data = new ByteArrayInputStream(mimeData);
			MimeMessage msgs[] = new MimeMessage[1];
			msgs[0] = new MimeMessage(session, data);
			EmailPoster processor = (EmailPoster)processorManager.getProcessor(binder,EmailPoster.PROCESSOR_KEY);
			org.kablink.teaming.domain.User user = RequestContextHolder.getRequestContext().getUser();
			List errors = processor.postMessages((Folder)binder, user.getEmailAddress(), msgs, session, user);
			if(errors.size() > 0) {
				// Bugzilla 609799:
				//    Protect against the error or its subject being
				//    null.
				Message m = (Message) errors.get(0);
				String subject;
				if (null == m) {
					subject = "";
				}
				else {
					subject = m.getSubject();
					if (null == subject) {
						subject = "";
					}
				}
				throw new RemotingException(subject);
			}
			String[] hdrs = msgs[0].getHeader(EmailPoster.X_TEAMING_ENTRYID);
			if(hdrs != null && hdrs.length > 0)
				entryId = Long.valueOf(hdrs[0]);
			return entryId;
		}
		catch(MessagingException e) {
			throw new RemotingException(e);
		}			
	}
	@SuppressWarnings("unchecked")
	protected long addFolderEntry(String accessToken, org.kablink.teaming.remoting.ws.model.FolderEntry entry, String attachedFileName, Map options) {
		try {
			return getFolderModule().addEntry(entry.getParentBinderId(), entry.getDefinitionId(), 
				new ModelInputData(entry), getFileAttachments("ss_attachFile", new String[]{attachedFileName} ), options).getId().longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	@Override
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

	@Override
	@SuppressWarnings("unchecked")
	public Calendar folder_modifyEntry(String accessToken, org.kablink.teaming.remoting.ws.model.FolderEntry entry) {
		SimpleProfiler.start("folderService_modifyEntry");
		try {
			HashMap options = new HashMap();
	 		getTimestamps(options, entry);
			getFolderModule().modifyEntry(entry.getParentBinderId(), entry.getId(), 
				new ModelInputData(entry), null, null, null, options);
			// Read it back from the database
			org.kablink.teaming.domain.Entry dEntry = getFolderModule().getEntry(entry.getParentBinderId(), entry.getId());
			Calendar modCal = Calendar.getInstance();
			modCal.setTime(dEntry.getModification().getDate());
			return modCal;
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
		finally {
			SimpleProfiler.stop("folderService_modifyEntry");
		}
	}

	@Override
	public void folder_deleteEntry(String accessToken, long entryId) {
		getFolderModule().deleteEntry(null, entryId);
	}
	@Override
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
	@Override
	@SuppressWarnings("unchecked")
	public void folder_restoreEntry(String accessToken, long entryId) {
		FolderEntry fe = getFolderModule().getEntry(null, entryId);
		Long binderId = fe.getParentBinder().getId();
		HashMap	hm = new HashMap();
		hm.put("_docId", String.valueOf(entryId));
		hm.put("_docType", "entry");
		hm.put("_binderId", String.valueOf(binderId));
		TrashHelper.restoreEntities(
			this,
			new TrashHelper.TrashEntity(hm));
	}
    @Override
	public long folder_copyEntry(String accessToken, long entryId, long destinationId) throws WriteFilesException {
    	return getFolderModule().copyEntry(null, entryId, destinationId, null, null).getId().longValue();
    }
    @Override
	public void folder_moveEntry(String accessToken, long entryId, long destinationId) throws WriteFilesException {
    	getFolderModule().moveEntry(null, entryId, destinationId, null, null);
    }
    @Override
	public void folder_reserveEntry(String accessToken,  long entryId) {
    	getFolderModule().reserveEntry(null, entryId);
    }
    @Override
	public void folder_unreserveEntry(String accessToken, long entryId) {
    	getFolderModule().unreserveEntry(null, entryId);
    }
	@Override
	public org.kablink.teaming.remoting.ws.model.Subscription folder_getSubscription(String accessToken, long entryId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		Subscription sub = getFolderModule().getSubscription(entry);
		if (sub == null) return null;
		return toSubscriptionModel(sub);
		
	}
	@Override
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
	@Override
	public void folder_deleteEntryTag(String accessToken, long entryId, String tagId) {
		getFolderModule().deleteTag(null, entryId, tagId);
	}
	@Override
	public void folder_setEntryTag(String accessToken, org.kablink.teaming.remoting.ws.model.Tag tag) {
		getFolderModule().setTag(null, tag.getEntityId(), tag.getName(), tag.isPublic());
	}
	@Override
	public org.kablink.teaming.remoting.ws.model.Tag[] folder_getEntryTags(String accessToken, long entryId) {
		Collection<Tag>tags = getFolderModule().getTags(getFolderModule().getEntry(null, entryId));
		org.kablink.teaming.remoting.ws.model.Tag[] results = new org.kablink.teaming.remoting.ws.model.Tag[tags.size()];
		int i=0;
		for (Tag tag:tags) {
			results[i++] = toTagModel(tag);
		}
		return results;
	}
	@Override
	public void folder_setRating(String accessToken, long entryId, long value) {
		getFolderModule().setUserRating(null, entryId, value);
	}
	
	@Override
	public FileVersions folder_getFileVersions(String accessToken, long entryId, String fileName) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		FileAttachment att = entry.getFileAttachment(fileName);
		if(att != null)
			return toFileVersions(att);
		else
			throw new IllegalArgumentException("No such file [" + fileName + "]");
	}

	@Override
	public Long folder_addMicroBlog(String accessToken, String text) {
		return BinderHelper.addMiniBlogEntry(this, text);
	}
	
	@Override
	public byte[] folder_getAttachmentAsByteArray(String accessToken,
			long entryId, String attachmentId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		return getFileAttachmentAsByteArray(entry.getParentBinder(), entry, attachmentId);
	}
	
	@Override
	public void folder_uploadFileAsByteArray(String accessToken, long entryId,
			String fileUploadDataItemName, String fileName, byte[] fileContent) {
		SimpleProfiler.start("folder_uploadFileAsByteArray");
		File originalFile = new File(fileName);
		fileName = originalFile.getName();
		
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		try {
			FileUtils.modifyFolderEntryWithFile(entry, fileUploadDataItemName, fileName, new ByteArrayInputStream(fileContent), null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
		SimpleProfiler.stop("folder_uploadFileAsByteArray");
	}
	
	@Override
	public String folder_validateUploadFile(String accessToken,long entryId, String fileName, long fileSize) {
		SimpleProfiler.start("folder_validateUploadFileAsByteArray");
		
		String result = "";
		
		try {
			File originalFile = new File(fileName);
			fileName = originalFile.getName();
			FolderEntry entry = getFolderModule().getEntry(null, entryId);
			FileUtils.validateModifyFolderEntryWithFile(entry, fileName, fileSize);
		}
		catch(WriteFilesException e) {
			result = e.getApiErrorCode().name();
		}
		catch(WriteEntryDataException e) {
			result = e.getApiErrorCode().name();
		}
		catch(RuntimeException e) {
			if(e instanceof ApiErrorCodeSupport)
				result = ((ApiErrorCodeSupport)e).getApiErrorCode().name();
			else
				result = ApiErrorCode.SERVER_ERROR.name();
		}
		SimpleProfiler.stop("folder_validateUploadFileAsByteArray");	
		
		return result;
	}

	@Override
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
		
		Map folderEntries = getBinderModule().executeSearchQuery(query, Constants.SEARCH_MODE_NORMAL, 0, 0,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
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

	@Override
	public long[] folder_getDeletedEntries(String accessToken, String family, Calendar startTime, Calendar endTime) {
		List<Long> ids = getReportModule().getDeletedFolderEntryIds(family,
				(startTime != null)? startTime.getTime():null,
				endTime.getTime());
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
	
	@Override
	public long[] folder_getDeletedEntriesInFolders(String accessToken, long[] folderIds, String family, Calendar startTime, Calendar endTime) {
		List<Long> ids = getReportModule().getDeletedFolderEntryIds(folderIds, 
				family, 
				(startTime != null)? startTime.getTime():null,
				endTime.getTime());
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

	@Override
	public long[] folder_getRestoredEntriesInFolders(String accessToken, long[] folderIds, String family, Calendar startTime, Calendar endTime) {
		List<Long> ids = getReportModule().getRestoredFolderEntryIds(folderIds, 
				family, 
				(startTime != null)? startTime.getTime():null, 
				endTime.getTime());
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

	@Override
	public long[] folder_getMovedEntries(String accessToken, Calendar startTime, Calendar endTime) {
		List<Long> ids = getReportModule().getMovedFolderEntryIds((startTime != null)? startTime.getTime():null, endTime.getTime());
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

	@Override
	public byte[] folder_getFileVersionAsByteArray(String accessToken,
			long entryId, String fileVersionId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		return getFileVersionAsByteArray(entry.getParentBinder(), entry, fileVersionId);
	}

	@Override
	public FileVersions folder_getFileVersionsFromAttachment(
			String accessToken, long entryId, String attachmentId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		FileAttachment fa = getFileAttachment(entry, attachmentId);
		return toFileVersions(fa);
	}

	@Override
	public void folder_removeAttachment(String accessToken, long entryId,
			String attachmentId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		Attachment att = entry.getAttachment(attachmentId);
		if(att == null || !(att instanceof FileAttachment))
			return;		
		try {
			FolderUtils.deleteFileInFolderEntry(this, entry, (FileAttachment)att);
		}	catch(WriteFilesException e) {
			throw new RemotingException(e);
		}	catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}			
	}

	@Override
	public void folder_uploadAttachmentAsByteArray(String accessToken,
			long entryId, String fileUploadDataItemName, String attachmentId,
			byte[] fileContent) {
		if (Validator.isNull(fileUploadDataItemName) || "ss_attachFile".equals(fileUploadDataItemName))
			fileUploadDataItemName="ss_attachFile1";
		
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		FileAttachment fa = getFileAttachment(entry, attachmentId);
	
		try {
			FileUtils.modifyFolderEntryWithFile(entry, fileUploadDataItemName, fa.getFileItem().getName(),  new ByteArrayInputStream(fileContent), null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
		catch(WriteEntryDataException e) {
			throw new RemotingException(e);
		}
	}

	@Override
	public String folder_validateUploadAttachment(String accessToken,
			long entryId, String attachmentId, long fileSize) {
		SimpleProfiler.start("folder_validateUploadAttachmentAsByteArray");
		
		String result = "";
		
		try {
			FolderEntry entry = getFolderModule().getEntry(null, entryId);
			FileAttachment fa = getFileAttachment(entry, attachmentId);		
			FileUtils.validateModifyFolderEntryWithFile(entry, fa.getFileItem().getName(), fileSize);
		}
		catch(WriteFilesException e) {
			result = e.getApiErrorCode().name();
		}
		catch(WriteEntryDataException e) {
			result = e.getApiErrorCode().name();
		}
		catch(RuntimeException e) {
			if(e instanceof ApiErrorCodeSupport)
				result = ((ApiErrorCodeSupport)e).getApiErrorCode().name();
			else
				result = ApiErrorCode.SERVER_ERROR.name();
		}
		SimpleProfiler.stop("folder_validateUploadAttachmentAsByteArray");	
		
		return result;
	}

	@Override
	public boolean folder_uploadAttachmentAsByteArrayConditional(String accessToken, long entryId, 
			String fileUploadDataItemName, String attachmentId, byte[] fileContent,
			Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		FileAttachment fa = getFileAttachment(entry, attachmentId);
		boolean result = FileUtils.matchesTopMostVersion(fa, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
		if(result)
			folder_uploadAttachmentAsByteArray(accessToken, entryId, fileUploadDataItemName, attachmentId, fileContent);
		return result;
	}

	@Override
	public String folder_validateUploadAttachmentConditional(String accessToken,
			long entryId, String attachmentId, long fileSize,
			Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		FileAttachment fa = getFileAttachment(entry, attachmentId);
		boolean result = FileUtils.matchesTopMostVersion(fa, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
		if(result)
			return folder_validateUploadAttachment(accessToken, entryId, attachmentId, fileSize);
		else
			return ApiErrorCode.FILE_VERSION_CONFLICT.name();
	}

	@Override
	public boolean[] folder_testFolderOperation(String accessToken,
			String operationName, long[] folderIds) {
		boolean[] result = new boolean[folderIds.length];
		FolderOperation folderOperation = null;
		try {
			folderOperation = FolderOperation.valueOf(operationName);
		}
		catch(IllegalArgumentException e) {
			for(int i = 0; i < folderIds.length; i++)
				result[i] = false;
			return result;
		}
		for(int i = 0; i < folderIds.length; i++) {
			try {
				// Do not use FolderModule.getFolder() method to load the folder, since it will 
				// fail if the caller doesn't already have the appropriate right to load it.
				Folder folder = getFolderModule().getFolderWithoutAccessCheck(folderIds[i]);
				result[i] = getFolderModule().testAccess(folder, folderOperation);
			}
			catch(NoFolderByTheIdException e) {
				// The specified folder does not exist. Instead of throwing an exception (and
				// aborting this operation all together), simply set the result to false for
				// this folder, and move on to the next folder.
				result[i] = false;
				continue;
			}
		}
		return result;
	}

	@Override
	public boolean[] folder_testFolderOperations(String accessToken, String[] operationNames, long folderId) {
		boolean[] result = new boolean[operationNames.length];
		for(int i = 0; i < operationNames.length; i++)
			result[i] = false;
		Folder folder;
		try {
			folder = getFolderModule().getFolderWithoutAccessCheck(folderId);
		}
		catch(NoFolderByTheIdException e) {
			return result;
		}
		FolderOperation folderOperation;
		for(int i = 0; i < operationNames.length; i++) {
			try {
				folderOperation = FolderOperation.valueOf(operationNames[i]);
				result[i] = getFolderModule().testAccess(folder, folderOperation);
			}
			catch(IllegalArgumentException e) {
				continue;
			}
		}
		return result;
	}

	@Override
	public boolean[] folder_testEntryOperation(String accessToken,
			String operationName, long[] entryIds) {
		boolean[] result = new boolean[entryIds.length];
		FolderOperation folderOperation = null;
		try {
			folderOperation = FolderOperation.valueOf(operationName);
		}
		catch(IllegalArgumentException e) {
			for(int i = 0; i < entryIds.length; i++)
				result[i] = false;
			return result;
		}
		for(int i = 0; i < entryIds.length; i++) {
			try {
				// Do not use FolderModule.getEntry() method to load the entry, since it will 
				// fail if the caller doesn't already have the appropriate right to load it.
				FolderEntry entry = getFolderModule().getEntryWithoutAccessCheck(null, entryIds[i]);
				result[i] = getFolderModule().testAccess(entry, folderOperation);
			}
			catch(NoFolderEntryByTheIdException e) {
				// The specified entry does not exist. Instead of throwing an exception (and
				// aborting this operation all together), simply set the result to false for
				// this entry, and move on to the next entry.
				result[i] = false;
				continue;
			}
		}
		return result;
	}

	@Override
	public boolean[] folder_testEntryOperations(String accessToken, String[] operationNames, long entryId) {
		boolean[] result = new boolean[operationNames.length];
		for(int i = 0; i < operationNames.length; i++)
			result[i] = false;
		FolderEntry entry;
		try {
			entry = getFolderModule().getEntryWithoutAccessCheck(null, entryId);
		}
		catch(NoFolderEntryByTheIdException e) {
			return result;
		}
		FolderOperation folderOperation;
		for(int i = 0; i < operationNames.length; i++) {
			try {
				folderOperation = FolderOperation.valueOf(operationNames[i]);
				result[i] = getFolderModule().testAccess(entry, folderOperation);
			}
			catch(IllegalArgumentException e) {
				continue;
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.remoting.ws.service.folder.FolderService#folder_incrementFileMajorVersion(java.lang.String, long, java.lang.String)
	 */
	@Override
	public void folder_incrementFileMajorVersion(String accessToken,
			long entryId, String attachmentId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		FileAttachment fa = getFileAttachment(entry, attachmentId);
		getBinderModule().incrementFileMajorVersion(entry, fa);
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.remoting.ws.service.folder.FolderService#folder_setFileVersionNote(java.lang.String, long, java.lang.String, java.lang.String)
	 */
	@Override
	public void folder_setFileVersionNote(String accessToken,
			long entryId, String fileVersionId, String note) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		VersionAttachment va = getVersionAttachment(entry, fileVersionId);
		FileUtils.setFileVersionNote(va, note);
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.remoting.ws.service.folder.FolderService#folder_promoteFileVersionCurrent(java.lang.String, long, java.lang.String)
	 */
	@Override
	public void folder_promoteFileVersionCurrent(String accessToken,
			long entryId, String fileVersionId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		VersionAttachment va = getVersionAttachment(entry, fileVersionId);
		FileUtils.promoteFileVersionCurrent(va);
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.remoting.ws.service.folder.FolderService#folder_deleteFileVersion(java.lang.String, long, java.lang.String)
	 */
	@Override
	public void folder_deleteFileVersion(String accessToken, long entryId,
			String fileVersionId) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		deleteFileVersion(entry, fileVersionId);
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.remoting.ws.service.folder.FolderService#folder_setFileVersionStatus(java.lang.String, long, java.lang.String, int)
	 */
	@Override
	public void folder_setFileVersionStatus(String accessToken, long entryId,
			String fileVersionId, int status) {
		FolderEntry entry = getFolderModule().getEntry(null, entryId);
		VersionAttachment va = getVersionAttachment(entry, fileVersionId);
		FileUtils.setFileVersionStatus(va, status);
	}

	private void deleteFileVersion(DefinableEntity entity, String fileVersionId) {
		VersionAttachment va;
		try {
			va = getVersionAttachment(entity, fileVersionId);
		}
		catch(NoFileVersionByTheIdException e) {
			// The version isn't found. Since post-action condition is still met, 
			// do not throw an exception. Return normally.
			return;
		}
		FileUtils.deleteFileVersion(va);
	}
}
