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
import org.kablink.teaming.gwt.client.GwtJitsZoneConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetJitsZoneConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveJitsZoneConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
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
public class JitsZoneConfigDlg extends DlgBox
	implements KeyPressHandler, EditSuccessfulHandler
{
	private CheckBox m_enableJitsCB;
	private TextBox m_maxWaitTimeTextBox;
	
	/**
	 * Callback interface to interact with the "Jits zone configuration" dialog
	 * asynchronously after it loads. 
	 */
	public interface JitsZoneConfigDlgClient
	{
		void onSuccess( JitsZoneConfigDlg cmaDlg );
		void onUnavailable();
	}

	

	/**
	 * 
	 */
	private JitsZoneConfigDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().jitsZoneConfigDlg_Header(), this, null, null ); 
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
		m_enableJitsCB = new CheckBox( messages.jitsZoneConfigDlg_EnableJits() );
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
			
			intervalLabel = new Label( messages.jitsZoneConfigDlg_MaxWaitLabel() );
			intervalLabel.addStyleName( "marginleft3" );
			intervalLabel.addStyleName( "marginright5px" );
			hPanel.add( intervalLabel );
			
			m_maxWaitTimeTextBox = new TextBox();
			m_maxWaitTimeTextBox.addKeyPressHandler( this );
			m_maxWaitTimeTextBox.setVisibleLength( 3 );
			hPanel.add( m_maxWaitTimeTextBox );
			
			intervalLabel = new Label( messages.jitsZoneConfigDlg_SecondsLabel() );
			intervalLabel.addStyleName( "marginleft2px" );
			intervalLabel.addStyleName( "gray3" );
			hPanel.add( intervalLabel );
			
			mainPanel.add( hPanel );
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
		GwtJitsZoneConfig jitsZoneConfig;
		SaveJitsZoneConfigCmd cmd;

		clearErrorPanel();
		hideErrorPanel();

		// Disable the Ok button.
		setOkEnabled( false );

		jitsZoneConfig = (GwtJitsZoneConfig) obj;
		
		// Create the callback that will be used when we issue an ajax request to save the jits zone config.
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
					errMsg = GwtTeaming.getMessages().jitsZoneConfigDlg_OnSaveUnknownException( caught.toString() );
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

		showStatusMsg( GwtTeaming.getMessages().jitsZoneConfigDlg_SavingConfig() );

		// Issue an ajax request to save the jits zone configuration to the db.  rpcSaveCallback will
		// be called when we get the response back.
		cmd = new SaveJitsZoneConfigCmd( jitsZoneConfig );
		GwtClientHelper.executeCommand( cmd, rpcSaveCallback );
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the configuration.
		return false;
	}

	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtJitsZoneConfig object.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtJitsZoneConfig jitsZoneConfig;
		
		jitsZoneConfig = new GwtJitsZoneConfig();

		// Get whether jits is enabled
		jitsZoneConfig.setJitsEnabled( getJitsEnabled() );
		
		// Get the max wait time from the dialog.
		jitsZoneConfig.setMaxWaitTime( getMaxWaitTimeInt() * 1000 );
		
		return jitsZoneConfig;
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
		helpData.setPageId( "netfolders_justintime" );
		
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
	 * 
	 */
	public void init()
	{
		AsyncCallback<VibeRpcResponse> rpcReadCallback;
		
		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );

		// Create a callback that will be called when we get the jits zone config.
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
					GwtTeaming.getMessages().rpcFailure_GetJitsZoneConfig() );
			}
	
			/**
			 * We successfully retrieved the jits zone config.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtJitsZoneConfig )
				{
					Scheduler.ScheduledCommand cmd;
					final GwtJitsZoneConfig jitsZoneConfig;
					
					jitsZoneConfig = (GwtJitsZoneConfig) response.getResponseData();
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							init( jitsZoneConfig );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Issue an ajax request to get the jits zone config.
		{
			GetJitsZoneConfigCmd cmd;
			
			// Issue an ajax request to get the jits zone config from the db.
			cmd = new GetJitsZoneConfigCmd();
			GwtClientHelper.executeCommand( cmd, rpcReadCallback );
		}
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the given values.
	 */
	private void init( GwtJitsZoneConfig jitsZoneConfig )
	{
		long maxWaitTime;
		
		// Initialize whether jits is enabled.
		m_enableJitsCB.setValue( jitsZoneConfig.getJitsEnabled() );
			
		// Initialize the max wait time textbox
		maxWaitTime = jitsZoneConfig.getMaxWaitTime();
		m_maxWaitTimeTextBox.setText( String.valueOf( maxWaitTime ) );
		
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
	 * Loads the JitsZoneConfigDlg split point and returns an instance
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
							final JitsZoneConfigDlgClient jzcDlgClient )
	{
		GWT.runAsync( JitsZoneConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_JitsZoneConfigDlg() );
				if ( jzcDlgClient != null )
				{
					jzcDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				JitsZoneConfigDlg jzcDlg;
				
				jzcDlg = new JitsZoneConfigDlg(
											autoHide,
											modal,
											left,
											top,
											width,
											height );
				jzcDlgClient.onSuccess( jzcDlg );
			}
		});
	}
}
