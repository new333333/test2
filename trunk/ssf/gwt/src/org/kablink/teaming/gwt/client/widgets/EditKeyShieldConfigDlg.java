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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtKeyShieldConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetKeyShieldConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveKeyShieldConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveKeyShieldConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * 
 * @author jwootton
 *
 */
public class EditKeyShieldConfigDlg extends DlgBox
	implements
		EditSuccessfulHandler,
		EditCanceledHandler,
		KeyPressHandler
{
	private GwtKeyShieldConfig m_config;
	
	private CheckBox m_enableKeyShieldCheckbox;
	private TextBox m_serverUrlTextBox;
	private TextBox m_timeoutTextBox;
	private TextBox m_apiAuthKeyTextBox;

	private List<HandlerRegistration> m_registeredEventHandlers;

	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
	};

	

	/**
	 * Callback interface to interact with the "edit KeyShield config" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditKeyShieldConfigDlgClient
	{
		void onSuccess( EditKeyShieldConfigDlg ekcDlg );
		void onUnavailable();
	}
	
	/**
	 * 
	 */
	private EditKeyShieldConfigDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().editKeyShieldConfigDlg_Header(), this, this, null );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		Label label;
		FlowPanel tmpPanel;
		int row = 0;
		FlexTable table;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_enableKeyShieldCheckbox = new CheckBox( messages.editKeyShieldConfigDlg_EnableKeyShieldLabel() );
		mainPanel.add( m_enableKeyShieldCheckbox );
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		mainPanel.add( table );
		
		// Add a little space
		{
			tmpPanel = new FlowPanel();
			tmpPanel.getElement().getStyle().setMarginTop( 15, Unit.PX );
			table.setWidget( row, 0, tmpPanel );
			++row;
		}
		
		// Add the server url controls
		{
			tmpPanel = new FlowPanel();
			label = new Label( messages.editKeyShieldConfigDlg_ServerUrlLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );
		
			m_serverUrlTextBox = new TextBox();
			m_serverUrlTextBox.setVisibleLength( 40 );
			table.setWidget( row, 1, m_serverUrlTextBox );
			++row;
		}
		
		// Add the HTTP connection timeout controls
		{
			FlexTable tmpTable;
			
			tmpPanel = new FlowPanel();
			label = new Label( messages.editKeyShieldConfigDlg_HttpConnectionTimeoutLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );

			tmpTable = new FlexTable();
			
			m_timeoutTextBox = new TextBox();
			m_timeoutTextBox.setVisibleLength( 6 );
			m_timeoutTextBox.addKeyPressHandler( this );
			tmpTable.setWidget( 0, 0, m_timeoutTextBox );
			
			tmpPanel = new FlowPanel();
			label = new Label( messages.editKeyShieldConfigDlg_MilliSecondsLabel() );
			tmpPanel.add( label );
			tmpTable.setHTML( 0, 1, tmpPanel.getElement().getInnerHTML()  );
			
			table.setWidget( row, 1, tmpTable );
			++row;
		}
		
		// Add the API authorization key
		{
			tmpPanel = new FlowPanel();
			label = new Label( messages.editKeyShieldConfigDlg_ApiAuthKeyLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );
			
			m_apiAuthKeyTextBox = new TextBox();
			m_apiAuthKeyTextBox.setVisibleLength( 30 );
			table.setWidget( row, 1, m_apiAuthKeyTextBox );
			++row;
		}
		
		return mainPanel;
	}

	/**
	 * This method gets called when user user presses the Cancel push
	 * button.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() interface
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean editCanceled()
	{
		boolean isDirty;
		
		isDirty = isDirty();
		if ( isDirty )
		{
			if ( Window.confirm( GwtTeaming.getMessages().confirmChangesWillBeLost() ) )
				return true;
			
			return false;
		}
		
		return true;
	}

	/**
	 * This gets called when the user presses ok.  Issue an rpc request to save the
	 * "allow adhoc folders" setting
	 */
	@Override
	public boolean editSuccessful( final Object obj )
	{
		Scheduler.ScheduledCommand cmd;
		
		if ( (obj instanceof GwtKeyShieldConfig) == false )
			return false;

		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Issue an rpc request to save the KeyShield configuration
				saveKeyShieldConfiguration( (GwtKeyShieldConfig) obj );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );

		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the user access configuration.
		return false;
	}

	/**
	 * 
	 */
	private String getApiAuthKey()
	{
		String value;
		
		value = m_apiAuthKeyTextBox.getValue();
		if ( value == null )
			value = "";
		
		return value;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtKeyShieldConfig config;
		
		config = GwtKeyShieldConfig.getGwtKeyShieldConfig();
		
		config.setApiAuthKey( getApiAuthKey() );
		config.setHttpConnectionTimeout( getHttpConnectionTimeout() );
		config.setIsEnabled( getIsKeyShieldEnabled() );
		config.setServerUrl( getServerUrl() );
		
		return config;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_enableKeyShieldCheckbox;
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
		helpData.setPageId( "keyshield" );
		
		return helpData;
	}
	
	/**
	 * 
	 */
	private int getHttpConnectionTimeout()
	{
		String value;
		
		value = m_timeoutTextBox.getValue();
		if ( value == null || value.length() == 0 )
			return 250;	// Default is 250 milliseconds
		
		return Integer.parseInt( value );
	}
	
	/**
	 * 
	 */
	private boolean getIsKeyShieldEnabled()
	{
		return m_enableKeyShieldCheckbox.getValue();
	}

	/**
	 * Issue an rpc request to get the KeyShield configuration data from the server.
	 */
	private void getKeyShieldConfigurationFromServer()
	{
		GetKeyShieldConfigCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		showStatusMsg( GwtTeaming.getMessages().editKeyShieldConfigDlg_ReadingConfig() );
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_GetKeyShieldConfig() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtKeyShieldConfig )
				{
					final GwtKeyShieldConfig config;
					ScheduledCommand cmd;
					
					config = (GwtKeyShieldConfig) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							hideStatusMsg();
							init( config );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Execute a GWT RPC command to get the KeyShield configuration
		cmd = new GetKeyShieldConfigCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * 
	 */
	private String getServerUrl()
	{
		String value;
		
		value = m_serverUrlTextBox.getValue();
		if ( value == null )
			value = "";
		
		return value;
	}
	
	/**
	 * 
	 */
	private void init()
	{
		getKeyShieldConfigurationFromServer();
	}
	
	/**
	 * 
	 */
	private void init( GwtKeyShieldConfig config )
	{
		m_enableKeyShieldCheckbox.setValue( false );
		m_serverUrlTextBox.setValue( "" );
		m_timeoutTextBox.setValue( "" );
		m_apiAuthKeyTextBox.setValue( "" );

		if ( config == null )
			return;
		
		m_config = config;
		
		m_enableKeyShieldCheckbox.setValue( m_config.isEnabled() );
		m_serverUrlTextBox.setValue( m_config.getServerUrl() );
		m_timeoutTextBox.setValue( String.valueOf( m_config.getHttpConnectionTimeout() ) );
		m_apiAuthKeyTextBox.setValue( m_config.getApiAuthKey() );
	}
	
	/**
	 * Return true if anything in the KeyShield configuration has changed.
	 */
	private boolean isDirty()
	{
		String value;
		
		if ( m_config == null )
			return true;
		
		// Has anything in the configuration changed?
		if ( getIsKeyShieldEnabled() != m_config.isEnabled() )
			return true;
		
		value = getServerUrl();
		if ( value != null && value.equalsIgnoreCase( m_config.getServerUrl() ) == false )
			return true;
		
		if ( getHttpConnectionTimeout() != m_config.getHttpConnectionTimeout() )
			return true;
		
		value = getApiAuthKey();
		if ( value != null && value.equalsIgnoreCase( m_config.getApiAuthKey() ) == false )
			return true;
		
		// If we get here, nothing has changed.
		return false;
	}
	
	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		
		registerEvents();
	}
	
	/**
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		
		unregisterEvents();
	}
	
	/**
	 * This method gets called when the user types in the http connection timeout text box.
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

	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
										GwtTeaming.getEventBus(),
										m_registeredEvents,
										this,
										m_registeredEventHandlers );
		}
	}

	/**
	 * Issue an rpc request to save the KeyShield configuration.
	 */
	private void saveKeyShieldConfiguration( GwtKeyShieldConfig config )
	{
		AsyncCallback<VibeRpcResponse> callback;
		SaveKeyShieldConfigCmd cmd;

		showStatusMsg( GwtTeaming.getMessages().editKeyShieldConfigDlg_SavingConfig() );

		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_SaveKeyShieldConfig() );
				
				hideStatusMsg();
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				hideStatusMsg();
				
				if ( response.getResponseData() != null &&
					 response.getResponseData() instanceof SaveKeyShieldConfigRpcResponseData )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_config = (GwtKeyShieldConfig) getDataFromDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		}; 

		// Execute a GWT RPC command to save the KeyShield configuration
		cmd = new SaveKeyShieldConfigCmd();
		cmd.setConfig( config );
		
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void createDlg(
		final boolean autoHide,
		final boolean modal,
		final int left,
		final int top,
		final int width,
		final int height,
		final EditKeyShieldConfigDlgClient ekcDlgClient )
	{
		GWT.runAsync( EditKeyShieldConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditKeyShieldConfigDlg() );
				if ( ekcDlgClient != null )
				{
					ekcDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditKeyShieldConfigDlg ekcDlg;
				
				ekcDlg = new EditKeyShieldConfigDlg(
											autoHide,
											modal,
											left,
											top,
											width,
											height );
				
				if ( ekcDlgClient != null )
					ekcDlgClient.onSuccess( ekcDlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final EditKeyShieldConfigDlg dlg,
		final int width,
		final int height,
		final int left,
		final int top,
		final EditKeyShieldConfigDlgClient ekcDlgClient )
	{
		GWT.runAsync( EditKeyShieldConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditKeyShieldConfigDlg() );
				if ( ekcDlgClient != null )
				{
					ekcDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				dlg.setPixelSize( width, height );
				dlg.init();
				dlg.setPopupPosition( left, top );
				dlg.show();
			}
		} );
	}
}
