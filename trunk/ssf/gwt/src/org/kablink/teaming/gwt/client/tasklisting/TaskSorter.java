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
package org.kablink.teaming.gwt.client.tasklisting;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;

/**
 * Class used to sort TaskListItem's.  
 * 
 * @author drfoster@novell.com
 */
public class TaskSorter {
	/**
	 * Inner class used to compare two tasks by their assignee.
	 */
	public static class TaskAssignedComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TaskAssignedComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their assignee.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param tli1
		 * @param tli2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem tli1, TaskListItem tli2) {
			String assigned1 = getAssignee(tli1.getTask());
			String assigned2 = getAssignee(tli2.getTask());

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(assigned1, assigned2);
			else reply = GwtClientHelper.safeSColatedCompare(assigned2, assigned1);
			return reply;
		}

		/*
		 * Returns the name of the assignee to use for comparison
		 * purposes.
		 */
		private static String getAssignee(TaskInfo task) {
			for (AssignmentInfo assignee:  task.getAssignments())      return assignee.getTitle();
			for (AssignmentInfo assignee:  task.getAssignmentGroups()) return assignee.getTitle();
			for (AssignmentInfo assignee:  task.getAssignmentTeams())  return assignee.getTitle();
			
			return "";
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their closed date or
	 * % done if they haven't been marked completed yet.
	 */
	public static class TaskClosedCompletedComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TaskClosedCompletedComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their closed date.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param tli1
		 * @param tli2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem tli1, TaskListItem tli2) {
			String c1 = tli1.getTask().getCompleted();
			String c2 = tli2.getTask().getCompleted();

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(c1, c2);
			else reply = GwtClientHelper.safeSColatedCompare(c2, c1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their due date.
	 */
	public static class TaskDueComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TaskDueComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their due date.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param tli1
		 * @param tli2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem tli1, TaskListItem tli2) {
			Date d1 = tli1.getTask().getEvent().getLogicalEnd();
			Date d2 = tli2.getTask().getEvent().getLogicalEnd();

			int reply;
			if (m_ascending)
			     reply = d1.compareTo(d2);
			else reply = d2.compareTo(d1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their IDs.
	 */
	public static class TaskIdComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TaskIdComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their IDs.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param tli1
		 * @param tli2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem tli1, TaskListItem tli2) {
			Long id1 = tli1.getTask().getTaskId();
			Long id2 = tli2.getTask().getTaskId();

			int reply;
			if (m_ascending)
			     reply = id1.compareTo(id2);
			else reply = id2.compareTo(id1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their name.
	 */
	public static class TaskNameComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TaskNameComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their names.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param tli1
		 * @param tli2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem tli1, TaskListItem tli2) {
			String name1 = tli1.getTask().getTitle();
			String name2 = tli2.getTask().getTitle();

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(name1, name2);
			else reply = GwtClientHelper.safeSColatedCompare(name2, name1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their priority.
	 */
	public static class TaskPriorityComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TaskPriorityComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their priority.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param tli1
		 * @param tli2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem tli1, TaskListItem tli2) {
			String p1 = tli1.getTask().getPriority();
			String p2 = tli2.getTask().getPriority();

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(p1, p2);
			else reply = GwtClientHelper.safeSColatedCompare(p2, p1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their status.
	 */
	public static class TaskStatusComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TaskStatusComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their status.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param tli1
		 * @param tli2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem tli1, TaskListItem tli2) {
			String s1 = tli1.getTask().getStatus();
			String s2 = tli2.getTask().getStatus();

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(s1, s2);
			else reply = GwtClientHelper.safeSColatedCompare(s2, s1);
			return reply;
		}
	}
	
	/**
	 * Sorts the Map<Long, TaskListItem> via the comparator.
	 * 
	 * @param map
	 * @param comparator
	 * 
	 * @return
	 */
	public Map<Long, TaskListItem> sort(Map<Long, TaskListItem> map, Comparator<TaskListItem> comparator) {
		List<TaskListItem> list = new LinkedList<TaskListItem>(map.values());
		Collections.sort(list, comparator);
		Map<Long, TaskListItem> reply = new LinkedHashMap<Long, TaskListItem>(list.size());
		for (TaskListItem task:  list) {
			reply.put(task.getTask().getTaskId(), task);
		}
		return reply;
	}
}
