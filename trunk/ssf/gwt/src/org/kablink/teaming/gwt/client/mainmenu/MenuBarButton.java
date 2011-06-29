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
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;


/**
 * Class used to implement a menu button that toggles between two
 * states.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MenuBarButton extends Anchor {
	private ActionTrigger	m_actionTrigger;	// The interface to trigger TeamingAction's through.
	private GwtEvent<?>		m_event;			// The event to fire when the button is clicked.
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
			// If we have an event...
			if (null != m_event) {
				// ...fire it...
				GwtTeaming.fireEvent(m_event);
			}
			
			else {
				// ...otherwise, fire the action.
				m_actionTrigger.triggerAction(m_action, m_actionObject);
			}
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 * @param imgRes
	 * @param imgTitle
	 * @param action
	 * @param actionObject
	 * @param clickHandler
	 */
	public MenuBarButton(ActionTrigger actionTrigger, ImageResource imgRes, String imgTitle, TeamingAction action, Object actionObject, GwtEvent<?> event, ClickHandler clickHandler) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_actionTrigger	= actionTrigger;
		m_action		= action;
		m_actionObject	= actionObject;
		m_event			= event;
		
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
	
	public MenuBarButton(ActionTrigger actionTrigger, ImageResource imgRes, String imgTitle, TeamingAction action, Object actionObject) {
		// Always use the initial form of the constructor.
		this(actionTrigger, imgRes, imgTitle, action, actionObject, null, null);
	}
	
	public MenuBarButton(ActionTrigger actionTrigger, ImageResource imgRes, String imgTitle, TeamingAction action) {
		// Always use the initial form of the constructor.
		this(actionTrigger, imgRes, imgTitle, action, null, null, null);
	}
	
	public MenuBarButton(ImageResource imgRes, String imgTitle, GwtEvent<?> event) {
		// Always use the initial form of the constructor.
		this(null, imgRes, imgTitle, null, null, event, null);
	}
	
	public MenuBarButton(ImageResource imgRes, String imgTitle, ClickHandler clickHandler) {
		// Always use the initial form of the constructor.
		this(null, imgRes, imgTitle, null, null, null, clickHandler);
	}

	/**
	 * Sets an Object for the teaming action.
	 * 
	 * @param actionObject
	 */
	public void setActionObject(Object actionObject) {
		// Simply store the parameter.
		m_actionObject = actionObject;
	}
}
