/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.Map;

import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Limit User Visibility view.
 * 
 * @author drfoster@novell.com
 */
public class LimitUserVisibilityView extends DataTableFolderViewBase {
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private LimitUserVisibilityView(ViewReady viewReady) {
		// Simply initialize the base class.
		super(null, viewReady, "vibe-limitUserVisibilityDataTable");
	}
	
	/**
	 * Resets the columns as appropriate for the limit user visibility
	 * view.
	 * 
	 * Unless otherwise specified the widths default to be a percentage
	 * value.  Default sizes as per the JSP page.  See
	 * profile_list.jsp.
	 * 
	 * Overrides the DataTableFolderViewBase.adjustFixedColumnWidths() method.
	 * 
	 * @param columnWidths
	 */
	@Override
	protected void adjustFixedColumnWidths(Map<String, ColumnWidth> columnWidths) {
		columnWidths.put(FolderColumn.COLUMN_ADMINISTRATOR,                 new ColumnWidth(50)         );
		columnWidths.put(FolderColumn.COLUMN_PRINCIPAL_TYPE,                new ColumnWidth(60, Unit.PX));
		columnWidths.put(FolderColumn.COLUMN_CAN_ONLY_SEE_MEMBERS,          new ColumnWidth(25)         );
		columnWidths.put(FolderColumn.COLUMN_CAN_ONLY_SEE_MEMBERS_OVERRIDE, new ColumnWidth(25)         );
	}

	/**
	 * Called from the base class to complete the construction of this
	 * limit user visibility view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Setup the appropriate styles for an limit user visibility
		// view, populate the view's contents and tell the base class
		// that we're done with the construction.
		getFlowPanel().addStyleName("vibe-limitUserVisibilityFlowPanel");
		viewReady();
		populateContent();
	}
	
	/**
	 * Loads the LimitUserVisibilityView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(LimitUserVisibilityView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				LimitUserVisibilityView dfView = new LimitUserVisibilityView(viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_LimitUserVisibilityView());
				vClient.onUnavailable();
			}
		});
	}
	
	/**
	 * Returns the widget to use for displaying the table empty message.
	 * 
	 * Provided as a convenience method.  Class that extend this may
	 * override to provide whatever they want displayed.
	 * 
	 * Overrides the DataTableFolderViewBase.getEmptyTableWidget()
	 * method.
	 * 
	 * @return
	 */
	@Override
	protected Widget getEmptyTableWidget() {
		return new EmptyLimitUserVisibilityComposite();
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
		// In the limit user visibility view, we don't show the filter
		// or footer panel beyond the default.
		boolean reply;
		switch (folderPanel) {
		case BINDER_OWNER_AVATAR:
		case FILTER:
		case FOOTER:  reply = false;                           break;
		default:      reply = super.includePanel(folderPanel); break;
		}

		// If we get here, reply is true if the panel should be shown
		// and false otherwise.
		return reply;
	}

	/**
	 * Called from the base class to reset the content of this
	 * limit user visibility view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView() {
		// Clear any existing content from the view...
		resetContent();
		
		// ...repopulate it...
		populateContent();
		
		// ...and make sure the select column didn't reappear if it
		// ...shouldn't be there.
		validateSelectSupportAsync();
	}
	
	/**
	 * Called from the base class to resize the content of this
	 * limit user visibility view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Nothing to do.
	}
	
	/*
	 * Asynchronously validates whether the select checkboxes in the
	 * data table should stay.  If there's no commands available for
	 * them to be used with, they shouldn't. 
	 */
	private void validateSelectSupportAsync(int delay) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				validateSelectSupportNow();
			}
		},
		delay);
	}

	/*
	 * Asynchronously validates whether the select checkboxes in the
	 * data table should stay.  If there's no commands available for
	 * them to be used with, they shouldn't. 
	 */
	private void validateSelectSupportAsync() {
		validateSelectSupportAsync(ENTRY_MENU_READY_DELAY);
	}
	
	/*
	 * Synchronously validates whether the select checkboxes in the
	 * data table should stay.  If there's no commands available for
	 * them to be used with, they shouldn't. 
	 */
	private void validateSelectSupportNow() {
		// If there's nothing in the entry menu for the user to act
		// on...
		EntryMenuPanel emp = getEntryMenuPanel();
		if ((null == emp) || (0 == emp.getMenuActions())) {
			// ...tell the data table to remove the selection column.
			removeSelectColumn();
		}
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
		
		// ...and validate whether the select checkboxes should appear
		// ...in the data table or not.
		validateSelectSupportAsync();
	}
}
