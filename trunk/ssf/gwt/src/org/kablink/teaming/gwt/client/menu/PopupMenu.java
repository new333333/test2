/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.menu;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuBar;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * This is the menu that holds the actions that can be taken with an
 * item in the Activity Stream control.  I.e., Reply, Share, Tag,...
 * 
 * @author drfoster@novell.com
 */
public class PopupMenu extends TeamingPopupPanel implements BlurHandler {
	private boolean		m_canHaveCheckedMenuItems;	// Can this pop-up menu have menu items that are checked?
	private VibeMenuBar	m_menu;						//

	/**
	 * Constructor method.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param canHaveCheckedMenuItems
	 */
	public PopupMenu(boolean autoHide, boolean modal, boolean canHaveCheckedMenuItems) {
		// Initialize the super class...
		super(autoHide, modal);
		
		// ...and store the parameters that need to be saved.
		m_canHaveCheckedMenuItems = canHaveCheckedMenuItems;
		
		// We need to replace gwt-PopupPanel style name because it is
		// causing an empty box to be displayed because initially this
		// control's width and height are 0.
		addStyleName("vibe-popupMenu");

		// Finally, add a blank menu so something is there.
		setMenu(new VibeMenuBar(true, "vibe-mainMenuPopup"));
	}
	

	/**
	 * Adds a menu item to the menu. 
	 * 
	 * @param event
	 * @param img
	 * @param text
	 */
	public VibeMenuItem addMenuItem(final VibeEventBase<?> event, Image img, String text) {
		Command cmd = new Command() {
			@Override
			public void execute() {
				// No longer need to hide the menu as that's now done
				// in the onBlur() handler.
				GwtTeaming.fireEvent(event);
			}
		};

		VibeMenuItem reply = new VibeMenuItem(cmd, event, img, text, "vibe-mainMenuPopup_Item", m_canHaveCheckedMenuItems);
		m_menu.addItem(reply);

		return reply;
	}
	
	/**
	 * Adds a menu item to the menu. 
	 * 
	 * @param cmd
	 * @param img
	 * @param text
	 */
	public VibeMenuItem addMenuItem(final Command cmd, Image img, String text) {
		Command miCmd = new Command() {
			@Override
			public void execute() {
				// No longer need to hide the menu as that's now done
				// in the onBlur() handler.
				cmd.execute();
			};
		};
		
		VibeMenuItem reply = new VibeMenuItem(miCmd, null, img, text, "vibe-mainMenuPopup_Item", m_canHaveCheckedMenuItems);
		m_menu.addItem(reply);
		
		return reply;
	}

	/**
	 * Adds a menu item to the menu. 
	 *
	 * @param mi
	 */
	public void addMenuItem(MenuItem mi) {
		// No longer need to wrap the menu's Command in another Command
		// to hide the menu.  That's now done in the onBlur() method.
		m_menu.addItem(mi);
	}

	/**
	 * Adds a list of MenuItem's to the menu.
	 * 
	 * @param miList
	 */
	public void addMenuItems(List<MenuItem> miList) {
		if (GwtClientHelper.hasItems(miList)) {
			for (MenuItem mi:  miList) {
				addMenuItem(mi);
			}
		}
	}
	
	/**
	 * Adds a separator to the menu. 
	 */
	public MenuItemSeparator addSeparator() {
		return m_menu.addSeparator();
	}

	/**
	 * Called when the popup's menu looses the focus.  Typically, this
	 * is when a command in the menu is activated.
	 * 
	 * Implements the BlurHandler.onBlur() method.
	 */
	@Override
	public void onBlur(BlurEvent event) {
		// Simply hide the popup.
		GwtClientHelper.deferCommand(new Command() {
			@Override
			public void execute() {
				hide();
			}
		});
	}
	
	/**
	 * Remove the given menu item from the menu.
	 * 
	 * @param mi
	 */
	public void removeMenuItem(MenuItem mi) {
		m_menu.removeItem(mi);
	}

	/**
	 * Sets/Replaces the VibeMenuBar in the Popup menu.
	 * 
	 * @param menu
	 */
	public void setMenu(VibeMenuBar menu) {
		// Replace the menu...
		m_menu = menu;
		setWidget(m_menu);
		
		if (null != m_menu) {
			m_menu.addDomHandler(this, BlurEvent.getType());
			
			// No longer need to wrap the MenuItem Command's in another
			// Command to hide the menu.  That's now done in the
			// onBlur() method.
		}
	}
	
	/*
	 * Asynchronously gives the menu the focus.
	 */
	private void setMenuFocusAsync() {
		GwtClientHelper.deferCommand(new Command() {
			@Override
			public void execute() {
				setMenuFocusNow();
			}
		});
	}
	
	/*
	 * Synchronously gives the menu the focus.
	 */
	private void setMenuFocusNow() {
		// Give the menu bar the focus.
		m_menu.focus();
	}
	
	/**
	 * Set's a menu item's checked state.
	 * 
	 * @param mi
	 * @param checked
	 */
	public void setMenuItemCheckedState(VibeMenuItem mi, boolean checked) {
		mi.setCheckedState(checked);
	}

	/**
	 * Shows the popup menu.
	 */
	@Override
	public void show() {
		// Tell the super class to show the menu...
		super.show();
		
		// ...and add vertical scrolling to the main frame for the
		// ...duration of the popup.
		GwtClientHelper.scrollUIForPopup(this);
	}	

	/**
	 * Shows the popup menu at the given location.
	 * 
	 * @param x
	 * @param y
	 */
	public void showMenu(final int x, final int y) {
		// Create a callback that will be called when this menu is shown.
		PopupPanel.PositionCallback posCallback = new PopupPanel.PositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
			    int left = x;
			    int top  = y;
			    if (top > Window.getClientHeight()) {
			    	top = Window.getClientHeight();
			    }
			    
				if ((left + offsetWidth) > Window.getClientWidth()) {
					left = (Window.getClientWidth() - offsetWidth - 25);
				}
				
			    int windowTop    = Window.getScrollTop();
			    int windowBottom = (windowTop + Window.getClientHeight());

			    // Calculate how far over the bottom 
				if ((top + offsetHeight) > windowBottom) {
					top -= offsetHeight;
				}
				
				setPopupPosition(left, top);
				setMenuFocusAsync();
			}
		};
		setPopupPositionAndShow(posCallback);
	}
	
	/**
	 * Shows the popup menu relative to a UIObject.
	 * 
	 * @param target
	 */
	public void showRelativeToTarget(final UIObject target) {
		showRelativeTo(target);
		setMenuFocusAsync();
	}
	
	public void showRelativeToTarget(final Element target) {
		// Always use the initial form of the method.
		showRelativeToTarget(GwtClientHelper.getUIObjectFromElement(target));
	}
}
