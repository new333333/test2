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

package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.util.TaskStats;

/**
 * Draws a graph of the status statistics from a TaskStats object.
 * 
 * @author drfoster@novell.com
 */
public class TaskStatusGraph extends TaskGraphBase {
	/**
	 * Constructor method.
	 *  
	 * @param taskStats
	 * @param gridStyles
	 * @param renderAsync
	 */
	public TaskStatusGraph(TaskStats taskStats, String gridStyles, boolean renderAsync) {
		// Simply initialize the super class.
		super(taskStats, gridStyles, renderAsync);
	}
	
	public TaskStatusGraph(TaskStats taskStats, String gridStyles) {
		// Always use the initial form of the constructor.
		this(taskStats, gridStyles, true);
	}

	/**
	 * Called by the base class to render the graph.
	 * 
	 * Implements the TaskGraphBase.render() method.
	 */
	@Override
	protected void render() {
		// Render the various status values into the grid.
		TaskStats ts = getTaskStatistics();
		int c = ts.getStatusNeedsAction();
		int p;
		String m;
		if (0 < c) {
			p = ts.getPercent(c);
			m = m_messages.taskGraphs_StatusNeedsAction(String.valueOf(p), String.valueOf(c));
			addBarSegment(c, p, "taskGraphs-statsStatus0", m);
		}
		
		c = ts.getStatusInProcess();
		if (0 < c) {
			p = ts.getPercent(c);
			m = m_messages.taskGraphs_StatusInProcess(String.valueOf(p), String.valueOf(c));
			addBarSegment(c, p, "taskGraphs-statsStatus1", m);
		}
		
		c = ts.getStatusCompleted();
		if (0 < c) {
			p = ts.getPercent(c);
			m = m_messages.taskGraphs_StatusCompleted(String.valueOf(p), String.valueOf(c));
			addBarSegment(c, p, "taskGraphs-statsStatus2", m);
		}
		
		c = ts.getStatusCanceled();
		if (0 < c) {
			p = ts.getPercent(c);
			m = m_messages.taskGraphs_StatusCanceled(String.valueOf(p), String.valueOf(c));
			addBarSegment(c, p, "taskGraphs-statsStatus3", m);
		}
	}
}
