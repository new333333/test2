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
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.util.FolderEntryBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.Pair;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/folder_entries")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@ResourceGroup("Folder Entries")
public class FolderEntryResource extends AbstractFolderEntryResource {


    /**
     * Get all of the parent binders of the folder entry.  The top workspace is the first item and the entry's parent binder is the
     * last item.
     *
     * <p>For example, the ancestry of an entry in "/Home Workspace/Personal Workspaces/Bob Barker (bbarker)/A/B" is:
     * <ul>
     *     <li>/Home Workspace</li>
     *     <li>/Home Workspace/Personal Workspaces</li>
     *     <li>/Home Workspace/Personal Workspaces/Bob Barker (bbarker)</li>
     *     <li>/Home Workspace/Personal Workspaces/Bob Barker (bbarker)/A</li>
     *     <li>/Home Workspace/Personal Workspaces/Bob Barker (bbarker)/A/B</li>
     * </ul>
     * @param id    The ID of the folder entry.
     * @return  A list of BinderBrief objects.
     */
    @GET
    @Path("{id}/ancestry")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public BinderBrief [] getAncestry(@PathParam("id") long id) {
        return _getAncestry(id);
    }

    /**
     * Get folder entries by ID.
     *
     * @param ids   The ID of a folder entry.  Can be specified multiple times.
     * @param descriptionFormatStr The desired format for the folder entry descriptions.  Can be "html" or "text".
     * @return A SearchResultList of FolderEntryBrief objects.
     */
	@GET
	public SearchResultList<FolderEntryBrief> getFolderEntries(@QueryParam("id") Set<Long> ids,
                                                               @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                               @QueryParam("first") @DefaultValue("0") Integer offset,
			                                                   @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        Junction criterion = Restrictions.conjunction();
        criterion.add(SearchUtils.buildEntriesCriterion());
        if (ids!=null) {
            Junction or = Restrictions.disjunction();
            for (Long id : ids) {
                or.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
            }
            criterion.add(or);
        }
        Document queryDoc = buildQueryDocument("<query/>", criterion);
        Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(toDomainFormat(descriptionFormatStr)), folderEntries, "/folder_entries", nextParams, offset);
        return results;
	}

	@POST
    @Path("legacy_query")
    @Undocumented
	public SearchResultList<FolderEntryBrief> getFolderEntriesViaLegacyQuery(@Context HttpServletRequest request,
                                                                             @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                         @QueryParam("first") @DefaultValue("0") Integer offset,
			                                             @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        String query = getRawInputStreamAsString(request);
        Document queryDoc = buildQueryDocument(query, SearchUtils.buildEntriesCriterion());
        Map folderEntries = getBinderModule().executeSearchQuery(queryDoc, Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>(offset);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(toDomainFormat(descriptionFormatStr)),
                folderEntries, "/folder_entries/legacy_query", nextParams, offset);
        return results;
	}

    @GET
    @Path("operations")
    @Undocumented
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
    @Undocumented
    public Operation getOperation(@PathParam("name") String id) {
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null) {
            return ResourceUtil.buildFolderEntryOperation(folderOp);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, id);
    }

    @GET
    @Path("operations/{name}/permissions")
    @Undocumented
    public SearchResultList<Permission> testPermissions(@PathParam("name") String id, @QueryParam("entry")List<Long> entryIds) {
        FolderModule.FolderOperation folderOp = getFolderOperation(id);
        if (folderOp!=null && folderOp.appliesToEntries()) {
            return testFolderPermissions(EntityIdentifier.EntityType.folderEntry, folderOp, entryIds);
        }
        throw new NotFoundException(ApiErrorCode.BAD_INPUT, "Checking permissions is not supported for the operation: " + id);
    }

    /**
     * Get a folder entry.
     * @param id   The ID of the folder entry.
     * @param includeAttachments    Whether to include file attachment metadata in the response.
     * @param descriptionFormatStr The desired format for the folder entry description.  Can be "html" or "text".
     * @return
     */
	@GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public FolderEntry getFolderEntry(
			@PathParam("id") long id,
            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        org.kablink.teaming.domain.FolderEntry hEntry = _getFolderEntry(id);
		return ResourceUtil.buildFolderEntry(hEntry, includeAttachments, toDomainFormat(descriptionFormatStr));
	}

    /**
     * Update the folder entry.
     * @param id    The ID of the folder entry.
     * @param entry The updated FolderEntry object.
     * @param descriptionFormatStr The desired format for the folder entry description in the response.  Can be "html" or "text".
     * @return  The updated FolderEntry object.
     */
	@PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public FolderEntry putFolderEntry(@PathParam("id") long id, FolderEntry entry,
                                      @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        SimpleProfiler.start("folderService_modifyEntry");
        HashMap options = new HashMap();
        populateTimestamps(options, entry);
        getFolderModule().modifyEntry(null, id, new RestModelInputData(entry), null, null, null, options);
        // Read it back from the database
        org.kablink.teaming.domain.Entry dEntry = getFolderModule().getEntry(null, id);
        SimpleProfiler.stop("folderService_modifyEntry");
        return ResourceUtil.buildFolderEntry((org.kablink.teaming.domain.FolderEntry) dEntry, true, toDomainFormat(descriptionFormatStr));
	}

    /**
     * Request that the server synchronize the file attachment metadata of the specified folder entry.  This means the
     * server will check the file content in the file's storage area and update the metadata if it is out of date.
     * <p>The Content-Type must be <code>application/x-www-form-urlencoded</code>.
     * The value of the file_name form parameter in the request body should be a UTF-8 string that has been URL encoded.</p>
     * @param id    The ID of the folder entry.
     * @param sync  If true, the file attachment metadata will be synced with the file in the storage area.
     * @param descriptionFormatStr The desired format for the folder entry description in the response.  Can be "html" or "text".
     * @return  The updated FolderEntry resource.
     */
    @POST
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public FolderEntry synchronize(@PathParam("id") Long id,
                                   @FormParam("synchronize") Boolean sync,
                                   @FormParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        if (sync==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "'synchronize' form parameter (true/false) is required");
        }
        if (Boolean.TRUE.equals(sync)) {
            org.kablink.teaming.domain.FolderEntry retEntry = synchronizeFolderEntry(entry, false);
            return ResourceUtil.buildFolderEntry(retEntry, true, toDomainFormat(descriptionFormatStr));
        }
        return null;
    }

    /**
     * Delete the specified folder entry.

     * <p>Entries in personal storage folders are moved to the trash by default.  <code>purge=true</code> will delete the entry
     * permanently instead.  Entries in external storage folders (net folders, mirrored folders) are always deleted permanantly.</p>
     *
     * @param id    The ID of the folder entry.
     * @param purge Whether the folder entry will be deleted permanently (true) or moved to the trash (false).
     * @param lastVersionNumber Only delete the entry if the current version of the entry's primary file attachment matches this version.
     * @throws Exception
     */
    @DELETE
    @Path("{id}")
    public void deleteFolderEntry(@PathParam("id") long id,
                                  @QueryParam("purge") @DefaultValue("false") boolean purge,
                                  @QueryParam("version") Integer lastVersionNumber
    ) throws Exception {
        _deleteFolderEntry(id, purge, lastVersionNumber);
    }

    /**
     * Get a tree structure with all of the replies to this folder entry.
     * @param id    The ID of the folder entry.
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
     * List the first level of replies to this entry.
     *
     * @param id    The ID of the folder entry.
     * @param descriptionFormatStr The desired format for the folder entry description in the response.  Can be "html" or "text".
     * @return A SearchResultList of Reply objects.
     */
    @GET
    @Path("{id}/replies")
    public SearchResultList<Reply> getReplies(@PathParam("id") Long id,
                                              @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        return _getReplies(id, descriptionFormatStr);
    }

    /**
     * Add a reply to the specified folder entry.
     * @param id    The ID of the folder entry.
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

    @GET
    @Path("{id}/reservation")
    @Undocumented
    public org.kablink.teaming.rest.v1.model.HistoryStamp getReservation(@PathParam("id") Long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = _getFolderEntry(id);
        org.kablink.teaming.domain.HistoryStamp reservation = hEntry.getReservation();
        return ResourceUtil.buildHistoryStamp(reservation);
    }

    /**
     * Get the access that the authenticated user has to the specified folder entry.
     * @param id    The ID of the file.
     * @return  An Access resource.
     */
    @GET
    @Path("{id}/access")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Access getAccessRole(@PathParam("id") long id) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        return getAccessRole(entry);
    }

    @PUT
    @Path("{id}/reservation")
    @Undocumented
    public HistoryStamp reserve(@PathParam("id") Long id) {
        _getFolderEntry(id);
        org.kablink.teaming.domain.HistoryStamp reservation = getFolderModule().reserveEntry(null, id);
        return ResourceUtil.buildHistoryStamp(reservation);
    }

    @DELETE
    @Path("{id}/reservation")
    @Undocumented
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
        List<Pair<ShareItem,DefinableEntity>> shareItems = getShareItems(spec, true, true, true);
        for (Pair<ShareItem, DefinableEntity> pair : shareItems) {
            ShareItem shareItem = pair.getA();
            results.append(ResourceUtil.buildShare(shareItem, getDefinableEntity(pair, true),
                    buildShareRecipient(shareItem), isGuestAccessEnabled()));
        }
        return results;
    }

    @POST
    @Path("{id}/shares")
    public Share shareEntity(@PathParam("id") Long id,
                             @QueryParam("notify") @DefaultValue("false") boolean notifyRecipient,
                             @QueryParam("notify_address") Set<String> notifyAddresses,
                             Share share) {
        return shareEntity(_getFolderEntry(id), share, notifyRecipient, notifyAddresses);
    }

    protected org.kablink.teaming.domain.FolderEntry _getFolderEntry(long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = getFolderModule().getEntry(null, id);
        if (!hEntry.isTop()) {
            throw new NoFolderEntryByTheIdException(id);
        }
        if (_isPreDeleted(hEntry)) {
            throw new NoFolderEntryByTheIdException(id);
        }
        return hEntry;
    }
}
