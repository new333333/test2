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
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.binderviews.LimitUserVisibilityView;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.ViewBase.ViewClient;
import org.kablink.teaming.gwt.client.GwtPrincipal;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FindControlBrowseEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetManageTitleEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedPrincipalsLimitedUserVisibilityEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetLimitUserVisibilityInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.LimitUserVisibilityInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetLimitedUserVisibilityRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetUserVisibilityCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.util.LimitedUserVisibilityInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.LimitUserVisibilityDlg;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements the Limit User Visibility dialog.
 * 
 * This dialog allows the administrator to define which users can only
 * see members of groups that they're in.
 * 
 * @author drfoster@novell.com
 */
public class LimitUserVisibilityDlg extends DlgBox
	implements ViewReady,
		// Event handlers implemented by this class.
		FindControlBrowseEvent.Handler,
		FullUIReloadEvent.Handler,
		GetManageTitleEvent.Handler,
		SearchFindResultsEvent.Handler,
		SetSelectedPrincipalsLimitedUserVisibilityEvent.Handler
{
	private boolean									m_dlgAttached;				// true when the dialog is attached to the document, false otherwise.
	private boolean									m_viewReady;				// true once the embedded view is ready,             false otherwise.
	private Boolean									m_findLimited;				// Boolean.TRUE if the FindCtrl is being used to add          limit user visibility rights.  null otherwise.
	private Boolean									m_findOverride;				// Boolean.TRUE if the FindCtrl is being used to add override limit user visibility rights.  null otherwise.
	private FindCtrl								m_findControl;				// The search widget.
	private FlowPanel								m_rootPanel;				//
	private GwtTeamingMessages						m_messages;					//
	private int										m_dlgHeightAdjust = (-1);	// Calculated the first time the dialog is shown.
	private int										m_showX;					//
	private int										m_showY;					//
	private int										m_showCX;					//
	private int										m_showCY;					//
	private LimitUserVisibilityInfoRpcResponseData	m_limitUserVisibilityInfo;	// Information necessary to run the limit user visibility dialog.
	private LimitUserVisibilityView					m_limitUserVisibilityView;	// The limit user visibility view.
	private List<HandlerRegistration>				m_registeredEventHandlers;	// Event handlers that are currently registered.
	private TeamingPopupPanel						m_findPopupPanel;			// The popup panel that will host m_findControl.
	
	// Constant adjustments to the size of the view so that it properly
	// fits the dialog's content area.
	private final static int DIALOG_HEIGHT_ADJUST	= 45;
	private final static int DIALOG_WIDTH_ADJUST	= 20;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.FIND_CONTROL_BROWSE,
		TeamingEvents.FULL_UI_RELOAD,
		TeamingEvents.GET_MANAGE_TITLE,
		TeamingEvents.SEARCH_FIND_RESULTS,
		TeamingEvents.SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private LimitUserVisibilityDlg(LimitUserVisibilityDlgClient luvDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		// Initialize the super class...
		super(
			autoHide,
			modal,
			x, y, cx, cy,
			DlgButtonMode.Close);
		
		// ...store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = cx;
		m_showCY = cy;
		
		// ...initialize anything else the needs it...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		addStyleName("vibe-limitUserVisibilityDlg");
		createAllDlgContent(
			m_messages.limitUserVisibilityDlg_Header(),
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			luvDlgClient);
	}

	/*
	 * Asynchronously add limit user visibility rights to the given
	 * Principal.
	 */
	private void addLimitUserVisibilityRightsToPrincipalAsync(final GwtPrincipal adminPrincipal) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				addLimitUserVisibilityRightsToPrincipalNow(adminPrincipal);
			}
		});
	}
	
	/*
	 * Synchronously add limit user visibility rights to the given
	 * Principal.
	 */
	private void addLimitUserVisibilityRightsToPrincipalNow(final GwtPrincipal limitVisibilityPrincipal) {
		// Set the limit user visibility rights.
		showDlgBusySpinner();
		List<Long> pids = new ArrayList<Long>();
		pids.add(limitVisibilityPrincipal.getIdLong());
		SetUserVisibilityCmd cmd = new SetUserVisibilityCmd(pids, m_findLimited, m_findOverride);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem.
				hideDlgBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SetPrincipalsLimitedUserVisibility());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// If we got any errors...
				hideDlgBusySpinner();
				SetLimitedUserVisibilityRpcResponseData responseData = ((SetLimitedUserVisibilityRpcResponseData) result.getResponseData()); 
				List<ErrorInfo> erList = responseData.getErrorList();
				if (GwtClientHelper.hasItems(erList)) {
					// ...display them...
					GwtClientHelper.displayMultipleErrors(m_messages.vibeDataTable_Error_SavingLimitedUserVisibility(), erList);
				}

				// ...and if we changed anything...
				final Map<Long, LimitedUserVisibilityInfo> adminRightsChangeMap = responseData.getLimitedUserVisibilityChangeMap(); 
				if (GwtClientHelper.hasItems(adminRightsChangeMap)) {
					// ...and force the UI to refresh.
					FullUIReloadEvent.fireOneAsync();
				}
			}
		});
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param unused
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Can we get the information necessary to manage
		// administrators?
		final LimitUserVisibilityDlg		luvDlg       = this;
		final LimitUserVisibilityDlgClient	luvDlgClient = ((LimitUserVisibilityDlgClient) callbackData);
		GwtClientHelper.executeCommand(new GetLimitUserVisibilityInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetLimitUserVisibilityInfo());

				// ...and tell the caller that the dialog will be
				// ...unavailable.
				luvDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Yes!  Store it and tell the caller that the dialog
				// is available.
				m_limitUserVisibilityInfo = ((LimitUserVisibilityInfoRpcResponseData) result.getResponseData());
				luvDlgClient.onSuccess(luvDlg);
			}
		});
		
		// Create the main panel that will hold the dialog's content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-limitUserVisibilityDlg-rootPanel");

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
		helpData.setPageId("uservisibility");
		return helpData;
	}

	/*
	 * Asynchronously invokes a FindCtrl to add limit user visibility
	 * rights to the selected principals.
	 */
	private void invokeAddRightsAsync(final Boolean limited, final Boolean override, final UIObject showRelativeTo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				invokeAddRightsNow(limited, override, showRelativeTo);
			}
		});
	}
	
	/*
	 * Synchronously invokes a FindCtrl to add limit user visibility
	 * rights to the selected principals.
	 */
	private void invokeAddRightsNow(final Boolean limited, final Boolean override, final UIObject showRelativeTo) {
		// Show the find control's popup panel relative to the given
		// UIObject...
		m_findLimited  = limited;
		m_findOverride = override;
		m_findPopupPanel.showRelativeTo(showRelativeTo);
		
		// ...and give it the focus.
		FocusWidget fw = m_findControl.getFocusWidget();
		if (null != fw) {
			GwtClientHelper.setFocusDelayed(fw);
		}
	}
	
	/*
	 * Asynchronously loads the find control.
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
	 * Synchronously loads the find control.
	 */
	private void loadPart1Now() {
		FindCtrl.createAsync(this, SearchType.PRINCIPAL, new FindCtrlClient() {			
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(FindCtrl findCtrl) {
				// Store the FindCtrl...
				m_findControl = findCtrl;
				m_findControl.addStyleName("vibe-manageAdministratorsDlg-findWidget");

				// ...tell it the kind of principals we want...
				m_findControl.setSearchForExternalPrincipals(false);
				m_findControl.setSearchForInternalPrincipals(true );
				m_findControl.setSearchForLdapGroups(        true );

				// ...wrap in in a TeamingPopupPanel...
				m_findPopupPanel = new TeamingPopupPanel(true);	// true -> This popup is auto hide.
				m_findPopupPanel.addStyleName("vibe-manageAdministratorsDlg-findPopup");
				m_findPopupPanel.setWidget(m_findControl);

				// ...and finish populating the dialog.
				populateDlgAsync();
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
		// Attach the widget and register the event handlers...
		super.onAttach();
		registerEvents();
		
		// ...and set the views size if its ready.
		m_dlgAttached = true;
		setViewSizeIfReady();
	}
	
	/**
	 * Called when the data table is detached.
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
	 * Handles FindControlBrowseEvent's received by this class.
	 * 
	 * Implements the FindControlBrowseEvent.Handler.onFindControlBrowse()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onFindControlBrowse(FindControlBrowseEvent event) {
		// Simply invoke the find browser using the parameters from the
		// event.
		FindControlBrowserPopup.doBrowse(event.getFindControl(), event.getFindStart());
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
		if (null != m_limitUserVisibilityView) {
			m_limitUserVisibilityView.resetView();
		}
	}
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults(SearchFindResultsEvent event) {
		// If the find results aren't for the limit user visibility
		// dialog...
		if (!(((Widget) event.getSource()).equals(this))) {
			// ...ignore the event.
			return;
		}
		
		// Hide the find widgets.
		m_findControl.hideSearchResults();
		m_findControl.clearText();
		m_findPopupPanel.hide();

		// If the search result is a GwtPrincipal, add the appropriate
		// limit user visibility rights to it.
		GwtTeamingItem obj = event.getSearchResults();
		if (obj instanceof GwtPrincipal)
		     addLimitUserVisibilityRightsToPrincipalAsync(((GwtPrincipal) obj));
		else GwtClientHelper.deferredAlert(m_messages.limitUserVisibilityDlgErrorInvalidSearchResult());
	}
	
	/**
	 * Handles SetSelectedPrincipalsLimitedUserVisibilityEvent's received by this class.
	 * 
	 * Implements the SetSelectedPrincipalsLimitedUserVisibilityEvent.Handler.onSetSelectedPrincipalsLimitedUserVisibility() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetSelectedPrincipalsLimitedUserVisibility(SetSelectedPrincipalsLimitedUserVisibilityEvent event) {
		// We only support setting rights from the limit user
		// visibility admin console dialog.  Is it supported?
		BinderInfo bi = m_limitUserVisibilityInfo.getRootWSInfo();
		WorkspaceType wt = bi.getWorkspaceType();
		if (wt.isLimitUserVisibility()) {
			// Yes!  Also, we only handle the 'select principal'
			// variation of this event.  Is that what this is?
			if (event.isSelectPrincipal()) {
				// Yes!  Is the event targeted to this view!
				invokeAddRightsAsync(event.getLimited(), event.getOverride(), event.getShowRelativeTo());
			}
		}
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
		if (event.getBinderInfo().isEqual(m_limitUserVisibilityInfo.getRootWSInfo())) {
			// ...respond to it.
			event.getManageTitleCallback().manageTitle(m_limitUserVisibilityInfo.getAdminActionTitle());
		}
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
		m_viewReady               = false;
		m_limitUserVisibilityView = null;
		m_rootPanel.clear();
		
		// ...repopulate it with data from the server...
		final BinderInfo rwsBI = m_limitUserVisibilityInfo.getRootWSInfo();
		LimitUserVisibilityView.createAsync(rwsBI, this, new ViewClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ViewBase limitUserVisibilityView) {
				// Store the view and add it to the panel.
				m_limitUserVisibilityView = ((LimitUserVisibilityView) limitUserVisibilityView);
				m_rootPanel.add(m_limitUserVisibilityView);
			}
		});
		
		// ...and position and show the dialog.
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
	private static void runDlgAsync(final LimitUserVisibilityDlg luvDlg, final int x, final int y, final int cx, final int cy) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					luvDlg.runDlgNow(x, y, cx, cy);
				}
			});
	}
	
	/*
	 * Synchronously runs the given instance of the limit user
	 * visibility dialog.
	 */
	private void runDlgNow(int x, int y, int cx, int cy) {
		// Store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = cx;
		m_showCY = cy;
		
		// ...and start populating the dialog.
		loadPart1Async();
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
		if (null != m_limitUserVisibilityView) {
			m_limitUserVisibilityView.setPixelSize(width, height);
		}
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
					new LimitUserVisibilityDlg(
						luvDlgClient,	// The client onSuccess()/onUnavailable() will be called from createContent().
						autoHide,
						modal,
						createX,
						createY,
						createCX,
						createCY);
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
	 * @param luvDlgClient
	 * @param autoHide
	 * @param modal
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void createAsync(final LimitUserVisibilityDlgClient luvDlgClient, final boolean autoHide, final boolean modal, final int x, final int y, final int cx, final int cy) {
		doAsyncOperation(
			// Creation parameters.
			luvDlgClient,
			autoHide,
			modal,
			x, y, cx, cy,
			
			// Initialize and show parameters.  Unused.
			null,
			(-1), (-1), (-1), (-1));
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
		doAsyncOperation(
			// Creation parameters.  Unused.
			null,
			false,
			false,
			(-1), (-1), (-1), (-1),
			
			// Initialize and show parameters.
			luvDlg,
			x, y, cx, cy);
	}
}
