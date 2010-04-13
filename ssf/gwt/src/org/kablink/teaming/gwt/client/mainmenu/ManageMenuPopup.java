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
	private TeamManagementInfo m_tmi;				// The team management information for which team management menu items should appear on the menu.
	private ToolbarItem m_emailTBI;					// The email subscription toolbar item, if found.
	private ToolbarItem m_folderActionsTBI;			// The folder actions     toolbar item, if found.
	private ToolbarItem m_folderViewsTBI;			// The folder views       toolbar item, if found.
	private ToolbarItem m_otherActionsTBI;			// The other actions      toolbar item, if found.
	private ToolbarItem m_unseenTBI;				// The unseen             toolbar item, if found.
	private ToolbarItem m_whatsNewTBI;				// The what's new         toolbar item, if found.
	private ToolbarItem m_whoHasAccessTBI;			// The who has access     toolbar item, if found.
	private ToolbarItem m_workspaceActionsTBI;		// The workspace actions  toolbar item, if found.

	/*
	 * Inner class that handles clicks on team management commands.
	 */
	private class TeamManagementClickHandler implements ClickHandler {
		private String m_manageUrl;		// The URL to launch for the management command, if done by URL.
		private TeamingAction m_action;	// The TeamingAction to perform for the management command, if done by triggering an action.
		
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
				jsLaunchUrlInWindow(m_manageUrl);
			}
			else {
				// ...otherwise, trigger the action.
				m_actionTrigger.triggerAction(m_action);
			}
		}
		
		/*
		 * Uses Teaming's existing ss_common JavaScript to launch a URL in
		 * a new window.
		 */
		private native void jsLaunchUrlInWindow(String url) /*-{
			window.top.ss_openUrlInWindow({href: url}, '_blank', 500, 600);
		}-*/;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 * @param manageName
	 */
	public ManageMenuPopup(ActionTrigger actionTrigger, String manageName) {
		// Initialize the super class.
		super(actionTrigger, manageName);
	}

	/*
	 * Returns true if a toolbar item will require a popout and false
	 * otherwise.
	 */
	private boolean hasNestedItems(ToolbarItem tbi) {
		// If there aren't any items for the pop out...
		List<ToolbarItem> niList = ((null == tbi) ? null : tbi.getNestedItemsList());
		if ((null == niList) || niList.isEmpty()) {
			// ...then one isn't required...
			return false;
		}
		
		// ...otherwise, one is.
		return true;
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
			if (tbName.equalsIgnoreCase("ssFolderToolbar")) {
				ToolbarItem adminTBI = tbi.getNestedToolbarItem("administration");
				ToolbarItem categoriesTBI = ((null == adminTBI) ? null : adminTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_folderActionsTBI    = categoriesTBI.getNestedToolbarItem("folders");
					m_otherActionsTBI     = categoriesTBI.getNestedToolbarItem(null);
					m_workspaceActionsTBI = categoriesTBI.getNestedToolbarItem("workspace");
				}
				
				m_whoHasAccessTBI = tbi.getNestedToolbarItem("whohasaccess");
			}
			
			else if (tbName.equalsIgnoreCase("ssFolderViewsToolbar")) {
				ToolbarItem displayStylesTBI = tbi.getNestedToolbarItem("display_styles");
				ToolbarItem categoriesTBI = ((null == displayStylesTBI) ? null : displayStylesTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_folderViewsTBI = categoriesTBI.getNestedToolbarItem("folderviews");
				}
			}
			
			else if (tbName.equalsIgnoreCase("ss_whatsNewToolbar")) {
				m_unseenTBI = tbi.getNestedToolbarItem("unseen");
				m_whatsNewTBI = tbi.getNestedToolbarItem("whatsnew");
			}
			
			else if (tbName.equalsIgnoreCase("ssEmailSubscriptionToolbar")) {
				m_emailTBI = tbi.getNestedToolbarItem("email");
			}
		}
		
		// Return true if we found any of the manage menu items and
		// false otherwise.
		return
			((null != m_emailTBI)                                                           ||
			 (null != m_whatsNewTBI)                                                        ||
			 (null != m_unseenTBI)                                                          ||
			 (null != m_whoHasAccessTBI)                                                    ||
			((null != m_folderActionsTBI)    && m_folderActionsTBI.hasNestedToolbarItems()) ||
			((null != m_folderViewsTBI)      && m_folderViewsTBI.hasNestedToolbarItems())   ||
			((null != m_otherActionsTBI)     && m_otherActionsTBI.hasNestedToolbarItems())  ||
			((null != m_workspaceActionsTBI) && m_workspaceActionsTBI.hasNestedToolbarItems()));
	}

	/*
	 * If there are any folder options, adds a folder options items to
	 * run the folder options dialog.
	 */
	private void showFolderOptions(ToolbarItem tbi) {
		// If there aren't any items for the pop out...
		final List<ToolbarItem> niList = ((null == tbi) ? null : tbi.getNestedItemsList());
		if ((null == niList) || niList.isEmpty()) {
			// ...bail.
			return;
		}

		// ...Add an anchor to run the folder options dialog.
		MenuPopupAnchor mtA = new MenuPopupAnchor((IDBASE + "FolderOptions"), m_messages.mainMenuManageFolderOptions(), null, new ClickHandler() {
			public void onClick(ClickEvent event) {
//!				...this needs to be implemented...
				addContentWidget(new Label("START:  ...this needs to be implemented..."));
				for (Iterator<ToolbarItem> niIT = niList.iterator(); niIT.hasNext(); ) {
					ToolbarItem nestedTBI = niIT.next();
					addContextMenuItem(IDBASE, nestedTBI);
				}
				addContentWidget(new Label("END:  ...this needs to be implemented..."));
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
		setPopupPosition(left, top);
		
		// Have we constructed the menu's contents yet?
		if (!(hasContent())) {
			// No!  We need to construct it now.  First the what's new,
			// unread and who has access items...
			addContextMenuItem(IDBASE, m_whatsNewTBI);
			addContextMenuItem(IDBASE, m_unseenTBI);
			addContextMenuItem(IDBASE, m_whoHasAccessTBI);
			if ((null != m_whatsNewTBI) || (null != m_unseenTBI) || (null != m_whoHasAccessTBI)) {
				// ...and spacer if we showed any of them.
				addSpacerMenuItem();
			}

			// Then the binder actions including the folder options
			// when required...
			addNestedContextMenuItems(IDBASE, m_folderActionsTBI);
			addNestedContextMenuItems(IDBASE, m_otherActionsTBI);
			addNestedContextMenuItems(IDBASE, m_workspaceActionsTBI);
			showFolderOptions(m_folderViewsTBI);
			boolean hasBinderActions =
				(hasNestedItems(m_folderActionsTBI) ||
				 hasNestedItems(m_folderViewsTBI)   ||
				 hasNestedItems(m_otherActionsTBI)  ||
				 hasNestedItems(m_workspaceActionsTBI));
			
			// If we're going to display more stuff below...
			if (hasBinderActions && (((null != m_tmi) && m_tmi.isTeamManagementEnabled()) || (null != m_emailTBI))) {
				// ...add another spacer before them.
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
			addContextMenuItem(IDBASE, m_emailTBI);
		}
					
		// Finally, show the popup.
		show();
	}
}
