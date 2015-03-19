/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class used to communicate information about a browse hierarchy
 * request between the MainMenuControl and its event handlers.
 * 
 * @author drfoster@novell.com
 */
public class OnBrowseHierarchyInfo {
	private MenuItem	m_browseItem;	//
	private Widget		m_browseWidget;	//
	
	/**
	 * Constructor method.
	 * 
	 * @param browseWidget
	 */
	public OnBrowseHierarchyInfo(Widget browseWidget) {
		// Initialize the super class...
		super();
		
		// ...and store the parameter.
		setBrowseWidget(browseWidget);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param browseWidget
	 */
	public OnBrowseHierarchyInfo(MenuItem browseItem) {
		// Initialize the super class...
		super();
		
		// ...and store the parameter.
		setBrowseItem(browseItem);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public MenuItem getBrowseItem()   {return m_browseItem;  }
	public Widget   getBrowseWidget() {return m_browseWidget;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBrowseItem(  MenuItem browseItem)   {m_browseItem   = browseItem;  }
	public void setBrowseWidget(Widget   browseWidget) {m_browseWidget = browseWidget;}
	
	/**
	 * Returns the absolute position the hierarchy browser is to be
	 * displayed at.
	 * 
	 * @return
	 */
	public int getLeft() {return ((null != m_browseWidget) ? m_browseWidget.getAbsoluteLeft()                : m_browseItem.getAbsoluteLeft());                               }
	public int getTop()  {return ((null != m_browseWidget) ? m_browseWidget.getElement().getAbsoluteBottom() : m_browseItem.getParentMenu().getElement().getAbsoluteBottom());}
	
	/**
	 * Returns the Element that running the hierarchy browser.
	 * 
	 * @return
	 */
	public Element getBrowseHierarchyElement() {
		Element reply;
		if      (null != m_browseWidget) reply = m_browseWidget.getElement();
		else if (null != m_browseItem)   reply = m_browseItem.getElement();
		else                             reply = null;
		return reply;
	}
}
