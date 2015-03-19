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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

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
import org.kablink.teaming.gwt.client.rpc.shared.TestKeyShieldConnectionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestKeyShieldConnectionResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * ?
 *  
 * @author jwootton
 */
public class EditKeyShieldConfigDlg extends DlgBox
	implements
		EditSuccessfulHandler,
		EditCanceledHandler,
		KeyPressHandler
{
	private GwtKeyShieldConfig m_config;
	private boolean m_testConnectionInProgress = false;
	
	private CheckBox m_enableKeyShieldCheckbox;
	private TextBox m_serverUrlTextBox;
	private TextBox m_timeoutTextBox;
	private TextBox m_apiAuthKeyTextBox;
	private TextBox m_authConnectorNamesTextBox;
	private FlowPanel m_alertPanel;
	private FlowPanel m_stackTracePanel;

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
		table.setCellSpacing( 8 );
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
		
		// Add the HTTP connection timeout controls
		{
			InlineLabel tmpLabel;
			
			tmpPanel = new FlowPanel();
			label = new Label( messages.editKeyShieldConfigDlg_HttpConnectionTimeoutLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );

			tmpPanel = new FlowPanel();
			m_timeoutTextBox = new TextBox();
			m_timeoutTextBox.setVisibleLength( 4 );
			m_timeoutTextBox.addKeyPressHandler( this );
			tmpPanel.add( m_timeoutTextBox );
			
			tmpLabel = new InlineLabel( messages.editKeyShieldConfigDlg_MilliSecondsLabel() );
			tmpLabel.addStyleName( "editKeyShieldConfigDlg_MilliSecondsLabel" );
			tmpPanel.add( tmpLabel );
			
			table.setWidget( row, 1, tmpPanel );
			++row;
		}
		
		// Add the controls for entering the authentication connector names
		{
			// Add a little space
			{
				tmpPanel = new FlowPanel();
				tmpPanel.addStyleName( "editKeyShieldConfigDlg_ConnectorNamesSpacing" );
				table.setWidget( row, 0, tmpPanel );
				++row;
			}
			
			// Add a hint.
			tmpPanel = new FlowPanel();
			label = new Label( messages.editKeyShieldConfigDlg_AuthConnectorNamesHint() );
			label.getElement().getStyle().setWidth( 600, Unit.PX );
			label.addStyleName( "editKeyShieldConfigDlg_Hint" );
			tmpPanel.add( label );
			table.setHTML( row, 1, tmpPanel.getElement().getInnerHTML() );
			++row;
			
			tmpPanel = new FlowPanel();
			label = new Label( messages.editKeyShieldConfigDlg_ConnectorNamesLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
			
			m_authConnectorNamesTextBox = new TextBox();
			m_authConnectorNamesTextBox.setVisibleLength( 40 );
			table.setWidget( row, 1, m_authConnectorNamesTextBox );
			++row;
		}
		
		// Add a "test connection" button
		{
			Button testConnectionBtn;
			
			// Add a little space
			{
				tmpPanel = new FlowPanel();
				tmpPanel.addStyleName( "editKeyShieldConfigDlg_TestConnectionSpacing" );
				table.setWidget( row, 0, tmpPanel );
				++row;
			}
			
			// Add "Test connection" button
			testConnectionBtn = new Button( messages.editKeyShieldConfigDlg_TestConnectionLabel() );
			testConnectionBtn.addStyleName( "teamingButton" );
			testConnectionBtn.addClickHandler( new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							testConnection();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			} );
			
			table.setWidget( row, 0, testConnectionBtn );
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
	 * 
	 */
	private TreeSet<String> getAuthConnectorNames()
	{
		String value;
		TreeSet<String> retValue;
		
		retValue = new TreeSet<String>();
		
		value = m_authConnectorNamesTextBox.getValue();
		if ( value != null )
		{
			String[] names;
			
			names = value.split( "," );
			if ( names != null )
			{
				for ( String nextName: names )
				{
					retValue.add( nextName );
				}
			}
			else
				retValue.add( value );
		}
		
		return retValue;
	}
	
	/**
	 * 
	 */
	private String convertTreeSetToCommaDelimitedString( TreeSet<String> set )
	{
		String value = "";
		
		if ( set != null )
		{
			StringBuffer strBuff;
			Iterator<String> iter;

			strBuff = new StringBuffer();
			
			iter = set.iterator();
			while ( iter.hasNext() )
			{
				String nextName;
				
				nextName = iter.next();
				if ( strBuff.length() > 0 )
					strBuff.append( ',' );
				
				strBuff.append( nextName );
			}
			
			value = strBuff.toString().toLowerCase();
		}
		
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
		config.setAuthConnectorNames( getAuthConnectorNames() );
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
		m_testConnectionInProgress = false;
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
		m_authConnectorNamesTextBox.setValue( "" );

		if ( config == null )
			return;
		
		m_config = config;
		
		m_enableKeyShieldCheckbox.setValue( m_config.isEnabled() );
		m_serverUrlTextBox.setValue( m_config.getServerUrl() );
		m_timeoutTextBox.setValue( String.valueOf( m_config.getHttpConnectionTimeout() ) );
		m_apiAuthKeyTextBox.setValue( m_config.getApiAuthKey() );
		
		{
			String value;
			
			value = convertTreeSetToCommaDelimitedString( m_config.getAuthConnectorNames() );

			m_authConnectorNamesTextBox.setValue( value );
		}
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
		
		{
			String value1;
			String value2;
			TreeSet<String> set1;
			TreeSet<String> set2;
			
			set1 = getAuthConnectorNames();
			value1 = convertTreeSetToCommaDelimitedString( set1 );
			
			set2 = m_config.getAuthConnectorNames();
			value2 = convertTreeSetToCommaDelimitedString( set2 );
			
			if ( value1.equalsIgnoreCase( value2 ) == false )
				return true;
		}
		
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
		clearErrorPanel();
		hideErrorPanel();

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
					SaveKeyShieldConfigRpcResponseData responseData;
					
					responseData = (SaveKeyShieldConfigRpcResponseData) response.getResponseData();
					if ( responseData.getSaveSuccessfull() == true )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute() 
							{
								hide();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
					else
					{
						FlowPanel errorPanel;
						Label label;
						
						errorPanel = getErrorPanel();
						label = new Label( GwtTeaming.getMessages().editKeyShieldConfigDlg_ErrorSavingConfig() );
						label.addStyleName( "dlgErrorLabel" );
						errorPanel.add( label );
						
						showErrorPanel();
					}
				}
			}
		}; 

		// Execute a GWT RPC command to save the KeyShield configuration
		cmd = new SaveKeyShieldConfigCmd();
		cmd.setConfig( config );
		
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * 
	 */
	private void testConnection()
	{
		AsyncCallback<VibeRpcResponse> rpcCallback;
		TestKeyShieldConnectionCmd cmd;
		GwtKeyShieldConfig config;

		if ( m_testConnectionInProgress == true )
			return;
		
		showStatusMsg( GwtTeaming.getMessages().testConnection_InProgressLabel() );
	
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				String errMsg;

				hideStatusMsg();
				m_testConnectionInProgress = false;
				errMsg = GwtTeaming.getMessages().rpcFailure_ErrorTestingKeyShieldConnection();
				Window.alert( errMsg );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				TestKeyShieldConnectionResponse response;
				String msg = null;
				
				hideStatusMsg();
				m_testConnectionInProgress = false;
				m_alertPanel = null;
				response = (TestKeyShieldConnectionResponse) result.getResponseData();
				switch ( response.getStatusCode() )
				{
				case NORMAL:
					msg = GwtTeaming.getMessages().testConnection_Normal();
					break;
				
				case FAILED:
					// Create a panel that will display the cause of the failure and a link
					// the user can click on to see the stack trace.
					{
						Label label;
						String labelText;
						String statusDesc;
						String stackTrace;
						
						m_alertPanel = new FlowPanel();
						
						labelText = GwtTeaming.getMessages().testConnection_FailedError();
						
						statusDesc = response.getStatusDescription();
						if ( statusDesc != null && statusDesc.length() > 0 )
						{
							labelText += " - " + statusDesc;
						}

						label = new Label( labelText );
						m_alertPanel.add( label );
						
						// Do we have a stack trace?
						stackTrace = response.getStackTrace();
						if ( stackTrace != null && stackTrace.length() > 0 )
						{
							SafeHtmlBuilder shBuilder;
							SafeHtml safeHtml;
							
							// Yes
							// Add a link the user can click on to see the stack trace.
							label = new Label( GwtTeaming.getMessages().editKeyShieldConfigDlg_StackTraceLabel() );
							label.addStyleName( "editKeyShieldDlg_SeeStackTraceLabel" );
							label.addClickHandler( new ClickHandler()
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
											m_stackTracePanel.setVisible( !m_stackTracePanel.isVisible() );
										}
									};
									Scheduler.get().scheduleDeferred( cmd );
								}
							});
							m_alertPanel.add( label );
							
							m_stackTracePanel = new FlowPanel();
							m_stackTracePanel.setVisible( false );
							m_stackTracePanel.addStyleName( "editKeyShieldDlg_StackTracePanel" );
							if ( GwtClientHelper.jsIsSafari() )
							{
								m_stackTracePanel.addStyleName( "maxWidth500" );
							}
							shBuilder = new SafeHtmlBuilder().appendEscapedLines( stackTrace );
							safeHtml = shBuilder.toSafeHtml();
							label = new Label( safeHtml.asString() );
							m_stackTracePanel.add( label );
							
							m_alertPanel.add( m_stackTracePanel );
						}
					}
					break;
				
				case UNKNOWN:
				default:
					msg = GwtTeaming.getMessages().testConnection_UnknownStatus();
					break;
				}
				
				if ( msg != null )
					GwtClientHelper.alertViaDlg( msg );
				else if ( m_alertPanel != null )
					GwtClientHelper.alertViaDlg( m_alertPanel );
			}						
		};
		
		// Issue an rpc request to test net folder root connection
		config = (GwtKeyShieldConfig) getDataFromDlg();
		cmd = new TestKeyShieldConnectionCmd( config );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
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
