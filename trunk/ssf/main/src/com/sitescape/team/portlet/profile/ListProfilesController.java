package com.sitescape.team.portlet.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.util.Validator;

public class ListProfilesController extends   SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
        User user = RequestContextHolder.getRequestContext().getUser();
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map updates = new HashMap();
			updates.put("displayStyle", PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
		} else if (op.equals(WebKeys.OPERATION_SELECT_FILTER)) {
				getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_USER_FILTER, 
						PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION_SELECT_FILTER,""));
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_PAGE_INFO)) {
			//Saves the folder page informaton when the user clicks on the page link
			
			String pageStartIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_START_INDEX, "");
			Tabs tabs = new Tabs(request);
			
			Binder binder = getBinderModule().getBinder(binderId);
			Map options = new HashMap();
			options.put(Tabs.PAGE, new Integer(pageStartIndex));
			
			tabs.setTab(binder, options);
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_GOTOPAGE_INFO)) {
			//Saves the folder page informaton when the user enters the page number in the go to page field
			String pageGoToIndex = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_GOTOPAGE_INDEX, "");
			
			Tabs tabs = new Tabs(request);
			Map tab = tabs.getTab(tabs.getCurrentTab());
			Integer recordsPerPage = (Integer) tab.get(Tabs.RECORDS_IN_PAGE);
					
			int intGoToPageIndex = new Integer(pageGoToIndex).intValue();
			int intRecordsPerPage = recordsPerPage.intValue();
			int intPageStartIndex = (intGoToPageIndex - 1) * intRecordsPerPage;
			
			Binder binder = getBinderModule().getBinder(binderId);
			Map options = new HashMap();
			options.put(Tabs.PAGE, new Integer(intPageStartIndex));
			
			tabs.setTab(binder, options);
		} else if (op.equals(WebKeys.OPERATION_CHANGE_ENTRIES_ON_PAGE)) {
			//Changes the number or records to be displayed in a page
			//Getting the new entries per page
			String newEntriesPerPage = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_ENTRIES_PER_PAGE, "");
			getProfileModule().setUserProperty(user.getId(), ObjectKeys.PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
		}
		response.setRenderParameters(request.getParameterMap());
		
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
 		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		HashMap model = new HashMap();

		if (op.equals(WebKeys.OPERATION_RELOAD_LISTING)) {
			//An action is asking us to build the url to reload the parent page
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			model.put(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());
			return new ModelAndView(BinderHelper.getViewListingJsp(this, getViewType(binderId.toString())), model);
		} else if (op.equals(WebKeys.OPERATION_VIEW_ENTRY)) {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			if (!entryId.equals("")) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				request.setAttribute("ssLoadEntryUrl", adapterUrl.toString());			
				request.setAttribute("ssLoadEntryId", entryId);			
			}
		}

	   	User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
		reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		
		Map users = null;
		Map options = new HashMap();
		
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		
		String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
		options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(ObjectKeys.LISTING_MAX_PAGE_SIZE));
		options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.TITLE1_FIELD);
		options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);

		Binder binderObj = getBinderModule().getBinder(binderId);
		//initializing tabs
		Tabs tabs = initTabs(request, binderObj);
		Map tabOptions = tabs.getTab(tabs.getCurrentTab());
		model.put(WebKeys.TABS, tabs.getTabs());
		
		//determine page starts/ counts
		initPageCounts(request, userProperties, tabOptions, options);
		
		if (!Validator.isNull(searchFilterName)) {
			Map searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
			options.put(ObjectKeys.SEARCH_SEARCH_FILTER, (Document)searchFilters.get(searchFilterName));
			users = getProfileModule().getUsers(binderId, options);
		} else {
			users = getProfileModule().getUsers(binderId, options);
		}
		ProfileBinder binder = (ProfileBinder)users.get(ObjectKeys.BINDER);
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		
		model.put(WebKeys.ENTRIES, users.get(ObjectKeys.SEARCH_ENTRIES));
		model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId()));
		
		model.putAll(getSearchAndPagingModels(users, options));		

		Integer currentTabId  = (Integer) tabOptions.get(Tabs.TAB_ID);
		model.put(WebKeys.URL_TAB_ID, currentTabId);
		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
		model.put(WebKeys.PAGE_MENU_CONTROL_TITLE, NLT.get("folder.Page", new Object[]{options.get(ObjectKeys.SEARCH_MAX_HITS)}));
		
		DashboardHelper.getDashboardMap(binder, userProperties, model);
		DefinitionHelper.getDefinitions(binder, model);
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		model.put(WebKeys.FOLDER_TOOLBAR, buildViewToolbar(request, response, binder).getToolbar());

		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, binder, model);
		
		return new ModelAndView(BinderHelper.getViewListingJsp(this, getViewType(binderId.toString())), model);
	}

	protected Tabs initTabs(RenderRequest request, Binder binder) throws Exception {
		//Set up the tabs
		Tabs tabs = new Tabs(request);
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		
		//What do the newTab values mean?
		//newTab == 1 means if the Tab already exists use it, if not create another one
		//newTab == 2 means create a new Tab always
		//newTab == 3 If the folder is opened up in another tab use it. If not use the current Tab irrespective of what type of tab it is.
		if (newTab.equals("1")) {
			tabs.setCurrentTab(tabs.findTab(binder, true));
		} else if (newTab.equals("2")) {
			tabs.setCurrentTab(tabs.addTab(binder));
		} else if (newTab.equals("3")) {
			tabs.setCurrentTab(tabs.findTab(binder, new HashMap(), true, tabs.getCurrentTab()));
		} else if (tabId != null) {
			//Do not set the page number to zero
			tabs.setCurrentTab(tabs.setTab(tabId.intValue(), binder));
		} else {
			//Don't overwrite a search tab
			if (tabs.getTabType(tabs.getCurrentTab()).equals(Tabs.QUERY)) {
				tabs.setCurrentTab(tabs.findTab(binder, true));				
			} else {
				tabs.setCurrentTab(tabs.findTab(binder, new HashMap(), true, tabs.getCurrentTab()));
			}
		}	
		return tabs;
	}	
	
	protected void initPageCounts(RenderRequest request, Map userProperties, Map tabOptions, Map options) {
		//Determine the Records Per Page
		//Getting the entries per page from the user properties
		//String entriesPerPage = (String) userFolderProperties.getProperty(ObjectKeys.PAGE_ENTRIES_PER_PAGE);
		//Moving the entries per information from the user/folder level to the user level.
		String entriesPerPage = (String) userProperties.get(ObjectKeys.PAGE_ENTRIES_PER_PAGE);
		//Getting the number of records per page entry in the tab
		Integer recordsInPage = (Integer) tabOptions.get(Tabs.RECORDS_IN_PAGE);
		Integer pageRecordIndex = (Integer) tabOptions.get(Tabs.PAGE);

		//If the entries per page is not present in the user properties, then it means the
		//number of records per page is obtained from the ssf properties file, so we do not have 
		//to worry about checking the old and new number or records per page.
		if (Validator.isNull(entriesPerPage)) {
			//This means that the tab does not have the information about the number of records to display in a page
			//So we need to add this information into the tab
			if (recordsInPage == null) {
				Integer searchMaxHits = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
				options.put(ObjectKeys.SEARCH_MAX_HITS, searchMaxHits);
				tabOptions.put(Tabs.RECORDS_IN_PAGE, searchMaxHits);
			} else {
				options.put(ObjectKeys.SEARCH_MAX_HITS, recordsInPage);
			}
		} else {
			Integer perPage = Integer.valueOf(entriesPerPage);
			options.put(ObjectKeys.SEARCH_MAX_HITS, perPage);
			tabOptions.put(Tabs.RECORDS_IN_PAGE, perPage);
			if (recordsInPage != null) {
				int intEntriesPerPage = perPage.intValue();
				int intEntriesPerPageInTab = recordsInPage.intValue();
				
				if (intEntriesPerPage != intEntriesPerPageInTab) {
					//We need to check and see if the page number is set in the tabs. If so, reset it
					if (pageRecordIndex != null) {
						int intPageRecordIndex = pageRecordIndex.intValue();
						int intNewPageNumber = (intPageRecordIndex + 1)/(intEntriesPerPage);
						int intNewPageStartIndex = (intNewPageNumber) * intEntriesPerPage;
						tabOptions.put(Tabs.PAGE, Integer.valueOf(intNewPageStartIndex));
					}
				}
			} else {
				tabOptions.put(Tabs.PAGE, new Integer(0));
			}
		}
		
		//Determine the Folder Start Index
		Integer tabPageNumber = (Integer) tabOptions.get(Tabs.PAGE);
		if (tabPageNumber == null) tabPageNumber = Integer.valueOf(0);
		options.put(ObjectKeys.SEARCH_OFFSET, tabPageNumber);
	}		

	protected Map getSearchAndPagingModels(Map userEntries, Map options) {
		Map model = new HashMap();
		
		String sortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
		Boolean sortDescend = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
		
		model.put(WebKeys.FOLDER_SORT_BY, sortBy);		
		model.put(WebKeys.FOLDER_SORT_DESCEND, sortDescend.toString());
		
		int totalRecordsFound = (Integer) userEntries.get(ObjectKeys.TOTAL_SEARCH_COUNT);
//		int totalRecordsReturned = (Integer) folderEntries.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
		//Start Point of the Record
		int searchOffset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
		int searchPageIncrement = (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS);
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
		model.put(WebKeys.PAGE_TOTAL_RECORDS, ""+totalRecordsFound);
		
		double dblNoOfPages = Math.ceil((double)totalRecordsFound/searchPageIncrement);
		
		model.put(WebKeys.PAGE_COUNT, ""+dblNoOfPages);
		model.put(WebKeys.SEARCH_TOTAL_HITS, userEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		
		return model;
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
	
	protected Toolbar buildViewToolbar(RenderRequest request, RenderResponse response, ProfileBinder binder) {
        User user = RequestContextHolder.getRequestContext().getUser();
        String userDisplayStyle = user.getDisplayStyle();
        if (userDisplayStyle == null) userDisplayStyle = "";
		PortletURL url;
		String binderId = binder.getId().toString();
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		//	The "Add" menu (Turned off because adding users must be done in the portal)
/*		List defaultEntryDefinitions = binder.getEntryDefinitions();
		if (!defaultEntryDefinitions.isEmpty()) {
			try {
				getProfileModule().checkAddEntryAllowed(binder);
				int count = 1;
				toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
				Map qualifiers = new HashMap();
				String onClickPhrase = "if (self.ss_addEntry) {return(self.ss_addEntry(this))} else {return true;}";
				qualifiers.put(ObjectKeys.TOOLBAR_QUALIFIER_ONCLICK, onClickPhrase);
				for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
					Definition def = (Definition) defaultEntryDefinitions.get(i);
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId);
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					String title = NLT.getDef(def.getTitle());
					if (toolbar.checkToolbarMenuItem("1_add", "entries", title)) {
						title = title + " (" + String.valueOf(count++) + ")";
					}
					toolbar.addToolbarMenuItem("1_add", "entries", title, adapterUrl.toString(), qualifiers);
				}
			} catch (AccessControlException ac) {};
		}
*/
		
		//The "Administration" menu
		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.administration"));
		//Access control
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.accessControl"), url);
		//Configuration
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url);
		//Definition builder
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.URL_BINDER_ID, binderId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder"), url);
		
		//Modify
		if (getBinderModule().testAccess(binder, "modifyBinder")) {
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.modify_workspace"), url, qualifiers);
		}
		//	The "Display styles" menu
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			toolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.folder_actions"));
			//vertical
			Map qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true); 
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
			toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_vertical"), 
					url, qualifiers);
			//iframe
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true); 
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
			toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_iframe"), 
					url, qualifiers);
			//popup
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true); 
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
			toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_popup"), 
					url, qualifiers);
		}
		return toolbar;
	}

	public String getViewType(String folderId) {

		User user = RequestContextHolder.getRequestContext().getUser();
		Binder binder = getBinderModule().getBinder(Long.valueOf(folderId));
		
		String viewType = "";
		
		//Check the access rights of the user
		if (getBinderModule().testAccess(binder, "setProperty")) {
			UserProperties userProperties = getProfileModule().getUserProperties(user.getId(), Long.valueOf(folderId)); 
			String displayDefId = (String) userProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
			Definition displayDef = binder.getDefaultViewDef();
			if (displayDefId != null && !displayDefId.equals("")) {
				displayDef = DefinitionHelper.getDefinition(displayDefId);
			}
			Document defDoc = null;
			if (displayDef != null) defDoc = displayDef.getDefinition();
			
			if (defDoc != null) {
				Element rootElement = defDoc.getRootElement();
				Element elementView = (Element) rootElement.selectSingleNode("//item[@name='forumView' or @name='profileView' or @name='workspaceView' or @name='userWorkspaceView']");
				if (elementView != null) {
					Element viewElement = (Element)elementView.selectSingleNode("./properties/property[@name='type']");
					if (viewElement != null) viewType = viewElement.attributeValue("value", "");
				}
			}
		}
		return viewType;
	}	
	
}
