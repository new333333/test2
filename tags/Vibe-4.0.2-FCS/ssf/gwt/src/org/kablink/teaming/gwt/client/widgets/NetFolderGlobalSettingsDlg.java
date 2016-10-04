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
import org.kablink.teaming.gwt.client.GwtNetFolderGlobalSettings;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderGlobalSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveNetFolderGlobalSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author jwootton
 *
 */
public class NetFolderGlobalSettingsDlg extends DlgBox
	implements KeyPressHandler, EditSuccessfulHandler
{
	private CheckBox m_enableJitsCB;
	private TextBox m_maxWaitTimeTextBox;
	private CheckBox m_useDirectoryRightsCB;
	private TextBox m_cachedRightsRefreshIntervalTB;
	
	/**
	 * Callback interface to interact with the "Net Folder Global Settings" dialog
	 * asynchronously after it loads. 
	 */
	public interface NetFolderGlobalSettingsDlgClient
	{
		void onSuccess( NetFolderGlobalSettingsDlg cmaDlg );
		void onUnavailable();
	}

	

	/**
	 * 
	 */
	private NetFolderGlobalSettingsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().netFolderGlobalSettingsDlg_Header(), this, null, null ); 
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
		
		// Add the "enable jits" checkbox
		m_enableJitsCB = new CheckBox( messages.netFolderGlobalSettingsDlg_EnableJits() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_enableJitsCB );
		tmpPanel.addStyleName( "marginbottom1" );
		mainPanel.add( tmpPanel );
		
		// Create the controls for the max wait time
		{
			HorizontalPanel hPanel;
			Label intervalLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 0 );
			
			intervalLabel = new Label( messages.netFolderGlobalSettingsDlg_MaxWaitLabel() );
			intervalLabel.addStyleName( "marginleft3" );
			intervalLabel.addStyleName( "marginright5px" );
			hPanel.add( intervalLabel );
			
			m_maxWaitTimeTextBox = new TextBox();
			m_maxWaitTimeTextBox.addKeyPressHandler( this );
			m_maxWaitTimeTextBox.setVisibleLength( 3 );
			hPanel.add( m_maxWaitTimeTextBox );
			
			intervalLabel = new Label( messages.netFolderGlobalSettingsDlg_SecondsLabel() );
			intervalLabel.addStyleName( "marginleft2px" );
			intervalLabel.addStyleName( "gray3" );
			hPanel.add( intervalLabel );
			
			mainPanel.add( hPanel );
		}
		
		// Add some space
		tmpPanel = new FlowPanel();
		tmpPanel.getElement().getStyle().setMarginTop( 8, Unit.PX );
		mainPanel.add( tmpPanel );
		
		// Add the controls for "Use directory rights"
		{
			Label hint;
			
			tmpPanel = new FlowPanel();
			m_useDirectoryRightsCB = new CheckBox( messages.modifyNetFolderServerDlg_UseDirectoryRightsCB() );
			tmpPanel.add( m_useDirectoryRightsCB );
			mainPanel.add( tmpPanel );
			
			// Add a hint that tells the user that changing the value of "use directory rights" will
			// take affect after the Filr server is restarted.
			hint = new Label( messages.netFolderGlobalSettingsDlg_UseDirRightsHint() );
			hint.addStyleName( "netFolderSettingsDlg_useDirRightsHint" );
			mainPanel.add( hint );
		}
		
		// Add the controls for "Refresh cached rights information every: xxx minutes"
		{
			InlineLabel label;
			
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "netFolderSettingsDlg_refreshCachedRightsInterval" );
			
			label = new InlineLabel( messages.modifyNetFolderServerDlg_RefreshRightsLabel() );
			label.getElement().getStyle().setMarginRight( 6, Unit.PX );
			tmpPanel.add( label );
			
			m_cachedRightsRefreshIntervalTB = new TextBox();
			m_cachedRightsRefreshIntervalTB.addKeyPressHandler( new KeyPressHandler()
			{
				@Override
				public void onKeyPress( KeyPressEvent event )
				{
			        int keyCode;

			        // Get the key the user pressed
			        keyCode = event.getNativeEvent().getKeyCode();
			        
			        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
			        {
		        		// Suppress the current keyboard event.
		        		m_cachedRightsRefreshIntervalTB.cancelKey();
			        }
				}
			} );

			m_cachedRightsRefreshIntervalTB.setVisibleLength( 3 );
			tmpPanel.add( m_cachedRightsRefreshIntervalTB );
			
			label = new InlineLabel( messages.modifyNetFolderServerDlg_Minutes() );
			label.getElement().getStyle().setMarginLeft( 6, Unit.PX );
			tmpPanel.add( label );
			
			mainPanel.add( tmpPanel );
		}

		return mainPanel;
	}
	
	/**
	 * This method gets called when user user presses ok.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		AsyncCallback<VibeRpcResponse> rpcSaveCallback = null;
		GwtNetFolderGlobalSettings globalSettings;
		SaveNetFolderGlobalSettingsCmd cmd;

		clearErrorPanel();
		hideErrorPanel();

		// Disable the Ok button.
		setOkEnabled( false );

		globalSettings = (GwtNetFolderGlobalSettings) obj;
		
		// Create the callback that will be used when we issue an ajax request to save the global settings.
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
				errorPanel.clear();
				
				if ( errMsg == null )
				{
					errMsg = GwtTeaming.getMessages().netFolderGlobalSettingsDlg_OnSaveUnknownException( caught.toString() );
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
				hideStatusMsg();
				setOkEnabled( true );
				hide();
			}
		};

		showStatusMsg( GwtTeaming.getMessages().netFolderGlobalSettingsDlg_SavingConfig() );

		// Issue an ajax request to save the net folder global settings to the db.  rpcSaveCallback will
		// be called when we get the response back.
		cmd = new SaveNetFolderGlobalSettingsCmd( globalSettings );
		GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the configuration.
		return false;
	}

	/**
	 * Returns the "cached rights refresh interval" entered by the user.
	 */
	private Integer getCachedRightsRefreshInterval()
	{
		String intervalStr;
		Integer interval = null;
		
		intervalStr = m_cachedRightsRefreshIntervalTB.getValue();
		if ( intervalStr != null && intervalStr.length() > 0 )
			interval = Integer.valueOf( intervalStr );
		
		return interval;
	}

	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtNetFolderGlobalSettings object.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtNetFolderGlobalSettings globalSettings;
		
		globalSettings = new GwtNetFolderGlobalSettings();

		// Get whether jits is enabled
		globalSettings.setJitsEnabled( getJitsEnabled() );
		
		// Get the max wait time from the dialog.
		globalSettings.setMaxWaitTime( getMaxWaitTimeInt() * 1000 );
		
		// Get the cached rights refresh interval
		globalSettings.setCachedRightsRefreshInterval( getCachedRightsRefreshInterval() );
		
		// Get the setting for "use directory rights"
		globalSettings.setUseDirectoryRights( getUseDirectoryRights() );
	
		return globalSettings;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_maxWaitTimeTextBox;
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
		helpData.setPageId( "netfolders_global" );
		
		return helpData;
	}
	
	/**
	 * Return whether jits is enabled.
	 */
	private boolean getJitsEnabled()
	{
		if ( m_enableJitsCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * Return the max wait time entered by the user.
	 */
	private long getMaxWaitTimeInt()
	{
		String intervalStr;
		long interval = 0;
		
		intervalStr = m_maxWaitTimeTextBox.getText();
		if ( intervalStr != null && intervalStr.length() > 0 )
			interval = Long.parseLong( intervalStr );
		
		return interval;
	}
	
	/**
	 * Return the value of the "Use directory rights in addition to file system rights"
	 */
	public Boolean getUseDirectoryRights()
	{
		if ( m_useDirectoryRightsCB.isVisible() )
			return m_useDirectoryRightsCB.getValue();
		
		return null;
	}
	
	/**
	 * 
	 */
	public void init()
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );

		m_useDirectoryRightsCB.setValue( false );
		m_cachedRightsRefreshIntervalTB.setValue( "5" );

		// Create a callback that will be called when we get the net folder global settings.
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
					GwtTeaming.getMessages().rpcFailure_GetNetFolderGlobalSettings() );
			}
	
			/**
			 * We successfully retrieved the net folder global settings.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtNetFolderGlobalSettings )
				{
					Scheduler.ScheduledCommand cmd;
					final GwtNetFolderGlobalSettings globalSettings;
					
					globalSettings = (GwtNetFolderGlobalSettings) response.getResponseData();
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							init( globalSettings );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Issue an ajax request to get the net folder global settings.
		{
			GetNetFolderGlobalSettingsCmd cmd;
			
			// Issue an ajax request to get the net folder global settings from the db.
			cmd = new GetNetFolderGlobalSettingsCmd();
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	private void init( GwtNetFolderGlobalSettings globalSettings )
	{
		long maxWaitTime;
		
		// Initialize whether jits is enabled.
		m_enableJitsCB.setValue( globalSettings.getJitsEnabled() );
			
		// Initialize the max wait time textbox
		maxWaitTime = globalSettings.getMaxWaitTime();
		m_maxWaitTimeTextBox.setText( String.valueOf( maxWaitTime ) );
		
		// Initialize the "use directory rights in addition to file system rights" checkbox.
		{
			Boolean value;
			
			value = globalSettings.getUseDirectoryRights();
			if ( value != null )
				m_useDirectoryRightsCB.setValue( value );
		}
		
		// Initialize the "cached rights refresh interval"
		{
			Integer value;
			
			value = globalSettings.getCachedRightsRefreshInterval();
			if ( value != null )
				m_cachedRightsRefreshIntervalTB.setValue( value.toString() );
		}

		hideErrorPanel();
	}
	
	
	/**
	 * This method gets called when the user types in the "max wait time" text box.
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
	 * Loads the NetFolderGlobalSettingsDlg split point and returns an instance
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
							final NetFolderGlobalSettingsDlgClient nfgsDlgClient )
	{
		GWT.runAsync( NetFolderGlobalSettingsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_NetFolderGlobalSettingsDlg() );
				if ( nfgsDlgClient != null )
				{
					nfgsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				NetFolderGlobalSettingsDlg jzcDlg;
				
				jzcDlg = new NetFolderGlobalSettingsDlg(
											autoHide,
											modal,
											left,
											top,
											width,
											height );
				nfgsDlgClient.onSuccess( jzcDlg );
			}
		});
	}
}
