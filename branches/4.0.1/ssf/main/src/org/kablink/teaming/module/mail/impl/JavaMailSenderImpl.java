/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.mail.impl;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.kablink.teaming.module.mail.ConnectionCallback;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Validator;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;

/**
 * This class extends the spring JavaMailSenderImpl.  It adds the bean name, so
 * we can locate the sender when mail is resent after an error.  The resend occurs
 * as a scheduled job.  It also caches the coneection so we can send lots of messages without
 * reconnecting and without holding lots of messages in memory
 * 
 * @author Janet McCann
 */
public class JavaMailSenderImpl extends
		org.springframework.mail.javamail.JavaMailSenderImpl
		implements org.kablink.teaming.module.mail.JavaMailSender {
	private String name="";

	@Override
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
				if (Validator.isNull(password)) {//see if moved to properties file
					String [] pieces = name.split("/");
					password = SPropsUtil.getString("mail." + pieces[pieces.length-1].trim() + ".out.password", "");
				}
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
	
	@Override
	public void setSession(Session session, String userName, String password) {
		super.setSession(session);
		setUsername(userName);
		setPassword(password);
		
	}
	
	@Override
	public String getDefaultFrom() {
		String from = MiscUtil.getFromOverride();
		if (!(MiscUtil.hasString(from))) {
			from = getUsername(); 
		}
		return from;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void send(Transport transport, MimeMessage mimeMessage) throws MailException {
		validate(transport);

		try {
			if (mimeMessage.getAllRecipients() != null) {
				transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
			}
		}
		catch (MailException ex) {
			throw ex;
		}
		catch (MessagingException ex) {
			throw new MailParseException(ex);
		}
		catch (Exception ex) {
			throw new MailPreparationException(ex);
		}
	}
	
	@Override
	public Object send(ConnectionCallback callback) throws MailException {
		Transport transport;
		try {
			transport = super.getTransport(getSession());
			transport.connect(getHost(), getPort(), getUsername(), getPassword());
		} catch (AuthenticationFailedException ex) {
				throw new MailAuthenticationException(ex);

		} catch (MessagingException ex) {
			throw new MailSendException("Mail server connection failed", ex);
		}
		try {
			return callback.doWithConnection(transport);
		} finally {
			try {transport.close();} catch (Exception ignore) {};
		}
	}
	
	private void validate(Transport transport) {
		try {
			if (!transport.isConnected()) transport.connect(getHost(), getPort(), getUsername(), getPassword());
		} catch (AuthenticationFailedException ex) {
			throw new MailAuthenticationException(ex);
		} catch (MessagingException ex) {
			throw new MailSendException("Mail server connection failed", ex);
		}
				
	}
}
