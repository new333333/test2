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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelReady;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.binderviews.accessories.AccessoriesPanel;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.widgets.LandingPageWidget;
import org.kablink.teaming.gwt.client.widgets.ProjectInfoWidget;
import org.kablink.teaming.gwt.client.widgets.ProjectStatsWidget;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

/**
 * This widget is the Project Management Workspace.  It is used to
 * display a project management workspace.
 * 
 * @author jwootton
 */
public class ProjectManagementWSView extends WorkspaceViewBase implements ToolPanelReady
{
	private VibeFlowPanel m_mainPanel;
	private VibeFlowPanel m_lpPanel;
	private VibeFlowPanel m_projectInfoPanel;
	private VibeFlowPanel m_projectStatsPanel;
	private VibeFlowPanel m_htmlElementPanel;
	
	/**
	 * 
	 */
	private ProjectManagementWSView( BinderInfo binderInfo, ViewReady viewReady )
	{
		super( binderInfo, viewReady );
		
		// Build this view
		buildView();
	}
	
	/**
	 * Build this view.  This view will have the following components:
	 * 1. Title
	 * 2. Description
	 * 3. Accessories
	 * 4. Project information widget
	 * 5. Project statistics widget
	 * 6. Footer
	 */
	private void buildView()
	{
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "vibe-projectManagementWSView_MainPanel" );
		
		// Add a place for landing page elements.  The LandingPage widget will display the description
		// if there is one.
		{
			m_lpPanel = new VibeFlowPanel();
			m_lpPanel.addStyleName( "vibe-teamWSView_LPPanel" );
			m_mainPanel.add( m_lpPanel );

			LandingPageWidget.createAsync( getBinderInfo(), new LandingPageWidget.LandingPageWidgetClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}

				@Override
				public void onSuccess( LandingPageWidget landingPage )
				{
					m_lpPanel.add( landingPage );
				}
			} );
		}

		// Add a place for an HTML element.
		{
			m_htmlElementPanel = new VibeFlowPanel();
			m_htmlElementPanel.addStyleName( "vibe-projectManagementWSView_HtmlElementPanel" );
			m_mainPanel.add( m_htmlElementPanel );
			
			HtmlElementPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase htmlElement )
				{
					m_htmlElementPanel.add( htmlElement );
				}
			});
		}
		
		// Add a place for the project information
		{
			m_projectInfoPanel = new VibeFlowPanel();
			m_projectInfoPanel.addStyleName( "vibe-projectManagementWSView_ProjectInfoPanel" );
			m_mainPanel.add( m_projectInfoPanel );
			
			ProjectInfoWidget.createAsync( this, getBinderInfo(), this , new ToolPanelClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase projectInfoWidget )
				{
					m_projectInfoPanel.add( projectInfoWidget );
				}
			} );
		}
		
		// Add a place for the project statistics
		{
			m_projectStatsPanel = new VibeFlowPanel();
			m_projectStatsPanel.addStyleName( "vibe-projectManagementWSView_ProjectStatsPanel" );
			m_mainPanel.add( m_projectStatsPanel );
			
			ProjectStatsWidget.createAsync( this, getBinderInfo(), this , new ToolPanelClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase projectStatsWidget )
				{
					m_projectStatsPanel.add( projectStatsWidget );
				}
			} );
		}
		
		super.viewReady();
		
		initWidget( m_mainPanel );
	}
	
	
	/**
	 * Loads the ProjectManagementWSView split point and returns an instance of it via the callback.
	 *
	 * @param binderInfo
	 * @param vClient
	 */
	public static void createAsync( final BinderInfo binderInfo, final ViewReady viewReady, final ViewClient vClient )
	{
		GWT.runAsync( ProjectManagementWSView.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ProjectManagementWSView() );
				vClient.onUnavailable();
			}

			@Override
			public void onSuccess()
			{
				ProjectManagementWSView view;
				
				view = new ProjectManagementWSView( binderInfo, viewReady );
				vClient.onSuccess( view );
			}
		} );
	}
	

	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 */
	@Override
	public void toolPanelReady( ToolPanelBase toolPanel )
	{
		// Nothing to do.  We don't need to know when tool panels are ready.
	}
}
