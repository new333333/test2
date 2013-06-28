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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.rpc.shared.AddNewFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CreateFolderRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Implements Vibe's add new folder dialog.
 *  
 * @author drfoster@novell.com
 */
public class AddNewFolderDlg extends DlgBox implements EditSuccessfulHandler {
	private FlowPanel			m_dlgPanel;			// The panel holding the dialog's content.
	private GwtTeamingMessages	m_messages;			// Access to Vibe's messages.
	private Long				m_binderId;			// The binder the new folder is to be added to.
	private Long				m_folderTemplateId;	// The ID of the folder template to use to create the folder.
	private TextBox 			m_folderNameInput;	//

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AddNewFolderDlg() {
		// Initialize the super class...
		super(false, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.addNewFolderDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
	}

	/*
	 * Asynchronously runs the new folder creation process.
	 */
	private void createNewFolderAsync() {
		ScheduledCommand doCreate = new ScheduledCommand() {
			@Override
			public void execute() {
				createNewFolderNow();
			}
		};
		Scheduler.get().scheduleDeferred(doCreate);
	}
	
	/*
	 * Synchronously runs the new folder creation process.
	 */
	private void createNewFolderNow() {
		// Did the user supply the name of a folder to create?
		String fn = m_folderNameInput.getValue();
		final String folderName = ((null == fn) ? "" : fn.trim());
		if (0 == folderName.length()) {
			// No!  Tell them about the error and bail.
			GwtClientHelper.deferredAlert(m_messages.addNewFolderDlgError_NoName());
			setButtonsEnabled(true);
			return;
		}

		// Can we add the new folder?
		AddNewFolderCmd cmd = new AddNewFolderCmd(m_binderId, m_folderTemplateId, folderName);
		GwtClientHelper.executeCommand(
				cmd,
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_AddNewFolder(),
					folderName);
				setButtonsEnabled(true);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Perhaps!  Were there any errors returned from the
				// add?
				CreateFolderRpcResponseData responseData = ((CreateFolderRpcResponseData) response.getResponseData());
				List<ErrorInfo> errors = responseData.getErrorList();
				if ((null != errors) && (0 < errors.size())) {
					// Yes!  Display them.
					GwtClientHelper.displayMultipleErrors(m_messages.addNewFolderDlgError_AddFailed(), errors);
					setButtonsEnabled(true);
				}
				
				else {
					// No, there weren't any errors!  Force things to
					// refresh...
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
					GwtTeaming.fireEvent(new FullUIReloadEvent());
					
					// ...and close the dialog.
					setButtonsEnabled(true);
					hide();
				}
			}
		});
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
		// Create and return a FlowPanel to hold the dialog's content.
		m_dlgPanel = new VibeFlowPanel();
		m_dlgPanel.addStyleName("vibe-addNewFolderDlg_Panel");
		return m_dlgPanel;
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
		// Disable the OK button so the user doesn't repeatedly press
		// it...
		setButtonsEnabled(false);
		
		// ...start the folder creation process...
		createNewFolderAsync();
		
		// ...and always return false to leave the dialog open.  If its
		// ...contents validate and the folder gets created, the dialog
		// ...will then be closed.
		return false;
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
		return m_folderNameInput;
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
		// Clear anything already in the dialog's panel.
		m_dlgPanel.clear();

		// Create a horizontal panel to hold the name input widgets...
		HorizontalPanel hp = new VibeHorizontalPanel(null, null);
		hp.addStyleName("vibe-addNewFolderDlg_NamePanel");
		m_dlgPanel.add(hp);

		// ...add a label...
		InlineLabel il = new InlineLabel(m_messages.addNewFolderDlgName());
		il.addStyleName("vibe-addNewFolderDlg_NameLabel");
		hp.add(il);
		hp.setCellVerticalAlignment(il, HasVerticalAlignment.ALIGN_MIDDLE);

		// ...and add an input widget.
		m_folderNameInput = new TextBox();
		m_folderNameInput.addStyleName("vibe-addNewFolderDlg_NameInput");
		m_folderNameInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				// What key is being pressed?
				switch (event.getNativeEvent().getKeyCode()) {
				case KeyCodes.KEY_ESCAPE:
					// Escape!  Simply hide the dialog.
					hide();
					break;
					
				case KeyCodes.KEY_ENTER:
					// Enter!  Treat it like the user pressed OK.
					editSuccessful(null);
					break;
				}
			}
		});
		hp.add(m_folderNameInput);
		hp.setCellVerticalAlignment(m_folderNameInput, HasVerticalAlignment.ALIGN_MIDDLE);
		GwtClientHelper.setFocusDelayed(m_folderNameInput);
		
		// Finally, show the dialog centered on the screen.
		setButtonsEnabled(true);
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the add new folder dialog.
	 */
	private static void runDlgAsync(final AddNewFolderDlg anfDlg, final Long binderId, final Long folderTemplateId) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				anfDlg.runDlgNow(binderId, folderTemplateId);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the add new folder dialog.
	 */
	private void runDlgNow(Long binderId, Long folderTemplateId) {
		// Store the parameters...
		m_binderId         = binderId;
		m_folderTemplateId = folderTemplateId;

		// ...and start populating the dialog.
		populateDlgAsync();
	}

	/*
	 * Enables/disables the buttons on the dialog.
	 */
	private void setButtonsEnabled(boolean enabled) {
		setOkEnabled(    enabled);
		setCancelEnabled(enabled);
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the add new folder dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the add new folder dialog
	 * asynchronously after it loads. 
	 */
	public interface AddNewFolderDlgClient {
		void onSuccess(AddNewFolderDlg anfDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the AddNewFolderDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final AddNewFolderDlgClient anfDlgClient,
			
			// initAndShow parameters,
			final AddNewFolderDlg anfDlg,
			final Long binderId,
			final Long folderTemplateId) {
		GWT.runAsync(AddNewFolderDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_AddNewFolderDlg());
				if (null != anfDlgClient) {
					anfDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != anfDlgClient) {
					// Yes!  Create it and return it via the callback.
					AddNewFolderDlg anfDlg = new AddNewFolderDlg();
					anfDlgClient.onSuccess(anfDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(anfDlg, binderId, folderTemplateId);
				}
			}
		});
	}
	
	/**
	 * Loads the AddNewFolderDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param anfDlgClient
	 */
	public static void createAsync(AddNewFolderDlgClient anfDlgClient) {
		doAsyncOperation(anfDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the add new folder dialog.
	 * 
	 * @param anfDlg
	 * @param binderId
	 * @param folderTemplateId
	 */
	public static void initAndShow(AddNewFolderDlg anfDlg, Long binderId, Long folderTemplateId) {
		doAsyncOperation(null, anfDlg, binderId, folderTemplateId);
	}
}
