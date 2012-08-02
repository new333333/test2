/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.SimpleName.SimpleNamePK;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.event.InvokeSendEmailToTeamEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem.NameValuePair;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CalendarShow;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.GwtUIHelper.TrackInfo;
import org.kablink.teaming.web.util.Tabs.TabEntry;
import org.kablink.util.BrowserSniffer;
import org.kablink.util.search.Constants;

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
	private final static String CONFIGURE_ACCESSORIES	= "configureAccessories";
	private final static String CONFIGURE_COLUMNS		= "configureColumns";
	private final static String CONFIGURE_DEFINITIONS	= "configureDefinitions";
	private final static String CONFIGURE_EMAIL			= "configEmail";
	private final static String COPY					= "copy";
	private final static String DELETE					= "delete";
	private final static String DISPLAY_STYLES			= "display_styles";
	private final static String EMAIL					= "email";
	private final static String FOLDER_VIEWS			= "folderViews";
	private final static String ICALENDAR				= "iCalendar";
	private final static String IMPORT_EXPORT			= "importExport";
	private final static String MANAGE_DEFINITIONS		= "manageDefinitions";
	private final static String MANAGE_TEMPLATES		= "manageTemplates";
	private final static String MANUAL_SYNC				= "manualSync";
	private final static String MOBILE_UI				= "mobileUI";
	private final static String MODIFY					= "modify";
	private final static String MOVE					= "move";
	private final static String PERMALINK				= "permalink";
	private final static String REPORTS					= "reports";
	private final static String SCHEDULE_SYNC			= "scheduleSync";
	private final static String SEND_EMAIL				= "sendEmail";
	private final static String SHARE					= "share";
	private final static String SIMPLE_NAMES			= "simpleNames";
	private final static String SS_FORUM				= "ss_forum";
	private final static String SUBSCRIBE_ATOM			= "subscribeAtom";
	private final static String SUBSCRIBE_RSS			= "subscribeRSS";
	private final static String TRASH					= "trash";
	private final static String UNSEEN					= "unseen";
	private final static String VIEW_AS_WEBDAV			= "viewaswebdav";
	private final static String WEBDAVURL				= "webdavUrl";
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
				miscTBI.addNestedItem( constructClipboardItem()                    );
				miscTBI.addNestedItem( constructSendEmailToItem(    request, binder));
				if (GwtShareHelper.isEntitySharable(bs, binder)) {
					miscTBI.addNestedItem( constructShareBinderItem(request, binder));
				}
				miscTBI.addNestedItem( constructMobileUiItem(       request, binder));
				miscTBI.addNestedItems(constructTrackBinderItem(    bs,      binder));
				miscTBI.addNestedItem( constructTrashItem(          request, binder));
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
		boolean isCalendar =                   BinderHelper.isBinderCalendar(bs, folder);
		boolean isTask     = ((!isCalendar) && BinderHelper.isBinderTask(    bs, folder));
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
	 * Constructs a ToolbarItem to run the branding editor on a binder.
	 */
	private static ToolbarItem constructEditBrandingItem(AllModulesInjected bs, Binder binder, EntityType binderType) {
		if (bs.getBinderModule().testAccess(binder, BinderOperation.modifyBinder)) {
			String menuKey = "toolbar.menu.brand.";
			if      (EntityType.workspace == binderType) menuKey += "workspace";
			else if (EntityType.folder    == binderType) menuKey += "folder";
			else if (EntityType.profiles  == binderType) menuKey += "workspace";
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
	 * Constructs a ToolbarItem for adding a new file folder the
	 * workspace.
	 */
	private static void constructEntryAddFileFolderItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws) {
		// Can the user add a folder to this workspace?
		if (bs.getBinderModule().testAccess(ws, BinderOperation.addFolder)) {
			// Yes!  Can we access any folder templates?
			TemplateModule tm = bs.getTemplateModule();
			List<TemplateBinder> folderTemplates = tm.getTemplates(Definition.FOLDER_VIEW);
			folderTemplates.addAll(tm.getTemplates(Definition.FOLDER_VIEW, ws, true));
			if (folderTemplates.isEmpty()) {
				folderTemplates.add(tm.addDefaultTemplate(Definition.FOLDER_VIEW));
			}
			if ((null != folderTemplates) && (0 < folderTemplates.size())) {
				// Yes!  Scan them.
				Long folderTemplateId = null;
				for (TemplateBinder tb:  folderTemplates) {
					// Yes!  Is this template for a file folder?
					String tbFamily = BinderHelper.getFamilyNameFromDef(tb.getEntryDef());
					if (MiscUtil.hasString(tbFamily) && tbFamily.equalsIgnoreCase(Definition.FAMILY_FILE)) {
						// Yes!  Save its ID.
						folderTemplateId = tb.getId();
						break;
					}
				}

				// Did we find the template ID for a file folder?
				if (null != folderTemplateId) {
					// Yes!  Use the information we've got to add a
					// ToolbarItem to add a new folder.
					ToolbarItem addTBI = new ToolbarItem("1_add");
					markTBITitle(addTBI, "toolbar.new");
				
					ToolbarItem addFolderTBI = new ToolbarItem(ADD_FOLDER);
					markTBITitle(           addFolderTBI, "toolbar.menu.addFolder"           );
					markTBIEvent(           addFolderTBI, TeamingEvents.INVOKE_ADD_NEW_FOLDER);
					markTBIFolderTemplateId(addFolderTBI, folderTemplateId                   );
					addTBI.addNestedItem(addFolderTBI);
					
					entryToolbar.addNestedItem(addTBI);
				}
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem for adding entries to a folder.
	 * 
	 * At the point this gets called, we know that the user has rights
	 * to add entries to the folder.
	 */
	@SuppressWarnings("unchecked")
	private static void constructEntryAddItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		// Read the folder's entry definitions.
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		int defaultEntryDefs = ((null == defaultEntryDefinitions) ? 0 : defaultEntryDefinitions.size());
		
		// Is the folder other than a mirrored folder or is it a
		// mirrored folder that can be written to?
		boolean hasVT = MiscUtil.hasString(viewType);
		AdaptedPortletURL url;
		BinderModule   bm = bs.getBinderModule();
		TemplateModule tm = bs.getTemplateModule();
		if ((!(folder.isMirrored())) || isFolderWritableMirrored(folder)) {
			// Yes!  Define the toolbar items for them.
			ToolbarItem addTBI = new ToolbarItem("1_add");
			markTBITitle(addTBI, "toolbar.new");
			entryToolbar.addNestedItem(addTBI);
			
			// Is this a non-blog folder that user has rights add a new
			// sub-folder to?
			if ((!(isViewBlog(viewType))) && bm.testAccess(folder, BinderOperation.addFolder)) {
				// Yes!  Can we access any folder templates?
				List<TemplateBinder> folderTemplates = tm.getTemplates(Definition.FOLDER_VIEW);
				folderTemplates.addAll(tm.getTemplates(Definition.FOLDER_VIEW, folder, true));
				if (folderTemplates.isEmpty()) {
					folderTemplates.add(tm.addDefaultTemplate(Definition.FOLDER_VIEW));
				}
				if ((null != folderTemplates) && (0 < folderTemplates.size())) {
					// Yes!  Scan them.
					String fedId = folder.getEntryDefId();
					Long folderTemplateId = null;
					for (TemplateBinder tb:  folderTemplates) {
						// Is the the template for this folder?
						if (tb.getEntryDefId().equals(fedId)) {
							// Yes!  Save its ID.
							folderTemplateId = tb.getId();
							break;
						}
					}

					// Did we find the template ID for the folder?
					if (null == folderTemplateId) {
						// No!  Default to the first one in the list.
						folderTemplateId = folderTemplates.get(0).getId();
					}
				
					// Finally, use the information we've got to add a
					// ToolbarItem to add a new folder.
					ToolbarItem addFolderTBI = new ToolbarItem(ADD_FOLDER);
					markTBITitle(           addFolderTBI, "toolbar.menu.addFolder"           );
					markTBIEvent(           addFolderTBI, TeamingEvents.INVOKE_ADD_NEW_FOLDER);
					markTBIFolderTemplateId(addFolderTBI, folderTemplateId                   );
					addTBI.addNestedItem(addFolderTBI);
				}
			}
			
			// Does this folder have more than one entry definition or
			// is for other than a guest book folder?
			if ((1 < defaultEntryDefs) || (!hasVT) || (!(isViewGuestBook(viewType)))) {
				// Yes!  Added items for each entry type.  (Note that
				// we skip this on guest books because they get their
				// own 'Sign the Guest Book' top level menu item.)
				int count = 1;
				int	defaultEntryDefIndex = ListFolderHelper.getDefaultFolderEntryDefinitionIndex(
					RequestContextHolder.getRequestContext().getUser().getId(),
					bs.getProfileModule(),
					folder,
					defaultEntryDefinitions);
				Map<String, Boolean> usedTitles = new HashMap<String, Boolean>();
				for (int i = 0; i < defaultEntryDefinitions.size(); i += 1) {
					Definition def = ((Definition) defaultEntryDefinitions.get(i));
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
					url.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
					url.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					String title = NLT.getDef(def.getTitle());
					if (null != usedTitles.get("title")) {
						title = (title + " (" + String.valueOf(count++) + ")");
					}
					
					ToolbarItem entriesTBI = new ToolbarItem("entries");
					markTBITitleRes(entriesTBI, title);
					markTBIPopup(   entriesTBI       );
					markTBIUrl(     entriesTBI, url  );
					if (i == defaultEntryDefIndex) {
						markTBIDefault(entriesTBI);
					}
					if ((0 == i) && addTBI.hasNestedToolbarItems()) {
						addTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
					}
					addTBI.addNestedItem(entriesTBI);
				}
			}
		}

		if (hasVT) {
			if (isViewPhotoAlbum(viewType)) {
				if (bm.testAccess(folder, BinderOperation.addFolder)) {
					TemplateBinder photoTemplate = tm.getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_PHOTO);
					if (photoTemplate != null) {
						url = createActionUrl(request);
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
		        		url.setParameter(WebKeys.URL_BINDER_ID, getFolderSetFolderId(bs, folder, viewType).toString());
						url.setParameter(WebKeys.URL_TEMPLATE_NAME, ObjectKeys.DEFAULT_TEMPLATE_NAME_PHOTO);
						
						ToolbarItem addTBI = new ToolbarItem("1_add_folder"       );
						markTBITitle(addTBI, "toolbar.menu.add_photo_album_folder");
						markTBIPopup(addTBI                                       );
						markTBIUrl(  addTBI, url                                  );
						entryToolbar.addNestedItem(addTBI);
					}
				}
			}
			
			else if (viewType.equals(Definition.VIEW_STYLE_WIKI)) {
				if (bm.testAccess(folder, BinderOperation.addFolder)) {
					TemplateBinder wikiTemplate = tm.getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_WIKI);
					if (wikiTemplate != null) {
						url = createActionUrl(request);
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
		        		url.setParameter(WebKeys.URL_BINDER_ID, getFolderSetFolderId(bs, folder, viewType).toString());
						url.setParameter(WebKeys.URL_TEMPLATE_NAME, ObjectKeys.DEFAULT_TEMPLATE_NAME_WIKI);
						
						ToolbarItem addTBI = new ToolbarItem("1_add_folder");
						markTBITitle(addTBI, "toolbar.menu.add_wiki_folder");
						markTBIPopup(addTBI                                );
						markTBIUrl(  addTBI, url                           );
						entryToolbar.addNestedItem(addTBI);
					}
				}
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem to configure the accessories panel.
	 */
	private static void constructEntryConfigureAccessories(AllModulesInjected bs, HttpServletRequest request, Binder binder, List<ToolbarItem> configureToolbarItems) {
		// Can the user configure accessories on this binder?
		String viewType = DefinitionUtils.getViewType(binder);
		if ((!(isViewBlog(viewType))) && (!(isViewMiniBlog(viewType)))) {
			// Yes!  Create a configure accessories ToolbarItem.
			boolean hideAccessories;
			try                 {hideAccessories = GwtViewHelper.getAccessoryStatus(bs, request, binder.getId());}
			catch (Exception e) {hideAccessories = false;}
			String titleKey;
			TeamingEvents event;
			if (hideAccessories) {
				titleKey = "misc.hideAccessories";
				event    = TeamingEvents.HIDE_ACCESSORIES;
			}
			else {
				titleKey = "misc.showAccessories";
				event    = TeamingEvents.SHOW_ACCESSORIES;
			}
			ToolbarItem accTBI = new ToolbarItem(CONFIGURE_ACCESSORIES);
			markTBITitle(accTBI, titleKey);
			markTBIEvent(accTBI, event   );
			configureToolbarItems.add(accTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for the add files applet.
	 * 
	 * At the point this gets called, we know the following:
	 * 1. The user has rights to add entries to the folder;
	 * 2. Applets are supported;
	 * 3. The folder is not a mini-blog; and
	 * 4. The folder is not a mirror file folder or it's a configured,
	 *    writable mirrored file folder. 
	 */
	private static void constructEntryDropBoxItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		ToolbarItem dropBoxTBI = new ToolbarItem("dropBox");
		markTBITitle(dropBoxTBI, "toolbar.menu.dropBox.dialog");
		markTBIEvent(dropBoxTBI, TeamingEvents.INVOKE_DROPBOX);
		entryToolbar.addNestedItem(dropBoxTBI);
	}
	
	/*
	 * Constructs a ToolbarItem to run the configure columns dialog.
	 */
	private static ToolbarItem constructEntryConfigureColumsItem(Binder binder) {
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
	 * Constructs a ToolbarItem for deleting the selected entries.
	 */
	private static void constructEntryDeleteItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		// For the view types that support it...
		if (MiscUtil.hasString(viewType)) {
			BinderModule bm = bs.getBinderModule();
			if (folderSupportsDeleteAndPurge(folder, viewType) && (!(folder.isMirrored()))) {
				// ...and for which the user has rights to do it...
				if (bm.testAccess(folder, BinderOperation.deleteEntries)) {
					// ...add a Delete item.
					constructEntryDeleteItem(entryToolbar);
				}
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem for deleting the selected entries.
	 */
	private static void constructEntryDeleteItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws) {
		// If the user has rights to do it...
		if ((null == ws) || bs.getBinderModule().testAccess(ws, BinderOperation.deleteBinder)) {
			// ...add a Delete item.
			constructEntryDeleteItem(entryToolbar);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for deleting the selected entries.
	 */
	private static void constructEntryDeleteItem(ToolbarItem entryToolbar) {
		// Add a Delete item.
		ToolbarItem deleteTBI = new ToolbarItem("1_deleteSelected");
		markTBITitle(deleteTBI, "toolbar.delete");
		markTBIEvent(deleteTBI, TeamingEvents.DELETE_SELECTED_ENTRIES);
		entryToolbar.addNestedItem(deleteTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for running the entry viewer the
	 * selected entry.
	 */
	private static void constructEntryDetailsItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request) {
		// Add a Details item.
		ToolbarItem detailsTBI = new ToolbarItem("1_detailsSelected");
		markTBITitle(detailsTBI, "toolbar.details");
		markTBIEvent(detailsTBI, TeamingEvents.VIEW_SELECTED_ENTRY);
		entryToolbar.addNestedItem(detailsTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for miscellaneous operations against
	 * the selected entries.
	 * 
	 * Menu Items (as per Lynn's prototype):
	 *		Copy...
	 *		Tag... (Future)
	 *		Move...
	 *		Purge
	 *		Lock
	 *		Unlock
	 *		Mark Read
	 *		-------------------
	 *		Change Entry Type...
	 *		Subscribe...
	 *		Access Control... (Future)
	 */
	private static void constructEntryMoreItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Long folderId, String viewType, Folder folder, Workspace ws) {
		User    user     = GwtServerHelper.getCurrentUser();
		boolean isGuest  = ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId());
		boolean isFolder = (null != folder);
		
		// Create the more toolbar item...
		ToolbarItem moreTBI = new ToolbarItem("1_more");
		markTBITitle(moreTBI, "toolbar.more");

		// ...add the copy item...
		ToolbarItem tbi = new ToolbarItem("1_copySelected");
		markTBITitle(tbi, "toolbar.copy");
		markTBIEvent(tbi, TeamingEvents.COPY_SELECTED_ENTRIES);
		moreTBI.addNestedItem(tbi);

		// ...add the move item....
		tbi = new ToolbarItem("1_moveSelected");
		markTBITitle(tbi, "toolbar.move");
		markTBIEvent(tbi, TeamingEvents.MOVE_SELECTED_ENTRIES);
		moreTBI.addNestedItem(tbi);
		
		BinderModule bm = bs.getBinderModule();
		if (isFolder) {
			// ...for the view types that support it...
			if (MiscUtil.hasString(viewType)) {
				if (folderSupportsDeleteAndPurge(folder, viewType)) {
					// ...and for which the user has rights to do it...
					if (bm.testAccess(folder, BinderOperation.deleteEntries)) {
						// ...add the Purge item...
						constructEntryMorePurgeItem(moreTBI);
					}
				}
			}
		}
		else {
			// ...and for which the user has rights to do it...
			if ((null == ws) || bm.testAccess(ws, BinderOperation.deleteBinder)) {
				// ...add the Purge item...
				constructEntryMorePurgeItem(moreTBI);
			}
		}

		// ...for folders...
		if (isFolder) {
			// ...add the lock item....
			tbi = new ToolbarItem("1_lockSelected");
			markTBITitle(tbi, "toolbar.lock");
			markTBIEvent(tbi, TeamingEvents.LOCK_SELECTED_ENTRIES);
			moreTBI.addNestedItem(tbi);
			
			// ...add the unlock item....
			tbi = new ToolbarItem("1_unlockSelected");
			markTBITitle(tbi, "toolbar.unlock");
			markTBIEvent(tbi, TeamingEvents.UNLOCK_SELECTED_ENTRIES);
			moreTBI.addNestedItem(tbi);
			
			// ...if the user is not the Guest user...
			if (!isGuest) {
				// ...add the mark read....
				tbi = new ToolbarItem("1_markReadSelected");
				markTBITitle(tbi, "toolbar.markRead");
				markTBIEvent(tbi, TeamingEvents.MARK_READ_SELECTED_ENTRIES);
				moreTBI.addNestedItem(tbi);
				
				// ...and the mark unread items....
				tbi = new ToolbarItem("1_markUnreadSelected");
				markTBITitle(tbi, "toolbar.markUnread");
				markTBIEvent(tbi, TeamingEvents.MARK_UNREAD_SELECTED_ENTRIES);
				moreTBI.addNestedItem(tbi);
			}
		}
		
		// ...add a separator item...
		if (moreTBI.hasNestedToolbarItems() && (isFolder || (!isGuest))) {
			moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
		}
		
		if (isFolder) {
			// ...add the change entry type item....
			tbi = new ToolbarItem("1_changeEntryTypeSelected");
			markTBITitle(tbi, "toolbar.changeEntryType");
			markTBIEvent(tbi, TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTRIES);
			moreTBI.addNestedItem(tbi);
		}
		
		// ...if the user is not the Guest user...
		if (!isGuest) {
			// ...add the subscribe item.
			tbi = new ToolbarItem("1_subscribeSelected");
			markTBITitle(tbi, "toolbar.menu.subscribeToEntrySelected");
			markTBIEvent(tbi, TeamingEvents.SUBSCRIBE_SELECTED_ENTRIES);
			moreTBI.addNestedItem(tbi);
		}

		// If we added anything to the more toolbar...
		if (!(moreTBI.getNestedItemsList().isEmpty())) {
			// ...and the more toolbar to the entry toolbar.
			entryToolbar.addNestedItem(moreTBI);
		}
	}

	/*
	 * Creates a 'purge' item in the 'more' toolbar.
	 */
	private static void constructEntryMorePurgeItem(ToolbarItem moreTBI) {
		ToolbarItem tbi = new ToolbarItem("1_purgeSelected");
		markTBITitle(tbi, "toolbar.purge");
		markTBIEvent(tbi, TeamingEvents.PURGE_SELECTED_ENTRIES);
		moreTBI.addNestedItem(tbi);
	}
	
	/*
	 * Constructs a ToolbarItem for viewing pinned vs. non-pinned
	 * entries.
	 */
	private static void constructEntryPinnedItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		// Is this a folder that supports pinning entries?
		if ((null != folder) && folderSupportsPinning(folder, viewType)) {
			// Yes!  Add the pinned item.
			ToolbarItem pinnedTBI = new ToolbarItem("1_viewPinned");
			markTBIEvent(pinnedTBI, TeamingEvents.VIEW_PINNED_ENTRIES);
			entryToolbar.addNestedItem(pinnedTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for the root profiles workspace view.
	 * 
	 * The logic for this was copied from
	 * ProfilesBinderHelper.buildViewEntryToolbar().
	 */
	@SuppressWarnings("unchecked")
	private static void constructEntryProfilesRootWSItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws) {
		// If the user can add entries...
		ProfileModule pm = bs.getProfileModule();
		if (pm.testAccess(((ProfileBinder) ws), ProfileOperation.addEntry)) {
			// ...and we can find the entry definition...
			List defaultEntryDefinitions = ws.getEntryDefinitions();
			if (MiscUtil.hasItems(defaultEntryDefinitions)) {
				// ...add the 'new user' option (only one option
				// ...available.)
				Definition def = ((Definition) defaultEntryDefinitions.get(0));
				AdaptedPortletURL url = createActionUrl(request);
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY);
				url.setParameter(WebKeys.URL_BINDER_ID, ws.getId().toString());
				url.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				ToolbarItem addUserTBI = new ToolbarItem("1_add");
				markTBITitle(    addUserTBI, "toolbar.new");
				markTBIPopup(    addUserTBI               );
				markTBIHighlight(addUserTBI               );
				markTBIUrl(      addUserTBI, url          );
				entryToolbar.addNestedItem(addUserTBI);
			}
		}
		
		// Create a 'more' item for the disable/enable and purge/delete
		// items.
		ToolbarItem tbi;
		ToolbarItem moreTBI = new ToolbarItem("1_more");
		markTBITitle(moreTBI, "toolbar.more");
		
		// If the user can manage entries in the workspace...
		if (pm.testAccess(((ProfileBinder) ws), ProfileOperation.manageEntries)) {
			// ...add the disable item...
			tbi = new ToolbarItem("1_disableSelected");
			markTBITitle(tbi, "toolbar.disable");
			markTBIEvent(tbi, TeamingEvents.DISABLE_SELECTED_USERS);
			moreTBI.addNestedItem(tbi);
			
			// ...and add the enable item.
			tbi = new ToolbarItem("1_enableSelected");
			markTBITitle(tbi, "toolbar.enable");
			markTBIEvent(tbi, TeamingEvents.ENABLE_SELECTED_USERS);
			moreTBI.addNestedItem(tbi);
		}
		
		// If the user can delete binders from the workspace...
		BinderModule bm = bs.getBinderModule();
		boolean canTrash = bm.testAccess(ws, BinderOperation.preDeleteBinder);
		if (canTrash) {
			// ...if needed...
			if (!(moreTBI.getNestedItemsList().isEmpty())) {
				// ...add a separator item...
				moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
			}
			
			// ...and add the delete workspaces item.
			tbi = new ToolbarItem("1_deletedSelectedWS");
			markTBITitle(tbi, "toolbar.delete.workspaces");
			markTBIEvent(tbi, TeamingEvents.DELETE_SELECTED_USER_WORKSPACES);
			moreTBI.addNestedItem(tbi);
		}
			
		// If the user can purge binders from the workspace...
		if (bm.testAccess(ws, BinderOperation.deleteBinder)) {
			// ...if needed...
			if ((!canTrash) && (!(moreTBI.getNestedItemsList().isEmpty()))) {
				// ...add a separator item...
				moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
			}
			
			// ...add the purge workspaces item...
			tbi = new ToolbarItem("1_purgeSelectedWS");
			markTBITitle(tbi, "toolbar.purge.workspaces");
			markTBIEvent(tbi, TeamingEvents.PURGE_SELECTED_USER_WORKSPACES);
			moreTBI.addNestedItem(tbi);
			
			// ...and add the purge users item.
			tbi = new ToolbarItem("1_purgeSelectedUsers");
			markTBITitle(tbi, "toolbar.purge.users");
			markTBIEvent(tbi, TeamingEvents.PURGE_SELECTED_USERS);
			moreTBI.addNestedItem(tbi);
		}
			
		// Finally, if we added anything to the more toolbar...
		if (!(moreTBI.getNestedItemsList().isEmpty())) {
			// ...and the more toolbar to the entry toolbar.
			entryToolbar.addNestedItem(moreTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for sharing the selected entries.
	 */
	private static void constructEntryShareItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		// For the view types that support it...
		if (MiscUtil.hasString(viewType)) {
			if (folderSupportsShare(bs, folder, viewType)) {
				// ...construct the share item.
				constructEntryShareItem(entryToolbar, bs, request);
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem for sharing the selected entries.
	 */
	private static void constructEntryShareItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request) {
		// For non-guest users...
		User    user    = GwtServerHelper.getCurrentUser();
		boolean isGuest = ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId());
		if (!isGuest) {
			// ...add the share item.
			ToolbarItem shareTBI = new ToolbarItem("1_shareSelected");
			markTBITitle(shareTBI, "toolbar.shareSelected");
			markTBIEvent(shareTBI, TeamingEvents.SHARE_SELECTED_ENTRIES);
			entryToolbar.addNestedItem(shareTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for the sign the guest book UI.
	 * 
	 * At the point this gets called, we know that the user has rights
	 * to add entries to the folder.
	 */
	private static void constructEntrySignTheGuestbookItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		ToolbarItem signGuestbookTBI = new ToolbarItem("signGuestbool");
		markTBITitle(signGuestbookTBI, "guestbook.addEntry");
		markTBIEvent(signGuestbookTBI, TeamingEvents.INVOKE_SIGN_GUESTBOOK);
		entryToolbar.addNestedItem(signGuestbookTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for sorting certain folder types.
	 * 
	 * At the point this gets called, we know that the folder is a blog
	 * or photo album, the only two view types that require 'sort by'
	 * items.
	 */
	private static void constructEntrySortByItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		Long folderId = folder.getId();
		UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUserId(), folderId);
		String searchSortBy = ((String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY));
		if (searchSortBy == null) searchSortBy = "";
		String[] sortOptions;
		boolean isPhotoAlbum = isViewPhotoAlbum(viewType);
		if (isPhotoAlbum)
		     sortOptions = new String[] {"number", "title",                    "activity"};	// For photo album.
		else sortOptions = new String[] {"number", "title", "state", "author", "activity"};	// For blog.
		Set<String> so = new HashSet<String>();
		for (String s:  sortOptions) {
			so.add(s);
		}
		
		ToolbarItem displayStylesTBI = new ToolbarItem("2_display_styles");
		markTBITitle(              displayStylesTBI, "toolbar.folder_sortBy");
		markTBIContentsSelectable( displayStylesTBI                         );
		entryToolbar.addNestedItem(displayStylesTBI                         );
		
		// Number.
		ToolbarItem sortByTBI;
		if (so.contains("number")) {
			String nltTag;
			if (isPhotoAlbum)
			     nltTag = "folder.column.CreationDate";
			else nltTag = "folder.column.Number";
			
			sortByTBI = new ToolbarItem("sortby");
			markTBITitle(sortByTBI, nltTag                       );
			markTBIEvent(sortByTBI, TeamingEvents.SET_FOLDER_SORT);
			markTBISort( sortByTBI, Constants.DOCID_FIELD, true  );
			if (searchSortBy.equals(Constants.DOCID_FIELD)) {
				markTBISelected(sortByTBI);
			}
			displayStylesTBI.addNestedItem(sortByTBI);
		}
		
		// Title.
		if (so.contains("title")) {
			sortByTBI = new ToolbarItem("sortby");
			markTBITitle(sortByTBI, "folder.column.Title"            );
			markTBIEvent(sortByTBI, TeamingEvents.SET_FOLDER_SORT    );
			markTBISort( sortByTBI, Constants.SORT_TITLE_FIELD, false);
			if (searchSortBy.equals(Constants.SORT_TITLE_FIELD)) {
				markTBISelected(sortByTBI);
			}
			displayStylesTBI.addNestedItem(sortByTBI);
		}
		
		// State.
		if (so.contains("state")) {
			sortByTBI = new ToolbarItem("sortby");
			markTBITitle(sortByTBI, "folder.column.State"                        );
			markTBIEvent(sortByTBI, TeamingEvents.SET_FOLDER_SORT                );
			markTBISort( sortByTBI, Constants.WORKFLOW_STATE_CAPTION_FIELD, false);
			if (searchSortBy.equals(Constants.WORKFLOW_STATE_CAPTION_FIELD)) {
				markTBISelected(sortByTBI);
			}
			displayStylesTBI.addNestedItem(sortByTBI);
		}
		
		// Author.
		if (so.contains("author")) {
			sortByTBI = new ToolbarItem("sortby");
			markTBITitle(sortByTBI, "folder.column.Author"              );
			markTBIEvent(sortByTBI, TeamingEvents.SET_FOLDER_SORT       );
			markTBISort( sortByTBI, Constants.CREATOR_TITLE_FIELD, false);
			if (searchSortBy.equals(Constants.CREATOR_TITLE_FIELD)) {
				markTBISelected(sortByTBI);
			}
			displayStylesTBI.addNestedItem(sortByTBI);
		}
		
		// Last activity date.
		if (so.contains("activity")) {
			sortByTBI = new ToolbarItem("sortby");
			markTBITitle(sortByTBI, "folder.column.LastActivity"      );
			markTBIEvent(sortByTBI, TeamingEvents.SET_FOLDER_SORT     );
			markTBISort( sortByTBI, Constants.LASTACTIVITY_FIELD, true);
			if (searchSortBy.equals(Constants.LASTACTIVITY_FIELD)) {
				markTBISelected(sortByTBI);
			}
			displayStylesTBI.addNestedItem(sortByTBI);
		}
		
		// Rating.
		if (so.contains("rating")) {
			sortByTBI = new ToolbarItem("sortby");
			markTBITitle(sortByTBI, "folder.column.Rating"       );
			markTBIEvent(sortByTBI, TeamingEvents.SET_FOLDER_SORT);
			markTBISort( sortByTBI, Constants.RATING_FIELD, true );
			if (searchSortBy.equals(Constants.RATING_FIELD)) {
				markTBISelected(sortByTBI);
			}
			displayStylesTBI.addNestedItem(sortByTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for trash views.
	 */
	private static void constructEntryTrashItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Binder binder) {
		ToolbarItem trashTBI = new ToolbarItem("1_trashRestore");
		markTBITitle(trashTBI, "toolbar.menu.trash.restore");
		markTBIEvent(trashTBI, TeamingEvents.TRASH_RESTORE_SELECTED_ENTRIES);
		entryToolbar.addNestedItem(trashTBI);
		
		trashTBI = new ToolbarItem("2_trashPurge");
		markTBITitle(trashTBI, "toolbar.menu.trash.purge");
		markTBIEvent(trashTBI, TeamingEvents.TRASH_PURGE_SELECTED_ENTRIES);
		entryToolbar.addNestedItem(trashTBI);
		
		trashTBI = new ToolbarItem("3_trashRestoreAll");
		markTBITitle(trashTBI, "toolbar.menu.trash.restoreAll");
		markTBIEvent(trashTBI, TeamingEvents.TRASH_RESTORE_ALL);
		entryToolbar.addNestedItem(trashTBI);
		
		trashTBI = new ToolbarItem("4_trashPurgeAll");
		markTBITitle(trashTBI, "toolbar.menu.trash.purgeAll");
		markTBIEvent(trashTBI, TeamingEvents.TRASH_PURGE_ALL);
		entryToolbar.addNestedItem(trashTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for toggling the 'Shared by/with Me'
	 * views between files and all entries.
	 */
	private static void constructEntryToggleSharedViewItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request) {
		// Add the toggle shared item.
		ToolbarItem shareTBI = new ToolbarItem("1_toggleShared");
		markTBIEvent(shareTBI, TeamingEvents.TOGGLE_SHARED_VIEW);
		entryToolbar.addNestedItem(shareTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for view operations against a calendar
	 * folder.
	 * 
	 * View:
	 *      Assigned Events (depending on containment)
	 *      From Folder - Events
	 *      From Folder - All by Creation
	 *      From Folder - All by Activity
	 */
	private static void constructEntryViewCalendarItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Long folderId, String viewType, Folder folder) {
		// How is the current user currently view events in this
		// folder?
		String eventType = EventsViewHelper.getCalendarDisplayEventType(
			bs,
			GwtServerHelper.getCurrentUserId(),
			folderId);
		
		if (null == eventType) {
			eventType = EventsViewHelper.EVENT_TYPE_EVENT;
		}

		// Create the view toolbar item...
		ToolbarItem viewTBI = new ToolbarItem("1_view");
		markTBITitle(viewTBI, "calendar.navi.chooseMode");
		markTBIContentsSelectable(viewTBI);

		// ...if the calendar folder is directly contained by a user...
		// ...workspace...
		Workspace folderWs = BinderHelper.getBinderWorkspace(folder);
		if (BinderHelper.isBinderUserWorkspace(folderWs)) {
			// ...add the 'assigned events' item...
			ToolbarItem tbi = new ToolbarItem("1_assigned");
			markTBITitle(       tbi, "calendar.navi.mode.alt.virtual");
			markTBIEvent(       tbi, TeamingEvents.CALENDAR_SHOW);
			markTBICalendarShow(tbi, CalendarShow.VIRTUAL);
			if (eventType.equals(EventsViewHelper.EVENT_TYPE_VIRTUAL)) {
				markTBISelected(tbi);
			}
			viewTBI.addNestedItem(tbi);
		}

		// ...add the 'from folder all' item...
		ToolbarItem tbi = new ToolbarItem("1_fromFolderAll");
		markTBITitle(       tbi, "calendar.navi.mode.alt.physical");
		markTBIEvent(       tbi, TeamingEvents.CALENDAR_SHOW);
		markTBICalendarShow(tbi, CalendarShow.PHYSICAL_EVENTS);
		if (eventType.equals(EventsViewHelper.EVENT_TYPE_EVENT)) {
			markTBISelected(tbi);
		}
		viewTBI.addNestedItem(tbi);

		// ...add the 'from folder by creation' item...
		tbi = new ToolbarItem("1_fromFolderByCreation");
		markTBITitle(       tbi, "calendar.navi.mode.alt.physical.byCreation");
		markTBIEvent(       tbi, TeamingEvents.CALENDAR_SHOW);
		markTBICalendarShow(tbi, CalendarShow.PHYSICAL_BY_CREATION);
		if (eventType.equals(EventsViewHelper.EVENT_TYPE_CREATION)) {
			markTBISelected(tbi);
		}
		viewTBI.addNestedItem(tbi);

		// ...and add the 'from folder by activity' item.
		tbi = new ToolbarItem("1_fromFolderByActivity");
		markTBITitle(       tbi, "calendar.navi.mode.alt.physical.byActivity");
		markTBIEvent(       tbi, TeamingEvents.CALENDAR_SHOW);
		markTBICalendarShow(tbi, CalendarShow.PHYSICAL_BY_ACTIVITY);
		if (eventType.equals(EventsViewHelper.EVENT_TYPE_ACTIVITY)) {
			markTBISelected(tbi);
		}
		viewTBI.addNestedItem(tbi);

		// If we added anything to the view toolbar...
		if (!(viewTBI.getNestedItemsList().isEmpty())) {
			// ...and the view toolbar to the entry toolbar.
			entryToolbar.addNestedItem(viewTBI);
		}
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
				allowCopyMove = ((null == wsDefType) || 
					((Definition.USER_WORKSPACE_VIEW != wsDefType.intValue()) &&
						(Definition.EXTERNAL_USER_WORKSPACE_VIEW != wsDefType.intValue())));
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
		
		// Does the user have rights to manage the templates on this
		// binder?
		if ((isFolder || isWorkspace) && bm.testAccess(binder, BinderOperation.manageConfiguration)) {
			// Yes!  Add the ToolbarItem for it.
			adminMenuCreated  =
			configMenuCreated = true;

			url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION,               WebKeys.ACTION_MANAGE_TEMPLATES);
			url.setParameter(WebKeys.URL_BINDER_PARENT_ID, binderIdS                      );
			
			actionTBI = new ToolbarItem(MANAGE_TEMPLATES);
			markTBIPopup(actionTBI                                         );
			markTBITitle(actionTBI, "administration.template_builder_local");
			markTBIUrl(  actionTBI, url                                    );
			
			configTBI.addNestedItem(actionTBI);
		}
		
		// Does the user have rights to delete this binder?
		if ((isFolder || isWorkspace) && (!isWorkspaceReserved) && bm.testAccess(binder, BinderOperation.deleteBinder)) {
			// Yes!  Is the binder other than a system user's or the
			// current user's workspace?
			if ((!(BinderHelper.isBinderSystemUserWS(  binder))) &&
				(!(BinderHelper.isBinderCurrentUsersWS(binder)))) {
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
			UserProperties userFolderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUserId(), folderId);
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
					markTBISelected(viewTBI);
				}				
				markTBITitleGetDef(viewTBI, def.getTitle());
				markTBIUrl(  viewTBI, url           );
				viewsTBI.addNestedItem(viewTBI);
			}
		}
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}

	/*
	 * Constructs the ToolbarItem's for the footer on a folder.
	 */
	private static void constructFooterFolderItems(ToolbarItem footerToolbar, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Construct the permalink item...
		String permaLink = PermaLinkUtil.getPermalink(request, folder);
		ToolbarItem permalinkTBI = new ToolbarItem(PERMALINK     );
		markTBITitle(permalinkTBI, "toolbar.menu.folderPermalink");
		markTBIUrl(  permalinkTBI, permaLink                     );
		footerToolbar.addNestedItem(permalinkTBI                 );

		// ...for file folders...
		if (folder.isLibrary()) {
			// ...construct any WebDAV items...
			String webdavUrl = SsfsUtil.getLibraryBinderUrl(request, folder);
			if (MiscUtil.hasString(webdavUrl)) {
				ToolbarItem webDavTBI = new ToolbarItem(VIEW_AS_WEBDAV);
				markTBITitle(webDavTBI, "toolbar.menu.viewASWebDav"   );
				markTBIUrl(  webDavTBI, webdavUrl                     );
				footerToolbar.addNestedItem(webDavTBI                 );
			
				String webdavSuffix = SPropsUtil.getString("webdav.folder.url.suffix", "");
				if (MiscUtil.hasString(webdavSuffix)) {
					webdavUrl = (webdavUrl + "/" + webdavSuffix);
				}
				webDavTBI = new ToolbarItem(WEBDAVURL            );
				markTBITitle(webDavTBI, "toolbar.menu.webdavUrl" );
				markTBIUrl(  webDavTBI, webdavUrl                );
				footerToolbar.addNestedItem(webDavTBI            );
			}
		}

		// ...for calendars and tasks...
		String viewType	= DefinitionUtils.getViewType(folder);
		if (viewType.equals(Definition.VIEW_STYLE_CALENDAR) || viewType.equals(Definition.VIEW_STYLE_TASK)) {
			// ...construct an iCal item...
			String icalUrl = org.kablink.teaming.ical.util.UrlUtil.getICalURLHttp(request, String.valueOf(folder.getId()), null);
			ToolbarItem icalTBI = new ToolbarItem(ICALENDAR);
			markTBITitle(icalTBI, "toolbar.menu.iCalendar" );
			markTBIUrl(  icalTBI, icalUrl                  );
			footerToolbar.addNestedItem(icalTBI);
		}
		
		// ...construct the RSS item...
        String topFolderId;
		if (folder.isTop())
			 topFolderId = folder.getId().toString();
		else topFolderId = folder.getTopFolder().getId().toString();		
		String rssUrl = org.kablink.teaming.module.rss.util.UrlUtil.getFeedURLHttp(request, topFolderId);
		if (MiscUtil.hasString(rssUrl)) {
			ToolbarItem rssTBI = new ToolbarItem(SUBSCRIBE_RSS);
			markTBITitle(rssTBI, "toolbar.menu.subscribeRSS"  );
			markTBIUrl(  rssTBI, rssUrl                       );
			footerToolbar.addNestedItem(rssTBI                );
		}

		// ...construct the ATOM item...
		String atomUrl = org.kablink.teaming.module.rss.util.UrlUtil.getAtomURLHttp(request, topFolderId);
		if (MiscUtil.hasString(atomUrl)) {
			ToolbarItem atomTBI = new ToolbarItem(SUBSCRIBE_ATOM);
			markTBITitle(atomTBI, "toolbar.menu.subscribeAtom"  );
			markTBIUrl(  atomTBI, atomUrl                       );
			footerToolbar.addNestedItem(atomTBI                 );
		}
		
		// ...and finally, construct the items for the simple names on
		// ...this folder.
		constructFooterSimpleNameItems(footerToolbar, bs, request, folder.getId());
	}

	/*
	 * Constructs the ToolbarItem's for the simple names defined on a
	 * binder.
	 */
	private static void constructFooterSimpleNameItems(ToolbarItem footerToolbar, AllModulesInjected bs, HttpServletRequest request, Long binderId) {
		// Are there any simple names defined on this binder?
		List<SimpleName> simpleNames = bs.getBinderModule().getSimpleNames(binderId);
		int c = ((null == simpleNames) ? 0 : simpleNames.size());
		if (0 < c ){
			// Yes!  Create a ToolbarItem to hold information about
			// them...
			ToolbarItem simpleNamesTBI = new ToolbarItem(SIMPLE_NAMES);
			footerToolbar.addNestedItem(simpleNamesTBI);

			// ...store the URL prefix for them and how many are
			// ...defined...
			simpleNamesTBI.addQualifier("simple.host",   getHostName(bs)                               );
			simpleNamesTBI.addQualifier("simple.prefix", WebUrlUtil.getSimpleURLContextRootURL(request));
			simpleNamesTBI.addQualifier("simple.count",  String.valueOf(c)                             );
			
			// ...scan them...
			for (int i = 0; i < c; i += 1) {
				// ...adding information about each to the toolbar
				// ...item.
				SimpleName   simpleName   = simpleNames.get(i);
				SimpleNamePK simpleNameId = simpleName.getId();
				simpleNamesTBI.addQualifier(("simple." + i + ".email"),                simpleName.getEmailAddress());
				simpleNamesTBI.addQualifier(("simple." + i + ".zone"),  String.valueOf(simpleNameId.getZoneId())   );
				simpleNamesTBI.addQualifier(("simple." + i + ".name"),                 simpleNameId.getName()      );
			}
		}
	}
	
	/*
	 * Constructs the ToolbarItem's for the footer on a workspace.
	 */
	private static void constructFooterWorkspaceItems(ToolbarItem footerToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws) {
		// Construct the permalink item...
		String permaLink = PermaLinkUtil.getPermalink(request, ws);
		ToolbarItem permalinkTBI = new ToolbarItem(PERMALINK);
		markTBITitle(permalinkTBI, "toolbar.menu.workspacePermalink");
		markTBIUrl(  permalinkTBI, permaLink                        );
		footerToolbar.addNestedItem(permalinkTBI);
		
		// ...and , construct the items for the simple names on this
		// ...workspace.
		constructFooterSimpleNameItems(footerToolbar, bs, request, ws.getId());
	}
	
	/*
	 * Constructs a ToolbarItem to run the send email to team members
	 * (actually, contributors but I followed the naming that was used
	 * in the JSP code) dialog. 
	 */
	private static ToolbarItem constructSendEmailToItem(HttpServletRequest request, Binder binder) {
		User user = GwtServerHelper.getCurrentUser();
		if (MiscUtil.hasString(user.getEmailAddress()) && (!(user.isShared()))) {
			ToolbarItem sendEmailToTBI = new ToolbarItem(SEND_EMAIL             );
			markTBITitle(sendEmailToTBI, "toolbar.menu.sendMail"                );
			markTBIEvent(sendEmailToTBI, TeamingEvents.INVOKE_SEND_EMAIL_TO_TEAM);
			AdaptedPortletURL url = createActionUrl(request);
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL);
			url.setParameter(WebKeys.URL_BINDER_ID, binder.getId().toString());
			url.setParameter(WebKeys.USER_IDS_TO_ADD, InvokeSendEmailToTeamEvent.CONTRIBUTOR_IDS_PLACEHOLER);
			markTBIUrl(sendEmailToTBI, url);
			return sendEmailToTBI;
		}
		return null;
	}

	/*
	 * Constructs a ToolbarItem to run the share binder dialog.
	 */
	private static ToolbarItem constructShareBinderItem(HttpServletRequest request, Binder binder) {
		ToolbarItem     shareTBI = new ToolbarItem(SHARE);
		markTBITitle(   shareTBI, GwtUIHelper.buildRelevanceKey(binder, "relevance.shareThis"));
		markTBIEvent(   shareTBI, TeamingEvents.INVOKE_SHARE_BINDER                           );
		markTBIBinderId(shareTBI, binder.getId()                                              );
		return shareTBI;
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

	/*
	 * Dumps a string.
	 */
	private static void dumpString(String dumpThis) {
		// Simply dump the string.
		m_logger.debug(dumpThis);
	}
	
	private static void dumpString(String dumpStart, String dumpThis) {
		// If we have a string to dump...
		if (MiscUtil.hasString(dumpThis)) {
			// ...dump it.
			dumpString(dumpStart + dumpThis);
		}
	}
	
	/*
	 * Dumps the contents of a toolbar item.
	 */
	private static void dumpToolbarItem(ToolbarItem tbi, String dumpStart) {
		// Dump the base information about the toolbar item...
		dumpString(dumpStart + ":toolbar=" + tbi.getName() );
		dumpString(dumpStart + "...:title=", tbi.getTitle());
		dumpString(dumpStart + "...:url=",   tbi.getUrl()  );
		
		// ...if there's an event in it...
		if (TeamingEvents.UNDEFINED != tbi.getTeamingEvent()) {
			// ...dump that...
			dumpString(dumpStart + "...:event=", tbi.getTeamingEvent().name());
		}
		
		// ...scan the toolbar's qualifier list...
		for (NameValuePair nvp:  tbi.getQualifiersList()) {
			// ...dumping the contents of each...
			dumpString(dumpStart + "...:qualifier:name:value=", nvp.getName() + ":" + nvp.getValue());
		}
		
		// ...and finally, dump its nested toolbar items.
		dumpToolbarItems(tbi.getNestedItemsList(), (dumpStart + "..."));
	}
	
	/*
	 * Dumps the contents of a toolbar item list.
	 */
	private static void dumpToolbarItems(List<ToolbarItem> tbiList, String dumpStart) {
		// If debug logging is enabled...
		if (m_logger.isDebugEnabled()) {
			// ...scan the toolbar items...
			for (ToolbarItem tbi:  tbiList) {
				// ...dumping the contents of each.
				dumpToolbarItem(tbi, dumpStart);
			}
		}
	}

	/*
	 * Fills a List<RecentPlaceInfo> with the recent places information
	 * stored in a Tabs object.
	 */
	@SuppressWarnings("unchecked")
	private static void fillRecentPlacesFromTabs(AllModulesInjected bs, HttpServletRequest request, Tabs tabs, List<RecentPlaceInfo> rpiList) {
		// Scan the tabs...
		int count = 0;
		List tabList = tabs.getTabList();
		int maxTitle = SPropsUtil.getInt("history.max.title",   30);
		int maxItems = SPropsUtil.getInt("recent-places-depth", 10);
		for (Iterator tabIT = tabList.iterator(); tabIT.hasNext(); ) {
			// ...creating a RecentPlaceInfo object for each...
			TabEntry tab = ((TabEntry) tabIT.next());
			RecentPlaceInfo rpi = new RecentPlaceInfo();
			String title = ((String) tab.getData().get("title"));
			if (title.length() > maxTitle) {
				title = (title.substring(0, maxTitle) + "...");
			}
			rpi.setTitle(title);
			rpi.setId(String.valueOf(tab.getTabId()));
			rpi.setType(tab.getType());
			switch (rpi.getTypeEnum()) {
			case BINDER:
				// If the tab's binder is no longer accessible...
				Long binderId = tab.getBinderId();
				Binder binder = GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId);
				if ((null == binder) || GwtUIHelper.isBinderPreDeleted(binder)) {
					// ...skip it.
					continue;
				}
				rpi.setBinderId(String.valueOf(binderId));
				rpi.setEntityPath(((String) tab.getData().get("path")));
				rpi.setEntryId(String.valueOf(tab.getEntryId()));
				rpi.setPermalink(PermaLinkUtil.getPermalink(request, binder));
				
				break;
				
			case SEARCH:
				rpi.setSearchQuery(tab.getQuery());
				rpi.setSearchQuick(((Boolean) tab.getData().get("quickSearch")));
				
				break;

			default:
				continue;
			}
			
			// ...adding it to the list of them...
			rpiList.add(rpi);
			
			// ...and stopping when we hit our maximum.
			count += 1;
			if (maxItems == count) {
				break;
			}
		}
	}
	
	/*
	 * Returns true if a folder (including its view type) supports
	 * delete and purge operations and false otherwise.
	 */
	private static boolean folderSupportsDeleteAndPurge(Folder folder, String viewType) {
		return folderSupportsSelection(folder, viewType);
	}
	
	/*
	 * Returns true if a folder (including its view type) supports
	 * entry selection and false otherwise.
	 */
	private static boolean folderSupportsEntrySelection(Folder folder, String viewType) {
		return folderSupportsSelection(folder, viewType);
	}
	
	/*
	 * Returns true if a folder (including its view type) supports
	 * the operations in the 'More' entry menu and false otherwise.
	 */
	private static boolean folderSupportsMore(Folder folder, String viewType) {
		return folderSupportsSelection(folder, viewType);
	}
	
	/*
	 * Returns true if a folder (including its view type) supports
	 * pinning and false otherwise.
	 */
	private static boolean folderSupportsPinning(Folder folder, String viewType) {
		boolean reply = ((null != folder) && MiscUtil.hasString(viewType));
		if (reply) {
			reply =
				(viewType.equals(Definition.VIEW_STYLE_DISCUSSION) ||	// Discussion - Standard View.
				 viewType.equals(Definition.VIEW_STYLE_TABLE));			// Discussion - Movable Columns.
		}
		return reply;
	}
	
	/*
	 * Returns true if a folder (including its view type) supports
	 * operations that involve selecting items and false otherwise.
	 */
	private static boolean folderSupportsSelection(Folder folder, String viewType) {
		boolean reply = ((null != folder) && MiscUtil.hasString(viewType));
		if (reply) {
			reply =
				((viewType.equals(Definition.VIEW_STYLE_CALENDAR)   ||
				  viewType.equals(Definition.VIEW_STYLE_DISCUSSION) ||
				  viewType.equals(Definition.VIEW_STYLE_TABLE)      ||
				  viewType.equals(Definition.VIEW_STYLE_FILE)       ||
				  viewType.equals(Definition.VIEW_STYLE_GUESTBOOK)  ||
				  viewType.equals(Definition.VIEW_STYLE_MILESTONE)  ||
				  viewType.equals(Definition.VIEW_STYLE_MINIBLOG)   ||
				  viewType.equals(Definition.VIEW_STYLE_SURVEY)     ||
				  viewType.equals(Definition.VIEW_STYLE_TASK)));
		}
		return reply;
	}
	
	/*
	 * Returns true if a folder (including its view type) supports
	 * share operations and false otherwise.
	 */
	private static boolean folderSupportsShare(AllModulesInjected bs, Folder folder, String viewType) {
		boolean reply = folderSupportsSelection(folder, viewType);
		if (reply) {
			reply = GwtShareHelper.isSharingEnabled(bs);
		}
		return reply;
	}
	
	/*
	 * Walks up the parentage change of a folder looking for the
	 * topmost containing folder of the same type.
	 * 
	 * Logic lifted from ListFolderHelper.buildBlogPageBeans()
	 */
	private static Long getFolderSetFolderId(AllModulesInjected bs, Folder folder, String viewType) {
		Binder parentBinder = folder.getParentBinder();
		Binder topBinder = folder;
		while (null != parentBinder) {
			Integer pbDefType = parentBinder.getDefinitionType();
			if ((null != pbDefType) && (!(pbDefType.equals(Definition.FOLDER_VIEW))) || 
					(!(viewType.equals(BinderHelper.getViewType(bs, parentBinder))))) {
				break;
			}
			topBinder = parentBinder;
			parentBinder = parentBinder.getParentBinder();
		}
		return topBinder.getId();
	}
	
	/**
	 * Returns a GetFolderToolbarItemsRpcResponseData containing the
	 * ToolbarItem's for a folder given the current user's rights to
	 * that folder.
	 *
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 */
	public static GetFolderToolbarItemsRpcResponseData getFolderToolbarItems(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) {
		SimpleProfiler.start("GwtMenuHelper.getFolderToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return...
			List<ToolbarItem> configureToolbarItems = new ArrayList<ToolbarItem>();
			List<ToolbarItem> toolbarItems          = new ArrayList<ToolbarItem>();
			GetFolderToolbarItemsRpcResponseData reply = new GetFolderToolbarItemsRpcResponseData(toolbarItems, configureToolbarItems);
			ToolbarItem entryToolbar = new ToolbarItem(WebKeys.ENTRY_TOOLBAR);
			toolbarItems.add(entryToolbar);
			
			// Access the binder/folder/workspace.
			Long		folderId = folderInfo.getBinderIdAsLong();
			Binder		binder   = bs.getBinderModule().getBinder(folderId);
			Folder		folder   = ((binder instanceof Folder)    ? ((Folder)    binder) : null);
			Workspace	ws       = ((binder instanceof Workspace) ? ((Workspace) binder) : null);
			boolean		isFolder = (null != folder);
			String		viewType = (isFolder ? DefinitionUtils.getViewType(folder) : null);

			// Construct the item for viewing pinned vs. non-pinned
			// items.
			constructEntryPinnedItem(entryToolbar, bs, request, viewType, folder);

			// Are we returning the toolbar items for a trash view?
			boolean isBinderCollection = folderInfo.isBinderCollection();
			boolean isBinderTrash      = folderInfo.isBinderTrash();
			if (isBinderTrash) {
				// Yes!  Construct the items for viewing the trash.
				constructEntryTrashItems(entryToolbar, bs, request, binder);
			}
			
			// No, we aren't returning the toolbar items for a trash
			// view!  Are we returning them for the root profiles
			// workspace view?
			else if (folderInfo.isBinderProfilesRootWS()) {
				// Yes!  Construct the items for viewing the root
				// profiles binder.
				constructEntryProfilesRootWSItems(entryToolbar, bs, request, ws);
			}
			
			// No, we aren't returning the toolbar items for the root
			// profiles workspace view either!  Are we returning them
			// for a collection view?
			else if (isBinderCollection) {
				boolean isCollectionMyFiles      = (CollectionType.MY_FILES       == folderInfo.getCollectionType());
				boolean isCollectionSharedByMe   = (CollectionType.SHARED_BY_ME   == folderInfo.getCollectionType());
				boolean isCollectionSharedWithMe = (CollectionType.SHARED_WITH_ME == folderInfo.getCollectionType());
				if ((!(Utils.checkIfFilr())) && (isCollectionSharedByMe || isCollectionSharedWithMe)) {
					constructEntryToggleSharedViewItem(entryToolbar, bs, request                                                             );
				}
				if (isCollectionMyFiles) {
					constructEntryAddFileFolderItem(   entryToolbar, bs, request,                                                  ws        );
				}
				if (isCollectionMyFiles || isCollectionSharedByMe) {
				    constructEntryShareItem(           entryToolbar, bs, request                                                             );
				}
				if (isCollectionSharedByMe || isCollectionSharedWithMe) {
					constructEntryDetailsItem(         entryToolbar, bs, request                                                             );
				}
				constructEntryDeleteItem(              entryToolbar, bs, request,                           (isCollectionMyFiles ? ws : null));
				constructEntryMoreItems(               entryToolbar, bs, request, folderId, viewType, null, (isCollectionMyFiles ? ws : null));
			}
			
			else {
				// No, we aren't returning the toolbar items for a
				// collection view either!  Is this is other than a
				// mirrored folder, or if its a mirrored folder, is its
				// resource driver configured?
				boolean isMirrored           = (isFolder && folder.isMirrored());
				boolean isMirroredConfigured = isMirrored && MiscUtil.hasString(folder.getResourceDriverName());
				if ((!isMirrored) || isMirroredConfigured) {
					// Yes!  Can the user can add entries to the
					// folder?
					FolderModule fm			= bs.getFolderModule();
					boolean      hasVT      = MiscUtil.hasString(viewType);
					boolean      addAllowed	= fm.testAccess(folder, FolderOperation.addEntry);
					if (addAllowed) {				
						// Yes!  If the folder is a guest book...
						if (hasVT && isViewGuestBook(viewType)) {
							// ...construct a sign the guest book item.
							constructEntrySignTheGuestbookItem(entryToolbar, bs, request, folder);
						}
					
						// ...and add the necessary 'add entry' items.
						constructEntryAddItems(entryToolbar, bs, request, viewType, folder);
					}
		
					// If the folder supports entry selection...
					if (folderSupportsEntrySelection(folder, viewType)) {
						// ...construct the details menu item.
						constructEntryDetailsItem(entryToolbar, bs, request);
					}
					
					// Constructs the items for sharing and deleting
					// the selected entries.
					constructEntryShareItem( entryToolbar, bs, request, viewType, folder);
					constructEntryDeleteItem(entryToolbar, bs, request, viewType, folder);
		
					// Can we determine the folder's view type?
					if (hasVT) {
						// Yes!  Is it a blog or photo album?
						if (isViewBlog(viewType)|| isViewPhotoAlbum(viewType)) {
							// Yes!  Add the necessary 'sort by' items. 
							constructEntrySortByItems(entryToolbar, bs, request, viewType, folder);
						}
		
						// Can the user add entries to the folder and
						// are applets supported?
						if (addAllowed && SsfsUtil.supportApplets(request)) {
							// Yes!  Is it other than a mini-blog or a mirrored
							// file that can't be written to?
							if ((!(isViewMiniBlog(viewType))) && ((!(folder.isMirrored())) || isFolderWritableMirrored(folder))) {
								// Yes!  The the 'drop box' item.
								constructEntryDropBoxItem(entryToolbar, bs, request, viewType, folder);
							}
						}
					}

					// If the folder supports operations from the
					// 'More' drop down...
					if (folderSupportsMore(folder, viewType)) {
						// ...construct the various items that appear
						// ...in it.
						constructEntryMoreItems(
							entryToolbar,
							bs,
							request,
							folderId,
							viewType,
							folder,
							null);	// null -> Not a Workspace.
					}
					
					// Are we working on a calendar folder?
					boolean isCalendar = (FolderType.CALENDAR == folderInfo.getFolderType());
					if (isCalendar) {
						// Yes!  Construct the various items that
						// appear in the view drop down.
						constructEntryViewCalendarItems(
							entryToolbar,
							bs,
							request,
							folderId,
							viewType,
							folder);
					}
				}
			}
			
			// Are we returning the toolbar items for other than a
			// trash or collections view?
			if ((!isBinderTrash) && (!isBinderCollection)) {
				// Yes!  Add the configure accessories item to the
				// toolbar.
				constructEntryConfigureAccessories(
					bs,
					request,
					binder,
					configureToolbarItems);
			}

			// If the binder supports column configuration...
			ToolbarItem configureColumns = constructEntryConfigureColumsItem(binder);
			if (null != configureColumns) {
				// ...add the toolbar item to the configure list.
				configureToolbarItems.add(configureColumns);
			}

			// If we get here, reply refers to the
			// GetFolderToolbarItemsRpcResponseData containing the
			// ToolbarItem's for the folder.  Return it.
			m_logger.debug("GwtMenuHelper.getFolderToolbarItems():");
			dumpToolbarItems(configureToolbarItems, "...");
			dumpToolbarItems(toolbarItems,          "...");
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("GwtMenuHelper.getFolderToolbarItems()");
		}
	}
	
	/**
	 * Returns a List<ToolbarItem> of the ToolbarItem's for a binder's
	 * footer given the binder type, the current user's rights to that
	 * binder, ...
	 *
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 */
	public static List<ToolbarItem> getFooterToolbarItems(AllModulesInjected bs, HttpServletRequest request, Long binderId) {
		SimpleProfiler.start("GwtMenuHelper.getFooterToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return...
			List<ToolbarItem> reply = new ArrayList<ToolbarItem>();
			ToolbarItem footerToolbar = new ToolbarItem(WebKeys.FOOTER_TOOLBAR);
			reply.add(footerToolbar);

			// Generate the toolbar items based on the type of binder
			// this is.
			BinderModule bm		= bs.getBinderModule();
			Binder       binder	= bm.getBinder(binderId);
			if (binder instanceof Folder) {
				Folder folder = ((Folder) binder);
				constructFooterFolderItems(footerToolbar, bs, request, folder);
			}
			
			else if (binder instanceof Workspace) {
				Workspace ws = ((Workspace) binder);
				constructFooterWorkspaceItems(footerToolbar, bs, request, ws);
			}
			

			// If we get here, reply refers to the List<ToolbarItem>
			// for the footer toolbar.  Return it.
			m_logger.debug("GwtMenuHelper.getFooterToolbarItems():");
			dumpToolbarItems(reply, "...");
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("GwtMenuHelper.getFooterToolbarItems()");
		}
	}

	/*
	 * Returns the hostname of the current running instance of Vibe.
	 */
	private static String getHostName(AllModulesInjected bs) {
		String hostname = bs.getZoneModule().getVirtualHost(RequestContextHolder.getRequestContext().getZoneName());
		if (!(MiscUtil.hasString(hostname))) {
			try {
		        InetAddress addr = InetAddress.getLocalHost();
		        hostname = addr.getHostName();
		    } catch (UnknownHostException e) {
				m_logger.debug("GwtMenuHelper.getHostName( UnknownHostException ):  Using localhost");
				hostname = "localhost";
		    }
		}
		return hostname;
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
			fillRecentPlacesFromTabs(bs, request, tab.getTabs(), reply);
		}
		
		// ...and return the List<RecentPlaceInfo>.
		return reply;
	}

	/**
	 * Returns a StringRpcResponseData containing the URL to use to run
	 * the guest book signing UI.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static StringRpcResponseData getSignGuestbookUrl(AllModulesInjected bs, HttpServletRequest request, Long folderId) throws GwtTeamingException {
		try {
			// Allocate a StringRpcResponseData to return the URL with.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Can we find the definition of a guest book entry?
			Definition def = DefinitionHelper.getDefinition(ObjectKeys.DEFAULT_ENTRY_GUESTBOOK_DEF);
			if (null != def) {
				// Yes!  Construct the add entry URL for it...
				AdaptedPortletURL url = createActionUrl(request);
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(folderId));
				url.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				
				// ...and store it in the StringRpcResponseData.
				reply.setStringValue(url.toString());
			}

			// If we get here, reply refers to a StringRpcResponseData
			// containing the URL to sign a guest book.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			if ((!(GwtServerHelper.m_logger.isDebugEnabled())) && m_logger.isDebugEnabled()) {
			     m_logger.debug("GwtMenuHelper.getSignGuestbookUrl( SOURCE EXCEPTION ):  ", e);
			}
			throw GwtServerHelper.getGwtTeamingException(e);
		}
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
					// Yes!  Can the user work with teams on this
					// binder?
					if ((!(Utils.checkIfFilr())) || (0 < binder.getTeamMemberIds().size())) {
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
			m_logger.debug("GwtMenuHelper.getToolbarItems():");
			dumpToolbarItems(reply, "...");
			return reply;
		}
		
		finally {
			SimpleProfiler.stop("GwtMenuHelper.getToolbarItems()");
		}
	}

	/*
	 * Returns true if a folder is writable mirrored folder and false
	 * otherwise.
	 */
	private static boolean isFolderWritableMirrored(Folder folder) {
		return
			(folder.isMirrored() &&										// Is mirrored...
				(!(folder.isMirroredAndReadOnly())) &&					// ...and is not read-only...
				MiscUtil.hasString(folder.getResourceDriverName()));	// ...and the resource driver is configured.
	}

	/*
	 * Returns true if a view type is a blog and false otherwise.
	 */
	private static boolean isViewBlog(String viewType) {
		return MiscUtil.hasString(viewType) && viewType.equals(Definition.VIEW_STYLE_BLOG);
	}
	
	/*
	 * Returns true if a view type is a guest book and false otherwise.
	 */
	private static boolean isViewGuestBook(String viewType) {
		return MiscUtil.hasString(viewType) && viewType.equals(Definition.VIEW_STYLE_GUESTBOOK);
	}
	
	/*
	 * Returns true if a view type is a mini blog and false otherwise.
	 */
	private static boolean isViewMiniBlog(String viewType) {
		return MiscUtil.hasString(viewType) && viewType.equals(Definition.VIEW_STYLE_MINIBLOG);
	}
	
	/*
	 * Returns true if a view type is a photo album and false
	 * otherwise.
	 */
	private static boolean isViewPhotoAlbum(String viewType) {
		return MiscUtil.hasString(viewType) && viewType.equals(Definition.VIEW_STYLE_PHOTO_ALBUM);
	}
	
	/*
	 * Marks a ToolbarItem with a binder ID.
	 */
	private static void markTBIBinderId(ToolbarItem tbi, String binderId) {
		tbi.addQualifier("binderId", binderId);
	}
	
	private static void markTBIBinderId(ToolbarItem tbi, Long binderId) {
		// Always use the initial form of the method.
		markTBIBinderId(tbi, String.valueOf(binderId));
	}
	
	/*
	 * Marks a ToolbarItem as needing to calendar show mode.
	 */
	private static void markTBICalendarShow(ToolbarItem tbi, CalendarShow show) {
		tbi.addQualifier("calendarShow", String.valueOf(show.ordinal()));
	}
	
	/*
	 * Marks a ToolbarItem as containing items that can be selected in
	 * some way.
	 */
	private static void markTBIContentsSelectable(ToolbarItem tbi) {
		tbi.addQualifier(WebKeys.TOOLBAR_MENU_CONTENTS_SELECTABLE, "true");
	}
	
	/*
	 * Marks a ToolbarItem as being the default of some sort.
	 */
	private static void markTBIDefault(ToolbarItem tbi) {
		tbi.addQualifier("default", "true");
	}

	/*
	 * Marks a ToolbarItem's event.
	 */
	private static void markTBIEvent(ToolbarItem tbi, TeamingEvents event) {
		tbi.setTeamingEvent(event);
	}
	
	/*
	 * Marks a ToolbarItem with a folder template ID qualifier.
	 */
	private static void markTBIFolderTemplateId(ToolbarItem tbi, Long folderTemplateId) {
		tbi.addQualifier("folderTemplateId", String.valueOf(folderTemplateId));
	}
	
	/*
	 * Marks a ToolbarItem as being highlighted in some way.
	 */
	private static void markTBIHighlight(ToolbarItem tbi) {
		tbi.addQualifier("highlight", "true");
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
	 * Marks a ToolbarItem as being selected in some way.
	 */
	private static void markTBISelected(ToolbarItem tbi) {
		tbi.addQualifier(WebKeys.TOOLBAR_MENU_SELECTED, "true");
	}
	
	/*
	 * Marks a ToolbarItem with a sort information.
	 */
	private static void markTBISort(ToolbarItem tbi, String sortKey, boolean sortDescending) {
		tbi.addQualifier("sortKey",                       sortKey        );
		tbi.addQualifier("sortDescending", String.valueOf(sortDescending));
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
	private static void markTBITitleRes(ToolbarItem tbi, String title) {
		tbi.setTitle(title);
	}
	
	/*
	 * Marks a ToolbarItem's title based on a resource key.
	 */
	private static void markTBITitle(ToolbarItem tbi, String key) {
		markTBITitleRes(tbi, NLT.get(key));
	}
	
	/*
	 * Marks a ToolbarItem's title based on a resource key.
	 */
	private static void markTBITitleGetDef(ToolbarItem tbi, String key) {
		markTBITitleRes(tbi, NLT.getDef(key));
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
