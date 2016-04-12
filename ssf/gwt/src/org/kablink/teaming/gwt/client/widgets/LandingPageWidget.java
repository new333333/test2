/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.lpe.ConfigItem;
import org.kablink.teaming.gwt.client.rpc.shared.GetLandingPageDataCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * This class will display the elements found in a landing page.
 * @author jwootton
 */
public class LandingPageWidget  extends VibeWidget
{
	private BinderInfo m_binderInfo;
	private VibeFlowPanel m_mainPanel;

	
	/**
	 * Callback interface to interact with the landing page widget asynchronously after it loads. 
	 */
	public interface LandingPageWidgetClient
	{
		void onSuccess( LandingPageWidget lpe );
		void onUnavailable();
	}


	
	/**
	 * 
	 */
	private LandingPageWidget( final BinderInfo binderInfo )
	{
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
				// Initialize this landing page for the given binder.
				buildLandingPage( binderInfo );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * 
	 */
	private LandingPageWidget( final ConfigData configData )
	{
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
				// Initialize this landing page for the given binder.
				buildLandingPage( configData );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Build this landing page for the given ConfigData.
	 */
	private void buildLandingPage( ConfigData configData )
	{
		int i;
		int numItems;
		String bgColor;
		String bgImgUrl;
		
		m_mainPanel.clear();
		
		if ( configData == null )
			return;

		configData.parse();
		
		if ( configData.isPreviewMode() == false )
		{
			String binderId;
			
			binderId = configData.getBinderId();
			
			// Handle the various landing page options such as hiding the masthead, hiding the menu, etc.
			if ( binderId != null )
			{
				GwtTeaming.getMainPage().handleLandingPageOptions(
																binderId,
																configData.getHideMasthead(),
																configData.getHideNavPanel(),
																false,
																configData.getHideMenu() );
			}
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
		
		// Add items to the widget that are defined in the configuration.
		numItems = configData.size();
		for (i = 0; i < numItems; ++i)
		{
			ConfigItem configItem;
			
			// Get the next item in the list.
			configItem = configData.get( i );
			if ( configItem != null )
			{
				ResizeComposite widget;
				
				// Create the appropriate composite based on the given ConfigItem.
				widget = configItem.createWidget( configData.getLandingPageProperties().getWidgetStyles() );
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
	}
	
	/**
	 * Build the landing page with data from the given binder.
	 * 
	 * @param binderInfo
	 */
	private void buildLandingPage( BinderInfo binderInfo )
	{
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
	public static void createAsync( final BinderInfo binderInfo, final LandingPageWidgetClient lpClient )
	{
		GWT.runAsync( LandingPageWidget.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_LandingPageWidget() );
				lpClient.onUnavailable();
			}

			@Override
			public void onSuccess()
			{
				LandingPageWidget lp;
				
				lp = new LandingPageWidget( binderInfo );
				lpClient.onSuccess( lp );
			}
		} );
	}
	

	/**
	 * Loads the LandingPage split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param vClient
	 */
	public static void createAsync( final ConfigData configData, final LandingPageWidgetClient lpClient )
	{
		GWT.runAsync( LandingPageWidget.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				LandingPageWidget lp;
				
				lp = new LandingPageWidget( configData );
				lpClient.onSuccess( lp );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_LandingPageWidget() );
				lpClient.onUnavailable();
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
		//m_mainPanel.addStyleName( "landingPageOverflowAuto" );
		
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
			@Override
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
			@Override
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
					@Override
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
}
