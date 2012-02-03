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
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to model task statistics. 
 * 
 * @author drfoster@novell.com
 */
public class TaskStats implements IsSerializable {
	private int m_completed0;			//
	private int m_completed10;			//
	private int m_completed20;			//
	private int m_completed30;			//
	private int m_completed40;			//
	private int m_completed50;			//
	private int m_completed60;			//
	private int m_completed70;			//
	private int m_completed80;			//
	private int m_completed90;			//
	private int m_completed100;			//
	
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
	 * Add'er methods.
	 * 
	 * @param
	 */
	public void addCompleted0(       int completed0)        {m_completed0        += completed0;       }
	public void addCompleted10(      int completed10)       {m_completed10       += completed10;      }
	public void addCompleted20(      int completed20)       {m_completed20       += completed20;      }
	public void addCompleted30(      int completed30)       {m_completed30       += completed30;      }
	public void addCompleted40(      int completed40)       {m_completed40       += completed40;      }
	public void addCompleted50(      int completed50)       {m_completed50       += completed50;      }
	public void addCompleted60(      int completed60)       {m_completed60       += completed60;      }
	public void addCompleted70(      int completed70)       {m_completed70       += completed70;      }
	public void addCompleted80(      int completed80)       {m_completed80       += completed80;      }
	public void addCompleted90(      int completed90)       {m_completed90       += completed90;      }
	public void addCompleted100(     int completed100)      {m_completed100      += completed100;     }
	
	public void addPriorityCritical( int prioritiyCritical) {m_priorityCritical  += prioritiyCritical;}
	public void addPriorityHigh(     int prioritiyHigh)     {m_priorityHigh      += prioritiyHigh;    }
	public void addPriorityLeast(    int prioritiyLeast)    {m_priorityLeast     += prioritiyLeast;   }
	public void addPriorityLow(      int prioritiyLow)      {m_priorityLow       += prioritiyLow;     }
	public void addPriorityMedium(   int prioritiyMedium)   {m_priorityMedium    += prioritiyMedium;  }
	public void addPriorityNone(     int prioritiyNone)     {m_priorityNone      += prioritiyNone;    }
	
	public void addStatusCanceled(   int statusCanceled)    {m_statusCanceled    += statusCanceled;   }
	public void addStatusCompleted(  int statusCompleted)   {m_statusCompleted   += statusCompleted;  }
	public void addStatusInProcess(  int statusInProcess)   {m_statusInProcess   += statusInProcess;  }
	public void addStatusNeedsAction(int statusNeedsAction) {m_statusNeedsAction += statusNeedsAction;}
	
	public void addTotalTasks(       int totalTasks)        {m_totalTasks        += totalTasks;       }

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int getCompleted0()        {return m_completed0;       }
	public int getCompleted10()       {return m_completed10;      }
	public int getCompleted20()       {return m_completed20;      }
	public int getCompleted30()       {return m_completed30;      }
	public int getCompleted40()       {return m_completed40;      }
	public int getCompleted50()       {return m_completed50;      }
	public int getCompleted60()       {return m_completed60;      }
	public int getCompleted70()       {return m_completed70;      }
	public int getCompleted80()       {return m_completed80;      }
	public int getCompleted90()       {return m_completed90;      }
	public int getCompleted100()      {return m_completed100;     }
	
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
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setCompleted0(       int completed0)        {m_completed0        = completed0;       }
	public void setCompleted10(      int completed10)       {m_completed10       = completed10;      }
	public void setCompleted20(      int completed20)       {m_completed20       = completed20;      }
	public void setCompleted30(      int completed30)       {m_completed30       = completed30;      }
	public void setCompleted40(      int completed40)       {m_completed40       = completed40;      }
	public void setCompleted50(      int completed50)       {m_completed50       = completed50;      }
	public void setCompleted60(      int completed60)       {m_completed60       = completed60;      }
	public void setCompleted70(      int completed70)       {m_completed70       = completed70;      }
	public void setCompleted80(      int completed80)       {m_completed80       = completed80;      }
	public void setCompleted90(      int completed90)       {m_completed90       = completed90;      }
	public void setCompleted100(     int completed100)      {m_completed100      = completed100;     }
	
	public void setPriorityCritical( int prioritiyCritical) {m_priorityCritical  = prioritiyCritical;}
	public void setPriorityHigh(     int prioritiyHigh)     {m_priorityHigh      = prioritiyHigh;    }
	public void setPriorityLeast(    int prioritiyLeast)    {m_priorityLeast     = prioritiyLeast;   }
	public void setPriorityLow(      int prioritiyLow)      {m_priorityLow       = prioritiyLow;     }
	public void setPriorityMedium(   int prioritiyMedium)   {m_priorityMedium    = prioritiyMedium;  }
	public void setPriorityNone(     int prioritiyNone)     {m_priorityNone      = prioritiyNone;    }
	
	public void setStatusCanceled(   int statusCanceled)    {m_statusCanceled    = statusCanceled;   }
	public void setStatusCompleted(  int statusCompleted)   {m_statusCompleted   = statusCompleted;  }
	public void setStatusInProcess(  int statusInProcess)   {m_statusInProcess   = statusInProcess;  }
	public void setStatusNeedsAction(int statusNeedsAction) {m_statusNeedsAction = statusNeedsAction;}
	
	public void setTotalTasks(       int totalTasks)        {m_totalTasks        = totalTasks;       }

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
		// Count the completed...
		TaskInfo ti = task.getTask();
		String   c  = ti.getCompleted();
		if      (c.equals(TaskInfo.COMPLETED_0))   m_completed0   += 1;
		else if (c.equals(TaskInfo.COMPLETED_10))  m_completed10  += 1;
		else if (c.equals(TaskInfo.COMPLETED_20))  m_completed20  += 1;
		else if (c.equals(TaskInfo.COMPLETED_30))  m_completed30  += 1;
		else if (c.equals(TaskInfo.COMPLETED_40))  m_completed40  += 1;
		else if (c.equals(TaskInfo.COMPLETED_50))  m_completed50  += 1;
		else if (c.equals(TaskInfo.COMPLETED_60))  m_completed60  += 1;
		else if (c.equals(TaskInfo.COMPLETED_70))  m_completed70  += 1;
		else if (c.equals(TaskInfo.COMPLETED_80))  m_completed80  += 1;
		else if (c.equals(TaskInfo.COMPLETED_90))  m_completed90  += 1;
		else if (c.equals(TaskInfo.COMPLETED_100)) m_completed100 += 1;
		
		// ...priority...
		String p = ti.getPriority();
		if      (p.equals(TaskInfo.PRIORITY_NONE))     m_priorityNone     += 1;
		else if (p.equals(TaskInfo.PRIORITY_CRITICAL)) m_priorityCritical += 1;
		else if (p.equals(TaskInfo.PRIORITY_HIGH))     m_priorityHigh     += 1;
		else if (p.equals(TaskInfo.PRIORITY_MEDIUM))   m_priorityMedium   += 1;
		else if (p.equals(TaskInfo.PRIORITY_LOW))      m_priorityLow      += 1;
		else if (p.equals(TaskInfo.PRIORITY_LEAST))    m_priorityLeast    += 1;

		// ...status...
		String s = ti.getStatus();
		if      (s.equals(TaskInfo.STATUS_NEEDS_ACTION)) m_statusNeedsAction += 1;
		else if (s.equals(TaskInfo.STATUS_IN_PROCESS))   m_statusInProcess   += 1;
		else if (s.equals(TaskInfo.STATUS_COMPLETED))    m_statusCompleted   += 1;
		else if (s.equals(TaskInfo.STATUS_CANCELED))     m_statusCanceled    += 1;
		
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
