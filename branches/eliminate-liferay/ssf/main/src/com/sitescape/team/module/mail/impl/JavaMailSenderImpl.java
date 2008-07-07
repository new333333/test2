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
package com.sitescape.team.module.mail.impl;
import java.io.IOException;
import java.util.Date;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.sitescape.util.Validator;
/**
 * This class extends the spring JavaMailSenderImpl.  It adds the bean name, so
 * we can locate the sender when mail is resent after an error.  The resend occurs
 * as a scheduled job.  It also caches the coneection so we can send lots of messages without
 * reconnecting and without holding lots of messages in memory
 * @author Janet McCann
 *
 */
public class JavaMailSenderImpl extends
		org.springframework.mail.javamail.JavaMailSenderImpl
		implements com.sitescape.team.module.mail.JavaMailSender {
	private String name;

	public void setSession(Session session) {
		//using either bean properties or jndi properties for host, port, userName
		super.setSession(session);
		String protocol = session.getProperty("mail.transport.protocol");
		if (Validator.isNotNull(protocol)) setProtocol(protocol);	//the default bean is smtp, need to set if supplied
		String prefix = "mail." + getProtocol() + ".";
		//if password not set in bean, see if in session properties
		if (Validator.isNull(getPassword())) {
			String auth = session.getProperty(prefix + "auth");
			if (Validator.isNull(auth)) 
				auth = session.getProperty("mail.auth");
			if ("true".equals(auth)) {
				String password = session.getProperty(prefix + "password");
				if (Validator.isNull(password)) 
					password = session.getProperty("mail.password");
				setPassword(password);
			}
		}
		//if password not set in bean, see if in session properties
		//	setup for defaultFrom
		if (Validator.isNull(getUsername())) {
			String user = session.getProperty(prefix + "user");
			if (Validator.isNull(user)) 
				user = session.getProperty("mail.user");
			setUsername(user); 
		}
	}
	public void setSession(Session session, String userName, String password) {
		super.setSession(session);
		setUsername(userName);
		setPassword(password);
		
	}
	public String getDefaultFrom() {
		return getUsername();
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void send(MimeMessagePreparator mimeMessagePreparator, Object ctx) throws MailException {
		Cache cache = (Cache)ctx;
		cache.validate();

		try {
			MimeMessage mimeMessage = createMimeMessage();
			mimeMessagePreparator.prepare(mimeMessage);
			if (mimeMessage.getSentDate() == null) {
				mimeMessage.setSentDate(new Date());
			}
			mimeMessage.saveChanges();
			cache.transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
		}
		catch (MailException ex) {
			throw ex;
		}
		catch (MessagingException ex) {
			throw new MailParseException(ex);
		}
		catch (IOException ex) {
			throw new MailPreparationException(ex);
		}
		catch (Exception ex) {
			throw new MailPreparationException(ex);
		}
	}
	public Object initializeConnection() throws MailException {
		Transport transport;
		try {
			transport = super.getTransport(getSession());
			transport.connect(getHost(), getPort(), getUsername(), getPassword());
		} catch (AuthenticationFailedException ex) {
				throw new MailAuthenticationException(ex);

		} catch (MessagingException ex) {
			throw new MailSendException("Mail server connection failed", ex);
		}
		return new Cache(transport);
	}
	public void releaseConnection(Object ctx) {
		if (ctx != null) ((Cache)ctx).release();
	}
	private class Cache {
		Transport transport;
		protected Cache(Transport transport) {
			this.transport = transport;
		}
		protected void release() {
			try {
				transport.close();
			} catch (Exception ex) {};
		}
		protected void validate() {
			try {
				if (!transport.isConnected()) transport.connect(getHost(), getPort(), getUsername(), getPassword());
			} catch (AuthenticationFailedException ex) {
				throw new MailAuthenticationException(ex);
			} catch (MessagingException ex) {
				throw new MailSendException("Mail server connection failed", ex);
			}
				
		}
	}
}
