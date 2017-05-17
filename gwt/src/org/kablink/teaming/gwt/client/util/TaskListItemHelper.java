/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
 * Class containing utility methods for manipulating TaskListItem's.
 *  
 * @author drfoster@novell.com
 */
public class TaskListItemHelper {
	// Enumeration used to tell moveTaskInDirection() which direction a
	// TaskListItem is to be moved within a TaskListItemHelper object.
	private enum Direction {
		UP,
		DOWN,
		LEFT,	// Decreases the subtask level.
		RIGHT,	// Increases the subtask level.
	}

	/*
	 * Class constructor.
	 */
	private TaskListItemHelper() {
		// Inhibits this class from being instantiated.
	}

	/*
	 * Adds a Long to a List<Long> if it's not already there.
	 */
	private static void addLToLLIfUnique(List<Long> lList, Long l) {
		// Do we have both a List<Long> and a Long to work with? 
		if ((null != lList) && (null != l)) {
			// Yes!  If the List<Long> doesn't contain the Long...
			if (!(lList.contains(l))) {
				// ...add it.
				lList.add(l);
			}
		}
	}

	/**
	 * Restructures the tasks in a TaskBundle as per a new TaskLinkage.
	 * 
	 * @param tb
	 * @param tl
	 */
	public static void applyTaskLinkage(TaskBundle tb) {
		// We need a flat list to apply the linkage to.  Flatten the
		// list in the TaskBundle...
		flattenTaskList(tb);

		// ...apply the linkage from the TaskBundle...
		List<TaskListItem> flatTaskList       = tb.getTasks();
		List<TaskListItem> structuredTaskList = new ArrayList<TaskListItem>();
		applyTaskLinkageImpl(structuredTaskList, flatTaskList, tb.getTaskLinkage().getTaskOrder());
		
		// ...scan any tasks that weren't addressed by the task
		// ...linkage...
		for (TaskListItem task:  flatTaskList) {
			// ...add each to the 'structured' List<TaskListItem>
			// ...being built.
			structuredTaskList.add(task);
		}

		// Finally, store the structured task list in TaskBundle.
		tb.setTasks(structuredTaskList);
	}
	
	/*
	 * Restructures a 'flat' List<TaskListInfo> into a 'structured'
	 * List<TaskListInfo> based a List<TaskLink> defining the structure.
	 */
	private static void applyTaskLinkageImpl(List<TaskListItem> structuredTaskList, List<TaskListItem> flatTaskList, List<TaskLink> links) {
		// Scan the List<TaskLink>.
		for (TaskLink link:  links) {
			// Can we find the TaskListItem for this TaskLink?
			TaskListItem flatTask = findTask(flatTaskList, link.getEntryId());
			if (null == flatTask) {
				// No!  Skip it.
				continue;
			}

			// Remove the TaskListItem from the flatTaskList and add it
			// the structured task list...
			flatTaskList.remove(   flatTask);
			structuredTaskList.add(flatTask);
			
			// ...and recursively process any subtasks.
			applyTaskLinkageImpl(flatTask.getSubtasks(), flatTaskList, link.getSubtasks());
		}
	}

	/*
	 * Returns true if the subtasks of task are all completed and false
	 * otherwise.
	 * 
	 * skipThisTask will not be evaluated.
	 */
	private static boolean areSubtasksComplete(TaskListItem task, TaskListItem skipThisTask) {
		// Were we given a task whose subtasks are to be checked?
		if (null != task) {
			// Yes!  Scan the subtasks.
			for (TaskListItem subtask:  task.getSubtasks()) {
				// Are we supposed to skip this subtask?
				if (subtask == skipThisTask) {
					// Yes!  Skip it.
					continue;
				}
				
				// If we find any subtasks, besides skipThisTask that
				// are not completed or canceled...
				if (!(subtask.getTask().isTaskCompleted() || subtask.getTask().isTaskCancelled())) {
					// ...we're done and return false.
					return false;
				}
			}
		}
		
		// If we get here, we consider all the task's children other
		// than skipThisTask to be completed.  Return true.
		return true;
	}
	
	/**
	 * Returns true if the task list in the TaskBundle is flat and
	 * false otherwise.
	 * 
	 * @param tb
	 * @return
	 */
	public static boolean areTasksFlat(TaskBundle tb) {
		for (TaskListItem taskScan:  tb.getTasks()) {
			if (!(taskScan.getSubtasks().isEmpty())) {
				return false;
			}
		}
		return true;
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
			newTaskLink.setEntryId(taskScan.getTask().getTaskId().getEntityId());
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
			newTaskLink.setEntryId(taskScan.getTask().getTaskId().getEntityId());
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
	 * @param task
	 * 
	 * @return
	 */
	public static boolean canMoveTaskDown(TaskBundle tb, TaskListItem task) {
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (index < (tasks.size() - 1));
		
		return reply;
	}
	
	public static boolean canMoveTaskDown(TaskBundle tb, Long entryId) {
		return canMoveTaskDown(tb, findTask(tb, entryId));
	}
	
	/**
	 * Returns true if the given task can be moved left from where
	 * its at.
	 * 
	 * @param tb
	 * @param task
	 * 
	 * @return
	 */
	public static boolean canMoveTaskLeft(TaskBundle tb, TaskListItem task) {
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		boolean reply = (tasks != tb.getTasks());
		if (reply) {
			int index = tasks.indexOf(task);
			reply = ((0 == index) || (index == (tasks.size() - 1)));
		}
		
		return reply;
	}
	
	public static boolean canMoveTaskLeft(TaskBundle tb, Long entryId) {
		return canMoveTaskLeft(tb, findTask(tb, entryId));
	}
	
	/**
	 * Returns true if the given task can be moved right from where
	 * its at.
	 * 
	 * @param tb
	 * @param task
	 * 
	 * @return
	 */
	public static boolean canMoveTaskRight(TaskBundle tb, TaskListItem task) {
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (0 < index);
		
		return reply;
	}
	
	public static boolean canMoveTaskRight(TaskBundle tb, Long entryId) {
		return canMoveTaskRight(tb, findTask(tb, entryId));
	}
	
	/**
	 * Returns true if the given task can be moved up from where
	 * its at.
	 * 
	 * @param tb
	 * @param task
	 * 
	 * @return
	 */
	public static boolean canMoveTaskUp(TaskBundle tb, TaskListItem task) {
		List<TaskListItem> tasks = findTaskList(tb, task);
		
		int     index = tasks.indexOf(task);
		boolean reply = (0 < index);
		
		return reply;
	}
	
	public static boolean canMoveTaskUp(TaskBundle tb, Long entryId) {
		return canMoveTaskUp(tb, findTask(tb, entryId));
	}

	/**
	 * Returns true if any of the tasks in the list can be purged and
	 * false otherwise.
	 * 
	 * @param tasks
	 * 
	 * @return
	 */
	public static boolean canPurgeTask(List<TaskListItem> tasks) {
		for (TaskListItem task:  tasks) {
			if (task.getTask().getCanPurge()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if any of the tasks in the list can be moved to the
	 * trash and false otherwise.
	 * 
	 * @param tasks
	 * 
	 * @return
	 */
	public static boolean canTrashTask(List<TaskListItem> tasks) {
		for (TaskListItem task:  tasks) {
			if (task.getTask().getCanTrash()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Scan a task's parentage for completed parent tasks that need to
	 * be made active when a subtask is made active.
	 * 
	 * @param tb
	 * @param task
	 * @param affectedTasks
	 */
	public static void findAffectedParentTasks_Active(TaskBundle tb, TaskListItem task, List<TaskListItem> affectedTasks) {
		// Is the task a subtask?
		TaskListItem parentTask = TaskListItemHelper.findTaskListItemContainingTask(tb, task);
		if (null != parentTask) {
			// Yes!  Is its parent completed?
			if (parentTask.getTask().isTaskCompleted()) {
				// Yes!  It needs to be made active and its parent
				// evaluated.
				affectedTasks.add(parentTask);				
				findAffectedParentTasks_Active(tb, parentTask, affectedTasks);
			}
		}
	}
	
	/**
	 * Scan a task's parentage for uncompleted parent tasks that need
	 * to be made completed when a subtask is made completed.
	 * 
	 * @param tb
	 * @param task
	 * @param affectedTasks
	 */
	public static void findAffectedParentTasks_Completed(TaskBundle tb, TaskListItem task, List<TaskListItem> affectedTasks) {
		// Is the task a subtask?
		TaskListItem parentTask = TaskListItemHelper.findTaskListItemContainingTask(tb, task);
		if (null != parentTask) {
			// Yes!  Is the parent active?
			if (parentTask.getTask().isTaskActive()) {
				// Yes!  Are all the parent's children, except the task
				// in question completed?
				if (areSubtasksComplete(parentTask, task)) {
					// Yes!  The parent needs to be marked completed
					// and its parent evaluated.
					affectedTasks.add(parentTask);					
					findAffectedParentTasks_Completed(tb, parentTask, affectedTasks);
				}
			}
		}
	}

	/**
	 * Returns a List<Long> of the contributor IDs from a
	 * List<TaskListItem>.
	 * 
	 * @return
	 */
	public static List<Long> findContributorIds(List<TaskListItem> tasks) {
		List<Long> reply = new ArrayList<Long>();
		if (null != tasks) {
			for (TaskListItem task:  tasks) {
				addLToLLIfUnique(reply, task.getTask().getCreatorId() );
				addLToLLIfUnique(reply, task.getTask().getModifierId());
			}
		}
		return reply;
	}
	
	/**
	 * Searches a task's subtasks for the given ID.
	 * 
	 * @param entryId
	 * 
	 * @return
	 */
	public static TaskListItem findSubtask(TaskListItem task, Long entryId) {
		for (TaskListItem taskScan:  task.getSubtasks()) {
			if (entryId.equals(taskScan.getTask().getTaskId().getEntityId())) {
				return taskScan;
			}
			
			TaskListItem reply = findSubtask(taskScan, entryId);
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
	 * @param entryId
	 * 
	 * @return
	 */
	public static List<TaskListItem> findSubtaskList(TaskListItem task, Long entryId) {
		for (TaskListItem taskScan:  task.getSubtasks()) {
			if (entryId.equals(taskScan.getTask().getTaskId().getEntityId())) {
				return task.getSubtasks();
			}
			
			List<TaskListItem> reply = findSubtaskList(taskScan, entryId);
			if (null != reply) {
				return reply;
			}
		}
		
		return null;
	}

	/**
	 * Returns the TaskListItem with the given ID from this TaskListItemHelper.
	 * 
	 * @param taskList
	 * @param entryId
	 * 
	 * @return
	 */	
	public static TaskListItem findTask(List<TaskListItem> taskList, Long entryId) {
		// Scan the TaskListItem's in this TaskListItemHelper.
		for (TaskListItem taskScan:  taskList) {
			// Is this the TaskListItem in question?
			if (taskScan.getTask().getTaskId().getEntityId().equals(entryId)) {
				// Yes!  Return it.
				return taskScan;
			}

			// Is this ID for a subtask of this TaskListItem?
			TaskListItem reply = findSubtask(taskScan, entryId);
			if (null != reply) {
				// Yes!  Return it.
				return reply;
			}
		}

		// If we get here, we couldn't find a TaskListItem with the given
		// ID.  Return null.
		return null;
	}
	
	public static TaskListItem findTask(TaskBundle tb, Long entryId) {
		// Always use the initial form of the method.
		return findTask(tb.getTasks(), entryId);
	}
	

	/**
	 * Returns the List<TaskListItem> containing the given ID from this
	 * TaskListItemHelper.
	 * 
	 * @param tb
	 * @param entryId
	 * 
	 * @return
	 */	
	public static List<TaskListItem> findTaskList(TaskBundle tb, Long entryId) {
		// Scan the TaskListItem's in this TaskListItemHelper.
		for (TaskListItem taskScan:  tb.getTasks()) {
			// Is this the TaskListItem in question?
			if (taskScan.getTask().getTaskId().getEntityId().equals(entryId)) {
				// Yes!  Return it.
				return tb.getTasks();
			}

			// Is this ID for a subtask of this TaskListItem?
			List<TaskListItem> reply = findSubtaskList(taskScan, entryId);
			if (null != reply) {
				// Yes!  Return it.
				return reply;
			}
		}

		// If we get here, we couldn't find a TaskListItem with the given
		// ID.  Return null.
		return null;
	}
	
	public static List<TaskListItem> findTaskList(TaskBundle tb, TaskListItem task) {
		// Always use the initial form of the method.
		return findTaskList(tb, task.getTask().getTaskId().getEntityId());
	}

	/**
	 * Searches the tasks in a TaskBundle for the task that contains
	 * tliList2Find as its subtask list.
	 * 
	 * @param tb
	 * @param tliList2Find
	 * 
	 * @return
	 */
	public static TaskListItem findTaskListItemContainingList(TaskBundle tb, List<TaskListItem> tliList2Find) {
		return findTaskListItemContainingListImpl(tb.getTasks(), tliList2Find);
	}
	
	/*
	 * Searches the TaskListItem's in tliList2Search for a TaskListItem
	 * contain tliList2Find as its subtask list.  If found, that
	 * TaskListItem is returned.  Otherwise, null is returned.
	 */
	private static TaskListItem findTaskListItemContainingListImpl(List<TaskListItem> tliList2Search, List<TaskListItem> tliList2Find) {
		// Scan the TaskListItem's in the List<TaskListItem> to search.
		for (TaskListItem task:  tliList2Search) {
			// If this TaskListItem's subtask List<TaskListItem> is the list in
			// question...
			List<TaskListItem> subtasks = task.getSubtasks();
			if (subtasks == tliList2Find) {
				// ...return the TaskListItem.
				return task;
			}

			// If the list we're looking for is a subtask of this
			// TaskListItem's subtasks...
			TaskListItem reply = findTaskListItemContainingListImpl(subtasks, tliList2Find);
			if (null != reply) {
				// ...return that TaskListItem.
				return reply;
			}
		}

		// If we get here, we couldn't find tliList2Find in
		// tliList2Search.  Return null.
		return null;
	}

	/**
	 * Searches the tasks in a TaskBundle for the task that contains
	 * tli2Find as a task in its subtask list.
	 * 
	 * @param tb
	 * @param tli2Find
	 * 
	 * @return
	 */
	public static TaskListItem findTaskListItemContainingTask(TaskBundle tb, TaskListItem tli2Find) {
		return findTaskListItemContainingTaskImpl(tb.getTasks(), tli2Find);
	}
	
	/*
	 * Searches the TaskListItem's in tliList2Search for a TaskListItem
	 * contain tli2Find as one of its subtasks.  If found, that
	 * TaskListItem is returned.  Otherwise, null is returned.
	 */
	private static TaskListItem findTaskListItemContainingTaskImpl(List<TaskListItem> tliList2Search, TaskListItem tli2Find) {
		// Scan the TaskListItem's in the List<TaskListItem> to search.
		for (TaskListItem task:  tliList2Search) {
			// If this TaskListItem's subtask List<TaskListItem> is the list in
			// question...
			List<TaskListItem> subtasks = task.getSubtasks();
			for (TaskListItem subtask:  subtasks) {
				if (subtask == tli2Find) {
					// ...return the TaskListItem.
					return task;
				}
			}

			// If the list we're looking for is a subtask of this
			// TaskListItem's subtasks...
			TaskListItem reply = findTaskListItemContainingTaskImpl(subtasks, tli2Find);
			if (null != reply) {
				// ...return that TaskListItem.
				return reply;
			}
		}

		// If we get here, we couldn't find tli2Find in tlList2Search.
		// Return null.
		return null;
	}

	/*
	 * Flattens the subtask list in a TaskListItem so that the
	 * hierarchy has been removed.
	 */
	private static void flattenSubtaskList(TaskListItem task, List<TaskListItem> flatTaskList) {
		for (TaskListItem subtask:  task.getSubtasks()) {
			flatTaskList.add(subtask);
			flattenSubtaskList(subtask, flatTaskList);
		}
		task.getSubtasks().clear();
	}

	/**
	 * Flattens the task list in a TaskBundle so that the hierarchy has
	 * been removed.
	 * 
	 * @param tb
	 */
	public static void flattenTaskList(TaskBundle tb) {
		List<TaskListItem> flatTaskList = new ArrayList<TaskListItem>();
		for (TaskListItem task:  tb.getTasks()) {
			flatTaskList.add(task);
			flattenSubtaskList(task, flatTaskList);
		}
		tb.setTasks(flatTaskList);
	}
	
	/**
	 * Returns a List<TaskListItem> containing the given task and all
	 * subtasks below it.
	 * 
	 * @param task
	 * 
	 * @return
	 */
	public static List<TaskListItem> getTaskHierarchy(TaskListItem task) {
		List<TaskListItem> reply = new ArrayList<TaskListItem>();
		
		reply.add(task);
		getTaskHierarchyImpl(task.getSubtasks(), reply);
		
		return reply;
	}
	
	private static void getTaskHierarchyImpl(List<TaskListItem> tliList, List<TaskListItem> tliHierarchy) {
		for (TaskListItem task:  tliList) {
			tliHierarchy.add(task);
			getTaskHierarchyImpl(task.getSubtasks(), tliHierarchy);
		}		
	}

	/**
	 * Returns the task IDs from a List<TaskListItem>, optionally
	 * including the IDs of the subtasks.
	 * 
	 * @param tliList
	 * @param includeSubtasks
	 * 
	 * @return
	 */
	public static List<EntityId> getTaskIdsFromList(List<TaskListItem> tliList, boolean includeSubtasks) {
		List<EntityId> reply = new ArrayList<EntityId>();		
		getTaskIdsFromListImpl(tliList, reply, includeSubtasks);		
		return reply;
	}
	
	public static List<EntityId> getTaskIdsFromList(List<TaskListItem> tliList) {
		return getTaskIdsFromList(tliList, true);
	}
	
	private static void getTaskIdsFromListImpl(List<TaskListItem> tliList, List<EntityId> taskIds, boolean includeSubtasks) {
		for (TaskListItem task:  tliList) {
			taskIds.add(task.getTask().getTaskId());
			if (includeSubtasks) {
				getTaskIdsFromListImpl(task.getSubtasks(), taskIds, includeSubtasks);
			}
		}
	}

	/**
	 * Returns true if the given TaskListItem corresponds to a parent
	 * task with no start date, no end date and a duration.  It returns
	 * false otherwise.
	 * 
	 * @param tli
	 * 
	 * @return
	 */
	public static boolean isParentWithDurationError(TaskListItem tli) {
		// Is this a parent task?
		List<TaskListItem> subTasks = ((null ==tli) ? null : tli.getSubtasks());
		if ((null == subTasks) || (0 == subTasks.size())) {
			// No!  Then it can't have a duration error.
			return false;
		}

		// If a parent task only has a duration, that's the error that
		// we're looking for.
		return tli.getTask().getEvent().hasDurationOnly();
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
	 * @param task
	 */
	public static void moveTaskDown(TaskBundle tb, TaskListItem task) {
		moveTaskInDirection(tb, task, tb.getTasks(), Direction.DOWN);
	}
	
	public static void moveTaskDown(TaskBundle tb, Long entryId) {
		// Always use the initial form of the method.
		moveTaskDown(tb, findTask(tb, entryId));
	}
	
	/*
	 * Moves a TaskListItem in the direction specified relative to the
	 * other TaskListItem's in this TaskListItemHelper.
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
				// ...make it a peer to the task it's a subtask of,
				// ...immediately below what was its parent.
				int tliListSize = tliList.size();
				tliList.remove(tliMoveThisIndex);
				TaskListItem tliWithList          = findTaskListItemContainingList(tb, tliList    );
				TaskListItem tliWithListContainer = findTaskListItemContainingTask(tb, tliWithList);
				List<TaskListItem> tliTargetList  = ((null == tliWithListContainer) ? tb.getTasks() : tliWithListContainer.getSubtasks());
				int tliMoveToIndex = tliTargetList.indexOf(tliWithList);
				if (tliMoveThisIndex == (tliListSize - 1)) {
					tliMoveToIndex += 1;
				}				
				tliTargetList.add(tliMoveToIndex, tliMoveThis);
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
	 * @param task
	 */
	public static void moveTaskLeft(TaskBundle tb, TaskListItem task) {
		moveTaskInDirection(tb, task, tb.getTasks(), Direction.LEFT);
	}
	
	public static void moveTaskLeft(TaskBundle tb, Long entryId) {
		// Always use the initial form of the method.
		moveTaskLeft(tb, findTask(tb, entryId));
	}
	
	/**
	 * Moves a TaskListItem right from its current position.
	 * 
	 * @param tb
	 * @param task
	 */
	public static void moveTaskRight(TaskBundle tb, TaskListItem task) {
		moveTaskInDirection(tb, task, tb.getTasks(), Direction.RIGHT);
	}
	
	public static void moveTaskRight(TaskBundle tb, Long entryId) {
		// Always use the initial form of the method.
		moveTaskRight(tb, findTask(tb, entryId));
	}
	
	/**
	 * Moves a TaskListItem up from its current position.
	 * 
	 * @param tb
	 * @param task
	 */
	public static void moveTaskUp(TaskBundle tb, TaskListItem task) {
		moveTaskInDirection(tb, task, tb.getTasks(), Direction.UP);
	}
	
	public static void moveTaskUp(TaskBundle tb, Long entryId) {
		// Always use the initial form of the method.
		moveTaskUp(tb, findTask(tb, entryId));
	}
}
