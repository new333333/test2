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

import java.io.Serializable;

public class User extends Principal implements Serializable {

	private String firstName;
	private String middleName;
	private String lastName;
	private String organization;
	private String phone;
	private String zonName;
	private String localeLanguage;
	private String localeCountry;
	private String timeZone;
	private String skypeId;
	private String twitterId;
	private Long miniBlogId;
	private Long diskQuota;
	private Long fileSizeLimit;
	private Long diskSpaceUsed;
	private Long maxGroupsQuota;
    protected Long maxGroupsFileSizeLimit;
	private Long workspaceId;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLocaleCountry() {
		return localeCountry;
	}
	public void setLocaleCountry(String localeCountry) {
		this.localeCountry = localeCountry;
	}
	public String getLocaleLanguage() {
		return localeLanguage;
	}
	public void setLocaleLanguage(String localeLanguage) {
		this.localeLanguage = localeLanguage;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSkypeId() {
		return skypeId;
	}
	public void setSkypeId(String skypeId) {
		this.skypeId = skypeId;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public String getZonName() {
		return zonName;
	}
	public void setZonName(String zonName) {
		this.zonName = zonName;
	}
	public String getTwitterId() {
		return twitterId;
	}
	public void setTwitterId(String twitterId) {
		this.twitterId = twitterId;
	}
	public Long getMiniBlogId() {
		return miniBlogId;
	}
	public void setMiniBlogId(Long miniBlogId) {
		this.miniBlogId = miniBlogId;
	}
	public Long getDiskQuota() {
		if (diskQuota == null) return new Long(0);
		return diskQuota;
	}
	public void setDiskQuota(Long diskQuota) {
		this.diskQuota = diskQuota;
	}
	public Long getFileSizeLimit() {
		return fileSizeLimit;
	}
	public void setFileSizeLimit(Long fileSizeLimit) {
		this.fileSizeLimit = fileSizeLimit;
	}
	public Long getMaxGroupsQuota() {
		if (maxGroupsQuota == null) return 0L;
		return maxGroupsQuota;
	}
	public void setMaxGroupsQuota(Long maxGroupsQuota) {
		this.maxGroupsQuota = maxGroupsQuota;
	}
	public Long getMaxGroupsFileSizeLimit() {
		return maxGroupsFileSizeLimit;
	}	
	public void setMaxGroupsFileSizeLimit(Long maxGroupsFileSizeLimit) {
		this.maxGroupsFileSizeLimit = maxGroupsFileSizeLimit;
	}	
	public Long getDiskSpaceUsed() {
		if (diskSpaceUsed == null) return 0L;
		return diskSpaceUsed;
	}
	public void setDiskSpaceUsed(Long diskSpaceUsed) {
		this.diskSpaceUsed = diskSpaceUsed;
	}
	public void incrementDiskSpaceUsed(Long diskSpace) {
		this.diskSpaceUsed += diskSpace;
	}
	public void decrementDiskSpaceUsed(Long diskSpace) {
		this.diskSpaceUsed -= diskSpace;
	}
	public void setWorkspaceId(Long workspaceId) {
		this.workspaceId = workspaceId;
	}
	public Long getWorkspaceId() {
		return workspaceId;
	}

}
