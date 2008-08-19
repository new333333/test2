package com.sitescape.team.domain;

public class AuthenticationConfig extends ZonedObject {

	private boolean allowLocalLogin = true;
	private boolean allowAnonymousAccess = true;
	private boolean allowSelfRegistration = false;
	private Long lastUpdate;
	
	public AuthenticationConfig()
	{
		lastUpdate = new Long(System.currentTimeMillis());
	}
	public void setZoneId(Long zoneId)
	{
		this.zoneId = zoneId;
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
