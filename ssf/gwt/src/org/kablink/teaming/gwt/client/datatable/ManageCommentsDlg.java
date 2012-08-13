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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.widgets.CommentsWidget;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's 'Manage Comments' dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ManageCommentsDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private CommentsInfo					m_commentsInfo;				// The CommentsInfo the ManageCommentsDlg is running against.
	private GwtTeamingDataTableImageBundle	m_images;					// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private UIObject						m_showRelativeWidget;		// The UIObject to show the dialog relative to.
	private VibeFlowPanel					m_fp;						// The panel that holds the dialog's contents.
	private CommentsWidget					m_commentsWidget;			// Widget that holds all the comments

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
	};

	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends InlineLabel {
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param title
		 */
		public DlgLabel(String label, String title) {
			super(label);
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
			addStyleName("vibe-manageCommentsDlg-label");
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null);
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageCommentsDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		addStyleName("vibe-manageCommentsDlg");
		createAllDlgContent(
			"",		// The dialog's caption will be set each time it is run.
			this,	// The dialog's EditSuccessfulHandler.
			DlgBox.getSimpleCanceledHandler(),
			null);	// Create callback data.  Unused. 
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create a panel to hold the dialog's content...
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-manageCommentsDlg-panel");
		
		m_commentsWidget = new CommentsWidget( false );
		m_commentsWidget.addStyleName( "vibe-manageCommentsDlg-commentsWidget" );
		m_fp.add( m_commentsWidget );
		
		// ...and return the Panel that holds the dialog's contents.
		return m_fp;
	}

	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Unused.
		return true;
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
		return null;
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
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
//!		...this needs to be implemented...
		populateDlgWithDataAsync();
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgWithDataAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgWithDataNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgWithDataNow() {
		m_commentsWidget.init( m_commentsInfo );
		
		// ...and show the dialog.
		showRelativeTo(m_showRelativeWidget);
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
	 * Asynchronously runs the given instance of the manage comments
	 * dialog.
	 */
	private static void runDlgAsync(final ManageCommentsDlg mcDlg, final CommentsInfo ci, final UIObject showRelativeWidget) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				mcDlg.runDlgNow(ci, showRelativeWidget);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the manage comments
	 * dialog.
	 */
	private void runDlgNow(CommentsInfo ci, UIObject showRelativeWidget) {
		// Set the dialog's caption...
		setCaption(ci.getEntityTitle());
		
		// ...store the parameters...
		m_commentsInfo       = ci;
		m_showRelativeWidget = showRelativeWidget;
		
		// ...and start populating the dialog.
		populateDlgAsync();
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
	/* the manage comment dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the manage comments dialog
	 * asynchronously after it loads. 
	 */
	public interface ManageCommentsDlgClient {
		void onSuccess(ManageCommentsDlg mcDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ManageCommentsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ManageCommentsDlgClient mcDlgClient,
			
			// initAndShow parameters,
			final ManageCommentsDlg mcDlg,
			final CommentsInfo ci,
			final UIObject showRelativeWidget) {
		GWT.runAsync(ManageCommentsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ManageCommentsDlg());
				if (null != mcDlgClient) {
					mcDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != mcDlgClient) {
					// Yes!  Create it and return it via the callback.
					ManageCommentsDlg mcDlg = new ManageCommentsDlg();
					mcDlgClient.onSuccess(mcDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(mcDlg, ci, showRelativeWidget);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageCommentsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param mcDlgClient
	 */
	public static void createAsync(ManageCommentsDlgClient mcDlgClient) {
		doAsyncOperation(mcDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the manage comments dialog.
	 * 
	 * @param mcDlg
	 * @param ci
	 */
	public static void initAndShow(ManageCommentsDlg mcDlg, CommentsInfo ci, UIObject showRelativeWidget) {
		doAsyncOperation(null, mcDlg, ci, showRelativeWidget);
	}
}
