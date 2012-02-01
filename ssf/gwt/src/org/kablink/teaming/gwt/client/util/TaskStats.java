/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import java.util.List;

import org.kablink.teaming.gwt.client.util.TaskListItem;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to model task statistics. 
 * 
 * @author drfoster@novell.com
 */
public class TaskStats implements IsSerializable {
	private int m_priorityCritical;		//
	private int m_priorityHigh;			//
	private int m_priorityLeast;		//
	private int m_priorityLow;			//
	private int m_priorityMedium;		//
	private int m_priorityNone;			//
	
	private int m_statusCanceled;		//
	private int m_statusCompleted;		//
	private int m_statusInProcess;		//
	private int m_statusNeedsAction;	//
	
	private int m_totalTasks;			//

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor for GWT serialization requirements.
	 */
	public TaskStats() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param taskList
	 */
	public TaskStats(List<TaskListItem> taskList) {
		// Initialize this object...
		this();
		
		// ...and gather the statistics from the task list.
		gatherStats(taskList);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int getPriorityCritical()  {return m_priorityCritical; }
	public int getPriorityHigh()      {return m_priorityHigh;     }
	public int getPriorityLeast()     {return m_priorityLeast;    }
	public int getPriorityLow()       {return m_priorityLow;      }
	public int getPriorityMedium()    {return m_priorityMedium;   }
	public int getPriorityNone()      {return m_priorityNone;     }
	
	public int getStatusCanceled()    {return m_statusCanceled;   }
	public int getStatusCompleted()   {return m_statusCompleted;  }
	public int getStatusInProcess()   {return m_statusInProcess;  }
	public int getStatusNeedsAction() {return m_statusNeedsAction;}
	
	public int getTotalTasks()        {return m_totalTasks;       }

	/**
	 * Returns the percentage a given count is of the total.
	 * 
	 * @param count
	 * 
	 * @return
	 */
	public int getPercent(int count) {
		return ((int) Math.round((((double) count) / ((double) m_totalTasks)) * 100.0));
	}
	
	/*
	 * Reflects this task in the counts.
	 */
	private void countTask(TaskListItem task) {
		// Count the priority...
		String p = task.getTask().getPriority();
		if      (p.equals(TaskListItem.TaskInfo.PRIORITY_NONE))     m_priorityNone     += 1;
		else if (p.equals(TaskListItem.TaskInfo.PRIORITY_CRITICAL)) m_priorityCritical += 1;
		else if (p.equals(TaskListItem.TaskInfo.PRIORITY_HIGH))     m_priorityHigh     += 1;
		else if (p.equals(TaskListItem.TaskInfo.PRIORITY_MEDIUM))   m_priorityMedium   += 1;
		else if (p.equals(TaskListItem.TaskInfo.PRIORITY_LOW))      m_priorityLow      += 1;
		else if (p.equals(TaskListItem.TaskInfo.PRIORITY_LEAST))    m_priorityLeast    += 1;

		// ...status...
		String s = task.getTask().getStatus();
		if      (s.equals(TaskListItem.TaskInfo.STATUS_NEEDS_ACTION)) m_statusNeedsAction += 1;
		else if (s.equals(TaskListItem.TaskInfo.STATUS_IN_PROCESS))   m_statusInProcess   += 1;
		else if (s.equals(TaskListItem.TaskInfo.STATUS_COMPLETED))    m_statusCompleted   += 1;
		else if (s.equals(TaskListItem.TaskInfo.STATUS_CANCELED))     m_statusCanceled    += 1;
		
		// ...and total.
		m_totalTasks += 1;
	}
	
	/*
	 * Gathers the counts from the given list.
	 */
	private void gatherStats(List<TaskListItem> taskList) {
		// If we don't have a list...
		if ((null == taskList) || taskList.isEmpty()) {
			// ...there's nothing to count.  Bail.
			return;
		}

		// Scan the tasks in the task list...
		for (TaskListItem task:  taskList) {
			// ...counting each one...
			countTask(task);
			
			// ...and its subtasks.
			gatherStats(task.getSubtasks());
		}
	}
}
