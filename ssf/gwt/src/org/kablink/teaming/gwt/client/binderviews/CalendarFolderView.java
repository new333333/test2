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

package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.datatable.AddFilesDlg;
import org.kablink.teaming.gwt.client.datatable.AddFilesDlg.AddFilesDlgClient;
import org.kablink.teaming.gwt.client.event.CalendarChangedEvent;
import org.kablink.teaming.gwt.client.event.CalendarGotoDateEvent;
import org.kablink.teaming.gwt.client.event.CalendarHoursEvent;
import org.kablink.teaming.gwt.client.event.CalendarNextPeriodEvent;
import org.kablink.teaming.gwt.client.event.CalendarPreviousPeriodEvent;
import org.kablink.teaming.gwt.client.event.CalendarSettingsEvent;
import org.kablink.teaming.gwt.client.event.CalendarShowEvent;
import org.kablink.teaming.gwt.client.event.CalendarViewDaysEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarAppointmentsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarAppointmentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarNextPreviousPeriodCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveCalendarDayViewCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveCalendarHoursCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveCalendarShowCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CalendarAppointment;
import org.kablink.teaming.gwt.client.util.CalendarAttendee;
import org.kablink.teaming.gwt.client.util.CalendarDayView;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.CalendarSettingsDlg;
import org.kablink.teaming.gwt.client.widgets.CalendarSettingsDlg.CalendarSettingsDlgClient;
import org.kablink.teaming.gwt.client.widgets.VibeCalendar;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.CalendarFormat;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings;
import com.bradrydzewski.gwt.calendar.client.CalendarViews;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Calendar folder view.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class CalendarFolderView extends FolderViewBase
	implements CalendarDisplayDataProvider,
		// Event handlers implemented by this class.
		CalendarGotoDateEvent.Handler,
		CalendarHoursEvent.Handler,
		CalendarNextPeriodEvent.Handler,
		CalendarPreviousPeriodEvent.Handler,
		CalendarSettingsEvent.Handler,
		CalendarShowEvent.Handler,
		CalendarViewDaysEvent.Handler,
		ContributorIdsRequestEvent.Handler,
		InvokeDropBoxEvent.Handler,
		QuickFilterEvent.Handler
{
	private ArrayList<Appointment>				m_appointments;				//
	private AddFilesDlg							m_addFilesDlg;				//
	private CalendarDisplayDataRpcResponseData	m_calendarDisplayData;		//
	private CalendarSettingsDlg					m_calendarSettingsDlg;		//
	private List<HandlerRegistration>			m_registeredEventHandlers;	// Event handlers that are currently registered.
	private String								m_quickFilter;				// Any quick filter that's active.
	private VibeCalendar						m_calendar;					// The calendar widget contained in the view.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CALENDAR_GOTO_DATE,
		TeamingEvents.CALENDAR_HOURS,
		TeamingEvents.CALENDAR_NEXT_PERIOD,
		TeamingEvents.CALENDAR_PREVIOUS_PERIOD,
		TeamingEvents.CALENDAR_SETTINGS,
		TeamingEvents.CALENDAR_SHOW,
		TeamingEvents.CALENDAR_VIEW_DAYS,
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.INVOKE_DROPBOX,
		TeamingEvents.QUICK_FILTER,
	};
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public CalendarFolderView(BinderInfo folderInfo, ViewReady viewReady) {
		// Initialize the super class...
		super(folderInfo, viewReady, "vibe-calendarFolder", false);
		
		// ...and tell it that we can provide the calendar display data
		// ...if it needs it.
		setCalendarDisplayDataProvider(this);
	}
	
	/**
	 * Called to construct the view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		loadPart1Async();
	}

	/*
	 * Asynchronously loads the display data for the next/previous
	 * period.
	 */
	private void doCalendarNextPreviousPeriodAsync(final boolean next) {
		Scheduler.ScheduledCommand doNextPreviousPeriod = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				doCalendarNextPreviousPeriodNow(next);
			}
		};
		Scheduler.get().scheduleDeferred(doNextPreviousPeriod);
	}
	
	/*
	 * Synchronously loads the display data for the next/previous
	 * period.
	 */
	private void doCalendarNextPreviousPeriodNow(boolean next) {
		GwtClientHelper.executeCommand(
				new GetCalendarNextPreviousPeriodCmd(getFolderInfo().getBinderIdAsLong(), m_calendarDisplayData, next),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetCalendarNextPreviousPeroid());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes!  Put the new calendar display data into
				// affect...
				m_calendarDisplayData = ((CalendarDisplayDataRpcResponseData) response.getResponseData());
				GwtTeaming.fireEvent(new CalendarChangedEvent(getFolderId(), m_calendarDisplayData));

				// ...and repopulate the view.
				setCalendarDisplay();
				m_calendar.clearAppointments();
				populateCalendarEventsAsync();
			}
		});
	}
	
	/**
	 * Returns the CalendarDisplayDataRpcResponseData used by this view.
	 * 
	 * Implements the CalendarDisplayDataProvider.getCalendarDisplayData() method.
	 * 
	 * @return
	 */
	@Override
	public void getCalendarDisplayData(final AsyncCalendarDisplayDataCallback cb) {
		// If we've already loaded the calendar display data...
		if (null != m_calendarDisplayData) {
			// ...simply return it.
			cb.success(m_calendarDisplayData);
		}
		
		else {
			// Otherwise, load it now.
			GwtClientHelper.executeCommand(
					new GetCalendarDisplayDataCmd(getFolderInfo()),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetCalendarDisplayData(),
						getFolderInfo().getBinderIdAsLong());
					cb.failure();
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the calendar display data and return it
					// through the callback.
					m_calendarDisplayData = ((CalendarDisplayDataRpcResponseData) response.getResponseData());
					cb.success(m_calendarDisplayData);
				}
			});
		}
	}

	/**
	 * Returns true for panels that are to be included and false
	 * otherwise.
	 * 
	 * Overrides the FolderViewBase.includePanel() method.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	@Override
	protected boolean includePanel(FolderPanels folderPanel) {
		// In the calendar folder view, we add the calendar navigation
		// panel beyond the default.
		boolean reply;
		switch (folderPanel) {
		case CALENDAR_NAVIGATION:  reply = true;                             break;
		default:                   reply = super.includePanel(folderPanel);  break;
		}
		return reply;
	}

	/*
	 * Asynchronously loads the calendar display data.
	 */
	private void loadPart1Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the calendar display data.
	 */
	private void loadPart1Now() {
		// If we've already loaded the calendar display data...
		if (null != m_calendarDisplayData) {
			// ...simply continue the load process.
			loadPart2Async();
		}
		
		else {
			// Otherwise, load it now.
			GwtClientHelper.executeCommand(
					new GetCalendarDisplayDataCmd(getFolderInfo()),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetCalendarDisplayData(),
						getFolderInfo().getBinderIdAsLong());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the calendar display data and continue
					// loading the view.
					m_calendarDisplayData = ((CalendarDisplayDataRpcResponseData) response.getResponseData());
					loadPart2Async();
				}
			});
		}
	}

	/*
	 * Asynchronously loads the Calendar widget.
	 */
	private void loadPart2Async() {
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the Calendar widget.
	 */
	private void loadPart2Now() {
		// Create the Calendar widget for the view and populate it.
		m_calendar = new VibeCalendar();
		setCalendarDisplay();
		populateViewAsync();
	}

	/**
	 * Called when the calendar folder view is attached.
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
	 * Handles CalendarGotoDateEvent's received by this class.
	 * 
	 * Implements the CalendarGotoDateEvent.Handler.onCalendarGotoDate() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarGotoDate(CalendarGotoDateEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(getFolderId())) {
			// Yes!  Tell the calendar to goto the requested date.
			m_calendar.setDate(event.getDate());
			GwtTeaming.fireEvent(new CalendarChangedEvent(getFolderId(), m_calendarDisplayData));
		}
	}
	
	/**
	 * Handles CalendarHoursEvent's received by this class.
	 * 
	 * Implements the CalendarHoursEvent.Handler.onCalendarHours() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarHours(CalendarHoursEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(getFolderId())) {
			// Yes!  Can we can save the hours setting on the server?
			GwtClientHelper.executeCommand(
					new SaveCalendarHoursCmd(getFolderInfo(), event.getHours()),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_SaveCalendarHours());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Yes!  Put the new calendar display data into
					// affect...
					m_calendarDisplayData = ((CalendarDisplayDataRpcResponseData) response.getResponseData());
					GwtTeaming.fireEvent(new CalendarChangedEvent(getFolderId(), m_calendarDisplayData));

					// ...and repopulate the view.
					setCalendarDisplay();
					m_calendar.clearAppointments();
					populateCalendarEventsAsync();
				}
			});
		}
	}
	
	/**
	 * Handles CalendarNextPeriodEvent's received by this class.
	 * 
	 * Implements the CalendarNextPeriodEvent.Handler.onCalendarNextPeriod() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarNextPeriod(CalendarNextPeriodEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(getFolderId())) {
			// Yes!  Load the display data for the next period.
			doCalendarNextPreviousPeriodAsync(true);	// true -> Next period.
		}
	}
	
	/**
	 * Handles CalendarPreviousPeriodEvent's received by this class.
	 * 
	 * Implements the CalendarPreviousPeriodEvent.Handler.onCalendarPreviousPeriod() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarPreviousPeriod(CalendarPreviousPeriodEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(getFolderId())) {
			// Yes!  Load the display data for the previous period.
			doCalendarNextPreviousPeriodAsync(false);	// false -> Previous period.
		}
	}
	
	/**
	 * Handles CalendarSettingsEvent's received by this class.
	 * 
	 * Implements the CalendarSettingsEvent.Handler.onCalendarSettings() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarSettings(CalendarSettingsEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(getFolderId())) {
			// Yes!  Have we instantiated a calendar settings dialog
			// yet?
			if (null == m_calendarSettingsDlg) {
				// No!  Instantiate one now.
				CalendarSettingsDlg.createAsync(new CalendarSettingsDlgClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(final CalendarSettingsDlg csDlg) {
						// ...and show it.
						m_calendarSettingsDlg = csDlg;
						showCalendarSettingsDlgAsync();
					}
				});
			}
			
			else {
				// Yes, we've instantiated a calendar settings dialog
				// already! Simply show it.
				showCalendarSettingsDlgAsync();
			}
		}
	}
	
	/**
	 * Handles CalendarShowEvent's received by this class.
	 * 
	 * Implements the CalendarShowEvent.Handler.onCalendarShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarShow(CalendarShowEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(getFolderId())) {
			// Yes!  Can we can save the show setting on the server?
			GwtClientHelper.executeCommand(
					new SaveCalendarShowCmd(getFolderInfo(), event.getShow()),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_SaveCalendarShow());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Yes!  Put the new calendar display data into
					// affect.  Note that we refresh the full UI
					// here and not just the calendar.  We do this so
					// that the entry menu, ... also get updated to
					// reflect the change.
					FullUIReloadEvent.fireOne();
				}
			});
		}
	}
	
	/**
	 * Handles CalendarViewDaysEvent's received by this class.
	 * 
	 * Implements the CalendarViewDaysEvent.Handler.onCalendarViewDays() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarViewDays(CalendarViewDaysEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(getFolderId())) {
			// Yes!  Can we can save the view setting on the server?
			GwtClientHelper.executeCommand(
					new SaveCalendarDayViewCmd(getFolderInfo(), event.getDayView()),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_SaveCalendarDayView());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Yes!  Put the new calendar display data into
					// affect...
					m_calendarDisplayData = ((CalendarDisplayDataRpcResponseData) response.getResponseData());
					GwtTeaming.fireEvent(new CalendarChangedEvent(getFolderId(), m_calendarDisplayData));

					// ...and repopulate the view.
					setCalendarDisplay();
					m_calendar.clearAppointments();
					populateCalendarEventsAsync();
				}
			});
		}
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsRequest(ContributorIdsRequestEvent event) {
		// Is the event targeted to this folder?
		if (event.getBinderId().equals(getFolderId())) {
			// Yes!
//!			...this needs to be implemented...
			Window.alert("CalendarFolderView.onContributorIdsRequest():  ...this needs to be implemented...");
		}
	}
	
	/**
	 * Called when the calendar folder view is detached.
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
	 * Handles InvokeDropBoxEvent's received by this class.
	 * 
	 * Implements the InvokeDropBoxEvent.Handler.onInvokeDropBox() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeDropBox(InvokeDropBoxEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Invoke the add file dialog on the folder.
			// Have we instantiated an add files dialog yet?
			if (null == m_addFilesDlg) {
				// No!  Instantiate one now.
				AddFilesDlg.createAsync(new AddFilesDlgClient() {			
					@Override
					public void onUnavailable() {
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}
					
					@Override
					public void onSuccess(final AddFilesDlg afDlg) {
						// ...and show it.
						m_addFilesDlg = afDlg;
						ScheduledCommand doShow = new ScheduledCommand() {
							@Override
							public void execute() {
								showAddFilesDlgNow();
							}
						};
						Scheduler.get().scheduleDeferred(doShow);
					}
				});
			}
			
			else {
				// Yes, we've instantiated an add files dialog already!
				// Simply show it.
				showAddFilesDlgNow();
			}
		}
	}
	
	/**
	 * Handles QuickFilterEvent's received by this class.
	 * 
	 * Implements the QuickFilterEvent.Handler.onQuickFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onQuickFilter(QuickFilterEvent event) {
		// Is the event is targeted to the folder we're viewing?
		if (event.getFolderId().equals(getFolderInfo().getBinderIdAsLong())) {
			// Yes!  Track the current quick filter and force the
			// calendar to refresh with it.
			m_quickFilter = event.getQuickFilter();
			m_calendar.clearAppointments();
			populateCalendarEventsAsync();
		}
	}

	/*
	 * Asynchronously populates the the calendar's events.
	 */
	private void populateCalendarEventsAsync() {
		Scheduler.ScheduledCommand doPopulate = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				populateCalendarEventsNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the the calendar's events.
	 */
	private void populateCalendarEventsNow() {
		// Can we read the appointments for the calendar based on the
		// current calendar display data?
		GwtClientHelper.executeCommand(
				new GetCalendarAppointmentsCmd(getFolderInfo().getBinderIdAsLong(), m_calendarDisplayData),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetCalendarAppointments());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes!  Add the appointments to the calendar.
				CalendarAppointmentsRpcResponseData responseData = ((CalendarAppointmentsRpcResponseData) response.getResponseData());
				m_appointments = responseData.getAppointments();
				m_calendar.suspendLayout();
				m_calendar.addAppointments(m_appointments);
				m_calendar.resumeLayout();
			}
		});
	}
	
	/*
	 * Asynchronously populates the the calendar view.
	 */
	private void populateViewAsync() {
		Scheduler.ScheduledCommand doPopulate = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				populateViewNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the the calendar view.
	 */
	private void populateViewNow() {
		// Put the calendar in the view...
		getFlowPanel().add(m_calendar);
		viewReady();

		// ...and populate it's events.
		populateCalendarEventsAsync();
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
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/**
	 * Called from the base class to reset the content of this
	 * discussion folder view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView() {
		getFlowPanel().clear();
		populateViewAsync();
	}
	
	/**
	 * Synchronously sets the size of the view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Get the sizes we need to calculate the height of the <DIV>.
		FooterPanel fp  = getFooterPanel();
		int viewHeight	= getOffsetHeight();							// Height of the view.
		int viewTop		= getAbsoluteTop();								// Absolute top of the view.		
		int cTop		= (m_calendar.getAbsoluteTop() - viewTop);		// Top of the calendar relative to the top of the view.		
		int fpHeight	= ((null == fp) ? 0 : fp.getOffsetHeight());	// Height of the view's footer panel.
		int totalBelow	= fpHeight;										// Total space on the page below the calendar.

		// What's the optimum height for the calendar so we don't get a
		// vertical scroll bar?
		int cHeight = (((viewHeight - cTop) - totalBelow) - (NO_VSCROLL_ADJUST + 10));
		if (MINIMUM_CONTENT_HEIGHT > cHeight) {
			// Too small!  Use the minimum even though this will turn
			// on the vertical scroll bar.
			cHeight = MINIMUM_CONTENT_HEIGHT;
		}
		
		// Set the height of the calendar.
		m_calendar.setHeight(cHeight + "px");
	}

	/*
	 * Given a CalendarDayView enumeration value, sets the calendar
	 * widget to that view.
	 */
	private void setCalendarDisplay() {
		// Calculate the hours to display...
		int hourStart;
		int duration;
		switch (m_calendarDisplayData.getHours()) {
		default:
		case WORK_DAY:  hourStart = m_calendarDisplayData.getWorkDayStart(); duration = 12; break;
		case FULL_DAY:  hourStart = 0;                                       duration = 24; break;
		}
		
		// ...and put them into affect.
		CalendarSettings cs = m_calendar.getSettings();
		cs.setWorkingHourStart(hourStart);
		cs.setWorkingHourEnd(  hourStart + duration);
		m_calendar.setSettings(cs);

		// Put the first day of the week into affect.
		CalendarFormat cf = CalendarFormat.INSTANCE;
		cf.setFirstDayOfWeek(m_calendarDisplayData.getWeekFirstDay());
		
		// Calculate the view and days...
		int days = (-1);
		CalendarViews cView;
		switch (m_calendarDisplayData.getDayView()) {
		default:
		case MONTH:       cView = CalendarViews.MONTH;          break;
		case ONE_DAY:     cView = CalendarViews.DAY; days =  1; break;
		case THREE_DAYS:  cView = CalendarViews.DAY; days =  3; break;
		case FIVE_DAYS:   cView = CalendarViews.DAY; days =  5; break;
		case WEEK:        cView = CalendarViews.DAY; days =  7; break;
		case TWO_WEEKS:   cView = CalendarViews.DAY; days = 14; break;
		}

		// ...and put them into affect.
		if ((-1) == days)
		     m_calendar.setView(cView      );
		else m_calendar.setView(cView, days);
		m_calendar.setDate(m_calendarDisplayData.getFirstDay());
	}
	
	/*
	 * Synchronously shows the add files dialog.
	 */
	private void showAddFilesDlgNow() {
		AddFilesDlg.initAndShow(
			m_addFilesDlg,
			getFolderInfo(),
			getEntryMenuPanel().getAddFilesMenuItem());
	}
	
	/*
	 * Asynchronously shows the calendar settings dialog.
	 */
	private void showCalendarSettingsDlgAsync() {
		ScheduledCommand doShow = new ScheduledCommand() {
			@Override
			public void execute() {
				showCalendarSettingsDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doShow);
	}
	
	/*
	 * Synchronously shows the calendar settings dialog.
	 */
	private void showCalendarSettingsDlgNow() {
		CalendarSettingsDlg.initAndShow(m_calendarSettingsDlg, getFolderId(), m_calendarDisplayData);
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * Overrides the FolderViewBase.viewComplete() method.
	 */
	@Override
	public void viewComplete() {
		// Tell the calendar to resize itself now that it can determine
		// how big everything is.
		resizeView();
	}
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the calendar folder view and perform some operation on it.    */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Loads the CalendarFolderView split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(CalendarFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				CalendarFolderView dfView = new CalendarFolderView(folderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_CalendarFolderView());
				vClient.onUnavailable();
			}
		});
	}
}
