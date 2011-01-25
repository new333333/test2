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
package org.kablink.teaming.gwt.client.util;

import java.util.List;

/**
 * Class containing utility methods for manipulating task linkage from
 * the client.
 *  
 * @author drfoster@novell.com
 */
public class TaskLinkageHelper {
	// Enumeration used to tell moveTaskInDirection() which direction a
	// TaskListItem is to be moved within a TaskLinkageHelper object.
	private enum Direction {
		UP,
		DOWN,
		LEFT,	// Decreases the subtask level.
		RIGHT,	// Increases the subtask level.
	}

	/*
	 * Class constructor.
	 */
	private TaskLinkageHelper() {
		// Inhibits this class from being instantiated.
	}

	/**
	 * Returns true if the given task can be moved down from where
	 * its at.
	 * 
	 * @param tb
	 * @param taskId
	 * 
	 * @return
	 */
	public static boolean canMoveTaskDown(TaskBundle tb, Long taskId) {
		TaskListItem       task  = findTask(tb, taskId);
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (index < (tasks.size() - 1));
		
		return reply;
	}
	
	/**
	 * Returns true if the given task can be moved left from where
	 * its at.
	 * 
	 * @param tb
	 * @param taskId
	 * 
	 * @return
	 */
	public static boolean canMoveTaskLeft(TaskBundle tb, Long taskId) {
		TaskListItem       task  = findTask(tb, taskId);
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = ((0 == index) && tasks != tb.getTasks());
		
		return reply;
	}
	
	/**
	 * Returns true if the given task can be moved right from where
	 * its at.
	 * 
	 * @param tb
	 * @param taskId
	 * 
	 * @return
	 */
	public static boolean canMoveTaskRight(TaskBundle tb, Long taskId) {
		TaskListItem       task  = findTask(tb, taskId);
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (0 < index);
		
		return reply;
	}
	
	/**
	 * Returns true if the given task can be moved up from where
	 * its at.
	 * 
	 * @param tb
	 * @param taskId
	 * 
	 * @return
	 */
	public static boolean canMoveTaskUp(TaskBundle tb, Long taskId) {
		TaskListItem       task  = findTask(tb, taskId);
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (0 < index);
		
		return reply;
	}
	
	/**
	 * Searches a task's subtasks for the given ID.
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public static TaskListItem findSubtask(TaskListItem task, Long id) {
		for (TaskListItem taskScan:  task.getSubtasks()) {
			if (id.equals(taskScan.getTask().getTaskId())) {
				return taskScan;
			}
			
			TaskListItem reply = findSubtask(taskScan, id);
			if (null != reply) {
				return reply;
			}
		}
		
		return null;
	}

	/**
	 * Searches a task's subtasks for the given ID and returns
	 * the List<TaskListItem> that contains it.  Returns null if the
	 * TaskListItem cannot be found.
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public static List<TaskListItem> findSubtaskList(TaskListItem task, Long id) {
		for (TaskListItem taskScan:  task.getSubtasks()) {
			if (id.equals(taskScan.getTask().getTaskId())) {
				return task.getSubtasks();
			}
			
			List<TaskListItem> reply = findSubtaskList(taskScan, id);
			if (null != reply) {
				return reply;
			}
		}
		
		return null;
	}

	/**
	 * Returns the TaskListItem with the given ID from this TaskLinkageHelper.
	 * 
	 * @param tb
	 * @param id
	 * 
	 * @return
	 */	
	public static TaskListItem findTask(TaskBundle tb, Long id) {
		// Scan the TaskListItem's in this TaskLinkageHelper.
		for (TaskListItem taskScan:  tb.getTasks()) {
			// Is this the TaskListItem in question?
			if (taskScan.getTask().getTaskId().equals(id)) {
				// Yes!  Return it.
				return taskScan;
			}

			// Is this ID for a subtask of this TaskListItem?
			TaskListItem reply = findSubtask(taskScan, id);
			if (null != reply) {
				// Yes!  Return it.
				return reply;
			}
		}

		// If we get here, we couldn't find a TaskListItem with the given
		// ID.  Return null.
		return null;
	}

	/**
	 * Returns the List<TaskListItem> containing the given ID from this
	 * TaskLinkageHelper.
	 * 
	 * @param tb
	 * @param id
	 * 
	 * @return
	 */	
	public static List<TaskListItem> findTaskList(TaskBundle tb, Long id) {
		// Scan the TaskListItem's in this TaskLinkageHelper.
		for (TaskListItem taskScan:  tb.getTasks()) {
			// Is this the TaskListItem in question?
			if (taskScan.getTask().getTaskId().equals(id)) {
				// Yes!  Return it.
				return tb.getTasks();
			}

			// Is this ID for a subtask of this TaskListItem?
			List<TaskListItem> reply = findSubtaskList(taskScan, id);
			if (null != reply) {
				// Yes!  Return it.
				return reply;
			}
		}

		// If we get here, we couldn't find a TaskListItem with the given
		// ID.  Return null.
		return null;
	}
	
	public static List<TaskListItem> findTaskList(TaskBundle tb, TaskListItem tl) {
		// Always use the initial form of the method.
		return findTaskList(tb, tl.getTask().getTaskId());
	}

	/*
	 * Searches the TaskListItem's in tlList2Search for a TaskListItem contain
	 * tlList2Find as its subtask list.  If found, that TaskListItem is
	 * returned.  Otherwise, null is returned.
	 */
	private static TaskListItem findTaskListItemContainingList(List<TaskListItem> tlList2Search, List<TaskListItem> tlList2Find) {
		// Scan the TaskListItem's in the List<TaskListItem> to search.
		for (TaskListItem tl:  tlList2Search) {
			// If this TaskListItem's subtask List<TaskListItem> is the list in
			// question...
			List<TaskListItem> subtasks = tl.getSubtasks();
			if (subtasks == tlList2Find) {
				// ...return the TaskListItem.
				return tl;
			}

			// If the list we're looking for is a subtask of this
			// TaskListItem's subtasks...
			TaskListItem reply = findTaskListItemContainingList(subtasks, tlList2Find);
			if (null != reply) {
				// ...return that TaskListItem.
				return reply;
			}
		}

		// If we get here, we couldn't find tlList2Find in
		// tlList2Search.  Return null.
		return null;
	}

	/**
	 * Move one TaskListItem above another.
	 * 
	 * @param tb
	 * @param tlMoveThis
	 * @param tlRelativeToThis
	 */
	public static void moveTaskAbove(TaskBundle tb, TaskListItem tlMoveThis, TaskListItem tlRelativeToThis) {
		// Find the List<TaskListItem>'s we're moving from and to...
		List<TaskListItem> tlFrom = findTaskList(tb, tlMoveThis);
		List<TaskListItem> tlTo   = findTaskList(tb, tlRelativeToThis);

		// ...and perform the move.
		tlFrom.remove(tlMoveThis);
		tlTo.add(tlTo.indexOf(tlRelativeToThis), tlMoveThis);
	}
	
	public static void moveTaskAbove(TaskBundle tb, Long idMoveThis, Long idRelativeToThis) {
		// Always use the initial form of the method.
		moveTaskAbove(tb, findTask(tb, idMoveThis), findTask(tb, idRelativeToThis));
	}
	
	/**
	 * Moves one TaskListItem below another.
	 * 
	 * @param tb
	 * @param tlMoveThis
	 * @param tlRelativeToThis
	 */
	public static void moveTaskBelow(TaskBundle tb, TaskListItem tlMoveThis, TaskListItem tlRelativeToThis) {
		// Find the List<TaskListItem>'s we're moving from and to...
		List<TaskListItem> tlFrom = findTaskList(tb, tlMoveThis);
		List<TaskListItem> tlTo   = findTaskList(tb, tlRelativeToThis);

		// ...and perform the move.
		tlFrom.remove(tlMoveThis);
		int toIndex = (tlTo.indexOf(tlRelativeToThis) + 1);
		if (tlTo.size() == toIndex)
		     tlTo.add(         tlMoveThis);
		else tlTo.add(toIndex, tlMoveThis);
	}
	
	public static void moveTaskBelow(TaskBundle tb, Long idMoveThis, Long idRelativeToThis) {
		// Always use the initial form of the method.
		moveTaskBelow(tb, findTask(tb, idMoveThis), findTask(tb, idRelativeToThis));
	}
	
	/**
	 * Moves a TaskListItem down from its current position.
	 * 
	 * @param tb
	 * @param tl
	 */
	public static void moveTaskDown(TaskBundle tb, TaskListItem tl) {
		moveTaskInDirection(tb, tl, tb.getTasks(), Direction.DOWN);
	}
	
	public static void moveTaskDown(TaskBundle tb, Long taskId) {
		// Always use the initial form of the method.
		moveTaskDown(tb, findTask(tb, taskId));
	}
	
	/*
	 * Moves a TaskListItem in the direction specified relative to the
	 * other TaskListItem's in this TaskLinkageHelper.
	 * 
	 * Returns true if we found the entry to move in the supplied
	 * List<TaskListItem> (even if we didn't move it) and false otherwise.
	 * 
	 * Note:  Moving up/down/in are all fairly intuitive.  Moving out,
	 *    however, is not as what's implemented here works the same as
	 *    GroupWise's Tasklist feature.
	 */
	private static boolean moveTaskInDirection(TaskBundle tb, TaskListItem tl, List<TaskListItem> tlList, Direction dir) {
		int tlIndex;
		int tlSize = tlList.size();
		TaskListItem tlScan = null;

		// Scan the TaskListItem's in the List<TaskListItem>. 
		for (tlIndex = 0; tlIndex < tlSize; tlIndex += 1) {
			// Is this the TaskListItem we need to move?
			tlScan = tlList.get(tlIndex);
			if (tlScan.getTask().getTaskId().equals(tl.getTask().getTaskId())) {
				// Yes!  Break out of the scan loop.
				break;
			}

			// Can we perform the move out of this TaskListItem's subtasks?
			if (moveTaskInDirection(tb, tl, tlScan.getSubtasks(), dir)) {
				// Yes!  Then we're done.
				return true;
			}
		}
		
		// If we couldn't find the TaskListItem in question...
		if (tlIndex == tlSize) {
			// ...there's nothing to move.
			return false;
		}

		// What direction are we to move the TaskListItem?
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
			
		case LEFT:
			// Left (i.e., decrease its subtask level)!  If it's in
			// other than the outer most task order list...
			if (tlList != tb.getTasks()) {
				// ...move all of the peer subtasks below it to be
				// ...subtasks of it...
				for (int i = (tlIndex + 1); i < tlSize; tlSize -= 1) {
					TaskListItem tl2Move = tlList.get(i);
					tlList.remove(i);
					tlScan.appendSubtask(tl2Move);
				}
				
				// ...and make it a peer to the task it's a subtask of,
				// ...immediately below what was its parent.
				tlList.remove(tlIndex);
				TaskListItem tlWithList = findTaskListItemContainingList(tb.getTasks(), tlList);
				tlWithList.appendSubtask(tlScan);
			}
			
			break;
			
		case RIGHT:
			// Right (i.e., increase its subtask level)!  If it's not
			// the first task in the list...
			if (0 < tlIndex) {
				// ...make it the last subtask of the one above it.
				tlList.remove(tlIndex);
				tlList.get(tlIndex - 1).appendSubtask(tlScan);
			}
			
			break;
		}

		// If we get here, we found the TaskListItem in the List<TaskListItem>,
		// even if we didn't move it.  Return true.
		return true;
	}
	
	/**
	 * Moves one TaskListItem into another.
	 * 
	 * @param tb
	 * @param tlMoveThis
	 * @param tlTarget
	 * @param targetIndex
	 */
	public static void moveTaskInto(TaskBundle tb, TaskListItem tlMoveThis, TaskListItem tlTarget, int targetIndex) {		
		// Find the List<TaskListItem>'s we're moving from and to...
		List<TaskListItem> tlFrom = findTaskList(tb, tlMoveThis);
		List<TaskListItem> tlTo   = findTaskList(tb, tlTarget);

		// ...and perform the move.
		tlFrom.remove(tlMoveThis);
		if ((-1) == targetIndex)
		     tlTo.add(             tlMoveThis);
		else tlTo.add(targetIndex, tlMoveThis);
	}
	
	public static void moveTaskInto(TaskBundle tb, TaskListItem tlMoveThis, TaskListItem tlTarget) {
		// Always use the initial form of the method.
		moveTaskInto(tb, tlMoveThis, tlTarget, 0);
	}
	
	public static void moveTaskInto(TaskBundle tb, Long idMoveThis, Long idTarget, int targetIndex) {
		// Always use the initial form of the method.
		moveTaskInto(tb, findTask(tb, idMoveThis), findTask(tb, idTarget), targetIndex);
	}
	
	public static void moveTaskInto(TaskBundle tb, Long idMoveThis, Long idTarget) {
		// Always use the initial form of the method.
		moveTaskInto(tb, findTask(tb, idMoveThis), findTask(tb, idTarget), 0);
	}
	
	/**
	 * Moves a TaskListItem left from its current position.
	 * 
	 * @param tb
	 * @param tl
	 */
	public static void moveTaskLeft(TaskBundle tb, TaskListItem tl) {
		moveTaskInDirection(tb, tl, tb.getTasks(), Direction.LEFT);
	}
	
	public static void moveTaskLeft(TaskBundle tb, Long taskId) {
		// Always use the initial form of the method.
		moveTaskLeft(tb, findTask(tb, taskId));
	}
	
	/**
	 * Moves a TaskListItem right from its current position.
	 * 
	 * @param tb
	 * @param tl
	 */
	public static void moveTaskRight(TaskBundle tb, TaskListItem tl) {
		moveTaskInDirection(tb, tl, tb.getTasks(), Direction.RIGHT);
	}
	
	public static void moveTaskRight(TaskBundle tb, Long taskId) {
		// Always use the initial form of the method.
		moveTaskRight(tb, findTask(tb, taskId));
	}
	
	/**
	 * Moves a TaskListItem up from its current position.
	 * 
	 * @param tb
	 * @param tl
	 */
	public static void moveTaskUp(TaskBundle tb, TaskListItem tl) {
		moveTaskInDirection(tb, tl, tb.getTasks(), Direction.UP);
	}
	
	public static void moveTaskUp(TaskBundle tb, Long taskId) {
		// Always use the initial form of the method.
		moveTaskUp(tb, findTask(tb, taskId));
	}
}
