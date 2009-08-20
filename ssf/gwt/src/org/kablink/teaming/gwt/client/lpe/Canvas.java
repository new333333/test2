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

package org.kablink.teaming.gwt.client.lpe;

import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This widget is used as the canvas in the Landing Page Editor.  This canvas displays the
 * elements that make up the Landing Page configuration.
 * @author jwootton
 *
 */
public class Canvas extends Composite
	implements DropZone
{
	private FlowPanel	m_panel;
	private FlowPanel m_dropIndicator;	// Horizontal bar that shows the user where a dropped widget will be inserted.
	private LandingPageEditor	m_lpe	= null;
	private DropWidget m_dropBeforeWidget = null;	// When a widget gets dropped on the canvas we will insert it before this widget.
	
	/**
	 * 
	 */
	public Canvas( LandingPageEditor lpe )
	{
		m_panel = new FlowPanel();

		// Associate the panel with its stylesheet.
		m_panel.setStyleName( "lpeCanvas" );
		m_panel.addStyleName( "lpeCanvas_width" );
		
		// Create a visual indicator that show the user where a dropped widget will be inserted.
		m_dropIndicator = new FlowPanel();
		m_dropIndicator.addStyleName( "lpeDropIndicator" );
		m_dropIndicator.addStyleName( "lpeCanvas_width" );
		m_dropIndicator.setVisible( false );
		m_panel.add( m_dropIndicator );
		
		// Register this widget for mouse-out and mouse-over events.
		addMouseOverHandler( this );
		addMouseOutHandler( this );
		
		// Remember the Landing Page Editor this canvas is in.
		m_lpe = lpe;
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_panel );
	}// end Canvas()
	

	/**
	 * 
	 */
	public HandlerRegistration addMouseOutHandler( MouseOutHandler handler )
	{
		return addDomHandler( handler, MouseOutEvent.getType() );
	}// end addMouseOutHandler()
	
	
	/**
	 * 
	 */
	public HandlerRegistration addMouseOverHandler( MouseOverHandler handler )
	{
		return addDomHandler( handler, MouseOverEvent.getType() );
	}// end addMouseOverHandler()
	

	/**
	 * Add the given widget to the canvas.  Use the x and y coordinates to determine where in our list of widgets
	 * we should add the new widget.
	 */
	public void addWidgetToDropZone( DropWidget dropWidget )
	{
		// Do we have a widget we should insert the dropped widget before?
		if ( m_dropBeforeWidget == null )
		{
			// No, add the widget to the end.
			m_panel.add( dropWidget );
		}
		else
		{
			// Yes, insert the dropped widget at the appropriate location.
			m_panel.insert( dropWidget, m_panel.getWidgetIndex( m_dropBeforeWidget ) );
		}
	}// end addWidgetToDropZone()
	
	
	/**
	 * If the user dropped a widget at the given mouse position, calculate the widget the dropped widget
	 * would be inserted before.
	 */
	public DropWidget getDropBeforeWidget( MouseEvent mouseEvent )
	{
		int numWidgets;
		int i;
		Widget widget;
		
		// Given the current position of the mouse, look at all the widgets on the canvas and figure out
		// which widget we would insert the drop widget before.
		numWidgets = m_panel.getWidgetCount();
		for (i = 0; i < numWidgets; ++i)
		{
			// Get the next widget in the canvas.
			widget = m_panel.getWidget( i );
			
			// Is this widget a widget that has been dropped on the canvas?
			if ( widget instanceof DropWidget )
			{
				int pos;
				DropWidget dropWidget;
				
				// Yes.
				dropWidget = (DropWidget) widget;
				
				// Is the cursor above the widget.
				pos = dropWidget.getMousePosOverWidget( mouseEvent );
				if ( pos == -1 )
				{
					// Yes
					return dropWidget;
				}
				
				// Is the cursor over the top-half of the widget?
				if ( pos == 1 )
				{
					// Yes
					return dropWidget;
				}
			}
		}
		
		// If we get here the cursor must be below all the widgets on the canvas.
		return null;
	}// end getDropBeforeWidget()
	
	
	/**
	 * Calculate where the drop clue should be positioned.
	 */
	public int getDropClueY( MouseEvent mouseEvent )
	{
		DropWidget dropWidget = null;
		Widget lastWidget;
		int numWidgets;
		
		// If the user were to drop a widget at the current mouse position, get the widget we would insert
		// the dropped widget before.
		dropWidget = getDropBeforeWidget( mouseEvent );

		// Do we have a DropWidget?
		if ( dropWidget != null )
		{
			// Yes, position the drop clue immediately above the widget.
			return dropWidget.getAbsoluteTop() - m_dropIndicator.getOffsetHeight();
		}
		
		// If we get here we don't have a widget we should insert a dropped widget before.
		// In this case we want the drop clue positioned below the last widget on the canvas.
		numWidgets = m_panel.getWidgetCount();
		lastWidget = m_panel.getWidget( numWidgets-1 );
		if ( lastWidget instanceof DropWidget )
		{
			return lastWidget.getAbsoluteTop() + lastWidget.getOffsetHeight();
		}
		
		// Position the drop clue at the top
		return 0;
	}// end getDropClueY()
	
	
	/**
	 * Hide the visual clue that was shown in the highlightDropZone() method.
	 */
	public void hideDropClue()
	{
		m_panel.removeStyleName( "lpeCanvas_highlighted" );
		m_dropIndicator.setVisible( false );
	}// end hideDropClue()

	
	/**
	 * 
	 */
	public void onMouseOut( MouseOutEvent event )
	{
		// Is the user currently dragging an item from the palette?
		if ( m_lpe != null && m_lpe.isPaletteItemDragInProgress() )
		{
			// Yes, tell the landing page editor that the cursor is no longer over this drop zone.
			m_lpe.setDropZone( null, event );
		}
	}// end onMouseOut()


	/**
	 * 
	 */
	public void onMouseOver( MouseOverEvent event )
	{
		// Is the user currently dragging an item from the palette?
		if ( m_lpe != null && m_lpe.isPaletteItemDragInProgress() )
		{
			// Yes, tell the landing page editor what drop zone the cursor is over.
			m_lpe.setDropZone( this, event );
		}
	}// end onMouseOver()


	/**
	 * This method gets called to let us know where the user wants a widget dropped on this canvas.
	 */
	public void setDropLocation( MouseEvent mouseEvent )
	{
		// Figure out the widget we would insert a dropped widget before.
		m_dropBeforeWidget = getDropBeforeWidget( mouseEvent );
	}// end setDropLocation()
	
	
	/**
	 * Show a visual clue that will indicate where a widget would be added if it were
	 * dropped on this Canvas. 
	 */
	public void showDropClue( MouseEvent mouseEvent )
	{
		// Does the canvas have any widgets?
		if ( m_panel.getWidgetCount() == 1 )
		{
			// No, put a border around the entire canvas.
			m_panel.addStyleName( "lpeCanvas_highlighted" );
		}
		else
		{
			Element element;
			
			element = m_dropIndicator.getElement();
			DOM.setStyleAttribute( element, "top", String.valueOf( getDropClueY( mouseEvent ) ) + "px" );
			DOM.setStyleAttribute( element, "left", String.valueOf( getAbsoluteLeft() ) + "px" );
			
			m_dropIndicator.setVisible( true );
		}
	}// end showDropClue()
}// end Canvas
