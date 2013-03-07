/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.LinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.DefinableEntity;
import org.kablink.teaming.rest.v1.model.DefinableEntityBrief;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.LibraryInfo;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.ParentBinder;
import org.kablink.teaming.rest.v1.model.RecentActivityEntry;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.TeamBrief;
import org.kablink.teaming.rest.v1.model.User;
import org.kablink.teaming.rest.v1.model.ZoneConfig;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.*;

/**
 * User: david
 * Date: 5/16/12
 * Time: 4:04 PM
 */
@Path("/self")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class SelfResource extends AbstractFileResource {

    /**
     * Gets the User object representing the authenticated user.
     * @param includeAttachments    Configures whether attachments should be included in the returned User object.
     * @return  Returns the authenticated User object
     */
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public User getSelf(@QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        Principal entry = getLoggedInUser();

        User user = ResourceUtil.buildUser((org.kablink.teaming.domain.User) entry, includeAttachments, textDescriptions);
        user.setDiskSpaceQuota(getProfileModule().getMaxUserQuota(entry.getId()));
        user.addAdditionalLink("password", user.getLink() + "/password");
        user.setLink("/self");
        user.addAdditionalLink("roots", "/self/roots");
        if (SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            user.addAdditionalLink("my_files", "/self/my_files");
        }
        user.addAdditionalLink("net_folders", "/self/net_folders");
        user.addAdditionalLink("shared_with_me", "/self/shared_with_me");
        user.addAdditionalLink("shared_by_me", "/self/shared_by_me");
        user.addAdditionalPermaLink("my_files", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_MY_FILES));
        user.addAdditionalPermaLink("net_folders", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_NET_FOLDERS));
        user.addAdditionalPermaLink("shared_with_me", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_SHARED_WITH_ME));
        user.addAdditionalPermaLink("shared_by_me", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_SHARED_BY_ME));
        user.addAdditionalPermaLink("recent_activity", PermaLinkUtil.getUserWhatsNewPermalink(null, entry.getId().toString()));
        Long myFilesFolderId = SearchUtils.getMyFilesFolderId(this, entry.getWorkspaceId(), true);
        if (myFilesFolderId!=null) {
            user.setHiddenFilesFolder(new LongIdLinkPair(myFilesFolderId, LinkUriUtil.getFolderLinkUri(myFilesFolderId)));
        }
        ZoneConfig zoneConfig = ResourceUtil.buildZoneConfig(
                getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId()),
                null,
                getProfileModule().getUserProperties(getLoggedInUserId()));

        user.setDesktopAppConfig(zoneConfig.getDesktopAppConfig());
        user.setMobileAppConfig(zoneConfig.getMobileAppConfig());
        return user;
    }

    /**
     * Returns the authenticated user's favorite binders
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/favorites")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getFavorites() {
        Long userId = getLoggedInUserId();
        List<Binder> binders = getProfileModule().getUserFavorites(userId);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        for (Binder binder : binders) {
            results.append(ResourceUtil.buildBinderBrief(binder));
        }
        return results;
    }

    /**
     * Returns the teams that the authenticated user is a member of.
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/teams")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<TeamBrief> getTeams() {
        Long userId = getLoggedInUserId();
        List<TeamInfo> binders = getProfileModule().getUserTeams(userId);
        SearchResultList<TeamBrief> results = new SearchResultList<TeamBrief>();
        for (TeamInfo binder : binders) {
            results.append(ResourceUtil.buildTeamBrief(binder));
        }
        return results;
    }

    /**
     * Returns a list of virtual workspace roots for the authenticated user.  This is useful for displaying
     * starting points for browsing different parts of the workspace hierarchy.
     * @deprecated  This operation is temporary and is very likely to change.
     * @return Returns a list of BinderBrief objects.
     */
    @GET
    @Path("/roots")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<BinderBrief> getRoots() {
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        results.appendAll(new BinderBrief[] {
                getFakeMyWorkspace(), getFakeMyTeams(), getFakeMyFavorites(),
                ResourceUtil.buildBinderBrief(getBinderModule().getBinder(getWorkspaceModule().getTopWorkspaceId()))
        });
        return results;
    }

    @GET
    @Path("/net_folders")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getNetFolders() {
        return getFakeNetFolders();
    }

    @GET
    @Path("/shared_with_me")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getSharedWithMe(@QueryParam("library_info") @DefaultValue("false") boolean libraryModTime) {
        BinderBrief fakeSharedWithMe = getFakeSharedWithMe();
        if (libraryModTime) {
            Long userId = getLoggedInUserId();
            fakeSharedWithMe.setLibraryInfo(getSharedWithLibraryInfo(userId));
        }
        return fakeSharedWithMe;
    }

    @GET
    @Path("/shared_by_me")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getSharedByMe() {
        return getFakeSharedByMe();
    }

    @GET
    @Path("/my_files")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getMyFiles(@QueryParam("library_info") @DefaultValue("false") boolean libraryInfo) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        BinderBrief fakeMyFileFolders = getFakeMyFileFolders();
        if (libraryInfo) {
            fakeMyFileFolders.setLibraryInfo(getMyFilesLibraryInfo());
        }
        return fakeMyFileFolders;
    }

    @GET
    @Path("/my_files/library_info")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public LibraryInfo getFiles() {
        return getMyFilesLibraryInfo();
    }

    @GET
    @Path("/my_files/library_folders")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMyFileLibraryFolders(
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("-1") Integer maxCount,
            @Context HttpServletRequest request) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }

        Date lastModified = getMyFilesLibraryModifiedDate(false);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<BinderBrief> results = _getMyFilesLibraryFolders(textDescriptions, offset, maxCount, lastModified);
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    @POST
   	@Path("/my_files/library_folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Folder createLibraryFolder(
                                      org.kablink.teaming.rest.v1.model.BinderBrief newBinder,
                                      @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions)
            throws WriteFilesException, WriteEntryDataException {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        org.kablink.teaming.domain.Binder parent = getMyFilesFolderParent();
        if (newBinder.getTitle()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No folder title was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Binder binder = FolderUtils.createLibraryFolder(parent, newBinder.getTitle());
        org.kablink.teaming.rest.v1.model.Folder folder = (org.kablink.teaming.rest.v1.model.Folder) ResourceUtil.buildBinder(binder, true, textDescriptions);
        folder.setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        return folder;
   	}

    @GET
    @Path("/my_files/library_tree")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderTree getMyFileLibraryTree(
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        BinderTree results = new BinderTree();
        SearchResultList<BinderBrief> folders = _getMyFilesLibraryFolders(false, 0, -1, null);
        if (folders.getCount()>0) {
            Criteria crit = new Criteria();
            crit.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
            List<String> idList = new ArrayList<String>(folders.getCount());
            for (BinderBrief folder : folders.getResults()) {
                idList.add(folder.getId().toString());
            }
            crit.add(Restrictions.in(Constants.ENTRY_ANCESTRY, idList));
            Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1);
            SearchResultBuilderUtil.buildSearchResultsTree(results, folders.getResults().toArray(new BinderBrief[folders.getCount()]),
                    new BinderBriefBuilder(textDescriptions), resultMap);
            for (SearchResultTreeNode<BinderBrief> node : results.getChildren()) {
                node.getItem().setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            }
            results.setItem(null);
        }
        return results;
    }

    @GET
    @Path("/my_files/library_entities")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<SearchableObject> getMyFileLibraryEntities(
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
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        Criteria subContextSearch = null;
        if (recursive) {
            SearchResultList<BinderBrief> folders = _getMyFilesLibraryFolders(false, 0, -1, null);
            if (folders.getCount()>0) {
                subContextSearch = new Criteria();
                Junction searchContext = Restrictions.disjunction();
                for (BinderBrief binder : folders.getResults()) {
                    Junction shareCrit = Restrictions.conjunction();
                    shareCrit.add(buildSearchBinderCriterion(binder.getId(), true));
                    searchContext.add(shareCrit);
                }
                subContextSearch.add(searchContext);
            }
        }

        Criteria myFilesCrit = SearchUtils.getMyFilesSearchCriteria(this, getLoggedInUser().getWorkspaceId(), includeBinders, includeFolderEntries, includeReplies, includeFiles);
        Junction searchContext = null;
        if (subContextSearch!=null) {
            searchContext = Restrictions.disjunction()
                    .add(myFilesCrit.asJunction())
                    .add(subContextSearch.asJunction());
        } else {
            searchContext = myFilesCrit.asJunction();
        }

        SearchResultList<SearchableObject> results = searchForLibraryEntities(keyword, searchContext, recursive, offset, maxCount, includeBinders, includeFolderEntries, includeReplies, includeFiles, includeParentPaths, textDescriptions, "/self/my_files/library_entities");
        setMyFilesParents(results);
        return results;
    }

    @GET
    @Path("/my_files/library_files")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response getMyFileLibraryFiles(
            @QueryParam("file_name") String fileName,
            @QueryParam("recursive") @DefaultValue("false") boolean recursive,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("first") Integer offset,
            @QueryParam("count") Integer maxCount,
            @Context HttpServletRequest request) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        Date lastModified = getMyFilesLibraryModifiedDate(recursive);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> resultList = _getMyFilesLibraryFiles(fileName, recursive, includeParentPaths, offset, maxCount);
        return Response.ok(resultList).lastModified(lastModified).build();
    }

    @POST
    @Path("/my_files/library_files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties addLibraryFileFromMultipart(@QueryParam("file_name") String fileName,
                                         @QueryParam("mod_date") String modDateISO8601,
                                         @QueryParam("md5") String expectedMd5,
                                         @QueryParam("overwrite_existing") @DefaultValue("false") Boolean overwriteExisting,
                                         @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        Folder folder = getMyFilesFileParent();
        InputStream is = getInputStreamFromMultipartFormdata(request);
        FileProperties file = createEntryWithAttachment(folder, fileName, modDateISO8601, expectedMd5, overwriteExisting, is);
        file.setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        return file;
    }

    @POST
    @Path("/my_files/library_files")
    @Consumes("*/*")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties addLibraryFile(@QueryParam("file_name") String fileName,
                                         @QueryParam("mod_date") String modDateISO8601,
                                         @QueryParam("md5") String expectedMd5,
                                         @QueryParam("overwrite_existing") @DefaultValue("false") Boolean overwriteExisting,
                                         @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        Folder folder = getMyFilesFileParent();
        InputStream is = getRawInputStream(request);
        FileProperties file = createEntryWithAttachment(folder, fileName, modDateISO8601, expectedMd5, overwriteExisting, is);
        file.setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        return file;
    }

    @GET
    @Path("/my_files/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<RecentActivityEntry> getMyFileRecentActivity(
            @QueryParam("file_name") String fileName,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("text_descriptions") @DefaultValue("false") boolean textDescriptions,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (fileName!=null) {
            nextParams.put("recursive", fileName);
        }
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("text_descriptions", Boolean.toString(textDescriptions));

        List<String> binders = null;
        List<String> entries = null;
        SearchResultList<BinderBrief> folders = _getMyFilesLibraryFolders(false, 0, -1, null);
        if (folders.getCount()>0) {
            binders = new ArrayList<String>();
            for (BinderBrief binder : folders.getResults()) {
                binders.add(binder.getId().toString());
            }
        }
        SearchResultList<FileProperties> files = _getMyFilesLibraryFiles(fileName, false, false, 0, -1);
        if (files.getCount()>0) {
            entries = new ArrayList<String>();
            for (FileProperties file : files.getResults()) {
                entries.add(file.getOwningEntity().getId().toString());
            }
        }
        if (entries==null && binders==null) {
            return new SearchResultList<RecentActivityEntry>();
        }
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binders, entries, null, true, Constants.LASTACTIVITY_FIELD);
        SearchResultList<RecentActivityEntry> resultList = _getRecentActivity(includeParentPaths, textDescriptions, offset, maxCount, criteria, "/self/my_files/recent_activity", nextParams);
        setMyFilesParents(resultList);
        return resultList;
    }

    private SearchResultList<BinderBrief> _getMyFilesLibraryFolders(boolean textDescriptions, Integer offset, Integer maxCount, Date parentModTime) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("text_descriptions", textDescriptions);
        Criteria crit = SearchUtils.getMyFilesSearchCriteria(this, getLoggedInUser().getWorkspaceId(), true, false, false, false);
        SearchResultList<BinderBrief> results = lookUpBinders(crit, textDescriptions, offset, maxCount, "/self/my_files/library_folders", nextParams, parentModTime);
        setMyFilesParents(results);
        return results;
    }

    private void setMyFilesParents(SearchResultList results) {
        List<Long> hiddenFolderIds = getEffectiveMyFilesFolderIds();
        Set<Long> allParentIds = new HashSet(hiddenFolderIds);
        allParentIds.add(getLoggedInUser().getWorkspaceId());
        for (Object obj : results.getResults()) {
            if (obj instanceof FileProperties && allParentIds.contains(((FileProperties)obj).getBinder().getId())) {
                ((FileProperties)obj).setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            } else if (obj instanceof DefinableEntity && allParentIds.contains(((DefinableEntity)obj).getParentBinder().getId())) {
                ((DefinableEntity)obj).setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            } else if (obj instanceof DefinableEntityBrief && allParentIds.contains(((DefinableEntityBrief)obj).getParentBinder().getId())) {
                ((DefinableEntityBrief)obj).setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            }
        }
    }

    private Folder getMyFilesFileParent() {
        org.kablink.teaming.domain.User loggedInUser = getLoggedInUser();
        if (SearchUtils.useHomeAsMyFiles(this, loggedInUser)) {
            return _getHomeFolder();
        } else {
            return _getHiddenFilesFolder();
        }
    }

    private Date getMyFilesFoldersModifiedTime() {
        List<Long> myFilesFolderIds = getEffectiveMyFilesFolderIds();
        if (myFilesFolderIds.size()==0) {
            return null;
        }
        Criteria root = new Criteria();
        root.add(super.buildBindersCriterion());
        Junction ids = Restrictions.disjunction();
        for (Long id : myFilesFolderIds) {
            ids.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
        }
        root.add(ids);
        Map map = getBinderModule().executeSearchQuery(root, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), map);
        return results.getLastModified();
    }

    private List<Long> getEffectiveMyFilesFolderIds() {
        org.kablink.teaming.domain.User user = getLoggedInUser();
        if (SearchUtils.useHomeAsMyFiles(this, user)) {
            return SearchUtils.getHomeFolderIds(this, user);
        } else {
            return SearchUtils.getMyFilesFolderIds(this, user);
        }
    }

    private SearchResultList<FileProperties> _getMyFilesLibraryFiles(String fileName, boolean recursive,
                                                                     boolean includeParentPaths, Integer offset,
                                                                     Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (fileName!=null) {
            nextParams.put("recursive", fileName);
        }
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        Criteria crit = new Criteria();
        crit.add(buildAttachmentsCriterion());
        crit.add(buildLibraryCriterion(true));
        Junction searchContexts = null;
        if (recursive) {
            SearchResultList<BinderBrief> folders = _getMyFilesLibraryFolders(false, 0, -1, null);
            if (folders.getCount()>0) {
                searchContexts = Restrictions.disjunction();
                for (BinderBrief folder : folders.getResults()) {
                    searchContexts.add(buildAncentryCriterion(folder.getId()));
                }
            }
        }
        Criteria myFiles = SearchUtils.getMyFilesSearchCriteria(this, getLoggedInUser().getWorkspaceId(), false, false, false, true);
        if (searchContexts!=null) {
            searchContexts.add(myFiles.asJunction());
            crit.add(searchContexts);
        } else {
            crit.add(myFiles.asJunction());
        }
        if (fileName!=null) {
            crit.add(buildFileNameCriterion(fileName));
        }
        SearchResultList<FileProperties> resultList = lookUpAttachments(crit, offset, maxCount, "/self/my_files/library_files", nextParams, getMyFilesFoldersModifiedTime());
        setMyFilesParents(resultList);
        if (includeParentPaths) {
            populateParentBinderPaths(resultList);
        }
        return resultList;
    }

    private BinderBrief getFakeMyWorkspace() {
        org.kablink.teaming.domain.User loggedInUser = RequestContextHolder.getRequestContext().getUser();
        Binder myWorkspace = getBinderModule().getBinder(loggedInUser.getWorkspaceId());
        BinderBrief binder = ResourceUtil.buildBinderBrief(myWorkspace);
        //TODO: localize
        binder.setTitle("My Workspace");
        return binder;
    }

    private BinderBrief getFakeMyTeams() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("My Teams");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace_team.png"));
        binder.addAdditionalLink("child_binders", "/self/teams");
        return binder;
    }

    private BinderBrief getFakeMyFavorites() {
        BinderBrief binder = new BinderBrief();
        //TODO: localize
        binder.setTitle("My Favorites");
        binder.setIcon(LinkUriUtil.buildIconLinkUri("/icons/workspace_star.png"));
        binder.addAdditionalLink("child_binders", "/self/favorites");
        return binder;
    }
}
