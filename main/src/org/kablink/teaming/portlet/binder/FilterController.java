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
package org.kablink.teaming.portlet.binder;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterRequestParser;
import org.kablink.teaming.search.filter.SearchFilterToMapConverter;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.springframework.web.portlet.ModelAndView;


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
		String filterNameOriginal = PortletRequestUtils.getStringParameter(request, "filterNameOriginal", "");
		String globalOriginal = PortletRequestUtils.getStringParameter(request, "globalOriginal", "---");
		User user = RequestContextHolder.getRequestContext().getUser();
		Binder binder = getBinderModule().getBinder(binderId);
			
		//See if the form was submitted
		if (formData.containsKey("okBtn")) {
			//Parse the search filter
			SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
			Document searchFilter = requestParser.getSearchQuery();
			if (searchFilter != null) {
				if (SearchFilter.checkIfFilterGlobal(searchFilter)) {
					Map searchFiltersG = (Map)binder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS);
					if (searchFiltersG == null) searchFiltersG = new HashMap();
					searchFiltersG.put(SearchFilter.getFilterName(searchFilter), searchFilter.asXML());
					if (getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
						getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_FILTERS, searchFiltersG);
					}
					if (globalOriginal != null && !globalOriginal.equals("true")) {
						//This was a change from personal to global, delete the old one
						UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
						Map searchFiltersP = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
						if (searchFiltersP == null) searchFiltersP = new HashMap();
						if (searchFiltersP.containsKey(filterNameOriginal)) {
							searchFiltersP.remove(filterNameOriginal);
							//Save the updated search filters
							getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFiltersP);
						}
					}
					if (!filterNameOriginal.equals(SearchFilter.getFilterName(searchFilter))) {
						//The name was modified, delete the old filter
						if (searchFiltersG.containsKey(filterNameOriginal)) {
							searchFiltersG.remove(filterNameOriginal);
							if (getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
								getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_FILTERS, searchFiltersG);
							}
						}
					}
				} else {
					UserProperties userForumProperties = getProfileModule().getUserProperties(user.getId(), binderId);
					Map searchFiltersP = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
					if (searchFiltersP == null) searchFiltersP = new HashMap();
					searchFiltersP.put(SearchFilter.getFilterName(searchFilter), searchFilter.asXML());
					//Save the updated search filters
					getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFiltersP);

					//See if the filter was changed to global or if the name was changed
					if (globalOriginal != null && globalOriginal.equals("true")) {
						//This was a change from global to personal, delete the old one
						Map searchFiltersG = (Map)binder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS);
						if (searchFiltersG != null && searchFiltersG.containsKey(filterNameOriginal)) {
							searchFiltersG.remove(filterNameOriginal);
							if (getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
								getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_FILTERS, searchFiltersG);
							}
						}
					}
					if (!filterNameOriginal.equals(SearchFilter.getFilterName(searchFilter))) {
						//The name was modified, delete the old filter
						if (searchFiltersP.containsKey(filterNameOriginal)) {
							searchFiltersP.remove(filterNameOriginal);
							getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, searchFiltersP);
						}
					}
				}
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
		
		} else if (formData.containsKey("deleteBtnGlobal")) {
			//This is a request to delete a global filter
			String selectedSearchFilter = PortletRequestUtils.getStringParameter(request, "selectedSearchFilterGlobal", "");
			if (!selectedSearchFilter.equals("")) {
				Map searchFilters = (Map)binder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS);
				if (searchFilters == null) searchFilters = new HashMap();
				if (searchFilters.containsKey(selectedSearchFilter)) {
					searchFilters.remove(selectedSearchFilter);
					binder.setProperty(ObjectKeys.BINDER_PROPERTY_FILTERS, searchFilters);
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
		
		//Get the name of the selected filter (if one is selected)
		String selectedSearchFilter = PortletRequestUtils.getStringParameter(request, "selectedSearchFilter", "");
		if (!selectedSearchFilter.equals("")) model.put(WebKeys.FILTER_SELECTED_FILTER_NAME, selectedSearchFilter);
		model.put(WebKeys.BINDER, binder);
			
		UserProperties userFolderProperties = getProfileModule().getUserProperties(null, binderId);
		Map searchFilters = (Map)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
		if (searchFilters == null) searchFilters = BinderHelper.convertV1Filters(this, userFolderProperties);
		model.put(WebKeys.FILTER_SEARCH_FILTERS, searchFilters);
		Map searchFilterData = new HashMap();
		model.put(WebKeys.SEARCH_FILTER_MAP, searchFilterData);
		
		String selectedSearchFilterGlobal = PortletRequestUtils.getStringParameter(request, "selectedSearchFilterGlobal", "");
		if (!selectedSearchFilterGlobal.equals("")) model.put(WebKeys.FILTER_SELECTED_FILTER_NAME, selectedSearchFilterGlobal);
		Map globalSearchFilters = (Map)binder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS);
		if (globalSearchFilters == null) globalSearchFilters = new HashMap();
		model.put(WebKeys.FILTER_SEARCH_FILTERS_GLOBAL, globalSearchFilters);
		
		Workspace ws = getWorkspaceModule().getTopWorkspace();
		Document tree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);

		if (formData.containsKey("addBtn")) {
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER, model);
		} else if (formData.containsKey("modifyBtn") || formData.containsKey("modifyBtnGlobal") || 
				formData.containsKey("deleteTerm")) {
			//Build a bean that contains all of the fields to be shown
			String filter = null;
			if (formData.containsKey("modifyBtn")) {
				filter = (String)searchFilters.get(selectedSearchFilter);
			} else if (formData.containsKey("modifyBtnGlobal")) {
				filter = (String)globalSearchFilters.get(selectedSearchFilterGlobal);
				model.put(WebKeys.FILTER_SEARCH_FILTER_IS_GLOBAL, "true");
			}
			if (filter != null) {
				SearchFilterToMapConverter searchFilterConverter = new SearchFilterToMapConverter(this, DocumentHelper.parseText(filter));
				model.putAll(searchFilterConverter.convertAndPrepareFormData());
			}
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER, model);
		} else {
			return new ModelAndView(WebKeys.VIEW_BUILD_FILTER_SELECT, model);
		}
	}

}
