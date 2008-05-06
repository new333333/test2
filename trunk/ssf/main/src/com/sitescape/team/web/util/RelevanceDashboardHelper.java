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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.lucene.document.DateTools;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.SharedEntity;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import static com.sitescape.team.module.shared.EntityIndexUtils.*;
import static com.sitescape.team.search.BasicIndexUtils.*;

import com.sitescape.team.module.report.ReportModule.ActivityInfo;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.SearchUtils;

import static com.sitescape.util.search.Restrictions.*;

import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.search.Criteria;
import com.sitescape.util.search.Order;

public class RelevanceDashboardHelper {
	
	public static void setupRelevanceDashboardBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Long binderId, String type, Map model) {
		model.put("ssRDCurrentTab", type);
		User user = RequestContextHolder.getRequestContext().getUser();
		//No dashboard for the guest account
		if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) return;
        
		//Figure out if this is a user workspace; dashboards are relative to the user workspace owner
        if (binderId == null) binderId = user.getWorkspaceId();
		Binder userWorkspace = bs.getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, userWorkspace);
		
		if (ObjectKeys.RELEVANCE_DASHBOARD_PROFILE.equals(type)) {
			if (!setupProfileBeans(bs, request, response, userWorkspace, model)) {
				//The profile isn't being shown in the dashboard, so get the what's new beans instead
				setupWhatsNewDashboardBeans(bs, userWorkspace, model);
			}
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_TASKS_AND_CALENDARS.equals(type)) {
			setupTasksBeans(bs, userWorkspace, model); 
			setupTrackedCalendarBeans(bs, userWorkspace, model);
			setupTrackedItemsBeans(bs, userWorkspace, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_WHATS_NEW.equals(type)) {
			setupWhatsNewDashboardBeans(bs, userWorkspace, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_ACTIVITIES.equals(type)) {
			setupSharedItemsBeans(bs, userWorkspace, model);
			setupActivitiesBean(bs, userWorkspace, model);
			setupVisitorsBeans(bs, userWorkspace, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_VIEWED_ENTRIES.equals(type)) {
			setupViewedEntriesBean(bs, userWorkspace, model);
			setupDocumentsBeans(bs, userWorkspace, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_HIDDEN.equals(type)) {
		}
	}
	
	public static void setupRelevanceDashboardPageBeans(AllModulesInjected bs, Long binderId, 
			String type, Map model) {
		User user = RequestContextHolder.getRequestContext().getUser();
		//No dashboard for the guest account
		if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) return;
        
		//Figure out if this is a user workspace; dashboards are relative to the user workspace owner
        if (binderId == null) binderId = user.getWorkspaceId();
		Binder userWorkspace = bs.getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, userWorkspace);
		
		if (ObjectKeys.RELEVANCE_PAGE_ENTRIES_VIEWED.equals(type)) {
			setupViewedEntriesBean(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_NEW_TRACKED.equals(type)) {
			setupTrackedPlacesBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_NEW_SITE.equals(type)) {
			setupWhatsNewSite(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_ACTIVITIES.equals(type)) {
			setupActivitiesBean(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_DOCS.equals(type)) {
			setupDocumentsBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_HOT.equals(type)) {
			setupWhatsHotBean(bs, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_SHARED.equals(type)) {
			setupSharedItemsBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_TASKS.equals(type)) {
			setupTasksBeans(bs, userWorkspace, model);
		} else if (ObjectKeys.RELEVANCE_PAGE_VISITORS.equals(type)) {
			setupVisitorsBeans(bs, userWorkspace, model);
		}
	}
	
	protected static void setupWhatsNewDashboardBeans(AllModulesInjected bs, Binder binder, Map model) {
		setupTrackedPlacesBeans(bs, binder, model);
		setupTrackedItemsBeans(bs, binder, model);
		setupWhatsHotBean(bs, model);
		setupWhatsNewSite(bs, binder, model);
		setupTrackedPeopleBeans(bs, binder, model);
	}

	protected static boolean setupProfileBeans(AllModulesInjected bs, RenderRequest request, 
			RenderResponse response, Binder binder, Map model) {
		DefinitionHelper.getDefinitions(binder, model);
		//Get the start of the view definition
		Element viewElement = (Element) model.get(WebKeys.CONFIG_ELEMENT);
		Element relevanceElement = (Element) viewElement.selectSingleNode("//item[@name='relevanceDashboard']");
		//See if there is anything to display inside the relevance dashboard definition
		if (!relevanceElement.selectNodes("item").isEmpty()) {
			model.put(WebKeys.CONFIG_ELEMENT_RELEVANCE_DASHBOARD, relevanceElement);
			try {
				if (!model.containsKey(WebKeys.WORKSPACE_BEANS_SETUP)) {
					model.put(WebKeys.WORKSPACE_BEANS_SETUP, true);
					WorkspaceTreeHelper.setupWorkspaceBeans(bs, binder.getId(), request, response, model);
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
		//Prepare for a standard dashboard search operation
		Map options = new HashMap();
		String page = (String) model.get(WebKeys.PAGE_NUMBER);
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.MY_TASKS_PAGE, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));

		setupInitialSearchOptions(options);

		options.put(ObjectKeys.SEARCH_OFFSET, Integer.valueOf(pageStart));
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		java.util.Date now = new java.util.Date();
		java.util.Date future = new java.util.Date(now.getTime() + 14*24*60*60*1000);
		String endDateTarget = DateTools.dateToString(future, DateTools.Resolution.DAY);

		Criteria crit = SearchUtils.tasksForUser(binder.getOwnerId());
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

		model.put(WebKeys.MY_TASKS, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map<String, Map> cacheEntryDef = new HashMap();
		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
	    		DateFormat fmt = DateFormat.getDateInstance(DateFormat.FULL,  Locale.US);
	    		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	    		Date endDate = null;
	    		try {
	    			endDate = fmt.parse((String)entry.get("start_end#EndDate"));
	    		} catch(Exception e) {}

	    		String entryDefId = (String)entry.get(COMMAND_DEFINITION_FIELD);
	    		if (cacheEntryDef.get(entryDefId) == null) {
	    			cacheEntryDef.put(entryDefId, bs.getDefinitionModule().getEntryDefinitionElements(entryDefId));
	    		}
	    		entry.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, cacheEntryDef.get(entryDefId));
				String id = (String)entry.get(EntityIndexUtils.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					Binder place = bs.getBinderModule().getBinder(bId);
					places.put(id, place);
				}
	    	}
    	}
    	model.put(WebKeys.MY_TASKS_FOLDERS, places);
	}
	
	protected static void setupDocumentsBeans(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the documents bean for the documents th the user just authored or modified
		Map options = new HashMap();
		String page = (String) model.get(WebKeys.PAGE_NUMBER);
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
	
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

		model.put(WebKeys.MY_DOCUMENTS, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get(EntityIndexUtils.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					Binder place = bs.getBinderModule().getBinder(bId);
					places.put(id, place);
				}
	    	}
    	}
    	model.put(WebKeys.MY_DOCUMENTS_FOLDERS, places);
	}
	
	protected static void setupTrackedPlacesBeans(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the documents bean for the documents th the user just authored or modified
		Map options = new HashMap();
		String page = (String) model.get(WebKeys.PAGE_NUMBER);
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
		
		List trackedPlaces = SearchUtils.getTrackedPlacesIds(bs, binder);
		if (trackedPlaces.size() > 0) {
			Criteria crit = SearchUtils.entriesForTrackedPlaces(bs, trackedPlaces);
			Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

			model.put(WebKeys.WHATS_NEW_TRACKED_PLACES, results.get(ObjectKeys.SEARCH_ENTRIES));

			Map places = new HashMap();
	    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items != null) {
		    	Iterator it = items.iterator();
		    	while (it.hasNext()) {
		    		Map entry = (Map)it.next();
					String id = (String)entry.get(EntityIndexUtils.BINDER_ID_FIELD);
					if (id != null) {
						Long bId = new Long(id);
						if (!places.containsKey(id)) {
							Binder place = bs.getBinderModule().getBinder(bId);
							places.put(id, place);
						}
					}
		    	}
	    	}
	    	model.put(WebKeys.WHATS_NEW_TRACKED_PLACES_FOLDERS, places);
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
			Criteria crit = SearchUtils.entriesForTrackedCalendars(bs, trackedCalendars);
			Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

			model.put(WebKeys.WHATS_NEW_TRACKED_CALENDARS, results.get(ObjectKeys.SEARCH_ENTRIES));

			Map places = new HashMap();
	    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items != null) {
		    	Iterator it = items.iterator();
		    	while (it.hasNext()) {
		    		Map entry = (Map)it.next();
					String id = (String)entry.get(EntityIndexUtils.BINDER_ID_FIELD);
					if (id != null) {
						Long bId = new Long(id);
						if (!places.containsKey(id)) {
							Binder place = bs.getBinderModule().getBinder(bId);
							places.put(id, place);
						}
					}
		    	}
	    	}
	    	model.put(WebKeys.WHATS_NEW_TRACKED_CALENDAR_FOLDERS, places);
		}
	}
	
	private static void setupTrackedPeopleBeans(AllModulesInjected bs, Binder binder, Map model) {
		//Get the documents bean for the documents th the user just authored or modified
		Map options = new HashMap();
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);
		
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		Criteria crit = SearchUtils.entriesForTrackedPeople(bs, binder);
	
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);
		model.put(WebKeys.WHATS_NEW_TRACKED_PEOPLE, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get(EntityIndexUtils.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					if (!places.containsKey(id)) {
						Binder place = bs.getBinderModule().getBinder(bId);
						places.put(id, place);
					}
				}
	    	}
    	}
    	model.put(WebKeys.WHATS_NEW_TRACKED_PEOPLE_FOLDERS, places);
	}
	
	protected static void setupWhatsNewSite(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the new items I can see
		Map options = new HashMap();
		String page = (String) model.get(WebKeys.PAGE_NUMBER);
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
	
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

		model.put(WebKeys.WHATS_NEW, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get(EntityIndexUtils.BINDER_ID_FIELD);
				if (id != null) {
					Long bId = new Long(id);
					Binder place = bs.getBinderModule().getBinder(bId);
					places.put(id, place);
				}
	    	}
    	}
    	model.put(WebKeys.WHATS_NEW_FOLDERS, places);
	}
	
	private static void setupVisitorsBeans(AllModulesInjected bs, Binder binder, Map model) {
		//Who has visited my page?
		if (binder != null) {
			String page = (String) model.get(WebKeys.PAGE_NUMBER);
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
		if (binder != null) {
			String page = (String) model.get(WebKeys.PAGE_NUMBER);
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
	
	private static void setupActivitiesBean(AllModulesInjected bs, Binder binder, Map model) {
		//What activities have been happening?
		if (binder != null) {
			String page = (String) model.get(WebKeys.PAGE_NUMBER);
			if (page == null || page.equals("")) page = "0";
			Integer pageNumber = Integer.valueOf(page);
			if (pageNumber < 0) pageNumber = 0;
			model.put(WebKeys.ACTIVITIES_PAGE, String.valueOf(pageNumber));
			int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));

			List<Long> trackedPeople = new ArrayList();
			if (!model.containsKey(WebKeys.RELEVANCE_TRACKED_PEOPLE)) {
				UserProperties userForumProperties = bs.getProfileModule().getUserProperties(binder.getOwnerId(), binder.getId());
				Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
				if (relevanceMap != null) {
					trackedPeople = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
				}
			} else {
				SortedSet users = (SortedSet) model.get(WebKeys.RELEVANCE_TRACKED_PEOPLE);
			}
			Long[] userIds = new Long[trackedPeople.size()];
			int count = 0;
			for (Long id:trackedPeople) {
				userIds[count++] = id;
			}
			if (userIds.length > 0) {
				GregorianCalendar start = new GregorianCalendar();
			    //get activities over last 2 weeks
				start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
				List activities = bs.getReportModule().getUsersActivities(binder.getOwnerId(),
						userIds, start.getTime(), new java.util.Date(), 
						pageStart + Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox")));
				if (activities != null && activities.size() > pageStart) {
					model.put(WebKeys.ACTIVITIES, activities.subList(pageStart, activities.size()));
				}
			}
		}
	}
	
	private static void setupTrackedItemsBeans(AllModulesInjected bs, Binder binder, Map model) {
		if (binder != null && EntityType.workspace.equals(binder.getEntityType()) && 
				binder.getDefinitionType() != null && Definition.USER_WORKSPACE_VIEW == binder.getDefinitionType().intValue()) {
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
				Definition.USER_WORKSPACE_VIEW == binder.getDefinitionType().intValue()) {
			String page = (String) model.get(WebKeys.PAGE_NUMBER);
			if (page == null || page.equals("")) page = "0";
			Integer pageNumber = Integer.valueOf(page);
			if (pageNumber < 0) pageNumber = 0;
			model.put(WebKeys.RELEVANCE_SHARED_ENTITIES_PAGE, String.valueOf(pageNumber));
			int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));

			GregorianCalendar since = new GregorianCalendar();
			since.add(Calendar.WEEK_OF_MONTH, -2);
			List<SharedEntity>sharedEntities = bs.getProfileModule().getShares(binder.getOwnerId(), since.getTime());
			if (sharedEntities != null && sharedEntities.size() > pageStart) {
				model.put(WebKeys.RELEVANCE_SHARED_ENTITIES, sharedEntities.subList(pageStart, sharedEntities.size()));
			}
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
		Collection myTeams = bs.getBinderModule().getTeamMemberships(user.getId());
		model.put(WebKeys.MY_TEAMS, myTeams);
	}
	
	public static void setupWhatsHotBean(AllModulesInjected bs, Map model) {
		String page = (String) model.get(WebKeys.PAGE_NUMBER);
		if (page == null || page.equals("")) page = "0";
		Integer pageNumber = Integer.valueOf(page);
		if (pageNumber < 0) pageNumber = 0;
		model.put(WebKeys.WHATS_HOT_PAGE, String.valueOf(pageNumber));
		int pageStart = pageNumber * Integer.valueOf(SPropsUtil.getString("relevance.entriesPerBox"));

		List hotEntries = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		GregorianCalendar start = new GregorianCalendar();
		//get users over last 2 weeks
		start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
		Object[] entityTypes = new Object[] {EntityType.folderEntry.name()};
		Collection<ActivityInfo> results = bs.getReportModule().culaEsCaliente(AuditType.view, 
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
	}
	
}
