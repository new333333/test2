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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.MenuIds;
import org.kablink.teaming.gwt.client.event.SearchSimpleEvent;
import org.kablink.teaming.gwt.client.util.EventWrapper;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Class used for the search widgets on the main menu bar.  
 * 
 * @author drfoster@novell.com
 */
public class SearchMenuPanel extends FlowPanel {
	private boolean							m_searchEmpty = true;	//
	private GwtTeamingMainMenuImageBundle	m_images;				//
	private GwtTeamingMessages				m_messages;				//
	private String							m_emptyTxt;				//
	private TextBox							m_searchInput;			//

	/**
	 * Class constructor.
	 */
	public SearchMenuPanel() {
		// Initialize the super class...
		super();
		
		// ...initialize everything else...
		m_images   = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();
		m_emptyTxt = m_messages.mainMenuSearchEmpty();
		addStyleName("vibe-mainMenuBar_BoxPanel vibe-mainMenuSearch_Panel");

		// ...and a add the search and button widgets.
		addSearchImage();
		addSearchWidget();
	}

	/*
	 * Adds the search image to the search panel.
	 */
	private void addSearchImage() {
		// Create the Image for the button...
		Image searchImg = new Image(m_images.searchGlass());
		searchImg.setTitle(m_messages.mainMenuSearchImageAlt());
		searchImg.addStyleName("vibe-mainMenuSearch_Image");

		// ...and add the Anchor to the panel.
		add(searchImg);
	}
	
	/*
	 * Adds the search widget to the search panel.
	 */
	private void addSearchWidget() {
		// Create the TextBox for the search...
		m_searchInput = new TextBox();
		m_searchInput.getElement().setId(MenuIds.MAIN_GLOBAL_SEARCH_INPUT);
		m_searchInput.setValue(m_emptyTxt);
		m_searchInput.addStyleName("vibe-mainMenuSearch_Input");
		setBlurStyles();
		
		// ...add a handler to intercept key presses...
		List<EventHandler> inputHandlers = new ArrayList<EventHandler>();
		inputHandlers.add(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				// Is this the enter key being pressed?
				int key = event.getNativeEvent().getKeyCode();
				if (KeyCodes.KEY_ENTER == key) {
					// Yes!  Perform the search.
					doSearch();
				}
			}
		});
		inputHandlers.add(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				// Set the appropriate styles on the input...
				setBlurStyles();

				// ...and if the search input is empty...
				m_searchEmpty = (0 == getSearchValue().length());
				if (m_searchEmpty) {
					// ...display an empty message in it.
					m_searchInput.setValue(m_emptyTxt);
				}
			}
		});
		inputHandlers.add(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				// Set the appropriate styles on the input...
				setFocusStyles();
				
				// ...and if the search input is empty...
				if (m_searchEmpty) {
					// ...remove any empty message from it.
					m_searchInput.setValue("");
				}
			}
		});
		EventWrapper.addHandlers(m_searchInput, inputHandlers);
		
		// ...and add the Anchor to the panel.
		add(m_searchInput);
	}
	
	/*
	 * Performs a search on the search widget's contents.
	 */
	private void doSearch() {
		String searchFor = getSearchValue();
		m_searchInput.setValue("");
		m_searchInput.setFocus(false);
		m_searchEmpty = true;
		m_searchInput.setValue(m_emptyTxt);
		setBlurStyles();
		GwtTeaming.fireEvent(new SearchSimpleEvent(searchFor));
	}
	
	/*
	 * Returns a non-null, non-space padded search value from the
	 * input widget.
	 */
	private String getSearchValue() {
		String reply = m_searchInput.getValue();
		if (null == reply)           reply = "";
		else if (0 < reply.length()) reply = reply.trim();
		return reply;
	}

	/*
	 * Sets the appropriate styles on the input widget for when
	 * it loses focus.
	 */
	private void setBlurStyles() {
		if (m_searchEmpty) {
			m_searchInput.removeStyleName("vibe-mainMenuSearch_InputFocus");
			m_searchInput.addStyleName(   "vibe-mainMenuSearch_InputBlur" );
		}
		
		else {
			setFocusStyles();
		}
	}
	
	/*
	 * Sets the appropriate styles on the input widget for when
	 * it gets focus.
	 */
	private void setFocusStyles() {
		m_searchInput.removeStyleName("vibe-mainMenuSearch_InputBlur" );
		m_searchInput.addStyleName(   "vibe-mainMenuSearch_InputFocus");
	}		
}
