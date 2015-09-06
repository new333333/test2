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
package org.kablink.teaming.remoting.rest.v1.resource;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.resource.Singleton;
import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.ReleaseInfo;
import org.kablink.teaming.rest.v1.model.RootRestObject;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.ZoneConfig;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.util.search.Constants;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class MiscResource extends AbstractResource {

    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
   	public RootRestObject getRootObject() {
        RootRestObject obj = new RootRestObject();
        obj.addAdditionalLink("binders", "/binders");
        obj.addAdditionalLink("binder_library_children", "/binders/library_children");
        obj.addAdditionalLink("definitions", "/definitions");
        obj.addAdditionalLink("files", "/files");
        obj.addAdditionalLink("folders", "/folders");
        obj.addAdditionalLink("folder_operations", "/folders/operations");
        obj.addAdditionalLink("folder_entries", "/folder_entries");
        obj.addAdditionalLink("folder_entry_operations", "/folder_entries/operations");
        obj.addAdditionalLink("groups", "/groups");
        obj.addAdditionalLink("library_entities", "/workspaces/" + getWorkspaceModule().getTopWorkspaceId() + "/library_entities");
        obj.addAdditionalLink("net_folders", "/net_folders");
        obj.addAdditionalLink("principals", "/principals");
        obj.addAdditionalLink("recent_activity", "/workspaces/" + getWorkspaceModule().getTopWorkspaceId() + "/recent_activity");
        obj.addAdditionalLink("release_info", "/release_info");
        obj.addAdditionalLink("self", "/self");
        obj.addAdditionalLink("shares", "/shares");
        obj.addAdditionalLink("templates", "/templates");
        obj.addAdditionalLink("users", "/users");
        obj.addAdditionalLink("zone_config", "/zone_config");
        if (getAdminModule().testAccess(AdminModule.AdminOperation.manageFunction)) {
            obj.addAdditionalLink("admin", "/admin");
        }
   		return obj;
   	}

	@GET
	@Path("release_info")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ReleaseInfo getReleaseInfo() {
        ReleaseInfo releaseInfo = new ReleaseInfo();
        releaseInfo.setBuildDate(ResourceUtil.toCalendar(org.kablink.teaming.util.ReleaseInfo.getBuildDate()));
        releaseInfo.setBuildNumber(org.kablink.teaming.util.ReleaseInfo.getBuildNumber());
        releaseInfo.setApplianceVersion(org.kablink.teaming.util.ReleaseInfo.getApplianceVersion());
        releaseInfo.setApplianceBuildNumber(org.kablink.teaming.util.ReleaseInfo.getApplianceBuildNumber());
        releaseInfo.setContentVersion(org.kablink.teaming.util.ReleaseInfo.getContentVersion());
        releaseInfo.setLicenseRequiredEdition(org.kablink.teaming.util.ReleaseInfo.isLicenseRequiredEdition());
        releaseInfo.setProductName(org.kablink.teaming.util.ReleaseInfo.getName());
        releaseInfo.setProductVersion(org.kablink.teaming.util.ReleaseInfo.getVersion());
        releaseInfo.setServerStartTime(ResourceUtil.toCalendar(org.kablink.teaming.util.ReleaseInfo.getServerStartTime()));
        String revStr = SPropsUtil.getString("rest.api.revision", "0");
        try {
            releaseInfo.setRestApiRevision(Integer.parseInt(revStr));
        } catch (NumberFormatException e) {
            releaseInfo.setRestApiRevision(0);
        }
        return releaseInfo;
	}

	@GET
	@Path("zone_config")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ZoneConfig getZoneConfig() {
        org.kablink.teaming.domain.ZoneConfig zoneConfig =
      			getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
        ZoneInfo info = getZoneModule().getZoneInfo(zoneConfig.getZoneId());
        User loggedInUser = getLoggedInUser();
        ZoneConfig result = ResourceUtil.buildZoneConfig(zoneConfig, info,
                AdminHelper.getEffectiveMobileAppsConfigOverride(this, loggedInUser),
                AdminHelper.getEffectiveDesktopAppsConfigOverride(this, loggedInUser),
                loggedInUser,
                this);
        result.setSharingRestrictions(_getExternalSharingRestrictions());
        return result;
	}

    @GET
    @Path("static/{subpath:.+}")
    public Response getStaticResource(@PathParam("subpath") String subpath, @Context HttpServletRequest request) {
        subpath = "static/" + subpath;
        InputStream stream = getServletContext().getResourceAsStream(subpath);
        if (stream!=null) {
            String mt = new MimetypesFileTypeMap().getContentType(subpath);
            return Response.ok(stream, mt).build();
        } else {
            return Response.status(404).build();
        }
    }

    @POST
    @Path("/legacy_query")
   	public SearchResultList<SearchableObject> legacySearch(@Context HttpServletRequest request,
                                                           @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                         @QueryParam("first") @DefaultValue("0") Integer offset,
                                                         @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, null);
        Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(this, toDomainFormat(descriptionFormatStr), false),
                resultsMap, "/legacy_query", nextParams, offset);
        return results;
    }
}
