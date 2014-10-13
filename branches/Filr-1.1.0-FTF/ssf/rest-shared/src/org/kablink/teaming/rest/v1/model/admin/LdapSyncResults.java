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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * User: david
 * Date: 7/25/12
 * Time: 11:38 AM
 */
@XmlRootElement(name="ldap_sync_results")
public class LdapSyncResults {
    private String status;
    private List<String> addedUsers;
    private List<String> modifiedUsers;
    private List<String> deletedUsers;
    private List<String> disabledUsers;
    private List<String> addedGroups;
    private List<String> modifiedGroups;
    private List<String> deletedGroups;
    private String error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlElementWrapper(name="added_users")
    @XmlElement(name="user")
    public List<String> getAddedUsers() {
        return addedUsers;
    }

    public void setAddedUsers(List<String> addedUsers) {
        this.addedUsers = addedUsers;
    }

    @XmlElementWrapper(name="modified_users")
    @XmlElement(name="user")
    public List<String> getModifiedUsers() {
        return modifiedUsers;
    }

    public void setModifiedUsers(List<String> modifiedUsers) {
        this.modifiedUsers = modifiedUsers;
    }

    @XmlElementWrapper(name="deleted_users")
    @XmlElement(name="user")
    public List<String> getDeletedUsers() {
        return deletedUsers;
    }

    public void setDeletedUsers(List<String> deletedUsers) {
        this.deletedUsers = deletedUsers;
    }

    @XmlElementWrapper(name="disabled_users")
    @XmlElement(name="user")
    public List<String> getDisabledUsers() {
        return disabledUsers;
    }

    public void setDisabledUsers(List<String> disabledUsers) {
        this.disabledUsers = disabledUsers;
    }

    @XmlElementWrapper(name="added_groups")
    @XmlElement(name="group")
    public List<String> getAddedGroups() {
        return addedGroups;
    }

    public void setAddedGroups(List<String> addedGroups) {
        this.addedGroups = addedGroups;
    }

    @XmlElementWrapper(name="modified_groups")
    @XmlElement(name="group")
    public List<String> getModifiedGroups() {
        return modifiedGroups;
    }

    public void setModifiedGroups(List<String> modifiedGroups) {
        this.modifiedGroups = modifiedGroups;
    }

    @XmlElementWrapper(name="deleted_groups")
    @XmlElement(name="group")
    public List<String> getDeletedGroups() {
        return deletedGroups;
    }

    public void setDeletedGroups(List<String> deletedGroups) {
        this.deletedGroups = deletedGroups;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
