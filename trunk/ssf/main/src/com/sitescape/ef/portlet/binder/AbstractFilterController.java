package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletRequest;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.portlet.forum.SAbstractForumController;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.util.Validator;
import org.springframework.web.portlet.bind.PortletRequestBindingException;

/**
 * @author Peter Hurley
 *
 */
public abstract class AbstractFilterController extends SAbstractForumController {
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		User user = RequestContextHolder.getRequestContext().getUser();
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			//Parse the search filter
			Document searchFilter = FilterHelper.getSearchFilter(request);
			if (searchFilter != null) {
				UserProperties userForumProperties = getProfileModule().getUserFolderProperties(user.getId(), binderId);
				Map searchFilters = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
				if (searchFilters == null) searchFilters = new HashMap();
				searchFilters.put(FilterHelper.getFilterName(searchFilter), searchFilter);
				
				//Save the updated search filters
				getProfileModule().setUserFolderProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
			}
			
			setResponseOnClose(response, binderId);
		} else if (formData.containsKey("cancelBtn") || formData.containsKey("closeBtn")) {
			setResponseOnClose(response, binderId);
		} else
			response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		Binder binder = getBinderModule().getBinder(binderId);
		
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER_WORKFLOW_ASSOCIATIONS, binder.getProperty(ObjectKeys.BINDER_WORKFLOW_ASSOCIATIONS));
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
		DefinitionUtils.getDefinitions(model);
		DefinitionUtils.getDefinitions(binder, model);
		DefinitionUtils.getDefinitions(Definition.WORKFLOW, WebKeys.PUBLIC_WORKFLOW_DEFINITIONS, model);
	
		return new ModelAndView(WebKeys.VIEW_BUILD_FILTER, model);
	}
	protected abstract void setResponseOnClose(ActionResponse responose, Long binderId);


}
