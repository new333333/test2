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
package org.kablink.teaming.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletSession;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;


public class TaskHelper {
	public static final String ASSIGNMENT_EXTERNAL_ENTRY_ATTRIBUTE_NAME		= "responsible_external";
	public static final String ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME	= "assignment_groups";
	public static final String ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME			= "assignment";
	public static final String ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME	= "assignment_teams";
	public static final String COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME			= "completed";
	public static final String PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME			= "priority";	
	public static final String STATUS_TASK_ENTRY_ATTRIBUTE_NAME				= "status";
	public static final String TASK_ASSIGNED_TO								= "assignedTo";
	public static final String TASK_ASSIGNED_TO_GROUPS						= "assignedToGroups";
	public static final String TASK_ASSIGNED_TO_TEAMS						= "assignedToTeams";	
	public static final String TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME		= "start_end";
	
	public enum FilterType {
		CLOSED,
		DAY,
		WEEK,
		MONTH,
		ACTIVE,
		ALL;
	}
	public static final FilterType FILTER_TYPE_DEFAULT = FilterType.ALL;

	/**
	 * 
	 * @param entry
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTaskCompletedValue(Entry entry) {	
		CustomAttribute customAttribute = entry.getCustomAttribute(COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);
		if (null == customAttribute) {
			return null;
		}
		
		Set value = ((Set) customAttribute.getValueSet());		
		if (null == value) {
			return null;
		}
		
		Iterator it = value.iterator();
		if (it.hasNext()) {
			return ((String) it.next());
		}
		
		return null;
	}

	/**
	 * 
	 * @param entry
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTaskStatusValue(Entry entry) {		
		CustomAttribute customAttribute = entry.getCustomAttribute(STATUS_TASK_ENTRY_ATTRIBUTE_NAME);
		if (null == customAttribute) {
			return null;
		}
		
		Set value = ((Set) customAttribute.getValueSet());		
		if (null == value) {
			return null;
		}
		
		Iterator it = value.iterator();
		if (it.hasNext()) {
			return ((String) it.next());
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param formData
	 * @param key
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static String getTaskSingleValue(Map formData, String key) {
		Object values = formData.get(key);
		
		if      (values == null)             return null;
		else if (values instanceof String)   return ((String)   values);
		else if (values instanceof String[]) return ((String[]) values)[0];
		
		return null;
	}
	
	/**
	 * 
	 * @param formData
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTaskStatusValue(Map formData) {
		return getTaskSingleValue(formData, STATUS_TASK_ENTRY_ATTRIBUTE_NAME);
	}
	
	/**
	 * 
	 * @param formData
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTaskPriorityValue(Map formData) {
		return getTaskSingleValue(formData, PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME);
	}
	
	/**
	 * 
	 * @param formData
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTaskCompletedValue(Map formData) {
		return getTaskSingleValue(formData, COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);
	}
	
	/**
	 * 
	 * @param portletSession
	 * 
	 * @return
	 */
	public static FilterType getTaskFilterType(PortletSession portletSession) {
		return ((FilterType) portletSession.getAttribute(WebKeys.TASK_CURRENT_FILTER_TYPE));
	}
	
	/**
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * 
	 * @return
	 */
	public static ModeType getTaskModeType(AllModulesInjected bs, Long userId, Long binderId) {
		return ListFolderHelper.getFolderModeType(bs, userId, binderId);
	}
	
	/**
	 * Saves given type in session or default type if given is <code>null</code> or unknown.
	 * 
	 * @param portletSession
	 * @param filterType
	 * @return
	 */
	public static FilterType setTaskFilterType(PortletSession portletSession, FilterType filterType) {
		if (null == filterType) {
			filterType = getTaskFilterType(portletSession);
			if (null == filterType) {
				filterType = FILTER_TYPE_DEFAULT;
			}
		}
		
		portletSession.setAttribute(WebKeys.TASK_CURRENT_FILTER_TYPE, filterType);
		return filterType;
	}

	/**
	 * Saves given folder mode in session or default mode if given is <code>null</code> or unknown.
	 * 
	 * @param portletSession
	 * @param modeType
	 * @return
	 */
	public static ListFolderHelper.ModeType setTaskModeType(AllModulesInjected bs, Long userId, Long binderId, ListFolderHelper.ModeType modeType) {
		return ListFolderHelper.setFolderModeType(bs, userId, binderId, modeType);
	}
	
	/**
	 * 
	 * @param filterType
	 * 
	 * @return
	 */
	public static SearchFilter buildSearchFilter(FilterType filterType) {
		return buildSearchFilter(filterType, ListFolderHelper.ModeType.PHYSICAL, null, null);
	}
	
	/**
	 * 
	 * @param filterType
	 * @param modeType
	 * @param model
	 * @param binder
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static SearchFilter buildSearchFilter(FilterType filterType, ListFolderHelper.ModeType modeType, Map model, Binder binder) {
		SearchFilter searchFilter = new SearchFilter(true);
		
		DateTimeZone userTimeZone = DateTimeZone.forTimeZone(RequestContextHolder.getRequestContext().getUser().getTimeZone());
		
		// Handle the filter type.
		DateTime dateTime;
		switch (filterType) {
		case CLOSED:
			searchFilter.addTaskStatuses(new String[] {"s3", "s4"});
			break;
		
		case ACTIVE:
			searchFilter.addTaskStatuses(new String[] {"s1", "s2"});
			break;

		case DAY:
			dateTime = (new DateTime(userTimeZone)).plusDays(1).toDateMidnight().toDateTime().withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
			searchFilter.addTaskStatuses(new String[] {"s1", "s2"});			
			break;

		case WEEK:
			dateTime = new DateTime(userTimeZone);
			dateTime = dateTime.plusWeeks(1).toDateMidnight().toDateTime().withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
			searchFilter.addTaskStatuses(new String[] {"s1", "s2"});
			break;

		case MONTH:
			dateTime = new DateTime(userTimeZone);
			dateTime = dateTime.plusMonths(1).toDateMidnight().toDateTime().withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
			searchFilter.addTaskStatuses(new String[] {"s1", "s2"});
			break;
		}

		// Are we only showing tasks assigned to something?
		if (ListFolderHelper.ModeType.VIRTUAL == modeType) {
  		   	String searchAsUser = "";
  		   	String searchAsTeam = "";
  		   	
  		   	// Yes!  If the binder's workspace is a Team workspace...
   	       	Workspace binderWs = BinderHelper.getBinderWorkspace(binder);
  		   	if (BinderHelper.isBinderTeamWorkspace(binderWs)) {
  	  		   	// ...we search for that Team's tasks...
	   			searchAsTeam = String.valueOf(binderWs.getId());
  		   	}
  		   	else {
  		   		// ...otherwise, we search for owner of the workspace's
  		   		// ...tasks...
  		   		searchAsUser = String.valueOf(binderWs.getOwnerId());	// RequestContextHolder.getRequestContext().getUser().getId()
  		   	}

  		   	// Setup the assignees to search for.
			SearchUtils.setupAssignees(
				searchFilter,
				model,
				searchAsUser,
				"",	// No special 'group assignee' setup required.
				searchAsTeam,
				SearchUtils.AssigneeType.TASK);
		}

		return searchFilter;
	}

	/**
	 * 
	 * @param entry
	 * @param formData
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map adjustTaskAttributesDependencies(FolderEntry entry, Map formData) {
		Map result = new HashMap(formData);
		if (!(isTaskEntryType(entry))) {
			return formData;
		}
		
		String newPriority = getTaskPriorityValue(formData);
		String newStatus = getTaskStatusValue(formData);
		String newCompleted = getTaskCompletedValue(formData);

		adjustTaskAttributesDependencies(entry, result, newPriority, newStatus, newCompleted);
		return result;
	}
	
	private static boolean isTaskEntryType(FolderEntry entry) {
		String family = DefinitionUtils.getFamily(entry.getEntryDefDoc());		
		return isTaskEntryType(family);
	}
	
	private static boolean isTaskEntryType(String family) {
		return ObjectKeys.FAMILY_TASK.equals(family);
	}

	/**
	 *
	 * @param entry
	 * @param typeOfEntry
	 * @param formData
	 * @param newPriority
	 * @param newStatus
	 * @param newCompleted
	 */
	@SuppressWarnings("unchecked")
	public static void adjustTaskAttributesDependencies(FolderEntry entry, String typeOfEntry, Map formData, String newPriority, String newStatus, String newCompleted) {
		if (((null != typeOfEntry) && (!(isTaskEntryType(typeOfEntry)))) ||
			((null != entry)       && (!(isTaskEntryType(entry))))) {
			return;
		}
		
		if (!(newPriority.equals(""))) {
			formData.put(PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newPriority});
		}
		
		if (!(newStatus.equals(""))) {
			formData.put(STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newStatus});
			if (newStatus.equals("s3")) {
				formData.put(COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"c100"});
			}
			
			if (null != entry) {
				String statusCurrent = getTaskStatusValue(entry);
				String completedCurrent = getTaskCompletedValue(entry);
				
				if ((newStatus.equals("s1") || newStatus.equals("s2")) &&
						"s3".equals(  statusCurrent)                   &&
						"c100".equals(completedCurrent)) {
					formData.put(COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"c090"});
				}
			}
		}
		
		if (!(newCompleted.equals(""))) {
			formData.put(COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newCompleted});			
			
			if (newCompleted.equals("c000")) {
				formData.put(STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"s1"});
			}
			
			else if (newCompleted.equals("c010") ||
				     newCompleted.equals("c020") ||
				     newCompleted.equals("c030") ||
				     newCompleted.equals("c040") ||
				     newCompleted.equals("c050") ||
				     newCompleted.equals("c060") ||
				     newCompleted.equals("c070") ||
				     newCompleted.equals("c080") ||
				     newCompleted.equals("c090")) {
				formData.put(STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"s2"});
			}
			
			else if (newCompleted.equals("c100")) {
				formData.put(STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"s3"});
			}		

		}
	}

	@SuppressWarnings("unchecked")
	public static void adjustTaskAttributesDependencies(FolderEntry entry, Map formData, String newPriority, String newStatus, String newCompleted) {
		// Always use the initial form of the method.
		adjustTaskAttributesDependencies(
			entry,
			DefinitionUtils.getFamily(entry.getEntryDefDoc()),
			formData,
			newPriority,
			newStatus,
			newCompleted);
	}	
}
