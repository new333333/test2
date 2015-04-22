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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Element;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.AbstractIntervalView;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.calendar.OneDayView;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.SharedEntity;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

import static org.kablink.util.search.Constants.*;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "deprecation"})
public class RelevanceDashboardHelper {
	public static void setupRelevanceDashboardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long binderId, String type, Map model) {
		model.put("ssRDCurrentTab", type);
		String page = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PAGE, "0");
		model.put(WebKeys.PAGE_NUMBER, page);
		String type2 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE2, "");
		String type3 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE3, "");
		model.put(WebKeys.TYPE2, type2);
		model.put(WebKeys.TYPE3, type3);
		
		User user = RequestContextHolder.getRequestContext().getUser();
        
		//See if this is a new "type" setting. If so, remember it
		if (binderId != null) {
			UserProperties userForumProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			String relevanceTab = (String)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_TAB);
			if ((null != relevanceTab) && relevanceTab.equalsIgnoreCase(ObjectKeys.RELEVANCE_DASHBOARD_MINIBLOGS)) {
				// Bug 876024:  Got rid of the 'Mini-Blogs and Shared
				// Items' tab in Vibe Hudson since it was showing the
				// new shares.
				relevanceTab = null;	// Forces default below.
			}
			if (!(MiscUtil.hasString(relevanceTab))) {
				if (Utils.checkIfFilr()) {
					relevanceTab = ObjectKeys.RELEVANCE_DASHBOARD_FILESPACES;  //not implemented
					relevanceTab = ObjectKeys.RELEVANCE_DASHBOARD_OVERVIEW;
				} else {
					relevanceTab = ObjectKeys.RELEVANCE_DASHBOARD_OVERVIEW;
				}
			}
			//Make sure the selected tab is legal in filr
			if (Utils.checkIfFilr()) {
				if (relevanceTab.equals(ObjectKeys.RELEVANCE_DASHBOARD_TASKS_AND_CALENDARS) ||
						relevanceTab.equals(ObjectKeys.RELEVANCE_DASHBOARD_MINIBLOGS) ||
						relevanceTab.equals(ObjectKeys.RELEVANCE_DASHBOARD_OVERVIEW)) {
					//This tab is not shown, revert back to the filespaces tab
					relevanceTab = ObjectKeys.RELEVANCE_DASHBOARD_FILESPACES;  //not implemented
					relevanceTab = ObjectKeys.RELEVANCE_DASHBOARD_OVERVIEW;
				}
			}
			if (!type.equals("") && !type.equals(relevanceTab)) {
				//Remember the last tab
				bs.getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_RELEVANCE_TAB, type);
			} else if (type.equals("")) {
				type = relevanceTab;
				model.put("ssRDCurrentTab", type);
			}
		}
		
		//Figure out if this is a user workspace; dashboards are relative to the user workspace owner
        if (binderId == null) binderId = user.getWorkspaceId();
		Binder userWorkspace = bs.getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, userWorkspace);
		
		if (!model.containsKey(WebKeys.CONFIG_ELEMENT)) 
			DefinitionHelper.getDefinitions(userWorkspace, model);
		//Get the start of the view definition
		Element viewElement = (Element) model.get(WebKeys.CONFIG_ELEMENT);
		Element relevanceElement = (Element) viewElement.selectSingleNode("//item[@name='relevanceDashboard']");
		//See if there is anything to display inside the relevance dashboard definition
		if (relevanceElement!=null && !relevanceElement.selectNodes("item").isEmpty()) {
			model.put(WebKeys.CONFIG_ELEMENT_RELEVANCE_DASHBOARD, relevanceElement);
		}

		if (ObjectKeys.RELEVANCE_DASHBOARD_TASKS_AND_CALENDARS.equals(type)) {
			setupTasksBeans(bs, userWorkspace, model); 
			setupTrackedCalendarBeans(bs, userWorkspace, model);
			setupTrackedItemsBeans(bs, userWorkspace, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_WHATS_NEW.equals(type)) { 
			setupWhatsNewDashboardBeans(bs, request, response, userWorkspace, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_ACTIVITIES.equals(type)) {
			setupViewedEntriesBean(bs, userWorkspace, model);
			setupDocumentsBeans(bs, userWorkspace, model);
			setupVisitorsBeans(bs, userWorkspace, model);
			setupMyTagsBeans(bs, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_MINIBLOGS.equals(type)) {
			setupSharedItemsBeans(bs, userWorkspace, model);
			setupMiniblogsBean(bs, userWorkspace, model);
		
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_FILESPACES.equals(type)) {
			
		} else if ( ObjectKeys.RELEVANCE_DASHBOARD_OVERVIEW.equalsIgnoreCase( type ) ) {
		}
	}
	
	public static void setupRelevanceDashboardPageBeans(AllModulesInjected bs, Long binderId, 
			String type, Map model) {
		User user = RequestContextHolder.getRequestContext().getUser();
        
		//Figure out if this is a user workspace; dashboards are relative to the user workspace owner
        if (binderId == null) binderId = user.getWorkspaceId();
		Binder userWorkspace = bs.getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, userWorkspace);
		
		if (ObjectKeys.RELEVANCE_PAGE_ENTRIES_VIEWED.equals(type)) {
			if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) 
				setupViewedEntriesBean(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_NEW_TRACKED.equals(type)) {
			if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) 
			 setupTrackedPlacesBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_NEW_TEAMS.equals(type)) {
			if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) 
				setupWhatsNewTeamsBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_NEW_SITE.equals(type)) {
			setupWhatsNewSite(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_MINIBLOGS.equals(type)) {
			if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) 
				setupMiniblogsBean(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_DOCS.equals(type)) {
			setupDocumentsBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_HOT.equals(type)) {
			setupWhatsHotBean(bs, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_SHARED.equals(type)) {
			if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) 
				setupSharedItemsBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_TASKS.equals(type)) {
			if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) 
				setupTasksBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_VISITORS.equals(type)) {
			if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) 
				setupVisitorsBeans(bs, userWorkspace, model);
		}
	}
	
	protected static void setupWhatsNewDashboardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Binder binder, Map model) {
		//See if this is a new "type" setting. If so, remember it
		User user = RequestContextHolder.getRequestContext().getUser();
		String type3 = PortletRequestUtils.getStringParameter(request, WebKeys.URL_TYPE3, "");
		model.put(WebKeys.TYPE3, type3);
		Long binderId = binder.getId();
		if (binderId != null) {
			UserProperties userForumProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			String savedType3 = (String)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_TAB_WHATS_NEW_TYPE);
			if (savedType3 == null) savedType3 = "";
			if (!type3.equals("") && !type3.equals(savedType3)) {
				//Remember the last type of results
				bs.getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_RELEVANCE_TAB_WHATS_NEW_TYPE, type3);
			} else if (type3.equals("")) {
				type3 = savedType3;
				model.put(WebKeys.TYPE3, type3);
			}
		}
		if (type3.equals("") && ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			type3 = ObjectKeys.RELEVANCE_DASHBOARD_WHATS_NEW_VIEW_DEFAULT_GUEST;
		} else if (type3.equals("") && !ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
			type3 = ObjectKeys.RELEVANCE_DASHBOARD_WHATS_NEW_VIEW_DEFAULT;
		}
		if (type3.equals(ObjectKeys.RELEVANCE_DASHBOARD_WHATS_NEW_VIEW_TEAMS)) {
			setupWhatsNewTeamsBeans(bs, binder, model);
		} else if (type3.equals(ObjectKeys.RELEVANCE_DASHBOARD_WHATS_NEW_VIEW_TRACKED)) {
			setupTrackedPlacesBeans(bs, binder, model);
			setupTrackedItemsBeans(bs, binder, model);
		} else if (type3.equals(ObjectKeys.RELEVANCE_DASHBOARD_WHATS_NEW_VIEW_SITE)) {
			setupWhatsHotBean(bs, model);
			setupWhatsNewSite(bs, binder, model);
		}
	}
	
	protected static boolean setupProfileBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Binder binder, Map model) {
		if (model.containsKey(WebKeys.CONFIG_ELEMENT_RELEVANCE_DASHBOARD)) {
			try {
				if (!model.containsKey(WebKeys.WORKSPACE_BEANS_SETUP)) {
					model.put(WebKeys.WORKSPACE_BEANS_SETUP, true);
					WorkspaceTreeHelper.setupWorkspaceBeans(bs, binder.getId(), request, response, model, false);
				}
			} catch(Exception e) {}
		} else {
			//There is no profile display, so load the "what's new" beans
			return false;
		}
		return true;
}
	
	protected static void setupTasksBeans(AllModulesInjected bs, Binder binder, Map model) {
		//Get the tasks bean
		//See if this is a new "type" setting. If so, remember it
		User user = RequestContextHolder.getRequestContext().getUser();
		String type3 = (String) model.get(WebKeys.TYPE3);
		if (type3 == null) type3 = "";
		Long binderId = binder.getId();
		if (binderId != null) {
			UserProperties userForumProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			String savedType3 = (String)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_TAB_TASKS_TYPE);
			if (savedType3 == null) savedType3 = "";
			if (!type3.equals("") && !type3.equals(savedType3)) {
				//Remember the last type of results
				bs.getProfileModule().setUserProperty(user.getId(), binderId, ObjectKeys.USER_PROPERTY_RELEVANCE_TAB_TASKS_TYPE, type3);
			} else if (type3.equals("")) {
				type3 = savedType3;
			}
		}
		if (type3.equals("")) type3 = ObjectKeys.RELEVANCE_DASHBOARD_TASKS_VIEW_DEFAULT;
		model.put(WebKeys.TYPE3, type3);

		//Prepare for a standard dashboard search operation
		Map options = new HashMap();
		String page = "0";
		page = (String) model.get(WebKeys.PAGE_NUMBER);
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.MY_TASKS_PAGE, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));

		setupInitialSearchOptions(options);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
		BinderModule binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");

		List groups = new ArrayList();
		List groupsS = new ArrayList();
		List teams = new ArrayList();
		
		groups.addAll(profileDao.getApplicationLevelGroupMembership(binder.getOwnerId(), binder.getZoneId()));
		Iterator itG = groups.iterator();
		while (itG.hasNext()) {
			groupsS.add(itG.next().toString());
		}
		Iterator teamMembershipsIt = binderModule.getTeamMemberships(binder.getOwnerId(), org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD)).iterator();
		while (teamMembershipsIt.hasNext()) {
			teams.add(((Map)teamMembershipsIt.next()).get(Constants.DOCID_FIELD));
		}
		
		DateTime today = (new DateMidnight(DateTimeZone.forTimeZone(user.getTimeZone()))).toDateTime();
		DateTime future = today.plusWeeks(SPropsUtil.getInt("relevance.tasks2WeeksAhead")).plusDays(1);
		DateTime fromDate = today.minusMonths(SPropsUtil.getInt("relevance.tasksFromMonthsAgo"));
		
		Criteria crit;
		Map results;
		if (model.containsKey(WebKeys.TYPE3) && model.get(WebKeys.TYPE3).equals("all")) {
			//Get all of the tasks
			crit = SearchUtils.tasksForUser(binder.getOwnerId(), 
					(String[])groupsS.toArray(new String[groupsS.size()]), 
					(String[])teams.toArray(new String[teams.size()]));
			results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);
		} else {
			//Get the tasks due shortly
			crit = SearchUtils.tasksForUser(binder.getOwnerId(), 
													(String[])groupsS.toArray(new String[groupsS.size()]), 
													(String[])teams.toArray(new String[teams.size()]),
													fromDate.toDate(),
													future.toDate());
			results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);
		}

		model.put(WebKeys.MY_TASKS, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map<String, Map> cacheEntryDef = new HashMap();
		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
	    		String entryDefId = (String)entry.get(COMMAND_DEFINITION_FIELD);
	    		if (cacheEntryDef.get(entryDefId) == null) {
	    			cacheEntryDef.put(entryDefId, bs.getDefinitionModule().getEntryDefinitionElements(entryDefId));
	    		}
	    		entry.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, cacheEntryDef.get(entryDefId));
				String id = (String)entry.get(Constants.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					try {
						Binder place = bs.getBinderModule().getBinder(bId);
						places.put(id, place);
					} catch(Exception e) {}
				}
	    	}
    	}
    	model.put(WebKeys.MY_TASKS_FOLDERS, places);
	}
	
	protected static void setupDocumentsBeans(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the documents bean for the documents th the user just authored or modified
		Map options = new HashMap();
		String page = "0";
		page = (String) model.get(WebKeys.PAGE_NUMBER);
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.MY_DOCUMENTS_PAGE, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		Criteria crit = SearchUtils.entriesForUser(binder.getOwnerId());
	
		Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);

		model.put(WebKeys.MY_DOCUMENTS, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get(Constants.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					try {
						Binder place = bs.getBinderModule().getBinder(bId);
						places.put(id, place);
					} catch(Exception e) {}
				}
	    	}
    	}
    	model.put(WebKeys.MY_DOCUMENTS_FOLDERS, places);
	}
	
	protected static void setupTrackedPlacesBeans(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the documents bean for the documents that the user is tracking
		Map options = new HashMap();
		String page = "0";
		page = (String) model.get(WebKeys.PAGE_NUMBER);
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.TRACKED_PLACES_PAGE, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		List trackedPlaces = new ArrayList();
		List<String> trackedPeopleIds = new ArrayList<String>();
		try {
			trackedPlaces = SearchUtils.getTrackedPlacesIds(bs, binder);
			trackedPeopleIds = SearchUtils.getTrackedPeopleIds(bs, binder);
		} catch(NoUserByTheIdException e) {}
		if (trackedPlaces.size() > 0 || trackedPeopleIds.size() > 0) {
			Criteria crit = SearchUtils.entriesForTrackedPlacesAndPeople(bs, trackedPlaces, trackedPeopleIds);
			Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);

			model.put(WebKeys.WHATS_NEW_TRACKED_PLACES, results.get(ObjectKeys.SEARCH_ENTRIES));

			Map places = new HashMap();
	    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items != null) {
		    	Iterator it = items.iterator();
		    	while (it.hasNext()) {
		    		Map entry = (Map)it.next();
					String id = (String)entry.get(Constants.BINDER_ID_FIELD);
					if (id != null) {
						Long bId = new Long(id);
						if (!places.containsKey(id)) {
							try {
								Binder place = bs.getBinderModule().getBinder(bId);
								places.put(id, place);
							} catch(Exception e) {}
						}
					}
		    	}
	    	}
	    	model.put(WebKeys.WHATS_NEW_TRACKED_PLACES_FOLDERS, places);
		}
	}
	
	protected static void setupWhatsNewTeamsBeans(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the documents bean for the documents that the user is tracking
		Map options = new HashMap();
		String page = "0";
		User user = RequestContextHolder.getRequestContext().getUser();
		page = (String) model.get(WebKeys.PAGE_NUMBER);
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.TRACKED_TEAMS_PAGE, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		Collection myTeams = bs.getBinderModule().getTeamMemberships(user.getId(), org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
		List teamIds = new ArrayList();
		Iterator itTeams = myTeams.iterator();
		while (itTeams.hasNext()) {
			Map team = (Map)itTeams.next();
			teamIds.add(team.get(Constants.DOCID_FIELD));
		}
		if (myTeams.size() > 0) {
			Criteria crit = SearchUtils.entriesForTrackedPlaces(bs, teamIds);
			Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);

			model.put(WebKeys.WHATS_NEW_TEAM_PLACES, results.get(ObjectKeys.SEARCH_ENTRIES));

			Map places = new HashMap();
	    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items != null) {
		    	Iterator it = items.iterator();
		    	while (it.hasNext()) {
		    		Map entry = (Map)it.next();
					String id = (String)entry.get(Constants.BINDER_ID_FIELD);
					if (id != null) {
						Long bId = new Long(id);
						if (!places.containsKey(id)) {
							try {
								Binder place = bs.getBinderModule().getBinder(bId);
								places.put(id, place);
							} catch(Exception e) {}
						}
					}
		    	}
	    	}
	    	model.put(WebKeys.WHATS_NEW_TEAM_PLACES_FOLDERS, places);
}
	}
	
	protected static void setupTrackedCalendarBeans(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the calendar bean
		Map options = new HashMap();
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);

		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		List trackedCalendars = SearchUtils.getTrackedCalendarIds(bs, binder);
		if (trackedCalendars.size() > 0) {
			AbstractIntervalView calendarInterval = new OneDayView(new Date());
			AbstractIntervalView.VisibleIntervalFormattedDates interval = calendarInterval.getVisibleIntervalInTZ();
			Criteria crit = SearchUtils.entriesForTrackedCalendars(bs, trackedCalendars, interval.startDate, interval.endDate);
			Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);

			Date today = new Date();
			model.put(WebKeys.WHATS_NEW_TRACKED_CALENDARS, results.get(ObjectKeys.SEARCH_ENTRIES));
			
			model.putAll(EventsViewHelper.getEventsBeans((List) results.get(ObjectKeys.SEARCH_ENTRIES), calendarInterval, EventsViewHelper.EVENT_TYPE_EVENT, EventsViewHelper.DAY_VIEW_TYPE_FULL, true));
			model.put(WebKeys.CALENDAR_GRID_TYPE, EventsViewHelper.GRID_DAY);
			model.put(WebKeys.CALENDAR_GRID_SIZE, 1);
			model.put(WebKeys.CALENDAR_CURRENT_DATE, today);
			
			
			
			Map places = new HashMap();
	    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items != null) {
		    	Iterator it = items.iterator();
		    	while (it.hasNext()) {
		    		Map entry = (Map)it.next();
					String id = (String)entry.get(Constants.BINDER_ID_FIELD);
					if (id != null) {
						Long bId = new Long(id);
						if (!places.containsKey(id)) {
							try {
								Binder place = bs.getBinderModule().getBinder(bId);
								places.put(id, place);
							} catch(Exception e) {}
						}
					}
		    	}
	    	}
	    	model.put(WebKeys.WHATS_NEW_TRACKED_CALENDAR_FOLDERS, places);
		}
	}
	
	protected static void setupWhatsNewSite(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the new items I can see
		Map options = new HashMap();
		String page = "0";
		page = (String) model.get(WebKeys.PAGE_NUMBER);
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.TRACKED_SITE_PAGE, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		Criteria crit = SearchUtils.newEntries();
	
		Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null);

		model.put(WebKeys.WHATS_NEW, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get(Constants.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					try {
						Binder place = bs.getBinderModule().getBinder(bId);
						places.put(id, place);
					} catch(Exception e) {}
				}
	    	}
    	}
    	model.put(WebKeys.WHATS_NEW_FOLDERS, places);
	}
	
	private static void setupVisitorsBeans(AllModulesInjected bs, Binder binder, Map model) {
		//Who has visited my page?
		if (binder != null) {
			String page = "0";
			page = (String) model.get(WebKeys.PAGE_NUMBER);
			if (page == null || page.equals("")) page = "0";
			Integer pageNumber = Integer.valueOf(page);
			if (pageNumber < 0) pageNumber = 0;
			model.put(WebKeys.VISITORS_PAGE, String.valueOf(pageNumber));
			int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
			GregorianCalendar start = new GregorianCalendar();
		    //get users over last 2 weeks
		   start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
		   List users = bs.getReportModule().getUsersActivity(binder, AuditType.view, 
				   start.getTime(), new java.util.Date());
			if (users != null && users.size() > pageStart) {
				model.put(WebKeys.VISITORS, users.subList(pageStart, users.size()));
			}
		}
	}
	
	private static void setupViewedEntriesBean(AllModulesInjected bs, Binder binder, Map model) {
		//What entries have I visited?
		User user = RequestContextHolder.getRequestContext().getUser();
		if (binder != null && user.getId().equals(binder.getOwnerId())) {
			String page = "0";
			page = (String) model.get(WebKeys.PAGE_NUMBER);
			if (page == null || page.equals("")) page = "0";
			Integer pageNumber = Integer.valueOf(page);
			if (pageNumber < 0) pageNumber = 0;
			model.put(WebKeys.ENTRIES_VIEWED_PAGE, String.valueOf(pageNumber));
			int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
			GregorianCalendar start = new GregorianCalendar();
		    //get entries viewed over last 2 weeks
			start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
			List entries = bs.getReportModule().getEntriesViewed(binder.getOwnerId(),
					start.getTime(), new java.util.Date(), 
					(pageNumber + 1) * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox")));
			if (entries != null && entries.size() > pageStart) {
				model.put(WebKeys.ENTRIES_VIEWED, entries.subList(pageStart, entries.size()));
			}
		}
	}
	
	public static List<Long> setupMiniblogsBean(AllModulesInjected bs, Binder binder, Map model) {
		//What activities have been happening?
		List<Long> trackedPeople = new ArrayList();
		if (binder != null) {
			String page = "0";
			page = (String) model.get(WebKeys.PAGE_NUMBER);
			if (page == null || page.equals("")) page = "0";
			Integer pageNumber = Integer.valueOf(page);
			if (pageNumber < 0) pageNumber = 0;
			model.put(WebKeys.ACTIVITIES_PAGE, String.valueOf(pageNumber));
			int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));

			if (!model.containsKey(WebKeys.RELEVANCE_TRACKED_PEOPLE)) {
				try {
					UserProperties userForumProperties = bs.getProfileModule().getUserProperties(binder.getOwnerId(), binder.getId());
					Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
					if (relevanceMap != null) {
						trackedPeople = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
					}
				} catch(Exception e) {}
			}
			Long[] userIds = new Long[trackedPeople.size()];
			int count = 0;
			for (Long id:trackedPeople) {
				userIds[count++] = id;
			}
			model.put(WebKeys.SEARCH_TOTAL_HITS, 0);
			if (userIds.length > 0) {
				GregorianCalendar start = new GregorianCalendar();
			    //get activities over last 2 weeks
				start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
				int pageSize = Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));
				List activities = bs.getReportModule().getUsersStatuses(userIds, start.getTime(), 
						new java.util.Date(), 
						pageStart + pageSize + 1);
				if (activities == null) activities = new ArrayList();
				model.put(WebKeys.SEARCH_TOTAL_HITS, activities.size());
				if (activities.size() > pageStart) {
					activities = activities.subList(pageStart, activities.size());
				}
				if (activities.size() > pageSize) {
					//There are more pages after this one; just show this page
					activities = activities.subList(0, pageSize);
				}
				model.put(WebKeys.ACTIVITIES, activities);
			}
		}
		return trackedPeople;
	}
	
	private static void setupTrackedItemsBeans(AllModulesInjected bs, Binder binder, Map model) {
		if (binder != null && EntityType.workspace.equals(binder.getEntityType()) && 
				binder.getDefinitionType() != null && 
				(Definition.USER_WORKSPACE_VIEW == binder.getDefinitionType().intValue() ||
						Definition.EXTERNAL_USER_WORKSPACE_VIEW == binder.getDefinitionType().intValue())) {
			UserProperties userForumProperties = bs.getProfileModule().getUserProperties(binder.getOwnerId(), binder.getId());
			Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
			if (relevanceMap != null) {
				List trackedBinders = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_BINDERS);
				if (trackedBinders != null) {
					SortedSet binders = bs.getBinderModule().getBinders(trackedBinders);
					model.put(WebKeys.RELEVANCE_TRACKED_BINDERS, binders);
				}
				List trackedCalendars = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_CALENDARS);
				if (trackedCalendars != null) {
					SortedSet binders = bs.getBinderModule().getBinders(trackedCalendars);
					model.put(WebKeys.RELEVANCE_TRACKED_CALENDARS, binders);
				}
				List trackedPeople = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
				if (trackedPeople != null) {
					SortedSet users = bs.getProfileModule().getUsers(trackedPeople);
					model.put(WebKeys.RELEVANCE_TRACKED_PEOPLE, users);
				}
			}
		}
	}
	
	private static void setupSharedItemsBeans(AllModulesInjected bs, Binder binder, Map model) {
		//What is this user workspace tracking?
		if (binder != null && EntityType.workspace.equals(binder.getEntityType()) && 
				binder.getDefinitionType() != null && 
				(Definition.USER_WORKSPACE_VIEW == binder.getDefinitionType().intValue() ||
					Definition.EXTERNAL_USER_WORKSPACE_VIEW == binder.getDefinitionType().intValue())) {
			String page = "0";
			page = (String) model.get(WebKeys.PAGE_NUMBER);
			if (page == null || page.equals("")) page = "0";
			Integer pageNumber = Integer.valueOf(page);
			if (pageNumber < 0) pageNumber = 0;
			model.put(WebKeys.RELEVANCE_SHARED_ENTITIES_PAGE, String.valueOf(pageNumber));
			int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));

			GregorianCalendar since = new GregorianCalendar();
			since.add(Calendar.WEEK_OF_MONTH, -2);
			try {
				List<SharedEntity>sharedEntities = bs.getProfileModule().getShares(binder.getOwnerId(), since.getTime());
				if (sharedEntities != null && sharedEntities.size() > pageStart) {
					model.put(WebKeys.RELEVANCE_SHARED_ENTITIES, sharedEntities.subList(pageStart, sharedEntities.size()));
				}
			} catch(Exception e) {}
		}
	}
    
	private static void setupInitialSearchOptions(Map options) {
		//Getting the entries per page from the user properties
		String entriesPerPage = SPropsUtil.getString("relevance.entriesPerBox");
		options.put(ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer(entriesPerPage));
		
		Integer searchUserOffset = 0;
			
		// TODO - implement it better(?) this stuff is to get from lucene proper entries,  
		// to get the ~ proper rankings we get from lucene MoreEntriesCounter more hits as max on page
		Integer searchLuceneOffset = 0;
		options.put(ObjectKeys.SEARCH_OFFSET, searchLuceneOffset);
		options.put(ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset);
		
		Integer maxHits = new Integer(entriesPerPage);
		options.put(ObjectKeys.SEARCH_USER_MAX_HITS, maxHits);
		
		Integer summaryWords = new Integer(20);
		options.put(WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords);
		
		Integer intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
		if (searchUserOffset > 0) {
			intInternalNumberOfRecordsToBeFetched+=searchUserOffset;
		}
		options.put(ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched);
	}
	
	public static void setupMyTeamsBeans(AllModulesInjected bs, Map model) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Collection myTeams = bs.getBinderModule().getTeamMemberships(user.getId(), null);
		model.put(WebKeys.MY_TEAMS, myTeams);
	}
	
	public static void setupWhatsHotBean(AllModulesInjected bs, Map model) {
		/** This function has been removed because it did not perfrom well
		String page = "0";
		page = (String) model.get(WebKeys.PAGE_NUMBER);
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.WHATS_HOT_PAGE, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));

		List hotEntries = new ArrayList();
		GregorianCalendar start = new GregorianCalendar();
		//get users over last 2 weeks
		start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
		Object[] entityTypes = new Object[] {EntityType.folderEntry.name()};
		Collection<ActivityInfo> results = bs.getReportModule().getActivity(AuditType.view, 
				start.getTime(), new java.util.Date(), entityTypes, 
				pageStart + Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox")));
		for(ActivityInfo info : results) {
			if (info.getWhoOrWhat().getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry entry = (FolderEntry) info.getWhoOrWhat();
				hotEntries.add(entry);
			}
		}
		if (hotEntries != null && hotEntries.size() > pageStart) {
			model.put(WebKeys.WHATS_HOT, hotEntries.subList(pageStart, hotEntries.size()));
		}
		*/
	}
	
	public static void setupMyTagsBeans(AllModulesInjected bs, Map model) {
		Collection<Map> myTags;
		if(SPropsUtil.getBoolean("setupMyTagsBeans.use.index", false)) {
			String findType = WebKeys.FIND_TYPE_PERSONAL_TAGS;
			myTags = bs.getBinderModule().getSearchTags("", findType);			
		}
		else {
			List<Tag> tagObjs = getCoreDao().loadPersonalTagsByOwner(RequestContextHolder.getRequestContext().getUser().getEntityIdentifier());
			myTags = new ArrayList<Map>();
			List myTagsList = new ArrayList();
			if (tagObjs != null) {
				for(Tag tagObj:tagObjs) {
					HashMap tag = new HashMap();
					tag.put(WebKeys.TAG_NAME, tagObj.getName());
					if (!myTagsList.contains(tagObj.getName())) {
						myTags.add(tag);
					}
					myTagsList.add(tagObj.getName());
				}
			}			
		}
		model.put(WebKeys.MY_TAGS, myTags);
	}
	public static void setupMyTagFreqBeans(AllModulesInjected bs, Map model) {
		String findType = WebKeys.FIND_TYPE_PERSONAL_TAGS;
		Collection myTags = bs.getBinderModule().getSearchTagsWithFrequencies("", findType);
		model.put(WebKeys.MY_TAGS_WITH_FREQ, myTags);
	}
	
	private static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
}
