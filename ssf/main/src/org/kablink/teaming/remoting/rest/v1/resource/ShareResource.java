/*
 * Copyright © 2009-2010 Novell, Inc.  All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.  IT MAY NOT BE USED, COPIED,
 * DISTRIBUTED, DISCLOSED, ADAPTED, PERFORMED, DISPLAYED, COLLECTED, COMPILED, OR LINKED WITHOUT NOVELL'S
 * PRIOR WRITTEN CONSENT.  USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE
 * PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 * NOVELL PROVIDES THE WORK "AS IS," WITHOUT ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING WITHOUT THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. NOVELL, THE
 * AUTHORS OF THE WORK, AND THE OWNERS OF COPYRIGHT IN THE WORK ARE NOT LIABLE FOR ANY CLAIM, DAMAGES,
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN
 * CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
 */

package org.kablink.teaming.remoting.rest.v1.resource;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.util.search.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * User: david
 * Date: 7/25/12
 * Time: 2:25 PM
 */
@Path("/shares")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class ShareResource extends AbstractResource {

    @GET
    @Path("/{id}")
    public Share getShare(@PathParam("id") Long id) {
        ShareItem share = getSharingModule().getShareItem(id);
        if (!share.isLatest() || !share.getSharerId().equals(getLoggedInUserId())) {
            // Don't allow the user to modify a share that is not the latest version of the share, or that was shared
            // by someone else.
            throw new NoShareItemByTheIdException(id);
        }
        return ResourceUtil.buildShare(share);
    }

    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Share updateShare(@PathParam("id") Long id, Share share) {
        ShareItem origItem = getSharingModule().getShareItem(id);
        if (!origItem.isLatest() || !origItem.getSharerId().equals(getLoggedInUserId())) {
            // Don't allow the user to modify a share that is not the latest version of the share, or that was shared
            // by someone else.
            throw new NoShareItemByTheIdException(id);
        }
        // You can't change the shared entity or the recipient via this API.  Perhaps I should fail if the client supplies
        // these values and they don't match?
        share.setSharedEntity(new EntityId(origItem.getSharedEntityIdentifier().getEntityId(), origItem.getSharedEntityIdentifier().getEntityType().name(), null));
        share.setRecipient(new EntityId(origItem.getRecipientId(), origItem.getRecipientType().name(), null));
        ShareItem shareItem = toShareItem(share);
        getSharingModule().modifyShareItem(shareItem, id);
        return ResourceUtil.buildShare(shareItem);
    }

    @DELETE
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteShare(@PathParam("id") Long id) {
        getSharingModule().deleteShareItem(id);
    }

    @GET
    @Path("/by_user/{id}")
    public SearchResultList<Share> getSharedByUser(@PathParam("id") Long userId) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<ShareItem> shareItems = getShareItems(spec);
        for (ShareItem shareItem : shareItems) {
            results.append(ResourceUtil.buildShare(shareItem));
        }
        return results;
    }

    @GET
    @Path("/by_user/{id}/binders")
    public SearchResultList<SharedBinderBrief> getBindersSharedByUser(@PathParam("id") Long userId) {
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedByBinders(userId, false, true));
        return results;
    }

    @GET
    @Path("/by_user/{id}/binder_tree")
    public BinderTree getSharedByUserBinderTree(@PathParam("id") Long userId,
                                                @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        SharedBinderBrief [] sharedBinders = getSharedByBinders(userId, false, false);
        return getSubBinderTree(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", sharedBinders, null, textDescriptions);
    }

    @GET
    @Path("/by_user/{id}/library_folders")
    public SearchResultList<SharedBinderBrief> getLibraryFoldersSharedByUser(@PathParam("id") Long userId) {
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedByBinders(userId, true, true));
        return results;
    }

    @GET
    @Path("/by_user/{id}/entries")
    public SearchResultList<SharedFolderEntryBrief> getEntriesSharedByUser(@PathParam("id") Long userId,
                                                                           @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        return _getSharedEntries(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", spec, null, includeParentPaths);
    }

    @GET
    @Path("/by_user/{id}/files")
    public SearchResultList<FileProperties> getFilesSharedByUser(@PathParam("id") Long userId,
                                                                 @QueryParam("file_name") String fileName,
                                                                 @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                 @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedByFiles(userId, false));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedByBinders(userId, false, false), fileName, false));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    @GET
    @Path("/by_user/{id}/library_entities")
    public SearchResultList<SearchableObject> getLibraryEntitiesSharedByUser(@PathParam("id") Long userId,
                                                                               @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                               @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
                                                                               @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
                                                                               @QueryParam("files") @DefaultValue("true") boolean includeFiles,
                                                                               @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
                                                                               @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                               @QueryParam("keyword") String keyword,
                                                                               @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                                               @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                               @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        _getUser(userId);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("binders", Boolean.toString(includeBinders));
        nextParams.put("folder_entries", Boolean.toString(includeFolderEntries));
        nextParams.put("files", Boolean.toString(includeFiles));
        nextParams.put("replies", Boolean.toString(includeReplies));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        if (keyword!=null) {
            nextParams.put("keyword", keyword);
        }
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        SearchResultList<SearchableObject> results = _getLibraryEntities(null, recursive, includeBinders, includeFolderEntries, includeFiles, includeReplies, includeParentPaths, keyword, textDescriptions, offset, maxCount, "/with_user/" + userId + "/library_entities", nextParams, spec);
        return results;
    }

    @GET
    @Path("/by_user/{id}/library_files")
    public SearchResultList<FileProperties> getLibraryFilesSharedByUser(@PathParam("id") Long userId,
                                                                        @QueryParam("file_name") String fileName,
                                                                        @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                        @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedByFiles(userId, true));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedByBinders(userId, true, false), fileName, true));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    @GET
    @Path("/by_user/{id}/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getRecentActivityInSharedByUser(
            @PathParam("id") Long userId,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));

        ShareItemSelectSpec spec = getSharedBySpec(userId);
        return _getRecentActivity(includeParentPaths, textDescriptions, offset, maxCount, spec, null, "/shares/by_user/" + userId + "/recent_activity", nextParams);
    }

    @GET
    @Path("/with_user/{id}")
    public SearchResultList<Share> getSharedWithUser(@PathParam("id") Long userId) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<ShareItem> shareItems = getShareItems(spec, userId);
        for (ShareItem shareItem : shareItems) {
            results.append(ResourceUtil.buildShare(shareItem));
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/binders")
    public SearchResultList<SharedBinderBrief> getBindersSharedWithUser(@PathParam("id") Long userId) {
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedWithBinders(userId, false, true));
        return results;
    }

    @GET
    @Path("/with_user/{id}/binder_tree")
    public BinderTree getSharedWithUserBinderTree(@PathParam("id") Long userId,
                                                  @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        SharedBinderBrief [] sharedBinders = getSharedWithBinders(userId, false, false);
        return getSubBinderTree(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", sharedBinders, null, textDescriptions);
    }

    @GET
    @Path("/with_user/{id}/library_folders")
    public SearchResultList<SharedBinderBrief> getLibraryFoldersSharedWithUser(@PathParam("id") Long userId) {
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedWithBinders(userId, true, true));
        return results;
    }

    @GET
    @Path("/with_user/{id}/library_entities")
    public SearchResultList<SearchableObject> getLibraryEntitiesSharedWithUser(@PathParam("id") Long userId,
                                                      @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                      @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
                                                      @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
                                                      @QueryParam("files") @DefaultValue("true") boolean includeFiles,
                                                      @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
                                                      @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                      @QueryParam("keyword") String keyword,
                                                      @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
                                                      @QueryParam("first") @DefaultValue("0") Integer offset,
                                                      @QueryParam("count") @DefaultValue("-1") Integer maxCount) {
        _getUser(userId);
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("binders", Boolean.toString(includeBinders));
        nextParams.put("folder_entries", Boolean.toString(includeFolderEntries));
        nextParams.put("files", Boolean.toString(includeFiles));
        nextParams.put("replies", Boolean.toString(includeReplies));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        if (keyword!=null) {
            nextParams.put("keyword", keyword);
        }
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        SearchResultList<SearchableObject> results = _getLibraryEntities(userId, recursive, includeBinders, includeFolderEntries, includeFiles, includeReplies, includeParentPaths, keyword, textDescriptions, offset, maxCount, "/with_user/" + userId + "/library_entities", nextParams, spec);
        return results;
    }

    @GET
    @Path("/with_user/{id}/entries")
    public SearchResultList<SharedFolderEntryBrief> getEntriesSharedWithUser(@PathParam("id") Long userId,
                                                                             @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        return _getSharedEntries(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", spec, userId, includeParentPaths);
    }

    @GET
    @Path("/with_user/{id}/files")
    public SearchResultList<FileProperties> getFilesSharedWithUser(@PathParam("id") Long userId,
                                                                   @QueryParam("file_name") String fileName,
                                                                   @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                   @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedWithFiles(userId, false));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedWithBinders(userId, false, false), fileName, false));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/library_files")
    public SearchResultList<FileProperties> getLibraryFilesSharedWithUser(@PathParam("id") Long userId,
                                                                          @QueryParam("file_name") String fileName,
                                                                          @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                          @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedWithFiles(userId, true));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedWithBinders(userId, true, false), fileName, true));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/library_tree")
    public BinderTree getSharedWithUserLibraryTree(@PathParam("id") Long userId,
                                                   @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        SharedBinderBrief [] sharedBinders = getSharedWithBinders(userId, true, false);
        return getSubBinderTree(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", sharedBinders, buildLibraryTreeCriterion(), textDescriptions);
    }

    @GET
    @Path("/with_user/{id}/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getRecentActivityInSharedWithUser(
            @PathParam("id") Long userId,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));

        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        return _getRecentActivity(includeParentPaths, textDescriptions, offset, maxCount, spec, userId, "/shares/with_user/" + userId + "/recent_activity", nextParams);
    }

    private SearchResultList<SearchableObject> _getRecentActivity(boolean includeParentPaths, boolean textDescriptions, Integer offset, Integer maxCount, ShareItemSelectSpec spec, Long excludedSharerId, String nextUrl, Map<String, Object> nextParams) {
        List<String> binderIds = new ArrayList<String>();
        List<String> entryIds = new ArrayList<String>();
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId);
        if (shareItems.size()==0) {
            return new SearchResultList<SearchableObject>();
        }
        for (ShareItem shareItem : shareItems) {
            EntityIdentifier entityId = shareItem.getSharedEntityIdentifier();
            if (entityId.getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                entryIds.add(entityId.getEntityId().toString());
            } else if (entityId.getEntityType()== EntityIdentifier.EntityType.folder || entityId.getEntityType()== EntityIdentifier.EntityType.workspace) {
                binderIds.add(entityId.getEntityId().toString());
            }
        }
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binderIds, entryIds, null, true, Constants.LASTACTIVITY_FIELD);
        return _getRecentActivity(includeParentPaths, textDescriptions, offset, maxCount, criteria, nextUrl, nextParams);
    }

    protected SharedBinderBrief [] getSharedByBinders(Long userId, boolean onlyLibrary, boolean replaceParent)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        if (replaceParent) {
            return _getSharedBinders(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", spec, null, onlyLibrary);
        }
        return _getSharedBinders(null, null, spec, null, onlyLibrary);
    }

    protected SharedFileProperties [] getSharedByFiles(Long userId, boolean onlyLibrary)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        return _getSharedFiles(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", spec, null, onlyLibrary);
    }

    protected SharedBinderBrief [] getSharedWithBinders(Long userId, boolean onlyLibrary, boolean replaceParent)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        if (replaceParent) {
            return _getSharedBinders(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", spec, userId, onlyLibrary);
        }
        return _getSharedBinders(null, null, spec, userId, onlyLibrary);
    }

    protected SharedFileProperties [] getSharedWithFiles(Long userId, boolean onlyLibrary)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        return _getSharedFiles(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", spec, userId, onlyLibrary);
    }

    private SearchResultList<SharedFolderEntryBrief> _getSharedEntries(Long topId, String topHref, ShareItemSelectSpec spec, Long excludedSharerId, boolean includeParentPaths) {
        Map<Long, SharedFolderEntryBrief> resultMap = new LinkedHashMap<Long, SharedFolderEntryBrief>();
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId);
        for (ShareItem shareItem : shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                try {
                    FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                    SharedFolderEntryBrief binderBrief = resultMap.get(entry.getId());
                    if (binderBrief!=null) {
                        binderBrief.addShare(ResourceUtil.buildShare(shareItem));
                    } else {
                        binderBrief = ResourceUtil.buildSharedFolderEntryBrief(shareItem, entry);
                        binderBrief.setParentBinder(new ParentBinder(topId, topHref));
                        resultMap.put(entry.getId(), binderBrief);
                    }
                } catch (AccessControlException e) {
                    logger.warn("User " + getLoggedInUserId() + " does not have permission to read an entity that was shared with him/her: " + shareItem.getEntityTypedId());
                }
            }
        }
        SearchResultList<SharedFolderEntryBrief> results = new SearchResultList<SharedFolderEntryBrief>();
        results.appendAll(resultMap.values());
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    protected SharedBinderBrief [] _getSharedBinders(Long topId, String topHref, ShareItemSelectSpec spec, Long excludedSharerId, boolean onlyLibrary)  {
        Map<Long, SharedBinderBrief> resultMap = new LinkedHashMap<Long, SharedBinderBrief>();
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId);
        for (ShareItem shareItem : shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType().isBinder()) {
                try {
                    Binder binder = (Binder) getSharingModule().getSharedEntity(shareItem);
                    if (!onlyLibrary || binder.getEntityType() == EntityIdentifier.EntityType.workspace || binder.isLibrary()) {
                        SharedBinderBrief binderBrief = resultMap.get(binder.getId());
                        if (binderBrief!=null) {
                            binderBrief.addShare(ResourceUtil.buildShare(shareItem));
                        } else {
                            binderBrief = ResourceUtil.buildSharedBinderBrief(shareItem, binder);
                            if (topId!=null) {
                                binderBrief.setParentBinder(new ParentBinder(topId, topHref));
                            }
                            resultMap.put(binder.getId(), binderBrief);
                        }
                    }
                } catch (AccessControlException e) {
                    logger.warn("User " + getLoggedInUserId() + " does not have permission to read an entity that was shared with him/her: " + shareItem.getEntityTypedId());
                }
            }
        }
        List<SharedBinderBrief> results = new ArrayList<SharedBinderBrief>();
        results.addAll(resultMap.values());
        return results.toArray(new SharedBinderBrief[results.size()]);
    }

    protected SharedFileProperties [] _getSharedFiles(Long topId, String topHref, ShareItemSelectSpec spec, Long excludedSharerId, boolean onlyLibrary)  {
        Map<String, SharedFileProperties> resultMap = new LinkedHashMap<String, SharedFileProperties>();
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId);
        for (ShareItem shareItem : shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                try {
                    FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                    Set<Attachment> attachments = entry.getAttachments();
                    for (Attachment attachment : attachments) {
                        if (attachment instanceof FileAttachment) {
                            SharedFileProperties fileProps = resultMap.get(attachment.getId());
                            if (fileProps!=null) {
                                fileProps.addShare(ResourceUtil.buildShare(shareItem));
                            } else {
                                fileProps = ResourceUtil.buildSharedFileProperties(shareItem, (FileAttachment) attachment);
                                fileProps.setBinder(new ParentBinder(topId, topHref));
                                resultMap.put(attachment.getId(), fileProps);
                            }
                        }
                    }
                } catch (AccessControlException e) {
                    logger.warn("User " + getLoggedInUserId() + " does not have permission to read an entity that was shared with him/her: " + shareItem.getEntityTypedId());
                }
            }
        }
        List<SharedFileProperties> results = new ArrayList<SharedFileProperties>();
        results.addAll(resultMap.values());
        return results.toArray(new SharedFileProperties[results.size()]);
    }

    protected BinderTree getSubBinderTree(Long topId, String topHref, SharedBinderBrief [] sharedBinders, Criterion filter, boolean textDescriptions) {
        BinderTree results = new BinderTree();
        if (sharedBinders.length>0) {
            ParentBinder topParent = new ParentBinder(topId, topHref);
            Criteria crit = new Criteria();
            if (filter!=null) {
                crit.add(filter);
            }
            crit.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
            crit.add(entryAncentryCriterion(sharedBinders));
            Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1);
            SearchResultBuilderUtil.buildSearchResultsTree(results, sharedBinders, new BinderBriefBuilder(textDescriptions), resultMap);
            results.setItem(null);
            for (SearchResultTreeNode<BinderBrief> child : results.getChildren()) {
                child.getItem().setParentBinder(topParent);
            }
        }
        return results;
    }

    protected List<FileProperties> getSubFiles(SharedBinderBrief [] sharedBinders, String fileName, boolean onlyLibraryFiles) {
        List<FileProperties> results = new ArrayList<FileProperties>();
        if (sharedBinders.length>0) {
            Junction criterion = Restrictions.conjunction()
                    .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT));

            criterion.add(entryAncentryCriterion(sharedBinders));
            if (onlyLibraryFiles) {
                criterion.add(Restrictions.eq(Constants.IS_LIBRARY_FIELD, ((Boolean) onlyLibraryFiles).toString()));
            }
            if (fileName!=null) {
                criterion.add(buildFileNameCriterion(fileName));
            }
            List<FileIndexData> files = getFileModule().getFileDataFromIndex(new Criteria().add(criterion));
            for (FileIndexData file : files) {
                results.add(ResourceUtil.buildFileProperties(file));
            }
        }
        return results;
    }

    private Criterion entryAncentryCriterion(SharedBinderBrief [] sharedBinders) {
        List<String> idList = new ArrayList<String>(sharedBinders.length);
        for (SharedBinderBrief binder : sharedBinders) {
            idList.add(binder.getId().toString());
        }
        return Restrictions.in(Constants.ENTRY_ANCESTRY, idList);
    }

    private SearchResultList<SearchableObject> _getLibraryEntities(Long excludedSharerId, boolean recursive, boolean includeBinders, boolean includeFolderEntries, boolean includeFiles, boolean includeReplies, boolean includeParentPaths, String keyword, boolean textDescriptions, Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams, ShareItemSelectSpec spec) {
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId);
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>(offset);
        if (shareItems.size()>0) {
            Junction criterion = Restrictions.conjunction();
            Junction searchContext = Restrictions.disjunction();
            for (ShareItem shareItem : shareItems) {
                Junction shareCrit = Restrictions.conjunction();
                if (keyword!=null) {
                    shareCrit.add(buildKeywordCriterion(keyword));
                }
                EntityIdentifier entityId = shareItem.getSharedEntityIdentifier();
                if (entityId.getEntityType()==EntityIdentifier.EntityType.folderEntry) {
                    shareCrit.add(Restrictions.disjunction()
                            .add(buildEntryCriterion(entityId.getEntityId()))
                            .add(buildAttachmentCriterion(entityId.getEntityId()))
                    );
                } else if (entityId.getEntityType()==EntityIdentifier.EntityType.folder ||
                        entityId.getEntityType()==EntityIdentifier.EntityType.workspace) {
                    if (recursive) {
                        shareCrit.add(buildSearchBinderCriterion(entityId.getEntityId(), true));
                    } else {
                        shareCrit.add(buildBinderCriterion(entityId.getEntityId()));
                    }
                }
                searchContext.add(shareCrit);
            }
            criterion.add(searchContext);
            criterion.add(buildDocTypeCriterion(includeBinders, includeFolderEntries, includeFiles, includeReplies));
            criterion.add(buildLibraryCriterion(true));
            Criteria crit = new Criteria();
            crit.add(criterion);
            Map resultsMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxCount);
            SearchResultBuilderUtil.buildSearchResults(results, new UniversalBuilder(textDescriptions), resultsMap,
                    nextUrl, nextParams, offset);
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    private ShareItemSelectSpec getSharedWithSpec(Long userId) {
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setRecipientsFromUserMembership(userId);
        spec.setLatest(true);
        return spec;
    }

}
