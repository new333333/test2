
package com.sitescape.ef.module.mail;
import java.util.Date;
import javax.mail.internet.MimeMessage;

/**
 * @author Janet McCann
 *
 */
public interface MailModule {
	public static final String _TEXT_HTML = "text/html;charset=\"UTF-8\"";

	public static final String _TEXT_PLAIN = "text/plain;charset=\"UTF-8\"";
	
	public Date sendNotifications(Long forumId);
	public boolean sendMail(MimeMessage msg);

}
