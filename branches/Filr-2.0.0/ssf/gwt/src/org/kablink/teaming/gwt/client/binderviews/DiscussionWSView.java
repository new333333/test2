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
import org.kablink.teaming.gwt.client.widgets.ChildBindersWidget;
import org.kablink.teaming.gwt.client.widgets.LandingPageWidget;
import org.kablink.teaming.gwt.client.widgets.LandingPageWidget.LandingPageWidgetClient;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;

/**
 * This widget is the Discussion Workspace.  It is used to display a
 * discussion workspace.
 * 
 * @author jwootton
 */
public class DiscussionWSView extends WorkspaceViewBase implements ToolPanelReady
{
	private VibeFlowPanel m_mainPanel;
	private VibeFlowPanel m_breadCrumbPanel;
	private VibeFlowPanel m_titlePanel;
	private VibeFlowPanel m_descPanel;
	private VibeFlowPanel m_lpPanel;
	private VibeFlowPanel m_accessoriesPanel;
	private VibeFlowPanel m_listOfChildrenPanel;
	private VibeFlowPanel m_footerPanel;
	private VibeFlowPanel m_htmlElementPanel;
	
	/**
	 * 
	 */
	private DiscussionWSView( BinderInfo binderInfo, ViewReady viewReady )
	{
		super( binderInfo, viewReady );
		
		// Build this view
		buildView();
	}
	
	/**
	 * Build this view.  This view will have the following components:
	 * 1. Title
	 * 2. Description
	 * 3. Landing page elements
	 * 4. Accessories
	 * 5. List of folders/workspaces
	 * 6. Footer
	 */
	private void buildView()
	{
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "vibe-discussionWSView_MainPanel" );
		
		// Add a place for the bread crumb control to live.
		{
			m_breadCrumbPanel = new VibeFlowPanel();
			m_breadCrumbPanel.addStyleName( "vibe-discussionWSView_BreadCrumbPanel" );
			m_mainPanel.add( m_breadCrumbPanel );

			BreadCrumbPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase breadCrumb )
				{
					m_breadCrumbPanel.add( breadCrumb );
				}
			});
		}

		m_titlePanel = new VibeFlowPanel();
		m_titlePanel.addStyleName( "vibe-discussionWSView_TitlePanel" );
		m_mainPanel.add( m_titlePanel );
		
		// Add a place for the description to live.
		{
			m_descPanel = new VibeFlowPanel();
			m_descPanel.addStyleName( "vibe-discussionWSView_DescPanel" );
			m_mainPanel.add( m_descPanel );
			
			DescriptionPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase tpb )
				{
					m_descPanel.add( tpb );
				}
			} );
		}

		// Add a place for landing page elements.  The LandingPage widget will display the description
		// if there is one.
		{
			m_lpPanel = new VibeFlowPanel();
			m_lpPanel.addStyleName( "vibe-discussionWSView_LPPanel" );
			m_mainPanel.add( m_lpPanel );

			LandingPageWidget.createAsync( getBinderInfo(), new LandingPageWidgetClient()
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
		
		// Add a place for the accessories.
		{
			m_accessoriesPanel = new VibeFlowPanel();
			m_accessoriesPanel.addStyleName( "vibe-discussionWSView_AccessoriesPanel" );
			m_mainPanel.add( m_accessoriesPanel );
			
			AccessoriesPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase accessories )
				{
					m_accessoriesPanel.add( accessories );
				}
			});
		}
		
		// Add a place for an HTML element.
		{
			m_htmlElementPanel = new VibeFlowPanel();
			m_htmlElementPanel.addStyleName( "vibe-discussionWSView_HtmlElementPanel" );
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
		
		// Add a place for the ChildBindersWidget
		{
			m_listOfChildrenPanel = new VibeFlowPanel();
			m_listOfChildrenPanel.addStyleName( "vibe-discussionWSView_ListOfChildrenPanel" );
			m_mainPanel.add( m_listOfChildrenPanel );

			ChildBindersWidget.createAsync( this, getBinderInfo(), this, new ToolPanelClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase cbWidget )
				{
					m_listOfChildrenPanel.add( cbWidget );
				}
			} );
		}

		// Add a place for the footer
		{
			m_footerPanel = new VibeFlowPanel();
			m_footerPanel.addStyleName( "vibe-discussionWSView_FooterPanel" );
			m_mainPanel.add( m_footerPanel );

			FooterPanel.createAsync( this, getBinderInfo(), this, new ToolPanelClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase tpb )
				{
					m_footerPanel.add( tpb );
				}
			} );
		}

		super.viewReady();
		
		initWidget( m_mainPanel );
	}
	
	
	/**
	 * Loads the DiscussionWSView split point and returns an instance of it
	 * via the callback.
	 *
	 * @param binderInfo
	 * @param vClient
	 */
	public static void createAsync( final BinderInfo binderInfo, final ViewReady viewReady, final ViewClient vClient )
	{
		GWT.runAsync( DiscussionWSView.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_DiscussionWSView() );
				vClient.onUnavailable();
			}

			@Override
			public void onSuccess()
			{
				DiscussionWSView view;
				
				view = new DiscussionWSView( binderInfo, viewReady );
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
