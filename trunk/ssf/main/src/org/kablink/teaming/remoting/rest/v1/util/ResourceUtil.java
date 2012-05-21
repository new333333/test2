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

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.rest.v1.model.AverageRating;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.DefinableEntity;
import org.kablink.teaming.rest.v1.model.Description;
import org.kablink.teaming.rest.v1.model.Entry;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.Principal;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.Workspace;
import org.kablink.teaming.rest.v1.model.ZoneConfig;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.WebUrlUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class contains utility methods that are shared among multiple resource types.
 * Do not place in this class any methods that are used by a single resource type.
 * 
 * @author jong
 *
 */
public class ResourceUtil {

    public static Calendar toCalendar(Date date) {
   		Calendar cal = Calendar.getInstance();
   		cal.setTime(date);
   		return cal;
   	}

    public static FileProperties buildFileProperties(FileAttachment fa) {
        FileAttachment.FileLock fl = fa.getFileLock();
        FileProperties fp = new FileProperties(fa.getId(),
      				fa.getFileItem().getName(),
      				new HistoryStamp(buildPrincipalBrief(Utils.redactUserPrincipalIfNecessary(fa.getCreation().getPrincipal())),
                              fa.getCreation().getDate()),
      				new HistoryStamp(buildPrincipalBrief(Utils.redactUserPrincipalIfNecessary(fa.getModification().getPrincipal())),
                              fa.getModification().getDate()),
      				fa.getFileItem().getLength(),
      				fa.getHighestVersionNumber(),
      				fa.getMajorVersion(),
      				fa.getMinorVersion(),
      				fa.getFileItem().getDescription().getText(),
      				fa.getFileStatus(),
      				WebUrlUtil.getFileUrl((String)null, WebKeys.ACTION_READ_FILE, fa),
      				(fl != null && fl.getOwner() != null)? fl.getOwner().getId():null,
      				(fl!= null)? fl.getExpirationDate():null);
        return fp;
    }

    public static TeamBrief buildTeamBrief(org.kablink.teaming.domain.TeamInfo binder) {
        TeamBrief model = new TeamBrief();
        model.setId(binder.getId());
        model.setTitle(binder.getTitle());
        model.setEntityType(binder.getEntityType());
        model.setFamily(binder.getFamily());
        model.setLibrary(binder.getLibrary());
        model.setMirrored(binder.getMirrored());
        model.setDefinitionType(binder.getDefinitionType());
        model.setPath(binder.getPath());
        model.setCreation(buildHistoryStamp(binder.getCreation()));
        model.setModification(buildHistoryStamp(binder.getModification()));
        model.setDefinitionType(binder.getDefinitionType());
        model.setLink(getBinderLinkUri(model));
        populateBinderLinks(model, model.isWorkspace(), model.isFolder());
        return model;
    }

    public static TeamBrief buildTeamBrief(org.kablink.teaming.domain.Binder binder) {
        TeamBrief model = new TeamBrief();
        populateBinderBrief(model, binder);
        return model;
    }

    public static Binder buildBinder(org.kablink.teaming.domain.Binder binder) {
        Binder model;
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            model = new Folder();
            populateFolder((Folder)model, (org.kablink.teaming.domain.Folder)binder);
        } else if (binder instanceof org.kablink.teaming.domain.Workspace) {
            model = new Workspace();
            populateWorkspace((Workspace)model, (org.kablink.teaming.domain.Workspace)binder);
        } else {
            model = new Binder();
            populateBinder(model, binder);
        }
        return model;
    }

    public static BinderBrief buildBinderBrief(org.kablink.teaming.domain.Binder binder) {
        BinderBrief model = new BinderBrief();
        populateBinderBrief(model, binder);
        return model;
    }

    public static User buildUser(org.kablink.teaming.domain.User user) {
        User model = new User();
        populatePrincipal(model, user);
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
        model.setMiniBlogId(user.getMiniBlogId());
        model.setDiskQuota(user.getDiskQuota());
        model.setFileSizeLimit(user.getFileSizeLimit());
        model.setDiskSpaceUsed(user.getDiskSpaceUsed());
        model.setWorkspaceId(user.getWorkspaceId());

        if (user.getId().equals(RequestContextHolder.getRequestContext().getUserId())) {
            model.setLink("/self");
            model.addAdditionalLink("roots", model.getLink() + "/roots");
        }
        model.addAdditionalLink("favorites", model.getLink() + "/favorites");
        model.addAdditionalLink("teams", model.getLink() + "/teams");

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
		return new FileVersionProperties(
				va.getId(),
				new HistoryStamp(buildPrincipalBrief(Utils.redactUserPrincipalIfNecessary(va.getCreation().getPrincipal())), va.getCreation().getDate()),
				new HistoryStamp(buildPrincipalBrief(Utils.redactUserPrincipalIfNecessary(va.getModification().getPrincipal())), va.getModification().getDate()),
				Long.valueOf(va.getFileItem().getLength()),
				Integer.valueOf(va.getVersionNumber()),
				Integer.valueOf(va.getMajorVersion()),
				Integer.valueOf(va.getMinorVersion()),
				va.getFileItem().getDescription().getText(), 
				va.getFileStatus(),
				WebUrlUtil.getFileUrl((String)null, WebKeys.ACTION_READ_FILE, va)
				);
	}

    private static void populateDefinableEntity(DefinableEntity model, org.kablink.teaming.domain.DefinableEntity entity) {
        model.setId(entity.getId());

        if (entity.getParentBinder() != null)
            model.setParentBinderId(entity.getParentBinder().getId());

        if(entity.getEntryDefId() != null)
            model.setDefinitionId(entity.getEntryDefId());

        model.setTitle(entity.getTitle());

        org.kablink.teaming.domain.Description desc = entity.getDescription();
        if(desc != null) {
            model.setDescription(buildDescription(desc));
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
        model.setPermaLink(PermaLinkUtil.getPermalink(entity));
    }

    private static void populateEntry(Entry model, org.kablink.teaming.domain.Entry entry) {
        populateDefinableEntity(model, entry);
    }

    private static void populatePrincipal(Principal model, org.kablink.teaming.domain.Principal principal) {
        populateEntry(model, principal);
        model.setEmailAddress(principal.getEmailAddress());
        model.setDisabled(principal.isDeleted());
        model.setReserved(principal.isReserved());
        model.setName(principal.getName());
        model.setLink(getPrincipalLinkUri(principal));
    }

    private static void populateBinder(Binder model, org.kablink.teaming.domain.Binder binder) {
        populateDefinableEntity(model, binder);
        model.setPath(binder.getPathName());
        org.dom4j.Document def = binder.getEntryDefDoc();
        if(def != null) {
            model.setFamily(DefinitionUtils.getFamily(def));
        }
        model.setLink(getBinderLinkUri(binder));
        populateBinderLinks(model, model instanceof Workspace, model instanceof Folder);
    }

    private static void populateWorkspace(Workspace model, org.kablink.teaming.domain.Workspace workspace) {
        populateBinder(model, workspace);
    }

    private static void populateFolder(Folder model, org.kablink.teaming.domain.Folder folder) {
        populateBinder(model, folder);
        model.setLibrary(folder.isLibrary());
        model.setMirrored(folder.isMirrored());
    }

    private static void populateBinderBrief(BinderBrief model, org.kablink.teaming.domain.Binder binder) {
        model.setId(binder.getId());
        model.setTitle(binder.getTitle());
        model.setEntityType(binder.getEntityType().toString());
        org.dom4j.Document def = binder.getEntryDefDoc();
        if(def != null) {
            model.setFamily(DefinitionUtils.getFamily(def));
        }
        model.setLibrary(binder.isLibrary());
        model.setMirrored(binder.isMirrored());
        model.setDefinitionType(binder.getDefinitionType());
        model.setPath(binder.getPathName());
        if(binder.getCreation() != null) {
            model.setCreation(buildHistoryStamp(binder.getCreation()));
        }
        if(binder.getModification() != null) {
            model.setDefinitionType(binder.getDefinitionType());
        }
        model.setLink(getBinderLinkUri(model));
        populateBinderLinks(model, model.isWorkspace(), model.isFolder());
    }

    private static AverageRating buildAverageRating(org.kablink.teaming.domain.AverageRating rating){
        AverageRating model = new AverageRating();
        model.setAverage(rating.getAverage());
        model.setCount(rating.getCount());
        return model;
    }

    private static HistoryStamp buildHistoryStamp(org.kablink.teaming.domain.HistoryStampBrief historyStamp) {
        return new HistoryStamp(buildPrincipalBrief(historyStamp), historyStamp.getDate());
    }

    private static HistoryStamp buildHistoryStamp(org.kablink.teaming.domain.HistoryStamp historyStamp) {
        return new HistoryStamp(buildPrincipalBrief(historyStamp.getPrincipal()), historyStamp.getDate());
    }

    private static PrincipalBrief buildPrincipalBrief(org.kablink.teaming.domain.HistoryStampBrief historyStamp) {
        PrincipalBrief model = new PrincipalBrief();
        model.setId(historyStamp.getPrincipalId());
        model.setName(historyStamp.getPrincipalName());
        String link = getUserLinkUri(historyStamp.getPrincipalId());
        if (link!=null) {
            model.setLink(link);
        }
       return model;
    }

    private static PrincipalBrief buildPrincipalBrief(org.kablink.teaming.domain.Principal principal) {
        PrincipalBrief model = new PrincipalBrief();
        model.setId(principal.getId());
        model.setName(principal.getName());
        String link = getPrincipalLinkUri(principal);
        if (link!=null) {
            model.setLink(link);
        }
       return model;
    }

    private static Description buildDescription(org.kablink.teaming.domain.Description description){
        Description model = new Description();
        model.setText(description.getText());
        model.setFormat(description.getFormat());
        return model;
    }

    private static String getBinderLinkUri(org.kablink.teaming.domain.Binder binder) {
        if (binder instanceof org.kablink.teaming.domain.Folder) {
            return "/folder/" + binder.getId();
        } else if (binder instanceof org.kablink.teaming.domain.Workspace) {
            return "/workspace/" + binder.getId();
        }
        return "/binder/" + binder.getId();
    }

    public static String getBinderLinkUri(BinderBrief binder) {
        if ("folder".equals(binder.getEntityType())) {
            return "/folder/" + binder.getId();
        } else if ("workspace".equals(binder.getEntityType())) {
            return "/workspace/" + binder.getId();
        }
        return "/binder/" + binder.getId();
    }

    private static String getPrincipalLinkUri(org.kablink.teaming.domain.Principal principal) {
        if (principal instanceof org.kablink.teaming.domain.UserPrincipal) {
            return "/user/" + principal.getId();
        }
        return null;
    }

    public static String getFolderEntryLinkUri(FolderEntryBrief entry) {
        return "/folder_entry/" + entry.getId();
    }

    public static String getUserLinkUri(Long id) {
        return "/user/" + id;
    }

    public static void populateBinderLinks(BaseRestObject model, boolean isWorkspace, boolean isFolder) {
        model.addAdditionalLink("child_binders", model.getLink() + "/binders");
        model.addAdditionalLink("child_folders", model.getLink() + "/folders");
        if (isWorkspace) {
            model.addAdditionalLink("child_workspaces", model.getLink() + "/workspaces");
        }
        if (isFolder) {
            model.addAdditionalLink("child_entries", model.getLink() + "/entries");
        }
    }
}
