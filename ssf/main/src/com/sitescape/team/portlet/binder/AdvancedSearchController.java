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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
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
		
        // User user = RequestContextHolder.getRequestContext().getUser();
        
        if (op.equals(WebKeys.SEARCH_RESULTS)) {
        	model = prepareSearchResultData(request);
        	return new ModelAndView("search/search_result", model);
        } else {
        	model = prepareSearchFormData(request);
        	return new ModelAndView("search/search_form", model);
        }
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

		
		// Document searchQuery = FilterHelper.getSearchTextQuery(searchText);
		Document searchQuery = getSearchQuery(request);
		Map results =  getBinderModule().executeSearchQuery(searchQuery);
		
		// Map options = prepareSearchTextOptions(searchText);
		Map options = prepareSearchOptions(request);

		//Store the search query in a new tab
		storeQueryInTabs(tabs, searchQuery, options, newTab);		
		model.put(WebKeys.TABS, tabs.getTabs());
		
		// for test only
		model.put("query", searchQuery.asXML());
		model.put("options", options);
		model.put("results", results);
		
		return model;
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
		
		// Get the search text to use it for the tab title
		String searchText = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchText, "");
		if (!searchText.equals("")) {
			options.put(Tabs.TITLE, searchText);
			options.put(Tabs.TAB_SEARCH_TEXT, searchText);
		}
		
		// Get authors
		String authors = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchAuthors, "");
		if (!authors.equals("")) {
			options.put("authors", authors);
		}
		
		// Get tags
		String tags = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchTags, "");
		if (!tags.equals("")) {
			options.put("tags", tags);
		}
		
		return options;
	}

	
	private Document getSearchQuery(RenderRequest request) {

		Boolean joiner = PortletRequestUtils.getBooleanParameter(request, FilterHelper.SearchJoiner, false);
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
		return searchFilter.getFilter();
	}

}
