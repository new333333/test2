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
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.remoting.rest.v1.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.BaseBinderChange;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.EntityId;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.LibraryInfo;
import org.kablink.teaming.rest.v1.model.NotifyWarning;
import org.kablink.teaming.rest.v1.model.ParentBinder;
import org.kablink.teaming.rest.v1.model.RecentActivityEntry;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.SharedBinderBrief;
import org.kablink.teaming.rest.v1.model.SharedFileProperties;
import org.kablink.teaming.rest.v1.model.SharedFolderEntryBrief;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.util.Pair;
import org.kablink.util.VibeRuntimeException;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
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
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public NotifyWarning [] noopPostShare(@FormParam("id") List<Long> ids,
                              @FormParam("notify") @DefaultValue("false") boolean notifyRecipient,
                              @FormParam("notify_address") Set<String> notifyAddresses) {
        if (notifyRecipient && ids!=null) {
            User loggedInUser = getLoggedInUser();
            List<NotifyWarning> failures = new ArrayList<NotifyWarning>();
            for (Long id : ids) {
                try {
                    ShareItem shareItem = _getShareItem(id);
                    if (shareItem.getSharerId().equals(loggedInUser.getId())) {
                        if (shareItem.getIsPartOfPublicShare()) {
                            failures.add(new NotifyWarning(id, ApiErrorCode.NOT_SUPPORTED.name(),
                                    "Cannot notify recipients of public shares"));
                        } else {
                            try {
                                validateNotifyParameters(notifyRecipient, notifyAddresses, shareItem);
                                notifyShareRecipients(shareItem, notifyAddresses);
                            } catch (VibeRuntimeException e) {
                                logger.warn("Failed to send share notification email", e);
                                failures.add(new NotifyWarning(id, e.getApiErrorCode().name(),
                                        "Failed to send the notification email: " + e.getMessage()));
                            } catch (Exception e) {
                                logger.warn("Failed to send share notification email", e);
                                failures.add(new NotifyWarning(id, ApiErrorCode.SERVER_ERROR.name(),
                                        "Failed to send the notification email: " + e.getMessage()));
                            }
                        }
                    } else {
                        logger.warn("Notify share recipients warning: share with id " + id +
                                " was shared by someone other than the current user (" + loggedInUser.getId() + ").");
                        failures.add(new NotifyWarning(id, ApiErrorCode.SHAREITEM_NOT_FOUND.name(),
                                "Share does not exist."));
                    }
                } catch (NoShareItemByTheIdException e) {
                    logger.warn("Notify share recipients warning: no share with id " + id, e);
                    failures.add(new NotifyWarning(id, ApiErrorCode.SHAREITEM_NOT_FOUND.name(),
                            "Share does not exist."));
                }
            }
            return failures.toArray(new NotifyWarning[failures.size()]);
        }
        return new NotifyWarning[0];
    }

    @GET
    @Path("/{id}")
    public Share getShare(@PathParam("id") Long id) {
        ShareItem share = _getShareItem(id);
        return ResourceUtil.buildShare(share, findDefinableEntity(share.getSharedEntityIdentifier()), buildShareRecipient(share), isGuestAccessEnabled());
    }

    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Share updateShare(@PathParam("id") Long id,
                             @QueryParam("notify") @DefaultValue("false") boolean notifyRecipient,
                             @QueryParam("notify_address") Set<String> notifyAddresses,
                             Share share) {
        List<ShareItem> origItems;
        ShareItem item = _getShareItem(id);
        if (item.getIsPartOfPublicShare()) {
            origItems = getAllPublicShareParts(item);
            notifyRecipient = false;
        } else {
            origItems = new ArrayList<ShareItem>(1);
            origItems.add(item);
        }

        validateNotifyParameters(notifyRecipient, notifyAddresses, item);

        // You can't change the shared entity or the recipient via this API.  Perhaps I should fail if the client supplies
        // these values and they don't match?
        share.setSharedEntity(new EntityId(item.getSharedEntityIdentifier().getEntityId(), item.getSharedEntityIdentifier().getEntityType().name(), null));
        share.setRecipient(buildShareRecipient(item));
        if (item.getRecipientType() == ShareItem.RecipientType.publicLink) {
            Access access = new Access();
            access.setRole(ShareItem.Role.VIEWER.name());
            share.setAccess(access);
        }
        ShareItem newShareItem = toShareItem(share);
        ShareItem shareItem = null;
        for (ShareItem origItem : origItems) {
            shareItem = new ShareItem(newShareItem);
            shareItem.setRecipientType(origItem.getRecipientType());
            shareItem.setRecipientId(origItem.getRecipientId());
            shareItem = getSharingModule().modifyShareItem(shareItem, origItem.getId());
        }
        DefinableEntity entity = findDefinableEntity(shareItem.getSharedEntityIdentifier());
        if (notifyRecipient) {
            notifyShareRecipients(shareItem, entity, false, notifyAddresses);
        }
        return ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), isGuestAccessEnabled());
    }

    @DELETE
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteShare(@PathParam("id") Long id) {
        List<ShareItem> origItems;
        ShareItem item = _getShareItem(id);
        if (item.getIsPartOfPublicShare()) {
            origItems = getAllPublicShareParts(item);
        } else {
            origItems = new ArrayList<ShareItem>(1);
            origItems.add(item);
        }
        for (ShareItem origItem : origItems) {
            getSharingModule().deleteShareItem(origItem.getId());
        }
    }

    @GET
    @Path("/by_user/{id}")
    public SearchResultList<Share> getSharedByUser(@PathParam("id") Long userId) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<ShareItem> shareItems = getShareItems(spec, true, true, true);
        for (ShareItem shareItem : shareItems) {
            results.append(ResourceUtil.buildShare(shareItem, findDefinableEntity(shareItem.getSharedEntityIdentifier()),
                    buildShareRecipient(shareItem), isGuestAccessEnabled()));
        }
        return results;
    }

    @GET
    @Path("/by_user/{id}/binders")
    public SearchResultList<SharedBinderBrief> getBindersSharedByUser(@PathParam("id") Long userId,
                                                                      @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                      @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedByBinders(userId, false, true, showHidden, showUnhidden, true, true));
        return results;
    }

    @GET
    @Path("/by_user/{id}/binder_tree")
    public BinderTree getSharedByUserBinderTree(@PathParam("id") Long userId,
                                                @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        SharedBinderBrief [] sharedBinders = getSharedByBinders(userId, false, false, showHidden, showUnhidden, true, true);
        return getSubBinderTree(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", sharedBinders, null, toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/by_user/{id}/library_children")
    public Response getLibraryChildrenSharedByUser(@PathParam("id") Long userId,
                                                                             @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                             @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                             @Context HttpServletRequest request) {
        Date lastModified = getSharedByLibraryModifiedDate(userId, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>();
        results.appendAll(getSharedByChildren(userId, true, true, showHidden, showUnhidden, true, true));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/by_user/{id}/library_folders")
    public Response getLibraryFoldersSharedByUser(@PathParam("id") Long userId,
                                                                             @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                             @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                             @Context HttpServletRequest request) {
        Date lastModified = getSharedByLibraryModifiedDate(userId, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedByBinders(userId, true, true, showHidden, showUnhidden, true, true));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/by_user/{id}/entries")
    public SearchResultList<SharedFolderEntryBrief> getEntriesSharedByUser(@PathParam("id") Long userId,
                                                                           @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                           @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                           @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        return _getSharedEntries(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", spec, null, includeParentPaths, showHidden, showUnhidden, true, true);
    }

    @GET
    @Path("/by_user/{id}/files")
    public Response getFilesSharedByUser(@PathParam("id") Long userId,
                                                                 @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                 @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                 @QueryParam("file_name") String fileName,
                                                                 @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                 @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                 @Context HttpServletRequest request) {
        Date lastModified = getSharedByLibraryModifiedDate(userId, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedByFiles(userId, false, showHidden, showUnhidden, true, true));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedByBinders(userId, false, false, showHidden, showUnhidden, true, true), fileName, false));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/by_user/{id}/library_entities")
    public SearchResultList<SearchableObject> getLibraryEntitiesSharedByUser(@PathParam("id") Long userId,
                                                                               @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                               @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
                                                                               @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
                                                                               @QueryParam("files") @DefaultValue("true") boolean includeFiles,
                                                                               @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
                                                                               @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                               @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                               @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                               @QueryParam("keyword") String keyword,
                                                                               @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                                               @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                               @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        SearchResultList<SearchableObject> results = _getLibraryEntities(ObjectKeys.SHARED_BY_ME_ID, null, recursive,
                includeBinders, includeFolderEntries, includeFiles, includeReplies, includeParentPaths, keyword,
                toDomainFormat(descriptionFormatStr), offset, maxCount, "/shares/by_user/" + userId + "/library_entities", spec,
                showHidden, showUnhidden, false, true, true);
        return results;
    }

    @GET
    @Path("/by_user/{id}/library_files")
    public SearchResultList<FileProperties> getLibraryFilesSharedByUser(@PathParam("id") Long userId,
                                                                        @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                        @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                        @QueryParam("file_name") String fileName,
                                                                        @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                        @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedByFiles(userId, true, showHidden, showUnhidden, true, true));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedByBinders(userId, true, false, showHidden, showUnhidden, true, true), fileName, true));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        return results;
    }

    @GET
    @Path("/by_user/{id}/library_changes")
    public BinderChanges getSharedByUserLibraryChanges(@PathParam("id") Long userId,
                                                       @QueryParam("since") String since,
                                                       @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                       @QueryParam ("count") @DefaultValue("500") Integer maxCount,
                                                       @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                       @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        return getSharedByChanges(userId, since, descriptionFormatStr, maxCount, "/public/library_changes", true, showHidden, showUnhidden);
    }

    @GET
    @Path("/by_user/{id}/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<RecentActivityEntry> getRecentActivityInSharedByUser(
            @PathParam("id") Long userId,
            @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
            @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("description_format", descriptionFormatStr);

        ShareItemSelectSpec spec = getSharedBySpec(userId);
        return _getRecentActivity(ObjectKeys.SHARED_BY_ME_ID, includeParentPaths, toDomainFormat(descriptionFormatStr), offset, maxCount, spec,
                null, "/shares/by_user/" + userId + "/recent_activity", nextParams, showHidden, showUnhidden, true, true);
    }

    @GET
    @Path("/with_user/{id}")
    public SearchResultList<Share> getSharedWithUser(@PathParam("id") Long userId) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<ShareItem> shareItems = getShareItems(spec, userId, false, false, true);
        for (ShareItem shareItem : shareItems) {
            results.append(ResourceUtil.buildShare(shareItem, findDefinableEntity(shareItem.getSharedEntityIdentifier()),
                    buildShareRecipient(shareItem), isGuestAccessEnabled()));
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/binders")
    public SearchResultList<SharedBinderBrief> getBindersSharedWithUser(@PathParam("id") Long userId,
                                                                        @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                        @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedWithBinders(userId, false, true, showHidden, showUnhidden, false, true));
        return results;
    }

    @GET
    @Path("/with_user/{id}/binder_tree")
    public BinderTree getSharedWithUserBinderTree(@PathParam("id") Long userId,
                                                  @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                  @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        SharedBinderBrief [] sharedBinders = getSharedWithBinders(userId, false, false, showHidden, showUnhidden, false, true);
        return getSubBinderTree(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", sharedBinders, null, toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/with_user/{id}/library_info")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public LibraryInfo getLibraryModTime(@PathParam("id") Long userId) {
        return getSharedWithLibraryInfo(userId);
    }

    @GET
    @Path("/with_user/{id}/library_children")
    public Response getLibraryChildrenSharedWithUser(@PathParam("id") Long userId,
                                                     @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                     @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                     @Context HttpServletRequest request) {
        Date lastModified = getSharedWithLibraryModifiedDate(userId, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>();
        results.appendAll(getSharedWithChildren(userId, true, true, showHidden, showUnhidden, false, true));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/with_user/{id}/library_folders")
    public Response getLibraryFoldersSharedWithUser(@PathParam("id") Long userId,
                                                                               @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                               @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                               @Context HttpServletRequest request) {
        Date lastModified = getSharedWithLibraryModifiedDate(userId, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedWithBinders(userId, true, true, showHidden, showUnhidden, false, true));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/with_user/{id}/library_entities")
    public SearchResultList<SearchableObject> getLibraryEntitiesSharedWithUser(@PathParam("id") Long userId,
                                                      @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                      @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
                                                      @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
                                                      @QueryParam("files") @DefaultValue("true") boolean includeFiles,
                                                      @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
                                                      @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                      @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                      @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                      @QueryParam("keyword") String keyword,
                                                      @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                      @QueryParam("first") @DefaultValue("0") Integer offset,
                                                      @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        SearchResultList<SearchableObject> results = _getLibraryEntities(ObjectKeys.SHARED_WITH_ME_ID, userId, recursive,
                includeBinders, includeFolderEntries, includeFiles, includeReplies, includeParentPaths, keyword,
                toDomainFormat(descriptionFormatStr), offset, maxCount, "/shares/with_user/" + userId + "/library_entities", spec,
                showHidden, showUnhidden, false, false, true);
        return results;
    }

    @GET
    @Path("/with_user/{id}/entries")
    public SearchResultList<SharedFolderEntryBrief> getEntriesSharedWithUser(@PathParam("id") Long userId,
                                                                             @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                             @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                             @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        return _getSharedEntries(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", spec, userId, includeParentPaths, showHidden, showUnhidden, false, true);
    }

    @GET
    @Path("/with_user/{id}/files")
    public Response getFilesSharedWithUser(@PathParam("id") Long userId,
                                                                   @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                   @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                   @QueryParam("file_name") String fileName,
                                                                   @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                   @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                   @Context HttpServletRequest request) {
        Date lastModified = getSharedWithLibraryModifiedDate(userId, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedWithFiles(userId, false, showHidden, showUnhidden, false, true));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedWithBinders(userId, false, false, showHidden, showUnhidden, false, true),
                                          fileName, false));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/with_user/{id}/library_files")
    public Response getLibraryFilesSharedWithUser(@PathParam("id") Long userId,
                                                                          @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                          @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                          @QueryParam("file_name") String fileName,
                                                                          @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                          @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                          @Context HttpServletRequest request) {
        Date lastModified = getSharedWithLibraryModifiedDate(userId, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedWithFiles(userId, true, showHidden, showUnhidden, false, true));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedWithBinders(userId, true, false, showHidden, showUnhidden, false, true), fileName, true));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/with_user/{id}/library_tree")
    public BinderTree getSharedWithUserLibraryTree(@PathParam("id") Long userId,
                                                   @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                   @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                   @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        SharedBinderBrief [] sharedBinders = getSharedWithBinders(userId, true, false, showHidden, showUnhidden, false, true);
        return getSubBinderTree(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", sharedBinders,
                SearchUtils.buildLibraryTreeCriterion(), toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/with_user/{id}/library_changes")
    public BinderChanges getSharedWithUserLibraryChanges(@PathParam("id") Long userId,
                                                         @QueryParam("since") String since,
                                                         @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                         @QueryParam ("count") @DefaultValue("500") Integer maxCount,
                                                         @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                         @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        return getSharedWithChanges(userId, since, descriptionFormatStr, maxCount, "/public/library_changes", true, showHidden, showUnhidden);
    }

    @GET
    @Path("/with_user/{id}/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<RecentActivityEntry> getRecentActivityInSharedWithUser(
            @PathParam("id") Long userId,
            @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
            @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("description_format", descriptionFormatStr);

        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        return _getRecentActivity(ObjectKeys.SHARED_WITH_ME_ID, includeParentPaths, toDomainFormat(descriptionFormatStr),
                offset, maxCount, spec, userId, "/shares/with_user/" + userId + "/recent_activity", nextParams,
                showHidden, showUnhidden, false, true);
    }

    @GET
    @Path("/public")
    public SearchResultList<Share> getSharedWithUser() {
        ShareItemSelectSpec spec = getSharedWithSpec(getLoggedInUserId());
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<ShareItem> shareItems = getShareItems(spec, getLoggedInUserId(), false, true, false);
        for (ShareItem shareItem : shareItems) {
            results.append(ResourceUtil.buildShare(shareItem, findDefinableEntity(shareItem.getSharedEntityIdentifier()),
                    buildShareRecipient(shareItem), isGuestAccessEnabled()));
        }
        return results;
    }

    @GET
    @Path("/public/binders")
    public SearchResultList<SharedBinderBrief> getPublicSharesBinders(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                      @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getPublicBinders(false, true, showHidden, showUnhidden));
        return results;
    }

    @GET
    @Path("/public/binder_tree")
    public BinderTree getPublicSharesBinderTree(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        SharedBinderBrief [] sharedBinders = getPublicBinders(false, false, showHidden, showUnhidden);
        return getSubBinderTree(ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", sharedBinders, null, toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/public/library_tree")
    public BinderTree getPublicSharesLibraryTree(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        SharedBinderBrief [] sharedBinders = getPublicBinders(true, false, showHidden, showUnhidden);
        return getSubBinderTree(ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", sharedBinders, null, toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/public/library_children")
    public Response getPublicSharesLibraryChildren(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                   @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                   @Context HttpServletRequest request) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        Date lastModified = getPublicSharesLibraryModifiedDate(false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>();
        results.appendAll(getPublicChildren(true, true, showHidden, showUnhidden));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/public/library_folders")
    public Response getPublicSharesLibraryFolders(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                             @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                             @Context HttpServletRequest request) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        Date lastModified = getPublicSharesLibraryModifiedDate(false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getPublicBinders(true, true, showHidden, showUnhidden));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/public/entries")
    public SearchResultList<SharedFolderEntryBrief> getPublicSharesEntries(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                           @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                           @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        ShareItemSelectSpec spec = getSharedWithSpec(getLoggedInUserId());
        return _getSharedEntries(ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", spec, null, includeParentPaths, showHidden, showUnhidden, true, false);
    }

    @GET
    @Path("/public/files")
    public Response getPublicSharesFiles(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                 @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                 @QueryParam("file_name") String fileName,
                                                                 @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                 @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                 @Context HttpServletRequest request) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        Date lastModified = getPublicSharesLibraryModifiedDate(false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getPublicFiles(false, showHidden, showUnhidden));
        if (recursive) {
            results.appendAll(getSubFiles(getPublicBinders(false, false, showHidden, showUnhidden), fileName, false));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/public/library_files")
    public Response getPublicSharesLibraryFiles(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                 @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                 @QueryParam("file_name") String fileName,
                                                                 @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                 @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                 @Context HttpServletRequest request) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        Date lastModified = getPublicSharesLibraryModifiedDate(false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getPublicFiles(true, showHidden, showUnhidden));
        if (recursive) {
            results.appendAll(getSubFiles(getPublicBinders(true, false, showHidden, showUnhidden), fileName, false));
        }
        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/public/library_entities")
    public SearchResultList<SearchableObject> getPublicSharesLibraryEntities(@QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                             @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
                                                                             @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
                                                                             @QueryParam("files") @DefaultValue("true") boolean includeFiles,
                                                                             @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
                                                                             @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                             @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                             @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                             @QueryParam("keyword") String keyword,
                                                                             @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                                             @QueryParam("first") @DefaultValue("0") Integer offset,
                                                                             @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        ShareItemSelectSpec spec = getSharedWithSpec(getLoggedInUserId());
        SearchResultList<SearchableObject> results = _getLibraryEntities(ObjectKeys.PUBLIC_SHARES_ID, null, recursive,
                includeBinders, includeFolderEntries, includeFiles, includeReplies, includeParentPaths, keyword,
                toDomainFormat(descriptionFormatStr), offset, maxCount, "/shares/public/library_entities", spec,
                showHidden, showUnhidden, false, true, false);
        return results;
    }

    @GET
    @Path("/public/library_changes")
    public BinderChanges getPublicSharesLibraryChanges(@QueryParam("since") String since,
                                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                  @QueryParam ("count") @DefaultValue("500") Integer maxCount,
                                                  @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                  @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        return getPublicChanges(since, descriptionFormatStr, maxCount, "/public/library_changes", true, showHidden, showUnhidden);
    }

    @GET
    @Path("/public/recent_activity")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<RecentActivityEntry> getRecentActivityInPublicShares(
            @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
            @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException("Access to the public collection is not allowed.", null);
        }
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("description_format", descriptionFormatStr);

        ShareItemSelectSpec spec = getSharedWithSpec(getLoggedInUserId());
        return _getRecentActivity(ObjectKeys.PUBLIC_SHARES_ID, includeParentPaths, toDomainFormat(descriptionFormatStr), offset, maxCount, spec,
                null, "/shares/public/recent_activity", nextParams, showHidden, showUnhidden, true, false);
    }

    private SearchResultList<RecentActivityEntry> _getRecentActivity(Long topId, boolean includeParentPaths, int descriptionFormat,
                                                                  Integer offset, Integer maxCount, ShareItemSelectSpec spec,
                                                                  Long excludedSharerId, String nextUrl, Map<String, Object> nextParams,
                                                                  boolean showHidden, boolean showUnhidden, boolean showPublic, boolean showNonPublic) {
        List<String> binderIds = new ArrayList<String>();
        List<String> entryIds = new ArrayList<String>();
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId, topId==ObjectKeys.SHARED_BY_ME_ID, showPublic, showNonPublic);
        if (shareItems.size()==0) {
            return new SearchResultList<RecentActivityEntry>();
        }
        for (ShareItem shareItem : shareItems) {
            EntityIdentifier entityId = shareItem.getSharedEntityIdentifier();
            if (entityId.getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                if (showToUser(entry, topId, showHidden, showUnhidden, false)) {
                    entryIds.add(entityId.getEntityId().toString());
                }
            } else if (entityId.getEntityType()== EntityIdentifier.EntityType.folder || entityId.getEntityType()== EntityIdentifier.EntityType.workspace) {
                Binder binder = (Binder) getSharingModule().getSharedEntity(shareItem);
                if (showBinderToUser(binder, false, topId, showHidden, showUnhidden, false)) {
                    binderIds.add(entityId.getEntityId().toString());
                }
            }
        }
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binderIds, entryIds, null, true, Constants.LASTACTIVITY_FIELD);
        return _getRecentActivity(includeParentPaths, descriptionFormat, offset, maxCount, criteria, nextUrl, nextParams);
    }

    protected List<SearchableObject> getSharedByChildren(Long userId, boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden,
                                                      boolean showPublic, boolean showNonPublic)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        if (replaceParent) {
            return _getSharedEntities(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", spec, null, onlyLibrary, showHidden,
                    showUnhidden, showPublic, showNonPublic, true, false, true);
        }
        return _getSharedEntities(null, null, spec, null, onlyLibrary, showHidden, showUnhidden, showPublic, showNonPublic, true, false, true);
    }

    protected SharedBinderBrief [] getSharedByBinders(Long userId, boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden,
                                                      boolean showPublic, boolean showNonPublic)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        if (replaceParent) {
            return _getSharedBinders(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", spec, null, onlyLibrary, showHidden, showUnhidden, showPublic, showNonPublic);
        }
        return _getSharedBinders(null, null, spec, null, onlyLibrary, showHidden, showUnhidden, showPublic, showNonPublic);
    }

    protected SharedFileProperties[] getSharedByFiles(Long userId, boolean onlyLibrary, boolean showHidden, boolean showUnhidden,
                                                       boolean showPublic, boolean showNonPublic)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        return _getSharedFiles(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", spec, null, onlyLibrary, showHidden, showUnhidden, showPublic, showNonPublic);
    }

    protected BinderChanges getSharedByChanges(Long userId, String since, String descriptionFormatStr, Integer maxCount, String nextUrl,
                                               boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        ShareItemSelectSpec spec = getSharedBySpec(userId);
        return _getSharedChanges(since, descriptionFormatStr, maxCount, nextUrl, ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me",
                spec, null, onlyLibrary, showHidden, showUnhidden, true, false);
    }

    protected List<SearchableObject> getSharedWithChildren(Long userId, boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden,
                                                           boolean showPublic, boolean showNonPublic)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        if (replaceParent) {
            return _getSharedEntities(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", spec, null, onlyLibrary, showHidden,
                    showUnhidden, showPublic, showNonPublic, true, false, true);
        }
        return _getSharedEntities(null, null, spec, null, onlyLibrary, showHidden, showUnhidden, showPublic, showNonPublic, true, false, true);
    }

    protected SharedBinderBrief [] getSharedWithBinders(Long userId, boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden,
                                                        boolean showPublic, boolean showNonPublic)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        if (replaceParent) {
            return _getSharedBinders(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", spec, userId, onlyLibrary, showHidden, showUnhidden, showPublic, showNonPublic);
        }
        return _getSharedBinders(null, null, spec, userId, onlyLibrary, showHidden, showUnhidden, showPublic, showNonPublic);
    }

    protected SharedFileProperties [] getSharedWithFiles(Long userId, boolean onlyLibrary, boolean showHidden, boolean showUnhidden,
                                                         boolean showPublic, boolean showNonPublic)  {
        _getUser(userId);
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        return _getSharedFiles(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", spec, userId, onlyLibrary, showHidden, showUnhidden, showPublic, showNonPublic);
    }

    protected BinderChanges getSharedWithChanges(Long userId, String since, String descriptionFormatStr, Integer maxCount, String nextUrl,
                                             boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        ShareItemSelectSpec spec = getSharedWithSpec(userId);
        return _getSharedChanges(since, descriptionFormatStr, maxCount, nextUrl, ObjectKeys.SHARED_WITH_ME_ID,
                "/self/shared_with_me", spec, null, onlyLibrary, showHidden, showUnhidden, false, true);
    }

    protected List<SearchableObject> getPublicChildren(boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden)  {
        ShareItemSelectSpec spec = getSharedWithSpec(getLoggedInUserId());
        if (replaceParent) {
            return _getSharedEntities(ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", spec, null, onlyLibrary, showHidden, showUnhidden, true, false, true, false, true);
        }
        return _getSharedEntities(null, null, spec, null, onlyLibrary, showHidden, showUnhidden, true, false, true, false, true);
    }

    protected SharedBinderBrief [] getPublicBinders(boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden)  {
        ShareItemSelectSpec spec = getSharedWithSpec(getLoggedInUserId());
        if (replaceParent) {
            return _getSharedBinders(ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", spec, null, onlyLibrary, showHidden, showUnhidden, true, false);
        }
        return _getSharedBinders(null, null, spec, null, onlyLibrary, showHidden, showUnhidden, true, false);
    }

    protected SharedFileProperties [] getPublicFiles(boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        ShareItemSelectSpec spec = getSharedWithSpec(getLoggedInUserId());
        return _getSharedFiles(ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", spec, null, onlyLibrary, showHidden, showUnhidden, true, false);
    }

    protected BinderChanges getPublicChanges(String since, String descriptionFormatStr, Integer maxCount, String nextUrl,
                                             boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        ShareItemSelectSpec spec = getSharedWithSpec(getLoggedInUserId());
        return _getSharedChanges(since, descriptionFormatStr, maxCount, nextUrl, ObjectKeys.PUBLIC_SHARES_ID,
                "/self/public_shares", spec, null, onlyLibrary, showHidden, showUnhidden, true, false);
    }

    private BinderChanges _getSharedChanges(String since, String descriptionFormatStr, Integer maxCount, String nextUrl,
                                            Long topId, String topHref, ShareItemSelectSpec spec, Long excludedSharerId, boolean includeParentPaths,
                                            boolean showHidden, boolean showUnhidden, boolean showPublic, boolean showNonPublic) {
        BinderChanges changes;
        // Include deleted shares
        spec.deleted = null;
        List<Pair<DefinableEntity, List<ShareItem>>> binders = _getSharedItems(topId, spec, excludedSharerId, false, showHidden, showUnhidden, true, showPublic, showNonPublic, true, true);
        if (binders.size()>0) {
            List<Long> binderIds = new ArrayList<Long>();
            List<Long> entryIds = new ArrayList<Long>();
            for (Pair<DefinableEntity, List<ShareItem>> pair : binders) {
                EntityIdentifier id = pair.getB().get(0).getSharedEntityIdentifier();
                if (id.getEntityType().isBinder()) {
                    binderIds.add(id.getEntityId());
                } else if (id.getEntityType()== EntityIdentifier.EntityType.folderEntry ) {
                    entryIds.add(id.getEntityId());
                }
            }
            changes = super.getBinderChanges(binderIds.toArray(new Long[binderIds.size()]), entryIds.toArray(new Long[entryIds.size()]),
                    since, descriptionFormatStr, maxCount, nextUrl);
        } else {
            changes = new BinderChanges();
        }
        try {
            BinderChanges changes2 = _getShareChanges(dateFormat.parse(since), topId, spec, excludedSharerId,
                    includeParentPaths, showHidden, showUnhidden, showPublic, showNonPublic, true, Description.FORMAT_NONE);
            return ResourceUtil.mergeBinderChanges(changes, changes2, maxCount);
        } catch (ParseException e) {
            // Shouldn't happen.  super.getBinderChanges() will also try to parse the date and throw an expection if it's invalid.
            throw new InternalServerErrorException(ApiErrorCode.SERVER_ERROR, e.getMessage());
        }
    }

    private SearchResultList<SharedFolderEntryBrief> _getSharedEntries(Long topId, String topHref, ShareItemSelectSpec spec, Long excludedSharerId, boolean includeParentPaths,
                                                                       boolean showHidden, boolean showUnhidden, boolean showPublic, boolean showNonPublic) {
        boolean guestEnabled = isGuestAccessEnabled();
        Map<Long, SharedFolderEntryBrief> resultMap = new LinkedHashMap<Long, SharedFolderEntryBrief>();
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId, topId == ObjectKeys.SHARED_BY_ME_ID, showPublic, showNonPublic);
        for (ShareItem shareItem : shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                try {
                    FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                    if (showToUser(entry, topId, showHidden, showUnhidden, false)) {
                        SharedFolderEntryBrief binderBrief = resultMap.get(entry.getId());
                        if (binderBrief!=null) {
                            binderBrief.addShare(ResourceUtil.buildShare(shareItem, entry, buildShareRecipient(shareItem), guestEnabled));
                        } else {
                            binderBrief = ResourceUtil.buildSharedFolderEntryBrief(shareItem, buildShareRecipient(shareItem), entry, guestEnabled);
                            binderBrief.setParentBinder(new ParentBinder(topId, topHref));
                            resultMap.put(entry.getId(), binderBrief);
                        }
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

    protected SharedBinderBrief [] _getSharedBinders(Long topId, String topHref, ShareItemSelectSpec spec, Long excludedSharerId, boolean onlyLibrary,
                                                     boolean showHidden, boolean showUnhidden, boolean showPublic, boolean showNonPublic)  {
        List<SearchableObject> _results = _getSharedEntities(topId, topHref, spec, excludedSharerId, onlyLibrary, showHidden,
                showUnhidden, showPublic, showNonPublic, true, false, false);
        List<SharedBinderBrief> results = new ArrayList<SharedBinderBrief>();
        for (SearchableObject obj : _results) {
            if (obj instanceof SharedBinderBrief) {
                results.add((SharedBinderBrief) obj);
            }
        }
        return results.toArray(new SharedBinderBrief[results.size()]);
    }

    protected SharedFileProperties [] _getSharedFiles(Long topId, String topHref, ShareItemSelectSpec spec, Long excludedSharerId, boolean onlyLibrary,
                                                      boolean showHidden, boolean showUnhidden, boolean showPublic, boolean showNonPublic)  {
        List<SearchableObject> _results = _getSharedEntities(topId, topHref, spec, excludedSharerId, onlyLibrary, showHidden,
                showUnhidden, showPublic, showNonPublic, false, false, true);
        List<SharedFileProperties> results = new ArrayList<SharedFileProperties>();
        for (SearchableObject obj : _results) {
            if (obj instanceof SharedFileProperties) {
                results.add((SharedFileProperties) obj);
            }
        }
        return results.toArray(new SharedFileProperties[results.size()]);
    }

    protected BinderChanges _getShareChanges(Date since, Long topId, ShareItemSelectSpec spec, Long excludedSharerId, boolean onlyLibrary,
                                                   boolean showHidden, boolean showUnhidden, boolean showPublic, boolean showNonPublic, boolean preferFile,
                                                   int descriptionFormat) {

        List<Pair<DefinableEntity, List<ShareItem>>> pairs = _getSharedItems(topId, spec, excludedSharerId, onlyLibrary, showHidden, showUnhidden, showHidden, showPublic, showNonPublic, true, true);

        List<DefinableEntity> entities = new ArrayList<DefinableEntity>();
        Map<Long, List<ShareItem>> resultMap = new LinkedHashMap<Long, List<ShareItem>>();
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId, true, showPublic, showNonPublic);
        for (ShareItem shareItem : shareItems) {
            try {
                DefinableEntity entity = null;
                if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                    FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                    if (showToUser(entry, topId, showHidden, showUnhidden, true)) {
                        entity = entry;
                    }
                } else if (shareItem.getSharedEntityIdentifier().getEntityType().isBinder()) {
                    Binder binder = (Binder) getSharingModule().getSharedEntity(shareItem);
                    if (showBinderToUser(binder, onlyLibrary, topId, showHidden, showUnhidden, true)) {
                        entity = binder;
                    }
                }
                if (entity!=null) {
                    List<ShareItem> shareList = resultMap.get(entity.getId());
                    if (shareList==null) {
                        shareList = new ArrayList<ShareItem>();
                        resultMap.put(entity.getId(), shareList);
                        entities.add(entity);
                    }
                    shareList.add(shareItem);
                }
            } catch (AccessControlException e) {
                logger.warn("User " + getLoggedInUserId() + " does not have permission to read an entity that was shared with him/her: " + shareItem.getEntityTypedId());
            }
        }

        BinderChanges changes = new BinderChanges();
        List<BaseBinderChange> changeList = new ArrayList<BaseBinderChange>();
        for (Pair<DefinableEntity, List<ShareItem>> pair : pairs) {
            boolean isNew = resultMap.size()>0;
            boolean isDeleted = isNew;
            Date createDate = null;
            Date expireDate = null;
            for (ShareItem item : pair.getB()) {
                createDate = ResourceUtil.min(createDate, item.getStartDate());
                expireDate = ResourceUtil.min(expireDate, item.getExpiredDate());
                if (!item.createdSince(since)) {
                    isNew = false;
                }
                if (!item.expiredSince(since)) {
                    isDeleted = false;
                }
            }
            if ((isNew && !isDeleted) || (!isNew && isDeleted)) {
                DefinableEntity entity = pair.getA();
                BaseBinderChange change = ResourceUtil.buildBinderChange(entity.getEntityIdentifier(),
                        isNew ? org.kablink.teaming.domain.BinderChange.Action.add : org.kablink.teaming.domain.BinderChange.Action.delete,
                        ResourceUtil.max(createDate, expireDate), null,
                        entity, preferFile, descriptionFormat);
                changeList.add(change);
            }
        }

        Collections.sort(changeList, new Comparator<BaseBinderChange>() {
            @Override
            public int compare(BaseBinderChange o1, BaseBinderChange o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        changes.appendAll(changeList);
        if (changeList.size()>0) {
            changes.setLastModified(changeList.get(changeList.size()-1).getDate());
        }

        return changes;
    }

    protected List<SearchableObject> _getSharedEntities(Long topId, String topHref, ShareItemSelectSpec spec, Long excludedSharerId, boolean onlyLibrary,
                                                      boolean showHidden, boolean showUnhidden, boolean showPublic, boolean showNonPublic,
                                                      boolean folders, boolean entries, boolean files)  {
        boolean guestEnabled = isGuestAccessEnabled();

        List<Pair<DefinableEntity, List<ShareItem>>> resultList = _getSharedItems(topId, spec, excludedSharerId, onlyLibrary,
                showHidden, showUnhidden, false, showPublic, showNonPublic, folders, entries || files);

        List<SearchableObject> results = new ArrayList<SearchableObject>();
        for (Pair<DefinableEntity, List<ShareItem>> entityShares : resultList) {
            try {
                DefinableEntity entity = entityShares.getA();
                List<ShareItem> shares = entityShares.getB();
                if (entity instanceof FolderEntry) {
                    if (entries) {
                        SharedFolderEntryBrief entryBrief = null;
                        for (ShareItem shareItem : shares) {
                            if (entryBrief!=null) {
                                entryBrief.addShare(ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), guestEnabled));
                            } else {
                                entryBrief = ResourceUtil.buildSharedFolderEntryBrief(shareItem, buildShareRecipient(shareItem), (FolderEntry) entity, guestEnabled);
                                if (topId!=null) {
                                    entryBrief.setParentBinder(new ParentBinder(topId, topHref));
                                }
                                results.add(entryBrief);
                            }
                        }
                    }
                    if (files) {
                        Set<Attachment> attachments = entity.getAttachments();
                        for (Attachment attachment : attachments) {
                            if (attachment instanceof FileAttachment) {
                                SharedFileProperties fileProps = null;
                                for (ShareItem shareItem : shares) {
                                    if (fileProps!=null) {
                                        fileProps.addShare(ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), guestEnabled));
                                    } else {
                                        fileProps = ResourceUtil.buildSharedFileProperties(shareItem, buildShareRecipient(shareItem), (FileAttachment) attachment, guestEnabled);
                                        fileProps.setBinder(new ParentBinder(topId, topHref));
                                        results.add(fileProps);
                                    }
                                }
                            }
                        }
                    }
                } else if (entity instanceof Binder) {
                    SharedBinderBrief binderBrief = null;
                    for (ShareItem shareItem : shares) {
                        if (binderBrief!=null) {
                            binderBrief.addShare(ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), guestEnabled));
                        } else {
                            binderBrief = ResourceUtil.buildSharedBinderBrief(shareItem, buildShareRecipient(shareItem), (Binder) entity, guestEnabled);
                            if (topId!=null) {
                                binderBrief.setParentBinder(new ParentBinder(topId, topHref));
                            }
                            results.add(binderBrief);
                        }
                    }
                }
            } catch (AccessControlException e) {
                logger.warn("User " + getLoggedInUserId() + " does not have permission to read an entity that was shared with him/her: " + entityShares.getA().getEntityTypedId());
            }
        }
        Collections.sort(results, new Comparator<SearchableObject>() {
            @Override
            public int compare(SearchableObject o1, SearchableObject o2) {
                int result = o1.getDocType().compareTo(o2.getDocType());
                if (result==0) {
                    result = o1.getDisplayName().compareTo(o2.getDisplayName());
                }
                return result;
            }
        });
        return results;
    }

    protected List<Pair<DefinableEntity, List<ShareItem>>> _getSharedItems(Long topId, ShareItemSelectSpec spec, Long excludedSharerId, boolean onlyLibrary,
                                                      boolean showHidden, boolean showUnhidden, boolean showDeleted, boolean showPublic, boolean showNonPublic,
                                                      boolean folders, boolean entries)  {
        Map<Object, Pair<DefinableEntity, List<ShareItem>>> resultMap = new LinkedHashMap<Object, Pair<DefinableEntity, List<ShareItem>>>();

        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId, topId==ObjectKeys.SHARED_BY_ME_ID, showPublic, showNonPublic);
        for (ShareItem shareItem : shareItems) {
            try {
                if (entries && shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                    FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                    if (showToUser(entry, topId, showHidden, showUnhidden, showDeleted)) {
                        Pair<DefinableEntity, List<ShareItem>> pair = resultMap.get(entry.getId());
                        if (pair==null) {
                            pair = new Pair<DefinableEntity, List<ShareItem>>(entry, new ArrayList<ShareItem>());
                            resultMap.put(entry.getId(), pair);
                        }
                        pair.getB().add(shareItem);
                    }
                } else if (folders && shareItem.getSharedEntityIdentifier().getEntityType().isBinder()) {
                    Binder binder = (Binder) getSharingModule().getSharedEntity(shareItem);
                    if (showBinderToUser(binder, onlyLibrary, topId, showHidden, showUnhidden, showDeleted)) {
                        Pair<DefinableEntity, List<ShareItem>> pair = resultMap.get(binder.getId());
                        if (pair==null) {
                            pair = new Pair<DefinableEntity, List<ShareItem>>(binder, new ArrayList<ShareItem>());
                            resultMap.put(binder.getId(), pair);
                        }
                        pair.getB().add(shareItem);
                    }
                }
            } catch (AccessControlException e) {
                logger.warn("User " + getLoggedInUserId() + " does not have permission to read an entity that was shared with him/her: " + shareItem.getEntityTypedId());
            }
        }
        List<Pair<DefinableEntity, List<ShareItem>>> results = new ArrayList<Pair<DefinableEntity, List<ShareItem>>>();
        results.addAll(resultMap.values());
        return results;
    }

    private boolean showBinderToUser(Binder binder, boolean onlyLibrary, Long collectionId, boolean showHidden, boolean showUnhidden, boolean showDeleted) {
        if ((!showDeleted && isBinderPreDeleted(binder)) || (onlyLibrary && binder.getEntityType() != EntityIdentifier.EntityType.workspace && !binder.isLibrary())) {
            return false;
        }
        if (showHidden && showUnhidden) {
            return true;
        }
        if (!showHidden && !showUnhidden) {
            return false;
        }
        SearchResultList<Tag> entryTags = getBinderTags(binder, true);
        for (Tag tag : entryTags.getResults()) {
            if ((collectionId == ObjectKeys.SHARED_WITH_ME_ID && isHiddenInSharedWithMe(tag)) ||
                    (collectionId == ObjectKeys.SHARED_BY_ME_ID && isHiddenInSharedByMe(tag))) {
                return showHidden;
            }
        }
        return showUnhidden;
    }

    private boolean showToUser(FolderEntry entry, Long collectionId, boolean showHidden, boolean showUnhidden, boolean showDeleted) {
        if (!showDeleted && entry.isPreDeleted()) {
            return false;
        }
        if (showHidden && showUnhidden) {
            return true;
        }
        if (!showHidden && !showUnhidden) {
            return false;
        }
        SearchResultList<Tag> entryTags = getEntryTags(entry, true);
        for (Tag tag : entryTags.getResults()) {
            if ((collectionId == ObjectKeys.SHARED_WITH_ME_ID && isHiddenInSharedWithMe(tag)) ||
                    (collectionId == ObjectKeys.SHARED_BY_ME_ID && isHiddenInSharedByMe(tag))) {
                return showHidden;
            }
        }
        return showUnhidden;
    }

    protected BinderTree getSubBinderTree(Long topId, String topHref, SharedBinderBrief [] sharedBinders, Criterion filter, int descriptionFormat) {
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
            SearchResultBuilderUtil.buildSearchResultsTree(results, sharedBinders, new BinderBriefBuilder(descriptionFormat), resultMap);
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
                criterion.add(SearchUtils.buildFileNameCriterion(fileName));
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

    private SearchResultList<SearchableObject> _getLibraryEntities(Long topId, Long excludedSharerId, boolean recursive,
                                                                   boolean includeBinders, boolean includeFolderEntries,
                                                                   boolean includeFiles, boolean includeReplies,
                                                                   boolean includeParentPaths, String keyword,
                                                                   int descriptionFormat, Integer offset,
                                                                   Integer maxCount, String nextUrl,
                                                                   ShareItemSelectSpec spec, boolean showHidden, boolean showUnhidden, boolean showDeleted,
                                                                   boolean showPublic, boolean showNonPublic) {
        List<ShareItem> shareItems = getShareItems(spec, excludedSharerId, topId==ObjectKeys.SHARED_BY_ME_ID, showPublic, showNonPublic);
        SearchResultList<SearchableObject> results;
        if (shareItems.size()>0) {
            Junction searchContext = Restrictions.disjunction();
            for (ShareItem shareItem : shareItems) {
                Junction shareCrit = Restrictions.conjunction();
                EntityIdentifier entityId = shareItem.getSharedEntityIdentifier();
                if (entityId.getEntityType()==EntityIdentifier.EntityType.folderEntry) {
                    FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                    if (showToUser(entry, topId, showHidden, showUnhidden, showDeleted)) {
                        shareCrit.add(Restrictions.disjunction()
                                .add(SearchUtils.buildEntryCriterion(entityId.getEntityId()))
                                .add(SearchUtils.buildAttachmentCriterion(entityId.getEntityId()))
                        );
                    }
                } else if (entityId.getEntityType()==EntityIdentifier.EntityType.folder ||
                        entityId.getEntityType()==EntityIdentifier.EntityType.workspace) {
                    Binder binder = (Binder) getSharingModule().getSharedEntity(shareItem);
                    if (showBinderToUser(binder, false, topId, showHidden, showUnhidden, showDeleted)) {
                        if (recursive) {
                            shareCrit.add(SearchUtils.buildSearchBinderCriterion(entityId.getEntityId(), true));
                        } else {
                            shareCrit.add(SearchUtils.buildBinderCriterion(entityId.getEntityId()));
                        }
                    }
                }
                searchContext.add(shareCrit);
            }
            results = searchForLibraryEntities(keyword, searchContext, recursive, offset, maxCount, includeBinders,
                    includeFolderEntries, includeReplies, includeFiles, includeParentPaths, descriptionFormat, nextUrl);
        } else {
            results = new SearchResultList<SearchableObject>();
        }
        return results;
    }

    private List<ShareItem> getAllPublicShareParts(ShareItem item) {
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setSharerId(item.getSharerId());
        spec.setLatest(true);
        spec.setSharedEntityIdentifier(item.getSharedEntityIdentifier());
        List<ShareItem> allShareItems = getShareItems(spec, null, false, true, false, false);
        if (allShareItems.size()==0) {
            throw new IllegalStateException("Could not find public shares corresponding to share with id: " + item.getId());
        }
        return allShareItems;
    }

    private ShareItem _getShareItem(Long id) {
        ShareItem share = getSharingModule().getShareItem(id);
        if (share.isDeleted() || !share.isLatest() || !share.getSharerId().equals(getLoggedInUserId())) {
            // Don't allow the user to modify a share that is not the latest version of the share, or that was shared
            // by someone else.
            throw new NoShareItemByTheIdException(id);
        }
        return share;
    }
}
