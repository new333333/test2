/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class ProfilesBinderHelper {
	public static ModelAndView setupProfilesBinderBeans(AllModulesInjected bs, Long binderId, RenderRequest request, 
			RenderResponse response, boolean showTrash) throws Exception {
 		Map<String,Object> model = new HashMap<String,Object>();
 		return setupProfilesBinderBeans(bs, binderId, request, response, model, showTrash);
	}
	public static ModelAndView setupProfilesBinderBeans(AllModulesInjected bs, Long binderId, RenderRequest request, 
			RenderResponse response, Map model, boolean showTrash) throws Exception {
	
		model.put(WebKeys.URL_SHOW_TRASH, new Boolean(showTrash));
	   	User user = RequestContextHolder.getRequestContext().getUser();
		Binder binderObj = bs.getBinderModule().getBinder(binderId);
		
		//Set up the standard beans
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId);
		UserProperties userProperties = (UserProperties)model.get(WebKeys.USER_PROPERTIES_OBJ);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);

		model.put(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
		reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		
		Map options;
		Map users = null;
		ProfileBinder binder;
		Tabs.TabEntry tab;
		if (showTrash) {
			tab = TrashHelper.buildTrashTabs(request, binderObj, model);
			
			TrashHelper.buildTrashViewToolbar(model);
			options = TrashHelper.buildTrashBeans(bs, request, response, binderId, model);
			Map trashEntries = TrashHelper.getTrashEntities(bs, model, binderObj, options);
			model.putAll(ListFolderHelper.getSearchAndPagingModels(trashEntries, options, showTrash));
			if (trashEntries != null) {
				List trashEntriesList = (List) trashEntries.get(ObjectKeys.SEARCH_ENTRIES);
				model.put(WebKeys.FOLDER_ENTRIES, trashEntriesList);
			}
			binder = ((ProfileBinder) binderObj);
		}
		else {
			tab = BinderHelper.initTabs(request, binderObj);
			model.put(WebKeys.TABS, tab.getTabs());
			
			options = new HashMap();
			options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(ObjectKeys.LISTING_MAX_PAGE_SIZE));
			options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);

			//determine page starts/ counts
			initPageCounts(request, userProperties.getProperties(), tab, options);
			options.put(ObjectKeys.SEARCH_SEARCH_FILTER, BinderHelper.getSearchFilter(bs, binderObj, userFolderProperties));
			users = bs.getProfileModule().getUsers(options);
			binder = (ProfileBinder)users.get(ObjectKeys.BINDER);
			model.put(WebKeys.ENTRIES, users.get(ObjectKeys.SEARCH_ENTRIES));
			model.putAll(getSearchAndPagingModels(users, options));		
			model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
			model.put(WebKeys.PAGE_MENU_CONTROL_TITLE, NLT.get("folder.Page", new Object[]{options.get(ObjectKeys.SEARCH_MAX_HITS)}));
		}
		
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		if (!model.containsKey(WebKeys.SEEN_MAP)) { 
			model.put(WebKeys.SEEN_MAP,bs.getProfileModule().getUserSeenMap(user.getId()));
		}
		model.put(WebKeys.URL_TAB_ID, String.valueOf(tab.getTabId()));
			
		DashboardHelper.getDashboardMap(binder, userProperties.getProperties(), model);
		DefinitionHelper.getDefinitions(binder, model);
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		buildViewFolderToolbars(bs, request, response, binder, model);
		model.put(WebKeys.ENTRY_TOOLBAR, buildViewEntryToolbar(bs, request, response, binder).getToolbar());

		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(bs, binder, model);
		
		//Build sidebar beans
		BinderHelper.buildWorkspaceTreeBean(bs, binder, model, null);

		Map tagResults = TagUtil.uniqueTags(bs.getBinderModule().getTags(binder));
		model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
		model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
		
		return new ModelAndView(BinderHelper.getViewListingJsp(bs, BinderHelper.getViewType(bs, binder)), model);
	}

	protected static void initPageCounts(RenderRequest request, Map userProperties, Tabs.TabEntry tab, Map options) {
		Map tabOptions = tab.getData();
		//Determine the Records Per Page
		//Getting the entries per page from the user properties
		//String entriesPerPage = (String) userFolderProperties.getProperty(ObjectKeys.PAGE_ENTRIES_PER_PAGE);
		//Moving the entries per information from the user/folder level to the user level.
		String entriesPerPage = MiscUtil.entriesPerPage(userProperties);
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
		tab.setData(tabOptions); //use synchronzied method
	}		

	protected static Map getSearchAndPagingModels(Map userEntries, Map options) {
		return BinderHelper.getSearchAndPagingModels(userEntries, options, false);
	}
	
	//This method returns a HashMap with Keys referring to the Previous Page Keys,
	//Paging Number related Page Keys and the Next Page Keys.
	public static HashMap getPagingLinks(int intTotalRecordsFound, int intSearchOffset, 
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
	
	protected static void buildViewFolderToolbars(AllModulesInjected bs, RenderRequest request, RenderResponse response, 
			ProfileBinder binder, Map model) {
        User user = RequestContextHolder.getRequestContext().getUser();
        String userDisplayStyle = user.getCurrentDisplayStyle();
        String binderId = binder.getId().toString();
        
		//Build the toolbar arrays
		Toolbar folderToolbar = new Toolbar();
		Toolbar folderActionsToolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		Toolbar footerToolbar = new Toolbar();
		Toolbar trashToolbar = new Toolbar();
		Toolbar gwtMiscToolbar = new Toolbar();
		Toolbar gwtUIToolbar = new Toolbar();
		
		AdaptedPortletURL adapterUrl;
		//The "Administration" menu
		Map qualifiers = new HashMap();
		qualifiers.put("popup", new Boolean(true));
		boolean adminMenuCreated=false;
		folderToolbar.addToolbarMenu("1_administration", NLT.get("toolbar.manageThisWorkspace"));
		if (bs.getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
			adminMenuCreated=true;
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.configuration"), adapterUrl.toString(), qualifiers);
			//Modify
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.modify_workspace"), adapterUrl.toString(), qualifiers);
		}
		//if no menu items were added, remove the empty menu
		if (!adminMenuCreated) folderToolbar.deleteToolbarMenu("1_administration");

		//Access control
		if (bs.getAdminModule().testAccess(binder, AdminOperation.manageFunctionMembership)) {
			adminMenuCreated = true;
			qualifiers = new HashMap();
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.accessControlMenu");
			qualifiers.put("popup", new Boolean(true));
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			adapterUrl.setParameter(WebKeys.URL_WORKAREA_ID, binder.getWorkAreaId().toString());
			adapterUrl.setParameter(WebKeys.URL_WORKAREA_TYPE, binder.getWorkAreaType());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.accessControl"), adapterUrl.toString(), qualifiers);
		}

		//Site administration
		if (bs.getAdminModule().testAccess(AdminOperation.manageFunction)) {
			adminMenuCreated=true;
			PortletURL url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_SITE_ADMINISTRATION);
			url.setParameter(WebKeys.URL_BINDER_ID, binderId);
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.siteAdministration"), url);
		}

		//The "Who has access" menu
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			qualifiers = new HashMap();
			qualifiers.put("title", NLT.get("toolbar.menu.title.whoHasAccessFolder"));
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("popupWidth", "600");
			qualifiers.put("popupHeight", "700");
			adminMenuCreated = true;
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			adapterUrl.setParameter(WebKeys.URL_WORKAREA_ID, binder.getWorkAreaId().toString());
			adapterUrl.setParameter(WebKeys.URL_WORKAREA_TYPE, binder.getWorkAreaType());
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ACCESS);
			folderToolbar.addToolbarMenu("2_whoHasAccess", 
					NLT.get("toolbar.whoHasAccess"), adapterUrl.toString(), qualifiers);
		}
		
		//Folder action menu
		boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) || !accessible_simple_ui) {
			//Folder action menu
			//Build the standard toolbar
			BinderHelper.buildFolderActionsToolbar(bs, request, response, folderActionsToolbar, binderId);
		}
		
		//	The "Manage dashboard" menu
		BinderHelper.buildDashboardToolbar(request, response, bs, binder, dashboardToolbar, model);
		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());

		//The "Footer" menu
		String permaLink = PermaLinkUtil.getPermalink(request, binder);
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showPermalink(this);return false;");
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.folderPermalink"), 
				permaLink, qualifiers);
		
		model.put(WebKeys.PERMALINK, permaLink);
		model.put(WebKeys.MOBILE_URL, SsfsUtil.getMobileUrl(request));		

		//  Build the simple URL beans
		BinderHelper.buildSimpleUrlBeans(bs,  request, binder, model);

		//Trash
		TrashHelper.buildTrashToolbar(user, binder, model, qualifiers, trashToolbar);

		// GWT UI.  Note that these need to be last in the toolbar
		// building sequence because they access things in the
		// model to construct toolbars specific to the GWT UI.
		GwtUIHelper.buildGwtMiscToolbar(bs, request, binder, model, gwtMiscToolbar);

		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR,  folderToolbar.getToolbar());
		model.put(WebKeys.FOLDER_ACTIONS_TOOLBAR,  folderActionsToolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
		model.put(WebKeys.TRASH_TOOLBAR,  trashToolbar.getToolbar());
		model.put(WebKeys.GWT_MISC_TOOLBAR,  gwtMiscToolbar.getToolbar());
		model.put(WebKeys.GWT_UI_TOOLBAR,  gwtUIToolbar.getToolbar());
	}
	
	protected static Toolbar buildViewEntryToolbar (AllModulesInjected bs, RenderRequest request, RenderResponse response, ProfileBinder binder) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		if (bs.getProfileModule().testAccess(binder, ProfileOperation.addEntry)) {
			List defaultEntryDefinitions = binder.getEntryDefinitions();
			if (!defaultEntryDefinitions.isEmpty()) {
				// Only one option
				Definition def = (Definition) defaultEntryDefinitions.get(0);
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				String[] nltArgs = new String[] {NLT.getDef(def.getTitle())};
				String title = NLT.get("toolbar.new_with_arg", nltArgs);
				Map qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				qualifiers.put("highlight", new Boolean(true));
				toolbar.addToolbarMenu("1_add", title, adapterUrl.toString(), qualifiers);
			}
		}
		return toolbar;
	}
}
