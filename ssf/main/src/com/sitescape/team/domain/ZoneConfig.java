package com.sitescape.team.domain;

public class ZoneConfig extends ZonedObject {
	public static Integer ZONE_LATEST_VERSION=3;
	private Integer upgradeVersion=ZONE_LATEST_VERSION; 
	private AuthenticationConfig authenticationConfig;
	private MailConfig mailConfig;
	public ZoneConfig()
	{
	}
	public ZoneConfig(Long zoneId) {
		this.zoneId = zoneId;
		this.authenticationConfig = new AuthenticationConfig();
		this.mailConfig = new MailConfig();
	}
	public void setZoneId(Long zoneId)
	{
		this.zoneId = zoneId;
	}

    public Integer getUpgradeVersion() {
        return this.upgradeVersion;
    }
    public void setUpgradeVersion(Integer upgradeVersion) {
        this.upgradeVersion = upgradeVersion;
    }
    public AuthenticationConfig getAuthenticationConfig() {
    	return authenticationConfig;
    }
    public void setAuthenticationConfig(AuthenticationConfig authenticationConfig) {
    	this.authenticationConfig = authenticationConfig;
    }
    public MailConfig getMailConfig() {
    	return mailConfig;
    }
    public void setMailConfig(MailConfig mailConfig) {
    	this.mailConfig = mailConfig;
    }
}
