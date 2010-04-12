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

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
		ContextItemClickHandler(String id, String url, boolean isPopup, int popupHeight, int popupWidth, String onClickJS) {
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
			m_contextMenu.hide();

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
	 * @param contextMenu
	 * @param idBase
	 * @param tbi
	 */
	public ContextMenuItem(MenuBarPopup contextMenu, String idBase, ToolbarItem tbi) {
		// Store the context menu we're creating this for.
		m_contextMenu = contextMenu;
		
		// If we don't have an menu item...
		if (null == tbi) {
			// Bail.
			return;
		}

		// Extract the commonly used values...
		String title = tbi.getTitle();
		String url   = tbi.getUrl();
		
		// ...and qualifiers from the menu item.
		String hover        = tbi.getQualifierValue("title");
		String onClickJS    = tbi.getQualifierValue("onclick");
		String popupS       = tbi.getQualifierValue("popup");
		String popupHeightS = tbi.getQualifierValue("popupHeight");
		String popupWidthS  = tbi.getQualifierValue("popupWidth");

		// Parse the non-string values.
		boolean isPopup = (GwtClientHelper.hasString(popupS)       ? Boolean.parseBoolean(popupS)   : false);
		int popupHeight = (GwtClientHelper.hasString(popupHeightS) ? Integer.parseInt(popupHeightS) : (-1));
		int popupWidth  = (GwtClientHelper.hasString(popupWidthS)  ? Integer.parseInt(popupWidthS)  : (-1));
		
		String id = (idBase + tbi.getName());
		m_contextMenuAnchor = new MenuPopupAnchor(
			id,
			title,
			hover,
			new ContextItemClickHandler(
				id,
				url,
				isPopup,
				popupHeight,
				popupWidth,
				onClickJS));
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
