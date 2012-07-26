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
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.*;
import org.kablink.util.search.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @Path("/with_user/{id}")
    public SearchResultList<Share> getSharedWithUser(@PathParam("id") Long userId) {
        _getUser(userId);
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setRecipientsFromUserMembership(userId);
        SearchResultList<Share> results = new SearchResultList<Share>();
        List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
        for (ShareItem shareItem : shareItems) {
            results.append(ResourceUtil.buildShare(shareItem));
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/binders")
    public SearchResultList<SharedBinderBrief> getBindersSharedWithUser(@PathParam("id") Long userId) {
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedBinders(userId, false));
        return results;
    }

    @GET
    @Path("/with_user/{id}/binder_tree")
    public BinderTree getSharedWithUserBinderTree(@PathParam("id") Long userId) {
        SharedBinderBrief [] sharedBinders = getSharedBinders(userId, false);
        return getSubBinderTree(sharedBinders, null);
    }

    @GET
    @Path("/with_user/{id}/library_folders")
    public SearchResultList<SharedBinderBrief> getLibraryFoldersSharedWithUser(@PathParam("id") Long userId) {
        SearchResultList<SharedBinderBrief> results = new SearchResultList<SharedBinderBrief>();
        results.appendAll(getSharedBinders(userId, true));
        return results;
    }

    @GET
    @Path("/with_user/{id}/entries")
    public SearchResultList<SharedFolderEntryBrief> getEntriesSharedWithUser(@PathParam("id") Long userId) {
        _getUser(userId);
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setRecipientsFromUserMembership(userId);
        SearchResultList<SharedFolderEntryBrief> results = new SearchResultList<SharedFolderEntryBrief>();
        List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
        for (ShareItem shareItem : shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                results.append(ResourceUtil.buildSharedFolderEntryBrief(shareItem, (FolderEntry) getSharingModule().getSharedEntity(shareItem)));
            }
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/files")
    public SearchResultList<FileProperties> getFilesSharedWithUser(@PathParam("id") Long userId,
                                                                         @QueryParam("recursive") @DefaultValue("false") boolean recursive) {
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedFiles(userId, false));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedBinders(userId, false), false));
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/library_files")
    public SearchResultList<FileProperties> getLibraryFilesSharedWithUser(@PathParam("id") Long userId,
                                                                          @QueryParam("recursive") @DefaultValue("false") boolean recursive) {
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.appendAll(getSharedFiles(userId, true));
        if (recursive) {
            results.appendAll(getSubFiles(getSharedBinders(userId, true), true));
        }
        return results;
    }

    @GET
    @Path("/with_user/{id}/library_tree")
    public BinderTree getSharedWithUserLibraryTree(@PathParam("id") Long userId) {
        SharedBinderBrief [] sharedBinders = getSharedBinders(userId, true);
        return getSubBinderTree(sharedBinders, buildLibraryTreeCriterion());
    }

    protected SharedBinderBrief [] getSharedBinders(Long userId, boolean onlyLibrary)  {
        _getUser(userId);
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setRecipientsFromUserMembership(userId);
        List<SharedBinderBrief> results = new ArrayList<SharedBinderBrief>();
        List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
        for (ShareItem shareItem : shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType().isBinder()) {
                Binder binder = (Binder) getSharingModule().getSharedEntity(shareItem);
                if (!onlyLibrary || binder.getEntityType() == EntityIdentifier.EntityType.workspace || binder.isLibrary()) {
                    results.add(ResourceUtil.buildSharedBinderBrief(shareItem, binder));
                }
            }
        }
        return results.toArray(new SharedBinderBrief[results.size()]);
    }

    protected SharedFileProperties [] getSharedFiles(Long userId, boolean onlyLibrary)  {
        _getUser(userId);
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setRecipientsFromUserMembership(userId);
        List<SharedFileProperties> results = new ArrayList<SharedFileProperties>();
        List<ShareItem> shareItems = getSharingModule().getShareItems(spec);
        for (ShareItem shareItem : shareItems) {
            if (shareItem.getSharedEntityIdentifier().getEntityType()== EntityIdentifier.EntityType.folderEntry) {
                FolderEntry entry = (FolderEntry) getSharingModule().getSharedEntity(shareItem);
                Set<Attachment> attachments = entry.getAttachments();
                for (Attachment attachment : attachments) {
                    if (attachment instanceof FileAttachment) {
                        results.add(ResourceUtil.buildSharedFileProperties(shareItem, (FileAttachment) attachment));
                    }
                }
            }
        }
        return results.toArray(new SharedFileProperties[results.size()]);
    }

    protected BinderTree getSubBinderTree(SharedBinderBrief [] sharedBinders, Criterion filter) {
        Criteria crit = new Criteria();
        if (filter!=null) {
            crit.add(filter);
        }
        crit.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
        crit.add(entryAncentryCriterion(sharedBinders));
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1);
        BinderTree results = new BinderTree();
        SearchResultBuilderUtil.buildSearchResultsTree(results, sharedBinders, new BinderBriefBuilder(), resultMap);
        results.setItem(null);
        return results;
    }

    protected List<FileProperties> getSubFiles(SharedBinderBrief [] sharedBinders, boolean onlyLibraryFiles) {
        Junction criterion = Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT));

        criterion.add(entryAncentryCriterion(sharedBinders));
        if (onlyLibraryFiles) {
            criterion.add(Restrictions.eq(Constants.IS_LIBRARY_FIELD, ((Boolean) onlyLibraryFiles).toString()));
        }
        List<FileIndexData> files = getFileModule().getFileDataFromIndex(new Criteria().add(criterion));
        List<FileProperties> results = new ArrayList<FileProperties>();
        for (FileIndexData file : files) {
            results.add(ResourceUtil.buildFileProperties(file));
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

}
