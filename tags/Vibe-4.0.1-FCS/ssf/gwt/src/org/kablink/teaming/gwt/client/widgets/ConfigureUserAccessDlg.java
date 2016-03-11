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
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserAccessConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserAccessConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.UserAccessConfig;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;

/**
 * ?
 * 
 * @author jwootton
 */
public class ConfigureUserAccessDlg extends DlgBox
	implements EditSuccessfulHandler
{
	CheckBox m_allowGuestAccessCkbox;
	CheckBox m_guestReadOnlyCkbox;
	CheckBox m_allowSelfRegOfInternalUserAccountCkbox;
	CheckBox m_allowSelfRegOfExternalUserAccountCkbox;
	CheckBox m_disableDownloadCkbox;
	CheckBox m_disableWebAccessCkbox;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
	};
	

	/**
	 * Callback interface to interact with the "configure user access" dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfigureUserAccessDlgClient
	{
		void onSuccess( ConfigureUserAccessDlg mnfDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private ConfigureUserAccessDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
									GwtTeaming.getEventBus(),
									m_registeredEvents,
									this );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().configureUserAccessDlg_Header(), this, null, null );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add the "Allow Guest access" checkbox;
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom2" );
			m_allowGuestAccessCkbox = new CheckBox( messages.configureUserAccessDlg_AllowGuestAccessLabel() );
			panel.add( m_allowGuestAccessCkbox );
			mainPanel.add( panel );
		}
		
		// Add the "Allow Guest read only" checkbox;
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom2" );
			m_guestReadOnlyCkbox = new CheckBox( messages.configureUserAccessDlg_AllowGuestReadOnlyLabel() );
			panel.add( m_guestReadOnlyCkbox );
			mainPanel.add( panel );
		}
		
		// Add the "Allow self registration" checkbox if we are running Kablink
		if ( GwtTeaming.m_requestInfo.isKablinkTeaming() )
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom2" );
			m_allowSelfRegOfInternalUserAccountCkbox = new CheckBox( messages.configureUserAccessDlg_AllowSelfRegInternalUserAccountLabel() );
			panel.add( m_allowSelfRegOfInternalUserAccountCkbox );
			mainPanel.add( panel );
		}
		
		// Add the "Disable Download" checkbox;
		if ( GwtClientHelper.isLicenseFilr() )
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom2" );
			m_disableDownloadCkbox = new CheckBox( messages.configureUserAccessDlg_DisableDownloadLabel() );
			panel.add( m_disableDownloadCkbox );
			mainPanel.add( panel );
		}
		
		// Add the "Disable WebAccess" checkbox;
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom2" );
			m_disableWebAccessCkbox = new CheckBox( messages.configureUserAccessDlg_DisableWebAccessLabel() );
			panel.add( m_disableWebAccessCkbox );
			mainPanel.add( panel );
		}
		
		return mainPanel;
	}
	
	/**
	 * 
	 */
	private void danceDlg()
	{
	}
	
	/**
	 * This gets called when the user presses ok.  Issue an rpc request to save the user
	 * access configuration.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		SaveUserAccessConfigCmd cmd;

		// Execute a GWT RPC command to save the user access configuration
		cmd = new SaveUserAccessConfigCmd();
		cmd.setConfig( (UserAccessConfig) obj );
		
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_SaveUserAccessConfig() );
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
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the user access configuration.
		return false;
	}
	
	/**
	 * 
	 */
	private boolean getAllowExternalSelfReg()
	{
		if ( m_allowSelfRegOfExternalUserAccountCkbox != null )
			return m_allowSelfRegOfExternalUserAccountCkbox.getValue();
		
		return false;
	}
	
	/**
	 * 
	 */
	private boolean getAllowGuestAccess()
	{
		return m_allowGuestAccessCkbox.getValue();
	}
	
	/**
	 * 
	 */
	private boolean getGuestReadOnly()
	{
		return m_guestReadOnlyCkbox.getValue();
	}
	
	/**
	 * 
	 */
	private boolean getAllowInternalSelfReg()
	{
		if ( m_allowSelfRegOfInternalUserAccountCkbox != null )
			return m_allowSelfRegOfInternalUserAccountCkbox.getValue();
		
		return false;
	}
	
	/**
	 * 
	 */
	private boolean getDisableDownload()
	{
		boolean reply;
		if ( GwtClientHelper.isLicenseFilr() )
		     reply = m_disableDownloadCkbox.getValue();
		else reply = false;
		return reply;
	}
	
	/**
	 * 
	 */
	private boolean getDisableWebAccess()
	{
		return m_disableWebAccessCkbox.getValue();
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		UserAccessConfig config;
		
		config = new UserAccessConfig();
		config.setAllowExternalUsersViaOpenID( false );
		config.setAllowExternalUsersSelfReg( getAllowExternalSelfReg() );
		config.setAllowGuestAccess( getAllowGuestAccess() );
		config.setGuestReadOnly( getGuestReadOnly() );
		config.setAllowSelfReg( getAllowInternalSelfReg() );
		config.setAllowDownload( !getDisableDownload() );
		config.setAllowWebAccess( !getDisableWebAccess() );
		
		return config;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_allowGuestAccessCkbox;
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
		helpData.setPageId( "access" );
		
		return helpData;
	}

	/**
	 * Issue an rpc request to get the user access information from the server.
	 */
	private void getUserAccessInfoFromServer()
	{
		GetUserAccessConfigCmd cmd;

		// Execute a GWT RPC command asking the server for the user access informaiton
		cmd = new GetUserAccessConfigCmd();
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_GetUserAccessInfo() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof UserAccessConfig )
				{
					final UserAccessConfig config;
					ScheduledCommand cmd;
					
					config = (UserAccessConfig) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							init( config );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		});
	}
	
	/**
	 * 
	 */
	public void init()
	{
		if ( m_allowGuestAccessCkbox != null )
			m_allowGuestAccessCkbox.setValue( false );
		
		if ( m_guestReadOnlyCkbox != null )
			m_guestReadOnlyCkbox.setValue( false );
		
		if ( m_allowSelfRegOfExternalUserAccountCkbox != null )
			m_allowSelfRegOfExternalUserAccountCkbox.setValue( false );
		
		if ( m_allowSelfRegOfInternalUserAccountCkbox != null )
			m_allowSelfRegOfInternalUserAccountCkbox.setValue( false );
		
		if ( GwtClientHelper.isLicenseFilr() )
		{
			if ( m_disableDownloadCkbox != null )
				m_disableDownloadCkbox.setValue( false );
		}
		
		if ( m_disableWebAccessCkbox != null )
			m_disableWebAccessCkbox.setValue( false );
		
		// Issue an rpc request to get the user access information from the server
		getUserAccessInfoFromServer();
	}
	
	/**
	 * 
	 */
	private void init( UserAccessConfig config )
	{
		if ( m_allowGuestAccessCkbox != null )
			m_allowGuestAccessCkbox.setValue( config.getAllowGuestAccess() );
		
		if ( m_guestReadOnlyCkbox != null )
			m_guestReadOnlyCkbox.setValue( config.getGuestReadOnly() );
		
		if ( m_allowSelfRegOfExternalUserAccountCkbox != null )
			m_allowSelfRegOfExternalUserAccountCkbox.setValue( config.getAllowExternalUsersSelfReg() );
		
		if ( m_allowSelfRegOfInternalUserAccountCkbox != null )
			m_allowSelfRegOfInternalUserAccountCkbox.setValue( config.getAllowSelfReg() );
		
		if ( GwtClientHelper.isLicenseFilr() )
		{
			if ( m_disableDownloadCkbox != null )
				m_disableDownloadCkbox.setValue( !config.getAllowDownload() );
		}
		
		if ( m_disableWebAccessCkbox != null )
			m_disableWebAccessCkbox.setValue( !config.getAllowWebAccess() );
		
		danceDlg();
	}
	
	/**
	 * Loads the ConfigureUserAccessDlg split point and returns an instance
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
							final ConfigureUserAccessDlgClient cuaDlgClient )
	{
		GWT.runAsync( ConfigureUserAccessDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureUserAccessDlg() );
				if ( cuaDlgClient != null )
				{
					cuaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ConfigureUserAccessDlg cuaDlg;
				
				cuaDlg = new ConfigureUserAccessDlg(
												autoHide,
												modal,
												left,
												top,
												width,
												height );
				cuaDlgClient.onSuccess( cuaDlg );
			}
		});
	}
}
