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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetTelemetrySettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TelemetrySettingsRpcResponseData;
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
 * This dialog is used to configure the telemetry settings.
 * 
 * @author drfoster@novell.com
 */
public class ConfigureTelemetryDlg extends DlgBox implements EditSuccessfulHandler {
	public final static boolean	SHOW_CONFIGURE_TELEMETRY = false;	//! DRF (20150805):  Leave false on checkin until it's all working.
	
	private GwtTeamingMessages					m_messages;					// Access to Filr's messages.
	private List<HandlerRegistration>			m_registeredEventHandlers;	//
	@SuppressWarnings("unused")
	private TelemetrySettingsRpcResponseData	m_tsData;					// The current telemetry settings once they're read from the server.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ConfigureTelemetryDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height) {
		// Initialize the super class...
		super(autoHide, modal, xPos, yPos, width, height, DlgButtonMode.OkCancel);

		// ...initialize everything else that requires it...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the header, content and footer of this dialog.
		createAllDlgContent(
			m_messages.configureTelemetryDlg_Header(),
			this,								// The dialog's editSuccessful() handler.
			DlgBox.getSimpleCanceledHandler(),	// The dialog's editCanceled()   handler.
			null);								// null -> No callback data is required by createContent().
	}

	
	/**
	 * Create all the controls that make up the dialog box.
	 * 
	 * Implements the DlgBox.createContent() method.
	 *
	 * @param props
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object props) {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("teamingDlgBoxContent");

//!		...this needs to be implemented...
		
		return mainPanel;
	}

	/**
	 * This gets called when the user presses OK.
	 * 
	 * Implements the EditSuccessful.editSuccessful() method.
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object obj) {
		clearErrorPanel();
		hideErrorPanel();

		// Disable the OK button.
		setOkEnabled(false);

//!		...this needs to be implemented...
		
		// Return false to leave the dialog open.  It will be closed
		// when the dialog's selections have been successfully saved.
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 * 
	 * Implements the DlgBox.getDataFromDlg() method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		return "";	// Unused.
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown.
	 * 
	 * Implements the DlgBox.getFocusWidget() method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}
	
	/**
	 * Returns the HelpData structure for running help for this dialog.
	 * 
	 * Overrides the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("configueTelemetry");
		return helpData;
	}

	/*
	 * Asynchronously loads the next part of the dialog.
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
	 * Synchronously loads the next part of the dialog.
	 */
	private void loadPart1Now() {
		GetTelemetrySettingsCmd cmd = new GetTelemetrySettingsCmd();
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetTelemetrySettings());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				m_tsData = ((TelemetrySettingsRpcResponseData) response.getResponseData());
				populateDlgAsync();
			}
		});
	}
	
	/**
	 * Called when the dialog is attached.
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
	 * Called when the dialog is detached.
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
	 * Asynchronously populates the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the dialog.
	 */
	private void populateDlgNow() {
		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled(true);

//!		...this needs to be implemented...

		show();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we haven't allocated a list to track events we've registered yet...
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
	 * Asynchronously runs the dialog.
	 */
	private static void runDlgAsync(final ConfigureTelemetryDlg ctDlg) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				ctDlg.runDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously runs the dialog.
	 */
	private void runDlgNow() {
		loadPart1Async();
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the configure telemetry dialog and perform some operation on  */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the configure telemetry
	 * dialog asynchronously after it loads. 
	 */
	public interface ConfigureTelemetryDlgClient {
		void onSuccess(ConfigureTelemetryDlg ctDlg);
		void onUnavailable();
	}

	/**
	 * Loads the ConfigureTelemetryDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param ctDlgClient
	 */
	public static void createDlg(
		final boolean						autoHide,		//
		final boolean						modal,			//
		final int							left,			//
		final int							top,			//
		final int							width,			//
		final int							height,			//
		final ConfigureTelemetryDlgClient	ctDlgClient)	//
	{
		GWT.runAsync( ConfigureTelemetryDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ConfigureTelemetryDlg());
				ctDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess() {
				ConfigureTelemetryDlg ctDlg = new ConfigureTelemetryDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height);
				ctDlgClient.onSuccess(ctDlg);
			}
		});
	}
	
	/**
	 * Initializes and shows the dialog.
	 * 
	 * @param ctDlg
	 */
	public static void initAndShow(final ConfigureTelemetryDlg ctDlg) {
		GWT.runAsync( ConfigureTelemetryDlg.class, new RunAsyncCallback() {			
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ConfigureTelemetryDlg());
			}

			@Override
			public void onSuccess() {
				runDlgAsync(ctDlg);
			}
		});
	}
}
