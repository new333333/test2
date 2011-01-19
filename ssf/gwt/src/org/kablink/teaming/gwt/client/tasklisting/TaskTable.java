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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.TaskListItem;

import com.google.gwt.gen2.table.client.AbstractScrollTable.SortPolicy;
import com.google.gwt.gen2.table.client.CachedTableModel;
import com.google.gwt.gen2.table.client.DefaultRowRenderer;
import com.google.gwt.gen2.table.client.DefaultTableDefinition;
import com.google.gwt.gen2.table.client.FixedWidthGridBulkRenderer;
import com.google.gwt.gen2.table.client.PagingOptions;
import com.google.gwt.gen2.table.client.PagingScrollTable;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Class that implements the Composite that contains the task folder
 * listing table.  
 * 
 * @author drfoster@novell.com
 */
public class TaskTable extends Composite {
	private CachedTableModel<TaskListItem>			m_cachedTableModel;		//
	private DefaultTableDefinition<TaskListItem>	m_tableDefinition;		//
	private int										m_taskCount;			//
	private Label									m_noTasksLabel;			//
	private PagingOptions							m_pagingOptions;		//
	private PagingScrollTable<TaskListItem>			m_pagingScrollTable;	//
	private TaskTableModel							m_tableModel;			//
	
	private FlexTable			m_flexTable = new FlexTable();				//
	private GwtTeamingMessages	m_messages  = GwtTeaming.getMessages();		//
	private VerticalPanel		m_vPanel    = new VerticalPanel();			//
		
	/**
	 * Class constructor.
	 */
	public TaskTable() {
		super();
		
		m_pagingScrollTable = createScrollTable();
		m_pagingScrollTable.setHeight("400px");	//! ...need to calculate this on resizes...		
		m_pagingOptions = new PagingOptions(m_pagingScrollTable);
		
		m_flexTable.setWidget(0, 0, m_pagingScrollTable);
		m_flexTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		m_flexTable.setWidget(1, 0, m_pagingOptions);
		
		m_noTasksLabel = new Label(m_messages.taskNoTasks());
		m_noTasksLabel.addStyleName("wiki-noentries-panel");
		
		m_vPanel.add(m_noTasksLabel);
		m_vPanel.add(m_flexTable   );
		
		m_vPanel.setWidth(   "100%");
		m_flexTable.setWidth("100%");
		
		super.initWidget(m_vPanel);
	}
	
	/*
	 * Creates the CachedTableModel.
	 */
	private CachedTableModel<TaskListItem> createCachedTableModel(TaskTableModel tableModel) {
		CachedTableModel<TaskListItem> tm = new CachedTableModel<TaskListItem>(tableModel);
		
		tm.setPreCachedRowCount( Integer.MAX_VALUE);
		tm.setPostCachedRowCount(Integer.MAX_VALUE);
		tm.setRowCount(          Integer.MAX_VALUE);
		
		return tm;
	}
	
	/*
	 * Creates the PagingScrolTable.
	 */
	private PagingScrollTable<TaskListItem> createScrollTable() {
		// Create our own table model...
		m_tableModel = new TaskTableModel();
		
		// ...add it to the cached table model...
		m_cachedTableModel = createCachedTableModel(m_tableModel);
		
		// ...create the table's definition...
		m_tableDefinition = createTableDefinition();
		
		// ...create the PagingScrollTable itself...
		PagingScrollTable<TaskListItem> pagingScrollTable = new PagingScrollTable<TaskListItem>(m_cachedTableModel, m_tableDefinition);
		pagingScrollTable.setPageSize(25);	//! ...get TaskBundle via preferences...
		pagingScrollTable.setEmptyTableWidget(new HTML(m_messages.taskNoTasks()));
//		pagingScrollTable.getDataTable().setSelectionPolicy(SelectionPolicy.CHECKBOX);
		
		// ...setup the bulk renderer...
		FixedWidthGridBulkRenderer<TaskListItem> bulkRenderer = new FixedWidthGridBulkRenderer<TaskListItem>(pagingScrollTable.getDataTable(), pagingScrollTable);
		pagingScrollTable.setBulkRenderer(bulkRenderer);
		
		// ...and setup the formatting.
		pagingScrollTable.setCellPadding(3);
		pagingScrollTable.setCellSpacing(0);
		pagingScrollTable.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);		
		pagingScrollTable.setSortPolicy(SortPolicy.SINGLE_CELL);

		// If we get here, pagingScrollTable is null or refers to the
		// PagingScrollTable created.  Return it.
		return pagingScrollTable;
	}
	
	/*
	 * Creates the 'Assigned To' column definition.
	 */
	private TaskAssignedColumnDefinition buildColumn_Assigned() {
		TaskAssignedColumnDefinition col = new TaskAssignedColumnDefinition();
		
		col.setColumnSortable(   true);
		col.setColumnTruncatable(true);
		col.setHeader(           0, new HTML(m_messages.taskColumn_assignedTo()));
		col.setHeaderCount(      1);
		col.setHeaderTruncatable(false);
		
		return col;
	}
	
	/*
	 * Creates the 'Closed - % Done' column definition.
	 */
	private TaskClosedCompletedColumnDefinition buildColumn_ClosedCompleted() {
		TaskClosedCompletedColumnDefinition col = new TaskClosedCompletedColumnDefinition();
		
		col.setColumnSortable(   true);
		col.setColumnTruncatable(true);
		col.setHeader(           0, new HTML(m_messages.taskColumn_closedCompleted()));
		col.setHeaderCount(      1);
		col.setHeaderTruncatable(false);
		
		return col;
	}
	
	/*
	 * Creates the 'Due Date' column definition.
	 */
	private TaskDueColumnDefinition buildColumn_Due() {
		TaskDueColumnDefinition col = new TaskDueColumnDefinition();
		
		col.setColumnSortable(   true);
		col.setColumnTruncatable(true);
		col.setHeader(           0, new HTML(m_messages.taskColumn_dueDate()));
		col.setHeaderCount(      1);
		col.setHeaderTruncatable(false);
		
		return col;
	}
	
	/*
	 * Creates the 'Task Name' column definition.
	 */
	private TaskNameColumnDefinition buildColumn_Name() {
		TaskNameColumnDefinition col = new TaskNameColumnDefinition();
		
		col.setColumnSortable(      true);
		col.setColumnTruncatable(   false);
		col.setPreferredColumnWidth(80);
		col.setHeader(              0, new HTML(m_messages.taskColumn_name()));
		col.setHeaderCount(         1);
		col.setHeaderTruncatable(   false);
		
		return col;
	}
	
	/*
	 * Creates the 'Priority' column definition.
	 */
	private TaskPriorityColumnDefinition buildColumn_Priority() {
		TaskPriorityColumnDefinition col = new TaskPriorityColumnDefinition();
		
		col.setColumnSortable(   true);
		col.setColumnTruncatable(false);
		col.setHeader(           0, new HTML(m_messages.taskColumn_priority()));
		col.setHeaderCount(      1);
		col.setHeaderTruncatable(false);
		
		return col;
	}
	
	/*
	 * Creates the 'Status' column definition.
	 */
	private TaskStatusColumnDefinition buildColumn_Status() {
		TaskStatusColumnDefinition col = new TaskStatusColumnDefinition();
		
		col.setColumnSortable(   true);
		col.setColumnTruncatable(false);
		col.setHeader(           0, new HTML(m_messages.taskColumn_status()));
		col.setHeaderCount(      1);
		col.setHeaderTruncatable(false);
		
		return col;
	}

	/*
	 * Creates the definition used for the PagingScrollTable.
	 */
	private DefaultTableDefinition<TaskListItem> createTableDefinition() {
		// Create the table...
		DefaultTableDefinition<TaskListItem> tableDefinition = new DefaultTableDefinition<TaskListItem>();
		
		// ...set the row renderer...
		String[] rowColors = new String[]{"#FFFFDD", "#EEEEEE"};
		tableDefinition.setRowRenderer(new DefaultRowRenderer<TaskListItem>(rowColors));
		
		// ...and define the columns.
		tableDefinition.addColumnDefinition(buildColumn_Name());
		tableDefinition.addColumnDefinition(buildColumn_Priority());
		tableDefinition.addColumnDefinition(buildColumn_Due());
		tableDefinition.addColumnDefinition(buildColumn_Status());
		tableDefinition.addColumnDefinition(buildColumn_Assigned());
		tableDefinition.addColumnDefinition(buildColumn_ClosedCompleted());

		// If we get here, tableDefinition refers to the table
		// definition created.  Return it.
		return tableDefinition;
	}

	/**
	 * Shows the tasks in the List<TaskListItem>.
	 * 
	 * @param tasks
	 * 
	 * @return
	 */
	public long showTasks(List<TaskListItem> tasks) {
		// Save when we start.
		long start = System.currentTimeMillis();
		
		// Reset the table model data..
		m_tableModel.setData(tasks);
		m_taskCount = m_tableModel.getTaskCount();
		
		// ...reset the cached model...
		m_cachedTableModel.clearCache();		
		m_cachedTableModel.setRowCount(m_taskCount);
		
		// ...hide/show the 'No Tasks' label as appropriate ...
		m_noTasksLabel.setVisible(0 == m_taskCount);
		
		// ...and force to page zero with a reload.
		m_pagingScrollTable.gotoPage(0, true);

		// Finally, return how long we took to show the tasks.
		long end = System.currentTimeMillis();
		return (end - start);
	}
}
