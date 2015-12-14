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
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtKeyShieldConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetKeyShieldConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveKeyShieldConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveKeyShieldConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TestKeyShieldConnectionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestKeyShieldConnectionResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implementation of the Administration Console's dialog for editing
 * the KeyShield SSO settings.
 *  
 * @author drfoster@novell.com
 */
public class EditKeyShieldConfigDlg extends DlgBox
	implements KeyPressHandler,
		// Event handlers implemented by this class.
		EditSuccessfulHandler,
		EditCanceledHandler
{
	private final static boolean	HARD_CODE_SSO_ERROR_STRINGS	= false;	// Jong requested these be hard coded.  That felt wrong so we pull them out of the resources.  Changing this to true will use hard coded versions.
	
	private final static int	ERROR_MESSAGE_MAXIMUM_LENGTH	= 128;
	private final static int	ERROR_MESSAGE_VISIBLE_LENGTH	=  90;
	
	private boolean						m_testConnectionInProgress;			//
	private CheckBox					m_enableKeyShieldCheckbox;			//
	private CheckBox					m_hardwareTokenRequiredCheckbox;	//
	private CheckBox					m_nonSsoAllowedForLdapUserCheckbox;	//
	private GwtKeyShieldConfig			m_config;							//
	private GwtTeamingMessages			m_messages;							//
	private Label						m_usernameAttributeAliasHint;		//
	private TextBox						m_apiAuthKeyTextBox;				//
	private TextBox						m_authConnectorNamesTextBox;		//
	private TextBox						m_serverUrlTextBox;					//
	private TextBox						m_ssoErrorMessageForWeb;			//
	private TextBox						m_ssoErrorMessageForWebdav;			//
	private TextBox						m_timeoutTextBox;					//
	private TextBox						m_usernameAttributeAliasTextBox;	//
	private FlowPanel					m_alertPanel;						//
	private FlowPanel					m_stackTracePanel;					//
	private List<HandlerRegistration>	m_registeredEventHandlers;			//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditKeyShieldConfigDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height) {
		// Initialize the super class..
		super(
			autoHide,
			modal,
			xPos,
			yPos,
			width,
			height,
			DlgButtonMode.OkCancel);
		
		// ...initialize everything else that requires it...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the header, content and footer of this dialog
		// ...box.
		createAllDlgContent(
			GwtTeaming.getMessages().editKeyShieldConfigDlg_Header(),
			this,	// Edit successful handler.
			this,	// Edit canceled   handler.
			null);
	}

	/**
	 * Create all the controls that make up the dialog box.
	 * 
	 * @param props
	 */
	@Override
	public Panel createContent(Object props) {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("teamingDlgBoxContent");

		m_enableKeyShieldCheckbox = new CheckBox(m_messages.editKeyShieldConfigDlg_EnableKeyShieldLabel());
		mainPanel.add(m_enableKeyShieldCheckbox);
		
		FlexTable table = new FlexTable();
		table.setCellSpacing(8);
		mainPanel.add(table);
		
		// Add a little space
		FlowPanel tmpPanel = new FlowPanel();
		tmpPanel.getElement().getStyle().setMarginTop(15, Unit.PX);
		int row = 0;
		table.setWidget(row, 0, tmpPanel);
		row += 1;
		
		// Add the server url controls
		tmpPanel = new FlowPanel();
		Label label = new Label(m_messages.editKeyShieldConfigDlg_ServerUrlLabel());
		tmpPanel.add(label);
		table.setHTML(row, 0, tmpPanel.getElement().getInnerHTML());
	
		m_serverUrlTextBox = new TextBox();
		m_serverUrlTextBox.setVisibleLength(60);
		table.setWidget(row, 1, m_serverUrlTextBox);
		row += 1;
		
		// Add the API authorization key
		tmpPanel = new FlowPanel();
		label = new Label(m_messages.editKeyShieldConfigDlg_ApiAuthKeyLabel());
		tmpPanel.add(label);
		table.setHTML(row, 0, tmpPanel.getElement().getInnerHTML());
		
		m_apiAuthKeyTextBox = new TextBox();
		m_apiAuthKeyTextBox.setVisibleLength(60);
		table.setWidget(row, 1, m_apiAuthKeyTextBox);
		row += 1;
		
		// Add the HTTP connection timeout controls
		InlineLabel tmpLabel;
		
		tmpPanel = new FlowPanel();
		label = new Label(m_messages.editKeyShieldConfigDlg_HttpConnectionTimeoutLabel());
		tmpPanel.add(label);
		table.setHTML(row, 0, tmpPanel.getElement().getInnerHTML());

		tmpPanel = new FlowPanel();
		m_timeoutTextBox = new TextBox();
		m_timeoutTextBox.setVisibleLength(4);
		m_timeoutTextBox.addKeyPressHandler(this);
		tmpPanel.add(m_timeoutTextBox);
		
		tmpLabel = new InlineLabel(m_messages.editKeyShieldConfigDlg_MilliSecondsLabel());
		tmpLabel.addStyleName("editKeyShieldConfigDlg_MilliSecondsLabel");
		tmpPanel.add(tmpLabel);
		
		table.setWidget(row, 1, tmpPanel);
		row += 1;
		
		// Add the controls for entering the authentication connector names
		// Add a little space
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName("editKeyShieldConfigDlg_ConnectorNamesSpacing");
		table.setWidget(row, 0, tmpPanel);
		row += 1;
		
		// Add a hint.
		tmpPanel = new FlowPanel();
		label = new Label(m_messages.editKeyShieldConfigDlg_AuthConnectorNamesHint());
		label.getElement().getStyle().setWidth(600, Unit.PX);
		label.addStyleName("editKeyShieldConfigDlg_Hint");
		tmpPanel.add(label);
		table.setHTML(row, 1, tmpPanel.getElement().getInnerHTML());
		row += 1;
		
		tmpPanel = new FlowPanel();
		label = new Label(m_messages.editKeyShieldConfigDlg_ConnectorNamesLabel());
		tmpPanel.add(label);
		table.setHTML(row, 0, tmpPanel.getElement().getInnerHTML());
		
		m_authConnectorNamesTextBox = new TextBox();
		m_authConnectorNamesTextBox.setVisibleLength(40);
		table.setWidget(row, 1, m_authConnectorNamesTextBox);
		row += 1;
		
		m_usernameAttributeAliasHint = new Label(m_messages.editKeyShieldConfigDlg_UsernameAttributeAliasHint());
		m_usernameAttributeAliasHint.addStyleName("editKeyShieldConfigDlg_Hint gwtUI_nowrap");
		table.setWidget(row, 1, m_usernameAttributeAliasHint);
		row += 1;
		
		tmpPanel = new FlowPanel();
		label = new Label(m_messages.editKeyShieldConfigDlg_UsernameAttributeAliasLabel());
		tmpPanel.add(label);
		table.setHTML(row, 0, tmpPanel.getElement().getInnerHTML());
		
		m_usernameAttributeAliasTextBox = new TextBox();
		m_usernameAttributeAliasTextBox.setVisibleLength(40);
		table.setWidget(row, 1, m_usernameAttributeAliasTextBox);
		row += 1;
		
		// Add the controls for entering the two-factor authentication
		// settings.  First, add a little space.
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName("editKeyShieldConfigDlg_ConnectorNamesSpacing");
		table.setWidget(row, 0, tmpPanel);
		row += 1;

		// Add a header for the two factor authentication fields.
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName("editKeyShieldConfigDlg_HeaderPanel");
		label = new Label(m_messages.editKeyShieldConfigDlg_TwoFactorAuthHeader());
		label.addStyleName("editKeyShieldConfigDlg_Header");
		tmpPanel.add(label);
		table.setWidget(row, 0, tmpPanel);
		FlexCellFormatter fcf = table.getFlexCellFormatter();
		fcf.setColSpan(row, 0, 2);
		row += 1;

		// Add the hardware token required checkbox.
		m_hardwareTokenRequiredCheckbox = new CheckBox(m_messages.editKeyShieldConfigDlg_HardwareTokenRequiredLabel());
		table.setWidget(row, 0, m_hardwareTokenRequiredCheckbox);
		fcf.setColSpan(row, 0, 2);
		m_hardwareTokenRequiredCheckbox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean checked = m_hardwareTokenRequiredCheckbox.getValue();
				m_ssoErrorMessageForWeb.setEnabled(   checked);
				m_ssoErrorMessageForWebdav.setEnabled(checked);
			}
		});
		row += 1;
		
		// Add the prompt for error to display by the web client for
		// SSO errors.
		tmpPanel = new FlowPanel();
		label = new Label(m_messages.editKeyShieldConfigDlg_Error_SsoErrorMessageForWeb());
		label.addStyleName("marginLeftCBWidth");
		tmpPanel.add(label);
		table.setHTML(row, 0, tmpPanel.getElement().getInnerHTML());
		
		m_ssoErrorMessageForWeb = new TextBox();
		m_ssoErrorMessageForWeb.setVisibleLength(ERROR_MESSAGE_VISIBLE_LENGTH);
		m_ssoErrorMessageForWeb.setMaxLength(    ERROR_MESSAGE_MAXIMUM_LENGTH);
		table.setWidget(row, 1, m_ssoErrorMessageForWeb);
		row += 1;
		
		// Add the prompt for error to display by WebDAV clients for
		// SSO errors.
		tmpPanel = new FlowPanel();
		label = new Label(m_messages.editKeyShieldConfigDlg_Error_SsoErrorMessageForWebdav());
		label.addStyleName("marginLeftCBWidth");
		tmpPanel.add(label);
		table.setHTML(row, 0, tmpPanel.getElement().getInnerHTML());
		
		m_ssoErrorMessageForWebdav = new TextBox();
		m_ssoErrorMessageForWebdav.setVisibleLength(ERROR_MESSAGE_VISIBLE_LENGTH);
		m_ssoErrorMessageForWebdav.setMaxLength(    ERROR_MESSAGE_MAXIMUM_LENGTH);
		table.setWidget(row, 1, m_ssoErrorMessageForWebdav);
		row += 1;
		
		// Add the non-SSO allowed for LDAP users checkbox.
		m_nonSsoAllowedForLdapUserCheckbox = new CheckBox(m_messages.editKeyShieldConfigDlg_NonSsoAllowedForLdapUserLabel());
		table.setWidget(row, 0, m_nonSsoAllowedForLdapUserCheckbox);
		fcf.setColSpan(row, 0, 2);
		row += 1;

		// Add a 'test connection' button.
		// Add a little space
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName("editKeyShieldConfigDlg_TestConnectionSpacing");
		table.setWidget(row, 0, tmpPanel);
		row += 1;
		
		// Add 'Test connection' button.
		Button testConnectionBtn = new Button(m_messages.editKeyShieldConfigDlg_TestConnectionLabel());
		testConnectionBtn.addStyleName("teamingButton");
		testConnectionBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						testConnection();
					}
				});
			}
			
		});
		
		table.setWidget(row, 0, testConnectionBtn);
		row += 1;

		return mainPanel;
	}

	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled() {
		// Is the dialog dirty?
		if (isDirty()) {
			// Yes!  Is the user sure they want to close it?
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
								// Yes, they're sure!  Hide it.
								hide();
							}

							@Override
							public void rejected() {
								// No, they're not sure!
							}
						},
						GwtTeaming.getMessages().confirmChangesWillBeLost());
				}
			});
			
			// Return false, the dialog will be closed in the
			// ConfirmDlg if needed.
			return false;
		}

		// No, the dialog's not dirty!  Simply let it close.
		return true;
	}

	/**
	 * This gets called when the user presses OK.  Issues a GWT RPC
	 * request to save the settings.
	 * 
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(final Object obj) {
		if (!(obj instanceof GwtKeyShieldConfig)) {
			return false;
		}

		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// Issue a GWT RPC request to save the KeyShield
				// configuration.
				saveKeyShieldConfiguration((GwtKeyShieldConfig) obj);
			}
		});

		// Returning false will prevent the dialog from closing.  We
		// will close the dialog after we successfully save the user
		// access configuration.
		return false;
	}

	/*
	 */
	private String getApiAuthKey() {
		String value = m_apiAuthKeyTextBox.getValue();
		if (null == value) {
			value = "";
		}
		return value;
	}
	
	/*
	 */
	private TreeSet<String> getAuthConnectorNames() {
		TreeSet<String> retValue = new TreeSet<String>();
		String value = m_authConnectorNamesTextBox.getValue();
		if (null != value) {
			String[] names = value.split(",");
			if (null != names) {
				for (String nextName:  names) {
					retValue.add(nextName);
				}
			}
			else {
				retValue.add(value);
			}
		}
		
		return retValue;
	}
	
	/*
	 */
	private String getUsernameAttributeAlias() {
		String value = m_usernameAttributeAliasTextBox.getValue();
		if (null == value) {
			value = "";
		}
		return value;
	}
	
	/*
	 */
	private String convertTreeSetToCommaDelimitedString(TreeSet<String> set) {
		String value;
		if (null != set) {
			StringBuffer strBuff = new StringBuffer();
			Iterator<String> iter = set.iterator();
			while (iter.hasNext()) {
				String nextName = iter.next();
				if (strBuff.length() > 0) {
					strBuff.append(',');
				}
				
				strBuff.append(nextName);
			}
			
			value = strBuff.toString().toLowerCase();
		}
		
		else {
			value = "";
		}
		
		return value;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		GwtKeyShieldConfig config = GwtKeyShieldConfig.getGwtKeyShieldConfig();
		
		config.setIsEnabled(getIsKeyShieldEnabled());
		config.setHttpConnectionTimeout(getHttpConnectionTimeout());
		config.setApiAuthKey(getApiAuthKey());
		config.setServerUrl(getServerUrl());
		config.setUsernameAttributeAlias(getUsernameAttributeAlias());
		config.setAuthConnectorNames(getAuthConnectorNames());
		config.setHardwareTokenRequired(getHardwareTokenRequired());
		config.setNonSsoAllowedForLdapUser(getNonSsoAllowedForLdapUser());
		config.setSsoErrorMessageForWeb(getSsoErrorMessageForWeb());
		config.setSsoErrorMessageForWebdav(getSsoErrorMessageForWebdav());
		
		return config;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is
	 * shown.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return m_enableKeyShieldCheckbox;
	}
	
	/**
	 * ?
	 * 
	 * @return 
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("keyshield");
		return helpData;
	}

	/*
	 */
	private boolean getHardwareTokenRequired() {
		return m_hardwareTokenRequiredCheckbox.getValue();
	}
	
	/*
	 */
	private int getHttpConnectionTimeout() {
		String value = m_timeoutTextBox.getValue();
		if (!(GwtClientHelper.hasString(value))) {
			return 250;	// Default is 250 milliseconds
		}
		
		return Integer.parseInt(value);
	}
	
	/*
	 */
	private boolean getIsKeyShieldEnabled() {
		return m_enableKeyShieldCheckbox.getValue();
	}

	/*
	 */
	private boolean getNonSsoAllowedForLdapUser() {
		return m_nonSsoAllowedForLdapUserCheckbox.getValue();
	}
	
	/*
	 */
	private String getSsoErrorMessageForWeb() {
		return m_ssoErrorMessageForWeb.getValue();
	}

	/*
	 */
	private String getSsoErrorMessageForWebdav() {
		return m_ssoErrorMessageForWebdav.getValue();
	}

	/*
	 * Issue a GWT RPC request to get the KeyShield configuration data
	 * from the server.
	 */
	private void getKeyShieldConfigurationFromServer() {
		showStatusMsg(GwtTeaming.getMessages().editKeyShieldConfigDlg_ReadingConfig());
		
		// Execute a GWT RPC command to get the KeyShield
		// configuration.
		GwtClientHelper.executeCommand(new GetKeyShieldConfigCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetKeyShieldConfig());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				final GwtKeyShieldConfig config = ((GwtKeyShieldConfig) response.getResponseData());
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						hideStatusMsg();
						init(config);
					}
				});
			}
		});
	}
	
	/*
	 */
	private String getServerUrl() {
		String value = m_serverUrlTextBox.getValue();
		if (null == value) {
			value = "";
		}
		
		return value;
	}

	/*
	 */
	private String getErrorMessage(GwtKeyShieldConfig gwtKsc, boolean webDAV) {
		String reply = null;
		
		boolean hardCodeSSOErrorStrings = HARD_CODE_SSO_ERROR_STRINGS;
		if (webDAV) {
			if (null != gwtKsc) {
				reply = gwtKsc.getSsoErrorMessageForWebdav();
			}
			if (!(GwtClientHelper.hasString(reply))) {
				if (hardCodeSSOErrorStrings)
				     reply = "Card presence is required for WebDAV interface access";
				else reply = m_messages.editKeyShieldConfigDlg_Error_DefaultWebDAV();
			}
		}
		
		else {
			if (null != gwtKsc) {
				reply = gwtKsc.getSsoErrorMessageForWeb();
			}
			if (!(GwtClientHelper.hasString(reply))) {
				if (hardCodeSSOErrorStrings)
				     reply = "Card presence is required for Web interface access";
				else reply = m_messages.editKeyShieldConfigDlg_Error_DefaultWeb();
			}
		}
		
		return reply;
	}
	
	/*
	 */
	private void init() {
		m_testConnectionInProgress = false;
		getKeyShieldConfigurationFromServer();
	}
	
	/*
	 */
	private void init(GwtKeyShieldConfig config) {
		if (null == config) {
			m_config = GwtKeyShieldConfig.getGwtKeyShieldConfig();
			
			m_enableKeyShieldCheckbox.setValue(false);
			m_serverUrlTextBox.setValue("");
			m_timeoutTextBox.setValue("250");
			m_apiAuthKeyTextBox.setValue("");
			m_authConnectorNamesTextBox.setValue("");
			m_usernameAttributeAliasTextBox.setValue(GwtClientHelper.isLicenseFilr() ? "x-filr" : "x-vibe");
			m_usernameAttributeAliasHint.setVisible(true);
			m_nonSsoAllowedForLdapUserCheckbox.setValue(true);
			m_hardwareTokenRequiredCheckbox.setValue(false);
			m_ssoErrorMessageForWeb.setValue(   getErrorMessage(null, false));
			m_ssoErrorMessageForWeb.setEnabled(false);
			m_ssoErrorMessageForWebdav.setValue(getErrorMessage(null, true));
			m_ssoErrorMessageForWebdav.setEnabled(false);
		}
		
		else {
			m_config = config;
			
			m_enableKeyShieldCheckbox.setValue(m_config.isEnabled());
			m_serverUrlTextBox.setValue(m_config.getServerUrl());
			m_timeoutTextBox.setValue(String.valueOf(m_config.getHttpConnectionTimeout()));
			m_apiAuthKeyTextBox.setValue(m_config.getApiAuthKey());
			
			String value = convertTreeSetToCommaDelimitedString(m_config.getAuthConnectorNames());
			m_authConnectorNamesTextBox.setValue(value);
			
			String unaa = m_config.getUsernameAttributeAlias();
			m_usernameAttributeAliasTextBox.setValue((null == unaa) ? "" : unaa);
			m_usernameAttributeAliasHint.setVisible(false);
			
			m_nonSsoAllowedForLdapUserCheckbox.setValue(m_config.isNonSsoAllowedForLdapUser());
			boolean hardwareTokenRequired = m_config.isHardwareTokenRequired();
			m_hardwareTokenRequiredCheckbox.setValue(hardwareTokenRequired);
			m_ssoErrorMessageForWeb.setValue(getErrorMessage(m_config, false));
			m_ssoErrorMessageForWeb.setEnabled(m_config.isHardwareTokenRequired());
			m_ssoErrorMessageForWebdav.setValue(getErrorMessage(m_config, true));
			m_ssoErrorMessageForWebdav.setEnabled(m_config.isHardwareTokenRequired());
		}
	}
	
	/*
	 * Return true if anything in the KeyShield configuration has
	 * changed.
	 */
	private boolean isDirty() {
		if (null == m_config) {
			return true;
		}
		
		// Has anything in the configuration changed?
		if (getIsKeyShieldEnabled() != m_config.isEnabled()) {	
			return true;
		}
		
		String value = getServerUrl();
		if ((null != value) && (!(value.equalsIgnoreCase(m_config.getServerUrl())))) {
			return true;
		}
		
		if (getHttpConnectionTimeout() != m_config.getHttpConnectionTimeout()) {
			return true;
		}
		
		value = getApiAuthKey();
		if ((null != value) && (!(value.equalsIgnoreCase(m_config.getApiAuthKey())))) {
			return true;
		}
		
		TreeSet<String> set1 = getAuthConnectorNames();
		String value1 = convertTreeSetToCommaDelimitedString(set1);
		
		TreeSet<String> set2 = m_config.getAuthConnectorNames();
		String value2 = convertTreeSetToCommaDelimitedString(set2);
		
		if (!(value1.equalsIgnoreCase(value2))) {
			return true;
		}
		
		// If we get here, nothing has changed.
		return false;
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
	
	/**
	 * This method gets called when the user types in the http connection timeout text box.
	 * We only allow the user to enter numbers.
	 */
	@Override
	public void onKeyPress(KeyPressEvent event) {
        // Get the key the user pressed
        int keyCode = event.getNativeEvent().getKeyCode();
        if (!(GwtClientHelper.isKeyValidForNumericField(event.getCharCode(), keyCode))) {
        	// Make sure we are dealing with a text box.
        	Object source = event.getSource();
        	if (source instanceof TextBox) {
        		// Suppress the current keyboard event.
            	TextBox txtBox = ((TextBox) source);
        		txtBox.cancelKey();
        	}
        }
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
	 * Issue a GWT RPC request to save the KeyShield configuration.
	 */
	private void saveKeyShieldConfiguration(GwtKeyShieldConfig config) {
		showStatusMsg(GwtTeaming.getMessages().editKeyShieldConfigDlg_SavingConfig());
		clearErrorPanel();
		hideErrorPanel();

		// Execute a GWT RPC command to save the KeyShield configuration
		SaveKeyShieldConfigCmd cmd = new SaveKeyShieldConfigCmd();
		cmd.setConfig(config);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_SaveKeyShieldConfig());
				
				hideStatusMsg();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				hideStatusMsg();
				
				if ((null != response.getResponseData()) && (response.getResponseData() instanceof SaveKeyShieldConfigRpcResponseData)) {
					SaveKeyShieldConfigRpcResponseData responseData = ((SaveKeyShieldConfigRpcResponseData) response.getResponseData());
					if (responseData.getSaveSuccessfull()) {
						GwtClientHelper.deferCommand(new ScheduledCommand() {
							@Override
							public void execute() {
								hide();
							}
						});
					}
					
					else {
						FlowPanel errorPanel = getErrorPanel();
						Label label = new Label(GwtTeaming.getMessages().editKeyShieldConfigDlg_Error_SavingConfig());
						label.addStyleName("dlgErrorLabel");
						errorPanel.add(label);
						
						showErrorPanel();
					}
				}
			}
		});
	}
	
	/*
	 */
	private void testConnection() {
		if (m_testConnectionInProgress) {
			return;
		}
		
		showStatusMsg(GwtTeaming.getMessages().testConnection_InProgressLabel());
	
		// Issue a GWT RPC request to test net folder root connection.
		GwtKeyShieldConfig config = ((GwtKeyShieldConfig) getDataFromDlg());
		GwtClientHelper.executeCommand(new TestKeyShieldConnectionCmd(config), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				hideStatusMsg();
				m_testConnectionInProgress = false;
				String errMsg = GwtTeaming.getMessages().rpcFailure_ErrorTestingKeyShieldConnection();
				GwtClientHelper.deferredAlert(errMsg);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				String msg = null;
				hideStatusMsg();
				m_testConnectionInProgress = false;
				m_alertPanel = null;
				TestKeyShieldConnectionResponse response = ((TestKeyShieldConnectionResponse) result.getResponseData());
				switch (response.getStatusCode()) {
				case NORMAL:
					msg = GwtTeaming.getMessages().testConnection_Normal();
					break;
				
				case FAILED:
					// Create a panel that will display the cause of the failure and a link
					// the user can click on to see the stack trace.
					m_alertPanel = new FlowPanel();
					
					String labelText  = GwtTeaming.getMessages().testConnection_FailedError();
					String statusDesc = response.getStatusDescription();
					if (GwtClientHelper.hasString(statusDesc)) {
						labelText += (" - " + statusDesc);
					}

					Label label = new Label(labelText);
					m_alertPanel.add(label);
					
					// Do we have a stack trace?
					String stackTrace = response.getStackTrace();
					if (GwtClientHelper.hasString(stackTrace)) {
						// Yes!  Add a link the user can click on to
						// see the stack trace.
						label = new Label(GwtTeaming.getMessages().editKeyShieldConfigDlg_StackTraceLabel());
						label.addStyleName("editKeyShieldDlg_SeeStackTraceLabel");
						label.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								GwtClientHelper.deferCommand(new ScheduledCommand() {
									@Override
									public void execute() {
										m_stackTracePanel.setVisible(!(m_stackTracePanel.isVisible()));
									}
								});
							}
						});
						m_alertPanel.add(label);
						
						m_stackTracePanel = new FlowPanel();
						m_stackTracePanel.setVisible(false);
						m_stackTracePanel.addStyleName("editKeyShieldDlg_StackTracePanel");
						if (GwtClientHelper.jsIsSafari()) {
							m_stackTracePanel.addStyleName("maxWidth500");
						}
						SafeHtmlBuilder shBuilder = new SafeHtmlBuilder().appendEscapedLines(stackTrace);
						SafeHtml safeHtml = shBuilder.toSafeHtml();
						label = new Label(safeHtml.asString());
						m_stackTracePanel.add(label);
						
						m_alertPanel.add(m_stackTracePanel);
					}
					break;
				
				case UNKNOWN:
				default:
					msg = GwtTeaming.getMessages().testConnection_UnknownStatus();
					break;
				}
				
				if      (null != msg)          GwtClientHelper.alertViaDlg(msg         );
				else if (null != m_alertPanel) GwtClientHelper.alertViaDlg(m_alertPanel);
			}						
		});
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
	/* the edit key shield config dialog and perform some operation  */
	/* on it.                                                        */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the "edit KeyShield config" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditKeyShieldConfigDlgClient {
		void onSuccess(EditKeyShieldConfigDlg ekscDlg);
		void onUnavailable();
	}
	
	/**
	 * Executes code through the GWT.runAsync() method to ensure that
	 * all of the executing code is in this split point.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param ekscDlgClient
	 */
	public static void createDlg(final boolean autoHide, final boolean modal, final int left, final int top, final int width, final int height, final EditKeyShieldConfigDlgClient ekscDlgClient) {
		GWT.runAsync(EditKeyShieldConfigDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_EditKeyShieldConfigDlg());
				if (null != ekscDlgClient) {
					ekscDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				EditKeyShieldConfigDlg ekscDlg = new EditKeyShieldConfigDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height);
				
				if (null != ekscDlgClient) {
					ekscDlgClient.onSuccess(ekscDlg);
				}
			}
		});
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that
	 * all of the executing code is in this split point.
	 * 
	 * @param dlg
	 * @param width
	 * @param height
	 * @param left
	 * @param top
	 * @param ekscDlgClient
	 */
	public static void initAndShow(final EditKeyShieldConfigDlg dlg, final int width, final int height, final int left, final int top, final EditKeyShieldConfigDlgClient ekscDlgClient) {
		GWT.runAsync(EditKeyShieldConfigDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_EditKeyShieldConfigDlg());
				if (null != ekscDlgClient) {
					ekscDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				dlg.setPixelSize(width, height);
				dlg.init();
				dlg.setPopupPosition(left, top);
				dlg.show();
			}
		});
	}
}
