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
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoTagByTheIdException;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.FolderEntryBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.Operation;
import org.kablink.teaming.rest.v1.model.Permission;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTree;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;

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

@Path("/folder_entries")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FolderEntryResource extends AbstractDefinableEntityResource {

	@GET
	public SearchResultList<FolderEntryBrief> getFolderEntries(@QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                               @QueryParam("first") @DefaultValue("0") Integer offset,
			                                                   @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        Document queryDoc = buildQueryDocument("<query/>", buildEntriesAndRepliesCriterion());
        Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(offset);
        Map<String, String> nextParams = new HashMap<String, String>();
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
        Document queryDoc = buildQueryDocument(query, buildEntriesAndRepliesCriterion());
        Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(offset);
        Map<String, String> nextParams = new HashMap<String, String>();
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

	// Delete folder entry
	@DELETE
    @Path("{id}")
	public void deleteFolderEntry(@PathParam("id") long id, @QueryParam("purge") @DefaultValue("false") boolean purge) {
        org.kablink.teaming.domain.FolderEntry folderEntry = _getFolderEntry(id);
        if (purge) {
            getFolderModule().deleteEntry(folderEntry.getParentBinder().getId(), id);
        } else {
            getFolderModule().preDeleteEntry(folderEntry.getParentBinder().getId(), id, getLoggedInUserId());
        }
	}

    @GET
    @Path("{id}/reservation")
    public HistoryStamp getReservation(@PathParam("id") Long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = _getFolderEntry(id);
        org.kablink.teaming.domain.HistoryStamp reservation = hEntry.getReservation();
        return ResourceUtil.buildHistoryStamp(reservation);
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
    @Path("{id}/reply_tree")
    public SearchResultTree<FolderEntry> getReplyTree(@PathParam("id") Long id,
                                                      @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        SearchResultTree<FolderEntry> tree = new SearchResultTree<FolderEntry>();
        populateReplies(entry, tree, textDescriptions);
        return tree;
    }

    @GET
    @Path("{id}/replies")
    public SearchResultList<FolderEntry> getReplies(@PathParam("id") Long id,
                                                    @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        List replies = entry.getReplies();
        SearchResultList<FolderEntry> results = new SearchResultList<FolderEntry>();
        for (Object o : replies) {
            org.kablink.teaming.domain.FolderEntry reply = (org.kablink.teaming.domain.FolderEntry) o;
            if (!reply.isPreDeleted()) {
                results.append(ResourceUtil.buildFolderEntry(reply, false, textDescriptions));
            }
        }
        return results;
    }

    @POST
    @Path("{id}/replies")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FolderEntry addReply(@PathParam("id") Long id,
                                FolderEntry entry,
                                @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.FolderEntry parent = _getFolderEntry(id);
        String defId = null;
        if (entry.getDefinition()!=null) {
            defId = entry.getDefinition().getId();
        }
        Map options = new HashMap();
      	populateTimestamps(options, entry);
        org.kablink.teaming.domain.FolderEntry newEntry = getFolderModule().addReply(null, id, defId, new RestModelInputData(entry), null, options);
        return ResourceUtil.buildFolderEntry(newEntry, true, textDescriptions);
    }

    @GET
    @Path("{id}/tags")
    public SearchResultList<Tag> getTags(@PathParam("id") Long id) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getFolderModule().getTags(entry);
        SearchResultList<Tag> results = new SearchResultList<Tag>();
        for (org.kablink.teaming.domain.Tag tag : tags) {
            results.append(ResourceUtil.buildTag(tag));
        }
        return results;
    }

    @POST
    @Path("{id}/tags")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SearchResultList<Tag> addTag(@PathParam("id") Long id, Tag tag) {
        _getFolderEntry(id);
        org.kablink.teaming.domain.Tag[] tags = getFolderModule().setTag(null, id, tag.getName(), tag.isPublic());
        SearchResultList<Tag> results = new SearchResultList<Tag>();
        for (org.kablink.teaming.domain.Tag tg : tags) {
            results.append(ResourceUtil.buildTag(tg));
        }
        return results;
    }

    @DELETE
    @Path("{id}/tags")
    public void deleteTags(@PathParam("id") Long id) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getFolderModule().getTags(entry);
        for (org.kablink.teaming.domain.Tag tag : tags) {
            getFolderModule().deleteTag(null, id, tag.getId());
        }
    }

    @GET
    @Path("{id}/tags/{tagId}")
    public Tag getTag(@PathParam("id") Long id, @PathParam("tagId") String tagId) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getFolderModule().getTags(entry);
        for (org.kablink.teaming.domain.Tag tag : tags) {
            if (tag.getId().equals(tagId)) {
                return ResourceUtil.buildTag(tag);
            }
        }
        throw new NoTagByTheIdException(tagId);
    }

    @DELETE
    @Path("{id}/tags/{tagId}")
    public void deleteTag(@PathParam("id") Long id, @PathParam("tagId") String tagId) {
        getFolderModule().deleteTag(null, id, tagId);
    }

    private void populateReplies(org.kablink.teaming.domain.FolderEntry entry, SearchResultTreeNode<FolderEntry> node, boolean textDescriptions) {
        List replies = entry.getReplies();
        for (Object o : replies) {
            org.kablink.teaming.domain.FolderEntry reply = (org.kablink.teaming.domain.FolderEntry) o;
            if (!reply.isPreDeleted()) {
                SearchResultTreeNode<FolderEntry> childNode = node.addChild(ResourceUtil.buildFolderEntry(reply, false, textDescriptions));
                populateReplies(reply, childNode, textDescriptions);
            }
        }
    }

    private org.kablink.teaming.domain.FolderEntry _getFolderEntry(long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = getFolderModule().getEntry(null, id);
        if (hEntry.isPreDeleted()) {
            throw new NoFolderEntryByTheIdException(id);
        }
        return hEntry;
    }

    @Override
    EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.folderEntry;
    }
}
