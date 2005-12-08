
package com.sitescape.ef.module.mail;
import com.sitescape.ef.jobs.ScheduleInfo;
import java.util.Date;

import javax.mail.internet.MimeMessage;
/**
 * @author Janet McCann
 *
 */
public interface MailModule {
	public static final String _TEXT_HTML = "text/html;charset=\"UTF-8\"";

	public static final String _TEXT_PLAIN = "text/plain;charset=\"UTF-8\"";
	
	public Date sendNotifications(Long folderId);
	public void receivePostings(ScheduleInfo info);
	public boolean sendMail(MimeMessage msg);
	public boolean sendMail(String mailSenderName, java.io.InputStream input);

}
