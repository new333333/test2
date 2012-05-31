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

    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Binder getBinder(@PathParam("id") long id,
                            @QueryParam("include_attachments") @DefaultValue("false") boolean includeAttachments) {
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments);
    }

    @DELETE
    public void deleteBinder(@PathParam("id") long id) {
        _deleteBinder(id);
    }

    // Read all subbinders
	@GET
	@Path("binders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubBinders(@PathParam("id") long id) {
        return getSubBinders(id, null);
	}

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
                                                  @QueryParam("recursive") @DefaultValue("false") boolean recursive) {
        return getSubFiles(id, recursive);
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

    protected SearchResultList<FileProperties> getSubFiles(long id, boolean recursive) {
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

    protected SearchResultList<BinderBrief> getSubBinders(long id, SearchFilter filter) {
        org.kablink.teaming.domain.Binder workspace = _getBinder(id);
        Map<String, Object> options = new HashMap<String, Object>();
        if (filter!=null) {
            options.put( ObjectKeys.SEARCH_SEARCH_FILTER, filter.getFilter() );
        }
        Map resultMap = getBinderModule().getBinders(workspace, options);
        SearchResultList<BinderBrief> results = new SearchResultList<BinderBrief>();
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), resultMap);
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
