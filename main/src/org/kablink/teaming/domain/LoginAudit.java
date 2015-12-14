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
 */
public class LoginAudit extends ZonedObject {

	/**
	 * Unknown authenticator
	 */
	public static final String AUTHENTICATOR_UNKNOWN = "unknown";
	public static final short AUTHENTICATOR_UNKNOWN_DB = -1;
	/**
	 * User logged in through portal
	 * @deprecated
	 */
	public static final String AUTHENTICATOR_PORTAL	= "portal";
	public static final short AUTHENTICATOR_PORTAL_DB = -2;
	/**
	 * 	Web client (standalone)
	 */
	public static final String AUTHENTICATOR_WEB	= "web";
	public static final short AUTHENTICATOR_WEB_DB = 1;
	/**
	 * REST web services client with HTTP Basic authentication
	 */
	public static final String AUTHENTICATOR_REST_B = "rest_b";
	public static final short AUTHENTICATOR_REST_B_DB = 11;
	/**
	 * WebDAV client
	 */
	public static final String AUTHENTICATOR_WEBDAV	= "webdav";
	public static final short AUTHENTICATOR_WEBDAV_DB = 21;
	/**
	 * SOAP web services client with WS-Security authentication (message-level security)
	 */
	public static final String AUTHENTICATOR_WS		= "ws";
	public static final short AUTHENTICATOR_WS_DB = 31;
	/**
	 * SOAP web services client with HTTP Basic authentication
	 */
	public static final String AUTHENTICATOR_REMOTING_B = "r_b";
	public static final short AUTHENTICATOR_REMOTING_B_DB = 32;
	/**
	 * SOAP web services client with token-based authentication (message-level security)
	 */
	public static final String AUTHENTICATOR_REMOTING_T = "r_t";
	public static final short AUTHENTICATOR_REMOTING_T_DB = 33;
	/**
	 * RSS client
	 */
	public static final String AUTHENTICATOR_RSS	= "rss";
	public static final short AUTHENTICATOR_RSS_DB = 41;
	/**
	 * iCal client
	 */
	public static final String AUTHENTICATOR_ICAL	= "ical";
	public static final short AUTHENTICATOR_ICAL_DB = 42;
	
	protected Long id; // Required
	protected Date loginTime; // Required - Time at which user logged in
	protected Long userId; // Required - User who logged in
	protected Short authenticator; // Required - Type of authenticator
	protected String clientAddr;
	
	// For Hibernate
	protected LoginAudit() {
	}

	public LoginAudit(Long zoneId, String authenticatorName, String clientAddr, Long userId, Date loginTime) {
		this(authenticatorName, clientAddr, userId, loginTime);
		this.zoneId = zoneId;
	}
	
	public LoginAudit(String authenticatorName, String clientAddr, Long userId) {
		this(authenticatorName, clientAddr, userId, new Date());
	}
	
	protected LoginAudit(String authenticatorName, String clientAddr, Long userId, Date loginTime) {
		setAuthenticatorName(authenticatorName);
		this.clientAddr = clientAddr;
		this.userId = userId;
		this.loginTime = loginTime;
	}

	public void setZoneId(Long zoneId) {
		this.zoneId = zoneId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
    public Long getUserId() {
        return this.userId;
    }

	public Date getLoginTime() {
		return loginTime;
	}

	public String getClientAddr() {
		return this.clientAddr;
	}
	
	public String getAuthenticatorName() {
		switch (authenticator) {
		case AUTHENTICATOR_UNKNOWN_DB:
			return AUTHENTICATOR_UNKNOWN;
		case AUTHENTICATOR_PORTAL_DB:
			return AUTHENTICATOR_PORTAL;
		case AUTHENTICATOR_WEB_DB:
			return AUTHENTICATOR_WEB;
		case AUTHENTICATOR_REST_B_DB:
			return AUTHENTICATOR_REST_B;
		case AUTHENTICATOR_WEBDAV_DB:
			return AUTHENTICATOR_WEBDAV;
		case AUTHENTICATOR_WS_DB:
			return AUTHENTICATOR_WS;
		case AUTHENTICATOR_REMOTING_B_DB:
			return AUTHENTICATOR_REMOTING_B;
		case AUTHENTICATOR_REMOTING_T_DB:
			return AUTHENTICATOR_REMOTING_T;
		case AUTHENTICATOR_RSS_DB:
			return AUTHENTICATOR_RSS;
		case AUTHENTICATOR_ICAL_DB:
			return AUTHENTICATOR_ICAL;
		default:
			return AUTHENTICATOR_UNKNOWN;
		}
	}

	protected void setAuthenticatorName(String authenticatorName) {
		this.authenticator = toAuthenticatorDbValue(authenticatorName);
	}
	
	public static short toAuthenticatorDbValue(String authenticatorName) {
		switch(authenticatorName) {
		case AUTHENTICATOR_UNKNOWN:
			return AUTHENTICATOR_UNKNOWN_DB;
		case AUTHENTICATOR_PORTAL:
			return AUTHENTICATOR_PORTAL_DB;
		case AUTHENTICATOR_WEB:
			return AUTHENTICATOR_WEB_DB;
		case AUTHENTICATOR_REST_B:
			return AUTHENTICATOR_REST_B_DB;
		case AUTHENTICATOR_WEBDAV:
			return AUTHENTICATOR_WEBDAV_DB;
		case AUTHENTICATOR_WS:
			return AUTHENTICATOR_WS_DB;
		case AUTHENTICATOR_REMOTING_B:
			return AUTHENTICATOR_REMOTING_B_DB;
		case AUTHENTICATOR_REMOTING_T:
			return AUTHENTICATOR_REMOTING_T_DB;
		case AUTHENTICATOR_RSS:
			return AUTHENTICATOR_RSS_DB;
		case AUTHENTICATOR_ICAL:
			return AUTHENTICATOR_ICAL_DB;
		default:
			throw new IllegalArgumentException("Invalid authenticator name [" + authenticatorName + "]");
		}
	}

}
