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
import org.dom4j.Document;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.remoting.rest.v1.resource.AbstractResource;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.ReleaseInfo;
import org.kablink.teaming.rest.v1.model.RootRestObject;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.ZoneConfig;
import org.kablink.teaming.rest.v1.model.admin.AssignedSharingPermission;
import org.kablink.teaming.rest.v1.model.admin.ExternalSharingRestrictions;
import org.kablink.teaming.rest.v1.model.admin.PersonalStorage;
import org.kablink.teaming.rest.v1.model.admin.ShareSettings;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.ShareLists;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.AssignedRole;
import org.kablink.util.search.Constants;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/admin")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class MainAdminResource extends AbstractAdminResource {

    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public RootRestObject getRootObject() {
        RootRestObject obj = new RootRestObject();
        obj.addAdditionalLink("net_folder_servers", "/admin/net_folder_servers");
        obj.addAdditionalLink("net_folders", "/admin/net_folders");
        obj.addAdditionalLink("personal_storage", "/admin/personal_storage");
        obj.addAdditionalLink("share_settings", "/admin/share_settings");
        obj.addAdditionalLink("user_sources", "/admin/user_sources");
   		return obj;
   	}

    @GET
    @Path("/personal_storage")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public PersonalStorage getPersonalStorageSettings() {
        PersonalStorage settings = new PersonalStorage();
        settings.setLink("/admin/personal_storage");
        settings.setAllowPersonalStorage(AdminHelper.getAdhocFolderSettingFromZone(this));
        return settings;
    }

    @PUT
    @Path("/personal_storage")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public PersonalStorage updatePersonalStorageSettings(PersonalStorage settings) {
        validateMandatoryField(settings, "getAllowPersonalStorage");
        getAdminModule().setAdHocFoldersEnabled(settings.getAllowPersonalStorage());
        return getPersonalStorageSettings();
    }

    @GET
    @Path("/share_settings")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public ShareSettings getShareSettings() {
        ShareSettings settings = new ShareSettings();
        settings.setLink("/admin/share_settings");
        settings.addAdditionalLink("permissions", settings.getLink() + "/permissions");
        settings.addAdditionalLink("external_restrictions", settings.getLink() + "/external_restrictions");
        settings.setAllowShareWithLdapGroups(getAdminModule().isSharingWithLdapGroupsEnabled());
        settings.setSharingPermissions(getSharingPermissions());
        settings.setExternalRestrictions(getExternalSharingRestrictions());
        return settings;
    }

    @PUT
    @Path("/share_settings")
   	@Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
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

    @GET
    @Path("/share_settings/permissions")
    public List<AssignedSharingPermission> getSharingPermissions() {
        List<AssignedSharingPermission> sharing = new ArrayList<AssignedSharingPermission>();
        List<AssignedRole> globalSharingRights = AdminHelper.getGlobalSharingRights(this);
        for (AssignedRole role : globalSharingRights) {
            sharing.add(AdminResourceUtil.buildAssignedSharingPermission(role));
        }
        return sharing;
    }

    @POST
    @Path("/share_settings/permissions")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
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

    @PUT
    @Path("/share_settings/permissions")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public List<AssignedSharingPermission> setSharingPermissions(List<AssignedSharingPermission> sharingPermissions) {
        List<AssignedRole> roles = toAssignedRoles(sharingPermissions);
        if (roles!=null) {
            AdminHelper.setGlobalSharingRights(this, roles);
        }
        return getSharingPermissions();
    }

    @DELETE
    @Path("/share_settings/permissions")
    public void removeAllSharingPermissions() {
        AdminHelper.setGlobalSharingRights(this, new ArrayList<AssignedRole>());
    }

    @GET
    @Path("/share_settings/external_restrictions")
    public ExternalSharingRestrictions getExternalSharingRestrictions() {
        ExternalSharingRestrictions restrictions = new ExternalSharingRestrictions();
        ShareLists shareLists = getSharingModule().getShareLists();
        ShareLists.ShareListMode shareListMode = shareLists.getShareListMode();
        if (shareListMode== ShareLists.ShareListMode.DISABLED) {
            restrictions.setMode(ExternalSharingRestrictions.Mode.none.name());
        } else if (shareListMode == ShareLists.ShareListMode.BLACKLIST) {
            restrictions.setMode(ExternalSharingRestrictions.Mode.blacklist.name());
        } else {
            restrictions.setMode(ExternalSharingRestrictions.Mode.whitelist.name());
        }
        List<String> domains = shareLists.getDomains();
        if (domains==null) {
            restrictions.setDomainList(new ArrayList<String>(0));
        } else {
            restrictions.setDomainList(domains);
        }
        List<String> emailAddresses = shareLists.getEmailAddresses();
        if (emailAddresses==null) {
            restrictions.setEmailList(new ArrayList<String>(0));
        } else {
            restrictions.setEmailList(emailAddresses);
        }
        return restrictions;
    }

    @PUT
    @Path("/share_settings/external_restrictions")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
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
