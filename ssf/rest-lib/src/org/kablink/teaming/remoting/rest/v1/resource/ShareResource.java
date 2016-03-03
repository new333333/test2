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
import com.webcohesion.enunciate.metadata.rs.ResourceGroup;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.remoting.rest.v1.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.FilePropertiesBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.BaseBinderChange;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderChange;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.EntityId;
import org.kablink.teaming.rest.v1.model.FileChange;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FolderEntryChange;
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
@ResourceGroup("Sharing")
public class ShareResource extends AbstractResource {
    /**
     * Notify share recipients without modifying the shares.
     * <p>For <code>public_link</code> share recipients, the <code>notify_address</code> form parameter can be used to specify
     * email addresses to notify of the public link.</p>
     * @param ids   ID of a share.  May be specified multiple times.
     * @param notifyRecipient   If true, the recipients will be notified.
     * @param notifyAddresses  An email address to send a public link to.  May be specified multiple times.
     * @return
     */
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

    /**
     * Get the specified Share object.
     * @param id    The ID of the share.
     * @return  A Share object.
     */
    @GET
    @Path("/{id}")
    public Share getShare(@PathParam("id") Long id) {
        ShareItem share = _getShareItem(id);
        DefinableEntity definableEntity = findDefinableEntity(share.getSharedEntityIdentifier());
        if (definableEntity!=null) {
            throw new NoShareItemByTheIdException(id);
        }
        return ResourceUtil.buildShare(share, definableEntity, buildShareRecipient(share), isGuestAccessEnabled());
    }

    /**
     * Overwrite the specified share.  This will not modify the Share's shared entity or recipient.
     *
     * <p>The share will be assigned a new ID.  The returned Share object contains the new ID.</p>
     * @param id    The ID of the share to overwrite.
     * @param notifyRecipient   If true, the recipient will be notified by email.
     * @param notifyAddresses   An email address to notify, if the recipient type is <code>public_link</code>.  May be specified multiple times.
     * @param share     A Share object containing the new share settings.
     * @return  The update SHare object.
     */
    @POST
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Share updateShare(@PathParam("id") Long id,
                             @QueryParam("notify") @DefaultValue("false") boolean notifyRecipient,
                             @QueryParam("notify_address") Set<String> notifyAddresses,
                             Share share) {
        List<Pair<ShareItem, DefinableEntity>> origItems;
        ShareItem item = _getShareItem(id);
        if (item.getIsPartOfPublicShare()) {
            origItems = getAllPublicShareParts(item);
            notifyRecipient = false;
        } else {
            origItems = new ArrayList<Pair<ShareItem, DefinableEntity>>(1);
            origItems.add(new Pair<ShareItem, DefinableEntity>(item, null));
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
        for (Pair<ShareItem, DefinableEntity> pair : origItems) {
            ShareItem origItem = pair.getA();
            shareItem = new ShareItem(newShareItem);
            shareItem.setRecipientType(origItem.getRecipientType());
            shareItem.setRecipientId(origItem.getRecipientId());
            shareItem = getSharingModule().modifyShareItem(shareItem, origItem.getId());
        }
        EntityIdentifier sharedEntityId = shareItem.getSharedEntityIdentifier();
        DefinableEntity entity = findDefinableEntity(sharedEntityId);
        if (entity==null) {
            if (sharedEntityId.getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                throw new NoFolderEntryByTheIdException(sharedEntityId.getEntityId());
            } else if (sharedEntityId.getEntityType().isBinder()) {
                throw new NoBinderByTheIdException(sharedEntityId.getEntityId());
            }
            throw new NotFoundException(ApiErrorCode.BAD_INPUT, "The shared entity could not be found.");
        }
        if (notifyRecipient) {
            notifyShareRecipients(shareItem, entity, false, notifyAddresses);
        }
        return ResourceUtil.buildShare(shareItem, entity, buildShareRecipient(shareItem), isGuestAccessEnabled());
    }

    /**
     * Delete the specified Share.
     * @param id    The ID of the share.
     */
    @DELETE
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteShare(@PathParam("id") Long id) {
        List<Pair<ShareItem, DefinableEntity>> origItems;
        ShareItem item = _getShareItem(id);
        if (item.getIsPartOfPublicShare()) {
            origItems = getAllPublicShareParts(item);
        } else {
            origItems = new ArrayList<Pair<ShareItem, DefinableEntity>>(1);
            origItems.add(new Pair<ShareItem, DefinableEntity>(item, null));
        }
        for (Pair<ShareItem, DefinableEntity> origItem : origItems) {
            getSharingModule().deleteShareItem(origItem.getA().getId());
        }
    }

    /**
     * Get the shares where the specified user is the sharer.
     * @param userId    The ID of the user
     * @return A SearchResultList of Share objects.
     */
    @GET
    @Path("/by_user/{id}")
    public SearchResultList<Share> getSharedByUser(@PathParam("id") Long userId) {
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, Boolean.FALSE);
        for (Pair<ShareItem, DefinableEntity> pair : shareItems) {
            results.append(ResourceUtil.buildShare(pair.getA(), getSharedEntity(pair.getA(), true),
                    buildShareRecipient(pair.getA()), isGuestAccessEnabled()));
        }
        return results;
    }

    @GET
    @Path("/by_user/{id}/binders")
    @Undocumented
    public SearchResultList<SharedBinderBrief> getBindersSharedByUser(@PathParam("id") Long userId,
                                                                      @QueryParam("title") String name,
                                                                      @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                      @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, Boolean.FALSE);
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedByBinders(shareItems, name, false, true, showHidden, showUnhidden));
        return results;
    }

    @GET
    @Path("/by_user/{id}/binder_tree")
    @Undocumented
    public BinderTree getSharedByUserBinderTree(@PathParam("id") Long userId,
                                                @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, Boolean.FALSE);
        SharedBinderBrief [] sharedBinders = getSharedByBinders(shareItems, null, false, false, showHidden, showUnhidden);
        return getSubBinderTree(ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", sharedBinders, null, toDomainFormat(descriptionFormatStr));
    }

    /**
     * List the files and folders shared by the specified user..
     *
     * <p>The <code>title</code> query parameter limits the results to those children with the specified name.  Wildcards are not supported.</p>
     *
     * @param userId    The ID of the user.
     * @param name  The name of the child to return,
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @return  A SearchResultList of SearchableObjects (SharedBinderBriefs and SharedFileProperties).
     */
    @GET
    @Path("/by_user/{id}/library_children")
    public Response getLibraryChildrenSharedByUser(@PathParam("id") Long userId,
                                                   @QueryParam("title") String name,
                                                   @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                   @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                   @Context HttpServletRequest request) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>();
        results.appendAll(getSharedByChildren(shareItems, name, true, true, showHidden, showUnhidden));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    /**
     * List the folders shared by the specified user.
     *
     * <p>The <code>title</code> query parameter limits the results to those folders with the specified name.  Wildcards are not supported.</p>
     *
     * @param userId    The ID of the user.
     * @param name  The name of the child to return,
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @return  A SearchResultList of SearchableObjects (SharedBinderBriefs and SharedFileProperties).
     */
    @GET
    @Path("/by_user/{id}/library_folders")
    public Response getLibraryFoldersSharedByUser(@PathParam("id") Long userId,
                                                  @QueryParam("title") String name,
                                                  @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                  @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                  @Context HttpServletRequest request) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedByBinders(shareItems, name, true, true, showHidden, showUnhidden));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/by_user/{id}/entries")
    @Undocumented
    public SearchResultList<SharedFolderEntryBrief> getEntriesSharedByUser(@PathParam("id") Long userId,
                                                                           @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                           @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                           @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, Boolean.FALSE);
        return _getSharedEntries(shareItems, ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", includeParentPaths, showHidden, showUnhidden);
    }

    @GET
    @Path("/by_user/{id}/files")
    @Undocumented
    public Response getFilesSharedByUser(@PathParam("id") Long userId,
                                                                 @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                 @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                 @QueryParam("file_name") String fileName,
                                                                 @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                 @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                 @Context HttpServletRequest request) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, recursive);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedByFiles(shareItems, false, showHidden, showUnhidden));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedByBinders(shareItems, null, false, false, showHidden, showUnhidden), fileName, false));
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

    /**
     * List the files shared by the specified user.
     *
     * @param userId    The ID of the user.
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @param fileName The name of the child to return,
     * @param recursive Whether to search the binder and sub-binders for files.
     * @param includeParentPaths    If true, the path of the parent binder is included in each result.
     * @return  A SearchResultList of SearchableObjects (SharedBinderBriefs and SharedFileProperties).
     */
    @GET
    @Path("/by_user/{id}/library_files")
    public Response getLibraryFilesSharedByUser(@PathParam("id") Long userId,
                                                                        @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                        @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                        @QueryParam("file_name") String fileName,
                                                                        @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                        @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                        @Context HttpServletRequest request) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, recursive);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedByFiles(shareItems, true, showHidden, showUnhidden));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedByBinders(shareItems, null, true, false, showHidden, showUnhidden), fileName, false));
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
    @Path("/by_user/{id}/library_changes")
    public BinderChanges getSharedByUserLibraryChanges(@PathParam("id") Long userId,
                                                       @QueryParam("since") String since,
                                                       @QueryParam("recursive") @DefaultValue("true") boolean recursive,
                                                       @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                       @QueryParam("count") @DefaultValue("500") Integer maxCount,
                                                       @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                       @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedByShareItems(userId, null);
        return getSharedByChanges(shareItems, since, recursive, descriptionFormatStr, maxCount, "/public/library_changes", true, showHidden, showUnhidden);
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

    /**
     * Get the shares where the specified user is the recipient.
     * @param userId    The ID of the user
     * @return A SearchResultList of Share objects.
     */
    @GET
    @Path("/with_user/{id}")
    public SearchResultList<Share> getSharedWithUser(@PathParam("id") Long userId) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, Boolean.FALSE);
        SearchResultList<Share> results = new SearchResultList<Share>();
        for (Pair<ShareItem, DefinableEntity> pair : shareItems) {
            ShareItem shareItem = pair.getA();
            DefinableEntity definableEntity = findDefinableEntity(shareItem.getSharedEntityIdentifier());
            if (definableEntity!=null) {
                results.append(ResourceUtil.buildShare(shareItem, definableEntity,
                        buildShareRecipient(shareItem), isGuestAccessEnabled()));
            }
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/binders")
    @Undocumented
    public SearchResultList<SharedBinderBrief> getBindersSharedWithUser(@PathParam("id") Long userId,
                                                                        @QueryParam("title") String name,
                                                                        @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                        @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, Boolean.FALSE);
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedWithBinders(shareItems, name, false, true, showHidden, showUnhidden));
        return results;
    }

    @GET
    @Path("/with_user/{id}/binder_tree")
    @Undocumented
    public BinderTree getSharedWithUserBinderTree(@PathParam("id") Long userId,
                                                  @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                  @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, Boolean.FALSE);
        SharedBinderBrief [] sharedBinders = getSharedWithBinders(shareItems, null, false, false, showHidden, showUnhidden);
        return getSubBinderTree(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", sharedBinders, null, toDomainFormat(descriptionFormatStr));
    }

    /**
     * List the files and folders shared with the specified user.
     *
     * <p>The <code>title</code> query parameter limits the results to those children with the specified name.  Wildcards are not supported.</p>
     *
     * @param userId    The ID of the user.
     * @param name  The name of the child to return,
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @return  A SearchResultList of SearchableObjects (BinderBriefs and FileProperties).
     */
    @GET
    @Path("/with_user/{id}/library_children")
    public Response getLibraryChildrenSharedWithUser(@PathParam("id") Long userId,
                                                     @QueryParam("title") String name,
                                                     @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                     @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                     @Context HttpServletRequest request) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>();
        results.appendAll(getSharedWithChildren(shareItems, name, true, true, showHidden, showUnhidden));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    /**
     * List the folders shared with the specified user.
     *
     * <p>The <code>title</code> query parameter limits the results to those folders with the specified name.  Wildcards are not supported.</p>
     *
     * @param userId    The ID of the user.
     * @param name  The name of the child to return,
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @return  A SearchResultList of SearchableObjects (SharedBinderBriefs and SharedFileProperties).
     */
    @GET
    @Path("/with_user/{id}/library_folders")
    public Response getLibraryFoldersSharedWithUser(@PathParam("id") Long userId,
                                                    @QueryParam("title") String name,
                                                    @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                    @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                    @Context HttpServletRequest request) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedWithBinders(shareItems, name, true, true, showHidden, showUnhidden));
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
    @Undocumented
    public SearchResultList<SharedFolderEntryBrief> getEntriesSharedWithUser(@PathParam("id") Long userId,
                                                                             @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                             @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                             @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, Boolean.FALSE);
        return _getSharedEntries(shareItems, ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", includeParentPaths, showHidden, showUnhidden);
    }

    @GET
    @Path("/with_user/{id}/files")
    @Undocumented
    public Response getFilesSharedWithUser(@PathParam("id") Long userId,
                                                                   @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                   @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                   @QueryParam("file_name") String fileName,
                                                                   @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                   @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                   @Context HttpServletRequest request) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedWithFiles(shareItems, false, showHidden, showUnhidden));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedWithBinders(shareItems, null, false, false, showHidden, showUnhidden),
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

    /**
     * List the files shared with the specified user.
     *
     * @param userId    The ID of the user.
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @param fileName The name of the child to return,
     * @param recursive Whether to search the binder and sub-binders for files.
     * @param includeParentPaths    If true, the path of the parent binder is included in each result.
     * @return  A SearchResultList of SearchableObjects (SharedBinderBriefs and SharedFileProperties).
     */
    @GET
    @Path("/with_user/{id}/library_files")
    public Response getLibraryFilesSharedWithUser(@PathParam("id") Long userId,
                                                                          @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                          @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                          @QueryParam("file_name") String fileName,
                                                                          @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                          @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                          @Context HttpServletRequest request) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedWithFiles(shareItems, true, showHidden, showUnhidden));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedWithBinders(shareItems, null, true, false, showHidden, showUnhidden), fileName, true));
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
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, Boolean.FALSE);
        SharedBinderBrief [] sharedBinders = getSharedWithBinders(shareItems, null, true, false, showHidden, showUnhidden);
        return getSubBinderTree(ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", sharedBinders,
                SearchUtils.buildLibraryTreeCriterion(), toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/with_user/{id}/library_changes")
    public BinderChanges getSharedWithUserLibraryChanges(@PathParam("id") Long userId,
                                                         @QueryParam("since") String since,
                                                         @QueryParam("recursive") @DefaultValue("true") boolean recursive,
                                                         @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                         @QueryParam ("count") @DefaultValue("500") Integer maxCount,
                                                         @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                         @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getSharedWithShareItems(userId, null);
        return getSharedWithChanges(shareItems, since, recursive, descriptionFormatStr, maxCount, "/public/library_changes", true, showHidden, showUnhidden);
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

    /**
     * Get the shares where the recipient is public.
     * @return A SearchResultList of Share objects.
     */
    @GET
    @Path("/public")
    public SearchResultList<Share> getPublicShares() {
        ShareItemSelectSpec spec = getPublicSpec();
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<Pair<ShareItem, DefinableEntity>> shareItems = getShareItems(spec, null, false, true, false);
        for (Pair<ShareItem, DefinableEntity> pair : shareItems) {
            ShareItem shareItem = pair.getA();
            DefinableEntity definableEntity = findDefinableEntity(shareItem.getSharedEntityIdentifier());
            if (definableEntity!=null) {
                results.append(ResourceUtil.buildShare(shareItem, definableEntity,
                        buildShareRecipient(shareItem), isGuestAccessEnabled()));
            }
        }
        return results;
    }

    @GET
    @Path("/public/binders")
    @Undocumented
    public SearchResultList<SharedBinderBrief> getPublicSharesBinders(
            @QueryParam("title") String name,
            @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                      @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(Boolean.FALSE);
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getPublicBinders(shareItems, name, false, true, showHidden, showUnhidden));
        return results;
    }

    @GET
    @Path("/public/binder_tree")
    @Undocumented
    public BinderTree getPublicSharesBinderTree(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(Boolean.FALSE);
        SharedBinderBrief [] sharedBinders = getPublicBinders(shareItems, null, false, false, showHidden, showUnhidden);
        return getSubBinderTree(ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", sharedBinders, null, toDomainFormat(descriptionFormatStr));
    }

    @GET
    @Path("/public/library_tree")
    public BinderTree getPublicSharesLibraryTree(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(Boolean.FALSE);
        SharedBinderBrief [] sharedBinders = getPublicBinders(shareItems, null, true, false, showHidden, showUnhidden);
        return getSubBinderTree(ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", sharedBinders, null, toDomainFormat(descriptionFormatStr));
    }

    /**
     * List the files and folders shared publicly.
     *
     * <p>The <code>title</code> query parameter limits the results to those children with the specified name.  Wildcards are not supported.</p>
     *
     * @param name  The name of the child to return,
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @return  A SearchResultList of SearchableObjects (SharedBinderBriefs and SharedFileProperties).
     */
    @GET
    @Path("/public/library_children")
    public Response getPublicSharesLibraryChildren(@QueryParam("title") String name,
                                                   @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                   @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                   @Context HttpServletRequest request) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SearchableObject> results = new SearchResultList<SearchableObject>();
        results.appendAll(getPublicChildren(shareItems, name, true, true, showHidden, showUnhidden));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    /**
     * List the folders shared publically.
     *
     * <p>The <code>title</code> query parameter limits the results to those folders with the specified name.  Wildcards are not supported.</p>
     *
     * @param name  The name of the child to return,
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @return  A SearchResultList of SearchableObjects (SharedBinderBriefs and SharedFileProperties).
     */
    @GET
    @Path("/public/library_folders")
    public Response getPublicSharesLibraryFolders(
            @QueryParam("title") String name,
            @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
            @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
            @Context HttpServletRequest request) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getPublicBinders(shareItems, name, true, true, showHidden, showUnhidden));
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @GET
    @Path("/public/entries")
    @Undocumented
    public SearchResultList<SharedFolderEntryBrief> getPublicSharesEntries(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                           @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                           @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(Boolean.FALSE);
        return _getSharedEntries(shareItems, ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", includeParentPaths, showHidden, showUnhidden);
    }

    @GET
    @Path("/public/files")
    @Undocumented
    public Response getPublicSharesFiles(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                 @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                 @QueryParam("file_name") String fileName,
                                                                 @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                 @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                 @Context HttpServletRequest request) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getPublicFiles(shareItems, false, showHidden, showUnhidden));
        if (recursive) {
            results.appendAll(getSubFiles(getPublicBinders(shareItems, null, false, false, showHidden, showUnhidden), fileName, false));
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

    /**
     * List the files shared publically.
     *
     * @param showHidden Whether to include hidden shares in the results.
     * @param showUnhidden Whether to include unhidden, or visible, shares in the results.
     * @param fileName The name of the child to return,
     * @param recursive Whether to search the binder and sub-binders for files.
     * @param includeParentPaths    If true, the path of the parent binder is included in each result.
     * @return  A SearchResultList of SearchableObjects (SharedBinderBriefs and SharedFileProperties).
     */
    @GET
    @Path("/public/library_files")
    public Response getPublicSharesLibraryFiles(@QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                                 @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden,
                                                                 @QueryParam("file_name") String fileName,
                                                                 @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                                 @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                 @Context HttpServletRequest request) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(null);
        Date lastModified = getSharesLibraryModifiedDate(shareItems, false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getPublicFiles(shareItems, true, showHidden, showUnhidden));
        if (recursive) {
            results.appendAll(getSubFiles(getPublicBinders(shareItems, null, true, false, showHidden, showUnhidden), fileName, false));
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
            throw new AccessControlException();
        }
        ShareItemSelectSpec spec = getPublicSpec();
        SearchResultList<SearchableObject> results = _getLibraryEntities(ObjectKeys.PUBLIC_SHARES_ID, null, recursive,
                includeBinders, includeFolderEntries, includeFiles, includeReplies, includeParentPaths, keyword,
                toDomainFormat(descriptionFormatStr), offset, maxCount, "/shares/public/library_entities", spec,
                showHidden, showUnhidden, false, true, false);
        return results;
    }

    @GET
    @Path("/public/library_changes")
    public BinderChanges getPublicSharesLibraryChanges(@QueryParam("since") String since,
                                                       @QueryParam("recursive") @DefaultValue("true") boolean recursive,
                                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                  @QueryParam ("count") @DefaultValue("500") Integer maxCount,
                                                  @QueryParam("hidden") @DefaultValue("false") boolean showHidden,
                                                  @QueryParam("unhidden") @DefaultValue("true") boolean showUnhidden) {
        if (!getEffectivePublicCollectionSetting(getLoggedInUser())) {
            throw new AccessControlException();
        }
        List<Pair<ShareItem, DefinableEntity>> shareItems = getPublicShareItems(null);
        return getPublicChanges(shareItems, since, recursive, descriptionFormatStr, maxCount, "/public/library_changes", true, showHidden, showUnhidden);
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
            throw new AccessControlException();
        }
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("description_format", descriptionFormatStr);

        ShareItemSelectSpec spec = getPublicSpec();
        return _getRecentActivity(ObjectKeys.PUBLIC_SHARES_ID, includeParentPaths, toDomainFormat(descriptionFormatStr), offset, maxCount, spec,
                null, "/shares/public/recent_activity", nextParams, showHidden, showUnhidden, true, false);
    }

    private SearchResultList<RecentActivityEntry> _getRecentActivity(Long topId, boolean includeParentPaths, int descriptionFormat,
                                                                  Integer offset, Integer maxCount, ShareItemSelectSpec spec,
                                                                  Long excludedSharerId, String nextUrl, Map<String, Object> nextParams,
                                                                  boolean showHidden, boolean showUnhidden, boolean showPublic, boolean showNonPublic) {
        List<String> binderIds = new ArrayList<String>();
        List<String> entryIds = new ArrayList<String>();
        List<Pair<ShareItem, DefinableEntity>> shareItems = getShareItems(spec, excludedSharerId, topId==ObjectKeys.SHARED_BY_ME_ID, showPublic, showNonPublic);
        if (shareItems.size()==0) {
            return new SearchResultList<RecentActivityEntry>();
        }
        for (Pair<ShareItem, DefinableEntity> pair : shareItems) {
            DefinableEntity entity = getDefinableEntity(pair, true);
            if (entity!=null) {
                if (entity.getEntityType()== EntityIdentifier.EntityType.folderEntry &&
                        showEntryToUser((FolderEntry) entity, topId, null, showHidden, showUnhidden, false)) {
                    entryIds.add(entity.getId().toString());
                } else if (entity.getEntityType().isBinder() && showBinderToUser((Binder) entity, null, false, topId, showHidden, showUnhidden, false)) {
                    binderIds.add(entity.getId().toString());
                }
            }
        }
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binderIds, entryIds, null, true, Constants.LASTACTIVITY_FIELD);
        return _getRecentActivity(includeParentPaths, descriptionFormat, offset, maxCount, criteria, nextUrl, nextParams);
    }

    protected SharedBinderBrief [] getSharedByBinders(List<Pair<ShareItem, DefinableEntity>> shareItems, String name, boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden)  {
        if (replaceParent) {
            return _getSharedBinders(shareItems, ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", name, onlyLibrary, showHidden, showUnhidden, false);
        }
        return _getSharedBinders(shareItems, null, null, name, onlyLibrary, showHidden, showUnhidden, false);
    }

    protected SharedFileProperties[] getSharedByFiles(List<Pair<ShareItem, DefinableEntity>> shareItems, boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        return _getSharedFiles(shareItems, ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me", onlyLibrary, showHidden, showUnhidden, false);
    }

    protected BinderChanges getSharedByChanges(List<Pair<ShareItem, DefinableEntity>> shareItems, String since, boolean recursive, String descriptionFormatStr, Integer maxCount, String nextUrl,
                                               boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        return _getSharedChanges(shareItems, since, recursive, descriptionFormatStr, maxCount, nextUrl, ObjectKeys.SHARED_BY_ME_ID, "/self/shared_by_me",
                onlyLibrary, showHidden, showUnhidden);
    }

    protected SharedBinderBrief [] getSharedWithBinders(List<Pair<ShareItem, DefinableEntity>> shareItems, String name, boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden)  {
        if (replaceParent) {
            return _getSharedBinders(shareItems, ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", name, onlyLibrary, showHidden, showUnhidden, true);
        }
        return _getSharedBinders(shareItems, null, null, name, onlyLibrary, showHidden, showUnhidden, true);
    }

    protected SharedFileProperties [] getSharedWithFiles(List<Pair<ShareItem, DefinableEntity>> shareItems, boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        return _getSharedFiles(shareItems, ObjectKeys.SHARED_WITH_ME_ID, "/self/shared_with_me", onlyLibrary, showHidden, showUnhidden, true);
    }

    protected BinderChanges getSharedWithChanges(List<Pair<ShareItem, DefinableEntity>> shareItems, String since, boolean recursive, String descriptionFormatStr, Integer maxCount, String nextUrl,
                                             boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        return _getSharedChanges(shareItems, since, recursive, descriptionFormatStr, maxCount, nextUrl, ObjectKeys.SHARED_WITH_ME_ID,
                "/self/shared_with_me", onlyLibrary, showHidden, showUnhidden);
    }

    protected SharedBinderBrief [] getPublicBinders(List<Pair<ShareItem, DefinableEntity>> shareItems, String name, boolean onlyLibrary, boolean replaceParent, boolean showHidden, boolean showUnhidden)  {
        if (replaceParent) {
            return _getSharedBinders(shareItems, ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", name, onlyLibrary, showHidden, showUnhidden, true);
        }
        return _getSharedBinders(shareItems, null, null, name, onlyLibrary, showHidden, showUnhidden, true);
    }

    protected SharedFileProperties [] getPublicFiles(List<Pair<ShareItem, DefinableEntity>> shareItems, boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        return _getSharedFiles(shareItems, ObjectKeys.PUBLIC_SHARES_ID, "/self/public_shares", onlyLibrary, showHidden, showUnhidden, true);
    }

    protected BinderChanges getPublicChanges(List<Pair<ShareItem, DefinableEntity>> shareItems, String since, boolean recursive, String descriptionFormatStr, Integer maxCount, String nextUrl,
                                             boolean onlyLibrary, boolean showHidden, boolean showUnhidden)  {
        return _getSharedChanges(shareItems, since, recursive, descriptionFormatStr, maxCount, nextUrl, ObjectKeys.PUBLIC_SHARES_ID,
                "/self/public_shares", onlyLibrary, showHidden, showUnhidden);
    }

    private BinderChanges _getSharedChanges(List<Pair<ShareItem, DefinableEntity>> shareItems, String since, boolean recursive,
                                            String descriptionFormatStr, Integer maxCount, String nextUrl, Long topId, String topHref,
                                            boolean includeParentPaths, boolean showHidden, boolean showUnhidden) {
        BinderChanges changes;
        // Include deleted shares
        List<Pair<DefinableEntity, List<ShareItem>>> sharedItems = _getSharedItems(shareItems, topId, null, false,
                showHidden, showUnhidden, topId==ObjectKeys.SHARED_BY_ME_ID, true, recursive, true);
        if (sharedItems.size()>0) {
            List<Long> binderIds = new ArrayList<Long>();
            List<Long> entryIds = new ArrayList<Long>();
            for (Pair<DefinableEntity, List<ShareItem>> pair : sharedItems) {
                EntityIdentifier id = pair.getB().get(0).getSharedEntityIdentifier();
                if (id.getEntityType().isBinder()) {
                    binderIds.add(id.getEntityId());
                } else if (id.getEntityType()== EntityIdentifier.EntityType.folderEntry ) {
                    entryIds.add(id.getEntityId());
                }
            }
            changes = super.getBinderChanges(binderIds.toArray(new Long[binderIds.size()]), entryIds.toArray(new Long[entryIds.size()]),
                    since, recursive, descriptionFormatStr, maxCount, nextUrl);
        } else {
            changes = new BinderChanges();
        }
        try {
            BinderChanges changes2 = _getShareChanges(sharedItems, dateFormat.parse(since), true, Description.FORMAT_NONE);
            changes = ResourceUtil.mergeBinderChanges(changes, changes2, maxCount);
            setParents(changes, sharedItems, new ParentBinder(topId, topHref));

            if (includeParentPaths) {
                populateParentBinderPaths(changes);
            }

            return changes;
        } catch (ParseException e) {
            // Shouldn't happen.  super.getBinderChanges() will also try to parse the date and throw an expection if it's invalid.
            throw new InternalServerErrorException(ApiErrorCode.SERVER_ERROR, e.getMessage());
        }
    }

    private SearchResultList<SharedFolderEntryBrief> _getSharedEntries(List<Pair<ShareItem, DefinableEntity>> shareItems, Long topId, String topHref, boolean includeParentPaths,
                                                                       boolean showHidden, boolean showUnhidden) {
        boolean guestEnabled = isGuestAccessEnabled();
        Map<Long, SharedFolderEntryBrief> resultMap = new LinkedHashMap<Long, SharedFolderEntryBrief>();
        for (Pair<ShareItem, DefinableEntity> pair : shareItems) {
            ShareItem shareItem = pair.getA();
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                try {
                    FolderEntry entry = (FolderEntry) getDefinableEntity(pair, true);
                    if (showEntryToUser(entry, topId, null, showHidden, showUnhidden, false)) {
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

    protected SharedBinderBrief [] _getSharedBinders(List<Pair<ShareItem, DefinableEntity>> shareItems, Long topId, String topHref,
                                                     String name, boolean onlyLibrary,
                                                     boolean showHidden, boolean showUnhidden, boolean resolveDuplicateNames)  {
        List<SearchableObject> _results = _getSharedEntities(shareItems, topId, topHref, name, onlyLibrary, showHidden,
                showUnhidden, resolveDuplicateNames, true, false, false);
        List<SharedBinderBrief> results = new ArrayList<SharedBinderBrief>();
        for (SearchableObject obj : _results) {
            if (obj instanceof SharedBinderBrief) {
                results.add((SharedBinderBrief) obj);
            }
        }
        return results.toArray(new SharedBinderBrief[results.size()]);
    }

    protected SharedFileProperties [] _getSharedFiles(List<Pair<ShareItem, DefinableEntity>> shareItems, Long topId, String topHref, boolean onlyLibrary,
                                                      boolean showHidden, boolean showUnhidden, boolean resolveDuplicateNames)  {
        List<SearchableObject> _results = _getSharedEntities(shareItems, topId, topHref, null, onlyLibrary, showHidden,
                showUnhidden, resolveDuplicateNames, false, false, true);
        List<SharedFileProperties> results = new ArrayList<SharedFileProperties>();
        for (SearchableObject obj : _results) {
            if (obj instanceof SharedFileProperties) {
                results.add((SharedFileProperties) obj);
            }
        }
        return results.toArray(new SharedFileProperties[results.size()]);
    }

    protected BinderChanges _getShareChanges(List<Pair<DefinableEntity, List<ShareItem>>> pairs, Date since,
                                             boolean preferFile, int descriptionFormat) {

        BinderChanges changes = new BinderChanges();
        List<BaseBinderChange> changeList = new ArrayList<BaseBinderChange>();
        for (Pair<DefinableEntity, List<ShareItem>> pair : pairs) {
            boolean isNew = pair.getB().size()>0;
            boolean isDeleted = isNew;
            boolean foundValidShare = false;
            Date createDate = null;
            Date expireDate = null;
            for (ShareItem item : pair.getB()) {
                if (!ignoreShareItemInLibraryChanges(item, since)) {
                    foundValidShare = true;
                    createDate = ResourceUtil.min(createDate, item.getStartDate());
                    expireDate = ResourceUtil.min(expireDate, item.getExpiredDate());
                    if (!item.createdSince(since)) {
                        isNew = false;
                    }
                    if (!item.expiredSince(since) && !item.deletedSince(since)) {
                        isDeleted = false;
                    }
                }
            }
            if (foundValidShare && ((isNew && !isDeleted) || (!isNew && isDeleted))) {
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

    private boolean ignoreShareItemInLibraryChanges(ShareItem item, Date since) {
        if (item.isExpired() && !item.expiredSince(since)) {
            return true;
        }
        if (item.isDeleted() && !item.deletedSince(since)) {
            return true;
        }

        if (item.createdSince(since) && (item.isExpired() || item.isDeleted())) {
            return true;
        }

        return false;
    }

    private DefinableEntity getSharedEntity(ShareItem shareItem, boolean accessCheck) {
        return accessCheck ?
                getSharingModule().getSharedEntity(shareItem) :
                getSharingModule().getSharedEntityWithoutAccessCheck(shareItem);
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
            Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1, null);
            SearchResultBuilderUtil.buildSearchResultsTree(results, sharedBinders, new BinderBriefBuilder(descriptionFormat), resultMap);
            results.setItem(null);
            for (SearchResultTreeNode<BinderBrief> child : results.getChildren()) {
                child.getItem().setParentBinder(topParent);
            }
        }
        return results;
    }

    protected List<FileProperties> getSubFiles(SharedBinderBrief [] sharedBinders, String fileName, boolean onlyLibraryFiles) {
        List<FileProperties> results;
        if (sharedBinders.length>0) {
            Junction criterion = Restrictions.conjunction();
            criterion.add(SearchUtils.buildEntriesCriterion());
            criterion.add(entryAncentryCriterion(sharedBinders));
            if (onlyLibraryFiles) {
                criterion.add(Restrictions.eq(Constants.IS_LIBRARY_FIELD, ((Boolean) onlyLibraryFiles).toString()));
            }
            if (fileName!=null) {
                criterion.add(SearchUtils.buildFileNameCriterion(fileName));
            }

            Map resultsMap = getBinderModule().executeSearchQuery(new Criteria().add(criterion), Constants.SEARCH_MODE_NORMAL, 0, -1, null);
            SearchResultList<FileProperties> searchresults = new SearchResultList<FileProperties>();
            SearchResultBuilderUtil.buildSearchResults(searchresults, new FilePropertiesBuilder(this), resultsMap);
            results = searchresults.getResults();
        } else {
            results = new ArrayList<FileProperties>();
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
        List<Pair<ShareItem, DefinableEntity>> shareItems = getShareItems(spec, excludedSharerId, topId==ObjectKeys.SHARED_BY_ME_ID, showPublic, showNonPublic);
        SearchResultList<SearchableObject> results;
        if (shareItems.size()>0) {
            Junction searchContext = Restrictions.disjunction();
            for (Pair<ShareItem, DefinableEntity> pair : shareItems) {
                ShareItem shareItem = pair.getA();
                Junction shareCrit = Restrictions.conjunction();
                EntityIdentifier entityId = shareItem.getSharedEntityIdentifier();
                if (entityId.getEntityType()==EntityIdentifier.EntityType.folderEntry) {
                    FolderEntry entry = (FolderEntry) getDefinableEntity(pair, true);
                    if (showEntryToUser(entry, topId, null, showHidden, showUnhidden, showDeleted)) {
                        shareCrit.add(Restrictions.disjunction()
                                .add(SearchUtils.buildEntryCriterion(entityId.getEntityId()))
                                .add(SearchUtils.buildAttachmentCriterion(entityId.getEntityId()))
                        );
                    }
                } else if (entityId.getEntityType().isBinder()) {
                    Binder binder = (Binder) getDefinableEntity(pair, true);
                    if (showBinderToUser(binder, null, false, topId, showHidden, showUnhidden, showDeleted)) {
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

    private ShareItem _getShareItem(Long id) {
        ShareItem share = getSharingModule().getShareItem(id);
        if (share.isDeleted() || !share.isLatest() || !share.getSharerId().equals(getLoggedInUserId())) {
            // Don't allow the user to modify a share that is not the latest version of the share, or that was shared
            // by someone else.
            throw new NoShareItemByTheIdException(id);
        }
        return share;
    }

    private void setParents(SearchResultList results, List<Pair<DefinableEntity, List<ShareItem>>> shareList, ParentBinder parent) {
        Set<Long> binderIds = new HashSet<Long>();
        Set<Long> entryIds = new HashSet<Long>();
        for (Pair<DefinableEntity, List<ShareItem>> pair : shareList) {
            EntityIdentifier id = pair.getB().get(0).getSharedEntityIdentifier();
            if (id.getEntityType().isBinder()) {
                binderIds.add(id.getEntityId());
            } else if (id.getEntityType()== EntityIdentifier.EntityType.folderEntry ) {
                entryIds.add(id.getEntityId());
            }
        }
        for (Object obj : results.getResults()) {
            if (obj instanceof BinderChange) {
                org.kablink.teaming.rest.v1.model.Binder binder = ((BinderChange)obj).getBinder();
                if (binder!=null && binderIds.contains(binder.getId())) {
                    binder.setParentBinder(parent);
                }
            } else if (obj instanceof FileChange) {
                FileProperties file = ((FileChange)obj).getFile();
                if (file!=null && entryIds.contains(file.getOwningEntity().getId())) {
                    file.setBinder(parent);
                }
            } else if (obj instanceof FolderEntryChange) {
                org.kablink.teaming.rest.v1.model.FolderEntry entry = ((FolderEntryChange)obj).getEntry();
                if (entry!=null && entryIds.contains(entry.getId())) {
                    entry.setParentBinder(parent);
                }
            }
        }
    }


}
