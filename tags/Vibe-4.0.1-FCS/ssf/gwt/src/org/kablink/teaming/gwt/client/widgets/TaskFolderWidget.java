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

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.lpe.TaskFolderProperties;
import org.kablink.teaming.gwt.client.rpc.shared.GetTaskListCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TaskListItemListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TaskListItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * This class is used to display a task folder widget in a landing page.  We will display the first
 * n tasks found in the given folder.
 * 
 * @author jwootton
 */
public class TaskFolderWidget extends VibeWidget
{
	private TaskFolderProperties m_properties;
	private String m_style;
	private Element m_folderTitleElement;
	private SimpleListOfTasksWidget m_tasksWidget;

	/**
	 * 
	 */
	public TaskFolderWidget( TaskFolderProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( properties, widgetStyles, landingPageStyle );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * Add the given tasks to this widget
	 */
	private void addTasks( List<TaskListItem> tasks )
	{
		if ( tasks == null || tasks.size() == 0 )
			return;
		
		// Do we have tasks widget to put the tasks in?
		if ( m_tasksWidget != null )
		{
			// Yes
			m_tasksWidget.addTasksFromTaskListItems( tasks );
		}
	}

	/**
	 * 
	 */
	private void getTasksFromFolder(
		String zoneId,
		Long folderId )
	{
		GetTaskListCmd cmd;

		// Issue an rpc request to get the last n tasks from the folder.
		cmd = new GetTaskListCmd( zoneId, folderId, "ALL", "PHYSICAL" );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
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
			@Override
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
							@Override
							public void execute()
							{
								// Add the tasks to this widget
								addTasks( tasks );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			}
		} );
	}
	
	/**
	 * When the user clicks on the folder's title, fire the ChangeContextEvent event
	 */
	private void handleClickOnFolderTitle()
	{
		EventHelper.fireChangeContextEventAsync( m_properties.getFolderId(), m_properties.getViewFolderUrl(), Instigator.GOTO_CONTENT_URL );
	}
	
	
	/**
	 * 
	 */
	private VibeFlowPanel init( TaskFolderProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		VibeFlowPanel contentPanel;
		final int numTasks;
		InlineLabel label;
		VibeFlowPanel titlePanel;
		int width;
		Unit widthUnits;
		int height;
		Unit heightUnits;
		ScheduledCommand cmd;
		
		m_properties = new TaskFolderProperties();
		m_properties.copy( properties );
		
		m_style = landingPageStyle;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "taskFolderWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );
		
		// Get the width and height
		width = m_properties.getWidth();
		widthUnits = m_properties.getWidthUnits();
		height = m_properties.getHeight();
		heightUnits = m_properties.getHeightUnits();
		
		// Set the width of the entire widget
		GwtClientHelper.setWidth( width, widthUnits, mainPanel );
		
		// If the height is a percentage, set the height of the entire widget.
		if ( heightUnits == Unit.PCT )
			GwtClientHelper.setHeight( height, heightUnits, mainPanel );
		
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
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
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
			
			// Set the title background color.
			GwtClientHelper.setElementBackgroundColor( titlePanel.getElement(), widgetStyles.getHeaderBgColor() );
			
			// Set the title text color.
			GwtClientHelper.setElementTextColor( m_folderTitleElement, widgetStyles.getHeaderTextColor() );

			mainPanel.add( titlePanel );
		}
		
		// Create a panel where all the content will live.
		{
			contentPanel = new VibeFlowPanel();
			contentPanel.addStyleName( "landingPageWidgetContentPanel" + m_style );
			mainPanel.add( contentPanel );
			
			// If the height is not a percentage, set the height of the contentPanel.
			if ( heightUnits != Unit.PCT )
				GwtClientHelper.setHeight( height, heightUnits, contentPanel );

			// Set the overflow value
			GwtClientHelper.setOverflow( m_properties.getOverflow(), contentPanel );
		}
		
		// Are we supposed to show tasks from this folder?
		numTasks = m_properties.getNumTasksToBeShownValue();
		if ( numTasks > 0 )
		{
			VibeFlowPanel tasksPanel;
			
			// Yes, create a panel for the tasks to live in.
			tasksPanel = new VibeFlowPanel();
			tasksPanel.addStyleName( "taskFolderWidgetListOfTasksPanel" + m_style );
			contentPanel.add( tasksPanel );
			
			// Create a tasks widget that will hold the tasks
			m_tasksWidget = new SimpleListOfTasksWidget( numTasks, widgetStyles, m_style );
			tasksPanel.add( m_tasksWidget );
		}

		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Issue an rpc request to get information about the folder.
				m_properties.getDataFromServer( new GetterCallback<Boolean>()
				{
					/**
					 * 
					 */
					@Override
					public void returnValue( Boolean value )
					{
						Scheduler.ScheduledCommand cmd2;

						// Did we successfully get the folder information?
						if ( value )
						{
							// Yes
							cmd2 = new Scheduler.ScheduledCommand()
							{
								@Override
								public void execute()
								{
									// Update this widget with the folder information
									updateWidget();

									if ( numTasks > 0 )
									{
										// Issue an rpc request to get the last n tasks from the folder.
										getTasksFromFolder( m_properties.getZoneUUID(), m_properties.getFolderIdL() );
									}
									
									getWidget().setVisible( true );
								}
							};
							Scheduler.get().scheduleDeferred( cmd2 );
						}
					}
				} );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );

		mainPanel.setVisible( false );
		
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
