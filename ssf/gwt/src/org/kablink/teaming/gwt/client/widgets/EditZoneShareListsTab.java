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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetShareListsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveShareListsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShareListsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtShareLists;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This composite is used to set the zone share whitelist/blacklist,
 * i.e., which external email addresses/domains can/cannot be shared with.
 * 
 * @author drfoster@novell.com
 */
public class EditZoneShareListsTab extends EditZoneShareTabBase {
	@SuppressWarnings("unused")
	private EditZoneShareSettingsDlg	m_shareDlg;					//
	private List<HandlerRegistration>	m_registeredEventHandlers;	//
	private GwtShareLists				m_shareLists;				//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};

	
	/**
	 * Constructor method. 
	 */
	public EditZoneShareListsTab(EditZoneShareSettingsDlg shareDlg) {
		// Initialize the super class...
		super();
		
		// ...save the parameter...
		m_shareDlg = shareDlg;
		
		// ...and create the of the tab.
		initWidget(createContent());
	}

	/**
	 * Called to allow the tab to initialize.
	 * 
	 * Implements the EditZoneShareTabBase.init() method.
	 */
	@Override
	public void init() {
		// Simply continue loading the tab.
		loadPart1Async();
	}

	/*
	 * Asynchronously loads the next part of the tab.
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
	 * Synchronously loads the next part of the tab.
	 * 
	 * Sends an RPC request to the server for a GwtShareLists object
	 * and uses it to complete the initialization of the tab.
	 */
	private void loadPart1Now() {
		GwtClientHelper.executeCommand(new GetShareListsCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_GetShareLists() );
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				m_shareLists = ((ShareListsRpcResponseData) result.getResponseData()).getShareLists();
				initFromShareListsAsync();
			}
		});
	}
	
	/*
	 * Asynchronously initializes the dialog's contents from a
	 * GwtShareLists object.
	 */
	private void initFromShareListsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				initFromShareListsNow();
			}
		});
	}

	/*
	 * Synchronously initializes the dialog's contents from a
	 * GwtShareLists object.
	 */
	private void initFromShareListsNow() {
//!		...this needs to be implemented...
	}

	/**
	 * Called if the user cancels the dialog.
	 * 
	 * Implements the EditZoneShareTabBase.cancel() method.
	 */
	@Override
	public void cancel(EditZoneShareTabCallback callback) {
		// We always allow the tab to be canceled.
		callback.success();
	}

	/**
	 * Create all the controls that make up the tab.
	 */
	public Panel createContent() {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(new InlineLabel("...this needs to be implemented..."));
		
//!		...this needs to be implemented...
		return mainPanel;
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
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
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
	
	/**
	 * Called if the user OKs the dialog.
	 * 
	 * Implements the EditZoneShareTabBase.save() method.
	 */
	@Override
	public void save(EditZoneShareTabCallback callback) {
		// Save the share lists.  This will call the appropriate
		// callback method when the request completes.
		saveShareListsAsync(callback);
	}
	
	/*
	 * Asynchronously saves the share lists via a GWT RPC.
	 */
	private void saveShareListsAsync(final EditZoneShareTabCallback callback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				saveShareListsNow(callback);
			}
		});
	}
	
	/*
	 * Synchronously saves the share lists via a GWT RPC.
	 */
	private void saveShareListsNow(final EditZoneShareTabCallback callback) {
		GwtClientHelper.executeCommand(new SaveShareListsCmd(m_shareLists), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SaveShareLists() );
				
				callback.failure();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				callback.success();
			}
		});
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
}
