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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.GotoUrlEvent;
import org.kablink.teaming.gwt.client.lpe.LinkToUrlConfig;
import org.kablink.teaming.gwt.client.lpe.LinkToUrlProperties;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;

/**
 * ?
 *
 * @author jwootton
 */
public class UrlWidget extends VibeWidget
{
	private LinkToUrlProperties m_properties;
	private String m_style;

	/**
	 * 
	 */
	public UrlWidget( LinkToUrlConfig config, WidgetStyles widgetStyles )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config, widgetStyles );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * 
	 */
	private VibeFlowPanel init( LinkToUrlConfig config, WidgetStyles widgetStyles )
	{
		LinkToUrlProperties properties;
		VibeFlowPanel mainPanel;
		String url;
		
		m_properties = new LinkToUrlProperties();
		properties = config.getProperties();
		m_properties.copy( properties );
		
		m_style = config.getLandingPageStyle();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "urlWidgetMainPanel" + m_style );
		
		url = m_properties.getUrl();
		if ( url != null && url.length() > 0 )
		{
			Anchor link = new Anchor();
			link.setHref( url );
			link.addStyleName( "urlWidgetLink" + m_style );

			// Set the text color for the title.
			LandingPageTitle span = new LandingPageTitle();
			span.setContent(m_properties.getTitle(), url);
			GwtClientHelper.setElementTextColor( span.getElement(), widgetStyles.getContentTextColor() );
			
			link.getElement().appendChild( span.getElement() );
			
			link.addClickHandler( new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					// Kill this event to prevent GWT from sending the url.
					event.preventDefault();
					
					// Should we open the url in a new window?
					if ( m_properties.getOpenInNewWindow() )
					{
						int height;
						int width;

						// Yes
						width = Window.getClientWidth();
						height = Window.getClientHeight();
						Window.open( m_properties.getUrl(), "_urlWidget", "height=" + String.valueOf( height ) + ",resizeable,scrollbars,width=" + String.valueOf( width ) );
					}
					else
						GwtTeaming.fireEvent( new GotoUrlEvent( m_properties.getUrl() ) );
				}
			});
			
			// Set the text color for the content.
			GwtClientHelper.setElementTextColor( link.getElement(), widgetStyles.getContentTextColor() );

			mainPanel.add( link );
		}
		
		return mainPanel;
	}
}
