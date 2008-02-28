package com.sitescape.team.portlet.binder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;
/**
 * Controller to handle type to find and lookup for search widgets
 * @author Janet
 *
 */
public class TypeToFindAjaxController extends SAbstractController {
	static Pattern replacePtrn = Pattern.compile("([\\p{Punct}&&[^\\*]])");

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
				return ajaxFindUserSearch(request, response);
		} else if (op.equals(WebKeys.OPERATION_USER_LIST_SEARCH)) {
			return ajaxUserListSearch(request, response);
		} 
			response.setContentType("text/xml");
			return new ModelAndView("forum/ajax_return");
	}
	
	private ModelAndView ajaxUserListSearch(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();;
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String searchType = PortletRequestUtils.getStringParameter(request, "searchType", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "");
		String listDivId = PortletRequestUtils.getStringParameter(request, "listDivId", "");
		String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "");
		String[] idsToSkip = PortletRequestUtils.getStringParameter(request, "idsToSkip", "").split(" ");
		
		Map userIdsToSkip = new HashMap();
		for (int i = 0; i < idsToSkip.length; i++) {
			if (!idsToSkip[i].equals("")) userIdsToSkip.put(idsToSkip[i], Long.valueOf(idsToSkip[i]));
		}
		
		String nameType = ProfileIndexUtils.LASTNAME_FIELD;
		if (searchType.equals("firstName")) nameType = ProfileIndexUtils.FIRSTNAME_FIELD;
		if (searchType.equals("loginName")) nameType = ProfileIndexUtils.LOGINNAME_FIELD;
		if (searchType.equals("groupName")) nameType = ProfileIndexUtils.GROUPNAME_FIELD;
		if (searchType.equals("title")) nameType = EntityIndexUtils.TITLE_FIELD;
		    	     	
		//Build the search query
		Document searchFilter = DocumentHelper.createDocument();
		Element sfRoot = searchFilter.addElement(SearchFilterKeys.FilterRootName);
		Element filterTerms = sfRoot.addElement(SearchFilterKeys.FilterTerms);
		Element filterTerm = filterTerms.addElement(SearchFilterKeys.FilterTerm);
		filterTerm.addAttribute(SearchFilterKeys.FilterType, SearchFilterKeys.FilterTypeEntryDefinition);
		filterTerm.addAttribute(SearchFilterKeys.FilterElementName, nameType);
		if (searchText.length() > 0) {
			Element filterTermValueEle = filterTerm.addElement(SearchFilterKeys.FilterElementValue);
			filterTermValueEle.setText(searchText);
		}
	   	

		//Do a search to find the first few users who match the search text
		User u = RequestContextHolder.getRequestContext().getUser();
		Map users = new HashMap();
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchFilter);
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_GROUP)) {
			users = getProfileModule().getGroups(u.getParentBinder().getId(), options);
		} else {
			users = getProfileModule().getUsers(u.getParentBinder().getId(), options);
		}
		model.put(WebKeys.USERS, users.get(ObjectKeys.SEARCH_ENTRIES));
		model.put(WebKeys.USER_IDS_TO_SKIP, userIdsToSkip);
		model.put(WebKeys.USER_SEARCH_USER_GROUP_TYPE, findType);
		model.put(WebKeys.DIV_ID, listDivId);
		response.setContentType("text/xml");
		return new ModelAndView("forum/user_list_search", model);
	}
	

	private ModelAndView ajaxFindUserSearch(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String searchText = PortletRequestUtils.getStringParameter(request, "searchText", "");
		String findType = PortletRequestUtils.getStringParameter(request, "findType", "");
		String listDivId = PortletRequestUtils.getStringParameter(request, "listDivId", "");
		String maxEntries = PortletRequestUtils.getStringParameter(request, "maxEntries", "10");
		String pageNumber = PortletRequestUtils.getStringParameter(request, "pageNumber", "0");
		String foldersOnly = PortletRequestUtils.getStringParameter(request, "foldersOnly", "false");
		String namespace = PortletRequestUtils.getStringParameter(request, "namespace", "");
		String binderId = PortletRequestUtils.getStringParameter(request, "binderId", "");
		String searchSubFolders = PortletRequestUtils.getStringParameter(request, "searchSubFolders", "");
		Integer startingCount = Integer.parseInt(pageNumber) * Integer.parseInt(maxEntries);

		User user = RequestContextHolder.getRequestContext().getUser();
		Map options = new HashMap();
		String view, viewAccessible;
		options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.parseInt(maxEntries));
		options.put(ObjectKeys.SEARCH_OFFSET, startingCount);
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
		
		model.put(WebKeys.DIV_ID, listDivId);
		
		if(op.equals(WebKeys.OPERATION_FIND_TAG_SEARCH)) {
		
			boolean result = ajaxCheckCurrentTag(searchText);
		
			if(result) {
				List thelist = null;
				view = "forum/find_tag_search";
				viewAccessible = "forum/find_tag_search_accessible";
				
				model.put(WebKeys.TAG_LENGTH_WARNING, NLT.get("tags.maxLengthWarning"));
				model.put(WebKeys.TAGS, thelist);
				
				if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(user.getDisplayStyle()) &&
						!user.getInternalId().equals(ObjectKeys.GUEST_USER_INTERNALID)) {
					view = viewAccessible;
				} else {
					response.setContentType("text/xml");
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
	     
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PLACES)) {
			searchTermFilter.addPlacesFilter(searchText, Boolean.valueOf(foldersOnly));

		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_ENTRIES)) {
			//Add the title term
			if (searchText.length()>0)
			searchTermFilter.addTitleFilter(searchText);

			List searchTerms = new ArrayList();
			searchTerms.add(EntityIdentifier.EntityType.folderEntry.name());
			searchTermFilter.addAndNestedTerms(SearchFilterKeys.FilterTypeEntityTypes, SearchFilterKeys.FilterEntityType, searchTerms);
			
			searchTermFilter.addAndFilter(SearchFilterKeys.FilterTypeTopEntry);
			
			//Add terms to search this folder
			if (!binderId.equals("")) {
				
				searchTermFilter.addAndFolderId(binderId);
				
				//TODO Need to implement "searchSubFolders"
			}
			
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_TAGS)) {
			// this has been replaced by a getTags method in the search engine.
			// searchTermFilter.addTagsFilter(FilterHelper.FilterTypeTags, searchText);
		} else {
			//Add the login name term
			if (searchText.length()>0) {
				searchTermFilter.addTitleFilter(searchText);
				searchTermFilter.addLoginNameFilter(searchText);
			}
		}
	   	
		//Do a search to find the first few items that match the search text
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
		if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PLACES)) {
			Map retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
			List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
			model.put(WebKeys.ENTRIES, entries);
			model.put(WebKeys.SEARCH_TOTAL_HITS, retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_places_search";
			viewAccessible = "forum/find_places_search_accessible";
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_ENTRIES)) {
			Map retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options);
			List entries = (List)retMap.get(ObjectKeys.SEARCH_ENTRIES);
			model.put(WebKeys.ENTRIES, entries);
			model.put(WebKeys.SEARCH_TOTAL_HITS, retMap.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_entries_search";
			viewAccessible = "forum/find_entries_search_accessible";
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_TAGS) ||
				findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PERSONAL_TAGS) ||
				findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_COMMUNITY_TAGS)) {
			
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
			model.put(WebKeys.TAGS, tagsPage);
			model.put(WebKeys.SEARCH_TOTAL_HITS, Integer.valueOf(tags.size()));
			view = "forum/find_tag_search";
			viewAccessible = "forum/find_tag_search_accessible";
		} else if (findType.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_GROUP)) {
			Map entries = getProfileModule().getGroups(user.getParentBinder().getId(), options);
			model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
			model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_user_search";
			viewAccessible = "forum/find_user_search_accessible";
		} else {
			Map entries = getProfileModule().getUsers(user.getParentBinder().getId(), options);
			model.put(WebKeys.USERS, entries.get(ObjectKeys.SEARCH_ENTRIES));
			model.put(WebKeys.SEARCH_TOTAL_HITS, entries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
			view = "forum/find_user_search";
			viewAccessible = "forum/find_user_search_accessible";
		}
		model.put(WebKeys.USER_SEARCH_USER_GROUP_TYPE, findType);
		model.put(WebKeys.PAGE_SIZE, maxEntries);
		model.put(WebKeys.PAGE_NUMBER, pageNumber);
		
		model.put(WebKeys.NAMESPACE, namespace);
		if (ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(user.getDisplayStyle()) &&
				!user.getInternalId().equals(ObjectKeys.GUEST_USER_INTERNALID)) {
			view = viewAccessible;
		} else {
			response.setContentType("text/xml");
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

}
