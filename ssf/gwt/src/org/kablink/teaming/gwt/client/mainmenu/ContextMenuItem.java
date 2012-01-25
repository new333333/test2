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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.event.GotoPermalinkUrlEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.ClientEventParameter;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Class used to wrap a context base menu item.  
 * 
 * @author drfoster@novell.com
 */
public class ContextMenuItem {
	private boolean m_hideEntryView;				// true -> Hides any open entry view when the menu item is triggered.  false -> It doesn't.	
	private MenuBarPopupBase m_contextMenu;			// The menu containing    this context based menu item.
	private MenuPopupAnchor m_contextMenuAnchor;	// The anchor created for this context based menu item.
	
	private enum ClickHandlerType {
		TEAMING_EVENT,
		JAVASCRIPT_STRING,
		URL_IN_POPUP_NO_FORM,
		URL_IN_POPUP_WITH_FORM,
		URL_IN_CONTENT_FRAME,
		
		UNDEFINED,
	}
	
	/*
	 * Inner class that handles clicks on the menu items.
	 */
	private class ContextItemClickHandler implements ClickHandler {
		private ClickHandlerType m_type = ClickHandlerType.UNDEFINED;
		private FormPanel m_fp;
		private int m_popupHeight;
		private int m_popupWidth;
		private String m_id;
		private String m_onClickJS;
		private String m_url;
		private TeamingEvents m_teamingEvent;
		@SuppressWarnings("unused")
		private ClientEventParameter m_clientEventParameter;
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 */
		ContextItemClickHandler(String id, String url) {
			// Store the type of click handler...
			m_type = ClickHandlerType.URL_IN_CONTENT_FRAME;
			
			// ...and the parameters.
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
			// Store the type of click handler...
			m_type = ClickHandlerType.URL_IN_POPUP_NO_FORM;
			
			// ...and the parameters.
			m_id          = id;
			m_url         = url;
			m_popupHeight = popupHeight;
			m_popupWidth  = popupWidth;
		}
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param fp
		 * @param popupHeight
		 * @param popupWidth
		 */
		ContextItemClickHandler(String id, FormPanel fp, int popupHeight, int popupWidth) {
			// Store the type of click handler...
			m_type = ClickHandlerType.URL_IN_POPUP_WITH_FORM;
			
			// ...and the parameters.
			m_id          = id;
			m_fp          = fp;
			m_popupHeight = popupHeight;
			m_popupWidth  = popupWidth;
		}
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 * @param onClickJS
		 */
		ContextItemClickHandler(String id, String url, String onClickJS) {
			// Store the type of click handler...
			m_type = ClickHandlerType.JAVASCRIPT_STRING;
			
			// ...and the parameters.
			m_id        = id;
			m_url       = url;
			m_onClickJS = onClickJS;
		}
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 * @param teamingEvent
		 * @param clientEventParameter
		 */
		ContextItemClickHandler(String id, String url, TeamingEvents teamingEvent, ClientEventParameter clientEventParameter) {
			// Store the type of click handler...
			m_type = ClickHandlerType.TEAMING_EVENT;
			
			// ...and the parameters.
			m_id                   = id;
			m_url                  = url;
			m_teamingEvent         = teamingEvent;
			m_clientEventParameter = clientEventParameter;
		}
		
		/**
		 * Class constructor.
		 *
		 * @param id
		 * @param url
		 * @param teamingEvent
		 */
		@SuppressWarnings("unused")
		ContextItemClickHandler(String id, String url, TeamingEvents teamingEvent) {
			// Always use the initial form of the constructor.
			this(id, url, teamingEvent, null);
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
			
			// ...if requested to do so...
			if (m_hideEntryView) {
				// ...hide any entry view...
				GwtClientHelper.jsHideNewPageEntryViewDIV();
			}

			// ...and perform the request based on the type of click
			// ...handler was constructed from.
			switch (m_type) {
			case JAVASCRIPT_STRING:
				GwtClientHelper.jsEvalString(m_url, m_onClickJS);
				break;
				
			case TEAMING_EVENT:
				switch (m_teamingEvent) {
				case GOTO_PERMALINK_URL:
					GwtTeaming.fireEvent(new GotoPermalinkUrlEvent(m_url));
					break;

				case EDIT_CURRENT_BINDER_BRANDING:
				case TRACK_CURRENT_BINDER:
				case UNTRACK_CURRENT_BINDER:
				case UNTRACK_CURRENT_PERSON:
				case VIEW_CURRENT_BINDER_TEAM_MEMBERS:
					EventHelper.fireSimpleEvent(m_teamingEvent);
					break;
					
				default:
					Window.alert(
						GwtTeaming.getMessages().eventHandling_NoContextMenuEventHandler(
							m_teamingEvent.name()));
					break;
				}				
				break;

			case URL_IN_CONTENT_FRAME:
				GwtTeaming.fireEvent(new GotoContentUrlEvent(m_url));
				break;
				
			case URL_IN_POPUP_NO_FORM:
				GwtClientHelper.jsLaunchUrlInWindow(m_url, "_blank", m_popupHeight, m_popupWidth);
				break;
				
			case URL_IN_POPUP_WITH_FORM:
				GwtClientHelper.jsLaunchUrlInWindow("", m_fp.getTarget(), m_popupHeight, m_popupWidth);
				m_fp.submit();
				break;
			}
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param contextMenu
	 * @param idBase
	 * @param tbi
	 * @param hideEntryView
	 */
	public ContextMenuItem(MenuBarPopupBase contextMenu, String idBase, ToolbarItem tbi, boolean hideEntryView) {
		// Store the parameters we keep class global.
		m_contextMenu = contextMenu;
		m_hideEntryView = hideEntryView;
		
		// If we don't have an menu item...
		if (null == tbi) {
			// ...bail.
			return;
		}
		
		// What should we use for the label for the anchor?
		String label = tbi.getTitle();
		if (!(GwtClientHelper.hasString(label))) {
			label = tbi.getName();
		}
		
		// Finally, create the anchor for the menu item.
		String id = (idBase + tbi.getName());
		ContextItemClickHandler clicker = createClickHandler(id, tbi);
		m_contextMenuAnchor = new MenuPopupAnchor(
			id,
			label,
			tbi.getQualifierValue("title"),
			clicker);
	}
	
	public ContextMenuItem(MenuBarPopupBase contextMenu, String idBase, ToolbarItem tbi) {
		// Always use the initial form of the constructor, defaulting
		// to not hiding an entry view.
		this(contextMenu, idBase, tbi, false);
	}

	/*
	 * Creates a click handler based on a toolbar item.
	 */
	private ContextItemClickHandler createClickHandler(String id, ToolbarItem tbi) {
		ContextItemClickHandler reply;
		String url = tbi.getUrl();
		TeamingEvents te = tbi.getTeamingEvent();
		ClientEventParameter cep = tbi.getClientEventParameter();
		if (TeamingEvents.UNDEFINED.equals(te)) {
			// It's not based on an event!  Is it based on an onClick
			// JavaScript string?
			String jsString = tbi.getQualifierValue("onclick");
			if (GwtClientHelper.hasString(jsString)) {
				// Yes!  Generate the appropriate click handler for it.
				reply = new ContextItemClickHandler(id, url, jsString);
			}
			
			// No, it isn't based on a JavaScript string!  Is is to
			// open a URL in a popup window?
			else if (GwtClientHelper.bFromS(tbi.getQualifierValue("popup"))) {
				// Yes!  Generate the appropriate click handler for it.
				reply = createPopupClickHandler(id, url, tbi);
			}
			
			else {
				// No, it isn't to open a URL in a popup window either!
				// The only option left is to launch the URL in the content
				// pane.  Generate the appropriate click handler for it.
				reply = new ContextItemClickHandler(id, url);
			}
		}
		
		else {
			// It's based on an event!  Generate the appropriate click
			// handler for it.
			reply = new ContextItemClickHandler(id, url, te, cep);
		}

		// If we get here, reply refers to the appropriate click
		// handler for the toolbar item.  Return it.
		return reply;
	}

	/*
	 * Creates a click handler that requires opening a popup window
	 * based on a toolbar item.
	 */
	private ContextItemClickHandler createPopupClickHandler(String id, String url, ToolbarItem tbi) {
		ContextItemClickHandler reply;
		
		// What's the dimensions for the popup window?
		int popupHeight = GwtClientHelper.iFromS(tbi.getQualifierValue("popupHeight"), Window.getClientHeight());
		int popupWidth  = GwtClientHelper.iFromS(tbi.getQualifierValue("popupWidth"),  Window.getClientWidth());

		// Are we supposed to open the popup window using a submitted form?
		int hiCount = 0;
		boolean popupFromForm = GwtClientHelper.bFromS(tbi.getQualifierValue("popup.fromForm"));
		if (popupFromForm) {
			// Possibly!  We'll only do so if we're going to have more
			// than one hidden input of data to pass through it.
			hiCount = GwtClientHelper.iFromS(tbi.getQualifierValue("popup.hiddenInput.count"));
			popupFromForm = (0 < hiCount);
		}
		if (popupFromForm) {
			// Yes!  Create the form...
			FormPanel fp = new FormPanel(id + "Wnd");
			fp.setAction(url);
			fp.setMethod(FormPanel.METHOD_POST);
			Element fpE = fp.getElement();
			fpE.setClassName("inline");
			GwtClientHelper.jsAppendDocumentElement(fpE);

			// ...since a FormPanel is a SimplePanel, it can only hold
			// ...a single widget.  If we have more than 1 hidden
			// ...input, we'll use a nested FlowPanel to contain them.
			Panel hiPanel;
			if (1 < hiCount) {
				FlowPanel fpPanel = new FlowPanel();
				fp.add(fpPanel);
				hiPanel = fpPanel;
			}
			else {
				hiPanel = fp;
			}
			
			// ...add the required hidden inputs to it...
			for (int i = 0; i < hiCount; i += 1) {
				String hiBase  = ("popup.hiddenInput." + i + ".");
				Hidden hInput = new Hidden();
				hInput.setName( tbi.getQualifierValue(hiBase + "name"));
				hInput.setValue(tbi.getQualifierValue(hiBase + "value"));
				hiPanel.add(hInput);
			}
			
			// ...and create the click handler.
			reply = new ContextItemClickHandler(id, fp, popupHeight, popupWidth);
		}
		
		else {
			// No, we don't need to use submitted form!  Generate the
			// appropriate click handler.
			reply = new ContextItemClickHandler(id, url, popupHeight, popupWidth);
		}
		
		// If we get here, reply refers to the appropriate click
		// handler for the toolbar item.  Return it.
		return reply;
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
