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

import org.springframework.ldap.core.support.LdapContextSource;

/**
 * Created with IntelliJ IDEA. User: RAJESH-PC Date: 3/28/13
 * Time: 4:34 PM.
 * 
 * To change this template use File | Settings | File Templates.
 * 
 * @author rvasudevan
 */
public class SecureLdapContextSource extends LdapContextSource
{
	private String userName;
	private String password;
	private Boolean useSSL;

	public SecureLdapContextSource(String principal, String password, Boolean useSSL)
	{
		this.userName = principal;
		this.password = password;
		this.useSSL = useSSL;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception
	{
		super.afterPropertiesSet(); // To change body of overridden methods use File | Settings | File Templates.

		if (useSSL != null && useSSL)
		{
			Hashtable env = super.getAuthenticatedEnv(userName, password);

			// simple authentication needs username and password, external needs a keystore
			env.put(Context.SECURITY_AUTHENTICATION, "External");

			// specify use of ssl
			env.put(Context.SECURITY_PROTOCOL, "ssl");

			env.put("java.naming.ldap.factory.socket", "com.novell.gw.admin.server.LdapSslSocketFactory");

			// This next two lines causes the LdapSslFactory and the trust manager to be initialized
			super.setupAuthenticatedEnvironment(env, userName, password);

			super.setBaseEnvironmentProperties(env);

			super.afterPropertiesSet();
		}

	}
}
