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
import java.util.HashMap;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.GwtUser.ExtUserProvState;
import org.kablink.teaming.gwt.client.SendForgottenPwdEmailRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FindUserByEmailAddressCmd;
import org.kablink.teaming.gwt.client.rpc.shared.FindUserByEmailAddressRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SendForgottenPwdEmailCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FindUserByEmailAddressCmd.UsersToFind;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PrincipalType;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ?
 *  
 * @author jwootton@novell.com
 */
public class ForgottenPwdDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private TextBox m_emailAddressTxtBox;
	
	/**
	 * Callback interface to interact with the "Forgotten Password" dialog asynchronously after it loads. 
	 */
	public interface ForgottenPwdDlgClient
	{
		void onSuccess( ForgottenPwdDlg fpDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ForgottenPwdDlg(
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().forgottenPwdDlg_Caption(),
						this,
						null,
						null ); 
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		VibeFlowPanel mainPanel;
		FlexTable table;
		Label label;
		int row = 0;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.addStyleName( "forgottenPwdDlg_MainPanel" );

		// Add a hint telling the user that forgotten password is only for external users.
		{
			Label hint;
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "forgottenPwdDlg_Hint" );
			
			hint = new Label( messages.forgottenPwdDlg_HintNote() );
			panel.add( hint );
			
			hint = new Label( messages.forgottenPwdDlg_Hint2( GwtClientHelper.getProductName() ) );
			panel.add( hint );
			
			mainPanel.add( panel );
		}

		label = new Label( messages.forgottenPwdDlg_Instructions() );
		label.addStyleName( "forgottenPwdDlg_Instructions" );
		mainPanel.add( label );

		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );

		table.setText( row, 0, messages.forgottenPwdDlg_EmailAddress() );

		m_emailAddressTxtBox = new TextBox();
		m_emailAddressTxtBox.setVisibleLength( 30 );
		m_emailAddressTxtBox.addKeyPressHandler( new KeyPressHandler()
		{
			@Override
			public void onKeyPress( KeyPressEvent event )
			{
		        int keyCode;
		    	
		        // Get the key the user pressed
		        keyCode = event.getNativeEvent().getKeyCode();
		        
		        // Did the user press the enter key?
		        if ( keyCode == KeyCodes.KEY_ENTER )
		        {
		        	GwtClientHelper.deferCommand( new ScheduledCommand()
		        	{
						@Override
						public void execute()
						{
							editSuccessful( null );
						}
					} );
		        }
			}
		} );
		table.setWidget( row, 1, m_emailAddressTxtBox );

		mainPanel.add( table );
		
		return mainPanel;
	}

	/**
	 * 
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		final String emailAddress;
		ArrayList<String> listOfEmailAddresses;
		
		emailAddress = m_emailAddressTxtBox.getValue();
		if ( emailAddress == null || emailAddress.length() == 0 )
		{
			// Tell the user to enter a valid email address
			Window.alert( GwtTeaming.getMessages().forgottenPwdDlg_EnterEmailAddress() );
			return false;
		}
		
		listOfEmailAddresses = new ArrayList<String>();
		listOfEmailAddresses.add( emailAddress );
		
		// See if the email address the user entered is associated with an external user.
		{
			AsyncCallback<VibeRpcResponse> findUserCallback;
	
			findUserCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
													caught,
													GwtTeaming.getMessages().rpcFailure_FindUserByEmailAddress() );
				}

				@Override
				public void onSuccess( final VibeRpcResponse vibeResult )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							final GwtUser gwtUser;
							VibeRpcResponseData responseData;
							boolean sendEmail = false;
							
							responseData = vibeResult.getResponseData();
							if ( responseData != null && responseData instanceof FindUserByEmailAddressRpcResponseData )
							{
								FindUserByEmailAddressRpcResponseData findUserResponseData;
								HashMap<String,GwtUser> listOfUsers;
								
								findUserResponseData = (FindUserByEmailAddressRpcResponseData) responseData;
								
								listOfUsers = findUserResponseData.getListOfUsers();
								if ( listOfUsers != null && listOfUsers.size() == 1 )
									gwtUser = listOfUsers.get( emailAddress );
								else
									gwtUser = null;
							}
							else
								gwtUser = null;
							
							
							// Did we get a user?
							if ( gwtUser != null )
							{
								PrincipalType userType;

								// Yes
								userType = gwtUser.getPrincipalType();
								if ( userType == PrincipalType.EXTERNAL_OTHERS ) 
								{
									ExtUserProvState extUserProvState;

									extUserProvState = gwtUser.getExtUserProvState();
									if ( extUserProvState == ExtUserProvState.VERIFIED ||
										 extUserProvState == ExtUserProvState.PWD_RESET_REQUESTED ||
										 extUserProvState == ExtUserProvState.PWD_RESET_WAITING_FOR_VERIFICATION )
									{
										sendEmail = true;
									}
								}
								else if ( userType == PrincipalType.EXTERNAL_OPEN_ID )
									sendEmail = true;
								
								if ( sendEmail )
								{
									// Issue an rpc request that will send an email to the user telling
									// them how to reset their password.
									GwtClientHelper.deferCommand( new ScheduledCommand()
									{
										@Override
										public void execute()
										{
											sendForgottenPwdEmail( gwtUser, emailAddress );
										}
									} );
								}
							}
							
							if ( sendEmail == false )
							{
								FlowPanel errorPanel;
								Label label;
								String txt;

								// No, tell the user about it.
								// Get the panel that holds the errors.
								errorPanel = getErrorPanel();
								errorPanel.clear();

								label = new Label( GwtTeaming.getMessages().shareErrors() );
								label.addStyleName( "dlgErrorLabel" );
								errorPanel.add( label );
								
								txt = GwtTeaming.getMessages().forgottenPwdDlg_InvalidEmailAddress2( GwtClientHelper.getProductName() );
								
								label = new Label( txt );
								label.addStyleName( "bulletListItem" );
								errorPanel.add( label );

								// Make the error panel visible.
								showErrorPanel();
								m_emailAddressTxtBox.setFocus( true );
							}
						}
					} );
				}				
			};

			// Issue an ajax request to see if the email address that was entered is associated
			// with an external user.
			FindUserByEmailAddressCmd cmd = new FindUserByEmailAddressCmd( listOfEmailAddresses );
			cmd.setUsersToFind( UsersToFind.EXTERNAL_USERS_ONLY );
			GwtClientHelper.executeCommand( cmd, findUserCallback );
		}
		
		return false;
	}

	/**
	 * 
	 */
	@Override
	public Object getDataFromDlg()
	{
		return Boolean.TRUE;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_emailAddressTxtBox;
	}

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init()
	{
		m_emailAddressTxtBox.setValue( "" );

		// Enable the Ok button.
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );
	}

	/**
	 * Issue an rpc request to send an email to the user with instructions on how to reset their
	 * password. 
	 */
	private void sendForgottenPwdEmail( final GwtUser user, String emailAddress )
	{
		AsyncCallback<VibeRpcResponse> callback;
		SendForgottenPwdEmailCmd cmd;

		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				GwtClientHelper.handleGwtRPCFailure(
												caught,
												GwtTeaming.getMessages().rpcFailure_SendForgottenPwdEmail() );
				// Enable the Ok button.
				hideStatusMsg();
				setOkEnabled( true );
			}

			@Override
			public void onSuccess( final VibeRpcResponse vibeResult )
			{
				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Get the results of our request
						if ( vibeResult.getResponseData() != null )
						{
							SendForgottenPwdEmailRpcResponseData responseData;
							String[] errors;
							
							// Were there any errors?
							responseData = (SendForgottenPwdEmailRpcResponseData) vibeResult.getResponseData();
							errors = responseData.getErrors();
							if ( errors != null && errors.length > 0 )
							{
								FlowPanel errorPanel;
								Label label;
								
								// Yes
								// Get the panel that holds the errors.
								errorPanel = getErrorPanel();
								errorPanel.clear();

								label = new Label( GwtTeaming.getMessages().shareErrors() );
								label.addStyleName( "dlgErrorLabel" );
								errorPanel.add( label );
								
								for ( String nextErrMsg : errors )
								{
									label = new Label( nextErrMsg );
									label.addStyleName( "bulletListItem" );
									errorPanel.add( label );
								}

								// Make the error panel visible.
								showErrorPanel();

								// Enable the Ok button.
								hideStatusMsg();
								setOkEnabled( true );
							}
							else
							{
								String msg;
								
								if ( user.getPrincipalType() == PrincipalType.EXTERNAL_OPEN_ID )
									msg = GwtTeaming.getMessages().forgottenPwdDlg_SelfRegistrationEmailSent();
								else
									msg = GwtTeaming.getMessages().forgottenPwdDlg_ForgottenPwdEmailSent();
								
								// No, tell the user an email has been sent.
								Window.alert( msg );
								
								// Hide this dialog.
								hide();
							}
						}
					}
				} );
			}				
		};

		// Disable the Ok button.
		showStatusMsg( GwtTeaming.getMessages().forgottenPwdDlg_sendingEmail(), "dlgBox_statusPanel_relative" );
		setOkEnabled( false );
		
		// Issue an ajax request to send the forgotten password email to the given email address
		cmd = new SendForgottenPwdEmailCmd( user, emailAddress );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Loads the ForgottenPwdDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final ForgottenPwdDlgClient fpDlgClient )
	{
		GWT.runAsync( ForgottenPwdDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ForgottenPwdDlg() );
				if ( fpDlgClient != null )
				{
					fpDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ForgottenPwdDlg fpDlg;
				
				fpDlg = new ForgottenPwdDlg( autoHide, modal );
				fpDlgClient.onSuccess( fpDlg );
			}
		});
	}
}
