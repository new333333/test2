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
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;


/**
 * Class used for the Views menu item popup.  
 * 
 * @author drfoster@novell.com
 */
public class ViewsMenuPopup extends MenuBarPopupBase {
	private final String IDBASE = "view_";	// Base ID for the items created in this menu.

	private BinderInfo m_currentBinder;				// The currently selected binder.
	private List<ToolbarItem> m_toolbarItemList;	// The context based toolbar requirements.
	private ToolbarItem m_aboutTBI;					// The about           toolbar item, if found.
	private ToolbarItem m_activityReportTBI;		// The activity report toolbar item, if found.
	private ToolbarItem m_clipboardTBI;				// The clipboard       toolbar item, if found.
	private ToolbarItem m_trashTBI;					// The trash           toolbar item, if found.
	private ToolbarItem m_whatsNewTBI;				// The what's new      toolbar item, if found.
	private ToolbarItem m_whatsUnreadTBI;			// The what's unread   toolbar item, if found.
	private ToolbarItem m_whoHasAccessTBI;			// The who has access  toolbar item, if found.
	private ToolbarItem m_mobileUiTBI;				// The mobile UI  	   toolbar item, if found.

	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 */
	public ViewsMenuPopup(ActionTrigger actionTrigger) {
		// Initialize the super class.
		super(actionTrigger, GwtTeaming.getMessages().mainMenuBarViews());
	}

	/*
	 * Returns a nested ToolbarItem a parent ToolbarItem where the
	 * nested item contains a URL that contains the specified action. 
	 */
	private ToolbarItem getNestedItemFromUrl(ToolbarItem tbi, String action) {
		// If we don't have a toolbar to search or it has no nested
		// items...
		List<ToolbarItem> tbiList = ((null == tbi) ? null : tbi.getNestedItemsList());
		if ((null == tbiList) || tbiList.isEmpty()) {
			// ...bail.
			return null;
		}

		// Scan the nested items.
		action = ("action=" + action.toLowerCase());
		for (Iterator<ToolbarItem> tbiIT = tbiList.iterator(); tbiIT.hasNext(); ) {
			// Does this nested item contain a URL?
			ToolbarItem nestedTBI = tbiIT.next();
			String url = nestedTBI.getUrl();
			if (!(GwtClientHelper.hasString(url))) {
				// No!  Skip it.
				continue;
			}
			
			// Does the nested item contain the action that we're
			// looking for?
			url = url.toLowerCase();
			if (0 < url.indexOf(action)) {
				// Yes!  Return it.
				return nestedTBI;
			}
		}
		
		// If we get here, we didn't find the requested action.  Return
		// false.
		return null;
	}

	/**
	 * Stores information about the currently selected binder.
	 * 
	 * Implements the MenuBarPopupBase.setCurrentBinder() abstract
	 * method.
	 * 
	 * @param binderInfo
	 */
	@Override
	public void setCurrentBinder(BinderInfo binderInfo) {
		// Simply store the parameter.
		m_currentBinder = binderInfo;
	}
	
	/**
	 * Store information about the context based toolbar requirements
	 * via a List<ToolbarItem>.
	 * 
	 * Implements the MenuBarPopupBase.setToolbarItemList() abstract
	 * method.
	 * 
	 * @param toolbarItemList
	 */
	@Override
	public void setToolbarItemList(List<ToolbarItem> toolbarItemList) {
		// Simply store the parameter.
		m_toolbarItemList = toolbarItemList;
	}
	
	/**
	 * Called to determine if given the List<ToolbarItem>, should
	 * the menu be shown.  Returns true if it should be shown and false
	 * otherwise.
	 * 
	 * Implements the MenuBarPopupBase.shouldShowMenu() abstract
	 * method.
	 * 
	 * @return
	 */
	@Override
	public boolean shouldShowMenu() {
		// How we handle some of the menu items varies whether the
		// current context is a folder or workspace.  What's our
		// current context?
		boolean isFolder    =                 m_currentBinder.isBinderFolder();
		boolean isWorkspace = ((!isFolder) && m_currentBinder.isBinderWorkspace());
		
		// Scan the toolbar items...
		for (Iterator<ToolbarItem> tbiIT = m_toolbarItemList.iterator(); tbiIT.hasNext(); ) {
			// ...and keep track of the ones that appear on the views
			// ...menu.
			ToolbarItem tbi = tbiIT.next();
			String tbName = tbi.getName();
			if (tbName.equalsIgnoreCase("ssFolderToolbar")) {
				ToolbarItem adminTBI = tbi.getNestedToolbarItem("administration");
				ToolbarItem categoriesTBI = ((null == adminTBI) ? null : adminTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					String reportCategory  = null;
					String reportURLAction = null;
					if (isWorkspace) {
						reportCategory  = "reports";
						reportURLAction = "activity_report";
					}
					else if (isFolder) {
						reportURLAction = "binder_report";
					}
					if (null != reportURLAction) {
						m_activityReportTBI = getNestedItemFromUrl(
							categoriesTBI.getNestedToolbarItem(reportCategory),
							reportURLAction);
					}
				}
				m_whoHasAccessTBI = tbi.getNestedToolbarItem("whohasaccess");
			}
			
			else if (tbName.equalsIgnoreCase("ss_whatsNewToolbar")) {
				m_whatsNewTBI    = tbi.getNestedToolbarItem("whatsnew");
				if (null != m_whatsNewTBI) {
					if      (isFolder)    m_whatsNewTBI.setTitle(m_messages.mainMenuViewsWhatsNewInFolder());
					else if (isWorkspace) m_whatsNewTBI.setTitle(m_messages.mainMenuViewsWhatsNewInWorkspace());
				}
				m_whatsUnreadTBI = tbi.getNestedToolbarItem("unseen");
				if (null != m_whatsUnreadTBI) {
					if      (isFolder)    m_whatsUnreadTBI.setTitle(m_messages.mainMenuViewsWhatsUnreadInFolder());
					else if (isWorkspace) m_whatsUnreadTBI.setTitle(m_messages.mainMenuViewsWhatsUnreadInWorkspace());
				}
			}
			
			else if (tbName.equalsIgnoreCase("ssGwtMiscToolbar")) {
				m_aboutTBI     = tbi.getNestedToolbarItem("about");
				m_clipboardTBI = tbi.getNestedToolbarItem("clipboard");
				m_trashTBI     = tbi.getNestedToolbarItem("trash");
				m_mobileUiTBI  = tbi.getNestedToolbarItem("mobileUI");
			}
		}
		
		// Return true if we found any of the views menu items and
		// false otherwise.
		return
			((null != m_aboutTBI)          ||
			 (null != m_activityReportTBI) ||
			 (null != m_clipboardTBI)      ||
			 (null != m_trashTBI)          ||
			 (null != m_mobileUiTBI)       ||
			 (null != m_whatsNewTBI)       ||
			 (null != m_whatsUnreadTBI)    ||
			 (null != m_whoHasAccessTBI));
	}
	
	/**
	 * Completes construction of the menu and shows it.
	 * 
	 * Implements the MenuBarPopupBase.showPopup() abstract method.
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
			addContextMenuItem(IDBASE, m_whatsNewTBI,    true);
			addContextMenuItem(IDBASE, m_whatsUnreadTBI, true);
			addContextMenuItem(IDBASE, m_whoHasAccessTBI);
			addContextMenuItem(IDBASE, m_activityReportTBI);
			if (isSpacerNeeded() &&
					((null != m_clipboardTBI) ||
					 (null != m_mobileUiTBI) ||
					 (null != m_trashTBI))) {
				addSpacerMenuItem();
			}
			addContextMenuItem(IDBASE, m_clipboardTBI);
			addContextMenuItem(IDBASE, m_trashTBI);
			addContextMenuItem(IDBASE, m_mobileUiTBI);
			if (null != m_aboutTBI) {
				if (isSpacerNeeded()) {
					addSpacerMenuItem();
				}
				addContextMenuItem(IDBASE, m_aboutTBI);
			}
		}
					
		// ...and show it.
		show();
	}
}
