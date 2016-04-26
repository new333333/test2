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
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * The metadata of a Net Folder
 */
@XmlRootElement(name="net_folder")
public class NetFolder extends BaseRestObject {
    public enum Type {
        net,
        home
    }
    private Long id;
    private String name;
    private LongIdLinkPair server;
    private String relativePath;
    private String type;
    private Boolean homeDir;
    private Boolean indexContent;
    private Boolean inheritIndexContent;
    private Boolean inheritJitsSettings;
    private Boolean jitsEnabled;
    private Long jitsMaxAge;
    private Long jitsMaxACLAge;
    private Boolean fullSyncDirOnly;
    private Boolean allowDesktopSync;
    private Boolean inheritClientSyncSettings;
    private Boolean allowClientInitiatedSync;
    private Boolean inheritSyncSchedule;
    private Schedule syncSchedule;
    private List<AssignedRight> assignedRights;

    public void replaceNullValues(NetFolder folder) {
        id = (id==null) ? folder.id : id;
        name = (name==null) ? folder.name : name;
        server = (server==null) ? folder.server : server;
        relativePath = (relativePath==null) ? folder.relativePath : relativePath;
        homeDir = (homeDir==null) ? folder.homeDir : homeDir;
        indexContent = (indexContent==null) ? folder.indexContent : indexContent;
        inheritIndexContent = (inheritIndexContent==null) ? folder.inheritIndexContent : inheritIndexContent;
        inheritJitsSettings = (inheritJitsSettings==null) ? folder.inheritJitsSettings : inheritJitsSettings;
        jitsMaxAge = (jitsMaxAge==null) ? folder.jitsMaxAge : jitsMaxAge;
        jitsMaxACLAge = (jitsMaxACLAge==null) ? folder.jitsMaxACLAge : jitsMaxACLAge;
        fullSyncDirOnly = (fullSyncDirOnly==null) ? folder.fullSyncDirOnly : fullSyncDirOnly;
        allowDesktopSync = (allowDesktopSync==null) ? folder.allowDesktopSync : allowDesktopSync;
        inheritClientSyncSettings = (inheritClientSyncSettings==null) ? folder.inheritClientSyncSettings : inheritClientSyncSettings;
        allowClientInitiatedSync = (allowClientInitiatedSync==null) ? folder.allowClientInitiatedSync : allowClientInitiatedSync;
        inheritSyncSchedule = (inheritSyncSchedule==null) ? folder.inheritSyncSchedule : inheritSyncSchedule;
    }

    /**
     * The ID of the Net Folder
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * The name of the Net Folder,
     * <p>This must be unique.</p>
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * The ID and HRef of the Net Folder Server associated with this Net Folder.
     */
    public LongIdLinkPair getServer() {
        return server;
    }

    public void setServer(LongIdLinkPair server) {
        this.server = server;
    }

    /**
     * The path to this Net Folder.
     *
     * <p>This path is relative to the base path defined in the associated Net Folder Server.</p>
     */
    @XmlElement(name="relative_path")
    public String getRelativePath() {
        if (relativePath==null) {
            return "";
        } else {
            return relativePath;
        }
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    /**
     * True if this is a user's home directory Net Folder.
     */
    @XmlElement(name="home_dir")
    public Boolean getHomeDir() {
        return homeDir;
    }

    public void setHomeDir(Boolean homeDir) {
        this.homeDir = homeDir;
    }

    /**
     * True if the file content in the net folder is indexed.
     *
     * <p>This attribute is ignored if <code>inherit_index_content==true</code></p>
     */
    @XmlElement(name="index_content")
    public Boolean getIndexContent() {
        return indexContent==null ? Boolean.FALSE : indexContent;
    }

    public void setIndexContent(Boolean indexContent) {
        this.indexContent = indexContent;
    }

    /**
     * Flag indicating whether the <code>index_content</code> setting should be inherited from the Net Folder Server.
     */
    @XmlElement(name="inherit_index_content")
    public Boolean getInheritIndexContent() {
        return inheritIndexContent==null ? Boolean.TRUE : inheritIndexContent;
    }

    public void setInheritIndexContent(Boolean inherit) {
        this.inheritIndexContent = inherit;
    }

    /**
     * Flag indicating whether the JITS settings should be inherited from the Net Folder Server.
     */
    @XmlElement(name="inherit_jits_settings")
    public Boolean getInheritJitsSettings() {
        return inheritJitsSettings==null ? Boolean.TRUE : inheritJitsSettings;
    }

    public void setInheritJitsSettings(Boolean inherit) {
        this.inheritJitsSettings = inherit;
    }

    /**
     * Flag indicating whether JITS is enabled for this Net Folder
     *
     * <p>This attribute is ignored if <code>inherit_jits_settings==true</code></p>
     */
    @XmlElement(name="jits_enabled")
    public Boolean getJitsEnabled() {
        return jitsEnabled==null ? Boolean.FALSE : jitsEnabled;
    }

    public void setJitsEnabled(Boolean jitsEnabled) {
        this.jitsEnabled = jitsEnabled;
    }

    /**
     * Maximum age for JITS results, in seconds.
     *
     * <p>This attribute is ignored if <code>inherit_jits_settings==true</code></p>
     */
    @XmlElement(name="jits_max_age")
    public Long getJitsMaxAge() {
        return jitsMaxAge==null ? 30L : jitsMaxAge;
    }

    public void setJitsMaxAge(Long jitsMaxAge) {
        this.jitsMaxAge = jitsMaxAge;
    }

    /**
     * Maximum age for ACL JITS results, in seconds.
     *
     * <p>This attribute is ignored if <code>inherit_jits_settings==true</code></p>
     */
    @XmlElement(name="jits_max_acl_age")
    public Long getJitsMaxACLAge() {
        return jitsMaxACLAge==null ? 60L : jitsMaxACLAge;
    }

    public void setJitsMaxACLAge(Long jitsMaxACLAge) {
        this.jitsMaxACLAge = jitsMaxACLAge;
    }

    @Undocumented
    @XmlElement(name="only_sync_dirs")
    public Boolean getFullSyncDirOnly() {
        return fullSyncDirOnly;
    }

    public void setFullSyncDirOnly(Boolean fullSyncDirOnly) {
        this.fullSyncDirOnly = fullSyncDirOnly;
    }

    /**
     * Flag indicating whether the desktop application can sync files in the Net Folder.
     */
    @XmlElement(name="allow_desktop_sync")
    public Boolean getAllowDesktopSync() {
        return allowDesktopSync==null ? Boolean.FALSE : allowDesktopSync;
    }

    public void setAllowDesktopSync(Boolean allowDesktopSync) {
        this.allowDesktopSync = allowDesktopSync;
    }

    @Undocumented
    @XmlElement(name="inherit_client_sync_settings")
    public Boolean getInheritClientSyncSettings() {
        return inheritClientSyncSettings==null ? Boolean.TRUE : inheritClientSyncSettings;
    }

    public void setInheritClientSyncSettings(Boolean inheritClientSyncSettings) {
        this.inheritClientSyncSettings = inheritClientSyncSettings;
    }

    @Undocumented
    @XmlElement(name="allow_client_initiated_sync")
    public Boolean getAllowClientInitiatedSync() {
        return allowClientInitiatedSync;
    }

    public void setAllowClientInitiatedSync(Boolean allowClientInitiatedSync) {
        this.allowClientInitiatedSync = allowClientInitiatedSync;
    }

    /**
     * Flag indicating whether the synchronization schedule should be inherited from the Net Folder Server.
     */
    @XmlElement(name="inherit_sync_schedule")
    public Boolean getInheritSyncSchedule() {
        return inheritSyncSchedule==null ? Boolean.FALSE : inheritSyncSchedule;
    }

    public void setInheritSyncSchedule(Boolean inheritSyncSchedule) {
        this.inheritSyncSchedule = inheritSyncSchedule;
    }

    /**
     * The synchronization schedule.
     *
     * <p>This attribute is ignored if <code>inherit_sync_schedule==true</code></p>
     */
    @XmlElement(name="sync_schedule")
    public Schedule getSyncSchedule() {
        return syncSchedule;
    }

    public void setSyncSchedule(Schedule syncSchedule) {
        this.syncSchedule = syncSchedule;
    }

    /**
     * List of users or groups that are allowed to access the Net Folder.
     *
     * <p>The only supported role in the AssignedRight Access object is <code>ACCESS</code></p>
     */
    @XmlElementWrapper(name="assigned_rights")
    @XmlElement(name="assigned_right")
    public List<AssignedRight> getAssignedRights() {
        return assignedRights;
    }

    public void setAssignedRights(List<AssignedRight> assignedRights) {
        this.assignedRights = assignedRights;
    }
}
