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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskLinkage.TaskLink;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskDuration;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DateComparer;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.MiscUtil;
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
	 * Converts a String to a Long, if possible, and adds it as the ID
	 * of an AssignmentInfo to a List<AssignmentInfo>.
	 */
	private static void addAIFromStringToList(String s, List<AssignmentInfo> l) {
		try {
			Long lVal = Long.parseLong(s);
			l.add(AssignmentInfo.construct(lVal));
		}
		catch (NumberFormatException nfe) {
		}
	}

	/*
	 * Adds a Long to a List<Long> if it's not already there.
	 */
	private static void addLToLLIfUnique(List<Long> lList, Long l) {
		// If the List<Long> doesn't contain the Long...
		if (!(lList.contains(l))) {
			// ...add it.
			lList.add(l);
		}
	}
	
	/*
	 * Generates a search index field name reference for an event's
	 * duration field.
	 */
	private static String buildDurationFieldName(String fieldName) {
		return (buildEventFieldName(Constants.EVENT_FIELD_DURATION) + BasicIndexUtils.DELIMITER + fieldName);
	}
	
	/*
	 * Generates a String to write to the log for a boolean.
	 */
	private static String buildDumpString(String label, boolean v) {
		return (label + ": " + String.valueOf(v));
	}

	/*
	 * Generates a String to write to the log for a Date.
	 */
	private static String buildDumpString(String label, Date date) {
		String dateS;
		
		if (null == date) {
			dateS = "null";
		}
		
		else {
			User user = GwtServerHelper.getCurrentUser();			
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale());
			df.setTimeZone(user.getTimeZone());			
			dateS = df.format(date);
		}
		return (label + ": " + dateS);
	}
	
	/*
	 * Generates a String to write to the log for an integer.
	 */
	private static String buildDumpString(String label, int v) {
		return (label + ": " + String.valueOf(v));
	}
	
	/*
	 * Generates a String to write to the log for a Long.
	 */
	private static String buildDumpString(String label, Long v) {
		return (label + ": " + ((null == v) ? "null" : String.valueOf(v)));
	}
	
	/*
	 * Generates a String to write to the log for a List<AssignmentInfo>.
	 */
	private static String buildDumpString(String label, List<AssignmentInfo> v, boolean teamOrGroup) {
		if (null == v) {
			v = new ArrayList<AssignmentInfo>();
		}
		
		StringBuffer buf = new StringBuffer(label);
		buf.append(": ");
		if (v.isEmpty()) {
			buf.append("EMPTY");
		}
		else {
			int c = 0;
			for (AssignmentInfo ai: v) {
				if (0 < c++) {
					buf.append(", ");
				}
				buf.append(String.valueOf(ai.getId()));
				buf.append("(" + ai.getTitle());
				if (teamOrGroup)
				     buf.append(":" + String.valueOf(ai.getMembers()));
				else buf.append(":" + ai.getPresence().getStatusText());
				buf.append(":" + ai.getPresenceDude() + ")");
			}
		}
		return buf.toString();
	}
	
	/*
	 * Generates a String to write to the log for a String.
	 */
	private static String buildDumpString(String label, String v) {
		return (label + ": '" + ((null == v) ? "null" : v) + "'");
	}

	/*
	 * Generates a String to write to the log for a TaskEvent.
	 */
	private static String buildDumpString(String label, TaskEvent event) {
		StringBuffer buf = new StringBuffer(label);
		
		buf.append(buildDumpString("Start", event.getLogicalStart()));
		buf.append(buildDumpString(", End", event.getLogicalEnd()));
		buf.append(buildDumpString(", All Day", event.getAllDayEvent()));
		
		TaskDuration taskDuration = event.getDuration();
		buf.append(buildDumpString(", Duration:S", taskDuration.getSeconds()));
		buf.append(buildDumpString(", M",          taskDuration.getMinutes()));
		buf.append(buildDumpString(", H",          taskDuration.getHours()));
		buf.append(buildDumpString(", D",          taskDuration.getDays()));
		buf.append(buildDumpString(", W",          taskDuration.getWeeks()));
		buf.append(buildDumpString(", Interval",   taskDuration.getInterval()));
		
		return buf.toString();
	}
	
	/*
	 * Generates a search index field name reference for an event
	 * field.
	 */
	private static String buildEventFieldName(String fieldName) {
		return (Constants.EVENT_FIELD_START_END + BasicIndexUtils.DELIMITER + fieldName);
	}
	
	/*
	 * Returns true if the current user can modify a task and false
	 * otherwise.
	 */
	private static boolean canModifyTask(AllModulesInjected bs, Long binderId, Long taskId) {
		boolean reply = false;
		try {
			FolderModule fm = bs.getFolderModule();
			FolderEntry fe = fm.getEntry(binderId, taskId);
			reply = fm.testAccess(fe, FolderOperation.modifyEntry);
		}
		catch (Exception ex) {
			// Ignore.
		}
		return reply;
	}
	
	/*
	 * When initially build, the AssignmentInfo's in the List<TaskInfo>
	 * only contain the assignee IDs.  We need to complete them with
	 * each assignee's title.
	 */
	@SuppressWarnings("unchecked")
	private static void completeAIs(AllModulesInjected bs, List<TaskInfo> tasks) {
		// If we don't have any TaskInfo's to complete...
		if ((null == tasks) || tasks.isEmpty()) {
			// ..bail.
			return;
		}

		// Allocate List<Long>'s to track the assignees that need to be
		// completed.
		List<Long> principalIds = new ArrayList<Long>();
		List<Long> teamIds      = new ArrayList<Long>();

		// Scan the List<TaskInfo>.
		for (TaskInfo ti:  tasks) {
			// Scan this TaskInfo's individual assignees...
			for (AssignmentInfo ai:  ti.getAssignments()) {
				// ...tracking each unique ID.
				addLToLLIfUnique(principalIds, ai.getId());
			}
			
			// Scan this TaskInfo's group assignees...
			for (AssignmentInfo ai:  ti.getAssignmentGroups()) {
				// ...tracking each unique ID.
				addLToLLIfUnique(principalIds, ai.getId());
			}
			
			// Scan this TaskInfo's team assignees...
			for (AssignmentInfo ai:  ti.getAssignmentTeams()) {
				// ...tracking each unique ID.
				addLToLLIfUnique(teamIds, ai.getId());
			}
		}

		// If we don't have any assignees to complete...
		boolean hasPrincipals = (!(principalIds.isEmpty()));
		boolean hasTeams      = (!(teamIds.isEmpty()));		
		if ((!hasPrincipals) && (!hasTeams)) {
			// ...bail.
			return;
		}

		// Construct Maps, mapping the principal IDs to their titles
		// and membership counts.
		Map<Long, String>          principalTitles   = new HashMap<Long, String>();
		Map<Long, Integer>         groupCounts       = new HashMap<Long, Integer>();
		Map<Long, GwtPresenceInfo> userPresence      = new HashMap<Long, GwtPresenceInfo>();
		boolean                    isPresenceEnabled = GwtServerHelper.isPresenceEnabled();
		if (hasPrincipals) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(principalIds);}
			catch (Exception ex) {}
			if ((null != principals) && (!(principals.isEmpty()))) {
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					Long pId = p.getId();
					principalTitles.put(pId, p.getTitle());					
					if (p instanceof GroupPrincipal) {
						groupCounts.put(pId, getGroupCount((GroupPrincipal) p));						
					}
					else if (isPresenceEnabled && (p instanceof UserPrincipal)) {
						userPresence.put(pId, GwtServerHelper.getPresenceInfo((User) p));
					}
				}
			}
		}
		
		// Construct Maps, mapping the team IDs to their titles and
		// membership counts.
		Map<Long, String>  teamTitles = new HashMap<Long, String>();
		Map<Long, Integer> teamCounts = new HashMap<Long, Integer>();
		if (hasTeams) {
			SortedSet<Binder> binders = null;
			try {binders = bs.getBinderModule().getBinders(teamIds);}
			catch (Exception ex) {}
			if ((null != binders) && (!(binders.isEmpty()))) {
				for (Binder b:  binders) {
					Long bId = b.getId();
					teamTitles.put(bId, b.getTitle());
					teamCounts.put(bId, getTeamCount(bs, b));
				}
			}
		}
		
		// Scan the List<TaskInfo> again.
		for (TaskInfo ti:  tasks) {
			// Scan this TaskInfo's individual assignees again...
			for (AssignmentInfo ai:  ti.getAssignments()) {
				// ...setting each one's title.
				setAITitle(   ai, principalTitles);
				setAIPresence(ai, userPresence   );
			}
			
			// Scan this TaskInfo's group assignees again...
			for (AssignmentInfo ai:  ti.getAssignmentGroups()) {
				// ...setting each one's title and membership count.
				setAITitle(  ai, principalTitles);
				setAIMembers(ai, groupCounts    );
				ai.setPresenceDude("pics/group_icon_small.gif");
			}
			
			// Scan this TaskInfo's team assignees again...
			for (AssignmentInfo ai:  ti.getAssignmentTeams()) {
				// ...setting each one's title and membership count.
				setAITitle(  ai, teamTitles);
				setAIMembers(ai, teamCounts);
				ai.setPresenceDude("trees/people.gif");
			}
		}
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
		m_logger.debug("GwtTaskHelper.dumpTaskInfoList( START: " + String.valueOf(tasks.size()) + " )");
		for (TaskInfo ti:  tasks) {
			StringBuffer buf = new StringBuffer(buildDumpString("\n\tTask ID", ti.getTaskId()));
			buf.append(buildDumpString("\n\t\tCanModify",        ti.getCanModify()              ));
			buf.append(buildDumpString("\n\t\tSeen",             ti.getSeen()                   ));
			buf.append(buildDumpString("\n\t\tAssignments",      ti.getAssignments(),      false));
			buf.append(buildDumpString("\n\t\tAssignmentGroups", ti.getAssignmentGroups(), true ));
			buf.append(buildDumpString("\n\t\tAssignmentTeams",  ti.getAssignmentTeams(),  true ));
			buf.append(buildDumpString("\n\t\tBinder ID",        ti.getBinderId()               ));
			buf.append(buildDumpString("\n\t\tCompleted",        ti.getCompleted()              ));
			buf.append(buildDumpString("\n\t\tEntityType",       ti.getEntityType()             ));
			buf.append(buildDumpString("\n\t\tPriority",         ti.getPriority()               ));
			buf.append(buildDumpString("\n\t\tTitle",            ti.getTitle()                  ));
			buf.append(buildDumpString("\n\t\tStatus",           ti.getStatus()                 ));
			buf.append(buildDumpString("\n\t\tOverdue",          ti.getOverdue()                ));
			buf.append(buildDumpString("\n\t\tEvent: ",          ti.getEvent()                  ));
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
		m_logger.debug("GwtTaskHelper.dumpTaskLinkage( START: " + String.valueOf(taskOrder.size()) + " )");
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
	 * Reads a List<AssignmentInfo> from a Map.
	 */
	@SuppressWarnings("unchecked")
	private static List<AssignmentInfo> getAIListFromMap(Map m, String key) {
		// Is there value for the key?
		List<AssignmentInfo> reply = new ArrayList<AssignmentInfo>();
		Object o = m.get(key);
		if (null != o) {
			// Yes!  Is the value is a String?
			if (o instanceof String) {
				// Yes!  Added it as a Long to the List<Long>. 
				addAIFromStringToList(((String) o), reply);
			}

			// No, the value isn't a String!  Is it a String[]?
			else if (o instanceof String[]) {
				// Yes!  Scan them and add each as a Long to the
				// List<Long>. 
				String[] strLs = ((String[]) o);
				int c = strLs.length;
				for (int i = 0; i < c; i += 1) {
					addAIFromStringToList(strLs[i], reply);
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
					addAIFromStringToList(strL, reply);
				}
			}
		}
		
		// If we get here, reply refers to the List<Long> of values
		// from the Map.  Return it.
		return reply;
	}

	/*
	 * Reads a Date from a Map.
	 */
	@SuppressWarnings("unchecked")
	private static Date getDateFromMap(Map m, String key) {
		Date reply;
		
		Object data = m.get(key);
		if (data instanceof Date) {
			reply = ((Date) data);
		}
		else if (data instanceof String) {
			try {
				reply = DateTools.stringToDate((String) data);
			}
			catch (ParseException pe) {
				reply = null;
			}
		}
		else {
			reply = null;;
		}
		return reply;
	}

	/*
	 * Returns the String representation for the given date, formatted
	 * based on the current user's locale and time zone.
	 */
	private static String getDateTimeString(Date date) {
		String reply;
		if (null == date) {
			reply = "";
		}
		else {
			User user = GwtServerHelper.getCurrentUser();
			
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale());
			df.setTimeZone(user.getTimeZone());
			
			reply = df.format(date);
		}
		return reply;
	}
	
	/*
	 * Reads an event from a map.
	 */
	@SuppressWarnings("unchecked")
	private static TaskEvent getEventFromMap(Map m) {
		TaskEvent reply = new TaskEvent();
		
		// Extract the event's end...
		Date logicalEnd = getDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_LOGICAL_END_DATE));
		reply.setLogicalEnd(                         logicalEnd);
		reply.setLogicalEndDisplay(getDateTimeString(logicalEnd));
		
		// ...and start dates from the Map...
		Date logicalStart = getDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_LOGICAL_START_DATE));
		reply.setLogicalStart(                         logicalStart);
		reply.setLogicalStartDisplay(getDateTimeString(logicalStart));

		// ...extract the event's 'All Day Event' flag from the Map...
		String tz = getStringFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_TIME_ZONE_ID));
		reply.setAllDayEvent(!(MiscUtil.hasString(tz)));

		// ...and extract the event's duration fields from the Map.
		TaskDuration taskDuration = new TaskDuration();
		taskDuration.setSeconds(getIntFromMap(m, buildDurationFieldName(Constants.DURATION_FIELD_SECONDS)));
		taskDuration.setMinutes(getIntFromMap(m, buildDurationFieldName(Constants.DURATION_FIELD_MINUTES)));
		taskDuration.setHours(  getIntFromMap(m, buildDurationFieldName(Constants.DURATION_FIELD_HOURS  )));
		taskDuration.setDays(   getIntFromMap(m, buildDurationFieldName(Constants.DURATION_FIELD_DAYS   )));
		taskDuration.setWeeks(  getIntFromMap(m, buildDurationFieldName(Constants.DURATION_FIELD_WEEKS  )));
		reply.setDuration(taskDuration);

		// If we get here, reply refers to the TaskEvent object
		// constructed from the information in the Map.  Return it.
		return reply;
	}

	/*
	 * Returns a count of the members of a group.
	 */
	private static int getGroupCount(GroupPrincipal group) {
		List<Long> groupIds = new ArrayList<Long>();
		groupIds.add(group.getId());
		Set<Long> groupMemberIds = null;
		try {
			ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
			groupMemberIds = profileDao.explodeGroups(groupIds, group.getZoneId());
		}
		catch (Exception ex) {}
		return ((null == groupMemberIds) ? 0 : groupMemberIds.size());
	}
	
	/*
	 * Reads an integer from a Map.
	 */
	@SuppressWarnings("unchecked")
	private static int getIntFromMap(Map m, String key) {
		int reply = 0;
		String i = getStringFromMap(m, key);
		if (0 < i.length()) {
			reply = Integer.parseInt(i);
		}
		return reply;
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
	 * Reads a date from a Map and determines if it's overdue.
	 */
	@SuppressWarnings("unchecked")
	private static boolean getOverdueFromMap(Map m, String key) {
		Date endDate = getDateFromMap(m, key);
		return ((null == endDate) ? false : DateComparer.isOverdue(endDate));
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
			m_logger.debug("GwtRpcServiceImpl.getTaskBinder( " + String.valueOf(binderId) + ": EXCEPTION ): ", ex);
			throw GwtServerHelper.getGwtTeamingException(ex);
		}

		if (null == reply) {
			m_logger.debug("GwtRpcServiceImpl.getTaskBinder( " + String.valueOf(binderId) + ": Could not access binder. )");
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
			m_logger.error("GwtTaskHelper.getTasks( EXCEPTION ): ", ex);
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
		Long binderId = binder.getId();
		SeenMap seenMap = bs.getProfileModule().getUserSeenMap(null);
		for (Map taskEntry:  taskEntriesList) {			
			TaskInfo ti = new TaskInfo();
			
			ti.setOverdue(         getOverdueFromMap(  taskEntry, buildEventFieldName(Constants.EVENT_FIELD_LOGICAL_END_DATE)));
			ti.setEvent(           getEventFromMap(    taskEntry                                                             ));
			ti.setStatus(          getStringFromMap(   taskEntry, "status"                                                   ));
			ti.setTaskId(          getLongFromMap(     taskEntry, Constants.DOCID_FIELD                                      ));
			ti.setCompleted(       getStringFromMap(   taskEntry, "completed"                                                ));
			ti.setSeen(            seenMap.checkIfSeen(taskEntry                                                             ));
			ti.setBinderId(        getLongFromMap(     taskEntry, Constants.BINDER_ID_FIELD                                  ));
			ti.setEntityType(      getStringFromMap(   taskEntry, Constants.ENTITY_FIELD                                     ));
			ti.setTitle(           getStringFromMap(   taskEntry, Constants.TITLE_FIELD                                      ));
			ti.setPriority(        getStringFromMap(   taskEntry, "priority"                                                 ));
			ti.setAssignments(     getAIListFromMap(   taskEntry, "assignment"                                               ));
			ti.setAssignmentGroups(getAIListFromMap(   taskEntry, "assignment_groups"                                        ));
			ti.setAssignmentTeams( getAIListFromMap(   taskEntry, "assignment_teams"                                         ));
			ti.setCanModify(       canModifyTask(bs, binderId, ti.getTaskId()));
			
			Date completedDate = getDateFromMap( taskEntry, Constants.TASK_COMPLETED_DATE_FIELD);
			ti.setCompletedDate(                         completedDate);
			ti.setCompletedDateDisplay(getDateTimeString(completedDate));
			
			reply.add(ti);
		}

		// At this point, the AssignmentInfo's in the List<TaskInfo>
		// that we're going to return only contain the assignee IDs.
		// We need to complete them with the title of each assignee. 
		completeAIs(bs, reply);
				
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("GwtTaskHelper.getTasks( Read List<TaskInfo> for binder ): " + String.valueOf(binderId));
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
		return
			getTaskListImpl(
				request,
				bs,
				binder,
				getTaskLinkage(bs, binder),
				filterType,
				modeType);
	}

	/*
	 * Reads the task information from the specified binder.
	 */
	private static List<TaskListItem> getTaskListImpl( HttpServletRequest request, AllModulesInjected bs, Binder binder, TaskLinkage taskLinkage, String filterType, String modeType) throws GwtTeamingException {
		// Create a List<TaskListItem> that we'll fill up with the task
		// list.
		List<TaskListItem> reply = new ArrayList<TaskListItem>();

		// Read the tasks from the binder.
		List<TaskInfo> tasks = getTasks(request, bs, binder, filterType, modeType);

		// Process the order information from the supplied task
		// linkage.
		List<TaskLink> taskOrder = taskLinkage.getTaskOrder();
		processTaskLinkList(reply, tasks, taskOrder);		

		// Scan any tasks that weren't addressed by the task linkage...
		boolean changedLinkage = (!(tasks.isEmpty()));
		for (TaskInfo task:  tasks) {
			// ...add each as a TaskListItem to the task list being
			// ...built...
			TaskListItem tli = new TaskListItem();
			tli.setTask(task);
			reply.add(tli);
			
			// ...and add it to the linkage since it will now be
			// ...considered 'linked into' the task folder's order
			// ...and hierarchy.
			TaskLink tl = new TaskLink();
			tl.setTaskId(task.getTaskId());
			taskOrder.add(tl);
		}

		// If we changed the task linkage in building the task list and
		// the user has rights to modify it on this folder...
		if (changedLinkage && TaskHelper.canModifyTaskLinkage(request, bs, binder)) {
			// ...we need to save the task linkage changes.
			saveTaskLinkage(bs, binder, taskLinkage);
		}

		// If we get here, reply refers to the List<TaskListItem> for
		// the tasks.  Return it.
		return reply;
	}

	/**
	 * Returns the TaskBundle from a task folder.
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
	public static TaskBundle getTaskBundle(HttpServletRequest request, AllModulesInjected bs, Binder binder, String filterType, String modeType) throws GwtTeamingException {
		// Read the task linkage and tasks...
		TaskLinkage        taskLinkage = getTaskLinkage(          bs, binder                                   );
		List<TaskListItem> tasks       = getTaskListImpl(request, bs, binder, taskLinkage, filterType, modeType);

		// ...and use that to create a TaskBundle. 
		TaskBundle reply = new TaskBundle();
		reply.setTaskLinkage(taskLinkage);
		reply.setTasks(      tasks      );
		
		// Set the Binder based rights...
		reply.setCanModifyTaskLinkage(TaskHelper.canModifyTaskLinkage( request, bs, binder ));

		// ...and the Folder base rights on the TaskBundle.
		FolderModule fm = bs.getFolderModule();
		reply.setCanModifyEntry(fm.testAccess(((Folder) binder), FolderOperation.modifyEntry   ));
		reply.setCanPurgeEntry( fm.testAccess(((Folder) binder), FolderOperation.deleteEntry   ));
		reply.setCanTrashEntry( fm.testAccess(((Folder) binder), FolderOperation.preDeleteEntry));

		// If we get here, reply refers to the request TaskBundle.
		// Return it.
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
	@SuppressWarnings("unchecked")
	public static TaskLinkage getTaskLinkage(AllModulesInjected bs, Binder binder) throws GwtTeamingException {
		Map serializationMap = ((Map) binder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE));
		TaskLinkage reply = TaskLinkage.loadSerializationMap(serializationMap);
		
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("GwtTaskHelper.getTaskLinkage( Read TaskLinkage for binder ): " + String.valueOf(binder.getId()));
			dumpTaskLinkage(reply);
		}
		
		return reply;
	}
	
	/*
	 * Returns a count of the members of a team.
	 */
	@SuppressWarnings("unchecked")
	private static int getTeamCount(AllModulesInjected bs, Binder binder) {
		Set teamMembers = null;
		try {teamMembers = bs.getBinderModule().getTeamMembers(binder, false);}
		catch (Exception ex) {}
		return ((null == teamMembers) ? 0 : teamMembers.size());
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
			m_logger.debug("GwtTaskHelper.isTaskLinkValid( EXCEPTION ): ", ex);
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
			bs.getBinderModule().setProperty(
				binder.getId(),
				ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE,
				((null == tl) ?
					null      :
					tl.getSerializationMap()));
			
			if (m_logger.isDebugEnabled()) {
				if (null == tl) {
					m_logger.debug("GwtTaskHelper.setTaskLinkage( Removed TaskLinkage for binder ): " + String.valueOf(binder.getId()));
				}
				else {
					m_logger.debug("GwtTaskHelper.setTaskLinkage( Stored TaskLinkage for binder ): " + String.valueOf(binder.getId()));
					dumpTaskLinkage(tl);
				}
			}
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}

	/*
	 * Stores the membership count of an AssignmentInfo based on Map
	 * lookup using its ID.
	 */
	private static void setAIMembers(AssignmentInfo ai, Map<Long, Integer> countMap) {
		Integer count = countMap.get(ai.getId());
		ai.setMembers((null == count) ? 0 : count.intValue());
	}

	/*
	 * Stores the title of an AssignmentInfo based on Map lookup using
	 * its ID.
	 */
	private static void setAITitle(AssignmentInfo ai, Map<Long, String> titleMap) {
		String title = titleMap.get(ai.getId());
		if (MiscUtil.hasString(title)) {
			ai.setTitle(title);
		}
	}

	/*
	 * Stores a GwtPresenceInfo of an AssignmentInfo based on Map
	 * lookup using its ID.
	 */
	private static void setAIPresence(AssignmentInfo ai, Map<Long, GwtPresenceInfo> presenceMap) {
		GwtPresenceInfo pi = presenceMap.get(ai.getId());
		if (null == pi) pi = GwtServerHelper.getPresenceInfoDefault();
		ai.setPresence(pi);
		ai.setPresenceDude(GwtServerHelper.getPresenceDude(pi));
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
