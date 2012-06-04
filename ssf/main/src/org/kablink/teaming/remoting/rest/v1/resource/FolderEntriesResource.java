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
import org.dom4j.Document;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.FolderEntryBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.Operation;
import org.kablink.teaming.rest.v1.model.Permission;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;

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
import java.util.List;
import java.util.Map;

@Path("/v1/folder_entries")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FolderEntriesResource extends AbstractDefinableEntitiesResource {

	@POST
    @Path("/legacy_query")
	public SearchResultList<FolderEntryBrief> getFolders(@Context HttpServletRequest request,
                                                         @QueryParam("first") @DefaultValue("0") Integer offset,
			                                             @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, buildEntriesCriterion());
        Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(), folderEntries, "/folder_entries/legacy_query", offset);
        return results;
	}

    @GET
    @Path("operations")
    public SearchResultList<Operation> getOperations() {
        SearchResultList<Operation> results = new SearchResultList<Operation>();
        for (FolderModule.FolderOperation operation : FolderModule.FolderOperation.values()) {
            if (operation.appliesToEntries()) {
                results.append(ResourceUtil.buildFolderEntryOperation(operation));
            }
        }
        return results;
    }

    @GET
    @Path("operation/{name}")
    public Operation getOperation(@PathParam("name") String id) {
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null) {
            return ResourceUtil.buildFolderEntryOperation(folderOp);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, id);
    }

    @GET
    @Path("operation/{name}/permissions")
    public SearchResultList<Permission> testPermissions(@PathParam("name") String id, @QueryParam("entry")List<Long> entryIds) {
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null && folderOp.appliesToEntries()) {
            return testFolderPermissions(EntityIdentifier.EntityType.folderEntry, folderOp, entryIds);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, "Checking permissions is not supported for the operation: " + id);
    }
}
