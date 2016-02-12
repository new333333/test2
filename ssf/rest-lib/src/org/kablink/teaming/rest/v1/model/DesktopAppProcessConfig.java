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
package org.kablink.teaming.rest.v1.model;

import org.kablink.teaming.rest.v1.model.admin.ProcessInfo;
import org.kablink.teaming.rest.v1.model.admin.ProcessLists;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Configuration of processes that are allowed to trigger on-demand file downloads on the desktop.
 */
@XmlRootElement(name="desktop_process_config")
public class DesktopAppProcessConfig extends BaseRestObject {
    private Boolean allowUnlistedProcesses;
    private Boolean allowUnlistedProcessOverride;
    private List<String> allowedProcesses;
    private List<String> blockedProcesses;

    public DesktopAppProcessConfig() {
    }

    /**
     * Defines whether an unlisted process can trigger on-demand downloads.
     *
     * <p>An unlisted process is any process that is not included in either the allowed_process or blocked_process lists</p>
     */
    @XmlElement(name="allow_unlisted_processes")
    public Boolean getAllowUnlistedProcesses() {
        return allowUnlistedProcesses;
    }

    public void setAllowUnlistedProcesses(Boolean allowUnlistedProcesses) {
        this.allowUnlistedProcesses = allowUnlistedProcesses;
    }

    /**
     * Defines whether a user can override the "allow_unlisted_processes" setting.  Only applies if "allow_unlisted_processes" is false.
     * <p>When this is true, unlisted processes are blocked until the desktop user indicates that the process should be allowed.</p>
     */
    @XmlElement(name="allow_unlisted_process_override")
    public Boolean getAllowUnlistedProcessOverride() {
        return allowUnlistedProcessOverride;
    }

    public void setAllowUnlistedProcessOverride(Boolean allowUnlistedProcessOverride) {
        this.allowUnlistedProcessOverride = allowUnlistedProcessOverride;
    }

    /**
     * A list of process names that are allowed to trigger on-demand downloads on the desktop.
     *
     * <p>Only applicable when allow_unlisted_processes is false or allow_unlisted_process_override is true.</p>
     */
    @XmlElementWrapper(name="allowed_processes")
    @XmlElement(name="name")
    public List<String> getAllowedProcesses() {
        return allowedProcesses;
    }

    public void setAllowedProcesses(List<String> allowedProcesses) {
        this.allowedProcesses = allowedProcesses;
    }

    /**
     * A list of process names that are not allowed to trigger on-demand downloads on the desktop.
     *
     * <p>Only applicable when allow_unlisted_processes is true.</p>
     */
    @XmlElementWrapper(name="blocked_processes")
    @XmlElement(name="name")
    public List<String> getBlockedProcesses() {
        return blockedProcesses;
    }

    public void setBlockedProcesses(List<String> blockedProcesses) {
        this.blockedProcesses = blockedProcesses;
    }
}
