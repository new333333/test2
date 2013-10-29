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
import org.kablink.teaming.gwt.client.GwtZoneMobileAppsConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetMobileAppsConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMobileAppsConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author jwootton
 *
 */
public class ConfigureMobileAppsDlg extends DlgBox
	implements KeyPressHandler, EditSuccessfulHandler
{
	private CheckBox m_enableMobileAppsAccessCB;
	private CheckBox m_allowPwdCacheCB;
	private CheckBox m_allowOfflineContentCB;
	private CheckBox m_allowPlayWithOtherAppsCB;
	private TextBox m_syncIntervalTextBox;
	
	/**
	 * Callback interface to interact with the "configure mobile applications" dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfigureMobileAppsDlgClient
	{
		void onSuccess( ConfigureMobileAppsDlg cmaDlg );
		void onUnavailable();
	}

	

	/**
	 * 
	 */
	private ConfigureMobileAppsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().configureMobileAppsDlgHeader(), this, null, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlowPanel ckboxPanel;
		FlowPanel tmpPanel;
		Label label;

		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		label = new Label( messages.configureMobileAppsDlgHeader2() );
		mainPanel.add( label );
		
		ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName( "marginleft1" );
		ckboxPanel.addStyleName( "margintop2" );
		mainPanel.add( ckboxPanel );
		
		// Add the controls for the "allow mobile applications to access Filr"
		{
			String productName;
			
			productName = "Vibe";
			if ( GwtClientHelper.isLicenseFilr() )
				productName = "Filr";
			
			m_enableMobileAppsAccessCB = new CheckBox( messages.configureMobileAppsDlgAllowAccess( productName ) );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_enableMobileAppsAccessCB );
			ckboxPanel.add( tmpPanel );
		}
		
		// Create the "Allow mobile applications to cache password"
		m_allowPwdCacheCB = new CheckBox( messages.configureMobileAppsDlgAllowCachePwd() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowPwdCacheCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the "Allow mobile applications to cache content offline"
		m_allowOfflineContentCB = new CheckBox( messages.configureMobileAppsDlgAllowCacheContent() );
		m_allowOfflineContentCB.addClickHandler( new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						danceDlg();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowOfflineContentCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the "Allow mobile applications to interact with other applications"
		m_allowPlayWithOtherAppsCB = new CheckBox( messages.configureMobileAppsDlgAllowPlayWithOtherApps() );
		tmpPanel = new FlowPanel();
		tmpPanel.addStyleName( "marginleft2" );
		tmpPanel.add( m_allowPlayWithOtherAppsCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the controls for sync interval
		{
			label = new Label( messages.configureMobileAppsDlgHeader3() );
			label.addStyleName( "margintop3" );
			mainPanel.add( label );
			
			HorizontalPanel hPanel;
			Label intervalLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.addStyleName( "marginleft1" );
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			
			intervalLabel = new Label( messages.configureMobileAppsSyncIntervalLabel() );
			hPanel.add( intervalLabel );
			
			m_syncIntervalTextBox = new TextBox();
			m_syncIntervalTextBox.addKeyPressHandler( this );
			m_syncIntervalTextBox.setVisibleLength( 3 );
			hPanel.add( m_syncIntervalTextBox );
			
			intervalLabel = new Label( messages.configureMobileAppsSyncMinutesLabel() );
			hPanel.add( intervalLabel );
			
			mainPanel.add( hPanel );
		}
		
		return mainPanel;
	}
	
	/**
	 * Enable/disable the "interact with other applications" checkbox
	 */
	private void danceDlg()
	{
		boolean enable;
		
		enable = m_allowOfflineContentCB.getValue();
		m_allowPlayWithOtherAppsCB.setEnabled( enable );
		if ( enable == false )
			m_allowPlayWithOtherAppsCB.setValue( false );
	}
	
	/**
	 * This method gets called when user user presses ok.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
		GwtZoneMobileAppsConfig mobileAppsConfig;
		SaveMobileAppsConfigurationCmd cmd;

		mobileAppsConfig = (GwtZoneMobileAppsConfig) obj;
		
		// Create the callback that will be used when we issue an ajax request to save the mobile apps configuration.
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
				
				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				if ( errMsg == null )
				{
					errMsg = GwtTeaming.getMessages().configureMobileAppsDlgOnSaveUnknownException( caught.toString() );
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
				// Close this dialog.
				hide();
			}
		};

		// Issue an ajax request to save the Mobile apps configuration to the db.  rpcSaveCallback will
		// be called when we get the response back.
		cmd = new SaveMobileAppsConfigurationCmd( mobileAppsConfig );
		GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
		
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
		GwtZoneMobileAppsConfig mobileAppsConfig;
		
		mobileAppsConfig = new GwtZoneMobileAppsConfig();

		// Get whether mobile apps can access Filr
		mobileAppsConfig.setMobileAppsEnabled( getMobileAppsEnabled() );
		
		// Get whether mobile apps can cache the user's password
		mobileAppsConfig.setAllowCachePwd( getAllowCachePwd() );
		
		// Get whether mobile apps can cache content
		mobileAppsConfig.setAllowCacheContent( getAllowOfflineContent() );
		
		// Get whether mobile apps can interact with other apps
		mobileAppsConfig.setAllowPlayWithOtherApps( getAllowPlayWithOtherApps() );
		
		// Get the sync interval from the dialog.
		mobileAppsConfig.setSyncInterval( getIntervalInt() );
		
		return mobileAppsConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_syncIntervalTextBox;
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
		helpData.setPageId( "mobile_site" );
		
		return helpData;
	}

	/**
	 * Return the interval entered by the user.
	 */
	private int getIntervalInt()
	{
		String intervalStr;
		int interval = 0;
		
		intervalStr = m_syncIntervalTextBox.getText();
		if ( intervalStr != null && intervalStr.length() > 0 )
			interval = Integer.parseInt( intervalStr );
		
		return interval;
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
	 * 
	 */
	public void init()
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
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
					GwtTeaming.getMessages().rpcFailure_GetMobileAppsConfiguration() );
			}
	
			/**
			 * We successfully retrieved the Mobile Apps configuration.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				final GwtZoneMobileAppsConfig mobileAppsConfiguration;
				
				mobileAppsConfiguration = (GwtZoneMobileAppsConfig) response.getResponseData();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute() 
					{
						init( mobileAppsConfiguration );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Issue an ajax request to get the Mobile Apps configuration.
		{
			GetMobileAppsConfigCmd cmd;
			
			// Issue an ajax request to get the Mobile Apps configuration from the db.
			cmd = new GetMobileAppsConfigCmd();
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	private void init( GwtZoneMobileAppsConfig mobileAppsConfiguration )
	{
		int interval;
		
		// Initialize whether mobile apps can access Filr
		m_enableMobileAppsAccessCB.setValue( mobileAppsConfiguration.getMobileAppsEnabled() );
			
		// Initialize the allow pwd cache checkbox
		m_allowPwdCacheCB.setValue( mobileAppsConfiguration.getAllowCachePwd() );

		// Initialize the offline content
		m_allowOfflineContentCB.setValue( mobileAppsConfiguration.getAllowCacheContent() );
		
		// Initialize the allow mobile apps to play with others
		m_allowPlayWithOtherAppsCB.setValue( mobileAppsConfiguration.getAllowPlayWithOtherApps() );
		
		// Initialize the interval textbox
		interval = mobileAppsConfiguration.getSyncInterval();
		m_syncIntervalTextBox.setText( String.valueOf( interval ) );
		
		hideErrorPanel();
		
		danceDlg();
	}
	
	
	/**
	 * This method gets called when the user types in the "interval" text box.
	 * We only allow the user to enter numbers.
	 */
	@Override
	public void onKeyPress( KeyPressEvent event )
	{
        int keyCode;

        // Get the key the user pressed
        keyCode = event.getNativeEvent().getKeyCode();
        
        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
        {
        	TextBox txtBox;
        	Object source;
        	
        	// Make sure we are dealing with a text box.
        	source = event.getSource();
        	if ( source instanceof TextBox )
        	{
        		// Suppress the current keyboard event.
        		txtBox = (TextBox) source;
        		txtBox.cancelKey();
        	}
        }
	}

	/**
	 * Loads the ConfigureMobileAppsDlg split point and returns an instance
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
							final ConfigureMobileAppsDlgClient cmaDlgClient )
	{
		GWT.runAsync( ConfigureMobileAppsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureMobileAppsDlg() );
				if ( cmaDlgClient != null )
				{
					cmaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ConfigureMobileAppsDlg cmaDlg;
				
				cmaDlg = new ConfigureMobileAppsDlg(
												autoHide,
												modal,
												left,
												top,
												width,
												height );
				cmaDlgClient.onSuccess( cmaDlg );
			}
		});
	}
}
