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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtJitsNetFolderConfig;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtNetFolderSyncScheduleConfig;
import org.kablink.teaming.gwt.client.GwtNetFolderSyncScheduleConfig.NetFolderSyncScheduleOption;
import org.kablink.teaming.gwt.client.GwtRole;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.NetFolderDataSyncSettings;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderModifiedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderRootCreatedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CreateNetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderRootsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderRootsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyNetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.ModifyNetFolderRootDlgClient;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.NetFolderRootType;
import org.kablink.teaming.gwt.client.widgets.NetFolderSelectPrincipalsWidget.NetFolderSelectPrincipalsWidgetClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This dialog can be used to create or modify a Net Folder.
 * 
 * @author jwootton@novell.com
 */
public class ModifyNetFolderDlg extends DlgBox
	implements EditSuccessfulHandler, KeyPressHandler,
		// Event handlers implemented by this class.
		NetFolderRootCreatedEvent.Handler
{
	private TabPanel m_tabPanel;
	private NetFolder m_netFolder;	// If we are modifying a net folder this is the net folder.
	private TextBox m_nameTxtBox;
	private TextBox m_relativePathTxtBox;
	private ListBox m_netFolderRootsListbox;
	private RadioButton m_useNFServerIndexContentOptionRB;
	private RadioButton m_useNFIndexContentOptionRB;
	private CheckBox m_indexContentCkbox;
	private RadioButton m_useJitsSettingsFromNFServerRB;
	private RadioButton m_useJitsSettingsFromNetFolderRB;
	private CheckBox m_jitsEnabledCkbox;
	private TextBox m_jitsResultsMaxAge;
	private TextBox m_jitsAclMaxAge;
	private InlineLabel m_noNetFolderRootsLabel;
	private RadioButton m_useNFServerScheduleRB;
	private RadioButton m_useNFScheduleRB;
	private ScheduleWidget m_scheduleWidget;
	private NetFolderSelectPrincipalsWidget m_selectPrincipalsWidget;
	private FlowPanel m_inProgressPanel;
	private CheckBox m_allowDesktopAppToSync;
	private CheckBox m_allowMobileAppsToSync;
	private RadioButton m_useNFServerSyncOptionRB;
	private RadioButton m_useNFSyncOptionRB;
	private CheckBox m_fullSyncDirOnlyCB = null;
	private RadioButton m_useNFServerAllowDesktopAppToTriggerSyncRB;
	private RadioButton m_useNFAllowDesktopAppToTriggerSyncRB;
	private CheckBox m_allowDesktopAppToTriggerSyncCB;
	private Panel m_rightsPanel;
	private Panel m_syncPanel;
	private ModifyNetFolderRootDlg m_modifyNetFolderRootDlg;
	private List<NetFolderRoot> m_listOfNetFolderRoots;
	private List<HandlerRegistration> m_registeredEventHandlers;

	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
		TeamingEvents.NET_FOLDER_ROOT_CREATED,
	};

	
	/**
	 * Callback interface to interact with the "modify net folder" dialog
	 * asynchronously after it loads. 
	 */
	public interface ModifyNetFolderDlgClient
	{
		void onSuccess( ModifyNetFolderDlg mnfDlg );
		void onUnavailable();
	}

	/**
	 * 
	 */
	private ModifyNetFolderDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( "", this, null, null ); 
	}

	
	/**
	 * Add the given list of Net Folder Roots to the dialog
	 */
	private void addNetFolderRoots( List<NetFolderRoot> listOfNetFolderRoots )
	{
		m_listOfNetFolderRoots = listOfNetFolderRoots;
		m_netFolderRootsListbox.clear();
		
		if ( listOfNetFolderRoots != null )
		{
			for ( NetFolderRoot nextRoot : listOfNetFolderRoots )
			{
				m_netFolderRootsListbox.addItem( nextRoot.getName(), nextRoot.getName() );
			}
		}
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_tabPanel = new TabPanel();
		m_tabPanel.addStyleName( "vibe-tabPanel" );

		mainPanel.add( m_tabPanel );
		
		// Create the panel that will hold the controls for the net folder configuration
		{
			Panel configPanel;
			
			configPanel = createConfigPanel();
			
			m_tabPanel.add( configPanel, messages.modifyNetFolderDlg_ConfigTab() );
		}
		
		// Create the panel that will hold the controls for access rights
		{
			m_rightsPanel = createRightsPanel();
			m_tabPanel.add( m_rightsPanel, messages.modifyNetFolderDlg_RightsTab() );
			m_tabPanel.addSelectionHandler( new SelectionHandler<Integer>()
			{
				@Override
				public void onSelection( SelectionEvent<Integer> event )
				{
					int tabId;

					tabId = event.getSelectedItem();
					if ( tabId == 1 )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								if ( m_selectPrincipalsWidget != null )
									m_selectPrincipalsWidget.relayout();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			} );
		}
		
		// create the panel that will hold the controls for the schedule
		{
			Panel schedPanel;
			
			schedPanel = createSchedulePanel();
			m_tabPanel.add( schedPanel, messages.modifyNetFolderDlg_ScheduleTab() );
		}
		
		// Create the panel that will hold the controls for allowing data sync
		{
			m_syncPanel = createDataSyncPanel();
			m_tabPanel.add( m_syncPanel, messages.modifyNetFolderDlg_DataSyncTab() );
		}

		return mainPanel;
	}

	/**
	 * Create a panel that holds all the controls for configuring the net folder.
	 */
	private Panel createConfigPanel()
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		final FlexTable table;
		Label label;
		int nextRow;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		
		// Create a table to hold the controls.
		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );

		nextRow = 0;
		
		// Create the controls for "Name"
		{
			label = new InlineLabel( messages.modifyNetFolderDlg_NameLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_nameTxtBox = new TextBox();
			m_nameTxtBox.setVisibleLength( 30 );
			table.setWidget( nextRow, 1, m_nameTxtBox );
			++nextRow;
		}
		
		// Create the controls for "net folder root"
		{
			FlowPanel flowPanel;
			Button createRootBtn;
			
			label = new InlineLabel( messages.modifyNetFolderDlg_NetFolderServerLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );

			// Add the listbox where the user can select the net folder root
			flowPanel = new FlowPanel();
			m_netFolderRootsListbox = new ListBox();
			m_netFolderRootsListbox.setMultipleSelect( false );
			m_netFolderRootsListbox.setVisibleItemCount( 1 );
			m_netFolderRootsListbox.setSelectedIndex( 0 );
			flowPanel.add( m_netFolderRootsListbox );
			
			// Add a label that will be displayed when there are no net folder roots to select.
			m_noNetFolderRootsLabel = new InlineLabel( messages.modifyNetFolderDlg_NoNetFolderServersLabel() );
			flowPanel.add( m_noNetFolderRootsLabel );
			
			// Add "Create net folder root" button
			createRootBtn = new Button( messages.modifyNetFolderDlg_CreateNetFolderServerLabel() );
			createRootBtn.addStyleName( "teamingButton" );
			createRootBtn.addStyleName( "marginleft3" );
			flowPanel.add( createRootBtn );
			createRootBtn.addClickHandler( new ClickHandler()
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
							invokeCreateNetFolderRootDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			} );

			table.setWidget( nextRow, 1, flowPanel );
			++nextRow;
		}
		
		// Create the controls for "relative path"
		{
			label = new InlineLabel( messages.modifyNetFolderDlg_RelativePathLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_relativePathTxtBox = new TextBox();
			m_relativePathTxtBox.setVisibleLength( 50 );
			table.setWidget( nextRow, 1, m_relativePathTxtBox );
			++nextRow;
		}
		
		// Add a "test connection" button
		{
			Button testConnectionBtn;
			
			// Add "Test connection" button
			testConnectionBtn = new Button( messages.modifyNetFolderServerDlg_TestConnectionLabel() );
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
							if ( isConfigurationValid() == true )
								testConnection();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			} );
			
				table.setHTML( nextRow, 0, "" );
				table.setWidget( nextRow, 1, testConnectionBtn );

			// Add a panel that will display "Testing connection..." message
			{
				ImageResource imgResource;
				Image img;
				
				m_inProgressPanel = new FlowPanel();
				m_inProgressPanel.addStyleName( "testConnection_InProgress" );
				m_inProgressPanel.setVisible( false );

				imgResource = GwtTeaming.getImageBundle().spinner16();
				img = new Image( imgResource );
				img.getElement().setAttribute( "align", "absmiddle" );
				m_inProgressPanel.add( img );

				label = new InlineLabel( GwtTeaming.getMessages().testConnection_InProgressLabel() );
				m_inProgressPanel.add( label );
				
				table.setWidget( nextRow, 2, m_inProgressPanel );
			}
			
			++nextRow;
		}
		
		// Add a "Index the content of this net folder" checkbox
		{
			FlowPanel tmpPanel;
			FlowPanel indexPanel;
			FlexCellFormatter cellFormatter;

			indexPanel = new FlowPanel();
			indexPanel.addStyleName( "margintop3" );
			
			m_useNFServerIndexContentOptionRB = new RadioButton( "indexContentOption", messages.modifyNetFolderDlg_UseNetFolderServerIndexContentOptionRbLabel() );
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "margintop3" );
			tmpPanel.add( m_useNFServerIndexContentOptionRB );
			indexPanel.add( tmpPanel );

			m_useNFIndexContentOptionRB = new RadioButton( "indexContentOption", messages.modifyNetFolderDlg_UseNetFolderIndexContentOptionRbLabel() );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_useNFIndexContentOptionRB );
			indexPanel.add( tmpPanel );

			m_indexContentCkbox = new CheckBox( messages.modifyNetFolderDlg_IndexContentLabel() );
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "marginleft3" );
			tmpPanel.add( m_indexContentCkbox );
			indexPanel.add( tmpPanel );
			
			cellFormatter = table.getFlexCellFormatter();
			cellFormatter.setColSpan( nextRow, 0, 2 );

			table.setWidget( nextRow, 0, indexPanel );
			++nextRow;
		}
		
		// Add the controls needed to define Jits settings
		{
			FlowPanel mainJitsPanel;
			FlowPanel subJitsPanel;
			FlowPanel tmpPanel;
			FlexCellFormatter cellFormatter;

			mainJitsPanel = new FlowPanel();
			mainJitsPanel.addStyleName( "margintop3" );

			m_useJitsSettingsFromNFServerRB = new RadioButton( "inheritJitsSettings", messages.modifyNetFolderDlg_UseJitsSettingsFromNetFolderServerRbLabel() );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_useJitsSettingsFromNFServerRB );
			mainJitsPanel.add( tmpPanel );

			m_useJitsSettingsFromNetFolderRB = new RadioButton( "inheritJitsSettings", messages.modifyNetFolderDlg_UseJistsSettingsFromNetFolderRbLabel() );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_useJitsSettingsFromNetFolderRB );
			mainJitsPanel.add( tmpPanel );

			subJitsPanel = new FlowPanel();
			subJitsPanel.addStyleName( "marginleft3" );
			mainJitsPanel.add( subJitsPanel );
			
			m_jitsEnabledCkbox = new CheckBox( messages.modifyNetFolderDlg_EnableJitsLabel() );
			tmpPanel = new FlowPanel();
			tmpPanel.add( m_jitsEnabledCkbox );
			subJitsPanel.add( tmpPanel );
			
			// Add a panel that holds all the max age controls.
			{
				FlowPanel maxAgePanel;
				
				maxAgePanel = new FlowPanel();
				maxAgePanel.addStyleName( "marginleft3" );
				
				// Add the controls for "results max age"
				{
					HorizontalPanel hPanel;
					Label intervalLabel;

					hPanel = new HorizontalPanel();
					hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
					hPanel.setSpacing( 0 );
					
					intervalLabel = new Label( messages.modifyNetFolderDlg_JitsResultsMaxAgeLabel() );
					hPanel.add( intervalLabel );
					
					m_jitsResultsMaxAge = new TextBox();
					m_jitsResultsMaxAge.addKeyPressHandler( this );
					m_jitsResultsMaxAge.setVisibleLength( 3 );
					hPanel.add( m_jitsResultsMaxAge );
					
					intervalLabel = new Label( messages.netFolderGlobalSettingsDlg_SecondsLabel() );
					intervalLabel.addStyleName( "marginleft2px" );
					intervalLabel.addStyleName( "gray3" );
					hPanel.add( intervalLabel );

					maxAgePanel.add( hPanel );
				}

				// Add the controls for "acl max age"
				{
					HorizontalPanel hPanel;
					Label intervalLabel;

					hPanel = new HorizontalPanel();
					hPanel.setVerticalAlignment( HasVerticalAlignment.ALIGN_MIDDLE );
					hPanel.setSpacing( 0 );
					
					intervalLabel = new Label( messages.modifyNetFolderDlg_JitsAclMaxAgeLabel() );
					hPanel.add( intervalLabel );
					
					m_jitsAclMaxAge = new TextBox();
					m_jitsAclMaxAge.addKeyPressHandler( this );
					m_jitsAclMaxAge.setVisibleLength( 3 );
					hPanel.add( m_jitsAclMaxAge );
					
					intervalLabel = new Label( messages.netFolderGlobalSettingsDlg_SecondsLabel() );
					intervalLabel.addStyleName( "marginleft2px" );
					intervalLabel.addStyleName( "gray3" );
					hPanel.add( intervalLabel );

					maxAgePanel.add( hPanel );
				}

				subJitsPanel.add( maxAgePanel );
			}

			cellFormatter = table.getFlexCellFormatter();
			cellFormatter.setColSpan( nextRow, 0, 2 );
			
			table.setWidget( nextRow, 0, mainJitsPanel );
			++nextRow;
		}
		
		mainPanel.add( table );

		return mainPanel;
		
	}
	
	/**
	 * Create the panel that holds the controls for allowing data sync
	 */
	private Panel createDataSyncPanel()
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		FlowPanel ckboxPanel;
		FlowPanel tmpPanel;
		Label label;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();

		label = new Label( messages.modifyNetFolderDlg_AllowDataSyncBy() );
		mainPanel.add( label );

		ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName( "marginleft1" );
		ckboxPanel.addStyleName( "marginbottom2" );
		mainPanel.add( ckboxPanel );
		
		m_allowDesktopAppToSync = new CheckBox( messages.modifyNetFolderDlg_AllowDesktopAppToSyncLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowDesktopAppToSync );
		ckboxPanel.add( tmpPanel );
		
		m_allowMobileAppsToSync = new CheckBox( messages.modifyNetFolderDlg_AllowMobileAppsToSyncLabel() );
		tmpPanel = new FlowPanel();
		tmpPanel.add( m_allowMobileAppsToSync );
		ckboxPanel.add( tmpPanel );
		
		// Hidden as per bug#816823.
		m_allowMobileAppsToSync.setVisible(false);
		
		// Add the radio buttons for selecting which sync option to use.
		if ( GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI() )
		{
			FlowPanel panel;
			
			m_useNFServerSyncOptionRB = new RadioButton( "syncOption", messages.modifyNetFolderDlg_UseNetFolderServerSyncOptionRbLabel() );
			panel = new FlowPanel();
			panel.addStyleName( "margintop3" );
			panel.add( m_useNFServerSyncOptionRB );
			mainPanel.add( panel );
		
			m_useNFSyncOptionRB = new RadioButton( "syncOption", messages.modifyNetFolderDlg_UseNetFolderSyncOptionRbLabel() );
			panel = new FlowPanel();
			panel.add( m_useNFSyncOptionRB );
			mainPanel.add( panel );
			
			m_fullSyncDirOnlyCB = new CheckBox( messages.modifyNetFolderDlg_FullSyncDirOnlyCB() );
			panel = new FlowPanel();
			panel.getElement().getStyle().setMarginLeft( 30, Unit.PX );
			panel.add( m_fullSyncDirOnlyCB );
			mainPanel.add( panel );
		}
		
		// Create the controls for "allow desktop app to trigger initial home folder sync"
		{
			FlowPanel panel;
			
			m_useNFServerAllowDesktopAppToTriggerSyncRB = new RadioButton( "desktopTriggerSync", messages.modifyNetFolderDlg_UseNetFolderServerDesktopAppTriggerSync() );
			panel = new FlowPanel();
			panel.addStyleName( "margintop3" );
			panel.add( m_useNFServerAllowDesktopAppToTriggerSyncRB );
			mainPanel.add( panel );

			m_useNFAllowDesktopAppToTriggerSyncRB = new RadioButton( "desktopTriggerSync", messages.modifyNetFolderDlg_UseNetFolderDesktopAppTriggerSync() );
			panel = new FlowPanel();
			panel.add( m_useNFAllowDesktopAppToTriggerSyncRB );
			mainPanel.add( panel );
			
			m_allowDesktopAppToTriggerSyncCB = new CheckBox( messages.modifyNetFolderDlg_AllowDesktopAppTriggerSync() );
			panel = new FlowPanel();
			panel.getElement().getStyle().setMarginLeft( 30, Unit.PX );
			panel.add( m_allowDesktopAppToTriggerSyncCB );
			mainPanel.add( panel );
		}

		return mainPanel;
	}
	
	/**
	 * Create the panel that holds all of the controls for defining access rights.
	 */
	private Panel createRightsPanel()
	{
		FlowPanel mainPanel;
		final FlexTable table;
		FlexCellFormatter cellFormatter;
		Label label;
		int nextRow = 0;
		final int selectPrincipalsWidgetRow;
		
		mainPanel = new FlowPanel();
		
		table = new FlexTable();
		cellFormatter = table.getFlexCellFormatter();
		mainPanel.add( table );
		
		// Add a hint
		cellFormatter.setColSpan( nextRow, 0, 2 );
		cellFormatter.setWordWrap( nextRow, 0, false );
		cellFormatter.addStyleName( nextRow, 0, "modifyNetFolderDlg_SelectPrincipalsHint" );
		label = new InlineLabel( GwtTeaming.getMessages().modifyNetFolderDlg_SelectPrincipalsHint() );
		table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
		++nextRow;
		
		cellFormatter.setColSpan( nextRow, 0, 2 );
		selectPrincipalsWidgetRow = nextRow;
		++nextRow;
		
		// Create a widget that lets the user select users and groups.
		NetFolderSelectPrincipalsWidget.createAsync( new NetFolderSelectPrincipalsWidgetClient() 
		{
			@Override
			public void onUnavailable() 
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( NetFolderSelectPrincipalsWidget widget )
			{
				m_selectPrincipalsWidget = widget;
				table.setWidget( selectPrincipalsWidgetRow, 0, m_selectPrincipalsWidget );
			}
		} );
	
		return mainPanel;
	}
	
	/**
	 * Create the panel that holds all of the controls for defining the sync schedule
	 */
	private Panel createSchedulePanel()
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		
		// Add some space at the top
		{
			FlowPanel tmpPanel;
			
			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "marginTop5px" );
			mainPanel.add( tmpPanel );
		}
		
		// Add the radio buttons for selecting which sync schedule to use.
		{
			FlowPanel rbPanel;
			
			m_useNFServerScheduleRB = new RadioButton( "scheduledSyncOption", messages.modifyNetFolderDlg_UseNetFolderServerScheduleRbLabel() );
			rbPanel = new FlowPanel();
			rbPanel.add( m_useNFServerScheduleRB );
			mainPanel.add( rbPanel );
		
			m_useNFScheduleRB = new RadioButton( "scheduledSyncOption", messages.modifyNetFolderDlg_UseNetFolderScheduleRbLabel() );
			rbPanel = new FlowPanel();
			rbPanel.add( m_useNFScheduleRB );
			mainPanel.add( rbPanel );
		}

		m_scheduleWidget = new ScheduleWidget( messages.modifyNetFolderDlg_EnableSyncScheduleLabel());
		m_scheduleWidget.addStyleName( "modifyNetFolderDlg_ScheduleWidget" );

		mainPanel.add( m_scheduleWidget );
		
		return mainPanel;
	}
	
	/**
	 * Issue an rpc request to create a net folder.  If the rpc request is successful
	 * close this dialog.
	 */
	private void createNetFolderAndClose()
	{
		CreateNetFolderCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				FlowPanel errorPanel;
				Label label;
				String errMsg;
				
				hideStatusMsg();
				setOkEnabled( true );

				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException ex;
					
					ex = (GwtTeamingException) caught;
					if ( ex.getExceptionType() == ExceptionType.NET_FOLDER_ALREADY_EXISTS )
					{
						String desc;
						
						desc = GwtTeaming.getMessages().modifyNetFolderDlg_NetFolderAlreadyExists();
						errMsg = GwtTeaming.getMessages().modifyNetFolderDlg_ErrorCreatingNetFolder( desc );
					}
					else
					{
						errMsg = GwtTeaming.getMessages().modifyNetFolderDlg_ErrorCreatingNetFolder( ex.getAdditionalDetails() );
					}
				}
				else
				{
					errMsg = GwtTeaming.getMessages().modifyNetFolderDlg_ErrorCreatingNetFolder( caught.toString() );
				}
				
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				ScheduledCommand cmd;
				final NetFolder netFolder;
				
				netFolder = (NetFolder) result.getResponseData();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						NetFolderCreatedEvent event;

						// Fire an event that lets everyone know a net folder was created.
						event = new NetFolderCreatedEvent( netFolder );
						GwtTeaming.fireEvent( event );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );

				hideStatusMsg();
				setOkEnabled( true );

				// Close this dialog.
				hide();
			}						
		};
		
		// Issue an rpc request to create the net folder.
		{
			NetFolder netFolder;
			
			netFolder = getNetFolderFromDlg();
			
			showStatusMsg( GwtTeaming.getMessages().modifyNetFolderDlg_CreatingNetFolder() );

			cmd = new CreateNetFolderCmd( netFolder );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * This gets called when the user presses ok.  If we are editing an existing net folder
	 * we will issue an rpc request to save the net folder and then throw a "net folder modified"
	 * event.
	 * If we are creating a new net folder we will issue an rpc request to create the new net folder
	 * and then throw a "net folder created" event.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		clearErrorPanel();
		hideErrorPanel();

		// Disable the Ok button.
		setOkEnabled( false );

		// Are we editing an existing net folder?
		if ( m_netFolder != null )
		{
			// Yes, issue an rpc request to modify the net folder.  If the rpc request is
			// successful, close this dialog.
			modifyNetFolderAndClose();
		}
		else
		{
			// No, we are creating a new net folder.
			
			// Is the name entered by the user valid?
			if ( isNameValid() == false )
			{
				m_nameTxtBox.setFocus( true );
				setOkEnabled( true );
				return false;
			}
			
			// Issue an rpc request to create the net folder.  If the rpc request is successful,
			// close this dialog.
			createNetFolderAndClose();
		}
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully create/modify a net folder.
		return false;
	}
	
	/**
	 * Get whether the desktop app can sync data from this net folder.
	 */
	private boolean getAllowDesktopAppToSyncData()
	{
		return m_allowDesktopAppToSync.getValue();
	}
	
	/**
	 * 
	 */
	private boolean getAllowDesktopAppToTriggerSync()
	{
		if ( m_allowDesktopAppToTriggerSyncCB.isVisible() == false )
			return false;
		
		return m_allowDesktopAppToTriggerSyncCB.getValue();
	}
	
	/**
	 * Get whether mobile apps can sync data from this net folder.
	 */
	private boolean getAllowMobileAppsToSyncData()
	{
		return m_allowMobileAppsToSync.getValue();
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what because editSuccessful() does the work.
		return Boolean.TRUE;
	}
	
	/**
	 * 
	 */
	private NetFolderDataSyncSettings getDataSyncSettings()
	{
		NetFolderDataSyncSettings settings;
		
		settings = new NetFolderDataSyncSettings();
		
		settings.setAllowDesktopAppToSyncData( getAllowDesktopAppToSyncData() );
		settings.setAllowMobileAppsToSyncData( getAllowMobileAppsToSyncData() );
		
		settings.setAllowDesktopAppToTriggerSync( getAllowDesktopAppToTriggerSync() );
		settings.setInheritAllowDesktopAppToTriggerSync( getInheritAllowDesktopAppToTriggerSync() );
		
		return settings;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		if ( m_netFolder == null )
			return m_nameTxtBox;
		
		return m_relativePathTxtBox;
	}
	
	/**
	 * 
	 */
	private Boolean getFullSyncDirOnly()
	{
		if ( GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI() == false )
			return null;
		
		if ( m_useNFServerSyncOptionRB.getValue() == true )
			return null;
		
		return m_fullSyncDirOnlyCB.getValue();
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
		helpData.setPageId( "netfolders_create" );
		
		return helpData;
	}

	/**
	 * 
	 */
	private boolean getIndexContent()
	{
		return m_indexContentCkbox.getValue();
	}
	
	/**
	 * 
	 */
	private Boolean getInheritAllowDesktopAppToTriggerSync()
	{
		if ( m_useNFAllowDesktopAppToTriggerSyncRB.isVisible() == false )
			return Boolean.TRUE;
		
		return m_useNFServerAllowDesktopAppToTriggerSyncRB.getValue();
	}
	
	/**
	 * 
	 */
	private Boolean getInheritIndexContent()
	{
		return m_useNFServerIndexContentOptionRB.getValue();
	}
	
	/**
	 * 
	 */
	private Boolean getInheritJitsSettings()
	{
		return m_useJitsSettingsFromNFServerRB.getValue();
	}
	
	/**
	 * 
	 */
	private GwtJitsNetFolderConfig getJitsSettings()
	{
		GwtJitsNetFolderConfig settings;
		
		settings = new GwtJitsNetFolderConfig();
		settings.setJitsEnabled( getJitsEnabled() );
		settings.setAclMaxAge( getJitsAclMaxAge() );
		settings.setResultsMaxAge( getJitsResultsMaxAge() );

		return settings;
	}
	
	/**
	 * 
	 */
	private long getJitsAclMaxAge()
	{
		String maxAgeStr;
		long maxAge = 0;
		
		maxAgeStr = m_jitsAclMaxAge.getText();
		if ( maxAgeStr != null && maxAgeStr.length() > 0 )
			maxAge = Long.parseLong( maxAgeStr );
		
		maxAge *= 1000;
		return maxAge;
	}
	
	/**
	 * 
	 */
	private boolean getJitsEnabled()
	{
		if ( m_jitsEnabledCkbox.getValue() == Boolean.TRUE )
			return true;

		return false;
	}
	
	/**
	 * 
	 */
	private long getJitsResultsMaxAge()
	{
		String maxAgeStr;
		long maxAge = 0;
		
		maxAgeStr = m_jitsResultsMaxAge.getText();
		if ( maxAgeStr != null && maxAgeStr.length() > 0 )
			maxAge = Long.parseLong( maxAgeStr );
		
		maxAge *= 1000;
		return maxAge;
	}
	
	/**
	 * Issue an ajax request to get the list of net folder roots the user has
	 * permission to use. 
	 */
	private void getListOfNetFolderRoots()
	{
		GetNetFolderRootsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		m_listOfNetFolderRoots = null;
		
		// Create the callback that will be used when we issue an ajax call to get the net folder roots.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetAllNetFolderServers() );
			}
	
			@Override
			public void onSuccess( final VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						GetNetFolderRootsRpcResponseData responseData;
						
						responseData = (GetNetFolderRootsRpcResponseData) response.getResponseData();
						
						// Add the net folder roots to the ui
						if ( responseData != null )
							addNetFolderRoots( responseData.getListOfNetFolderRoots() );
						
						// Are there any net folder roots to select from?
						if ( m_netFolderRootsListbox.getItemCount() > 0 )
						{
							// Yes
							m_netFolderRootsListbox.setVisible( true );
							m_noNetFolderRootsLabel.setVisible( false );
							if ( m_netFolder != null )
							{
								// select the appropriate net folder root.
								GwtClientHelper.selectListboxItemByValue( m_netFolderRootsListbox, m_netFolder.getNetFolderRootName() );
							}
							else
								m_netFolderRootsListbox.setSelectedIndex( 0 );
						}
						else
						{
							// No, hide the listbox
							m_netFolderRootsListbox.setVisible( false );
							m_noNetFolderRootsLabel.setVisible( true );

							// Tell the user they need to create a net folder root before they
							// can create a net folder.
							Window.alert( GwtTeaming.getMessages().modifyNetFolderDlg_NoNetFolderServersPrompt() );
							
							// Invoke the "Create net folder root dialog"
							invokeCreateNetFolderRootDlg();
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Issue an ajax request to get a list of all the net folder roots.
		cmd = new GetNetFolderRootsCmd();
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Create a NetFolder object that holds the id of the net folder being edited,
	 * and the net folder's new info
	 */
	private NetFolder getNetFolderFromDlg()
	{
		NetFolder netFolder = null;
		
		netFolder = new NetFolder();
		netFolder.setName( getName() );
		
		// If we are working with an existing net folder, grab its display name.
		if ( m_netFolder != null )
		{
			if ( m_netFolder.getIsHomeDir() == false )
				netFolder.setDisplayName( getName() );
			else
				netFolder.setDisplayName( m_netFolder.getDisplayName() );
			
		}
		else
			netFolder.setDisplayName( getName() );
		
		netFolder.setRelativePath( getRelativePath() );
		netFolder.setNetFolderRootName( getNetFolderRootName() );
		netFolder.setInheritIndexContentSetting( getInheritIndexContent() );
		netFolder.setInheritJitsSettings( getInheritJitsSettings() );
		netFolder.setIndexContent( getIndexContent() );
		netFolder.setSyncScheduleConfig( getSyncScheduleConfig() );
		netFolder.setDataSyncSettings( getDataSyncSettings() );
		netFolder.setJitsConfig( getJitsSettings() );
		netFolder.setFullSyncDirOnly( getFullSyncDirOnly() );

		if ( m_netFolder != null )
		{
			netFolder.setId( m_netFolder.getId() );
			netFolder.setIsHomeDir( m_netFolder.getIsHomeDir() );
			if ( m_netFolder.getIsHomeDir() == false )
				netFolder.setRoles( getRoles() );
		}
		else
			netFolder.setRoles( getRoles() );
		
		return netFolder;
	}

	/**
	 * Return the selected net folder root
	 */
	private NetFolderRoot getNetFolderRoot()
	{
		// Are there any net folder roots to select from?
		if ( m_netFolderRootsListbox.getItemCount() > 0 )
		{
			int selectedIndex;

			// Yes
			selectedIndex = m_netFolderRootsListbox.getSelectedIndex();
			if ( selectedIndex >= 0 )
			{
				String rootName;
				
				rootName = m_netFolderRootsListbox.getValue( selectedIndex );
				
				// Find the NetFolderRoot by name.
				if ( m_listOfNetFolderRoots != null )
				{
					for ( NetFolderRoot nextRoot : m_listOfNetFolderRoots )
					{
						if ( rootName.equalsIgnoreCase( nextRoot.getName() ) )
							return nextRoot;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Return the name of the selected net folder root.
	 */
	private String getNetFolderRootName()
	{
		NetFolderRoot root;
		
		// Get the selected net folder root.
		root = getNetFolderRoot();
		if ( root != null )
			return root.getName();
		
		return null;
	}
	
	/**
	 * Return the ID of the selected net folder root.
	 */
	@SuppressWarnings("unused")
	private Long getNetFolderRootId()
	{
		NetFolderRoot root;
		
		// Get the selected net folder root.
		root = getNetFolderRoot();
		if ( root != null )
			return root.getId();
		
		return null;
	}
	
	
	/**
	 * Return the name entered by the user.
	 */
	private String getName()
	{
		return m_nameTxtBox.getValue();
	}
	
	/**
	 * Return the relative path entered by the user.
	 */
	private String getRelativePath()
	{
		return m_relativePathTxtBox.getValue();
	}
	
	/**
	 * Return the sync schedule
	 */
	private GwtNetFolderSyncScheduleConfig getSyncScheduleConfig()
	{
		GwtNetFolderSyncScheduleConfig config;
		
		config = new GwtNetFolderSyncScheduleConfig();

		config.setSyncSchedule( m_scheduleWidget.getSchedule() );
		
		if ( m_useNFServerScheduleRB.getValue() == true )
			config.setSyncScheduleOption( NetFolderSyncScheduleOption.USE_NET_FOLDER_SERVER_SCHEDULE );
		else
			config.setSyncScheduleOption( NetFolderSyncScheduleOption.USE_NET_FOLDER_SCHEDULE );
		
		return config;
	}
	
	/**
	 * Return the roles (rights) the user defined on this net folder
	 */
	private ArrayList<GwtRole> getRoles()
	{
		return m_selectPrincipalsWidget.getRoles();
	}
	
	/**
	 * 
	 */
	public void init( NetFolder netFolder )
	{
		hideErrorPanel();
		
		m_netFolder = netFolder;

		clearErrorPanel();
		hideErrorPanel();
		hideStatusMsg();
		setOkEnabled( true );

		// Clear existing data in the controls.
		m_nameTxtBox.setValue( "" );
		m_relativePathTxtBox.setValue( "" );
		m_useNFServerIndexContentOptionRB.setValue( true );
		m_useNFIndexContentOptionRB.setValue( false );
		m_indexContentCkbox.setValue( false );
		m_netFolderRootsListbox.clear();
		m_netFolderRootsListbox.setVisible( false );
		m_noNetFolderRootsLabel.setVisible( false );

		// Make sure the "Rights" tab has been added.
		if ( m_tabPanel.getWidgetIndex( m_rightsPanel ) == -1 )
			m_tabPanel.insert( m_rightsPanel, GwtTeaming.getMessages().modifyNetFolderDlg_RightsTab(), 1 );
		
		// Make sure the "Data Synchronization" tab has been added.
		if ( m_tabPanel.getWidgetIndex( m_syncPanel ) == -1 )
			m_tabPanel.add( m_syncPanel, GwtTeaming.getMessages().modifyNetFolderDlg_DataSyncTab());
		
		// Initialize the sync schedule controls
		initSyncSchedule();
		
		// Initialize the access rights
		initShareRights();
		
		// Initialize the data sync controls.
		initDataSync();
		
		// Initialize the jits controls
		initJits();

		// Are we modifying an existing net folder?
		if ( m_netFolder != null )
		{
			// Yes
			// Update the dialog's header to say "Edit Net Folder"
			setCaption( GwtTeaming.getMessages().modifyNetFolderDlg_EditHeader( m_netFolder.getName() ) );
			
			m_nameTxtBox.setValue( netFolder.getName() );
			
			m_relativePathTxtBox.setValue( netFolder.getRelativePath() );
			
			if ( netFolder.getInheritIndexContentSetting() )
			{
				m_useNFServerIndexContentOptionRB.setValue( true );
				m_useNFIndexContentOptionRB.setValue( false );
			}
			else
			{
				m_useNFServerIndexContentOptionRB.setValue( false );
				m_useNFIndexContentOptionRB.setValue( true );
			}

			m_indexContentCkbox.setValue( netFolder.getIndexContent() );
			
			// Are we dealing with a home net folder?
			if ( netFolder.getIsHomeDir() )
			{
				// Yes, remove the "Rights" panel...
				m_tabPanel.remove( m_tabPanel.getWidgetIndex( m_rightsPanel ) );
				
				// ...and remove the "Data Synchronization" panel.
				m_tabPanel.remove( m_tabPanel.getWidgetIndex( m_syncPanel ) );
			}
		}
		else
		{
			// No
			// Update the dialog's header to say "Create Net Folder"
			setCaption( GwtTeaming.getMessages().modifyNetFolderDlg_AddHeader() );
			
			// Enable the "Name" field.
			m_nameTxtBox.setEnabled( true );
		}
		
		// Select the "Configuration" tab
		m_tabPanel.selectTab( 0 );

		// Issue an ajax request to get the list of net folder roots this user has
		// permission to use.
		getListOfNetFolderRoots();
	}

	/**
	 * Initialize the controls in the data sync panel
	 */
	private void initDataSync()
	{
		boolean isHomeDir = false;
		
		m_allowDesktopAppToSync.setValue( true );
		m_allowMobileAppsToSync.setValue( true );
		
		// Initialize the controls dealing with "allow the desktop app to trigger initial home folder sync".
		{
			m_useNFServerAllowDesktopAppToTriggerSyncRB.setValue( true );
			m_useNFAllowDesktopAppToTriggerSyncRB.setValue( false );
			m_allowDesktopAppToTriggerSyncCB.setValue( false );
		}
		
		if ( GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI() )
		{
			m_useNFServerSyncOptionRB.setValue( true );
			m_useNFSyncOptionRB.setValue( false );
			m_fullSyncDirOnlyCB.setValue( false );
		}
		
		if ( m_netFolder != null )
		{
			NetFolderDataSyncSettings settings;
			Boolean dirOnly;
		
			isHomeDir = m_netFolder.getIsHomeDir();

			settings = m_netFolder.getDataSyncSettings();
			if ( settings != null )
			{
				m_allowDesktopAppToSync.setValue( settings.getAllowDesktopAppToSyncData() );
				m_allowMobileAppsToSync.setValue( settings.getAllowMobileAppsToSyncData() );

				// Set the controls dealing with "allow the desktop app to trigger initial home folder sync"
				{
					Boolean inherit;
					
					inherit = settings.getInheritAllowDesktopAppToTriggerSync();
					if ( inherit == false )
					{
						m_useNFServerAllowDesktopAppToTriggerSyncRB.setValue( false );
						m_useNFAllowDesktopAppToTriggerSyncRB.setValue( true );
					}
					
					m_allowDesktopAppToTriggerSyncCB.setValue( settings.getAllowDesktopAppToTriggerSync() );
				}
			}
			
			if ( GwtMainPage.m_requestInfo.getShowSyncOnlyDirStructureUI() )
			{
				dirOnly = m_netFolder.getFullSyncDirOnly(); 
				if ( dirOnly == null )
					m_useNFServerSyncOptionRB.setValue( true );
				else
				{
					m_useNFServerSyncOptionRB.setValue( false );
					m_useNFSyncOptionRB.setValue( true );
					m_fullSyncDirOnlyCB.setValue( dirOnly );
				}
			}
		}

		// Enable/disable controls dealing with "allow desktop app to trigger initial home folder sync"
		m_useNFServerAllowDesktopAppToTriggerSyncRB.setVisible( isHomeDir );
		m_useNFAllowDesktopAppToTriggerSyncRB.setVisible( isHomeDir );
		m_allowDesktopAppToTriggerSyncCB.setVisible( isHomeDir );
	}
	
	/**
	 * Initialize the controls used for the jits settings
	 */
	private void initJits()
	{
		m_useJitsSettingsFromNFServerRB.setValue( true );
		m_useJitsSettingsFromNetFolderRB.setValue( false );
		m_jitsEnabledCkbox.setValue( true );
		m_jitsAclMaxAge.setValue( "" );
		m_jitsResultsMaxAge.setValue( "" );
		
		if ( m_netFolder != null )
		{
			GwtJitsNetFolderConfig settings;
			
			if ( m_netFolder.getInheritJitsSettings() )
			{
				m_useJitsSettingsFromNFServerRB.setValue( true );
				m_useJitsSettingsFromNetFolderRB.setValue( false );
			}
			else
			{
				m_useJitsSettingsFromNFServerRB.setValue( false );
				m_useJitsSettingsFromNetFolderRB.setValue( true );
			}

			settings = m_netFolder.getJitsConfig();
			if ( settings != null )
			{
				m_jitsEnabledCkbox.setValue( settings.getJitsEnabled() );
				m_jitsAclMaxAge.setValue( String.valueOf( settings.getAclMaxAge() / 1000 ) );
				m_jitsResultsMaxAge.setValue( String.valueOf( settings.getResultsMaxAge() / 1000 ) );
			}
		}
		else
		{
			Long value;
			
			m_jitsEnabledCkbox.setValue( false );
			
			value = GwtMainPage.m_requestInfo.getDefaultJitsAclMaxAge() / 1000;
			m_jitsAclMaxAge.setValue( value.toString() );
			
			value = GwtMainPage.m_requestInfo.getDefaultJitsResultsMaxAge() / 1000;
			m_jitsResultsMaxAge.setValue( value.toString() );
		}
	}
	
	/**
	 * 
	 */
	private void initShareRights()
	{
		if ( m_selectPrincipalsWidget != null && m_selectPrincipalsWidget.isReady() )
		{
			if ( m_netFolder != null && m_netFolder.getIsHomeDir() == false )
				m_selectPrincipalsWidget.initWidget( m_netFolder.getRoles() );
			else
				m_selectPrincipalsWidget.initWidget( null );

			// Only allow the user to search for internal users.
			m_selectPrincipalsWidget.setSearchForExternalPrincipals( false );
			m_selectPrincipalsWidget.setSearchForInternalPrincipals( true );
			
			// Allow the user to search for ldap containers
			m_selectPrincipalsWidget.setSearchForLdapContainers( true );
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
	 * 
	 */
	private void initSyncSchedule()
	{
		if ( m_scheduleWidget != null )
		{
			m_scheduleWidget.init( null );
			m_useNFServerScheduleRB.setValue( false );
			m_useNFScheduleRB.setValue( true );
			
			if ( m_netFolder != null )
			{
				GwtNetFolderSyncScheduleConfig config;
				
				config = m_netFolder.getSyncScheduleConfig();
				if ( config != null )
				{
					m_scheduleWidget.init( config.getSyncSchedule() );
					
					m_useNFServerScheduleRB.setValue( false );
					m_useNFScheduleRB.setValue( false );
					
					if ( config.getSyncScheduleOption() == NetFolderSyncScheduleOption.USE_NET_FOLDER_SERVER_SCHEDULE )
						m_useNFServerScheduleRB.setValue( true );
					else
						m_useNFScheduleRB.setValue( true );
				}
			}
		}
		else
		{
			ScheduledCommand cmd;
			
			cmd = new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					initSyncSchedule();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * Invoke the "create net folder root" dialog
	 */
	private void invokeCreateNetFolderRootDlg()
	{
		int x;
		int y;
		
		// Get the position of this dialog.
		x = getAbsoluteLeft() + 50;
		y = getAbsoluteTop() + 50;
		
		if ( m_modifyNetFolderRootDlg == null )
		{
			ModifyNetFolderRootDlg.createAsync(
											true, 
											false,
											x, 
											y,
											new ModifyNetFolderRootDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ModifyNetFolderRootDlg mnfrDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_modifyNetFolderRootDlg = mnfrDlg;
							
							m_modifyNetFolderRootDlg.init( null );
							m_modifyNetFolderRootDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_modifyNetFolderRootDlg.init( null );
			m_modifyNetFolderRootDlg.setPopupPosition( x, y );
			m_modifyNetFolderRootDlg.show();
		}
	}
	
	/**
	 * Check to see if the configuration is valid.  We will check the follow:
	 * 1. Net folder server has been selected.
	 * 2. Relative path has been entered and does not contain a /
	 */
	private boolean isConfigurationValid()
	{
		String relPath;
		NetFolderRoot nfRoot;
		NetFolderRootType nfRootType;
		
		// Is a net folder server selected?
		nfRoot = getNetFolderRoot(); 
		if ( nfRoot == null )
		{
			Scheduler.ScheduledCommand cmd;
			
			// No
			Window.alert( GwtTeaming.getMessages().modifyNetFolderDlg_PleaseSelectNetFolderServer() );
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					m_netFolderRootsListbox.setFocus( true );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
			
			return false;
		}
		
		// Does the relative path have any '/' in it.
		// A '/' in the relative path causes problems with OES-NCP when we do a test connection
		// See bug, https://bugzilla.novell.com/show_bug.cgi?id=785315
		relPath = getRelativePath();
		nfRootType = nfRoot.getRootType();
		if ( nfRootType != NetFolderRootType.SHARE_POINT_2013 &&
			 nfRootType != NetFolderRootType.SHARE_POINT_2010 &&
			 relPath.indexOf( '/' ) != -1 )
		{
			Scheduler.ScheduledCommand cmd;
			
			// Yes
			Window.alert( GwtTeaming.getMessages().modifyNetFolderDlg_ForwardSlashNotPermittedInRelativePath() );

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					m_relativePathTxtBox.setFocus( true );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
			
			return false;
		}
		
		// If we get here everything is valid
		return true;
	}
	
	/**
	 * Is the name entered by the user valid?
	 */
	private boolean isNameValid()
	{
		String value;
		
		value = m_nameTxtBox.getValue();
		if ( value == null || value.length() == 0 )
		{
			Window.alert( GwtTeaming.getMessages().modifyNetFolderDlg_NameRequired() );
			return false;
		}
		
		return true;
	}
	
	/**
	 * Issue an rpc request to modify the net folder.  If the rpc request was successful
	 * close this dialog.
	 */
	private void modifyNetFolderAndClose()
	{
		final NetFolder newNetFolder;
		ModifyNetFolderCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;

		// Create a NetFolder object that holds the information about the net folder
		newNetFolder = getNetFolderFromDlg();
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				FlowPanel errorPanel;
				Label label;
				String errMsg;
				
				hideStatusMsg();
				setOkEnabled( true );

				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				if ( caught instanceof GwtTeamingException )
				{
					GwtTeamingException ex;
					
					ex = (GwtTeamingException) caught;
					if ( ex.getExceptionType() == ExceptionType.ACCESS_CONTROL_EXCEPTION )
					{
						String desc;
						
						desc = GwtTeaming.getMessages().modifyNetFolderDlg_InsufficientRights();
						errMsg = GwtTeaming.getMessages().modifyNetFolderDlg_ErrorModifyingNetFolder( desc );
					}
					else
					{
						errMsg = GwtTeaming.getMessages().modifyNetFolderDlg_ErrorModifyingNetFolder( ex.getAdditionalDetails() );
					}
				}
				else
				{
					errMsg = GwtTeaming.getMessages().modifyNetFolderDlg_ErrorModifyingNetFolder( caught.toString() );
				}
				
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				NetFolderModifiedEvent event;
				
				// Fire an event that lets everyone know this net folder was modified.
				event = new NetFolderModifiedEvent( newNetFolder );
				GwtTeaming.fireEvent( event );

				hideStatusMsg();
				setOkEnabled( true );

				// Close this dialog.
				hide();
			}						
		};
		
		showStatusMsg( GwtTeaming.getMessages().modifyNetFolderDlg_ModifyingNetFolder() );
		
		// Issue an rpc request to update the net folder.
		cmd = new ModifyNetFolderCmd( newNetFolder ); 
		GwtClientHelper.executeCommand( cmd, rpcCallback );
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
	 * This method gets called when the user types in the "results max age" or the
	 * "acl max age" text box.
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
	 * Handles the NetFolderRootCreatedEvent received by this class
	 */
	@Override
	public void onNetFolderRootCreated( NetFolderRootCreatedEvent event )
	{
		NetFolderRoot root;

		// Get the newly created net folder root.
		root = event.getNetFolderRoot();
		
		if ( root != null )
		{
			// Add the net folder root as the first item in the list.
			if ( m_listOfNetFolderRoots == null )
				m_listOfNetFolderRoots = new ArrayList<NetFolderRoot>();
			
			m_listOfNetFolderRoots.add( root );

			// Add the net folder root to the listbox
			m_netFolderRootsListbox.addItem( root.getName(), root.getName() );
			
			// Select the new net folder root.
			GwtClientHelper.selectListboxItemByValue( m_netFolderRootsListbox, root.getName() );
			
			m_netFolderRootsListbox.setVisible( true );
			m_noNetFolderRootsLabel.setVisible( false );
		}
	}
	

	/**
	 * Test the connection to the server to see if the information they have entered is valid.
	 */
	private void testConnection()
	{
		NetFolder netFolder;
		NetFolderRoot root;
		AsyncCallback<VibeRpcResponse> rpcCallback;

		// Is there a "test connection" request currently running?
		if ( m_inProgressPanel.isVisible() )
		{
			// Yes, bail
			return;
		}
		
		m_inProgressPanel.setVisible( true );
		
		// Create a NetFolder object that holds the information about the net folder
		netFolder = getNetFolderFromDlg();
		root = getNetFolderRoot();
		
		if ( root != null )
		{
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable caught )
				{
					String errMsg;
					
					m_inProgressPanel.setVisible( false );
					errMsg = GwtTeaming.getMessages().rpcFailure_ErrorTestingNetFolderServerConnection();
					Window.alert( errMsg );
				}
	
				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					TestNetFolderConnectionResponse response;
					String msg;
					
					response = (TestNetFolderConnectionResponse) result.getResponseData();
					switch ( response.getStatusCode() )
					{
					case NETWORK_ERROR:
						msg = GwtTeaming.getMessages().testConnection_NetworkError();
						break;
					
					case NORMAL:
						msg = GwtTeaming.getMessages().testConnection_Normal();
						break;
					
					case PROXY_CREDENTIALS_ERROR:
						msg = GwtTeaming.getMessages().testConnection_ProxyCredentialsError();
						break;
					
					case UNKNOWN:
					default:
						msg = GwtTeaming.getMessages().testConnection_UnknownStatus();
						break;
					}
					
					m_inProgressPanel.setVisible( false );
					Window.alert( msg );
				}						
			};
			
			// Issue a GWT RPC request to test net folder root connection.
			TestNetFolderConnectionCmd cmd;
			if ( root.getUseProxyIdentity() )
			     cmd = new TestNetFolderConnectionCmd( root.getName(), root.getRootType(), root.getRootPath(), netFolder.getRelativePath(), root.getProxyIdentity()                 ); 
			else cmd = new TestNetFolderConnectionCmd( root.getName(), root.getRootType(), root.getRootPath(), netFolder.getRelativePath(), root.getProxyName(), root.getProxyPwd() ); 
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
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void createDlg(
		final boolean autoHide,
		final boolean modal,
		final int left,
		final int top,
		final ModifyNetFolderDlgClient mnfDlgClient )
	{
		GWT.runAsync( ModifyNetFolderDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ModifyNetFolderDlg() );
				if ( mnfDlgClient != null )
				{
					mnfDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ModifyNetFolderDlg mnfDlg;
					
				mnfDlg = new ModifyNetFolderDlg(
											autoHide,
											modal,
											left,
											top );
				
				if ( mnfDlgClient != null )
					mnfDlgClient.onSuccess( mnfDlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final ModifyNetFolderDlg dlg,
		final int left,
		final int top,
		final UIObject showRelativeTo,
		final NetFolder netFolder,
		final ModifyNetFolderDlgClient mnfDlgClient )
	{
		GWT.runAsync( ModifyNetFolderDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ModifyNetFolderDlg() );
				if ( mnfDlgClient != null )
				{
					mnfDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				dlg.init( netFolder );
				if ( showRelativeTo != null )
					dlg.showRelativeTo( showRelativeTo );
				else
				{
					dlg.setPopupPosition( left, top );
					dlg.show();
				}
			}
		} );
	}
}
