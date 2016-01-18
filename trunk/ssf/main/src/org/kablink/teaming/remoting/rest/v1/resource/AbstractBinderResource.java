package org.kablink.teaming.remoting.rest.v1.resource;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.BinderChange;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoTagByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.BinderUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.remoting.rest.v1.exc.*;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.FilePropertiesBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.remoting.rest.v1.util.UniversalBuilder;
import org.kablink.teaming.rest.v1.model.Access;
import org.kablink.teaming.rest.v1.model.BaseBinderChange;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderChanges;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.Folder;
import org.kablink.teaming.rest.v1.model.LibraryInfo;
import org.kablink.teaming.rest.v1.model.ParentBinder;
import org.kablink.teaming.rest.v1.model.PrincipalBrief;
import org.kablink.teaming.rest.v1.model.RecentActivityEntry;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchableObject;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.rest.v1.model.TeamMember;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.Pair;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Order;
import org.kablink.util.search.Restrictions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Locale;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:08 PM
 */
abstract public class AbstractBinderResource extends AbstractDefinableEntityResource {

    abstract protected String getBasePath();
    
    /**
     * Returns the binder with the specified ID.
     * @param id    The ID of the binder to return.
     * @param includeAttachments    Configures whether attachments should be included in the returned binder object.
     * @return  Returns a subclass of Binder.
     */
    @GET
    @Path("{id}")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Binder getBinder(@PathParam("id") long id,
                            @QueryParam("library_info") @DefaultValue("false") boolean libraryInfo,
                            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                            @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        Binder model = ResourceUtil.buildBinder(binder, includeAttachments, toDomainFormat(descriptionFormatStr));
        if (libraryInfo) {
            model.setLibraryInfo(getLibraryInfo(new Long[]{id}, binder.isMirrored()));
        }
        return model;
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Binder updateBinder(@PathParam("id") long id, Binder binder,
                               @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                               @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        _getBinder(id);
        getBinderModule().modifyBinder(id,
                new RestModelInputData(binder), null, null, null);
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments, toDomainFormat(descriptionFormatStr));
    }

    /**
     * Deletes the specifed binder object.  The binder is moved into the Trash, not permanently deleted.
     * @param id    The ID of the binder to delete.
     */
    @DELETE
    @Path("{id}")
    public void deleteBinder(@PathParam("id") long id,
                             @QueryParam("only_if_empty") @DefaultValue("false") boolean onlyIfEmpty,
                             @QueryParam("purge") @DefaultValue("false") boolean purge) throws Exception {
        _deleteBinder(id, onlyIfEmpty, purge);
    }

    @GET
    @Path("{id}/access")
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Access getAccessRole(@PathParam("id") long id) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        return getAccessRole(binder);
    }

    @POST
    @Path("{id}/title")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Binder renameBinder(@PathParam("id") Long id,
                                     @FormParam("title") String name,
                                     @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                                     @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        if (name==null || name.length()==0) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'title' form parameter");
        }

        Map data = new HashMap();
        data.put("title", Normalizer.normalize(name, Normalizer.Form.NFC));
        InputDataAccessor inputData = new MapInputData(data);

        getBinderModule().modifyBinder(id, inputData, null, null, null);

        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments, toDomainFormat(descriptionFormatStr));
    }

    @POST
    @Path("{id}/parent_binder")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Binder moveBinder(@PathParam("id") Long id,
                             @FormParam("binder_id") Long newBinderId,
                             @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments,
                             @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        if (newBinderId ==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'binder_id' form parameter");
        }
        Long finalParentId = null;
        if (newBinderId.equals(ObjectKeys.MY_FILES_ID)) {
            finalParentId = newBinderId;
            if (SearchUtils.useHomeAsMyFiles(this)) {
                newBinderId = SearchUtils.getHomeFolderId(this);
            } else {
                newBinderId = getLoggedInUser().getWorkspaceId();
            }
        }

        org.kablink.teaming.domain.Binder parentBinder = _getBinderImpl(newBinderId);
        org.kablink.teaming.domain.Binder newBinder = getBinderModule().moveBinder(binder.getId(), parentBinder.getId(), null);

        Binder modifiedBinder = ResourceUtil.buildBinder(newBinder, includeAttachments, toDomainFormat(descriptionFormatStr));
        if (finalParentId!=null) {
            modifiedBinder.setParentBinder(new ParentBinder(ObjectKeys.MY_FILES_ID, "/self/my_files"));
        }
        return modifiedBinder;
    }

    /**
     * Creates a new binder as a child of the specified binder.
     * @param id    The id of the binder where the new binder will be created.
     * @param binder    An object containing the information about the binder to be created.  Requires the <code>title</code>
     *                  property to be set.  All other binder properties are ignored.
     * @param templateId    Optional template ID defining the type of binder to create.  If no template is specified, the
     *                      new parent binder is used to determine the type of binder to create.
     * @return Returns a Binder object representing the newly created binder.
     */
	@POST
	@Path("{id}/binders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Binder createSubBinder(@PathParam("id") long id, Binder binder,
                                  @QueryParam("template") Long templateId,
                                  @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        return createBinder(id, binder, templateId, toDomainFormat(descriptionFormatStr));
	}

	@GET
	@Path("{id}/binder_tree")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public BinderTree getSubBinderTree(@PathParam("id") long id,
                                       @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        return getSubBinderTree(id, null, toDomainFormat(descriptionFormatStr));
	}

	@GET
	@Path("{id}/library_tree")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public BinderTree getLibraryTree(@PathParam("id") long id,
                                     @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        return getSubBinderTree(id, SearchUtils.buildLibraryTreeCriterion(), toDomainFormat(descriptionFormatStr));
	}

    @GET
    @Path ("{id}/library_changes")
    public BinderChanges getLibraryChildrenChanges(@PathParam("id") long id,
                                              @QueryParam("since") String since,
                                              @QueryParam("recursive") @DefaultValue("true") boolean recursive,
                                              @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                              @QueryParam ("count") @DefaultValue("500") Integer maxCount) {
        return getBinderChanges(new Long [] {id}, null, since, recursive, descriptionFormatStr, maxCount, getBasePath() + id + "/library_changes");
    }

    @GET
   	@Path("{id}/library_children")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public Response getLibraryChildren(@PathParam("id") long id,
                                       @QueryParam("title") String name,
                                       @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                       @QueryParam("allow_jits") @DefaultValue("true") Boolean allowJits,
                                       @QueryParam("first") @DefaultValue("0") Integer offset,
                                       @QueryParam("count") @DefaultValue("100") Integer maxCount,
                                       @Context HttpServletRequest request) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        Date lastModified = getLibraryModifiedDate(new Long[]{id}, false, allowJits);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<SearchableObject> children = getChildren(id, SearchUtils.buildLibraryCriterion(true, true), name, true, false, true,
                allowJits, offset, maxCount, getBasePath() + id + "/library_children", nextParams,
                toDomainFormat(descriptionFormatStr), null);
        return Response.ok(children).lastModified(lastModified).build();
   	}

    @GET
   	@Path("{id}/library_folders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public Response getLibraryFolders(@PathParam("id") long id,
                                         @QueryParam("title") String name,
                                         @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                                         @QueryParam("first") @DefaultValue("0") Integer offset,
                                         @QueryParam("count") @DefaultValue("100") Integer maxCount,
                                         @Context HttpServletRequest request) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("description_format", descriptionFormatStr);
        Date lastModified = getLibraryModifiedDate(new Long[]{id}, false, true);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<BinderBrief> subBinders = getSubBinders(id, SearchUtils.libraryFolders(), name, true, offset, maxCount,
                getBasePath() + id + "/library_folders", nextParams, toDomainFormat(descriptionFormatStr), ifModifiedSince);
        return Response.ok(subBinders).lastModified(lastModified).build();
   	}

    @POST
    @Path("{id}/library_folders")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Folder copyFolder(@PathParam("id") long parentId,
                             @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                             @FormParam("title") String title,
                             @FormParam("source_id") Long sourceId) {
        _getBinder(parentId);
        if (title==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No title parameter was supplied in the POST data.");
        }
        if (sourceId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No source_id parameter was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Folder source = _getFolder(sourceId);
        if (BinderHelper.isBinderHomeFolder(source)) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Copying a home folder is not supported");
        }
        title = Normalizer.normalize(title, Normalizer.Form.NFC);
        Map options = new HashMap();
        options.put(ObjectKeys.INPUT_OPTION_REQUIRED_TITLE, title);
        try {
            org.kablink.teaming.domain.Binder binder = getBinderModule().copyBinder(sourceId, parentId, true, options);
            return (Folder) ResourceUtil.buildBinder(binder, true, toDomainFormat(descriptionFormatStr));
        } catch (TitleException e) {
            Binder data = null;
            try {
                org.kablink.teaming.domain.Binder binder = getFolderByName(parentId, title);
                if (binder!=null) {
                    data = ResourceUtil.buildBinder(binder, true, toDomainFormat(descriptionFormatStr));
                }
            } catch (AccessControlException e1) {
            }
            throw new RestExceptionWrapper(e, e, e, data);
        }
    }

    @POST
   	@Path("{id}/library_folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public Folder createLibraryFolder(@PathParam("id") long id,
                                      org.kablink.teaming.rest.v1.model.BinderBrief binder,
                                      @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        return (Folder) _createLibraryFolder(id, binder, toDomainFormat(descriptionFormatStr));
   	}

    // Read entries
	@GET
	@Path("{id}/library_files")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getLibraryFiles(@PathParam("id") long id,
                                                  @QueryParam("file_name") String fileName,
                                                  @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                  @QueryParam("first") Integer offset,
                                                  @QueryParam("count") Integer maxCount,
                                                  @Context HttpServletRequest request) {
        if (!recursive && maxCount==null) {
            maxCount = 100;
        }
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (fileName!=null) {
            nextParams.put("file_name", fileName);
        }
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        Date lastModified = getLibraryModifiedDate(new Long[]{id}, recursive, true);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> subFiles = getSubFiles(id, fileName, recursive, true, includeParentPaths,
                offset, maxCount, getBasePath() + id + "/library_files", nextParams, ifModifiedSince);
        return Response.ok(subFiles).lastModified(lastModified).build();
	}

    // Read entries
	@GET
	@Path("{id}/files")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getFiles(@PathParam("id") long id,
                                                     @QueryParam("file_name") String fileName,
                                                     @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                  @QueryParam("first") Integer offset,
                                                  @QueryParam("count") @DefaultValue("100") Integer maxCount,
                                                  @Context HttpServletRequest request) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        if (fileName!=null) {
            nextParams.put("file_name", fileName);
        }
        nextParams.put("recursive", Boolean.toString(recursive));
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        Date lastModified = getLibraryModifiedDate(new Long[]{id}, recursive, true);
        Date ifModifiedSince = getIfModifiedSinceDate(request);
        if (ifModifiedSince!=null && lastModified!=null && !ifModifiedSince.before(lastModified)) {
            throw new NotModifiedException();
        }
        SearchResultList<FileProperties> subFiles = getSubFiles(id, fileName, recursive, false, includeParentPaths,
                offset, maxCount, getBasePath() + id + "/files", nextParams, ifModifiedSince);
        return Response.ok(subFiles).lastModified(lastModified).build();
	}

	@GET
	@Path("{id}/library_info")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public LibraryInfo getBinderLibraryInfo(@PathParam("id") long id) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        return getLibraryInfo(new Long[] {id}, binder.isMirrored());
	}

    @GET
    @Path("{id}/recent_activity")
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public SearchResultList<RecentActivityEntry> getRecentActivity(@PathParam("id") Long id,
                                                                @QueryParam("parent_binder_paths") @DefaultValue("false") boolean includeParentPaths,
                                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr,
                @QueryParam("first") @DefaultValue("0") Integer offset,
                @QueryParam("count") @DefaultValue("20") Integer maxCount) {
        Map<String, Object> nextParams = new HashMap<String, Object>();
        nextParams.put("parent_binder_paths", Boolean.toString(includeParentPaths));
        nextParams.put("description_format", descriptionFormatStr);

        List<String> binders = new ArrayList<String>();
        binders.add(id.toString());
        Criteria criteria = SearchUtils.entriesForTrackedPlacesEntriesAndPeople(this, binders, null, null, true, Constants.LASTACTIVITY_FIELD);
        return _getRecentActivity(includeParentPaths, toDomainFormat(descriptionFormatStr), offset, maxCount, criteria,
                this.getBasePath() + id + "/recent_activity", nextParams);
    }

    @GET
    @Path("{id}/shares")
    public SearchResultList<Share> getShares(@PathParam("id") Long id) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        spec.setSharerId(getLoggedInUserId());
        spec.setLatest(true);
        spec.setSharedEntityIdentifier(new EntityIdentifier(id, binder.getEntityType()));
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
                             Share share) {
        return shareEntity(_getBinder(id), share, notifyRecipient, null);
    }

    @GET
   	@Path("{id}/team_members")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public SearchResultList<TeamMember> getTeamMembers(@PathParam("id") long id,
            @QueryParam("expand_groups") @DefaultValue("false") boolean expandGroups) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        SortedSet<Principal> teamMembers = getBinderModule().getTeamMembers(binder, expandGroups);
        SearchResultList<TeamMember> results = new SearchResultList<TeamMember>();
        for (Principal principal : teamMembers) {
            results.append(ResourceUtil.buildTeamMember(id, principal));
        }
        return results;
    }

    @POST
   	@Path("{id}/team_members")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public TeamMember addTeamMember(@PathParam("id") long id, PrincipalBrief principal) {
        _getBinder(id);
        Principal member = getProfileModule().getEntry(principal.getId());
        Set<Long> teamMembers = getBinderModule().getTeamMemberIds(id, false);
        if (!teamMembers.contains(principal.getId())) {
            teamMembers.add(principal.getId());
            getBinderModule().setTeamMembers(id, teamMembers);
        }
        return ResourceUtil.buildTeamMember(id, member);
    }

    @DELETE
   	@Path("{id}/team_members")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public void clearTeamMembers(@PathParam("id") long id) {
        _getBinder(id);
        getBinderModule().setTeamMembershipInherited(id, true);
    }

    @DELETE
   	@Path("{id}/team_members/{memberId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public void deleteTeamMember(@PathParam("id") long id, @PathParam("memberId") long memberId) {
        _getBinder(id);
        Set<Long> teamMembers = getBinderModule().getTeamMemberIds(id, false);
        if (teamMembers.contains(memberId)) {
            teamMembers.remove(memberId);
            getBinderModule().setTeamMembers(id, teamMembers);
        }
        teamMembers = getBinderModule().getTeamMemberIds(id, false);
        logger.debug("");
    }

    @GET
    @Path("{id}/tags")
    public SearchResultList<Tag> getTags(@PathParam("id") Long id) {
        org.kablink.teaming.domain.Binder entry = _getBinder(id);
        return getBinderTags(entry, false);
    }

    @POST
    @Path("{id}/tags")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SearchResultList<Tag> addTag(@PathParam("id") Long id, Tag tag) {
        _getBinder(id);
        org.kablink.teaming.domain.Tag[] tags = getBinderModule().setTag(id, tag.getName(), tag.isPublic());
        SearchResultList<Tag> results = new SearchResultList<Tag>();
        for (org.kablink.teaming.domain.Tag tg : tags) {
            results.append(ResourceUtil.buildTag(tg));
        }
        return results;
    }

    @DELETE
    @Path("{id}/tags")
    public void deleteTags(@PathParam("id") Long id) {
        org.kablink.teaming.domain.Binder entry = _getBinder(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getBinderModule().getTags(entry);
        for (org.kablink.teaming.domain.Tag tag : tags) {
            getBinderModule().deleteTag(id, tag.getId());
        }
    }

    @GET
    @Path("{id}/tags/{tagId}")
    public Tag getTag(@PathParam("id") Long id, @PathParam("tagId") String tagId) {
        org.kablink.teaming.domain.Binder entry = _getBinder(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getBinderModule().getTags(entry);
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

    protected Binder _createLibraryFolder(long parentId, BinderBrief newBinder, int descriptionFormat) throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.Binder parent = _getBinder(parentId);
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
            return ResourceUtil.buildBinder(binder, true, descriptionFormat);
        } catch (TitleException e) {
            Binder data = null;
            try {
                org.kablink.teaming.domain.Binder binder = getFolderByName(parentId, newBinder.getTitle());
                if (binder!=null) {
                    data = ResourceUtil.buildBinder(binder, true, descriptionFormat);
                }
            } catch (AccessControlException e1) {
            }
            throw new RestExceptionWrapper(e, e, e, data);
        }
    }

    protected Binder createBinder(long parentId, Binder newBinder, Long templateId, int descriptionFormat) throws WriteFilesException, WriteEntryDataException {
        _getBinderImpl(parentId);
        if (newBinder.getTitle()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No binder title was supplied in the POST data.");
        }
        newBinder.setTitle(Normalizer.normalize(newBinder.getTitle(), Normalizer.Form.NFC));
        org.kablink.teaming.domain.Binder binder = BinderUtils.createBinder(parentId, newBinder.getTitle(), null, templateId);
        return ResourceUtil.buildBinder(binder, true, descriptionFormat);
    }

    protected void _deleteBinder(long id, boolean onlyIfEmpty, boolean purge) throws Exception {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);
        if (binder.isMirrored() && (binder instanceof org.kablink.teaming.domain.Folder) &&
                ((org.kablink.teaming.domain.Folder)binder).isTop()) {
            throw new BadRequestException(ApiErrorCode.NOT_SUPPORTED, "The folder is a top-level net or home folder and cannot be deleted in this manner.");
        }
        if (onlyIfEmpty && hasChildren(id, true)) {
            throw new ConflictException(ApiErrorCode.BINDER_NOT_EMPTY, "The binder is not empty.");
        }
        if (purge || binder.isMirrored()) {
            getBinderModule().deleteBinder(id);
        } else {
            TrashHelper.preDeleteBinder(this, id);
        }
    }

    protected SearchResultList<FileProperties> getSubFiles(long id, String fileName, boolean recursive, boolean onlyLibraryFiles, boolean includeParentPaths,
                                                           Integer offset, Integer maxCount, String nextUrl, Map<String, Object> nextParams,
                                                           Date modifiedSince) {
        org.kablink.teaming.domain.Binder binder = _getBinder(id);

        Junction criterion = Restrictions.conjunction();
        criterion.add(SearchUtils.buildEntriesCriterion());
        criterion.add(SearchUtils.buildSearchBinderCriterion(id, recursive));
        if (onlyLibraryFiles) {
            criterion.add(SearchUtils.buildLibraryCriterion(onlyLibraryFiles, Boolean.FALSE));
        }
        if (fileName!=null) {
            criterion.add(SearchUtils.buildFileNameCriterion(fileName));
        }
        if (offset==null) {
            offset = 0;
        }
        if (maxCount==null) {
            maxCount = Integer.MAX_VALUE;
        }
        Map resultsMap = getBinderModule().executeSearchQuery(new Criteria().add(criterion), Constants.SEARCH_MODE_NORMAL, offset, maxCount, null);
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>(0, binder.getModificationDate());
        SearchResultBuilderUtil.buildSearchResults(results, new FilePropertiesBuilder(this), resultsMap, nextUrl, nextParams, offset);

        if (modifiedSince!=null && results.getLastModified()!=null && !modifiedSince.before(results.getLastModified())) {
            throw new NotModifiedException();
        }

        if (includeParentPaths) {
            populateParentBinderPaths(results);
        }

        return results;
    }

    protected SearchResultList<BinderBrief> getSubBinders(long id, Criterion filter, String name, boolean allowJits, Integer offset, Integer maxCount,
                                                          String nextUrl, Map<String, Object> nextParams, int descriptionFormat,
                                                          Date modifiedSince) {
        SearchResultList<SearchableObject> results = getChildren(id, filter, name, true, false, false, allowJits, offset,
                maxCount, nextUrl, nextParams, descriptionFormat, modifiedSince);
        SearchResultList<BinderBrief> binderResults = new SearchResultList<BinderBrief>(offset, results.getLastModified());
        binderResults.setFirst(results.getFirst());
        binderResults.setTotal(results.getTotal());
        binderResults.setNext(results.getNext());
        for (SearchableObject obj : results.getResults()) {
            if (obj instanceof BinderBrief) {
                binderResults.append((BinderBrief) obj);
            }
        }
        return binderResults;
    }

    protected BinderTree getSubBinderTree(long id, Criterion filter, int descriptionFormat) {
        _getBinder(id);
        Criteria crit = new Criteria();
        if (filter!=null) {
            crit.add(filter);
        }
        crit.add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
        crit.add(SearchUtils.buildAncentryCriterion(id));
        Map resultMap = getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, -1, null);
        BinderTree results = new BinderTree();
        SearchResultBuilderUtil.buildSearchResultsTree(this, results, id, new BinderBriefBuilder(descriptionFormat), resultMap);
        results.setItem(null);
        return results;
    }

}
