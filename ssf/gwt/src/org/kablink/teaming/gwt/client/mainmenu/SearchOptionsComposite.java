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
import org.kablink.teaming.gwt.client.util.ActionTrigger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * Class used for the content of the additional search options.  
 * 
 * @author drfoster@novell.com
 */
public class SearchOptionsComposite extends Composite {
	@SuppressWarnings("unused")
	private ActionTrigger m_actionTrigger;
	private FlowPanel m_mainPanel = new FlowPanel();
	private GwtTeamingMainMenuImageBundle m_images;
	private GwtTeamingMessages m_messages;
	private PopupPanel m_searchOptionsPopup;
	
	public SearchOptionsComposite(PopupPanel searchOptionsPopup, ActionTrigger actionTrigger) {
		// Store the parameter...
		m_searchOptionsPopup = searchOptionsPopup;
		m_actionTrigger = actionTrigger;

		// ...and initialize everything else.
		m_images = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();

		// Create the composite's content.
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName("searchOptionsDlg_Content");
		addHeader();
		addContent();
		
		// All composites must call initWidget() in their constructors.
		initWidget(m_mainPanel);
	}

	/*
	 * Adds the content to the main panel.
	 */
	private void addContent() {
//!		...this needs to be implemented...
		m_mainPanel.add(new InlineLabel("...this needs to be implemented..."));
	}

	/*
	 * Adds the header to the main panel.
	 */
	private void addHeader() {
		// Add the close push button...
		m_mainPanel.add(createCloser());
		
		// ...and the header text. 
		FlowPanel headerPanel = new FlowPanel();
		headerPanel.addStyleName("searchOptionsDlg_Header");
		headerPanel.add(new InlineLabel(m_messages.mainMenuSearchOptionsHeader()));
		m_mainPanel.add(headerPanel);
	}
	
	/*
	 * Creates the close push button.
	 */
	private FlowPanel createCloser() {
		// Create the panel...
		FlowPanel panel = new FlowPanel();
		
		// ...create the Image...
		Image img = new Image(m_images.closeX());
		img.addStyleName("searchOptionsDlg_CloseImg");
		img.setTitle(m_messages.mainMenuSearchOptionsCloseAlt());
		
		// ...create the Anchor...
		Anchor a = new Anchor();
		a.addStyleName("searchOptionsDlg_CloseA");
		a.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				m_searchOptionsPopup.hide();
			}
		});
		
		// ...tie things together...
		a.getElement().appendChild(img.getElement());
		
		// ...and add the Anchor to the panel and return it.
		panel.add(a);
		return panel;
	}
}
