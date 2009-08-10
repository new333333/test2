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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.lpe.PaletteItem.DragProxy;

import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This widget is the Landing Page Editor.  As its name implies, it is used to edit a 
 * landing page configuration.
 * @author jwootton
 *
 */
public class LandingPageEditor extends Composite
	implements MouseDownHandler, MouseMoveHandler, MouseUpHandler, HasMouseMoveHandlers, HasMouseUpHandlers
{
	private Palette	m_palette;
	private Canvas		m_canvas;
	private boolean	m_dragInProgress;
	private DragProxy	m_dragProxy;
	private HandlerRegistration	m_mouseMoveHandlerReg = null;
	private HandlerRegistration	m_mouseUpHandlerReg = null;
	
	/**
	 * 
	 */
	public LandingPageEditor()
	{
		HorizontalPanel	hPanel;
		VerticalPanel	vPanel;
		Label			hintLabel;
		
		// Create a vertical panel for the hint and the horizontal panel to live in.
		vPanel = new VerticalPanel();
		
		// Create a hint
		hintLabel = new Label( GwtTeaming.getMessages().lpeHint() );
		hintLabel.addStyleName( "lpeHint" );
		vPanel.add( hintLabel );
		
		// Create a panel for the palette and canvas to live in.
		hPanel = new HorizontalPanel();
		
		// Create some space between the palette and the canvas.
		hPanel.setSpacing( 5 );
		
		// Create a palette and a canvas.
		m_palette = new Palette( this );
		m_canvas = new Canvas();
		
		// Add the palette and canvas to the panel.
		hPanel.add( m_palette );
		hPanel.add( m_canvas );
		
		vPanel.add( hPanel );
		
		m_dragInProgress = false;
		
		// All composites must call initWidget() in their constructors.
		initWidget( vPanel );
	}// end LandingPageEditor()
	
	
	/**
	 * Method to add mouse move handlers to this landing page editor.
	 */
	public HandlerRegistration addMouseMoveHandler( MouseMoveHandler handler )
	{
		return addDomHandler( handler, MouseMoveEvent.getType() );
	}// end addMouseMoveHandler()

	
	/**
	 * Method to add mouse up handlers to this landing page editor.
	 */
	public HandlerRegistration addMouseUpHandler( MouseUpHandler handler )
	{
		return addDomHandler( handler, MouseUpEvent.getType() );
	}// end addMouseUpHandler()

	
	/**
	 * Handles the MouseDownEvent.  This will initiate the dragging of an item from the palette.
	 */
	public void onMouseDown( MouseDownEvent event )
	{
		Object	eventSender;
		
		// Is the object that sent this event a PaletteItem object?
		eventSender = event.getSource();
		if ( eventSender instanceof PaletteItem )
		{
			// Yes
			startDrag( event );
			
			// Kill this mouse-down event so text on the page does not get highlighted when the user moves the mouse.
			event.getNativeEvent().preventDefault();
		}
	}// end onMouseDown()
	
	
	/**
	 * Handles the MouseMoveEvent.  If the user is dragging an item from the palette we will update
	 * the position of the drag proxy.
	 */
	public void onMouseMove( MouseMoveEvent event )
	{
		// Is the user currently dragging an item from the palette?
		if ( m_dragInProgress && m_dragProxy != null )
		{
			// Yes, update the position of the drag proxy.
			m_dragProxy.setPopupPosition( event.getClientX(), event.getClientY() );
		}
	}// end onMouseMove()
	
	
	/**
	 * Handle the MouseUpEvent.  If the user is dragging an item from the palette, we will drop the item
	 * on a drop target.
	 */
	public void onMouseUp( MouseUpEvent event )
	{
		// Is the user currently dragging an item from the palette?
		if ( m_dragInProgress )
		{
			// Yes
			m_dragInProgress = false;
			
			// Hide the drag proxy widget.
			if ( m_dragProxy != null )
			{
				m_dragProxy.hide();
				m_dragProxy = null;
			}
			
			// Remove the mouse-move event handler
			if ( m_mouseMoveHandlerReg != null )
			{
				m_mouseMoveHandlerReg.removeHandler();
				m_mouseMoveHandlerReg = null;
			}

			// Remove the mouse-up event handler
			if ( m_mouseUpHandlerReg != null )
			{
				m_mouseUpHandlerReg.removeHandler();
				m_mouseUpHandlerReg = null;
			}
		}
	}// end onMouseUp()
	
	
	/**
	 * 
	 */
	private void startDrag( MouseDownEvent event )
	{
		PaletteItem	paletteItem;
		Object		eventSource;
		
		eventSource = event.getSource();
		if ( !(eventSource instanceof PaletteItem) )
			return;
		
		// Get the PaletteItem the user clicked on.
		paletteItem = (PaletteItem) eventSource;
		
		// Get the drag proxy widget we should display and display it.
		m_dragProxy = paletteItem.getDragProxy();

		// Position the drag proxy widget at the cursor position.
		m_dragProxy.setPopupPosition( event.getClientX(), event.getClientY() );
		
		// Show the drag-proxy widget.
		m_dragProxy.show();
		
		// Register for mouse move events.
		m_mouseMoveHandlerReg = addMouseMoveHandler( this );
		
		// Register for mouse up event.
		m_mouseUpHandlerReg = m_dragProxy.addMouseUpHandler( this );

		// Remember that the drag process has started.
		m_dragInProgress = true;
	}// end startDrag()
}// end LandingPageEditor
