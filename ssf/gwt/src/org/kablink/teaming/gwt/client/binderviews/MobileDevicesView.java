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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.Map;

import com.google.gwt.user.client.ui.UIObject;
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
 * Mobile Devices (per user and system wide) view.
 * 
 * @author drfoster@novell.com
 */
public class MobileDevicesView extends DataTableFolderViewBase {
	public final static boolean	CLICK_ON_ZERO_TO_CREATE_DUMMIES	= false;	//! DRF (20131119):  Leave false on checkin!  DEBUG ONLY:  Setting true will cause a device bubble that represents zero devices to create dummy devices when clicked. 
	public final static boolean	SHOW_MOBILE_DEVICES_SYSTEM		= true;		// Setting false will eliminate the 'Administration Console -> Management -> Mobile Devices' option.
	public final static boolean	SHOW_MOBILE_DEVICES_USER		= true;		// Setting false will disable the manage mobile devices dialog from displaying off the device count bubble in the manage user's view. 
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MobileDevicesView(BinderInfo folderInfo, UIObject parent, ViewReady viewReady) {
		// Initialize the super class.
		super(folderInfo, parent, viewReady, "vibe-mobileDevicesDataTable");
	}
	
	/**
	 * Resets the columns as appropriate for the mobile devices view.
	 * 
	 * Overrides the DataTableFolderViewBase.adjustFixedColumnWidths() method.
	 * 
	 * @param columnWidths
	 */
	@Override
	protected void adjustFixedColumnWidths(Map<String, ColumnWidth> columnWidths) {
		if (isSystemView()) {
			columnWidths.put(FolderColumn.COLUMN_DEVICE_USER,        new ColumnWidth(24));
			columnWidths.put(FolderColumn.COLUMN_DEVICE_DESCRIPTION, new ColumnWidth(76));
		}
		else {
			columnWidths.put(FolderColumn.COLUMN_DEVICE_DESCRIPTION, new ColumnWidth(100));
		}
		columnWidths.put(FolderColumn.COLUMN_DEVICE_LAST_LOGIN,     new ColumnWidth(160, Unit.PX));	// Unless otherwise specified...
		columnWidths.put(FolderColumn.COLUMN_DEVICE_WIPE_SCHEDULED, new ColumnWidth(100, Unit.PX));	// ...the widths default to...
		columnWidths.put(FolderColumn.COLUMN_DEVICE_WIPE_DATE,      new ColumnWidth(160, Unit.PX));	// ...be a percentage value.
	}

	/**
	 * Called from the base class to complete the construction of this
	 * mobile devices view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Setup the appropriate styles for the mobile devices,
		// populate the view's contents and tell the base class that
		// we're done with the construction.
		getFlowPanel().addStyleName("vibe-mobileDevicesFlowPanel");
		viewReady();
		populateContent();
	}
	
	/**
	 * Loads the MobileDevicesView split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final UIObject parent, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(MobileDevicesView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				MobileDevicesView dfView = new MobileDevicesView(folderInfo, parent, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_MobileDevicesView());
				vClient.onUnavailable();
			}
		});
	}
	
	/**
	 * Returns the widget to use for displaying the table empty message.
	 * 
	 * Overrides the DataTableFolderViewBase.getEmptyTableWidget()
	 * method.
	 * 
	 * @return
	 */
	@Override
	protected Widget getEmptyTableWidget() {
		return new EmptyMobileDevicesComposite(isSystemView());
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
		// In the mobile devices view, we don't need the download,
		// description, filter or footer panels. 
		boolean reply;
		switch (folderPanel) {
		case DOWNLOAD:
		case DESCRIPTION:
		case FILTER:
		case FOOTER:  reply = false;                           break;
		default:      reply = super.includePanel(folderPanel); break;
		}

		// If we get here, reply is true if the panel should be shown
		// and false otherwise.
		return reply;
	}

	/*
	 * Returns true if the mobile device view is a system view and
	 * false otherwise.
	 */
	private boolean isSystemView() {
		return getFolderInfo().getMobileDevicesViewSpec().isSystem();
	}
	
	/**
	 * Called from the base class to reset the content of this mobile
	 * devices view.
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
	 * Called from the base class to resize the content of this mobile
	 * devices view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Nothing to do.
	}
}
