/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.binderviews.landingpage;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.DescriptionPanel;
import org.kablink.teaming.gwt.client.binderviews.FooterPanel;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelReady;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.ToolPanelBase.ToolPanelClient;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.lpe.ConfigItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetLandingPageDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * This widget is the Landing Page.  It is used to render a landing page configuration.
 * @author jwootton
 *
 */
public class LandingPage extends ViewBase implements ToolPanelReady
{
	private BinderInfo m_binderInfo;
	private ConfigData m_configData;
	private VibeFlowPanel m_mainPanel;
	
	/**
	 * 
	 */
	public LandingPage( final BinderInfo binderInfo, final ViewReady viewReady )
	{
		super( viewReady );
		
		Scheduler.ScheduledCommand cmd;

		init();
		
		cmd = new Scheduler.ScheduledCommand()
		{
			/**
			 * 
			 */
			public void execute()
			{
				// Initialize this landing page for the given binder.
				buildLandingPage( binderInfo );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * 
	 */
	public LandingPage( final ConfigData configData )
	{
		super( null );
		
		Scheduler.ScheduledCommand cmd;
		
		init();

		cmd = new Scheduler.ScheduledCommand()
		{
			/**
			 * 
			 */
			public void execute()
			{
				// Initialize this landing page for the given binder.
				buildLandingPage( configData );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Build this landing page for the given ConfigData.
	 */
	public void buildLandingPage( ConfigData configData )
	{
		int i;;
		int numItems;
		String bgColor;
		String bgImgUrl;
		
		m_mainPanel.clear();
		
		m_configData = configData;
		m_configData.parse();
		
		if ( m_configData == null )
		{
			// Tell the base class that we're done constructing the landing
			// page view.
			if ( configData.isPreviewMode() == false )
				super.viewReady();

			return;
		}

		String binderId = ((null == m_binderInfo) ? null : m_binderInfo.getBinderId());
		if ( configData.isPreviewMode() == false && binderId != null )
		{
			// Handle the various landing page options such as hiding the masthead, hiding the menu, etc.
			GwtTeaming.getMainPage().handleLandingPageOptions( binderId, m_configData.getHideMasthead(), m_configData.getHideNavPanel(), false, m_configData.getHideMenu() );
			
			// Add the description to the page.
			DescriptionPanel.createAsync( this, m_binderInfo, this, new ToolPanelClient()
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
		bgColor = m_configData.getBackgroundColor();
		if ( bgColor != null && bgColor.length() > 0 )
		{
			// Yes
			m_mainPanel.getElement().getStyle().setBackgroundColor( bgColor );
		}
		
		// Is a background image specified?
		bgImgUrl = m_configData.getBackgroundImgUrl();
		if ( bgImgUrl != null && bgImgUrl.length() > 0 )
		{
			String bgRepeat;
			
			// Yes
			m_mainPanel.getElement().getStyle().setBackgroundImage( "url( " + bgImgUrl + " )" );
			
			// Is a background repeat value specified?
			bgRepeat = m_configData.getBackgroundImgRepeat();
			if ( bgRepeat != null && bgRepeat.length() > 0 )
			{
				// Yes
				m_mainPanel.getElement().getStyle().setProperty( "backgroundRepeat", bgRepeat );
			}
		}
		
		// Add items to the page that are defined in the configuration.
		numItems = m_configData.size();
		for (i = 0; i < numItems; ++i)
		{
			ConfigItem configItem;
			
			// Get the next item in the list.
			configItem = m_configData.get( i );
			if ( configItem != null )
			{
				ResizeComposite widget;
				
				// Create the appropriate composite based on the given ConfigItem.
				widget = configItem.createWidget();
				if ( widget != null )
				{
					m_mainPanel.add( widget );
				}
				else
				{
					Label label;
					
					label = new Label( "widget: " + configItem.getClass().getName() );
					m_mainPanel.add( label );
				}
			}
		}

		// Add the footer to the page
		if ( m_binderInfo != null )
		{
			FooterPanel.createAsync( this, m_binderInfo, this, new ToolPanelClient()
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
		
		// Tell the base class that we're done constructing the landing
		// page view.
		if ( configData.isPreviewMode() == false )
			super.viewReady();
	}
	
	/**
	 * Build the landing page with data from the given binder.
	 * 
	 * @param binderInfo
	 */
	public void buildLandingPage( BinderInfo binderInfo )
	{
		m_configData = null;
		m_binderInfo = binderInfo;

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
		GWT.runAsync( LandingPage.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				LandingPage lp;
				
				lp = new LandingPage( binderInfo, viewReady );
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
		GWT.runAsync( LandingPage.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				LandingPage lp;
				
				lp = new LandingPage( configData );
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

		final String binderId = ((null == m_binderInfo) ? null : m_binderInfo.getBinderId());
		if ( binderId == null )
			return;
		
		// Issue an ajax request to get the landing page configuration data.
		cmd = new GetLandingPageDataCmd( binderId );
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetLandingPageData(),
					binderId );
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				final ConfigData configData;
				
				configData = (ConfigData) response.getResponseData();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					public void execute()
					{
						// Add the landing page elements to the landing page.
						buildLandingPage( configData );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
	}
	
	/**
	 * Implements the ToolPanelReady.toolPanelReady() method.
	 */
	@Override
	public void toolPanelReady( ToolPanelBase toolPanel )
	{
//!		...this needs to be implemented...		
	}// end toolPanelReady()
}
