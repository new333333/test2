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
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.StringUtil;

/**
 * This class represents an email log entry.
 * 
 * @author ?
 */
public class EmailLog extends ZonedObject {
	private static final Log logger = LogFactory.getLog(EmailLog.class);
	
	private int maxFromAddressLength = 255;
	private int maxSubjLength = 255;
	
	protected String id;										
	
	protected Date sendDate;									//Date mail was sent
	protected String from;										//Email address of sender
	protected String subj;										//Subject line from mail message
	protected String comments;									//Error message (if any)
	protected EmailLogType type = EmailLogType.unknown;			//Type of mail
	protected EmailLogStatus status = EmailLogStatus.unknown;	//sent, queued, error
	protected String[] toEmailAddresses;						//comma separated list of email addresses
	protected String[] fileAttachments;							//comma separated list of file names

	public enum EmailLogType {
		unknown,
		sendMail,
		binderNotification, 
		workflowNotification,
		retry,
		emailPosting
	};
	
	public enum EmailLogStatus {
		unknown,
		sent,
		queued, 
		error,
		received
	};
	
	// Applications use this constructor
	public EmailLog(EmailLogType type, Date sendDate, Collection<InternetAddress> toEmailAddresses, 
			InternetAddress from, EmailLogStatus status) {
		this.type = type;
		this.sendDate = sendDate;
		if (from != null) {
			this.from = from.getAddress();
		} else {
			this.from = "";
		}
		this.toEmailAddresses = new String[toEmailAddresses.size()];
		int i = 0;
		for (InternetAddress address : toEmailAddresses) {
			this.toEmailAddresses[i] = address.getAddress();
			i++;
		}
		this.status = status;
	}
	
	public EmailLog(EmailLogType type, Date sendDate, Collection<String> toEmailAddresses, 
			String from, EmailLogStatus status) {
		this.type = type;
		this.sendDate = sendDate;
		this.from = from;
		this.toEmailAddresses = new String[toEmailAddresses.size()];
		int i = 0;
		for (String address : toEmailAddresses) {
			this.toEmailAddresses[i] = address;
			i++;
		}
		this.status = status;
	}
	
	public EmailLog(EmailLogType type, Date sendDate, Address[] toEmailAddresses, 
			String from, EmailLogStatus status) {
		this.type = type;
		this.sendDate = sendDate;
		this.from = from;
		this.toEmailAddresses = new String[toEmailAddresses.length];
		for (int i = 0; i < toEmailAddresses.length; i++) {
			this.toEmailAddresses[i] = toEmailAddresses[i].toString();
		}
		this.status = status;
	}
	
	public EmailLog(EmailLogType type, MimeMessage mailMsg, EmailLogStatus status) {
 		Date sendDate = new Date();
		this.type = type;
		this.sendDate = sendDate;
		this.status = status;
		this.fillFromMimeMessage(mailMsg);
	}
	
	public EmailLog(EmailLogType type, EmailLogStatus status) {
 		Date sendDate = new Date();
		this.type = type;
		this.sendDate = sendDate;
		this.status = status;
	}
	
	// This constructor is reserved for use by Hibernate only.
	protected EmailLog() {}
	
	/**
	 * Fill fields from mime message
	 * 
	 * @param mailMsg
	 */
	public void fillFromMimeMessage(MimeMessage mailMsg) {
		// Does the email log contain an from address?
  		String fromAddress = this.getFrom();
  		if (!(MiscUtil.hasString(fromAddress))) {
  			// No!  Extract it from the mime and store it in the log.
			try {
				Address[] fromAdrs = mailMsg.getFrom();
				if ((null != fromAdrs) && (0 < fromAdrs.length)) {
					fromAddress = fromAdrs[0].toString();
				}
			}
			catch (MessagingException e2) {}
	  		if (!(MiscUtil.hasString(fromAddress))) {
				fromAddress = NLT.get("mail.noFromAddress");
	  		}
			this.setFrom(fromAddress);
  		}
  		
		// Does the email log contain any recipients?
  		if (!(MiscUtil.hasString(this.getToEmailAddressesStr()))) {
  			// No!  Extract them from the mime and store them in the
  			// log.
			List<String> toAddressList = new ArrayList<String>();
			try {
				Address[] toAdrs = mailMsg.getAllRecipients();
				if (toAdrs != null && toAdrs.length > 0) {
					for (Address toAdr:  toAdrs) {
						toAddressList.add(toAdr.toString());
					}
				}
			}
			catch (MessagingException e2) {}
			if (toAddressList.isEmpty()) {
				toAddressList.add(NLT.get("mail.noToAddress"));
			}
			this.setToEmailAddresses(toAddressList);
  		}

		// Does the email log contain a subject?
  		String subj = this.getSubj();
  		if (!(MiscUtil.hasString(subj))) {
  			// No!  Extract it from the mime and store it in the log.
	  		try {
				subj = mailMsg.getSubject();
			}
	  		catch (MessagingException e1) {}
	  		if (!(MiscUtil.hasString(subj))) {
	  			subj = NLT.get("mail.noSubject");
	  		}
			this.setSubj(subj);
  		}

		// Does the email log contain any file attachment names?
  		String fileAttachmentNames = this.getFileAttachmentsStr();
  		if (!(MiscUtil.hasString(fileAttachmentNames))) {
  			// No!  Extract any from the mime and store them in the
  			// log.
			try {
				fileAttachmentNames = mailMsg.getFileName();
			} catch (MessagingException e) {}
	  		if (MiscUtil.hasString(fileAttachmentNames)) {
	  			this.setFileAttachmentsStr(fileAttachmentNames);
	  		}
  		}
	}

    public String getId() {
        return id;
    }
    protected void setId(String id) {
        this.id = id;
    }

	public EmailLogType getType() {
		return type;
	}
	public void setType(EmailLogType type) {
		this.type = type;
	}
	public Date getSendDate() {
		return sendDate;
	}
	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}
	public String[] getToEmailAddresses() {
		return toEmailAddresses;
	}
	public List<String> getToEmailAddressesAsList() {
		if(toEmailAddresses == null)
			return new ArrayList<String>();
		else
			return Arrays.asList(toEmailAddresses);
	}
	public void setToEmailAddresses(String[] toEmailAddresses) {
		this.toEmailAddresses = toEmailAddresses;
	}
	public void setToEmailAddresses(List<String> toEmailAddresses) {
		if(toEmailAddresses == null)
			this.toEmailAddresses = null;
		else
			this.toEmailAddresses = toEmailAddresses.toArray(new String[]{});
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		if (from.length() > maxFromAddressLength) {
			this.from = from.substring(0, maxFromAddressLength - 4) + "...";
		} else {
			this.from = from;
		}
	}
	public String getSubj() {
		return subj;
	}
	public void setSubj(String subj) {
		if(subj == null)
			subj = "";
		String subj2 = StringUtil.definedUnicode(subj);
		if(logger.isDebugEnabled()) {
			if(subj.length() != subj2.length())
				logger.debug((subj.length()-subj2.length()) + " unsafe characters removed from original subj size of " + subj.length());
		}
		if (subj2.length() > maxSubjLength) {
			this.subj = subj2.substring(0, maxSubjLength - 4) + "...";
		} else {
			this.subj = subj2;
		}
	}
	public EmailLogStatus getStatus() {
		return status;
	}
	public void setStatus(EmailLogStatus status) {
		this.status = status;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String[] getFileAttachments() {
		return fileAttachments;
	}
	public List<String> getFileAttachmentsAsList() {
		if(fileAttachments == null)
			return new ArrayList<String>();
		else
			return Arrays.asList(fileAttachments);
	}
	public void setFileAttachments(String[] fileAttachments) {
		this.fileAttachments = fileAttachments;
	}
	public void setFileAttachments(List<String> fileAttachments) {
		if(fileAttachments == null)
			this.fileAttachments = null;
		else
			this.fileAttachments = fileAttachments.toArray(new String[]{});
	}

	/// Begin Persistence - The methods in this section are used only by Hibernate for persistence.
	protected String getTypeStr() {
		if(type == null)
			return EmailLogType.unknown.name();
		else
			return type.name();
	}
	protected void setTypeStr(String typeStr) {
		type = EmailLogType.valueOf(typeStr);
	}
	
	protected String getStatusStr() {
		if(status == null)
			return EmailLogStatus.unknown.name();
		else
			return status.name();
	}
	protected void setStatusStr(String statusStr) {
		status = EmailLogStatus.valueOf(statusStr);
	}
	
	protected String getToEmailAddressesStr() {
		// Return a comma separated list of email addresses for storage
		if(toEmailAddresses == null)
			return null;
		else
			return StringUtil.merge(toEmailAddresses);
	}
	
	protected void setToEmailAddressesStr(String toEmailAddressesStr) {
		// Receive a command separated list of email addresses from storage
		if(toEmailAddressesStr == null)
			toEmailAddresses = null;
		else
			toEmailAddresses = StringUtil.split(toEmailAddressesStr);
	}
	
	protected String getFileAttachmentsStr() {
		// Return a comma separated list of file names for storage
		if(fileAttachments == null)
			return null;
		else
			return StringUtil.merge(fileAttachments);
	}
	
	protected void setFileAttachmentsStr(String fileAttachmentsStr) {
		// Receive a command separated list of file names from storage
		if(fileAttachmentsStr == null)
			fileAttachments = null;
		else
			fileAttachments = StringUtil.split(fileAttachmentsStr);
	}
	/// End Persistence
}
