package com.sitescape.team.domain;

import java.util.Date;

import com.sitescape.team.domain.AuditTrail.AuditType;

/**
 * @hibernate.subclass discriminator-value="L"
 * 
 * Each instance of this class represents a user login.
 * A user login may be stateful and long-lasting (eg. portal and WebDAV) 
 * or stateless and short-lived (eg. RSS, iCAL and WS). 
 *
 */
public class LoginInfo extends AuditTrail {

	public static final String AUTHENTICATOR_PORTAL	= "portal";
	public static final String AUTHENTICATOR_WEBDAV	= "webdav";
	public static final String AUTHENTICATOR_RSS	= "rss";
	public static final String AUTHENTICATOR_ICAL	= "ical";
	public static final String AUTHENTICATOR_WS		= "ws";
		
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
