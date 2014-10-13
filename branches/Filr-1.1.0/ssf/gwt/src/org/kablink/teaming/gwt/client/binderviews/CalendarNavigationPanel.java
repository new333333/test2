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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.CalendarDisplayDataProvider.AsyncCalendarDisplayDataCallback;
import org.kablink.teaming.gwt.client.event.CalendarChangedEvent;
import org.kablink.teaming.gwt.client.event.CalendarGotoDateEvent;
import org.kablink.teaming.gwt.client.event.CalendarHoursEvent;
import org.kablink.teaming.gwt.client.event.CalendarNextPeriodEvent;
import org.kablink.teaming.gwt.client.event.CalendarPreviousPeriodEvent;
import org.kablink.teaming.gwt.client.event.CalendarSettingsEvent;
import org.kablink.teaming.gwt.client.event.CalendarViewDaysEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CalendarDayView;
import org.kablink.teaming.gwt.client.util.CalendarHours;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.EventButton;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeHorizontalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Class used for displaying the calendar navigation tool panel.  
 * 
 * @author drfoster@novell.com
 */
public class CalendarNavigationPanel extends ToolPanelBase
	implements
		// Event handlers implemented by this class.
		CalendarChangedEvent.Handler
{
	private boolean								m_isIE;							//
	private boolean								m_toolPanelReady;				//
	private CalendarDisplayDataProvider			m_calendarDisplayDataProvider;	//
	private CalendarDisplayDataRpcResponseData	m_calendarDisplayData;			//
	private List<HandlerRegistration>			m_registeredEventHandlers;		// Event handlers that are currently registered.
	private long								m_browserTZOffset;				// The timezone offset from the browser.
	private VibeHorizontalPanel					m_hp;							// The panel holding the content.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		TeamingEvents.CALENDAR_CHANGED,
	};
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CalendarNavigationPanel(RequiresResize containerResizer, CalendarDisplayDataProvider calendarDisplayDataProvider, BinderInfo binderInfo, ToolPanelReady toolPanelReady) {
		// Initialize the super class...
		super(containerResizer, binderInfo, toolPanelReady);
		
		// ...store the parameters...
		m_calendarDisplayDataProvider = calendarDisplayDataProvider;
		
		// ...initialize the other data members...
		m_isIE            = GwtClientHelper.jsIsIE();
		m_browserTZOffset = (GwtClientHelper.getTimeZoneOffsetMillis(new Date()) * (-1l));
		
		
		// ...and construct the panel.
		m_hp = new VibeHorizontalPanel("100%", null);
		m_hp.addStyleName("vibe-binderViewTools vibe-calNav-panel");
		initWidget(m_hp);
		loadPart1Async();
	}

	/*
	 * Adds a widget to a horizontal panel with middle vertical
	 * alignment. 
	 */
	private void addHPCellVMiddle(VibeHorizontalPanel hp, Widget w, HasHorizontalAlignment.HorizontalAlignmentConstant rac) {
		hp.add(w);
		hp.setCellVerticalAlignment(w, HasVerticalAlignment.ALIGN_MIDDLE);
		if (null != rac) {
			hp.setCellHorizontalAlignment(w, rac);
		}
	}
	
	private void addHPCellVMiddle(VibeHorizontalPanel hp, Widget w) {
		// Always use the initial form of the method.
		addHPCellVMiddle(hp, w, null);
	}
	
	/*
	 * Returns a widget with the date/date range.
	 */
	private Widget buildDateDisplay() {
		// Extract the current date/date range from the display
		// information.
		InlineLabel il = new InlineLabel(m_calendarDisplayData.getDisplayDate());
		il.addStyleName("vibe-calNav-date");
		
		VibeFlowPanel reply = new VibeFlowPanel();
		reply.addStyleName(buildDisplayStyle("vibe-calNav-datePanel"));
		reply.add(il);
		
		return reply;
	}

	/*
	 * Returns a widget with the date navigation buttons.
	 */
	private Widget buildDateNavigation() {
		// Construct a button to activate the date picker...
		final EventButton datePickerButton = new EventButton(
			m_images.calDatePicker(),
			null,
			m_images.calDatePickerMouseOver(),
			true,
			m_messages.calendarNav_Alt_GoTo(),
			((Command) null));
		datePickerButton.addStyleName("vibe-calNav-datePicker");

		// ...connect a command to it that gets called when the button
		// ...is selected...
		datePickerButton.setCommand(
			new Command() {
				@Override
				public void execute() {
					final TeamingPopupPanel popup = new TeamingPopupPanel(true);
					popup.setStyleName("dateBoxPopup");
					DatePicker dp = new DatePicker();
					dp.addValueChangeHandler(new ValueChangeHandler<Date>() {
						@Override
						public void onValueChange(ValueChangeEvent<Date> event) {
							popup.hide();
							navigateToDateAsync(event.getValue());
						}
					});
					popup.add(dp);
					popup.showRelativeTo(datePickerButton);
				}
			});

		// ...construct a button to navigate to today...
		final EventButton todayButton = new EventButton(
			m_images.calViewToday(),
			null,
			m_images.calViewTodayMouseOver(),
			true,
			m_messages.calendarNav_Alt_GoToToday(),
			new Command() {
				@Override
				public void execute() {
					navigateToDateAsync(new Date());
				}
			});
		todayButton.addStyleName("vibe-calNav-today");

		// ...add the buttons to a panel...
		VibeFlowPanel reply = new VibeFlowPanel();
		reply.addStyleName(buildDisplayStyle("vibe-calNav-dateNavPanel"));
		reply.add(datePickerButton);
		reply.add(todayButton     );

		// ...and return the panel.
		return reply;
	}

	/*
	 * Returns a widget containing the days viewed selection buttons.
	 */
	private Widget buildDaysSelection() {
		VibeFlowPanel reply = new VibeFlowPanel();
		reply.addStyleName(buildDisplayStyle("vibe-calNav-daysSelPanel"));
		
		EventButton oneDay    = new EventButton(m_images.calView1Day(),  null, m_images.calView1DayMouseOver(),  true, m_messages.calendarNav_Alt_View1(),        new CalendarViewDaysEvent(m_binderInfo.getBinderIdAsLong(), CalendarDayView.ONE_DAY),    "vibe-calNav-daysSelImg");
		EventButton threeDays = new EventButton(m_images.calView3Day(),  null, m_images.calView3DayMouseOver(),  true, m_messages.calendarNav_Alt_View3(),        new CalendarViewDaysEvent(m_binderInfo.getBinderIdAsLong(), CalendarDayView.THREE_DAYS), "vibe-calNav-daysSelImg");
		EventButton fiveDays  = new EventButton(m_images.calView5Day(),  null, m_images.calView5DayMouseOver(),  true, m_messages.calendarNav_Alt_ViewWorkWeek(), new CalendarViewDaysEvent(m_binderInfo.getBinderIdAsLong(), CalendarDayView.FIVE_DAYS),  "vibe-calNav-daysSelImg");
		EventButton oneWeek   = new EventButton(m_images.calViewWeek(),  null, m_images.calViewWeekMouseOver(),  true, m_messages.calendarNav_Alt_ViewWeek(),     new CalendarViewDaysEvent(m_binderInfo.getBinderIdAsLong(), CalendarDayView.WEEK),       "vibe-calNav-daysSelImg");
		EventButton twoWeeks  = new EventButton(m_images.calView2Week(), null, m_images.calView2WeekMouseOver(), true, m_messages.calendarNav_Alt_View2Weeks(),   new CalendarViewDaysEvent(m_binderInfo.getBinderIdAsLong(), CalendarDayView.TWO_WEEKS),  "vibe-calNav-daysSelImg");
		EventButton oneMonth  = new EventButton(m_images.calViewMonth(), null, m_images.calViewMonthMouseOver(), true, m_messages.calendarNav_Alt_ViewMonth(),    new CalendarViewDaysEvent(m_binderInfo.getBinderIdAsLong(), CalendarDayView.MONTH),      "vibe-calNav-daysSelImg");
		
		// Extract the current day view selection from the display
		// information.
		switch (m_calendarDisplayData.getDayView()) {
		case ONE_DAY:     oneDay.setBaseImgRes(   m_images.calView1DaySelected());  break;
		case THREE_DAYS:  threeDays.setBaseImgRes(m_images.calView3DaySelected());  break;
		case FIVE_DAYS:   fiveDays.setBaseImgRes( m_images.calView5DaySelected());  break;
		case WEEK:        oneWeek.setBaseImgRes(  m_images.calViewWeekSelected());  break;
		case TWO_WEEKS:   twoWeeks.setBaseImgRes( m_images.calView2WeekSelected()); break;
		case MONTH:       oneMonth.setBaseImgRes( m_images.calViewMonthSelected()); break;
		}
		
		reply.add(oneDay   );
		reply.add(threeDays);
		reply.add(fiveDays );
		reply.add(oneWeek  );
		reply.add(twoWeeks );
		reply.add(oneMonth );
		
		return reply;
	}

	/*
	 * Returns a string with a base style and a IE/non-IE specific
	 * display style.
	 */
	private String buildDisplayStyle(String baseStyle) {
		return (baseStyle + " " + (m_isIE ? "displayInline" : "displayInlineBlock"));
	}
	
	/*
	 * Returns a widget with the hours menu.
	 */
	private Widget buildHoursMenu() {
		VibeFlowPanel reply = new VibeFlowPanel();
		reply.addStyleName(buildDisplayStyle("vibe-calNav-hoursMenuPanel"));
		
		// Construct the menu bar to return.
		VibeMenuBar hoursMenu = new VibeMenuBar(buildDisplayStyle("vibe-entryMenuBar vibe-calNav-hoursMenu"));

		// Extract the current hours value from the display
		// information.
		String selectedHours;
		switch(m_calendarDisplayData.getHours()) {
		default:
		case WORK_DAY:  selectedHours = m_messages.calendarNav_Hours_WorkDay(); break;
		case FULL_DAY:  selectedHours = m_messages.calendarNav_Hours_FullDay(); break;
		}

		// Generate the top level menu item...
		VibeMenuBar	hoursMenuBar = new VibeMenuBar(true, "vibe-entryMenuPopup");	// true -> Vertical drop down menu.
		final VibeMenuItem hoursMenuItem = new VibeMenuItem(selectedHours, false, hoursMenuBar, "vibe-entryMenuBarItem");
		hoursMenuItem.setHTML(renderItemHTML(selectedHours, true));

		// ...the menu item for 'full day'...
		hoursMenuBar.addItem(new VibeMenuItem(m_messages.calendarNav_Hours_FullDay(), false, new Command() {
			@Override
			public void execute() {
				hoursMenuItem.setHTML(renderItemHTML(m_messages.calendarNav_Hours_FullDay(), true));
				GwtTeaming.fireEvent(new CalendarHoursEvent(m_binderInfo.getBinderIdAsLong(), CalendarHours.FULL_DAY));
			}
		}));
		
		// ...the menu item for 'work day'...
		hoursMenuBar.addItem(new VibeMenuItem(m_messages.calendarNav_Hours_WorkDay(), false, new Command() {
			@Override
			public void execute() {
				hoursMenuItem.setHTML(renderItemHTML(m_messages.calendarNav_Hours_WorkDay(), true));
				GwtTeaming.fireEvent(new CalendarHoursEvent(m_binderInfo.getBinderIdAsLong(), CalendarHours.WORK_DAY));
			}
		}));

		// ...and add the menu item to the menu bar.
		hoursMenu.addItem(hoursMenuItem);
		
		// Finally, tie everything together.
		reply.add(hoursMenu       );
		reply.add(buildSeparator());
		
		return reply;
	}

	/*
	 * Returns a widget containing the period navigation buttons.
	 */
	private Widget buildPeriodNavigation() {
		VibeFlowPanel reply = new VibeFlowPanel();
		reply.addStyleName(buildDisplayStyle("vibe-calNav-periodNavPanel"));
		
		reply.add(new EventButton(m_images.previous16(), m_images.previousDisabled16(), m_images.previousMouseOver16(), true, m_messages.calendarNav_Alt_PreviousTimePeriod(), new CalendarPreviousPeriodEvent(m_binderInfo.getBinderIdAsLong())));
		reply.add(new EventButton(m_images.next16(),     m_images.nextDisabled16(),     m_images.nextMouseOver16(),     true, m_messages.calendarNav_Alt_NextTimePeriod(),     new CalendarNextPeriodEvent(    m_binderInfo.getBinderIdAsLong())));
		
		return reply;
	}
	
	/*
	 * Returns a widget with a separator that can be used between
	 * sections of the calendar navigation bar.
	 */
	private static Widget buildSeparator() {
		InlineLabel reply = new InlineLabel();
		reply.addStyleName("vibe-calNav-separator");
		return reply;
	}

	/*
	 * Returns a widget containing the settings access button.
	 */
	private Widget buildSettings() {
		VibeFlowPanel reply = new VibeFlowPanel();
		reply.addStyleName(buildDisplayStyle("vibe-calNav-settingsPanel"));

		reply.add(buildSeparator());
		reply.add(new EventButton(m_images.configOptions(), null, m_images.configOptionsMouseOver(), true, m_messages.calendarNav_Alt_Settings(), new CalendarSettingsEvent(m_binderInfo.getBinderIdAsLong())));
		
		return reply;
	}
	
	/**
	 * Loads the CalendarNavigationPanel split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param containerResizer
	 * @param binderInfo
	 * @param toolPanelReady
	 * @param tpClient
	 */
	public static void createAsync(final RequiresResize containerResizer, final CalendarDisplayDataProvider calendarDisplayDataProvider, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient tpClient) {
		GWT.runAsync(CalendarNavigationPanel.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				CalendarNavigationPanel bcp = new CalendarNavigationPanel(containerResizer, calendarDisplayDataProvider, binderInfo, toolPanelReady);
				tpClient.onSuccess(bcp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_CalendarNavigationPanel());
				tpClient.onUnavailable();
			}
		});
	}

	/*
	 * Asynchronously loads the next part of the calendar navigation
	 * panel.
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
	 * Synchronously loads the next part of the calendar navigation
	 * panel.
	 */
	private void loadPart1Now() {
		// Were we given a CalendarDisplayDataProvider?
		if (null != m_calendarDisplayDataProvider) {
			// Yes!  Can it get us the
			// CalendarDisplayDataRpcResponseData?
			m_calendarDisplayDataProvider.getCalendarDisplayData(new AsyncCalendarDisplayDataCallback() {
				@Override
				public void success(CalendarDisplayDataRpcResponseData data) {
					// Yes!  Store it and render the calendar
					// navigation panel.
					m_calendarDisplayData = data;
					renderCalendarNavigationAsync();
				}
				
				@Override
				public void failure() {
					// No, it couldn't provide the
					// CalendarDisplayDataRpcResponseData!  Forget
					// about the provider and try getting the display
					// data directly.
					m_calendarDisplayDataProvider = null;
					loadPart1Async();
				}
			});
		}
		
		else {
			// No, we weren't given a CalendarDisplayDataProvider!
			// Load the calendar display data directly.
			GwtClientHelper.executeCommand(
					new GetCalendarDisplayDataCmd(m_browserTZOffset, m_binderInfo),
					new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					GwtClientHelper.handleGwtRPCFailure(
						t,
						m_messages.rpcFailure_GetCalendarDisplayData(),
						m_binderInfo.getBinderIdAsLong());
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// Store the calendar display data and render the
					// panel.
					m_calendarDisplayData = ((CalendarDisplayDataRpcResponseData) response.getResponseData());
					renderCalendarNavigationAsync();
				}
			});
		}
	}
	
	/*
	 * Asynchronously navigates the calendar to the given date.
	 */
	private void navigateToDateAsync(final Date date) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				navigateToDateNow(date);
			}
		});
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
	 * Handles CalendarChangedEvent's received by this class.
	 * 
	 * Implements the CalendarChangedEvent.Handler.onCalendarChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCalendarChanged(CalendarChangedEvent event) {
		// Is the event targeted to this folder?
		if (event.getFolderId().equals(m_binderInfo.getBinderIdAsLong())) {
			// Yes!  Tell the navigation panel to reset.
			m_calendarDisplayData = event.getDisplayData();
			resetPanel();
		}
	}
	
	/*
	 * Synchronously navigates the calendar to the given date.
	 */
	private void navigateToDateNow(Date date) {
		GwtTeaming.fireEvent(new CalendarGotoDateEvent(m_binderInfo.getBinderIdAsLong(), date));
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

	/*
	 * Asynchronously renders the calendar navigation panel.
	 */
	private void renderCalendarNavigationAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				renderCalendarNavigationNow();
			}
		});
	}
	
	/*
	 * Synchronously renders the calendar navigation panel.
	 */
	private void renderCalendarNavigationNow() {
		// Add the various calendar navigation widgets.
		VibeFlowPanel leftPanel = new VibeFlowPanel();
		leftPanel.addStyleName(buildDisplayStyle("vibe-calNav-panelLeft"));
		VibeHorizontalPanel leftHP = new VibeHorizontalPanel(null, null);
		leftHP.addStyleName("vibe-calNav-panelLeftHP");
		leftPanel.add(leftHP);
		addHPCellVMiddle(m_hp,   leftHP                 );
		addHPCellVMiddle(leftHP, buildHoursMenu()       );
		addHPCellVMiddle(leftHP, buildPeriodNavigation());
		addHPCellVMiddle(leftHP, buildDateDisplay()     );
		
		VibeFlowPanel rightPanel = new VibeFlowPanel();
		rightPanel.addStyleName(buildDisplayStyle("vibe-calNav-panelRight"));
		addHPCellVMiddle(m_hp, rightPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		rightPanel.add(buildDateNavigation());
		rightPanel.add(buildDaysSelection() );
		rightPanel.add(buildSettings()      );
		
		// Finally, if we haven't told it yet...
		if (!m_toolPanelReady) {
			// ...tell our container that we're ready.
			toolPanelReady();
			m_toolPanelReady = true;
		}
	}

	/*
	 * Renders HTML for a menu item.
	 */
	private String renderItemHTML(String itemText, boolean enabled) {
		FlowPanel htmlPanel = new FlowPanel();
		InlineLabel itemLabel = new InlineLabel(itemText);
		itemLabel.addStyleName("vibe-mainMenuBar_BoxText");
		htmlPanel.add(itemLabel);

		Image dropDownImg = new Image(enabled ? GwtTeaming.getMainMenuImageBundle().menuArrow() : GwtTeaming.getMainMenuImageBundle().menuArrowGray());
		dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImg");
		if (!(GwtClientHelper.jsIsIE())) {
			dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImgNonIE");
		}
		htmlPanel.add(dropDownImg);
		
		return htmlPanel.getElement().getInnerHTML();
	}
	
	/**
	 * Called from the binder view to allow the panel to do any work
	 * required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel() {
		// Simply render the calendar navigation panel (again, if its
		// already been rendered.)
		m_hp.clear();
		renderCalendarNavigationAsync();
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
}
