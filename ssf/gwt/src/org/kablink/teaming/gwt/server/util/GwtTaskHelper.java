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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
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
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskId;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskLinkage.TaskLink;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskDuration;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TaskListItemHelper;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.task.TaskHelper.FilterType;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DateComparer;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.util.cal.Duration;
import org.kablink.util.search.Constants;

/**
 * Helper methods for the GWT UI server code that services task
 * requests.
 *
 * @author drfoster@novell.com
 */
public class GwtTaskHelper {
	protected static Log m_logger = LogFactory.getLog(GwtTaskHelper.class);

	/**
	 * Inner class used to compare two AssignmentInfo's.
	 */
	private static class AssignmentInfoComparator implements Comparator<AssignmentInfo> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public AssignmentInfoComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two AssignmentInfo's by their assignee's name.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param ai1
		 * @param ai2
		 * 
		 * @return
		 */
		@Override
		public int compare(AssignmentInfo ai1, AssignmentInfo ai2) {
			String assignee1 = ai1.getTitle();
			String assignee2 = ai2.getTitle();

			int reply;
			if (m_ascending)
			     reply = MiscUtil.safeSColatedCompare(assignee1, assignee2);
			else reply = MiscUtil.safeSColatedCompare(assignee2, assignee1);
			return reply;
		}
	}

	/*
	 * Inner class used to track event dates that are required for
	 * updating calculated dates on the server.
	 */
	private static class ServerDates {
		private TaskDate	m_actualStart;	//
		private TaskDate	m_actualEnd;	//
		private TaskDate	m_calcStart;	//
		private TaskDate	m_calcEnd;		//

		/**
		 * Constructor method.
		 */
		public ServerDates() {
			// Nothing to do.
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public TaskDate getActualStart() {return m_actualStart; }
		public TaskDate getActualEnd()   {return m_actualEnd;   }
		public TaskDate getCalcStart()   {return m_calcStart;   }
		public TaskDate getCalcEnd()     {return m_calcEnd;     }
		
		public boolean  hasActualStart() {return (null != m_actualStart);}
		public boolean  hasActualEnd()   {return (null != m_actualEnd  );}
		public boolean  hasCalcStart()   {return (null != m_calcStart  );}
		public boolean  hasCalcEnd()     {return (null != m_calcEnd    );}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setActualStart(TaskDate actualStart) {m_actualStart = actualStart;}
		public void setActualEnd(  TaskDate actualEnd)   {m_actualEnd   = actualEnd;  }
		public void setCalcStart(  TaskDate calcStart)   {m_calcStart   = calcStart;  }
		public void setCalcEnd(    TaskDate calcEnd)     {m_calcEnd     = calcEnd;    }
	}
	
	/*
	 * Converts a String to a Long, if possible, and adds it as the ID
	 * of an AssignmentInfo to a List<AssignmentInfo>.
	 */
	private static void addAIFromStringToList(String s, List<AssignmentInfo> l) {
		try {
			Long lVal = Long.parseLong(s);
			l.add(AssignmentInfo.construct(lVal));
		}
		catch (NumberFormatException nfe) {/* Ignored. */}
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
	 * Generates a base TaskBundle object with the rights, binder
	 * information, task linkage, ... initialized.
	 * 
	 * The only thing missing are the tasks which are filled in by a
	 * follow up call to readTaskList().
	 */
	private static TaskBundle buildBaseTaskBundle(HttpServletRequest request, AllModulesInjected bs, Binder binder, String filterTypeParam, String modeTypeParam) throws GwtTeamingException {
		// Allocate a new TaskBundle. 
		TaskBundle reply = new TaskBundle();
		reply.setIsDebug(TaskHelper.TASK_DEBUG_ENABLED);

		// Store information about the Binder.
		reply.setBinderId(        binder.getId()     );
		reply.setBinderIsMirrored(binder.isMirrored());

		// Store the task linkage for the Binder.
		reply.setTaskLinkage(getTaskLinkage(bs, binder));
		
		// Store the user's rights to the Binder.
		reply.setCanModifyTaskLinkage(TaskHelper.canModifyTaskLinkage(request, bs, binder));
		setTaskBundleRights(request, bs, ((Folder) binder), reply);
		
		// Store whether the task list is being filtered.
		reply.setFilterTypeParam(filterTypeParam);
		FilterType filterType = ((null == filterTypeParam) ? FilterType.ALL : FilterType.valueOf(filterTypeParam));
		boolean isFiltered = (FilterType.ALL != filterType);
		if (!isFiltered) {
			User user = GwtServerHelper.getCurrentUser();
			UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binder.getId());
			isFiltered = (null != BinderHelper.getSearchFilter(bs, binder, userFolderProperties));
		}
		reply.setIsFiltered(isFiltered);

		// Store whether the tasks being shown are from the folder (vs.
		// those assigned to the current user.)
		reply.setModeTypeParam(  modeTypeParam  );
		ModeType modeType = ((null == modeTypeParam) ? ModeType.PHYSICAL : ModeType.valueOf(modeTypeParam));
		reply.setIsFromFolder(ModeType.PHYSICAL == modeType);

		// If we get here, reply refers to the base TaskBundle.  Return
		// it.
		return reply;
	}
	
	/*
	 * Generates a String to write to the log for a boolean.
	 */
	private static String buildDumpString(String label, boolean v) {
		return (label + ": " + String.valueOf(v));
	}

	/*
	 * Generates a String to write to the log for a TaskDate.
	 */
	private static String buildDumpString(String label, TaskDate date) {
		String dateS;
		if (null == date) {
			dateS = "null";
		}
		
		else {
			dateS = date.getDateDisplay();
			if (!(MiscUtil.hasString(dateS))) {
				User user = GwtServerHelper.getCurrentUser();			
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale());
				df.setTimeZone(user.getTimeZone());			
				dateS = df.format(date.getDate());
			}
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
		
		buf.append(buildDumpString("Actual Start",    event.getActualStart()));
		buf.append(buildDumpString(", Actual End",    event.getActualEnd()));
		buf.append(buildDumpString(", Logical Start", event.getLogicalStart()));
		buf.append(buildDumpString(", Logical End",   event.getLogicalEnd()));
		buf.append(buildDumpString(", All Day",       event.getAllDayEvent()));
		
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
	 * Generates a search index field name reference for an event's
	 * duration field.
	 */
	private static String buildDurationFieldName(String fieldName) {
		return (buildEventFieldName(Constants.EVENT_FIELD_DURATION) + BasicIndexUtils.DELIMITER + fieldName);
	}
	
	/*
	 * Generates a search index field name reference for an event
	 * field.
	 */
	private static String buildEventFieldName(String fieldName) {
		return (Constants.EVENT_FIELD_START_END + BasicIndexUtils.DELIMITER + fieldName);
	}

	/*
	 * Clears the flags from a binder telling us we've got a change
	 * pending.
	 */
	private static void clearTaskChanged(AllModulesInjected bs, Long userId, Long binderId) {
		bs.getProfileModule().setUserProperty(userId, binderId, ObjectKeys.BINDER_PROPERTY_TASK_CHANGE, "");
		bs.getProfileModule().setUserProperty(userId, binderId, ObjectKeys.BINDER_PROPERTY_TASK_ID,     "");
	}
	
	/**
	 * Marks the given task in the given binder as having its subtask
	 * display collapsed.
	 * 
	 * @param bs
	 * @param binderId
	 * @param taskId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean collapseSubtasks(AllModulesInjected bs, Long binderId, Long entryId) throws GwtTeamingException {
		try {
			List<Long> collapsedSubtasks = getCollapsedSubtasks(bs, binderId);
			if (!(collapsedSubtasks.contains(entryId))) {
				collapsedSubtasks.add(entryId);
		   		bs.getProfileModule().setUserProperty(null, binderId, ObjectKeys.USER_PROPERTY_COLLAPSE_SUBTASKS, collapsedSubtasks);				
			}
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
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
		Map<Long, Long>            presenceUserWSIds = new HashMap<Long, Long>();
		boolean                    isPresenceEnabled = GwtServerHelper.isPresenceEnabled();
		if (hasPrincipals) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(principalIds);}
			catch (Exception ex) {/* Ignored. */}
			if ((null != principals) && (!(principals.isEmpty()))) {
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					Long pId = p.getId();
					boolean isUser = (p instanceof UserPrincipal);
					principalTitles.put(pId, p.getTitle());					
					if (p instanceof GroupPrincipal) {
						groupCounts.put(pId, getGroupCount((GroupPrincipal) p));						
					}
					else if (isUser) {
						User user = ((User) p);
						presenceUserWSIds.put(pId, user.getWorkspaceId());
						if (isPresenceEnabled) {
							userPresence.put(pId, GwtServerHelper.getPresenceInfo(user));
						}
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
			catch (Exception ex) {/* Ignored. */}
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
			// The removeList is used to handle cases where an ID could
			// not be resolved (e.g., an 'Assigned To' user has been
			// deleted.)
			List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
			
			// Scan this TaskInfo's individual assignees again...
			for (AssignmentInfo ai:  ti.getAssignments()) {
				// ...setting each one's title.
				if (setAITitle(           ai, principalTitles )) {
					setAIPresence(        ai, userPresence     );
					setAIPresenceUserWSId(ai, presenceUserWSIds);
				}
				else {
					removeList.add(ai);
				}
			}
			removeUnresolvedAssignees(ti.getAssignments(), removeList);
			
			// Scan this TaskInfo's group assignees again...
			for (AssignmentInfo ai:  ti.getAssignmentGroups()) {
				// ...setting each one's title and membership count.
				if (setAITitle(  ai, principalTitles)) {
					setAIMembers(ai, groupCounts     );
					ai.setPresenceDude("pics/group_icon_small.png");
				}
				else {
					removeList.add(ai);
				}
			}
			removeUnresolvedAssignees(ti.getAssignmentGroups(), removeList);
			
			// Scan this TaskInfo's team assignees again...
			for (AssignmentInfo ai:  ti.getAssignmentTeams()) {
				// ...setting each one's title and membership count.
				if (setAITitle(  ai, teamTitles)) {
					setAIMembers(ai, teamCounts );
					ai.setPresenceDude("pics/team_16.png");
				}
				else {
					removeList.add(ai);
				}
			}
			removeUnresolvedAssignees(ti.getAssignmentTeams(), removeList);
		}		

		// Finally, one last scan through the List<TaskInfo>...
		Comparator<AssignmentInfo> comparator = new AssignmentInfoComparator(true);
		for (TaskInfo ti:  tasks) {
			// ...this time, to sort the assignee lists.
			Collections.sort(ti.getAssignments(),      comparator);
			Collections.sort(ti.getAssignmentGroups(), comparator);
			Collections.sort(ti.getAssignmentTeams(),  comparator);
		}
	}
	
	/*
	 * Scan the List<TaskInfo> and stores a location string in each
	 * TaskInfo.
	 */
	private static void completeBinderLocations(AllModulesInjected bs, List<TaskInfo> tasks) {
		// If we don't have any TaskInfo's to complete...
		if ((null == tasks) || tasks.isEmpty()) {
			// ..bail.
			return;
		}
		
		// Generate an List<Long> of the unique binder IDs.
		List<Long> binderIds = new ArrayList<Long>();		
		for (TaskInfo ti:  tasks) {
			addLToLLIfUnique(binderIds, ti.getTaskId().getBinderId());
		}

		// Do we have any binder IDs?
		Map<Long, String> binderLocationMap = new HashMap<Long, String>();
		if (!(binderIds.isEmpty())) {
			try {
				// Yes!  Can we read the binders?
				SortedSet<Binder> binders = bs.getBinderModule().getBinders(binderIds);
				if ((null != binders) && (!(binders.isEmpty()))) {
					// Yes!  Scan the binders...
					for (Binder binder:  binders) {
						// ...storing the location for each in the Map.
						String location = binder.getTitle();
						try {
							Binder parentBinder = ((Binder) binder.getParentWorkArea());
							location = (location + " (" + parentBinder.getTitle() + ")");
						}
						catch (Exception ex) {/* Ignored. */}
						binderLocationMap.put(binder.getId(), location);					
					}
				}
			}
			catch (Exception ex) {/* Ignored. */}
		}

		// Scan the List<TaskInfo> again...
		for (TaskInfo task:  tasks) {
			// ...this time, storing the location for each binder in
			// ...the TaskInfo's.
			String location = binderLocationMap.get(task.getTaskId().getBinderId());
			task.setLocation((null == location) ? "" : location);
		}
	}

	/*
	 * When initially build, the AssignmentInfo's in the
	 * List<AssignmentInfo> only contain the assignee IDs.  We need to
	 * complete them with each assignee's title, ...
	 */
	@SuppressWarnings("unchecked")
	private static void completeMembershipAIs(AllModulesInjected bs, List<AssignmentInfo> assignees) {
		// Collect the IDs of the assignees in a List<Long>.
		List<Long> assigneeIds = new ArrayList<Long>();
		for (AssignmentInfo ai:  assignees) {
			assigneeIds.add(ai.getId());
		}
		
		// Construct Maps, mapping the principal IDs to their titles
		// and membership counts.
		Map<Long, Group>           groups            = new HashMap<Long, Group>();
		Map<Long, GwtPresenceInfo> presenceInfo      = new HashMap<Long, GwtPresenceInfo>();
		Map<Long, Integer>         groupCounts       = new HashMap<Long, Integer>();
		Map<Long, Long>            presenceUserWSIds = new HashMap<Long, Long>();
		Map<Long, String>          principalTitles   = new HashMap<Long, String>();
		Map<Long, User>            users             = new HashMap<Long, User>();
		if (!(assigneeIds.isEmpty())) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(assigneeIds);}
			catch (Exception ex) {/* Ignored. */}
			if ((null != principals) && (!(principals.isEmpty()))) {
				boolean isPresenceEnabled = GwtServerHelper.isPresenceEnabled();
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					Long pId = p.getId();
					principalTitles.put(pId, p.getTitle());					
					if (p instanceof GroupPrincipal) {
						Group group = ((Group) p);
						groups.put(pId, group);
						groupCounts.put(pId, getGroupCount(group));						
					}
					else if (p instanceof UserPrincipal) {
						User user = ((User) p);
						users.put(pId, user);
						presenceUserWSIds.put(pId, user.getWorkspaceId());
						if (isPresenceEnabled) {
							presenceInfo.put(pId, GwtServerHelper.getPresenceInfo(user));
						}
					}
				}
			}
		}
		
		// Scan the List<AssignmentInfo> again.
		List<AssignmentInfo> bogusAssignees = new ArrayList<AssignmentInfo>();
		for (AssignmentInfo ai:  assignees) {
			// Is this assignment that of a user?
			Long aiId = ai.getId();
			if (null != users.get(aiId)) {
				// Yes!  Set its title and presence information.
				setAITitle(           ai, principalTitles  );
				setAIPresence(        ai, presenceInfo     );
				setAIPresenceUserWSId(ai, presenceUserWSIds);
			}
			
			// No, this assignment isn't that of a user!  Is it that
			// of a group?
			else if (null != groups.get(aiId)) {
				// Yes!  Set its title and membership count.
				setAITitle(  ai, principalTitles);
				setAIMembers(ai, groupCounts    );
				ai.setPresenceDude("pics/group_icon_small.png");
			}
			
			else {
				// No, this assignment isn't that of a group either!
				// It may be a deleted user, or something else we just
				// can't access.  Track it as bogus.
				bogusAssignees.add(ai);
			}
		}

		// Scan any AssignmentInfo's we couldn't complete...
		for (AssignmentInfo bogusAssignee:  bogusAssignees) {
			// ...and remove the from the list.
			assignees.remove(bogusAssignee);
		}

		// Finally, sort the list so that it appears nicely in the
		// assigned to list.
		Collections.sort(assignees, new AssignmentInfoComparator(true));
	}

	/*
	 * Scans the List<TaskInfo> and sets the access rights for the
	 * current user for each task.
	 */
	private static void completeTaskRights(AllModulesInjected bs, List<TaskInfo> tasks) {
		// If we don't have any TaskInfo's to complete...
		if ((null == tasks) || tasks.isEmpty()) {
			// ..bail.
			return;
		}

		// Collect the entry IDs of the tasks from the List<TaskInfo>.
		List<Long> entryIds = new ArrayList<Long>();
		for (TaskInfo ti:  tasks) {
			entryIds.add(ti.getTaskId().getEntryId());
		}
		
		try {
			// Read the FolderEntry's for the tasks...
			FolderModule fm = bs.getFolderModule();
			SortedSet<FolderEntry> taskEntries = fm.getEntries(entryIds);
			
			// ...mapping each FolderEntry to its ID.
			Map<Long, FolderEntry> taskEntryMap = new HashMap<Long, FolderEntry>();
			for (FolderEntry task: taskEntries) {
				taskEntryMap.put(task.getId(), task);
			}

			// Scan the List<TaskInfo> again.
			for (TaskInfo ti:  tasks) {
				// Do we have a FolderEntry for this task?
				FolderEntry task = taskEntryMap.get(ti.getTaskId().getEntryId());
				if (null != task) {
					// Yes!  Store the user's rights to that
					// FolderEntry.
					ti.setCanModify(fm.testAccess(task, FolderOperation.modifyEntry   ));
					ti.setCanPurge( fm.testAccess(task, FolderOperation.deleteEntry   ));
					ti.setCanTrash( fm.testAccess(task, FolderOperation.preDeleteEntry));
				}
			}
			
		}
		catch (Exception ex) {/* Ignored. */}
	}
	
	/**
	 * Deletes the specified tasks.
	 *
	 * @param request
	 * @param bs
	 * @param taskIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean deleteTasks(HttpServletRequest request, AllModulesInjected bs, List<TaskId> taskIds) throws GwtTeamingException {
		try {
			// Before we delete any of them... 
			FolderModule fm = bs.getFolderModule();
			for (TaskId taskId:  taskIds) {
				// ...make sure we can delete all of them.
				fm.checkAccess(
					fm.getEntry(
						taskId.getBinderId(),
						taskId.getEntryId()),
						FolderOperation.preDeleteEntry);
			}

			// If we get here, we have rights to delete all the tasks
			// that we were given.  Scan them...
			List<Long> binderIds = new ArrayList<Long>();  
			for (TaskId taskId:  taskIds) {
				// ...deleting each.
				Long binderId = taskId.getBinderId();
				addLToLLIfUnique(binderIds, binderId);
				TrashHelper.preDeleteEntry(
					bs,
					binderId,
					taskId.getEntryId());
			}

			// Scan the IDs of binders that we're deleting from...
			for (Long binderId:  binderIds) {
				// ...and tell each to update the calculated dates they
				// ...contain.
				updateCalculatedDates(
					request,
					bs,
					getTaskBinder(bs, binderId),
					null);	// null -> Update the calculated dates for all the tasks in the binder.
			}

			// If we get here, the deletes were successful.
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
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
			StringBuffer buf = new StringBuffer("");
			buf.append(buildDumpString("\n\tTask ID",            ti.getTaskId().getEntryId()    ));
			buf.append(buildDumpString("\n\t\tBinder ID",        ti.getTaskId().getBinderId()   ));
			buf.append(buildDumpString("\n\t\tCanModify",        ti.getCanModify()              ));
			buf.append(buildDumpString("\n\t\tCanPurge",         ti.getCanPurge()               ));
			buf.append(buildDumpString("\n\t\tCanTrash",         ti.getCanTrash()               ));
			buf.append(buildDumpString("\n\t\tSeen",             ti.getSeen()                   ));
			buf.append(buildDumpString("\n\t\tAssignments",      ti.getAssignments(),      false));
			buf.append(buildDumpString("\n\t\tAssignmentGroups", ti.getAssignmentGroups(), true ));
			buf.append(buildDumpString("\n\t\tAssignmentTeams",  ti.getAssignmentTeams(),  true ));
			buf.append(buildDumpString("\n\t\tLocation",         ti.getLocation()               ));
			buf.append(buildDumpString("\n\t\tCompleted",        ti.getCompleted()              ));
			buf.append(buildDumpString("\n\t\t\tDate",           ti.getCompletedDate()          ));
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
		logBuffer.append(String.valueOf(tl.getEntryId()));
		
		// ...and write it.
		m_logger.debug(logBuffer.toString());
		
		// Scan this TaskLink's subtasks...
		int subtaskDepth = (depth + 1);
		for (TaskLink tlScan:  tl.getSubtasks()) {
			// ...logging each of them one level deeper.
			dumpTaskLink(tlScan, subtaskDepth);
		}
	}

	/**
	 * Marks the given task in the given binder as having its subtask
	 * display expanded.
	 *
	 * @param bs
	 * @param binderId
	 * @param taskId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean expandSubtasks(AllModulesInjected bs, Long binderId, Long entryId) throws GwtTeamingException {
		try {
			List<Long> collapsedSubtasks = getCollapsedSubtasks(bs, binderId);
			if (collapsedSubtasks.contains(entryId)) {
				collapsedSubtasks.remove(entryId);
		   		bs.getProfileModule().setUserProperty(null, binderId, ObjectKeys.USER_PROPERTY_COLLAPSE_SUBTASKS, collapsedSubtasks);				
			}
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}

	/*
	 * Searches the List<TaskListItem> for the task whose logical start
	 * date is the earliest and returns that logical start date.
	 */
	private static TaskDate findEarliestLogicalStart(List<TaskListItem> taskList) {
		TaskDate reply = null;		
		for (TaskListItem task:  taskList) {
			TaskDate start = task.getTask().getEvent().getLogicalStart();
			if (hasDateValue(start)) {
				if ((null == reply) || (start.getDate().getTime() < reply.getDate().getTime())) {
					reply = start;
				}
			}
		}		
		return reply;
	}
	
	/*
	 * Searches the List<TaskListItem> for the task whose logical end
	 * date is the latest and returns that logical end date.
	 */
	private static TaskDate findLatestLogicalEnd(List<TaskListItem> taskList) {
		TaskDate reply = null;
		for (TaskListItem task:  taskList) {
			TaskDate end = task.getTask().getEvent().getLogicalEnd();
			if (hasDateValue(end)) {
				if ((null == reply) || (end.getDate().getTime() > reply.getDate().getTime())) {
					reply = end;
				}
			}
		}
		return reply;
	}
	
	/*
	 * Searches a List<TaskInfo> for a task with a specific ID.
	 */
	private static TaskInfo findTaskInList(Long entryId, List<TaskInfo> tasks) {
		// Scan the List<TaskInfo>.
		for (TaskInfo task:  tasks) {
			// Is this the task in question?
			if (task.getTaskId().getEntryId().equals(entryId)) {
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
	 * Returns the Calendar equivalent of a TaskDate.
	 */
	private static Calendar getCFromTD(TaskDate td) {
		GregorianCalendar reply;
		if (null == td) {
			reply = null;
		}
		else {
			reply = new GregorianCalendar();
			reply.setTime(td.getDate());
		}
		return reply;
	}

	/*
	 * Returns a List<Long> of the task IDs from the Binder that should
	 * have their subtask lists collapsed.
	 */
	@SuppressWarnings("unchecked")
	private static List<Long> getCollapsedSubtasks(AllModulesInjected bs, Long binderId) {
		User user = GwtServerHelper.getCurrentUser();
		UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
		List<Long> reply = ((List<Long>) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_COLLAPSE_SUBTASKS));
		if (null == reply) {
			reply = new ArrayList<Long>();
		}
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
			reply = null;
		}
		return reply;
	}

	/*
	 * Reads an event from a map.
	 */
	@SuppressWarnings("unchecked")
	private static TaskEvent getEventFromMap(Map m, boolean clientBundle) {
		TaskEvent reply = new TaskEvent();

		// Extract the event's 'All Day Event' flag from the Map.
		String tz = getStringFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_TIME_ZONE_ID));
		boolean allDayEvent = (!(MiscUtil.hasString(tz)));
		reply.setAllDayEvent(allDayEvent);

		// Are we reading the information for the client?
		TaskDate calcEnd = getTaskDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_CALC_END_DATE), allDayEvent, true);
		if (!clientBundle) {
			// No!  Extract the event's actual and calculated start and
			// end dates.
			ServerDates sd = new ServerDates();
			sd.setActualStart(getTaskDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_START_DATE     ), allDayEvent, true));
			sd.setActualEnd(  getTaskDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_END_DATE       ), allDayEvent, true));
			sd.setCalcStart(  getTaskDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_CALC_START_DATE), allDayEvent, true));
			sd.setCalcEnd(    calcEnd                                                                                );
			reply.setServerData(sd);
		}
		
		// Extract the event's actual start and end...
		reply.setActualStart(getTaskDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_START_DATE), allDayEvent, false ));		
		reply.setActualEnd(  getTaskDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_END_DATE  ), allDayEvent, false ));
		
		// ...extract the event's logical start and end...
		reply.setLogicalStart(getTaskDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_LOGICAL_START_DATE), allDayEvent, false ));		
		reply.setLogicalEnd(  getTaskDateFromMap(m, buildEventFieldName(Constants.EVENT_FIELD_LOGICAL_END_DATE  ), allDayEvent, false ));
		
		// ...pass through an indicator of whether it's using a
		// ...calculated end date...
		reply.setEndIsCalculated(null != calcEnd);

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
		Set<Long> groupMemberIds = getGroupMemberIds(group);
		return ((null == groupMemberIds) ? 0 : groupMemberIds.size());
	}

	/*
	 * Returns a Set<Long> of the IDs of the members of a group.
	 */
	private static Set<Long> getGroupMemberIds(GroupPrincipal group) {
		List<Long> groupIds = new ArrayList<Long>();
		groupIds.add(group.getId());
		Set<Long> groupMemberIds = null;
		try {
			ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
			groupMemberIds = profileDao.explodeGroups(groupIds, group.getZoneId());
		}
		catch (Exception ex) {/* Ignored. */}
		return validatePrincipalIds(groupMemberIds);
	}
	
	/**
	 * Returns a List<AssignmentInfo> containing information about the
	 * membership of a group.
	 * 
	 * @param bs
	 * @param groupId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static List<AssignmentInfo> getGroupMembership(AllModulesInjected bs, Long groupId) throws GwtTeamingException {
		try {
			// Construct a List<AssignmentInfo> we can return.
			List<AssignmentInfo> reply = new ArrayList<AssignmentInfo>();
			
			// Can we resolve the group ID to a GroupPrincipal?
			List<Long> groupIds = new ArrayList<Long>();
			groupIds.add(groupId);
			List principals = ResolveIds.getPrincipals(groupIds);
			if ((null != principals) && (!(principals.isEmpty()))) {
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					if (p instanceof GroupPrincipal) {
						// Yes!  Can we read its membership IDs?
						Set<Long> groupMemberIds = getGroupMemberIds((GroupPrincipal) p);
						if (null != groupMemberIds) {
							// Yes!  Add a base AssignmentInfo with
							// each ID to the List<AssignmentInfo> that
							// we're going to return.
							for (Long memberId:  groupMemberIds) {
								reply.add(AssignmentInfo.construct(memberId));
							}
						}
					}
				}
			}
			
			// Complete the content of the AssignmentInfo's to return.
			completeMembershipAIs(bs, reply);

			// If we get here, reply refers to a List<AssignmentInfo>
			// describing the members of a group.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
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
			m_logger.debug("GwtTaskHelper.getTaskBinder( " + String.valueOf(binderId) + ": EXCEPTION ): ", ex);
			throw GwtServerHelper.getGwtTeamingException(ex);
		}

		if (null == reply) {
			m_logger.debug("GwtTaskHelper.getTaskBinder( " + String.valueOf(binderId) + ": Could not access binder. )");
			throw GwtServerHelper.getGwtTeamingException();
		}
		
		return reply; 
	}
	
	/**
	 * Reads the task information from the specified binder.
	 * 
	 * @param request
	 * @param bs
	 * @param binder
	 * @param filterTypeParam
	 * @param modeTypeParam
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static List<TaskListItem> getTaskList( HttpServletRequest request, AllModulesInjected bs, Binder binder, String filterTypeParam, String modeTypeParam) throws GwtTeamingException {
		TaskBundle tb = getTaskBundle(request, bs, binder, filterTypeParam, modeTypeParam);
		return tb.getTasks();
	}

	/**
	 * Returns the TaskBundle from a task folder.
	 *
	 * @param request
	 * @param bs
	 * @param binder
	 * @param filterTypeParam
	 * @param modeTypeParam
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static TaskBundle getTaskBundle(HttpServletRequest request, AllModulesInjected bs, Binder binder, String filterTypeParam, String modeTypeParam) throws GwtTeamingException {
		// Clear any previously stored reason for the tasks being
		// read.  By the time this is called, TaskListing.java will
		// have already made use of that information.
		clearTaskChanged(
			bs,
			GwtServerHelper.getCurrentUser().getId(),
			binder.getId());
		
		return
			getTaskBundleImpl(
				request,
				bs,
				binder,
				filterTypeParam,
				modeTypeParam,
				true);	// true -> Retrieve the TaskBundle on behalf of the client.
	}
	
	private static TaskBundle getTaskBundleImpl(HttpServletRequest request, AllModulesInjected bs, Binder binder, String filterTypeParam, String modeTypeParam, boolean clientBundle) throws GwtTeamingException {
		// Build a base TaskBundle...
		TaskBundle reply = buildBaseTaskBundle(
			request,
			bs,
			binder,
			filterTypeParam,
			modeTypeParam);
		
		// ...and read the tasks into it.
		readTaskList(
			request,
			bs,
			binder,
			reply,
			getCollapsedSubtasks(bs, binder.getId()),
			clientBundle);
		
		// If we get here, reply refers to the requested TaskBundle.
		// Return it.
		return reply;
	}
	
	/*
	 * Reads a Date from a Map and constructs a TaskDate from it.
	 */
	@SuppressWarnings("unchecked")
	private static TaskDate getTaskDateFromMap(Map m, String key, boolean allDayEvent, boolean nullIfNotThere) {
		TaskDate reply;
		Date date = getDateFromMap(m, key);
		if ((null == date) && nullIfNotThere) {
			reply = null;
		}
		else {
			reply = new TaskDate();
			reply.setDate(                                     date              );
			reply.setDateDisplay(EventHelper.getDateTimeString(date, allDayEvent));
		}
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
	 * Returns the TaskDate equivalent of a Calendar. 
	 */
	private static TaskDate getTDFromC(Calendar c, boolean allDayEvent) {
		Date date = ((null == c) ? null : c.getTime());
		TaskDate reply = new TaskDate(date);
		reply.setDateDisplay(EventHelper.getDateTimeString(date, allDayEvent));
		return reply;
	}

	/*
	 * Returns the TaskDuration equivalent of an Event Duration.
	 */
	private static TaskDuration getTDurFromEDur(Duration eDuration) {
		TaskDuration reply;
		if (null == eDuration) {
			reply = null;
		}
		else {
			reply = new TaskDuration();
			reply.setDays(   eDuration.getDays()   );
			reply.setHours(  eDuration.getHours()  );
			reply.setMinutes(eDuration.getMinutes());
			reply.setSeconds(eDuration.getSeconds());
			reply.setWeeks(  eDuration.getWeeks()  );
		}
		return reply;
	}

	/*
	 * Returns a count of the members of a team.
	 */
	private static int getTeamCount(AllModulesInjected bs, Binder binder) {
		Set<Long> teamMemberIds = getTeamMemberIds(bs, binder.getId());
		return ((null == teamMemberIds) ? 0 : teamMemberIds.size());
	}

	/*
	 * Returns a Set<Long> of the member IDs of a team.
	 */
	private static Set<Long> getTeamMemberIds(AllModulesInjected bs, Long binderId) {
		Set<Long> teamMemberIds = null;
		try {teamMemberIds = bs.getBinderModule().getTeamMemberIds(binderId, false);}
		catch (Exception ex) {/* Ignored. */}
		return validatePrincipalIds(teamMemberIds);
	}
	
	/**
	 * Returns a List<AssignmentInfo> containing information about the
	 * membership of a team.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static List<AssignmentInfo> getTeamMembership(AllModulesInjected bs, Long binderId) throws GwtTeamingException {
		try {
			// Construct a List<AssignmentInfo> we can return.
			List<AssignmentInfo> reply = new ArrayList<AssignmentInfo>();
			
			// Can we resolve the binder ID to a set of team member
			// IDs?
			Set<Long> teamMemberIds = getTeamMemberIds(bs, binderId);
			if (null != teamMemberIds) {
				// Yes!  Add a base AssignmentInfo with each ID to the
				// List<AssignmentInfo> that we're going to return.
				for (Long memberId:  teamMemberIds) {
					reply.add(AssignmentInfo.construct(memberId));
				}
			}
			
			// Complete the content of the AssignmentInfo's to return.
			completeMembershipAIs(bs, reply);			

			// If we get here, reply refers to a List<AssignmentInfo>
			// describing the members of a team.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}

	/*
	 * Returns true if given a TaskDate with a value and false
	 * otherwise.
	 */
	private static boolean hasDateValue(TaskDate td) {
		return ((null != td) && MiscUtil.hasString(td.getDateDisplay()));
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
			FolderEntry taskFE = bs.getFolderModule().getEntry(null, tl.getEntryId());
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
	private static void processTaskLinkList(List<TaskListItem> taskList, List<Long> collapsedSubtasks, List<TaskInfo> tasks, List<TaskLink> links) {
		// Scan the List<TaskLink>.
		for (TaskLink link:  links) {
			// Can we find the TaskInfo for this TaskLink?
			TaskInfo task = findTaskInList(link.getEntryId(), tasks);
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
			tli.setExpandedSubtasks(!(collapsedSubtasks.contains(task.getTaskId().getEntryId())));
			taskList.add(tli);
			
			// ...and recursively process any subtasks.
			processTaskLinkList(tli.getSubtasks(), collapsedSubtasks, tasks, link.getSubtasks());
		}
	}
	
	/**
	 * Purges the specified tasks.
	 * 
	 * @param bs
	 * @param taskIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean purgeTasks(HttpServletRequest request, AllModulesInjected bs, List<TaskId> taskIds) throws GwtTeamingException {
		try {
			// Before we purge any of them... 
			FolderModule fm = bs.getFolderModule();
			for (TaskId taskId:  taskIds) {
				// ...make sure we can purge all of them.
				fm.checkAccess(
					fm.getEntry(
						taskId.getBinderId(),
						taskId.getEntryId()),
						FolderOperation.deleteEntry);
			}

			// If we get here, we have rights to purge all the tasks
			// that we were given.  Scan them...
			List<Long> binderIds = new ArrayList<Long>();  
			for (TaskId taskId:  taskIds) {
				// ...deleting each.
				Long binderId = taskId.getBinderId();
				addLToLLIfUnique(binderIds, binderId);
				fm.deleteEntry(
					binderId,
					taskId.getEntryId());
			}
			
			// Scan the IDs of binders that we're purging from...
			for (Long binderId:  binderIds) {
				// ...and tell each to update any calculated dates they
				// ...contain.
				updateCalculatedDates(
					request,
					bs,
					getTaskBinder(bs, binderId),
					null);	// null -> Update the calculated dates for all the tasks in the binder.
			}

			// If we get here, the purges were successful.
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}
	
	/*
	 * Reads the task information from the specified binder and stores
	 * it in the given TaskBundle.
	 * 
	 * Apply task linkage, ... as necessary to the list stored.
	 */
	private static void readTaskList(HttpServletRequest request, AllModulesInjected bs, Binder binder, TaskBundle tb, List<Long> collapsedSubtasks, boolean clientBundle) throws GwtTeamingException {
		// Create a List<TaskListItem> that we'll fill up with the task
		// list.
		List<TaskListItem> taskList = new ArrayList<TaskListItem>();
		tb.setTasks(taskList);

		// Read the tasks from the binder.
		List<TaskInfo> tasks = readTasks(
			request,
			bs,
			binder,
			tb.getFilterTypeParam(),
			tb.getModeTypeParam(),
			clientBundle);		
		tb.setTotalTasks(tasks.size());

		// Do we need to respect the task linkage information?
		List<TaskLink> taskOrder;
		boolean respectLinkage = tb.respectLinkage();
		if (respectLinkage) {
			// Yes!  Process the order information from the supplied
			// task linkage.
			taskOrder = tb.getTaskLinkage().getTaskOrder();
			processTaskLinkList(taskList, collapsedSubtasks, tasks, taskOrder);
		}
		else {
			// No, we are not applying the task linkage!  In this case,
			// no order/hierarchy is assumed.
			taskOrder = null;
		}

		// Scan any tasks that weren't addressed by the task linkage...
		boolean linkageChanged = false;
		for (TaskInfo task:  tasks) {
			// ...add each as a TaskListItem to the task list being
			// ...built...
			TaskListItem tli = new TaskListItem();
			tli.setTask(task);
			if (respectLinkage) {
				tli.setExpandedSubtasks(!(collapsedSubtasks.contains(task.getTaskId().getEntryId())));
			}
			taskList.add(tli);

			// ...and if we're handling the task linkage... 
			if (respectLinkage) {
				// ...add it to the order since it will now be
				// ...considered 'linked into' the task folder's order
				// ...and hierarchy.
				TaskLink tl = new TaskLink();
				tl.setEntryId(task.getTaskId().getEntryId());
				taskOrder.add(tl);
				linkageChanged = true;
			}
		}

		// If we changed the linkage based on the rendering and we have
		// rights to modify the linkage...
		if (linkageChanged && tb.getCanModifyTaskLinkage()) {
			// ...save the new linkage.
			saveTaskLinkage(bs, binder, tb.getTaskLinkage());
		}
	}

	/*
	 * Reads the tasks from the specified binder.
	 */
	@SuppressWarnings("unchecked")
	private static List<TaskInfo> readTasks(HttpServletRequest request, AllModulesInjected bs, Binder binder, String filterTypeParam, String modeTypeParam, boolean clientBundle) throws GwtTeamingException {
		Map taskEntriesMap;		
		try {
			// Setup to read the task entries...
			HttpSession session = WebHelper.getRequiredSession(request);
			GwtUISessionData optionsObj = ((GwtUISessionData) session.getAttribute(TaskHelper.CACHED_FIND_TASKS_OPTIONS_KEY));
			Map options = ((Map) optionsObj.getData());
			options.put(ObjectKeys.SEARCH_MAX_HITS, (Integer.MAX_VALUE - 1));
			options.put(ObjectKeys.SEARCH_OFFSET,   0);
	
			// ...and read them.
			taskEntriesMap = TaskHelper.findTaskEntries(
				bs,
				binder,
				filterTypeParam,
				modeTypeParam,
				options);
		}
		catch (Exception ex) {
			m_logger.error("GwtTaskHelper.readTasks( EXCEPTION ): ", ex);
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
			ti.setEvent(           getEventFromMap(    taskEntry, clientBundle                                               ));
			ti.setStatus(          getStringFromMap(   taskEntry, "status"                                                   ));
			ti.setCompleted(       getStringFromMap(   taskEntry, "completed"                                                ));
			ti.setSeen(            seenMap.checkIfSeen(taskEntry                                                             ));
			ti.setEntityType(      getStringFromMap(   taskEntry, Constants.ENTITY_FIELD                                     ));
			ti.setPriority(        getStringFromMap(   taskEntry, "priority"                                                 ));
			ti.setAssignments(     getAIListFromMap(   taskEntry, "assignment"                                               ));
			ti.setAssignmentGroups(getAIListFromMap(   taskEntry, "assignment_groups"                                        ));
			ti.setAssignmentTeams( getAIListFromMap(   taskEntry, "assignment_teams"                                         ));
			
			String title = getStringFromMap(taskEntry, Constants.TITLE_FIELD);
			if (!(MiscUtil.hasString(title))) {
				title = ("--" + NLT.get("entry.noTitle") + "--");
			}
			ti.setTitle(title);
			
			TaskId taskId = new TaskId();
			taskId.setBinderId(getLongFromMap(taskEntry, Constants.BINDER_ID_FIELD));
			taskId.setEntryId( getLongFromMap(taskEntry, Constants.DOCID_FIELD    ));
			ti.setTaskId(taskId);

			ti.setCompletedDate(
				getTaskDateFromMap(
					taskEntry,
					Constants.TASK_COMPLETED_DATE_FIELD,
					ti.getEvent().getAllDayEvent(),
					false));	// false -> Don't return a null entry.
			
			reply.add(ti);
		}

		// At this point, the TaskInfo's in the List<TaskInfo> are not
		// complete.  They're missing things like the user's rights to
		// the entries, the location of their binders and details about
		// the task's assignments.  Complete their content.  Note that
		// we do this AFTER collecting data from the search index so
		// that we only have to perform a single DB read for each type
		// of information we need to complete the TaskInfo details.
		completeTaskRights(         bs, reply);
		if (clientBundle) {
			completeBinderLocations(bs, reply);
			completeAIs(            bs, reply);
		}
				
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("GwtTaskHelper.readTasks( Read List<TaskInfo> for binder ): " + String.valueOf(binderId));
			dumpTaskInfoList(reply);
		}
		
		// If we get here, reply refers to the List<TaskInfo> of the
		// entries contained in binder.  Return it. 
		return reply;
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

	/*
	 * Removes the AssignmentInfo's in a remove list from an assignee
	 * list and clears the remove list.
	 */
	private static void removeUnresolvedAssignees(List<AssignmentInfo> assigneeList, List<AssignmentInfo> removeList) {
		// Scan the remove list...
		for (AssignmentInfo ai: removeList) {
			// ...removing the assignments from the assignee list...
			assigneeList.remove(ai);
		}
		
		// ...and clearing the remove list.
		removeList.clear();
	}
	
	/**
	 * Stores a new completed value for a task.
	 * 
	 * @param bs
	 * @param binderId
	 * @param taskIds
	 * @param completed
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static String saveTaskCompleted(AllModulesInjected bs, List<TaskId> taskIds, String completed) throws GwtTeamingException {
		// Are we marking the tasks completed?
		boolean nowCompleted = (TaskInfo.COMPLETED_100.equals(completed));
				
		// Scan the tasks whose completed value is changing.
		FolderModule  fm      = bs.getFolderModule();
		ProfileModule pm      = bs.getProfileModule();
		SeenMap       seenMap = pm.getUserSeenMap(null);
		for (TaskId taskId:  taskIds) {
			Long binderId = taskId.getBinderId();
			Long entryId  = taskId.getEntryId();
			try {
				// Has the user seen this entry already?
				FolderEntry fe       = fm.getEntry(binderId, entryId);
				boolean     taskSeen = seenMap.checkIfSeen(fe);
				
				// Construct the appropriate form data for the change...
				Map formData = new HashMap();
				formData.put(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {completed});
				if (nowCompleted) {
					formData.put(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {TaskInfo.STATUS_COMPLETED});
				}				
				else {				
					String currentStatus = TaskHelper.getTaskStatusValue(   fe);
					if (TaskInfo.STATUS_COMPLETED.equals(currentStatus)) {
						formData.put(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {TaskInfo.STATUS_IN_PROCESS});
					}
				}

				// ...and modify the entry.
				fm.modifyEntry(
					binderId,
					entryId, 
					new MapInputData(formData),
					null,
					null,
					null,
					null);
				
				// If the user saw the entry before we modified it...
				if (taskSeen) {
					// ...retain that seen state.
					pm.setSeen(null, fe);
				}
			}
			
			catch (Exception ex) {
				throw GwtServerHelper.getGwtTeamingException(ex);
			}
		}

		// If we're marking the entries completed...
		String reply;
		if (nowCompleted) {
			// ...return the current date/time stamp for the current
			// ...user's locale and time zone...
			User user = GwtServerHelper.getCurrentUser();			
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale());
			df.setTimeZone(user.getTimeZone());			
			reply = df.format(new Date());
		}
		else {
			// ...otherwise, return null.
			reply = null;
		}
		return reply;
	}
	
	/**
	 * Stores a new due date value for a task.
	 * 
	 * @param bs
	 * @param taskId
	 * @param taskEvent
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static TaskEvent saveTaskDueDate(AllModulesInjected bs, TaskId taskId, TaskEvent taskEvent) throws GwtTeamingException {
		TaskEvent reply = null;
		try {
			// - - - - - - - - - - - - - //
			// Modify the task's Event.  //
			// - - - - - - - - - - - - - //
			
			// Read the Event currently stored on the task...
			Long binderId = taskId.getBinderId();
			Long entryId = taskId.getEntryId();
			FolderModule fm = bs.getFolderModule();
			FolderEntry fe = fm.getEntry(binderId, entryId);
			CustomAttribute customAttribute = fe.getCustomAttribute(TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME);
			Event event = ((Event) customAttribute.getValue());
			if (null == event) {
				event = new Event();
			}

			// ...modify that Event based on the TaskEvent we were
			// ...given...
			boolean oldIsAllDay = event.isAllDayEvent();
			boolean newIsAllDay = taskEvent.getAllDayEvent();
			if (oldIsAllDay != newIsAllDay) {
				// The Event's all day setting is changing!
				// Store/clear a TZ in the Event.  It's the TZ controls
				// whether an event is recognized as an all day event
				// or not.
				event.setTimeZone(
					newIsAllDay ?
						null    :							// null ---> All day.
						TimeZoneHelper.getTimeZone("GMT"));	// Any TZ -> Not all day.
			}			
			TaskDuration tDuration = taskEvent.getDuration();
			Duration eDuration = new Duration();
			if (null != tDuration) {
				eDuration.setDays(tDuration.getDays());
			}
			event.setDuration(eDuration);
			event.setDtCalcStart((Calendar) null); event.setDtStart(getCFromTD(taskEvent.getActualStart()));			
			event.setDtCalcEnd(  (Calendar) null); event.setDtEnd(  getCFromTD(taskEvent.getActualEnd()));
			
			// ...check whether the user has seen this entry already...
			ProfileModule pm = bs.getProfileModule();
			boolean taskSeen = pm.getUserSeenMap(null).checkIfSeen(fe);
			
			// ...save the new Event...
			Map formData = new HashMap(); 
			formData.put(TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME, event);
			fm.modifyEntry(binderId, entryId, new MapInputData(formData), null, null, null, null);
			
			// ...and if the user saw the entry before we modified
			// ...it...
			if (taskSeen) {
				// ...retain that seen state.
				pm.setSeen(null, fe);
			}

			// - - - - - - - - - - - - - - - //
			// Create a TaskEvent to return. //
			// - - - - - - - - - - - - - - - //
			
			// Reread the task so that we have its Event AFTER we
			// modified it.  Note that there's processing that goes on
			// that may have changed it as part of the modify.
			fe              = fm.getEntry(binderId, entryId);
			customAttribute = fe.getCustomAttribute(TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME);
			event           = ((Event) customAttribute.getValue());
			
			// Finally we need to create a TaskEvent to return that
			// reflects the Event stored as it stands AFTER it was
			// modified.
			reply = new TaskEvent(true);
			reply.setActualStart(getTDFromC(     event.getDtStart(), event.isAllDayEvent())); reply.setLogicalStart(getTDFromC(event.getLogicalStart(), event.isAllDayEvent()));
			reply.setActualEnd(  getTDFromC(     event.getDtEnd(),   event.isAllDayEvent())); reply.setLogicalEnd(  getTDFromC(event.getLogicalEnd(),   event.isAllDayEvent()));			
			reply.setDuration(   getTDurFromEDur(event.getDuration()));				
			reply.setAllDayEvent(event.isAllDayEvent());
			
			// If we get here, reply refers to the modified TaskEvent or is
			// null.  Return it.
			return reply;			
		}
		
		catch (Exception ex) {
			m_logger.debug("GwtTaskHelper.saveTaskDueDate( Can't Save Due Date on task:  '" + taskId.getEntryId() + "' )");
			m_logger.debug("GwtTaskHelper.saveTaskDueDate( EXCEPTION ):  ", ex);
			
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
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

	/**
	 * Stores a new priority value for a task.
	 * 
	 * @param bs
	 * @param binderId
	 * @param entryId
	 * @param priority
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static Boolean saveTaskPriority(AllModulesInjected bs, Long binderId, Long entryId, String priority) throws GwtTeamingException {
		try {
			// Has the user seen this entry already?
			FolderModule  fm       = bs.getFolderModule();
			ProfileModule pm       = bs.getProfileModule();
			FolderEntry   fe       = fm.getEntry(binderId, entryId);
			boolean       taskSeen = pm.getUserSeenMap(null).checkIfSeen(fe);
			
			Map formData = new HashMap();
			formData.put(TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {priority});
			fm.modifyEntry(
				binderId,
				entryId, 
				new MapInputData(formData),
				null,
				null,
				null,
				null);
			
			// If the user saw the entry before we modified it...
			if (taskSeen) {
				// ...retain that seen state.
				pm.setSeen(null, fe);
			}
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}

	/**
	 * Save a task folder sort options on the specified binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param sortKey
	 * @param sortAscending
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveTaskSort(AllModulesInjected bs, Long binderId, String sortKey, boolean sortAscending) throws GwtTeamingException {
		try {
			Long          userId = GwtServerHelper.getCurrentUser().getId();
			ProfileModule pm     = bs.getProfileModule();
			pm.setUserProperty(userId, binderId, ObjectKeys.SEARCH_SORT_BY,                      sortKey       );
			pm.setUserProperty(userId, binderId, ObjectKeys.SEARCH_SORT_DESCEND, String.valueOf(!sortAscending));
			
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("GwtTaskHelper.saveTaskSort( Stored task sort for binder ):  Binder:  " + binderId.longValue() + ", Sort Key:  '" + sortKey + "', Sort Ascending:  " + sortAscending);
			}
			return Boolean.FALSE;
		}
		
		catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException(ex);
		}
	}

	/**
	 * Stores a new status value for a task.
	 * 
	 * @param bs
	 * @param taskIds
	 * @param status
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static String saveTaskStatus(AllModulesInjected bs, List<TaskId> taskIds, String status) throws GwtTeamingException {
		// Are we marking the tasks completed?
		boolean nowCompleted = (TaskInfo.STATUS_COMPLETED.equals(status));
		
		// Scan the tasks whose status is changing.
		FolderModule  fm      = bs.getFolderModule();
		ProfileModule pm      = bs.getProfileModule();
		SeenMap       seenMap = pm.getUserSeenMap(null);
		for (TaskId taskId:  taskIds) {
			Long binderId = taskId.getBinderId();
			Long entryId  = taskId.getEntryId();
			try {
				// Has the user seen this entry already?
				FolderEntry   fe       = fm.getEntry(binderId, entryId);
				boolean       taskSeen = seenMap.checkIfSeen(fe);
				
				// Construct the appropriate form data for the change...
				Map formData = new HashMap();
				formData.put(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {status});
				if (nowCompleted) {
					formData.put(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {TaskInfo.COMPLETED_100});
				}				
				else {
					// What state is the task changing to?
					boolean nowActive   = (TaskInfo.STATUS_NEEDS_ACTION.equals(status)) || (TaskInfo.STATUS_IN_PROCESS.equals(status));
					boolean nowCanceled =  TaskInfo.STATUS_CANCELED.equals(    status);
					
					// What state is the task changing from?
					String  currentStatus    = TaskHelper.getTaskStatusValue(   fe); if (null == currentStatus)    currentStatus    = "";
					String  currentCompleted = TaskHelper.getTaskCompletedValue(fe); if (null == currentCompleted) currentCompleted = "";					
					boolean wasComplete      = (TaskInfo.COMPLETED_100.equals(   currentCompleted) || (0 == currentCompleted.length()));
					boolean wasInactive      = (TaskInfo.STATUS_COMPLETED.equals(currentStatus   ) || TaskInfo.STATUS_CANCELED.equals(currentStatus));

					// If we need to force a status change in what's
					// being stored...
					String forcedStatus;
					if      (nowActive   && wasInactive && wasComplete) forcedStatus = TaskInfo.COMPLETED_90;
					else if (nowCanceled &&                wasComplete) forcedStatus = TaskInfo.COMPLETED_90;
					else                                                forcedStatus = null;
					if (null != forcedStatus) {
						// ...add the change to the form data.
						formData.put(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME, new String[] {forcedStatus});
					}
				}

				// ...and modify the entry.
				fm.modifyEntry(
					binderId,
					entryId, 
					new MapInputData(formData),
					null,
					null,
					null,
					null);
				
				// If the user saw the entry before we modified it...
				if (taskSeen) {
					// ...retain that seen state.
					pm.setSeen(null, fe);
				}
			}
			
			catch (Exception ex) {
				throw GwtServerHelper.getGwtTeamingException(ex);
			}
		}

		// If we're marking the entries completed...
		String reply;
		if (nowCompleted) {
			// ...return the current date/time stamp for the current
			// ...user's locale and time zone...
			User user = GwtServerHelper.getCurrentUser();			
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale());
			df.setTimeZone(user.getTimeZone());			
			reply = df.format(new Date());
		}
		else {
			// ...otherwise, return null.
			reply = null;
		}
		return reply;
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
	 * 
	 * Returns true if a title was stored and false otherwise.
	 */
	private static boolean setAITitle(AssignmentInfo ai, Map<Long, String> titleMap) {
		String title = titleMap.get(ai.getId());
		boolean reply = MiscUtil.hasString(title);
		if (reply) {
			ai.setTitle(title);
		}
		return reply;
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

	/*
	 * Stores a user's workspace ID of an AssignmentInfo based on a Map
	 * lookup using its ID.
	 */
	private static void setAIPresenceUserWSId(AssignmentInfo ai, Map<Long, Long> presenceUserWSIdsMap) {
		Long presenceUserWSId = presenceUserWSIdsMap.get(ai.getId());
		ai.setPresenceUserWSId(presenceUserWSId);
	}

	/*
	 * Sets the rights properties on a TaskBundle object.
	 */
	private static void setTaskBundleRights(HttpServletRequest request, AllModulesInjected bs, Folder taskFolder, TaskBundle tb) {
		// Set the rights associated with managing entries in the
		// folder.
		boolean hasAddEntryRights = bs.getFolderModule().testAccess(taskFolder, FolderOperation.addEntry);
		tb.setCanAddEntry(   hasAddEntryRights);
		tb.setCanModifyEntry(hasAddEntryRights);
		tb.setCanPurgeEntry( hasAddEntryRights);
		tb.setCanTrashEntry( hasAddEntryRights);

		// Scan the definitions that this folder may contain.
		@SuppressWarnings("unchecked")
		List<Definition> folderDefs = taskFolder.getEntryDefinitions();
		for (Definition folderDef:  folderDefs) {
			// Is this a task definition?
			String family = DefinitionUtils.getFamily(folderDef.getDefinition());
			if (ObjectKeys.FAMILY_TASK.equals(family)) {
				// Yes!  Mark that the folder can contain task
				// entries...
				tb.setCanContainTasks(true);

				// ...store the URL to use to create a new task...
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, taskFolder.getId().toString());
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, folderDef.getId());
				tb.setNewTaskUrl(adapterUrl.toString());

				// ...and break out of the loop.  Once we process the
				// ...task definition, we're done.
				break;
			}
		}
	}

	/**
	 * Updates the calculated dates on a given task.
	 * 
	 * Notes:
	 * 1) If the updating requires changes to this task or others, the
	 *    Map<Long, TaskDate> returned will contain a mapping between
	 *    the task IDs and the new calculated end date.  Otherwise, the
	 *    map returned will be empty.
	 * 2) If the entryId is null, ALL the tasks in the binder will have
	 *    their calculated dates updated.
	 *
	 * @param request
	 * @param bs
	 * @param binder
	 * @param entryId	May be null.
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Map<Long, TaskDate> updateCalculatedDates(HttpServletRequest request, AllModulesInjected bs, Binder binder, Long entryId) throws GwtTeamingException {
		// Read the TaskBundle.  We'll use this to find the tasks that
		// need to be updated.
		TaskBundle tb = getTaskBundleImpl(
			request,
			bs,
			binder,
			String.valueOf(FilterType.ALL   ),	// We need all the tasks from...
			String.valueOf(ModeType.PHYSICAL),	// ...the binder for the update.
			false);								// false -> Retrieve the TaskBundle to perform server updates.

		// Allocate a Map we can use to track the IDs and dates for
		// those entries whose calculated dates changed.
		Map<Long, TaskDate> reply = new HashMap<Long, TaskDate>();
		
		// Are we updating the calculated dates based on a specific
		// task? 
		if (null != entryId) {
			// Yes!  Update the dates of the list containing the task
			// and all of its subtasks.
			updateCalculatedDatesImpl(
				bs,
				tb,
				reply,
				TaskListItemHelper.findTaskList(
					tb,
					entryId));
		}
		
		else {
			// No, we're not updating a specific task!  Update
			// everything.
			updateCalculatedDatesImpl(
				bs,
				tb,
				reply,
				tb.getTasks());
		}
		
		// If we get here, reply refers to a Map<Long, TaskDate>
		// mapping the tasks whose calculated dates had to be updated
		// with the newly calculated end dates.  Return it. 
		return reply;
	}

	/*
	 * Updates the calculated dates of the tasks in a list and all
	 * their subtasks.
	 */
	private static void updateCalculatedDatesImpl(AllModulesInjected bs, TaskBundle tb, Map<Long, TaskDate> updates, List<TaskListItem> taskList) throws GwtTeamingException {
		// What task and list are we working from?
		boolean  rootTasks = (tb.getTasks() == taskList);
		TaskListItem parentTask;
		if (rootTasks)
		     parentTask = null;
		else parentTask = TaskListItemHelper.findTaskListItemContainingList(tb, taskList);
		
		// Scan the tasks in the list.
		TaskInfo prevTI = null;		
		for (TaskListItem task:  taskList) {
			// Does this task require some form of date calculations?
			TaskInfo  ti  = task.getTask();
			TaskEvent tiE = ti.getEvent();
			if (tiE.requiresDateCalculations()) {
				// Yes!  Extract the additional information we may need
				// for the calculations from the task...
				TaskDuration tiD            = tiE.getDuration();
				int          durDays        = tiD.getDays();
				ServerDates  tiSD           = ((ServerDates) tiE.getServerData());
				boolean      hasActualStart = tiSD.hasActualStart();
				boolean      hasActualEnd   = tiSD.hasActualEnd();

				// ...and define a few other local variables we'll
				// ...need for the analysis.
				boolean removeCalcStart = false;
				boolean removeCalcEnd   = false;
				Date    newCalcStart    = null;
				Date    newCalcEnd      = null;
				
				// What combination of actual start/end dates were
				// specified for this task?
				if (hasActualStart && hasActualEnd) {
					// Has start and end!  No calculated dates.
					removeCalcStart =
					removeCalcEnd   = true;
				}
				
				else if (hasActualStart && (!hasActualEnd)) {
					// Has start, no end!  Calculate an end.
					if (EventHelper.debugEnabled()) {
						EventHelper.debugLog("GwtTaskHelper.updateCalculatedDatesImpl( 1:Adjusting Start ):  " + ti.getTitle());
					}
					newCalcEnd      = EventHelper.adjustDate(tiSD.getActualStart().getDate(), durDays);
					removeCalcStart = true;
				}
				
				else if ((!hasActualStart) && hasActualEnd) {
					// No start, has end!  Calculate a start.
					if (EventHelper.debugEnabled()) {
						EventHelper.debugLog("GwtTaskHelper.updateCalculatedDatesImpl( 2:Adjusting End ):  " + ti.getTitle());
					}
					newCalcStart  = EventHelper.adjustDate(tiSD.getActualEnd().getDate(), (-durDays));
					removeCalcEnd = true;
				}
				
				else {
					// No start or end!  If we're working on the root
					// task list in the binder...
					if (rootTasks) {
						// ...we don't try to supply either calculated
						// ...date.
						removeCalcStart =
						removeCalcEnd   = true;
					}
					
					else {
						// No, this isn't the root task list in the
						// binder!  Is it the first task in the list?
						if (null == prevTI) {
							// Yes!  Use the parent task's start date
							// as its start.
							TaskDate parentStart = ((null == parentTask)  ? null : parentTask.getTask().getEvent().getLogicalStart());
							newCalcStart         = ((null == parentStart) ? null : parentStart.getDate());
						}
						
						else {
							// No, it isn't the first task in the list!
							// Use the previous task's end date as its
							// start.
							newCalcStart = prevTI.getEvent().getLogicalEnd().getDate();
						}

						// If we don't have its start...
						if (null == newCalcStart) {
							// ...remove any existing calculated dates...
							removeCalcStart =
							removeCalcEnd   = true;
						}
						else {
							// ...otherwise, use its start to calculate
							// ...its end.
							if (EventHelper.debugEnabled()) {
								EventHelper.debugLog("GwtTaskHelper.updateCalculatedDatesImpl( 3:Adjusting End ):  " + ti.getTitle());
							}
							newCalcEnd = EventHelper.adjustDate(newCalcStart, durDays);
						}
					}
				}

				// If we have a calculated start that didn't change...
				if ((null != newCalcStart) && tiSD.hasCalcStart()) {
					long newTime = newCalcStart.getTime();
					long oldTime = tiSD.getCalcStart().getDate().getTime();
					if (newTime == oldTime) {
						// ...don't save it.
						newCalcStart = null;
					}
				}
				
				// If we have calculated end that didn't change...
				if ((null != newCalcEnd) && tiSD.hasCalcEnd()) {
					long newTime = newCalcEnd.getTime();
					long oldTime = tiSD.getCalcEnd().getDate().getTime();
					if (newTime == oldTime) {
						// ...don't save it.
						newCalcEnd = null;
					}
				}

				// Do we have changes to make to the calculated start
				// or end dates for this task?
				if (removeCalcStart || (null != newCalcStart) ||
					removeCalcEnd   || (null != newCalcEnd)) {
					// Yes!  Can we save the changes?
					boolean saved = updateCalculatedDatesOnTask(
						bs,
						ti,
						removeCalcStart, newCalcStart,
						removeCalcEnd,   newCalcEnd);
					
					if (saved) {
						// Yes!  If we changed the calculated start...
						if (removeCalcStart || (null != newCalcStart)) {
							// ...update it in the task.
							TaskDate calcTD = new TaskDate();
							if (!removeCalcStart) {
								calcTD.setDate(newCalcStart);
								calcTD.setDateDisplay(EventHelper.getDateTimeString(newCalcStart, tiE.getAllDayEvent()));
							}
							tiE.setLogicalStart(calcTD);
						}

						// If we changed the calculated end...
						if (removeCalcEnd || (null != newCalcEnd)) {
							// ...update it in the task...
							TaskDate calcTD = new TaskDate();
							if (!removeCalcEnd) {
								calcTD.setDate(                                     newCalcEnd                       );
								calcTD.setDateDisplay(EventHelper.getDateTimeString(newCalcEnd, tiE.getAllDayEvent()));
							}
							tiE.setLogicalEnd(calcTD);
							
							// ...and track the fact that we changed
							// ...this task's calculated end date.
							updates.put(ti.getTaskId().getEntryId(), calcTD);
						}
					}
				}
			}
			
			// Update this tasks's subtasks.
			updateCalculatedDatesImpl(
				bs,
				tb,
				updates,
				task.getSubtasks());

			// As we step through the task list, we may need to look
			// back at the previous task to calculate the next one.
			// Keep track of it.
			prevTI = ti;
		}

		// Did we just process a non-empty subtask list for some parent
		// task?
		if ((null != parentTask) && (!(taskList.isEmpty()))) {
			// Yes!  What do we know about the parent task?
			TaskInfo     pTask       = parentTask.getTask();
			TaskEvent    pEvent      = pTask.getEvent();
			boolean      pHasDurDays = pEvent.getDuration().hasDaysOnly();
			ServerDates  pDates      = ((ServerDates) pEvent.getServerData());
			boolean      pHasStart   = hasDateValue(pDates.getActualStart());
			boolean      pHasEnd     = hasDateValue(pDates.getActualEnd());			


			// Does the parent task need its calculated start updated
			// from its subtasks?
			TaskDate pNewCalcStart;
			if ((!pHasStart) && ((!pHasEnd) || (!pHasDurDays))) {
				// No start and no end or duration!  It needs a start!
				// (If it had an end and duration, that would have been
				// used to calculated the start previously.)  Find the
				// earliest logical start among all the its subtasks.
				pNewCalcStart = findEarliestLogicalStart(taskList);
			}
			else {
				// We don't need a start from the subtasks.
				pNewCalcStart = null;
			}
			
			// Does the parent task need its calculated end updated
			// from its subtasks?
			TaskDate pNewCalcEnd;
			if ((!pHasEnd) && ((!pHasStart) || (!pHasDurDays))) {
				// No end and no start or duration!  It needs an end!
				// (If it had a start and duration, that would have
				// been used to calculated the end previously.)  Find
				// the latest logical end among all its subtasks.
				pNewCalcEnd = findLatestLogicalEnd(taskList);
			}
			else {
				// We don't need an end from the subtasks.
				pNewCalcEnd = null;
			}
			
			// Do we have new calculated start and/or end for the
			// parent task?
			if ((null != pNewCalcStart) || (null != pNewCalcEnd)) {
				// Yes!  Can we save the changes?
				boolean saved = updateCalculatedDatesOnTask(
					bs,
					pTask,
					false, ((null == pNewCalcStart) ? null : pNewCalcStart.getDate()),
					false, ((null == pNewCalcEnd)   ? null : pNewCalcEnd.getDate()));

				if (saved) {
					// Yes!  Put them into effect on the parent
					// task.
					if (null != pNewCalcStart) {
						pEvent.setLogicalStart(pNewCalcStart);
					}
					
					if (null != pNewCalcEnd) {
						pEvent.setLogicalEnd(  pNewCalcEnd  );
						updates.put(pTask.getTaskId().getEntryId(), pNewCalcEnd);
					}
				}
			}
		}
	}

	/*
	 * Updates the calculated start and/or end dates on a task.
	 * 
	 * Returns true if the update was performed and false otherwise.
	 */
	@SuppressWarnings("unchecked")
	private static boolean updateCalculatedDatesOnTask(AllModulesInjected bs, TaskInfo ti, boolean removeCalcStart, Date calcStart, boolean removeCalcEnd, Date calcEnd) throws GwtTeamingException {		
		// Do we have rights to modify this task?
		boolean reply = false;
		if (ti.getCanModify()) {		
			try {
				// Yes!  Read the existing Event data from it...
				Long            binderId        = ti.getTaskId().getBinderId();
				Long            entryId         = ti.getTaskId().getEntryId();
				FolderModule    fm              = bs.getFolderModule();
				FolderEntry     fe              = fm.getEntry(binderId, entryId);
				CustomAttribute customAttribute = fe.getCustomAttribute(TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME);
				Event           event           = ((Event) customAttribute.getValue());
				
				// ...modify the event with the new calculated
				// ...start/end dates, as appropriate...
				Calendar eventCalcStart = event.getDtCalcStart();
				Calendar eventCalcEnd   = event.getDtCalcEnd();
				boolean  modifyEvent    = false;
				if (removeCalcStart) {
					if (null != eventCalcStart) {
						event.setDtCalcStart((Calendar) null);
						modifyEvent = true;
					}
				}
				else if (null != calcStart) {
					if ((null == eventCalcStart) || (eventCalcStart.getTime().getTime() != calcStart.getTime())) {
						Calendar cal = new GregorianCalendar();
						cal.setTime(calcStart);
						event.setDtCalcStart(cal);
						modifyEvent = true;
					}
				}
				
				if (removeCalcEnd) {
					if (null != eventCalcEnd) {
						event.setDtCalcEnd((Calendar) null);
						modifyEvent = true;
					}
				}
				else if (null != calcEnd) {
					if ((null == eventCalcEnd) || (eventCalcEnd.getTime().getTime() != calcEnd.getTime())) {
						Calendar cal = new GregorianCalendar();
						cal.setTime(calcEnd);
						event.setDtCalcEnd(cal);
						modifyEvent = true;
					}
				}

				// ...and if we really need to change anything...
				if (modifyEvent) {
					// ...check whether the user has seen this entry
					// ...already...
					ProfileModule pm = bs.getProfileModule();
					boolean taskSeen = pm.getUserSeenMap(null).checkIfSeen(fe);
					
					// ...and save the modified Event.
					Map formData = new HashMap(); 
					formData.put(TaskHelper.TIME_PERIOD_TASK_ENTRY_ATTRIBUTE_NAME, event);
					formData.put(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME,      ti.getStatus());	// We pass the status along so that we don't adversely affect any completion date.
					fm.modifyEntry(binderId, entryId, new MapInputData(formData), null, null, null, null);
					reply = true;
					
					// If the user saw the entry before we modified it...
					if (taskSeen) {
						// ...retain that seen state.
						pm.setSeen(null, fe);
					}
				}
			}
			
			catch (Exception ex) {
				m_logger.debug("GwtTaskHelper.updateCalculatedDatesOnTask( Update failed on:  '" + ti.getTitle() + "' )");
				m_logger.debug("GwtTaskHelper.updateCalculatedDatesOnTask( EXCEPTION ):  ", ex);
			}
		}
		
		else {
			// No, the user doesn't have rights to modify the task.
			m_logger.debug("GwtTaskHelper.updateCalculatedDatesOnTask( Update failed on:  '" + ti.getTitle() + "' )");
			m_logger.debug("GwtTaskHelper.updateCalculatedDatesOnTask( Insufficient Rights )");
		}
		
		// If we get here, reply is true if we modified task's
		// calculated start and or end and false otherwise.  Return it.
		return reply;
	}

	/*
	 * Validates that the Long's in a Set<Long> are valid principal
	 * IDs.
	 */
	@SuppressWarnings("unchecked")
	private static Set<Long> validatePrincipalIds(Set<Long> principalIds) {
		Set<Long> reply = new HashSet<Long>();
		if ((null != principalIds) && (!(principalIds.isEmpty()))) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(principalIds);}
			catch (Exception ex) {/* Ignored. */}
			if ((null != principals) && (!(principals.isEmpty()))) {
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					reply.add(p.getId());
				}
			}
		}
		return reply;
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
