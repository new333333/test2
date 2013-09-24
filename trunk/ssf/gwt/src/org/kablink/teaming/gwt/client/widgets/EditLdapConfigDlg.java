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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig.GwtLdapSyncStatus;
import org.kablink.teaming.gwt.client.GwtLocales;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTimeZones;
import org.kablink.teaming.gwt.client.datatable.LdapServerUrlCell;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetLocalesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTimeZonesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveLdapConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveLdapConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StartLdapSyncCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditLdapServerConfigDlg.EditLdapServerConfigDlgClient;

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


/**
 * 
 * @author jwootton
 *
 */
public class EditLdapConfigDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private GwtLdapConfig m_ldapConfig;
	
	private CellTable<GwtLdapConnectionConfig> m_ldapServersTable;
    private MultiSelectionModel<GwtLdapConnectionConfig> m_selectionModel;
	private ListDataProvider<GwtLdapConnectionConfig> m_dataProvider;
	private VibeSimplePager m_pager;
	private List<GwtLdapConnectionConfig> m_listOfLdapServers;

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
	
	private boolean m_isDirty;
	
	
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
	private EditLdapConfigDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.OkCancel );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().editLdapConfigDlg_Header(), this, null, null );
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
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		TabPanel tabPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		tabPanel = new TabPanel();
		tabPanel.addStyleName( "vibe-tabPanel" );

		mainPanel.add( tabPanel );

		// Create a panel to hold the list of ldap servers
		{
			Panel serversPanel;
			
			serversPanel = createLdapServersPanel( messages );
			tabPanel.add( serversPanel, messages.editLdapConfigDlg_LdapServersTab() );
		}
		
		// Create a panel to hold the user configuration
		{
			Panel userPanel;
			
			userPanel = createUserConfigPanel( messages );
			tabPanel.add( userPanel, messages.editLdapConfigDlg_UsersTab() );
		}
		
		// Create a panel to hold the group configuration
		{
			Panel groupPanel;
			
			groupPanel = createGroupConfigPanel( messages );
			tabPanel.add( groupPanel, messages.editLdapConfigDlg_GroupsTab() );
		}

		// Add controls dealing with sync schedule
		{
			Panel schedulePanel;
			
			schedulePanel = createSchedulePanel( messages );
			tabPanel.add( schedulePanel, messages.editLdapConfigDlg_ScheduleTab() );
		}
		
		// Add controls dealing with local user accounts
		{
			Panel localPanel;
			
			localPanel = createLocalAccountsPanel( messages );
			tabPanel.add( localPanel, messages.editLdapConfigDlg_LocalUserAccountsTab() );
		}
		
		tabPanel.selectTab( 0 );
		
		return mainPanel;
	}

	/**
	 * 
	 */
	private Panel createGroupConfigPanel( GwtTeamingMessages messages )
	{
		FlowPanel groupPanel;
		FlowPanel tmpPanel;
		
		groupPanel = new FlowPanel();
		
		tmpPanel = new FlowPanel();
		m_registerGroupProfilesAutomaticallyCheckBox = new CheckBox( messages.editLdapConfigDlg_RegisterGroupProfilesAutomatically() );
		tmpPanel.add( m_registerGroupProfilesAutomaticallyCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_syncGroupProfilesCheckBox = new CheckBox( messages.editLdapConfigDlg_SyncGroupProfiles() );
		tmpPanel.add( m_syncGroupProfilesCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_syncGroupMembershipCheckBox = new CheckBox( messages.editLdapConfigDlg_SyncGroupMembership() );
		tmpPanel.add( m_syncGroupMembershipCheckBox );
		groupPanel.add( tmpPanel );
		
		tmpPanel = new FlowPanel();
		m_deleteGroupsCheckBox = new CheckBox( messages.editLdapConfigDlg_DeleteGroupsLabel() );
		tmpPanel.add( m_deleteGroupsCheckBox );
		groupPanel.add( tmpPanel );
		
		return groupPanel;
	}
	
	/**
	 * 
	 */
	private Panel createLdapServersPanel( GwtTeamingMessages messages )
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
			label = new InlineLabel( messages.editLdapConfigDlg_AddLdapServerLabel() );
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
			label = new InlineLabel( messages.editLdapConfigDlg_DeleteLdapServerLabel() );
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
			
			// Add a "Sync" button.
			label = new InlineLabel( messages.editLdapConfigDlg_SyncLdapServerLabel() );
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
							startLdapSync();
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
			noServersLabel = new InlineLabel( messages.editLdapConfigDlg_NoLdapServersLabel() );
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
			m_ldapServersTable.addColumn( serverUrlCol, messages.editLdapConfigDlg_ServerUrlCol() );
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
			m_ldapServersTable.addColumn( userDNCol, messages.editLdapConfigDlg_UserDNCol() );
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
	private Panel createLocalAccountsPanel( GwtTeamingMessages messages )
	{
		FlowPanel localPanel;
		FlowPanel tmpPanel;
		
		localPanel = new FlowPanel();
		
		tmpPanel = new FlowPanel();
		m_allowLocalLoginCheckBox = new CheckBox( messages.editLdapConfigDlg_AllowLocalLoginLabel() );
		tmpPanel.add( m_allowLocalLoginCheckBox );
		localPanel.add( tmpPanel );
		
		return localPanel;
	}

	/**
	 * 
	 */
	private Panel createSchedulePanel( GwtTeamingMessages messages )
	{
		FlowPanel schedulePanel;
		
		schedulePanel = new FlowPanel();
		
		m_scheduleWidget = new ScheduleWidget( messages.editLdapConfigDlg_EnableSyncScheduleLabel() );
		schedulePanel.add( m_scheduleWidget );
		
		return schedulePanel;
	}

	/**
	 * 
	 */
	private Panel createUserConfigPanel( GwtTeamingMessages messages )
	{
		FlowPanel userPanel;
		Label label;
		
		userPanel = new FlowPanel();
		
		// Add the controls dealing with syncing users
		{
			FlowPanel tmpPanel;

			tmpPanel = new FlowPanel();
			m_registerUserProfilesAutomaticallyCheckBox = new CheckBox( messages.editLdapConfigDlg_RegisterUserProfilesAutomatically() );
			tmpPanel.add( m_registerUserProfilesAutomaticallyCheckBox );
			userPanel.add( tmpPanel );
	
			tmpPanel = new FlowPanel();
			m_syncUserProfilesCheckBox = new CheckBox( messages.editLdapConfigDlg_SyncUserProfiles() );
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
								Window.alert( GwtTeaming.getMessages().editLdapConfigDlg_DeleteUsersWarning() );
							}
							
							danceDlg();
						}
					};
					
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "margintop3" );
			m_disableUsersRB = new RadioButton( "notInLdap", messages.editLdapConfigDlg_DisableUserLabel() );
			m_disableUsersRB.addClickHandler( clickHandler );
			tmpPanel.add( m_disableUsersRB );
			userPanel.add( tmpPanel );
			
			tmpPanel = new FlowPanel();
			m_deleteUsersRB = new RadioButton( "notInLdap", messages.editLdapConfigDlg_DeleteUserLabel() );
			m_deleteUsersRB.addClickHandler( clickHandler );
			tmpPanel.add( m_deleteUsersRB );
			userPanel.add( tmpPanel );
			
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "marginleft2" );
			m_deleteWorkspaceCheckBox = new CheckBox( messages.editLdapConfigDlg_DeleteWorkspaceLabel() );
			tmpPanel.add( m_deleteWorkspaceCheckBox );
			userPanel.add( tmpPanel );
		}
		
		// Add the controls dealing time zone
		{
			label = new Label( messages.editLdapConfigDlg_DefaultTimeZoneLabel() );
			label.addStyleName( "margintop3" );
			userPanel.add( label );
			
			m_timeZonesListbox = new ListBox( false );
			m_timeZonesListbox.setVisibleItemCount( 1 );
			m_timeZonesListbox.addStyleName( "marginleft1" );
			userPanel.add( m_timeZonesListbox );
		}
		
		// Add the controls dealing with locale
		{
			label = new Label( messages.editLdapConfigDlg_DefaultLocaleLabel() );
			label.addStyleName( "margintop3" );
			userPanel.add( label );
			
			m_localesListbox = new ListBox( false );
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
			msg = GwtTeaming.getMessages().editLdapConfigDlg_ConfirmDelete( serverUrls );
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
			Window.alert( GwtTeaming.getMessages().editLdapConfigDlg_SelectLdapServersToDelete() );
		}
	}
	
	/**
	 * This gets called when the user presses ok.  Issue an rpc request to save the
	 * "allow adhoc folders" setting
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		Scheduler.ScheduledCommand cmd;
		
		if ( (obj instanceof GwtLdapConfig) == false )
			return false;
		
		cmd = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				hide();
			}
		};
		
		// Issue an rpc request to save the ldap configuration
		saveLdapConfiguration( (GwtLdapConfig) obj, cmd );

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
	 * Issue an rpc request to get the ldap configuration data from the server.
	 */
	private void getLdapConfigurationFromServer()
	{
		GetLdapConfigCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		showStatusMsg( GwtTeaming.getMessages().editLdapConfigDlg_ReadingLdapConfig() );
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				hideStatusMsg();
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_GetLdapConfig() );
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
											GwtTeaming.getMessages().rpcFailure_GetLocales() );
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
											GwtTeaming.getMessages().rpcFailure_GetTimeZones() );
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
	 * 
	 */
	public void init()
	{
		// Get the ldap configuration data from the server.
		getLdapConfigurationFromServer();
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
		
		invokeModifyLdapServerDlg( ldapServer, editSuccessfulHandler );
	}
	
	/**
	 * 
	 */
	private void invokeLdapSyncResultsDlg()
	{
		Window.alert( "Finish invokeLdapSyncResultsDlg()" );
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
		
		invokeModifyLdapServerDlg( ldapServer, editSuccessfulHandler );
	}
	
	/**
	 * 
	 */
	private void invokeModifyLdapServerDlg(
		final GwtLdapConnectionConfig ldapServer,
		final EditSuccessfulHandler editSuccessfulHandler )
	{
		if ( m_editLdapServerDlg == null )
		{
			int x;
			int y;
			
			x = m_ldapServersTable.getAbsoluteLeft();
			y = m_ldapServersTable.getAbsoluteTop();
			
			EditLdapServerConfigDlg.createAsync(
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
				public void onSuccess( final EditLdapServerConfigDlg elscDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_editLdapServerDlg = elscDlg;
							
							invokeModifyLdapServerDlg( ldapServer, editSuccessfulHandler );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_editLdapServerDlg.init(
									ldapServer,
									m_ldapConfig.getDefaultUserFilter(),
									m_ldapConfig.getDefaultGroupFilter() );
			m_editLdapServerDlg.initHandlers( editSuccessfulHandler, null );
			m_editLdapServerDlg.show();
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
		//!!!
		return false;
	}
	
	/**
	 * Issue an rpc request to save the ldap configuration.
	 */
	private void saveLdapConfiguration( GwtLdapConfig ldapConfig, final Scheduler.ScheduledCommand schedCmd )
	{
		AsyncCallback<VibeRpcResponse> callback;
		SaveLdapConfigCmd cmd;

		showStatusMsg( GwtTeaming.getMessages().editLdapConfigDlg_SavingLdapConfig() );

		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_SaveLdapConfig() );
				
				hideStatusMsg();
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				hideStatusMsg();
				
				if ( response.getResponseData() != null &&
					 response.getResponseData() instanceof SaveLdapConfigRpcResponseData )
				{
					// Mark the ldap configuration as not dirty.
					m_isDirty = false;
					if ( m_listOfLdapServers != null )
					{
						for ( GwtLdapConnectionConfig nextLdapServer : m_listOfLdapServers )
						{
							nextLdapServer.setIsDirty( false );
						}
					}
					
					// Execute the command we were passed.
					if ( schedCmd != null )
						Scheduler.get().scheduleDeferred( schedCmd );
				}
			}
		}; 

		// Execute a GWT RPC command to save the ldap configuration
		cmd = new SaveLdapConfigCmd();
		cmd.setLdapConfig( ldapConfig );
		
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Issue an rpc request to save the ldap configuration and after we receive the response to the save
	 * issue an rpc request to sync the selected ldap servers.
	 */
	private void startLdapSync()
	{
		GwtTeamingMessages messages;
		boolean startSync = false;

		messages = GwtTeaming.getMessages();
		
		// Are there any ldap servers to sync?
		if ( m_listOfLdapServers == null || m_listOfLdapServers.size() == 0 )
		{
			// No
			Window.alert( messages.editLdapConfigDlg_NoLdapServersToSync() );
			return;
		}
		
		// Is an ldap sync currently in progress?
		if ( isLdapSyncInProgress() )
		{
			// Yes, tell the user they can't start another.
			Window.alert( messages.editLdapConfigDlg_LdapSyncInProgressCantStartAnother() );
			
			// Invoke the LDAP Sync Results dialog
			invokeLdapSyncResultsDlg();
			
			return;
		}
		
		// Has the ldap configuration changed?
		if ( isDirty() )
		{
			// Yes, tell the user the ldap configuration must be saved first.
			if ( Window.confirm( messages.editLdapConfigDlg_LdapConfigMustBeSaved() ) )
			{
				Object obj;
				
				obj = getDataFromDlg();
				if ( obj != null && obj instanceof GwtLdapConfig )
				{
					GwtLdapConfig ldapConfig;
					Scheduler.ScheduledCommand cmd;

					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							boolean newLdapServer = false;
							
							// Do we have any ldap servers that were newly added?
							if ( m_listOfLdapServers != null )
							{
								for ( GwtLdapConnectionConfig nextLdapServer : m_listOfLdapServers )
								{
									String id;
									
									// Does the ldap server config have an id?
									id = nextLdapServer.getId();
									if ( id == null || id.length() == 0 )
									{
										// No
										newLdapServer = true;
										break;
									}
								}
							}
							
							if ( newLdapServer == false )
								startLdapSync();
							else
							{
								// Re-read the ldap configuration.
								init();
							}
						}
					};

					ldapConfig = (GwtLdapConfig) obj;
					
					// Issue an rpc request to save the ldap configuration
					saveLdapConfiguration( ldapConfig, cmd );
				}
			}
		}
		else
			startSync = true;
		
		if ( startSync )
		{
			AsyncCallback<VibeRpcResponse> callback;
			StartLdapSyncCmd cmd;

			callback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_StartLdapSync() );
					
					hideStatusMsg();
				}
				
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					if ( response.getResponseData() != null &&
						 response.getResponseData() instanceof SaveLdapConfigRpcResponseData )
					{
						//!!! Finish
					}
				}
			}; 

			cmd = new StartLdapSyncCmd();
			
			// Get the list of selected ldap servers
			{
				Set<GwtLdapConnectionConfig> selectedServers;
				
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
						
						nextServer.setSyncStatus( GwtLdapSyncStatus.SYNC_IN_PROGRESS );
					}
				}
				else if ( m_listOfLdapServers != null && m_listOfLdapServers.size() > 0 )
				{
					for ( GwtLdapConnectionConfig nextLdapServer : m_listOfLdapServers )
					{
						nextLdapServer.setSyncStatus( GwtLdapSyncStatus.SYNC_IN_PROGRESS );
					}
				}

				// Refresh the list of ldap servers so the syncing image is displayed.
				m_dataProvider.refresh();
			}
			
			// Execute a GWT RPC command to start an ldap sync
			GwtClientHelper.executeCommand( cmd, callback );
		}
	}
	
	/**
	 * Loads the EditLdapConfigDlg split point and returns an instance
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
				EditLdapConfigDlg elcDlg;
				
				elcDlg = new EditLdapConfigDlg(
											autoHide,
											modal,
											left,
											top,
											width,
											height );
				elcDlgClient.onSuccess( elcDlg );
			}
		});
	}
}
