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
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.rpc.shared.GetLandingPageDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.LandingPageWidget;
import org.kablink.teaming.gwt.client.widgets.LandingPageWidget.LandingPageWidgetClient;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This widget is the Landing Page.  It is used to render a landing
 * page configuration.
 * 
 * @author jwootton@novell.com
 */
public class LandingPageView extends WorkspaceViewBase implements ToolPanelReady
{
	private VibeFlowPanel m_mainPanel;
	
	/**
	 * 
	 */
	private LandingPageView( final BinderInfo binderInfo, final ViewReady viewReady )
	{
		super( binderInfo, viewReady );
		
		init();
		
		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			/**
			 * 
			 */
			@Override
			public void execute()
			{
				// Initialize this landing page for the given binder.
				buildLandingPage( binderInfo );
			}
		});
	}
	
	/**
	 * 
	 */
	private LandingPageView( final ConfigData configData )
	{
		super( null, null );
		
		init();

		GwtClientHelper.deferCommand(new ScheduledCommand()
		{
			/**
			 * 
			 */
			@Override
			public void execute()
			{
				// Initialize this landing page for the given binder.
				buildLandingPage( configData );
			}
		});
	}
	
	/**
	 * Add a footer to this page.
	 */
	private void addFooter()
	{
		BinderInfo binderInfo;
		
		binderInfo = getBinderInfo();
		if ( binderInfo != null )
		{
			FooterPanel.createAsync( this, binderInfo, this, new ToolPanelClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase tpb )
				{
					m_mainPanel.add( tpb );
				}
			} );
		}
	}
	
	/**
	 * Build this landing page for the given ConfigData.
	 */
	private void buildLandingPage( final ConfigData configData )
	{
		String bgColor;
		String bgImgUrl;
		BinderInfo binderInfo;
		
		m_mainPanel.clear();
		
		if ( configData == null ) {
			doViewReady();
			return;
		}
		
		if ( configData.isPreviewMode() )
			m_mainPanel.addStyleName( "landingPageViewPreviewHeight" );

		configData.parse();
		
		// Tell the base class that we're done constructing the landing
		// page view.
		doViewReady();

		binderInfo = getBinderInfo();
		String binderId = ((null == binderInfo) ? null : binderInfo.getBinderId());
		if ( configData.isPreviewMode() == false && binderId != null )
		{
			// Add the description to the page.
			DescriptionPanel.createAsync( this, binderInfo, this, new ToolPanelClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ToolPanelBase tpb )
				{
					// Insert the description as the first element on the landing page.
					m_mainPanel.insert( tpb, 0 );
				}
			} );
		}
		
		// Is a background color specified?
		bgColor = configData.getBackgroundColor();
		if ( bgColor != null && bgColor.length() > 0 )
		{
			// Yes
			m_mainPanel.getElement().getStyle().setBackgroundColor( bgColor );
		}
		
		// Is a background image specified?
		bgImgUrl = configData.getBackgroundImgUrl();
		if ( bgImgUrl != null && bgImgUrl.length() > 0 )
		{
			String bgRepeat;
			
			// Yes
			m_mainPanel.getElement().getStyle().setBackgroundImage( "url( " + bgImgUrl + " )" );
			
			// Is a background repeat value specified?
			bgRepeat = configData.getBackgroundImgRepeat();
			if ( bgRepeat != null && bgRepeat.length() > 0 )
			{
				// Yes
				m_mainPanel.getElement().getStyle().setProperty( "backgroundRepeat", bgRepeat );
			}
		}

		// Add a landing page widget to the page.
		LandingPageWidget.createAsync( configData, new LandingPageWidgetClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( LandingPageWidget lpWidget )
			{
				// Add the landing page widget to the page.
				m_mainPanel.add( lpWidget );

			}
		} );

		// Tell the base class that we're done constructing the landing
		// page view.
		doViewReady();
	}
	
	/**
	 * Build the landing page with data from the given binder.
	 * 
	 * @param binderInfo
	 */
	private void buildLandingPage( BinderInfo binderInfo )
	{
		setBinderInfo( binderInfo );

		// Read the configuration data from the server.
		readConfigurationData();
	}
	
	
	/**
	 * Loads the LandingPage split point and returns an instance of it
	 * via the callback.
	 *
	 * @param binderInfo
	 * @param vClient
	 */
	public static void createAsync( final BinderInfo binderInfo, final ViewReady viewReady, final ViewClient vClient )
	{
		GWT.runAsync( LandingPageView.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				LandingPageView lp;
				
				lp = new LandingPageView( binderInfo, viewReady );
				vClient.onSuccess( lp );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_LandingPage() );
				vClient.onUnavailable();
			}
		} );
	}
	

	/**
	 * Loads the LandingPage split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param vClient
	 */
	public static void createAsync( final ConfigData configData, final ViewClient vClient )
	{
		GWT.runAsync( LandingPageView.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				LandingPageView lp;
				
				lp = new LandingPageView( configData );
				vClient.onSuccess( lp );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_LandingPage() );
				vClient.onUnavailable();
			}
		} );
	}
	
	/**
	 * 
	 */
	private void init()
	{
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "landingPageMainPanel" );
		m_mainPanel.addStyleName( "landingPageOverflowAuto" );
		
		initWidget( m_mainPanel );
	}
	
	/**
	 * Issue an ajax call to read the configuration data for this landing page.
	 */
	private void readConfigurationData()
	{
		GetLandingPageDataCmd cmd;

		final String binderId = getBinderIdAsString();
		if ( binderId == null ) {
			doViewReady();
			return;
		}
		
		// Issue an ajax request to get the landing page configuration data.
		cmd = new GetLandingPageDataCmd( binderId );
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
					GwtTeaming.getMessages().rpcFailure_GetLandingPageData(),
					binderId );
				doViewReady();
			}
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				final ConfigData configData;
				
				configData = (ConfigData) response.getResponseData();
				
				GwtClientHelper.deferCommand(new ScheduledCommand()
				{
					/**
					 * 
					 */
					@Override
					public void execute()
					{
						// Add the landing page elements to the landing page.
						buildLandingPage( configData );
					}
				});
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
	
	/*
	 * Tells the super class that the view is ready to go.
	 */
	private void doViewReady() {
		super.viewReady();
	}
}
