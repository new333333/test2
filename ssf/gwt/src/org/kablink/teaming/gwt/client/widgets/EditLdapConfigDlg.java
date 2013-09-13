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


import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapConfigCmd;
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
 * 
 * @author jwootton
 *
 */
public class EditLdapConfigDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private CheckBox m_syncUserProfilesCheckBox;
	private CheckBox m_registerUserProfilesAutomaticallyCheckBox;
	
	
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
		FlowPanel tmpPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		tmpPanel = new FlowPanel();
		m_registerUserProfilesAutomaticallyCheckBox = new CheckBox( messages.editLdapConfigDlg_RegisterUserProfilesAutomatically() );
		tmpPanel.add( m_registerUserProfilesAutomaticallyCheckBox );
		mainPanel.add( tmpPanel );

		tmpPanel = new FlowPanel();
		m_syncUserProfilesCheckBox = new CheckBox( messages.editLdapConfigDlg_SyncUserProfiles() );
		tmpPanel.add( m_syncUserProfilesCheckBox );
		mainPanel.add( tmpPanel );
		
		return mainPanel;
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
