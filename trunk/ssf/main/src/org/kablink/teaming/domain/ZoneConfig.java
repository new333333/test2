/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.DesktopApplicationsLists;
import org.kablink.teaming.util.ShareLists;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class ZoneConfig extends ZonedObject implements WorkArea {
	public static final String WORKAREA_TYPE = "zone";
	
	public static Integer ZONE_LATEST_VERSION = 24;  //This is used to introduce changes and fix things up between releases.
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
	private Boolean fsaDeployLocalApps;
	private Boolean fsaAllowCachePwd;
	private Integer fsaSynchInterval;
	private String fsaAutoUpdateUrl;
	private Integer fsaMaxFileSize;
    private String fsaApplicationsBlob;
	private OpenIDConfig openIDConfig;
	private Boolean externalUserEnabled;
	private String localeLanguage;
	private String localeLanguageExt;
	private String localeCountry;
	private String localeCountryExt;
	private String timeZone;
	private String timeZoneExt;
	private Boolean adHocFoldersEnabled;
	private Boolean autoApplyDeferredUpdateLogs;
    private Date adHocFoldersLastModified;
	private Boolean fileArchivingEnabled;
	private Boolean passwordPolicyEnabled;
	private Boolean downloadEnabled;
	private Boolean webAccessEnabled;
	private Integer auditTrailKeepDays;
	private Integer changeLogsKeepDays;
	private Boolean auditTrailEnabled;
	private Boolean changeLogEnabled;
	private MobileAppsConfig mobileAppsConfig;
	// If this is false, JITS is turned off on all binders regardless of their individual settings.
	private Boolean jitsEnabled = Boolean.TRUE;
	// This setting exists only on zones, not on individual binders.
    private Long jitsWaitTimeout; // in milliseconds
    private String shareListsBlob;
    private Boolean allowShareWithLdapGroups;
    private NameCompletionSettings nameCompletionSettings;
	private Boolean useDirectoryRights;
	private Integer cachedRightsRefreshInterval;
	private Boolean telemetryEnabled;
	private Boolean telemetryTier2Enabled;
	private String extUserTermsAndConditions;
	private Boolean extUserTermsAndConditionsOn;

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
		this.nameCompletionSettings = new NameCompletionSettings();
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

	/**
	 * 
	 */
	public boolean getUseDirectoryRights()
	{
		if ( useDirectoryRights == null )
			return true;
		
		return useDirectoryRights.booleanValue();
	}

	/**
	 * 
	 */
	public void setUseDirectoryRights( Boolean value )
	{
		useDirectoryRights = value;
	}
	
	/**
	 * 
	 */
	public Integer getCachedRightsRefreshInterval()
	{
		return cachedRightsRefreshInterval;
	}
	
	/**
	 * 
	 */
	public void setCachedRightsRefreshInterval( Integer value )
	{
		cachedRightsRefreshInterval = value;
	}

    /**
     * 
     */
	public NameCompletionSettings getNameCompletionSettings()
	{
    	return nameCompletionSettings;
    }
	
	/**
	 * 
	 */
    public void setNameCompletionSettings( NameCompletionSettings settings )
    {
    	this.nameCompletionSettings = settings;
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
    @Override
	public Long getWorkAreaId() {
    	return getZoneId();
    }
    @Override
	public String getWorkAreaType() {
    	return WORKAREA_TYPE;
    }
    @Override
	public WorkArea getParentWorkArea() {
    	return null;
    }
    @Override
	public boolean isFunctionMembershipInheritanceSupported() {
    	return false;
    }
    @Override
	public boolean isFunctionMembershipInherited() {
   	return false;
   }
  
   @Override
public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
   	
   }
   @Override
public boolean isExtFunctionMembershipInherited() {
  	return false;
  }
 
  @Override
public void setExtFunctionMembershipInherited(boolean extFunctionMembershipInherited) {
  	
  }
    @Override
	public Long getOwnerId() {
    	return null;
    }
    @Override
	public Principal getOwner() {
    	return null;
    }
    @Override
	public void setOwner(Principal owner) {
    	
    }
     @Override
	public boolean isTeamMembershipInherited() {
    	return false;
    }
    @Override
	public Set<Long> getChildWorkAreas() {
    	return new HashSet();
    }
    @Override
	public boolean isAclExternallyControlled() {
    	return Boolean.FALSE;
    }
    @Override
	public List<WorkAreaOperation> getExternallyControlledRights() {
    	return new ArrayList<WorkAreaOperation>();
    }
    @Override
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
	 * @hibernate.property type="org.springframework.orm.hibernate3.support.ClobStringType"
	 * @return
	 */
	public String getFsaApplicationsBlob() {
		return fsaApplicationsBlob;
	}
	protected void setFsaApplicationsBlob(String fsaApplicationsBlob) {
		this.fsaApplicationsBlob = fsaApplicationsBlob;
	}

	public DesktopApplicationsLists getDesktopApplicationsLists(boolean defaultAppLists) {
		return DesktopApplicationsLists.parseFsaApplicationsBlob(defaultAppLists ? null : getFsaApplicationsBlob());
	}
	public DesktopApplicationsLists getDesktopApplicationsLists() {
		return getDesktopApplicationsLists(false);
	}
	public void setDesktopApplicationsLists(DesktopApplicationsLists desktopApplicationsLists) {
		setFsaApplicationsBlob(
			(null == desktopApplicationsLists) ?
				null                           :
				desktopApplicationsLists.getFsaApplicationsBlob());
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
	 * 
	 */
	public boolean getFsaDeployLocalApps()
	{
		if ( fsaDeployLocalApps == null )
			return SPropsUtil.getBoolean( "fsa.deploy.local.apps.default", false );
		else
			return fsaDeployLocalApps.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setFsaDeployLocalApps( boolean deployLocalApps )
	{
		fsaDeployLocalApps = Boolean.valueOf( deployLocalApps );
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
	
	public String getLocaleLanguageExt() {
		if(localeLanguageExt != null)
			return localeLanguageExt;
		else {
			String lang = SPropsUtil.getString("i18n.default.locale.language.external", "");
			if (!(MiscUtil.hasString(lang))) {
				lang = SPropsUtil.getString("i18n.default.locale.language", "");
			}
			return lang;
		}
	}
	public void setLocaleLanguageExt(String localeLanguageExt) {
		this.localeLanguageExt = localeLanguageExt;
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
	
	public String getLocaleCountryExt() {
		if(localeCountryExt != null)
			return localeCountryExt;
		else {
			String country = SPropsUtil.getString("i18n.default.locale.country.external", "");
			if (!(MiscUtil.hasString(country))) {
				country = SPropsUtil.getString("i18n.default.locale.country", "");
			}
			return country;
		}
	}
	public void setLocaleCountryExt(String localeCountryExt) {
		this.localeCountryExt = localeCountryExt;
	}
	
	public String getTimeZone() {
		if(timeZone != null)
			return timeZone;
		else
			return SPropsUtil.getString("i18n.default.timezone", "");
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
	public String getTimeZoneExt() {
		if(timeZoneExt != null)
			return timeZoneExt;
		else {
			String tz = SPropsUtil.getString("i18n.default.timezone.external", "");
			if (!(MiscUtil.hasString(tz))) {
				tz = SPropsUtil.getString("i18n.default.timezone", "");
			}
			return tz;
		}
	}
	public void setTimeZoneExt(String timeZoneExt) {
		this.timeZoneExt = timeZoneExt;
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

    public Date getAdHocFoldersLastModified() {
        return adHocFoldersLastModified;
    }

    public void setAdHocFoldersLastModified(Date adHocFoldersLastModified) {
        this.adHocFoldersLastModified = adHocFoldersLastModified;
    }

	/**
	 * 
	 */
	public boolean isAutoApplyDeferredUpdateLogs()
	{
		if ( autoApplyDeferredUpdateLogs == null )
			return true;
		
		return autoApplyDeferredUpdateLogs.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setAutoApplyDeferredUpdateLogs( boolean autoApply )
	{
		autoApplyDeferredUpdateLogs = Boolean.valueOf( autoApply );
	}

    /**
	 * 
	 */
	public boolean isDownloadEnabled()
	{
		if ( downloadEnabled == null )
			return true;
		
		return downloadEnabled.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setDownloadEnabled( boolean enabled )
	{
		downloadEnabled = Boolean.valueOf( enabled );
	}
	
	/**
	 * 
	 */
	public boolean isPasswordPolicyEnabled()
	{
		if ( passwordPolicyEnabled == null )
			return false;
		
		return passwordPolicyEnabled.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setPasswordPolicyEnabled( boolean enabled )
	{
		passwordPolicyEnabled = Boolean.valueOf( enabled );
	}
	
	/**
	 * 
	 */
	public boolean isWebAccessEnabled()
	{
		if ( webAccessEnabled == null )
			return true;
		
		return webAccessEnabled.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setWebAccessEnabled( boolean enabled )
	{
		webAccessEnabled = Boolean.valueOf( enabled );
	}
	
	/**
	 * 
	 */
	public boolean isSharingWithLdapGroupsEnabled()
	{
		if ( allowShareWithLdapGroups == null )
			return true;
		
		return allowShareWithLdapGroups.booleanValue();
	}
	
	/**
	 * 
	 */
	public void setAllowShareWithLdapGroups( boolean allow )
	{
		allowShareWithLdapGroups = Boolean.valueOf( allow );
	}
	
	public int getAuditTrailKeepDays() {
		if (auditTrailKeepDays == null) {
			if (Utils.checkIfFilr() || Utils.checkIfIPrint()) {
				return SPropsUtil.getInt("default.table.purge.keep.days.audittrail.filr", 183);
            } else {
				return SPropsUtil.getInt("default.table.purge.keep.days.audittrail", 0);
			}
		} else {
			return auditTrailKeepDays;
		}
	}
	public void setAuditTrailKeepDays(int auditTrailKeepDays) {
		this.auditTrailKeepDays = auditTrailKeepDays;
	}
	
	public boolean isFileArchivingEnabled() {
		if(fileArchivingEnabled == null) {
			if (Utils.checkIfVibe() || Utils.checkIfFilrAndVibe()) {
				return true;
			} else {
				//Filr and iPrint (and kablink) do not archive anything
				return false;
			}
		}
		return fileArchivingEnabled;
	}
	public void setFileArchivingEnabled(boolean fileArchivingEnabled) {
		this.fileArchivingEnabled = fileArchivingEnabled;
	}
	
	public boolean isAuditTrailEnabled() {
		if (null == auditTrailEnabled) {
			return true;
		}
		
		// Once this value has been set by the administrator, use that
		// value from then on.
		return auditTrailEnabled;
	}
	public void setAuditTrailEnabled(boolean auditTrailEnabled) {
		this.auditTrailEnabled = auditTrailEnabled;
	}
	
	public int getChangeLogsKeepDays() {
		if (changeLogsKeepDays == null) {
			if (Utils.checkIfFilr() || Utils.checkIfIPrint()) {
				return SPropsUtil.getInt("default.table.purge.keep.days.changelogs.filr", 183);
			} else {
				return SPropsUtil.getInt("default.table.purge.keep.days.changelogs", 0);
			}
		} else {
			return changeLogsKeepDays;
		}
	}
	public void setChangeLogsKeepDays(int changeLogsKeepDays) {
		this.changeLogsKeepDays = changeLogsKeepDays;
	}

	public boolean isChangeLogEnabled() {
		if(changeLogEnabled == null)
			if (Utils.checkIfVibe() || Utils.checkIfFilrAndVibe()) {
				//Vibe installations do Change Logging by default
				return true;
			} else {
				//Filr and iPrint systems don't do Change Logging by default.
				return false;
			}
		return changeLogEnabled;
	}
	public void setChangeLogEnabled(boolean changeLogEnabled) {
		this.changeLogEnabled = changeLogEnabled;
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
    		return true;
    	else
    		return jitsEnabled.booleanValue();
	}
	public void setJitsEnabled(boolean jitsEnabled) {
		this.jitsEnabled = Boolean.valueOf(jitsEnabled);
	}
	
	public long getJitsWaitTimeout() {
		if(jitsWaitTimeout == null)
			return SPropsUtil.getLong("nf.jits.wait.timeout", 5000L);
		return jitsWaitTimeout;
	}
	public void setJitsWaitTimeout(long jitsWaitTimeout) {
		this.jitsWaitTimeout = Long.valueOf(jitsWaitTimeout);
	}
	
	/**
	 * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobSerializableType"
	 * @return
	 */
	public String getShareListsBlob() {
		return shareListsBlob;
	}
	protected void setShareListsBlob(String shareListsBlob) {
		this.shareListsBlob = shareListsBlob;
	}
	
	public ShareLists getShareLists() {
		return ShareLists.parseShareListsBlob(getShareListsBlob());
	}
	public void setShareLists(ShareLists shareLists) {
		setShareListsBlob(shareLists.getShareListsBlob());
	}

	public boolean getTelemetryEnabled() {
		if(telemetryEnabled == null)
			return true;
		else
			return telemetryEnabled.booleanValue();
	}
	public void setTelemetryEnabled(boolean enabled) {
		this.telemetryEnabled = Boolean.valueOf(enabled);
	}
	
	public Boolean getTelemetryTier2Enabled() {
		return telemetryTier2Enabled;
	}
	public void setTelemetryTier2Enabled(boolean telemetryOptinEnabled) {
		this.telemetryTier2Enabled = Boolean.valueOf(telemetryOptinEnabled);
	}
	
	public String getExtUserTermsAndConditions() {
		return extUserTermsAndConditions;
	}
	public void setExtUserTermsAndConditions(String extUserTermsAndConditions) {
		this.extUserTermsAndConditions = extUserTermsAndConditions;
	}
	
	public boolean isExtUserTermsAndConditionsEnabled() {
		if(extUserTermsAndConditionsOn == null)
			return false; // off by default
		else
			return extUserTermsAndConditionsOn.booleanValue();
	}
	
	public void setExtUserTermsAndConditionsEnabled(boolean on) {
		this.extUserTermsAndConditionsOn = Boolean.valueOf(on);
	}
}
