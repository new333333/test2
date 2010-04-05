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

import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * Class used to implement a menu button that toggles between two
 * states.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MenuBarButton extends Anchor {
	private ActionTrigger	m_actionTrigger;	// The interface to trigger TeamingAction's through.
	private Object			m_actionObject;		// The Object to send with the TeamingAction.
	private TeamingAction	m_action;			// The TeamingAction to trigger when the button is clicked.
	
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
			// Fire the action.
			m_actionTrigger.triggerAction(m_action, m_actionObject);
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 * @param hoverWidget
	 * @param imgRes
	 * @param imgTitle
	 * @param action
	 */
	public MenuBarButton(ActionTrigger actionTrigger, Widget hoverWidget, ImageResource imgRes, String imgTitle, TeamingAction action, Object actionObject) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_actionTrigger	= actionTrigger;
		m_action		= action;
		m_actionObject	= actionObject;
		
		// Create the Image...
		Image img = new Image(imgRes);
		img.setTitle(imgTitle);
		img.addStyleName("mainMenuButton_WidgetImage");
		
		// ...create the Anchor...
		addStyleName("mainMenuButton_WidgetAnchor");
		
		// ...tie things together...
		getElement().appendChild(img.getElement());
		addClickHandler(new MenuButtonSelector());
		
		// ...add mouse over handling...
		MenuHoverByWidget hover = new MenuHoverByWidget(hoverWidget, "subhead-control-bg2");
		addMouseOverHandler(hover);
		addMouseOutHandler( hover);
	}
	
	public MenuBarButton(ActionTrigger actionTrigger, FlowPanel hostPanel, ImageResource imgRes, String imgTitle, TeamingAction action) {
		// Always use the other form of the constructor.
		this(actionTrigger, hostPanel, imgRes, imgTitle, action, null);
	}
}
