/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.domain;

import java.util.Date;

/**
 * @hibernate.subclass discriminator-value="L"
 * 
 * Each instance of this class represents a user login.
 * A user login may be stateful and long-lasting (eg. portal and WebDAV) 
 * or stateless and short-lived (eg. RSS, iCAL and WS). 
 *
 * @deprecated As of Filr 1.1.1 and Vibe Hudson - Use {@link LoginAudit} instead.
 */
public class LoginInfo extends AuditTrail {

	/**
	 * Unknown authenticator
	 */
	public static final String AUTHENTICATOR_UNKNOWN = "unknown";
	/**
	 * User logged in through portal
	 * @deprecated
	 */
	public static final String AUTHENTICATOR_PORTAL	= "portal";
	/**
	 * 	Web client (standalone)
	 */
	public static final String AUTHENTICATOR_WEB	= "web";
	/**
	 * WebDAV client
	 */
	public static final String AUTHENTICATOR_WEBDAV	= "webdav";
	/**
	 * RSS client
	 */
	public static final String AUTHENTICATOR_RSS	= "rss";
	/**
	 * iCal client
	 */
	public static final String AUTHENTICATOR_ICAL	= "ical";
	/**
	 * SOAP web services client with WS-Security authentication (message-level security)
	 */
	public static final String AUTHENTICATOR_WS		= "ws";
	/**
	 * SOAP web services client with HTTP Basic authentication
	 */
	public static final String AUTHENTICATOR_REMOTING_B = "r_b";
	/**
	 * SOAP web services client with token-based authentication (message-level security)
	 */
	public static final String AUTHENTICATOR_REMOTING_T = "r_t";
	/**
	 * REST web services client with HTTP Basic authentication
	 */
	public static final String AUTHENTICATOR_REST_B = "rest_b";
	
	public LoginInfo() {
	}

	public LoginInfo(String authenticatorName, Long userId, Date loginTime) {
		setAuditType(AuditType.login);
		setDescription(authenticatorName);
		setStartBy(userId);
		setStartDate(loginTime);
	}
	
	public LoginInfo(String authenticatorName, Long userId) {
		this(authenticatorName, userId, new Date());
	}
	
	public String getAuthenticatorName() {
		return getDescription();
	}

	public void setAuthenticatorName(String authenticatorName) {
		setDescription(authenticatorName);
	}

	public Date getLoginTime() {
		return getStartDate();
	}

	public void setLoginTime(Date loginTime) {
		setStartDate(loginTime);
	}
}
