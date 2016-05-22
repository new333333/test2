/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.rest.v1.resource.admin;

import com.sun.jersey.spi.resource.Singleton;
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import com.webcohesion.enunciate.metadata.rs.ResourceLabel;
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.OpenIDConfig;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.admin.*;
import org.kablink.teaming.rest.v1.model.ExternalSharingRestrictions;
import org.kablink.teaming.util.DesktopApplicationsLists;
import org.kablink.teaming.util.ShareLists;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.AssignedRole;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/admin")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceLabel("Admin Miscellaneous Resource")
public class MainAdminResource extends AbstractAdminResource {

    /**
     * Returns links that can be used to navigate the Admin REST APIs.
     *
     * @return
     */
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Admin Root")
   	public RootAdminObject getRootObject() {
        RootAdminObject obj = new RootAdminObject();
        obj.addAdditionalLink("desktop_application", "/admin/desktop_application");
        obj.addAdditionalLink("net_folder_servers", "/admin/net_folder_servers");
        obj.addAdditionalLink("net_folders", "/admin/net_folders");
        obj.addAdditionalLink("personal_storage", "/admin/personal_storage");
        obj.addAdditionalLink("public_shares", "/admin/shares/public");
        obj.addAdditionalLink("shares", "/admin/shares");
        obj.addAdditionalLink("share_settings", "/admin/share_settings");
        obj.addAdditionalLink("user_sources", "/admin/user_sources");
        obj.addAdditionalLink("user_source_sync", "/admin/user_sources/sync");
        obj.addAdditionalLink("user_source_sync_config", "/admin/user_sources/sync_config");
        obj.addAdditionalLink("web_application", "/admin/web_application");
   		return obj;
   	}

    /**
     * Gets the current Personal Storage settings.
     * @return The Personal Storage settings
     */
    @GET
    @Path("/personal_storage")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Personal Storage")
    public PersonalStorage getPersonalStorageSettings() {
        PersonalStorage settings = new PersonalStorage();
        settings.setLink("/admin/personal_storage");
        settings.setAllowPersonalStorage(AdminHelper.getAdhocFolderSettingFromZone(this));
        return settings;
    }

    /**
     * Updates the Personal Storage settings.
     * @return The updated Personal Storage settings
     */
    @PUT
    @Path("/personal_storage")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Personal Storage")
    public PersonalStorage updatePersonalStorageSettings(PersonalStorage settings) {
        validateMandatoryField(settings, "getAllowPersonalStorage");
        getAdminModule().setAdHocFoldersEnabled(settings.getAllowPersonalStorage());
        return getPersonalStorageSettings();
    }

    /**
     * Gets the current Web Application settings.
     * @return The Web Application settings
     */
    @GET
    @Path("/web_application")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Web Application")
    public WebAppConfig getWebApplicationSettings() {
        WebAppConfig settings = new WebAppConfig();
        settings.setLink("/admin/web_application");
        settings.setEnabled(getAdminModule().isWebAccessEnabled());
        settings.setAllowDownloads(getAdminModule().isDownloadEnabled());
        AuthenticationConfig authConfig = getAuthenticationModule().getAuthenticationConfig();
        settings.setAllowGuestAccess(authConfig.isAllowAnonymousAccess());
        settings.setReadOnlyGuest(authConfig.isAnonymousReadOnly());
        org.kablink.teaming.domain.ZoneConfig zoneConfig = getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
        OpenIDConfig openIdConfig = zoneConfig.getOpenIDConfig();
        settings.setAllowOpenId(zoneConfig.isExternalUserEnabled() && openIdConfig.isAuthenticationEnabled());
        return settings;
    }

    /**
     * Updates the Web Application settings.
     * Only the fields that are included in the request body will be updated.
     * @return The Web Application settings
     */
    @PUT
    @Path("/web_application")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Web Application")
    public WebAppConfig updateWebAppSettings(WebAppConfig settings) {
        if (settings.getEnabled()!=null) {
            getAdminModule().setWebAccessEnabled(settings.getEnabled());
        }
        if (settings.getAllowDownloads()!=null) {
            getAdminModule().setDownloadEnabled(settings.getAllowDownloads());
        }
        if (settings.getAllowGuestAccess()!=null || settings.getReadOnlyGuest()!=null) {
            AuthenticationConfig authConfig = getAuthenticationModule().getAuthenticationConfig();
            if (settings.getAllowGuestAccess()!=null) {
                authConfig.setAllowAnonymousAccess(settings.getAllowGuestAccess());
            }
            if (settings.getReadOnlyGuest()!=null) {
                authConfig.setAnonymousReadOnly(settings.getReadOnlyGuest());
            }
            getAuthenticationModule().setAuthenticationConfig(authConfig);
        }
        if (settings.getAllowOpenId()!=null) {
            org.kablink.teaming.domain.ZoneConfig zoneConfig = getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());

            OpenIDConfig openIdConfig = zoneConfig.getOpenIDConfig();
            openIdConfig.setAuthenticationEnabled( settings.getAllowOpenId() );

            getAdminModule().setOpenIDConfig( openIdConfig );
        }

        return getWebApplicationSettings();
    }

    /**
     * Gets the current Desktop Application settings.
     * @return The Desktop Application settings
     */
    @GET
    @Path("/desktop_application")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Desktop Application")
    public DesktopAppAdminConfig getDesktopApplicationSettings() {
        DesktopAppAdminConfig settings = new  DesktopAppAdminConfig();
        settings.setLink("/admin/desktop_application");
        settings.addAdditionalLink("process_config", "/admin/desktop_application/process_config");
        org.kablink.teaming.domain.ZoneConfig zoneConfig = getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
        settings.setEnabled(zoneConfig.getFsaEnabled());
        settings.setAllowCachedPassword(zoneConfig.getFsaAllowCachePwd());
        settings.setSyncFrequencyInMinutes(zoneConfig.getFsaSynchInterval());
        settings.setMaxSyncSizeInMBs(zoneConfig.getFsaMaxFileSize());
        settings.setProcessConfig(getDesktopProcessConfig());
        return settings;
    }

    /**
     * Updates the Desktop Application settings.  Only the fields that are included in the request body are modified.
     * Any fields left out will remain unchanged.
     * @return The full Desktop Appication settings.
     */
    @PUT
    @Path("/desktop_application")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Desktop Application")
    public DesktopAppAdminConfig updateDesktopAppSettings(DesktopAppAdminConfig settings) {
        getAdminModule().setFileSynchAppSettings(settings.getEnabled(), settings.getSyncFrequencyInMinutes(), null, null,
                null, settings.getAllowCachedPassword(), settings.getMaxSyncSizeInMBs(), null, null, null, null);

        return getDesktopApplicationSettings();
    }

    /**
     * Not yet supported
     * @return
     */
    @GET
    @Path("/desktop_application/process_config")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Desktop Application")
    @Undocumented
    public DesktopProcessConfig getDesktopProcessConfig() {
        DesktopProcessConfig processConfig = new DesktopProcessConfig();
        org.kablink.teaming.domain.ZoneConfig zoneConfig = getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
        DesktopApplicationsLists appLists = zoneConfig.getDesktopApplicationsLists();
        processConfig.setAllowUnlistedProcesses(!appLists.isWhitelist());
        if (!appLists.isDisabled()) {
            processConfig.setMacProcesses(toProcessLists(appLists.getMacWhitelist(), appLists.getMacBlacklist()));
            processConfig.setWindowsProcesses(toProcessLists(appLists.getWindowsWhitelist(), appLists.getWindowsBlacklist()));
        }
        return processConfig;
    }

    /**
     * Not yet supported
     * @return
     */
    @PUT
    @Path("/desktop_application/process_config")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Desktop Application")
    @Undocumented
    public DesktopProcessConfig updateDesktopProcessConfig(DesktopProcessConfig config) {
        org.kablink.teaming.domain.ZoneConfig zoneConfig = getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
        DesktopApplicationsLists appLists = zoneConfig.getDesktopApplicationsLists();

        if (config.getAllowUnlistedProcesses()==Boolean.TRUE) {
            appLists.setAppListMode(DesktopApplicationsLists.AppListMode.BLACKLIST);
        } else if (config.getAllowUnlistedProcesses()==Boolean.FALSE) {
            appLists.setAppListMode(DesktopApplicationsLists.AppListMode.WHITELIST);
        }

        ProcessLists mac = config.getMacProcesses();
        if (mac!=null) {
            populateAppList(appLists.getMacWhitelist(), mac.getAllowedProcesses());
            populateAppList(appLists.getMacBlacklist(), mac.getBlockedProcesses());
        }
        ProcessLists win = config.getWindowsProcesses();
        if (win != null) {
            populateAppList(appLists.getWindowsWhitelist(), win.getAllowedProcesses());
            populateAppList(appLists.getWindowsBlacklist(), win.getBlockedProcesses());
        }
        getAdminModule().setFileSynchAppSettings(null, null, null, null, null, null, null, appLists, null, null, null);
        return getDesktopProcessConfig();
    }

    private void populateAppList(List<DesktopApplicationsLists.AppInfo> appList, List<ProcessInfo> procList) {
        if (appList!=null && procList!=null) {
            appList.clear();
            for (ProcessInfo proc : procList) {
                appList.add(new DesktopApplicationsLists.AppInfo(proc.getDescription(), proc.getName()));
            }
        }
    }

    private List<DesktopApplicationsLists.AppInfo> toAppInfoList(List<ProcessInfo> processList) {
        List<DesktopApplicationsLists.AppInfo> appList = new ArrayList<DesktopApplicationsLists.AppInfo>();
        for (ProcessInfo proc : processList) {
            DesktopApplicationsLists.AppInfo appInfo = new DesktopApplicationsLists.AppInfo(proc.getDescription(), proc.getName());
            appList.add(appInfo);
        }
        return appList;

    }

    private ProcessLists toProcessLists(List<DesktopApplicationsLists.AppInfo> whiteList, List<DesktopApplicationsLists.AppInfo> blackList) {
        ProcessLists lists = new ProcessLists();
        lists.setAllowedProcesses(toProcessInfoList(whiteList));
        lists.setBlockedProcesses(toProcessInfoList(blackList));
        return lists;
    }

    private List<ProcessInfo> toProcessInfoList(List<DesktopApplicationsLists.AppInfo> appList) {
        List<ProcessInfo> procList = new ArrayList<ProcessInfo>();
        for (DesktopApplicationsLists.AppInfo appinfo : appList) {
            ProcessInfo proc = new ProcessInfo();
            proc.setName(appinfo.getProcessName());
            proc.setDescription(proc.getDescription());
            procList.add(proc);
        }
        return procList;
    }

    /**
     * Gets the current Share Settings
     * @return The Share Settings
     */
    @GET
    @Path("/share_settings")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Share Settings")
   	public ShareSettings getShareSettings() {
        ShareSettings settings = new ShareSettings();
        settings.setLink("/admin/share_settings");
        settings.addAdditionalLink("permissions", settings.getLink() + "/permissions");
        settings.addAdditionalLink("external_restrictions", settings.getLink() + "/external_restrictions");
        settings.setAllowShareWithLdapGroups(getAdminModule().isSharingWithLdapGroupsEnabled());
        settings.setSharingPermissions(getSharingPermissions());
        settings.setExternalRestrictions(_getExternalSharingRestrictions());
        return settings;
    }

    /**
     * Updates the current Share Settings.  Only the fields that are included in the request body will be updated.
     * For example, if the request body is <code>{"allow_sharing_with_ldap_groups":false}</code>, the permissions and
     * external restrictions settings will not change.  Only the "Allow sharing with LDAP groups" setting will be updated.
     * @return The Share Settings
     */
    @PUT
    @Path("/share_settings")
   	@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Share Settings")
   	public ShareSettings updateShareSettings(ShareSettings settings) {
        if (settings.getAllowShareWithLdapGroups()!=null) {
            getAdminModule().setAllowShareWithLdapGroups(settings.getAllowShareWithLdapGroups());
        }
        List<AssignedSharingPermission> sharingPermissions = settings.getSharingPermissions();
        if (sharingPermissions!=null) {
            setSharingPermissions(sharingPermissions);
        }

        ExternalSharingRestrictions restrictions = settings.getExternalRestrictions();
        if (restrictions!=null) {
            updateExternalRestrictions(restrictions);
        }
        return getShareSettings();
    }

    /**
     * Gets the list of sharing permissions that have been configured.
     * @return  A list of AssignedSharingPermission objects
     */
    @GET
    @Path("/share_settings/permissions")
    @ResourceGroup("Share Settings")
    public List<AssignedSharingPermission> getSharingPermissions() {
        List<AssignedSharingPermission> sharing = new ArrayList<AssignedSharingPermission>();
        List<AssignedRole> globalSharingRights = AdminHelper.getGlobalSharingRights(this);
        for (AssignedRole role : globalSharingRights) {
            sharing.add(AdminResourceUtil.buildAssignedSharingPermission(role));
        }
        return sharing;
    }

    /**
     * Assign sharing permissions for a particular user or group.  If the user or group has already been assigned
     * sharing permissions, those permissions will be replaced.  If not, a new assigned sharing permission will be added.
     * @return The full list of assigned sharing permissions
     */
    @POST
    @Path("/share_settings/permissions")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Share Settings")
    public List<AssignedSharingPermission> addSharingPermission(AssignedSharingPermission permission) {
        AssignedRole roleToAdd = toAssignedRole(permission);
        List<AssignedRole> globalSharingRights = AdminHelper.getGlobalSharingRights(this);
        boolean found = false;
        for (AssignedRole existing : globalSharingRights) {
            if (existing.getPrincipal().getId().equals(roleToAdd.getPrincipal().getId())) {
                existing.setRoles(roleToAdd.getRoles());
                found = true;
                break;
            }
        }
        if (!found) {
            globalSharingRights.add(roleToAdd);
        }
        AdminHelper.setGlobalSharingRights(this, globalSharingRights);
        return getSharingPermissions();
    }

    /**
     * Replaces the list of assigned sharing permissions.
     * @return The full list of assigned sharing permissions
     */
    @PUT
    @Path("/share_settings/permissions")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Share Settings")
    public List<AssignedSharingPermission> setSharingPermissions(List<AssignedSharingPermission> sharingPermissions) {
        List<AssignedRole> roles = toAssignedRoles(sharingPermissions);
        if (roles!=null) {
            AdminHelper.setGlobalSharingRights(this, roles);
        }
        return getSharingPermissions();
    }

    /**
     * Deletes the full list of assigned sharing permissions.
     */
    @DELETE
    @Path("/share_settings/permissions")
    @ResourceGroup("Share Settings")
    @StatusCodes({
            @ResponseCode(code=204, condition="The list is deleted successfully"),
    })
    public void removeAllSharingPermissions() {
        AdminHelper.setGlobalSharingRights(this, new ArrayList<AssignedRole>());
    }

    /**
     * Gets the current restrictions for sharing with external users.  This is a
     * whitelist or blacklist of email addresses and domains.
     * @return The external sharing restrictions
     */
    @GET
    @Path("/share_settings/external_restrictions")
    @ResourceGroup("Share Settings")
    public ExternalSharingRestrictions getExternalSharingRestrictions() {
        return _getExternalSharingRestrictions();
    }

    /**
     * Updates the restrictions for sharing with external users.  Only the fields that are included in the request body
     * will be modified.  For example, a request body of <code>{"domain_list":["microfocus.com","novell.com"]}</code>
     * will replace the current list of domains, but will not modify the mode (blacklist/whitelist/none) nor the list of
     * email addresses.
     * @return The full external sharing restrictions
     */
    @PUT
    @Path("/share_settings/external_restrictions")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Share Settings")
    public ExternalSharingRestrictions updateExternalRestrictions(ExternalSharingRestrictions restrictions) {
        ShareLists shareLists = getSharingModule().getShareLists();
        if (restrictions.getMode()!=null) {
            ExternalSharingRestrictions.Mode mode = toEnum(ExternalSharingRestrictions.Mode.class, "mode", restrictions.getMode());
            if (mode == ExternalSharingRestrictions.Mode.none) {
                shareLists.setShareListMode(ShareLists.ShareListMode.DISABLED);
            } else if (mode == ExternalSharingRestrictions.Mode.blacklist) {
                shareLists.setShareListMode(ShareLists.ShareListMode.BLACKLIST);
            } else {
                shareLists.setShareListMode(ShareLists.ShareListMode.WHITELIST);
            }
        }

        if (restrictions.getDomainList()!=null) {
            shareLists.setDomains(restrictions.getDomainList());
        }

        if (restrictions.getEmailList()!=null) {
            shareLists.setEmailAddresses(restrictions.getEmailList());
        }

        getSharingModule().setShareLists(shareLists);
        return getExternalSharingRestrictions();
    }
}
