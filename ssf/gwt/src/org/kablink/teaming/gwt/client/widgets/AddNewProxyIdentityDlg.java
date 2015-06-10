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

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.rpc.shared.AddNewProxyIdentityCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CreateProxyIdentityRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Implements Filr's add new proxy identity dialog.
 *  
 * @author drfoster@novell.com
 */
public class AddNewProxyIdentityDlg extends DlgBox implements EditSuccessfulHandler {
	private FlowPanel			m_dlgPanel;				// The panel holding the dialog's content.
	private GwtTeamingMessages	m_messages;				// Access to Filr's messages.
	private TextBox 			m_passwordInput;		//
	private TextBox 			m_passwordVerifyInput;	//
	private TextBox 			m_proxyNameInput;		//
	private TextBox 			m_titleInput;			//

	/*
	 * Interface used to interact with the content of the TextBox
	 * widgets when one of them is in error.
	 */
	private interface GetTextBoxValueError {
		public String getNoValueError();
		public String getTooLongError(int maxLength);
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private AddNewProxyIdentityDlg() {
		// Initialize the super class...
		super(false, true);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.addNewProxyIdentityDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
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
		m_dlgPanel.addStyleName("vibe-addNewProxyIdentityDlg_Panel");
		return m_dlgPanel;
	}

	/*
	 * Creates a stylized TextBox widget and adds it to the provided
	 * FlexTable.
	 * 
	 * The TextBox widget is returned.
	 */
	private TextBox createInputWidget(FlexTable grid, int row, String labelText, String labelStyle, String tbStyle, boolean isPassword) {
		// Get a FlexCellFormatter for working with the Grid.
		FlexCellFormatter gridCellFmt = grid.getFlexCellFormatter();
		
		// Create the label for the TextBox and add it to the Grid...
		InlineLabel il = new InlineLabel(labelText);
		il.addStyleName(labelStyle);
		grid.setWidget(                  row, 0, il                               );
		gridCellFmt.setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);

		// ...and create the TextBox widget and add it to the Grid.
		TextBox reply = new TextBox();
		if (isPassword)
		     reply = new PasswordTextBox();
		else reply = new TextBox();
		reply.addStyleName(tbStyle);
		reply.addKeyDownHandler(new KeyDownHandler() {
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
		grid.setWidget(                  row, 1, reply                            );
		gridCellFmt.setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		
		// If we get here, reply refers to the TextBox we created.
		// Return it.
		return reply;
	}
	
	/*
	 * Asynchronously runs the new proxy identity creation process.
	 */
	private void createNewProxyIdentityAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				createNewProxyIdentityNow();
			}
		});
	}
	
	/*
	 * Synchronously runs the new proxy identity creation process.
	 */
	private void createNewProxyIdentityNow() {
		// Did the user supply a valid title for the proxy identity?
		String title = getTextBoxValue(m_titleInput, false, GwtConstants.MAX_PROXY_IDENTITY_TITLE_LENGTH, new GetTextBoxValueError() {
			@Override
			public String getNoValueError() {return null;}	// null -> Error doesn't matter since this input doesn't have to have a value.

			@Override
			public String getTooLongError(int maxLength) {return m_messages.addNewProxyIdentityDlgError_TitleTooLong(maxLength);}
		});
		if (null == title) {
			// No!  The user will have been told about the error.
			// Simply bail.
			return;
		}
		
		// Did the user supply a valid name for the proxy identity?
		final String proxyName = getTextBoxValue(m_proxyNameInput, true, GwtConstants.MAX_PROXY_IDENTITY_NAME_LENGTH, new GetTextBoxValueError() {
			@Override
			public String getNoValueError() {return m_messages.addNewProxyIdentityDlgError_NoName();}

			@Override
			public String getTooLongError(int maxLength) {return m_messages.addNewProxyIdentityDlgError_NameTooLong(maxLength);}
		});
		if (null == proxyName) {
			// No!  The user will have been told about the error.
			// Simply bail.
			return;
		}
		
		// Did the user supply a valid password for the proxy identity?
		String password = getTextBoxValue(m_passwordInput, true, GwtConstants.MAX_PROXY_IDENTITY_PASSWORD_LENGTH, new GetTextBoxValueError() {
			@Override
			public String getNoValueError() {return m_messages.addNewProxyIdentityDlgError_NoPassword();}

			@Override
			public String getTooLongError(int maxLength) {return m_messages.addNewProxyIdentityDlgError_PasswordTooLong(maxLength);}
		});
		if (null == password) {
			// No!  The user will have been told about the error.
			// Simply bail.
			return;
		}

		// Did the user supply a valid password verification for the
		// proxy identity?
		String passwordVerify = getTextBoxValue(m_passwordVerifyInput, true, GwtConstants.MAX_PROXY_IDENTITY_PASSWORD_LENGTH, new GetTextBoxValueError() {
			@Override
			public String getNoValueError() {return m_messages.addNewProxyIdentityDlgError_NoPasswordVerify();}

			@Override
			public String getTooLongError(int maxLength) {return m_messages.addNewProxyIdentityDlgError_PasswordVerifyTooLong(maxLength);}
		});
		if (null == passwordVerify) {
			// No!  The user will have been told about the error.
			// Simply bail.
			return;
		}
		
		// Does the password match the password verification?
		if (!(password.equals(passwordVerify))) {
			// No!  Tell the user about the error and bail.
			GwtClientHelper.deferredAlert(m_messages.addNewProxyIdentityDlgError_PasswordsDontMatch());
			return;
		}

		// We have the parts we need.  Can we use them to add the new
		// proxy identity?
		showDlgBusySpinner();
		AddNewProxyIdentityCmd cmd = new AddNewProxyIdentityCmd(title, proxyName, password);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Restore the dialog's functionality...
				hideDlgBusySpinner();
				setButtonsEnabled(true);

				// ...and tell the user about the problem.
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_AddNewProxyIdentity(),
					proxyName);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Perhaps!  Restore the dialog's functionality.
				hideDlgBusySpinner();
				setButtonsEnabled(true);
				
				// Were there any errors returned from the add?
				CreateProxyIdentityRpcResponseData responseData = ((CreateProxyIdentityRpcResponseData) response.getResponseData());
				List<ErrorInfo> errors = responseData.getErrorList();
				if ((null != errors) && (0 < errors.size())) {
					// Yes!  Display them.
					GwtClientHelper.displayMultipleErrors(m_messages.addNewProxyIdentityDlgError_AddFailed(), errors);
				}
				
				else {
					// No, there weren't any errors!  Force things to
					// refresh and close the dialog.
					FullUIReloadEvent.fireOneAsync();
					hide();
				}
			}
		});
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
		
		// ...start the proxy identity creation process...
		createNewProxyIdentityAsync();
		
		// ...and always return false to leave the dialog open.  If its
		// ...contents validate and the proxy identity gets created,
		// ...the dialog will then be closed.
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
		return m_titleInput;
	}

	/*
	 * Returns a range checked value from a TextBox widget.
	 */
	private String getTextBoxValue(TextBox tb, boolean valueRequired, int maxLength, GetTextBoxValueError gtbvErrorCallback) {
		// Did the user supply a value when one is required?
		String reply = tb.getValue();
		reply = ((null == reply) ? "" : reply.trim());
		int replyLength = reply.length();
		if (valueRequired && (0 == replyLength)) {
			// No!  Tell them about the error and bail.
			GwtClientHelper.deferredAlert(gtbvErrorCallback.getNoValueError());
			setButtonsEnabled(true);
			return null;
		}

		// Is the user supplied value for this too long?
		if (maxLength < replyLength) {
			// Yes!  Tell them about the error and bail.
			GwtClientHelper.deferredAlert(gtbvErrorCallback.getTooLongError(maxLength));
			setButtonsEnabled(true);
			return null;
		}

		// If we get here, reply refers to the non-null, range checked
		// value requested.  Return it.
		return reply;
	}
	
    /**
     * Called after the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingEnded() method.
     */
	@Override
    protected void okBtnProcessingEnded() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
    /**
     * Called before the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingStarted() method.
     */
	@Override
    protected void okBtnProcessingStarted() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
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
		// Clear anything already in the dialog's panel.
		m_dlgPanel.clear();

		// Create a FlexTable to hold the input widgets...
		VibeFlexTable grid = new VibeFlexTable();
		grid.addStyleName("vibe-addNewProxyIdentityDlg_Grid");
		m_dlgPanel.add(grid);

		// ...and create the input widgets.
		m_titleInput          = createInputWidget(grid, 0, m_messages.addNewProxyIdentityDlgTitle(),          "vibe-addNewProxyIdentityDlg_NameLabel", "vibe-addNewProxyIdentityDlg_NameInput", false);	// false -> Normal TextBox.
		m_proxyNameInput      = createInputWidget(grid, 1, m_messages.addNewProxyIdentityDlgName(),           "vibe-addNewProxyIdentityDlg_NameLabel", "vibe-addNewProxyIdentityDlg_NameInput", false);	// false -> Normal TextBox.
		m_passwordInput       = createInputWidget(grid, 2, m_messages.addNewProxyIdentityDlgPassword(),       "vibe-addNewProxyIdentityDlg_NameLabel", "vibe-addNewProxyIdentityDlg_NameInput", true );	// true  -> PasswordTextBox.
		m_passwordVerifyInput = createInputWidget(grid, 3, m_messages.addNewProxyIdentityDlgPasswordVerify(), "vibe-addNewProxyIdentityDlg_NameLabel", "vibe-addNewProxyIdentityDlg_NameInput", true );	// true  -> PasswordTextBox.
		
		// Finally, show the dialog, centered on the screen.
		setButtonsEnabled(true);
		center();
	}
	
	/*
	 * Asynchronously runs the given instance of the add new proxy
	 * identity dialog.
	 */
	private static void runDlgAsync(final AddNewProxyIdentityDlg anpiDlg) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				anpiDlg.runDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously runs this instance of the add new proxy identity
	 * dialog.
	 */
	private void runDlgNow() {
		// Populate the dialog.
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
	/* the add new proxy identity dialog and perform some operation  */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the add new proxy identity
	 * dialog asynchronously after it loads. 
	 */
	public interface AddNewProxyIdentityDlgClient {
		void onSuccess(AddNewProxyIdentityDlg anpiDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the AddNewProxyIdentityDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync parameters.
			final AddNewProxyIdentityDlgClient	anpiDlgClient,
			
			// initAndShow parameters,
			final AddNewProxyIdentityDlg	anpiDlg) {
		GWT.runAsync(AddNewProxyIdentityDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_AddNewProxyIdentityDlg());
				if (null != anpiDlgClient) {
					anpiDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != anpiDlgClient) {
					// Yes!  Create it and return it via the callback.
					AddNewProxyIdentityDlg anpiDlg = new AddNewProxyIdentityDlg();
					anpiDlgClient.onSuccess(anpiDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(anpiDlg);
				}
			}
		});
	}
	
	/**
	 * Loads the AddNewProxyIdentityDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param anpiDlgClient
	 */
	public static void createAsync(AddNewProxyIdentityDlgClient anpiDlgClient) {
		doAsyncOperation(anpiDlgClient, null);
	}
	
	/**
	 * Initializes and shows the add new proxy identity dialog.
	 * 
	 * @param anpiDlg
	 */
	public static void initAndShow(AddNewProxyIdentityDlg anpiDlg) {
		doAsyncOperation(null, anpiDlg);
	}
}
