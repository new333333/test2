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

package com.sitescape.team.module.mail;
import java.util.Date;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import java.util.Collection;

import com.sitescape.team.domain.Binder;

/**
 * @author Janet McCann
 *
 */
public interface MailModule {
	public static String POSTING_JOB="posting.job";
	public static final String NOTIFY_TEMPLATE_TEXT="notify.mailText";
	public static final String NOTIFY_TEMPLATE_HTML="notify.mailHtml";
	public static final String NOTIFY_TEMPLATE_CACHE_DISABLED="notify.templateCacheDisabled";
	public static final String NOTIFY_FROM="notify.from";
	public static final String NOTIFY_SUBJECT="notify.subject";
    public static final String REPLY_SUBJECT="RE: DocId:";
    public static final String DEFAULT_TIMEZONE="notify.timezone";
	public Date sendNotifications(Long folderId, Date start);
	public void fillSubscription(Long folderId, Long entryId, Date stamp);
	public void receivePostings();
	public void sendMail(MimeMessage msg);
	public void sendMail(String mailSenderName, MimeMessage msg);
	public void sendMail(String mailSenderName, java.io.InputStream input);
    public void sendMail(String mailSenderName, MimeMessagePreparator preparer);
    public boolean sendMail(Binder binder, Map message, String comment);
    public void scheduleMail(Binder binder, Map message, String comment);
	public String getMailProperty(String zoneName, String name);
	public String getMailAttribute(String zoneName, String node, String name);
}
