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
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.GwtUIHelper.TrackInfo;
import org.kablink.util.BrowserSniffer;


/**
 * Helper methods for the GWT UI server code that services menu bar
 * requests.
 *
 * @author drfoster@novell.com
 */
public class GwtMenuHelper {
	protected static Log m_logger = LogFactory.getLog(GwtMenuHelper.class);

	private final static String ABOUT					= "about";
	private final static String ACCESS_CONTROL			= "accessControl";
	private final static String ADD_BINDER				= "addBinder";
	private final static String ADD_FOLDER				= "addFolder";
	private final static String ADD_WORKSPACE			= "addWorkspace";
	private final static String ADMINISTRATION			= "administration";
	private final static String BRANDING				= "branding";
	private final static String CALENDAR				= "calendar";
	private final static String CATEGORIES				= "categories";
	private final static String CLIPBOARD				= "clipboard";
	private final static String CONFIGURATION			= "configuration";
	private final static String CONFIGURE_COLUMNS		= "configureColumns";
	private final static String CONFIGURE_DEFINITIONS	= "configureDefinitions";
	private final static String CONFIGURE_EMAIL			= "configEmail";
	private final static String COPY					= "copy";
	private final static String DELETE					= "delete";
	private final static String DISPLAY_STYLES			= "display_styles";
	private final static String EMAIL					= "email";
	private final static String FOLDER_VIEWS			= "folderViews";
	private final static String IMPORT_EXPORT			= "importExport";
	private final static String MANAGE_DEFINITIONS		= "manageDefinitions";
	private final static String MANUAL_SYNC				= "manualSync";
	private final static String MOBILE_UI				= "mobileUI";
	private final static String MODIFY					= "modify";
	private final static String MOVE					= "move";
	private final static String REPORTS					= "reports";
	private final static String SCHEDULE_SYNC			= "scheduleSync";
	private final static String SEND_EMAIL				= "sendEmail";
	private final static String SHARE					= "share";
	private final static String SS_FORUM				= "ss_forum";
	private final static String TRASH					= "trash";
	private final static String UNSEEN					= "unseen";
	private final static String WHATS_NEW				= "whatsnew";
	private final static String WHO_HAS_ACCESS			= "whohasaccess";

	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtMenuHelper() {
		// Nothing to do.
	}

	/*
	 * Adds the ToolBarItem's for a folder to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on ListFolderHelper.buildFolderToolbars()
	 */
	private static void buildFolderMenuItems(AllModulesInjected bs, HttpServletRequest request, Folder folder, List<ToolbarItem> tbiList) {
		tbiList.add(constructFolderItems(           WebKeys.FOLDER_TOOLBAR,             bs, request, folder, EntityType.folder));
		tbiList.add(constructFolderViewsItems(      WebKeys.FOLDER_VIEWS_TOOLBAR,       bs, request, folder                   ));
		tbiList.add(constructCalendarImportItems(   WebKeys.CALENDAR_IMPORT_TOOLBAR,    bs, request, folder                   ));		
		tbiList.add(constructWhatsNewItems(         WebKeys.WHATS_NEW_TOOLBAR,          bs, request, folder, EntityType.folder));
		tbiList.add(constructEmailSubscriptionItems(WebKeys.EMAIL_SUBSCRIPTION_TOOLBAR, bs, request, folder                   ));
	}
	
	/*
	 * Adds the ToolBarItem's common to all binder types to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on GwtUIHelper.buildGwtMiscToolbar()
	 */
	private static void buildMiscMenuItems(AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType, List<ToolbarItem> tbiList) {
		// Generate the GWT miscellaneous ToolbarItem.
		ToolbarItem miscTBI = new ToolbarItem(WebKeys.GWT_MISC_TOOLBAR);
		tbiList.add(miscTBI);

		// Add the about to it.
		miscTBI.addNestedItem(constructAboutItem());
		
		// Do we have a binder and are we running as other than
		// guest?
		if ((null != binder) && (!(GwtServerHelper.getCurrentUser().isShared()))) {
			// Yes!  Add a ToolbarItem for editing its branding.
			miscTBI.addNestedItem(constructEditBrandingItem(bs, binder, binderType));

			// Is this other than the profiles binder?
			if (EntityIdentifier.EntityType.profiles != binder.getEntityType()) {
				// Yes!  Add the various binder based
				// ToolbarItem's.
				miscTBI.addNestedItem( constructClipboardItem()                 );
				miscTBI.addNestedItem( constructConfigureColumsItem(     binder));
				miscTBI.addNestedItem( constructSendEmailToItem()               );
				miscTBI.addNestedItem( constructShareBinderItem(request, binder));
				miscTBI.addNestedItem( constructMobileUiItem(   request, binder));
				miscTBI.addNestedItems(constructTrackBinderItem(bs,      binder));
				miscTBI.addNestedItem( constructTrashItem(      request, binder));
			}
		}
	}
	
	/*
	 * Adds the ToolBarItem's for the profiles binder to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on ProfilesBinderHelper.buildViewFolderToolbars()
	 */
	private static void buildProfilesMenuItems(AllModulesInjected bs, HttpServletRequest request, ProfileBinder pb, List<ToolbarItem> tbiList) {
		tbiList.add(constructFolderItems(WebKeys.FOLDER_TOOLBAR, bs, request, pb, EntityType.profiles));
	}
	
	/*
	 * Adds the ToolBarItem's for a workspace to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on WorkspaceTreeHelper.buildWorkspaceToolbar()
	 */
	private static void buildWorkspaceMenuItems(AllModulesInjected bs, HttpServletRequest request, Workspace ws, List<ToolbarItem> tbiList) {
		tbiList.add(constructFolderItems(  WebKeys.FOLDER_TOOLBAR,    bs, request, ws, EntityType.workspace));
		tbiList.add(constructWhatsNewItems(WebKeys.WHATS_NEW_TOOLBAR, bs, request, ws, EntityType.workspace));
	}
	
	/*
	 * Constructs a ToolbarItem for About.
	 */
	private static ToolbarItem constructAboutItem() {
		ToolbarItem aboutTBI = new ToolbarItem(ABOUT);
		markTBITitle(aboutTBI, "misc.about");
		markTBIEvent(aboutTBI, TeamingEvents.INVOKE_ABOUT);
		return aboutTBI;
	}
	
	/*
	 * Constructs a ToolbarItem for calendar imports. to the
	 */
	private static ToolbarItem constructCalendarImportItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);

		// Are we looking at a calendar or task folder that we can add entries to?
		boolean isCalendar =                   BinderHelper.isBinderCalendar(folder);
		boolean isTask     = ((!isCalendar) && BinderHelper.isBinderTask(    folder));
		if ((isCalendar || isTask) && bs.getFolderModule().testAccess(folder, FolderOperation.addEntry)) {
			// Yes!  Generate the required structure for the menu.
			ToolbarItem calTBI  = new ToolbarItem(CALENDAR  );
			ToolbarItem catTBI  = new ToolbarItem(CATEGORIES);
			ToolbarItem calTBI2 = new ToolbarItem(CALENDAR  );
			reply.addNestedItem( calTBI );			
			calTBI.addNestedItem(catTBI );			
			catTBI.addNestedItem(calTBI2);
			
			// Load the localized strings we need for the menu items...
			String importFromFile;
			String importByURL;
			String importType;
			if (isCalendar) {
				importFromFile = "toolbar.menu.calendarImport.fromFile";
				importByURL    = "toolbar.menu.calendarImport.byURL";
				importType     = "calendar";
			}
			
			else {
				importFromFile = "toolbar.menu.taskImport.fromFile";
				importByURL    = "toolbar.menu.taskImport.byURL";				
				importType     = "task";
			}
			
			// ...and generate the items.
			ToolbarItem importTBI = new ToolbarItem(importType + ".File");
			markTBITitle(importTBI, importFromFile                       );
			markTBIEvent(importTBI, TeamingEvents.INVOKE_IMPORT_ICAL_FILE);
			calTBI2.addNestedItem(importTBI);
			
			importTBI = new ToolbarItem(importType + ".Url");
			markTBITitle(importTBI, importByURL                         );
			markTBIEvent(importTBI, TeamingEvents.INVOKE_IMPORT_ICAL_URL);
			calTBI2.addNestedItem(importTBI);
		}		
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}

	/*
	 * Constructs a ToolbarItem to run the clipboard dialog.
	 */
	private static ToolbarItem constructClipboardItem() {
		ToolbarItem cbTBI = new ToolbarItem(CLIPBOARD);
		markTBITitle(cbTBI, "toolbar.menu.clipboard"      );
		markTBIEvent(cbTBI, TeamingEvents.INVOKE_CLIPBOARD);
		return cbTBI;
	}

	/*
	 * Constructs a ToolbarItem to run the configure columns dialog.
	 */
	private static ToolbarItem constructConfigureColumsItem(Binder binder) {
		// Can the user configure columns on this binder?
		String viewType = DefinitionUtils.getViewType(binder);
		if (MiscUtil.hasString(viewType)) {
			if (viewType.equalsIgnoreCase("folder") ||
					viewType.equalsIgnoreCase("table") ||
					viewType.equalsIgnoreCase("file")) {
				// Yes!  Create a configure columns ToolbarItem.
				ToolbarItem ccTBI = new ToolbarItem(CONFIGURE_COLUMNS);
				markTBITitle(ccTBI, "misc.configureColumns"               );
				markTBIEvent(ccTBI, TeamingEvents.INVOKE_CONFIGURE_COLUMNS);
				return ccTBI;
			}
		}
		return null;
	}
	
	/*
	 * Constructs a ToolbarItem to run the branding editor on a binder.
	 */
	private static ToolbarItem constructEditBrandingItem(AllModulesInjected bs, Binder binder, EntityType binderType) {
		if (bs.getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
			String menuKey = "toolbar.menu.brand.";
			if      (EntityType.workspace == binderType) menuKey += "workspace";
			else if (EntityType.folder    == binderType) menuKey += "folder";
			else                                         return null;
			ToolbarItem ebTBI = new ToolbarItem(BRANDING);
			markTBITitle(ebTBI, menuKey                                   );
			markTBIEvent(ebTBI, TeamingEvents.EDIT_CURRENT_BINDER_BRANDING);
			return ebTBI;
		}
		return null;
	}
	
	/*
	 * Constructs a ToolbarItem for email subscription handling.
	 */
	private static ToolbarItem constructEmailSubscriptionItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);

		// Is the current user the guest user?
		if (!(GwtServerHelper.getCurrentUser().isShared())) {
			// No!  Add a ToolbarItem for email notification.
			ToolbarItem emailTBI = new ToolbarItem(EMAIL);
			markTBITitle(emailTBI, "toolbar.menu.subscribeToFolder"       );
			markTBIHint(emailTBI, "toolbar.menu.title.emailSubscriptions" );
			markTBIEvent(emailTBI, TeamingEvents.INVOKE_EMAIL_NOTIFICATION);
			reply.addNestedItem(emailTBI);
		}
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}
	
	/*
	 * Constructs a ToolbarItem for folders.
	 */
	@SuppressWarnings("unused")
	private static ToolbarItem constructFolderItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);

		// Define some locals to work with.
		AdaptedPortletURL url;
		AdminModule  am = bs.getAdminModule();
		BinderModule bm = bs.getBinderModule();
		boolean isFolder            = (EntityType.folder    == binderType);
		boolean isProfiles          = (EntityType.profiles  == binderType);
		boolean isWorkspace         = (EntityType.workspace == binderType);
		boolean isWorkspaceReserved = (isWorkspace && binder.isReserved());
		boolean isWorkspaceRoot     = (isWorkspace && binder.isRoot());
		Long binderId = binder.getId();
		String binderIdS = String.valueOf(binderId);
		ToolbarItem actionTBI;
		
		// Generate the required structure for the menu.
		ToolbarItem addTBI    = new ToolbarItem(                ADD_BINDER    );
		ToolbarItem adminTBI  = new ToolbarItem(                ADMINISTRATION);		
		ToolbarItem catTBI    = new ToolbarItem(                CATEGORIES    );
		ToolbarItem configTBI = new ToolbarItem(isFolder ? "" : CONFIGURATION );
		ToolbarItem reportsTBI = new ToolbarItem(               REPORTS       );
		adminTBI.addNestedItem(catTBI);

		// Is the binder a folder or workspace other than the root
		// workspace?
		boolean addMenuCreated     = false;
		boolean adminMenuCreated   = false;
		boolean configMenuCreated  = false;
		boolean reportsMenuCreated = false;
		if ((isFolder || isWorkspace) && (!isWorkspaceRoot)) {
			// Yes!  Does the user have rights add a new folder to this
			// binder?
			if (bm.testAccess(binder, BinderOperation.addFolder)) {
				// Yes!  Add a ToolbarItem for it.
				addMenuCreated   =
				adminMenuCreated = true;
				
				url = createActionUrl(request);
				url.setParameter(WebKeys.ACTION,        WebKeys.ACTION_ADD_BINDER);
				url.setParameter(WebKeys.URL_BINDER_ID, binderIdS                );
				url.setParameter(WebKeys.URL_OPERATION, (isFolder ? WebKeys.OPERATION_ADD_SUB_FOLDER : WebKeys.OPERATION_ADD_FOLDER));
				
				actionTBI = new ToolbarItem(ADD_FOLDER);
				markTBIPopup(actionTBI                          );
				markTBITitle(actionTBI, "toolbar.menu.addFolder");
				markTBIUrl(  actionTBI, url                     );
				
				addTBI.addNestedItem(actionTBI);
			}

			// Does the user have rights to add a new workspace to this
			// binder?
			if (isWorkspace && bm.testAccess(binder, BinderOperation.addWorkspace)) {
				// Yes!  Add a ToolbarItem for it.
				addMenuCreated   =
				adminMenuCreated = true;
				
				url = createActionUrl(request);
				url.setParameter(WebKeys.ACTION,        WebKeys.ACTION_ADD_BINDER);
				url.setParameter(WebKeys.URL_BINDER_ID, binderIdS                );
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
				
				actionTBI = new ToolbarItem(ADD_WORKSPACE);
				markTBIPopup(actionTBI                             );
				markTBITitle(actionTBI, "toolbar.menu.addWorkspace");
				markTBIUrl(  actionTBI, url                        );
				
				addTBI.addNestedItem(actionTBI);
			}
		}
		
		// Is the binder a folder or workspace other than a reserved
		// workspace?
		if ((isFolder || isWorkspace) && (!isWorkspaceReserved)) {			
			// Yes!  Can the user potentially copy or move this binder?
			boolean allowCopyMove;
			if (isWorkspace) {
				Integer wsDefType = binder.getDefinitionType();
				allowCopyMove = ((null == wsDefType) || (Definition.USER_WORKSPACE_VIEW != wsDefType.intValue()));
			}
			else {
				allowCopyMove = true;
			}
			if (allowCopyMove) {
				// Yes!  Do they have rights to move it?
				if (bm.testAccess(binder, BinderOperation.moveBinder)) {
					// Yes!  Add a ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
					
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER);
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
					url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()           );
					url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_MOVE      );
					
					actionTBI = new ToolbarItem(MOVE);
					markTBIPopup(actionTBI                                                                         );
					markTBITitle(actionTBI, (isFolder ? "toolbar.menu.move_folder" : "toolbar.menu.move_workspace"));
					markTBIUrl(  actionTBI, url                                                                    );
					
					configTBI.addNestedItem(actionTBI);
				}

				// Yes!  Do they have rights to copy it?
				if (bm.testAccess(binder, BinderOperation.copyBinder)) {
					// Yes!  Add a ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
					
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER);
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
					url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()           );
					url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_COPY      );
					
					actionTBI = new ToolbarItem(COPY);
					markTBIPopup(actionTBI                                                                         );
					markTBITitle(actionTBI, (isFolder ? "toolbar.menu.copy_folder" : "toolbar.menu.copy_workspace"));
					markTBIUrl(  actionTBI, url                                                                    );
					
					configTBI.addNestedItem(actionTBI);
				}
			}
		}
		
		// Does the user have rights modify this binder?
		if (bm.testAccess(binder, BinderOperation.modifyBinder)) {
			// Yes!  Add the ToolBarItem's for it.
			adminMenuCreated  =
			configMenuCreated = true;

			// First, a modify ToolbarItem...
			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()           );
			url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_MODIFY    );
			
			actionTBI = new ToolbarItem(MODIFY);
			markTBIPopup(actionTBI                                                                             );
			markTBITitle(actionTBI, (isFolder ? "toolbar.menu.modify_folder" : "toolbar.menu.modify_workspace"));
			markTBIUrl(  actionTBI, url                                                                        );
			
			configTBI.addNestedItem(actionTBI);

			// ...then a configure ToolbarItem.
			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_CONFIGURE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                           );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()                   );
			
			actionTBI = new ToolbarItem(CONFIGURE_DEFINITIONS);
			markTBIPopup(actionTBI                              );
			markTBITitle(actionTBI, "toolbar.menu.configuration");
			markTBIUrl(  actionTBI, url                         );
			
			configTBI.addNestedItem(actionTBI);
		}

		// Does the user have rights to generate a report on this binder?
		if ((isFolder || isWorkspace) && bm.testAccess(binder, BinderOperation.report)) {
			// Yes!  Add the ToolBarItem for it.
			adminMenuCreated = true;
			if (isFolder)
			     configMenuCreated  = true;
			else reportsMenuCreated = true;
			
			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,          (isFolder ? WebKeys.ACTION_BINDER_REPORTS : WebKeys.ACTION_ACTIVITY_REPORT));
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                                                                  );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()                                                          );
			
			actionTBI = new ToolbarItem(REPORTS);
			markTBIPopup(actionTBI                        );
			markTBITitle(actionTBI, "toolbar.menu.reports");
			markTBIUrl(  actionTBI, url                   );

			if (isFolder)
			      configTBI.addNestedItem(actionTBI);
			else reportsTBI.addNestedItem(actionTBI);
		}
		
		// Does the user have rights to manage the definitions on this
		// binder?
		if ((isFolder || isWorkspace) && bm.testAccess(binder, BinderOperation.manageConfiguration)) {
			// Yes!  Add the ToolbarItem for it.
			adminMenuCreated  =
			configMenuCreated = true;

			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MANAGE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                        );
			
			actionTBI = new ToolbarItem(MANAGE_DEFINITIONS);
			markTBIPopup(actionTBI                                               );
			markTBITitle(actionTBI, "administration.definition_builder_designers");
			markTBIUrl(  actionTBI, url                                          );
			
			configTBI.addNestedItem(actionTBI);
		}
		
		// Does the user have rights to delete this binder?
		if ((isFolder || isWorkspace) && (!isWorkspaceReserved) && bm.testAccess(binder, BinderOperation.deleteBinder)) {
			// Yes!  Add the ToolbarItem for it.
			adminMenuCreated  =
			configMenuCreated = true;

			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()           );
			url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_DELETE    );
			
			actionTBI = new ToolbarItem(DELETE);
			markTBIPopup(actionTBI                                                                             );
			markTBITitle(actionTBI, (isFolder ? "toolbar.menu.delete_folder" : "toolbar.menu.delete_workspace"));
			markTBIUrl(  actionTBI, url                                                                        );
			
			configTBI.addNestedItem(actionTBI);
		}

		// Is this a mirrored folder?
		if (isFolder && LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder")) {
			Folder folder = ((Folder) binder);
			if (folder.isMirrored() && (null != folder.getResourceDriverName())) {
				// Yes!  Does this user have rights to manually
				// synchronize this mirrored folder?
				FolderModule fm = bs.getFolderModule();
				if (fm.testAccess(folder, FolderOperation.synchronize)) {
					// Yes!  Add the ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
	
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER                 );
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                                    );
					url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()                            );
					url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_SYNCHRONIZE_MIRRORED_FOLDER);
					
					actionTBI = new ToolbarItem(MANUAL_SYNC);
					markTBISpinner(actionTBI                                                   );
					markTBITitle(  actionTBI, "toolbar.menu.synchronize_mirrored_folder.manual");
					markTBIUrl(    actionTBI, url                                              );
					
					configTBI.addNestedItem(actionTBI);
				}
				
				// Does this user have rights to define a
				// synchronization schedule on this mirrored folder?
				if (fm.testAccess(folder, FolderOperation.scheduleSynchronization)) {
					// Yes!  Add the ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
	
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_SCHEDULE_SYNCHRONIZATION);
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                              );
					
					actionTBI = new ToolbarItem(SCHEDULE_SYNC);
					markTBIPopup(actionTBI                                                      );
					markTBITitle(actionTBI, "toolbar.menu.synchronize_mirrored_folder.scheduled");
					markTBIUrl(  actionTBI, url                                                 );
					
					configTBI.addNestedItem(actionTBI);
				}
			}
		}
		
		// Does the user have rights to import/export this binder?
		if ((isFolder || isWorkspace) && bm.testAccess(binder, BinderOperation.export)) {
			// Yes!  Add the ToolbarItem for it.
			adminMenuCreated  =
			configMenuCreated = true;

			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_EXPORT_IMPORT);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
			url.setParameter(WebKeys.URL_SHOW_MENU,   "true"                      );
			
			actionTBI = new ToolbarItem(IMPORT_EXPORT);
			markTBIPopup(actionTBI                                                                                           );
			markTBITitle(actionTBI, (isFolder ? "toolbar.menu.export_import_folder" : "toolbar.menu.export_import_workspace"));
			markTBIUrl(  actionTBI, url                                                                                      );
			
			configTBI.addNestedItem(actionTBI);
		}
		
		// Does the user have rights to configure email settings on
		// this binder?
		if (isFolder && bm.testAccess(binder, BinderOperation.manageMail)) {
			Folder folder = ((Folder) binder);
			if (folder.isTop() || am.getMailConfig().isPostingEnabled()) {
				// Yes!  Add the ToolbarItem for it.
				adminMenuCreated  =
				configMenuCreated = true;

				url = createActionUrl(request);
				url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_CONFIG_EMAIL);
				url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                  );
				
				actionTBI = new ToolbarItem(CONFIGURE_EMAIL);
				markTBIPopup(actionTBI                                       );
				markTBITitle(actionTBI, "toolbar.menu.configure_folder_email");
				markTBIUrl(  actionTBI, url                                  );
				
				configTBI.addNestedItem(actionTBI);
			}
		}
		
		// Dose the user have rights to manage access controls on this
		// binder?
		if (am.testAccess(binder, AdminOperation.manageFunctionMembership)) {
			// Yes!  Add the ToolbarItem for it.
			adminMenuCreated  =
			configMenuCreated = true;

			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,            WebKeys.ACTION_ACCESS_CONTROL         );
			url.setParameter(WebKeys.URL_WORKAREA_ID,   String.valueOf(binder.getWorkAreaId()));
			url.setParameter(WebKeys.URL_WORKAREA_TYPE, binder.getWorkAreaType()              );
			
			actionTBI = new ToolbarItem(ACCESS_CONTROL);
			markTBIPopup(actionTBI                              );
			markTBITitle(actionTBI, "toolbar.menu.accessControl");
			markTBIUrl(  actionTBI, url                         );
			
			configTBI.addNestedItem(actionTBI);
		}
		
		// Does the user have rights to view who has access to this
		// binder?
		if (!(GwtServerHelper.getCurrentUser().isShared())) {
			// Yes!  Add the ToolbarItem for it.
			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,            WebKeys.ACTION_ACCESS_CONTROL         );
			url.setParameter(WebKeys.URL_WORKAREA_ID,   String.valueOf(binder.getWorkAreaId()));
			url.setParameter(WebKeys.URL_WORKAREA_TYPE, binder.getWorkAreaType()              );
			url.setParameter(WebKeys.URL_OPERATION,     WebKeys.OPERATION_VIEW_ACCESS         );
			
			actionTBI = new ToolbarItem(WHO_HAS_ACCESS);
			markTBIPopup(actionTBI, "600", "700"                                                                                        );
			markTBIHint( actionTBI, (isWorkspace ? "toolbar.menu.title.whoHasAccessWorkspace" : "toolbar.menu.title.whoHasAccessFolder"));
			markTBITitle(actionTBI, "toolbar.whoHasAccess"                                                                              );
			markTBIUrl(  actionTBI, url                                                                                                 );
			
			reply.addNestedItem(actionTBI);
		}

		// Connect any menus we created together.
		if (addMenuCreated)     catTBI.addNestedItem(addTBI    );
		if (configMenuCreated)  catTBI.addNestedItem(configTBI );		
		if (reportsMenuCreated) catTBI.addNestedItem(reportsTBI);		
		if (adminMenuCreated)   reply.addNestedItem( adminTBI  );
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}
	
	/*
	 * Constructs a ToolbarItem for folder views.
	 */
	private static ToolbarItem constructFolderViewsItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);

		// Are there any views defined on this folder?
		@SuppressWarnings("unchecked")
		List<Definition> folderViewDefs = folder.getViewDefinitions();
		if (!(folderViewDefs.isEmpty())) {
			// Yes!  Generate the required structure for the menu.
			ToolbarItem dispStylesTBI = new ToolbarItem(DISPLAY_STYLES);
			ToolbarItem catTBI        = new ToolbarItem(CATEGORIES    );
			ToolbarItem viewsTBI      = new ToolbarItem(FOLDER_VIEWS  );
			reply.addNestedItem(        dispStylesTBI );			
			dispStylesTBI.addNestedItem(catTBI        );
			catTBI.addNestedItem(       viewsTBI      );

			// Does the user have a default view defined on this
			// folder?
			Long folderId = folder.getId();
			UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUser().getId(), folderId);
			String userSelectedDefinition = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION));
			Definition currentDef = folderViewDefs.get(0);
			if (MiscUtil.hasString(userSelectedDefinition)) {
				// Yes!  Scan the defined views.
				for (Definition def:  folderViewDefs) {
					// Is this the user's default view?
					if (userSelectedDefinition.equals(def.getId())) {
						// Yes!  Track it.
						currentDef = def;
						break;
					}
				}
			}

			// Scan the defined views again.
			String folderIdS = String.valueOf(folderId);
			for (Definition def:  folderViewDefs) {
				// Build URL to switch to this view...
				AdaptedPortletURL url = createActionUrl(request);
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING             );
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_SET_DISPLAY_DEFINITION);
				url.setParameter(WebKeys.URL_BINDER_ID, folderIdS                               );
				url.setParameter(WebKeys.URL_VALUE, def.getId()                                 );
				
				// ...and create a ToolbarItem for it.
				ToolbarItem viewTBI = new ToolbarItem(def.getName());
				if (def.equals(currentDef)) {
					viewTBI.addQualifier(WebKeys.TOOLBAR_MENU_SELECTED, "true");
				}				
				markTBITitle(viewTBI, def.getTitle());
				markTBIUrl(  viewTBI, url           );
				viewsTBI.addNestedItem(viewTBI);
			}
		}
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}

	/*
	 * Constructs a ToolbarItem to run the send email to team members
	 * dialog. 
	 */
	private static ToolbarItem constructSendEmailToItem() {
		User user = GwtServerHelper.getCurrentUser();
		if (MiscUtil.hasString(user.getEmailAddress()) && (!(user.isShared()))) {
			ToolbarItem sendEmailToTBI = new ToolbarItem(SEND_EMAIL             );
			markTBITitle(sendEmailToTBI, "toolbar.menu.sendMail"                );
			markTBIEvent(sendEmailToTBI, TeamingEvents.INVOKE_SEND_EMAIL_TO_TEAM);
			return sendEmailToTBI;
		}
		return null;
	}

	/*
	 * Constructs a ToolbarItem to run the share binder dialog.
	 */
	private static ToolbarItem constructShareBinderItem(HttpServletRequest request, Binder binder) {
		// Create the base ToolbarItem for the share binder...
		ToolbarItem sbTBI = new ToolbarItem(SHARE);
		markTBIPopup(sbTBI, "550", "750");
		markTBITitle(sbTBI, GwtUIHelper.buildRelevanceKey(binder, "relevance.shareThis"));
		
		// ...generate the URL to share it...
		AdaptedPortletURL url = new AdaptedPortletURL(request, SS_FORUM, true);
		url.setParameter(WebKeys.ACTION,        "__ajax_relevance"            );
		url.setParameter(WebKeys.URL_OPERATION, "share_this_binder"           );
		url.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(binder.getId()));
		markTBIUrl(sbTBI, url);
		
		// ...and return the item.
		return sbTBI;
	}
	
	/*
	 * Constructs a ToolbarItem to change the UI to mobile.
	 */
	private static ToolbarItem constructMobileUiItem(HttpServletRequest request, Binder binder) {
		// Are we running in a mobile browser?
		String userAgents = org.kablink.teaming.util.SPropsUtil.getString("mobile.userAgents", "");
		if (BrowserSniffer.is_mobile(request, userAgents)) {
			// Yes!  Construct a URL to go to the mobile UI...
			AdaptedPortletURL url = new AdaptedPortletURL(request, SS_FORUM, true, true);
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_MOBILE_SHOW_MOBILE_UI);

			// ...generate a ToolbarItem for it...
			ToolbarItem mobileTBI = new ToolbarItem(MOBILE_UI);
			markTBITitle(mobileTBI, "toolbar.menu.mobileUI"         );
			markTBIEvent(mobileTBI, TeamingEvents.GOTO_PERMALINK_URL);
			markTBIUrl(  mobileTBI, url                             );
			
			// ...and return it.
			return mobileTBI;
		}
		return null;
	}
	
	/*
	 * Returns a List<ToolbarItem> of track binder items required
	 * for the binder.
	 */
	private static List<ToolbarItem> constructTrackBinderItem(AllModulesInjected bs, Binder binder) {
		List<ToolbarItem> tbTBIs = new ArrayList<ToolbarItem>();
		List<TrackInfo> tiList = GwtUIHelper.getTrackInfoList(bs, binder);
		for (TrackInfo ti:  tiList) {
			ToolbarItem tbTBI = new ToolbarItem(ti.m_tbName);
			markTBITitle(tbTBI, ti.m_resourceKey                 );
			markTBIEvent(tbTBI, TeamingEvents.valueOf(ti.m_event));
			tbTBIs.add(tbTBI);
		}
		return tbTBIs;
	}

	/*
	 * Constructs a ToolbarItem to view the trash on an binder.
	 */
	private static ToolbarItem constructTrashItem(HttpServletRequest request, Binder binder) {		
		// Construct a permalink URL to the trash for the binder...
		String binderPermalink = PermaLinkUtil.getPermalink(request, binder);
		String trashPermalink  = GwtUIHelper.getTrashPermalink(binderPermalink);
		
		// ...construct a ToolbarItem for it...
		ToolbarItem trashTBI = new ToolbarItem(TRASH);
		markTBITitle(trashTBI, "toolbar.menu.trash"            );
		markTBIEvent(trashTBI, TeamingEvents.GOTO_PERMALINK_URL);
		markTBIUrl(  trashTBI, trashPermalink                  );

		// ...and return it.
		return trashTBI;
	}
	
	/*
	 * Constructs the ToolbarItem's for What's New handling.
	 */
	private static ToolbarItem constructWhatsNewItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);

		// Is the current user the guest user?
		if (!(GwtServerHelper.getCurrentUser().isShared())) {
			// No!  Is this other than a WIKI folder?
			boolean isFolder = (EntityType.folder == binderType);
			if ((!isFolder) || (!(BinderHelper.isBinderWiki(binder)))) {
				// Yes!  Generate a ToolbarItem for What's New....
				ToolbarItem itemTBI = new ToolbarItem(WHATS_NEW);
				markTBITitle(itemTBI,             "toolbar.menu.whatsNew"                                                          );
				markTBIHint( itemTBI, (isFolder ? "toolbar.menu.title.whatsNewInFolder" : "toolbar.menu.title.whatsNewInWorkspace"));
				markTBIEvent(itemTBI, TeamingEvents.VIEW_WHATS_NEW_IN_BINDER);
				reply.addNestedItem(itemTBI);

				// ...and generate one for Unseen.
				itemTBI = new ToolbarItem(UNSEEN);
				markTBITitle(itemTBI,             "toolbar.menu.whatsUnseen"                                                             );
				markTBIHint( itemTBI, (isFolder ? "toolbar.menu.title.whatsUnreadInFolder" : "toolbar.menu.title.whatsUnreadInWorkspace"));
				markTBIEvent(itemTBI, TeamingEvents.VIEW_WHATS_UNSEEN_IN_BINDER);
				reply.addNestedItem(itemTBI);
			}
		}
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}

	/*
	 * Creates a base URL for sending actions to the server.
	 */
	private static AdaptedPortletURL createActionUrl(HttpServletRequest request) {
		return new AdaptedPortletURL(request, SS_FORUM, true);
	}
	
	/**
	 * Returns a List<ToolbarItem> of the ToolbarItem's for a folder
	 * given the current user's rights to that folder.
	 *
	 * @param bs
	 * @param request
	 * @param folderId
	 * 
	 * @return
	 */
	public static List<ToolbarItem> getFolderToolbarItems(AllModulesInjected bs, HttpServletRequest request, Long folderId) {
		SimpleProfiler.start("GwtMenuHelper.getFolderToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return...
			List<ToolbarItem> reply = new ArrayList<ToolbarItem>();

//!			...this needs to be implemented...
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("GwtMenuHelper.getFolderToolbarItems()");
		}
	}
	
	/**
	 * Returns information about the recent places the current user has
	 * visited.
	 *
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 */
	public static List<RecentPlaceInfo> getRecentPlaces(AllModulesInjected bs, HttpServletRequest request, Long binderId) {
		List<RecentPlaceInfo> reply = new ArrayList<RecentPlaceInfo>();
		
		// Read the current tabs...
		Tabs tabs = Tabs.getTabs(request);
		Binder binder = GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId);
		if (null != binder) {
			// ...use them to fill List<RecentPlaceInfo>... 
			Tabs.TabEntry tab = tabs.findTab(binder, false);
			GwtServerHelper.fillRecentPlacesFromTabs(bs, request, tab.getTabs(), reply);
		}
		
		// ...and return the List<RecentPlaceInfo>.
		return reply;
	}
	
	/**
	 * Returns a TeamManagementInfo object regarding the current user's
	 * team management capabilities.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 */
	public static TeamManagementInfo getTeamManagementInfo(AllModulesInjected bs, HttpServletRequest request, String binderId) {
		SimpleProfiler.start("GwtMenuHelper.getTeamManagementInfo()");
		try {
			// Construct a base TeamManagementInfo object to return.
			TeamManagementInfo reply = new TeamManagementInfo();
			
			// Is the current user the guest user?
			User user = GwtServerHelper.getCurrentUser();
			if (!(user.isShared())) {
				// No!  Is the binder other than the profiles container?
				BinderModule bm = bs.getBinderModule();
				Binder binder = GwtUIHelper.getBinderSafely(bm, binderId);
				if ((null != binder) && (EntityIdentifier.EntityType.profiles != binder.getEntityType())) {				
					// Yes!  Then the user is allowed to view team membership.
					reply.setViewAllowed(true);
		
					// If the user can manage the team...
					AdaptedPortletURL adapterUrl;
					if (bm.testAccess(binder, BinderOperation.manageTeamMembers)) {
						// ...store the team management URL...
						adapterUrl = createActionUrl(request);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId);
						adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
						reply.setManageUrl(adapterUrl.toString());
					}
		
					// ...if the user can send mail to the team...
					if (MiscUtil.hasString(user.getEmailAddress())) {
						// ...store the send mail URL...
						adapterUrl = createActionUrl(request);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId);
						adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
						reply.setSendMailUrl(adapterUrl.toString());
					}
		
					// ...if the user can start a team meeting...
					if (bs.getConferencingModule().isEnabled()) {
						CustomAttribute ca = user.getCustomAttribute("conferencingID");
						if ((null != ca) && MiscUtil.hasString((String)ca.getValue())) {		
							// ...store the team meeting URL.
							try {
								reply.setTeamMeetingUrl(GwtServerHelper.getAddMeetingUrl(bs, request, binderId));
							}
							catch (GwtTeamingException e) {
								// Nothing to do...
							}
						}
					}
				}
			}
	
			// If we get here, reply refers to a TeamManagementInfo
			// object containing the user's team management
			// capabilities.  Return it.
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("GwtMenuHelper.getTeamManagementInfo()");
		}
	}
	
	/**
	 * Returns a List<ToolbarItem> of the ToolbarItem's
	 * applicable for the given context.
	 *
	 * @param bs
	 * @param request
	 * @param binderIdS
	 * 
	 * @return
	 */
	public static List<ToolbarItem> getToolbarItems(AllModulesInjected bs, HttpServletRequest request, String binderIdS) {
		SimpleProfiler.start("GwtMenuHelper.getToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return...
			List<ToolbarItem> reply = new ArrayList<ToolbarItem>();

			// ...add the ToolbarItem's specific to a binder type to
			// ...the list...
			Long binderId = Long.parseLong(binderIdS);
			Binder binder = bs.getBinderModule().getBinder(binderId);
			EntityType binderType = binder.getEntityType();
			switch (binderType) {
			case workspace:  buildWorkspaceMenuItems(bs, request, ((Workspace)     binder), reply); break;
			case folder:     buildFolderMenuItems(   bs, request, ((Folder)        binder), reply); break;
			case profiles:   buildProfilesMenuItems( bs, request, ((ProfileBinder) binder), reply); break;
			}

			// ...and add the ToolbarItem's required by all binder
			// ...types to the list.
			buildMiscMenuItems(bs, request, binder, binderType, reply);
			
			// If we get here, reply refers to the List<ToolbarItem> of
			// the ToolbarItem's for the binder.  Return it.
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("GwtMenuHelper.getToolbarItems()");
		}
	}

	/*
	 * Marks a ToolbarItem's event.
	 */
	private static void markTBIEvent(ToolbarItem tbi, TeamingEvents event) {
		tbi.setTeamingEvent(event);
	}
	
	/*
	 * Marks a ToolbarItem's hint based on a resource key.
	 */
	private static void markTBIHint(ToolbarItem tbi, String key) {
		tbi.addQualifier("title", NLT.get(key));
	}
	
	/*
	 * Marks a ToolbarItem as bringing up a popup window.
	 */
	private static void markTBIPopup(ToolbarItem tbi, String width, String height) {
		tbi.addQualifier("popup", "true");
		if (MiscUtil.hasString(width )) tbi.addQualifier("popupWidth",  width );
		if (MiscUtil.hasString(height)) tbi.addQualifier("popupHeight", height);
	}
	private static void markTBIPopup(ToolbarItem tbi) {
		markTBIPopup(tbi, null, null);
	}
	
	/*
	 * Marks a ToolbarItem as needing to show a spinner.
	 */
	private static void markTBISpinner(ToolbarItem tbi) {
		tbi.addQualifier("showSpinner", "true");
	}
	
	/*
	 * Marks a ToolbarItem's title based on a resource key.
	 */
	private static void markTBITitle(ToolbarItem tbi, String key) {
		tbi.setTitle(NLT.get(key));
	}
	
	/*
	 * Marks a ToolbarItem's URL based on an AdaptedPortletURL.
	 */
	private static void markTBIUrl(ToolbarItem tbi, String url) {
		tbi.setUrl(url);
	}
	
	private static void markTBIUrl(ToolbarItem tbi, AdaptedPortletURL url) {
		markTBIUrl(tbi, url.toString());
	}
}
