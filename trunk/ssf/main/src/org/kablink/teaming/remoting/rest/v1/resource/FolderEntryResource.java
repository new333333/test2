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
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoTagByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.FolderEntryBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.AccessRole;
import org.kablink.teaming.rest.v1.model.EntityId;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.Operation;
import org.kablink.teaming.rest.v1.model.Permission;
import org.kablink.teaming.rest.v1.model.Reply;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTree;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/folder_entries")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FolderEntryResource extends AbstractFolderEntryResource {

	@GET
	public SearchResultList<FolderEntryBrief> getFolderEntries(@QueryParam("id") Set<Long> ids,
                                                               @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                               @QueryParam("first") @DefaultValue("0") Integer offset,
			                                                   @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Junction criterion = Restrictions.conjunction();
        criterion.add(buildEntriesCriterion());
        if (ids!=null) {
            Junction or = Restrictions.disjunction();
            for (Long id : ids) {
                or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
            }
            criterion.add(or);
        }
        Document queryDoc = buildQueryDocument("<query/>", criterion);
        Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(textDescriptions), folderEntries, "/folder_entries", nextParams, offset);
        return results;
	}

	@POST
    @Path("legacy_query")
	public SearchResultList<FolderEntryBrief> getFolderEntriesViaLegacyQuery(@Context HttpServletRequest request,
                                                                             @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                         @QueryParam("first") @DefaultValue("0") Integer offset,
			                                             @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, buildEntriesCriterion());
        Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(textDescriptions), folderEntries, "/folder_entries/legacy_query", nextParams, offset);
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
    @Path("operations/{name}")
    public Operation getOperation(@PathParam("name") String id) {
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null) {
            return ResourceUtil.buildFolderEntryOperation(folderOp);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, id);
    }

    @GET
    @Path("operations/{name}/permissions")
    public SearchResultList<Permission> testPermissions(@PathParam("name") String id, @QueryParam("entry")List<Long> entryIds) {
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null && folderOp.appliesToEntries()) {
            return testFolderPermissions(EntityIdentifier.EntityType.folderEntry, folderOp, entryIds);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, "Checking permissions is not supported for the operation: " + id);
    }

	// Read folder entry
	@GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public FolderEntry getFolderEntry(
			@PathParam("id") long id,
            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        org.kablink.teaming.domain.FolderEntry hEntry = _getFolderEntry(id);
		return ResourceUtil.buildFolderEntry(hEntry, includeAttachments, textDescriptions);
	}

    // Update folder entry
	@PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public FolderEntry putFolderEntry(@PathParam("id") long id, FolderEntry entry,
                                      @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
            throws WriteFilesException, WriteEntryDataException {
        SimpleProfiler.start("folderService_modifyEntry");
        HashMap options = new HashMap();
        populateTimestamps(options, entry);
        getFolderModule().modifyEntry(null, id, new RestModelInputData(entry), null, null, null, options);
        // Read it back from the database
        org.kablink.teaming.domain.Entry dEntry = getFolderModule().getEntry(null, id);
        SimpleProfiler.stop("folderService_modifyEntry");
        return ResourceUtil.buildFolderEntry((org.kablink.teaming.domain.FolderEntry) dEntry, true, textDescriptions);
	}

    @GET
    @Path("{id}/reservation")
    public HistoryStamp getReservation(@PathParam("id") Long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = _getFolderEntry(id);
        org.kablink.teaming.domain.HistoryStamp reservation = hEntry.getReservation();
        return ResourceUtil.buildHistoryStamp(reservation);
    }

    @GET
    @Path("{id}/access_role")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public AccessRole getAccessRole(@PathParam("id") long id) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        return getAccessRole(entry);
    }

    @PUT
    @Path("{id}/reservation")
    public HistoryStamp reserve(@PathParam("id") Long id) {
        _getFolderEntry(id);
        org.kablink.teaming.domain.HistoryStamp reservation = getFolderModule().reserveEntry(null, id);
        return ResourceUtil.buildHistoryStamp(reservation);
    }

    @DELETE
    @Path("{id}/reservation")
    public void unreserve(@PathParam("id") Long id) {
        getFolderModule().unreserveEntry(null, id);
    }

    @GET
    @Path("{id}/shares")
    public SearchResultList<Share> getShares(@PathParam("id") Long id) {
        _getFolderEntry(id);
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setSharerId(getLoggedInUserId());
        spec.setLatest(true);
        spec.setSharedEntityIdentifier(new EntityIdentifier(id, EntityIdentifier.EntityType.folderEntry));
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<ShareItem> shareItems = getShareItems(spec, true);
        for (ShareItem shareItem : shareItems) {
            results.append(ResourceUtil.buildShare(shareItem));
        }
        return results;
    }

    @POST
    @Path("{id}/shares")
    public Share shareEntity(@PathParam("id") Long id, Share share) {
        return shareEntity(_getFolderEntry(id), share);
    }

    protected org.kablink.teaming.domain.FolderEntry _getFolderEntry(long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = getFolderModule().getEntry(null, id);
        if (hEntry.isPreDeleted()) {
            throw new NoFolderEntryByTheIdException(id);
        }
        if (!hEntry.isTop()) {
            throw new NoFolderEntryByTheIdException(id);
        }
        return hEntry;
    }
}
