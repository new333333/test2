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

import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * This widget is used as the canvas in the Landing Page Editor.  This canvas displays the
 * elements that make up the Landing Page configuration.
 * @author jwootton
 *
 */
public class Canvas extends DropZone
	implements MouseOutHandler, MouseOverHandler
{
	private FlowPanel	m_panel;
	
	/**
	 * 
	 */
	public Canvas( LandingPageEditor lpe )
	{
		m_panel = new FlowPanel();

		// Associate the panel with its stylesheet.
		m_panel.setStyleName( "lpeCanvas" );
		
		// Register this widget for mouse-out and mouse-over events.
		addMouseOverHandler( this );
		addMouseOutHandler( this );
		
		// Remember the Landing Page Editor this canvas is in.
		setLandingPageEditor( lpe );
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_panel );
	}// end Canvas()
	
	/**
	 * Add the given widget to the canvas.  Use the x and y coordinates to determine where in our list of widgets
	 * we should add the new widget.
	 */
	public void addWidgetToDropZone( DropWidget dropWidget )
	{
		m_panel.add( dropWidget );
	}// end addWidgetToDropZone()
	
	
	/**
	 * Hide the visual clue that was shown in the highlightDropZone() method.
	 */
	public void hideDropClue()
	{
		m_panel.removeStyleName( "lpeCanvas_highlighted" );
	}// end hideDropClue()

	
	/**
	 * Show a visual clue that will indicate where a widget would be added if it were
	 * dropped on this Canvas. 
	 */
	public void showDropClue()
	{
		m_panel.addStyleName( "lpeCanvas_highlighted" );
	}// end showDropClue()
}// end Canvas
