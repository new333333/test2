package com.sitescape.team.mail.impl;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;

import com.sitescape.util.Validator;
/**
 * This class extends the spring JavaMailSenderImpl.  It adds the bean name, so
 * we can locate the sender when mail is resent after an error.  The resend occurs
 * as a scheduled job.
 * @author Janet McCann
 *
 */
public class JavaMailSenderImpl extends
		org.springframework.mail.javamail.JavaMailSenderImpl
		implements com.sitescape.team.mail.JavaMailSender {
	private String name;
	protected Transport getTransport(Session session) throws NoSuchProviderException {
		Transport transport = super.getTransport(session);
		//setup password
		String protocol = getProtocol();
		String prefix = "mail." + protocol + ".";
		String auth = session.getProperty(prefix + "auth");
		if (Validator.isNull(auth)) 
			auth = session.getProperty("mail.auth");
		//apparently this isn't a standard property
		if ("true".equals(auth)) {
			String password = session.getProperty(prefix + "password");
			if (Validator.isNull(password)) 
				password = session.getProperty("mail.password");
			setPassword(password);
		}
		return transport;
	}

	public String getDefaultFrom() {
		Session session = getSession();
		String protocol = getProtocol();
		String from = session.getProperty("mail." + protocol + ".user");
		if (Validator.isNull(from))
			from = session.getProperty("mail.user");
		return from;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

}
