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

import org.kablink.teaming.gwt.client.binderviews.EntryMenuPanel;
import org.kablink.teaming.gwt.client.binderviews.PersonalWorkspacesView;
import org.kablink.teaming.gwt.client.binderviews.TrashView;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewBase.ViewClient;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.CheckManageDlgActiveEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.InvokeImportProfilesDlgEvent;
import org.kablink.teaming.gwt.client.event.GetManageTitleEvent;
import org.kablink.teaming.gwt.client.event.InvokeUserPropertiesDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokePrincipalDesktopSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokePrincipalMobileSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokeUserShareRightsDlgEvent;
import org.kablink.teaming.gwt.client.event.ManageUsersFilterEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedUserDesktopSettingsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedUserMobileSettingsEvent;
import org.kablink.teaming.gwt.client.event.SetSelectedUserShareRightsEvent;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetManageUsersInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ManageUsersInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveManageUsersStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.util.ManageUsersState;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.ImportProfilesDlg.ImportProfilesDlgClient;
import org.kablink.teaming.gwt.client.widgets.UserPropertiesDlg.UserPropertiesDlgClient;
import org.kablink.teaming.gwt.client.widgets.UserShareRightsDlg.UserShareRightsDlgClient;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements the 'Manage Users' dialog.
 *  
 * @author drfoster@novell.com
 */
public class ManageUsersDlg extends DlgBox
	implements ViewReady,
		// Event handlers implemented by this class.
		AdministrationExitEvent.Handler,
		CheckManageDlgActiveEvent.Handler,
		FullUIReloadEvent.Handler,
		GetManageTitleEvent.Handler,
		ManageUsersFilterEvent.Handler,
		InvokeImportProfilesDlgEvent.Handler,
		InvokeUserPropertiesDlgEvent.Handler,
		InvokeUserShareRightsDlgEvent.Handler,
		SetSelectedUserDesktopSettingsEvent.Handler,
		SetSelectedUserMobileSettingsEvent.Handler,
		SetSelectedUserShareRightsEvent.Handler
{
	private boolean							m_dlgAttached;				// true when the dialog is attached to the document.            false otherwise.
	private boolean							m_trashView;				// true if we're viewing the trash on the personal workspaces.  false otherwise.
	private boolean							m_viewReady;				// true once the embedded view is ready.                        false otherwise.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private ImportProfilesDlg				m_importProfilesDlg;		// An ImportProfilesDlg, once one is created.
	private int								m_dlgHeightAdjust = (-1);	// Calculated the first time the dialog is shown.
	private int								m_showX;					// The x and...
	private int								m_showY;					// ...y position and...
	private int								m_showCX;					// ...width and...
	private int								m_showCY;					// ...height of the dialog.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ManageUsersInfoRpcResponseData	m_manageUsersInfo;			// Information necessary to run the manage users dialog.
	private PersonalWorkspacesView			m_pwsView;					// The personal workspace       view.
	private TrashView						m_pwsTrashView;				// The personal workspace trash view.
	private UserPropertiesDlg				m_userPropertiesDlg;		// A UserPropertiesDlg,  once one is created.
	private UserShareRightsDlg				m_userShareRightsDlg;		// A UserShareRightsDlg, once one is created.
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
		TeamingEvents.MANAGE_USERS_FILTER,
		TeamingEvents.INVOKE_IMPORT_PROFILES_DLG,
		TeamingEvents.INVOKE_USER_PROPERTIES_DLG,
		TeamingEvents.INVOKE_USER_SHARE_RIGHTS_DLG,
		TeamingEvents.SET_SELECTED_USER_DESKTOP_SETTINGS,
		TeamingEvents.SET_SELECTED_USER_MOBILE_SETTINGS,
		TeamingEvents.SET_SELECTED_USER_SHARE_RIGHTS,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageUsersDlg(ManageUsersDlgClient muDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
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
		addStyleName("vibe-manageUsersDlg");
		createAllDlgContent(
			m_messages.manageUsersDlgCaption(),
			DlgBox.getSimpleSuccessfulHandler(),
			DlgBox.getSimpleCanceledHandler(),
			muDlgClient); 
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
		// Can we get the information necessary to manage users?
		final ManageUsersDlg		muDlg       = this;
		final ManageUsersDlgClient	muDlgClient = ((ManageUsersDlgClient) callbackData);
		GwtClientHelper.executeCommand(new GetManageUsersInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetManageUsersInfo());

				// ...and tell the caller that the dialog will be
				// ...unavailable.
				muDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Yes!  Store it and tell the caller that the dialog
				// is available.
				m_manageUsersInfo = ((ManageUsersInfoRpcResponseData) result.getResponseData());
				muDlgClient.onSuccess(muDlg);
			}
		});
		
		// Create the main panel that will hold the dialog's content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-manageUsersDlg-rootPanel");
		
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
	 * Returns the HelpData for the manage users dialog.
	 * 
	 * Overrides the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("users");
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
	 * Called when the manage users dialog is attached.
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
	 * Called when the manage users dialog is detached.
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
		if      (null != m_pwsView)      m_pwsView.resetView();
		else if (null != m_pwsTrashView) m_pwsTrashView.resetView();
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
		if (event.getBinderInfo().isEqual(m_manageUsersInfo.getProfilesRootWSInfo())) {
			// ...respond to it.
			event.getManageTitleCallback().manageTitle(m_manageUsersInfo.getAdminActionTitle());
		}
	}

	/**
	 * Handles ManageUsersFilterEvent's received by this class.
	 * 
	 * Implements the ManageUsersFilterEvent.Handler.onManageUsersFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onManageUsersFilter(ManageUsersFilterEvent event) {
		// If we don't have a PersonalWorkspacesView...
		if (null == m_pwsView) {
			// ...bail.
			return;
		}
		
		// Toggle the appropriate state...
		EntryMenuPanel		emp = m_pwsView.getEntryMenuPanel();
		ManageUsersState	mus = emp.getManageUsersState().createCopy();
		switch (event.getManageUsersFilter()) {
		case SHOW_DISABLED_USERS:   mus.setShowDisabled(     !(mus.isShowDisabled()));      break;
		case SHOW_ENABLED_USERS:    mus.setShowEnabled(      !(mus.isShowEnabled()));       break;
		case SHOW_EXTERNAL_USERS:   mus.setShowExternal(     !(mus.isShowExternal()));      break;
		case SHOW_INTERNAL_USERS:   mus.setShowInternal(     !(mus.isShowInternal()));      break;
		case SHOW_SITE_ADMINS:      mus.setShowSiteAdmins(   !(mus.isShowSiteAdmins()));    break;
		case SHOW_NON_SITE_ADMINS:  mus.setShowNonSiteAdmins(!(mus.isShowNonSiteAdmins())); break;
		}

		// ...save it...
		GwtClientHelper.executeCommand(
				new SaveManageUsersStateCmd(mus),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveManageUsersState());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// ...and force a UI refresh.
				FullUIReloadEvent.fireOneAsync();
			}
		});
	}

	/**
	 * Handles InvokeImportProfilesDlgEvent's received by this class.
	 * 
	 * Implements the InvokeImportProfilesDlgEvent.Handler.onInvokeImportProfilesDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeImportProfilesDlg(InvokeImportProfilesDlgEvent event) {
		// If we don't have a PersonalWorkspacesView...
		if (null == m_pwsView) {
			// ...bail.
			return;
		}
		
		// Have we create an import profiles dialog yet?
		if (null == m_importProfilesDlg) {
			// No!  Can we create one now?
			ImportProfilesDlg.createAsync(new ImportProfilesDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in 
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ImportProfilesDlg ipDlg) {
					// Yes, we created the import profiles dialog!
					// Show it.
					m_importProfilesDlg = ipDlg;
					showImportProfilesDlgAsync();
				}
			});
		}
		
		else {
			// Yes, we have an import profiles dialog!  Show it.
			showImportProfilesDlgAsync();
		}
	}
	
	/**
	 * Handles InvokeUserPropertiesDlgEvent's received by this class.
	 * 
	 * Implements the InvokeUserPropertiesDlgEvent.Handler.onInvokeUserPropertiesDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeUserPropertiesDlg(final InvokeUserPropertiesDlgEvent event) {
		// Have we create an user properties dialog yet?
		if (null == m_userPropertiesDlg) {
			// No!  Can we create one now?
			UserPropertiesDlg.createAsync(new UserPropertiesDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in 
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(UserPropertiesDlg upDlg) {
					// Yes, we created the user properties dialog!
					// Show it.
					m_userPropertiesDlg = upDlg;
					showUserPropertiesDlgAsync(event.getUserId(), event.getShowRelativeTo());
				}
			});
		}
		
		else {
			// Yes, we have an user properties dialog!  Show it.
			showUserPropertiesDlgAsync(event.getUserId(), event.getShowRelativeTo());
		}
	}
	
	/**
	 * Handles InvokeUserShareRightsDlgEvent's received by this class.
	 * 
	 * Implements the InvokeUserShareRightsDlgEvent.Handler.onInvokeUserShareRightsDlg() method.
	 * 
	 * @param event
	 */
	@Override
	public void onInvokeUserShareRightsDlg(final InvokeUserShareRightsDlgEvent event) {
		// Have we create a user share rights dialog yet?
		if (null == m_userShareRightsDlg) {
			// No!  Can we create one now?
			UserShareRightsDlg.createAsync(new UserShareRightsDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in 
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(UserShareRightsDlg usrDlg) {
					// Yes, we created the user share rights dialog!
					// Show it.
					m_userShareRightsDlg = usrDlg;
					showUserShareRightsDlgAsync(event.getUserIds(), event.getShowRelativeTo());
				}
			});
		}
		
		else {
			// Yes, we have a user share rights dialog!  Show it.
			showUserShareRightsDlgAsync(event.getUserIds(), event.getShowRelativeTo());
		}
	}
	
	/**
	 * Handles SetSelectedUserDesktopSettingsEvent's received by this class.
	 * 
	 * Implements the SetSelectedUserDesktopSettingsEvent.Handler.onSetSelectedUserDesktopSettings() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetSelectedUserDesktopSettings(SetSelectedUserDesktopSettingsEvent event) {
		// If we don't have a PersonalWorkspacesView...
		if (null == m_pwsView) {
			// ...bail.
			return;
		}
		
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_manageUsersInfo.getProfilesRootWSInfo().getBinderIdAsLong())) {
			// Yes!  Get the selected EntityId's...
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = m_pwsView.getSelectedEntityIds();
			}
			
			// ...extract the selected user ID's from that...
			final List<Long> selectedUserList = new ArrayList<Long>();
			for (EntityId eid:  selectedEntityIds) {
				selectedUserList.add(eid.getEntityId());
			}

			// ...and use them to invoke the settings dialog.
			GwtTeaming.fireEventAsync(
				new InvokePrincipalDesktopSettingsDlgEvent(
					selectedUserList,
					true));	// true -> IDs are users.
		}
	}

	/**
	 * Handles SetSelectedUserMobileSettingsEvent's received by this class.
	 * 
	 * Implements the SetSelectedUserMobileSettingsEvent.Handler.onSetSelectedUserMobileSettings() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetSelectedUserMobileSettings(SetSelectedUserMobileSettingsEvent event) {
		// If we don't have a PersonalWorkspacesView...
		if (null == m_pwsView) {
			// ...bail.
			return;
		}
		
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_manageUsersInfo.getProfilesRootWSInfo().getBinderIdAsLong())) {
			// Yes!  Get the selected EntityId's...
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = m_pwsView.getSelectedEntityIds();
			}
			
			// ...extract the selected user ID's from that...
			final List<Long> selectedUserList = new ArrayList<Long>();
			for (EntityId eid:  selectedEntityIds) {
				selectedUserList.add(eid.getEntityId());
			}

			// ...and use them to invoke the settings dialog.
			GwtTeaming.fireEventAsync(
				new InvokePrincipalMobileSettingsDlgEvent(
					selectedUserList,
					true));	// true -> IDs are users.
		}
	}

	/**
	 * Handles SetSelectedUserShareRightsEvent's received by this class.
	 * 
	 * Implements the SetSelectedUserShareRightsEvent.Handler.onSetSelectedUserShareRights() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSetSelectedUserShareRights(SetSelectedUserShareRightsEvent event) {
		// If we don't have a PersonalWorkspacesView...
		if (null == m_pwsView) {
			// ...bail.
			return;
		}
		
		// Is the event targeted to this folder?
		Long eventFolderId = event.getFolderId();
		if (eventFolderId.equals(m_manageUsersInfo.getProfilesRootWSInfo().getBinderIdAsLong())) {
			// Yes!  Get the selected EntityId's...
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
				selectedEntityIds = m_pwsView.getSelectedEntityIds();
			}
			
			// ...extract the selected user ID's from that...
			final List<Long> selectedUserList = new ArrayList<Long>();
			for (EntityId eid:  selectedEntityIds) {
				selectedUserList.add(eid.getEntityId());
			}

			// ...and invoke the user share rights dialog.
			GwtTeaming.fireEventAsync(
				new InvokeUserShareRightsDlgEvent(
					selectedUserList));
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
		if      (null != m_pwsView)      m_pwsView.setPixelSize(     width, height);
		else if (null != m_pwsTrashView) m_pwsTrashView.setPixelSize(width, height);
	}
	
	/*
	 * Asynchronously shows the import profiles dialog.
	 */
	private void showImportProfilesDlgAsync() {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					showImportProfilesDlgNow();
				}
			});
	}

	/*
	 * Synchronously shows the import profiles dialog.
	 */
	private void showImportProfilesDlgNow() {
		ImportProfilesDlg.initAndShow(m_importProfilesDlg, m_manageUsersInfo.getProfilesRootWSInfo());
	}

	/*
	 * Asynchronously shows the user properties dialog.
	 */
	private void showUserPropertiesDlgAsync(final Long userId, final UIObject showRelativeTo) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					showUserPropertiesDlgNow(userId, showRelativeTo);
				}
			});
	}

	/*
	 * Synchronously shows the user properties dialog.
	 */
	private void showUserPropertiesDlgNow(Long userId, UIObject showRelativeTo) {
		UserPropertiesDlg.initAndShow(m_userPropertiesDlg, userId, showRelativeTo);
	}

	/*
	 * Asynchronously shows the user share rights dialog.
	 */
	private void showUserShareRightsDlgAsync(final List<Long> selectedUserList, final UIObject showRelativeTo) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					showUserShareRightsDlgNow(selectedUserList, showRelativeTo);
				}
			});
	}

	/*
	 * Synchronously shows the user share rights dialog.
	 */
	private void showUserShareRightsDlgNow(final List<Long> selectedUserList, final UIObject showRelativeTo) {
		UserShareRightsDlg.initAndShow(
			m_userShareRightsDlg,
			selectedUserList,
			showRelativeTo);
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
		m_pwsView      = null;
		m_pwsTrashView = null;
		m_rootPanel.clear();

		// Are we viewing the trash on the personal workspaces?
		final BinderInfo pwsBI = m_manageUsersInfo.getProfilesRootWSInfo();
		if (m_trashView) {
			// Yes!  Create a TrashView widget for the personal
			// workspaces binder.
			BinderInfo trashBI = pwsBI.copyBinderInfo();
			trashBI.setWorkspaceType(WorkspaceType.TRASH);
			TrashView.createAsync(trashBI, m_rootPanel, this, new ViewClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(ViewBase pwsTrashView) {
					// Store the view and add it to the panel.
					m_pwsTrashView = ((TrashView) pwsTrashView);
					m_rootPanel.add(m_pwsTrashView);
				}
			});
		}
		
		else {
			// No, we aren't viewing the trash on the personal
			// workspaces!  Create a PersonalWorkspacesView widget for
			// the selected binder.
			PersonalWorkspacesView.createAsync(pwsBI, m_rootPanel, this, new ViewClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(ViewBase pwsView) {
					// Store the view and add it to the panel.
					m_pwsView = ((PersonalWorkspacesView) pwsView);
					m_rootPanel.add(m_pwsView);
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
	 * Asynchronously runs the given instance of the manage users
	 * dialog.
	 */
	private static void runDlgAsync(final ManageUsersDlg muDlg, final boolean trashView, final int x, final int y, final int width, final int height) {
		GwtClientHelper.deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					muDlg.runDlgNow(trashView, x, y, width, height);
				}
			});
	}
	
	/*
	 * Synchronously runs the given instance of the manage users
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
	/* the manage users dialog and perform some operation on it.     */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the manage users dialog
	 * asynchronously after it loads. 
	 */
	public interface ManageUsersDlgClient {
		void onSuccess(ManageUsersDlg muDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ManageUsersDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Parameters used to create the dialog.
			final ManageUsersDlgClient	muDlgClient,
			final boolean				autoHide,
			final boolean				modal,
			final int					createX,
			final int					createY,
			final int					createCX,
			final int					createCY,
			
			// Parameters used to initialize and show an instance of the dialog.
			final ManageUsersDlg	muDlg,
			final boolean			trashView,
			final int				initX,
			final int				initY) {
		GWT.runAsync(ManageUsersDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ManageUsersDlg());
				if (null != muDlgClient) {
					muDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != muDlgClient) {
					// Yes!  Create the dialog.  Note that its
					// construction flow will call the appropriate
					// method off the ManageUsersDlgClient object.
					new ManageUsersDlg(muDlgClient, autoHide, modal, createX, createY, createCX, createCY);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(muDlg, trashView, initX, initY, createCX, createCY);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageUsersDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param muDlgClient
	 * @param autoHide
	 * @param modal
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 */
	public static void createAsync(ManageUsersDlgClient muDlgClient, boolean autoHide, boolean modal, int x, int y, int cx, int cy) {
		doAsyncOperation(muDlgClient, autoHide, modal, x, y, cx, cy, null, false, (-1), (-1));
	}
	
	/**
	 * Initializes and shows the manage users dialog.
	 * 
	 * @param muDlg
	 * @param trashView
	 * @param x
	 * @param y
	 */
	public static void initAndShow(ManageUsersDlg muDlg, boolean trashView, int x, int y, int width, int height) {
		doAsyncOperation(null, false, false, (-1), (-1), width, height, muDlg, trashView, x, y);
	}
}
