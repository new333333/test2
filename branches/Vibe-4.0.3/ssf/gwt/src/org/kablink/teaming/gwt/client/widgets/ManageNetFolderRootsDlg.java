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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolderRoot.NetFolderRootStatus;
import org.kablink.teaming.gwt.client.datatable.NetFolderRootNameCell;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderRootCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderRootModifiedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CheckNetFolderServerStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CheckNetFolderServerStatusRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFolderRootsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFolderServersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderRootsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderRootsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetHasSeenOesWarningCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFolderRootsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFolderServerCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.ModifyNetFolderRootDlgClient;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.NetFolderRootType;

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
import com.google.gwt.user.client.Timer;
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
 * ?
 *  
 * @author jwootton
 */
public class ManageNetFolderRootsDlg extends DlgBox
	implements
		NetFolderRootCreatedEvent.Handler,
		NetFolderRootModifiedEvent.Handler
{
	private CellTable<NetFolderRoot> m_netFolderRootsTable;
    private MultiSelectionModel<NetFolderRoot> m_selectionModel;
	private ListDataProvider<NetFolderRoot> m_dataProvider;
	private VibeSimplePager m_pager;
	private List<NetFolderRoot> m_listOfNetFolderRoots;
	private ModifyNetFolderRootDlg m_modifyNetFolderRootDlg;
    private int m_width;
    private boolean m_hasSeenOesWarning = false;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
		TeamingEvents.NET_FOLDER_ROOT_CREATED,
		TeamingEvents.NET_FOLDER_ROOT_MODIFIED
	};
	

	/**
	 * Callback interface to interact with the "manage net folder roots" dialog
	 * asynchronously after it loads. 
	 */
	public interface ManageNetFolderRootsDlgClient
	{
		void onSuccess( ManageNetFolderRootsDlg mfsrDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private ManageNetFolderRootsDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		int width,
		int height )
	{
		super( autoHide, modal, xPos, yPos, new Integer( width ), new Integer( height ), DlgButtonMode.Close );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
									GwtTeaming.getEventBus(),
									m_registeredEvents,
									this );
		
		// Create the header, content and footer of this dialog box.
		m_width = width;
		createAllDlgContent( GwtTeaming.getMessages().manageNetFolderServersDlg_Header(), null, null, null );
	}

	/**
	 * Add the given list of Net Folder Roots to the dialog
	 */
	private void addNetFolderRoots( List<NetFolderRoot> listOfNetFolderRoots )
	{
		m_listOfNetFolderRoots = listOfNetFolderRoots;
		
		if ( m_dataProvider == null )
		{
			m_dataProvider = new ListDataProvider<NetFolderRoot>( m_listOfNetFolderRoots );
			m_dataProvider.addDataDisplay( m_netFolderRootsTable );
		}
		else
		{
			m_dataProvider.setList( m_listOfNetFolderRoots );
			m_dataProvider.refresh();
		}
		
		// Clear all selections.
		m_selectionModel.clear();
		
		// Go to the first page
		m_pager.firstPage();
		
		// Tell the table how many net folder roots we have.
		m_netFolderRootsTable.setRowCount( m_listOfNetFolderRoots.size(), true );
	}

	/**
	 * Check the sync status of each net folder root that has a sync in progress.
	 */
	private void checkSyncStatus()
	{
		HashSet<NetFolderRoot> listOfNetFolderRootsToCheck;
		
		listOfNetFolderRootsToCheck = new HashSet<NetFolderRoot>();
		
		if ( m_listOfNetFolderRoots != null )
		{
			for ( NetFolderRoot nextFolderRoot : m_listOfNetFolderRoots )
			{
				if ( nextFolderRoot.getStatus() == NetFolderRootStatus.SYNC_IN_PROGRESS &&
					 nextFolderRoot.getStatusTicketId() != null )
				{
					listOfNetFolderRootsToCheck.add( nextFolderRoot );
				}
			}
		}
		
		if ( listOfNetFolderRootsToCheck.size() > 0 )
		{
			CheckNetFolderServerStatusCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;

			// Create the callback that will be used when we issue an ajax call
			// to check the sync status of the net folder roots.
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( final Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_CheckNetFolderRootsStatus() );
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
							if ( response.getResponseData() != null )
							{
								if ( response.getResponseData() != null )
								{
									Set<NetFolderRoot> listOfNetFolderRoots;
									CheckNetFolderServerStatusRpcResponseData responseData;
									
									responseData = (CheckNetFolderServerStatusRpcResponseData) response.getResponseData();
									
									listOfNetFolderRoots = responseData.getListOfNetFolderServers();
									
									// Update the status of each of the folder roots.
									updateFolderRootStatus( listOfNetFolderRoots );
								}
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Issue an ajax request to check the sync status of the list of net folder roots.
			cmd = new CheckNetFolderServerStatusCmd( listOfNetFolderRootsToCheck );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
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
		NetFolderRootNameCell cell;
		Column<NetFolderRoot,NetFolderRoot> nameCol;
		TextColumn<NetFolderRoot> rootPathCol;
		FlowPanel menuPanel;
		CellTable.Resources cellTableResources;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "manageNetFolderRootsDlg_MenuPanel" );
			
			// Add an "Add" button.
			label = new InlineLabel( messages.manageNetFolderServersDlg_AddNetFolderServerLabel() );
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
							invokeAddNetFolderRootDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
			
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
							deleteSelectedNetFolderRoots();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );

			// Add a "Sync" button.
			label = new InlineLabel( messages.manageNetFolderServersDlg_SyncLabel() );
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
							syncSelectedNetFolderRoots();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
		}
		
		// Create the CellTable that will display the list of Net Folder Roots.
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_netFolderRootsTable = new CellTable<NetFolderRoot>( 20, cellTableResources );
		m_netFolderRootsTable.setWidth( String.valueOf( m_width-20 ) + "px" );
		
		// Set the widget that will be displayed when there are no Net Folder Roots
		{
			FlowPanel flowPanel;
			InlineLabel noRootsLabel;
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noRootsLabel = new InlineLabel( GwtTeaming.getMessages().manageNetFolderServersDlg_NoNetFolderServersLabel() );
			flowPanel.add( noRootsLabel );
			
			m_netFolderRootsTable.setEmptyTableWidget( flowPanel );
		}
		
	    // Add a selection model so we can select net folder roots.
	    m_selectionModel = new MultiSelectionModel<NetFolderRoot>();
	    m_netFolderRootsTable.setSelectionModel(
	    									m_selectionModel,
	    									DefaultSelectionEventManager.<NetFolderRoot> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<NetFolderRoot, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<NetFolderRoot, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( NetFolderRoot netFolderRoot )
		        {
            		// Get the value from the selection model.
		            return m_selectionModel.isSelected( netFolderRoot );
		        }
		    };
	        m_netFolderRootsTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_netFolderRootsTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Name" column.  The user can click on the text in this column
		// to edit the Net Folder Root.
		{
			cell = new NetFolderRootNameCell();
			nameCol = new Column<NetFolderRoot, NetFolderRoot>( cell )
			{
				@Override
				public NetFolderRoot getValue( NetFolderRoot netFolderRoot )
				{
					return netFolderRoot;
				}
			};
		
			nameCol.setFieldUpdater( new FieldUpdater<NetFolderRoot, NetFolderRoot>()
			{
				@Override
				public void update( int index, NetFolderRoot netFolderRoot, NetFolderRoot value )
				{
					invokeModifyNetFolderRootDlg( netFolderRoot );
				}
			} );
			m_netFolderRootsTable.addColumn( nameCol, messages.manageNetFolderServersDlg_NameCol() );
		}
		  
		// Add the "Root Path" column
		rootPathCol = new TextColumn<NetFolderRoot>()
		{
			@Override
			public String getValue( NetFolderRoot netFolderRoot )
			{
				String rootPath;
				
				rootPath = netFolderRoot.getRootPath();
				if ( rootPath == null )
					rootPath = "";
				
				return rootPath;
			}
		};
		m_netFolderRootsTable.addColumn( rootPathCol, messages.manageNetFolderServersDlg_ServerPathCol() );
		
		// Create a pager
		{
			m_pager = new VibeSimplePager();
			m_pager.setDisplay( m_netFolderRootsTable );
		}

		mainPanel.add( menuPanel );
		mainPanel.add( m_netFolderRootsTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_pager );
		mainPanel.setCellHeight( m_pager, "100%" );

		return mainPanel;
	}
	

	/**
	 * Delete the selected net folder roots.
	 */
	private void deleteSelectedNetFolderRoots()
	{
		Set<NetFolderRoot> selectedRoots;
		Iterator<NetFolderRoot> rootIterator;
		String rootNames;
		int count = 0;
		
		rootNames = "";
		
		selectedRoots = getSelectedNetFolderRoots();
		if ( selectedRoots != null )
		{
			// Get a list of all the selected net folder root names
			rootIterator = selectedRoots.iterator();
			while ( rootIterator.hasNext() )
			{
				NetFolderRoot nextRoot;
				String text;
				
				nextRoot = rootIterator.next();
				
				text = nextRoot.getName();
				rootNames += "\t" + text + "\n";
				
				++count;
			}
		}
		
		// Do we have any net folder roots to delete?
		if ( count > 0 )
		{
			String msg;
			
			// Yes, ask the user if they want to delete the selected net folder roots?
			msg = GwtTeaming.getMessages().manageNetFolderServersDlg_ConfirmDelete( rootNames );
			if ( Window.confirm( msg ) )
			{
				// Unselect the selected net folder servers.
				m_selectionModel.clear();
				
				deleteNetFolderRootsFromServer( selectedRoots );
			}
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().manageNetFolderServersDlg_SelectServersToDelete() );
		}
	}
	
	/**
	 * 
	 */
	private void deleteNetFolderRootsFromServer( Set<NetFolderRoot> listOfNetFolderRootsToDelete )
	{
		if ( listOfNetFolderRootsToDelete != null && listOfNetFolderRootsToDelete.size() > 0 )
		{
			DeleteNetFolderRootsCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;
	
			// Create the callback that will be used when we issue an ajax call
			// to delete the net folder roots.
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
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
									GwtTeaming.getMessages().rpcFailure_DeleteNetFolderServers() );

							// Update the table to reflect the fact that we deleted a net folder root.
							m_dataProvider.refresh();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
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
							DeleteNetFolderServersRpcResponseData responseData;
							
							responseData = (DeleteNetFolderServersRpcResponseData) response.getResponseData();
							
							if ( responseData != null )
							{
								List<NetFolderRoot> list;
								
								// Get the list of net folder servers that were deleted
								list = responseData.getListOfDeletedNetFolderRoots();
								if ( list != null && list.size() > 0 )
								{
									// Spin through the list of net folder roots we just deleted and remove
									// them from the table.
									for (NetFolderRoot nextRoot : list)
									{
										NetFolderRoot fsRoot;
										
										fsRoot = findNetFolderRootById( nextRoot.getId() );
										if ( fsRoot != null )
											m_listOfNetFolderRoots.remove( fsRoot );
									}
								}
								
								// Get the list of net folder servers that could not be deleted.
								list = responseData.getListOfCouldNotDeleteNetFolderServers();
								if ( list != null && list.size() > 0 )
								{
									String rootNames;
									
									rootNames = "";
									
									// Spin through the list of net folder roots that could not be
									// deleted and show the user the list.
									for (NetFolderRoot nextRoot : list)
									{
										NetFolderRoot fsRoot;
										
										fsRoot = findNetFolderRootById( nextRoot.getId() );
										if ( fsRoot != null )
										{
											String text;
											
											text = nextRoot.getName();
											rootNames += "\t" + text + "\n";
										}
									}
									
									// Show the user the list of net folders servers that could not be deleted.
									Window.alert( GwtTeaming.getMessages().manageNetFolderServersDlg_CouldNotDeletePrompt( rootNames ) );
								}
							}
							
							// Update the table to reflect the fact that we deleted a net folder root.
							m_dataProvider.refresh();

							// Tell the table how many net folder roots we have.
							m_netFolderRootsTable.setRowCount( m_listOfNetFolderRoots.size(), true );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Update the table to reflect the fact that net folder root deletion is in progress
			m_dataProvider.refresh();

			// Issue an ajax request to delete the list of net folder roots.
			cmd = new DeleteNetFolderRootsCmd( listOfNetFolderRootsToDelete );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}		
	}

	/**
	 * Find the given net folder root in our list of net folder roots .
	 */
	private NetFolderRoot findNetFolderRootById( Long id )
	{
		if ( m_listOfNetFolderRoots != null && id != null )
		{
			for (NetFolderRoot nextRoot : m_listOfNetFolderRoots)
			{
				if ( id.compareTo( nextRoot.getId() ) == 0 )
					return nextRoot;
			}
		}
		
		// If we get here we did not find the net folder root.
		return null;
	}
	
	/**
	 * Issue an ajax request to get a list of all the net folder roots.
	 */
	private void getAllNetFolderRootsFromServer()
	{
		GetNetFolderRootsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		// Create the callback that will be used when we issue an ajax call to get all the net folder roots.
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
						{
							addNetFolderRoots( responseData.getListOfNetFolderRoots() );
							
							m_hasSeenOesWarning = responseData.getHasSeenOesNetFolderServerWarning();
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
	@Override
	public HelpData getHelpData()
	{
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName( HelpData.ADMIN_GUIDE );
		helpData.setPageId( "netfolders_servers" );
		
		return helpData;
	}

	/**
	 * Return a list of selected net folder roots.
	 */
	public Set<NetFolderRoot> getSelectedNetFolderRoots()
	{
		return m_selectionModel.getSelectedSet();
	}
	
	/**
	 * 
	 */
	public void init()
	{
		// Issue an ajax request to get a list of all the net folder roots
		getAllNetFolderRootsFromServer();
	}
	
	/**
	 * Invoke the "Add Net Folder Root" dialog.
	 */
	private void invokeAddNetFolderRootDlg()
	{
		invokeModifyNetFolderRootDlg( null );
	}
	
	/**
	 * 
	 */
	private void invokeModifyNetFolderRootDlg( final NetFolderRoot netFolderRoot )
	{
		int x;
		int y;
		
		// Get the position of this dialog.
		x = getAbsoluteLeft() + 50;
		y = getAbsoluteTop() + 50;
		
		if ( m_modifyNetFolderRootDlg == null )
		{
			ModifyNetFolderRootDlg.createAsync(
											false, 
											true,
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
							
							m_modifyNetFolderRootDlg.init( netFolderRoot );
							m_modifyNetFolderRootDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_modifyNetFolderRootDlg.init( netFolderRoot );
			m_modifyNetFolderRootDlg.setPopupPosition( x, y );
			m_modifyNetFolderRootDlg.show();
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
			m_listOfNetFolderRoots.add( 0, root );
			
			// Update the table to reflect the new root we just created.
			m_dataProvider.refresh();
			
			// Go to the first page.
			m_pager.firstPage();
			
			// Select the newly created root.
			m_selectionModel.setSelected( root, true );

			// Tell the table how many roots we have.
			m_netFolderRootsTable.setRowCount( m_listOfNetFolderRoots.size(), true );
			
			showOesWarningIfNeeded( root );
		}
	}
	

	/**
	 * Handles the NetFolderRootModifiedEvent received by this class
	 */
	@Override
	public void onNetFolderRootModified( NetFolderRootModifiedEvent event )
	{
		NetFolderRoot root;
		
		// Get the NetFolderRoot passed in the event.
		root = event.getNetFolderRoot();
		
		if ( root != null )
		{
			Long id;
			NetFolderRoot existingRoot;
			
			// Find this root in our list of roots.
			id = root.getId();
			existingRoot = findNetFolderRootById( id );
			
			if ( existingRoot != null )
			{
				// Update the root object with the new data.
				existingRoot.copy( root );
				
				// Update the table to reflect the fact that this root has been modified.
				m_dataProvider.refresh();
				
				showOesWarningIfNeeded( existingRoot );
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void setPixelSize( int width, int height )
	{
		super.setPixelSize( width, height );
		
		m_width = width;
		m_netFolderRootsTable.setWidth( String.valueOf( m_width-20 ) + "px" );
	}
	

	/**
	 * If the user has not seen the oes warning and the server type is oes show the warning
	 * about the OES server needs to be fully patched.
	 */
	private void showOesWarningIfNeeded( NetFolderRoot root )
	{
		if ( root != null )
		{
			// Is the server type OES?
			if ( root.getRootType() == NetFolderRootType.OES )
			{
				// Yes
				// Has the user seen the oes warning before?
				if ( m_hasSeenOesWarning == false )
				{
					SetHasSeenOesWarningCmd cmd;
					AsyncCallback<VibeRpcResponse> rpcCallback = null;

					// No, show it.
					Window.alert( GwtTeaming.getMessages().manageNetFolderServersDlg_OesWarning() );
					
					// Create the callback that will be used when we issue an ajax call
					// to set the value of the "has seen oes warning" user property.
					rpcCallback = new AsyncCallback<VibeRpcResponse>()
					{
						@Override
						public void onFailure( final Throwable t )
						{
							GwtClientHelper.handleGwtRPCFailure(
													t,
													GwtTeaming.getMessages().rpcFailure_SetHasSeenOesWarning() );
						}
				
						@Override
						public void onSuccess( final VibeRpcResponse response )
						{
							m_hasSeenOesWarning = true;
						}
					};
					
					// Remember that the user has seen the OES warning
					cmd = new SetHasSeenOesWarningCmd( true );
					GwtClientHelper.executeCommand( cmd, rpcCallback );				}
			}
		}
	}
	
	/**
	 * Sync the given net folder roots
	 */
	private void syncNetFolderRoots( final Set<NetFolderRoot> selectedRoots )
	{
		SyncNetFolderServerCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		if ( selectedRoots == null || selectedRoots.size() == 0 )
			return;
		
		// Mark each of the selected net folders as "sync in progress"
		for ( NetFolderRoot nextNetFolderRoot : selectedRoots )
		{
			nextNetFolderRoot.setStatus( NetFolderRootStatus.SYNC_IN_PROGRESS );
		}

		// Create the callback that will be used when we issue an ajax call
		// to sync the net folder roots.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
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
								GwtTeaming.getMessages().rpcFailure_SyncNetFolderRoots() );

						// Update the table to reflect the fact that we sync'd a net folder root.
						m_dataProvider.refresh();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
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
						if ( response.getResponseData() != null )
						{
							SyncNetFolderRootsRpcResponseData responseData;
							Set<NetFolderRoot> listOfNetFolderRoots;
							
							responseData = (SyncNetFolderRootsRpcResponseData) response.getResponseData();
							
							listOfNetFolderRoots = responseData.getListOfNetFolderRoots();
							
							updateFolderRootStatus( listOfNetFolderRoots );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Update the table to reflect the fact that net folder sync is in progress
		m_dataProvider.refresh();

		// Issue an ajax request to sync the list of net folder roots.
		cmd = new SyncNetFolderServerCmd( selectedRoots );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Sync the selected net folder roots.
	 */
	private void syncSelectedNetFolderRoots()
	{
		Set<NetFolderRoot> selectedRoots;
		
		selectedRoots = getSelectedNetFolderRoots();

		// Do we have any net folder roots to sync?
		if ( selectedRoots == null || selectedRoots.size() == 0 )
		{
			// No
			Window.alert( GwtTeaming.getMessages().manageNetFolderServersDlg_SelectServersToSync() );
			return;
		}
		
		syncNetFolderRoots( selectedRoots );
	}
	
	/**
	 * For each net folder root in the given list, find the net folder root in our internal list
	 * and update its status
	 */
	private void updateFolderRootStatus( Set<NetFolderRoot> listOfNetFolderRoots )
	{
		if ( listOfNetFolderRoots != null )
		{
			boolean checkAgain;
			
			checkAgain = false;
			
			for ( NetFolderRoot nextNetFolderRoot : listOfNetFolderRoots )
			{
				NetFolderRoot ourNetFolderRoot;
				
				ourNetFolderRoot = findNetFolderRootById( nextNetFolderRoot.getId() );
				
				if ( ourNetFolderRoot != null )
				{
					NetFolderRootStatus status;
					String statusTicketId;

					status = nextNetFolderRoot.getStatus();
					ourNetFolderRoot.setStatus( status );
					
					statusTicketId = nextNetFolderRoot.getStatusTicketId();
					ourNetFolderRoot.setStatusTicketId( statusTicketId );
					
					if ( status != NetFolderRootStatus.READY )
					{
						checkAgain = true;
					}
				}
			}
			
			// Update the table to reflect the fact that we sync'd a net folder root.
			m_dataProvider.refresh();
			
			if ( checkAgain )
			{
				Timer timer;
				
				timer = new Timer()
				{
					@Override
					public void run()
					{
						checkSyncStatus();
					}
				};
				timer.schedule( 5000 );
			}
		}
	}
	
	/**
	 * Loads the ManageNetFolderRootsDlg split point and returns an instance
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
							final ManageNetFolderRootsDlgClient mfsrDlgClient )
	{
		GWT.runAsync( ManageNetFolderRootsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ManageNetFolderServersDlg() );
				if ( mfsrDlgClient != null )
				{
					mfsrDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ManageNetFolderRootsDlg mfsrDlg;
				
				mfsrDlg = new ManageNetFolderRootsDlg(
													autoHide,
													modal,
													left,
													top,
													width,
													height );
				mfsrDlgClient.onSuccess( mfsrDlg );
			}
		});
	}
}
