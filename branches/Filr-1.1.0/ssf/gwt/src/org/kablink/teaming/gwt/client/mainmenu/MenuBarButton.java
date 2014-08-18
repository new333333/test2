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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.VibeEventBase;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Class used to implement a button on the main menu bar.  
 * 
 * @author drfoster@novell.com
 */
public class MenuBarButton extends VibeMenuItem {
	/*
	 * Inner class that implements selecting a button on the menu.
	 */
	private static class MenuButtonSelector implements Command {
		private VibeEventBase<?>	m_event;	// The event to fire when the button is selected.

		/**
		 * Constructor method.
		 * 
		 * @param event
		 */
		public MenuButtonSelector(VibeEventBase<?> event) {
			super();
			m_event = event;
		}
		
		/**
		 * Called when the button is selected.
		 * 
		 * Implements the Command.execute() method.
		 */
		@Override
		public void execute() {
			GwtTeaming.fireEvent(m_event);
		}
	}

	/*
	 * Constructor method.
	 */
	private MenuBarButton(String id, ImageResource imgRes, String imgTitle, VibeEventBase<?> event, Command command) {
		// Initialize the super class...
		super(
			"",	// Place holder.  Actual HTML supplied later.
			((null == command)                ?
				new MenuButtonSelector(event) :
				command));
		
		// ...set item's styles...
		addStyleName("vibe-mainMenuButton_WidgetAnchor");
		
		// ...create the Image...
		Image img = new Image(imgRes);
		img.setTitle(imgTitle);
		img.addStyleName("vibe-mainMenuButton_WidgetImage");
		
		// ...and tie things together.
		FlowPanel imgPanel = new FlowPanel();
		imgPanel.getElement().appendChild(img.getElement());
		setHTML(imgPanel.getElement().getInnerHTML());
		getElement().setId(id);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param id
	 * @param imgRes
	 * @param imgTitle
	 * @param event
	 */
	public MenuBarButton(String id, ImageResource imgRes, String imgTitle, VibeEventBase<?> event) {
		// Always use the private form of the constructor.
		this(id, imgRes, imgTitle, event, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param imgRes
	 * @param imgTitle
	 * @param command
	 */
	public MenuBarButton(String id, ImageResource imgRes, String imgTitle, Command command) {
		// Always use the private form of the constructor.
		this(id, imgRes, imgTitle, null, command);
	}
}
