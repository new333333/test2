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
import com.google.gwt.event.dom.client.KeyCodes;
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
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author jwootton
 *
 */
public class ConfigureFileSyncAppDlg extends DlgBox
	implements KeyPressHandler, EditSuccessfulHandler
{
	private CheckBox m_enableFileSyncAccessCB;
	private CheckBox m_enableDeployCB;
	private CheckBox m_allowPwdCacheCB;
	private TextBox m_syncIntervalTextBox;
	private TextBox m_autoUpdateUrlTextBox;
	private TextBox m_maxFileSizeTextBox;
	
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
		
		// Create the "Allow deployment of Desktop application" checkbox
		m_enableDeployCB = new CheckBox( messages.fileSyncAppEnableDeployLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_enableDeployCB );
		ckboxPanel.add( tmpPanel );
		
		// Create the controls for File Sync interval
		{
			HorizontalPanel hPanel;
			Label intervalLabel;
			
			hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
			hPanel.setSpacing( 4 );
			
			intervalLabel = new Label( messages.fileSyncAppIntervalLabel() );
			hPanel.add( intervalLabel );
			
			m_syncIntervalTextBox = new TextBox();
			m_syncIntervalTextBox.addKeyPressHandler( this );
			m_syncIntervalTextBox.setVisibleLength( 3 );
			hPanel.add( m_syncIntervalTextBox );
			
			intervalLabel = new Label( messages.fileSyncAppMinutesLabel() );
			hPanel.add( intervalLabel );

			mainPanel.add( hPanel );
		}
		
		// Create the controls for auto-update url.
		{
			FlexTable tmpTable;
			
			tmpTable = new FlexTable();
			tmpTable.setCellSpacing( 4 );
			
			label = new InlineLabel( messages.fileSyncAppAutoUpdateUrlLabel() );
			tmpTable.setWidget( 0, 0, label );
			
			// Create a textbox for the user to enter the auto-update url.
			m_autoUpdateUrlTextBox = new TextBox();
			m_autoUpdateUrlTextBox.setVisibleLength( 40 );
			tmpTable.setWidget( 0, 1, m_autoUpdateUrlTextBox );

			mainPanel.add( tmpTable );
		}
		
		// Create the controls for the max file size
		{
			FlexTable tmpTable;
			
			tmpTable = new FlexTable();
			tmpTable.setCellSpacing( 4 );
			
			label = new InlineLabel( messages.fileSyncAppMaxFileSizeLabel() );
			tmpTable.setWidget( 0, 0, label );
			
			m_maxFileSizeTextBox = new TextBox();
			m_maxFileSizeTextBox.addKeyPressHandler( this );
			m_maxFileSizeTextBox.setVisibleLength( 3 );
			tmpTable.setWidget( 0, 1, m_maxFileSizeTextBox );
			
			label = new InlineLabel( messages.fileSyncAppMBLabel() );
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
	 * Get the data from the controls in the dialog box and store the data in a GwtFileSyncAppConfiguration object.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtFileSyncAppConfiguration fileSyncAppConfig;
		String autoUpdateUrl;
		boolean deployEnabled;
		
		fileSyncAppConfig = new GwtFileSyncAppConfiguration();

		autoUpdateUrl = getAutoUpdateUrl();
		deployEnabled = getIsFileSyncAppDeployEnabled();

		// Get whether the File Sync App is enabled.
		fileSyncAppConfig.setIsFileSyncAppEnabled( getIsFileSyncAppEnabled() );
		
		// Get the sync interval from the dialog.
		fileSyncAppConfig.setSyncInterval( getIntervalInt() );
		
		// Get the auto-update url from the dialog.
		fileSyncAppConfig.setAutoUpdateUrl( autoUpdateUrl );
		
		// Get whether the file sync app can be deployed
		fileSyncAppConfig.setIsDeploymentEnabled( deployEnabled );
		
		// Get whether the file sync app can cache the user's password
		fileSyncAppConfig.setAllowCachePwd( getAllowCachePwd() );
		
		// Get the max file size the file sync app can sync
		fileSyncAppConfig.setMaxFileSize( getMaxFileSize() );

		// If the "allow deployment..." checkbox is checked the user must have an auto-update url
		if ( deployEnabled && (autoUpdateUrl == null || autoUpdateUrl.length() == 0) )
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
        
        if ( (!Character.isDigit(event.getCharCode())) && (keyCode != KeyCodes.KEY_TAB) && (keyCode != KeyCodes.KEY_BACKSPACE)
            && (keyCode != KeyCodes.KEY_DELETE) && (keyCode != KeyCodes.KEY_ENTER) && (keyCode != KeyCodes.KEY_HOME)
            && (keyCode != KeyCodes.KEY_END) && (keyCode != KeyCodes.KEY_LEFT) && (keyCode != KeyCodes.KEY_UP)
            && (keyCode != KeyCodes.KEY_RIGHT) && (keyCode != KeyCodes.KEY_DOWN))
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
	 * Loads the ConfigureFileSyncAppDlg split point and returns an instance
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
				cfsaDlgClient.onSuccess( cfsaDlg );
			}
		});
	}
}
