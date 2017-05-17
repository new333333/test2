/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.Element;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.ServerTaskLinkage;
import org.kablink.teaming.web.util.ServerTaskLinkage.ServerTaskLink;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.util.search.Constants;

import org.springframework.web.portlet.bind.PortletRequestBindingException;

/**
 * Helper methods for code that services task requests.
 *
 * @author drfoster@novell.com
 */
public class TaskHelper {
	protected static Log m_logger = LogFactory.getLog(TaskHelper.class);
	
	// The following control aspects of code in Vibe OnPrem that has
	// been added to assist in debugging task handling.
	public static final boolean TASK_DEBUG_ENABLED = SPropsUtil.getBoolean("subtasks.debug.enabled", false);
	public static final String  TASK_MAGIC_TITLE   = "create.tasks.";
	
	// Attribute names used for items related to tasks.
	public static final String ASSIGNMENT_EXTERNAL_ENTRY_ATTRIBUTE_NAME		= "responsible_external";
	public static final String ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME	= "assignment_groups";
	public static final String ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME			= "assignment";
	public static final String ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME	= "assignment_teams";
	public static final String COMPLETED_DATE_TASK_ENTRY_ATTRIBUTE_NAME		= "completedDate";
	public static final String COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME			= "completed";
	public static final String PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME			= "priority";	
	public static final String STATUS_TASK_ENTRY_ATTRIBUTE_NAME				= "status";
	public static final String TASK_ASSIGNED_TO								= "assignedTo";
	public static final String TASK_ASSIGNED_TO_GROUPS						= "assignedToGroups";
	public static final String TASK_ASSIGNED_TO_TEAMS						= "assignedToTeams";	
	public static final String TASK_COMPLETED_DATE_ATTRIBUTE 				= "completedDate";
	public static final String TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME		= "start_end";

	// The following are the values used for task completion
	// percentages.
	private final static String COMPLETED_0   = "c000";
	private final static String COMPLETED_10  = "c010";
	private final static String COMPLETED_20  = "c020";
	private final static String COMPLETED_30  = "c030";
	private final static String COMPLETED_40  = "c040";
	private final static String COMPLETED_50  = "c050";
	private final static String COMPLETED_60  = "c060";
	private final static String COMPLETED_70  = "c070";
	private final static String COMPLETED_80  = "c080";
	private final static String COMPLETED_90  = "c090";
	private final static String COMPLETED_100 = "c100";
	
	// The following are the values used for task statuses.
	private final static String STATUS_NEEDS_ACTION = "s1";
	private final static String STATUS_IN_PROCESS   = "s2";
	private final static String STATUS_COMPLETED    = "s3";
	private final static String STATUS_CANCELED     = "s4";
	
	// Key into the session cache used to store the find task options
	// Map for use by the GWT UI.
	public final static String CACHED_FIND_TASKS_OPTIONS_KEY = "gwt-ui-find-tasks-options";

	// Enumeration type that represents the 'Quick Filters' that can be
	// applied to the tasks in a task folder.
	public enum FilterType {
		CLOSED,
		DAY,
		WEEK,
		MONTH,
		ACTIVE,
		ALL;
	}
	
	// The default filter type applied to the tasks in a task folder.
	// If nothing is stored, this is used as the default.
	public static final FilterType FILTER_TYPE_DEFAULT = FilterType.ALL;
	
	/**
	 * Adjusts the various task attributes so that they make sense in unison.
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
		
		String newPriority  = getTaskPriorityValue( formData);
		String newStatus    = getTaskStatusValue(   formData);
		String newCompleted = getTaskCompletedValue(formData);

		adjustTaskAttributesDependencies(entry, result, newPriority, newStatus, newCompleted);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static void adjustTaskAttributesDependencies(FolderEntry entry, String typeOfEntry, Map formData, String newPriority, String newStatus, String newCompleted) {
		if (((null != typeOfEntry) && (!(isTaskEntryType(typeOfEntry)))) ||
			((null != entry)       && (!(isTaskEntryType(entry))))) {
			return;
		}
		
		if (!(newPriority.equals(""))) {
			formData.put(PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newPriority});
		}
		
		boolean hasNewStatus = (!(newStatus.equals("")));
		if (hasNewStatus) {
			formData.put(STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newStatus});
			if (newStatus.equals(STATUS_COMPLETED)) {
				formData.put(COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {COMPLETED_100});
			}
			
			if (null != entry) {
				String statusCurrent = getTaskStatusValue(entry);
				String completedCurrent = getTaskCompletedValue(entry);
				
				if ((newStatus.equals(STATUS_NEEDS_ACTION) || newStatus.equals(STATUS_IN_PROCESS)) &&
						STATUS_COMPLETED.equals(  statusCurrent)                                   &&
						COMPLETED_100.equals(completedCurrent)) {
					formData.put(COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {COMPLETED_90});
				}
			}
		}
		
		if (!(newCompleted.equals(""))) {
			formData.put(COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newCompleted});			
			if (!hasNewStatus) {
				if (newCompleted.equals(COMPLETED_0)) {
					formData.put(STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {STATUS_NEEDS_ACTION});
				}
				
				else if (newCompleted.equals(COMPLETED_10) ||
					     newCompleted.equals(COMPLETED_20) ||
					     newCompleted.equals(COMPLETED_30) ||
					     newCompleted.equals(COMPLETED_40) ||
					     newCompleted.equals(COMPLETED_50) ||
					     newCompleted.equals(COMPLETED_60) ||
					     newCompleted.equals(COMPLETED_70) ||
					     newCompleted.equals(COMPLETED_80) ||
					     newCompleted.equals(COMPLETED_90)) {
					formData.put(STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {STATUS_IN_PROCESS});
				}
				
				else if (newCompleted.equals(COMPLETED_100)) {
					formData.put(STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {STATUS_COMPLETED});
				}		
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
	
	/**
	 * Returns a search filter based on a task filter type.
	 * 
	 * @param filterType
	 * @param modeType
	 * @param model
	 * @param binder
	 * 
	 * @return
	 */
	@SuppressWarnings({"unchecked", "incomplete-switch", "deprecation"})
	public static SearchFilter buildSearchFilter(FilterType filterType, ListFolderHelper.ModeType modeType, Map model, Binder binder) {
		SearchFilter searchFilter = new SearchFilter(true);
		
		DateTimeZone userTimeZone = DateTimeZone.forTimeZone(RequestContextHolder.getRequestContext().getUser().getTimeZone());
		
		// Handle the filter type.
		DateTime dateTime;
		switch (filterType) {
		case CLOSED:
			searchFilter.addTaskStatuses(new String[] {STATUS_COMPLETED, STATUS_CANCELED});
			break;
		
		case ACTIVE:
			searchFilter.addTaskStatuses(new String[] {STATUS_NEEDS_ACTION, STATUS_IN_PROCESS});
			break;

		case DAY:
			dateTime = (new DateTime(userTimeZone)).plusDays(1).toDateMidnight().toDateTime().withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
			searchFilter.addTaskStatuses(new String[] {STATUS_NEEDS_ACTION, STATUS_IN_PROCESS});			
			break;

		case WEEK:
			dateTime = new DateTime(userTimeZone);
			dateTime = dateTime.plusWeeks(1).toDateMidnight().toDateTime().withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
			searchFilter.addTaskStatuses(new String[] {STATUS_NEEDS_ACTION, STATUS_IN_PROCESS});
			break;

		case MONTH:
			dateTime = new DateTime(userTimeZone);
			dateTime = dateTime.plusMonths(1).toDateMidnight().toDateTime().withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
			searchFilter.addTaskStatuses(new String[] {STATUS_NEEDS_ACTION, STATUS_IN_PROCESS});
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
	
	public static SearchFilter buildSearchFilter(FilterType filterType) {
		return buildSearchFilter(filterType, ListFolderHelper.ModeType.PHYSICAL, null, null);
	}
	
	/**
	 * Returns true if the current user has rights to modify the
	 * TaskLinkage on the given Binder and false otherwise.
	 * 
	 * @param bs
	 * @param binder
	 */
	public static boolean canModifyTaskLinkage(HttpServletRequest hRequest, AllModulesInjected bs, Binder binder) {
		return (GwtUIHelper.isGwtUIActive(hRequest) && canModifyTaskLinkageImpl(bs, binder));
	}
	
	public static boolean canModifyTaskLinkage(PortletRequest pRequest, AllModulesInjected bs, Binder binder) {
		return (GwtUIHelper.isGwtUIActive(pRequest) && canModifyTaskLinkageImpl(bs, binder));
	}
	
	private static boolean canModifyTaskLinkageImpl(AllModulesInjected bs, Binder binder) {
		return bs.getBinderModule().testAccess(binder, BinderOperation.setProperty);
	}

	/**
	 * Called to read task entries.
	 * 
	 * @param bs
	 * @param request
	 * @param binder
	 * @param model
	 * @param options
	 * 
	 * @return
	 * 
	 * @throws PortletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	public static Map findTaskEntries(
			AllModulesInjected	bs,
			RenderRequest		request, 
			Binder				binder,
			Map					model,
			Map					options)
				throws PortletRequestBindingException {
		// Read the requested task entries.
		Map taskEntries = findTaskEntriesImpl(
			bs,
			binder,
			model,
			PortletRequestUtils.getStringParameter(request, WebKeys.TASK_FILTER_TYPE, null),
			PortletRequestUtils.getStringParameter(request, WebKeys.FOLDER_MODE_TYPE, null),
			options);

		// If we're in the GWT UI (i.e., not mobile, ...)...
		if (GwtUIHelper.isGwtUIActive(request)) {
			// ...write the options used for the search (which includes
			// ...the filter, ...) to the session cache.
			HttpServletRequest hRequest = WebHelper.getHttpServletRequest(request);
			HttpSession        hSession = WebHelper.getRequiredSession(hRequest);
			
			hSession.setAttribute(CACHED_FIND_TASKS_OPTIONS_KEY, new GwtUISessionData(options));
		}

		// If we get here, taskEntries refers to a Map of the entries
		// that were read.  Return it.
		return taskEntries;
	}
	
	/**
	 * Called to read task entries.
	 * 
	 * @param bs
	 * @param binder
	 * @param filterType
	 * @Param modeType
	 * @param options
	 * 
	 * @return
	 * 
	 * @throws PortletRequestBindingException
	 */
	@SuppressWarnings("unchecked")
	public static Map findTaskEntries(
			AllModulesInjected	bs,
			Binder				binder,
			String				filterType,
			String				modeType,
			Map					options)
				throws PortletRequestBindingException {
		return
			findTaskEntriesImpl(
				bs,
				binder,
				new HashMap(),	// The model is not used with the GWT UI.
				filterType,
				modeType,
				options);
	}
	
	/*
	 * Private method used by the versions of findTaskEntries() above
	 * to actually read the request task entries.
	 */
	@SuppressWarnings("unchecked")
	private static Map findTaskEntriesImpl(
			AllModulesInjected	bs,
			Binder				binder,
			Map					model,
			String				filterTypeParam,
			String				modeTypeParam,
			Map					options)
				throws PortletRequestBindingException {
		User user = RequestContextHolder.getRequestContext().getUser();
		Long userId = user.getId();
		model.put(WebKeys.USER_PRINCIPAL, user);
		Long binderId = binder.getId();
		
		Map folderEntries = new HashMap();

		// What are we filtering for?  (Closed, Today, Week, ...)
		FilterType filterType = ((filterTypeParam != null) ? FilterType.valueOf(filterTypeParam) : null);
		filterType = setTaskFilterType(bs, userId, binderId, filterType);
		model.put(WebKeys.TASK_CURRENT_FILTER_TYPE, filterType);

		// Are we producing a physical or virtual listing?
		ModeType modeType;
		Boolean showModeSelect;
		Workspace binderWs = BinderHelper.getBinderWorkspace(binder);
		if (BinderHelper.isBinderUserWorkspace(binderWs)) {
			modeType = ListFolderHelper.setFolderModeType(bs, userId, binderId, ((modeTypeParam != null) ? ModeType.valueOf(modeTypeParam) : null));
			showModeSelect = Boolean.TRUE;
		}
		else {
			modeType = ListFolderHelper.setFolderModeType(bs, userId, binderId, ModeType.PHYSICAL);
			showModeSelect = Boolean.FALSE;
		}
		model.put(WebKeys.FOLDER_CURRENT_MODE_TYPE, modeType);
		model.put(WebKeys.FOLDER_SHOW_MODE_SELECT,  showModeSelect);

		options.put(ObjectKeys.FOLDER_MODE_TYPE, modeType);
		options.put(ObjectKeys.SEARCH_SEARCH_DYNAMIC_FILTER, buildSearchFilter(filterType, modeType, model, binder).getFilter());

		// If the caller hasn't specified...
		if (!(options.containsKey(ObjectKeys.SEARCH_SORT_BY))) {
			// ...sort by the ID...
			options.put(ObjectKeys.SEARCH_SORT_BY, Constants.SORTNUMBER_FIELD);
		}
		if (!(options.containsKey(ObjectKeys.SEARCH_SORT_DESCEND))) {
			// ...ascending.
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE);
		}
       	
		// Are we finding all assigned tasks?
		if (modeType.equals(ModeType.VIRTUAL)) {
			// Yes!  Is there a search filter in effect?
			Document searchFilter = ((Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER));
			if (null != searchFilter) {
				// Yes!  Does it contain any <filterTerms>'s?
				List<Element> filterTermsList = ((List<Element>) searchFilter.getRootElement().selectNodes("//filterTerms"));
				if ((null != filterTermsList) && (0 < filterTermsList.size())) {
					// Yes!  Scan them.
					for (Element filterTerms:  filterTermsList) {
						// Does this <filterTerms> contain any
						// <filterTerm filterType="foldersList">'s?
						List<Element> filterTermList = ((List<Element>) filterTerms.selectNodes("//filterTerm[@filterType='foldersList']"));
						if ((null != filterTermList) && (0 < filterTermsList.size())) {
							// Yes!  Scan them...
							for (Element filterTerm:  filterTermList) {
								// ...removing each from its parent
								// ...node.
								filterTerm.getParent().remove(filterTerm);
							}
						}
					}
				}
			}
		}
       	
		if (binder instanceof Folder) {
			folderEntries = bs.getFolderModule().getEntries(binderId, options);
		} else {
			//a template
			folderEntries = new HashMap();
		}

		// If we have any virtual entries...
		if ((0 < folderEntries.size()) && (ModeType.VIRTUAL == modeType)) {
			// ...we need to pass information about their binder's too.
			List<Long> binderIds = new ArrayList<Long>();
	    	List items = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
	    	if (items != null) {
		    	Iterator it = items.iterator();
		    	while (it.hasNext()) {
		    		Map entry = (Map)it.next();
		    		String entryBinderId = (String)entry.get(Constants.BINDER_ID_FIELD);
		    		binderIds.add(Long.valueOf(entryBinderId));
		    	}
	    	}
			model.put(WebKeys.FOLDER_LIST, bs.getBinderModule().getBinders(binderIds));
		}

		Map<String, Map> cacheEntryDef = new HashMap();
    	List items = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
    	if (items != null) {
	    	Iterator it = items.iterator();
	    	while (it.hasNext()) {
	    		Map entry = (Map)it.next();
	    		String entryDefId = (String)entry.get(Constants.COMMAND_DEFINITION_FIELD);
	    		if (cacheEntryDef.get(entryDefId) == null) {
	    			cacheEntryDef.put(entryDefId, bs.getDefinitionModule().getEntryDefinitionElements(entryDefId));
	    		}
	    		entry.put(WebKeys.ENTRY_DEFINTION_ELEMENT_DATA, cacheEntryDef.get(entryDefId));
	    	}
    	}
		
		return folderEntries;
	}

	/**
	 * Given a task folder and an entry ID of a task, returns that task
	 * s parent task ID or null if the task isn't a subtask, or the
	 * task linkage cannot be accessed.
	 * 
	 * @param taskFolder
	 * @param taskId
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Long getParentTaskId(Folder taskFolder, Long taskId) {
		// If the folder doesn't have a task linkage serialization map
		// stored on it...
		Map serializationMap = ((Map) taskFolder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE));
		if ((null == serializationMap) || serializationMap.isEmpty()) {
			// ...there can be no parent task.
			return null;
		}

		// If we can find the parent task of the task whose entry ID we
		// were given, return its ID, otherwise, return null.
		ServerTaskLinkage tl = ServerTaskLinkage.loadSerializationMap(serializationMap);
		ServerTaskLink parentTask = ServerTaskLinkage.findTaskContainingTask(tl, taskId);
		return ((null == parentTask) ? null : parentTask.getEntryId());
	}
	
	
	/**
	 * Returns the task completed value from an entry.
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

	@SuppressWarnings("unchecked")
	public static String getTaskCompletedValue(Map formData) {
		return getTaskSingleValue(formData, COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);
	}
	
	/**
	 * Returns a filter type stored in the session cache.
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * 
	 * @return
	 */
	public static FilterType getTaskFilterType(AllModulesInjected bs, Long userId, Long binderId) {
		UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(userId, binderId);
		return ((FilterType) userFolderProperties.getProperty(WebKeys.TASK_CURRENT_FILTER_TYPE));
	}
	
	/**
	 * Return the 'Mode' (physical vs. virtual) for a task folder.
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
	 * Returns a task priority from an entry.
	 *  
	 * @param formData
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getTaskPriorityValue(Entry entry) {		
		CustomAttribute customAttribute = entry.getCustomAttribute(PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME);
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
	
	@SuppressWarnings("unchecked")
	public static String getTaskPriorityValue(Map formData) {
		return getTaskSingleValue(formData, PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME);
	}
	
	/*
	 * Returns a single value based on a key from a form input Map.
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
	 * Returns a task status from an entry.
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
	
	@SuppressWarnings("unchecked")
	public static String getTaskStatusValue(Map formData) {
		return getTaskSingleValue(formData, STATUS_TASK_ENTRY_ATTRIBUTE_NAME);
	}

	/**
	 * Returns true if folder is being viewed as a task listing and
	 * false otherwise.
	 * 
	 * @param folder
	 * 
	 * @return
	 */
	public static boolean isTaskFolderType(Folder folder) {
		Definition folderDef = folder.getDefaultViewDef();
		String viewType = DefinitionUtils.getViewType(folderDef.getDefinition());
		return isTaskFolderType(viewType);
	}
	
	private static boolean isTaskFolderType(String viewType) {
		return viewType.equals(Definition.VIEW_STYLE_TASK);		
	}
	
	/**
	 * Returns true if a folder entry is a task and false otherwise.
	 * 
	 * @param entry
	 * 
	 * @return
	 */
	public static boolean isTaskEntryType(FolderEntry entry) {
		String family = DefinitionUtils.getFamily(entry.getEntryDefDoc());		
		return isTaskEntryType(family);
	}

	/**
	 * Returns true if the family from an entry definition is that of a
	 * task and false otherwise.
	 * 
	 * @param family
	 * 
	 * @return
	 */
	public static boolean isTaskEntryType(String family) {
		boolean reply;
		if (null == family)
		     reply = false;
		else reply = ObjectKeys.FAMILY_TASK.equals(family);
		return reply;
	}
	
	/**
	 * Called when a task is modified or created to process the
	 * completion value.
	 * 
	 * @param task
	 * @param inputData
	 * @param entryData
	 */
	@SuppressWarnings("unchecked")
	public static void processTaskCompletion(Entry task, InputDataAccessor inputData, Map entryData) {
		Binder parentBinder = task.getParentBinder();
		if (BinderHelper.isBinderTask(null, parentBinder)) {
			// Validate the completed value in the input data...
			String c = inputData.getSingleValue(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);
			if (null == c) {
				String s = inputData.getSingleValue(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME);
				if ((null != s) && s.equals(STATUS_COMPLETED)) {
					c = COMPLETED_100;
				}
			}
			String cV = ((null == c) ? null : validateCompleted(c));
			if (null != cV) {
				c = cV;
			}
			
			// ...and use that to update the completion date, as necessary.
			processTaskCompletionDate(task, c, inputData);
	
			// Does the entry data contain a completed value?
			Object o = entryData.get(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);
			boolean asString  = false;
			boolean asStringA = false;
			if (null != o) {
				// Yes!  Validate it...
				if      (o instanceof String)   {c = ((String)   o);    asString  = true;}
				else if (o instanceof String[]) {c = ((String[]) o)[0]; asStringA = true;}
				else                             c = null;
				cV = validateCompleted(c);
			
				// ...and if it needs to be changed...
				if (null != cV) {
					// ...store the new value.
					if      (asString)  o = cV;
					else if (asStringA) o = new String[]{cV};
					entryData.put(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, o);
				}
			}
		}
	}

	/*
	 * Validates a task's completed value.  If the value passed in
	 * is valid, null is returned.  Otherwise, a valid value for it
	 * is returned.
	 */
	private static String validateCompleted(String completed) {
		String reply = COMPLETED_0;	// Default to 0%.
		
		// Do we have a completed value?
		if (MiscUtil.hasString(completed)) {
			// Yes!  Does it start with a 'c'?
			char firstChar = completed.charAt(0);
			if (('C' == firstChar) || ('c' == firstChar)) {
				// Yes!  Does it contain stuff after the 'c'?
				String percentS = completed.substring(1);
				if (MiscUtil.hasString(percentS)) {
					try {
						// Yes!  Can we parse that as an integer?
						int percentI = Integer.parseInt(percentS);
						
						// Yes!  Range check it between 0 and 100...
						int fixedPercentI;
						if      (  0 > percentI) fixedPercentI = 0;
						else if (100 < percentI) fixedPercentI = 100;
						else                     fixedPercentI = percentI;

						// ...and round it to the nearest 10.
						int tens = (fixedPercentI / 10);
						int ones = (fixedPercentI % 10);
						if (ones >= 5) {
							tens += 1;
						}
						fixedPercentI = (tens * 10);
						
						// Do we need to change the percentage?
						if (fixedPercentI == percentI) {
							// No!  Return null.
							reply = null;
						}
						
						else {
							// Yes, we need to change the percentage!
							// Generate a new percentage string and
							// return that.
							percentS = String.valueOf(fixedPercentI);
							int l =  percentS.length();
							switch (l) {
							case 1:  percentS = ("00" + percentS); break;
							case 2:  percentS = ( "0" + percentS); break;
							}
							reply = ("c" + percentS);
						}
					}
					catch (NumberFormatException nfe) {
						// No, we couldn't parse it as an integer!
					}
				}
			}
		}

		// If we generated a valid value and debugging is enabled...
		if ((null != reply) && m_logger.isDebugEnabled()) {
			// ...trace what we did.
			m_logger.debug("TaskHelper.validateCompleted( " + ((null == completed) ? "<null>" : completed) + " ):  Changed to:  " + reply);
		}

		// If we get here, reply is null if the completed value was
		// valid or it refers to a validate value for the completed
		// value.
		return reply;
	}
	
	/*
	 * Called when a task is modified or created to process the
	 * completion date.
	 */
	private static void processTaskCompletionDate(Entry task, String completeS, InputDataAccessor inputData) {
		// Do we have a 'complete' setting for a FolderEntry?
		if ((!(MiscUtil.hasString(completeS))) || (!(task instanceof FolderEntry))) {
			// No!  Nothing to do.
			return;
		}
		
		// Is the completion value for the task changing?
		boolean complete     = completeS.equals(COMPLETED_100);
		String  wasCompleteS = getTaskCompletedValue(task);
		boolean wasComplete  = (MiscUtil.hasString(wasCompleteS) && wasCompleteS.equals(COMPLETED_100));		
		if (complete == wasComplete) {
			// No!  Nothing to do.
			return;
		}

		// If we already have a completed date stored for the task...
		CustomAttribute completedDateAttr = task.getCustomAttribute(TASK_COMPLETED_DATE_ATTRIBUTE);
		Date completedDate = ((null == completedDateAttr) ? null : ((Date) completedDateAttr.getValue()));		
		boolean hasCompletedDate = (null != completedDate);
		if (hasCompletedDate) {
			// ...remove it.
			task.removeCustomAttribute(completedDateAttr);
		}
		
		// If the task is now being marked completed...
		if (complete) {
			// ...write the completion date/time to the entry.
			Date idDate = inputData.getDateValue(COMPLETED_DATE_TASK_ENTRY_ATTRIBUTE_NAME);
			task.addCustomAttribute(TASK_COMPLETED_DATE_ATTRIBUTE, ((null == idDate) ? new Date() : idDate));
		}		
	}

	/**
	 * Called from the JSP controller's when a task is removed so that
	 * the folder's linkage can be fixed up.
	 * 
	 * @param bs
	 * @param folder
	 * @param entryId
	 */
	@SuppressWarnings("unchecked")
	public static void removeTaskFromLinkage(AllModulesInjected bs, Folder folder, Long entryId) {
		// If the current user can't modify the binder linkage...
		if (!(canModifyTaskLinkageImpl(bs, folder))) {
			// ...don't even try.
			return;
		}
		
		// If the folder doesn't have a task linkage serialization map
		// stored on it...
		Map serializationMap = ((Map) folder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE));
		if ((null == serializationMap) || serializationMap.isEmpty()) {
			// ...there's nothing to remove from.
			return;
		}

		// Parse the serialized task linkage...
		ServerTaskLinkage tl = ServerTaskLinkage.loadSerializationMap(serializationMap);

		// ...find the task list and task that's being removed...
		List<ServerTaskLink> fixList = ServerTaskLinkage.findTaskList(tl, entryId);
		if (null == fixList) {
			return;
		}
		ServerTaskLink task = ServerTaskLinkage.findTask(fixList, entryId);
		int taskIndex = fixList.indexOf(task);
		
		// ...scan the subtasks of the task being removed...
		List<ServerTaskLink> subtaskList = task.getSubtasks();
		int count = subtaskList.size();
		for (int i = (count - 1); i >= 0; i -= 1) {
			// ...moving each subtask into the hierarchy where it's
			// ...parent task was... 
			ServerTaskLink subtask = subtaskList.get(i);
			subtaskList.remove(i);
			fixList.add(taskIndex, subtask);
		}
		
		// ...remove the task being removed from its list...
		fixList.remove(task);

		// ...and finally, store the modified linkage back to the task
		// ...folder.
		bs.getBinderModule().setProperty(
			folder.getId(),
			ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE,
			tl.getSerializationMap());
	}

	/**
	 * Saves given type in session or default type if given is
	 * <code>null</code> or unknown.
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * @param filterType
	 * 
	 * @return
	 */
	public static FilterType setTaskFilterType(AllModulesInjected bs, Long userId, Long binderId, FilterType filterType) {
		if (null == filterType) {
			filterType = getTaskFilterType(bs, userId, binderId);
			if (null == filterType) {
				filterType = FILTER_TYPE_DEFAULT;
			}
		}
		
		bs.getProfileModule().setUserProperty(userId, binderId, WebKeys.TASK_CURRENT_FILTER_TYPE, filterType);
		return filterType;
	}
	
	/**
	 * Saves given folder mode in session or default mode if given is
	 * <code>null</code> or unknown.
	 * 
	 * @param bs
	 * @param userId
	 * @param binderId
	 * @param modeType
	 * 
	 * @return
	 */
	public static ListFolderHelper.ModeType setTaskModeType(AllModulesInjected bs, Long userId, Long binderId, ListFolderHelper.ModeType modeType) {
		return ListFolderHelper.setFolderModeType(bs, userId, binderId, modeType);
	}	
}
