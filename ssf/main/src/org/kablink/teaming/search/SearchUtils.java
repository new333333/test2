/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.DateTools;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.runwith.RunWithCallback;
import org.kablink.teaming.security.runwith.RunWithTemplate;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.search.*;
import org.kablink.util.search.Junction.Conjunction;
import org.kablink.util.search.Junction.Disjunction;

import static org.kablink.util.search.Constants.*;
import static org.kablink.util.search.Restrictions.*;

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
		crit.addOrder(Order.asc(MODIFICATION_DATE_FIELD));
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
	
	public static Criteria entriesForTrackedPlacesEntriesAndPeople(AllModulesInjected bs, List userWorkspaces, 
			List<String> trackedEntryIds, List<String> trackedPeopleIds, boolean entriesOnly, String searchDateField)
	{
		String[] entryTypes;
		if (entriesOnly)
			 entryTypes = new String[] {Constants.ENTRY_TYPE_ENTRY                            };
		else entryTypes = new String[] {Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY};
		
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD, entryTypes                             ))
			.add(in(DOC_TYPE_FIELD,   new String[] {Constants.DOC_TYPE_ENTRY}));
		crit.addOrder(Order.desc(searchDateField));
		
		Disjunction disjunction = disjunction();
		if ((null != userWorkspaces) && (!(userWorkspaces.isEmpty()))) {
			disjunction.add(in(ENTRY_ANCESTRY, userWorkspaces));
		}
		
		if ((null != trackedEntryIds) && (!(trackedEntryIds.isEmpty()))) {
			disjunction.add(in(Constants.DOCID_FIELD, trackedEntryIds));
		}
		
		if ((null != trackedPeopleIds) && (!(trackedPeopleIds.isEmpty()))) {
			disjunction.add(in(CREATORID_FIELD, trackedPeopleIds));
		}
		crit.add(disjunction);

		return crit;
	}
	
	public static Criteria entriesForTrackedPlacesAndPeople(AllModulesInjected bs, List userWorkspaces, 
			List<String> trackedPeopleIds, boolean entriesOnly, String searchDateField)
	{
		return entriesForTrackedPlacesEntriesAndPeople(bs, userWorkspaces, null, trackedPeopleIds, entriesOnly, searchDateField);
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

    public static Criterion libraryFolders() {
        Junction crit = Restrictions.conjunction();
        crit.add(Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER));
        crit.add(Restrictions.eq(Constants.IS_LIBRARY_FIELD, "true"));
        crit.add(Restrictions.disjunction()
                .add(Restrictions.eq(Constants.FAMILY_FIELD, Constants.FAMILY_FIELD_FILE))
                .add(Restrictions.eq(Constants.FAMILY_FIELD, Constants.FAMILY_FIELD_PHOTO)));
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
	
	public static Criteria getMyFilesSearchCriteria(AllModulesInjected bs) {
        // By default, just return binders and entries
        return getMyFilesSearchCriteria(bs, true, true, false, false);
    }

	public static Criteria getMyFilesSearchCriteria(AllModulesInjected bs, boolean binders, boolean entries, boolean replies, boolean attachments) {
		User user = RequestContextHolder.getRequestContext().getUser();
		String userWSId = String.valueOf(user.getWorkspaceId());
		
		// Based on the installed license, what definition families do
		// we consider as 'file'?
		String[] fileFamilies;
		if (Utils.checkIfFilr())
		     fileFamilies = new String[]{Definition.FAMILY_FILE};
		else fileFamilies = new String[]{Definition.FAMILY_FILE, Definition.FAMILY_PHOTO};

        Long mfContainerId = getMyFilesFolderId(bs, user.getWorkspaceId(), false);	// false -> Don't create it if it doesn't exist.

        Disjunction	root     = disjunction();
        Criteria crit = new Criteria();
        crit.add(root);
        if (binders) {
            Conjunction	rootConj = conjunction();
            root.add(rootConj);
            rootConj.add(in(Constants.DOC_TYPE_FIELD,          new String[]{Constants.DOC_TYPE_BINDER}));
            rootConj.add(in(Constants.BINDERS_PARENT_ID_FIELD, new String[]{userWSId}));
            rootConj.add(in(Constants.FAMILY_FIELD,            fileFamilies));
            rootConj.add(in(Constants.IS_LIBRARY_FIELD,        new String[]{Constants.TRUE}));

            // ...that are non-mirrored...
            Disjunction disj = disjunction();
            rootConj.add(disj);
            Conjunction conj = conjunction();
            conj.add(in(Constants.IS_MIRRORED_FIELD, new String[]{Constants.FALSE}));
            disj.add(conj);

            // ...or configured mirrored File Folders.
            conj = conjunction();
            conj.add(in(Constants.IS_MIRRORED_FIELD,         new String[]{Constants.TRUE}));
            conj.add(in(Constants.HAS_RESOURCE_DRIVER_FIELD, new String[]{Constants.TRUE}));
            conj.add(in(Constants.IS_HOME_DIR_FIELD,         new String[]{Constants.TRUE}));
            disj.add(conj);

            if (mfContainerId!=null) {
                // ...exclude it from the binder list.
                Junction noMF = not();
                rootConj.add(noMF);
                String mfContStr = mfContainerId.toString();
                noMF.add(in(Constants.DOCID_FIELD, new String[]{mfContStr}));

                // Also include any folders in there as well.
                conj = conjunction();
                conj.add(in(Constants.DOC_TYPE_FIELD,          new String[]{Constants.DOC_TYPE_BINDER}));
                conj.add(in(Constants.BINDERS_PARENT_ID_FIELD, new String[]{mfContStr}));
                conj.add(in(Constants.FAMILY_FIELD,            fileFamilies));
                conj.add(in(Constants.IS_LIBRARY_FIELD,        new String[]{Constants.TRUE}));
                root.add(conj);
            }
        }
        if ((entries || attachments || replies) && mfContainerId!=null) {
            if (entries) {
                // Search for any file entries...
                Conjunction conj = conjunction();
                conj.add(in(Constants.DOC_TYPE_FIELD,  new String[]{Constants.DOC_TYPE_ENTRY}));
                conj.add(in(Constants.ENTRY_TYPE_FIELD,  new String[]{Constants.ENTRY_TYPE_ENTRY}));
                conj.add(in(Constants.BINDER_ID_FIELD, new String[]{mfContainerId.toString()}));
                conj.add(in(Constants.FAMILY_FIELD,    fileFamilies));
                root.add(conj);
            }
            if (replies) {
                // Search for any file entries...
                Conjunction conj = conjunction();
                conj.add(in(Constants.DOC_TYPE_FIELD,  new String[]{Constants.DOC_TYPE_ENTRY}));
                conj.add(in(Constants.ENTRY_TYPE_FIELD,  new String[]{Constants.ENTRY_TYPE_REPLY}));
                conj.add(in(Constants.BINDER_ID_FIELD, new String[]{mfContainerId.toString()}));
                root.add(conj);
            }
            if (attachments) {
                Conjunction conj = conjunction();
                conj.add(in(Constants.DOC_TYPE_FIELD,  new String[]{Constants.DOC_TYPE_ATTACHMENT}));
                conj.add(in(Constants.BINDER_ID_FIELD, new String[]{mfContainerId.toString()}));
                conj.add(in(Constants.IS_LIBRARY_FIELD,        new String[]{Constants.TRUE}));
                root.add(conj);
            }
        }
		return crit;
	}
	
	public static Criteria getNetFoldersSearchCriteria(AllModulesInjected bs) {
		// Can we access the ID of the top workspace?
		Long topWSId = bs.getWorkspaceModule().getTopWorkspaceId();

		// Add the criteria for top level mirrored file folders
		// that have been configured.
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD,            new String[]{Constants.DOC_TYPE_BINDER}));
		crit.add(in(Constants.ENTRY_ANCESTRY,            new String[]{String.valueOf(topWSId)}));
		crit.add(in(Constants.FAMILY_FIELD,              new String[]{Definition.FAMILY_FILE}));
		crit.add(in(Constants.IS_MIRRORED_FIELD,         new String[]{Constants.TRUE}));
		crit.add(in(Constants.IS_TOP_FOLDER_FIELD,       new String[]{Constants.TRUE}));
		crit.add(in(Constants.HAS_RESOURCE_DRIVER_FIELD, new String[]{Constants.TRUE}));

		return crit;
	}
	
	public static Criteria getBinderEntriesSearchCriteria(AllModulesInjected bs, List binderIds, boolean entriesOnly) {
		Criteria crit =
			SearchUtils.entriesForTrackedPlacesEntriesAndPeople(
				bs,
				binderIds,
				null,
				null,
				entriesOnly,	// true -> Entries only (no replies.)
				Constants.LASTACTIVITY_FIELD);
		return crit;
	}

    /**
   	 * If the user has a folder that's recognized as their My Files
   	 * folder, it's ID is returned.  Otherwise, null is returned.
   	 *
   	 * @param bs
   	 *
   	 * @return
   	 */
   	public static Long getMyFilesFolderId(AllModulesInjected bs, Long userWorkspaceId, boolean createIfNeccessary) {
   		Long reply;
   		List<Long> mfFolderIds = getMyFilesFolderIds(bs, userWorkspaceId);
   		if ((null != mfFolderIds) && (!(mfFolderIds.isEmpty()))) {
   			reply = mfFolderIds.get(0);
   		}
   		else if (createIfNeccessary) {
   			reply = createMyFilesFolder(bs, userWorkspaceId);
   		}
   		else {
   			reply = null;
   		}
   		return reply;
   	}

    private static List<Long> getMyFilesFolderIds(AllModulesInjected bs, Long userWorkspaceId) {
   		// Build a search for the user's binders...
   		Criteria crit = new Criteria();
   		crit.add(in(Constants.DOC_TYPE_FIELD,          new String[]{Constants.DOC_TYPE_BINDER}));
   		crit.add(in(Constants.BINDERS_PARENT_ID_FIELD, new String[]{String.valueOf(userWorkspaceId)}));

   		// ...that are marked as their My Files folder...
   		crit.add(in(Constants.FAMILY_FIELD,         new String[]{Definition.FAMILY_FILE}));
   		crit.add(in(Constants.IS_LIBRARY_FIELD,	    new String[]{Constants.TRUE}));
   		crit.add(in(Constants.IS_MYFILES_DIR_FIELD, new String[]{Constants.TRUE}));

   		// ...that are not mirrored File Folders.
   		crit.add(in(Constants.IS_MIRRORED_FIELD, new String[]{Constants.FALSE}));

   		// Can we find any?
   		Map			searchResults = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, Integer.MAX_VALUE);
   		List<Map>	searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
   		List<Long>	reply         = new ArrayList<Long>();
   		if ((null != searchEntries) && (!(searchEntries.isEmpty()))) {
   			// Yes!  Scan them...
   			for (Map entryMap:  searchEntries) {
   				// ...extracting their IDs from from the search
   				// ...results.
   				String   docIdS   = (String) entryMap.get(Constants.DOCID_FIELD);
   				Long     docId    = Long.parseLong(docIdS);
   				reply.add(docId);
   			}
   		}

   		// If we get here, reply refers to a List<Long> of the folders
   		// in a user's workspace that are recognized as their My Files
   		// folder.  Return it.
   		return reply;
   	}

	/*
	 * Creates a user's My Files container and returns its ID.
	 */
	private static Long createMyFilesFolder(AllModulesInjected bs, Long userWorkspaceId) {
		// Can we determine the template to use for the My Files
		// folder?
		TemplateBinder mfFolderTemplate   = bs.getTemplateModule().getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_LIBRARY);
		Long			mfFolderTemplateId = ((null == mfFolderTemplate) ? null : mfFolderTemplate.getId());
		if (null == mfFolderTemplateId) {
			// No!  Then we can't create it.
			return null;
		}

		// Generate a unique name for the folder.
		Long				reply       = null;
		final String		mfTitleBase = NLT.get("collection.myFiles.folder");
		final BinderModule bm          = bs.getBinderModule();
		for (int tries = 0; true; tries += 1) {
			try {
				// For tries beyond the first, we simply bump a counter
				// until we find a name to use.
				String mfTitle = mfTitleBase;
				if (0 < tries) {
					mfTitle += ("-" + tries);
				}

				// Can we create a folder with this name?
				final Long		mfFolderId = bs.getTemplateModule().addBinder(mfFolderTemplateId, userWorkspaceId, mfTitle, null).getId();
				final Binder	mfFolder   = bm.getBinder(mfFolderId);
				if (null != mfFolder) {
					// Yes!  Mark it as being the My Files folder...
					bm.setProperty(mfFolderId, ObjectKeys.BINDER_PROPERTY_MYFILES_DIR, Boolean.TRUE);
					bm.indexBinder(mfFolderId                                                      );

					// ...and to inherit its team membership.
					RunWithTemplate.runWith(new RunWithCallback() {
                        @Override
                        public Object runWith() {
                            bm.setTeamMembershipInherited(mfFolderId, true);
                            return null;
                        }
                    },
                            new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION},
                            null);

					// Return the ID of the folder we created.
					reply = mfFolderId;
					break;
				}
			}

			catch (Exception e) {
				// If the create fails because of a naming conflict...
				if (e instanceof TitleException) {
					// ...simply try again with a new name.
					continue;
				}
				break;
			}
		}

		// If we get here, reply is null or refers to the ID of the
		// newly created folder.  Return it.
		return reply;
	}

}
