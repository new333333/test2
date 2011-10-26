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

import org.kablink.teaming.gwt.client.lpe.ConfigItem;
import org.kablink.teaming.gwt.client.lpe.ListConfig;
import org.kablink.teaming.gwt.client.lpe.ListProperties;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;



/**
 * 
 * @author jwootton
 *
 */
public class ListWidget extends VibeWidget
{
	private VibeFlowPanel m_layoutPanel;
	private ListProperties m_properties;
	private UListElement m_uList;
	private String m_style;

	/**
	 * 
	 */
	public ListWidget( ListConfig config )
	{
		init( config );
		
		// Add a widget to this table as defined in the TableConfig data
		addChildWidgetsToList( config );

		// All composites must call initWidget() in their constructors.
		initWidget( m_layoutPanel );
	}

	/**
	 * Add the necessary widgets to this ListWidget.
	 */
	private void addChildWidgetsToList( ListConfig config )
	{
		int i;
		
		for (i = 0; i < config.numItems(); ++i)
		{
			ConfigItem configItem;
			ResizeComposite widget;
			LIElement liElement;
			
			// Create a <li> element for the next widget to live in.
			liElement = Document.get().createLIElement();
			m_uList.appendChild( liElement );
			
			// Get the next piece of configuration information.
			configItem = config.get( i );
			
			// Create the appropriate widget based on the given ConfigItem.
			widget = configItem.createWidget();
			if ( widget != null )
				liElement.appendChild( widget.getElement() );
			else
			{
				Label label;
				
				label = new Label( "widget: " + configItem.getClass().getName() );
				liElement.appendChild( label.getElement() );
			}
		}
	}
	
	/**
	 * 
	 */
	private void init( ListConfig config )
	{
		String title;
		ListProperties properties;
		
		properties = config.getProperties();
		m_style = config.getLandingPageStyle();
		
		m_layoutPanel = new VibeFlowPanel();
		m_layoutPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		m_layoutPanel.addStyleName( "listWidgetMainPanel" + m_style );
		
		m_properties = new ListProperties();
		if ( properties != null )
			m_properties.copy( properties );
		
		// Turn borders on/off
		if ( properties.getShowBorderValue() )
		{
			m_layoutPanel.removeStyleName( "landingPageWidgetNoBorder" );
			m_layoutPanel.addStyleName( "landingPageWidgetShowBorder" );
		}
		else
		{
			m_layoutPanel.removeStyleName( "landingPageWidgetShowBorder" );
			m_layoutPanel.addStyleName( "landingPageWidgetNoBorder" );
		}

		// Is there a title?
		title = properties.getTitle();
		if ( title != null && title.length() > 0 )
		{
			VibeFlowPanel titlePanel;
			InlineLabel label;
			
			// Yes, create a place for the title to live.
			titlePanel = new VibeFlowPanel();
			titlePanel.addStyleName( "landingPageWidgetTitlePanel" + m_style );
			titlePanel.addStyleName( "listWidgetTitlePanel" + m_style );
			
			label = new InlineLabel( title );
			titlePanel.add( label );
			
			m_layoutPanel.add( titlePanel );
		}
		
		// Create a panel for the content of the list to live in.
		{
			VibeFlowPanel contentPanel;
			
			contentPanel = new VibeFlowPanel();
			contentPanel.addStyleName( "listWidgetContentPanel" + m_style );
			m_layoutPanel.add( contentPanel );
			
			// Create a <ul> element for the content of the list to live in
			m_uList = Document.get().createULElement();
			contentPanel.getElement().appendChild( m_uList );
		}
	}
}

