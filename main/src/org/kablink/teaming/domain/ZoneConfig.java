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
import java.util.Set;
import java.util.HashSet;

import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.util.SPropsUtil;

@SuppressWarnings("unchecked")
public class ZoneConfig extends ZonedObject implements WorkArea {
	public static Integer ZONE_LATEST_VERSION=8;  //This is used to introduce changes and fix things up between releases.
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
	private Integer fsaSynchInterval;
	private String fsaAutoUpdateUrl;
	private Long fsaMaxFileSize;

	public ZoneConfig()
	{
	}
	public ZoneConfig(Long zoneId) {
		this.zoneId = zoneId;
		this.authenticationConfig = new AuthenticationConfig();
		this.homePageConfig = new HomePageConfig();
		this.weekendsAndHolidaysConfig = new WeekendsAndHolidaysConfig();
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
    
	public boolean getFsaEnabled() {
		if(fsaEnabled == null)
			return SPropsUtil.getBoolean("fsa.enabled.default", false);
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
	public long getFsaMaxFileSize() {
		if(fsaMaxFileSize == null)
			return SPropsUtil.getLong("fsa.max.file.size", 1073741824L);
		else
			return fsaMaxFileSize;
	}
	public void setFsaMaxFileSize(long fsaMaxFileSize) {
		// In this version of Vibe, this field is NOT persisted to database. So, this method shouldn't be used.
		this.fsaMaxFileSize = fsaMaxFileSize;
	}
}
