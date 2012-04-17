/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget that provides a 'layout based' VerticalPanel.
 * 
 * @author drfoster@novell.com
 */
public class VibeVerticalPanel extends VerticalPanel implements ProvidesResize, RequiresResize {
	
	/**
	 * Constructor method.
	 * 
	 * @param width
	 * @param height
	 */
	public VibeVerticalPanel(String width, String height) {
		super();
		
		if (GwtClientHelper.hasString(width))  setWidth( width );
		if (GwtClientHelper.hasString(height)) setHeight(height);
	}
	
	public VibeVerticalPanel() {
		// Always use the initial form of the constructor.
		this("100%", "100%");
	}

	/**
	 * Adds a row at the bottom of the panel that will fill it. 
	 */
	public void addBottomPad() {
		InlineLabel nbsp = new InlineLabel();
		nbsp.getElement().setInnerHTML("&nbsp;");
		add(nbsp);
		setCellHeight(nbsp, "100%");
	}
	
	/**
	 * Implements RequiresResize.onResize()
	 */
	@Override
	public void onResize() {
		// Iterate through the VerticalPanel's widgets...
		for (Widget w:  this) {
			// ...and for each that implements RequiresResize...
	    	if (w instanceof RequiresResize) {
	    		// ...invoke their onResize() method.
	        	((RequiresResize) w).onResize();
	        }
		}
	}
}
