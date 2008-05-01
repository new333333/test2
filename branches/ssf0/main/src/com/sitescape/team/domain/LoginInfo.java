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
package com.sitescape.team.domain;

import java.util.Date;

/**
 * @hibernate.subclass discriminator-value="L"
 * 
 * Each instance of this class represents a user login.
 * A user login may be stateful and long-lasting (eg. portal and WebDAV) 
 * or stateless and short-lived (eg. RSS, iCAL and WS). 
 *
 */
public class LoginInfo extends AuditTrail {

	/**
	 * Unknown authenticator
	 */
	public static final String AUTHENTICATOR_UNKNOWN = "unknown";
	/**
	 * User logged in through portal
	 */
	public static final String AUTHENTICATOR_PORTAL	= "portal";
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
	 * SOAP-based WS client with WS-Security authentication (message-level security)
	 */
	public static final String AUTHENTICATOR_WS		= "ws";
	/**
	 * Remoting client with HTTP Basic authentication
	 */
	public static final String AUTHENTICATOR_REMOTING_B = "r_b";
	/**
	 * Remoting client with token-based authentication (message-level security)
	 */
	public static final String AUTHENTICATOR_REMOTING_T = "r_t";
	
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
