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
import org.kablink.teaming.gwt.client.rpc.shared.CreateEmailReportCmd;
import org.kablink.teaming.gwt.client.rpc.shared.EmailReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EmailReportRpcResponseData.EmailItem;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.CreateEmailReportCmd.EmailType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * Composite that runs an email report.
 * 
 * @author drfoster@novell.com
 */
public class EmailReportComposite extends ReportCompositeBase {
	private DateBox						m_beginDateBox;		// The DateBox for selecting the date to begin the report at.
	private DateBox						m_endDateBox;		// The DateBox for selecting the date to end   the report at.
	private EmailReportRpcResponseData	m_emailReport;		// Email report data once read from the server.
	private EmailType					m_emailType;		// The type of report to generate (sent, received, errors, ...)
	private FlexCellFormatter			m_reportTableCF;	// The formatter to setting styles on the report table's <TD>s.
	private RowFormatter				m_reportTableRF;	// The formatter to setting styles on the report table's <TR>s.
	private VibeFlexTable				m_reportTable;		// The <TABLE> containing the report's output.
	private VibeFlowPanel				m_reportPanel;		// The panel containing the report table.

	// Indexes of the columns in the report table.
	private final static int COL_SEND_DATE		= 0;
	private final static int COL_FROM			= 1;
	private final static int COL_TO_ADDRESSES	= 2;
	private final static int COL_LOG_TYPE		= 3;
	private final static int COL_LOG_STATUS		= 4;
	private final static int COL_SUBJECT		= 5;
	private final static int COL_ATTACHED_FILES	= 6;
	private final static int COL_COMMENT		= 7;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};
	
	/**
	 * Constructor method.
	 */
	public EmailReportComposite() {
		// Simply initialize the super class.
		super();
	}

	/*
	 * Builds a Label to use for cells for the 'Attached Files' column.
	 */
	private Label buildFilesLabel(String data) {
		Label reply = new Label();

		// Logic copied from email_report.jsp.
		data = GwtClientHelper.replace(data, "\"", "&quot;");
		data = GwtClientHelper.replace(data, "<",  "&lt;"  );
		data = GwtClientHelper.replace(data, ">",  "&gt;"  );
		data = GwtClientHelper.replace(data, ",",  "<br/>" );
		reply.getElement().setInnerHTML(data);
		
		return reply;
	}
	
	/*
	 * Builds a Label to use for cells for the 'To Addresses' column.
	 */
	private Label buildToLabel(String data) {
		Label reply = new Label();
		
		// Logic copied from email_report.jsp.
		data = GwtClientHelper.replace(data, ",", "<br/>");
		if (data.indexOf('/') < 0) {
			reply.addStyleName("gwtUI_nowrap");
		}
		else {
			data = GwtClientHelper.replace(data, " ", "&nbsp;");
			data = GwtClientHelper.replace(data, "/", " /"    );
		}
		reply.getElement().setInnerHTML(data);
				
		return reply;
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
		InlineLabel il = buildInlineLabel(m_messages.emailReportCaption(), "vibe-reportCompositeBase-caption");
		m_rootContent.add(il);

		// ...add a panel for the report widgets...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-reportCompositeBase-widgetsPanel");
		m_rootContent.add(fp);

		// ...add a horizontal panel for the date selectors...
		HorizontalPanel dates = new HorizontalPanel();
		dates.addStyleName("vibe-emailReportComposite-datesPanel");
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
		il = buildInlineLabel(m_messages.emailReportAndSeparator(), "vibe-reportCompositeBase-andSeparator");
		dates.add(il);
		
		// ...and an ending date selector...
		Date endDate = new Date();
		CalendarUtil.addDaysToDate(endDate, 1);		//Go through the next day so it picks up today's email messages
		m_endDateBox = new DateBox(new DatePicker(), endDate, dateFormatter);
		dates.add(m_endDateBox);

		// ...add a horizontal panel for the types radio buttons...
		HorizontalPanel types = new HorizontalPanel();
		types.addStyleName("vibe-emailReportComposite-typesPanel");
		types.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		fp.add(types);

		// ...add the sent radio button...
		RadioButton	rb = new RadioButton("emailTypes", m_messages.emailReportTypeSent());
		rb.addStyleName("vibe-emailReportComposite-typeRadio");
		rb.setValue(true);
		m_emailType = EmailType.SENT;
		rb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				m_emailType = EmailType.SENT;
			}
		});
		types.add(rb);
		
		// ...add the received radio button...
		rb = new RadioButton("emailTypes", m_messages.emailReportTypeReceived());
		rb.addStyleName("vibe-emailReportComposite-typeRadio");
		rb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				m_emailType = EmailType.RECEIVED;
			}
		});
		types.add(rb);
		
		// ...add the errors radio button...
		rb = new RadioButton("emailTypes", m_messages.emailReportTypeErrors());
		rb.addStyleName("vibe-emailReportComposite-typeRadio");
		rb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				m_emailType = EmailType.ERROR;
			}
		});
		types.add(rb);

		// ...add the 'Run Report' push button...
		Button runReportBtn = new Button(m_messages.emailReportRunReport());
		runReportBtn.addStyleName("vibe-reportCompositeBase-buttonBase vibe-reportCompositeBase-runButton");
		runReportBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createReport();
			}
		});
		fp.add(runReportBtn);
		
		// ...add the panel containing the output of the report...
		m_reportPanel = new VibeFlowPanel();
		m_reportPanel.addStyleName("vibe-emailReportComposite-reportPanel");
		m_reportPanel.setVisible(false);	// Initially hidden, shown once a report is created.
		m_rootContent.add(m_reportPanel);

		// ...and create a table to hold its content.
		m_reportTable = new VibeFlexTable();
		m_reportTable.addStyleName("vibe-emailReportComposite-reportTable");
		m_reportPanel.add(m_reportTable);
		m_reportTable.setCellPadding(2);
		m_reportTable.setCellSpacing(0);
		m_reportTable.setWidth("100%");
		m_reportTableRF = m_reportTable.getRowFormatter();
		m_reportTableCF = m_reportTable.getFlexCellFormatter();
	}

	/*
	 * Creates a report and uploads it into the display.
	 */
	private void createReport() {
		m_busySpinner.center();
		GwtClientHelper.executeCommand(
				new CreateEmailReportCmd(m_beginDateBox.getValue(), m_endDateBox.getValue(), m_emailType),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				m_busySpinner.hide();
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_CreateEmailReport());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				m_emailReport = ((EmailReportRpcResponseData) response.getResponseData());
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
	 * Populates a cell within the report table.
	 */
	private void populateReportCell(int row, int col, EmailItem ei) {
		String data;
		String styles;
		String width;
		Widget w;
		
		switch (col) {
		case COL_ATTACHED_FILES:  data = ei.getAttachedFiles(); styles = "vibe-emailReportComposite-reportTableCell vibe-emailReportComposite-reportTableCell-attachedFiles"; width =  "5%"; w = buildFilesLabel( data    ); break;
		case COL_COMMENT:         data = ei.getComment();       styles = "vibe-emailReportComposite-reportTableCell vibe-emailReportComposite-reportTableCell-errors";        width = "30%"; w = buildInlineLabel(data    ); break;
		case COL_FROM:            data = ei.getFrom();          styles = "vibe-emailReportComposite-reportTableCell vibe-emailReportComposite-reportTableCell-fromAddress";   width =  "5%"; w = buildInlineLabel(data    ); break;
		case COL_LOG_STATUS:      data = ei.getLogStatus();     styles = "vibe-emailReportComposite-reportTableCell vibe-emailReportComposite-reportTableCell-status";        width =  "5%"; w = buildInlineLabel(data    ); break;
		case COL_LOG_TYPE:        data = ei.getLogType();       styles = "vibe-emailReportComposite-reportTableCell vibe-emailReportComposite-reportTableCell-type";          width =  "5%"; w = buildInlineLabel(data    ); break;
		case COL_SEND_DATE:       data = ei.getSendDate();      styles = "vibe-emailReportComposite-reportTableCell vibe-emailReportComposite-reportTableCell-sendDate";      width =  "5%"; w = buildInlineLabel(data    ); break;
		case COL_SUBJECT:         data = ei.getSubject();       styles = "vibe-emailReportComposite-reportTableCell vibe-emailReportComposite-reportTableCell-subject";       width = "35%"; w = buildInlineLabel(data, 80); break;
		case COL_TO_ADDRESSES:    data = ei.getToAddresses();   styles = "vibe-emailReportComposite-reportTableCell vibe-emailReportComposite-reportTableCell-toAddresses";   width = " 5%"; w = buildToLabel(    data    ); break;
		default:
			return;
		}
		
		m_reportTable.setWidget(     row, col, w     );
		m_reportTableCF.addStyleName(row, col, styles);
		m_reportTableCF.setWidth(    row, col, width );
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
		// Reset the report table.
		resetReportTable(true);	// true -> Show the report panel.

		// Are there any items in the report?
		List<EmailItem> eiList = m_emailReport.getEmailItems();
		if (GwtClientHelper.hasItems(eiList)) {
			// Yes!  Scan them...
			for (EmailItem ei:  eiList) {
				// ...creating a row in the table for each.
				int row = m_reportTable.getRowCount();
				populateReportCell(row, COL_SEND_DATE,      ei);
				populateReportCell(row, COL_FROM,           ei);
				populateReportCell(row, COL_TO_ADDRESSES,   ei);
				populateReportCell(row, COL_LOG_STATUS,     ei);
				populateReportCell(row, COL_LOG_TYPE,       ei);
				populateReportCell(row, COL_SUBJECT,        ei);
				populateReportCell(row, COL_ATTACHED_FILES, ei);
				populateReportCell(row, COL_COMMENT,        ei);
			}
		}
		
		else {
			// No, there aren't any items in the report!  Tell the user
			// the report was empty.
			GwtClientHelper.deferredAlert(m_messages.emailReportWarning_NoData());
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
		resetReportTable(false);
	}
	
	/*
	 * Resets the table holding the output of a report.
	 */
	private void resetReportTable(boolean visible) {
		// Hide/show the report panel...
		m_reportPanel.setVisible(visible);

		// ...empty the report table...
		m_reportTable.removeAllRows();

		// ...and if the report panel is visible...
		if (visible) {
			// ...recreate the table's header.
			m_reportTable.setWidget(0,  COL_SEND_DATE,      buildInlineLabel(m_messages.emailReportReportColumn_SendDate(),      "vibe-emailReportComposite-reportTableHeaderCell"));
			m_reportTable.setWidget(0,  COL_FROM,           buildInlineLabel(m_messages.emailReportReportColumn_From(),          "vibe-emailReportComposite-reportTableHeaderCell"));
			m_reportTable.setWidget(0,  COL_TO_ADDRESSES,   buildInlineLabel(m_messages.emailReportReportColumn_ToAddresses(),   "vibe-emailReportComposite-reportTableHeaderCell"));
			m_reportTable.setWidget(0,  COL_LOG_TYPE,       buildInlineLabel(m_messages.emailReportReportColumn_LogType(),       "vibe-emailReportComposite-reportTableHeaderCell"));
			m_reportTable.setWidget(0,  COL_LOG_STATUS,     buildInlineLabel(m_messages.emailReportReportColumn_LogStatus(),     "vibe-emailReportComposite-reportTableHeaderCell"));
			m_reportTable.setWidget(0,  COL_SUBJECT,        buildInlineLabel(m_messages.emailReportReportColumn_Subject(),       "vibe-emailReportComposite-reportTableHeaderCell"));
			m_reportTable.setWidget(0,  COL_ATTACHED_FILES, buildInlineLabel(m_messages.emailReportReportColumn_AttachedFiles(), "vibe-emailReportComposite-reportTableHeaderCell"));
			m_reportTable.setWidget(0,  COL_COMMENT,        buildInlineLabel(m_messages.emailReportReportColumn_Comment(),       "vibe-emailReportComposite-reportTableHeaderCell"));
			m_reportTableRF.addStyleName(0, "vibe-emailReportComposite-reportTableHeaderRow");
		}
	}
}
