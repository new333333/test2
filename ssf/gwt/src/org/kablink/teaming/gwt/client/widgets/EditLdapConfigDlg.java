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


import java.util.Map;
import java.util.TreeMap;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtLocales;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTimeZones;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetLocalesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTimeZonesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;


/**
 * 
 * @author jwootton
 *
 */
public class EditLdapConfigDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private CheckBox m_syncUserProfilesCheckBox;
	private CheckBox m_registerUserProfilesAutomaticallyCheckBox;
	private RadioButton m_disableUsersRB;
	private RadioButton m_deleteUsersRB;
	private CheckBox m_deleteWorkspaceCheckBox;
	private ListBox m_timeZonesListbox;
	private ListBox m_localesListbox;

	private CheckBox m_syncGroupProfilesCheckBox;
	private CheckBox m_registerGroupProfilesAutomaticallyCheckBox;
	private CheckBox m_syncGroupMembershipCheckBox;
	private CheckBox m_deleteGroupsCheckBox;
	
	private ScheduleWidget m_scheduleWidget;
	
	private CheckBox m_allowLocalLoginCheckBox;
	
	/**
	 * Callback interface to interact with the "edit ldap config" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditLdapConfigDlgClient
	{
		void onSuccess( EditLdapConfigDlg elcDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private EditLdapConfigDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().editLdapConfigDlg_Header(), this, null, null );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		TabPanel tabPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		tabPanel = new TabPanel();
		tabPanel.addStyleName( "vibe-tabPanel" );

		mainPanel.add( tabPanel );

		// Create a panel to hold the user configuration
		{
			Panel userPanel;
			
			userPanel = createUserConfigPanel( messages );
			tabPanel.add( userPanel, messages.editLdapConfigDlg_UsersTab() );
		}
		
		// Create a panel to hold the group configuration
		{
			Panel groupPanel;
			
			groupPanel = createGroupConfigPanel( messages );
			tabPanel.add( groupPanel, messages.editLdapConfigDlg_GroupsTab() );
		}

		// Add controls dealing with sync schedule
		{
			Panel schedulePanel;
			
			schedulePanel = createSchedulePanel( messages );
			tabPanel.add( schedulePanel, messages.editLdapConfigDlg_ScheduleTab() );
		}
		
		// Add controls dealing with local user accounts
		{
			Panel localPanel;
			
			localPanel = createLocalAccountsPanel( messages );
			tabPanel.add( localPanel, messages.editLdapConfigDlg_LocalUserAccountsTab() );
		}
		
		tabPanel.selectTab( 0 );
		
		return mainPanel;
	}

	/**
	 * 
	 */
	private Panel createGroupConfigPanel( GwtTeamingMessages messages )
	{
		FlowPanel groupPanel;
		FlowPanel tmpPanel;
		
		groupPanel = new FlowPanel();
		
		tmpPanel = new FlowPanel();
		m_registerGroupProfilesAutomaticallyCheckBox = new CheckBox( messages.editLdapConfigDlg_RegisterGroupProfilesAutomatically() );
		tmpPanel.add( m_registerGroupProfilesAutomaticallyCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_syncGroupProfilesCheckBox = new CheckBox( messages.editLdapConfigDlg_SyncGroupProfiles() );
		tmpPanel.add( m_syncGroupProfilesCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_syncGroupMembershipCheckBox = new CheckBox( messages.editLdapConfigDlg_SyncGroupMembership() );
		tmpPanel.add( m_syncGroupMembershipCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_deleteGroupsCheckBox = new CheckBox( messages.editLdapConfigDlg_DeleteGroupsLabel() );
		tmpPanel.add( m_deleteGroupsCheckBox );
		groupPanel.add( tmpPanel );
		
		return groupPanel;
	}

	/**
	 * 
	 */
	private Panel createLocalAccountsPanel( GwtTeamingMessages messages )
	{
		FlowPanel localPanel;
		FlowPanel tmpPanel;
		
		localPanel = new FlowPanel();
		
		tmpPanel = new FlowPanel();
		m_allowLocalLoginCheckBox = new CheckBox( messages.editLdapConfigDlg_AllowLocalLoginLabel() );
		tmpPanel.add( m_allowLocalLoginCheckBox );
		localPanel.add( tmpPanel );
		
		return localPanel;
	}

	/**
	 * 
	 */
	private Panel createSchedulePanel( GwtTeamingMessages messages )
	{
		FlowPanel schedulePanel;
		
		schedulePanel = new FlowPanel();
		
		m_scheduleWidget = new ScheduleWidget( messages.editLdapConfigDlg_EnableSyncScheduleLabel() );
		schedulePanel.add( m_scheduleWidget );
		
		return schedulePanel;
	}

	/**
	 * 
	 */
	private Panel createUserConfigPanel( GwtTeamingMessages messages )
	{
		FlowPanel userPanel;
		Label label;
		
		userPanel = new FlowPanel();
		
		// Add the controls dealing with syncing users
		{
			FlowPanel tmpPanel;

			tmpPanel = new FlowPanel();
			m_registerUserProfilesAutomaticallyCheckBox = new CheckBox( messages.editLdapConfigDlg_RegisterUserProfilesAutomatically() );
			tmpPanel.add( m_registerUserProfilesAutomaticallyCheckBox );
			userPanel.add( tmpPanel );
	
			tmpPanel = new FlowPanel();
			m_syncUserProfilesCheckBox = new CheckBox( messages.editLdapConfigDlg_SyncUserProfiles() );
			tmpPanel.add( m_syncUserProfilesCheckBox );
			userPanel.add( tmpPanel );
		}
		
		// Add the controls dealing with deleting users
		{
			FlowPanel tmpPanel;
			ClickHandler clickHandler;
			
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							if ( m_deleteUsersRB.getValue() == true )
							{
								// Warn the admin about deleting users
								Window.alert( GwtTeaming.getMessages().editLdapConfigDlg_DeleteUsersWarning() );
							}
							
							danceDlg();
						}
					};
					
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "margintop3" );
			m_disableUsersRB = new RadioButton( "notInLdap", messages.editLdapConfigDlg_DisableUserLabel() );
			m_disableUsersRB.addClickHandler( clickHandler );
			tmpPanel.add( m_disableUsersRB );
			userPanel.add( tmpPanel );
			
			tmpPanel = new FlowPanel();
			m_deleteUsersRB = new RadioButton( "notInLdap", messages.editLdapConfigDlg_DeleteUserLabel() );
			m_deleteUsersRB.addClickHandler( clickHandler );
			tmpPanel.add( m_deleteUsersRB );
			userPanel.add( tmpPanel );
			
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "marginleft2" );
			m_deleteWorkspaceCheckBox = new CheckBox( messages.editLdapConfigDlg_DeleteWorkspaceLabel() );
			tmpPanel.add( m_deleteWorkspaceCheckBox );
			userPanel.add( tmpPanel );
		}
		
		// Add the controls dealing time zone
		{
			label = new Label( messages.editLdapConfigDlg_DefaultTimeZoneLabel() );
			label.addStyleName( "margintop3" );
			userPanel.add( label );
			
			m_timeZonesListbox = new ListBox( false );
			m_timeZonesListbox.setVisibleItemCount( 1 );
			m_timeZonesListbox.addStyleName( "marginleft1" );
			userPanel.add( m_timeZonesListbox );
		}
		
		// Add the controls dealing with locale
		{
			label = new Label( messages.editLdapConfigDlg_DefaultLocaleLabel() );
			label.addStyleName( "margintop3" );
			userPanel.add( label );
			
			m_localesListbox = new ListBox( false );
			m_localesListbox.setVisibleItemCount( 1 );
			m_localesListbox.addStyleName( "marginleft1" );
			userPanel.add( m_localesListbox );
		}

		return userPanel;
	}
	
	/**
	 * 
	 */
	private void danceDlg()
	{
		if ( m_deleteUsersRB.getValue() == true )
			m_deleteWorkspaceCheckBox.setEnabled( true );
		else
			m_deleteWorkspaceCheckBox.setEnabled( false );
	}
	
	/**
	 * This gets called when the user presses ok.  Issue an rpc request to save the
	 * "allow adhoc folders" setting
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
/**
		SaveAdhocFolderSettingCmd cmd;

		// Execute a GWT RPC command to save the user access configuration
		cmd = new SaveAdhocFolderSettingCmd( m_userId, (Boolean) obj );
		
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_SaveAdhocFolderSetting() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof BooleanRpcResponseData )
				{
					BooleanRpcResponseData responseData;
					
					responseData = (BooleanRpcResponseData) response.getResponseData();
					if ( responseData.getBooleanValue() == true )
						hide();
				}
			}
		});
**/		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the user access configuration.
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
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
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public HelpData getHelpData()
	{
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName( HelpData.ADMIN_GUIDE );
		helpData.setPageId( "ldap" );
		
		return helpData;
	}

	/**
	 * Issue an rpc request to get the ldap configuration data from the server.
	 */
	private void getLdapConfigurationFromServer()
	{
		GetLdapConfigCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		showStatusMsg( GwtTeaming.getMessages().editLdapConfigDlg_ReadingLdapConfig() );
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_GetLdapConfig() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtLdapConfig )
				{
					final GwtLdapConfig ldapConfig;
					ScheduledCommand cmd;
					
					ldapConfig = (GwtLdapConfig) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							hideStatusMsg();
							init( ldapConfig );

							// Get a list of time zones
							getTimeZonesFromServer( ldapConfig );
							
							// Get the list of locales
							getLocalesFromServer( ldapConfig );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Execute a GWT RPC command to get the ldap configuration
		cmd = new GetLdapConfigCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Issue an rpc request to get a list of locales from the server.
	 */
	private void getLocalesFromServer( final GwtLdapConfig ldapConfig )
	{
		GetLocalesCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		// Have we already retrieved the list of locales?
		if ( m_localesListbox.getItemCount() > 0 )
		{
			// Yes
			return;
		}
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_GetLocales() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtLocales )
				{
					final GwtLocales locales;
					ScheduledCommand cmd;
					
					locales = (GwtLocales) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							initLocales( locales, ldapConfig );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Execute a GWT RPC command to get the list of locales
		cmd = new GetLocalesCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Issue an rpc request to get a list of time zones from the server.
	 */
	private void getTimeZonesFromServer( final GwtLdapConfig ldapConfig )
	{
		GetTimeZonesCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		// Have we already retrieved the list of time zones?
		if ( m_timeZonesListbox.getItemCount() > 0 )
		{
			// Yes
			return;
		}
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_GetTimeZones() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtTimeZones)
				{
					final GwtTimeZones timeZones;
					ScheduledCommand cmd;
					
					timeZones = (GwtTimeZones) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							initTimeZones( timeZones, ldapConfig );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Execute a GWT RPC command to get the time zones
		cmd = new GetTimeZonesCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * 
	 */
	public void init()
	{
		// Get the ldap configuration data from the server.
		getLdapConfigurationFromServer();
	}
	
	/**
	 * 
	 */
	private void init( GwtLdapConfig ldapConfig )
	{
		if ( ldapConfig == null )
			return;
		
		m_syncUserProfilesCheckBox.setValue( ldapConfig.getSyncUserProfiles() );
		m_registerUserProfilesAutomaticallyCheckBox.setValue( ldapConfig.getRegisterUserProfilesAutomatically() );
		
		if ( ldapConfig.getDeleteLdapUsers() )
		{
			m_disableUsersRB.setValue( false );
			m_deleteUsersRB.setValue( true );
		}
		else
		{
			m_disableUsersRB.setValue( true );
			m_deleteUsersRB.setValue( false );
		}
		
		m_deleteWorkspaceCheckBox.setValue( ldapConfig.getDeleteUserWorkspace() );

		m_syncGroupProfilesCheckBox.setValue( ldapConfig.getSyncGroupProfiles() );
		m_registerGroupProfilesAutomaticallyCheckBox.setValue( ldapConfig.getRegisterGroupProfilesAutomatically() );
		m_syncGroupMembershipCheckBox.setValue( ldapConfig.getSyncGroupMembership() );
		m_deleteGroupsCheckBox.setValue( ldapConfig.getDeleteNonLdapGroups() );
		
		m_allowLocalLoginCheckBox.setValue( ldapConfig.getAllowLocalLogin() );
		
		if ( m_timeZonesListbox.getItemCount() > 0 )
		{
			String defaultTimeZone;
			
			// Get the default time zone
			defaultTimeZone = ldapConfig.getTimeZone();
			GwtClientHelper.selectListboxItemByValue( m_timeZonesListbox, defaultTimeZone );
		}
		
		if ( m_localesListbox.getItemCount() > 0 )
		{
			String defaultLocale;
			
			defaultLocale = ldapConfig.getLocale();
			GwtClientHelper.selectListboxItemByValue( m_localesListbox, defaultLocale );
		}
		
		danceDlg();
	}
	
	/**
	 * 
	 */
	private void initLocales( GwtLocales locales, GwtLdapConfig ldapConfig )
	{
		TreeMap<String,String> listOfLocales;
		String defaultLocale;
		
		if ( locales == null )
			return;
		
		listOfLocales = locales.getListOfLocales();
		if ( listOfLocales == null )
			return;
		
		for ( Map.Entry<String,String> mapEntry : listOfLocales.entrySet() )
		{
			String localeDisplayName;
			String localeId;
			
			// Get the name of the next locale.
			localeDisplayName = mapEntry.getKey();
			
			// Get the id of the next locale.
			localeId = mapEntry.getValue();

			m_localesListbox.addItem( localeDisplayName, localeId );
		}
		
		// Get the default time zone
		defaultLocale = ldapConfig.getLocale();
		GwtClientHelper.selectListboxItemByValue( m_localesListbox, defaultLocale );
	}
	
	/**
	 * 
	 */
	private void initTimeZones( GwtTimeZones timeZones, GwtLdapConfig ldapConfig )
	{
		TreeMap<String,String> listOfTimeZones;
		String defaultTimeZone;
		
		if ( timeZones == null )
			return;
		
		listOfTimeZones = timeZones.getListOfTimeZones();
		if ( listOfTimeZones == null )
			return;
		
		for ( Map.Entry<String,String> mapEntry : listOfTimeZones.entrySet() )
		{
			String tzId;
			String tzName;
			
			tzId = mapEntry.getValue();
			tzName = mapEntry.getKey();
			
			m_timeZonesListbox.addItem( tzName, tzId );
		}
		
		// Get the default time zone
		defaultTimeZone = ldapConfig.getTimeZone();
		GwtClientHelper.selectListboxItemByValue( m_timeZonesListbox, defaultTimeZone );
	}
	
	/**
	 * Loads the EditLdapConfigDlg split point and returns an instance
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
							final EditLdapConfigDlgClient elcDlgClient )
	{
		GWT.runAsync( EditLdapConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapConfigDlg() );
				if ( elcDlgClient != null )
				{
					elcDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditLdapConfigDlg elcDlg;
				
				elcDlg = new EditLdapConfigDlg(
											autoHide,
											modal,
											left,
											top,
											width,
											height );
				elcDlgClient.onSuccess( elcDlg );
			}
		});
	}
}
