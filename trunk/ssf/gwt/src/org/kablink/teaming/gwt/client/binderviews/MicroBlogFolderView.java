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

import com.google.gwt.user.client.ui.UIObject;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.util.search.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.dom.client.Style.Unit;

/**
 * MicroBlog folder view.
 * 
 * @author drfoster@novell.com
 */
public class MicroBlogFolderView extends DataTableFolderViewBase {
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MicroBlogFolderView(BinderInfo folderInfo, UIObject parent, ViewReady viewReady) {
		// Simply initialize the base class.
		super(folderInfo, parent, viewReady, "vibe-microBlogFolderDataTable");
	}
	
	/**
	 * Resets the columns as appropriate for the micro-blog folder
	 * view.
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
		columnWidths.put(FolderColumn.COLUMN_TITLE,       new ColumnWidth(15));
		columnWidths.put(FolderColumn.COLUMN_DESCRIPTION, new ColumnWidth(85));
	}

	/**
	 * Overrides the DataTableFolderViewBase.adjustFloatColumnWidths() method.
	 * 
	 * @param columnWidths
	 */
	@Override
	protected void adjustFloatColumnWidths(Map<String, ColumnWidth> columnWidths) {
		columnWidths.remove(FolderColumn.COLUMN_TITLE);
		columnWidths.put(   FolderColumn.COLUMN_DESCRIPTION, new ColumnWidth(100, Unit.PCT));
	}

	/**
	 * Called from the base class to complete the construction of this
	 * micro-blog folder view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Setup the appropriate styles for a micro-blog folder,
		// populate the view's contents and tell the base class
		// that we're done with the construction.
		getFlowPanel().addStyleName("vibe-microBlogFolderFlowPanel");
		loadPart1Async();
	}
	
	/**
	 * Loads the MicroBlogFolderView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final UIObject parent, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(MicroBlogFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				MicroBlogFolderView dfView = new MicroBlogFolderView(folderInfo, parent, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_MicroBlogFolderView());
				vClient.onUnavailable();
			}
		});
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
		// In the micro-blog folder view, we never show the bread
		// crumbs, accessories or filter panels but do show the binder
		// owner avatar panel beyond the default.
		boolean reply;
		switch (folderPanel) {
		case BREADCRUMB:
		case ACCESSORIES:
		case FILTER:               reply = false;                            break;
		case BINDER_OWNER_AVATAR:  reply = true;                             break;
		default:                   reply = super.includePanel(folderPanel);  break;
		}
		
		return reply;
	}

	/*
	 * Asynchronously loads the micro-blog folder's content.
	 */
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the micro-blog folder's content.
	 */
	private void loadPart1Now() {
		viewReady();
		populateContent();
	}

	/**
	 * Called from the base class to reset the content of this
	 * micro-blog folder view.
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
	 * micro-blog folder view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Nothing to do.
	}
	
	/**
	 * We don't show an icon for the entry title cells in a micro-blog
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
	
	protected void processFolderColumnsList(List<FolderColumn> folderColumnsList){
		if(folderColumnsList!=null){
			for(FolderColumn fColumn:folderColumnsList){
				if(fColumn.getColumnName()!=null && fColumn.getColumnName().equalsIgnoreCase("title")){
					fColumn.setColumnSortKey("_modificationDate");
				}
				else if(fColumn.getColumnName()!=null && fColumn.getColumnName().equalsIgnoreCase("description")){
					fColumn.setColumnSortKey("description");					
				}
			}
		}
	}
}
