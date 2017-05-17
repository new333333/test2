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
package org.kablink.teaming.module.mail.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.BodyPart;
import javax.mail.Part;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EmailLog;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.EmailLog.EmailLogStatus;
import org.kablink.teaming.domain.EmailLog.EmailLogType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.EntryDataErrors;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.AttendedEntries;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.mail.EmailPoster;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TextToHtml;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Html;
import org.kablink.util.Validator;
import org.springframework.util.FileCopyUtils;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class DefaultEmailPoster extends CommonDependencyInjection implements EmailPoster {
	/*
	 * Inner class used to track where an entry's description comes
	 * from.
	 */
	@SuppressWarnings("unused")
	private static class DescInfo {
		enum Type {
			NONE,
			PLAIN,
			HTML
		};
		private int		m_descSavedAtMultiPartDepth;	//
		private int		m_descSavedAtPartDepth;			//
		private int		m_multiPartDepth;				//
		private int		m_partDepth;					//
		private Type	m_descType;						//

		/*
		 * Constructor method.
		 */
		DescInfo() {
			m_descType                  = Type.NONE;
			m_descSavedAtMultiPartDepth =
			m_descSavedAtPartDepth      = (-1);
		}
		
		void multiPartEntry() {                          m_multiPartDepth += 1;}
		void multiPartExit()  {if (0 < m_multiPartDepth) m_multiPartDepth -= 1;}
		void partEntry()      {                          m_partDepth += 1;     }
		void partExit()       {if (0 < m_partDepth)      m_partDepth -= 1;     }
		
		/*
		 * Marks a description of m_type descType as having been saved at
		 * the current m_part depth.
		 */
		void descSaved(Type descType) {
			m_descType                  = descType;
			m_descSavedAtMultiPartDepth = m_multiPartDepth;
			m_descSavedAtPartDepth      = m_partDepth;
		}
		
		/*
		 * Returns true if a description of m_type descType should be
		 * saved at the current m_part depth.
		 */
		boolean saveDesc(Type descType) {
			// Invalid case...
			if (Type.NONE == descType) {
				// ...ignore.
				return false;
			}
			
			// If we haven't save a description yet...
			if (Type.NONE == m_descType) {
				// ...we can save any m_type now.
				return true;
			}
			
			// If we're saving a plain text description...
			if (Type.PLAIN == descType) {
				// ...we can only do so if it's from a m_part shallower
				// ...than previously saved from.
				return (m_descSavedAtPartDepth > m_partDepth);
			}
			
			// If we're saving an HTML text description...
			else if (Type.HTML == descType) {
				// ...we can do so if its from a m_part shallower than
				// ...previously saved from...
				if (m_descSavedAtPartDepth > m_partDepth) {
					return true;
				}
				
				// ...or if it's overwriting a plain text description
				// ...at the current depth (i.e., we allow an HTML
				// ...description to overwrite a plain text
				// ...description.)
				return ((Type.PLAIN == m_descType) && (m_descSavedAtPartDepth == m_partDepth));
			}

			// No other types can be saved.
			return false;
		}
	}

	/**
	 * Inner class used to ???
	 * 
	 * @author ?
	 */
	public class FileHandler implements org.springframework.web.multipart.MultipartFile {
		private int		m_size;			//
		private Part	m_part;			//
		private String	m_contentId;	//
		private String	m_fileName;		//
		private String	m_type;			//
		
		/**
		 * Constructor method.
		 * 
		 * @param m_part
		 * 
		 * @throws MessagingException
		 */
		public FileHandler(Part part) throws MessagingException {
			m_part     = part;
			m_fileName = part.getFileName();
			if (null != m_fileName) {
				try {
					m_fileName = javax.mail.internet.MimeUtility.decodeText(m_fileName);
				}
				catch (java.io.UnsupportedEncodingException  nc) {
					throw new MessagingException(nc.getMessage());	
				}
			}
			
			m_type = part.getContentType();
			m_size = part.getSize();
			
			String[] contentIds = part.getHeader("Content-ID");
			if (contentIds    != null && contentIds.length      > 0 && 
				contentIds[0] != null && contentIds[0].length() > 1) {
				m_contentId = contentIds[0].substring(1, contentIds[0].length() - 1);
				if (null == m_fileName) {
					m_fileName = m_contentId;
				}
			}
		}

		/**
		 * ?
		 * 
		 * @return
		 */
		public String getContentId() throws MessagingException {
			return m_contentId;
		}
		
		/**
		 * Return the name of the parameter in the multipart form.
		 * 
		 * @return
		 */
		@Override
		public String getName() {
			return "attachment";
		}

		/**
		 * Return whether the uploaded file is empty in the sense that
		 * no file has been chosen in the multipart form.
		 * 
		 * @return
		 */
		@Override
		public boolean isEmpty() {
			return false;
		}
		
		/**
		 * Return the original filename in the client's filesystem.
		 * This may contain path information depending on the browser used,
		 * but it typically will not with any other than Opera.
		 * 
		 * @return
		 */
		@Override
		public String getOriginalFilename() {
			return m_fileName;
		}
		
		
		/**
		 * Return the content m_type of the file.
		 * 
		 * @return
		 */
		@Override
		public String getContentType() {
			return m_type;
		}

		/**
		 * Return the m_size of the file in bytes.
		 * 
		 * @return
		 */
		@Override
		public long getSize() {
			return m_size;
		}
		
		/**
		 * Return the contents of the file as an array of bytes.
		 * 
		 * @return
		 *
		 * @throws IOException
		 */
		@Override
		public byte[] getBytes() throws IOException {
			byte [] results = new byte[m_size];
			try {
				m_part.getInputStream().read(results);
			}
			catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage()==null? me.getMessage():me.getLocalizedMessage());
			}
			return results;
		}

		/**
		 * Return an InputStream to read the contents of the file from.
		 * The user is responsible for closing the stream.
		 * 
		 * @return
		 *
		 * @throws IOException
		 */
		@Override
		public InputStream getInputStream() throws IOException {
			try {
				return m_part.getInputStream();
			}
			catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage()==null? me.getMessage():me.getLocalizedMessage());
			}
		}
		
		/**
		 * Transfer the received file to the given destination file.
		 * <p>This may either move the file in the filesystem, copy the file in the
		 * filesystem, or save memory-held contents to the destination file.
		 * If the destination file already exists, it will be deleted first.
		 * <p>If the file has been moved in the filesystem, this operation cannot
		 * be invoked again. Therefore, call this method just once to be able to
		 * work with any storage mechanism.
		 * 
		 * @param dest the destination file
		 * 
		 * @throws	IOException in case of reading or writing errors
		 * @throws	java.lang.IllegalStateException if the file has
		 * 			already been moved in the filesystem as is not
		 *			available anymore for another transfer
		 */
		@Override
		public void transferTo(File dest) throws IOException, IllegalStateException {
			// Copied from org.springframework.web.multipart.commons.CommonsMultiPart.
//			if (!isAvailable()) {
//				throw new IllegalStateException("File has already been moved - cannot be transferred again");
//			}

			if (dest.exists() && !dest.delete()) {
				throw new IOException(
					"Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
			}

			FileOutputStream	out = null;
			InputStream			in  = null;
			try {
				out = new FileOutputStream(dest);
				in  = getInputStream();
				FileCopyUtils.copy(in, out);
				
/*				dest.this.fileItem.write(dest);
				if (logger.isDebugEnabled()) {
					String action = "transferred";
					if (!this.fileItem.isInMemory()) {
						action = isAvailable() ? "copied" : "moved";
					}
					logger.debug("Multipart file '" + getName() + "' with original filename [" +
							getOriginalFilename() + "], stored " + getStorageDescription() + ": " +
							action + " to [" + dest.getAbsolutePath() + "]");
				}
*/
			}
			catch (IOException ex) {
				throw ex;
			}
			catch (Exception ex) {
				String exMsg = ex.getLocalizedMessage();
				if (null == exMsg) {
					exMsg = ex.getMessage();
				}
				logger.error("Could not transfer to file (1)", ex);
				throw new IOException("Could not transfer to file: " + exMsg);
			}
			finally {
				if (null != in) {
					try {
						in.close();
					}
					catch(Exception e) {
						logger.error("Could not transfer to file (2)");
					}
				}
				if (null != out) {
					try {
						out.close();
					}
					catch(Exception e) {
						logger.error("Could not transfer to file (3)");
					}
				}
			}
		
		}		
	}
	
	// FolderModule access.
    private FolderModule folderModule;
    public void setFolderModule(FolderModule folderModule) {
    	this.folderModule = folderModule;
    }
    public FolderModule getFolderModule() {
    	return this.folderModule;
    }
    
	// IcalModule access.
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}
	
	// ReportModule access.
	private ReportModule reportModule;
	public ReportModule getReportModule() {
		return reportModule;
	}	
	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}

	/**
	 * Processes posting a Message[] into Vibe.
	 * 
	 * @param folder
	 * @param recipient
	 * @param msgs
	 * @param session
	 * @param postAsUser
	 */
	@Override
	public List postMessages(Folder folder, String recipient, Message[] msgs, Session session, User postAsUser) {
		// Initialize collections.
		Map fileItems = new HashMap();
		List iCalendars = new ArrayList();
		Map inputData = new HashMap();
		List errors = new ArrayList();
		
		// Save the job processing user context.
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		
		// If no recipient specified then user the current context user
		InternetAddress from = null;
		if(null != postAsUser) {
			try {
				from = new InternetAddress(postAsUser.getEmailAddress(), Utils.getUserTitle(postAsUser));
			}
			catch (UnsupportedEncodingException ex) {
				String exMsg = ex.getLocalizedMessage();
				if (null == exMsg) {
					exMsg = ex.getMessage();
				}
				logger.error("Error building InternetAddress for:  '" + postAsUser.getEmailAddress() + "',  Error:  " + exMsg);
			}
		}
		
		for (int i = 0; i < msgs.length; i += 1) {
			try {
				if (msgs[i].isSet(Flags.Flag.DELETED)) {
					continue;  // GroupWise doesn't purge right away so just skip it.
				}
				
				String title = msgs[i].getSubject();
				if (title == null) {
					title = "";
				}
				if((null == postAsUser) || (null == from)) {
					from = ((InternetAddress) msgs[i].getFrom()[0]);
				}
				
		    	// We'll add an entry into the email log for each post.
		  		EmailLog emailLog = new EmailLog(EmailLogType.emailPosting, EmailLogStatus.received);
		  		emailLog.setFrom(from.getAddress());
		  		emailLog.setSubj(title);
		  		String[] toEmailAddresses = new String[] {folder.getPathName()};
		  		emailLog.setToEmailAddresses(toEmailAddresses);
		  		
				try {
					// Save the original from.
					try {
						//Decode the ISO 8859 format address to Unicode - Bug #792016 
						inputData.put(ObjectKeys.INPUT_FIELD_POSTING_FROM, MimeUtility.decodeText(from.toUnicodeString())); 
					} catch (UnsupportedEncodingException e) {
						inputData.put(ObjectKeys.INPUT_FIELD_POSTING_FROM, from.toString()); 
					}
					inputData.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
					if (isReply(folder, title, msgs[i]))						
					     processReply(folder, from, msgs[i], inputData, fileItems, iCalendars, emailLog);
					else processEntry(folder, from, msgs[i], inputData, fileItems, iCalendars, emailLog);
				}
				catch(Exception ex) {
					String exMsg = ex.getLocalizedMessage();
					if (null == exMsg) {
						exMsg = ex.getMessage();
					}
					try {
						emailLog.setComments("Error posting the message from (1):  '" + MimeUtility.decodeText(from.toUnicodeString()) + "', Error:  " + exMsg);
					} catch (UnsupportedEncodingException e) {
						emailLog.setComments("Error posting the message from (2):  '" + from.toString() + "', Error:  " + exMsg);
					}
					emailLog.setStatus(EmailLogStatus.error);
					throw ex;
				}
				finally {
					// Reset the context to handle the next message.
					fileItems.clear();
					inputData.clear();
					iCalendars.clear();
					getReportModule().addEmailLog(emailLog);
				}
				
			}
			catch (MessageRemovedException rx) {
				continue;
			}
			catch (Exception ex) {
				String exMsg = ex.getLocalizedMessage();
				if (null == exMsg) {
					exMsg = ex.getMessage();
				}
				try {
					if (logger.isDebugEnabled())
					     logger.debug("Error posting the message from (3):  '" + MimeUtility.decodeText(from.toUnicodeString()) + "', Error:  " + exMsg, ex);
					else logger.error("Error posting the message from (4):  '" + MimeUtility.decodeText(from.toUnicodeString()) + "', Error:  " + exMsg    );
				} catch (UnsupportedEncodingException e) {
					if (logger.isDebugEnabled())
					     logger.debug("Error posting the message from (5):  '" + from.toString() + "', Error:  " + exMsg, ex);
					else logger.error("Error posting the message from (6):  '" + from.toString() + "', Error:  " + exMsg    );
				}
				
				// If it failed and it's from self, don't reply or we
				// will get it back.   (??? Where is that done???)
				errors.add(
					postError(
						recipient,
						msgs[i],
						from,
						postAsUser,
						ex));
			}
			finally {
				// Restore the job processing user context.
				RequestContextHolder.setRequestContext(oldCtx);				
			}
		}
		return errors;
	}
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected void processReply(Folder folder, InternetAddress from, Message msg, Map inputData, Map fileItems, List iCalendars, EmailLog emailLog ) throws Exception {
		inputData = StringCheckUtil.check(inputData);
		String title = (String)inputData.get(ObjectKeys.FIELD_ENTITY_TITLE);
		Long parentDocId = getParentDocId(folder, title, msg);
		FolderEntry entry = getFolderModule().getEntry(folder.getId(), parentDocId);
		User fromUser = setUser(folder, from);
		if (null == fromUser) {
			fromUser = setAnonymousUser(folder);
		}
		else {
			if (!(getFolderModule().testAccess(entry, FolderModule.FolderOperation.addReply))) {
				fromUser = setAnonymousUser(folder);				
			}
		}
		
		Definition def = getReplyDefinition(folder, parentDocId);
		processPart(folder, msg, inputData, fileItems, iCalendars, new DescInfo());
		if (MiscUtil.hasItems(fileItems)) {
			// Log the files that were attached.
			List<String> fileNames = new ArrayList<String>();
			Iterator itFileItems = fileItems.entrySet().iterator();
			while (itFileItems.hasNext()) {
				Map.Entry fileItemEntry = ((Map.Entry) itFileItems.next());
				FileHandler fh = ((FileHandler) fileItemEntry.getValue());
				fileNames.add(fh.getName());
			}
			emailLog.setFileAttachments(fileNames);
		}

		FolderEntry reply = getFolderModule().addReply(folder.getId(), parentDocId, def == null? null:def.getId(), new MapInputData(inputData), fileItems, null);
		if (null != reply) {
			try {
				msg.addHeader(EmailPoster.X_TEAMING_ENTRYID, reply.getId().toString());
			}
			catch(MessagingException ex){}
		}
		msg.setFlag(Flags.Flag.DELETED, true);
	}
	
	/*
	 *  Override to provide alternate processing. 
	 */
	protected void processEntry(Folder folder, InternetAddress from, Message msg, Map inputData, Map fileItems, List iCalendars, EmailLog emailLog ) throws Exception {
		inputData = StringCheckUtil.check(inputData);
		User fromUser = setUser(folder, from);
		if (null == fromUser) {
			fromUser = setAnonymousUser(folder);
		}
		else {
			if (!(getFolderModule().testAccess(folder, FolderModule.FolderOperation.addEntry))) {
				fromUser = setAnonymousUser(folder);				
			}
		}
		
		Definition def = getEntryDefinition(folder);
		processPart(folder, msg, inputData, fileItems, iCalendars, new DescInfo());
		AttendedEntries entryIdsFromICalendars = new AttendedEntries();
		if (!(fileItems.isEmpty())) {
			uniquifyFileItems(fileItems);
			entryIdsFromICalendars.addAll(processICalAttachments(folder, def, inputData, fileItems, iCalendars));
			
			// Log the files that were attached.
			List<String> fileNames = new ArrayList<String>();
			Iterator itFileItems = fileItems.entrySet().iterator();
			while (itFileItems.hasNext()) {
				Map.Entry fileItemEntry = ((Map.Entry) itFileItems.next());
				FileHandler fh = ((FileHandler) fileItemEntry.getValue());
				fileNames.add(fh.getOriginalFilename());
			}
			emailLog.setFileAttachments(fileNames);
		}
		
		if (!(iCalendars.isEmpty())) {
			entryIdsFromICalendars.addAll(processICalInline(folder, def, inputData, fileItems, iCalendars));
		}
		
//DEBUG	java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
//		msg.writeTo(buf);
//		inputData.put("description", buf.toString());
		
		// Did we just process a single iCal attachment?
		int iCalEntries = entryIdsFromICalendars.getTotalCount();
		FolderEntry entry;
		if (1 == iCalEntries) {
			// Yes!  Then we'll use the rest of the information to
			// modify that.
			Long id;
			if (1 == entryIdsFromICalendars.getAddedCount()) id = ((Long) entryIdsFromICalendars.added.get(   0));
			else                                             id = ((Long) entryIdsFromICalendars.modified.get(0));
			entry = folderModule.getEntry(null, id);
			folderModule.modifyEntry(
				folder.getId(),
				id,
				new MapInputData(inputData), 
		    	fileItems,
		    	new ArrayList<String>(),
		    	new HashMap<FileAttachment,String>(),
		    	new HashMap());				
		}
		else {
			// No, we didn't we process a single iCal attachment!
			// Add a new entry for the message.
			entry = folderModule.addEntry(
				folder.getId(),
				((null == def) ? null : def.getId()),
				new MapInputData(inputData),
				fileItems,
				null);
		}
		
		// Do we have an entry we just created?
		if(null != entry) {
			// If it was a MiniBlog entry, update the user's status.
			Principal folderOwner = folder.getOwner();
			if (folderOwner instanceof User) {
				BinderHelper.updateUserStatus(folder.getId(), entry.getId(), (User)folderOwner);
			}
			
			try {
				msg.addHeader(EmailPoster.X_TEAMING_ENTRYID, entry.getId().toString());
			}
			catch(MessagingException ex){}
		}
		msg.setFlag(Flags.Flag.DELETED, true);
	}
	
	/*
	 * Posts and error back to the sender.
	 */
	private Message postError(String recipient, Message msg, InternetAddress from, User postAsUser, Exception error) {
		try {
			msg.setFlag(Flags.Flag.DELETED, true);
			
			// Bugzilla 609799:
			//    Protect against the recipient or from being null.
			String fromUser;
			if (null == from) {
				fromUser = "";
			}
			else {
				fromUser = from.getAddress();
				if (null == fromUser) {
					fromUser = "";
				}
			}
			if (null == recipient) {
				recipient = "";
			}
			
			if ((!(recipient.equals(fromUser)) || (null != postAsUser))) {
				String errMsg = error.getLocalizedMessage();
				if (null == errMsg) {
					errMsg = error.getMessage();
				}
				String errorMsg = NLT.get("errorcode.postMessage.failed", new Object[]{Html.stripHtml(errMsg)});
				Message reject = msg.reply(false);
				reject.setText(errorMsg);
				reject.setFrom(new InternetAddress(recipient));
				reject.setContent(msg.getContent(), msg.getContentType());
				reject.setSubject(errorMsg + " (" + reject.getSubject() + ")"); 
				return reject;
			} 
		}
		catch (Exception ex) {}
		
		return null;
	}
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected boolean isReply(Folder folder, String title, Message msg) {
		// See if for this folder.
		if (!(title.startsWith(MailModule.REPLY_SUBJECT + folder.getId().toString() + ":"))) {
			return false;
		}
		return true;
	}
	
	/*
	 * Override to provide alternate processing 
	 */
	protected Long getParentDocId(Folder folder, String title, Message msg) {
		String flag = (MailModule.REPLY_SUBJECT + folder.getId().toString() + ":");
		
		// docId encoded in subject line.
		String docId = title.substring(flag.length());
		int index = docId.indexOf(" ");
		if ((-1) == index) {
			return Long.valueOf(docId);
		}
		return Long.valueOf(docId.substring(0, index));
	}
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected Definition getReplyDefinition(Folder folder, Long parentDocId) {
		// Let the folderModule figure it out.  This works as long as
		// iCal processing is not done on replies.
		return null;
	}
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected Definition getEntryDefinition(Folder folder) {
		// If not defined, let folderModule figure it out.
		if(null == folder.getPosting()) {
			return null;
		}
		
		return  folder.getPosting().getDefinition();
	}
	
	/*
	 */
	protected User setUser(Folder folder, InternetAddress from) {
		// Try to map email address to a user.
		String fromEmail = from.getAddress();	
		List<Principal> ps = getProfileDao().loadPrincipalByEmail(fromEmail, null, RequestContextHolder.getRequestContext().getZoneId());
		User user = null;
		for (Principal p:  ps) {
            // Make sure it is a user.
            try {
            	User principal = ((User) getProfileDao().loadUser(p.getId(), RequestContextHolder.getRequestContext().getZoneId()));
            	if (user == null) {
            		user = principal;
            	}
            	else if (!(principal.equals(user))) {
        			logger.error("Multiple users with same email address, cannot use for incoming email.");
        			break;
            	}
            }
            catch (Exception ignoreEx) {};  
		}
		
		if (null != user) {
			// Need to setup user context for request.
			RequestContextUtil.setThreadContext(user).resolve();
			return user;
		}
		
		return null;
	}

	/*
	 */
	protected User setAnonymousUser(Folder folder) {
		User user = getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, RequestContextHolder.getRequestContext().getZoneId());
		RequestContextUtil.setThreadContext(user).resolve();
		return user;
	}
	
	/*
	 * Override to provide alternate processing 
	 */
	protected void processPart(Folder folder, Part part, Map inputData, Map fileItems, List iCalendars, DescInfo descInfo) throws MessagingException, IOException {
		try {
			descInfo.partEntry();
			if (part.isMimeType(MailModule.CONTENT_TYPE_CALENDAR)) {
				processICalendar(folder, part.getContent(), iCalendars);
			}
			else { 
				// Old mailers may not use disposition, and instead put
				// the name in the content-m_type java mail handles this.
				String fileName = part.getFileName();
				if (Validator.isNotNull(fileName) && (!(part.isMimeType("text/html")) && (part instanceof Message || part instanceof MimeBodyPart))) {
					fileItems.put(ObjectKeys.INPUT_FIELD_ENTITY_ATTACHMENTS + Integer.toString(fileItems.size() + 1), new FileHandler(part));
				}
				else if (part.isMimeType("text/html")) {
					processHTML(folder, part.getContent(), inputData, descInfo);
				}
				else if (part.isMimeType("text/plain")) {
					processText(folder, part.getContent(), inputData, descInfo);
//!				}
//!				else if (m_part.isMimeType("message/rfc822")) {
//!					// Ignore for now.  We'll address these when bug
//!					// 566222 gets fixed.
				}
				else {
					Object bContent = part.getContent();
					if (bContent instanceof MimeMultipart) {
						processMultiPart(folder, ((MimeMultipart) bContent), inputData, fileItems, iCalendars, descInfo);
					}
					else if (bContent instanceof Part) {
						// Forwarded messages.
						processPart(folder, ((Part) bContent), inputData, fileItems, iCalendars, descInfo);
					}
					else if (part.getContentType().startsWith("image/")) {
						// No file name, no text/HTML,no text/plain, no
						// multipart.  So check if it's inline image -
						// this pattern is used by GroupWise (tested
						// with 7.0.2)
						fileItems.put(ObjectKeys.INPUT_FIELD_ENTITY_ATTACHMENTS + Integer.toString(fileItems.size() + 1), new FileHandler(part));
					}
				}
			}
		}
		finally {
			descInfo.partExit();
		}
	}	
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected void processMultiPart(Folder folder, MimeMultipart content, Map inputData, Map fileItems, List iCalendars, DescInfo descInfo) throws MessagingException, IOException {
		try {
			descInfo.multiPartEntry();
			int count = content.getCount();
			for (int i = 0; i < count; i += 1) {
				BodyPart part = content.getBodyPart(i);
				Object bContent = part.getContent();
				if (bContent instanceof MimeMultipart)
				     processMultiPart(folder, ((MimeMultipart) bContent), inputData, fileItems, iCalendars, descInfo);
				else processPart(     folder, part,                       inputData, fileItems, iCalendars, descInfo);
			}
		}
		finally {
			descInfo.multiPartExit();
		}
	}

	
	/*
	 * Override to provide alternate processing. 
	 */
	protected void processText(Folder folder, Object content, Map inputData, DescInfo descInfo) {
		if (descInfo.saveDesc(DescInfo.Type.PLAIN)) {
			String[] val = new String[1];
			String text = (String)content;
			text = text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			
			// Get the body text and turn it into HTML.
			TextToHtml textToHtml = new TextToHtml();
			
			// Should we add breaks where the text has a CR/LF?
			boolean breakOnLines = Boolean.valueOf(SPropsUtil.getString("mail.incoming.text.messages.breakOnLines", "false").trim());
			textToHtml.setBreakOnLines(breakOnLines);
			textToHtml.setStripHtml(false);
			textToHtml.parseText(text);
			val[0] = textToHtml.toString();
			inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, val);			
			inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf(Description.FORMAT_HTML));
			descInfo.descSaved(DescInfo.Type.PLAIN);
		}
	}
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected void processHTML(Folder folder, Object content, Map inputData, DescInfo descInfo) {
		if (descInfo.saveDesc(DescInfo.Type.HTML)) {
			String[] val = new String[1];
			if (!((String)content).equals("")) {
				//Check that this text is legal HTML
				DefinitionModule definitionModule = ((DefinitionModule) SpringContextUtil.getBean("definitionModule"));
				Description desc = new Description((String)content, Description.FORMAT_HTML);
				EntryDataErrors entryDataErrors = new EntryDataErrors();
				definitionModule.tidyCheckText(desc, entryDataErrors);
				if (!entryDataErrors.getProblems().isEmpty()) {
					//The HTML was invalid, so try adding it as text
					//This is done by converting the HTML to text and the converting that text back to HTML
					//Get the body text and convert it to text and then turn it back into html. This should get rid of the bad HTML
					TextToHtml textToHtml = new TextToHtml();
					textToHtml.setBreakOnLines(true);
					textToHtml.setStripHtml(false);
					desc.setText((String)content);
					String text = desc.getStrippedText(true);
					text += "\n\n" + NLT.get("email.badHTML");
					textToHtml.parseText(text);
					val[0] = textToHtml.toString();
				} else {
					val[0] = (String)content;
				}
			} else {
				val[0] = (String)content;
			}
			inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, val);			
			inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf(Description.FORMAT_HTML));
			descInfo.descSaved(DescInfo.Type.HTML);
		}
	}	
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected void processICalendar(Folder folder, Object content, List iCalendars) throws IOException {
		try {
			iCalendars.add((InputStream)content);
		}
		catch (ClassCastException e) {
			// ignore
		}
	}
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected AttendedEntries processICalAttachments(Folder folder, Definition def, Map inputData, Map fileItems, List iCalendars) {
		AttendedEntries entryIdsFromICalendars = new AttendedEntries();
		Iterator fileItemsIt = fileItems.entrySet().iterator();
		while (fileItemsIt.hasNext()) {
			Map.Entry	me          = ((Map.Entry) fileItemsIt.next());
			FileHandler	fileHandler = ((FileHandler) me.getValue());
			
			if ((!(fileHandler.getOriginalFilename() != null && fileHandler.getOriginalFilename().toLowerCase().endsWith(MailModule.ICAL_FILE_EXTENSION))) &&
				(!(fileHandler.getContentType()      != null && fileHandler.getContentType().toLowerCase().startsWith(   MailModule.CONTENT_TYPE_CALENDAR)))) {
				continue;
			}
			
			try {
				AttendedEntries entryIds = getIcalModule().parseToEntries(folder, def, fileHandler.getInputStream(), inputData);
				entryIdsFromICalendars.addAll(entryIds);
				if (!(entryIds.isEmpty())) {
					fileItemsIt.remove();
				}
			}
			catch (Exception e) {
				// Can't import iCal!  Ignore error, it's probably
				// wrong file format.
				logger.warn(e);
			}
		}
		return entryIdsFromICalendars;
	}
	
	/*
	 * Override to provide alternate processing. 
	 */
	protected AttendedEntries processICalInline(Folder folder,  Definition def, Map inputData, Map fileItems, List iCalendars) {
		// Process inline iCals.
		AttendedEntries entryIdsFromICalendars = new AttendedEntries();
		Iterator icalIt = iCalendars.iterator();
		while (icalIt.hasNext()) {
			InputStream icalStream = ((InputStream) icalIt.next());
			try {
				AttendedEntries entryIds = getIcalModule().parseToEntries(folder, def, icalStream, inputData);
				entryIdsFromICalendars.addAll(entryIds);
				if (!(entryIds.isEmpty())) {
					icalIt.remove();
				}								
			}
			catch (Exception e) {
				// Can't import iCal!  Ignore error, it's probably
				// wrong file format.
				logger.warn(e);
			}
		}
		return entryIdsFromICalendars;
		
	}
	
	/*
	 * Given a Map of FileHandler's, ensure's that they're all
	 * referring to unique filenames.
	 */
	private static void uniquifyFileItems(Map fileItems) {
		// If there's nothing in the Map...
		if ((null == fileItems) || fileItems.isEmpty()) {
			// ...there's nothing to make unique.
			return;
		}
		
		// Scan the Map's keys.
		Object[] keys = fileItems.keySet().toArray();
		int c = keys.length;
		for (int i = 0; i < (c - 1); i += 1) {
			// Extract this FileHandler's filename.
			FileHandler fh1 = ((FileHandler) fileItems.get(keys[i]));
			String fn1 = fh1.getOriginalFilename();
			
			// Scan the Map's keys below the current one.
			for (int j = (i + 1); j < c; j += 1) {
				// Extract this FileHandler's filename.
				FileHandler fh2 = ((FileHandler) fileItems.get(keys[j]));
				String fn2 = fh2.getOriginalFilename();
				
				// Is the 2nd filename a duplicate of the 1st?
				if (fn1.equalsIgnoreCase(fn2)) {
					// Yes!  We need to make it unique.  We do this by
					// appending an '_n' on the end of the filename
					// m_part of the name.
					int pPos = fn2.lastIndexOf(".");
					if ((-1) == pPos) {
						fh2.m_fileName = (fn2 + "_" + j);
					}
					else {
						String p1 = fn2.substring(0, pPos);
						String p2 = fn2.substring(   pPos);
						fh2.m_fileName = (p1 + "_" + j + p2);
					}
				}
			}
		}
	}
}
