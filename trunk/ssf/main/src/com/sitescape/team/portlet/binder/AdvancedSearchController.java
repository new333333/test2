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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.TreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.search.Constants;

/**
 * @author Renata Nowicka
 *
 */

public class AdvancedSearchController extends AbstractBinderController {
	
	public static final String NEW_TAB_VALUE = "1";
		
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		
		// set form data for the render method
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		//ajax requests
		if (op.equals(WebKeys.OPERATION_FIND_TAG_WIDGET)) {
			return ajaxGetTags(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_WORKFLOWS_WIDGET)) {
			return ajaxGetWorkflows(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_WORKFLOW_STEP_WIDGET)) {
			return ajaxGetWorkflowSteps(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_TYPES_WIDGET)) {
			return ajaxGetEntryTypes(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_FIELDS_WIDGET)) {
			return ajaxGetEntryFields(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_USERS_WIDGET)) {
			return ajaxGetUsers(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_GROUPS_WIDGET)) {
			return ajaxGetGroups(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_TEAMS_WIDGET)) {
			return ajaxGetTeams(request, response);
		}
		Map<String,Object> model = new HashMap();
		//Set up the standard beans
		BinderHelper.setupStandardBeans(this, request, response, model);

		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);
		
		String strBinderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		model.put(WebKeys.BINDER_ID, strBinderId);
		Long binderId = null;
		if (!strBinderId.equals("")) {
			binderId = Long.valueOf(strBinderId);
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
		}
        
        // this is necessary for the breadcrumbs and places choose
        Workspace top = getWorkspaceModule().getTopWorkspace();
		BinderHelper.buildNavigationLinkBeans(this, top, model);
		model.put(WebKeys.DEFINITION_ENTRY, top);

		model.put(WebKeys.SEARCH_TOP_FOLDER_ID, Collections.singletonList(top.getId().toString()));
        Tabs tabs = Tabs.getTabs(request);
		model.put(WebKeys.TABS, tabs);
		
		/** Vertical mode has been removed
		if (ObjectKeys.USER_DISPLAY_STYLE_VERTICAL.equals(RequestContextHolder.getRequestContext().getUser().getDisplayStyle())) {
			model.put(WebKeys.FOLDER_ACTION_VERTICAL_OVERRIDE, "yes");
		}
		*/

       if (op.equals(WebKeys.SEARCH_RESULTS)) {
        	model.putAll(BinderHelper.prepareSearchResultData(this, request, tabs));
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);

    		//Build a reload url
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

    		return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else if (op.equals(WebKeys.SEARCH_VIEW_PAGE)) {
        	model.putAll(BinderHelper.prepareSearchResultPage(this, request, tabs));
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);
        	
    		//Build a reload url
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

        	return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else if (op.equals(WebKeys.SEARCH_SAVED_QUERY)) {
        	model.putAll(BinderHelper.prepareSavedQueryResultData(this, request, tabs));
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);
        	
    		//Build a reload url
     		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

        	return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else {
        	model.putAll(BinderHelper.prepareSearchFormData(this, request));
        	if (binderId != null) {
        		Map options = new HashMap();
        		options.put("search_currentAndSubfolders", Boolean.TRUE);
        		options.put("search_subfolders", Boolean.TRUE);
        		options.put("searchFolders", Collections.singletonList(binderId));
        		model.put(WebKeys.SEARCH_FILTER_MAP, options);
        	}
        	
    		//Build a reload url
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

        	return new ModelAndView("search/search_form", model);
        }
	}
	
	public void addPropertiesForFolderView(Map model) {
    	User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		model.put(WebKeys.SEEN_MAP, getProfileModule().getUserSeenMap(user.getId()));
    	
		Definition def = getDefinitionModule().addDefaultDefinition(Definition.FOLDER_VIEW);
		DefinitionHelper.getDefinition(def, model, "//item[@name='forumView']");
    	model.put(WebKeys.SHOW_SEARCH_RESULTS, true);
    	
		Workspace ws = getWorkspaceModule().getTopWorkspace();
		Document tree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);
	}	

	
	protected void buildToolbars(Map model, RenderRequest request) {
		model.put(WebKeys.FOOTER_TOOLBAR,  buildFooterToolbar(model, request).getToolbar());
	}
	
	private Toolbar buildFooterToolbar(Map model, RenderRequest request) {
		Toolbar footerToolbar = new Toolbar();
		String[] contributorIds = collectContributorIds((List)model.get(WebKeys.FOLDER_ENTRYPEOPLE + "_all"));

		addClipboardOption(footerToolbar, contributorIds);
		addStartMeetingOption(footerToolbar, request, contributorIds);
		return footerToolbar;
	}

	private void addStartMeetingOption(Toolbar footerToolbar, RenderRequest request, String[] contributorIds) {
		if (getIcBrokerModule().isEnabled()) {
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			Map qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);		
			qualifiers.put("post", Boolean.TRUE);
			qualifiers.put("postParams", Collections.singletonMap(WebKeys.USER_IDS_TO_ADD, contributorIds));
		
			footerToolbar.addToolbarMenu("addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
		}
	}
	
	private void addClipboardOption(Toolbar toolbar, String[] contributorIds) {
		// clipboard
		Map qualifiers = new HashMap();
		String contributorIdsAsJSString = "";
		for (int i = 0; i < contributorIds.length; i++) {
			contributorIdsAsJSString += contributorIds[i];
			if (i < (contributorIds.length -1)) {
				contributorIdsAsJSString += ", ";	
			}
		}
		qualifiers.put("onClick", "ss_muster.showForm('" + Clipboard.USERS + "', [" + contributorIdsAsJSString + "]);return false;");
		toolbar.addToolbarMenu("clipboard", NLT.get("toolbar.menu.clipboard"), "#", qualifiers);
	}
	
	private String[] collectContributorIds(List entries) {
		Set principals = new HashSet();
		
		if (entries != null) {
			Iterator entriesIt = entries.iterator();
			while (entriesIt.hasNext()) {
				Map entry = (Map)entriesIt.next();
				Principal principal = (Principal)entry.get(WebKeys.USER_PRINCIPAL);
				principals.add(principal.getId().toString());
			}	
		}
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}
	private ModelAndView ajaxGetTags(RenderRequest request, RenderResponse response) {
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "tags");
		String pagerText = PortletRequestUtils.getStringParameter(request, "pager", "");
		
		Map model = new HashMap();
		List tags = new ArrayList();
			
		if (WebHelper.isUserLoggedIn(request)) {
			int startPageNo = 1;
			int endPageNo = 10;
			if (!pagerText.equals("")) {
				String[] pagesNos = pagerText.split(";");
				startPageNo = Integer.parseInt(pagesNos[0]);
				endPageNo = Integer.parseInt(pagesNos[1]);
			}
			
			String wordRoot = searchText;
			int i = wordRoot.indexOf("*");
			if (i > 0) wordRoot = wordRoot.substring(0, i);
			
			tags = getBinderModule().getSearchTags(wordRoot, findType);
			int searchCountTotal = tags.size();
			
			if (tags.size() > startPageNo) {
				if (tags.size() < endPageNo) endPageNo = tags.size();
				tags = tags.subList(startPageNo - 1, endPageNo);
			}
			
			if (endPageNo < searchCountTotal) {
				Map next = new HashMap();
				next.put("start", endPageNo + 1);
				if (endPageNo + 10 < searchCountTotal) {
					next.put("end",  endPageNo + 10);
				} else {
					next.put("end",  searchCountTotal);
				}
				model.put(WebKeys.PAGE_NEXT, next);
			}
	
			if (startPageNo > 1) {
				Map prev = new HashMap();
				prev.put("start", startPageNo - 10);
				prev.put("end", startPageNo - 1);
				model.put(WebKeys.PAGE_PREVIOUS, prev);
			}
		
		}
		model.put(WebKeys.TAGS, tags);
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_tags_widget", model);
	}
	

	private ModelAndView ajaxGetWorkflows(RenderRequest request, RenderResponse response) {
		
		Set workflows = new HashSet();
		if (WebHelper.isUserLoggedIn(request)) {
			Collection<Long> ids = TreeHelper.getSelectedIds(request.getParameterMap());
			for (Long id:ids) {
				try {
					workflows.addAll(getDefinitionModule().getDefinitions(id, Boolean.TRUE, Definition.WORKFLOW));
				} catch (Exception ex) {}
			}
			if (workflows.isEmpty()) workflows.addAll(getDefinitionModule().getDefinitions(null, Boolean.TRUE, Definition.WORKFLOW));
		}
		
		Map model = new HashMap();
		model.put(WebKeys.WORKFLOW_DEFINITION_MAP, DefinitionHelper.orderDefinitions(workflows, false));
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_workflows_widget", model);
	}
	
	private ModelAndView ajaxGetWorkflowSteps(RenderRequest request, RenderResponse response) {
		String workflowId = PortletRequestUtils.getStringParameter(request, "workflowId", "");
		Map model = new HashMap();
		
		Map stateData = new HashMap();
		if (WebHelper.isUserLoggedIn(request)) {
			stateData = getDefinitionModule().getWorkflowDefinitionStates(workflowId);
		}
		model.put(WebKeys.WORKFLOW_DEFINTION_STATE_DATA, stateData);
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_workflow_steps_widget", model);
	}
	private ModelAndView ajaxGetEntryTypes(RenderRequest request, RenderResponse response) {
		
		Map model = new HashMap();
		Set entries = new HashSet();
		if (WebHelper.isUserLoggedIn(request)) {
			Collection<Long> ids = TreeHelper.getSelectedIds(request.getParameterMap());
			for (Long id:ids) {
				try {
					entries.addAll(getDefinitionModule().getDefinitions(id, Boolean.TRUE, Definition.FOLDER_ENTRY));
				} catch (Exception ex) {}
			}
			if (entries.isEmpty()) entries.addAll(getDefinitionModule().getDefinitions(null, Boolean.TRUE, Definition.FOLDER_ENTRY));
		}
		model.put(WebKeys.ENTRY, DefinitionHelper.orderDefinitions(entries, false));
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_entry_types_widget", model);
	}
	
	private ModelAndView ajaxGetEntryFields(RenderRequest request, RenderResponse response) {
		String entryTypeId = PortletRequestUtils.getStringParameter(request,WebKeys.FILTER_ENTRY_DEF_ID, "");
		String entryField = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField, "");
		
		
		Map model = new HashMap();
		response.setContentType("text/json");
		
		Map fieldsData = new HashMap();
		if (WebHelper.isUserLoggedIn(request)) {
			fieldsData = getDefinitionModule().getEntryDefinitionElements(entryTypeId);
		}
	
		if (entryField.equals("")) {
			SortedMap fieldsSorted = new TreeMap();
			Iterator<Map.Entry<String, Map>> it = fieldsData.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Map> mapEntry = it.next();
				fieldsSorted.put(mapEntry.getValue().get("caption") + "|" + mapEntry.getKey(), mapEntry.getValue());
			}
			
			model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, fieldsSorted);
			return new ModelAndView("forum/json/find_entry_fields_widget", model);
		} else {
			Map valuesData = new HashMap();
			if (WebHelper.isUserLoggedIn(request)) {
				Map entryFieldMap = (Map) fieldsData.get(entryField); 
				if (entryFieldMap != null) {
					valuesData = (Map) entryFieldMap.get("values");
					String fieldType = (String)entryFieldMap.get("type");
					if (valuesData == null && "checkbox".equals(fieldType)) {
						valuesData = new HashMap();
						valuesData.put(Boolean.TRUE.toString(), NLT.get("searchForm.checkbox.selected"));
						valuesData.put(Boolean.FALSE.toString(), NLT.get("searchForm.checkbox.unselected"));
					}
				}
			}
			model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, valuesData);
			return new ModelAndView("forum/json/find_entry_field_values_widget", model);
		}
 	}
	
	private ModelAndView ajaxGetUsers(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		String search = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String pagerText = PortletRequestUtils.getStringParameter(request, "pager", "");

		List users = new ArrayList();
		if (WebHelper.isUserLoggedIn(request)) {
				
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
			
			int startPageNo = 1;
			int endPageNo = 10;
			if (!pagerText.equals("")) {
				String[] pagesNos = pagerText.split(";");
				startPageNo = Integer.parseInt(pagesNos[0]);
				endPageNo = Integer.parseInt(pagesNos[1]);
			}
	
			options.put(ObjectKeys.SEARCH_MAX_HITS, (endPageNo - startPageNo) + 1);
			options.put(ObjectKeys.SEARCH_OFFSET, startPageNo - 1);
			
			if (!search.equals("")) {
				SearchFilter searchTermFilter = new SearchFilter();
				search += "*";
				
				searchTermFilter.addTitleFilter(search);
				searchTermFilter.addLoginNameFilter(search);
							
				options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
			}
			
			Map entries = getProfileModule().getUsers(currentUser.getParentBinder().getId(), options);
			users = (List)entries.get(ObjectKeys.SEARCH_ENTRIES);
		
			
			int searchCountTotal = ((Integer)entries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
			int totalSearchRecordsReturned = ((Integer)entries.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED)).intValue();
			
			
			if (startPageNo + totalSearchRecordsReturned < searchCountTotal) {
				Map next = new HashMap();
				next.put("start", startPageNo + totalSearchRecordsReturned);
				if (startPageNo + totalSearchRecordsReturned + 10 < searchCountTotal) {
					next.put("end",  startPageNo + totalSearchRecordsReturned + 10);
				} else {
					next.put("end",  searchCountTotal);
				}
				model.put(WebKeys.PAGE_NEXT, next);
			}
	
			if (startPageNo > 1) {
				Map prev = new HashMap();
				prev.put("start", startPageNo - 10);
				prev.put("end", startPageNo - 1);
				model.put(WebKeys.PAGE_PREVIOUS, prev);
			}
			if (startPageNo == 1 && (search.equals("") || search.equals("*"))) {
				// add relative option "current user" and "me"
				Map currentUserPlaceholder = new HashMap();
				currentUserPlaceholder.put("title", NLT.get("searchForm.currentUserTitle"));
				currentUserPlaceholder.put("_docId", SearchFilterKeys.CurrentUserId);
				
				
				Map mePlaceholder = new HashMap();
				mePlaceholder.put("title", NLT.get("searchForm.meTitle") + " (" + currentUser.getTitle() + ")");
				mePlaceholder.put("_docId", currentUser.getId());
				
				
				users.add(0, mePlaceholder);
				users.add(0, currentUserPlaceholder);
			} 
		}

		model.put(WebKeys.USERS, users);
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_users_widget", model);
	}

	private ModelAndView ajaxGetGroups(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		String search = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String pagerText = PortletRequestUtils.getStringParameter(request, "pager", "");

		List users = new ArrayList();
		if (WebHelper.isUserLoggedIn(request)) {
				
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
			
			int startPageNo = 1;
			int endPageNo = 10;
			if (!pagerText.equals("")) {
				String[] pagesNos = pagerText.split(";");
				startPageNo = Integer.parseInt(pagesNos[0]);
				endPageNo = Integer.parseInt(pagesNos[1]);
			}
	
			options.put(ObjectKeys.SEARCH_MAX_HITS, (endPageNo - startPageNo) + 1);
			options.put(ObjectKeys.SEARCH_OFFSET, startPageNo - 1);
			
			if (!search.equals("")) {
				SearchFilter searchTermFilter = new SearchFilter();
				search += "*";
				
				searchTermFilter.addTitleFilter(search);
				searchTermFilter.addLoginNameFilter(search);
							
				options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
			}
			
			Map entries = getProfileModule().getGroups(currentUser.getParentBinder().getId(), options);
			users = (List)entries.get(ObjectKeys.SEARCH_ENTRIES);
		
			
			int searchCountTotal = ((Integer)entries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
			int totalSearchRecordsReturned = ((Integer)entries.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED)).intValue();
			
			
			if (startPageNo + totalSearchRecordsReturned < searchCountTotal) {
				Map next = new HashMap();
				next.put("start", startPageNo + totalSearchRecordsReturned);
				if (startPageNo + totalSearchRecordsReturned + 10 < searchCountTotal) {
					next.put("end",  startPageNo + totalSearchRecordsReturned + 10);
				} else {
					next.put("end",  searchCountTotal);
				}
				model.put(WebKeys.PAGE_NEXT, next);
			}
	
			if (startPageNo > 1) {
				Map prev = new HashMap();
				prev.put("start", startPageNo - 10);
				prev.put("end", startPageNo - 1);
				model.put(WebKeys.PAGE_PREVIOUS, prev);
			}
		}

		model.put(WebKeys.USERS, users);
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_users_widget", model);
	}	
	
	private ModelAndView ajaxGetTeams(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();
		String search = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String pagerText = PortletRequestUtils.getStringParameter(request, "pager", "");

		List users = new ArrayList();
		if (WebHelper.isUserLoggedIn(request)) {
				
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
			
			int startPageNo = 1;
			int endPageNo = 10;
			if (!pagerText.equals("")) {
				String[] pagesNos = pagerText.split(";");
				startPageNo = Integer.parseInt(pagesNos[0]);
				endPageNo = Integer.parseInt(pagesNos[1]);
			}
	
			options.put(ObjectKeys.SEARCH_MAX_HITS, (endPageNo - startPageNo) + 1);
			options.put(ObjectKeys.SEARCH_OFFSET, startPageNo - 1);
			
			SearchFilter searchTermFilter = new SearchFilter();
			
			String newStr = search;
			Matcher matcher = TypeToFindAjaxController.replacePtrn.matcher(newStr);
			while (matcher.find()) {
				newStr = matcher.replaceFirst(" ");
				matcher = TypeToFindAjaxController.replacePtrn.matcher(newStr);
			}
			newStr = newStr.replaceAll(" \\*", "\\*");
			
			search = newStr;
		    
			searchTermFilter.addWorkspaceFilter(search + "*");
			
			Map entries = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
			users = (List)entries.get(ObjectKeys.SEARCH_ENTRIES);

		
			
			int searchCountTotal = ((Integer)entries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
			int totalSearchRecordsReturned = ((Integer)entries.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED)).intValue();
			
			
			if (startPageNo + totalSearchRecordsReturned < searchCountTotal) {
				Map next = new HashMap();
				next.put("start", startPageNo + totalSearchRecordsReturned);
				if (startPageNo + totalSearchRecordsReturned + 10 < searchCountTotal) {
					next.put("end",  startPageNo + totalSearchRecordsReturned + 10);
				} else {
					next.put("end",  searchCountTotal);
				}
				model.put(WebKeys.PAGE_NEXT, next);
			}
	
			if (startPageNo > 1) {
				Map prev = new HashMap();
				prev.put("start", startPageNo - 10);
				prev.put("end", startPageNo - 1);
				model.put(WebKeys.PAGE_PREVIOUS, prev);
			}
		}

		model.put(WebKeys.USERS, users);
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_users_widget", model);
	}	
}
