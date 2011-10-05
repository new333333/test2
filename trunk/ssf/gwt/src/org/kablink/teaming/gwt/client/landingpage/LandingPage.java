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

package org.kablink.teaming.gwt.client.landingpage;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.lpe.ConfigItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetLandingPageDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.AdminControl;
import org.kablink.teaming.gwt.client.widgets.VibeDockLayoutPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * This widget is the Landing Page.  It is used to render a landing page configuration.
 * @author jwootton
 *
 */
public class LandingPage extends ViewBase
{
	private String m_binderId;
	private ConfigData m_configData;
	private VibeDockLayoutPanel m_mainPanel;
	
	/**
	 * 
	 */
	public LandingPage( final String binderId, final ViewReady viewReady )
	{
		super( viewReady );
		
		Scheduler.ScheduledCommand cmd;
		FlowPanel flowPanel;
		
		m_mainPanel = new VibeDockLayoutPanel( Style.Unit.PX );
		m_mainPanel.addStyleName( "landingPageMainPanel" );
		flowPanel = new FlowPanel();
		m_mainPanel.add( flowPanel );
		
		initWidget( m_mainPanel );

		cmd = new Scheduler.ScheduledCommand()
		{
			/**
			 * 
			 */
			public void execute()
			{
				// Initialize this landing page for the given binder.
				initialize( binderId );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Callback interface to interact with a Landing Page asynchronously after it loads. 
	 */
	public interface LandingPageClient
	{
		/**
		 * 
		 */
		void onSuccess( LandingPage landingPage );
		
		/**
		 * 
		 */
		void onUnavailable();
	}

	/**
	 * Construct this landing page given the configuration data found in m_configData.
	 */
	private void constructLandingPage()
	{
		int i;;
		int numItems;
		FlowPanel flowPanel;
		
		if ( m_configData == null )
			return;
		
		flowPanel = (FlowPanel) m_mainPanel.getCenter();
		flowPanel.clear();
		
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
					flowPanel.add( widget );
				else
				{
					//!!!
					Label label;
					
					label = new Label( "widget: " + configItem.getClass().getName() );
					flowPanel.add( label );
				}
			}
		}

		// Tell the base class that we're done constructing the the
		// landing page view.
		super.viewReady();
	}
	
	/**
	 * Loads the LandingPage split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param landingPageClient
	 */
	public static void createAsync( final String binderId, final ViewReady viewReady, final LandingPageClient landingPageClient )
	{
		GWT.runAsync( AdminControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				LandingPage lp;
				
				lp = new LandingPage( binderId, viewReady );
				landingPageClient.onSuccess( lp );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_LandingPage() );
				landingPageClient.onUnavailable();
			}
		} );
	}
	
	/**
	 * Initialize this landing page with data from the given binder.
	 */
	public void initialize( String binderId )
	{
		m_configData = null;
		m_binderId = binderId;

		// Read the configuration data from the server.
		readConfigurationData();
	}
	
	
	/**
	 * Issue an ajax call to read the configuration data for this landing page.
	 */
	private void readConfigurationData()
	{
		GetLandingPageDataCmd cmd;
		
		if ( m_binderId == null )
			return;
		
		// Issue an ajax request to get the landing page configuration data.
		cmd = new GetLandingPageDataCmd( m_binderId );
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
					m_binderId );
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				Scheduler.ScheduledCommand cmd;
				
				m_configData = (ConfigData) response.getResponseData();
				m_configData.parse();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					/**
					 * 
					 */
					public void execute()
					{
						// Add the landing page elements to the landing page.
						constructLandingPage();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );
	}
}
