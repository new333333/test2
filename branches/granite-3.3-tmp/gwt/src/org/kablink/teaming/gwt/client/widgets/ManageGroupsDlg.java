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
import java.util.Set;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.VibeCellTableResources;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GroupCreatedEvent;
import org.kablink.teaming.gwt.client.event.GroupCreationFailedEvent;
import org.kablink.teaming.gwt.client.event.GroupCreationStartedEvent;
import org.kablink.teaming.gwt.client.event.GroupModificationFailedEvent;
import org.kablink.teaming.gwt.client.event.GroupModificationStartedEvent;
import org.kablink.teaming.gwt.client.event.GroupModifiedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteGroupsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetAllGroupsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;


/**
 * 
 * @author jwootton
 *
 */
public class ManageGroupsDlg extends DlgBox
	implements
		GroupCreatedEvent.Handler,
		GroupCreationFailedEvent.Handler,
		GroupCreationStartedEvent.Handler,
		GroupModificationFailedEvent.Handler,
		GroupModificationStartedEvent.Handler,
		GroupModifiedEvent.Handler
{
	private CellTable<GroupInfoPlus> m_groupsTable;
    private MultiSelectionModel<GroupInfoPlus> m_selectionModel;
	private ListDataProvider<GroupInfoPlus> m_dataProvider;
	private SimplePager m_pager;
	private List<GroupInfoPlus> m_listOfGroups;
	private ModifyGroupDlg m_modifyGroupDlg;
    private int m_width;
    private int m_height;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
		TeamingEvents.GROUP_CREATED,
		TeamingEvents.GROUP_CREATION_FAILED,
		TeamingEvents.GROUP_CREATION_STARTED,
		TeamingEvents.GROUP_MODIFICATION_FAILED,
		TeamingEvents.GROUP_MODIFICATION_STARTED,
		TeamingEvents.GROUP_MODIFIED
	};
	
	/**
	 * The different statuses of a group 
	 */
	public enum GroupModificationStatus
	{
		GROUP_CREATION_IN_PROGRESS,
		GROUP_DELETION_IN_PROGRESS,
		GROUP_MODIFICATION_IN_PROGRESS,
		READY
	}

	/**
	 * This class holds information about a group and the current status of a group.
	 * For example, is group modification in process, is group creation in process.
	 */
	public class GroupInfoPlus
	{
		private GroupInfo m_groupInfo;
		private GroupModificationStatus m_status;
		
		/**
		 * 
		 */
		public GroupInfoPlus( GroupInfo groupInfo, GroupModificationStatus status )
		{
			m_groupInfo = groupInfo;
			m_status = status;
		}
		
		/**
		 * 
		 */
		public GroupInfo getGroupInfo()
		{
			return m_groupInfo;
		}
		
		/**
		 * 
		 */
		public GroupModificationStatus getStatus()
		{
			return m_status;
		}
		
		/**
		 * 
		 */
		public void setStatus( GroupModificationStatus status )
		{
			m_status = status;
		}
	}
	
	/**
	 * 
	 */
	public ManageGroupsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.Close );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
									GwtTeaming.getEventBus(),
									m_registeredEvents,
									this );

		// Create the header, content and footer of this dialog box.
		m_width = width;
		m_height = height;
		createAllDlgContent( GwtTeaming.getMessages().manageGroupsDlgHeader(), null, null, null ); 
	}

	/**
	 * Add the given list of groups to the dialog
	 */
	private void addGroups( List<GroupInfo> listOfGroups )
	{
		m_listOfGroups = new ArrayList<GroupInfoPlus>();
		
		// Create a GroupInfoPlus object for every group
		for (GroupInfo nextGroup : listOfGroups )
		{
			GroupInfoPlus groupInfoPlus;
			
			groupInfoPlus = new GroupInfoPlus( nextGroup, GroupModificationStatus.READY );
			m_listOfGroups.add( groupInfoPlus );
		}
		
		if ( m_dataProvider == null )
		{
			m_dataProvider = new ListDataProvider<GroupInfoPlus>( m_listOfGroups );
			m_dataProvider.addDataDisplay( m_groupsTable );
		}
		else
		{
			m_dataProvider.setList( m_listOfGroups );
			m_dataProvider.refresh();
		}
		
		// Clear all selections.
		m_selectionModel.clear();
		
		// Go to the first page
		m_pager.firstPage();
		
		// Tell the table how many groups we have.
		m_groupsTable.setRowCount( m_listOfGroups.size(), true );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		final GwtTeamingMessages messages;
		VerticalPanel mainPanel = null;
		GroupTitleCell cell;
		Column<GroupInfoPlus,GroupInfoPlus> titleCol;
		TextColumn<GroupInfoPlus> nameCol;
		FlowPanel menuPanel;
		CellTable.Resources cellTableResources;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "groupManagementMenuPanel" );
			
			// Add an "Add" button.
			label = new InlineLabel( messages.manageGroupsDlgAddGroupLabel() );
			label.addStyleName( "groupManagementBtn" );
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
							invokeAddGroupDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
			
			// Add a "Delete" button.
			label = new InlineLabel( messages.manageGroupsDlgDeleteGroupLabel() );
			label.addStyleName( "groupManagementBtn" );
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
							deleteSelectedGroups();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
		}
		
		// Create the CellTable that will display the list of groups.
		cellTableResources = GWT.create( VibeCellTableResources.class );
		m_groupsTable = new CellTable<GroupInfoPlus>( 15, cellTableResources );
		m_groupsTable.setWidth( String.valueOf( m_width ) + "px" );
		
		// Set the widget that will be displayed when there are no groups
		{
			FlowPanel flowPanel;
			InlineLabel noGroupsLabel;
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noGroupsLabel = new InlineLabel( GwtTeaming.getMessages().manageGroupsDlgNoGroupsLabel() );
			flowPanel.add( noGroupsLabel );
			
			m_groupsTable.setEmptyTableWidget( flowPanel );
		}
		
	    // Add a selection model so we can select groups.
	    m_selectionModel = new MultiSelectionModel<GroupInfoPlus>();
	    m_groupsTable.setSelectionModel( m_selectionModel, DefaultSelectionEventManager.<GroupInfoPlus> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<GroupInfoPlus, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<GroupInfoPlus, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( GroupInfoPlus groupInfoPlus )
		        {
            		// Get the value from the selection model.
		            return m_selectionModel.isSelected( groupInfoPlus );
		        }
		    };
	        m_groupsTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_groupsTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Title" column.  The user can click on the text in this column
		// to edit the group.
		{
			cell = new GroupTitleCell();
			titleCol = new Column<GroupInfoPlus, GroupInfoPlus>( cell )
			{
				@Override
				public GroupInfoPlus getValue( GroupInfoPlus groupInfoPlus )
				{
					return groupInfoPlus;
				}
			};
		
			titleCol.setFieldUpdater( new FieldUpdater<GroupInfoPlus, GroupInfoPlus>()
			{
				@Override
				public void update( int index, GroupInfoPlus groupInfoPlus, GroupInfoPlus value )
				{
					// Is this group in the process of being deleted, modified or created?
					if ( groupInfoPlus.getStatus() == GroupModificationStatus.READY )
					{
						// No, let the user edit the group.
						invokeModifyGroupDlg( groupInfoPlus.getGroupInfo() );
					}
				}
			} );
			m_groupsTable.addColumn( titleCol, messages.manageGroupsDlgTitleCol() );
		}
		  
		// Add the "Name" column
		nameCol = new TextColumn<GroupInfoPlus>()
		{
			@Override
			public String getValue( GroupInfoPlus groupInfoPlus )
			{
				GroupInfo groupInfo;
				String name;
				
				groupInfo = groupInfoPlus.getGroupInfo();
				
				name = groupInfo.getName();
				if ( name == null )
					name = "";
				
				return name;
			}
		};
		m_groupsTable.addColumn( nameCol, messages.manageGroupsDlgNameCol() );
		
		// Create a pager
		{
			SimplePager.Resources pagerResources;

			pagerResources = GWT.create( SimplePager.Resources.class );
			m_pager = new SimplePager( TextLocation.CENTER, pagerResources, false, 0, true );
			m_pager.setDisplay( m_groupsTable );
		}

		mainPanel.add( menuPanel );
		mainPanel.add( m_groupsTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_pager );

		return mainPanel;
	}
	

	/**
	 * Delete the selected groups.
	 */
	private void deleteSelectedGroups()
	{
		Set<GroupInfoPlus> selectedGroups;
		Iterator<GroupInfoPlus> groupIterator;
		String grpNames;
		int count = 0;
		
		grpNames = "";
		
		selectedGroups = getSelectedGroups();
		if ( selectedGroups != null )
		{
			// Get a list of all the selected group names
			groupIterator = selectedGroups.iterator();
			while ( groupIterator.hasNext() )
			{
				GroupInfoPlus nextGroup;
				
				nextGroup = groupIterator.next();
				
				if ( nextGroup.getStatus() == GroupModificationStatus.READY )
				{
					GroupInfo groupInfo;
					String text;

					groupInfo = nextGroup.getGroupInfo();
					
					text = groupInfo.getTitle();
					if ( text == null || text.length() == 0 )
						text = groupInfo.getName();
					grpNames += "\t" + text + "\n";
					
					++count;
				}
			}
		}
		
		// Do we have any groups to delete?
		if ( count > 0 )
		{
			String msg;
			
			// Yes, ask the user if they want to delete the selected groups?
			msg = GwtTeaming.getMessages().manageGroupsDlgConfirmDelete( grpNames );
			if ( Window.confirm( msg ) )
			{
				deleteGroupsFromServer( selectedGroups );
			}
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().manageGroupsDlgSelectGroupToDelete() );
		}
	}
	
	/**
	 * 
	 */
	private void deleteGroupsFromServer( Set<GroupInfoPlus> selectedGroups )
	{
		ArrayList<GroupInfo> listOfGroupsToDelete;
		final ArrayList<GroupInfo> listOfGroupsToDeleteFinal;
		Iterator<GroupInfoPlus> groupIterator;
		
		listOfGroupsToDelete = new ArrayList<GroupInfo>();
		
		// Get a list of GroupInfo objects
		groupIterator = selectedGroups.iterator();
		while ( groupIterator.hasNext() )
		{
			GroupInfoPlus nextGroup;
			
			nextGroup = groupIterator.next();
			if ( nextGroup.getStatus() == GroupModificationStatus.READY )
			{
				GroupInfo groupInfo;

				groupInfo = nextGroup.getGroupInfo();
				
				// Add this group to the list of groups to be deleted.
				listOfGroupsToDelete.add( groupInfo );
				
				// Update the status of this group.
				nextGroup.setStatus( GroupModificationStatus.GROUP_DELETION_IN_PROGRESS );
			}
		}

		listOfGroupsToDeleteFinal = listOfGroupsToDelete;
		if ( listOfGroupsToDeleteFinal != null && listOfGroupsToDeleteFinal.size() > 0 )
		{
			DeleteGroupsCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;
	
			// Create the callback that will be used when we issue an ajax call to delete the groups.
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure( final Throwable t )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							GwtClientHelper.handleGwtRPCFailure(
									t,
									GwtTeaming.getMessages().rpcFailure_DeleteGroups() );

							// Spin through the list of groups that were to be deleted
							// are set their status to ready.  We don't know which
							// groups actually got deleted.
							for (GroupInfo nextGroup : listOfGroupsToDeleteFinal)
							{
								GroupInfoPlus nextGroupPlus;
								
								nextGroupPlus = findGroupById( nextGroup.getId() );
								if ( nextGroupPlus != null )
									nextGroupPlus.setStatus( GroupModificationStatus.READY );
							}
							
							// Update the table to reflect the fact that we deleted a group.
							m_dataProvider.refresh();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( final VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						public void execute()
						{
							// Spin through the list of groups we just deleted and remove
							// them from the table.
							for (GroupInfo nextGroup : listOfGroupsToDeleteFinal)
							{
								GroupInfoPlus nextGroupPlus;
								
								nextGroupPlus = findGroupById( nextGroup.getId() );
								if ( nextGroupPlus != null )
									m_listOfGroups.remove( nextGroupPlus );
							}
							
							// Update the table to reflect the fact that we deleted a group.
							m_dataProvider.refresh();

							// Tell the table how many groups we have.
							m_groupsTable.setRowCount( m_listOfGroups.size(), true );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Update the table to reflect the fact group deletion is in progress
			m_dataProvider.refresh();

			// Issue an ajax request to get delete the list of groups.
			cmd = new DeleteGroupsCmd( listOfGroupsToDeleteFinal );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}		
	}

	/**
	 * Find the given group in our list of groups.
	 */
	private GroupInfoPlus findGroupById( Long id )
	{
		if ( m_listOfGroups != null && id != null )
		{
			for (GroupInfoPlus nextGroup : m_listOfGroups)
			{
				GroupInfo groupInfo;
				
				groupInfo = nextGroup.getGroupInfo();
				if ( groupInfo.getId() == id )
					return nextGroup;
			}
		}
		
		// If we get here we did not find the group.
		return null;
	}
	
	/**
	 * Find the given group by name in our list of groups.
	 */
	private GroupInfoPlus findNewGroupByName( String name )
	{
		if ( m_listOfGroups != null && name != null )
		{
			for (GroupInfoPlus nextGroup : m_listOfGroups)
			{
				GroupInfo groupInfo;
				
				groupInfo = nextGroup.getGroupInfo();
				if ( name.equalsIgnoreCase( groupInfo.getName() ) )
				{
					Long id;
					
					// We are looking for a group that doesn't existing the db yet.
					id = groupInfo.getId();
					if ( id != null && id == -1 )
						return nextGroup;
				}
			}
		}
		
		// If we get here we did not find the group.
		return null;
	}
	
	/**
	 * Issue an ajax request to get a list of all the groups.
	 */
	private void getAllGroupsFromServer()
	{
		GetAllGroupsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		// Create the callback that will be used when we issue an ajax call to get all the groups.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetAllGroups() );
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( final VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					public void execute()
					{
						GetGroupsRpcResponseData responseData;
						
						responseData = (GetGroupsRpcResponseData) response.getResponseData();
						
						// Add the groups to the ui
						if ( responseData != null )
							addGroups( responseData.getGroups() );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Issue an ajax request to get a list of all the groups.
		cmd = new GetAllGroupsCmd();
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Get the data from the controls in the dialog box.
	 */
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what since we only have a close button.
		return Boolean.TRUE;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	/**
	 * Return a list of selected groups.
	 */
	public Set<GroupInfoPlus> getSelectedGroups()
	{
		return m_selectionModel.getSelectedSet();
	}
	
	/**
	 * 
	 */
	public void init()
	{
		// Issue an ajax request to get a list of all the groups
		getAllGroupsFromServer();
	}
	
	/**
	 * Invoke the "Add Group" dialog.
	 */
	private void invokeAddGroupDlg()
	{
		invokeModifyGroupDlg( null );
	}
	
	/**
	 * 
	 */
	private void invokeModifyGroupDlg( GroupInfo groupInfo )
	{
		int x;
		int y;
		
		// Get the position of this dialog.
		x = getAbsoluteLeft() + 50;
		y = getAbsoluteTop() + 50;
		
		if ( m_modifyGroupDlg == null )
		{
			m_modifyGroupDlg = new ModifyGroupDlg( false, true, x, y );
		}
		
		m_modifyGroupDlg.init( groupInfo );
		m_modifyGroupDlg.setPopupPosition( x, y );
		m_modifyGroupDlg.show();
	}

	/**
	 * Handles the GroupCreatedEvent received by this class
	 */
	@Override
	public void onGroupCreated( GroupCreatedEvent event )
	{
		GroupInfo createdGroupInfo;

		// Get the newly created group.
		createdGroupInfo = event.getGroupInfo();
		
		if ( createdGroupInfo != null )
		{
			GroupInfoPlus groupInfoPlus;
			
			// Find this group in our list of groups.
			groupInfoPlus = findNewGroupByName( createdGroupInfo.getName() );
			
			if ( groupInfoPlus != null )
			{
				GroupInfo groupInfo;
				
				groupInfo = groupInfoPlus.getGroupInfo();
				
				// Update the group with the title and description from the group that
				// was passed in the event.
				groupInfo.setId( createdGroupInfo.getId() );
				groupInfo.setTitle( createdGroupInfo.getTitle() );
				groupInfo.setDesc( createdGroupInfo.getDesc() );
				
				// Update the status of the group.
				groupInfoPlus.setStatus( GroupModificationStatus.READY );
				
				// Update the table to reflect the fact that a group was modified.
				m_dataProvider.refresh();
			}
		}
	}
	
	/**
	 * Handles the GroupCreationFailedEvent received by this class.
	 */
	@Override
	public void onGroupCreationFailed( GroupCreationFailedEvent event )
	{
		GroupInfo groupInfo;
		
		// Get the group we failed on
		groupInfo = event.getGroupInfo();
		
		if ( groupInfo != null )
		{
			GroupInfoPlus groupInfoPlus;
			
			// Tell the user about the error
			GwtClientHelper.handleGwtRPCFailure(
											event.getException(),
											GwtTeaming.getMessages().rpcFailure_CreateGroup(),
											groupInfo.getName() );
			
			// Find this group in our list of groups.
			groupInfoPlus = findNewGroupByName( groupInfo.getName() );
			
			if ( groupInfoPlus != null )
			{
				// Remove the group from the list.
				m_listOfGroups.remove( groupInfoPlus );
			
				// Update the table to reflect the fact that we deleted a group.
				m_dataProvider.refresh();

				// Tell the table how many groups we have.
				m_groupsTable.setRowCount( m_listOfGroups.size(), true );
			}
		}
	}
	
	/**
	 * Handles the GroupCreationStartedEvent received by this class
	 */
	public void onGroupCreationStarted( GroupCreationStartedEvent event )
	{
		GroupInfo groupInfo;
		
		// Get the GroupInfo passed in the event.
		groupInfo = event.getGroupInfo();
		
		if ( groupInfo != null )
		{
			GroupInfoPlus newGroupInfoPlus;
			
			newGroupInfoPlus = new GroupInfoPlus( groupInfo, GroupModificationStatus.GROUP_CREATION_IN_PROGRESS );
			
			// Add the group as the first group in the list.
			m_listOfGroups.add( 0, newGroupInfoPlus );
			
			// Update the table to reflect the new group we just created.
			m_dataProvider.refresh();
			
			// Go to the first page.
			m_pager.firstPage();
			
			// Select the newly created group.
			m_selectionModel.setSelected( newGroupInfoPlus, true );

			// Tell the table how many groups we have.
			m_groupsTable.setRowCount( m_listOfGroups.size(), true );
		}
	}
	
	/**
	 * Handles the GroupModificationFailedEvent received by this class
	 */
	@Override
	public void onGroupModificationFailed( GroupModificationFailedEvent event )
	{
		GroupInfo groupInfo;
		
		// Get the GroupInfo passed in the event.
		groupInfo = event.getGroupInfo();
		
		if ( groupInfo != null )
		{
			GroupInfoPlus groupInfoPlus;
			Long id;
			
			// Tell the user about the error
			GwtClientHelper.handleGwtRPCFailure(
										event.getException(),
										GwtTeaming.getMessages().rpcFailure_ModifyGroup() );
			
			// Find this group in our list of groups.
			id = groupInfo.getId();
			groupInfoPlus = findGroupById( id );
			
			if ( groupInfoPlus != null )
			{
				// Set the group's modification state to ready
				groupInfoPlus.setStatus( GroupModificationStatus.READY );

				// Update the table to reflect the fact that this group is being modified.
				m_dataProvider.refresh();
			}
		}
	}

	/**
	 * Handles the GroupModificationStartedEvent received by this class
	 */
	@Override
	public void onGroupModificationStarted( GroupModificationStartedEvent event )
	{
		GroupInfo groupInfo;
		
		// Get the GroupInfo passed in the event.
		groupInfo = event.getGroupInfo();
		
		if ( groupInfo != null )
		{
			GroupInfoPlus groupInfoPlus;
			Long id;
			
			// Find this group in our list of groups.
			id = groupInfo.getId();
			groupInfoPlus = findGroupById( id );
			
			if ( groupInfoPlus != null )
			{
				// Set the group's modification state to modification in progress
				groupInfoPlus.setStatus( GroupModificationStatus.GROUP_MODIFICATION_IN_PROGRESS );

				// Update the table to reflect the fact that this group is being modified.
				m_dataProvider.refresh();
			}
		}
	}

	/**
	 * Handles the GroupModifiedEvent received by this class
	 */
	@Override
	public void onGroupModified( GroupModifiedEvent event )
	{
		GroupInfo modifiedGroup;
		
		// Get the GroupInfo passed in the event.
		modifiedGroup = event.getGroupInfo();
		
		if ( modifiedGroup != null )
		{
			GroupInfoPlus groupInfoPlus;
			
			// Find this group in our list of groups.
			groupInfoPlus = findGroupById( modifiedGroup.getId() );
			
			if ( groupInfoPlus != null )
			{
				GroupInfo groupInfo;
				
				groupInfo = groupInfoPlus.getGroupInfo();
				
				// Update the group with the title and description from the group that
				// was passed in the event.
				groupInfo.setTitle( modifiedGroup.getTitle() );
				groupInfo.setDesc( modifiedGroup.getDesc() );
				
				// Update the status of the group.
				groupInfoPlus.setStatus( GroupModificationStatus.READY );
				
				// Update the table to reflect the fact that a group was modified.
				m_dataProvider.refresh();
			}
		}
	}
}
