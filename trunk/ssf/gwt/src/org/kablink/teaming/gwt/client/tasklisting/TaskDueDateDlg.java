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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TaskPickDateEvent;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.TimePicker;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;


/**
 * Implements a dialog for editing a task's due date.
 *  
 * @author drfoster@novell.com
 */
public class TaskDueDateDlg extends DlgBox
	implements EditSuccessfulHandler, EditCanceledHandler,
	// Event handlers implemented by this class.
		TaskPickDateEvent.Handler
	{
	private Grid					m_taskDueDateGrid;	// Once displayed, the table of task due date dialog's widgets.
	private Map<String, DateBox>	m_dateBoxMap;		//
	private TaskListItem			m_selectedTask;		// The task whose due date is being edited.
	private TaskTable				m_taskTable;		// Access to the TaskTable we're prompting for.

	private final GwtTeamingMessages				m_messages  = GwtTeaming.getMessages();					//
	private final GwtTeamingTaskListingImageBundle	m_images	= GwtTeaming.getTaskListingImageBundle();	//
	
	private final static String IDBASE	= "taskDueDate_";	// Base ID for rows in the task due date Grid.
	private final static String IDDATE	= "_date";			// ID used to identify the date  widgets in the dialog.
	private final static String IDEND	= "end";			// ID used to identify the end   widgets in the dialog.
	private final static String IDSTART	= "start";			// ID used to identify the start widgets in the dialog.
	private final static String IDTIME	= "_time";			// ID used to identify the time  widgets in the dialog.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.TASK_PICK_DATE,
	};
	
	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label, String addedStyle) {
			super(label);
			addStyleName("taskDueDateDlg_Label");
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
	 * @param selectedTask
	 */
	public TaskDueDateDlg(boolean autoHide, boolean modal, int left, int top, TaskTable taskTable, TaskListItem selectedTask) {
		// Initialize the superclass...
		super(autoHide, modal, left, top);

		// ..register the events to be handled by this class...
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);
		
		// ...initialize everything else...
		m_dateBoxMap   = new HashMap<String, DateBox>();
		m_taskTable    = taskTable;
		m_selectedTask = selectedTask;
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.taskDueDateDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Data passed via global data members.
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

		// ...add the title of the top of the dialog...
		vp.add(
			new DlgLabel(
				m_selectedTask.getTask().getTitle(),
				"taskDueDateDlg_TaskTitle"));

		// ...create Grid for it...
		m_taskDueDateGrid = new Grid(0, 2);
		m_taskDueDateGrid.addStyleName("taskDueDateDlg_Grid");
		m_taskDueDateGrid.setCellPadding(0);
		m_taskDueDateGrid.setCellSpacing(0);

		// ...populate the grid...
		renderDateRow(IDSTART, m_messages.taskDueDateDlgLabelStart());
		renderDateRow(IDEND,   m_messages.taskDueDateDlgLabelEnd()  );
		renderDurationRow();
		renderClearAllRow();
		
		// ...and connect everything together.
		vp.add(m_taskDueDateGrid);
			
		// Finally, return the panel the with the dialog's contents.
		return vp;
	}
	
	
	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	public boolean editCanceled() {
		// Simply return true to allow the dialog to close.
		return true;
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
	public boolean editSuccessful(Object callbackData) {
		// Asynchronously put the due date into affect...
		Scheduler.ScheduledCommand taskDueDateChanger;
		taskDueDateChanger = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				m_taskTable.applyTaskDueDate(
					null,	//! ...this needs to be implemented...   NEW DUE DATE INFORMATION GOES HERE.
					m_selectedTask.getTask().getTaskId().getEntryId());
			}
		};
		Scheduler.get().scheduleDeferred(taskDueDateChanger);
		
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
	
	/**
	 * Handles TaskPickDateEvent's received by this class.
	 * 
	 * Implements the TaskPickDateEvent.Handler.onTaskPickDate() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTaskPickDate(TaskPickDateEvent event) {
		String datePickerId = event.getDatePickerId();
		DateBox db = m_dateBoxMap.get(datePickerId);
		db.showDatePicker();
	}

	/*
	 * Renders the clear all push button row into the dialog.
	 */
	private void renderClearAllRow() {
		int row	= m_taskDueDateGrid.getRowCount();
		m_taskDueDateGrid.insertRow(row);
		
		Button clearAllBtn = new Button(m_messages.taskDueDateDlgLabelClearAll());
		clearAllBtn.addStyleName("teamingButton");
		clearAllBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
//!				...this needs to be implemented...
				Window.alert("...clear all...");
			}
		});
		m_taskDueDateGrid.setWidget(row, 1, clearAllBtn);
	}
	
	/*
	 * Renders a date row into the dialog.
	 */
	private void renderDateRow(String baseId, String label) {
		// Insert the row into the table...
		int row	= m_taskDueDateGrid.getRowCount();
		m_taskDueDateGrid.insertRow(row);

		// ...determine the date to initialize the pickers with...
		TaskDate  taskDate;
		TaskEvent taskEvent = m_selectedTask.getTask().getEvent();
		if (null == taskEvent)
		     taskDate = null;
		else taskDate = (baseId.equals(IDSTART) ? taskEvent.getActualStart() : taskEvent.getActualEnd());
		Date pickerDate = ((null == taskDate) ? null : taskDate.getDate());

		// ...create the DateBox...
		m_taskDueDateGrid.setWidget(row, 0, new DlgLabel(label));
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("taskDispositionDlg_DateTimeTable");
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
		DateBox dateBox = new DateBox(new DatePicker(), pickerDate, new DateBox.DefaultFormat(dateFormat));
		String rowId = (IDBASE + baseId);
		DatePicker dp = dateBox.getDatePicker();
		dp.getElement().setId(rowId + IDDATE);
		dateBox.getElement().setId(rowId);
		hp.add(dateBox);
		m_dateBoxMap.put(rowId, dateBox);

		// ...create the associated picker button...
		TaskButton datePickerButton = new TaskButton(
			m_images.calMenu(),
			null,	// null -> No disabled image.
			m_images.calMenuOver(),
			true,	// true -> Enabled.
			null,	// null -> No alternate text necessary.
			new TaskPickDateEvent(rowId));
		hp.add(datePickerButton);

		// ...define the TimePicker...
		TimePicker tp = new TimePicker(
			((null == pickerDate) ? new Date() : pickerDate),	// Initial date.
			false,												// false -> Not 24 hour mode.
			null);												// null -> No seconds.
		tp.addStyleName("taskDispositionDlg_TimePicker taskDispositionDlg_DateTimeTable");
		tp.getElement().setId(rowId + IDTIME);
		hp.add(tp);

		// ...and finally, tie everything together.
		m_taskDueDateGrid.setWidget(row, 1, hp);
	}
	
	/*
	 * Renders the duration row into the dialog.
	 */
	private void renderDurationRow() {
		int row	= m_taskDueDateGrid.getRowCount();
		m_taskDueDateGrid.insertRow(row);
		
		m_taskDueDateGrid.setWidget(row, 0, new DlgLabel(m_messages.taskDueDateDlgLabelDuration()));
	}
}
