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
	
	private int m_menuLeft;							// Left coordinate of where the menu is to be placed.
	private int m_menuTop;							// Top  coordinate of where the menu is to be placed.
	private List<ToolbarItem> m_toolbarItemList;	// The context based toolbar requirements.
	private String m_currentBinderId;				// ID of the currently selected binder.
	private TeamManagementInfo m_tmi;				// The team management information for which team management menu items should appear on the menu.
	private ToolbarItem m_calendarImportTBI;		// The calendar import    toolbar item, if found.
	private ToolbarItem m_commonActionsTBI;			// The common actions     toolbar item, if found.
	private ToolbarItem m_emailNotificationTBI;		// The email notification toolbar item, if found.
	private ToolbarItem m_folderActionsTBI;			// The folder actions     toolbar item, if found.
	private ToolbarItem m_folderViewsTBI;			// The folder views       toolbar item, if found.
	private ToolbarItem m_trackThisTBI;				// The track this xxx     toolbar item, if found.
	private ToolbarItem m_whatsNewTBI;				// The what's new         toolbar item, if found.
	private ToolbarItem m_whatsUnreadTBI;			// The what's unread      toolbar item, if found.
	private ToolbarItem m_whoHasAccessTBI;			// The who has access     toolbar item, if found.
	private ToolbarItem m_workspaceActionsTBI;		// The workspace actions  toolbar item, if found.

	/*
	 * Inner class that handles clicks on team management commands.
	 */
	private class TeamManagementClickHandler implements ClickHandler {
		private String m_manageUrl;		// The URL to launch for the team management command, if done by URL.
		private TeamingAction m_action;	// The TeamingAction to perform for the team management command, if done by triggering an action.
		
		/**
		 * Class constructor.
		 * 
		 * @param manageUrl
		 */
		TeamManagementClickHandler(String manageUrl) {
			// Simply store the parameter.
			m_manageUrl = manageUrl;
		}
		
		/**
		 * Class constructor.
		 * 
		 * @param teamingAction
		 */
		TeamManagementClickHandler(TeamingAction action) {
			// Simply store the parameter.
			m_action = action;
		}
		
		/**
		 * Called when the user clicks on a team management command.
		 * 
		 * @param event
		 */
		public void onClick(ClickEvent event) {
			// Hide the menu.
			hide();
			
			// If the team management command is implemented as a URL...
			if (GwtClientHelper.hasString(m_manageUrl)) {
				// ...launch it in a window...
				GwtClientHelper.jsLaunchUrlInWindow(m_manageUrl, 500, 600);
			}
			else {
				// ...otherwise, trigger the action.
				m_actionTrigger.triggerAction(m_action);
			}
		}
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 * @param manageName
	 */
	public ManageMenuPopup(ActionTrigger actionTrigger, String manageName) {
		// Simply initialize the super class.
		super(actionTrigger, manageName);
	}

	/*
	 * Returns true if a toolbar item has nested toolbar items and
	 * false otherwise.
	 */
	private boolean hasNestedItems(ToolbarItem tbi, int atLeast) {
		return ((null == tbi) ? false : tbi.hasNestedToolbarItems(atLeast));
	}
	
	private boolean hasNestedItems(ToolbarItem tbi) {
		return hasNestedItems(tbi, 1);
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
		m_currentBinderId = binderId;
	}

	/**
	 * Stores team management information for use by the menu.
	 * 
	 * @param tmi
	 */
	public void setTeamManagementInfo(TeamManagementInfo tmi) {
		m_tmi = tmi;
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
		ToolbarItem categoriesTBI;
		for (Iterator<ToolbarItem> tbiIT = m_toolbarItemList.iterator(); tbiIT.hasNext(); ) {
			// ...and keep track of the ones that appear on the manage
			// ...menu.
			ToolbarItem tbi = tbiIT.next();
			String tbName = tbi.getName();
			if (tbName.equalsIgnoreCase("ssFolderToolbar")) {
				ToolbarItem adminTBI = tbi.getNestedToolbarItem("administration");
				categoriesTBI = ((null == adminTBI) ? null : adminTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_folderActionsTBI    = categoriesTBI.getNestedToolbarItem("folders");
					m_commonActionsTBI    = categoriesTBI.getNestedToolbarItem(null);
					m_workspaceActionsTBI = categoriesTBI.getNestedToolbarItem("workspace");
				}
				m_whoHasAccessTBI = tbi.getNestedToolbarItem("whohasaccess");
			}
			
			else if (tbName.equalsIgnoreCase("ssFolderViewsToolbar")) {
				ToolbarItem displayStylesTBI = tbi.getNestedToolbarItem("display_styles");
				categoriesTBI = ((null == displayStylesTBI) ? null : displayStylesTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_folderViewsTBI = categoriesTBI.getNestedToolbarItem("folderviews");
				}
			}
			
			else if (tbName.equalsIgnoreCase("ss_whatsNewToolbar")) {
				m_whatsNewTBI    = tbi.getNestedToolbarItem("whatsnew");
				m_whatsUnreadTBI = tbi.getNestedToolbarItem("unseen");
			}
			
			else if (tbName.equalsIgnoreCase("ssEmailSubscriptionToolbar")) {
				m_emailNotificationTBI = tbi.getNestedToolbarItem("email");
			}
			
			else if (tbName.equalsIgnoreCase("ssCalendarImportToolbar")) {
				ToolbarItem calendarTBI = tbi.getNestedToolbarItem("calendar");
				if (null != calendarTBI) {
					categoriesTBI = calendarTBI.getNestedToolbarItem("categories");
					if (null != categoriesTBI) {
						m_calendarImportTBI = categoriesTBI.getNestedToolbarItem("calendar");
					}
				}
			}
			
			else if (tbName.equalsIgnoreCase("ssGwtMiscToolbar")) {
				m_trackThisTBI = tbi.getNestedToolbarItem("track");
			}
		}
		
		// Return true if we found any of the manage menu items and
		// false otherwise.
		return
			((null != m_emailNotificationTBI)                                                ||
			 (null != m_trackThisTBI)                                                        ||
			 (null != m_whatsNewTBI)                                                         ||
			 (null != m_whatsUnreadTBI)                                                      ||
			 (null != m_whoHasAccessTBI)                                                     ||
			((null != m_calendarImportTBI)   && m_calendarImportTBI.hasNestedToolbarItems()) ||
			((null != m_commonActionsTBI)    && m_commonActionsTBI.hasNestedToolbarItems())  ||
			((null != m_folderActionsTBI)    && m_folderActionsTBI.hasNestedToolbarItems())  ||
			((null != m_folderViewsTBI)      && m_folderViewsTBI.hasNestedToolbarItems(2))   ||
			((null != m_workspaceActionsTBI) && m_workspaceActionsTBI.hasNestedToolbarItems()));
	}

	/*
	 * If there are any folder options, adds a folder options menu item
	 * that will run the folder options dialog.
	 */
	private void showFolderOptions(final ToolbarItem folderViewsTBI, final ToolbarItem calendarImportTBI) {
		// If there aren't any folder options...
		if ((!(hasNestedItems(folderViewsTBI, 2))) &&
			(!(hasNestedItems(calendarImportTBI)))) {
			// ...bail.
			return;
		}

		// Add an anchor to run the folder options dialog.
		final String foId = (IDBASE + "FolderOptions");
		MenuPopupAnchor mtA = new MenuPopupAnchor(foId, m_messages.mainMenuManageFolderOptions(), null, new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Remove the selection from the menu item...
				Element menuItemElement = Document.get().getElementById(foId);
				menuItemElement.removeClassName("mainMenuPopup_ItemHover");
				
				// ...hide the menu...
				hide();
				
				// ...and run the folder options dialog.
				FolderOptionsDlg folderOptionsDlg = new FolderOptionsDlg(
					true,	// true -> Auto hide.
					true,	// true -> Modal.
					m_menuLeft,
					m_menuTop,
					((null == calendarImportTBI) ? null : calendarImportTBI.getNestedItemsList()),
					((null == folderViewsTBI)    ? null : folderViewsTBI.getNestedItemsList()));
				folderOptionsDlg.addStyleName("folderOptionsDlg");
				folderOptionsDlg.show();
			}
		});
		addContentWidget(mtA);
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
		m_menuLeft = left;
		m_menuTop  = top;
		setPopupPosition(m_menuLeft, m_menuTop);
		
		// Have we constructed the menu's contents yet?
		if (!(hasContent())) {
			boolean hasBinderActions = 
				(hasNestedItems(m_calendarImportTBI) ||
				 hasNestedItems(m_commonActionsTBI)  ||
				 hasNestedItems(m_folderActionsTBI)  ||
				 hasNestedItems(m_folderViewsTBI, 2) ||
				 hasNestedItems(m_workspaceActionsTBI));
			boolean hasShowActions = ((null != m_whatsNewTBI) || (null != m_whatsUnreadTBI) || (null != m_whoHasAccessTBI));
			boolean hasManageActions = (((null != m_tmi) && m_tmi.isTeamManagementEnabled()) || (null != m_emailNotificationTBI));
			boolean hasMiscActions = (null != m_trackThisTBI);
			
			// No!  We need to construct it now.  First the what's new,
			// unread and who has access items...
			addContextMenuItem(IDBASE, m_whatsNewTBI);
			addContextMenuItem(IDBASE, m_whatsUnreadTBI);
			addContextMenuItem(IDBASE, m_whoHasAccessTBI);
			if (hasShowActions && hasBinderActions) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}

			// Then the binder actions including the folder options
			// when required...
			addNestedContextMenuItems(IDBASE, m_folderActionsTBI);
			addNestedContextMenuItems(IDBASE, m_commonActionsTBI);
			addNestedContextMenuItems(IDBASE, m_workspaceActionsTBI);
			showFolderOptions(m_folderViewsTBI, m_calendarImportTBI);
			if (hasBinderActions && hasManageActions) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}

			// If we have any team management items...
			if (null != m_tmi) {
				// ...add them to the menu...
				MenuPopupAnchor mtA;
				if (m_tmi.isViewAllowed()) {
					mtA = new MenuPopupAnchor((IDBASE + "View"), m_messages.mainMenuManageViewTeam(), null, new TeamManagementClickHandler(TeamingAction.VIEW_TEAM_MEMBERS));
					addContentWidget(mtA);
				}
				if (m_tmi.isManageAllowed()) {
					mtA = new MenuPopupAnchor((IDBASE + "Edit"), m_messages.mainMenuManageEditTeam(), null, new TeamManagementClickHandler(m_tmi.getManageUrl()));
					addContentWidget(mtA);
				}
				if (m_tmi.isSendMailAllowed()) {
					mtA = new MenuPopupAnchor((IDBASE + "Send"), m_messages.mainMenuManageSendTeamEmail(), null, new TeamManagementClickHandler(m_tmi.getSendMailUrl()));
					addContentWidget(mtA);
				}
				if (m_tmi.isTeamMeetingAllowed()) {
					mtA = new MenuPopupAnchor((IDBASE + "Conference"), m_messages.mainMenuManageStartTeamConference(), null, new TeamManagementClickHandler(m_tmi.getTeamMeetingUrl()));
					addContentWidget(mtA);
				}
			}
			
			// ...and any email notification item.
			addContextMenuItem(IDBASE, m_emailNotificationTBI);
			if (hasManageActions && hasMiscActions) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}

			// Add any miscellaneous items.
			addContextMenuItem(IDBASE, m_trackThisTBI);
		}
					
		// Finally, show the popup.
		show();
	}
}
