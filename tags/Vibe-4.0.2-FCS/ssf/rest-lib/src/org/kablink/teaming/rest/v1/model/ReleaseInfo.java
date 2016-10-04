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

import org.kablink.teaming.rest.v1.annotations.Undocumented;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Calendar;

/**
 * Server version information.
 */
@XmlRootElement(name = "release_info")
public class ReleaseInfo extends BaseRestObject {
    private String productName;
   	private String productVersion;
   	private Integer buildNumber;
   	private Calendar buildDate;
    private String applianceVersion;
    private Integer applianceBuildNumber;
   	private Calendar serverStartTime;
   	private Boolean licenseRequiredEdition;
   	private String contentVersion;
    private Integer restApiRevision;

    /**
     * Build date.
     */
    @XmlElement(name="build_date")
    public Calendar getBuildDate() {
        return buildDate;
    }

    /**
     * Product build number.
     */
    @XmlElement(name="build_number")
    public Integer getBuildNumber() {
        return buildNumber;
    }

    @XmlElement(name="content_version")
    public String getContentVersion() {
        return contentVersion;
    }

    /**
     * Whether the server is fully licensed.
     */
    @XmlElement(name="licensed_edition")
    public Boolean isLicenseRequiredEdition() {
        return licenseRequiredEdition;
    }

    /**
     * Name of the product.
     */
    @XmlElement(name="product_name")
    public String getProductName() {
        return productName;
    }

    /**
     * Product version.
     * @return
     */
    @XmlElement(name="product_version")
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Date and time when the server last started.
     * @return
     */
    @XmlElement(name="server_start_time")
    public Calendar getServerStartTime() {
        return serverStartTime;
    }

    public void setBuildDate(Calendar buildDate) {
        this.buildDate = buildDate;
    }

    public void setBuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber;
    }

    public void setContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
    }

    public void setLicenseRequiredEdition(Boolean licenseRequiredEdition) {
        this.licenseRequiredEdition = licenseRequiredEdition;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public void setServerStartTime(Calendar serverStartTime) {
        this.serverStartTime = serverStartTime;
    }

    @Undocumented
    @XmlElement(name="rest_api_revision")
    public Integer getRestApiRevision() {
        return restApiRevision;
    }

    public void setRestApiRevision(Integer restApiRevision) {
        this.restApiRevision = restApiRevision;
    }

    /**
     * Version of the server appliance.
     */
    @XmlElement(name="appliance_version")
    public String getApplianceVersion() {
        return applianceVersion;
    }

    public void setApplianceVersion(String applianceVersion) {
        this.applianceVersion = applianceVersion;
    }

    /**
     * Appliance build number.
     */
    @XmlElement(name="appliance_build_number")
    public Integer getApplianceBuildNumber() {
        return applianceBuildNumber;
    }

    public void setApplianceBuildNumber(Integer applianceBuildNumber) {
        this.applianceBuildNumber = applianceBuildNumber;
    }
}
