/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.rest.v1.annotations.Undocumented;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The system settings for the authenticated user.
 */
@XmlRootElement(name = "zone_config")
public class ZoneConfig extends BaseRestObject {
    private Long id;
    private String guid;
    private BinderQuotasConfig binderQuotasConfig;
    private DiskQuotasConfig diskQuotasConfig;
    private DesktopAppConfig desktopAppConfig;
    private MobileAppConfig mobileAppConfig;
   	private Long fileUploadSizeLimit;
   	private Long fileVersionsMaxAge;
    private Boolean allowShareWithLdapGroups;
    private ExternalSharingRestrictions sharingRestrictions;

    /**
     * The ID of the zone.  Vibe supports multiple zones.  Filr only supports one zone.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * A GUID that can be used to uniquely identify this system.
     */
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @XmlElement(name="binder_quotas")
    public BinderQuotasConfig getBinderQuotasConfig() {
        return binderQuotasConfig;
    }

    public void setBinderQuotasConfig(BinderQuotasConfig binderQuotasConfig) {
        this.binderQuotasConfig = binderQuotasConfig;
    }

    /**
     * The user's effective disk quota settings.
     */
    @XmlElement(name="disk_quotas")
    public DiskQuotasConfig getDiskQuotasConfig() {
        return diskQuotasConfig;
    }

    public void setDiskQuotasConfig(DiskQuotasConfig diskQuotasConfig) {
        this.diskQuotasConfig = diskQuotasConfig;
    }

    /**
     * The user's effective desktop application settings.
     * @return
     */
    @XmlElement(name="desktop_app_config")
    public DesktopAppConfig getDesktopAppConfig() {
        return desktopAppConfig;
    }

    public void setDesktopAppConfig(DesktopAppConfig desktopAppConfig) {
        this.desktopAppConfig = desktopAppConfig;
    }

    /**
     * The user's effective mobile application settings,
     */
    @XmlElement(name="mobile_app_config")
    public MobileAppConfig getMobileAppConfig() {
        return mobileAppConfig;
    }

    public void setMobileAppConfig(MobileAppConfig mobileAppConfig) {
        this.mobileAppConfig = mobileAppConfig;
    }

    /**
     * The user's file upload size limit, in bytes.  This is the maximum file size that the user can upload to Filr from any client.
     * May be null if no size limit has been configured.
     */
    public Long getFileUploadSizeLimit() {
        return fileUploadSizeLimit;
    }

    @XmlElement(name="file_upload_size_limit")
    public void setFileUploadSizeLimit(Long fileUploadSizeLimit) {
        this.fileUploadSizeLimit = fileUploadSizeLimit;
    }

    public Long getFileVersionsMaxAge() {
        return fileVersionsMaxAge;
    }

    public void setFileVersionsMaxAge(Long fileVersionsMaxAge) {
        this.fileVersionsMaxAge = fileVersionsMaxAge;
    }

    /**
     * Whether or not the use is allowed to share with groups imported from LDAP.
     */
    @XmlElement(name="allow_sharing_with_ldap_groups")
    public Boolean getAllowShareWithLdapGroups() {
        return allowShareWithLdapGroups==null ? Boolean.TRUE : allowShareWithLdapGroups;
    }

    public void setAllowShareWithLdapGroups(Boolean allowShareWithLdapGroups) {
        this.allowShareWithLdapGroups = allowShareWithLdapGroups;
    }

    /**
     * Defines which email addresses are allowed or denied when sharing with external users.
     */
    @XmlElement(name="external_sharing_restrictions")
    public ExternalSharingRestrictions getSharingRestrictions() {
        return sharingRestrictions;
    }

    public void setSharingRestrictions(ExternalSharingRestrictions sharingRestrictions) {
        this.sharingRestrictions = sharingRestrictions;
    }
}
