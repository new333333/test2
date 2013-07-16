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
import org.kablink.teaming.gwt.client.lpe.CustomJspConfig;
import org.kablink.teaming.gwt.client.lpe.CustomJspProperties;
import org.kablink.teaming.gwt.client.rpc.shared.ExecuteLandingPageCustomJspCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;



/**
 * 
 * @author jwootton
 *
 */
public class CustomJspWidget extends VibeWidget
{
	private String m_lpBinderId;	// landing page binder id
	private CustomJspProperties m_properties;
	private AsyncCallback<VibeRpcResponse> m_executeJspCallback = null;
	private VibeFlowPanel m_mainPanel;
	private String m_html;		// The html that we got from executing a jsp
	
	/**
	 * This widget simply displays the name of the jsp file that is associated with the view type. 
	 */
	public CustomJspWidget( CustomJspConfig config, String lpBinderId )
	{
		VibeFlowPanel mainPanel;
		
		// Remember the landing page binderId.
		m_lpBinderId = lpBinderId;
		
		mainPanel = init( config.getProperties(), config.getLandingPageStyle() );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * Evaluate scripts in the html found in the given element.
	 */
	public static native void executeJavaScript( com.google.gwt.dom.client.Element element ) /*-{
		//$wnd.alert( 'In executeJavaScript()' );
		if ( $wnd.top.ss_executeJavascript != null && (typeof $wnd.top.ss_executeJavascript != 'undefined') )
		{
			//$wnd.alert( 'found ss_executeJavascript()' );
			$wnd.top.ss_executeJavascript( element, false );
		}
	}-*/;

	/**
	 * Execute the jsp associated with this enhanced view
	 */
	private void executeJsp()
	{
		// Execute the jsp associated with this enhanced view by issuing an rpc request.
		executeJspViaRpc();
	}
	
	/**
	 * Execute the jsp associated with this enhanced view using GWT rpc
	 */
	private void executeJspViaRpc()
	{
		ExecuteLandingPageCustomJspCmd cmd;
		
		// Create the callback that will be used when we issue an ajax call to execute the jsp.
		if ( m_executeJspCallback == null )
		{
			m_executeJspCallback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_executeCustomJsp(),
						m_properties.getJspName() );
				}
		
				/**
				 * 
				 * @param result
				 */
				public void onSuccess( VibeRpcResponse response )
				{
					Scheduler.ScheduledCommand cmd;
					
					m_html = "";
					
					if ( response.getResponseData() != null )
					{
						StringRpcResponseData responseData;
						
						responseData = (StringRpcResponseData) response.getResponseData();
						m_html = responseData.getStringValue();
					}

					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							m_mainPanel.getElement().setInnerHTML( m_html );
							executeJavaScript( m_mainPanel.getElement() );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
		}

		// Execute the custom jsp
		cmd = new ExecuteLandingPageCustomJspCmd( m_lpBinderId, m_properties.getJspName(), m_properties.createConfigString() );
		GwtClientHelper.executeCommand( cmd, m_executeJspCallback );
	}
	
	/**
	 * 
	 */
	private VibeFlowPanel init( CustomJspProperties properties, String landingPageStyle )
	{
		m_properties = new CustomJspProperties();
		m_properties.copy( properties );
		
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "landingPageWidgetMainPanel" + landingPageStyle );
		m_mainPanel.addStyleName( "customJspWidgetMainPanel" + landingPageStyle );

		// Set the width and height
		{
			Style style;
			int width;
			int height;
			Unit unit;
			
			style = m_mainPanel.getElement().getStyle();
			
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

		// Issue a request to execute the jsp associated with this enhanced view
		{
			Scheduler.ScheduledCommand schCmd;
			
			schCmd = new Scheduler.ScheduledCommand()
			{
				@Override
				public void execute()
				{
					executeJsp();
				}
			};
			Scheduler.get().scheduleDeferred( schCmd );
		}
		
		return m_mainPanel;
	}
}

