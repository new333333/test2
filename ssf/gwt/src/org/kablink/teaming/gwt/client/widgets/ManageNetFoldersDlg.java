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

import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolder.NetFolderStatus;
import org.kablink.teaming.gwt.client.datatable.NetFolderNameCell;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderModifiedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CheckNetFoldersStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CheckNetFoldersStatusRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFoldersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFoldersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFoldersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFoldersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFoldersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderDlg.ModifyNetFolderDlgClient;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg.ShareThisDlgMode;

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
public class ManageNetFoldersDlg extends DlgBox
	implements
		NetFolderCreatedEvent.Handler,
		NetFolderModifiedEvent.Handler
{
	private CellTable<NetFolder> m_netFoldersTable;
    private MultiSelectionModel<NetFolder> m_selectionModel;
	private ListDataProvider<NetFolder> m_dataProvider;
	private VibeSimplePager m_pager;
	private List<NetFolder> m_listOfNetFolders;
	private ModifyNetFolderDlg m_modifyNetFolderDlg;
    private int m_width;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
		TeamingEvents.NET_FOLDER_CREATED,
		TeamingEvents.NET_FOLDER_MODIFIED
	};
	

	/**
	 * Callback interface to interact with the "manage net folders" dialog
	 * asynchronously after it loads. 
	 */
	public interface ManageNetFoldersDlgClient
	{
		void onSuccess( ManageNetFoldersDlg mnfDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private ManageNetFoldersDlg(
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
		createAllDlgContent( GwtTeaming.getMessages().manageNetFoldersDlg_Header(), null, null, null );
	}

	/**
	 * Add the given list of Net Folders to the dialog
	 */
	private void addNetFolders( List<NetFolder> listOfNetFolders )
	{
		m_listOfNetFolders = listOfNetFolders;
		
		if ( m_dataProvider == null )
		{
			m_dataProvider = new ListDataProvider<NetFolder>( m_listOfNetFolders );
			m_dataProvider.addDataDisplay( m_netFoldersTable );
		}
		else
		{
			m_dataProvider.setList( m_listOfNetFolders );
			m_dataProvider.refresh();
		}
		
		// Clear all selections.
		m_selectionModel.clear();
		
		// Go to the first page
		m_pager.firstPage();
		
		// Tell the table how many net folders we have.
		m_netFoldersTable.setRowCount( m_listOfNetFolders.size(), true );
	}

	/**
	 * Check the sync status of each net folder that has a sync in progress.
	 */
	private void checkSyncStatus()
	{
		HashSet<NetFolder> listOfNetFoldersToCheck;
		
		listOfNetFoldersToCheck = new HashSet<NetFolder>();
		
		if ( m_listOfNetFolders != null )
		{
			for ( NetFolder nextFolder : m_listOfNetFolders )
			{
				if ( nextFolder.getStatus() == NetFolderStatus.SYNC_IN_PROGRESS &&
					 nextFolder.getStatusTicketId() != null )
				{
					listOfNetFoldersToCheck.add( nextFolder );
				}
			}
		}
		
		if ( listOfNetFoldersToCheck.size() > 0 )
		{
			CheckNetFoldersStatusCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;

			// Create the callback that will be used when we issue an ajax call
			// to sync the net folders.
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( final Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
											t,
											GwtTeaming.getMessages().rpcFailure_CheckNetFoldersStatus() );
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
							CheckNetFoldersStatusRpcResponseData responseData;
							
							responseData = (CheckNetFoldersStatusRpcResponseData) response.getResponseData();
							
							if ( responseData != null )
							{
								Set<NetFolder> listOfNetFolders;
								
								listOfNetFolders = responseData.getListOfNetFolders();
								
								// Update the status of each of the folders.
								updateFolderStatus( listOfNetFolders );
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Issue an ajax request to sync the list of net folders.
			cmd = new CheckNetFoldersStatusCmd( listOfNetFoldersToCheck );
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
		NetFolderNameCell cell;
		Column<NetFolder,NetFolder> nameCol;
		TextColumn<NetFolder> rootCol;
		TextColumn<NetFolder> relativePathCol;
		FlowPanel menuPanel;
		CellTable.Resources cellTableResources;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VerticalPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "manageNetFoldersDlg_MenuPanel" );
			
			// Add an "Add" button.
			label = new InlineLabel( messages.manageNetFoldersDlg_AddNetFolderLabel() );
			label.addStyleName( "manageNetFoldersDlg_Btn" );
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
							invokeAddNetFolderDlg();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
			
			// Add a "Delete" button.
			label = new InlineLabel( messages.manageNetFoldersDlg_DeleteNetFolderLabel() );
			label.addStyleName( "manageNetFoldersDlg_Btn" );
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
							deleteSelectedNetFolders();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );

			// Add a "Sync" button.
			label = new InlineLabel( messages.manageNetFoldersDlg_SyncLabel() );
			label.addStyleName( "manageNetFoldersDlg_Btn" );
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
							syncSelectedNetFolders();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
		}
		
		// Create the CellTable that will display the list of Net Folders.
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_netFoldersTable = new CellTable<NetFolder>( 20, cellTableResources );
		m_netFoldersTable.setWidth( String.valueOf( m_width-20 ) + "px" );
		
		// Set the widget that will be displayed when there are no Net Folders
		{
			FlowPanel flowPanel;
			InlineLabel noNetFoldersLabel;
			
			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noNetFoldersLabel = new InlineLabel( GwtTeaming.getMessages().manageNetFoldersDlg_NoNetFoldersLabel() );
			flowPanel.add( noNetFoldersLabel );
			
			m_netFoldersTable.setEmptyTableWidget( flowPanel );
		}
		
	    // Add a selection model so we can select net folders.
	    m_selectionModel = new MultiSelectionModel<NetFolder>();
	    m_netFoldersTable.setSelectionModel(
	    									m_selectionModel,
	    									DefaultSelectionEventManager.<NetFolder> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<NetFolder, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<NetFolder, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( NetFolder netFolder )
		        {
            		// Get the value from the selection model.
		            return m_selectionModel.isSelected( netFolder );
		        }
		    };
	        m_netFoldersTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_netFoldersTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Name" column.  The user can click on the text in this column
		// to edit the Net Folder.
		{
			cell = new NetFolderNameCell();
			nameCol = new Column<NetFolder, NetFolder>( cell )
			{
				@Override
				public NetFolder getValue( NetFolder netFolder )
				{
					return netFolder;
				}
			};
		
			nameCol.setFieldUpdater( new FieldUpdater<NetFolder, NetFolder>()
			{
				@Override
				public void update( int index, NetFolder netFolder, NetFolder value )
				{
					invokeModifyNetFolderDlgById( netFolder.getId() );
				}
			} );
			m_netFoldersTable.addColumn( nameCol, messages.manageNetFoldersDlg_NameCol() );
		}
		  
		// Add the "Net Folder Root" column
		rootCol = new TextColumn<NetFolder>()
		{
			@Override
			public String getValue( NetFolder netFolder )
			{
				String rootName;
				
				rootName = netFolder.getNetFolderRootName();
				if ( rootName == null )
					rootName = "";
				
				return rootName;
			}
		};
		m_netFoldersTable.addColumn( rootCol, messages.manageNetFoldersDlg_ServerCol() );
		
		// Add the "Relative Path" column
		relativePathCol = new TextColumn<NetFolder>()
		{
			@Override
			public String getValue( NetFolder netFolder )
			{
				String relativePath;
				
				relativePath = netFolder.getRelativePath();
				if ( relativePath == null )
					relativePath = "";
				
				return relativePath;
			}
		};
		m_netFoldersTable.addColumn( relativePathCol, messages.manageNetFoldersDlg_RelativePathCol() );
		
		// Create a pager
		{
			m_pager = new VibeSimplePager();
			m_pager.setDisplay( m_netFoldersTable );
		}

		mainPanel.add( menuPanel );
		mainPanel.add( m_netFoldersTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_pager );
		mainPanel.setCellHeight( m_pager, "100%" );

		return mainPanel;
	}
	

	/**
	 * Delete the selected net folders.
	 */
	private void deleteSelectedNetFolders()
	{
		Set<NetFolder> selectedFolders;
		Iterator<NetFolder> folderIterator;
		String folderNames;
		int count = 0;
		
		folderNames = "";
		
		selectedFolders = getSelectedNetFolders();
		if ( selectedFolders != null )
		{
			// Get a list of all the selected net folder names
			folderIterator = selectedFolders.iterator();
			while ( folderIterator.hasNext() )
			{
				NetFolder nextFolder;
				String text;
				
				nextFolder = folderIterator.next();
				
				text = nextFolder.getName();
				folderNames += "\t" + text + "\n";
				
				++count;
			}
		}
		
		// Do we have any net folders to delete?
		if ( count > 0 )
		{
			String msg;
			
			// Yes, ask the user if they want to delete the selected net folders?
			msg = GwtTeaming.getMessages().manageNetFoldersDlg_ConfirmDelete( folderNames );
			if ( Window.confirm( msg ) )
			{
				deleteNetFoldersFromServer( selectedFolders );
			}
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().manageNetFoldersDlg_SelectFoldersToDelete() );
		}
	}
	
	/**
	 * 
	 */
	private void deleteNetFoldersFromServer( final Set<NetFolder> listOfNetFoldersToDelete )
	{
		if ( listOfNetFoldersToDelete != null && listOfNetFoldersToDelete.size() > 0 )
		{
			DeleteNetFoldersCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;
	
			// Create the callback that will be used when we issue an ajax call
			// to delete the net folders.
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
									GwtTeaming.getMessages().rpcFailure_DeleteNetFolders() );

							// Clear all selections.
							m_selectionModel.clear();
							
							// Update the table to reflect the fact that we deleted a net folder.
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
							// Spin through the list of net folders we just deleted and remove
							// them from the table.
							for (NetFolder nextFolder : listOfNetFoldersToDelete)
							{
								NetFolder netFolder;
								
								netFolder = findNetFolderById( nextFolder.getId() );
								if ( netFolder != null )
									m_listOfNetFolders.remove( netFolder );
							}
							
							// Clear all selections.
							m_selectionModel.clear();
							
							// Update the table to reflect the fact that we deleted a net folder.
							m_dataProvider.refresh();

							// Tell the table how many net folders we have.
							m_netFoldersTable.setRowCount( m_listOfNetFolders.size(), true );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Update the table to reflect the fact that net folder deletion is in progress
			m_dataProvider.refresh();

			// Issue an ajax request to delete the list of net folders.
			cmd = new DeleteNetFoldersCmd( listOfNetFoldersToDelete );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}	
	}

	/**
	 * Find the given net folder in our list of net folders .
	 */
	private NetFolder findNetFolderById( Long id )
	{
		if ( m_listOfNetFolders != null && id != null )
		{
			for (NetFolder nextFolder : m_listOfNetFolders)
			{
				if ( id.compareTo( nextFolder.getId() ) == 0 )
					return nextFolder;
			}
		}
		
		// If we get here we did not find the net folder.
		return null;
	}
	
	/**
	 * Issue an ajax request to get a list of all the net folders.
	 */
	private void getAllNetFoldersFromServer()
	{
		GetNetFoldersCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		// Create the callback that will be used when we issue an ajax call to get all the net folders.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetAllNetFolders() );
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
						GetNetFoldersRpcResponseData responseData;
						
						responseData = (GetNetFoldersRpcResponseData) response.getResponseData();
						
						// Add the net folders to the ui
						if ( responseData != null )
						{
							List<NetFolder> listOfNetFolders;
							
							listOfNetFolders = responseData.getListOfNetFolders();
							addNetFolders( listOfNetFolders );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Issue an ajax request to get a list of all the net folders.
		cmd = new GetNetFoldersCmd();
		cmd.setIncludeHomeDirNetFolders( false );
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
		helpData.setPageId( "netfolders" );
		
		return helpData;
	}

	/**
	 * Return a list of selected net folders.
	 */
	public Set<NetFolder> getSelectedNetFolders()
	{
		return m_selectionModel.getSelectedSet();
	}
	
	/**
	 * 
	 */
	public void init()
	{
		// Issue an ajax request to get a list of all the net folders
		getAllNetFoldersFromServer();
	}
	
	/**
	 * Invoke the "Add Net Folder" dialog.
	 */
	private void invokeAddNetFolderDlg()
	{
		invokeModifyNetFolderDlg( null );
	}
	
	/**
	 * 
	 */
	private void invokeModifyNetFolderDlgById( Long id )
	{
		// Are we editing an existing net folder?
		if ( id != null )
		{
			GetNetFolderCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;

			// Yes
			// Create the callback that will be used when we issue an ajax call to get the net folder.
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetNetFolder() );
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
							NetFolder netFolder;
							
							netFolder = (NetFolder) response.getResponseData();
							
							// Invoke the modify net folder dialog
							if ( netFolder != null )
							{
								invokeModifyNetFolderDlg( netFolder );
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Issue an ajax request to get the net folder.
			cmd = new GetNetFolderCmd();
			cmd.setId( id );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}

	/**
	 * 
	 */
	private void invokeModifyNetFolderDlg( final NetFolder netFolder )
	{
		int x;
		int y;
		
		// Get the position of this dialog.
		x = getAbsoluteLeft() + 50;
		y = getAbsoluteTop() + 50;
		
		if ( m_modifyNetFolderDlg == null )
		{
			ModifyNetFolderDlg.createAsync(
										true, 
										false,
										x, 
										y,
										new ModifyNetFolderDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final ModifyNetFolderDlg mnfDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_modifyNetFolderDlg = mnfDlg;
							
							m_modifyNetFolderDlg.init( netFolder );
							m_modifyNetFolderDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			m_modifyNetFolderDlg.init( netFolder );
			m_modifyNetFolderDlg.setPopupPosition( x, y );
			m_modifyNetFolderDlg.show();
		}
	}

	/**
	 * Handles the NetFolderCreatedEvent received by this class
	 */
	@Override
	public void onNetFolderCreated( NetFolderCreatedEvent event )
	{
		final NetFolder netFolder;

		// Get the newly created net folder.
		netFolder = event.getNetFolder();
		
		if ( netFolder != null )
		{
			Scheduler.ScheduledCommand cmd;
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Add the net folder as the first item in the list.
					m_listOfNetFolders.add( 0, netFolder );
					
					// Update the table to reflect the new folder we just created.
					m_dataProvider.refresh();
					
					// Go to the first page.
					m_pager.firstPage();
					
					// Select the newly created folder.
					m_selectionModel.setSelected( netFolder, true );

					// Tell the table how many folders we have.
					m_netFoldersTable.setRowCount( m_listOfNetFolders.size(), true );
					
					// Ask the user if they want to sync the newly created net folder.
					if ( Window.confirm( GwtTeaming.getMessages().manageNetFoldersDlg_PromptForSync() ) )
					{
						HashSet<NetFolder> folders;
						
						folders = new HashSet<NetFolder>();
						folders.add( netFolder );
						syncNetFolders( folders );
					}
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	

	/**
	 * Handles the NetFolderModifiedEvent received by this class
	 */
	@Override
	public void onNetFolderModified( NetFolderModifiedEvent event )
	{
		NetFolder netFolder;
		
		// Get the NetFolder passed in the event.
		netFolder = event.getNetFolder();
		
		if ( netFolder != null )
		{
			Long id;
			NetFolder existingFolder;
			
			// Find this folder in our list of folders.
			id = netFolder.getId();
			existingFolder = findNetFolderById( id );
			
			if ( existingFolder != null )
			{
				// Update the folder object with the new data.
				existingFolder.copy( netFolder );
				
				// Update the table to reflect the fact that this net folder has been modified.
				m_dataProvider.refresh();
			} 
		}
	}

	/**
	 * Sync the given net folders
	 */
	private void syncNetFolders( final Set<NetFolder> selectedFolders )
	{
		SyncNetFoldersCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		if ( selectedFolders == null || selectedFolders.size() == 0 )
			return;
		
		// Mark each of the selected net folders as "sync in progress"
		for ( NetFolder nextNetFolder : selectedFolders )
		{
			nextNetFolder.setStatus( NetFolderStatus.SYNC_IN_PROGRESS );
		}

		// Create the callback that will be used when we issue an ajax call
		// to sync the net folders.
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
								GwtTeaming.getMessages().rpcFailure_SyncNetFolders() );

						// Update the table to reflect the fact that we sync'd a net folder.
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
						SyncNetFoldersRpcResponseData responseData;
						
						responseData = (SyncNetFoldersRpcResponseData) response.getResponseData();
						
						if ( responseData != null )
						{
							Set<NetFolder> listOfNetFolders;
							
							listOfNetFolders = responseData.getListOfNetFolders();
							
							updateFolderStatus( listOfNetFolders );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Update the table to reflect the fact that net folder sync is in progress
		m_dataProvider.refresh();

		// Issue an ajax request to sync the list of net folders.
		cmd = new SyncNetFoldersCmd( selectedFolders );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Sync the selected net folders.
	 */
	private void syncSelectedNetFolders()
	{
		Set<NetFolder> selectedFolders;
		
		selectedFolders = getSelectedNetFolders();

		// Do we have any net folders to sync?
		if ( selectedFolders == null || selectedFolders.size() == 0 )
		{
			// No
			Window.alert( GwtTeaming.getMessages().manageNetFoldersDlg_SelectFoldersToSync() );
			return;
		}
		
		syncNetFolders( selectedFolders );
	}
	
	/**
	 * For each net folder in the given list, find the net folder in our internal list
	 * and update its status
	 */
	private void updateFolderStatus( Set<NetFolder> listOfNetFolders )
	{
		if ( listOfNetFolders != null )
		{
			boolean checkAgain;
			
			checkAgain = false;
			
			for ( NetFolder nextNetFolder : listOfNetFolders )
			{
				NetFolder ourNetFolder;
				
				ourNetFolder = findNetFolderById( nextNetFolder.getId() );
				
				if ( ourNetFolder != null )
				{
					NetFolderStatus status;
					String statusTicketId;

					status = nextNetFolder.getStatus();
					ourNetFolder.setStatus( status );
					
					statusTicketId = nextNetFolder.getStatusTicketId();
					ourNetFolder.setStatusTicketId( statusTicketId );
					
					// Was this net folder deleted as a result of the sync process?
					if ( status == NetFolderStatus.DELETED_BY_SYNC_PROCESS )
					{
						// Yes
						// Find this net folder in our list and remove it.
						m_listOfNetFolders.remove( ourNetFolder );
					}
					else if ( status != NetFolderStatus.READY )
					{
						checkAgain = true;
					}
				}
			}
			
			// Update the table to reflect the fact that we sync'd a net folder.
			m_dataProvider.refresh();

			// Tell the table how many net folders we have.
			m_netFoldersTable.setRowCount( m_listOfNetFolders.size(), true );
			
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
	 * Loads the ManageNetFolderDlg split point and returns an instance
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
							final ManageNetFoldersDlgClient mnfDlgClient )
	{
		GWT.runAsync( ManageNetFoldersDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ManageNetFoldersDlg() );
				if ( mnfDlgClient != null )
				{
					mnfDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ManageNetFoldersDlg mnfDlg;
				
				mnfDlg = new ManageNetFoldersDlg(
												autoHide,
												modal,
												left,
												top,
												width,
												height );
				mnfDlgClient.onSuccess( mnfDlg );
			}
		});
	}
}
