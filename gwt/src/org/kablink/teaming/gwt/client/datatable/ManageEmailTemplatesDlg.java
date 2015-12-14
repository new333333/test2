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
package org.kablink.teaming.gwt.client.datatable;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.EmailTemplatesView;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.ViewBase.ViewClient;
import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedCustomizedEmailTemplatesEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetManageTitleEvent;
import org.kablink.teaming.gwt.client.event.ResetVelocityEngineEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteCustomizedEmailTemplatesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteCustomizedEmailTemplatesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetManageEmailTemplatesInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ManageEmailTemplatesInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ResetVelocityEngineCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Filr's 'Manage Email Templates' dialog.
 *  
 * @author drfoster@novell.com
 */
public class ManageEmailTemplatesDlg extends DlgBox
	implements ViewReady,
		// Event handlers implemented by this class.
		AdministrationExitEvent.Handler,
		DeleteSelectedCustomizedEmailTemplatesEvent.Handler,
		FullUIReloadEvent.Handler,
		GetManageTitleEvent.Handler,
		ResetVelocityEngineEvent.Handler
{
	private boolean									m_dlgAttached;						// true when the dialog is attached to the document.       false otherwise.
	private boolean									m_viewReady;						// true once the embedded email templates view is ready.  false otherwise.
	private FlowPanel								m_rootPanel;						// The panel that holds the dialog's contents.
	private GwtTeamingMessages						m_messages;							// Access to Filr's messages.
	private int										m_dlgHeightAdjust = (-1);			// Calculated the first time the dialog is shown.
	private int										m_showX;							//
	private int										m_showY;							//
	private Integer									m_showCX;							//
	private Integer									m_showCY;							//
	private List<HandlerRegistration>				m_metDlg_registeredEventHandlers;	// Event handlers that are currently registered.
	private ManageEmailTemplatesInfoRpcResponseData	m_manageEmailTemplatesInfo;			// Information necessary to run the manage email templates dialog.
	private EmailTemplatesView						m_piView;							// The email templates view.
	private UIObject								m_showRelativeTo;					// The UIObject to show the dialog relative to.  null -> Center the dialog.

	// Constant adjustments to the size of the email templates view so
	// that it properly fits the dialog's content area.
	private final static int DIALOG_HEIGHT_ADJUST_FLOAT	= 35;
	private final static int DIALOG_HEIGHT_ADJUST_FIXED	= 45;
	private final static int DIALOG_WIDTH_ADJUST_FLOAT	= 30;
	private final static int DIALOG_WIDTH_ADJUST_FIXED	= 20;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] metDlg_REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.ADMINISTRATION_EXIT,
		TeamingEvents.DELETE_SELECTED_CUSTOMIZED_EMAIL_TEMPLATES,
		TeamingEvents.FULL_UI_RELOAD,
		TeamingEvents.GET_MANAGE_TITLE,
		TeamingEvents.RESET_VELOCITY_ENGINE,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageEmailTemplatesDlg(ManageEmailTemplatesDlgClient metDlgClient, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy) {
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
		addStyleName("vibe-manageEmailTemplatesDlg");
		createAllDlgContent(
			"",										// The dialog's caption will be set each time it is run.
			DlgBox.getSimpleSuccessfulHandler(),	// The dialog's editSuccessful() handler.
			DlgBox.getSimpleCanceledHandler(),		// The dialog's editCanceled()   handler.
			metDlgClient);							// null -> No callback data is required by createContent(). 
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
		// Can we get the information necessary to manage email
		// templates?
		final ManageEmailTemplatesDlg       metDlg       = this;
		final ManageEmailTemplatesDlgClient metDlgClient = ((ManageEmailTemplatesDlgClient) callbackData);
		GwtClientHelper.executeCommand(new GetManageEmailTemplatesInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetManageEmailTemplatesInfo());

				// ...and tell the caller that the dialog will be
				// ...unavailable.
				metDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Yes!  Store it and tell the caller that the dialog
				// is available.
				m_manageEmailTemplatesInfo = ((ManageEmailTemplatesInfoRpcResponseData) result.getResponseData());
				metDlgClient.onSuccess(metDlg);
			}
		});
		
		// Create the main panel that holds the dialog's content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-manageEmailTemplatesDlg-panel");

		// ...and return it.  Note that it will get populated during
		// ...the initAndShow() call.
		return m_rootPanel;
	}

	/*
	 * Asynchronously deletes the selected email templates.
	 */
	private void deleteSelectedCustomizedEmailTemplatesAsync(final List<EntityId> selectedEmailTemplates) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				deleteSelectedCustomizedEmailTemplatesNow(selectedEmailTemplates);
			}
		});
	}
	
	/*
	 * Synchronously deletes the selected email templates.
	 */
	private void deleteSelectedCustomizedEmailTemplatesNow(final List<EntityId> selectedEmailTemplates) {
		// Show a busy spinner while we clear the adHoc folder
		// settings.
		showDlgBusySpinner();

		// Delete the selected email templates...
		DeleteCustomizedEmailTemplatesCmd cmd = new DeleteCustomizedEmailTemplatesCmd(selectedEmailTemplates);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				hideDlgBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DeleteCustomizedEmailTemplates());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				DeleteCustomizedEmailTemplatesRpcResponseData erList = ((DeleteCustomizedEmailTemplatesRpcResponseData) response.getResponseData());
				if (erList.hasErrors()) {
					// ...display them...
					String errorMsg;
					List<ErrorInfo> errors     = erList.getErrorList();
					int             errorCount = errors.size();
					switch (selectedEmailTemplates.size()) {
					case 1:
						switch (errorCount) {
						case 1:   errorMsg = m_messages.manageEmailTemplatesDlg_failureDeletingEmailTemplates11(); break;	//  1 error, 1 template.
						default:  errorMsg = m_messages.manageEmailTemplatesDlg_failureDeletingEmailTemplates21(); break;	// >1 error, 1 template.
						}
						break;
						
					default:
						switch (errorCount) {
						case 1:   errorMsg = m_messages.manageEmailTemplatesDlg_failureDeletingEmailTemplates12(); break;	//  1 error, >1 template.
						default:  errorMsg = m_messages.manageEmailTemplatesDlg_failureDeletingEmailTemplates22(); break;	// >1 error, >1 template.
						}
						break;
					}
					GwtClientHelper.displayMultipleErrors(errorMsg, errors);
				}

				// ...and hide the busy spinner.
				hideDlgBusySpinner();
				
				// Were any email templates successfully deleted?
				List<EntityId> delList = erList.getSuccessfulDeletes();
				final int count = ((null == delList) ? 0 : delList.size());
				if (0 < count) {
					// Yes!  Reset the view to reflect the deletions.
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
	 * Returns the HelpData for invoking this dialog's 'Help' page.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("email_templates_mng");
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
	 * Called when the manage email templates dialog is attached.
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
	 * Handles DeleteSelectedCustomizedEmailTemplatesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedCustomizedEmailTemplatesEvent.Handler.onDeleteSelectedCustomizedEmailTemplates() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedCustomizedEmailTemplates(DeleteSelectedCustomizedEmailTemplatesEvent event) {
		// If the event doesn't have any selected devices...
		List<EntityId> selIds = event.getSelectedEntities();
		if (!(GwtClientHelper.hasItems(selIds))) {
			// ...use the ones selected in the view.
			selIds = m_piView.getSelectedEntityIds();
		}
		if (!(GwtClientHelper.hasItems(selIds))) {
			return;
		}
		final List<EntityId> selectedEntityIds = selIds;

		// Is the user sure about deleting the selected custom
		// email templates?
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				String confirmQuestion =
					((1 == selectedEntityIds.size()) ?
						m_messages.manageEmailTemplatesDlg_confirmDelete1():
						m_messages.manageEmailTemplatesDlg_confirmDelete2());

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
							// email templates.
							deleteSelectedCustomizedEmailTemplatesAsync(selectedEntityIds);
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					confirmQuestion);
			}
		});
		
	}
	
	/**
	 * Called when the manage email templates dialog is detached.
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
		// If we have a email templates view...
		if (null != m_piView) {
			// ...tell it to reset itself.
			m_piView.resetView();
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
		if (event.getBinderInfo().isEqual(m_manageEmailTemplatesInfo.getEmailTemplatesRootWSInfo())) {
			// ...respond to it.
			event.getManageTitleCallback().manageTitle(m_messages.manageEmailTemplatesDlg());
		}
	}

	/**
	 * Handles ResetVelocityEngineEvent's received by this class.
	 * 
	 * Implements the ResetVelocityEngineEvent.Handler.onResetVelocityEngine() method.
	 * 
	 * @param event
	 */
	@Override
	public void onResetVelocityEngine(ResetVelocityEngineEvent event) {
		// Reset the velocity engine.
		ResetVelocityEngineCmd cmd = new ResetVelocityEngineCmd();
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ResetVelocityEngine());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Nothing to do.
			}
		});
	}
	
	/**
	 * Called when the email templates view reaches the ready state.
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
		m_piView      = null;
		m_rootPanel.clear();
		
		// Create a EmailTemplatesView widget...
		EmailTemplatesView.createAsync(m_manageEmailTemplatesInfo.getEmailTemplatesRootWSInfo(), this, new ViewClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in asynchronous
				// provider.
			}
			
			@Override
			public void onSuccess(ViewBase pwsView) {
				// ...storing it in the view and adding it to the
				// ...panel...
				m_piView = ((EmailTemplatesView) pwsView);
				m_rootPanel.add(m_piView);
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
		if (null == m_metDlg_registeredEventHandlers) {
			// ...allocate one now.
			m_metDlg_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}
		
		// If the list of registered events is empty...
		if (m_metDlg_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				metDlg_REGISTERED_EVENTS,
				this,
				m_metDlg_registeredEventHandlers);
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
		m_piView.resetView();
	}
	
	/*
	 * Asynchronously runs the given instance of the manage email
	 * templates dialog.
	 */
	private static void runDlgAsync(final ManageEmailTemplatesDlg metDlg, final UIObject showRelativeTo, final boolean autoHide, final boolean modal, final int x, final int y, final Integer cx, final Integer cy) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				metDlg.runDlgNow(showRelativeTo, autoHide, modal, x, y, cx, cy);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the manage email
	 * templates dialog.
	 */
	private void runDlgNow(UIObject showRelativeTo, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy) {
		// Store the parameters...
		m_showRelativeTo = showRelativeTo;
		m_showX          = x;
		m_showY          = y;
		m_showCX         = cx;
		m_showCY         = cy;

		// ...update the dialog's styles, caption and
		// ...ManageEmailTemplatesInfo based on the parameters...
		setAutoHideAndModality(autoHide, modal);				// true -> Auto hide.  false -> Not modal.
		Label hcl = getHeaderCaptionLabel();
		hcl.setStyleName("teamingDlgBoxHeader-captionLabel");	// Default style as originally set in DlgBox.createCaption().
		
		setCaption(m_messages.manageEmailTemplatesDlgCaption());
		
		// ...and populate the dialog.
		populateDlgAsync();
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

		// ...set the size of the email templates view.
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
		m_piView.setPixelSize(cx, cy);
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_metDlg_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_metDlg_registeredEventHandlers);
		}
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the manage email templates dialog and perform some operation */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the manage email templates
	 * dialog asynchronously after it loads. 
	 */
	public interface ManageEmailTemplatesDlgClient {
		void onSuccess(ManageEmailTemplatesDlg metDlg);
		void onUnavailable();
	}
	
	/*
	 * Asynchronously loads the ManageEmailTemplatesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync() parameters.
			final ManageEmailTemplatesDlgClient	metDlgClient,
			final boolean 						autoHide,
			final boolean 						modal,
			
			// ...used by both createAsync() and initAndShow()...
			final int							x,
			final int							y,
			final Integer						cx,
			final Integer						cy,
			
			// initAndShow() parameters.
			final ManageEmailTemplatesDlg		metDlg,
			final UIObject						showRelativeTo) {
		GWT.runAsync(ManageEmailTemplatesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ManageEmailTemplatesDlg());
				if (null != metDlgClient) {
					metDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != metDlgClient) {
					// Yes!  Create the dialog.  Note that its
					// construction flow will call the appropriate
					// method off the ManageEmailTemplatesDlgClient
					// object.
					new ManageEmailTemplatesDlg(metDlgClient, autoHide, modal, x, y, cx, cy);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(metDlg, showRelativeTo, autoHide, modal, x, y, cx, cy);
				}
			}
		});
	}
	
	/**
	 * Loads the ManageEmailTemplatesDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param metDlgClient
	 */
	public static void createAsync(ManageEmailTemplatesDlgClient metDlgClient, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy) {
		doAsyncOperation(metDlgClient, autoHide, modal, x, y, cx, cy, null, null);
	}
	
	public static void createAsync(ManageEmailTemplatesDlgClient metDlgClient) {
		doAsyncOperation(metDlgClient, false, true, 0, 0, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the manage email templates dialog via its
	 * split point.
	 * 
	 * @param
	 */
	public static void initAndShow(ManageEmailTemplatesDlg metDlg, UIObject showRelativeTo) {
		doAsyncOperation(null, false, true, 0, 0, null, null, metDlg, showRelativeTo);
	}
	
	public static void initAndShow(ManageEmailTemplatesDlg metDlg, boolean autoHide, boolean modal, int x, int y, Integer cx, Integer cy) {
		doAsyncOperation(null, autoHide, modal, x, y, cx, cy, metDlg, null);
	}
	
	public static void initAndShow(ManageEmailTemplatesDlg metDlg, int x, int y, Integer cx, Integer cy) {
		doAsyncOperation(null, false, true, x, y, cx, cy, metDlg, null);
	}
	
	public static void initAndShow(ManageEmailTemplatesDlg metDlg) {
		doAsyncOperation(null, false, true, 0, 0, null, null, metDlg, null);
	}
}
