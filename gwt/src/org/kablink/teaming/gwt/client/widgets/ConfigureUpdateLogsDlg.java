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
import org.kablink.teaming.gwt.client.rpc.shared.GetUpdateLogsConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUpdateLogsConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UpdateLogsConfig;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
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
 * Implements the Configure Update Logs dialog for the administration
 * console.
 * 
 * @author drfoster@novell.com
 */
public class ConfigureUpdateLogsDlg extends DlgBox 
	implements EditSuccessfulHandler
{
	private CheckBox			m_autoApplyDeferredUpdateLogsCB;	//
	private GwtTeamingMessages	m_messages;							//
	
	/*
	 */
	private ConfigureUpdateLogsDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height) {
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
			GwtTeaming.getMessages().configureUpdateLogsDlg_Header(),
			this,
			null,
			null);
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
		mainPanel.setStyleName("vibe-configUpdateLogsDlg teamingDlgBoxContent");

		// ...add the panel we'll display a hint about update logs
		// ...to...
		FlowPanel hintPanel = new FlowPanel();
		hintPanel.addStyleName("vibe-configUpdateLogsDlg-hintPanel");
		createHintContent(hintPanel);
		mainPanel.add(hintPanel);
		
		// ...add the automatically apply deferred update logs
		// ...checkbox...
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("vibe-configUpdateLogsDlg-checkPanel");
		m_autoApplyDeferredUpdateLogsCB = new CheckBox(m_messages.configureUpdateLogsDlg_AutoUpdateLogs());
		panel.add(m_autoApplyDeferredUpdateLogsCB);
		mainPanel.add(panel);

		// ...and return the main content panel.
		return mainPanel;
	}

	/*
	 * Creates the dialog's hint content.
	 */
	private void createHintContent(FlowPanel hintPanel) {
		Label l = new Label(m_messages.configureUpdateLogsDlg_Hint());
		l.addStyleName("vibe-configUpdateLogsDlg-hintLabel vibe-configUpdateLogsDlg-hintLabelHeader");
		hintPanel.add(l);
	}

	/*
	 */
	private void danceDlg() {
	}
	
	/**
	 * This gets called when the user presses OK.
	 * 
	 * Issues an RPC request to save the update logs configuration.
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
		// Save the update logs configuration.
		UpdateLogsConfig config = ((UpdateLogsConfig) obj);
		saveUpdateLogsConfigAsync(new SaveUpdateLogsConfigCmd(config));
		
		// Returning false will prevent the dialog from closing.  We
		// will close the dialog after we successfully save the
		// update logs configuration.
		return false;
	}
	
	/**
	 * Returns the edited UpdateLogsConfig.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		return new UpdateLogsConfig(m_autoApplyDeferredUpdateLogsCB.getValue());
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
		return m_autoApplyDeferredUpdateLogsCB;
	}
	
	/**
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("autoupdatedeferredlogs");
		
		return helpData;
	}

	/*
	 * Issue an RPC request to get the update logs information from
	 * the server.
	 */
	private void getUpdateLogsConfigFromServer() {
		// Execute an RPC command asking the server for the update
		// logs configuration.
		GwtClientHelper.executeCommand(new GetUpdateLogsConfigCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetUpdateLogsConfig());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				if ((null != response.getResponseData()) && (response.getResponseData() instanceof UpdateLogsConfig)) {
					final UpdateLogsConfig config = ((UpdateLogsConfig) response.getResponseData());
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
		if (null != m_autoApplyDeferredUpdateLogsCB) {
			m_autoApplyDeferredUpdateLogsCB.setValue(false);
		}
		
		// Issue an RPC request to get the update logs information
		// from the server.
		getUpdateLogsConfigFromServer();
	}
	
	/*
	 */
	private void init(UpdateLogsConfig config) {
		if (null != m_autoApplyDeferredUpdateLogsCB) {
			m_autoApplyDeferredUpdateLogsCB.setValue(config.isAutoApplyDeferredUpdateLogs());
		}
		
		danceDlg();
	}

	/*
	 * Asynchronously saves the update logs configuration by
	 * sending the specific RPC command to the server.
	 */
	private void saveUpdateLogsConfigAsync(final SaveUpdateLogsConfigCmd cmd) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				saveUpdateLogsConfigNow(cmd);
			}
		});
	}
	
	/*
	 * Synchronously saves the update logs configuration by
	 * sending the specific RPC command to the server.
	 */
	private void saveUpdateLogsConfigNow(final SaveUpdateLogsConfigCmd cmd) {
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveUpdateLogsConfig());
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
	/* the update logs dialog and perform some operation on it.      */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the 'configure update logs'
	 * dialog asynchronously after it loads. 
	 */
	public interface ConfigureUpdateLogsDlgClient {
		void onSuccess(ConfigureUpdateLogsDlg culDlg);
		void onUnavailable();
	}
	
	/**
	 * Loads the ConfigureUpdateLogsDlg split point and returns an
	 * instance of it via the callback.
	 *
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param culDlgClient
	 */
	public static void createAsync(
		final boolean						autoHide,
		final boolean						modal,
		final int							left,
		final int							top,
		final int							width,
		final int							height,
		final ConfigureUpdateLogsDlgClient	culDlgClient) {
		GWT.runAsync(ConfigureUpdateLogsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ConfigureUpdateLogsDlg());
				if (null != culDlgClient) {
					culDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				ConfigureUpdateLogsDlg culDlg = new ConfigureUpdateLogsDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height);
				culDlgClient.onSuccess(culDlg);
			}
		});
	}
}
