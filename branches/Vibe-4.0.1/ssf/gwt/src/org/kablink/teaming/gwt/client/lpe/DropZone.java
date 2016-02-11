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

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author jwootton
 *
 */
public class DropZone extends Composite
	implements HasMouseOutHandlers, HasMouseOverHandlers, MouseOutHandler, MouseOverHandler
{
	private FlowPanel	m_panel;
	private FlowPanel m_dropIndicator;	// Horizontal bar that shows the user where a dropped widget will be inserted.
	private LandingPageEditor	m_lpe	= null;
	private DropWidget m_dropBeforeWidget = null;	// When a widget gets dropped on this drop zone we will insert it before this widget.
	private DropZone m_parentDropZone = null;		// The DropZone this DropZone lives in.
	private String m_debugName = "";				// This is used for debug purposes only.
	private String m_id;
	private static int m_baseId = 0;
	
	/**
	 * 
	 */
	public DropZone( LandingPageEditor lpe, String styleName )
	{
		m_panel = new FlowPanel();
		m_panel.setStylePrimaryName( styleName );
		m_id = "dropZone" + m_baseId;
		++m_baseId;
		m_panel.getElement().setId( m_id );

		// Create a visual indicator that show the user where a dropped widget will be inserted.
		m_dropIndicator = new FlowPanel();
		m_dropIndicator.addStyleName( "lpeDropIndicator" );
		m_dropIndicator.setVisible( false );
		m_panel.add( m_dropIndicator );
		
		// Register for mouse-over and mouse-out events.
		addMouseOverHandler( this );
		addMouseOutHandler( this );

		// Remember the Landing Page Editor this drop zone is a part of.
		m_lpe = lpe;
		
		// Tell the Landing Page Editor to cache is DropZone.
		m_lpe.cacheDropZone( this );
		
		if ( styleName != null )
			m_debugName += styleName;
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_panel );
	}// end DropZone()
	
	
	/**
	 * 
	 */
	@Override
	public HandlerRegistration addMouseOutHandler( MouseOutHandler handler )
	{
		return addDomHandler( handler, MouseOutEvent.getType() );
	}// end addMouseOutHandler()
	
	
	/**
	 * 
	 */
	@Override
	public HandlerRegistration addMouseOverHandler( MouseOverHandler handler )
	{
		return addDomHandler( handler, MouseOverEvent.getType() );
	}// end addMouseOverHandler()
	

	/**
	 * Add the given widget to this drop zone.  Use the x and y coordinates to determine where in our list of widgets
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
		
		// Tell the widget which DropZone he lives in.
		dropWidget.setParentDropZone( this );
	}// end addWidgetToDropZone()
	
	
	/**
	 * Adjust the height of all the table widgets so all the DropZones in a table are the same height.
	 */
	public int  adjustHeightOfAllTableWidgets()
	{
		ArrayList<DropWidget> widgets;
		int i;
		
		// Get all of the widgets from this DropZone
		widgets = getWidgets();
		
		// Go through the list of widgets and have each TableDropWidget adjust its height
		for (i = 0; i < widgets.size(); ++i)
		{
			DropWidget nextWidget;
			
			// Get the next widget.
			nextWidget = widgets.get( i );
			
			// Is this widget a TableDropWidget?
			if ( nextWidget instanceof TableDropWidget )
			{
				TableDropWidget tableWidget;
				
				// Yes, adjust the height of the table widget.
				tableWidget = (TableDropWidget) nextWidget;
				tableWidget.adjustTableHeight();
			}
			else if ( nextWidget instanceof ListDropWidget )
			{
				ListDropWidget listWidget;
				
				// Adjust the height of all table widgets contained in this list widget.
				listWidget = (ListDropWidget) nextWidget;
				listWidget.adjustHeightOfAllTableWidgets();
			}
		}// end for()
		
		// Get the adjusted height of this drop zone.
		return getOffsetHeight();
	}// end adjustHeightOfAllTableWidgets()
	

	/**
	 * Check to see if this DropZone contains the given DropZone.
	 */
	public boolean containsDropZone( DropZone dropZone )
	{
		ArrayList<DropWidget> childWidgets;
		DropWidget nextWidget;
		int i;

		// Get the list of widgets that are children of this DropZone.
		childWidgets = getWidgets();
		
		// Spin through the list of child widgets and see if they contain the given DropZone
		if ( childWidgets != null )
		{
			for (i = 0; i < childWidgets.size(); ++i)
			{
				nextWidget = childWidgets.get( i );
				
				// Does this widget have DropZones?
				if ( nextWidget instanceof HasDropZone )
				{
					// Yes, does this widget hold the given DropZone
					if ( ((HasDropZone) nextWidget).containsDropZone( dropZone ) )
					{
						// Yes
						return true;
					}
				}
			}
		}

		// If we get here, we don't hold the given drop zone.
		return false;
	}
	
	
	/**
	 * 
	 */
	public String getDebugName()
	{
		return m_debugName;
	}
	
	
	/**
	 * If the user dropped a widget at the given mouse position, calculate the widget the dropped widget
	 * would be inserted before.
	 */
	public DropWidget getDropBeforeWidget( int clientY )
	{
		int numWidgets;
		int i;
		Widget widget;
		
		// Given the current position of the mouse, look at all the widgets in this drop zone and figure out
		// which widget we would insert the drop widget before.
		numWidgets = m_panel.getWidgetCount();
		for (i = 0; i < numWidgets; ++i)
		{
			// Get the next widget in this drop zone.
			widget = m_panel.getWidget( i );
			
			// Is this widget a widget that has been dropped on this drop zone?
			if ( widget instanceof DropWidget )
			{
				int pos;
				DropWidget dropWidget;
				
				// Yes.
				dropWidget = (DropWidget) widget;
				
				// Is the cursor above the widget.
				pos = dropWidget.getMousePosOverWidget( clientY );
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
		
		// If we get here the cursor must be below all the widgets in this drop zone.
		return null;
	}// end getDropBeforeWidget()
	
	
	/**
	 * Calculate the x position of where the drop clue should be positioned.
	 */
	public int getDropClueX( int clientX )
	{
		return getAbsoluteLeft() - m_lpe.getCanvasLeft();		
	}// end getDropClueX()
	
	
	/**
	 * Calculate where the drop clue should be positioned.
	 */
	public int getDropClueY( int clientY )
	{
		DropWidget dropWidget = null;
		Widget lastWidget;
		int numWidgets;
		int canvasY;
		int scrollY;
		int yPos;
		
		// If the user were to drop a widget at the current mouse position, get the widget we would insert
		// the dropped widget before.
		dropWidget = getDropBeforeWidget( clientY );

		// Get the absolute position of the canvas.
		canvasY = m_lpe.getCanvasTop();
		
		scrollY = m_lpe.getCanvasScrollY();
		
		// Do we have a DropWidget?
		if ( dropWidget != null )
		{
			// Yes, position the drop clue immediately above the widget.
			yPos = dropWidget.getAbsoluteTop() - m_dropIndicator.getOffsetHeight() - canvasY + scrollY;
			if ( yPos < 0 )
				yPos = 0;
			
			return yPos;
		}
		
		// If we get here we don't have a widget we should insert a dropped widget before.
		// In this case we want the drop clue positioned below the last widget on this drop zone.
		numWidgets = m_panel.getWidgetCount();
		lastWidget = m_panel.getWidget( numWidgets-1 );
		if ( lastWidget instanceof DropWidget )
		{
			return lastWidget.getAbsoluteTop() + lastWidget.getOffsetHeight() - canvasY + scrollY - 5;
		}
		
		// Position the drop clue at the top
		return 0;
	}// end getDropClueY()
	
	
	/**
	 * 
	 */
	public String getId()
	{
		return m_id;
	}
	
	
	/**
	 * Return the DropZone this widget lives in.
	 */
	public DropZone getParentDropZone()
	{
		return m_parentDropZone;
	}
	

	/**
	 * Return how much this drop zone has been scrolled vertically.
	 */
	public int getScrollY()
	{
		Element element;
		int scrollTop;
		
		element = m_panel.getElement();
		scrollTop = DOM.getElementPropertyInt( element, "scrollTop" );
		return scrollTop;
	}// end getScrollY()
	
	
	/**
	 * Return all the widgets that live in this drop zone.
	 */
	public ArrayList<DropWidget> getWidgets()
	{
		ArrayList<DropWidget> widgets;
		int i;
		
		widgets = new ArrayList<DropWidget>();
		for (i = 0; i < m_panel.getWidgetCount(); ++i)
		{
			Widget nextWidget;
			
			// Get the next widget.
			nextWidget = m_panel.getWidget( i );
			
			// Is this widget a DropWidget?
			if ( nextWidget instanceof DropWidget )
			{
				// Yes, add it to the arraylist.
				widgets.add( (DropWidget)nextWidget );
			}
		}

		return widgets;
	}// end getWidgets()
	
	
	/**
	 * Hide the visual clue that was shown in the highlightDropZone() method.
	 */
	public void hideDropClue()
	{
		m_panel.removeStyleDependentName( "highlighted" );
		m_dropIndicator.setVisible( false );
	}// end hideDropClue()
	
	
	/**
	 * Is the mouse over this drop zone?
	 */
	@SuppressWarnings("unchecked")
	public boolean isMouseOverDropZone( MouseEvent mouseEvent )
	{
		int left;
		int top;
		int width;
		int height;
		int mouseY;
		int mouseX;
		
		// Get the position and dimensions of this drop zone
		left = getAbsoluteLeft();
		top = getAbsoluteTop();
		height = getOffsetHeight();
		width = getOffsetWidth();
		
		// Get the position of the mouse.
		mouseY = mouseEvent.getClientY() + Window.getScrollTop();
		mouseX = mouseEvent.getClientX() + Window.getScrollLeft();
		
		// Is the mouse over this drop zone?
		if ( mouseY >= top && mouseY <= (top + height) && mouseX >= left && (mouseX <= left + width) )
			return true;
		
		return false;
	}// end isMouseOverDropZone()


	/**
	 * 
	 */
	@Override
	public void onMouseOut( MouseOutEvent event )
	{
		// Is the user currently dragging an item?
		if ( m_lpe != null && m_lpe.isDragInProgress() )
		{
			// Yes, tell the landing page editor that the cursor is no longer over this drop zone.
			m_lpe.leavingDropZone( this, event.getClientX(), event.getClientY() );
		}
	}// end onMouseOut()


	/**
	 * 
	 */
	@Override
	public void onMouseOver( MouseOverEvent event )
	{
		// Because of a problem with IE we need to handle the on-mouse-over event in the
		// LandingPageEditor.onPreviewNativeEvent().  That is why we just return.
		if ( event != null || event == null )
			return;
		
		// Is the user currently dragging an item?
		if ( m_lpe != null && m_lpe.isDragInProgress() )
		{
			// Yes, tell the landing page editor what drop zone the cursor is over.
			m_lpe.enteringDropZone( this, event.getClientX(), event.getClientY() );
		}
	}// end onMouseOver()


	/**
	 * Remove all the widgets from this DropZone.
	 */
	public void removeAllWidgets()
	{
		ArrayList<DropWidget> widgets;
		int i;
		
		// Get all of the widgets from this DropZone
		widgets = getWidgets();
		
		// Go through the list of widgets and have each TableDropWidget adjust its height
		for (i = 0; i < widgets.size(); ++i)
		{
			DropWidget nextWidget;
			
			// Get the next widget.
			nextWidget = widgets.get( i );

			nextWidget.removeFromParent();
		}
	}
	
	/**
	 * This method gets called to let us know where the user wants a widget dropped on this drop zone.
	 */
	public void setDropLocation( int clientX, int clientY )
	{
		// Figure out the widget we would insert a dropped widget before.
		m_dropBeforeWidget = getDropBeforeWidget( clientY );
	}// end setDropLocation()
	
	
	/**
	 * Set the DropZone this DropZone lives in.
	 */
	public void setParentDropZone( DropZone dropZone )
	{
		m_parentDropZone = dropZone;
	}
	
	/**
	 * Set the height of this drop zone.
	 */
	public void setZoneHeight( int height, Unit unit )
	{
		m_panel.getElement().getStyle().setHeight( height, unit );
	}// end setHeight()
	
	
	/**
	 * Set the width of this drop zone
	 */
	public void setZoneWidth( int width, Unit unit )
	{
		m_panel.getElement().getStyle().setWidth( width, unit );
	}
	
	
	/**
	 * Show a visual clue that will indicate where a widget would be added if it were
	 * dropped on this drop zone. 
	 */
	public void showDropClue( int clientX, int clientY )
	{
		// Does this drop zone have any widgets?
		if ( m_panel.getWidgetCount() == 1 )
		{
			// No, put a border around the entire drop zone.
			m_panel.addStyleDependentName( "highlighted" );
		}
		else
		{
			Element element;
			int width;
			
			element = m_dropIndicator.getElement();
			DOM.setStyleAttribute( element, "top", String.valueOf( getDropClueY( clientY ) ) + "px" );
			DOM.setStyleAttribute( element, "left", String.valueOf( getDropClueX( clientX ) ) + "px" );
			width = (int)(getOffsetWidth() * .97);
			DOM.setStyleAttribute( element, "width", String.valueOf( width ) + "px" );
			
			m_dropIndicator.setVisible( true );
		}
	}// end showDropClue()
}// end DropZone
