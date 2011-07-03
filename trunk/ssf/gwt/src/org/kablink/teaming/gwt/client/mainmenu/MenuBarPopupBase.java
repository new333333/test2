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
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Abstract base class used for a menu item popups.  
 * 
 * @author drfoster@novell.com
 */
public abstract class MenuBarPopupBase extends TeamingPopupPanel {
	private   boolean						m_spacerNeeded;		// false -> The last widget added was a spacer.  true -> It was something else.
	protected GwtTeamingMainMenuImageBundle	m_images;			// The menu's images.
	protected GwtTeamingMessages			m_messages;			// The menu's messages.
	protected GwtRpcServiceAsync			m_rpcService;		//
	private   MenuBarBox					m_menuBarBox;		// The menu bar box associated with a popup when opened.
	private   VerticalPanel					m_contentPanel;		// A VerticalPanel that will hold the popup's contents.
	
	/**
	 * Class constructor.
	 * 
	 * @param title
	 */
	public MenuBarPopupBase(String title) {
		// Construct the super class...
		super(true);
		setGlassEnabled(true);
		setGlassStyleName("mainMenuPopup_Glass");

		// Add a close handler to the popup so that it can restore the
		// menu bar box's styles when the popup menu is closed.
		addCloseHandler(new CloseHandler<PopupPanel>(){
			public void onClose(CloseEvent<PopupPanel> event) {
				removeAutoHidePartner(m_menuBarBox.getElement());
				m_menuBarBox.popupMenuClosed();
				m_menuBarBox = null;
			}});

		// ...store the parameters...
		m_images     = GwtTeaming.getMainMenuImageBundle();
		m_messages   = GwtTeaming.getMessages();
		m_rpcService = GwtTeaming.getRpcService();
		
		// ...and initialize everything else.
		addStyleName("mainMenuPopup_Core roundcornerSM-bottom");
		GwtClientHelper.rollDownPopup(this);

		// ...create the popup's innards...
		DockPanel dp = new DockPanel();
		dp.addStyleName("mainMenuPopup roundcornerSM-bottom smalltext");
		dp.add(createPopupContentPanel(),   DockPanel.CENTER);
		dp.add(createPopupBottom(m_images), DockPanel.SOUTH);

		// ...and add it to the popup.
		setWidget(dp);
	}

	/**
	 * Adds a Widget to the content VerticalPanel.
	 * 
	 * @param contentWidget
	 */
	final public void addContentWidget(Widget contentWidget) {
		// Simply add the widget to the content panel.
		m_contentPanel.add(contentWidget);
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
		// If we have a widget for the menu item... 
		ContextMenuItem cmi = new ContextMenuItem(this, idBase, tbi, hideEntryView);
		Widget cmiWidget = cmi.getWidget();
		if (null != cmiWidget) {
			// ...add it to the popup.
			addContentWidget(cmiWidget);
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
		FlowPanel spacerPanel = new FlowPanel();
		spacerPanel.addStyleName("mainMenuPopup_ItemSpacer");
		if (GwtClientHelper.jsIsIE()) {
			Image spacerContent = new Image(m_images.spacer1px());
			spacerPanel.add(spacerContent);
		}
		addContentWidget(spacerPanel);
		m_spacerNeeded = false;
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
	
	/**
	 * Returns true if the menu bar has content and false otherwise.
	 * 
	 * @return
	 */
	final public boolean hasContent() {
		return (0 < m_contentPanel.getWidgetCount());
	}

	/**
	 * Called to close the menu.
	 */
	final public void hideMenu() {
		// Simply hide the popup.
		hide();
	}
	
	/**
	 * Returns false if the last widget added was a spacer and true
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
	@Override
	public void show() {
		// Tell the super class to show the menu...
		super.show();
		
		// ...tell the menu's box that its got an open popup menu
		// ...associated with it...
		addAutoHidePartner(m_menuBarBox.getElement());
		m_menuBarBox.popupMenuOpened();
		
		// ...and add vertical scrolling to the main frame for the
		// ...duration of the popup.
		GwtClientHelper.scrollUIForPopup(this);
	}
	
	/**
	 * Called to show the menu associated with the given menu bar box.
	 * 
	 * @param menuBarBox
	 */
	final public void showMenu(MenuBarBox menuBarBox) {
		// Tell the menu to show its popup.
		m_menuBarBox = menuBarBox;
		showPopup(menuBarBox.getBoxLeft(), menuBarBox.getBoxBottom());
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
