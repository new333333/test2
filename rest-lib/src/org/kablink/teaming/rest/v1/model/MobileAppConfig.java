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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.rest.v1.model;

import org.kablink.teaming.rest.v1.annotations.Undocumented;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * A user's effective mobile application settings.
 */
@XmlRootElement(name = "mobile_app_config")
public class MobileAppConfig {

    public enum OpenInApps {
        all,
        none,
        selected
    }

    private Boolean enabled;
    private Boolean allowCachedPassword;
   	private Boolean allowCachedContent;
   	private Boolean allowPlayWithOtherApps;
   	private Boolean forcePinCode;
   	private Integer syncInterval;
    private Boolean	allowCutCopy;
    private Boolean	allowScreenCapture;
    private Boolean	allowRootedDevices;
    private String allowedOpenInApps;
    private List<NameHrefPair> branding;
    private List<String> androidAppWhiteList;
    private List<String> iOSAppWhiteList;

    /**
     * Whether or not users should be able to use the mobile application.  This is not enforced by the server.
     * It's up to mobile applications to honor this setting.
     */
    @XmlElement(name="enabled")
    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Whether users can save their password or must provide their password each time they open the mobile application.
     */
    @XmlElement(name="allow_cached_password")
    public Boolean getAllowCachedPassword() {
        return allowCachedPassword;
    }

    public void setAllowCachedPassword(Boolean allowCachedPassword) {
        this.allowCachedPassword = allowCachedPassword;
    }

    /**
     * Whether users can download files in the mobile application for viewing while offline.
     */
    @XmlElement(name="allow_cached_content")
    public Boolean getAllowCachedContent() {
        return allowCachedContent;
    }

    public void setAllowCachedContent(Boolean allowCachedContent) {
        this.allowCachedContent = allowCachedContent;
    }

    @Undocumented
    @XmlElement(name="allow_play_with_other_apps")
    public Boolean getAllowPlayWithOtherApps() {
        return allowPlayWithOtherApps;
    }

    public void setAllowPlayWithOtherApps(Boolean allowPlayWithOtherApps) {
        this.allowPlayWithOtherApps = allowPlayWithOtherApps;
    }

    /**
     * Require users to use a PIN code to access the mobile application.
     * @return
     */
    @XmlElement(name="force_pin_code")
    public Boolean getForcePinCode() {
        return forcePinCode;
    }

    public void setForcePinCode(Boolean forcePinCode) {
        this.forcePinCode = forcePinCode;
    }

    /**
     * How often, in minutes, that the mobile applications should check with the server for updated settings and modified files.
     */
    @XmlElement(name="sync_interval")
    public Integer getSyncInterval() {
        return syncInterval;
    }

    public void setSyncInterval(Integer syncInterval) {
        this.syncInterval = syncInterval;
    }

    /**
     * Whether users can cut or copy data from the mobile application so the data can be pasted into third-party applications.
     */
    @XmlElement(name="allow_cut_copy")
    public Boolean getAllowCutCopy() {
        return allowCutCopy;
    }

    public void setAllowCutCopy(Boolean allowCutCopy) {
        this.allowCutCopy = allowCutCopy;
    }

    /**
     * Whether users can take a screen capture while inside the mobile application.
     */
    @XmlElement(name="allow_screen_capture")
    public Boolean getAllowScreenCapture() {
        return allowScreenCapture;
    }

    public void setAllowScreenCapture(Boolean allowScreenCapture) {
        this.allowScreenCapture = allowScreenCapture;
    }

    /**
     * Whether users are allowed to run the mobile application on a rooted or jail-broken device.
     */
    @XmlElement(name="allow_rooted_devices")
    public Boolean getAllowRootedDevices() {
        return allowRootedDevices;
    }

    public void setAllowRootedDevices(Boolean allowRootedDevices) {
        this.allowRootedDevices = allowRootedDevices;
    }

    /**
     * Controls the user's ability to send files to third-party applications for viewing and editing.
     *
     * <p>Possible values are:
     * <ul>
     *     <li><code>all</code> (user can send files to any application)</li>
     *     <li><code>none</code> (user cannot send files to any application)</li>
     *     <li><code>selected</code> (user can only send files applications listed in the platform-specific whitelist
     *     (<code>android_app_whitelist</code> or <code>ios_app_whitelist</code></li>
     * </ul>
     * </p>
     */
    @XmlElement(name="allowed_open_in_apps")
    public String getAllowedOpenInApps() {
        return allowedOpenInApps;
    }

    public void setAllowedOpenInApps(String allowedOpenInApps) {
        this.allowedOpenInApps = allowedOpenInApps;
    }

    /**
     * List of Android applications to which the user is allowed to send files.  Only applies if <code>allowed_open_in_apps</code> is <code>selected</code>.
     */
    @XmlElementWrapper(name="android_app_whitelist")
    @XmlElement(name="app")
    public List<String> getAndroidAppWhiteList() {
        return androidAppWhiteList;
    }

    public void setAndroidAppWhiteList(List<String> androidAppWhiteList) {
        this.androidAppWhiteList = androidAppWhiteList;
    }

    @Undocumented
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

    /**
     * List of iOS applications to which the user is allowed to send files.  Only applies if <code>allowed_open_in_apps</code> is <code>selected</code>.
     */
    @XmlElementWrapper(name="ios_app_whitelist")
    @XmlElement(name="app")
    public List<String> getiOSAppWhiteList() {
        return iOSAppWhiteList;
    }

    public void setiOSAppWhiteList(List<String> iOSAppWhiteList) {
        this.iOSAppWhiteList = iOSAppWhiteList;
    }
}
