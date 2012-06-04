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
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.BinderBrief;
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

@Path("/v1/folders")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FoldersResource extends AbstractDefinableEntitiesResource {

	// Read sub-folders
	@GET
	public SearchResultList<BinderBrief> getFolders() {
        return new SearchResultList<BinderBrief>();
	}

    @POST
    @Path("/legacy_query")
   	public SearchResultList<BinderBrief> getFolders(@Context HttpServletRequest request,
                                                            @QueryParam("first") @DefaultValue("0") Integer offset,
   			                                             @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
           String query = getRawInputStreamAsString(request);
           Document queryDoc = buildQueryDocument(query, buildFoldersCriterion());
           Map resultsMap = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
           SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
           SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), resultsMap, "/folders/legacy_query", offset);
           return results;
   	}

    /**
     * Returns a list of all access-control related operations that can be performed on a folder.
     * @return A list of Operation objects
     */
    @GET
    @Path("operations")
    public SearchResultList<Operation> getOperations() {
        SearchResultList<Operation> results = new SearchResultList<Operation>();
        for (BinderModule.BinderOperation operation : BinderModule.BinderOperation.values()) {
            results.append(ResourceUtil.buildFolderOperation(operation));
        }
        for (FolderModule.FolderOperation operation : FolderModule.FolderOperation.values()) {
            if (operation.appliesToFolders()) {
                results.append(ResourceUtil.buildFolderOperation(operation));
            }
        }
        return results;
    }

    @GET
    @Path("operation/{name}")
    public Operation getOperation(@PathParam("name") String id) {
        BinderModule.BinderOperation binderOp = getBinderOperation(id);
        if (binderOp!=null) {
            return ResourceUtil.buildFolderOperation(binderOp);
        }
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null && folderOp.appliesToFolders()) {
            return ResourceUtil.buildFolderOperation(folderOp);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, id);
    }

    /**
     * Tests whether the authenticated user has permission to perform the specified operation on one or more folders.
     * @param id    The name of the operation
     * @param folderIds One or more folder IDs to test.
     * @return A list of Permission objects
     */
    @GET
    @Path("operation/{name}/permissions")
    public SearchResultList<Permission> testPermissions(@PathParam("name") String id, @QueryParam("folder")List<Long> folderIds) {
        BinderModule.BinderOperation binderOp = getBinderOperation(id);
        if (binderOp!=null) {
            return testBinderPermissions(EntityIdentifier.EntityType.folder, binderOp, folderIds);
        }
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null && folderOp.appliesToFolders()) {
            return testFolderPermissions(EntityIdentifier.EntityType.folder, folderOp, folderIds);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, "Checking permissions is not supported for the operation: " + id);
    }
}
