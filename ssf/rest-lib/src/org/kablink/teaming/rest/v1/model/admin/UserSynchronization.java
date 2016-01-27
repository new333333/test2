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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User: David
 * Date: 11/20/13
 * Time: 9:40 PM
 */
@XmlRootElement(name="user_synchronization")
public class UserSynchronization {
    public enum RemovedAccountAction {
        disable,
        delete
    }

    private Boolean register;
    private Boolean syncProfiles;
    private String removedAccountAction;
    private Boolean deleteWorkspace;
    private String defaultTimezone;
    private String defaultLocale;

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    @XmlElement(name="sync_profiles")
    public Boolean getSyncProfiles() {
        return syncProfiles;
    }

    public void setSyncProfiles(Boolean syncProfiles) {
        this.syncProfiles = syncProfiles;
    }

    @XmlElement(name="removed_account_action")
    public String getRemovedAccountAction() {
        return removedAccountAction;
    }

    public void setRemovedAccountAction(String removedAccountAction) {
        this.removedAccountAction = removedAccountAction;
    }

    @XmlElement(name="delete_workspace")
    public Boolean getDeleteWorkspace() {
        return deleteWorkspace;
    }

    public void setDeleteWorkspace(Boolean deleteWorkspace) {
        this.deleteWorkspace = deleteWorkspace;
    }

    @XmlElement(name="default_timezone")
    public String getDefaultTimezone() {
        return defaultTimezone;
    }

    public void setDefaultTimezone(String defaultTimezone) {
        this.defaultTimezone = defaultTimezone;
    }

    @XmlElement(name="default_locale")
    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }
}
