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

import java.util.Date;
import java.util.List;

import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CreateLicenseReportCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseItem;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseReleaseInfo;
import org.kablink.teaming.gwt.client.rpc.shared.LicenseReportRpcResponseData.LicenseStatsItem;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * Composite that runs a license report.
 * 
 * @author drfoster@novell.com
 */
public class LicenseReportComposite extends ReportCompositeBase {
	private DateBox							m_beginDateBox;		// The DateBox for selecting the date to begin the report at.
	private DateBox							m_endDateBox;		// The DateBox for selecting the date to end   the report at.
	private LicenseReportRpcResponseData	m_licenseReport;	// License report data once read from the server.
	private VibeFlowPanel					m_reportPanel;		// The panel containing the report table.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};
	
	/**
	 * Constructor method.
	 */
	public LicenseReportComposite() {
		// Simply initialize the super class.
		super();
	}

	/**
	 * Creates the content for the report.
	 * 
	 * Overrides the ReportCompositeBase.createContent() method.
	 */
	@Override
	public void createContent() {
		// Let the super class create the initial base content...
		super.createContent();
		
		// ...add a caption above the content...
		InlineLabel il = buildInlineLabel(m_messages.licenseReportCaption(), "vibe-reportCompositeBase-caption");
		m_rootContent.add(il);

		// ...add a panel for the report widgets...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-reportCompositeBase-widgetsPanel");
		m_rootContent.add(fp);

		// ...add a horizontal panel for the date selectors...
		HorizontalPanel dates = new HorizontalPanel();
		dates.addStyleName("vibe-licenseReportComposite-datesPanel");
		dates.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		fp.add(dates);

		// ...and a beginning date selector...
		DateTimeFormat dateFormat    = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
		DefaultFormat  dateFormatter = new DefaultFormat(dateFormat);
		Date beginDate = new Date();
		CalendarUtil.addMonthsToDate(beginDate, (-1));
		m_beginDateBox = new DateBox(new DatePicker(), beginDate, dateFormatter);
		dates.add(m_beginDateBox);

		// ...and 'and' between the two date selectors...
		il = buildInlineLabel(m_messages.licenseReportAndSeparator(), "vibe-reportCompositeBase-andSeparator");
		dates.add(il);
		
		// ...and an ending date selector...
		m_endDateBox = new DateBox(new DatePicker(), new Date(), dateFormatter);
		dates.add(m_endDateBox);

		// ...add the 'Run Report' push button...
		Button runReportBtn = new Button(m_messages.licenseReportRunReport());
		runReportBtn.addStyleName("vibe-reportCompositeBase-buttonBase vibe-reportCompositeBase-runButton");
		runReportBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createReport();
			}
		});
		fp.add(runReportBtn);
		
		// ...and add the panel containing the output of the report.
		m_reportPanel = new VibeFlowPanel();
		m_reportPanel.addStyleName("vibe-licenseReportComposite-reportPanel");
		m_reportPanel.setVisible(false);	// Initially hidden, shown once a report is created.
		m_rootContent.add(m_reportPanel);
	}
	
	/*
	 * Creates a report and uploads it into the display.
	 */
	private void createReport() {
		m_busySpinner.center();
		GwtClientHelper.executeCommand(
				new CreateLicenseReportCmd(m_beginDateBox.getValue(), m_endDateBox.getValue()),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				m_busySpinner.hide();
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_CreateLicenseReport());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				m_licenseReport = ((LicenseReportRpcResponseData) response.getResponseData());
				populateReportResultsAsync();
			}
		});
	}
	
	/**
	 * Returns a TeamingEvents[] of the events to be registered for the
	 * composite.
	 *
	 * Implements the ReportCompositeBase.getRegisteredEvents() method.
	 * 
	 * @return
	 */
	@Override
	public TeamingEvents[] getRegisteredEvents() {
		return REGISTERED_EVENTS;
	}

	/*
	 * Returns a non-null long.
	 */
	private static long safeL(Long l) {
		return ((null == l) ? 0 : l);
	}
	
	/*
	 * Returns a non-null string.
	 */
	private static String safeS(String s) {
		return ((null == s) ? "" : s);
	}
	
	/*
	 * Asynchronously populates the results of a report.
	 */
	private void populateReportResultsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateReportResultsNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the results of a report.
	 */
	private void populateReportResultsNow() {
		// Reset the report panel.
		resetReportPanel(true);	// true -> Show the report panel.

		// Add a caption with the product/version information.
		LicenseReleaseInfo lri = m_licenseReport.getReleaseInfo();
		InlineLabel il = new InlineLabel(m_messages.licenseReportReport_License(lri.getName(), lri.getVersion(), m_licenseReport.getReportDate()));
		il.addStyleName("vibe-licenseReportComposite-reportDataCaption");
		m_reportPanel.add(il);

		// Are there any licenses installed?
		List<LicenseItem> licenses = m_licenseReport.getLicenses();
		if (GwtClientHelper.hasItems(licenses)) {
			// Yes!  Pick the last one...
			LicenseItem curLic = licenses.get(licenses.size() - 1);

			// ...and generate the display from that.
			GwtClientHelper.addBR(m_reportPanel, 2);
			il = new InlineLabel(m_messages.licenseReportReport_CurrentLicense());
			il.addStyleName("vibe-licenseReportComposite-reportDataCaption");
			m_reportPanel.add(il);
			
			VibeFlexTable ft = new VibeFlexTable();
			ft.addStyleName("vibe-licenseReportComposite-reportDataCurLic");
			m_reportPanel.add(ft);
			ft.setCellSpacing(2);
			ft.setCellPadding(0);
			ft.setText(0, 0, m_messages.licenseReportReport_ProductTitle());
			ft.setText(0, 1, safeS(curLic.getProductTitle()) + " " + safeS(curLic.getProductVersion()));
			
			ft.setText(1, 0, m_messages.licenseReportReport_KeyUID());
			ft.setText(1, 1, safeS(m_licenseReport.getLicenseKey()));
			
			ft.setText(2, 0, m_messages.licenseReportReport_KeyIssued());
			ft.setText(2, 1, safeS(curLic.getIssued()));
			
			ft.setText(3, 0, m_messages.licenseReportReport_Effective());
			ft.setText(3, 1, safeS(curLic.getEffectiveStart()) + " - " + safeS(curLic.getEffectiveEnd()));
			
			ft.setText(4, 0, m_messages.licenseReportReport_AllowedReg());
			long uc = m_licenseReport.getRegisteredUsers();
			ft.setText(4, 1, (0 > uc) ? m_messages.licenseReportReport_AllowedRegNote() : String.valueOf(uc));
			
			ft.setText(5, 0, m_messages.licenseReportReport_AllowedExt());
			uc = m_licenseReport.getExternalUsers();
			ft.setText(5, 1, (0 > uc) ? m_messages.licenseReportReport_AllowedExtNote() : String.valueOf(uc));
		}

		// Add information about the dates this report spans.
		GwtClientHelper.addBR(m_reportPanel, 1);
		il = buildInlineLabel(safeS(m_messages.licenseReportReport_Activity()), "vibe-licenseReportComposite-reportDataCaption");
		m_reportPanel.add(il);
		il = buildInlineLabel(" " + safeS(m_licenseReport.getBeginDate()) + " " + m_messages.licenseReportAndSeparator() + " " + safeS(m_licenseReport.getEndDate()));
		m_reportPanel.add(il);

		// Are there any license statistics items?
		List<LicenseStatsItem> licenseStats = m_licenseReport.getLicenseStats();
		if (GwtClientHelper.hasItems(licenseStats)) {
			// Yes!  The current one is last one.  Add the current user
			// count.
			LicenseStatsItem curStat = licenseStats.get(licenseStats.size() - 1);
			GwtClientHelper.addBR(m_reportPanel, 1);
			il = buildInlineLabel(safeS(m_messages.licenseReportReport_CurrentActive(safeL(curStat.getActiveUserCount()))));
			m_reportPanel.add(il);

			// Create a table for the report snapshots...
			GwtClientHelper.addBR(m_reportPanel, 2);
			VibeFlexTable ft = new VibeFlexTable();
			ft.addStyleName("vibe-licenseReportComposite-reportDataStats");
			m_reportPanel.add(ft);
			ft.setCellSpacing(2);
			ft.setCellPadding(0);

			// ...create a header for that table.
			FlexCellFormatter fcf = ft.getFlexCellFormatter();
			ft.setText(0, 0, m_messages.licenseReportReport_StatsCol_Date()    ); fcf.addStyleName(0, 0, "vibe-licenseReportComposite-reportDataStatsHC");
			ft.setText(0, 1, m_messages.licenseReportReport_StatsCol_Local()   ); fcf.addStyleName(0, 1, "vibe-licenseReportComposite-reportDataStatsHC");
			ft.setText(0, 2, m_messages.licenseReportReport_StatsCol_LDAP()    ); fcf.addStyleName(0, 2, "vibe-licenseReportComposite-reportDataStatsHC");
			ft.setText(0, 3, m_messages.licenseReportReport_StatsCol_OpenId()  ); fcf.addStyleName(0, 3, "vibe-licenseReportComposite-reportDataStatsHC");
			ft.setText(0, 4, m_messages.licenseReportReport_StatsCol_OtherExt()  ); fcf.addStyleName(0, 4, "vibe-licenseReportComposite-reportDataStatsHC");
			ft.setText(0, 5, m_messages.licenseReportReport_StatsCol_GuestAccessEnabled()  ); fcf.addStyleName(0, 5, "vibe-licenseReportComposite-reportDataStatsHC");
			ft.setText(0, 6, m_messages.licenseReportReport_StatsCol_365()     ); fcf.addStyleName(0, 6, "vibe-licenseReportComposite-reportDataStatsHC");
			ft.setText(0, 7, m_messages.licenseReportReport_StatsCol_Checksum()); fcf.addStyleName(0, 7, "vibe-licenseReportComposite-reportDataStatsHC");
			ft.getRowFormatter().addStyleName(0, "vibe-licenseReportComposite-reportDataStatsHR");

			// ...scan the license snapshots....
			for (LicenseStatsItem lsi:  licenseStats) {
				// ...and create a row for each.
				int row = ft.getRowCount();
				ft.setText(row, 0, safeS(lsi.getSnapshotDate())                    );
				ft.setText(row, 1, String.valueOf(      lsi.getInternalUserCount())); fcf.setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
				ft.setText(row, 2, String.valueOf(      lsi.getExternalUserCount())); fcf.setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER);
				ft.setText(row, 3, String.valueOf(safeL(lsi.getOpenIdUserCount())) ); fcf.setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
				ft.setText(row, 4, String.valueOf(safeL(lsi.getOtherExtUserCount())) ); fcf.setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
				if (lsi.getGuestAccessEnabled()) {
					ft.setText(row, 5, m_messages.licenseReportGuestAccessEnabledYes() ); fcf.setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
				} else {
					ft.setText(row, 5, m_messages.licenseReportGuestAccessEnabledNo() ); fcf.setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
				}
				ft.setText(row, 6, String.valueOf(safeL(lsi.getActiveUserCount())) ); fcf.setHorizontalAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER);
				ft.setText(row, 7, String.valueOf(      lsi.getCheckSum())         ); fcf.setHorizontalAlignment(row, 7, HasHorizontalAlignment.ALIGN_CENTER);
			}
		}
		
		// Finally, hide any busy spinner that may be showing.
		m_busySpinner.hide();
	}
	
	/**
	 * Resets the reports content.
	 * 
	 * Implements the ReportCompositeBase.resetReport() method.
	 */
	@Override
	public void resetReport() {
		resetReportPanel(false);
	}
	
	/*
	 * Resets the panel holding the output of a report.
	 */
	private void resetReportPanel(boolean visible) {
		// Hide/show the report panel and clear its contents.
		m_reportPanel.setVisible(visible);
		m_reportPanel.clear();
	}
}
