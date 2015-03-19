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
package org.kablink.teaming.gwt.client.datatable;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.MobileDevicesView;
import org.kablink.teaming.gwt.client.binderviews.MobileDevicesViewSpec;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.ViewBase.ViewClient;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.ClearScheduledWipeSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetManageTitleEvent;
import org.kablink.teaming.gwt.client.event.ScheduleWipeSelectedMobileDevicesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteMobileDevicesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteMobileDevicesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetManageMobileDevicesInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ManageMobileDevicesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetMobileDevicesWipeScheduledStateCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.util.MobileDeviceRemovedCallback;
import org.kablink.teaming.gwt.client.util.MobileDevicesInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's 'Manage Mobile Devices' dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ManageMobileDevicesDlg extends DlgBox
	implements ViewReady,
		// Event handlers implemented by this class.
		AdministrationExitEvent.Handler,
		ClearScheduledWipeSelectedMobileDevicesEvent.Handler,
		DeleteSelectedMobileDevicesEvent.Handler,
		FullUIReloadEvent.Handler,
		ScheduleWipeSelectedMobileDevicesEvent.Handler,
		GetManageTitleEvent.Handler
{
	private boolean									m_dlgAttached;				// true when the dialog is attached to the document.     false otherwise.
	private boolean									m_viewReady;				// true once the embedded mobile devices view is ready.  false otherwise.
	private GwtTeamingMessages						m_messages;					// Access to Vibe's messages.
	private int										m_dlgHeightAdjust = (-1);	// Calculated the first time the dialog is shown.
	private int										m_showX;					//
	private int										m_showY;					//
	private Integer									m_showCX;					//
	private Integer									m_showCY;					//
	private Label									m_mobileDevicesCountLabel;	// The mobile devices count Label that's stored in the dialog's caption.
	private List<HandlerRegistration>				m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ManageMobileDevicesInfoRpcResponseData	m_manageMobileDevicesInfo;		// Information necessary to run the manage mobile devices dialog.
	private MobileDeviceRemovedCallback				m_removedCallback;			// Interface used to tell who's running the dialog that a device was removed .
	private MobileDevicesInfo						m_mdInfo;					// The MobileDevicesInfo this ManageMobileDevicesDlg is running against.
	private MobileDevicesView						m_mdView;					// The mobile devices view.
	private UIObject								m_showRelativeTo;			// The UIObject to show the dialog relative to.  null -> Center the dialog.
	private VibeFlowPanel							m_rootPanel;				// The panel that holds the dialog's contents.

	// Constant adjustments to the size of the mobile devices view so
	// that it properly fits the dialog's content area.
	private final static int DIALOG_HEIGHT_ADJUST_FLOAT	= 35;
	private final static int DIALOG_HEIGHT_ADJUST_FIXED	= 45;
	private final static int DIALOG_WIDTH_ADJUST_FLOAT	= 30;
	private final static int DIALOG_WIDTH_ADJUST_FIXED	= 20;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.ADMINISTRATION_EXIT,
		TeamingEvents.CLEAR_SCHEDULED_WIPE_SELECTED_MOBILE_DEVICES,
		TeamingEvents.DELETE_SELECTED_MOBILE_DEVICES,
		TeamingEvents.FULL_UI_RELOAD,
		TeamingEvents.GET_MANAGE_TITLE,
		TeamingEvents.SCHEDULE_WIPE_SELECTED_MOBILE_DEVICES,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageMobileDevicesDlg(ManageMobileDevicesDlgClient mmdDlgClient, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy) {
		// Initialize the superclass...
		super(
			autoHide,
			modal,
			x, y, cx, cy,			// Will be 0, 0, null, null for a floating dialog. 
			DlgButtonMode.Close);	// We only need a close button.

		// ...store the parameters...
		m_showX  = x;
		m_showY  = y;
		m_showCX = cx;
		m_showCY = cy;
		
		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the dialog's content.
		addStyleName("vibe-manageMobileDevicesDlg");
		createAllDlgContent(
			"",										// The dialog's caption will be set each time it is run.
			DlgBox.getSimpleSuccessfulHandler(),	// The dialog's editSuccessful() handler.
			DlgBox.getSimpleCanceledHandler(),		// The dialog's editCanceled()   handler.
			mmdDlgClient);							// null -> No callback data is required by createContent(). 
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
		// Can we get the information necessary to manage mobile
		// devices?
		final ManageMobileDevicesDlg       mmdDlg       = this;
		final ManageMobileDevicesDlgClient mmdDlgClient = ((ManageMobileDevicesDlgClient) callbackData);
		GwtClientHelper.executeCommand(new GetManageMobileDevicesInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetManageMobileDevicesInfo());

				// ...and tell the caller that the dialog will be
				// ...unavailable.
				mmdDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Yes!  Store it and tell the caller that the dialog
				// is available.
				m_manageMobileDevicesInfo = ((ManageMobileDevicesInfoRpcResponseData) result.getResponseData());
				mmdDlgClient.onSuccess(mmdDlg);
			}
		});
		
		// Create the main panel that holds the dialog's content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-manageMobileDevicesDlg-panel");

		// ...and return it.  Note that it will get populated during
		// ...the initAndShow() call.
		return m_rootPanel;
	}

	/*
	 * Asynchronously deletes the selected mobile devices.
	 */
	private void deleteSelectedMobileDevicesAsync(final List<EntityId> selectedMobileDevices) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				deleteSelectedMobileDevicesNow(selectedMobileDevices);
			}
		});
	}
	
	/*
	 * Synchronously deletes the selected mobile devices.
	 */
	private void deleteSelectedMobileDevicesNow(final List<EntityId> selectedMobileDevices) {
		// Show a busy spinner while we clear the adHoc folder
		// settings.
		showDlgBusySpinner();

		// Delete the selected mobile devices...
		DeleteMobileDevicesCmd cmd = new DeleteMobileDevicesCmd(selectedMobileDevices);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				hideDlgBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DeleteMobileDevices());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				DeleteMobileDevicesRpcResponseData erList = ((DeleteMobileDevicesRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them...
					GwtClientHelper.displayMultipleErrors(
						m_messages.manageMobileDevicesDlg_failureDeletingMobileDevices(),
						erList.getErrorList());
				}

				// ...and hide the busy spinner.
				hideDlgBusySpinner();
				
				// Were any mobile devices successfully deleted?
				List<EntityId> delList = erList.getSuccessfulDeletes();
				final int count = ((null == delList) ? 0 : delList.size());
				if (0 < count) {
					// Yes!  If we have a removed callback...
					if (null != m_removedCallback) {
						final MobileDevicesInfo mdInfoClone = m_mdInfo.copyMobileDevicesInfo();	// Cloned so that the change below doesn't affect it.
						GwtClientHelper.deferCommand(new ScheduledCommand() {
							@Override
							public void execute() {
								// ...tell it how many were deleted...
								m_removedCallback.mobileDevicesRemoved(mdInfoClone, count);
							}
						});
					}

					// If we're running in per user mode...
					if (null != m_mdInfo.getUserId()) {
						// ...we need to update the device count in the
						// ...dialog's caption as well.
						int devCount = m_mdInfo.getMobileDevicesCount();
						devCount -= count;
						if (0 > devCount) {
							devCount = 0;
						}
						m_mdInfo.setMobileDevicesCount(devCount);
						setCaptionDevicesCount(devCount);
					}
					
					// and reset the view to reflect the deletions.
					resetViewAsync();
				}
			}
		});
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
		// Unused.
		return null;
	}

	/**
	 * 
	 */
	@Override
	public HelpData getHelpData()
	{
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName( HelpData.ADMIN_GUIDE );
		helpData.setPageId( "mobile_mng" );
		
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
	 * Called when the manage mobile devices dialog is attached.
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
	 * Handles ClearScheduledWipeSelectedMobileDevicesEvent's received by this class.
	 * 
	 * Implements the ClearScheduledWipeSelectedMobileDevicesEvent.Handler.onClearScheduledWipeSelectedMobileDevices() method.
	 * 
	 * @param event
	 */
	@Override
	public void onClearScheduledWipeSelectedMobileDevices(ClearScheduledWipeSelectedMobileDevicesEvent event) {
		// If the event doesn't have any selected devices...
		List<EntityId> selectedEntityIds = event.getSelectedEntities();
		if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
			// ...use the ones selected in the view.
			selectedEntityIds = m_mdView.getSelectedEntityIds();
		}
		if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
			return;
		}
		
		// Clear the scheduled wipes...
		SetMobileDevicesWipeScheduledStateCmd cmd = new SetMobileDevicesWipeScheduledStateCmd(selectedEntityIds, false);	// false -> Clear scheduled wipes.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetMobileDevicesWipeScheduledState());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...and if successful...
				boolean reply = ((BooleanRpcResponseData) response.getResponseData()).getBooleanValue();
				if (reply) {
					resetViewAsync();
				}
			}
		});
	}
	
	/**
	 * Handles DeleteSelectedMobileDevicesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedMobileDevicesEvent.Handler.onDeleteSelectedMobileDevices() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedMobileDevices(DeleteSelectedMobileDevicesEvent event) {
		// If the event doesn't have any selected devices...
		List<EntityId> selIds = event.getSelectedEntities();
		if (!(GwtClientHelper.hasItems(selIds))) {
			// ...use the ones selected in the view.
			selIds = m_mdView.getSelectedEntityIds();
		}
		if (!(GwtClientHelper.hasItems(selIds))) {
			return;
		}
		final List<EntityId> selectedEntityIds = selIds;

		// Is the user sure about deleting the selected mobile devices?
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes, they're sure!  Delete the selected
							// mobile devices.
							deleteSelectedMobileDevicesAsync(selectedEntityIds);
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.manageMobileDevicesDlg_confirmDelete());
			}
		});
		
	}
	
	/**
	 * Called when the manage mobile devices dialog is detached.
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

	/**
	 * Handles FullUIReloadEvent's received by this class.
	 * 
	 * Implements the FullUIReloadEvent.Handler.onFullUIReload() method.
	 * 
	 * @param event
	 */
	@Override
	public void onFullUIReload(FullUIReloadEvent event) {
		// If we have a mobile devices view...
		if (null != m_mdView) {
			// ...tell it to reset itself.
			m_mdView.resetView();
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
		if (event.getBinderInfo().isEqual(m_manageMobileDevicesInfo.getProfilesRootWSInfo())) {
			// ...respond to it.
			String txt;
			if (null == m_mdInfo.getUserId())
			     txt = m_messages.manageMobileDevicesDlgSystem();
			else txt = m_messages.manageMobileDevicesDlgUser(m_mdInfo.getClientItemTitle());
			event.getManageTitleCallback().manageTitle(txt);
		}
	}

	/**
	 * Handles ScheduleWipeSelectedMobileDevicesEvent's received by this class.
	 * 
	 * Implements the ScheduleWipeSelectedMobileDevicesEvent.Handler.onScheduleWipeSelectedMobileDevices() method.
	 * 
	 * @param event
	 */
	@Override
	public void onScheduleWipeSelectedMobileDevices(ScheduleWipeSelectedMobileDevicesEvent event) {
		// If the event doesn't have any selected devices...
		List<EntityId> selectedEntityIds = event.getSelectedEntities();
		if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
			// ...use the ones selected in the view.
			selectedEntityIds = m_mdView.getSelectedEntityIds();
		}
		if (!(GwtClientHelper.hasItems(selectedEntityIds))) {
			return;
		}

		// Schedule the wipes...
		SetMobileDevicesWipeScheduledStateCmd cmd = new SetMobileDevicesWipeScheduledStateCmd(selectedEntityIds, true);	// true -> Schedule wipes.
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetMobileDevicesWipeScheduledState());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...and if successful...
				boolean reply = ((BooleanRpcResponseData) response.getResponseData()).getBooleanValue();
				if (reply) {
					resetViewAsync();
				}
			}
		});
	}
	
	/**
	 * Called when the mobile devices view reaches the ready state.
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
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
		m_dlgAttached =
		m_viewReady   = false;
		m_mdView      = null;
		m_rootPanel.clear();
		
		// Create a MobileDevicesView widget...
		MobileDevicesView.createAsync(m_manageMobileDevicesInfo.getProfilesRootWSInfo(), this, new ViewClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ViewBase pwsView) {
				// ...storing it in the view and adding it to the
				// ...panel...
				m_mdView = ((MobileDevicesView) pwsView);
				m_rootPanel.add(m_mdView);
			}
		});

		// ...and show the dialog.
		setFixedSize(m_showCX, m_showCY);
		if       (null != m_showRelativeTo)                 showRelativeTo(  m_showRelativeTo );			// Unused?
		else if ((null != m_showCX) && (null != m_showCY)) {setPopupPosition(m_showX,  m_showY); show();}	// For:  Manage system devices!
		else                                                center();										// For:  Manage user devices!
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
	 * Asynchronously reset's the view's contents.
	 */
	private void resetViewAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				resetViewNow();
			}
		} );
	}
	
	/*
	 * Synchronously reset's the view's contents.
	 */
	private void resetViewNow() {
		m_mdView.resetView();
	}
	
	/*
	 * Asynchronously runs the given instance of the manage mobile
	 * devices dialog.
	 */
	private static void runDlgAsync(final ManageMobileDevicesDlg mmdDlg, final MobileDevicesInfo ci, final UIObject showRelativeTo, final boolean autoHide, final boolean modal, final int x, final int y, final Integer cx, final Integer cy, final MobileDeviceRemovedCallback removedCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mmdDlg.runDlgNow(ci, showRelativeTo, autoHide, modal, x, y, cx, cy, removedCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the manage mobile
	 * devices dialog.
	 */
	private void runDlgNow(MobileDevicesInfo mdInfo, UIObject showRelativeTo, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy, MobileDeviceRemovedCallback removedCallback) {
		// Store the parameters...
		m_mdInfo          = mdInfo;
		m_showRelativeTo  = showRelativeTo;
		m_showX           = x;
		m_showY           = y;
		m_showCX          = cx;
		m_showCY          = cy;
		m_removedCallback = removedCallback;

		// ...update the dialog's styles, caption and
		// ...ManageMobileDevicesInfo based on the parameters...
		MobileDevicesViewSpec mdvSpec;
		Label hcl = getHeaderCaptionLabel();
		Long userId = mdInfo.getUserId();
		if (null == userId) {
			addStyleName(   "vibe-manageMobileDevicesDlg-system");
			removeStyleName("vibe-manageMobileDevicesDlg-user"  );
			
			setAutoHideAndModality(autoHide, modal);				// true -> Auto hide.  false -> Not modal.
			hcl.setStyleName("teamingDlgBoxHeader-captionLabel");	// Default style as originally set in DlgBox.createCaption().
			
			setCaption(m_messages.manageMobileDevicesDlgCaptionSystem());
			
			mdvSpec = new MobileDevicesViewSpec(MobileDevicesViewSpec.Mode.SYSTEM);
		}
		else {
			removeStyleName("vibe-manageMobileDevicesDlg-system");
			addStyleName(   "vibe-manageMobileDevicesDlg-user"  );
			
			setAutoHideAndModality(autoHide, modal);							// false -> Not auto hide.  true -> Modal.
			hcl.setStyleName("vibe-manageMobileDevicesDlg-headerCaption-user");	// Style that accounts for the device count on the right of the caption.
			
			setCaption(             mdInfo.getClientItemTitle()   );
			setCaptionImage((Image) mdInfo.getClientItemImage()   );
			setCaptionDevicesCount( mdInfo.getMobileDevicesCount());
			
			mdvSpec = new MobileDevicesViewSpec(MobileDevicesViewSpec.Mode.USER, userId);
		}
		m_manageMobileDevicesInfo.setMobileDeviceViewSpec(mdvSpec);
		
		// ...and populate the dialog.
		populateDlgAsync();
	}

	/*
	 * Sets a devices count label into the header.
	 */
	private void setCaptionDevicesCount(int cCount) {
		// If we haven't added the Label to the dialog's header yet...
		if (null == m_mobileDevicesCountLabel) {
			// ...create it and add it now...
			m_mobileDevicesCountLabel = new Label();
			String bgStyle;
			if (GwtClientHelper.jsIsIE())
			     bgStyle = "teamingDlgBoxHeaderBG_IE";
			else bgStyle = "teamingDlgBoxHeaderBG_NonIE";
			m_mobileDevicesCountLabel.addStyleName("vibe-manageMobileDevicesDlg-captionCount " + bgStyle);
			getHeaderPanel().add(m_mobileDevicesCountLabel);
		}
		
		// ...and store the appropriate text into it.
		m_mobileDevicesCountLabel.setText(GwtTeaming.getMessages().manageMobileDevicesDlgDevices(cCount));
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
		boolean floatLayout = ((null == m_showCX) && (null == m_showCY));
		if ((-1) == m_dlgHeightAdjust) {
			// ...calculate it now...
			m_dlgHeightAdjust = (
				getHeaderPanel().getOffsetHeight() +
				getFooterPanel().getOffsetHeight());
			
			if (floatLayout)
			     m_dlgHeightAdjust += DIALOG_HEIGHT_ADJUST_FLOAT;
			else m_dlgHeightAdjust += DIALOG_HEIGHT_ADJUST_FIXED;
		}

		// ...set the size of the mobile devices view.
		int cx;
		int cy;
		if (floatLayout) {
			cx = (getOffsetWidth()  - DIALOG_WIDTH_ADJUST_FLOAT);
			cy = (getOffsetHeight() - m_dlgHeightAdjust);
		}
		else {
			cx = (m_showCX - DIALOG_WIDTH_ADJUST_FIXED);
			cy = (m_showCY - m_dlgHeightAdjust);
		}
		m_mdView.setPixelSize(cx, cy);
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
	/* the manage mobile devices dialog and perform some operation   */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the manage mobile devices
	 * dialog asynchronously after it loads. 
	 */
	public interface ManageMobileDevicesDlgClient {
		void onSuccess(ManageMobileDevicesDlg mmdDlg);
		void onUnavailable();
	}
	
	/*
	 * Asynchronously loads the ManageMobileDevicesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync() parameters.
			final ManageMobileDevicesDlgClient	mmdDlgClient,
			
			final boolean autoHide,
			final boolean modal,
			
			// ...used by both createAsync() and initAndShow()...
			final int							x,
			final int							y,
			final Integer						cx,
			final Integer						cy,
			
			// initAndShow() parameters.
			final ManageMobileDevicesDlg		mmdDlg,
			final MobileDevicesInfo				mdInfo,
			final UIObject						showRelativeTo,
			final MobileDeviceRemovedCallback	removedCallback) {
		GWT.runAsync(ManageMobileDevicesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ManageMobileDevicesDlg());
				if (null != mmdDlgClient) {
					mmdDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != mmdDlgClient) {
					// Yes!  Create the dialog.  Note that its
					// construction flow will call the appropriate
					// method off the ManageMobileDevicesDlgClient
					// object.
					new ManageMobileDevicesDlg(mmdDlgClient, autoHide, modal, x, y, cx, cy);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(mmdDlg, mdInfo, showRelativeTo, autoHide, modal, x, y, cx, cy, removedCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageMobileDevicesDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param mmdDlgClient
	 */
	public static void createAsync(ManageMobileDevicesDlgClient mmdDlgClient, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy) {
		doAsyncOperation(mmdDlgClient, autoHide, modal, x, y, cx, cy, null, null, null, null);
	}
	
	public static void createAsync(ManageMobileDevicesDlgClient mmdDlgClient) {
		doAsyncOperation(mmdDlgClient, false, true, 0, 0, null, null, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the manage mobile devices dialog via its
	 * split point.
	 * 
	 * @param mmdDlg
	 * @param mdInfo
	 * @param showRelativeTo
	 * @param removedCallback
	 */
	public static void initAndShow(ManageMobileDevicesDlg mmdDlg, MobileDevicesInfo mdInfo, UIObject showRelativeTo, MobileDeviceRemovedCallback removedCallback) {
		doAsyncOperation(null, false, true, 0, 0, null, null, mmdDlg, mdInfo, showRelativeTo, removedCallback);
	}
	
	public static void initAndShow(ManageMobileDevicesDlg mmdDlg, MobileDevicesInfo mdInfo, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy,  MobileDeviceRemovedCallback removedCallback) {
		doAsyncOperation(null, autoHide, modal, x, y, cx, cy, mmdDlg, mdInfo, null, removedCallback);
	}
	
	public static void initAndShow(ManageMobileDevicesDlg mmdDlg, MobileDevicesInfo mdInfo, int x, int y, Integer cx, Integer cy,  MobileDeviceRemovedCallback removedCallback) {
		doAsyncOperation(null, false, true, x, y, cx, cy, mmdDlg, mdInfo, null, removedCallback);
	}
	
	public static void initAndShow(ManageMobileDevicesDlg mmdDlg, MobileDevicesInfo mdInfo, MobileDeviceRemovedCallback removedCallback) {
		doAsyncOperation(null, false, true, 0, 0, null, null, mmdDlg, mdInfo, null, removedCallback);
	}
	
	public static void initAndShow(ManageMobileDevicesDlg mmdDlg, MobileDevicesInfo mdInfo, UIObject showRelativeTo) {
		// Always use previous form of the method.
		initAndShow(mmdDlg, mdInfo, showRelativeTo, null);
	}
	
	public static void initAndShow(ManageMobileDevicesDlg mmdDlg, MobileDevicesInfo mdInfo, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy) {
		// Always use previous form of the method.
		initAndShow(mmdDlg, mdInfo, autoHide, modal, x, y, cx, cy, null);
	}
	
	public static void initAndShow(ManageMobileDevicesDlg mmdDlg, MobileDevicesInfo mdInfo, int x, int y, Integer cx, Integer cy) {
		// Always use previous form of the method.
		initAndShow(mmdDlg, mdInfo, x, y, cx, cy, null);
	}
	
	public static void initAndShow(ManageMobileDevicesDlg mmdDlg, MobileDevicesInfo mdInfo) {
		// Always use previous form of the method.
		initAndShow(mmdDlg, mdInfo, null, null);
	}
}
