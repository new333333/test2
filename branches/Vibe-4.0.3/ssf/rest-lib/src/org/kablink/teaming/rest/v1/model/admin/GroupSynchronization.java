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
 * The LDAP group synchronization settings.
 */
@XmlRootElement(name="group_synchronization")
public class GroupSynchronization {
    private Boolean register;
    private Boolean syncProfiles;
    private Boolean syncMembership;
    private Boolean deleteRemovedGroups;

    /**
     * Register LDAP group profiles automatically.
     */
    @XmlElement(name="register")
    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    /**
     * Synchronize group profiles.
     */
    @XmlElement(name="sync_profiles")
    public Boolean getSyncProfiles() {
        return syncProfiles;
    }

    public void setSyncProfiles(Boolean syncProfiles) {
        this.syncProfiles = syncProfiles;
    }

    /**
     * Synchronize group membership.
     */
    @XmlElement(name="sync_membership")
    public Boolean getSyncMembership() {
        return syncMembership;
    }

    public void setSyncMembership(Boolean syncMembership) {
        this.syncMembership = syncMembership;
    }

    /**
     * Delete groups that were provisioned from LDAP but are no longer in LDAP.
     * @return
     */
    @XmlElement(name="delete_removed_groups")
    public Boolean getDeleteRemovedGroups() {
        return deleteRemovedGroups;
    }

    public void setDeleteRemovedGroups(Boolean deleteRemovedGroups) {
        this.deleteRemovedGroups = deleteRemovedGroups;
    }
}
