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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to represent order and hierarchy for the tasks within a
 * task folder.
 *  
 * @author drfoster
 */
public class TaskLinkage implements IsSerializable {
	private List<TaskLink> m_taskOrder = new ArrayList<TaskLink>();	// Ordered list of the TaskLink's of task FolderEntry's tracked by this TaskLinkage.
	
	// The following are used as keys for the information stored in a
	// Map representation of a TaskLinkage object.
	private final static String SERIALIZED_TASK_IDS      = "taskIds";
	private final static String SERIALIZED_SUBTASKS_BASE = "subtasks.";
	
	/**
	 * Inner class used to track individual tasks within a TaskLinkage.
	 */
	public static class TaskLink implements IsSerializable {
		private List<TaskLink>	m_subtasks = new ArrayList<TaskLink>();	// List<TaskLink> of the subtasks of this task.
		private Long			m_taskId;								// ID of the FolderEntry of this task

		/**
		 * Class constructor.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public TaskLink() {
			// Nothing to do.
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
			TaskLink tl = new TaskLink();
			tl.setTaskId(id);
			appendSubtask(tl);
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

		/**
		 * Stores a new List<TaskLink> of subtasks on this TaskLink.
		 * 
		 * @param subtasks
		 */
		public void setSubtasks(List<TaskLink> subtasks) {
			m_subtasks = subtasks;
		}
		
		/**
		 * Stores a task ID in this TaskLink.
		 * 
		 * @param taskId
		 */
		public void setTaskId(Long taskId) {
			m_taskId = taskId;
		}
	}
	
	/**
	 * Class constructor.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public TaskLinkage() {
		// Nothing to do.
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
		TaskLink tl = new TaskLink();
		tl.setTaskId(id);
		appendTask(id);
	}

	/**
	 * Returns a Map that that represents the serialization of this
	 * task linkage.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getSerializationMap() {
		// Simply serialize the List<TaskLink> order list.
		return getSerializationMapImpl(m_taskOrder);
	}
	
	/*
	 * Builds a serialization map for the given List<TaskLink>.
	 */
	@SuppressWarnings("unchecked")
	private static Map getSerializationMapImpl(List<TaskLink> links) {
		Map reply = new HashMap();
		
		List<Long> taskIds = new ArrayList<Long>();
		for (TaskLink tl:  links) {
			Long tid = tl.getTaskId();
			taskIds.add(tid);
			reply.put(
				(SERIALIZED_SUBTASKS_BASE + String.valueOf(tid)),
				getSerializationMapImpl(
					tl.getSubtasks()));
		}		
		reply.put(SERIALIZED_TASK_IDS, taskIds);
				
		return reply;
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
	 * Constructs and returns a TaskLinkage from a serialization Map.
	 * 
	 * @param serializationMap
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static TaskLinkage loadSerializationMap(Map serializationMap) {
		TaskLinkage reply = new TaskLinkage();
		if ((null != serializationMap) && (!(serializationMap.isEmpty()))) {
			reply.setTaskOrder(loadSerializationMapImpl(serializationMap));
		}
		return reply;
	}

	/*
	 * Constructs and returns a List<TaskLink> from a serialization
	 * Map.
	 */
	@SuppressWarnings("unchecked")
	private static List<TaskLink> loadSerializationMapImpl(Map serializationMap) {
		List<TaskLink> reply = new ArrayList<TaskLink>();
		if ((null != serializationMap) && (!(serializationMap.isEmpty()))) {
			List<Long> taskIds = ((List<Long>) serializationMap.get(SERIALIZED_TASK_IDS));
			for (Long taskId:  taskIds) {
				TaskLink taskLink = new TaskLink();
				taskLink.setTaskId(taskId);
				taskLink.setSubtasks(
					loadSerializationMapImpl(
						((Map) serializationMap.get(SERIALIZED_SUBTASKS_BASE + String.valueOf(taskId)))));
				reply.add(taskLink);
			}
		}
		return reply;
	}
	
	/**
	 * Stores a new List<TaskLink> task ordering in this TaskLinkage.
	 * 
	 * @param taskOrder
	 */
	public void setTaskOrder(List<TaskLink> taskOrder) {
		m_taskOrder = taskOrder;
	}
}
