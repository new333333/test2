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
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskLinkage.TaskLink;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DateComparer;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.search.Constants;


/**
 * Helper methods for the GWT UI server code that services task
 * requests.
 *
 * @author drfoster@novell.com
 */
public class GwtTaskHelper {
	protected static Log m_logger = LogFactory.getLog(GwtTaskHelper.class);

	/*
	 * Converts a String to a Long, if possible, and adds it to a
	 * List<Long>.
	 */
	private static void addLongFromStringToList(String s, List<Long> l) {
		try {
			Long lVal = Long.parseLong(s);
			l.add(lVal);
		}
		catch (NumberFormatException nfe) {
		}
	}

	/*
	 * Generates a String to write to the log for a boolean.
	 */
	private static String buildDumpString(String label, boolean v) {
		return (label + ":  " + String.valueOf(v));
	}
	
	/*
	 * Generates a String to write to the log for a Long.
	 */
	private static String buildDumpString(String label, Long v) {
		return (label + ":  " + ((null == v) ? "null" : String.valueOf(v)));
	}
	
	/*
	 * Generates a String to write to the log for a List<Long>.
	 */
	private static String buildDumpString(String label, List<Long> v) {
		if (null == v) {
			v = new ArrayList<Long>();
		}
		
		StringBuffer buf = new StringBuffer(label);
		buf.append(":  ");
		if (v.isEmpty()) {
			buf.append("EMPTY");
		}
		else {
			int c = 0;
			for (Long l:  v) {
				if (0 < c++) {
					buf.append(", ");
				}
				buf.append(String.valueOf(l));
			}
		}
		return buf.toString();
	}
	
	/*
	 * Generates a String to write to the log for a String.
	 */
	private static String buildDumpString(String label, String v) {
		return (label + ":  '" + ((null == v) ? "null" : v) + "'");
	}
	
	/*
	 * Logs the contents of a List<TaskInfo>.
	 */
	private static void dumpTaskInfoList(List<TaskInfo> tasks) {		
		// If debug logging is disabled...
		if (!(m_logger.isDebugEnabled())) {
			// ...bail.
			return;
		}

		// If there are no TaskInfo's in the list...
		if (tasks.isEmpty()) {
			// ...log that fact and bail.
			m_logger.debug("GwtTaskHelper.dumpTaskInfoList( EMPTY )");
			return;
		}

		// Dump the tasks.
		m_logger.debug("GwtTaskHelper.dumpTaskInfoList( START:  " + String.valueOf(tasks.size()) + " )");
		for (TaskInfo ti:  tasks) {
			StringBuffer buf = new StringBuffer(buildDumpString("\n\tTask ID", ti.getTaskId()));
			buf.append(buildDumpString("\n\t\tOverdue",          ti.getOverdue()         ));
			buf.append(buildDumpString("\n\t\tSeen",             ti.getSeen()            ));
			buf.append(buildDumpString("\n\t\tAssignments",      ti.getAssignments()     ));
			buf.append(buildDumpString("\n\t\tAssignmentGroups", ti.getAssignmentGroups()));
			buf.append(buildDumpString("\n\t\tAssignmentTeams",  ti.getAssignmentTeams() ));
			buf.append(buildDumpString("\n\t\tBinder ID",        ti.getBinderId()        ));
			buf.append(buildDumpString("\n\t\tCompleted",        ti.getCompleted()       ));
			buf.append(buildDumpString("\n\t\tEntityType",       ti.getEntityType()      ));
			buf.append(buildDumpString("\n\t\tPriority",         ti.getPriority()        ));
			buf.append(buildDumpString("\n\t\tTitle",            ti.getTitle()           ));
			buf.append(buildDumpString("\n\t\tStatus",           ti.getStatus()          ));
			m_logger.debug("GwtTaskHelper.dumpTaskInfoList()" + buf.toString());
		}
		m_logger.debug("GwtTaskHelper.dumpTaskInfoList( END )");
	}

	/*
	 * Logs the contents of a TaskLinkage object.
	 */
	private static void dumpTaskLinkage(TaskLinkage linkage) {
		// If debug logging is disabled...
		if (!(m_logger.isDebugEnabled())) {
			// ...bail.
			return;
		}

		// If there are no TaskLink's in the task order list...
		List<TaskLink> taskOrder = linkage.getTaskOrder();
		if (taskOrder.isEmpty()) {
			// ...log that fact and bail.
			m_logger.debug("GwtTaskHelper.dumpTaskLinkage( EMPTY )");
			return;
		}

		// Scan the TaskLink's in the task order list...
		m_logger.debug("GwtTaskHelper.dumpTaskLinkage( START )");
		for (TaskLink tl:  taskOrder) {
			// ...logging each of them.
			dumpTaskLink(tl, 0);
		}
		m_logger.debug("GwtTaskHelper.dumpTaskLinkage( END )");
	}

	/*
	 * Logs the contents of a TaskLink object.  The information is
	 * indented depth levels.
	 */
	private static void dumpTaskLink(TaskLink tl, int depth) {
		// Build a string to write to the log...
		StringBuffer logBuffer = new StringBuffer("   ");
		for (int i = 0; i < depth; i += 1) {
			logBuffer.append("   ");
		}
		logBuffer.append(String.valueOf(depth));
		logBuffer.append(":");
		logBuffer.append(String.valueOf(tl.getTaskId()));
		
		// ...and write it.
		m_logger.debug(logBuffer.toString());
		
		// Scan this TaskLink's subtasks...
		int subtaskDepth = (depth + 1);
		for (TaskLink tlScan:  tl.getSubtasks()) {
			// ...logging each of them one level deeper.
			dumpTaskLink(tlScan, subtaskDepth);
		}
	}

	/*
	 * Searches a List<TaskInfo> for a task with a specific ID.
	 */
	private static TaskInfo findTaskInList(Long taskId, List<TaskInfo> tasks) {
		// Scan the List<TaskInfo>.
		for (TaskInfo task:  tasks) {
			// Is this the task in question?
			if (task.getTaskId().equals(taskId)) {
				// Yes!  Return it.
				return task;
			}
		}
		
		// If we get here, we couldn't find the task in the
		// List<TaskInfo>.  Return null.
		return null;
	}

	/*
	 * Reads a Date from a Map.
	 */
	@SuppressWarnings("unchecked")
	private static Date getDateFromMap(Map m, String key) {
		return ((Date) m.get(key));
	}

	/*
	 * Reads a Long from a Map.
	 */
	@SuppressWarnings("unchecked")
	private static Long getLongFromMap(Map m, String key) {
		Long reply = null;
		String l = getStringFromMap(m, key);
		if (0 < l.length()) {
			reply = Long.parseLong(l);
		}
		return reply;
	}
	
	/*
	 * Reads a List<Long> from a Map.
	 */
	@SuppressWarnings("unchecked")
	private static List<Long> getLongListFromMap(Map m, String key) {
		// Is there value for the key?
		List<Long> reply = new ArrayList<Long>();
		Object o = m.get(key);
		if (null != o) {
			// Yes!  Is the value is a String?
			if (o instanceof String) {
				// Yes!  Added it as a Long to the List<Long>. 
				addLongFromStringToList(((String) o), reply);
			}

			// No, the value isn't a String!  Is it a String[]?
			else if (o instanceof String[]) {
				// Yes!  Scan them and add each as a Long to the
				// List<Long>. 
				String[] strLs = ((String[]) o);
				int c = strLs.length;
				for (int i = 0; i < c; i += 1) {
					addLongFromStringToList(strLs[i], reply);
				}
			}

			// No, the value isn't a String[] either!  Is it a
			// SearchFieldResult?
			else if (o instanceof SearchFieldResult) {
				// Yes!  Scan the value set from it and add each as a
				// Long to the List<Long>. 
				SearchFieldResult sfr = ((SearchFieldResult) m.get(key));
				Set<String> strLs = ((Set<String>) sfr.getValueSet());
				for (String strL:  strLs) {
					addLongFromStringToList(strL, reply);
				}
			}
		}
		
		// If we get here, reply refers to the List<Long> of values
		// from the Map.  Return it.
		return reply;
	}

	/*
	 * Reads a String from a Map.
	 */
	@SuppressWarnings("unchecked")
	private static String getStringFromMap(Map m, String key) {
		String reply = ((String) m.get(key));
		if (null == reply) {
			reply = "";
		}
		return reply;
	}

	/**
	 * Returns the Binder corresponding to an ID, throwing a GwtTeamingException
	 * as necessary.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Binder getTaskBinder(AllModulesInjected bs, Long binderId) throws GwtTeamingException {
		Binder reply = null;
		try {
			reply = bs.getBinderModule().getBinder(binderId);
		}
		
		catch (Exception ex) {
			m_logger.debug("GwtRpcServiceImpl.getTaskBinder( " + String.valueOf(binderId) + ":  EXCEPTION ):  ", ex);
			throw GwtServerHelper.getGwtTeamingException(ex);
		}

		if (null == reply) {
			m_logger.debug("GwtRpcServiceImpl.getTaskBinder( " + String.valueOf(binderId) + ":  Could not access binder. )");
			throw GwtServerHelper.getGwtTeamingException();
		}
		
		return reply; 
	}// end getTasKBinder()
	
	/*
	 * Reads the tasks from the specified binder.
	 */
	@SuppressWarnings("unchecked")
	private static List<TaskInfo> getTasks(HttpServletRequest request, AllModulesInjected bs, Binder binder, String filterType, String modeType) throws GwtTeamingException {
		Map taskEntriesMap;		
		try {
			// Setup to read the task entries...
			HttpSession session = WebHelper.getRequiredSession(request);
			GwtUISessionData optionsObj = ((GwtUISessionData) session.getAttribute(TaskHelper.CACHED_FIND_TASKS_OPTIONS_KEY));
			Map options = ((Map) optionsObj.getData());
			options.put(ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE);
			options.put(ObjectKeys.SEARCH_OFFSET,   0);
	
			// ...and read them.
			taskEntriesMap = TaskHelper.findTaskEntries(
				bs,
				WebHelper.getRequiredSession(request), 
				binder,
				filterType,
				modeType,
				options);
		}
		catch (Exception ex) {
			m_logger.error("GwtTaskHelper.getTasks( EXCEPTION ):  ", ex);
			throw GwtServerHelper.getGwtTeamingException(ex);
		}		
    	List<Map> taskEntriesList = ((List) taskEntriesMap.get(ObjectKeys.SEARCH_ENTRIES));

		// Did we find any entries?
		List<TaskInfo> reply = new ArrayList<TaskInfo>();
		if ((null == taskEntriesList) || taskEntriesList.isEmpty()) {
			// No!  Bail.
			return reply;
		}
		
		// Scan the task entries that we read.
		SeenMap seenMap = bs.getProfileModule().getUserSeenMap(null);
		for (Map taskEntry:  taskEntriesList) {			
			TaskInfo ti = new TaskInfo();
			
			Date endDate = getDateFromMap(taskEntry, "start_end#EndDate");
			ti.setOverdue(         (null == endDate) ? false : DateComparer.isOverdue(endDate));
			ti.setStatus(          getStringFromMap(   taskEntry, "status"                 ));
			ti.setTaskId(          getLongFromMap(     taskEntry, Constants.DOCID_FIELD    ));
			ti.setCompleted(       getStringFromMap(   taskEntry, "completed"              ));
			ti.setSeen(            seenMap.checkIfSeen(taskEntry                           ));
			ti.setBinderId(        getLongFromMap(     taskEntry, Constants.BINDER_ID_FIELD));
			ti.setEntityType(      getStringFromMap(   taskEntry, Constants.ENTITY_FIELD   ));
			ti.setTitle(           getStringFromMap(   taskEntry, Constants.TITLE_FIELD    ));
			ti.setPriority(        getStringFromMap(   taskEntry, "priority"               ));
			ti.setAssignments(     getLongListFromMap( taskEntry, "assignment"             ));
			ti.setAssignmentGroups(getLongListFromMap( taskEntry, "assignment_groups"      ));
			ti.setAssignmentTeams( getLongListFromMap( taskEntry, "assignment_teams"       ));
			
			reply.add(ti);
		}
				
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("GwtTaskHelper.getTasks( Read List<TaskInfo> for binder ):  " + String.valueOf(binder.getId()));
			dumpTaskInfoList(reply);
		}
		
		// If we get here, reply refers to the List<TaskInfo> of the
		// entries contained in binder.  Return it. 
		return reply;
	}

	/**
	 * Reads the task information from the specified binder.
	 * 
	 * @param request
	 * @param bs
	 * @param binder
	 * @param filterType
	 * @param modeType
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static List<TaskListItem> getTaskList( HttpServletRequest request, AllModulesInjected bs, Binder binder, String filterType, String modeType) throws GwtTeamingException {
		// Create a List<TaskListItem> that we can fill up with the
		// task list.
		List<TaskListItem> reply = new ArrayList<TaskListItem>();

		// Read the task linkage and tasks from the binder.
		TaskLinkage    linkage = getTaskLinkage(   bs, binder                      );
		List<TaskInfo> tasks   = getTasks(request, bs, binder, filterType, modeType);

		// Process the order/hierarchy information from the task
		// linkage.
		processTaskLinkList(reply, tasks, linkage.getTaskOrder());		

		// Scan any remaining tasks...
		for (TaskInfo task:  tasks) {
			// ...and add each as a TaskListItem to the task list that
			// ...we're building.
			TaskListItem tli = new TaskListItem();
			tli.setTask(task);
			reply.add(tli);
		}

		// If we get here, reply refers to a List<TaskListItem> for the
		// tasks.  Return it.
		return reply;
	}

	/**
	 * Returns the TaskLinkage from a task folder.
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static TaskLinkage getTaskLinkage(AllModulesInjected bs, Binder binder) throws GwtTeamingException {
		TaskLinkage reply = ((TaskLinkage) binder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE));
		if (null == reply) {
			reply = new TaskLinkage();
		}
		
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("GwtTaskHelper.getTaskLinkage( Read TaskLinkage for binder ):  " + String.valueOf(binder.getId()));
			dumpTaskLinkage(reply);
		}
		
		return reply;
	}
	
	/*
	 * Returns true if a TaskLink refers to a valid FolderEntry and false
	 * otherwise.
	 */
	private static boolean isTaskLinkValid(AllModulesInjected bs, TaskLink tl) {
		// If we weren't given a TaskLink...
		boolean reply = false;
		if (null == tl) {
			// ..it can't be valid.
			return reply;
		}
		
		try {
			// If we can access the TaskLink's FolderEntry and it's not
			// deleted or predeleted, it's valid.
			FolderEntry taskFE = bs.getFolderModule().getEntry(null, tl.getTaskId());
			reply = ((null != taskFE) && (!(taskFE.isDeleted())) && (!(taskFE.isPreDeleted())));
		}
		
		catch (Exception ex) {
			// If we can't access the TaskLink's FolderEntry, it's not
			// valid.
			m_logger.debug("GwtTaskHelper.isTaskLinkValid( EXCEPTION ):  ", ex);
			reply = false;
		}
		
		// If we get here, reply is true if the TaskLink references a
		// task we can access and false otherwise.  Return it.
		return reply;
	}
	
	/*
	 * Processes the TaskLink's in a List<TaskLink> into a
	 * List<TaskListItem>, maintaining order and hierarchy.
	 */
	private static void processTaskLinkList(List<TaskListItem> taskList, List<TaskInfo> tasks, List<TaskLink> links) {
		// Scan the List<TaskLink>.
		for (TaskLink link:  links) {
			// Can we find the TaskInfo for this TaskLink?
			TaskInfo task = findTaskInList(link.getTaskId(), tasks);
			if (null == task) {
				// No!  Skip it.
				continue;
			}

			// Remove the TaskInfo from the List<TaskInfo>...
			tasks.remove(task);

			// ...wrap it in a TaskListItem and add it to the
			// ...List<TaskListItem>...
			TaskListItem tli = new TaskListItem();
			tli.setTask(task);			
			taskList.add(tli);
			
			// ...and recursively process any subtasks.
			processTaskLinkList(tli.getSubtasks(), tasks, link.getSubtasks());
		}
	}
	
	/**
	 * Removes the TaskLinkage from a task folder.
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean removeTaskLinkage(AllModulesInjected bs, Binder binder) throws GwtTeamingException {
		return saveTaskLinkage(bs, binder, null);
	}

	/**
	 * Stores the TaskLinkage for a task folder.
	 * 
	 * @param bs
	 * @param binder
	 * @param tl
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveTaskLinkage(AllModulesInjected bs, Binder binder, TaskLinkage tl) throws GwtTeamingException {
		try {
			bs.getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE, tl);
			if (m_logger.isDebugEnabled()) {
				if (null == tl) {
					m_logger.debug("GwtTaskHelper.setTaskLinkage( Removed TaskLinkage for binder ):  " + String.valueOf(binder.getId()));
				}
				else {
					m_logger.debug("GwtTaskHelper.setTaskLinkage( Stored TaskLinkage for binder ):  " + String.valueOf(binder.getId()));
					dumpTaskLinkage(tl);
				}
			}
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}
	
	/**
	 * Validates the task FolderEntry's referenced by a TaskLinkage.
	 * 
	 * @param bs
	 * @param tl
	 */
	public static void validateTaskLinkage(AllModulesInjected bs, TaskLinkage tl) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("GwtTaskHelper.validateTaskLinkage( BEFORE ):");
			dumpTaskLinkage(tl);
		}
		
		// Simply validate the List<TaskLink> of the task ordering.
		validateTaskLinkList(bs, tl.getTaskOrder());
		
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("GwtTaskHelper.validateTaskLinkage( AFTER ):");
			dumpTaskLinkage(tl);
		}
	}

	/*
	 * Validates the TaskLink's in a List<TaskLink> removing any that
	 * are invalid or cannot be accessed.
	 */
	private static void validateTaskLinkList(AllModulesInjected bs, List<TaskLink> tlList) {
		// Scan the TaskLink's in the List<TaskLink>.
		int c = ((null == tlList) ? 0 : tlList.size());
		for (int i = (c - 1); i >= 0; i -= 1) {
			// Is this TaskLink valid?
			TaskLink tl = tlList.get(i);
			if (!(isTaskLinkValid(bs, tl))) {
				// No!  Remove it.
				tlList.remove(i);
			}
			
			else {			
				// Yes, this TaskLink is valid!  Validate its subtasks.
				validateTaskLinkList(bs, tl.getSubtasks());
			}
		}
	}	
}
