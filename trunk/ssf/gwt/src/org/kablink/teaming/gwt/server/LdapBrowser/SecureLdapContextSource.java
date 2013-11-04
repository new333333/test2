/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.LdapBrowser;

import java.util.Hashtable;

import javax.naming.Context;

import org.kablink.teaming.web.util.MiscUtil;
import org.springframework.ldap.core.support.LdapContextSource;

/**
 * ?
 * 
 * @author rvasudevan
 */
public class SecureLdapContextSource extends LdapContextSource {
	private Boolean	m_useSSL;	//
	private String	m_userName;	//
	private String	m_password;	//

	/**
	 * Constructor method.
	 * 
	 * @param principal
	 * @param m_password
	 * @param m_useSSL
	 */
	public SecureLdapContextSource(String principal, String password, Boolean useSSL) {
		// Initialize the super class...
		super();
		
		// ...and store the parameters.
		m_userName = principal;
		m_password = password;
		m_useSSL   = useSSL;
	}

	/**
	 * ?
	 * 
	 * @throws Exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet(); // To change body of overridden methods use File | Settings | File Templates.
		boolean anonymous = ((!(MiscUtil.hasString(m_userName))) && (!(MiscUtil.hasString(m_password))));
		if ((null != m_useSSL) && m_useSSL) {
			Hashtable env;
			if (anonymous)
			     env = super.getAnonymousEnv();
			else env = super.getAuthenticatedEnv(m_userName, m_password);

			// Simple authentication needs username and m_password,
			// external needs a keystore.
			env.put(Context.SECURITY_AUTHENTICATION, "External");

			// Specify use of ssl.
			env.put(Context.SECURITY_PROTOCOL, "ssl");

			env.put("java.naming.ldap.factory.socket", "org.kablink.teaming.gwt.server.LdapBrowser.LdapSslSocketFactory");

			// This next two lines causes the LdapSslFactory and the
			// trust manager to be initialized.
			super.setBaseEnvironmentProperties(env);
			if (anonymous)
			     super.setAnonymousReadOnly(true);
			else super.setupAuthenticatedEnvironment(env, m_userName, m_password);
			super.afterPropertiesSet();
		}
		
		else if (anonymous) {
			super.setBaseEnvironmentProperties(super.getAnonymousEnv());
			super.setAnonymousReadOnly(true);
			super.afterPropertiesSet();
		}
	}
}
