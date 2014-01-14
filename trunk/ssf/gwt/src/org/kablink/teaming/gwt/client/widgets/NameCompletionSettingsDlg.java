/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtNameCompletionSettings;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtNameCompletionSettings.GwtDisplayField;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetNameCompletionSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveNameCompletionSettingsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveNameCompletionSettingsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;

/**
 * ?
 * 
 * @author jwootton
 */
public class NameCompletionSettingsDlg extends DlgBox
	implements EditSuccessfulHandler
{
	ListBox m_primaryLB;
	ListBox m_secondaryLB;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
	};
	

	/**
	 * Callback interface to interact with the "Name Completion Settings" dialog
	 * asynchronously after it loads. 
	 */
	public interface NameCompletionSettingsDlgClient
	{
		void onSuccess( NameCompletionSettingsDlg ncsDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private NameCompletionSettingsDlg(
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
		createAllDlgContent( GwtTeaming.getMessages().nameCompletionSettingsDlg_Header(), this, null, null );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		FlexTable table;
		int row = 0;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 4 );

		// Add a hint
		{
			Label label;
			
			label = new Label( messages.nameCompletionSettingsDlg_Hint() );
			label.addStyleName( "nameCompletionSettingsDlg_Hint" );
			mainPanel.add( label );
		}

		mainPanel.add( table );

		// Add the "Primary Display Text" controls
		{
			Label label;
			String value;
			
			label = new Label( messages.nameCompletionSettingsDlg_PrimaryDisplayLabel() );
			table.setWidget( row, 0, label );
			
			m_primaryLB  = new ListBox( false );
			m_primaryLB.setVisibleItemCount( 1 );
			table.setWidget( row, 1, m_primaryLB );
			
			value = GwtDisplayField.getFieldAsString( GwtDisplayField.NAME );
			m_primaryLB.addItem( messages.nameCompletionSettingsDlg_Name(), value );
			
			value = GwtDisplayField.getFieldAsString( GwtDisplayField.TITLE );
			m_primaryLB.addItem( messages.nameCompletionSettingsDlg_Title(), value );
		
			++row;
		}
		
		// Add the "Secondary Display Text" controls
		{
			Label label;
			
			label = new Label( messages.nameCompletionSettingsDlg_SecondaryDisplayLabel() );
			table.setWidget( row, 0, label );
			
			m_secondaryLB  = new ListBox( false );
			m_secondaryLB.setVisibleItemCount( 1 );
			table.setWidget( row, 1, m_secondaryLB );
			
			m_secondaryLB.addItem( messages.nameCompletionSettingsDlg_Desc(), "desc" );
			m_secondaryLB.addItem( messages.nameCompletionSettingsDlg_FQDN(), "fqdn" );
		
			++row;
		}
		
		return mainPanel;
	}
	
	/**
	 * This gets called when the user presses ok.  Issue an rpc request to save the user
	 * access configuration.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		if ( obj != null && obj instanceof GwtNameCompletionSettings )
			saveNameCompletionSettings( (GwtNameCompletionSettings) obj );

		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the name completion settings.
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtNameCompletionSettings settings;
		
		settings = new GwtNameCompletionSettings();
		
		settings.setGroupPrimaryDisplayField( getGroupPrimaryDisplayField() );
		settings.setGroupSecondaryDisplayField( getGroupSecondaryDisplayField() );
		
		return settings;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_primaryLB;
	}
	
	/**
	 * 
	 */
	private GwtDisplayField getGroupPrimaryDisplayField()
	{
		GwtDisplayField field;
		int index;
		
		index = m_primaryLB.getSelectedIndex();
		if ( index != -1 )
		{
			String value;
			
			value = m_primaryLB.getValue( index );
			field = GwtDisplayField.getFieldFromString( value );
		}
		else
			field = GwtDisplayField.NAME;
		
		return field;
	}
	
	/**
	 * 
	 */
	private GwtDisplayField getGroupSecondaryDisplayField()
	{
		GwtDisplayField field;
		int index;
		
		index = m_secondaryLB.getSelectedIndex();
		if ( index != -1 )
		{
			String value;
			
			value = m_secondaryLB.getValue( index );
			field = GwtDisplayField.getFieldFromString( value );
		}
		else
			field = GwtDisplayField.DESCRIPTION;
		
		return field;
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
		helpData.setPageId( "groups_namecompletion" );
		
		return helpData;
	}

	/**
	 * Issue an rpc request to get the name completion settings from the server.
	 */
	private void getNameCompletionSettingsFromServer()
	{
		GetNameCompletionSettingsCmd cmd;

		// Execute a GWT RPC command asking the server for the name completion settings
		cmd = new GetNameCompletionSettingsCmd();
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_GetNameCompletionSettings() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtNameCompletionSettings )
				{
					final GwtNameCompletionSettings settings;
					ScheduledCommand cmd;
					
					settings = (GwtNameCompletionSettings) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							init( settings);
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
		hideStatusMsg();
		setOkEnabled( true );
		
		m_primaryLB.setItemSelected( 0, true );
		m_secondaryLB.setItemSelected( 0, true );
		
		// Issue an rpc request to get the name completion settings from the server
		getNameCompletionSettingsFromServer();
	}
	
	/**
	 * 
	 */
	private void saveNameCompletionSettings( GwtNameCompletionSettings settings )
	{
		SaveNameCompletionSettingsCmd cmd;

		// Disable the Ok button.
		setOkEnabled( false );

		showStatusMsg( GwtTeaming.getMessages().nameCompletionSettingsDlg_SavingSettings() );
		
		// Execute a GWT RPC command to save the name completion settings
		cmd = new SaveNameCompletionSettingsCmd();
		cmd.setSettings( settings );
		
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_SaveNameCompletionSettings() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null &&
					 response.getResponseData() instanceof SaveNameCompletionSettingsRpcResponseData )
				{
					SaveNameCompletionSettingsRpcResponseData responseData;
					ArrayList<String> errors;
					
					hideStatusMsg();
					setOkEnabled( true );
					
					responseData = (SaveNameCompletionSettingsRpcResponseData) response.getResponseData();
					
					// Get any errors that may have happened
					errors = responseData.getErrors();
					if ( errors != null && errors.size() > 0 )
					{
						FlowPanel errorPanel;
						
						errorPanel = getErrorPanel();
						for ( String nextErr: errors )
						{
							Label label;
						
							label = new Label( nextErr );
							label.addStyleName( "dlgErrorLabel" );
							label.addStyleName( "marginleft1" );
							errorPanel.add( label );
						}
						
						showErrorPanel();
					}
					else
						hide();
				}
			}
		});
	}
	
	/**
	 * 
	 */
	private void init( GwtNameCompletionSettings settings )
	{
		String value;
		
		if ( settings == null )
			return;
		
		value = GwtDisplayField.getFieldAsString( settings.getGroupPrimaryDisplayField() );
		GwtClientHelper.selectListboxItemByValue( m_primaryLB, value );
		
		value = GwtDisplayField.getFieldAsString( settings.getGroupSecondaryDisplayField() );
		GwtClientHelper.selectListboxItemByValue( m_secondaryLB, value );
	}
	
	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void createDlg(
		final boolean autoHide,
		final boolean modal,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final NameCompletionSettingsDlgClient ncsDlgClient )
	{
		GWT.runAsync( NameCompletionSettingsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				if ( ncsDlgClient != null )
				{
					ncsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				NameCompletionSettingsDlg ncsDlg;
				
				ncsDlg = new NameCompletionSettingsDlg(
													autoHide,
													modal,
													left,
													top,
													width,
													height );
				
				if ( ncsDlgClient != null )
					ncsDlgClient.onSuccess( ncsDlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final NameCompletionSettingsDlg dlg,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final NameCompletionSettingsDlgClient ncsDlgClient )
	{
		GWT.runAsync( NameCompletionSettingsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				if ( ncsDlgClient != null )
				{
					ncsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				if ( width != null && height != null )
					dlg.setPixelSize( width, height );
				
				dlg.init();
				
				if ( left != null && top != null )
					dlg.setPopupPosition( left, top );
				
				dlg.show();
			}
		} );
	}
}
