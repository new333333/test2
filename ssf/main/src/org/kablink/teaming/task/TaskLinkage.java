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
package org.kablink.teaming.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class used to represent order and hierarchy for the tasks within a
 * task folder.
 *  
 * @author drfoster
 */
@SuppressWarnings("serial")
public class TaskLinkage implements Serializable {
	private static Log m_logger = LogFactory.getLog(TaskLinkage.class);
	
	private List<TaskLink> m_taskOrder;	// Ordered list of the ID's of task FolderEntry's tracked by this TaskLinkage.
	
	// Enumeration used to tell moveTaskImpl() which direction a
	// TaskLink is to be moved within a TaskLinkage object.
	private enum Direction {
		UP,
		DOWN,
		IN,		// Increases the subtask level.
		OUT,	// Decreases the subtask level.
	}
	
	/**
	 * Inner class use used to track individual tasks within a
	 * TaskLinkage object.
	 */
	public static class TaskLink implements Serializable {
		private Long			m_taskId;	// ID of the FolderEntry of this task
		private List<TaskLink>	m_subtasks;	// List<TaskLink> of the subtasks of this task.

		/**
		 * Class constructor.
		 * 
		 * @param taskId
		 */
		public TaskLink(Long taskId) {
			m_taskId   = taskId;
			m_subtasks = new ArrayList<TaskLink>();
		}

		/**
		 * Appends a new TaskLink to this TaskLink's subtask list.
		 * 
		 * @param tl
		 */		
		public void appendSubtask(TaskLink tl) {
			m_subtasks.add(tl);
		}
		
		public void appendSubtask(Long id) {
			// Always use the initial form of the method.
			appendSubtask(new TaskLink(id));
		}
		
		/**
		 * Searches this tasks subtasks for the given ID.
		 * 
		 * @param id
		 * 
		 * @return
		 */
		public TaskLink findSubtask(Long id) {
			for (TaskLink tl:  m_subtasks) {
				if (id.equals(tl.getTaskId())) {
					return tl;
				}
				
				TaskLink reply = tl.findSubtask(id);
				if (null != reply) {
					return reply;
				}
			}
			
			return null;
		}

		/**
		 * Returns the List<TaskLink> of subtasks from this TaskLink.
		 * 
		 * @return
		 */
		public List<TaskLink> getSubtasks() {
			return m_subtasks;
		}
		
		/**
		 * Returns the ID from this TaskLink.
		 * 
		 * @return
		 */
		public Long getTaskId() {
			return m_taskId;
		}

		/**
		 * Returns true if this TaskLink has subtasks and false
		 * otherwise.
		 * @return
		 */
		public boolean hasSubtasks() {
			return (!(m_subtasks.isEmpty()));
		}
	}
	
	/**
	 * Class constructor.
	 */
	public TaskLinkage() {
		m_taskOrder = new ArrayList<TaskLink>();
	}

	/**
	 * Appends a new TaskLink to this TaskLinkage's task ordering list.
	 * 
	 * @param tl
	 */		
	public void appendTask(TaskLink tl) {
		m_taskOrder.add(tl);
	}
	
	public void appendTask(Long id) {
		// Always use the initial form of the method.
		appendTask(new TaskLink(id));
	}

	/**
	 * Logs the contents of this TaskLinkage object.
	 */
	public void debugDump() {
		// If debug logging is disabled...
		if (!(m_logger.isDebugEnabled())) {
			// ...bail.
			return;
		}

		// If there are no TaskLink's in the task order list...
		if (m_taskOrder.isEmpty()) {
			// ...log that fact and bail.
			m_logger.debug("TaskLinkage.debugDump( EMPTY )");
			return;
		}

		// Scan the TaskLink's in the task order list...
		m_logger.debug("TaskLinkage.debugDump( START )");
		for (TaskLink tl:  m_taskOrder) {
			// ...logging each of them.
			debugDump(tl, 0);
		}
		m_logger.debug("TaskLinkage.debugDump( END )");
	}

	/*
	 * Logs the contents of the supplied TaskLink.  The information is
	 * indented depth levels.
	 */
	private static void debugDump(TaskLink tl, int depth) {
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
			debugDump(tlScan, subtaskDepth);
		}
	}
	
	/**
	 * Returns the TaskLink with the given ID from this TaskLinkage.
	 * 
	 * @param id
	 * @param searchSubtasks
	 * 
	 * @return
	 */	
	public TaskLink findTask(Long id, boolean searchSubtasks) {
		// Scan the TaskLink's in this TaskLinkage.
		for (TaskLink tl:  m_taskOrder) {
			// Is this the TaskLink in question?
			if (tl.getTaskId().equals(id)) {
				// Yes!  Return it.
				return tl;
			}

			// Are we supposed to search the subtasks? 
			if (searchSubtasks) {
				// Yes!  Is this ID for a subtask of this TaskLink?
				TaskLink reply = tl.findSubtask(id);
				if (null != reply) {
					// Yes!  Return it.
					return reply;
				}
			}
		}

		// If we get here, we couldn't find a TaskLink with the given
		// ID.  Return null.
		return null;
	}
	
	public TaskLink findTask(Long id) {
		// Always use the initial form of the method.
		return findTask(id, false);
	}

	/*
	 * Searches the TaskLink's in tlList2Search for a TaskLink contain
	 * tlList2Find as its subtask list.  If found, that TaskLink is
	 * returned.  Otherwise, null is returned.
	 */
	private static TaskLink findTaskLinkContainingList(List<TaskLink> tlList2Search, List<TaskLink> tlList2Find) {
		// Scan the TaskLink's in the List<TaskLink> to search.
		for (TaskLink tl:  tlList2Search) {
			// If this TaskLink's subtask List<TaskLink> is the list in
			// question...
			List<TaskLink> subtasks = tl.getSubtasks();
			if (subtasks == tlList2Find) {
				// ...return the TaskLink.
				return tl;
			}

			// If the list we're looking for is a subtask of this
			// TaskLink's subtasks...
			TaskLink reply = findTaskLinkContainingList(subtasks, tlList2Find);
			if (null != reply) {
				// ...return that TaskLink.
				return reply;
			}
		}

		// If we get here, we couldn't find tlList2Find in
		// tlList2Search.  Return null.
		return null;
	}

	
	/**
	 * Returns the List<TaskLink> of order from this TaskLinkage.
	 * 
	 * @return
	 */
	public List<TaskLink> getTaskOrder() {
		return m_taskOrder;
	}

	/**
	 * Moves a TaskLink down from its current position.
	 * 
	 * @param tl
	 */
	public void moveTaskDown(TaskLink tl) {
		moveTaskImpl(tl, m_taskOrder, Direction.DOWN);
	}
	
	public void moveTaskDown(Long taskId) {
		// Always use the initial form of the method.
		moveTaskDown(findTask(taskId));
	}
	
	/**
	 * Moves a TaskLink in from its current position.
	 * 
	 * @param tl
	 */
	public void moveTaskIn(TaskLink tl) {
		moveTaskImpl(tl, m_taskOrder, Direction.IN);
	}
	
	public void moveTaskIn(Long taskId) {
		// Always use the initial form of the method.
		moveTaskIn(findTask(taskId));
	}
	
	/**
	 * Moves a TaskLink out from its current position.
	 * 
	 * @param tl
	 */
	public void moveTaskOut(TaskLink tl) {
		moveTaskImpl(tl, m_taskOrder, Direction.OUT);
	}
	
	public void moveTaskOut(Long taskId) {
		// Always use the initial form of the method.
		moveTaskOut(findTask(taskId));
	}
	
	/**
	 * Moves a TaskLink up from its current position.
	 * 
	 * @param tl
	 */
	public void moveTaskUp(TaskLink tl) {
		moveTaskImpl(tl, m_taskOrder, Direction.UP);
	}
	
	public void moveTaskUp(Long taskId) {
		// Always use the initial form of the method.
		moveTaskUp(findTask(taskId));
	}
	
	/*
	 * Moves a TaskLink in the direction specified relative to the
	 * other TaskLink's in this TaskLinkage object.
	 * 
	 * Returns true if we found the entry to move in the supplied
	 * List<TaskLink> (even if we didn't move it) and false otherwise.
	 * 
	 * Note:  Moving up/down/in are all fairly intuitive.  Moving out,
	 *    however, is not as what's implemented here works the same as
	 *    GroupWise's Tasklist feature.
	 */
	private boolean moveTaskImpl(TaskLink tl, List<TaskLink> tlList, Direction dir) {
		int tlIndex;
		int tlSize = tlList.size();
		TaskLink tlScan = null;

		// Scan the TaskLink's in the List<TaskLink>. 
		for (tlIndex = 0; tlIndex < tlSize; tlIndex += 1) {
			// Is this the TaskLink we need to move?
			tlScan = tlList.get(tlIndex);
			if (tlScan.getTaskId().equals(tl.getTaskId())) {
				// Yes!  Break out of the scan loop.
				break;
			}

			// Can we perform the move out of this TaskLink's subtasks?
			if (moveTaskImpl(tl, tlScan.getSubtasks(), dir)) {
				// Yes!  Then we're done.
				return true;
			}
		}
		
		// If we couldn't find the TaskLink in question...
		if (tlIndex == tlSize) {
			// ...there's nothing to move.
			return false;
		}

		// What direction are we to move the TaskLink?
		switch (dir) {
		case UP:
			// Up!  If it's not the first task in the list...
			if (0 < tlIndex) {
				// ...move it up a notch.
				tlList.remove(tlIndex);
				tlList.add((tlIndex - 1), tlScan);
			}

			break;
			
		case DOWN:
			// Down!  If it's not the last task in the list...
			if (tlIndex < tlSize) {
				// ...move it down a notch.
				tlList.remove(tlIndex);
				if (tlIndex == (tlSize - 1)) tlList.add(               tlScan);
				else                         tlList.add((tlIndex + 1), tlScan);
			}
			
			break;
			
		case IN:
			// In (i.e., increase its subtask level)!  If it's not the
			// first task in the list...
			if (0 < tlIndex) {
				// ...make it the last subtask of the one above it.
				tlList.remove(tlIndex);
				tlList.get(tlIndex - 1).appendSubtask(tlScan);
			}
			
			break;
			
		case OUT:
			// Out (i.e., decrease its subtask level)!  If it's in
			// other than the outer most task order list...
			if (tlList != m_taskOrder) {
				// ...move all of the peer subtasks below it to be
				// ...subtasks of it...
				for (int i = (tlIndex + 1); i < tlSize; tlSize -= 1) {
					TaskLink tl2Move = tlList.get(i);
					tlList.remove(i);
					tlScan.appendSubtask(tl2Move);
				}
				
				// ...and make it a peer to the task it's a subtask of,
				// ...immediately below what was its parent.
				tlList.remove(tlIndex);
				TaskLink tlWithList = findTaskLinkContainingList(m_taskOrder, tlList);
				tlWithList.appendSubtask(tlScan);
			}
			
			break;
		}

		// If we get here, we found the TaskLink in the List<TaskLink>,
		// even if we didn't move it.  Return true.
		return true;
	}
}
