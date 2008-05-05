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
package com.sitescape.team.portlet.forum;

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

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.CalendarViewRangeDates;
import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.context.request.RequestContextHolder;
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
import com.sitescape.team.module.admin.AdminModule.AdminOperation;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.module.folder.index.IndexUtils;
import com.sitescape.team.module.license.LicenseChecker;
import com.sitescape.team.module.rss.util.UrlUtil;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.ssfs.util.SsfsUtil;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.TagUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.portlet.SAbstractController;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.Clipboard;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.team.web.util.PortletRequestUtils;
import com.sitescape.team.web.util.Tabs;
import com.sitescape.team.web.util.Toolbar;
import com.sitescape.team.web.util.WebHelper;
import com.sitescape.util.Validator;

/**
 * @author Peter Hurley
 *
 */
public class ListFolderController extends  SAbstractController {
	
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

	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) throws Exception {
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long binderId= PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		
		if (op.equals(WebKeys.OPERATION_SET_DISPLAY_STYLE)) {
			Map<String,Object> updates = new HashMap<String,Object>();
			updates.put(ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			getProfileModule().modifyEntry(user.getParentBinder().getId(), user.getId(), new MapInputData(updates));
		
		} else if (op.equals(WebKeys.OPERATION_SET_DISPLAY_DEFINITION)) {
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
			
		} else if (op.equals(WebKeys.OPERATION_SELECT_FILTER)) {
			getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_USER_FILTER, 
					PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION_SELECT_FILTER,""));
		
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_COLUMNS)) {
			if (formData.containsKey("okBtn")) {
				Map columns = new LinkedHashMap();
				String[] columnNames = new String[] {"number", "title", "comments", "size", "download", "html", "state", "author", "date"};
				for (int i = 0; i < columnNames.length; i++) {
					columns.put(columnNames[i], PortletRequestUtils.getStringParameter(request, columnNames[i], ""));
				}
				Iterator itFormData = formData.entrySet().iterator();
				while (itFormData.hasNext()) {
					Map.Entry me = (Map.Entry) itFormData.next();
					if (me.getKey().toString().startsWith("customCol_", 0)) {
						String colName = me.getKey().toString().substring(10, me.getKey().toString().length());
						columns.put(colName, "on");
					}
				}
				getProfileModule().setUserProperty(user.getId(), binderId, 
						ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS, columns);
				
				//See if this request was to set the folder default
				if (formData.containsKey("setFolderDefaultColumns")) {
					Binder binder = getBinderModule().getBinder(binderId);
					if (getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
						binder.setProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS, columns);
					}
				}
				
				//Reset the column positions to the default
			   	getProfileModule().setUserProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_COLUMN_POSITIONS, "");
			} else if (formData.containsKey("defaultBtn")) {
				getProfileModule().setUserProperty(user.getId(), binderId, 
						ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS, null);
				//Reset the column positions to the default
			   	getProfileModule().setUserProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_COLUMN_POSITIONS, "");
				//Reset the Sort Order information in the User Properties
				getProfileModule().setUserProperty(user.getId(), Long.valueOf(binderId), ObjectKeys.SEARCH_SORT_BY, "");
				getProfileModule().setUserProperty(user.getId(), Long.valueOf(binderId), ObjectKeys.SEARCH_SORT_DESCEND, "");
			}
		} else if (op.equals(WebKeys.OPERATION_SUBSCRIBE)) {
			Integer style = PortletRequestUtils.getIntParameter(request, "notifyType");
			String entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			if (entryId.equals("")) {
				entryId= PortletRequestUtils.getStringParameter(request, WebKeys.URL_SUBSCRIBE_ENTRY_ID, "");
			}
			if (style != null) {
				if (entryId.equals("")) {
					if (style.intValue() == -1) getBinderModule().deleteSubscription(binderId);
					else getBinderModule().addSubscription(binderId, style.intValue());
				} else {
					if (style.intValue() == -1) getFolderModule().deleteSubscription(binderId, Long.valueOf(entryId));
					else getFolderModule().addSubscription(binderId, Long.valueOf(entryId), style.intValue());
				}
			}
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO)) {
			//Saves the folder sort information

			String folderSortBy = PortletRequestUtils.getStringParameter(request, WebKeys.FOLDER_SORT_BY, "");
			String folderSortDescend = PortletRequestUtils.getStringParameter(request, WebKeys.FOLDER_SORT_DESCEND, "");
			
			//Saving the Sort Order information in the User Properties
			getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.SEARCH_SORT_BY, folderSortBy);
			getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.SEARCH_SORT_DESCEND, folderSortDescend);

			//Saving the Sort Order information in the Tab - Reason for doing it here is because if the Tab Sort Order has
			//already been set and if the user changes the sort order, you want the new Sort Order to take precendence over the
			//sort order set in the tab. So setting the tab sort order here will take care of the correct sort order being chosen 
			Tabs tabs = new Tabs(request);
			Map tab = tabs.getTab(tabs.getCurrentTab());
			tab.put(Tabs.SORTBY, new String(folderSortBy));
			tab.put(Tabs.SORTDESCEND, new String(folderSortDescend));
			//When the column is sorted, the page number need not be retained, the user can be taken to page number 1
			tab.put(Tabs.PAGE, new Integer(0));
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
			//Saving the Sort Order information in the User Properties
			//Changing the user folder paging information from folder/binder level to the user level 
			//getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
			getProfileModule().setUserProperty(user.getId(), ObjectKeys.PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
		} else if (op.equals(WebKeys.OPERATION_CHANGE_ENTRIES_ON_PAGE)) {
			//Set the id of the wiki homepage
			Long entryId= PortletRequestUtils.getRequiredLongParameter(request, WebKeys.ENTRY_ID);
			//TODO finish this code
		}

		response.setRenderParameters(request.getParameterMap());
		try {response.setWindowState(request.getWindowState());} catch(Exception e){};
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		if (request.getWindowState().equals(WindowState.NORMAL)) 
			return BinderHelper.CommonPortletDispatch(this, request, response);
		
        User user = RequestContextHolder.getRequestContext().getUser();
		Map formData = request.getParameterMap();
		Long binderId= PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID);
		BinderHelper.setBinderPermaLink(this, request, response);

 		//Check special options in the URL
		String[] debug = (String[])formData.get(WebKeys.URL_DEBUG);
		if (debug != null && (debug[0].equals(WebKeys.DEBUG_ON) || debug[0].equals(WebKeys.DEBUG_OFF))) {
			//The user is requesting debug mode to be turned on or off
			if (debug[0].equals(WebKeys.DEBUG_ON)) {
				getProfileModule().setUserProperty(user.getId(), 
						ObjectKeys.USER_PROPERTY_DEBUG, new Boolean(true));
			} else if (debug[0].equals(WebKeys.DEBUG_OFF)) {
				getProfileModule().setUserProperty(user.getId(), 
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
			return new ModelAndView(BinderHelper.getViewListingJsp(this, BinderHelper.getViewType(this, binderId)));
		}
		Binder binder = null;
		try {
			binder = getBinderModule().getBinder(binderId);
		} catch(NoBinderByTheIdException e) {
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
	     	if (binder != null) getReportModule().addAuditTrail(AuditType.view, binder);

		}

		request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);

		Map<String,Object> model = new HashMap<String,Object>();
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);
 		model.put(WebKeys.WINDOW_STATE, request.getWindowState());
		
		//See if the entry to be shown is also included
		String entryIdToBeShown = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
		if (entryIdToBeShown.equals(WebKeys.URL_ENTRY_ID_PLACE_HOLDER)) entryIdToBeShown = "";
		String entryTitle = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TITLE, "");
		if (!entryTitle.equals("") && !entryTitle.equals(WebKeys.URL_ENTRY_TITLE_PLACE_HOLDER)) {
			//This must be a request for a title link
			Set entries = getFolderModule().getFolderEntryByNormalizedTitle(binderId, entryTitle);
			if (entries.size() == 1) {
				FolderEntry entry = (FolderEntry)entries.iterator().next();
				entryIdToBeShown = entry.getId().toString();
			} else {
				entryIdToBeShown = "";
			}
		}
		model.put(WebKeys.ENTRY_ID_TO_BE_SHOWN, entryIdToBeShown);

		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		reloadUrl.setParameter(WebKeys.URL_ENTRY_ID, WebKeys.URL_ENTRY_ID_PLACE_HOLDER);
		reloadUrl.setParameter(WebKeys.URL_RANDOM, WebKeys.URL_RANDOM_PLACEHOLDER);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
	
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
		model.put(WebKeys.SEEN_MAP, getProfileModule().getUserSeenMap(user.getId()));
		if(binder != null) {
			DashboardHelper.getDashboardMap(binder, userProperties, model);
			//See if the user has selected a specific view to use
			DefinitionHelper.getDefinitions(binder, model, (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));
		}
		Tabs tabs = BinderHelper.initTabs(request, binder);
		Map tabOptions = tabs.getTab(tabs.getCurrentTab());
		model.put(WebKeys.TABS, tabs.getTabs());		

		Map options = new HashMap();		
		
		options.putAll(getSearchFilter(request, userFolderProperties));
		
		//determine page starts/ counts
		initPageCounts(request, userProperties, tabOptions, options);

		String viewType = "";
		Element configElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		if (configElement != null) {
			viewType = DefinitionUtils.getPropertyValue(configElement, "type");
			if (viewType == null) viewType = "";
		}

		//If the Folder View is anything other than Table and if the Folder Action happens to be  
		//vertical (view at the bottom), then we need to display the entry in the iframe view
		if (!viewType.equals(Definition.VIEW_STYLE_TABLE)) {
			String displayStyle = user.getDisplayStyle();
			if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
				model.put(WebKeys.FOLDER_ACTION_VERTICAL_OVERRIDE, "yes");
			}
		}
		
		if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
			//In Blog style we only want to show this entry
			if (!entryIdToBeShown.equals("")) {
				//options.put(ObjectKeys.FOLDER_ENTRY_TO_BE_SHOWN, entryIdToBeShown);
				model.put(WebKeys.FOLDER_VIEW_TYPE, viewType);
			}
		}

		//Checking the Sort Order that has been set. If not using the Default Sort Order
		initSortOrder(request, userFolderProperties, tabOptions, options, viewType);

		setupUrlCalendar(request, tabOptions, options, model);
		setupUrlTags(request, tabOptions, options, model);

		String view = null;
		if(binder== null) {
			view = "binder/deleted_binder";
		} else if(configElement == null) {
			view = WebKeys.VIEW_NO_DEFINITION;
		} else if (op.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
			model.put(WebKeys.SHOW_TEAM_MEMBERS, true);
			view = getTeamMembers(formData, request, response, (Folder)binder, options, model, viewType);
		} else {
			view = getShowFolder(formData, request, response, (Folder)binder, options, model, viewType);
		}
		
		Integer currentTabId  = (Integer) tabOptions.get(Tabs.TAB_ID);
		model.put(WebKeys.URL_TAB_ID, currentTabId);
		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
		model.put(WebKeys.PAGE_MENU_CONTROL_TITLE, NLT.get("folder.Page", new Object[]{options.get(ObjectKeys.SEARCH_MAX_HITS)}));

		if(binder != null) {
			Map tagResults = TagUtil.uniqueTags(getBinderModule().getTags(binder));
			model.put(WebKeys.COMMUNITY_TAGS, tagResults.get(ObjectKeys.COMMUNITY_ENTITY_TAGS));
			model.put(WebKeys.PERSONAL_TAGS, tagResults.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
		}

		try {
			//won't work on adapter
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"0");
		} catch (UnsupportedOperationException us) {}
		
		return new ModelAndView(view, model);
	}
	
	public static Map getSearchFilter(RenderRequest request, UserProperties userFolderProperties) {
		Map result = new HashMap();
		
		//Determine the Search Filter
		String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
		Document searchFilter = null;
		if (Validator.isNotNull(searchFilterName)) {
			Map searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
			searchFilter = (Document)searchFilters.get(searchFilterName);
		}
		result.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchFilter);
		String searchTitle = PortletRequestUtils.getStringParameter(request, WebKeys.SEARCH_TITLE, "");
		if (!searchTitle.equals("")) {
			result.put(ObjectKeys.SEARCH_TITLE, searchTitle);
		}
		
		return result;
	}
	
	protected void initSortOrder(RenderRequest request, UserProperties userFolderProperties, Map tabOptions, Map options, String viewType) {
		//Start - Determine the Sort Order
		if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
			//This is a blog view, set the default sort order
			options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.DOCID_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);

		} else {

			//First Check the Tab Object, see if it has the Sort Order Information
			//	If it is not there, Check the User Folder Properties for the Sort Order Information
			//Get Sort Informtion from Tab Level
			String searchSortBy  = (String) tabOptions.get(Tabs.SORTBY);
			String searchSortDescend  = (String) tabOptions.get(Tabs.SORTDESCEND);
			//Trying to get Sort Information from the User Folder Properties, since Sort information is not available at Tab Level 
			if (Validator.isNull(searchSortBy)) {
				searchSortBy = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY);
				searchSortDescend = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_DESCEND);
			}
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
				options.put(ObjectKeys.SEARCH_SORT_BY, IndexUtils.SORTNUMBER_FIELD);
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
			} else if (!options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) 
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE);
		}

		//Saving the Sort Option in the Tab
		tabOptions.put(Tabs.SORTBY, (String) options.get(ObjectKeys.SEARCH_SORT_BY));
		tabOptions.put(Tabs.SORTDESCEND, options.get(ObjectKeys.SEARCH_SORT_DESCEND).toString());
		//End - Determine the Sort Order
		
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
	
	protected void setupUrlCalendar(RenderRequest request, Map tabOptions, Map options, Map model) {
		// TODO: is this in use?
		
		//See if the url contains an ending date
		Calendar cal = Calendar.getInstance(RequestContextHolder.getRequestContext().getUser().getTimeZone());
		model.put(WebKeys.FOLDER_END_DATE, cal.getTime());
		String day = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_DAY, "");
		String month = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_MONTH, "");
		String year = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DATE_YEAR, "");
		if (!day.equals("") || !month.equals("") || !year.equals("")) {
			String strDate = DateHelper.getDateStringFromDMY(day, month, year);
			options.put(ObjectKeys.SEARCH_END_DATE, strDate);
			tabOptions.put(Tabs.END_DATE, strDate);
			tabOptions.put(Tabs.YEAR_MONTH, "");
			tabOptions.put(Tabs.TAG_COMMUNITY, "");
			tabOptions.put(Tabs.TAG_PERSONAL, "");	
			model.put(WebKeys.FOLDER_END_DATE, DateHelper.getDateFromDMY(day, month, year));
			model.put(WebKeys.URL_DATE_DAY, day);
			model.put(WebKeys.URL_DATE_MONTH, month);
			model.put(WebKeys.URL_DATE_YEAR, year);
		}
		else if (tabOptions.containsKey(Tabs.END_DATE)) {
			String strEndDate = (String) tabOptions.get(Tabs.END_DATE);
			if (strEndDate != null && !"".equals(strEndDate)) {
				options.put(ObjectKeys.SEARCH_END_DATE, strEndDate);
				model.put(WebKeys.URL_DATE_DAY, day);
				model.put(WebKeys.URL_DATE_MONTH, month);
				model.put(WebKeys.URL_DATE_YEAR, year);
			}
		}
		
		//See if this is a request for a specific year/month
		String yearMonth = PortletRequestUtils.getStringParameter(request, WebKeys.URL_YEAR_MONTH, "");
		if (!yearMonth.equals("")) {
			options.put(ObjectKeys.SEARCH_YEAR_MONTH, yearMonth);
			tabOptions.put(Tabs.END_DATE, "");
			tabOptions.put(Tabs.YEAR_MONTH, yearMonth);
			tabOptions.put(Tabs.TAG_COMMUNITY, "");
			tabOptions.put(Tabs.TAG_PERSONAL, "");	
			model.put(WebKeys.URL_YEAR_MONTH, yearMonth);

			String strYear = yearMonth.substring(0, 4);
			String strMonth = yearMonth.substring(4, 6);
			int intMonth = Integer.parseInt(strMonth);
			String strMonthName = NLT.get(monthNames[intMonth-1]);
			
			model.put(WebKeys.SELECTED_YEAR_MONTH, strMonthName + " " +strYear);
		}
		else if (tabOptions.containsKey(Tabs.YEAR_MONTH)) {
			String strYearMonth = (String) tabOptions.get(Tabs.YEAR_MONTH);
			if (strYearMonth != null && !"".equals(strYearMonth)) {
				options.put(ObjectKeys.SEARCH_YEAR_MONTH, strYearMonth);
				model.put(WebKeys.URL_YEAR_MONTH, strYearMonth);
				String strYear = strYearMonth.substring(0, 4);
				String strMonth = strYearMonth.substring(4, 6);
				int intMonth = Integer.parseInt(strMonth);
				String strMonthName = NLT.get(monthNames[intMonth-1]);
				
				model.put(WebKeys.SELECTED_YEAR_MONTH, strMonthName + " " +strYear);
			}
		}
		
	}
	protected void setupUrlTags(RenderRequest request, Map tabOptions, Map options, Map model) {
		//See if the url has tags 
		String cTag = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TAG_COMMUNITY, "");
		if (!cTag.equals("")) {
			options.put(ObjectKeys.SEARCH_COMMUNITY_TAG, cTag);
			tabOptions.put(Tabs.END_DATE, "");
			tabOptions.put(Tabs.YEAR_MONTH, "");
			tabOptions.put(Tabs.TAG_COMMUNITY, cTag);
			tabOptions.put(Tabs.TAG_PERSONAL, "");	
			model.put(WebKeys.URL_TAG_COMMUNITY, cTag);
		}
		else if (tabOptions.containsKey(Tabs.TAG_COMMUNITY)) {
			String strCommunityTag = (String) tabOptions.get(Tabs.TAG_COMMUNITY);
			if (strCommunityTag != null && !"".equals(strCommunityTag)) {
				options.put(ObjectKeys.SEARCH_COMMUNITY_TAG, strCommunityTag);
				model.put(WebKeys.URL_TAG_COMMUNITY, strCommunityTag);
			}
		}

		String pTag = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TAG_PERSONAL, "");
		if (!pTag.equals("")) {
			options.put(ObjectKeys.SEARCH_PERSONAL_TAG, pTag);
			tabOptions.put(Tabs.END_DATE, "");
			tabOptions.put(Tabs.YEAR_MONTH, "");
			tabOptions.put(Tabs.TAG_COMMUNITY, "");
			tabOptions.put(Tabs.TAG_PERSONAL, pTag);	
			model.put(WebKeys.URL_TAG_PERSONAL, pTag);
		}
		else if (tabOptions.containsKey(Tabs.TAG_PERSONAL)) {
			String strPersonalTag = (String) tabOptions.get(Tabs.TAG_PERSONAL);
			if (strPersonalTag != null && !"".equals(strPersonalTag)) {
				options.put(ObjectKeys.SEARCH_PERSONAL_TAG, strPersonalTag);
				model.put(WebKeys.URL_TAG_PERSONAL, strPersonalTag);
			}
		}

	}
	
	protected void setupViewBinder(ActionResponse response, Long binderId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
	}

	private Map findCalendarEvents(RenderRequest request, RenderResponse response, Map model) throws PortletRequestBindingException {
		Map folderEntries = new HashMap();
		Map options = new HashMap();
		
		model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		
		int year = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_YEAR, -1);
		int month = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_MONTH, -1);
		int dayOfMonth = PortletRequestUtils.getIntParameter(request, WebKeys.URL_DATE_DAY_OF_MONTH, -1);
		
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);
		
		Date currentDate = EventsViewHelper.getCalendarCurrentDate(portletSession);
		currentDate = EventsViewHelper.getDate(year, month, dayOfMonth, currentDate);
		model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
		EventsViewHelper.setCalendarCurrentDate(portletSession, currentDate);
		
		String gridType = PortletRequestUtils.getStringParameter(request, WebKeys.CALENDAR_GRID_TYPE, "");
		Integer gridSize = PortletRequestUtils.getIntParameter(request, WebKeys.CALENDAR_GRID_SIZE, -1);
		model.put(WebKeys.CALENDAR_CURRENT_GRID_TYPE, EventsViewHelper.setCalendarGridType(portletSession, gridType));
		model.put(WebKeys.CALENDAR_CURRENT_GRID_SIZE, EventsViewHelper.setCalendarGridSize(portletSession, gridSize));
		
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		options.putAll(ListFolderController.getSearchFilter(request, userFolderProperties));

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
		
       	String strSessGridType = EventsViewHelper.getCalendarGridType(portletSession);
       	Integer sessGridSize = EventsViewHelper.getCalendarGridSize(portletSession);
       	String strSessGridSize = "";
       	if (sessGridSize != null) strSessGridSize = sessGridSize.toString(); 
      	
       	if (EventsViewHelper.GRID_MONTH.equals(strSessGridType)) {
    		setStartDayOfMonth(calStartDateRange);
    		calEndDateRange = (Calendar) calStartDateRange.clone();
    		setEndDayOfMonth(calEndDateRange);
       		
       		nextDate.add(Calendar.MONTH, 1);
       		prevDate.add(Calendar.MONTH, -1);
       		
       	} else if (EventsViewHelper.GRID_DAY.equals(strSessGridType)) {
       		setDatesForGridDayView(calStartDateRange, calEndDateRange, strSessGridSize, prevDate, nextDate);
       	}
       	
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		
		options.put(ObjectKeys.SEARCH_MAX_HITS, 10000);
       	options.put(ObjectKeys.SEARCH_EVENT_DAYS, getExtViewDayDates(calStartDateRange, calEndDateRange));
       	
       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_START, formatter.format(calStartDateRange.getTime()));
       	options.put(ObjectKeys.SEARCH_LASTACTIVITY_DATE_END, formatter.format(calEndDateRange.getTime()));

       	options.put(ObjectKeys.SEARCH_CREATION_DATE_START, formatter.format(calStartDateRange.getTime()));
       	options.put(ObjectKeys.SEARCH_CREATION_DATE_END, formatter.format(calEndDateRange.getTime()));

       	model.put(WebKeys.CALENDAR_PREV_DATE, prevDate);
       	model.put(WebKeys.CALENDAR_NEXT_DATE, nextDate);
       	model.put(WebKeys.CALENDAR_CURR_DATE, calCurrentDate);
       	model.put(WebKeys.CALENDAR_RANGE_END_DATE, calEndDateRange);
       	
		if (binder instanceof Folder) {
			folderEntries = getFolderModule().getEntries(binderId, options);
		}
		
		return folderEntries;
	}
	
	public void setDatesForGridDayView(Calendar calStartDateRange, Calendar calEndDateRange, String strSessGridSize, 
			Calendar prevDate, Calendar nextDate) {
		
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
	
	public List getExtViewDayDates(Calendar startViewExtWindow, Calendar endViewExtWindow) {
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

	private void setStartDayOfMonth(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
	}

	private void setEndDayOfMonth(Calendar startDateOfMonth) {
		startDateOfMonth.add(Calendar.MONTH, 1);
		startDateOfMonth.set(Calendar.HOUR_OF_DAY, 0);
		startDateOfMonth.set(Calendar.MINUTE, 0);
		startDateOfMonth.set(Calendar.SECOND, 0);
		startDateOfMonth.set(Calendar.MILLISECOND, 0);
		startDateOfMonth.add(Calendar.MILLISECOND, -1);
	}	

	private void setStartOfDay(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	private void setEndOfDay(Calendar cal) {
		cal.add(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MILLISECOND, -1);
	}

	private void setDaysToBeSearched(Calendar cal, int numberOfDaysToBeSearched) {
		cal.add(Calendar.DAY_OF_MONTH, numberOfDaysToBeSearched);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MILLISECOND, -1);
	}
	
	protected String getShowFolder(Map formData, RenderRequest req, 
			RenderResponse response, Folder folder, Map options, 
			Map<String,Object>model, String viewType) throws Exception {
		Map folderEntries = null;
		Long folderId = folder.getId();
		User user = RequestContextHolder.getRequestContext().getUser();
					
		if (viewType.equals(Definition.VIEW_STYLE_BLOG)) {
			folderEntries = getFolderModule().getFullEntries(folderId, options);
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
					getProfileModule().setSeen(null, es);
					break;
				}
			}
			//Get the WebDAV URLs
			buildWebDAVURLs(req, folderEntries, model, folder);
			
			//Get the list of all entries to build the archive list
			buildBlogBeans(response, folder, options, model, folderEntries);
		} else {
			String strUserDisplayStyle = user.getDisplayStyle();
			if (strUserDisplayStyle == null) { strUserDisplayStyle = ""; }
			
			if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) && !ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle)) {
				// do it with ajax
			} else if (viewType.equals(Definition.VIEW_STYLE_TASK) && !ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle)) {
				folderEntries = findTaskEntries(req, response, model, options);
			} else if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) && ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE.equals(strUserDisplayStyle)) {
				folderEntries = findCalendarEvents(req, response, model);
			}
			else {
				folderEntries = getFolderModule().getEntries(folderId, options);
			}
			if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
				buildWikiBeans(response, folder, options, model, folderEntries);
			}
			if (viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
				//Get the list of all entries to build the archive list
				buildBlogBeans(response, folder, options, model, folderEntries);
			}
			if (viewType.equals(Definition.VIEW_STYLE_MILESTONE)) {
				//Get the list of all entries to build the archive list
				loadFolderStatisticsForPlacesAttributes(response, folder, options, model, folderEntries);
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
			getBlogEntries(folder, folderEntries, model, req, response);
		}
		
		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, folder, model);
		Binder workspaceBinder = folder.getParentBinder();
		if (folder.isTop()) {
			workspaceBinder = folder.getParentBinder();
		} else {
			workspaceBinder = folder.getTopFolder().getParentBinder();
		}
		BinderHelper.buildWorkspaceTreeBean(this, workspaceBinder, model, null);
		
		String forumId = folderId.toString();
		buildFolderToolbars(req, response, folder, forumId, model, viewType);
		return BinderHelper.getViewListingJsp(this, viewType);
	}
	
	protected Map getSearchAndPagingModels(Map folderEntries, Map options) {
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

		model.put(WebKeys.PAGE_PREVIOUS, prevPage);
		model.put(WebKeys.PAGE_NUMBERS, pageNumbers);
		model.put(WebKeys.PAGE_NEXT, nextPage);
		model.put(WebKeys.PAGE_START_INDEX, pageStartIndex);
		model.put(WebKeys.PAGE_END_INDEX, pageEndIndex);
		model.put(WebKeys.PAGE_TOTAL_RECORDS, ""+totalRecordsFound);
		
		double dblNoOfPages = Math.ceil((double)totalRecordsFound/searchPageIncrement);
		
		model.put(WebKeys.PAGE_COUNT, ""+dblNoOfPages);
		model.put(WebKeys.SEARCH_TOTAL_HITS, folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		
		return model;
	}	
	
	protected String getTeamMembers(Map formData, RenderRequest req, 
			RenderResponse response, Folder folder, Map options, 
			Map<String,Object>model, String viewType) throws PortletRequestBindingException {
		
		try {
			Collection users = getBinderModule().getTeamMembers(folder, true);
			model.put(WebKeys.TEAM_MEMBERS, users);
			model.put(WebKeys.TEAM_MEMBERS_COUNT, users.size());
		} catch (AccessControlException ac) {} //just skip
		
		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, folder, model);
		
		buildFolderToolbars(req, response, folder, folder.getId().toString(), model, viewType);
		return "entry/view_listing_team_members";
	}
	
	public static void getShowTemplate(RenderRequest req, 
			RenderResponse response, Binder folder, Map<String,Object>model) throws PortletRequestBindingException {

		String viewType = "";
		Element configElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		if (configElement != null) {
			viewType = DefinitionUtils.getPropertyValue(configElement, "type");
		}
		//	The "Display styles" menu
		Toolbar entryToolbar = new Toolbar();
		entryToolbar.addToolbarMenu("2_display_styles", NLT.get("toolbar.folder_views"));
		//Get the definitions available for use in this folder
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
//			buildBlogBeans(response, folder, options, model, folderEntries);
		} else if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
			//Get the list of all entries to build the archive list
			model.put(WebKeys.WIKI_HOMEPAGE_ENTRY_ID, folder.getProperty(ObjectKeys.BINDER_PROPERTY_WIKI_HOMEPAGE));
		} else if (viewType.equals(Definition.VIEW_STYLE_CALENDAR)) {
			Date currentDate = EventsViewHelper.getCalendarCurrentDate(WebHelper.getRequiredPortletSession(req));
			model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
		}
		
	}
	//Method to find the WebDAV URL for each of the Blog entries
	public void buildWebDAVURLs(RenderRequest req, Map folderEntries, Map model, Folder folder) {
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
	public void buildBlogBeans(RenderResponse response, Folder folder, Map options, Map model, Map folderEntries) {
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
		options2.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.CREATION_YEAR_MONTH_FIELD);
    	//Look only for binderId=binder and doctype = entry (not attachement)
    	if (folder != null) {
			Document searchFilter2 = DocumentHelper.createDocument();
    		Element rootElement = searchFilter2.addElement(QueryBuilder.AND_ELEMENT);
    		Element field = rootElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
        	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	child.setText(folder.getId().toString());
        	
        	//Look only for docType=entry and entryType=entry
        	field = rootElement.addElement(QueryBuilder.FIELD_ELEMENT);
        	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
        	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
        	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);
           	field = rootElement.addElement(QueryBuilder.FIELD_ELEMENT);
           	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
           	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
           	child.setText(EntityIndexUtils.ENTRY_TYPE_ENTRY);
        	options2.put(ObjectKeys.SEARCH_FILTER_AND, searchFilter2);
    	}
		Map entriesMap = getBinderModule().executeSearchQuery(searchFilter, options2);
		List entries = (List) entriesMap.get(ObjectKeys.SEARCH_ENTRIES);
		LinkedHashMap monthHits = new LinkedHashMap();
		Map monthTitles = new HashMap();
		Map monthUrls = new HashMap();
		Iterator itEntries = entries.iterator();
		while (itEntries.hasNext()) {
			Map entry = (Map)itEntries.next();
			if (entry.containsKey(EntityIndexUtils.CREATION_YEAR_MONTH_FIELD)) {
				String yearMonth = (String) entry.get(EntityIndexUtils.CREATION_YEAR_MONTH_FIELD);
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
			if (entry.containsKey(EntityIndexUtils.CREATION_YEAR_MONTH_FIELD)) {
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

	public void buildWikiBeans(RenderResponse response, Binder binder, Map options, Map model, Map folderEntries) {
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
	public void loadFolderStatisticsForPlacesAttributes(RenderResponse response, Binder binder, Map options, Map model, Map folderEntries) {
		if (folderEntries.get(ObjectKeys.SEARCH_ENTRIES) == null) {
			return;
		}
		Map folders = new HashMap();
		
		List placesIds = new ArrayList();
		
		Iterator it = ((List)folderEntries.get(ObjectKeys.SEARCH_ENTRIES)).iterator();
		while (it.hasNext()) {
			Map entry = (Map)it.next();
			String definitionId = (String)entry.get(EntityIndexUtils.COMMAND_DEFINITION_FIELD);
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
				Folder folder = getFolderModule().getFolder(placeId);
				folders.put(placeId.toString(), folder);
			} catch (Exception ex) {continue;}
		}
		
		if (!folders.isEmpty()) {
			model.put(WebKeys.FOLDERS, folders);
		}
		
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
	
	protected void buildFolderToolbars(RenderRequest request, 
			RenderResponse response, Folder folder, String forumId, Map model, String viewType) {
        User user = RequestContextHolder.getRequestContext().getUser();
        String userDisplayStyle = user.getDisplayStyle();
        if (userDisplayStyle == null) userDisplayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
        
		//Build the toolbar arrays
		Toolbar folderToolbar = new Toolbar();
		Toolbar entryToolbar = new Toolbar();
		Toolbar dashboardToolbar = new Toolbar();
		Toolbar footerToolbar = new Toolbar();
		AdaptedPortletURL adapterUrl;
		Map qualifiers;
		Map dropdownQualifiers;
		//	The "Add entry" menu
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		PortletURL url;
		if (!defaultEntryDefinitions.isEmpty()) {
			if (getFolderModule().testAccess(folder, FolderOperation.addEntry)) {				

				if (defaultEntryDefinitions.size() > 1) {
					int count = 1;
					dropdownQualifiers = new HashMap();
					dropdownQualifiers.put("highlight", new Boolean(true));
					entryToolbar.addToolbarMenu("1_add", NLT.get("toolbar.new"), "", dropdownQualifiers);
					qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					//String onClickPhrase = "if (self.ss_addEntry) {return(self.ss_addEntry(this))} else {return true;}";
					//qualifiers.put(ObjectKeys.TOOLBAR_QUALIFIER_ONCLICK, onClickPhrase);
					for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
						Definition def = (Definition) defaultEntryDefinitions.get(i);
						adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
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
				} else {
					// Only one option
					Definition def = (Definition) defaultEntryDefinitions.get(0);
					adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					String title = NLT.get("toolbar.new") + ": " + NLT.getDef(def.getTitle());
					qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					qualifiers.put("highlight", new Boolean(true));
					entryToolbar.addToolbarMenu("1_add", title, adapterUrl.toString(), qualifiers);

					adapterUrl.setParameter(WebKeys.URL_NAMESPACE, response.getNamespace());
					adapterUrl.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
					model.put(WebKeys.URL_ADD_DEFAULT_ENTRY, adapterUrl.toString());

				}

			}
		}
		//The "Administration" menu
		qualifiers = new HashMap();
		qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageFolderMenu");
		boolean adminMenuCreated=false;
		folderToolbar.addToolbarMenu("1_administration", NLT.get("toolbar.manageThisFolder"), "", qualifiers);
		//Add Folder
		if (getFolderModule().testAccess(folder, FolderOperation.addFolder)) {
			adminMenuCreated=true;
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_SUB_FOLDER);
			folderToolbar.addToolbarMenuItem("1_administration", "folders", NLT.get("toolbar.menu.addFolder"), url);
		}
		
		//Move binder
		if (getBinderModule().testAccess(folder, BinderOperation.moveBinder)) {
			adminMenuCreated=true;
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.move_folder"), url);
		}

		//Configuration
		if (getBinderModule().testAccess(folder, BinderOperation.modifyBinder)) {
			adminMenuCreated=true;
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.configuration"), url);
		}
		
		//Reporting
		if (getBinderModule().testAccess(folder, BinderOperation.report)) {
			adminMenuCreated=true;
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.report"), url);
		}
		
		//Definition builder - forms (turned off until local definitions supported)
		/*
		adminMenuCreated=true;
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.FOLDER_ENTRY));
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
		folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.definition_builder.folderEntry"), url);
		//Definition builder - workflows
		adminMenuCreated=true;
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.WORKFLOW));
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
		folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.definition_builder.workflow"), url);
		*/
		
		//Delete binder
		if (getBinderModule().testAccess(folder, BinderOperation.deleteBinder)) {
			adminMenuCreated=true;
			qualifiers = new HashMap();
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.delete_folder"), url, qualifiers);		
		}

		//Modify binder
		if (getBinderModule().testAccess(folder, BinderOperation.modifyBinder)) {
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
	
		if(LicenseChecker.isAuthorizedByLicense("com.sitescape.team.module.folder.MirroredFolder")) {
			//Synchronize mirrored folder
			if(folder.isMirrored() &&
					getFolderModule().testAccess(folder, FolderOperation.synchronize)) {
				adminMenuCreated=true;
				qualifiers = new HashMap();
				qualifiers.put("showSpinner", new Boolean(true));
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SYNCHRONIZE_MIRRORED_FOLDER);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
				folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.synchronize_mirrored_folder"), url, qualifiers);
			}
		}

		//set email
		if (getBinderModule().testAccess(folder, BinderOperation.manageMail)) {
			try {
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIG_EMAIL);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.configure_folder_email"), url);
				adminMenuCreated=true;
			} catch (AccessControlException ac) {};
		}

		//if no menu items were added, remove the empty menu
		if (!adminMenuCreated) folderToolbar.deleteToolbarMenu("1_administration");

		//Access control
		if (getAdminModule().testAccess(folder, AdminOperation.manageFunctionMembership)) {
			qualifiers = new HashMap();
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.accessControlMenu");
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			folderToolbar.addToolbarMenuItem("1_administration", "", NLT.get("toolbar.menu.accessControl"), url, qualifiers);
		}

		//The "Subsrciptions" menu
		if (folder.isTop()) {
			qualifiers = new HashMap();
			qualifiers.put(WebKeys.HELP_SPOT, "helpSpot.manageSubscriptionsMenu");
			folderToolbar.addToolbarMenu("3_administration", NLT.get("toolbar.manageFolderSubscriptions"), "", qualifiers);
		
			Subscription sub = getBinderModule().getSubscription(folder.getId());
			qualifiers = new HashMap();
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", false);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SUBSCRIBE);			
			adapterUrl.setParameter("rn", "ss_randomNumberPlaceholder");			
			qualifiers.put("onClick", "ss_createPopupDiv(this, 'ss_subscription_menu');return false;");
			if (sub == null) {
				folderToolbar.addToolbarMenuItem("3_administration", "", 
						NLT.get("toolbar.menu.subscribeToFolder"), adapterUrl.toString(), qualifiers);	
			} else {
				folderToolbar.addToolbarMenuItem("3_administration", "", 
						NLT.get("toolbar.menu.subscriptionToFolder"), adapterUrl.toString(), qualifiers);			
			}

			//RSS link 
			qualifiers = new HashMap();
			String rssUrl = UrlUtil.getFeedURL(request, forumId);
			if (rssUrl != null && !rssUrl.equals(""))
				folderToolbar.addToolbarMenuItem("3_administration", "", NLT.get("toolbar.menu.rss"), 
						rssUrl, qualifiers);
		}
		
		// list team members
		qualifiers = new HashMap();			
			
		//The "Teams" menu
		folderToolbar.addToolbarMenu("5_team", NLT.get("toolbar.teams"));
			
		//Add
		if (getBinderModule().testAccess(folder, BinderOperation.manageTeamMembers)) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			qualifiers.put("popupWidth", "500");
			qualifiers.put("popupHeight", "600");
			folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.addMember"), adapterUrl.toString(), qualifiers);
		}
		//View
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SHOW_TEAM_MEMBERS);
		url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityType().name());
		folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.view"), url);
			
		//Sendmail
		adapterUrl = new AdaptedPortletURL((PortletRequest)null, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
		qualifiers = new HashMap();
		qualifiers.put("popup", Boolean.TRUE);
		folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.sendmail"), adapterUrl.toString(), qualifiers);
		
		//Meet
		if (getIcBrokerModule().isEnabled()) {
			adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
			qualifiers = new HashMap();
			qualifiers.put("popup", Boolean.TRUE);
			folderToolbar.addToolbarMenuItem("5_team", "", NLT.get("toolbar.teams.meet"), adapterUrl.toString(), qualifiers);
		}
	
		
		//See if a "sort by" menu is needed
		if (viewType.equals(Definition.VIEW_STYLE_DEFAULT) || 
				viewType.equals(Definition.VIEW_STYLE_WIKI)|| 
				viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM)) {
			//Add a way to set the sorting
			UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), folder.getId());
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
				if (searchSortBy.equals(EntityIndexUtils.DOCID_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, EntityIndexUtils.DOCID_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "true");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.Number"), url, qualifiers);
			}
			
			//title
			if (so.contains("title")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(EntityIndexUtils.SORT_TITLE_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, EntityIndexUtils.SORT_TITLE_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "false");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.Title"), url, qualifiers);
			}
			
			//state
			if (so.contains("state")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(EntityIndexUtils.WORKFLOW_STATE_CAPTION_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, EntityIndexUtils.WORKFLOW_STATE_CAPTION_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "false");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.State"), url, qualifiers);
			}
			
			//author
			if (so.contains("author")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(EntityIndexUtils.CREATOR_TITLE_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, EntityIndexUtils.CREATOR_TITLE_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "false");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.Author"), url, qualifiers);
			}
			
			//last activity date
			if (so.contains("activity")) {
				qualifiers = new HashMap();
				if (searchSortBy.equals(IndexUtils.LASTACTIVITY_FIELD)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SAVE_FOLDER_SORT_INFO);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.FOLDER_SORT_BY, IndexUtils.LASTACTIVITY_FIELD);
				url.setParameter(WebKeys.FOLDER_SORT_DESCEND, "true");
				entryToolbar.addToolbarMenuItem("2_display_styles", "sortby", 
						NLT.get("folder.column.LastActivity"), url, qualifiers);
			}
		}
		
		//	The "Display styles" menu
		entryToolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.folder_views"));
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
			entryToolbar.addToolbarMenuItem("3_display_styles", "folderviews", NLT.getDef(def.getTitle()), url, qualifiers);
		}
		//WebDav folder view
		String webdavUrl = "";
		if (folder.isLibrary()) {
			webdavUrl = SsfsUtil.getLibraryBinderUrl(request, folder);
			qualifiers = new HashMap();
			qualifiers.put("webdavUrl", webdavUrl);
			qualifiers.put("folder", webdavUrl);
			entryToolbar.addToolbarMenuItem("3_display_styles", "folderviews", NLT.get("toolbar.menu.viewASWebDav"), webdavUrl, qualifiers);
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
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) && 
				(viewType.equals(Definition.VIEW_STYLE_DEFAULT) 
				|| viewType.equals(Definition.VIEW_STYLE_BLOG) 
				|| viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM) 
				|| viewType.equals(Definition.VIEW_STYLE_GUESTBOOK) 
				|| viewType.equals(Definition.VIEW_STYLE_TASK) 
				|| viewType.equals(Definition.VIEW_STYLE_TABLE)
				|| viewType.equals(Definition.VIEW_STYLE_CALENDAR)
				|| viewType.equals(Definition.VIEW_STYLE_FILE)
				|| viewType.equals(""))) {
			//Only show these options if in the folder table style and not in accessible mode
			entryToolbar.addToolbarMenu("4_display_styles", NLT.get("toolbar.folder_actions"));
			
			//Hemanth: Display Show entries at bottom folder action option only for the Table view
			if (viewType.equals(Definition.VIEW_STYLE_TABLE)) {
				//vertical
				qualifiers = new HashMap();
				if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) 
					qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true); 
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
				entryToolbar.addToolbarMenuItem("4_display_styles", "styles", 
						NLT.get("toolbar.menu.display_style_vertical"), url, qualifiers);
			}
			
			//iframe
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
			entryToolbar.addToolbarMenuItem("4_display_styles", "styles", 
					NLT.get("toolbar.menu.display_style_iframe"), url, qualifiers);
			//popup
			qualifiers = new HashMap();
			if (userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) 
				qualifiers.put(WebKeys.TOOLBAR_MENU_SELECTED, true);
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
			entryToolbar.addToolbarMenuItem("4_display_styles", "styles", 
					NLT.get("toolbar.menu.display_style_popup"), url, qualifiers);
		}
		
		//Folder action menu
		if (!userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE) && 
				(viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
						viewType.equals(Definition.VIEW_STYLE_TASK))) {
			
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_calendar_import.importForm('" + forumId + "', '" + response.getNamespace() + "');return false;");
			if (viewType.equals(Definition.VIEW_STYLE_CALENDAR)) {
				entryToolbar.addToolbarMenu("5_calendar", NLT.get("toolbar.menu.calendarImport"), "#", qualifiers);
			} else {
				entryToolbar.addToolbarMenu("5_calendar", NLT.get("toolbar.menu.taskImport"), "#", qualifiers);
			}
		}
		
		//Build the "Manage dashboard" toolbar
		BinderHelper.buildDashboardToolbar(request, response, this, folder, dashboardToolbar, model);

		//The "Footer" menu
		adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, folder.getEntityType().toString());
		qualifiers = new HashMap();
		qualifiers.put("onClick", "ss_showPermalink(this);return false;");
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.folderPermalink"), 
				adapterUrl.toString(), qualifiers);

		String[] contributorIds = collectContributorIds((List)model.get(WebKeys.FOLDER_ENTRIES));
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");

		// iCalendar
		if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) ||
				viewType.equals(Definition.VIEW_STYLE_TASK)) {
			qualifiers = new HashMap();
			footerToolbar.addToolbarMenu("iCalendar", NLT.get("toolbar.menu.iCalendar"), com.sitescape.team.ical.util.UrlUtil.getICalURL(request, forumId, null), qualifiers);
		}

		// clipboard
		qualifiers = new HashMap();
		String contributorIdsAsJSString = "";
		for (int i = 0; i < contributorIds.length; i++) {
			contributorIdsAsJSString += contributorIds[i];
			if (i < (contributorIds.length -1)) {
				contributorIdsAsJSString += ", ";	
			}
		}
		qualifiers.put("onClick", "ss_muster.showForm('" + Clipboard.USERS + "', [" + contributorIdsAsJSString + "]" + ", '" + forumId + "');return false;");
		footerToolbar.addToolbarMenu("clipboard", NLT.get("toolbar.menu.clipboard"), "#", qualifiers);
		
		// email
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
		}
		footerToolbar.addToolbarMenu("sendMail", NLT.get("toolbar.menu.sendMail"), adapterUrl.toString(), qualifiers);

		// start meeting
		if (getIcBrokerModule().isEnabled()) {
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
			}
			footerToolbar.addToolbarMenu("addMeeting", NLT.get("toolbar.menu.addMeeting"), adapterUrl.toString(), qualifiers);
		}
		if (folder.isLibrary() && !webdavUrl.equals("")) {
			qualifiers = new HashMap();
			qualifiers.put("webdavUrl", webdavUrl);
			qualifiers.put("folder", webdavUrl);
			footerToolbar.addToolbarMenu("webdavUrl", NLT.get("toolbar.menu.webdavUrl"), webdavUrl, qualifiers);
		}
		
		boolean isAppletSupported = SsfsUtil.supportApplets();
        
        boolean isAccessible = false;
		String displayStyle = user.getDisplayStyle();
		if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
			isAccessible = true;
		}
		
		if (isAppletSupported && getFolderModule().testAccess(folder, FolderOperation.addEntry) && !isAccessible) {
			qualifiers = new HashMap();
			qualifiers.put("onClick", "javascript: ss_showFolderAddAttachmentDropbox" + folder.getId().toString() + response.getNamespace() + "()" +"; return false;");
			footerToolbar.addToolbarMenu("dropBox", NLT.get("toolbar.menu.dropBox"), "javascript: ;", qualifiers);
		}
		
		qualifiers = new HashMap();
		qualifiers.put("onClick", "javascript: ss_changeUITheme('" +
				NLT.get("ui.availableThemeIds") + "', '" +
				NLT.get("ui.availableThemeNames") + "'); return false;");
		footerToolbar.addToolbarMenu("themeChanger", NLT.get("toolbar.menu.changeUiTheme"), "javascript: ;", qualifiers);
		
		model.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
		model.put(WebKeys.FOLDER_TOOLBAR,  folderToolbar.getToolbar());
		model.put(WebKeys.ENTRY_TOOLBAR,  entryToolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
	}
	

	private String[] collectContributorIds(List entries) {
		Set principals = new HashSet();
		
		if (entries != null) {
			Iterator entriesIt = entries.iterator();
			while (entriesIt.hasNext()) {
				Map entry = (Map)entriesIt.next();
				String creatorId = entry.get(EntityIndexUtils.CREATORID_FIELD).toString();
				String modificationId = entry.get(EntityIndexUtils.MODIFICATIONID_FIELD).toString();
				principals.add(creatorId);
				principals.add(modificationId);
			}	
		}
		String[] as = new String[principals.size()];
		principals.toArray(as);
		return as;
	}

	protected void getBlogEntries(Folder folder, Map folderEntries,  Map model, RenderRequest request, RenderResponse response) {
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
			if (getFolderModule().testAccess(entry, FolderOperation.addReply)) {
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

			if (getFolderModule().testAccess(entry, FolderOperation.reserveEntry)) {
				reserveAccessCheck = true;
			}
			if (getFolderModule().testAccess(entry, FolderOperation.overrideReserveEntry)) {
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
			if (getFolderModule().testAccess(entry, FolderOperation.modifyEntry)) {
				if (reserveAccessCheck && isEntryReserved && !(isUserBinderAdministrator || isLockedByAndLoginUserSame) ) {
				} else {
					accessControlEntryMap.put("modifyEntry", new Boolean(true));
				}
			}

			entryMap.put(WebKeys.COMMUNITY_TAGS, publicTags.get(entry.getId()));
			entryMap.put(WebKeys.PERSONAL_TAGS, privateTags.get(entry.getId()));
		}
	}

	private Map findTaskEntries(RenderRequest request, RenderResponse response, Map model, Map options) throws PortletRequestBindingException {
		model.put(WebKeys.USER_PRINCIPAL, RequestContextHolder.getRequestContext().getUser());
		Long binderId = PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID);
		Binder binder = getBinderModule().getBinder(binderId);
		
		PortletSession portletSession = WebHelper.getRequiredPortletSession(request);

		Map folderEntries = new HashMap();
		
		User user = RequestContextHolder.getRequestContext().getUser();
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		options.putAll(ListFolderController.getSearchFilter(request, userFolderProperties));
		
		String filterTypeParam = PortletRequestUtils.getStringParameter(request, WebKeys.TASK_FILTER_TYPE, null);
		TaskHelper.FilterType filterType = TaskHelper.setTaskFilterType(portletSession, filterTypeParam != null ? TaskHelper.FilterType.valueOf(filterTypeParam) : null);
		model.put(WebKeys.TASK_CURRENT_FILTER_TYPE, filterType);
		
		options.put(ObjectKeys.SEARCH_SEARCH_DYNAMIC_FILTER, TaskHelper.buildSearchFilter(filterType).getFilter());
       	
		if (binder instanceof Folder) {
			folderEntries = getFolderModule().getEntries(binderId, options);
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
	    		String entryDefId = (String)entry.get(EntityIndexUtils.COMMAND_DEFINITION_FIELD);
	    		if (cacheEntryDef.get(entryDefId) == null) {
	    			cacheEntryDef.put(entryDefId, getDefinitionModule().getEntryDefinitionElements(entryDefId));
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
	public class DummySeenMap extends SeenMap {
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

