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
package org.kablink.teaming.module.mail;

import java.util.Date;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Entry;

/**
 * ?
 * 
 * @author Janet McCann
 */
@SuppressWarnings("unchecked")
public interface MailModule {
	public enum Property {
		POSTING_JOB ("posting.job"),
		NOTIFICATION_JOB ("notification.job"),
		SENDMAIL_JOB ("mail.send"),
		SUBSCRIPTION_JOB ("subscription.job"),
		SUBSCRIPTION_MINUTES ("subscription.minutes"),
		NOTIFY_FROM ("notify.from"),
		NOTIFY_SUBJECT ("notify.subject"),
	    DEFAULT_TIMEZONE ("notify.timezone");
		String keyValue;
		Property(String value) {
			keyValue = value;
		}
		public String getKey() {return keyValue;}
	}

	//reply posting subject header
    public static final String REPLY_SUBJECT="RE: DocId:";
	//Inputs to sendMail from Map
	public static final String SUBJECT="SUBJECT";//string
	public static final String TO="TO";	//Collection of InternetAddress
	public static final String CC="CC";	//Collection of InternetAddress
	public static final String BCC="BCC";	//Collection of InternetAddress
	public static final String TEXT_MSG="TEXT"; //String
	public static final String HTML_MSG="HTML"; //String
	public static final String ATTACHMENTS="attachments"; //file attachments
	public static final String ICALENDARS="icalendars"; //Collection of net.fortuna.ical4j.model.Calendar
	public static final String FROM="FROM"; 	//InternetAddress
	public static final String LOG_TYPE="LOG_TYPE"; 	//EmailLogType

	public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	public static final String HEADER_CONTENT_TRANSFER_ENCODING_8BIT = "8bit";
	public static final String MULTIPART_SUBTYPE_ALTERNATIVE = "alternative";
	public static final String CONTENT_TYPE_ALTERNATIVE = "text/alternative";
	public static final String CONTENT_ENCODING = "utf-8";
	public static final String CONTENT_TYPE_HTML = "text/html";
	public static final String CONTENT_TYPE_CALENDAR = "text/calendar";
	public static final String CONTENT_TYPE_CHARSET_SUFFIX = ";charset=";
	public static final String CONTENT_TYPE_CALENDAR_COMPONENT_SUFFIX = "; component=";
	public static final String CONTENT_TYPE_CALENDAR_METHOD_SUFFIX = "; method=";
	public static final String ICAL_FILE_EXTENSION = ".ics";
	public static final String HEADER_X_VIBE_ONPREM = "X-Vibe-OnPrem";
	public static final String HEADER_X_ROOTVIBE_ONPREM = "X-RootVibe-OnPrem";
	public static final String HEADER_X_NOVELL_PRODUCT = "X-Novell-Product";
	public static final String HEADER_X_NOVELL_PRODUCT_FILR = "filr";
	public static final String HEADER_X_NOVELL_PRODUCT_VIBE = "vibe";
	public static final String HEADER_IN_REPLY_TO = "In-Reply-To";
	public static final String HEADER_MESSAGE_ID = "Message-ID";
	
    public Date sendNotifications(Long folderId, Date begin);
	public Date fillSubscriptions(Date begin);
	public void receivePostings();
	public void sendMail(MimeMessage msg);
	public void sendMail(String mailSenderName, MimeMessage msg);
	public void sendMail(String mailSenderName, java.io.InputStream input);
	public void sendMail(String mailSenderName, String account, String password, java.io.InputStream input);
    public void sendMail(String mailSenderName, MimeMessagePreparator preparer);
    public MailSentStatus sendMail(Binder binder, Map message, String comment);
    public MailSentStatus sendMail(Entry entry, Map message, String comment, boolean sendAttachments);
    public void scheduleMail(Binder binder, Map message, String comment) throws Exception;
	public String getMailProperty(String zoneName, Property property);
	public String getMailProperty(String zoneName, String name);
	public String getMailAttribute(String zoneName, String node, String name);
	public JavaMailSender getMailSender(Binder binder);
	public String getNotificationDefaultFrom(Binder binder);
	public String getNotificationMailSenderName(Binder binder);
}
