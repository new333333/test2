/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.MenuIds;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.FolderOptionsDlg.FolderOptionsDlgClient;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetCanManageBinderTagsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.ContextBinderProvider;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg;
import org.kablink.teaming.gwt.client.widgets.TagThisDlg.TagThisDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class used for the Manage menu item popup.  
 * 
 * @author drfoster@novell.com
 */
public class ManageMenuPopup extends MenuBarPopupBase {
	private BinderInfo			m_currentBinder;		// The currently selected binder.
	private List<ToolbarItem>	m_actionsBucket;		// List of action         items for the context based menu.
	private List<ToolbarItem>	m_configBucket;			// List of configuration  items for the context based menu.
	private List<ToolbarItem>	m_ignoreBucket;			// List of ignored        items for the context based menu.
	private List<ToolbarItem>	m_miscBucket;			// List of miscellaneous  items for the context based menu.
	private List<ToolbarItem>	m_primaryBucket;		// List of primary        items for the context based menu.
	private List<ToolbarItem>	m_teamAndEmailBucket;	// List of team and email items for the context based menu.
	private List<ToolbarItem>	m_toolbarItemList;		// The context based toolbar requirements.
	private List<ToolbarItem>	m_filrBucket;			// List of Filr specific  items for the context based menu.
	private TagThisDlg			m_tagThisDlg;			// Instance of a TagThisDlg.
	private TeamManagementInfo	m_tmi;					// The team management information for which team management menu items should appear on the menu.
	private ToolbarItem			m_brandingTBI;			// The branding                  toolbar item, if found.
	private ToolbarItem			m_calendarImportTBI;	// The calendar import           toolbar item, if found.
	private ToolbarItem			m_commonActionsTBI;		// The common actions            toolbar item, if found.
	private ToolbarItem			m_emailContributorsTBI;	// The email contributors        toolbar item, if found.
	private ToolbarItem			m_emailNotificationTBI;	// The email notification        toolbar item, if found.
	private ToolbarItem			m_folderViewsTBI;		// The folder views              toolbar item, if found.
	private ToolbarItem			m_shareThisTBI;			// The share this                toolbar item, if found.
	private ToolbarItem			m_trackBinderTBI;		// The binder tracking           toolbar item, if found.
	private ToolbarItem			m_trackPersonTBI;		// The person tracking           toolbar item, if found.
	private ToolbarItem			m_trashTBI;				// The trash                     toolbar item, if found.
	private ToolbarItem			m_wsShareRightsTBI;		// The workspace share rights    toolbar item, if found.
	
	private final static String IDBASE = "manage_";	// Base ID for the items created in this menu.

	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ManageMenuPopup(ContextBinderProvider binderProvider) {
		// Simply initialize the super class.
		super(binderProvider);
	}

	/*
	 * Scans the nested items of a toolbar looking for one whose URL
	 * contains a specific event.  If it is found, it is added to
	 * the bucket.
	 * 
	 * Returns true if the event was found and added to the bucket
	 * and false otherwise.
	 */
	private boolean addNestedItemFromEvent(List<ToolbarItem> bucket, ToolbarItem tbi, TeamingEvents event) {
		// If we don't have a toolbar to search or it has no nested
		// items...
		List<ToolbarItem> tbiList = ((null == tbi) ? null : tbi.getNestedItemsList());
		if ((null == tbiList) || tbiList.isEmpty()) {
			// ...bail.
			return false;
		}

		// Scan the nested items.
		for (ToolbarItem nestedTBI:  tbiList) {
			// Does the nested item contain the event that we're
			// looking for?
			if (event.equals(nestedTBI.getTeamingEvent())) {
				// Yes!  Add it to the bucket and return true.
				tbiList.remove(nestedTBI);
				bucket.add(nestedTBI);
				return true;
			}
		}
		
		// If we get here, we didn't find the requested event.  Return
		// false.
		return false;
	}
	
	/*
	 * Scans the nested items of a toolbar looking for one whose URL
	 * contains a specific action.  If it is found, it is added to
	 * the bucket.
	 * 
	 * Returns true if the action was found and added to the bucket
	 * and false otherwise.
	 */
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
		for (ToolbarItem nestedTBI:  tbiList) {
			// Does this nested item contain a URL?
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
	
	private boolean addNestedItemFromUrl(List<ToolbarItem> bucket, ToolbarItem tbi, String action) {
		// Always use the initial form of the method.
		return addNestedItemFromUrl(bucket, tbi, action, null);
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
				for (ToolbarItem tbiIT:  nestedSrcTBIList) {
					// ...and store them in the destination's nested
					// ...List<ToolbarItem>.
					tbiDest.addNestedItem(tbiIT);
				}
			}
		}
	}

	/*
	 * Called to process the various menu items into ordered lists of
	 * items that appear in the various sections of the menu.
	 */
	private void fillBuckets() {
		// Allocate the bucket lists.
		m_actionsBucket      = new ArrayList<ToolbarItem>();
		m_configBucket       = new ArrayList<ToolbarItem>();
		m_filrBucket         = new ArrayList<ToolbarItem>();
		m_ignoreBucket       = new ArrayList<ToolbarItem>();
		m_miscBucket         = new ArrayList<ToolbarItem>();
		m_primaryBucket      = new ArrayList<ToolbarItem>();
		m_teamAndEmailBucket = new ArrayList<ToolbarItem>();

		// File the buckets in the order things will appear in the
		// menu.  Start with the actions section...
		if (null != m_shareThisTBI) {
			m_primaryBucket.add(m_shareThisTBI);
		}
		if (null != m_wsShareRightsTBI) {
			m_primaryBucket.add(m_wsShareRightsTBI);
		}
		addNestedItemFromUrl(  m_actionsBucket, m_commonActionsTBI, "add_binder",    "add_folder"            );
		addNestedItemFromUrl(  m_actionsBucket, m_commonActionsTBI, "add_binder",    "add_subFolder"         );
		addNestedItemFromUrl(  m_actionsBucket, m_commonActionsTBI, "add_binder",    "add_workspace"         );
		addNestedItemFromUrl(  m_actionsBucket, m_commonActionsTBI, "modify_binder", "modify"                );
		addNestedItemFromEvent(m_actionsBucket, m_commonActionsTBI, TeamingEvents.INVOKE_RENAME_ENTITY       );
		addNestedItemFromEvent(m_actionsBucket, m_commonActionsTBI, TeamingEvents.DELETE_SELECTED_ENTITIES   );
		addNestedItemFromEvent(m_actionsBucket, m_commonActionsTBI, TeamingEvents.COPY_SELECTED_ENTITIES     );
		addNestedItemFromEvent(m_actionsBucket, m_commonActionsTBI, TeamingEvents.MOVE_SELECTED_ENTITIES     );
		addNestedItemFromEvent(m_actionsBucket, m_commonActionsTBI, TeamingEvents.ZIP_AND_DOWNLOAD_FOLDER    );
		addNestedItemFromEvent(m_actionsBucket, m_commonActionsTBI, TeamingEvents.DOWNLOAD_FOLDER_AS_CSV_FILE);
		addNestedItemFromEvent(m_actionsBucket, m_commonActionsTBI, TeamingEvents.MARK_FOLDER_CONTENTS_READ  );
		addNestedItemFromEvent(m_actionsBucket, m_commonActionsTBI, TeamingEvents.MARK_FOLDER_CONTENTS_UNREAD);
		addNestedItemFromUrl(  m_actionsBucket, m_commonActionsTBI, "export_import"                          );
		addNestedItemFromUrl(  m_actionsBucket, m_commonActionsTBI, "manage_binder_quota"                    );
		addNestedItemFromUrl(  m_actionsBucket, m_commonActionsTBI, "manage_definitions"                     );
		addNestedItemFromUrl(  m_actionsBucket, m_commonActionsTBI, "configure_configuration"                );
		
		// ...then the team section...
		if ((null != m_tmi) && m_tmi.isTeamManagementEnabled()) {
			// Add the team management items.
			ToolbarItem localTBI;
			if (m_tmi.isViewAllowed()) {
				localTBI = new ToolbarItem(MenuIds.MANAGE_VIEW_TEAM);
				localTBI.setTitle(m_messages.mainMenuManageViewTeam());
				localTBI.setTeamingEvent(TeamingEvents.VIEW_CURRENT_BINDER_TEAM_MEMBERS);
				m_teamAndEmailBucket.add(localTBI);
			}
			if (m_tmi.isManageAllowed()) {
				localTBI = new ToolbarItem(MenuIds.MANAGE_EDIT_TEAM);
				localTBI.setTitle(m_messages.mainMenuManageEditTeam());
				localTBI.setUrl(m_tmi.getManageUrl());
				localTBI.addQualifier("popup", "true");
				localTBI.addQualifier("popupHeight", String.valueOf(TeamManagementInfo.POPUP_HEIGHT));
				localTBI.addQualifier("popupWidth",  String.valueOf(TeamManagementInfo.POPUP_WIDTH ));
				m_teamAndEmailBucket.add(localTBI);
			}
			if (m_tmi.isTeamMeetingAllowed()) {
				localTBI = new ToolbarItem(MenuIds.MANAGE_MEET_TEAM);
				localTBI.setTitle(m_messages.mainMenuManageStartTeamConference());
				localTBI.setUrl(m_tmi.getTeamMeetingUrl());
				localTBI.addQualifier("popup", "true");
				localTBI.addQualifier("popupHeight", String.valueOf(TeamManagementInfo.POPUP_HEIGHT));
				localTBI.addQualifier("popupWidth",  String.valueOf(TeamManagementInfo.POPUP_WIDTH ));
				m_teamAndEmailBucket.add(localTBI);
			}
			if (m_tmi.isSendMailAllowed()) {
				localTBI = new ToolbarItem(MenuIds.MANAGE_MAIL_TEAM);
				localTBI.setTitle(m_messages.mainMenuManageEmailTeam());
				localTBI.setUrl(m_tmi.getSendMailUrl());
				localTBI.addQualifier("popup", "true");
				localTBI.addQualifier("popupHeight", String.valueOf(TeamManagementInfo.POPUP_HEIGHT));
				localTBI.addQualifier("popupWidth",  String.valueOf(TeamManagementInfo.POPUP_WIDTH ));
				m_teamAndEmailBucket.add(localTBI);
			}
		}
		if (null != m_emailContributorsTBI) m_teamAndEmailBucket.add(m_emailContributorsTBI);
		
		// ...then the miscellaneous section...
		if (null != m_trackPersonTBI) m_miscBucket.add(m_trackPersonTBI);
		if (null != m_trackBinderTBI) m_miscBucket.add(m_trackBinderTBI);
		
		// ...then the configuration section...
		if (null != m_brandingTBI) {
			m_configBucket.add(m_brandingTBI);
		}
		addNestedItemFromUrl(m_configBucket, m_commonActionsTBI, "configure_definitions");
		addNestedItemFromUrl(m_configBucket, m_commonActionsTBI, "config_email");
		addNestedItemFromUrl(m_configBucket, m_commonActionsTBI, "configure_access_control");
		
		// ...and finally, the Filr section.
		if (null != m_trashTBI) {
			m_filrBucket.add(m_trashTBI);
		}
		
		// When all is said and done, we're going to render anything
		// that's left in the menus from the server at the bottom of
		// the manage menu.  There are certain, known items that we
		// don't want to render here.  The following will see to it
		// that they're ignored by removing them from the appropriate
		// lists.
		
		// Site administration is handled in the masthead.
		addNestedItemFromUrl(m_ignoreBucket, m_commonActionsTBI, "site_administration");
		
		// In Vibe mode...
		if (!(GwtClientHelper.isLicenseFilr())) {
			// ...this is in the views menu.
			addNestedItemFromUrl(m_ignoreBucket, m_commonActionsTBI, "binder_report");
		}
	}
	
	/*
	 * Returns true if a toolbar item has nested toolbar items and
	 * false otherwise.
	 */
	private boolean hasNestedItems(ToolbarItem tbi, int atLeast) {
		return ((null == tbi) ? false : tbi.hasNestedToolbarItems(atLeast));
	}
	
	private boolean hasNestedItems(ToolbarItem tbi) {
		// Always use the initial form of the method.
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
		for (ToolbarItem tbi:  m_toolbarItemList) {
			// ...and keep track of the ones that appear on the manage
			// ...menu.
			String tbName = tbi.getName();
			if (tbName.equalsIgnoreCase("ssFolderToolbar")) {
				ToolbarItem adminTBI = tbi.getNestedToolbarItem("administration");
				categoriesTBI = ((null == adminTBI) ? null : adminTBI.getNestedToolbarItem("categories"));
				if (null != categoriesTBI) {
					m_commonActionsTBI = new ToolbarItem(MenuIds.MANAGE_COMMON);
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
				m_brandingTBI          = tbi.getNestedToolbarItem("branding"     );
				m_emailContributorsTBI = tbi.getNestedToolbarItem("sendEmail"    );
				m_shareThisTBI         = tbi.getNestedToolbarItem("share"        );
				m_trackBinderTBI       = tbi.getNestedToolbarItem("track"        );
				m_trackPersonTBI       = tbi.getNestedToolbarItem("trackPerson"  );
				m_trashTBI             = tbi.getNestedToolbarItem("trash"        );
				m_wsShareRightsTBI     = tbi.getNestedToolbarItem("wsShareRights");
			}
		}
		
		// Return true if we found any of the manage menu items and
		// false otherwise.
		boolean reply =
			((null != m_brandingTBI)                                                         ||
			 (null != m_emailContributorsTBI)                                                ||
			 (null != m_emailNotificationTBI)                                                ||
			 (null != m_shareThisTBI)                                                        ||
			 (null != m_trackBinderTBI)                                                      ||
			 (null != m_trackPersonTBI)                                                      ||
			 (null != m_wsShareRightsTBI)                                                    ||
			((null != m_calendarImportTBI)   && m_calendarImportTBI.hasNestedToolbarItems()) ||
			((null != m_commonActionsTBI)    && m_commonActionsTBI.hasNestedToolbarItems())  ||
			((null != m_trashTBI)            && GwtClientHelper.isLicenseFilr())             ||
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
			(!(hasNestedItems(calendarImportTBI)))) {
			// ...bail.
			return;
		}

		// Add an anchor to run the folder options dialog.
		final String foId = (IDBASE + "FolderOptions");
		MenuPopupAnchor mtA = new MenuPopupAnchor(foId, m_messages.mainMenuManageFolderOptions(), null, new Command() {
			@Override
			public void execute() {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						showFolderOptionsAsync(foId, folderViewsTBI, calendarImportTBI);
					}
				});
			}
		});
		addContentMenuItem(mtA);
	}
	
	private void showFolderOptionsAsync(String foId, ToolbarItem folderViewsTBI, ToolbarItem calendarImportTBI) {
		// Run the folder options dialog.
		final int x = getRelativeX();
		final int y = getRelativeY();
		FolderOptionsDlg.createAsync(
				false,	// false -> Don't auto hide.
				true,	// true  -> Modal.
				x, y,
				m_currentBinder.getBinderId(),
				((null == calendarImportTBI) ? null : calendarImportTBI.getNestedItemsList()),
				((null == folderViewsTBI)    ? null : folderViewsTBI.getNestedItemsList()),
			new FolderOptionsDlgClient() {					
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(FolderOptionsDlg dlg) {
					// ...and run the folder options dialog.
					dlg.addStyleName("folderOptionsDlg");
					dlg.show((0 == x) && (0 == y));
				}
			});
	}
	
	/**
	 * Completes construction of the menu.
	 * 
	 * Implements the MenuBarPopupBase.populateMenu() abstract method.
	 */
	@Override
	public void populateMenu() {
		// Begin populating the menu.
		populateMenuPart1();
	}

	/*
	 * Begins the menu construction.
	 */
	private void populateMenuPart1() {
		// Have we constructed the menu's contents yet?
		if (!(hasContent())) {
			// No!  We need to construct it now.  First the primary
			// section.
			addContextMenuItemsFromList(IDBASE, m_primaryBucket);
			
			// Then the actions section.
			boolean hasPrimarySection = (!(m_primaryBucket.isEmpty()));
			if (hasPrimarySection && isSpacerNeeded()) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}
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
			boolean needsMiscSpacer = (hasMiscSection && isSpacerNeeded());
			if (GwtClientHelper.isLicenseFilr()) {
				if (needsMiscSpacer) {
					// ...and add a spacer when required.
					addSpacerMenuItem();
				}
				populateMenuPart2Now();
			}
			else {
				// Note that this will call populateMenuPart2Async()
				// after performing its asynchronous operations.
				showTagThis(needsMiscSpacer);
			}
		}
	}
	
	/*
	 * Asynchronously completes the menu construction.
	 */
	private void populateMenuPart2Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateMenuPart2Now();
			}
		});
	}
	
	/*
	 * Synchronously completes the menu construction.
	 */
	private void populateMenuPart2Now() {
		// Add the items from the miscellaneous bucket.
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

		// Then the Filr section...
		boolean hasFilrSection = (GwtClientHelper.isLicenseFilr() && (!(m_filrBucket.isEmpty())));
		if (hasFilrSection) {
			if (isSpacerNeeded()) {
				// ...and add a spacer when required.
				addSpacerMenuItem();
			}
			addContextMenuItemsFromList(IDBASE, m_filrBucket);
		}
		
		// Finally, a section containing anything that's left over.
		boolean hasLeftOversSection = ((null != m_commonActionsTBI) && m_commonActionsTBI.hasNestedToolbarItems());
		if (hasLeftOversSection && isSpacerNeeded()) {
			// ...and add a spacer when required.
			addSpacerMenuItem();
		}
		addNestedContextMenuItems(IDBASE, m_commonActionsTBI);
	}
	
	/*
	 * Adds a tag this menu item when appropriate.
	 */
	private void showTagThis(final boolean needsSpacer) {
		// Is the current binder a folder?
		final String menuText;
		final String dlgCaption;
		
		if (m_currentBinder.isBinderFolder()) {
			// Yes!  Define the menu and dialog labels to use.
			menuText   = m_messages.mainMenuManageTagThisFolder();
			dlgCaption = m_messages.mainMenuTagThisDlgHeaderFolder();
		}
		
		// No, the current binder isn't a folder!  Is it a workspace?
		else if (m_currentBinder.isBinderWorkspace()) {
			// Yes!  For certain types of workspaces, we don't show the
			// tag this menu item.  Is this one of the ones we don't?
			switch (m_currentBinder.getWorkspaceType()) {
			case PROFILE_ROOT:
			case PROFILE_ROOT_MANAGEMENT:
			case NOT_A_WORKSPACE:
				// Yes!  Bail.
				populateMenuPart2Async();
				return;
			}
			
			// Define the menu and dialog labels to use.
			menuText   = m_messages.mainMenuManageTagThisWorkspace();
			dlgCaption = m_messages.mainMenuTagThisDlgHeaderWorkspace();
		}
		
		else {
			// No, it isn't a workspace either!  We don't show a
			// tag this menu item on it.
			populateMenuPart2Async();
			return;
		}

		// Does the user have rights to manage tags on this binder?
		final Long binderId = m_currentBinder.getBinderIdAsLong();
		GetCanManageBinderTagsCmd cmd = new GetCanManageBinderTagsCmd(binderId);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetCanManageBinderTags(),
					binderId);
				populateMenuPart2Async();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				BooleanRpcResponseData reply = ((BooleanRpcResponseData) response.getResponseData());
				if (reply.getBooleanValue()) {
					// Yes!  Add an anchor to run the tag this dialog.
					final String menuId = (IDBASE + "TagThis");
					MenuPopupAnchor mtA = new MenuPopupAnchor(menuId, menuText, null, new Command() {
						@Override
						public void execute() {
							GwtClientHelper.deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									showTagThisAsync(menuId, dlgCaption);
								}
							});
						}
					});
					if (needsSpacer) {
						// ...and add a spacer when required.
						addSpacerMenuItem();
					}
					addContentMenuItem(mtA);
				}
				populateMenuPart2Async();
			}
		});
	}

	private void showTagThisAsync(String menuId, String dlgCaption) {
		// Show the tag this dialog.
		final int x = getRelativeX();
		final int y = getRelativeY();
		if (null == m_tagThisDlg) {
			TagThisDlg.createAsync(
					false,	// false -> Don't auto hide.
					true,	// true  -> Modal.
					null,
					x, y,
					dlgCaption,
					new TagThisDlgClient() {						
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(TagThisDlg dlg) {
					m_tagThisDlg = dlg;
					showTagThisNow();
				}
			});
		}
		
		else {
			showTagThisNow();
		}
	}
	
	private void showTagThisNow() {
		TagThisDlg.initAndShow(
			m_tagThisDlg,
			m_currentBinder.getBinderId(),
			m_currentBinder.getBinderTitle(),
			m_currentBinder.getBinderType());
	}
	
	/**
	 * Callback interface to interact with the manage menu popup
	 * asynchronously after it loads. 
	 */
	public interface ManageMenuPopupClient {
		void onSuccess(ManageMenuPopup mmp);
		void onUnavailable();
	}

	/**
	 * Loads the ManageMenuPopup split point and returns an
	 * instance of it via the callback.
	 *
	 * @param binderProvider
	 * @param mmpClient
	 */
	public static void createAsync(final ContextBinderProvider binderProvider, final ManageMenuPopupClient mmpClient) {
		GWT.runAsync(ManageMenuPopup.class, new RunAsyncCallback() {			
			@Override
			public void onSuccess() {
				ManageMenuPopup mmp = new ManageMenuPopup(binderProvider);
				mmpClient.onSuccess(mmp);
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_ManageMenuPopup());
				mmpClient.onUnavailable();
			}
		});
	}
}
