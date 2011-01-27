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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.util.TaskLinkage.TaskLink;

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
	 * Uses the current contents of m_tasks to update the contained
	 * TaskLinkage.
	 * 
	 * @param tb
	 * 
	 * @return
	 */
	public static TaskLinkage buildLinkage(TaskBundle tb) {
		// Create a new TaskLinkage to hold the updates.
		TaskLinkage reply = new TaskLinkage();
		
		// Scan the current TaskListItem's in m_tasks...
		for (TaskListItem taskScan:  tb.getTasks()) {
			// ...adding a new TaskLink to the linkage...
			TaskLink newTaskLink = new TaskLink();
			newTaskLink.setTaskId(taskScan.getTask().getTaskId());
			reply.appendTask(newTaskLink);

			// ...and updating its subtasks.
			buildSubtaskLinkage(newTaskLink, taskScan);
		}
		
		// If we get here, reply refers to the updated TaskLinkage.
		// Return it.
		return reply;
	}
	
	private static void buildSubtaskLinkage(TaskLink taskLink, TaskListItem task) {
		// Scan the subtasks of the TaskListItem.
		for (TaskListItem taskScan:  task.getSubtasks()) {
			// ...adding a new TaskLink to the subtask linkage...
			TaskLink newTaskLink = new TaskLink();
			newTaskLink.setTaskId(taskScan.getTask().getTaskId());
			taskLink.appendSubtask(newTaskLink);
			
			// ...and updating its subtasks.
			buildSubtaskLinkage(newTaskLink, taskScan);
		}
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
	public static boolean canMoveTaskDown(TaskBundle tb, TaskListItem task) {
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (index < (tasks.size() - 1));
		
		return reply;
	}
	
	public static boolean canMoveTaskDown(TaskBundle tb, Long taskId) {
		return canMoveTaskDown(tb, findTask(tb, taskId));
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
	public static boolean canMoveTaskLeft(TaskBundle tb, TaskListItem task) {
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = ((0 == index) && tasks != tb.getTasks());
		
		return reply;
	}
	
	public static boolean canMoveTaskLeft(TaskBundle tb, Long taskId) {
		return canMoveTaskLeft(tb, findTask(tb, taskId));
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
	public static boolean canMoveTaskRight(TaskBundle tb, TaskListItem task) {
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (0 < index);
		
		return reply;
	}
	
	public static boolean canMoveTaskRight(TaskBundle tb, Long taskId) {
		return canMoveTaskRight(tb, findTask(tb, taskId));
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
	public static boolean canMoveTaskUp(TaskBundle tb, TaskListItem task) {
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (0 < index);
		
		return reply;
	}
	
	public static boolean canMoveTaskUp(TaskBundle tb, Long taskId) {
		return canMoveTaskUp(tb, findTask(tb, taskId));
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
	
	public static List<TaskListItem> findTaskList(TaskBundle tb, TaskListItem tli) {
		// Always use the initial form of the method.
		return findTaskList(tb, tli.getTask().getTaskId());
	}

	/*
	 * Searches the TaskListItem's in tliList2Search for a TaskListItem
	 * contain tliList2Find as its subtask list.  If found, that
	 * TaskListItem is returned.  Otherwise, null is returned.
	 */
	private static TaskListItem findTaskListItemContainingList(List<TaskListItem> tliList2Search, List<TaskListItem> tliList2Find) {
		// Scan the TaskListItem's in the List<TaskListItem> to search.
		for (TaskListItem tli:  tliList2Search) {
			// If this TaskListItem's subtask List<TaskListItem> is the list in
			// question...
			List<TaskListItem> subtasks = tli.getSubtasks();
			if (subtasks == tliList2Find) {
				// ...return the TaskListItem.
				return tli;
			}

			// If the list we're looking for is a subtask of this
			// TaskListItem's subtasks...
			TaskListItem reply = findTaskListItemContainingList(subtasks, tliList2Find);
			if (null != reply) {
				// ...return that TaskListItem.
				return reply;
			}
		}

		// If we get here, we couldn't find tliList2Find in
		// tliList2Search.  Return null.
		return null;
	}

	/*
	 * Searches the TaskListItem's in tliList2Search for a TaskListItem
	 * contain tli2Find as one of its subtasks.  If found, that
	 * TaskListItem is returned.  Otherwise, null is returned.
	 */
	private static TaskListItem findTaskListItemContainingTask(List<TaskListItem> tliList2Search, TaskListItem tli2Find) {
		// Scan the TaskListItem's in the List<TaskListItem> to search.
		for (TaskListItem tli:  tliList2Search) {
			// If this TaskListItem's subtask List<TaskListItem> is the list in
			// question...
			List<TaskListItem> subtasks = tli.getSubtasks();
			for (TaskListItem subtask:  subtasks) {
				if (subtask == tli2Find) {
					// ...return the TaskListItem.
					return tli;
				}
			}

			// If the list we're looking for is a subtask of this
			// TaskListItem's subtasks...
			TaskListItem reply = findTaskListItemContainingTask(subtasks, tli2Find);
			if (null != reply) {
				// ...return that TaskListItem.
				return reply;
			}
		}

		// If we get here, we couldn't find tliList2Find in
		// tlList2Search.  Return null.
		return null;
	}

	/**
	 * Returns a List<Long> containing the ID of the given task and the
	 * IDss of all subtasks below it.
	 * 
	 * @param tli
	 * 
	 * @return
	 */
	public static List<Long> getTaskIdHierarchy(TaskListItem tli) {
		List<Long> reply = new ArrayList<Long>();
		
		reply.add(tli.getTask().getTaskId());
		getTaskIdHierarchyImpl(tli.getSubtasks(), reply);
		
		return reply;
	}
	
	private static void getTaskIdHierarchyImpl(List<TaskListItem> tliList, List<Long> tliHierarchy) {
		for (TaskListItem tli:  tliList) {
			tliHierarchy.add(tli.getTask().getTaskId());
			getTaskIdHierarchyImpl(tli.getSubtasks(), tliHierarchy);
		}		
	}
	
	/**
	 * Move one TaskListItem above another.
	 * 
	 * @param tb
	 * @param tliMoveThis
	 * @param tliRelativeToThis
	 */
	public static void moveTaskAbove(TaskBundle tb, TaskListItem tliMoveThis, TaskListItem tliRelativeToThis) {
		// Find the List<TaskListItem>'s we're moving from and to...
		List<TaskListItem> tliFromList = findTaskList(tb, tliMoveThis);
		List<TaskListItem> tliToList   = findTaskList(tb, tliRelativeToThis);

		// ...and perform the move.
		tliFromList.remove(tliMoveThis);
		tliToList.add(tliToList.indexOf(tliRelativeToThis), tliMoveThis);
	}
	
	public static void moveTaskAbove(TaskBundle tb, Long idMoveThis, Long idRelativeToThis) {
		// Always use the initial form of the method.
		moveTaskAbove(tb, findTask(tb, idMoveThis), findTask(tb, idRelativeToThis));
	}
	
	/**
	 * Moves one TaskListItem below another.
	 * 
	 * @param tb
	 * @param tliMoveThis
	 * @param tliRelativeToThis
	 */
	public static void moveTaskBelow(TaskBundle tb, TaskListItem tliMoveThis, TaskListItem tliRelativeToThis) {
		// Find the List<TaskListItem>'s we're moving from and to...
		List<TaskListItem> tliFromList = findTaskList(tb, tliMoveThis);
		List<TaskListItem> tliToList   = findTaskList(tb, tliRelativeToThis);

		// ...and perform the move.
		tliFromList.remove(tliMoveThis);
		int toIndex = (tliToList.indexOf(tliRelativeToThis) + 1);
		if (tliToList.size() == toIndex)
		     tliToList.add(         tliMoveThis);
		else tliToList.add(toIndex, tliMoveThis);
	}
	
	public static void moveTaskBelow(TaskBundle tb, Long idMoveThis, Long idRelativeToThis) {
		// Always use the initial form of the method.
		moveTaskBelow(tb, findTask(tb, idMoveThis), findTask(tb, idRelativeToThis));
	}
	
	/**
	 * Moves a TaskListItem down from its current position.
	 * 
	 * @param tb
	 * @param tli
	 */
	public static void moveTaskDown(TaskBundle tb, TaskListItem tli) {
		moveTaskInDirection(tb, tli, tb.getTasks(), Direction.DOWN);
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
	private static boolean moveTaskInDirection(TaskBundle tb, TaskListItem tliMoveThis, List<TaskListItem> tliList, Direction dir) {
		// Scan the TaskListItem's in the List<TaskListItem>. 
		int tliMoveThisIndex;
		int tliSize = tliList.size();
		for (tliMoveThisIndex = 0; tliMoveThisIndex < tliSize; tliMoveThisIndex += 1) {
			// Is this the TaskListItem we need to move?
			TaskListItem tliScan = tliList.get(tliMoveThisIndex);
			if (tliScan == tliMoveThis) {
				// Yes!  Break out of the scan loop.
				break;
			}

			// Can we perform the move out of this TaskListItem's subtasks?
			if (moveTaskInDirection(tb, tliMoveThis, tliScan.getSubtasks(), dir)) {
				// Yes!  Then we're done.
				return true;
			}
		}
		
		// If we couldn't find the TaskListItem in question...
		if (tliMoveThisIndex == tliSize) {
			// ...there's nothing to move.
			return false;
		}

		// What direction are we to move the TaskListItem?
		switch (dir) {
		case UP:
			// Up!  If it's not the first task in the list...
			if (0 < tliMoveThisIndex) {
				// ...move it up a notch.
				tliList.remove(tliMoveThisIndex                  );
				tliList.add(  (tliMoveThisIndex - 1), tliMoveThis);
			}

			break;
			
		case DOWN:
			// Down!  If it's not the last task in the list...
			if (tliMoveThisIndex < tliSize) {
				// ...move it down a notch.
				tliList.remove(tliMoveThisIndex);
				if (tliMoveThisIndex == (tliSize - 1)) tliList.add(                        tliMoveThis);
				else                                   tliList.add((tliMoveThisIndex + 1), tliMoveThis);
			}
			
			break;
			
		case LEFT:
			// Left (i.e., decrease its subtask level)!  If it's in
			// other than the outer most task order list...
			if (tliList != tb.getTasks()) {
				// ...move all of the peer subtasks below it to be
				// ...subtasks of it...
				for (int i = (tliMoveThisIndex + 1); i < tliSize; tliSize -= 1) {
					TaskListItem tli2Move = tliList.get(i);
					tliList.remove(i);
					tliMoveThis.appendSubtask(tli2Move);
				}
				
				// ...and make it a peer to the task it's a subtask of,
				// ...immediately below what was its parent.
				tliList.remove(tliMoveThisIndex);
				TaskListItem tliWithList          = findTaskListItemContainingList(tb.getTasks(), tliList    );
				TaskListItem tliWithListContainer = findTaskListItemContainingTask(tb.getTasks(), tliWithList);
				List<TaskListItem> tliTargetList  = ((null == tliWithListContainer) ? tb.getTasks() : tliWithListContainer.getSubtasks());
				tliTargetList.add(
					(tliTargetList.indexOf(tliWithList) + 1),
					tliMoveThis);
			}
			
			break;
			
		case RIGHT:
			// Right (i.e., increase its subtask level)!  If it's not
			// the first task in the list...
			if (0 < tliMoveThisIndex) {
				// ...make it the last subtask of the one above it.
				tliList.remove(tliMoveThisIndex);
				tliList.get(   tliMoveThisIndex - 1).appendSubtask(tliMoveThis);
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
	 * @param tliMoveThis
	 * @param tliTarget
	 * @param targetIndex
	 */
	public static void moveTaskInto(TaskBundle tb, TaskListItem tliMoveThis, TaskListItem tliTarget, int targetIndex) {		
		// Find the List<TaskListItem>'s we're moving from and to...
		List<TaskListItem> tliFromList = findTaskList(tb, tliMoveThis);
		List<TaskListItem> tliToList   = findTaskList(tb, tliTarget);

		// ...and perform the move.
		tliFromList.remove(tliMoveThis);
		if ((-1) == targetIndex)
		     tliToList.add(             tliMoveThis);
		else tliToList.add(targetIndex, tliMoveThis);
	}
	
	public static void moveTaskInto(TaskBundle tb, TaskListItem tliMoveThis, TaskListItem tliTarget) {
		// Always use the initial form of the method.
		moveTaskInto(tb, tliMoveThis, tliTarget, 0);
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
	 * @param tli
	 */
	public static void moveTaskLeft(TaskBundle tb, TaskListItem tli) {
		moveTaskInDirection(tb, tli, tb.getTasks(), Direction.LEFT);
	}
	
	public static void moveTaskLeft(TaskBundle tb, Long taskId) {
		// Always use the initial form of the method.
		moveTaskLeft(tb, findTask(tb, taskId));
	}
	
	/**
	 * Moves a TaskListItem right from its current position.
	 * 
	 * @param tb
	 * @param tli
	 */
	public static void moveTaskRight(TaskBundle tb, TaskListItem tli) {
		moveTaskInDirection(tb, tli, tb.getTasks(), Direction.RIGHT);
	}
	
	public static void moveTaskRight(TaskBundle tb, Long taskId) {
		// Always use the initial form of the method.
		moveTaskRight(tb, findTask(tb, taskId));
	}
	
	/**
	 * Moves a TaskListItem up from its current position.
	 * 
	 * @param tb
	 * @param tli
	 */
	public static void moveTaskUp(TaskBundle tb, TaskListItem tli) {
		moveTaskInDirection(tb, tli, tb.getTasks(), Direction.UP);
	}
	
	public static void moveTaskUp(TaskBundle tb, Long taskId) {
		// Always use the initial form of the method.
		moveTaskUp(tb, findTask(tb, taskId));
	}
}
