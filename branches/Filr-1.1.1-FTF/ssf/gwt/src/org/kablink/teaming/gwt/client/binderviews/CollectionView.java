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

import java.util.Map;

import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.util.BinderInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Collection view.
 * 
 * @author drfoster@novell.com
 */
public class CollectionView extends DataTableFolderViewBase {
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CollectionView(BinderInfo folderInfo, ViewReady viewReady) {
		// Simply initialize the base class.
		super(folderInfo, viewReady, "vibe-collectionDataTable");
	}
	
	/**
	 * Resets the columns as appropriate for the collection view.
	 * 
	 * Unless otherwise specified the widths default to be a percentage
	 * value.
	 * 
	 * Note:  We use the default select width (33px) and the default
	 *        comment width (70px) as defined in
	 *        DataTableFolderViewBase.initDataMembersFixed().
	 * 
	 * Overrides the DataTableFolderViewBase.adjustFixedColumnWidths() method.
	 * 
	 * @param columnWidths
	 */
	@Override
	protected void adjustFixedColumnWidths(Map<String, ColumnWidth> columnWidths) {
		switch (getFolderInfo().getCollectionType()) {
		case MY_FILES:
			columnWidths.put(FolderColumn.COLUMN_FAMILY, new ColumnWidth(20));
			break;
			
		case NET_FOLDERS:
			columnWidths.put(FolderColumn.COLUMN_NETFOLDER_ACCESS, new ColumnWidth(10));	// = 10
			columnWidths.put(FolderColumn.COLUMN_DESCRIPTION_HTML, new ColumnWidth(90));	// = 100%			
			break;
			
		case SHARED_BY_ME:
		case SHARED_WITH_ME:
		case SHARED_PUBLIC:
			columnWidths.put(FolderColumn.COLUMN_SHARE_DATE,        new ColumnWidth(160, Unit.PX));
			columnWidths.put(FolderColumn.COLUMN_SHARE_EXPIRATION,  new ColumnWidth(160, Unit.PX));
			columnWidths.put(FolderColumn.COLUMN_SHARE_SHARED_BY,   new ColumnWidth(180, Unit.PX));
			columnWidths.put(FolderColumn.COLUMN_SHARE_SHARED_WITH, new ColumnWidth(180, Unit.PX));
			columnWidths.put(FolderColumn.COLUMN_SHARE_ACCESS,      new ColumnWidth(180, Unit.PX));
			columnWidths.put(FolderColumn.COLUMN_SHARE_MESSAGE,     new ColumnWidth(100         ));			
			break;
		
		default:
			// No changes for the other collection types.
			break;
		}
	}

	/**
	 * Called from the base class to complete the construction of this
	 * collection view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Setup the appropriate styles for a collection view,
		// populate the view's contents and tell the base class
		// that we're done with the construction.
		getFlowPanel().addStyleName("vibe-collectionFlowPanel");
		populateContent();
		super.viewReady();
	}
	
	/**
	 * Loads the CollectionView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(CollectionView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				CollectionView dfView = new CollectionView(folderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_CollectionView());
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
		return new EmptyCollectionComposite(getFolderInfo().getCollectionType());
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
		// In the collection view, the only panels we need are the
		// bread crumbs, download and entry menu.
		boolean reply;
		switch (folderPanel) {
		case BREADCRUMB:
		case DOWNLOAD:
		case ENTRY_MENU:  reply = true;   break;
		default:          reply = false;  break;
		}
		
		return reply;
	}

	/**
	 * Called from the base class to reset the content of this
	 * collection view.
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
	 * collection view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Nothing to do.
	}
}
