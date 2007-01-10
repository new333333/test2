package com.sitescape.ef.portlet.forum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.module.folder.index.IndexUtils;
import com.sitescape.ef.module.shared.EntityIndexUtils;
import com.sitescape.ef.module.shared.MapInputData;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.rss.util.UrlUtil;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.QueryBuilder;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.ssfs.util.SsfsUtil;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.BinderHelper;
import com.sitescape.ef.web.util.DashboardHelper;
import com.sitescape.ef.web.util.DateHelper;
import com.sitescape.ef.web.util.DefinitionHelper;
import com.sitescape.ef.web.util.FilterHelper;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Tabs;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.web.util.BinderHelper.TreeBuilder;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.util.cal.CalendarUtil;

/**
 * @author Peter Hurley
 *
 */
public class ListFolderController extends  SAbstractController {
	
	String[] monthNames = { 
			NLT.get("calendar.january"),
			NLT.get("calendar.february"),
			NLT.get("calendar.march"),
			NLT.get("calendar.april"),
			NLT.get("calendar.may"),
			NLT.get("calendar.june"),
			NLT.get("calendar.july"),
			NLT.get("calendar.august"),
			NLT.get("calendar.september"),
			NLT.get("calendar.october"),
			NLT.get("calendar.november"),
			NLT.get("calendar.december")
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
		
		} else if (op.equals(WebKeys.OPERATION_SET_CALENDAR_DISPLAY_MODE)) {
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_CALENDAR_VIEWMODE, 
					PortletRequestUtils.getStringParameter(request,WebKeys.URL_VALUE,""));
		
		} else if (op.equals(WebKeys.OPERATION_SET_CALENDAR_DISPLAY_DATE)) {
			PortletSession ps = WebHelper.getRequiredPortletSession(request);
			String urldate = PortletRequestUtils.getStringParameter(request,WebKeys.CALENDAR_URL_NEWVIEWDATE, "");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
			Date newdate = sdf.parse(urldate);
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, newdate);
			String viewMode = PortletRequestUtils.getStringParameter(request,WebKeys.CALENDAR_URL_VIEWMODE, "");
			getProfileModule().setUserProperty(user.getId(), binderId, 
					ObjectKeys.USER_PROPERTY_CALENDAR_VIEWMODE, viewMode);
		
		} else if (op.equals(WebKeys.OPERATION_CALENDAR_GOTO_DATE)) {
			PortletSession ps = WebHelper.getRequiredPortletSession(request);
			Date dt = DateHelper.getDateFromInput(new MapInputData(formData), "ss_goto");
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, dt);
			
		} else if (op.equals(WebKeys.OPERATION_SELECT_FILTER)) {
			getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_USER_FILTER, 
					PortletRequestUtils.getStringParameter(request, WebKeys.OPERATION_SELECT_FILTER,""));
		
		} else if (op.equals(WebKeys.OPERATION_SAVE_FOLDER_COLUMNS)) {
			if (formData.containsKey("okBtn")) {
				Map columns = new HashMap();
				String[] columnNames = new String[] {"number", "title", "state", "author", "date"};
				for (int i = 0; i < columnNames.length; i++) {
					columns.put(columnNames[i], PortletRequestUtils.getStringParameter(request, columnNames[i], ""));
				}
				getProfileModule().setUserProperty(user.getId(), binderId, 
						ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS, columns);
				//Reset the column positions to the default
			   	getProfileModule().setUserProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_COLUMN_POSITIONS, "");
			} else if (formData.containsKey("defaultBtn")) {
				getProfileModule().setUserProperty(user.getId(), binderId, 
						ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS, null);
				//Reset the column positions to the default
			   	getProfileModule().setUserProperty(user.getId(), Long.valueOf(binderId), WebKeys.FOLDER_COLUMN_POSITIONS, "");
			}
		} else if (op.equals(WebKeys.OPERATION_SUBSCRIBE)) {
			Integer style = PortletRequestUtils.getIntParameter(request, "notifyType");
			if (style != null) {
				if (style.intValue() == -1) getBinderModule().deleteSubscription(binderId);
				else getBinderModule().addSubscription(binderId, style.intValue());
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
			
			int intTab = tabs.setTab(binder, options);
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
			
			int intTab = tabs.setTab(binder, options);
		} else if (op.equals(WebKeys.OPERATION_CHANGE_ENTRIES_ON_PAGE)) {
			//Changes the number or records to be displayed in a page
			//Getting the new entries per page
			String newEntriesPerPage = PortletRequestUtils.getStringParameter(request, WebKeys.PAGE_ENTRIES_PER_PAGE, "");
			//Saving the Sort Order information in the User Properties
			//Changing the user folder paging information from folder/binder level to the user level 
			//getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
			getProfileModule().setUserProperty(user.getId(), ObjectKeys.PAGE_ENTRIES_PER_PAGE, newEntriesPerPage);
		}

		response.setRenderParameters(request.getParameterMap());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
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
			request.setAttribute("ssReloadUrl", reloadUrl.toString());			
			return new ModelAndView(BinderHelper.getViewListingJsp(this));
		} else if (op.equals(WebKeys.OPERATION_VIEW_ENTRY)) {
			String entryId = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_ID, "");
			if (!entryId.equals("")) {
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entryId);
				request.setAttribute("ssLoadEntryUrl", adapterUrl.toString());			
				request.setAttribute("ssLoadEntryId", entryId);			
			}
		}

		request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		Binder binder = getBinderModule().getBinder(binderId);
		Map<String,Object> model = new HashMap<String,Object>();
		model.put(WebKeys.BINDER, binder);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.ENTRY, binder);
		
		//Set up the tabs
		Tabs tabs = new Tabs(request);
		Integer tabId = PortletRequestUtils.getIntParameter(request, WebKeys.URL_TAB_ID);
		String newTab = PortletRequestUtils.getStringParameter(request, WebKeys.URL_NEW_TAB, "");
		
		//What do the newTab values mean?
		//newTab == 1 means if the Tab already exists use it, if not create another one
		//newTab == 2 means create a new Tab always
		if (newTab.equals("1")) {
			boolean blnClearTab = true;
			tabs.setCurrentTab(tabs.findTab(binder, blnClearTab));
		} else if (newTab.equals("2")) {
			tabs.setCurrentTab(tabs.addTab(binder));
		} else if (tabId != null) {
			//Do not set the page number to zero
			tabs.setCurrentTab(tabs.setTab(tabId.intValue(), binder));
		} else {
			//Don't overwrite a search tab
			if (tabs.getTabType(tabs.getCurrentTab()).equals(Tabs.QUERY)) {
				boolean blnClearTab = true;
				tabs.setCurrentTab(tabs.findTab(binder, blnClearTab));
				
			} else {
				boolean blnClearTab = true;
				tabs.setCurrentTab(tabs.setTab(binder, blnClearTab));
			}
		}
		model.put(WebKeys.TABS, tabs.getTabs());
		
		//Build a reload url
		PortletURL reloadUrl = response.createRenderURL();
		reloadUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		reloadUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		model.put(WebKeys.RELOAD_URL, reloadUrl.toString());
	
		Map userProperties = (Map) getProfileModule().getUserProperties(user.getId()).getProperties();
		model.put(WebKeys.USER_PROPERTIES, userProperties);
		UserProperties userFolderProperties = getProfileModule().getUserProperties(user.getId(), binderId);
		model.put(WebKeys.USER_FOLDER_PROPERTIES, userFolderProperties);
		DashboardHelper.getDashboardMap(binder, userProperties, model);

		Map options = new HashMap();		
		
		//Determine the Search Filter
		String searchFilterName = (String)userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER);
		Document searchFilter = null;
		if (searchFilterName != null && !searchFilterName.equals("")) {
			Map searchFilters = (Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS);
			searchFilter = (Document)searchFilters.get(searchFilterName);
		}
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchFilter);
		
		//See if the user has selected a specific view to use
        UserProperties uProps = getProfileModule().getUserProperties(user.getId(), binderId);
		String userDefaultDef = (String)uProps.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION);
		DefinitionHelper.getDefinitions(binder, model, userDefaultDef);

		Map tabOptions = tabs.getTab(tabs.getCurrentTab());
		
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
		if (entriesPerPage == null || "".equals(entriesPerPage)) {
			//This means that the tab does not have the information about the number of records to display in a page
			//So we need to add this information into the tab
			if (recordsInPage == null) {
				String searchMaxHits = SPropsUtil.getString("folder.records.listed");
				options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(searchMaxHits));
				tabOptions.put(Tabs.RECORDS_IN_PAGE, new Integer(searchMaxHits));
			} else {
				options.put(ObjectKeys.SEARCH_MAX_HITS, recordsInPage);
			}
		} else {
			options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(entriesPerPage));
			tabOptions.put(Tabs.RECORDS_IN_PAGE, new Integer(entriesPerPage));
			if (recordsInPage != null) {
				int intEntriesPerPage = (new Integer(entriesPerPage)).intValue();
				int intEntriesPerPageInTab = recordsInPage.intValue();
				
				if (intEntriesPerPage != intEntriesPerPageInTab) {
					//We need to check and see if the page number is set in the tabs. If so, reset it
					if (pageRecordIndex != null) {
						int intPageRecordIndex = pageRecordIndex.intValue();
						int intNewPageNumber = (intPageRecordIndex + 1)/(intEntriesPerPage);
						int intNewPageStartIndex = (intNewPageNumber) * intEntriesPerPage;
						tabOptions.put(Tabs.PAGE, new Integer(intNewPageStartIndex));
					}
				}
			} else {
				tabOptions.put(Tabs.PAGE, new Integer(0));
			}
		}
		
		//Determine the Folder Start Index
		Integer tabPageNumber = (Integer) tabOptions.get(Tabs.PAGE);
		if (tabPageNumber == null) tabPageNumber = new Integer(0);
		options.put(ObjectKeys.SEARCH_OFFSET, tabPageNumber);
		
		//Start - Determine the Sort Order
		//First Check the Tab Object, see if it has the Sort Order Information
		//If it is not there, Check the User Folder Properties for the Sort Order Information
		//Get Sort Informtion from Tab Level
		String searchSortBy  = (String) tabOptions.get(Tabs.SORTBY);
		String searchSortDescend  = (String) tabOptions.get(Tabs.SORTDESCEND);
		//Trying to get Sort Information from the User Folder Properties, since Sort information is not available at Tab Level 
		if (searchSortBy == null || searchSortBy.equals("")) {
			searchSortBy = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY);
			searchSortDescend = (String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_DESCEND);
		}
		//Setting the Sort properties if it is available in the Tab or User Folder Properties Level. 
		//If not, go with the Default Sort Properties 
		if (searchSortBy != null && !searchSortBy.equals("")) {
			options.put(ObjectKeys.SEARCH_SORT_BY, searchSortBy);
			if (("true").equalsIgnoreCase(searchSortDescend)) {
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
			} else {
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
			}
		}
		//Checking the Sort Order that has been set. If not using the Default Sort Order
		String viewType = "";
		Element elementView = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		if (elementView != null) {
			Element viewElement = (Element)elementView.selectSingleNode("./properties/property[@name='type']");
			if (viewElement != null)
				viewType = viewElement.attributeValue("value", "");
		}
		if (viewType.equals("blog")) {
			//This is a blog view, set the default sort order
			options.put(ObjectKeys.SEARCH_SORT_BY, EntityIndexUtils.DOCID_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));

		} else {
			if (!options.containsKey(ObjectKeys.SEARCH_SORT_BY)) { 
				options.put(ObjectKeys.SEARCH_SORT_BY, IndexUtils.SORTNUMBER_FIELD);
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
			} else if (!options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) 
				options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
		}
		//Saving the Sort Option in the Tab
		tabOptions.put(Tabs.SORTBY, (String) options.get(ObjectKeys.SEARCH_SORT_BY));
		tabOptions.put(Tabs.SORTDESCEND, ((Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND)).toString());
		//End - Determine the Sort Order

		//See if the url contains an ending date
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(user.getTimeZone());
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
			String strMonthName = monthNames[intMonth-1];
			
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
				String strMonthName = monthNames[intMonth-1];
				
				model.put(WebKeys.SELECTED_YEAR_MONTH, strMonthName + " " +strYear);
			}
		}
		
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

		String view;
		view = getShowFolder(formData, request, response, (Folder)binder, options, model);
		
		Integer currentTabId  = (Integer) tabOptions.get(Tabs.TAB_ID);
		model.put(WebKeys.URL_TAB_ID, currentTabId);
		model.put(WebKeys.PAGE_ENTRIES_PER_PAGE, (Integer) options.get(ObjectKeys.SEARCH_MAX_HITS));
		model.put(WebKeys.PAGE_MENU_CONTROL_TITLE, NLT.get("folder.Page", new Object[]{options.get(ObjectKeys.SEARCH_MAX_HITS)}));
			
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		try {
			//won't work on adapter
			response.setProperty(RenderResponse.EXPIRATION_CACHE,"0");
		} catch (UnsupportedOperationException us) {}
		
		return new ModelAndView(view, model);
	}

	protected void setupViewBinder(ActionResponse response, Long binderId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
	}
		
	protected String getShowFolder(Map formData, RenderRequest req, 
			RenderResponse response, Folder folder, Map options, 
			Map<String,Object>model) throws PortletRequestBindingException {
		Map folderEntries;
		Long folderId = folder.getId();
		String forumId = folderId.toString();

		String viewType = "";
		Element view = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		if (view != null) {
			Element viewElement = (Element)view.selectSingleNode("./properties/property[@name='type']");
			if (viewElement != null)
				viewType = viewElement.attributeValue("value", "");
		}
		if (viewType.equals("blog")) {
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(true));
			folderEntries = getFolderModule().getFullEntries(folderId, options);
			//Get the list of all entries to build the archive list
			buildBlogBeans(response, folder, options, model, folderEntries);
		} else {
			folderEntries = getFolderModule().getEntries(folderId, options);
		}

		String sortBy = (String) options.get(ObjectKeys.SEARCH_SORT_BY);
		if (sortBy == null) sortBy = "";
		Boolean sortDescend = (Boolean) options.get(ObjectKeys.SEARCH_SORT_DESCEND);
		if (sortDescend == null) sortDescend = new Boolean(true);
		
		model.put(WebKeys.FOLDER_SORT_BY, sortBy);		
		model.put(WebKeys.FOLDER_SORT_DESCEND, sortDescend.toString());
		
		int totalRecordsFound = (Integer) folderEntries.get(ObjectKeys.TOTAL_SEARCH_COUNT);
		int totalRecordsReturned = (Integer) folderEntries.get(ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED);
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
		
		//Build the beans depending on the operation being done
		model.put(WebKeys.FOLDER, folder);
		Folder topFolder = folder.getTopFolder();
		if (topFolder == null) {
			model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(folderId, new TreeBuilder(null, false, getBinderModule())));
		} else {
			model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(topFolder.getId(), new TreeBuilder(topFolder, false, getBinderModule())));			
		}
		List entries = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
		model.put(WebKeys.FOLDER_ENTRIES, entries);
		model.put(WebKeys.SEARCH_TOTAL_HITS, folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		User user = RequestContextHolder.getRequestContext().getUser();
		model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId()));
				
		//See if this folder is to be viewed as a calendar
		if (viewType.equals("event")) {
			//This is a calendar view, so get the event beans
			getEvents(folder, entries, model, req, response);
		}
		//See if this folder is to be viewed as a blog
		if (viewType.equals("blog")) {
			//This is a blog view, so get the extra blog beans
			getBlogEntries(folder, (List)folderEntries.get(ObjectKeys.FULL_ENTRIES), model, req, response);
		}
		
		//Build the navigation beans
		BinderHelper.buildNavigationLinkBeans(this, folder, model);
		
		buildFolderToolbars(req, response, folder, forumId, model);
		return BinderHelper.getViewListingJsp(this);
	}  
	
	//Routine to build the beans for the blog archives list
	public void buildBlogBeans(RenderResponse response, Folder folder, Map options, Map model, Map folderEntries) {
		Document searchFilter = (Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER);
		if (searchFilter == null) {
			searchFilter = DocumentHelper.createDocument();
    		Element rootElement = searchFilter.addElement(FilterHelper.FilterRootName);
        	rootElement.addElement(FilterHelper.FilterTerms);
    		Element filterTerms = rootElement.addElement(FilterHelper.FilterTerms);
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
		Map entriesMap = getBinderModule().executeSearchQuery(folder, searchFilter, options2);
		List entries = (List) entriesMap.get(WebKeys.FOLDER_ENTRIES);
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
					monthTitles.put(yearMonth, monthNames[m%12] + " " + year);
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
			RenderResponse response, Folder folder, String forumId, Map model) {
		//Build the toolbar arrays
		Toolbar folderToolbar = new Toolbar();
		Toolbar entryToolbar = new Toolbar();
		Toolbar footerToolbar = new Toolbar();
		//	The "Add" menu
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		PortletURL url;
		if (!defaultEntryDefinitions.isEmpty()) {
			try {
				getFolderModule().checkAccess(folder, "addEntry");				
				int count = 1;
				entryToolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
				Map qualifiers = new HashMap();
				qualifiers.put("popup", new Boolean(true));
				String onClickPhrase = "if (self.ss_addEntry) {return(self.ss_addEntry(this))} else {return true;}";
				//qualifiers.put(ObjectKeys.TOOLBAR_QUALIFIER_ONCLICK, onClickPhrase);
				for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
					Definition def = (Definition) defaultEntryDefinitions.get(i);
					AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
					adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
					adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
					adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					String title = NLT.getDef(def.getTitle());
					if (entryToolbar.checkToolbarMenuItem("1_add", "entries", title)) {
						title = title + " (" + String.valueOf(count++) + ")";
					}
					entryToolbar.addToolbarMenuItem("1_add", "entries", title, adapterUrl.toString(), qualifiers);
				}
			} catch (AccessControlException ac) {};
		}
		//The "Administration" menu
		folderToolbar.addToolbarMenu("2_administration", NLT.get("toolbar.manageThisFolder"));
		//Add Folder
		try {
			getFolderModule().checkAccess(folder, "addFolder");
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityIdentifier().getEntityType().name());
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_SUB_FOLDER);
			folderToolbar.addToolbarMenuItem("2_administration", "folders", NLT.get("toolbar.menu.addFolder"), url);
		} catch (AccessControlException ac) {};
		
		//Access control
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityIdentifier().getEntityType().name());
		folderToolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.accessControl"), url);
		//Configuration
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_FORUM);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityIdentifier().getEntityType().name());
		folderToolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url);
		//Definition builder - forms
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.FOLDER_ENTRY));
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityIdentifier().getEntityType().name());
		folderToolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder.folderEntry"), url);
		//Definition builder - workflows
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.ACTION_DEFINITION_BUILDER_DEFINITION_TYPE, String.valueOf(Definition.WORKFLOW));
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityIdentifier().getEntityType().name());
		folderToolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder.workflow"), url);
		
		//Delete binder
		try {
			getBinderModule().checkAccess(folder, "deleteBinder");
			Map qualifiers = new HashMap();
			qualifiers.put("onClick", "return ss_confirmDeleteFolder();");
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_DELETE);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityIdentifier().getEntityType().name());
			folderToolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.delete_folder"), url, qualifiers);		
		} catch (AccessControlException ac) {};

		//Modify binder
		try {
			getBinderModule().checkAccess(folder, "modifyBinder");
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityIdentifier().getEntityType().name());
			folderToolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.modify_folder"), url);		
		} catch (AccessControlException ac) {};
		
		//Move binder
		try {
			getBinderModule().checkAccess(folder, "moveBinder");
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_BINDER_TYPE, folder.getEntityIdentifier().getEntityType().name());
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOVE);
			folderToolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.move_folder"), url);
		} catch (AccessControlException ac) {};

		//set email
		try {
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIG_EMAIL);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			folderToolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configure_folder_email"), url);
		} catch (AccessControlException ac) {};
		
		//	The "Display styles" menu
		entryToolbar.addToolbarMenu("2_display_styles", NLT.get("toolbar.display_styles"));
		//Get the definitions available for use in this folder
		List folderViewDefs = folder.getViewDefinitions();
		for (int i = 0; i < folderViewDefs.size(); i++) {
			Definition def = (Definition)folderViewDefs.get(i);
			//Build a url to switch to this view
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_DEFINITION);
			url.setParameter(WebKeys.URL_BINDER_ID, forumId);
			url.setParameter(WebKeys.URL_VALUE, def.getId());
			entryToolbar.addToolbarMenuItem("2_display_styles", "folderviews", NLT.getDef(def.getTitle()), url);
		}
		//WebDav folder view
		String webdavUrl = SsfsUtil.getLibraryBinderUrl(folder);
		Map qualifiers = new HashMap();
		qualifiers.put("folder", webdavUrl);
		entryToolbar.addToolbarMenuItem("2_display_styles", "folderviews", NLT.get("toolbar.menu.viewASWebDav"), webdavUrl, qualifiers);
		
		//vertical
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
		entryToolbar.addToolbarMenuItem("2_display_styles", "styles", NLT.get("toolbar.menu.display_style_vertical"), url);
		//accessible
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		entryToolbar.addToolbarMenuItem("2_display_styles", "styles", NLT.get("toolbar.menu.display_style_accessible"), url);
		//iframe
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		entryToolbar.addToolbarMenuItem("2_display_styles", "styles", NLT.get("toolbar.menu.display_style_iframe"), url);
		//popup
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
		entryToolbar.addToolbarMenuItem("2_display_styles", "styles", NLT.get("toolbar.menu.display_style_popup"), url);

		//	The "Manage dashboard" menu
		//See if the dashboard is being shown in the definition
		if (DefinitionHelper.checkIfBinderShowingDashboard(folder)) {
			boolean dashboardContentExists = false;
			Map ssDashboard = (Map)model.get(WebKeys.DASHBOARD);
			if (ssDashboard != null && ssDashboard.containsKey(WebKeys.DASHBOARD_COMPONENTS_LIST)) {
				Map dashboard = (Map)ssDashboard.get("dashboard");
				if (dashboard != null) {
					dashboardContentExists = DashboardHelper.checkIfContentExists(dashboard);
				}
			}
			
			//This folder is showing the dashboard
			folderToolbar.addToolbarMenu("3_manageDashboard", NLT.get("toolbar.manageDashboard"));
			qualifiers = new HashMap();
			qualifiers.put("onClick", "ss_addDashboardComponents('" + response.getNamespace() + "_dashboardAddContentPanel');return false;");
			folderToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("toolbar.addPenlets"), "#", qualifiers);

			if (dashboardContentExists) {
				qualifiers = new HashMap();
				qualifiers.put("textId", response.getNamespace() + "_dashboard_menu_controls");
				qualifiers.put("onClick", "ss_toggle_dashboard_hidden_controls('" + response.getNamespace() + "');return false;");
				folderToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.showHiddenControls"), "#", qualifiers);
	
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DASHBOARD_TITLE);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter("_scope", "local");
				folderToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.setTitle"), url);
	
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
				url.setParameter(WebKeys.URL_BINDER_ID, forumId);
				url.setParameter("_scope", "global");
				folderToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.global"), url);
	
				//Check the access rights of the user
				try {
					getBinderModule().checkAccess(folder, "setProperty");
					url = response.createActionURL();
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MODIFY_DASHBOARD);
					url.setParameter(WebKeys.URL_BINDER_ID, forumId);
					url.setParameter("_scope", "binder");
					folderToolbar.addToolbarMenuItem("3_manageDashboard", "dashboard", NLT.get("dashboard.configure.binder"), url);
				} catch(AccessControlException e) {};
	
				qualifiers = new HashMap();
				qualifiers.put("onClick", "ss_showHideAllDashboardComponents(this, '" + 
						response.getNamespace() + "_dashboardComponentCanvas', 'binderId="+
						folder.getId().toString()+"');return false;");
				if (DashboardHelper.checkIfShowingAllComponents(folder)) {
					folderToolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.hideDashboard"), "#", qualifiers);
				} else {
					folderToolbar.addToolbarMenu("4_showHideDashboard", NLT.get("toolbar.showDashboard"), "#", qualifiers);
				}
			}
		}

		//The "Footer" menu
		if (folder.isTop()) {
			//RSS link 
			footerToolbar.addToolbarMenu("RSS", NLT.get("toolbar.menu.rss"), UrlUtil.getFeedURL(request, forumId));
			Subscription sub = getBinderModule().getSubscription(folder.getId());
			qualifiers = new HashMap();
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", false);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST);
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SUBSCRIBE);			
			adapterUrl.setParameter("rn", "ss_randomNumberPlaceholder");			
			qualifiers.put("onClick", "ss_createPopupDiv(this, 'ss_subscription_menu');return false;");
			if (sub == null) {
				footerToolbar.addToolbarMenu("subscribeToFolder", NLT.get("toolbar.menu.subscribeToFolder"), adapterUrl.toString(), qualifiers);	
			} else {
				footerToolbar.addToolbarMenu("subscriptionToFolder", NLT.get("toolbar.menu.subscriptionToFolder"), adapterUrl.toString(), qualifiers);			
			}

		}
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, folder.getEntityIdentifier().getEntityType().toString());
		footerToolbar.addToolbarMenu("permalink", NLT.get("toolbar.menu.folderPermalink"), adapterUrl.toString());
		
		qualifiers = new HashMap();
		qualifiers.put("folder", webdavUrl);
		footerToolbar.addToolbarMenu("webdavUrl", NLT.get("toolbar.menu.webdavUrl"), webdavUrl, qualifiers);

		
		
		model.put(WebKeys.FOLDER_TOOLBAR,  folderToolbar.getToolbar());
		model.put(WebKeys.ENTRY_TOOLBAR,  entryToolbar.getToolbar());
		model.put(WebKeys.FOOTER_TOOLBAR,  footerToolbar.getToolbar());
	}
	

	/* 
	 * getEvents ripples through all the entries in the current entry list, finds their
	 * associated events, checks each event against the session's current calendar view mode
	 * and current selected date, and populates the bean with a list of dates that fall in range.
	 * Returns: side-effects the bean "model" and adds a key called CALENDAR_EVENTDATES which is a
	 * hashMap whose keys are dates and whose values are lists of events that occur on the given day.
	 */
	protected void getEvents(Folder folder, List entrylist, Map model, RenderRequest req, RenderResponse response) {
        User user = RequestContextHolder.getRequestContext().getUser();
		String folderId = folder.getId().toString();
		Iterator entryIterator = entrylist.listIterator();
		PortletSession ps = WebHelper.getRequiredPortletSession(req);
		// view mode is one of day, week, or month
		UserProperties userFolderProperties = (UserProperties) getProfileModule().getUserProperties(
				user.getId(), folder.getId());
		Map userFolderPropertiesMap = userFolderProperties.getProperties();
		String viewMode = WebKeys.CALENDAR_VIEW_WEEK;
		if (userFolderPropertiesMap.containsKey(ObjectKeys.USER_PROPERTY_CALENDAR_VIEWMODE)) {
			viewMode = (String) userFolderPropertiesMap.get(ObjectKeys.USER_PROPERTY_CALENDAR_VIEWMODE);
		}
		model.put(WebKeys.CALENDAR_VIEWMODE, viewMode);
		// currentDate is the date selected by the user; we make sure this date is in view 
		// whatever viewMode is set to
		Date currentDate = (Date) ps.getAttribute(WebKeys.CALENDAR_CURRENT_DATE);
		if (currentDate == null) {
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, new Date());	
			currentDate = new Date();
		} 
		model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
		// urls for common calendar links
		PortletURL url;

		// calendar navigation via nav bar; must be an action so form data is transmitted
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, folderId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_CALENDAR_GOTO_DATE);
		model.put("goto_form_url", url.toString());
		
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, folderId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.URL_VALUE, WebKeys.CALENDAR_VIEW_DAY);
		model.put("set_day_view", url.toString());

		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, folderId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.URL_VALUE, WebKeys.CALENDAR_VIEW_WEEK);
		model.put("set_week_view", url.toString());
		
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, folderId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.URL_VALUE, WebKeys.CALENDAR_VIEW_MONTH);
		model.put("set_month_view", url.toString());
		
		// calculate the start and end of the range as defined by current date and current view
		GregorianCalendar startViewCal = new GregorianCalendar();
		// this trick zeros the low order parts of the time
		startViewCal.setTimeInMillis(0);
		startViewCal.setTime(currentDate);
		GregorianCalendar endViewCal = new GregorianCalendar();
		endViewCal.setTimeInMillis(0);
		endViewCal.setTime(currentDate);
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_DAY)) {
			endViewCal.add(Calendar.DATE, 1);
		} else if (viewMode.equals(WebKeys.CALENDAR_VIEW_WEEK)) {
			startViewCal.set(Calendar.DAY_OF_WEEK, startViewCal.getFirstDayOfWeek());
			endViewCal.setTime(startViewCal.getTime());
			endViewCal.add(Calendar.DATE, 7);
		} else if (viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
			startViewCal.set(Calendar.DAY_OF_MONTH, 1);
			endViewCal.setTime(startViewCal.getTime());
			endViewCal.add(Calendar.MONTH, 1);
		}
		startViewCal.set(Calendar.HOUR_OF_DAY, 0);
		startViewCal.set(Calendar.MINUTE, 0);
		startViewCal.set(Calendar.SECOND, 0);
		endViewCal.set(Calendar.HOUR_OF_DAY, 0);
		endViewCal.set(Calendar.MINUTE, 0);
		endViewCal.set(Calendar.SECOND, 0);
		model.put(WebKeys.CALENDAR_CURRENT_VIEW_STARTDATE, startViewCal.getTime());
		model.put(WebKeys.CALENDAR_CURRENT_VIEW_ENDDATE, endViewCal.getTime());
		// these two longs will be used to determine if an event is in range
		long startMillis = startViewCal.getTime().getTime();
		long endMillis = endViewCal.getTime().getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		HashMap results = new HashMap();  
		while (entryIterator.hasNext()) {
			HashMap e = (HashMap) entryIterator.next();
			//Entry e = (Entry) entryIterator.next();
			
			//Add the modification date as an event
			Date modifyDate = (Date)e.get(EntityIndexUtils.MODIFICATION_DATE_FIELD);
			long thisDateMillis = modifyDate.getTime();
			if (thisDateMillis < endMillis && startMillis < thisDateMillis) {
				Event ev = new Event();
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.setTime(modifyDate);
				ev.setDtStart(gcal);
				ev.setDtEnd(gcal);				
				String dateKey = sdf.format(modifyDate);
				ArrayList entryList;
				// reslist is going to be a list of maps; each map will carry the entry and 
				// also the event that caused this entry to be in range
				ArrayList resList = new ArrayList();
				Map res = new HashMap();
				res.put("event", ev);
				res.put("entry", e);
				entryList  = (ArrayList) results.get(dateKey);
				if (entryList == null) {
					resList.add(res);
				} else {
					resList.addAll(entryList);
					resList.add(res);
				}
				results.put(dateKey, resList);
			}			
			
			//Add the events 
			int count = 0;
			String ec = (String)e.get(EntityIndexUtils.EVENT_COUNT_FIELD);
			if (ec == null || ec.equals("")) ec = "0";
			count = new Integer(ec).intValue();
			// look through the custom attrs of this entry for any of type EVENT
			for (int j = 0; j < count; j++) {
				String name = (String)e.get(EntityIndexUtils.EVENT_FIELD + j);
				Date evStartDate = (Date)e.get(name + BasicIndexUtils.DELIMITER + 
						EntityIndexUtils.EVENT_FIELD_START_DATE);
				Date evEndDate = (Date)e.get(name + BasicIndexUtils.DELIMITER + 
						EntityIndexUtils.EVENT_FIELD_END_DATE);
				Event ev = new Event();
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.setTime(evStartDate);
				ev.setDtStart(gcal);
				gcal.setTime(evEndDate);
				ev.setDtEnd(gcal);				
				thisDateMillis = evStartDate.getTime();
				if (thisDateMillis < endMillis && startMillis < thisDateMillis) {
					String dateKey = sdf.format(evStartDate);
					ArrayList entryList;
					// reslist is going to be a list of maps; each map will carry the entry and 
					// also the event that caused this entry to be in range
					ArrayList resList = new ArrayList();
					Map res = new HashMap();
					res.put("event", ev);
					res.put("entry", e);
					entryList  = (ArrayList) results.get(dateKey);
					if (entryList == null) {
						resList.add(res);
					} else {
						resList.addAll(entryList);
						resList.add(res);
					}
					results.put(dateKey, resList);
				}
			}
		}
		model.put(WebKeys.CALENDAR_EVENTDATES, results);
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_WEEK)) {
			getCalendarViewBean(folder, startViewCal, endViewCal, response, results, viewMode, model);
		}
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_DAY)) {
			
			getCalendarViewBean(folder, startViewCal, endViewCal, response, results, viewMode, model);
		}
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
			
			getCalendarViewBean(folder, startViewCal, endViewCal, response, results, viewMode, model);
		}
	}
	
	/**
	 * populate the bean for weekly and monthly calendar view.
	 * used by getEvents
	 * returns a bean for the entire month, regardless of which view you are in
	 * this bean contains month headers, and a list of weeks. Each week is a map which
	 * contains the week number and a list of days.
	 * Each entry in the dayss list is a daymap, 
	 * a map with info about the day, such as the day of the week, day of the month, and a boolean 
	 * indicating whether the day is today. The daymap also contains a sorted map of event info,
	 * called eventdatamap, whose keys are the event times in millis; this is so that the interator
	 * will return the day's events in chronological order. Each key-value is a list of events 
	 * at that starting time (since you can have multiple events that start at the same time.
	 * and each list entry is a dataMap, which contains both event and entry information for the event 
	 * suitable for displaying on the view calendar page.
	 * 
	 * So the picture looks like this:
	 *  monthBean -- map 
	 *   dayHeaders -- list of day header strings for the month grid
	 *   weekList -- list of week beans
	 *     weekMap -- map
	 *       weekNum -- string
	 *       weekURL -- link to that week
	 *       dayList -- list of days
	 *         dayMap -- map for each day of the week
	 *          day-of-wee  - string
	 *          day-of-month - string
	 *          isToday - Boolean
	 *          dayEvents -- sorted map of event occurrences for the day, keyed by start time
	 *            timeEvents -- list of event occurrences for a specific time
	 *              dataMap -- for each occurrence, a map of stuff about the instance
	 *                 entry
	 *                 event
	 *                 starttime -- string
	 *                 endtime -- string
	 *              
	 */
	private void getCalendarViewBean (Folder folder, Calendar startCal, Calendar endCal, RenderResponse response, Map eventDates, String viewMode, Map model) {
		String folderId = folder.getId().toString();
		HashMap monthBean = new HashMap();
		ArrayList dayheaders = new ArrayList();
		GregorianCalendar loopCal = new GregorianCalendar();
		int j = loopCal.getFirstDayOfWeek();
		for (int i=0; i< 7; i++) {
			dayheaders.add(DateHelper.getDayAbbrevString(j));
			// we don't know for sure that the d-o-w won't wrap, so prepare to wrap it
			if (j++ == 7) {
				j = 0;
			}
		}
		monthBean.put("dayHeaders",dayheaders);
		loopCal.setTime(startCal.getTime());
		List weekList = new ArrayList();
		
		HashMap weekMap = null;
		ArrayList dayList = null;
		// this trick enables the main loop code to start a new week and reset/wrap dayCtr at same time
		int dayCtr = 6;
		// build string for date to stick in url -- note that it cannot contain "/"s so we use "_"
		SimpleDateFormat urldatesdf = new SimpleDateFormat("yyyy_MM_dd");
		String urldatestring;
		String urldatestring2;
		PortletURL url;
		// main loop, loops through days in the range, periodically recycling the week stuff
		while (loopCal.getTime().getTime() < endCal.getTime().getTime()) {
			urldatestring = urldatesdf.format(loopCal.getTime());
			if (++dayCtr > 6) {
				dayCtr = 0;
				// before starting a new week, write out the old one (except first time through)
				if (weekMap != null) {
					weekMap.put("dayList", dayList);
					weekList.add(weekMap);
				}
				weekMap = new HashMap();
				// "w" is format pattern for week number in the year
				SimpleDateFormat sdfweeknum = new SimpleDateFormat("w");
				String wn = sdfweeknum.format(loopCal.getTime());
				weekMap.put("weekNum", wn);

				// before starting a new dayList, check if this is the first week of a month view
				if (dayList == null && viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
					dayList = new ArrayList();
					GregorianCalendar gcal = new GregorianCalendar();
					gcal.setTime(startCal.getTime());
					// when does the week that includes the first day of the month begin?
					gcal.set(Calendar.DAY_OF_WEEK, gcal.getFirstDayOfWeek());
					// note that the week url must include this date instead of the startCal date
					urldatestring = urldatesdf.format(gcal.getTime());
					while (gcal.getTime().getTime() < startCal.getTime().getTime()) {
						// fill in the dayList with blank days
						HashMap emptyDayMap = new HashMap();
						emptyDayMap.put(WebKeys.CALENDAR_DOM, Integer.toString(gcal.get(Calendar.DAY_OF_MONTH)));
						emptyDayMap.put("inView", new Boolean(false));
						// because this loop is adding extra days, we need to build their URLs into the daymap here
						// and we don't want to clobber urldatestring because it's used later for the week URL
						urldatestring2 = urldatesdf.format(gcal.getTime());
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_CALENDAR_DISPLAY_DATE);
						url.setParameter(WebKeys.CALENDAR_URL_VIEWMODE, "day");
						url.setParameter(WebKeys.CALENDAR_URL_NEWVIEWDATE, urldatestring2);
						emptyDayMap.put("dayURL", url.toString());
						dayCtr++;
						dayList.add(emptyDayMap);
						gcal.add(Calendar.DATE, 1);
					}
				} else {
					dayList = new ArrayList();
				}
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_CALENDAR_DISPLAY_DATE);
				url.setParameter(WebKeys.CALENDAR_URL_VIEWMODE, "week");
				url.setParameter(WebKeys.CALENDAR_URL_NEWVIEWDATE, urldatestring);
				weekMap.put("weekURL", url.toString());
			}
			HashMap daymap = new HashMap();
			daymap.put(WebKeys.CALENDAR_DOW, DateHelper.getDayAbbrevString(loopCal.get(Calendar.DAY_OF_WEEK)));
			daymap.put(WebKeys.CALENDAR_DOM, Integer.toString(loopCal.get(Calendar.DAY_OF_MONTH)));
			url = response.createActionURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			url.setParameter(WebKeys.URL_BINDER_ID, folderId);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_CALENDAR_DISPLAY_DATE);
			url.setParameter(WebKeys.CALENDAR_URL_VIEWMODE, "day");
			url.setParameter(WebKeys.CALENDAR_URL_NEWVIEWDATE, urldatestring);
			daymap.put("dayURL", url.toString());

			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String dateKey = sdf.format(loopCal.getTime());
			// is this loop date today? We need to beanify that fact so that the calendar view can shade it
			GregorianCalendar today = new GregorianCalendar();
			if (sdf.format(today.getTime()).equals(dateKey)) {
				daymap.put("isToday", new Boolean(true));
			} else {
				daymap.put("isToday", new Boolean(false));
			}
			daymap.put("inView", new Boolean(true));
			if (eventDates.containsKey(dateKey)) {
				List evList = (List) eventDates.get(dateKey);
				Iterator evIt = evList.iterator();
				TreeMap dayEvents = new TreeMap();
				while (evIt.hasNext()) {
					// thisMap is the next entry, event pair
					HashMap thisMap = (HashMap) evIt.next();
					// dataMap is the map of data for the bean, to be keyed by the time
					HashMap dataMap = new HashMap();
					HashMap e = (HashMap) thisMap.get("entry");
					Event ev = (Event) thisMap.get("event");
					SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
					// we build up the dataMap for this instance
					dataMap.put("entry", e);
					dataMap.put("entry_tostring", e.get(BasicIndexUtils.UID_FIELD).toString());
					dataMap.put(WebKeys.CALENDAR_STARTTIMESTRING, sdf2.format(ev.getDtStart().getTime()));
					dataMap.put(WebKeys.CALENDAR_ENDTIMESTRING, sdf2.format(ev.getDtEnd().getTime()));
					
					// dayEvents is sorted by time in millis; must make a Long object though
					Long millis = new Long(ev.getDtStart().getTime().getTime());
					// must see if this key already has stuff; 
					// build a list of all dataMaps that occur at the same time on this particular day
					ArrayList thisTime = (ArrayList) dayEvents.get(millis);
					ArrayList resList = new ArrayList();
					if (thisTime == null) {
						resList.add(dataMap);
					} else {
						resList.addAll(thisTime);
						resList.add(dataMap);
					}
					dayEvents.put(millis, resList);
				}
				daymap.put(WebKeys.CALENDAR_EVENTDATAMAP, dayEvents);
			}
			
			loopCal.add(Calendar.DATE, 1);
			dayList.add(daymap);
		}
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
			// note that the week url must include this date instead of the startCal date
			urldatestring = urldatesdf.format(loopCal.getTime());
			while (dayCtr++ < 6) {
				// fill in the dayList with blank days
				HashMap emptyDayMap = new HashMap();
				emptyDayMap.put(WebKeys.CALENDAR_DOM, Integer.toString(loopCal.get(Calendar.DAY_OF_MONTH)));
				emptyDayMap.put("inView", new Boolean(false));
				// because this loop is adding extra days, we need to build their URLs into the daymap here
				// and we don't want to clobber urldatestring because it's used later for the week URL
				urldatestring2 = urldatesdf.format(loopCal.getTime());
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
				url.setParameter(WebKeys.URL_BINDER_ID, folderId);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_CALENDAR_DISPLAY_DATE);
				url.setParameter(WebKeys.CALENDAR_URL_VIEWMODE, "day");
				url.setParameter(WebKeys.CALENDAR_URL_NEWVIEWDATE, urldatestring2);
				emptyDayMap.put("dayURL", url.toString());
				dayList.add(emptyDayMap);
				loopCal.add(Calendar.DATE, 1);
			}
		}

		
		weekMap.put("dayList", dayList);
		weekList.add(weekMap);
		monthBean.put("weekList", weekList);
		model.put(WebKeys.CALENDAR_VIEWBEAN, monthBean);
	}

	protected void getBlogEntries(Folder folder, List entrylist, Map model, RenderRequest request, RenderResponse response) {
		Map entries = new TreeMap();
		model.put(WebKeys.BLOG_ENTRIES, entries);
		Iterator entryIterator = entrylist.listIterator();
		while (entryIterator.hasNext()) {
			FolderEntry entry  = (FolderEntry) entryIterator.next();
			Map entryMap = new HashMap();
			entries.put(entry.getId().toString(), entryMap);
			entryMap.put("entry", entry);
			if (DefinitionHelper.getDefinition(entry.getEntryDef(), entryMap, "//item[@name='entryBlogView']") == false) {
				DefinitionHelper.getDefaultEntryView(entry, entryMap, "//item[@name='entryBlogView']");				
			}
			//See if this entry can have replies added
			entryMap.put(WebKeys.REPLY_BLOG_URL, "");
			Definition def = (Definition)entryMap.get(WebKeys.ENTRY_DEFINITION);
			Document defDoc = def.getDefinition();
			List replyStyles = defDoc.getRootElement().selectNodes("properties/property[@name='replyStyle']");
			if (!replyStyles.isEmpty()) {
				try {
					getFolderModule().checkAccess(entry, "addReply");
					String replyStyleId = ((Element)replyStyles.get(0)).attributeValue("value", "");
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
				} catch(Exception e) {}
			}

			entryMap.put(WebKeys.COMMUNITY_TAGS, getFolderModule().getCommunityTags(folder.getId(), entry.getId()));
			entryMap.put(WebKeys.PERSONAL_TAGS, getFolderModule().getPersonalTags(folder.getId(), entry.getId()));
		}
	}
	


}

