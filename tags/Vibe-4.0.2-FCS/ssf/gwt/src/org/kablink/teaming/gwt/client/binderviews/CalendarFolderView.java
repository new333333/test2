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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.UIObject;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteEntitiesHelper.DeleteEntitiesCallback;
import org.kablink.teaming.gwt.client.event.CalendarChangedEvent;
import org.kablink.teaming.gwt.client.event.CalendarGotoDateEvent;
import org.kablink.teaming.gwt.client.event.CalendarHoursEvent;
import org.kablink.teaming.gwt.client.event.CalendarNextPeriodEvent;
import org.kablink.teaming.gwt.client.event.CalendarPreviousPeriodEvent;
import org.kablink.teaming.gwt.client.event.CalendarSettingsEvent;
import org.kablink.teaming.gwt.client.event.CalendarShowEvent;
import org.kablink.teaming.gwt.client.event.CalendarShowHintEvent;
import org.kablink.teaming.gwt.client.event.CalendarViewDaysEvent;
import org.kablink.teaming.gwt.client.event.ChangeEntryTypeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DownloadFolderAsCSVFileEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.InvokeCopyFiltersDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeDropBoxEvent;
import org.kablink.teaming.gwt.client.event.LockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsReadEvent;
import org.kablink.teaming.gwt.client.event.MarkFolderContentsUnreadEvent;
import org.kablink.teaming.gwt.client.event.MarkReadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MarkUnreadSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.UnlockSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.ViewSelectedEntryEvent;
import org.kablink.teaming.gwt.client.event.ViewWhoHasAccessEvent;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarAppointmentsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarAppointmentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarDisplayDateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarNextPreviousPeriodCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveCalendarDayViewCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveCalendarHoursCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveCalendarShowCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UpdateCalendarEventCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CalendarAppointment;
import org.kablink.teaming.gwt.client.util.CalendarDayView;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.CalendarSettingsDlg;
import org.kablink.teaming.gwt.client.widgets.AlertDlg.AlertDlgClient;
import org.kablink.teaming.gwt.client.widgets.CalendarSettingsDlg.CalendarSettingsDlgClient;
import org.kablink.teaming.gwt.client.widgets.AlertDlg;
import org.kablink.teaming.gwt.client.widgets.HoverHintPopup;
import org.kablink.teaming.gwt.client.widgets.VibeCalendar;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.CalendarFormat;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings;
import com.bradrydzewski.gwt.calendar.client.CalendarViews;
import com.bradrydzewski.gwt.calendar.client.event.DateRequestEvent;
import com.bradrydzewski.gwt.calendar.client.event.DateRequestHandler;
import com.bradrydzewski.gwt.calendar.client.event.DeleteEvent;
import com.bradrydzewski.gwt.calendar.client.event.DeleteHandler;
import com.bradrydzewski.gwt.calendar.client.event.MouseOverEvent;
import com.bradrydzewski.gwt.calendar.client.event.MouseOverHandler;
import com.bradrydzewski.gwt.calendar.client.event.TimeBlockClickEvent;
import com.bradrydzewski.gwt.calendar.client.event.TimeBlockClickHandler;
import com.bradrydzewski.gwt.calendar.client.event.UpdateEvent;
import com.bradrydzewski.gwt.calendar.client.event.UpdateHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Calendar folder view.
 * 
 * @author drfoster@novell.com
 */
public class CalendarFolderView extends FolderViewBase
	implements CalendarDisplayDataProvider,
		// Event handlers implemented by this class.
		CalendarGotoDateEvent.Handler,
		CalendarHoursEvent.Handler,
		CalendarNextPeriodEvent.Handler,
		CalendarPreviousPeriodEvent.Handler,
		CalendarSettingsEvent.Handler,
		CalendarShowEvent.Handler,
		CalendarShowHintEvent.Handler,
		CalendarViewDaysEvent.Handler,
		ChangeEntryTypeSelectedEntitiesEvent.Handler,
		CopySelectedEntitiesEvent.Handler,
		ContributorIdsRequestEvent.Handler,
		DeleteSelectedEntitiesEvent.Handler,
		DownloadFolderAsCSVFileEvent.Handler,
		InvokeCopyFiltersDlgEvent.Handler,
		InvokeDropBoxEvent.Handler,
		LockSelectedEntitiesEvent.Handler,
		MarkFolderContentsReadEvent.Handler,
		MarkFolderContentsUnreadEvent.Handler,
		MarkReadSelectedEntitiesEvent.Handler,
		MarkUnreadSelectedEntitiesEvent.Handler,
		MoveSelectedEntitiesEvent.Handler,
		QuickFilterEvent.Handler,
		ShareSelectedEntitiesEvent.Handler,
		SubscribeSelectedEntitiesEvent.Handler,
		UnlockSelectedEntitiesEvent.Handler,
		ViewSelectedEntryEvent.Handler,
		ViewWhoHasAccessEvent.Handler
{
	private AlertDlg							m_calendarShowHintDlg;		//
	private ArrayList<Appointment>				m_appointments;				//
	private CalendarAppointment					m_selectedEvent;			//
	private CalendarDisplayDataRpcResponseData	m_calendarDisplayData;		//
	private CalendarSettingsDlg					m_calendarSettingsDlg;		//
	private HoverHintPopup						m_hoverHintPopup;			//
	private List<HandlerRegistration>			m_registeredEventHandlers;	// Event handlers that are currently registered.
	private long								m_browserTZOffset;			// The timezone offset from the browser.
	private String								m_quickFilter;				// Any quick filter that's active.
	private VibeCalendar						m_calendar;					// The calendar widget contained in the view.

	private static int MINIMUM_CALENDAR_HEIGHT	= 250;
	
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
		TeamingEvents.CALENDAR_SHOW_HINT,
		TeamingEvents.CALENDAR_VIEW_DAYS,
		TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTITIES,
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		TeamingEvents.COPY_SELECTED_ENTITIES,
		TeamingEvents.DELETE_SELECTED_ENTITIES,
		TeamingEvents.DOWNLOAD_FOLDER_AS_CSV_FILE,
		TeamingEvents.INVOKE_COPY_FILTERS_DLG,
		TeamingEvents.INVOKE_DROPBOX,
		TeamingEvents.LOCK_SELECTED_ENTITIES,
		TeamingEvents.MARK_FOLDER_CONTENTS_READ,
		TeamingEvents.MARK_FOLDER_CONTENTS_UNREAD,
		TeamingEvents.MARK_READ_SELECTED_ENTITIES,
		TeamingEvents.MARK_UNREAD_SELECTED_ENTITIES,
		TeamingEvents.MOVE_SELECTED_ENTITIES,
		TeamingEvents.QUICK_FILTER,
		TeamingEvents.SHARE_SELECTED_ENTITIES,
		TeamingEvents.SUBSCRIBE_SELECTED_ENTITIES,
		TeamingEvents.UNLOCK_SELECTED_ENTITIES,
		TeamingEvents.VIEW_SELECTED_ENTRY,
		TeamingEvents.VIEW_WHO_HAS_ACCESS,
	};
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public CalendarFolderView(BinderInfo folderInfo, UIObject parent, ViewReady viewReady) {
		// Initialize the super class...
		super(folderInfo, parent, viewReady, "vibe-calendarFolder", false);

		// ...initialize anything else that requires it...
		m_browserTZOffset = (GwtClientHelper.getTimeZoneOffsetMillis(new Date()) * (-1l));
		
		// ...and tell it that we can provide the calendar display data
		// ...if it needs it.
		setCalendarDisplayDataProvider(this);
	}
	
	/*
	 * Attaches the event handlers to the calendar widget.
	 */
	private void attachEventHandlers() {
		// Add a date request handler...
		m_calendar.addDateRequestHandler(new DateRequestHandler<Date>() {
			@Override
			public void onDateRequested(DateRequestEvent<Date> event) {
				// ...that switches to day view on the given date.
				GwtTeaming.fireEvent(
					new CalendarViewDaysEvent(
						getFolderInfo().getBinderIdAsLong(),
						CalendarDayView.ONE_DAY,
						event.getTarget()));
			}
		});
		
		// Add a delete handler.
		m_calendar.addDeleteHandler(new DeleteHandler<Appointment>() {
			@Override
			public void onDelete(DeleteEvent<Appointment> event) {
				// Does the user have rights to delete this event?
				CalendarAppointment ca = ((CalendarAppointment) event.getTarget());
				if (!(ca.canTrash())) {
					// No!  Tell them about the problem and cancel the
					// event.
					GwtClientHelper.deferredAlert(m_messages.calendarView_Error_CantTrash());
					event.setCancelled(true);
					FullUIReloadEvent.fireOneAsync();
					return;					
				}
				
				// Delete the selected appointment.
				doDeleteEntityAsync((CalendarAppointment) event.getTarget());
				event.setCancelled(true);
			}
		});

		// Add a mouse over handler.
		m_calendar.addMouseOverHandler(new MouseOverHandler<Appointment>() {
			@Override
			public void onMouseOver(MouseOverEvent<Appointment> event) {
				// Does the current day view support hover hints?
				if (viewSupportsHoverHints()) {
					// Yes!  Does the appointment have a description?
					CalendarAppointment ca = ((CalendarAppointment) event.getTarget());
					String caDesc = ca.getDescriptionHtml();
					if (!(GwtClientHelper.hasString(caDesc))) {
						caDesc = ca.getDescription();
					}
					if (GwtClientHelper.hasString(caDesc)) {
						// Yes!  If we haven't created a hover hint
						// popup to show descriptions in yet...
						if (null == m_hoverHintPopup) {
							// ...create one now...
							m_hoverHintPopup = new HoverHintPopup();
						}
						
						// ...and show it with the description HTML.
						m_hoverHintPopup.setHoverText(caDesc);
						m_hoverHintPopup.showHintRelativeTo((Element) event.getElement());
					}
					
					else if (null != m_hoverHintPopup) {
						// Hovering over any other appointment will
						// hide an existing hover hint.
						m_hoverHintPopup.hide();
					}
				}
			}
		});
		
		// Add an open handler...
		m_calendar.addOpenHandler(new OpenHandler<Appointment>() {
			@Override
			public void onOpen(OpenEvent<Appointment> event) {
				// ...that runs the entry viewer on the appointment.
				doViewEntryAsync((CalendarAppointment) event.getTarget());
			}
		});

		// Add a selection handler...
		m_calendar.addSelectionHandler(new SelectionHandler<Appointment> () {
			@Override
			public void onSelection(SelectionEvent<Appointment> event) {
				// ...that tracks the selected item.
				m_selectedEvent = ((CalendarAppointment) event.getSelectedItem());
				
				// If we have an entry menu...
				EntryMenuPanel emp = getEntryMenuPanel();
				if (null != emp) {
					// ...tell it to update the state of its items that
					// ...require entries be available.
					boolean haveSelection = (null != m_selectedEvent);
					EntryMenuPanel.setEntrySelected(  emp, haveSelection);
					EntryMenuPanel.setEntriesSelected(emp, haveSelection);
				}
			}
		});

		// Add a time block click handler.
		m_calendar.addTimeBlockClickHandler(new TimeBlockClickHandler<Date>() {
			@Override
			public void onTimeBlockClick(TimeBlockClickEvent<Date> event) {
				// If there's an appointment selected...
				if (m_calendar.hasAppointmentSelected()) {
					// ...simply clear the selection and bail.
					clearSelection();
					return;
				}
				
				// Are we showing events by date?
				if (m_calendarDisplayData.getShow().isPhysicalByDate()) {
					// Yes!  Then we don't support clicking on a date
					// to create a new appointment.  Tell the user
					// about the problem and bail.
					GwtClientHelper.deferredAlert(m_messages.calendarView_Error_CantClickCreateWhenViewByDate());
					return;					
				}

				// Does the user have rights to add entries to this
				// folder?
				if (!(m_calendarDisplayData.canAddFolderEntry())) {
					// No!  Tell them about the problem and bail.
					GwtClientHelper.deferredAlert(m_messages.calendarView_Error_CantAdd());
					return;					
				}

				// Launch the URL to add a new appointment.
				doAddAppointmentAsync(event.getTarget());
			}
		});
		
		// Add an update handler.
		m_calendar.addUpdateHandler(new UpdateHandler<Appointment>() {
			@Override
			public void onUpdate(UpdateEvent<Appointment> event) {
				// Are we showing events by date?
				if (m_calendarDisplayData.getShow().isPhysicalByDate()) {
					// Yes!  Then we don't support drag an drop.  Tell
					// the user about the problem and cancel the event.
					GwtClientHelper.deferredAlert(m_messages.calendarView_Error_CantUpdateWhenViewByDate());
					event.setCancelled(true);
					FullUIReloadEvent.fireOneAsync();
					return;
				}

				// Does the user have rights to modify this event?
				final CalendarAppointment ca = ((CalendarAppointment) event.getTarget());
				if (!(ca.canModify())) {
					// No!  Tell them about the problem and cancel the
					// event.
					GwtClientHelper.deferredAlert(m_messages.calendarView_Error_CantModify());
					event.setCancelled(true);
					FullUIReloadEvent.fireOneAsync();
					return;
				}

				// Is this an instance of a recurrent event?
				if (ca.isClientRecurrentInstance()) {
					// Yes  Regardless of what else we do, cancel the
					// event.  They either can't drag and drop it or
					// we'll do a full refresh when they do.
					event.setCancelled(true);


					// Is this the second or later in a sequence of
					// recurrent events?
					if (0 < ca.getClientRecurrenceIndex()) {
						// Yes!  Then we don't support drag an drop on
						// it.  Tell the user about the problem and
						// bail.
						GwtClientHelper.deferredAlert(m_messages.calendarView_Error_CantUpdateRecurrence());
						FullUIReloadEvent.fireOneAsync();
						return;
					}
				}

				// Can we update the given event?
				GwtClientHelper.executeCommand(
						new UpdateCalendarEventCmd(m_browserTZOffset, getFolderInfo().getBinderIdAsLong(), ca),
						new AsyncCallback<VibeRpcResponse>() {
							@Override
							public void onFailure(Throwable t) {
								// No!  Tell the user about the problem...
								GwtClientHelper.handleGwtRPCFailure(
										t,
										m_messages.rpcFailure_UpdateCalendarEvent(),
										(ca.isTask() ?
												m_messages.rpcFailure_UpdateCalendarEventTask() :
												m_messages.rpcFailure_UpdateCalendarEventAppointment()));

								// ...and repopulate the view to get back what
								// ...was displayed.
								doFullCalendarRefreshAsync();
							}

							@Override
							public void onSuccess(VibeRpcResponse response) {
								// Yes, the event has been updated!  Is it an
								// instance of a recurrent event that we just
								// updated?
								if (ca.isClientRecurrentInstance()) {
									// Yes!  Repopulate the view so that all
									// recurrences get updated too.
									doFullCalendarRefreshAsync();
								}

								// Otherwise, there's nothing more to do as the
								// drag and drop will have positioned the event
								// correctly.
							}
						});
			}
		});
	}

	/*
	 * Clears any currently selected appointment.
	 */
	private void clearSelection() {
		// If the calendar has an appointment selected...
		if (m_calendar.hasAppointmentSelected()) {
			// ...clear the selection.
			m_calendar.suspendLayout();
			m_calendar.resetSelectedAppointment();
			m_calendar.fireSelectionEvent(null);
			m_calendar.resumeLayout();
		}
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
	 * Asynchronously launches a URL to add a new appointment to the
	 * folder.
	 */
	private void doAddAppointmentAsync(final Date date) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doAddAppointmentNow(date);
			}
		});
	}

	/*
	 * Synchronously launches a URL to add a new appointment to the
	 * folder.
	 */
	@SuppressWarnings("deprecation")
	private void doAddAppointmentNow(Date date) {
		StringBuffer addEntryUrl = new StringBuffer(m_calendarDisplayData.getAddEntryUrl());
		addEntryUrl.append("&year=");       addEntryUrl.append(String.valueOf(date.getYear() + 1900));
		addEntryUrl.append("&month=");      addEntryUrl.append(String.valueOf(date.getMonth()      ));
		addEntryUrl.append("&dayOfMonth="); addEntryUrl.append(String.valueOf(date.getDate()       ));
		if (CalendarDayView.MONTH == m_calendarDisplayData.getDayView()) {
			date = new Date();
		}
		addEntryUrl.append("&time="); addEntryUrl.append(String.valueOf(date.getHours()  ));
		addEntryUrl.append(":");      addEntryUrl.append(String.valueOf(date.getMinutes()));
		GwtTeaming.fireEvent(new GotoContentUrlEvent(addEntryUrl.toString()));
	}
	
	/*
	 * Asynchronously loads the display data for the next/previous
	 * period.
	 */
	private void doCalendarNextPreviousPeriodAsync(final boolean next) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doCalendarNextPreviousPeriodNow(next);
			}
		});
	}
	
	/*
	 * Synchronously loads the display data for the next/previous
	 * period.
	 */
	private void doCalendarNextPreviousPeriodNow(boolean next) {
		GwtClientHelper.executeCommand(
				new GetCalendarNextPreviousPeriodCmd(m_browserTZOffset, getFolderInfo().getBinderIdAsLong(), m_calendarDisplayData, next),
				new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
								t,
								m_messages.rpcFailure_GetCalendarNextPreviousPeriod());
					}

					@Override
					public void onSuccess(VibeRpcResponse response) {
						// Yes!  Put the new calendar display data into
						// affect...
						m_calendarDisplayData = ((CalendarDisplayDataRpcResponseData) response.getResponseData());
						GwtTeaming.fireEvent(new CalendarChangedEvent(getFolderId(), m_calendarDisplayData));

						// ...and repopulate the view.
						doFullCalendarRefreshAsync();
					}
				});
	}
	
	/*
	 * Asynchronously deletes the given appointment.
	 */
	private void doDeleteEntityAsync(final CalendarAppointment appointment) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doDeleteEntityNow(appointment);
			}
		});
	}
	
	/*
	 * Synchronously deletes the given appointment.
	 */
	private void doDeleteEntityNow(final CalendarAppointment appointment) {
		// Is the user sure they want to delete the appointment?
		final List<EntityId> entityIds = appointment.getEntityIdAsList();
		BinderViewsHelper.deleteSelections(entityIds, new DeleteEntitiesCallback() {
			@Override
			public void operationCanceled() {
				// No, they're not sure!
			}

			@Override
			public void operationComplete() {
				// Yes, the user was sure and we've deleted the
				// appointment!  If the appoint was a recurrent
				// instance...
				if (appointment.isClientRecurrentInstance()) {
					// ...force the calendar to
					// ...refresh.
					doFullCalendarRefreshAsync();
				} else {
					// ...otherwise, remove the
					// ...appointment from the
					// ...calendar.
					m_calendar.suspendLayout();
					m_calendar.removeAppointment(appointment);
					m_calendar.resumeLayout();
					if (CalendarDayView.ONE_DAY.equals(m_calendarDisplayData.getDayView())) {
						m_calendar.scrollToHour(m_calendarDisplayData.getWorkDayStart());
					}
				}
			}

			@Override
			public void operationFailed() {
				// Nothing to do.  The delete call will have
				// told the user about the failure.
			}
		});
	}
	
	/*
	 * Asynchronously refreshes the full contents of the calendar.
	 */
	private void doFullCalendarRefreshAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doFullCalendarRefreshNow();
			}
		});
	}
	
	/*
	 * Synchronously refreshes the full contents of the calendar.
	 */
	private void doFullCalendarRefreshNow() {
		setCalendarDisplay();
		m_calendar.clearAppointments();
		populateCalendarEventsAsync();
	}
	
	/*
	 * Asynchronously runs the entry viewer on the given appointment.
	 */
	private void doViewEntryAsync(final CalendarAppointment appointment) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doViewEntryNow(appointment);
			}
		});
	}
	
	/*
	 * Synchronously runs the entry viewer on the given appointment.
	 */
	private void doViewEntryNow(CalendarAppointment appointment) {
		final Long folderId = appointment.getFolderId();
		final Long entryId  = appointment.getEntryId();
		
		GetViewFolderEntryUrlCmd cmd = new GetViewFolderEntryUrlCmd(folderId, entryId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
						String.valueOf(entryId));
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				String viewFolderEntryUrl = ((StringRpcResponseData) response.getResponseData()).getStringValue();
				GwtClientHelper.jsShowForumEntry(viewFolderEntryUrl);
			}
		});
	}

	/*
	 * Walks an appointment list and expands recurrent appointments
	 * into individual appointment object for each recurrence.
	 */
	private static ArrayList<Appointment> expandRecurrentAppointments(List<Appointment> appointments) {
		// Allocate a list of appointments that we'll return.
		ArrayList<Appointment> reply = new ArrayList<Appointment>();

		// We're we given any appointments to expand?
		if ((null != appointments) && (!(appointments.isEmpty()))) {
			// Yes!  Scan them.
			for (Appointment appointment:  appointments) {
				// Is this appointment recurrent?
				CalendarAppointment ca = ((CalendarAppointment) appointment);
				if (ca.isServerRecurrent()) {
					// Yes!  Expand each recurrence into its own
					// appointment object.  Note that we append
					// an indication of the recurrence instance in
					// event's title.
					List<Date[]> recurrenceDates = ca.getServerRecurrence().getRecurrenceDates();
					int count = recurrenceDates.size();
					int index = 0;
					for (Date[] dates:  recurrenceDates) {
						CalendarAppointment caClone = ca.cloneAppointment();
						caClone.setServerRecurrence(     null                                                                       );
						caClone.setClientRecurrenceIndex(index++                                                                    );
						caClone.setStart(                dates[0]                                                                   );
						caClone.setEnd(                  dates[1]                                                                   );
						caClone.setTitle(                caClone.getTitle() + " " + m_messages.calendarView_Recurrence(index, count));
						reply.add(                       caClone                                                                    );
					}
				}
				
				else {
					// No, this appointment isn't recurrent!  Simply
					// add it to the reply list.
					ca.setClientRecurrenceIndex(-1);
					reply.add(ca);
				}
			}
		}

		// If we get here, reply refers to the expanded list of
		// appointments.  Return it.
		return reply;
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
					new GetCalendarDisplayDataCmd(m_browserTZOffset, getFolderInfo()),
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

	/*
	 * Returns a List<EntityId> that contains the selected entity.
	 */
	private List<EntityId> getSelectedEntityIds() {
		List<EntityId> reply;
		if (null != m_selectedEvent)
		     reply = m_selectedEvent.getEntityIdAsList();
		else reply = new ArrayList<EntityId>();
		return reply;
	}
	
	/**
	 * Returns the minimum height to used for a folder view's content.
	 * 
	 * Overrides the FolderViewBase.getMinimumContentHeight() method.
	 * 
	 * @return
	 */
	@Override
	public int getMinimumContentHeight() {
		return MINIMUM_CALENDAR_HEIGHT;
	}

	/**
	 * Returns the adjustment to used for a folder view's content so
	 * that it doesn't get a vertical scroll bar.
	 * 
	 * Overrides the FolderViewBase.getNoVScrollAdjustment() method.
	 * 
	 * @return
	 */
	@Override
	public int getNoVScrollAdjustment() {
		return (super.getNoVScrollAdjustment() + 10);
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
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
					new GetCalendarDisplayDataCmd(m_browserTZOffset, getFolderInfo()),
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the Calendar widget.
	 */
	private void loadPart2Now() {
		// Create the Calendar widget for the view and populate it.
		m_calendar = new VibeCalendar();
		attachEventHandlers();
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
			m_calendarDisplayData.setFirstDay(event.getDate());
			m_calendarDisplayData.setStartDay(event.getDate());
			GwtClientHelper.executeCommand(
					new GetCalendarDisplayDateCmd(m_browserTZOffset, getFolderInfo().getBinderIdAsLong(), m_calendarDisplayData),
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
					doFullCalendarRefreshAsync();
				}
			});
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
					new SaveCalendarHoursCmd(m_browserTZOffset, getFolderInfo(), event.getHours()),
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
					doFullCalendarRefreshAsync();
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
					new SaveCalendarShowCmd(m_browserTZOffset, getFolderInfo(), event.getShow()),
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
	 * Handles CalendarShowHintEvent's received by this class.
	 * 
	 * Implements the CalendarShowHintEvent.Handler.onCalendarShowHint() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarShowHint(CalendarShowHintEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(getFolderId())) {
			// Yes!  Show the appropriate hint.
			String hint;
			switch (event.getShow()) {
			default:
			case PHYSICAL_EVENTS:       hint = m_messages.calendarView_Hint_PhysicalEvents();     break;
			case PHYSICAL_BY_CREATION:  hint = m_messages.calendarView_Hint_PhysicalByCreation(); break;
			case PHYSICAL_BY_ACTIVITY:  hint = m_messages.calendarView_Hint_PhysicalByActivity(); break;
			case VIRTUAL:               hint = m_messages.calendarView_Hint_Virtual();            break;
			}
			showCalendarShowHintAsync(hint);
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
					new SaveCalendarDayViewCmd(m_browserTZOffset, getFolderInfo(), event.getDayView(), event.getDate()),
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
							doFullCalendarRefreshAsync();
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
			// Yes!  Do we have any events being displayed in the
			// calendar?
			List<Appointment> appointments = m_calendar.getAppointments();
			if ((null != appointments) && (!(appointments.isEmpty()))) {
				// Yes!  Collect the contributor IDs from the
				// appointments...
				final List<Long> contributorIds = new ArrayList<Long>();
				for (Appointment appointment:  appointments) {
					Long contributorId = ((CalendarAppointment) appointment).getCreatorId();
					if (!(contributorIds.contains(contributorId))) {
						contributorIds.add(contributorId);
					}
				}
				
				// ...and asynchronously fire the corresponding reply
				// ...event with the contributor IDs.
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						GwtTeaming.fireEvent(
							new ContributorIdsReplyEvent(
								getFolderId(),
								contributorIds));
					}
				});
			}
		}
	}
	
	/**
	 * Handles ChangeEntryTypeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ChangeEntryTypeSelectedEntitiesEvent.Handler.onChangeEntryTypeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeEntryTypeSelectedEntities(ChangeEntryTypeSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the change.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.changeEntryTypes(selectedEntityIds);
		}
	}
	
	/**
	 * Handles CopySelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the CopySelectedEntitiesEvent.Handler.onCopySelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopySelectedEntities(CopySelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Does the event contain any entities?
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				// No!  Invoke the copy on those selected in the view.
				selectedEntityIds = getSelectedEntityIds();
				BinderViewsHelper.copyEntries(selectedEntityIds);
			}
		}
	}
	
	/**
	 * Handles DeleteSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedEntitiesEvent.Handler.onDeleteSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedEntities(DeleteSelectedEntitiesEvent event) {
		// If we don't have an event selected...
		if (null == m_selectedEvent) {
			// ...bail.
			return;
		}
		
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Delete the selected event.
			// Does the user have rights to delete this event?
			if (!(m_selectedEvent.canTrash())) {
				// No!  Tell them about the problem and cancel the
				// event.
				GwtClientHelper.deferredAlert(m_messages.calendarView_Error_CantTrash());
				return;					
			}
			
			// Delete the selected appointment.
			doDeleteEntityAsync(m_selectedEvent);
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
	 * Handles DownloadFolderAsCSVFileEvent's received by this class.
	 * 
	 * Implements the DownloadFolderAsCSVFileEvent.Handler.onDownloadFolderAsCSVFile() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDownloadFolderAsCSVFile(DownloadFolderAsCSVFileEvent event) {
		// Is the event targeted to this folder?
		Long dlFolderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = dlFolderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the download.
			BinderViewsHelper.downloadFolderAsCSVFile(
				getDownloadPanel().getForm(),
				dlFolderId);
		}
	}
	
	/**
	 * Handles InvokeCopyFiltersDlgEvent's received by this class.
	 * 
	 * Implements the InvokeCopyFiltersDlgEvent.Handler.onInvokeCopyFiltersDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeCopyFiltersDlg(InvokeCopyFiltersDlgEvent event) {
		// Is the event targeted to this folder?
		BinderInfo eventFolderInfo = event.getFolderInfo();
		if (eventFolderInfo.isEqual(getFolderInfo())) {
			// Yes!  Invoke the copy filters dialog on the folder.
			onInvokeCopyFiltersDlgAsync(eventFolderInfo);
		}
	}

	/*
	 * Asynchronously invokes the copy filters dialog.
	 */
	private void onInvokeCopyFiltersDlgAsync(final BinderInfo folderInfo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onInvokeCopyFiltersDlgNow(folderInfo);
			}
		} );
	}
	
	/*
	 * Synchronously invokes the copy filters dialog.
	 */
	private void onInvokeCopyFiltersDlgNow(final BinderInfo folderInfo) {
		BinderViewsHelper.invokeCopyFiltersDlg(folderInfo);
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
			// Yes!  Invoke the files drop box on the folder.
			BinderViewsHelper.invokeDropBox(
				getFolderInfo(),
				getEntryMenuPanel().getAddFilesMenuItem());
		}
	}
	
	/**
	 * Handles LockSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the LockSelectedEntitiesEvent.Handler.onLockSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onLockSelectedEntities(LockSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the lock.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.lockEntries(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MarkFolderContentsReadEvent's received by this class.
	 * 
	 * Implements the MarkFolderContentsReadEvent.Handler.onMarkFolderContentsRead() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkFolderContentsRead(MarkFolderContentsReadEvent event) {
		// Is the event targeted to this folder?
		Long folderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = folderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Mark the folder contents as having been read.
			BinderViewsHelper.markFolderContentsRead(folderId);
		}
	}
	
	/**
	 * Handles MarkFolderContentsUnreadEvent's received by this class.
	 * 
	 * Implements the MarkFolderContentsUnreadEvent.Handler.onMarkFolderContentsUnread() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkFolderContentsUnread(MarkFolderContentsUnreadEvent event) {
		// Is the event targeted to this folder?
		Long folderId    = event.getFolderId();
		Long eventFolderId = event.getHandleByFolderId();
		if (null == eventFolderId) {
			eventFolderId = folderId;
		}
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Mark the folder contents as having been unread.
			BinderViewsHelper.markFolderContentsUnread(folderId);
		}
	}
	
	/**
	 * Handles MarkReadSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MarkReadSelectedEntitiesEvent.Handler.onMarkReadSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkReadSelectedEntities(MarkReadSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the mark entries read.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.markEntriesRead(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MarkUnreadSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MarkUnreadSelectedEntitiesEvent.Handler.onMarkUnreadSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMarkUnreadSelectedEntities(MarkUnreadSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the mark entries read.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.markEntriesUnread(selectedEntityIds);
		}
	}
	
	/**
	 * Handles MoveSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MoveSelectedEntitiesEvent.Handler.onMoveSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMoveSelectedEntities(MoveSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Are there any entities in the event?
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				// No!  Invoke the move on those selected in the view.
				selectedEntityIds = getSelectedEntityIds();
				BinderViewsHelper.moveEntries(selectedEntityIds);
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
			doFullCalendarRefreshAsync();
		}
	}

	/**
	 * Handles ShareSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the ShareSelectedEntitiesEvent.Handler.onShareSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShareSelectedEntities(ShareSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the share.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.shareEntities(selectedEntityIds);
		}
	}
	
	/**
	 * Handles SubscribeSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the SubscribeSelectedEntitiesEvent.Handler.onSubscribeSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSubscribeSelectedEntities(SubscribeSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the subscribe to.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.subscribeToEntries(selectedEntityIds);
		}
	}
	
	/**
	 * Handles UnlockSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the UnlockSelectedEntitiesEvent.Handler.onUnlockSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onUnlockSelectedEntities(UnlockSelectedEntitiesEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the unlock.
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = getSelectedEntityIds();
			}
			BinderViewsHelper.unlockEntries(selectedEntityIds);
		}
	}
	
	/**
	 * Handles ViewSelectedEntryEvent's received by this class.
	 * 
	 * Implements the ViewSelectedEntryEvent.Handler.onViewSelectedEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewSelectedEntry(ViewSelectedEntryEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the view.
			List<EntityId> eids = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(eids))) {
				eids = getSelectedEntityIds();
			}
			if (GwtClientHelper.hasItems(eids)) {
				for (EntityId eid:  eids) {
					if (eid.isEntry()) {
						BinderViewsHelper.viewEntry(eid);
						return;
					}
				}
			}
		}
	}
	
	/**
	 * Handles ViewWhoHasAccessEvent's received by this class.
	 * 
	 * Implements the ViewWhoHasAccessEvent.Handler.onViewWhoHasAccess() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewWhoHasAccess(ViewWhoHasAccessEvent event) {
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(getFolderId())) {
			// Yes!  Invoke the view.
			List<EntityId> eids = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(eids))) {
				eids = getSelectedEntityIds();
			}
			if (GwtClientHelper.hasItems(eids)) {
				for (EntityId eid:  eids) {
					BinderViewsHelper.viewWhoHasAccess(eid);
					return;
				}
			}
		}
	}
	
	/*
	 * Asynchronously populates the the calendar's events.
	 */
	private void populateCalendarEventsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateCalendarEventsNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the the calendar's events.
	 */
	private void populateCalendarEventsNow() {
		// Can we read the appointments for the calendar based on the
		// current calendar display data?
		GwtClientHelper.executeCommand(
				new GetCalendarAppointmentsCmd(m_browserTZOffset, getFolderInfo().getBinderIdAsLong(), m_calendarDisplayData, m_quickFilter),
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
						m_appointments = expandRecurrentAppointments(responseData.getAppointments());
						m_calendar.suspendLayout();
						m_calendar.addAppointments(m_appointments);
						m_calendar.resumeLayout();
						if (CalendarDayView.ONE_DAY.equals(m_calendarDisplayData.getDayView())) {
							m_calendar.scrollToHour(m_calendarDisplayData.getWorkDayStart());
						}
					}
				});
	}
	
	/*
	 * Asynchronously populates the the calendar view.
	 */
	private void populateViewAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateViewNow();
			}
		});
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
		int cHeight   = (((viewHeight - cTop) - totalBelow) - getNoVScrollAdjustment());
		int minHeight = getMinimumContentHeight();
		if (minHeight > cHeight) {
			// Too small!  Use the minimum even though this will turn
			// on the vertical scroll bar.
			cHeight = minHeight;
		}
		
		// Set the height of the calendar.
		m_calendar.setHeight(cHeight + "px");
	}

	@Override
	public int getMinimumViewHeight() {
		int viewTop			= getAbsoluteTop();								// Absolute top of the view.
		int dtTop			= (m_calendar.getAbsoluteTop() - viewTop);				// Top of the data table relative to the top of the view.

		return getMinimumContentHeight() + dtTop + getNoVScrollAdjustment();
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
		cf.setFirstDayOfWeek(m_calendarDisplayData.getWeekFirstDay() - 1);
		
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
	 * Asynchronously sets the size of the calendar based on its
	 * position in the view.
	 */
	private void resizeViewAsync(int delay) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				resizeView();
			}
		},
		delay);
	}

	/*
	 * Asynchronously sets the size of the calendar based on its
	 * position in the view.
	 */
	private void resizeViewAsync() {
		resizeViewAsync(0);	// DRF (20130726):  Was INITIAL_RESIZE_DELAY (like other views.)  Changed to 0 to fix initial display glitches.
	}
	
	/*
	 * Asynchronously shows the calendar settings dialog.
	 */
	private void showCalendarSettingsDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showCalendarSettingsDlgNow();
			}
		});
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

	/*
	 * Asynchronously shows the calendar show hint when requested.
	 */
	private void showCalendarShowHintAsync(final String hint) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				showCalendarShowHintNow(hint);
			}
		});
	}
	
	/*
	 * Synchronously shows the calendar show hint when requested.
	 */
	private void showCalendarShowHintNow(final String hint) {
		// Have we created the calendar show hint dialog yet?
		if (null == m_calendarShowHintDlg) {
			// No!  Create it now...
			AlertDlg.createAsync(new AlertDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(AlertDlg aDlg) {
					// ...and show it.
					m_calendarShowHintDlg = aDlg;
					m_calendarShowHintDlg.addStyleName("vibe-calendarFolderShowHintDlg");
					showCalendarShowHintImpl(hint);
				}
			},
			true,	// true  -> Auto hide the dialog. 
			false);	// false -> The dialog is not modal.
		}
		
		else
		{
			// Yes, we've already created the calendar show hint
			// dialog!  Simply show it.
			showCalendarShowHintImpl(hint);
		}
	}

	/*
	 * Implementation method that shows the calendar show hint dialog.
	 */
	private void showCalendarShowHintImpl(final String hint) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				AlertDlg.initAndShow(
					m_calendarShowHintDlg,
					hint);
			}
		});
	}
	
	/*
	 * Returns true if the current view mode supports hover hints and
	 * false otherwise.
	 */
	private boolean viewSupportsHoverHints() {
		boolean reply;
		switch (m_calendarDisplayData.getDayView()) {
		default:     reply = false; break;
		case MONTH:  reply = true;  break;
		}
		return reply;
	}
	
	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * Overrides the FolderViewBase.viewComplete() method.
	 */
	@Override
	public void viewComplete() {
		// Tell the super class the view is complete...
		super.viewComplete();
		
		// ...and tell the calendar to resize itself now that it can
		// ...determine how big everything is.
		resizeViewAsync();
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
	public static void createAsync(final BinderInfo folderInfo, final UIObject parent, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(CalendarFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				CalendarFolderView dfView = new CalendarFolderView(folderInfo, parent, viewReady);
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
