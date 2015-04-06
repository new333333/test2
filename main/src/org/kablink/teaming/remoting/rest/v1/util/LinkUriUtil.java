/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.search.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * User: david
 * Date: 5/24/12
 * Time: 9:11 AM
 */
public class LinkUriUtil {
    private static Map<String, String> defaultIconByEntityType = new HashMap<String, String>() {
        {
            put(Constants.ENTITY_TYPE_WORKSPACE, "/icons/workspace.png");
            put(Constants.ENTITY_TYPE_FOLDER, "/icons/folder.png");
            put(Constants.ENTRY_TYPE_USER, "/icons/User_16.png");
            put(Constants.ENTRY_TYPE_GROUP, null);
            put(Constants.ENTRY_TYPE_ENTRY, null);
            put(Constants.ENTRY_TYPE_REPLY, null);
            put(Constants.ENTRY_TYPE_APPLICATION, null);
            put(Constants.ENTRY_TYPE_APPLICATION_GROUP, null);
        }
    };

    public static String getBinderLinkUri(Long id) {
        return "/binders/" + id;
    }

    public static String getFolderLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.folder, id);
    }

    public static String getWorkspaceLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.workspace, id);
    }

    public static String getBinderLinkUri(org.kablink.teaming.domain.Binder binder) {
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            return getFolderLinkUri(binder.getId());
        } else if (binder instanceof org.kablink.teaming.domain.Workspace) {
            return getWorkspaceLinkUri(binder.getId());
        }
        return getBinderLinkUri(binder.getId());
    }

    public static String getBinderLinkUri(BinderBrief binder) {
        if (binder.isFolder()) {
            return getFolderLinkUri(binder.getId());
        } else if (binder.isWorkspace()) {
            return getWorkspaceLinkUri(binder.getId());
        }
        return getBinderLinkUri(binder.getId());
    }

    public static String getPrincipalLinkUri(org.kablink.teaming.domain.Principal principal) {
        if (principal instanceof org.kablink.teaming.domain.User) {
            return getUserLinkUri(principal.getId());
        } else if (principal instanceof org.kablink.teaming.domain.Group) {
            return getGroupLinkUri(principal.getId());
        }
        return null;
    }

    public static String getReplyLinkUri(Long id) {
        return "/replies/" + id;
    }

    public static String getFolderEntryLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.folderEntry, id);
    }

    public static String getUserLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.user, id);
    }

    public static String getGroupLinkUri(Long id) {
        return getDefinableEntityLinkUri(EntityIdentifier.EntityType.group, id);
    }

    public static String getFilePropertiesLinkUri(String fileId) {
        return getFileBaseLinkUri(fileId) + "/metadata";
    }

    public static String getFileBaseLinkUri(String fileId) {
        return "/files/" + fileId;
    }

    public static String getShareLinkUri(Long id) {
        return "/shares/" + id;
    }

    public static String getFileVersionBaseLinkUri(FileVersionProperties fp) {
        return "/file_versions/" + fp.getId();
    }

    public static void populateDefinableEntityLinks(BaseRestObject model) {
        model.addAdditionalLink("ancestry", model.getLink() + "/ancestry");
        model.addAdditionalLink("attachments", model.getLink() + "/attachments");
    }

    public static void populateBaseFolderEntryLinks(BaseRestObject model) {
        populateDefinableEntityLinks(model);
        model.addAdditionalLink("replies", model.getLink() + "/replies");
        model.addAdditionalLink("reply_tree", model.getLink() + "/reply_tree");
        model.addAdditionalLink("tags", model.getLink() + "/tags");
    }

    public static void populateReplyLinks(BaseRestObject model, Long id) {
        model.setLink(getReplyLinkUri(id));
        populateBaseFolderEntryLinks(model);
    }

    public static void populateFolderEntryLinks(BaseRestObject model, Long id) {
        model.setLink(getFolderEntryLinkUri(id));
        populateBaseFolderEntryLinks(model);
        model.addAdditionalLink("access", model.getLink() + "/access");
        model.addAdditionalLink("reservation", model.getLink() + "/reservation");
        model.addAdditionalLink("shares", model.getLink() + "/shares");
//        if (model instanceof FolderEntry) {
//            ((FolderEntry)model).addAdditionalPermaLink("subscribe", PermaLinkUtil.getSubscribePermalink(((FolderEntry) model).getId(), EntityIdentifier.EntityType.folderEntry, null));
//            ((FolderEntry)model).addAdditionalPermaLink("share", PermaLinkUtil.getSharePermalink(((FolderEntry) model).getId(), EntityIdentifier.EntityType.folderEntry, null));
//        } else if (model instanceof FolderEntryBrief) {
//            ((FolderEntryBrief)model).addAdditionalPermaLink("subscribe", PermaLinkUtil.getSubscribePermalink(((FolderEntryBrief) model).getId(), EntityIdentifier.EntityType.folderEntry, null));
//            ((FolderEntryBrief)model).addAdditionalPermaLink("share", PermaLinkUtil.getSharePermalink(((FolderEntryBrief) model).getId(), EntityIdentifier.EntityType.folderEntry, null));
//        }
    }

    public static void populateUserLinks(Long id, BaseRestObject model) {
        populateDefinableEntityLinks(model);
        model.addAdditionalLink("favorites", model.getLink() + "/favorites");
        model.addAdditionalLink("teams", model.getLink() + "/teams");
        model.addAdditionalLink("shares_with", "/shares/with_user/" + id);
        model.addAdditionalLink("shares_by", "/shares/by_user/" + id);
    }

    public static void populateGroupLinks(BaseRestObject model) {
        populateDefinableEntityLinks(model);
        model.addAdditionalLink("members", model.getLink() + "/members");
    }

    public static void populateBinderLinks(BaseRestObject model) {
        populateDefinableEntityLinks(model);
        model.addAdditionalLink("children", model.getLink() + "/children");
        model.addAdditionalLink("child_binders", model.getLink() + "/binders");
        model.addAdditionalLink("child_binder_tree", model.getLink() + "/binder_tree");
        model.addAdditionalLink("child_files", model.getLink() + "/files");
        if (isWorkspace(model) || isLibraryFolder(model)) {
            model.addAdditionalLink("library_changes", model.getLink() + "/library_changes");
            model.addAdditionalLink("library_children", model.getLink() + "/library_children");
            model.addAdditionalLink("library_info", model.getLink() + "/library_info");
            model.addAdditionalLink("child_library_entities", model.getLink() + "/library_entities");
            model.addAdditionalLink("child_library_folders", model.getLink() + "/library_folders");
            model.addAdditionalLink("child_library_tree", model.getLink() + "/library_tree");
            model.addAdditionalLink("child_library_files", model.getLink() + "/library_files");
        }
        model.addAdditionalLink("access", model.getLink() + "/access");
        model.addAdditionalLink("title", model.getLink() + "/title");
        model.addAdditionalLink("parent_binder", model.getLink() + "/parent_binder");
        model.addAdditionalLink("recent_activity", model.getLink() + "/recent_activity");
        model.addAdditionalLink("shares", model.getLink() + "/shares");
        model.addAdditionalLink("team_members", model.getLink() + "/team_members");
        model.addAdditionalLink("tags", model.getLink() + "/tags");
    }

    public static void populateWorkspaceLinks(BaseRestObject model) {
        populateBinderLinks(model);
        model.addAdditionalLink("child_workspaces", model.getLink() + "/workspaces");
        model.addAdditionalLink("child_folders", model.getLink() + "/folders");
//        if (model instanceof Workspace) {
//            ((Workspace)model).addAdditionalPermaLink("subscribe", PermaLinkUtil.getSubscribePermalink(((Workspace) model).getId(), EntityIdentifier.EntityType.workspace, null));
//            ((Workspace)model).addAdditionalPermaLink("share", PermaLinkUtil.getSharePermalink(((Workspace) model).getId(), EntityIdentifier.EntityType.workspace, null));
//        } else if (model instanceof BinderBrief) {
//            ((BinderBrief)model).addAdditionalPermaLink("subscribe", PermaLinkUtil.getSubscribePermalink(((BinderBrief) model).getId(), EntityIdentifier.EntityType.workspace, null));
//            ((BinderBrief)model).addAdditionalPermaLink("share", PermaLinkUtil.getSharePermalink(((BinderBrief) model).getId(), EntityIdentifier.EntityType.workspace, null));
//        }
    }

    public static void populateFolderLinks(BaseRestObject model) {
        populateBinderLinks(model);
        model.addAdditionalLink("child_entries", model.getLink() + "/entries");
        model.addAdditionalLink("child_folders", model.getLink() + "/folders");
//        if (model instanceof Folder) {
//            ((Folder)model).addAdditionalPermaLink("subscribe", PermaLinkUtil.getSubscribePermalink(((Folder) model).getId(), EntityIdentifier.EntityType.folder, null));
//            ((Folder)model).addAdditionalPermaLink("share", PermaLinkUtil.getSharePermalink(((Folder) model).getId(), EntityIdentifier.EntityType.folder, null));
//        } else if (model instanceof BinderBrief) {
//            ((BinderBrief)model).addAdditionalPermaLink("subscribe", PermaLinkUtil.getSubscribePermalink(((BinderBrief) model).getId(), EntityIdentifier.EntityType.folder, null));
//            ((BinderBrief)model).addAdditionalPermaLink("share", PermaLinkUtil.getSharePermalink(((BinderBrief) model).getId(), EntityIdentifier.EntityType.folder, null));
//        }
    }

    public static void populateFileLinks(FileProperties fp, Long owningEntityId, EntityIdentifier.EntityType owningEntityType) {
        fp.setLink(getFilePropertiesLinkUri(fp.getId()));
        String baseUrl = getFileBaseLinkUri(fp.getId());
        fp.addAdditionalLink("content", baseUrl);
        fp.addAdditionalLink("ancestry", fp.getOwningEntity().getLink() + "/ancestry");
        fp.addAdditionalLink("major_version", baseUrl + "/major_version");
        fp.addAdditionalLink("name", baseUrl + "/name");
        fp.addAdditionalLink("parent_folder", baseUrl + "/parent_folder");
        fp.addAdditionalLink("shares", fp.getOwningEntity().getLink() + "/shares");
        fp.addAdditionalLink("access", fp.getOwningEntity().getLink() + "/access");
        fp.addAdditionalLink("versions", baseUrl + "/versions");
        fp.addAdditionalLink("thumbnail", baseUrl + "/thumbnail");
        fp.addAdditionalLink("scaled_image", baseUrl + "/scaled");
        fp.addAdditionalLink("current_version", baseUrl + "/versions/current");
        if (owningEntityType== EntityIdentifier.EntityType.folder ||
                owningEntityType== EntityIdentifier.EntityType.folderEntry ||
                owningEntityType== EntityIdentifier.EntityType.workspace) {
            fp.addAdditionalPermaLink("subscribe", PermaLinkUtil.getSubscribePermalink(owningEntityId, owningEntityType, null));
            fp.addAdditionalPermaLink("share", PermaLinkUtil.getSharePermalink(owningEntityId, owningEntityType, null));
            fp.addAdditionalPermaLink("content", PermaLinkUtil.getFileDownloadPermalink(fp.getId(), fp.getName(), fp.getModificationDate(),
                    owningEntityId, owningEntityType));
        }
    }

    public static void populateAvatarLinks(StringIdLinkPair model) {
        model.setLink(getFilePropertiesLinkUri(model.getId()));
        String baseUrl = getFileBaseLinkUri(model.getId());
        model.addAdditionalLink("content", baseUrl);
        model.addAdditionalLink("thumbnail", baseUrl + "/thumbnail");
        model.addAdditionalLink("scaled_image", baseUrl + "/scaled");
    }

    public static void populateFileVersionLinks(FileVersionProperties fp) {
        String baseUrl = getFileVersionBaseLinkUri(fp);
        fp.addAdditionalLink("content", baseUrl);
        fp.setLink(baseUrl + "/metadata");
        fp.addAdditionalLink("note", baseUrl + "/note");
        fp.addAdditionalLink("status", baseUrl + "/status");
        fp.addAdditionalLink("current", baseUrl + "/current");
        fp.addAdditionalLink("file_metadata", "/files/" + fp.getId() + "/metadata");
    }

    public static String getIconLinkUri(String iconName, String entityType) {
        String uri = buildIconLinkUri(iconName);
        if (uri==null) {
            uri = buildIconLinkUri(defaultIconByEntityType.get(entityType));
        }
        return uri;
    }

    public static String buildIconLinkUri(String iconName) {
        if (iconName==null || iconName.length()==0) {
            return null;
        }
        if (!iconName.startsWith("/")) {
            iconName = "/" + iconName;
        }
        return "/" + MiscUtil.getStaticPath() + "images" + iconName;
    }

    public static String getDefinitionLinkUri(String id) {
        return "/definitions/" + id;
    }

    public static String getTemplateLinkUri(TemplateBrief model) {
        return "/templates/" + model.getId();
    }

    public static String getFolderOperationLinkUri(BinderModule.BinderOperation operation) {
        return "/folders/operations/" + operation.name();
    }

    public static String getFolderOperationLinkUri(FolderModule.FolderOperation operation) {
        return "/folders/operations/" + operation.name();
    }

    public static String getFolderEntryOperationLinkUri(FolderModule.FolderOperation operation) {
        return "/folder_entries/operations/" + operation.name();
    }

    public static String getTagLinkUri(Tag model) {
        EntityIdentifier.EntityType type = EntityIdentifier.EntityType.valueOf(model.getEntity().getType());
        String baseEntityUri = getDefinableEntityLinkUri(type, model.getEntity().getId());
        if (baseEntityUri!=null) {
            return baseEntityUri + "/tags/" + model.getId();
        }
        return null;
    }

    public static String getDefinableEntityLinkUri(EntityIdentifier entityId) {
        return getDefinableEntityLinkUri(entityId.getEntityType(), entityId.getEntityId());
    }

    public static String getDefinableEntityLinkUri(EntityIdentifier.EntityType type, Long id) {
        if (type==EntityIdentifier.EntityType.folderEntry) {
            return "/folder_entries/" + id;
        } else if (type==EntityIdentifier.EntityType.folder) {
            return "/folders/" + id;
        } else if (type==EntityIdentifier.EntityType.group) {
            return "/groups/" + id;
        } else if (type==EntityIdentifier.EntityType.user) {
            return "/users/" + id;
        } else if (type==EntityIdentifier.EntityType.workspace) {
            return "/workspaces/" + id;
        }
        return null;
    }

    public static String getGroupMemberLinkUri(long groupId, Long memberId) {
        return getGroupLinkUri(groupId) + "/members/" + memberId;
    }

    public static String getTeamMemberLinkUri(long teamId, Long memberId) {
        return getWorkspaceLinkUri(teamId) + "/team_members/" + memberId;
    }

    public static boolean isWorkspace(BaseRestObject model) {
        return (model instanceof Workspace ||
                (model instanceof BinderBrief && ((BinderBrief)model).isWorkspace()));
    }

    public static boolean isFolder(BaseRestObject model) {
        return (model instanceof Folder ||
                (model instanceof BinderBrief && ((BinderBrief)model).isFolder()));
    }

    public static boolean isLibraryFolder(BaseRestObject model) {
        return isFolder(model) &&
               ((model instanceof Folder && ((Folder)model).getLibrary()) ||
               (model instanceof BinderBrief && ((BinderBrief)model).getLibrary()));
    }
}
