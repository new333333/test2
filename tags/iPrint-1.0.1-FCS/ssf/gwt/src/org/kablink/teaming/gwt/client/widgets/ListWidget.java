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
import org.kablink.teaming.gwt.client.lpe.ConfigItem;
import org.kablink.teaming.gwt.client.lpe.ListConfig;
import org.kablink.teaming.gwt.client.lpe.ListProperties;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
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
	/**
	 * 
	 */
	private class ListItem extends VibeWidget
	{
		/**
		 * 
		 */
		public ListItem( ResizeComposite child )
		{
			VibeFlowPanel flowPanel;
			Image img;
			ImageResource imageResource;
			FlexTable table;
			
			table = new FlexTable();
			table.setCellSpacing( 0 );

			imageResource = GwtTeaming.getImageBundle().breadSpace();
			img = new Image( imageResource );
			
			table.setWidget( 0, 0, img );
			table.setWidget( 0, 1, child );
			child.addStyleName( "fontSizeMedium" );
			table.getFlexCellFormatter().setWidth( 0, 1, "100%" );
			
			flowPanel = new VibeFlowPanel();
			flowPanel.add( table );
			
			initWidget( flowPanel );
		}
	}
	
	
	private VibeFlowPanel m_layoutPanel;
	private VibeFlowPanel m_contentPanel;
	private ListProperties m_properties;
	private String m_style;
	private WidgetStyles m_widgetStyles;

	/**
	 * 
	 */
	public ListWidget( ListConfig config, WidgetStyles widgetStyles )
	{
		m_widgetStyles = widgetStyles;
		
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
			
			// Get the next piece of configuration information.
			configItem = config.get( i );
			
			// Create the appropriate widget based on the given ConfigItem.
			widget = configItem.createWidget( m_widgetStyles );
			if ( widget != null )
			{
				ListItem listItem;
				
				listItem = new ListItem( widget );
				m_contentPanel.add( listItem );
			}
			else
			{
				Label label;
				
				label = new Label( "widget: " + configItem.getClass().getName() );
				m_contentPanel.add( label );
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
		int width;
		Unit widthUnits;
		int height;
		Unit heightUnits;
		
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
			
			// Set the border width and color.
			GwtClientHelper.setElementBorderStyles( m_layoutPanel.getElement(), m_widgetStyles );
		}
		else
		{
			m_layoutPanel.removeStyleName( "landingPageWidgetShowBorder" );
			m_layoutPanel.addStyleName( "landingPageWidgetNoBorder" );
		}
		
		// Get the width and height
		width = m_properties.getWidth();
		widthUnits = m_properties.getWidthUnits();
		height = m_properties.getHeight();
		heightUnits = m_properties.getHeightUnits();
		
		// Set the width of the entire widget
		GwtClientHelper.setWidth( width, widthUnits, m_layoutPanel );
		
		// If the height is a percentage, set the height of the entire widget.
		if ( heightUnits == Unit.PCT )
			GwtClientHelper.setHeight( height, heightUnits, m_layoutPanel );
		
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
			label.addStyleName( "listWidgetTitleLabel" + m_style );
			titlePanel.add( label );
			
			// Set the title background color.
			GwtClientHelper.setElementBackgroundColor( titlePanel.getElement(), m_widgetStyles.getHeaderBgColor() );
			
			// Set the title text color.
			GwtClientHelper.setElementTextColor( label.getElement(), m_widgetStyles.getHeaderTextColor() );

			m_layoutPanel.add( titlePanel );
		}
		
		// Create a panel for the content of the list to live in.
		{
			m_contentPanel = new VibeFlowPanel();
			m_contentPanel.addStyleName( "listWidgetContentPanel" + m_style );
			m_layoutPanel.add( m_contentPanel );

			// If the height is not a percentage, set the height of the contentPanel.
			if ( heightUnits != Unit.PCT )
				GwtClientHelper.setHeight( height, heightUnits, m_contentPanel );

			// Set the overflow value
			GwtClientHelper.setOverflow( m_properties.getOverflow(), m_contentPanel );
		}
	}
}

