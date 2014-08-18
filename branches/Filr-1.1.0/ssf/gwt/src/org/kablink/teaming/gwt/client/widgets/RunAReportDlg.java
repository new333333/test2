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
import org.kablink.teaming.gwt.client.rpc.shared.GetReportsInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.JspHtmlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeJspHtmlType;
import org.kablink.teaming.gwt.client.rpc.shared.ReportsInfoRpcResponseData.ReportInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements the 'Run a Report' dialog.
 *  
 * @author drfoster@novell.com
 */
public class RunAReportDlg extends DlgBox
	implements
		// Event handlers implemented by this class.
		AdministrationExitEvent.Handler
{
	public static final boolean	SHOW_JSP_ADMIN_REPORTS	= false;	//! DRF (20121130):  Leave false on checkin.  Used to bring back the JSP versions for testing.
	
	private GwtTeamingMessages			m_messages;					// Access to Vibe's messages.
	private int							m_showX;					// The x and...
	private int							m_showY;					// ...y position to show the dialog.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private Map<AdminAction, Widget>	m_reportWidgets;			// Map of the report widgets, as they get created.
	private ReportsInfoRpcResponseData	m_reportsInfo;				// Information about the available reports obtained via a GWT RPC request to the server.
	private ScrollPanel					m_reportScrollPanel;		// The scroll panel that holds the the report's contents.
	private VibeFlowPanel				m_rootPanel;				// The panel that holds the dialog's contents.

	/*
	 * Inner class used to wrap an HTMLPanel for report content.
	 */
	private static class ReportHTMLPanel extends HTMLPanel {
		private boolean	m_firstAttach = true;	// Set false after the panel's been attached once.
		
		/**
		 * Constructor method.
		 * 
		 * @param html
		 */
		public ReportHTMLPanel(String html) {
			// Simply initialize the super class.
			super(html);
		}
		
		/**
		 * Called when the HTMLPanel is attached to the document.
		 * 
		 * Overrides the Widget.onAttach() method.
		 */
		@Override
		public void onAttach() {
			// Tell the super class that we've attached...
			super.onAttach();

			// ...and if this the first time we've attached...
			if (m_firstAttach) {
				// ...execute the HTML's contained JavaScript.
				m_firstAttach = false;
				executeJavaScriptAsync();
			}
		}
		
		/*
		 * Asynchronously executes the JavaScript in the HTML panel.
		 */
		private void executeJavaScriptAsync() {
			GwtClientHelper.deferCommand(
				new ScheduledCommand() {
					@Override
					public void execute() {
						executeJavaScriptNow();
					}
				});
		}
		
		/*
		 * Asynchronously executes the JavaScript in the HTML panel.
		 */
		private void executeJavaScriptNow() {
			GwtClientHelper.jsExecuteJavaScript(getElement(), true);
		}
	}
	
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
			DlgButtonMode.Close,
			false);	// false -> Don't use overflow auto on the content.

		// ...store the parameters...
		m_showX = x;
		m_showY = y;
		
		// ...initialize everything else...
		m_messages      = GwtTeaming.getMessages();
		m_reportWidgets = new HashMap<AdminAction, Widget>();
		
		// ...and create the dialog's content.
		addStyleName("vibe-runAReportDlg");
		createAllDlgContent(
			m_messages.runAReportDlgCaption(),
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			rarDlgClient); 
	}

	/*
	 * Wraps the given HTML in an HTMLPanel, stores it in the dialog's
	 * ScrollPanel and executes its contained JavaScript.
	 */
	private void buildAndSetHtmlContent(AdminAction reportAction, String html) {
		// Create the HTMLPanel...
		ReportHTMLPanel htmlPanel = new ReportHTMLPanel(html);
		htmlPanel.addStyleName("vibe-runAReportDlg-reportHtmltPanel");
		
		// ...put it in the ScrollPanel...
		m_reportScrollPanel.setWidget(htmlPanel);

		// ...and store it in the Map tracking them.
		m_reportWidgets.put(reportAction, htmlPanel);
	}
	
	/*
	 * Constructs a widget for running a change log report and stores
	 * it in the report panel.
	 */
	private void buildChangeLogReport() {
		ChangeLogReportComposite clrc = new ChangeLogReportComposite();
		m_reportScrollPanel.setWidget(clrc);
		m_reportWidgets.put(AdminAction.REPORT_VIEW_CHANGELOG, clrc);
	}
	
	/*
	 * Reads the credits HTML from the server and stores it in the
	 * report panel.
	 */
	private void buildCreditsReport() {
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(VibeJspHtmlType.ADMIN_REPORT_CREDITS),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetCreditsHtml());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display the credits report HTML in the report's
				// content panel.
				JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
				buildAndSetHtmlContent(AdminAction.REPORT_VIEW_CREDITS, responseData.getHtml());
			}
		});
	}
	
	/*
	 * Reads the data quota exceeded report HTML from the server and
	 * stores it in the report panel.
	 */
	private void buildDataQuotaExceededReport() {
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(VibeJspHtmlType.ADMIN_REPORT_DATA_QUOTA_EXCEEDED),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetDataQuotaExceededHtml());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display the data quota exceeded report HTML in the
				// report's content panel.
				JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
				buildAndSetHtmlContent(AdminAction.REPORT_DATA_QUOTA_EXCEEDED, responseData.getHtml());
			}
		});
	}
	
	/*
	 * Reads the data quota high water exceeded report HTML from the
	 * server and stores it in the report panel.
	 */
	private void buildDataQuotaHighwaterExceededReport() {
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(VibeJspHtmlType.ADMIN_REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetDataQuotaHighwaterExceededHtml());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display the data quota high water exceeded report
				// HTML in the report's content panel.
				JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
				buildAndSetHtmlContent(AdminAction.REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED, responseData.getHtml());
			}
		});
	}
	
	/*
	 * Reads the disk usage report HTML from the server and stores it
	 * in the report panel.
	 */
	private void buildDiskUsageReport() {
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(VibeJspHtmlType.ADMIN_REPORT_DISK_USAGE),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetDiskUsageHtml());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display the disk usage report HTML in the report's
				// content panel.
				JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
				buildAndSetHtmlContent(AdminAction.REPORT_DISK_USAGE, responseData.getHtml());
			}
		});
	}
	
	/*
	 * Constructs a widget for running an email report and stores it in
	 * the report panel.
	 */
	private void buildEmailReport() {
		EmailReportComposite erc = new EmailReportComposite();
		m_reportScrollPanel.setWidget(erc);
		m_reportWidgets.put(AdminAction.REPORT_EMAIL, erc);
	}
	
	/*
	 * Constructs a widget for running a license report and stores it
	 * in the report panel.
	 */
	private void buildLicenseReport() {
		LicenseReportComposite lrc = new LicenseReportComposite();
		m_reportScrollPanel.setWidget(lrc);
		m_reportWidgets.put(AdminAction.REPORT_LICENSE, lrc);
	}
	
	/*
	 * Constructs a widget for running a login report and stores it in
	 * the report panel.
	 */
	private void buildLoginReport() {
		LoginReportComposite lrc = new LoginReportComposite();
		m_reportScrollPanel.setWidget(lrc);
		m_reportWidgets.put(AdminAction.REPORT_LOGIN, lrc);
	}
	
	/*
	 * Constructs a widget for running a user access report and stores
	 * it in the report panel.
	 */
	private void buildUserAccessReport() {
		UserAccessReportComposite uarc = new UserAccessReportComposite();
		m_reportScrollPanel.setWidget(uarc);
		m_reportWidgets.put(AdminAction.REPORT_USER_ACCESS, uarc);
	}
	
	/*
	 * Constructs a widget for running a user activity report and
	 * stores it in the report panel.
	 */
	private void buildUserActivityReport() {
		UserActivityReportComposite uarc = new UserActivityReportComposite();
		m_reportScrollPanel.setWidget(uarc);
		m_reportWidgets.put(AdminAction.REPORT_ACTIVITY_BY_USER, uarc);
	}
	
	/*
	 * Constructs a widget for running a system error log report and
	 * stores it in the report panel.
	 */
	private void buildSystemErrorLogReport() {
		SystemErrorLogReportComposite selrc = new SystemErrorLogReportComposite();
		m_reportScrollPanel.setWidget(selrc);
		m_reportWidgets.put(AdminAction.REPORT_VIEW_SYSTEM_ERROR_LOG, selrc);
	}
	
	/*
	 * Reads the XSS report HTML from the server and stores it in the
	 * report panel.
	 */
	private void buildXssReport() {
		GwtClientHelper.executeCommand(
				new GetJspHtmlCmd(VibeJspHtmlType.ADMIN_REPORT_XSS),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetXssHtml());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Display the XSS report HTML in the report's content
				// panel.
				JspHtmlRpcResponseData responseData = ((JspHtmlRpcResponseData) response.getResponseData());
				buildAndSetHtmlContent(AdminAction.REPORT_XSS, responseData.getHtml());
			}
		});
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

		// Add a message about reports possibly being truncated if
		// they're too large.
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(6);
		hPanel.addStyleName("vibe-runAReportDlg-reportMaxSizePanel");
		Label warningLabel = new Label(m_messages.runAReportDlgMaxSize());
		hPanel.add(warningLabel);
		m_rootPanel.add(hPanel);

		// Create the panels to hold the selected report.
		m_reportScrollPanel = new ScrollPanel();
		m_reportScrollPanel.addStyleName("vibe-runAReportDlg-reportScrollPanel");
		m_rootPanel.add(m_reportScrollPanel);
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

	/**
	 * Returns a HelpData object for the dialog's help.
	 * 
	 * Overrides the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("reports_generate");
		return helpData;
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
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					handleReportActionNow(report);
				}
			});
	}
	
	/*
	 * Synchronously handles invoking a report.
	 */
	private void handleReportActionNow(ReportInfo report) {
		// Have we created the widget for this report yet?
		AdminAction reportAction = report.getReport();
		Widget w = m_reportWidgets.get(reportAction);
		if (null == w) {
			// No!  Display the newly selected report.
			switch (reportAction) {
			case REPORT_ACTIVITY_BY_USER:               buildUserActivityReport();               break;
			case REPORT_DATA_QUOTA_EXCEEDED:            buildDataQuotaExceededReport();          break;
			case REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED:  buildDataQuotaHighwaterExceededReport(); break;
			case REPORT_DISK_USAGE:                     buildDiskUsageReport();                  break;
			case REPORT_EMAIL:                          buildEmailReport();                      break;
			case REPORT_LICENSE:                        buildLicenseReport();                    break;
			case REPORT_LOGIN:                          buildLoginReport();                      break;
			case REPORT_VIEW_CHANGELOG:                 buildChangeLogReport();                  break;
			case REPORT_VIEW_CREDITS:                   buildCreditsReport();                    break;
			case REPORT_VIEW_SYSTEM_ERROR_LOG:          buildSystemErrorLogReport();             break;
			case REPORT_XSS:                            buildXssReport();                        break;
			case REPORT_USER_ACCESS:                    buildUserAccessReport();                 break;
				
			default:
				// Display an error for anything we don't know about. 
				GwtClientHelper.deferredAlert(m_messages.runAReportDlgInternalError_UnknownReport(reportAction.name()));
				break;
			}
		}
		
		else {
			// Yes, we've already created widget for this report!
			// Simply show it.
			m_reportScrollPanel.setWidget(w);
		}
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
	 * Called when the dialog is attached to the document.
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
	 * Called when the dialog is detached from the document.
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
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
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
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_rootPanel.clear();

		// ...create the widgets in the dialog...
		createContentWidgets();
		
		// ...and position and show the dialog.
		setPopupPosition(m_showX, m_showY);
		show();
		setReportSizeAsync();
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
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					rarDlg.runDlgNow(x, y);
				}
			});
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
	 * Asynchronously sets the size of the report panel.
	 */
	private void setReportSizeAsync() {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					setReportSizeNow();
				}
			});
	}
	
	/*
	 * Synchronously sets the size of the report panel.
	 */
	private void setReportSizeNow() {
		int rt = m_reportScrollPanel.getElement().getOffsetTop();
		int ft = getFooterPanel().getElement().getOffsetTop();
		int rh = ((ft - rt) - 5);
		m_reportScrollPanel.setHeight(rh + "px");
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
