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
import org.kablink.teaming.gwt.client.GwtADLdapObject;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtProxyIdentity;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.ldapbrowser.DirectoryServer;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapSearchInfo;
import org.kablink.teaming.gwt.client.ldapbrowser.LdapServer.DirectoryType;
import org.kablink.teaming.gwt.client.rpc.shared.AddNewProxyIdentityCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapObjectFromADCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyProxyIdentityCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ProxyIdentityRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg;
import org.kablink.teaming.gwt.client.widgets.LdapBrowserDlg.LdapBrowserDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Implements Filr's proxy identity dialog.  This dialog is used to add
 * a new proxy identity as well as modify an existing one.
 *  
 * @author drfoster@novell.com
 */
public class ProxyIdentityDlg extends DlgBox implements EditSuccessfulHandler {
	private boolean					m_adding;				// true -> We're adding a proxy identity.  false -> We're modifying an existing one.
	private FlowPanel				m_dlgPanel;				// The panel holding the dialog's content.
	private GwtTeamingImageBundle	m_images;				// Access to Filr's images.
	private GwtTeamingMessages		m_messages;				// Access to Filr's messages.
	private GwtProxyIdentity		m_proxyIdentity;		// null -> Adding a new proxy identity.  non-null -> References a proxy identity to be modified.
	private InputWidgets 			m_passwordInput;		// The <INPUT> widget for the proxy identity's password.
	private InputWidgets 			m_passwordVerifyInput;	// The <INPUT> widget to verify the proxy identity's password.
	private InputWidgets 			m_proxyNameInput;		// The <INPUT> widget for the proxy identity's name.
	private InputWidgets 			m_titleInput;			// The <INPUT> widget for the proxy identity's title.
	private LdapBrowserDlg			m_ldapBrowserDlg;		// An instance of an LDAP browser, once one is created.
	private List<LdapBrowseSpec>	m_ldapConnections;		// List of LDAP connections that have been defined.

	/*
	 * Interface used to interact with the content of the TextBox
	 * widgets when one of them is in error.
	 */
	private interface GetTextBoxValueError {
		public String getNoValueError();
		public String getTooLongError(int maxLength);
	}

	/*
	 * Inner class used to encapsulate a TextBox with an associated
	 * LDAP browse Button.
	 */
	private static class InputWidgets {
		private Button	m_ldapBrowseButton;		//
		private TextBox	m_textBox;				//

		/**
		 * Constructor method.
		 * 
		 * @param ldapBrowseButton
		 * @param textBox
		 */
		public InputWidgets(Button ldapBrowseButton, TextBox textBox) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			setLdapBrowseButton(ldapBrowseButton);
			setTextBox(         textBox         );
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Button  getLdapBrowseButton() {return m_ldapBrowseButton;}
		public TextBox getTextBox()          {return m_textBox;         }

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setLdapBrowseButton(Button  ldapBrowseButton) {m_ldapBrowseButton = ldapBrowseButton;}
		public void setTextBox(         TextBox textBox)          {m_textBox          = textBox;         }
	}
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ProxyIdentityDlg() {
		// Initialize the super class...
		super(false, true);

		// ...initialize everything else...
		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",							// No caption yet.  It's set appropriately when the dialog runs.
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
	}

	/*
	 * Runs the LDAP browser for a user DN.
	 */
	private void browseLdapForUserDN(final InputWidgets ldapBrowserWidgets) {
		// Have we instantiated an LDAP browser yet?
		if (null == m_ldapBrowserDlg) {
			// No!  Create one now...
			LdapBrowserDlg.createAsync(new LdapBrowserDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(LdapBrowserDlg ldapDlg) {
					// ...save it away...
					m_ldapBrowserDlg = ldapDlg;
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// ...and run it.
							browseLdapForUserDNImpl(ldapBrowserWidgets);
						}
					});
				}
			});
		}
		
		else {
			// Yes, we've already instantiated an LDAP browser!  Simply
			// run it.
			browseLdapForUserDNImpl(ldapBrowserWidgets);
		}
	}
	
	/*
	 * Implementation method to run an LDAP browser for a user DN.
	 */
	private void browseLdapForUserDNImpl(final InputWidgets ldapBrowserWidgets) {
		LdapBrowserDlg.initAndShow(
			m_ldapBrowserDlg,
			new LdapBrowserCallback() {
				@Override
				public void closed() {
					// Ignored.  We don't care if the user closes the
					// browser.
				}
	
				@Override
				public void selectionChanged(LdapObject selection, DirectoryType dt) {
					// Since we're browsing for a user DN, it can ONLY
					// be a leaf node.  Ignore non-leaf selections.
					if (selection.isLeaf()) {
						setLdapNameFromBrowser(m_proxyNameInput.getTextBox(), selection.getDn(), dt);
						m_ldapBrowserDlg.hide();
					}
				}
			},
			m_ldapConnections,
			ldapBrowserWidgets.getLdapBrowseButton());
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
		m_dlgPanel.addStyleName("vibe-proxyIdentityDlg_Panel");
		return m_dlgPanel;
	}

	/*
	 * Creates a stylized TextBox widget and adds it to the provided
	 * FlexTable.
	 * 
	 * The TextBox widget is returned.
	 */
	private InputWidgets createInputWidget(FlexTable grid, int row, String initialValue, String labelText, String labelStyle, String tbStyle, boolean isPassword, boolean isLdapDN) {
		// Get a FlexCellFormatter for working with the Grid.
		FlexCellFormatter gridCellFmt = grid.getFlexCellFormatter();
		
		// Create the label for the TextBox and add it to the Grid...
		InlineLabel il = new InlineLabel(labelText);
		il.addStyleName(labelStyle);
		grid.setWidget(                  row, 0, il                               );
		gridCellFmt.setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_MIDDLE);

		// ...and create the TextBox widget and add it to the Grid.
		FlowPanel tbPanel = new VibeFlowPanel();
		TextBox   tb      = new TextBox();
		if (isPassword)
		     tb = new PasswordTextBox();
		else tb = new TextBox();
		tb.addStyleName(tbStyle);
		tb.addKeyDownHandler(new KeyDownHandler() {
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
		if (GwtClientHelper.hasString(initialValue)) {
			tb.setValue(initialValue);
		}
		tbPanel.add(tb);
		final InputWidgets reply = new InputWidgets(null, tb);	// Will fill in the LDAP browse button below if one is created.
		
		Button ldapBrowseBtn;
		if (isLdapDN) {
			Image btnImg = GwtClientHelper.buildImage(m_images.browseLdap().getSafeUri().asString());
			btnImg.setTitle(m_messages.proxyIdentityDlgName_Alt());
			FlowPanel html = new VibeFlowPanel();
			html.add(btnImg);
			ldapBrowseBtn = new Button(html.getElement().getInnerHTML());
			ldapBrowseBtn.addStyleName("vibe-proxyIdentityDlg_BrowseDN");
			ldapBrowseBtn.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							browseLdapForUserDN(reply);
						}
					});
				}
			});
			reply.setLdapBrowseButton(ldapBrowseBtn);
			
			// Because most customers do not have anonymous access
			// turned on in their LDAP directory we are not going to
			// add an LDAP browse button.
			tbPanel.add(ldapBrowseBtn);
		}
		else {
			ldapBrowseBtn = null;
		}
		grid.setWidget(                  row, 1, tbPanel                         );
		gridCellFmt.setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_MIDDLE);
		
		// If we get here, reply refers to an InputWidgets containing
		// the TextBox and optionally, the LDAP browse button we
		// created.  Return it.
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
		String title = getTextBoxValue(m_titleInput.getTextBox(), true, GwtConstants.MAX_PROXY_IDENTITY_TITLE_LENGTH, new GetTextBoxValueError() {
			@Override public String getNoValueError()              {return m_messages.proxyIdentityDlgError_NoTitle();              }
			@Override public String getTooLongError(int maxLength) {return m_messages.proxyIdentityDlgError_TitleTooLong(maxLength);}
		});
		if (null == title) {
			// No!  The user will have been told about the error.
			// Simply bail.
			return;
		}
		
		// Did the user supply a valid name for the proxy identity?
		final String proxyName = getTextBoxValue(m_proxyNameInput.getTextBox(), true, GwtConstants.MAX_PROXY_IDENTITY_NAME_LENGTH, new GetTextBoxValueError() {
			@Override public String getNoValueError()              {return m_messages.proxyIdentityDlgError_NoName();              }
			@Override public String getTooLongError(int maxLength) {return m_messages.proxyIdentityDlgError_NameTooLong(maxLength);}
		});
		if (null == proxyName) {
			// No!  The user will have been told about the error.
			// Simply bail.
			return;
		}
		
		// Did the user supply a valid password for the proxy identity?
		String password = getTextBoxValue(m_passwordInput.getTextBox(), m_adding, GwtConstants.MAX_PROXY_IDENTITY_PASSWORD_LENGTH, new GetTextBoxValueError() {
			@Override public String getNoValueError()              {return m_messages.proxyIdentityDlgError_NoPassword();              }
			@Override public String getTooLongError(int maxLength) {return m_messages.proxyIdentityDlgError_PasswordTooLong(maxLength);}
		});
		if (null == password) {
			// No!  The user will have been told about the error.
			// Simply bail.
			return;
		}

		// Did the user supply a valid password verification for the
		// proxy identity?
		String passwordVerify = getTextBoxValue(m_passwordVerifyInput.getTextBox(), m_adding, GwtConstants.MAX_PROXY_IDENTITY_PASSWORD_LENGTH, new GetTextBoxValueError() {
			@Override public String getNoValueError()              {return m_messages.proxyIdentityDlgError_NoPasswordVerify();              }
			@Override public String getTooLongError(int maxLength) {return m_messages.proxyIdentityDlgError_PasswordVerifyTooLong(maxLength);}
		});
		if (null == passwordVerify) {
			// No!  The user will have been told about the error.
			// Simply bail.
			return;
		}
		
		// Does the password match the password verification?
		if (!(password.equals(passwordVerify))) {
			// No!  Tell the user about the error and bail.
			GwtClientHelper.deferredAlert(m_messages.proxyIdentityDlgError_PasswordsDontMatch());
			GwtClientHelper.setFocusDelayed(m_passwordInput.getTextBox());
			setButtonsEnabled(true);
			return;
		}

		// We have the parts we need.  Can we use them to add the new
		// proxy identity or modify the existing one?
		showDlgBusySpinner();
		VibeRpcCmd cmd;
		if (m_adding)
		     cmd = new AddNewProxyIdentityCmd(new GwtProxyIdentity(                         title, proxyName,                                    password ));
		else cmd = new ModifyProxyIdentityCmd(new GwtProxyIdentity(m_proxyIdentity.getId(), title, proxyName, ((0 == password.length()) ? null : password)));
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// No!  Restore the dialog's functionality...
				hideDlgBusySpinner();
				setButtonsEnabled(true);

				// ...and tell the user about the problem.
				GwtClientHelper.handleGwtRPCFailure(
					t,
					(m_adding                                       ?
						m_messages.rpcFailure_AddNewProxyIdentity() :
						m_messages.rpcFailure_ModifyProxyIdentity()),
					proxyName);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Perhaps!  Restore the dialog's functionality.
				hideDlgBusySpinner();
				setButtonsEnabled(true);
				
				// Were there any errors returned from the command?
				ProxyIdentityRpcResponseData responseData = ((ProxyIdentityRpcResponseData) response.getResponseData());
				List<ErrorInfo> errors = responseData.getErrorList();
				if ((null != errors) && (0 < errors.size())) {
					// Yes!  Display them.
					GwtClientHelper.displayMultipleErrors(
						(m_adding                                        ?
							m_messages.proxyIdentityDlgError_AddFailed() :
							m_messages.proxyIdentityDlgError_ModifyFailed()),
						errors);
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
		return ((null == m_titleInput) ? null : m_titleInput.getTextBox());
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
			GwtClientHelper.setFocusDelayed(tb);
			setButtonsEnabled(true);
			return null;
		}

		// Is the user supplied value for this too long?
		if (maxLength < replyLength) {
			// Yes!  Tell them about the error and bail.
			GwtClientHelper.deferredAlert(gtbvErrorCallback.getTooLongError(maxLength));
			GwtClientHelper.setFocusDelayed(tb);
			setButtonsEnabled(true);
			return null;
		}

		// If we get here, reply refers to the non-null, range checked
		// value requested.  Return it.
		return reply;
	}

	/*
	 * Asynchronously loads the currently defined LDAP server and
	 * populates the dialog.
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
	 * Synchronously loads the currently defined LDAP server and
	 * populates the dialog.
	 */
	private void loadPart1Now() {
		// Can we load the LDAP connections?
		GwtClientHelper.executeCommand(new GetLdapConfigCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// No!  Tell the user about the problem...
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_GetLdapConfig());

				// ...and run the dialog without them.
				m_ldapConnections = null;
				populateDlgAsync();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Perhaps!  Did we actually get any?
				GwtLdapConfig                 ldapConfig      = ((GwtLdapConfig) result.getResponseData());
				List<GwtLdapConnectionConfig> ldapConnections = ldapConfig.getListOfLdapConnections();
				if (GwtClientHelper.hasItems(ldapConnections)) {
					// Yes!  Scan them...
					m_ldapConnections = new ArrayList<LdapBrowseSpec>();
					for (GwtLdapConnectionConfig ldapConnection:  ldapConnections) {
						// ...creating an LdapBrowseSpec for each.
						DirectoryServer ds = new DirectoryServer();
						ds.setAddress(      ldapConnection.getServerUrl());
						ds.setSyncUser(     ldapConnection.getProxyDn());
						ds.setSyncPassword( ldapConnection.getProxyPwd());
						ds.setGuidAttribute(ldapConnection.getLdapGuidAttribute());

						LdapSearchInfo si = new LdapSearchInfo();
						si.setSearchObjectClass(LdapSearchInfo.RETURN_USERS);
						si.setSearchSubTree(false);
						
						m_ldapConnections.add(new LdapBrowseSpec(ds, si));
					}
				}
				
				else {
					// No, we didn't get any LDAP connections!  Well
					// run the dialog without any.
					m_ldapConnections = null;
				}
				
				// Finish populating the dialog.
				populateDlgAsync();
			}
		});
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
		grid.addStyleName("vibe-proxyIdentityDlg_Grid");
		m_dlgPanel.add(grid);

		// ...and create the input widgets.
		m_titleInput          = createInputWidget(grid, 0, (m_adding ? null : m_proxyIdentity.getTitle()),     m_messages.proxyIdentityDlgTitle(),          "vibe-proxyIdentityDlg_NameLabel", "vibe-proxyIdentityDlg_NameInput", false, false                                      );
		m_proxyNameInput      = createInputWidget(grid, 1, (m_adding ? null : m_proxyIdentity.getProxyName()), m_messages.proxyIdentityDlgName(),           "vibe-proxyIdentityDlg_NameLabel", "vibe-proxyIdentityDlg_NameInput", false, GwtClientHelper.hasItems(m_ldapConnections));
		m_passwordInput       = createInputWidget(grid, 2, null,                                               m_messages.proxyIdentityDlgPassword(),       "vibe-proxyIdentityDlg_NameLabel", "vibe-proxyIdentityDlg_NameInput", true,  false                                      );
		m_passwordVerifyInput = createInputWidget(grid, 3, null,                                               m_messages.proxyIdentityDlgPasswordVerify(), "vibe-proxyIdentityDlg_NameLabel", "vibe-proxyIdentityDlg_NameInput", true,  false                                      );
		
		// Finally, show the dialog, centered on the screen.
		setButtonsEnabled(true);
		center();
	}
	
	/*
	 * Asynchronously runs the given instance of the proxy identity
	 * dialog.
	 */
	private static void runDlgAsync(final ProxyIdentityDlg piDlg, final GwtProxyIdentity pi) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				piDlg.runDlgNow(pi);
			}
		});
	}
	
	/*
	 * Synchronously runs this instance of the proxy identity dialog.
	 */
	private void runDlgNow(final GwtProxyIdentity pi) {
		// Store the parameters.
		m_adding        = (null == pi);
		m_proxyIdentity = pi;
		
		// Set an appropriate dialog caption...
		setCaption(m_adding ? m_messages.proxyIdentityDlgHeader_Add() : m_messages.proxyIdentityDlgHeader_Modify());

		// ...and populate the dialog.
		loadPart1Async();
	}

	/*
	 * Enables/disables the buttons on the dialog.
	 */
	private void setButtonsEnabled(boolean enabled) {
		setOkEnabled(    enabled);
		setCancelEnabled(enabled);
	}
	
	/*
	 * If the selected directory type is Windows, get the proxy name in
	 * the format domain\samAccountName and store that in the TextBox.
	 */
	private void setLdapNameFromBrowser(TextBox tb, String ldapName, DirectoryType dt) {
		switch (dt) {
		default:
		case EDIRECTORY:        setLdapName(                  tb, ldapName); break;
		case ACTIVE_DIRECTORY:  setLdapNameUsingWindowsFormat(tb, ldapName); break;
		}
	}
	
	/*
	 * Stores an LDAP name in the given TextBox.
	 */
	private void setLdapName(TextBox tb, String ldapName) {
		tb.setValue(ldapName);
	}
	
	/*
	 * For the given full qualified DN, get the proxy name in the
	 * Windows format of domain-name\samAccountName and stick that
	 * proxy name in the text box.
	 */
	private void setLdapNameUsingWindowsFormat(final TextBox tb, final String initialLDAPName) {
		// Issue a GWT RPC request to get an LDAP object from AD.  We
		// will get the domain-name and samAccountName from the LDAP
		// object.
		GwtClientHelper.executeCommand(new GetLdapObjectFromADCmd(initialLDAPName), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Ignore any error and simply use the LDAP name we
				// were given directly.
				setLdapName(tb, initialLDAPName);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				GwtADLdapObject ldapObject = ((GwtADLdapObject) result.getResponseData());
				String headPart = ldapObject.getNetbiosName();
				if (!(GwtClientHelper.hasString(headPart))) {
					headPart = ldapObject.getDomainName();
				}
				String tailPart = ldapObject.getSamAccountName();
				String finalLDAPName;
				if (GwtClientHelper.hasString(headPart) && GwtClientHelper.hasString(tailPart))
				     finalLDAPName = (headPart + "\\" + tailPart);
				else finalLDAPName = initialLDAPName;
				setLdapName(tb, finalLDAPName);
			}						
		});
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the proxy identity dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the proxy identity dialog
	 * asynchronously after it loads. 
	 */
	public interface ProxyIdentityDlgClient {
		void onSuccess(ProxyIdentityDlg piDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the ProxyIdentityDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync parameters.
			final ProxyIdentityDlgClient	piDlgClient,
			
			// initAndShow parameters,
			final ProxyIdentityDlg			piDlg,
			final GwtProxyIdentity			pi) {
		GWT.runAsync(ProxyIdentityDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ProxyIdentityDlg());
				if (null != piDlgClient) {
					piDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != piDlgClient) {
					// Yes!  Create it and return it via the callback.
					ProxyIdentityDlg piDlg = new ProxyIdentityDlg();
					piDlgClient.onSuccess(piDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(piDlg, pi);
				}
			}
		});
	}
	
	/**
	 * Loads the ProxyIdentityDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param piDlgClient
	 */
	public static void createAsync(ProxyIdentityDlgClient piDlgClient) {
		doAsyncOperation(piDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the proxy identity dialog for adding a new
	 * proxy identity.
	 * 
	 * @param piDlg
	 */
	public static void initAndShow(ProxyIdentityDlg piDlg) {
		doAsyncOperation(null, piDlg, null);
	}
	
	/**
	 * Initializes and shows the proxy identity dialog for modifying an
	 * existing proxy identity.
	 * 
	 * @param piDlg
	 * @param pi
	 */
	public static void initAndShow(ProxyIdentityDlg piDlg, GwtProxyIdentity pi) {
		doAsyncOperation(null, piDlg, pi);
	}
}
