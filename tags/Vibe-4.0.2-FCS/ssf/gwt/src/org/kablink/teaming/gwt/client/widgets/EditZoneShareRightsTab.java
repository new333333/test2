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
import java.util.List;

import org.kablink.teaming.gwt.client.GwtRole;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.ZoneShareRights;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetZoneShareRightsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveZoneShareRightsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.ZoneShareRightsSelectPrincipalsWidget.ZoneShareRightsSelectPrincipalsWidgetClient;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This composite is used to set the zone share rights, i.e., who can
 * share internal/external/public etc...
 * 
 * @author jwootton
 */
public class EditZoneShareRightsTab extends EditZoneShareTabBase
{
	private CheckBox m_allowShareWithLdapGroupsCB;
	private EditZoneShareSettingsDlg m_shareDlg;
	private ZoneShareRights m_zoneShareRights;
	private ZoneShareRightsSelectPrincipalsWidget m_selectPrincipalsWidget;
	private List<HandlerRegistration> m_registeredEventHandlers;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
	};

	
	/**
	 * Constructor method. 
	 */
	public EditZoneShareRightsTab(EditZoneShareSettingsDlg shareDlg)
	{
		// Initialize the super class...
		super();
		
		// ...save the parameter...
		m_shareDlg = shareDlg;
		
		// ...and create the of the tab.
		initWidget( createContent() );
	}

	/**
	 * Called if the user cancels the dialog.
	 * 
	 * Implements the EditZoneShareTabBase.cancel() method.
	 */
	@Override
	public void cancel(EditZoneShareTabCallback callback) {
		// We always allow the tab to be canceled.
		callback.success();
	}

	/*
	 * Create all the controls that make up the tab.
	 */
	private Panel createContent()
	{
		FlowPanel mainPanel;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "editZoneShareRightsTab_Content" );
		
		// Create the panel that will hold the controls for access rights
		{
			Panel rightsPanel;
			
			rightsPanel = createRightsPanel();
			mainPanel.add( rightsPanel );
		}
		
		return mainPanel;
	}

	/**
	 * Create the panel that holds all of the controls for the share rights
	 */
	private Panel createRightsPanel()
	{
		FlowPanel mainPanel;
		final FlexTable table;
		FlexCellFormatter cellFormatter;
		Label label;
		int nextRow = 0;
		final int selectPrincipalsWidgetRow;
		GwtTeamingMessages messages;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		
		// Add the "Allow all users to share with groups that have been imported from LDAP" check box
		{
			FlowPanel tmpPanel;
			
			tmpPanel = new FlowPanel();
			tmpPanel.getElement().getStyle().setMarginTop( 4, Unit.PX );
			tmpPanel.getElement().getStyle().setMarginBottom( 24, Unit.PX );

			String cbText;
			if ( m_shareDlg.isSupportsExternalUserImport() )
			     cbText = messages.editZoneShareRightsTab_AllowShareWithLdapGroups_WithExternal();
			else cbText = messages.editZoneShareRightsTab_AllowShareWithLdapGroups_NoExternal();
			
			m_allowShareWithLdapGroupsCB = new CheckBox( cbText );
			tmpPanel.add( m_allowShareWithLdapGroupsCB );
			mainPanel.add( tmpPanel );
		}
		
		table = new FlexTable();
		cellFormatter = table.getFlexCellFormatter();
		mainPanel.add( table );
		
		// Add a hint
		cellFormatter.setColSpan( nextRow, 0, 2 );
		cellFormatter.setWordWrap( nextRow, 0, false );
		cellFormatter.addStyleName( nextRow, 0, "editZoneShareRightsTab_SelectPrincipalsHint" );
		label = new InlineLabel( messages.editZoneShareRightsTab_SelectPrincipalsHint() );
		table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
		++nextRow;
		
		cellFormatter.setColSpan( nextRow, 0, 2 );
		selectPrincipalsWidgetRow = nextRow;
		++nextRow;
		
		// Create a widget that lets the user select users and groups.
		ZoneShareRightsSelectPrincipalsWidget.createAsync( new ZoneShareRightsSelectPrincipalsWidgetClient() 
		{
			@Override
			public void onUnavailable() 
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ZoneShareRightsSelectPrincipalsWidget widget )
			{
				m_selectPrincipalsWidget = widget;
				table.setWidget( selectPrincipalsWidgetRow, 0, m_selectPrincipalsWidget );
			}
		} );
	
		return mainPanel;
	}
	
	/**
	 * Return the roles (rights) the user defined
	 */
	private ArrayList<GwtRole> getRoles()
	{
		return m_selectPrincipalsWidget.getRoles();
	}

	/**
	 * Issue an rpc request to get the zone share rights from the server.
	 */
	private void getShareRightsFromServer()
	{
		AsyncCallback<VibeRpcResponse> rpcCallback;

		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				m_shareDlg.hideStatusMsg();
				
				GwtClientHelper.handleGwtRPCFailure(
												caught,
												GwtTeaming.getMessages().rpcFailure_GetZoneShareRights() );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				Object obj;
				Scheduler.ScheduledCommand cmd;
				
				obj = result.getResponseData();
				if ( obj != null && obj instanceof ZoneShareRights )
				{
					m_zoneShareRights = (ZoneShareRights) obj;
				}

				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Initialize the share rights controls
						initShareRights();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
				
				m_shareDlg.hideStatusMsg();
				m_shareDlg.setOkEnabled( true );
			}						
		};
		
		// Issue an rpc request to read the zone share rights
		{
			GetZoneShareRightsCmd cmd;

			m_shareDlg.showStatusMsg( GwtTeaming.getMessages().editZoneShareRightsTab_ReadingRights() );
			m_shareDlg.setOkEnabled( false );

			cmd = new GetZoneShareRightsCmd();
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	/**
	 * 
	 */
	@Override
	public void init()
	{
		m_shareDlg.hideErrorPanel();
		
		m_shareDlg.clearErrorPanel();
		m_shareDlg.hideErrorPanel();
		m_shareDlg.hideStatusMsg();
		m_shareDlg.setOkEnabled( true );
		
		m_zoneShareRights = null;
		
		// Issue an rpc request to get the zone share rights.
		getShareRightsFromServer();
	}

	/**
	 * 
	 */
	private void initShareRights()
	{
		m_allowShareWithLdapGroupsCB.setValue( m_zoneShareRights.getAllowShareWithLdapGroups() );
		
		if ( m_selectPrincipalsWidget != null && m_selectPrincipalsWidget.isReady() )
		{
			if ( m_zoneShareRights != null )
				m_selectPrincipalsWidget.initWidget( m_zoneShareRights.getRoles() );
			else
				m_selectPrincipalsWidget.initWidget( null );

			m_selectPrincipalsWidget.setSearchForExternalPrincipals( false );
			m_selectPrincipalsWidget.setSearchForInternalPrincipals( true );
		}
		else
		{
			ScheduledCommand cmd;
			
			cmd = new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					Timer timer;
					
					timer = new Timer()
					{
						@Override
						public void run()
						{
							initShareRights();
						}
					};
					
					timer.schedule( 250 );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
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
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we haven't allocated a list to track events we've registered yet...
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
											REGISTERED_EVENTS,
											this,
											m_registeredEventHandlers );
		}
	}
	
	/**
	 * Called if the user OKs the dialog.
	 * 
	 * Implements the EditZoneShareTabBase.save() method.
	 */
	@Override
	public void save( EditZoneShareTabCallback callback )
	{
		m_shareDlg.clearErrorPanel();
		m_shareDlg.hideErrorPanel();

		// Disable the Ok button.
		m_shareDlg.setOkEnabled( false );

		// Issue an rpc request to save the share rights.  If the rpc request is successful,
		// close this dialog.
		saveShareRightsAndClose( callback );
	}
	
	/**
	 * Issue an rpc request to save the share rights.  If the save was successful, close the dialog
	 */
	private void saveShareRightsAndClose( final EditZoneShareTabCallback callback )
	{
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				m_shareDlg.hideStatusMsg();
				m_shareDlg.setOkEnabled( true );
				
				GwtClientHelper.handleGwtRPCFailure(
												caught,
												GwtTeaming.getMessages().rpcFailure_SaveZoneShareRights() );
				
				callback.failure();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				m_shareDlg.hideStatusMsg();
				m_shareDlg.setOkEnabled( true );
				
				callback.success();
			}						
		};
		
		// Issue an rpc request to save the zone share rights
		{
			SaveZoneShareRightsCmd cmd;
			ArrayList<GwtRole> roles;
			ZoneShareRights zoneShareRights;

			m_shareDlg.showStatusMsg( GwtTeaming.getMessages().editZoneShareRightsTab_SavingRights() );
			m_shareDlg.setOkEnabled( false );

			// Get the share rights the user entered.
			roles = getRoles();
			zoneShareRights = new ZoneShareRights();
			zoneShareRights.setRoles( roles );
			
			zoneShareRights.setAllowShareWithLdapGroups( m_allowShareWithLdapGroupsCB.getValue() );
			
			cmd = new SaveZoneShareRightsCmd();
			cmd.setRights( zoneShareRights );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	
	/**
	 * Called to validate the contents of the tab.
	 * 
	 * Implements the EditZoneShareTabBase.validate() method.
	 */
	@Override
	public void validate(EditZoneShareTabCallback callback) {
		// The rights tab is always considered valid.
		callback.success();
	}
}
