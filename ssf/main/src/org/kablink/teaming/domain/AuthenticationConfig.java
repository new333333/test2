package org.kablink.teaming.domain;
//Used to encapsalate authentication information.  A component of zoneConfig
public class AuthenticationConfig  {

	private boolean allowLocalLogin = true;
	private boolean allowAnonymousAccess = true;
	private boolean allowSelfRegistration = false;
	private Long lastUpdate;
	
	public AuthenticationConfig()
	{
		lastUpdate = new Long(System.currentTimeMillis());
	}
	
	public boolean isAllowAnonymousAccess() {
		return allowAnonymousAccess;
	}
	public void setAllowAnonymousAccess(boolean allowAnonymousAccess) {
		this.allowAnonymousAccess = allowAnonymousAccess;
	}
	public boolean isAllowLocalLogin() {
		return allowLocalLogin;
	}
	public void setAllowLocalLogin(boolean allowLocalLogin) {
		this.allowLocalLogin = allowLocalLogin;
	}
	public boolean isAllowSelfRegistration() {
		return allowSelfRegistration;
	}
	public void setAllowSelfRegistration(boolean allowSelfRegistration) {
		this.allowSelfRegistration = allowSelfRegistration;
	}
	public Long getLastUpdate() {
		return lastUpdate;
	}
	protected void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public void markAsUpdated()
	{
		setLastUpdate(System.currentTimeMillis());
	}
}
