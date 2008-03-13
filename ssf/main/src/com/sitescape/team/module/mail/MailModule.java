/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */

package com.sitescape.team.module.mail;
import java.util.Date;
import java.util.Map;
import javax.mail.internet.MimeMessage;

import com.sitescape.team.domain.Binder;

/**
 * @author Janet McCann
 *
 */
public interface MailModule {
	
	public static String POSTING_JOB_KEY="posting.job";
	public static String NOTIFICATION_JOB_KEY="notification.job";
	public final static String SUBSCRIPTION_JOB_KEY="subscription.job";
	public final static String SUBSCRIPTION_MINUTES_KEY="subscription.minutes";

	public static final String NOTIFY_TEMPLATE_TEXT_KEY="notify.mailText";
	public static final String NOTIFY_TEMPLATE_HTML_KEY="notify.mailHtml";
	public static final String NOTIFY_TEMPLATE_CACHE_DISABLED_KEY="notify.templateCacheDisabled";
	public static final String NOTIFY_FROM_KEY="notify.from";
	public static final String NOTIFY_SUBJECT_KEY="notify.subject";
    public static final String DEFAULT_TIMEZONE_KEY="notify.timezone";

    public static final String REPLY_SUBJECT="RE: DocId:";
	//Inputs to sendMail from Map
	public static final String SUBJECT="SUBJECT";//string
	public static final String TO="TO";	//Collection of InternetAddress
	public static final String TEXT_MSG="TEXT"; //String
	public static final String HTML_MSG="HTML"; //String
	public static final String ATTACHMENTS="attachments"; //fileattachments
	public static final String ICALENDARS="icalendars"; //Collection of net.fortuna.ical4j.model.Calendar
	public static final String FROM="FROM"; 	//InternetAddress

    public Date sendNotifications(Long folderId, Date begin);
	public Date fillSubscriptions(Date begin);
	public void receivePostings();
	public void sendMail(MimeMessage msg);
	public void sendMail(String mailSenderName, MimeMessage msg);
	public void sendMail(String mailSenderName, java.io.InputStream input);
	public void sendMail(String mailSenderName, String account, String password, java.io.InputStream input);
    public void sendMail(String mailSenderName, MimeMessagePreparator preparer);
    public boolean sendMail(Binder binder, Map message, String comment);
    public void scheduleMail(Binder binder, Map message, String comment) throws Exception;
	public String getMailProperty(String zoneName, String name);
	public String getMailAttribute(String zoneName, String node, String name);
}
