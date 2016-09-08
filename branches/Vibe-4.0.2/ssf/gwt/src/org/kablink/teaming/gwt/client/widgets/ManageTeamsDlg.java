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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.TeamWorkspacesView;
import org.kablink.teaming.gwt.client.binderviews.TrashView;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewBase.ViewClient;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.CheckManageDlgActiveEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetManageTitleEvent;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetManageTeamsInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ManageTeamsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements the 'Manage Teams' dialog.
 *  
 * @author drfoster@novell.com
 */
public class ManageTeamsDlg extends DlgBox
	implements ViewReady,
		// Event handlers implemented by this class.
		AdministrationExitEvent.Handler,
		CheckManageDlgActiveEvent.Handler,
		FullUIReloadEvent.Handler,
		GetManageTitleEvent.Handler
{
	private boolean							m_dlgAttached;				// true when the dialog is attached to the document.        false otherwise.
	private boolean							m_trashView;				// true if we're viewing the trash on the team workspaces.  false otherwise.
	private boolean							m_viewReady;				// true once the embedded view is ready.                    false otherwise.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private int								m_dlgHeightAdjust = (-1);	// Calculated the first time the dialog is shown.
	private int								m_showX;					// The x and...
	private int								m_showY;					// ...y position and...
	private int								m_showCX;					// ...width and...
	private int								m_showCY;					// ...height of the dialog.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ManageTeamsInfoRpcResponseData	m_manageTeamsInfo;			// Information necessary to run the manage teams dialog.
	private TeamWorkspacesView				m_twsView;					// The team workspace       view.
	private TrashView						m_twsTrashView;				// The team workspace trash view.
	private VibeFlowPanel					m_rootPanel;				// The panel that holds the dialog's contents.

	// Constant adjustments to the size of the view so that it properly
	// fits the dialog's content area.
	private final static int DIALOG_HEIGHT_ADJUST	= 35;
	private final static int DIALOG_WIDTH_ADJUST	= 20;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.ADMINISTRATION_EXIT,
		TeamingEvents.CHECK_MANAGE_DLG_ACTIVE,
		TeamingEvents.FULL_UI_RELOAD,
		TeamingEvents.GET_MANAGE_TITLE,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageTeamsDlg(ManageTeamsDlgClient mtDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		// Initialize the superclass...
		super(
			autoHide,
			modal,
			x, y, cx, cy,
			DlgButtonMode.Close,
			false );

		// ...store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = cx;
		m_showCY = cy;
		
		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		addStyleName("vibe-manageTeamsDlg");
		createAllDlgContent(
			m_messages.manageTeamsDlgCaption(),
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			mtDlgClient); 
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
		// Can we get the information necessary to manage teams?
		final ManageTeamsDlg		mtDlg       = this;
		final ManageTeamsDlgClient	mtDlgClient = ((ManageTeamsDlgClient) callbackData);
		GwtClientHelper.executeCommand(new GetManageTeamsInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetManageTeamsInfo());

				// ...and tell the caller that the dialog will be
				// ...unavailable.
				mtDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Yes!  Store it and tell the caller that the dialog
				// is available.
				m_manageTeamsInfo = ((ManageTeamsInfoRpcResponseData) result.getResponseData());
				mtDlgClient.onSuccess(mtDlg);
			}
		});
		
		// Create the main panel that will hold the dialog's content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-manageTeamsDlg-rootPanel");
		
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
	 * Returns the HelpData for the manage teams dialog.
	 * 
	 * Overrides the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("teams");
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
	 * Called when the manage teams dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Attach the widget and register the event handlers...
		super.onAttach();
		registerEvents();

		// ...and set the views size if its ready.
		m_dlgAttached = true;
		setViewSizeIfReady();
	}
	
	/**
	 * Handles CheckManageDlgActiveEvent's received by this class.
	 * 
	 * Implements the CheckManageDlgActiveEvent.Handler.onCheckManageDlgActive() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCheckManageDlgActive(CheckManageDlgActiveEvent event) {
		event.getManageDlgActiveCallback().manageDlgActive(true);
	}

	/**
	 * Called when the manage teams dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers...
		super.onDetach();
		unregisterEvents();
		
		// ...and mark the dialog as being detached.
		m_dlgAttached = false;
	}

	/**
	 * Handles FullUIReloadEvent's received by this class.
	 * 
	 * Implements the FullUIReloadEvent.Handler.onFullUIReload() method.
	 * 
	 * @param event
	 */
	@Override
	public void onFullUIReload(FullUIReloadEvent event) {
		// Tell whatever view we've got to reload.
		if      (null != m_twsView)      m_twsView.resetView();
		else if (null != m_twsTrashView) m_twsTrashView.resetView();
	}
	
	/**
	 * Handles GetManageTitleEvent's received by this class.
	 * 
	 * Implements the GetManageTitleEvent.Handler.onGetManageTitle() method.
	 * 
	 * @param event
	 */
	@Override
	public void onGetManageTitle(GetManageTitleEvent event) {
		// If this event is targeted to this dialog...
		if (event.getBinderInfo().isEqual(m_manageTeamsInfo.getTeamsRootWSInfo())) {
			// ...respond to it.
			event.getManageTitleCallback().manageTitle(m_manageTeamsInfo.getAdminActionTitle());
		}
	}

	/*
	 * Sets the view's size once thing are ready for it.
	 */
	private void setViewSizeIfReady() {
		// If the dialog is attached and the view is ready...
		if (m_dlgAttached && m_viewReady) {
			// ...it's ready to be sized.
			setViewSizeAsync();
		}
	}
	
	/*
	 * Asynchronously adjusts the views size based on its header and
	 * footer. 
	 */
	private void setViewSizeAsync() {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					setViewSizeNow();
				}
			});
	}
	
	/*
	 * Synchronously adjusts the views size based on its header and
	 * footer. 
	 */
	private void setViewSizeNow() {
		// If we don't have the height adjustment for the dialog yet...
		if ((-1) == m_dlgHeightAdjust) {
			// ...calculate it now...
			m_dlgHeightAdjust =
				(DIALOG_HEIGHT_ADJUST              +
				getHeaderPanel().getOffsetHeight() +
				getFooterPanel().getOffsetHeight());
		}

		// ...and set the size of the appropriate view.
		int width  = (m_showCX - DIALOG_WIDTH_ADJUST);
		int height = (m_showCY - m_dlgHeightAdjust);
		if      (null != m_twsView)      m_twsView.setPixelSize(     width, height);
		else if (null != m_twsTrashView) m_twsTrashView.setPixelSize(width, height);
	}
	
	/**
	 * Called when the contained view reaches the ready state.
	 * 
	 * Implements the ViewReady.viewReady() method.
	 */
	@Override
	public void viewReady() {
		m_viewReady = true;
		setViewSizeIfReady();
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
		m_viewReady    = false;
		m_twsView      = null;
		m_twsTrashView = null;
		m_rootPanel.clear();

		// Are we viewing the trash on the team workspaces?
		final BinderInfo pwsBI = m_manageTeamsInfo.getTeamsRootWSInfo();
		if (m_trashView) {
			// Yes!  Create a TrashView widget for the team workspaces
			// binder.
			BinderInfo trashBI = pwsBI.copyBinderInfo();
			trashBI.setWorkspaceType(WorkspaceType.TRASH);
			TrashView.createAsync(trashBI, m_rootPanel, this, new ViewClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(ViewBase twsTrashView) {
					// Store the view and add it to the panel.
					m_twsTrashView = ((TrashView) twsTrashView);
					m_rootPanel.add(m_twsTrashView);
				}
			});
		}
		
		else {
			// No, we aren't viewing the trash on the team workspaces!
			// Create a TeamWorkspacesView widget for the selected
			// binder.
			TeamWorkspacesView.createAsync(pwsBI, m_rootPanel, this, new ViewClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(ViewBase twsView) {
					// Store the view and add it to the panel.
					m_twsView = ((TeamWorkspacesView) twsView);
					m_rootPanel.add(m_twsView);
				}
			});
		}

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
	 * Asynchronously runs the given instance of the manage teams
	 * dialog.
	 */
	private static void runDlgAsync(final ManageTeamsDlg mtDlg, final boolean trashView, final int x, final int y, final int width, final int height) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					mtDlg.runDlgNow(trashView, x, y, width, height);
				}
			});
	}
	
	/*
	 * Synchronously runs the given instance of the manage teams
	 * dialog.
	 */
	private void runDlgNow(boolean trashView, int x, int y, int width, int height) {
		// Store the parameters...
		m_trashView = trashView;
		m_showX     = x;
		m_showY     = y;
		m_showCX    = width;
		m_showCY    = height;
		
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
	/* the manage teams dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the manage teams dialog
	 * asynchronously after it loads. 
	 */
	public interface ManageTeamsDlgClient {
		void onSuccess(ManageTeamsDlg mtDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ManageTeamsDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the dialog.
			final ManageTeamsDlgClient	mtDlgClient,
			final boolean				autoHide,
			final boolean				modal,
			final int					createX,
			final int					createY,
			final int					createCX,
			final int					createCY,
			
			// Parameters used to initialize and show an instance of the dialog.
			final ManageTeamsDlg	mtDlg,
			final boolean			trashView,
			final int				initX,
			final int				initY) {
		GWT.runAsync(ManageTeamsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ManageTeamsDlg());
				if (null != mtDlgClient) {
					mtDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != mtDlgClient) {
					// Yes!  Create the dialog.  Note that its
					// construction flow will call the appropriate
					// method off the ManageTeamsDlgClient object.
					new ManageTeamsDlg(mtDlgClient, autoHide, modal, createX, createY, createCX, createCY);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(mtDlg, trashView, initX, initY, createCX, createCY);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageTeamsDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param mtDlgClient
	 * @param autoHide
	 * @param modal
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void createAsync(ManageTeamsDlgClient mtDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		doAsyncOperation(mtDlgClient, autoHide, modal, x, y, cx, cy, null, false, (-1), (-1));
	}
	
	/**
	 * Initializes and shows the manage teams dialog.
	 * 
	 * @param mtDlg
	 * @param trashView
	 * @param x
	 * @param y
	 */
	public static void initAndShow(ManageTeamsDlg mtDlg, boolean trashView, int x, int y, int width, int height) {
		doAsyncOperation(null, false, false, (-1), (-1), width, height, mtDlg, trashView, x, y);
	}
}
