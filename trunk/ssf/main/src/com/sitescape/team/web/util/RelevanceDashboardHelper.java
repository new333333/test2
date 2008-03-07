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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.SharedEntity;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import static com.sitescape.team.module.shared.EntityIndexUtils.*;
import static com.sitescape.team.search.BasicIndexUtils.*;

import com.sitescape.team.module.report.ReportModule.ActivityInfo;
import com.sitescape.team.search.Criteria;

import static com.sitescape.team.search.Restrictions.*;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;

public class RelevanceDashboardHelper {
	
	public static void setupRelevanceDashboardBeans(AllModulesInjected bs, Long binderId, 
			String type, Map model) {
		User user = RequestContextHolder.getRequestContext().getUser();
		//No dashboard for the guest account
		if (ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) return;
        
		//Figure out if this is a user workspace; dashboards are relative to the user workspace owner
        if (binderId == null) binderId = user.getWorkspaceId();
		Binder userWorkspace = bs.getBinderModule().getBinder(binderId);
		model.put(WebKeys.BINDER, userWorkspace);
		
		if (ObjectKeys.RELEVANCE_DASHBOARD_DASHBOARD.equals(type)) {
			setupTasksBeans(bs, userWorkspace, model);
			setupDocumentsBeans(bs, userWorkspace, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_NETWORK_DASHBOARD.equals(type)) {
			setupTrackedPlacesBeans(bs, userWorkspace, model);
			setupTrackedPeopleBeans(bs, userWorkspace, model);
			setupSharedItemsBeans(bs, userWorkspace, model);
			setupWhatsHotBean(bs, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_VISITORS.equals(type)) {
			setupVisitorsBeans(bs, userWorkspace, model);
			
		} else if (ObjectKeys.RELEVANCE_DASHBOARD_TRACKED_ITEMS.equals(type)) {
			setupTrackedItemsBeans(bs, userWorkspace, model);
		}
	}
	
	protected static void setupTasksBeans(AllModulesInjected bs, Binder binder, Map model) {		
		//Get the tasks bean
		//Prepare for a standard dashboard search operation
		Map options = new HashMap();
		setupInitialSearchOptions(options);
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();

		Criteria crit = new Criteria();
		crit.add(eq(FAMILY_FIELD,FAMILY_FIELD_TASK))
			.add(eq(DOC_TYPE_FIELD, DOC_TYPE_ENTRY))
			.add(eq(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME,binder.getOwnerId().toString()));

		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

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
				String id = (String)entry.get("_binderId");
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
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {"entry", "reply"}))
			.add(eq(CREATORID_FIELD,binder.getOwnerId().toString()));
	
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

		model.put(WebKeys.MY_DOCUMENTS, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get("_binderId");
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
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);

		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {"entry", "reply"}))
			.add(in(ENTRY_ANCESTRY, getTrackedPlacesIds(bs, binder)));
	
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);

		model.put(WebKeys.WHATS_NEW_TRACKED_PLACES, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get("_binderId");
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
	
	private static void setupTrackedPeopleBeans(AllModulesInjected bs, Binder binder, Map model) {
		//Get the documents bean for the documents th the user just authored or modified
		Map options = new HashMap();
		
		//Prepare for a standard dashboard search operation
		setupInitialSearchOptions(options);
		
		int offset = ((Integer) options.get(ObjectKeys.SEARCH_OFFSET)).intValue();
		int maxResults = ((Integer) options.get(ObjectKeys.SEARCH_MAX_HITS)).intValue();
		
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {"entry", "reply"}))
			.add(in(CREATORID_FIELD, getTrackedPeopleIds(bs, binder)));
	
		Map results = bs.getBinderModule().executeSearchQuery(crit, offset, maxResults);
		model.put(WebKeys.WHATS_NEW_TRACKED_PEOPLE, results.get(ObjectKeys.SEARCH_ENTRIES));

		Map places = new HashMap();
    	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
				String id = (String)entry.get("_binderId");
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
	
	private static void setupVisitorsBeans(AllModulesInjected bs, Binder binder, Map model) {
		//Who has visited my page?
		if (binder != null) {
			GregorianCalendar start = new GregorianCalendar();
		    //get users over last 2 weeks
		   start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
		   Collection users = bs.getReportModule().getUsersActivity(binder, AuditType.view, 
				   start.getTime(), new java.util.Date());
		   model.put(WebKeys.USERS, users);
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
			GregorianCalendar since = new GregorianCalendar();
			since.add(Calendar.WEEK_OF_MONTH, -2);
			Collection<SharedEntity>sharedEntities = bs.getProfileModule().getShares(binder.getOwnerId(), since.getTime());
			model.put(WebKeys.RELEVANCE_SHARED_ENTITIES, sharedEntities);
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
		List hotEntries = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		GregorianCalendar start = new GregorianCalendar();
		//get users over last 2 weeks
		start.add(java.util.Calendar.HOUR_OF_DAY, -24*14);
		Collection<ActivityInfo> results = bs.getReportModule().culaEsCaliente(AuditType.view, 
				start.getTime(), new java.util.Date());
		for(ActivityInfo info : results) {
			Element resultElem = null;
			if (info.getWhoOrWhat().getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry entry = (FolderEntry) info.getWhoOrWhat();
				hotEntries.add(entry);
			}
		}
		model.put(WebKeys.WHATS_HOT, hotEntries);
	}
	
	private static List<String> getTrackedPlacesIds(AllModulesInjected bs, Binder binder) {
		List<String> sIdList = new ArrayList<String>();
		UserProperties userForumProperties = bs.getProfileModule().getUserProperties(binder.getOwnerId(), binder.getId());
		Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
		if (relevanceMap != null) {
			List<Long> idList = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_BINDERS);
			if (idList != null) {
				for (Long id: idList) {
					sIdList.add(String.valueOf(id));
				}
			}
		}
		return sIdList;
	}

	private static List getTrackedPeopleIds(AllModulesInjected bs, Binder binder) {
		List sIdList = new ArrayList();
		UserProperties userForumProperties = bs.getProfileModule().getUserProperties(binder.getOwnerId(), binder.getId());
		Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
		if (relevanceMap != null) {
			List trackedPeople = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
			if (trackedPeople != null) {
				List<Long> idList = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_BINDERS);
				if (idList != null) {
					for (Long id: idList) {
						sIdList.add(String.valueOf(id));
					}
				}
			}
		}
		return sIdList;
	}

}
