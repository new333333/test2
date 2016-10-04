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
package org.kablink.teaming.gwt.client.tasklisting;

import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements a dialog for selecting where a new task should be placed
 * in a task hierarchy.
 *  
 * @author drfoster@novell.com
 */
public class TaskDispositionDlg extends DlgBox implements EditSuccessfulHandler {
	private final static String IDBASE			= "taskDisposition_";	// Base ID for rows in the task disposition Grid.
	private final static String IDTAIL_RADIO	= "_rb";				// Used for constructing the ID of a row's radio button.

	private Grid				m_taskDispositionsGrid;	// Once displayed, the table of task disposition options.
	private GwtTeamingMessages	m_messages;				// Access to the GWT UI messages.
	private Long				m_newTaskId;			// The ID of the new task whose disposition is being queried.
	private TaskListItem		m_selectedTask;			// The task the new task is being disposed of relative to.
	private TaskTable			m_taskTable;			// Access to the TaskTable we're prompting for.

	// This enumeration is used to specify the task disposition options
	// (i.e., where a new task should be placed relative to the
	// selected task.)
	private Map<TaskDisposition, RadioButton> m_rbMap = new HashMap<TaskDisposition, RadioButton>();	// Map of dispositions, radio button pairs.
	public enum TaskDisposition {
		BEFORE,
		AFTER,
		APPEND,
		SUBTASK,
	}
	
	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label, String addedStyle) {
			super(label);
			addStyleName("taskDispositionDlg_Label");
			if (!(GwtClientHelper.hasString(addedStyle))) {
				addedStyle = "gwtUI_nowrap";
			}
			addStyleName(addedStyle);
		}
		
		public DlgLabel(String label) {
			this(label, null);
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param taskTable
	 * @param newTaskId
	 * @param selectedTask
	 */
	public TaskDispositionDlg(boolean autoHide, boolean modal, int left, int top, TaskTable taskTable, Long newTaskId, TaskListItem selectedTask) {
		// Initialize the superclass...
		super(autoHide, modal, left, top, DlgButtonMode.Ok);

		// ...initialize everything else...
		m_messages     = GwtTeaming.getMessages();
		m_taskTable    = taskTable;
		m_newTaskId    = newTaskId;
		m_selectedTask = selectedTask;
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.taskDispositionDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Data passed via global data members.
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param ignored
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object ignored) {
		// Create a panel to hold the dialog's content...
		VerticalPanel vp = new VerticalPanel();

		// ...add a hint about what's happening at the top of the
		// ...dialog...
		vp.add(
			new DlgLabel(
				m_messages.taskDispositionDlgHint(
					m_selectedTask.getTask().getTitle()),
					"taskDispositionDlg_Hint"));

		// ...create Grid for that...
		m_taskDispositionsGrid = new Grid(0, 2);
		m_taskDispositionsGrid.addStyleName("taskDispositionDlg_Grid");
		m_taskDispositionsGrid.setCellPadding(0);
		m_taskDispositionsGrid.setCellSpacing(0);

		// ...create each option row...
		renderRow(m_taskDispositionsGrid, m_taskDispositionsGrid.getRowCount(), m_messages.taskDispositionDlgInsertBefore(),    TaskDisposition.BEFORE,  false);
		renderRow(m_taskDispositionsGrid, m_taskDispositionsGrid.getRowCount(), m_messages.taskDispositionDlgInsertAfter(),     TaskDisposition.AFTER,   false);
		renderRow(m_taskDispositionsGrid, m_taskDispositionsGrid.getRowCount(), m_messages.taskDispositionDlgInsertAsSubtask(), TaskDisposition.SUBTASK, false);
		renderRow(m_taskDispositionsGrid, m_taskDispositionsGrid.getRowCount(), m_messages.taskDispositionDlgInsertAppend(),    TaskDisposition.APPEND,  true );

		// ...and connect everything together.
		vp.add(m_taskDispositionsGrid);
			
		// Finally, return the panel the with the dialog's contents.
		return vp;
	}
	
	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// What task disposition option did the user select?
		TaskDisposition rbAction = TaskDisposition.APPEND;
		for (TaskDisposition rbDisposition:  m_rbMap.keySet()) {
			RadioButton rb = m_rbMap.get(rbDisposition);
			if (rb.getValue()) {
				rbAction = rbDisposition;
				break;
			}
		}
		
		// Asynchronously put that disposition into affect...
		final TaskDisposition td = rbAction;
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				m_taskTable.applyTaskDisposition(
					td,
					m_newTaskId,
					m_selectedTask.getTask().getTaskId().getEntityId());
			}
		});
		
		// ...and return true to close the dialog.
		return true;
	}

	/**
	 * Returns the edited List<ToolbarItem>.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Not used.  Return something non-null.
		return Boolean.TRUE;
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		// There is no specific focus widget for this dialog.
		return null;
	}

	/*
	 * Renders a disposition row in the Grid.
	 */
	private void renderRow(Grid grid, int row, String rbText, TaskDisposition disposition, boolean checked) {
		grid.insertRow(row);
		
		String rowId = (IDBASE + disposition.toString());
		grid.getRowFormatter().getElement(row).setId(rowId);
		
		RadioButton rb = new RadioButton("taskDispositions");
		rb.addStyleName("taskDispositionDlg_Radio");
		rb.getElement().setId(rowId + IDTAIL_RADIO);
		rb.setValue(checked);
		grid.setWidget(row, 0, rb);
		String txt = rbText;
		grid.setWidget(row, 1, new DlgLabel(txt));
		grid.getCellFormatter().setWidth(row, 1, "100%");
		
		m_rbMap.put(disposition, rb);
	}
}
