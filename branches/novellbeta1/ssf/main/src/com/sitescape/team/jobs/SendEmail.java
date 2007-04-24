/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.jobs;

import java.util.Map;

public interface SendEmail {
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_sendEmailJob";
	public static final String SEND_MAIL_GROUP="send-email";
	public static final String SUBJECT="SUBJECT";
	//List of InternetAddress
	public static final String TO="TO";
	public static final String TEXT_MSG="TEXT";
	public static final String HTML_MSG="HTML";
	public static final String MIME_MESSAGE="mimeMessage";
	public static final String ATTACHMENTS="attachments";
	public static final String ICALENDARS="icalendars";
	//InternetAddress
	public static final String FROM="FROM";
	//List of InternetAddress
	public static final String CC="CC";
	
    public void schedule(String mailSenderName, Map message, String comment);
    public boolean sendMail(String mailSenderName, Map message, String comment);

}
