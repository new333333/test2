/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

/**
 * Class that provides the label widget used for a title on a landing
 * page. 
 *  
 * @author drfoster@novell.com
 */
public class LandingPageTitle extends InlineLabel {
	/**
	 * Constructor method.
	 */
	public LandingPageTitle() {
		// Initialize the super class...
		super();
		
		// ...and set the appropriate styles for the widget.
		setLandingingPageTitleStyles();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param labelText
	 */
	public LandingPageTitle(String labelText) {
		// Initialize the super class...
		super(labelText);
		
		// ...and set the appropriate styles for the widget.
		setLandingingPageTitleStyles();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param labelElement
	 */
	public LandingPageTitle(Element labelElement) {
		// Initialize the super class...
		super(labelElement);
		
		// ...and set the appropriate styles for the widget.
		setLandingingPageTitleStyles();
	}

	/**
	 * Sets the content of a landing page title.
	 * 
	 * @param title
	 * @param defaultTitle
	 */
	public void setContent(String title, String defaultTitle) {
		if (GwtClientHelper.hasString(title))
		     getElement().setInnerHTML(title       );
		else setText(                  defaultTitle);
	}

	/*
	 * Sets the style appropriate for a landing page title.
	 * 
	 * Sets up style names as appropriate for a landing page.
	 */
	private void setLandingingPageTitleStyles() {
		removeStyleName("gwt-InlineLabel");
	}
}
