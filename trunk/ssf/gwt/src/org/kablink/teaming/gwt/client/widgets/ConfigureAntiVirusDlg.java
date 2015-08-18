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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetAntiVirusSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetAntiVirusSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.AntiVirusSettingsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TestAntiVirusSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestAntiVirusSettingsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtAntiVirusConfig;
import org.kablink.teaming.gwt.client.util.GwtAntiVirusConfig.GwtAntiVirusType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This dialog is used to configure the anti virus settings.
 * 
 * @author drfoster@novell.com
 */
public class ConfigureAntiVirusDlg extends DlgBox implements EditSuccessfulHandler {
	private AntiVirusSettingsRpcResponseData	m_avData;					// The current anti virus settings once they're read from the server.
	private CheckBox							m_avEnabledCB;				// The basic enablement checkbox.
	private GwtTeamingMessages					m_messages;					// Access to our localized messages.
	private List<HandlerRegistration>			m_registeredEventHandlers;	// Event handlers, when they're registered.
	private PasswordTextBox						m_password;					// The password   <INPUT> widget.
	private TextBox								m_serverUrl;				// The server URL <INPUT> widget.
	private TextBox								m_userName;					// The username   <INPUT> widget.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ConfigureAntiVirusDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height) {
		// Initialize the super class...
		super(autoHide, modal, xPos, yPos, width, height, DlgButtonMode.OkCancel);

		// ...initialize everything else that requires it...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the header, content and footer of this dialog.
		createAllDlgContent(
			m_messages.configureAntiVirusDlg_Header(),
			this,								// The dialog's editSuccessful() handler.
			DlgBox.getSimpleCanceledHandler(),	// The dialog's editCanceled()   handler.
			null);								// null -> No callback data is required by createContent().
	}

	
	/**
	 * Create all the controls that make up the dialog box.
	 * 
	 * Implements the DlgBox.createContent() method.
	 *
	 * @param props
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object props) {
		// Create a panel to hold the dialog's content...
		VerticalPanel vp = new VibeVerticalPanel(null, null);
		vp.addStyleName("vibe-configureAntiVirusDlg-panel");

		// ...add the enable anti virus scanning checkbox...
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-configureAntiVirusDlg-checkboxPanel");
		vp.add(fp);
		m_avEnabledCB = new CheckBox(m_messages.configureAntiVirusDlgEnabledCheckBoxLabel());
		m_avEnabledCB.addStyleName("vibe-configureAntiVirusDlg-checkbox");
		fp.add(m_avEnabledCB);
		
		// ...add a table for the username and password...
		FlexTable ft = new FlexTable();
		ft.addStyleName("vibe-configureAntiVirusDlg-table");
		ft.setCellPadding(2);
		ft.setCellSpacing(2);
		vp.add(ft);

		// ...add the server URL widgets...
		InlineLabel il = new InlineLabel(m_messages.configureAntiVirusDlgServerURLLabel());
		il.addStyleName("vibe-configureAntiVirusDlg-label");
		ft.setWidget(0, 0, il);
		
		m_serverUrl = new TextBox();
		m_serverUrl.addStyleName("vibe-configureAntiVirusDlg-textBox");
		ft.setWidget(0, 1, m_serverUrl);
		
		// ...add the username widgets...
		il = new InlineLabel(m_messages.configureAntiVirusDlgUsernameLabel());
		il.addStyleName("vibe-configureAntiVirusDlg-label");
		ft.setWidget(1, 0, il);
		
		m_userName = new TextBox();
		m_userName.addStyleName("vibe-configureAntiVirusDlg-textBox");
		ft.setWidget(1, 1, m_userName);
		
		// ...add the password widgets...
		il = new InlineLabel(m_messages.configureAntiVirusDlgPasswordLabel());
		il.addStyleName("vibe-configureAntiVirusDlg-label");
		ft.setWidget(2, 0, il);
		
		m_password = new PasswordTextBox();
		m_password.addStyleName("vibe-configureAntiVirusDlg-passwordBox");
		ft.setWidget(2, 1, m_password);
		
		// ...add a test connection button...
		Button testButton = new Button(m_messages.configureAntiVirusDlgTestLabel(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				testConnectionAsync();
			}
		});
		testButton.addStyleName("vibe-configureAntiVirusDlg-testBtn");
		vp.add(testButton);
		
		// ...and return the panel.
		return vp;
	}

	/**
	 * This gets called when the user presses OK.
	 * 
	 * Implements the EditSuccessful.editSuccessful() method.
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object obj) {
		// If the dialog's not in a valid state...
		GwtAntiVirusConfig avConfig = getAntiVirusConfigFromWidgets();
		if (!(isValid(avConfig))) {
			// ...return false to keep the dialog open.  isValid() will
			// ...have told the user about the error(s).
			return false;
		}
		
		// Disable the OK button while we apply the settings...
		setOkEnabled(false);

		// ...send a GWT RPC command to apply them...
		SetAntiVirusSettingsCmd cmd = new SetAntiVirusSettingsCmd(avConfig);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				setOkEnabled(true);
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetAntiVirusSettings());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...and hide the dialog.
				setOkEnabled(true);
				hide();
			}
		});
		
		// Return false to leave the dialog open.  It will be closed
		// when the dialog's selections have been saved.
		return false;
	}

	/*
	 * Returns a GwtAntiVirusConfig object constructed using the
	 * widgets of the dialog.
	 */
	private GwtAntiVirusConfig getAntiVirusConfigFromWidgets() {
		GwtAntiVirusConfig reply = new GwtAntiVirusConfig();
		
		reply.setType(     GwtAntiVirusType.gwava  );
		reply.setEnabled(  m_avEnabledCB.getValue());
		reply.setUsername( m_userName.getValue()   );
		reply.setPassword( m_password.getValue()   );
		reply.setServerUrl(m_serverUrl.getValue()  );

		return reply;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 * 
	 * Implements the DlgBox.getDataFromDlg() method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		return "";	// Unused.
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown.
	 * 
	 * Implements the DlgBox.getFocusWidget() method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return m_userName;	// Input focus starts in the username <INPUT>.
	}
	
	/**
	 * Returns the HelpData structure for running help for this dialog.
	 * 
	 * Overrides the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("configueAntiVirus");
		return helpData;
	}
	
	/*
	 * Validates that the settings in a GwtAntiVirusConfig can be
	 * written to the server.
	 */
	private boolean isValid(GwtAntiVirusConfig avConfig) {
		String      error = null;
		FocusWidget fw    = null;
		
		// Is anti virus scanning enabled?
		if (avConfig.isEnabled()) {
			// Yes!  Did the user supply a server URL?
			if (!(GwtClientHelper.hasString(avConfig.getServerUrl()))) {
				// No!  Tell the user about he problem.
				error = m_messages.configureAntiVirusDlgErrorNoServerUrl();
				fw = m_serverUrl;
			}
			
			// Did the user supply a username?
			else if (!(GwtClientHelper.hasString(avConfig.getUsername()))) {
				// No!  Tell the user about he problem.
				error = m_messages.configureAntiVirusDlgErrorNoUserName();
				fw = m_serverUrl;
			}
			
			// Did the user supply a password?
			else if (!(GwtClientHelper.hasString(avConfig.getPassword()))) {
				// No!  Tell the user about he problem.
				error = m_messages.configureAntiVirusDlgErrorNoPassword();
				fw = m_serverUrl;
			}
		}
		
		// Do we have an error to display?
		boolean hasError = (null != error);
		if (hasError) {
			// Yes!  Put the focus in the widget in error...
			if (null != fw) {
				fw.setFocus(true);
			}
			// ...and display the error.
			GwtClientHelper.deferredAlert(error);
		}
		
		// Return true if we didn't display an error and false
		// otherwise.
		return (!hasError);
	}

	/*
	 * Asynchronously loads the next part of the dialog.
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
	 * Synchronously loads the next part of the dialog.
	 */
	private void loadPart1Now() {
		GetAntiVirusSettingsCmd cmd = new GetAntiVirusSettingsCmd();
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetAntiVirusSettings());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
				m_avData = ((AntiVirusSettingsRpcResponseData) response.getResponseData());
				populateDlgAsync();
			}
		});
	}
	
	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the dialog is detached.
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
	
	/*
	 * Asynchronously populates the dialog.
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
	 * Synchronously populates the dialog.
	 */
	private void populateDlgNow() {
		// Enable the OK push button...
		setOkEnabled(true);

		// ...store the current settings in the dialog's widgets...
		GwtAntiVirusConfig avConfig = m_avData.getAntiVirusConfig();
		m_avEnabledCB.setValue(avConfig.isEnabled()   );
		m_serverUrl.setValue(  avConfig.getServerUrl());
		m_userName.setValue(   avConfig.getUsername() );
		m_password.setValue(   avConfig.getPassword() );
		
		// ...and show the dialog.
		show();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we haven't allocated a list to track events we've registered yet...
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
	 * Asynchronously runs the dialog.
	 */
	private static void runDlgAsync(final ConfigureAntiVirusDlg cavDlg) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cavDlg.runDlgNow();
			}
		});
	}
	
	/*
	 * Synchronously runs the dialog.
	 */
	private void runDlgNow() {
		loadPart1Async();
	}
	
	/*
	 * Asynchronously tests the connection using the values from the
	 * dialog.
	 */
	private void testConnectionAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				testConnectionNow();
			}
		});
	}
	
	/*
	 * Asynchronously tests the connection using the values from the
	 * dialog.
	 */
	public void testConnectionNow() {
		// If the anti virus configuration is disabled...
		GwtAntiVirusConfig avConfig = getAntiVirusConfigFromWidgets();
		if (!(avConfig.isEnabled())) {
			// ..it can't be tested.
			GwtClientHelper.deferredAlert(m_messages.configureAntiVirusDlgErrorDisabled());
			return;
		}
		
		// If the dialog's not in a valid state...
		if (!(isValid(avConfig))) {
			// ...simply bail.  isValid() will have told the user about
			// ...the error(s).
			return;
		}
		
		// Disable the OK button while we test the settings...
		setOkEnabled(false);

		// ...send a GWT RPC command to test them.
		TestAntiVirusSettingsCmd cmd = new TestAntiVirusSettingsCmd(avConfig);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				setOkEnabled(true);
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_TestAntiVirusSettings());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Was the connection successful?
				setOkEnabled(true);
				TestAntiVirusSettingsRpcResponseData reply = ((TestAntiVirusSettingsRpcResponseData) response.getResponseData());
				String message;
				if (reply.isValid()) {
					// Yes!  Tell the user.
					message = m_messages.configureAntiVirusDlgTestSuccess();
				}
				else {
					// No, the connection wasn't successful!  Tell the
					// user about the problem.
					message = reply.getDetails();
					if (GwtClientHelper.hasString(message))
					     message = m_messages.configureAntiVirusDlgTestErrorWithDetails(message);
					else message = m_messages.configureAntiVirusDlgTestErrorWithoutDetails();
				}
				GwtClientHelper.deferredAlert(message);
			}
		});
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the configure anti virus dialog and perform some operation on */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the configure anti virus
	 * dialog asynchronously after it loads. 
	 */
	public interface ConfigureAntiVirusDlgClient {
		void onSuccess(ConfigureAntiVirusDlg cavDlg);
		void onUnavailable();
	}

	/**
	 * Loads the ConfigureAntiVirusDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param cavDlgClient
	 */
	public static void createDlg(
		final boolean						autoHide,		//
		final boolean						modal,			//
		final int							left,			//
		final int							top,			//
		final int							width,			//
		final int							height,			//
		final ConfigureAntiVirusDlgClient	cavDlgClient)	//
	{
		GWT.runAsync( ConfigureAntiVirusDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ConfigureAntiVirusDlg());
				cavDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess() {
				ConfigureAntiVirusDlg cavDlg = new ConfigureAntiVirusDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height);
				cavDlgClient.onSuccess(cavDlg);
			}
		});
	}
	
	/**
	 * Initializes and shows the dialog.
	 * 
	 * @param cavDlg
	 */
	public static void initAndShow(final ConfigureAntiVirusDlg cavDlg) {
		GWT.runAsync( ConfigureAntiVirusDlg.class, new RunAsyncCallback() {			
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ConfigureAntiVirusDlg());
			}

			@Override
			public void onSuccess() {
				runDlgAsync(cavDlg);
			}
		});
	}
}
