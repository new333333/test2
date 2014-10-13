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
package org.kablink.teaming.gwt.client.mainmenu;

import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Class used to implement a button on the main menu bar that toggles
 * between two states.  
 * 
 * @author drfoster@novell.com
 */
public class MenuBarToggle extends VibeMenuItem {
	private MenuToggleSelector	m_mts;			// The MenuToggleSelector used by this MenuBarToggle.
	private TeamingEvents		m_altEvent;		// The alternate TeamingEvents.
	private TeamingEvents		m_baseEvent;	// The base      TeamingEvents.
	
	/*
	 * Inner class that implements selecting on a MenuBarToggle.
	 */
	private static class MenuToggleSelector implements Command {
		private String			m_altHTML;		// The alternate state's HTML.
		private String			m_baseHTML;		// The base      state's HTML.
		private String			m_currentHTML;	// The current   state's HTML.
		private TeamingEvents	m_altEvent;		// The alternate TeamingEvents.
		private TeamingEvents	m_baseEvent;	// The base      TeamingEvents.
		private VibeMenuItem	m_mi;			// The menu item this MenuToggleSelector is tied to.
		
		/**
		 * Constructor method.
		 */
		MenuToggleSelector(String baseHTML, TeamingEvents baseEvent, String altHTML, TeamingEvents altEvent) {
			// Initialize the super class...
			super();
			
			// ...store the parameters...
			m_baseHTML  = baseHTML;
			m_baseEvent = baseEvent;
			m_altHTML   = altHTML;
			m_altEvent  = altEvent;
			
			// ...and initialize everything else.
			m_currentHTML = baseHTML;
		}

		/**
		 * Called when the toggle is selected.
		 * 
		 * Implements the Command.execute() method.
		 */
		@Override
		public void execute() {
			// Fire the event...
			TeamingEvents event;
			if (m_currentHTML.equals(m_baseHTML))
			     event = m_baseEvent;
			else event = m_altEvent;
			EventHelper.fireSimpleEvent(event);
			
			// ...and toggle the state of the button.
			if (event == m_baseEvent)
			     event = m_altEvent;
			else event = m_baseEvent;
			setEventHTML(event);
		}
		
		/**
		 * Sets the menu item's HTML appropriate for an event.
		 * 
		 * @param event
		 */
		public void setEventHTML(TeamingEvents event) {
			String html;
			if (event == m_baseEvent)
			     html = m_baseHTML;
			else html = m_altHTML;
			m_currentHTML = html;
			m_mi.setHTML(html);
		}
		
		/**
		 * Stores the menu item associated with this MenuToggleSelector.
		 * 
		 * @param mi
		 */
		public void setMenuItem(VibeMenuItem mi) {
			m_mi = mi;
			m_mi.setHTML(m_currentHTML);
		}
	}

	
	/**
	 * Constructor method.
	 * 
	 * @param baseImgRes
	 * @param baseTitle
	 * @param baseEvent
	 * @param altImgRes
	 * @param altTitle
	 * @param altEvent
	 */
	public MenuBarToggle(String id, ImageResource baseImgRes, String baseTitle, TeamingEvents baseEvent, ImageResource altImgRes, String altTitle, TeamingEvents altEvent) {
		// Initialize the superclass...
		super(
			"",	// HTML is set below in the call to MenuToggleSelector.setMenuItem().
			new MenuToggleSelector(
				constructHTML(baseTitle, baseImgRes),
				baseEvent,
				constructHTML(altTitle, altImgRes),
				altEvent));

		// ...connect the parts together...
		m_mts = ((MenuToggleSelector) getScheduledCommand());
		m_mts.setMenuItem(this);
		
		// ...store the parameters...
		m_baseEvent = baseEvent;
		m_altEvent  = altEvent;
		
		// ...and setup the MenuBarToggle's styles.
		addStyleName("vibe-mainMenuButton_WidgetAnchor");
		getElement().setId(id);
	}

	/*
	 * Given the tile and resource for an image, constructs and returns
	 * the HTML to render that image.
	 */
	private static String constructHTML(String imgTitle, ImageResource imgRes) {
		// Create the Image...
		Image img = new Image(imgRes);
		img.setTitle(imgTitle);
		img.addStyleName("vibe-mainMenuButton_WidgetImage");

		// ...add to something to extract it's HTML from...
		FlowPanel fp = new FlowPanel();
		fp.add(img);

		// ...and return it's HTML.
		return fp.getElement().getInnerHTML();
	}
	
	/**
	 * Set the image for this menu bar toggle.
	 * 
	 * @param event
	 */
	public void setImage(TeamingEvents event) {
		m_mts.setEventHTML(event);
	}
		
	/**
	 * Set the state of this toggle to the given event.
	 * 
	 * @param event
	 */
	public void setState(TeamingEvents event) {
		// Make sure we're based on a valid state...
		if ((event != m_baseEvent) && (event != m_altEvent)) {
			Window.alert("MenuBarToggle.setState( Invalid State ):  " + event.name());
			return;
		}

		// ...and put it into effect.
		setImage(event);
	}
}
