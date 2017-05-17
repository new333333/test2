/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.mail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.util.Calendars;

import org.apache.commons.logging.Log;

import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EmailLog;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.module.definition.notify.Notify;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TextToHtml;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Validator;

import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class MimeNotifyPreparator extends AbstractMailPreparator {
	EmailFormatter processor;
	Binder binder;
	Collection<String> ccAddrs;
	Collection<String> bccAddrs;
	Collection<String> toAddrs;
	Collection entries;
	Entry entry;
	Notify.NotifyType messageType;
	Date startDate;
	Locale locale;
	TimeZone timezone;
	boolean sendAttachments=false;
	boolean redacted=false;
	boolean sendVTODO;
	IcalModule icalModule;
	Notify notify;
	EmailLog emailLog;
	String entryPermalinkUrl;
	String rootPermalinkUrl;
	
	/**
	 * Class constructor.
	 * 
	 * @param processor
	 * @param binder
	 * @param startDate
	 * @param logger
	 * @param sendVTODO
	 */
	public MimeNotifyPreparator(EmailFormatter processor, Binder binder, Date startDate, Log logger, boolean sendVTODO) {
		super(logger);
		this.processor = processor;
		this.binder = binder;
		this.startDate = startDate;	
		this.sendVTODO = sendVTODO;
		icalModule = (IcalModule)SpringContextUtil.getBean("icalModule");
	}
	
	/**
	 * Stores the CC: addresses to send to.
	 * 
	 * @param ccAddrs
	 */
	public void setCcAddrs(Collection<String> ccAddrs) {
		this.ccAddrs = MiscUtil.validateEmailAddressCollection(MailModule.CC, ccAddrs);			
	}
	
	/**
	 * Stores the BCC: addresses to send to.
	 * 
	 * @param bccAddrs
	 */
	public void setBccAddrs(Collection<String> bccAddrs) {
		this.bccAddrs = MiscUtil.validateEmailAddressCollection(MailModule.BCC, bccAddrs);			
	}
	
	/**
	 * Stores the TO: addresses to send to.
	 * 
	 * @param toAddrs
	 */
	public void setToAddrs(Collection<String> toAddrs) {
		this.toAddrs = MiscUtil.validateEmailAddressCollection(MailModule.TO, toAddrs);			
	}

	/**
	 * Stores the entry the notification is being sent about.
	 * 
	 * @param entry
	 */
	public void setEntry(Entry entry) {
		this.entry = entry;		
	}
	
	/**
	 * Stores the email log object that is being built for this.
	 * 
	 * @param entry
	 */
	public void setEmailLog(EmailLog emailLog) {
		this.emailLog = emailLog;		
	}
	
	/**
	 * Stores the start date of the notification.
	 * 
	 * @param startDate
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Stores whether or not attachments are to be included in the
	 * notification.
	 * 
	 * @param sendAttachments
	 */
	public void setSendAttachments(boolean sendAttachments) {
		this.sendAttachments = sendAttachments;
	}
	
	/**
	 * Stores whether or not the from address is supposed to be hidden.
	 * 
	 * @param redacted
	 */
	public void setRedacted(boolean redacted) {
		this.redacted = redacted;
	}
	
	/**
	 * Stores a Collection of entries the notification is being sent
	 * about.
	 * 
	 * @param entries
	 */
	public void setEntries(Collection entries) {
		this.entries = entries;
	}
	
	/**
	 * Stores the locale the notification is to be sent in.
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	/**
	 * Stores the time zone the notification is to be sent from.
	 * 
	 * @param timezone
	 */
	public void setTimeZone(String timezone) {			
		this.timezone = TimeZoneHelper.getTimeZone(timezone);
	}
	
	/**
	 * Stores the type of notification to be sent.
	 * 
	 * @param type
	 */
	public void setType(Notify.NotifyType type) {
		messageType = type;
	}
	
	/**
	 * Stores the entry permalink URL to send as the X-Vibe-OnPrem.
	 * 
	 * @param entryPermalinkUrl
	 */
	public void setEntryPermalinkUrl(String entryPermalinkUrl) {
		this.entryPermalinkUrl = mimeEncodePermalinkUrl(entryPermalinkUrl); 
	}
	
	/**
	 * Stores the root permalink URL to send as the X-RootVibe-OnPrem.
	 * 
	 * @param rootPermalinkUrl
	 */
	public void setRootPermalinkUrl(String rootPermalinkUrl) {
		this.rootPermalinkUrl = mimeEncodePermalinkUrl(rootPermalinkUrl); 
	}
	
	/*
	 * Uses MimeUtil.encodeText() to encode a permalink URL for
	 * inclusion as part of an email header.
	 */
	private String mimeEncodePermalinkUrl(String permalinkUrl) {
		String reply;
		try {
			reply = MimeUtility.encodeText(permalinkUrl);
		}
		catch (UnsupportedEncodingException uee) {
			logger.error("MimeNotifyPreparotor.mimeEncodePermalinkUrl( EXCEPTION ):  ", uee);
			reply = null;
		}
		return reply;
	}
	
	/**
	 * Stores the subject to include in the notification.
	 * 
	 * @param helper
	 * 
	 * @throws MessagingException
	 */
	protected void setSubject(MimeMessageHelper helper) throws MessagingException {
		helper.setSubject(processor.getSubject(binder, entry, notify));
	}
	
	/*
	 */
	protected void setFrom(MimeMessageHelper helper) throws MessagingException {
		String from = processor.getFrom(binder, notify);
		if (Validator.isNull(from)) {
			from = defaultFrom;
		}
		helper.setFrom(from);
	}
	
	/*
	 */
	protected void setCcAddrs(MimeMessageHelper helper) throws MessagingException {
		if (null != ccAddrs) {
			//Using 1 set results in 1 TO: line in mime-header - GW like this better
			helper.setCc(ccAddrs.toArray(new String[ccAddrs.size()]));
		}
	}
	
	/*
	 */
	protected void setBccAddrs(MimeMessageHelper helper) throws MessagingException {
		if (null != bccAddrs) {
			//Using 1 set results in 1 TO: line in mime-header - GW like this better
			helper.setBcc(bccAddrs.toArray(new String[bccAddrs.size()]));
		}
	}
	
	/*
	 */
	protected void setToAddrs(MimeMessageHelper helper) throws MessagingException {
		if (null != toAddrs) {
			//Using 1 set results in 1 TO: line in mime-header - GW like this better
			helper.setTo(toAddrs.toArray(new String[toAddrs.size()]));
		}
	}
	
	/**
	 * Constructs the MimeMessage to be sent.
	 * 
	 * @param mimeMessage
	 */
	@Override
	public void prepare(MimeMessage mimeMessage) throws MessagingException {
		// Make sure nothing saved yet.
		notify = new Notify(messageType, locale, timezone, startDate);
		notify.setRedacted(redacted);
		
		// This needs to be set true, initially if sendAttachments is
		// true so that the call to processor.build() below properly
		// sets up the attachments set in notify.
		notify.setAttachmentsIncluded(sendAttachments);
				
		message = null;
		Map result=null;

		// If we're not sending a text message...
		boolean textMessage = messageType.equals(Notify.NotifyType.text);
		if (!textMessage) {
			// ...build the message as appropriate for the entry or
			// ...entries.
			if (entry != null)
			     result = processor.buildMessage(binder, entry,   notify);
			else result = processor.buildMessage(binder, entries, notify);
		}

		// Now, set notify's attachments included flag false, even if
		// sendAttachements is true if there are aren't any actual
		// attachments.
		Set<FileAttachment> faSet = notify.getAttachments();
		boolean hasFAs = MiscUtil.hasItems(faSet);
		notify.setAttachmentsIncluded(sendAttachments && hasFAs);
		
		// Set up events here.
		int multipartMode = MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;
		if (notify.getEvents() != null && notify.getEvents().entrySet().size() > 0) {
			// Need to attach iCalendar as alternative content.  If
			// there is more then one iCal then, all are merged and
			// add ones to email as alternative content.
			multipartMode = MimeMessageHelper.MULTIPART_MODE_MIXED;
		}

		// Set any X-* fields required for GW integration.
		if (MiscUtil.hasString(entryPermalinkUrl)) mimeMessage.addHeader(MailModule.HEADER_X_VIBE_ONPREM,     entryPermalinkUrl);
		if (MiscUtil.hasString(rootPermalinkUrl )) mimeMessage.addHeader(MailModule.HEADER_X_ROOTVIBE_ONPREM, rootPermalinkUrl );
		mimeMessage.addHeader(
			MailModule.HEADER_X_NOVELL_PRODUCT,
			(Utils.checkIfFilr()                        ?
				MailModule.HEADER_X_NOVELL_PRODUCT_FILR :
				MailModule.HEADER_X_NOVELL_PRODUCT_VIBE));

		// Do we have an Entry for this mimeMessage?
		if (null != entry) {
			// Yes!  Use it to construct a Message-ID header for the
			// message.
			String vibeMessageId = buildMessageId(entry);
			if (mimeMessage instanceof VibeMimeMessage) {
				((VibeMimeMessage) mimeMessage).setVibeMessageId(vibeMessageId);
			}
			mimeMessage.addHeader(MailModule.HEADER_MESSAGE_ID, vibeMessageId);
			
			// Is the Entry we've got a FolderEntry?
			if (entry instanceof FolderEntry) {
				// Yes!  Is it a reply to another FolderEntry?
				FolderEntry entryParent = ((FolderEntry) entry).getParentEntry();
				if (null != entryParent) {
					// Yes!  Add an In-Reply-To header for the message
					// this is in reply to.
					mimeMessage.addHeader(MailModule.HEADER_IN_REPLY_TO, buildMessageId(entryParent));
				}
			}
		}

		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipartMode, MailModule.CONTENT_ENCODING);
		mimeMessage.addHeader(MailModule.HEADER_CONTENT_TRANSFER_ENCODING, MailModule.HEADER_CONTENT_TRANSFER_ENCODING_8BIT);
		setSubject(helper);
		setCcAddrs(helper);
		setBccAddrs(helper);
		setToAddrs(helper);
		setFrom(helper);
		
		// Are we sending a text message?
		if (textMessage) {
			// Yes!  We only send a plain text MIME part that contains
			// permalink URLs to the entries we sending notification
			// about.
			StringBuffer ptBuf = new StringBuffer("");
			Collection msgEntries;
			if (entry != null) {
				msgEntries = new ArrayList();
				msgEntries.add(entry);
			} else {
				msgEntries = entries;
			}
			if ((null != msgEntries) && (!(msgEntries.isEmpty()))) {
				int count = 0;
				Iterator itMsgEntries = msgEntries.iterator();
				while (itMsgEntries.hasNext()) {
					Entry nextEntry = (Entry) itMsgEntries.next();
					if (0 < count) {
						ptBuf.append("\r\n");
					}
					count++;
					String[] txtMsgArgs = new String[3];
					String txtMsgString;
					if (nextEntry.getCreation().getDate().before(nextEntry.getModification().getDate())) {
						txtMsgArgs[0] = nextEntry.getCreation().getPrincipal().getTitle();
						txtMsgString = "email.textMessageFormatModified";
					} else {
						txtMsgArgs[0] = nextEntry.getModification().getPrincipal().getTitle();
						txtMsgString = "email.textMessageFormatAdded";
					}
					txtMsgArgs[1] = nextEntry.getTitle();
					String path = "";
					Binder parent = nextEntry.getParentBinder();
					while (parent != null && parent instanceof Folder) {
						if (!path.equals("")) path = "/" + path;
						path = parent.getTitle() + path;
						parent = parent.getParentBinder();
					}
					if (parent != null) {
						//Add in the first workspace above the folder
						path = parent.getTitle() + "/" + path;
					}
					txtMsgArgs[2] = path;
					ptBuf.append(NLT.get(txtMsgString, txtMsgArgs));
				}
			}
			
			String ptStr = ptBuf.toString();
			//Get the body text and turn it into html
			TextToHtml textToHtml = new TextToHtml();
			textToHtml.setBreakOnLines(true);
			textToHtml.setStripHtml(false);
			textToHtml.parseText(ptStr);
			String hStr = EmailUtil.validateHTMLForEmail((textToHtml.toString()));
			String subject = mimeMessage.getSubject();
			if ((subject.length() + ptStr.length()) >= 156) {
				//This message is longer than the 160 characters allowed in a text message, truncate it.
				ptStr = ptStr.substring(0, 155-subject.length()) + "...";
			}
			setText(ptStr, hStr, helper);
			
		} else {
			// No, we aren't sending a text message!  Use MailHelper so
			// the alternative part get added for calendars.
			setText(
				((String) result.get(EmailFormatter.TEXT)),
				((String) result.get(EmailFormatter.HTML)),
				helper);
			
			// Are we supposed to send attachments and are there any
			// attachments to send?
			if (sendAttachments && hasFAs) {
				// Yes!  Prepare and log them.
				prepareAttachments(faSet, helper);
				logFileAttachments(faSet        );
			}
			notify.clearAttachments();
			
			// Prepare any iCal's required because the entry is
			// an event.
			prepareICalendars(helper);
		}
		
		// Finally, save the message in case we cannot connect and need
		// to re-send it.
		message = mimeMessage;
	}

	/*
	 * Returns a MimeMessage compatible Message-ID based on an entry
	 * ID.
	 * 
	 * Format:  <entry-id.modification-date-time@host>
	 */
	private static String buildMessageId(Entry entry) {
		HistoryStamp entryStamp = entry.getModification();
		if (null == entryStamp) {
			entryStamp = entry.getCreation();
		}
		Date entryDate;
		if (null == entryStamp)
		     entryDate = new Date();
		else entryDate = entryStamp.getDate();
		
		String msgDate = String.valueOf(entryDate.getTime());		
		String entryId = String.valueOf(entry.getId().longValue());
		return ("<" + entryId + "." + msgDate + "@" + SPropsUtil.getDefaultHost() + ">");
	}
	
	/*
	 */
	protected void prepareICalendars(MimeMessageHelper helper) throws MessagingException {
		int c = 0;
		int eventsSize = notify.getEvents().size();
		Calendar margedCalendars = null;
		for (Iterator entryEventsIt = notify.getEvents().entrySet().iterator(); entryEventsIt.hasNext();) { 
			Map.Entry mapEntry = ((Map.Entry) entryEventsIt.next());
			DefinableEntity entry = ((DefinableEntity) mapEntry.getKey());
			Collection events = ((Collection) mapEntry.getValue());
			Calendar iCal = icalModule.generate(entry, events, notify.getTimeZone().getID());
			
			String fileName = (entry.getTitle() + MailModule.ICAL_FILE_EXTENSION);
			if (eventsSize > 1) {
				fileName = (entry.getTitle() + c + MailModule.ICAL_FILE_EXTENSION);
			}
			
			// If OK to send TODO or not a TODO build alternative.
			String component = getICalComponentType(iCal);
			if (sendVTODO || !Component.VTODO.equals(component)) {
				// Attach alternative iCalendar content.
				if (eventsSize == 1 && messageType.includeICalAsAlternative()) {
					// Always send as attachment and alternative text.
					prepareICalendar(iCal, fileName, component, true, true, helper);
				}
				
				else  {
					// Always send as attachment, not alternative.
					prepareICalendar(iCal, fileName, component, true, false, helper);
					if (eventsSize > 1) {
						if (margedCalendars == null) {
							margedCalendars = new Calendar();
						}
						margedCalendars = Calendars.merge(margedCalendars, iCal);
					}
				}
			}
			
			else {
				// Always send as attachment, not alternative.
				prepareICalendar(iCal, fileName, component, true, false, helper);
			}
			
			c += 1;
		}
		
		if (margedCalendars != null) {
			// Add to alternative text, attachments handled already.
			prepareICalendar(
				margedCalendars,
				(binder.getTitle() + MailModule.ICAL_FILE_EXTENSION),
				getICalComponentType(margedCalendars),
				false,
				true,
				helper);
		}
		
		notify.clearEvents();
	}
	
	private void logFileAttachments(Set<FileAttachment> fileAttachments) {
		List<String> fileNames = new ArrayList<String>();
		if (Utils.testSendMailAttachmentsSize(fileAttachments)) {
			for (FileAttachment fAtt: fileAttachments) {
				if (Utils.testSendMailAttachmentSize(fAtt)) {
					fileNames.add(fAtt.getFileItem().getName());
				}
			}
		}
		if (emailLog != null) {
			emailLog.setFileAttachments(fileNames);
		}
	}
}
