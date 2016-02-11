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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetLimitedUserVisibilityDisplayCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetPrincipalInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PrincipalInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.LimitedUserVisibilityInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;

/**
 * Implements a dialog to modify the limited user visibility settings
 * on a principal (user or group.)
 *  
 * @author drfoster@novell.com
 */
public class ModifyLimitedUserVisibilityDlg extends DlgBox implements EditCanceledHandler, EditSuccessfulHandler {
	private GwtTeamingMessages					m_messages;			// Access to our localized string resources.
	private LimitedUserVisibilityInfo			m_luvInfo;			// The LimitedUserVisibilityInfo describing what's being modified.
	private ModifyLimitedUserVisibilityCallback	m_mluvCallback;		// Callback to tell the caller about dialog life cycle events (OK, Cancel, ...)
	private PrincipalInfoRpcResponseData		m_principalInfo;	// Information about the Principal obtained via a GWT RPC call when the dialog is shown.
	private RadioButton							m_clearRB;			// The 'Remove Visibility Setting'              radio button.
	private RadioButton							m_limitedRB;		// The 'Can Only See Members of Groups I am In' radio button.
	private RadioButton							m_overrideRB;		// The 'Override - Can See Everybody'           radio button.
	private VibeFlowPanel						m_fp;				// The panel holding the dialog's content.

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ModifyLimitedUserVisibilityDlg() {
		// Initialize the superclass...
		super(true, false, DlgButtonMode.OkCancel);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.modifyLimitedUserVisibilityDlgHeader(),	// The dialog's header.
			this,												// The dialog's EditSuccessfulHandler.
			this,												// The dialog's EditCanceledHandler.
			null);												// Create callback data.  Unused.
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
		// Create and return a panel to hold the dialog's content.
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-modifyLimitedUserVisibilityDlg-rootPanel");
		return m_fp;
	}

	/**
	 * Called when the user presses the dialog's Cancel button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() method.
	 */
	@Override
	public boolean editCanceled() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// Tell the caller the dialog was canceled and close
				// the dialog.
				m_mluvCallback.onEditCanceled();
				hide();
			}
		});
		
		// Return false.  Well close the dialog after we tell the
		// caller about the cancel.
		return false;
	}

	/**
	 * Called when the user presses the dialog's OK button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() method.
	 * 
	 * @param unused
	 */
	@Override
	public boolean editSuccessful(Object unused) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				editSuccessfulImpl();
			}
		});
		
		// Return false.  Well close the dialog after we tell the
		// caller about the OK.
		return false;
	}
	
	/*
	 * Called when the user presses the dialog's OK button.
	 */
	private void editSuccessfulImpl() {
		// Did the user change anything?
		final boolean limited  = m_limitedRB.getValue();
		final boolean override = m_overrideRB.getValue();
		if ((limited == m_luvInfo.isLimited()) && (override == m_luvInfo.isOverride())) {
			// No!  Simply close the dialog as though the user canceled
			// it.
			m_mluvCallback.onEditCanceled();
			hide();
			return;
		}
		
		GetLimitedUserVisibilityDisplayCmd cmd = new GetLimitedUserVisibilityDisplayCmd(limited, override);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem.
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetLimitedUserVisibilityDisplay());
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Construct a LimitedUserVisibilityInfo with the
				// current settings...
				StringRpcResponseData pInfo = ((StringRpcResponseData) result.getResponseData());
				final LimitedUserVisibilityInfo luvApply = new LimitedUserVisibilityInfo(
					limited,
					override,
					m_luvInfo.getPrincipalId(),
					pInfo.getStringValue());
				
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// ...use it to tell the caller the dialog was
						// ...OK'ed...
						if (m_mluvCallback.onEditSuccessful(m_principalInfo.getEntityid(), luvApply)) {
							// ...and put it into effect and close the
							// ...dialog if the caller says we're done.
							m_luvInfo = luvApply;
							hide();
						}
					}
				});
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
		return null;
	}

	/*
	 * Asynchronously populates the contents of the dialog.
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
	 * Synchronously populates the contents of the dialog.
	 */
	private void loadPart1Now() {
		// Get the principal information for this principal.
		final Long pId = m_luvInfo.getPrincipalId();
		GetPrincipalInfoCmd cmd = new GetPrincipalInfoCmd(pId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Tell the user about the problem.
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetPrincipalInfo(),
					pId);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Did we get an error trying to retrieve the info?
				PrincipalInfoRpcResponseData pInfo = ((PrincipalInfoRpcResponseData) result.getResponseData());
				String error = pInfo.getError();
				if (GwtClientHelper.hasString(error)) {
					// Yes!  Display it and bail.
					GwtClientHelper.deferredAlert(error);
					return;
				}
				
				// No error!  Store the info and use it to populate
				// the dialog.
				m_principalInfo = pInfo;
				populateDlgAsync();
			}
		});
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
		m_fp.clear();

		// Add a hint for the dialog...
		EntityId eId = m_principalInfo.getEntityid();
		String name = m_principalInfo.getName();
		String hint;
		if (eId.isGroup())
		     hint = m_messages.modifyLimitedUserVisibilityDlgHint_Group(name);
		else hint = m_messages.modifyLimitedUserVisibilityDlgHint_User( name);
		Label l = new Label(hint);
		l.addStyleName("vibe-modifyLimitedUserVisibilityDlg-hint");
		m_fp.add(l);

		// ...add the radio buttons for the user to make changes...
		m_limitedRB = new RadioButton("limitedUserVisibilitySettings", m_messages.modifyLimitedUserVisibilityDlgRB_Limited());
		m_limitedRB.addStyleName("vibe-modifyLimitedUserVisibilityDlg-rb");
		m_fp.add(m_limitedRB);
		m_overrideRB = new RadioButton("limitedUserVisibilitySettings", m_messages.modifyLimitedUserVisibilityDlgRB_Override());
		m_overrideRB.addStyleName("vibe-modifyLimitedUserVisibilityDlg-rb");
		m_fp.add(m_overrideRB);
		m_clearRB = new RadioButton("limitedUserVisibilitySettings", m_messages.modifyLimitedUserVisibilityDlgRB_Clear());
		m_clearRB.addStyleName("vibe-modifyLimitedUserVisibilityDlg-rb");
		m_fp.add(m_clearRB);
		if      (m_luvInfo.isOverride()) m_overrideRB.setValue(true);
		else if (m_luvInfo.isLimited())  m_limitedRB.setValue( true);
		else                             m_clearRB.setValue(   true);

		// ...and show the dialog.
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the modify limited
	 * user visibility dialog.
	 */
	private static void runDlgAsync(final ModifyLimitedUserVisibilityDlg mluvDlg, final LimitedUserVisibilityInfo luvInfo, final ModifyLimitedUserVisibilityCallback mluvCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mluvDlg.runDlgNow(luvInfo, mluvCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the modify limited
	 * user visibility dialog.
	 */
	private void runDlgNow(final LimitedUserVisibilityInfo luvInfo, final ModifyLimitedUserVisibilityCallback mluvCallback) {
		// Store the parameters...
		m_luvInfo      = luvInfo;
		m_mluvCallback = mluvCallback;

		// ...and load the dialog's content.
		loadPart1Async();
	}


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the modify limited user visibility dialog and perform some    */
	/* operation on it.                                              */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the limited user visibility
	 * dialog asynchronously after it loads. 
	 */
	public interface ModifyLimitedUserVisibilityDlgClient {
		void onSuccess(ModifyLimitedUserVisibilityDlg mluvDlg);
		void onUnavailable();
	}
	
	/**
	 * Callback interface to tell the caller about dialog life cycle
	 * events. 
	 */
	public interface ModifyLimitedUserVisibilityCallback {
		public boolean onEditSuccessful(EntityId luvEid, LimitedUserVisibilityInfo luvInfo);
		public void    onEditCanceled();
	}

	/*
	 * Asynchronously loads the ModifyLimitedUserVisibilityDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final ModifyLimitedUserVisibilityDlgClient	mluvDlgClient,
			
			// initAndShow parameters,
			final ModifyLimitedUserVisibilityDlg		mluvDlg,
			final LimitedUserVisibilityInfo				luvInfo,
			final ModifyLimitedUserVisibilityCallback	mluvCallback) {
		GWT.runAsync(ModifyLimitedUserVisibilityDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ModifyLimitedUserVisibilityDlg());
				if (null != mluvDlgClient) {
					mluvDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != mluvDlgClient) {
					// Yes!  Create it and return it via the callback.
					ModifyLimitedUserVisibilityDlg mluvDlg = new ModifyLimitedUserVisibilityDlg();
					mluvDlgClient.onSuccess(mluvDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(mluvDlg, luvInfo, mluvCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the ModifyLimitedUserVisibilityDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param mluvDlgClient
	 */
	public static void createAsync(ModifyLimitedUserVisibilityDlgClient mluvDlgClient) {
		doAsyncOperation(mluvDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the limited user visibility dialog.
	 * 
	 * @param mluvDlg
	 * @param luvInfo
	 */
	public static void initAndShow(ModifyLimitedUserVisibilityDlg mluvDlg, LimitedUserVisibilityInfo luvInfo, ModifyLimitedUserVisibilityCallback mluvCallback) {
		doAsyncOperation(null, mluvDlg, luvInfo, mluvCallback);
	}
}
