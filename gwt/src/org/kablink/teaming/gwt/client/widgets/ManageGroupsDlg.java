/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.QuickFilter;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.datatable.GroupActionCell;
import org.kablink.teaming.gwt.client.datatable.GroupTitleCell;
import org.kablink.teaming.gwt.client.datatable.GroupTypeCell;
import org.kablink.teaming.gwt.client.datatable.VibeCellTable;
import org.kablink.teaming.gwt.client.datatable.VibeDataTableConstants;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GroupCreatedEvent;
import org.kablink.teaming.gwt.client.event.GroupMembershipModificationFailedEvent;
import org.kablink.teaming.gwt.client.event.GroupMembershipModificationStartedEvent;
import org.kablink.teaming.gwt.client.event.GroupMembershipModifiedEvent;
import org.kablink.teaming.gwt.client.event.GroupModificationFailedEvent;
import org.kablink.teaming.gwt.client.event.GroupModificationStartedEvent;
import org.kablink.teaming.gwt.client.event.GroupModifiedEvent;
import org.kablink.teaming.gwt.client.event.InvokePrincipalDesktopSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.InvokePrincipalMobileSettingsDlgEvent;
import org.kablink.teaming.gwt.client.event.QuickFilterEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteGroupsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetAllGroupsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetPersonalPrefsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetPrincipalsAdminRightsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetPrincipalsAdminRightsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetPrincipalsAdminRightsRpcResponseData.AdminRights;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GroupType;
import org.kablink.teaming.gwt.client.util.GroupType.GroupClass;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.ModifyGroupDlg.ModifyGroupDlgClient;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
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
public class ManageGroupsDlg extends DlgBox implements
		GroupCreatedEvent.Handler,
		GroupMembershipModificationFailedEvent.Handler,
		GroupMembershipModificationStartedEvent.Handler,
		GroupMembershipModifiedEvent.Handler,
		GroupModificationFailedEvent.Handler,
		GroupModificationStartedEvent.Handler,
		GroupModifiedEvent.Handler,
		QuickFilterEvent.Handler
{
	private CellTable<GroupInfoPlus> m_groupsTable;
	private MultiSelectionModel<GroupInfoPlus> m_selectionModel;
	private ListDataProvider<GroupInfoPlus> m_dataProvider;
	private VibeSimplePager m_pager;
	private List<GroupInfoPlus> m_listOfGroups;
	private ModifyGroupDlg m_modifyGroupDlg;
	private QuickFilter m_quickFilter;
	private int m_width;
	private int m_pageSize;
	private GwtTeamingMessages m_messages;

	// The following defines the TeamingEvents that are handled by
	// this class. See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
	{
		TeamingEvents.GROUP_CREATED,
		TeamingEvents.GROUP_MEMBERSHIP_MODIFICATION_FAILED,
		TeamingEvents.GROUP_MEMBERSHIP_MODIFICATION_STARTED,
		TeamingEvents.GROUP_MEMBERSHIP_MODIFIED,
		TeamingEvents.GROUP_MODIFICATION_FAILED,
		TeamingEvents.GROUP_MODIFICATION_STARTED,
		TeamingEvents.GROUP_MODIFIED,
		TeamingEvents.QUICK_FILTER
	};
	
	// MANAGE_GROUPS_ID is used to tell the QuickFilter widget who it is dealing with.
	private static final long MANAGE_GROUPS_ID = -101;

	/**
	 * Callback interface to interact with the "manage groups" dialog
	 * asynchronously after it loads.
	 */
	public interface ManageGroupsDlgClient {
		void onSuccess(ManageGroupsDlg mgDlg);

		void onUnavailable();
	}

	/**
	 * The different statuses of a group
	 */
	public enum GroupModificationStatus {
		GROUP_CREATION_IN_PROGRESS,
		GROUP_DELETION_IN_PROGRESS,
		GROUP_MODIFICATION_IN_PROGRESS,
		GROUP_MEMBERSHIP_MODIFICATION_IN_PROGRESS,
		READY;
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isBeingCreated()  {return GROUP_CREATION_IN_PROGRESS.equals(    this);}
		public boolean isBeingDeleted()  {return GROUP_DELETION_IN_PROGRESS.equals(    this);}
		public boolean isBeingModified() {return GROUP_MODIFICATION_IN_PROGRESS.equals(this);}
		public boolean isMembershipBeingModified() { return GROUP_MEMBERSHIP_MODIFICATION_IN_PROGRESS.equals( this ); }
		public boolean isReady()         {return READY.equals(                         this);}
	}

	/**
	 * This class holds information about a group and the current status of a
	 * group. For example, is group modification in process, is group creation
	 * in process.
	 */
	public class GroupInfoPlus {
		private GroupInfo m_groupInfo;
		private GroupModificationStatus m_status;

		/**
		 * 
		 */
		public GroupInfoPlus(GroupInfo groupInfo, GroupModificationStatus status) {
			m_groupInfo = groupInfo;
			m_status = status;
		}

		/**
		 * 
		 */
		public GroupInfo getGroupInfo() {
			return m_groupInfo;
		}

		/**
		 * 
		 */
		public GroupModificationStatus getStatus() {
			return m_status;
		}

		/**
		 * 
		 */
		public void setStatus(GroupModificationStatus status) {
			m_status = status;
		}
	}

	/**
	 * 
	 */
	private ManageGroupsDlg(boolean autoHide, boolean modal, int xPos,
			int yPos, int width, int height) {
		super(autoHide, modal, xPos, yPos, new Integer(width), new Integer(
				height), DlgButtonMode.Close);

		m_messages = GwtTeaming.getMessages();
		m_pageSize = 20;

		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(GwtTeaming.getEventBus(),
				m_registeredEvents, this);

		// Create the header, content and footer of this dialog box.
		m_width = width;
		createAllDlgContent(GwtTeaming.getMessages().manageGroupsDlgHeader(),
				null, null, null);
	}

	/**
	 * Add the given list of groups to the dialog
	 */
	private void addGroups(List<GroupInfo> listOfGroups) {
		m_listOfGroups = new ArrayList<GroupInfoPlus>();

		// Create a GroupInfoPlus object for every group
		for (GroupInfo nextGroup : listOfGroups) {
			GroupInfoPlus groupInfoPlus;

			groupInfoPlus = new GroupInfoPlus(nextGroup,
					GroupModificationStatus.READY);
			m_listOfGroups.add(groupInfoPlus);
		}

		if (m_dataProvider == null) {
			m_dataProvider = new ListDataProvider<GroupInfoPlus>(m_listOfGroups);
			m_dataProvider.addDataDisplay(m_groupsTable);
		} else {
			m_dataProvider.setList(m_listOfGroups);
			m_dataProvider.refresh();
		}

		// Clear all selections.
		m_selectionModel.clear();

		// Go to the first page
		m_pager.firstPage();

		// Tell the table how many groups we have.
		m_groupsTable.setRowCount(m_listOfGroups.size(), true);
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent(Object props) {
		VerticalPanel mainPanel = null;
		GroupTitleCell cell;
		Column<GroupInfoPlus, GroupInfoPlus> titleCol;
		TextColumn<GroupInfoPlus> nameCol;
		FlowPanel menuPanel;
		CellTable.Resources cellTableResources;

		mainPanel = new VerticalPanel();
		mainPanel.setStyleName("teamingDlgBoxContent");

		// Create a menu
		{
			InlineLabel label;

			menuPanel = new FlowPanel();
			menuPanel.addStyleName("groupManagementMenuPanel");

			// Add a "New" button.
			label = new InlineLabel(m_messages.manageGroupsDlgNewGroupLabel());
			label.addStyleName("groupManagementBtn");
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							invokeAddGroupDlg();
						}
					});
				}
			});
			menuPanel.add(label);

			// Add a "Delete" button.
			label = new InlineLabel(m_messages.manageGroupsDlgDeleteGroupLabel());
			label.addStyleName("groupManagementBtn");
			label.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							deleteSelectedGroups();
						}
					});
				}
			});
			menuPanel.add(label);
			
			// Add a "More" button.
			final PopupMenu morePopup = new PopupMenu(true, false, false);
			morePopup.addStyleName("groupManagementDropDown");
			final InlineLabel moreMenu = new InlineLabel();
			moreMenu.getElement().setInnerHTML(renderDropdownMenuItemHtml(m_messages.manageGroupsDlgMoreLabel()));
			moreMenu.addStyleName("groupManagementBtn");
			moreMenu.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					morePopup.showRelativeToTarget(moreMenu);
				}
			});
			menuPanel.add(moreMenu);
			populateMorePopup(morePopup);
			
			// Add a quick filter
			{
				FlowPanel qfPanel;
				
				qfPanel = new FlowPanel();
				qfPanel.addStyleName( "manageGroups_QuickFilterPanel" );
				
				m_quickFilter = new QuickFilter( MANAGE_GROUPS_ID );
				qfPanel.add( m_quickFilter );
				
				menuPanel.add( qfPanel );
			}
		}

		// Create the CellTable that will display the list of groups.
		cellTableResources = GWT.create( VibeCellTable.VibeCellTableResources.class );
		m_groupsTable = new CellTable<GroupInfoPlus>(m_pageSize, cellTableResources);
		m_groupsTable.setWidth(String.valueOf(m_width - 20) + "px");

		// Set the widget that will be displayed when there are no groups
		{
			FlowPanel flowPanel;
			InlineLabel noGroupsLabel;

			flowPanel = new FlowPanel();
			flowPanel.addStyleName("noObjectsFound");
			noGroupsLabel = new InlineLabel(GwtTeaming.getMessages()
					.manageGroupsDlgNoGroupsLabel());
			flowPanel.add(noGroupsLabel);

			m_groupsTable.setEmptyTableWidget(flowPanel);
		}

		// Add a selection model so we can select groups.
		m_selectionModel = new MultiSelectionModel<GroupInfoPlus>();
		m_groupsTable.setSelectionModel(
									m_selectionModel,
									DefaultSelectionEventManager.<GroupInfoPlus> createCheckboxManager() );

		// Add a checkbox in the first column.
		{
			// Define the select all checkbox in the header...
			CheckboxCell ckboxCell = new CheckboxCell( true, false );
			final VibeSelectAllHeader saHeader = new VibeSelectAllHeader( ckboxCell );
			saHeader.setUpdater(
				new ValueUpdater<Boolean>()
				{
					@Override
					public void update( Boolean checked )
					{
						List<GroupInfoPlus> rows = m_groupsTable.getVisibleItems();
						if ( null != rows )
						{
							for ( GroupInfoPlus row : rows )
							{
								m_selectionModel.setSelected( row, checked );
							}
						}
					}
				} );
			
			// ...define a column for it...
			Column<GroupInfoPlus, Boolean> ckboxColumn;
			ckboxColumn = new Column<GroupInfoPlus, Boolean>( ckboxCell )
			{
				@Override
				public Boolean getValue( GroupInfoPlus groupInfoPlus )
				{
					// Get the value from the selection model.
					return m_selectionModel.isSelected( groupInfoPlus );
				}
			};
			
			// ...connect updating the contents of the table when the
			// ...check box is checked or unchecked...
			ckboxColumn.setFieldUpdater(
				new FieldUpdater<GroupInfoPlus, Boolean>()
				{
					@Override
					public void update( int index, GroupInfoPlus row, Boolean checked )
					{
						m_selectionModel.setSelected( row, checked );
						if ( ! checked )
						{
							saHeader.setValue( checked );
						}
					};
				} );

			// ...and connect it all together.
			m_groupsTable.addColumn(
								ckboxColumn,
								saHeader );
			m_groupsTable.setColumnWidth( ckboxColumn, 20, Unit.PX );
		}

		// Add the "Type" column.
		{
			Column<GroupInfoPlus, GroupType> typeCol;
			GroupTypeCell groupTypeCell;

			groupTypeCell = new GroupTypeCell();
			typeCol = new Column<GroupInfoPlus, GroupType>(groupTypeCell) {
				@Override
				public GroupType getValue(GroupInfoPlus groupInfoPlus)
				{
					GroupInfo groupInfo = groupInfoPlus.getGroupInfo();
					if (groupInfo.getIsFromLdap()) {
						if (groupInfo.getIsExternalAllowed()) {
							return new GroupType(GroupClass.EXTERNAL_LDAP, groupInfo.isAdmin());
						}
						return new GroupType(GroupClass.INTERNAL_LDAP, groupInfo.isAdmin());
					}

					if (groupInfo.getIsExternalAllowed()) {
						return new GroupType(GroupClass.EXTERNAL_LOCAL, groupInfo.isAdmin());
					}
					return new GroupType(GroupClass.INTERNAL_LOCAL, groupInfo.isAdmin());
				}
			};
			m_groupsTable.addColumn(typeCol, m_messages.manageGroupsDlgTypeCol());
		}

		// Add the "Title" column. The user can click on the text in this column
		// to edit the group.
		{
			cell = new GroupTitleCell();
			titleCol = new Column<GroupInfoPlus, GroupInfoPlus>(cell) {
				@Override
				public GroupInfoPlus getValue(GroupInfoPlus groupInfoPlus) {
					return groupInfoPlus;
				}
			};

			titleCol.setFieldUpdater(new FieldUpdater<GroupInfoPlus, GroupInfoPlus>() {
				@Override
				public void update(int index, GroupInfoPlus groupInfoPlus,
						GroupInfoPlus value) {
					// Is this group in the process of being deleted, modified
					// or created?
					if (groupInfoPlus.getStatus().isReady()) {
						// No, let the user edit the group.
						invokeModifyGroupDlg(groupInfoPlus.getGroupInfo());
					}
				}
			});
			m_groupsTable.addColumn(
								titleCol,
								m_messages.manageGroupsDlgTitleCol());
		}

		// Add the "Action" column.
		{
			Column<GroupInfoPlus, GroupInfoPlus> actionCol;
			
			GroupActionCell gac = new GroupActionCell();
			actionCol = new Column<GroupInfoPlus, GroupInfoPlus>(gac) {
				@Override
				public GroupInfoPlus getValue(GroupInfoPlus groupInfoPlus) {
					return groupInfoPlus;
				}
			};

			m_groupsTable.addColumn(
								actionCol,
								SafeHtmlUtils.fromSafeConstant("<br/>"));
			m_groupsTable.setColumnWidth(actionCol, VibeDataTableConstants.ACTION_MENU_WIDTH_PX, Unit.PX);
		}

		// Add the "Name" column
		nameCol = new TextColumn<GroupInfoPlus>() {
			@Override
			public String getValue(GroupInfoPlus groupInfoPlus) {
				GroupInfo groupInfo;
				String name;

				groupInfo = groupInfoPlus.getGroupInfo();

				name = groupInfo.getName();
				if (name == null)
					name = "";

				return name;
			}
		};
		m_groupsTable.addColumn(nameCol, m_messages.manageGroupsDlgNameCol());

		// Add the "Admin" column
		nameCol = new TextColumn<GroupInfoPlus>() {
			@Override
			public String getValue(GroupInfoPlus groupInfoPlus) {
				GroupInfo groupInfo = groupInfoPlus.getGroupInfo();
				String adminRights;
				if ( groupInfo.isAdmin() )
				     adminRights = m_messages.manageGroupsDlgGroup();
				else adminRights = "";
				return adminRights;
			}
		};
		m_groupsTable.addColumn(nameCol, m_messages.manageGroupsDlgAdminCol());

		// Create a pager
		{
			m_pager = new VibeSimplePager();
			m_pager.setDisplay(m_groupsTable);
		}

		mainPanel.add(menuPanel);
		mainPanel.add(m_groupsTable);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.add(m_pager);
		mainPanel.setCellHeight(m_pager, "100%");

		return mainPanel;
	}

	/**
	 * Delete the selected groups.
	 */
	private void deleteSelectedGroups() {
		Set<GroupInfoPlus> selectedGroups;
		Iterator<GroupInfoPlus> groupIterator;
		String grpNames;
		int count = 0;

		grpNames = "";

		selectedGroups = getSelectedGroups();
		if (selectedGroups != null) {
			// Get a list of all the selected group names
			groupIterator = selectedGroups.iterator();
			while (groupIterator.hasNext()) {
				GroupInfoPlus nextGroup;

				nextGroup = groupIterator.next();

				if (nextGroup.getStatus().isReady()) {
					GroupInfo groupInfo;
					String text;

					groupInfo = nextGroup.getGroupInfo();

					text = groupInfo.getTitle();
					if (text == null || text.length() == 0)
						text = groupInfo.getName();
					grpNames += "\t" + text + "\n";

					++count;
				}
			}
		}

		// Do we have any groups to delete?
		if (count > 0) {
			String msg;

			// Yes, ask the user if they want to delete the selected groups?
			msg = GwtTeaming.getMessages().manageGroupsDlgConfirmDelete(
					grpNames);
			if (Window.confirm(msg)) {
				deleteGroupsFromServer(selectedGroups);
			}
		} else {
			Window.alert(GwtTeaming.getMessages()
					.manageGroupsDlgSelectGroupToDelete());
		}
	}

	/**
	 * 
	 */
	private void deleteGroupsFromServer(Set<GroupInfoPlus> selectedGroups) {
		ArrayList<GroupInfo> listOfGroupsToDelete;
		final ArrayList<GroupInfo> listOfGroupsToDeleteFinal;
		Iterator<GroupInfoPlus> groupIterator;

		listOfGroupsToDelete = new ArrayList<GroupInfo>();

		// Get a list of GroupInfo objects
		groupIterator = selectedGroups.iterator();
		while (groupIterator.hasNext()) {
			GroupInfoPlus nextGroup;

			nextGroup = groupIterator.next();
			if (nextGroup.getStatus().isReady()) {
				GroupInfo groupInfo;

				groupInfo = nextGroup.getGroupInfo();

				// Add this group to the list of groups to be deleted.
				listOfGroupsToDelete.add(groupInfo);

				// Update the status of this group.
				nextGroup
						.setStatus(GroupModificationStatus.GROUP_DELETION_IN_PROGRESS);
			}
		}

		listOfGroupsToDeleteFinal = listOfGroupsToDelete;
		if (listOfGroupsToDeleteFinal != null
				&& listOfGroupsToDeleteFinal.size() > 0) {
			DeleteGroupsCmd cmd;
			AsyncCallback<VibeRpcResponse> rpcCallback = null;

			// Create the callback that will be used when we issue an ajax call
			// to delete the groups.
			rpcCallback = new AsyncCallback<VibeRpcResponse>() {
				/**
				 * 
				 */
				@Override
				public void onFailure(final Throwable t) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							GwtClientHelper.handleGwtRPCFailure(t, GwtTeaming
									.getMessages().rpcFailure_DeleteGroups());

							// Spin through the list of groups that were to be
							// deleted
							// are set their status to ready. We don't know
							// which
							// groups actually got deleted.
							for (GroupInfo nextGroup : listOfGroupsToDeleteFinal) {
								GroupInfoPlus nextGroupPlus;

								nextGroupPlus = findGroupById(nextGroup.getId());
								if (nextGroupPlus != null)
									nextGroupPlus
											.setStatus(GroupModificationStatus.READY);
							}

							// Update the table to reflect the fact that we
							// deleted a group.
							m_dataProvider.refresh();
						}
					});
				}

				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess(final VibeRpcResponse response) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						/**
						 * 
						 */
						@Override
						public void execute() {
							// Spin through the list of groups we just deleted
							// and remove
							// them from the table.
							for (GroupInfo nextGroup : listOfGroupsToDeleteFinal) {
								GroupInfoPlus nextGroupPlus;

								nextGroupPlus = findGroupById(nextGroup.getId());
								if (nextGroupPlus != null)
									m_listOfGroups.remove(nextGroupPlus);
							}

							// Update the table to reflect the fact that we
							// deleted a group.
							m_dataProvider.refresh();

							// Tell the table how many groups we have.
							m_groupsTable.setRowCount(m_listOfGroups.size(),
									true);
						}
					});
				}
			};

			// Update the table to reflect the fact group deletion is in
			// progress
			m_dataProvider.refresh();

			// Issue an ajax request to get delete the list of groups.
			cmd = new DeleteGroupsCmd(listOfGroupsToDeleteFinal);
			GwtClientHelper.executeCommand(cmd, rpcCallback);
		}
	}

	/**
	 * Find the given group in our list of groups.
	 */
	private GroupInfoPlus findGroupById(Long id) {
		if (m_listOfGroups != null && id != null) {
			for (GroupInfoPlus nextGroup : m_listOfGroups) {
				GroupInfo groupInfo;

				groupInfo = nextGroup.getGroupInfo();
				if (groupInfo.getId() == id)
					return nextGroup;
			}
		}

		// If we get here we did not find the group.
		return null;
	}

	/*
	 * Find the given group by name in our list of groups.
	 */
	@SuppressWarnings("unused")
	private GroupInfoPlus findNewGroupByName(String name) {
		if (m_listOfGroups != null && name != null) {
			for (GroupInfoPlus nextGroup : m_listOfGroups) {
				GroupInfo groupInfo;

				groupInfo = nextGroup.getGroupInfo();
				if (name.equalsIgnoreCase(groupInfo.getName())) {
					Long id;

					// We are looking for a group that doesn't existing the db
					// yet.
					id = groupInfo.getId();
					if (id != null && id == -1)
						return nextGroup;
				}
			}
		}

		// If we get here we did not find the group.
		return null;
	}

	/*
	 * Asynchronously loads the first piece of information to run the
	 * dialog.
	 */
	private void loadPart1Async( final String filter )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				loadPart1Now( filter );
			}
		} );
	}
	
	/*
	 * Asynchronously loads the first piece of information to run the
	 * dialog.
	 */
	private void loadPart1Now( final String filter )
	{
		GwtClientHelper.executeCommand(
			new GetPersonalPrefsCmd(),
			new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure( Throwable caught )
				{
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						GwtTeaming.getMessages().rpcFailure_GetPersonalPreferences());
					loadPart2Async( filter );
				}

				@Override
				public void onSuccess( VibeRpcResponse result )
				{
					GwtPersonalPreferences prefs = ( (GwtPersonalPreferences) result.getResponseData() );
					int pageSize = ((null == prefs) ? 0 : prefs.getNumEntriesPerPage());
					if ((0 < pageSize) && (pageSize != m_pageSize))
					{
						m_pageSize = pageSize;
						m_groupsTable.setPageSize( m_pageSize );
					}
					loadPart2Async( filter );
				}
			} );
	}
	
	/*
	 * Asynchronously loads the next piece of information to run the
	 * dialog.
	 */
	private void loadPart2Async( final String filter )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				getAllGroupsFromServer( filter );
			}
		} );
	}
	
	/*
	 * Issue an ajax request to get a list of all the groups.
	 */
	private void getAllGroupsFromServer( String filter )
	{
		GetAllGroupsCmd cmd;
		AsyncCallback<VibeRpcResponse> rpcCallback = null;

		// Create the callback that will be used when we issue an ajax call to
		// get all the groups.
		rpcCallback = new AsyncCallback<VibeRpcResponse>() {
			/**
			 * 
			 */
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(t, GwtTeaming.getMessages()
						.rpcFailure_GetAllGroups());
			}

			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess(final VibeRpcResponse response) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					/**
					 * 
					 */
					@Override
					public void execute() {
						GetGroupsRpcResponseData responseData;

						responseData = (GetGroupsRpcResponseData) response
								.getResponseData();

						// Add the groups to the ui
						if (responseData != null)
							addGroups(responseData.getGroups());
					}
				});
			}
		};

		// Issue an ajax request to get a list of all the groups.
		cmd = new GetAllGroupsCmd();
		cmd.setFilter( filter );
		GwtClientHelper.executeCommand(cmd, rpcCallback);
	}

	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg() {
		// Return something. Doesn't matter what since we only have a close
		// button.
		return Boolean.TRUE;
	}

	/**
	 * Return the widget that should get the focus when the dialog is shown.
	 */
	@Override
	public FocusWidget getFocusWidget() {
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
		helpData.setPageId( "groups_manage" );
		
		return helpData;
	}
	
	/**
	 * Return a list of selected groups.
	 */
	public Set<GroupInfoPlus> getSelectedGroups() {
		return m_selectionModel.getSelectedSet();
	}

	/**
	 * 
	 */
	public void init()
	{
		// Begin the process of loading the contents of the dialog.
		loadPart1Async( null );
	}

	/**
	 * Invoke the "Add Group" dialog.
	 */
	private void invokeAddGroupDlg() {
		invokeModifyGroupDlg(null);
	}

	/**
	 * 
	 */
	private void invokeModifyGroupDlg(final GroupInfo groupInfo) {
		int x;
		int y;

		// Get the position of this dialog.
		x = getAbsoluteLeft() + 50;
		y = getAbsoluteTop() + 50;

		if (m_modifyGroupDlg == null) {
			ModifyGroupDlg.createAsync( false, true, x, y,
					new ModifyGroupDlgClient() {
						@Override
						public void onUnavailable() {
							// Nothing to do. Error handled in asynchronous
							// provider.
						}

						@Override
						public void onSuccess(final ModifyGroupDlg mgDlg) {
							GwtClientHelper.deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									m_modifyGroupDlg = mgDlg;

									m_modifyGroupDlg.init(groupInfo);
									m_modifyGroupDlg.show();
								}
							});
						}
					});
		} else {
			m_modifyGroupDlg.init(groupInfo);
			m_modifyGroupDlg.setPopupPosition(x, y);
			m_modifyGroupDlg.show();
		}
	}

	/**
	 * Handles the GroupCreatedEvent received by this class
	 */
	@Override
	public void onGroupCreated(GroupCreatedEvent event) {
		GroupInfo createdGroupInfo;

		// Get the newly created group.
		createdGroupInfo = event.getGroupInfo();

		if ( createdGroupInfo != null )
		{
			GroupInfoPlus newGroupInfoPlus;

			newGroupInfoPlus = new GroupInfoPlus( createdGroupInfo, GroupModificationStatus.READY );

			// Add the group as the first group in the list.
			m_listOfGroups.add(0, newGroupInfoPlus);

			// Update the table to reflect the new group we just created.
			m_dataProvider.refresh();

			// Go to the first page.
			m_pager.firstPage();

			// Select the newly created group.
			m_selectionModel.setSelected(newGroupInfoPlus, true);

			// Tell the table how many groups we have.
			m_groupsTable.setRowCount(m_listOfGroups.size(), true);
		}
	}

	/**
	 * Handles the GroupMembershipModificationFailedEvent received by this class
	 */
	@Override
	public void onGroupMembershipModificationFailed( GroupMembershipModificationFailedEvent event )
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
											GwtTeaming.getMessages().rpcFailure_ModifyGroupMembership() );

			// Find this group in our list of groups.
			id = groupInfo.getId();
			groupInfoPlus = findGroupById( id );

			if ( groupInfoPlus != null )
			{
				// Set the group's modification state to ready
				groupInfoPlus.setStatus( GroupModificationStatus.READY );

				// Update the table to reflect the fact that this group's status changed
				m_dataProvider.refresh();
			}
		}
	}

	/**
	 * Handles the GroupMembershipModificationStartedEvent received by this class
	 */
	@Override
	public void onGroupMembershipModificationStarted( GroupMembershipModificationStartedEvent event )
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
			groupInfoPlus = findGroupById(id);

			if ( groupInfoPlus != null )
			{
				// Set the group's modification state to group membership modification in progress
				groupInfoPlus.setStatus( GroupModificationStatus.GROUP_MEMBERSHIP_MODIFICATION_IN_PROGRESS );

				// Update the table to reflect the fact that this group is being modified.
				m_dataProvider.refresh();
			}
		}
	}

	/**
	 * Handles the GroupMembershipModifiedEvent received by this class
	 */
	@Override
	public void onGroupMembershipModified( GroupMembershipModifiedEvent event )
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
				@SuppressWarnings("unused")
				GroupInfo groupInfo;

				groupInfo = groupInfoPlus.getGroupInfo();

				// Update the status of the group.
				groupInfoPlus.setStatus( GroupModificationStatus.READY );

				// Update the table to reflect the fact that a group was modified.
				m_dataProvider.refresh();
			}
		}
	}

	/**
	 * Handles the GroupModificationFailedEvent received by this class
	 */
	@Override
	public void onGroupModificationFailed(GroupModificationFailedEvent event) {
		GroupInfo groupInfo;

		// Get the GroupInfo passed in the event.
		groupInfo = event.getGroupInfo();

		if (groupInfo != null) {
			GroupInfoPlus groupInfoPlus;
			Long id;

			// Tell the user about the error
			GwtClientHelper.handleGwtRPCFailure(event.getException(),
					GwtTeaming.getMessages().rpcFailure_ModifyGroup());

			// Find this group in our list of groups.
			id = groupInfo.getId();
			groupInfoPlus = findGroupById(id);

			if (groupInfoPlus != null) {
				// Set the group's modification state to ready
				groupInfoPlus.setStatus(GroupModificationStatus.READY);

				// Update the table to reflect the fact that this group is being
				// modified.
				m_dataProvider.refresh();
			}
		}
	}

	/**
	 * Handles the GroupModificationStartedEvent received by this class
	 */
	@Override
	public void onGroupModificationStarted(GroupModificationStartedEvent event) {
		GroupInfo groupInfo;

		// Get the GroupInfo passed in the event.
		groupInfo = event.getGroupInfo();

		if (groupInfo != null) {
			GroupInfoPlus groupInfoPlus;
			Long id;

			// Find this group in our list of groups.
			id = groupInfo.getId();
			groupInfoPlus = findGroupById(id);

			if (groupInfoPlus != null) {
				// Set the group's modification state to modification in
				// progress
				groupInfoPlus
						.setStatus(GroupModificationStatus.GROUP_MODIFICATION_IN_PROGRESS);

				// Update the table to reflect the fact that this group is being
				// modified.
				m_dataProvider.refresh();
			}
		}
	}

	/**
	 * Handles the GroupModifiedEvent received by this class
	 */
	@Override
	public void onGroupModified(GroupModifiedEvent event) {
		GroupInfo modifiedGroup;

		// Get the GroupInfo passed in the event.
		modifiedGroup = event.getGroupInfo();

		if (modifiedGroup != null) {
			GroupInfoPlus groupInfoPlus;

			// Find this group in our list of groups.
			groupInfoPlus = findGroupById(modifiedGroup.getId());

			if (groupInfoPlus != null) {
				GroupInfo groupInfo;

				groupInfo = groupInfoPlus.getGroupInfo();

				// Update the group with the title and description from the
				// group that
				// was passed in the event.
				groupInfo.setTitle(modifiedGroup.getTitle());
				groupInfo.setDesc(modifiedGroup.getDesc());

				// Update the status of the group.
				groupInfoPlus.setStatus(GroupModificationStatus.READY);

				// Update the table to reflect the fact that a group was
				// modified.
				m_dataProvider.refresh();
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
		if ( event.getFolderId().equals( MANAGE_GROUPS_ID ) )
		{
			final String filter;
			
			// Yes.  Search for groups using the filter entered by the user.
			filter = event.getQuickFilter();
			
			GwtClientHelper.deferCommand(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					getAllGroupsFromServer( filter );
				}
			});
		}
	}

	/**
	 * Loads the ManageGroupsDlg split point and returns an instance of it via
	 * the callback.
	 * 
	 */
	public static void createAsync(final boolean autoHide, final boolean modal,
			final int left, final int top, final int width, final int height,
			final ManageGroupsDlgClient mgDlgClient) {
		GWT.runAsync(ManageGroupsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages()
						.codeSplitFailure_ManageGroupsDlg());
				if (mgDlgClient != null) {
					mgDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				ManageGroupsDlg mgDlg;

				mgDlg = new ManageGroupsDlg(autoHide, modal, left, top, width,
						height);
				mgDlgClient.onSuccess(mgDlg);
			}
		});
	}
	
	/*
	 * Renders HTML for used in a drop down menu item.
	 */
	private String renderDropdownMenuItemHtml(String itemText) {
		FlowPanel htmlPanel = new FlowPanel();
		InlineLabel itemLabel = new InlineLabel(itemText);
		itemLabel.addStyleName("groupManagementDropDownText");
		htmlPanel.add(itemLabel);

		Image dropDownImg = new Image(GwtTeaming.getMainMenuImageBundle().menuArrow());
		dropDownImg.addStyleName("groupManagementDropDownImg");
		if (!GwtClientHelper.jsIsIE()) {
			dropDownImg.addStyleName("groupManagementDropDownImgNonIE");
		}
		htmlPanel.add(dropDownImg);
		
		return htmlPanel.getElement().getInnerHTML();
	}
	
	/*
	 * Populates the More popup menu.
	 */
	private void populateMorePopup(PopupMenu morePopup) {
		final String emptyWarning = m_messages.manageGroupsDlgSelectGroupsToModify();
		if (GwtClientHelper.isLicenseFilr()) {
			// Personal storage options.
			morePopup.addMenuItem(
				new Command() {
					@Override
					public void execute() {
						List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
						if (!(groups.isEmpty())) {
							BinderViewsHelper.disableUsersAdHocFolders(groups);
						}
					}
				},
				null,
				m_messages.manageGroupsDlgPersonalStorage_Disable());
			
			morePopup.addMenuItem(
				new Command() {
					@Override
					public void execute() {
						List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
						if (!(groups.isEmpty())) {
							BinderViewsHelper.enableUsersAdHocFolders(groups);
						}
					}
				},
				null,
				m_messages.manageGroupsDlgPersonalStorage_Enable());
			
			morePopup.addMenuItem(
				new Command() {
					@Override
					public void execute() {
						List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
						if (!(groups.isEmpty())) {
							BinderViewsHelper.clearUsersAdHocFolders(groups);
						}
					}
				},
				null,
				m_messages.manageGroupsDlgPersonalStorage_Clear());

			// Download options.
			morePopup.addSeparator();
			morePopup.addMenuItem(
				new Command() {
					@Override
					public void execute() {
						List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
						if (!(groups.isEmpty())) {
							BinderViewsHelper.disableUsersDownload(groups);
						}
					}
				},
				null,
				m_messages.manageGroupsDlgDownload_Disable());
				
			morePopup.addMenuItem(
				new Command() {
					@Override
					public void execute() {
						List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
						if (!(groups.isEmpty())) {
							BinderViewsHelper.enableUsersDownload(groups);
						}
					}
				},
				null,
				m_messages.manageGroupsDlgDownload_Enable());
				
			morePopup.addMenuItem(
				new Command() {
					@Override
					public void execute() {
						List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
						if (!(groups.isEmpty())) {
							BinderViewsHelper.clearUsersDownload(groups);
						}
					}
				},
				null,
				m_messages.manageGroupsDlgDownload_Clear());
			
			morePopup.addSeparator();
		}
		
		// WebAccess options.
		morePopup.addMenuItem(
			new Command() {
				@Override
				public void execute() {
					List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
					if (!(groups.isEmpty())) {
						BinderViewsHelper.disableUsersWebAccess(groups);
					}
				}
			},
			null,
			m_messages.manageGroupsDlgWebAccess_Disable());
			
		morePopup.addMenuItem(
			new Command() {
				@Override
				public void execute() {
					List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
					if (!(groups.isEmpty())) {
						BinderViewsHelper.enableUsersWebAccess(groups);
					}
				}
			},
			null,
			m_messages.manageGroupsDlgWebAccess_Enable());
			
		morePopup.addMenuItem(
			new Command() {
				@Override
				public void execute() {
					List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true -> Ready only.
					if (!(groups.isEmpty())) {
						BinderViewsHelper.clearUsersWebAccess(groups);
					}
				}
			},
			null,
			m_messages.manageGroupsDlgWebAccess_Clear());
		
		// Desktop Application and Mobile Application settings.
		morePopup.addSeparator();
		morePopup.addMenuItem(
			new Command() {
				@Override
				public void execute() {
					List<Long> groups = getSelectedGroupIds(true, emptyWarning);								// true  -> Ready only.
					if (!(groups.isEmpty())) {
						GwtTeaming.fireEventAsync(new InvokePrincipalDesktopSettingsDlgEvent(groups, false));	// false -> IDs are groups.
					}
				}
			},
			null,
			m_messages.manageGroupsDlgDesktopAppSettings());
			
		if ( GwtClientHelper.showFilrFeatures() )
		{
			morePopup.addMenuItem(
				new Command()
				{
					@Override
					public void execute()
					{
						List<Long> groups = getSelectedGroupIds( true, emptyWarning );									// true  -> Ready only.
						if ( ! ( groups.isEmpty() ) )
						{
							GwtTeaming.fireEventAsync( new InvokePrincipalMobileSettingsDlgEvent( groups, false ) );	// false -> IDs are groups.
						}
					}
				},
				null,
				m_messages.manageGroupsDlgMobileAppSettings() );
		}

		// Is this the built-in admin user?
		if (GwtClientHelper.isBuiltInAdmin()) {
			// Admin rights options.
			morePopup.addSeparator();
			morePopup.addMenuItem(
				new Command() {
					@Override
					public void execute() {
						List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true  -> Ready only.
						if (!(groups.isEmpty())) {
							setAdminRightsAsync(groups, true);
						}
					}
				},
				null,
				m_messages.manageGroupsDlgAdminRightsSet());
			morePopup.addMenuItem(
				new Command() {
					@Override
					public void execute() {
						List<Long> groups = getSelectedGroupIds(true, emptyWarning);	// true  -> Ready only.
						if (!(groups.isEmpty())) {
							setAdminRightsAsync(groups, false);
						}
					}
				},
				null,
				m_messages.manageGroupsDlgAdminRightsClear());
		}
	}

	/*
	 * Returns a List<Long> of the selected group IDs.
	 */
	private List<Long> getSelectedGroupIds(boolean readyOnly, String emptyWarning) {
		List<Long> reply = new ArrayList<Long>();
		Set<GroupInfoPlus> selectedGroups = getSelectedGroups();
		if (GwtClientHelper.hasItems(selectedGroups)) {
			for (GroupInfoPlus g:  selectedGroups) {
				if ((!readyOnly) || g.getStatus().isReady()) {
					reply.add(g.getGroupInfo().getId());
				}
			}
		}
		
		if (GwtClientHelper.hasString(emptyWarning) && reply.isEmpty()) {
			GwtClientHelper.deferredAlert(emptyWarning);
		}
		return reply;
	}
	
	@SuppressWarnings("unused")
	private List<Long> getSelectedGroupIds(boolean readyOnly) {
		// Always use the initial form of the method.
		return getSelectedGroupIds(readyOnly, null);
	}

	/*
	 * Asynchronously sets or clears the admin rights on the selected
	 * groups.
	 */
	private void setAdminRightsAsync(final List<Long> groups, final boolean setRights) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				setAdminRightsNow(groups, setRights);
			}
		});
	}
	
	/*
	 * Synchronously sets or clears the admin rights on the selected
	 * groups.
	 */
	private void setAdminRightsNow(final List<Long> groups, final boolean setRights) {
	    showDlgBusySpinner();
		SetPrincipalsAdminRightsCmd cmd = new SetPrincipalsAdminRightsCmd(groups, setRights);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
			    hideDlgBusySpinner();
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetPrincipalsAdminRights());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// We're done.  If we had any errors...
			    hideDlgBusySpinner();
			    SetPrincipalsAdminRightsRpcResponseData responseData = ((SetPrincipalsAdminRightsRpcResponseData) response.getResponseData()); 
				List<ErrorInfo> erList = responseData.getErrorList();
				if ( GwtClientHelper.hasItems( erList ) )
				{
					// ...display them.
					GwtClientHelper.displayMultipleErrors( m_messages.manageGroupsDlg_Error_SavingAdminRights(), erList );
				}

				// If we changed anything...
				final Map<Long, AdminRights> adminRightsChangeMap = responseData.getAdminRightsChangeMap();
				if ( GwtClientHelper.hasItems( adminRightsChangeMap ) )
				{
					// ...update the table to reflect the fact that a
					// ...group was modified.
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							List<GroupInfoPlus>	rows   = m_groupsTable.getVisibleItems();
							Set<Long>			keySet = adminRightsChangeMap.keySet();
							for ( Long key:  keySet )
							{
								int rowIndex = 0;
								for ( GroupInfoPlus row : rows ) {
									Long rowId = row.getGroupInfo().getId();
									if ( rowId.equals( key ) )
									{
										row.getGroupInfo().setAdmin( adminRightsChangeMap.get( key ).isAdmin() );
										m_groupsTable.redrawRow( rowIndex );
										break;
									}
									rowIndex += 1;
								}
							}
						}
					} );
				}
			}
		});
	}
}
