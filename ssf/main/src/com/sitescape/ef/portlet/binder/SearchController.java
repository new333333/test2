package com.sitescape.ef.portlet.binder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.util.ResolveIds;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Tabs;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.util.SPropsUtil;

/**
 * @author Peter Hurley
 *
 */
public class SearchController extends AbstractBinderController {
	public void handleActionRequestAfterValidation(ActionRequest request, 
			ActionResponse response) throws Exception {

		Map formData = request.getParameterMap();
		User user = RequestContextHolder.getRequestContext().getUser();

		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map updates = new HashMap();
			updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
		
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_COLUMNS)) {
			if (formData.containsKey("okBtn")) {
				Map columns = new HashMap();
				String[] columnNames = new String[] {"folder", "number", "title", "state", "author", "date"};
				for (int i = 0; i < columnNames.length; i++) {
					columns.put(columnNames[i], PortletRequestUtils.getStringParameter(request, columnNames[i], ""));
				}
				getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_SEARCH_RESULTS_FOLDER_COLUMNS, columns);
				//Reset the column positions to the default
			   	getProfileModule().setUserProperty(user.getId(), WebKeys.SEARCH_RESULTS_COLUMN_POSITIONS, "");
			} else if (formData.containsKey("defaultBtn")) {
				getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_SEARCH_RESULTS_FOLDER_COLUMNS, null);
				//Reset the column positions to the default
			   	getProfileModule().setUserProperty(user.getId(), WebKeys.SEARCH_RESULTS_COLUMN_POSITIONS, "");
			}
		} else if (op.equals(WebKeys.OPERATION_SAVE_SEARCH_SORT_INFO)) {
			String folderSortBy = PortletRequestUtils.getStringParameter(request, WebKeys.FOLDER_SORT_BY, "");
			String folderSortDescend = PortletRequestUtils.getStringParameter(request, WebKeys.FOLDER_SORT_DESCEND, "");
			
			Tabs tabs = new Tabs(request);
			Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
			if (tabId != null) tabs.setCurrentTab(tabId.intValue());
			
			Map tab = tabs.getTab(tabs.getCurrentTab());
			tab.put(Tabs.SORTBY, new String(folderSortBy));
			tab.put(Tabs.SORTDESCEND, new String(folderSortDescend));
			//When the column is sorted, the page number need not be retained, the user can be taken to page number 1
			tab.put(Tabs.PAGE, new Integer(0));
		} else if (op.equals(WebKeys.OPERATION_SAVE_SEARCH_PAGE_INFO)) {
			String pageStartIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_START_INDEX, "");
		
			Tabs tabs = new Tabs(request);
			Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
			if (tabId != null) tabs.setCurrentTab(tabId.intValue());
			Map tab = tabs.getTab(tabs.getCurrentTab());
			tab.put(Tabs.PAGE, new Integer(pageStartIndex));
			
		} else if (op.equals(WebKeys.OPERATION_SAVE_SEARCH_GOTOPAGE_INFO)) {
			//Saves the folder page informaton when the user enters the page number in the go to page field
			String pageGoToIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_GOTOPAGE_INDEX, "");
			Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
			
			Tabs tabs = new Tabs(request);
			if (tabId != null) tabs.setCurrentTab(tabId.intValue());
			
			Map tab = tabs.getTab(tabs.getCurrentTab());
			Integer recordsPerPage = (Integer) tab.get(Tabs.RECORDS_IN_PAGE);
					
			int intGoToPageIndex = new Integer(pageGoToIndex).intValue();
			int intRecordsPerPage = recordsPerPage.intValue();
			int intPageStartIndex = (intGoToPageIndex - 1) * intRecordsPerPage;
			
			tab.put(Tabs.PAGE, new Integer(intPageStartIndex));
		} else if (op.equals(WebKeys.OPERATION_CHANGE_ENTRIES_ON_SEARCH_PAGE)) {
			//Changes the number or records to be displayed in a page
			//Getting the new entries per page
			String newEntriesPerPage = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_ENTRIES_PER_PAGE, "");
			//Saving the Sort Order information in the User Properties
			getProfileModule().setUserProperty(user.getId(), ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
		}		
		
		response.setRenderParameters(formData);
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
	
		Map model = new HashMap();
		Map formData = request.getParameterMap();
        User user = RequestContextHolder.getRequestContext().getUser();

		//Set up the tabs
		Tabs tabs = new Tabs(request);
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
		if (tabId != null) tabs.setCurrentTab(tabId.intValue());
		model.put(WebKeys.TABS, tabs.getTabs());

		Document searchQuery = null;

		Map tab = tabs.getTab(tabs.getCurrentTab());
		String tabType = null;
		if (tab != null) tabType = (String)tab.get(Tabs.TYPE);
		//See if the search form was submitted
		if (formData.containsKey("searchBtn") || formData.containsKey("searchBtn.x") || formData.containsKey("searchBtn.y")) {
			//Parse the search filter
			searchQuery = FilterHelper.getSearchQuery(request);
			Map options = new HashMap();
			//Get the search text to use it for the tab title 
			String searchText = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchText, "");
			if (!searchText.equals("")) {
				String tabTitle = searchText;
				options.put(Tabs.TITLE, tabTitle);
			}
			//Store the search query in the current tab
			boolean blnClearTab = true;
			tabs.setCurrentTab(tabs.findTab(searchQuery, options, blnClearTab));
		} else if (tabType != null && tabType.equals(Tabs.QUERY)) {
			//Get the search query from the tab
			searchQuery = (Document) tab.get(Tabs.QUERY_DOC);
		} else if (tabType != null && !tabType.equals(Tabs.QUERY)) {
			//The tab changed, go to the right controller
			return new ModelAndView("binder/tab_redirect", model);
		}
		
		List<Map>entries;
		List<Map>newEntries;
		List people = new ArrayList();
		List entryPeople = new ArrayList();
		List entryPlaces = new ArrayList();
		Map entryMap = new HashMap();
		Map peopleMap = new HashMap();
		
		UserProperties userProp = getProfileModule().getUserProperties(user.getId());

		Map options = new HashMap();		
		

		Map tabInfo = tabs.getTab(tabs.getCurrentTab());
		
		//Determine the Records Per Page
		//Getting the entries per page from the user properties
		String entriesPerPage = (String) userProp.getProperty(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE);
		//Getting the number of records per page entry in the tab
		Integer recordsInPage = (Integer) tabInfo.get(Tabs.RECORDS_IN_PAGE);
		Integer pageRecordIndex = (Integer) tabInfo.get(Tabs.PAGE);

		//If the entries per page is not present in the user properties, then it means the
		//number of records per page is obtained from the ssf properties file, so we do not have 
		//to worry about checking the old and new number or records per page.
		if (entriesPerPage == null || "".equals(entriesPerPage)) {
			//This means that the tab does not have the information about the number of records to display in a page
			//So we need to add this information into the tab
			if (recordsInPage == null) {
				String searchMaxHits = SPropsUtil.getString("search.records.listed");
				options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(searchMaxHits));
				tabInfo.put(Tabs.RECORDS_IN_PAGE, new Integer(searchMaxHits));
			}
			else {
				//Not putting the RECORDS_IN_PAGE as the tabInfo already has this information
				options.put(ObjectKeys.SEARCH_MAX_HITS, recordsInPage);
			}
		}
		else {
			options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(entriesPerPage));
			tabInfo.put(Tabs.RECORDS_IN_PAGE, new Integer(entriesPerPage));
			if (recordsInPage != null) {
				int intEntriesPerPage = (new Integer(entriesPerPage)).intValue();
				int intEntriesPerPageInTab = recordsInPage.intValue();
				
				if (intEntriesPerPage != intEntriesPerPageInTab) {
					//We need to check and see if the page number is set in the tabs. If so, reset it
					if (pageRecordIndex != null) {
						int intPageRecordIndex = pageRecordIndex.intValue();
						int intNewPageNumber = (intPageRecordIndex + 1)/(intEntriesPerPage);
						int intNewPageStartIndex = (intNewPageNumber) * intEntriesPerPage;
						tabInfo.put(Tabs.PAGE, new Integer(intNewPageStartIndex));
					}
				}
			}
			else {
				tabInfo.put(Tabs.PAGE, new Integer(0));
			}
		}
		
		//Determine the Starting Page Index - To be displayed in the screen
		Integer tabPageNumber = (Integer) tabInfo.get(Tabs.PAGE);
		if (tabPageNumber == null) tabPageNumber = new Integer(0);
		//options.put(ObjectKeys.SEARCH_OFFSET, tabPageNumber);
		
		//Internally we will always start the search from the first record
		options.put(ObjectKeys.SEARCH_OFFSET, new Integer(0));
		
		//actual number of records to be displayed
		Integer recordsToBeDisplayed = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
		
		//actual number of records to be fetched
		int intInternalNumberOfRecordsToBeFetched = tabPageNumber.intValue() + recordsToBeDisplayed.intValue() + 200;
		options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(intInternalNumberOfRecordsToBeFetched));
		
		//Determining the Sorting Order
		//When the search tab is loaded for the first time, no sorting order is mentioned
		//When the user chooses a sorting order for the specific search tab, then we will
		//store the sort order in the tab and maintain it when the user returns to the search
		//tab by clicking on the search tab link. It will also be maintained if the user pages
		//through the record.
		String searchSortBy = (String) tabInfo.get(Tabs.SORTBY);
		String searchSortDescend = (String) tabInfo.get(Tabs.SORTDESCEND);
		if (searchSortBy != null && !searchSortBy.equals("")) {
			options.put(ObjectKeys.SEARCH_SORT_BY, searchSortBy);
			if (("true").equalsIgnoreCase(searchSortDescend)) {
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
			}
			else if (("false").equalsIgnoreCase(searchSortDescend)) {
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
			}
		}
		
		Integer currentTabId  = (Integer) tabInfo.get(Tabs.TAB_ID);
		model.put(WebKeys.URL_TAB_ID, currentTabId);
		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, recordsToBeDisplayed);
		model.put(WebKeys.PAGE_MENU_CONTROL_TITLE, NLT.get("folder.Page", new Object[]{recordsToBeDisplayed}));
		
		if (searchQuery != null) {
			//Do the search and store the search results in the bean
			entryMap = getBinderModule().executeSearchQuery(searchQuery, options);
			peopleMap = getBinderModule().executePeopleSearchQuery(searchQuery);
			entries = (List) entryMap.get(WebKeys.FOLDER_ENTRIES);
			people = (List) peopleMap.get(WebKeys.PEOPLE_RESULTS);
			
			int intEntriesLength = entries.size();
			int intStartIndex = tabPageNumber.intValue();
			int intEndIndex = intStartIndex + recordsToBeDisplayed;   
			if (intEndIndex > intEntriesLength) {
				intEndIndex = intEntriesLength;				
			}
			newEntries = entries.subList(intStartIndex, intEndIndex);
			//entries = newEntries;
			
			entryPeople = sortPeopleInEntriesSearchResults(entries);
			entryPeople = ratePeople(entryPeople);
			
			entryPlaces = sortPlacesInEntriesSearchResults(entries);
			entryPlaces = ratePlaces(entryPlaces);
		} 
		else {
			entries = new ArrayList();
			newEntries = new ArrayList();
		}
		
		Integer entrySearchTotalCount = (Integer)entryMap.get(WebKeys.ENTRY_SEARCH_COUNT);
		Integer entrySearchReturnedCount = (Integer)entryMap.get(WebKeys.ENTRY_SEARCH_RECORDS_RETURNED);
		Integer peopleSearchTotalCount = (Integer)peopleMap.get(WebKeys.PEOPLE_RESULTCOUNT);
		
		model.put(WebKeys.FOLDER_ENTRIES, newEntries);
		model.put(WebKeys.ENTRY_SEARCH_COUNT, entrySearchTotalCount);
		model.put(WebKeys.ENTRY_SEARCH_RECORDS_RETURNED, entrySearchReturnedCount);
		
		int totalRecordsFound = entrySearchTotalCount;
		int totalRecordsReturned = entrySearchReturnedCount;
		//int searchOffset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
		int searchOffset = tabPageNumber.intValue();
		int searchPageIncrement = recordsToBeDisplayed;
		int goBackSoManyPages = 2;
		int goFrontSoManyPages = 3;
		
		HashMap pagingInfo = getPagingLinks(totalRecordsFound, searchOffset, searchPageIncrement, 
				goBackSoManyPages, goFrontSoManyPages);

		HashMap prevPage = (HashMap) pagingInfo.get(WebKeys.PAGE_PREVIOUS);
		ArrayList pageNumbers = (ArrayList) pagingInfo.get(WebKeys.PAGE_NUMBERS);
		HashMap nextPage = (HashMap) pagingInfo.get(WebKeys.PAGE_NEXT);
		String pageStartIndex = (String) pagingInfo.get(WebKeys.PAGE_START_INDEX);
		String pageEndIndex = (String) pagingInfo.get(WebKeys.PAGE_END_INDEX);
		
		model.put(WebKeys.PAGE_PREVIOUS, prevPage);
		model.put(WebKeys.PAGE_NUMBERS, pageNumbers);
		model.put(WebKeys.PAGE_NEXT, nextPage);
		model.put(WebKeys.PAGE_START_INDEX, pageStartIndex);
		model.put(WebKeys.PAGE_END_INDEX, pageEndIndex);
		
		double dblNoOfPages = Math.ceil((double)totalRecordsFound/searchPageIncrement);
		model.put(WebKeys.PAGE_COUNT, ""+dblNoOfPages);
		
		//since the results span multiple folders, we need to get the folder titles
		Set ids = new HashSet();
		for (Map r : entries) {
			String entityType = (String) r.get("_entityType");
			if (entityType != null && r.containsKey("_docId") && 
					(entityType.equals(EntityType.folder.toString()) || entityType.equals(EntityType.workspace.toString()))) {
				ids.add(Long.valueOf((String)r.get("_docId")));
			} else if (r.containsKey("_binderId")) {
				ids.add(Long.valueOf((String)r.get("_binderId")));
			}
		}
		model.put(WebKeys.BINDER_DATA, ResolveIds.getBinderTitlesAndIcons(ids));
							
		model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId()));
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		UserProperties userFolderProperties = null;
		model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
		model.put(WebKeys.FOLDER_ENTRYPEOPLE, entryPeople);
		model.put(WebKeys.FOLDER_ENTRYPLACES, entryPlaces);
		model.put(WebKeys.PEOPLE_RESULTS, people);
		model.put(WebKeys.PEOPLE_RESULTCOUNT, peopleSearchTotalCount);
		model.put(WebKeys.FOLDER_SORT_BY, searchSortBy);		
		model.put(WebKeys.FOLDER_SORT_DESCEND, searchSortDescend);
		
		//Get a default folder definition to satisfy the folder view jsps
		Definition def = getDefinitionModule().createDefaultDefinition(Definition.FOLDER_VIEW);
		DefinitionHelper.getDefinition(def, model, "//item[@name='forumView']");
		model.put(WebKeys.SHOW_SEARCH_RESULTS, true);
		buildSearchResultsToolbars(request, response, model);
		return new ModelAndView(BinderHelper.getViewListingJsp(this), model);
	}

	//This method returns a HashMap with Keys referring to the Previous Page Keys,
	//Paging Number related Page Keys and the Next Page Keys.
	public HashMap getPagingLinks(int intTotalRecordsFound, int intSearchOffset, 
			int intSearchPageIncrement, int intGoBackSoManyPages, int intGoFrontSoManyPages) {
		
		HashMap<String, Object> hmRet = new HashMap<String, Object>();
		ArrayList<HashMap> pagingInfo = new ArrayList<HashMap>(); 
		int currentDisplayValue = ( intSearchOffset + intSearchPageIncrement) / intSearchPageIncrement;		

		//Adding Prev Page Link
		int prevInternalValue = intSearchOffset - intSearchPageIncrement;
		HashMap<String, Object> hmRetPrev = new HashMap<String, Object>();
		hmRetPrev.put(WebKeys.PAGE_DISPLAY_VALUE, "<<");
		hmRetPrev.put(WebKeys.PAGE_INTERNAL_VALUE, "" + prevInternalValue);
		if (intSearchOffset == 0) {
			hmRetPrev.put(WebKeys.PAGE_NO_LINK, "" + new Boolean(true));
		}
		hmRet.put(WebKeys.PAGE_PREVIOUS, hmRetPrev);

		//Adding Links before Current Display
		if (intSearchOffset != 0) {
			//Code for generating the Numeric Paging Information previous to offset			
			int startPrevDisplayFrom = currentDisplayValue - intGoBackSoManyPages;
			
			int wentBackSoManyPages = intGoBackSoManyPages + 1;
			for (int i = startPrevDisplayFrom; i < currentDisplayValue; i++) {
				wentBackSoManyPages--;
				if (i < 1) continue;
				prevInternalValue = (intSearchOffset - (intSearchPageIncrement * wentBackSoManyPages));
				HashMap<String, Object> hmPrev = new HashMap<String, Object>();
				hmPrev.put(WebKeys.PAGE_DISPLAY_VALUE, "" + i);
				hmPrev.put(WebKeys.PAGE_INTERNAL_VALUE, "" + prevInternalValue);
				pagingInfo.add(hmPrev);
			}
		}
		
		//Adding Links after Current Display
		for (int i = 0; i < intGoFrontSoManyPages; i++) {
			int nextInternalValue = intSearchOffset + (intSearchPageIncrement * i);
			int nextDisplayValue = (nextInternalValue + intSearchPageIncrement) / intSearchPageIncrement;  
			if ( !(nextInternalValue >= intTotalRecordsFound) ) {
				HashMap<String, Object> hmNext = new HashMap<String, Object>();
				hmNext.put(WebKeys.PAGE_DISPLAY_VALUE, "" + nextDisplayValue);
				hmNext.put(WebKeys.PAGE_INTERNAL_VALUE, "" + nextInternalValue);
				if (nextDisplayValue == currentDisplayValue) hmNext.put(WebKeys.PAGE_IS_CURRENT, new Boolean(true));
				pagingInfo.add(hmNext);
			}
			else break;
		}
		hmRet.put(WebKeys.PAGE_NUMBERS, pagingInfo);
		
		//Adding Next Page Link
		int nextInternalValue = intSearchOffset + intSearchPageIncrement;
		HashMap<String, Object> hmRetNext = new HashMap<String, Object>();
		hmRetNext.put(WebKeys.PAGE_DISPLAY_VALUE, ">>");
		hmRetNext.put(WebKeys.PAGE_INTERNAL_VALUE, "" + nextInternalValue);
		
		if ( (nextInternalValue >= intTotalRecordsFound) ) {
			hmRetNext.put(WebKeys.PAGE_NO_LINK, "" + new Boolean(true));
		}
		hmRet.put(WebKeys.PAGE_NEXT, hmRetNext);
		hmRet.put(WebKeys.PAGE_START_INDEX, "" + (intSearchOffset + 1));
		
		if (nextInternalValue >= intTotalRecordsFound) hmRet.put(WebKeys.PAGE_END_INDEX, "" + intTotalRecordsFound);
		else hmRet.put(WebKeys.PAGE_END_INDEX, "" + nextInternalValue);
		
		return hmRet;
	}
	
	// This class is used by the following method as a way to sort
	// the values in a hashmap
	public class Person implements Comparable {
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
	
	// This method reads thru the results from a search, finds the principals, 
	// and places them into an array that is ordered by the number of times
	// they show up in the results list.
	protected List sortPeopleInEntriesSearchResults(List entries) {
		List results = new ArrayList();
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
	
	public List ratePeople(List entries) {
		//The same logic and naming has been followed for both people and placess
		return ratePlaces(entries);
	}
	
	// This class is used by the following method as a way to sort
	// the values in a hashmap
	public class Place implements Comparable {
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
	
	// This method reads thru the results from a search, finds the folder that 
	// each entry is in, and places them into an array that is ordered by the 
	// number of times they show up in the results list.
	protected List sortPlacesInEntriesSearchResults(List entries) {
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
			Binder binder = getBinderModule().getBinder(((Place)array[j]).getId());
			int count = ((Place)array[j]).getCount();
			Map place = new HashMap();
			place.put(WebKeys.BINDER, binder);
			place.put(WebKeys.SEARCH_RESULTS_COUNT, new Integer(count));
			placeList.add(place);
		}
		return placeList;
	}

	public List ratePlaces(List entries) {
		ArrayList ratedList = new ArrayList();
		int intMaxHitsPerFolder = 0;
		for (int i = 0; i < entries.size(); i++) {
			Map place = (Map) entries.get(i);
			Integer resultCount = (Integer) place.get(WebKeys.SEARCH_RESULTS_COUNT);
			if (i == 0) {
				place.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(100));
				place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "firstRating");
				intMaxHitsPerFolder = resultCount;
			}
			else {
				int intResultCount = resultCount.intValue();
				Double DblRatingForFolder = ((double)intResultCount/intMaxHitsPerFolder) * 100;
				int intRatingForFolder = DblRatingForFolder.intValue();
				place.put(WebKeys.SEARCH_RESULTS_RATING, new Integer(DblRatingForFolder.intValue()));
				if (intRatingForFolder > 80 && intRatingForFolder <= 100) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "firstRating");
				}
				else if (intRatingForFolder > 50 && intRatingForFolder <= 80) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "secondRating");
				}
				else if (intRatingForFolder > 20 && intRatingForFolder <= 50) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "thirdRating");
				}
				else if (intRatingForFolder > 10 && intRatingForFolder <= 20) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "fourthRating");
				}
				else if (intRatingForFolder >= 0 && intRatingForFolder <= 10) {
					place.put(WebKeys.SEARCH_RESULTS_RATING_CSS, "fifthRating");
				}
			}
			ratedList.add(place);
		}
		return ratedList;
	}
	
	protected void buildSearchResultsToolbars(RenderRequest request, 
			RenderResponse response, Map model) {
		//Build the toolbar arrays
		Toolbar folderToolbar = new Toolbar();
		Toolbar entryToolbar = new Toolbar();
		Toolbar footerToolbar = new Toolbar();
		PortletURL url;
		
		//	The "Display styles" menu
		entryToolbar.addToolbarMenu("2_display_styles", NLT.get("toolbar.display_styles"));
		
		//vertical
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_SEARCH_RESULTS_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
		entryToolbar.addToolbarMenuItem("2_display_styles", "styles", NLT.get("toolbar.menu.display_style_vertical"), url);
		//accessible
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_SEARCH_RESULTS_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		entryToolbar.addToolbarMenuItem("2_display_styles", "styles", NLT.get("toolbar.menu.display_style_accessible"), url);
		//iframe
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_SEARCH_RESULTS_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		entryToolbar.addToolbarMenuItem("2_display_styles", "styles", NLT.get("toolbar.menu.display_style_iframe"), url);
		//popup
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_SEARCH_RESULTS_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
		entryToolbar.addToolbarMenuItem("2_display_styles", "styles", NLT.get("toolbar.menu.display_style_popup"), url);

		model.put(WebKeys.FOLDER_TOOLBAR,  folderToolbar.getToolbar());
		model.put(WebKeys.ENTRY_TOOLBAR,  entryToolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
	}
}
