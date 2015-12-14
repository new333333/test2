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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * User: David
 * Date: 11/20/13
 * Time: 9:40 PM
 */
@XmlRootElement(name="desktop_process_config")
public class DesktopProcessConfig extends BaseRestObject {
    private Boolean allowUnlistedProcesses;
    private ProcessLists macProcesses;
    private ProcessLists windowsProcesses;

    public DesktopProcessConfig() {
    }

    @XmlElement(name="allow_unlisted_processes")
    public Boolean getAllowUnlistedProcesses() {
        return allowUnlistedProcesses;
    }

    public void setAllowUnlistedProcesses(Boolean allowUnlistedProcesses) {
        this.allowUnlistedProcesses = allowUnlistedProcesses;
    }

    @XmlElement(name="mac_processes")
    public ProcessLists getMacProcesses() {
        return macProcesses;
    }

    public void setMacProcesses(ProcessLists macProcesses) {
        this.macProcesses = macProcesses;
    }

    @XmlElement(name="windows_processes")
    public ProcessLists getWindowsProcesses() {
        return windowsProcesses;
    }

    public void setWindowsProcesses(ProcessLists windowsProcesses) {
        this.windowsProcesses = windowsProcesses;
    }
}
