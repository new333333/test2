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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchType;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.SearchFindResultsEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.FindCtrl.FindCtrlClient;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;

/**
 * This dialog can be used to modify the static membership of a group.
 * 
 * @author jwootton
 */
public class ModifyStaticMembershipDlg extends DlgBox
	implements SearchFindResultsEvent.Handler
{
	/**
	 * 
	 */
	public class StaticMembershipInfo
	{
		private boolean m_externalMembersAllowed;
		private ArrayList<GwtTeamingItem> m_membershipList;
		
		/**
		 * 
		 */
		public StaticMembershipInfo(
			boolean externalMembersAllowed,
			ArrayList<GwtTeamingItem> membershipList )
		{
			m_externalMembersAllowed = externalMembersAllowed;
			m_membershipList = membershipList;
		}
		
		/**
		 * 
		 */
		public boolean getIsExternalMembersAllowed()
		{
			return m_externalMembersAllowed;
		}
		
		/**
		 * 
		 */
		public ArrayList<GwtTeamingItem> getMembershipList()
		{
			return m_membershipList;
		}
	}
	
	/**
	 * Inner class used to compare two groups
	 */
	public static class GroupComparator implements Comparator<GwtGroup> 
	{
		/**
		 * Class constructor.
		 */
		public GroupComparator() 
		{
		}

		/**
		 * Compares two GwtGroup's by their name.
		 * 
		 * Implements the Comparator.compare() method.
		 */
		@Override
		public int compare( GwtGroup group1, GwtGroup group2 ) 
		{
			int reply;
			String name1;
			String name2;

			name1 = getGroupDisplayName( group1 );
			name2 = getGroupDisplayName( group2 );
			reply = GwtClientHelper.safeSColatedCompare( name1, name2 );

			return reply;
		}
	}

	/**
	 * Inner class used to compare two users
	 */
	public static class UserComparator implements Comparator<GwtUser> 
	{
		/**
		 * Class constructor.
		 */
		public UserComparator() 
		{
		}

		/**
		 * Compares two GwtUsers's by their name.
		 * 
		 * Implements the Comparator.compare() method.
		 */
		@Override
		public int compare( GwtUser user1, GwtUser user2 ) 
		{
			int reply;
			String name1;
			String name2;

			name1 = getUserDisplayName( user1 );
			name2 = getUserDisplayName( user2 );
			reply = GwtClientHelper.safeSColatedCompare( name1, name2 );

			return reply;
		}
	}

	private TabPanel m_tabPanel;
	private CheckBox m_externalAllowedCb;
	private CellTable<GwtUser> m_userTable;
	private CellTable<GwtGroup> m_groupTable;
	private ListDataProvider<GwtUser> m_userDataProvider;
	private ListDataProvider<GwtGroup> m_groupDataProvider;
	private MultiSelectionModel<GwtUser> m_userSelectionModel;
	private MultiSelectionModel<GwtGroup> m_groupSelectionModel;
	private VibeSimplePager m_userPager;
	private VibeSimplePager m_groupPager;
	private ArrayList<GwtUser> m_listOfUsers;
	private ArrayList<GwtGroup> m_listOfGroups;
	private FindCtrl m_findUserCtrl;
	private FindCtrl m_findGroupCtrl;

	// The following defines the TeamingEvents that are handled by
	// this class. See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
    {
		// Search events.
		TeamingEvents.SEARCH_FIND_RESULTS 
	};

	/**
	 * 
	 */
	public ModifyStaticMembershipDlg(
			boolean autoHide,
			boolean modal,
			EditSuccessfulHandler editSuccessfulHandler, // We will call this handler when the user presses the ok button
			EditCanceledHandler editCanceledHandler, // This gets called when the user presses the Cancel button
			int xPos, 
			int yPos ) 
	{
		super( autoHide, modal, xPos, yPos );

		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents, 
				this );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( "", editSuccessfulHandler, editCanceledHandler, null );
	}

	/**
	 * Add the given group to the list of groups
	 */
	private void addGroup( GwtGroup group ) 
	{
		// Add the group as the first group in the list.
		m_listOfGroups.add( 0, group );

		// Update the table to reflect the new group we just created.
		m_groupDataProvider.refresh();

		// Go to the first page.
		m_groupPager.firstPage();

		// Select the newly created group.
		m_groupSelectionModel.setSelected( group, true );

		// Tell the table how many groups we have
		m_groupTable.setRowCount( m_listOfGroups.size(), true );
	}

	/**
	 * Add the given user to the list of users.
	 */
	private void addUser( GwtUser user ) 
	{
		// Add the user as the first user in the list.
		m_listOfUsers.add( 0, user );

		// Update the table to reflect the new user we just created.
		m_userDataProvider.refresh();

		// Go to the first page.
		m_userPager.firstPage();

		// Select the newly created user.
		m_userSelectionModel.setSelected( user, true );

		// Tell the table how many users we have
		m_userTable.setRowCount( m_listOfUsers.size(), true );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props ) 
	{
		GwtTeamingMessages messages;
		Panel mainPanel;

		messages = GwtTeaming.getMessages();

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		m_tabPanel = new TabPanel();
		m_tabPanel.addStyleName( "vibe-tabPanel" );

		// Add an "Allow external users and groups" checkbox
		{
			ClickHandler clickHandler;
			FlowPanel tmpPanel;
			
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
							boolean externalMembersAllowed;
							
							// Tell the FindCtrl whether or not to search for just internal
							// users/groups or both internal and external users/groups
							externalMembersAllowed = externalMembersAllowed();
							m_findGroupCtrl.setSearchForInternalPrincipals( true );
							m_findGroupCtrl.setSearchForExternalPrincipals( externalMembersAllowed );
							m_findUserCtrl.setSearchForInternalPrincipals( true );
							m_findUserCtrl.setSearchForExternalPrincipals( externalMembersAllowed );
							
							// Since the user changed "Allow external users and groups" checkbox
							// validate the membership to make sure it abides by the
							// "allow external users and groups" setting
							validateGroupMembership();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
				
			};

			tmpPanel = new FlowPanel();
			tmpPanel.addStyleName( "modifyStaticMembershipDlg_externalAllowedCb" );
			m_externalAllowedCb = new CheckBox( messages.modifyStaticMembershipDlgExternalAllowedLabel() );
			m_externalAllowedCb.addClickHandler( clickHandler );
			tmpPanel.add( m_externalAllowedCb );
			mainPanel.add( tmpPanel );
		}

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
	 * Create the panel that holds the ui for adding/removing groups from the
	 * group
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

		// Add a find control for groups
		{
			HTMLTable.RowFormatter rowFormatter;
			final FlexTable table = new FlexTable();

			rowFormatter = table.getRowFormatter();
			rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_MIDDLE );

			table.getElement().getStyle().setMarginBottom( 8, Unit.PX );
			mainPanel.add( table );

			table.setText( 0, 0, messages.modifyStaticMembershipDlgGroupLabel() );

			FindCtrl.createAsync(
						this,
						GwtSearchCriteria.SearchType.GROUP,
						new FindCtrlClient() 
						{
							@Override
							public void onUnavailable() 
							{
								// Nothing to do. Error handled in asynchronous
								// provider.
							}
	
							@Override
							public void onSuccess( FindCtrl findCtrl ) 
							{
								m_findGroupCtrl = findCtrl;
								m_findGroupCtrl.setSearchType( SearchType.GROUP );
								m_findGroupCtrl.setSearchForInternalPrincipals( true );
								m_findGroupCtrl.setSearchForExternalPrincipals( externalMembersAllowed() );
								table.setWidget( 0, 1, m_findGroupCtrl );
						}
					} );
		}

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
				public void onClick(ClickEvent event) 
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
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_groupTable = new CellTable<GwtGroup>( 10, cellTableResources );
		m_groupTable.setWidth( "100%" );

		// Set the widget that will be displayed when there are no groups
		{
			FlowPanel flowPanel;
			InlineLabel noGroupsLabel;

			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noGroupsLabel = new InlineLabel( GwtTeaming.getMessages().modifyStaticMembershipDlgNoGroupsLabel() );
			flowPanel.add( noGroupsLabel );

			m_groupTable.setEmptyTableWidget( flowPanel );
		}

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
			m_groupTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant("<br/>") );
			m_groupTable.setColumnWidth( ckboxColumn, 20, Unit.PX );
		}

		// Add the "Name" column
		nameCol = new TextColumn<GwtGroup>() 
		{
			@Override
			public String getValue( GwtGroup group ) 
			{
				String name;

				name = getGroupDisplayName( group );

				return name;
			}
		};
		m_groupTable.addColumn( nameCol, messages.modifyStaticMembershipDlgNameCol() );

		// Create a pager
		{
			m_groupPager = new VibeSimplePager();
			m_groupPager.setDisplay( m_groupTable );
		}

		mainPanel.add( menuPanel );
		mainPanel.add( m_groupTable );
		mainPanel.setHorizontalAlignment( HasHorizontalAlignment.ALIGN_CENTER );
		mainPanel.add( m_groupPager );

		return mainPanel;
	}

	/**
	 * Create the panel that holds the ui for adding/removing users from the
	 * group
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

		// Add a find control for users
		{
			HTMLTable.RowFormatter rowFormatter;
			final FlexTable table = new FlexTable();

			rowFormatter = table.getRowFormatter();
			rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_MIDDLE );

			table.getElement().getStyle().setMarginBottom( 8, Unit.PX );
			mainPanel.add( table );

			table.setText( 0, 0, messages.modifyStaticMembershipDlgUserLabel() );

			FindCtrl.createAsync(
						this, 
						GwtSearchCriteria.SearchType.USER,
						new FindCtrlClient() 
						{
							@Override
							public void onUnavailable() 
							{
								// Nothing to do. Error handled in asynchronous
								// provider.
							}
	
							@Override
							public void onSuccess( FindCtrl findCtrl ) 
							{
								m_findUserCtrl = findCtrl;
								m_findUserCtrl.setSearchType( SearchType.USER );
								m_findUserCtrl.setSearchForInternalPrincipals( true );
								m_findUserCtrl.setSearchForExternalPrincipals( externalMembersAllowed() );
								table.setWidget( 0, 1, m_findUserCtrl );
							}
						} );
		}

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
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_userTable = new CellTable<GwtUser>( 10, cellTableResources );
		m_userTable.setWidth( "100%" );

		// Set the widget that will be displayed when there are no groups
		{
			FlowPanel flowPanel;
			InlineLabel noGroupsLabel;

			flowPanel = new FlowPanel();
			flowPanel.addStyleName( "noObjectsFound" );
			noGroupsLabel = new InlineLabel( GwtTeaming.getMessages().modifyStaticMembershipDlgNoUsersLabel() );
			flowPanel.add( noGroupsLabel );

			m_userTable.setEmptyTableWidget( flowPanel );
		}

		// Add a selection model so we can select users.
		m_userSelectionModel = new MultiSelectionModel<GwtUser>();
		m_userTable.setSelectionModel(
						m_userSelectionModel,
						DefaultSelectionEventManager.<GwtUser>createCheckboxManager() );

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
			m_userTable.addColumn( ckboxColumn, SafeHtmlUtils.fromSafeConstant("<br/>") );
			m_userTable.setColumnWidth( ckboxColumn, 20, Unit.PX );
		}

		// Add the "Name" column
		nameCol = new TextColumn<GwtUser>() 
		{
			@Override
			public String getValue( GwtUser user ) 
			{
				String name;

				name = getUserDisplayName( user );

				return name;
			}
		};
		m_userTable.addColumn( nameCol, messages.modifyStaticMembershipDlgNameCol() );

		// Create a pager
		{
			m_userPager = new VibeSimplePager();
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
	 * Are external members allowed?
	 */
	private boolean externalMembersAllowed()
	{
		if ( m_externalAllowedCb != null )
			return m_externalAllowedCb.getValue();
		
		return false;
	}

	/**
	 * Create a list of all the users and groups.
	 */
	@Override
	public Object getDataFromDlg() 
	{
		StaticMembershipInfo membershipInfo;
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

		membershipInfo = new StaticMembershipInfo( externalMembersAllowed(), membershipList );
		
		return membershipInfo;
	}

	/**
	 * Return the widget that should get the focus when the dialog is shown.
	 */
	@Override
	public FocusWidget getFocusWidget() 
	{
		if ( m_findUserCtrl != null )
			return m_findUserCtrl.getFocusWidget();
		
		return null;
	}

	/**
	 * Get the display name for the given group
	 */
	private static String getGroupDisplayName( GwtGroup group ) 
	{
		String name;

		name = group.getTitle();
		if ( name == null || name.length() == 0 )
			name = group.getName();

		return name;
	}

	/**
	 * Get the display name for the given user
	 */
	private static String getUserDisplayName( GwtUser user ) 
	{
		String name;

		name = user.getTitle();
		if ( name == null || name.length() == 0 )
			name = user.getName();

		return name;
	}

	/**
	 * 
	 */
	public void init(
		String groupName,
		List<GwtTeamingItem> membership,
		boolean externalMembersAllowed,
		boolean groupExistsInDb ) 
	{
		// Update the dialog header.
		setCaption( GwtTeaming.getMessages().modifyStaticMembershipDlgHeader( groupName ) );

		if ( m_externalAllowedCb != null )
		{
			m_externalAllowedCb.setValue( externalMembersAllowed );
			
			// If this group already exists in the db, we don't let them change if external members
			// are allowed.
			m_externalAllowedCb.setEnabled( !groupExistsInDb );
		}
		
		if ( m_findUserCtrl != null )
		{
			// Tell the FindCtrl whether or not to search for just internal
			// users or both internal and external users
			m_findUserCtrl.setSearchForInternalPrincipals( true );
			m_findUserCtrl.setSearchForExternalPrincipals( externalMembersAllowed );
			m_findUserCtrl.setInitialSearchString( "" );
		}
		
		if ( m_findGroupCtrl != null )
		{
			// Tell the FindCtrl whether or not to search for just internal
			// groups or both internal and external groups
			m_findGroupCtrl.setSearchForInternalPrincipals( true );
			m_findGroupCtrl.setSearchForExternalPrincipals( externalMembersAllowed );
			m_findGroupCtrl.setInitialSearchString( "" );
		}
		

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
			// Separate the membership into a list of users and a list of
			// groups.
			for (GwtTeamingItem nextMember : membership) 
			{
				if ( nextMember instanceof GwtUser )
					m_listOfUsers.add( (GwtUser) nextMember );
				else if ( nextMember instanceof GwtGroup )
					m_listOfGroups.add( (GwtGroup) nextMember );
			}

			// Sort the list of users
			Collections.sort( m_listOfUsers, new UserComparator() );

			// Sort the list of groups
			Collections.sort( m_listOfGroups, new GroupComparator() );
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

	/**
	 * See if the given group in already in the group.
	 */
	private boolean isGroupInGroup( GwtGroup group ) 
	{
		String grpId;

		grpId = group.getId();
		if ( m_listOfGroups != null && grpId != null ) 
		{
			for (GwtGroup nextGroup : m_listOfGroups) 
			{
				String nxtGrpId;

				nxtGrpId = nextGroup.getId();
				if ( grpId.equalsIgnoreCase( nxtGrpId ) )
					return true;
			}
		}

		// If we get here we did not find the group.
		return false;
	}

	/**
	 * See if the given user in already in the group.
	 */
	private boolean isUserInGroup( GwtUser user ) 
	{
		String userId;

		userId = user.getUserId();
		if ( m_listOfUsers != null && userId != null ) 
		{
			for (GwtUser nextUser : m_listOfUsers) 
			{
				String nextUserId;

				nextUserId = nextUser.getUserId();
				if ( userId.equalsIgnoreCase( nextUserId ) )
					return true;
			}
		}

		// If we get here we did not find the user.
		return false;
	}

	/**
	 * Handles SearchFindResultsEvent's received by this class.
	 * 
	 * Implements the SearchFindResultsEvent.Handler.onSearchFindResults()
	 * method.
	 * 
	 * @param event
	 */
	@Override
	public void onSearchFindResults( SearchFindResultsEvent event ) 
	{
		// If the find results aren't for this share this dialog...
		if ( !((Widget) event.getSource()).equals( this ) ) 
		{
			// ...ignore the event.
			return;
		}

		// Are we dealing with a User?
		GwtTeamingItem selectedObj = event.getSearchResults();
		if ( selectedObj instanceof GwtUser ) 
		{
			GwtUser user;

			// Yes
			user = (GwtUser) selectedObj;

			// Is this user already in our list?
			if ( isUserInGroup( user ) == false )
			{
				boolean doAdd = true;
				
				// No
				// Is this an external user
				if ( user.isInternal() == false )
				{
					// Yes
					// Are external users allowed?
					if ( externalMembersAllowed() == false )
					{
						// No, don't add this user
						doAdd = false;
					}
				}
				
				// Add the user to the list of users.
				if ( doAdd )
					addUser( user );
			}

			// Hide the search-results widget.
			m_findUserCtrl.hideSearchResults();

			// Clear the text from the find control.
			m_findUserCtrl.clearText();

			// Give the find control the focus
			m_findUserCtrl.getFocusWidget().setFocus( true );
		}
		// Are we dealing with a group?
		else if ( selectedObj instanceof GwtGroup ) 
		{
			GwtGroup group;

			// Yes
			group = (GwtGroup) selectedObj;

			// Is this group already in our list?
			if ( isGroupInGroup( group ) == false ) 
			{
				boolean doAdd = true;
				
				// No
				// Does this group hold external users?
				if ( group.isInternal() == false )
				{
					// Yes
					// Are external users allowed?
					if ( externalMembersAllowed() == false )
					{
						// No, don't add this group
						doAdd = false;
					}
				}
				// No, add the group to the list of groups.
				if ( doAdd )
					addGroup( group );
			}

			// Hide the search-results widget.
			m_findGroupCtrl.hideSearchResults();

			// Clear the text from the find control.
			m_findGroupCtrl.clearText();

			// Give the find control the focus.
			m_findGroupCtrl.getFocusWidget().setFocus( true );
		}
	}
	
	/**
	 * Go through the group membership and make sure it abides by the
	 * "allow external users/groups" setting.
	 */
	private void validateGroupMembership()
	{
		// Are external members allowed?
		if ( externalMembersAllowed() == false )
		{
			// No, remove any external users/groups from the list
			if ( m_listOfUsers != null ) 
			{
				ArrayList<GwtUser> usersToBeRemoved;

				usersToBeRemoved = new ArrayList<GwtUser>();
				
				for (GwtUser nextUser : m_listOfUsers) 
				{
					if ( nextUser.isInternal() == false )
					{
						// Add this user as one to be removed from the list of users.
						usersToBeRemoved.add( nextUser );
					}
				}
				
				// Remove all the external users.
				if ( usersToBeRemoved.size() > 0 )
				{
					for (GwtUser nextUser : usersToBeRemoved)
					{
						m_listOfUsers.remove( nextUser );
					}

					// Update the table to reflect the fact that we removed a user.
					m_userDataProvider.refresh();

					// Tell the table how many users we have.
					m_userTable.setRowCount( m_listOfUsers.size(), true );
				}
			}

			if ( m_listOfGroups != null ) 
			{
				ArrayList<GwtGroup> groupsToBeRemoved;
				
				groupsToBeRemoved = new ArrayList<GwtGroup>();
				
				for (GwtGroup nextGroup : m_listOfGroups) 
				{
					if ( nextGroup.isInternal() == false )
					{
						// Add this group as one to be removed from the list of groups.
						groupsToBeRemoved.add( nextGroup );
					}
				}
				
				// Remove all the external groups.
				if ( groupsToBeRemoved.size() > 0 )
				{
					for (GwtGroup nextGroup : groupsToBeRemoved)
					{
						m_listOfGroups.remove( nextGroup );
					}

					// Update the table to reflect the fact that we removed a group.
					m_groupDataProvider.refresh();

					// Tell the table how many groups we have.
					m_groupTable.setRowCount( m_listOfGroups.size(), true );
				}
			}
		}
	}
}
