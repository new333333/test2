/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;


/**
 * Abstract base class used for a menu item popups.  
 * 
 * @author drfoster@novell.com
 */
public abstract class MenuBarPopupBase extends TeamingPopupPanel {
	private		boolean							m_spacerNeeded;		// false -> The last item added was a spacer.  true -> It was something else.
	private		int								m_itemCount;		//
	protected	GwtTeamingMainMenuImageBundle	m_images;			// The menu's images.
	protected	GwtTeamingMessages				m_messages;			// The menu's messages.
	protected	GwtRpcServiceAsync				m_rpcService;		//
	private		MenuBarBox						m_menuBarBox;		// The menu bar box associated with a popup when opened.
	private		VibeMenuBar						m_menuBar;			//
	
	/**
	 * Class constructor.
	 * 
	 * @param title
	 */
	public MenuBarPopupBase(String title) {
		// Construct the super class...
		super(true);
		setGlassEnabled(true);
		setGlassStyleName("vibe-mainMenuPopup_Glass");

		// Change the styles to match a MenuBar popup instead of a
		// PopupPanel.
		removeStyleName("gwt-PopupPanel");
		addStyleName(   "gwt-MenuBarPopup");
		
		// Add a close handler to the popup so that it can restore the
		// menu bar box's styles when the popup menu is closed.
		addCloseHandler(new CloseHandler<PopupPanel>(){
			public void onClose(CloseEvent<PopupPanel> event) {
				removeAutoHidePartner(m_menuBarBox.getElement());
				m_menuBarBox.popupMenuClosed();
				m_menuBarBox = null;
			}});
		
		// ...and initialize everything else.
		m_images     = GwtTeaming.getMainMenuImageBundle();
		m_messages   = GwtTeaming.getMessages();
		m_rpcService = GwtTeaming.getRpcService();
		
		addStyleName("vibe-mainMenuPopup_Core");
		GwtClientHelper.oneWayCornerPopup(this);

		// Create a menu bar to hold the popup menu items...
		m_menuBar = new VibeMenuBar(true, "vibe-mainMenuPopup");

		// ...and lay it out in the popup like a standard GWT menu
		// ...lays out its popup...
		Grid grid = new Grid(3, 3);
		grid.setCellPadding(0);
		grid.setCellSpacing(0);
		CellFormatter cf = grid.getCellFormatter();
		RowFormatter  rf = grid.getRowFormatter();

		// ...first, the top row of the popup...
		rf.addStyleName(0, "menuPopupTop");
		FlowPanel cell = new FlowPanel();
		cell.addStyleName("menuPopupTopLeftInner");
		cf.addStyleName(0, 0, "menuPopupTopLeft");
		grid.setWidget(0, 0, cell);
		
		cell = new FlowPanel();
		cell.addStyleName("menuPopupTopCenterInner");
		cf.addStyleName(0, 1, "menuPopupTopCenter");
		grid.setWidget(0, 1, cell);
		
		cell = new FlowPanel();
		cell.addStyleName("menuPopupTopRightInner");
		cf.addStyleName(0, 2, "menuPopupTopRight");
		grid.setWidget(0, 2, cell);

		// ...then the middle (i.e., the content) row of the popup...
		rf.addStyleName(1, "menuPopupMiddle");
		cell = new FlowPanel();
		cell.addStyleName("menuPopupMiddleLeftInner");
		cf.addStyleName(1, 0, "menuPopupMiddleLeft");
		grid.setWidget(1, 0, cell);
		
		cell = new FlowPanel();
		cell.addStyleName("menuPopupMiddleCenterInner");
		cell.add(m_menuBar);
		cf.addStyleName(1, 1, "menuPopupMiddleCenter");
		grid.setWidget(1, 1, cell);
		
		cell = new FlowPanel();
		cell.addStyleName("menuPopupMiddleRightInner");
		cf.addStyleName(1, 2, "menuPopupMiddleRight");
		grid.setWidget(1, 2, cell);
		
		// ...and then the bottom row of the popup.
		rf.addStyleName(2, "menuPopupBottom");
		cell = new FlowPanel();
		cell.addStyleName("menuPopupBottomLeftInner");
		cf.addStyleName(2, 0, "menuPopupBottomLeft");
		grid.setWidget(2, 0, cell);
		
		cell = new FlowPanel();
		cell.addStyleName("menuPopupBottomCenterInner");
		cf.addStyleName(2, 1, "menuPopupBottomCenter");
		grid.setWidget(2, 1, cell);
		
		cell = new FlowPanel();
		cell.addStyleName("menuPopupBottomRightInner");
		cf.addStyleName(2, 2, "menuPopupBottomRight");
		grid.setWidget(2, 2, cell);

		// Finally, store the popup's Grid as the popup panel's widget.
		setWidget(grid);
	}

	/**
	 * Adds a VibeMenuItem to the menu.
	 * 
	 * @param mi
	 */
	final public void addContentMenuItem(VibeMenuItem mi) {
		// Simply add the item to the menu.
		m_itemCount += 1;
		m_menuBar.addItem(mi);
		m_spacerNeeded = true;
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
		// Always use the initial form of the method, defaulting to not
		// hiding an entry view.
		addContextMenuItem(idBase, tbi, false);
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
	 * Adds a spacer line to the menu.
	 */
	final public void addSpacerMenuItem() {
		m_menuBar.addSeparator();
		m_spacerNeeded = false;
	}
	
	public void clearItems() {
		m_itemCount = 0;
		m_menuBar.clearItems();
	}

	/**
	 * Returns a count of the MenuItem's in this popup.
	 * 
	 * @return
	 */
	final public int getItemCount() {
		return m_itemCount;
	}
	
	/**
	 * Returns true if the menu bar has content and false otherwise.
	 * 
	 * @return
	 */
	final public boolean hasContent() {
		return (0 < getItemCount());
	}

	/**
	 * Called to close the menu.
	 */
	final public void hideMenu() {
		// Simply close the menu and hide the popup.
		m_menuBar.closeAllChildren(false);
		hide();
	}
	
	/**
	 * Returns false if the last item added was a spacer and true
	 * otherwise.
	 * 
	 * @return
	 */
	final public boolean isSpacerNeeded() {
		return m_spacerNeeded;
	}
	
	/**
	 * Passes a BinderInfo describing the currently selected binder to
	 * classes that extend MenuBarPopupBase.
	 * 
	 * @param binderInfo
	 */
	public abstract void setCurrentBinder(BinderInfo binderInfo);

	/**
	 * Passes information about the context based toolbar requirements
	 * via a List<ToolbarItem> to classes that extend MenuBarPopupBase.
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

	/**
	 * Shows the popup menu.
	 * 
	 * Overrides PopupPanel.show().
	 */
	public void show() {
		// Tell the super class to show the popup...
		super.show();
		
		// ...tell the menu's box that its got an open popup menu
		// ...associated with it...
		addAutoHidePartner(m_menuBarBox.getElement());
		m_menuBarBox.popupMenuOpened();
		
		// ...add vertical scrolling to the main frame for the duration
		// ...of the popup....
		GwtClientHelper.scrollUIForPopup(this);
		
		// ...and give the menu bar the focus.
		m_menuBar.focus();
	}
	
	/**
	 * Called to show the menu associated with the given menu bar box.
	 * 
	 * @param menuBarBox
	 */
	final public void showMenu(MenuBarBox menuBarBox) {
		// Tell the menu to show its popup.
		m_menuBarBox = menuBarBox;
		showPopup((menuBarBox.getBoxLeft() + 10), menuBarBox.getBoxBottom());
	}
	
	/**
	 * Classes that extend do what needs to be done to show their
	 * MenuBarPopupBase.
	 * 
	 * @param left
	 * @param top
	 */
	public abstract void showPopup(int left, int top);
}
