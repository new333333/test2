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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * User: david
 * Date: 5/16/12
 * Time: 2:48 PM
 */
@XmlRootElement(name = "desktop_app_config")
public class DesktopAppConfig {
    private Boolean enabled;
    private Boolean allowCachedPassword;
   	private Integer syncInterval;
   	private String autoUpdateUrl;
   	private Long maxFileSize;
    private List<NameHrefPair> branding;
    private DesktopAppProcessConfig processConfig;

    @XmlElement(name="auto_update_url")
    public String getAutoUpdateUrl() {
        return autoUpdateUrl;
    }

    public void setAutoUpdateUrl(String autoUpdateUrl) {
        this.autoUpdateUrl = autoUpdateUrl;
    }

    @XmlElement(name="enabled")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @XmlElement(name="allow_cached_password")
    public Boolean getAllowCachedPassword() {
        return allowCachedPassword;
    }

    public void setAllowCachedPassword(Boolean allowCachedPassword) {
        this.allowCachedPassword = allowCachedPassword;
    }

    @XmlElement(name="max_file_size")
    public Long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @XmlElement(name="sync_interval")
    public Integer getSyncInterval() {
        return syncInterval;
    }

    public void setSyncInterval(Integer syncInterval) {
        this.syncInterval = syncInterval;
    }

    @XmlElementWrapper(name="branding")
    @XmlElement(name="platform")
    public List<NameHrefPair> getBranding() {
        return branding;
    }

    public void setBranding(List<NameHrefPair> branding) {
        this.branding = branding;
    }

    public void addBrandingHref(NameHrefPair link) {
        if (link!=null) {
            if (branding == null) {
                branding = new ArrayList<NameHrefPair>();
            }
            branding.add(link);
        }
    }

    @XmlElement(name="process_config")
    public DesktopAppProcessConfig getProcessConfig() {
        return processConfig;
    }

    public void setProcessConfig(DesktopAppProcessConfig processConfig) {
        this.processConfig = processConfig;
    }
}
