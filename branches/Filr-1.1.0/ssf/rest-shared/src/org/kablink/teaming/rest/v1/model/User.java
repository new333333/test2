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
package org.kablink.teaming.rest.v1.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "user")
public class User extends Principal {
    private Boolean person;
    private String firstName;
   	private String middleName;
   	private String lastName;
   	private String organization;
   	private String phone;
   	private Locale locale;
   	private String timeZone;
   	private String skypeId;
   	private String twitterId;
   	private StringIdLinkPair avatar;
   	private LongIdLinkPair miniBlog;
   	private Long diskQuota;
   	private Long fileSizeLimit;
   	private Long diskSpaceUsed;
    private Long diskSpaceQuota;
    private LongIdLinkPair hiddenFilesFolder;
   	private LongIdLinkPair workspace;
    private MobileAppConfig mobileAppConfig;
    private DesktopAppConfig desktopAppConfig;
    private List<LongIdLinkPair> groups;
    private List<MobileDevice> mobileDevices;

    public User() {
        super();
    }

    protected User(User orig) {
        super(orig);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return new User(this);
    }

    public Boolean getPerson() {
        return person;
    }

    public void setPerson(Boolean person) {
        this.person = person;
    }

    @XmlElement(name="disk_quota")
    public Long getDiskQuota() {
        return diskQuota;
    }

    public void setDiskQuota(Long diskQuota) {
        this.diskQuota = diskQuota;
    }

    @XmlElement(name="disk_space_used")
    public Long getDiskSpaceUsed() {
        return diskSpaceUsed;
    }

    public void setDiskSpaceUsed(Long diskSpaceUsed) {
        this.diskSpaceUsed = diskSpaceUsed;
    }

    @XmlElement(name="disk_space_quota")
    public Long getDiskSpaceQuota() {
        return diskSpaceQuota;
    }

    public void setDiskSpaceQuota(Long diskSpaceQuota) {
        this.diskSpaceQuota = diskSpaceQuota;
    }

    @XmlElement(name="file_size_limit")
    public Long getFileSizeLimit() {
        return fileSizeLimit;
    }

    public void setFileSizeLimit(Long fileSizeLimit) {
        this.fileSizeLimit = fileSizeLimit;
    }

    @XmlElement(name="first_name")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlElement(name="last_name")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @XmlElement(name="middle_name")
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @XmlElement(name="mini_blog")
    public LongIdLinkPair getMiniBlog() {
        return miniBlog;
    }

    public void setMiniBlog(LongIdLinkPair miniBlog) {
        this.miniBlog = miniBlog;
    }

    @XmlElement(name="organization")
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @XmlElement(name="phone")
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @XmlElement(name="skype_id")
    public String getSkypeId() {
        return skypeId;
    }

    public void setSkypeId(String skypeId) {
        this.skypeId = skypeId;
    }

    @XmlElement(name="time_zone")
    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @XmlElement(name="twitter_id")
    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    @XmlElement(name="avatar")
    public StringIdLinkPair getAvatar() {
        return avatar;
    }

    public void setAvatar(StringIdLinkPair avatar) {
        this.avatar = avatar;
    }

    @XmlElement(name="hidden_files_folder")
    public LongIdLinkPair getHiddenFilesFolder() {
        return hiddenFilesFolder;
    }

    public void setHiddenFilesFolder(LongIdLinkPair hiddenFilesFolder) {
        this.hiddenFilesFolder = hiddenFilesFolder;
    }

    @XmlElement(name="workspace")
    public LongIdLinkPair getWorkspace() {
        return workspace;
    }

    public void setWorkspace(LongIdLinkPair workspace) {
        this.workspace = workspace;
    }

    @XmlElement(name="mobile_app_config")
    public MobileAppConfig getMobileAppConfig() {
        return mobileAppConfig;
    }

    public void setMobileAppConfig(MobileAppConfig mobileAppConfig) {
        this.mobileAppConfig = mobileAppConfig;
    }

    @XmlElement(name="desktop_app_config")
    public DesktopAppConfig getDesktopAppConfig() {
        return desktopAppConfig;
    }

    public void setDesktopAppConfig(DesktopAppConfig desktopAppConfig) {
        this.desktopAppConfig = desktopAppConfig;
    }

    @XmlElementWrapper(name="groups")
    @XmlElement(name="group")
    public List<LongIdLinkPair> getGroups() {
        return groups;
    }

    public void setGroups(List<LongIdLinkPair> groups) {
        this.groups = groups;
    }

    @XmlElementWrapper(name="mobile_devices")
    @XmlElement(name="mobile_device")
    public List<MobileDevice> getMobileDevices() {
        return mobileDevices;
    }

    public void setMobileDevices(List<MobileDevice> mobileDevices) {
        this.mobileDevices = mobileDevices;
    }
}
