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

import org.kablink.teaming.gwt.client.lpe.IFrameConfig;
import org.kablink.teaming.gwt.client.lpe.IFrameProperties;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.ui.Frame;


/**
 * 
 * @author jwootton
 *
 */
public class IFrameWidget extends VibeWidget
{
	private IFrameProperties m_properties;
	private String m_style;

	/**
	 * 
	 */
	public IFrameWidget( IFrameConfig config )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * 
	 */
	private VibeFlowPanel init( IFrameConfig config )
	{
		IFrameProperties properties;
		VibeFlowPanel mainPanel;
		Frame iframe;
		Element element;
		
		m_properties = new IFrameProperties();
		properties = config.getProperties();
		m_properties.copy( properties );
		
		m_style = config.getLandingPageStyle();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "iFrameWidgetMainPanel" + m_style );
		
		iframe = new Frame();
		mainPanel.add( iframe );

		// Get the iframe's element.
		element = iframe.getElement();
		if ( element instanceof IFrameElement )
		{
			IFrameElement iframeElement;
			String value;
			
			iframeElement = (IFrameElement) element;
			
			// Update the frame's properties.
			if ( m_properties.getShowBorder() == true )
				iframe.addStyleName( "iFrameWidgetShowBorder" );
			else
				iframe.addStyleName( "iFrameWidgetNoBorder" );
			
			iframeElement.setName( m_properties.getName() );
			
			value = m_properties.getScrollbarValueAsString();
			if ( value != null && value.equalsIgnoreCase( "auto" ) )
				iframeElement.removeAttribute( "scrolling" );
			else
				iframeElement.setScrolling( value );
			
			iframeElement.setAttribute( "height", m_properties.getHeightAsString() );
			iframeElement.setAttribute( "width", m_properties.getWidthAsString() );
			iframeElement.setSrc( m_properties.getUrl() );
		}
		
		return mainPanel;
	}
}
