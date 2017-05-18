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

import com.sun.jersey.spi.resource.Singleton;
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.LimitedUserView;
import org.kablink.teaming.domain.NoPrincipalByTheIdException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.remoting.rest.v1.util.PrincipalBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.Principal;
import org.kablink.teaming.rest.v1.model.PrincipalBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Path("/principals")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Users and Groups")
public class PrincipalResource extends AbstractPrincipalResource {
    /**
     * Get users and groups by ID or by keyword.
     *
     * <p>
     *     <ul>
     *         <li>By ID: <code>id=20&id=32&id=46</code></li>
     *         <li>By Keyword: <code>keyword=Jo*</code></li>
     *     </ul>
     * </p>
     * @param ids   A user or group ID.  May be specified multiple times.
     * @param keyword   A search term.  Matches on full names, login names, and email address.
     * @param groups    Whether to include groups in the results.
     * @param users     Whether to include users in the results.
     * @param includeAllUsers   Whether to include the "All Users" group in the results.
     * @param descriptionFormatStr The desired format for the user and group descriptions.  Can be "html" or "text".
     * @param offset    The index of the first result to return.
     * @param maxCount  The maximum number of results to return.
     * @return A SearchResultList of UserBrief and GroupBrief objects.
     */
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<PrincipalBrief> getPrincipals(
        @QueryParam("id") Set<Long> ids,
		@QueryParam("keyword") String keyword,
		@QueryParam("included_groups") @DefaultValue("all") String groups,
		@QueryParam("included_users") @DefaultValue("all") String users,
		@QueryParam("include_all_users_group") @DefaultValue("true") boolean includeAllUsers,
        @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
		@QueryParam("first") @DefaultValue("0") Integer offset,
		@QueryParam("count") @DefaultValue("25") Integer maxCount) {
        SearchResultList<PrincipalBrief> results;
        if (canViewUsers()) {
            PrincipalOptions userOption = toEnum(PrincipalOptions.class, "included_users", users);
            PrincipalOptions groupOption = toEnum(PrincipalOptions.class, "included_groups", groups);
            Map<String, Object> nextParams = new HashMap<String, Object>();
            Map resultMap = searchForPrincipals(ids, keyword, userOption, groupOption, includeAllUsers, descriptionFormatStr, offset, maxCount, nextParams);
            results = new SearchResultList<PrincipalBrief>(offset);
            SearchResultBuilderUtil.buildSearchResults(results, new PrincipalBriefBuilder(toDomainFormat(descriptionFormatStr)), resultMap, "/principals", nextParams, offset);
        } else {
            Set<LimitedUserView> views = searchForPrincipalsLimited(ids);
            results = new SearchResultList<PrincipalBrief>();
            for (LimitedUserView view : views) {
                results.append(ResourceUtil.buildUserBrief(view));
            }
        }
        return results;
    }

    /**
     * Get a user or group.
     * @param id    The ID of the user or group.
     * @param includeAttachments    Whether to include attachments in the returned user or group.
     * @param descriptionFormatStr The desired format for the description.  Can be "html" or "text".
     * @return  A User or Group resource.
     */
    @GET
    @Path("/{id}")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Principal getPrincipal(@PathParam("id") long id,
                        @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (canViewUsers()) {
            org.kablink.teaming.domain.Principal entry = getProfileModule().getEntry(id);
            if (entry instanceof User) {
                return ResourceUtil.buildUser((User) entry, includeAttachments, toDomainFormat(descriptionFormatStr));
            } else if (entry instanceof org.kablink.teaming.domain.Group) {
                return ResourceUtil.buildGroup((org.kablink.teaming.domain.Group) entry, includeAttachments, toDomainFormat(descriptionFormatStr));
            }
        } else {
            return ResourceUtil.buildLimitedUser(getLimitedUser(id));
        }
        throw new NoPrincipalByTheIdException(id);
    }

    protected DefinableEntity getDefinableEntity(Long id) {
        return getProfileModule().getEntry(id);
    }

    @Override
    protected EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.none;
    }
}
