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

import org.kablink.teaming.gwt.client.GetterCallback;
import org.kablink.teaming.gwt.client.lpe.GraphicConfig;
import org.kablink.teaming.gwt.client.lpe.GraphicProperties;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;




/**
 * 
 * @author jwootton
 *
 */
public class GraphicWidget extends VibeWidget
{
	private GraphicProperties m_properties;
	private String m_style;
	private Image m_img;

	/**
	 * 
	 */
	public GraphicWidget( GraphicConfig config, WidgetStyles widgetStyles )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config, widgetStyles );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}

	/**
	 * 
	 */
	private VibeFlowPanel init( GraphicConfig config, WidgetStyles widgetStyles )
	{
		GraphicProperties properties;
		VibeFlowPanel mainPanel;
		
		m_properties = new GraphicProperties();
		properties = config.getProperties();
		m_properties.copy( properties );
		
		m_style = config.getLandingPageStyle();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "graphicWidgetMainPanel" + m_style );
		
		m_img = new Image();
		mainPanel.add( m_img );
		if ( m_properties.getShowBorderValue() == true )
		{
			m_img.addStyleName( "landingPageWidgetShowBorder" );

			// Set the border width and color.
			GwtClientHelper.setElementBorderStyles( m_img.getElement(), widgetStyles );
		}
		
		// Set the width and height
		{
			Style style;
			int width;
			int height;
			Unit unit;
			
			style = m_img.getElement().getStyle();
			
			width = m_properties.getWidth();
			unit = m_properties.getWidthUnits();
			if ( width > 0 )
				style.setWidth( width, unit );
			
			height = m_properties.getHeight();
			unit = m_properties.getHeightUnits();
			if ( height > 0 )
				style.setHeight( height, unit );
		}


		// Issue an ajax request to get the url needed to display the graphic.
		m_properties.getGraphicUrl( new GetterCallback<String>()
		{
			/**
			 * 
			 */
			@Override
			public void returnValue( String url )
			{
				m_img.setUrl( url );
			}
		} );
		
		return mainPanel;
	}
}

