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
package org.kablink.teaming.gwt.server.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskLinkage.TaskLink;
import org.kablink.teaming.util.AllModulesInjected;


/**
 * Helper methods for the GWT UI server code that services task
 * requests.
 *
 * @author drfoster@novell.com
 */
public class GwtTaskHelper {
	protected static Log m_logger = LogFactory.getLog(GwtTaskHelper.class);

	/*
	 * Logs the contents of a TaskLinkage object.
	 */
	private static void dumpTaskLinkage(TaskLinkage linkage) {
		// If debug logging is disabled...
		if (!(m_logger.isDebugEnabled())) {
			// ...bail.
			return;
		}

		// If there are no TaskLink's in the task order list...
		List<TaskLink> taskOrder = linkage.getTaskOrder();
		if (taskOrder.isEmpty()) {
			// ...log that fact and bail.
			m_logger.debug("TaskLinkage.dumpTaskLinkage( EMPTY )");
			return;
		}

		// Scan the TaskLink's in the task order list...
		m_logger.debug("TaskLinkage.dumpTaskLinkage( START )");
		for (TaskLink tl:  taskOrder) {
			// ...logging each of them.
			dumpTaskLink(tl, 0);
		}
		m_logger.debug("TaskLinkage.dumpTaskLinkage( END )");
	}

	/*
	 * Logs the contents of a TaskLink object.  The information is
	 * indented depth levels.
	 */
	private static void dumpTaskLink(TaskLink tl, int depth) {
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
			dumpTaskLink(tlScan, subtaskDepth);
		}
	}
	
	/**
	 * Returns the TaskLinkage from a task folder.
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	public static TaskLinkage getTaskLinkage(AllModulesInjected bs, Binder binder) {
		TaskLinkage reply = ((TaskLinkage) binder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE));
		if (null == reply) {
			reply = new TaskLinkage();
		}
		
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("TaskHelper.getTaskLinkage( Read TaskLinkage for binder ):  " + String.valueOf(binder.getId()));
			dumpTaskLinkage(reply);
		}
		
		return reply;
	}
	
	/*
	 * Returns true if a TaskLink refers to a valid FolderEntry and false
	 * otherwise.
	 */
	private static boolean isTaskLinkValid(AllModulesInjected bs, TaskLink tl) {
		// If we weren't given a TaskLink...
		boolean reply = false;
		if (null == tl) {
			// ..it can't be valid.
			return reply;
		}
		
		try {
			// If we can access the TaskLink's FolderEntry and it's not
			// deleted or predeleted, it's valid.
			FolderEntry taskFE = bs.getFolderModule().getEntry(null, tl.getTaskId());
			reply = ((null != taskFE) && (!(taskFE.isDeleted())) && (!(taskFE.isPreDeleted())));
		}
		
		catch (Exception ex) {
			// If we can't access the TaskLink's FolderEntry, it's not
			// valid.
			m_logger.debug("TaskHelper.isTaskLinkValid( EXCEPTION ):  ", ex);
			reply = false;
		}
		
		// If we get here, reply is true if the TaskLink references a
		// task we can access and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Removes the TaskLinkage from a task folder.
	 * 
	 * @param bs
	 * @param binder
	 */
	public static void removeTaskLinkage(AllModulesInjected bs, Binder binder) {
		setTaskLinkage(bs, binder, null);
	}

	/**
	 * Stores the TaskLinkage for a task folder.
	 * 
	 * @param bs
	 * @param binder
	 * @param tl
	 */
	public static void setTaskLinkage(AllModulesInjected bs, Binder binder, TaskLinkage tl) {
		bs.getBinderModule().setProperty(binder.getId(), ObjectKeys.BINDER_PROPERTY_TASK_LINKAGE, tl);
		if (m_logger.isDebugEnabled()) {
			if (null == tl) {
				m_logger.debug("TaskHelper.setTaskLinkage( Removed TaskLinkage for binder ):  " + String.valueOf(binder.getId()));
			}
			else {
				m_logger.debug("TaskHelper.setTaskLinkage( Stored TaskLinkage for binder ):  " + String.valueOf(binder.getId()));
				dumpTaskLinkage(tl);
			}
		}
	}
	
	/**
	 * Validates the task FolderEntry's referenced by a TaskLinkage.
	 * 
	 * @param bs
	 * @param tl
	 */
	public static void validateTaskLinkage(AllModulesInjected bs, TaskLinkage tl) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("TaskHelper.validateTaskLinkage( BEFORE ):");
			dumpTaskLinkage(tl);
		}
		
		// Simply validate the List<TaskList> of the task ordering.
		validateTaskLinkList(bs, tl.getTaskOrder());
		
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("TaskHelper.validateTaskLinkage( AFTER ):");
			dumpTaskLinkage(tl);
		}
	}

	/*
	 * Validates the TaskLink's in a List<TaskLink> removing any that
	 * are invalid or cannot be accessed.
	 */
	private static void validateTaskLinkList(AllModulesInjected bs, List<TaskLink> tlList) {
		// Scan the TaskLink's in the List<TaskLink>.
		int c = ((null == tlList) ? 0 : tlList.size());
		for (int i = (c - 1); i >= 0; i -= 1) {
			// Is this TaskLink valid?
			TaskLink tl = tlList.get(i);
			if (!(isTaskLinkValid(bs, tl))) {
				// No!  Remove it.
				tlList.remove(i);
			}
			
			else {			
				// Yes, this TaskLink is valid!  Validate its subtasks.
				validateTaskLinkList(bs, tl.getSubtasks());
			}
		}
	}	
}
