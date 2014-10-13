/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CreateUserAccessReportCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UserAccessReportRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.UserAccessReportRpcResponseData.UserAccessItem;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

/**
 * Composite that runs an user access report.
 * 
 * @author drfoster@novell.com
 * @param <UserAccessReportRpcResponseData>
 */
public class UserAccessReportComposite extends ReportCompositeBase
	implements
		SearchFindResultsEvent.Handler
{
	private FindCtrl						m_userFinder;		// The FindCtrl to select the user to report on.
	private FlexCellFormatter				m_reportTableCF;	// The formatter to setting styles on the report table's <TD>s.
	private RowFormatter					m_reportTableRF;	// The formatter to setting styles on the report table's <TR>s.
	private VibeFlexTable					m_reportTable;		// The <TABLE> containing the report's output.
	private VibeFlowPanel					m_reportPanel;		// The panel containing the report table.
	private UserAccessReportRpcResponseData	m_userAccessReport;	// User access report data once read from the server.
	
	// Indexes of the columns in the report table.
	private final static int COL_NAME		= 0;
	private final static int COL_TYPE		= 1;
	private final static int COL_PADDING	= 2;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.SEARCH_FIND_RESULTS,
	};
	
	/**
	 * Constructor method.
	 */
	public UserAccessReportComposite() {
		// Simply initialize the super class.
		super();
	}

	/*
	 * Builds the report widget(s) for the name column in the report
	 * table.
	 */
	private Widget buildNameWidget(final UserAccessItem uai) {
		// Can the ACLs be modified on this entity?
		final String entityType = uai.getEntityType();
		boolean canModifyACLs =
			((!m_isFilr)                              &&
			(entityType.equalsIgnoreCase("workspace") ||
			 entityType.equalsIgnoreCase("folder"   ) ||
			 entityType.equalsIgnoreCase("profiles" )));
		
		Widget w;
		String entityPath = uai.getEntityPath();
		if (canModifyACLs) {		
			// Yes!  Construct an anchor to access the modify ACL page
			// on it...
			String url = m_userAccessReport.getModifyACLsUrl();
			url += ("&workAreaId=" + uai.getBinderId() + "&workAreaType=" + entityType);
			
			// ...and create an <A> for it.
			Anchor a = new Anchor();
			a.addStyleName("vibe-userAccessReportComposite-reportTableCell-link");
			a.setText(entityPath);
			a.setHref(url);
			a.setTarget("_blank");
			w = a;
		}
		
		else {
			// No, ACLs can't be modified on this entity!  Include a
			// simple label for its name.
			w = buildInlineLabel(entityPath);
		}
		
		// If we get here, w refers to the widget to display for the
		// item's name column.  Return it.
		return w;
	}
	
	/*
	 * Builds the report widget(s) for the type column in the report
	 * table.
	 */
	private Widget buildTypeWidget(UserAccessItem uai) {
		String typeName;
		String entryType = uai.getEntityType();
		if      (entryType.equalsIgnoreCase("workspace")) typeName = m_messages.userAccessReportObjectType_Workspace();
		else if (entryType.equalsIgnoreCase("folder"))    typeName = m_messages.userAccessReportObjectType_Folder();
		else if (entryType.equalsIgnoreCase("profiles"))  typeName = m_messages.userAccessReportObjectType_Profiles();
		else                                              typeName = m_messages.userAccessReportObjectType_Unknown();		
		return buildInlineLabel(typeName);
	}
	
	/**
	 * Creates the content for the report.
	 * 
	 * Overrides the ReportCompositeBase.createContent() method.
	 */
	@Override
	public void createContent() {
		// Let the super class create the initial base content.
		super.createContent();
		
		// ...add a caption above the content...
		InlineLabel il = buildInlineLabel(m_messages.userAccessReportCaption(), "vibe-reportCompositeBase-caption");
		m_rootContent.add(il);

		// ...add a panel for the report widgets...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-reportCompositeBase-widgetsPanel");
		m_rootContent.add(fp);

		// ...add a hint for the user select widget...
		il = buildInlineLabel(m_messages.userAccessReportUserHint(), "vibe-userAccessReportComposite-label");
		fp.add(il);
		
		// ...add the user select widget...
		final HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("vibe-userAccessReportComposite-userPanel");
		fp.add(hp);
		il = buildInlineLabel(m_messages.userAccessReportUser(), "vibe-userAccessReportComposite-label marginright5px");
		hp.add(                     il                                   );
		hp.setCellVerticalAlignment(il, HasVerticalAlignment.ALIGN_MIDDLE);
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

				m_userFinder.setSearchForExternalPrincipals( true );
				m_userFinder.setSearchForInternalPrincipals( true );

				// ...style it...
				m_userFinder.addStyleName("vibe-userAccessReportComposite-userFind");
				FocusWidget fw = m_userFinder.getFocusWidget();
				if ((null != fw) && (fw instanceof TextBox)) {
					fw.addStyleName("vibe-userAccessReportComposite-userFind");
				}
				
				// ...and add it to the horizontal panel.
				hp.add(                     m_userFinder                                   );
				hp.setCellVerticalAlignment(m_userFinder, HasVerticalAlignment.ALIGN_MIDDLE);
			}
		}); 

		// ...add the panel containing the output of the report...
		m_reportPanel = new VibeFlowPanel();
		m_reportPanel.addStyleName("vibe-userAccessReportComposite-reportPanel");
		m_reportPanel.setVisible(false);	// Initially hidden, shown once a report is created.
		m_rootContent.add(m_reportPanel);

		// ...if we're not running as Filr...
		if (!m_isFilr) {
			// ...add a hind for what's in the report table...
			il = buildInlineLabel(m_messages.userAccessReportObjectsHint(), "vibe-userAccessReportComposite-label");
			m_reportPanel.add(il);
		}

		// ...and create the table to hold its report.
		m_reportTable = new VibeFlexTable();
		m_reportTable.addStyleName("vibe-userAccessReportComposite-reportTable");
		m_reportPanel.add(m_reportTable);
		m_reportTable.setCellPadding(2);
		m_reportTable.setCellSpacing(0);
		m_reportTable.setWidth("100%");
		m_reportTableRF = m_reportTable.getRowFormatter();
		m_reportTableCF = m_reportTable.getFlexCellFormatter();
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
	 * Asynchronously retrieves the access report for the selected
	 * user.
	 */
	private void getReportDataAsync(final GwtUser user) {
		GwtClientHelper.deferCommand(
			new Scheduler.ScheduledCommand() {
				@Override
				public void execute() {
					getReportDataNow(user);
				}
			});
	}
	
	/*
	 * Synchronously retrieves the access report for the selected user.
	 */
	private void getReportDataNow(GwtUser user) {
		m_busySpinner.center();
		GwtClientHelper.executeCommand(
				new CreateUserAccessReportCmd(user.getIdLong()),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_CreateUserAccessReport());
				m_busySpinner.hide();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				m_userAccessReport = ((UserAccessReportRpcResponseData) response.getResponseData());
				populateReportResultsAsync();
			}
		});
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
		m_userFinder.hideSearchResults();
		
		// Are we dealing with a User?
		GwtTeamingItem selectedObj = event.getSearchResults();
		if (selectedObj instanceof GwtUser) {						
			// Yes!  Read their report data.
			getReportDataAsync((GwtUser) selectedObj);
		}
	}
	
	/*
	 * Populates a cell within the report table.
	 */
	private void populateReportCell(int row, int col, UserAccessItem uai) {
		String styles;
		Widget w;
		
		switch (col) {
		case COL_NAME:  styles = "vibe-userAccessReportComposite-reportTableCell vibe-userAccessReportComposite-reportTableCell-name"; w = buildNameWidget(uai); break;
		case COL_TYPE:  styles = "vibe-userAccessReportComposite-reportTableCell vibe-userAccessReportComposite-reportTableCell-type"; w = buildTypeWidget(uai); break;
		default:
			return;
		}
		
		m_reportTable.setWidget(     row, col, w     );
		m_reportTableCF.addStyleName(row, col, styles);
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
		List<UserAccessItem> uaiList = m_userAccessReport.getUserAccessItems();
		if (GwtClientHelper.hasItems(uaiList)) {
			// Yes!  Scan them...
			for (UserAccessItem uai:  uaiList) {
				// ...creating a row in the table for each.
				int row = m_reportTable.getRowCount();
				populateReportCell(row, COL_NAME, uai);
				populateReportCell(row, COL_TYPE, uai);
			}
		}
		
		else {
			// No, there aren't any items in the report!  Tell the user
			// the report was empty.
			GwtClientHelper.deferredAlert(m_messages.userAccessReportWarning_NoData());
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
			m_reportTable.setWidget(     0, COL_NAME,    buildInlineLabel(m_messages.userAccessReportObjectsColName(), "vibe-userAccessReportComposite-reportTableHeaderCell"));
			m_reportTable.setWidget(     0, COL_TYPE,    buildInlineLabel(m_messages.userAccessReportObjectsColType(), "vibe-userAccessReportComposite-reportTableHeaderCell"));
			m_reportTable.setHTML(       0, COL_PADDING, "&nbsp;"                                );
			m_reportTableCF.setWidth(    0, COL_PADDING, "100%"                                  );
			m_reportTableRF.addStyleName(0, "vibe-userAccessReportComposite-reportTableHeaderRow");
		}
		
		else {
			// ...otherwise, ensure the user entry field is blank.
			m_userFinder.clearText();
		}
	}
}
