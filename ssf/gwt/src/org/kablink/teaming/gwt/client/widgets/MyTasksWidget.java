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
import org.kablink.teaming.gwt.client.lpe.MyTasksProperties;
import org.kablink.teaming.gwt.client.rpc.shared.GetMyTasksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.TaskInfoListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * This class is used to display a widget that displays all the tasks assigned to the
 * logged-in user.
 * 
 * @author jwootton
 */
public class MyTasksWidget extends VibeWidget
{
	private String m_style;
	private SimpleListOfTasksWidget m_tasksWidget;

	/**
	 * 
	 */
	public MyTasksWidget( MyTasksProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( properties, widgetStyles, landingPageStyle );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * Add the given tasks to this widget
	 */
	private void addTasks( List<TaskInfo> tasks )
	{
		if ( tasks == null || tasks.size() == 0 )
			return;
		
		// Do we have tasks widget to put the tasks in?
		if ( m_tasksWidget != null )
		{
			// Yes
			m_tasksWidget.addTasksFromTaskInfos( tasks );
		}
	}
	
	/**
	 * 
	 */
	private VibeFlowPanel init( MyTasksProperties properties, WidgetStyles widgetStyles, String landingPageStyle )
	{
		VibeFlowPanel mainPanel;
		VibeFlowPanel contentPanel;
		InlineLabel label;
		VibeFlowPanel titlePanel;
		int width;
		Unit widthUnits;
		int height;
		Unit heightUnits;
		
		m_style = landingPageStyle;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "taskFolderWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "landingPageWidgetShowBorder" );
		
		// Get the width and height
		width = properties.getWidth();
		widthUnits = properties.getWidthUnits();
		height = properties.getHeight();
		heightUnits = properties.getHeightUnits();
		
		// Set the width of the entire widget
		GwtClientHelper.setWidth( width, widthUnits, mainPanel );
		
		// If the height is a percentage, set the height of the entire widget.
		if ( heightUnits == Unit.PCT )
			GwtClientHelper.setHeight( height, heightUnits, mainPanel );
		
		// Add a place for a header
		{
			Element headerElement;
			
			// Create a place for the folder title to live.
			titlePanel = new VibeFlowPanel();
			titlePanel.addStyleName( "landingPageWidgetTitlePanel" + m_style );
			titlePanel.addStyleName( "taskFolderWidgetTitlePanel" + m_style );
			
			label = new InlineLabel( GwtTeaming.getMessages().myTasksHeader() );
			label.addStyleName( "myTasksWidgetTitleLabel" + m_style );
			titlePanel.add( label );
			headerElement = label.getElement();
			
			// Set the title background color.
			GwtClientHelper.setElementBackgroundColor( titlePanel.getElement(), widgetStyles.getHeaderBgColor() );
			
			// Set the title text color.
			GwtClientHelper.setElementTextColor( headerElement, widgetStyles.getHeaderTextColor() );

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
			GwtClientHelper.setOverflow( properties.getOverflow(), contentPanel );
		}
		
		// Create widget to hold the list of tasks assigned to the logged-in user.
		{
			GetMyTasksCmd cmd;
			VibeFlowPanel tasksPanel;
			
			// Create a panel for the tasks to live in.
			tasksPanel = new VibeFlowPanel();
			tasksPanel.addStyleName( "taskFolderWidgetListOfTasksPanel" + m_style );
			contentPanel.add( tasksPanel );
			
			// Create a tasks widget that will hold the tasks
			m_tasksWidget = new SimpleListOfTasksWidget( 2000, widgetStyles, m_style );
			tasksPanel.add( m_tasksWidget );

			// Issue an rpc request to get a list of the tasks assigned to the logged-in user.
			cmd = new GetMyTasksCmd();
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
					TaskInfoListRpcResponseData tilResponse;
					
					tilResponse = (TaskInfoListRpcResponseData) response.getResponseData();
					
					if ( tilResponse != null )
					{
						final List<TaskInfo> tasks;
						
						tasks = tilResponse.getTaskList();
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
}
