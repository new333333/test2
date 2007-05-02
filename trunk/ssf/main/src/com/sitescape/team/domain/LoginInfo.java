package com.sitescape.team.domain;

import java.util.Date;

/**
 * Each instance of this class represents a user login.
 * A user login may be stateful and long-lasting (eg. portal and WebDAV) 
 * or stateless and short-lived (eg. RSS, iCAL and WS). 
 *
 */
public class LoginInfo {

	public static final String AUTHENTICATOR_PORTAL	= "portal";
	public static final String AUTHENTICATOR_WEBDAV	= "webdav";
	public static final String AUTHENTICATOR_RSS	= "rss";
	public static final String AUTHENTICATOR_ICAL	= "ical";
	public static final String AUTHENTICATOR_WS		= "ws";
	
	/**
	 * Name of the (sub)system performing the authentication.
	 */
	private String authenticatorName;
	/**
	 * Id of the user logged in.
	 */
	private Long userId;
	/**
	 * Time of login.
	 */
	private Date loginTime;
	
	public LoginInfo() {
	}

	public LoginInfo(String authenticatorName, Long userId, Date loginTime) {
		this.authenticatorName = authenticatorName;
		this.userId = userId;
		this.loginTime = loginTime;
	}
	
	public LoginInfo(String authenticatorName, Long userId) {
		this(authenticatorName, userId, new Date());
	}
	
	public String getAuthenticatorName() {
		return authenticatorName;
	}

	public void setAuthenticatorName(String authenticatorName) {
		this.authenticatorName = authenticatorName;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
