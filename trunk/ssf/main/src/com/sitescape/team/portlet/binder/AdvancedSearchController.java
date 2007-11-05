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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.text.DateFormat;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.search.filter.SearchFilterRequestParser;
import com.sitescape.team.search.filter.SearchFilterToMapConverter;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.team.web.util.WebHelper;

/**
 * @author Renata Nowicka
 *
 */

public class AdvancedSearchController extends AbstractBinderController {
	
	private static Log logger = LogFactory.getLog(AdvancedSearchController.class);
	
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
		}	
		Map model = new HashMap();
		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);
		
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
        Tabs tabs = Tabs.getTabs(request);
		model.put(WebKeys.TABS, tabs);

       if (op.equals(WebKeys.SEARCH_RESULTS)) {
        	model.putAll(prepareSearchResultData(request, tabs));
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
        	model.putAll(prepareSearchResultPage(request, tabs));
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
        	model.putAll(prepareSavedQueryResultData(request, tabs));
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
        	model.putAll(prepareSearchFormData(request));
        	
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
    	
		Workspace ws = getWorkspaceModule().getWorkspace();
		Document tree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);
	}
	
	private Map prepareSearchResultPage(RenderRequest request, Tabs tabs) {
		Map model = new HashMap();

		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID, -1);
		//new search
		Tabs.TabEntry tab = tabs.findTab(Tabs.SEARCH, tabId);
		if (tab == null) return prepareSearchResultData(request, tabs);
		// get query and options from tab
		Document searchQuery = tab.getQueryDoc();
		Map options = getOptionsFromTab(tab);
		Integer pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, -1);
		if (pageNo != -1) options.put(Tabs.PAGE, pageNo);				

		// get page no and actualize options
		// execute query
		// actualize tabs info
		actualizeOptions(options, request);
		Map results =  getBinderModule().executeSearchQuery(searchQuery, options);
		prepareSearchResultPage(results, model, searchQuery, options, tab);
		
		return model;
	}

	private Map prepareSavedQueryResultData(RenderRequest request, Tabs tabs) throws PortletRequestBindingException {
		Map model = new HashMap();

		String queryName = PortletRequestUtils.getStringParameter(request, WebKeys.URL_SEARCH_QUERY_NAME, "");
		User currentUser = RequestContextHolder.getRequestContext().getUser();
		
		// get query and options from tab		
		Document searchQuery = getSavedQuery(queryName, getProfileModule().getUserProperties(currentUser.getId()));
		
		// get page no and actualize options
		// execute query
		// actualize tabs info
		Map options = prepareSearchOptions(request);
		actualizeOptions(options, request);

		options.put(Tabs.TITLE, queryName);
		Map results =  getBinderModule().executeSearchQuery(searchQuery, options);
		
		Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
		
		prepareSearchResultPage(results, model, searchQuery, options, tab);
		
		return model;
	}
	
	private Map prepareSearchResultData(RenderRequest request, Tabs tabs) {
		Map model = new HashMap();

		SearchFilterRequestParser requestParser = new SearchFilterRequestParser(request, getDefinitionModule());
		Document searchQuery = requestParser.getSearchQuery();
		Map options = prepareSearchOptions(request);
		Map results =  getBinderModule().executeSearchQuery(searchQuery, options);
		
		Tabs.TabEntry tab = tabs.addTab(searchQuery, options);
		
		prepareSearchResultPage(results, model, searchQuery, options, tab);

		return model;
	}
	
	private void prepareSearchResultPage (Map results, Map model, Document query, Map options, Tabs.TabEntry tab) {
		
		model.put(WebKeys.URL_TAB_ID, tab.getTabId());
		//save tab options
		tab.setData(options);
		SearchFilterToMapConverter searchFilterConverter = new SearchFilterToMapConverter(query, getDefinitionModule(), getProfileModule(), getBinderModule());
		model.putAll(searchFilterConverter.convertAndPrepareFormData());
		
		// SearchUtils.filterEntryAttachmentResults(results);
		prepareRatingsAndFolders(model, (List) results.get(ObjectKeys.SEARCH_ENTRIES));
		model.putAll(prepareSavedQueries());

		// this function puts also proper part of entries list into a model
		preparePagination(model, results, options, tab);
		
		model.put("resultsCount", options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		model.put("summaryWordCount", (Integer)options.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));

		model.put("quickSearch", options.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		
	}
	private Map prepareSearchFormData(RenderRequest request) throws PortletRequestBindingException {
		Map options = prepareSearchOptions(request);
		Map model = new HashMap();
		model.put("resultsCount", options.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		model.put("quickSearch", false);
		
		model.putAll(prepareSavedQueries());
		
		Workspace ws = getWorkspaceModule().getWorkspace();
		Document tree = getWorkspaceModule().getDomWorkspaceTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
		model.put(WebKeys.DOM_TREE, tree);
		
		return model;
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

		Integer pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, 1);
		options.put(Tabs.PAGE, pageNo);				
		
		Boolean quickSearch = PortletRequestUtils.getBooleanParameter(request, WebKeys.SEARCH_FORM_QUICKSEARCH, Boolean.FALSE);
		options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, quickSearch);

		if (quickSearch) {
			options.put(Tabs.TITLE, NLT.get("searchForm.quicksearch.Title") + " " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale()).format(new Date()));
		} else {
			options.put(Tabs.TITLE, NLT.get("searchForm.advanced.Title") + " " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale()).format(new Date()));
		} 
	
		return options;
	}
	private void actualizeOptions(Map options, RenderRequest request) {
		Integer pageNo = (Integer)options.get(Tabs.PAGE);
		if ((pageNo == null) || pageNo < 1) {
			pageNo = 1;
			
		}
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
	private Map getOptionsFromTab(Tabs.TabEntry tab) {
		Map options = new HashMap();
		Map tabData = tab.getData();
		if (tabData.containsKey(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE)) options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, tabData.get(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE));
		if (tabData.containsKey(ObjectKeys.SEARCH_OFFSET)) options.put(ObjectKeys.SEARCH_OFFSET, tabData.get(ObjectKeys.SEARCH_OFFSET));
		if (tabData.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) options.put(ObjectKeys.SEARCH_USER_OFFSET, tabData.get(ObjectKeys.SEARCH_USER_OFFSET));
		if (tabData.containsKey(ObjectKeys.SEARCH_MAX_HITS)) options.put(ObjectKeys.SEARCH_MAX_HITS, tabData.get(ObjectKeys.SEARCH_MAX_HITS));
		if (tabData.containsKey(ObjectKeys.SEARCH_USER_MAX_HITS)) options.put(ObjectKeys.SEARCH_USER_MAX_HITS, tabData.get(ObjectKeys.SEARCH_USER_MAX_HITS));
		if (tabData.containsKey(Tabs.TITLE)) options.put(Tabs.TITLE, tabData.get(Tabs.TITLE));
		if (tabData.containsKey(WebKeys.SEARCH_FORM_SUMMARY_WORDS)) options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, tabData.get(WebKeys.SEARCH_FORM_SUMMARY_WORDS));
		if (tabData.containsKey(WebKeys.SEARCH_FORM_QUICKSEARCH)) options.put(WebKeys.SEARCH_FORM_QUICKSEARCH, tabData.get(WebKeys.SEARCH_FORM_QUICKSEARCH));
		if (tabData.containsKey(Tabs.PAGE)) options.put(Tabs.PAGE, tabData.get(Tabs.PAGE));
		return options;
	}
	
	public Document getSavedQuery(String queryName, UserProperties userProperties) {
		
		Map properties = userProperties.getProperties();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			Map queries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
			Object q = queries.get(queryName);
			if (q == null) return null;
			if (q instanceof String) {
				try {
					return DocumentHelper.parseText((String)q);
				} catch (Exception ex) {
					queries.remove(queryName);
					getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, queries);
				};
			}
			//In v1 these are stored as documents; shouldn't be because the hibernate dirty check always fails causing updates
			if (q instanceof Document) {
				queries.put(queryName, ((Document)q).asXML());
				getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, queries);
				return (Document)q;
			}
		
		}
		return null;
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
	
	private void preparePagination(Map model, Map results, Map options, Tabs.TabEntry tab) {
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
		
		List workflows = new ArrayList();
		if (WebHelper.isUserLoggedIn(request)) {
			workflows = DefinitionHelper.getDefinitions(Definition.WORKFLOW);
		}
		
		Map model = new HashMap();
		model.put(WebKeys.WORKFLOW_DEFINITION_MAP, workflows);
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
		List entryTypes = new ArrayList();
		if (WebHelper.isUserLoggedIn(request)) {
			entryTypes = DefinitionHelper.getDefinitions(Definition.FOLDER_ENTRY);
		}
		model.put(WebKeys.ENTRY, entryTypes);
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
			model.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, fieldsData);
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
			options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
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

}
