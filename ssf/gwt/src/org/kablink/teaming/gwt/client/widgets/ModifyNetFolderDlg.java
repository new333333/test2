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
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.NetFolderCreatedEvent;
import org.kablink.teaming.gwt.client.event.NetFolderModifiedEvent;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.CreateNetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderRootsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetNetFolderRootsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyNetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.web.bindery.event.shared.HandlerRegistration;


/**
 * This dialog can be used to create a net folder or modify a net folder.
 * @author jwootton
 *
 */
public class ModifyNetFolderDlg extends DlgBox
	implements
		EditSuccessfulHandler,
		SearchFindResultsEvent.Handler
{
	private NetFolder m_netFolder;	// If we are modifying a net folder this is the net folder.
	private TextBox m_nameTxtBox;
	private TextBox m_relativePathTxtBox;
	private ListBox m_netFolderRootsListbox;
	private InlineLabel m_noNetFolderRootsLabel;
	private InlineLabel m_parentFolderNameLabel;
	private Button m_editBtn;
	private FlowPanel m_findPanel;
	private FindCtrl m_findCtrl;
	private ScheduleWidget m_scheduleWidget;
	private String m_parentFolderId = null;
	private List<HandlerRegistration> m_registeredEventHandlers;

	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
		TeamingEvents.SEARCH_FIND_RESULTS,
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
		final FlexTable table;
		Label label;
		int nextRow;
		CaptionPanel captionPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
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
		
		// Create the controls for "relative path"
		{
			label = new InlineLabel( messages.modifyNetFolderDlg_RelativePathLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			m_relativePathTxtBox = new TextBox();
			m_relativePathTxtBox.setVisibleLength( 50 );
			table.setWidget( nextRow, 1, m_relativePathTxtBox );
			++nextRow;
		}
		
		// Create the controls for "net folder root"
		{
			FlowPanel flowPanel;
			
			label = new InlineLabel( messages.modifyNetFolderDlg_NetFolderRootLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );

			// Add the listbox where the user can select the net folder root
			flowPanel = new FlowPanel();
			m_netFolderRootsListbox = new ListBox( false );
			m_netFolderRootsListbox.setVisibleItemCount( 1 );
			m_netFolderRootsListbox.setSelectedIndex( 0 );
			flowPanel.add( m_netFolderRootsListbox );
			
			// Add a label that will be displayed when there are no net folder roots to select.
			m_noNetFolderRootsLabel = new InlineLabel( messages.modifyNetFolderDlg_NoNetFolderRootsLabel() );
			flowPanel.add( m_noNetFolderRootsLabel );
			
			table.setWidget( nextRow, 1, flowPanel );
			++nextRow;
		}
		
		// Create the controls needed to select the parent folder
		{
			FlowPanel panel;
			
			// Add a label that will say Current folder:
			label = new InlineLabel( messages.modifyNetFolderDlg_ParentFolderLabel() );
			table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
			
			// Add a label to hold the name of the selected folder.
			m_parentFolderNameLabel = new InlineLabel( GwtTeaming.getMessages().noFolderSelected() );
			m_parentFolderNameLabel.addStyleName( "noFolderSelected" );
			m_parentFolderNameLabel.addStyleName( "marginLeftPoint25em" );
			m_parentFolderNameLabel.addStyleName( "marginright10px" );
			panel = new FlowPanel();
			panel.add( m_parentFolderNameLabel );

			// Add an "Edit" button
			{
				ClickHandler clickHandler;
				
				m_editBtn = new Button( GwtTeaming.getMessages().edit() );
				m_editBtn.addStyleName( "teamingButton" );
				panel.add( m_editBtn );
				
				clickHandler = new ClickHandler()
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
								// Make the find control visible.
								showFindControl();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
					
				};
				m_editBtn.addClickHandler( clickHandler );
			}

			table.setWidget( nextRow, 1, panel );
			++nextRow;
		
			// Add a "find" control
			{
				InlineLabel findLabel;
				
				m_findPanel = new FlowPanel();
				m_findPanel.addStyleName( "findCtrlPanel" );
				m_findPanel.setVisible( false );
				
				// Add an image the user can click on to close the find panel.
				{
					Image img;
					ImageResource imageResource;
					ClickHandler clickHandler;
					
					imageResource = GwtTeaming.getImageBundle().closeX();
					img = new Image( imageResource );
					img.addStyleName( "findCtrlCloseImg" );
					img.getElement().setAttribute( "title", GwtTeaming.getMessages().close() );
					m_findPanel.add( img );
			
					// Add a click handler to the "close" image.
					clickHandler = new ClickHandler()
					{
						@Override
						public void onClick( ClickEvent clickEvent )
						{
							Scheduler.ScheduledCommand cmd;
							
							cmd = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									// Close the panel that holds find controls.
									hideFindControl();
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					};
					img.addClickHandler( clickHandler );
				}
				
				final FlexTable findTable = new FlexTable();
				
				findLabel = new InlineLabel( GwtTeaming.getMessages().find() );
				findLabel.addStyleName( "findCtrlLabel" );
				findTable.setWidget( 0, 0, findLabel );
				
				FindCtrl.createAsync(
								this,
								SearchType.PLACES,
								new FindCtrlClient() {				
					@Override
					public void onUnavailable()
					{
						// Nothing to do.  Error handled in asynchronous provider.
					}
					
					@Override
					public void onSuccess( FindCtrl findCtrl )
					{
						m_findCtrl = findCtrl;
						findTable.setWidget( 0, 1, m_findCtrl );
					}
				} );
				
				m_findPanel.add( findTable );
			}
		}
		
		// Create the controls for defining the sync schedule
		{
			FlowPanel spacerPanel;
			FlowPanel captionPanelMainPanel;
			
			// Add some space
			spacerPanel = new FlowPanel();
			spacerPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );
			table.setHTML( nextRow, 0, spacerPanel.getElement().getString() );
			++nextRow;
			
			captionPanel = new CaptionPanel( messages.modifyNetFolderDlg_SyncScheduleCaption() );
			captionPanel.addStyleName( "modifyNetFolderDlg_SyncScheduleCaptionPanel" );
			
			captionPanelMainPanel = new FlowPanel();
			captionPanel.add( captionPanelMainPanel );

			m_scheduleWidget = new ScheduleWidget( messages.modifyNetFolderDlg_EnableSyncScheduleLabel());
			m_scheduleWidget.addStyleName( "modifyNetFolderDlg_ScheduleWidget" );
			captionPanelMainPanel.add( m_scheduleWidget );
		}
		
		mainPanel.add( table );
		mainPanel.add( m_findPanel );
		mainPanel.add( captionPanel );

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
				
				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().modifyNetFolderDlg_ErrorCreatingNetFolder( caught.toString() );
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
				}
				label = new Label( errMsg );
				label.addStyleName( "dlgErrorLabel" );
				errorPanel.add( label );
				
				showErrorPanel();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				NetFolderCreatedEvent event;
				NetFolder netFolder;
				
				netFolder = (NetFolder) result.getResponseData();
				
				// Fire an event that lets everyone know a net folder was created.
				event = new NetFolderCreatedEvent( netFolder );
				GwtTeaming.fireEvent( event );

				// Close this dialog.
				hide();
			}						
		};
		
		// Issue an rpc request to create the net folder.
		{
			NetFolder netFolder;
			
			netFolder = getNetFolderFromDlg();
			
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
				return false;
			}
			
			// Did the user select a binder to create the net folder in.
			if ( isParentBinderValid() == false )
			{
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
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Return something.  Doesn't matter what because editSuccessful() does the work.
		return Boolean.TRUE;
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
	 * Issue an ajax request to get the folder for the given id.  After we get the folder
	 * we will update the name of the selected folder.
	 */
	private void getParentFolder()
	{
		if ( m_parentFolderId != null )
		{
			GetFolderCmd cmd;
			AsyncCallback<VibeRpcResponse> callback;
			
			callback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolder(),
						m_parentFolderId );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					GwtFolder gwtFolder;
					
					gwtFolder = (GwtFolder) response.getResponseData();
					
					if ( gwtFolder != null )
					{
						// Update the name of the selected folder.
						m_parentFolderNameLabel.setText( gwtFolder.getFolderName() );
						m_parentFolderNameLabel.removeStyleName( "noFolderSelected" );
						m_parentFolderNameLabel.addStyleName( "bold" );
					}
				}
			};
	
			cmd = new GetFolderCmd( null, m_parentFolderId );
			GwtClientHelper.executeCommand( cmd, callback );
		}
	}
	
	/**
	 * Issue an ajax request to get the list of net folder roots the user has
	 * permission to use. 
	 */
	private void getListOfNetFolderRoots()
	{
		GetNetFolderRootsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		// Create the callback that will be used when we issue an ajax call to get the net folder roots.
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetAllNetFolderRoots() );
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
							// No, hide the listbox and display a message that tells the user
							// there aren't any net folder roots they have permission to use.
							m_netFolderRootsListbox.setVisible( false );
							m_noNetFolderRootsLabel.setVisible( true );
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
		netFolder.setRelativePath( getRelativePath() );
		netFolder.setNetFolderRootName( getNetFolderRootName() );
		netFolder.setParentBinderId( getParentBinderId() );
		netFolder.setSyncSchedule( getSyncSchedule() );
		
		if ( m_netFolder != null )
			netFolder.setId( m_netFolder.getId() );
		
		return netFolder;
	}
	
	/**
	 * Return the name of the selected net folder root.
	 */
	private String getNetFolderRootName()
	{
		String rootName = null;
		
		// Are there any net folder roots to select from?
		if ( m_netFolderRootsListbox.getItemCount() > 0 )
		{
			int selectedIndex;

			// Yes
			selectedIndex = m_netFolderRootsListbox.getSelectedIndex();
			if ( selectedIndex >= 0 )
				rootName = m_netFolderRootsListbox.getValue( selectedIndex );
		}
		
		return rootName;
	}
	
	
	/**
	 * Return the name entered by the user.
	 */
	private String getName()
	{
		return m_nameTxtBox.getValue();
	}
	
	/**
	 * Return the id of the binder the user selected to create the net folder in.
	 */
	private Long getParentBinderId()
	{
		if ( m_parentFolderId != null )
			return Long.valueOf( m_parentFolderId );
		
		return null;
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
	private GwtSchedule getSyncSchedule()
	{
		return m_scheduleWidget.getSchedule( );
	}
	
	/**
	 * 
	 */
	private void hideFindControl()
	{
		m_findPanel.setVisible( false );
		if ( m_findCtrl != null )
			m_findCtrl.hideSearchResults();
	}

	/**
	 * Hide all of the controls related to selecting a parent folder
	 */
	private void hideParentFolderControls()
	{
		m_editBtn.setVisible( false );
		m_findPanel.setVisible( false );
	}
	
	/**
	 * 
	 */
	public void init( NetFolder netFolder )
	{
		hideErrorPanel();
		
		m_netFolder = netFolder;

		// Clear existing data in the controls.
		m_nameTxtBox.setValue( "" );
		m_relativePathTxtBox.setValue( "" );
		m_netFolderRootsListbox.clear();
		m_netFolderRootsListbox.setVisible( false );
		m_noNetFolderRootsLabel.setVisible( false );
		m_parentFolderId = null;
		
		// Clear out the sync schedule controls
		m_scheduleWidget.init( null );

		// Are we modifying an existing net folder?
		if ( m_netFolder != null )
		{
			// Yes
			// Update the dialog's header to say "Edit Net Folder"
			setCaption( GwtTeaming.getMessages().modifyNetFolderDlg_EditHeader( m_netFolder.getName() ) );
			
			// Don't let the user edit the name.
			m_nameTxtBox.setValue( netFolder.getName() );
			m_nameTxtBox.setEnabled( false );
			
			m_relativePathTxtBox.setValue( netFolder.getRelativePath() );
			
			// Hide all of the controls related to selecting a parent folder
			hideParentFolderControls();
			
			// Get the name of the parent binder
			m_parentFolderId = m_netFolder.getParentBinderIdAsString();
			getParentFolder();
			
			// Initialize the sync schedule controls
			m_scheduleWidget.init( m_netFolder.getSyncSchedule() );
		}
		else
		{
			// No
			// Update the dialog's header to say "Create Net Folder"
			setCaption( GwtTeaming.getMessages().modifyNetFolderDlg_AddHeader() );
			
			// Enable the "Name" field.
			m_nameTxtBox.setEnabled( true );
			
			// Show all of the controls related to selecting a parent folder.
			showParentFolderControls();
			
			if ( m_findCtrl != null )
				m_findCtrl.setInitialSearchString( "" );

			m_parentFolderNameLabel.setText( GwtTeaming.getMessages().noFolderSelected() );
			m_parentFolderNameLabel.addStyleName( "noFolderSelected" );
			m_parentFolderNameLabel.removeStyleName( "bold" );
		}
		
		// Issue an ajax request to get the list of net folder roots this user has
		// permission to use.
		getListOfNetFolderRoots();
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
	 * Did the user select a binder to create the net folder in?
	 */
	private boolean isParentBinderValid()
	{
		// Are we dealing with an existing net folder?
		if ( m_netFolder == null )
		{
			Long binderId;
			
			// No
			// Did the user select a binder to create the net folder in.
			binderId = getParentBinderId();
			if ( binderId == null )
			{
				// No
				Window.alert( GwtTeaming.getMessages().modifyNetFolderDlg_ParentBinderRequired() );
				return false;
			}
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
				
				// Get the panel that holds the errors.
				errorPanel = getErrorPanel();
				errorPanel.clear();
				
				errMsg = GwtTeaming.getMessages().modifyNetFolderDlg_ErrorModifyingNetFolder( caught.toString() );
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

				// Close this dialog.
				hide();
			}						
		};
		
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
	
	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults( SearchFindResultsEvent event )
	{
		// If the find results aren't for this widget...
		if ( !((Widget) event.getSource()).equals( this ) )
		{
			// ...ignore the event.
			return;
		}
		
		// Make sure we are dealing with a GwtFolder object.
		GwtTeamingItem selectedObj = event.getSearchResults();
		if ( selectedObj instanceof GwtFolder )
		{
			GwtFolder gwtFolder;
			
			gwtFolder = (GwtFolder) selectedObj;
			m_parentFolderId = gwtFolder.getFolderId();
			
			// Hide the find control.
			hideFindControl();
			
			// Issue an ajax request to get information about the selected folder.
			getParentFolder();
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
											REGISTERED_EVENTS,
											this,
											m_registeredEventHandlers );
		}
	}

	/**
	 * Show the find control and give it the focus.
	 */
	private void showFindControl()
	{
		m_findPanel.setVisible( true );

		if ( m_findCtrl != null )
		{
			FocusWidget focusWidget;

			focusWidget = m_findCtrl.getFocusWidget();
			if ( focusWidget != null )
				focusWidget.setFocus( true );
		}
	}
	
	/**
	 * Show all of the controls related to selecting a parent folder
	 */
	private void showParentFolderControls()
	{
		m_editBtn.setVisible( true );
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
	 * Loads the ModifyNetFolderDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final ModifyNetFolderDlgClient mnfDlgClient )
	{
		GWT.runAsync( ModifyNetFolderDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
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
				mnfDlgClient.onSuccess( mnfDlg );
			}
		});
	}
}
