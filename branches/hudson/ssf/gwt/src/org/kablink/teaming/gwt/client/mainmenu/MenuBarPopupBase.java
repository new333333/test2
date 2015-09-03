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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.ContextBinderProvider;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Abstract base class used for a menu item popups.  
 * 
 * @author drfoster@novell.com
 */
public abstract class MenuBarPopupBase {
	private   ContextBinderProvider			m_binderProvider;	// Provides the current binder context, as required.
	protected GwtTeamingMainMenuImageBundle	m_images;			// Vibe's image  resource.
	protected GwtTeamingMessages			m_messages;			// Vibe's string resources.
	protected GwtRpcServiceAsync			m_rpcService;		// Vibe's RPC service.
	private   MenuBarBox					m_menuBox;			// The box wrapping the menu item that invokes this popup.
	private   String						m_scrollCSSAdded;	// ScrollBar CSS class added to the main <BODY> while the menu is attached.
	private   VibeMenuBar					m_menuBar;			// The menu bar containing this popup's menu items.
	
	/**
	 * Constructor method.
	 * 
	 * @param binderProvider
	 */
	public MenuBarPopupBase(ContextBinderProvider binderProvider) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_binderProvider = binderProvider;

		// ...and initialize everything else.
		m_images     = GwtTeaming.getMainMenuImageBundle();
		m_messages   = GwtTeaming.getMessages();
		m_rpcService = GwtTeaming.getRpcService();
		
		// Create a menu bar to hold the popup menu items...
		m_menuBar = new VibeMenuBar(true, "vibe-mainMenuPopup");
		m_menuBar.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached())
					 onAttach();
				else onDetach();
			}
		});
	}

	/**
	 * Adds a VibeMenuItem to the menu.
	 * 
	 * @param mi
	 */
	final public void addContentMenuItem(VibeMenuItem mi) {
		// Simply add the item to the menu.
		m_menuBar.addItem(mi);
	}

	/**
	 * Adds a context based toolbar item to the menu.
	 * 
	 * @param idBase
	 * @param tbi
	 * @param addEntryView
	 */
	final public void addContextMenuItem(String idBase, ToolbarItem tbi, boolean hideEntryView) {
		// If we have a ToolbarItem...
		if (null != tbi) {
			// ...and if we can generate a menu item from it... 
			ContextMenuItem cmi = new ContextMenuItem(this, idBase, tbi, hideEntryView);
			if (null != cmi) {
				// ...add it to the popup.
				addContentMenuItem(cmi);
			}
		}
	}
	
	final public void addContextMenuItem(String idBase, ToolbarItem tbi) {
		// Always use the initial form of the method.
		addContextMenuItem(idBase, tbi, false);	// false -> Don't hide an entry view.
	}

	/**
	 * Adds a collection of context based toolbar items to the menu.
	 * 
	 * @param idBase
	 * @param tbi
	 */
	final public void addContextMenuItemsFromList(String idBase, List<ToolbarItem> tbiList) {
		// If there aren't any items...
		if ((null == tbiList) || tbiList.isEmpty()) {
			// ...bail.
			return;
		}

		// Scan the items...
		for (Iterator<ToolbarItem> niIT = tbiList.iterator(); niIT.hasNext(); ) {
			// ...adding each to the menu.
			ToolbarItem tbi = niIT.next();
			addContextMenuItem(idBase, tbi);
		}
	}
	
	/**
	 * Adds a collection of nested context based toolbar items to the
	 * menu.
	 * 
	 * @param idBase
	 * @param tbi
	 */
	final public void addNestedContextMenuItems(String idBase, ToolbarItem tbi) {
		List<ToolbarItem> niList = ((null == tbi) ? null : tbi.getNestedItemsList());
		addContextMenuItemsFromList(idBase, niList);
	}
	
	/**
	 * Adds a spacer to the menu.
	 */
	final public void addSpacerMenuItem() {
		m_menuBar.addSeparator();
	}

	/**
	 * Clears the contents of the menu.
	 */
	final public void clearItems() {
		m_menuBar.clearItems();
	}

	/**
	 * Returns a count of the MenuItem's in this popup.
	 * 
	 * @return
	 */
	final public int getItemCount() {
		return m_menuBar.getItemCount();
	}

	/**
	 * Returns the index of a MenuItem in the menu.
	 * 
	 * @param mi
	 * 
	 * @return
	 */
	final public int getItemIndex(MenuItem mi) {
		return m_menuBar.getItemIndex(mi);
	}
	
	/**
	 * Returns the menu associated with this MenuBarPopupBase.
	 * 
	 * @return
	 */
	final public VibeMenuBar getMenuBar() {
		return m_menuBar;
	}

	/**
	 * Returns the X position to use to position something relative to
	 * the bottom of this menu.
	 * 
	 * @return
	 */
	final public int getRelativeX() {
		return ((null == m_menuBox) ? 0 : m_menuBox.getBoxLeft());
	}
	
	/**
	 * Returns the Y position to use to position something relative to
	 * the bottom of this menu.
	 * 
	 * @return
	 */
	final public int getRelativeY() {
		return ((null == m_menuBox) ? 0 : m_menuBox.getBoxBottom());
	}
	
	/**
	 * Returns true if the menu has content and false otherwise.
	 * 
	 * @return
	 */
	final public boolean hasContent() {
		return m_menuBar.hasContent();
	}

	/**
	 * Returns false if the last item added was a spacer and true
	 * otherwise.
	 * 
	 * @return
	 */
	final public boolean isSpacerNeeded() {
		return m_menuBar.isSpacerNeeded();
	}

	/**
	 * Called when the popup is shown. 
	 * 
	 * Classes that extend this class may override this to do something
	 * they require.
	 */
	public void onAttach() {
		setCurrentBinder(m_binderProvider.getContextBinder());
		populateMenu();
		
		// If needed, add CSS to the  main <BODY> so that it scrolls to
		// handle long popups.
		m_scrollCSSAdded = GwtClientHelper.scrollUIForPopup(null);	// null -> Styles are not automatically removed.  They are removed manually in MainMenuControl.
	}
	
	/**
	 * Called when the menu popup is closed.
	 * 
	 * Classes that extend this class may override this to do something
	 * they require.
	 */
	public void onDetach() {
		// If we added ScrollBar CSS to the main <BODY>...
		if (GwtClientHelper.hasString(m_scrollCSSAdded)) {
			// ...remove it.
			RootPanel.getBodyElement().removeClassName(m_scrollCSSAdded);
			m_scrollCSSAdded = null;
		}
	}
	
	/**
	 * Classes that extend do what needs to be done to populate their
	 * menu.
	 */
	public abstract void populateMenu();
	
	/**
	 * Passes a BinderInfo describing the currently selected binder to
	 * classes that extend this.
	 * 
	 * @param binderInfo
	 */
	public abstract void setCurrentBinder(BinderInfo binderInfo);

	/**
	 * Stores the MenuBarBox associated with this menu.
	 * 
	 * @param menuBox
	 */
	final public void setMenuBox(MenuBarBox menuBox) {
		m_menuBox = menuBox;
	}
	
	/**
	 * Passes information about the context based toolbar requirements
	 * via a List<ToolbarItem> to classes that extend this.
	 * 
	 * Not used for non-context based menus (My Teams, Favorites, ...)
	 * 
	 * @param toolbarItemList
	 */
	public abstract void setToolbarItemList(List<ToolbarItem> toolbarItemList);
	
	/**
	 * Called to determine if the menu should be shown.
	 * 
	 * Not used for non-context based menus (My Teams, Favorites, ...)
	 * 
	 * @return
	 */
	public abstract boolean shouldShowMenu();
}
