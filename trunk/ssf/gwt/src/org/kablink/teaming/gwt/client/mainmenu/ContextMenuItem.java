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

import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;


/**
 * Class used to wrap a context base menu item.  
 * 
 * @author drfoster@novell.com
 */
public class ContextMenuItem {
	private MenuBarPopup m_contextMenu;				// The menu containing    this context based menu item.
	private MenuPopupAnchor m_contextMenuAnchor;	// The anchor created for this context based menu item.
	
	/*
	 * Inner class that handles clicks on the menu items.
	 */
	private class ContextItemClickHandler implements ClickHandler {
		private boolean m_isPopup;
		private int m_popupHeight = (-1);
		private int m_popupWidth  = (-1);
		private String m_id;
		private String m_onClickJS;
		private String m_url;
		private TeamingAction m_teamingAction = TeamingAction.UNDEFINED;
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 * @param popupHeight
		 * @param popupWidth
		 */
		ContextItemClickHandler(String id, String url) {
			// Simply store the parameters.
			m_id  = id;
			m_url = url;
		}
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 * @param popupHeight
		 * @param popupWidth
		 */
		ContextItemClickHandler(String id, String url, int popupHeight, int popupWidth) {
			// Simply store the parameters.
			m_id          = id;
			m_url         = url;
			m_isPopup     = true;
			m_popupHeight = (((-1) == popupHeight) ? Window.getClientHeight() : popupHeight);
			m_popupWidth  = (((-1) == popupWidth)  ? Window.getClientWidth()  : popupWidth);
		}
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 * @param onClickJS
		 */
		ContextItemClickHandler(String id, String url, String onClickJS) {
			// Simply store the parameters.
			m_id          = id;
			m_url         = url;
			m_isPopup     = false;
			m_popupHeight = (-1);
			m_popupWidth  = (-1);
			m_onClickJS   = onClickJS;
		}
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 * @param teamingAction
		 */
		ContextItemClickHandler(String id, String url, TeamingAction teamingAction) {
			// Simply store the parameters.
			m_id            = id;
			m_url           = url;
			m_isPopup       = false;
			m_teamingAction = teamingAction;
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
			m_contextMenu.hide();

			// ...and perform the request.
			if (TeamingAction.UNDEFINED != m_teamingAction)  m_contextMenu.m_actionTrigger.triggerAction(m_teamingAction, m_url);
			else if (m_isPopup)                              GwtClientHelper.jsLaunchUrlInWindow(    m_url, m_popupHeight, m_popupWidth);
			else if (GwtClientHelper.hasString(m_onClickJS)) GwtClientHelper.jsEvalString(           m_url, m_onClickJS);
			else                                             GwtClientHelper.jsLoadUrlInContentFrame(m_url);
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param contextMenu
	 * @param idBase
	 * @param tbi
	 */
	public ContextMenuItem(MenuBarPopup contextMenu, String idBase, ToolbarItem tbi) {
		// Store the context menu we're creating this for.
		m_contextMenu = contextMenu;
		
		// If we don't have an menu item...
		if (null == tbi) {
			// ...bail.
			return;
		}
		
		// Generate an ID for this menu item.
		String id = (idBase + tbi.getName());

		// Extract the commonly used values.
		String title = tbi.getTitle();
		if (!(GwtClientHelper.hasString(title))) {
			title = tbi.getName();
		}
		String url   = tbi.getUrl();
		String hover = tbi.getQualifierValue("title");
		
		// Is this menu item based on a teaming action?
		TeamingAction ta = tbi.getTeamingAction();
		ContextItemClickHandler cich;
		if (TeamingAction.UNDEFINED == ta) {
			// No!  Is it based on an onClick JavaScript string?
			String onClickJS    = tbi.getQualifierValue("onclick");
			if (GwtClientHelper.hasString(onClickJS)) {
				// Yes!  Generate the appropriate click handler for it.
				cich = new ContextItemClickHandler(id, url, onClickJS);
			}
			else {
				// No, it isn't based on an onClick JavaScript string
				// either!  Is is to open a URL in a popup window?
				String popupS   = tbi.getQualifierValue("popup");
				boolean isPopup = (GwtClientHelper.hasString(popupS) ? Boolean.parseBoolean(popupS) : false);
				if (isPopup) {
					// Yes!  Generate the appropriate click handler for it.
					String popupHeightS = tbi.getQualifierValue("popupHeight");
					String popupWidthS  = tbi.getQualifierValue("popupWidth");
					
					int popupHeight = (GwtClientHelper.hasString(popupHeightS) ? Integer.parseInt(popupHeightS) : (-1));
					int popupWidth  = (GwtClientHelper.hasString(popupWidthS)  ? Integer.parseInt(popupWidthS)  : (-1));
	
					cich = new ContextItemClickHandler(id, url, popupHeight, popupWidth);
				}
				
				else {
					// No, it isn't to open a URL in a popup window
					// either!  The only option left is to launch the
					// URL in the content pane.  Generate the
					// appropriate click handler for it.
					cich = new ContextItemClickHandler(id, url);
				}
			}
		}
		else {
			// Yes, this menu item is based on a teaming action! 
			// Generate the appropriate click handler for it.
			cich = new ContextItemClickHandler(id, url, ta);
		}

		// Finally, create the anchor for the menu item.
		m_contextMenuAnchor = new MenuPopupAnchor(id, title, hover, cich);
	}

	/**
	 * Returns the widget constructed for this menu item.
	 * 
	 * @return
	 */
	public Widget getWidget() {
		return m_contextMenuAnchor;
	}
}
