/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.BinderState;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.ldap.LdapSyncResults;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.Recipient;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.ShareRecipient;
import org.kablink.teaming.rest.v1.model.SharingPermission;
import org.kablink.teaming.rest.v1.model.admin.AssignedRight;
import org.kablink.teaming.rest.v1.model.admin.AssignedSharingPermission;
import org.kablink.teaming.rest.v1.model.admin.KeyValuePair;
import org.kablink.teaming.rest.v1.model.admin.LdapHomeDirConfig;
import org.kablink.teaming.rest.v1.model.admin.LdapSearchInfo;
import org.kablink.teaming.rest.v1.model.admin.LdapUserSource;
import org.kablink.teaming.rest.v1.model.admin.NetFolder;
import org.kablink.teaming.rest.v1.model.admin.NetFolderServer;
import org.kablink.teaming.rest.v1.model.admin.NetFolderSyncStatus;
import org.kablink.teaming.rest.v1.model.admin.Schedule;
import org.kablink.teaming.rest.v1.model.admin.SelectedDays;
import org.kablink.teaming.rest.v1.model.admin.Time;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.AssignedRole;
import org.kablink.teaming.web.util.NetFolderHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: David
 * Date: 11/18/13
 * Time: 12:43 PM
 */
public class AdminResourceUtil {

    public static NetFolderServer buildNetFolderServer(ResourceDriverConfig config, boolean fullDetails, boolean includePassword) {
        NetFolderServer model = new NetFolderServer();
        model.setAccountName(config.getAccountName());
        model.setUseProxyIdentity(config.getUseProxyIdentity());
        model.setProxyIdentityId(config.getProxyIdentityId());
        if (config.getAuthenticationType()!=null) {
            model.setAuthenticationType(config.getAuthenticationType().name());
        }
        if (config.getChangeDetectionMechanism()!=null) {
            model.setChangeDetectionMechanism(config.getChangeDetectionMechanism().name());
        }
        if (config.getDriverType()!=null) {
            model.setDriverType(config.getDriverType().name());
        }
        model.setFullSyncDirOnly(config.getFullSyncDirOnly());
        model.setId(config.getId());
        model.setModifiedOn(config.getModifiedOn());
        model.setName(config.getName());
        if (includePassword) {
            model.setPassword(config.getPassword());
        }
        model.setRootPath(config.getRootPath());
        model.setIndexContent(config.getIndexContent());
        model.setJitsEnabled(config.isJitsEnabled());
        model.setJitsMaxAge(config.getJitsMaxAge());
        model.setJitsMaxACLAge(config.getJitsAclMaxAge());
        model.setAllowClientInitiatedSync(config.getAllowDesktopAppToTriggerInitialHomeFolderSync());
        if (fullDetails) {
            model.setSyncSchedule(buildSchedule(NetFolderHelper.getNetFolderServerSynchronizationSchedule(model.getId())));
        }
        model.setLink(AdminLinkUriUtil.getNetFolderServerLinkUri(config.getId()));
        model.addAdditionalLink("net_folders", model.getLink() + "/net_folders");
        return model;
    }

    public static NetFolder buildNetFolder(NetFolderConfig nfc, AllModulesInjected ami, boolean fullDetails) {
        NetFolder model = new NetFolder();
        model.setId(nfc.getTopFolderId());
        model.setName(nfc.getName());
        model.setRelativePath(nfc.getResourcePath());

        ResourceDriverConfig driverConfig = NetFolderUtil.getNetFolderServerById(nfc.getNetFolderServerId());
        if (driverConfig!=null) {
            model.setServer(new LongIdLinkPair(driverConfig.getId(), AdminLinkUriUtil.getNetFolderServerLinkUri(driverConfig.getId())));
        }
        model.setHomeDir(nfc.isHomeDir());

        model.setIndexContent(nfc.getIndexContent());
        model.setInheritIndexContent(nfc.getUseInheritedIndexContent());

        model.setJitsEnabled(nfc.isJitsEnabled());
        model.setJitsMaxACLAge(nfc.getJitsMaxAge());
        model.setJitsMaxAge(nfc.getJitsAclMaxAge());
        
        model.setInheritSyncSchedule(nfc.getSyncScheduleOption() != NetFolderConfig.SyncScheduleOption.useNetFolderSchedule);
        if (fullDetails) {
            ScheduleInfo scheduleInfo = NetFolderHelper.getMirroredFolderSynchronizationSchedule( nfc.getTopFolderId() );
            model.setSyncSchedule(buildSchedule(scheduleInfo));
            Folder folder = ami.getFolderModule().getFolder(nfc.getTopFolderId());
            model.setAssignedRights(buildAssignedRights(NetFolderHelper.getNetFolderRights(ami, folder)));
        }

        model.setAllowDesktopSync(nfc.getAllowDesktopAppToSyncData());
        model.setInheritClientSyncSettings(nfc.getUseInheritedDesktopAppTriggerSetting());
        model.setAllowClientInitiatedSync(nfc.getAllowDesktopAppToTriggerInitialHomeFolderSync());

        model.setFullSyncDirOnly(nfc.getFullSyncDirOnly());

        model.setLink(AdminLinkUriUtil.getNetFolderLinkUri(nfc.getTopFolderId()));
        model.addAdditionalLink("sync", model.getLink() + "/sync");

        return model;
    }

    public static LdapUserSource buildUserSource(LdapConnectionConfig ldapConfig,
                                                 ResourceDriverModule resourceDriverModule) {
        LdapUserSource model = new LdapUserSource();
        model.setId(ldapConfig.getId());
        model.setUrl(ldapConfig.getUrl());
        model.setPrincipal(ldapConfig.getPrincipal());
        // Don't return the password in the results
        // model.setCredentials(ldapConfig.getCredentials());
        model.setGuidAttribute(ldapConfig.getLdapGuidAttribute());
        model.setUsernameAttribute(ldapConfig.getUserIdAttribute());
        model.setMappings(buildKeyValueList(ldapConfig.getMappings()));
        model.setUserSearches(buildSearchInfoList(ldapConfig.getUserSearches(), resourceDriverModule));
        model.setGroupSearches(buildSearchInfoList(ldapConfig.getGroupSearches(), null));
        model.setLink(AdminLinkUriUtil.getUserSourceLinkUri(model.getId()));
        return model;
    }

    private static List<LdapSearchInfo> buildSearchInfoList(List<LdapConnectionConfig.SearchInfo> searchInfos,
                                                            ResourceDriverModule resourceDriverModule) {
        List<LdapSearchInfo> model = new ArrayList<LdapSearchInfo>();
        for (LdapConnectionConfig.SearchInfo searchInfo : searchInfos) {
            model.add(buildSearchInfo(searchInfo, resourceDriverModule));
        }
        return model;
    }

    private static LdapSearchInfo buildSearchInfo(LdapConnectionConfig.SearchInfo searchInfo,
                                                  ResourceDriverModule resourceDriverModule) {
        LdapSearchInfo model = null;
        if (searchInfo!=null) {
            model = new LdapSearchInfo();
            model.setBaseDn(searchInfo.getBaseDn());
            model.setFilter(searchInfo.getFilter());
            model.setSearchSubtree(searchInfo.isSearchSubtree());
            model.setHomeDirConfig(buildHomeDirConfig(searchInfo.getHomeDirConfig(), resourceDriverModule));
        }
        return model;
    }

    private static LdapHomeDirConfig buildHomeDirConfig(LdapConnectionConfig.HomeDirConfig homeDirConfig,
                                                        ResourceDriverModule resourceDriverModule) {
        LdapHomeDirConfig model = null;
        if (homeDirConfig!=null) {
            model = new LdapHomeDirConfig();
            LdapConnectionConfig.HomeDirCreationOption creationOption = homeDirConfig.getCreationOption();
            if (creationOption == LdapConnectionConfig.HomeDirCreationOption.USE_CUSTOM_CONFIG) {
                String serverName = homeDirConfig.getNetFolderServerName();
                if (serverName != null && resourceDriverModule!=null) {
                    ResourceDriverConfig netFolderServer = NetFolderHelper.findNetFolderRootByName(null, resourceDriverModule, serverName);
                    if (netFolderServer!=null) {
                        model.setOption(LdapHomeDirConfig.TYPE_CUSTOM_NET_FOLDER);
                        model.setNetFolderServer(new LongIdLinkPair(netFolderServer.getId(), AdminLinkUriUtil.getNetFolderServerLinkUri(netFolderServer.getId())));
                        model.setPath(homeDirConfig.getPath());
                    }
                }
            } else if (creationOption == LdapConnectionConfig.HomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE) {
                model.setOption(LdapHomeDirConfig.TYPE_HOME_DIR_ATTRIBUTE);
            } else if (creationOption == LdapConnectionConfig.HomeDirCreationOption.USE_CUSTOM_ATTRIBUTE) {
                model.setOption(LdapHomeDirConfig.TYPE_CUSTOM_ATTRIBUTE);
                model.setLdapAttribute(homeDirConfig.getAttributeName());
            }
            if (model.getOption()==null) {
                model.setOption(LdapHomeDirConfig.TYPE_NONE);
            }
        }
        return model;
    }

    private static List<KeyValuePair> buildKeyValueList(Map<String, String> mappings) {
        List<KeyValuePair> model = new ArrayList<KeyValuePair>();
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            model.add(new KeyValuePair(entry.getKey(), entry.getValue()));
        }
        return model;
    }

    public static Schedule buildSchedule(ScheduleInfo scheduleInfo) {
        Schedule model = null;
        if (scheduleInfo!=null) {
            model = new Schedule();
            model.setEnabled(scheduleInfo.isEnabled());
            org.kablink.teaming.jobs.Schedule sch = scheduleInfo.getSchedule();
            if (sch.isDaily()) {
                model.setDayFrequency(Schedule.DayFrequency.daily.name());
            } else {
                model.setDayFrequency(Schedule.DayFrequency.selected_days.name());
                SelectedDays days = new SelectedDays();
                days.setSun(sch.isOnSunday());
                days.setMon(sch.isOnMonday());
                days.setTue(sch.isOnTuesday());
                days.setWed(sch.isOnWednesday());
                days.setThu(sch.isOnThursday());
                days.setFri(sch.isOnFriday());
                days.setSat(sch.isOnSaturday());
                model.setSelectedDays(days);
            }

            if ( sch.isRepeatMinutes() ) {
                Time every = new Time();
                every.setHour(0);
                every.setMinute(Integer.valueOf(sch.getMinutesRepeat()));
                model.setEvery(every);
            } else if ( sch.isRepeatHours() ) {
                Time every = new Time();
                every.setHour(Integer.valueOf( sch.getHoursRepeat() ));
                every.setMinute(0);
                model.setEvery(every);
            } else {
                Time at = new Time();
                at.setHour(Integer.valueOf( sch.getHours() ));
                at.setMinute(Integer.valueOf(sch.getMinutes()));
                model.setAt(at);
            }
        }
        return model;
    }

    public static List<AssignedRight> buildAssignedRights(List<AssignedRole> roles) {
        List<AssignedRight> model = new ArrayList<AssignedRight>();
        for (AssignedRole role : roles) {
            AssignedRight right = buildAssignedRight(role);
            if (right!=null) {
                model.add(right);
            }
        }
        return model;
    }

    public static AssignedRight buildAssignedRight(AssignedRole role) {
        AssignedRight model = new AssignedRight();
        Principal principal = role.getPrincipal();
        Recipient recipient = buildRecipient(principal);
        if (recipient == null) {
            return null;
        }
        model.setPrincipal(recipient);

        Set<AssignedRole.RoleType> roles = role.getRoles();
        Access access = new Access();
        if (roles.contains(AssignedRole.RoleType.AllowAccess)) {
            access.setRole(Access.RoleType.ACCESS.name());
            SharingPermission permission = new SharingPermission();
            permission.setInternal(roles.contains(AssignedRole.RoleType.ShareInternal));
            permission.setExternal(roles.contains(AssignedRole.RoleType.ShareExternal));
            permission.setPublic(roles.contains(AssignedRole.RoleType.SharePublic));
            permission.setPublicLink(roles.contains(AssignedRole.RoleType.SharePublicLinks));
            permission.setGrantReshare(roles.contains(AssignedRole.RoleType.ShareForward));
            access.setSharing(permission);
        } else {
            access.setRole(Access.RoleType.NONE.name());
        }
        model.setAccess(access);
        return model;
    }

    public static AssignedSharingPermission buildAssignedSharingPermission(AssignedRole role) {
        AssignedSharingPermission model = new AssignedSharingPermission();
        Principal principal = role.getPrincipal();
        Recipient recipient = buildRecipient(principal);
        if (recipient == null) {
            return null;
        }
        model.setPrincipal(recipient);

        Set<AssignedRole.RoleType> roles = role.getRoles();
        SharingPermission permission = new SharingPermission();
        permission.setInternal(roles.contains(AssignedRole.RoleType.EnableShareInternal));
        permission.setExternal(roles.contains(AssignedRole.RoleType.EnableShareExternal));
        permission.setAllInternal(roles.contains(AssignedRole.RoleType.EnableShareWithAllInternal));
        permission.setAllExternal(roles.contains(AssignedRole.RoleType.EnableShareWithAllExternal));
        permission.setPublic(roles.contains(AssignedRole.RoleType.EnableSharePublic));
        permission.setPublicLink(roles.contains(AssignedRole.RoleType.EnableLinkSharing));
        permission.setGrantReshare(roles.contains(AssignedRole.RoleType.EnableShareForward));
        model.setSharing(permission);
        return model;
    }

    private static Recipient buildRecipient(Principal principal) {
        Recipient recipient = new Recipient();
        if (principal instanceof User) {
            recipient.setType(Recipient.RecipientType.user.name());
            recipient.setId(principal.getId());
            recipient.setLink(LinkUriUtil.getUserLinkUri(principal.getId()));
        } else if (principal instanceof Group) {
            recipient.setType(Recipient.RecipientType.group.name());
            recipient.setId(principal.getId());
            recipient.setLink(LinkUriUtil.getGroupLinkUri(principal.getId()));
        } else {
            return null;
        }
        return recipient;
    }

    public static org.kablink.teaming.rest.v1.model.admin.LdapSyncResults buildLdapSyncResults(LdapSyncResults results) {
        org.kablink.teaming.rest.v1.model.admin.LdapSyncResults model = new org.kablink.teaming.rest.v1.model.admin.LdapSyncResults();
        model.setStatus(results.getStatus().name());
        model.setError(results.getErrorDesc());
        model.setAddedGroups(buildList(results.getAddedGroups()));
        model.setModifiedGroups(buildList(results.getModifiedGroups()));
        model.setDeletedGroups(buildList(results.getDeletedGroups()));
        model.setAddedUsers(buildList(results.getAddedUsers()));
        model.setModifiedUsers(buildList(results.getModifiedUsers()));
        model.setDeletedUsers(buildList(results.getDisabledUsers()));
        model.setDisabledUsers(buildList(results.getDisabledUsers()));
        return model;
    }

    private static List<String> buildList(LdapSyncResults.PartialLdapSyncResults results) {
        if (results!=null) {
            return results.getResults();
        }
        return null;
    }

    public static NetFolderSyncStatus buildNetFolderSyncStatus(BinderState.FullSyncStats syncStats) {
        NetFolderSyncStatus model = new NetFolderSyncStatus();
        model.setStatus(syncStats.getStatus().name());
        model.setDirectoryEnumerationFailure(syncStats.getEnumerationFailed());
        model.setDirectoryOnly(syncStats.getDirOnly());
        model.setEndDate(syncStats.getEndDate());
        model.setEntriesExpunged(syncStats.getCountEntryExpunge());
        model.setFailures(syncStats.getCountFailure());
        model.setFilesAdded(syncStats.getCountFileAdd());
        model.setFilesExpunged(syncStats.getCountFileExpunge());
        model.setFilesFound(syncStats.getCountFiles());
        model.setFilesModified(syncStats.getCountFileModify());
        model.setFilesWithModifiedACL(syncStats.getCountFileSetAcl());
        model.setFilesWithModifiedOwner(syncStats.getCountFileSetOwnership());
        model.setFoldersAdded(syncStats.getCountFolderAdd());
        model.setFoldersExpunged(syncStats.getCountFolderExpunge());
        model.setFoldersFound(syncStats.getCountFolders());
        model.setFoldersProcessed(syncStats.getCountFolderProcessed());
        model.setFoldersWithModifiedACL(syncStats.getCountFolderSetAcl());
        model.setFoldersWithModifiedOwner(syncStats.getCountFolderSetOwnership());
        model.setNodeIPAddress(syncStats.getStatusIpv4Address());
        model.setStartDate(syncStats.getStartDate());
        return model;
    }

    public static Share buildShare(ShareItem shareItem, org.kablink.teaming.domain.DefinableEntity sharedEntity, ShareRecipient recipient, boolean guestEnabled) {
        Share share = ResourceUtil.buildShare(shareItem, sharedEntity, recipient, guestEnabled);
        share.setLink(AdminLinkUriUtil.getShareLinkUri(share.getId()));
        return share;
    }
}
