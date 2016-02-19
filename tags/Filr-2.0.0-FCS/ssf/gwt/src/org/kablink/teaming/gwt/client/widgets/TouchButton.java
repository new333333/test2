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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * Class used to make a button touchable on an touch device
 * (iPhone, iPad, ...)
 *  
 * @author drfoster@novell.com
 */
public class TouchButton implements TouchStartHandler, TouchMoveHandler, TouchEndHandler {
	private boolean	m_touchMoved;	//
	private Button	m_button;		// This Button this TouchButton wraps.
	private int		m_startX;		//
	private int		m_startY;		//
	
	/**
	 * Constructor method.
	 * 
	 * @param button
	 */
	public TouchButton(final Button button) {
		// Initialize the super class...
		super();

		// ...store the parameters...
		m_button = button;
		
		// ...and add the touch start, move and end Handlers.
		m_button.addDomHandler(this, TouchStartEvent.getType());
		m_button.addDomHandler(this, TouchMoveEvent.getType() );
		m_button.addDomHandler(this, TouchEndEvent.getType()  );
	}

	/**
	 * Implements the TouchEndHandler.onTouchEnd() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTouchEnd(TouchEndEvent event) {
		// If the touch hasn't moved...
		if (!m_touchMoved) {
			// ...translate it into a click event.
			NativeEvent evt = Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false);
			m_button.getElement().dispatchEvent(evt);
		}
	}
	
	/**
	 * Implements the TouchMoveHandler.onTouchMove() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTouchMove(TouchMoveEvent event) {
		// Have we registered the touch as moved yet?
		if (!m_touchMoved) {
			// No!  Has it moved now?  
			Touch touch = event.getTouches().get(0);
			int deltaX = Math.abs(m_startX - touch.getClientX());
			int deltaY = Math.abs(m_startY - touch.getClientY());
			if ((deltaX > 5) || (deltaY > 5)) {
				// Yes!  We consider it moved if it moved more than 5px
				// in both the X and Y directions.
				m_touchMoved = true;
			}
		}
	}
	
	/**
	 * Implements the TouchStartHandler.onTouchStart() method.
	 * 
	 * @param event
	 */
	@Override
	public void onTouchStart(TouchStartEvent event) {
		// Track the fact that a touch sequence has been started.
		Touch touch = event.getTouches().get(0);
		m_startX     = touch.getClientX();
		m_startY     = touch.getClientY();
		m_touchMoved = false;
	}
}
