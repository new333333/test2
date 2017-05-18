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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about contents of a task
 * folder between the client (i.e., the TaskListing) and the server
 * (i.e., GwtRpcServiceImpl.getTaskListItem().)
 * 
 * @author drfoster@novell.com
 *
 */
public class TaskListItem implements IsSerializable {
	private boolean				m_expandSubtasks = true;							//
	private List<TaskListItem>	m_subtasks       = new ArrayList<TaskListItem>();	//
	private TaskInfo			m_task           = new TaskInfo();					//

	// The following is used to store information for managing this
	// TaskListItem in the user interface.
	private transient Object	m_uiData;

	/**
	 * Inner class used to model the Vibe Duration object in a way
	 * that's compatible with GWT RPC calls 
	 */
	public static class TaskDuration implements IsSerializable {
		private int m_days;
		private int m_hours;
		private int m_minutes;
		private int m_seconds;
		private int m_weeks;
		
		private final static int SECONDS_PER_MINUTE = 60;
		private final static int MINUTES_PER_HOUR   = 60;
		private final static int HOURS_PER_DAY      = 24;
		private final static int DAYS_PER_WEEK      =  7;

		private final static int  MILLIS_PER_SECOND = 1000;
		private final static int  MILLIS_PER_MINUTE = (SECONDS_PER_MINUTE * MILLIS_PER_SECOND);
		private final static long MILLIS_PER_HOUR   = (MINUTES_PER_HOUR   * MILLIS_PER_MINUTE);
		private final static long MILLIS_PER_DAY    = (HOURS_PER_DAY      * MILLIS_PER_HOUR);
		private final static long MILLIS_PER_WEEK   = (DAYS_PER_WEEK      * MILLIS_PER_DAY);
		  
		/**
		 * Constructor method.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public TaskDuration() {
			// Nothing to do.
		}

		/**
		 * Constructor method.
		 * 
		 * @param s	Seconds.
		 * @param m	Minutes.
		 * @param h	Hours.
		 * @param d	Days.
		 * @param w	Weeks.
		 */
		public TaskDuration(int s, int m, int h, int d, int w) {
			// Construct the base object...
			this();

			// ...and store the parameters.
			setSeconds(s);
			setMinutes(m);
			setHours(  h);
			setDays(   d);
			setWeeks(  w);
		}

		/**
		 * Constructor method.
		 * 
		 * @param d	Days only.
		 */
		public TaskDuration(int d) {
			// Always use the other form of the constructor.
			this(0, 0, 0, d, 0);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public int getDays()    {return m_days;   }
		public int getHours()   {return m_hours;  }
		public int getMinutes() {return m_minutes;}
		public int getSeconds() {return m_seconds;}
		public int getWeeks()   {return m_weeks;  }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setDays(   int days   ) {m_days    = days;   }
		public void setHours(  int hours  ) {m_hours   = hours;  }
		public void setMinutes(int minutes) {m_minutes = minutes;}
		public void setSeconds(int seconds) {m_seconds = seconds;}
		public void setWeeks(  int weeks  ) {m_weeks   = weeks;  }

		/**
		 * Constructs a TaskDuration from the parameters.
		 * 
		 * @param s
		 * @param m
		 * @param h
		 * @param d
		 * @param w
		 * 
		 * @return
		 */
		public static TaskDuration construct(int s, int m, int h, int d, int w) {
			return new TaskDuration(s, m, h, d, w);
		}

		/**
		 * Constructs a TaskDuration from the parameters.
		 * 
		 * @param d
		 * 
		 * @return
		 */
		public static TaskDuration construct(int d) {
			return new TaskDuration(d);
		}
		
		/**
		 * Returns the duration's interval.
		 * 
		 * @return
		 */
		public long getInterval() {
			return
				((m_seconds * MILLIS_PER_SECOND) +
			     (m_minutes * MILLIS_PER_MINUTE) +
			     (m_hours   * MILLIS_PER_HOUR)   +
			     (m_days    * MILLIS_PER_DAY)    +
			     (m_weeks   * MILLIS_PER_WEEK));
		}

		/**
		 * Returns true if the duration only contains a days value and
		 * false otherwise.
		 *
		 * @return
		 */
		public boolean hasDaysOnly() {
			return
				((0 == m_seconds) &&
				 (0 == m_minutes) &&
				 (0 == m_hours)   &&
				 (0 != m_days)    &&
				 (0 == m_weeks));
		}
	}
	
	/**
	 * Inner class used to model the Vibe Event object in a way that's
	 * compatible with GWT RPC calls 
	 */
	public static class TaskEvent implements IsSerializable {
		private boolean			m_allDayEvent;		//
		private boolean			m_endIsCalculated;	//
		private TaskDate		m_actualStart;		//
		private TaskDate		m_actualEnd;		//
		private TaskDate		m_logicalStart;		//
		private TaskDate		m_logicalEnd;		//
		private TaskDuration	m_duration;			//
		
		// The following is used to store information for managing this
		// TaskEvent on the server while updating calculated dates.
		private transient Object	m_serverData;						//
		
		/**
		 * Constructor method.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public TaskEvent() {
			this(false);
		}

		/**
		 * Constructor method.
		 * 
		 * @param empty
		 */
		public TaskEvent(boolean empty) {
			// If we're not creating an empty TaskEvent...
			if (!empty) {
				// ...generate non-null start and end values.
				m_actualStart  = new TaskDate();
				m_actualEnd    = new TaskDate();
				m_logicalStart = new TaskDate();
				m_logicalEnd   = new TaskDate();
			}
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean      getAllDayEvent()     {return m_allDayEvent;    }
		public boolean      getEndIsCalculated() {return m_endIsCalculated;}
		public TaskDate     getActualStart()     {return m_actualStart;    }
		public TaskDate     getActualEnd()       {return m_actualEnd;      }
		public TaskDate     getLogicalStart()    {return m_logicalStart;   }
		public TaskDate     getLogicalEnd()      {return m_logicalEnd;     }
		public TaskDuration getDuration()        {return m_duration;       }
		public Object       getServerData()      {return m_serverData;     }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setAllDayEvent(    boolean      allDayEvent)     {m_allDayEvent     = allDayEvent;    }
		public void setEndIsCalculated(boolean      endIsCalculated) {m_endIsCalculated = endIsCalculated;}
		public void setActualStart(    TaskDate     actualStart)     {m_actualStart     = actualStart;    }
		public void setActualEnd(      TaskDate     actualEnd)       {m_actualEnd       = actualEnd;      }
		public void setLogicalStart(   TaskDate     logicalStart)    {m_logicalStart    = logicalStart;   }
		public void setLogicalEnd(     TaskDate     logicalEnd)      {m_logicalEnd      = logicalEnd;     }
		public void setDuration(       TaskDuration duration)        {m_duration        = duration;       }
		public void setServerData(     Object       serverData)      {m_serverData      = serverData;     }

		/**
		 * Returns true if the TaskDate has an actual end date and
		 * false otherwise.
		 * 
		 * @return
		 */
		public boolean hasActualEnd() {
			return hasDate(getActualEnd());
		}
		
		/**
		 * Returns true if the TaskDate has an actual start date and
		 * false otherwise.
		 * 
		 * @return
		 */
		public boolean hasActualStart() {
			return hasDate(getActualStart());
		}

		/*
		 * Returns true the the TaskDate has a value and false
		 * otherwise.
		 */
		private boolean hasDate(TaskDate d) {
			boolean reply = (null != d);
			if (reply) {
				String dd = d.getDateDisplay();
				reply = ((null != dd) && (0 < dd.length()));
			}
			return reply;
		}

		/**
		 * Returns true if the TaskEvent only as a duration in dates
		 * (no actual start or end) and false otherwise.
		 * 
		 * @return
		 */
		public boolean hasDurationOnly() {
			// Is the task an all day event?
			boolean reply = (!(getAllDayEvent()));
			if (reply) {
				// No!  Is it's duration in days only?
				TaskDuration tD = getDuration();
				reply = tD.hasDaysOnly();
				if (reply) {
					// Yes!  If the task doesn't have an actual start
					// and end date, it only has a duration in days.
					reply = ((!(hasActualEnd())) && (!(hasActualStart())));				
				}
			}
			
			// If we get here, reply is true if the TaskEvent only has
			// a duration in days.  Return it.
			return reply;
		}

		/**
		 * Returns true if the TaskEvent requires some type of task
		 * date calculation and false otherwise.
		 * 
		 * @return
		 */
		public boolean requiresDateCalculations() {
			// Is the task an all day event?
			boolean reply = (!(getAllDayEvent()));
			if (reply) {
				// No!  Is it's duration in days only?
				TaskDuration tD = getDuration();
				reply = tD.hasDaysOnly();
				if (reply) {
					// Yes!  If the task has an actual start and end
					// date, no calculations are required.
					boolean hasStartAndEnd = (hasActualEnd() && hasActualStart());
					reply = (!(hasStartAndEnd));				
				}
			}
			
			// If we get here, reply is true if the TaskEvent requires
			// some date calculation and false otherwise.  Return it.
			return reply;
		}
	}
	
	/**
	 * Inner class used to communicate information about an individual
	 * task.
	 * 
	 * The information contained in this object is constructed using
	 * the information returned from a search for the FolderEntry's to
	 * display from a task folder.  It's basically the same information
	 * displayed by task_folder_list.jsp.
	 */
	public static class TaskInfo implements IsSerializable {
		private boolean					m_canModify;
		private boolean					m_canPurge;
		private boolean					m_canTrash;
		private boolean					m_overdue;	
		private boolean					m_seen;	
		private List<AssignmentInfo>	m_assignments      = new ArrayList<AssignmentInfo>();
		private List<AssignmentInfo>	m_assignmentGroups = new ArrayList<AssignmentInfo>();
		private List<AssignmentInfo>	m_assignmentTeams  = new ArrayList<AssignmentInfo>();
		private Long					m_creatorId;
		private Long					m_modifierId;
		private String					m_completed     = "";	
		private String					m_entityType    = "";
		private String					m_location      = "";
		private String					m_priority      = "";
		private String					m_title         = "";
		private String					m_status        = "";
		private TaskDate				m_completedDate = new TaskDate();	
		private TaskEvent				m_event         = new TaskEvent();
		private EntityId				m_taskId        = new EntityId(null, null, ((String) null), null);	// Initialized when used.
		private String					m_desc			= "";

		// The following are the values used for task completion
		// percentages.
		public final static String COMPLETED_0   = "c000";
		public final static String COMPLETED_10  = "c010";
		public final static String COMPLETED_20  = "c020";
		public final static String COMPLETED_30  = "c030";
		public final static String COMPLETED_40  = "c040";
		public final static String COMPLETED_50  = "c050";
		public final static String COMPLETED_60  = "c060";
		public final static String COMPLETED_70  = "c070";
		public final static String COMPLETED_80  = "c080";
		public final static String COMPLETED_90  = "c090";
		public final static String COMPLETED_100 = "c100";
		
		// The following are the values used for task priorities.
		public final static String PRIORITY_NONE     = "p0";
		public final static String PRIORITY_CRITICAL = "p1";
		public final static String PRIORITY_HIGH     = "p2";
		public final static String PRIORITY_MEDIUM   = "p3";
		public final static String PRIORITY_LOW      = "p4";
		public final static String PRIORITY_LEAST    = "p5";
		
		// The following are the values used for task statuses.
		public final static String STATUS_NEEDS_ACTION = "s1";
		public final static String STATUS_IN_PROCESS   = "s2";
		public final static String STATUS_COMPLETED    = "s3";
		public final static String STATUS_CANCELED     = "s4";
		
		/**
		 * Constructor method.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public TaskInfo() {
			// Nothing to do.
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean              getCanModify()        {return m_canModify;       }
		public boolean              getCanPurge()         {return m_canPurge;        }
		public boolean              getCanTrash()         {return m_canTrash;        }
		public boolean              getOverdue()          {return m_overdue;         }
		public boolean              getSeen()             {return m_seen;            }
		public List<AssignmentInfo> getAssignments()      {return m_assignments;     }
		public List<AssignmentInfo> getAssignmentGroups() {return m_assignmentGroups;}
		public List<AssignmentInfo> getAssignmentTeams()  {return m_assignmentTeams; }
		public Long                 getCreatorId()        {return m_creatorId;       }
		public Long                 getModifierId()       {return m_modifierId;      }
		public String               getCompleted()        {return m_completed;       }
		public String               getEntityType()       {return m_entityType;      }
		public String               getLocation()         {return m_location;        }
		public String               getPriority()         {return m_priority;        }
		public String               getTitle()            {return m_title;           }
		public String               getStatus()           {return m_status;          }
		public TaskDate             getCompletedDate()    {return m_completedDate;   }
		public TaskEvent            getEvent()            {return m_event;           }
		public EntityId             getTaskId()           {return m_taskId;          }
		public String				getDesc()			  {return m_desc;			 }
		
		/**
		 * Set'er methods.
		 * 
		 * @return
		 */
		public void setCanModify(       boolean              canModify)        {m_canModify        = canModify;       }
		public void setCanPurge(        boolean              canPurge)         {m_canPurge         = canPurge;        }
		public void setCanTrash(        boolean              canTrash)         {m_canTrash         = canTrash;        }
		public void setOverdue(         boolean              overdue)          {m_overdue          = overdue;         }
		public void setSeen(            boolean              seen)             {m_seen             = seen;            }
		public void setAssignments(     List<AssignmentInfo> assignments)      {m_assignments      = assignments;     }
		public void setAssignmentGroups(List<AssignmentInfo> assignmentGroups) {m_assignmentGroups = assignmentGroups;}
		public void setAssignmentTeams( List<AssignmentInfo> assignmentTeams)  {m_assignmentTeams  = assignmentTeams; }
		public void setCreatorId(       Long                 creatorId)        {m_creatorId        = creatorId;       }
		public void setModifierId(      Long                 modifierId)       {m_modifierId       = modifierId;      }
		public void setCompleted(       String               completed)        {m_completed        = completed;       }
		public void setEntityType(      String               entityType)       {m_entityType       = entityType;      }
		public void setLocation(        String               location)         {m_location         = location;        }
		public void setPriority(        String               priority)         {m_priority         = priority;        }
		public void setTitle(           String               title)            {m_title            = title;           }
		public void setStatus(          String               status)           {m_status           = status;          }
		public void setCompletedDate(   TaskDate             completedDate)    {m_completedDate    = completedDate;   }
		public void setEvent(           TaskEvent            event)            {m_event            = event;           }
		public void setTaskId(          EntityId             taskId)           {m_taskId           = taskId;          }
		public void setDesc(			String				 desc )			   {m_desc			   = desc;			  }

		/**
		 * Various task state evaluators.
		 * 
		 * @return
		 */
		public boolean isTaskActive() {
			String status = getStatus();
			return (status.equals(STATUS_IN_PROCESS) || status.equals(STATUS_NEEDS_ACTION));
		}
		
		public boolean isTaskCancelled() {
			return getStatus().equals(STATUS_CANCELED);
		}
		
		public boolean isTaskClosed() {
			return (isTaskCompleted() && (!(isTaskCancelled())));
		}
		
		public boolean isTaskCompleted() {
			return getCompleted().equals(COMPLETED_100);
		}
		
		public boolean isTaskOverdue() {
			return (getOverdue() && (!(isTaskCompleted())) && (!(isTaskCancelled())));
		}
		
		public boolean isTaskUnseen() {
			return (!(getSeen()));
		}		
	}
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TaskListItem() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param task
	 */
	public TaskListItem(TaskInfo task) {
		// Initialize this object...
		this();
		
		// ...and store the task.
		setTask(task);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean            getExpandSubtasks() {return m_expandSubtasks;}
	public List<TaskListItem> getSubtasks()       {return m_subtasks;      }
	public TaskInfo           getTask()           {return m_task;          }
	public Object             getUIData()         {return m_uiData;        }
	
	/**
	 * Set'er methods.
	 * 
	 * @return
	 */
	public void setExpandedSubtasks(boolean            expandSubtasks) {m_expandSubtasks = expandSubtasks;}
	public void setSubtasks(        List<TaskListItem> subtasks      ) {m_subtasks       = subtasks;      }
	public void setTask(            TaskInfo           task          ) {m_task           = task;          }
	public void setUIData(          Object             uiData        ) {m_uiData         = uiData;        }

	/**
	 * Appends a TaskListItem as a subtask.
	 * @param task
	 */
	public void appendSubtask(TaskListItem task) {
		m_subtasks.add(task);
	}
}
