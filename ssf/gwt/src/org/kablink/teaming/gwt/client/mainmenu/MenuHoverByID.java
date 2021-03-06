/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.mainmenu;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;


/**
 * Class used to handle mouse hover events for menu items.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MenuHoverByID implements MouseOverHandler, MouseOutHandler {
	private String m_hoverStyle;	// The style to use with the hover.
	private String m_hoverId;		// The ID of the widget to apply the hover style to.
	
	/**
	 * Class constructor.
	 * 
	 * @param hoverId
	 * @param hoverString;
	 */
	MenuHoverByID(String hoverId, String hoverStyle) {
		// Simply store the parameters.
		m_hoverId = hoverId;
		m_hoverStyle = hoverStyle;
	}
	
	/**
	 * Called when the mouse leaves a menu item.
	 * 
	 * @param me
	 */
	public void onMouseOut(MouseOutEvent me) {
		// Simply remove the hover style.
		Element selectorPanel_New = Document.get().getElementById(m_hoverId);
		selectorPanel_New.removeClassName(m_hoverStyle);
	}
	
	/**
	 * Called when the mouse enters a menu item.
	 * 
	 * @param me
	 */
	public void onMouseOver(MouseOverEvent me) {
		// Simply add the hover style.
		Element selectorPanel_New = Document.get().getElementById(m_hoverId);
		selectorPanel_New.addClassName(m_hoverStyle);
	}
}
