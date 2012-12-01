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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetJspHtmlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetManageUsersInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetReportsInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.JspHtmlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageUsersInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeJspHtmlType;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData.ReportInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData.EntryType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.ManageUsersDlg.ManageUsersDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements the 'Run a Report' dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class RunAReportDlg extends DlgBox
	implements
		// Event handlers implemented by this class.
		AdministrationExitEvent.Handler
{
	public static final boolean	SHOW_GWT_ADMIN_REPORTS	= false;	// DRF:  Leave false on checkin until I get the GWT stuff working.
	public static final boolean	SHOW_JSP_ADMIN_REPORTS	= true;		// DRF:  Leave true  on checkin until I get the GWT stuff working.
	
	private GwtTeamingMessages			m_messages;					// Access to Vibe's messages.
	private int							m_showX;					// The x and...
	private int							m_showY;					// ...y position and...
	private int							m_showCX;					// ...width and...
	private int							m_showCY;					// ...height of the dialog.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ReportsInfoRpcResponseData	m_reportsInfo;				//
	private ScrollPanel					m_reportPanel;				// The panel that holds the report's contents.
	private VibeFlowPanel				m_rootPanel;				// The panel that holds the dialog's contents.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.ADMINISTRATION_EXIT,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private RunAReportDlg(RunAReportDlgClient rarDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		// Initialize the superclass...
		super(
			autoHide,
			modal,
			x, y, cx, cy,
			DlgButtonMode.Close);

		// ...store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = cx;
		m_showCY = cy;
		
		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		addStyleName("vibe-runAReportDlg");
		createAllDlgContent(
			m_messages.runAReportDlgCaption(),
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			rarDlgClient); 
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Can we get the information necessary to construct the
		// reports list?
		final RunAReportDlg			rarDlg       = this;
		final RunAReportDlgClient	rarDlgClient = ((RunAReportDlgClient) callbackData);
		GwtClientHelper.executeCommand(new GetReportsInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetReportsInfo());

				// ...and tell the caller that the dialog will be
				// ...unavailable.
				rarDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Yes!  Store it and tell the caller that the dialog
				// is available.
				m_reportsInfo = ((ReportsInfoRpcResponseData) result.getResponseData());
				rarDlgClient.onSuccess(rarDlg);
			}
		});
		
		// Create the main panel that will hold the dialog's content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-runAReportDlg-rootPanel");

		// ...and return it.  Note that it will get populated during
		// ...the initAndShow() call.
		return m_rootPanel;
	}

	/*
	 * Creates the widgets within the dialog.
	 */
	private void createContentWidgets() {
		// Add the <SELECT> for which report to run...
		InlineLabel il = new InlineLabel(m_messages.runAReportDlgChoose());
		il.addStyleName("vibe-runAReportDlg-chooseLabel");
		il.setWordWrap(false);
		m_rootPanel.add(il);
		final ListBox reportsLB = new ListBox();
		reportsLB.addStyleName("vibe-runAReportDlg-chooseSelect");
		m_rootPanel.add(reportsLB);
		reportsLB.addItem(m_messages.runAReportDlgSelect(), "");
		for (ReportInfo report:  m_reportsInfo.getReports()) {
			reportsLB.addItem(report.getTitle(), String.valueOf(report.getReport().ordinal()));
		}
		reportsLB.setSelectedIndex(0);
		
		// ...and add a handler to it to handle the user's selection.
		reportsLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// If the user has somehow selected the select a report
				// option...
				int		si     = reportsLB.getSelectedIndex();
				String	report = reportsLB.getValue(si);
				if (0 == report.length()) {
					// ...ignore it.
					return;
				}

				// Get the ReportInfo for the selected report...
				ReportInfo selectedReport = getReportInfo(AdminAction.getEnum(Integer.parseInt(report)));
				
				// ...and if the first item in the <SELECT> is the
				// ...select a report option... 
				if ((0 < si) && (0 == reportsLB.getValue(0).length())) {
					// ...remove it...
					reportsLB.removeItem(0);
				}

				// ...and handle the selected report.
				handleReportActionAsync(selectedReport);
			}
		});
		
		// Create a panel to hold the selected report.
		m_reportPanel = new ScrollPanel();
		m_reportPanel.addStyleName("vibe-runAReportDlg-reportPanel");
		m_rootPanel.add(m_reportPanel);
		m_reportPanel.setVisible(false);
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
		// Nothing focusable in the dialog.
		return null;
	}

	/*
	 * Returns the ReportInfo from the ReportInfoRpcResponseData that
	 * corresponds to an AdminAction.
	 */
	private ReportInfo getReportInfo(AdminAction reportAction) {
		for (ReportInfo report:  m_reportsInfo.getReports()) {
			if (report.getReport().equals(reportAction)) {
				return report;
			}
		}
		return null;
	}

	/*
	 * Asynchronously handles invoking a report.
	 */
	private void handleReportActionAsync(final ReportInfo report) {
		ScheduledCommand doReport = new ScheduledCommand() {
			@Override
			public void execute() {
				handleReportActionNow(report);
			}
		};
		Scheduler.get().scheduleDeferred(doReport);
	}
	
	/*
	 * Synchronously handles invoking a report.
	 */
	private void handleReportActionNow(ReportInfo report) {
		m_reportPanel.clear();
		
		switch (report.getReport()) {
		case REPORT_VIEW_CREDITS:
			showCredits();
			break;
			
		default:
//!			...this needs to be implemented...		
			m_reportPanel.setVisible(false);
			GwtClientHelper.deferredAlert("RunAReportDlg.handleReportActionNow( " + report.getReport().name() + " ):  ...this needs to be implemented...");
			return;
		}
		
		m_reportPanel.setVisible(true);
	}
	
	/**
	 * Handles AdministrationExitEvent's received by this class.
	 * 
	 * Implements the AdministrationExitEvent.Handler.onAdministrationExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationExit(AdministrationExitEvent event) {
		// If the administration console is exited, simply close the
		// dialog.
		hide();
	}
	
	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Attach the widget and register the event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the data table is detached.
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

	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_rootPanel.clear();

		// ...create the widgets in the dialog...
		createContentWidgets();
		
		// ...and position and show the dialog.
		setPopupPosition(m_showX, m_showY);
		show();
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
	 * Asynchronously runs the given instance of the run a report
	 * dialog.
	 */
	private static void runDlgAsync(final RunAReportDlg rarDlg, final int x, final int y) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				rarDlg.runDlgNow(x, y);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the run a report
	 * dialog.
	 */
	private void runDlgNow(int x, int y) {
		// Store the parameters...
		m_showX = x;
		m_showY = y;
		
		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Reads the credits HTML from the server and stores it in the
	 * report panel.
	 */
	private void showCredits() {
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(VibeJspHtmlType.CREDITS),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetCreditsHtml(),
					VibeJspHtmlType.ACCESSORY_PANEL.toString());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display the credits HTML in the report panel.
				JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
				m_reportPanel.getElement().setInnerHTML(responseData.getHtml());
			}
		});
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
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the run a report dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the run a report dialog
	 * asynchronously after it loads. 
	 */
	public interface RunAReportDlgClient {
		void onSuccess(RunAReportDlg rarDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the RunAReportDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the dialog.
			final RunAReportDlgClient	rarDlgClient,
			final boolean				autoHide,
			final boolean				modal,
			final int					createX,
			final int					createY,
			final int					createCX,
			final int					createCY,
			
			// Parameters used to initialize and show an instance of the dialog.
			final RunAReportDlg	rarDlg,
			final int				initX,
			final int				initY) {
		GWT.runAsync(RunAReportDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_RunAReportDlg());
				if (null != rarDlgClient) {
					rarDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != rarDlgClient) {
					// Yes!  Create the dialog.  Note that its
					// construction flow will call the appropriate
					// method off the RunAReportDlgClient object.
					new RunAReportDlg(rarDlgClient, autoHide, modal, createX, createY, createCX, createCY);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(rarDlg, initX, initY);
				}
			}
		});
	}
	
	/**
	 * Loads the RunAReportDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param rarDlgClient
	 * @param autoHide
	 * @param modal
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void createAsync(RunAReportDlgClient rarDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		doAsyncOperation(rarDlgClient, autoHide, modal, x, y, cx, cy, null, (-1), (-1));
	}
	
	/**
	 * Initializes and shows the run a report dialog.
	 * 
	 * @param rarDlg
	 * @param x
	 * @param y
	 */
	public static void initAndShow(RunAReportDlg rarDlg, int x, int y) {
		doAsyncOperation(null, false, false, (-1), (-1), (-1), (-1), rarDlg, x, y);
	}
}
