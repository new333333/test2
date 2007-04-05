/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.shared.SearchUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchEntryFilter;
import com.sitescape.team.search.SearchFilter;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.FilterHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;

/**
 * @author Renata Nowicka
 *
 */

public class AdvancedSearchController extends AbstractBinderController {
	
	public static final String NEW_TAB_VALUE = "1";
	
	public static String SearchBlockTypeCreationDate = "creation_date";
	public static String SearchBlockTypeModificationDate = "modification_date";
	public static String SearchBlockTypeWorkflow = "workflow";
	public static String SearchBlockTypeEntry = "entry";
	public static String SearchBlockTypeAuthor = "creator_by_id";
	public static String SearchBlockTypeTag = "tag";
	public static String SearchBlockType = "type";
		
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.SEARCH_FORM_FORM)) {
			// TODO: load saved query if necessary  
		} else if (op.equals(WebKeys.SEARCH_RESULTS)) {
			// TODO search
			// prepare query from parameters
			// save query
			// do search
			// put search result in model
		}
		
		// set form data for the render method
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
        
        if (op.equals(WebKeys.SEARCH_RESULTS)) {
        	model = prepareSearchResultData(request);
        	return new ModelAndView("search/search_result", model);
        } else if (op.equals(WebKeys.SEARCH_VIEW_PAGE)) {
        	model = prepareSearchResultPage(request);
        	return new ModelAndView("search/search_result", model);
        } else {
        	model = prepareSearchFormData(request);
        	return new ModelAndView("search/search_form", model);
        }
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
		prepareSearchResultPage(model, query, options, tabs, "");
		
		return model;
	}
	
	private void actualizeOptions(Map options, RenderRequest request) {
		int pageNo = PortletRequestUtils.getIntParameter(request, WebKeys.URL_PAGE_NUMBER, 1);
		int defaultMaxOnPage = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) defaultMaxOnPage = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		int maxOnPage = PortletRequestUtils.getIntParameter(request, WebKeys.SEARCH_FORM_MAX_HITS, defaultMaxOnPage);
		int userOffset = (pageNo - 1) * maxOnPage;
		
		Integer searchLuceneOffset = 0;
		if (userOffset > 200) {
				searchLuceneOffset = userOffset-200;
				userOffset = searchLuceneOffset;
		}
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, userOffset);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxOnPage);
	}
	private Map getOptionsFromTab(Map tab) {
		Map options = new HashMap();
		if (tab.containsKey(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE)) options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, tab.get(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE));
		if (tab.containsKey(ObjectKeys.SEARCH_OFFSET)) options.put(ObjectKeys.SEARCH_OFFSET, tab.get(ObjectKeys.SEARCH_OFFSET));
		if (tab.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) options.put(ObjectKeys.SEARCH_USER_OFFSET, tab.get(ObjectKeys.SEARCH_USER_OFFSET));
		if (tab.containsKey(ObjectKeys.SEARCH_MAX_HITS)) options.put(ObjectKeys.SEARCH_MAX_HITS, tab.get(ObjectKeys.SEARCH_MAX_HITS));
		if (tab.containsKey(ObjectKeys.SEARCH_USER_MAX_HITS)) options.put(ObjectKeys.SEARCH_USER_MAX_HITS, tab.get(ObjectKeys.SEARCH_USER_MAX_HITS));

		return options;
	}
	
	private Document getQueryFromTab(Map tab) {
		Document query = (Document) tab.get(Tabs.QUERY_DOC);
		return query;
	}
	
	private Map prepareSearchFormData(RenderRequest request) throws PortletRequestBindingException {
		// TODO implement
		Map formData = request.getParameterMap();
		Map model = new HashMap();
		
		return model;
	}

	private Map prepareSearchResultData(RenderRequest request) throws Exception {
		Map model = new HashMap();
		Tabs tabs = setupTabs(request);
		String tabType = tabs.getTabType(tabs.getCurrentTab());
		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");

		Document searchQuery = getSearchQuery(request);
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
		model.put("filterMap", convertedToDisplay(query));
		
		// for test only
		model.put("query", query.asXML());
		model.put("options", options);
		model.put("results", results);
		
		SearchUtils.filterEntryAttachmentResults(results);
	
		prepareRatings(model, (List) results.get(ObjectKeys.SEARCH_ENTRIES));

		// this function puts also proper part of entries list into a model
		preparePagination(model, results, options);

		model.put("quickSearch", false);
		// TODO implement - get values from user setup? options?
		model.put("summaryWordCount", 20);		
	}
	
	private void prepareRatings(Map model, List entries) {
		List peoplesWithCounters = SearchController.sortPeopleInEntriesSearchResults(entries);
		List placesWithCounters = SearchController.sortPlacesInEntriesSearchResults(getBinderModule(), entries);
		model.put(WebKeys.FOLDER_ENTRYPEOPLE, SearchController.ratePeople(peoplesWithCounters));
		model.put(WebKeys.FOLDER_ENTRYPLACES, SearchController.ratePlaces(placesWithCounters));
		
		// TODO check and make it better, copied from SearchController
		List entryCommunityTags = BinderHelper.sortCommunityTags(entries);
		List entryPersonalTags = BinderHelper.sortPersonalTags(entries);
		
		int intMaxHitsForCommunityTags = BinderHelper.getMaxHitsPerTag(entryCommunityTags);
		int intMaxHitsForPersonalTags = BinderHelper.getMaxHitsPerTag(entryPersonalTags);
		
		int intMaxHits = intMaxHitsForCommunityTags;
		if (intMaxHitsForPersonalTags > intMaxHitsForCommunityTags) intMaxHits = intMaxHitsForPersonalTags;
		
		entryCommunityTags = BinderHelper.rateCommunityTags(entryCommunityTags, intMaxHits);
		entryPersonalTags = BinderHelper.ratePersonalTags(entryPersonalTags, intMaxHits);

		entryCommunityTags = BinderHelper.determineSignBeforeTag(entryCommunityTags, "");
		entryPersonalTags = BinderHelper.determineSignBeforeTag(entryPersonalTags, "");

		model.put(WebKeys.FOLDER_ENTRYTAGS, entryCommunityTags);
		model.put(WebKeys.FOLDER_ENTRYPERSONALTAGS, entryPersonalTags);
	}
	
	private void preparePagination(Map model, Map results, Map options) {

		int totalRecordsFound = (Integer) results.get(ObjectKeys.SEARCH_COUNT_TOTAL);
		int firstOnCurrentPage = 0;
		if (options != null && options.containsKey(ObjectKeys.SEARCH_USER_OFFSET)) firstOnCurrentPage = (Integer) options.get(ObjectKeys.SEARCH_USER_OFFSET);
		
		int countReturned = (Integer) results.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
		int pageInterval = ObjectKeys.SEARCH_MAX_HITS_DEFAULT;
		if (options != null && options.get(ObjectKeys.SEARCH_USER_MAX_HITS) != null) pageInterval = (Integer) options.get(ObjectKeys.SEARCH_USER_MAX_HITS);
		int lastOnCurrentPage = firstOnCurrentPage + pageInterval;
		if ((countReturned - firstOnCurrentPage)<pageInterval) lastOnCurrentPage = firstOnCurrentPage + (countReturned-firstOnCurrentPage);
		
		int currentPageNo = (firstOnCurrentPage + pageInterval)/pageInterval; 
		
		model.put(WebKeys.FOLDER_ENTRIES, ((List) results.get(ObjectKeys.SEARCH_ENTRIES)).subList(firstOnCurrentPage, lastOnCurrentPage));
		model.put(WebKeys.PAGE_NUMBER, currentPageNo);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, totalRecordsFound);
		model.put(WebKeys.PAGE_START_INDEX, firstOnCurrentPage+1);
		model.put(WebKeys.PAGE_END_INDEX, lastOnCurrentPage);
	}
	
	private Map convertedToDisplay(Document searchQuery) {
		Map searchFormData = new HashMap();
		if (searchQuery != null) { 
	    	Element rootElement = searchQuery.getRootElement();
	    	List liFilterTerms = rootElement.selectNodes(FilterHelper.FilterTerms);

	    	System.out.println("Query: "+searchQuery.asXML());
	    	System.out.println("Terms size: "+liFilterTerms.size());

	    	// read the joiner information, probably the size will be always 1 so it can be in loop
	    	for (int i = 0; i < liFilterTerms.size(); i++) {
	    		Element filterTerms = (Element) liFilterTerms.get(i);

	    		String andJoiner = Boolean.FALSE.toString();
	    		if (filterTerms.attributeValue(FilterHelper.FilterAnd, "").equals(Boolean.TRUE.toString())) {
	    			andJoiner = Boolean.TRUE.toString();
	    		}
	    		
	    		String searchedText = "";
	    		String searchedTags = "";
	    		String searchedAuthors = "";
	    		Map blocks = new HashMap();
	    		List liFilterTermsTerm = filterTerms.selectNodes("./" + FilterHelper.FilterTerm);
	    		if (liFilterTermsTerm.size() > 0) {
	            	for (int j = 0; j < liFilterTermsTerm.size(); j++) {
	    	    		Element filterTerm = (Element) liFilterTermsTerm.get(j);
	    	    		String filterType = filterTerm.attributeValue(FilterHelper.FilterType, "");
	    	    		if (filterType.equals(FilterHelper.FilterTypeSearchText)) {
	    	    			if (searchedText.equals("")) searchedText = filterTerm.getText(); 
	    	    			else searchedText = searchedText.concat(" "+filterTerm.getText()); 
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeAuthor)) {
	    	    			if (searchedAuthors.equals("")) searchedAuthors = filterTerm.getText(); 
	    	    			else  searchedAuthors = searchedAuthors.concat(" "+filterTerm.getText());
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeTags)) {
	    	    			if (searchedTags.equals("")) searchedTags = filterTerm.getText(); 
	    	    			else  searchedTags = searchedTags.concat(" "+filterTerm.getText());
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeEntry)) {
	    	    			if (blocks.get(SearchBlockTypeEntry) == null) blocks.put(SearchBlockTypeEntry, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeEntry)).add(createEntryBlock(filterTerm));
	    	    		} else if ( filterType.equals(FilterHelper.FilterTypeCreatorById)) {
	    	    			if (blocks.get(SearchBlockTypeAuthor) == null) blocks.put(SearchBlockTypeAuthor, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeAuthor)).add(createCreatorBlock(filterTerm));
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeWorkflow)) {
	    	    			if (blocks.get(SearchBlockTypeWorkflow) == null) blocks.put(SearchBlockTypeWorkflow, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeWorkflow)).add(createWorkflowBlock(filterTerm));
	    	    		} else if (filterType.equals(FilterHelper.FilterTypeCommunityTagSearch)) {
	    	    			if (blocks.get(SearchBlockTypeTag) == null) blocks.put(SearchBlockTypeTag, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeTag)).add(createTagBlock(filterTerm));
	    	    		} else if (filterType.equals(FilterHelper.FilterTypePersonalTagSearch)) {
	    	    			if (blocks.get(SearchBlockTypeTag) == null) blocks.put(SearchBlockTypeTag, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeTag)).add(createTagBlock(filterTerm));
	    		    	} else if (filterType.equals(FilterHelper.FilterTypeDate)) {
	    	    			if (blocks.get(SearchBlockTypeCreationDate) == null) blocks.put(SearchBlockTypeCreationDate, new ArrayList());
	    	    			((List)blocks.get(SearchBlockTypeCreationDate)).add(createDateBlock(filterTerm));
	    		    		// addDateRange(block, filterTerm.attributeValue(FilterHelper.FilterElementName, ""), filterTerm.attributeValue(FilterHelper.FilterStartDate, ""),filterTerm.attributeValue(FilterHelper.FilterEndDate, ""));
	    		    	}
	            	}
	    		}
	    		searchFormData.put(FilterHelper.SearchText, searchedText);
	    		searchFormData.put(FilterHelper.SearchAuthors, searchedAuthors);
	    		searchFormData.put(FilterHelper.SearchTags, searchedTags);
	    		searchFormData.put(FilterHelper.SearchJoiner, andJoiner);
	    		searchFormData.put(FilterHelper.SearchAdditionalFilters, blocks);
	    	}
		}
		return searchFormData;
	}
	
	private Map createEntryBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		
		return block;
	}
	private Map createCreatorBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		return block;
	}
	private Map createWorkflowBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));	
		block.put(FilterHelper.SearchWorkflowId, filterTerm.attributeValue(FilterHelper.FilterWorkflowDefId, ""));
		
		List steps = filterTerm.selectNodes(FilterHelper.FilterWorkflowStateName);
		List modelSteps = new ArrayList();
		for (int i = 0; i < steps.size(); i++) {
			Element step = (Element) steps.get(i);
			modelSteps.add(step.getText());
		}
		block.put(FilterHelper.FilterWorkflowStateName, modelSteps);
		return block;
	}
	private Map createTagBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		return block;
	}
	private Map createDateBlock(Element filterTerm) {
		Map block = new HashMap();
		block.put(SearchBlockType, filterTerm.attributeValue(FilterHelper.FilterType, ""));
		return block;
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
	
	private Tabs setupTabs(RenderRequest request) throws PortletRequestBindingException {
		Tabs tabs = new Tabs(request);
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
		if (tabId != null) tabs.setCurrentTab(tabId.intValue());
		return tabs;
	}
	
	private Map prepareSearchTextOptions(String searchText) {
		Map options = new HashMap();
		//Get the search text to use it for the tab title 
		if (!searchText.equals("")) {
			options.put(Tabs.TITLE, searchText);
			options.put(Tabs.TAB_SEARCH_TEXT, searchText);
		}
		return options;
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
			options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(SPropsUtil.getString("search.records.listed")));
		} else {
			options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		}
		
		Integer searchUserOffset = PortletRequestUtils.getIntParameter(request, ObjectKeys.SEARCH_USER_OFFSET, 0);
			
		// TODO - implement it better(?) this stuff is to get from lucene proper entries,  
		// to get the ~ proper rankings we get from lucene 200 more hits as max on page
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = PortletRequestUtils.getIntParameter(request, ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_DEFAULT);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchUserOffset + maxHits + 200;
		if (searchUserOffset > 200) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);

		Integer pageNo = PortletRequestUtils.getIntParameter(request, Tabs.PAGE, 1);
		options.put(Tabs.PAGE, pageNo);
		
		// TODO - if needed - implement dynamic
//		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE.toString());
//		options.put(ObjectKeys.SEARCH_SORT_BY, IndexUtils.SORTNUMBER_FIELD);
		
		return options;
	}

	
	private Document getSearchQuery(RenderRequest request) {
		Boolean joiner = PortletRequestUtils.getBooleanParameter(request, FilterHelper.SearchJoiner, true);
		SearchEntryFilter searchFilter = new SearchEntryFilter(joiner);
		
		String searchText = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchText, "");
		if (!searchText.equals("")) {
			searchFilter.addText(searchText);
		}
		
		String authors = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchAuthors, "");
		if (authors!=null && !authors.equals("")) {
			String[] authorsArr = authors.split(" ");
			for (int i=0; i<authorsArr.length; i++) {
				searchFilter.addCreator(authorsArr[i]);
			}
		}
		
		String tags =  PortletRequestUtils.getStringParameter(request, FilterHelper.SearchTags, "");
		if (tags!=null && !tags.equals("")) {
			String[] tagsArr = tags.split(" ");
			for (int i=0; i<tagsArr.length; i++) {
				searchFilter.addTag(tagsArr[i]);
			}
		}
		
		String[] numbers = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchNumbers, "").split(" ");
		String[] types = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchTypes, "").split(" ");
		
		for (int i=0; i<types.length; i++) {
			if (types[i].equals(SearchBlockTypeWorkflow)) {
				String workflowId =  PortletRequestUtils.getStringParameter(request, FilterHelper.SearchWorkflowId.concat(numbers[i]), "");
				String[] workflowSteps =  PortletRequestUtils.getStringParameters(request, FilterHelper.SearchWorkflowStep.concat(numbers[i]));
				if (!workflowId.equals("")) searchFilter.addWorkflow(workflowId, workflowSteps);
			}
			if (types[i].equals(SearchBlockTypeEntry)) {
				String entryTypeId = PortletRequestUtils.getStringParameter(request, FilterHelper.FilterEntryDefIdField.concat(numbers[i]), "");
				String entryFieldId = PortletRequestUtils.getStringParameter(request, FilterHelper.FilterElementNameField.concat(numbers[i]), "");
				String[] value = PortletRequestUtils.getStringParameters(request, FilterHelper.FilterElementValueField.concat(numbers[i]));
//				String[] valueType = PortletRequestUtils.getStringParameters(request, FilterElementValueTypeField + String.valueOf(i));
//				if (valueType.length > 0 && valueType[0].equals("checkbox")) {
//					//Fix up the value for a checkbox. Make it either true or false
//					if (value.length > 0 && value[0].equals("on")) {
//						value[0] = "true";
//					} else if (value.length > 0) {
//						value[0] = "false";
//					} else {
//						value = new String[] {"false"};
//					}
//				}
				
				if (!entryTypeId.equals("")) searchFilter.addEntryType(entryTypeId, entryFieldId, value);

			}
			if (types[i].equals(SearchBlockTypeCreationDate) || types[i].equals(SearchBlockTypeModificationDate)) {
				String startDate = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchStartDate.concat(numbers[i]), "");
				String endDate = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchEndDate.concat(numbers[i]), "");
				SimpleDateFormat inputFormater = new SimpleDateFormat("yyyy-MM-dd");
				User user = RequestContextHolder.getRequestContext().getUser();
				inputFormater.setTimeZone(user.getTimeZone());
				Date startD = null;
				Date endD = null;
				if (!startDate.equals(""))
					try {startD = inputFormater.parse(startDate);} catch (ParseException e) {System.out.println("Parse exception by date:"+startDate);}
				if (!endDate.equals(""))	
					try {endD = inputFormater.parse(endDate);} catch (ParseException e) {System.out.println("Parse exception by date:"+endDate);}
				if (types[i].equals(SearchBlockTypeCreationDate))
					searchFilter.addCreationDateRange(startD, endD);
				else if (types[i].equals(SearchBlockTypeModificationDate))
					searchFilter.addModificationDateRange(startD, endD);
				
			}
			if (types[i].equals(SearchBlockTypeAuthor)) {
				String author = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchAuthors.concat(numbers[i]).concat("_selected"), "");
				if (!author.equals("")) searchFilter.addCreatorById(author);
			}
			if (types[i].equals(SearchBlockTypeTag)) {
				String personalTag = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchPersonalTags.concat(numbers[i]), "");
				String communityTag = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchCommunityTags.concat(numbers[i]), "");
				if (!personalTag.equals("")) searchFilter.addPersonalTag(personalTag);
				if (!communityTag.equals("")) searchFilter.addCommunityTag(communityTag);
			}
		}
		return searchFilter.getFilter();
	}
}
