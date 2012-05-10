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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.CalendarDisplayDataProvider.AsyncCalendarDisplayDataCallback;
import org.kablink.teaming.gwt.client.event.CalendarHoursFullDayEvent;
import org.kablink.teaming.gwt.client.event.CalendarHoursWorkDayEvent;
import org.kablink.teaming.gwt.client.event.CalendarNextPeriodEvent;
import org.kablink.teaming.gwt.client.event.CalendarPreviousPeriodEvent;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetCalendarDisplayDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class used for displaying the calendar navigation tool panel.  
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class CalendarNavigationPanel extends ToolPanelBase {
	private CalendarDisplayDataProvider			m_calendarDisplayDataProvider;	//
	private CalendarDisplayDataRpcResponseData	m_calendarDisplayData;			//
	private VibeFlowPanel						m_fp;							// The panel holding the content.
	
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
		
		// ...and construct the panel.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-binderViewTools vibe-calNav-panel");
		initWidget(m_fp);
		loadPart1Async();
	}
	
	/*
	 * Returns the menu bar item for the hours menu.
	 */
	private VibeMenuBar buildHoursMenu() {
		// Construct the menu bar to return.
		VibeMenuBar reply = new VibeMenuBar("vibe-entryMenuBar vibe-calNav-hoursMenu");

		// Extract the current hours value from the display
		// information.
//! 	...this needs to be implemented...
		String selectedHours = m_messages.calendarNav_Hours_WorkDay();

		// Generate the top level menu item...
		VibeMenuBar	hoursMenuBar = new VibeMenuBar(true, "vibe-entryMenuPopup");	// true -> Vertical drop down menu.
		final VibeMenuItem hoursMenuItem = new VibeMenuItem(selectedHours, false, hoursMenuBar, "vibe-entryMenuBarItem");
		hoursMenuItem.setHTML(renderItemHTML(selectedHours, true));

		// ...the menu item for 'full day'...
		hoursMenuBar.addItem(new VibeMenuItem(m_messages.calendarNav_Hours_FullDay(), false, new Command() {
			@Override
			public void execute() {
				hoursMenuItem.setHTML(renderItemHTML(m_messages.calendarNav_Hours_FullDay(), true));
				GwtTeaming.fireEvent(new CalendarHoursFullDayEvent(m_binderInfo.getBinderIdAsLong()));
			}
		}));
		
		// ...and the menu item for 'work day'.
		hoursMenuBar.addItem(new VibeMenuItem(m_messages.calendarNav_Hours_WorkDay(), false, new Command() {
			@Override
			public void execute() {
				hoursMenuItem.setHTML(renderItemHTML(m_messages.calendarNav_Hours_WorkDay(), true));
				GwtTeaming.fireEvent(new CalendarHoursWorkDayEvent(m_binderInfo.getBinderIdAsLong()));
			}
		}));

		// Finally, return the menu bar item we constructed.
		reply.addItem(hoursMenuItem);
		return reply;
	}
	
	/*
	 * Constructs a widget that can be used as a separator on the
	 * calendar navigation bar.
	 */
	private static Widget buildSeparator() {
		InlineLabel reply = new InlineLabel();
		reply.addStyleName("vibe-calNav-separator");
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
		Scheduler.ScheduledCommand doLoad = new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
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
					new GetCalendarDisplayDataCmd(m_binderInfo),
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
	 * Asynchronously renders the calendar navigation panel.
	 */
	private void renderCalendarNavigationAsync() {
		ScheduledCommand doRender = new ScheduledCommand() {
			@Override
			public void execute() {
				renderCalendarNavigationNow();
			}
		};
		Scheduler.get().scheduleDeferred(doRender);
	}
	
	/*
	 * Synchronously renders the calendar navigation panel.
	 */
	private void renderCalendarNavigationNow() {
		// Add the hours select widget...
		m_fp.add(buildHoursMenu());
		m_fp.add(buildSeparator());
		
		// ...add the next/previous period buttons...
		m_fp.add(new CalendarNavigationButton(m_images.previous16(), m_images.previousDisabled16(), m_images.previousMouseOver16(), true, m_messages.calendarNav_Alt_PreviousTimePeriod(), new CalendarPreviousPeriodEvent(m_binderInfo.getBinderIdAsLong())));
		m_fp.add(new CalendarNavigationButton(m_images.next16(),     m_images.nextDisabled16(),     m_images.nextMouseOver16(),     true, m_messages.calendarNav_Alt_NextTimePeriod(),     new CalendarNextPeriodEvent(    m_binderInfo.getBinderIdAsLong())));

		// ...add the currently selected date/date range...
//!		...this needs to be implemented...
		
		// ...add the date navigation buttons...
//!		...this needs to be implemented...

		// ...add the view selection buttons...
//!		...this needs to be implemented...
		
		// ...and add the settings button.
//!		...this needs to be implemented...
		
		InlineLabel il = new InlineLabel("CalendarNavigationPanel.renderCalendarNavigationNow():  ...this needs to be implemented...");
		il.addStyleName("white marginleft8px");
		m_fp.add(il);
		
		// Finally, tell our container that we're ready.
		toolPanelReady();
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
		m_fp.clear();
		renderCalendarNavigationAsync();
	}
}
