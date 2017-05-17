/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.MenuBar;

/**
 * Class used to contain items on the main menu bar.  
 * 
 * @author drfoster@novell.com
 */
public class MenuBarBox extends VibeMenuItem {
	/**
	 * Constructor method.
	 *
	 * @param boxId
	 * @param itemImgRes
	 * @param itemText
	 * @param cmd
	 */
	public MenuBarBox(String boxId, ImageResource itemImgRes, String itemText, Command cmd) {
		// Initialize the super class...
		super("", cmd);
		
		// ...and initialize everything else.
		initBox(boxId, itemImgRes, itemText, false);
	}
	
	/**
	 * Constructor method.
	 *
	 * @param boxId
	 * @param itemImgRes
	 * @param itemText
	 * @param subMenu
	 */
	public MenuBarBox(String boxId, ImageResource itemImgRes, String itemText, MenuBar subMenu) {
		// Initialize the super class...
		super("", subMenu);
		
		// ...and initialize everything else.
		initBox(boxId, itemImgRes, itemText, true);
	}
	
	/**
	 * Constructor method.
	 *
	 * @param boxId
	 * @param itemText
	 * @param subMenu
	 */
	public MenuBarBox(String boxId, String itemText, MenuBar subMenu) {
		// Always use one of the initial forms of the constructor.
		this(boxId, null, itemText, subMenu);
	}
	
	/**
	 * Constructor method.
	 *
	 * @param boxId
	 * @param itemText
	 */
	public MenuBarBox(String boxId, String itemText) {
		// Always use one of the initial forms of the constructor.
		this(
			boxId,
			null,
			itemText,
			buildNoopCommand());	// Place holder.  Actual command will be supplied later.
	}
	
	/**
	 * Constructor method.
	 *
	 * @param boxId
	 * @param itemImgRes
	 * @param itemText
	 */
	public MenuBarBox(String boxId, ImageResource itemImgRes, String itemText) {
		// Always use one of the initial forms of the constructor.
		this(
			boxId,
			itemImgRes,
			itemText,
			buildNoopCommand());	// Place holder.  Actual command will be supplied later.
	}

	/*
	 * Returns a Command that does nothing.
	 */
	private static Command buildNoopCommand() {
		return
			new Command() {
				@Override
				public void execute() {
					// Do nothing.
				}
		};
	}
	
	/*
	 * Completes the initialization of a MenuBarBox.
	 */
	private void initBox(String boxId, ImageResource itemImgRes, String itemText, boolean dropdown) {
		// Add the ID to the box.
		getElement().setId(boxId);
		
		// Add the base style to the box.
		addStyleName("vibe-mainMenuContent");

		// If we need an image for the box...
		FlowPanel boxPanel = new FlowPanel();
		if (null != itemImgRes) {
			// ...add it...
			Image itemImg = new Image(itemImgRes);
			itemImg.addStyleName("vibe-mainMenuBar_BoxImg");
			if (!(GwtClientHelper.jsIsIE())) {
				itemImg.addStyleName("vibe-mainMenuBar_BoxImgNonIE");
			}
			boxPanel.add(itemImg);
		}

		// ...add the label for the box...
		InlineLabel itemLabel = new InlineLabel(itemText);
		itemLabel.addStyleName("vibe-mainMenuBar_BoxText");
		boxPanel.add(itemLabel);

		// ...if we need a drop down image for the box...
		if (dropdown) {
			// ...add it...
			Image dropDownImg = new Image(GwtTeaming.getMainMenuImageBundle().menuArrow());
			dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImg");
			if (!(GwtClientHelper.jsIsIE())) {
				dropDownImg.addStyleName("vibe-mainMenuBar_BoxDropDownImgNonIE");
			}
			boxPanel.add(dropDownImg);
		}

		// ...and finally, set the HTML for the MenuItem.
		setHTML(boxPanel.getElement().getInnerHTML());
	}
	
	/**
	 * Returns the menu bar box's absolute bottom position.
	 * 
	 * @return
	 */
	public int getBoxBottom() {
		return getElement().getAbsoluteBottom();
	}
	
	/**
	 * Returns the menu bar box's absolute left position.
	 * 
	 * @return
	 */
	public int getBoxLeft () {
		return getAbsoluteLeft();
	}

	/**
	 * Sets a MenuBarBox's visibility state.
	 * 
	 * When being hidden, they are disabled so that menu item is
	 * removed from the tab order.
	 * 
	 * Overrides the MenuItem.setVisible() method.
	 */
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		setEnabled(visible);
	}
}
