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
package org.kablink.teaming.gwt.server.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
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
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.TaskDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TaskEventRpcResponseData;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskStats;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.gwt.client.util.TaskLinkage.TaskLink;
import org.kablink.teaming.gwt.client.util.TaskListItem;
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
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
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
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.util.cal.Duration;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

import com.sitescape.team.domain.Statistics;

/**
 * Helper methods for the GWT UI server code that services task
 * requests.
 *
 * @author drfoster@novell.com
 */
public class GwtTaskHelper {
	protected static Log m_logger = LogFactory.getLog(GwtTaskHelper.class);

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
	 * Inhibits this class from being instantiated. 
	 */
	private GwtTaskHelper() {
		// Nothing to do.
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
			isFiltered = (null != BinderHelper.getSearchFilter(bs, binder, userFolderProperties, true));
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
				Date tdDate = date.getDate();
				dateS = ((null == tdDate) ? "null" : df.format(tdDate));
			}
		}
		
		return (label + ": " + dateS);
	}
	
	/*
	 * Generates a String to write to the log for a TaskEvent.
	 */
	private static String buildDumpString(String label, TaskEvent event) {
		StringBuffer buf = new StringBuffer(label);
		
		buf.append(               buildDumpString("Actual Start",    event.getActualStart()));
		buf.append(               buildDumpString(", Actual End",    event.getActualEnd()));
		buf.append(               buildDumpString(", Logical Start", event.getLogicalStart()));
		buf.append(               buildDumpString(", Logical End",   event.getLogicalEnd()));
		buf.append(GwtEventHelper.buildDumpString(", All Day",       event.getAllDayEvent()));
		
		TaskDuration taskDuration = event.getDuration();
		buf.append(GwtEventHelper.buildDumpString(", Duration:S", taskDuration.getSeconds()));
		buf.append(GwtEventHelper.buildDumpString(", M",          taskDuration.getMinutes()));
		buf.append(GwtEventHelper.buildDumpString(", H",          taskDuration.getHours()));
		buf.append(GwtEventHelper.buildDumpString(", D",          taskDuration.getDays()));
		buf.append(GwtEventHelper.buildDumpString(", W",          taskDuration.getWeeks()));
		buf.append(GwtEventHelper.buildDumpString(", Interval",   taskDuration.getInterval()));
		
		return buf.toString();
	}
	
	/*
	 * Generates a search index field name reference for an event's
	 * duration field.
	 */
	private static String buildDurationFieldName(String fieldName) {
		return (GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_DURATION) + BasicIndexUtils.DELIMITER + fieldName);
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
			throw GwtLogHelper.getGwtClientException(ex);
		}
	}
	
	/*
	 * When initially build, the AssignmentInfo's in the List<TaskInfo>
	 * only contain the assignee IDs.  We need to complete them with
	 * each assignee's title.
	 */
	private static void completeAIs(AllModulesInjected bs, HttpServletRequest request, List<TaskInfo> tasks) {
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
				ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			
			// Scan this TaskInfo's group assignees...
			for (AssignmentInfo ai:  ti.getAssignmentGroups()) {
				// ...tracking each unique ID.
				ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
			}
			
			// Scan this TaskInfo's team assignees...
			for (AssignmentInfo ai:  ti.getAssignmentTeams()) {
				// ...tracking each unique ID.
				ListUtil.addLongToListLongIfUnique(teamIds, ai.getId());
			}
		}

		// If we don't have any assignees to complete...
		boolean hasPrincipals = (!(principalIds.isEmpty()));
		boolean hasTeams      = (!(teamIds.isEmpty()));		
		if ((!hasPrincipals) && (!hasTeams)) {
			// ...bail.
			return;
		}
		
		// Construct Maps, mapping the IDs to their titles, membership
		// counts, ...
		Map<Long, String>			avatarUrls        = new HashMap<Long, String>();
		Map<Long, String>			principalEMAs     = new HashMap<Long, String>();
		Map<Long, String>			principalTitles   = new HashMap<Long, String>();
		Map<Long, Integer>			groupCounts       = new HashMap<Long, Integer>();
		Map<Long, GwtPresenceInfo>	userPresence      = new HashMap<Long, GwtPresenceInfo>();
		Map<Long, Boolean>			userExternal      = new HashMap<Long, Boolean>();
		Map<Long, Long>				presenceUserWSIds = new HashMap<Long, Long>();
		Map<Long, String>			teamTitles        = new HashMap<Long, String>();
		Map<Long, Integer>			teamCounts        = new HashMap<Long, Integer>();
		GwtEventHelper.readEventStuffFromDB(
			// Uses these...
			bs,
			request,
			principalIds,
			teamIds,

			// ...to complete these.
			principalEMAs,
			principalTitles,
			groupCounts,
			userPresence,
			userExternal,
			presenceUserWSIds,
			
			teamTitles,
			teamCounts,
			
			avatarUrls);
		
		// Scan the List<TaskInfo> again.
		for (TaskInfo ti:  tasks) {
			// The removeList is used to handle cases where an ID could
			// not be resolved (e.g., an 'Assigned To' user has been
			// deleted.)
			List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
			
			// Scan this TaskInfo's individual assignees again...
			for (AssignmentInfo ai:  ti.getAssignments()) {
				// ...setting each one's title.
				if (GwtEventHelper.setAssignmentInfoTitle(           ai, principalTitles )) {
					GwtEventHelper.setAssignmentInfoEmailAddress(    ai, principalEMAs    );
					GwtEventHelper.setAssignmentInfoPresence(        ai, userPresence     );
					GwtEventHelper.setAssignmentInfoExternal(        ai, userExternal     );
					GwtEventHelper.setAssignmentInfoPresenceUserWSId(ai, presenceUserWSIds);
					GwtEventHelper.setAssignmentInfoAvatarUrl(       ai, avatarUrls       );
				}
				else {
					removeList.add(ai);
				}
			}
			GwtServerHelper.removeUnresolvedAssignees(ti.getAssignments(), removeList);
			
			// Scan this TaskInfo's group assignees again...
			for (AssignmentInfo ai:  ti.getAssignmentGroups()) {
				// ...setting each one's title and membership count.
				if (GwtEventHelper.setAssignmentInfoTitle(  ai, principalTitles)) {
					GwtEventHelper.setAssignmentInfoMembers(ai, groupCounts     );
					ai.setPresenceDude("pics/group_20.png");
				}
				else {
					removeList.add(ai);
				}
			}
			GwtServerHelper.removeUnresolvedAssignees(ti.getAssignmentGroups(), removeList);
			
			// Scan this TaskInfo's team assignees again...
			for (AssignmentInfo ai:  ti.getAssignmentTeams()) {
				// ...setting each one's title and membership count.
				if (GwtEventHelper.setAssignmentInfoTitle(  ai, teamTitles)) {
					GwtEventHelper.setAssignmentInfoMembers(ai, teamCounts );
					ai.setPresenceDude("pics/team_16.png");
				}
				else {
					removeList.add(ai);
				}
			}
			GwtServerHelper.removeUnresolvedAssignees(ti.getAssignmentTeams(), removeList);
		}		

		// Finally, one last scan through the List<TaskInfo>...
		Comparator<AssignmentInfo> comparator = new GwtServerHelper.AssignmentInfoComparator(true);
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
			ListUtil.addLongToListLongIfUnique(binderIds, ti.getTaskId().getBinderId());
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
		Map<Long, String>          principalEMAs     = new HashMap<Long, String>();
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
						groupCounts.put(pId, GwtServerHelper.getGroupCount(group));						
					}
					else if (p instanceof UserPrincipal) {
						User user = ((User) p);
						users.put(pId, user);
						presenceUserWSIds.put(pId, user.getWorkspaceId());
						if (isPresenceEnabled) {
							presenceInfo.put(pId, GwtServerHelper.getPresenceInfo(user));
						}
						String ema = user.getEmailAddress();
						if (MiscUtil.hasString(ema)) {
							principalEMAs.put(pId, ema);
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
				GwtEventHelper.setAssignmentInfoTitle(           ai, principalTitles  );
				GwtEventHelper.setAssignmentInfoEmailAddress(    ai, principalEMAs    );
				GwtEventHelper.setAssignmentInfoPresence(        ai, presenceInfo     );
				GwtEventHelper.setAssignmentInfoPresenceUserWSId(ai, presenceUserWSIds);
			}
			
			// No, this assignment isn't that of a user!  Is it that
			// of a group?
			else if (null != groups.get(aiId)) {
				// Yes!  Set its title and membership count.
				GwtEventHelper.setAssignmentInfoTitle(  ai, principalTitles);
				GwtEventHelper.setAssignmentInfoMembers(ai, groupCounts    );
				ai.setPresenceDude("pics/group_20.png");
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
		Collections.sort(assignees, new GwtServerHelper.AssignmentInfoComparator(true));
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

		// Collect the entity IDs of the tasks from the List<TaskInfo>.
		List<Long> entryIds = new ArrayList<Long>();
		for (TaskInfo ti:  tasks) {
			entryIds.add(ti.getTaskId().getEntityId());
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
				FolderEntry task = taskEntryMap.get(ti.getTaskId().getEntityId());
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
	public static ErrorListRpcResponseData deleteTasks(HttpServletRequest request, AllModulesInjected bs, List<EntityId> taskIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// If we get here, we have rights to delete all the tasks
			// that we were given.  Scan them...
			List<Long> binderIds = new ArrayList<Long>();  
			for (EntityId taskId:  taskIds) {
				// ...deleting each...
				Long binderId = taskId.getBinderId();
				ListUtil.addLongToListLongIfUnique(binderIds, binderId);
				try {
					TrashHelper.preDeleteEntry(bs, taskId.getBinderId(), taskId.getEntityId());
				}

				catch (Exception e) {
					// ...tracking any that we couldn't delete.
					String entryTitle = GwtServerHelper.getEntityTitle(bs, taskId);
					String messageKey;
					if      (e instanceof AccessControlException) messageKey = "deleteEntryError.AccssControlException";
					else                                          messageKey = "deleteEntryError.OtherException";
					reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
				}
			}

			// Scan the IDs of binders that we're deleting from...
			for (Long binderId:  binderIds) {
				// ...and tell each to update the calculated dates they
				// ...contain.
				updateCalculatedDates(
					request,
					bs,
					getTaskBinder(bs, null, binderId),
					null);	// null -> Update the calculated dates for all the tasks in the binder.
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
		}
	}
	
	/*
	 * Logs the contents of a List<TaskInfo>.
	 */
	private static void dumpTaskInfoList(List<TaskInfo> tasks) {		
		// If debug logging is disabled...
		if (!(GwtLogHelper.isDebugEnabled(m_logger))) {
			// ...bail.
			return;
		}

		// If there are no TaskInfo's in the list...
		if (tasks.isEmpty()) {
			// ...log that fact and bail.
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.dumpTaskInfoList( EMPTY )");
			return;
		}

		// Dump the tasks.
		GwtLogHelper.debug(m_logger, "GwtTaskHelper.dumpTaskInfoList( START: " + String.valueOf(tasks.size()) + " )");
		for (TaskInfo ti:  tasks) {
			StringBuffer buf = new StringBuffer("");
			buf.append(GwtEventHelper.buildDumpString("\n\tTask ID",            ti.getTaskId().getEntityId()   ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tBinder ID",        ti.getTaskId().getBinderId()   ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tCanModify",        ti.getCanModify()              ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tCanPurge",         ti.getCanPurge()               ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tCanTrash",         ti.getCanTrash()               ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tSeen",             ti.getSeen()                   ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tAssignments",      ti.getAssignments(),      false));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tAssignmentGroups", ti.getAssignmentGroups(), true ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tAssignmentTeams",  ti.getAssignmentTeams(),  true ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tLocation",         ti.getLocation()               ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tCompleted",        ti.getCompleted()              ));
			buf.append(               buildDumpString("\n\t\t\tDate",           ti.getCompletedDate()          ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tEntityType",       ti.getEntityType()             ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tPriority",         ti.getPriority()               ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tTitle",            ti.getTitle()                  ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tStatus",           ti.getStatus()                 ));
			buf.append(GwtEventHelper.buildDumpString("\n\t\tOverdue",          ti.getOverdue()                ));
			buf.append(               buildDumpString("\n\t\tEvent: ",          ti.getEvent()                  ));
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.dumpTaskInfoList()" + buf.toString());
		}
		GwtLogHelper.debug(m_logger, "GwtTaskHelper.dumpTaskInfoList( END )");
	}

	/*
	 * Logs the contents of a TaskLinkage object.
	 */
	private static void dumpTaskLinkage(TaskLinkage linkage) {
		// If debug logging is disabled...
		if (!(GwtLogHelper.isDebugEnabled(m_logger))) {
			// ...bail.
			return;
		}

		// If there are no TaskLink's in the task order list...
		List<TaskLink> taskOrder = linkage.getTaskOrder();
		if (taskOrder.isEmpty()) {
			// ...log that fact and bail.
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.dumpTaskLinkage( EMPTY )");
			return;
		}

		// Scan the TaskLink's in the task order list...
		GwtLogHelper.debug(m_logger, "GwtTaskHelper.dumpTaskLinkage( START: " + String.valueOf(taskOrder.size()) + " )");
		for (TaskLink tl:  taskOrder) {
			// ...logging each of them.
			dumpTaskLink(tl, 0);
		}
		GwtLogHelper.debug(m_logger, "GwtTaskHelper.dumpTaskLinkage( END )");
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
		GwtLogHelper.debug(m_logger, logBuffer.toString());
		
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
			throw GwtLogHelper.getGwtClientException(ex);
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
			if (task.getTaskId().getEntityId().equals(entryId)) {
				// Yes!  Return it.
				return task;
			}
		}
		
		// If we get here, we couldn't find the task in the
		// List<TaskInfo>.  Return null.
		return null;
	}

	/*
	 * Returns the Calendar equivalent of a Date.
	 */
	private static Calendar getCalFromDate(Date d) {
		Calendar reply;
		if (null == d) {
			reply = null;
		}
		else {
			reply = new GregorianCalendar();
			reply.setTime(d);
		}
		return reply;
	}
	
	/*
	 * Returns the Calendar equivalent of a TaskDate.
	 */
	private static Calendar getCalFromDate(TaskDate td) {
		Calendar reply;
		if (null == td)
		     reply = null;
		else reply = getCalFromDate(td.getDate());
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
	 * Reads an event from a map.
	 */
	@SuppressWarnings("unchecked")
	private static TaskEvent getTaskEventFromEntryMap(Map m, boolean clientBundle) {
		TaskEvent reply = new TaskEvent();

		// Extract the event's 'All Day Event' flag from the Map.
		String tz = GwtEventHelper.getStringFromEntryMapRaw(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_TIME_ZONE_ID));
		boolean allDayEvent = (!(MiscUtil.hasString(tz)));
		reply.setAllDayEvent(allDayEvent);

		// Are we reading the information for the client?
		TaskDate calcEnd = getTaskDateFromEntryMap(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_CALC_END_DATE), allDayEvent, true);
		if (!clientBundle) {
			// No!  Extract the event's actual and calculated start and
			// end dates.
			ServerDates sd = new ServerDates();
			sd.setActualStart(getTaskDateFromEntryMap(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_START_DATE     ), allDayEvent, true));
			sd.setActualEnd(  getTaskDateFromEntryMap(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_END_DATE       ), allDayEvent, true));
			sd.setCalcStart(  getTaskDateFromEntryMap(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_CALC_START_DATE), allDayEvent, true));
			sd.setCalcEnd(    calcEnd                                                                                );
			reply.setServerData(sd);
		}
		
		// Extract the event's actual start and end...
		reply.setActualStart(getTaskDateFromEntryMap(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_START_DATE), allDayEvent, false ));		
		reply.setActualEnd(  getTaskDateFromEntryMap(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_END_DATE  ), allDayEvent, false ));
		
		// ...extract the event's logical start and end...
		reply.setLogicalStart(getTaskDateFromEntryMap(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_LOGICAL_START_DATE), allDayEvent, false ));		
		reply.setLogicalEnd(  getTaskDateFromEntryMap(m, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_LOGICAL_END_DATE  ), allDayEvent, false ));
		
		// ...pass through an indicator of whether it's using a
		// ...calculated end date...
		reply.setEndIsCalculated(null != calcEnd);

		// ...and extract the event's duration fields from the Map.
		TaskDuration taskDuration = new TaskDuration();
		taskDuration.setSeconds(GwtEventHelper.getIntFromEntryMap(m, buildDurationFieldName(Constants.DURATION_FIELD_SECONDS)));
		taskDuration.setMinutes(GwtEventHelper.getIntFromEntryMap(m, buildDurationFieldName(Constants.DURATION_FIELD_MINUTES)));
		taskDuration.setHours(  GwtEventHelper.getIntFromEntryMap(m, buildDurationFieldName(Constants.DURATION_FIELD_HOURS  )));
		taskDuration.setDays(   GwtEventHelper.getIntFromEntryMap(m, buildDurationFieldName(Constants.DURATION_FIELD_DAYS   )));
		taskDuration.setWeeks(  GwtEventHelper.getIntFromEntryMap(m, buildDurationFieldName(Constants.DURATION_FIELD_WEEKS  )));
		reply.setDuration(taskDuration);

		// If we get here, reply refers to the TaskEvent object
		// constructed from the information in the Map.  Return it.
		return reply;
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
						Set<Long> groupMemberIds = GwtServerHelper.getGroupMemberIds((GroupPrincipal) p);
						if (null != groupMemberIds) {
							// Yes!  Add a base AssignmentInfo with
							// each ID to the List<AssignmentInfo> that
							// we're going to return.
							for (Long memberId:  groupMemberIds) {
								reply.add(AssignmentInfo.construct(memberId, AssigneeType.GROUP));
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
			throw GwtLogHelper.getGwtClientException(ex);
		}
	}

	/**
	 * Return a list of all the tasks assigned to the logged-in user.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<TaskInfo> getMyTasks(AllModulesInjected ami, HttpServletRequest request) {
		boolean clientBundle;
		Map options = new HashMap();
		Integer searchUserOffset = 0;
		Integer searchLuceneOffset = 0;
		Integer maxHits;
		Integer summaryWords;
		Integer intInternalNumberOfRecordsToBeFetched;
		int offset;
		int maxResults;
		User user;
		List groups;
		List groupsS;
		List teams;
		ProfileDao profileDao;
		Iterator itG;
		Iterator teamMembershipsIt;
		Criteria crit; 
		Map results;

		clientBundle = true;
		
		options = new HashMap();
		options.put( ObjectKeys.SEARCH_PAGE_ENTRIES_PER_PAGE, new Integer( ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES ) );
		
		options.put( ObjectKeys.SEARCH_OFFSET, searchLuceneOffset );
		options.put( ObjectKeys.SEARCH_USER_OFFSET, searchUserOffset );
		
		maxHits = new Integer( ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES );
		options.put( ObjectKeys.SEARCH_USER_MAX_HITS, maxHits );
		
		summaryWords = new Integer( 20 );
		options.put( WebKeys.SEARCH_FORM_SUMMARY_WORDS, summaryWords );
		
		intInternalNumberOfRecordsToBeFetched = searchLuceneOffset + maxHits;
		if ( searchUserOffset > 0 )
		{
			intInternalNumberOfRecordsToBeFetched += searchUserOffset;
		}
		options.put( ObjectKeys.SEARCH_MAX_HITS, intInternalNumberOfRecordsToBeFetched );

		offset = ((Integer) options.get( ObjectKeys.SEARCH_OFFSET )).intValue();
		maxResults = ((Integer) options.get( ObjectKeys.SEARCH_MAX_HITS )).intValue();
		
		user = GwtServerHelper.getCurrentUser();

		groups = new ArrayList();
		groupsS = new ArrayList();
		teams = new ArrayList();
		
		profileDao = (ProfileDao)SpringContextUtil.getBean( "profileDao" );
		groups.addAll( profileDao.getApplicationLevelGroupMembership( user.getId(), user.getZoneId() ) );
		
		itG = groups.iterator();
		while ( itG.hasNext() )
		{
			groupsS.add( itG.next().toString() );
		}
		
		teamMembershipsIt = ami.getBinderModule().getTeamMemberships( user.getId(), org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD) ).iterator();
		while ( teamMembershipsIt.hasNext() )
		{
			teams.add( ((Map)teamMembershipsIt.next()).get( Constants.DOCID_FIELD ) );
		}
		
		crit = SearchUtils.tasksForUser( user.getId(), 
										 (String[])groupsS.toArray(new String[groupsS.size()]), 
										 (String[])teams.toArray(new String[teams.size()]) );
		results = ami.getBinderModule().executeSearchQuery( crit, Constants.SEARCH_MODE_NORMAL, offset, maxResults, null );

		// Create a TaskInfo object for every task we found
		{
	    	List<Map> taskEntriesList;
			List<TaskInfo> reply;
			SeenMap seenMap;

	    	taskEntriesList = ((List) results.get( ObjectKeys.SEARCH_ENTRIES ) );

			// Did we find any entries?
			reply = new ArrayList<TaskInfo>();
			if ( taskEntriesList == null || taskEntriesList.isEmpty() )
			{
				// No!  Bail.
				return reply;
			}
			
			// Scan the task entries that we read.
			seenMap = ami.getProfileModule().getUserSeenMap(null);
			for (Map taskEntry:  taskEntriesList) {			
				TaskInfo ti = new TaskInfo();
				
				ti.setOverdue(         GwtEventHelper.getOverdueFromEntryMap(           taskEntry, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_LOGICAL_END_DATE) ));
				ti.setEvent(                          getTaskEventFromEntryMap(         taskEntry, clientBundle                                                                   ));
				ti.setStatus(          GwtEventHelper.getStringFromEntryMapRaw(         taskEntry, TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME                                    ));
				ti.setCompleted(       GwtEventHelper.getStringFromEntryMapRaw(         taskEntry, TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME                                 ));
				ti.setSeen(                   seenMap.checkIfSeen(                      taskEntry                                                                                 ));
				ti.setEntityType(      GwtEventHelper.getStringFromEntryMapRaw(         taskEntry, Constants.ENTITY_FIELD                                                         ));
				ti.setPriority(        GwtEventHelper.getStringFromEntryMapRaw(         taskEntry, TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME                                  ));
				ti.setAssignments(     GwtEventHelper.getAssignmentInfoListFromEntryMap(taskEntry, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME,        AssigneeType.INDIVIDUAL));
				ti.setAssignmentGroups(GwtEventHelper.getAssignmentInfoListFromEntryMap(taskEntry, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME, AssigneeType.GROUP     ));
				ti.setAssignmentTeams( GwtEventHelper.getAssignmentInfoListFromEntryMap(taskEntry, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME,  AssigneeType.TEAM      ));
				
				String title = GwtEventHelper.getStringFromEntryMapRaw(taskEntry, Constants.TITLE_FIELD);
				if (!(MiscUtil.hasString(title))) {
					title = ("--" + NLT.get("entry.noTitle") + "--");
				}
				ti.setTitle(title);
				
				String desc = GwtEventHelper.getStringFromEntryMapRaw(taskEntry, Constants.DESC_FIELD);
				ti.setDesc(desc);
				
				EntityId taskId = new EntityId(
					GwtEventHelper.getLongFromEntryMap(taskEntry, Constants.BINDER_ID_FIELD),
					GwtEventHelper.getLongFromEntryMap(taskEntry, Constants.DOCID_FIELD    ),
					EntityId.FOLDER_ENTRY);
				ti.setTaskId(taskId);

				ti.setCompletedDate(
					getTaskDateFromEntryMap(
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
			completeTaskRights(ami, reply);
			if (clientBundle) {
				completeBinderLocations(ami, reply);
				completeAIs(ami, request, reply);
			}
			
			return reply;
		}
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
	public static Binder getTaskBinder(AllModulesInjected bs, String zoneUUID, Long binderId) throws GwtTeamingException {
		Binder reply = null;
		try {
			ZoneInfo zoneInfo;
			String zoneInfoId;

			// Get the id of the zone we are running in.
			zoneInfo = MiscUtil.getCurrentZone();
			zoneInfoId = zoneInfo.getId();
			if ( zoneInfoId == null )
				zoneInfoId = "";

			// Are we looking for a folder that was imported from another zone?
			if ( zoneUUID != null && zoneUUID.length() > 0 && !zoneInfoId.equals( zoneUUID ) )
			{
				// Yes, get the binder id for the binder in this zone.
				binderId = bs.getBinderModule().getZoneBinderId( binderId, zoneUUID, EntityType.folder.name() );
			}

			reply = bs.getBinderModule().getBinder(binderId);
		}
		
		catch (Exception ex) {
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					ex,
					"GwtTaskHelper.getTaskBinder( " + String.valueOf(binderId) + ": EXCEPTION ): ");
		}

		if (null == reply) {
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					null,
					"GwtTaskHelper.getTaskBinder( " + String.valueOf(binderId) + ": Could not access binder. )");
		}
		
		return reply; 
	}
	
	/**
	 * Reads the task information from the specified binder.
	 * 
	 * @param request
	 * @param bs
	 * @param applyUsersFilter
	 * @param embeddedInJSP
	 * @param binder
	 * @param filterTypeParam
	 * @param modeTypeParam
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static List<TaskListItem> getTaskList(HttpServletRequest request, AllModulesInjected bs, boolean applyUsersFilter, boolean embeddedInJSP, Binder binder, String filterTypeParam, String modeTypeParam) throws GwtTeamingException {
		TaskBundle tb = getTaskBundle(request, bs, applyUsersFilter, embeddedInJSP, binder, filterTypeParam, modeTypeParam);
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
	public static TaskBundle getTaskBundle(HttpServletRequest request, AllModulesInjected bs, boolean applyUsersFilter, boolean embeddedInJSP, Binder binder, String filterTypeParam, String modeTypeParam) throws GwtTeamingException {
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
				applyUsersFilter,
				embeddedInJSP,
				binder,
				filterTypeParam,
				modeTypeParam,
				true);	// true -> Retrieve the TaskBundle on behalf of the client.
	}
	
	/*
	 * Returns the TaskBundle from a task folder.
	 */
	private static TaskBundle getTaskBundleImpl(HttpServletRequest request, AllModulesInjected bs, boolean applyUsersFilter, boolean embeddedInJSP, Binder binder, String filterTypeParam, String modeTypeParam, boolean clientBundle) throws GwtTeamingException {
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
			applyUsersFilter,
			embeddedInJSP,
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
	private static TaskDate getTaskDateFromEntryMap(Map m, String key, boolean allDayEvent, boolean nullIfNotThere) {
		TaskDate reply;
		Date date = GwtEventHelper.getDateFromEntryMap(m, key);
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
	 * Returns a TaskDisplayDataRpcResponseData object for the current
	 * user's view of the specified task folder.
	 * 
	 * @param request
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static TaskDisplayDataRpcResponseData getTaskDisplayData(HttpServletRequest request, AllModulesInjected bs, Long binderId) throws GwtTeamingException {
		try {
			// Access the information we need from the binder...
			Long userId = GwtServerHelper.getCurrentUser().getId();
			FilterType ft = TaskHelper.getTaskFilterType(bs, userId, binderId);
			if (null == ft) {
				ft = FilterType.ALL;
			}
			ModeType mode = TaskHelper.getTaskModeType(  bs, userId, binderId);
			if (null == mode) {
				mode = ModeType.PHYSICAL;
			}
			UserProperties userProperties = bs.getProfileModule().getUserProperties(userId, binderId);
			String taskChangeReason = ((String) userProperties.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_CHANGE));
			String taskChangeId     = ((String) userProperties.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_ID    ));
			Workspace binderWs = BinderHelper.getBinderWorkspace(bs.getBinderModule().getBinder(binderId));
			boolean showModeSelect = BinderHelper.isBinderUserWorkspace(binderWs);
			AdaptedPortletURL adaptedUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adaptedUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
			adaptedUrl.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(binderId));
			adaptedUrl.setParameter("xxx_operand_xxx", "xxx_option_xxx");	// Place holder -> Patched when used.
			String expandGraphsS = ((String) userProperties.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_EXPAND_GRAPHS));
			boolean expandGraphs = (MiscUtil.hasString(expandGraphsS) ? Boolean.parseBoolean(expandGraphsS) : false);
			
			// ...and use it to construct and return a
			// ...TaskDisplayDataRpcResponseData object.
			return
				new TaskDisplayDataRpcResponseData(
					ft.name(),
					mode.name(),
					(MiscUtil.hasString(taskChangeId) ? Long.parseLong(taskChangeId) : null),
					taskChangeReason,
					MiscUtil.hasString(taskChangeReason),
					showModeSelect,
					expandGraphs,
					adaptedUrl.toString());
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtTaskHelper.getTaskDisplayData( SOURCE EXCEPTION ):  ");
		}
		
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
		
		if (GwtLogHelper.isDebugEnabled(m_logger)) {
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.getTaskLinkage( Read TaskLinkage for binder ): " + String.valueOf(binder.getId()));
			dumpTaskLinkage(reply);
		}
		
		return reply;
	}

	/**
	 * Returns a TaskStats object for a given task folder.
	 * 
	 * @param folder
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static TaskStats getTaskStatistics(Folder folder) {
		// Allocate a TaskStats object we can return.
		TaskStats reply = new TaskStats();
		
		// Initialize some variables to track what we find.
		boolean foundComplete = false;
		boolean foundPriority = false;
		boolean foundStatus   = false;

		// Does the folder have a Statistics custom attribute?
		Statistics stats = GwtStatisticsHelper.getFolderStatistics(folder);
		if (null != stats) {
			// Yes!  Does it contain any task definition value maps?
			List<Map> defMaps = GwtStatisticsHelper.getEntryDefMaps(stats, ObjectKeys.FAMILY_TASK);
			if ((null != defMaps) && (!(defMaps.isEmpty()))) {
				// Yes!  Scan them.
				for (Map defMap:  defMaps) {
					// Is there a map for completed percentage
					// statistics in this definition map?
					int totalThisDef = (-1);
					Map completeMap = ((Map) defMap.get(TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME));
					if (null != completeMap) {
						// Yes!  Extract the completed values.
						foundComplete = true;
						Integer t = ((Integer) completeMap.get(Statistics.TOTAL_KEY));
						if ((null != t) && (t != totalThisDef)) {
							if ((-1) != totalThisDef) {
								GwtLogHelper.error(m_logger, "GwtTaskHelper.getTaskStatistics():  Inconsistent total for 'Complete'");
							}
							totalThisDef = t;
						}
						
						Map cValuesMap = ((Map) completeMap.get(Statistics.VALUES));
						if (null != cValuesMap) {
							Set<String> cKeys = ((Set<String>) cValuesMap.keySet());
							for (String cKey:  cKeys) {
								Integer c = ((Integer) cValuesMap.get(cKey));
								if (null != c) {
									if      (TaskInfo.COMPLETED_0.equals(  cKey)) reply.addCompleted0(  c);
									else if (TaskInfo.COMPLETED_10.equals( cKey)) reply.addCompleted10( c);
									else if (TaskInfo.COMPLETED_20.equals( cKey)) reply.addCompleted20( c);
									else if (TaskInfo.COMPLETED_30.equals( cKey)) reply.addCompleted30( c);
									else if (TaskInfo.COMPLETED_40.equals( cKey)) reply.addCompleted40( c);
									else if (TaskInfo.COMPLETED_50.equals( cKey)) reply.addCompleted50( c);
									else if (TaskInfo.COMPLETED_60.equals( cKey)) reply.addCompleted60( c);
									else if (TaskInfo.COMPLETED_70.equals( cKey)) reply.addCompleted70( c);
									else if (TaskInfo.COMPLETED_80.equals( cKey)) reply.addCompleted80( c);
									else if (TaskInfo.COMPLETED_90.equals( cKey)) reply.addCompleted90( c);
									else if (TaskInfo.COMPLETED_100.equals(cKey)) reply.addCompleted100(c);
								}
							}
						}
					}
					
					// Is there a map for priority statistics in this
					// definition map?
					Map priorityMap = ((Map) defMap.get(TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME));
					if (null != priorityMap) {
						// Yes!  Extract the priority values.
						foundPriority = true;
						Integer t = ((Integer) completeMap.get(Statistics.TOTAL_KEY));
						if ((null != t) && (t != totalThisDef)) {
							if ((-1) != totalThisDef) {
								GwtLogHelper.error(m_logger, "GwtTaskHelper.getTaskStatistics():  Inconsistent total for 'Priority'");
							}
							totalThisDef = t;
						}
						
						Map pValuesMap = ((Map) priorityMap.get(Statistics.VALUES));
						if (null != pValuesMap) {
							Set<String> pKeys = ((Set<String>) pValuesMap.keySet());
							for (String pKey:  pKeys) {
								Integer p = ((Integer) pValuesMap.get(pKey));
								if (null != p) {
									if      (TaskInfo.PRIORITY_NONE.equals(    pKey)) reply.addPriorityNone(    p);
									else if (TaskInfo.PRIORITY_CRITICAL.equals(pKey)) reply.addPriorityCritical(p);
									else if (TaskInfo.PRIORITY_HIGH.equals(    pKey)) reply.addPriorityHigh(    p);
									else if (TaskInfo.PRIORITY_MEDIUM.equals(  pKey)) reply.addPriorityMedium(  p);
									else if (TaskInfo.PRIORITY_LOW.equals(     pKey)) reply.addPriorityLow(     p);
									else if (TaskInfo.PRIORITY_LEAST.equals(   pKey)) reply.addPriorityLeast(   p);
								}
							}
						}
					}
					
					// Is there a map for status statistics in this
					// definition map?
					Map statusMap = ((Map) defMap.get(TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME));
					if (null != statusMap) {
						// Yes!  Extract the status values.
						foundStatus = true;
						Integer t = ((Integer) completeMap.get(Statistics.TOTAL_KEY));
						if ((null != t) && (t != totalThisDef)) {
							if ((-1) != totalThisDef) {
								GwtLogHelper.error(m_logger, "GwtTaskHelper.getTaskStatistics():  Inconsistent total for 'Status'");
							}
							totalThisDef = t;
						}
						
						Map sValuesMap = ((Map) statusMap.get(Statistics.VALUES));
						if (null != sValuesMap) {
							Set<String> sKeys = ((Set<String>) sValuesMap.keySet());
							for (String sKey:  sKeys) {
								Integer s = ((Integer) sValuesMap.get(sKey));
								if (null != s) {
									if      (TaskInfo.STATUS_NEEDS_ACTION.equals(sKey)) reply.addStatusNeedsAction(s);
									else if (TaskInfo.STATUS_IN_PROCESS.equals(  sKey)) reply.addStatusInProcess(  s);
									else if (TaskInfo.STATUS_COMPLETED.equals(   sKey)) reply.addStatusCompleted(  s);
									else if (TaskInfo.STATUS_CANCELED.equals(    sKey)) reply.addStatusCanceled(   s);
								}
							}
						}
					}

					// If we have a total from this definition...
					if ((-1) != totalThisDef) {
						// ...add it to the reply.
						reply.addTotalTasks(totalThisDef);
					}
				}
			}
		}

		// If we found any of the statistic components, return the task
		// statistics object.  Otherwise, return null.
		return ((foundComplete || foundPriority || foundStatus) ? reply : null);
	}

	/**
	 * Returns a TaskStats object for a given task folder.
	 * 
	 * @param bs
	 * @param folderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static TaskStats getTaskStatistics(AllModulesInjected bs, Long folderId) throws GwtTeamingException {
		try {
			Folder folder = bs.getFolderModule().getFolder(folderId);
			return getTaskStatistics(folder);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtTaskHelper.getTaskStatistics( SOURCE EXCEPTION ):  ");
		}
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
			Set<Long> teamMemberIds = GwtServerHelper.getTeamMemberIds(bs, binderId);
			if (null != teamMemberIds) {
				// Yes!  Add a base AssignmentInfo with each ID to the
				// List<AssignmentInfo> that we're going to return.
				for (Long memberId:  teamMemberIds) {
					reply.add(AssignmentInfo.construct(memberId, AssigneeType.TEAM));
				}
			}
			
			// Complete the content of the AssignmentInfo's to return.
			completeMembershipAIs(bs, reply);			

			// If we get here, reply refers to a List<AssignmentInfo>
			// describing the members of a team.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
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
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.isTaskLinkValid( EXCEPTION ): ", ex);
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
			TaskListItem tli = new TaskListItem(task);
			tli.setExpandedSubtasks(!(collapsedSubtasks.contains(task.getTaskId().getEntityId())));
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
	public static ErrorListRpcResponseData purgeTasks(HttpServletRequest request, AllModulesInjected bs, List<EntityId> taskIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// If we get here, we have rights to purge all the tasks
			// that we were given.  Scan them...
			List<Long> binderIds = new ArrayList<Long>();
			FolderModule fm = bs.getFolderModule();
			for (EntityId taskId:  taskIds) {
				// ...purging each...
				Long binderId = taskId.getBinderId();
				ListUtil.addLongToListLongIfUnique(binderIds, binderId);
				try {
					fm.deleteEntry(
						binderId,
						taskId.getEntityId());
				}
				
				catch (Exception e) {
					// ...tracking any that we couldn't purge.
					String entryTitle = GwtServerHelper.getEntityTitle(bs, taskId);
					String messageKey;
					if      (e instanceof AccessControlException) messageKey = "purgeEntryError.AccssControlException";
					else                                          messageKey = "purgeEntryError.OtherException";
					reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
				}
			}
			
			// Scan the IDs of binders that we're purging from...
			for (Long binderId:  binderIds) {
				// ...and tell each to update any calculated dates they
				// ...contain.
				updateCalculatedDates(
					request,
					bs,
					getTaskBinder(bs, null, binderId),
					null);	// null -> Update the calculated dates for all the tasks in the binder.
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
		}
	}
	
	/*
	 * Reads the task information from the specified binder and stores
	 * it in the given TaskBundle.
	 * 
	 * Apply task linkage, ... as necessary to the list stored.
	 */
	private static void readTaskList(HttpServletRequest request, AllModulesInjected bs, boolean applyUsersFilter, boolean embeddedInJSP, Binder binder, TaskBundle tb, List<Long> collapsedSubtasks, boolean clientBundle) throws GwtTeamingException {
		// Create a List<TaskListItem> that we'll fill up with the task
		// list.
		List<TaskListItem> taskList = new ArrayList<TaskListItem>();
		tb.setTasks(taskList);

		// Read the tasks from the binder.
		List<TaskInfo> tasks = readTasks(
			request,
			bs,
			applyUsersFilter,
			embeddedInJSP,
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
			TaskListItem tli = new TaskListItem(task);
			if (respectLinkage) {
				tli.setExpandedSubtasks(!(collapsedSubtasks.contains(task.getTaskId().getEntityId())));
			}
			taskList.add(tli);

			// ...and if we're handling the task linkage... 
			if (respectLinkage) {
				// ...add it to the order since it will now be
				// ...considered 'linked into' the task folder's order
				// ...and hierarchy.
				TaskLink tl = new TaskLink();
				tl.setEntryId(task.getTaskId().getEntityId());
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

	/**
	 * Reads the tasks from the specified binder.
	 * 
	 * @param request
	 * @param bs
	 * @param applyUsersFilter
	 * @param embeddedInJSP
	 * @param binder
	 * @param filterTypeParam
	 * @param modeTypeParam
	 * @param clientBundle
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static List<TaskInfo> readTasks(HttpServletRequest request, AllModulesInjected bs, boolean applyUsersFilter, boolean embeddedInJSP, Binder binder, String filterTypeParam, String modeTypeParam, boolean clientBundle) throws GwtTeamingException {
		Map taskEntriesMap;		
		try {
			// Setup to read the task entries...
			Map options;
			HttpSession session = WebHelper.getRequiredSession(request);
			
			options = null;
			if (embeddedInJSP) { 
				GwtUISessionData optionsObj = ((GwtUISessionData) session.getAttribute(TaskHelper.CACHED_FIND_TASKS_OPTIONS_KEY));
				if (null != optionsObj) {
					options = ((Map) optionsObj.getData());
				}
			}
			if (null == options) {
				options = new HashMap();
			}
			options.put(ObjectKeys.SEARCH_MAX_HITS, (Integer.MAX_VALUE - 1));
			options.put(ObjectKeys.SEARCH_OFFSET,   0);
			if (applyUsersFilter) {
				Document searchFilter = ((Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER));
				if (null == searchFilter) {
					User user = GwtServerHelper.getCurrentUser();
					UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binder.getId());
					BinderHelper.addSearchFiltersToOptions(bs, binder, userFolderProperties, true, options);
				}
			}
	
			// ...and read them.
			taskEntriesMap = TaskHelper.findTaskEntries(
				bs,
				binder,
				filterTypeParam,
				modeTypeParam,
				options);
		}
		catch (Exception ex) {
			GwtLogHelper.error(m_logger, "GwtTaskHelper.readTasks( EXCEPTION ): ", ex);
			throw GwtLogHelper.getGwtClientException(ex);
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
			
			ti.setOverdue(         GwtEventHelper.getOverdueFromEntryMap(           taskEntry, GwtEventHelper.buildTaskEventFieldName(Constants.EVENT_FIELD_LOGICAL_END_DATE) ));
			ti.setEvent(                          getTaskEventFromEntryMap(         taskEntry, clientBundle                                                                   ));
			ti.setStatus(          GwtEventHelper.getStringFromEntryMapRaw(         taskEntry, TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME                                    ));
			ti.setCompleted(       GwtEventHelper.getStringFromEntryMapRaw(         taskEntry, TaskHelper.COMPLETED_TASK_ENTRY_ATTRIBUTE_NAME                                 ));
			ti.setSeen(                   seenMap.checkIfSeen(                      taskEntry                                                                                 ));
			ti.setEntityType(      GwtEventHelper.getStringFromEntryMapRaw(         taskEntry, Constants.ENTITY_FIELD                                                         ));
			ti.setPriority(        GwtEventHelper.getStringFromEntryMapRaw(         taskEntry, TaskHelper.PRIORITY_TASK_ENTRY_ATTRIBUTE_NAME                                  ));
			ti.setAssignments(     GwtEventHelper.getAssignmentInfoListFromEntryMap(taskEntry, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME,        AssigneeType.INDIVIDUAL));
			ti.setAssignmentGroups(GwtEventHelper.getAssignmentInfoListFromEntryMap(taskEntry, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME, AssigneeType.GROUP     ));
			ti.setAssignmentTeams( GwtEventHelper.getAssignmentInfoListFromEntryMap(taskEntry, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME,  AssigneeType.TEAM      ));
			
			String title = GwtEventHelper.getStringFromEntryMapRaw(taskEntry, Constants.TITLE_FIELD);
			if (!(MiscUtil.hasString(title))) {
				title = ("--" + NLT.get("entry.noTitle") + "--");
			}
			ti.setTitle(title);
			
			String desc = GwtEventHelper.getStringFromEntryMapRaw( taskEntry, Constants.DESC_FIELD );
			ti.setDesc( desc );
			
			EntityId taskId = new EntityId(
				GwtEventHelper.getLongFromEntryMap(taskEntry, Constants.BINDER_ID_FIELD),
				GwtEventHelper.getLongFromEntryMap(taskEntry, Constants.DOCID_FIELD    ),
				EntityId.FOLDER_ENTRY);
			ti.setTaskId(taskId);

			ti.setCompletedDate(
				getTaskDateFromEntryMap(
					taskEntry,
					Constants.TASK_COMPLETED_DATE_FIELD,
					ti.getEvent().getAllDayEvent(),
					false));	// false -> Don't return a null entry.
			
			ti.setCreatorId( GwtEventHelper.getLongFromEntryMap(taskEntry, Constants.CREATORID_FIELD)     );
			ti.setModifierId(GwtEventHelper.getLongFromEntryMap(taskEntry, Constants.MODIFICATIONID_FIELD));
			
			reply.add(ti);
		}

		// At this point, the TaskInfo's in the List<TaskInfo> are not
		// complete.  They're missing things like the user's rights to
		// the entries, the location of their binders and details about
		// the task's assignments.  Complete their content.  Note that
		// we do this AFTER collecting data from the search index so
		// that we only have to perform a single DB read for each type
		// of information we need to complete the TaskInfo details.
		completeTaskRights(         bs,          reply);
		if (clientBundle || GwtLogHelper.isDebugEnabled(m_logger)) {
			completeBinderLocations(bs,          reply);
			completeAIs(            bs, request, reply);
		}
				
		if (GwtLogHelper.isDebugEnabled(m_logger)) {
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.readTasks( Read List<TaskInfo> for binder ): " + String.valueOf(binderId));
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
	public static String saveTaskCompleted(AllModulesInjected bs, List<EntityId> taskIds, String completed) throws GwtTeamingException {
		// Are we marking the tasks completed?
		boolean nowCompleted = (TaskInfo.COMPLETED_100.equals(completed));
				
		// Scan the tasks whose completed value is changing.
		FolderModule  fm      = bs.getFolderModule();
		ProfileModule pm      = bs.getProfileModule();
		SeenMap       seenMap = pm.getUserSeenMap(null);
		for (EntityId taskId:  taskIds) {
			Long binderId = taskId.getBinderId();
			Long entryId  = taskId.getEntityId();
			try {
				// Has the user seen this entry already?
				FolderEntry fe       = fm.getEntry(binderId, entryId);
				boolean     taskSeen = seenMap.checkIfSeen(fe);
				
				// Construct the appropriate form data for the change...
				String status;
				if (nowCompleted) {
					status = TaskInfo.STATUS_COMPLETED;
				}				
				else {				
					status = TaskHelper.getTaskStatusValue(fe);
					if (TaskInfo.STATUS_COMPLETED.equals(status)) {
						status = TaskInfo.STATUS_IN_PROCESS;
					}
				}
				String priority = TaskHelper.getTaskPriorityValue(fe);
				Map formData = new HashMap();
				TaskHelper.adjustTaskAttributesDependencies(fe, formData, priority, status, completed);

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
				throw GwtLogHelper.getGwtClientException(ex);
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
	public static TaskEventRpcResponseData saveTaskDueDate(AllModulesInjected bs, EntityId taskId, TaskEvent taskEvent) throws GwtTeamingException {
		TaskEvent reply = null;
		try {
			// - - - - - - - - - - - - - //
			// Modify the task's Event.  //
			// - - - - - - - - - - - - - //
			
			// Read the Event currently stored on the task...
			Long binderId = taskId.getBinderId();
			Long entryId  = taskId.getEntityId();
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
			event.setDtCalcStart((Calendar) null); event.setDtStart(getCalFromDate(taskEvent.getActualStart()));			
			event.setDtCalcEnd(  (Calendar) null); event.setDtEnd(  getCalFromDate(taskEvent.getActualEnd()));
			
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
			if (!(reply.getAllDayEvent())) {
				TaskDuration tD = reply.getDuration();
				if (tD.hasDaysOnly()) {
					boolean hasStartAndEnd = (reply.hasActualEnd() && reply.hasActualStart());
					reply.setEndIsCalculated(!hasStartAndEnd);
				}
			}
			
			// Is the modified task overdue?
			TaskDate endTD   = reply.getLogicalEnd();
			Date     endDate = ((null == endTD)   ? null  : endTD.getDate()                );
			boolean  overdue = ((null == endDate) ? false : DateComparer.isOverdue(endDate));

			// If we get here, reply refers to the modified TaskEvent.
			// Return it wrapped in a TaskEventRpcResponseData.
			return new TaskEventRpcResponseData(reply, overdue);			
		}
		
		catch (Exception ex) {
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					ex,
					"GwtTaskHelper.saveTaskDueDate( Can't Save Due Date on task:  '" + taskId.getEntityId() + "', EXCEPTION )");
		}
	}

	/**
	 * Saves the save of the task graphs on a folder for the current
	 * user.
	 * 
	 * @param request
	 * @param bs
	 * @param folderId
	 * @param expandGraphs
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData saveTaskGraphState( HttpServletRequest request, AllModulesInjected bs, Long folderId, boolean expandGraphs) throws GwtTeamingException {
		try {
			bs.getProfileModule().setUserProperty(GwtServerHelper.getCurrentUser().getId(), folderId, ObjectKeys.BINDER_PROPERTY_TASK_EXPAND_GRAPHS, String.valueOf(expandGraphs));
			return new BooleanRpcResponseData(true);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
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
			
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				if (null == tl) {
					GwtLogHelper.debug(m_logger, "GwtTaskHelper.setTaskLinkage( Removed TaskLinkage for binder ): " + String.valueOf(binder.getId()));
				}
				else {
					GwtLogHelper.debug(m_logger, "GwtTaskHelper.setTaskLinkage( Stored TaskLinkage for binder ): " + String.valueOf(binder.getId()));
					dumpTaskLinkage(tl);
				}
			}
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
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
			String completed = TaskHelper.getTaskCompletedValue(fe);
			String status    = TaskHelper.getTaskStatusValue(   fe);
			TaskHelper.adjustTaskAttributesDependencies(fe, formData, priority, status, completed);
			
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
			throw GwtLogHelper.getGwtClientException(ex);
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
	public static String saveTaskStatus(AllModulesInjected bs, List<EntityId> taskIds, String status) throws GwtTeamingException {
		// Are we marking the tasks completed?
		boolean nowCompleted = (TaskInfo.STATUS_COMPLETED.equals(status));
		
		// Scan the tasks whose status is changing.
		FolderModule  fm      = bs.getFolderModule();
		ProfileModule pm      = bs.getProfileModule();
		SeenMap       seenMap = pm.getUserSeenMap(null);
		for (EntityId taskId:  taskIds) {
			Long binderId = taskId.getBinderId();
			Long entryId  = taskId.getEntityId();
			try {
				// Has the user seen this entry already?
				FolderEntry   fe       = fm.getEntry(binderId, entryId);
				boolean       taskSeen = seenMap.checkIfSeen(fe);
				
				// Construct the appropriate form data for the change...
				String completed;
				if (nowCompleted) {
					completed = TaskInfo.COMPLETED_100;
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

					// Do we need to force a status change in what's
					// being stored?
					String forcedCompleted;
					if      (nowActive   && wasInactive && wasComplete) forcedCompleted = TaskInfo.COMPLETED_90;
					else if (nowCanceled &&                wasComplete) forcedCompleted = TaskInfo.COMPLETED_90;
					else                                                forcedCompleted = null;
					if (null != forcedCompleted)
					     completed = forcedCompleted;
					else completed = currentCompleted;
				}
				String priority = TaskHelper.getTaskPriorityValue(fe);
				Map formData = new HashMap();
				TaskHelper.adjustTaskAttributesDependencies(fe, formData, priority, status, completed);

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
				throw GwtLogHelper.getGwtClientException(ex);
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
	 * 2) If the entry is null, ALL the tasks in the binder will have
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
			true,
			false,
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
					if (GwtLogHelper.isDebugEnabled(m_logger)) {
						GwtLogHelper.debug(m_logger, "GwtTaskHelper.updateCalculatedDatesImpl( 1:Adjusting Start ):  " + ti.getTitle());
					}
					newCalcEnd      = EventHelper.adjustDate(tiSD.getActualStart().getDate(), durDays);
					removeCalcStart = true;
				}
				
				else if ((!hasActualStart) && hasActualEnd) {
					// No start, has end!  Calculate a start.
					if (GwtLogHelper.isDebugEnabled(m_logger)) {
						GwtLogHelper.debug(m_logger, "GwtTaskHelper.updateCalculatedDatesImpl( 2:Adjusting End ):  " + ti.getTitle());
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
							if (GwtLogHelper.isDebugEnabled(m_logger)) {
								GwtLogHelper.debug(m_logger, "GwtTaskHelper.updateCalculatedDatesImpl( 3:Adjusting End ):  " + ti.getTitle());
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
							TaskDate calcTD;
							if (null != newCalcStart) {
								calcTD = new TaskDate();
								calcTD.setDate(                                     newCalcStart                       );
								calcTD.setDateDisplay(EventHelper.getDateTimeString(newCalcStart, tiE.getAllDayEvent()));
							}
							else {
								calcTD = tiE.getActualStart();
							}
							tiE.setLogicalStart(calcTD);
						}

						// If we changed the calculated end...
						if (removeCalcEnd || (null != newCalcEnd)) {
							// ...update it in the task...
							TaskDate calcTD;
							if (null != newCalcEnd) {
								calcTD = new TaskDate();
								calcTD.setDate(                                     newCalcEnd                       );
								calcTD.setDateDisplay(EventHelper.getDateTimeString(newCalcEnd, tiE.getAllDayEvent()));
							}
							else {
								calcTD = tiE.getActualEnd();
							}
							tiE.setLogicalEnd(calcTD);
							
							// ...and track the fact that we changed
							// ...this task's calculated end date.
							updates.put(ti.getTaskId().getEntityId(), calcTD);
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
						updates.put(pTask.getTaskId().getEntityId(), pNewCalcEnd);
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
				Long            entryId         = ti.getTaskId().getEntityId();
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
						event.setDtCalcStart(getCalFromDate(calcStart));
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
						event.setDtCalcEnd(getCalFromDate(calcEnd));
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
				GwtLogHelper.error(m_logger, "GwtTaskHelper.updateCalculatedDatesOnTask( Update failed on:  '" + ti.getTitle() + "' )");
				GwtLogHelper.error(m_logger, "GwtTaskHelper.updateCalculatedDatesOnTask( EXCEPTION ):  ", ex);
			}
		}
		
		else {
			// No, the user doesn't have rights to modify the task.
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.updateCalculatedDatesOnTask( Update failed on:  '" + ti.getTitle() + "' )");
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.updateCalculatedDatesOnTask( Insufficient Rights )");
		}
		
		// If we get here, reply is true if we modified task's
		// calculated start and or end and false otherwise.  Return it.
		return reply;
	}

	/**
	 * Validates the task FolderEntry's referenced by a TaskLinkage.
	 * 
	 * @param bs
	 * @param tl
	 */
	public static void validateTaskLinkage(AllModulesInjected bs, TaskLinkage tl) {
		if (GwtLogHelper.isDebugEnabled(m_logger)) {
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.validateTaskLinkage( BEFORE ):");
			dumpTaskLinkage(tl);
		}
		
		// Simply validate the List<TaskLink> of the task ordering.
		validateTaskLinkList(bs, tl.getTaskOrder());
		
		if (GwtLogHelper.isDebugEnabled(m_logger)) {
			GwtLogHelper.debug(m_logger, "GwtTaskHelper.validateTaskLinkage( AFTER ):");
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
