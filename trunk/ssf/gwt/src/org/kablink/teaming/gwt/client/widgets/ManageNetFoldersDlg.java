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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolder.NetFolderSyncStatus;
import org.kablink.teaming.gwt.client.binderviews.QuickFilter;
import org.kablink.teaming.gwt.client.datatable.NetFolderNameCell;
import org.kablink.teaming.gwt.client.datatable.NetFolderSyncStatusCell;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.datatable.VibeCheckboxCell;
import org.kablink.teaming.gwt.client.datatable.VibeCheckboxCell.VibeCheckboxData;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderModifiedEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.CheckNetFoldersStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CheckNetFoldersStatusRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFolderResult;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFolderRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFoldersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFoldersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFoldersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetPersonalPrefsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StopSyncNetFoldersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StopSyncNetFoldersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFoldersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SyncNetFoldersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderDlg.ModifyNetFolderDlgClient;
import org.kablink.teaming.gwt.client.widgets.NetFolderSyncStatisticsDlg.NetFolderSyncStatisticsDlgClient;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * ?
 *  
 * @author jwootton
 */
public class ManageNetFoldersDlg extends DlgBox
	implements
		NetFolderCreatedEvent.Handler,
		NetFolderModifiedEvent.Handler,
		QuickFilterEvent.Handler
{
	private CellTable<NetFolder> m_netFoldersTable;
    private MultiSelectionModel<NetFolder> m_selectionModel;
	private AsyncDataProvider<NetFolder> m_dataProvider;
	private VibeSimplePager m_pager;
	private int m_totalCount;
	private QuickFilter m_quickFilter;
	private PopupMenu m_filterPopupMenu;
	private VibeMenuItem m_showHomeDirsMenuItem;
	private Command m_toggleShowHomeDirsCmd;
	private String m_currentFilterStr = null;
	private SelectAllHeader m_selectAllHeader;
	private List<NetFolder> m_listOfNetFolders;
	private ModifyNetFolderDlg m_modifyNetFolderDlg;
	private NetFolderSyncStatisticsDlg m_netFolderSyncStatisticsDlg;
	private int m_width;
	private Timer m_timer;
	private boolean m_firstTime = true;
	private Long m_idOfNetFolderToSelect = null;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
		TeamingEvents.NET_FOLDER_CREATED,
		TeamingEvents.NET_FOLDER_MODIFIED,
		TeamingEvents.QUICK_FILTER
	};
	
	// MANAGE_NET_FOLDER_ID is used to tell the QuickFilter widget who it is dealing with.
	private static final long MANAGE_NET_FOLDERS_ID = -201;


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
	private void addNetFolders( int startIndex, List<NetFolder> listOfNetFolders )
	{
		m_listOfNetFolders = listOfNetFolders;
		
		// Clear all selections.
		m_selectionModel.clear();
		
		m_dataProvider.updateRowData( startIndex, listOfNetFolders );
		
		// Do we have a net folder we should select?
		if ( m_idOfNetFolderToSelect != null )
		{
			// Yes
			if ( m_listOfNetFolders != null )
			{
				for ( NetFolder nextNetFolder : m_listOfNetFolders )
				{
					if ( m_idOfNetFolderToSelect.equals( nextNetFolder.getId() ) )
					{
						m_selectionModel.setSelected( nextNetFolder, true );
						break;
					}
				}
			}
			
			m_idOfNetFolderToSelect = null;
		}
	}

	/**
	 * 
	 */
	private void checkSyncStatus( HashSet<NetFolder> listOfNetFoldersToCheck )
	{
		if ( listOfNetFoldersToCheck != null && listOfNetFoldersToCheck.size() > 0 )
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
	 * Check the sync status of each net folder
	 */
	private void checkSyncStatus()
	{
		HashSet<NetFolder> listOfNetFoldersToCheck;
		
		listOfNetFoldersToCheck = new HashSet<NetFolder>();
		
		if ( m_listOfNetFolders != null )
		{
			for ( NetFolder nextFolder : m_listOfNetFolders )
			{
				listOfNetFoldersToCheck.add( nextFolder );
			}
		}
		
		if ( listOfNetFoldersToCheck.size() > 0 )
			checkSyncStatus( listOfNetFoldersToCheck );
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		final GwtTeamingMessages messages;
		VerticalPanel mainPanel = null;
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
			
			// Add a "Stop sync" button
			label = new InlineLabel( messages.manageNetFoldersDlg_StopSyncLabel() );
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
							stopSyncSelectedNetFolders();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );

			// Add filtering controls
			{
				FlowPanel mainFilterPanel;
				FlowPanel qfPanel;
				
				mainFilterPanel = new FlowPanel();
				mainFilterPanel.addStyleName( GwtClientHelper.jsIsIE() ? "displayInline" : "displayInlineBlock" );
				mainFilterPanel.addStyleName( "manageNetFoldersDlg_MainFilterPanel" );
				
				qfPanel = new FlowPanel();
				qfPanel.addStyleName( "manageNetFoldersDlg_QuickFilterPanel" );
				qfPanel.addStyleName( GwtClientHelper.jsIsIE() ? "displayInline" : "displayInlineBlock" );
				
				mainFilterPanel.add( qfPanel );
				
				m_quickFilter = new QuickFilter( MANAGE_NET_FOLDERS_ID );
				qfPanel.add( m_quickFilter );
				
				// Add an image the user can click on to invoke the menu items that allows the
				// user to select if they want to display home dir net folders.
				{
					FlowPanel tmpPanel;
					final Anchor a;
					Image filterImg;
					
					tmpPanel = new FlowPanel();
					tmpPanel.addStyleName( GwtClientHelper.jsIsIE() ? "displayInline" : "displayInlineBlock" );
					
					a = new Anchor();
					a.setTitle( messages.manageNetFoldersDlg_FilterOptionsAlt() );
					filterImg = new Image( GwtTeaming.getImageBundle().menuButton() );
					filterImg.addStyleName( "vibe-filterMenuImg" );
					filterImg.getElement().setAttribute( "align", "absmiddle" );
					a.getElement().appendChild( filterImg.getElement() );
					
					// Create the popup menu that will be displayed when the user clicks on the image.
					{
						m_filterPopupMenu = new PopupMenu( true, false, true );
						m_filterPopupMenu.addStyleName( "vibe-filterMenuBarDropDown" );
						
						// Add the "Show Home Directories" menu item.
						{
							m_toggleShowHomeDirsCmd = new Command()
							{
								@Override
								public void execute()
								{
									handleShowHomeDirectoriesMenuItem();
								}
							};
							
							m_showHomeDirsMenuItem = m_filterPopupMenu.addMenuItem(
																			m_toggleShowHomeDirsCmd,
																			null,
																			messages.manageNetFoldersDlg_ShowHomeDirsLabel() );
						}
					}
					
					a.addClickHandler( new ClickHandler()
					{
						@Override
						public void onClick( ClickEvent event )
						{
							m_filterPopupMenu.showRelativeToTarget( a );
						}
					} );

					tmpPanel.add( a );
					mainFilterPanel.add( tmpPanel );
				}
				
				menuPanel.add( mainFilterPanel );
			}
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
			Column<NetFolder, VibeCheckboxData> ckboxColumn;
			VibeCheckboxCell ckboxCell;
			
			// Create a checkbox that will be in the column header and will be used to select/deselect
			// net folders
			{
				CheckboxCell cbSelectAllCell;

				cbSelectAllCell = new CheckboxCell();
				m_selectAllHeader = new SelectAllHeader( cbSelectAllCell );
				m_selectAllHeader.setUpdater( new ValueUpdater<Boolean>()
				{
					@Override
					public void update( Boolean checked )
					{
						List<NetFolder> netFolders;

						netFolders = m_netFoldersTable.getVisibleItems();
						if ( netFolders != null )
						{
							for ( NetFolder nextNetFolder : netFolders )
							{
								m_selectionModel.setSelected( nextNetFolder, checked );
							}
						}
					}
				} );
			}
			
            ckboxCell = new VibeCheckboxCell();
		    ckboxColumn = new Column<NetFolder, VibeCheckboxData>( ckboxCell )
            {
            	@Override
		        public VibeCheckboxData getValue( NetFolder netFolder )
		        {
            		// Get the value from the selection model.
		            return new VibeCheckboxData( m_selectionModel.isSelected( netFolder ), false );
		        }
		    };
		    
		    // Add a field updater so when the user checks/unchecks the checkbox next to a
		    // net folder we will uncheck the "select all" checkbox that is in the header.
		    {
		    	ckboxColumn.setFieldUpdater( new FieldUpdater<NetFolder,VibeCheckboxData>()
		    	{
		    		@Override
		    		public void update( int index, NetFolder netFolder, VibeCheckboxData data )
		    		{
		    			Boolean checked = data.getValue();
		    			m_selectionModel.setSelected( netFolder,  checked );
		    			
		    			if ( checked == false )
		    			{
		    				m_selectAllHeader.setValue( false );
		    			}
		    		}
		    	} );
		    }
		    
	        m_netFoldersTable.addColumn( ckboxColumn, m_selectAllHeader );
		    m_netFoldersTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Name" column.  The user can click on the text in this column
		// to edit the Net Folder.
		{
			NetFolderNameCell cell;

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
		  
		// Add the "Sync status" column.  The user can click on the image in this column
		// to invoke the "sync details" dialog.
		{
			NetFolderSyncStatusCell cell;
			Column<NetFolder,NetFolder> statusCol;

			cell = new NetFolderSyncStatusCell();
			statusCol = new Column<NetFolder, NetFolder>( cell )
			{
				@Override
				public NetFolder getValue( NetFolder netFolder )
				{
					return netFolder;
				}
			};
		
			statusCol.setFieldUpdater( new FieldUpdater<NetFolder, NetFolder>()
			{
				@Override
				public void update( int index, NetFolder netFolder, NetFolder value )
				{
					invokeNetFolderSyncStatisticsDlg( netFolder );

					// Update the sync status of this net folder
					{
						HashSet<NetFolder> listOfNetFoldersToCheck;
						
						listOfNetFoldersToCheck = new HashSet<NetFolder>();
						
						listOfNetFoldersToCheck.add( netFolder );
						
						checkSyncStatus( listOfNetFoldersToCheck );
					}
				}
			} );
			m_netFoldersTable.addColumn( statusCol, messages.manageNetFoldersDlg_SyncStatusCol() );
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
		
		// Put the table that holds the list of net folders into a scrollable div
		{
			FlowPanel panel;
		
			panel = new FlowPanel();
			panel.addStyleName( "manageNetFoldersDlg_ListOfNetFoldersPanel" );
			panel.add( m_netFoldersTable );
			mainPanel.add( panel );
		}
		
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
			if ( selectedFolders.size() < 15 )
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
			else
			{
				folderNames = GwtTeaming.getMessages().manageNetFoldersDlg_nNetFoldersToDelete( selectedFolders.size() );
				count = selectedFolders.size();
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
	private void deleteNetFoldersFromServer( Set<NetFolder> listOfNetFoldersToDelete )
	{
		if ( listOfNetFoldersToDelete != null && listOfNetFoldersToDelete.size() > 0 )
		{
			DeleteNetFoldersCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;
	
			// Mark each of the selected net folders as "deleting"
			for ( NetFolder nextNetFolder : listOfNetFoldersToDelete )
			{
				nextNetFolder.setStatus( NetFolderSyncStatus.DELETE_IN_PROGRESS );
			}

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
							int numDeleted = 0;

							if ( response != null &&
								 response.getResponseData() instanceof DeleteNetFolderRpcResponseData )
							{
								DeleteNetFolderRpcResponseData responseData;
								HashMap<NetFolder,DeleteNetFolderResult> results;
								
								responseData = (DeleteNetFolderRpcResponseData) response.getResponseData();
								results = responseData.getResults();
								if ( results != null )
								{
									Set<Entry<NetFolder,DeleteNetFolderResult>> entrySet;
									
									entrySet = results.entrySet();
									if ( entrySet != null )
									{
										FlowPanel errorPanel;
										Label label;
										boolean showErrorPanel = false;
										
										// Get the panel that holds the errors.
										errorPanel = getErrorPanel();
										errorPanel.clear();
										
										for ( Entry<NetFolder,DeleteNetFolderResult> nextEntry : entrySet )
										{
											NetFolder nextNetFolder;
											NetFolder ourNetFolder;
											DeleteNetFolderResult result;
											
											nextNetFolder = nextEntry.getKey();
											ourNetFolder = findNetFolderById( nextNetFolder.getId() );
											result = nextEntry.getValue();
											
											switch ( result.getStatus() )
											{
											case DELETE_FAILED:
											case DELETE_FAILED_SYNC_IN_PROGRESS:
												showErrorPanel = true;
												label = new Label( GwtTeaming.getMessages().manageNetFoldersDlg_DeleteNetFolderErrorMsg( nextNetFolder.getName(), result.getErrorMsg() ) );
												label.addStyleName( "dlgErrorLabel" );
												errorPanel.add( label );
												
												if ( ourNetFolder != null )
													ourNetFolder.setStatus( NetFolderSyncStatus.DELETE_FAILED );
												
												break;
											
											case SUCCESS:
												if ( ourNetFolder != null )
												{
													m_listOfNetFolders.remove( ourNetFolder );
													++numDeleted;
												}
												
												break;
											}
										}
										
										// Show the error panel if we encountered errors deleting the net folders.
										if ( showErrorPanel )
											showErrorPanel();
									}
								}
							}
							
							// Clear all selections.
							m_selectionModel.clear();
							
							if ( numDeleted > 0 )
							{
								boolean refreshCurrentPage = true;
								
								// Do we have any net folders left on this page?
								if ( m_listOfNetFolders.size() == 0 )
								{
									// No
									// Are we on the last page?
									if ( m_pager.getPage() == (m_pager.getPageCount()-1) )
									{
										// Yes
										// Are we on the first page?
										if ( m_pager.getPage() > 0 )
										{
											// No, move to the previous page.
											refreshCurrentPage = false;
											m_pager.previousPage();
										}
									}
								}
								
								if ( refreshCurrentPage )
								{
									boolean showHomeDir;
									int start;
									
									// Get a fresh list starting with our current page #.
									start = m_netFoldersTable.getPageStart();
									showHomeDir = m_showHomeDirsMenuItem.isChecked();
									getPageOfNetFoldersFromServer( m_currentFilterStr, showHomeDir, start );
								}
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};

			// Update the table to reflect the fact that net folder deletion is in progress
			refresh();

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
	 * Issue an ajax request to get a page of net folders.
	 */
	private void getPageOfNetFoldersFromServer(
		String filter,
		boolean includeHomeDirectories,
		final int startIndex )
	{
		GetNetFoldersCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		m_currentFilterStr = filter;
		
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
						
						hideStatusMsg();
						
						responseData = (GetNetFoldersRpcResponseData) response.getResponseData();
						
						// Add the net folders to the ui
						if ( responseData != null )
						{
							List<NetFolder> listOfNetFolders;

							m_totalCount = responseData.getTotalCount();
							m_dataProvider.updateRowCount( m_totalCount, true );

							listOfNetFolders = responseData.getListOfNetFolders();
							addNetFolders( startIndex, listOfNetFolders );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		showStatusMsg( GwtTeaming.getMessages().manageNetFoldersDlg_SearchingForNetFolders() );
		
		// Issue an ajax request to get a list of all the net folders.
		cmd = new GetNetFoldersCmd();
		cmd.setIncludeHomeDirNetFolders( includeHomeDirectories );
		cmd.setFilter( filter );
		cmd.setPageSize( m_pager.getPageSize() );
		cmd.setStartIndex( startIndex );
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
	 * Issue an rpc request to get the page size from the user preferences.
	 */
	private void getPageSize()
	{
		GetPersonalPrefsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		// Create a callback that will be called when we get the personal preferences.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetPersonalPreferences() );
				
				m_pager.setPageSize( 125 );
				init2();
			}// end onFailure()
	
			/**
			 * We successfully retrieved the user's personal preferences.  Now invoke the "edit personal preferences" dialog.
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				GwtPersonalPreferences personalPrefs;
				ScheduledCommand cmd;

				personalPrefs = (GwtPersonalPreferences) response.getResponseData();
				m_pager.setPageSize( personalPrefs.getNumEntriesPerPage() );
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						init2();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}// end onSuccess()
		};

		// Issue an ajax request to get the personal preferences from the db.
		cmd = new GetPersonalPrefsCmd();
		GwtClientHelper.executeCommand( cmd, rpcCallback );
	}
	
	/**
	 * Return a list of selected net folders.
	 */
	public Set<NetFolder> getSelectedNetFolders()
	{
		return m_selectionModel.getSelectedSet();
	}
	
	/**
	 * Go to the first page and request a page of net folders.
	 */
	private void gotoFirstPage()
	{
		// Are we already on the first page?
		if ( m_pager.getPage() == 0 )
		{
			// Yes
			getPageOfNetFoldersFromServer( m_currentFilterStr, m_showHomeDirsMenuItem.isChecked(), 0 );
		}
		else
		{
			// Calling firstPage() will trigger onRangeChanged() to be called.
			m_pager.firstPage();
		}
	}
	
	/**
	 * This method gets called when the user clicks on the "Show Home Directories" menu item.
	 * If we are currently showing home directories we will stop showing them.  If we are not
	 * showing them we will start showing them. 
	 */
	private void handleShowHomeDirectoriesMenuItem()
	{
		boolean currentState;
		
		currentState = m_showHomeDirsMenuItem.isChecked();
		m_showHomeDirsMenuItem.setCheckedState( !currentState );
		
		// Go to the first page and request a page of net folders.
		gotoFirstPage();
	}
	
	/**
	 * 
	 */
	public void init()
	{
		hideErrorPanel();
		getPageSize();
	}
	
	/**
	 * 
	 */
	private void init2()
	{
		if ( m_dataProvider == null )
		{
			m_dataProvider = new AsyncDataProvider<NetFolder>()
			{
				@Override
				protected void onRangeChanged( final HasData<NetFolder> display )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Is this the first time running this dialog?
							if ( m_firstTime )
							{
								m_firstTime = false;
								getPageOfNetFoldersFromServer( m_currentFilterStr, false, 0 );
							}
							else
							{
								int start;
								boolean showHomeDir;

								// No, get the next page.
								start = display.getVisibleRange().getStart();
								showHomeDir = m_showHomeDirsMenuItem.isChecked();
								getPageOfNetFoldersFromServer( m_currentFilterStr, showHomeDir, start );
							}
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			m_dataProvider.addDataDisplay( m_netFoldersTable );
		}
		else
		{
			// Go to the first page and request a page of net folders.
			gotoFirstPage();
		}
		
		// Check in the sync status every minute.
		m_timer = new Timer()
		{
			@Override
			public void run()
			{
				checkSyncStatus();
			}
		};
		m_timer.scheduleRepeating( 60000 );

		// Set the height of the panel that holds the list of net folders
		{
			FlowPanel panel;
			int height;
			
			panel = (FlowPanel)m_netFoldersTable.getParent();
			
			// Figure out how tall the panel should be.
			height = getOffsetHeight();
			height -= panel.getAbsoluteTop();
			height -= m_pager.getOffsetHeight();
			height += 10;
			panel.getElement().getStyle().setHeight( height, Unit.PX );
		}
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
			// No, create it.
			ModifyNetFolderDlg.createDlg(
										false,
										true,
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

					m_modifyNetFolderDlg = mnfDlg;

					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							invokeModifyNetFolderDlg( netFolder );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
		}
		else
		{
			ModifyNetFolderDlg.initAndShow( m_modifyNetFolderDlg, x, y, null, netFolder, null );
		}
	}

	/**
	 * Invoke the dialog that display all of the sync statistics for the given net folder 
	 */
	private void invokeNetFolderSyncStatisticsDlg( final NetFolder netFolder)
	{
		int x;
		int y;
		
		// Get the position of this dialog.
		x = getAbsoluteLeft() + 50;
		y = getAbsoluteTop() + 50;
		
		if ( m_netFolderSyncStatisticsDlg == null )
		{
			NetFolderSyncStatisticsDlgClient nfssClient;
			
			nfssClient = new NetFolderSyncStatisticsDlgClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( final NetFolderSyncStatisticsDlg nfssDlg )
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							m_netFolderSyncStatisticsDlg = nfssDlg;
							
							m_netFolderSyncStatisticsDlg.init( netFolder );
							m_netFolderSyncStatisticsDlg.show();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			NetFolderSyncStatisticsDlg.createAsync(
											true, 
											false,
											x, 
											y,
											nfssClient );
		}
		else
		{
			m_netFolderSyncStatisticsDlg.init( netFolder );
			m_netFolderSyncStatisticsDlg.setPopupPosition( x, y );
			m_netFolderSyncStatisticsDlg.show();
		}
	}

	/**
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach
		super.onDetach();
		
		// Kill the timer.
		if ( m_timer != null )
		{
			m_timer.cancel();
			m_timer = null;
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
					// Ask the user if they want to sync the newly created net folder.
					if ( Window.confirm( GwtTeaming.getMessages().manageNetFoldersDlg_PromptForSync() ) )
					{
						HashSet<NetFolder> folders;
						
						folders = new HashSet<NetFolder>();
						folders.add( netFolder );
						syncNetFolders( folders );
					}

					// Request the last page of data because that is where the newly created net folder will be
					{
						// Remember the id of the net folder we should select after we refresh
						// the list of net folders.
						m_idOfNetFolderToSelect = netFolder.getId();
						
						++m_totalCount;
						m_dataProvider.updateRowCount( m_totalCount, true );

						// Are we on the last page?
						if ( m_pager.getPage() == (m_pager.getPageCount() -1) ) 
						{
							boolean showHomeDir;
							int start;
							
							// Yes
							// Get a fresh list starting with our current page #.
							start = m_netFoldersTable.getPageStart();
							
							// Is our current page full?
							if ( m_listOfNetFolders.size() == m_pager.getPageSize() )
							{
								// Yes, we have a new last page.
								start += m_pager.getPageSize();
							}
							
							showHomeDir = m_showHomeDirsMenuItem.isChecked();
							getPageOfNetFoldersFromServer( m_currentFilterStr, showHomeDir, start );
							m_pager.lastPage();
						}
						else
						{
							// No, move there.
							m_pager.lastPage();
						}
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
				refresh();
			} 
		}
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
		if ( event.getFolderId().equals( MANAGE_NET_FOLDERS_ID ) )
		{
			Scheduler.ScheduledCommand cmd;
			
			// Yes.  Search for net folders using the filter entered by the user.
			m_currentFilterStr = event.getQuickFilter();
			
			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Go to the first page and request a page of net folders.
					gotoFirstPage();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * Refresh the ui for the current list of net folders we are displaying
	 */
	private void refresh()
	{
		m_dataProvider.updateRowData( m_netFoldersTable.getVisibleRange().getStart(), m_listOfNetFolders );
	}

	/**
	 * 
	 */
	@Override
	public void setPixelSize( int width, int height )
	{
		super.setPixelSize( width, height );
		
		m_width = width;
		m_netFoldersTable.setWidth( String.valueOf( m_width-20 ) + "px" );
	}
	
	/**
	 * Stop the sync the selected net folders.
	 */
	private void stopSyncSelectedNetFolders()
	{
		Set<NetFolder> selectedFolders;
		
		selectedFolders = getSelectedNetFolders();

		// Are there any selected net folders?
		if ( selectedFolders == null || selectedFolders.size() == 0 )
		{
			// No
			Window.alert( GwtTeaming.getMessages().manageNetFoldersDlg_SelectFoldersToStopSync() );
			return;
		}
		
		stopSyncNetFolders( selectedFolders );
	}
	
	/**
	 * Stop the synchronization of the given net folders
	 */
	private void stopSyncNetFolders( final Set<NetFolder> selectedFolders )
	{
		StopSyncNetFoldersCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		if ( selectedFolders == null || selectedFolders.size() == 0 )
			return;
		
		// Create the callback that will be used when we issue an ajax call
		// to stop the sync of the net folders.
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
								GwtTeaming.getMessages().rpcFailure_StopSyncNetFolders() );

						refresh();
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
						StopSyncNetFoldersRpcResponseData responseData;
						
						responseData = (StopSyncNetFoldersRpcResponseData) response.getResponseData();
						
						if ( responseData != null )
						{
							Set<NetFolder> listOfNetFolders;
							
							listOfNetFolders = responseData.getListOfNetFolders();
							
							updateFolderStatus( listOfNetFolders );
							
							Window.alert( GwtTeaming.getMessages().manageNetFoldersDlg_CancelSyncRequested() );
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Issue an ajax request to stop the sync of the list of net folders.
		cmd = new StopSyncNetFoldersCmd( selectedFolders );
		GwtClientHelper.executeCommand( cmd, rpcCallback );
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
		
		// Mark each of the selected net folders as "waiting to be sync'd"
		for ( NetFolder nextNetFolder : selectedFolders )
		{
			nextNetFolder.setStatus( NetFolderSyncStatus.WAITING_TO_BE_SYNCD );
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
						refresh();
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
							
							// The sync has started.  Now issue a request to get the sync status
							{
								Timer timer;
								
								timer = new Timer()
								{
									@Override
									public void run()
									{
										HashSet<NetFolder> listOfNetFoldersToCheck;
										
										listOfNetFoldersToCheck = new HashSet<NetFolder>();
										
										for ( NetFolder nextFolder : selectedFolders )
										{
											listOfNetFoldersToCheck.add( nextFolder );
										}

										checkSyncStatus( listOfNetFoldersToCheck );
									}
								};
								timer.schedule( 5000 );
							}
						}
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};

		// Update the table to reflect the fact that net folder sync is in progress
		refresh();

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
			for ( NetFolder nextNetFolder : listOfNetFolders )
			{
				NetFolder ourNetFolder;
				
				ourNetFolder = findNetFolderById( nextNetFolder.getId() );
				
				if ( ourNetFolder != null )
				{
					NetFolderSyncStatus status;

					status = nextNetFolder.getStatus();
					ourNetFolder.setStatus( status );
				}
			}
			
			// Update the table to reflect the fact that we sync'd a net folder.
			refresh();
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
