/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

//! import java.util.Comparator;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.datatable.VibeDataTable;
import org.kablink.teaming.gwt.client.util.BinderInfo;
//! import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.DefaultSelectionEventManager;

/**
 * Discussion folder view.
 * 
 * @author drfoster@novell.com
 */
public class DiscussionFolderView extends DataTableFolderViewBase {
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	public DiscussionFolderView(BinderInfo folderInfo, ViewReady viewReady) {
		// Simply initialize the base class.
		super(folderInfo, viewReady);
	}
	
	/**
	 * Callback interface used to interact with a discussion folder
	 * view asynchronously after it loads. 
	 */
	public interface DiscussionFolderViewClient {
		void onSuccess(DiscussionFolderView dfView);
		void onUnavailable();
	}

	/**
	 * Called from the base class to complete the construction of this
	 * discussion folder view.
	 * 
	 * Implements DataTableFolderViewBase.constructView().
	 * 
	 * @param folderColumnsList
	 * @param folderSortBy
	 * @param folderSortDescend
	 * @param folderPageSize
	 */
	@Override
	public void constructView(List<FolderColumn> folderColumnsList, String folderSortBy, boolean folderSortDescend, int folderPageSize) {
		// Setup the appropriate styles for a discussion folder...
		getFlowPanel().addStyleName("vibe-discussionFolderFlowPanel");

		// ...populate the view's content...
		populateView(folderColumnsList, folderSortBy, folderSortDescend, folderPageSize);
		
		// ...and tell the base class that we're done with the
		// ...construction.
		super.viewReady();
	}
	
	/**
	 * Loads the DiscussionFolderView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param dfvClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final DiscussionFolderViewClient dfvClient) {
		GWT.runAsync(DiscussionFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				DiscussionFolderView dfView = new DiscussionFolderView(folderInfo, viewReady);
				dfvClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_DiscussionFolderView());
				dfvClient.onUnavailable();
			}
		});
	}
	
	/*
	 * Add the columns to the table.
	 */
	private void initTableColumns(VibeDataTable<FolderRow> vdt, List<FolderColumn> folderColumnsList, final FolderRowSelectionModel selectionModel, FolderRowAsyncHandler sortHandler) {
		// Add a column for a checkbox selector.
	    Column<FolderRow, Boolean> cc =
	        new Column<FolderRow, Boolean>(new CheckboxCell(true, false)) {
	          @Override
	          public Boolean getValue(FolderRow object) {
	            return selectionModel.isSelected(object);
	          }
	        };
	    vdt.addColumn(cc, SafeHtmlUtils.fromSafeConstant("<br/>"));

	    // Scan the columns defined in this folder.
		for (final FolderColumn fc:  folderColumnsList) {
			// Define a TextColumn for each column...
			TextColumn<FolderRow> tc = new TextColumn<FolderRow>() {
				@Override
				public String getValue(FolderRow fr) {
					return fr.getColumnValue(fc);
				}
			};
			
			// ...that's can be sorted...
			tc.setSortable(true);
//!			...this needs to be implemented...
/*
			sortHandler.setComparator(tc, new Comparator<FolderRow>() {
				@Override
				public int compare(FolderRow o1, FolderRow o2) {
					return
						GwtClientHelper.jsStringCompare(
							o1.getColumnValue(fc),
							o2.getColumnValue(fc));
				}
			});
*/
			// ...and add it to the table.
			vdt.addColumn(tc, fc.getColumnTitle());
		}
	}
	  
	/**
	 * Called from the base class to reset the content of this
	 * discussion folder view.
	 * 
	 * Implements DataTableFolderViewBase.resetView().
	 * 
	 * @param folderColumnsList
	 * @param folderSortBy
	 * @param folderSortDescend
	 * @param folderPageSize
	 */
	@Override
	public void resetView(List<FolderColumn> folderColumnsList, String folderSortBy, boolean folderSortDescend, int folderPageSize) {
		// Clear any existing content from the view and repopulate it.
		resetContent();
		populateView(folderColumnsList, folderSortBy, folderSortDescend, folderPageSize);
	}
	
	/*
	 * Called from the base class to reset the content of this
	 * discussion folder view.
	 */
	private void populateView(List<FolderColumn> folderColumnsList, String folderSortBy, boolean folderSortDescend, int folderPageSize) {
		// Create a key provider that will provide a unique key for
		// each row.
		FolderRowKeyProvider keyProvider = new FolderRowKeyProvider();
		
		// Create the table.
		VibeDataTable<FolderRow> vdt = new VibeDataTable<FolderRow>(folderPageSize, keyProvider);
		vdt.setWidth("100%");
		
		// Set a message to display when the table is empty.
		vdt.setEmptyTableWidget(new Label(m_messages.discussionFolder_Empty()));

		// Attach a sort handler to sort the list.
	    FolderRowAsyncHandler sortHandler = new FolderRowAsyncHandler(vdt);
	    vdt.addColumnSortHandler(sortHandler);
		
		// Create a pager that lets the user page through the table.
	    FolderRowPager pager = new FolderRowPager();
	    pager.setDisplay(vdt);

	    // Add a selection model so the user can select cells.
	    final FolderRowSelectionModel selectionModel = new FolderRowSelectionModel(keyProvider);
	    vdt.setSelectionModel(selectionModel, DefaultSelectionEventManager.<FolderRow> createCheckboxManager());

	    // Initialize the table's columns.
	    initTableColumns(vdt, folderColumnsList, selectionModel, sortHandler);
	    
	    // Add the provider that supplies FolderRow's for the table.
		FolderRowProvider folderRowProvider = new FolderRowProvider(vdt, keyProvider);
	    folderRowProvider.addDataDisplay(vdt);

	    // Finally, add the table to the view.
		getFlowPanel().add(vdt);
		
//!		...this needs to be implemented...
		VibeVerticalPanel vp = new VibeVerticalPanel();
		vp.add(new HTML("<br/>- - - - - Start:  Folder Display Data - - - - -<br/><br/>"));
		vp.add(new InlineLabel("Sort by:  "         + folderSortBy));
		vp.add(new InlineLabel("Sort descending:  " + folderSortDescend));
		vp.add(new InlineLabel("Page size:  "       + folderPageSize));
		vp.add(new HTML("<br/>"));
		for (FolderColumn fc:  folderColumnsList) {
			vp.add(new InlineLabel(fc.getColumnName() + "='" + fc.getColumnTitle() + "'"));
		}
		vp.add(new HTML("<br/>- - - - - End:  Folder Display Data - - - - -<br/>"));
		getFlowPanel().add(vp);
	}
}
