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

import static com.sitescape.util.search.Restrictions.in;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import net.sf.json.JSONArray;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.portlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.comparator.BinderComparator;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractControllerRetry;
import com.sitescape.team.web.tree.DomTreeBuilder;
import com.sitescape.team.web.tree.FolderConfigHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.Favorites;
import com.sitescape.team.web.util.PermaLinkUtil;
import com.sitescape.team.web.util.PortletPreferencesUtil;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.team.web.util.WebStatusTicket;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;
import com.sitescape.util.search.Criteria;
import com.sitescape.util.search.Order;
import com.sitescape.team.util.LongIdUtil;
/**
 * @author Peter Hurley
 *
 */
public class MobileAjaxController  extends SAbstractControllerRetry {
	static Pattern replacePtrn = Pattern.compile("([\\p{Punct}&&[^\\*]])");	
	
	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestWithRetry(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		User user = RequestContextHolder.getRequestContext().getUser();
		if (WebHelper.isUserLoggedIn(request) && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
		
			//The user is logged in
			if (op.equals(WebKeys.OPERATION_MOBILE_ADD_ENTRY)) {
				ajaxMobileDoAddEntry(request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_ADD_REPLY)) {
				ajaxMobileDoAddReply(request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE)) {
				ajaxMobileDoFrontPage(this, request, response);
			} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_ENTRY)) {
				ajaxMobileDoShowEntry(this, request, response);
			}
		}
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		User user = RequestContextHolder.getRequestContext().getUser();
		if (!WebHelper.isUserLoggedIn(request) || ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			return ajaxMobileLogin(this, request, response);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FOLDER)) {
			return ajaxMobileShowFolder(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_WORKSPACE)) {
			return ajaxMobileShowWorkspace(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_ENTRY)) {
			return ajaxMobileShowEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_NEXT_ENTRY)) {
			return ajaxMobileShowNextEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_PREV_ENTRY)) {
			return ajaxMobileShowPrevEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_ADD_ENTRY)) {
			return ajaxMobileAddEntry(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_ADD_REPLY)) {
			return ajaxMobileAddReply(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_MODIFY_ENTRY)) {
			//return ajaxMobileModifyEntry(request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_WHATS_NEW)) {
			return ajaxMobileWhatsNew(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_LOGIN)) {
			return ajaxMobileLogin(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE)) {
			return ajaxMobileFrontPage(this, request, response);
			
		} else if (op.equals(WebKeys.OPERATION_MOBILE_SHOW_SEARCH_RESULTS)) {
			return ajaxMobileSearchResults(this, request, response);
		
		} else if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE)) {
			return ajaxMobileFindPeople(this, request, response);
		}
		if (!WebHelper.isUserLoggedIn(request) || ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			return ajaxMobileLogin(this, request, response);
		} else {
			return ajaxMobileFrontPage(this, request, response);
		}
	} 

	private void ajaxMobileDoAddEntry(ActionRequest request, ActionResponse response) 
			throws Exception {
		//Add an entry
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		//See if the add entry form was submitted
		Long entryId=null;
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap = new HashMap();
			MapInputData inputData = new MapInputData(formData);
			entryId= getFolderModule().addEntry(folderId, entryType, inputData, fileMap, null);
			
			//See if the user wants to subscribe to this entry
			BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
		} else {
			String sUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MOBILE_URL, "");
			if (!sUrl.equals("")) response.sendRedirect(sUrl);
		}
	}
	
	private void ajaxMobileDoAddReply(ActionRequest request, ActionResponse response) 
			throws Exception {
		//Add an entry
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		//See if the add entry form was submitted
		Long entryId=null;
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap = new HashMap();
			MapInputData inputData = new MapInputData(formData);
			Long id = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
			entryId = getFolderModule().addReply(folderId, id, entryType, inputData, fileMap, null);

			//See if the user wants to subscribe to this entry
			BinderHelper.subscribeToThisEntry(this, request, folderId, entryId);
		} else {
			String sUrl = PortletRequestUtils.getStringParameter(request, WebKeys.URL_MOBILE_URL, "");
			if (!sUrl.equals("")) response.sendRedirect(sUrl);
		}
	}

	private void ajaxMobileDoFrontPage(AllModulesInjected bs, ActionRequest request, ActionResponse response) 
			throws Exception {
		//Do front page stuff
		User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		//See if the add entry form was submitted
		if (formData.containsKey("miniblogBtn")) {
			//The miniblog form was submitted. Go process it
			String text = PortletRequestUtils.getStringParameter(request, "miniblogText", "");
			BinderHelper.addMiniBlogEntry(bs, text);
		}
	}

	private void ajaxMobileDoShowEntry(AllModulesInjected bs, ActionRequest request, ActionResponse response) 
			throws Exception {
		Map formData = request.getParameterMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID);				
		
		if (entryId != null) {
			//See if the user asked to change state
			if (formData.containsKey("changeStateBtn")) {
				//Change the state
				//Get the workflow process to change and the name of the new state
				Long replyId = new Long(PortletRequestUtils.getLongParameter(request, "replyId"));
				if (replyId == null) replyId = entryId;
		        Long tokenId = new Long(PortletRequestUtils.getRequiredLongParameter(request, "tokenId"));	
				String toState = PortletRequestUtils.getRequiredStringParameter(request, "toState");
				getFolderModule().modifyWorkflowState(folderId, replyId, tokenId, toState);
			}
		}
	}

	private ModelAndView ajaxMobileLogin(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
		adapterUrl.setParameter(WebKeys.OPERATION, WebKeys.OPERATION_MOBILE_SHOW_FRONT_PAGE);
		model.put(WebKeys.URL, adapterUrl);
		return new ModelAndView("mobile/show_login_form", model);
	}
	
	
	private ModelAndView ajaxMobileFrontPage(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");

		Object obj = userProperties.get(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites f;
		if (obj != null && obj instanceof Document) {
			f = new Favorites((Document)obj);
			//fixup - have to store as string cause hibernate equals fails
			getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
		} else {		
			f = new Favorites((String)obj);
		}
		List<Map> favList = f.getFavoritesList();
		model.put(WebKeys.MOBILE_FAVORITES_LIST, favList);
		
		Map userQueries = new HashMap();
		if (userProperties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)userProperties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}
		
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		ProfileBinder profileBinder = null;
		try {
			profileBinder = getProfileModule().getProfileBinder();
		} catch(Exception e) {}
		if (profileBinder != null) {
			accessControlMap.put(WebKeys.CAN_VIEW_USER_PROFILES, true);
		}

		model.put("ss_UserQueries", userQueries);
		return new ModelAndView("mobile/show_front_page", model);
	}

	private ModelAndView ajaxMobileSearchResults(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		User user = RequestContextHolder.getRequestContext().getUser(); 
		String queryName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_QUERY_NAME, "");
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		Map model = new HashMap();
		model.put("ss_queryName", queryName);
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");

		String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "1");
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = (Integer.parseInt(pageNumber) -1) * pageSize;
      	int pageEnd = pageStart + pageSize;
		Map formData = request.getParameterMap();
	    Tabs tabs = Tabs.getTabs(request);
		model.put(WebKeys.TABS, tabs);		
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		model.put(WebKeys.SEARCH_TEXT, searchText);
	    if (formData.containsKey("searchBtn") || formData.containsKey("quickSearch")) {
			SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
			Document searchQuery = requestParser.getSearchQuery();
			Map options = BinderHelper.prepareSearchOptions(bs, request);
			options.put(ObjectKeys.SEARCH_OFFSET, new Integer(pageStart));
			options.put(ObjectKeys.SEARCH_USER_MAX_HITS, new Integer(pageSize));
			Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, options);
			
			Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
			
			BinderHelper.prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
	    } else {
			
			// get query and options from tab		
			Document searchQuery = BinderHelper.getSavedQuery(bs, queryName, bs.getProfileModule().getUserProperties(user.getId()));
			
			// get page no and actualize options
			// execute query
			// actualize tabs info
			Map options = BinderHelper.prepareSearchOptions(bs, request);
			options.put(ObjectKeys.SEARCH_OFFSET, new Integer(pageStart));
			options.put(ObjectKeys.SEARCH_USER_MAX_HITS, new Integer(pageSize));

			options.put(Tabs.TITLE, queryName);
			Map results =  bs.getBinderModule().executeSearchQuery(searchQuery, options);
			
			Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
			
			BinderHelper.prepareSearchResultPage(bs, results, model, searchQuery, options, tab);
	    }
		Map userQueries = new HashMap();
		if (userProperties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)userProperties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}

      	//Get the total records found by the search
      	Integer totalRecords = (Integer)model.get(WebKeys.PAGE_TOTAL_RECORDS);
      	//Get the records returned (which may be more than the page size)
      	List results = (List)model.get(WebKeys.FOLDER_ENTRIES);
      	String nextPage = "";
      	String prevPage = "";
      	if (totalRecords.intValue() < pageStart) {
      		if (Integer.parseInt(pageNumber) > 1) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
      	} else if (totalRecords.intValue() >= pageEnd) {
      		nextPage = String.valueOf(Integer.parseInt(pageNumber) + 1);
      		if (Integer.parseInt(pageNumber) > 1) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
      	} else {
      		if (Integer.parseInt(pageNumber) > 1) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
      	}
		model.put(WebKeys.TAB_ID, String.valueOf(model.get(WebKeys.URL_TAB_ID)));
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		return new ModelAndView("mobile/show_search_results", model);
	}

	private ModelAndView ajaxMobileShowFolder(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		Map options = new HashMap();		
		Map folderEntries = null;
		
		if (binder== null) {
			return ajaxMobileFrontPage(this, request, response);
		} 
		model.put(WebKeys.BINDER, binder);

      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER);
      	if (pageNumber == null || pageNumber < 0) pageNumber = 0;
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = pageNumber.intValue() * pageSize;
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
		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));

		if (binder instanceof Folder && getFolderModule().testAccess((Folder)binder, FolderOperation.addEntry)) {				
			List defaultEntryDefinitions = binder.getEntryDefinitions();
			List<Map> defTitleUrlList = new ArrayList();
			for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
				Definition def = (Definition) defaultEntryDefinitions.get(i);
				String title = NLT.getDef(def.getTitle());
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_ADD_ENTRY);
				Map defTitle = new HashMap();
				defTitle.put("title", title);
				defTitle.put("def", def);
				defTitle.put("url", adapterUrl.toString());
				defTitleUrlList.add(defTitle);
			}
			model.put(WebKeys.MOBILE_BINDER_DEF_URL_LIST, defTitleUrlList);
		}

		List folders = new ArrayList();
		List folderIds = new ArrayList();
		folderIds.add(binderId.toString());
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER}))
			.add(in(Constants.BINDERS_PARENT_ID_FIELD, folderIds));
		crit.addOrder(Order.asc(Constants.SORT_TITLE_FIELD));
		Map binderMap = bs.getBinderModule().executeSearchQuery(crit, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS);

		List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderIdList = new ArrayList();

      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		binderIdList.add(new Long((String)entryMap.get("_docId")));
      	}
      	SortedSet binderList = bs.getBinderModule().getBinders(binderIdList);
        for (Iterator iter=binderList.iterator(); iter.hasNext();) {
     		Binder b = (Binder)iter.next();
      		if (b.isDeleted()) continue;
      		if (b.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
      			folders.add(b);
      		}
		}
		if (!folders.isEmpty()) {
			model.put(WebKeys.FOLDERS, folders);
		}
		return new ModelAndView("mobile/show_folder", model);
	}

	private ModelAndView ajaxMobileWhatsNew(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		if (binderId == null) binderId = getWorkspaceModule().getTopWorkspace().getId();
		Binder binder = getBinderModule().getBinder(binderId);
		String type = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE, WebKeys.URL_WHATS_NEW);
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");

		Map options = new HashMap();		
		if (binder== null) {
			return ajaxMobileFrontPage(bs, request, response);
		} 
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.TYPE, type);

      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER);
      	if (pageNumber == null || pageNumber < 0) pageNumber = 0;
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE_WHATS_NEW).intValue();
      	int pageStart = pageNumber.intValue() * pageSize;
      	String nextPage = "";
      	String prevPage = "";
      	options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(pageSize));
      	options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));

		if (type.equals(WebKeys.URL_WHATS_NEW)) {
			BinderHelper.setupWhatsNewBinderBeans(bs, binder, model, String.valueOf(pageNumber));
		} else if (type.equals(WebKeys.URL_WHATS_NEW_TRACKED)) {
			BinderHelper.setupWhatsNewBinderBeans(bs, binder, model, String.valueOf(pageNumber));
		} else if (type.equals(WebKeys.URL_UNSEEN)) {
			BinderHelper.setupUnseenBinderBeans(bs, binder, model, String.valueOf(pageNumber));
		}

		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);

		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
		return new ModelAndView("mobile/show_whats_new", model);
	}

	private ModelAndView ajaxMobileShowWorkspace(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		Workspace binder;
		List wsList = new ArrayList();
		List workspaces = new ArrayList();
		List folders = new ArrayList();
		try {
			binder = getWorkspaceModule().getWorkspace(Long.valueOf(binderId));
		} catch (Exception ex) {
			binder = getWorkspaceModule().getTopWorkspace();				
		}
		model.put(WebKeys.BINDER, binder);
		if (binder == null) {
			return new ModelAndView("mobile/show_workspace", model);
		}
		//See if this is a user workspace
		if (binder != null && binder.getDefinitionType() != null && 
				Definition.USER_WORKSPACE_VIEW == binder.getDefinitionType()) {
			Set wsUsers = new HashSet();
			wsUsers.add(binder.getCreation().getPrincipal().getId());
			SortedSet wsUsers2 = getProfileModule().getUsers(wsUsers);
			model.put(WebKeys.WORKSPACE_CREATOR, wsUsers2.first());
		}
		Map options = new HashMap();
		Map binderMap = getBinderModule().getBinders(binder, options);
		List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderIdList = new ArrayList();

      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		binderIdList.add(new Long((String)entryMap.get("_docId")));
      	}
      	SortedSet binderList = getBinderModule().getBinders(binderIdList);
        for (Iterator iter=binderList.iterator(); iter.hasNext();) {
     		Binder b = (Binder)iter.next();
      		if (b.isDeleted()) continue;
      		if (b.getEntityType().equals(EntityIdentifier.EntityType.workspace) || 
      				b.getEntityType().equals(EntityIdentifier.EntityType.profiles)) {
      			workspaces.add(b);
      		} else if (b.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
      			folders.add(b);
      		}
		}

      	Integer pageNumber = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER);
      	if (pageNumber == null) pageNumber = 0;
      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
      	int pageStart = pageNumber.intValue() * pageSize;
      	int pageEnd = pageStart + pageSize;
      	
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
		model.put(WebKeys.PAGE_NUMBER, pageNumber.toString());
		model.put(WebKeys.NEXT_PAGE, nextPage);
		model.put(WebKeys.PREV_PAGE, prevPage);
		model.put(WebKeys.FOLDERS, folders);
		return new ModelAndView("mobile/show_workspace", model);
	}

	private ModelAndView ajaxMobileShowNextEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Long nextEntryId = getNextPrevEntry((Folder) binder, entryId, true);	
		if (nextEntryId != null) return ajaxMobileShowEntry(bs, request, response, nextEntryId);
		
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.ENTRY_ID, entryId);
		return new ModelAndView("mobile/show_no_entry", model);
	}
	private ModelAndView ajaxMobileShowPrevEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		Binder binder = getBinderModule().getBinder(binderId);
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		Long nextEntryId = getNextPrevEntry((Folder) binder, entryId, false);	
		if (nextEntryId != null) return ajaxMobileShowEntry(bs, request, response, nextEntryId);
		
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.ENTRY_ID, entryId);
		return new ModelAndView("mobile/show_no_entry", model);
	}
	private ModelAndView ajaxMobileShowEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
		return ajaxMobileShowEntry(bs, request, response, entryId);
	}
	private ModelAndView ajaxMobileShowEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long entryId) throws Exception {
	Map model = new HashMap();
	Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
	Binder binder = getBinderModule().getBinder(binderId);
	BinderHelper.setupStandardBeans(bs, request, response, model, binderId, "ss_mobile");
	model.put(WebKeys.BINDER, binder);
	User user = RequestContextHolder.getRequestContext().getUser();
	
	FolderEntry entry = null;
	Map folderEntries = null;
	folderEntries  = getFolderModule().getEntryTree(binderId, entryId);
	if (folderEntries != null) {
		entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
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

		if (getFolderModule().testAccess(entry, FolderOperation.addReply)) {
			Definition def = entry.getEntryDef(); //cannot be null here
			List replyStyles = DefinitionUtils.getPropertyValueList(def.getDefinition().getRootElement(), "replyStyle");
			model.put(WebKeys.ENTRY_REPLY_STYLES, replyStyles);
			List<Map> defTitleUrlList = new ArrayList();
			model.put(WebKeys.MOBILE_BINDER_DEF_URL_LIST, defTitleUrlList);
			if (!replyStyles.isEmpty()) {
				for (int i = 0; i < replyStyles.size(); i++) {
					String replyStyleId = (String)replyStyles.get(i);
					Definition replyDef = getDefinitionModule().getDefinition(replyStyleId);
					String title = NLT.getDef(replyDef.getTitle());
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_mobile", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyDef.getId());
					adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_ADD_REPLY);
					Map defTitle = new HashMap();
					defTitle.put("title", title);
					defTitle.put("def", replyDef);
					defTitle.put("url", adapterUrl.toString());
					defTitleUrlList.add(defTitle);
				}
			}
		}
		List entries = new ArrayList();
		entries.add(entry);
		BinderHelper.buildWorkflowSupportBeans(this, entries, model);
	}
	return new ModelAndView("mobile/show_entry", model);
}	

	private ModelAndView ajaxMobileAddEntry(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
		Folder folder = getFolderModule().getFolder(folderId);
		//Adding an entry; get the specific definition
		Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
		String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
    	request.setAttribute(WebKeys.URL_ENTRY_TYPE, entryType);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.BINDER, folder);
		model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
		model.put(WebKeys.DEFINITION_ID, entryType);
		//Make sure the requested definition is legal
		if (folderEntryDefs.containsKey(entryType)) {
			DefinitionHelper.getDefinition((Definition)folderEntryDefs.get(entryType), model, "//item[@type='form']");
		} else {
			DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
		}
		Map formData = request.getParameterMap();
		if (entryType.equals("") || formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			return ajaxMobileShowFolder(bs, request, response);
		} else {
			return new ModelAndView("mobile/add_entry", model);
		}
	}	

	private ModelAndView ajaxMobileAddReply(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();

		Long folderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));		
    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));
    	request.setAttribute(WebKeys.URL_ENTRY_ID, entryId.toString());
    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
    	Folder folder = entry.getParentFolder();
    	model.put(WebKeys.FOLDER, folder); 
    	model.put(WebKeys.ENTRY, entry); 
			
    	//Get the legal reply types from the parent entry definition
		Document entryView = null;
		Definition entryDefinition = entry.getEntryDef();
		if (entryDefinition != null) {
			entryView = entryDefinition.getDefinition();
		}
		List replyStyles = null;
		if (entryView != null) {
			//See if there is a reply style for this entry definition
			replyStyles = DefinitionUtils.getPropertyValueList(entryDefinition.getDefinition().getRootElement(), "replyStyle");
		}
   	
    	//Adding an entry; get the specific definition
		Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(folder);
    	String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
    	request.setAttribute(WebKeys.URL_ENTRY_TYPE, entryType);
		model.put(WebKeys.DEFINITION_ID, entryType);
    	model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
    	model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_MOBILE);
    	
        //Make sure the requested reply definition is legal
    	boolean replyStyleIsGood = false;
    	Iterator itReplyStyles = replyStyles.iterator();
    	while (itReplyStyles.hasNext()) {
    		if (((String)itReplyStyles.next()).equals(entryType)) {
    			replyStyleIsGood = true;
    			break;
    		}
    	}
	    	
		if (replyStyleIsGood) {
			DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
		} else {
			DefinitionHelper.getDefinition(null, model, "//item[@name='entryForm']");
		}
		
		Map formData = request.getParameterMap();
		if (entryType.equals("") || formData.containsKey("okBtn") || formData.containsKey("cancelBtn")) {
			return ajaxMobileShowEntry(bs, request, response);
		} else {
			return new ModelAndView("mobile/add_reply", model);
		}
	}	

	private ModelAndView ajaxMobileFindPeople(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		BinderHelper.setupStandardBeans(bs, request, response, model, null, "ss_mobile");
		Map formData = request.getParameterMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_MOBILE_FIND_PEOPLE)) {
			String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
			if (formData.containsKey("okBtn") || !searchText.equals("")) {
				model.put(WebKeys.SEARCH_TEXT, searchText);
				String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "10");
				String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "0");
				Integer startingCount = Integer.parseInt(pageNumber) * Integer.parseInt(maxEntries);

				User user = RequestContextHolder.getRequestContext().getUser();
				Map options = new HashMap();
				String view;
				options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
				options.put(ObjectKeys.SEARCH_OFFSET, startingCount);
				options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
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
				Map entries = getProfileModule().getUsers(options);
				model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
				model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				view = "mobile/find_people";

				model.put(WebKeys.PAGE_SIZE, maxEntries);
				model.put(WebKeys.PAGE_NUMBER, pageNumber);

		      	Integer searchCountTotal = (Integer) entries.get(ObjectKeys.SEARCH_COUNT_TOTAL);
		      	if (searchCountTotal == null) searchCountTotal = 0;
		      	int pageSize = Integer.valueOf(WebKeys.MOBILE_PAGE_SIZE).intValue();
		      	int pageStart = Integer.parseInt(pageNumber) * pageSize;
		      	int pageEnd = pageStart + pageSize;
		      	String nextPage = "";
		      	String prevPage = "";
		      	if (searchCountTotal.intValue() < pageStart) {
		      		if (Integer.parseInt(pageNumber) > 0) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
		      	} else if (searchCountTotal.intValue() >= pageEnd) {
		      		nextPage = String.valueOf(Integer.parseInt(pageNumber) + 1);
		      		if (Integer.parseInt(pageNumber) > 0) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
		      	} else {
		      		if (Integer.parseInt(pageNumber) > 0) prevPage = String.valueOf(Integer.parseInt(pageNumber) - 1);
		      	}
				model.put(WebKeys.NEXT_PAGE, nextPage);
				model.put(WebKeys.PREV_PAGE, prevPage);

				return new ModelAndView(view, model);
				
			}
		}
		return new ModelAndView("mobile/find_people", model);
	}
	
	private Long getNextPrevEntry(Folder folder, Long entryId, boolean next) {
		if (folder == null) {
			return null;
		} 
		Map options = new HashMap();		
      	options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf(ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES));
      	options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(0));

      	Map searchResults = getFolderModule().getEntries(folder.getId(), options);
		List folderEntries = (List) searchResults.get(ObjectKeys.SEARCH_ENTRIES);

		for (int i = 0; i < folderEntries.size(); i++) {
			Map entry = (Map) folderEntries.get(i);
			if (entry.containsKey("_docId") && ((String)entry.get("_docId")).equals(entryId.toString())) {
				//Found the current entry
				if (next) {
					i++;
				} else {
					i--;
				}
				if (i >= 0 && i < folderEntries.size()) {
					entry = (Map) folderEntries.get(i);
					String docId = (String) entry.get("_docId");
					if (docId != null) return Long.valueOf(docId);
				}
				return null;
			}
		}
		return null;
	}
	
}
