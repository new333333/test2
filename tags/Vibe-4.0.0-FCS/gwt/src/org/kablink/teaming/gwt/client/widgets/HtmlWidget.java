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
import org.kablink.teaming.gwt.client.lpe.HtmlConfig;
import org.kablink.teaming.gwt.client.lpe.HtmlProperties;

import com.google.gwt.core.client.Scheduler;


/**
 * 
 * @author jwootton
 *
 */
public class HtmlWidget extends VibeWidget
{
	private VibeFlowPanel m_layoutPanel;
	private HtmlProperties m_properties;
	private String m_style;

	/**
	 * 
	 */
	public HtmlWidget( HtmlConfig config )
	{
		HtmlProperties properties;
		
		properties = config.getProperties();
		m_style = config.getLandingPageStyle();
		
		init( properties, config.getBinderId() );
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_layoutPanel );
	}

	/**
	 * 
	 */
	private void init( HtmlProperties properties, String binderId )
	{
		m_layoutPanel = new VibeFlowPanel();
		m_layoutPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		m_layoutPanel.addStyleName( "htmlWidgetMainPanel" + m_style );
		
		m_properties = new HtmlProperties();
		if ( properties != null )
			m_properties.copy( properties );
		
		// Replace any markup that may be in the html.
		m_properties.replaceMarkup( binderId, HtmlProperties.ContextType.VIEW, new GetterCallback<String>()
		{
			/**
			 * 
			 */
			public void returnValue( String value )
			{
				Scheduler.ScheduledCommand cmd;

				// Yes
				cmd = new Scheduler.ScheduledCommand()
				{
					public void execute()
					{
						// Update this widget with the folder information
						updateWidget();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		} );

		
		updateWidget();
	}


	/**
	 * 
	 */
	private void updateWidget()
	{
		// Update this widget with the given html.
		if ( m_layoutPanel != null )
			m_layoutPanel.getElement().setInnerHTML( m_properties.getHtml() );
	}
}

