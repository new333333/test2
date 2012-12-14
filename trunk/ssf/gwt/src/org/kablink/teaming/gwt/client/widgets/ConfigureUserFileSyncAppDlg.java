/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtUserFileSyncAppConfig;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserFileSyncAppConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserFileSyncAppConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserFileSyncAppConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;


/**
 * 
 * @author jwootton
 *
 */
public class ConfigureUserFileSyncAppDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private CheckBox m_enableFileSyncAccessCB;
	private CheckBox m_allowPwdCacheCB;

	private List<Long> m_userIds;
	private ArrayList<Long> m_listOfRemainingUserIds;	// This is the list we draw from when we are saving the config.
	private ArrayList<Long> m_nextBatchOfUserIds;
	
	private static int BATCH_SIZE = 10;

	/**
	 * Callback interface to interact with the "configure user file sync app" dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfigureUserFileSyncAppDlgClient
	{
		void onSuccess( ConfigureUserFileSyncAppDlg cufsaDlg );
		void onUnavailable();
	}

	

	/**
	 * 
	 */
	private ConfigureUserFileSyncAppDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().configureUserFileSyncAppDlgHeader( "1" ), this, null, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlowPanel tmpPanel;
		FlowPanel ckboxPanel;
		Label label;

		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		label = new Label( messages.fileSyncAppHeader2() );
		mainPanel.add( label );
		
		ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName( "marginleft1" );
		ckboxPanel.addStyleName( "marginbottom2" );
		mainPanel.add( ckboxPanel );
		
		// Add the controls for enable/disable File Sync App
		{
			String productName;
			
			productName = "Vibe";
			if ( GwtClientHelper.isLicenseFilr() )
				productName = "Filr";
			
			m_enableFileSyncAccessCB = new CheckBox( messages.fileSyncAppAllowAccess( productName ) );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_enableFileSyncAccessCB );
			ckboxPanel.add( tmpPanel );
		}
		
		// Create the "Allow desktop application to cache password"
		m_allowPwdCacheCB = new CheckBox( messages.fileSyncAppAllowCachePwd() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowPwdCacheCB );
		ckboxPanel.add( tmpPanel );
		
		return mainPanel;
	}
	
	/**
	 * This method gets called when user user presses ok.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		if ( m_userIds != null && m_userIds.size() > 0 )
		{
			final GwtUserFileSyncAppConfig config;
			Scheduler.ScheduledCommand cmd;

			config = (GwtUserFileSyncAppConfig) obj;

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute() 
				{
					clearErrorPanel();
					hideErrorPanel();
					
					// We save the config for users in a batch of 10.  m_listOfRemainingUserIds
					// is the list we work from.
					if ( m_listOfRemainingUserIds == null )
						m_listOfRemainingUserIds = new ArrayList<Long>();
					else
						m_listOfRemainingUserIds.clear();
					for ( Long userId : m_userIds )
					{
						m_listOfRemainingUserIds.add( userId );
					}
					
					// Disable the Ok button.
					setOkEnabled( false );

					// Issue an rpc request to save the config for the first n users.
					saveConfigForNextBatchOfUsers( config );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the configuration.
		return false;
	}

	/**
	 * Return whether the value entered by the user for allowing the file sync app to cache passwords
	 */
	private boolean getAllowCachePwd()
	{
		return m_allowPwdCacheCB.getValue();
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtFileSyncAppConfiguration object.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtUserFileSyncAppConfig config;
		
		config = new GwtUserFileSyncAppConfig();

		// Get whether the File Sync App is enabled.
		config.setIsFileSyncAppEnabled( getIsFileSyncAppEnabled() );
		
		// Get whether the file sync app can cache the user's password
		config.setAllowCachePwd( getAllowCachePwd() );
		
		return config;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_enableFileSyncAccessCB;
	}
	
	/**
	 * Return whether the File Sync App is enabled.
	 */
	private boolean getIsFileSyncAppEnabled()
	{
		if ( m_enableFileSyncAccessCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * Get the list of the next n userIds
	 */
	private ArrayList<Long> getNextBatchOfUserIds()
	{
		if ( m_listOfRemainingUserIds != null && m_listOfRemainingUserIds.size() > 0 )
		{
			int cnt;
			
			if ( m_nextBatchOfUserIds == null )
				m_nextBatchOfUserIds = new ArrayList<Long>();
			else
				m_nextBatchOfUserIds.clear();
			
			for ( cnt = 0; cnt < BATCH_SIZE && m_listOfRemainingUserIds.size() > 0; ++cnt )
			{
				m_nextBatchOfUserIds.add( m_listOfRemainingUserIds.get( 0 ) );
				
				// Remove this user id from the working list.
				m_listOfRemainingUserIds.remove( 0 );
			}
			
			return m_nextBatchOfUserIds;
		}
		
		return null;
	}
	
	/**
	 * 
	 */
	public void init( List<Long> userIds )
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		if ( userIds == null )
			return;

		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );

		m_userIds = userIds;
		
		setCaption( GwtTeaming.getMessages().configureUserFileSyncAppDlgHeader( String.valueOf( userIds.size() ) ) );
		
		// Create a callback that will be called when we get the file sync configuration.
		rpcReadCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetUserFileSyncAppConfig() );
			}
	
			/**
			 * We successfully retrieved the user's file sync app configuration.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				final GwtUserFileSyncAppConfig config;
				
				config = (GwtUserFileSyncAppConfig) response.getResponseData();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute() 
					{
						init( config );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// If we are only dealing with 1 user, issue an ajax request to get the
		// User's file sync app configuration.
		if ( userIds.size() == 1 )
		{
			GetUserFileSyncAppConfigCmd cmd;
			
			// Issue an ajax request to get the user's file sync app configuration from the db.
			cmd = new GetUserFileSyncAppConfigCmd();
			cmd.setUserId( userIds.get( 0 ) );
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
		else
		{
			GwtUserFileSyncAppConfig config = null;
			
			init( config );
		}
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	private void init( GwtUserFileSyncAppConfig config )
	{
		if ( config != null )
		{
			// Initialize the on/off radio buttons.
			m_enableFileSyncAccessCB.setValue( config.getIsFileSyncAppEnabled() );
				
			// Initialize the allow pwd  cache checkbox
			m_allowPwdCacheCB.setValue( config.getAllowCachePwd() );
		}
		else
		{
			m_enableFileSyncAccessCB.setValue( false );
			m_allowPwdCacheCB.setValue( false );
		}
		
		hideErrorPanel();
	}
	
	/**
	 * Issue an rpc request to save the config for the next n users.
	 */
	private void saveConfigForNextBatchOfUsers( final GwtUserFileSyncAppConfig config )
	{
		ArrayList<Long> userIds;

		// Get the next batch of user ids
		userIds = getNextBatchOfUserIds();
		if ( userIds != null && userIds.size() > 0 )
		{
			AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
			SaveUserFileSyncAppConfigCmd cmd;

			// Update the Saving n of nn message
			updateStatusMsg();
			
			// Create the callback that will be used when we issue an ajax request to save
			// the user's file sync app configuration.
			rpcSaveCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable caught )
				{
					FlowPanel errorPanel;
					Label label;
					String errMsg = null;
					
					hideStatusMsg();
					setOkEnabled( true );

					// Get the panel that holds the errors.
					errorPanel = getErrorPanel();
					
					if ( errMsg == null )
					{
						errMsg = GwtTeaming.getMessages().configureUserFileSyncAppDlgOnSaveUnknownException( caught.toString() );
					}
					
					label = new Label( errMsg );
					label.addStyleName( "dlgErrorLabel" );
					errorPanel.add( label );
					
					showErrorPanel();
				}
	
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					VibeRpcResponseData data;
					
					data = response.getResponseData();
					if ( data instanceof SaveUserFileSyncAppConfigRpcResponseData )
					{
						SaveUserFileSyncAppConfigRpcResponseData responseData;
						ArrayList<String> errors;
						
						// Get any errors that may have happened
						responseData = (SaveUserFileSyncAppConfigRpcResponseData) data;
						errors = responseData.getErrors();
						if ( errors != null && errors.size() > 0 )
						{
							FlowPanel errorPanel;
							
							// Is the error panel already visible?
							errorPanel = getErrorPanel();
							if ( errorPanel.isVisible() == false )
							{
								Label label;
								
								// No, add an error header to it
								label = new Label( GwtTeaming.getMessages().configureUserFileSyncAppDlgErrorHeader() );
								errorPanel.add( label );
							}
							
							for ( String nextErr: errors )
							{
								Label label;
							
								label = new Label( nextErr );
								label.addStyleName( "dlgErrorLabel" );
								label.addStyleName( "marginleft1" );
								errorPanel.add( label );
							}
							
							showErrorPanel();
						}
					}
					
					cmd = new Scheduler.ScheduledCommand() 
					{
						@Override
						public void execute() 
						{
							// Call this method again to see if there are any users that we need
							// to save the config for.
							saveConfigForNextBatchOfUsers( config );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Issue an ajax request to save the user's file sync app configuration to the db.  rpcSaveCallback will
			// be called when we get the response back.
			cmd = new SaveUserFileSyncAppConfigCmd( config, userIds );
			GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
		}
		else
		{
			FlowPanel errorPanel;
			
			// We have saved the config to all the users.

			// Enable the Ok button.
			hideStatusMsg();
			setOkEnabled( true );

			// Were there any errors displayed?
			errorPanel = getErrorPanel();
			if ( errorPanel.isVisible() == false )
			{
				// No
				// Close the dialog.
				hide();
			}
		}
	}
	
	/**
	 * Update the status message that displays Saving n of n
	 */
	private void updateStatusMsg()
	{
		if ( m_userIds != null && m_listOfRemainingUserIds != null )
		{
			String msg;
			int total;
			int remaining;

			total = m_userIds.size();
			remaining = m_listOfRemainingUserIds.size();
			msg = GwtTeaming.getMessages().configureUserFileSyncDlgSaving(
																		String.valueOf( total-remaining ),
																		String.valueOf( total ) );
			showStatusMsg( msg );
		}
	}
	
	
	/**
	 * Loads the ConfigureUserFileSyncAppDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final int width,
							final int height,
							final ConfigureUserFileSyncAppDlgClient cufsaDlgClient )
	{
		GWT.runAsync( ConfigureUserFileSyncAppDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureUserFileSyncAppDlg() );
				if ( cufsaDlgClient != null )
				{
					cufsaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ConfigureUserFileSyncAppDlg cufsaDlg;
				
				cufsaDlg = new ConfigureUserFileSyncAppDlg(
												autoHide,
												modal,
												left,
												top,
												width,
												height );
				cufsaDlgClient.onSuccess( cufsaDlg );
			}
		});
	}
}
