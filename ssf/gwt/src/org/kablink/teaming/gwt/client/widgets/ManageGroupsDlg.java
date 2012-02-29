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

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.VibeCellTableResources;
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
{
	private CellTable<GroupInfo> m_groupsTable;
    private MultiSelectionModel<GroupInfo> m_selectionModel;
	private ListDataProvider<GroupInfo> m_dataProvider;
	private SimplePager m_pager;
	private List<GroupInfo> m_listOfGroups;
	private ModifyGroupDlg m_modifyGroupDlg;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private GroupInfo m_groupBeingEdited;
    private int m_width;
    private int m_height;
	
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
		m_listOfGroups = listOfGroups;
		
		if ( m_dataProvider == null )
		{
			m_dataProvider = new ListDataProvider<GroupInfo>( listOfGroups );
			m_dataProvider.addDataDisplay( m_groupsTable );
		}
		else
		{
			m_dataProvider.setList( listOfGroups );
			m_dataProvider.refresh();
		}
		
		// Clear all selections.
		m_selectionModel.clear();
		
		// Go to the first page
		m_pager.firstPage();
		
		// Tell the table how many groups we have.
		m_groupsTable.setRowCount( listOfGroups.size(), true );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		final GwtTeamingMessages messages;
		VerticalPanel mainPanel = null;
		AnchorCell cell;
		Column<GroupInfo,String> titleCol;
		TextColumn<GroupInfo> nameCol;
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
		m_groupsTable = new CellTable<GroupInfo>( 15, cellTableResources );
		m_groupsTable.getElement().getStyle().setWidth( m_width, Unit.PX );
		m_groupsTable.getElement().getStyle().setHeight( m_height, Unit.PX );
		
	    // Add a selection model so we can select groups.
	    m_selectionModel = new MultiSelectionModel<GroupInfo>();
	    m_groupsTable.setSelectionModel( m_selectionModel, DefaultSelectionEventManager.<GroupInfo> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<GroupInfo, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<GroupInfo, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( GroupInfo groupInfo )
		        {
            		// Get the value from the selection model.
		            return m_selectionModel.isSelected( groupInfo );
		        }
		    };
	        m_groupsTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_groupsTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Title" column.  The user can click on the text in this column
		// to edit the group.
		{
			cell = new AnchorCell();
			titleCol = new Column<GroupInfo, String>( cell )
			{
				@Override
				public String getValue( GroupInfo groupInfo )
				{
					String title;
					
					title = groupInfo.getTitle();
					if ( title == null || title.length() == 0 )
						title = messages.noTitle();
					
					return title;
				}
			};
		
			titleCol.setFieldUpdater( new FieldUpdater<GroupInfo, String>()
			{
				@Override
				public void update( int index, GroupInfo groupInfo, String value )
				{
				    invokeModifyGroupDlg( groupInfo );
				}
			} );
			m_groupsTable.addColumn( titleCol, messages.manageGroupsDlgTitleCol() );
		}
		  
		// Add the "Name" column
		nameCol = new TextColumn<GroupInfo>()
		{
			@Override
			public String getValue( GroupInfo groupInfo )
			{
				String name;
				
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
		Set<GroupInfo> selectedGroups;
		Iterator<GroupInfo> groupIterator;
		String grpNames;
		ArrayList<GroupInfo> listOfGroupsToDelete;
		
		listOfGroupsToDelete = new ArrayList<GroupInfo>();
		
		grpNames = "";
		
		selectedGroups = getSelectedGroups();
		if ( selectedGroups != null )
		{
			// Get a list of all the selected group names
			groupIterator = selectedGroups.iterator();
			while ( groupIterator.hasNext() )
			{
				GroupInfo nextGroup;
				
				nextGroup = groupIterator.next();
				listOfGroupsToDelete.add( nextGroup );
				
				grpNames += "\t" + nextGroup.getTitle() + "\n";
			}
		}
		
		// Do we have any groups to delete?
		if ( listOfGroupsToDelete.size() > 0 )
		{
			String msg;
			
			// Yes, ask the user if they want to delete the selected groups?
			msg = GwtTeaming.getMessages().manageGroupsDlgConfirmDelete( grpNames );
			if ( Window.confirm( msg ) )
			{
				deleteGroupsFromServer( listOfGroupsToDelete );
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
	private void deleteGroupsFromServer( final ArrayList<GroupInfo> listOfGroupsToDelete )
	{
		if ( listOfGroupsToDelete != null && listOfGroupsToDelete.size() > 0 )
		{
			DeleteGroupsCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;
	
			// Create the callback that will be used when we issue an ajax call to delete the groups.
			rpcCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_DeleteGroups() );
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
							for (GroupInfo nextGroup : listOfGroupsToDelete )
							{
								m_listOfGroups.remove( nextGroup );
							}
							
							// Clear all selections.
							m_selectionModel.clear();
							
							// Update the table to reflect the fact that we deleted a group.
							m_dataProvider.refresh();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
	
			// Issue an ajax request to get delete the list of groups.
			cmd = new DeleteGroupsCmd( listOfGroupsToDelete );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}		
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
	public Set<GroupInfo> getSelectedGroups()
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
		
		// Remember the group we are working with.
		m_groupBeingEdited = groupInfo;
		
		if ( m_modifyGroupDlg == null )
		{
			// Create a handler that will be called when the user presses Ok in the
			// ModifyGroupDlg.
			if ( m_editSuccessfulHandler == null )
			{
				m_editSuccessfulHandler = new EditSuccessfulHandler()
				{
					@Override
					public boolean editSuccessful( Object obj )
					{
						// Were we editing a group?
						if ( m_groupBeingEdited != null )
						{
							// Yes
							// The groupInfo object that was passed to us was passed to the
							// ModifyGroupDlg.  That dialog would have updated the object
							// with whatever the user entered.
							// Update the table to reflect the fact that a group was modified.
							m_dataProvider.refresh();
						}
						else
						{
							GroupInfo newGroup;
							
							// No, we are doing an add.
							// Get the newly created group.
							newGroup = (GroupInfo) obj;
							
							// Add the group as the first group in the list.
							m_listOfGroups.add( 0, newGroup );
							
							// Update the table to reflect the new group we just created.
							m_dataProvider.refresh();
							
							// Go to the first page.
							m_pager.firstPage();
							
							// Select the newly created group.
							m_selectionModel.setSelected( newGroup, true );
						}
						return true;
					}
				};
			}
			m_modifyGroupDlg = new ModifyGroupDlg( false, true, m_editSuccessfulHandler, null, x, y );
		}
		
		m_modifyGroupDlg.init( m_groupBeingEdited );
		m_modifyGroupDlg.setPopupPosition( x, y );
		m_modifyGroupDlg.show();
	}
}
