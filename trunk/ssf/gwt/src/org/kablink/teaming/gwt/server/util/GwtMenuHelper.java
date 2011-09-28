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
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
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


/**
 * Helper methods for the GWT UI server code that services menu bar
 * requests.
 *
 * @author drfoster@novell.com
 */
public class GwtMenuHelper {
	protected static Log m_logger = LogFactory.getLog(GwtMenuHelper.class);

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
		tbiList.add(constructFolderViewsItems(      WebKeys.FOLDER_VIEWS_TOOLBAR,       bs, request, folder, EntityType.folder));
		tbiList.add(constructFolderActionsItems(    WebKeys.FOLDER_ACTIONS_TOOLBAR,     bs, request, folder, EntityType.folder));		
		tbiList.add(constructCalendarImportItems(   WebKeys.CALENDAR_IMPORT_TOOLBAR,    bs, request, folder                   ));		
		tbiList.add(constructWhatsNewItems(         WebKeys.WHATS_NEW_TOOLBAR,          bs, request, folder, EntityType.folder));
		tbiList.add(constructEmailSubscriptionItems(WebKeys.EMAIL_SUBSCRIPTION_TOOLBAR, bs, request, folder, EntityType.folder));
	}
	
	/*
	 * Adds the ToolBarItem's common to all binder types to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on GwtUIHelper.buildGwtMiscToolbar()
	 */
	private static void buildMiscMenuItems(AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType, List<ToolbarItem> tbiList) {
//!		...this needs to be implemented...
	}
	
	/*
	 * Adds the ToolBarItem's for the profiles binder to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on ProfilesBinderHelper.buildViewFolderToolbars()
	 */
	private static void buildProfilesMenuItems(AllModulesInjected bs, HttpServletRequest request, ProfileBinder pb, List<ToolbarItem> tbiList) {
		tbiList.add(constructFolderItems(       WebKeys.FOLDER_TOOLBAR,         bs, request, pb, EntityType.profiles));
		tbiList.add(constructFolderActionsItems(WebKeys.FOLDER_ACTIONS_TOOLBAR, bs, request, pb, EntityType.profiles));
		tbiList.add(constructWhatsNewItems(     WebKeys.WHATS_NEW_TOOLBAR,      bs, request, pb, EntityType.profiles));
	}
	
	/*
	 * Adds the ToolBarItem's for a workspace to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on WorkspaceTreeHelper.buildWorkspaceToolbar()
	 */
	private static void buildWorkspaceMenuItems(AllModulesInjected bs, HttpServletRequest request, Workspace ws, List<ToolbarItem> tbiList) {
		tbiList.add(constructFolderItems(       WebKeys.FOLDER_TOOLBAR,         bs, request, ws, EntityType.workspace));
		tbiList.add(constructWhatsNewItems(     WebKeys.WHATS_NEW_TOOLBAR,      bs, request, ws, EntityType.workspace));
		tbiList.add(constructFolderActionsItems(WebKeys.FOLDER_ACTIONS_TOOLBAR, bs, request, ws, EntityType.workspace));
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
			ToolbarItem calTBI  = new ToolbarItem("calendar"  );
			ToolbarItem catTBI  = new ToolbarItem("categories");
			ToolbarItem calTBI2 = new ToolbarItem("calendar"  );
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
			markTBITitle(importTBI, importFromFile                );
			markTBIEvent(importTBI, TeamingEvents.IMPORT_ICAL_FILE);
			calTBI2.addNestedItem(importTBI);
			
			importTBI = new ToolbarItem(importType + ".Url");
			markTBITitle(importTBI, importByURL                  );
			markTBIEvent(importTBI, TeamingEvents.IMPORT_ICAL_URL);
			calTBI2.addNestedItem(importTBI);
		}		
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}
	
	/*
	 * Constructs a ToolbarItem for email subscription handling.
	 */
	private static ToolbarItem constructEmailSubscriptionItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);
		
//!		...this needs to be implemented...		
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}
	
	/*
	 * Constructs a ToolbarItem for folder actions.
	 */
	private static ToolbarItem constructFolderActionsItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);
		
//!		...this needs to be implemented...		
		
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
		ToolbarItem addTBI    = new ToolbarItem(                "addBinder"     );
		ToolbarItem adminTBI  = new ToolbarItem(                "administration");		
		ToolbarItem catTBI    = new ToolbarItem(                "categories"    );
		ToolbarItem configTBI = new ToolbarItem(isFolder ? "" : "configuration" );
		ToolbarItem reportsTBI = new ToolbarItem(               "reports"       );
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
				
				url = new AdaptedPortletURL(request, "ss_forum", true);
				url.setParameter(WebKeys.ACTION,        WebKeys.ACTION_ADD_BINDER);
				url.setParameter(WebKeys.URL_BINDER_ID, binderIdS                );
				url.setParameter(WebKeys.URL_OPERATION, (isFolder ? WebKeys.OPERATION_ADD_SUB_FOLDER : WebKeys.OPERATION_ADD_FOLDER));
				
				actionTBI = new ToolbarItem("addFolder");
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
				
				url = new AdaptedPortletURL(request, "ss_forum", true);
				url.setParameter(WebKeys.ACTION,        WebKeys.ACTION_ADD_BINDER);
				url.setParameter(WebKeys.URL_BINDER_ID, binderIdS                );
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_WORKSPACE);
				
				actionTBI = new ToolbarItem("addWorkspace");
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
				if (bs.getBinderModule().testAccess(binder, BinderOperation.moveBinder)) {
					// Yes!  Add a ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
					
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER);
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
					url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()           );
					url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_MOVE      );
					
					actionTBI = new ToolbarItem("move");
					markTBIPopup(actionTBI                                                                         );
					markTBITitle(actionTBI, (isFolder ? "toolbar.menu.move_folder" : "toolbar.menu.move_workspace"));
					markTBIUrl(  actionTBI, url                                                                    );
					
					configTBI.addNestedItem(actionTBI);
				}

				// Yes!  Do they have rights to copy it?
				if (bs.getBinderModule().testAccess(binder, BinderOperation.copyBinder)) {
					// Yes!  Add a ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
					
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER);
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
					url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()           );
					url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_COPY      );
					
					actionTBI = new ToolbarItem("copy");
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
			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()           );
			url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_MODIFY    );
			
			actionTBI = new ToolbarItem("modify");
			markTBIPopup(actionTBI                                                                             );
			markTBITitle(actionTBI, (isFolder ? "toolbar.menu.modify_folder" : "toolbar.menu.modify_workspace"));
			markTBIUrl(  actionTBI, url                                                                        );
			
			configTBI.addNestedItem(actionTBI);

			// ...then a configure ToolbarItem.
			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_CONFIGURE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                           );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()                   );
			
			actionTBI = new ToolbarItem("configureDefinitions");
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
			
			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,          (isFolder ? WebKeys.ACTION_BINDER_REPORTS : WebKeys.ACTION_ACTIVITY_REPORT));
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                                                                  );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()                                                          );
			
			actionTBI = new ToolbarItem("reports");
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

			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MANAGE_DEFINITIONS);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                        );
			
			actionTBI = new ToolbarItem("manageDefinitions");
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

			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()           );
			url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_DELETE    );
			
			actionTBI = new ToolbarItem("delete");
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
	
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_MODIFY_BINDER                 );
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                                    );
					url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()                            );
					url.setParameter(WebKeys.URL_OPERATION,   WebKeys.OPERATION_SYNCHRONIZE_MIRRORED_FOLDER);
					
					actionTBI = new ToolbarItem("manualSync");
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
	
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_SCHEDULE_SYNCHRONIZATION);
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                              );
					
					actionTBI = new ToolbarItem("scheduleSync");
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

			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_EXPORT_IMPORT);
			url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                   );
			url.setParameter(WebKeys.URL_SHOW_MENU,   "true"                      );
			
			actionTBI = new ToolbarItem("importExport");
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

				url = new AdaptedPortletURL(request, "ss_forum", true);
				url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_CONFIG_EMAIL);
				url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                  );
				
				actionTBI = new ToolbarItem("configEmail");
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

			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,            WebKeys.ACTION_ACCESS_CONTROL         );
			url.setParameter(WebKeys.URL_WORKAREA_ID,   String.valueOf(binder.getWorkAreaId()));
			url.setParameter(WebKeys.URL_WORKAREA_TYPE, binder.getWorkAreaType()              );
			
			actionTBI = new ToolbarItem("accessControl");
			markTBIPopup(actionTBI                              );
			markTBITitle(actionTBI, "toolbar.menu.accessControl");
			markTBIUrl(  actionTBI, url                         );
			
			configTBI.addNestedItem(actionTBI);
		}
		
		// Does the user have rights to view who has access to this
		// binder?
		if (!(ObjectKeys.GUEST_USER_INTERNALID.equals(GwtServerHelper.getCurrentUser().getInternalId()))) {
			// Yes!  Add the ToolbarItem for it.
			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,            WebKeys.ACTION_ACCESS_CONTROL         );
			url.setParameter(WebKeys.URL_WORKAREA_ID,   String.valueOf(binder.getWorkAreaId()));
			url.setParameter(WebKeys.URL_WORKAREA_TYPE, binder.getWorkAreaType()              );
			url.setParameter(WebKeys.URL_OPERATION,     WebKeys.OPERATION_VIEW_ACCESS         );
			
			actionTBI = new ToolbarItem("whohasaccess");
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
	private static ToolbarItem constructFolderViewsItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);
		
//!		...this needs to be implemented...		
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}
	
	/*
	 * Constructs a ToolbarItem for What's New handling.
	 */
	private static ToolbarItem constructWhatsNewItems(String tbKey, AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType) {
		// Allocate the base ToolbarItem to return;
		ToolbarItem reply = new ToolbarItem(tbKey);
		
//!		...this needs to be implemented...
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
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
			if (!(ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()))) {
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
						adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
						adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER);
						adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId);
						adapterUrl.setParameter(WebKeys.URL_BINDER_TYPE, binder.getEntityType().name());
						reply.setManageUrl(adapterUrl.toString());
					}
		
					// ...if the user can send mail to the team...
					if (MiscUtil.hasString(user.getEmailAddress())) {
						// ...store the send mail URL...
						adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
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
	private static void markTBIUrl(ToolbarItem tbi, AdaptedPortletURL url) {
		tbi.setUrl(url.toString());
	}
}
