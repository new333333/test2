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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelReady;
import org.kablink.teaming.gwt.client.event.InvokeSimpleProfileEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetProjectInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.ProjectInfo;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;

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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * This class will display basic project information for the given
 * binder.
 * 
 * @author jwootton
 */
public class ProjectInfoWidget extends ToolPanelBase
{
	private VibeFlowPanel m_mainPanel;
	private FlexTable m_infoTable;

	/**
	 * 
	 */
	private ProjectInfoWidget( RequiresResize containerResizer, BinderInfo binderInfo, ToolPanelReady toolPanelReady )
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
	 * Build the ui of this widget with the given data.
	 */
	private void buildUI( ProjectInfo projectInfo )
	{
		// Add the status info
		m_infoTable.setText( 0, 1, projectInfo.getStatusStr() );
		
		// Add the manager info
		{
			ArrayList<PrincipalInfo> managers;
			
			// Get the list of managers assigned to this project.
			managers = projectInfo.getManagers();
			if ( managers != null )
			{
				VibeFlowPanel panel;
				
				panel = new VibeFlowPanel();
				panel.addStyleName( "projectInfoWidget_ManagersPanel" );
				
				for (PrincipalInfo manager : managers)
				{
					FlowPanel namePanel;
					final InlineLabel name;
					final PrincipalInfo nextManager;
					
					nextManager = manager;
					
					namePanel = new FlowPanel();
					name = new InlineLabel( nextManager.getTitle() );
					name.addStyleName( "projectInfoWidget_ManagerName" );
					name.addClickHandler( new ClickHandler()
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
									Long workspaceId;
									
									workspaceId = nextManager.getPresenceUserWSId();
									if ( workspaceId != null )
									{
										SimpleProfileParams params;
										
										// Invoke the Simple Profile dialog.
										params = new SimpleProfileParams( name.getElement(), String.valueOf( nextManager.getId() ), String.valueOf( workspaceId ), nextManager.getTitle() );
										GwtTeaming.fireEvent(new InvokeSimpleProfileEvent( params ));
									}
								}
							};
							Scheduler.get().scheduleDeferred( cmd );
						}
					} );
					namePanel.add( name );
					
					panel.add( namePanel );
				}// end for()
				
				m_infoTable.setWidget( 1, 1, panel );
			}
		}
		
		// Add the due date
		m_infoTable.setText( 2, 1, projectInfo.getDueDate() );
	}
	
	/**
	 * Build the ui by reading the project information for the given binder.
	 * 
	 * @param binderInfo
	 */
	private void buildUI()
	{
		// Read the project information from the server.
		getProjectInfo();
	}
	
	/**
	 * Loads the ProjectInfoWidget split point and returns an instance of it via the callback.
	 *
	 * @param binderInfo
	 * @param vClient
	 */
	public static void createAsync( final RequiresResize containerResizer, final BinderInfo binderInfo, final ToolPanelReady toolPanelReady, final ToolPanelClient client )
	{
		GWT.runAsync( ProjectInfoWidget.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ProjectInfoWidget() );
				client.onUnavailable();
			}

			@Override
			public void onSuccess()
			{
				ProjectInfoWidget piWidget;
				
				piWidget = new ProjectInfoWidget( containerResizer, binderInfo, toolPanelReady );
				client.onSuccess( piWidget );
			}
		} );
	}
	
	/**
	 * 
	 */
	private void init()
	{
		CaptionPanel panel;
		RowFormatter rowFormatter;

		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "projectInfoWidget_MainPanel" );
		
		// Create a CaptionPanel for the project information to go in.
		panel = new CaptionPanel( GwtTeaming.getMessages().projectInformation() );
		panel.addStyleName( "projectInfoWidget_CaptionPanel" );
		m_mainPanel.add( panel );
		
		// Create a table for the project information to go in.
		m_infoTable = new FlexTable();
		m_infoTable.addStyleName( "projectInfoWidget_InfoTable" );
		panel.add( m_infoTable );

		// Set the formatting on the table
		rowFormatter = m_infoTable.getRowFormatter();
		rowFormatter.setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );
		rowFormatter.setVerticalAlign( 1, HasVerticalAlignment.ALIGN_TOP );
		rowFormatter.setVerticalAlign( 2, HasVerticalAlignment.ALIGN_TOP );

		// Add the labels to the table
		m_infoTable.setText( 0, 0, GwtTeaming.getMessages().statusLabel() );
		m_infoTable.setText( 1, 0, GwtTeaming.getMessages().managerLabel() );
		m_infoTable.setText( 2, 0, GwtTeaming.getMessages().dueDateLabel() );

		initWidget( m_mainPanel );
	}

	/**
	 * Issue an ajax call to project information for the given binder.
	 */
	private void getProjectInfo()
	{
		GetProjectInfoCmd cmd;
		
		// Get a ProjectInfo object for the binder we are working with.
		cmd = new GetProjectInfoCmd( m_binderInfo.getBinderId() );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure(Throwable t)
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetProjectInfo(),
					m_binderInfo.getBinderId() );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				final ProjectInfo projectInfo;
				
				projectInfo = (ProjectInfo) response.getResponseData();
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					@Override
					public void execute()
					{
						// Build the ui based on the information we read.
						buildUI( projectInfo );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		});
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
