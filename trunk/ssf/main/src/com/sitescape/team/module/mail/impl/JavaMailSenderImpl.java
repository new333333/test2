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
