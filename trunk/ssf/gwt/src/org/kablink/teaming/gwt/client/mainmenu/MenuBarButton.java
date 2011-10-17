/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.util.VibeKBHook;
import org.kablink.teaming.gwt.client.widgets.VibeAnchorTabstop;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;


/**
 * Class used to implement a menu button that toggles between two
 * states.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MenuBarButton extends VibeAnchorTabstop {
	private VibeEventBase<?>	m_event;	// The event to fire when the button is clicked.
	
	/*
	 * Inner class that implements clicking on buttons on the menu.
	 */
	private class MenuButtonSelector implements ClickHandler {
		/**
		 * Called when the button is clicked.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			GwtTeaming.fireEvent(m_event);
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param imgRes
	 * @param imgTitle
	 * @param event
	 * @param clickHandler
	 */
	public MenuBarButton(ImageResource imgRes, String imgTitle, VibeEventBase<?> event, ClickHandler clickHandler, VibeKBHook kbHook) {
		// Initialize the super class...
		super(kbHook);
		
		// ...store the parameters...
		m_event = event;
		
		// Create the Image...
		Image img = new Image(imgRes);
		img.setTitle(imgTitle);
		img.addStyleName("mainMenuButton_WidgetImage");
		
		// ...create the Anchor...
		addStyleName("mainMenuButton_WidgetAnchor");
		
		// ...tie things together...
		getElement().appendChild(img.getElement());
		if (null == clickHandler) {
			clickHandler = new MenuButtonSelector();
		}
		addClickHandler(clickHandler);
		
		// ...add mouse over handling...
		MenuHoverByWidget hover = new MenuHoverByWidget(this, "subhead-control-bg2");
		addMouseOverHandler(hover);
		addMouseOutHandler( hover);
	}
	
	public MenuBarButton(ImageResource imgRes, String imgTitle, VibeEventBase<?> event, VibeKBHook kbHook) {
		// Always use the initial form of the constructor.
		this(imgRes, imgTitle, event, null, kbHook);
	}
	
	public MenuBarButton(ImageResource imgRes, String imgTitle, VibeEventBase<?> event) {
		// Always use the initial form of the constructor.
		this(imgRes, imgTitle, event, null);
	}
	
	public MenuBarButton(ImageResource imgRes, String imgTitle, ClickHandler clickHandler, VibeKBHook kbHook) {
		// Always use the initial form of the constructor.
		this(imgRes, imgTitle, null, clickHandler, kbHook);
	}
	
	public MenuBarButton(ImageResource imgRes, String imgTitle, ClickHandler clickHandler) {
		// Always use the initial form of the constructor.
		this(imgRes, imgTitle, null, clickHandler, null);
	}
}
