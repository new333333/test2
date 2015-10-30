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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFileSyncAppConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ?
 * 
 * @author jwootton@novell.com
 */
public class ConfigureFileSyncAppDlg extends DlgBox
	implements KeyPressHandler, EditSuccessfulHandler
{
	private CheckBox	m_enableFileSyncAccessCB;
	private CheckBox	m_enableDeployCB;
	private CheckBox	m_allowPwdCacheCB;
	private FlexTable	m_autoUpdateChoiceTable;
	private FlexTable	m_autoUpdateUrlOnlyTable;
	private RadioButton m_useLocalApps;
	private RadioButton m_useRemoteApps;
	private TextBox		m_syncIntervalTextBox;
	private TextBox		m_autoUpdateUrlTextBox_Choice;
	private TextBox		m_autoUpdateUrlTextBox_UrlOnly;
	private TextBox		m_autoUpdateUrlTextBox;
	private TextBox		m_maxFileSizeTextBox;
	
	/**
	 * Callback interface to interact with the "configure file sync app" dialog
	 * asynchronously after it loads. 
	 */
	public interface ConfigureFileSyncAppDlgClient
	{
		void onSuccess( ConfigureFileSyncAppDlg cfsaDlg );
		void onUnavailable();
	}

	

	/**
	 * 
	 */
	private ConfigureFileSyncAppDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().fileSyncAppDlgHeader(), this, null, null ); 
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
		ckboxPanel.addStyleName( "marginleft1 margintop2" );
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
		
		// Create the "Allow deployment of Desktop application" checkbox
		m_enableDeployCB = new CheckBox( messages.fileSyncAppEnableDeployLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_enableDeployCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the controls for auto-update url.
		{
			m_autoUpdateChoiceTable = new FlexTable();
			m_autoUpdateChoiceTable.addStyleName( "marginleft1" );
			m_autoUpdateChoiceTable.setCellSpacing( 0 );
			m_autoUpdateChoiceTable.setCellPadding( 0 );

			{
				m_useLocalApps = new RadioButton( "fileSyncAppLocation" );
				m_useLocalApps.addStyleName( "filrSyncAppDlg_Radio" );
				m_useLocalApps.setValue( true );
				m_autoUpdateChoiceTable.setWidget( 0, 0, m_useLocalApps );
				label = new InlineLabel( messages.fileSyncAppAutoUpdateUrlLabel_UseLocal() );
				label.addStyleName("gwtUI_nowrap");
				m_autoUpdateChoiceTable.setWidget( 0, 1, label );

				m_useRemoteApps = new RadioButton( "fileSyncAppLocation" );
				m_useRemoteApps.addStyleName( "filrSyncAppDlg_Radio" );
				m_useRemoteApps.setValue( false );
				m_autoUpdateChoiceTable.setWidget( 1, 0, m_useRemoteApps );
				label = new InlineLabel( messages.fileSyncAppAutoUpdateUrlLabel_UseRemote() );
				label.addStyleName("gwtUI_nowrap");
				m_autoUpdateChoiceTable.setWidget( 1, 1, label );
				
				// Create a textbox for the user to enter the auto-update url.
				m_autoUpdateUrlTextBox_Choice = new TextBox();
				m_autoUpdateUrlTextBox_Choice.setVisibleLength( 40 );
				m_autoUpdateChoiceTable.setWidget( 2, 1, m_autoUpdateUrlTextBox_Choice );
				
				mainPanel.add( m_autoUpdateChoiceTable );
			}
			
			{
				m_autoUpdateUrlOnlyTable = new FlexTable();
				m_autoUpdateUrlOnlyTable.addStyleName( "marginleft1" );
				m_autoUpdateUrlOnlyTable.setCellSpacing( 4 );
				
				label = new InlineLabel( messages.fileSyncAppAutoUpdateUrlLabel() );
				label.addStyleName("gwtUI_nowrap");
				m_autoUpdateUrlOnlyTable.setWidget( 0, 0, label );
				
				// Create a textbox for the user to enter the auto-update url.
				m_autoUpdateUrlTextBox_UrlOnly = new TextBox();
				m_autoUpdateUrlTextBox_UrlOnly.setVisibleLength( 40 );
				m_autoUpdateUrlOnlyTable.setWidget( 0, 1, m_autoUpdateUrlTextBox_UrlOnly );
				
				mainPanel.add( m_autoUpdateUrlOnlyTable );
			}

		}
		
		// Create the controls for File Sync interval
		{
			label = new Label( messages.fileSyncAppHeader3() );
			label.addStyleName( "margintop3" );
			mainPanel.add( label );

			HorizontalPanel hPanel;
			Label intervalLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.addStyleName( "marginleft1" );
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			
			intervalLabel = new Label( messages.fileSyncAppIntervalLabel() );
			hPanel.add( intervalLabel );
			
			m_syncIntervalTextBox = new TextBox();
			m_syncIntervalTextBox.addKeyPressHandler( this );
			m_syncIntervalTextBox.setVisibleLength( 3 );
			hPanel.add( m_syncIntervalTextBox );
			
			intervalLabel = new Label( messages.fileSyncAppMinutesLabel() );
			intervalLabel.addStyleName( "gray3" );
			hPanel.add( intervalLabel );

			mainPanel.add( hPanel );
		}
		
		// Create the controls for the max file size
		{
			FlexTable tmpTable;
			
			tmpTable = new FlexTable();
			tmpTable.addStyleName( "marginleft1" );
			tmpTable.setCellSpacing( 4 );
			
			label = new InlineLabel( messages.fileSyncAppMaxFileSizeLabel() );
			tmpTable.setWidget( 0, 0, label );
			
			m_maxFileSizeTextBox = new TextBox();
			m_maxFileSizeTextBox.addKeyPressHandler( this );
			m_maxFileSizeTextBox.setVisibleLength( 3 );
			tmpTable.setWidget( 0, 1, m_maxFileSizeTextBox );
			
			label = new InlineLabel( messages.fileSyncAppMBLabel() );
			label.addStyleName( "gray3" );
			tmpTable.setWidget( 0, 2, label );

			mainPanel.add( tmpTable );
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
		GwtFileSyncAppConfiguration fileSyncAppConfig;
		SaveFileSyncAppConfigurationCmd cmd;

		fileSyncAppConfig = (GwtFileSyncAppConfiguration) obj;
		
		// Create the callback that will be used when we issue an ajax request to save the file sync app configuration.
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
				
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException ex;
					
					ex = (GwtTeamingException) caught;
					if ( ex.getExceptionType() == ExceptionType.INVALID_AUTO_UPDATE_URL )
					{
						errMsg = GwtTeaming.getMessages().fileSyncApp_InvalidAutoUpdateUrlText();
					}
				}
				
				if ( errMsg == null )
				{
					errMsg = GwtTeaming.getMessages().fileSyncApp_OnSaveUnknownException( caught.toString() );
				}
				
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
				m_autoUpdateUrlTextBox.setFocus( true );
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

		// Issue an ajax request to save the File Sync App configuration to the db.  rpcSaveCallback will
		// be called when we get the response back.
		cmd = new SaveFileSyncAppConfigurationCmd( fileSyncAppConfig );
		GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
		
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
	 * Return the string entered by the user for the auto-update url
	 */
	private String getAutoUpdateUrl()
	{
		return m_autoUpdateUrlTextBox.getText();
	}
	
	/**
	 * Returns whether to use the desktop applications that are local
	 * to the system.
	 */
	private boolean getUseLocalApps()
	{
		return ( m_autoUpdateChoiceTable.isVisible() ? m_useLocalApps.getValue() : false );
	}
	
	/**
	 * Returns whether to use desktop applications from a remote
	 * location.
	 */
	private boolean getUseRemoteApps()
	{
		return ( m_autoUpdateChoiceTable.isVisible() ? m_useRemoteApps.getValue() : true );
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtFileSyncAppConfiguration object.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtFileSyncAppConfiguration fileSyncAppConfig;
		String autoUpdateUrl;
		boolean deployEnabled;
		boolean useLocalApps;
		boolean useRemoteApps;
		
		fileSyncAppConfig = new GwtFileSyncAppConfiguration();

		useLocalApps  = getUseLocalApps();
		useRemoteApps = getUseRemoteApps();
		autoUpdateUrl = getAutoUpdateUrl();
		deployEnabled = getIsFileSyncAppDeployEnabled();

		// Get whether the File Sync App is enabled.
		fileSyncAppConfig.setIsFileSyncAppEnabled( getIsFileSyncAppEnabled() );
		
		// Get the sync interval from the dialog.
		fileSyncAppConfig.setSyncInterval( getIntervalInt() );
		
		// Get the auto-update url from the dialog.
		fileSyncAppConfig.setAutoUpdateUrl( autoUpdateUrl );
		
		// Get the location of the desktop applications from the dialog.
		fileSyncAppConfig.setUseLocalApps(  useLocalApps  );
		fileSyncAppConfig.setUseRemoteApps( useRemoteApps );
		
		// Get whether the file sync app can be deployed
		fileSyncAppConfig.setIsDeploymentEnabled( deployEnabled );
		
		// Get whether the file sync app can cache the user's password
		fileSyncAppConfig.setAllowCachePwd( getAllowCachePwd() );
		
		// Get the max file size the file sync app can sync
		fileSyncAppConfig.setMaxFileSize( getMaxFileSize() );

		// If the "allow deployment..." checkbox is checked the user must have an auto-update url
		if ( deployEnabled && getUseRemoteApps() && ( ! ( GwtClientHelper.hasString( autoUpdateUrl ) ) ) )
		{
			Window.alert( GwtTeaming.getMessages().fileSyncAppAutoUpdateUrlRequiredPrompt() );
			m_autoUpdateUrlTextBox.setFocus( true );
			return null;
		}
		
		return fileSyncAppConfig;
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
		helpData.setPageId( "desktopapp" );
		
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
	 * Return whether the File Sync App is enabled.
	 */
	private boolean getIsFileSyncAppEnabled()
	{
		if ( m_enableFileSyncAccessCB.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * Return whether deployment of the file sync app is enabled.
	 */
	private boolean getIsFileSyncAppDeployEnabled()
	{
		if ( m_enableDeployCB.getValue() == Boolean.TRUE )
			return true;
		
		return false;
	}
	
	/**
	 * Get the max file size entered by the user
	 */
	private int getMaxFileSize()
	{
		String maxStr;
		int max = -1;
		
		maxStr = m_maxFileSizeTextBox.getValue();
		if ( maxStr != null && maxStr.length() > 0 )
			max = Integer.parseInt( maxStr );
		
		return max;
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	public void init( GwtFileSyncAppConfiguration fileSyncAppConfiguration )
	{
		int interval;
		int size;
		String value;
		
		// If local desktop applications are available for download...
		if ( fileSyncAppConfiguration.getLocalAppsExist() )
		{
			// ...we give the user the choice to use them...
			m_autoUpdateChoiceTable.setVisible(  true  );
			m_autoUpdateUrlOnlyTable.setVisible( false );
			m_autoUpdateUrlTextBox = m_autoUpdateUrlTextBox_Choice;
			m_useLocalApps.setValue(  fileSyncAppConfiguration.getUseLocalApps()  );
			m_useRemoteApps.setValue( fileSyncAppConfiguration.getUseRemoteApps() );
		}
		
		else
		{
			// ...otherwise, we only allow a URL to be specified.
			m_autoUpdateChoiceTable.setVisible(  false );
			m_autoUpdateUrlOnlyTable.setVisible( true  );
			m_autoUpdateUrlTextBox = m_autoUpdateUrlTextBox_UrlOnly;
			m_useLocalApps.setValue(  false );
			m_useRemoteApps.setValue( true  );
		}
		
		// Initialize the on/off radio buttons.
		m_enableFileSyncAccessCB.setValue( fileSyncAppConfiguration.getIsFileSyncAppEnabled() );
			
		// Initialize the deployment enabled checkbox
		m_enableDeployCB.setValue( fileSyncAppConfiguration.getIsDeploymentEnabled() );

		// Initialize the allow pwd  cache checkbox
		m_allowPwdCacheCB.setValue( fileSyncAppConfiguration.getAllowCachePwd() );
		
		// Initialize the interval textbox
		interval = fileSyncAppConfiguration.getSyncInterval();
		m_syncIntervalTextBox.setText( String.valueOf( interval ) );
		
		// Initialize the auto-update url.
		m_autoUpdateUrlTextBox.setText( fileSyncAppConfiguration.getAutoUpdateUrl() );
		
		// Initialize the max file size
		size = fileSyncAppConfiguration.getMaxFileSize();
		if ( size < 0 )
			value = "";
		else
			value = String.valueOf( size );
		m_maxFileSizeTextBox.setText( value );
		
		hideErrorPanel();
	}
	
	
	/**
	 * This method gets called when the user types in the "number of entries to show" text box.
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
		final ConfigureFileSyncAppDlgClient cfsaDlgClient )
	{
		GWT.runAsync( ConfigureFileSyncAppDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureFileSyncAppDlg() );
				if ( cfsaDlgClient != null )
				{
					cfsaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ConfigureFileSyncAppDlg cfsaDlg;
				
				cfsaDlg = new ConfigureFileSyncAppDlg(
											autoHide,
											modal,
											left,
											top,
											width,
											height );
				
				if ( cfsaDlgClient != null )
					cfsaDlgClient.onSuccess( cfsaDlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final ConfigureFileSyncAppDlg dlg,
		final GwtFileSyncAppConfiguration config,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final ConfigureFileSyncAppDlgClient cfsaDlgClient )
	{
		GWT.runAsync( ConfigureFileSyncAppDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ConfigureFileSyncAppDlg() );
				if ( cfsaDlgClient != null )
				{
					cfsaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				if ( width != null && height != null )
					dlg.setPixelSize( width, height );
				
				dlg.init( config );
				
				if ( left != null && top != null )
					dlg.setPopupPosition( left, top );

				dlg.show();
			}
		} );
	}
}
