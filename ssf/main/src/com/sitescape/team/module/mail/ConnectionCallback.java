package com.sitescape.team.module.mail;
import javax.mail.Transport;
import org.springframework.mail.MailException;
/**
 * Used to share a connection between mutiple messages.  An alternative to building all messages first.
 * @author Janet
 *
 */
public interface ConnectionCallback {
	public Object doWithConnection(Transport transport) throws MailException;
}
