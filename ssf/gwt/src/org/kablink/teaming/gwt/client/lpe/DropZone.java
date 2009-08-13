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

import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;

/**
 * 
 * @author jwootton
 *
 */
public abstract class DropZone extends Composite
	implements HasMouseOutHandlers, HasMouseOverHandlers, MouseOutHandler, MouseOverHandler
{
	private LandingPageEditor	m_lpe	= null;
	
	/**
	 * This method should add the given widget to this DropZone.
	 */
	public abstract void addWidgetToDropZone( DropWidget dropWidget );
	
	
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
	 * 
	 */
	public abstract void hideDropClue();

	/**
	 * 
	 */
	public void onMouseOut( MouseOutEvent event )
	{
		// Is the user currently dragging an item from the palette?
		if ( m_lpe != null && m_lpe.isPaletteItemDragInProgress() )
		{
			// Yes, tell the landing page editor that the cursor is no longer over this drop zone.
			m_lpe.setDropZone( null );
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
			m_lpe.setDropZone( this );
		}
	}// end onMouseOver()


	/**
	 * 
	 */
	public void setLandingPageEditor( LandingPageEditor lpe )
	{
		m_lpe = lpe;
	}// end setLandingPageEditor()

	/**
	 * 
	 */
	public abstract void showDropClue();
	
}// end DropZone()
