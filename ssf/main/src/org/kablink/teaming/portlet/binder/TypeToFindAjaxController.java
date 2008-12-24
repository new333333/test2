package org.kablink.teaming.portlet.binder;

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
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.web.portlet.ModelAndView;

/**
 * Controller to handle type to find and lookup for search widgets
 * @author Janet
 *
 */
public class TypeToFindAjaxController extends SAbstractController {
	public static Pattern replacePtrn = Pattern.compile("([\\p{Punct}&&[^\\*]])");

	//caller will retry on OptimisiticLockExceptions
	public void handleActionRequestInternal(ActionRequest request, ActionResponse response) throws Exception {
		response.setRenderParameters(request.getParameterMap());
	}
	
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		if (!WebHelper.isUserLoggedIn(request)) {
			Map model = new HashMap();
			Map statusMap = new HashMap();
			
			//Signal that the user is not logged in. 
			//  The code on the calling page will output the proper translated message.
			statusMap.put(WebKeys.AJAX_STATUS_NOT_LOGGED_IN, new Boolean(true));
			model.put(WebKeys.AJAX_STATUS, statusMap);
			
			response.setContentType("text/xml");			
			return new ModelAndView("forum/ajax_return", model);
		}
		
		//The user is logged in
		if (op.equals(WebKeys.OPERATION_FIND_USER_SEARCH) ||
					op.equals(WebKeys.OPERATION_FIND_PLACES_SEARCH) || 
					op.equals(WebKeys.OPERATION_FIND_ENTRIES_SEARCH) || 
					op.equals(WebKeys.OPERATION_FIND_TAG_SEARCH)) {
				return ajaxFind(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_WORKFLOWS_SEARCH)) {
			return ajaxWorkflowsListSearch(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_WORKFLOW_STEPS_SEARCH)) {
			return ajaxGetWorkflowSteps(request, response);			
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_TYPES_SEARCH)) {
			return ajaxEntryTypesListSearch(request, response);
		} else if (op.equals(WebKeys.OPERATION_FIND_ENTRY_FIELDS_SEARCH)) {
			return ajaxGetEntryFields(request, response);
		}
		response.setContentType("text/xml");
		return new ModelAndView("forum/ajax_return");
	}
	
	private ModelAndView ajaxFind(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "");
		String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "10");
		String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "0");
		String foldersOnly = PortletRequestUtils.getStringParameter(request, "foldersOnly", "false");
		String searchSubFolders = PortletRequestUtils.getStringParameter(request, "searchSubFolders", "false");
		String showFolderTitles = PortletRequestUtils.getStringParameter(request, "showFolderTitles", "false");
		String binderId = PortletRequestUtils.getStringParameter(request, "binderId", "");
		String showUserTitleOnly = PortletRequestUtils.getStringParameter(request, "showUserTitleOnly", "false");
		boolean addCurrentUser = PortletRequestUtils.getBooleanParameter(request, "addCurrentUser", false);
		String findObjectName = PortletRequestUtils.getStringParameter(request, "findObjectName", "");
		
		
		Integer startingCount = Integer.parseInt(pageNumber) * Integer.parseInt(maxEntries);

		User user = RequestContextHolder.getRequestContext().getUser();
		Map options = new HashMap();
		String view = "forum/json/find_search_result";
		String viewAccessible = "forum/find_search_result_accessible";	
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
		options.put(ObjectKeys.SEARCH_OFFSET, startingCount);
		options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
		
		if(op.equals(WebKeys.OPERATION_FIND_TAG_SEARCH)) {
		
			boolean result = ajaxCheckCurrentTag(searchText);
		
			if(result) {
				List thelist = null;
				
				model.put(WebKeys.TAG_LENGTH_WARNING, NLT.get("tags.maxLengthWarning"));
				model.put(WebKeys.TAGS, thelist);
				
				if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(user.getDisplayStyle())) {
					view = viewAccessible;
				} else {
					response.setContentType("text/json-comment-filtered");
				}
				return new ModelAndView(view, model);	
			}
		}
		
		//Build the search query
		SearchFilter searchTermFilter = new SearchFilter();
		
		String newStr = searchText;
		Matcher matcher = replacePtrn.matcher(newStr);
		while (matcher.find()) {
			newStr = matcher.replaceFirst(" ");
			matcher = replacePtrn.matcher(newStr);
		}
		newStr = newStr.replaceAll(" \\*", "\\*");
		
	    searchText = newStr;
	     
		if (findType.equals(WebKeys.FIND_TYPE_PLACES)) {
			searchTermFilter.addPlacesFilter(searchText, Boolean.valueOf(foldersOnly));
		} else if (findType.equals(WebKeys.FIND_TYPE_TEAMS)) {
			searchTermFilter.addWorkspaceFilter(searchText);
		} else if (findType.equals(WebKeys.FIND_TYPE_ENTRIES)) {
			//Add the title term
			if (searchText.length()>0)
			searchTermFilter.addTitleFilter(searchText);

			List searchTerms = new ArrayList();
			searchTerms.add(EntityIdentifier.EntityType.folderEntry.name());
			searchTermFilter.addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes, SearchFilterKeys.FilterEntityType, searchTerms);
			
			searchTermFilter.addAndFilter(SearchFilterKeys.FilterTypeTopEntry);
			
			//Add terms to search this folder
			if (!binderId.equals("")) {
				if (searchSubFolders.equals("false")) {
					searchTermFilter.addAndFolderId(binderId);
				} else {
					searchTermFilter.addAncestryId(binderId);
				}
			}
			
		} else if (findType.equals(WebKeys.FIND_TYPE_TAGS)) {
			// this has been replaced by a getTags method in the search engine.
			// searchTermFilter.addTagsFilter(FilterHelper.FilterTypeTags, searchText);
		} else {
			//Add the login name term
			if (searchText.length()>0) {
				searchTermFilter.addTitleFilter(searchText);
				searchTermFilter.addLoginNameFilter(searchText);
			}
		}
		
		try {
		
			//Do a search to find the first few items that match the search text
			options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
			if (findType.equals(WebKeys.FIND_TYPE_PLACES)) {
				Map retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
				List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
				model.put(WebKeys.ENTRIES, entries);
				model.put(WebKeys.SEARCH_TOTAL_HITS, retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			} else if (findType.equals(WebKeys.FIND_TYPE_TEAMS)) {
				Map retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
				List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
				model.put(WebKeys.ENTRIES, entries);
				model.put(WebKeys.SEARCH_TOTAL_HITS, retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			} else if (findType.equals(WebKeys.FIND_TYPE_ENTRIES)) {
				Map retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
				List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
				List placesWithCounters = BinderHelper.sortPlacesInEntriesSearchResults(getBinderModule(), entries);
				Map foldersMap = BinderHelper.prepareFolderList(placesWithCounters, false);
				BinderHelper.extendEntriesInfo(entries, foldersMap);
				model.put(WebKeys.ENTRIES, entries);
				model.put(WebKeys.SEARCH_TOTAL_HITS, retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				model.put(WebKeys.FIND_SHOW_FOLDER_TITLES, showFolderTitles);
			} else if (findType.equals(WebKeys.FIND_TYPE_TAGS) ||
					findType.equals(WebKeys.FIND_TYPE_PERSONAL_TAGS) ||
					findType.equals(WebKeys.FIND_TYPE_COMMUNITY_TAGS)) {
				
				String wordRoot = searchText;
				int i = wordRoot.indexOf("*");
				if (i > 0) wordRoot = wordRoot.substring(0, i);
				
				List tags = getBinderModule().getSearchTags(wordRoot, findType);
				
				List tagsPage = new ArrayList();			
				if (tags.size() > startingCount.intValue()) {
					int endTag = startingCount.intValue() + Integer.valueOf(maxEntries);
					if (tags.size() < endTag) endTag = tags.size();
					tagsPage = tags.subList(startingCount.intValue(), endTag);
				}
				model.put(WebKeys.ENTRIES, tagsPage);
				model.put(WebKeys.SEARCH_TOTAL_HITS, Integer.valueOf(tags.size()));
			} else if (findType.equals(WebKeys.FIND_TYPE_GROUP)) {
				Map entries = getProfileModule().getGroups(options);
				model.put(WebKeys.ENTRIES, entries.get(ObjectKeys.SEARCH_ENTRIES));
				model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			} else if (findType.equals(WebKeys.FIND_TYPE_USER)) {
				Map entries = getProfileModule().getUsers(options);
				
				int page = 0;
				try {
					page = Integer.parseInt(pageNumber);
				} catch (NumberFormatException e) {}
				
				List resultList = (List)entries.get(ObjectKeys.SEARCH_ENTRIES);
				if (addCurrentUser && page == 0 && (searchText.equals("") || searchText.equals("*"))) {
					// add relative option "current user" and "me"
					Map currentUserPlaceholder = new HashMap();
					currentUserPlaceholder.put("title", NLT.get("searchForm.currentUserTitle"));
					currentUserPlaceholder.put("_docId", SearchFilterKeys.CurrentUserId);
					resultList.add(0, currentUserPlaceholder);
				}
				model.put(WebKeys.ENTRIES, resultList);
				model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				model.put(WebKeys.FIND_SHOW_USER_TITLE_ONLY, showUserTitleOnly);
			} else if (findType.equals(WebKeys.FIND_TYPE_APPLICATION_GROUP)) {
				Map entries = getProfileModule().getApplicationGroups(options);
				model.put(WebKeys.ENTRIES, entries.get(ObjectKeys.SEARCH_ENTRIES));
				model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
				model.put(WebKeys.FIND_SHOW_USER_TITLE_ONLY, showUserTitleOnly);
			} else if (findType.equals(WebKeys.FIND_TYPE_APPLICATION)) {
				Map entries = getProfileModule().getApplications(options);
				model.put(WebKeys.ENTRIES, entries.get(ObjectKeys.SEARCH_ENTRIES));
				model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			}
			
		} catch(AccessControlException e) {
			model.put(WebKeys.ENTRIES, Collections.EMPTY_LIST);
			model.put(WebKeys.SEARCH_TOTAL_HITS, 0);
		}
		model.put(WebKeys.FIND_TYPE, findType);
		model.put(WebKeys.PAGE_SIZE, maxEntries);
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		model.put("findObjectName", findObjectName);
		
		
		if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(user.getDisplayStyle())) {
			view = viewAccessible;
		} else {
			response.setContentType("text/json-comment-filtered");
		}
		return new ModelAndView(view, model);
	}
	private boolean ajaxCheckCurrentTag(String newTag){
		if (Validator.isNull(newTag)) return false;
		 
		newTag = newTag.replaceAll("[\\p{Punct}]", " ").trim().replaceAll("\\s+"," ");
		String[] newTags = newTag.split(" ");
		if (newTags.length == 0) return false;
		
	   	String tagName = newTags[newTags.length - 1].trim();
	   	if (tagName.length() > ObjectKeys.MAX_TAG_LENGTH) {
	   		return true;
	   	}
	   		
	   	return false;
	}

	private ModelAndView ajaxEntryTypesListSearch(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();;
		
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		int maxEntries = PortletRequestUtils.getIntParameter(request, "maxEntries", 10);
		int pageNumber = PortletRequestUtils.getIntParameter(request, "pageNumber", 0);
		
		while (searchText.endsWith("*")) {
			searchText = searchText.substring(0, searchText.length() - 1); 
		}
		
		Set<Definition> entries = new HashSet();
		if (WebHelper.isUserLoggedIn(request)) {
			Collection<Long> ids = TreeHelper.getSelectedIds(request.getParameterMap());
			for (Long id:ids) {
				try {
					entries.addAll(getDefinitionModule().getDefinitions(id, Boolean.TRUE, Definition.FOLDER_ENTRY));
				} catch (Exception ex) {}
			}
			if (entries.isEmpty()) entries.addAll(getDefinitionModule().getDefinitions(null, Boolean.TRUE, Definition.FOLDER_ENTRY));
		}
		
		Map<String, Definition> entriesOrdered = DefinitionHelper.orderDefinitions(entries, false);
		
		List entriesResult = new ArrayList();
		for (Map.Entry<String, Definition> mapEntry : entriesOrdered.entrySet()) {
			String title = mapEntry.getKey();
			if (title != null && title.toLowerCase().startsWith(searchText.toLowerCase())) {
				Map entryType = new HashMap();
				entryType.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
				entryType.put(Constants.DOCID_FIELD, mapEntry.getValue().getId());
				entriesResult.add(entryType);
			}
		}
		
		int totalHits = entriesResult.size();
		
		int indexLast = (maxEntries * pageNumber) + maxEntries;
		if (entriesResult.size() < maxEntries * pageNumber) {
			entriesResult.clear();
		} else {
			if (entriesResult.size() < ((maxEntries * pageNumber) + maxEntries)) {
				indexLast = entriesResult.size();
			}
			entriesResult = entriesResult.subList(maxEntries * pageNumber, indexLast);
		}
		
		model.put(WebKeys.ENTRIES, entriesResult);
		model.put(WebKeys.SEARCH_TOTAL_HITS, totalHits);
		
		model.put(WebKeys.PAGE_SIZE, maxEntries);
		model.put(WebKeys.PAGE_NUMBER, pageNumber);	
		
		response.setContentType("text/json-comment-filtered");
		
		return new ModelAndView("forum/json/find_search_result", model);
		
//		model.put(WebKeys.ENTRY, DefinitionHelper.orderDefinitions(entries, false));
//		response.setContentType("text/json");
//		return new ModelAndView("forum/json/find_entry_types_widget", model);

	}


	private ModelAndView ajaxWorkflowsListSearch(RenderRequest request, RenderResponse response) {
		Map model = new HashMap();;
		
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		int maxEntries = PortletRequestUtils.getIntParameter(request, "maxEntries", 10);
		int pageNumber = PortletRequestUtils.getIntParameter(request, "pageNumber", 0);
		
		while (searchText.endsWith("*")) {
			searchText = searchText.substring(0, searchText.length() - 1); 
		}
		
		Set<Definition> workflows = new HashSet();
		if (WebHelper.isUserLoggedIn(request)) {
			Collection<Long> ids = TreeHelper.getSelectedIds(request.getParameterMap());
			for (Long id:ids) {
				try {
					workflows.addAll(getDefinitionModule().getDefinitions(id, Boolean.TRUE, Definition.WORKFLOW));
				} catch (Exception ex) {}
			}
			if (workflows.isEmpty()) workflows.addAll(getDefinitionModule().getDefinitions(null, Boolean.TRUE, Definition.WORKFLOW));
		}
		
		Map<String, Definition> workflowsOrdered = DefinitionHelper.orderDefinitions(workflows, false);
		
		List workflowsResult = new ArrayList();
		for (Map.Entry<String, Definition> mapEntry : workflowsOrdered.entrySet()) {
			String title = mapEntry.getKey();
			if (title != null && title.toLowerCase().startsWith(searchText.toLowerCase())) {
				Map workflow = new HashMap();
				workflow.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
				workflow.put(Constants.DOCID_FIELD, mapEntry.getValue().getId());
				workflowsResult.add(workflow);
			}
		}
		
		int totalHits = workflowsResult.size();
		
		int indexLast = (maxEntries * pageNumber) + maxEntries;
		if (workflowsResult.size() < maxEntries * pageNumber) {
			workflowsResult.clear();
		} else {
			if (workflowsResult.size() < ((maxEntries * pageNumber) + maxEntries)) {
				indexLast = workflowsResult.size();
			}
			workflowsResult = workflowsResult.subList(maxEntries * pageNumber, indexLast);
		}
		
		model.put(WebKeys.ENTRIES, workflowsResult);
		model.put(WebKeys.SEARCH_TOTAL_HITS, totalHits);
		
		model.put(WebKeys.PAGE_SIZE, maxEntries);
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		
		response.setContentType("text/json-comment-filtered");
		
		return new ModelAndView("forum/json/find_search_result", model);
	}	
	
	private ModelAndView ajaxGetWorkflowSteps(RenderRequest request, RenderResponse response) {
		String workflowId = PortletRequestUtils.getStringParameter(request, "workflowId", "");
		Map model = new HashMap();
		
		Map stateData = new HashMap();
		if (WebHelper.isUserLoggedIn(request)) {
			stateData = getDefinitionModule().getWorkflowDefinitionStates(workflowId);
		}
		model.put(WebKeys.WORKFLOW_DEFINITION_STATE_DATA, stateData);
		
		response.setContentType("text/json");
		return new ModelAndView("forum/json/find_workflow_steps", model);
	}
	
	private ModelAndView ajaxGetEntryFields(RenderRequest request, RenderResponse response) {
		String entryTypeId = PortletRequestUtils.getStringParameter(request,WebKeys.FILTER_ENTRY_DEF_ID, "");
		String entryField = PortletRequestUtils.getStringParameter(request, SearchFilterKeys.FilterElementNameField, "");
		
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		int maxEntries = PortletRequestUtils.getIntParameter(request, "maxEntries", 10);
		int pageNumber = PortletRequestUtils.getIntParameter(request, "pageNumber", 0);
		
		while (searchText.endsWith("*")) {
			searchText = searchText.substring(0, searchText.length() - 1); 
		}
		
		Map model = new HashMap();
		response.setContentType("text/json-comment-filtered");
		
		Map<String, Map> fieldsData = new HashMap();
		if (WebHelper.isUserLoggedIn(request)) {
			fieldsData = getDefinitionModule().getEntryDefinitionElements(entryTypeId);
		}
	
		if (entryField.equals("")) {		
			SortedMap<String, Map> fieldsDataSortedByCaption = new TreeMap();
			Iterator<Map.Entry<String, Map>> it = fieldsData.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Map> mapEntry = it.next();
				fieldsDataSortedByCaption.put((String)mapEntry.getValue().get("caption"), mapEntry.getValue());
			}
			
			List fieldsResult = new ArrayList();
			for (Map.Entry<String, Map> mapEntry : fieldsDataSortedByCaption.entrySet()) {
				String title = (String)mapEntry.getKey();
				String type = (String)mapEntry.getValue().get("type");
				if (title != null && title.toLowerCase().startsWith(searchText.toLowerCase()) && 
						!"attachFiles".equals(type) && !"file".equals(type) && 
						!"graphic".equals(type) && !"profileEntryPicture".equals(type) && 
						!"description".equals(type) && !"htmlEditorTextarea".equals(type) &&
						!"eventScheduler".equals(type)) {
					Map field = new HashMap();
					field.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
					field.put(Constants.DOCID_FIELD, (String)mapEntry.getValue().get("name"));
					field.put(WebKeys.ENTITY_TYPE, type);
					fieldsResult.add(field);
				}
			}
			
			int totalHits = fieldsResult.size();
			
			int indexLast = (maxEntries * pageNumber) + maxEntries;
			if (fieldsResult.size() < maxEntries * pageNumber) {
				fieldsResult.clear();
			} else {
				if (fieldsResult.size() < ((maxEntries * pageNumber) + maxEntries)) {
					indexLast = fieldsResult.size();
				}
				fieldsResult = fieldsResult.subList(maxEntries * pageNumber, indexLast);
			}
			
			model.put(WebKeys.ENTRIES, fieldsResult);
			model.put(WebKeys.SEARCH_TOTAL_HITS, totalHits);
			
			model.put(WebKeys.PAGE_SIZE, maxEntries);
			model.put(WebKeys.PAGE_NUMBER, pageNumber);
			
			return new ModelAndView("forum/json/find_search_result", model);
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
}
