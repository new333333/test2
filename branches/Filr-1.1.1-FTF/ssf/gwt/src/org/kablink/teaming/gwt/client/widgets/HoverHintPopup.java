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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class used to implement a hover hint.  
 * 
 * @author drfoster@novell.com
 */
public class HoverHintPopup extends TeamingPopupPanel {
	private InlineLabel m_hoverHintLabel;	//

	/**
	 * Constructor method.
	 */
	public HoverHintPopup() {
		// Initialize the super class...
		super(true);
		
		// ...initialize the widget's style...
		removeStyleName("gwt-PopupPanel");
		addStyleName("vibe-hoverHint");

		// ...and create an InlineLabel to hold the hover hint text.
		m_hoverHintLabel = new InlineLabel();
		m_hoverHintLabel.removeStyleName("gwt-InlineLabel");
		m_hoverHintLabel.addStyleName("vibe-hoverHintLabel");
		setWidget(m_hoverHintLabel);
	}

	/**
	 * Set the text in the hover hint.
	 * 
	 * @param hoverText
	 * @param isHtml
	 */
	public void setHoverText(String hoverText, boolean isHtml) {
		if (isHtml)
		     m_hoverHintLabel.getElement().setInnerHTML(hoverText);
		else m_hoverHintLabel.getElement().setInnerText(hoverText);
	}
	
	public void setHoverText(String hoverText) {
		// Always use the initial form of the method.
		setHoverText(hoverText, true);
	}

	/**
	 * Shows the hover hint relative to an element.
	 * 
	 * @param element
	 */
	public void showHintRelativeTo(Element element) {
		showRelativeTo(GwtClientHelper.getUIObjectFromElement(element));
	}
	
	/**
	 * Shows the hover hint relative to a widget.
	 * 
	 * @param widget
	 */
	public void showHintRelativeTo(Widget widget) {
		showRelativeTo(widget);
	}
}
