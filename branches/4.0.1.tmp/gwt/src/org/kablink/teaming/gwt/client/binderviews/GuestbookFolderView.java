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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetSignGuestbookUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Guest book folder view.
 * 
 * @author drfoster@novell.com
 */
public class GuestbookFolderView extends DataTableFolderViewBase {
	private String m_signGuestBookUrl;	//
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private GuestbookFolderView(BinderInfo folderInfo, ViewReady viewReady) {
		// Simply initialize the base class.
		super(folderInfo, viewReady, "vibe-guestbookFolderDataTable");
	}
	
	/**
	 * Resets the columns as appropriate for the guest book folder
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
		columnWidths.put(FolderColumn.COLUMN_GUEST,            new ColumnWidth(15));
		columnWidths.put(FolderColumn.COLUMN_TITLE,            new ColumnWidth(20));
		columnWidths.put(FolderColumn.COLUMN_DESCRIPTION_HTML, new ColumnWidth(65));
	}

	/**
	 * Overrides the DataTableFolderViewBase.adjustFloatColumnWidths() method.
	 * 
	 * @param columnWidths
	 */
	@Override
	protected void adjustFloatColumnWidths(Map<String, ColumnWidth> columnWidths) {
		columnWidths.remove(FolderColumn.COLUMN_TITLE);
		columnWidths.put(   FolderColumn.COLUMN_DESCRIPTION_HTML, new ColumnWidth(100, Unit.PCT));
	}

	/**
	 * Called from the base class to complete the construction of this
	 * guest book folder view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView() {
		// Setup the appropriate styles for a guest book folder,
		// populate the view's contents and tell the base class that
		// we're done with the construction.
		getFlowPanel().addStyleName("vibe-guestbookFolderFlowPanel");
		populateContent();
		super.viewReady();
	}
	
	/**
	 * Loads the GuestbookFolderView split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync(final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient) {
		GWT.runAsync(GuestbookFolderView.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				GuestbookFolderView dfView = new GuestbookFolderView(folderInfo, viewReady);
				vClient.onSuccess(dfView);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_GuestbookFolderView());
				vClient.onUnavailable();
			}
		});
	}
	
	/**
	 * Called from the base class to reset the content of this
	 * guest book folder view.
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
	 * guest book folder view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView() {
		// Nothing to do.
	}
	
	/**
	 * We don't show an icon for the entry title cells in a guest book.
	 *
	 * Overrides the DataTableFolderViewBase.showEntryTitleIcon() method.
	 * 
	 * @return
	 */
	@Override
	protected boolean showEntryTitleIcon() {
		return false;
	}
	
	/**
	 * Invokes the sign the guest book UI. 
	 * 
	 * Overrides the DataTableFolderViewBase.signGuestbook()
	 * method.
	 */
	@Override
	public void signGuestbook() {
		// Do we have the URL to launch the signing UI?
		if (null == m_signGuestBookUrl) {
			// No!  Can we get it now?
			GetSignGuestbookUrlCmd cmd = new GetSignGuestbookUrlCmd(getFolderInfo().getBinderIdAsLong());
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						m_messages.rpcFailure_GetSignGuestbookUrl());
				}
	
				@Override
				public void onSuccess(VibeRpcResponse result) {
					StringRpcResponseData responseData = ((StringRpcResponseData) result.getResponseData());
					String url = responseData.getStringValue();
					if (GwtClientHelper.hasString(url)) {
						// Yes!  Launch the signing UI.
						m_signGuestBookUrl = url;
						signGuestbookAsync();
					}
					
					else {
						// No, we couln't get the URL to launch the
						// signer!  Tell the user about the problem.
						GwtClientHelper.debugAlert(m_messages.guestBook_Error_CouldNotGetSigningURL());
					}
				}
			});
		}
		
		else {
			// Yes, we have the URL to launch the signing UI.  Launch
			// it.
			signGuestbookAsync();
		}
	}

	/*
	 * Asynchronously launches the UI to sign the guest book.
	 */
	private void signGuestbookAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				signGuestbookNow();
			}
		});
	}
	
	/*
	 * Synchronously launches the UI to sign the guest book.
	 */
	private void signGuestbookNow() {
		OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
			m_signGuestBookUrl,
			Instigator.GOTO_CONTENT_URL);
		
		if (GwtClientHelper.validateOSBI(osbInfo)) {
			GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
		}
	}
}
