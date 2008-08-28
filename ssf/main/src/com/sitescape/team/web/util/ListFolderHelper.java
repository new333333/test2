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
package com.sitescape.team.web.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestBindingException;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.AbstractIntervalView;
import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.calendar.OneDayView;
import com.sitescape.team.calendar.OneMonthView;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.AuditTrail;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.admin.AdminModule.AdminOperation;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.module.license.LicenseChecker;
import com.sitescape.team.module.rss.util.UrlUtil;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.portletadapter.support.PortletAdapterUtil;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.CalendarHelper;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;
public class ListFolderHelper {

	public static final String[] monthNames = { 
		"calendar.january",
		"calendar.february",
		"calendar.march",
		"calendar.april",
		"calendar.may",
		"calendar.june",
		"calendar.july",
		"calendar.august",
		"calendar.september",
		"calendar.october",
		"calendar.november",
		"calendar.december"
	};

	public static final String[] monthNamesShort = { 
		"calendar.abbreviation.january",
		"calendar.abbreviation.february",
		"calendar.abbreviation.march",
		"calendar.abbreviation.april",
		"calendar.abbreviation.may",
		"calendar.abbreviation.june",
		"calendar.abbreviation.july",
		"calendar.abbreviation.august",
		"calendar.abbreviation.september",
		"calendar.abbreviation.october",
		"calendar.abbreviation.november",
		"calendar.abbreviation.december"
	};

	static public ModelAndView BuildFolderBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long binderId) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		String displayType = BinderHelper.getDisplayType(request);
		Map formData = request.getParameterMap();
		BinderHelper.setBinderPermaLink(bs, request, response);

		String namespace = response.getNamespace();
        if (PortletAdapterUtil.isRunByAdapter(request)) {
        	namespace = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NAMESPACE, "");
        }
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		portletSession.setAttribute(WebKeys.LAST_BINDER_VIEWED + namespace, binderId, PortletSession.APPLICATION_SCOPE);
		portletSession.setAttribute(WebKeys.LAST_BINDER_ENTITY_TYPE + namespace, EntityType.folder.name(), PortletSession.APPLICATION_SCOPE);
        
		//Check special options in the URL
		String[] debug = (String[])formData.get(WebKeys.URL_DEBUG);
		if (debug != null && (debug[0].equals(WebKeys.DEBUG_ON) || debug[0].equals(WebKeys.DEBUG_OFF))) {
			//The user is requesting debug mode to be turned on or off
			if (debug[0].equals(WebKeys.DEBUG_ON)) {
				bs.getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(true));
			} else if (debug[0].equals(WebKeys.DEBUG_OFF)) {
				bs.getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(false));
			}
		}

		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		if (op.equals(WebKeys.OPERATION_RELOAD_LISTING)) {
			//An action is asking us to build the url
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			request.setAttribute(WebKeys.RELOAD_URL_FORCED, reloadUrl.toString());			
			return new ModelAndView(BinderHelper.getViewListingJsp(bs, BinderHelper.getViewType(bs, binderId)));
		}
		
		Map<String,Object> model = new HashMap<String,Object>();
		String view = BinderHelper.getViewListingJsp(bs, null);;
		
		//Set up the standard beans
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId);
		UserProperties userProperties = (UserProperties)model.get(WebKeys.USER_PROPERTIES_OBJ);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);

		//See if the entry to be shown is also included
		String entryIdToBeShown = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		if (entryIdToBeShown.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER)) entryIdToBeShown = "";
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		if (!entryTitle.equals("") && !entryTitle.equals(WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER)) {
			//This must be a request for a title link
			Set entries = bs.getFolderModule().getFolderEntryByNormalizedTitle(binderId, entryTitle);
			if (entries.size() == 1) {
				FolderEntry entry = (FolderEntry)entries.iterator().next();
				entryIdToBeShown = entry.getId().toString();
			} else {
				entryIdToBeShown = "";
			}
		}
		model.put(WebKeys.ENTRY_ID_TO_BE_SHOWN, entryIdToBeShown);

		//See if showing the workarea portlet or the actual folder in the adapter
		if (PortletAdapterUtil.isRunByAdapter(request) || 
				!BinderHelper.WORKAREA_PORTLET.equals(displayType)) {
			Binder binder = null;
			try {
				binder = bs.getBinderModule().getBinder(binderId);
			} catch(NoBinderByTheIdException e) {
			} catch(AccessControlException e) {
				if (WebHelper.isUserLoggedIn(request) && 
						!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
					//Access is not allowed
					return new ModelAndView(WebKeys.VIEW_ACCESS_DENIED, model);
				} else {
					//Please log in 
					String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
					model.put(WebKeys.URL, refererUrl);
					return new ModelAndView(WebKeys.VIEW_LOGIN_PLEASE, model);
				}
			}
			if (op.equals(WebKeys.OPERATION_VIEW_ENTRY)) {
				String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
				if (!entryId.equals("")) {
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
					adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
					request.setAttribute("ssLoadEntryUrl", adapterUrl.toString());			
					request.setAttribute("ssLoadEntryId", entryId);			
				}
			} else {
		     	if (binder != null) bs.getReportModule().addAuditTrail(AuditType.view, binder);
	
			}
	
			request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
	
				
			//Set up more standard beans
			//These have been documented, so don't delete any
			model.put(WebKeys.BINDER, binder);
			model.put(WebKeys.FOLDER, binder);
			model.put(WebKeys.DEFINITION_ENTRY, binder);
			model.put(WebKeys.ENTRY, binder);
	
			//Build a reload url
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			if (binder != null && binder.getParentBinder() != null) reloadUrl.setParameter(WebKeys.URL_BINDER_PARENT_ID, 
					binder.getParentBinder().getId().toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
			reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
			model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		
			model.put(WebKeys.SEEN_MAP, bs.getProfileModule().getUserSeenMap(user.getId()));
			if (binder != null) {
				DashboardHelper.getDashboardMap(binder, userProperties.getProperties(), model);
				//See if the user has selected a specific view to use
				DefinitionHelper.getDefinitions(binder, model, 
						(String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));
			}
	
			Tabs.TabEntry tab= BinderHelper.initTabs(request, binder);
			model.put(WebKeys.TABS, tab.getTabs());		
			//check tabs based on operation
			
			Map options = getSearchFilter(bs, request, userFolderProperties);
			
			//determine page starts/ counts
			initPageCounts(bs, request, userProperties.getProperties(), tab, options);
	
			Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			Element configElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
			String viewType = DefinitionUtils.getViewType(configDocument);
			if (viewType == null) viewType = "";

			/** Vertical mode has been removed
			//If the Folder View is anything other than Table and if the Folder Action happens to be  
			//vertical (view at the bottom), then we need to display the entry in the iframe view
			if (!viewType.equals(Definition.VIEW_STYLE_TABLE)) {
				String displayStyle = user.getDisplayStyle();
				if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
					model.put(WebKeys.FOLDER_ACTION_VERTICAL_OVERRIDE, "yes");
				}
			}
			*/
			
			if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
				//In Blog style we only want to show this entry
				if (!entryIdToBeShown.equals("")) {
					//options.put(ObjectKeys.FOLDER_ENTRY_TO_BE_SHOWN, entryIdToBeShown);
					model.put(WebKeys.FOLDER_VIEW_TYPE, viewType);
				}
			}
	
			//Checking the Sort Order that has been set. If not using the Default Sort Order
			initSortOrder(bs, request, userFolderProperties, tab, options, viewType);
	
			setupUrlOptions(bs, request, tab, options, model);
	
			if (binder== null) {
				view = "binder/deleted_binder";
			} else if(configElement == null) {
				buildFolderToolbars(bs, request, response, (Folder)binder, binderId.toString(), model, viewType);
				view = WebKeys.VIEW_NO_DEFINITION;
			} else if (op.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				model.put(WebKeys.SHOW_TEAM_MEMBERS, true);
				view = getTeamMembers(bs, formData, request, response, (Folder)binder, options, model, viewType);
			} else {
				view = getShowFolder(bs, formData, request, response, (Folder)binder, options, model, viewType);
			}
			
			model.put(WebKeys.URL_TAB_ID, String.valueOf(tab.getTabId()));
			model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
			model.put(WebKeys.PAGE_MENU_CONTROL_TITLE, NLT.get("folder.Page", new Object[]{options.get(ObjectKeys.SEARCH_MAX_HITS)}));
	
			if(binder != null) {
				Map tagResults = TagUtil.uniqueTags(bs.getBinderModule().getTags(binder));
				model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
				model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
			}
	
			try {
				//won't work on adapter
				response.setProperty(RenderResponse.EXPIRATION_CACHE,"0");
			} catch (UnsupportedOperationException us) {}

			return new ModelAndView(view, model);
			
		} else {
			return new ModelAndView(view, model);
		}
			
	}
	
	public static Map getSearchFilter(AllModulesInjected bs, RenderRequest request, UserProperties userFolderProperties) {
		Map result = new HashMap();
		result.put(ObjectKeys.SEARCH_SEARCH_FILTER, BinderHelper.getSearchFilter(bs, userFolderProperties));
		String searchTitle = PortletRequestUtils.getStringParameter(request, WebKeys.SEARCH_TITLE, "");
		if (!searchTitle.equals("")) {
			result.put(ObjectKeys.SEARCH_TITLE, searchTitle);
		}
		
		return result;
	}
	protected static void initSortOrder(AllModulesInjected bs, RenderRequest request, 
			UserProperties userFolderProperties, Tabs.TabEntry tab, Map options, String viewType) {
		//Start - Determine the Sort Order
		//since one one tab/folder, no use in saving info in tabs
		//Trying to get Sort Information from the User Folder Properties
		String	searchSortBy = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY);
		String	searchSortDescend = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_DESCEND);
		
		//Setting the Sort properties if it is available in the Tab or User Folder Properties Level. 
		//If not, go with the Default Sort Properties 
		if (Validator.isNotNull(searchSortBy)) {
			options.put(ObjectKeys.SEARCH_SORT_BY, searchSortBy);
			if (("true").equalsIgnoreCase(searchSortDescend)) {
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
			} else {
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
			}
		}
		if (!options.containsKey(ObjectKeys.SEARCH_SORT_BY)) { 
			options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORTNUMBER_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
		} else if (!options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) {
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
		}
		//End - Determine the Sort Order
		
	}
	protected static void initPageCounts(AllModulesInjected bs, RenderRequest request, 
			Map userProperties, Tabs.TabEntry tab, Map options) {
		Map tabOptions = tab.getData();
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
		if (tabPageNumber == null || tabPageNumber < 0) tabPageNumber = Integer.valueOf(0);
		options.put(ObjectKeys.SEARCH_OFFSET, tabPageNumber);
		tab.setData(tabOptions); //use synchronzied method
		
	}
	
	protected static void setupUrlOptions(AllModulesInjected bs, RenderRequest request, 
			Tabs.TabEntry tab, Map options, Map model) {
		Map tabOptions = tab.getData();
		//See if the url contains an ending date
		String day = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_DAY, "");
		String month = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_MONTH, "");
		String year = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_YEAR, "");
		String strDate=null;
		if (!day.equals("") || !month.equals("") || !year.equals("")) {
			strDate = DateHelper.getDateStringFromDMY(day, month, year);
		} else{
			strDate = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_END, null);
			if (Validator.isNull(strDate)) 	strDate = (String) tabOptions.get(Tabs.END_DATE);
		}
		if (Validator.isNotNull(strDate)) {
			options.put(ObjectKeys.SEARCH_END_DATE, strDate);
			model.put(WebKeys.FOLDER_END_DATE, DateHelper.getDateFromDMY(day, month, year));
			model.put(WebKeys.URL_DATE_END, strDate);
			tabOptions.put(Tabs.END_DATE, strDate);
			tabOptions.remove(Tabs.YEAR_MONTH);
			tabOptions.remove(Tabs.TAG_COMMUNITY);
			tabOptions.remove(Tabs.TAG_PERSONAL);	
		} else {
			Calendar cal = Calendar.getInstance(RequestContextHolder.getRequestContext().getUser().getTimeZone());
			model.put(WebKeys.FOLDER_END_DATE, cal.getTime());
		}
		
		//See if this is a request for a specific year/month
		String yearMonth = PortletRequestUtils.getStringParameter(request, WebKeys.URL_YEAR_MONTH, null);
		if (Validator.isNull(yearMonth)) yearMonth = (String) tabOptions.get(Tabs.YEAR_MONTH);
		if (Validator.isNotNull(yearMonth)) {
			options.put(ObjectKeys.SEARCH_YEAR_MONTH, yearMonth);
			model.put(WebKeys.URL_YEAR_MONTH, yearMonth);
			String strYear = yearMonth.substring(0, 4);
			String strMonth = yearMonth.substring(4, 6);
			int intMonth = Integer.parseInt(strMonth);
			String strMonthName = NLT.get(monthNames[intMonth-1]);				
			model.put(WebKeys.SELECTED_YEAR_MONTH, strMonthName + " " +strYear);
			tabOptions.put(Tabs.YEAR_MONTH, yearMonth);
			tabOptions.remove(Tabs.END_DATE);
			tabOptions.remove(Tabs.TAG_COMMUNITY);
			tabOptions.remove(Tabs.TAG_PERSONAL);				
		} 
		
		String cTag = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TAG_COMMUNITY, null);
		if (Validator.isNull(cTag)) cTag = (String) tabOptions.get(Tabs.TAG_COMMUNITY);
		if 	(Validator.isNotNull(cTag)) {
			options.put(ObjectKeys.SEARCH_COMMUNITY_TAG, cTag);
			model.put(WebKeys.URL_TAG_COMMUNITY, cTag);
			tabOptions.put(Tabs.TAG_COMMUNITY, cTag);
			tabOptions.remove(Tabs.END_DATE);
			tabOptions.remove(Tabs.YEAR_MONTH);
			tabOptions.remove(Tabs.TAG_PERSONAL);	
		}

		String pTag = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TAG_PERSONAL, "");
		if (Validator.isNull(pTag)) pTag = (String) tabOptions.get(Tabs.TAG_PERSONAL);
		if (Validator.isNotNull(pTag)) {
			options.put(ObjectKeys.SEARCH_PERSONAL_TAG, pTag);
			model.put(WebKeys.URL_TAG_PERSONAL, pTag);
			tabOptions.put(Tabs.TAG_PERSONAL, pTag);	
			tabOptions.remove(Tabs.END_DATE);
			tabOptions.remove(Tabs.YEAR_MONTH);
			tabOptions.remove(Tabs.TAG_COMMUNITY);
		}
		tab.setData(tabOptions);
	}
	
	protected void setupViewBinder(ActionResponse response, Long binderId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
	}

	private static Map findCalendarEvents(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Binder binder, Map model) throws PortletRequestBindingException {
		Map folderEntries = new HashMap();
		Long binderId = binder.getId();
		
		int year = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_YEAR, -1);
		int month = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_MONTH, -1);
		int dayOfMonth = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_DAY_OF_MONTH, -1);
		
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		
		Date currentDate = EventsViewHelper.getCalendarCurrentDate(portletSession);
		currentDate = EventsViewHelper.getDate(year, month, dayOfMonth, currentDate);
		model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
		EventsViewHelper.setCalendarCurrentDate(portletSession, currentDate);
		
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
		
		String gridType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_GRID_TYPE, "");
		Integer gridSize = PortletRequestUtils.getIntParameter(request, WebKeys.CALENDAR_GRID_SIZE, -1);
		Map grids = EventsViewHelper.setCalendarGrid(portletSession, userProperties, binderId.toString(), gridType, gridSize);
		model.put(WebKeys.CALENDAR_GRID_TYPE, ((EventsViewHelper.Grid)grids.get(binderId.toString())).type);
		model.put(WebKeys.CALENDAR_GRID_SIZE, ((EventsViewHelper.Grid)grids.get(binderId.toString())).size);
		
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
		TimeZone timeZone = user.getTimeZone();
		
		Calendar calCurrentDate = new GregorianCalendar(timeZone);
		calCurrentDate.setTime(currentDate);

		Calendar nextDate = new GregorianCalendar(timeZone);
		nextDate.setTime(currentDate);

		Calendar prevDate = new GregorianCalendar(timeZone);
		prevDate.setTime(currentDate);

		Calendar calStartDateRange = new GregorianCalendar(timeZone);
		Calendar calEndDateRange = new GregorianCalendar(timeZone);
   		calStartDateRange.setTime(currentDate);
   		calEndDateRange.setTime(currentDate);
		
   		String strSessGridType = null;
   		Integer sessGridSize = null;
   		EventsViewHelper.Grid grid = EventsViewHelper.getCalendarGrid(portletSession, userProperties, binderId.toString());
   		if (grid != null) {
   			strSessGridType = grid.type;
   			sessGridSize = grid.size;
   		}
       	String strSessGridSize = "";
       	if (sessGridSize != null) strSessGridSize = sessGridSize.toString(); 
      	
       	AbstractIntervalView intervalView = null;
       	if (EventsViewHelper.GRID_MONTH.equals(strSessGridType)) {
			Integer weekFirstDay = (Integer)userProperties.getProperty(ObjectKeys.USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK);
			weekFirstDay = weekFirstDay!=null?weekFirstDay:CalendarHelper.getFirstDayOfWeek();
			
       		intervalView = new OneMonthView(currentDate, weekFirstDay);
       		
//    		setStartDayOfMonth(calStartDateRange);
//    		calEndDateRange = (Calendar) calStartDateRange.clone();
//    		setEndDayOfMonth(calEndDateRange);
       		
       		nextDate.add(Calendar.MONTH, 1);
       		prevDate.add(Calendar.MONTH, -1);
       		
       	} else if (EventsViewHelper.GRID_DAY.equals(strSessGridType)) {
       		intervalView = new OneDayView(currentDate);
       		setDatesForGridDayView(calStartDateRange, calEndDateRange, strSessGridSize, prevDate, nextDate);
       	}
       	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Map options = getSearchFilter(bs, request, userFolderProperties);
		options.put(ObjectKeys.SEARCH_MAX_HITS, 10000);
       	// options.put(ObjectKeys.SEARCH_EVENT_DAYS, getExtViewDayDates(calStartDateRange, calEndDateRange));
       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, intervalView.getVisibleInterval());
       	
       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START, formatter.format(calStartDateRange.getTime()));
       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END, formatter.format(calEndDateRange.getTime()));

       	options.put(ObjectKeys.SEARCH_CREATION_DATE_START, formatter.format(calStartDateRange.getTime()));
       	options.put(ObjectKeys.SEARCH_CREATION_DATE_END, formatter.format(calEndDateRange.getTime()));

       	model.put(WebKeys.CALENDAR_PREV_DATE, prevDate);
       	model.put(WebKeys.CALENDAR_NEXT_DATE, nextDate);
       	model.put(WebKeys.CALENDAR_CURR_DATE, calCurrentDate);
       	model.put(WebKeys.CALENDAR_RANGE_END_DATE, calEndDateRange);
       	
		if (binder instanceof Folder) {
			folderEntries = bs.getFolderModule().getEntries(binderId, options);
		}
		
		return folderEntries;
	}
	
	public static void setDatesForGridDayView(Calendar calStartDateRange, Calendar calEndDateRange, 
			String strSessGridSize, Calendar prevDate, Calendar nextDate) {
		
		if (strSessGridSize == null || strSessGridSize.equals("1") || strSessGridSize.equals("-1") || strSessGridSize.equals(""))
			strSessGridSize = "1";
		
		int intRange = 1;
		try {
			intRange = Integer.parseInt(strSessGridSize);
		} catch (NumberFormatException e) {
			intRange = 1;
		}
	
		setStartOfDay(calStartDateRange);
		
		if (intRange == 1) {
			setEndOfDay(calEndDateRange);
		} else {
			setDaysToBeSearched(calEndDateRange, intRange);			
		}
	
		nextDate.add(Calendar.DAY_OF_MONTH, intRange);
		prevDate.add(Calendar.DAY_OF_MONTH, ((-1)*(intRange)));
	}
	
	public static List getExtViewDayDates(Calendar startViewExtWindow, Calendar endViewExtWindow) {
		Calendar start = (Calendar) startViewExtWindow.clone();
		Calendar end = (Calendar) endViewExtWindow.clone();

		List result = new ArrayList();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
		Calendar dayStart = start;
		
		Calendar dayEnd = (Calendar)dayStart.clone();
		dayEnd.add(Calendar.DAY_OF_MONTH, 1);
		dayEnd.add(Calendar.MILLISECOND, -1);
		
		while (dayStart.getTimeInMillis() <= end.getTimeInMillis()) {
			result.add(new String[] {formatter.format(dayStart.getTime()), formatter.format(dayEnd.getTime())});
			
			dayStart.add(Calendar.DAY_OF_MONTH, 1);
			
			dayEnd = (Calendar)dayStart.clone();
			dayEnd.add(Calendar.DAY_OF_MONTH, 1);
			dayEnd.add(Calendar.MILLISECOND, -1);
		}

		return result;
	}	

	private static void setStartDayOfMonth(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
	}

	private static void setEndDayOfMonth(Calendar startDateOfMonth) {
		startDateOfMonth.add(Calendar.MONTH, 1);
		startDateOfMonth.set(Calendar.HOUR_OF_DAY, 0);
		startDateOfMonth.set(Calendar.MINUTE, 0);
		startDateOfMonth.set(Calendar.SECOND, 0);
		startDateOfMonth.set(Calendar.MILLISECOND, 0);
		startDateOfMonth.add(Calendar.MILLISECOND, -1);
	}	

	private static void setStartOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	private static void setEndOfDay(Calendar cal) {
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MILLISECOND, -1);
	}

	private static void setDaysToBeSearched(Calendar cal, int numberOfDaysToBeSearched) {
		cal.add(Calendar.DAY_OF_MONTH, numberOfDaysToBeSearched);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MILLISECOND, -1);
	}
	
	protected static String getShowFolder(AllModulesInjected bs, Map formData, RenderRequest req, 
			RenderResponse response, Folder folder, Map options, 
			Map<String,Object>model, String viewType) throws Exception {
		Map folderEntries = null;
		Long folderId = folder.getId();
		User user = RequestContextHolder.getRequestContext().getUser();
					
		if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
			folderEntries = bs.getFolderModule().getFullEntries(folderId, options);
			Collection<FolderEntry> full = (Collection)folderEntries.get(ObjectKeys.FULL_ENTRIES);
			SeenMap seen = (SeenMap)model.get(WebKeys.SEEN_MAP);
			//viewing a blog is seeing the entries, but still want ui to display unseen icon
			//so return a dummy map that still has the entries unseen to the UI
			SeenMap newMap = new DummySeenMap(seen);
			model.put(WebKeys.SEEN_MAP, newMap);
			for (FolderEntry f:full) {
				//try to avoid extra set transaction if not needed
				if (!seen.checkIfSeen(f)) {  
					Collection<Entry> es = new ArrayList(full);
					bs.getProfileModule().setSeen(null, es);
					break;
				}
			}
			//Set the first item in the folder as viewed in the audit trail (arbitrary action)
			//TODO think of a better algorithm for what is viewed
			for (FolderEntry f:full) {
				bs.getReportModule().addAuditTrail(AuditTrail.AuditType.view, f);
				break;
			}
			
			//Get the WebDAV URLs
			buildWebDAVURLs(bs, req, folderEntries, model, folder);
			
			//Get the list of all entries to build the archive list
			buildBlogBeans(bs, response, folder, options, model, folderEntries);
		} else {
			String strUserDisplayStyle = user.getDisplayStyle();
			if (strUserDisplayStyle == null) { strUserDisplayStyle = ""; }
			
			if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) && 
					!ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle)) {
				// do it with ajax
			} else if (viewType.equals(Definition.VIEW_STYLE_TASK) && 
					!ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle)) {
				folderEntries = findTaskEntries(bs, req, response, (Binder) folder, model, options);
			} else if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) 
					&& ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle) &&
					!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
				folderEntries = findCalendarEvents(bs, req, response, (Binder) folder, model);
			}
			else {
				folderEntries = bs.getFolderModule().getEntries(folderId, options);
			}
			if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
				buildWikiBeans(bs, response, folder, options, model, folderEntries);
			}
			if (viewType.equals(Definition.VIEW_STYLE_BLOG) || 
					viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
				//Get the list of all entries to build the archive list
				buildBlogBeans(bs, response, folder, options, model, folderEntries);
			}
			if (viewType.equals(Definition.VIEW_STYLE_MILESTONE)) {
				//Get the list of all entries to build the archive list
				loadFolderStatisticsForPlacesAttributes(bs, response, folder, options, model, folderEntries);
			}			
			// viewType == task is pure ajax solution (view AjaxController)
		}

		model.putAll(getSearchAndPagingModels(folderEntries, options));
		if (folderEntries != null) {
			model.put(WebKeys.FOLDER_ENTRIES, (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
		}
		
		if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
				viewType.equals(Definition.VIEW_STYLE_TASK)) {
			// all in Ajax
		} else if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
			//This is a blog view, so get the extra blog beans
			getBlogEntries(bs, folder, folderEntries, model, req, response);
		}
		
		String type = PortletRequestUtils.getStringParameter(req, WebKeys.URL_TYPE, "");
		model.put(WebKeys.TYPE, type);
		String page = PortletRequestUtils.getStringParameter(req, WebKeys.URL_PAGE, "0");
		model.put(WebKeys.PAGE_NUMBER, page);
		if (type.equals(WebKeys.URL_WHATS_NEW)) 
			BinderHelper.setupWhatsNewBinderBeans(bs, folder, model, page);
		if (type.equals(WebKeys.URL_UNSEEN)) 
			BinderHelper.setupUnseenBinderBeans(bs, folder, model, page);

		//Build the mashup beans
		Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
		Element configElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		DefinitionHelper.buildMashupBeans(bs, folder, configDocument, model);
		
		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(bs, folder, model);
		Binder workspaceBinder = folder.getParentBinder();
		if (folder.isTop()) {
			workspaceBinder = folder.getParentBinder();
		} else {
			workspaceBinder = folder.getTopFolder().getParentBinder();
		}
		BinderHelper.buildWorkspaceTreeBean(bs, workspaceBinder, model, null);
		
		buildFolderToolbars(bs, req, response, folder, folderId.toString(), model, viewType);
		return BinderHelper.getViewListingJsp(bs, viewType);
	}
	
	protected static Map getSearchAndPagingModels(Map folderEntries, Map options) {
		Map model = new HashMap();
		
		if (folderEntries == null) {
			// there is no paging to set
			return model;
		}
		
		String sortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
		Boolean sortDescend = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
		
		model.put(WebKeys.FOLDER_SORT_BY, sortBy);		
		model.put(WebKeys.FOLDER_SORT_DESCEND, sortDescend.toString());
		
		int totalRecordsFound = (Integer) folderEntries.get(ObjectKeys.TOTAL_SEARCH_COUNT);
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

		model.put(WebKeys.PAGE_CURRENT, pagingInfo.get(WebKeys.PAGE_CURRENT));
		model.put(WebKeys.PAGE_PREVIOUS, prevPage);
		model.put(WebKeys.PAGE_NUMBERS, pageNumbers);
		model.put(WebKeys.PAGE_NEXT, nextPage);
		model.put(WebKeys.PAGE_START_INDEX, pageStartIndex);
		model.put(WebKeys.PAGE_END_INDEX, pageEndIndex);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, ""+totalRecordsFound);
		
		double dblNoOfPages = Math.ceil((double)totalRecordsFound/searchPageIncrement);
		
		model.put(WebKeys.PAGE_COUNT, ""+dblNoOfPages);
		model.put(WebKeys.PAGE_LAST, String.valueOf(Math.round(dblNoOfPages)));
		model.put(WebKeys.PAGE_LAST_STARTING_INDEX, String.valueOf((Math.round(dblNoOfPages) -1) * searchPageIncrement));
		model.put(WebKeys.SEARCH_TOTAL_HITS, folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		
		return model;
	}	
	
	protected static String getTeamMembers(AllModulesInjected bs, Map formData, RenderRequest req, 
			RenderResponse response, Folder folder, Map options, 
			Map<String,Object>model, String viewType) throws PortletRequestBindingException {
		
		try {
			Collection users = bs.getBinderModule().getTeamMembers(folder, true);
			model.put(WebKeys.TEAM_MEMBERS, users);
			model.put(WebKeys.TEAM_MEMBERS_COUNT, users.size());
		} catch (AccessControlException ac) {} //just skip
		
		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(bs, folder, model);
		
		buildFolderToolbars(bs, req, response, folder, folder.getId().toString(), model, viewType);
		return "entry/view_listing_team_members";
	}
	
	public static void getShowTemplate(AllModulesInjected bs, RenderRequest req, 
			RenderResponse response, Binder folder, Map<String,Object>model) throws PortletRequestBindingException {

		Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
		String viewType = DefinitionUtils.getViewType(configDocument);
		if (viewType == null) viewType = "";

		//	The "Display styles" menu
		Toolbar entryToolbar = new Toolbar();
//FolderEntries need folders as parents, not templates
//		if (bs.getAdminModule().testAccess(AdminOperation.manageTemplate)) {				
//			addEntryToolbar(bs, req, response, folder, entryToolbar, model);
//		}
		entryToolbar.addToolbarMenu("2_display_styles", NLT.get("toolbar.folder_views"));
		//Get the definitions available for use in this folder
		List<Definition> folderViewDefs = folder.getViewDefinitions();
		Definition currentDef = (Definition)model.get(WebKeys.DEFAULT_FOLDER_DEFINITION);  //current definition in use
		for (Definition def: folderViewDefs) {
			//Build a url to switch to this view
			Map qualifiers = new HashMap();
			if (def.equals(currentDef)) qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			//Build a url to switch to this view
			PortletURL url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_DEFINITION);
			url.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
			url.setParameter(WebKeys.URL_VALUE, def.getId());
			entryToolbar.addToolbarMenuItem("2_display_styles", "folderviews", NLT.getDef(def.getTitle()), url, qualifiers);
		}
		model.put(WebKeys.ENTRY_TOOLBAR,  entryToolbar.getToolbar());
		List entries = new ArrayList();
		model.put(WebKeys.FOLDER_ENTRIES, entries);
		//dummy seen map
		model.put(WebKeys.SEEN_MAP, new SeenMap(Long.valueOf(-1)));

		if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
			//Get the WebDAV URLs
			model.put(WebKeys.BLOG_ENTRIES, new TreeMap());
			model.put(WebKeys.FOLDER_ENTRIES_WEBDAVURLS, new HashMap());
			
			//Get the list of all entries to build the archive list
//			buildBlogBeans(bs, response, folder, options, model, folderEntries);
		} else if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
			//Get the list of all entries to build the archive list
			model.put(WebKeys.WIKI_HOMEPAGE_ENTRY_ID, folder.getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE));
		} else if (viewType.equals(Definition.VIEW_STYLE_CALENDAR)) {
			Date currentDate = EventsViewHelper.getCalendarCurrentDate(WebHelper.getRequiredPortletSession(req));
			model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
		}
		
	}
	//Method to find the WebDAV URL for each of the Blog entries
	public static void buildWebDAVURLs(AllModulesInjected bs, RenderRequest req, Map folderEntries, 
			Map model, Folder folder) {
		List folderList = (List) folderEntries.get(ObjectKeys.FULL_ENTRIES);
		HashMap hmWebDAVURLs = new HashMap();
		
		for (Iterator iter= folderList.iterator(); iter.hasNext();) {
			Object itrObj = iter.next();
			FolderEntry folderEntry = (FolderEntry) itrObj;
			String strWebDAVURL = DefinitionHelper.getWebDAVURL(req, folder, folderEntry);
			Long lngFolderEntry = folderEntry.getId();
			hmWebDAVURLs.put(lngFolderEntry, strWebDAVURL);
		}
		model.put(WebKeys.FOLDER_ENTRIES_WEBDAVURLS, hmWebDAVURLs);
	}
	
	//Routine to build the beans for the blog archives list
	public static void buildBlogBeans(AllModulesInjected bs, RenderResponse response, 
			Folder folder, Map options, Map model, Map folderEntries) {
		Document searchFilter = (Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER);
		if (searchFilter == null) {
			searchFilter = DocumentHelper.createDocument();
    		Element rootElement = searchFilter.addElement(SearchFilterKeys.FilterRootName);
        	rootElement.addElement(SearchFilterKeys.FilterTerms);
    		rootElement.addElement(SearchFilterKeys.FilterTerms);
		}
		Map options2 = new HashMap();
		options2.put(ObjectKeys.SEARCH_MAX_HITS, 
				Integer.valueOf(SPropsUtil.getString("blog.archives.searchCount")));
		options2.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
		options2.put(ObjectKeys.SEARCH_SORT_BY, Constants.CREATION_YEAR_MONTH_FIELD);
    	//Look only for binderId=binder and doctype = entry (not attachement)
    	if (folder != null) {
			Document searchFilter2 = DocumentHelper.createDocument();
    		Element rootElement = searchFilter2.addElement(Constants.AND_ELEMENT);
    		Element field = rootElement.addElement(Constants.FIELD_ELEMENT);
        	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.BINDER_ID_FIELD);
        	Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
        	child.setText(folder.getId().toString());
        	
        	//Look only for docType=entry and entryType=entry
        	field = rootElement.addElement(Constants.FIELD_ELEMENT);
        	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.DOC_TYPE_FIELD);
        	child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
        	child.setText(Constants.DOC_TYPE_ENTRY);
           	field = rootElement.addElement(Constants.FIELD_ELEMENT);
           	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_TYPE_FIELD);
           	child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
           	child.setText(Constants.ENTRY_TYPE_ENTRY);
        	options2.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter2);
    	}
		Map entriesMap = bs.getBinderModule().executeSearchQuery(searchFilter, options2);
		List entries = (List) entriesMap.get(ObjectKeys.SEARCH_ENTRIES);
		LinkedHashMap monthHits = new LinkedHashMap();
		Map monthTitles = new HashMap();
		Map monthUrls = new HashMap();
		Iterator itEntries = entries.iterator();
		while (itEntries.hasNext()) {
			Map entry = (Map)itEntries.next();
			if (entry.containsKey(Constants.CREATION_YEAR_MONTH_FIELD)) {
				String yearMonth = (String) entry.get(Constants.CREATION_YEAR_MONTH_FIELD);
				if (!monthHits.containsKey(yearMonth)) {
					monthHits.put(yearMonth, new Integer(0));
					String year = yearMonth.substring(0, 4);
					String monthNumber = yearMonth.substring(4, 6);
					int m = Integer.valueOf(monthNumber).intValue() - 1;
					monthTitles.put(yearMonth, NLT.get(monthNames[m%12]) + " " + year);
					PortletURL url = response.createRenderURL();
					url.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
					url.setParameter(WebKeys.URL_YEAR_MONTH, yearMonth);
					monthUrls.put(yearMonth, url.toString());
				}
				int hitCount = (Integer) monthHits.get(yearMonth);
				monthHits.put(yearMonth, new Integer(++hitCount));
			}
			if (entry.containsKey(Constants.CREATION_YEAR_MONTH_FIELD)) {
				//TODO - ???
			}
		}
		List entryCommunityTags = new ArrayList();
		List entryPersonalTags = new ArrayList();
		entryCommunityTags = BinderHelper.sortCommunityTags(entries);
		entryPersonalTags = BinderHelper.sortPersonalTags(entries);
		
		int intMaxHitsForCommunityTags = BinderHelper.getMaxHitsPerTag(entryCommunityTags);
		int intMaxHitsForPersonalTags = BinderHelper.getMaxHitsPerTag(entryPersonalTags);
		
		int intMaxHits = intMaxHitsForCommunityTags;
		if (intMaxHitsForPersonalTags > intMaxHitsForCommunityTags) intMaxHits = intMaxHitsForPersonalTags;
		
		entryCommunityTags = BinderHelper.rateCommunityTags(entryCommunityTags, intMaxHits);
		entryPersonalTags = BinderHelper.ratePersonalTags(entryPersonalTags, intMaxHits);

		entryCommunityTags = BinderHelper.determineSignBeforeTag(entryCommunityTags, "");
		entryPersonalTags = BinderHelper.determineSignBeforeTag(entryPersonalTags, "");
		model.put(WebKeys.BLOG_MONTH_HITS, monthHits);
		model.put(WebKeys.BLOG_MONTH_TITLES, monthTitles);
		model.put(WebKeys.BLOG_MONTH_URLS, monthUrls);
		model.put(WebKeys.FOLDER_ENTRYTAGS, entryCommunityTags);
		model.put(WebKeys.FOLDER_ENTRYPERSONALTAGS, entryPersonalTags);

		/*
		//The following code shows how to search for the count of entries in a month
		options2.put(ObjectKeys.SEARCH_MAX_HITS, 1);
		Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.AND_ELEMENT);
		Element andField = qTreeRootElement.addElement(QueryBuilder.AND_ELEMENT);
		Element field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATION_YEAR_MONTH_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText("200701");
    	options2.put(ObjectKeys.SEARCH_FILTER_AND, qTree);
		Map monthEntries = getBinderModule().executeSearchQuery(folder, searchFilter, options2);
		int c = 0;
		if (monthEntries.containsKey(WebKeys.ENTRY_SEARCH_COUNT)) 
			c = (Integer) monthEntries.get(WebKeys.ENTRY_SEARCH_COUNT);
		*/
	}

	public static void buildWikiBeans(AllModulesInjected bs, RenderResponse response, Binder binder, 
			Map options, Map model, Map folderEntries) {
		List entries = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		List entryCommunityTags = new ArrayList();
		List entryPersonalTags = new ArrayList();
		entryCommunityTags = BinderHelper.sortCommunityTags(entries);
		entryPersonalTags = BinderHelper.sortPersonalTags(entries);
		
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
		model.put(WebKeys.WIKI_HOMEPAGE_ENTRY_ID, binder.getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE));
	}
	
	//Routine to build the beans for the blog archives list
	public static void loadFolderStatisticsForPlacesAttributes(AllModulesInjected bs, 
			RenderResponse response, Binder binder, Map options, Map model, Map folderEntries) {
		if (folderEntries.get(ObjectKeys.SEARCH_ENTRIES) == null) {
			return;
		}
		Map folders = new HashMap();
		
		List placesIds = new ArrayList();
		
		Iterator it = ((List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES)).iterator();
		while (it.hasNext()) {
			Map entry = (Map)it.next();
			String definitionId = (String)entry.get(Constants.COMMAND_DEFINITION_FIELD);
			if (definitionId == null) {
				continue;
			}
			Definition def = DefinitionHelper.getDefinition(definitionId);
			if (def == null) {
				continue;
			}
			Iterator placesAttributeNamesIt = DefinitionHelper.findPlacesAttributes(def.getDefinition()).iterator();
			while (placesAttributeNamesIt.hasNext()) {
				String attributeName = (String)placesAttributeNamesIt.next();
			
				Object attributeValue = entry.get(attributeName);
				if (attributeValue != null) {
					if (attributeValue.getClass().isAssignableFrom(String.class)) {
						placesIds.add((String)attributeValue);
					} else if (attributeValue.getClass().isAssignableFrom(SearchFieldResult.class)) {
						placesIds.addAll(((SearchFieldResult)attributeValue).getValueSet());
					}
				}
			}
		}
		
		Iterator placesIdsIt = placesIds.iterator();
		while (placesIdsIt.hasNext()) {
			Long placeId = Long.parseLong((String)placesIdsIt.next());
			if (folders.get(placeId) != null) {
				continue;
			}
			try {
				Folder folder = bs.getFolderModule().getFolder(placeId);
				folders.put(placeId.toString(), folder);
			} catch (Exception ex) {continue;}
		}
		
		if (!folders.isEmpty()) {
			model.put(WebKeys.FOLDERS, folders);
		}
		
	}
	
	//This method returns a HashMap with Keys referring to the Previous Page Keys,
	//Paging Number related Page Keys and the Next Page Keys.
	public static HashMap getPagingLinks(int intTotalRecordsFound, int intSearchOffset, 
			int intSearchPageIncrement, int intGoBackSoManyPages, int intGoFrontSoManyPages) {
		
		HashMap<String, Object> hmRet = new HashMap<String, Object>();
		ArrayList<HashMap> pagingInfo = new ArrayList<HashMap>(); 
		int currentDisplayValue = ( intSearchOffset + intSearchPageIncrement) / intSearchPageIncrement;
		hmRet.put(WebKeys.PAGE_CURRENT, String.valueOf(currentDisplayValue));

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
		
		hmRet.put(WebKeys.PAGE_END_INDEX, "" + intTotalRecordsFound);
		
		return hmRet;
	}
	protected static void addEntryToolbar(AllModulesInjected bs, RenderRequest request, RenderResponse response, Binder folder, Toolbar entryToolbar, Map model) {
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		if (defaultEntryDefinitions != null && defaultEntryDefinitions.size() > 1) {
			int count = 1;
			Map dropdownQualifiers = new HashMap();
			dropdownQualifiers.put("highlight", new Boolean(true));
			entryToolbar.addToolbarMenu("1_add", NLT.get("toolbar.new"), "", dropdownQualifiers);
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			//String onClickPhrase = "if (self.ss_addEntry) {return(self.ss_addEntry(this))} else {return true;}";
			//qualifiers.put(ObjectKeys.TOOLBAR_QUALIFIER_ONCLICK, onClickPhrase);
			for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
				Definition def = (Definition) defaultEntryDefinitions.get(i);
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				String title = NLT.getDef(def.getTitle());
				if (entryToolbar.checkToolbarMenuItem("1_add", "entries", title)) {
					title = title + " (" + String.valueOf(count++) + ")";
				}
				entryToolbar.addToolbarMenuItem("1_add", "entries", title, adapterUrl.toString(), qualifiers);
				if (i == 0) {
					adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
					adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
					model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());
				}
			}
		} else if (defaultEntryDefinitions != null && defaultEntryDefinitions.size() != 0) {
			// Only one option
			Definition def = (Definition) defaultEntryDefinitions.get(0);
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
			adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
			String title = NLT.get("toolbar.new") + ": " + NLT.getDef(def.getTitle());
			Map qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			qualifiers.put("highlight", new Boolean(true));
			entryToolbar.addToolbarMenu("1_add", title, adapterUrl.toString(), qualifiers);
			
			adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
			adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
			model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());


		}		
	}
	protected static void buildFolderToolbars(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Folder folder, String forumId, Map model, String viewType) {
        User user = RequestContextHolder.getRequestContext().getUser();
        String userDisplayStyle = user.getDisplayStyle();
        if (userDisplayStyle == null) userDisplayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
        
		//Build the toolbar arrays
		Toolbar folderToolbar = new Toolbar();
		Toolbar entryToolbar = new Toolbar();
		Toolbar folderViewsToolbar = new Toolbar();
		Toolbar folderActionsToolbar = new Toolbar();
		Toolbar calendarImportToolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		Toolbar footerToolbar = new Toolbar();
		Toolbar whatsNewToolbar = new Toolbar();
		
		boolean isAppletSupported = SsfsUtil.supportApplets();
        boolean isAccessible = false;
		String displayStyle = user.getDisplayStyle();
		if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) &&
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			isAccessible = true;
		}
		
		AdaptedPortletURL adapterUrl;
		Map qualifiers;
		PortletURL url;
		if (bs.getFolderModule().testAccess(folder, FolderOperation.addEntry)) {				
			addEntryToolbar(bs, request, response, folder, entryToolbar, model);
		}
		//The "Administration" menu
		qualifiers = new HashMap();
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageFolderMenu");
		boolean adminMenuCreated=false;
		folderToolbar.addToolbarMenu("1_administration", 
				NLT.get("toolbar.manageThisFolder"), "", qualifiers);
		//Add Folder
		if (bs.getBinderModule().testAccess(folder, BinderOperation.addFolder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_SUB_FOLDER);
			folderToolbar.addToolbarMenuItem("1_administration", "folders", 
					NLT.get("toolbar.menu.addFolder"), url, qualifiers);
		}
		
		//Move binder
		if (bs.getBinderModule().testAccess(folder, BinderOperation.moveBinder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.move_folder"), url, qualifiers);
		}

		//Copy binder
		if (bs.getBinderModule().testAccess(folder, BinderOperation.copyBinder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_COPY);
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.copy_folder"), url, qualifiers);
		}

		//Configuration
		if (bs.getBinderModule().testAccess(folder, BinderOperation.modifyBinder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.configuration"), url, qualifiers);
		}
		
		//Site administration
		if (bs.getAdminModule().testAccess(AdminOperation.manageFunction)) {
			adminMenuCreated=true;
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_SITE_ADMINISTRATION);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.siteAdministration"), url);
		}

		//Reporting
		if (bs.getBinderModule().testAccess(folder, BinderOperation.report)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.report"), url, qualifiers);
		}
		
		if (bs.getBinderModule().testAccess(folder, BinderOperation.manageConfiguration)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("administration.definition_builder_designers"), url, qualifiers);
		}
		
		//Delete binder
		if (bs.getBinderModule().testAccess(folder, BinderOperation.deleteBinder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.delete_folder"), url, qualifiers);		
		}

		//Modify binder
		if (bs.getBinderModule().testAccess(folder, BinderOperation.modifyBinder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.modify_folder"), 
					adapterUrl.toString(), qualifiers);		
		}
	
		if (bs.getFolderModule().testAccess(folder, FolderOperation.addEntry)) {				
			adminMenuCreated=true;
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_IMPORT_FORUM_ENTRIES);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.import_forum_entries"), adapterUrl.toString(), qualifiers);		
		}
		
		if(LicenseChecker.isAuthorizedByLicense("com.sitescape.team.module.folder.MirroredFolder")) {
			//Synchronize mirrored folder
			if(folder.isMirrored() &&
					bs.getFolderModule().testAccess(folder, FolderOperation.synchronize)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				qualifiers.put("showSpinner", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SYNCHRONIZE_MIRRORED_FOLDER);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
				folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.synchronize_mirrored_folder.manual"), url, qualifiers);
			}
			if(folder.isMirrored() &&
					bs.getFolderModule().testAccess(folder, FolderOperation.scheduleSynchronization)) {
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));

				adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SCHEDULE_SYNCHRONIZATION);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
				folderToolbar.addToolbarMenuItem("1_administration", "", 
						NLT.get("toolbar.menu.synchronize_mirrored_folder.scheduled"), adapterUrl.toString(), qualifiers);
			}
		}

		//set email
		if (bs.getBinderModule().testAccess(folder, BinderOperation.manageMail)) {
			try {
				qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIG_EMAIL);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				folderToolbar.addToolbarMenuItem("1_administration", "", 
						NLT.get("toolbar.menu.configure_folder_email"), url, qualifiers);
				adminMenuCreated=true;
			} catch (AccessControlException ac) {};
		}

		//if no menu items were added, remove the empty menu
		if (!adminMenuCreated) folderToolbar.deleteToolbarMenu("1_administration");

		//Access control
		if (bs.getAdminModule().testAccess(folder, AdminOperation.manageFunctionMembership)) {
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.accessControlMenu");
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.accessControl"), url, qualifiers);
		}

		//The "Subsrciptions" menu
		if (folder.isTop() ) {
			if (!user.isShared()) {
				qualifiers = new HashMap();
				qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageSubscriptionsMenu");
				//folderToolbar.addToolbarMenu("3_administration", NLT.get("toolbar.manageFolderSubscriptions"), "", qualifiers);
			
				Subscription sub = bs.getBinderModule().getSubscription(folder);
				qualifiers = new HashMap();
				adapterUrl = new AdaptedPortletURL(request, "ss_forum", false);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
				adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SUBSCRIBE);			
				adapterUrl.setParameter("rn", "ss_randomNumberPlaceholder");			
				qualifiers.put("onClick", "ss_createPopupDiv(this, 'ss_subscription_menu');return false;");
				if (sub == null) {
					//folderToolbar.addToolbarMenuItem("3_administration", "", 
							//NLT.get("toolbar.menu.subscribeToFolder"), adapterUrl.toString(), qualifiers);	
					model.put(WebKeys.TOOLBAR_SUBSCRIBE_EMAIL, adapterUrl.toString());
				} else {
					//folderToolbar.addToolbarMenuItem("3_administration", "", 
							//NLT.get("toolbar.menu.subscriptionToFolder"), adapterUrl.toString(), qualifiers);
					model.put(WebKeys.TOOLBAR_SUBSCRIBE_EMAIL, adapterUrl.toString());
				}
			} else {
				qualifiers = new HashMap();
				//folderToolbar.addToolbarMenu("3_administration", NLT.get("toolbar.manageFolderSubscriptions"), "", qualifiers);

			}
			//RSS link 
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_showPermalink(this);return false;");
			String rssUrl = UrlUtil.getFeedURL(request, forumId);
			if (rssUrl != null && !rssUrl.equals("")) {
				//folderToolbar.addToolbarMenuItem("3_administration", "", NLT.get("toolbar.menu.rss"), rssUrl, qualifiers);
				model.put(WebKeys.TOOLBAR_SUBSCRIBE_RSS, rssUrl.toString());
			}
		}
		
		// list team members
		qualifiers = new HashMap();			
			
		//The "Teams" menu
		//folderToolbar.addToolbarMenu("5_team", NLT.get("toolbar.teams"));
			
		//Add
		if (bs.getBinderModule().testAccess(folder, BinderOperation.manageTeamMembers)) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("popupWidth", "500");
			qualifiers.put("popupHeight", "600");
			//folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.addMember"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_ADD_URL, adapterUrl.toString());
		}
		//View
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SHOW_TEAM_MEMBERS);
		url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
		//folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.view"), url);
		model.put(WebKeys.TOOLBAR_TEAM_VIEW_URL, url.toString());
			
		//Sendmail
		if (Validator.isNotNull(user.getEmailAddress()) && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL((PortletRequest) request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.sendmail"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_SENDMAIL_URL, adapterUrl.toString());
		}
		
		//Meet
		if (bs.getIcBrokerModule().isEnabled() && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			//folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.meet"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_MEET_URL, adapterUrl.toString());
		}
	
		
		//See if a "sort by" menu is needed
		if (viewType.equals(Definition.VIEW_STYLE_WIKI)|| viewType.equals(Definition.VIEW_STYLE_BLOG)|| 
				viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
			//Add a way to set the sorting
			UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
			String searchSortBy = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY);
			if (searchSortBy == null) searchSortBy = "";
			entryToolbar.addToolbarMenu("2_display_styles", NLT.get("toolbar.folder_sortBy"));
			String[] sortOptions = new String[] {"number", "title", "state", "author", "activity"};
			if (viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM) || 
					viewType.equals(Definition.VIEW_STYLE_WIKI)) {
				sortOptions = new String[] {"title",  "activity"};
			}
			Set so = new HashSet();
			for (String s : sortOptions) so.add(s);
			
			//number
			if (so.contains("number")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(Constants.DOCID_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, Constants.DOCID_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "true");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.Number"), url, qualifiers);
			}
			
			//title
			if (so.contains("title")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(Constants.SORT_TITLE_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, Constants.SORT_TITLE_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "false");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.Title"), url, qualifiers);
			}
			
			//state
			if (so.contains("state")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(Constants.WORKFLOW_STATE_CAPTION_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, Constants.WORKFLOW_STATE_CAPTION_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "false");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.State"), url, qualifiers);
			}
			
			//author
			if (so.contains("author")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(Constants.CREATOR_TITLE_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, Constants.CREATOR_TITLE_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "false");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.Author"), url, qualifiers);
			}
			
			//last activity date
			if (so.contains("activity")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(Constants.LASTACTIVITY_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, Constants.LASTACTIVITY_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "true");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.LastActivity"), url, qualifiers);
			}
			
			//rating
			if (so.contains("rating")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(Constants.RATING_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, Constants.RATING_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "true");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.Rating"), url, qualifiers);
			}
		}
		
		if (isAppletSupported && !folder.isMirroredAndReadOnly() && 
				bs.getFolderModule().testAccess(folder, FolderOperation.addEntry) && 
				!isAccessible) {
			qualifiers = new HashMap();
			qualifiers.put("onClick", "javascript: ss_showFolderAddAttachmentDropbox('" + response.getNamespace() + "', '" + folder.getId() + "','" + Boolean.toString(folder.isLibrary()) + "'); return false;");
			entryToolbar.addToolbarMenu("dropBox", NLT.get("toolbar.menu.dropBox"), "javascript: ;", qualifiers);
		}
		
		//	The "Display styles" menu
		folderViewsToolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.folder_views"));
		//Get the definitions available for use in this folder
		List<Definition> folderViewDefs = folder.getViewDefinitions();
		Definition currentDef = (Definition)model.get(WebKeys.DEFAULT_FOLDER_DEFINITION);  //current definition in use
		for (Definition def: folderViewDefs) {
			//Build a url to switch to this view
			qualifiers = new HashMap();
			if (def.equals(currentDef)) qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_DEFINITION);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, def.getId());
			folderViewsToolbar.addToolbarMenuItem("3_display_styles", "folderviews", NLT.getDef(def.getTitle()), url, qualifiers);
		}
		//WebDav folder view
		String webdavUrl = "";
		if (folder.isLibrary()) {
			webdavUrl = SsfsUtil.getLibraryBinderUrl(request, folder);
			qualifiers = new HashMap();
			qualifiers.put("webdavUrl", webdavUrl);
			qualifiers.put("folder", webdavUrl);
			folderViewsToolbar.addToolbarMenuItem("3_display_styles", "folderviews", NLT.get("toolbar.menu.viewASWebDav"), webdavUrl, qualifiers);
		}
		
		//WebDav Permalink
		if (!webdavUrl.equals("")) {
			qualifiers = new HashMap();
			qualifiers.put("webdavUrl", webdavUrl);
			qualifiers.put("onClick", "ss_showPermalink(this);return false;");
			footerToolbar.addToolbarMenu("webdavpermalink", NLT.get("toolbar.menu.webdavPermalink"), 
					webdavUrl, qualifiers);
		}
		
		//Folder action menu
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			//Folder action menu
			//Build the standard toolbar
			BinderHelper.buildFolderActionsToolbar(bs, request, response, folderActionsToolbar, forumId);
		}
		
		//Calendar import menu
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) && 
				(viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
						viewType.equals(Definition.VIEW_STYLE_TASK)) &&
				bs.getFolderModule().testAccess(folder, FolderOperation.addEntry)) {
			
			String titleFromFile = NLT.get("calendar.import.window.title.fromFile").replaceAll("'", "\\'");
			String titleByURL = NLT.get("calendar.import.window.title.byURL").replaceAll("'", "\\'");
			String legendFromFile = NLT.get("calendar.import.window.legend.fromFile").replaceAll("'", "\\'");
			String legendByURL = NLT.get("calendar.import.window.legend.byURL").replaceAll("'", "\\'");
			String btnFromFile = NLT.get("calendar.import.window.upload.fromFile").replaceAll("'", "\\'");
			String btnByURL = NLT.get("calendar.import.window.upload.byURL").replaceAll("'", "\\'");
			String optionTitle = NLT.get("toolbar.menu.calendarImport").replaceAll("'", "\\'");
			String importFromFile = NLT.get("toolbar.menu.calendarImport.fromFile");
			String importByURL = NLT.get("toolbar.menu.calendarImport.byURL");
			if (viewType.equals(Definition.VIEW_STYLE_TASK)) {
				titleFromFile = NLT.get("task.import.window.title.fromFile").replaceAll("'", "\\'");
				titleByURL = NLT.get("task.import.window.title.byURL").replaceAll("'", "\\'");
				legendFromFile = NLT.get("task.import.window.legend.fromFile").replaceAll("'", "\\'");
				legendByURL = NLT.get("task.import.window.legend.byURL").replaceAll("'", "\\'");
				btnFromFile = NLT.get("task.import.window.upload.fromFile").replaceAll("'", "\\'");
				btnByURL = NLT.get("task.import.window.upload.byURL").replaceAll("'", "\\'");
				optionTitle = NLT.get("toolbar.menu.taskImport").replaceAll("'", "\\'");
				importFromFile = NLT.get("toolbar.menu.taskImport.fromFile");
				importByURL = NLT.get("toolbar.menu.taskImport.byURL");				
			}
			
			calendarImportToolbar.addToolbarMenu("5_calendar", optionTitle);	
			
			Map qualifiersByFile = new HashMap();
			qualifiersByFile.put("onClick", "ss_calendar_import.importFormFromFile({forumId: '" + forumId + "', namespace: '" + response.getNamespace() + "', title: '" + 
					titleFromFile + "', legend: '" + legendFromFile + "', btn: '" + btnFromFile + "'});return false;");
			calendarImportToolbar.addToolbarMenuItem("5_calendar", "calendar", importFromFile, "#", qualifiersByFile);
			
			Map qualifiersByURL = new HashMap();
			qualifiersByURL.put("onClick", "ss_calendar_import.importFormByURL({forumId: '" + forumId + "', namespace: '" + response.getNamespace() + "', title: '" + 
					titleByURL + "', legend: '" + legendByURL + "', btn: '" + btnByURL + "'});return false;");
			calendarImportToolbar.addToolbarMenuItem("5_calendar", "calendar", importByURL, "#", qualifiersByURL);
		}
		
		//Build the "Manage dashboard" toolbar
		BinderHelper.buildDashboardToolbar(request, response, bs, folder, dashboardToolbar, model);

		//The "Footer" menu
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, folder.getEntityType().toString());
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showPermalink(this);return false;");
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.folderPermalink"), 
				adapterUrl.toString(), qualifiers);
		
		model.put(WebKeys.PERMALINK, adapterUrl.toString());

		String[] contributorIds = collectContributorIds((List)model.get(WebKeys.FOLDER_ENTRIES));
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		// iCalendar
		if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
				viewType.equals(Definition.VIEW_STYLE_TASK)) {
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_showPermalink(this);return false;");
			footerToolbar.addToolbarMenu("iCalendar", NLT.get("toolbar.menu.iCalendar"), com.sitescape.team.ical.util.UrlUtil.getICalURL(request, forumId, null), qualifiers);
		}

		// clipboard
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			qualifiers = new HashMap();
			String contributorIdsAsJSString = "";
			for (int i = 0; i < contributorIds.length; i++) {
				contributorIdsAsJSString += contributorIds[i];
				if (i < (contributorIds.length -1)) {
					contributorIdsAsJSString += ", ";	
				}
			}
			qualifiers.put("onClick", "ss_muster.showForm('" + Clipboard.USERS + "', [" + contributorIdsAsJSString + "]" + ", '" + forumId + "');return false;");
			//footerToolbar.addToolbarMenu("clipboard", NLT.get("toolbar.menu.clipboard"), "#", qualifiers);
			model.put(WebKeys.TOOLBAR_CLIPBOARD_IDS, contributorIds);
			model.put(WebKeys.TOOLBAR_CLIPBOARD_SHOW, Boolean.TRUE);
		}
		
		// email
		if (user.getEmailAddress() != null && !user.getEmailAddress().equals("") && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			if (op.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());	
			}		
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			if (!op.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				qualifiers.put("post", Boolean.TRUE);
				qualifiers.put("postParams", Collections.singletonMap(WebKeys.USER_IDS_TO_ADD, contributorIds));
				model.put(WebKeys.TOOLBAR_SENDMAIL_POST, Boolean.TRUE);
			}
			//footerToolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_SENDMAIL_URL, adapterUrl.toString());
			model.put(WebKeys.TOOLBAR_SENDMAIL_IDS, contributorIds);
		}

		// start meeting
		if (bs.getIcBrokerModule().isEnabled() && 
				!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			if (op.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());	
			}
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);		
			if (!op.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				qualifiers.put("post", Boolean.TRUE);
				qualifiers.put("postParams", Collections.singletonMap(WebKeys.USER_IDS_TO_ADD, contributorIds));
				model.put(WebKeys.TOOLBAR_MEETING_POST, Boolean.TRUE);
			}
			//footerToolbar.addToolbarMenu("addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_MEETING_URL, adapterUrl.toString());
			model.put(WebKeys.TOOLBAR_MEETING_IDS, contributorIds);
		}
		if (folder.isLibrary() && !webdavUrl.equals("")) {
			qualifiers = new HashMap();
			qualifiers.put("webdavUrl", webdavUrl);
			qualifiers.put("folder", webdavUrl);
			footerToolbar.addToolbarMenu("webdavUrl", NLT.get("toolbar.menu.webdavUrl"), webdavUrl, qualifiers);
		}
		
		//Set up the whatsNewToolbar links
		//What's new
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_TYPE, "whatsNew");
		adapterUrl.setParameter(WebKeys.URL_PAGE, "0");
		adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showWhatsNewPage(this, '"+forumId+"', 'whatsNew', '0', '', 'ss_whatsNewDiv', '"+response.getNamespace()+"');return false;");
		whatsNewToolbar.addToolbarMenu("whatsnew", NLT.get("toolbar.menu.whatsNew"), 
				adapterUrl.toString(), qualifiers);
		
		// What's unseen
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_TYPE, "unseen");
		adapterUrl.setParameter(WebKeys.URL_PAGE, "0");
		adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showWhatsNewPage(this, '"+forumId+"', 'unseen', '0', '', 'ss_whatsNewDiv', '"+response.getNamespace()+"');return false;");
		whatsNewToolbar.addToolbarMenu("unseen", NLT.get("toolbar.menu.whatsUnseen"), 
				adapterUrl.toString(), qualifiers);

		
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			qualifiers = new HashMap();
			qualifiers.put("onClick", "javascript: ss_changeUITheme('" +
					NLT.get("ui.availableThemeIds") + "', '" +
					NLT.get("ui.availableThemeNames") + "'); return false;");
			//footerToolbar.addToolbarMenu("themeChanger", NLT.get("toolbar.menu.changeUiTheme"), "javascript: ;", qualifiers);
			model.put(WebKeys.TOOLBAR_THEME_IDS, NLT.get("ui.availableThemeIds"));
			model.put(WebKeys.TOOLBAR_THEME_NAMES, NLT.get("ui.availableThemeNames"));
		}
		
		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR,  folderToolbar.getToolbar());
		model.put(WebKeys.ENTRY_TOOLBAR,  entryToolbar.getToolbar());
		model.put(WebKeys.FOLDER_VIEWS_TOOLBAR,  folderViewsToolbar.getToolbar());
		model.put(WebKeys.FOLDER_ACTIONS_TOOLBAR,  folderActionsToolbar.getToolbar());
		model.put(WebKeys.CALENDAR_IMPORT_TOOLBAR,  calendarImportToolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
		model.put(WebKeys.WHATS_NEW_TOOLBAR,  whatsNewToolbar.getToolbar());
	}
	

	private static String[] collectContributorIds(List entries) {
		Set principals = new HashSet();
		
		if (entries != null) {
			Iterator entriesIt = entries.iterator();
			while (entriesIt.hasNext()) {
				Map entry = (Map)entriesIt.next();
				String creatorId = entry.get(Constants.CREATORID_FIELD).toString();
				String modificationId = entry.get(Constants.MODIFICATIONID_FIELD).toString();
				principals.add(creatorId);
				principals.add(modificationId);
			}	
		}
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}

	protected static void getBlogEntries(AllModulesInjected bs, Folder folder, 
			Map folderEntries,  Map model, RenderRequest request, RenderResponse response) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Map entries = new TreeMap();
		model.put(WebKeys.BLOG_ENTRIES, entries);

		List entrylist = (List)folderEntries.get(ObjectKeys.FULL_ENTRIES);
		Map publicTags = (Map)folderEntries.get(ObjectKeys.COMMUNITY_ENTITY_TAGS);
		Map privateTags = (Map)folderEntries.get(ObjectKeys.PERSONAL_ENTITY_TAGS);
		Iterator entryIterator = entrylist.listIterator();
		while (entryIterator.hasNext()) {
			FolderEntry entry  = (FolderEntry) entryIterator.next();
			Map entryMap = new HashMap();
			Map accessControlEntryMap = BinderHelper.getAccessControlEntityMapBean(model, entry);
			entries.put(entry.getId().toString(), entryMap);
			entryMap.put("entry", entry);
			if (DefinitionHelper.getDefinition(entry.getEntryDef(), entryMap, "//item[@name='entryBlogView']") == false) {
				//this will fill it the entryDef for the entry
				DefinitionHelper.getDefaultEntryView(entry, entryMap, "//item[@name='entryBlogView']");				
			}
			//See if this entry can have replies added
			entryMap.put(WebKeys.REPLY_BLOG_URL, "");
			Definition def = entry.getEntryDef();
			if (bs.getFolderModule().testAccess(entry, FolderOperation.addReply)) {
				accessControlEntryMap.put("addReply", new Boolean(true));
				Document defDoc = def.getDefinition();
				List replyStyles = DefinitionUtils.getPropertyValueList(defDoc.getRootElement(), "replyStyle");
				if (!replyStyles.isEmpty()) {
					String replyStyleId = (String)replyStyles.get(0);
					if (!replyStyleId.equals("")) {
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
						adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
						adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
						adapterUrl.setParameter(WebKeys.URL_BLOG_REPLY, "1");
						adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
						entryMap.put(WebKeys.REPLY_BLOG_URL, adapterUrl);
					}
				}
			}
			//See if the user can modify this entry
			boolean reserveAccessCheck = false;
			boolean isUserBinderAdministrator = false;
			boolean isEntryReserved = false;
			boolean isLockedByAndLoginUserSame = false;

			if (bs.getFolderModule().testAccess(entry, FolderOperation.reserveEntry)) {
				reserveAccessCheck = true;
			}
			if (bs.getFolderModule().testAccess(entry, FolderOperation.overrideReserveEntry)) {
				isUserBinderAdministrator = true;
			}
			
			HistoryStamp historyStamp = entry.getReservation();
			if (historyStamp != null) isEntryReserved = true;

			if (isEntryReserved) {
				Principal lockedByUser = historyStamp.getPrincipal();
				if (lockedByUser.getId().equals(user.getId())) {
					isLockedByAndLoginUserSame = true;
				}
			}
			if (bs.getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
				if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
				} else {
					accessControlEntryMap.put("modifyEntry", new Boolean(true));
				}
			}

			entryMap.put(WebKeys.COMMUNITY_TAGS, publicTags.get(entry.getId()));
			entryMap.put(WebKeys.PERSONAL_TAGS, privateTags.get(entry.getId()));
		}
	}

	private static Map findTaskEntries(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Binder binder, Map model, Map options) throws PortletRequestBindingException {
		model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
		Long binderId = binder.getId();
		
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);

		Map folderEntries = new HashMap();
		
		String filterTypeParam = PortletRequestUtils.getStringParameter(request, WebKeys.TASK_FILTER_TYPE, null);
		TaskHelper.FilterType filterType = TaskHelper.setTaskFilterType(portletSession, filterTypeParam != null ? TaskHelper.FilterType.valueOf(filterTypeParam) : null);
		model.put(WebKeys.TASK_CURRENT_FILTER_TYPE, filterType);
		
		options.put(ObjectKeys.SEARCH_SEARCH_DYNAMIC_FILTER, TaskHelper.buildSearchFilter(filterType).getFilter());
       	
		if (binder instanceof Folder) {
			folderEntries = bs.getFolderModule().getEntries(binderId, options);
		} else {
			//a template
			folderEntries = new HashMap();
		}

		Map<String, Map> cacheEntryDef = new HashMap();
    	List items = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
	    		String entryDefId = (String)entry.get(Constants.COMMAND_DEFINITION_FIELD);
	    		if (cacheEntryDef.get(entryDefId) == null) {
	    			cacheEntryDef.put(entryDefId, bs.getDefinitionModule().getEntryDefinitionElements(entryDefId));
	    		}
	    		entry.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, cacheEntryDef.get(entryDefId));
	    	}
    	}
		
		return folderEntries;
	}
	/**
	 * Dummy class to wrap old seenMap so we can set the entries as seen from a controller,
	 * but maintain the previous state for the jsps.
	 * @author Janet
	 *
	 */
	public static class DummySeenMap extends SeenMap {
		protected DummySeenMap() {};
		
		protected DummySeenMap(SeenMap currentMap) {
			setSeenMap(new HashMap(currentMap.getSeenMap()));
		}
		public void setSeen(Entry entry) {
		}
		public void setSeen(FolderEntry entry) {
		}
		public boolean checkIfSeen(FolderEntry entry) {
			return checkAndSetSeen(entry, false);
		}
		protected boolean checkAndSetSeen(FolderEntry entry, boolean setIt) {
			return super.checkAndSetSeen(entry, false);
		}
		public boolean checkAndSetSeen(Map entry, boolean setIt) {
			return super.checkAndSetSeen(entry, false);
		}	
		public boolean checkIfSeen(Map entry) {
			return super.checkAndSetSeen(entry, false);
		}   
		   
	}
}
