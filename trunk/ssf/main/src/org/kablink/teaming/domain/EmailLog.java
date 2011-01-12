/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.kablink.util.StringUtil;

/**
 * This class represents an email log entry.
 *
 */
public class EmailLog extends ZonedObject {

	protected String id;										
	
	protected Date sendDate;									//Date mail was sent
	protected String from;										//Email address of sender
	protected String subj;										//Subj line from mail message
	protected String comment;									//Error message (if any)
	protected EmailLogType type = EmailLogType.unknown;			//Type of mail
	protected EmailLogStatus status = EmailLogStatus.unknown;	//sent, queued, error
	protected String[] toEmailAddresses;						//comma separated list of email addresses
	protected String[] fileAttachments;							//comma separated list of file names

	public enum EmailLogType {
		unknown,
		sendMail,
		binderImmediateNotification, 
		binderScheduledNotification, 
		workflowNotification
	};
	
	public enum EmailLogStatus {
		unknown,
		sent,
		queued, 
		error
	};
	
	// Applications use this constructor
	public EmailLog(EmailLogType type, Date sendDate, Collection<InternetAddress> toEmailAddresses, 
			InternetAddress from, EmailLogStatus status) {
		this.type = type;
		this.sendDate = sendDate;
		this.from = from.getAddress();
		this.toEmailAddresses = new String[toEmailAddresses.size()];
		Set<String> emailAddresses = new HashSet<String>();
		int i = 0;
		for (InternetAddress address : toEmailAddresses) {
			this.toEmailAddresses[i] = address.getAddress();
			i++;
		}
		this.status = status;
		this.from = from.getAddress();
	}
	
	// This constructor is reserved for use by Hibernate only.
	protected EmailLog() {}

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
		this.from = from;
	}
	public String getSubj() {
		return subj;
	}
	public void setSubj(String subj) {
		this.subj = subj;
	}
	public EmailLogStatus getStatus() {
		return status;
	}
	public void setStatus(EmailLogStatus status) {
		this.status = status;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
