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

import org.kablink.teaming.gwt.client.GwtLdapSyncResult;
import org.kablink.teaming.gwt.client.GwtLdapSyncResult.GwtEntityType;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults.GwtLdapSyncStatus;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.event.LdapSyncStatusEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetLdapSyncResultsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;


/**
 * ?
 *  
 * @author jwootton
 */
public class LdapSyncResultsDlg extends DlgBox
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

	private Label m_addedUsersLabel;
	private Label m_modifiedUsersLabel;
	private Label m_deletedUsersLabel;
	private Label m_addedGroupsLabel;
	private Label m_modifiedGroupsLabel;
	private Label m_deletedGroupsLabel;
	
	private int m_numAddedUsers = 0;
	private int m_numModifiedUsers = 0;
	private int m_numDeletedUsers = 0;
	private int m_numAddedGroups = 0;
	private int m_numModifiedGroups = 0;
	private int m_numDeletedGroups = 0;
	
	// These data members are used to determine which sync results we display
	private boolean m_showAddedUsers = true;
	private boolean m_showModifiedUsers = true;
	private boolean m_showDeletedUsers = true;
	private boolean m_showAddedGroups = true;
	private boolean m_showModifiedGroups = true;
	private boolean m_showDeletedGroups = true;

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
			GwtEntityType entityType;
			
			// Add the result to our list that holds all the results
			m_listOfAllLdapSyncResults.add( nextResult );
			
			// Only add results that match our filter.
			entityType = nextResult.getEntityType();
			addResult = false;
			switch ( nextResult.getSyncAction() )
			{
			case ADDED_ENTITY:
				if ( entityType == GwtEntityType.USER && m_showAddedUsers )
					addResult = true;
				else if ( entityType == GwtEntityType.GROUP && m_showAddedGroups )
					addResult = true;
				
				break;
				
			case DELETED_ENTITY:
				if ( entityType == GwtEntityType.USER && m_showDeletedUsers )
					addResult = true;
				else if ( entityType == GwtEntityType.GROUP && m_showDeletedGroups )
					addResult = true;
				
				break;
				
			case MODIFIED_ENTITY:
				if ( entityType == GwtEntityType.USER && m_showModifiedUsers )
					addResult = true;
				else if ( entityType == GwtEntityType.GROUP && m_showModifiedGroups )
					addResult = true;
				
				break;
			}
			
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
		
		// Create the controls that holds the sync statistics
		{
			Label label;
			FlexTable statsTable;
			
			statsTable = new FlexTable();
			
			// Create the controls used to display user statistics
			{
				FlowPanel userStatsPanel;
				FlexTable userStatsTable;
				int row = 0;
				
				userStatsPanel = new FlowPanel();
				userStatsPanel.addStyleName( "marginbottom3" );
				
				userStatsTable = new FlexTable();
				userStatsPanel.add( userStatsTable );
				
				// Add the "Added users:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_AddedUsersLabel() );
					userStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_addedUsersLabel = new Label( "0" );
					userStatsTable.setWidget( row, 1, m_addedUsersLabel );
					
					++row;
				}

				// Add the "Modified users:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_ModifiedUsersLabel() );
					userStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_modifiedUsersLabel = new Label( "0" );
					userStatsTable.setWidget( row, 1, m_modifiedUsersLabel );
					
					++row;
				}

				// Add the "Deleted users:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_DeletedUsersLabel() );
					userStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_deletedUsersLabel = new Label( "0" );
					userStatsTable.setWidget( row, 1, m_deletedUsersLabel );
					
					++row;
				}
				
				statsTable.setWidget( 0, 0, userStatsPanel );
			}
			
			// Create the controls used to display group statistics
			{
				FlowPanel groupStatsPanel;
				FlexTable groupStatsTable;
				int row = 0;
				
				groupStatsPanel = new FlowPanel();
				groupStatsPanel.addStyleName( "marginbottom3" );
				groupStatsPanel.addStyleName( "marginleft2" );
				
				groupStatsTable = new FlexTable();
				groupStatsPanel.add( groupStatsTable );
				
				// Add the "Added Groups:" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_AddedGroupsLabel() );
					groupStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_addedGroupsLabel = new Label( "0" );
					groupStatsTable.setWidget( row, 1, m_addedGroupsLabel );
					
					++row;
				}

				// Add the "Modified groups" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_ModifiedGroupsLabel() );
					groupStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_modifiedGroupsLabel = new Label( "0" );
					groupStatsTable.setWidget( row, 1, m_modifiedGroupsLabel );
					
					++row;
				}

				// Add the "Deleted groups" controls
				{
					label = new Label( messages.ldapSyncResultsDlg_DeletedGroupsLabel() );
					groupStatsTable.setHTML( row, 0, label.getElement().getInnerHTML() );
				
					m_deletedGroupsLabel = new Label( "0" );
					groupStatsTable.setWidget( row, 1, m_deletedGroupsLabel );
					
					++row;
				}
				
				statsTable.setWidget( 0, 1, groupStatsPanel );
			}
			
			mainPanel.add( statsTable );
		}
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "ldapSyncResultsDlg_MenuPanel" );
			
			// Add a "Delete" button.
			label = new InlineLabel( messages.manageNetFolderServersDlg_DeleteNetFolderServerLabel() );
			label.addStyleName( "manageNetFolderRootsDlg_Btn" );
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
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
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
	 * Issue an rpc request to get the ldap sync results
	 */
	private void getLdapSyncResults()
	{
		GetLdapSyncResultsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;
		
		if ( m_syncId == null || m_syncId.length() == 0 )
			return;
		
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
							final GwtLdapSyncResults ldapSyncResults;
							
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
	 * 
	 */
	private void setLdapSyncStatus( GwtLdapSyncResults ldapSyncResults )
	{
		GwtLdapSyncStatus status;
		
		status = ldapSyncResults.getSyncStatus();
		
		// Is the sync status changing?
		if ( m_syncStatus != status )
		{
			LdapSyncStatusEvent event;
			
			// Yes
			// Fire an event that lets everyone know the ldap sync status changed.
			event = new LdapSyncStatusEvent( status );
			GwtTeaming.fireEvent( event );
		}
		
		m_syncStatus = status;
		updateSyncStatusLabel();
		
		switch ( m_syncStatus )
		{
		case STATUS_ABORTED_BY_ERROR:
			showSyncError( ldapSyncResults.getErrorDesc() );
			break;
			
		case STATUS_IN_PROGRESS:
		{
			Timer timer;
			
			timer = new Timer()
			{
				@Override
				public void run()
				{
					getLdapSyncResults();
				}
			};
			timer.schedule( 3000 );
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
		m_numModifiedGroups += ldapSyncResults.getNumGroupsModified();
		m_numModifiedUsers += ldapSyncResults.getNumUsersModified();
		
		m_addedGroupsLabel.setText( String.valueOf( m_numAddedGroups ) );
		m_addedUsersLabel.setText( String.valueOf( m_numAddedUsers ) );
		m_deletedGroupsLabel.setText( String.valueOf( m_numDeletedGroups ) );
		m_deletedUsersLabel.setText( String.valueOf( m_numDeletedUsers ) );
		m_modifiedGroupsLabel.setText( String.valueOf( m_numModifiedGroups ) );
		m_modifiedUsersLabel.setText( String.valueOf( m_numModifiedUsers ) );
	}
	
	/**
	 * 
	 */
	public void init( String syncId, boolean clearExistingResults )
	{
		// The sync id is what we use to find the sync results in the session.
		m_syncId = syncId;
		
		m_syncStatus = GwtLdapSyncStatus.STATUS_IN_PROGRESS;
		updateSyncStatusLabel();
		
		hideErrorPanel();
		
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
			m_numModifiedGroups = 0;
			m_numModifiedUsers = 0;
			updateSyncStatusLabel();
		}
		
		getLdapSyncResults();
	}

	/**
	 * Show the following sync error
	 */
	private void showSyncError( String errorMsg )
	{
		FlowPanel errorPanel;
		Label label;
		
		clearErrorPanel();

		errorPanel = getErrorPanel();
	
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
			m_syncStatusImg.setVisible( false );
			statusTxt = messages.ldapSyncResultsDlg_SyncStatus_Completed();
			break;
		
		case STATUS_STOP_COLLECTING_RESULTS:
			m_syncStatusImg.setVisible( false );
			statusTxt = messages.ldapSyncResultsDlg_SyncStatus_NotCollectingResults();
			break;
		
		case STATUS_SYNC_ALREADY_IN_PROGRESS:
			m_syncStatusImg.setVisible( false );
			statusTxt = messages.ldapSyncResultsDlg_SyncStatus_SyncAlreadyInProgress();
			break;
		}

		m_syncStatusLabel.setText( statusTxt );
	}
	
	/**
	 * Loads the LdapSyncResultsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final LdapSyncResultsDlgClient lsrDlgClient )
	{
		GWT.runAsync( LdapSyncResultsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_LdapSyncResultsDlg() );
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
				lsrDlgClient.onSuccess( lsrDlg );
			}
		});
	}
}
