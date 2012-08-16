/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.datatable;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.vectomatic.dnd.DropPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's 'add files' using HTML5.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class AddFilesHtml5Popup extends TeamingPopupPanel
	implements
		DragEnterHandler,
		DragLeaveHandler,
		DragOverHandler,
		DropHandler
{
	private BinderInfo						m_folderInfo;				// The folder the add files is running against.
	private DropPanel						m_dndPanel;					// The drag and drop panel that holds the popup's content.
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
	};

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AddFilesHtml5Popup() {
		// Initialize the superclass...
		super(false, true);

		// ...initialize everything else...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the popup's content.
		addStyleName("vibe-addFilesHtml5Popup");
		add(createContent());
	}

	/*
	 * Creates all the controls that make up the popup.
	 */
	private Panel createContent() {
		// Create a panel to hold the popup's content...
		m_dndPanel = new DropPanel();
		m_dndPanel.addStyleName("vibe-addFilesHtml5Popup-dndPanel");

		// ...connect the various drag and drop handles to the popup...
		m_dndPanel.addDragEnterHandler(this);
		m_dndPanel.addDragLeaveHandler(this);
		m_dndPanel.addDragOverHandler( this);
		m_dndPanel.addDropHandler(     this);
		
		// ...and return the panel that holds the content.
		return m_dndPanel;
	}

	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides Widget.onAttach()
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
	 * Overrides Widget.onDetach()
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Called when a something is dragged into the drag and drop popup.
	 * 
	 * Implements the DragEnterHandler.onDragEnter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragEnter(DragEnterEvent event) {
//!		...this needs to be implemented...
	}
	
	/**
	 * Called when a something is dragged out of the drag and drop
	 * popup.
	 * 
	 * Implements the DragEnterHandler.onDragLeave() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragLeave(DragLeaveEvent event) {
//!		...this needs to be implemented...
	}

	/**
	 * Called when a something is being dragged over the drag and drop
	 * popup.
	 * 
	 * Implements the DragEnterHandler.onDragOver() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDragOver(DragOverEvent event) {
//!		...this needs to be implemented...
	}

	/**
	 * Called when a something is dropped on the drag and drop popup.
	 * 
	 * Implements the DragEnterHandler.onDrop() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDrop(DropEvent event) {
//!		...this needs to be implemented...
	}
	
	/*
	 * Asynchronously populates the contents of the popup.
	 */
	private void populatePopupAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populatePopupNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the popup.
	 */
	private void populatePopupNow() {
//!		...this needs to be implemented...

		// ...and show the popup.
		center();
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
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously runs the given instance of the add files popup.
	 */
	private static void runPopupAsync(final AddFilesHtml5Popup afPopup, final BinderInfo fi) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				afPopup.runPopupNow(fi);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the add files popup.
	 */
	private void runPopupNow(BinderInfo fi) {
		// Store the parameter...
		m_folderInfo = fi;
		
		// ...and start populating the popup.
		populatePopupAsync();
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
	/* the add files using HTML5 popup and perform some operation on */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the add files popup
	 * asynchronously after it loads. 
	 */
	public interface AddFilesHtml5PopupClient {
		void onSuccess(AddFilesHtml5Popup afPopup);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the AddFilesHtml5Popup and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final AddFilesHtml5PopupClient afPopupClient,
			
			// initAndShow parameters,
			final AddFilesHtml5Popup afPopup,
			final BinderInfo fi) {
		GWT.runAsync(AddFilesHtml5Popup.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_AddFilesHtml5Popup());
				if (null != afPopupClient) {
					afPopupClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a popup?
				if (null != afPopupClient) {
					// Yes!  Create it and return it via the callback.
					AddFilesHtml5Popup afPopup = new AddFilesHtml5Popup();
					afPopupClient.onSuccess(afPopup);
				}
				
				else {
					// No, it's not a request to create a popup!  It
					// must be a request to run an existing one.  Run
					// it.
					runPopupAsync(afPopup, fi);
				}
			}
		});
	}
	
	/**
	 * Loads the AddFilesHtml5Popup split point and returns an instance
	 * of it via the callback.
	 * 
	 * @param afPopupClient
	 */
	public static void createAsync(AddFilesHtml5PopupClient afPopupClient) {
		doAsyncOperation(afPopupClient, null, null);
	}
	
	/**
	 * Initializes and shows the add files popup.
	 * 
	 * @param afPopup
	 * @param fi
	 */
	public static void initAndShow(AddFilesHtml5Popup afPopup, BinderInfo fi) {
		doAsyncOperation(null, afPopup, fi);
	}
}
