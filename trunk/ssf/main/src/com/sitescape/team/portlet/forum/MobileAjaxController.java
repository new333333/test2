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
package com.sitescape.team.portlet.forum;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.BinderComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractControllerRetry;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.Favorites;
import com.sitescape.team.web.util.PortletPreferencesUtil;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebStatusTicket;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;
/**
 * @author Peter Hurley
 *
 */
public class MobileAjaxController  extends SAbstractControllerRetry {
	static Pattern replacePtrn = Pattern.compile("([\\p{Punct}&&[^\\*]])");	
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		if (!WebHelper.isUserLoggedIn(request)) {
			return ajaxMobileLogin(request, response);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FOLDER)) {
			return ajaxMobileShowFolder(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_WORKSPACE)) {
			return ajaxMobileShowWorkspace(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_ENTRY)) {
			return ajaxMobileShowEntry(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_LOGIN)) {
			return ajaxMobileLogin(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE)) {
			return ajaxMobileFrontPage(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_SEARCH_RESULTS)) {
			return ajaxMobileSearchResults(request, response);
		} else if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE)) {
			return ajaxMobileFindPeople(request, response);
		}
		return ajaxMobileFrontPage(request, response);
	} 


	private ModelAndView ajaxMobileLogin(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		return new ModelAndView("mobile/show_login_form", model);
	}
	
	
	private ModelAndView ajaxMobileFrontPage(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		Map model = new HashMap();
		//This is the portlet view; get the configured list of folders to show
		String[] mobileBinderIds = (String[])userProperties.get(ObjectKeys.USER_PROPERTY_MOBILE_BINDER_IDS);

		//Build the jsp bean (sorted by folder title)
		List<Long> binderIds = new ArrayList<Long>();
		if (mobileBinderIds != null) {
			for (int i = 0; i < mobileBinderIds.length; i++) {
				binderIds.add(new Long(mobileBinderIds[i]));
			}
		}
		model.put(WebKeys.MOBILE_BINDER_LIST, getBinderModule().getBinders(binderIds));
		
		Map unseenCounts = new HashMap();
		unseenCounts = getFolderModule().getUnseenCounts(binderIds);
		model.put(WebKeys.LIST_UNSEEN_COUNTS, unseenCounts);
		
		Map userQueries = new HashMap();
		if (userProperties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)userProperties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}

		model.put("ss_UserQueries", userQueries);
		return new ModelAndView("mobile/show_front_page", model);
	}

	private ModelAndView ajaxMobileSearchResults(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		String queryName = PortletRequestUtils.getStringParameter(request, "ss_queryName", "");
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		Map model = new HashMap();
		model.put("ss_queryName", queryName);

		Map formData = request.getParameterMap();
	    Tabs tabs = Tabs.getTabs(request);
	    if (formData.containsKey("searchBtn")) {
	    	model.putAll(BinderHelper.prepareSearchResultData(this, request, tabs));
	    } else {
	    	model.putAll(BinderHelper.prepareSavedQueryResultData(this, request, tabs));
	    }
		Map userQueries = new HashMap();
		if (userProperties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)userProperties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}

		return new ModelAndView("mobile/show_search_results", model);
	}

	private ModelAndView ajaxMobileShowFolder(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		Map options = new HashMap();		
		String viewType = "";
		Map folderEntries = null;
		
		String view = null;
		if (binder== null) {
			return ajaxMobileFrontPage(request, response);
		} 
		model.put(WebKeys.BINDER, binder);

      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER);
      	if (pageNumber == null) pageNumber = 0;
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = pageNumber.intValue() * pageSize;
      	int pageEnd = pageStart + pageSize;
      	String nextPage = "";
      	String prevPage = "";
      	options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(pageSize));
      	options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));

      	folderEntries = getFolderModule().getEntries(binderId, options);
      	
      	model.put(WebKeys.SEARCH_TOTAL_HITS, folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		if (folderEntries != null) {
			model.put(WebKeys.FOLDER_ENTRIES, (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
		}
		
      	if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber - 1);
      	if (((List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES)).size() == pageSize && 
      			((Integer)folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue() > ((pageNumber.intValue() + 1) * pageSize)) 
      		nextPage = String.valueOf(pageNumber + 1);
		model.put(WebKeys.URL_PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
		return new ModelAndView("mobile/show_folder", model);
	}

	private ModelAndView ajaxMobileShowWorkspace(RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Workspace binder;
		List workspaces = new ArrayList();
		List folders = new ArrayList();
		try {
			binder = getWorkspaceModule().getWorkspace(Long.valueOf(binderId));
		} catch (Exception ex) {
			binder = getWorkspaceModule().getWorkspace();				
		}
		if (binder == null) binder = getWorkspaceModule().getWorkspace();
		model.put(WebKeys.BINDER, binder);
		//See if this is a user workspace
		if (binder.getDefinitionType() == Definition.USER_WORKSPACE_VIEW) {
			Set wsUsers = new HashSet();
			wsUsers.add(binder.getCreation().getPrincipal().getId());
			SortedSet wsUsers2 = getProfileModule().getUsers(wsUsers);
			model.put(WebKeys.WORKSPACE_CREATOR, wsUsers2.first());
		}
        Comparator c = new BinderComparator(user.getLocale(),BinderComparator.SortByField.searchTitle);
		TreeSet ws = new TreeSet(c);
		ws.addAll(binder.getWorkspaces());
      	for (Iterator iter=ws.iterator(); iter.hasNext();) {
     		Workspace w = (Workspace)iter.next();
      		if (!w.isDeleted()) workspaces.add(w);
		}
      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER);
      	if (pageNumber == null) pageNumber = 0;
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = pageNumber.intValue() * pageSize;
      	int pageEnd = pageStart + pageSize;
      	List wsList;
      	String nextPage = "";
      	String prevPage = "";
      	if (workspaces.size() < pageStart) {
      		wsList = new ArrayList();
      		if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber.intValue() - 1);
      	} else if (workspaces.size() >= pageEnd) {
      		wsList = workspaces.subList(pageStart, pageEnd);
      		nextPage = String.valueOf(pageNumber.intValue() + 1);
      		if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber.intValue() - 1);
      	} else {
      		wsList = workspaces.subList(pageStart, workspaces.size());
      		if (pageNumber.intValue() > 0) prevPage = String.valueOf(pageNumber.intValue() - 1);
      	}
		model.put(WebKeys.WORKSPACES, wsList);
		model.put(WebKeys.URL_PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);
		ws.clear();
		ws.addAll(binder.getFolders());
      	for (Iterator iter=ws.iterator(); iter.hasNext();) {
     		Binder f = (Binder)iter.next();
      		if (!f.isDeleted()) folders.add(f);
		}
		model.put(WebKeys.FOLDERS, folders);
		return new ModelAndView("mobile/show_workspace", model);
	}

	private ModelAndView ajaxMobileShowEntry(RenderRequest request, 
				RenderResponse response) throws Exception {
		Map model = new HashMap();
		model.put(WebKeys.SHOW_MOBILE_VIEW, true);
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, binder);
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		User user = RequestContextHolder.getRequestContext().getUser();
		
		FolderEntry entry = null;
		Map folderEntries = null;
		folderEntries  = getFolderModule().getEntryTree(binderId, entryId);
		if (folderEntries != null) {
			entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
			model.put(WebKeys.CONFIG_JSP_STYLE, "view");
			if (DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@name='entryView']") == false) {
				DefinitionHelper.getDefaultEntryView(entry, model);
			}
			BinderHelper.setAccessControlForAttachmentList(this, model, entry, user);
			Map accessControlMap = (Map) model.get(WebKeys.ACCESS_CONTROL_MAP);
			HashMap entryAccessMap = BinderHelper.getEntryAccessMap(this, model, entry);
			model.put(WebKeys.ENTRY, entry);
			model.put(WebKeys.DEFINITION_ENTRY, entry);
			model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
			model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
			if (DefinitionHelper.getDefinition(entry.getEntryDef(), model, "//item[@name='entryView']") == false) {
				DefinitionHelper.getDefaultEntryView(entry, model);
			}
			SeenMap seen = getProfileModule().getUserSeenMap(null);
			model.put(WebKeys.SEEN_MAP, seen);
			List replies = new ArrayList((List)model.get(WebKeys.FOLDER_ENTRY_DESCENDANTS));
			if (replies != null)  {
				accessControlMap.put(entry.getId(), entryAccessMap);
				for (int i=0; i<replies.size(); i++) {
					FolderEntry reply = (FolderEntry)replies.get(i);
					accessControlMap.put(reply.getId(), entryAccessMap);
				}
			}
			if (!seen.checkIfSeen(entry)) { 
				//only mark top entries as seen
				getProfileModule().setSeen(null, entry);
			}
		}
		return new ModelAndView("mobile/show_entry", model);
	}	

	private ModelAndView ajaxMobileFindPeople(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE)) {
			if (formData.containsKey("okBtn")) {
				String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
				model.put(WebKeys.SEARCH_TEXT, searchText);
				String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "10");
				String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "0");
				Integer startingCount = Integer.parseInt(pageNumber) * Integer.parseInt(maxEntries);

				User user = RequestContextHolder.getRequestContext().getUser();
				Map options = new HashMap();
				String view;
				options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
				options.put(ObjectKeys.SEARCH_OFFSET, startingCount);
				options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
				
				//Build the search query
				SearchFilter searchTermFilter = new SearchFilter();
				
				String newStr = searchText;
				Matcher matcher = replacePtrn.matcher(newStr);
				while (matcher.find()) {
					newStr = matcher.replaceFirst(" ");
					matcher = replacePtrn.matcher(newStr);
				}
				newStr = newStr.replaceAll(" \\*", "\\*");
				
			    searchText = newStr + "*";
				//Add the login name term
				if (searchText.length()>0) {
					searchTermFilter.addTitleFilter(searchText);
					searchTermFilter.addLoginNameFilter(searchText);
				}
			   	
				//Do a search to find the first few items that match the search text
				options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
				Map entries = getProfileModule().getUsers(user.getParentBinder().getId(), options);
				model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
				model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				view = "mobile/find_people";

				model.put(WebKeys.PAGE_SIZE, maxEntries);
				model.put(WebKeys.PAGE_NUMBER, pageNumber);
				
				return new ModelAndView(view, model);
				
			}
		}
		return new ModelAndView("mobile/find_people", model);
	}
	
}
