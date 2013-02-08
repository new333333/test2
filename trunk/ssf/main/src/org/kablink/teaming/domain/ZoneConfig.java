/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SPropsUtil;

@SuppressWarnings("unchecked")
public class ZoneConfig extends ZonedObject implements WorkArea {
	
	public static final String WORKAREA_TYPE = "zone";
	
	public static Integer ZONE_LATEST_VERSION=13;  //This is used to introduce changes and fix things up between releases.
	private Integer upgradeVersion=ZONE_LATEST_VERSION; 
	private AuthenticationConfig authenticationConfig;
	private HomePageConfig homePageConfig;
	private WeekendsAndHolidaysConfig weekendsAndHolidaysConfig;
	private Boolean mobileAccessEnabled; // access="field"
	private Boolean diskQuotasEnabled;
	private Integer diskQuotaUserDefault;
	private Integer diskQuotasHighwaterPercentage;
	private Long fileSizeLimitUserDefault;
	private Boolean binderQuotasInitialized;
	private Boolean binderQuotasEnabled;
	private Boolean binderQuotasAllowOwner;
	private Long fileVersionsMaxAge;
	private MailConfig mailConfig;
	private Boolean fsaEnabled;
	private Boolean fsaDeployEnabled;
	private Boolean fsaAllowCachePwd;
	private Integer fsaSynchInterval;
	private String fsaAutoUpdateUrl;
	private Integer fsaMaxFileSize;
	private OpenIDConfig openIDConfig;
	private Boolean externalUserEnabled;
	private String localeLanguage;
	private String localeCountry;
	private Boolean adHocFoldersEnabled;
	private Integer auditTrailKeepDays;
	private Integer changeLogsKeepDays;
	private MobileAppsConfig mobileAppsConfig;
	private Boolean jitsEnabled;
    private Long jitsWaitTimeout; // in milliseconds

	public ZoneConfig()
	{
	}
	public ZoneConfig(Long zoneId) {
		this.zoneId = zoneId;
		this.authenticationConfig = new AuthenticationConfig();
		this.homePageConfig = new HomePageConfig();
		this.weekendsAndHolidaysConfig = new WeekendsAndHolidaysConfig();
		this.mailConfig = new MailConfig();
		this.openIDConfig = new OpenIDConfig();
		this.mobileAppsConfig = new MobileAppsConfig();
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
    public OpenIDConfig getOpenIDConfig() {
    	if(openIDConfig == null)
    		return new OpenIDConfig(); // actor for default settings
		return openIDConfig;
	}
	public void setOpenIDConfig(OpenIDConfig openidConfig) {
		this.openIDConfig = openidConfig;
	}
	public HomePageConfig getHomePageConfig() {
    	return homePageConfig;
    }
    public void setHomePageConfig(HomePageConfig homePageConfig) {
    	this.homePageConfig = homePageConfig;
    }
    public WeekendsAndHolidaysConfig getWeekendsAndHolidaysConfig() {
    	return weekendsAndHolidaysConfig;
    }
    public void setWeekendsAndHolidaysConfig(WeekendsAndHolidaysConfig weekendsAndHolidaysConfig) {
    	this.weekendsAndHolidaysConfig = weekendsAndHolidaysConfig;
    }
    public boolean isMobileAccessEnabled() {
		if (mobileAccessEnabled != null)
			return mobileAccessEnabled.booleanValue();
		else
			return true; // default value
	}
	public void setMobileAccessEnabled(boolean mobileAccessEnabled) {
		this.mobileAccessEnabled = Boolean.valueOf(mobileAccessEnabled);
	}
	
	public Boolean isDiskQuotaEnabled() {
		if (diskQuotasEnabled != null)
			return diskQuotasEnabled.booleanValue();
		else
			return false; // default value
	}
	
	public void setDiskQuotasEnabled(Boolean diskQuotasEnabled) {
		this.diskQuotasEnabled = Boolean.valueOf(diskQuotasEnabled);
	}
	
	public void setBinderQuotasInitialized(Boolean binderQuotasInitialized) {
		this.binderQuotasInitialized = Boolean.valueOf(binderQuotasInitialized);
	}
	
	public void setBinderQuotasEnabled(Boolean binderQuotasEnabled, Boolean allowBinderOwner) {
		this.binderQuotasEnabled = Boolean.valueOf(binderQuotasEnabled);
		this.binderQuotasAllowOwner = Boolean.valueOf(allowBinderOwner);
	}
	
	public Boolean isBinderQuotaInitialized() {
		if (binderQuotasInitialized != null)
			return binderQuotasInitialized.booleanValue();
		else
			return false; // default value
	}
	
	public Boolean isBinderQuotaEnabled() {
		if (binderQuotasEnabled != null)
			return binderQuotasEnabled.booleanValue();
		else
			return false; // default value
	}
	
	public Boolean isBinderQuotaAllowBinderOwnerEnabled() {
		if (binderQuotasAllowOwner != null)
			return binderQuotasAllowOwner.booleanValue();
		else
			return false; // default value
	}
	
	public Integer getDiskQuotasHighwaterPercentage() {
		if (diskQuotasHighwaterPercentage != null)
			return diskQuotasHighwaterPercentage.intValue();
		else
			return 90; // default value
	}
	
	public void setDiskQuotasHighwaterPercentage(
			Integer diskQuotasHighwaterPercentage) {
		this.diskQuotasHighwaterPercentage = diskQuotasHighwaterPercentage;
	}
	
	public Integer getDiskQuotaUserDefault() {
		if (diskQuotaUserDefault != null)
			return diskQuotaUserDefault.intValue();
		else
			return 100; // default value in Megabytes
	}
	
	public void setDiskQuotaUserDefault(Integer diskQuotaUserDefault) {
		this.diskQuotaUserDefault = diskQuotaUserDefault;
	}
	
	public Long getFileSizeLimitUserDefault() {
		return fileSizeLimitUserDefault;
	}
	
	public void setFileSizeLimitUserDefault(Long fileSizeLimitUserDefault) {
		this.fileSizeLimitUserDefault = fileSizeLimitUserDefault;
	}
	
	public Long getFileVersionsMaxAge() {
		return fileVersionsMaxAge;
	}
	
	public void setFileVersionsMaxAge(Long fileVersionsMaxAge) {
		this.fileVersionsMaxAge = fileVersionsMaxAge;
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
    	return WORKAREA_TYPE;
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
   public boolean isExtFunctionMembershipInherited() {
  	return false;
  }
 
  public void setExtFunctionMembershipInherited(boolean extFunctionMembershipInherited) {
  	
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
    public boolean isAclExternallyControlled() {
    	return Boolean.FALSE;
    }
    public List<WorkAreaOperation> getExternallyControlledRights() {
    	return new ArrayList<WorkAreaOperation>();
    }
    public String getRegisteredRoleType() {
    	return "";
    }
    
/**
 * The following methods deal with the settings used by the desktop application (file sync app)
 */
    
	public boolean getFsaEnabled() {
		if(fsaEnabled == null)
			return SPropsUtil.getBoolean("fsa.enabled.default", true);
		else
			return fsaEnabled.booleanValue();
	}
	public void setFsaEnabled(boolean fsaEnabled) {
		this.fsaEnabled = Boolean.valueOf(fsaEnabled);
	}
	public int getFsaSynchInterval() {
		if(fsaSynchInterval == null)
			return SPropsUtil.getInt("fsa.synch.interval.default", 15);
		else 
			return fsaSynchInterval.intValue();
	}
	public void setFsaSynchInterval(int fsaSynchInterval) {
		this.fsaSynchInterval = Integer.valueOf(fsaSynchInterval);
	}
	public String getFsaAutoUpdateUrl() {
		if(fsaAutoUpdateUrl == null)
			return SPropsUtil.getString("fsa.auto.update.url.default", "");
		else 
			return fsaAutoUpdateUrl;
	}
	public void setFsaAutoUpdateUrl(String fsaAutoUpdateUrl) {
		this.fsaAutoUpdateUrl = fsaAutoUpdateUrl;
	}
	public int getFsaMaxFileSize() {
		if(fsaMaxFileSize == null)
			return SPropsUtil.getInt( "fsa.max.file.size", 50 );
		else
			return fsaMaxFileSize;
	}
	public void setFsaMaxFileSize( int fsaMaxFileSize) {
		// In this version of Vibe, this field is NOT persisted to database. So, this method shouldn't be used.
		this.fsaMaxFileSize = fsaMaxFileSize;
	}

	/**
	 * 
	 */
	public boolean getFsaAllowCachePwd()
	{
		if ( fsaAllowCachePwd == null )
			return SPropsUtil.getBoolean( "fsa.allow.cache.pwd.default", true );
		else
			return fsaAllowCachePwd.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setFsaAllowCachePwd( boolean allow )
	{
		fsaAllowCachePwd = Boolean.valueOf( allow );
	}

	/**
	 * 
	 */
	public boolean getFsaDeployEnabled()
	{
		if ( fsaDeployEnabled == null )
			return SPropsUtil.getBoolean( "fsa.deploy.enabled.default", false );
		else
			return fsaDeployEnabled.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setFsaDeployEnabled( boolean enabled )
	{
		fsaDeployEnabled = Boolean.valueOf( enabled );
	}

/**
 * End of methods dealing with desktop application
 */

	
	public boolean isExternalUserEnabled() {
		if(externalUserEnabled == null)
			return SPropsUtil.getBoolean("external.user.enabled.default", false);
		else
			return externalUserEnabled.booleanValue();
	}
	public void setExternalUserEnabled(boolean externalUserEnabled) {
		this.externalUserEnabled = externalUserEnabled;
	}
	
	public String getLocaleLanguage() {
		if(localeLanguage != null)
			return localeLanguage;
		else
			return SPropsUtil.getString("i18n.default.locale.language", "");
	}
	public void setLocaleLanguage(String localeLanguage) {
		this.localeLanguage = localeLanguage;
	}
	
	public String getLocaleCountry() {
		if(localeCountry != null)
			return localeCountry;
		else
			return SPropsUtil.getString("i18n.default.locale.country", "");
	}
	public void setLocaleCountry(String localeCountry) {
		this.localeCountry = localeCountry;
	}
	
	/**
	 * 
	 */
	public boolean isAdHocFoldersEnabled()
	{
		if ( adHocFoldersEnabled == null )
			return SPropsUtil.getBoolean( "adHoc.folders.enabled.default", false );
		
		return adHocFoldersEnabled.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setAdHocFoldersEnabled( boolean enabled )
	{
		adHocFoldersEnabled = Boolean.valueOf( enabled );
	}
	
	public int getAuditTrailKeepDays() {
		if(auditTrailKeepDays == null)
			return SPropsUtil.getInt("default.table.purge.keep.days.audittrail", 183);
		return auditTrailKeepDays;
	}
	public void setAuditTrailKeepDays(int auditTrailKeepDays) {
		this.auditTrailKeepDays = auditTrailKeepDays;
	}
	
	public int getChangeLogsKeepDays() {
		if(changeLogsKeepDays == null)
			return SPropsUtil.getInt("default.table.purge.keep.days.changelogs", 183);
		return changeLogsKeepDays;
	}
	public void setChangeLogsKeepDays(int changeLogsKeepDays) {
		this.changeLogsKeepDays = changeLogsKeepDays;
	}

	
	/**
	 * 
	 */
	public MobileAppsConfig getMobileAppsConfig()
	{
		if ( mobileAppsConfig == null )
			return new MobileAppsConfig();
		
		return mobileAppsConfig;
	}
	
	/**
	 * 
	 */
	public void setMobileAppsConfig( MobileAppsConfig config )
	{
		mobileAppsConfig = config;
	}
	
	public boolean getJitsEnabled() {
		if(jitsEnabled == null)
			return SPropsUtil.getBoolean("nf.jits.enabled", true);
		else
			return fsaEnabled.booleanValue();
	}
	public void setJitsEnabled(boolean jitsEnabled) {
		this.jitsEnabled = Boolean.valueOf(jitsEnabled);
	}
	
	public long getJitsWaitTimeout() {
		if(jitsWaitTimeout == null)
			return SPropsUtil.getLong("nf.jits.wait.timeout", 15000L);
		return jitsWaitTimeout;
	}
	public void setJitsWaitTimeout(long jitsWaitTimeout) {
		this.jitsWaitTimeout = Long.valueOf(jitsWaitTimeout);
	}
	
}
