package org.kablink.teaming.module.mail.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
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
import javax.mail.internet.MimeMultipart;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.RequestContextUtil;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.AttendedEntries;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.mail.EmailPoster;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.util.NLT;
import org.kablink.util.Html;
import org.kablink.util.Validator;
import org.springframework.util.FileCopyUtils;


public class DefaultEmailPoster  extends CommonDependencyInjection implements EmailPoster {
    private FolderModule folderModule;
    public void setFolderModule(FolderModule folderModule) {
    	this.folderModule = folderModule;
    }
    public FolderModule getFolderModule() {
    	return this.folderModule;
    }
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	
	public List postMessages(Folder folder, String recipient, Message[] msgs, Session session) {
		//initialize collections
		Map fileItems = new HashMap();
		List iCalendars = new ArrayList();
		Map inputData = new HashMap();
		List errors = new ArrayList();
		InternetAddress from=null;
		
		//save job processing user context
		RequestContext oldCtx = RequestContextHolder.getRequestContext();
		
		for (int i=0; i<msgs.length; ++i) {
			try {
				if (msgs[i].isSet(Flags.Flag.DELETED)) continue;  //groupwise doesn't purge rightaway
				String title = msgs[i].getSubject();
				if (title == null) title = "";
				from = (InternetAddress)msgs[i].getFrom()[0];
				try {
					//save original from
					inputData.put(ObjectKeys.INPUT_FIELD_POSTING_FROM, from.toString()); 
					inputData.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
					if (isReply(folder, title, msgs[i])) {						
						processReply(folder, from, msgs[i], inputData, fileItems, iCalendars);
					} else {
						processEntry(folder, from, msgs[i], inputData, fileItems, iCalendars);
					}
				} finally {
					//reset context
					fileItems.clear();
					inputData.clear();
					iCalendars.clear();
					
				}
			} catch (MessageRemovedException rx) {
				continue;
			} catch (Exception ex) {
				logger.error("Error posting the message from: " + from.toString() + " Error: " + (ex.getLocalizedMessage()==null? ex.getMessage():ex.getLocalizedMessage()));
				//if fails and from self, don't reply or we will get it back
				errors.add(postError(recipient, msgs[i], from, ex));
			} finally {
				RequestContextHolder.setRequestContext(oldCtx);				
			}
		}
		return errors;
	}
	//override to provide alternate processing 
	protected void processReply(Folder folder, InternetAddress from, Message msg, Map inputData, Map fileItems, List iCalendars ) throws Exception {
		String title = (String)inputData.get(ObjectKeys.FIELD_ENTITY_TITLE);
		Long parentDocId = getParentDocId(folder, title, msg);
		FolderEntry entry = getFolderModule().getEntry(folder.getId(), parentDocId);
		User fromUser = setUser(folder, from);
		if (fromUser == null) {
			fromUser = setAnonymousUser(folder);
		} else {
			if (!getFolderModule().testAccess(entry, FolderModule.FolderOperation.addReply)) {
				fromUser = setAnonymousUser(folder);				
			}
		}
		Definition def = getReplyDefinition(folder, parentDocId);
		processPart(folder, msg, inputData, fileItems, iCalendars);
		getFolderModule().addReply(folder.getId(), parentDocId, def == null? null:def.getId(), new MapInputData(inputData), fileItems, null);
		msg.setFlag(Flags.Flag.DELETED, true);
	}
	//override to provide alternate processing 
	protected void processEntry(Folder folder, InternetAddress from, Message msg, Map inputData, Map fileItems, List iCalendars ) throws Exception {
		User fromUser = setUser(folder, from);
		if (fromUser == null) {
			fromUser = setAnonymousUser(folder);
		} else {
			if (!getFolderModule().testAccess(folder, FolderModule.FolderOperation.addEntry)) {
				fromUser = setAnonymousUser(folder);				
			}
		}
		Definition def = getEntryDefinition(folder);
		processPart(folder, msg, inputData, fileItems, iCalendars);
		AttendedEntries entryIdsFromICalendars = new AttendedEntries();
		if (!fileItems.isEmpty()) {
			entryIdsFromICalendars.addAll(processICalAttachments(folder, def, inputData, fileItems, iCalendars));
		}
		if (!iCalendars.isEmpty()) {
			entryIdsFromICalendars.addAll(processICalInline(folder, def, inputData, fileItems, iCalendars));
		}
//DEBUG		java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
//		msg.writeTo(buf);
//		inputData.put("description", buf.toString());
		//IF attachments left or message didn't contain ICALs; add as an entry
		if (!fileItems.isEmpty() || entryIdsFromICalendars.isEmpty()) {
			folderModule.addEntry(folder.getId(), def == null? null:def.getId(), new MapInputData(inputData), fileItems, null);
		}
		msg.setFlag(Flags.Flag.DELETED, true);
	}
	private Message postError(String recipient, Message msg, InternetAddress from, Exception error) {
		try {
			msg.setFlag(Flags.Flag.DELETED, true);
			if (!recipient.equals(from.getAddress())) {
				String errorMsg = NLT.get("errorcode.postMessage.failed", new Object[]{Html.stripHtml((error.getLocalizedMessage()==null? error.getMessage():error.getLocalizedMessage()))});
				Message reject = msg.reply(false);
				reject.setText(errorMsg);
				reject.setFrom(new InternetAddress(recipient));
				reject.setContent(msg.getContent(), msg.getContentType());
				reject.setSubject(reject.getSubject() + " " + errorMsg); 
				return reject;
			} 
		} catch (Exception ex2) {}
		return null;
	}
	//override to provide alternate processing 
	protected boolean isReply(Folder folder, String title, Message msg) {
		//see if for this folder
		if (!title.startsWith(MailModule.REPLY_SUBJECT+folder.getId().toString()+":")) return false;
		return true;
	}
	//override to provide alternate processing 
	protected Long getParentDocId(Folder folder, String title, Message msg) {
		String flag = MailModule.REPLY_SUBJECT+folder.getId().toString()+":";
		//docId encoded in sublect line
		String docId = title.substring(flag.length());
		int index = docId.indexOf(" ");
		if (index == -1) return Long.valueOf(docId);
		return Long.valueOf(docId.substring(0, index));
	}
	//override to provide alternate processing 
	protected Definition getReplyDefinition(Folder folder, Long parentDocId) {
		//Let the folderModule figure it out.  This works as long as ical processing is not done on replies
		return null;
	}
	//override to provide alternate processing 
	protected Definition getEntryDefinition(Folder folder) {
		//if not defined, let folderModule figure it out
		if(folder.getPosting() == null) return null;
		return  folder.getPosting().getDefinition();
	}
	protected User setUser(Folder folder, InternetAddress from) {
		//try to map email address to a user
		String fromEmail = from.getAddress();	
		List<Principal> ps = getProfileDao().loadPrincipalByEmail(fromEmail, RequestContextHolder.getRequestContext().getZoneId());
		User user = null;
		for (Principal p:ps) {
            //Make sure it is a user
            try {
            	User principal = (User)getProfileDao().loadUser(p.getId(), RequestContextHolder.getRequestContext().getZoneId());
            	if (user == null) user = principal;
            	else if (!principal.equals(user)) {
        			logger.error("Multiple users with same email address, cannot use for incoming email");
        			break;
            	}
            } catch (Exception ignoreEx) {};  
		}
		if (user != null) {
			//need to setup user context for request
			RequestContextUtil.setThreadContext(user).resolve();
			return user;
		}
		
		return null;
	}
	protected User setAnonymousUser(Folder folder) {
		User user = getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, RequestContextHolder.getRequestContext().getZoneId());
		RequestContextUtil.setThreadContext(user).resolve();
		return user;
	}
	//override to provide alternate processing 
	protected void processPart(Folder folder, Part part, Map inputData, Map fileItems, List iCalendars) throws MessagingException, IOException {
		if (part.isMimeType(MailModule.CONTENT_TYPE_CALENDAR)) {
			processICalendar(folder, part.getContent(), iCalendars);
		} else { 
			//old mailers may not use disposition, and instead put the name in the content-type
			//java mail handles this.
			String fileName = part.getFileName();
			if (Validator.isNotNull(fileName)) {
				fileItems.put(ObjectKeys.INPUT_FIELD_ENTITY_ATTACHMENTS + Integer.toString(fileItems.size() + 1), new FileHandler(part));
			} else if (part.isMimeType("text/html")) {
				processHTML(folder, part.getContent(), inputData);
			} else if (part.isMimeType("text/plain")) {
				processText(folder, part.getContent(), inputData);
			} else {
				Object bContent = part.getContent();
				if (bContent instanceof MimeMultipart) {
					processMultiPart(folder, (MimeMultipart)bContent, inputData, fileItems, iCalendars);
				} else if (bContent instanceof Part) {
					//forwarded messages
					processPart(folder, (Part)bContent, inputData, fileItems, iCalendars);
				} else if (part.getContentType().startsWith("image/")) {
					// no file name, no text/html,no text/plain, no multipart
					// so check if it's inline image - this pattern is used by GroupWise (tested with 7.0.2)
					fileItems.put(ObjectKeys.INPUT_FIELD_ENTITY_ATTACHMENTS + Integer.toString(fileItems.size() + 1), new FileHandler(part));
				}
			}
		}
	}	
	//override to provide alternate processing 
	protected void processMultiPart(Folder folder, MimeMultipart content, Map inputData, Map fileItems, List iCalendars) throws MessagingException, IOException {
		int count = content.getCount();
		for (int i=0; i<count; ++i ) {
			BodyPart part = content.getBodyPart(i);
			Object bContent = part.getContent();
			if (bContent instanceof MimeMultipart) {
				processMultiPart(folder, (MimeMultipart)bContent, inputData, fileItems, iCalendars);
			} else {
				processPart(folder, part, inputData, fileItems, iCalendars);
			}
		}
	}

	
	//override to provide alternate processing 
	protected void processText(Folder folder, Object content, Map inputData) {
		if (inputData.containsKey(ObjectKeys.FIELD_ENTITY_DESCRIPTION)) return;
		String[] val = new String[1];
		val[0] = (String)content;
		inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, val);			
	}
	//override to provide alternate processing 
	protected void processHTML(Folder folder, Object content, Map inputData) {
		String[] val = new String[1];
		val[0] = (String)content;
		inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, val);			
	}	
	//override to provide alternate processing 
	protected void processICalendar(Folder folder, Object content, List iCalendars) throws IOException {
		try {
			iCalendars.add((InputStream)content);
		} catch (ClassCastException e) {
			// ignore
		}
	}
	//override to provide alternate processing 
	protected AttendedEntries processICalAttachments(Folder folder, Definition def, Map inputData, Map fileItems, List iCalendars) {
		AttendedEntries entryIdsFromICalendars = new AttendedEntries();
		Iterator fileItemsIt = fileItems.entrySet().iterator();
		while (fileItemsIt.hasNext()) {
			Map.Entry me = (Map.Entry)fileItemsIt.next();
			FileHandler fileHandler = (FileHandler)me.getValue();
			
			if ((!(fileHandler.getOriginalFilename() != null && fileHandler.getOriginalFilename().toLowerCase().endsWith(MailModule.ICAL_FILE_EXTENSION))) &&
					(!(fileHandler.getContentType() != null && fileHandler.getContentType().toLowerCase().startsWith(MailModule.CONTENT_TYPE_CALENDAR))) ) {
				continue;
			}
			
			try {
				AttendedEntries entryIds = getIcalModule().parseToEntries(folder, def, fileHandler.getInputStream());
				entryIdsFromICalendars.addAll(entryIds);
				if (!entryIds.isEmpty()) {
					fileItemsIt.remove();
				}
			} catch (Exception e) {
				// can't import ical, ignore error, it's probably wrong file format
				logger.warn(e);
			}
		}
		return entryIdsFromICalendars;
	}
	//override to provide alternate processing 
	protected AttendedEntries processICalInline(Folder folder,  Definition def, Map inputData, Map fileItems, List iCalendars) {
		// process inline iCalendars
		AttendedEntries entryIdsFromICalendars = new AttendedEntries();
		Iterator icalIt = iCalendars.iterator();
		while (icalIt.hasNext()) {
			InputStream icalStream = (InputStream)icalIt.next();
			try {
				AttendedEntries entryIds = getIcalModule().parseToEntries(folder, def, icalStream);
				entryIdsFromICalendars.addAll(entryIds);
				if (!entryIds.isEmpty()) {
					icalIt.remove();
				}								
			} catch (Exception e) {
				// can't import ical, ignore error, it's probably wrong file format
				logger.warn(e);
			}
		}
		return entryIdsFromICalendars;
		
	}
	
	public class FileHandler implements org.springframework.web.multipart.MultipartFile {
		Part part;
		String fileName;
		String type;
		String contentId;
		int size;
		
		public FileHandler(Part part) throws MessagingException {
			this.part = part;
			try {
				fileName = javax.mail.internet.MimeUtility.decodeText(part.getFileName());
			} catch (java.io.UnsupportedEncodingException  nc) {
				throw new MessagingException(nc.getMessage());	
			}
			type = part.getContentType();
			size = part.getSize();
			
			String[] contentIds = part.getHeader("Content-ID");
			if (contentIds != null && contentIds.length > 0 && 
					contentIds[0] != null && contentIds[0].length() > 1) {
				contentId = contentIds[0].substring(1, contentIds[0].length() - 1);
				if (fileName == null) {
					fileName = contentId;
				}
			}
		}
		
		public String getContentId() throws MessagingException {
			return contentId;
		}
		/**
		 * Return the name of the parameter in the multipart form.
		 * @return the name of the parameter
		 */
		public String getName() {return "attachment";}

		/**
		 * Return whether the uploaded file is empty in the sense that
		 * no file has been chosen in the multipart form.
		 * @return whether the uploaded file is empty
		 */
		public boolean isEmpty() {return false;}
		
		/**
		 * Return the original filename in the client's filesystem.
		 * This may contain path information depending on the browser used,
		 * but it typically will not with any other than Opera.
		 * @return the original filename, or null if empty
		 */
		public String getOriginalFilename() {return fileName;}
		
		
		/**
		 * Return the content type of the file.
		 * @return the content type, or null if empty or not defined
		 */
		public String getContentType() {return type;}

		/**
		 * Return the size of the file in bytes.
		 * @return the size of the file, or 0 if empty
		 */
		public long getSize() {return size;}
		
		/**
		 * Return the contents of the file as an array of bytes.
		 * @return the contents of the file as bytes,
		 * or an empty byte array if empty
		 * @throws IOException in case of access errors
		 * (if the temporary store fails)
		 */
		public byte[] getBytes() throws IOException {
			byte [] results = new byte[size];
			try {
				part.getInputStream().read(results);
			} catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage()==null? me.getMessage():me.getLocalizedMessage());
			}
			return results;
		}

		/**
		 * Return an InputStream to read the contents of the file from.
		 * The user is responsible for closing the stream.
		 * @return the contents of the file as stream,
		 * or an empty stream if empty
		 * @throws IOException in case of access errors
		 * (if the temporary store fails)
		 */
		public InputStream getInputStream() throws IOException {
			try {
				return part.getInputStream();
			} catch (MessagingException me) {
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
		 * @param dest the destination file
		 * @throws IOException in case of reading or writing errors
		 * @throws java.lang.IllegalStateException if the file has already been moved
		 * in the filesystem as is not available anymore for another transfer
		*/
		public void transferTo(File dest) throws IOException, IllegalStateException {
			//copied from org.springframework.web.multipart.commons.CommonsMultiPart
//			if (!isAvailable()) {
//				throw new IllegalStateException("File has already been moved - cannot be transferred again");
//			}

			if (dest.exists() && !dest.delete()) {
				throw new IOException(
						"Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
			}

			try {
				
				FileOutputStream out = new FileOutputStream(dest);
				InputStream in = getInputStream();
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
				logger.error("Could not transfer to file", ex);
				throw new IOException("Could not transfer to file: " + (ex.getLocalizedMessage()==null? ex.getMessage():ex.getLocalizedMessage()));
			}
		}
	}
}
