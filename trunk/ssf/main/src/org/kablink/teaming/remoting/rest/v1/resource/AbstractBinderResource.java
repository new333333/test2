package org.kablink.teaming.remoting.rest.v1.resource;

import com.sun.jersey.api.core.InjectParam;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.BinderBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.SearchResults;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * User: david
 * Date: 5/21/12
 * Time: 2:08 PM
 */
public class AbstractBinderResource {
    @InjectParam("binderModule") protected BinderModule binderModule;

    @GET
   	@Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public org.kablink.teaming.rest.v1.model.Binder getBinder(@PathParam("id") long id) {
        return ResourceUtil.buildBinder(_getBinder(id));
    }


    // Read all subbinders
	@GET
	@Path("binders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResults<BinderBrief> getSubBinders(@PathParam("id") long id) {
        return getSubBinders(id, null);
	}

    protected SearchResults<BinderBrief> getSubBinders(long id, SearchFilter filter) {
        org.kablink.teaming.domain.Binder workspace = _getBinder(id);
        Map<String, Object> options = new HashMap<String, Object>();
        if (filter!=null) {
            options.put( ObjectKeys.SEARCH_SEARCH_FILTER, filter.getFilter() );
        }
        Map resultMap = binderModule.getBinders(workspace, options);
        SearchResults<BinderBrief> results = new SearchResults<BinderBrief>();
        SearchResultBuilderUtil.buildSearchResults(results, new BinderBriefBuilder(), resultMap);
        return results;
    }

    protected Binder _getBinder(long id) {
        try{
            return binderModule.getBinder(id);
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.BINDER_NOT_FOUND, "NOT FOUND");
    }
}
