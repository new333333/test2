package org.kablink.teaming.remoting.rest.v1.resource;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.BinderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.Binder;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.util.api.ApiErrorCode;

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
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:08 PM
 */
abstract public class AbstractBinderResource extends AbstractDefinableEntityResource {

    /**
     * Returns the binder with the specified ID.
     * @param id    The ID of the binder to return.
     * @param includeAttachments    Configures whether attachments should be included in the returned binder object.
     * @return  Returns a subclass of Binder.
     */
    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Binder getBinder(@PathParam("id") long id,
                            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments) {
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments);
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void updateBinder(@PathParam("id") long id, Binder binder)
            throws WriteFilesException, WriteEntryDataException {
        _getBinder(id);
        getBinderModule().modifyBinder(id,
                         new RestModelInputData(binder), null, null, null);
    }

    /**
     * Deletes the specifed binder object.  The binder is moved into the Trash, not permanently deleted.
     * @param id    The ID of the binder to delete.
     */
    @DELETE
    public void deleteBinder(@PathParam("id") long id) {
        _deleteBinder(id);
    }

    /**
     * Gets a list of child binders contained in the specified binder.
     * @param id The id of the parent binder
     * @return Returns a list of BinderBrief objects.
     */
	@GET
	@Path("binders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubBinders(@PathParam("id") long id,
			@QueryParam("first") Integer offset,
			@QueryParam("count") Integer maxCount) {
        return getSubBinders(id, null, offset, maxCount, null);
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
	@Path("binders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Binder createSubBinder(@PathParam("id") long id, Binder binder, @QueryParam("template") Long templateId)
            throws WriteFilesException, WriteEntryDataException {
        return createBinder(id, binder, templateId);
	}

	@GET
	@Path("binder_tree")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public BinderTree getSubBinderTree(@PathParam("id") long id) {
        return getSubBinderTree(id, null);
	}

	// Read entries
	@GET
	@Path("files")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<FileProperties> getFiles(@PathParam("id") long id,
                                                  @QueryParam("recursive") @DefaultValue("false") boolean recursive,
                                                  @QueryParam("first") Integer offset,
                                                  @QueryParam("count") Integer maxCount) {
        return getSubFiles(id, recursive, offset, maxCount, null);
	}

    protected Binder createBinder(long parentId, Binder newBinder, Long templateId) throws WriteFilesException, WriteEntryDataException {
        _getBinder(parentId);
        if (newBinder.getTitle()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No binder title was supplied in the POST data.");
        }
        org.kablink.teaming.domain.Binder binder = BinderUtils.createBinder(parentId, newBinder.getTitle(), null, templateId);
        return ResourceUtil.buildBinder(binder, true);
    }

    protected void _deleteBinder(long id) {
        _getBinder(id);
        getBinderModule().preDeleteBinder(id, getLoggedInUserId());
    }

    protected SearchResultList<FileProperties> getSubFiles(long id, boolean recursive, Integer offset, Integer maxCount, String nextUrl) {
        _getBinder(id);
        Map<String,FileIndexData> files = (recursive) ?
                getFileModule().getChildrenFileDataFromIndexRecursively(id) : getFileModule().getChildrenFileDataFromIndex(id);
        SearchResultList<FileProperties> results = new SearchResultList<FileProperties>();
        results.setFirst(0);
        results.setCount(files.size());
        results.setTotal(files.size());
        for (FileIndexData file : files.values()) {
            results.append(ResourceUtil.buildFileProperties(file));
        }

        return results;
    }

    protected SearchResultList<BinderBrief> getSubBinders(long id, SearchFilter filter, Integer offset, Integer maxCount, String nextUrl) {
        org.kablink.teaming.domain.Binder workspace = _getBinder(id);
        Map<String, Object> options = new HashMap<String, Object>();
        if (filter!=null) {
            options.put( ObjectKeys.SEARCH_SEARCH_FILTER, filter.getFilter() );
        }
        if (offset!=null) {
            options.put(ObjectKeys.SEARCH_OFFSET, offset);
        } else {
            offset = 0;
        }
        if (maxCount!=null) {
            options.put(ObjectKeys.SEARCH_MAX_HITS, maxCount);
        }
        Map resultMap = getBinderModule().getBinders(workspace, options);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>(offset);
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), resultMap, nextUrl, offset);
        return results;
    }

    protected BinderTree getSubBinderTree(long id, SearchFilter filter) {
        org.kablink.teaming.domain.Binder workspace = _getBinder(id);
        Map<String, Object> options = new HashMap<String, Object>();
        if (filter!=null) {
            options.put( ObjectKeys.SEARCH_SEARCH_FILTER, filter.getFilter() );
        }
        Map resultMap = getBinderModule().getBindersRecursively(workspace, options);
        BinderTree results = new BinderTree();
        SearchResultBuilderUtil.buildSearchResultsTree(results, id, new BinderBriefBuilder(), resultMap);
        results.setItem(null);
        return results;
    }

    protected org.kablink.teaming.domain.Binder _getBinder(long id) {
        try{
            return getBinderModule().getBinder(id);
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.BINDER_NOT_FOUND, "NOT FOUND");
    }
}
