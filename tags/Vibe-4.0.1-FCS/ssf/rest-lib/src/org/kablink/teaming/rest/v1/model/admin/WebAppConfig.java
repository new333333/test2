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

import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.BaseRestObject;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Web Application settings.
 */
@XmlRootElement(name="web_app_config")
public class WebAppConfig extends BaseRestObject {
    private Boolean enabled;
    private Boolean allowGuestAccess;
    private Boolean readOnlyGuest;
    private Boolean allowOpenId;
    private Boolean allowDownloads;

    public WebAppConfig() {
    }

    /**
     * Whether or not the web application is enabled.
     */
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Whether or not guest access is enabled.  Must be true for users to share publicly.
     */
    @XmlElement(name="allow_guest_access")
    public Boolean getAllowGuestAccess() {
        return allowGuestAccess;
    }

    public void setAllowGuestAccess(Boolean allowGuestAccess) {
        this.allowGuestAccess = allowGuestAccess;
    }

    /**
     * Whether or not guest access is limited to read-only.
     */
    @XmlElement(name="read_only_guest")
    public Boolean getReadOnlyGuest() {
        return readOnlyGuest;
    }

    public void setReadOnlyGuest(Boolean readOnlyGuest) {
        this.readOnlyGuest = readOnlyGuest;
    }

    @Undocumented
    @XmlElement(name="allow_open_id")
    public Boolean getAllowOpenId() {
        return allowOpenId;
    }

    public void setAllowOpenId(Boolean allowOpenId) {
        this.allowOpenId = allowOpenId;
    }

    /**
     * Whether or not users can download files from the web application.
     */
    @XmlElement(name="allow_downloads")
    public Boolean getAllowDownloads() {
        return allowDownloads;
    }

    public void setAllowDownloads(Boolean allowDownloads) {
        this.allowDownloads = allowDownloads;
    }
}
