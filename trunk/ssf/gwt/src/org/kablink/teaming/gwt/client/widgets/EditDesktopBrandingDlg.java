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

import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GwtDesktopBrandingRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements the 'Edit Desktop Branding' dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class EditDesktopBrandingDlg extends DlgBox
	implements
		// Event handlers implemented by this class.
		AdministrationExitEvent.Handler
{
	private GwtDesktopBrandingRpcResponseData	m_desktopBrandingData;		//
	private GwtTeamingMessages					m_messages;					// Access to Vibe's messages.
	private int									m_showCX;					// The cx and...
	private int									m_showCY;					// ...cy size the dialog.
	private int									m_showX;					// The x and...
	private int									m_showY;					// ...y position to show the dialog.
	private List<HandlerRegistration>			m_registeredEventHandlers;	// Event handlers that are currently registered.
	private VibeFlowPanel						m_rootPanel;				// The panel that holds the dialog's contents.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.ADMINISTRATION_EXIT,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditDesktopBrandingDlg(EditDesktopBrandingDlgClient edbDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		// Initialize the superclass...
		super(
			autoHide,
			modal,
			x, y, cx, cy,
			DlgButtonMode.Close,
			false);	// false -> Don't use overflow auto on the content.

		// ...store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = cx;
		m_showCY = cy;
		
		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		addStyleName("vibe-editDesktopBrandingDlg");
		createAllDlgContent(
			m_messages.editDesktopBrandingDlgCaption(),
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			edbDlgClient); 
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
	public Panel createContent(Object callbackData) {
		// Create the main panel that will hold the dialog's content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-editDesktopBrandingDlg-rootPanel");
		
//!		...this needs to be implemented...

		// ...and return it.  Note that it will get populated during
		// ...the initAndShow() call.
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
	 * Returns a HelpData object for the dialog's help.
	 * 
	 * Overrides the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("edit_desktop_branding");
		return helpData;
	}

	/**
	 * Handles AdministrationExitEvent's received by this class.
	 * 
	 * Implements the AdministrationExitEvent.Handler.onAdministrationExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationExit(AdministrationExitEvent event) {
		// If the administration console is exited, simply close the
		// dialog.
		hide();
	}
	
	/**
	 * Called when the dialog is attached to the document.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Attach the widget and register the event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the dialog is detached from the document.
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

//!		...this needs to be implemented...
		
		// ...and position and show the dialog.
		setPixelSize(    m_showCX, m_showCY);
		setPopupPosition(m_showX,  m_showY );
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
	 * Asynchronously runs the given instance of the edit desktop
	 * branding dialog.
	 */
	private static void runDlgAsync(final EditDesktopBrandingDlg edbDlg, final GwtDesktopBrandingRpcResponseData desktopBrandingData, final int x, final int y, final int cx, final int cy) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					edbDlg.runDlgNow(desktopBrandingData, x, y, cx, cy);
				}
			});
	}
	
	/*
	 * Synchronously runs the given instance of the edit desktop
	 * branding dialog.
	 */
	private void runDlgNow(GwtDesktopBrandingRpcResponseData desktopBrandingData, int x, int y, int cx, int cy) {
		// Store the parameters...
		m_desktopBrandingData = desktopBrandingData;
		m_showX               = x;
		m_showY               = y;
		m_showCX              = cx;
		m_showCY              = cy;
		
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
	/* the edit desktop branding dialog and perform some operation   */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the edit desktop branding
	 * dialog asynchronously after it loads. 
	 */
	public interface EditDesktopBrandingDlgClient {
		void onSuccess(EditDesktopBrandingDlg edbDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the EditDesktopBrandingDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the dialog.
			final EditDesktopBrandingDlgClient	edbDlgClient,
			final boolean				autoHide,
			final boolean				modal,
			final int					createX,
			final int					createY,
			final int					createCX,
			final int					createCY,
			
			// Parameters used to initialize and show an instance of the dialog.
			final EditDesktopBrandingDlg			edbDlg,
			final GwtDesktopBrandingRpcResponseData	desktopBrandingData,
			final int								initX,
			final int								initY,
			final int								initCX,
			final int								initCY) {
		GWT.runAsync(EditDesktopBrandingDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_EditDesktopBrandingDlg());
				if (null != edbDlgClient) {
					edbDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != edbDlgClient) {
					// Yes!  Create the dialog.  Note that its
					// construction flow will call the appropriate
					// method off the EditDesktopBrandingDlgClient object.
					EditDesktopBrandingDlg edbDlg = new EditDesktopBrandingDlg(edbDlgClient, autoHide, modal, createX, createY, createCX, createCY);
					edbDlgClient.onSuccess(edbDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(edbDlg, desktopBrandingData, initX, initY, initCX, initCY);
				}
			}
		});
	}
	
	/**
	 * Loads the EditDesktopBrandingDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param edbDlgClient
	 * @param autoHide
	 * @param modal
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void createAsync(EditDesktopBrandingDlgClient edbDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		doAsyncOperation(edbDlgClient, autoHide, modal, x, y, cx, cy, null, null, (-1), (-1), (-1), (-1));
	}
	
	/**
	 * Initializes and shows the edit desktop branding dialog.
	 * 
	 * @param edbDlg
	 * @param x
	 * @param y
	 */
	public static void initAndShow(EditDesktopBrandingDlg edbDlg, GwtDesktopBrandingRpcResponseData desktopBrandingData, int x, int y, int cx, int cy) {
		doAsyncOperation(null, false, false, (-1), (-1), (-1), (-1), edbDlg, desktopBrandingData, x, y, cx, cy);
	}
}
