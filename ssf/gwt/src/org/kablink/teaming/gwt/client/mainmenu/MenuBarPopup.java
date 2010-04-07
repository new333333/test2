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
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.ActionTrigger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Class used for a menu item popup.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MenuBarPopup extends PopupPanel {
	protected ActionTrigger					m_actionTrigger;	// Used to trigger actions from the popup.
	protected GwtTeamingMainMenuImageBundle	m_images;			// The menu's images.
	protected GwtTeamingMessages			m_messages;			// The menu's messages.
	protected GwtRpcServiceAsync			m_rpcService;		//
	private   VerticalPanel					m_contentPanel;		// A VerticalPanel that will hold the popup's contents.
	
	/**
	 * Class constructor.
	 * 
	 * @param title
	 * @param left
	 * @param top
	 */
	public MenuBarPopup(ActionTrigger actionTrigger, String title, int left, int top) {
		// Construct the super class...
		super(true);

		// ...store the parameters...
		m_actionTrigger	= actionTrigger;
		m_images		= GwtTeaming.getMainMenuImageBundle();
		m_messages		= GwtTeaming.getMessages();
		m_rpcService	= GwtTeaming.getRpcService();
		
		// ...and initialize everything else.
		addStyleName("mainMenuPopup_Core");
		setAnimationEnabled(true);
//!		setAnimationType(PopupPanel.AnimationType.ROLL_DOWN);
		setPopupPosition(left, top);

		// ...create the popup's innards...
		DockPanel dp = new DockPanel();
		dp.addStyleName("mainMenuPopup");
		dp.add(createPopupTitleBar(title, m_images), DockPanel.NORTH);
		dp.add(createPopupContentPanel(),            DockPanel.CENTER);
		dp.add(createPopupBottom(m_images),          DockPanel.SOUTH);

		// ...and add it to the popup.
		setWidget(dp);
	}

	/**
	 * Adds a Widget to the content VerticalPanel.
	 * 
	 * @param contentWidget
	 */
	public void addContentWidget(Widget contentWidget) {
		// Simply add the widget to the content panel.
		m_contentPanel.add(contentWidget);
	}

	/*
	 * Creates the bottom of the popup.
	 */
	private FlowPanel createPopupBottom(GwtTeamingMainMenuImageBundle images) {
		FlowPanel bottomPanel = new FlowPanel();
		bottomPanel.addStyleName("mainMenuPopup_Bottom");
		Image bottomImg = new Image(images.spacer1px());
		bottomImg.setHeight("4px");
		bottomImg.setWidth("4px");
		bottomPanel.add(bottomImg);
		return bottomPanel;
	}
	
	/*
	 * Creates the main content panel for the popup.
	 */
	private VerticalPanel createPopupContentPanel() {
		m_contentPanel = new VerticalPanel();
		return m_contentPanel;
	}
	
	/*
	 * Creates the title bar for the popup.
	 */
	private FlowPanel createPopupTitleBar(String title, GwtTeamingMainMenuImageBundle images) {
		// Create the title bar's panel...
		FlowPanel panel = new FlowPanel();
		panel.addStyleName("mainMenuPopup_TitleBar");

		// ...add the title text...
		Label titleLabel = new Label(title);
		titleLabel.addStyleName("mainMenuPopup_TitleBarText");
		panel.add(titleLabel);

		// ...add a close Anchor...
		Anchor closeA = new Anchor();
		closeA.addStyleName("mainMenuPopup_TitleBarCloseA");
		Image closeImg = new Image(images.closeCircle16());
		closeImg.addStyleName("mainMenuPopup_TitleBarCloseImg");
		closeA.getElement().appendChild(closeImg.getElement());
		closeA.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		panel.add(closeA);
		
		// ...and return the panel.
		return panel;
	}
}
