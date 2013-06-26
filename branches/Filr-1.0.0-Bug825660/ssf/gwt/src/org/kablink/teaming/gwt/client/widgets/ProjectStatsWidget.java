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

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelReady;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderStatsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetListOfChildBindersCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetListOfChildBindersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderStats;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.MilestoneStats;
import org.kablink.teaming.gwt.client.util.TaskStats;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * This class will display project statistics for the given binder.
 * 
 * @author jwootton
 */
public class ProjectStatsWidget extends ToolPanelBase
{
	private VibeFlowPanel m_mainPanel;
	private VibeFlowPanel m_contentPanel;


	/**
	 * 
	 */
	private ProjectStatsWidget( RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady )
	{
		super( containerResizer, binderInfo, toolPanelReady );
		
		Scheduler.ScheduledCommand cmd;

		init();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			/**
			 * 
			 */
			@Override
			public void execute()
			{
				// Initialize this widget for the given binder.
				buildUI();
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	

	/**
	 * Add a tasks statistics graph and a milestone statistics graph to the given panel.
	 */
	private void addStatsGraphs( TaskStats taskStats, MilestoneStats milestoneStats, VibeFlowPanel panel )
	{
		FlexTable table = null;
		int col = 0;
		
		if ( taskStats != null || milestoneStats != null )
		{
			table = new FlexTable();
			panel.add( table );
		}
		
		if ( taskStats != null )
		{
			VibeFlowPanel graphPanel;
			TaskStatusGraph taskGraph;
			
			graphPanel = new VibeFlowPanel();
			graphPanel.addStyleName( "projectStatsWidget_GraphPanel" );
			
			// Create a status graph for the tasks.
			taskGraph = new TaskStatusGraph( taskStats, "projectStatsWidget_TasksStatsPanel", true );
			graphPanel.add( taskGraph );
			
			table.setWidget( 0, col, graphPanel );
			++col;
		}

		if ( milestoneStats != null )
		{
			VibeFlowPanel graphPanel;
			MilestoneStatusGraph milestoneGraph;
			
			graphPanel = new VibeFlowPanel();
			graphPanel.addStyleName( "projectStatsWidget_GraphPanel" );
			
			// Create a status graph for the milestones.
			milestoneGraph = new MilestoneStatusGraph( milestoneStats, "projectStatsWidget_MilestoneStatsPanel", true );
			graphPanel.add( milestoneGraph );
			
			table.setWidget( 0, col, graphPanel );
		}
	}
	
	/**
	 * Add a milestones statistics graph to the given panel.
	 */
	@SuppressWarnings("unused")
	private void addMilestoneStatsGraph( MilestoneStats milestoneStats, VibeFlowPanel panel )
	{
		if ( milestoneStats != null )
		{
			VibeFlowPanel graphPanel;
			MilestoneStatusGraph milestoneGraph;
			
			graphPanel = new VibeFlowPanel();
			graphPanel.addStyleName( "projectStatsWidget_GraphPanel" );
			
			// Create a status graph for the milestones.
			milestoneGraph = new MilestoneStatusGraph( milestoneStats, "projectStatsWidget_MilestoneStatsPanel", true );
			graphPanel.add( milestoneGraph );
			
			panel.add( graphPanel );
		}
	}

	/**
	 * Add a tasks statistics graph to the given panel.
	 */
	@SuppressWarnings("unused")
	private void addTaskStatsGraph( TaskStats taskStats, VibeFlowPanel panel )
	{
		if ( taskStats != null )
		{
			VibeFlowPanel graphPanel;
			TaskStatusGraph taskGraph;
			
			graphPanel = new VibeFlowPanel();
			graphPanel.addStyleName( "projectStatsWidget_GraphPanel" );
			
			// Create a status graph for the tasks.
			taskGraph = new TaskStatusGraph( taskStats, "projectStatsWidget_TasksStatsPanel", true );
			graphPanel.add( taskGraph );
			
			panel.add( graphPanel );
		}
	}

	/**
	 * Build the ui of this widget with the given data.
	 */
	private void buildUI( ArrayList<TreeInfo> listOfChildBinders )
	{
		// Add the name of each binder to this widget and get the project statistics
		// for each binder.
		for (TreeInfo childBinder : listOfChildBinders)
		{
			BinderInfo childBinderInfo;
			VibeFlowPanel binderPanel;
			InlineLabel binderLabel;
			final String childBinderId;
			final String childBinderUrl;
			
			childBinderInfo = childBinder.getBinderInfo();
			childBinderId = childBinderInfo.getBinderId();
			childBinderUrl = childBinder.getBinderPermalink();
			
			binderPanel = new VibeFlowPanel();
			binderPanel.addStyleName( "projectStatsWidget_BinderPanel" );
			
			// Give the panel that holds the binder an id equal to the binder id.
			// That gives us a way to find the panel again later.
			binderPanel.getElement().setId( childBinderInfo.getBinderId() );

			binderLabel = new InlineLabel( childBinderInfo.getBinderTitle() );
			binderLabel.addStyleName( "projectStatsWidget_BinderLabel" );
			binderLabel.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					ScheduledCommand cmd;
					
					cmd = new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							handleClickOnBinder( childBinderId, childBinderUrl );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			
			// Get the statistics for this binder.  If the binder has any statistics
			// a graph will be added to the binderPanel.
			getBinderStatistics( childBinderId, binderPanel );

			binderPanel.add( binderLabel );
			
			m_contentPanel.add( binderPanel );
		}
	}
	
	/**
	 * Display a list of binders that are children of the given binder.  For each child
	 * binder, display statistics for the binder if statistics exist.
	 * 
	 */
	private void buildUI()
	{
		// Get a list of binders that are children of the given binder.
		getListOfChildBinders();
	}
	
	/**
	 * Loads the ProjectStatsWidget split point and returns an instance of it via the callback.
	 *
	 * @param binderInfo
	 * @param vClient
	 */
	public static void createAsync( final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient client )
	{
		GWT.runAsync( ProjectStatsWidget.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ProjectStatsWidget() );
				client.onUnavailable();
			}

			@Override
			public void onSuccess()
			{
				ProjectStatsWidget psWidget;
				
				psWidget = new ProjectStatsWidget( containerResizer, binderInfo, toolPanelReady );
				client.onSuccess( psWidget );
			}
		} );
	}
	
	/**
	 * Issue an ajax call to get the statistics for the given binder.  If the binder
	 * has statisics we will add a graph to the given binder.
	 * we are working with.
	 */
	private void getBinderStatistics( final String binderId, final VibeFlowPanel panel )
	{
		GetBinderStatsCmd cmd;
		
		// Get a BinderStats object for the given binder.
		cmd = new GetBinderStatsCmd( binderId );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBinderStats(),
					binderId );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				BinderStats binderStats;
				final TaskStats taskStats;
				final MilestoneStats milestoneStats;
				
				binderStats = (BinderStats) response.getResponseData();
				
				// Do we have any task or milestone statistics?
				taskStats = binderStats.getTaskStats();
				milestoneStats = binderStats.getMilestoneStats();
				if ( taskStats != null || milestoneStats != null )
				{
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							// Add a task statistics graph and a milestone statistics
							// graph to the given panel.
							addStatsGraphs( taskStats, milestoneStats, panel );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		});
	}
	
	/**
	 * Issue an ajax call to get a list of binders that are children of the binder
	 * we are working with.
	 */
	private void getListOfChildBinders()
	{
		GetListOfChildBindersCmd cmd;
		
		// Get a TreeInfo object for each of the child binders.
		cmd = new GetListOfChildBindersCmd( m_binderInfo.getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetListOfChildBinders(),
					m_binderInfo.getBinderId() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				GetListOfChildBindersRpcResponseData responseData;
				final ArrayList<TreeInfo> listOfChildBinders;
				
				responseData = (GetListOfChildBindersRpcResponseData) response.getResponseData();
				listOfChildBinders = responseData.getListOfChildBinders();
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					@Override
					public void execute()
					{
						// Build the ui based on the information we read.
						buildUI( listOfChildBinders );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		});
	}
	
	/**
	 * 
	 */
	private void handleClickOnBinder( String binderId, String binderUrl )
	{
		if ( GwtClientHelper.hasString( binderUrl ) )
		{
			EventHelper.fireChangeContextEventAsync(
				binderId,
				binderUrl,
				Instigator.GOTO_CONTENT_URL );
		}
	}
	
	/**
	 * 
	 */
	private void init()
	{
		CaptionPanel captionPanel;

		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "projectStatsWidget_MainPanel" );
		
		// Create a CaptionPanel for the project statistics to go in.
		captionPanel = new CaptionPanel( GwtTeaming.getMessages().projectStatistics() );
		captionPanel.addStyleName( "projectStatsWidget_CaptionPanel" );
		m_mainPanel.add( captionPanel );
		
		// Create a panel to hold all the content.
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName( "projectStatsWidget_ContentPanel" );
		captionPanel.add( m_contentPanel );
		
		initWidget( m_mainPanel );
	}

	/**
	 * Called from the binder view to allow the panel to do any work required to reset itself.
	 * 
	 * Implements ToolPanelBase.resetPanel()
	 */
	@Override
	public void resetPanel()
	{
		// Reset the widgets and reload the description.
		m_mainPanel.clear();
		buildUI();
	}
}
