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
import org.dom4j.Document;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoTagByTheIdException;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.FolderEntryBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ReplyBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.rest.v1.model.Operation;
import org.kablink.teaming.rest.v1.model.Permission;
import org.kablink.teaming.rest.v1.model.Reply;
import org.kablink.teaming.rest.v1.model.ReplyBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTree;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.search.SearchUtils;
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

@Path("/replies")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Comments")
public class ReplyResource extends AbstractFolderEntryResource {

    /**
     * Get replies by ID.
     *
     * @param ids   The ID of a reply.  Can be specified multiple times.
     * @param descriptionFormatStr The desired format for the reply descriptions.  Can be "html" or "text".
     * @return A SearchResultList of ReplyBrief objects.
     */
	@GET
	public SearchResultList<ReplyBrief> getRepliesById(@QueryParam("id") Set<Long> ids,
                                                 @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                 @QueryParam("first") @DefaultValue("0") Integer offset,
			                                     @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        Junction criterion = Restrictions.conjunction();
        criterion.add(SearchUtils.buildRepliesCriterion());
        if (ids!=null) {
            Junction or = Restrictions.disjunction();
            for (Long id : ids) {
                or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
            }
            criterion.add(or);
        }
        Document queryDoc = buildQueryDocument("<query/>", criterion);
        Map replies = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<ReplyBrief> results = new SearchResultList<ReplyBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        SearchResultBuilderUtil.buildSearchResults(results, new ReplyBriefBuilder(toDomainFormat(descriptionFormatStr)),
                replies, "/replies", nextParams, offset);
        return results;
	}

    /**
     * Get a reply.
     * @param id   The ID of the repy.
     * @param includeAttachments    Whether to include file attachment metadata in the response.
     * @param descriptionFormatStr The desired format for the reply description.  Can be "html" or "text".
     * @return
     */
	@GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Reply getReply(
			@PathParam("id") long id,
            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        org.kablink.teaming.domain.FolderEntry hEntry = _getFolderEntry(id);
		return ResourceUtil.buildReply(hEntry, includeAttachments, toDomainFormat(descriptionFormatStr));
	}

    /**
     * Update the reply.
     * @param id    The ID of the reply.
     * @param entry The updated Reply object.
     * @param descriptionFormatStr The desired format for the reply description in the response.  Can be "html" or "text".
     * @return  The updated Reply object.
     */
	@PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Reply putReply(@PathParam("id") long id, Reply entry,
                          @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        SimpleProfiler.start("folderService_modifyEntry");
        HashMap options = new HashMap();
        populateTimestamps(options, entry);
        getFolderModule().modifyEntry(null, id, new RestModelInputData(entry), null, null, null, options);
        // Read it back from the database
        org.kablink.teaming.domain.Entry dEntry = getFolderModule().getEntry(null, id);
        SimpleProfiler.stop("folderService_modifyEntry");
        return ResourceUtil.buildReply((org.kablink.teaming.domain.FolderEntry) dEntry, true, toDomainFormat(descriptionFormatStr));
	}

    /**
     * Delete the specified reply and its child replies.
     *
     * @param id    The ID of the folder entry.
     */
    @DELETE
    @Path("{id}")
    public void deleteReply(@PathParam("id") long id) throws Exception {
        _deleteFolderEntry(id, true, null);
    }

    /**
     * Get a tree structure with all of the replies to this reply.
     * @param id    The ID of the reply.
     * @param descriptionFormatStr The desired format for the reply descriptions in the response.  Can be "html" or "text".
     * @return A SearchResultTree of Reply objects.
     */
    @GET
    @Path("{id}/reply_tree")
    public SearchResultTree<Reply> getReplyTree(@PathParam("id") Long id,
                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        return _getReplyTree(id, descriptionFormatStr);
    }

    /**
     * List the first level of replies to this reply.
     *
     * @param id    The ID of the reply.
     * @param descriptionFormatStr The desired format for the reply description in the response.  Can be "html" or "text".
     * @return A SearchResultList of Reply objects.
     */
    @GET
    @Path("{id}/replies")
    public SearchResultList<Reply> getReplies(@PathParam("id") Long id,
                                              @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        return _getReplies(id, descriptionFormatStr);
    }

    /**
     * Add a reply to the specified reply.
     * @param id    The ID of the reply.
     * @param entry  The reply to add.  The description text must be specified in the Reply object.  The title is optional.
     * @param descriptionFormatStr The desired format for the folder entry description in the response.  Can be "html" or "text".
     * @return The new Reply object.
     */
    @POST
    @Path("{id}/replies")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Reply addReply(@PathParam("id") Long id,
                          Reply entry,
                          @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        return _addReply(id, entry, descriptionFormatStr);
    }

    protected org.kablink.teaming.domain.FolderEntry _getFolderEntry(long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = getFolderModule().getEntry(null, id);
        if (hEntry.isPreDeleted()) {
            throw new NoFolderEntryByTheIdException(id);
        }
        if (hEntry.isTop()) {
            throw new NoFolderEntryByTheIdException(id);
        }
        return hEntry;
    }
}
