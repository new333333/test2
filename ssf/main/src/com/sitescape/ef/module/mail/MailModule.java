
package com.sitescape.ef.module.mail;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.jobs.ScheduleInfo;
import com.sitescape.ef.util.SZoneConfig;

import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.dom4j.Element;
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
	
	public Date sendNotifications(Long folderId, Date start);
	public void receivePostings(ScheduleInfo info);
	public boolean sendMail(MimeMessage msg);
	public boolean sendMail(String mailSenderName, java.io.InputStream input);
	public String getMailProperty(String zoneName, String name);
	public String getMailAttribute(String zoneName, String node, String name);
	public String getMailAttribute(Folder folder, String node, String name);

}
