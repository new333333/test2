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
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * Class used for the Manage menu item popup.  
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ManageMenuPopup extends MenuBarPopup {
	private final String IDBASE = "manage_";	// Base ID for the items created in this menu.
	
	private List<ToolbarItem> m_toolbarItemList;	// The context based toolbar requirements.
	private String m_currentBinderId;				// ID of the currently selected binder.
	private ToolbarItem m_emailTBI;					// The email subscription toolbar item, if found.
	private ToolbarItem m_folderActionsTBI;			// The folder actions     toolbar item, if found.
	private ToolbarItem m_folderStylesTBI;			// The folder styles      toolbar item, if found.
	private ToolbarItem m_folderViewsTBI;			// The folder views       toolbar item, if found.
	private ToolbarItem m_manageProfileTBI;			// The manage profile     toolbar item, if found.
	private ToolbarItem m_otherActionsTBI;			// The other actions      toolbar item, if found.
	private ToolbarItem m_workspaceActionsTBI;		// The workspace actions  toolbar item, if found.

	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 */
	public ManageMenuPopup(ActionTrigger actionTrigger) {
		// Initialize the super class.
		super(actionTrigger, GwtTeaming.getMessages().mainMenuBarManage());
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
	 * Store information about the context based toolbar requirements
	 * via a List<ToolbarItem>.
	 * 
	 * Implements the MenuBarPopup.setToolbarItemList() abstract method.
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
	 * Implements the MenuBarPopup.shouldShowMenu() abstract method.
	 * 
	 * @return
	 */
	@Override
	public boolean shouldShowMenu() {
		// Scan the toolbar items...
		for (Iterator<ToolbarItem> tbiIT = m_toolbarItemList.iterator(); tbiIT.hasNext(); ) {
			// ...and keep track of the ones that appear on the manage
			// ...menu.
			ToolbarItem tbi = tbiIT.next();
			String tbName = tbi.getName();
			if (tbName.equalsIgnoreCase("ssEmailSubscriptionToolbar")) {
				m_emailTBI = tbi.getNestedToolbarItem("email");
			}
			
			else if (tbName.equalsIgnoreCase("ssFolderToolbar")) {
				ToolbarItem adminTBI = tbi.getNestedToolbarItem("administration");
				ToolbarItem categoriesTBI = ((null == adminTBI) ? null : adminTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_folderActionsTBI    = categoriesTBI.getNestedToolbarItem("folders");
					m_otherActionsTBI     = categoriesTBI.getNestedToolbarItem(null);
					m_workspaceActionsTBI = categoriesTBI.getNestedToolbarItem("workspace");
				}
				
				m_manageProfileTBI = tbi.getNestedToolbarItem("manageProfile");
			}
			
			else if (tbName.equalsIgnoreCase("ssFolderActionsToolbar")) {
				ToolbarItem displayStylesTBI = tbi.getNestedToolbarItem("display_styles");
				ToolbarItem categoriesTBI = ((null == displayStylesTBI) ? null : displayStylesTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_folderStylesTBI = categoriesTBI.getNestedToolbarItem("styles");
				}
			}
			
			else if (tbName.equalsIgnoreCase("ssFolderViewsToolbar")) {
				ToolbarItem displayStylesTBI = tbi.getNestedToolbarItem("display_styles");
				ToolbarItem categoriesTBI = ((null == displayStylesTBI) ? null : displayStylesTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_folderViewsTBI = categoriesTBI.getNestedToolbarItem("folderviews");
				}
			}
			
//!			...this needs to be implemented...
		}
		
		// Return true if we found any of the manage menu items and
		// false otherwise.
		return
			((null != m_emailTBI)                                                           ||
			 (null != m_manageProfileTBI)                                                   ||
			((null != m_folderActionsTBI)    && m_folderActionsTBI.hasNestedToolbarItems()) ||
			((null != m_folderStylesTBI)     && m_folderStylesTBI.hasNestedToolbarItems())  ||
			((null != m_folderViewsTBI)      && m_folderViewsTBI.hasNestedToolbarItems())   ||
			((null != m_otherActionsTBI)     && m_otherActionsTBI.hasNestedToolbarItems())  ||
			((null != m_workspaceActionsTBI) && m_workspaceActionsTBI.hasNestedToolbarItems()));
	}

	/*
	 * Shows nested toolbar items as a pop out menu from the main menu
	 * popup
	 */
	private void showPopoutMenuItems(ToolbarItem tbi) {
		// If there aren't any items for the pop out...
		List<ToolbarItem> niList = ((null == tbi) ? null : tbi.getNestedItemsList());
		if ((null == niList) || niList.isEmpty()) {
			// bail.
			return;
		}
		
//!		...this needs to be implemented...
		
		for (Iterator<ToolbarItem> niIT = niList.iterator(); niIT.hasNext(); ) {
			ToolbarItem nestedTBI = niIT.next();
			addContextMenuItem(IDBASE, nestedTBI);
		}
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
			// ...construct it now.  Add the simple menu items...
			addContextMenuItem(IDBASE, m_emailTBI);
			addContextMenuItem(IDBASE, m_manageProfileTBI);
			
			// ...and add the pop out menu items.
			showPopoutMenuItems(m_folderActionsTBI);
			showPopoutMenuItems(m_folderStylesTBI);
			showPopoutMenuItems(m_folderViewsTBI);
			showPopoutMenuItems(m_otherActionsTBI);
			showPopoutMenuItems(m_workspaceActionsTBI);
		}
					
		// ...and show it.
		show();
	}
}
