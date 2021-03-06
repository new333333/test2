/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import com.webcohesion.enunciate.metadata.rs.ResponseCode;
import com.webcohesion.enunciate.metadata.rs.StatusCodes;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.TeamInfo;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.AdhocSettingChangedException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.NotModifiedException;
import org.kablink.teaming.remoting.rest.v1.exc.RestExceptionWrapper;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.LinkUriUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.LibraryInfo;
import org.kablink.teaming.rest.v1.model.LongIdLinkPair;
import org.kablink.teaming.rest.v1.model.MobileDevice;
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
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
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
import javax.ws.rs.core.Response;

import java.io.InputStream;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.*;

/**
 * Resources related the authenticated user.
 */
@Path("/self")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
@SuppressWarnings("unchecked")
public class SelfResource extends AbstractFileResource {
    private Map<Long, Long> homeDirCheckTime = new HashMap<Long, Long>();

    /**
     * Get the User object representing the authenticated user.
     * @param includeAttachments    Whether to include attachments in the returned User object.
     * @param includeMobileDevices  Whether to include the mobile devices associated with the user in the response.
     * @param includeGroups  Whether to include the groups the user belongs to in the response.
     * @param descriptionFormatStr The desired format for the User description.  Can be "html" or "text".
     * @return  The authenticated User object
     */
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ResourceGroup("Authenticated User")
    public User getSelf(@Undocumented @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                        @QueryParam("include_mobile_devices") @DefaultValue("false") boolean includeMobileDevices,
                        @QueryParam("include_groups") @DefaultValue("false") boolean includeGroups,
                        @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        org.kablink.teaming.domain.User loggedInUser = getLoggedInUser();
        Principal entry = loggedInUser;

        User user = ResourceUtil.buildUser((org.kablink.teaming.domain.User) entry, includeAttachments,
                toDomainFormat(descriptionFormatStr));
        user.setDiskSpaceQuota(getProfileModule().getMaxUserQuota(entry.getId()));
        user.addAdditionalLink("password", user.getLink() + "/password");
        user.setLink("/self");
        user.addAdditionalLink("mobile_devices", "/self/mobile_devices");
        user.addAdditionalLink("roots", "/self/roots");
        user.setPermaLink(PermaLinkUtil.getPermalink(entry));
        try {
            Long nextCheck = homeDirCheckTime.get(user.getId());
            if (nextCheck==null || nextCheck<System.currentTimeMillis()) {
                getLdapModule().updateHomeDirectoryIfNecessary((org.kablink.teaming.domain.User)entry, entry.getName(), true);
                homeDirCheckTime.put(user.getId(), System.currentTimeMillis()+1000*60*60);
            }
        } catch (Exception e) {
            logger.warn("An error occurred checking to see if the user's home folder needs to be updated", e);
        }
        if (SearchUtils.userCanAccessMyFiles(this, loggedInUser)) {
            user.addAdditionalLink("my_files", "/self/my_files");
        }
        user.addAdditionalLink("net_folders", "/self/net_folders");
        user.addAdditionalLink("shared_with_me", "/self/shared_with_me");
        user.addAdditionalLink("shared_by_me", "/self/shared_by_me");
        if (getEffectivePublicCollectionSetting((org.kablink.teaming.domain.User) entry)) {
            user.addAdditionalLink("public_shares", "/self/public_shares");
        }
        user.addAdditionalLink("my_teams", "/self/my_teams");
        user.addAdditionalLink("my_favorites", "/self/my_favorites");
//        user.addAdditionalPermaLink("my_files", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_MY_FILES));
//        user.addAdditionalPermaLink("net_folders", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_NET_FOLDERS));
        user.addAdditionalPermaLink("shared_with_me", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_SHARED_WITH_ME));
//        user.addAdditionalPermaLink("shared_by_me", PermaLinkUtil.getUserPermalink(null, entry.getId().toString(), PermaLinkUtil.COLLECTION_SHARED_BY_ME));
//        user.addAdditionalPermaLink("recent_activity", PermaLinkUtil.getUserWhatsNewPermalink(null, entry.getId().toString()));
        Long myFilesFolderId = SearchUtils.getMyFilesFolderId(this, (org.kablink.teaming.domain.User) entry, true);
        if (myFilesFolderId!=null) {
            user.setHiddenFilesFolder(new LongIdLinkPair(myFilesFolderId, LinkUriUtil.getFolderLinkUri(myFilesFolderId)));
        }
        ZoneConfig zoneConfig = ResourceUtil.buildZoneConfig(
                getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId()),
                null,
                AdminHelper.getEffectiveMobileAppsConfigOverride(this, loggedInUser),
                AdminHelper.getEffectiveDesktopAppsConfigOverride(this, loggedInUser),
                loggedInUser,
                false,
                null,
                this);

        user.setDesktopAppConfig(zoneConfig.getDesktopAppConfig());
        user.setMobileAppConfig(zoneConfig.getMobileAppConfig());
        if (includeMobileDevices) {
        	List<org.kablink.teaming.domain.MobileDevice> mdList = getMobileDeviceModule().getMobileDeviceList(getLoggedInUserId());
            List<MobileDevice> devices = new ArrayList<MobileDevice>();
            if (null != mdList) {
	            for (org.kablink.teaming.domain.MobileDevice d :mdList) {
	                devices.add(ResourceUtil.buildMobileDevice(d));
	            }
            }
            user.setMobileDevices(devices);
        }

        if (includeGroups && entry.getIdentityInfo().isInternal()) {
            List<Group> groups = getProfileModule().getUserGroups(getLoggedInUserId());
            List<LongIdLinkPair> ids = new ArrayList<LongIdLinkPair>(groups.size());
            for (Group group : groups) {
                ids.add(new LongIdLinkPair(group.getId(), LinkUriUtil.getGroupLinkUri(group.getId())));
            }
            user.setGroups(ids);
        }
        return user;
    }

    /**
     * Get the authenticated user's favorite binders
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
     * Get the teams that the authenticated user is a member of.
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
     * Get a list of virtual workspace roots for the authenticated user.  This is useful for displaying
     * starting points for browsing different parts of the workspace hierarchy.
     * @deprecated  This operation is temporary and is very likely to change.
     * @return Returns a list of BinderBrief objects.
     */
	@GET
    @Path("/roots")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Top Level Folders")
    public SearchResultList<BinderBrief> getRoots() {

        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        if (Utils.checkIfFilr()) {
            BinderBrief root = getFakeMyFileFolders(true);
            if (root!=null) {
                results.append(root);
            }
            results.append(getFakeSharedWithMe());
            results.append(getFakeSharedByMe());
            results.append(getFakeNetFolders());
            root = getFakePublicShares(true);
            if (root!=null) {
                results.append(root);
            }
        } else {
            results.appendAll(new BinderBrief[] {
                    getFakeMyWorkspace(), getFakeMyTeams(), getFakeMyFavorites(),
                    ResourceUtil.buildBinderBrief(getBinderModule().getBinder(getWorkspaceModule().getTopWorkspaceId()))
            });
        }

        return results;
    }

    /**
     * Get a Binder object representing the top-level Net Folders folder.
     * @return A BinderBrief object.
     */
    @GET
    @Path("/net_folders")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Top Level Folders")
    public BinderBrief getNetFolders() {
        return getFakeNetFolders();
    }

    /**
     * Get a Binder object representing the top-level Public folder.
     * @return A BinderBrief object.
     */
    @GET
    @Path("/public_shares")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("Top Level Folders")
    public BinderBrief getPublicShares() {
        BinderBrief fakePublicShares = getFakePublicShares(true);
        if (fakePublicShares==null) {
            throw new AccessControlException();
        }
        return fakePublicShares;
    }

    @Undocumented
    @GET
    @Path("/public_shares/library_info")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public LibraryInfo getPublicLibraryInfo() {
        BinderBrief fakePublicShares = getFakePublicShares(true);
        if (fakePublicShares==null) {
            throw new AccessControlException();
        }
        return getPublicSharesLibraryInfo();
    }

    /**
     * Get a Binder object representing the top-level Shared with Me folder.
     * @return A BinderBrief object.
     */
    @ResourceGroup("Top Level Folders")
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

    @Undocumented
    @GET
    @Path("/shared_with_me/library_info")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public LibraryInfo getSharedWithMeLibraryInfo() {
        return getSharedWithLibraryInfo(getLoggedInUserId());
    }

    /**
     * Get a Binder object representing the top-level Shared by Me folder.
     * @return A BinderBrief object.
     */
    @ResourceGroup("Top Level Folders")
    @GET
    @Path("/shared_by_me")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getSharedByMe() {
        return getFakeSharedByMe();
    }

    @Undocumented
    @GET
    @Path("/shared_by_me/library_info")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public LibraryInfo getSharedByMeLibraryInfo() {
        return getSharedByLibraryInfo(getLoggedInUserId());
    }

    /**
     * Get a Binder object representing the top-level My Files folder.
     * @return A BinderBrief object.
     */
    @ResourceGroup("Top Level Folders")
    @GET
    @Path("/my_files")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getMyFiles(@QueryParam("library_info") @DefaultValue("false") boolean libraryInfo) {
        org.kablink.teaming.domain.User loggedInUser = getLoggedInUser();

        BinderBrief fakeMyFileFolders = getFakeMyFileFolders(true);
        if (fakeMyFileFolders==null) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        if (libraryInfo) {
            fakeMyFileFolders.setLibraryInfo(getMyFilesLibraryInfo());
        }
        return fakeMyFileFolders;
    }

    @Undocumented
    @GET
    @Path("/my_files/library_info")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public LibraryInfo getFiles() {
        return getMyFilesLibraryInfo();
    }

    /**
     * List the children of My Files.
     *
     * <p>The <code>title</code> query parameter limits the results to those children with the specified name.  Wildcards are not supported.</p>
     *
     * @param name  The name of the child to return,
     * @param descriptionFormatStr The desired format for the children descriptions.  Can be "html" or "text".
     * @param allowJits Whether to trigger JITS, if applicable.
     * @param offset    The index of the first result to return.
     * @param maxCount  The maximum number of results to return.
     * @return  A SearchResultList of SearchableObjects (BinderBriefs and FileProperties).
     */
    @GET
    @Path("/my_files/library_children")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("My Files")
    public Response getMyFileLibraryChildren(
            @QueryParam("title") String name,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
            @QueryParam("allow_jits") @DefaultValue("true") Boolean allowJits,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("100") Integer maxCount,
            @Context HttpServletRequest request) {
        SearchResultList<SearchableObject> results = _getMyFilesLibraryChildren(name, getIfModifiedSinceDate(request), true, false, true,
                allowJits, toDomainFormat(descriptionFormatStr), offset, maxCount, "/self/my_files/library_children");
        Date lastModified = results.getLastModified();
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    /**
     * List the child folders of My Files.
     *
     * <p>The <code>title</code> query parameter limits the results to those folders with the specified name.  Wildcards are not supported.</p>
     *
     * @param name  The name of the child to return,
     * @param descriptionFormatStr The desired format for the children descriptions.  Can be "html" or "text".
     * @param offset    The index of the first result to return.
     * @param maxCount  The maximum number of results to return.
     * @return  A SearchResultList of SearchableObjects (BinderBriefs and FileProperties).
     */
    @GET
    @Path("/my_files/library_folders")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("My Files")
    public Response getMyFileLibraryFolders(
            @QueryParam("title") String name,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
            @QueryParam("first") @DefaultValue("0") Integer offset,
            @QueryParam("count") @DefaultValue("100") Integer maxCount,
            @Context HttpServletRequest request) {
        SearchResultList<SearchableObject> results = _getMyFilesLibraryChildren(name, getIfModifiedSinceDate(request), true, false, false,
                true, toDomainFormat(descriptionFormatStr), offset, maxCount, "/self/my_files/library_children");
        Date lastModified = results.getLastModified();
        if (lastModified!=null) {
            return Response.ok(results).lastModified(lastModified).build();
        } else {
            return Response.ok(results).build();
        }
    }

    /**
     * Copy a folder into the user's My Files folder.
     *
     * <p>The Content-Type must be <code>application/x-www-form-urlencoded</code>.  The title value in the form data should
     * be a URL-encoded UTF-8 string.  For example: <code>source_id=48&title=H%C3%B6wdy</code>.</p>
     * @param title    The name of the new folder.
     * @param sourceId    The ID of the source folder to copy.
     * @return  The new binder metadata.
     */
	@POST
    @Path("/my_files/library_folders")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ResourceGroup("My Files")
    public org.kablink.teaming.rest.v1.model.Folder copyFolder(@QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                                               @FormParam("title") String title,
                                                               @FormParam("source_id") Long sourceId) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        if (title==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No title parameter was supplied in the POST data.");
        }
        if (sourceId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No source_id parameter was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Binder parent = getMyFilesFolderParent();
        org.kablink.teaming.domain.Folder source = _getFolder(sourceId);
        if (BinderHelper.isBinderHomeFolder(source)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Copying a home folder is not supported");
        }
        Map options = new HashMap();
        options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, title);
        try {
            org.kablink.teaming.domain.Binder binder = getBinderModule().copyBinder(sourceId, parent.getId(), true, options);
            return (org.kablink.teaming.rest.v1.model.Folder) ResourceUtil.buildBinder(binder, true, toDomainFormat(descriptionFormatStr));
        } catch (TitleException e) {
            org.kablink.teaming.rest.v1.model.Binder data = null;
            try {
                org.kablink.teaming.domain.Binder binder = getFolderByName(parent.getId(), title);
                if (binder!=null) {
                    data = ResourceUtil.buildBinder(binder, true, toDomainFormat(descriptionFormatStr));
                }
            } catch (AccessControlException e1) {
            }
            throw new RestExceptionWrapper(e, e, e, data);
        }
    }

    /**
     * Create a new folder in the authenticated user's My Files folder.
     *
     * @param newBinder    The BinderBrief object to be created.  Minimally, you must specify the "title".
     * @param descriptionFormatStr The desired format for the folder description in the response.  Can be "html" or "text".
     * @return  The new Folder object.
     */
    @POST
   	@Path("/my_files/library_folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ResourceGroup("My Files")
   	public org.kablink.teaming.rest.v1.model.Folder createLibraryFolder(
                                      org.kablink.teaming.rest.v1.model.BinderBrief newBinder,
                                      @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        org.kablink.teaming.domain.Binder parent = getMyFilesFolderParent();
        if (newBinder.getTitle()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No folder title was supplied in the POST data.");
        }
        try {
            newBinder.setTitle(Normalizer.normalize(newBinder.getTitle(), Normalizer.Form.NFC));
            org.kablink.teaming.domain.Binder child = getFolderByName(parent.getId(), newBinder.getTitle());
            if (child!=null) {
                throw new TitleException(newBinder.getTitle());
            }
            org.kablink.teaming.domain.Binder binder = FolderUtils.createLibraryFolder(parent, newBinder.getTitle());
            org.kablink.teaming.rest.v1.model.Folder folder =
                    (org.kablink.teaming.rest.v1.model.Folder) ResourceUtil.buildBinder(binder, true, toDomainFormat(descriptionFormatStr));
            folder.setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            return folder;
        } catch (TitleException e) {
            org.kablink.teaming.rest.v1.model.Binder data = null;
            try {
                org.kablink.teaming.domain.Binder binder = getFolderByName(parent.getId(), newBinder.getTitle());
                if (binder!=null) {
                    data = ResourceUtil.buildBinder(binder, true, toDomainFormat(descriptionFormatStr));
                    data.setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
                }
            } catch (AccessControlException e1) {
            }
            throw new RestExceptionWrapper(e, e, e, data);
        }

   	}

    /**
     * Get changes to files and folders that have occurred in My Files since the specified date.
     * @param since UTC date and time in ISO 8601 format.  For example, 2016-03-05T06:24:57Z.
     * @param recursive Whether to return changes in the immediate folder only (false) or all subfolders (true).
     * @param descriptionFormatStr The desired format for descriptions.  Can be "html" or "text".
     * @param maxCount  The maximum number of changes to return.
     * @return  A BinderChanges resource.
     */
    @GET
    @Path ("/my_files/library_changes")
    @ResourceGroup("My Files")
    @StatusCodes({
            @ResponseCode(code=409, condition="The changes cannot be determined."),
    })
    public BinderChanges getMyFilesChanges(@QueryParam("since") String since,
                                           @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                           @QueryParam("recursive") @DefaultValue("true") boolean recursive,
                                           @QueryParam ("count") @DefaultValue("500") Integer maxCount) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        org.kablink.teaming.domain.Binder parent;
        if (recursive) {
            parent = getMyFilesFolderParent();
        } else {
            parent = getMyFilesFileParent();
        }
        if (since==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'since' query parameter");
        }
        try {
            Date sinceDate = dateFormat.parse(since);
            Date adhocSettingDate = AdminHelper.getEffectiveAdhocFolderSettingDate(this, getLoggedInUser());
            if (adhocSettingDate!=null && sinceDate.before(adhocSettingDate)) {
                throw new AdhocSettingChangedException();
            }
            BinderChanges binderChanges = getBinderChanges(new Long[]{parent.getId()}, null, since, recursive, descriptionFormatStr, maxCount, "/my_files/library_changes");
            setMyFilesParents(binderChanges);
            return binderChanges;
        } catch (ParseException e) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Invalid date in the 'since' query parameter");
        }
    }

    /**
     * Get a tree structure representing the folder structure contained in My Files.
     * @param descriptionFormatStr The desired format for the binder descriptions.  Can be "html" or "text".
     * @return  A BinderTree
     */
    @GET
    @Path("/my_files/library_tree")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("My Files")
    public BinderTree getMyFileLibraryTree(
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        BinderTree results = new BinderTree();
        int descriptionFormat = toDomainFormat(descriptionFormatStr);
        SearchResultList<BinderBrief> folders = _getMyFilesLibraryFolders(descriptionFormat, 0, -1, null);
        if (folders.getCount()>0) {
            Criteria crit = new Criteria();
            crit.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
            List<String> idList = new ArrayList<String>(folders.getCount());
            for (BinderBrief folder : folders.getResults()) {
                idList.add(folder.getId().toString());
            }
            crit.add(Restrictions.in(Constants.ENTRY_ANCESTRY, idList));
            Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1, null);
            SearchResultBuilderUtil.buildSearchResultsTree(results, folders.getResults().toArray(new BinderBrief[folders.getCount()]),
                    new BinderBriefBuilder(descriptionFormat), resultMap);
            for (SearchResultTreeNode<BinderBrief> node : results.getChildren()) {
                node.getItem().setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
            }
            results.setItem(null);
        }
        return results;
    }

    /**
     * Search for entities by keyword.
     * @param recursive Whether to search the immediate folder (false) or all subfolders (true).
     * @param includeBinders    Whether to include binders in the results.
     * @param includeFolderEntries  Whether to include folder entries in the results.
     * @param includeFiles  Whether to include files in the results.
     * @param includeReplies    Whether to include replies in the results.
     * @param includeParentPaths    Whether to include the parent binder path with each entity.
     * @param keyword   A search term.  May include wildcards, but cannot begin with a wildcard.  For example, "keyword=D*d" is
     *                  allowed but "keyword=*d" is not.
     * @param descriptionFormatStr The desired format for the binder description.  Can be "html" or "text".
     * @param offset    The index of the first result to return.
     * @param maxCount  The maximum number of results to return.
     * @return  A SearchResultList of SearchableObject resources (BinderBrief, FolderEntryBrief, FileProperties, ReplyBrief).
     */
    @GET
    @Path("/my_files/library_entities")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("My Files")
    public SearchResultList<SearchableObject> getMyFileLibraryEntities(
              @QueryParam("recursive") @DefaultValue("false") boolean recursive,
              @QueryParam("binders") @DefaultValue("true") boolean includeBinders,
              @QueryParam("folder_entries") @DefaultValue("true") boolean includeFolderEntries,
              @QueryParam("files") @DefaultValue("true") boolean includeFiles,
              @QueryParam("replies") @DefaultValue("true") boolean includeReplies,
              @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
              @QueryParam("keyword") String keyword,
              @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
              @QueryParam("first") @DefaultValue("0") Integer offset,
              @QueryParam("count") @DefaultValue("100") Integer maxCount) {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        int descriptionFormat = toDomainFormat(descriptionFormatStr);
        Criteria subContextSearch = null;
        if (recursive) {
            SearchResultList<BinderBrief> folders = _getMyFilesLibraryFolders(descriptionFormat, 0, -1, null);
            if (folders.getCount()>0) {
                subContextSearch = new Criteria();
                Junction searchContext = Restrictions.disjunction();
                for (BinderBrief binder : folders.getResults()) {
                    Junction shareCrit = Restrictions.conjunction();
                    shareCrit.add(SearchUtils.buildSearchBinderCriterion(binder.getId(), true));
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

        SearchResultList<SearchableObject> results = searchForLibraryEntities(keyword, searchContext, recursive, offset,
                maxCount, includeBinders, includeFolderEntries, includeReplies, includeFiles, includeParentPaths,
                descriptionFormat, "/self/my_files/library_entities");
        setMyFilesParents(results);
        return results;
    }

    /**
     * List the child files of My Files.
     *
     * @param fileName The name of the child to return,
     * @param recursive Whether to search the binder and sub-binders for files.
     * @param offset    The index of the first result to return.
     * @param maxCount  The maximum number of results to return.
     * @param includeParentPaths    If true, the path of the parent binder is included in each result.
     * @return  A SearchResultList of SearchableObjects (BinderBriefs and FileProperties).
     */
    @GET
    @Path("/my_files/library_files")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("My Files")
    public Response getMyFileLibraryFiles(
            @QueryParam("file_name") String fileName,
            @QueryParam("recursive") @DefaultValue("false") boolean recursive,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("first") Integer offset,
            @QueryParam("count") Integer maxCount,
            @Context HttpServletRequest request) {
        if (!recursive && maxCount==null) {
            maxCount = 100;
        }
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        Date lastModified = getMyFilesLibraryModifiedDate(recursive, true);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
    }
        SearchResultList<FileProperties> resultList = _getMyFilesLibraryFiles(fileName, recursive, includeParentPaths, offset, maxCount);
        resultList.updateLastModified(AdminHelper.getEffectiveAdhocFolderSettingDate(this, getLoggedInUser()));
        return Response.ok(resultList).lastModified(lastModified).build();
    }

    /**
     * Copy a file into the user's My Files top level folder.
     *
     * <p>The Content-Type must be <code>application/x-www-form-urlencoded</code>.  The parameter values in the form data should
     * be URL-encoded UTF-8 strings.  For example: <code>source_id=09c1c3fb530f562401531070137b000e&file_name=H%C3%B6wdy</code></p>.
     * @param fileName    The name of the new file.
     * @param sourceId    The ID of the source file to copy.
     * @return  The new file metadata.
     */
    @POST
    @Path("/my_files/library_files")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ResourceGroup("My Files")
    @StatusCodes({
            @ResponseCode(code=404, condition="(FILE_NOT_FOUND) The source file does not exist."),
            @ResponseCode(code=409, condition="(FILE_EXISTS) A file with the specified name already exists in the target folder."),
    })
    public FileProperties copyFile(@FormParam("file_name") String fileName,
                                   @FormParam("source_id") String sourceId,
                                   @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        if (fileName==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No file_name parameter was supplied in the POST data.");
        }
        if (sourceId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No source_id parameter was supplied in the POST data.");
        }
        Folder folder = getMyFilesFileParent();
        FileAttachment existing = getFileAttachment(sourceId);
        org.kablink.teaming.domain.DefinableEntity origEntry = existing.getOwner().getEntity();
        org.kablink.teaming.domain.FolderEntry newEntry = getFolderModule().copyEntry(origEntry.getParentBinder().getId(),
                origEntry.getId(), folder.getId(), new String[] {fileName}, null);
        Set<Attachment> attachments = newEntry.getAttachments();
        for (Attachment attachment : attachments) {
            if (attachment instanceof FileAttachment) {
                FileProperties file = ResourceUtil.buildFileProperties((FileAttachment) attachment);
                file.setBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
                return file;
            }
        }
        return null;
    }

    /**
     * Add a file to the user's My Files top level folder.  This is the multipart form version.  The Content-Type must be <code>multipart/form-data</code>.
     * See <a>https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2</a>.
     *
     * @param fileName  The name of the file to create.
     * @param modDateISO8601    The desired last modified time for the new file.
     * @param expectedMd5       The MD5 checksum of the file.  If specified, the REST interface returns an error if the
     *                          MD5 checksum of the uploaded content does not match the expected value.
     * @param overwriteExisting     If a file already exists with the specified name, this specifies whether to overwrite the file (true) or fail with an error (false).
     */
    @Undocumented
    @POST
    @Path("/my_files/library_files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ResourceGroup("My Files")
    @StatusCodes({
            @ResponseCode(code=409, condition="(FILE_EXISTS) A file with the specified name already exists in the target folder."),
    })
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

    /**
     * Add a file to the user's My Files top level folder.  The request Content-Type can be anything except <code>x-www-form-urlencoded</code>.
     * Supports <code>multipart/form-data</code> posts (see <a href="https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2">here</a>).
     * If another Content-Type is specified (<code>application/octet-stream</code>, for example), the raw bytes of the request body
     * are read and stored as the file content.
     *
     * @param fileName  The name of the file to create.
     * @param modDateISO8601    The desired last modified time for the new file.
     * @param expectedMd5       The MD5 checksum of the file.  If specified, the REST interface returns an error if the
     *                          MD5 checksum of the uploaded content does not match the expected value.
     * @param overwriteExisting     If a file already exists with the specified name, this specifies whether to overwrite the file (true) or fail with an error (false).
     */
    @POST
    @Path("/my_files/library_files")
    @Consumes("*/*")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ResourceGroup("My Files")
    @StatusCodes({
            @ResponseCode(code=409, condition="(FILE_EXISTS) A file with the specified name already exists in the target folder."),
    })
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

    /**
     * List recently changed folder entries in My Files.
     * @param includeParentPaths    Whether to include the parent binder path with each entry.
     * @param descriptionFormatStr The desired format for the folder entry description.  Can be "html" or "text".
     * @param offset    The index of the first result to return.
     * @param maxCount  The maximum number of results to return.
     * @return  A SearchResultList of RecentActivityEntry resources.
     */
    @GET
    @Path("/my_files/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @ResourceGroup("My Files")
    public SearchResultList<RecentActivityEntry> getMyFileRecentActivity(
            @QueryParam("file_name") String fileName,
            @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
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
        nextParams.put("description_format", descriptionFormatStr);

        List<String> binders = null;
        List<String> entries = null;
        int descriptionFormat = toDomainFormat(descriptionFormatStr);
        SearchResultList<BinderBrief> folders = _getMyFilesLibraryFolders(descriptionFormat, 0, -1, null);
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
        SearchResultList<RecentActivityEntry> resultList = _getRecentActivity(includeParentPaths, descriptionFormat,
                offset, maxCount, criteria, "/self/my_files/recent_activity", nextParams);
        setMyFilesParents(resultList);
        return resultList;
    }

    @Undocumented
    @POST
    @Path("/my_files/initial_sync")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response performInitialSync() {
        if (!SearchUtils.userCanAccessMyFiles(this, getLoggedInUser())) {
            throw new AccessControlException("Personal storage is not allowed.", null);
        }
        List<Long> homeFolderIds = SearchUtils.getHomeFolderIds(this, getLoggedInUser());
        for (Long id : homeFolderIds) {
            try {
                lookupNetFolder(id);
                getFolderModule().enqueueInitialNetFolderSync(id);
            } catch (Exception e) {
                logger.error("Unable to trigger initial sync of the user's home folder: " + getLoggedInUser().getName(), e);
                throw new InternalServerErrorException(ApiErrorCode.SERVER_ERROR, e.getMessage());
            }
        }
        return Response.ok().build();
    }

    @Undocumented
    @GET
    @Path("mobile_devices")
    public SearchResultList<MobileDevice> getMobileDevices() {
    	List<org.kablink.teaming.domain.MobileDevice> mdList = getMobileDeviceModule().getMobileDeviceList(getLoggedInUserId());
        SearchResultList<MobileDevice> results = new SearchResultList<MobileDevice>();
        if (null != mdList) {
            for (org.kablink.teaming.domain.MobileDevice device : mdList) {
                results.append(ResourceUtil.buildMobileDevice(device));
            }
        }
        return results;
    }

    @Undocumented
    @POST
    @Path("mobile_devices")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MobileDevice addMobileDevice(MobileDevice device) {
        org.kablink.teaming.domain.MobileDevice existingDevice = getMobileDeviceModule().getMobileDevice(getLoggedInUserId(), device.getId());
        if (null != existingDevice) {
            throw new ConflictException(ApiErrorCode.DEVICE_EXISTS, "A device with the specified ID already exists.");
        }
        org.kablink.teaming.domain.MobileDevice mobileDevice = toMobileDevice(device);
        getMobileDeviceModule().addMobileDevice(mobileDevice);
        return ResourceUtil.buildMobileDevice(mobileDevice);
    }

    @Undocumented
    @GET
    @Path("mobile_devices/{id}")
    public MobileDevice getMobileDevice(@PathParam("id") String id) {
    	org.kablink.teaming.domain.MobileDevice device = getMobileDeviceModule().getMobileDevice(getLoggedInUserId(), id);
        if (null != device) {
            return ResourceUtil.buildMobileDevice(device);
        }
        throw new NotFoundException(ApiErrorCode.DEVICE_NOT_FOUND, "No device with ID: " + id);
    }

    @Undocumented
    @PUT
    @Path("mobile_devices/{id}")
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public MobileDevice updateMobileDevice(@PathParam("id") String id, MobileDevice newDevice) {
        newDevice.setId(id);
        org.kablink.teaming.domain.MobileDevice newDomainDevice      = toMobileDevice(newDevice);
        org.kablink.teaming.domain.MobileDevice existingDomainDevice = getMobileDeviceModule().getMobileDevice(getLoggedInUserId(), id);
        if (existingDomainDevice!=null) {
            if (newDomainDevice.getDescription()!=null) {
                existingDomainDevice.setDescription(newDomainDevice.getDescription());
            }
//!         if (mobileDevice.getPushToken()!=null) {
//!             existingevice.setPushToken(mobileDevice.getPushToken());
//!         }
            if (newDomainDevice.getLastWipe()!=null) {
                existingDomainDevice.setLastWipe(newDomainDevice.getLastWipe());
            }
            existingDomainDevice.setLastLogin(new Date());
            if (newDevice.isWipeScheduled()!=null) {
                existingDomainDevice.setWipeScheduled(newDevice.isWipeScheduled());
            }
            getMobileDeviceModule().modifyMobileDevice(existingDomainDevice);
            return ResourceUtil.buildMobileDevice(existingDomainDevice);
        }
        throw new NotFoundException(ApiErrorCode.DEVICE_NOT_FOUND, "No device with ID: " + id);
    }

    @Undocumented
    @DELETE
    @Path("mobile_devices/{id}")
    public void deleteMobileDevice(@PathParam("id") String id) {
        org.kablink.teaming.domain.MobileDevice mobileDevice = getMobileDeviceModule().getMobileDevice(getLoggedInUserId(), id);
        if (mobileDevice!=null) {
            getMobileDeviceModule().deleteMobileDevice(getLoggedInUserId(), id);
            return;
        }
        throw new NotFoundException(ApiErrorCode.DEVICE_NOT_FOUND, "No device with ID: " + id);
    }

    @GET
    @Path("/my_teams")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getMyTeams(@QueryParam("library_info") @DefaultValue("false") boolean libraryInfo) {
        BinderBrief fakeFolder = getFakeMyTeams();
        if (libraryInfo) {
            //fakeMyFileFolders.setLibraryInfo(getMyFilesLibraryInfo());
        }
        return fakeFolder;
    }

    @GET
    @Path("/my_favorites")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public BinderBrief getMyFavorites(@QueryParam("library_info") @DefaultValue("false") boolean libraryInfo) {
        BinderBrief fakeFolder = getFakeMyFavorites();
        if (libraryInfo) {
            //fakeMyFileFolders.setLibraryInfo(getMyFilesLibraryInfo());
        }
        return fakeFolder;
    }

    private org.kablink.teaming.domain.MobileDevice toMobileDevice(MobileDevice mobileDevice) {
        validateMandatoryField(mobileDevice, "getId");
        org.kablink.teaming.domain.MobileDevice device = new org.kablink.teaming.domain.MobileDevice(getLoggedInUserId(), mobileDevice.getId());
//!     device.setPushToken(mobileDevice.getPushToken());
        device.setDescription(mobileDevice.getDescription());
        device.setLastLogin(new Date());
        device.setLastWipe(mobileDevice.getLastWipe());
        return device;
    }

    private SearchResultList<BinderBrief> _getMyFilesLibraryFolders(int descriptionFormat, Integer offset, Integer maxCount, Date parentModTime) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (descriptionFormat==Description.FORMAT_HTML) {
            nextParams.put("description_format", "html");
        } else {
            nextParams.put("description_format", "text");
        }
        Criteria crit = SearchUtils.getMyFilesSearchCriteria(this, getLoggedInUser().getWorkspaceId(), true, false, false, false);
        SearchResultList<BinderBrief> results = lookUpBinders(crit, descriptionFormat, offset, maxCount, "/self/my_files/library_folders", nextParams, parentModTime);
        setMyFilesParents(results);
        return results;
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
        root.add(SearchUtils.buildBindersCriterion());
        Junction ids = Restrictions.disjunction();
        for (Long id : myFilesFolderIds) {
            ids.add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
        }
        root.add(ids);
        Map map = getBinderModule().executeSearchQuery(root, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1, null);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), map);
        return results.getLastModified();
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
        crit.add(SearchUtils.buildEntriesCriterion());
        crit.add(SearchUtils.buildLibraryCriterion(true, Boolean.FALSE));
        Junction searchContexts = null;
        if (recursive) {
            SearchResultList<BinderBrief> folders = _getMyFilesLibraryFolders(Description.FORMAT_NONE, 0, -1, null);
            if (folders.getCount()>0) {
                searchContexts = Restrictions.disjunction();
                for (BinderBrief folder : folders.getResults()) {
                    searchContexts.add(SearchUtils.buildAncentryCriterion(folder.getId()));
                }
            }
        }
        Criteria myFiles = SearchUtils.getMyFilesSearchCriteria(this, getLoggedInUser().getWorkspaceId(), false, true, false, false);
        if (searchContexts!=null) {
            searchContexts.add(myFiles.asJunction());
            crit.add(searchContexts);
        } else {
            crit.add(myFiles.asJunction());
        }
        if (fileName!=null) {
            crit.add(SearchUtils.buildFileNameCriterion(fileName));
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
}
