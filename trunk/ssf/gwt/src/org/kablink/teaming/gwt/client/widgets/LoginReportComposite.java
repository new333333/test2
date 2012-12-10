/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution Login Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/logins/cpal_1.0. The
 * CPAL is based on the Mozilla Public Login Version 1.1 but Sections 14 and 15
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
import java.util.Date;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CreateLoginReportCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

/**
 * Composite that runs a login report.
 * 
 * @author drfoster@novell.com
 */
public class LoginReportComposite extends ReportCompositeBase
	implements
		SearchFindResultsEvent.Handler
{
	private DateBox		m_beginDateBox;		// The DateBox  for selecting the date to begin the report at.
	private DateBox		m_endDateBox;		// The DateBox  for selecting the date to end   the report at.
	private FindCtrl	m_userFinder;		// The FindCtrl to select the users to report on.
	private FormPanel	m_downloadForm;		// The form that will be submitted to download the report.
	private ListBox		m_userList;			// The ListBox  for tracking the selected users.
	private String		m_longReportSort;	// How a long report should be sorted.
	private String		m_reportType;		// Type type of report being requested (long vs. short.)
	private String		m_shortReportSort;	// How a short report should be sorted.

	// The types of reports supported.
	private final static String LONG_REPORT		= "longReport";
	private final static String SHORT_REPORT	= "shortReport";

	// Options for sorting a summary (i.e., short) report.
	private final static String SUMMARY_SORT_NONE	= "none_sort";
	private final static String SUMMARY_SORT_USER	= "user_sort";
	private final static String SUMMARY_SORT_LAST	= "last_login_sort";
	private final static String SUMMARY_SORT_NUMBER	= "login_count_sort";
	
	// Options for sorting an all (i.e., long) report.
	private final static String ALL_SORT_DATE	= "login_date_sort";
	private final static String ALL_SORT_USER	= "user_sort";
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/**
	 * Constructor method.
	 */
	public LoginReportComposite() {
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
		
		// ...add the captions above the content...
		InlineLabel il = buildInlineLabel(m_messages.loginReportCaption1(), "vibe-loginReportComposite-caption");
		m_rootContent.add(il);
		GwtClientHelper.addBR(m_rootContent, 2);
		il = buildInlineLabel(m_messages.loginReportCaption2(), "vibe-loginReportComposite-caption");
		m_rootContent.add(il);

		// ...add a panel for the report widgets...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-loginReportComposite-widgetsPanel");
		m_rootContent.add(fp);

		// ...add a horizontal panel for the date selectors...
		HorizontalPanel dates = new HorizontalPanel();
		dates.addStyleName("vibe-loginReportComposite-datesPanel");
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
		il = buildInlineLabel(m_messages.loginReportAndSeparator(), "vibe-loginReportComposite-andSeparator");
		dates.add(il);
		
		// ...and an ending date selector...
		m_endDateBox = new DateBox(new DatePicker(), new Date(), dateFormatter);
		dates.add(m_endDateBox);

		// ...create the find control for the users...
		final VibeFlexTable ft = new VibeFlexTable();
		ft.addStyleName("vibe-loginReportComposite-peoplePanel");
		ft.setCellPadding(0);
		ft.setCellSpacing(2);
		fp.add(ft);
		ft.setWidget(0, 0, buildInlineLabel(m_messages.loginReportPeople(), "vibe-loginReportComposite-peopleLabel"));
		FindCtrl.createAsync(this, GwtSearchCriteria.SearchType.PERSON, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				// Store the find control...
				m_userFinder = findCtrl;

				// ...style it...
				m_userFinder.addStyleName("vibe-loginReportComposite-peopleFind");
				FocusWidget fw = m_userFinder.getFocusWidget();
				if ((null != fw) && (fw instanceof TextBox)) {
					fw.addStyleName("vibe-loginReportComposite-peopleFind");
				}
				
				// ...and add it to the layout table.
				ft.setWidget(0, 1, m_userFinder);
			}
		}); 

		// ...create a list to holder the users that are selected...
		m_userList = new ListBox(true);
		m_userList.addStyleName("vibe-loginReportComposite-peopleList");
		m_userList.setVisibleItemCount(3);
		ft.setWidget(1, 1, m_userList);
		Button removeBtn = new Button(m_messages.loginReportRemove());
		removeBtn.addStyleName("vibe-loginReportComposite-peopleRemove");
		removeBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int c = m_userList.getItemCount();
				for (int i = (c - 1); i >= 0; i -= 1) {
					if (m_userList.isItemSelected(i)) {
						m_userList.removeItem(i);
					}
				}
			}
		});
		ft.setWidget(                                  1, 2, removeBtn                     );
		ft.getFlexCellFormatter().setVerticalAlignment(1, 2, HasVerticalAlignment.ALIGN_TOP);
		
		// ...create a radio button for reporting summaries...
		RadioButton	rb = new RadioButton("reportType", m_messages.loginReportType_Summaries());
		rb.addStyleName("vibe-loginReportComposite-typeRadio");
		rb.setValue(true);
		m_reportType = SHORT_REPORT;
		rb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				m_reportType = SHORT_REPORT;
			}
		});
		fp.add(rb);

		// ...create a list for how to sort summaries...
		VibeFlowPanel sortPanel = new VibeFlowPanel();
		sortPanel.addStyleName("vibe-loginReportComposite-sortPanel");
		fp.add(sortPanel);
		sortPanel.add(buildInlineLabel(m_messages.loginReportSort(), "vibe-loginReportComposite-sortCap"));
		final ListBox sortSummariesBy = new ListBox(false);
		sortSummariesBy.addStyleName("vibe-loginReportComposite-sortList");
		sortSummariesBy.setVisibleItemCount(1);
		m_shortReportSort = SUMMARY_SORT_NONE;
		sortSummariesBy.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int i = sortSummariesBy.getSelectedIndex();
				if ((-1) != i) {
					m_shortReportSort = sortSummariesBy.getValue(i);
				}
			}
		});
		sortSummariesBy.addItem(m_messages.loginReportSortSummaries_None(),   SUMMARY_SORT_NONE  );
		sortSummariesBy.addItem(m_messages.loginReportSortSummaries_User(),   SUMMARY_SORT_USER  );
		sortSummariesBy.addItem(m_messages.loginReportSortSummaries_Last(),   SUMMARY_SORT_LAST  );
		sortSummariesBy.addItem(m_messages.loginReportSortSummaries_Number(), SUMMARY_SORT_NUMBER);
		sortPanel.add(sortSummariesBy);
		
		// ...create a radio button for reporting everything...
		rb = new RadioButton("reportType", m_messages.loginReportType_All());
		rb.addStyleName("vibe-loginReportComposite-typeRadio");
		rb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				m_reportType = LONG_REPORT;
			}
		});
		fp.add(rb);
		
		// ...create a list for how to sort everything...
		sortPanel = new VibeFlowPanel();
		sortPanel.addStyleName("vibe-loginReportComposite-sortPanel");
		fp.add(sortPanel);
		sortPanel.add(buildInlineLabel(m_messages.loginReportSort(), "vibe-loginReportComposite-sortCap"));
		final ListBox sortAllBy = new ListBox(false);
		sortAllBy.addStyleName("vibe-loginReportComposite-sortList");
		sortAllBy.setVisibleItemCount(1);
		m_longReportSort = ALL_SORT_DATE;
		sortAllBy.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int i = sortAllBy.getSelectedIndex();
				if ((-1) != i) {
					m_longReportSort = sortAllBy.getValue(i);
				}
			}
		});
		sortAllBy.addItem(m_messages.loginReportSortAll_Date(), ALL_SORT_DATE);
		sortAllBy.addItem(m_messages.loginReportSortAll_User(), ALL_SORT_USER);
		sortPanel.add(sortAllBy);
		
		// ...add the 'Run Report' push button...
		Button runReportBtn = new Button(m_messages.loginReportRunReport());
		runReportBtn.addStyleName("vibe-loginReportComposite-runButton");
		runReportBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createReport();
			}
		});
		fp.add(runReportBtn);
		
		// ...and finally, create a form we'll submit to download the
		// ...report with.
		m_downloadForm = new FormPanel();
		m_downloadForm.setMethod(FormPanel.METHOD_POST);
		Hidden h = new Hidden();
		h.setName("forumOkBtn"    );
		h.setID(  "forumOkBtn"    );
		h.setValue("Create Report");
		m_downloadForm.add(h);
		m_rootContent.add(m_downloadForm);
	}
	
	/*
	 * Creates a report and downloads it.
	 */
	private void createReport() {
		// Construct a List<Long> with the IDs of the selected users...
		m_busySpinner.center();
		List<Long> userIds = new ArrayList<Long>();
		int c = m_userList.getItemCount();
		for (int i = 0; i < c; i += 1) {
			userIds.add(Long.parseLong(m_userList.getValue(i)));
		}

		// ...construct the RPC command to create a login report URL...
		CreateLoginReportCmd cmd = new CreateLoginReportCmd(
			m_beginDateBox.getValue(),
			m_endDateBox.getValue(),
			userIds,
			m_reportType,
			m_longReportSort,
			m_shortReportSort);

		// ...and execute the command.
		GwtClientHelper.executeCommand(
				cmd,
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				m_busySpinner.hide();
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_CreateLoginReport());
			}
			
			@Override
			public void onSuccess(final VibeRpcResponse response) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Simply store the URL in the download form
						// and submit it.
						String reportUrl = ((StringRpcResponseData) response.getResponseData()).getStringValue();
						m_downloadForm.setAction(reportUrl);
						m_downloadForm.submit();
					}
				});
				
				// Finally, hide any busy spinner that may be showing.
				m_busySpinner.hide();
			}
		});
	}

	/*
	 * Scans the user list looking for the user in question.  If it's
	 * found, its index in the list is returned.  Otherwise, -1 is
	 * returned.
	 */
	private int findUser(GwtUser user) {
		// Scan the user list.
		int c = m_userList.getItemCount();
		for (int i = 0; i < c; i += 1) {
			// Is this the user in question?
			Long userId = Long.parseLong(m_userList.getValue(i));
			if (userId.equals(user.getIdLong())) {
				// Yes!  Return its index.
				return i;
			}
		}
		
		// If we get here, we didn't find the user in the list.
		// Return -1.
		return (-1);
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
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults(final SearchFindResultsEvent event) {
		// If the find results aren't for this this composite...
		if (!((Widget) event.getSource()).equals(this)) {
			// ...ignore the event.
			return;
		}

		// Process the find results.
		GwtClientHelper.deferCommand(
			new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {					
					// Hide the search results.
					m_userFinder.hideSearchResults();
					m_userFinder.clearText();
					
					// Are we dealing with a User?
					GwtTeamingItem selectedObj = event.getSearchResults();
					if (selectedObj instanceof GwtUser) {						
						// Yes!  Is this user already in the list?
						GwtUser user = ((GwtUser) selectedObj);						
						if ((-1) == findUser(user)) {
							// No!  Add the users to our list of users.
							m_userList.addItem(
								user.getTitle(),
								String.valueOf(
									user.getIdLong()));
						}
						
						else {
							// Yes, tell the user
							GwtClientHelper.deferredAlert(
								m_messages.loginReportWarning_UserAlreadySelected(
									user.getTitle()));
						}
					}
				}
			});
	}
	
	/**
	 * Resets the reports content.
	 * 
	 * Implements the ReportCompositeBase.resetReport() method.
	 */
	@Override
	public void resetReport() {
		// Nothing to do.
	}
}
