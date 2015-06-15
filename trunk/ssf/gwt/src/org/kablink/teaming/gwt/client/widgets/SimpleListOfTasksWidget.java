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

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingTaskListingImageBundle;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeSimpleProfileEvent;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.rpc.shared.AssignmentInfoListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupAssigneeMembershipCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTeamAssigneeMembershipCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * This class is used to display simple list of tasks.
 * 
 * @author jwootton
 */
public class SimpleListOfTasksWidget extends VibeWidget
{
	/**
	 * This class is used as the click handler when the user clicks a person assigned to a task
	 *
	 */
	private class PersonClickHandler implements ClickHandler
	{
		private Element m_element;
		private String m_userId;
		private String m_workspaceId;
		private String m_name;
		
		/**
		 * 
		 */
		public PersonClickHandler( Element element, Long userId, Long workspaceId, String name )
		{
			super();
			
			m_element = element;
			m_userId = String.valueOf( userId );
			m_workspaceId = null;
			if ( workspaceId != null )
				m_workspaceId = String.valueOf( workspaceId );
			m_name = name;
		}

		/**
		 * 
		 */
		private void handleClickOnLink()
		{
			if ( GwtClientHelper.hasString( m_workspaceId ) )
			{
				SimpleProfileParams params;
				
				// Invoke the Simple Profile dialog.
				params = new SimpleProfileParams( m_element, m_userId, m_workspaceId, m_name );
				GwtTeaming.fireEvent(new InvokeSimpleProfileEvent( params ));
			}
		}
		
		/**
		 * 
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					handleClickOnLink();
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * This class is used as the click handler when the user clicks a group assigned to a task
	 *
	 */
	private class GroupClickHandler implements ClickHandler
	{
		private VibeFlowPanel m_groupMembersPanel;
		private Long m_groupId;
		private List<AssignmentInfo> m_groupMembers;
		
		/**
		 * 
		 */
		public GroupClickHandler( VibeFlowPanel groupMembersPanel, Long groupId )
		{
			super();

			m_groupMembersPanel = groupMembersPanel;
			m_groupId = groupId;
			m_groupMembers = null;
		}
		
		/**
		 * Add the members found in m_groupMembers to m_groupMembersPanel
		 */
		private void addGroupMembers()
		{
			if ( m_groupMembers != null && m_groupMembersPanel != null )
			{
				for (AssignmentInfo assignmentInfo: m_groupMembers)
				{
					// Are we dealing with a group or a person?
					if ( assignmentInfo.getPresence() == null )
					{
						// This is a group.
						addAssignedGroup( m_groupMembersPanel, assignmentInfo );
					}
					else
					{
						// This is a person
						addAssignedPerson( m_groupMembersPanel, assignmentInfo );
					}
				}
			}
		}

		/**
		 * 
		 */
		private void handleClickOnGroup()
		{
			if ( m_groupMembersPanel != null )
			{
				// Hide/show the group membership panel.
				m_groupMembersPanel.setVisible( !m_groupMembersPanel.isVisible( ) );
			}
		}
		
		/**
		 * 
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Have we already retrieved the group membership?
					if ( m_groupMembers != null )
					{
						// Yes
						handleClickOnGroup();
					}
					else
					{
						GetGroupAssigneeMembershipCmd groupCmd;

						// No, issue a rpc request to get the group membership
						groupCmd = new GetGroupAssigneeMembershipCmd( m_groupId );
						GwtClientHelper.executeCommand( groupCmd, new AsyncCallback<VibeRpcResponse>()
						{
							@Override
							public void onFailure( Throwable caught )
							{
								GwtClientHelper.handleGwtRPCFailure(
									caught,
									GwtTeaming.getMessages().rpcFailure_GetGroupMembership(),
									String.valueOf( m_groupId ) );
							}

							@Override
							public void onSuccess( VibeRpcResponse result )
							{
								AssignmentInfoListRpcResponseData responseData;
								Scheduler.ScheduledCommand cmd2;

								// Store the group membership (so we don't re-read
								// it if it gets displayed again) and display it.
								responseData = ((AssignmentInfoListRpcResponseData) result.getResponseData());
								m_groupMembers = responseData.getAssignmentInfoList();
								
								cmd2 = new Scheduler.ScheduledCommand()
								{
									@Override
									public void execute()
									{
										// Add the group members to the ui.
										addGroupMembers();
										
										handleClickOnGroup();
									}
								};
								Scheduler.get().scheduleDeferred( cmd2 );
							}				
						});
					}
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * This class is used as the click handler when the user clicks a team assigned to a task
	 *
	 */
	private class TeamClickHandler implements ClickHandler
	{
		private VibeFlowPanel m_teamMembersPanel;
		private Long m_teamId;
		private List<AssignmentInfo> m_teamMembers;
		
		/**
		 * 
		 */
		public TeamClickHandler( VibeFlowPanel teamMembersPanel, Long teamId )
		{
			super();

			m_teamMembersPanel = teamMembersPanel;
			m_teamId = teamId;
			m_teamMembers = null;
		}
		
		/**
		 * Add the members found in m_teamMembers to m_teamMembersPanel
		 */
		private void addTeamMembers()
		{
			if ( m_teamMembers != null && m_teamMembersPanel != null )
			{
				for (AssignmentInfo assignmentInfo: m_teamMembers)
				{
					// Are we dealing with a group or a person?
					if ( assignmentInfo.getPresence() == null )
					{
						// This is a group.
						addAssignedGroup( m_teamMembersPanel, assignmentInfo );
					}
					else
					{
						// This is a person
						addAssignedPerson( m_teamMembersPanel, assignmentInfo );
					}
				}
			}
		}

		/**
		 * 
		 */
		private void handleClickOnTeam()
		{
			if ( m_teamMembersPanel != null )
			{
				// Hide/show the team membership panel.
				m_teamMembersPanel.setVisible( !m_teamMembersPanel.isVisible( ) );
			}
		}
		
		/**
		 * 
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Have we already retrieved the team membership?
					if ( m_teamMembers != null )
					{
						// Yes
						handleClickOnTeam();
					}
					else
					{
						GetTeamAssigneeMembershipCmd teamCmd;

						// No, issue a rpc request to get the team membership
						teamCmd = new GetTeamAssigneeMembershipCmd( m_teamId );
						GwtClientHelper.executeCommand( teamCmd, new AsyncCallback<VibeRpcResponse>()
						{
							@Override
							public void onFailure( Throwable caught )
							{
								GwtClientHelper.handleGwtRPCFailure(
									caught,
									GwtTeaming.getMessages().rpcFailure_GetTeamMembership(),
									String.valueOf( m_teamId ) );
							}

							@Override
							public void onSuccess( VibeRpcResponse result )
							{
								AssignmentInfoListRpcResponseData responseData;
								Scheduler.ScheduledCommand cmd2;

								// Store the team membership (so we don't re-read
								// it if it gets displayed again) and display it.
								responseData = ((AssignmentInfoListRpcResponseData) result.getResponseData());
								m_teamMembers = responseData.getAssignmentInfoList();

								cmd2 = new Scheduler.ScheduledCommand()
								{
									@Override
									public void execute()
									{
										// Add the team members to the ui.
										addTeamMembers();
										
										handleClickOnTeam();
									}
								};
								Scheduler.get().scheduleDeferred( cmd2 );
							}
						});
					}
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	/**
	 * This class is used as the click handler when the user clicks on the title of a task.
	 *
	 */
	private class TaskClickHandler implements ClickHandler
	{
		private TaskInfo m_taskInfo;
		private String m_viewTaskUrl;
		
		/**
		 * 
		 */
		public TaskClickHandler( TaskInfo taskInfo )
		{
			super();
			
			m_taskInfo = taskInfo;
			m_viewTaskUrl = null;
		}

		/**
		 * 
		 */
		private void handleClickOnLink()
		{
			// Do we have the url needed to view this task?
			if ( GwtClientHelper.hasString( m_viewTaskUrl ) )
			{
				// Yes, Fire the "view entry" event.
				GwtTeaming.fireEvent( new ViewForumEntryEvent( m_viewTaskUrl ) );
			}
		}
		
		/**
		 * 
		 */
		@Override
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					// Do we have the url needed to view this task?
					if ( GwtClientHelper.hasString( m_viewTaskUrl ) )
					{
						// Yes
						handleClickOnLink();
					}
					else if ( m_taskInfo != null )
					{
						GetViewFolderEntryUrlCmd cmd;

						// No, issue an rpc request to get the needed url
						cmd = new GetViewFolderEntryUrlCmd( m_taskInfo.getTaskId().getBinderId(), m_taskInfo.getTaskId().getEntityId() );
						GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
						{
							@Override
							public void onFailure(Throwable t)
							{
								GwtClientHelper.handleGwtRPCFailure(
									t,
									GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
									String.valueOf( m_taskInfo.getTaskId().getEntityId() ) );
							}
							
							@Override
							public void onSuccess( VibeRpcResponse response )
							{
								Scheduler.ScheduledCommand cmd2;
								
								m_viewTaskUrl = ((StringRpcResponseData) response.getResponseData()).getStringValue();
								
								cmd2 = new Scheduler.ScheduledCommand()
								{
									@Override
									public void execute()
									{
										handleClickOnLink();
									}
								};
								Scheduler.get().scheduleDeferred( cmd2 );
							}
						});
					}
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
		}
	}
	
	private int m_numTasksDisplayed;
	private int m_maxTasksToDisplay;
	private String m_style;
	private WidgetStyles m_widgetStyles;
	private FlexTable m_tasksTable;
	private FlexTable.FlexCellFormatter m_cellFormatter;

	/**
	 * 
	 */
	public SimpleListOfTasksWidget( int maxTasksToDisplay, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		m_numTasksDisplayed = 0;
		m_maxTasksToDisplay = maxTasksToDisplay;
		mainPanel = init( widgetStyles, landingPageStyle );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * Add the given assigned group to the given panel.
	 */
	private void addAssignedGroup( VibeFlowPanel assignedToPanel, AssignmentInfo assignedGroup )
	{
		VibeFlowPanel tmpPanel;
		InlineLabel groupName;
		Image assigneeImg;
		int numMembers;
		String membersString;
		
		// Create a panel for the groups's info to live in.
		tmpPanel = new VibeFlowPanel();
		tmpPanel.addStyleName( "taskFolderWidgetAssignedGroupPanel" + m_style );
		
		assigneeImg = new Image();
		assigneeImg.setUrl( GwtClientHelper.getRequestInfo().getImagesPath() + assignedGroup.getPresenceDude() );
		assigneeImg.getElement().setAttribute( "align", "absmiddle" );
		
		// Create the label with the name of the group (number of members in the group)
		numMembers = assignedGroup.getMembers();
		membersString = GwtTeaming.getMessages().taskMemberCount( String.valueOf( numMembers ) );
		groupName = new InlineLabel( assignedGroup.getTitle() + " " + membersString );
		groupName.addStyleName( "taskFolderWidgetAssignedGroup" + m_style );
		GwtClientHelper.setElementTextColor( groupName.getElement(), m_widgetStyles.getContentTextColor() );
		tmpPanel.add( groupName );
		
		tmpPanel.add( assigneeImg );
		tmpPanel.add( groupName );
		
		// Are there any members of the group?
		if ( numMembers > 0 )
		{
			VibeFlowPanel membersPanel;
			GroupClickHandler groupClickHandler;
			
			// Yes
			membersPanel = new VibeFlowPanel();
			membersPanel.addStyleName( "taskFolderWidgetGroupMembersPanel" + m_style );
			membersPanel.setVisible( false );
			
			// Add a handler that will handle when the user clicks on the group
			groupClickHandler = new GroupClickHandler( membersPanel, assignedGroup.getId() );
			groupName.addClickHandler( groupClickHandler );
			
			tmpPanel.add( membersPanel );
		}

		assignedToPanel.add( tmpPanel );
	}
	
	
	/**
	 * Add the given assigned person to the given panel.
	 */
	private void addAssignedPerson( VibeFlowPanel assignedToPanel, AssignmentInfo assignedPerson )
	{
		VibeFlowPanel panel;
		InlineLabel userName;
		PersonClickHandler personClickHandler;
		
		panel = new VibeFlowPanel();
		userName = new InlineLabel( assignedPerson.getTitle() );
		userName.addStyleName( "taskFolderWidgetAssignedPerson" + m_style );
		GwtClientHelper.setElementTextColor( userName.getElement(), m_widgetStyles.getContentTextColor() );
		panel.add( userName );
		
		// Create a click handler
		personClickHandler = new PersonClickHandler( userName.getElement(), assignedPerson.getId(), assignedPerson.getPresenceUserWSId(), assignedPerson.getTitle() );
		userName.addClickHandler( personClickHandler );
		
		assignedToPanel.add( panel );
	}
	
	/**
	 * Add the given assigned team to the given panel
	 */
	private void addAssignedTeam( VibeFlowPanel assignedToPanel, AssignmentInfo assignedTeam )
	{
		VibeFlowPanel tmpPanel;
		InlineLabel teamName;
		Image assigneeImg;
		int numMembers;
		String membersString;
		
		// Create a panel for the team's info to live in.
		tmpPanel = new VibeFlowPanel();
		tmpPanel.addStyleName( "taskFolderWidgetAssignedTeamPanel" + m_style );
		
		assigneeImg = new Image();
		assigneeImg.setUrl( GwtClientHelper.getRequestInfo().getImagesPath() + assignedTeam.getPresenceDude() );
		assigneeImg.getElement().setAttribute( "align", "absmiddle" );
		
		// Create the label with the name of the team (number of members in the team)
		numMembers = assignedTeam.getMembers();
		membersString = GwtTeaming.getMessages().taskMemberCount( String.valueOf( numMembers ) );
		teamName = new InlineLabel( assignedTeam.getTitle() + " " + membersString );
		teamName.addStyleName( "taskFolderWidgetAssignedTeam" + m_style );
		GwtClientHelper.setElementTextColor( teamName.getElement(), m_widgetStyles.getContentTextColor() );
		tmpPanel.add( teamName );
		
		tmpPanel.add( assigneeImg );
		tmpPanel.add( teamName );
		
		// Are there any members of the team?
		if ( numMembers > 0 )
		{
			VibeFlowPanel membersPanel;
			TeamClickHandler teamClickHandler;
			
			// Yes
			membersPanel = new VibeFlowPanel();
			membersPanel.addStyleName( "taskFolderWidgetTeamMembersPanel" + m_style );
			membersPanel.setVisible( false );
			
			// Add a handler that will handle when the user clicks on the team
			teamClickHandler = new TeamClickHandler( membersPanel, assignedTeam.getId() );
			teamName.addClickHandler( teamClickHandler );
			
			tmpPanel.add( membersPanel );
		}

		assignedToPanel.add( tmpPanel );
	}
	
	/**
	 * Add the given assignee information to the given panel.
	 */
	private void addAssignees( VibeFlowPanel assignedToPanel, TaskInfo taskInfo )
	{
		// Add the assigned people
		{
			List<AssignmentInfo> assignedPeople;
			
			assignedPeople = taskInfo.getAssignments();
			if ( assignedPeople != null )
			{
				for (AssignmentInfo assignmentInfo: assignedPeople)
				{
					addAssignedPerson( assignedToPanel, assignmentInfo );
				}
			}
		}
		
		// Add the assigned groups
		{
			List<AssignmentInfo> assignedGroups;
			
			assignedGroups = taskInfo.getAssignmentGroups();
			if ( assignedGroups != null )
			{
				for (AssignmentInfo assignmentInfo: assignedGroups)
				{
					addAssignedGroup( assignedToPanel, assignmentInfo );
				}
			}
		}
		
		// Add the assigned teams
		{
			List<AssignmentInfo> assignedTeams;
			
			assignedTeams = taskInfo.getAssignmentTeams();
			if ( assignedTeams != null )
			{
				for (AssignmentInfo assignmentInfo: assignedTeams)
				{
					addAssignedTeam( assignedToPanel, assignmentInfo );
				}
			}
		}
	}
	
	/**
	 * Add the given task to this widget
	 */
	private void addTask( final TaskInfo taskInfo )
	{
		TaskClickHandler clickHandler;
		int row;
		int col;
		GwtTeamingMessages messages;
		GwtTeamingTaskListingImageBundle images;

		// Have we reached the max number of tasks to display?
		if ( m_numTasksDisplayed >= m_maxTasksToDisplay )
		{
			// Yes, don't add any more.
			return;
		}

		images = GwtTeaming.getTaskListingImageBundle();
		messages = GwtTeaming.getMessages();

		// Add the tag as the first tag in the table.
		col = 0;
		row = 1;
		m_tasksTable.insertRow( row );
		
		m_tasksTable.getRowFormatter().setVerticalAlign( row, HasVerticalAlignment.ALIGN_TOP );
		
		// Add the task title in the first column.  Allow the user to click on the task title to view the task.
		{
			InlineLabel label;
			VibeFlowPanel titlePanel;
			String desc;
			
			m_cellFormatter.setColSpan( row, col, 1 );
			
			titlePanel = new VibeFlowPanel();
			titlePanel.addStyleName( "taskFolderWidgetTaskTitlePanel" + m_style );
			
			label = new InlineLabel( taskInfo.getTitle() );
			label.addStyleName( "taskFolderWidgetLinkToTask" + m_style );
			GwtClientHelper.setElementTextColor( label.getElement(), m_widgetStyles.getContentTextColor() );
			clickHandler = new TaskClickHandler( taskInfo );
			label.addClickHandler( clickHandler );
			
			titlePanel.add( label );
			
			// Does the task have a description?
			desc = taskInfo.getDesc();
			if ( GwtClientHelper.hasString( desc ) )
			{
				VibeFlowPanel descPanel;
				
				// Yes
				descPanel = new VibeFlowPanel();
				descPanel.addStyleName( "taskFolderWidgetTaskDescPanel" + m_style );
				descPanel.getElement().setInnerHTML( desc );
				
				titlePanel.add( descPanel );
			}
			
			m_tasksTable.setWidget( row, col, titlePanel );
			++col;
		}

		// Add the due date in the next column.
		{
			InlineLabel dueDateLabel;
			TaskEvent taskEvent;
			String dueDate;
			boolean hasDueDate;
			
			taskEvent = taskInfo.getEvent();
			
			dueDate = taskEvent.getLogicalEnd().getDateDisplay();
			hasDueDate = GwtClientHelper.hasString( dueDate );
			
			dueDateLabel = new InlineLabel();
			dueDateLabel.getElement().setInnerHTML( hasDueDate ? dueDate : GwtTeaming.getMessages().taskNoDueDate() );
			dueDateLabel.setWordWrap( false );
			
			if ( taskInfo.isTaskOverdue() )
			{
				dueDateLabel.addStyleName( "gwtTaskList_task-overdue-color" );
			}
			
			if ( taskEvent.getEndIsCalculated() && hasDueDate )
			{
				dueDateLabel.addStyleName( "gwtTaskList_calculatedDate" );
				dueDateLabel.setTitle( GwtTeaming.getMessages().taskAltDateCalculated() );
			}

			m_tasksTable.setWidget( row, col, dueDateLabel );
			++col;
		}
		
		// Add the priority to the next column.
		{
			String priority;
			String altText = null;
			ImageResource imgResource = null;
		
			priority = taskInfo.getPriority();
			
			if ( "p1".equalsIgnoreCase( priority ) )
			{
				imgResource = images.p1();
				altText = messages.taskPriority_p1();
			}
			else if ( "p2".equalsIgnoreCase( priority ) )
			{
				imgResource = images.p2();
				altText = messages.taskPriority_p2();
			}
			else if ( "p3".equalsIgnoreCase( priority ) )
			{
				imgResource = images.p3();
				altText = messages.taskPriority_p3();
			}
			else if ( "p4".equalsIgnoreCase( priority ) )
			{
				imgResource = images.p4();
				altText = messages.taskPriority_p4();
			}
			else if ( "p5".equalsIgnoreCase( priority ) )
			{
				imgResource = images.p5();
				altText = messages.taskPriority_p5();
			}
			
			if ( imgResource != null )
			{
				Image img;
				
				img = new Image( imgResource );
				
				if ( altText != null )
				{
					img.setAltText( altText );
					img.setTitle( altText );
				}
				
				m_tasksTable.setWidget( row, col, img );
			}

			++col;
		}
		
		// Add the status to the next column.
		{
			String status;
			String altText = null;
			ImageResource imgResource = null;
			
			status = taskInfo.getStatus();
			if ( "s1".equalsIgnoreCase( status ) )
			{
				imgResource = images.needsAction();
				altText = messages.taskStatus_needsAction();
			}
			else if ( "s2".equalsIgnoreCase( status ) )
			{
				imgResource = images.inProcess();
				altText = messages.taskStatus_inProcess();
			}
			else if ( "s3".equalsIgnoreCase( status ) )
			{
				imgResource = images.completed();
				altText = messages.taskStatus_completed();
			}
			else if ( "s4".equalsIgnoreCase( status ) )
			{
				imgResource = images.cancelled();
				altText = messages.taskStatus_cancelled();
			}

			if ( imgResource != null )
			{
				Image img;
				
				img = new Image( imgResource );
				
				if ( altText != null )
				{
					img.setAltText( altText );
					img.setTitle( altText );
				}
				
				m_tasksTable.setWidget( row, col, img );
			}

			++col;
		}
		
		// Add % done to the next column.
		{
			String completed;
			String altText = null;
			ImageResource imgResource = null;
			
			completed = taskInfo.getCompleted();

			if ( "c000".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c0();
				altText = messages.taskCompleted_c0();
			}
			else if ( "c010".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c10();
				altText = messages.taskCompleted_c10();
			}
			else if ( "c020".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c20();
				altText = messages.taskCompleted_c20();
			}
			else if ( "c030".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c30();
				altText = messages.taskCompleted_c30();
			}
			else if ( "c040".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c40();
				altText = messages.taskCompleted_c40();
			}
			else if ( "c050".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c50();
				altText = messages.taskCompleted_c50();
			}
			else if ( "c060".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c60();
				altText = messages.taskCompleted_c60();
			}
			else if ( "c070".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c70();
				altText = messages.taskCompleted_c70();
			}
			else if ( "c080".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c80();
				altText = messages.taskCompleted_c80();
			}
			else if ( "c090".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c90();
				altText = messages.taskCompleted_c90();
			}
			else if ( "c100".equalsIgnoreCase( completed ) )
			{
				imgResource = images.c100();
				altText = messages.taskCompleted_c100();
			}

			if ( imgResource != null )
			{
				Image img;
				
				img = new Image( imgResource );
				
				if ( altText != null )
				{
					img.setAltText( altText );
					img.setTitle( altText );
				}
				
				m_tasksTable.setWidget( row, col, img );
			}

			++col;
		}
		
		// Add assigned to to the next column.
		{
			VibeFlowPanel assignedToPanel;
			
			// Create a panel where all the assigned people/groups/teams will go.
			assignedToPanel = new VibeFlowPanel();
			assignedToPanel.addStyleName( "taskFolderWidgetAssignedToPanel" + m_style );

			// Add the people/groups/teams assigned to this task.
			addAssignees( assignedToPanel, taskInfo );
			
			m_tasksTable.setWidget( row, col, assignedToPanel );
			++col;
		}
		
		// Add the location to the next column
		{
			InlineLabel locationLabel;
			
			locationLabel = new InlineLabel( taskInfo.getLocation() );
			locationLabel.addStyleName( "taskFolderWidgetLinkToLocation" + m_style );
			GwtClientHelper.setElementTextColor( locationLabel.getElement(), m_widgetStyles.getContentTextColor() );
			locationLabel.addClickHandler( new ClickHandler()
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
							EntityId taskId;
							Long binderId;
							
							taskId = taskInfo.getTaskId();
							binderId = taskId.getBinderId();

							handleClickOnLocation( binderId );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			
			m_tasksTable.setWidget( row, col, locationLabel );
			++col;
		}

		// Add the necessary styles to the cells in the row.
		{
			int i;
			
			m_cellFormatter.addStyleName( row, 0, "oltBorderLeft" );
			m_cellFormatter.addStyleName( row, col-1, "oltBorderRight" );
			
			for (i = 0; i < col; ++i)
			{
				m_cellFormatter.addStyleName( row, i, "oltContentBorderBottom" );
				m_cellFormatter.addStyleName( row, i, "oltContentPadding" );
			}
		}
		
		++m_numTasksDisplayed;
	}
	
	/**
	 * Add the given tasks to this widget
	 */
	public void addTasksFromTaskListItems( List<TaskListItem> tasks )
	{
		if ( tasks == null || tasks.size() == 0 )
			return;
		
		// Do we have a panel to put the tasks in?
		if ( m_tasksTable != null )
		{
			int i;
			
			// Yes
			for (i = 0; i < tasks.size(); ++i)
			{
				TaskListItem task;
				List<TaskListItem> subTasks;

				// Add this task to our list.
				task = tasks.get( i );
				addTask( task.getTask() );
				
				// Does this task have any sub tasks?
				subTasks = task.getSubtasks();
				if ( subTasks != null && subTasks.size() > 0 )
				{
					// Yes, add the sub tasks
					addTasksFromTaskListItems( subTasks );
				}
			}
		}
	}
	
	/**
	 * Add the given tasks to this widget
	 */
	public void addTasksFromTaskInfos( List<TaskInfo> tasks )
	{
		if ( tasks == null || tasks.size() == 0 )
			return;
		
		// Do we have a panel to put the tasks in?
		if ( m_tasksTable != null )
		{
			int i;
			
			// Yes
			for (i = 0; i < tasks.size(); ++i)
			{
				TaskInfo task;

				// Add this task to our list.
				task = tasks.get( i );
				addTask( task );
			}
		}
	}
	
	/**
	 * 
	 */
	private void handleClickOnLocation( Long binderId )
	{
		if ( binderId != null )
		{
			GetBinderPermalinkCmd cmd;
			AsyncCallback<VibeRpcResponse> callback;
			final String binderIdS;
			
			binderIdS = String.valueOf( binderId );
			
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
						GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
						binderIdS );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess(  VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					StringRpcResponseData responseData;
					final String binderPermalink;
	
					responseData = (StringRpcResponseData) response.getResponseData();
					binderPermalink = responseData.getStringValue();
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							EventHelper.fireChangeContextEventAsync( binderIdS, binderPermalink, Instigator.GOTO_CONTENT_URL );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			
			// Issue an ajax request to get the permalink of the given binder.
			cmd = new GetBinderPermalinkCmd( binderIdS );
			GwtClientHelper.executeCommand( cmd, callback );
		}
	}
	
	/**
	 * 
	 */
	private VibeFlowPanel init( WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		m_style = landingPageStyle;
		m_widgetStyles = widgetStyles;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "taskFolderWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );
		
		// Create a panel for the tasks to live in.
		{
			VibeFlowPanel tasksPanel;
			
			tasksPanel = new VibeFlowPanel();
			tasksPanel.addStyleName( "taskFolderWidgetListOfTasksPanel" + m_style );
			mainPanel.add( tasksPanel );
			
			// Create a table that will hold the tasks
			{
				HTMLTable.RowFormatter rowFormatter;

				m_tasksTable = new FlexTable();
				m_tasksTable.setWidth( "100%" );
				m_tasksTable.setCellSpacing( 0 );
			
				// Add the column headers.
				{
					InlineLabel colLabel;
					
					colLabel = new InlineLabel( GwtTeaming.getMessages().title() );
					m_tasksTable.setWidget( 0, 0, colLabel );
					
					colLabel = new InlineLabel( GwtTeaming.getMessages().taskFolderWidget_dueDate() );
					m_tasksTable.setWidget( 0, 1, colLabel );
					
					colLabel = new InlineLabel( GwtTeaming.getMessages().taskFolderWidget_priority() );
					m_tasksTable.setWidget( 0, 2, colLabel );
					
					colLabel = new InlineLabel( GwtTeaming.getMessages().taskFolderWidget_status() );
					m_tasksTable.setWidget( 0, 3, colLabel );
					
					colLabel = new InlineLabel( GwtTeaming.getMessages().taskFolderWidget_percentDone() );
					m_tasksTable.setWidget( 0, 4, colLabel );
					
					colLabel = new InlineLabel( GwtTeaming.getMessages().taskFolderWidget_assignedTo() );
					m_tasksTable.setWidget( 0, 5, colLabel );
					
					colLabel = new InlineLabel( GwtTeaming.getMessages().taskColumn_location() );
					m_tasksTable.setWidget( 0, 6, colLabel );
					
					rowFormatter = m_tasksTable.getRowFormatter();
					rowFormatter.addStyleName( 0, "oltHeader" );

					m_cellFormatter = m_tasksTable.getFlexCellFormatter();
					
					// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
					// That is why we are calling DOM.setElementAttribute(...) instead.
					//~JW:  m_cellFormatter.setWidth( 0, 2, "*" );
					m_cellFormatter.getElement( 0, 6 ).setAttribute( "width", "*" );
					
					m_cellFormatter.addStyleName( 0, 0, "oltBorderLeft" );
					m_cellFormatter.addStyleName( 0, 0, "oltHeaderBorderTop" );
					m_cellFormatter.addStyleName( 0, 0, "oltHeaderBorderBottom" );
					m_cellFormatter.addStyleName( 0, 0, "oltHeaderPadding" );
					m_cellFormatter.addStyleName( 0, 1, "oltHeaderBorderTop" );
					m_cellFormatter.addStyleName( 0, 1, "oltHeaderBorderBottom" );
					m_cellFormatter.addStyleName( 0, 1, "oltHeaderPadding" );
					m_cellFormatter.addStyleName( 0, 2, "oltHeaderBorderTop" );
					m_cellFormatter.addStyleName( 0, 2, "oltHeaderBorderBottom" );
					m_cellFormatter.addStyleName( 0, 2, "oltHeaderPadding" );
					m_cellFormatter.addStyleName( 0, 3, "oltHeaderBorderTop" );
					m_cellFormatter.addStyleName( 0, 3, "oltHeaderBorderBottom" );
					m_cellFormatter.addStyleName( 0, 3, "oltHeaderPadding" );
					m_cellFormatter.addStyleName( 0, 4, "oltHeaderBorderTop" );
					m_cellFormatter.addStyleName( 0, 4, "oltHeaderBorderBottom" );
					m_cellFormatter.addStyleName( 0, 4, "oltHeaderPadding" );
					m_cellFormatter.addStyleName( 0, 5, "oltHeaderBorderTop" );
					m_cellFormatter.addStyleName( 0, 5, "oltHeaderBorderBottom" );
					m_cellFormatter.addStyleName( 0, 5, "oltHeaderPadding" );
					m_cellFormatter.addStyleName( 0, 6, "oltBorderRight" );
					m_cellFormatter.addStyleName( 0, 6, "oltHeaderBorderTop" );
					m_cellFormatter.addStyleName( 0, 6, "oltHeaderBorderBottom" );
					m_cellFormatter.addStyleName( 0, 6, "oltHeaderPadding" );
				}

				tasksPanel.add( m_tasksTable );
			}

		}
		
		return mainPanel;
	}
}
