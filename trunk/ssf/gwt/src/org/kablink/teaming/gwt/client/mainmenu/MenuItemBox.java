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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;



/**
 * Class used to contain items on the main menu bar.  
 * 
 * @author drfoster@novell.com
 *
 */
public class MenuItemBox extends FlowPanel {
	private Anchor m_boxA;
	
	/**
	 * Class constructor.
	 *
	 * @param boxId
	 * @param itemImgRes
	 * @param itemText
	 * @param dropdown
	 * @param ch
	 */
	public MenuItemBox(String boxId, ImageResource itemImgRes, String itemText, boolean dropdown) {
		// Initialize the FlowPanel super class...
		super();
		addStyleName("mainMenuContent");

		// ...create an Anchor to contain the box...
		m_boxA = new Anchor();
		m_boxA.addStyleName("mainMenuItem_BoxA");

		// ...create a FlowPanel to contain the items in the box...
		FlowPanel boxPanel = new FlowPanel();
		boxPanel.getElement().setId(boxId);
		boxPanel.addStyleName("mainMenuItem_BoxPanel");

		// ...add mouse over handling on the panel...
		MenuItemIDHover hover = new MenuItemIDHover(boxId, "mainMenuItem_BoxHover");
		m_boxA.addMouseOverHandler(hover);
		m_boxA.addMouseOutHandler( hover);

		// ...if we need an image for the box...
		if (null != itemImgRes) {
			// ...add it...
			Image itemImg = new Image(itemImgRes);
			itemImg.addStyleName("mainMenuItem_BoxImg");
			boxPanel.add(itemImg);
		}

		// ...add the label for the box...
		Label itemLabel = new Label(itemText);
		itemLabel.addStyleName("mainMenuItem_BoxText");
		boxPanel.add(itemLabel);

		// ...if we need a drop down image for the box...
		if (dropdown) {
			// ...add it...
			Image dropDownImg = new Image(GwtTeaming.getMainMenuImageBundle().menu_9());
			dropDownImg.addStyleName("mainMenuItem_BoxDropDownImg");
			boxPanel.add(dropDownImg);
		}

		// ...and finally, add the panel to the Anchor and the Anchor
		// ...to the box.
		m_boxA.getElement().appendChild(boxPanel.getElement());
		add(m_boxA);
	}
	
	public MenuItemBox(String boxId, String itemText, boolean dropdown) {
		// Always use the initial form of the constructor.
		this(boxId, null, itemText, dropdown);
	}
	
	public MenuItemBox(String boxId, String itemText) {
		// Always use the initial form of the constructor.
		this(boxId, null, itemText, false);
	}
	
	public MenuItemBox(String boxId, ImageResource itemImgRes, String itemText) {
		// Always use the initial form of the constructor.
		this(boxId, itemImgRes, itemText, false);
	}

	/**
	 * Adds a ClickHandler to the MenuItemBox's Anchor.
	 * 
	 * @param ch
	 */
	public void addClickHandler(ClickHandler ch) {
		m_boxA.addClickHandler(ch);
	}
}
