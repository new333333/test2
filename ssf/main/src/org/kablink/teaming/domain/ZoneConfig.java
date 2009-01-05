package org.kablink.teaming.domain;
import java.util.Set;
import java.util.HashSet;

import org.kablink.teaming.security.function.WorkArea;
public class ZoneConfig extends ZonedObject implements WorkArea {
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
    //simulate a workarea to support the zone wide rights and provide a workarea for the security code
    public Long getWorkAreaId() {
    	return getZoneId();
    }
    public String getWorkAreaType() {
    	return EntityIdentifier.EntityType.zone.name();
    }
    public WorkArea getParentWorkArea() {
    	return null;
    }
    public boolean isFunctionMembershipInheritanceSupported() {
    	return false;
    }
     public boolean isFunctionMembershipInherited() {
    	return false;
    }
   
    public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
    	
    }
    public Long getOwnerId() {
    	return null;
    }
    public Principal getOwner() {
    	return null;
    }
    public void setOwner(Principal owner) {
    	
    }
     public boolean isTeamMembershipInherited() {
    	return false;
    }
     public Set<Long> getTeamMemberIds() {
    	return new HashSet();
    }
    public void setTeamMemberIds(Set<Long> memberIds) {
    	
    }
    public Set<Long> getChildWorkAreas() {
    	return new HashSet();
    }

}
