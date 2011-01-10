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

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
/**
 * This class represents an email log entry.
 *
 */
public class EmailLog extends ZonedObject implements Serializable {

	private static final long serialVersionUID = 1L;
	protected EmailLogType type = EmailLogType.unknown;	//Type of mail
	protected Date sendDate;							//Date mail was sent
	protected CommaSeparatedValue toIds;				//comma separated list of user ids
	protected CommaSeparatedValue toEmailAddresses;		//comma separated list of email addresses
	protected Long fromId;								//User id of sender
	protected Long zoneId; 								//zone id
	protected String subj;								//Subj line from mail message
	protected EmailLogStatus status;							//sent, queued, error
	protected String comment;							//Error message (if any)
	protected CommaSeparatedValue fileAttachments;		//comma separated list of file names

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
	
	public EmailLog(EmailLogType type, Date sendDate, List<Long> toIds, Long fromId, EmailLogStatus status, Long zoneId) {
		this.type = type;
		this.sendDate = sendDate;
		this.fromId = fromId;
		this.toIds = new CommaSeparatedValue();
		this.toIds.setValue(toIds);
		this.status = status;
		this.zoneId = zoneId;
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
	public CommaSeparatedValue getToIds() {
		return toIds;
	}
	public void setToIds(CommaSeparatedValue toIds) {
		this.toIds = toIds;
	}
	public CommaSeparatedValue getToEmailAddresses() {
		return toEmailAddresses;
	}
	public void setToEmailAddresses(CommaSeparatedValue toEmailAddresses) {
		this.toEmailAddresses = toEmailAddresses;
	}
	public Long getFromId() {
		return fromId;
	}
	public void setFromId(Long fromId) {
		this.fromId = fromId;
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
	public CommaSeparatedValue getFileAttachments() {
		return fileAttachments;
	}
	public void setFileAttachments(CommaSeparatedValue fileAttachments) {
		this.fileAttachments = fileAttachments;
	}
	public Long getZoneId() {
		return zoneId;
	}
	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}
	protected EmailLog() {
	}

}
