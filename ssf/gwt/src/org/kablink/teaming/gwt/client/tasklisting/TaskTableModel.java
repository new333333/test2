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
package org.kablink.teaming.gwt.client.tasklisting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.util.TaskListItem;

import com.google.gwt.gen2.table.client.MutableTableModel;
import com.google.gwt.gen2.table.client.TableModelHelper.Request;
import com.google.gwt.gen2.table.client.TableModelHelper.Response;

/**
 * Class used to model rows in the task folder listing table.  
 * 
 * @author drfoster@novell.com
 */
public class TaskTableModel extends MutableTableModel<TaskListItem> {
	private int						m_taskCount;					//
	private Map<Long, TaskListItem>	m_map;							//
	private TaskSorter				m_sorter = new TaskSorter();	//

	/**
	 * Fetch a {@link TaskLinkItem} by its ID.
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public TaskListItem getTaskById(Long id) {
		return m_map.get(id);
	}

	/**
	 * Returns the count of the tasks currently being managed.
	 * 
	 * @return
	 */
	public int getTaskCount() {
		return m_taskCount;
	}
	
	/**
	 * Implements the MutableTableModel.onRowInserted() method.
	 * 
	 * @param beforeRow
	 * 
	 * @return
	 */
	@Override
	protected boolean onRowInserted(int beforeRow) {
		return true;
	}

	/**
	 * Implements the MutableTableModel.onRowRemoved() method.
	 * 
	 * @param row
	 * 
	 * @return
	 */
	@Override
	protected boolean onRowRemoved(int row) {
		return true;
	}

	/**
	 * Implements the MutableTableModel.onSetRowValue() method.
	 * 
	 * @param row
	 * @param rowValue
	 * 
	 * @return
	 */
	@Override
	protected boolean onSetRowValue(int row, TaskListItem rowValue) {
		return true;
	}

	/**
	 * Implements the MutableTableModel.requestRows() method.
	 * 
	 * @param request
	 * @param callback
	 */
	@Override
	public void requestRows(final Request request, com.google.gwt.gen2.table.client.TableModel.Callback<TaskListItem> callback) {
		callback.onRowsReady(request, new Response<TaskListItem>() {
			/**
			 * Implements the Response.getRowValues() method.
			 * 
			 * @return
			 */
			@Override
			public Iterator<TaskListItem> getRowValues() {
				int     col       = request.getColumnSortList().getPrimaryColumn();
				boolean ascending = request.getColumnSortList().isPrimaryAscending();
				if (0 > col) {
					m_map = m_sorter.sort(m_map, new TaskSorter.TaskIdComparator(ascending));
				}
				
				else {
					switch (col) {
					case 0:  m_map = m_sorter.sort(m_map, new TaskSorter.TaskNameComparator(           ascending)); break;						
					case 1:  m_map = m_sorter.sort(m_map, new TaskSorter.TaskPriorityComparator(       ascending)); break;
					case 2:  m_map = m_sorter.sort(m_map, new TaskSorter.TaskDueComparator(            ascending)); break;
					case 3:  m_map = m_sorter.sort(m_map, new TaskSorter.TaskStatusComparator(         ascending)); break;
					case 4:  m_map = m_sorter.sort(m_map, new TaskSorter.TaskAssignedComparator(       ascending)); break;
					case 5:  m_map = m_sorter.sort(m_map, new TaskSorter.TaskClosedCompletedComparator(ascending)); break;
					}					
				}
				return m_map.values().iterator();
			}
		});
	}

	/**
	 * Set the data on the model.  Overrides prior data.
	 * 
	 * @param taskList
	 */
	public void setData(List<TaskListItem> taskList) {
		m_taskCount = 0;
		m_map       = new HashMap<Long, TaskListItem>();
		
		setDataImpl(taskList);
		setRowCount(m_taskCount);
	}
	
	private void setDataImpl(List<TaskListItem> taskList) {
		for (TaskListItem task:  taskList) {
			m_taskCount += 1;
			m_map.put(task.getTask().getTaskId(), task);
			setDataImpl(task.getSubtasks());
		}
	}
}
