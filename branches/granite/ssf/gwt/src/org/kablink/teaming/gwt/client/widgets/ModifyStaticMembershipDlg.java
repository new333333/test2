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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.VibeCellTableResources;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;


/**
 * This dialog can be used to modify the static membership of a group.
 * @author jwootton
 *
 */
public class ModifyStaticMembershipDlg extends DlgBox
{
	private TabPanel m_tabPanel;
	private CellTable<GwtUser> m_userTable;
	private CellTable<GwtGroup> m_groupTable;
	private ListDataProvider<GwtUser> m_userDataProvider;
	private ListDataProvider<GwtGroup> m_groupDataProvider;
	private MultiSelectionModel<GwtUser> m_userSelectionModel;
	private MultiSelectionModel<GwtGroup> m_groupSelectionModel;
	private SimplePager m_userPager;
	private SimplePager m_groupPager;
	private ArrayList<GwtUser> m_listOfUsers;
	private ArrayList<GwtGroup> m_listOfGroups;
	
	/**
	 * 
	 */
	public ModifyStaticMembershipDlg(
		boolean autoHide,
		boolean modal,
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( "", editSuccessfulHandler, editCanceledHandler, null ); 
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		Panel mainPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_tabPanel = new TabPanel();
		m_tabPanel.addStyleName( "vibe-tabPanel" );
		
		// Add the "User" tab
		{
			Panel userPanel;

			// Create the panel used to add/remove users
			userPanel = createUserPanel();
			
			m_tabPanel.add( userPanel, messages.modifyStaticMembershipDlgUserTab() );
		}
		
		// Add the "Group" tab
		{
			Panel groupPanel;

			// Create the panel used to add/remove groups.
			groupPanel = createGroupPanel();
			
			m_tabPanel.add( groupPanel, messages.modifyStaticMembershipDlgGroupTab() );
		}
		
		mainPanel.add( m_tabPanel );
		
		return mainPanel;
	}

	/**
	 * Create the panel that holds the ui for adding/removing groups from the group
	 */
	private Panel createGroupPanel()
	{
		VerticalPanel mainPanel;
		FlowPanel menuPanel;
		GwtTeamingMessages messages;
		CellTable.Resources cellTableResources;
		TextColumn<GwtGroup> nameCol;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VerticalPanel();
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "modifyStaticMembershipMenuPanel" );
			
			// Add a "Delete" button.
			label = new InlineLabel( messages.modifyStaticMembershipDlgDeleteLabel() );
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
		m_groupTable = new CellTable<GwtGroup>( 10, cellTableResources );
		m_groupTable.setWidth( "100%" );
		
	    // Add a selection model so we can select groups.
	    m_groupSelectionModel = new MultiSelectionModel<GwtGroup>();
	    m_groupTable.setSelectionModel( m_groupSelectionModel, DefaultSelectionEventManager.<GwtGroup> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<GwtGroup, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<GwtGroup, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( GwtGroup group )
		        {
            		// Get the value from the selection model.
		            return m_groupSelectionModel.isSelected( group );
		        }
		    };
	        m_groupTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_groupTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Name" column
		nameCol = new TextColumn<GwtGroup>()
		{
			@Override
			public String getValue( GwtGroup group )
			{
				String value;
				
				value = group.getTitle();
				if ( value == null || value.length() == 0 )
					value = group.getName();
				
				return value;
			}
		};
		m_groupTable.addColumn( nameCol, messages.modifyStaticMembershipDlgNameCol() );
		
		// Create a pager
		{
			SimplePager.Resources pagerResources;

			pagerResources = GWT.create( SimplePager.Resources.class );
			m_groupPager = new SimplePager( TextLocation.CENTER, pagerResources, false, 0, true );
			m_groupPager.setDisplay( m_groupTable );
		}

		mainPanel.add( menuPanel );
		mainPanel.add( m_groupTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_groupPager );
		
		return mainPanel;
	}
	
	/**
	 * Create the panel that holds the ui for adding/removing users from the group
	 */
	private Panel createUserPanel()
	{
		VerticalPanel mainPanel;
		FlowPanel menuPanel;
		GwtTeamingMessages messages;
		CellTable.Resources cellTableResources;
		TextColumn<GwtUser> nameCol;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VerticalPanel();
		
		// Create a menu
		{
			InlineLabel label;
			
			menuPanel = new FlowPanel();
			menuPanel.addStyleName( "modifyStaticMembershipMenuPanel" );
			
			// Add a "Delete" button.
			label = new InlineLabel( messages.modifyStaticMembershipDlgDeleteLabel() );
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
							deleteSelectedUsers();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			menuPanel.add( label );
		}
		
		// Create the CellTable that will display the list of users.
		cellTableResources = GWT.create( VibeCellTableResources.class );
		m_userTable = new CellTable<GwtUser>( 10, cellTableResources );
		m_userTable.setWidth( "100%" );
		
	    // Add a selection model so we can select users.
	    m_userSelectionModel = new MultiSelectionModel<GwtUser>();
	    m_userTable.setSelectionModel( m_userSelectionModel, DefaultSelectionEventManager.<GwtUser> createCheckboxManager() );

		// Add a checkbox in the first column
		{
			Column<GwtUser, Boolean> ckboxColumn;
			CheckboxCell ckboxCell;
			
            ckboxCell = new CheckboxCell( true, false );
		    ckboxColumn = new Column<GwtUser, Boolean>( ckboxCell )
            {
            	@Override
		        public Boolean getValue( GwtUser user )
		        {
            		// Get the value from the selection model.
		            return m_userSelectionModel.isSelected( user );
		        }
		    };
	        m_userTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant( "<br/>" ) );
		    m_userTable.setColumnWidth( ckboxColumn, 20, Unit.PX );			
		}
		
		// Add the "Name" column
		nameCol = new TextColumn<GwtUser>()
		{
			@Override
			public String getValue( GwtUser user )
			{
				String name;
				
				name = user.getTitle();
				if ( name == null || name.length() == 0 )
					name = user.getName();
				
				return name;
			}
		};
		m_userTable.addColumn( nameCol, messages.modifyStaticMembershipDlgNameCol() );
		
		// Create a pager
		{
			SimplePager.Resources pagerResources;

			pagerResources = GWT.create( SimplePager.Resources.class );
			m_userPager = new SimplePager( TextLocation.CENTER, pagerResources, false, 0, true );
			m_userPager.setDisplay( m_userTable );
		}

		mainPanel.add( menuPanel );
		mainPanel.add( m_userTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_userPager );
		
		return mainPanel;
	}
	
	/**
	 * 
	 */
	private void deleteSelectedGroups()
	{
		Set<GwtGroup> selectedGroups;
		Iterator<GwtGroup> groupIterator;
		
		selectedGroups = m_groupSelectionModel.getSelectedSet();
		if ( selectedGroups != null && selectedGroups.size() > 0 )
		{
			groupIterator = selectedGroups.iterator();
			while ( groupIterator.hasNext() )
			{
				GwtGroup nextGroup;
				
				// Remove this group from our list of groups.
				nextGroup = groupIterator.next();
				m_listOfGroups.remove( nextGroup );
			}

			// Clear all selections.
			m_groupSelectionModel.clear();
			
			// Update the table to reflect the fact that we removed a group.
			m_groupDataProvider.refresh();

			// Tell the table how many groups we have.
			m_groupTable.setRowCount( m_listOfGroups.size(), true );
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().modifyStaticMembershipDlgSelectGroupToRemove() );
		}
	}
	
	/**
	 * 
	 */
	private void deleteSelectedUsers()
	{
		Set<GwtUser> selectedUsers;
		Iterator<GwtUser> userIterator;
		
		selectedUsers = m_userSelectionModel.getSelectedSet();
		if ( selectedUsers != null && selectedUsers.size() > 0 )
		{
			userIterator = selectedUsers.iterator();
			while ( userIterator.hasNext() )
			{
				GwtUser nextUser;
				
				// Remove this user from our list of users.
				nextUser = userIterator.next();
				m_listOfUsers.remove( nextUser );
			}

			// Clear all selections.
			m_userSelectionModel.clear();
			
			// Update the table to reflect the fact that we removed a user.
			m_userDataProvider.refresh();

			// Tell the table how many users we have.
			m_userTable.setRowCount( m_listOfUsers.size(), true );
		}
		else
		{
			Window.alert( GwtTeaming.getMessages().modifyStaticMembershipDlgSelectUserToRemove() );
		}
	}
	
	/**
	 * Create a list of all the users and groups.
	 */
	public Object getDataFromDlg()
	{
		ArrayList<GwtTeamingItem> membershipList;
		
		membershipList = new ArrayList<GwtTeamingItem>();
		
		if ( m_listOfUsers != null )
		{
			for (GwtUser nextUser : m_listOfUsers)
			{
				membershipList.add( nextUser );
			}
		}
		
		if ( m_listOfGroups != null )
		{
			for (GwtGroup nextGroup : m_listOfGroups)
			{
				membershipList.add( nextGroup );
			}
		}
		
		return membershipList;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	/**
	 * 
	 */
	public void init( String groupName, List<GwtTeamingItem> membership )
	{
		// Update the dialog header.
		setHeaderText( GwtTeaming.getMessages().modifyStaticMembershipDlgHeader( groupName ) );
		
		if ( m_listOfUsers == null )
			m_listOfUsers = new ArrayList<GwtUser>();
		else
			m_listOfUsers.clear();
		
		if ( m_listOfGroups == null )
			m_listOfGroups = new ArrayList<GwtGroup>();
		else
			m_listOfGroups.clear();
		
		if ( membership != null )
		{
			// Separate the membership into a list of users and a list of groups.
			for (GwtTeamingItem nextMember : membership)
			{
				if ( nextMember instanceof GwtUser )
					m_listOfUsers.add( (GwtUser) nextMember );
				else if ( nextMember instanceof GwtGroup )
					m_listOfGroups.add( (GwtGroup) nextMember );
			}
		}

		// Initialize the user CellTable information
		{
			if ( m_userDataProvider == null )
			{
				m_userDataProvider = new ListDataProvider<GwtUser>( m_listOfUsers );
				m_userDataProvider.addDataDisplay( m_userTable );
			}
			else
			{
				m_userDataProvider.setList( m_listOfUsers );
				m_userDataProvider.refresh();
			}
			
			// Clear all selections.
			m_userSelectionModel.clear();
			
			// Go to the first page
			m_userPager.firstPage();
			
			// Tell the table how many users we have.
			m_userTable.setRowCount( m_listOfUsers.size(), true );
		}
		
		// Initialize the group CellTable information
		{
			if ( m_groupDataProvider == null )
			{
				m_groupDataProvider = new ListDataProvider<GwtGroup>( m_listOfGroups );
				m_groupDataProvider.addDataDisplay( m_groupTable );
			}
			else
			{
				m_groupDataProvider.setList( m_listOfGroups );
				m_groupDataProvider.refresh();
			}
			
			// Clear all selections.
			m_groupSelectionModel.clear();
			
			// Go to the first page
			m_groupPager.firstPage();
			
			// Tell the table how many groups we have.
			m_groupTable.setRowCount( m_listOfGroups.size(), true );
		}
	
		// Select the "Users" tab
		m_tabPanel.selectTab( 0 );
	}
}
