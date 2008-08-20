/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.portlet.binder;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.search.filter.SearchFilterToSearchBooleanConverter;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.search.filter.SearchFilterToMapConverter;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;


/**
 * @author Peter Hurley
 *
 */
public class FilterController extends AbstractBinderController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));	
		String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
		User user = RequestContextHolder.getRequestContext().getUser();
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			//Parse the search filter
			SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
			Document searchFilter = requestParser.getSearchQuery();
			if (searchFilter != null) {
				UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				Map searchFilters = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
				if (searchFilters == null) searchFilters = new HashMap();
				searchFilters.put(SearchFilter.getFilterName(searchFilter), searchFilter);
				
				//Save the updated search filters
				getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
			}
			setupViewBinder(response, binderId, binderType);
		
		} else if (formData.containsKey("deleteBtn")) {
			//This is a request to delete a filter
			String selectedSearchFilter = PortletRequestUtils.getStringParameter(request, "selectedSearchFilter", "");
			if (!selectedSearchFilter.equals("")) {
				UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
				Map searchFilters = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
				if (searchFilters == null) searchFilters = new HashMap();
				if (searchFilters.containsKey(selectedSearchFilter)) {
					searchFilters.remove(selectedSearchFilter);
					//Save the updated search filters
					getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFilters);
				}
			}
			setupViewBinder(response, binderId, binderType);
		
		} else if (formData.containsKey("cancelBtn")) {
			//Go back to the "Add filter" page
			response.setRenderParameters(formData);
		
		} else if (formData.containsKey("closeBtn")) {
			setupViewBinder(response, binderId, binderType);
		
		} else {
			response.setRenderParameters(formData);
		}
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		
		//Get the name of the selected filter (if one is selected)
		String selectedSearchFilter = PortletRequestUtils.getStringParameter(request, "selectedSearchFilter", "");
		model.put(WebKeys.FILTER_SELECTED_FILTER_NAME, selectedSearchFilter);
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
		UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		Map searchFilters = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
		model.put(WebKeys.FILTER_SEARCH_FILTERS, searchFilters);
		Map searchFilterData = new HashMap();
		model.put(WebKeys.SEARCH_FILTER_MAP, searchFilterData);
		
		Workspace ws = getWorkspaceModule().getTopWorkspace();
		Document tree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);

		if (formData.containsKey("addBtn")) {
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER, model);
		} else if (formData.containsKey("modifyBtn") || formData.containsKey("deleteTerm")) {
			//Build a bean that contains all of the fields to be shown
			if (searchFilters.containsKey(selectedSearchFilter)) {
				SearchFilterToMapConverter searchFilterConverter = new SearchFilterToMapConverter(this, (Document)searchFilters.get(selectedSearchFilter));
				model.putAll(searchFilterConverter.convertAndPrepareFormData());
			}
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER, model);
		} else {
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER_SELECT, model);
		}
	}

}
