/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EventButton;
import org.kablink.teaming.gwt.client.widgets.TimePicker;
import org.kablink.teaming.gwt.client.widgets.ValueSpinner;
import org.kablink.teaming.gwt.client.widgets.TZDateBox;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements a dialog for editing a task's due date.
 *  
 * Note:
 *    We use deprecated APIs here since GWT's client side has no
 *    Calendar equivalent.  This is the only way to manipulate a date
 *    and time.
 *    
 * @author drfoster@novell.com
 */
public class TaskDueDateDlg extends DlgBox
	implements EditSuccessfulHandler,
		// Event handlers implemented by this class.
		TaskPickDateEvent.Handler
{
	private CheckBox					m_allDayCB;					// The all day checkbox.
	private DlgLabel					m_bannerLabel;				// The banner at the top of the dialog containing the task's title.
	private FlexTable					m_taskDueDateTable;			// Once displayed, the table of task due date dialog's widgets.
	private int							m_durationDaysRow;			// The index of the row in the table that contains the duration days spinner.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private Map<String, TZDateBox>		m_dateBoxMap;				// Map of the TZDateBox  widgets, indexed by their base ID.
	private Map<String, TimePicker>		m_timePickerMap;			// Map of the TimePicker widgets, indexed by their base ID.
	private TaskEvent					m_selectedTaskEvent;		// The task whose due date is being edited.
	private TaskInfo					m_selectedTask;				// The task whose due date is being edited.
	private TaskTable					m_taskTable;				// Access to the TaskTable we're prompting for.
	private ValueSpinner				m_durationDays;				// The duration days ValueSpinner.

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
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.TASK_PICK_DATE,
	};
	
	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		/**
		 * Class constructor.
		 * 
		 * @param label
		 * @param addedStyle
		 */
		public DlgLabel(String label, String addedStyle) {
			super(label);
			setStyles(addedStyle);
		}

		/**
		 * Class constructor.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			this(label, null);
		}

		/**
		 * Class constructor.
		 * 
		 * @param ignored
		 * @param addedStyle
		 */
		public DlgLabel(boolean ignored, String addedStyle) {
			super();
			setStyles(addedStyle);
		}

		/*
		 * Sets the DlgLabel's styles.
		 */
		private void setStyles(String addedStyle) {
			addStyleName("taskDueDateDlg_Label");
			if (!(GwtClientHelper.hasString(addedStyle))) {
				addedStyle = "gwtUI_nowrap";
			}
			addStyleName(addedStyle);
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

		// ...initialize everything else...
		m_dateBoxMap        = new HashMap<String, TZDateBox>();
		m_timePickerMap     = new HashMap<String, TimePicker>();
		m_taskTable         = taskTable;
		m_selectedTask      = selectedTask;
		m_selectedTaskEvent = m_selectedTask.getEvent();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.taskDueDateDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Data passed via global data members.
	}

	/*
	 * Validates the dialog's contents and if everything is valid,
	 * applies a TaskEvent with the edited due date and closes the
	 * dialog.
	 * 
	 * If anything is invalid, the user is told about the problem and
	 * the dialog is left open.
	 */
	private void applyNewTaskDueDate() {
		TZDateBox  startDate = m_dateBoxMap.get(   IDSTART); boolean hasStartDate = hasDateBoxDate(startDate); 
		TimePicker startTime = m_timePickerMap.get(IDSTART); boolean hasStartTime = startTime.hasTime();
		
		TZDateBox  endDate   = m_dateBoxMap.get(   IDEND  ); boolean hasEndDate   = hasDateBoxDate(endDate  );
		TimePicker endTime   = m_timePickerMap.get(IDEND  ); boolean hasEndTime   = endTime.hasTime();
		
		boolean hasDurationDays = m_durationDays.hasValue();
		
		// Is the all day checkbox checked? 
		boolean allDayChecked = m_allDayCB.getValue();
		if (allDayChecked) {
			// Yes!  Do we have both a start and end date?
			if (    (!hasStartDate) && (!hasEndDate)) {GwtClientHelper.deferredAlert(m_messages.taskDueDateDlgError_NoStartNoEnd()); setOkEnabled(true);}
			else if (!hasStartDate)                   {GwtClientHelper.deferredAlert(m_messages.taskDueDateDlgError_NoStart()     ); setOkEnabled(true);}
			else if                    (!hasEndDate)  {GwtClientHelper.deferredAlert(m_messages.taskDueDateDlgError_NoEnd()       ); setOkEnabled(true);}
			else {
				// Yes!  Construct an appropriate TaskEvent to and
				// apply it.
				TaskEvent event = new TaskEvent(true);	// true -> Initialize with null dates.
				event.setAllDayEvent( true                                               );				
				event.setActualStart( new TaskDate(getAllDayDateOnSave(startDate, true )));	// true  -> Start date.
				event.setActualEnd(   new TaskDate(getAllDayDateOnSave(  endDate, false)));	// false -> End   date.
				applyNewTaskDueDateImpl(event);
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
				
				
				// Is the user sure they want to delete the selected user
				// workspaces?
				ConfirmDlg.createAsync(new ConfirmDlgClient() {
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(ConfirmDlg cDlg) {
						ConfirmDlg.initAndShow(
							cDlg,
							new ConfirmCallback() {
								@Override
								public void dialogReady() {
									// Ignored.  We don't really care when the
									// dialog is ready.
								}

								@Override
								public void accepted() {
									// Yes!  Construct an appropriate
									// TaskEvent and apply it.
									TaskEvent event = new TaskEvent(true);
									event.setDuration(new TaskDuration(1));
									applyNewTaskDueDateImpl(event);
								}

								@Override
								public void rejected() {
									// No, they're not sure!
									setOkEnabled(true);
								}
							},
							m_messages.taskDueDateDlgConfirm_DefaultTo1Day());
					}
				});
			}
			
			else {
				// Yes, something has been specified!
				//    As per the Task Improvements for Evergreen
				//    design document, the following items must be
				//    supplied:
				//    1) A 'Start' date; or
				//    2) Both a 'Start' and an End' date; or
				//    3) A 'Start' date and a 'Duration' (in days); or
				//    4) A 'Duration' (in days.)
				// Is it valid?
				TaskEvent event    = null;
				boolean   hasStart = (hasStartDate && true);	// hasStartTime);	// Commented out and leave it to the defaults...
				boolean   hasEnd   = (hasEndDate   && true);	// hasEndTime);		// ...as per Bugzilla 712328 and 714419.
				if (hasStart && (!hasEnd) && (!hasDurationDays)) {
					// Condition 1 has been met.
					event = new TaskEvent(true);
					event.setActualStart(getTDFromDT(new Date(startDate.getValue()), startTime.getDateTime()));
				}
				
				else {
					if (hasStart && hasEnd && (!hasDurationDays)) {
						// Condition 2 has been met.
						event = new TaskEvent(true);
						event.setActualStart(getTDFromDT(new Date(startDate.getValue()), startTime.getDateTime()));
						event.setActualEnd(  getTDFromDT(new Date(endDate.getValue()),   endTime.getDateTime()));
					}
					
					else {
						if (hasStart && (!hasEnd) && hasDurationDays) {
							// Condition 3 has been met.
							event = new TaskEvent(true);
							event.setActualStart(getTDFromDT(new Date(startDate.getValue()), startTime.getDateTime()));
							event.setDuration(new TaskDuration((int) m_durationDays.getSpinner().getValue()));
						}
						
						else {
							if ((!hasStart) && (!hasEnd) && hasDurationDays) {
								// Condition 4 has been met.
								event = new TaskEvent(true);
								event.setDuration(new TaskDuration((int) m_durationDays.getSpinner().getValue()));
							}
							
							else {
								// One of the conditions has not been met.
								GwtClientHelper.deferredAlert(m_messages.taskDueDateDlgError_DurationInvalidCombination());
								setOkEnabled(true);
							}
						}
					}
				}
				
				// Apply any event we may have built.
				applyNewTaskDueDateImpl(event);
			}
		}
	}
	
	/*
	 * If we were given an event to apply, apply it and close the
	 * dialog.  Otherwise, do nothing.
	 */
	private void applyNewTaskDueDateImpl(final TaskEvent event) {
		// Do we have an event to apply?
		if (null != event) {
			// Yes!  Asynchronously put the due date into affect...
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					// Put the new due date information into affect.
					m_taskTable.applyTaskDueDate(
						event,
						m_selectedTask.getTaskId().getEntityId());
				}
			});
			
			// ...and close the dialog.
			setOkEnabled(true);
			hide();
		}
		
		else {
			// May be null in the case of an error in which case we
			// still need the OK enabled.
			setOkEnabled(true);
		}
	}

	/*
	 * Returns a TaskDate object based on a date and time.
	 * 
	 * If the time is null, midnight is used.
	 */
	@SuppressWarnings("deprecation")
	private TaskDate getTDFromDT(Date date, Date time) {
		Date reply = CalendarUtil.copyDate(date);
		boolean hasTime = (null != time);
		reply.setHours(  (hasTime ? time.getHours()   : 0));
		reply.setMinutes((hasTime ? time.getMinutes() : 0));
		reply.setSeconds((hasTime ? time.getSeconds() : 0));
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
		m_bannerLabel = new DlgLabel(false, "taskDueDateDlg_TaskTitle");
		vp.add(m_bannerLabel);
		setBanner();

		// ...create Grid for it...
		m_taskDueDateTable = new FlexTable();
		m_taskDueDateTable.addStyleName("taskDueDateDlg_Table");
		m_taskDueDateTable.setCellPadding(0);
		m_taskDueDateTable.setCellSpacing(3);

		// ...populate the grid...
		renderAllDayRow();
		renderDateRow(true,  m_messages.taskDueDateDlgLabelStart());	// true --> Start date.
		renderDateRow(false, m_messages.taskDueDateDlgLabelEnd()  );	// false -> End date.
		renderDurationRow();
		renderClearAllRow();
		
		// ...and connect everything together.
		vp.add(m_taskDueDateTable);
			
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
		// Apply the change...
		setOkEnabled(false);
		applyNewTaskDueDate();
		
		// ...and return false.  The apply will close the dialog if we
		// ...were successful.
		return false;
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
		long days;
		boolean hasDurationDays = (hasEvent() && m_selectedTaskEvent.requiresDateCalculations());
		if (hasDurationDays)
		     days = m_selectedTaskEvent.getDuration().getDays();
		else days = (-1);
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
	 * Returns true if a DateBox widget has a value and false
	 * otherwise.
	 */
	private boolean hasDateBoxDate(TZDateBox db) {
		String value = db.getDateBox().getTextBox().getValue();
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

	/*
	 * Returns the Date to use as the start or end date of an all day
	 * event when saving the dialog's results.
	 */
	@SuppressWarnings("deprecation")
	private Date getAllDayDateOnSave(Date dIn, boolean isStart) {
		long t = dIn.getTime();
		Date d;
		if (isStart) {
			d = new Date(t - GwtClientHelper.getTimeZoneOffsetMillis(dIn));
		}
		
		else {
			d = new Date(t);
			d.setHours(24);										//
			int tzoM = GwtClientHelper.getTimeZoneOffset(d);	// Time zone offset, in minutes.
			d.setMinutes(-tzoM);								//
			d.setSeconds(-1   );								// An all day end is always at 1 second before midnight GMT.
		}
		return d;
	}
	
	private Date getAllDayDateOnSave(TZDateBox db, boolean isStart) {
		// Always use the initial form of the method.
		return getAllDayDateOnSave(new Date(db.getValue()), isStart);
	}
	
	/*
	 * Returns the Date to use as the start or end date of an event to
	 * initialize the date picker when entering the dialog.
	 */
	private Date getPickerDateOnEntry(TaskDate taskDate) {
		boolean hasDate = (null != taskDate) && (null != taskDate.getDate());
		Date    reply   = (hasDate ? new Date(taskDate.getDate().getTime()) : null);
		if (hasDate && isAllDayEvent()) {
			Long t = reply.getTime();
			reply.setTime(t + GwtClientHelper.getTimeZoneOffsetMillis(reply));
		}
		return reply;
	}
	
	private Date getPickerDateOnEntry(boolean isStart) {
		// Extract the appropriate TaskDate from the event...
		TaskDate taskDate;
		if (hasEvent())
		     taskDate = (isStart ? m_selectedTaskEvent.getActualStart() : m_selectedTaskEvent.getActualEnd());
		else taskDate = null;
		
		// ...and always use the initial form of the method.
		return getPickerDateOnEntry(taskDate);
	}
	
    /**
     * Called after the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingEnded() method.
     */
	@Override
    protected void okBtnProcessingEnded() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
    /**
     * Called before the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingStarted() method.
     */
	@Override
    protected void okBtnProcessingStarted() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
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
		TZDateBox db = m_dateBoxMap.get(datePickerId);
		db.getDateBox().showDatePicker();
	}

	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
		}
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
				boolean allDay = cb.getValue();
				
				// ...hide/show the time picker widgets...
				for (String tpKey:  m_timePickerMap.keySet()) {
					TimePicker tp = m_timePickerMap.get(tpKey);
					if (allDay) {
						tp.clearTime();
					}
					tp.setVisible(!allDay);
				}

				// ...and hide/show the duration days row.
				m_taskDueDateTable.getRowFormatter().setVisible(m_durationDaysRow, (!allDay));
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
	private void renderDateRow(boolean isStart, String label) {
		// Determine the date to initialize the pickers with...
		Date pickerDate = getPickerDateOnEntry(isStart);

		// ...create the DateBox...
		int row	= m_taskDueDateTable.getRowCount();
		m_taskDueDateTable.setWidget(row, 0, new DlgLabel(label));
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("taskDispositionDlg_DateTimeTable");
		DateTimeFormat dateFormat = GwtClientHelper.getShortDateFormat();
		TZDateBox dateBox = new TZDateBox(new DatePicker(), (-1), new DateBox.DefaultFormat(dateFormat));
		dateBox.setTZOffset(0);
		if (null != pickerDate) {
			dateBox.setValue(pickerDate.getTime());
		}
		String baseId = (isStart ? IDSTART : IDEND);
		String rowId = (IDBASE + baseId);
		DatePicker dp = dateBox.getDateBox().getDatePicker();
		dp.getElement().setId(rowId + IDDATE);
		dateBox.getElement().setId(rowId);
		hp.add(dateBox);
		m_dateBoxMap.put(baseId, dateBox);

		// ...create the associated picker button...
		EventButton datePickerButton = new EventButton(
			m_images.calMenu(),
			null,	// null -> No disabled image.
			m_images.calMenuOver(),
			true,	// true -> Enabled.
			null,	// null -> No alternate text necessary.
			new TaskPickDateEvent(baseId));
		hp.add(datePickerButton);
		
		// ...define the TimePicker...
		String  myTime   = (GwtClientHelper.getShortTimeFormat().format(new Date())).toLowerCase();
		boolean is24Hour = ((0 > myTime.indexOf("am")) && (0 > myTime.indexOf("pm")));
		TimePicker tp = new TimePicker(
			pickerDate,	// Initial date.
			is24Hour,	// AM/PM vs. 24 hour mode.
			null);		// null -> No seconds.
		tp.addStyleName("taskDispositionDlg_TimePicker taskDispositionDlg_DateTimeTable");
		tp.getElement().setId(rowId + IDTIME);
		hp.add(tp);
		m_timePickerMap.put(baseId, tp);
		if (isAllDayEvent()) {
			tp.clearTime();
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
		setBanner();
		setAllDay();
		setDueDate(true );	// true --> Start date.
		setDueDate(false);	// false -> End date.
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
	 * Sets the value of the banner label to the task's title.
	 */
	private void setBanner() {
		m_bannerLabel.setText(m_selectedTask.getTitle());
	}
	
	/*
	 * Set the value of a set of the due date widgets. 
	 */
	private void setDueDate(boolean isStart) {
		// Extract the date for the picker...
		Date pickerDate = getPickerDateOnEntry(isStart);

		// ...use it to set the date box...
		String baseId = (isStart ? IDSTART : IDEND);
		TZDateBox db = m_dateBoxMap.get(baseId);
		db.setValue((null == pickerDate) ? (-1) : pickerDate.getTime());

		// ...and time picker.
		TimePicker tp = m_timePickerMap.get(baseId);
		tp.setDateTime(pickerDate);
		if (isAllDayEvent()) {
			tp.clearTime();
		}
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
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
}
