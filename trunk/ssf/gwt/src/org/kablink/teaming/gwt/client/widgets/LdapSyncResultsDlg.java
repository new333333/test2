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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtLdapConnectionConfig;
import org.kablink.teaming.gwt.client.GwtLdapSyncResult;
import org.kablink.teaming.gwt.client.GwtLdapSyncResult.GwtEntityType;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults.GwtLdapSyncStatus;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.QuickFilter;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.LdapSyncStatusEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetDateTimeStrCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapSyncResultsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.EditLdapConfigDlg.GwtLdapSyncMode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * ?
 *  
 * @author jwootton
 */
public class LdapSyncResultsDlg extends DlgBox
	implements
		QuickFilterEvent.Handler
{
	private CellTable<GwtLdapSyncResult> m_ldapSyncResultsTable;
	private ListDataProvider<GwtLdapSyncResult> m_dataProvider;
	private VibeSimplePager m_pager;
	private ArrayList<GwtLdapSyncResult> m_listOfAllLdapSyncResults;
	private ArrayList<GwtLdapSyncResult> m_listOfDisplayedLdapSyncResults;
	
	private String m_syncId;
	private GwtLdapSyncStatus m_syncStatus;

	private InlineLabel m_syncStatusLabel;
	private Image m_syncStatusImg;

	private FlowPanel m_previewHintPanel;
	private FlexTable m_userStatsTable;
	private FlexTable m_groupStatsTable;
	private int m_modifiedUsersRow;
	private int m_modifiedGroupsRow;

	private Label m_addedUsersLabel;
	private Label m_modifiedUsersLabel;
	private Label m_deletedUsersLabel;
	private Label m_disabledUsersLabel;
	private Label m_addedGroupsLabel;
	private Label m_modifiedGroupsLabel;
	private Label m_deletedGroupsLabel;

	private LdapSyncResultsFilterPopup m_filterPopup;

	private int m_numAddedUsers = 0;
	private int m_numModifiedUsers = 0;
	private int m_numDeletedUsers = 0;
	private int m_numDisabledUsers = 0;
	private int m_numAddedGroups = 0;
	private int m_numModifiedGroups = 0;
	private int m_numDeletedGroups = 0;
	
	// These data members are used to determine which sync results we display
	private boolean m_showAddedUsers = true;
	private boolean m_showModifiedUsers = true;
	private boolean m_showDeletedUsers = true;
	private boolean m_showDisabledUsers = true;
	private boolean m_showAddedGroups = true;
	private boolean m_showModifiedGroups = true;
	private boolean m_showDeletedGroups = true;

	private Timer m_getSyncResultsTimer = null;
	
	private QuickFilter m_quickFilter;
	private String m_currentFilterStr = null;

	private List<GwtLdapConnectionConfig> m_listOfLdapServers;

	private List<HandlerRegistration> m_registeredEventHandlers;

	// LDAP_SYNC_RESULTS_DLG is used to tell the QuickFilter widget who it is dealing with.
	private static final long LDAP_SYNC_RESULTS_DLG = -301;

	// The following defines the TeamingEvents that are handled by
	// this class. See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
	{
		TeamingEvents.QUICK_FILTER
	};
	

	/**
	 * Callback interface to interact with the "ldap sync results" dialog
	 * asynchronously after it loads. 
	 */
	public interface LdapSyncResultsDlgClient
	{
		void onSuccess( LdapSyncResultsDlg lsrDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private LdapSyncResultsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.Close );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().ldapSyncResultsDlg_Header(), null, null, null );
	}

	/**
	 * Add the given list of ldap sync results to the table.
	 */
	private void addLdapSyncResults( ArrayList<GwtLdapSyncResult> listOfLdapSyncResults )
	{
		boolean addedAResult = false;
		
		if ( listOfLdapSyncResults == null || listOfLdapSyncResults.size() == 0 )
			return;

		// Add the results to the table
		for ( GwtLdapSyncResult nextResult : listOfLdapSyncResults )
		{
			boolean addResult;
			
			// Add the result to our list that holds all the results
			m_listOfAllLdapSyncResults.add( nextResult );
			
			// Only add results that match our filter.
			addResult = doesLdapSyncResultMatchFilter( nextResult );
				
			if ( addResult )
			{
				m_listOfDisplayedLdapSyncResults.add( nextResult );
				addedAResult = true;
			}
		}
		
		// Did we add something to the display list?
		if ( addedAResult )
		{
			// Yes
			m_dataProvider.refresh();

			// Tell the table how many sync results we have.
			m_ldapSyncResultsTable.setRowCount( m_listOfDisplayedLdapSyncResults.size(), true );
		}
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		final GwtTeamingMessages messages;
		VerticalPanel mainPanel = null;
		TextColumn<GwtLdapSyncResult> col;
		FlowPanel menuPanel;
		CellTable.Resources cellTableResources;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Create the controls that display the sync status
		{
			FlexTable statusTable;
			Label label;
			
			statusTable = new FlexTable();
			statusTable.getElement().getStyle().setMarginBottom( 6, Unit.PX );
			
			label = new Label( messages.ldapSyncResultsDlg_SyncStatusLabel() );
			statusTable.setHTML( 0, 0, label.getElement().getInnerHTML() );

			// Add a place for the status
			{
				ImageResource imgResource;
				FlowPanel statusPanel;

				statusPanel = new FlowPanel();
				
				m_syncStatusLabel = new InlineLabel();
				m_syncStatusLabel.getElement().getStyle().setMarginRight( 4, Unit.PX );
				statusPanel.add( m_syncStatusLabel );
	
				imgResource = GwtTeaming.getImageBundle().spinner16();
				m_syncStatusImg = GwtClientHelper.buildImage( imgResource );
				m_syncStatusImg.setVisible( false );
				statusPanel.add( m_syncStatusImg );

				statusTable.setWidget( 0, 1, statusPanel );
			}

			mainPanel.add( statusTable );
		}
		
		// Add a hint that will be displayed when we are in "preview" mode
		{
			Label label;
			
			m_previewHintPanel = new FlowPanel();
			label = new Label( messages.ldapSyncResultsDlg_PreviewHint() );
			label.addStyleName( "ldapSyncResultsDlg_PreviewHint" );
			m_previewHintPanel.add( label );
			
			mainPanel.add( m_previewHintPanel );
		}
		
		// Create the controls that holds the sync statistics
		{
			Label label;
			FlexTable statsTable;
			CellFormatter cellFormatter;
			
			statsTable = new FlexTable();
			
			cellFormatter = statsTable.getCellFormatter();
			cellFormatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
			cellFormatter.setVerticalAlignment( 0, 1, HasVerticalAlignment.ALIGN_TOP );
			
			// Create the controls used to display user statistics
			{
				FlowPanel userStatsPanel;
				int row = 0;
				
				userStatsPanel = new FlowPanel();
				userStatsPanel.addStyleName( "marginbottom3" );
				
				m_userStatsTable = new FlexTable();
				userStatsPanel.add( m_userStatsTable );
				
				// Add the "Added users:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_AddedUsersLabel() );
					m_userStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_addedUsersLabel = new Label( "0" );
					m_userStatsTable.setWidget( row, 1, m_addedUsersLabel );
					
					++row;
				}

				// Add the "Modified users:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_ModifiedUsersLabel() );
					m_userStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_modifiedUsersLabel = new Label( "0" );
					m_userStatsTable.setWidget( row, 1, m_modifiedUsersLabel );

					m_modifiedUsersRow = row;
					++row;
				}

				// Add the "Deleted users:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_DeletedUsersLabel() );
					m_userStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_deletedUsersLabel = new Label( "0" );
					m_userStatsTable.setWidget( row, 1, m_deletedUsersLabel );
					
					++row;
				}
				
				// Add the "Disabled users:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_DisabledUsersLabel() );
					m_userStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_disabledUsersLabel = new Label( "0" );
					m_userStatsTable.setWidget( row, 1, m_disabledUsersLabel );
					
					++row;
				}
				
				statsTable.setWidget( 0, 0, userStatsPanel );
			}
			
			// Create the controls used to display group statistics
			{
				FlowPanel groupStatsPanel;
				int row = 0;
				
				groupStatsPanel = new FlowPanel();
				groupStatsPanel.addStyleName( "marginbottom3" );
				groupStatsPanel.addStyleName( "marginleft2" );
				
				m_groupStatsTable = new FlexTable();
				groupStatsPanel.add( m_groupStatsTable );
				
				// Add the "Added Groups:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_AddedGroupsLabel() );
					m_groupStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_addedGroupsLabel = new Label( "0" );
					m_groupStatsTable.setWidget( row, 1, m_addedGroupsLabel );
					
					++row;
				}

				// Add the "Modified groups" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_ModifiedGroupsLabel() );
					m_groupStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_modifiedGroupsLabel = new Label( "0" );
					m_groupStatsTable.setWidget( row, 1, m_modifiedGroupsLabel );
					
					m_modifiedGroupsRow = row;
					++row;
				}

				// Add the "Deleted groups" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_DeletedGroupsLabel() );
					m_groupStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_deletedGroupsLabel = new Label( "0" );
					m_groupStatsTable.setWidget( row, 1, m_deletedGroupsLabel );
					
					++row;
				}
				
				statsTable.setWidget( 0, 1, groupStatsPanel );
			}
			
			mainPanel.add( statsTable );
		}
		
		// Create a menu
		{
			FlowPanel mainFilterPanel;

			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "ldapSyncResultsDlg_MenuPanel" );
			
			// Add filtering controls
			{
				FlowPanel qfPanel;
				
				mainFilterPanel = new FlowPanel();
				mainFilterPanel.addStyleName( GwtClientHelper.jsIsIE() ? "displayInline" : "displayInlineBlock" );
				mainFilterPanel.addStyleName( "ldapSyncResultsDlg_MainFilterPanel" );
				
				qfPanel = new FlowPanel();
				qfPanel.addStyleName( "ldapSyncResultsDlg_QuickFilterPanel" );
				qfPanel.addStyleName( GwtClientHelper.jsIsIE() ? "displayInline" : "displayInlineBlock" );
				
				mainFilterPanel.add( qfPanel );
				
				m_quickFilter = new QuickFilter( LDAP_SYNC_RESULTS_DLG );
				qfPanel.add( m_quickFilter );
				
				menuPanel.add( mainFilterPanel );
			}

			// Add an image the user can click on to invoke the menu items that allows the
			// user to select which types of ldap sync results they want to see
			{
				FlowPanel tmpPanel;
				final Anchor a;
				Image filterImg;
				
				tmpPanel = new FlowPanel();
				tmpPanel.addStyleName( GwtClientHelper.jsIsIE() ? "displayInline" : "displayInlineBlock" );
				
				a = new Anchor();
				a.setTitle( messages.ldapSyncResultsDlg_FilterOptionsAlt() );
				filterImg = new Image( GwtTeaming.getImageBundle().menuButton() );
				filterImg.addStyleName( "vibe-filterMenuImg" );
				filterImg.getElement().setAttribute( "align", "absmiddle" );
				a.getElement().appendChild( filterImg.getElement() );
				
				a.addClickHandler( new ClickHandler()
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
								m_filterPopup.init();
								m_filterPopup.showRelativeTo( a );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				} );

				tmpPanel.add( a );
				mainFilterPanel.add( tmpPanel );

				// Create the popup that will be displayed when the user clicks on the image.
				m_filterPopup = new LdapSyncResultsFilterPopup();
			}
		}
		
		// Create the CellTable that will display the list of ldap sync results.
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_ldapSyncResultsTable = new CellTable<GwtLdapSyncResult>( 20, cellTableResources );
		
		// Set the widget that will be displayed when there are no ldap sync results
		{
			FlowPanel flowPanel;
			InlineLabel noResultsLabel;
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noResultsLabel = new InlineLabel( GwtTeaming.getMessages().ldapSyncResultsDlg_NoLdapSyncResults() );
			flowPanel.add( noResultsLabel );
			
			m_ldapSyncResultsTable.setEmptyTableWidget( flowPanel );
		}
		
		// Add the "Name" column
		col = new TextColumn<GwtLdapSyncResult>()
		{
			@Override
			public String getValue( GwtLdapSyncResult ldapSyncResult )
			{
				String name;
				
				name = ldapSyncResult.getEntityName();
				if ( name == null )
					name = "";
				
				return name;
			}
		};
		m_ldapSyncResultsTable.addColumn( col, messages.ldapSyncResultsDlg_NameCol() );
		  
		// Add the "Type" column
		col = new TextColumn<GwtLdapSyncResult>()
		{
			@Override
			public String getValue( GwtLdapSyncResult ldapSyncResult )
			{
				String type;
				
				if ( ldapSyncResult.getEntityType() == GwtEntityType.GROUP )
					type = messages.ldapSyncResultsDlg_GroupType();
				else if ( ldapSyncResult.getEntityType() == GwtEntityType.USER )
					type = messages.ldapSyncResultsDlg_UserType();
				else
					type = "Unknown Type";
				
				return type;
			}
		};
		m_ldapSyncResultsTable.addColumn( col, messages.ldapSyncResultsDlg_TypeCol() );
		
		// Add the "Action" column
		col = new TextColumn<GwtLdapSyncResult>()
		{
			@Override
			public String getValue( GwtLdapSyncResult ldapSyncResult )
			{
				String action;
				
				switch ( ldapSyncResult.getSyncAction() )
				{
				case ADDED_ENTITY:
					action = messages.ldapSyncResultsDlg_AddedAction();
					break;
					
				case DELETED_ENTITY:
					action = messages.ldapSyncResultsDlg_DeletedAction();
					break;
					
				case DISABLED_ENTITY:
					action = messages.ldapSyncResultsDlg_DisabledAction();
					break;
					
				case MODIFIED_ENTITY:
					action = messages.ldapSyncResultsDlg_ModifiedAction();
					break;
					
				default:
					action = "Unknown Action";
					break;
				}
				
				return action;
			}
		};
		m_ldapSyncResultsTable.addColumn( col, messages.ldapSyncResultsDlg_ActionCol() );

		// Create a data provider
		{
			m_listOfAllLdapSyncResults = new ArrayList<GwtLdapSyncResult>();
			m_listOfDisplayedLdapSyncResults = new ArrayList<GwtLdapSyncResult>();
			
			m_dataProvider = new ListDataProvider<GwtLdapSyncResult>( m_listOfDisplayedLdapSyncResults );
			m_dataProvider.addDataDisplay( m_ldapSyncResultsTable );
		}
		
		// Create a pager
		{
			m_pager = new VibeSimplePager();
			m_pager.setPageSize( 20 );
			m_pager.setDisplay( m_ldapSyncResultsTable );
		}

		mainPanel.add( menuPanel );
		mainPanel.add( m_ldapSyncResultsTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_pager );
		mainPanel.setCellHeight( m_pager, "100%" );

		return mainPanel;
	}

	/**
	 * Does the given ldap sync result match our filter?
	 */
	private boolean doesLdapSyncResultMatchFilter( GwtLdapSyncResult result )
	{
		boolean match;
		GwtEntityType entityType;
		GwtTeamingMessages messages;
		
		messages = GwtTeaming.getMessages();
		
		entityType = result.getEntityType();
		match = false;
		switch ( result.getSyncAction() )
		{
		case ADDED_ENTITY:
			if ( entityType == GwtEntityType.USER && m_showAddedUsers )
				match = true;
			else if ( entityType == GwtEntityType.GROUP && m_showAddedGroups )
				match = true;
			
			break;
			
		case DELETED_ENTITY:
			if ( entityType == GwtEntityType.USER && m_showDeletedUsers )
				match = true;
			else if ( entityType == GwtEntityType.GROUP && m_showDeletedGroups )
				match = true;
			
			break;
			
		case DISABLED_ENTITY:
			if ( entityType == GwtEntityType.USER && m_showDisabledUsers )
				match = true;
			
			break;
			
		case MODIFIED_ENTITY:
			if ( entityType == GwtEntityType.USER && m_showModifiedUsers )
				match = true;
			else if ( entityType == GwtEntityType.GROUP && m_showModifiedGroups )
				match = true;
			
			break;
		}
		
		if ( match )
		{
			// Do we have a filter string?
			if ( m_currentFilterStr != null && m_currentFilterStr.length() > 0 )
			{
				String name;
				String type = null;
				String action = null;
				String lowerCaseFilter;
				
				// Yes
				match = false;
			
				name = result.getEntityName();
				if ( name != null )
					name = name.toLowerCase();
				
				if ( result.getEntityType() == GwtEntityType.GROUP )
					type = messages.ldapSyncResultsDlg_GroupType();
				else if ( result.getEntityType() == GwtEntityType.USER )
					type = messages.ldapSyncResultsDlg_UserType();
				
				if ( type != null )
					type = type.toLowerCase();

				switch ( result.getSyncAction() )
				{
				case ADDED_ENTITY:
					action = messages.ldapSyncResultsDlg_AddedAction();
					break;
					
				case DELETED_ENTITY:
					action = messages.ldapSyncResultsDlg_DeletedAction();
					break;
					
				case DISABLED_ENTITY:
					action = messages.ldapSyncResultsDlg_DisabledAction();
					break;
					
				case MODIFIED_ENTITY:
					action = messages.ldapSyncResultsDlg_ModifiedAction();
					break;
					
				default:
					break;
				}
				
				if ( action != null )
					action = action.toLowerCase();
				
				// Does the next result match our filter
				lowerCaseFilter = m_currentFilterStr.toLowerCase();
				if ( (name != null && name.indexOf( lowerCaseFilter ) != -1) ||
					 (type != null && type.indexOf( lowerCaseFilter ) != -1) ||
					 (action != null && action.indexOf( lowerCaseFilter ) != -1) )
				{
					match = true;
				}
			}
		}
		
		return match;
	}

	/**
	 * Filter the current ldap sync results
	 */
	private void filterCurrentLdapSyncResults()
	{
		// Clear the current list of displayed results.
		if ( m_listOfDisplayedLdapSyncResults != null )
			m_listOfDisplayedLdapSyncResults.clear();
		
		if ( m_listOfAllLdapSyncResults != null )
		{
			for ( GwtLdapSyncResult nextResult : m_listOfAllLdapSyncResults )
			{
				boolean addResult;
				
				// Only add results that match our filter.
				addResult = doesLdapSyncResultMatchFilter( nextResult );
				if ( addResult )
					m_listOfDisplayedLdapSyncResults.add( nextResult );
			}
		}
		
		m_dataProvider.refresh();

		// Tell the table how many sync results we have.
		m_pager.setPage( 0 );
		m_ldapSyncResultsTable.setRowCount( m_listOfDisplayedLdapSyncResults.size(), true );
	}

	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what since we only have a close button.
		return Boolean.TRUE;
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
	private void getLdapSyncResultsDelayed( int milliSeconds )
	{
		// Does a timer already exist for getting the ldap sync results?
		if ( m_getSyncResultsTimer != null )
		{
			// Yes, nothing to do.
			return;
		}
		
		m_getSyncResultsTimer = new Timer()
		{
			@Override
			public void run()
			{
				m_getSyncResultsTimer = null;
				getLdapSyncResultsImpl();
			}
		};
		m_getSyncResultsTimer.schedule( milliSeconds );
	}
	
	/**
	 * Issue an rpc request to get the ldap sync results
	 */
	private void getLdapSyncResultsImpl()
	{
		GetLdapSyncResultsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;
		
		if ( m_syncId == null || m_syncId.length() == 0 )
			return;
		
		hideErrorPanel();
		
		// Create the callback that will be used when we issue an ajax call
		// to get the ldap sync results.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( final Throwable t )
			{
				FlowPanel errorPanel;
				Label label;
				
				clearErrorPanel();

				errorPanel = getErrorPanel();
			
				label = new Label( "getLdapSyncResults(), rpc failure" );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );

				showErrorPanel();

				GwtClientHelper.handleGwtRPCFailure(
										t,
										GwtTeaming.getMessages().rpcFailure_GetLdapSyncResults() );
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
						VibeRpcResponseData responseData;
						
						hideStatusMsg();
						
						responseData = response.getResponseData();
						if ( responseData != null && responseData instanceof GwtLdapSyncResults )
						{
							GwtLdapSyncResults ldapSyncResults;
							
							ldapSyncResults = (GwtLdapSyncResults) responseData;
							
							// Add the results to the dialog
							addLdapSyncResults( ldapSyncResults.getListOfSyncResults() );
							
							// Update the statistics
							updateLdapSyncStatistics( ldapSyncResults );

							setLdapSyncStatus( ldapSyncResults );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};
		
		showStatusMsg( GwtTeaming.getMessages().ldapSyncResultsDlg_RequestingLdapSyncResults() );

		cmd = new GetLdapSyncResultsCmd();
		cmd.setSyncId( m_syncId );
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
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/**
	 * Handles QuickFilterEvent's received by this class.
	 * 
	 * Implements the QuickFilterEvent.Handler.onQuickFilter() method.
	 * 
	 * @param event
	 */
	@Override
	public void onQuickFilter( QuickFilterEvent event )
	{
		// Is this event meant for us?
		if ( event.getFolderId().equals( LDAP_SYNC_RESULTS_DLG ) )
		{
			Scheduler.ScheduledCommand cmd;
			
			// Yes.  Filter the list of ldap sync results.
			m_currentFilterStr = event.getQuickFilter();

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					filterCurrentLdapSyncResults();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
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
										m_registeredEvents,
										this,
										m_registeredEventHandlers );
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
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	
	/**
	 * 
	 */
	private void setLdapSyncStatus( GwtLdapSyncResults ldapSyncResults )
	{
		GwtLdapSyncStatus status;
		LdapSyncStatusEvent event;
		
		status = ldapSyncResults.getSyncStatus();
		if ( status == null )
		{
			Window.alert( "in setLdapSyncStatus(), status is null" );
			return;
		}
		
		// Fire an event that lets everyone know the ldap sync status changed.
		event = new LdapSyncStatusEvent( status );
		GwtTeaming.fireEvent( event );
		
		m_syncStatus = status;
		updateSyncStatusLabel();
		
		switch ( m_syncStatus )
		{
		case STATUS_ABORTED_BY_ERROR:
			showSyncError( ldapSyncResults.getErrorLdapServerId(), ldapSyncResults.getErrorDesc() );
			break;
			
		case STATUS_IN_PROGRESS:
		{
			Scheduler.ScheduledCommand cmd;
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					getLdapSyncResultsDelayed( 3000 );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
			break;
		}
			
		case STATUS_COMPLETED:
			break;
			
		case STATUS_STOP_COLLECTING_RESULTS:
			break;
			
		case STATUS_SYNC_ALREADY_IN_PROGRESS:
			break;
		}
	}
	
	/**
	 * 
	 */
	private void updateLdapSyncStatistics( GwtLdapSyncResults ldapSyncResults )
	{
		m_numAddedGroups += ldapSyncResults.getNumGroupsAdded();
		m_numAddedUsers += ldapSyncResults.getNumUsersAdded();
		m_numDeletedGroups += ldapSyncResults.getNumGroupsDeleted();
		m_numDeletedUsers += ldapSyncResults.getNumUsersDeleted();
		m_numDisabledUsers += ldapSyncResults.getNumUsersDisabled();
		m_numModifiedGroups += ldapSyncResults.getNumGroupsModified();
		m_numModifiedUsers += ldapSyncResults.getNumUsersModified();
		
		m_addedGroupsLabel.setText( String.valueOf( m_numAddedGroups ) );
		m_addedUsersLabel.setText( String.valueOf( m_numAddedUsers ) );
		m_deletedGroupsLabel.setText( String.valueOf( m_numDeletedGroups ) );
		m_deletedUsersLabel.setText( String.valueOf( m_numDeletedUsers ) );
		m_disabledUsersLabel.setText( String.valueOf( m_numDisabledUsers ) );
		m_modifiedGroupsLabel.setText( String.valueOf( m_numModifiedGroups ) );
		m_modifiedUsersLabel.setText( String.valueOf( m_numModifiedUsers ) );
	}
	
	/**
	 * 
	 */
	private void init(
		List<GwtLdapConnectionConfig> listOfLdapServers,
		String syncId,
		boolean clearExistingResults,
		GwtLdapSyncMode syncMode )
	{
		GwtTeamingMessages messages;
		
		hideErrorPanel();
		
		m_listOfLdapServers = listOfLdapServers;
		
		// Update the header depending on the sync mode.
		messages = GwtTeaming.getMessages();
		if ( syncMode == GwtLdapSyncMode.PERFORM_SYNC )
		{
			setCaption( messages.ldapSyncResultsDlg_Header() );
			m_previewHintPanel.setVisible( false );
			
			// Show the "modified users" and "modified groups" controls
			m_userStatsTable.getRowFormatter().setVisible( m_modifiedUsersRow, true );
			m_groupStatsTable.getRowFormatter().setVisible( m_modifiedGroupsRow, true );
		}
		else if ( syncMode == GwtLdapSyncMode.PREVIEW_ONLY )
		{
			setCaption( messages.ldapSyncResultsDlg_HeaderPreview() );
			m_previewHintPanel.setVisible( true );
			
			// Hide the "modified users" and "modified groups" controls
			m_userStatsTable.getRowFormatter().setVisible( m_modifiedUsersRow, false );
			m_groupStatsTable.getRowFormatter().setVisible( m_modifiedGroupsRow, false );
		}
		
		// Should we start fresh?
		if ( clearExistingResults )
		{
			// Yes
			m_listOfAllLdapSyncResults.clear();
			m_listOfDisplayedLdapSyncResults.clear();

			// Tell the table how many sync results we have.
			m_ldapSyncResultsTable.setRowCount( m_listOfDisplayedLdapSyncResults.size(), true );
			m_dataProvider.refresh();
			
			m_numAddedGroups = 0;
			m_numAddedUsers = 0;
			m_numDeletedGroups = 0;
			m_numDeletedUsers = 0;
			m_numDisabledUsers = 0;
			m_numModifiedGroups = 0;
			m_numModifiedUsers = 0;
			
			m_syncStatusImg.setVisible( false );
			m_syncStatusLabel.setText( "" );
		}

		if ( syncId != null && syncId.length() > 0 )
		{
			// The sync id is what we use to find the sync results in the session.
			m_syncId = syncId;
			
			// Wait for 1.5 second before we issue a request to get the ldap sync results.  We need to give the
			// request to start the ldap sync time to create an LdapSyncThread and store that object in the
			// session.
			getLdapSyncResultsDelayed( 1500 );
		}
	}

	/**
	 * Show the following sync error
	 */
	private void showSyncError( String ldapServerId, String errorMsg )
	{
		FlowPanel errorPanel;
		Label label;
		
		clearErrorPanel();

		errorPanel = getErrorPanel();
	
		if ( ldapServerId != null && ldapServerId.length() > 0 && m_listOfLdapServers != null )
		{
			// Find the ldap server that had the problem.
			for ( GwtLdapConnectionConfig nextLdapServer : m_listOfLdapServers )
			{
				if ( ldapServerId.equalsIgnoreCase( nextLdapServer.getId() ) )
				{
					label = new Label( GwtTeaming.getMessages().ldapSyncResultsDlg_ServerLabel() + " " + nextLdapServer.getServerUrl() );
					label.addStyleName( "dlgErrorLabel" );
					errorPanel.add( label );
					
					break;
				}
			}
		}
		
		label = new Label( errorMsg );
		label.addStyleName( "dlgErrorLabel" );
		errorPanel.add( label );

		showErrorPanel();
	}
	
	/**
	 * Update the label that displays the current sync status
	 */
	private void updateSyncStatusLabel()
	{
		String statusTxt;
		GwtTeamingMessages messages;
		
		m_syncStatusImg.setVisible( false );

		if ( m_syncStatus == null )
			return;
		
		messages = GwtTeaming.getMessages();
		
		statusTxt = "Unknown sync status";
		
		switch ( m_syncStatus )
		{
		case STATUS_ABORTED_BY_ERROR:
			m_syncStatusImg.setVisible( false );
			statusTxt = messages.ldapSyncResultsDlg_SyncStatus_Error();
			break;
		
		case STATUS_IN_PROGRESS:
			m_syncStatusImg.setVisible( true );
			statusTxt = messages.ldapSyncResultsDlg_SyncStatus_InProgress();
			break;
		
		case STATUS_COMPLETED:
		{
			// Add the current date/time to the status message
			Date now;
			GetDateTimeStrCmd cmd;
			AsyncCallback<VibeRpcResponse> getDateStrCallback = null;
			
			m_syncStatusImg.setVisible( false );
			statusTxt = null;

			getDateStrCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					String txt;
					
					txt = GwtTeaming.getMessages().ldapSyncResultsDlg_SyncStatus_Completed();
					m_syncStatusLabel.setText( txt );

					GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetDateStr() );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					StringRpcResponseData responseData = null;
					String txt;
					
					if ( response.getResponseData() instanceof StringRpcResponseData )
						responseData = (StringRpcResponseData) response.getResponseData();
					
					txt = GwtTeaming.getMessages().ldapSyncResultsDlg_SyncStatus_Completed();

					if ( responseData != null )
					{
						String dateTimeStr;

						dateTimeStr = responseData.getStringValue();
						if ( dateTimeStr != null )
							txt += " (" + dateTimeStr + ")";
					}

					m_syncStatusLabel.setText( txt );
				}
			};
			
			// Issue an rpc request to get the date/time string.
			now = new Date();
			cmd = new GetDateTimeStrCmd( now.getTime(), DateFormat.LONG, DateFormat.LONG );
			GwtClientHelper.executeCommand( cmd, getDateStrCallback );
			break;
		}
		
		case STATUS_STOP_COLLECTING_RESULTS:
			m_syncStatusImg.setVisible( false );
			statusTxt = messages.ldapSyncResultsDlg_SyncStatus_NotCollectingResults();
			break;
		
		case STATUS_SYNC_ALREADY_IN_PROGRESS:
			m_syncStatusImg.setVisible( false );
			statusTxt = messages.ldapSyncResultsDlg_SyncStatus_SyncAlreadyInProgress();
			break;
		}

		if ( statusTxt != null )
			m_syncStatusLabel.setText( statusTxt );
	}

	
	/**
	 * This class displays the options for filtering ldap sync results.
	 * @author jwootton
	 *
	 */
	public class LdapSyncResultsFilterPopup extends TeamingPopupPanel
	{
		private CheckBox m_showAddedGroupsCB;
		private CheckBox m_showAddedUsersCB;
		private CheckBox m_showDeletedGroupsCB;
		private CheckBox m_showDeletedUsersCB;
		private CheckBox m_showDisabledUsersCB;
		private CheckBox m_showModifiedGroupsCB;
		private CheckBox m_showModifiedUsersCB;

		/**
		 * 
		 */
		public LdapSyncResultsFilterPopup()
		{
			super( true, true );
		
			FlowPanel mainPanel;
			FlowPanel footerPanel;
			FlexTable statsTable;
			CellFormatter cellFormatter;
			Button btn;
			GwtTeamingMessages messages;
		
			messages = GwtTeaming.getMessages();
			
			// Tell this popup to 'roll down' when opening. 
			GwtClientHelper.rollDownPopup( this );
			
			// Override the style used for PopupPanel
			setStyleName( "ldapSyncResultsDlg_FilterPopup" );
		
			mainPanel = new FlowPanel();

			statsTable = new FlexTable();
			
			cellFormatter = statsTable.getCellFormatter();
			cellFormatter.setVerticalAlignment( 0, 0, HasVerticalAlignment.ALIGN_TOP );
			cellFormatter.setVerticalAlignment( 0, 1, HasVerticalAlignment.ALIGN_TOP );
			
			// Create the checkboxes used to define which user statistics are displayed
			{
				FlowPanel userPanel;
				FlexTable userTable;
				int row = 0;
				
				userPanel = new FlowPanel();
				userPanel.addStyleName( "marginbottom3" );
				
				userTable = new FlexTable();
				userPanel.add( userTable );

				// Add the "Added users" controls
				{
					m_showAddedUsersCB = new CheckBox( messages.ldapSyncResultsDlg_ShowAddedUsersCB() );
					userTable.setWidget( row, 0, m_showAddedUsersCB );
				
					++row;
				}

				// Add the "Modified users:" controls
				{
					m_showModifiedUsersCB = new CheckBox( messages.ldapSyncResultsDlg_ShowModifiedUsersCB() );
					userTable.setWidget( row, 0, m_showModifiedUsersCB );
					
					++row;
				}

				// Add the "Deleted users:" controls
				{
					m_showDeletedUsersCB = new CheckBox( messages.ldapSyncResultsDlg_ShowDeletedUsersCB() );
					userTable.setWidget( row, 0, m_showDeletedUsersCB );
					
					++row;
				}
				
				// Add the "Disabled users:" controls
				{
					m_showDisabledUsersCB = new CheckBox( messages.ldapSyncResultsDlg_ShowDisabledUsersCB() );
					userTable.setWidget( row, 0, m_showDisabledUsersCB );
					
					++row;
				}
				
				statsTable.setWidget( 0, 0, userPanel );
			}
			
			// Create the checkboxes used to define which group statistics are displayed
			{
				FlowPanel groupPanel;
				FlexTable groupTable;
				int row = 0;
				
				groupPanel = new FlowPanel();
				groupPanel.addStyleName( "marginbottom3" );
				groupPanel.addStyleName( "marginleft2" );
				
				groupTable = new FlexTable();
				groupPanel.add( groupTable );
				
				// Add the "Added Groups:" controls
				{
					m_showAddedGroupsCB = new CheckBox( messages.ldapSyncResultsDlg_ShowAddedGroupsCB() );
					groupTable.setWidget( row, 0, m_showAddedGroupsCB );
					
					++row;
				}

				// Add the "Modified groups" controls
				{
					m_showModifiedGroupsCB = new CheckBox( messages.ldapSyncResultsDlg_ShowModifiedGroupsCB() );
					groupTable.setWidget( row, 0, m_showModifiedGroupsCB );
					
					++row;
				}

				// Add the "Deleted groups" controls
				{
					m_showDeletedGroupsCB = new CheckBox( messages.ldapSyncResultsDlg_ShowDeletedGroupsCB() );
					groupTable.setWidget( row, 0, m_showDeletedGroupsCB );
					
					++row;
				}
				
				statsTable.setWidget( 0, 1, groupPanel );
			}
			
			mainPanel.add( statsTable );

			footerPanel = new FlowPanel();
			footerPanel.getElement().getStyle().setTextAlign( TextAlign.RIGHT );
			mainPanel.add( footerPanel );
			
			btn = new Button( messages.ok() );
			btn.addStyleName( "teamingSmallButton" );
			btn.addClickHandler( new ClickHandler()
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
							m_showAddedGroups = m_showAddedGroupsCB.getValue();
							m_showAddedUsers = m_showAddedUsersCB.getValue();
							m_showModifiedGroups = m_showModifiedGroupsCB.getValue();
							m_showModifiedUsers = m_showModifiedUsersCB.getValue();
							m_showDeletedGroups = m_showDeletedGroupsCB.getValue();
							m_showDeletedUsers = m_showDeletedUsersCB.getValue();
							m_showDisabledUsers = m_showDisabledUsersCB.getValue();

							// Redisplay the current list of ldap sync results with the new criteria
							filterCurrentLdapSyncResults();
							
							hide();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			footerPanel.add( btn );

			btn = new Button( messages.cancel() );
			btn.addStyleName( "teamingSmallButton" );
			btn.addClickHandler( new ClickHandler()
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
							hide();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			footerPanel.add( btn );

			setWidget( mainPanel );
		}

		/**
		 * 
		 */
		public void init()
		{
			m_showAddedUsersCB.setValue( m_showAddedUsers );
			m_showModifiedUsersCB.setValue( m_showModifiedUsers );
			m_showDeletedUsersCB.setValue( m_showDeletedUsers );
			m_showDisabledUsersCB.setValue( m_showDisabledUsers );
			m_showAddedGroupsCB.setValue( m_showAddedGroups );
			m_showModifiedGroupsCB.setValue( m_showModifiedGroups );
			m_showDeletedGroupsCB.setValue( m_showDeletedGroups );
		}
		
		/**
		 * Shows this popup.
		 */
		@Override
		public void show()
		{
			super.show();

			// ...and add vertical scrolling to the main frame for the
			// ...duration of the popup.
			GwtClientHelper.scrollUIForPopup( this );
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
		final LdapSyncResultsDlgClient lsrDlgClient )
	{
		GWT.runAsync( LdapSyncResultsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapConfigDlg() );
				if ( lsrDlgClient != null )
				{
					lsrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				LdapSyncResultsDlg lsrDlg;
				
				lsrDlg = new LdapSyncResultsDlg(
											autoHide,
											modal,
											left,
											top );
				
				if ( lsrDlgClient != null )
					lsrDlgClient.onSuccess( lsrDlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final LdapSyncResultsDlg dlg,
		final List<GwtLdapConnectionConfig> listOfLdapServers,
		final String syncId,
		final boolean clearResults,
		final GwtLdapSyncMode syncMode,
		final LdapSyncResultsDlgClient lsrDlgClient )
	{
		GWT.runAsync( LdapSyncResultsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapConfigDlg() );
				if ( lsrDlgClient != null )
				{
					lsrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				dlg.init(
						listOfLdapServers,
						syncId,
						clearResults,
						syncMode );
				dlg.show();
			}
		} );
	}
}
