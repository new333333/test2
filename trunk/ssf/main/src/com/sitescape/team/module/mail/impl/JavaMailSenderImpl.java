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
package com.sitescape.team.module.mail.impl;
import javax.mail.Session;

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
	public String getDefaultFrom() {
		return getUsername();
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

}
