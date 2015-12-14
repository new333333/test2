/*
 * Copyright © 2009-2015 Novell, Inc.  All Rights Reserved.
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

import java.util.Date;

/**
 * User: david
 * Date: 7/25/12
 * Time: 11:38 AM
 */
@XmlRootElement(name="net_folder_server")
public class NetFolderServer extends BaseRestObject {
    private Long id; // This is primary key
    private String name;
    private String driverType;
    private Boolean fullSyncDirOnly;
    private String rootPath;
    private String accountName;
    private String password; //set by hibernate access="field" type="encrypted"
	private Boolean useProxyIdentity;
	private Long proxyIdentityId;
    private Date modifiedOn;
    private String changeDetectionMechanism;
    private String authenticationType;
    private Schedule syncSchedule;
    private Boolean indexContent;
    private Boolean jitsEnabled;
    private Long jitsMaxAge;
    private Long jitsMaxACLAge;
    private Boolean allowClientInitiatedSync;

    public void replaceNullValues(NetFolderServer server) {
        id = (id==null) ? server.id : id;
        name = (name==null) ? server.name : name;
        driverType = (driverType==null) ? server.driverType : driverType;
        fullSyncDirOnly = (fullSyncDirOnly==null) ? server.fullSyncDirOnly : fullSyncDirOnly;
        rootPath = (rootPath==null) ? server.rootPath : rootPath;
        accountName = (accountName==null) ? server.accountName : accountName;
        password = (password==null) ? server.password : password;
        useProxyIdentity = (useProxyIdentity == null) ? server.useProxyIdentity : useProxyIdentity;
        proxyIdentityId = (proxyIdentityId == null) ? server.proxyIdentityId : proxyIdentityId;
        changeDetectionMechanism = (changeDetectionMechanism==null) ? server.changeDetectionMechanism : changeDetectionMechanism;
        authenticationType = (authenticationType==null) ? server.authenticationType : authenticationType;
        indexContent = (indexContent==null) ? server.indexContent : indexContent;
        jitsEnabled = (jitsEnabled==null) ? server.jitsEnabled : jitsEnabled;
        jitsMaxAge = (jitsMaxAge==null) ? server.jitsMaxAge : jitsMaxAge;
        jitsMaxACLAge = (jitsMaxACLAge==null) ? server.jitsMaxACLAge : jitsMaxACLAge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="driver_type")
    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    @XmlElement(name="only_sync_dirs")
    public Boolean getFullSyncDirOnly() {
        return fullSyncDirOnly!=null && fullSyncDirOnly;
    }

    public void setFullSyncDirOnly(Boolean fullSyncDirOnly) {
        this.fullSyncDirOnly = fullSyncDirOnly;
    }

    @XmlElement(name="server_path")
    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @XmlElement(name="proxy_dn")
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @XmlElement(name="proxy_password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @XmlElement(name="proxy_use_identity")
    public Boolean getUseProxyIdentity() {
    	if (null == useProxyIdentity) {
    		return false;
    	}
        return useProxyIdentity;
    }

    public void setUseProxyIdentity(Boolean useProxyIdentity) {
        this.useProxyIdentity = useProxyIdentity;
    }

    @XmlElement(name="proxy_identity_id")
    public Long getProxyIdentityId() {
        return proxyIdentityId;
    }

    public void setProxyIdentityId(Long proxyIdentityId) {
        this.proxyIdentityId = proxyIdentityId;
    }

    @XmlElement(name="last_modified")
    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    @XmlElement(name="change_detection_mechanism")
    public String getChangeDetectionMechanism() {
        return changeDetectionMechanism;
    }

    public void setChangeDetectionMechanism(String changeDetectionMechanism) {
        this.changeDetectionMechanism = changeDetectionMechanism;
    }

    @XmlElement(name="auth_type")
    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    @XmlElement(name="sync_schedule")
    public Schedule getSyncSchedule() {
        return syncSchedule;
    }

    public void setSyncSchedule(Schedule syncSchedule) {
        this.syncSchedule = syncSchedule;
    }

    @XmlElement(name="index_content")
    public Boolean getIndexContent() {
        return indexContent==null ? Boolean.FALSE : indexContent;
    }

    public void setIndexContent(Boolean indexContent) {
        this.indexContent = indexContent;
    }

    @XmlElement(name="jits_enabled")
    public Boolean getJitsEnabled() {
        return jitsEnabled;
    }

    public void setJitsEnabled(Boolean jitsEnabled) {
        this.jitsEnabled = jitsEnabled;
    }

    @XmlElement(name="jits_max_age")
    public Long getJitsMaxAge() {
        return jitsMaxAge;
    }

    public void setJitsMaxAge(Long jitsMaxAge) {
        this.jitsMaxAge = jitsMaxAge;
    }

    @XmlElement(name="jits_max_acl_age")
    public Long getJitsMaxACLAge() {
        return jitsMaxACLAge;
    }

    public void setJitsMaxACLAge(Long jitsMaxACLAge) {
        this.jitsMaxACLAge = jitsMaxACLAge;
    }

    @XmlElement(name="allow_client_initiated_sync")
    public Boolean getAllowClientInitiatedSync() {
        return allowClientInitiatedSync;
    }

    public void setAllowClientInitiatedSync(Boolean allowClientInitiatedSync) {
        this.allowClientInitiatedSync = allowClientInitiatedSync;
    }
}
