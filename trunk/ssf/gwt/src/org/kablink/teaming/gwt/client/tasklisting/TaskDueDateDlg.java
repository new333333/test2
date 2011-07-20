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
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskDuration;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.TimePicker;
import org.kablink.teaming.gwt.client.widgets.ValueSpinner;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
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
	private CheckBox				m_allDayCB;
	private FlexTable				m_taskDueDateTable;		// Once displayed, the table of task due date dialog's widgets.
	private int						m_durationDaysRow;		// The index of the row in the table that contains the duration days spinner.
	private Map<String, DateBox>	m_dateBoxMap;			// Map of the DateBox    widgets, indexed by their base ID.
	private Map<String, TimePicker>	m_timePickerMap;		// Map of the TimePicker widgets, indexed by their base ID.
	private TaskEvent				m_selectedTaskEvent;	// The task whose due date is being edited.
	private TaskInfo				m_selectedTask;			// The task whose due date is being edited.
	private TaskTable				m_taskTable;			// Access to the TaskTable we're prompting for.
	private ValueSpinner			m_durationDays;			// The duration days ValueSpinner.

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
	 * @param taskTable
	 * @param selectedTask
	 */
	public TaskDueDateDlg(TaskTable taskTable, TaskInfo selectedTask) {
		// Initialize the superclass...
		super(
			false,	// false -> Don't auto hide.
			true,	// true --> Dialog is modal.
			0, 0);	// The position of the dialog will be set dynamically based on the position of the widget it's being shown relative to.

		// ..register the events to be handled by this class...
		EventHelper.registerEventHandlers(
			GwtTeaming.getEventBus(),
			m_registeredEvents,
			this);
		
		// ...initialize everything else...
		m_dateBoxMap        = new HashMap<String, DateBox>();
		m_timePickerMap     = new HashMap<String, TimePicker>();
		m_taskTable         = taskTable;
		m_selectedTask      = selectedTask;
		m_selectedTaskEvent = m_selectedTask.getEvent();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.taskDueDateDlgHeader(),
			this,	// The dialog's EditSuccessfulHandler.
			this,	// The dialog's EditCanceledHandler.
			null);	// Data passed via global data members.
	}

	/*
	 * Returns a TaskDate object based on a date and time.
	 * 
	 * Note:
	 *    We use deprecated APIs here since GWT's client side has no
	 *    GregorianCalendar equivalent.  This is the only way to merge
	 *    a date an time.
	 */
	@SuppressWarnings("deprecation")
	private TaskDate getTDFromDT(Date date, Date time) {
		Date reply = CalendarUtil.copyDate(date);
		reply.setHours(time.getHours());
		reply.setMinutes(time.getMinutes());
		reply.setSeconds(time.getSeconds());
		return new TaskDate(reply);
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
				m_selectedTask.getTitle(),
				"taskDueDateDlg_TaskTitle"));

		// ...create Grid for it...
		m_taskDueDateTable = new FlexTable();
		m_taskDueDateTable.addStyleName("taskDueDateDlg_Table");
		m_taskDueDateTable.setCellPadding(0);
		m_taskDueDateTable.setCellSpacing(0);

		// ...populate the grid...
		renderAllDayRow();
		renderDateRow(IDSTART, m_messages.taskDueDateDlgLabelStart());
		renderDateRow(IDEND,   m_messages.taskDueDateDlgLabelEnd()  );
		renderDurationRow();
		renderClearAllRow();
		
		// ...and connect everything together.
		vp.add(m_taskDueDateTable);
			
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
		// Return true to allow the dialog to close.
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
		// Are the contents of the dialog valid?
		final TaskEvent reply = getNewTaskDueDate();
		if (null == reply) {
			// No!  getNewTaskDueDate() will have told the user about
			// any problem.  Simply return false to keep the dialog
			// open.
			return false;
		}
		
		// Asynchronously put the due date into affect...
		Scheduler.ScheduledCommand taskDueDateChanger;
		taskDueDateChanger = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				// Put the new due date information into affect.
				m_taskTable.applyTaskDueDate(
					reply,
					m_selectedTask.getTaskId().getEntryId());
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

	/*
	 * Returns the duration days value to use for the current task.
	 */
	private long getDurationDays() {
		long days = (-1);
		boolean hasDurationDays = hasEvent();
		if (hasDurationDays) {
			TaskDuration duration = m_selectedTaskEvent.getDuration();
			hasDurationDays = ((null != duration));
			if (hasDurationDays) {
				hasDurationDays = duration.hasDaysOnly();
				if (hasDurationDays) {
					days = duration.getDays();
				}
			}
		}
		return days;
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
	 * Validates the dialog's contents and if everything is valid,
	 * constructs a TaskEvent with the edited due date.
	 * 
	 * If anything is invalid, the user is told about the problem and
	 * null is returned.
	 */
	private TaskEvent getNewTaskDueDate() {
		String      alertMessage = null;
		TaskEvent   reply        = null;
		
		DateBox    startDate = m_dateBoxMap.get(   IDSTART); boolean hasStartDate = hasDateBoxDate(startDate); 
		TimePicker startTime = m_timePickerMap.get(IDSTART); boolean hasStartTime = startTime.hasTime();
		
		DateBox    endDate   = m_dateBoxMap.get(   IDEND  ); boolean hasEndDate   = hasDateBoxDate(endDate  );
		TimePicker endTime   = m_timePickerMap.get(IDEND  ); boolean hasEndTime   = endTime.hasTime();
		
		boolean hasDurationDays = m_durationDays.hasValue();
		
		// Is the all day checkbox checked? 
		boolean allDayChecked = m_allDayCB.getValue();
		if (allDayChecked) {
			// Yes!  Do we have both a start and end date?
			if (    (!hasStartDate) && (!hasEndDate)) alertMessage = m_messages.taskDueDateDlgError_NoStartNoEnd();
			else if (!hasStartDate)                   alertMessage = m_messages.taskDueDateDlgError_NoStart();
			else if                    (!hasEndDate)  alertMessage = m_messages.taskDueDateDlgError_NoEnd();
			else {
				// Yes!  Construct an appropriate TaskEvent to return.
				reply = new TaskEvent(true);
				reply.setAllDayEvent(true);
				reply.setActualStart(new TaskDate(startDate.getValue()));
				reply.setActualEnd(  new TaskDate(endDate.getValue()  ));
			}
		}
		
		else {
			// No, the all day checkbox was not checked!  Has anything
			// been specified?
			if ((!hasStartDate) && (!hasStartTime) &&
				(!hasEndDate)   && (!hasEndTime)   &&
				(!hasDurationDays)) {
				// No!  Bugzilla 682430:
				//    If the start, end and duration are all blank,
				//    supply a default duration days of 1.
				// Is that what the user wants to do?
				if (Window.confirm(m_messages.taskDueDateDlgConfirm_DefaultTo1Day())) {
					// Yes!  Construct an appropriate TaskEvent to
					// return.
					reply = new TaskEvent(true);
					reply.setDuration(new TaskDuration(1));
				}
			}
			
			else {
				// Yes, something has been specified!
				//    As per the Task Improvements for Evergreen
				//    design document, the following items must be
				//    supplied:
				//    1) A 'Start' date and time; or
				//    2) Both a 'Start' and an End' date and time; or
				//    3) A 'Start' date and time and a 'Duration' (in days); or
				//    4) A 'Duration' (in days.)
				// Is it valid?
				boolean hasStart = (hasStartDate && hasStartTime);
				boolean hasEnd   = (hasEndDate   && hasEndTime);
				if (hasStart && (!hasEnd) && (!hasDurationDays)) {
					// Condition 1 has been met.
					reply = new TaskEvent(true);
					reply.setActualStart(getTDFromDT(startDate.getValue(), startTime.getDateTime()));
				}
				
				else {
					if (hasStart && hasEnd && (!hasDurationDays)) {
						// Condition 2 has been met.
						reply = new TaskEvent(true);
						reply.setActualStart(getTDFromDT(startDate.getValue(), startTime.getDateTime()));
						reply.setActualEnd(  getTDFromDT(endDate.getValue(),   endTime.getDateTime()));
					}
					
					else {
						if (hasStart && (!hasEnd) && hasDurationDays) {
							// Condition 3 has been met.
							reply = new TaskEvent(true);
							reply.setActualStart(getTDFromDT(startDate.getValue(), startTime.getDateTime()));
							reply.setDuration(new TaskDuration((int) m_durationDays.getSpinner().getValue()));
						}
						
						else {
							if ((!hasStart) && (!hasEnd) && hasDurationDays) {
								// Condition 4 has been met.
								reply = new TaskEvent(true);
								reply.setDuration(new TaskDuration((int) m_durationDays.getSpinner().getValue()));
							}
							
							else {
								// One of the conditions has not been met.
								alertMessage = m_messages.taskDueDateDlgError_DurationInvalidCombination();
							}
						}
					}
				}
			}
		}
		
		// If we have a message to display...
		if (null != alertMessage) {
			// ...display it.
			Window.alert(alertMessage);
		}
		
		// If we get here, reply refers to the validated TaskEvent
		// object to return or is null (if there was an error.)  Return
		// it.
		return reply;
	}

	/*
	 * Returns true if a DateBox widget has a value and false
	 * otherwise.
	 */
	private boolean hasDateBoxDate(DateBox db) {
		String value = db.getTextBox().getValue();
		return ((null != value) && (0 < value.length()));
	}
	
	/*
	 * Returns true if the task we were given has an event associated
	 * with it and false otherwise.
	 */
	private boolean hasEvent() {
		return (null != m_selectedTaskEvent);
	}
	
	/*
	 * Returns true if the task we were given has an event associated
	 * with it that's an all day event and false otherwise.
	 */
	private boolean isAllDayEvent() {
		return (hasEvent() && m_selectedTaskEvent.getAllDayEvent());
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
	 * Renders the all day row into the dialog.
	 */
	private void renderAllDayRow() {
		m_allDayCB = new CheckBox();
		m_allDayCB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Based on the current state of the checkbox...
				CheckBox cb = ((CheckBox) event.getSource());
				boolean notAllDay = (!(cb.getValue()));
				
				// ...hide/show the time picker widgets...
				for (String tpKey:  m_timePickerMap.keySet()) {
					m_timePickerMap.get(tpKey).setVisible(notAllDay);
				}
				
				// ...and hide/show the duration days row.
				m_taskDueDateTable.getRowFormatter().setVisible(m_durationDaysRow, notAllDay);
			}
		});
		setAllDay();
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("margintop1a");
		hp.add(m_allDayCB);
		hp.add(new DlgLabel(m_messages.taskDueDateDlgLabelAllDay()));
		m_taskDueDateTable.setWidget(m_taskDueDateTable.getRowCount(), 1, hp);
	}
	
	/*
	 * Renders the clear all push button row into the dialog.
	 */
	private void renderClearAllRow() {
		Button clearAllBtn = new Button(m_messages.taskDueDateDlgLabelClearAll());
		clearAllBtn.addStyleName("teamingButton margintop1a");
		clearAllBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Clear the date boxes...
				for (String dbKey:  m_dateBoxMap.keySet()) {
					m_dateBoxMap.get(dbKey).setValue(null);
				}
				
				// ...clear the time pickers...
				for (String tpKey:  m_timePickerMap.keySet()) {
					m_timePickerMap.get(tpKey).clearTime();
				}
				
				// ...and clear the duration days spinner.
				m_durationDays.clearValue();
			}
		});
		m_taskDueDateTable.setWidget(m_taskDueDateTable.getRowCount(), 1, clearAllBtn);
	}
	
	/*
	 * Renders a date row into the dialog.
	 */
	private void renderDateRow(String baseId, String label) {
		// Determine the date to initialize the pickers with...
		TaskDate taskDate;
		if (!hasEvent())
		     taskDate = null;
		else taskDate = (baseId.equals(IDSTART) ? m_selectedTaskEvent.getActualStart() : m_selectedTaskEvent.getActualEnd());
		Date pickerDate = ((null == taskDate) ? null : taskDate.getDate());

		// ...create the DateBox...
		int row	= m_taskDueDateTable.getRowCount();
		m_taskDueDateTable.setWidget(row, 0, new DlgLabel(label));
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("taskDispositionDlg_DateTimeTable");
		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
		DateBox dateBox = new DateBox(new DatePicker(), pickerDate, new DateBox.DefaultFormat(dateFormat));
		String rowId = (IDBASE + baseId);
		DatePicker dp = dateBox.getDatePicker();
		dp.getElement().setId(rowId + IDDATE);
		dateBox.getElement().setId(rowId);
		hp.add(dateBox);
		m_dateBoxMap.put(baseId, dateBox);

		// ...create the associated picker button...
		TaskButton datePickerButton = new TaskButton(
			m_images.calMenu(),
			null,	// null -> No disabled image.
			m_images.calMenuOver(),
			true,	// true -> Enabled.
			null,	// null -> No alternate text necessary.
			new TaskPickDateEvent(baseId));
		hp.add(datePickerButton);

		// ...define the TimePicker...
		TimePicker tp = new TimePicker(
			pickerDate,	// Initial date.
			false,		// false -> Not 24 hour mode.
			null);		// null -> No seconds.
		tp.addStyleName("taskDispositionDlg_TimePicker taskDispositionDlg_DateTimeTable");
		tp.getElement().setId(rowId + IDTIME);
		hp.add(tp);
		m_timePickerMap.put(baseId, tp);
		if (isAllDayEvent()) {
			tp.setVisible(false);
		}

		// ...and finally, tie everything together.
		m_taskDueDateTable.setWidget(row, 1, hp);
	}
	
	/*
	 * Renders the duration row into the dialog.
	 */
	private void renderDurationRow() {
		m_durationDaysRow = m_taskDueDateTable.getRowCount();
		m_taskDueDateTable.setWidget(m_durationDaysRow, 0, new DlgLabel(m_messages.taskDueDateDlgLabelDuration()));
		
		HorizontalPanel hp = new HorizontalPanel();
		m_taskDueDateTable.setWidget(m_durationDaysRow, 1, hp);
		
		m_durationDays = new ValueSpinner(1, 1, Integer.MAX_VALUE, 1, 1);
		m_durationDays.getTextBox().setVisibleLength(5);
		hp.add(m_durationDays);
		hp.add(new DlgLabel(m_messages.taskDueDateDlgLabelDays()));
		
		setDurationDays();
	}
	/**
	 * Resets the dialog based on a new task.
	 * 
	 * @param selectedTask
	 */
	public void resetDueDateTask(TaskInfo selectedTask) {
		// Store the new task information...
		m_selectedTask      = selectedTask;
		m_selectedTaskEvent = m_selectedTask.getEvent();

		// ...and put it into affect.
		setAllDay();
		setDueDate(IDSTART);
		setDueDate(IDEND  );
		setDurationDays();
	}

	/*
	 * Sets the value of the all day event check box.
	 */
	private void setAllDay() {
		// Simply set the state of the all day checkbox.
		m_allDayCB.setValue(isAllDayEvent());
	}

	/*
	 * Set the value of a set of the due date widgets. 
	 */
	private void setDueDate(String baseId) {
		// Extract the date for the picker...
		TaskDate taskDate;
		if (!hasEvent())
		     taskDate = null;
		else taskDate = (baseId.equals(IDSTART) ? m_selectedTaskEvent.getActualStart() : m_selectedTaskEvent.getActualEnd());
		Date pickerDate = ((null == taskDate) ? null : taskDate.getDate());

		// ...use it to set the date box...
		DateBox db = m_dateBoxMap.get(baseId);
		db.setValue(pickerDate);

		// ...and time picker.
		TimePicker tp = m_timePickerMap.get(baseId);
		tp.setDateTime(pickerDate);
		tp.setVisible(!(isAllDayEvent())); 
	}

	/*
	 * Sets the value of the duration days widgets.
	 */
	private void setDurationDays() {
		// If we don't have a duration days value...
		long days = getDurationDays();
		boolean hasDurationDays = ((-1) != days);
		m_durationDays.getSpinner().setValue((hasDurationDays ? days : 1), true);
		if (!hasDurationDays) {
			// ...clear the spinner...
			m_durationDays.clearValue();
		}
		
		// ...and hide/show the widget based on whether this task is
		// ...an all day event.
		m_taskDueDateTable.getRowFormatter().setVisible(m_durationDaysRow, (!(isAllDayEvent())));
	}
}
