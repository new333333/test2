/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.search;

import static org.kablink.util.search.Constants.*;
import static org.kablink.util.search.Restrictions.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.DateTools;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;
import org.kablink.util.search.Junction.Disjunction;


public class SearchUtils {

	public static Criteria tasksForUser(Long userId, String[] groupIds, String[] teamIds, Date from, Date to)
	{
		Criteria crit = new Criteria();
		crit.add(eq(FAMILY_FIELD,FAMILY_FIELD_TASK))
			.add(eq(DOC_TYPE_FIELD, DOC_TYPE_ENTRY))
			.add(between(
					TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME + BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_END_DATE, 
					DateTools.dateToString(from, DateTools.Resolution.SECOND), 
					DateTools.dateToString(to, DateTools.Resolution.SECOND)));
		crit.addOrder(Order.desc(TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME + BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_END_DATE));
		
		Disjunction disjunction = disjunction();
		disjunction.add(eq(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME,userId.toString()));
		
		if (groupIds != null & groupIds.length > 0) {
			disjunction.add(in(TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME,groupIds));
		}
		if (teamIds != null & teamIds.length > 0) {
			disjunction.add(in(TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME,teamIds));
		}
		crit.add(disjunction);
		
		return crit;
	}
	
	public static Criteria entriesForUser(Long userId)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}))
			.add(eq(CREATORID_FIELD,userId.toString()));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria entriesForTrackedPlaces(AllModulesInjected bs, List userWorkspaces)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {Constants.ENTRY_TYPE_ENTRY, 
				Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}))
			.add(in(ENTRY_ANCESTRY, userWorkspaces));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria entriesForTrackedCalendars(AllModulesInjected bs, List userWorkspaces, String start, String end)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {Constants.ENTRY_TYPE_ENTRY, 
				Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}))
			.add(in(ENTRY_ANCESTRY, userWorkspaces))
			.add(between(Constants.EVENT_DATES_FIELD, start, end));
		crit.addOrder(Order.asc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria entriesForTrackedMiniBlogs(Long[] userIds)
	{
		String[] uids = new String[userIds.length];
		for (int i = 0; i < userIds.length; i++) {
			uids[i] = String.valueOf(userIds[i]);
		}
		Criteria crit = new Criteria();
		crit.add(eq(FAMILY_FIELD, FAMILY_FIELD_MINIBLOG))
			.add(eq(DOC_TYPE_FIELD, DOC_TYPE_ENTRY))
		    .add(in(CREATORID_FIELD, uids));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria bindersForTrackedMiniBlogs(List<Long> userIds)
	{
		List<String> s_userIds = new ArrayList<String>();
		for (Long id : userIds) s_userIds.add(id.toString());
		Criteria crit = new Criteria();
		crit.add(eq(FAMILY_FIELD, FAMILY_FIELD_MINIBLOG))
			.add(eq(DOC_TYPE_FIELD, DOC_TYPE_BINDER))
		    .add(in(OWNERID_FIELD, s_userIds));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria entriesForTrackedPeople(AllModulesInjected bs, Binder userWorkspace)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {Constants.ENTRY_TYPE_ENTRY, 
				Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}))
			.add(in(CREATORID_FIELD, getTrackedPeopleIds(bs, userWorkspace)));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}

	public static List<String> getTrackedPlacesIds(AllModulesInjected bs, Binder userWorkspace) {
		List<String> sIdList = new ArrayList<String>();
		UserProperties userForumProperties = bs.getProfileModule().getUserProperties(userWorkspace.getOwnerId(), userWorkspace.getId());
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

	public static List<String> getTrackedCalendarIds(AllModulesInjected bs, Binder userWorkspace) {
		List<String> sIdList = new ArrayList<String>();
		UserProperties userForumProperties = bs.getProfileModule().getUserProperties(userWorkspace.getOwnerId(), userWorkspace.getId());
		Map relevanceMap = (Map)userForumProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP);
		if (relevanceMap != null) {
			List<Long> idList = (List) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_CALENDARS);
			if (idList != null) {
				for (Long id: idList) {
					sIdList.add(String.valueOf(id));
				}
			}
		}
		return sIdList;
	}

	public static List<String> getTrackedPeopleIds(AllModulesInjected bs, Binder binder) {
		List<String> sIdList = new ArrayList<String>();
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
	
	public static Criteria newEntries()
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria newEntriesDescendants(List binderIds)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {ENTRY_TYPE_ENTRY, ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {DOC_TYPE_ENTRY}))
			.add(in(ENTRY_ANCESTRY, binderIds));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
    public static String getIndexName() {
    	// We use zone key as the index name
    	return org.kablink.teaming.util.Utils.getZoneKey();
    }

	public static Criteria bindersByAccess(Long userId)
	{
		String[] uids = new String[1];
		uids[0] = String.valueOf(userId);
		Criteria crit = new Criteria();
		crit.add(eq(DOC_TYPE_FIELD, DOC_TYPE_BINDER));
		crit.addOrder(Order.asc(ENTITY_PATH));
		return crit;
	}
	
	public static Criteria entriesForTeamingFeedCache(Date now, int updateInterval)
	{
		Date toDate = new Date();
		toDate.setTime(now.getTime() + 60*1000); //Go a little into the future to cover anything really new
		Date fromDate = new Date();
		fromDate.setTime(now.getTime() - updateInterval*60*1000 - 1);
		String s_toDate = DateTools.dateToString(toDate, DateTools.Resolution.SECOND);
		String s_fromDate = DateTools.dateToString(fromDate, DateTools.Resolution.SECOND);
		Criteria crit = new Criteria();
		crit.add(eq(DOC_TYPE_FIELD, DOC_TYPE_ENTRY))
		    .add(between(MODIFICATION_DATE_FIELD, s_fromDate, s_toDate));
		crit.addOrder(Order.asc(MODIFICATION_DATE_FIELD));
		return crit;
	}

	public static Criteria bindersForAncestryBinders(AllModulesInjected bs, List<Long> binderIds)
	{
		List<String> s_binderIds = new ArrayList<String>();
		for (Long id : binderIds) s_binderIds.add(id.toString());
		Criteria crit = new Criteria();
		crit.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_BINDER}))
			.add(in(ENTRY_ANCESTRY, s_binderIds));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}

}
