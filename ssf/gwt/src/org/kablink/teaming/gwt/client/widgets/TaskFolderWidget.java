/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.lpe.TaskFolderProperties;
import org.kablink.teaming.gwt.client.rpc.shared.GetTaskListCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TaskListItemListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.TaskListItem;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;



/**
 * 
 * This class is used to display a task folder widget in a landing page.  We will display the first
 * n tasks found in the given folder.
 * @author jwootton
 *
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
	private VibeFlowPanel init( TaskFolderProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		VibeFlowPanel contentPanel;
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
		
		// Issue an rpc request to get information about the folder.
		m_properties.getDataFromServer( new GetterCallback<Boolean>()
		{
			/**
			 * 
			 */
			@Override
			public void returnValue( Boolean value )
			{
				Scheduler.ScheduledCommand cmd;

				// Did we successfully get the folder information?
				if ( value )
				{
					// Yes
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
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

		// Create a panel where all the content will live.
		{
			contentPanel = new VibeFlowPanel();
			mainPanel.add( contentPanel );
			
			// Set the width and height
			{
				Style style;
				int width;
				int height;
				Unit unit;
				
				style = contentPanel.getElement().getStyle();
				
				// Don't set the width if it is set to 100%.  This causes a scroll bar to appear
				width = m_properties.getWidth();
				unit = m_properties.getWidthUnits();
				if ( width != 100 || unit != Unit.PCT )
					style.setWidth( width, unit );
				
				// Don't set the height if it is set to 100%.  This causes a scroll bar to appear.
				height = m_properties.getHeight();
				unit = m_properties.getHeightUnits();
				if ( height != 100 || unit != Unit.PCT )
					style.setHeight( height, unit );
				
				style.setOverflow( m_properties.getOverflow() );
			}
		}
		
		// Are we supposed to show tasks from this folder?
		numTasks = m_properties.getNumTasksToBeShownValue();
		if ( numTasks > 0 )
		{
			GetTaskListCmd cmd;
			VibeFlowPanel tasksPanel;
			
			// Yes, create a panel for the tasks to live in.
			tasksPanel = new VibeFlowPanel();
			tasksPanel.addStyleName( "taskFolderWidgetListOfTasksPanel" + m_style );
			contentPanel.add( tasksPanel );
			
			// Create a tasks widget that will hold the tasks
			m_tasksWidget = new SimpleListOfTasksWidget( numTasks, widgetStyles, m_style );
			tasksPanel.add( m_tasksWidget );

			// Issue an rpc request to get the last n tasks from the folder.
			cmd = new GetTaskListCmd( m_properties.getZoneUUID(), m_properties.getFolderIdL(), "ALL", "PHYSICAL" );
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
