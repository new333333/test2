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
package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.rest.v1.model.AverageRating;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderChange;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.DefinableEntity;
import org.kablink.teaming.rest.v1.model.Description;
import org.kablink.teaming.rest.v1.model.Entry;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.Group;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.Principal;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.Workspace;
import org.kablink.teaming.rest.v1.model.ZoneConfig;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.*;
import org.kablink.teaming.web.util.DateHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.dom4j.Element;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * This class contains utility methods that are shared among multiple resource types.
 * Do not place in this class any methods that are used by a single resource type.
 * 
 * @author jong
 */
@SuppressWarnings({"unchecked", "unused"})
public class ResourceUtil {
    private static DefinitionModule definitionModule;
    private static Set<String> ignoredCustomFields = new HashSet<String>() {
        {
            add("description");
            add("emailAddress");
            add("firstName");
            add("lastName");
            add("middleName");
            add("name");
            add("phone");
            add("skypeId");
            add("ss_attachFile");
            add("timeZone");
            add("title");
        }
    };

    public static Calendar toCalendar(Date date) {
   		Calendar cal = null;
        if (date!=null) {
            cal = Calendar.getInstance();
            cal.setTime(date);
        }
   		return cal;
   	}

    public static BinderChanges mergeBinderChanges(BinderChanges changes1, BinderChanges changes2, Integer maxCount) {
        if (changes1.getCount()==0) {
            return changes2;
        }
        if (changes2.getCount()==0) {
            return changes1;
        }
        BinderChanges model = new BinderChanges();
        model.setTotal(changes1.getTotal() + changes2.getTotal());
        ListIterator<BaseBinderChange> iter1 = changes1.getResults().listIterator();
        ListIterator<BaseBinderChange> iter2 = changes2.getResults().listIterator();
        boolean done = false;
        BaseBinderChange change1 = null;
        BaseBinderChange change2 = null;
        while (!done) {
            if (model.getCount()>=maxCount) {
                done = true;
            } else {
                if (change1==null && iter1.hasNext()) {
                    change1 = iter1.next();
                }
                if (change2==null && iter2.hasNext()) {
                    change2 = iter2.next();
                }
                if (change1==null && change2==null) {
                    done = true;
                } else if (change1==null) {
                    model.append(change2);
                    change2 = null;
                } else if (change2==null) {
                    model.append(change1);
                    change1 = null;
                } else if (change1.getDate().before(change2.getDate())) {
                    model.append(change1);
                    change1 = null;
                } else {
                    model.append(change2);
                    change2 = null;
                }
            }
        }
        model.setLastModified(model.getResults().get(model.getCount() - 1).getDate());
        model.setLastChange(model.getLastModified());
        return model;
    }

    public static BinderChanges buildBinderChanges(org.kablink.teaming.domain.BinderChanges changes, List<BaseBinderChange> changeList) {
        BinderChanges model = new BinderChanges();
        model.setCount(changes.getCount());
        model.setTotal(changes.getTotal());
        model.appendAll(changeList);
        if (changeList.size()>0) {
            model.setLastModified(changeList.get(changeList.size()-1).getDate());
            model.setLastChange(model.getLastModified());
        }
        return model;
    }

    public static BaseBinderChange buildBinderChange(org.kablink.teaming.domain.BinderChange change, org.kablink.teaming.domain.DefinableEntity entity, boolean preferFile, int descriptionFormat) {
        return buildBinderChange(change.getEntityId(), change.getAction(), change.getDate(), change.getPrimaryFileId(), entity, preferFile, descriptionFormat);
    }

    public static BaseBinderChange buildBinderChange(EntityIdentifier entityId, org.kablink.teaming.domain.BinderChange.Action action, Date changeDate,
                                                     String fileId, org.kablink.teaming.domain.DefinableEntity entity, boolean preferFile, int descriptionFormat) {
        BaseBinderChange model = null;
        EntityIdentifier.EntityType entityType = entityId.getEntityType();
        if (entityType == EntityIdentifier.EntityType.folderEntry) {
            if (preferFile) {
                FileProperties props = null;
                if (fileId==null && entity!=null) {
                    SortedSet<FileAttachment> fileAttachments = entity.getFileAttachments();
                    if (fileAttachments.size()>0) {
                        FileAttachment attachment = fileAttachments.first();
                        fileId = attachment.getId();
                        if (action!= org.kablink.teaming.domain.BinderChange.Action.delete) {
                            props = buildFileProperties(attachment);
                        }
                    }
                }
                if (fileId!=null) {
                    FileChange model1 = new FileChange();
                    model1.setAction(action.name());
                    model1.setDate(changeDate);
                    model1.setId(fileId);
                    model1.setFile(props);
                    model = model1;
                }
            }
            if (model==null) {
                FolderEntryChange model1 = new FolderEntryChange();
                model1.setId(entityId.getEntityId());
                model1.setAction(action.name());
                model1.setDate(changeDate);
                if (entity!=null && action!=org.kablink.teaming.domain.BinderChange.Action.delete) {
                    model1.setEntry(buildFolderEntry((org.kablink.teaming.domain.FolderEntry) entity, false, descriptionFormat));
                }
                model = model1;
            }
        } else if (entityType.isBinder()) {
            BinderChange model1 = new BinderChange();
            model1.setId(entityId.getEntityId());
            model1.setAction(action.name());
            model1.setDate(changeDate);
            if (entity!=null && action != org.kablink.teaming.domain.BinderChange.Action.delete) {
                model1.setBinder(buildBinder((org.kablink.teaming.domain.Binder) entity, false, descriptionFormat));
            }
            model = model1;
        }
        return model;
    }

    public static FolderEntryBrief buildFolderEntryBrief(org.kablink.teaming.domain.FolderEntry entry) {
        FolderEntryBrief model = new FolderEntryBrief();
        populateBaseFolderEntryBrief(model, entry);
        LinkUriUtil.populateFolderEntryLinks(model, model.getId());
        return model;
    }

    public static FolderEntry buildFolderEntry(org.kablink.teaming.domain.FolderEntry entry, boolean includeAttachments, int descriptionFormat) {
        FolderEntry model = new FolderEntry();
        populateBaseFolderEntry(model, entry, includeAttachments, descriptionFormat);
        model.setEntryType(Constants.ENTRY_TYPE_ENTRY);
        model.setReservation(buildHistoryStamp(entry.getReservation()));
        LinkUriUtil.populateFolderEntryLinks(model, model.getId());
        return model;
    }

    public static Reply buildReply(org.kablink.teaming.domain.FolderEntry entry, boolean includeAttachments, int descriptionFormat) {
        Reply model = new Reply();
        populateBaseFolderEntry(model, entry, includeAttachments, descriptionFormat);
        model.setEntryType(Constants.ENTRY_TYPE_REPLY);
        org.kablink.teaming.domain.FolderEntry top = entry.getTopEntry();
        if (top!=null) {
            model.setTopEntry(new LongIdLinkPair(top.getId(), LinkUriUtil.getFolderEntryLinkUri(top.getId())));
            org.kablink.teaming.domain.FolderEntry parent = entry.getParentEntry();
            if (parent!=null) {
                if (parent.getId().equals(top.getId())) {
                    model.setParentEntry(model.getTopEntry());
                } else {
                    model.setParentEntry(new LongIdLinkPair(parent.getId(), LinkUriUtil.getReplyLinkUri(parent.getId())));
                }
            }
        }
        LinkUriUtil.populateReplyLinks(model, model.getId());
        return model;
    }

    public static FileProperties buildFileProperties(FileIndexData fa) {
        FileProperties fp = new FileProperties();
        fp.setId(fa.getId());
        fp.setOwningEntity(buildEntityId(fa.getOwningEntityType(), fa.getOwningEntityId()));
        fp.setPermaLink(PermaLinkUtil.getPermalink(fa.getOwningEntityId(), fa.getOwningEntityType(), null));
        fp.setBinder(new ParentBinder(fa.getBinderId(), LinkUriUtil.getBinderLinkUri(fa.getBinderId())));
        fp.setName(fa.getName());
        fp.setCreation(new HistoryStamp(new LongIdLinkPair(fa.getCreatorId(), LinkUriUtil.getUserLinkUri(fa.getCreatorId())),
                fa.getCreatedDate()));
        fp.setModification(new HistoryStamp(new LongIdLinkPair(fa.getModifierId(), LinkUriUtil.getUserLinkUri(fa.getModifierId())),
                fa.getModifiedDate()));
        fp.setLength(fa.getSize());
        fp.setMd5(fa.getMd5());
        fp.setVersionNumber(fa.getVersionNumber());
        if (fp.getVersionNumber()==-1) {
            fp.setVersionNumber(1);
        }
        fp.setMajorVersion(fa.getMajorVersionNumber());
        fp.setMinorVersion(fa.getMinorVersionNumber());
        LinkUriUtil.populateFileLinks(fp, fa.getOwningEntityId(), fa.getOwningEntityType());
        return fp;
    }

    public static FileProperties buildFileProperties(FileAttachment fa) {
        FileProperties fp = null;
        if (fa!=null) {
            fp = new FileProperties();
            populateFileProperties(fp, fa);
        }
        return fp;
    }

    public static void populateFileProperties(FileProperties fp, FileAttachment fa) {
        FileAttachment.FileLock fl = fa.getFileLock();
        Long creatorId = fa.getCreation().getPrincipal().getId();
        Long modifierId = fa.getModification().getPrincipal().getId();
        fp.setId(fa.getId());
        fp.setName(fa.getFileItem().getName());
        fp.setCreation(new HistoryStamp(new LongIdLinkPair(creatorId, LinkUriUtil.getUserLinkUri(creatorId)),
                fa.getCreation().getDate()));
        fp.setModification(new HistoryStamp(new LongIdLinkPair(modifierId, LinkUriUtil.getUserLinkUri(modifierId)),
                fa.getModification().getDate()));
        fp.setLength(fa.getFileItem().getLength());
        fp.setMd5(fa.getFileItem().getMd5());
        fp.setVersionNumber(fa.getLastVersion());
        fp.setMajorVersion(fa.getMajorVersion());
        fp.setMinorVersion(fa.getMinorVersion());
        fp.setNote(fa.getFileItem().getDescription().getText());
        fp.setStatus(fa.getFileStatus());
        if (fl!=null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(fl.getExpirationDate());
            fp.setLockExpiration(cal);
            if (fl.getOwner()!=null) {
                fp.setLockedBy(fl.getOwner().getId());
            }
        }
        org.kablink.teaming.domain.DefinableEntity entity = fa.getOwner().getEntity();
        fp.setOwningEntity(buildEntityId(entity.getEntityType(), entity.getId()));
        fp.setPermaLink(PermaLinkUtil.getPermalink(entity.getId(), entity.getEntityType(), null));
        Long binderId = entity.getParentBinder().getId();
        fp.setBinder(new ParentBinder(binderId, LinkUriUtil.getBinderLinkUri(binderId)));
        LinkUriUtil.populateFileLinks(fp, entity.getId(), entity.getEntityType());
    }

    public static TeamBrief buildTeamBrief(org.kablink.teaming.domain.TeamInfo binder) {
        TeamBrief model = new TeamBrief();
        model.setId(binder.getId());
        model.setTitle(binder.getTitle());
        model.setEntityType(binder.getEntityType());
        model.setFamily(binder.getFamily());
        model.setLibrary(binder.getLibrary());
        model.setMirrored(binder.getMirrored());
        model.setPath(binder.getPath());
        model.setCreation(buildHistoryStamp(binder.getCreation()));
        model.setModification(buildHistoryStamp(binder.getModification()));
        model.setLink(LinkUriUtil.getBinderLinkUri(model));
        if (model.isFolder()) {
            LinkUriUtil.populateFolderLinks(model);
        } else if (model.isWorkspace()) {
            LinkUriUtil.populateWorkspaceLinks(model);
        }
        return model;
    }

    public static Binder buildBinder(org.kablink.teaming.domain.Binder binder, boolean includeAttachments, int descriptionFormat) {
        Binder model;
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            model = new Folder();
            populateFolder((Folder) model, (org.kablink.teaming.domain.Folder) binder, includeAttachments, descriptionFormat);
        } else if (binder instanceof org.kablink.teaming.domain.Workspace) {
            model = new Workspace();
            populateWorkspace((Workspace)model, (org.kablink.teaming.domain.Workspace)binder, includeAttachments, descriptionFormat);
        } else {
            model = new Binder();
            populateBinder(model, binder, includeAttachments, descriptionFormat);
        }
        return model;
    }

    public static BinderBrief buildBinderBrief(org.kablink.teaming.domain.Binder binder) {
        BinderBrief model = new BinderBrief();
        populateBinderBrief(model, binder);
        return model;
    }

    public static GroupMember buildGroupMember(long groupId, org.kablink.teaming.domain.Principal principal) {
        GroupMember model = new GroupMember();
        model.setPrincipal(buildPrincipalBrief(principal));
        model.setLink(LinkUriUtil.getGroupMemberLinkUri(groupId, model.getPrincipal().getId()));
        return model;
    }

    public static TeamMember buildTeamMember(long teamId, org.kablink.teaming.domain.Principal principal) {
        TeamMember model = new TeamMember();
        model.setPrincipal(buildPrincipalBrief(principal));
        model.setLink(LinkUriUtil.getTeamMemberLinkUri(teamId, model.getPrincipal().getId()));
        return model;
    }

    public static PrincipalBrief buildPrincipalBrief(org.kablink.teaming.domain.Principal principal) {
        if (principal instanceof org.kablink.teaming.domain.User) {
            return buildUserBrief((org.kablink.teaming.domain.User) principal);
        } else if (principal instanceof org.kablink.teaming.domain.Group) {
            return buildGroupBrief((org.kablink.teaming.domain.Group) principal);
        } else if (principal instanceof UserPrincipal) {
            EntityIdentifier.EntityType entityType = principal.getEntityType();
            if (entityType==EntityIdentifier.EntityType.user) {
                return buildUserBrief((UserPrincipal) principal);
            } else if (entityType==EntityIdentifier.EntityType.group) {
                return buildGroupBrief((UserPrincipal) principal);
            }
        }
        return null;
    }

    public static UserBrief buildUserBrief(org.kablink.teaming.domain.UserPrincipal user) {
        UserBrief model = new UserBrief();
        populatePrincipalBrief(model, user);
        if (user instanceof org.kablink.teaming.domain.User) {
            org.kablink.teaming.domain.User u = (org.kablink.teaming.domain.User) user;
            model.setPerson(u.isPerson());
            model.setFirstName(u.getFirstName());
            model.setMiddleName(u.getMiddleName());
            model.setLastName(u.getLastName());
            model.setAvatar(buildAvatar(u.getAvatarAttachmentId()));
        }
        model.setLink(LinkUriUtil.getUserLinkUri(model.getId()));
        LinkUriUtil.populateUserLinks(model.getId(), model);
        return model;
    }

    public static UserBrief buildUserBrief(LimitedUserView user) {
        UserBrief model = new UserBrief();
        model.setId(user.getId());
        model.setEntityType(Constants.ENTRY_TYPE_USER);
        model.setTitle(user.getTitle());
        model.setAvatar(buildAvatar(user.getAvatarId()));
        model.setLink(LinkUriUtil.getUserLinkUri(model.getId()));
        return model;
    }

    public static User buildLimitedUser(LimitedUserView user) {
        User model = new User();
        model.setId(user.getId());
        model.setEntityType(Constants.ENTRY_TYPE_USER);
        model.setTitle(user.getTitle());
        model.setAvatar(buildAvatar(user.getAvatarId()));
        model.setLink(LinkUriUtil.getUserLinkUri(model.getId()));
        return model;
    }

    public static GroupBrief buildGroupBrief(org.kablink.teaming.domain.UserPrincipal group) {
        GroupBrief model = new GroupBrief();
        populatePrincipalBrief(model, group);
        model.setLink(LinkUriUtil.getGroupLinkUri(model.getId()));
        LinkUriUtil.populateGroupLinks(model);
        return model;
    }

    public static User buildUser(org.kablink.teaming.domain.User user, boolean includeAttachments, int descriptionFormat) {
        User model = new User();
        populatePrincipal(model, user, includeAttachments, descriptionFormat);
        model.setPerson(user.isPerson());
        model.setFirstName(user.getFirstName());
        model.setMiddleName(user.getMiddleName());
        model.setLastName(user.getLastName());
        model.setOrganization(user.getOrganization());
        model.setPhone(user.getPhone());

        java.util.Locale locale = user.getLocale();
        if(locale != null) {
            Locale localeModel = new Locale();
            localeModel.setLanguage(locale.getLanguage());
            localeModel.setCountry(locale.getCountry());
            model.setLocale(localeModel);
        }
        if(user.getTimeZone() != null) {
            model.setTimeZone(user.getTimeZone().getID());
        }
        model.setSkypeId(user.getSkypeId());
        model.setTwitterId(user.getTwitterId());

        model.setAvatar(buildAvatar(user.getAvatarAttachmentId()));

        if (user.getMiniBlogId()!=null) {
            model.setMiniBlog(new LongIdLinkPair(user.getMiniBlogId(), LinkUriUtil.getFolderLinkUri(user.getMiniBlogId())));
        }
        model.setDiskQuota(user.getDiskQuota());
        model.setFileSizeLimit(user.getFileSizeLimit());
        model.setDiskSpaceUsed(user.getDiskSpaceUsed());
        if (user.getWorkspaceId()!=null) {
            model.setWorkspace(new LongIdLinkPair(user.getWorkspaceId(), LinkUriUtil.getWorkspaceLinkUri(user.getWorkspaceId())));
        }

        LinkUriUtil.populateUserLinks(model.getId(), model);

        return model;
    }

    public static Group buildGroup(org.kablink.teaming.domain.Group group, boolean includeAttachments, int descriptionFormat) {
        Group model = new Group();
        populatePrincipal(model, group, includeAttachments, descriptionFormat);

        LinkUriUtil.populateGroupLinks(model);

        return model;
    }

    public static ZoneConfig buildZoneConfig(org.kablink.teaming.domain.ZoneConfig config, ZoneInfo zoneInfo,
                                             PrincipalMobileAppsConfig userMobileAppsConfig, PrincipalDesktopAppsConfig userDesktopAppsConfig,
                                             org.kablink.teaming.domain.User loggedInUser,
                                             boolean includeProcessConfig,
                                             DesktopApplicationsLists.AppPlatform processPlatform,
                                             AllModulesInjected ami) {
        ZoneConfig modelConfig = new ZoneConfig();
        modelConfig.setId(config.getZoneId());
        if (zoneInfo!=null) {
            modelConfig.setGuid(zoneInfo.getId());
        }
        BinderQuotasConfig binderQuotasConfig = new BinderQuotasConfig();
        binderQuotasConfig.setAllowOwner(config.isBinderQuotaAllowBinderOwnerEnabled());
        binderQuotasConfig.setEnabled(config.isBinderQuotaEnabled());
        binderQuotasConfig.setInitialized(config.isBinderQuotaInitialized());
        modelConfig.setBinderQuotasConfig(binderQuotasConfig);

        DiskQuotasConfig diskQuotasConfig = new DiskQuotasConfig();
        diskQuotasConfig.setEnabled(config.isDiskQuotaEnabled());
        diskQuotasConfig.setHighwaterPercentage(config.getDiskQuotasHighwaterPercentage());
        diskQuotasConfig.setUserDefault(config.getDiskQuotaUserDefault());
        modelConfig.setDiskQuotasConfig(diskQuotasConfig);

        Long fileSizeLimit = loggedInUser.getFileSizeLimit();
        if (fileSizeLimit==null) {
            fileSizeLimit = loggedInUser.getMaxGroupsFileSizeLimit();
            if (fileSizeLimit==null) {
                fileSizeLimit = config.getFileSizeLimitUserDefault();
            }
        }
        if (fileSizeLimit!=null) {
            fileSizeLimit = fileSizeLimit*1024*1024;
        }
        modelConfig.setFileUploadSizeLimit(fileSizeLimit);
        modelConfig.setFileVersionsMaxAge(config.getFileVersionsMaxAge());
        modelConfig.setAllowShareWithLdapGroups(ami.getAdminModule().isSharingWithLdapGroupsEnabled());

        DesktopAppConfig desktopAppConfig = buildDesktopAppConfig(config);
        overrideDesktopAppConfig(desktopAppConfig, userDesktopAppsConfig);
        if (includeProcessConfig) {
            DesktopApplicationsLists appLists = config.getDesktopApplicationsLists();
            DesktopAppProcessConfig procConfig = new DesktopAppProcessConfig();
            procConfig.setAllowUnlistedProcesses(appLists.isBlacklist() || appLists.isDisabled());
            procConfig.setAllowUnlistedProcessOverride(appLists.isBoth());

            if (appLists.isWhitelist() || appLists.isBoth()) {
                List<DesktopApplicationsLists.AppInfo> appList = appLists.getWhitelist(processPlatform);
                List<String> procList = new ArrayList<String>();
                if (appList != null) {
                    for (DesktopApplicationsLists.AppInfo appInfo : appList) {
                        procList.add(appInfo.getProcessName());
                    }
                }
                procConfig.setAllowedProcesses(procList);
            }
            if (appLists.isBlacklist() || appLists.isBoth()) {
                List<DesktopApplicationsLists.AppInfo> appList = appLists.getBlacklist(processPlatform);
                List<String> procList = new ArrayList<String>();
                if (appList!=null) {
                    for (DesktopApplicationsLists.AppInfo appInfo : appList) {
                        procList.add(appInfo.getProcessName());
                    }
                }
                procConfig.setBlockedProcesses(procList);
            }

            desktopAppConfig.setProcessConfig(procConfig);
        }
        modelConfig.setDesktopAppConfig(desktopAppConfig);

        MobileAppConfig mobileAppConfig = buildMobileAppConfig(config.getMobileAppsConfig());
        overrideMobileAppConfig(mobileAppConfig, userMobileAppsConfig);
        modelConfig.setMobileAppConfig(mobileAppConfig);

        return modelConfig;
    }

    private static DesktopAppConfig buildDesktopAppConfig(org.kablink.teaming.domain.ZoneConfig config) {
        DesktopAppConfig desktopAppConfig = new DesktopAppConfig();
        if (config.getFsaDeployLocalApps()) {
            desktopAppConfig.setAutoUpdateUrl(WebUrlUtil.getLocalDesktopDeploymentURL());
        } else {
            desktopAppConfig.setAutoUpdateUrl(config.getFsaAutoUpdateUrl());
        }
        desktopAppConfig.setEnabled(config.getFsaEnabled());
        desktopAppConfig.setSyncInterval(config.getFsaSynchInterval());
        if (config.getFsaMaxFileSize()>0) {
            desktopAppConfig.setMaxFileSize(((long)config.getFsaMaxFileSize()) * 1024 * 1024);
        } else {
            desktopAppConfig.setMaxFileSize(50L * 1024 * 1024);
        }
        desktopAppConfig.setAllowCachedPassword(config.getFsaAllowCachePwd());
        return desktopAppConfig;
    }

    private static void overrideDesktopAppConfig(DesktopAppConfig desktopAppConfig, PrincipalDesktopAppsConfig config) {
        if (config==null || config.getUseDefaultSettings()) {
            return;
        }
        desktopAppConfig.setEnabled(config.getIsFileSyncAppEnabled());
        desktopAppConfig.setAllowCachedPassword(config.getAllowCachePwd());
    }

    private static MobileAppConfig buildMobileAppConfig(MobileAppsConfig mac) {
        MobileAppConfig mobileAppConfig = new MobileAppConfig();
        mobileAppConfig.setAllowCachedContent(mac.getMobileAppsAllowCacheContent());
        mobileAppConfig.setAllowCachedPassword(mac.getMobileAppsAllowCachePwd());
        mobileAppConfig.setAllowPlayWithOtherApps(mac.getMobileAppsAllowPlayWithOtherApps());
        mobileAppConfig.setForcePinCode(mac.getMobileAppsForcePinCode());
        mobileAppConfig.setEnabled(mac.getMobileAppsEnabled());
        mobileAppConfig.setSyncInterval(mac.getMobileAppsSyncInterval());
        mobileAppConfig.setAllowCutCopy(mac.getMobileCutCopyEnabled());
        mobileAppConfig.setAllowRootedDevices(Boolean.FALSE.equals(mac.getMobileDisableOnRootedOrJailBrokenDevices()));
        mobileAppConfig.setAllowScreenCapture(mac.getMobileAndroidScreenCaptureEnabled());
        MobileAppConfig.OpenInApps openIn = toModelEnum(mac.getMobileOpenInEnum());
        mobileAppConfig.setAllowedOpenInApps(openIn.name());
        if (openIn == MobileAppConfig.OpenInApps.selected) {
            MobileOpenInWhiteLists whiteLists = mac.getMobileOpenInWhiteLists();
            if (whiteLists!=null && whiteLists.getAndroidApplications()!=null) {
                mobileAppConfig.setAndroidAppWhiteList(new ArrayList<String>(whiteLists.getAndroidApplications()));
            } else {
                mobileAppConfig.setAndroidAppWhiteList(new ArrayList<String>(0));
            }
            if (whiteLists!=null && whiteLists.getIosApplications()!=null) {
                mobileAppConfig.setiOSAppWhiteList(new ArrayList<String>(whiteLists.getIosApplications()));
            } else {
                mobileAppConfig.setiOSAppWhiteList(new ArrayList<String>(0));
            }
        }
        return mobileAppConfig;
    }

    private static void overrideMobileAppConfig(MobileAppConfig mobileAppConfig, PrincipalMobileAppsConfig mac) {
        if (mac==null || mac.getUseDefaultSettings()) {
            return;
        }
        mobileAppConfig.setAllowCachedContent(mac.getAllowCacheContent());
        mobileAppConfig.setAllowCachedPassword(mac.getAllowCachePwd());
        mobileAppConfig.setAllowPlayWithOtherApps(mac.getAllowPlayWithOtherApps());
        mobileAppConfig.setForcePinCode(mac.getForcePinCode());
        mobileAppConfig.setEnabled(mac.getMobileAppsEnabled());
        mobileAppConfig.setAllowCutCopy(mac.getMobileCutCopyEnabled());
        mobileAppConfig.setAllowRootedDevices(Boolean.FALSE.equals(mac.getMobileDisableOnRootedOrJailBrokenDevices()));
        mobileAppConfig.setAllowScreenCapture(mac.getMobileAndroidScreenCaptureEnabled());
        MobileAppConfig.OpenInApps openIn = toModelEnum(mac.getMobileOpenIn());
        mobileAppConfig.setAllowedOpenInApps(openIn.name());
        if (openIn == MobileAppConfig.OpenInApps.selected) {
            if (mac.getAndroidApplications()!=null) {
                mobileAppConfig.setAndroidAppWhiteList(new ArrayList<String>(mac.getAndroidApplications()));
            } else {
                mobileAppConfig.setAndroidAppWhiteList(new ArrayList<String>(0));
            }
            if (mac.getIosApplications()!=null) {
                mobileAppConfig.setiOSAppWhiteList(new ArrayList<String>(mac.getIosApplications()));
            } else {
                mobileAppConfig.setiOSAppWhiteList(new ArrayList<String>(0));
            }
        } else {
            mobileAppConfig.setiOSAppWhiteList(null);
            mobileAppConfig.setAndroidAppWhiteList(null);
        }
    }

    private static MobileAppConfig.OpenInApps toModelEnum(MobileAppsConfig.MobileOpenInSetting openIn) {
        if (openIn==null || openIn==MobileAppsConfig.MobileOpenInSetting.ALL_APPLICATIONS) {
            return MobileAppConfig.OpenInApps.all;
        }
        if (openIn==MobileAppsConfig.MobileOpenInSetting.WHITE_LIST) {
            return MobileAppConfig.OpenInApps.selected;
        }
        return MobileAppConfig.OpenInApps.none;
    }

	public static FileVersionProperties fileVersionFromFileAttachment(VersionAttachment va) {
        Long creatorId = va.getCreation().getPrincipal().getId();
        Long modifierId = va.getModification().getPrincipal().getId();
        FileVersionProperties props = new FileVersionProperties();
        props.setId(va.getId());
        props.setCreation(new HistoryStamp(new LongIdLinkPair(creatorId, LinkUriUtil.getUserLinkUri(creatorId)), va.getCreation().getDate()));
        props.setModification(new HistoryStamp(new LongIdLinkPair(modifierId, LinkUriUtil.getUserLinkUri(modifierId)), va.getModification().getDate()));
        props.setLength(va.getFileItem().getLength());
        props.setMd5(va.getFileItem().getMd5());
        props.setVersionNumber(va.getVersionNumber());
        props.setMajorVersion(va.getMajorVersion());
        props.setMinorVersion(va.getMinorVersion());
        props.setNote(va.getFileItem().getDescription().getText());
        props.setStatus(va.getFileStatus());
        LinkUriUtil.populateFileVersionLinks(props);
        return props;
	}

    public static Tag buildTag(org.kablink.teaming.domain.Tag tag) {
        Tag model = new Tag();
        model.setId(tag.getId());
        model.setName(tag.getName());
        model.setPublic(tag.isPublic());
        EntityIdentifier entityIdentifier = tag.getEntityIdentifier();
        model.setEntity(buildEntityId(entityIdentifier.getEntityType(), entityIdentifier.getEntityId()));
        model.setLink(LinkUriUtil.getTagLinkUri(model));
        return model;
    }

    public static EntityId buildEntityId(EntityIdentifier entityId) {
        return new EntityId(entityId.getEntityId(), entityId.getEntityType().name(), LinkUriUtil.getDefinableEntityLinkUri(entityId));
    }

    public static EntityId buildEntityId(EntityIdentifier.EntityType type, Long id) {
        return new EntityId(id, type.name(), LinkUriUtil.getDefinableEntityLinkUri(type, id));
    }

    private static void populateBaseFolderEntryBrief(BaseFolderEntryBrief model, org.kablink.teaming.domain.FolderEntry entry) {
        populateEntryBrief(model, entry);
        model.setDocLevel(entry.getDocLevel());
        model.setDocNumber(entry.getDocNumber());
        List<String> filenames = new ArrayList<String>();
        for (FileAttachment attach : entry.getFileAttachments()) {
            filenames.add(attach.getName());
        }
        model.setFileNames(filenames.toArray(new String[filenames.size()]));
    }

    private static void populateBaseFolderEntry(BaseFolderEntry model, org.kablink.teaming.domain.FolderEntry entry, boolean includeAttachments, int descriptionFormat) {
        populateEntry(model, entry, includeAttachments, descriptionFormat);
        if (entry.isTop()) {
            model.setEntryType(Constants.ENTRY_TYPE_ENTRY);
        } else {
            model.setEntryType(Constants.ENTRY_TYPE_REPLY);
        }
        model.setDocLevel(entry.getDocLevel());
        model.setDocNumber(entry.getDocNumber());
        model.setReplyCount(entry.getReplyCount());
        model.setTotalReplyCount(entry.getTotalReplyCount());
    }

    private static void populateDefinableEntity(DefinableEntity model, org.kablink.teaming.domain.DefinableEntity entity, boolean includeAttachments, int descriptionFormat) {
        model.setId(entity.getId());

        if (entity.getParentBinder() != null) {
            Long binderId = entity.getParentBinder().getId();
            model.setParentBinder(new ParentBinder(binderId, LinkUriUtil.getBinderLinkUri(binderId)));
        }

        if(entity.getEntryDefId() != null)
            model.setDefinition(new StringIdLinkPair(entity.getEntryDefId(), LinkUriUtil.getDefinitionLinkUri(entity.getEntryDefId())));

        model.setTitle(entity.getTitle());

        org.kablink.teaming.domain.Description desc = entity.getDescription();
        if(desc != null) {
            model.setDescription(buildDescription(desc, descriptionFormat));
        }

        if(entity.getCreation() != null) {
            model.setCreation(buildHistoryStamp(entity.getCreation()));
        }
        if(entity.getModification() != null) {
            model.setModification(buildHistoryStamp(entity.getModification()));
        }

        if(entity.getAverageRating() != null) {
            model.setAverageRating(buildAverageRating(entity.getAverageRating()));
        }
        if(entity.getEntityType() != null) {
            model.setEntityType(entity.getEntityType().name());
        }
        org.dom4j.Document def = entity.getEntryDefDoc();
        if(def != null) {
            model.setFamily(DefinitionUtils.getFamily(def));
        }
        model.setIcon(LinkUriUtil.getIconLinkUri(entity.getIconName(), model.getEntityType()));
        model.setPermaLink(PermaLinkUtil.getPermalink(entity));
        if (includeAttachments) {
            Set<Attachment> attachments = entity.getAttachments();
            List<BaseFileProperties> props = new ArrayList<BaseFileProperties>(attachments.size());
			int i = 0;
            for (Attachment attachment : attachments) {
                if (attachment instanceof FileAttachment) {
                    props.add(ResourceUtil.buildFileProperties((FileAttachment) attachment));
                } else if (attachment instanceof VersionAttachment) {
                    props.add(ResourceUtil.fileVersionFromFileAttachment((VersionAttachment) attachment));
                }
            }
            model.setAttachments(props.toArray(new BaseFileProperties[props.size()]));
        }

        populateCustomFields(model, entity, descriptionFormat);
    }

    private static void populateCustomFields(final DefinableEntity model, final org.kablink.teaming.domain.DefinableEntity entity, final int descriptionFormat) {
        final Map<String, CustomField> fields = new LinkedHashMap<String, CustomField>();
        DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
            @Override
			public void visit(Element entryElement, Element flagElement, Map args) {
                if (flagElement.attributeValue("apply").equals("true")) {
                    String fieldBuilder = flagElement.attributeValue("elementBuilder");
                    String typeValue = entryElement.attributeValue("name");
                    String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");
                    if (Validator.isNull(nameValue)) {nameValue = typeValue;}
                    if (!ignoredCustomFields.contains(nameValue)) {
                        CustomField field = new CustomField(nameValue, typeValue);
                        CustomAttribute attribute = entity.getCustomAttribute(nameValue);
                        if (attribute!=null) {
                            Object value = attribute.getRawValue(descriptionFormat);
                            if (value instanceof Collection) {
                                field.setValues(((Collection) value).toArray());
                            } else {
                                field.setValue(value);
                            }
                        } else {
                            try {
                                Object value = InvokeUtil.invokeGetter(entity, nameValue);
                                if (value!=null) {
                                    field.setValue(value);
                                }
                            } catch (ObjectPropertyNotFoundException ex) {
                                // Ignore
                            }
                        }
                        fields.put(field.getName(), field);
                    }
                }
            }
            @Override
			public String getFlagElementName() { return "webService"; }
        };

        getDefinitionModule().walkDefinition(entity, visitor, null);

        List<CustomField> fieldList = new ArrayList<CustomField>(fields.values());
        Collections.sort(fieldList, new Comparator<CustomField>() {
            @Override
			public int compare(CustomField o1, CustomField o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        model.setCustomFields(fieldList.toArray(new CustomField[fieldList.size()]));
    }

    private static void populateEntry(Entry model, org.kablink.teaming.domain.Entry entry, boolean includeAttachments, int descriptionFormat) {
        populateDefinableEntity(model, entry, includeAttachments, descriptionFormat);
    }

    private static void populatePrincipalBrief(PrincipalBrief model, org.kablink.teaming.domain.Principal principal) {
        populateEntryBrief(model, principal);
        model.setReserved(principal.isReserved());
        model.setName(principal.getName());
        model.setEmailAddress(principal.getEmailAddress());
    }

    private static void populatePrincipal(Principal model, org.kablink.teaming.domain.Principal principal, boolean includeAttachments, int descriptionFormat) {
        populateEntry(model, principal, includeAttachments, descriptionFormat);
        model.setEmailAddress(principal.getEmailAddress());
        model.setDisabled(principal.isDisabled());
        model.setReserved(principal.isReserved());
        model.setName(principal.getName());
        model.setLink(LinkUriUtil.getPrincipalLinkUri(principal));
    }

    private static void populateBinder(Binder model, org.kablink.teaming.domain.Binder binder, boolean includeAttachments, int descriptionFormat) {
        populateDefinableEntity(model, binder, includeAttachments, descriptionFormat);
        model.setPath(binder.getPathName());
        org.dom4j.Document def = binder.getEntryDefDoc();
        if(def != null) {
            model.setFamily(DefinitionUtils.getFamily(def));
        }
        model.setLink(LinkUriUtil.getBinderLinkUri(binder));
        if (model instanceof Folder) {
            LinkUriUtil.populateFolderLinks(model);
        } else if (model instanceof Workspace) {
            LinkUriUtil.populateWorkspaceLinks(model);
        }
    }

    private static void populateWorkspace(Workspace model, org.kablink.teaming.domain.Workspace workspace, boolean includeAttachments, int descriptionFormat) {
        populateBinder(model, workspace, includeAttachments, descriptionFormat);
    }

    private static void populateFolder(Folder model, org.kablink.teaming.domain.Folder folder, boolean includeAttachments, int descriptionFormat) {
        model.setLibrary(folder.isLibrary());
        model.setMirrored(folder.isMirrored());
        populateBinder(model, folder, includeAttachments, descriptionFormat);
    }

    private static void populateBinderBrief(BinderBrief model, org.kablink.teaming.domain.Binder binder) {
        populateDefinableEntityBrief(model, binder);
        model.setIcon(LinkUriUtil.getIconLinkUri(binder.getIconName(), model.getEntityType()));
        model.setLibrary(binder.isLibrary());
        model.setMirrored(binder.isMirrored());
        model.setPath(binder.getPathName());
        model.setLink(LinkUriUtil.getBinderLinkUri(model));
        if (model.isFolder()) {
            LinkUriUtil.populateFolderLinks(model);
        } else if (model.isWorkspace()) {
            LinkUriUtil.populateWorkspaceLinks(model);
        }
    }

    private static void populateEntryBrief(EntryBrief model, org.kablink.teaming.domain.Entry entry) {
        populateDefinableEntityBrief(model, entry);
    }

    private static void populateDefinableEntityBrief(DefinableEntityBrief model, org.kablink.teaming.domain.DefinableEntity binder) {
        model.setId(binder.getId());
        model.setTitle(binder.getTitle());
        model.setEntityType(binder.getEntityType().toString());
        org.dom4j.Document def = binder.getEntryDefDoc();
        if(def != null) {
            model.setFamily(DefinitionUtils.getFamily(def));
        }
        model.setPermaLink(PermaLinkUtil.getPermalink(binder));
        if(binder.getEntryDefId() != null)
            model.setDefinition(new StringIdLinkPair(binder.getEntryDefId(), LinkUriUtil.getDefinitionLinkUri(binder.getEntryDefId())));
        if(binder.getCreation() != null) {
            model.setCreation(buildHistoryStamp(binder.getCreation()));
        }
        if(binder.getModification() != null) {
            model.setModification(buildHistoryStamp(binder.getModification()));
        }
        if (binder.getParentBinder()!=null) {
            Long parentId = binder.getParentBinder().getId();
            model.setParentBinder(new ParentBinder(parentId, LinkUriUtil.getBinderLinkUri(parentId)));
        }
    }

    private static AverageRating buildAverageRating(org.kablink.teaming.domain.AverageRating rating){
        AverageRating model = new AverageRating();
        model.setAverage(rating.getAverage());
        model.setCount(rating.getCount());
        return model;
    }

    private static HistoryStamp buildHistoryStamp(org.kablink.teaming.domain.HistoryStampBrief historyStamp) {
        Long userId = historyStamp.getPrincipalId();
        return new HistoryStamp(new LongIdLinkPair(userId, LinkUriUtil.getUserLinkUri(userId)), historyStamp.getDate());
    }

    public static HistoryStamp buildHistoryStamp(org.kablink.teaming.domain.HistoryStamp historyStamp) {
        if (historyStamp==null) {
            return null;
        }
        Long userId = historyStamp.getPrincipal().getId();
        return new HistoryStamp(new LongIdLinkPair(userId, LinkUriUtil.getUserLinkUri(userId)), historyStamp.getDate());
    }

    public static Description buildDescription(org.kablink.teaming.domain.Description description, int descriptionFormat){
        Description model = new Description();
        if (descriptionFormat==org.kablink.teaming.domain.Description.FORMAT_NONE) {
            model.setText(description.getStrippedText(true));
            model.setFormat(descriptionFormat);
            model.setFormatString("text");
        } else if (descriptionFormat==org.kablink.teaming.domain.Description.FORMAT_HTML) {
            model.setText(description.getHtmlText());
            model.setFormat(descriptionFormat);
            model.setFormatString("html");
        } else {
            model.setText(description.getText());
            model.setFormat(org.kablink.teaming.domain.Description.FORMAT_AUTOMATIC);
            model.setFormatString("unknown");
        }
        return model;
    }

    public static Description buildDescription(String text, String formatStr, int descriptionFormat){
        Description model = null;
        if (text!=null) {
            int format = org.kablink.teaming.domain.Description.FORMAT_HTML;
            if (formatStr!=null) {
                try {
                    format = Integer.parseInt(formatStr);
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }
            model = buildDescription(new org.kablink.teaming.domain.Description(text, format), descriptionFormat);
        }
        return model;
    }

    public static DefinitionBrief buildDefinitionBrief(Definition def) {
        DefinitionBrief model = new DefinitionBrief();
        model.setId(def.getId());
        model.setInternalId(def.getInternalId());
        model.setName(def.getName());
        model.setTitle(def.getTitle());
        model.setType(def.getType());
        model.setLink(LinkUriUtil.getDefinitionLinkUri(model.getId()));
        return model;
    }

    public static TemplateBrief buildTemplateBrief(TemplateBinder template) {
        TemplateBrief model = new TemplateBrief();
        populateDefinableEntityBrief(model, template);
        model.setInternalId(template.getInternalId());
        model.setName(template.getName());
        model.setLink(LinkUriUtil.getTemplateLinkUri(model));
        return model;

    }

    public static Operation buildFolderOperation(BinderModule.BinderOperation operation) {
        Operation model = new Operation(operation.name(), LinkUriUtil.getFolderOperationLinkUri(operation));
        model.addAdditionalLink("permissions", model.getLink() + "/permissions");
        return model;
    }

    public static Operation buildFolderOperation(FolderModule.FolderOperation operation) {
        Operation model = new Operation(operation.name(), LinkUriUtil.getFolderOperationLinkUri(operation));
        model.addAdditionalLink("permissions", model.getLink() + "/permissions");
        return model;
    }

    public static Operation buildFolderEntryOperation(FolderModule.FolderOperation operation) {
        Operation model = new Operation(operation.name(), LinkUriUtil.getFolderEntryOperationLinkUri(operation));
        model.addAdditionalLink("permissions", model.getLink() + "/permissions");
        return model;
    }


    public static DefinitionModule getDefinitionModule() {
   		if(definitionModule == null)
   			definitionModule = (DefinitionModule) SpringContextUtil.getBean("definitionModule");
   		return definitionModule;
   	}

    public static Share buildShare(ShareItem shareItem, org.kablink.teaming.domain.DefinableEntity sharedEntity, ShareRecipient recipient, boolean guestEnabled) {
        Share model = new Share();
        model.setComment(shareItem.getComment());
        model.setSharer(new LongIdLinkPair(shareItem.getSharerId(), LinkUriUtil.getUserLinkUri(shareItem.getSharerId())));
        if (shareItem.getDaysToExpire()>0) {
            model.setDaysToExpire(shareItem.getDaysToExpire());
        }
        model.setStartDate(shareItem.getStartDate());
        model.setEndDate(shareItem.getEndDate());
        model.setId(shareItem.getId());

        populateRecipientLink(recipient);
        model.setRecipient(recipient);
        model.setSharedEntity(buildEntityId(shareItem.getSharedEntityIdentifier()));

        Access access = new Access();
        access.setRole(shareItem.getRole().name());
        SharingPermission sharing = new SharingPermission();
        access.setSharing(sharing);
        WorkAreaOperation.RightSet rightSet = shareItem.getRightSet();
        sharing.setInternal(rightSet.isAllowSharing());
        sharing.setExternal(rightSet.isAllowSharingExternal());
        sharing.setPublic(rightSet.isAllowSharingPublic() && guestEnabled);
        sharing.setPublicLink(rightSet.isAllowSharingPublicLinks());
        sharing.setGrantReshare(rightSet.isAllowSharingForward());
        model.setAccess(access);
        model.setRole(access.getRole());
        model.setCanShare(sharing.getPublic() || sharing.getPublicLink() || sharing.getExternal() || sharing.getInternal());

        model.setLink(LinkUriUtil.getShareLinkUri(model.getId()));

        if (shareItem.getRecipientType()== ShareItem.RecipientType.publicLink) {
            Map<String, String> urls = buildPublicLinks(shareItem, sharedEntity);
            for (String name : urls.keySet()) {
                model.addAdditionalPermaLink(name, urls.get(name));
            }
        }

        return model;
    }

    public static Map<String, String> buildPublicLinks(ShareItem shareItem, org.kablink.teaming.domain.DefinableEntity sharedEntity) {
        Map<String, String> urls = new HashMap<String, String>();
        String fileName = MiscUtil.getPrimaryFileName(sharedEntity);
        urls.put("download", PermaLinkUtil.getSharedPublicFileDownloadPermalink(shareItem.getId(), shareItem.getPassKey(), fileName));
        if (SsfsUtil.supportsViewAsHtml(fileName) || MiscUtil.isPdf(fileName)) {
            urls.put("view", PermaLinkUtil.getSharedPublicFileViewPermalink(shareItem.getId(), shareItem.getPassKey(), fileName));
        }
        return urls;
    }

    private static void populateRecipientLink(ShareRecipient recipient) {
        String type = recipient.getType();
        String link = null;
        Long recipientId = recipient.getId();
        if (type.equals(ShareRecipient.INTERNAL_USER) || type.equals(ShareRecipient.EXTERNAL_USER)) {
            link = LinkUriUtil.getDefinableEntityLinkUri(EntityIdentifier.EntityType.user, recipientId);
        } else if (type.equals(ShareRecipient.GROUP)) {
            link = LinkUriUtil.getDefinableEntityLinkUri(EntityIdentifier.EntityType.group, recipientId);
        } else if (type.equals(ShareRecipient.TEAM)) {
            link = LinkUriUtil.getBinderLinkUri(recipientId);
        }
        recipient.setLink(link);
    }

    public static SharedBinderBrief buildSharedBinderBrief(ShareItem shareItem, ShareRecipient recipient, org.kablink.teaming.domain.Binder binder, boolean guestEnabled) {
        SharedBinderBrief model = new SharedBinderBrief();
        populateBinderBrief(model, binder);
        model.addShare(buildShare(shareItem, binder, recipient, guestEnabled));
        return model;
    }

    public static SharedFileProperties buildSharedFileProperties(ShareItem shareItem, ShareRecipient recipient, FileAttachment attachment, boolean guestEnabled) {
        SharedFileProperties model = new SharedFileProperties();
        populateFileProperties(model, attachment);
        model.addShare(buildShare(shareItem, attachment.getOwner().getEntity(), recipient, guestEnabled));
        return model;
    }

    public static SharedFolderEntryBrief buildSharedFolderEntryBrief(ShareItem shareItem, ShareRecipient recipient, org.kablink.teaming.domain.FolderEntry entry, boolean guestEnabled) {
        SharedFolderEntryBrief model = new SharedFolderEntryBrief();
        populateEntryBrief(model, entry);
        model.addShare(buildShare(shareItem, entry, recipient, guestEnabled));
        return model;
    }

    public static StringIdLinkPair buildAvatar(String fileId) {
        if (fileId==null) {
            return null;
        }
        StringIdLinkPair model = new StringIdLinkPair();
        model.setId(fileId);
        LinkUriUtil.populateAvatarLinks(model);
        return model;
    }

    public static org.kablink.teaming.rest.v1.model.MobileDevice buildMobileDevice(org.kablink.teaming.domain.MobileDevice device) {
    	org.kablink.teaming.rest.v1.model.MobileDevice model = new org.kablink.teaming.rest.v1.model.MobileDevice();
        model.setId(device.getDeviceId());
        model.setDescription(device.getDescription());
        model.setLastLogin(device.getLastLogin());
        model.setLastWipe(device.getLastWipe());
//!     model.setPushToken(device.getPushToken());
        model.setWipeScheduled(device.getWipeScheduled());
        model.setLink("/self/mobile_devices/" + model.getId());
        return model;
    }

    public static Date max(Date d1, Date d2) {
        return DateHelper.max(d1, d2);
    }

    public static Date min(Date d1, Date d2) {
        return DateHelper.min(d1, d2);
    }
}
