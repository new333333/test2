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

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * Class used for the Show menu item popup.  
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ShowMenuPopup extends MenuBarPopup {
	private final String IDBASE = "show_";
	
	private List<TeamingMenuItem> m_menuItemList;	// The context based menu requirements.
	private String m_currentBinderId;				// ID of the currently selected binder.
	private TeamingMenuItem m_unseenMI;				// The Unseen         menu item, if found.
	private TeamingMenuItem m_whatsNewMI;			// The What's new     menu item, if found.
	private TeamingMenuItem m_whoHasAccessMI;		// THe Who has access menu item, if found.

	/*
	 * Inner class that handles clicks on the menu items.
	 */
	private class ShowClickHandler implements ClickHandler {
		private boolean m_isPopup;
		private int m_popupHeight;
		private int m_popupWidth;
		private String m_id;
		private String m_onClickJS;
		private String m_url;
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 * @param isPopup
		 * @param popupHeight
		 * @param popupWidth
		 * @param onClickJS
		 */
		ShowClickHandler(String id, String url, boolean isPopup, int popupHeight, int popupWidth, String onClickJS) {
			// Simply store the parameters.
			m_id = id;
			m_url = url;
			m_isPopup = isPopup;
			m_popupHeight = popupHeight;
			m_popupWidth = popupWidth;
			m_onClickJS = onClickJS;
		}
		
		/**
		 * Called when the user clicks on a team management command.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Remove the selection from the menu item...
			Element menuItemElement = Document.get().getElementById(m_id);
			menuItemElement.removeClassName("mainMenuPopup_ItemHover");
			
			// ...hide the menu...
			hide();

			// ...and perform the request.
			if (m_isPopup)
				 jsLaunchUrlInWindow(m_url, m_popupHeight, m_popupWidth);
			else jsEvalString(       m_url, m_onClickJS                );
		}
		
		/*
		 * Evaluates a JavaScript string containing embedded
		 * JavaScript. 
		 */
		private native void jsEvalString(String url, String jsString) /*-{
			// Setup an object to pass through the URL...
			var hrefObj = {href: url};
			
			// ...patch the JavaScript string...
			jsString = jsString.replace("this", "hrefObj");
			jsString = jsString.replace("return false;", "");
			jsString = ("window.top.gwtContentIframe." + jsString);
			
			// ...and evaluate it.
			eval(jsString);
		}-*/;
		
		/*
		 * Uses Teaming's existing ss_common JavaScript to launch a URL in
		 * a new window.
		 */
		private native void jsLaunchUrlInWindow(String url, int height, int width) /*-{
			window.top.ss_openUrlInWindow({href: url}, '_blank', width, height);
		}-*/;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 */
	public ShowMenuPopup(ActionTrigger actionTrigger) {
		// Initialize the super class.
		super(actionTrigger, GwtTeaming.getMessages().mainMenuBarShow());
	}

	/*
	 * Add a menu item to the show menu.
	 */
	private void addMenuItem(TeamingMenuItem mi) {
		// If we don't have an menu item...
		if (null == mi) {
			// Bail.
			return;
		}

		// Extract the commonly used values...
		String title = mi.getTitle();
		String url   = mi.getUrl();
		
		// ...and qualifiers from the menu item.
		String hover        = mi.getQualifierValue("title");
		String onClickJS    = mi.getQualifierValue("onclick");
		String popupS       = mi.getQualifierValue("popup");
		String popupHeightS = mi.getQualifierValue("popupHeight");
		String popupWidthS  = mi.getQualifierValue("popupWidth");

		// Parse the non-string values.
		boolean isPopup = (GwtClientHelper.hasString(popupS)       ? Boolean.parseBoolean(popupS)   : false);
		int popupHeight = (GwtClientHelper.hasString(popupHeightS) ? Integer.parseInt(popupHeightS) : (-1));
		int popupWidth  = (GwtClientHelper.hasString(popupWidthS)  ? Integer.parseInt(popupWidthS)  : (-1));
		
		String id = (IDBASE + mi.getName());
		MenuPopupAnchor mtA = new MenuPopupAnchor(id, title, hover, new ShowClickHandler(id, url, isPopup, popupHeight, popupWidth, onClickJS));
		addContentWidget(mtA);
	}
	
	/**
	 * Stores the ID of the currently selected binder.
	 * 
	 * Implements the MenuBarPopup.setCurrentBinder() abstract method.
	 * 
	 * @param binderId
	 */
	@Override
	public void setCurrentBinder(String binderId) {
		// Simply store the parameter.
		m_currentBinderId = binderId;
	}
	
	/**
	 * Store information about the context based menu requirements via
	 * a List<TeamingMenuItem>.
	 * 
	 * Implements the MenuBarPopup.setMenuItemList() abstract method.
	 * 
	 * @param menuItemList
	 */
	@Override
	public void setMenuItemList(List<TeamingMenuItem> menuItemList) {
		// Simply store the parameter.
		m_menuItemList = menuItemList;
	}
	
	/**
	 * Called to determine if given the List<TeamingMenuItem>, should
	 * the menu be shown.  Returns true if it should be shown and false
	 * otherwise.
	 * 
	 * Implements the MenuBarPopup.shouldShowMenu() abstract method.
	 * 
	 * @return
	 */
	@Override
	public boolean shouldShowMenu() {
		// Scan the menu items...
		for (Iterator<TeamingMenuItem> miIT = m_menuItemList.iterator(); miIT.hasNext(); ) {
			// ...and keep track of the ones that appear on the show
			// ...menu.
			TeamingMenuItem mi = miIT.next();
			String miName = mi.getName();
			if (miName.equalsIgnoreCase("ss_whatsNewToolbar")) {
				m_unseenMI = mi.getNestedMenuItem("unseen");
				m_whatsNewMI = mi.getNestedMenuItem("whatsnew");
			}
			
			else if (miName.equalsIgnoreCase("ssFolderToolbar")) {
				m_whoHasAccessMI = mi.getNestedMenuItem("whohasaccess");
			}
		}

		// Return true if we found any of the show menu items and false
		// otherwise.
		return
			((null != m_unseenMI) ||
			 (null != m_whatsNewMI) ||
			 (null != m_whoHasAccessMI));
	}
	
	/**
	 * Completes construction of the menu and shows it.
	 * 
	 * Implements the MenuBarPopup.showPopup() abstract method.
	 * 
	 * @param left
	 * @param top
	 */
	@Override
	public void showPopup(int left, int top) {
		// Position the menu...
		setPopupPosition(left, top);
		
		// ...and if we haven't already constructed its contents...
		if (!(hasContent())) {
			// ...construct it now...
			addMenuItem(m_unseenMI);
			addMenuItem(m_whatsNewMI);
			addMenuItem(m_whoHasAccessMI);
		}

		// ...and show it.
		show();
	}
}
