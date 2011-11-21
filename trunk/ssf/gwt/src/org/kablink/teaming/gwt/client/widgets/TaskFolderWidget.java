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

import java.util.List;

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.lpe.TaskFolderProperties;
import org.kablink.teaming.gwt.client.rpc.shared.GetTaskListCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewFolderEntryUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TaskListItemListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;



/**
 * 
 * This class is used to display a task folder widget in a landing page.  We will display the first
 * n tasks found in the given folder.
 * @author jwootton
 *
 */
public class TaskFolderWidget extends VibeWidget
{
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
		public void onClick( ClickEvent event )
		{
			Scheduler.ScheduledCommand cmd;

			cmd = new Scheduler.ScheduledCommand()
			{
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
						cmd = new GetViewFolderEntryUrlCmd( m_taskInfo.getTaskId().getBinderId(), m_taskInfo.getTaskId().getEntryId() );
						GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
						{
							@Override
							public void onFailure(Throwable t)
							{
								GwtClientHelper.handleGwtRPCFailure(
									t,
									GwtTeaming.getMessages().rpcFailure_GetViewFolderEntryUrl(),
									String.valueOf( m_taskInfo.getTaskId().getEntryId() ) );
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
	
	private TaskFolderProperties m_properties;
	private String m_style;
	private int m_numTasksDisplayed;
	private Element m_folderTitleElement;
	private FlexTable m_tasksTable;
	private FlexTable.FlexCellFormatter m_cellFormatter;

	/**
	 * 
	 */
	public TaskFolderWidget( TaskFolderProperties properties, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		m_numTasksDisplayed = 0;
		mainPanel = init( properties, landingPageStyle );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * Add the given task to this widget
	 */
	private void addTask( TaskListItem task )
	{
		TaskInfo taskInfo;
		TaskClickHandler clickHandler;
		int row;
		int col;

		// Have we reached the max number of tasks to display?
		if ( m_numTasksDisplayed >= m_properties.getNumTasksToBeShownValue() )
		{
			// Yes, don't add any more.
		}
		
		taskInfo = task.getTask();

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
			String value;
			
			priority = taskInfo.getPriority();
			if ( "p1".equalsIgnoreCase( priority ) )
				value = GwtTeaming.getMessages().taskPriority_p1();
			else if ( "p2".equalsIgnoreCase( priority ) )
				value = GwtTeaming.getMessages().taskPriority_p2();
			else if ( "p3".equalsIgnoreCase( priority ) )
				value = GwtTeaming.getMessages().taskPriority_p3();
			else if ( "p4".equalsIgnoreCase( priority ) )
				value = GwtTeaming.getMessages().taskPriority_p4();
			else if ( "p5".equalsIgnoreCase( priority ) )
				value = GwtTeaming.getMessages().taskPriority_p5();
			else
				value = priority;
			m_tasksTable.setText( row, col, value );
			++col;
		}
		
		// Add the status to the next column.
		{
			String status;
			String statusValue;
			
			status = taskInfo.getStatus();
			if ( "s1".equalsIgnoreCase( status ) )
				statusValue = GwtTeaming.getMessages().taskStatus_needsAction();
			else if ( "s2".equalsIgnoreCase( status ) )
				statusValue = GwtTeaming.getMessages().taskStatus_inProcess();
			else if ( "s3".equalsIgnoreCase( status ) )
				statusValue = GwtTeaming.getMessages().taskStatus_completed();
			else if ( "s4".equalsIgnoreCase( status ) )
				statusValue = GwtTeaming.getMessages().taskStatus_cancelled();
			else
				statusValue = status;
			m_tasksTable.setText( row, col, statusValue );
			++col;
		}
		
		// Add % done to the next column.
		{
			String completed;
			String completedValue;
			
			completed = taskInfo.getCompleted();
			if ( "c000".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c0();
			else if ( "c010".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c10();
			else if ( "c020".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c20();
			else if ( "c030".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c30();
			else if ( "c040".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c40();
			else if ( "c050".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c50();
			else if ( "c060".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c60();
			else if ( "c070".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c70();
			else if ( "c080".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c80();
			else if ( "c090".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c90();
			else if ( "c100".equalsIgnoreCase( completed ) )
				completedValue = GwtTeaming.getMessages().taskCompleted_c100();
			else
				completedValue = completed;
			m_tasksTable.setText( row, col, completedValue );
			++col;
		}
		
		// Add assigned to to the next column.
		{
			VibeFlowPanel assignedToPanel;
			
			// Create a panel where all the assigned people/groups/teams will go.
			assignedToPanel = new VibeFlowPanel();
			assignedToPanel.addStyleName( "taskFolderWidgetAssignedToPanel" + m_style );
			
			// Add the assigned people
			{
				List<AssignmentInfo> assignedPeople;
				
				assignedPeople = taskInfo.getAssignments();
				if ( assignedPeople != null )
				{
					for (AssignmentInfo assignmentInfo: assignedPeople)
					{
						VibeFlowPanel tmpPanel;
						Label userName;
						
						// Create a panel for the person's info to live in.
						tmpPanel = new VibeFlowPanel();
						tmpPanel.addStyleName( "taskFolderWidgetAssignedPersonPanel" + m_style );
						
						userName = new Label( assignmentInfo.getTitle() );
						userName.addStyleName( "taskFolderWidgetAssignedPerson" + m_style );
						tmpPanel.add( userName );
						
						assignedToPanel.add( tmpPanel );
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
						VibeFlowPanel tmpPanel;
						Label groupName;
						
						// Create a panel for the groups's info to live in.
						tmpPanel = new VibeFlowPanel();
						tmpPanel.addStyleName( "taskFolderWidgetAssignedGroupPanel" + m_style );
						
						groupName = new Label( assignmentInfo.getTitle() );
						groupName.addStyleName( "taskFolderWidgetAssignedGroup" + m_style );
						tmpPanel.add( groupName );
						
						assignedToPanel.add( tmpPanel );
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
						VibeFlowPanel tmpPanel;
						Label teamName;
						
						// Create a panel for the team's info to live in.
						tmpPanel = new VibeFlowPanel();
						tmpPanel.addStyleName( "taskFolderWidgetAssignedTeamPanel" + m_style );
						
						teamName = new Label( assignmentInfo.getTitle() );
						teamName.addStyleName( "taskFolderWidgetAssignedTeam" + m_style );
						tmpPanel.add( teamName );
						
						assignedToPanel.add( tmpPanel );
					}
				}
			}
			
			m_tasksTable.setWidget( row, col, assignedToPanel );
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
	private void addTasks( List<TaskListItem> tasks )
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
				addTask( task );
				
				// Does this task have any sub tasks?
				subTasks = task.getSubtasks();
				if ( subTasks != null && subTasks.size() > 0 )
				{
					// Yes, add the sub tasks
					addTasks( subTasks );
				}
			}
		}
	}
	
	/**
	 * When the user clicks on the folder's title, fire the ChangeContextEvent event
	 */
	private void handleClickOnFolderTitle()
	{
		OnSelectBinderInfo binderInfo;
		
		binderInfo = new OnSelectBinderInfo( m_properties.getFolderId(), m_properties.getViewFolderUrl(), false, Instigator.UNKNOWN );
		GwtTeaming.fireEvent( new ChangeContextEvent( binderInfo ) );
	}
	
	
	/**
	 * 
	 */
	private VibeFlowPanel init( TaskFolderProperties properties, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		int numTasks;
		InlineLabel label;
		VibeFlowPanel titlePanel;
		
		m_properties = new TaskFolderProperties();
		m_properties.copy( properties );
		
		m_style = landingPageStyle;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "taskFolderWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );
		
		// Add a place for the folder's title
		{
			// Create a place for the folder title to live.
			titlePanel = new VibeFlowPanel();
			titlePanel.addStyleName( "landingPageWidgetTitlePanel" + m_style );
			titlePanel.addStyleName( "taskFolderWidgetTitlePanel" + m_style );
			
			label = new InlineLabel( " " );
			label.addStyleName( "taskFolderWidgetTitleLabel" + m_style );
			label.addClickHandler( new ClickHandler()
			{
				/**
				 * 
				 */
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						public void execute()
						{
							handleClickOnFolderTitle();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			titlePanel.add( label );
			m_folderTitleElement = label.getElement();
			
			mainPanel.add( titlePanel );
		}
		
		// Issue an rpc request to get information about the folder.
		m_properties.getDataFromServer( new GetterCallback<Boolean>()
		{
			/**
			 * 
			 */
			public void returnValue( Boolean value )
			{
				Scheduler.ScheduledCommand cmd;

				// Did we successfully get the folder information?
				if ( value )
				{
					// Yes
					cmd = new Scheduler.ScheduledCommand()
					{
						public void execute()
						{
							// Update this widget with the folder information
							updateWidget();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		} );
		
		// Are we supposed to show tasks from this folder?
		numTasks = m_properties.getNumTasksToBeShownValue();
		if ( numTasks > 0 )
		{
			GetTaskListCmd cmd;
			VibeFlowPanel tasksPanel;
			
			// Yes, create a panel for the tasks to live in.
			tasksPanel = new VibeFlowPanel();
			tasksPanel.addStyleName( "taskFolderWidgetListOfTasksPanel" + m_style );
			mainPanel.add( tasksPanel );
			
			// Create a table that will hold the tasks
			{
				HTMLTable.RowFormatter rowFormatter;

				m_tasksTable = new FlexTable();
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
					
					rowFormatter = m_tasksTable.getRowFormatter();
					rowFormatter.addStyleName( 0, "oltHeader" );

					m_cellFormatter = m_tasksTable.getFlexCellFormatter();
					
					// On IE calling m_cellFormatter.setWidth( 0, 2, "*" ); throws an exception.
					// That is why we are calling DOM.setElementAttribute(...) instead.
					//!!!m_cellFormatter.setWidth( 0, 2, "*" );
					DOM.setElementAttribute( m_cellFormatter.getElement( 0, 5 ), "width", "*" );
					
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
					m_cellFormatter.addStyleName( 0, 5, "oltBorderRight" );
					m_cellFormatter.addStyleName( 0, 5, "oltHeaderBorderTop" );
					m_cellFormatter.addStyleName( 0, 5, "oltHeaderBorderBottom" );
					m_cellFormatter.addStyleName( 0, 5, "oltHeaderPadding" );
				}

				tasksPanel.add( m_tasksTable );
			}

			// Issue an rpc request to get the last n tasks from the folder.
			cmd = new GetTaskListCmd( m_properties.getFolderIdL(), "ALL", "PHYSICAL" );
			GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetTaskList() );
				}
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( VibeRpcResponse response )
				{
					TaskListItemListRpcResponseData tlilResponse;
					
					tlilResponse = (TaskListItemListRpcResponseData) response.getResponseData();
					
					if ( tlilResponse != null )
					{
						final List<TaskListItem> tasks;
						
						tasks = tlilResponse.getTaskList();
						if ( tasks != null )
						{
							Scheduler.ScheduledCommand cmd;
	
							cmd = new Scheduler.ScheduledCommand()
							{
								public void execute()
								{
									// Add the tasks to this widget
									m_numTasksDisplayed = 0;
									addTasks( tasks );
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					}
				}
			} );
		}
		
		return mainPanel;
	}
	
	/**
	 * Update the folder's title 
	 */
	private void updateWidget()
	{
		// Update the title if we are showing it.
		if ( m_folderTitleElement != null )
		{
			String title;
			
			title = m_properties.getFolderTitle();
			if ( title == null || title.length() == 0 )
				title = GwtTeaming.getMessages().noTitle();

			m_folderTitleElement.setInnerHTML( title );
		}
	}
}
