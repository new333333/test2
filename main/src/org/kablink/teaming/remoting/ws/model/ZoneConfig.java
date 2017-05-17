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
package org.kablink.teaming.remoting.ws.model;

public class ZoneConfig {
	private boolean fsaEnabled;
	private int fsaSynchInterval;
	private String fsaAutoUpdateUrl;
	private long fsaMaxFileSize;
	private boolean mobileAccessEnabled;
	private boolean diskQuotasEnabled;
	private int diskQuotaUserDefault;
	private int diskQuotasHighwaterPercentage;
	private Long fileSizeLimitUserDefault;
	private boolean binderQuotasInitialized;
	private boolean binderQuotasEnabled;
	private boolean binderQuotasAllowOwner;
	private Long fileVersionsMaxAge;
	
	public boolean isFsaEnabled() {
		return fsaEnabled;
	}
	public void setFsaEnabled(boolean fsaEnabled) {
		this.fsaEnabled = fsaEnabled;
	}
	public int getFsaSynchInterval() {
		return fsaSynchInterval;
	}
	public void setFsaSynchInterval(int fsaSynchInterval) {
		this.fsaSynchInterval = fsaSynchInterval;
	}
	public String getFsaAutoUpdateUrl() {
		return fsaAutoUpdateUrl;
	}
	public void setFsaAutoUpdateUrl(String fsaAutoUpdateUrl) {
		this.fsaAutoUpdateUrl = fsaAutoUpdateUrl;
	}
	public long getFsaMaxFileSize() {
		return fsaMaxFileSize;
	}
	public void setFsaMaxFileSize(long fsaMaxFileSize) {
		this.fsaMaxFileSize = fsaMaxFileSize;
	}
	public boolean isMobileAccessEnabled() {
		return mobileAccessEnabled;
	}
	public void setMobileAccessEnabled(boolean mobileAccessEnabled) {
		this.mobileAccessEnabled = mobileAccessEnabled;
	}
	public boolean isDiskQuotasEnabled() {
		return diskQuotasEnabled;
	}
	public void setDiskQuotasEnabled(boolean diskQuotasEnabled) {
		this.diskQuotasEnabled = diskQuotasEnabled;
	}
	public int getDiskQuotaUserDefault() {
		return diskQuotaUserDefault;
	}
	public void setDiskQuotaUserDefault(int diskQuotaUserDefault) {
		this.diskQuotaUserDefault = diskQuotaUserDefault;
	}
	public int getDiskQuotasHighwaterPercentage() {
		return diskQuotasHighwaterPercentage;
	}
	public void setDiskQuotasHighwaterPercentage(int diskQuotasHighwaterPercentage) {
		this.diskQuotasHighwaterPercentage = diskQuotasHighwaterPercentage;
	}
	public Long getFileSizeLimitUserDefault() {
		return fileSizeLimitUserDefault;
	}
	public void setFileSizeLimitUserDefault(Long fileSizeLimitUserDefault) {
		this.fileSizeLimitUserDefault = fileSizeLimitUserDefault;
	}
	public boolean isBinderQuotasInitialized() {
		return binderQuotasInitialized;
	}
	public void setBinderQuotasInitialized(boolean binderQuotasInitialized) {
		this.binderQuotasInitialized = binderQuotasInitialized;
	}
	public boolean isBinderQuotasEnabled() {
		return binderQuotasEnabled;
	}
	public void setBinderQuotasEnabled(boolean binderQuotasEnabled) {
		this.binderQuotasEnabled = binderQuotasEnabled;
	}
	public boolean isBinderQuotasAllowOwner() {
		return binderQuotasAllowOwner;
	}
	public void setBinderQuotasAllowOwner(boolean binderQuotasAllowOwner) {
		this.binderQuotasAllowOwner = binderQuotasAllowOwner;
	}
	public Long getFileVersionsMaxAge() {
		return fileVersionsMaxAge;
	}
	public void setFileVersionsMaxAge(Long fileVersionsMaxAge) {
		this.fileVersionsMaxAge = fileVersionsMaxAge;
	}
	
}
