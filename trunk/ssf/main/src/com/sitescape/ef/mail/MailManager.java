
package com.sitescape.ef.mail;
import java.util.Date;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import java.util.Collection;

import com.sitescape.team.domain.Binder;

/**
 * @author Janet McCann
 *
 */
public interface MailManager {
	public static String POSTING_JOB="posting.job";
	public static final String NOTIFY_TEMPLATE_TEXT="notify.mailText";
	public static final String NOTIFY_TEMPLATE_HTML="notify.mailHtml";
	public static final String NOTIFY_TEMPLATE_CACHE_DISABLED="notify.templateCacheDisabled";
	public static final String NOTIFY_FROM="notify.from";
	public static final String NOTIFY_SUBJECT="notify.subject";
    public static final String REPLY_SUBJECT="RE: DocId:";

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
	public String getMailAttribute(Binder binder, String node, String name);
}
