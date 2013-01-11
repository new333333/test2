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
import org.kablink.teaming.gwt.client.GwtUserMobileAppsConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserMobileAppsConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserMobileAppsConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserMobileAppsConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;


/**
 * This dialog is used to edit the user's mobile apps settings.
 * @author jwootton
 *
 */
public class ConfigureUserMobileAppsDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private RadioButton m_useGlobalSettingsRB;
	private RadioButton m_useUserSettingsRB;
	private CheckBox m_enableMobileAppsAccessCB;
	private CheckBox m_allowPwdCacheCB;
	private CheckBox m_allowOfflineContentCB;
	private CheckBox m_allowPlayWithOtherAppsCB;
	
	private List<Long> m_userIds;
	private ArrayList<Long> m_listOfRemainingUserIds;	// This is the list we draw from when we are saving the config.
	private ArrayList<Long> m_nextBatchOfUserIds;
	
	private static int BATCH_SIZE = 10;
	
	/**
	 * Callback interface to interact with the "configure user mobile applications" dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfigureUserMobileAppsDlgClient
	{
		void onSuccess( ConfigureUserMobileAppsDlg cumaDlg );
		void onUnavailable();
	}

	

	/**
	 * 
	 */
	private ConfigureUserMobileAppsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().configureUserMobileAppsDlgHeader( "1" ), this, null, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlowPanel userPanel;
		FlowPanel ckboxPanel;
		FlowPanel tmpPanel;
		ClickHandler clickHandler;

		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_useGlobalSettingsRB = new RadioButton( "settingScope", messages.configureMobileAppsDlgUseGlobalSettings() );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginbottom1" );
		tmpPanel.add( m_useGlobalSettingsRB );
		mainPanel.add( tmpPanel );
		
		m_useUserSettingsRB = new RadioButton( "settingScope", messages.configureMobileAppsDlgUseUserSettings() );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginbottom1" );
		tmpPanel.add( m_useUserSettingsRB );
		mainPanel.add( tmpPanel );
		
		userPanel = new FlowPanel();
		userPanel.addStyleName( "marginleft1" );
		mainPanel.add( userPanel );

		ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName( "marginleft1" );
		ckboxPanel.addStyleName( "marginbottom2" );
		userPanel.add( ckboxPanel );
		
		clickHandler = new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				m_useGlobalSettingsRB.setValue( false );
				m_useUserSettingsRB.setValue( true );
			}
		};
		
		// Add the controls for the "allow mobile applications to access Filr"
		{
			String productName;
			
			productName = "Vibe";
			if ( GwtClientHelper.isLicenseFilr() )
				productName = "Filr";
			
			m_enableMobileAppsAccessCB = new CheckBox( messages.configureMobileAppsDlgAllowAccess( productName ) );
			m_enableMobileAppsAccessCB.addClickHandler( clickHandler );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_enableMobileAppsAccessCB );
			ckboxPanel.add( tmpPanel );
		}
		
		// Create the "Allow mobile applications to cache password"
		m_allowPwdCacheCB = new CheckBox( messages.configureMobileAppsDlgAllowCachePwd() );
		m_allowPwdCacheCB.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowPwdCacheCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the "Allow mobile applications to cache content offline"
		m_allowOfflineContentCB = new CheckBox( messages.configureMobileAppsDlgAllowCacheContent() );
		m_allowOfflineContentCB.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowOfflineContentCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the "Allow mobile applications to interact with other applications"
		m_allowPlayWithOtherAppsCB = new CheckBox( messages.configureMobileAppsDlgAllowPlayWithOtherApps() );
		m_allowPlayWithOtherAppsCB.addClickHandler( clickHandler );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowPlayWithOtherAppsCB );
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
			final GwtUserMobileAppsConfig mobileAppsConfig;
			Scheduler.ScheduledCommand cmd;
	
			mobileAppsConfig = (GwtUserMobileAppsConfig) obj;
			
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
					saveConfigForNextBatchOfUsers( mobileAppsConfig );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the configuration.
		return false;
	}

	/**
	 * Return whether the value entered by the user for allowing mobile apps to cache passwords
	 */
	private boolean getAllowCachePwd()
	{
		return m_allowPwdCacheCB.getValue();
	}
	
	/**
	 * Return whether the mobile apps can cache content offline
	 */
	private boolean getAllowOfflineContent()
	{
		return m_allowOfflineContentCB.getValue();
	}
	
	/**
	 * Return whether mobile apps can interact with other applications
	 */
	private boolean getAllowPlayWithOtherApps()
	{
		return m_allowPlayWithOtherAppsCB.getValue();
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtMobileAppsConfiguration object.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtUserMobileAppsConfig mobileAppsConfig;
		
		mobileAppsConfig = new GwtUserMobileAppsConfig();

		// Get whether to use the global settings
		mobileAppsConfig.setUseGlobalSettings( getUseGlobalSettings() );

		// Get whether mobile apps can access Filr
		mobileAppsConfig.setMobileAppsEnabled( getMobileAppsEnabled() );
		
		// Get whether mobile apps can cache the user's password
		mobileAppsConfig.setAllowCachePwd( getAllowCachePwd() );
		
		// Get whether mobile apps can cache content
		mobileAppsConfig.setAllowCacheContent( getAllowOfflineContent() );
		
		// Get whether mobile apps can interact with other apps
		mobileAppsConfig.setAllowPlayWithOtherApps( getAllowPlayWithOtherApps() );
		
		return mobileAppsConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_enableMobileAppsAccessCB;
	}
	
	/**
	 * Return whether mobile apps are enabled.
	 */
	private boolean getMobileAppsEnabled()
	{
		if ( m_enableMobileAppsAccessCB.getValue() == Boolean.TRUE )
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
	private boolean getUseGlobalSettings()
	{
		return m_useGlobalSettingsRB.getValue();
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
		
		setCaption( GwtTeaming.getMessages().configureUserMobileAppsDlgHeader( String.valueOf( userIds.size() ) ) );
		
		// Create a callback that will be called when we get the mobile apps configuration.
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
					GwtTeaming.getMessages().rpcFailure_GetUserMobileAppsConfiguration() );
			}
	
			/**
			 * We successfully retrieved the Mobile Apps configuration.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				final GwtUserMobileAppsConfig mobileAppsConfig;
				
				mobileAppsConfig = (GwtUserMobileAppsConfig) response.getResponseData();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute() 
					{
						init( mobileAppsConfig );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// If we are only dealing with 1 user, issue an ajax request to get the
		// User's Mobile Apps configuration.
		if ( userIds.size() == 1 )
		{
			GetUserMobileAppsConfigCmd cmd;
			
			// Issue an ajax request to get the Mobile Apps configuration from the db.
			cmd = new GetUserMobileAppsConfigCmd();
			cmd.setUserId( userIds.get( 0 ) );
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
		else
		{
			GwtUserMobileAppsConfig config = null;
			
			init( config );
		}
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	private void init( GwtUserMobileAppsConfig mobileAppsConfig )
	{
		if ( mobileAppsConfig != null )
		{
			m_useGlobalSettingsRB.setValue( mobileAppsConfig.getUseGlobalSettings() );
			m_useUserSettingsRB.setValue( !mobileAppsConfig.getUseGlobalSettings() );
			
			// Initialize whether mobile apps can access Filr
			m_enableMobileAppsAccessCB.setValue( mobileAppsConfig.getMobileAppsEnabled() );
				
			// Initialize the allow pwd cache checkbox
			m_allowPwdCacheCB.setValue( mobileAppsConfig.getAllowCachePwd() );
	
			// Initialize the offline content
			m_allowOfflineContentCB.setValue( mobileAppsConfig.getAllowCacheContent() );
			
			// Initialize the allow mobile apps to play with others
			m_allowPlayWithOtherAppsCB.setValue( mobileAppsConfig.getAllowPlayWithOtherApps() );
		}
		else
		{
			m_useGlobalSettingsRB.setValue( true );
			m_useUserSettingsRB.setValue( false );
			m_enableMobileAppsAccessCB.setValue( false );
			m_allowPwdCacheCB.setValue( false );
			m_allowOfflineContentCB.setValue( false );
			m_allowPlayWithOtherAppsCB.setValue( false );
		}
		
		hideErrorPanel();
	}
	
	/**
	 * Issue an rpc request to save the config for the next n users.
	 */
	private void saveConfigForNextBatchOfUsers( final GwtUserMobileAppsConfig config )
	{
		ArrayList<Long> userIds;

		// Get the next batch of user ids
		userIds = getNextBatchOfUserIds();
		if ( userIds != null && userIds.size() > 0 )
		{
			AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
			SaveUserMobileAppsConfigCmd cmd;

			// Update the Saving n of nn message
			updateStatusMsg();
			
			// Create the callback that will be used when we issue an ajax request to save the user's mobile apps configuration.
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
						errMsg = GwtTeaming.getMessages().configureUserMobileAppsDlgOnSaveUnknownException( caught.toString() );
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
					if ( data instanceof SaveUserMobileAppsConfigRpcResponseData )
					{
						SaveUserMobileAppsConfigRpcResponseData responseData;
						ArrayList<String> errors;
						
						// Get any errors that may have happened
						responseData = (SaveUserMobileAppsConfigRpcResponseData) data;
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
								label = new Label( GwtTeaming.getMessages().configuerUserMobileAppsDlgErrorHeader() );
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

			// Issue an ajax request to save the user's Mobile apps configuration to the db.  rpcSaveCallback will
			// be called when we get the response back.
			cmd = new SaveUserMobileAppsConfigCmd( config, userIds );
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
			msg = GwtTeaming.getMessages().configuerUserMobileAppsDlgSaving(
																		String.valueOf( total-remaining ),
																		String.valueOf( total ) );
			showStatusMsg( msg );
		}
	}
	
	/**
	 * Loads the ConfigureUserMobileAppsDlg split point and returns an instance
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
							final ConfigureUserMobileAppsDlgClient cumaDlgClient )
	{
		GWT.runAsync( ConfigureUserMobileAppsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureUserMobileAppsDlg() );
				if ( cumaDlgClient != null )
				{
					cumaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ConfigureUserMobileAppsDlg cumaDlg;
				
				cumaDlg = new ConfigureUserMobileAppsDlg(
												autoHide,
												modal,
												left,
												top,
												width,
												height );
				cumaDlgClient.onSuccess( cumaDlg );
			}
		});
	}
}
