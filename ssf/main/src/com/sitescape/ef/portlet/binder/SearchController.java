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


/**
 * @author Peter Hurley
 *
 */
public class SearchController extends AbstractBinderController {
	public void handleActionRequestInternal(ActionRequest request, 
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

		List entryPeople = new ArrayList();
		Document searchQuery = null;

		Map tab = tabs.getTab(tabs.getCurrentTab());
		String tabType = (String)tab.get(Tabs.TYPE);
		//See if the search form was submitted
		if (formData.containsKey("searchBtn") || formData.containsKey("searchBtn.x") || formData.containsKey("searchBtn.y")) {
			//Parse the search filter
			searchQuery = FilterHelper.getSearchQuery(request);
			Map options = new HashMap();
			//Get the search text to use it for the tab title 
			String searchText = PortletRequestUtils.getStringParameter(request, FilterHelper.SearchText, "");
			if (!searchText.equals("")) {
				String tabTitle = NLT.get("tabs.search") + ": " + searchText;
				options.put(Tabs.TITLE, tabTitle);
			}
			//Store the search query in the current tab
			tabs.setCurrentTab(tabs.addTab(searchQuery, options));
		} else if (tabType != null && tabType.equals(Tabs.QUERY)) {
			//Get the search query from the tab
			searchQuery = (Document) tab.get(Tabs.QUERY_DOC);
		} else if (tabType != null && !tabType.equals(Tabs.QUERY)) {
			//The tab changed, go to the right controller
			return new ModelAndView("binder/tab_redirect", model);
		}
		
		List<Map>entries;
		List people = new ArrayList();
		if (searchQuery != null) {
			//Do the search and store the search results in the bean
			entries = getBinderModule().executeSearchQuery(searchQuery);
			people = getBinderModule().executePeopleSearchQuery(searchQuery);
			entryPeople = sortPeopleInEntriesSearchResults(entries, people);

		} else entries = new ArrayList();
		model.put(WebKeys.FOLDER_ENTRIES, entries);
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
		model.put(WebKeys.PEOPLE_RESULTS, people);   
		
		//Get a default folder definition to satisfy the folder view jsps
		Definition def = getDefinitionModule().createDefaultDefinition(Definition.FOLDER_VIEW);
		DefinitionHelper.getDefinition(def, model, "//item[@name='forumView']");
		model.put(WebKeys.SHOW_SEARCH_RESULTS, true);
		buildSearchResultsToolbars(request, response, model);
		return new ModelAndView(BinderHelper.getViewListingJsp(), model);
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
	
	// This method reads thru the results from an search, finds the principles, 
	// and places them into an array that is ordered by the number of times
	// they show up in the results list.
	protected List sortPeopleInEntriesSearchResults(List entries, List people) {
		List results = new ArrayList();
		HashMap userMap = new HashMap();
		ArrayList userList = new ArrayList();
		// first go thru the original search results and 
		// find all the unique principles.  Keep a count to see
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
			userList.add(((Person)array[j]).getUser());
		}
		return userList;
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
