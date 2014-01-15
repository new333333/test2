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
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetAdhocFolderSettingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveAdhocFolderSettingCmd;
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
public class ConfigureAdhocFoldersDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private CheckBox m_allowAdhocFoldersCkbox;
	private Long m_userId = null;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
	};
	

	/**
	 * Callback interface to interact with the "configure adhoc folders" dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfigureAdhocFoldersDlgClient
	{
		void onSuccess( ConfigureAdhocFoldersDlg cafDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private ConfigureAdhocFoldersDlg(
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
		createAllDlgContent( GwtTeaming.getMessages().configureAdhocFoldersDlg_Header(), this, null, null );
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

		// Add the "Enable adhoc folders" checkbox;
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.addStyleName( "marginbottom2" );
			m_allowAdhocFoldersCkbox = new CheckBox( messages.configureAdhocFoldersDlg_AllowAdhocFoldersLabel() );
			panel.add( m_allowAdhocFoldersCkbox );
			mainPanel.add( panel );
		}
		return mainPanel;
	}
	
	/**
	 * This gets called when the user presses ok.  Issue an rpc request to save the
	 * "allow adhoc folders" setting
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
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
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the user access configuration.
		return false;
	}
	
	/**
	 * 
	 */
	private boolean getAllowAdhocFolders()
	{
		if ( m_allowAdhocFoldersCkbox != null )
			return m_allowAdhocFoldersCkbox.getValue();
		
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		return new Boolean( getAllowAdhocFolders() );
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_allowAdhocFoldersCkbox;
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
		helpData.setPageId( "personalfolders" );
		
		return helpData;
	}

	/**
	 * Issue an rpc request to get the "allow adhoc folders" setting from the server.
	 * If m_userId is not null we will get the setting from the user's properties.  Otherwise,
	 * we will get the setting from the zone.
	 */
	private void getAdhocFoldersSettingFromServer()
	{
		GetAdhocFolderSettingCmd cmd;

		// Execute a GWT RPC command asking the server for the adhoc folder setting
		cmd = new GetAdhocFolderSettingCmd( m_userId );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_GetAdhocFolderSetting() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof BooleanRpcResponseData )
				{
					final BooleanRpcResponseData responseData;
					ScheduledCommand cmd;
					
					responseData = (BooleanRpcResponseData) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							init( responseData.getBooleanValue().booleanValue() );
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
	public void init( Long userId )
	{
		m_userId = userId;
		
		// Get the "adhoc folder" setting from the server.
		getAdhocFoldersSettingFromServer();
	}
	
	
	/**
	 * 
	 */
	private void init( boolean allow )
	{
		if ( m_allowAdhocFoldersCkbox != null )
			m_allowAdhocFoldersCkbox.setValue( allow );
	}
	
	
	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void createDlg(
		final Boolean autoHide,
		final Boolean modal,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final ConfigureAdhocFoldersDlgClient cafDlgClient )
	{
		GWT.runAsync( ConfigureAdhocFoldersDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureAdhocFoldersDlg() );
				if ( cafDlgClient != null )
				{
					cafDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ConfigureAdhocFoldersDlg cafDlg;
				
				cafDlg = new ConfigureAdhocFoldersDlg(
											autoHide,
											modal,
											left,
											top,
											width,
											height );
				
				if ( cafDlgClient != null )
					cafDlgClient.onSuccess( cafDlg );
			}
		} );
	}
	
	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final ConfigureAdhocFoldersDlg dlg,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final ConfigureAdhocFoldersDlgClient cafDlgClient )
	{
		GWT.runAsync( ConfigureAdhocFoldersDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureAdhocFoldersDlg() );
				if ( cafDlgClient != null )
				{
					cafDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				if ( width != null && height != null )
					dlg.setPixelSize( width, height );
				
				dlg.init( null );
				
				if ( left != null && top != null )
					dlg.setPopupPosition( left, top );
				
				dlg.show();
			}
		} );
	}
}
