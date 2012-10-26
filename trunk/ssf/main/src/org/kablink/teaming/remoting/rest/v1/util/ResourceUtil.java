/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.ObjectPropertyNotFoundException;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.dom4j.Element;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class contains utility methods that are shared among multiple resource types.
 * Do not place in this class any methods that are used by a single resource type.
 * 
 * @author jong
 *
 */
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
   		Calendar cal = Calendar.getInstance();
   		cal.setTime(date);
   		return cal;
   	}

    public static FolderEntryBrief buildFolderEntryBrief(org.kablink.teaming.domain.FolderEntry entry) {
        FolderEntryBrief model = new FolderEntryBrief();
        populateBaseFolderEntryBrief(model, entry);
        LinkUriUtil.populateFolderEntryLinks(model, model.getId());
        return model;
    }

    public static FolderEntry buildFolderEntry(org.kablink.teaming.domain.FolderEntry entry, boolean includeAttachments, boolean textDescriptions) {
        FolderEntry model = new FolderEntry();
        populateBaseFolderEntry(model, entry, includeAttachments, textDescriptions);
        model.setEntryType(Constants.ENTRY_TYPE_ENTRY);
        model.setReservation(buildHistoryStamp(entry.getReservation()));
        LinkUriUtil.populateFolderEntryLinks(model, model.getId());
        return model;
    }

    public static Reply buildReply(org.kablink.teaming.domain.FolderEntry entry, boolean includeAttachments, boolean textDescriptions) {
        Reply model = new Reply();
        populateBaseFolderEntry(model, entry, includeAttachments, textDescriptions);
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
        fp.setPermaLink(PermaLinkUtil.getPermalink(fa.getOwningEntityId(), fa.getOwningEntityType(), true));
        fp.setBinder(new ParentBinder(fa.getBinderId(), LinkUriUtil.getBinderLinkUri(fa.getBinderId())));
        fp.setName(fa.getName());
        fp.setCreation(new HistoryStamp(new LongIdLinkPair(fa.getCreatorId(), LinkUriUtil.getUserLinkUri(fa.getCreatorId())),
                fa.getCreatedDate()));
        fp.setModification(new HistoryStamp(new LongIdLinkPair(fa.getModifierId(), LinkUriUtil.getUserLinkUri(fa.getModifierId())),
                fa.getModifiedDate()));
        fp.setLength(fa.getSize());
        fp.setMd5(fa.getMd5());
        fp.setVersionNumber(fa.getVersionNumber());
        fp.setMajorVersion(fa.getMajorVersionNumber());
        fp.setMinorVersion(fa.getMinorVersionNumber());
        LinkUriUtil.populateFileLinks(fp);
        return fp;
    }

    public static FileProperties buildFileProperties(FileAttachment fa) {
        FileProperties fp = new FileProperties();
        populateFileProperties(fp, fa);
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
        fp.setVersionNumber(fa.getHighestVersionNumber());
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
        fp.setPermaLink(PermaLinkUtil.getPermalink(entity.getId(), entity.getEntityType(), true));
        Long binderId = entity.getParentBinder().getId();
        fp.setBinder(new ParentBinder(binderId, LinkUriUtil.getBinderLinkUri(binderId)));
        LinkUriUtil.populateFileLinks(fp);
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

    public static Binder buildBinder(org.kablink.teaming.domain.Binder binder, boolean includeAttachments, boolean textDescriptions) {
        Binder model;
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            model = new Folder();
            populateFolder((Folder) model, (org.kablink.teaming.domain.Folder) binder, includeAttachments, textDescriptions);
        } else if (binder instanceof org.kablink.teaming.domain.Workspace) {
            model = new Workspace();
            populateWorkspace((Workspace)model, (org.kablink.teaming.domain.Workspace)binder, includeAttachments, textDescriptions);
        } else {
            model = new Binder();
            populateBinder(model, binder, includeAttachments, textDescriptions);
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
            model.setFirstName(u.getFirstName());
            model.setMiddleName(u.getMiddleName());
            model.setLastName(u.getLastName());
        }
        model.setLink(LinkUriUtil.getUserLinkUri(model.getId()));
        LinkUriUtil.populateUserLinks(model);
        return model;
    }

    public static GroupBrief buildGroupBrief(org.kablink.teaming.domain.UserPrincipal group) {
        GroupBrief model = new GroupBrief();
        populatePrincipalBrief(model, group);
        model.setLink(LinkUriUtil.getGroupLinkUri(model.getId()));
        LinkUriUtil.populateGroupLinks(model);
        return model;
    }

    public static User buildUser(org.kablink.teaming.domain.User user, boolean includeAttachments, boolean textDescriptions) {
        User model = new User();
        populatePrincipal(model, user, includeAttachments, textDescriptions);
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
        if (user.getMiniBlogId()!=null) {
            model.setMiniBlog(new LongIdLinkPair(user.getMiniBlogId(), LinkUriUtil.getFolderLinkUri(user.getMiniBlogId())));
        }
        model.setDiskQuota(user.getDiskQuota());
        model.setFileSizeLimit(user.getFileSizeLimit());
        model.setDiskSpaceUsed(user.getDiskSpaceUsed());
        if (user.getWorkspaceId()!=null) {
            model.setWorkspace(new LongIdLinkPair(user.getWorkspaceId(), LinkUriUtil.getWorkspaceLinkUri(user.getWorkspaceId())));
        }

        LinkUriUtil.populateUserLinks(model);

        return model;
    }

    public static Group buildGroup(org.kablink.teaming.domain.Group group, boolean includeAttachments, boolean textDescriptions) {
        Group model = new Group();
        populatePrincipal(model, group, includeAttachments, textDescriptions);

        LinkUriUtil.populateGroupLinks(model);

        return model;
    }

    public static ZoneConfig buildZoneConfig(org.kablink.teaming.domain.ZoneConfig config) {
        ZoneConfig modelConfig = new ZoneConfig();
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

        modelConfig.setFileSizeLimitUserDefault(config.getFileSizeLimitUserDefault());
        modelConfig.setFileVersionsMaxAge(config.getFileVersionsMaxAge());

        FsaConfig fsaConfig = new FsaConfig();
        fsaConfig.setAutoUpdateUrl(config.getFsaAutoUpdateUrl());
        fsaConfig.setEnabled(config.getFsaEnabled());
        fsaConfig.setSyncInterval(config.getFsaSynchInterval());
        modelConfig.setFsaConfig(fsaConfig);

        modelConfig.setMobileAccessEnabled(config.isMobileAccessEnabled());
        return modelConfig;
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

    private static void populateBaseFolderEntry(BaseFolderEntry model, org.kablink.teaming.domain.FolderEntry entry, boolean includeAttachments, boolean textDescriptions) {
        populateEntry(model, entry, includeAttachments, textDescriptions);
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

    private static void populateDefinableEntity(DefinableEntity model, org.kablink.teaming.domain.DefinableEntity entity, boolean includeAttachments, boolean textDescriptions) {
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
            model.setDescription(buildDescription(desc, textDescriptions));
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

        populateCustomFields(model, entity, textDescriptions);
    }

    private static void populateCustomFields(final DefinableEntity model, final org.kablink.teaming.domain.DefinableEntity entity, final boolean textDescriptions) {
        final Map<String, CustomField> fields = new LinkedHashMap<String, CustomField>();
        DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
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
                            Object value = attribute.getRawValue(textDescriptions);
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
            public String getFlagElementName() { return "webService"; }
        };

        getDefinitionModule().walkDefinition(entity, visitor, null);

        List<CustomField> fieldList = new ArrayList<CustomField>(fields.values());
        Collections.sort(fieldList, new Comparator<CustomField>() {
            public int compare(CustomField o1, CustomField o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        model.setCustomFields(fieldList.toArray(new CustomField[fieldList.size()]));
    }

    private static void populateEntry(Entry model, org.kablink.teaming.domain.Entry entry, boolean includeAttachments, boolean textDescriptions) {
        populateDefinableEntity(model, entry, includeAttachments, textDescriptions);
    }

    private static void populatePrincipalBrief(PrincipalBrief model, org.kablink.teaming.domain.Principal principal) {
        populateEntryBrief(model, principal);
        model.setName(principal.getName());
        model.setEmailAddress(principal.getEmailAddress());
    }

    private static void populatePrincipal(Principal model, org.kablink.teaming.domain.Principal principal, boolean includeAttachments, boolean textDescriptions) {
        populateEntry(model, principal, includeAttachments, textDescriptions);
        model.setEmailAddress(principal.getEmailAddress());
        model.setDisabled(principal.isDisabled());
        model.setReserved(principal.isReserved());
        model.setName(principal.getName());
        model.setLink(LinkUriUtil.getPrincipalLinkUri(principal));
    }

    private static void populateBinder(Binder model, org.kablink.teaming.domain.Binder binder, boolean includeAttachments, boolean textDescriptions) {
        populateDefinableEntity(model, binder, includeAttachments, textDescriptions);
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

    private static void populateWorkspace(Workspace model, org.kablink.teaming.domain.Workspace workspace, boolean includeAttachments, boolean textDescriptions) {
        populateBinder(model, workspace, includeAttachments, textDescriptions);
    }

    private static void populateFolder(Folder model, org.kablink.teaming.domain.Folder folder, boolean includeAttachments, boolean textDescriptions) {
        model.setLibrary(folder.isLibrary());
        model.setMirrored(folder.isMirrored());
        populateBinder(model, folder, includeAttachments, textDescriptions);
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

    public static Description buildDescription(org.kablink.teaming.domain.Description description, boolean textDescriptions){
        Description model = new Description();
        if (textDescriptions) {
            model.setText(description.getStrippedText());
            model.setFormat(org.kablink.teaming.domain.Description.FORMAT_NONE);
        } else {
            model.setText(description.getText());
            model.setFormat(description.getFormat());
        }
        return model;
    }

    public static Description buildDescription(String text, String formatStr, boolean textDescriptions){
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
            model = buildDescription(new org.kablink.teaming.domain.Description(text, format), textDescriptions);
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

    public static Share buildShare(ShareItem shareItem) {
        Share model = new Share();
        model.setComment(shareItem.getComment());
        model.setSharer(new LongIdLinkPair(shareItem.getSharerId(), LinkUriUtil.getUserLinkUri(shareItem.getSharerId())));
        model.setStartDate(shareItem.getStartDate());
        model.setDaysToExpire(shareItem.getDaysToExpire());
        model.setEndDate(shareItem.getEndDate());
        model.setId(shareItem.getId());

        Long recipientId = shareItem.getRecipientId();
        ShareItem.RecipientType recipType = shareItem.getRecipientType();
        String link = null;
        if (recipType==ShareItem.RecipientType.user) {
            link = LinkUriUtil.getDefinableEntityLinkUri(EntityIdentifier.EntityType.user, recipientId);
        } else if (recipType==ShareItem.RecipientType.group) {
            link = LinkUriUtil.getDefinableEntityLinkUri(EntityIdentifier.EntityType.group, recipientId);
        } else if (recipType==ShareItem.RecipientType.team) {
            link = LinkUriUtil.getBinderLinkUri(recipientId);
        }
        if (link!=null) {
            model.setRecipient(new EntityId(recipientId, recipType.name(), link));
        }
        model.setRole(shareItem.getRole().name());
        model.setSharedEntity(buildEntityId(shareItem.getSharedEntityIdentifier()));
        model.setCanShare(shareItem.getRightSet().isAllowSharing());
        model.setLink(LinkUriUtil.getShareLinkUri(model.getId()));
        return model;
    }

    public static SharedBinderBrief buildSharedBinderBrief(ShareItem shareItem, org.kablink.teaming.domain.Binder binder) {
        SharedBinderBrief model = new SharedBinderBrief();
        populateBinderBrief(model, binder);
        model.addShare(buildShare(shareItem));
        return model;
    }

    public static SharedFileProperties buildSharedFileProperties(ShareItem shareItem, FileAttachment attachment) {
        SharedFileProperties model = new SharedFileProperties();
        populateFileProperties(model, attachment);
        model.addShare(buildShare(shareItem));
        return model;
    }

    public static SharedFolderEntryBrief buildSharedFolderEntryBrief(ShareItem shareItem, org.kablink.teaming.domain.FolderEntry entry) {
        SharedFolderEntryBrief model = new SharedFolderEntryBrief();
        populateEntryBrief(model, entry);
        model.addShare(buildShare(shareItem));
        return model;
    }
}
