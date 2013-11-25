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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.SearchSimpleEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;


/**
 * Class used for the search widgets on the main menu bar.  
 * 
 * @author drfoster@novell.com
 */
public class SearchMenuPanel extends FlowPanel {
	private GwtTeamingMainMenuImageBundle m_images;
	private GwtTeamingMessages m_messages;
	private TextBox m_searchInput;

	/**
	 * Class constructor.
	 */
	public SearchMenuPanel() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_images = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();
		addStyleName("mainMenuBar_BoxPanel mainMenuSearch_Panel");

		// Finally, add the search and button widgets.
		addSearchWidget();
		addSearchButton();
	}

	/*
	 * Adds the search button to the search panel.
	 */
	private void addSearchButton() {
		final SearchMenuPanel searchMenu = this;
		
		// Create the Image for the button...
		Image img = new Image(m_images.searchGlass());
		img.setTitle(m_messages.mainMenuSearchButtonAlt());
		img.addStyleName("mainMenuSearch_ButtonImage");
		
		// ...create the Anchor for it...
		Anchor searchAnchor = new Anchor();
		searchAnchor.getElement().appendChild(img.getElement());
		searchAnchor.addStyleName("mainMenuSearch_ButtonAnchor");
		searchAnchor.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Turn off any over indicator on the menu bar item and
				// perform the search.
				searchMenu.getElement().removeClassName("subhead-control-bg2");
				doSearch();
			}
		});
		
		// ...add mouse over handling...
		MenuHoverByWidget hover = new MenuHoverByWidget(searchMenu, "subhead-control-bg2");
		searchAnchor.addMouseOverHandler(hover);
		searchAnchor.addMouseOutHandler( hover);
		
		// ...and add the Anchor to the panel.
		add(searchAnchor);
	}
	
	/*
	 * Adds the search widget to the search panel.
	 */
	private void addSearchWidget() {
		// Create the TextBox for the search...
		m_searchInput = new TextBox();
		m_searchInput.addStyleName("mainMenuSearch_Input");
		
		// ...add a handler to intercept key presses...
		m_searchInput.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				// Is this the enter key being pressed?
				int key = event.getNativeEvent().getKeyCode();
				if ( KeyCodes.KEY_ENTER == key ) {
					// Yes!  Perform the search.
					doSearch();
				}
			}
		});
		
		// ...and add the Anchor to the panel.
		add(m_searchInput);
	}
	
	/*
	 * Performs a search on the search widget's contents.
	 */
	private void doSearch() {
		String searchFor = m_searchInput.getValue();
		m_searchInput.setValue("");
		GwtTeaming.fireEvent(new SearchSimpleEvent(searchFor));
	}
}
