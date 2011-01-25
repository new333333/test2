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
import java.util.List;

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
	public static class AssignedToComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public AssignedToComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their assignee.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param task1
		 * @param task2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem task1, TaskListItem task2) {
			String assigned1 = getAssignee(task1.getTask());
			String assigned2 = getAssignee(task2.getTask());

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
			for (AssignmentInfo assignee:  task.getAssignments())      return getNonNullString(assignee.getTitle());
			for (AssignmentInfo assignee:  task.getAssignmentGroups()) return getNonNullString(assignee.getTitle());
			for (AssignmentInfo assignee:  task.getAssignmentTeams())  return getNonNullString(assignee.getTitle());
			
			return "";
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their closed date or
	 * their % done value if they haven't been marked completed yet.
	 */
	public static class ClosedPercentDoneComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public ClosedPercentDoneComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their closed date or their
		 * % done value if they haven'tbeen marked completed yet.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param task1
		 * @param task2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem task1, TaskListItem task2) {
			String c1 = getSortString(task1, getNonNullString(task1.getTask().getCompleted(), "c000"));
			String c2 = getSortString(task2, getNonNullString(task2.getTask().getCompleted(), "c000"));
			
			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(c1, c2);
			else reply = GwtClientHelper.safeSColatedCompare(c2, c1);
			return reply;
		}

		/*
		 * Returns the string to use for sorting 'Closed % Done'
		 * values.
		 * 
		 * Forces tasks that are closed with a completion date to
		 * sort after those without a completion date.
		 */
		private static String getSortString(TaskListItem task, String completed) {
			String reply = completed;
			if (reply.equals("c100")) {
				String completedDateDisplay = task.getTask().getCompletedDateDisplay();
				if (GwtClientHelper.hasString(completedDateDisplay)) {
					reply = ("c101:" + completedDateDisplay);
				}
			}
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their due date.
	 */
	public static class DueDateComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public DueDateComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their due date.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param task1
		 * @param task2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem task1, TaskListItem task2) {
			Date d1 = getNonNullDate(task1.getTask().getEvent().getLogicalEnd());
			Date d2 = getNonNullDate(task2.getTask().getEvent().getLogicalEnd());
			
			int reply;
			if (m_ascending)
			     reply = d1.compareTo(d2);
			else reply = d2.compareTo(d1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their location.
	 */
	public static class LocationComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public LocationComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their locations.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param task1
		 * @param task2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem task1, TaskListItem task2) {
			String loc1 = getNonNullString(task1.getTask().getLocation());
			String loc2 = getNonNullString(task2.getTask().getLocation());

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(loc1, loc2);
			else reply = GwtClientHelper.safeSColatedCompare(loc2, loc1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their name.
	 */
	public static class NameComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public NameComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their names.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param task1
		 * @param task2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem task1, TaskListItem task2) {
			String name1 = getNonNullString(task1.getTask().getTitle());
			String name2 = getNonNullString(task2.getTask().getTitle());

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(name1, name2);
			else reply = GwtClientHelper.safeSColatedCompare(name2, name1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their order number.
	 */
	public static class OrderComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public OrderComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their order number.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param task1
		 * @param task2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem task1, TaskListItem task2) {
			Integer id1 = TaskTable.getTaskOrder(task1);
			Integer id2 = TaskTable.getTaskOrder(task2);

			int reply;
			if (m_ascending)
			     reply = id1.compareTo(id2);
			else reply = id2.compareTo(id1);
			return reply;
		}
	}
	
	/**
	 * Inner class used to compare two tasks by their priority.
	 */
	public static class PriorityComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public PriorityComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their priority.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param task1
		 * @param task2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem task1, TaskListItem task2) {
			String p1 = getNonNullString(task1.getTask().getPriority(), "p0");
			String p2 = getNonNullString(task2.getTask().getPriority(), "p0");

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
	public static class StatusComparator implements Comparator<TaskListItem> {
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public StatusComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two TaskListItem's by their status.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param task1
		 * @param task2
		 * 
		 * @return
		 */
		@Override
		public int compare(TaskListItem task1, TaskListItem task2) {
			String s1 = getNonNullString(task1.getTask().getStatus(), "s0");
			String s2 = getNonNullString(task2.getTask().getStatus(), "s0");

			int reply;
			if (m_ascending)
			     reply = GwtClientHelper.safeSColatedCompare(s1, s2);
			else reply = GwtClientHelper.safeSColatedCompare(s2, s1);
			return reply;
		}
	}
	
	/**
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private TaskSorter() {
		// Nothing to do.
	}
		
	/**
	 * Sorts the List<TaskListItem> via the comparator provided.
	 * 
	 * @param map
	 * @param comparator
	 */
	public static void sort(List<TaskListItem> tasks, Comparator<TaskListItem> comparator) {
		Collections.sort(tasks, comparator);
	}

	/*
	 * Helper methods used to guard against comparing null values.
	 */
	private static Date getNonNullDate(Date d, Date defDate) {return ((null == d) ? defDate : d);   }	
	private static Date getNonNullDate(Date d)               {return getNonNullDate(d, new Date(0));}
	
	private static String getNonNullString(String s, String defString) {return ((null == s) ? defString : s);}	
	private static String getNonNullString(String s)                   {return getNonNullString(s, "");      }
}
