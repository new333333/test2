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
import org.kablink.teaming.rest.v1.model.ExternalSharingRestrictions;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Global Share Settings.
 */
@XmlRootElement(name="share_settings")
public class ShareSettings extends BaseRestObject {
    private Boolean allowShareWithLdapGroups;
    private List<AssignedSharingPermission> sharingPermissions;
    private ExternalSharingRestrictions externalRestrictions;

    public ShareSettings() {
    }

    /**
     * Whether or not users can share with LDAP groups.
     */
    @XmlElement(name="allow_sharing_with_ldap_groups")
    public Boolean getAllowShareWithLdapGroups() {
        return allowShareWithLdapGroups;
    }

    public void setAllowShareWithLdapGroups(Boolean allowShareWithLdapGroups) {
        this.allowShareWithLdapGroups = allowShareWithLdapGroups;
    }

    /**
     * List of users and groups who have been assigned permissions to share.
     */
    @XmlElementWrapper(name="sharing_permissions")
    @XmlElement(name="permission")
    public List<AssignedSharingPermission> getSharingPermissions() {
        return sharingPermissions;
    }

    public void setSharingPermissions(List<AssignedSharingPermission> sharingPermissions) {
        this.sharingPermissions = sharingPermissions;
    }

    /**
     * Restricts sharing with external users by email address or email domain.
     */
    @XmlElement(name="external_restrictions")
    public ExternalSharingRestrictions getExternalRestrictions() {
        return externalRestrictions;
    }

    public void setExternalRestrictions(ExternalSharingRestrictions externalRestrictions) {
        this.externalRestrictions = externalRestrictions;
    }
}
