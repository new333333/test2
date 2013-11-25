/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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


@SuppressWarnings("unchecked")
public class SearchUtils {
	public static Criteria tasksForUser(Long userId, String[] groupIds, String[] teamIds, Date from, Date to)
	{
		Criteria crit = new Criteria();
		crit.add(eq(FAMILY_FIELD,FAMILY_FIELD_TASK))
			.add(eq(DOC_TYPE_FIELD, DOC_TYPE_ENTRY))
			.add(between(
					TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME + BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_LOGICAL_END_DATE, 
					DateTools.dateToString(from, DateTools.Resolution.SECOND), 
					DateTools.dateToString(to, DateTools.Resolution.SECOND)))
			.add(not().add(eq("status","s3")))	// Not completed.
			.add(not().add(eq("status","s4")));	// Not canceled.
		crit.addOrder(Order.desc(TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME + BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_LOGICAL_END_DATE));
		
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
	
	//Get tasks that have no due date
	public static Criteria tasksForUser(Long userId, String[] groupIds, String[] teamIds)
	{
		Criteria crit = new Criteria();
		crit.add(eq(FAMILY_FIELD,FAMILY_FIELD_TASK))
			.add(eq(DOC_TYPE_FIELD, DOC_TYPE_ENTRY))
			.add(not().add(eq("status","s3")))	// Not completed.
			.add(not().add(eq("status","s4")));	// Not canceled.
		crit.addOrder(Order.desc(TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME + BasicIndexUtils.DELIMITER + Constants.EVENT_FIELD_LOGICAL_END_DATE));
		
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
	
	public static Criteria entryReplies(String[] entryIds, boolean topEntryReplies)
	{
		String entryReplyType;
		if (topEntryReplies)
			 entryReplyType = ENTRY_TOP_ENTRY_ID_FIELD;
		else entryReplyType = ENTRY_PARENT_ID_FIELD;
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}))
			.add(in(entryReplyType, entryIds));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria entryReplies(String entryId, boolean topEntryReplies)
	{
		// Always use the initial form of the method.
		return entryReplies(new String[]{entryId}, topEntryReplies);
	}
	
	public static Criteria entryReplies(String[] entryIds)
	{
		// Always use the initial form of the method.
		return entryReplies(entryIds, false);
	}
	
	public static Criteria entryReplies(String entryId)
	{
		// Always use the initial form of the method.
		return entryReplies(new String[]{entryId}, false);
	}
	
	public static Criteria entries(List<Long> entryIds)
	{
		String[] s_entryIds = new String[entryIds.size()];
		for (int i = 0; i < entryIds.size(); i++) s_entryIds[i] = entryIds.get(i).toString();
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD, new String[] {Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_ENTRY}))
			.add(in(DOCID_FIELD, s_entryIds));
		crit.addOrder(Order.desc(CREATION_DATE_FIELD));
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
	
	public static Criteria entriesForTrackedPlacesAndPeople(AllModulesInjected bs, List userWorkspaces, 
			List<String> trackedPeopleIds, boolean entriesOnly, String searchDateField)
	{
		String[] entryTypes;
		if (entriesOnly)
			 entryTypes = new String[] {Constants.ENTRY_TYPE_ENTRY                            };
		else entryTypes = new String[] {Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY};
		
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,entryTypes))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}));
		crit.addOrder(Order.desc(searchDateField));
		
		Disjunction disjunction = disjunction();
		if (!userWorkspaces.isEmpty()) disjunction.add(in(ENTRY_ANCESTRY, userWorkspaces));
		
		if (!trackedPeopleIds.isEmpty()) disjunction.add(in(CREATORID_FIELD, trackedPeopleIds));
		crit.add(disjunction);

		return crit;
	}
	
	public static Criteria entriesForTrackedPlacesAndPeople(AllModulesInjected bs, List userWorkspaces, 
			List<String> trackedPeopleIds) {
		// Always use the initial form of the method.
		return entriesForTrackedPlacesAndPeople(bs, userWorkspaces, trackedPeopleIds, false, MODIFICATION_DATE_FIELD);
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
	
	/*
	 * Returns a Criteria object that can be used to search for the
	 * entries that users whose IDs are in the List<String> are
	 * tracking.
	 */
	private static Criteria entriesForTrackedPeopleImpl(AllModulesInjected bs, List<String> trackedPeopleIds)
	{
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {Constants.ENTRY_TYPE_ENTRY, 
				Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}))
			.add(in(CREATORID_FIELD, trackedPeopleIds));
		crit.addOrder(Order.desc(MODIFICATION_DATE_FIELD));
		return crit;
	}

	/**
	 * Returns a Criteria object that can be used to search for the
	 * entries that users being tracked by the owner of the given
	 * binder are tracking.
	 * 
	 * Note:  If userWorksapce is the guest user workspace, the owner
	 *        will be admin which may not be what's really wanted.
	 * 
	 * @param bs
	 * @param userWorkspace
	 * 
	 * @return
	 */
	public static Criteria entriesForTrackedPeople(AllModulesInjected bs, Binder userWorkspace)
	{
		// Always use the implementation version of this method.
		return entriesForTrackedPeopleImpl(bs, getTrackedPeopleIds(bs, userWorkspace));
	}

	/**
	 * Returns a Criteria object that can be used to search for the
	 * entries that users being tracked by the given user are tracking.
	 * 
	 * @param bs
	 * @param userId
	 * 
	 * @return
	 */
	public static Criteria entriesForTrackedPeople(AllModulesInjected bs, Long userId)
	{
		// Always use the implementation version of this method.
		return entriesForTrackedPeopleImpl(bs, getTrackedPeopleIds(bs, userId));
	}

	/**
	 * Returns a List<String> containing the IDs of the places that that
	 * the owner of the given binder is tracking.
	 * 
	 * Note:  If binder is the guest user workspace, the owner will be
	 *        admin which may not be what's really wanted.
	 * 
	 * @param bs
	 * @param userId
	 * 
	 * @return
	 */
	public static List<String> getTrackedPlacesIds(AllModulesInjected bs, Binder userWorkspace) {
		List<String> sIdList = new ArrayList<String>();
		UserProperties userProperties = getUserProperties(bs, userWorkspace.getOwnerId(), userWorkspace.getId());
		Map relevanceMap = ((Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP));
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
	
	/**
	 * Returns a List<String> containing the IDs of the places that that
	 * user whose ID is userId is tracking.
	 * 
	 * @param bs
	 * @param userId
	 * 
	 * @return
	 */
	public static List<String> getTrackedPlacesIds(AllModulesInjected bs, Long userId) {
		List<String> sIdList = new ArrayList<String>();
		UserProperties userProperties = getUserProperties(bs, userId);
		Map relevanceMap = ((Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP));
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
		UserProperties userProperties = getUserProperties(bs, userWorkspace.getOwnerId(), userWorkspace.getId());
		Map relevanceMap = ((Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP));
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

	/**
	 * Returns a List<String> containing the IDs of the users that that
	 * the owner of the given binder is tracking.
	 * 
	 * Note:  If binder is the guest user workspace, the owner will be
	 *        admin which may not be what's really wanted.
	 * 
	 * @param bs
	 * @param userId
	 * 
	 * @return
	 */
	public static List<String> getTrackedPeopleIds(AllModulesInjected bs, Binder binder) {
		List<String> sIdList = new ArrayList<String>();
		if (binder == null) return sIdList;
		UserProperties userProperties = getUserProperties(bs, binder.getOwnerId(), binder.getId());
		Map relevanceMap = ((Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP));
		if (relevanceMap != null) {
			List<Long> trackedPeople = (List<Long>) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
			if (trackedPeople != null) {
				for (Long id: trackedPeople) {
					sIdList.add(String.valueOf(id));
				}
			}
		}
		return sIdList;
	}
	
	/**
	 * Returns a List<String> containing the IDs of the users that that
	 * user whose ID is userId is tracking.
	 * 
	 * @param bs
	 * @param userId
	 * 
	 * @return
	 */
	public static List<String> getTrackedPeopleIds(AllModulesInjected bs, Long userId) {
		List<String> sIdList = new ArrayList<String>();
		UserProperties userProperties = getUserProperties(bs, userId);
		if (userProperties != null) {
			Map relevanceMap = ((Map) userProperties.getProperty(ObjectKeys.USER_PROPERTY_RELEVANCE_MAP));
			if (relevanceMap != null) {
				List<Long> trackedPeople = (List<Long>) relevanceMap.get(ObjectKeys.RELEVANCE_TRACKED_PEOPLE);
				if (trackedPeople != null) {
					for (Long id: trackedPeople) {
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
	
	public static Criteria entitiesByDateAndAncestor(List binderIds, Date startDate, Date endDate)
	{
		Criteria crit = new Criteria();
		crit.add(in(DOC_TYPE_FIELD,new String[] {DOC_TYPE_BINDER, DOC_TYPE_ENTRY}));
		if (!binderIds.isEmpty()) {
			crit.add(in(ENTRY_ANCESTRY, binderIds));
		}
		if (startDate != null && endDate != null && !binderIds.isEmpty()) {
			String s_toDate = DateTools.dateToString(endDate, DateTools.Resolution.SECOND);
			String s_fromDate = DateTools.dateToString(startDate, DateTools.Resolution.SECOND);
			crit.add(between(MODIFICATION_DATE_FIELD, s_fromDate, s_toDate));
		}
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
	
	private static Criteria entrysByTimeInterval(Date now, long updateIntervalInMS)
	{
		Date toDate = new Date();
		toDate.setTime(now.getTime() + (60L * 1000L)); // Go a minute into the future to cover anything really new.
		Date fromDate = new Date();
		fromDate.setTime(now.getTime() - updateIntervalInMS - 1L);
		String s_toDate = DateTools.dateToString(toDate, DateTools.Resolution.SECOND);
		String s_fromDate = DateTools.dateToString(fromDate, DateTools.Resolution.SECOND);
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD,new String[] {Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}))
			.add(in(DOC_TYPE_FIELD,new String[] {Constants.DOC_TYPE_ENTRY}))
		    .add(between(MODIFICATION_DATE_FIELD, s_fromDate, s_toDate));
		crit.addOrder(Order.asc(MODIFICATION_DATE_FIELD));
		return crit;
	}
	
	public static Criteria entriesForTeamingFeedCache(Date now, int updateIntervalInMinutes)
	{
		return entrysByTimeInterval(now, (((long) updateIntervalInMinutes) * (60L * 1000L)));
	}
	
	public static Criteria entriesForActivityStreamCache(Date now, int updateIntervalInSeconds)
	{
		return entrysByTimeInterval(now, (((long) updateIntervalInSeconds) * 1000L));
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

	/*
	 * Returns the UserProperties based on a user and workspace ID.  If
	 * no workspace ID is provided, an attempt to locate it based on
	 * user's profile.
	 */
	private static UserProperties getUserProperties(AllModulesInjected bs, Long userId, Long wsId) {
		// If we weren't given a workspace ID...
		if (null == wsId) {
			// ...try to locate it based on the user's profile.
			try                 {wsId = bs.getProfileModule().getEntryWorkspaceId(userId);}
			catch (Exception e) {wsId = null;}
		}

		// Access and return the user's properties, using their
		// workspace ID if available.
		UserProperties reply = null;
		try {
			if (null == wsId)
			     reply = bs.getProfileModule().getUserProperties(userId);
			else reply = bs.getProfileModule().getUserProperties(userId, wsId);
		} catch(Exception e) {}
		return reply;
	}
	
	private static UserProperties getUserProperties(AllModulesInjected bs, Long userId) {
		// Always use the initial form of the method.
		return getUserProperties(bs, userId, null);
	}
}
