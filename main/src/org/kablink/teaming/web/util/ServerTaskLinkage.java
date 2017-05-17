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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.module.binder.BinderModule;

/**
 * Class used to represent order and hierarchy for the tasks within
 * a task folder.
 * 
 * Note:
 *    This class is a copy of the TaskLinkage class.  Changes done
 *    to to one must be reflected in the other.
 */
@SuppressWarnings("unchecked")
public class ServerTaskLinkage {
	protected static final Log m_logger = LogFactory.getLog(ServerTaskLinkage.class);
	
	// The following are used as keys for the information stored in a
	// Map representation of a ServerTaskLinkage object.
	private final static String SERIALIZED_ENTRY_IDS		= "taskIds";
	private final static String SERIALIZED_SUBTASKS_BASE	= "subtasks.";

	private List<ServerTaskLink> m_taskOrder = new ArrayList<ServerTaskLink>();	// Ordered list of the ServerTaskLink's of task FolderEntry's tracked by this ServerTaskLinkage.
	
	/*
	 * Inner class used to track individual tasks within a
	 * ServerTaskLinkage.
	 * 
	 * Note:
	 *    This class is a copy of the TaskLinkage.TaskLink class.
	 *    Changes done to to one must be reflected in the other.
	 */
	public static class ServerTaskLink {
		private List<ServerTaskLink>	m_subtasks = new ArrayList<ServerTaskLink>();	// List<ServerTaskLink> of the subtasks of this task.
		private Long					m_entryId;										// ID of the FolderEntry of this task

		/**
		 * Class constructor.
		 */
		public ServerTaskLink() {
			// Nothing to do.
		}

		/**
		 * Returns the List<ServerTaskLink> of subtasks from this
		 * ServerTaskLink.
		 * 
		 * @return
		 */
		public List<ServerTaskLink> getSubtasks() {
			return m_subtasks;
		}
		
		/**
		 * Returns the entry ID from this ServerTaskLink.
		 * 
		 * @return
		 */
		public Long getEntryId() {
			return m_entryId;
		}

		/**
		 * Returns true if this ServerTaskLink has subtasks and false
		 * otherwise.
		 * @return
		 */
		public boolean hasSubtasks() {
			return (!(m_subtasks.isEmpty()));
		}

		/**
		 * Stores a new List<ServerTaskLink> of subtasks on this
		 * ServerTaskLink.
		 * 
		 * @param subtasks
		 */
		public void setSubtasks(List<ServerTaskLink> subtasks) {
			m_subtasks = subtasks;
		}
		
		/**
		 * Stores an entry ID of a task in this ServerTaskLink.
		 * 
		 * @param entryId
		 */
		public void setEntryId(Long entryId) {
			m_entryId = entryId;
		}
	}
	
	/**
	 * Class constructor.
	 */
	public ServerTaskLinkage() {
		// Nothing to do.
	}

	/**
	 * Returns the ServerTaskLink with the given ID from this ServerTaskLinkage.
	 * 
	 * @param taskList
	 * @param entryId
	 * 
	 * @return
	 */	
	public static ServerTaskLink findTask(List<ServerTaskLink> taskList, Long entryId) {
		// Scan the ServerTaskLink's in this ServerTaskLinkage.
		for (ServerTaskLink taskScan:  taskList) {
			// Is this the ServerTaskLink in question?
			if (taskScan.getEntryId().equals(entryId)) {
				// Yes!  Return it.
				return taskScan;
			}

			// Is this ID for a subtask of this ServerTaskLink?
			ServerTaskLink reply = findSubtask(taskScan, entryId);
			if (null != reply) {
				// Yes!  Return it.
				return reply;
			}
		}

		// If we get here, we couldn't find a ServerTaskLink with the given
		// ID.  Return null.
		return null;
	}

	/**
	 * Searches a ServerTaskLinkage for the ServerTaskLink that
	 * contains the given task.
	 * 
	 * @param taskLinkage
	 * @param taskId2Find
	 * 
	 * @return
	 */
	public static ServerTaskLink findTaskContainingTask(ServerTaskLinkage taskLinkage, Long taskId2Find) {
		List<ServerTaskLink> taskList = ((null == taskLinkage) ? null : taskLinkage.getTaskOrder());
		ServerTaskLink reply = ((null == taskList) ? null : findTaskContainingTaskImpl(taskList, taskId2Find));
		return reply;
	}
	
	/*
	 * Searches the ServerTaskLink's in stlList2Search for a
	 * ServerTaskLink contain taskId2Find as one of its subtasks.  If
	 * found, that ServerTaskLink is returned.  Otherwise, null is
	 * returned.
	 */
	private static ServerTaskLink findTaskContainingTaskImpl(List<ServerTaskLink> stlList2Search, Long taskId2Find) {
		// Scan the ServerTaskLink's in the List<ServerTaskLink> to search.
		for (ServerTaskLink task:  stlList2Search) {
			// If this ServerTaskLink's subtask List<ServerTaskLink> is the list in
			// question...
			List<ServerTaskLink> subtasks = task.getSubtasks();
			for (ServerTaskLink subtask:  subtasks) {
				if (subtask.getEntryId().equals(taskId2Find)) {
					// ...return the ServerTaskLink.
					return task;
				}
			}

			// If the list we're looking for is a subtask of this
			// ServerTaskLink's subtasks...
			ServerTaskLink reply = findTaskContainingTaskImpl(subtasks, taskId2Find);
			if (null != reply) {
				// ...return that ServerTaskLink.
				return reply;
			}
		}

		// If we get here, we couldn't find tli2Find in tlList2Search.
		// Return null.
		return null;
	}

	/**
	 * Returns the List<ServerTaskLink> containing the given ID from this
	 * ServerTaskLinkage.
	 * 
	 * @param tb
	 * @param entryId
	 * 
	 * @return
	 */	
	public static List<ServerTaskLink> findTaskList(ServerTaskLinkage tl, Long entryId) {
		// Scan the ServerTaskLink's in this ServerTaskLinkage.
		for (ServerTaskLink taskScan:  tl.getTaskOrder()) {
			// Is this the ServerTaskLink in question?
			if (taskScan.getEntryId().equals(entryId)) {
				// Yes!  Return it.
				return tl.getTaskOrder();
			}

			// Is this ID for a subtask of this ServerTaskList?
			List<ServerTaskLink> reply = findSubtaskList(taskScan, entryId);
			if (null != reply) {
				// Yes!  Return it.
				return reply;
			}
		}

		// If we get here, we couldn't find a ServerTaskLink with the given
		// ID.  Return null.
		return null;
	}
	
	/*
	 * Searches a task's subtasks for the given ID.
	 */
	private static ServerTaskLink findSubtask(ServerTaskLink task, Long entryId) {
		for (ServerTaskLink taskScan:  task.getSubtasks()) {
			if (entryId.equals(taskScan.getEntryId())) {
				return taskScan;
			}
			
			ServerTaskLink reply = findSubtask(taskScan, entryId);
			if (null != reply) {
				return reply;
			}
		}
		
		return null;
	}

	/*
	 * Searches a task's subtasks for the given ID and returns
	 * the List<ServerTaskLink> that contains it.  Returns null if the
	 * ServerTaskLink cannot be found.
	 */
	private static List<ServerTaskLink> findSubtaskList(ServerTaskLink task, Long entryId) {
		for (ServerTaskLink taskScan:  task.getSubtasks()) {
			if (entryId.equals(taskScan.getEntryId())) {
				return task.getSubtasks();
			}
			
			List<ServerTaskLink> reply = findSubtaskList(taskScan, entryId);
			if (null != reply) {
				return reply;
			}
		}
		
		return null;
	}

	/*
	 * Given a binder ID and a mapping between old entry IDs and new
	 * entry IDs, fixes the old entry ID references in the
	 * ServerTaskLinkage and writes a serialized version of it to the
	 * binder's properties.
	 */
	private void fixupAndStoreTaskLinkage(BinderModule bm, Long binderId, Map<Long, Long> entryIdMap) {
		// Recursively fixup the entry ID's in the ServerTaskLinkage's
		// List<ServerTaskLink>...
		fixupTaskLinkList(getTaskOrder(), entryIdMap);
		
		// ...and write the serialized ServerTaskLinkage to the binder.
		bm.setProperty(
			binderId,
			ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE,
			getSerializationMap());
	}

	/**
	 * Given a mapping between binder IDs and ServerTaskLinkage's and a
	 * mapping between old entry IDs and new entry IDs, fixes the old
	 * entry ID references in the ServerTaskLinkage's and writes the
	 * serialized version of them to each binder's properties.
	 * 
	 * @param bm
	 * @param taskLinkageMap
	 * @param entryIdMap
	 */
	public static void fixupAndStoreTaskLinkages(BinderModule bm, Map<Long, ServerTaskLinkage> taskLinkageMap, Map<Long, Long> entryIdMap) {
		Iterator itTaskLinkages = taskLinkageMap.entrySet().iterator();
		while (itTaskLinkages.hasNext()) {
			Map.Entry me = ((Map.Entry) itTaskLinkages.next());
			fixupAndStoreTaskLinkages(
				bm,
				((Long) me.getKey()),
				((ServerTaskLinkage) me.getValue()),
				entryIdMap);
		}
	}
	
	public static void fixupAndStoreTaskLinkages(BinderModule bm, Long binderId, ServerTaskLinkage tl, Map<Long, Long> entryIdMap) {
		tl.fixupAndStoreTaskLinkage(
			bm,
			binderId,
			entryIdMap);
	}

	/*
	 * Recursively scans the ServerTaskLink's in a List<ServerTaskLink>
	 * and changes references to old entry IDs to new entry IDs.
	 */
	private static void fixupTaskLinkList(List<ServerTaskLink> tlList, Map<Long, Long> entryIdMap) {
		// Scan the ServerTaskLink's in the List<ServerTaskLink>.
		int c = tlList.size();
		for (int i = (c - 1); i >= 0; i -= 1) {
			// Can we map this ServerTaskLink's old entry ID to a new
			// entry ID?
			ServerTaskLink link = tlList.get(i);
			Long entryId = link.getEntryId();
			Long newEntryId = entryIdMap.get(entryId);
			if (null == newEntryId) {
				// No!  Log an error and remove it from the linkage.
				m_logger.error("Task " + entryId + " missing from entry ID map, dropped from folder's linkage.");
				tlList.remove(i);
			}
			else {
				// Yes, we've got the new entry ID!  Store it and fixup
				// its subtasks.
				link.setEntryId(newEntryId);
				fixupTaskLinkList(link.getSubtasks(), entryIdMap);
			}
		}
	}
	
	/**
	 * Returns a Map that that represents the serialization of a
	 * ServerTaskLinkage.
	 * 
	 * @return
	 */
	public Map getSerializationMap() {
		// Simply serialize the List<ServerTaskLink> order list.
		return getSerializationMapImpl(m_taskOrder);
	}
	
	/*
	 * Builds a serialization map for the given
	 * List<ServerTaskLink>.
	 */
	private static Map getSerializationMapImpl(List<ServerTaskLink> links) {
		Map reply = new HashMap();
		
		List<Long> entryIds = new ArrayList<Long>();
		for (ServerTaskLink tl:  links) {
			Long entryId = tl.getEntryId();
			entryIds.add(entryId);
			reply.put(
				(SERIALIZED_SUBTASKS_BASE + String.valueOf(entryId)),
				getSerializationMapImpl(
					tl.getSubtasks()));
		}		
		reply.put(SERIALIZED_ENTRY_IDS, entryIds);
				
		return reply;
	}
	
	/**
	 * Returns the List<ServerTaskLink> of order from this
	 * ServerTaskLinkage.
	 * 
	 * @return
	 */
	public List<ServerTaskLink> getTaskOrder() {
		return m_taskOrder;
	}

	/**
	 * Constructs and returns a ServerTaskLinkage from a
	 * serialization Map.
	 * 
	 * @param serializationMap
	 * 
	 * @return
	 */
	public static ServerTaskLinkage loadSerializationMap(Map serializationMap) {
		ServerTaskLinkage reply = new ServerTaskLinkage();
		if ((null != serializationMap) && (!(serializationMap.isEmpty()))) {
			reply.setTaskOrder(loadSerializationMapImpl(serializationMap));
		}
		return reply;
	}

	/*
	 * Constructs and returns a List<ServerTaskLink> from a
	 * serialization Map.
	 */
	private static List<ServerTaskLink> loadSerializationMapImpl(Map serializationMap) {
		List<ServerTaskLink> reply = new ArrayList<ServerTaskLink>();
		if ((null != serializationMap) && (!(serializationMap.isEmpty()))) {
			List<Long> entryIds = ((List<Long>) serializationMap.get(SERIALIZED_ENTRY_IDS));
			for (Long entryId:  entryIds) {
				ServerTaskLink taskLink = new ServerTaskLink();
				taskLink.setEntryId(entryId);
				taskLink.setSubtasks(
					loadSerializationMapImpl(
						((Map) serializationMap.get(SERIALIZED_SUBTASKS_BASE + String.valueOf(entryId)))));
				reply.add(taskLink);
			}
		}
		return reply;
	}
	
	/**
	 * Stores a new List<ServerTaskLink> task ordering in this
	 * ServerTaskLinkage.
	 * 
	 * @param taskOrder
	 */
	public void setTaskOrder(List<ServerTaskLink> taskOrder) {
		m_taskOrder = taskOrder;
	}
	
	/**
	 * Validates the task IDs referenced by a ServerTaskLinkage.
	 * 
	 * @param entryIds
	 */
	public void validateTaskLinkage(List<Long> entryIds) {
		// Simply validate the List<ServerTaskLink> of the task ordering.
		validateTaskLinkList(getTaskOrder(), entryIds);
	}
	
	/*
	 * Validates the ServerTaskLink's in a List<ServerTaskLink>
	 * removing any that are invalid..
	 */
	private static void validateTaskLinkList(List<ServerTaskLink> tlList, List<Long> entryIds) {
		// Scan the ServerTaskLink's in the List<ServerTaskLink>.
		int c = ((null == tlList) ? 0 : tlList.size());
		for (int i = (c - 1); i >= 0; i -= 1) {
			// Is this ServerTaskLink valid?
			ServerTaskLink tl = tlList.get(i);
			if (!(entryIds.contains(tl.getEntryId()))) {
				// No!  Remove it.
				tlList.remove(i);
			}
			
			else {			
				// Yes, this ServerTaskLink is valid!  Validate its subtasks.
				validateTaskLinkList(tl.getSubtasks(), entryIds);
			}
		}
	}
}
