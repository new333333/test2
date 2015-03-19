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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.util.BinderInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.cellview.client.AbstractCellTable;
import com.google.gwt.user.client.Window;

/**
 * Milestone folder view.
 * 
 * @author drfoster@novell.com
 */
public class MilestoneFolderView extends DataTableFolderViewBase {
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MilestoneFolderView(BinderInfo folderInfo, ViewReady viewReady) {
		// Simply initialize the base class.
		super(folderInfo, viewReady, "vibe-milestoneFolderDataTable");
	}
	
	/**
	 * Resets the columns as appropriate for the milestone folder view.
	 * 
	 * Unless otherwise specified the widths default to be a percentage
	 * value.
	 * 
	 * Overrides the DataTableFolderViewBase.adjustFixedColumnWidths() method.
	 * 
	 * @param columnWidths
	 */
	@Override
	protected void adjustFixedColumnWidths(Map<String, ColumnWidth> columnWidths) {
		columnWidths.put(FolderColumn.COLUMN_TITLE,       new ColumnWidth(45));
		columnWidths.put(FolderColumn.COLUMN_RESPONSIBLE, new ColumnWidth(25));
		columnWidths.put(FolderColumn.COLUMN_TASKS,       new ColumnWidth(25));
		columnWidths.put(FolderColumn.COLUMN_STATUS,      new ColumnWidth( 5));
	}

	/**
	 * Called from the base class to complete the construction of this
	 * milestone folder view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Setup the appropriate styles for a milestone folder,
		// populate the view's contents and tell the base class that
		// we're done with the construction.
		getFlowPanel().addStyleName("vibe-milestoneFolderFlowPanel");
		populateContent();
		super.viewReady();
	}
	
	/**
	 * Loads the MilestoneFolderView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(MilestoneFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				MilestoneFolderView dfView = new MilestoneFolderView(folderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_MilestoneFolderView());
				vClient.onUnavailable();
			}
		});
	}
	
	/**
	 * Scans the rows looking for overdue due dates and adds the
	 * appropriate styling to the cells.
	 * 
	 * Overrides the DataTableFolderViewBase.postProcessRowData() method.
	 * 
	 * @param columnWidths
	 */
	@Override
	protected void postProcessRowData(final List<FolderRow> folderRows, final List<FolderColumn> folderColumns) {
		// If there aren't any rows...
		int rowCount = ((null == folderRows) ? 0 : folderRows.size());
		if (0 == rowCount) {
			// ...there's nothing to process.  Bail.
			return;
		}
		
		// If there aren't any columns...
		if ((null == folderColumns) || folderColumns.isEmpty()) {
			// ...there's nothing to process.  Bail.
			return;
		}

		// Scan the rows...
		AbstractCellTable<FolderRow> dt = getDataTable();
		for (int rowIndex = 0; rowIndex < rowCount; rowIndex += 1) {
			// ...an scan the columns.
			FolderRow fr = folderRows.get(rowIndex);
			for (FolderColumn fc:  folderColumns) {
				// Is this an overdue date column?
				Boolean overdueDate = fr.getColumnOverdueDate(fc);
				if ((null != overdueDate) && overdueDate) {
					// Yes!  Add the milestone overdue style to the
					// cell.
					TableRowElement  tr = dt.getRowElement(rowIndex);
					TableCellElement td = tr.getCells().getItem(fc.getDisplayIndex());
					td.addClassName("vibe-milestoneFolderOverdue");
				}
			}
		}
	}
	
	/**
	 * Called from the base class to reset the content of this
	 * milestone folder view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView() {
		// Clear any existing content from the view and repopulate it.
		resetContent();
		populateContent();
	}
	
	/**
	 * Called from the base class to resize the content of this
	 * milestone folder view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Nothing to do.
	}
	
	/**
	 * We don't show an icon for the entry title cells in a milestone
	 * folder.
	 *
	 * Overrides the DataTableFolderViewBase.showEntryTitleIcon() method.
	 * 
	 * @return
	 */
	@Override
	protected boolean showEntryTitleIcon() {
		return false;
	}
}
