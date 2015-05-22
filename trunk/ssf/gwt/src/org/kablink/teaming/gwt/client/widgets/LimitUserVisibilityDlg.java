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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetLimitUserVisibilityInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LimitUserVisibilityInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements the Limit User Visibility dialog.
 * 
 * This dialog allows the administrator to define which users can only
 * see members of groups that they're in.
 * 
 * @author drfoster@novell.com
 */
public class LimitUserVisibilityDlg extends DlgBox {
	public static final boolean SHOW_LIMIT_USER_VISIBILITY_DLG	= false;	//! DRF (20150922):  Leave false on checkin until it's all working.
	
	private FlowPanel					m_rootPanel;				//
	private GwtTeamingMessages			m_messages;					//
	private int							m_showX;					//
	private int							m_showY;					//
	@SuppressWarnings("unused")
	private int							m_showCX;					//
	@SuppressWarnings("unused")
	private int							m_showCY;					//
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private LimitUserVisibilityDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height) {
		// Initialize the super class...
		super(
			autoHide,
			modal,
			xPos,
			yPos,
			new Integer(width),
			new Integer(height),
			DlgButtonMode.Close);
		
		// ...initialize anything else the needs it...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.limitUserVisibilityDlg_Header(),
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			null);
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object props) {
		// Create the FlowPanel containing the dialog's content.
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.setStyleName("teamingDlgBoxContent");

//!		...this needs to be implemented...

		// Return the FlowPanel containing the dialog's content.
		return m_rootPanel;
	}
	
	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}
	
	
	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		// Nothing focusable in the dialog.
		return null;
	}
	
	/**
	 * Returns the HelpData for the limit user visibility dialog.
	 * 
	 * Overrides the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("userVisibility");
		return helpData;
	}

	/*
	 * Issue an RPC request to get the limit user visibility
	 * information from the server.
	 */
	private void getUserVisibilityInfoFromServer() {
		// Execute a GWT RPC command asking the server for the limit
		// user visibility information.
		GwtClientHelper.executeCommand(new GetLimitUserVisibilityInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetUserAccessInfo());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				populateDlgWithDataAsync((LimitUserVisibilityInfoRpcResponseData) response.getResponseData());
			}
		});
	}
	
	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					populateDlgNow();
				}
			});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_rootPanel.clear();
		
		// ...and repopulate it with data from the server.
		getUserVisibilityInfoFromServer();
	}

	/*
	 * Asynchronously populates the contents of the dialog with the
	 * given data.
	 */
	private void populateDlgWithDataAsync(final LimitUserVisibilityInfoRpcResponseData userVisibilityData) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					populateDlgWithDataNow(userVisibilityData);
				}
			});
	}
	
	/*
	 * Synchronously populates the contents of the dialog with the
	 * given data.
	 */
	private void populateDlgWithDataNow(final LimitUserVisibilityInfoRpcResponseData userVisibilityData) {
//!		...this needs to be implemented...
		
		// Position and show the dialog.
		setPopupPosition(m_showX, m_showY);
		show();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously runs the given instance of the limit user
	 * visibility dialog.
	 */
	private static void runDlgAsync(final LimitUserVisibilityDlg luvDlg, final int x, final int y, final int width, final int height) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					luvDlg.runDlgNow(x, y, width, height);
				}
			});
	}
	
	/*
	 * Synchronously runs the given instance of the limit user
	 * visibility dialog.
	 */
	private void runDlgNow(int x, int y, int width, int height) {
		// Store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = width;
		m_showCY = height;
		
		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the limit user visibility dialog and perform some operation   */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the 'limit user visibility'
	 * dialog asynchronously after it loads. 
	 */
	public interface LimitUserVisibilityDlgClient {
		void onSuccess(LimitUserVisibilityDlg luvDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the UserPropertiesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the dialog.
			final LimitUserVisibilityDlgClient	luvDlgClient,
			final boolean						autoHide,
			final boolean						modal,
			final int							createX,
			final int							createY,
			final int							createCX,
			final int							createCY,
			
			// Parameters used to initialize and show an instance of the dialog.
			final LimitUserVisibilityDlg		luvDlg,
			final int							initX,
			final int							initY,
			final int							initCX,
			final int							initCY) {
		GWT.runAsync(LimitUserVisibilityDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_LimitUserVisibilityDlg());
				if (null != luvDlgClient) {
					luvDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != luvDlgClient) {
					// Yes!  Create the dialog....
					LimitUserVisibilityDlg luvDlg = new LimitUserVisibilityDlg(
						autoHide,
						modal,
						createX,
						createY,
						createCX,
						createCY);
					
					// ...and return it through the callback.
					luvDlgClient.onSuccess(luvDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(luvDlg, initX, initY, initCX, initCY);
				}
			}
		});
	}
	
	/**
	 * Creates an instance of the LimitUserVisibilityDlg returns it via
	 * the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 * @param luvDlgClient
	 */
	public static void createAsync(final LimitUserVisibilityDlgClient luvDlgClient, final boolean autoHide, final boolean modal, final int x, final int y, final int cx, final int cy) {
		doAsyncOperation(luvDlgClient, autoHide, modal, x, y, cx, cy, null, (-1), (-1), (-1), (-1));
	}
	
	/**
	 * Initializes and shows the limit user visibility dialog.
	 * 
	 * @param luvDlg
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void initAndShow(LimitUserVisibilityDlg luvDlg, int x, int y, int cx, int cy) {
		doAsyncOperation(null, false, false, (-1), (-1), (-1), (-1), luvDlg, x, y, cx, cy);
	}
}
