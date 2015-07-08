/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import static org.kablink.util.search.Restrictions.in;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.lucene.document.DateTools;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.AbstractIntervalView;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.calendar.OneDayView;
import org.kablink.teaming.calendar.OneMonthView;
import org.kablink.teaming.calendar.StartEndDatesView;
import org.kablink.teaming.calendar.EventsViewHelper.Grid;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.rss.util.UrlUtil;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.support.PortletAdapterUtil;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.CalendarHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.PortletRequestBindingException;

/**
 * ?
 *  
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
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
	public static final String[] folderColumns= new String[] {"number", "title", "comments", "size", "download", "html", "state", "author", "date", "rating"};

	public enum ModeType {
		PHYSICAL, VIRTUAL, MY_EVENTS;
	}
	public static final ModeType MODE_TYPE_DEFAULT = ModeType.PHYSICAL;

	static public ModelAndView BuildFolderBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long binderId, String zoneUUID, boolean showTrash) throws Exception {
		binderId = bs.getBinderModule().getZoneBinderId(binderId, zoneUUID, EntityType.folder.name());
		if (binderId == null) {
			Map<String,Object> model = new HashMap<String,Object>();
			String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
			model.put(WebKeys.REFERER_URL, refererUrl);
			model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.folder.not.imported"));
			return new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model);
		}
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
		model.put(WebKeys.URL_SHOW_TRASH, new Boolean(showTrash));
		String view = BinderHelper.getViewListingJsp(bs, null);;
		
		//Set up the standard beans
		BinderHelper.setupStandardBeans(bs, request, response, model, binderId);
		UserProperties userProperties = (UserProperties)model.get(WebKeys.USER_PROPERTIES_OBJ);
		UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);

		String errorMsg = PortletRequestUtils.getStringParameter(request, WebKeys.ENTRY_DATA_PROCESSING_ERRORS, "", false);
		String errorMsg2 = PortletRequestUtils.getStringParameter(request, WebKeys.FILE_PROCESSING_ERRORS, "", false);
		if (errorMsg.equals("")) {
			errorMsg = errorMsg2;
		} else if (!errorMsg.equals("") && !errorMsg2.equals("")) {
			errorMsg += "<br/>" + errorMsg2;
		}
		model.put(WebKeys.FILE_PROCESSING_ERRORS, errorMsg);
		if (!errorMsg.equals("")) {
			model.put(WebKeys.ERROR_MESSAGE, errorMsg);
			return new ModelAndView("forum/reload_opener", model);
		}

		//See if the entry to be shown is also included
		String entryIdToBeShown = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		if (entryIdToBeShown.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER)) entryIdToBeShown = "";
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "", false);
		if (!entryTitle.equals("") && !entryTitle.equals(WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER)) {
			//This must be a request for a title link
			Set entries = bs.getFolderModule().getFolderEntryByNormalizedTitle(binderId, entryTitle, zoneUUID);
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
				model.put(WebKeys.ERROR_MESSAGE, NLT.get("errorcode.no.folder.by.the.id", new String[] {binderId.toString()}));
				return new ModelAndView(WebKeys.VIEW_ERROR_RETURN, model);
			} catch(AccessControlException e) {
				if (WebHelper.isUserLoggedIn(request) && 
						!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
					//Access is not allowed
					String refererUrl = (String)request.getAttribute(WebKeys.REFERER_URL);
					model.put(WebKeys.URL, refererUrl);
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
			//model.put(WebKeys.ENTRY, binder);
			Map accessControlBinderMap = BinderHelper.getAccessControlEntityMapBean(model, binder);
			if (bs.getBinderModule().testAccess(binder, BinderOperation.deleteEntries)) {
				accessControlBinderMap.put("deleteEntries", new Boolean(true));
			}
	
			//Build a reload url
			PortletURL reloadUrl = response.createRenderURL();
			reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
			if (binder != null && binder.getParentBinder() != null) reloadUrl.setParameter(WebKeys.URL_BINDER_PARENT_ID, 
					binder.getParentBinder().getId().toString());
			reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
			reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
			model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
		
			if (!model.containsKey(WebKeys.SEEN_MAP)) 
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
			
			Map options = getSearchFilter(bs, request, binder, userFolderProperties);
			
			//determine page starts/ counts
			initPageCounts(bs, request, userProperties.getProperties(), tab, options);
	
			Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			Element configElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
			String viewType = DefinitionUtils.getViewType(configDocument);
			if (viewType == null) viewType = "";
			model.put(WebKeys.VIEW_TYPE, viewType);

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
			} else if (viewType.equals(Definition.VIEW_STYLE_CALENDAR)) {
				Workspace binderWs = BinderHelper.getBinderWorkspace(binder);
				boolean showModeSelect = (BinderHelper.isBinderUserWorkspace(binderWs));
				model.put(WebKeys.FOLDER_SHOW_MODE_SELECT, new Boolean(showModeSelect));
			} else if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
				options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES_WIKI);
				options.put(ObjectKeys.SEARCH_OFFSET, 0);
			}
	
			//Checking the Sort Order that has been set. If not using the Default Sort Order
			BinderHelper.initSortOrder(bs, userFolderProperties, options, viewType);
	
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
				view = getShowFolder(bs, formData, request, response, (Folder)binder, options, model, viewType, showTrash);
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

	/**
	 * 
	 * @param bs
	 * @param request
	 * @param binder
	 * @param userFolderProperties
	 * @param searchTrash
	 * @return
	 */
	public static Map getSearchFilter(AllModulesInjected bs, RenderRequest request, Binder binder, UserProperties userFolderProperties, boolean searchTrash) {
		Map result = new HashMap();
		result.put(ObjectKeys.SEARCH_SEARCH_FILTER, BinderHelper.getSearchFilter(bs, binder, userFolderProperties));
		String searchTitle = ((null == request) ? "" : PortletRequestUtils.getStringParameter(request, WebKeys.SEARCH_TITLE, ""));
		if (!searchTitle.equals("")) {
			result.put(ObjectKeys.SEARCH_TITLE, searchTitle);
		}
		
		return result;
	}
	public static Map getSearchFilter(AllModulesInjected bs, RenderRequest request, Binder binder, UserProperties userFolderProperties) {
		return getSearchFilter(bs, request, binder, userFolderProperties, false);
	}
	
	protected static void initPageCounts(AllModulesInjected bs, RenderRequest request, 
			Map userProperties, Tabs.TabEntry tab, Map options) {
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
			if (null == recordsInPage) {
				recordsInPage = Integer.valueOf(entriesPerPage);
			}
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
			model.put(WebKeys.YEAR_MONTH, yearMonth);
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

	//Return the list of calendar events to show
	//  Note: this routine is used by the Mobile devices to get the list of calendar events to show
	public static List findCalendarEvents(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Binder binder, Map model) throws PortletRequestBindingException {
		Map folderEntries = new HashMap();
		Long binderId = binder.getId();
		List binderIds = Arrays.asList(String.valueOf(binderId));
		
		int year = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_YEAR, -1);
		int month = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_MONTH, -1);
		int dayOfMonth = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_DAY_OF_MONTH, -1);
		
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		String calendarStickyId = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_STICKY_ID, String.valueOf(binderId) + "_");
		String calendarModeType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_MODE_TYPE, "");
		
		Date currentDate = EventsViewHelper.getCalendarCurrentDate(portletSession);
		currentDate = EventsViewHelper.getDate(year, month, dayOfMonth, currentDate);
		model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
		EventsViewHelper.setCalendarCurrentDate(portletSession, currentDate);
		
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
		UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
		TimeZone timeZone = user.getTimeZone();
		
		Grid oldGrid = EventsViewHelper.getCalendarGrid(portletSession, userProperties, binderId.toString() + "_");
		String gridType = "";
		Integer gridSize = -1;
		if (oldGrid != null) {
			gridType = oldGrid.type;
			gridSize = oldGrid.size;
		}
		gridType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_GRID_TYPE, gridType);
		gridSize = PortletRequestUtils.getIntParameter(request, WebKeys.CALENDAR_GRID_SIZE, gridSize);
		
		Map grids = EventsViewHelper.setCalendarGrid(portletSession, userProperties, calendarStickyId, gridType, gridSize);

		model.put(WebKeys.CALENDAR_GRID_TYPE, ((EventsViewHelper.Grid)grids.get(calendarStickyId)).type);
		model.put(WebKeys.CALENDAR_GRID_SIZE, ((EventsViewHelper.Grid)grids.get(calendarStickyId)).size);
		
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

       	model.put(WebKeys.CALENDAR_PREV_DATE, prevDate);
       	model.put(WebKeys.CALENDAR_NEXT_DATE, nextDate);
       	model.put(WebKeys.CALENDAR_CURR_DATE, calCurrentDate);
       	model.put(WebKeys.CALENDAR_RANGE_END_DATE, calEndDateRange);

   		String strSessGridType = gridType;
   		Integer sessGridSize = gridSize;
   		EventsViewHelper.Grid grid = EventsViewHelper.getCalendarGrid(portletSession, userProperties, binderId.toString() + "_");
   		if (grid != null) {
   			strSessGridType = grid.type;
   			sessGridSize = grid.size;
   		}
       	String strSessGridSize = "";
       	if (sessGridSize != null) strSessGridSize = sessGridSize.toString(); 
      	
		Integer weekFirstDay = (Integer)userProperties.getProperty(ObjectKeys.USER_PROPERTY_CALENDAR_FIRST_DAY_OF_WEEK);
		weekFirstDay = weekFirstDay!=null?weekFirstDay:CalendarHelper.getFirstDayOfWeek();
		
       	AbstractIntervalView intervalView = new OneMonthView(currentDate, weekFirstDay);;
       	if (EventsViewHelper.GRID_MONTH.equals(strSessGridType)) {
			
       		intervalView = new OneMonthView(currentDate, weekFirstDay);
       		
//    		setStartDayOfMonth(calStartDateRange);
//    		calEndDateRange = (Calendar) calStartDateRange.clone();
//    		setEndDayOfMonth(calEndDateRange);
       		
       		nextDate.add(Calendar.MONTH, 1);
       		prevDate.add(Calendar.MONTH, -1);
       		
       	} else if (EventsViewHelper.GRID_DAY.equals(strSessGridType) && strSessGridSize.equals("1")) {
       		intervalView = new OneDayView(currentDate);
       		setDatesForGridDayView(calStartDateRange, calEndDateRange, strSessGridSize, prevDate, nextDate);
       	} else if (EventsViewHelper.GRID_DAY.equals(strSessGridType) && !strSessGridSize.equals("1")) {
       		Calendar cal = Calendar.getInstance();  
       		cal.setTime(currentDate);
       		cal.add(Calendar.DATE, Integer.valueOf(strSessGridSize));
       		Date currentEndDate = cal.getTime();
       		intervalView = new StartEndDatesView(currentDate, currentEndDate);
       		setDatesForGridDayView(calStartDateRange, calEndDateRange, strSessGridSize, prevDate, nextDate);
       	}
       	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Map options = getSearchFilter(bs, request, binder, userFolderProperties);
		
		options.put(ObjectKeys.SEARCH_MAX_HITS, 10000);
		options.put( ObjectKeys.SEARCH_SORT_DESCEND, new Boolean( false ) );
		
		List intervals = new ArrayList(1);
		intervals.add(intervalView.getVisibleIntervalRaw());
       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, intervals);
       	
		AbstractIntervalView calendarViewRangeDates = intervalView; //! new OneMonthView(currentDate, weekFirstDay);
       	String start = DateTools.dateToString(calendarViewRangeDates.getVisibleStart(), DateTools.Resolution.SECOND);
       	String end =  DateTools.dateToString(calendarViewRangeDates.getVisibleEnd(), DateTools.Resolution.SECOND);
       	
		String eventsType = EventsViewHelper.getCalendarDisplayEventType(bs, user.getId(), binderId);
		boolean eventDate    = ((null != eventsType) && "event".equals(eventsType));
		boolean activityDate = ((null != eventsType) && "activity".equals(eventsType));
		boolean creationDate = ((null != eventsType) && "creation".equals(eventsType));
		boolean virtual      = ((null != eventsType) && "virtual".equals(eventsType));
		
		if (activityDate) {
			options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START, start);
			options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END, end);
		}
		else if (creationDate) {
	       	options.put(ObjectKeys.SEARCH_CREATION_DATE_START, start);
	       	options.put(ObjectKeys.SEARCH_CREATION_DATE_END, end);
		}

       	if (!calendarModeType.equals(ObjectKeys.CALENDAR_MODE_TYPE_MY_EVENTS)) {
			//See if there is a filter turned on for this folder. But don't do it for the MyEvents display
			options.putAll(ListFolderHelper.getSearchFilter(bs, request, binder, userFolderProperties));
		}
		Document baseFilter = ((Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER));
		boolean filtered = (null != baseFilter); 
		if (filtered) {
			Element preDeletedOnlyTerm = (Element)baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@preDeletedOnly='true']");
			if (preDeletedOnlyTerm != null) {
				options.put(ObjectKeys.SEARCH_PRE_DELETED, Boolean.TRUE);
			}
		}				
		
		// Are we searching for events in a folder or workspace?
       	List entries = new ArrayList();;
		if (binder instanceof Folder || binder instanceof Workspace) {
			// Yes!  Are we searching for physical events using
			// a filter?
			if ((!virtual) && filtered) {
				// Yes!  Simply perform the search using that
				// filter.
				folderEntries = bs.getBinderModule().executeSearchQuery(baseFilter, Constants.SEARCH_MODE_NORMAL, options);
				entries = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
			
			} else {
				// No, the search is either for virtual event
				// or not using a filter!  Is it a search for
				// physical events?
				if (!virtual) {
					// Yes!  Is it really?  We'll consider it
					// a search for virtual event if there
					// aren't any binders to search through.
					int binderCount = ((null == binderIds) ? 0 : binderIds.size());
					switch (binderCount) {
					case 0:
						virtual = true;
						break;
						
					case 1:
						Object binderO = binderIds.get(0);
						if ((null != binderO) && (binderO instanceof String)) {
							virtual = ("none".equalsIgnoreCase((String)binderO));
						}
						break;
					}
				}

				// Is it a search for virtual events using a
				// defined filter?
				if (virtual && filtered) {
					// Yes!  Instead of searching the current
					// binder, which the filter should have
					// been setup for, we need to search the
					// entire tree.  Adjust the filter
					// accordingly.
					Element foldersListFilterTerm = (Element)baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@filterType='foldersList']");
					Element filterFolderId = (Element)baseFilter.getRootElement().selectSingleNode("//filterTerms/filterTerm[@filterType='foldersList']/filterFolderId");
					if ((null != foldersListFilterTerm) && (null != filterFolderId)) {
						foldersListFilterTerm.addAttribute("filterType", "ancestriesList");
						filterFolderId.setText(String.valueOf(bs.getWorkspaceModule().getTopWorkspace().getId()));
					}
				}

				// Search for the events that are calendar
				// entries.
				ModeType modeType = (virtual ? ModeType.VIRTUAL : ModeType.PHYSICAL); 
				if (calendarModeType.equals(ObjectKeys.CALENDAR_MODE_TYPE_MY_EVENTS)) {
					//This is a request for calendar events for the current user
					modeType = ModeType.MY_EVENTS;
				}
				Document searchFilter = EventHelper.buildSearchFilterDoc(baseFilter, request, modeType, binderIds, binder, SearchUtils.AssigneeType.CALENDAR);
				folderEntries = bs.getBinderModule().executeSearchQuery(searchFilter, Constants.SEARCH_MODE_NORMAL, options);
				entries = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
				
				// Are we searching for virtual events?
				if (virtual) {
					// Yes!  Search for the events that are
					// task entries...
					List taskIntervals = new ArrayList(1);
					taskIntervals.add(calendarViewRangeDates.getVisibleIntervalRaw());
			       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, taskIntervals);
			       	
					searchFilter = EventHelper.buildSearchFilterDoc(baseFilter, request, modeType, binderIds, binder, SearchUtils.AssigneeType.TASK);
					folderEntries = bs.getBinderModule().executeSearchQuery(searchFilter, Constants.SEARCH_MODE_NORMAL, options);
					List taskEntries = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
					int tasks = ((null == taskEntries) ? 0 : taskEntries.size());
					if (0 < tasks) {
						// ...and add them to the calendar sorted by end date
						// ...events we found above.
						Map<Integer, List> taskEntriesByDay = new HashMap<Integer, List>();
						for (int i = 0; i < tasks; i += 1) {
							Map taskEntry = (Map)taskEntries.get(i);
							String event0 = (String)taskEntry.get("_event0");
							if (event0 != null) {
								Date startDate = (Date)taskEntry.get(event0+"#LogicalStartDate");
								Date endDate = (Date)taskEntry.get(event0+"#LogicalEndDate");
								if (endDate != null) {
									startDate = endDate;
								}
								if (startDate != null) {
									Calendar cal = new GregorianCalendar();
									cal.setTime(startDate);
									Integer monthDay = cal.get(Calendar.DAY_OF_MONTH);
									if (!taskEntriesByDay.containsKey(monthDay)) {
										taskEntriesByDay.put(monthDay, new ArrayList());
									}
									List dayEntries = taskEntriesByDay.get(monthDay);
									dayEntries.add(taskEntry);
								}
							}
						}
						for (int i=1; i <= 31; i++) {
							if (taskEntriesByDay.containsKey(Integer.valueOf(i))) {
								List dayEntries = taskEntriesByDay.get(Integer.valueOf(i));
								for (Object te : dayEntries) {
									entries.add(te);
								}
							}
						}
					}
				}
			}
			
		} else {
			// A template.
			entries = new ArrayList();
		}
		
		return entries;
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
			Map<String,Object>model, String viewType, boolean showTrash) throws Exception {
		Map folderEntries = null;
		Long folderId = folder.getId();
		User user = RequestContextHolder.getRequestContext().getUser();
		List pinnedFolderEntries = new ArrayList();
		
		if (showTrash) {
			TrashHelper.buildTrashViewToolbar(model);
			folderEntries = TrashHelper.getTrashEntities(bs, model, folder, options);
		}
		
		else {
			if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
				folderEntries = bs.getFolderModule().getFullEntries(folderId, options);
				
				//Get the WebDAV URLs
				buildWebDAVURLs(bs, req, folderEntries, model, folder);
				
				//Get the list of all entries to build the archive list
				buildBlogBeans(bs, response, folder, options, model, folderEntries, viewType);
			} else {
				String strUserDisplayStyle = user.getDisplayStyle();
				if (strUserDisplayStyle == null) { strUserDisplayStyle = ""; }
				
				boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
				if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) && (!accessible_simple_ui ||
						!ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle))) {
					// do it with ajax
				} else if (viewType.equals(Definition.VIEW_STYLE_TASK) && (!accessible_simple_ui ||
						!ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle))) {
					folderEntries = TaskHelper.findTaskEntries(bs, req, (Binder) folder, model, options);
					model.put(WebKeys.TASK_CAN_MODIFY_LINKAGE, new Boolean(TaskHelper.canModifyTaskLinkage(req, bs, ((Binder) folder))));
					GwtUIHelper.setCommonRequestInfoData(req, bs, model);
				} else {
					folderEntries = bs.getFolderModule().getEntries(folderId, options);
				}
				if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
					buildWikiBeans(bs, req, response, folder, options, model, folderEntries);
					//Get the pages bean
					buildBlogPageBeans(bs, response, folder, model, viewType);
	
				}
				if (viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
					//Get the list of all entries to build the archive list
					buildBlogBeans(bs, response, folder, options, model, folderEntries, viewType);
				}
				if (viewType.equals(Definition.VIEW_STYLE_MILESTONE)) {
					//Get the list of all entries to build the archive list
					loadFolderStatisticsForPlacesAttributes(bs, response, folder, options, model, folderEntries);
				}			
				if (viewType.equals(Definition.VIEW_STYLE_DISCUSSION)) {
					Integer offset = (Integer) options.get(ObjectKeys.SEARCH_OFFSET);
					if (offset == null || offset == 0) {
						//See if there are any pinned entries (show on first page only)
						UserProperties userFolderProperties = 
							bs.getProfileModule().getUserProperties(user.getId(), folder.getId());
						Map properties = userFolderProperties.getProperties();
						Map pinnedEntriesMap = new HashMap();
						model.put(WebKeys.PINNED_ENTRIES, pinnedEntriesMap);
						String pinnedEntries = "";
						if ((null != properties) && properties.containsKey(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES)) {
							pinnedEntries = (String)properties.get(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES);
						}
						if (!pinnedEntries.equals("")) {
							if (pinnedEntries.lastIndexOf(",") == pinnedEntries.length()-1) 
								pinnedEntries = pinnedEntries.substring(0,pinnedEntries.length()-1);
							String[] peArray = pinnedEntries.split(",");
							List peSet = new ArrayList();
							for (int i = 0; i < peArray.length; i++) {
								String pe = peArray[i];
								if (!pe.equals("")) {
									peSet.add(Long.valueOf(pe));
								}
							}
							SortedSet<FolderEntry> pinnedFolderEntriesSet = bs.getFolderModule().getEntries(peSet);
							List pinnedFolderEntriesList = new ArrayList();
							for (FolderEntry entry : pinnedFolderEntriesSet) {
								//Make sure the entry is not deleted and is still in this folder
								if (!(entry.isPreDeleted()) && entry.getParentBinder().equals(folder)) {
									org.apache.lucene.document.Document indexDoc = 
										bs.getFolderModule().buildIndexDocumentFromEntry(entry.getParentBinder(), entry, null);
									pinnedFolderEntriesList.add(indexDoc);
									pinnedEntriesMap.put(entry.getId().toString(), entry);
								}
							}
							pinnedFolderEntries = SearchUtils.getSearchEntries(pinnedFolderEntriesList);
							bs.getFolderModule().getEntryPrincipals(pinnedFolderEntries);
						}
					}
				}
				// viewType == task is pure ajax solution (view AjaxController)
			}
		}
	
		model.putAll(getSearchAndPagingModels(folderEntries, options, showTrash));
		if (folderEntries != null) {
			List folderEntriesList = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
			if (!pinnedFolderEntries.isEmpty()) folderEntriesList.addAll(0, pinnedFolderEntries);
			model.put(WebKeys.FOLDER_ENTRIES, folderEntriesList);
		}
		
		if (!showTrash) {
			if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
					viewType.equals(Definition.VIEW_STYLE_TASK)) {
				// all in Ajax
			} else if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
				//This is a blog view, so get the extra blog beans
				getBlogEntries(bs, folder, folderEntries, model, req, response);
			}
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
		DefinitionHelper.buildMashupBeans(bs, folder, configDocument, model, req );
		
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
		String targetJSP;
		if (showTrash) {
			targetJSP = WebKeys.VIEW_LISTING_IFRAME;
		}
		else {
			targetJSP = BinderHelper.getViewListingJsp(bs, viewType);
		}
		return targetJSP;
	}
	
	protected static Map getSearchAndPagingModels(Map folderEntries, Map options, boolean showTrash) {
		return BinderHelper.getSearchAndPagingModels(folderEntries, options, showTrash);
	}	
	
	protected static String getTeamMembers(AllModulesInjected bs, Map formData, RenderRequest req, 
			RenderResponse response, Folder folder, Map options, 
			Map<String,Object>model, String viewType) throws PortletRequestBindingException {
		
		try {
			bs.getProfileModule().getProfileBinder(); //Check access to user list
			Collection<Principal> usersAndGroups = bs.getBinderModule().getTeamMembers(folder, false);
			SortedMap<String, User> teamUsers = new TreeMap();
			SortedMap<String, Group> teamGroups = new TreeMap();
			for (Principal p : usersAndGroups) {
				if (p instanceof User) {
					teamUsers.put(Utils.getUserTitle(p), (User)p);
				} else if (p instanceof Group) {
					teamGroups.put(p.getTitle(), (Group)p);
				}
			}
			model.put(WebKeys.TEAM_MEMBERS, teamUsers);
			model.put(WebKeys.TEAM_MEMBERS_COUNT, teamUsers.size());
			model.put(WebKeys.TEAM_MEMBER_GROUPS, teamGroups);
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
//			addEntryToolbar(bs, req, response, folder, entryToolbar, model, viewType);
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
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION);
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
//			buildBlogBeans(bs, response, folder, options, model, folderEntries, viewType);
		} else if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
			//Get the list of all entries to build the archive list
			String wikiHomePageId = (String)folder.getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE);
			FolderEntry wikiHomePage = null;
			if (Validator.isNotNull(wikiHomePageId)) {
				//Check if this is a valid page
				try {
					wikiHomePage = bs.getFolderModule().getEntry(folder.getId(), Long.valueOf(wikiHomePageId));
				} catch(Exception e) {
					wikiHomePageId = null;
				}
			}
			model.put(WebKeys.WIKI_HOMEPAGE_ENTRY_ID, wikiHomePageId);
			model.put(WebKeys.WIKI_HOMEPAGE_ENTRY, wikiHomePage);
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
			Folder folder, Map options, Map model, Map folderEntries, String viewType) {
        User user = RequestContextHolder.getRequestContext().getUser();
		//Get the pages bean
		buildBlogPageBeans(bs, response, folder, model, viewType);
		
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
        	field.addAttribute(Constants.FIELD_NAME_ATTRIBUTE,Constants.ENTRY_ANCESTRY);
        	Element child = field.addElement(Constants.FIELD_TERMS_ELEMENT);
        	Binder blogSetBinder = (Binder) model.get(WebKeys.BLOG_SET_BINDER);
        	if (blogSetBinder != null) {
        		child.setText(blogSetBinder.getId().toString());
        	} else {
        		child.setText(folder.getId().toString());
        	}
        	
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
		Map entriesMap = bs.getBinderModule().executeSearchQuery(searchFilter, Constants.SEARCH_MODE_NORMAL, options2);
		List entries = (List) entriesMap.get(ObjectKeys.SEARCH_ENTRIES);
		LinkedHashMap monthHits = new LinkedHashMap();
		Map folderHits = new HashMap();
		Map monthTitles = new HashMap();
		Map monthUrls = new HashMap();
		DateFormat df = DateFormat.getInstance();
    	SimpleDateFormat sf = (SimpleDateFormat)df;
		sf.setTimeZone(user.getTimeZone());
    	sf.applyPattern("yyyyMM");
		Iterator itEntries = entries.iterator();
		while (itEntries.hasNext()) {
			Map entry = (Map)itEntries.next();
			if (entry.containsKey(Constants.CREATION_DATE_FIELD)) {
				Date creationDate = (Date) entry.get(Constants.CREATION_DATE_FIELD);
				String yearMonth = sf.format(creationDate);
				if (!monthHits.containsKey(yearMonth)) {
					monthHits.put(yearMonth, new Integer(0));
					String year = yearMonth.substring(0, 4);
					String monthNumber = yearMonth.substring(4, 6);
					int m = Integer.valueOf(monthNumber).intValue() - 1;
					String args[] = new String[2];
					args[0] = NLT.get(monthNames[m%12]);
					args[1] = year;
					monthTitles.put(yearMonth,  NLT.get("calendar.monthYear", args));
					PortletURL url = response.createRenderURL();
					url.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
					url.setParameter(WebKeys.URL_YEAR_MONTH, yearMonth);
					monthUrls.put(yearMonth, url.toString());
				}
				int hitCount = (Integer) monthHits.get(yearMonth);
				monthHits.put(yearMonth, new Integer(++hitCount));
				//Keep track of the hits per folder
				String entryBinderId = (String)entry.get(Constants.BINDER_ID_FIELD);
				if (entryBinderId != null) {
					String monthFolder = yearMonth + "/" + entryBinderId;
					if (!folderHits.containsKey(monthFolder)) folderHits.put(monthFolder, new Integer(0));
					int f = (Integer) folderHits.get(monthFolder);
					folderHits.put(monthFolder, new Integer(++f));
				}
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
		model.put(WebKeys.BLOG_MONTH_FOLDER_HITS, folderHits);
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

	//Routine to build the beans for the blog pages list
	public static void buildBlogPageBeans(AllModulesInjected bs, RenderResponse response, 
			Folder folder, Map model, String viewType) {
		List blogPages = new ArrayList();
		Binder parentBinder = folder.getParentBinder();
		Binder topBinder = folder;
		while (parentBinder != null) {
			if (parentBinder.getDefinitionType() != null && !parentBinder.getDefinitionType().equals(Definition.FOLDER_VIEW) || 
					!viewType.equals(BinderHelper.getViewType(bs, parentBinder))) break;
			topBinder = parentBinder;
			parentBinder = parentBinder.getParentBinder();
		}
		//The top binder is a blog folder, so add it and its descendants
		//blogPages.add(topBinder);
		model.put(WebKeys.BLOG_SET_BINDER, topBinder);
				
		List folderIds = new ArrayList();
		folderIds.add(topBinder.getId().toString());
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER}))
			.add(in(Constants.ENTRY_ANCESTRY, folderIds));
		crit.addOrder(Order.asc(Constants.SORT_TITLE_FIELD));
		Map binderMap = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));

		List binderMapList = (List)binderMap.get(ObjectKeys.SEARCH_ENTRIES); 
		List binderIdList = new ArrayList();

      	for (Iterator iter=binderMapList.iterator(); iter.hasNext();) {
      		Map entryMap = (Map) iter.next();
      		binderIdList.add(new Long((String)entryMap.get(Constants.DOCID_FIELD)));
      	}
      	//Get sub-binder list including intermediate binders that may be inaccessible
      	SortedSet binderList = bs.getBinderModule().getBinders(binderIdList, Boolean.FALSE);
        for (Iterator iter=binderList.iterator(); iter.hasNext();) {
     		Binder b = (Binder)iter.next();
      		if (b.isDeleted()) continue;
      		if (b.getEntityType().equals(EntityIdentifier.EntityType.folder)) {
      			blogPages.add(b);
      		}
		}
		model.put(WebKeys.BLOG_PAGES, blogPages);
	}
	
	public static void buildWikiBeans(AllModulesInjected bs, RenderRequest request, RenderResponse response, Binder binder, 
			Map options, Map model, Map folderEntries) {
        User user = RequestContextHolder.getRequestContext().getUser();
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
		
		Boolean isWikiFolderList = PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_WIKI_FOLDER_LIST, false);
		model.put(WebKeys.WIKI_FOLDER_LIST, isWikiFolderList);

		String wikiHomePageId = (String)binder.getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE);
		String entryIdToBeShown = (String)model.get(WebKeys.ENTRY_ID_TO_BE_SHOWN);
		FolderEntry wikiHomePage = null;
		if (Validator.isNotNull(entryIdToBeShown)) {
			//We are looking to show a specific entry
			//Check if this is a valid page
			try {
				wikiHomePage = bs.getFolderModule().getEntry(binder.getId(), Long.valueOf(entryIdToBeShown));
			} catch(Exception e) {
				wikiHomePageId = null;
				wikiHomePage = null;
			}
		} else if (Validator.isNotNull(wikiHomePageId)) {
			//Check if this is a valid page
			try {
				wikiHomePage = bs.getFolderModule().getEntry(binder.getId(), Long.valueOf(wikiHomePageId));
				if ((null != wikiHomePage) && wikiHomePage.isPreDeleted()) {
					wikiHomePageId = null;
					wikiHomePage = null;
				}
			} catch(Exception e) {
				wikiHomePageId = null;
				wikiHomePage = null;
			}
		}
		model.put(WebKeys.WIKI_HOMEPAGE_ENTRY, wikiHomePage);
		model.put(WebKeys.WIKI_HOMEPAGE_ENTRY_ID, wikiHomePageId);
		if (wikiHomePage != null && !isWikiFolderList) {
			//Also set up the config info to show the entry
			
			
			Map entryMap = new HashMap();
			Map accessControlEntryMap = BinderHelper.getAccessControlEntityMapBean(model, wikiHomePage);
			model.put(WebKeys.WIKI_HOMEPAGE_ENTRY_MAP, entryMap);
			entryMap.put("entry", wikiHomePage);
			if (DefinitionHelper.getDefinition(wikiHomePage.getEntryDefDoc(), entryMap, "//item[@name='entryView']") == false) {
				//this will fill in the entryDef for the entry
				DefinitionHelper.getDefaultEntryView(wikiHomePage, entryMap, "//item[@name='entryView']");				
			}
			//See if this entry can have replies added
			entryMap.put(WebKeys.REPLY_BLOG_URL, "");
			if (bs.getFolderModule().testAccess(wikiHomePage, FolderOperation.addReply)) {
				accessControlEntryMap.put("addReply", new Boolean(true));
				Document defDoc = wikiHomePage.getEntryDefDoc();
				List replyStyles = DefinitionUtils.getPropertyValueList(defDoc.getRootElement(), "replyStyle");
				if (!replyStyles.isEmpty()) {
					String replyStyleId = (String)replyStyles.get(0);
					if (!replyStyleId.equals("")) {
						AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_REPLY);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, wikiHomePage.getParentFolder().getId().toString());
						adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, replyStyleId);
						adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, wikiHomePage.getId().toString());
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

			if (bs.getFolderModule().testAccess(wikiHomePage, FolderOperation.reserveEntry)) {
				reserveAccessCheck = true;
			}
			if (bs.getFolderModule().testAccess(wikiHomePage, FolderOperation.overrideReserveEntry)) {
				isUserBinderAdministrator = true;
			}
			
			HistoryStamp historyStamp = wikiHomePage.getReservation();
			if (historyStamp != null) isEntryReserved = true;

			if (isEntryReserved) {
				Principal lockedByUser = historyStamp.getPrincipal();
				if (lockedByUser.getId().equals(user.getId())) {
					isLockedByAndLoginUserSame = true;
				}
			}
			if (bs.getFolderModule().testAccess(wikiHomePage, FolderOperation.modifyEntry)) {
				if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
				} else {
					accessControlEntryMap.put("modifyEntry", new Boolean(true));
				}
			}

			//entryMap.put(WebKeys.COMMUNITY_TAGS, publicTags.get(wikiHomePage.getId()));
			//entryMap.put(WebKeys.PERSONAL_TAGS, privateTags.get(wikiHomePage.getId()));
		}
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

	/**
	 * Returns the index of the entry definition to use as the default
	 * for a folder.
	 *
	 * @param		userId - The ID of the user we running as.
	 * @param		profileModule - The user's ProfileModule.
	 * @param		folder - The Binder whose default entry definition is being queried.
	 * @return		Index in the folder's entry definitions for the entry type that most closely matches the folder's default view.
	 */
	public static int getDefaultFolderEntryDefinitionIndex(Long userId, ProfileModule profileModule, Binder folder) {
		return getDefaultFolderEntryDefinitionIndex(userId, profileModule, folder, folder.getEntryDefinitions());
	}

	/**
	 * Returns the index of the entry definition to use as the default
	 * for a folder.
	 * 
	 * @param		userId - The ID of the user we running as.
	 * @param		profileModule - The user's ProfileModule.
	 * @param		folder - The Binder whose default entry definition is being queried.
	 * @param		folderEntryDefinitions - Entry definitions for folder.
	 * @return		Index in the folder's entry definitions for the entry type that most closely matches the folder's default view.
	 */
	public static int getDefaultFolderEntryDefinitionIndex(Long userId, ProfileModule profileModule, Binder folder, List folderEntryDefinitions) {
   		// Does the user have properties defined for the folder?
		int	reply = 0;
		UserProperties userFolderProperties = profileModule.getUserProperties(userId, folder.getId());
		if (null != userFolderProperties) {
			// Yes!  Do the folder properties contain a display
			// definition for the folder?
			String folderDisplayDefId = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));
			if (Validator.isNull(folderDisplayDefId)) {
				folderDisplayDefId = folder.getDefaultViewDef().getId();
			}
			if (Validator.isNotNull(folderDisplayDefId)) {
				// Yes!  Scan the views defined on the folder.
				String folderViewName = null;
				List folderViewDefinitions = folder.getViewDefinitions();
				int folderViewDefs = ((null == folderViewDefinitions) ? 0 : folderViewDefinitions.size());
				for (int i = 0; i < folderViewDefs; i += 1) {
					// Is this the view the user's got selected to
					// display this folder with?
					Definition folderViewDefinition = ((Definition) folderViewDefinitions.get(i));
					if (folderDisplayDefId.equals(folderViewDefinition.getId())) {
						// Yes!  Save its name and quit looking.
						folderViewName = folderViewDefinition.getName();
						break;
					}
				}
				
				// Do we have the name of a view the user has selected
				// for this folder?
				if (Validator.isNotNull(folderViewName)) {
					// Yes!  Does it end with 'Folder'?
		   			int	folderPart = folderViewName.indexOf("Folder");
		   			if (0 < folderPart) {
		   				// Yes!  Scan the folder's entry definitions.
		   				folderViewName = folderViewName.substring(0, folderPart);
						int folderEntryDefs = folderEntryDefinitions.size();
						for (int i = 0; i < folderEntryDefs; i += 1) {
			   				// Does this entry definition's name start the
							// same as the view's name?
							Definition folderEntryDefinition = ((Definition) folderEntryDefinitions.get(i));
							String	folderEntryDefName = folderEntryDefinition.getName();
							if (folderEntryDefName.startsWith(folderViewName)) {
								// Yes!  That's the default that we're
								// looking for.  Return its index.
								reply = i;
								break;
							}
						}
		   			}
				}
			}
		}

		return reply;
	}
	
	protected static void addEntryToolbar(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Binder folder, Toolbar entryToolbar, Map model, String viewType) {
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		int defaultEntryDefs = ((null == defaultEntryDefinitions) ? 0 : defaultEntryDefinitions.size());
		model.put(WebKeys.URL_BINDER_ENTRY_DEFS, String.valueOf( defaultEntryDefs ));
		
		if((!(folder.isMirrored() && folder.getResourceDriverName() == null)) && !folder.isMirroredAndReadOnly()) {
			if (defaultEntryDefs > 1) {
				SortedMap addEntryUrls = new TreeMap();
				model.put(WebKeys.URL_ADD_ENTRIES, addEntryUrls);
				int count = 1;
				int	defaultEntryDefIndex = getDefaultFolderEntryDefinitionIndex(
					RequestContextHolder.getRequestContext().getUser().getId(),
					bs.getProfileModule(),
					folder,
					defaultEntryDefinitions);
				Map dropdownQualifiers = new HashMap();
				dropdownQualifiers.put("highlight", new Boolean(true));
				String	entryAdd = NLT.get("toolbar.new");
				entryToolbar.addToolbarMenu("1_add", entryAdd, "", dropdownQualifiers);
				model.put(WebKeys.URL_BINDER_ENTRY_ADD, entryAdd);
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
					if (i == defaultEntryDefIndex) {
						adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
						adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
						model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());
					}
					addEntryUrls.put(title, adapterUrl.toString());
				}
			} else if (defaultEntryDefs != 0) {
				// Only one option
				Definition def = (Definition) defaultEntryDefinitions.get(0);
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				String[] nltArgs = new String[] {NLT.getDef(def.getTitle())};
				String title = NLT.get("toolbar.new_with_arg", nltArgs);
				Map qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				qualifiers.put("highlight", new Boolean(true));
				entryToolbar.addToolbarMenu("1_add", title, adapterUrl.toString(), qualifiers);
				model.put(WebKeys.URL_BINDER_ENTRY_ADD, title);
				model.put(WebKeys.URL_ADD_ENTRY, adapterUrl.toString());
				
				adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
				adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
				model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());
			}
		}
		if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
			if (bs.getBinderModule().testAccess(folder, BinderOperation.addFolder)) {
				TemplateBinder blogTemplate = bs.getTemplateModule().getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_BLOG);
				if (blogTemplate != null) {
					Map qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
		        	Binder blogSetBinder = (Binder) model.get(WebKeys.BLOG_SET_BINDER);
		        	if (blogSetBinder != null) {
		        		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, blogSetBinder.getId().toString());
		        	} else {
		        		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
		        	}
					adapterUrl.setParameter(WebKeys.URL_TEMPLATE_NAME, ObjectKeys.DEFAULT_TEMPLATE_NAME_BLOG);
					entryToolbar.addToolbarMenu("1_add_folder", NLT.get("toolbar.menu.add_blog_folder"), 
							adapterUrl.toString(), qualifiers);
				}
			}
		}		
		if (viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
			if (bs.getBinderModule().testAccess(folder, BinderOperation.addFolder)) {
				TemplateBinder photoTemplate = bs.getTemplateModule().getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_PHOTO);
				if (photoTemplate != null) {
					Map qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
		        	Binder blogSetBinder = (Binder) model.get(WebKeys.BLOG_SET_BINDER);
		        	if (blogSetBinder != null) {
		        		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, blogSetBinder.getId().toString());
		        	} else {
		        		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
		        	}
					adapterUrl.setParameter(WebKeys.URL_TEMPLATE_NAME, ObjectKeys.DEFAULT_TEMPLATE_NAME_PHOTO);
					entryToolbar.addToolbarMenu("1_add_folder", NLT.get("toolbar.menu.add_photo_album_folder"), 
							adapterUrl.toString(), qualifiers);
				}
			}
		}		
		if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
			if (bs.getBinderModule().testAccess(folder, BinderOperation.addFolder)) {
				TemplateBinder wikiTemplate = bs.getTemplateModule().getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_WIKI);
				if (wikiTemplate != null) {
					Map qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
		        	Binder blogSetBinder = (Binder) model.get(WebKeys.BLOG_SET_BINDER);
		        	if (blogSetBinder != null) {
		        		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, blogSetBinder.getId().toString());
		        	} else {
		        		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
		        	}
					adapterUrl.setParameter(WebKeys.URL_TEMPLATE_NAME, ObjectKeys.DEFAULT_TEMPLATE_NAME_WIKI);
					entryToolbar.addToolbarMenu("1_add_folder", NLT.get("toolbar.menu.add_wiki_folder"), 
							adapterUrl.toString(), qualifiers);
				}
			}
		}
		if ((viewType.equals(Definition.VIEW_STYLE_DISCUSSION) || viewType.equals(Definition.VIEW_STYLE_TABLE) || 
				viewType.equals(Definition.VIEW_STYLE_FILE)) && !folder.isMirrored()) {
			//Add the Delete and Purge buttons
			if (bs.getBinderModule().testAccess(folder, BinderOperation.deleteEntries)) {
				Map qualifiers = new HashMap();
				String onClickPhrase = "ss_deleteSelectedEntries('delete');return false;";
				qualifiers.put("title", NLT.get("file.command.deleteEntries"));
				qualifiers.put("onClick", onClickPhrase);
				qualifiers.put("linkclass", "ss_toolbarDeleteBtnDisabled");
				qualifiers.put("textId", "ss_toolbarDeleteBtn");
				entryToolbar.addToolbarMenu("1_deleteSelected", NLT.get("toolbar.delete"), 
						"#", qualifiers);
				qualifiers = new HashMap();
				onClickPhrase = "ss_deleteSelectedEntries('purge');return false;";
				qualifiers.put("title", NLT.get("file.command.deleteEntriesPurge"));
				qualifiers.put("onClick", onClickPhrase);
				qualifiers.put("linkclass", "ss_toolbarDeleteBtnDisabled");
				qualifiers.put("textId", "ss_toolbarPurgeBtn");
				entryToolbar.addToolbarMenu("1_purgeSelected", NLT.get("toolbar.purge"), 
						"#", qualifiers);
			}
		}
	}
	protected static void buildFolderToolbars(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Folder folder, String forumId, Map model, String viewType) {
        User user = RequestContextHolder.getRequestContext().getUser();
        String userDisplayStyle = user.getCurrentDisplayStyle();
		
        String topFolderId = folder.getId().toString();
		if (!folder.isTop()) {
			topFolderId = folder.getTopFolder().getId().toString();
		}
        
		//Build the toolbar arrays
		Toolbar folderToolbar = new Toolbar();
		Toolbar entryToolbar = new Toolbar();
		Toolbar folderViewsToolbar = new Toolbar();
		Toolbar folderActionsToolbar = new Toolbar();
		Toolbar calendarImportToolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		Toolbar footerToolbar = new Toolbar();
		Toolbar whatsNewToolbar = new Toolbar();
		Toolbar emailSubscriptionToolbar = new Toolbar();
		Toolbar trashToolbar = new Toolbar();
		Toolbar gwtMiscToolbar = new Toolbar();
		Toolbar gwtUIToolbar = new Toolbar();
		
		boolean accessible_simple_ui = SPropsUtil.getBoolean("accessibility.simple_ui", false);
		boolean isAppletSupported = SsfsUtil.supportApplets(request);
        boolean isAccessible = false;
		String displayStyle = user.getDisplayStyle();
		if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) && 
				accessible_simple_ui && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			isAccessible = true;
		}
		
		AdaptedPortletURL adapterUrl;
		Map qualifiers;
		PortletURL url;
		if (bs.getFolderModule().testAccess(folder, FolderOperation.addEntry)) {				
			addEntryToolbar(bs, request, response, folder, entryToolbar, model, viewType);
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
			model.put(WebKeys.URL_ADD_FOLDER, url.toString());
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
			url = response.createActionURL();
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
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_BINDER_REPORTS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.reports"), url, qualifiers);
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
		
		if (bs.getBinderModule().testAccess(folder, BinderOperation.manageConfiguration)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_TEMPLATES);
			url.setParameter(WebKeys.URL_BINDER_PARENT_ID, forumId);
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("administration.template_builder_local"), url, qualifiers);
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
	
		if(LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder")) {
			//Synchronize mirrored folder
			if(folder.isMirrored() &&
					folder.getResourceDriverName() != null &&
					bs.getFolderModule().testAccess(folder, FolderOperation.fullSynchronize)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("showSpinner", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SYNCHRONIZE_MIRRORED_FOLDER);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
				folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.synchronize_mirrored_folder.manual"), url, qualifiers);
			}
			if(folder.isMirrored() &&
					folder.getResourceDriverName() != null &&
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
		
		//Export / Import
		if (bs.getBinderModule().testAccess(folder, BinderOperation.export)) {
			
			qualifiers = new HashMap();
			qualifiers.put("popup", new Boolean(true));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_EXPORT_IMPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_SHOW_MENU, "true");
			folderToolbar.addToolbarMenuItem("1_administration", "", 
					NLT.get("toolbar.menu.export_import_folder"), url, qualifiers);
			//adminMenuCreated=true;	
		}

		//set email
		if (bs.getBinderModule().testAccess(folder, BinderOperation.manageMail) &&
				(folder.isTop() || bs.getAdminModule().getMailConfig().isPostingEnabled()))  {
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
			url.setParameter(WebKeys.URL_WORKAREA_ID, folder.getWorkAreaId().toString());
			url.setParameter(WebKeys.URL_WORKAREA_TYPE, folder.getWorkAreaType());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.accessControl"), url, qualifiers);
		}

		//The "Subscriptions" menu
		if (!user.isShared()) {
			qualifiers = new HashMap();
			qualifiers.put("title", NLT.get("toolbar.menu.title.emailSubscriptions"));
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageSubscriptionsMenu");
		
			Subscription sub = bs.getBinderModule().getSubscription(folder);
			qualifiers = new HashMap();
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", false);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, topFolderId);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SUBSCRIBE);			
			adapterUrl.setParameter("rn", "ss_randomNumberPlaceholder");			
			qualifiers.put("onClick", "ss_createPopupDiv(this, 'ss_subscription_menu');return false;");
			if (sub == null) {
				emailSubscriptionToolbar.addToolbarMenu("1_email", NLT.get("toolbar.menu.subscribeToFolder"), adapterUrl.toString(), qualifiers);	
				model.put(WebKeys.TOOLBAR_SUBSCRIBE_EMAIL, adapterUrl.toString());
			} else {
				emailSubscriptionToolbar.addToolbarMenu("1_email", NLT.get("toolbar.menu.subscriptionToFolder"), adapterUrl.toString(), qualifiers);
				model.put(WebKeys.TOOLBAR_SUBSCRIBE_EMAIL, adapterUrl.toString());
			}
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
			adapterUrl.setParameter(WebKeys.URL_WORKAREA_ID, folder.getWorkAreaId().toString());
			adapterUrl.setParameter(WebKeys.URL_WORKAREA_TYPE, folder.getWorkAreaType());
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_ACCESS);
			folderToolbar.addToolbarMenu("2_whoHasAccess", 
					NLT.get("toolbar.whoHasAccess"), adapterUrl.toString(), qualifiers);
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
			qualifiers.put("popupWidth", "600");
			qualifiers.put("popupHeight", "600");
			//folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.addMember"), adapterUrl.toString(), qualifiers);
			model.put(WebKeys.TOOLBAR_TEAM_ADD_URL, adapterUrl.toString());
		}
		//View
		if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SHOW_TEAM_MEMBERS);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			//folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.view"), url);
			model.put(WebKeys.TOOLBAR_TEAM_VIEW_URL, url.toString());
		}
			
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
		if (bs.getConferencingModule().isEnabled() && 
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
		if (viewType.equals(Definition.VIEW_STYLE_BLOG)|| 
				viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
			//Add a way to set the sorting
			UserProperties userFolderProperties = (UserProperties)model.get(WebKeys.USER_FOLDER_PROPERTIES_OBJ);
			String searchSortBy = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY);
			if (searchSortBy == null) searchSortBy = "";
			entryToolbar.addToolbarMenu("2_display_styles", NLT.get("toolbar.folder_sortBy"));
			String[] sortOptions = new String[] {"number", "title", "state", "author", "activity"};
			if (viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
				sortOptions = new String[] {"number", "title",  "activity"};
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
				String nltTag = "folder.column.Number";
				if (viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
					nltTag = "folder.column.CreationDate";
				}
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get(nltTag), url, qualifiers);
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
		
		if (!viewType.equals(Definition.VIEW_STYLE_MINIBLOG) && isAppletSupported && !folder.isMirroredAndReadOnly() && 
				!(folder.isMirrored() && folder.getResourceDriverName() == null) &&
				bs.getFolderModule().testAccess(folder, FolderOperation.addEntry) && 
				!isAccessible) {
			qualifiers = new HashMap();
			if (!folder.isMirrored() && bs.getProfileModule().isDiskQuotaExceeded()) {
				qualifiers.put("onClick", "alert('" + NLT.get("quota.diskQuotaExceeded").replaceAll("'", "''") + "'); return false;");
			} else if (!folder.isMirrored() && !bs.getBinderModule().isBinderDiskQuotaOk((Binder)folder, 0L)) {
				qualifiers.put("onClick", "alert('" + NLT.get("quota.diskBinderQuotaExceeded").replaceAll("'", "''") + "'); return false;");
			} else {
				String msg = "ss_showFolderAddAttachmentDropbox('" + response.getNamespace() + "', '" + folder.getId() + "','" + Boolean.toString(folder.isLibrary()) + "');";
				if (bs.getProfileModule().isDiskQuotaHighWaterMarkExceeded() && !folder.isMirrored()) {
					Double quotaLeft = (Double.valueOf(bs.getProfileModule().getMaxUserQuota()) - 
							Double.valueOf(user.getDiskSpaceUsed()))/1048576;
					Locale.setDefault(user.getLocale());
					DecimalFormat form = new DecimalFormat("0.00");
					String[] args = new String[] {form.format(quotaLeft)};
					msg += "alert('" + NLT.get("quota.nearLimit", args).replaceAll("'", "''") + "');";
				}
				msg += "return false;";
				qualifiers.put("onClick", msg);
			}
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
			footerToolbar.addToolbarMenuItem("viewaswebdav", "folderviews", NLT.get("toolbar.menu.viewASWebDav"), webdavUrl, qualifiers);
		}
		
		//Folder action menu
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) || !accessible_simple_ui) {
			//Folder action menu
			//Build the standard toolbar
			BinderHelper.buildFolderActionsToolbar(bs, request, response, folderActionsToolbar, forumId);
		}
		
		//Calendar import menu
		if ((!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) || !accessible_simple_ui) && 
				(viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
						viewType.equals(Definition.VIEW_STYLE_TASK)) &&
				bs.getFolderModule().testAccess(folder, FolderOperation.addEntry)) {
			
			String titleFromFile = NLT.get("calendar.import.window.title.fromFile").replaceAll("'", "\\\\'");
			String titleByURL = NLT.get("calendar.import.window.title.byURL").replaceAll("'", "\\\\'");
			String legendFromFile = NLT.get("calendar.import.window.legend.fromFile").replaceAll("'", "\\\\'");
			String legendByURL = NLT.get("calendar.import.window.legend.byURL").replaceAll("'", "\\\\'");
			String btnFromFile = NLT.get("calendar.import.window.upload.fromFile").replaceAll("'", "\\\\'");
			String btnByURL = NLT.get("calendar.import.window.upload.byURL").replaceAll("'", "\\\\'");
			String optionTitle = NLT.get("toolbar.menu.calendarImport").replaceAll("'", "\\'");
			String importFromFile = NLT.get("toolbar.menu.calendarImport.fromFile");
			String importByURL = NLT.get("toolbar.menu.calendarImport.byURL");
			if (viewType.equals(Definition.VIEW_STYLE_TASK)) {
				titleFromFile = NLT.get("task.import.window.title.fromFile").replaceAll("'", "\\\\'");
				titleByURL = NLT.get("task.import.window.title.byURL").replaceAll("'", "\\\\'");
				legendFromFile = NLT.get("task.import.window.legend.fromFile").replaceAll("'", "\\\\'");
				legendByURL = NLT.get("task.import.window.legend.byURL").replaceAll("'", "\\\\'");
				btnFromFile = NLT.get("task.import.window.upload.fromFile").replaceAll("'", "\\\\'");
				btnByURL = NLT.get("task.import.window.upload.byURL").replaceAll("'", "\\\\'");
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
		String permaLink = PermaLinkUtil.getPermalink(request, folder);
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showPermalink(this);return false;");
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.folderPermalink"), 
				permaLink, qualifiers);
		
		String[] contributorIds = collectContributorIds((List)model.get(WebKeys.FOLDER_ENTRIES));
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		//   iCalendar
		if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
				viewType.equals(Definition.VIEW_STYLE_TASK)) {
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_showPermalink(this);return false;");
			footerToolbar.addToolbarMenu("iCalendar", NLT.get("toolbar.menu.iCalendar"), org.kablink.teaming.ical.util.UrlUtil.getICalURL(request, forumId, null), qualifiers);
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
			model.put(WebKeys.TOOLBAR_CLIPBOARD_IDS_AS_JS_STRING, contributorIdsAsJSString);
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

		// trash
		TrashHelper.buildTrashToolbar(user, folder, model, qualifiers, trashToolbar);

		// start meeting
		if (bs.getConferencingModule().isEnabled() && 
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
			// To work around a bug in MS that sometimes causes a webdav URL on XP to be treated as a SMB resource,
			// we add trailing '#' charactger to the WebDAV URL used in this particular capacity. See bug#559341.
			// Comment added later: Change the default in a way that undo the above workaround. This is to 
			// force Windows client to use WebDAV redirector instead of Office WebDAV.
			String webdavSuffix = SPropsUtil.getString("webdav.folder.url.suffix", "");
			if(webdavSuffix.length() > 0)
				webdavUrl = webdavUrl + "/" + webdavSuffix;
			qualifiers.put("folder", webdavUrl);
			footerToolbar.addToolbarMenu("webdavUrl", NLT.get("toolbar.menu.webdavUrl"), webdavUrl, qualifiers);
		}
		
		//Permalink urls, rss url, webdav url, email addresses
		model.put(WebKeys.PERMALINK, permaLink);
		model.put(WebKeys.MOBILE_URL, SsfsUtil.getMobileUrl(request));		
		//  WebDav Permalink
		if (!webdavUrl.equals("")) {
			model.put(WebKeys.TOOLBAR_URL_WEBDAV, webdavUrl);
		}
		//  RSS link 
		String rssUrl = UrlUtil.getFeedURL(request, topFolderId);
		if (rssUrl != null && !rssUrl.equals("")) {
			model.put(WebKeys.TOOLBAR_URL_SUBSCRIBE_RSS, rssUrl);
		}
		String atomUrl = UrlUtil.getAtomURL(request, topFolderId);
		if (atomUrl != null && !atomUrl.equals("")) {
			model.put(WebKeys.TOOLBAR_URL_SUBSCRIBE_ATOM, atomUrl);
		}
		//  Build the simple URL beans
		BinderHelper.buildSimpleUrlBeans(bs,  request, folder, model);
		//   iCalendar
		if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
				viewType.equals(Definition.VIEW_STYLE_TASK)) {
			model.put(WebKeys.TOOLBAR_URL_ICAL, org.kablink.teaming.ical.util.UrlUtil.getICalURL(request, forumId, null));
		}

		
		//Set up the whatsNewToolbar links
		//What's new
        //What's new is not available to the guest user or on a Wiki.
        if ((!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) && (!BinderHelper.isBinderWiki(folder))) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_TYPE, "whatsNew");
			adapterUrl.setParameter(WebKeys.URL_PAGE, "0");
			adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
			qualifiers = new HashMap();
			qualifiers.put("title", NLT.get("toolbar.menu.title.whatsNewInFolder"));
			qualifiers.put("onClick", "ss_showWhatsNewPage(this, '"+forumId+"', 'whatsNew', '0', '', 'ss_whatsNewDiv', '"+response.getNamespace()+"');return false;");
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.whatsNew");
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
			qualifiers.put("title", NLT.get("toolbar.menu.title.whatsUnreadInFolder"));
			qualifiers.put("onClick", "ss_showWhatsNewPage(this, '"+forumId+"', 'unseen', '0', '', 'ss_whatsNewDiv', '"+response.getNamespace()+"');return false;");
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.whatsUnread");
			whatsNewToolbar.addToolbarMenu("unseen", NLT.get("toolbar.menu.whatsUnseen"), 
					adapterUrl.toString(), qualifiers);
        }

		// GWT UI.  Note that these need to be last in the toolbar
		// building sequence because they access things in the
		// model to construct toolbars specific to the GWT UI.
		GwtUIHelper.buildGwtMiscToolbar(bs, request, folder, model, gwtMiscToolbar);

		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR,  folderToolbar.getToolbar());
		model.put(WebKeys.ENTRY_TOOLBAR,  entryToolbar.getToolbar());
		model.put(WebKeys.FOLDER_VIEWS_TOOLBAR,  folderViewsToolbar.getToolbar());
		model.put(WebKeys.FOLDER_ACTIONS_TOOLBAR,  folderActionsToolbar.getToolbar());
		model.put(WebKeys.CALENDAR_IMPORT_TOOLBAR,  calendarImportToolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
		model.put(WebKeys.WHATS_NEW_TOOLBAR,  whatsNewToolbar.getToolbar());
		model.put(WebKeys.EMAIL_SUBSCRIPTION_TOOLBAR,  emailSubscriptionToolbar.getToolbar());
		model.put(WebKeys.TRASH_TOOLBAR,  trashToolbar.getToolbar());
		model.put(WebKeys.GWT_MISC_TOOLBAR,  gwtMiscToolbar.getToolbar());
		model.put(WebKeys.GWT_UI_TOOLBAR,  gwtUIToolbar.getToolbar());
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
			if (DefinitionHelper.getDefinition(entry.getEntryDefDoc(), entryMap, "//item[@name='entryBlogView']") == false) {
				//this will fill it the entryDef for the entry
				DefinitionHelper.getDefaultEntryView(entry, entryMap, "//item[@name='entryBlogView']");				
			}
			//See if this entry can have replies added
			entryMap.put(WebKeys.REPLY_BLOG_URL, "");
			if (bs.getFolderModule().testAccess(entry, FolderOperation.addReply)) {
				accessControlEntryMap.put("addReply", new Boolean(true));
				Document defDoc = entry.getEntryDefDoc();
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

	/**
	 * Returns the current folder mode from the user properties.
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * 
	 * @return
	 */
	public static ModeType getFolderModeType(AllModulesInjected bs, Long userId, Long binderId) {
		UserProperties userProps = bs.getProfileModule().getUserProperties(userId, binderId);
		return ((ModeType) userProps.getProperty(WebKeys.FOLDER_MODE_PREF));
	}

	/**
	 * Returns true if a folder holds events that user is currently
	 * viewing in 'Virtual' mode and false otherwise.
	 * 
	 * @param bs
	 * @param user
	 * @param viewType
	 * @param folderId
	 * 
	 * @return
	 */
	public static boolean isVirtualEventFolder(AllModulesInjected bs, User user, String viewType, Long folderId) {
		boolean reply;
		if ((MiscUtil.hasString(viewType) &&
				(viewType.equals(Definition.VIEW_STYLE_TASK) || viewType.equals(Definition.VIEW_STYLE_CALENDAR)))) {
			ModeType mode = ListFolderHelper.getFolderModeType(bs, user.getId(), folderId);
			reply = (ModeType.VIRTUAL == mode);
		}
		else {
			reply = false;
		}
		return reply;
	}
	
	/**
	 * Saves given folder mode in session or default mode if given is
	 * <code>null</code> or unknown.
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * @param modeType
	 * 
	 * @return
	 */
	public static ModeType setFolderModeType(AllModulesInjected bs, Long userId, Long binderId, ModeType modeType) {
		if (null == modeType) {			
			modeType = getFolderModeType(bs, userId, binderId);
			if (modeType == null) {
				modeType = MODE_TYPE_DEFAULT;
			}
		}
		bs.getProfileModule().setUserProperty(userId, binderId, WebKeys.FOLDER_MODE_PREF, modeType);
		return modeType;
	}
	
	/**
	 * Returns a String[] of the IDs of the contributors to an entry.
	 * 
	 * @param entry
	 * 
	 * @return
	 */
	public static String[] collectContributorIds(FolderEntry entry) {		
		Set principals = new HashSet();
		collectCreatorAndMoficationIdsRecursive(entry, principals);
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}

	/*
	 * Recursively collects the contributor Principals to an entry into
	 * the given set.
	 */
	private static void collectCreatorAndMoficationIdsRecursive(FolderEntry entry, Set principals) {		
		principals.add(entry.getCreation().getPrincipal().getId().toString());
		principals.add(entry.getModification().getPrincipal().getId().toString());
		Iterator repliesIt = entry.getReplies().iterator();
		while (repliesIt.hasNext()) {
			collectCreatorAndMoficationIdsRecursive((FolderEntry)repliesIt.next(), principals);
		}
	}
}
