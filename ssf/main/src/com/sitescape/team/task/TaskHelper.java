package com.sitescape.team.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletSession;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.search.filter.SearchFilter;
import com.sitescape.team.search.filter.SearchFilterKeys;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.PortletRequestUtils;

public class TaskHelper {
	
	public static final String PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME = "priority";
	
	public static final String STATUS_TASK_ENTRY_ATTRIBUTE_NAME = "status";

	public static final String COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME = "completed";

	public static final String ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME = "assignment";
	
	public static final String TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME = "start_end";
	
	public enum FilterType {
		CLOSED, DAY, WEEK, MONTH, ACTIVE;
	}
	
	
	public static final FilterType FILTER_TYPE_DEFAULT = FilterType.WEEK;
	
	public static void extendTasksInfo(List folderEntries) {
		if (folderEntries == null || folderEntries.size() == 0) {
			return;
		}
		Iterator it = folderEntries.iterator();
		while (it.hasNext()) {
			 Map entry = (Map) it.next();
			 
			 Object assignment = entry.get(ObjectKeys.TASK_FIELD_ASSIGNMENT);
			 if (assignment != null) {
				 Iterator usersIt = null;
				 if (SearchFieldResult.class.isAssignableFrom(assignment.getClass())) {
					SearchFieldResult sfr = (SearchFieldResult) entry.get(ObjectKeys.TASK_FIELD_ASSIGNMENT);
					usersIt = sfr.getValueArray().iterator();
				 } else if (String.class.isAssignableFrom(assignment.getClass())) {
					usersIt = Collections.singleton(assignment).iterator();
				 }
			 
				 Collection ids = new ArrayList();
				 while (usersIt.hasNext()) {
					 Long userId = new Long((String)usersIt.next());
					 ids.add(userId);
				 }
				 if (ids != null && ids.size()>0) {
					 List assignedUsers = ResolveIds.getPrincipals(ids);
					 entry.put(WebKeys.ENTRY_USER_LIST, assignedUsers);
				 }
			 }
			 Object event = entry.get(ObjectKeys.TASK_FIELD_EVENT);
			 entry.put(WebKeys.ENTRY_DUE_DATE, event);
		}
	}
	
	public static Map extendTaskInfo(Entry entry) {
		 Map result = new HashMap();
		 
		 CustomAttribute cAttribute = entry.getCustomAttribute(ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME);
		 
		 if (cAttribute != null) {
		 
			 Set assignment = cAttribute.getValueSet();
			 if (assignment != null) {
				 Iterator usersIt = assignment.iterator();
			 
				 Collection ids = new ArrayList();
				 while (usersIt.hasNext()) {
					 Long userId = new Long((String)usersIt.next());
					 ids.add(userId);
				 }
				 if (ids != null && ids.size()>0) {
					 List assignedUsers = ResolveIds.getPrincipals(ids);
					 result.put(WebKeys.ENTRY_USER_LIST, assignedUsers);
				 }
			 }
		 }
		 return result;

	}
	
	public static String getTaskCompletedValue(Entry entry) {
	
		CustomAttribute customAttribute = entry.getCustomAttribute(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return null;
		}
		
		Set value = (Set) customAttribute.getValue();
		
		if (value == null) {
			return null;
		}
		
		Iterator it = value.iterator();
		if (it.hasNext()) {
			return (String)it.next();
		}
		
		return null;
	}
	
	public static String getTaskStatusValue(Entry entry) {
		
		CustomAttribute customAttribute = entry.getCustomAttribute(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME);

		if (customAttribute == null) {
			return null;
		}
		
		Set value = (Set) customAttribute.getValue();
		
		if (value == null) {
			return null;
		}
		
		Iterator it = value.iterator();
		if (it.hasNext()) {
			return (String)it.next();
		}
		
		return null;
	}
	
	private static String getTaskSingleValue(Map formData, String key) {
		Object values = formData.get(key);
		
		if (values == null)
			return null;
		else if (values instanceof String) 
			return (String)values;
		else if (values instanceof String[]) 
			return ((String[]) values)[0];
		
		return null;
	}
	
	public static String getTaskStatusValue(Map formData) {
		return getTaskSingleValue(formData, TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME);
	}
	
	public static String getTaskPriorityValue(Map formData) {
		return getTaskSingleValue(formData, TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME);
	}
	
	public static String getTaskCompletedValue(Map formData) {
		return getTaskSingleValue(formData, TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME);
	}
	
	public static FilterType getTaskFilterType(PortletSession portletSession) {
		return (FilterType)portletSession.getAttribute(WebKeys.TASK_CURRENT_FILTER_TYPE);
	}
	
	/**
	 * Saves given type in session or default type if given is <code>null</code> or unknown.
	 * 
	 * @param portletSession
	 * @param filterType
	 * @return
	 */
	public static FilterType setTaskRange(PortletSession portletSession, FilterType filterType) {
		if (filterType == null) {
			
			filterType = getTaskFilterType(portletSession);
			if (filterType == null) {
				filterType = FILTER_TYPE_DEFAULT;
			}
		}
		portletSession.setAttribute(WebKeys.TASK_CURRENT_FILTER_TYPE, filterType);
		return filterType;
	}

	public static SearchFilter buildSearchFilter(FilterType filterType) {
		SearchFilter searchFilter = new SearchFilter(true);
		
		if (filterType.equals(FilterType.CLOSED)) {
			searchFilter.addTaskStatuses(new String[] {"completed", "cancelled"});
		} else if (filterType.equals(FilterType.ACTIVE)) {
			searchFilter.addTaskStatuses(new String[] {"needsAction", "inProcess"});
		}
		
		DateTimeZone userTimeZone = DateTimeZone.forTimeZone(RequestContextHolder.getRequestContext().getUser().getTimeZone());
		
		if (filterType.equals(FilterType.DAY)) {
			DateTime dateTime = (new DateTime(userTimeZone)).toDateMidnight().toDateTime();
			dateTime.withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
		} else if (filterType.equals(FilterType.WEEK)) {
			DateTime dateTime = new DateTime(userTimeZone);
			dateTime = dateTime.plusWeeks(1).toDateMidnight().toDateTime().withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
		} else if (filterType.equals(FilterType.MONTH)) {
			DateTime dateTime = new DateTime(userTimeZone);
			dateTime = dateTime.plusMonths(1).toDateMidnight().toDateTime().withZone(DateTimeZone.forID("GMT"));
			searchFilter.addTaskEndDate(dateTime.toString("yyyy-MM-dd HH:mm"));
		}
		
		return searchFilter;
	}

	public static Map adjustTaskAttributesDependencies(FolderEntry entry, Map formData) {
		Map result = new HashMap(formData);
		if (!TaskHelper.isTaskEntryType(entry)) {
			return formData;
		}
		
		String newPriority = TaskHelper.getTaskPriorityValue(formData);
		String newStatus = TaskHelper.getTaskStatusValue(formData);
		String newCompleted = TaskHelper.getTaskCompletedValue(formData);

		TaskHelper.adjustTaskAttributesDependencies(entry, result, newPriority, newStatus, newCompleted);
		return result;
	}
	
	public static Map adjustTaskAttributesDependencies(String entryType, Map formData) {
		Map result = new HashMap(formData);
		if (!TaskHelper.isTaskEntryType(entryType)) {
			return formData;
		}
		
		String newPriority = TaskHelper.getTaskPriorityValue(formData);
		String newStatus = TaskHelper.getTaskStatusValue(formData);
		String newCompleted = TaskHelper.getTaskCompletedValue(formData);
		
		TaskHelper.adjustTaskAttributesDependencies(null, entryType, result, newPriority, newStatus, newCompleted);
		return result;
	}
	
	private static boolean isTaskEntryType(FolderEntry entry) {
		Definition entryDef = entry.getEntryDef();
		String entryDefId = entryDef.getId();

		return isTaskEntryType(entryDefId);
	}
	
	private static boolean isTaskEntryType(String entryDefId) {
		return entryDefId.equals(ObjectKeys.DEFAULT_ENTRY_TASK_CONFIG);
	}

	public static void adjustTaskAttributesDependencies(FolderEntry entry, Map formData, String newPriority, String newStatus, String newCompleted) {
		Definition entryDef = entry.getEntryDef();
		String entryDefId = entryDef.getId();
		TaskHelper.adjustTaskAttributesDependencies(null, entryDefId, formData, newPriority, newStatus, newCompleted);
	}
	
	public static void adjustTaskAttributesDependencies(FolderEntry entry, String entryDefId, Map formData, String newPriority, String newStatus, String newCompleted) {
		if ((entryDefId != null && !TaskHelper.isTaskEntryType(entryDefId)) ||
				(entry != null && !TaskHelper.isTaskEntryType(entry))) {
			return;
		}
		
		if (!newPriority.equals("")) {
			formData.put(TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newPriority});
		}
		
		if (!newStatus.equals("")) {
			formData.put(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newStatus});
			if (newStatus.equals("completed")) {
				formData.put(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"c100"});
			}
			
			if (entry != null) {
				String statusCurrent = TaskHelper.getTaskStatusValue(entry);
				String completedCurrent = TaskHelper.getTaskCompletedValue(entry);
				
				if ((newStatus.equals("needsAction") || newStatus.equals("inProcess")) && "completed".equals(statusCurrent) &&
						"c100".equals(completedCurrent)) {
					formData.put(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"c90"});
				}
			}
		}
		
		if (!newCompleted.equals("")) {
			formData.put(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {newCompleted});
			
			
			if (newCompleted.equals("c0")) {
				formData.put(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"needsAction"});
			}
			
			if (newCompleted.equals("c10") || newCompleted.equals("c20") ||
					newCompleted.equals("c30") || newCompleted.equals("c40") ||
					newCompleted.equals("c50") || newCompleted.equals("c60") ||
					newCompleted.equals("c70") || newCompleted.equals("c80") ||
					newCompleted.equals("c90")) {
				formData.put(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"inProcess"});
			}
			
			if (newCompleted.equals("c100")) {
				formData.put(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {"completed"});
			}		

		}
	}

}

	