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
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.ChangePasswordCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This dialog is used to change a user's password
 * 
 * @author jwootton@novell.com
 */
public class ChangePasswordDlg extends DlgBox
	implements
		EditSuccessfulHandler
{
	private PasswordTextBox m_currentPwdTxtBox;
	private PasswordTextBox m_pwd1TxtBox;
	private PasswordTextBox m_pwd2TxtBox;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private Label m_changePasswordHintLabel;
	private Long m_userId;
	
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
	};

	
	/**
	 * Callback interface to interact with the "change password" dialog
	 * asynchronously after it loads. 
	 */
	public interface ChangePasswordDlgClient
	{
		void onSuccess( ChangePasswordDlg cpDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ChangePasswordDlg()
	{
		super( false, true );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().changePasswordDlg_ChangeDefaultPasswordHeader(),
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
		FlowPanel mainPanel;
		FlowPanel tmpPanel;
		Label label;
		KeyPressHandler keyPressHandler;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_changePasswordHintLabel = new Label( messages.changePasswordDlg_ChangeDefaultPasswordHint() );
		m_changePasswordHintLabel.addStyleName( "changePasswordDlg_Hint" );
		mainPanel.add( m_changePasswordHintLabel );

		label = new Label( messages.changePasswordDlg_CurrentPasswordLabel() );
		label.addStyleName( "changePasswordDlg_Label" );
		mainPanel.add( label );
		
		keyPressHandler = new KeyPressHandler()
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
							okBtnPressed();
						}
					} );
		        }
			}
		};
		
		tmpPanel = new FlowPanel();
		m_currentPwdTxtBox = new PasswordTextBox();
		m_currentPwdTxtBox.addKeyPressHandler( keyPressHandler );
		tmpPanel.add( m_currentPwdTxtBox );
		mainPanel.add( tmpPanel );
		
		label = new Label( messages.changePasswordDlg_NewPasswordLabel() );
		label.addStyleName( "changePasswordDlg_Label" );
		mainPanel.add( label );
		
		tmpPanel = new FlowPanel();
		m_pwd1TxtBox = new PasswordTextBox();
		m_pwd1TxtBox.addKeyPressHandler( keyPressHandler );
		tmpPanel.add( m_pwd1TxtBox );
		mainPanel.add( tmpPanel );
		
		label = new Label( messages.changePasswordDlg_ConfirmPasswordLabel() );
		label.addStyleName( "changePasswordDlg_Label" );
		mainPanel.add( label );
		
		tmpPanel = new FlowPanel();
		m_pwd2TxtBox = new PasswordTextBox();
		m_pwd2TxtBox.addKeyPressHandler( keyPressHandler );
		tmpPanel.add( m_pwd2TxtBox );
		mainPanel.add( tmpPanel );
		
		return mainPanel;
	}
	
	/**
	 * Issue an rpc request to change the password.  If the rpc request is successful
	 * close this dialog.
	 */
	private void changePasswordAndClose()
	{
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				FlowPanel errorPanel;
				Label label;
				String errMsg;
				
				hideStatusMsg();
				setOkEnabled( true );

				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException gtEx;
					
					gtEx = (GwtTeamingException) caught;
					errMsg = GwtTeaming.getMessages().changePasswordDlg_ErrorChangingPassword( gtEx.getAdditionalDetails() );
				}
				else
					errMsg = GwtTeaming.getMessages().changePasswordDlg_ErrorChangingPassword( caught.toString() );
					
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				ErrorListRpcResponseData erList = ( (ErrorListRpcResponseData) result.getResponseData() );
				final boolean hasErrors = erList.hasErrors(); 
				if ( hasErrors )
				{
					// ...display them.
					FlowPanel errorPanel = getErrorPanel();
					errorPanel.clear();
					for ( ErrorInfo error:  erList.getErrorList() )
					{
						Label label = new Label( error.getMessage() );
						label.addStyleName( "dlgErrorLabel" );
						errorPanel.add( label );
					}
					showErrorPanel();
				}

				GwtClientHelper.deferCommand( new ScheduledCommand()
				{
					@Override
					public void execute() 
					{
						hideStatusMsg();
						setOkEnabled( true );

						// If we didn't display any errors...
						if ( ! hasErrors )
						{
							// ...close the dialog.
							hide();
						}
					}
				} );
			}						
		};
		
		// Issue an rpc request to change the password.
		{
			showStatusMsg( GwtTeaming.getMessages().changePasswordDlg_ChangingPassword(), "dlgBox_statusPanel_relative" );

			ChangePasswordCmd cmd = new ChangePasswordCmd( getOldPwd(), getNewPwd(), m_userId );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * This gets called when the user presses ok.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		String pwd1;
		String pwd2;
		
		// Did the user enter their current password?
		pwd1 = getOldPwd();
		if ( pwd1 == null || pwd1.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().changePasswordDlg_EnterCurrentPwd() );
			m_currentPwdTxtBox.setFocus( true );
			return false;
		}
		
		// Did the user enter a new password?
		pwd1 = getNewPwd();
		pwd2 = getNewPwd2();
		if ( (pwd1 != null && pwd1.length() > 0) || (pwd2 != null && pwd2.length() > 0) )
		{
			boolean equal;
			
			// Do the passwords match.
			equal = true;
			if ( pwd1 != null && pwd1.equals( pwd2 ) == false )
				equal = false;
			else if ( pwd2 != null && pwd2.equals( pwd1 ) == false )
				equal = false;
			
			if ( equal == false )
			{
				Window.alert( GwtTeaming.getMessages().changePasswordDlg_PasswordsDoNotMatch() );
				m_pwd1TxtBox.setFocus( true );
				return false;
			}
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().changePasswordDlg_PasswordCannotBeEmpty() );
			m_pwd1TxtBox.setFocus( true );
			return false;
		}
		
		clearErrorPanel();
		hideErrorPanel();
		setOkEnabled( false );

		// Issue an rpc request to change the password.  If the rpc request is successful,
		// close this dialog.
		changePasswordAndClose();
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully change the password
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what because editSuccessful() does the work.
		return Boolean.TRUE;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_currentPwdTxtBox;
	}
	
	/**
	 * 
	 */
	private String getNewPwd()
	{
		return m_pwd1TxtBox.getValue();
	}
	
	/**
	 * 
	 */
	private String getNewPwd2()
	{
		return m_pwd2TxtBox.getValue();
	}
	
	/**
	 * 
	 */
	private String getOldPwd()
	{
		return m_currentPwdTxtBox.getValue();
	}
	
	/**
	 * 
	 */
	public void init( String changeHint, Long userId )
	{
		m_userId = userId;
		
		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );
		
		boolean hasHint = GwtClientHelper.hasString( changeHint );
		m_changePasswordHintLabel.setVisible( hasHint );
		m_changePasswordHintLabel.setText( hasHint ? changeHint : "" );

		// Clear existing data in the controls.
		m_currentPwdTxtBox.setValue( "" );
		m_pwd1TxtBox.setValue( "" );
		m_pwd2TxtBox.setValue( "" );
	}
	
	public void init( String changeHint )
	{
		// Always use the initial form of the method.
		init( changeHint, null );	// null -> Change it for the currently logged in user.
	}
	
	public void init()
	{
		// Always use the initial form of the method.
		init( GwtTeaming.getMessages().changePasswordDlg_ChangeDefaultPasswordHint() );
	}
	
	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
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
	public void onDetach()
	{
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we haven't allocated a list to track events we've registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
											GwtTeaming.getEventBus(),
											REGISTERED_EVENTS,
											this,
											m_registeredEventHandlers );
		}
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}

	
	
	/**
	 * Loads the ChangePasswordDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync( final ChangePasswordDlgClient cpDlgClient )
	{
		GWT.runAsync( ChangePasswordDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ChangePasswordDlg() );
				if ( cpDlgClient != null )
				{
					cpDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ChangePasswordDlg cpDlg;
				
				cpDlg = new ChangePasswordDlg();
				cpDlgClient.onSuccess( cpDlg );
			}
		});
	}
}
