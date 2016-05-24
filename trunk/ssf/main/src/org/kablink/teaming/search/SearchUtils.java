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
package org.kablink.teaming.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SearchWildCardException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.HomeFolderSelectSpec;
import org.kablink.teaming.dao.util.MyFilesStorageSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.runwith.RunWithCallback;
import org.kablink.teaming.security.runwith.RunWithTemplate;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Criterion;
import org.kablink.util.search.Junction;
import org.kablink.util.search.Junction.Conjunction;
import org.kablink.util.search.Junction.Disjunction;
import org.kablink.util.search.Order;
import org.kablink.util.search.Restrictions;

import static org.kablink.util.search.Constants.*;
import static org.kablink.util.search.Restrictions.*;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class SearchUtils {	
	protected static final Log m_logger = LogFactory.getLog(SearchUtils.class);
	
	/**
	 * Returns true the binder contains nested remote (i.e., Mirrored,
	 * Net or Cloud) folders and false otherwise.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static boolean binderHasNestedRemoteFolders(AllModulesInjected bs, Long binderId) {
		return bindersHaveNestedRemoteFolders(bs, new String[]{String.valueOf(binderId)});
	}
	
	/**
	 * Returns true if any of the binders in a list of binder IDs
	 * contain nested remote (i.e., Mirrored, Net or Cloud) folders and false otherwise.
	 * 
	 * @param bs
	 * @param binderIds
	 * 
	 * @return
	 */
	public static boolean bindersHaveNestedRemoteFolders(AllModulesInjected bs, String[] binderIds) {
		Criteria crit = new Criteria();
		crit.add(eq(Constants.DOC_TYPE_FIELD,    Constants.DOC_TYPE_BINDER));
		crit.add(eq(Constants.IS_MIRRORED_FIELD, Constants.TRUE));
		crit.add(in(Constants.ENTRY_ANCESTRY,    binderIds));
		Map searchResults = bs.getBinderModule().executeSearchQuery(
			crit,
			Constants.SEARCH_MODE_NORMAL,
			0,	// Starting index.
			1,	// Hits requested.
			org.kablink.teaming.module.shared.SearchUtils.fieldNamesList());
		int totalRecords = ((Integer) searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL)).intValue();
		return (0 < totalRecords);
	}
	
	public static boolean bindersHaveNestedRemoteFolders(AllModulesInjected bs, List<Long> binderIds) {
		// Always use the initial form of the method.
		int c = binderIds.size();
		String[] fIds = new String[c];
		for (int i = 0; i < c; i += 1) {
			fIds[i] = String.valueOf(binderIds.get(i));
		}
		return bindersHaveNestedRemoteFolders(bs, fIds);
	}
	
	protected static CoreDao getCoreDao() {
		return (CoreDao)SpringContextUtil.getBean("coreDao");
	};
	
	protected static FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}
	
	protected static ProfileDao getProfileDao() {
		return (ProfileDao)SpringContextUtil.getBean("profileDao");
	}

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
		return entriesForTrackedPlacesEntriesAndPeople(bs, userWorkspaces, 
				trackedEntryIds, trackedPeopleIds, entriesOnly, searchDateField, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
	}

	public static Criteria entriesForTrackedPlacesEntriesAndPeople(AllModulesInjected bs, List userWorkspaces, 
			List<String> trackedEntryIds, List<String> trackedPeopleIds, boolean entriesOnly, String searchDateField, 
			boolean searchSubFolders, boolean includeAttachments, boolean includeFolderNames)
	{
		List entryTypesList = new ArrayList<String>();
		entryTypesList.add(Constants.ENTRY_TYPE_ENTRY);
		if (!entriesOnly) {
			entryTypesList.add(Constants.ENTRY_TYPE_REPLY);
		}
		String[] entryTypes = new String[entryTypesList.size()];
		entryTypesList.toArray(entryTypes);
		
		List docTypesList = new ArrayList<String>();
		docTypesList.add(Constants.DOC_TYPE_ENTRY);
		if (includeAttachments) {
			docTypesList.add(Constants.DOC_TYPE_ATTACHMENT);
		}
		if (includeFolderNames) {
			docTypesList.add(Constants.DOC_TYPE_BINDER);
			entryTypes = new String[0];		//If including folder names, don't filter by entryTypes
		}
		String[] docTypes = new String[docTypesList.size()];
		docTypesList.toArray(docTypes);
		
		Criteria crit = new Criteria();
		crit.add(in(ENTRY_TYPE_FIELD, entryTypes))
			.add(in(DOC_TYPE_FIELD, docTypes));
		crit.addOrder(Order.desc(searchDateField));
		
		Disjunction disjunction = disjunction();
		if ((null != userWorkspaces) && (!(userWorkspaces.isEmpty()))) {
			if (searchSubFolders) {
				disjunction.add(in(ENTRY_ANCESTRY, userWorkspaces));
			} else {
				disjunction.add(in(BINDER_ID_FIELD, userWorkspaces));
			}
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
	 * @param userWorkspace
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
	 * @param binder
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

    public static Criterion buildDocTypeCriterion(boolean includeBinders, boolean includeFolderEntries, boolean includeFiles, boolean includeReplies) {
        Junction types = Restrictions.disjunction();
        // Include a restriction that will always evaluate to false.  That way if all of the include* parameters are false
        // no results will be returned (instead of all results being returned)
        types.add(getFalseCriterion());
        if (includeBinders) {
            types.add(buildBindersCriterion());
        }
        if (includeFiles) {
            types.add(buildAttachmentsCriterion());
        }
        if (includeFolderEntries) {
            types.add(buildEntriesCriterion());
        }
        if (includeReplies) {
            types.add(buildRepliesCriterion());
        }
        return types;
    }

    public static Criterion buildEntryCriterion(Long id) {
        return Restrictions.conjunction()
                .add(buildEntriesAndRepliesCriterion())
                .add(Restrictions.disjunction()
                        .add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()))
                        .add(Restrictions.eq(Constants.ENTRY_TOP_ENTRY_ID_FIELD, id.toString())));
    }

    public static Criterion buildAttachmentsCriterion() {
        return Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT);
    }

    public static Criterion buildAttachmentCriterion(Long entryId) {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ATTACHMENT))
                .add(Restrictions.eq(Constants.ENTRY_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.eq(Constants.DOCID_FIELD, entryId.toString()));
    }

    public static Criterion buildEntriesAndRepliesCriterion() {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_ENTRY, Constants.ENTRY_TYPE_REPLY}));
    }

    public static Criterion buildEntriesCriterion() {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_ENTRY}));
    }

    public static Criterion buildRepliesCriterion() {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.in(Constants.ENTRY_TYPE_FIELD, new String[]{Constants.ENTRY_TYPE_REPLY}));
    }

    public static Criterion buildFoldersCriterion() {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER))
                .add(Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER));
    }

    public static Criterion buildWorkspacesCriterion() {
        return Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER))
                .add(Restrictions.eq(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_WORKSPACE));
    }

    public static Criterion buildBindersCriterion() {
        return Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER);
    }

    public static Criterion buildAncentryCriterion(Long id) {
        return Restrictions.eq(Constants.ENTRY_ANCESTRY, id.toString());
    }

    public static Criterion buildDocIdCriterion(Long id) {
        return Restrictions.eq(Constants.DOCID_FIELD, id.toString());
    }

    public static Criterion getFalseCriterion() {
        return Restrictions.eq(Constants.DOC_TYPE_FIELD, "_fake_");
    }

    public static Criterion buildLibraryTreeCriterion() {
        Junction criteria = Restrictions.disjunction();
        criteria.add(SearchUtils.libraryFolders());
        criteria.add(buildWorkspacesCriterion());
        return criteria;
    }

    public static Criterion buildBinderCriterion(Long id) {
        return Restrictions.conjunction()
                .add(buildBindersCriterion())
                .add(Restrictions.eq(Constants.DOCID_FIELD, id.toString()));
    }

    public static Criterion buildUsersCriterion(boolean allowExternal) {
        Junction crit = Restrictions.conjunction()
                .add(Restrictions.eq(Constants.PERSONFLAG_FIELD, Boolean.TRUE.toString()))
                .add(Restrictions.eq(Constants.DISABLED_PRINCIPAL_FIELD, Boolean.FALSE.toString()))
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.eq(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_USER));
        if (!allowExternal) {
            crit.add(Restrictions.eq(Constants.IDENTITY_INTERNAL_FIELD, Boolean.TRUE.toString()));
        }
        return crit;
    }

    public static Criterion buildGroupsCriterion(Boolean fromLdap, boolean includeAllUsersGroup) {
        Junction crit = Restrictions.conjunction()
                .add(Restrictions.eq(Constants.DOC_TYPE_FIELD, Constants.DOC_TYPE_ENTRY))
                .add(Restrictions.eq(Constants.ENTRY_TYPE_FIELD, Constants.ENTRY_TYPE_GROUP))
                .add(SearchUtils.buildExcludeUniversalAndContainerGroupCriterion(!includeAllUsersGroup));
        if (fromLdap!=null) {
            crit.add(Restrictions.eq(Constants.IS_GROUP_FROM_LDAP_FIELD, fromLdap.toString()));
        }
        return crit;
    }

    public static Criterion buildParentBinderCriterion(Long id) {
        return  Restrictions.disjunction()
                .add(Restrictions.eq(Constants.BINDER_ID_FIELD, id.toString()))
                .add(Restrictions.eq(Constants.BINDERS_PARENT_ID_FIELD, id.toString()));
    }

    public static Criterion buildSearchBinderCriterion(Long id, boolean recursive) {
        if (recursive) {
            return buildAncentryCriterion(id);
        } else {
            return buildParentBinderCriterion(id);
        }
    }

    public static Criterion buildLibraryCriterion(Boolean onlyLibrary, Boolean includeWorkspaces) {
		Criterion crit = Restrictions.eq(Constants.IS_LIBRARY_FIELD, ((Boolean) onlyLibrary).toString());
		if (includeWorkspaces) {
			crit = Restrictions.disjunction().add(buildWorkspacesCriterion()).add(crit);
		}
        return crit;
    }

    public static Criterion buildFileNameCriterion(String fileName) {
        return Restrictions.like(Constants.FILENAME_FIELD, fileName);
    }

    /*
	 * Creates a user's My Files container and returns its ID.
	 */
	private static Long createMyFilesFolder(AllModulesInjected bs, Long userWorkspaceId) {
		// Can we determine the template to use for the My Files
		// folder?
		final TemplateModule	tm                 = bs.getTemplateModule();
		final TemplateBinder	mfFolderTemplate   = tm.getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_LIBRARY);
		final Long				mfFolderTemplateId = ((null == mfFolderTemplate) ? null : mfFolderTemplate.getId());
		if (null == mfFolderTemplateId) {
			// No!  Then we can't create it.
			return null;
		}

		// Generate a unique name for the folder.
		Long				reply       = null;
		final String		mfTitleBase = NLT.get("collection.myFiles.folder");
		final BinderModule	bm          = bs.getBinderModule();
		for (int tries = 0; true; tries += 1) {
			try {
				// For tries beyond the first, we simply bump a counter
				// until we find a name to use.
				String mfTitle = mfTitleBase;
				if (0 < tries) {
					mfTitle += ("-" + tries);
				}

				// Is there a binder that already exists by that name? 
				Binder existingMF;
				try                 {existingMF = bm.getBinderByParentAndTitle(userWorkspaceId, mfTitle);}
				catch (Exception e) {existingMF = null;}
				if (null != existingMF) {
					// Yes!  Is it a My Files Storage folder?
					if (BinderHelper.isBinderMyFilesStorage(existingMF)) {
						// Yes!  Re-index it (if it had been properly
						// indexed, we should have found it with a
						// search) and return its ID.
						Long existingMFFolderId = existingMF.getId();
						bm.indexBinder(existingMFFolderId);
						return existingMFFolderId;
					}
					
					// Cycle the try loop.  We cycle it because we know
					// there's a file with the current name that's NOT
					// a My Files Storage folder so we'll need to
					// synthesize a new name.
					continue;
				}

				// Can we create a folder with this name?
				final Long		mfFolderId = tm.addBinder(mfFolderTemplateId, userWorkspaceId, mfTitle, null).getId();
				final Binder	mfFolder   = bm.getBinder(mfFolderId);
				if (null != mfFolder) {
					// Yes!  Mark it as being the My Files folder...
					bm.setMyFilesDir(mfFolderId, true);
					bm.indexBinder(mfFolderId        );

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

	/**
	 * Returns a List<Long> of the current user's home folder IDs.
	 * 
	 * @param bs
	 * @param userWSId
	 * 
	 * @return
	 */
	public static List<Long> getHomeFolderIds(AllModulesInjected bs, Long userWSId) {
		// Can we find any Home folders using a database query?
		Long                 zoneId = RequestContextHolder.getRequestContext().getZoneId();
		HomeFolderSelectSpec hfSpec = new HomeFolderSelectSpec(userWSId);
		List<Folder>         hfs    = getFolderDao().findHomeFolders(hfSpec, zoneId);
		List<Long>	         reply  = new ArrayList<Long>();
		if (MiscUtil.hasItems(hfs)) {
			// Yes!  Copy their IDs into the reply List<Long>.
			for (Folder mf:  hfs) {
				reply.add(mf.getId());
			}
		}
		
		// If info logging is enabled and we found more than one Home
		// folder for this user workspace...
		if (m_logger.isInfoEnabled() && (null != reply) && (1 < reply.size())) {
			// ...log that fact.
			m_logger.info("SearchUtils.getHomeFolderIds():  User whose workspace ID is " + userWSId + " has " + reply.size() + " Home folders.");
		}
		
		// If we get here, reply refers to a List<Long> of the IDs of
		// the folders in a user's workspace that are recognized as
		// Home folders.  Return it.
		return reply;
	}
	
	public static List<Long> getHomeFolderIds(AllModulesInjected bs, User user) {
		// Always use the initial form of the method.
		return getHomeFolderIds(bs, user.getWorkspaceId());
	}
	
	/**
	 * Returns the current user's home folder ID.
	 * 
	 * @param bs
	 * @param userWSId
	 * 
	 * @return
	 */
	public static Long getHomeFolderId(AllModulesInjected bs, Long userWSId) {
		List<Long> homeFolderIds = getHomeFolderIds(bs, userWSId);
		Long reply;
		if ((null != homeFolderIds) && (!(homeFolderIds.isEmpty())))
		     reply = homeFolderIds.get(0);
		else reply = null;
		return reply;
	}
	
	public static Long getHomeFolderId(AllModulesInjected bs, User user) {
		// Always use the initial form of the method.
		return getHomeFolderId(bs, user.getWorkspaceId());
	}
	
	public static Long getHomeFolderId(AllModulesInjected bs) {
		// Always use the initial form of the method.
		User user = RequestContextHolder.getRequestContext().getUser();
		return getHomeFolderId(bs, user.getWorkspaceId());
	}

    /**
   	 * If the user has a folder that's recognized as their My Files
   	 * folder, it's ID is returned.  Otherwise, null is returned.
   	 *
   	 * Uses the user's properties to locate an existing My Files
   	 * folder.
   	 * 
   	 * @param bs
   	 * @param user
   	 * @param createIfNecessary
   	 *
   	 * @return
   	 */
   	public static Long getMyFilesFolderId(AllModulesInjected bs, User user, boolean createIfNecessary) {
   		Long reply;
   		List<Long> mfFolderIds = getMyFilesFolderIdsImpl(bs, user);
   		if ((null != mfFolderIds) && (!(mfFolderIds.isEmpty()))) {
   			reply = mfFolderIds.get(0);
   		}
   		else if (createIfNecessary) {
   			reply = createMyFilesFolder(bs, user.getWorkspaceId());
   		}
   		else {
   			reply = null;
   		}
   		return reply;
   	}
   	
   	public static Long getMyFilesFolderId(AllModulesInjected bs, boolean createIfNecessary) {
   		// Always use the initial form of the method.
   		return getMyFilesFolderId(bs, RequestContextHolder.getRequestContext().getUser(), createIfNecessary);
   	}

	/*
	 * Returns a List<Long> of the IDs of the My Files Storage folders
	 * contained in a user's workspace.
	 */
	private static List<Long> getMyFilesFolderIdsImpl(AllModulesInjected bs, User user) {
		// Can we find any My Files Storage folders using a database
		// query?
		Long userWSId = user.getWorkspaceId();
		List<Long> reply = getMyFilesFolderIdsUsingDBQuery(bs, userWSId);
		if (!(MiscUtil.hasItems(reply))) {
			// No!  Look for them using a Lucene search!
			reply = getMyFilesFolderIdsUsingLuceneSearch(bs, userWSId);
			if (null == reply) {
				reply = new ArrayList<Long>();
			}

			// Is there a My Files Storage marker in the user's
			// properties?
			Long mfId = getMyFilesFolderIdUsingUserProperties(bs, user);
			if (null != mfId) {
				// Yes!  Add it to the reply list, if it's not already
				// there and remove it from their UserProperties.
				ListUtil.addLongToListLongIfUnique(reply, mfId);
		   		BinderHelper.removeUserPropertiesMyFilesDirMarkers(user.getId());
			}

			// Do we have any My Files Storage folder IDs?
			if (MiscUtil.hasItems(reply)) {
				// Yes!  Change them so that in the future, we'll find
				// them using the database query.
				try {
					Set<Binder> binders = bs.getBinderModule().getBinders(reply, Boolean.FALSE);
					if (MiscUtil.hasItems(binders)) {
						for (Binder binder:  binders) {
							BinderHelper.updateBinderMyFilesDirMarkers(binder);
						}
					}
				}
				catch (Exception e) {
					m_logger.error("SearchUtils.getMyFilesFolderIdsImpl( EXCEPTION ):  ", e);
				}
			}
		}
		
		// If info logging is enabled and we found more than one My
		// Files Storage folder for this user...
		if (m_logger.isInfoEnabled() && (null != reply) && (1 < reply.size())) {
			// ...log that fact.
			m_logger.info("SearchUtils.getMyFilesFolderIdsImpl():  User '" + user.getTitle() + "' (" + user.getId() + ") has " + reply.size() + " My Files Storage folders.");
		}
		
		// If we get here, reply refers to a List<Long> of the IDs of
		// the folders in a user's workspace that are recognized as My
		// Files Storage folders.  Return it.
		return reply;
	}
	
	/*
	 * Returns a List<Long> of the IDs of the My Files Storage folders
	 * contained in a user workspace.
	 * 
	 * The list is built using a database query.
	 */
	private static List<Long> getMyFilesFolderIdsUsingDBQuery(AllModulesInjected bs, Long userWSId) {
		// Can we find any My Files Storage folders using a database
		// query?
		Long                     zoneId = RequestContextHolder.getRequestContext().getZoneId();
		MyFilesStorageSelectSpec mfSpec = new MyFilesStorageSelectSpec(userWSId);
		List<Folder>             mfs    = getFolderDao().findMyFilesStorageFolders(mfSpec, zoneId);
		List<Long>	             reply  = new ArrayList<Long>();
		if (MiscUtil.hasItems(mfs)) {
			// Yes!  Copy their IDs into the reply List<Long>.
			for (Folder mf:  mfs) {
				reply.add(mf.getId());
			}
		}
		
		// If we get here, reply refers to a List<Long> of the IDs of
		// the folders in a user's workspace that are recognized as My
		// Files Storage folders.  Return it.
		return reply;
	}
	
	/*
	 * Returns a List<Long> of the IDs of the My Files Storage folders
	 * contained in a user workspace.
	 * 
	 * The list is built using a Lucene search.
	 */
	private static List<Long> getMyFilesFolderIdsUsingLuceneSearch(AllModulesInjected bs, Long userWSId) {
		// Build a search for the user's binders...
		Criteria crit = new Criteria();
		crit.add(in(Constants.DOC_TYPE_FIELD,          new String[]{Constants.DOC_TYPE_BINDER}));
		crit.add(in(Constants.BINDERS_PARENT_ID_FIELD, new String[]{String.valueOf(userWSId)}));
		
		// ...that are marked as a My Files Storage folder.
		crit.add(in(Constants.FAMILY_FIELD,         new String[]{Definition.FAMILY_FILE}));
		crit.add(in(Constants.IS_LIBRARY_FIELD,	    new String[]{Constants.TRUE        }));
		crit.add(in(Constants.IS_MYFILES_DIR_FIELD, new String[]{Constants.TRUE        }));

		// Can we find any?
		Map        searchResults = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_SELF_CONTAINED_ONLY, 0, Integer.MAX_VALUE,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
		List<Map>  searchEntries = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
		List<Long> reply         = new ArrayList<Long>();
		if ((null != searchEntries) && (!(searchEntries.isEmpty()))) {
			// Yes!  Scan them...
			for (Map entryMap:  searchEntries) {
				// ...extracting their IDs from from the search
				// ...results.
				String docIdS = ((String) entryMap.get(Constants.DOCID_FIELD));
				if (MiscUtil.hasString(docIdS)) {
					reply.add(Long.valueOf(docIdS));
				}
			}
		}
		
		// If we get here, reply refers to a List<Long> of the IDs of
		// the folders in a user's workspace that are recognized as My
		// Files Storage folders.  Return it.
		return reply;
	}
	
    /*
   	 * Returns any My Files Storage folder ID the user has stored in
   	 * their UserProperties.  If there isn't one, null is returned.
   	 */
   	@SuppressWarnings("deprecation")
	private static Long getMyFilesFolderIdUsingUserProperties(AllModulesInjected bs, User user) {
   		// If we looking for Guest's My Files Storage folder...
   		if (user.isShared()) {
   			// ...return null since Guest's UserProperties aren't
   			// ...persistent.
   			return null;
   		}

   		// Return any My Files Storage folder ID stored in the user's
   		// properties?
   		UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId(), false);	// false -> User can be disabled.
   		return ((null == userProperties) ? null : ((Long) userProperties.getProperty(ObjectKeys.USER_PROPERTY_MYFILES_DIR_DEPRECATED)));
   	}

	/**
	 * This routine returns a Criteria that will search all folders
	 * associated with the My Files collection.
	 * 
	 * @param bs
	 * @param rootBinderId
	 * @param binders
	 * @param entries
	 * @param replies
	 * @param attachments
	 * @param includeMyFilesStorageBinder
	 * 
	 * @return
	 */
	public static Criteria getMyFilesSearchCriteria(AllModulesInjected bs, Long rootBinderId, 
			boolean binders, boolean entries, boolean replies, boolean attachments, boolean includeMyFilesStorageBinder) {
		SimpleProfiler.start("SearchUtils.getMyFilesSearchCriteria()");
		try {
			// Based on the installed license, what definition
			// families do we consider as 'file'?
			String[] fileEntryFamilies;
			if (Utils.checkIfFilr())
			     fileEntryFamilies = new String[]{Definition.FAMILY_FILE};
			else fileEntryFamilies = null;	// In Vibe, we want ALL entries, not just file entries.
			boolean hasFileEntryFamilies = ((null != fileEntryFamilies) && (0 < fileEntryFamilies.length));
			
			String[] fileFolderFamilies;
			if (Utils.checkIfFilr())
			     fileFolderFamilies = new String[]{Definition.FAMILY_FILE};
			else fileFolderFamilies = new String[]{Definition.FAMILY_FILE, Definition.FAMILY_PHOTO};
			boolean hasFileFolderFamilies = ((null != fileFolderFamilies) && (0 < fileFolderFamilies.length));
	
	        Long	mfRootId;
	        boolean	usingHomeAsMF = useHomeAsMyFiles(bs);
	        if (usingHomeAsMF) {
	            // Yes!  Can we find their home folder?
	            mfRootId      = getHomeFolderId(bs);
	            usingHomeAsMF = (null != mfRootId);
	            if (!usingHomeAsMF) {
	                // No!  Just use the binder we were given.
	                mfRootId = rootBinderId;
	            }
	        }
	        else {
	            // No, we aren't supposed to use this user's home
	            // folder as the root of their My Files area!  Use
	            // the binder we were given.
	            mfRootId = rootBinderId;
	        }
	        String myFilesRootIdS = String.valueOf(mfRootId);
	
	        // Do we have a folder to use as a My Files container?
	        Long mfContainerId;
	        if (usingHomeAsMF)
	             mfContainerId = mfRootId;
	        else mfContainerId = getMyFilesFolderId(bs, false);
	        boolean  hasMFContainerId = (null != mfContainerId);
	        String   mfContainerIdS;
	        String[] mfContainerIdStrings;
	        if (hasMFContainerId) {
	            mfContainerIdS       = String.valueOf(mfContainerId);
	            mfContainerIdStrings = new String[]{mfContainerIdS};
	        }
	        else {
	        	mfContainerIdS       = null;
	        	mfContainerIdStrings = new String[0];
	        }
	
	        // Search for file folders within the binder...
	        Disjunction	root = disjunction();
	        Criteria crit = new Criteria();
	        crit.add(root);
	        if (binders) {
	            Conjunction	rootConj = conjunction();
	            root.add(rootConj);
	            rootConj.add(in(Constants.DOC_TYPE_FIELD,          new String[]{Constants.DOC_TYPE_BINDER}));
	            rootConj.add(in(Constants.BINDERS_PARENT_ID_FIELD, new String[]{myFilesRootIdS}));
	            if (hasFileEntryFamilies) {
	            	rootConj.add(in(Constants.FAMILY_FIELD,        fileEntryFamilies));
	            }
	            rootConj.add(in(Constants.IS_LIBRARY_FIELD,        new String[]{Constants.TRUE}));
	
	            // ...if we have a non-Home My Files containers...
	            if (hasMFContainerId && (!usingHomeAsMF) && !includeMyFilesStorageBinder) {
	                // ...exclude them from the binder list.
	                Junction noMF = not();
	                rootConj.add(noMF);
	                noMF.add(in(Constants.DOCID_FIELD, mfContainerIdStrings));
	            }
	
	            if (!usingHomeAsMF) {
	                // ...that are non-mirrored...
	                Disjunction disj = disjunction();
	                rootConj.add(disj);
	                Conjunction conj = conjunction();
	                conj.add(in(Constants.IS_MIRRORED_FIELD, new String[]{Constants.FALSE}));
	                disj.add(conj);
	
	                // ...or configured mirrored File Home Folders...
	                conj = conjunction();
	                conj.add(in(Constants.IS_MIRRORED_FIELD,         new String[]{Constants.TRUE}));
	                conj.add(in(Constants.HAS_RESOURCE_DRIVER_FIELD, new String[]{Constants.TRUE}));
	                conj.add(in(Constants.IS_HOME_DIR_FIELD,         new String[]{Constants.TRUE}));
	                disj.add(conj);
	                
	                // ...or configured mirrored File Cloud Folders...
	                conj = conjunction();
	                conj.add(in(Constants.IS_MIRRORED_FIELD,         new String[]{Constants.TRUE}));
	                conj.add(in(Constants.HAS_RESOURCE_DRIVER_FIELD, new String[]{Constants.TRUE}));
	                conj.add(in(Constants.IS_CLOUD_FOLDER_FIELD,     new String[]{Constants.TRUE}));
	                disj.add(conj);
	            }
	            if (hasMFContainerId) {
	                // Yes!  Search for any folders in there as well.
	                Disjunction disj = disjunction();
	                rootConj.add(disj);
	                Conjunction conj = conjunction();
	                conj.add(in(Constants.DOC_TYPE_FIELD,          new String[]{Constants.DOC_TYPE_BINDER}));
	                conj.add(in(Constants.BINDERS_PARENT_ID_FIELD, new String[]{mfContainerIdS}));
	                if (hasFileFolderFamilies) {
	                	conj.add(in(Constants.FAMILY_FIELD,        fileFolderFamilies));
	                }
	                conj.add(in(Constants.IS_LIBRARY_FIELD,        new String[]{Constants.TRUE}));
	                root.add(conj);
	            }
	        }
	        if ((entries || attachments || replies) && hasMFContainerId) {
	            if (entries) {
	                // Search for any file entries...
	                Conjunction conj = conjunction();
	                conj.add(in(Constants.DOC_TYPE_FIELD,    new String[]{Constants.DOC_TYPE_ENTRY}));
	                conj.add(in(Constants.ENTRY_TYPE_FIELD,  new String[]{Constants.ENTRY_TYPE_ENTRY}));
	                conj.add(in(Constants.BINDER_ID_FIELD,   new String[]{mfContainerIdS}));
	                if (hasFileEntryFamilies) {
	                	conj.add(in(Constants.FAMILY_FIELD,  fileEntryFamilies));
	                }
	                root.add(conj);
	            }
	            if (replies) {
	                // Search for any file entries...
	                Conjunction conj = conjunction();
	                conj.add(in(Constants.DOC_TYPE_FIELD,    new String[]{Constants.DOC_TYPE_ENTRY}));
	                conj.add(in(Constants.ENTRY_TYPE_FIELD,  new String[]{Constants.ENTRY_TYPE_REPLY}));
	                conj.add(in(Constants.BINDER_ID_FIELD,   new String[]{mfContainerIdS}));
	                root.add(conj);
	            }
	            if (attachments) {
	                Conjunction conj = conjunction();
	                conj.add(in(Constants.DOC_TYPE_FIELD,    new String[]{Constants.DOC_TYPE_ATTACHMENT}));
	                conj.add(in(Constants.BINDER_ID_FIELD,   new String[]{mfContainerIdS}));
	                conj.add(in(Constants.IS_LIBRARY_FIELD,  new String[]{Constants.TRUE}));
	                root.add(conj);
	            }
	        }
			return crit;
		}
		
		finally {
			SimpleProfiler.stop("SearchUtils.getMyFilesSearchCriteria()");
		}
	}
	
	public static Criteria getMyFilesSearchCriteria(AllModulesInjected bs) {
		// Always use the initial form of the method.
        User user = RequestContextHolder.getRequestContext().getUser();
        return getMyFilesSearchCriteria(bs, user.getWorkspaceId());
    }

	public static Criteria getMyFilesSearchCriteria(AllModulesInjected bs, Long rootBinderId) {
		// Always use the initial form of the method.  By default, just
		// return binders and entries
        return getMyFilesSearchCriteria(bs, rootBinderId, true, true, false, false, false);
    }
	
	public static Criteria getMyFilesSearchCriteria(AllModulesInjected bs, Long rootBinderId, boolean binders, boolean entries, boolean replies, boolean attachments) {
		return getMyFilesSearchCriteria (bs, rootBinderId, binders, entries, replies, attachments, false);
	}


	/**
	 * Returns the Net Folders root binder.
	 * 
	 * @return
	 */
	public static Binder getNetFoldersRootBinder() {
		Binder reply;
		if (Utils.checkIfFilr() || Utils.checkIfFilrAndVibe()) {
			reply = getCoreDao().loadReservedBinder(
				ObjectKeys.NET_FOLDERS_ROOT_INTERNALID, 
				RequestContextHolder.getRequestContext().getZoneId());
		}
		else {
			reply = null;
		}
		return reply;
	}
	
	/**
	 * Returns the ID of the Net Folders root binder, optionally
	 * defaulting to the top workspace if that binder doesn't exist.
	 * 
	 * @param bs
	 * @param defaultToTop
	 * 
	 * @return
	 */
	public static Long getNetFoldersRootBinderId(AllModulesInjected bs, boolean defaultToTop) {
		Binder nfBinder = getNetFoldersRootBinder();
		Long   reply;
		if      (null != nfBinder) reply = nfBinder.getId();
		else if (defaultToTop)     reply = bs.getWorkspaceModule().getTopWorkspaceId();
		else                       reply = null;
		return reply;
	}
	
	public static Long getNetFoldersRootBinderId(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return getNetFoldersRootBinderId(bs, false);	// false -> Don't default to the top workspace.
	}
	
	/**
	 * This routine returns a Criteria that will search all the net
	 * folders a user has access to.
	 * 
	 * @param bs
	 * @param defaultToTop
	 * 
	 * @return
	 */
	public static Criteria getNetFoldersSearchCriteria(AllModulesInjected bs, boolean defaultToTop) {
		SimpleProfiler.start("SearchUtils.getNetFoldersSearchCriteria()");
		try {
			// Look in the Net Folders root binder.
			Long nfBinderId = getNetFoldersRootBinderId(bs, defaultToTop);
	
			// Add the criteria for top level mirrored file folders
			// that have been configured.
			Criteria reply = new Criteria();
			reply.add(eq(Constants.DOC_TYPE_FIELD,            Constants.DOC_TYPE_BINDER));
			reply.add(eq(Constants.IS_TOP_FOLDER_FIELD,       Constants.TRUE));
			reply.add(eq(Constants.HAS_RESOURCE_DRIVER_FIELD, Constants.TRUE));
			reply.add(eq(Constants.BINDERS_PARENT_ID_FIELD,   nfBinderId.toString()));
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("SearchUtils.getNetFoldersSearchCriteria()");
		}
	}
	
	public static Criteria getNetFoldersSearchCriteria(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return getNetFoldersSearchCriteria(bs, true);	// true -> Default to the top workspace.
	}
	
	public static void removeNetFoldersWithNoRootAccess(Map netFolderSearchResults) {
		// Filter out any folders that don't have the 
		// AllowAccessToNetFolder right
		
		// 7/31/2013 JK (bug 829793) - This method is no longer necessary due to changes made as part of ACL dredging reduction work.
	}
		
	public static List<BinderIndexData> removeNetFoldersWithNoRootAccess2(List<BinderIndexData> netFolderMapList) {
		// 7/31/2013 JK (bug 829793) - This method is no longer necessary due to changes made as part of ACL dredging reduction work.
		return netFolderMapList;
	}

    /*
	 * Returns the UserProperties based on a user and workspace ID.  If
	 * no workspace ID is provided, an attempt to locate it based on
	 * user's profile.
	 */
	private static UserProperties getUserProperties(AllModulesInjected bs, Long userId, Long wsId) {
		// If we weren't given a workspace ID...
		ProfileModule pm = bs.getProfileModule();
		if (null == wsId) {
			// ...try to locate it based on the user's profile.
			try                 {wsId = pm.getEntryWorkspaceId(userId);}
			catch (Exception e) {wsId = null;}
		}

		// Access and return the user's properties, using their
		// workspace ID if available.
		UserProperties reply = null;
		try {
			if (null == wsId)
			     reply = pm.getUserProperties(userId);
			else reply = pm.getUserProperties(userId, wsId);
		} catch(Exception e) {}
		return reply;
	}
	
	private static UserProperties getUserProperties(AllModulesInjected bs, Long userId) {
		// Always use the initial form of the method.
		return getUserProperties(bs, userId, null);
	}
	
	/**
	 * Returns the search criteria used to find all the entries in a
	 * binder.
	 *  
	 * @param bs
	 * @param binderIds
	 * @param entriesOnly
	 * @param searchSubFolders
	 * 
	 * @return
	 */
	public static Criteria getBinderEntriesSearchCriteria(AllModulesInjected bs, List binderIds, boolean entriesOnly, boolean searchSubFolders) {
		Criteria reply =
			entriesForTrackedPlacesEntriesAndPeople(
				bs,
				binderIds,
				null,
				null,
				entriesOnly,	// true -> Entries only (no replies.)
				Constants.LASTACTIVITY_FIELD,
				searchSubFolders,
				Boolean.FALSE,
				Boolean.FALSE);
		
		return reply;
	}
	
	public static Criteria getBinderEntriesSearchCriteria(AllModulesInjected bs, List binderIds, boolean entriesOnly) {
		// Always use the initial form of the method.
		return getBinderEntriesSearchCriteria(bs, binderIds, entriesOnly, true);
	}	

    /**
     * Adds a quick filter to the search filter in the options map.
     *
     * @param quickFilter
     * 
     * @return
     */
    public static String modifyQuickFilter(String quickFilter) {
        // If we weren't given a quick filter to add...
        quickFilter = ((null == quickFilter) ? "" : quickFilter.trim());
        if (0 == quickFilter.length()) {
            // ...there's nothing to do.  Bail.
            return null;
        }

        // If the quick filter doesn't end with an '*'...
        if (!(quickFilter.endsWith("*"))) {
            // ...add one.
            quickFilter += "*";
        }
        return quickFilter;
    }

    /**
     * Returns the results of a net folders search.
     *   
     * @param mods
     * @param quickFilter
     * @param options
     * 
     * @return
     */
    public static Map searchForNetFolders(AllModulesInjected mods, String quickFilter, Map options) {
        // Do we have a quick filter?
        Criteria crit = getNetFoldersSearchCriteria(mods, false);
        quickFilter   = modifyQuickFilter(quickFilter);
        if (null != quickFilter) {
            crit.add(like(Constants.TITLE_FIELD, quickFilter));
        }

        // Add in the sort information...
        boolean sortAscend = (!(getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false                   )));
        String  sortBy     =    getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      Constants.SORT_TITLE_FIELD);
        crit.addOrder(new Order(Constants.ENTITY_FIELD, sortAscend));
        crit.addOrder(new Order(sortBy,                 sortAscend));

        // ...and issue the query and return the entries.
        Binder nfBinder = getNetFoldersRootBinder();
        Map netFolderResults = mods.getBinderModule().searchFolderOneLevelWithInferredAccess(
                crit,
                Constants.SEARCH_MODE_SELF_CONTAINED_ONLY,
                getOptionInt(options, ObjectKeys.SEARCH_OFFSET,   0),
                getOptionInt(options, ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS),
                nfBinder);
        
		// Remove any results where the current user does not have
        // AllowNetFolderAccess rights.
		removeNetFoldersWithNoRootAccess(netFolderResults);
		return netFolderResults;
    }

    /**
     * ?
     * 
     * @return
     */
    public static Criteria getSharedWithMePrincipalsCriteria() {
		User user = RequestContextHolder.getRequestContext().getUser();
		Set<Long> principalIds = getProfileDao().getAllPrincipalIds(user);
		Set<String> pIds = new HashSet<String>();
		for (Long pid : principalIds) {
			pIds.add(String.valueOf(pid));
		}
		Criteria crit = new Criteria();
		crit.add(in(DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER, Constants.DOC_TYPE_ENTRY, 
				Constants.DOC_TYPE_ATTACHMENT}));
		crit.add(in(SHARED_IDS, pIds));
		return crit;
    }

    /**
     * ?
     * 
     * @param binderIds
     * 
     * @return
     */
    public static Criteria getSharedWithMeSearchCriteria(List<String> binderIds) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Set<Long> principalIds = getProfileDao().getAllPrincipalIds(user);
		Set<String> pIds = new HashSet<String>();
		for (Long pid : principalIds) {
			pIds.add(String.valueOf(pid));
		}
		
		Criteria reply = new Criteria();
		reply.add(in(DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER, Constants.DOC_TYPE_ENTRY, Constants.DOC_TYPE_ATTACHMENT}));
		
		Disjunction disjunction = disjunction();
		if ((null != binderIds) && (!(binderIds.isEmpty()))) {
			disjunction.add(in(ENTRY_ANCESTRY, binderIds));
		}
		disjunction.add(in(SHARED_IDS, pIds));		
		reply.add(disjunction);
		
		return reply;
    }
    
	/**
	 * This routine returns a Criteria that will find the folders shared by me
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static Criteria getSharedByMeFoldersSearchCriteria(AllModulesInjected bs, User user) {
		SimpleProfiler.start("SearchUtils.getSharedByMeFoldersSearchCriteria()");
		if (user == null) {
			//Do this for the current user
			user = RequestContextHolder.getRequestContext().getUser();
		}
		String userId = String.valueOf(user.getId());
		try {
			// Add the criteria for "sharedBy" field
			// that have been configured.
			Criteria reply = new Criteria();
			reply.add(eq(DOC_TYPE_FIELD, Constants.DOC_TYPE_BINDER));
			reply.add(eq(Constants.SHARE_CREATOR, userId));
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("SearchUtils.getSharedByMeFoldersSearchCriteria()");
		}
	}
	
	/**
	 * This routine returns a Criteria that will search all "shared by me" files and folders
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static Criteria getSharedByMeSearchCriteria(AllModulesInjected bs, User user, List<String> binderIds) {
		SimpleProfiler.start("SearchUtils.getSharedByMeSearchCriteria()");
		if (user == null) {
			//Do this for the current user
			user = RequestContextHolder.getRequestContext().getUser();
		}
		String userId = String.valueOf(user.getId());
		try {
			// Add the criteria for "sharedBy" field
			// that have been configured.
			Criteria reply = new Criteria();
			
			reply.add(in(DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER, Constants.DOC_TYPE_ENTRY, Constants.DOC_TYPE_ATTACHMENT}));
			Disjunction disjunction = disjunction();
			disjunction.add(eq(Constants.SHARE_CREATOR, userId));
			if ((null != binderIds) && (!(binderIds.isEmpty()))) {
				disjunction.add(in(ENTRY_ANCESTRY, binderIds));
			}
			reply.add(disjunction);
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("SearchUtils.getSharedByMeSearchCriteria()");
		}
	}

    public static Criterion buildExcludeUniversalAndContainerGroupCriterion(boolean excludeAllInternal) {
        Junction not = Restrictions.not();
        Junction or = Restrictions.disjunction();
        not.add(or);

        if (excludeAllInternal) {
            or.add(Restrictions.eq(Constants.GROUPNAME_FIELD, "allusers"));
        }
        or.add(Restrictions.eq(Constants.GROUPNAME_FIELD, "allextusers"));
        or.add(Restrictions.eq(Constants.IS_LDAP_CONTAINER_FIELD, Constants.TRUE));
        return not;
    }
	
    public static Document buildExcludeUniversalAndContainerGroupFilter(boolean excludeAllInternal) {
        Document searchFilter;
        Element rootElement, orElement;
        Element field;
        Element child;

        searchFilter = DocumentHelper.createDocument();
        rootElement = searchFilter.addElement( Constants.NOT_ELEMENT );
        orElement = rootElement.addElement(Constants.OR_ELEMENT);

        if (excludeAllInternal) {
            field = orElement.addElement( Constants.FIELD_ELEMENT );
            field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE, Constants.GROUPNAME_FIELD );
            child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
            child.setText( "allusers" );
        }

        field = orElement.addElement( Constants.FIELD_ELEMENT );
        field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE, Constants.GROUPNAME_FIELD );
        child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
        child.setText( "allextusers" );

        // Don't include "ldap container" groups.
        field = orElement.addElement( Constants.FIELD_ELEMENT );
        field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE, Constants.IS_LDAP_CONTAINER_FIELD );
        child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
        child.setText( Constants.TRUE );
        return searchFilter;
    }
	
    public static Document buildExcludeFilter(String name, String value) {
        Document searchFilter;
        Element rootElement;
        Element field;
        Element child;

        searchFilter = DocumentHelper.createDocument();
        rootElement = searchFilter.addElement( Constants.NOT_ELEMENT );

        field = rootElement.addElement( Constants.FIELD_ELEMENT );
        field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE, name );
        child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
        child.setText( value );
        return searchFilter;
    }
	
	/**
	 * Returns an Integer based value from an options Map.  If a value
	 * for key isn't found, defInt is returned.
	 * 
	 * @param options
	 * @param key
	 * @param defInt
	 *  
	 * @return
	 */
	public static int getOptionInt(Map options, String key, int defInt) {
		Integer obj = ((Integer) options.get(key));
		return ((null == obj) ? defInt : obj.intValue());
	}
	
	/**
	 * Returns a Boolean based value from an options Map.  If a value
	 * for key isn't found, defBool is returned. 
	 * 
	 * @param options
	 * @param key
	 * @param defBool
	 *  
	 * @return
	 */
	public static boolean getOptionBoolean(Map options, String key, boolean defBool) {
		Boolean obj = ((Boolean) options.get(key));
		return ((null == obj) ? defBool : obj.booleanValue());
	}

	/**
	 * Returns a String based value from an options Map.  If a value
	 * for key isn't found, defStr is returned. 
	 * 
	 * @param options
	 * @param key
	 * @param defStr
	 *  
	 * @return
	 */
	public static String getOptionString(Map options, String key, String defStr) {
		String obj = ((String) options.get(key));
		return (((null == obj) || (0 == obj.length())) ? defStr : obj);
	}

   	/**
   	 * Returns true if the user should have access to a My Files
   	 * collection and false otherwise.
   	 * 
   	 * @param bs
   	 * @param user
   	 * 
   	 * @return
   	 */
    public static boolean userCanAccessMyFiles(AllModulesInjected bs, User user) {
        // For Filr, we don't support My Files for the guest or
        // external users.
        boolean reply = true;
        boolean isGuestOrExternal = (user.isShared() || (!(user.getIdentityInfo().isInternal())));
        if (Utils.checkIfFilr() && isGuestOrExternal) {
            reply = false;
        }
        else {
            // The user can access My Files if adHoc folders are
            // not allowed and the user doesn't have a home folder.
            if (useHomeAsMyFiles(bs, user) && (null == getHomeFolderId(bs, user))) {
                reply = false;
            }
        }
        return reply;
    }
    
	/**
	 * Returns true if the current user should have their My Files area
	 * mapped to their home directory and false otherwise.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static boolean useHomeAsMyFiles(AllModulesInjected bs, User user) {
		// If we're running Filr...
		if (Utils.checkIfFilr()) {
			// ...and the user has been provisioned from ldap...
			IdentityInfo idInfo = user.getIdentityInfo();
			if (idInfo.isFromLdap() || SPropsUtil.getBoolean("myfiles.allow.nonldap.homeOnly", false)) {
				// ...check the user's and/or zone setting.
				Boolean result = AdminHelper.getEffectiveAdhocFolderSetting(bs, user);
				if ((null != result) && (!(result))) {
					return true;
				}
			}
		}

		// If we get here, we're not mapping 'Home' to 'My Files.  Return
		// false.
		return false;
	}

	public static boolean useHomeAsMyFiles(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return useHomeAsMyFiles(bs, RequestContextHolder.getRequestContext().getUser());
	}

    /**
     * Routine to look for invalid wild cards.  This routine may also
     * fix up the text so that it will work
     * 
     * @param searchText
     * 
     * @return
     */
	public static String validateSearchText(String searchText) {
        if (searchText != null) {
            Pattern p = Pattern.compile("[\\s][*?][a-zA-Z0-9_]|^[*?][a-zA-Z0-9_]|[a-zA-Z0-9_][*][a-zA-Z0-9_]");
            Matcher m = p.matcher(searchText);
            if (m.find()) {
                //This request has an invalid use of the wild card character; give an error
                throw new SearchWildCardException(searchText);
            }
            p = Pattern.compile("([^a-zA-Z0-9_])([*])([^a-zA-Z0-9_])");
            m = p.matcher(searchText);
            if (m.find()) {
            	//Remove any stand alone "*" characters
            	searchText = m.replaceAll("$1$3");
            }
            p = Pattern.compile("^([*])([^a-zA-Z0-9_])");
            m = p.matcher(searchText);
            if (m.find()) {
            	//Remove any stand alone "*" characters
            	searchText = m.replaceAll("$2");
            }
            searchText = searchText.trim();
        }
        return searchText;
    }
}
