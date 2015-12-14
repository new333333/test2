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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults.GwtLdapSyncStatus;
import org.kablink.teaming.gwt.client.GwtLocales;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTimeZones;
import org.kablink.teaming.gwt.client.datatable.LdapServerUrlCell;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeLdapSyncResultsDlgEvent;
import org.kablink.teaming.gwt.client.event.LdapSyncStatusEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapSupportsExternalUserImportCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetLocalesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTimeZonesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveLdapConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveLdapConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StartLdapSyncCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StartLdapSyncRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditLdapServerConfigDlg.EditLdapServerConfigDlgClient;
import org.kablink.teaming.gwt.client.widgets.LdapSyncResultsDlg.LdapSyncResultsDlgClient;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * ?
 *  
 * @author jwootton
 */
public class EditLdapConfigDlg extends DlgBox
	implements
		EditSuccessfulHandler,
		EditCanceledHandler,
		LdapSyncStatusEvent.Handler,
		InvokeLdapSyncResultsDlgEvent.Handler
{
	private boolean m_supportsExternalUserImport;
	
	private GwtLdapConfig m_ldapConfig;
	
	private CellTable<GwtLdapConnectionConfig> m_ldapServersTable;
    private MultiSelectionModel<GwtLdapConnectionConfig> m_selectionModel;
	private ListDataProvider<GwtLdapConnectionConfig> m_dataProvider;
	private VibeSimplePager m_pager;
	private List<GwtLdapConnectionConfig> m_listOfLdapServers;
	private TabPanel m_serversTabPanel;

	private CheckBox m_syncUserProfilesCheckBox;
	private CheckBox m_registerUserProfilesAutomaticallyCheckBox;
	private RadioButton m_disableUsersRB;
	private RadioButton m_deleteUsersRB;
	private CheckBox m_deleteWorkspaceCheckBox;
	private ListBox m_timeZonesListbox;
	private ListBox m_localesListbox;

	private CheckBox m_syncGroupProfilesCheckBox;
	private CheckBox m_registerGroupProfilesAutomaticallyCheckBox;
	private CheckBox m_syncGroupMembershipCheckBox;
	private CheckBox m_deleteGroupsCheckBox;
	
	private ScheduleWidget m_scheduleWidget;
	
	private CheckBox m_allowLocalLoginCheckBox;

	private EditLdapServerConfigDlg m_editLdapServerDlg = null;
	private LdapSyncResultsDlg m_ldapSyncResultsDlg = null;
	
	private boolean m_isDirty;
	private String m_ldapSyncId;
	private GwtLdapSyncStatus m_ldapSyncStatus;
	private GwtLdapSyncMode m_lastSyncMode;

	private GwtTeamingMessages m_messages;
	
	private List<HandlerRegistration> m_registeredEventHandlers;

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
		TeamingEvents.LDAP_SYNC_STATUS,
		TeamingEvents.INVOKE_LDAP_SYNC_RESULTS_DLG
	};
	

	/**
	 * Callback interface to interact with the "edit ldap config" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditLdapConfigDlgClient
	{
		void onSuccess( EditLdapConfigDlg elcDlg );
		void onUnavailable();
	}
	
	/**
	 * 
	 */
	public enum GwtLdapSyncMode implements IsSerializable
	{
		PERFORM_SYNC,
		PREVIEW_ONLY
	}

	
	/**
	 * 
	 */
	private EditLdapConfigDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height,
		EditLdapConfigDlgClient elcDlgClient )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );
		
		m_messages = GwtTeaming.getMessages();
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( m_messages.editLdapConfigDlg_Header(), this, this, elcDlgClient );
		
		m_lastSyncMode = GwtLdapSyncMode.PERFORM_SYNC;
	}

	/**
	 * Add the given list of ldap servers to the dialog
	 */
	private void addLdapServers( List<GwtLdapConnectionConfig> listOfLdapServers )
	{
		if ( m_listOfLdapServers == null )
			m_listOfLdapServers = new ArrayList<GwtLdapConnectionConfig>();
		else
			m_listOfLdapServers.clear();
		
		if ( listOfLdapServers != null )
		{
			// Make a copy of the list
			for ( GwtLdapConnectionConfig nextServerConfig : listOfLdapServers )
			{
				m_listOfLdapServers.add( nextServerConfig );
			}
		}
		
		if ( m_dataProvider == null )
		{
			m_dataProvider = new ListDataProvider<GwtLdapConnectionConfig>( m_listOfLdapServers );
			m_dataProvider.addDataDisplay( m_ldapServersTable );
		}
		else
		{
			m_dataProvider.setList( m_listOfLdapServers );
		}
		
		// Clear all selections.
		m_selectionModel.clear();
		
		// Go to the first page
		m_pager.firstPage();
		
		// Tell the table how many ldap servers we have.
		m_ldapServersTable.setRowCount( m_listOfLdapServers.size(), true );
		m_dataProvider.refresh();
	}


	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		final EditLdapConfigDlgClient elcDlgClient = ((EditLdapConfigDlgClient) props);
		
		FlowPanel mainPanel = null;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_serversTabPanel = new TabPanel();
		m_serversTabPanel.addStyleName( "vibe-tabPanel" );
		
		GwtClientHelper.executeCommand( new GetLdapSupportsExternalUserImportCmd(), new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetLdapSupportsExternalUserImport() );
				
				m_supportsExternalUserImport = false;
				createTabPanelsAsync( elcDlgClient );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				BooleanRpcResponseData reply = ((BooleanRpcResponseData) result.getResponseData());
				m_supportsExternalUserImport = reply.getBooleanValue();
				createTabPanelsAsync( elcDlgClient );
			}
		} );


		mainPanel.add( m_serversTabPanel );
		
		return mainPanel;
	}
	
	private void createTabPanelsAsync( final EditLdapConfigDlgClient elcDlgClient )
	{
		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				createTabPanelsNow( elcDlgClient );
			}
		} );
	}
	
	private void createTabPanelsNow( final EditLdapConfigDlgClient elcDlgClient )
	{
		// Create a panel to hold the list of ldap servers
		{
			Panel serversPanel;
			
			serversPanel = createLdapServersPanel();
			m_serversTabPanel.add( serversPanel, m_messages.editLdapConfigDlg_LdapServersTab() );
		}
		
		// Create a panel to hold the user configuration
		{
			Panel userPanel;
			
			userPanel = createUserConfigPanel();
			m_serversTabPanel.add( userPanel, m_messages.editLdapConfigDlg_UsersTab() );
		}
		
		// Create a panel to hold the group configuration
		{
			Panel groupPanel;
			
			groupPanel = createGroupConfigPanel();
			m_serversTabPanel.add( groupPanel, m_messages.editLdapConfigDlg_GroupsTab() );
		}

		// Add controls dealing with sync schedule
		{
			Panel schedulePanel;
			
			schedulePanel = createSchedulePanel();
			m_serversTabPanel.add( schedulePanel, m_messages.editLdapConfigDlg_ScheduleTab() );
		}
		
		// Add controls dealing with local user accounts
		{
			Panel localPanel;
			
			localPanel = createLocalAccountsPanel();
			m_serversTabPanel.add( localPanel, m_messages.editLdapConfigDlg_LocalUserAccountsTab() );
		}
		
		m_serversTabPanel.selectTab( 0 );
		
		if ( null != elcDlgClient )
		{
			elcDlgClient.onSuccess( this );
		}
	}

	/**
	 * 
	 */
	private Panel createGroupConfigPanel()
	{
		FlowPanel groupPanel;
		FlowPanel tmpPanel;
		
		groupPanel = new FlowPanel();
		
		tmpPanel = new FlowPanel();
		m_registerGroupProfilesAutomaticallyCheckBox = new CheckBox( m_messages.editLdapConfigDlg_RegisterGroupProfilesAutomatically() );
		tmpPanel.add( m_registerGroupProfilesAutomaticallyCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_syncGroupProfilesCheckBox = new CheckBox( m_messages.editLdapConfigDlg_SyncGroupProfiles() );
		tmpPanel.add( m_syncGroupProfilesCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_syncGroupMembershipCheckBox = new CheckBox( m_messages.editLdapConfigDlg_SyncGroupMembership() );
		tmpPanel.add( m_syncGroupMembershipCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_deleteGroupsCheckBox = new CheckBox( m_messages.editLdapConfigDlg_DeleteGroupsLabel() );
		tmpPanel.add( m_deleteGroupsCheckBox );
		groupPanel.add( tmpPanel );
		
		return groupPanel;
	}
	
	/**
	 * 
	 */
	private Panel createLdapServersPanel()
	{
		VerticalPanel serversPanel;
		CellTable.Resources cellTableResources;
		FlowPanel menuPanel;
		
		serversPanel = new VerticalPanel();

		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "editLdapConfigDlg_MenuPanel" );
			
			// Add an "Add" button.
			label = new InlineLabel( m_messages.editLdapConfigDlg_AddLdapServerLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
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
							invokeAddLdapServerDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
			
			// Add a "Delete" button.
			label = new InlineLabel( m_messages.editLdapConfigDlg_DeleteLdapServerLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
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
							deleteSelectedLdapServers();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
			
			// Add a "Sync all" button.
			label = new InlineLabel( m_messages.editLdapConfigDlg_SyncLdapServerLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
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
							handleClickOnSyncAllButton();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );

			// Add a "Preview sync" button.
			label = new InlineLabel( m_messages.editLdapConfigDlg_PreviewLdapSyncLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
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
							handleClickOnPreviewSyncButton();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );

			// Add a "Show sync results" button.
			label = new InlineLabel( m_messages.editLdapConfigDlg_ShowSyncResultsLabel() );
			label.addStyleName( "editLdapConfigDlg_Btn" );
			label.addClickHandler( new ClickHandler()
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
							showLdapSyncResults();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
		}
	
		// Create the CellTable that will display the list of ldap servers.
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_ldapServersTable = new CellTable<GwtLdapConnectionConfig>( 20, cellTableResources );
		
		// Set the widget that will be displayed when there are no ldap servers
		{
			FlowPanel flowPanel;
			InlineLabel noServersLabel;
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noServersLabel = new InlineLabel( m_messages.editLdapConfigDlg_NoLdapServersLabel() );
			flowPanel.add( noServersLabel );
			
			m_ldapServersTable.setEmptyTableWidget( flowPanel );
		}
		
	    // Add a selection model so we can select ldap servers.
	    m_selectionModel = new MultiSelectionModel<GwtLdapConnectionConfig>();
	    m_ldapServersTable.setSelectionModel(
	    									m_selectionModel,
	    									DefaultSelectionEventManager.<GwtLdapConnectionConfig> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<GwtLdapConnectionConfig, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<GwtLdapConnectionConfig, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( GwtLdapConnectionConfig ldapServer )
		        {
            		// Get the value from the selection model.
		            return m_selectionModel.isSelected( ldapServer );
		        }
		    };
	        m_ldapServersTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_ldapServersTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Server URL" column.  The user can click on the text in this column
		// to edit the ldap server.
		{
			LdapServerUrlCell cell;
			Column<GwtLdapConnectionConfig,GwtLdapConnectionConfig> serverUrlCol;

			cell = new LdapServerUrlCell();
			serverUrlCol = new Column<GwtLdapConnectionConfig, GwtLdapConnectionConfig>( cell )
			{
				@Override
				public GwtLdapConnectionConfig getValue( GwtLdapConnectionConfig ldapServer )
				{
					return ldapServer;
				}
			};
		
			serverUrlCol.setFieldUpdater( new FieldUpdater<GwtLdapConnectionConfig, GwtLdapConnectionConfig>()
			{
				@Override
				public void update( int index, final GwtLdapConnectionConfig ldapServer, GwtLdapConnectionConfig value )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							invokeModifyExistingLdapServerDlg( ldapServer );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			m_ldapServersTable.addColumn( serverUrlCol, m_messages.editLdapConfigDlg_ServerUrlCol() );
		}

		// If we support importing as external users...
		if ( m_supportsExternalUserImport )
		{
			// ...add the "User Type" column
			{
				TextColumn<GwtLdapConnectionConfig> userTypeCol;

				userTypeCol = new TextColumn<GwtLdapConnectionConfig>()
				{
					@Override
					public String getValue( GwtLdapConnectionConfig ldapServer )
					{
						String userType;
						if ( ldapServer.isImportUsersAsExternalUsers() )
						     userType = m_messages.editLdapServerConfigDlg_UserTypeExternal();
						else userType = m_messages.editLdapServerConfigDlg_UserTypeInternal();
						return userType;
					}
				};
				m_ldapServersTable.addColumn( userTypeCol, m_messages.editLdapConfigDlg_UserTypeCol() );
			}
		}
		
		// Add the "User DN" column
		{
			TextColumn<GwtLdapConnectionConfig> userDNCol;

			userDNCol = new TextColumn<GwtLdapConnectionConfig>()
			{
				@Override
				public String getValue( GwtLdapConnectionConfig ldapServer )
				{
					String userDN;
					
					userDN = ldapServer.getProxyDn();
					if ( userDN == null )
						userDN = "";
					
					return userDN;
				}
			};
			m_ldapServersTable.addColumn( userDNCol, m_messages.editLdapConfigDlg_UserDNCol() );
		}

		// Create a pager
		{
			m_pager = new VibeSimplePager();
			m_pager.setDisplay( m_ldapServersTable );
		}

		serversPanel.add( menuPanel );
		serversPanel.add( m_ldapServersTable );
		serversPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		serversPanel.add( m_pager );
		serversPanel.setCellHeight( m_pager, "100%" );

		return serversPanel;
	}

	/**
	 * 
	 */
	private Panel createLocalAccountsPanel()
	{
		FlowPanel localPanel;
		FlowPanel tmpPanel;
		
		localPanel = new FlowPanel();
		
		tmpPanel = new FlowPanel();
		m_allowLocalLoginCheckBox = new CheckBox( m_messages.editLdapConfigDlg_AllowLocalLoginLabel() );
		tmpPanel.add( m_allowLocalLoginCheckBox );
		localPanel.add( tmpPanel );
		
		return localPanel;
	}

	/**
	 * 
	 */
	private Panel createSchedulePanel()
	{
		FlowPanel schedulePanel;
		
		schedulePanel = new FlowPanel();
		
		m_scheduleWidget = new ScheduleWidget( m_messages.editLdapConfigDlg_EnableSyncScheduleLabel() );
		schedulePanel.add( m_scheduleWidget );
		
		return schedulePanel;
	}

	/**
	 * 
	 */
	private Panel createUserConfigPanel()
	{
		FlowPanel userPanel;
		Label label;
		
		userPanel = new FlowPanel();
		
		// Add the controls dealing with syncing users
		{
			FlowPanel tmpPanel;

			tmpPanel = new FlowPanel();
			m_registerUserProfilesAutomaticallyCheckBox = new CheckBox( m_messages.editLdapConfigDlg_RegisterUserProfilesAutomatically() );
			tmpPanel.add( m_registerUserProfilesAutomaticallyCheckBox );
			userPanel.add( tmpPanel );
	
			tmpPanel = new FlowPanel();
			m_syncUserProfilesCheckBox = new CheckBox( m_messages.editLdapConfigDlg_SyncUserProfiles() );
			tmpPanel.add( m_syncUserProfilesCheckBox );
			userPanel.add( tmpPanel );
		}
		
		// Add the controls dealing with deleting users
		{
			FlowPanel tmpPanel;
			ClickHandler clickHandler;
			
			clickHandler = new ClickHandler()
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
							if ( m_deleteUsersRB.getValue() == true )
							{
								// Warn the admin about deleting users
								Window.alert( m_messages.editLdapConfigDlg_DeleteUsersWarning() );
							}
							
							danceDlg();
						}
					};
					
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			label = new Label( m_messages.editLdapConfigDlg_DisableUserLabel() );
			label.addStyleName( "margintop3 fontSize16px" );
			userPanel.add( label );

			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "margintop2 marginleft1" );
			m_disableUsersRB = new RadioButton( "notInLdap", m_messages.editLdapConfigDlg_DisableUserLabel2() );
			m_disableUsersRB.addClickHandler( clickHandler );
			tmpPanel.add( m_disableUsersRB );
			userPanel.add( tmpPanel );
			
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "margintop1 marginleft1" );
			m_deleteUsersRB = new RadioButton( "notInLdap", m_messages.editLdapConfigDlg_DeleteUserLabel() );
			m_deleteUsersRB.addClickHandler( clickHandler );
			tmpPanel.add( m_deleteUsersRB );
			userPanel.add( tmpPanel );
			
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "marginleft2" );
			m_deleteWorkspaceCheckBox = new CheckBox( m_messages.editLdapConfigDlg_DeleteWorkspaceLabel() );
			tmpPanel.add( m_deleteWorkspaceCheckBox );
			userPanel.add( tmpPanel );
		}
		
		// Add the controls dealing time zone
		{
			label = new Label( m_messages.editLdapConfigDlg_CreatingUsersLabel() );
			label.addStyleName( "margintop3 fontSize16px" );
			userPanel.add( label );

			label = new Label( m_messages.editLdapConfigDlg_DefaultTimeZoneLabel() );
			label.addStyleName( "margintop2 marginleft1" );
			userPanel.add( label );
			
			m_timeZonesListbox = new ListBox();
			m_timeZonesListbox.setMultipleSelect( false );
			m_timeZonesListbox.setVisibleItemCount( 1 );
			m_timeZonesListbox.addStyleName( "marginleft1" );
			userPanel.add( m_timeZonesListbox );
		}
		
		// Add the controls dealing with locale
		{
			label = new Label( m_messages.editLdapConfigDlg_DefaultLocaleLabel() );
			label.addStyleName( "margintop2 marginleft1" );
			userPanel.add( label );
			
			m_localesListbox = new ListBox();
			m_localesListbox.setMultipleSelect( false );
			m_localesListbox.setVisibleItemCount( 1 );
			m_localesListbox.addStyleName( "marginleft1" );
			userPanel.add( m_localesListbox );
		}

		return userPanel;
	}
	
	/**
	 * 
	 */
	private void danceDlg()
	{
		if ( m_deleteUsersRB.getValue() == true )
			m_deleteWorkspaceCheckBox.setEnabled( true );
		else
			m_deleteWorkspaceCheckBox.setEnabled( false );
	}
	
	/**
	 * 
	 */
	private void deleteSelectedLdapServers()
	{
		Set<GwtLdapConnectionConfig> selectedServers;
		Iterator<GwtLdapConnectionConfig> serverIterator;
		String serverUrls;
		int count = 0;
		
		serverUrls = "";
		
		selectedServers = getSelectedLdapServers();
		if ( selectedServers != null )
		{
			// Get a list of all the selected ldap server urls
			serverIterator = selectedServers.iterator();
			while ( serverIterator.hasNext() )
			{
				GwtLdapConnectionConfig nextServer;
				String text;
				
				nextServer = serverIterator.next();
				
				text = nextServer.getServerUrl();
				serverUrls += "\t" + text + "\n";
				
				++count;
			}
		}
		
		// Do we have any servers to delete?
		if ( count > 0 )
		{
			String msg;
			
			// Yes, ask the user if they want to delete the selected ldap servers?
			msg = m_messages.editLdapConfigDlg_ConfirmDelete( serverUrls );
			if ( Window.confirm( msg ) )
			{
				m_isDirty = true;
				
				// Remove the selected ldap servers from our list.
				for ( GwtLdapConnectionConfig nextLdapServer : selectedServers )
				{
					m_listOfLdapServers.remove( nextLdapServer );
				}
				
				// Unselect the selected ldap servers.
				m_selectionModel.clear();
				
				// Update the table to reflect the fact that we deleted an ldap server.
				m_dataProvider.refresh();

				// Tell the table how many ldap servers we have left.
				m_ldapServersTable.setRowCount( m_listOfLdapServers.size(), true );
			}
		}
		else
		{
			Window.alert( m_messages.editLdapConfigDlg_SelectLdapServersToDelete() );
		}
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
			if ( Window.confirm( m_messages.confirmChangesWillBeLost() ) )
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
		Scheduler.ScheduledCommand cmd1;
		
		if ( (obj instanceof GwtLdapConfig) == false )
			return false;

		cmd1 = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				Scheduler.ScheduledCommand cmd;
				final String[] listOfLdapConfigsToSyncGuid;
				final boolean doSync;

				// Get the list of ldap config ids whose ldap guid attribute changed
				listOfLdapConfigsToSyncGuid = getListOfLdapConfigsToSyncGuid( (GwtLdapConfig) obj );
				
				// Did the ldap guid attribute change for any of the ldap servers?
				if ( listOfLdapConfigsToSyncGuid != null && listOfLdapConfigsToSyncGuid.length > 0 )
				{
					// Yes, tell the user we need to sync the guids for all users and groups.
					Window.alert( m_messages.editLdapConfigDlg_LdapGuidAttribChanged() );

					doSync = true;
				}
				else
					doSync = false;
				
				cmd = new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						if ( doSync )
							init( true, false, listOfLdapConfigsToSyncGuid );
						else
							hide();
					}
				};
				
				// Issue an rpc request to save the ldap configuration
				saveLdapConfiguration( (GwtLdapConfig) obj, cmd );
			}
		};
		Scheduler.get().scheduleDeferred( cmd1 );

		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the user access configuration.
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtLdapConfig ldapConfig;
		
		ldapConfig = new GwtLdapConfig();
		
		// Get the user configuration
		{
			ldapConfig.setRegisterUserProfilesAutomatically( m_registerUserProfilesAutomaticallyCheckBox.getValue() );
			ldapConfig.setSyncUserProfiles( m_syncUserProfilesCheckBox.getValue() );
			ldapConfig.setDeleteLdapUsers( m_deleteUsersRB.getValue() );
			ldapConfig.setDeleteUserWorkspace( m_deleteWorkspaceCheckBox.getValue() );
			
			ldapConfig.setTimeZone( getSelectedTimeZone() );
			ldapConfig.setLocale( getSelectedLocale() );
		}
		
		// Get the group configuration
		{
			ldapConfig.setRegisterGroupProfilesAutomatically( m_registerGroupProfilesAutomaticallyCheckBox.getValue() );
			ldapConfig.setSyncGroupProfiles( m_syncGroupProfilesCheckBox.getValue() );
			ldapConfig.setSyncGroupMembership( m_syncGroupMembershipCheckBox.getValue() );
			ldapConfig.setDeleteNonLdapGroups( m_deleteGroupsCheckBox.getValue() );
		}
		
		// Get the sync schedule
		ldapConfig.setSchedule( m_scheduleWidget.getSchedule() );

		// Get the local user accounts configuration
		ldapConfig.setAllowLocalLogin( m_allowLocalLoginCheckBox.getValue() );
		
		// Get the list of ldap servers
		if ( m_listOfLdapServers != null )
		{
			for ( GwtLdapConnectionConfig nextServer : m_listOfLdapServers )
			{
				ldapConfig.addLdapConnectionConfig( nextServer );
			}
		}
		
		return ldapConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
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
		helpData.setPageId( "ldap" );
		
		return helpData;
	}

	/**
	 * Get a list of the ldap configs whose ldap guid attribute has changed.
	 */
	private String[] getListOfLdapConfigsToSyncGuid( GwtLdapConfig ldapConfig )
	{
		ArrayList<GwtLdapConnectionConfig> listOfLdapServers;
		ArrayList<String> listOfLdapServerUrls;
		
		if ( ldapConfig == null )
			return null;
		
		listOfLdapServers = ldapConfig.getListOfLdapConnections();
		if ( listOfLdapServers == null || listOfLdapServers.size() == 0 )
			return null;
		
		listOfLdapServerUrls= new ArrayList<String>();
		
		for ( GwtLdapConnectionConfig nextLdapServer : listOfLdapServers )
		{
			String id;
			
			id = nextLdapServer.getId();
			
			// Is this an existing ldap config?
			if ( id != null && id.length() > 0 )
			{
				String serverUrl;
				String origGuid;
				String newGuid;
				boolean changed;
				
				// Yes
				serverUrl = nextLdapServer.getServerUrl();
				origGuid = nextLdapServer.getOrigLdapGuidAttribute();
				newGuid = nextLdapServer.getLdapGuidAttribute();
				changed = false;

				if ( origGuid != null && origGuid.equalsIgnoreCase( newGuid ) == false )
					changed = true;
				else if ( newGuid != null && newGuid.equalsIgnoreCase( origGuid ) == false )
					changed = true;
				
				if ( changed )
					listOfLdapServerUrls.add( serverUrl );
			}
		}
		
		if ( listOfLdapServerUrls.size() == 0 )
			return null;
		
		return listOfLdapServerUrls.toArray( new String[listOfLdapServerUrls.size()] );
	}
	
	
	/**
	 * Issue an rpc request to get the ldap configuration data from the server.
	 */
	private void getLdapConfigurationFromServer(
		final boolean startLdapSync,
		final boolean syncAll,
		final GwtLdapSyncMode syncMode,
		final String[] listOfLdapConfigsToSyncGuid )
	{
		GetLdapConfigCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		showStatusMsg( m_messages.editLdapConfigDlg_ReadingLdapConfig() );
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
											t,
											m_messages.rpcFailure_GetLdapConfig() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtLdapConfig )
				{
					final GwtLdapConfig ldapConfig;
					ScheduledCommand cmd;
					
					ldapConfig = (GwtLdapConfig) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							hideStatusMsg();
							init( ldapConfig );

							// Get a list of time zones
							getTimeZonesFromServer( ldapConfig );
							
							// Get the list of locales
							getLocalesFromServer( ldapConfig );
							
							if ( startLdapSync )
								startLdapSync( syncAll, listOfLdapConfigsToSyncGuid, syncMode );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Execute a GWT RPC command to get the ldap configuration
		cmd = new GetLdapConfigCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Issue an rpc request to get a list of locales from the server.
	 */
	private void getLocalesFromServer( final GwtLdapConfig ldapConfig )
	{
		GetLocalesCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		// Have we already retrieved the list of locales?
		if ( m_localesListbox.getItemCount() > 0 )
		{
			// Yes
			return;
		}
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
											t,
											m_messages.rpcFailure_GetLocales() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtLocales )
				{
					final GwtLocales locales;
					ScheduledCommand cmd;
					
					locales = (GwtLocales) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							initLocales( locales, ldapConfig );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Execute a GWT RPC command to get the list of locales
		cmd = new GetLocalesCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Return a list of selected ldap servers.
	 */
	public Set<GwtLdapConnectionConfig> getSelectedLdapServers()
	{
		return m_selectionModel.getSelectedSet();
	}
	
	/**
	 * 
	 */
	private String getSelectedLocale()
	{
		int selectedIndex;
		String localeId;
		
		selectedIndex = m_localesListbox.getSelectedIndex();
		if ( selectedIndex == -1 )
			selectedIndex = 0;
		
		localeId = m_localesListbox.getValue( selectedIndex );
		
		return localeId;
	}
	
	/**
	 * 
	 */
	private String getSelectedTimeZone()
	{
		int selectedIndex;
		String timeZoneId;
		
		selectedIndex = m_timeZonesListbox.getSelectedIndex();
		if ( selectedIndex == -1 )
			selectedIndex = 0;
		
		timeZoneId = m_timeZonesListbox.getValue( selectedIndex );
		
		return timeZoneId;
	}
	
	/**
	 * Issue an rpc request to get a list of time zones from the server.
	 */
	private void getTimeZonesFromServer( final GwtLdapConfig ldapConfig )
	{
		GetTimeZonesCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		// Have we already retrieved the list of time zones?
		if ( m_timeZonesListbox.getItemCount() > 0 )
		{
			// Yes
			return;
		}
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
											t,
											m_messages.rpcFailure_GetTimeZones() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null && response.getResponseData() instanceof GwtTimeZones)
				{
					final GwtTimeZones timeZones;
					ScheduledCommand cmd;
					
					timeZones = (GwtTimeZones) response.getResponseData();
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							initTimeZones( timeZones, ldapConfig );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		// Execute a GWT RPC command to get the time zones
		cmd = new GetTimeZonesCmd();
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * This method gets called when the user clicks on the "Preview sync" button
	 */
	private void handleClickOnPreviewSyncButton()
	{
		Object obj;
		boolean startPreview = false;
		GwtLdapConfig ldapConfig;
		
		obj = getDataFromDlg();
		if ( obj == null || (obj instanceof GwtLdapConfig) == false )
			return;
		
		ldapConfig = (GwtLdapConfig) obj;
		
		if ( isDirty() )
		{
			// The ldap config is dirty, tell the user the ldap configuration must be saved first.
			if ( Window.confirm( m_messages.editLdapConfigDlg_LdapConfigMustBeSavedBeforePreviewCanBeStarted() ) )
			{
				Scheduler.ScheduledCommand cmd;
				final String[] listOfLdapConfigsToSyncGuid;

				// Get the list of ldap config ids whose ldap guid attribute changed
				listOfLdapConfigsToSyncGuid = getListOfLdapConfigsToSyncGuid( ldapConfig );
					
				cmd = new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Re-read the ldap configuration and then start an ldap sync preview
						init( true, true, GwtLdapSyncMode.PREVIEW_ONLY, listOfLdapConfigsToSyncGuid );
					}
				};

				// Issue an rpc request to save the ldap configuration
				saveLdapConfiguration( ldapConfig, cmd );
			}
		}
		else
			startPreview = true;

		if ( startPreview )
			startLdapSync( true, null, GwtLdapSyncMode.PREVIEW_ONLY );
	}
	
	/**
	 * This method gets called when the user clicks on the "Sync all" button
	 */
	private void handleClickOnSyncAllButton()
	{
		Object obj;
		boolean startSync = false;
		GwtLdapConfig ldapConfig;
		
		obj = getDataFromDlg();
		if ( obj == null || (obj instanceof GwtLdapConfig) == false )
			return;
		
		ldapConfig = (GwtLdapConfig) obj;
		
		if ( isDirty() )
		{
			// The ldap config is dirty, tell the user the ldap configuration must be saved first.
			if ( Window.confirm( m_messages.editLdapConfigDlg_LdapConfigMustBeSaved() ) )
			{
				Scheduler.ScheduledCommand cmd;
				final String[] listOfLdapConfigsToSyncGuid;

				// Get the list of ldap config ids whose ldap guid attribute changed
				listOfLdapConfigsToSyncGuid = getListOfLdapConfigsToSyncGuid( ldapConfig );
					
				cmd = new ScheduledCommand()
				{
					@Override
					public void execute()
					{
						// Re-read the ldap configuration and then start an ldap sync
						init( true, true, listOfLdapConfigsToSyncGuid );
					}
				};

				// Issue an rpc request to save the ldap configuration
				saveLdapConfiguration( ldapConfig, cmd );
			}
		}
		else
			startSync = true;

		if ( startSync )
			startLdapSync( true, null, GwtLdapSyncMode.PERFORM_SYNC );
	}
	
	/**
	 * 
	 */
	public void init()
	{
		init( false, false, null );
	}
	
	/**
	 * 
	 */
	private void init( boolean startLdapSync, boolean syncAll, String[] listOfLdapConfigsToSyncGuid )
	{
		init( startLdapSync, syncAll, GwtLdapSyncMode.PERFORM_SYNC, listOfLdapConfigsToSyncGuid );
	}
	
	/**
	 * 
	 */
	private void init( boolean startLdapSync, boolean syncAll, GwtLdapSyncMode syncMode, String[] listOfLdapConfigsToSyncGuid )
	{
		// Get the ldap configuration data from the server.
		getLdapConfigurationFromServer( startLdapSync, syncAll, syncMode, listOfLdapConfigsToSyncGuid );
	}
	
	/**
	 * 
	 */
	private void init( GwtLdapConfig ldapConfig )
	{
		m_isDirty = false;

		if ( ldapConfig == null )
			return;
		
		m_ldapConfig = ldapConfig;
		
		addLdapServers( ldapConfig.getListOfLdapConnections() );
		
		m_syncUserProfilesCheckBox.setValue( ldapConfig.getSyncUserProfiles() );
		m_registerUserProfilesAutomaticallyCheckBox.setValue( ldapConfig.getRegisterUserProfilesAutomatically() );
		
		if ( ldapConfig.getDeleteLdapUsers() )
		{
			m_disableUsersRB.setValue( false );
			m_deleteUsersRB.setValue( true );
		}
		else
		{
			m_disableUsersRB.setValue( true );
			m_deleteUsersRB.setValue( false );
		}
		
		m_deleteWorkspaceCheckBox.setValue( ldapConfig.getDeleteUserWorkspace() );

		m_syncGroupProfilesCheckBox.setValue( ldapConfig.getSyncGroupProfiles() );
		m_registerGroupProfilesAutomaticallyCheckBox.setValue( ldapConfig.getRegisterGroupProfilesAutomatically() );
		m_syncGroupMembershipCheckBox.setValue( ldapConfig.getSyncGroupMembership() );
		m_deleteGroupsCheckBox.setValue( ldapConfig.getDeleteNonLdapGroups() );
		
		m_allowLocalLoginCheckBox.setValue( ldapConfig.getAllowLocalLogin() );
		
		if ( m_timeZonesListbox.getItemCount() > 0 )
		{
			String defaultTimeZone;
			
			// Get the default time zone
			defaultTimeZone = ldapConfig.getTimeZone();
			GwtClientHelper.selectListboxItemByValue( m_timeZonesListbox, defaultTimeZone );
		}
		
		if ( m_localesListbox.getItemCount() > 0 )
		{
			String defaultLocale;
			
			defaultLocale = ldapConfig.getLocale();
			GwtClientHelper.selectListboxItemByValue( m_localesListbox, defaultLocale );
		}
		
		m_scheduleWidget.init( ldapConfig.getSchedule() );
		
		danceDlg();
	}
	
	/**
	 * 
	 */
	private void initLocales( GwtLocales locales, GwtLdapConfig ldapConfig )
	{
		TreeMap<String,String> listOfLocales;
		String defaultLocale;
		
		if ( locales == null )
			return;
		
		listOfLocales = locales.getListOfLocales();
		if ( listOfLocales == null )
			return;
		
		for ( Map.Entry<String,String> mapEntry : listOfLocales.entrySet() )
		{
			String localeDisplayName;
			String localeId;
			
			// Get the name of the next locale.
			localeDisplayName = mapEntry.getKey();
			
			// Get the id of the next locale.
			localeId = mapEntry.getValue();

			m_localesListbox.addItem( localeDisplayName, localeId );
		}
		
		// Get the default time zone
		defaultLocale = ldapConfig.getLocale();
		GwtClientHelper.selectListboxItemByValue( m_localesListbox, defaultLocale );
	}
	
	/**
	 * 
	 */
	private void initTimeZones( GwtTimeZones timeZones, GwtLdapConfig ldapConfig )
	{
		TreeMap<String,String> listOfTimeZones;
		String defaultTimeZone;
		
		if ( timeZones == null )
			return;
		
		listOfTimeZones = timeZones.getListOfTimeZones();
		if ( listOfTimeZones == null )
			return;
		
		for ( Map.Entry<String,String> mapEntry : listOfTimeZones.entrySet() )
		{
			String tzId;
			String tzName;
			
			tzId = mapEntry.getValue();
			tzName = mapEntry.getKey();
			
			m_timeZonesListbox.addItem( tzName, tzId );
		}
		
		// Get the default time zone
		defaultTimeZone = ldapConfig.getTimeZone();
		GwtClientHelper.selectListboxItemByValue( m_timeZonesListbox, defaultTimeZone );
	}
	
	/**
	 * 
	 */
	private void invokeAddLdapServerDlg()
	{
		GwtLdapConnectionConfig ldapServer;
		EditSuccessfulHandler editSuccessfulHandler;
		
		ldapServer = new GwtLdapConnectionConfig();
		ldapServer.setUserAttributeMappings( m_ldapConfig.getDefaultUserAttributeMappings() );
		
		editSuccessfulHandler = new EditSuccessfulHandler()
		{
			@Override
			public boolean editSuccessful( Object obj )
			{
				if ( obj instanceof GwtLdapConnectionConfig )
				{
					m_isDirty = true;
					
					// Add the new ldap server to our list
					m_listOfLdapServers.add( (GwtLdapConnectionConfig) obj );
					
					// Tell the table how many ldap servers we have.
					m_ldapServersTable.setRowCount( m_listOfLdapServers.size(), true );
					
					m_dataProvider.refresh();
				}
				
				return true;
			}
		};
		
		invokeModifyLdapServerDlg( ldapServer, true, editSuccessfulHandler );
	}
	
	/**
	 * 
	 */
	private void invokeLdapSyncResultsDlg( final boolean clearResults, final GwtLdapSyncMode syncMode )
	{
		// Have we created the dialog yet?
		if ( m_ldapSyncResultsDlg == null )
		{
			int x;
			int y;

			// No, create it
			x = m_ldapServersTable.getAbsoluteLeft();
			y = m_ldapServersTable.getAbsoluteTop() - 50;

			// Create the dialog.
			LdapSyncResultsDlg.createDlg(
									true,
									false,
									x,
									y,
									new LdapSyncResultsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final LdapSyncResultsDlg lsrDlg )
				{
					ScheduledCommand cmd;
					
					m_ldapSyncResultsDlg = lsrDlg;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeLdapSyncResultsDlg( clearResults, syncMode );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			ScheduledCommand cmd;
			
			cmd = new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Run an async cmd to show the dialog.
					LdapSyncResultsDlg.initAndShow(
												m_ldapSyncResultsDlg,
												m_listOfLdapServers,
												m_ldapSyncId,
												clearResults,
												syncMode,
												null );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * 
	 */
	private void invokeModifyExistingLdapServerDlg( GwtLdapConnectionConfig ldapServer )
	{
		EditSuccessfulHandler editSuccessfulHandler;
		
		editSuccessfulHandler = new EditSuccessfulHandler()
		{
			@Override
			public boolean editSuccessful( Object obj )
			{
				if ( obj instanceof GwtLdapConnectionConfig )
				{
					int index;
					
					index = m_listOfLdapServers.indexOf( obj );
					if ( index != -1 )
					{
						m_dataProvider.getList().set( index, (GwtLdapConnectionConfig) obj );
					}
				}
				
				return true;
			}
		};
		
		invokeModifyLdapServerDlg( ldapServer, false, editSuccessfulHandler );
	}
	
	/**
	 * 
	 */
	private void invokeModifyLdapServerDlg(
			final GwtLdapConnectionConfig ldapServer,
			final boolean newConfig,
			final EditSuccessfulHandler editSuccessfulHandler )
	{
		// Have we created the dialog yet?
		if ( m_editLdapServerDlg == null )
		{
			int x;
			int y;
			
			// No, create it.
			x = m_ldapServersTable.getAbsoluteLeft();
			y = m_ldapServersTable.getAbsoluteTop();
			
			// Create the dialog.
			EditLdapServerConfigDlg.createDlg(
											false,
											true,
											x,
											y,
											null,
											new EditLdapServerConfigDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( EditLdapServerConfigDlg elscDlg )
				{
					ScheduledCommand cmd;
					
					m_editLdapServerDlg = elscDlg;

					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeModifyLdapServerDlg( ldapServer, newConfig, editSuccessfulHandler );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			// Run an async cmd to show the dialog.
			EditLdapServerConfigDlg.initAndShow(
											m_editLdapServerDlg,
											ldapServer,
											newConfig,
											m_ldapConfig.getDefaultUserFilter(),
											m_ldapConfig.getDefaultGroupFilter(),
											editSuccessfulHandler,
											null );
		}
	}

	/**
	 * Return true if anything in the ldap configuration has changed.
	 */
	private boolean isDirty()
	{
		// Has anything in the base configuration changed?
		if ( m_isDirty == true )
			return true;
		
		// Check the user configuration for changes
		{
			if ( m_registerUserProfilesAutomaticallyCheckBox.getValue() != m_ldapConfig.getRegisterUserProfilesAutomatically() )
				return true;
			
			if ( m_syncUserProfilesCheckBox.getValue() != m_ldapConfig.getSyncUserProfiles() )
				return true;
			
			if ( m_deleteUsersRB.getValue() != m_ldapConfig.getDeleteLdapUsers() )
				return true;
			
			if ( m_deleteWorkspaceCheckBox.getValue() != m_ldapConfig.getDeleteUserWorkspace() )
				return true;
			
			if ( GwtClientHelper.areStringsEqual( getSelectedTimeZone(), m_ldapConfig.getTimeZone() ) == false )
				return true;
			
			if ( GwtClientHelper.areStringsEqual( getSelectedLocale(), m_ldapConfig.getLocale() ) == false )
				return true;
		}
		
		// Check the group configuration for changes
		{
			if ( m_registerGroupProfilesAutomaticallyCheckBox.getValue() != m_ldapConfig.getRegisterGroupProfilesAutomatically() )
				return true;
			
			if ( m_syncGroupProfilesCheckBox.getValue() != m_ldapConfig.getSyncGroupProfiles() )
				return true;

			if ( m_syncGroupMembershipCheckBox.getValue() != m_ldapConfig.getSyncGroupMembership() )
				return true;

			if ( m_deleteGroupsCheckBox.getValue() != m_ldapConfig.getDeleteNonLdapGroups() )
				return true;
		}

		// Check the ldap server configuration for changes
		if ( m_listOfLdapServers != null )
		{
			for ( GwtLdapConnectionConfig nextLdapServer : m_listOfLdapServers )
			{
				if ( nextLdapServer.isDirty() )
					return true;
			}
		}
		
		// If we get here, nothing has changed.
		return false;
	}
	
	/**
	 * Return true if an ldap sync is currently in progress.
	 */
	private boolean isLdapSyncInProgress()
	{
		if ( m_ldapSyncId != null && m_ldapSyncId.length() > 0 )
			return true;
		
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
	 * Called to invoke the "ldap sync results" dialog
	 */
	@Override
	public void onInvokeLdapSyncResultsDlg( InvokeLdapSyncResultsDlgEvent event )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				invokeLdapSyncResultsDlg( false, m_lastSyncMode );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Called when the ldap sync status changes
	 */
	@Override
	public void onLdapSyncStatusChanged( LdapSyncStatusEvent event )
	{
		if ( event != null )
		{
			if ( event.getLdapSyncStatus() == null )
				return;
			
			m_ldapSyncStatus = event.getLdapSyncStatus();
			switch ( m_ldapSyncStatus )
			{
			case STATUS_ABORTED_BY_ERROR:
			case STATUS_COMPLETED:
				m_ldapSyncId = null;
				if ( m_listOfLdapServers != null && m_listOfLdapServers.size() > 0 )
				{
					for ( GwtLdapConnectionConfig nextLdapServer : m_listOfLdapServers )
					{
						nextLdapServer.setLdapSyncStatus( m_ldapSyncStatus );
					}
				}
				m_dataProvider.refresh();
				break;

			case STATUS_SYNC_ALREADY_IN_PROGRESS:
				m_ldapSyncId = null;
				break;
				
			case STATUS_IN_PROGRESS:
			case STATUS_STOP_COLLECTING_RESULTS:
				break;
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
	 * Issue an rpc request to save the ldap configuration.
	 */
	private void saveLdapConfiguration(
		GwtLdapConfig ldapConfig,
		final Scheduler.ScheduledCommand schedCmd )
	{
		AsyncCallback<VibeRpcResponse> callback;
		SaveLdapConfigCmd cmd;

		showStatusMsg( m_messages.editLdapConfigDlg_SavingLdapConfig() );

		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											m_messages.rpcFailure_SaveLdapConfig() );
				
				hideStatusMsg();
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				hideStatusMsg();
				
				if ( response.getResponseData() != null &&
					 response.getResponseData() instanceof SaveLdapConfigRpcResponseData )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							// Mark the ldap configuration as not dirty.
							m_isDirty = false;
							if ( m_listOfLdapServers != null )
							{
								for ( GwtLdapConnectionConfig nextLdapServer : m_listOfLdapServers )
								{
									nextLdapServer.setIsDirty( false );
									nextLdapServer.setOrigLdapGuidAttribute( nextLdapServer.getLdapGuidAttribute() );
								}
							}
							
							// Execute the command we were passed.
							if ( schedCmd != null )
								Scheduler.get().scheduleDeferred( schedCmd );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		}; 

		// Execute a GWT RPC command to save the ldap configuration
		cmd = new SaveLdapConfigCmd();
		cmd.setLdapConfig( ldapConfig );
		
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Show the current ldap sync results
	 */
	private void showLdapSyncResults()
	{
		// Invoke the LDAP Sync Results dialog
		invokeLdapSyncResultsDlg( false, m_lastSyncMode );
	}
	
	/**
	 * issue an rpc request to sync the selected ldap servers.
	 */
	private void startLdapSync(
		boolean syncAll,
		String[] listOfLdapConfigsToSyncGuid,
		GwtLdapSyncMode syncMode )
	{
		AsyncCallback<VibeRpcResponse> callback;
		StartLdapSyncCmd cmd;

		m_lastSyncMode = syncMode;
		
		// Is an ldap sync currently in progress?
		if ( isLdapSyncInProgress() )
		{
			// Yes, tell the user they can't start another.
			Window.alert( m_messages.editLdapConfigDlg_LdapSyncInProgressCantStartAnother() );
			
			// Invoke the LDAP Sync Results dialog
			invokeLdapSyncResultsDlg( false, syncMode );
			
			return;
		}
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											m_messages.rpcFailure_StartLdapSync() );
				
				m_ldapSyncId = null;
				hideStatusMsg();
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				if ( response.getResponseData() != null &&
					 response.getResponseData() instanceof StartLdapSyncRpcResponseData )
				{
					StartLdapSyncRpcResponseData responseData;
					GwtLdapSyncResults ldapSyncResults;

					responseData = (StartLdapSyncRpcResponseData) response.getResponseData();
					ldapSyncResults = responseData.getLdapSyncResults();
					if ( ldapSyncResults != null )
					{
						GwtLdapSyncStatus status;
						
						status = ldapSyncResults.getSyncStatus();
	
						if ( status != null )
						{
							LdapSyncStatusEvent event;

							// Fire an event that lets everyone know the sync completed.
							event = new LdapSyncStatusEvent( status );
							GwtTeaming.fireEvent( event );
						}
					}
				}
			}
		}; 

		cmd = new StartLdapSyncCmd();
		cmd.setSyncAll( syncAll );
		cmd.setListOfLdapConfigsToSyncGuid( listOfLdapConfigsToSyncGuid );
		cmd.setSyncMode( syncMode );
		m_ldapSyncId = String.valueOf( Math.random() );
		cmd.setSyncId( m_ldapSyncId );
		
		// Get the list of selected ldap servers
		{
			Set<GwtLdapConnectionConfig> selectedServers = null;
			
			if ( syncAll == false )
				selectedServers = getSelectedLdapServers();
			
			if ( selectedServers != null && selectedServers.size() > 0 )
			{
				Iterator<GwtLdapConnectionConfig> serverIterator;

				// Get a list of all the selected ldap server urls
				serverIterator = selectedServers.iterator();
				while ( serverIterator.hasNext() )
				{
					GwtLdapConnectionConfig nextServer;
					
					nextServer = serverIterator.next();
					cmd.addLdapServerToSync( nextServer );
					
					nextServer.setLdapSyncStatus( GwtLdapSyncStatus.STATUS_IN_PROGRESS );
				}
			}
			else if ( m_listOfLdapServers != null && m_listOfLdapServers.size() > 0 )
			{
				for ( GwtLdapConnectionConfig nextLdapServer : m_listOfLdapServers )
				{
					nextLdapServer.setLdapSyncStatus( GwtLdapSyncStatus.STATUS_IN_PROGRESS );
				}
			}

			// Refresh the list of ldap servers so the syncing image is displayed.
			m_dataProvider.refresh();
		}
		
		// Execute a GWT RPC command to start an ldap sync
		GwtClientHelper.executeCommand( cmd, callback );

		// Invoke the LDAP Sync Results dialog
		invokeLdapSyncResultsDlg( true, syncMode );
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
		final EditLdapConfigDlgClient elcDlgClient )
	{
		GWT.runAsync( EditLdapConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapConfigDlg() );
				if ( elcDlgClient != null )
				{
					elcDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				new EditLdapConfigDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height,
					elcDlgClient );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final EditLdapConfigDlg dlg,
		final int width,
		final int height,
		final int left,
		final int top,
		final EditLdapConfigDlgClient elcDlgClient )
	{
		GWT.runAsync( EditLdapConfigDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapConfigDlg() );
				if ( elcDlgClient != null )
				{
					elcDlgClient.onUnavailable();
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
