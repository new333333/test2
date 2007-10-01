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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.admin.AdminModule.AdminOperation;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.rss.util.UrlUtil;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFilterToSearchBooleanConverter;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.search.filter.SearchFilterToMapConverter;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;

/**
 * @author Renata Nowicka
 *
 */

public class AdvancedSearchController extends AbstractBinderController {
	
	private static Log logger = LogFactory.getLog(AdvancedSearchController.class);
	
	public static final String NEW_TAB_VALUE = "1";
		
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		// set form data for the render method
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
		Map model = new HashMap();
		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);
		
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String strBinderId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_ID, "");
		model.put(WebKeys.BINDER_ID, strBinderId);
		if (!strBinderId.equals("")) {
			Long binderId = Long.valueOf(strBinderId);
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
		}
        
        // this is necessary for the breadcrumbs and places choose
        Workspace top = getWorkspaceModule().getTopWorkspace();
		BinderHelper.buildNavigationLinkBeans(this, top, model);
		model.put(WebKeys.DEFINITION_ENTRY, top);

		model.put(WebKeys.SEARCH_TOP_FOLDER_ID, Collections.singletonList(top.getId().toString()));
        
       if (op.equals(WebKeys.SEARCH_RESULTS)) {
        	model.putAll(prepareSearchResultData(request));
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);

    		//Build a reload url
    		Tabs tabs = setupTabs(request);
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(tabs.getCurrentTab()));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

    		return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else if (op.equals(WebKeys.SEARCH_VIEW_PAGE)) {
        	model.putAll(prepareSearchResultPage(request));
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);
        	
    		//Build a reload url
    		Tabs tabs = setupTabs(request);
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(tabs.getCurrentTab()));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

        	return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else if (op.equals(WebKeys.SEARCH_SAVED_QUERY)) {
        	model.putAll(prepareSavedQueryResultData(request));
        	addPropertiesForFolderView(model);
        	buildToolbars(model, request);
        	
    		//Build a reload url
    		Tabs tabs = setupTabs(request);
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(tabs.getCurrentTab()));
    		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());

        	return new ModelAndView(BinderHelper.getViewListingJsp(this, ObjectKeys.SEARCH_RESULTS_DISPLAY), model);
        } else {
        	model.putAll(prepareSearchFormData(request));
        	
    		//Build a reload url
    		Tabs tabs = setupTabs(request);
    		PortletURL reloadUrl = response.createRenderURL();
    		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADVANCED_SEARCH);
    		reloadUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.SEARCH_VIEW_PAGE);
    		reloadUrl.setParameter(WebKeys.URL_TAB_ID, String.valueOf(tabs.getCurrentTab()));
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
    	
		Workspace ws = getWorkspaceModule().getWorkspace();
		Document tree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);
	}
	
	private Map prepareSearchResultPage(RenderRequest request) throws PortletRequestBindingException {
		Map model = new HashMap();

		// get curent tab from tab
		Tabs tabs = setupTabs(request);
		int tabId = tabs.getCurrentTab();  
		Map currentTab = tabs.getTab(tabId);
		
		// get query and options from tab
		Document query = getQueryFromTab(currentTab);
		Map options = getOptionsFromTab(currentTab);
		
		// get page no and actualize options
		// execute query
		// actualize tabs info
		actualizeOptions(options, request);
		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		prepareSearchResultPage(model, query, options, tabs, newTab);
		
		return model;
	}
	
	private Map prepareSavedQueryResultData(RenderRequest request) throws PortletRequestBindingException {
		Map model = new HashMap();

		String queryName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_QUERY_NAME, "");
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		// get query and options from tab		
		Document query = getSavedQuery(queryName, getProfileModule().getUserProperties(currentUser.getId()));
		
		// get page no and actualize options
		// execute query
		// actualize tabs info
		Map options = prepareSearchOptions(request);
		actualizeOptions(options, request);

		options.put(Tabs.TITLE, queryName);
		options.put(Tabs.TAB_SEARCH_TEXT, queryName);
		
		Tabs tabs = setupTabs(request);
		
		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		prepareSearchResultPage(model, query, options, tabs, newTab);
		
		return model;
	}
	
	private Map prepareSearchFormData(RenderRequest request) throws PortletRequestBindingException {
		Tabs tabs = setupTabs(request);
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID, -1);
		Map options = prepareSearchOptions(request);
		if (tabId < 0) {
			if (!options.containsKey(Tabs.TITLE)) options.put(Tabs.TITLE, NLT.get("searchForm.advanced.Title"));
			if (!options.containsKey(Tabs.TAB_SEARCH_TEXT))options.put(Tabs.TAB_SEARCH_TEXT, NLT.get("searchForm.advanced.Title"));
			options.put(Tabs.TYPE, Tabs.SEARCH);
			int newTabId = tabs.findTab(DocumentHelper.createDocument(), options);
			tabs.setCurrentTab(newTabId);
		}		
		Map model = new HashMap();
		model.put(WebKeys.TABS, tabs.getTabs());
		model.put(WebKeys.URL_TAB_ID, tabs.getCurrentTab());
		model.put("resultsCount", options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		model.put("quickSearch", false);
		
		model.putAll(prepareSavedQueries());
		
		Workspace ws = getWorkspaceModule().getWorkspace();
		Document tree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);
		
		return model;
	}
	
	private void actualizeOptions(Map options, RenderRequest request) {
		int pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, 1);
		int defaultMaxOnPage = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) defaultMaxOnPage = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		int[] maxOnPageArr = PortletRequestUtils.getIntParameters(request, WebKeys.SEARCH_FORM_MAX_HITS);
		int maxOnPage = defaultMaxOnPage;
		if (maxOnPageArr.length >0) maxOnPage = maxOnPageArr[0];
		int userOffset = (pageNo - 1) * maxOnPage;
		int[] summaryWords = PortletRequestUtils.getIntParameters(request, WebKeys.SEARCH_FORM_SUMMARY_WORDS);
		int summaryWordsCount = 20;
		if (options.containsKey(WebKeys.SEARCH_FORM_SUMMARY_WORDS)) { summaryWordsCount = (Integer)options.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS);}
		if (summaryWords.length > 0) {summaryWordsCount = summaryWords[0];}
		
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, userOffset);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxOnPage);
		options.put(WebKeys.URL_PAGE_NUMBER, pageNo);
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWordsCount);
		
	}
	private Map getOptionsFromTab(Map tab) {
		Map options = new HashMap();
		if (tab.containsKey(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE)) options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, tab.get(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE));
		if (tab.containsKey(ObjectKeys.SEARCH_OFFSET)) options.put(ObjectKeys.SEARCH_OFFSET, tab.get(ObjectKeys.SEARCH_OFFSET));
		if (tab.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) options.put(ObjectKeys.SEARCH_USER_OFFSET, tab.get(ObjectKeys.SEARCH_USER_OFFSET));
		if (tab.containsKey(ObjectKeys.SEARCH_MAX_HITS)) options.put(ObjectKeys.SEARCH_MAX_HITS, tab.get(ObjectKeys.SEARCH_MAX_HITS));
		if (tab.containsKey(ObjectKeys.SEARCH_USER_MAX_HITS)) options.put(ObjectKeys.SEARCH_USER_MAX_HITS, tab.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		if (tab.containsKey(Tabs.TITLE)) options.put(Tabs.TITLE, tab.get(Tabs.TITLE));
		if (tab.containsKey(Tabs.TYPE)) options.put(Tabs.TYPE, tab.get(Tabs.TYPE));
		if (tab.containsKey(Tabs.TAB_SEARCH_TEXT)) options.put(Tabs.TAB_SEARCH_TEXT, tab.get(Tabs.TAB_SEARCH_TEXT));
		if (tab.containsKey(WebKeys.SEARCH_FORM_SUMMARY_WORDS)) options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, tab.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));
		if (tab.containsKey(WebKeys.SEARCH_FORM_QUICKSEARCH)) options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, tab.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		return options;
	}
	
	public static Document getQueryFromTab(Map tab) {
		Document query = (Document) tab.get(Tabs.QUERY_DOC);
		return query;
	}
	
	public static Document getSavedQuery(String queryName, UserProperties userProperties) {
		
		Map properties = userProperties.getProperties();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			Map queries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
			return (Document)queries.get(queryName);
		}
		return null;
	}

	private Map prepareSearchResultData(RenderRequest request) throws Exception {
		Map model = new HashMap();
		Tabs tabs = setupTabs(request);
		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");

		SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
		Document searchQuery = requestParser.getSearchQuery();
		Map options = prepareSearchOptions(request);
 
 		prepareSearchResultPage(model, searchQuery, options, tabs, newTab);

		return model;
	}

	private void prepareSearchResultPage (Map model, Document query, Map options, Tabs tabs, String newTab) {

		Map results =  getBinderModule().executeSearchQuery(query, options);
		
		//Store the search query in a new tab
		storeQueryInTabs(tabs, query, options, newTab);
		model.put(WebKeys.TABS, tabs.getTabs());

		model.put(WebKeys.URL_TAB_ID, tabs.getCurrentTab());
		
		SearchFilterToMapConverter searchFilterConverter = new SearchFilterToMapConverter(query, getDefinitionModule(), getProfileModule(), getBinderModule());
		model.putAll(searchFilterConverter.convertAndPrepareFormData());
		
		// SearchUtils.filterEntryAttachmentResults(results);
		prepareRatingsAndFolders(model, (List) results.get(ObjectKeys.SEARCH_ENTRIES));
		model.putAll(prepareSavedQueries());

		// this function puts also proper part of entries list into a model
		preparePagination(model, results, options);
		
		model.put("resultsCount", options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		model.put("summaryWordCount", (Integer)options.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));

		model.put("quickSearch", options.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		
	}
	
	private Map prepareSavedQueries() {
		Map result = new HashMap();
		
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		UserProperties userProperties = getProfileModule().getUserProperties(currentUser.getId());
		if (userProperties != null) {
			Map properties = userProperties.getProperties();
			if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
				Map queries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
				result.put(WebKeys.SEARCH_SAVED_QUERIES, queries.keySet());
			}
		}
		return result;
	}
	//This method rates the people
	public static List ratePeople(List entries) {
		//The same logic and naming has been followed for both people and placess
		return ratePlaces(entries);
	}

	private void prepareRatingsAndFolders(Map model, List entries) {
		List peoplesWithCounters = sortPeopleInEntriesSearchResults(entries);
		List placesWithCounters = sortPlacesInEntriesSearchResults(getBinderModule(), entries);
		
		List peoplesRating = ratePeople(peoplesWithCounters);
		model.put(WebKeys.FOLDER_ENTRYPEOPLE + "_all", peoplesRating);
		
		List peoplesRatingToShow = new ArrayList();
		if (peoplesRating.size() > 20) {
			peoplesRatingToShow.addAll(peoplesRating.subList(0,20));
		} else {
			peoplesRatingToShow.addAll(peoplesRating);
		}
		List placesRating = ratePlaces(placesWithCounters);
		if (placesRating.size() > 20) {
			placesRating = placesRating.subList(0,20);
		}
		model.put(WebKeys.FOLDER_ENTRYPEOPLE, peoplesRatingToShow);
		model.put(WebKeys.FOLDER_ENTRYPLACES, placesRating);

		Map folders = prepareFolderList(placesWithCounters);
		extendEntriesInfo(entries, folders);

		// TODO check and make it better, copied from SearchController
		List entryCommunityTags = BinderHelper.sortCommunityTags(entries);
		List entryPersonalTags = BinderHelper.sortPersonalTags(entries);
		int intMaxHitsForCommunityTags = BinderHelper.getMaxHitsPerTag(entryCommunityTags);
		int intMaxHitsForPersonalTags = BinderHelper.getMaxHitsPerTag(entryPersonalTags);
		int intMaxHits = intMaxHitsForCommunityTags;
		if (intMaxHitsForPersonalTags > intMaxHitsForCommunityTags) intMaxHits = intMaxHitsForPersonalTags;
		entryCommunityTags = BinderHelper.rateCommunityTags(entryCommunityTags, intMaxHits);
		entryPersonalTags = BinderHelper.ratePersonalTags(entryPersonalTags, intMaxHits);

		model.put(WebKeys.FOLDER_ENTRYTAGS, entryCommunityTags);
		model.put(WebKeys.FOLDER_ENTRYPERSONALTAGS, entryPersonalTags);
	}
	
	protected static List sortPlacesInEntriesSearchResults(BinderModule binderModule, List entries) {
		HashMap placeMap = new HashMap();
		ArrayList placeList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique places.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			String id = (String)entry.get("_binderId");
			if (id == null) continue;
			Long bId = new Long(id);
			if (placeMap.get(bId) == null) {
				placeMap.put(bId, new Place(bId,1));
			} else {
				Place p = (Place)placeMap.remove(bId);
				p = new Place(p.getId(),p.getCount()+1);
				placeMap.put(bId,p);
			}
		}
		//sort the hits
		Collection collection = placeMap.values();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			Binder binder=null;
			try {
				binder = binderModule.getBinder(((Place)array[j]).getId());
			} catch (Exception ex) {
				//not access or doesn't exist?
			}
			int count = ((Place)array[j]).getCount();
			Map place = new HashMap();
			place.put(WebKeys.BINDER, binder);
			place.put(WebKeys.SEARCH_RESULTS_COUNT, new Integer(count));
			placeList.add(place);
		}
		return placeList;

	}

	//This method rates the places
	public static List ratePlaces(List entries) {
		List ratedList = new ArrayList();
		int intMaxHitsPerFolder = 0;
		for (int i = 0; i < entries.size(); i++) {
			Map place = (Map) entries.get(i);
			Integer resultCount = (Integer) place.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (i == 0) {
				place.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(100));
				place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brightest");
				intMaxHitsPerFolder = resultCount;
			}
			else {
				int intResultCount = resultCount.intValue();
				Double DblRatingForFolder = ((double)intResultCount/intMaxHitsPerFolder) * 100;
				int intRatingForFolder = DblRatingForFolder.intValue();
				place.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(DblRatingForFolder.intValue()));
				if (intRatingForFolder > 80 && intRatingForFolder <= 100) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brightest");
				}
				else if (intRatingForFolder > 50 && intRatingForFolder <= 80) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_brighter");
				}
				else if (intRatingForFolder > 20 && intRatingForFolder <= 50) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_bright");
				}
				else if (intRatingForFolder > 10 && intRatingForFolder <= 20) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_dim");
				}
				else if (intRatingForFolder >= 0 && intRatingForFolder <= 10) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "ss_very_dim");
				}
			}
			ratedList.add(place);
		}
		return ratedList;
	}
	
	private Map prepareFolderList(List placesWithCounters) {
		Map folderMap = new HashMap();
		Iterator it = placesWithCounters.iterator();
		while (it.hasNext()) {
			Map place = (Map) it.next();
			Binder binder = (Binder)place.get(WebKeys.BINDER);
			if (binder == null) continue;
			Binder parentBinder = binder.getParentBinder();
			String parentBinderTitle = "";
			if (parentBinder != null) parentBinderTitle = parentBinder.getTitle() + " // ";
			folderMap.put(binder.getId(), parentBinderTitle + binder.getTitle());
		}
		return folderMap;
	}
	
	private void extendEntriesInfo(List entries, Map folders) {
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Map entry = (Map) it.next();
			if (entry.get(WebKeys.SEARCH_BINDER_ID) != null) {
				entry.put(WebKeys.BINDER_TITLE, folders.get(Long.parseLong((String)entry.get(WebKeys.SEARCH_BINDER_ID))));
			}
		}
	}
	
	private void preparePagination(Map model, Map results, Map options) {

		int totalRecordsFound = (Integer) results.get(ObjectKeys.SEARCH_COUNT_TOTAL);
		int countReturned = (Integer) results.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
		int pageInterval = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options != null && options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) {
			pageInterval = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		}
		
		int pagesCount = (int)Math.ceil((double)totalRecordsFound / pageInterval);
		
		int firstOnCurrentPage = 0;
		if (options != null && options.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) {
			firstOnCurrentPage = (Integer) options.get(ObjectKeys.SEARCH_USER_OFFSET);
		}
		
		if (firstOnCurrentPage > totalRecordsFound || firstOnCurrentPage < 0) {
			firstOnCurrentPage = 0;
		}

		int currentPageNo = firstOnCurrentPage / pageInterval + 1;
		int lastOnCurrentPage = firstOnCurrentPage + pageInterval;
		if ((countReturned - firstOnCurrentPage)<pageInterval) {
			//If asking for more than search results returned, don't go beyond the ammount returned
			//TODO Make the search request ask for the proper results (i.e., set the "starting count" properly on the search)
			lastOnCurrentPage = firstOnCurrentPage + (countReturned-firstOnCurrentPage);
			if (firstOnCurrentPage < 0) firstOnCurrentPage = 0;
		}
		
		List shownOnPage = ((List) results.get(ObjectKeys.SEARCH_ENTRIES)).subList(firstOnCurrentPage, lastOnCurrentPage);
		checkFileIds(shownOnPage);
		model.put(WebKeys.FOLDER_ENTRIES, shownOnPage);
		model.put(WebKeys.PAGE_NUMBER, currentPageNo);
		
		List pageNos = new ArrayList();
		for (int i = currentPageNo - 2; i <= currentPageNo; i++) {
			if (i > 0) {
				pageNos.add(i);
			}
		}
		
		for (int i = currentPageNo+1; i <= currentPageNo+2; i++) {
			if (i <= pagesCount) {
				pageNos.add(i);
			}
		}
		model.put(WebKeys.PAGE_NUMBERS, pageNos);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, totalRecordsFound);
		model.put(WebKeys.PAGE_START_INDEX, firstOnCurrentPage+1);
		model.put(WebKeys.PAGE_END_INDEX, lastOnCurrentPage);
	}
	
	public static void checkFileIds(List entries) {
		Iterator it = entries.iterator();
		while (it.hasNext()) {
			Map entry = (Map)it.next();
			if (entry.containsKey(ObjectKeys.FIELD_FILE_ID)) {
				try{ 
					Set files = ((SearchFieldResult)entry.get(ObjectKeys.FIELD_FILE_ID)).getValueSet();
					if (!files.isEmpty()) {
						entry.put(ObjectKeys.FIELD_FILE_ID, files.iterator().next());
					}
				} catch (Exception e) {
					// do nothing, if not set - it is only one or no _fileID
				}
			}
		}
	}
	
	
	private void storeQueryInTabs(Tabs tabs, Document query, Map options, String newTab) {
		int targetTab;
		if (newTab.equals(NEW_TAB_VALUE)) {
			targetTab = -1;
		} else {
			targetTab = tabs.getCurrentTab();
		}
		boolean clearTab = true;
		tabs.setCurrentTab(tabs.findTab(query, options, clearTab, targetTab));
	}
	
	public static Tabs setupTabs(PortletRequest request) throws PortletRequestBindingException {
		Tabs tabs = new Tabs(request);
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
		if (tabId != null) {
			tabs.setCurrentTab(tabId.intValue());
		}
		
		return tabs;
	}
	
	
	private Map prepareSearchOptions(RenderRequest request) {
		
		Map options = new HashMap();
		
		//If the entries per page is not present in the user properties, then it means the
		//number of records per page is obtained from the ssf properties file, so we do not have 
		//to worry about checking the old and new number or records per page.
		
		//Getting the entries per page from the user properties
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userProp = getProfileModule().getUserProperties(user.getId());
		String entriesPerPage = (String) userProp.getProperty(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE);
		if (entriesPerPage == null || "".equals(entriesPerPage)) {
			entriesPerPage = SPropsUtil.getString("search.records.listed");
		}
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		
		Integer searchUserOffset = PortletRequestUtils.getIntParameter(request, ObjectKeys.SEARCH_USER_OFFSET, 0);
			
		// TODO - implement it better(?) this stuff is to get from lucene proper entries,  
		// to get the ~ proper rankings we get from lucene MoreEntriesCounter more hits as max on page
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_MAX_HITS, new Integer(entriesPerPage));
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_SUMMARY_WORDS, new Integer(20));
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits + 200;
		if (searchUserOffset > 200) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		Integer pageNo = PortletRequestUtils.getIntParameter(request, Tabs.PAGE, 1);
		options.put(Tabs.PAGE, pageNo);
		
		
		
		Boolean quickSearch = PortletRequestUtils.getBooleanParameter(request, WebKeys.SEARCH_FORM_QUICKSEARCH, Boolean.FALSE);
		options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, quickSearch);

		if (quickSearch) {
			options.put(Tabs.TITLE, NLT.get("searchForm.quicksearch.Title"));
			options.put(Tabs.TAB_SEARCH_TEXT, NLT.get("searchForm.quicksearch.Title"));			
		} else {
			options.put(Tabs.TITLE, NLT.get("searchForm.advanced.Title"));
			options.put(Tabs.TAB_SEARCH_TEXT, NLT.get("searchForm.advanced.Title"));
		} 

		// TODO - if needed - implement dynamic
//		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE.toString());
//		options.put(ObjectKeys.SEARCH_SORT_BY, IndexUtils.SORTNUMBER_FIELD);
		
		return options;
	}

	// This method reads thru the results from a search, finds the principals, 
	// and places them into an array that is ordered by the number of times
	// they show up in the results list.
	public static List sortPeopleInEntriesSearchResults(List entries) {
		HashMap userMap = new HashMap();
		ArrayList userList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique principals.  Keep a count to see
		// if any are more active than others.
		for (int i = 0; i < entries.size(); i++) {
			Map entry = (Map)entries.get(i);
			Principal user = (Principal)entry.get(WebKeys.PRINCIPAL);
			if (userMap.get(user.getId()) == null) {
				userMap.put(user.getId(), new Person(user.getId(),user,1));
			} else {
				Person p = (Person)userMap.remove(user.getId());
				p.incrCount();
				userMap.put(user.getId(),p);
			}
		}
		//sort the hits
		Collection collection = userMap.values();
		Object[] array = collection.toArray();
		Arrays.sort(array);
		
		for (int j = 0; j < array.length; j++) {
			HashMap person = new HashMap();
			Principal user = (Principal) ((Person)array[j]).getUser();
			int intUserCount = ((Person)array[j]).getCount();
			person.put(WebKeys.USER_PRINCIPAL, user);
			person.put(WebKeys.SEARCH_RESULTS_COUNT, new Integer(intUserCount));
			userList.add(person);
		}
		return userList;
	}
	
	// This class is used by the following method as a way to sort
	// the values in a hashmap
	public static class Person implements Comparable {
		long id;
		int count;
		Principal user;

		public Person (long id, Principal p, int count) {
			this.id = id;
			this.user = p;
			this.count = count;
		}
		
		public int getCount() {
			return this.count;
		}

		public void incrCount() {
			this.count += 1;
		}
		
		public Principal getUser() {
			return this.user;
		}
		
		public int compareTo(Object o) {
			Person p = (Person) o;
			int result = this.getCount() < p.getCount() ? 1 : 0;
			return result;
			}
	}
	// This class is used by the following method as a way to sort
	// the values in a hashmap
	public static class Place implements Comparable {
		long id;
		int count;

		public Place (long id, int count) {
			this.id = id;
			this.count = count;
		}
		
		public int getCount() {
			return this.count;
		}

		public void incrCount() {
			this.count += 1;
		}
		
		public long getId() {
			return this.id;
		}
		
		public int compareTo(Object o) {
			Place p = (Place) o;
			int result = this.getCount() < p.getCount() ? 1 : 0;
			return result;
			}
	}	
	
	protected void buildToolbars(Map model, RenderRequest request) {
		model.put(WebKeys.FOOTER_TOOLBAR,  buildFooterToolbar(model, request).getToolbar());
	}
	
	private Toolbar buildFooterToolbar(Map model, RenderRequest request) {
		Toolbar footerToolbar = new Toolbar();
		List users = (List)model.get(WebKeys.FOLDER_ENTRIES);
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
	
}
