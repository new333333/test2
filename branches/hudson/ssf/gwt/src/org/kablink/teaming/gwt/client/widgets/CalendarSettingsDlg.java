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
package org.kablink.teaming.gwt.client.widgets;

import java.util.Date;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveCalendarSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements Vibe's calendar settings dialog.
 *  
 * @author drfoster@novell.com
 */
public class CalendarSettingsDlg extends DlgBox implements EditSuccessfulHandler {
	private CalendarDisplayDataRpcResponseData	m_calendarDisplayData;	//
	private FlowPanel							m_dlgPanel;				// The panel holding the dialog's content.
	private GwtTeamingMessages					m_messages;				// Access to Vibe's messages.
	private ListBox								m_weekStartList;		//
	private ListBox								m_workDayStartList;		//
	private Long								m_folderId;				// The calendar folder whose settings are being configured.
	private String[]							m_weekDays;				//
	private String[]							m_workHours;			//

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	@SuppressWarnings("deprecation")
	private CalendarSettingsDlg() {
		// Initialize the super class...
		super(false, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_weekDays = new String[] {
			m_messages.calendarSettingsDlg_Day_Sunday(),
			m_messages.calendarSettingsDlg_Day_Monday(),
			m_messages.calendarSettingsDlg_Day_Tuesday(),
			m_messages.calendarSettingsDlg_Day_Wednesday(),
			m_messages.calendarSettingsDlg_Day_Thursday(),
			m_messages.calendarSettingsDlg_Day_Friday(),
			m_messages.calendarSettingsDlg_Day_Saturday(),
		};
		DateTimeFormat df = DateTimeFormat.getFormat("ha");
		Date d = new Date();
		m_workHours = new String[13];
		for (int hour = 0; hour <= 12; hour += 1) {
			d.setHours(hour);
			m_workHours[hour] = df.format(d).toLowerCase();
		}
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.calendarSettingsDlg_Header(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create and return a FlowPanel to hold the dialog's content.
		m_dlgPanel = new VibeFlowPanel();
		m_dlgPanel.addStyleName("vibe-calendarSettingsDlg-panel");
		return m_dlgPanel;
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
		// Save the calendar settings.
		setOkEnabled(false);
		saveCalendarSettingsAsync();
		
		// Always return false to leave the dialog open.  If it's
		// contents validate and the folder gets created, the dialog
		// will then be closed.
		return false;
	}

	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
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
		return null;
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
    
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear anything already in the dialog's panel.
		m_dlgPanel.clear();

		// Create a grid for holding the dialog's widgets.
		FlexTable grid = new FlexTable();
		grid.addStyleName("vibe-calendarSettingsDlg-grid");
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		m_dlgPanel.add(grid);

		// Add the widgets for adjusting the week start day.
		InlineLabel il = new InlineLabel(m_messages.calendarSettingsDlg_Label_WeekStartsOn());
		il.addStyleName("vibe-calendarSettingsDlg-label");
		grid.setWidget(0, 0, il);
		m_weekStartList = new ListBox(false);
		m_weekStartList.addStyleName("vibe-calendarSettingsDlg-list");
		m_weekStartList.addItem(m_weekDays[6], "7");
		m_weekStartList.addItem(m_weekDays[0], "1");
		m_weekStartList.addItem(m_weekDays[1], "2");
		int si;
		switch (m_calendarDisplayData.getWeekFirstDay()) {
		case 7:  si = 0;    break;
		case 1:  si = 1;    break;
		case 2:  si = 2;    break;
		default: si = (-1); break;
		}
		if ((-1) != si) {
			m_weekStartList.setSelectedIndex(si);
		}
		grid.setWidget(0, 1, m_weekStartList);

		// Add the widgets for adjusting the work day starting hour.
		il = new InlineLabel(m_messages.calendarSettingsDlg_Label_WorkDayStartsAt());
		il.addStyleName("vibe-calendarSettingsDlg-label");
		grid.setWidget(1, 0, il);
		m_workDayStartList = new ListBox(false);
		m_workDayStartList.addStyleName("vibe-calendarSettingsDlg-list");
		for (int hour = 0; hour < m_workHours.length; hour += 1) {
			m_workDayStartList.addItem(m_workHours[hour], String.valueOf(hour));
		}
		si = m_calendarDisplayData.getWorkDayStart();
		if ((0 <= si) && (12 >= si)) {
			m_workDayStartList.setSelectedIndex(si);
		}
		grid.setWidget(1, 1, m_workDayStartList);
		
		// Finally, show the dialog centered on the screen.
		GwtClientHelper.setFocusDelayed(m_workDayStartList);
		setCancelEnabled(true);
		setOkEnabled(    true);
		show(            true);
	}
	
	/*
	 * Asynchronously runs the given instance of the calendar settings
	 * dialog.
	 */
	private static void runDlgAsync(final CalendarSettingsDlg csDlg, final Long folderId, final CalendarDisplayDataRpcResponseData calendarDisplayData) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				csDlg.runDlgNow(folderId, calendarDisplayData);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the calendar settings
	 * dialog.
	 */
	private void runDlgNow(Long folderId, CalendarDisplayDataRpcResponseData calendarDisplayData) {
		// Store the parameters...
		m_folderId            = folderId;
		m_calendarDisplayData = calendarDisplayData;

		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Asynchronously saves the calendar settings.
	 */
	private void saveCalendarSettingsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				saveCalendarSettingsNow();
			}
		});
	}
	
	/*
	 * Synchronously saves the calendar settings.
	 */
	private void saveCalendarSettingsNow() {
		// What week starting day was selected?
		int si = m_weekStartList.getSelectedIndex();
		int weekStart = Integer.parseInt(m_weekStartList.getValue(si));
		if (weekStart == m_calendarDisplayData.getWeekFirstDay()) {
			weekStart = (-1);
		}

		// What work day starting hour was selected?
		si = m_workDayStartList.getSelectedIndex();
		int workDayStart = Integer.parseInt(m_workDayStartList.getValue(si));
		if (workDayStart == m_calendarDisplayData.getWorkDayStart()) {
			workDayStart = (-1);
		}

		// Did the user change their current settings?
		if (((-1) == weekStart) && ((-1) == workDayStart)) {
			// No!  Then we don't have to save anything.  SImply hide
			// the dialog and bail.
			setOkEnabled(true);
			hide();
			return;
		}

		// Can we save the calendar settings?
		GwtClientHelper.executeCommand(
				new SaveCalendarSettingsCmd(m_folderId, weekStart, workDayStart),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveCalendarSettings());
				setOkEnabled(true);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes!  Hide the dialog an put the new calendar
				// settings into affect...
				setOkEnabled(true);
				hide();
				FullUIReloadEvent.fireOne();
			}
		});
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the calendar settings dialog and perform some operation on    */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the calendarSettings dialog
	 * asynchronously after it loads. 
	 */
	public interface CalendarSettingsDlgClient {
		void onSuccess(CalendarSettingsDlg csDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the CalendarSettingsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final CalendarSettingsDlgClient csDlgClient,
			
			// initAndShow parameters,
			final CalendarSettingsDlg					csDlg,
			final Long									folderId,
			final CalendarDisplayDataRpcResponseData	calendarDisplayData) {
		GWT.runAsync(CalendarSettingsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_CalendarSettingsDlg());
				if (null != csDlgClient) {
					csDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != csDlgClient) {
					// Yes!  Create it and return it via the callback.
					CalendarSettingsDlg csDlg = new CalendarSettingsDlg();
					csDlgClient.onSuccess(csDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(csDlg, folderId, calendarDisplayData);
				}
			}
		});
	}
	
	/**
	 * Loads the CalendarSettingsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param csDlgClient
	 */
	public static void createAsync(CalendarSettingsDlgClient csDlgClient) {
		doAsyncOperation(csDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the calendar settings dialog.
	 * 
	 * @param csDlg
	 * @param folderId
	 * @param calendarDisplayData
	 */
	public static void initAndShow(CalendarSettingsDlg csDlg, Long folderId, CalendarDisplayDataRpcResponseData calendarDisplayData) {
		doAsyncOperation(null, csDlg, folderId, calendarDisplayData);
	}
}
