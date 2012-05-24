package org.kablink.teaming.remoting.rest.v1.resource;

import org.dom4j.Document;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.BinderTree;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTree;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.web.tree.WebSvcTreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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
public class AbstractBinderResource extends AbstractResource {

    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public org.kablink.teaming.rest.v1.model.Binder getBinder(@PathParam("id") long id,
                                                              @QueryParam("include_attachments") @DefaultValue("false") boolean includeAttachments) {
        return ResourceUtil.buildBinder(_getBinder(id), includeAttachments);
    }


    // Read all subbinders
	@GET
	@Path("binders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubBinders(@PathParam("id") long id) {
        return getSubBinders(id, null);
	}

	@GET
	@Path("binder_tree")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public BinderTree getSubBinderTree(@PathParam("id") long id) {
        return getSubBinderTree(id, null);
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

    protected Binder _getBinder(long id) {
        try{
            return getBinderModule().getBinder(id);
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.BINDER_NOT_FOUND, "NOT FOUND");
    }
}
