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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.AccessToItemDeniedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.lpe.ConfigItem;
import org.kablink.teaming.gwt.client.lpe.ListConfig;
import org.kablink.teaming.gwt.client.lpe.ListProperties;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;



/**
 * 
 * @author jwootton
 *
 */
public class ListWidget extends VibeWidget
	implements 
		// Event handlers implemented by this class.
		AccessToItemDeniedEvent.Handler
{
	private List<HandlerRegistration> m_registeredEventHandlers;
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] { TeamingEvents.ACCESS_TO_ITEM_DENIED };

	/**
	 * 
	 */
	private class ListItem extends VibeWidget
	{
		Widget m_childWidget;
		
		/**
		 * 
		 */
		public ListItem( ResizeComposite child )
		{
			VibeFlowPanel flowPanel;
			Image img;
			ImageResource imageResource;
			FlexTable table;
			
			m_childWidget = child;
			
			table = new FlexTable();
			table.setCellSpacing( 0 );
			table.getRowFormatter().setVerticalAlign( 0, HasVerticalAlignment.ALIGN_TOP );

			imageResource = GwtTeaming.getImageBundle().listItemArrow();
			img = new Image( imageResource );
			
			table.setWidget( 0, 0, img );
			table.setWidget( 0, 1, child );
			child.addStyleName( "fontSizeMedium" );
			table.getFlexCellFormatter().setWidth( 0, 1, "100%" );

			table.getCellFormatter().getElement( 0, 0 ).getStyle().setPaddingLeft( .5, Unit.EM );
			table.getCellFormatter().getElement( 0, 0 ).getStyle().setPaddingRight( .5, Unit.EM );

			flowPanel = new VibeFlowPanel();
			flowPanel.add( table );
			
			initWidget( flowPanel );
		}

		/**
		 * 
		 */
		public Widget getChildWidget()
		{
			return m_childWidget;
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
			m_contentPanel.addStyleName( "landingPageWidgetContentPanel" + m_style );
			m_layoutPanel.add( m_contentPanel );

			// If the height is not a percentage, set the height of the contentPanel.
			if ( heightUnits != Unit.PCT )
				GwtClientHelper.setHeight( height, heightUnits, m_contentPanel );

			// Set the overflow value
			GwtClientHelper.setOverflow( m_properties.getOverflow(), m_contentPanel );
		}
	}

	/**
	 * 
	 */
	@Override
	public void onAccessToItemDenied( final AccessToItemDeniedEvent event )
	{
		Scheduler.ScheduledCommand cmd1;
		
		cmd1 = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				int numWidgets;
				int i;
				
				// Find the widget the user doesn't have rights to see and remove it.
				numWidgets = m_contentPanel.getWidgetCount();
				for ( i = 0; i < numWidgets; ++i )
				{
					final Widget nextWidget;
					
					nextWidget = m_contentPanel.getWidget( i );
					if ( nextWidget instanceof ListItem )
					{
						ListItem listItem;
						
						listItem = (ListItem) nextWidget;
						if ( listItem.getChildWidget() == event.getWidget() )
						{
							m_contentPanel.remove( nextWidget );
							return;
						}
					}
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd1 );
	}

	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
										GwtTeaming.getEventBus(),
										m_registeredEvents,
										this,
										m_registeredEventHandlers );
		}
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
}

