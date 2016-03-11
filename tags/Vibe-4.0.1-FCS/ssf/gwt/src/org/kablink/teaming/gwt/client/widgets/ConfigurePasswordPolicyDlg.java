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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetPasswordPolicyConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetPasswordPolicyInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PasswordPolicyInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SavePasswordPolicyConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PasswordPolicyConfig;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Implements the Configure Password Policy dialog for the
 * administration console.
 * 
 * @author drfoster@novell.com
 */
public class ConfigurePasswordPolicyDlg extends DlgBox 
	implements EditSuccessfulHandler
{
	private CheckBox							m_passwordPolicyEnabledCB;	//
	private FlowPanel							m_hintPanel;				//
	private GwtTeamingMessages					m_messages;					//
	private PasswordPolicyInfoRpcResponseData	m_passwordPolicyInfo;		//
	
	/*
	 */
	private ConfigurePasswordPolicyDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height, ConfigurePasswordPolicyDlgClient	cppDlgClient) {
		// Initialize the super class...
		super(
			autoHide,
			modal,
			xPos,
			yPos,
			new Integer(width),
			new Integer(height),
			DlgButtonMode.OkCancel);
		
		// ...initialize the data members...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the header, content and footer of this dialog.
		createAllDlgContent(
			GwtTeaming.getMessages().configurePasswordPolicyDlg_Header(),
			this,
			null,
			cppDlgClient);
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
	public Panel createContent(Object props) {
		// Create the dialog's main content panel...
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("vibe-configPasswordPolicyDlg teamingDlgBoxContent");

		// ...add the panel we'll display a hint about password policy
		// ...to...
		m_hintPanel = new FlowPanel();
		m_hintPanel.addStyleName("vibe-configPasswordPolicyDlg-hintPanel");
		mainPanel.add(m_hintPanel);
		
		// ...add the enable password complexity checking checkbox...
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("vibe-configPasswordPolicyDlg-checkPanel");
		m_passwordPolicyEnabledCB = new CheckBox(m_messages.configurePasswordPolicyDlg_EnablePasswordComplexityChecking());
		panel.add(m_passwordPolicyEnabledCB);
		mainPanel.add(panel);

		// ...request the password policy information to populate the
		// ...rest of the content...
		loadPart1Async((ConfigurePasswordPolicyDlgClient) props);

		// ...and return the main content panel.
		return mainPanel;
	}

	/*
	 * Asynchronously completes the creation of the dialog's content.
	 */
	private void createHintContentAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				createHintContentNow();
			}
		});
	}
	
	/*
	 * Synchronously completes the creation of the dialog's content.
	 */
	private void createHintContentNow() {
		Label l = new Label(m_messages.configurePasswordPolicyDlg_Hint());
		l.addStyleName("vibe-configPasswordPolicyDlg-hintLabelHeader");
		m_hintPanel.add(l);
		
		if (m_passwordPolicyInfo.isExpirationEnabled()) {
			l = new Label(m_messages.configurePasswordPolicyDlg_Hint_Expiration(m_passwordPolicyInfo.getExpirationDays()));
			l.addStyleName("vibe-configPasswordPolicyDlg-hintLabel");
			m_hintPanel.add(l);
		}
		
		l = new Label(m_messages.configurePasswordPolicyDlg_Hint_MinimumLength(m_passwordPolicyInfo.getMinimumLength()));
		l.addStyleName("vibe-configPasswordPolicyDlg-hintLabel");
		m_hintPanel.add(l);
		
		l = new Label(m_messages.configurePasswordPolicyDlg_Hint_NoName());
		l.addStyleName("vibe-configPasswordPolicyDlg-hintLabel");
		m_hintPanel.add(l);
		
		l = new Label(m_messages.configurePasswordPolicyDlg_Hint_AtLeast3());
		l.addStyleName("vibe-configPasswordPolicyDlg-hintLabel");
		m_hintPanel.add(l);
		
		l = new Label(m_messages.configurePasswordPolicyDlg_Hint_Lower());
		l.addStyleName("vibe-configPasswordPolicyDlg-hintLabel3of");
		m_hintPanel.add(l);
		
		l = new Label(m_messages.configurePasswordPolicyDlg_Hint_Upper());
		l.addStyleName("vibe-configPasswordPolicyDlg-hintLabel3of");
		m_hintPanel.add(l);
		
		l = new Label(m_messages.configurePasswordPolicyDlg_Hint_Number());
		l.addStyleName("vibe-configPasswordPolicyDlg-hintLabel3of");
		m_hintPanel.add(l);
		
		StringBuffer symbolsBuffer = new StringBuffer();
		char[]       symbolsArray  = m_passwordPolicyInfo.getSymbols();
		for (int i = 0; i < symbolsArray.length; i += 1) {
			if (0 < i) {
				symbolsBuffer.append(" ");
			}
			symbolsBuffer.append(symbolsArray[i]);
		}
		l = new Label(m_messages.configurePasswordPolicyDlg_Hint_Symbol(symbolsBuffer.toString()));
		l.addStyleName("vibe-configPasswordPolicyDlg-hintLabel3of");
		m_hintPanel.add(l);
	}

	/*
	 */
	private void danceDlg() {
	}
	
	/**
	 * This gets called when the user presses OK.
	 * 
	 * Issues an RPC request to save the password policy configuration.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object obj) {
		// Create the GWT RPC command to save the password policy
		// configuration.
		PasswordPolicyConfig config = ((PasswordPolicyConfig) obj);
		final SavePasswordPolicyConfigCmd cmd = new SavePasswordPolicyConfigCmd(config);
		
		// Is the password policy being enabled and do we support
		// password expirations?
		if (config.isPasswordPolicyEnabled() && m_passwordPolicyInfo.isExpirationEnabled()) {
			// Yes!  Does the user want to force all local and external
			// users to change their password the next time they login?
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
								// Ignored.  We don't really care when
								// the dialog is ready.
							}

							@Override
							public void accepted() {
								// Yes!  Set the command to force a
								// password change and send it.
								cmd.setForcePasswordChange(true);
								savePasswordPolicyConfigAsync(cmd);
							}

							@Override
							public void rejected() {
								// No!  Send the command as is.
								savePasswordPolicyConfigAsync(cmd);
							}
						},
						m_messages.configurePasswordPolicyDlg_ConfirmForcePasswordChanges());
				}
			});
		}
		else {
			// No, password policy is being disabled or we don't
			// support password expiration!  Simply send the command.
			savePasswordPolicyConfigAsync(cmd);
		}
		
		// Returning false will prevent the dialog from closing.  We
		// will close the dialog after we successfully save the
		// password policy configuration.
		return false;
	}
	
	/**
	 * Returns the edited PasswordPolicyConfig.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		return new PasswordPolicyConfig(m_passwordPolicyEnabledCB.getValue());
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
		return m_passwordPolicyEnabledCB;
	}
	
	/**
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("passwordpolicy");
		
		return helpData;
	}

	/*
	 * Issue an RPC request to get the password policy information from
	 * the server.
	 */
	private void getPasswordPolicyConfigFromServer() {
		// Execute an RPC command asking the server for the password
		// policy information.
		GwtClientHelper.executeCommand(new GetPasswordPolicyConfigCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetPasswordPolicyConfig());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				if ((null != response.getResponseData()) && (response.getResponseData() instanceof PasswordPolicyConfig)) {
					final PasswordPolicyConfig config = ((PasswordPolicyConfig) response.getResponseData());
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							init(config);
						}
					});
				}
			}
		});
	}
	
	/**
	 */
	public void init() {
		if (null != m_passwordPolicyEnabledCB) {
			m_passwordPolicyEnabledCB.setValue(false);
		}
		
		// Issue an RPC request to get the password policy information
		// from the server.
		getPasswordPolicyConfigFromServer();
	}
	
	/*
	 */
	private void init(PasswordPolicyConfig config) {
		if (null != m_passwordPolicyEnabledCB) {
			m_passwordPolicyEnabledCB.setValue(config.isPasswordPolicyEnabled());
		}
		
		danceDlg();
	}

	/*
	 * Asynchronously loads the password policy information and
	 * completes the construction of the dialog's content.
	 */
	private void loadPart1Async(final ConfigurePasswordPolicyDlgClient cppDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(cppDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously loads the password policy information and
	 * completes the construction of the dialog's content.
	 */
	private void loadPart1Now(final ConfigurePasswordPolicyDlgClient cppDlgClient) {
		// Execute an RPC command asking the server for the password
		// policy information.
		final ConfigurePasswordPolicyDlg cppDlg = this;
		GwtClientHelper.executeCommand(new GetPasswordPolicyInfoCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetPasswordPolicyInfo());
				cppDlgClient.onUnavailable();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				m_passwordPolicyInfo = ((PasswordPolicyInfoRpcResponseData) response.getResponseData());
				createHintContentAsync();
				cppDlgClient.onSuccess(cppDlg);
			}
		});
	}
	
	/*
	 * Asynchronously saves the password policy configuration by
	 * sending the specific RPC command to the server.
	 */
	private void savePasswordPolicyConfigAsync(final SavePasswordPolicyConfigCmd cmd) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				savePasswordPolicyConfigNow(cmd);
			}
		});
	}
	
	/*
	 * Synchronously saves the password policy configuration by
	 * sending the specific RPC command to the server.
	 */
	private void savePasswordPolicyConfigNow(final SavePasswordPolicyConfigCmd cmd) {
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SavePasswordPolicyConfig());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				if ((null != response.getResponseData()) && (response.getResponseData() instanceof BooleanRpcResponseData)) {
					BooleanRpcResponseData responseData = ((BooleanRpcResponseData) response.getResponseData());
					if (responseData.getBooleanValue()) {
						hide();
					}
				}
			}
		});
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the password policy dialog and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the 'configure password
	 * policy' dialog asynchronously after it loads. 
	 */
	public interface ConfigurePasswordPolicyDlgClient {
		void onSuccess(ConfigurePasswordPolicyDlg cppDlg);
		void onUnavailable();
	}
	
	/**
	 * Loads the ConfigurePasswordPolicyDlg split point and returns an
	 * instance of it via the callback.
	 *
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param cppDlgClient
	 */
	public static void createAsync(
		final boolean							autoHide,
		final boolean							modal,
		final int								left,
		final int								top,
		final int								width,
		final int								height,
		final ConfigurePasswordPolicyDlgClient	cppDlgClient) {
		GWT.runAsync(ConfigurePasswordPolicyDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ConfigurePasswordPolicyDlg());
				if (null != cppDlgClient) {
					cppDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Upon successful construction of the dialog, the
				// client's onSuccess() handler will be called.  See
				// the implementation of loadPart1Now() above.
				new ConfigurePasswordPolicyDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height,
					cppDlgClient);
			}
		});
	}
}
