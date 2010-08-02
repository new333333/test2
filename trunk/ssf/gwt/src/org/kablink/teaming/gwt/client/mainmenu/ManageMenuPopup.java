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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * Class used for the Manage menu item popup.  
 * 
 * @author drfoster@novell.com
 */
public class ManageMenuPopup extends MenuBarPopupBase {
	private final String IDBASE = "manage_";	// Base ID for the items created in this menu.
	
	private BinderInfo m_currentBinder;				// The currently selected binder.
	private int m_menuLeft;							// Left coordinate of where the menu is to be placed.
	private int m_menuTop;							// Top  coordinate of where the menu is to be placed.
	private List<ToolbarItem> m_actionsBucket;		// List of action         items for the context based menu.
	private List<ToolbarItem> m_configBucket;		// List of configuration  items for the context based menu.
	private List<ToolbarItem> m_ignoreBucket;		// List of ignored        items for the context based menu.
	private List<ToolbarItem> m_miscBucket;			// List of miscellaneous  items for the context based menu.
	private List<ToolbarItem> m_teamAndEmailBucket;	// List of team and email items for the context based menu.
	private List<ToolbarItem> m_toolbarItemList;	// The context based toolbar requirements.
	private TeamManagementInfo m_tmi;				// The team management information for which team management menu items should appear on the menu.
	private ToolbarItem m_brandingTBI;				// The branding                  toolbar item, if found.
	private ToolbarItem m_calendarImportTBI;		// The calendar import           toolbar item, if found.
	private ToolbarItem m_commonActionsTBI;			// The common actions            toolbar item, if found.
	private ToolbarItem m_configureColumnsTBI;		// The configure columns         toolbar item, if found.
	private ToolbarItem m_emailContributorsTBI;		// The email contributors        toolbar item, if found.
	private ToolbarItem m_emailNotificationTBI;		// The email notification        toolbar item, if found.
	private ToolbarItem m_folderViewsTBI;			// The folder views              toolbar item, if found.
	private ToolbarItem m_shareThisTBI;				// The share this                toolbar item, if found.
	private ToolbarItem m_trackBinderTBI;			// The binder tracking           toolbar item, if found.
	private ToolbarItem m_trackPersonTBI;			// The person tracking           toolbar item, if found.

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
	 * Scans the nested items of a toolbar looking for one whose URL
	 * contains a specific action.  If it is found, it is added to
	 * the bucket.
	 * 
	 * Returns true if the action was found and added to the bucket
	 * and false otherwise.
	 */
	private boolean addNestedItemFromUrl(List<ToolbarItem> bucket, ToolbarItem tbi, String action) {
		return addNestedItemFromUrl(bucket, tbi, action, null);
	}
	private boolean addNestedItemFromUrl(List<ToolbarItem> bucket, ToolbarItem tbi, String action, String operation) {
		// If we don't have a toolbar to search or it has no nested
		// items...
		List<ToolbarItem> tbiList = ((null == tbi) ? null : tbi.getNestedItemsList());
		if ((null == tbiList) || tbiList.isEmpty()) {
			// ...bail.
			return false;
		}

		// Scan the nested items.
		action = ("action=" + action.toLowerCase());
		if (null != operation) {
			operation = ("operation=" + operation.toLowerCase());
		}
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
				// Yes!  If we don't have an operation to check for or
				// the URL contains the operation...
				if ((null == operation) || (0 < url.indexOf(operation))) {
					// ...add it to the bucket and return true.
					tbiList.remove(nestedTBI);
					bucket.add(nestedTBI);
					return true;
				}
			}
		}
		
		// If we get here, we didn't find the requested action.  Return
		// false.
		return false;
	}

	/*
	 * Copies the nested ToolbarItem's from one ToolbarItem to another.
	 */
	private static void copyNestedToolbarItems(ToolbarItem tbiDest, ToolbarItem tbiSrc) {
		// If we have both source and destination ToolbarItems's...
		if ((null != tbiDest) && (null != tbiSrc)) {		
			// ...and the source has a nested List<ToolBarItem>...
			List<ToolbarItem> nestedSrcTBIList = tbiSrc.getNestedItemsList();
			if (null != nestedSrcTBIList) {
				// ...scan them...
				for (Iterator<ToolbarItem> tbiIT = nestedSrcTBIList.iterator(); tbiIT.hasNext(); ) {
					// ...and store them in the destination's nested
					// ...List<ToolbarItem>.
					tbiDest.addNestedItem(tbiIT.next());
				}
			}
		}
	}

	/*
	 * Called to process the various menu items into ordered lists of
	 * items that appear in the various sections of the menu.
	 */
	private void fillBuckets() {
		ToolbarItem localTBI;

		// Allocate the bucket lists.
		m_actionsBucket      = new ArrayList<ToolbarItem>();
		m_configBucket       = new ArrayList<ToolbarItem>();
		m_ignoreBucket       = new ArrayList<ToolbarItem>();
		m_miscBucket         = new ArrayList<ToolbarItem>();
		m_teamAndEmailBucket = new ArrayList<ToolbarItem>();

		// File the buckets in the order things will appear in the
		// menu.  Start with the actions section...
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "add_binder",    "add_folder");
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "add_binder",    "add_subFolder");
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "add_binder",    "add_workspace");
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "modify_binder", "delete");
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "modify_binder", "modify");
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "modify_binder", "copy");
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "modify_binder", "move");
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "export_import");
		addNestedItemFromUrl(m_actionsBucket, m_commonActionsTBI, "manage_definitions");
		
		// ...then the team section...
		if ((null != m_tmi) && m_tmi.isTeamManagementEnabled()) {
			// Add the team management items.
			if (m_tmi.isViewAllowed()) {
				localTBI = new ToolbarItem();
				localTBI.setName("viewTeam");
				localTBI.setTitle(m_messages.mainMenuManageViewTeam());
				localTBI.setTeamingAction(TeamingAction.VIEW_TEAM_MEMBERS);
				m_teamAndEmailBucket.add(localTBI);
			}
			if (m_tmi.isManageAllowed()) {
				localTBI = new ToolbarItem();
				localTBI.setName("editTeam");
				localTBI.setTitle(m_messages.mainMenuManageEditTeam());
				localTBI.setUrl(m_tmi.getManageUrl());
				localTBI.addQualifier("popup", "true");
				localTBI.addQualifier("popupHeight", "500");
				localTBI.addQualifier("popupWidth",  "600");
				m_teamAndEmailBucket.add(localTBI);
			}
			if (m_tmi.isTeamMeetingAllowed()) {
				localTBI = new ToolbarItem();
				localTBI.setName("meetTeam");
				localTBI.setTitle(m_messages.mainMenuManageStartTeamConference());
				localTBI.setUrl(m_tmi.getTeamMeetingUrl());
				localTBI.addQualifier("popup", "true");
				localTBI.addQualifier("popupHeight", "500");
				localTBI.addQualifier("popupWidth",  "600");
			}
			if (m_tmi.isSendMailAllowed()) {
				localTBI = new ToolbarItem();
				localTBI.setName("mailTeam");
				localTBI.setTitle(m_messages.mainMenuManageEmailTeam());
				localTBI.setUrl(m_tmi.getSendMailUrl());
				localTBI.addQualifier("popup", "true");
				localTBI.addQualifier("popupHeight", "500");
				localTBI.addQualifier("popupWidth",  "600");
				m_teamAndEmailBucket.add(localTBI);
			}
		}
		if (null != m_emailContributorsTBI) m_teamAndEmailBucket.add(m_emailContributorsTBI);
		
		// ...then the miscellaneous section...
		if (null != m_trackPersonTBI) m_miscBucket.add(m_trackPersonTBI);
		if (null != m_trackBinderTBI) m_miscBucket.add(m_trackBinderTBI);
		if (null != m_shareThisTBI)   m_miscBucket.add(m_shareThisTBI);
		
		// ...and finally, the configuration section.
		if (null != m_brandingTBI) {
			m_configBucket.add(m_brandingTBI);
		}
		addNestedItemFromUrl(m_configBucket, m_commonActionsTBI, "configure_definitions");
		addNestedItemFromUrl(m_configBucket, m_commonActionsTBI, "config_email");
		addNestedItemFromUrl(m_configBucket, m_commonActionsTBI, "configure_access_control");
		
		// When all is said and done, where going to render anything
		// that's left in the menus from the server at the bottom of
		// the manage menu.  There are certain, known items that we
		// don't want to render here.  The following will see to it
		// that they're ignored by removing them from the appropriate
		// lists.
		addNestedItemFromUrl(m_ignoreBucket, m_commonActionsTBI, "site_administration");	// This is handled in the masthead.
		addNestedItemFromUrl(m_ignoreBucket, m_commonActionsTBI, "binder_report");			// This is in the views menu.
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
	 * Implements the MenuBarPopupBase.setToolbarItemList() abstract
	 * method.
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
	 * Implements the MenuBarPopupBase.shouldShowMenu() abstract
	 * method.
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
					m_commonActionsTBI = new ToolbarItem();
					copyNestedToolbarItems(m_commonActionsTBI, categoriesTBI.getNestedToolbarItem(null));
					copyNestedToolbarItems(m_commonActionsTBI, categoriesTBI.getNestedToolbarItem("addBinder"));
					copyNestedToolbarItems(m_commonActionsTBI, categoriesTBI.getNestedToolbarItem("configuration"));
					copyNestedToolbarItems(m_commonActionsTBI, categoriesTBI.getNestedToolbarItem("folders"));
					copyNestedToolbarItems(m_commonActionsTBI, categoriesTBI.getNestedToolbarItem("workspace"));
				}
			}
			
			else if (tbName.equalsIgnoreCase("ssFolderViewsToolbar")) {
				ToolbarItem displayStylesTBI = tbi.getNestedToolbarItem("display_styles");
				categoriesTBI = ((null == displayStylesTBI) ? null : displayStylesTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_folderViewsTBI = categoriesTBI.getNestedToolbarItem("folderviews");
				}
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
				m_brandingTBI          = tbi.getNestedToolbarItem("branding");
				m_configureColumnsTBI  = tbi.getNestedToolbarItem("configureColumns");
				m_emailContributorsTBI = tbi.getNestedToolbarItem("sendEmail");
				m_shareThisTBI         = tbi.getNestedToolbarItem("share");
				m_trackBinderTBI       = tbi.getNestedToolbarItem("track");
				m_trackPersonTBI       = tbi.getNestedToolbarItem("trackPerson");
			}
		}
		
		// Return true if we found any of the manage menu items and
		// false otherwise.
		boolean reply =
			((null != m_configureColumnsTBI)                                                 ||
			 (null != m_brandingTBI)                                                         ||
			 (null != m_emailContributorsTBI)                                                ||
			 (null != m_emailNotificationTBI)                                                ||
			 (null != m_shareThisTBI)                                                        ||
			 (null != m_trackBinderTBI)                                                      ||
			 (null != m_trackPersonTBI)                                                      ||
			((null != m_calendarImportTBI)   && m_calendarImportTBI.hasNestedToolbarItems()) ||
			((null != m_commonActionsTBI)    && m_commonActionsTBI.hasNestedToolbarItems())  ||
			((null != m_folderViewsTBI)      && m_folderViewsTBI.hasNestedToolbarItems(2)));
		if (reply) {
			fillBuckets();
		}
		return reply;
	}

	/*
	 * If there are any folder options, adds a folder options menu item
	 * that will run the folder options dialog.
	 */
	private void showFolderOptions(final ToolbarItem folderViewsTBI, final ToolbarItem calendarImportTBI) {
		// If there aren't any folder options...
		if ((!(hasNestedItems(folderViewsTBI, 2))) &&
			(!(hasNestedItems(calendarImportTBI))) &&
			(null == m_configureColumnsTBI)) {
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
					false,	// false -> Don't auto hide.
					true,	// true  -> Modal.
					m_menuLeft,
					m_menuTop,
					m_currentBinder.getBinderId(),
					m_configureColumnsTBI,
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
	 * Implements the MenuBarPopupBase.showPopup() abstract method.
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
			// No!  We need to construct it now.  First the actions
			// section.
			addContextMenuItemsFromList(IDBASE, m_actionsBucket);
			
			// Then the team and email section...
			boolean hasTeamAndEmailSection = (!(m_teamAndEmailBucket.isEmpty()));
			if (hasTeamAndEmailSection && isSpacerNeeded()) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}
			addContextMenuItemsFromList(IDBASE, m_teamAndEmailBucket);
			
			// Then the miscellaneous section...
			boolean hasMiscSection = (!(m_miscBucket.isEmpty()));
			if (!hasMiscSection) {
				hasMiscSection = m_currentBinder.isBinderWorkspace();
			}
			if (hasMiscSection && isSpacerNeeded()) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}
			showTagThis();
			addContextMenuItemsFromList(IDBASE, m_miscBucket);
			
			// Then the config section...
			boolean hasConfigSection = (!(m_configBucket.isEmpty()));
			if (hasConfigSection && isSpacerNeeded()) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}
			addContextMenuItemsFromList(IDBASE, m_configBucket);
			addContextMenuItem(IDBASE, m_emailNotificationTBI);
			showFolderOptions(m_folderViewsTBI, m_calendarImportTBI);

			// Finally, a section containing anything that's left over.
			boolean hasLeftOversSection = ((null != m_commonActionsTBI) && m_commonActionsTBI.hasNestedToolbarItems());
			if (hasLeftOversSection && isSpacerNeeded()) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}
			addNestedContextMenuItems(IDBASE, m_commonActionsTBI);
		}
					
		// Finally, show the popup.
		show();
	}
	
	/*
	 * Adds a tag this menu item when appropriate.
	 */
	private void showTagThis() {
		// Is the current binder a folder?
		String menuText;
		final String dlgCaption;
		if (m_currentBinder.isBinderFolder()) {
			// Yes!  Define the menu and dialog labels to use.
			menuText = m_messages.mainMenuManageTagThisFolder();
			dlgCaption   = m_messages.mainMenuTagThisDlgHeaderFolder();
		}
		
		// No, the current binder isn't a folder!  Is it a workspace?
		else if (m_currentBinder.isBinderWorkspace()) {
			// Yes!  For certain types of workspaces, we don't show the
			// tag this menu item.  Is this one of the ones we don't?
			switch (m_currentBinder.getWorkspaceType()) {
			case PROFILE_ROOT:
			case TRASH:
			case OTHER:
			case NOT_A_WORKSPACE:
				// Yes!  Bail.
				return;
			}
			
			// Define the menu and dialog labels to use.
			menuText = m_messages.mainMenuManageTagThisWorkspace();
			dlgCaption   = m_messages.mainMenuTagThisDlgHeaderWorkspace();
		}
		
		else {
			// No, it isn't a workspace either!  We don't show a
			// tag this menu item on it.
			return;
		}

		// Add an anchor to run the tag this dialog.
		final String menuId = (IDBASE + "TagThis");
		MenuPopupAnchor mtA = new MenuPopupAnchor(menuId, menuText, null, new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Remove the selection from the menu item...
				Element menuItemElement = Document.get().getElementById(menuId);
				menuItemElement.removeClassName("mainMenuPopup_ItemHover");
				
				// ...hide the menu...
				hide();

				// ...and run the tag this dialog.
				GwtTeaming.getRpcService().getBinderTags(m_currentBinder.getBinderId(), new AsyncCallback<List<TagInfo>>() {
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							m_messages.rpcFailure_GetBinderTags(),
							m_currentBinder.getBinderId());
					}
					public void onSuccess(final List<TagInfo> binderTags) {
						GwtTeaming.getRpcService().canManagePublicBinderTags(m_currentBinder.getBinderId(), new AsyncCallback<Boolean>() {
							public void onFailure(Throwable t) {
								GwtClientHelper.handleGwtRPCFailure(
									m_messages.rpcFailure_CanManagePublicBinderTags(),
									m_currentBinder.getBinderId());
							}
							public void onSuccess(Boolean isPublicTagManager) {
								TagThisDlg ttDlg = new TagThisDlg(
									false,	// false -> Don't auto hide.
									true,	// true  -> Modal.
									m_actionTrigger,
									m_menuLeft,
									m_menuTop,
									m_currentBinder,
									binderTags,
									((null != isPublicTagManager) && isPublicTagManager.booleanValue()),
									dlgCaption);
								ttDlg.addStyleName("tagThisDlg");
								ttDlg.show();
							}
						});
					}
				});
			}
		});
		addContentWidget(mtA);
	}
}
