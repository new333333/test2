/*
 * Copyright © 2009-2010 Novell, Inc.  All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.  IT MAY NOT BE USED, COPIED,
 * DISTRIBUTED, DISCLOSED, ADAPTED, PERFORMED, DISPLAYED, COLLECTED, COMPILED, OR LINKED WITHOUT NOVELL'S
 * PRIOR WRITTEN CONSENT.  USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE
 * PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 * NOVELL PROVIDES THE WORK "AS IS," WITHOUT ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING WITHOUT THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. NOVELL, THE
 * AUTHORS OF THE WORK, AND THE OWNERS OF COPYRIGHT IN THE WORK ARE NOT LIABLE FOR ANY CLAIM, DAMAGES,
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN
 * CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
 */
package org.kablink.teaming.rest.v1.model.admin;

import org.kablink.teaming.rest.v1.model.BaseRestObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: David
 * Date: 11/20/13
 * Time: 9:40 PM
 */
@XmlRootElement(name="admin_desktop_app_config")
public class DesktopAppAdminConfig extends BaseRestObject {
    private Boolean enabled;
    private Boolean allowCachedPassword;
    private Integer syncFrequencyInMinutes;
    private Integer maxSyncSizeInMBs;
    private DesktopProcessConfig processConfig;

    public DesktopAppAdminConfig() {
    }

    public Boolean getEnabled() {
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

    @XmlElement(name="max_file_size_mbs")
    public Integer getMaxSyncSizeInMBs() {
        return maxSyncSizeInMBs;
    }

    public void setMaxSyncSizeInMBs(Integer maxSyncSizeInMBs) {
        this.maxSyncSizeInMBs = maxSyncSizeInMBs;
    }

    @XmlElement(name="sync_interval_mins")
    public Integer getSyncFrequencyInMinutes() {
        return syncFrequencyInMinutes;
    }

    public void setSyncFrequencyInMinutes(Integer syncFrequencyInMinutes) {
        this.syncFrequencyInMinutes = syncFrequencyInMinutes;
    }

    @XmlElement(name="process_config")
    public DesktopProcessConfig getProcessConfig() {
        return processConfig;
    }

    public void setProcessConfig(DesktopProcessConfig processConfig) {
        this.processConfig = processConfig;
    }
}
