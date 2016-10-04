/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.SimpleName.SimpleNamePK;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.MenuIds;
import org.kablink.teaming.gwt.client.event.InvokeSendEmailToTeamEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem.NameValuePair;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetToolbarItemsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData.AccountInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.CalendarShow;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.definition.notify.NotifyBuilderUtil;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PasswordPolicyHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.WebHelper;
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
	private final static String ACTIVITY_REPORT			= "activityReport";
	private final static String ADD_BINDER				= "addBinder";
	private final static String ADD_FOLDER				= "addFolder";
	private final static String ADD_WORKSPACE			= "addWorkspace";
	private final static String ADMINISTRATION			= "administration";
	private final static String BRANDING				= "branding";
	private final static String CALENDAR				= "calendar";
	private final static String CATEGORIES				= "categories";
	private final static String CHANGE_ENTRY_TYPE		= "changeEntryType";
	private final static String CLIPBOARD				= "clipboard";
	private final static String CONFIGURATION			= "configuration";
	private final static String CONFIGURE_ACCESSORIES	= "configureAccessories";
	private final static String CONFIGURE_COLUMNS		= "configureColumns";
	private final static String CONFIGURE_DEFINITIONS	= "configureDefinitions";
	private final static String CONFIGURE_EMAIL			= "configEmail";
	private final static String CONFIGURE_HTML_ELEMENT	= "configureHtmlElement";
	private final static String CONFIGURE_USER_LIST		= "configureUserList";
	private final static String COPY					= "copy";
	private final static String COPY_PUBLIC_LINK		= "copyPublicLink";
	private final static String DELETE					= "delete";
	private final static String DISPLAY_STYLES			= "display_styles";
	private final static String DOWNLOAD_AS_CSV_FILE	= "downloadAsCSVFile";
	private final static String EDIT_IN_PLACE			= "editInPlace";
	private final static String EDIT_PUBLIC_LINK		= "editPublicLink";
	private final static String EMAIL					= "email";
	private final static String EMAIL_PUBLIC_LINK		= "emailPublicLink";
	private final static String	FILE_DOWNLOAD			= "fileDownload";
	private final static String FOLDER_VIEWS			= "folderViews";
	private final static String FORCE_FILE_UNLOCK		= "forceFileUnlock";
	private final static String ICALENDAR				= "iCalendar";
	private final static String IMPORT_EXPORT			= "importExport";
	private final static String LOCK					= "lock"; 
	private final static String MAILTO_PUBLIC_LINK		= "mailToPublicLink";
	private final static String MANAGE_DEFINITIONS		= "manageDefinitions";
	private final static String MANAGE_TEMPLATES		= "manageTemplates";
	private final static String MANUAL_SYNC				= "manualSync";
	private final static String MOBILE_UI				= "mobileUI";
	private final static String MODIFY					= "modify";
	private final static String MORE					= "more";
	private final static String MOVE					= "move";
	private final static String PERMALINK				= "permalink";
	private final static String REPORTS					= "reports";
	private final static String RENAME					= "rename";
	private final static String SCHEDULE_SYNC			= "scheduleSync";
	private final static String SEEN					= "seen";
	private final static String SEEN_FOLDER				= "seenFolder";
	private final static String SEND_EMAIL				= "sendEmail";
	private final static String SHARE					= "share";
	private final static String SIMPLE_NAMES			= "simpleNames";
	private final static String SS_FORUM				= "ss_forum";
	private final static String SUBSCRIBE			    = "subscribe";
	private final static String SUBSCRIBE_ATOM			= "subscribeAtom";
	private final static String SUBSCRIBE_RSS			= "subscribeRSS";
	private final static String TRASH					= "trash";
	private final static String UNLOCK					= "unlock"; 
	private final static String UNSEEN					= "unseen";
	private final static String UNSEEN_FOLDER			= "unseenFolder";
	private final static String VIEW_AS_WEBDAV			= "viewaswebdav";
	private final static String WEBDAVURL				= "webdavUrl";
	private final static String WHATS_NEW				= "whatsnew";
	private final static String WHO_HAS_ACCESS			= "whohasaccess";
	private final static String WORKFLOW_HISTORY_REPORT	= "workflowHistoryReport";
	private final static String WORKSPACE_SHARE_RIGHTS	= "wsShareRights";
	private final static String ZIP_AND_DOWNLOAD		= "zipAndDownload";
	
	// Controls whether WebDAV information shows up in footers.
	// DRF (20130225):  Bug 805858:  Disabled these as a per a
	// recommendation from Jong.
	private final static boolean INCLUDE_FOOTER_WEBDAV_URLS_FILR	= false;
	private final static boolean INCLUDE_FOOTER_WEBDAV_URLS_VIBE	= true;

	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtMenuHelper() {
		// Nothing to do.
	}

	/**
	 * Ensures that the given binder is being tracked for the recent
	 * places menu.
	 * 
	 * @param request
	 * @param binder
	 * @param clearTab
	 */
	public static void addBinderToRecentPlaces(HttpServletRequest request, Binder binder, boolean clearTab) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.addBinderToRecentPlaces()");
		try {
			try {
				BinderHelper.initTabs(Tabs.getTabs(request), binder, true);
			}
			catch (Exception e) {}	// Ignored.
		}
		
		finally {
			gsp.stop();
		}
	}
	
	public static void addBinderToRecentPlaces(HttpServletRequest request, Binder binder) {
		// Always use the initial form of the method.
		addBinderToRecentPlaces(request, binder, true);
	}
	
	/*
	 * Adds a separator to a ToolbarItem's nested toolbar, if needed.
	 */
	private static boolean addNestedSeparatorIfNeeded(ToolbarItem tbi, boolean needSeparator) {
		// Do w need to add a separator?
		if (needSeparator) {
			// Add a separator between the action commands above and
			// the management commands that follow.
			tbi.addNestedItem(ToolbarItem.constructSeparatorTBI());
		}
		return false;
	}

	/*
	 * If a FolderEntry references a file that we support edit-in-place
	 * on and the user has rights to edit the file, a ToolbarItem
	 * referencing an edit-in-place action is returned.  Otherwise,
	 * null is returned.
	 */
	private static ToolbarItem buildEditInPlaceToolbarItem(AllModulesInjected bs, HttpServletRequest request, FolderEntry fe, boolean isFilr) {
		// If NPAPI isn't supported on the browser...
		/*if (!(MiscUtil.isNPAPISupported(request))) {
			// ...there can be no applets and hence no edit-in-place.
			return null;
		}*/
		
		// Is the entry unlock and does the user have rights to
		// modify it?
		ToolbarItem		reply  = null;
		HistoryStamp	lStamp = fe.getReservation();
		boolean			locked = ((!isFilr) && (null != lStamp));	// We ignore locking in filr. 
		if ((!locked) && bs.getFolderModule().testAccess(fe, FolderOperation.modifyEntry)) {
			// Yes!  Is it a file entry with a file attached?
			FileAttachment fa = GwtServerHelper.getFileEntrysFileAttachment(bs, fe);
			if (null != fa) {
				// Yes!  Did this user log in using OpenID?
				if (!(WebHelper.isUserAuthenticatedViaOpenid())) {
					// No!  Are there any applications defined to
					// edit the file?
					String operatingSystem  = BrowserSniffer.getOSInfo(request);
					String relativeFilePath = fa.getFileItem().getName();
					String strOpenInEditor  = SsfsUtil.openInEditor(relativeFilePath, operatingSystem, bs.getProfileModule().getUserProperties(null));
					if (MiscUtil.hasString(strOpenInEditor)) {
						String strEditorType;
						if (BrowserSniffer.is_ie(request))
						     strEditorType = SsfsUtil.attachmentEditTypeForIE();
						else strEditorType = SsfsUtil.attachmentEditTypeForNonIE();
						if (MiscUtil.hasString(strEditorType)) {
							// Yes!  Add an edit-in-place toolbar
							// item for it.
							reply = new ToolbarItem(EDIT_IN_PLACE);
							markTBITitle(   reply, "file.editFile"                   );
							markTBIEvent(   reply, TeamingEvents.INVOKE_EDIT_IN_PLACE);
							markTBIEntityId(reply, fe                                );
							markTBIEditInPlace(
								reply,
								operatingSystem,
								strOpenInEditor,
								strEditorType,
								fa.getId(),
								SsfsUtil.getInternalAttachmentUrl(request, fe.getParentFolder(), fe, fa));
						}
					}
				}
			}
		}
		
		// If we get here, reply refers to an edit-in-place ToolbarItem
		// if all the requirements for including it are met or is false
		// otherwise.  Return it.
		return reply;
	}
	/*
	 * Adds the ToolBarItem's for a folder to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on ListFolderHelper.buildFolderToolbars()
	 */
	private static void buildFolderMenuItems(AllModulesInjected bs, HttpServletRequest request, Folder folder, boolean isMyFilesStorage, List<ToolbarItem> tbiList) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.buildFolderMenuItems()");
		try {
			boolean isFilr = Utils.checkIfFilr();
			tbiList.add(constructFolderItems(           isFilr, WebKeys.FOLDER_TOOLBAR,             bs, request, folder, isMyFilesStorage, EntityType.folder));
			tbiList.add(constructFolderViewsItems(      isFilr, WebKeys.FOLDER_VIEWS_TOOLBAR,       bs, request, folder                                     ));
			tbiList.add(constructCalendarImportItems(   isFilr, WebKeys.CALENDAR_IMPORT_TOOLBAR,    bs, request, folder                                     ));		
			tbiList.add(constructWhatsNewItems(         isFilr, WebKeys.WHATS_NEW_TOOLBAR,          bs, request, folder,                   EntityType.folder));
			tbiList.add(constructEmailSubscriptionItems(isFilr, WebKeys.EMAIL_SUBSCRIPTION_TOOLBAR, bs, request, folder                                     ));
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Adds the ToolBarItem's common to all binder types to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on GwtUIHelper.buildGwtMiscToolbar()
	 */
	private static void buildMiscMenuItems(AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType, List<ToolbarItem> tbiList) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.buildMiscMenuItems()");
		try {
			// Generate the GWT miscellaneous ToolbarItem.
			ToolbarItem miscTBI = new ToolbarItem(WebKeys.GWT_MISC_TOOLBAR);
			tbiList.add(miscTBI);
	
			// Add the about to it.
			miscTBI.addNestedItem(constructAboutItem());
			
			// Do we have a binder and are we running as other than
			// guest?
			if ((null != binder) && (!(GwtServerHelper.getCurrentUser().isShared()))) {
				// Yes!
				boolean isFilr = Utils.checkIfFilr();
	
				if (!isFilr) {
					// Add a ToolbarItem for editing its branding.
					miscTBI.addNestedItem(constructEditBrandingItem(bs, binder, binderType));
				}
	
				// Is this other than the profiles binder?
				if (EntityIdentifier.EntityType.profiles != binder.getEntityType()) {
					// Yes!  Add the various binder based
					// ToolbarItem's.
					miscTBI.addNestedItem(constructClipboardItem()                           );
					miscTBI.addNestedItem(constructSendEmailToItem(    request, binder      ));
					if (GwtShareHelper.isEntitySharable(bs, binder)) {
						miscTBI.addNestedItem(constructShareBinderItem(request, binder, null));
					}
					if ((binder instanceof Workspace) && bs.getSharingModule().isSharingEnabled()) {
						miscTBI.addNestedItem(constructShareWorkspaceRightsItem(request, binder));
					}
					miscTBI.addNestedItem(constructMobileUiItem(       request, binder      ));
					if (!isFilr) {
						miscTBI.addNestedItems(constructTrackBinderItem(bs,     binder      ));
					}
					if (isBinderTrashEnabled(binder)) {
						miscTBI.addNestedItem(constructTrashItem(      request, binder      ));
					}
				}
			}
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Adds the ToolBarItem's for the profiles binder to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on ProfilesBinderHelper.buildViewFolderToolbars()
	 */
	private static void buildProfilesMenuItems(AllModulesInjected bs, HttpServletRequest request, ProfileBinder pb, List<ToolbarItem> tbiList) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.buildProfilesMenuItems()");
		try {
			tbiList.add(constructFolderItems(Utils.checkIfFilr(), WebKeys.FOLDER_TOOLBAR, bs, request, pb, false, EntityType.profiles));
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Adds the ToolBarItem's for a workspace to the
	 * List<ToolBarItem> of them.
	 * 
	 * Based on WorkspaceTreeHelper.buildWorkspaceToolbar()
	 */
	private static void buildWorkspaceMenuItems(AllModulesInjected bs, HttpServletRequest request, Workspace ws, List<ToolbarItem> tbiList) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.buildWorkspaceMenuItems()");
		try {
			boolean isFilr = Utils.checkIfFilr();
			tbiList.add(constructFolderItems(  isFilr, WebKeys.FOLDER_TOOLBAR,    bs, request, ws, false, EntityType.workspace));
			tbiList.add(constructWhatsNewItems(isFilr, WebKeys.WHATS_NEW_TOOLBAR, bs, request, ws,        EntityType.workspace));
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns true of the user has the right to delete the entities
	 * contained in the binder and false otherwise.
	 */
	private static boolean canDeleteContainedEntries(AllModulesInjected bs, Binder binder) {
		return bs.getBinderModule().testAccess(binder, BinderOperation.deleteEntries);
	}
	
	/*
	 * Returns true of the user has the right to delete the entity and
	 * false otherwise.
	 * 
	 * They can delete the entity if they can trash or purge it.
	 */
	private static boolean canDeleteEntity(AllModulesInjected bs, DefinableEntity de) {
		return (canTrashEntity(bs, de) || canPurgeEntity(bs, de));
	}
	
	/*
	 * Returns true of the user has the right to purge the entity and
	 * false otherwise.
	 */
	private static boolean canPurgeEntity(AllModulesInjected bs, DefinableEntity de) {
		boolean reply;
		if (de instanceof FolderEntry) {
			reply = bs.getFolderModule().testAccess(((FolderEntry) de), FolderOperation.deleteEntry );
		}
		else if (de instanceof Binder) {
			Binder binder = ((Binder) de);
			reply = bs.getBinderModule().testAccess(binder, BinderOperation.deleteBinder);
			if (reply) {
				reply = (!(BinderHelper.isBinderDeleteProtected(binder)));
			}
		}
		else {
			reply = false;
		}
		return reply;
	}
	
	/*
	 * Returns true of the user has the right to trash the entity and
	 * false otherwise.
	 */
	private static boolean canTrashEntity(AllModulesInjected bs, DefinableEntity de) {
		boolean reply;
		if (de instanceof FolderEntry) {
			reply = bs.getFolderModule().testAccess(((FolderEntry) de), FolderOperation.preDeleteEntry );
		}
		else if (de instanceof Binder) {
			reply = bs.getBinderModule().testAccess(((Binder) de), BinderOperation.preDeleteBinder);
		}
		else {
			reply = false;
		}
		return reply;
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
	private static ToolbarItem constructCalendarImportItems(boolean isFilr, String tbKey, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Allocate the base ToolbarItem to return.
		ToolbarItem reply = new ToolbarItem(tbKey);
		
		// Are we running in other than simple Filr mode?
		if (!isFilr) {
			// Yes!  Are we looking at a calendar or task folder that
			// we can add entries to?
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
				
				// Load the localized strings we need for the menu
				// items...
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
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.constructEditBrandingItem()");
		try {
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
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Constructs a ToolbarItem for e-mail subscription handling.
	 */
	private static ToolbarItem constructEmailSubscriptionItems(boolean isFilr, String tbKey, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Allocate the base ToolbarItem to return.
		ToolbarItem reply = new ToolbarItem(tbKey);

		// If the current user is not guest, they have an email
		// address...
		User user = GwtServerHelper.getCurrentUser();
		if ((!(user.isShared())) && GwtEmailHelper.userHasEmailAddress(user)) {
			// ...add a ToolbarItem for e-mail notification.
			ToolbarItem emailTBI = new ToolbarItem(EMAIL);
			markTBITitle(emailTBI, "toolbar.menu.subscribeToFolder"       );
			markTBIHint( emailTBI, "toolbar.menu.title.emailSubscriptions");
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
	private static void constructEntryAddFileFolderItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws, Long homeFolderTargetId) {
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
				// Yes!  Can we find the template ID for a file folder?
				TemplateBinder	libraryTemplate;
				if (null == homeFolderTargetId)
				     libraryTemplate  = tm.getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_LIBRARY      );
				else libraryTemplate  = tm.getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_MIRRORED_FILE);
				Long folderTemplateId = ((null == libraryTemplate) ? null : libraryTemplate.getId());
				
				if (null != folderTemplateId) {
					// Yes!  Use the information we've got to add a
					// ToolbarItem to add a new folder.
					ToolbarItem addTBI = new ToolbarItem("1_add");
					markTBITitle(addTBI, "toolbar.new");
				
					ToolbarItem addFolderTBI = new ToolbarItem(ADD_FOLDER);
					markTBITitle(           addFolderTBI, "toolbar.menu.addFolder"           );
					markTBIEvent(           addFolderTBI, TeamingEvents.INVOKE_ADD_NEW_FOLDER);
					markTBIFolderTemplateId(addFolderTBI, folderTemplateId                   );
					markTBIFolderTargetId(  addFolderTBI, homeFolderTargetId                 );
					addTBI.addNestedItem(addFolderTBI);
					
					entryToolbar.addNestedItem(addTBI);
				}
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem for adding entries and nested folders
	 * to a folder.
	 * 
	 * At the point this gets called, we know that the user has rights
	 * to add entries to the folder.
	 */
	private static void constructEntryAddItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		// Define the toolbar item for the add items.
		ToolbarItem addTBI = new ToolbarItem("1_add");
		markTBITitle(addTBI, "toolbar.new");
		
		// Is the folder other than a mirrored folder or is it a
		// mirrored folder that can be written to?
		BinderModule   bm = bs.getBinderModule();
		TemplateModule tm = bs.getTemplateModule();
		if ((!(folder.isMirrored())) || isFolderWritableMirrored(folder)) {
			// Yes!  Create the various 'New' menu items for this
			// folder.
			constructEntryAddItems_Entry( bs, request, addTBI, folder, viewType);
			constructEntryAddItems_Folder(bm, tm,      addTBI, folder, viewType);
		}

		// If we added anything to the add toolbar item...
		if (addTBI.hasNestedToolbarItems()) {
			// ...add that to the entry toolbar.
			entryToolbar.addNestedItem(addTBI);
		}

		// Do we have a view type?
		if (MiscUtil.hasString(viewType)) {
			// Yes!  Handle the special add cases for view types that
			// need it.
			AdaptedPortletURL url;
			if (isViewPhotoAlbum(viewType)) {
				if (bm.testAccess(folder, BinderOperation.addFolder)) {
					TemplateBinder photoTemplate = tm.getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_PHOTO);
					if (photoTemplate != null) {
						url = createActionUrl(request);
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_BINDER);
		        		url.setParameter(WebKeys.URL_BINDER_ID, getFolderSetFolderId(bs, folder, viewType).toString());
						url.setParameter(WebKeys.URL_TEMPLATE_NAME, ObjectKeys.DEFAULT_TEMPLATE_NAME_PHOTO);
						
						addTBI = new ToolbarItem("1_add_folder"       );
						markTBITitle(addTBI, "toolbar.menu.add_photo_album_folder");
						// markTBIPopup(addTBI                                    );
						markTBIUrl(     addTBI, url                               );
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
						
						addTBI = new ToolbarItem("1_add_folder");
						markTBITitle(   addTBI, "toolbar.menu.add_wiki_folder");
						// markTBIPopup(addTBI                                );
						markTBIUrl(     addTBI, url                           );
						entryToolbar.addNestedItem(addTBI);
					}
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
	private static void constructEntryAddItems_Entry(AllModulesInjected bs, HttpServletRequest request, ToolbarItem addTBI, Folder folder, String viewType) {
		// Read the folder's entry definitions.
		List defaultEntryDefinitions = folder.getEntryDefinitions();
		int defaultEntryDefs = ((null == defaultEntryDefinitions) ? 0 : defaultEntryDefinitions.size());
		
		// Do we need to include new entry options?
		boolean isFileFolder      = GwtServerHelper.isFamilyFile(GwtServerHelper.getFolderEntityFamily(bs, folder));
		boolean includeNewEntries = ((!(Utils.checkIfFilr())) || (!(SsfsUtil.supportApplets(request))) || (1 < defaultEntryDefs) || (!isFileFolder));
		if (includeNewEntries) {
			// Yes!  Does this folder have more than one entry
			// definition or is for other than a guest book
			// folder?
			if ((1 < defaultEntryDefs) || (!(MiscUtil.hasString(viewType))) || (!(isViewGuestBook(viewType)))) {
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
					AdaptedPortletURL url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_FOLDER_ENTRY);
					url.setParameter(WebKeys.URL_BINDER_ID, folder.getId().toString());
					url.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
					String title = NLT.getDef(def.getTitle());
					if (null != usedTitles.get("title")) {
						title = (title + " (" + String.valueOf(count++) + ")");
					}
					
					ToolbarItem entriesTBI = new ToolbarItem("entries");
					markTBITitleRes(         entriesTBI, title);
					// markTBIPopup(         entriesTBI       );
					markTBIUrlAsForcedAnchor(entriesTBI, url  );
					if (i == defaultEntryDefIndex) {
						markTBIDefault(      entriesTBI);
					}
					addTBI.addNestedItem(    entriesTBI);
				}
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem for adding nested folders to a folder.
	 * 
	 * At the point this gets called, we know that the user has rights
	 * to add entries to the folder.
	 */
	private static void constructEntryAddItems_Folder(BinderModule bm, TemplateModule tm, ToolbarItem addTBI, Folder folder, String viewType) {
		if ((!(isViewBlog(viewType))) &&							// If not a blog folder and...
				(!(BinderHelper.isBinderMyFilesStorage(folder))) &&	// ...not a 'My Files Storage' folder and...
				bm.testAccess(folder, BinderOperation.addFolder)) {	// ...the user has rights.
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
						String tbInternalId = tb.getInternalId();
						if ((null != tbInternalId) && tbInternalId.equals(ObjectKeys.DEFAULT_FOLDER_FILR_ROOT_CONFIG)) {
							tb = tm.getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_FILR_NETFOLDER_FILE);
							folderTemplateId = ((null == tb) ? null : tb.getId());
						}
						else {
							folderTemplateId = tb.getId();
						}
						break;
					}
				}

				// Did we find the template ID for the folder?
				if (null == folderTemplateId) {
					// No!  Default to the first one in the list.
					folderTemplateId = folderTemplates.get(0).getId();
				}

				// If there are 'New' entry items in the menu...
				if (addTBI.hasNestedToolbarItems()) {
					// ...we need a separator between them and the
					// ...'New' folder item.
					addTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
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
	 * Constructs a ToolbarItem to configure the HTML element panel.
	 */
	private static void constructEntryConfigureHtmlElement(AllModulesInjected bs, HttpServletRequest request, Binder binder, List<ToolbarItem> configureToolbarItems) {
		// Does the binder have an HTML element in its definition?
		if (GwtHtmlElementHelper.getBinderHasHtmlElement(binder)) {
			// Yes!  Create a configure HTML element ToolbarItem.
			boolean hideHtmlElement;
			try                 {hideHtmlElement = GwtHtmlElementHelper.getHtmlElementStatus(bs, request, binder.getId());}
			catch (Exception e) {hideHtmlElement = false;}
			String titleKey;
			TeamingEvents event;
			if (hideHtmlElement) {
				titleKey = "misc.hideHtmlElement";
				event    = TeamingEvents.HIDE_HTML_ELEMENT;
			}
			else {
				titleKey = "misc.showHtmlElement";
				event    = TeamingEvents.SHOW_HTML_ELEMENT;
			}
			ToolbarItem heTBI = new ToolbarItem(CONFIGURE_HTML_ELEMENT);
			markTBITitle(heTBI, titleKey);
			markTBIEvent(heTBI, event   );
			configureToolbarItems.add(heTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem to configure the user list panel.
	 */
	private static void constructEntryConfigureUserList(AllModulesInjected bs, HttpServletRequest request, Binder binder, List<ToolbarItem> configureToolbarItems) {
		// Is the binder a Folder that has user_list items in its
		// definition?
		if ((binder instanceof Folder) && GwtUserListHelper.getFolderHasUserList((Folder) binder)) {
			// Yes!  Create a configure user list ToolbarItem.
			boolean hideUserList;
			try                 {hideUserList = GwtUserListHelper.getUserListStatus(bs, request, binder.getId());}
			catch (Exception e) {hideUserList = false;}
			String titleKey;
			TeamingEvents event;
			if (hideUserList) {
				titleKey = "misc.hideUserList";
				event    = TeamingEvents.HIDE_USER_LIST;
			}
			else {
				titleKey = "misc.showUserList";
				event    = TeamingEvents.SHOW_USER_LIST;
			}
			ToolbarItem ulTBI = new ToolbarItem(CONFIGURE_USER_LIST);
			markTBITitle(ulTBI, titleKey);
			markTBIEvent(ulTBI, event   );
			configureToolbarItems.add(ulTBI);
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
	private static void constructEntryDropBoxItem(ToolbarItem entryToolbar) {
		ToolbarItem dropBoxTBI = new ToolbarItem("dropBox");
		markTBITitle(dropBoxTBI, "toolbar.menu.dropBox.dialog");
		markTBIEvent(dropBoxTBI, TeamingEvents.INVOKE_DROPBOX);
		entryToolbar.addNestedItem(dropBoxTBI);
	}
	
	/*
	 * Constructs a ToolbarItem to run the configure columns dialog.
	 */
	private static ToolbarItem constructEntryConfigureColumsItem(Binder binder, boolean isBinderTrash) {
		// If we're not in a trash view...
		if (!isBinderTrash) {
			// ...can the user configure columns on this binder?
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
		}
		return null;
	}
	
	/*
	 * Constructs a ToolbarItem for deleting the selected entries.
	 */
	private static void constructEntryDeleteItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		// For the view types that support it...
		if (MiscUtil.hasString(viewType)) {
			if (folderSupportsDeleteAndPurge(folder, viewType)) {
				// ...and for which the user has rights to do it...
				if (canDeleteContainedEntries(bs, folder)) {
					// ...add a Delete item.
					constructEntryDeleteItem(entryToolbar);
				}
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem for deleting the selected entries.
	 */
	private static void constructEntryDeleteItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws, boolean isMyFilesCollection) {
		// If the user has rights to do it...
		if ((null == ws) || isMyFilesCollection || canDeleteContainedEntries(bs, ws)) {
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
		markTBIEvent(deleteTBI, TeamingEvents.DELETE_SELECTED_ENTITIES);
		entryToolbar.addNestedItem(deleteTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for running the entry viewer the
	 * selected entry.
	 */
	private static void constructEntryDetailsItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String resourceKey) {
		// Add a Details item.
		ToolbarItem detailsTBI = new ToolbarItem("1_detailsSelected");
		markTBITitle(detailsTBI, resourceKey);
		markTBIEvent(detailsTBI, TeamingEvents.VIEW_SELECTED_ENTRY);
		entryToolbar.addNestedItem(detailsTBI);
	}

	/*
	 * Constructs a ToolbarItem for download a folder as a CSV file.
	 */
	private static void constructEntryDownloadAsCSVFile(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		boolean canDownload = AdminHelper.getEffectiveDownloadSetting(bs, GwtServerHelper.getCurrentUser());
		if (canDownload && bs.getFolderModule().testAccess(folder, FolderOperation.downloadFolderAsCsv)) {
			ToolbarItem downloadAsCSVFileTBI = new ToolbarItem("1_downloadAsCSVFile");
			markTBITitle(   downloadAsCSVFileTBI, "toolbar.menu.downloadFolderAsCSVFile"   );
			markTBIEvent(   downloadAsCSVFileTBI, TeamingEvents.DOWNLOAD_FOLDER_AS_CSV_FILE);
			markTBIEntityId(downloadAsCSVFileTBI, folder                                   );
			entryToolbar.addNestedItem(downloadAsCSVFileTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for downloading a file.
	 */
	private static void constructEntryDownloadEntry(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, FolderEntry fe) {
		boolean canDownload = AdminHelper.getEffectiveDownloadSetting(bs, GwtServerHelper.getCurrentUser());
		if (canDownload && (null != GwtServerHelper.getFileEntrysFileAttachment(bs, fe, true))) {
			String downloadFileUrl;
			try                  {downloadFileUrl = GwtDesktopApplicationsHelper.getDownloadFileUrl(request, bs, fe.getId(), fe.getId(), true);}	// true -> Return a permalink URL.
			catch (Exception ex) {downloadFileUrl = null;}
			if (MiscUtil.hasString(downloadFileUrl)) {
				ToolbarItem downloadFileTBI = new ToolbarItem(FILE_DOWNLOAD  );
				markTBITitle(              downloadFileTBI, "toolbar.downloadFile");
    			markTBIUrlAsTargetedAnchor(downloadFileTBI, downloadFileUrl       );
				entryToolbar.addNestedItem(downloadFileTBI);
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem for making (or removing) a folder as a favorite.
	 */
	private static void constructEntryFavoriteItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, boolean isFavorite) {
		ToolbarItem favoriteTBI = new ToolbarItem("1_favoriteSelected");
		markTBITitle(       favoriteTBI, (isFavorite ? "toolbar.favorite.remove" : "toolbar.favorite.add"));
		markTBIEvent(       favoriteTBI, TeamingEvents.CHANGE_FAVORITE_STATE                              );
		markTBIMakeFavorite(favoriteTBI, (!isFavorite)                                                    );
		entryToolbar.addNestedItem(favoriteTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for the limit user visibility view.
	 */
	private static void constructEntryLimitUserVisibilityItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws) {
		// Add the 'Add' items...
		ToolbarItem luvTBI = new ToolbarItem("1_addLimitUserVisibility");
		markTBITitle(luvTBI, "toolbar.admin.addLimitUserVisibility");
		markTBIEvent(luvTBI, TeamingEvents.SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY);
		markTBIBoolean(luvTBI, "limited",         Boolean.TRUE);
		markTBIBoolean(luvTBI, "selectPrincipal", Boolean.TRUE);
		entryToolbar.addNestedItem(luvTBI);
		
		luvTBI = new ToolbarItem("1_addOverrideUserVisibility");
		markTBITitle(luvTBI, "toolbar.admin.addOverrideUserVisibility");
		markTBIEvent(luvTBI, TeamingEvents.SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY);
		markTBIBoolean(luvTBI, "override",        Boolean.TRUE);
		markTBIBoolean(luvTBI, "selectPrincipal", Boolean.TRUE);
		entryToolbar.addNestedItem(luvTBI);
		
		// ...and a 'Remove' item.
		luvTBI = new ToolbarItem("1_removeAdminRights");
		markTBITitle(luvTBI, "toolbar.admin.removeLimitUserVisibility");
		markTBIEvent(luvTBI, TeamingEvents.SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY);
		markTBIBoolean(luvTBI, "limited",  Boolean.FALSE);
		markTBIBoolean(luvTBI, "override", Boolean.FALSE);
		entryToolbar.addNestedItem(luvTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for the root manage administrators
	 * view.
	 */
	private static void constructEntryManageAdminsItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws) {
		// Add an 'Add' rights item...
		ToolbarItem manageAdminTBI = new ToolbarItem("1_addAdminRights");
		markTBITitle(manageAdminTBI, "toolbar.admin.addRights");
		markTBIEvent(manageAdminTBI, TeamingEvents.ADD_PRINCIPAL_ADMIN_RIGHTS);
		entryToolbar.addNestedItem(manageAdminTBI);
		
		// ...and a 'Remove' rights item.
		manageAdminTBI = new ToolbarItem("1_removeAdminRights");
		markTBITitle(manageAdminTBI, "toolbar.admin.removeRights");
		markTBIEvent(manageAdminTBI, TeamingEvents.SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS);
		markTBIBoolean(manageAdminTBI, "setRights", Boolean.FALSE);
		entryToolbar.addNestedItem(manageAdminTBI);
	}
	
	/*
	 * Constructs a ToolbarItem for managing the shares for the
	 * selected entries.
	 */
	@SuppressWarnings("unused")
	private static void constructEntryManageSharesItem(ToolbarItem entryToolbar, AllModulesInjected bs, String viewType, Folder folder) {
		// For the view types that support it...
		if (MiscUtil.hasString(viewType)) {
			if (folderSupportsShare(bs, folder, viewType)) {
				// ...add the 'Manage Shares...' menu item.
				constructEntryManageSharesItem(entryToolbar, bs);
			}
		}
	}
	
	/*
	 * Constructs the ToolbarItems for action menu on individual users
	 * in the Manage Users dialog.
	 */
	private static void constructEntryManageUserItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Long userId) {
		// Add a user properties item.
		ToolbarItem manageUserTBI = new ToolbarItem("1_userProperties");
		markTBITitle(manageUserTBI, "toolbar.details.userProperties");
		markTBIEvent(manageUserTBI, TeamingEvents.INVOKE_USER_PROPERTIES_DLG);
		entryToolbar.addNestedItem(manageUserTBI);

		// If we're dealing with a non-person user (e.g., E-Mail
		// Posting Agent, ...)...
		User user = ((User) bs.getProfileModule().getEntry(userId));
		if (!(user.isPerson())) {
			// ...there are no other options.  Bail.
			return;
		}
		IdentityInfo userII     = user.getIdentityInfo();
		boolean      isAdmin    = user.isSuper();
		boolean      isInternal =                userII.isInternal();
		boolean      isLdap     = (isInternal && userII.isFromLdap());
		boolean      isGuest    = user.isShared();
		
		UserPropertiesRpcResponseData	upData;
		try {
			// Yes!  Can we determine the user's current adHoc folder
			// access?
			upData = GwtViewHelper.getUserProperties(bs, request, userId, false);	// false -> Don't include the last login information.  It's expensive to obtain and for this usage, we don't need it.
		}
		catch (Exception ex) {
			// If we can't access the user properties information,
			// we simply don't add options that require it.
			return;
		}
		AccountInfo ai = upData.getAccountInfo();
		
		// Are we dealing with an LDAP user in filr mode?
		if (Utils.checkIfFilr() && isLdap) {
			// Yes!  Add a separator after the user properties item...
			entryToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());

			// ...and if they have adHoc folders...
			if (ai.hasAdHocFolders()) {
				// ...add the disable users adHoc folders item...
				manageUserTBI = new ToolbarItem("1_disableSelectedAdHoc");
				markTBITitle(manageUserTBI, "toolbar.disable.user.adHoc.perUser");
				markTBIEvent(manageUserTBI, TeamingEvents.DISABLE_SELECTED_USERS_ADHOC_FOLDERS);
				entryToolbar.addNestedItem(manageUserTBI);
			}
			
			else {
				// ...otherwise, if they don't have adHoc folders, add
				// ...the enable users adHoc folders item...
				manageUserTBI = new ToolbarItem("1_enableSelectedAdHoc");
				markTBITitle(manageUserTBI, "toolbar.enable.user.adHoc.perUser");
				markTBIEvent(manageUserTBI, TeamingEvents.ENABLE_SELECTED_USERS_ADHOC_FOLDERS);
				entryToolbar.addNestedItem(manageUserTBI);
			}

			// ...if they currently have a per user adHoc folder
			// ...setting...
			if (ai.isPerUserAdHoc()) {
				// ...and add the clear users adHoc folders item.
				manageUserTBI = new ToolbarItem("1_clearSelectedAdHoc");
				markTBITitle(manageUserTBI, "toolbar.clear.user.adHoc");
				markTBIEvent(manageUserTBI, TeamingEvents.CLEAR_SELECTED_USERS_ADHOC_FOLDERS);
				entryToolbar.addNestedItem(manageUserTBI);
			}
		}

		// Is this other than the built-in admin user?
		if (!isAdmin) {
			// Yes!  Is this Filr?
			if (Utils.checkIfFilr()) {
				// Yes!  Add a separator after the previous item...
				entryToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
		
				// ...and if they have download access...
				if (ai.canDownload()) {
					// ...add the disable download item...
					manageUserTBI = new ToolbarItem("1_disableSelectedDownload");
					markTBITitle(manageUserTBI, "toolbar.disable.user.download.perUser");
					markTBIEvent(manageUserTBI, TeamingEvents.DISABLE_SELECTED_USERS_DOWNLOAD);
					entryToolbar.addNestedItem(manageUserTBI);
				}
				
				else {
					// ...otherwise, if they can't download, add
					// ...the enable download item...
					manageUserTBI = new ToolbarItem("1_enableSelectedDownload");
					markTBITitle(manageUserTBI, "toolbar.enable.user.download.perUser");
					markTBIEvent(manageUserTBI, TeamingEvents.ENABLE_SELECTED_USERS_DOWNLOAD);
					entryToolbar.addNestedItem(manageUserTBI);
				}
		
				// ...if they currently have a per user download
				// ...setting...
				if (ai.isPerUserDownload()) {
					// ...and add the clear users download item.
					manageUserTBI = new ToolbarItem("1_clearSelectedDownload");
					markTBITitle(manageUserTBI, "toolbar.clear.user.download");
					markTBIEvent(manageUserTBI, TeamingEvents.CLEAR_SELECTED_USERS_DOWNLOAD);
					entryToolbar.addNestedItem(manageUserTBI);
				}
			}

			// Is this other than the guest user?
			if (!isGuest) {
				// Yes!  Add a separator after the previous item...
				entryToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
		
				// ...and if they have web access...
				if (ai.hasWebAccess()) {
					// ...add the disable web access item...
					manageUserTBI = new ToolbarItem("1_disableSelectedWebAccess");
					markTBITitle(manageUserTBI, "toolbar.disable.user.webAccess.perUser");
					markTBIEvent(manageUserTBI, TeamingEvents.DISABLE_SELECTED_USERS_WEBACCESS);
					entryToolbar.addNestedItem(manageUserTBI);
				}
				
				else {
					// ...otherwise, if they can't use web access, add
					// ...the enable web access item...
					manageUserTBI = new ToolbarItem("1_enableSelectedWebAccess");
					markTBITitle(manageUserTBI, "toolbar.enable.user.webAccess.perUser");
					markTBIEvent(manageUserTBI, TeamingEvents.ENABLE_SELECTED_USERS_WEBACCESS);
					entryToolbar.addNestedItem(manageUserTBI);
				}
		
				// ...if they currently have a per user web access
				// ...setting...
				if (ai.isPerUserWebAccess()) {
					// ...and add the clear users web access item.
					manageUserTBI = new ToolbarItem("1_clearSelectedWebAccess");
					markTBITitle(manageUserTBI, "toolbar.clear.user.webAccess");
					markTBIEvent(manageUserTBI, TeamingEvents.CLEAR_SELECTED_USERS_WEBACCESS);
					entryToolbar.addNestedItem(manageUserTBI);
				}
			}
		}
	}

	/*
	 * Constructs a ToolbarItem for the 'Manage Shares...' menu item.
	 */
	private static void constructEntryManageSharesItem(ToolbarItem entryToolbar, AllModulesInjected bs) {
    	// Is the current user a zone administrator?
		User currentUser = GwtServerHelper.getCurrentUser();
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		AccessControlManager accessControlManager = getAccessControlManager();
		accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		if (accessControlManager.testOperation(currentUser, zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION)) {
			// Yes!  Add the 'Manage Shares...' menu item.
			ToolbarItem tbi = new ToolbarItem("1_manageSharesSelected");
			markTBITitle(tbi, "toolbar.menu.manageSharesSelected");
			markTBIEvent(tbi, TeamingEvents.MANAGE_SHARES_SELECTED_ENTITIES);
			entryToolbar.addNestedItem(tbi);
		}
	}
	
	/*
	 * Constructs ToolbarItem's for marking the contents of a folder
	 * read or unread.
	 */
	private static void constructEntryMarkFolderContentsReadAndUnread(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// For other than the Guest user...
		boolean isGuest = GwtServerHelper.getCurrentUser().isShared();
		if (!isGuest) {
			// ...add a ToolbarItem for for marking the folder's
			// ...contents as having been read...
			ToolbarItem actionTBI = new ToolbarItem(SEEN_FOLDER);
			markTBITitle(   actionTBI, "toolbar.menu.markFolderContentsRead"  );
			markTBIEvent(   actionTBI, TeamingEvents.MARK_FOLDER_CONTENTS_READ);
			markTBIEntityId(actionTBI, folder                                 );
			entryToolbar.addNestedItem(actionTBI);
			
			// ...and one for for marking the folder's contents as
			// ...having been unread.
			actionTBI = new ToolbarItem(UNSEEN_FOLDER);
			markTBITitle(   actionTBI, "toolbar.menu.markFolderContentsUnread"  );
			markTBIEvent(   actionTBI, TeamingEvents.MARK_FOLDER_CONTENTS_UNREAD);
			markTBIEntityId(actionTBI, folder                                   );
			entryToolbar.addNestedItem(actionTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for miscellaneous operations against
	 * the selected entries.
	 */
	private static void constructEntryMoreItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, WorkArea wa, Long folderId, String viewType, Folder folder, Workspace ws, CollectionType ct) {
		User    user                = GwtServerHelper.getCurrentUser();
		boolean isGuest             = user.isShared();
		boolean isFilr              = Utils.checkIfFilr();
		boolean isFilrGuest         = (isFilr && isGuest);
		boolean isFolder            = (null != folder);
		boolean isMyFilesCollection = CollectionType.MY_FILES.equals(ct);
		boolean isSharedCollection  = ct.isSharedCollection();
		boolean isEntryContainer    = (isFolder || isMyFilesCollection || isSharedCollection);
		
		// Create the more toolbar item...
		ToolbarItem moreTBI = new ToolbarItem("1_more");
		markTBITitle(moreTBI, "toolbar.more");

		// ...if this is not the Filr Guest user...
		ToolbarItem tbi;
		if (!isFilrGuest) {
			// ...add the copy item...
			tbi = new ToolbarItem("1_copySelected");
			markTBITitle(tbi, "toolbar.copy");
			markTBIEvent(tbi, TeamingEvents.COPY_SELECTED_ENTITIES);
			moreTBI.addNestedItem(tbi);

			// ...for non-shared collections...
			if ((!isSharedCollection) && ((null == folder) || GwtShareHelper.visibleWithoutShares(bs, user, folder))) {
				// ...add the move item....
				tbi = new ToolbarItem("1_moveSelected");
				markTBITitle(tbi, "toolbar.move");
				markTBIEvent(tbi, TeamingEvents.MOVE_SELECTED_ENTITIES);
				moreTBI.addNestedItem(tbi);
			}
		}
		
		// ...for My Files, Shared by/with Me lists and file folders...
		boolean canDownload = AdminHelper.getEffectiveDownloadSetting(bs, user);
		if (canDownload && isEntryContainer && (isMyFilesCollection || isSharedCollection || GwtServerHelper.isFamilyFile(GwtServerHelper.getFolderEntityFamily(bs, folder)))) {
			// ...allow the user to zip and download the selected
			// ...files...
			tbi = new ToolbarItem("1_zipAndDownloadSelected");
			markTBITitle(tbi, "toolbar.zipAndDownload");
			markTBIEvent(tbi, TeamingEvents.ZIP_AND_DOWNLOAD_SELECTED_FILES);
			moreTBI.addNestedItem(tbi);
		}
		
		// ...for views that can contain entries...
		if (isEntryContainer) {
			// ...if we're not in Filr mode...
			if (!isFilr) {
				// ...add the lock item....
				tbi = new ToolbarItem("1_lockSelected");
				markTBITitle(tbi, "toolbar.lock");
				markTBIEvent(tbi, TeamingEvents.LOCK_SELECTED_ENTITIES);
				moreTBI.addNestedItem(tbi);
				
				// ...add the unlock item....
				tbi = new ToolbarItem("1_unlockSelected");
				markTBITitle(tbi, "toolbar.unlock");
				markTBIEvent(tbi, TeamingEvents.UNLOCK_SELECTED_ENTITIES);
				moreTBI.addNestedItem(tbi);
			}
			
			// ...if the user is not the Guest user...
			if (!isGuest) {
				// ...add the mark read....
				tbi = new ToolbarItem("1_markReadSelected");
				markTBITitle(tbi, "toolbar.markRead");
				markTBIEvent(tbi, TeamingEvents.MARK_READ_SELECTED_ENTITIES);
				moreTBI.addNestedItem(tbi);
				
				// ...and the mark unread items....
				tbi = new ToolbarItem("1_markUnreadSelected");
				markTBITitle(tbi, "toolbar.markUnread");
				markTBIEvent(tbi, TeamingEvents.MARK_UNREAD_SELECTED_ENTITIES);
				moreTBI.addNestedItem(tbi);
			}
		}

		// ...if this is other than the guest user...
		if (!isGuest) {
			// ...for a 'Shared By/With Me' collection...
			if (isSharedCollection) {
				// ...we allow shares to be hidden or shown...
				tbi = new ToolbarItem("1_hideSelected");
				markTBITitle(tbi, "toolbar.hideShares");
				markTBIEvent(tbi, TeamingEvents.HIDE_SELECTED_SHARES);
				moreTBI.addNestedItem(tbi);
				
				tbi = new ToolbarItem("1_showSelected");
				markTBITitle(tbi, "toolbar.showShares");
				markTBIEvent(tbi, TeamingEvents.SHOW_SELECTED_SHARES);
				moreTBI.addNestedItem(tbi);
			}
			
			// ...add a separator item if needed...
			if (moreTBI.hasNestedToolbarItems() && isEntryContainer) {
				moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
			}
			
			if (isEntryContainer && (!isFilr)) {
				// ...add the change entry type item when not Filr....
				tbi = new ToolbarItem("1_changeEntryTypeSelected");
				markTBITitle(tbi, "toolbar.changeEntryType");
				markTBIEvent(tbi, TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTITIES);
				moreTBI.addNestedItem(tbi);
			}
	
			// ...add the subscribe item...
			constructEntrySubscribeItem(moreTBI, bs, request, wa, false);
			
			// ...and add the 'Manage Shares...' menu item.  This will
			// ...only be available for the admin users.
			constructEntryManageSharesItem(moreTBI, bs);
		}

		// If we added anything to the more toolbar...
		if (!(moreTBI.getNestedItemsList().isEmpty())) {
			// ...and the more toolbar to the entry toolbar.
			entryToolbar.addNestedItem(moreTBI);
		}
	}

	/*
	 * Constructs a ToolbarItem for viewing pinned vs. non-pinned
	 * entries.
	 */
	private static void constructEntryPinnedItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Is this a folder that supports pinning entries?
		if ((null != folder) && folderSupportsPinning(bs, folder)) {
			// Yes!  Add the pinned item.
			ToolbarItem pinnedTBI = new ToolbarItem("1_viewPinned");
			markTBIEvent(pinnedTBI, TeamingEvents.VIEW_PINNED_ENTRIES);
			entryToolbar.addNestedItem(pinnedTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for the root profiles workspace view.
	 * 
	 * The initial logic for this was copied from
	 * ProfilesBinderHelper.buildViewEntryToolbar().
	 */
	@SuppressWarnings("unchecked")
	private static void constructEntryProfilesRootWSItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws, boolean manageUsers) {
		// If we're not in the manage users version of the profiles
		// root WS viewer...
		if (!manageUsers) {
			// ...there are no menu items.  Bail.
			return;
		}
		
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
				url.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
				ToolbarItem addUserTBI = new ToolbarItem("1_add");
				markTBITitle(    addUserTBI, "toolbar.new");
				markTBIPopup(    addUserTBI               );
				markTBIHighlight(addUserTBI               );
				markTBIUrl(      addUserTBI, url          );
				entryToolbar.addNestedItem(addUserTBI);
			}

			// Add the ability to import profiles.
			ToolbarItem importProfilesTBI = new ToolbarItem("1_importProfiles");
			markTBITitle(importProfilesTBI, "toolbar.importProfiles");
			markTBIEvent(importProfilesTBI, TeamingEvents.INVOKE_IMPORT_PROFILES_DLG);
			entryToolbar.addNestedItem(importProfilesTBI);
		}
		
		// If the user can delete binders from the workspace...
		boolean canManageProfiles = pm.testAccess(((ProfileBinder) ws), ProfileOperation.manageEntries);
		boolean	canTrash          = canManageProfiles;	// Should we be checking something else?
		if (canTrash) {
			// ...and add the delete users item.
			ToolbarItem deleteTBI = new ToolbarItem("1_deletedSelectedWS");
			markTBITitle(deleteTBI, "toolbar.delete.users");
			markTBIEvent(deleteTBI, TeamingEvents.DELETE_SELECTED_USERS);
			entryToolbar.addNestedItem(deleteTBI);
		}
			
		// Create a 'more' item for the disable/enable items.
		ToolbarItem tbi;
		ToolbarItem moreTBI = new ToolbarItem("1_more");
		markTBITitle(moreTBI, "toolbar.more");
		
		// If the user can manage entries in the workspace...
		boolean needSeparator     = false;
		if (canManageProfiles) {
			// ...add the disable users item...
			tbi = new ToolbarItem("1_disableSelected");
			markTBITitle(tbi, "toolbar.disable.user");
			markTBIEvent(tbi, TeamingEvents.DISABLE_SELECTED_USERS);
			moreTBI.addNestedItem(tbi);
			
			// ...add the enable users item...
			tbi = new ToolbarItem("1_enableSelected");
			markTBITitle(tbi, "toolbar.enable.user");
			markTBIEvent(tbi, TeamingEvents.ENABLE_SELECTED_USERS);
			moreTBI.addNestedItem(tbi);

			// ...if we're in filr mode...
			if (Utils.checkIfFilr()) {
				// ...add the disable users adHoc folders item...
				moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
				tbi = new ToolbarItem("1_disableSelectedAdHoc");
				markTBITitle(tbi, "toolbar.disable.user.adHoc");
				markTBIEvent(tbi, TeamingEvents.DISABLE_SELECTED_USERS_ADHOC_FOLDERS);
				moreTBI.addNestedItem(tbi);
				
				// ...add the enable users adHoc folders item...
				tbi = new ToolbarItem("1_enableSelectedAdHoc");
				markTBITitle(tbi, "toolbar.enable.user.adHoc");
				markTBIEvent(tbi, TeamingEvents.ENABLE_SELECTED_USERS_ADHOC_FOLDERS);
				moreTBI.addNestedItem(tbi);
				
				// ...and add the clear users adHoc folders item.
				tbi = new ToolbarItem("1_clearSelectedAdHoc");
				markTBITitle(tbi, "toolbar.clear.user.adHoc");
				markTBIEvent(tbi, TeamingEvents.CLEAR_SELECTED_USERS_ADHOC_FOLDERS);
				moreTBI.addNestedItem(tbi);
			}

			// ...if this is Filr...
			if (Utils.checkIfFilr()) {
				// ...add the disable users download files item...
				moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
				tbi = new ToolbarItem("1_disableSelectedDownload");
				markTBITitle(tbi, "toolbar.disable.user.download");
				markTBIEvent(tbi, TeamingEvents.DISABLE_SELECTED_USERS_DOWNLOAD);
				moreTBI.addNestedItem(tbi);
				
				// ...add the enable users download files item...
				tbi = new ToolbarItem("1_enableSelectedDownload");
				markTBITitle(tbi, "toolbar.enable.user.download");
				markTBIEvent(tbi, TeamingEvents.ENABLE_SELECTED_USERS_DOWNLOAD);
				moreTBI.addNestedItem(tbi);
				
				// ...and add the clear users download files item.
				tbi = new ToolbarItem("1_clearSelectedDownload");
				markTBITitle(tbi, "toolbar.clear.user.download");
				markTBIEvent(tbi, TeamingEvents.CLEAR_SELECTED_USERS_DOWNLOAD);
				moreTBI.addNestedItem(tbi);
			}
			
			// ...add the disable users web access item...
			moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
			tbi = new ToolbarItem("1_disableSelectedWebAccess");
			markTBITitle(tbi, "toolbar.disable.user.webAccess");
			markTBIEvent(tbi, TeamingEvents.DISABLE_SELECTED_USERS_WEBACCESS);
			moreTBI.addNestedItem(tbi);
			
			// ...add the enable users web access item...
			tbi = new ToolbarItem("1_enableSelectedWebAccess");
			markTBITitle(tbi, "toolbar.enable.user.webAccess");
			markTBIEvent(tbi, TeamingEvents.ENABLE_SELECTED_USERS_WEBACCESS);
			moreTBI.addNestedItem(tbi);
			
			// ...and add the clear users web access item.
			tbi = new ToolbarItem("1_clearSelectedWebAccess");
			markTBITitle(tbi, "toolbar.clear.user.webAccess");
			markTBIEvent(tbi, TeamingEvents.CLEAR_SELECTED_USERS_WEBACCESS);
			moreTBI.addNestedItem(tbi);
			
			needSeparator = true;
		}
		
		// ...if the user can manage profiles...
		boolean needSep2 = false;
		if (canManageProfiles) {
			// ...if needed add a separator item...
			addNestedSeparatorIfNeeded(moreTBI, (needSep2 || needSeparator));
			needSeparator =
			needSep2      = false;
			
			// ...add the set selected user share rights...
			tbi = new ToolbarItem("1_setShareRights");
			markTBITitle(tbi, "toolbar.setUserWSShareRights");
			markTBIEvent(tbi, TeamingEvents.SET_SELECTED_USER_SHARE_RIGHTS);
			moreTBI.addNestedItem(tbi);
		}
		
		// ...add the set selected user desktop and mobile rights...
		addNestedSeparatorIfNeeded(moreTBI, (needSep2 || needSeparator));
		needSeparator =
		needSep2      = false;
		tbi = new ToolbarItem("1_setDesktopSettings");
		markTBITitle(tbi, "toolbar.setUserDesktopSettings");
		markTBIEvent(tbi, TeamingEvents.SET_SELECTED_USER_DESKTOP_SETTINGS);
		moreTBI.addNestedItem(tbi);
		if (LicenseChecker.showFilrFeatures()) {
			tbi = new ToolbarItem("1_setMobileSettings");
			markTBITitle(tbi, "toolbar.setUserMobileSettings");
			markTBIEvent(tbi, TeamingEvents.SET_SELECTED_USER_MOBILE_SETTINGS);
			moreTBI.addNestedItem(tbi);
		}

		// ...if the current user is the built-in admin user...
		boolean isAdmin = GwtServerHelper.getCurrentUser().isAdmin();
		if (isAdmin) {
			// ...handles admin override for the admin console...
			User parentUser = RequestContextHolder.getRequestContext().getParentUser();
			isAdmin = ((null == parentUser) || parentUser.isAdmin());
		}
		if (isAdmin) {
			// ...and add the set/clear admin rights.
			moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
			tbi = new ToolbarItem("1_setAdminRights");
			markTBITitle(tbi, "toolbar.adminRightsSet");
			markTBIEvent(tbi, TeamingEvents.SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS);
			markTBIBoolean(tbi, "setRights", Boolean.TRUE);
			moreTBI.addNestedItem(tbi);
			
			tbi = new ToolbarItem("1_clearAdminRights");
			markTBITitle(tbi, "toolbar.adminRightsClear");
			markTBIEvent(tbi, TeamingEvents.SET_SELECTED_PRINCIPALS_ADMIN_RIGHTS);
			markTBIBoolean(tbi, "setRights", Boolean.FALSE);
			moreTBI.addNestedItem(tbi);
		}

		// If passwords can expire...
		if (PasswordPolicyHelper.passwordExpirationEnabled()) {
			// ...add the force users to change password option.
			moreTBI.addNestedItem(ToolbarItem.constructSeparatorTBI());
			tbi = new ToolbarItem("1_forcePasswordChange");
			markTBITitle(tbi, "toolbar.force.users.to.change.password");
			markTBIEvent(tbi, TeamingEvents.FORCE_SELECTED_USERS_TO_CHANGE_PASSWORD);
			moreTBI.addNestedItem(tbi);
		}
				
		// Finally, if we added anything to the more toolbar...
		if (!(moreTBI.getNestedItemsList().isEmpty())) {
			// ...and the more toolbar to the entry toolbar.
			entryToolbar.addNestedItem(moreTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem to rename a folder.
	 */
	private static void constructEntryRenameFolder(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Does the user have rights to rename this folder?  Note that
		// even with rights, they can't rename a 'Home' folder.
		if (bs.getBinderModule().testAccess(folder, BinderOperation.renameBinder) && (!(BinderHelper.isBinderHomeFolder(folder)))) {
			// Yes!  Add a Rename ToolbarItem.
			ToolbarItem renameTBI = new ToolbarItem(RENAME);
			markTBITitle(renameTBI, "toolbar.menu.rename_folder"      );
			markTBIEvent(renameTBI, TeamingEvents.INVOKE_RENAME_ENTITY);
			entryToolbar.addNestedItem(renameTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem to rename a file.
	 */
	private static void constructEntryRenameFile(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, FolderEntry fe) {
		// Does the user have rights to rename this entry?
		if (bs.getFolderModule().testAccess(fe, FolderOperation.renameEntry)) {
			// Yes!  Is it a file entry?
			String feFamily = GwtServerHelper.getFolderEntityFamily(bs, fe);
			if (GwtServerHelper.isFamilyFile(feFamily)) {
				// Yes!  Does it have a file attached?
				FileAttachment fa = GwtServerHelper.getFileEntrysFileAttachment(bs, fe, false);
				if (null != fa) {
					// Yes!  Add a Rename ToolbarItem.
					ToolbarItem renameTBI = new ToolbarItem(RENAME);
					markTBITitle(renameTBI, "toolbar.menu.rename_file"        );
					markTBIEvent(renameTBI, TeamingEvents.INVOKE_RENAME_ENTITY);
					entryToolbar.addNestedItem(renameTBI);
				}
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem to rename a workspace.
	 */
	private static void constructEntryRenameWorkspace(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws) {
		// Does the user have rights to rename this workspace?
		if (bs.getBinderModule().testAccess(ws, BinderOperation.renameBinder)) {
			// Yes!  Add a Rename ToolbarItem.
			ToolbarItem renameTBI = new ToolbarItem(RENAME);
			markTBITitle(renameTBI, "toolbar.menu.rename_workspace"   );
			markTBIEvent(renameTBI, TeamingEvents.INVOKE_RENAME_ENTITY);
			entryToolbar.addNestedItem(renameTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for sharing the selected entries.
	 */
	@SuppressWarnings("unused")
	private static void constructEntryShareItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, String viewType, Folder folder) {
		// For the view types that support it...
		if (MiscUtil.hasString(viewType)) {
			if (folderSupportsShare(bs, folder, viewType)) {
				// ...construct the share item.
				constructEntryShareItem(
					entryToolbar,
					bs,
					request,
					folder,
					GwtServerHelper.isFamilyFile(
						GwtServerHelper.getFolderEntityFamily(
							bs,
							folder)));
			}
		}
	}
	
	/*
	 * Constructs a ToolbarItem for sharing the selected entries.
	 */
	private static void constructEntryShareItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, DefinableEntity de, boolean isFileEntity) {
		// For non-guest users...
		boolean isGuest = GwtServerHelper.getCurrentUser().isShared();
		if (!isGuest) {
			// ...add the share items.
			boolean isFilr   = Utils.checkIfFilr();
			boolean isFolder = de.getEntityType().equals(EntityType.folder);
			String keyTail;
			if      (isFolder) keyTail = "folder";
			else if (isFilr)   keyTail = "file";
			else               keyTail = "entry";
			
			ToolbarItem shareTBI;
			SharingModule sm = bs.getSharingModule();
			if (sm.testAddShareEntity(de)) {
				shareTBI = new ToolbarItem("1_shareSelected");
				markTBITitle(shareTBI, "toolbar.shareSelected." + keyTail);
				markTBIEvent(shareTBI, TeamingEvents.SHARE_SELECTED_ENTITIES);
				entryToolbar.addNestedItem(shareTBI);
			}
			
			if ((!isFolder) && isFileEntity && sm.testAddShareEntityPublicLinks(de)) {
				if (isFilr)
				     keyTail = "filr";
				else keyTail = "vibe";
				
				FolderEntry fe  = ((FolderEntry) de);
				EntityId    eid = new EntityId(fe.getParentFolder().getId(), fe.getId(), EntityType.folderEntry.name());
				if (GwtShareHelper.hasPublicLinks(bs, GwtServerHelper.getCurrentUserId(), eid)) {
					shareTBI = new ToolbarItem("1_editPublicLinkSelected");
					markTBITitle(shareTBI, "toolbar.editPublicLinkSelected." + keyTail);
					markTBIEvent(shareTBI, TeamingEvents.EDIT_PUBLIC_LINK_SELECTED_ENTITIES);
					entryToolbar.addNestedItem(shareTBI);
				}
				else {
					shareTBI = new ToolbarItem("1_copyPublicLinkSelected");
					markTBITitle(shareTBI, "toolbar.copyPublicLinkSelected." + keyTail);
					markTBIEvent(shareTBI, TeamingEvents.COPY_PUBLIC_LINK_SELECTED_ENTITIES);
					entryToolbar.addNestedItem(shareTBI);
				}
				
				shareTBI = new ToolbarItem("1_mailtoPublicLink");
				markTBITitle(shareTBI, "toolbar.mailtoPublicLink." + keyTail);
				markTBIEvent(shareTBI, TeamingEvents.MAILTO_PUBLIC_LINK_ENTITY);
				entryToolbar.addNestedItem(shareTBI);
				
				shareTBI = new ToolbarItem("1_emailPublicLinkSelected");
				markTBITitle(shareTBI, "toolbar.emailPublicLinkSelected." + keyTail);
				markTBIEvent(shareTBI, TeamingEvents.EMAIL_PUBLIC_LINK_SELECTED_ENTITIES);
				entryToolbar.addNestedItem(shareTBI);
			}
		}
	}
	
	/*
	 * Constructs the ToolbarItems for the share menu on a entry menu
	 * bar.
	 */
	private static void constructEntryShareItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, boolean isSWM, boolean isFileEntity) {
		// Are we logged in as Guest?
		boolean isGuest = GwtServerHelper.getCurrentUser().isShared();
		if (!isGuest) {
			// No!  Are we constructing share items for a file entity?
			if (isFileEntity) {
				// Yes!  Is it for other than a Shared with Me view or
				// does the user have share forwarding rights?
				if ((!isSWM) || GwtShareHelper.isShareForwardingEnabled(bs)) {
					// Yes!  Create the share toolbar...
					ToolbarItem shareItemsTBI = new ToolbarItem("1_share");
					markTBITitle(shareItemsTBI, "toolbar.share");
	
					ToolbarItem shareTBI;
					if (GwtShareHelper.isSharingEnabled(bs)) {
						// ...add the share item...
						shareTBI = new ToolbarItem(SHARE);
						markTBITitle(shareTBI, "toolbar.shareSelected");
						markTBIEvent(shareTBI, TeamingEvents.SHARE_SELECTED_ENTITIES);
						shareItemsTBI.addNestedItem(shareTBI);
					}
					
					if (GwtShareHelper.isSharingPublicLinksEnabled(bs)) {
						// ...add the copy public link item...
						String keyTail;
						if (Utils.checkIfFilr())
						     keyTail = "filr";
						else keyTail = "vibe";
						shareTBI = new ToolbarItem(COPY_PUBLIC_LINK);
						markTBITitle(shareTBI, "toolbar.copyPublicLinkSelected." + keyTail);
						markTBIEvent(shareTBI, TeamingEvents.COPY_PUBLIC_LINK_SELECTED_ENTITIES);
						shareItemsTBI.addNestedItem(shareTBI);
						
						// ...add the e-mail public link item...
						shareTBI = new ToolbarItem(EMAIL_PUBLIC_LINK);
						markTBITitle(shareTBI, "toolbar.emailPublicLinkSelected." + keyTail);
						markTBIEvent(shareTBI, TeamingEvents.EMAIL_PUBLIC_LINK_SELECTED_ENTITIES);
						shareItemsTBI.addNestedItem(shareTBI);
					}
					
					if (shareItemsTBI.hasNestedToolbarItems()) {
						// ...and the share toolbar to the entry
						// ...toolbar.
						entryToolbar.addNestedItem(shareItemsTBI);
					}
				}
			}
			
			else {
				// No, we aren't constructing share items for a file
				// entity!  Add a simple share item.
				ToolbarItem shareTBI = new ToolbarItem(SHARE);
				markTBITitle(shareTBI, "toolbar.shareSelected");
				markTBIEvent(shareTBI, TeamingEvents.SHARE_SELECTED_ENTITIES);
				entryToolbar.addNestedItem(shareTBI);
			}
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
	 * Constructs a ToolbarItem for the root team workspace view.
	 */
	@SuppressWarnings("unchecked")
	private static void constructEntryRootWSItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Workspace ws, WorkspaceType wt) {
		// If the user can add entries...
		BinderModule bm = bs.getBinderModule();
		if (bm.testAccess(ws, BinderOperation.addWorkspace)) {
			// ...add a 'New Workspace...' menu item.
			Long cfgType = null;
			List result = bs.getTemplateModule().getTemplates(Definition.WORKSPACE_VIEW);
			if (result.isEmpty()) {
				result.add(bs.getTemplateModule().addDefaultTemplate(Definition.WORKSPACE_VIEW));	
			}
			for (int i = 0; i < result.size(); i++) {
				TemplateBinder tb = (TemplateBinder) result.get(i);
				if (tb.getInternalId() != null && tb.getInternalId().toString().equals(ObjectKeys.DEFAULT_TEAM_WORKSPACE_CONFIG)) {
					//We have found the team workspace template, get its config id
					cfgType = tb.getId();
					break;
				}
			}
			if (cfgType != null) {
				AdaptedPortletURL url = createActionUrl(request);
				url.setParameter(WebKeys.ACTION,        WebKeys.ACTION_ADD_BINDER);
				url.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(ws.getId()));
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_ADD_TEAM_WORKSPACE);
				url.setParameter(WebKeys.URL_BINDER_CONFIG_ID, cfgType.toString());
				url.setParameter(WebKeys.URL_ADD_DEFAULT_ENTRY_FROM_INFRAME, "1");
				
				ToolbarItem addTBI = new ToolbarItem(ADD_WORKSPACE);
				markTBIPopup(addTBI                             );
				markTBITitle(addTBI, "team.addTeam"             );
				markTBIUrl(  addTBI, url                        );
				entryToolbar.addNestedItem(addTBI);
			}
		}

		// If the user can delete items from the workspace...
		if (bm.testAccess(ws, BinderOperation.deleteBinder) || bm.testAccess(ws, BinderOperation.preDeleteBinder)) {
			// ...add a delete item.
			constructEntryDeleteItem(entryToolbar);
		}
		
		// Create a 'more' item for additional items.
		ToolbarItem tbi;
		ToolbarItem moreTBI = new ToolbarItem("1_more");
		markTBITitle(moreTBI, "toolbar.more");

		// If sharing is enabled...
		boolean canSetSharingRights = bs.getSharingModule().isSharingEnabled();
		if (canSetSharingRights) {
			// ...and we support sharing from this root workspace...
			canSetSharingRights =
				((wt.isTeamRoot() || wt.isGlobalRoot()) && 
				bs.getAdminModule().testAccess(AdminOperation.manageFunction));
		}
		if (canSetSharingRights) {
			// ...add the set selected share rights.
			tbi = new ToolbarItem("1_setShareRights");
			String titleKey = "toolbar.setShareRights.";
			if (wt.isGlobalRoot())
			     titleKey += "Workspace";
			else titleKey += "Team";
			markTBITitle(tbi, titleKey);
			markTBIEvent(tbi, TeamingEvents.SET_SELECTED_BINDER_SHARE_RIGHTS);
			moreTBI.addNestedItem(tbi);
		}
		
		// Finally, if we added anything to the more toolbar...
		if (!(moreTBI.getNestedItemsList().isEmpty())) {
			// ...and the more toolbar to the entry toolbar.
			entryToolbar.addNestedItem(moreTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for trash views.
	 */
	private static void constructEntryTrashItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Binder binder) {
		ToolbarItem trashTBI = new ToolbarItem("1_trashRestore");
		markTBITitle(trashTBI, "toolbar.menu.trash.restore");
		markTBIEvent(trashTBI, TeamingEvents.TRASH_RESTORE_SELECTED_ENTITIES);
		entryToolbar.addNestedItem(trashTBI);
		
		trashTBI = new ToolbarItem("2_trashPurge");
		markTBITitle(trashTBI, "toolbar.menu.trash.purge");
		markTBIEvent(trashTBI, TeamingEvents.TRASH_PURGE_SELECTED_ENTITIES);
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
	 * Constructs a ToolbarItem for subscribing to an item.
	 */
	private static void constructEntrySubscribeItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, WorkArea wa, boolean separatorBefore) {
		User user = GwtServerHelper.getCurrentUser();
		boolean isGuest = user.isShared();
		if ((!isGuest) && GwtEmailHelper.userHasEmailAddress(user)) {
			// ...add the subscribe item.
			if (separatorBefore) {
				entryToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
			}
			ToolbarItem tbi = new ToolbarItem("1_subscribeSelected");
			markTBITitle(tbi, "toolbar.menu.subscribeToEntrySelected");
			markTBIEvent(tbi, TeamingEvents.SUBSCRIBE_SELECTED_ENTITIES);
			entryToolbar.addNestedItem(tbi);
		}
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
		markTBITitle(viewTBI, "calendar.navi.chooseMode.gwt");
		markTBIContentsSelectable(viewTBI);

		// ...if the calendar folder is directly contained by a user...
		// ...workspace...
		CalendarShow currentMode = null;
		Workspace folderWs = BinderHelper.getBinderWorkspace(folder);
		if (BinderHelper.isBinderUserWorkspace(folderWs)) {
			// ...add the 'assigned events' item...
			ToolbarItem tbi = new ToolbarItem("1_assigned");
			markTBITitle(       tbi, "calendar.navi.mode.alt.virtual");
			markTBIEvent(       tbi, TeamingEvents.CALENDAR_SHOW);
			markTBICalendarShow(tbi, CalendarShow.VIRTUAL);
			if (eventType.equals(EventsViewHelper.EVENT_TYPE_VIRTUAL)) {
				currentMode = CalendarShow.VIRTUAL;
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
			currentMode = CalendarShow.PHYSICAL_EVENTS;
			markTBISelected(tbi);
		}
		viewTBI.addNestedItem(tbi);

		// ...add the 'from folder by creation' item...
		tbi = new ToolbarItem("1_fromFolderByCreation");
		markTBITitle(       tbi, "calendar.navi.mode.alt.physical.byCreation");
		markTBIEvent(       tbi, TeamingEvents.CALENDAR_SHOW);
		markTBICalendarShow(tbi, CalendarShow.PHYSICAL_BY_CREATION);
		if (eventType.equals(EventsViewHelper.EVENT_TYPE_CREATION)) {
			currentMode = CalendarShow.PHYSICAL_BY_CREATION;
			markTBISelected(tbi);
		}
		viewTBI.addNestedItem(tbi);

		// ...add the 'from folder by activity' item...
		tbi = new ToolbarItem("1_fromFolderByActivity");
		markTBITitle(       tbi, "calendar.navi.mode.alt.physical.byActivity");
		markTBIEvent(       tbi, TeamingEvents.CALENDAR_SHOW);
		markTBICalendarShow(tbi, CalendarShow.PHYSICAL_BY_ACTIVITY);
		if (eventType.equals(EventsViewHelper.EVENT_TYPE_ACTIVITY)) {
			currentMode = CalendarShow.PHYSICAL_BY_ACTIVITY;
			markTBISelected(tbi);
		}
		viewTBI.addNestedItem(tbi);

		// ...add the view toolbar to the entry toolbar...
		entryToolbar.addNestedItem(viewTBI);
		
		// ...and finally, if we're not simple show physical events
		// ...from the folder...
		if (!(currentMode.equals(CalendarShow.PHYSICAL_EVENTS))) {
			// ...add an item the user can use to display a hint about
			// ...the current CalendarShow mode.
			ToolbarItem hintTBI = new ToolbarItem("2_view");
			markTBITitle(       hintTBI, ("calendar.navi.chooseMode.hint." + eventType));
			markTBIEvent(       hintTBI, TeamingEvents.CALENDAR_SHOW_HINT);
			markTBICalendarShow(hintTBI, currentMode);
			entryToolbar.addNestedItem(hintTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for running the entry viewer the
	 * selected entry.
	 */
	private static void constructEntryViewHtmlItem(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, FolderEntry fe) {
		// Is the entry a file entry with an attachment with a
		// filename?
		FileAttachment fa = GwtServerHelper.getFileEntrysFileAttachment(bs, fe);
		if (null != fa) {
			// Yes!  Do we support viewing that type of file as
    		// HTML?
			String fName = fa.getFileItem().getName();
    		if (SsfsUtil.supportsViewAsHtml(fName)) {
				try {
	        		// Yes!  Generate a toolbar item contain the URL to
					// view its HTML.
					ViewFileInfo vfi = new ViewFileInfo();
					vfi.setFileId(     fa.getId());
					vfi.setEntityId(   new EntityId(fe.getParentFolder().getId(), fe.getId(), EntityType.folderEntry.name()));
					vfi.setFileTime(   String.valueOf(fa.getModification().getDate().getTime()));
					vfi.setViewFileUrl(GwtServerHelper.getViewFileUrl(request, vfi));
					
	    			ToolbarItem viewHtmlTBI = new ToolbarItem("1_viewHtml");
	    			markTBITitle(              viewHtmlTBI, "toolbar.view.html" );
	    			markTBIUrlAsTargetedAnchor(viewHtmlTBI, vfi.getViewFileUrl());
	    			entryToolbar.addNestedItem(viewHtmlTBI                      );
				}
				catch (GwtTeamingException ex) {/* Ignored. */}
    		}
    	}
	}
	
	/*
	 * Constructs a ToolbarItem for running the who has access viewer
	 * on the selected entity.
	 */
	private static void constructEntryViewWhoHasAccess(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request) {
		// Is other than guest or an external user logged in?
		User user = GwtServerHelper.getCurrentUser();
		if ((!(user.isShared())) && user.getIdentityInfo().isInternal()) {
			// Yes!  Add the who has access item.
			ToolbarItem whoHasAccessTBI = new ToolbarItem("1_whoHasAccess");
			markTBITitle(whoHasAccessTBI, "toolbar.menu.who_has_access");
			markTBIEvent(whoHasAccessTBI, TeamingEvents.VIEW_WHO_HAS_ACCESS);
			entryToolbar.addNestedItem(whoHasAccessTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for zipping and downloading a file.
	 */
	private static void constructEntryZipAndDownloadEntry(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, FolderEntry fe) {
		boolean canDownload = AdminHelper.getEffectiveDownloadSetting(bs, GwtServerHelper.getCurrentUser());
		if (canDownload && (null != GwtServerHelper.getFileEntrysFileAttachment(bs, fe, true))) {
			ToolbarItem zipAndDownloadTBI = new ToolbarItem("1_zipAndDownload"              );
			markTBITitle(   zipAndDownloadTBI, "toolbar.zipAndDownloadFile"                 );
			markTBIEvent(   zipAndDownloadTBI, TeamingEvents.ZIP_AND_DOWNLOAD_SELECTED_FILES);
			markTBIEntityId(zipAndDownloadTBI, fe                                           );
			entryToolbar.addNestedItem(zipAndDownloadTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for zipping and downloading a folder.
	 */
	private static void constructEntryZipAndDownloadFolder(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		boolean canDownload = AdminHelper.getEffectiveDownloadSetting(bs, GwtServerHelper.getCurrentUser());
		if (canDownload && bs.getBinderModule().testAccess(folder, BinderOperation.readEntries) && GwtServerHelper.isFamilyFile(GwtServerHelper.getFolderEntityFamily(bs, folder))) {
			ToolbarItem zipAndDownloadTBI = new ToolbarItem("1_zipAndDownload");
			markTBITitle(    zipAndDownloadTBI, "toolbar.menu.zipAndDownloadFolder"  );
			markTBIEvent(    zipAndDownloadTBI, TeamingEvents.ZIP_AND_DOWNLOAD_FOLDER);
			markTBIRecursive(zipAndDownloadTBI, true                                 );
			markTBIEntityId( zipAndDownloadTBI, folder                               );
			entryToolbar.addNestedItem(zipAndDownloadTBI);
		}
	}
	
	/*
	 * Constructs a ToolbarItem for folders.
	 */
	@SuppressWarnings("unused")
	private static ToolbarItem constructFolderItems(boolean isFilr, String tbKey, AllModulesInjected bs, HttpServletRequest request, Binder binder, boolean isMyFilesStorage, EntityType binderType) {
		// Allocate the base ToolbarItem to return.
		ToolbarItem reply = new ToolbarItem(tbKey);

		// Define some locals to work with.
		AdaptedPortletURL url;
		AdminModule  am   = bs.getAdminModule();
		BinderModule bm   = bs.getBinderModule();
		User         user = GwtServerHelper.getCurrentUser();
		boolean isFolder               = (EntityType.folder    == binderType);
		boolean isProfiles             = (EntityType.profiles  == binderType);
		boolean isWorkspace            = (EntityType.workspace == binderType);
		boolean isWorkspaceReserved    = (isWorkspace && binder.isReserved());
		boolean isWorkspaceRoot        = (isWorkspace && binder.isRoot());
		boolean isVisibleWithoutShares = GwtShareHelper.visibleWithoutShares(bs, user, binder);
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
			if ((!isFilr) && (!isMyFilesStorage) && bm.testAccess(binder, BinderOperation.addFolder) && (!(BinderHelper.isBinderMyFilesStorage(binder)))) {
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
			// binder?  (Note we don't support adding workspaces
			// anywhere in Filr.)
			if ((!isFilr) && isWorkspace && bm.testAccess(binder, BinderOperation.addWorkspace)) {
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
			boolean isGuest       = GwtServerHelper.getCurrentUser().isShared();
			boolean isFilrGuest   = (isFilr && isGuest);
			boolean allowCopyMove = ((!isFilrGuest));
			if (allowCopyMove) {
				if (isWorkspace) {
					Integer wsDefType = binder.getDefinitionType();
					allowCopyMove = ((null == wsDefType) || 
						((Definition.USER_WORKSPACE_VIEW != wsDefType.intValue()) &&
						 (Definition.EXTERNAL_USER_WORKSPACE_VIEW != wsDefType.intValue())));
				}
				else {
					allowCopyMove = true;
				}
			}
			if (allowCopyMove) {
				// Yes!  Do they have rights to move it?
				if (bm.testAccess(binder, BinderOperation.moveBinder) && (!isMyFilesStorage) && isVisibleWithoutShares) {
					// Yes!  Add a ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
					
					// ...add the move item....
					actionTBI = new ToolbarItem(MOVE);
					markTBITitle(   actionTBI, (isFolder ? "toolbar.menu.move_folder" : "toolbar.menu.move_workspace"));
					markTBIEvent(   actionTBI, TeamingEvents.MOVE_SELECTED_ENTITIES                                   );
					markTBIEntityId(actionTBI, binder                                                                 );
					
					configTBI.addNestedItem(actionTBI);
				}

				// Yes!  Do they have rights to copy it?
				if (bm.testAccess(binder, BinderOperation.copyBinder)) {
					// Yes!  Add a ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
					
					actionTBI = new ToolbarItem(COPY);
					markTBITitle(   actionTBI, (isFolder ? "toolbar.menu.copy_folder" : "toolbar.menu.copy_workspace"));
					markTBIEvent(   actionTBI, TeamingEvents.COPY_SELECTED_ENTITIES                                   );
					markTBIEntityId(actionTBI, binder                                                                 );
					
					configTBI.addNestedItem(actionTBI);
				}
			}
			
			// Is this a folder whose entries can be read by the user?
			boolean canDownload = AdminHelper.getEffectiveDownloadSetting(bs, user);
			if (canDownload && isFolder && bm.testAccess(binder, BinderOperation.readEntries)) {
				// Yes!  If it's a file folder...
				adminMenuCreated  =
				configMenuCreated = true;
				if (GwtServerHelper.isFamilyFile(GwtServerHelper.getFolderEntityFamily(bs, binder))) {
					// ...add a ToolbarItem for for zipping and
					// ...downloading its files.
					actionTBI = new ToolbarItem(ZIP_AND_DOWNLOAD);
					markTBITitle(    actionTBI, "toolbar.menu.zipAndDownloadFolder"  );
					markTBIEvent(    actionTBI, TeamingEvents.ZIP_AND_DOWNLOAD_FOLDER);
					markTBIRecursive(actionTBI, true                                 );
					markTBIEntityId( actionTBI, binder                               );
					configTBI.addNestedItem(actionTBI);
				}
				
				// For folders...
				if (isFolder ) {
					// ...does the user have rights to download the
					// ...folder as a CSV file?
					Folder folder = bs.getFolderModule().getFolder(binder.getId());
					if (bs.getFolderModule().testAccess(folder, FolderOperation.downloadFolderAsCsv)) {
						// Yes!  Add a ToolbarItem for for downloading
						// it as a CSV file.
						actionTBI = new ToolbarItem(DOWNLOAD_AS_CSV_FILE);
						markTBITitle(   actionTBI, "toolbar.menu.downloadFolderAsCSVFile"   );
						markTBIEvent(   actionTBI, TeamingEvents.DOWNLOAD_FOLDER_AS_CSV_FILE);
						markTBIEntityId(actionTBI, binder                                   );
						configTBI.addNestedItem(actionTBI);
					}
				}
			}

			// For folders and other than the Guest user...
			if (isFolder && (!isGuest)) {
				// ...add a ToolbarItem for for marking the folder's
				// ...contents as having been read...
				actionTBI = new ToolbarItem(SEEN_FOLDER);
				markTBITitle(   actionTBI, "toolbar.menu.markFolderContentsRead"  );
				markTBIEvent(   actionTBI, TeamingEvents.MARK_FOLDER_CONTENTS_READ);
				markTBIEntityId(actionTBI, binder                                 );
				configTBI.addNestedItem(actionTBI);
				
				// ...and one for for marking the folder's contents as
				// ...having been unread.
				actionTBI = new ToolbarItem(UNSEEN_FOLDER);
				markTBITitle(   actionTBI, "toolbar.menu.markFolderContentsUnread"  );
				markTBIEvent(   actionTBI, TeamingEvents.MARK_FOLDER_CONTENTS_UNREAD);
				markTBIEntityId(actionTBI, binder                                   );
				configTBI.addNestedItem(actionTBI);
			}
		}
		
		// Does the user have rights modify this binder?
		if ((bm.testAccess(binder, BinderOperation.modifyBinder) || bm.testAccess(binder, BinderOperation.renameBinder))) {
			// Yes!  Add the ToolBarItem's for it.
			adminMenuCreated  =
			configMenuCreated = true;

			if ((!isMyFilesStorage) && bm.testAccess(binder, BinderOperation.renameBinder)) {
				// First, a rename ToolbarItem...
				actionTBI = new ToolbarItem(RENAME);
				markTBITitle(actionTBI, (isFolder ? "toolbar.menu.rename_folder" : "toolbar.menu.rename_workspace"));
				markTBIEvent(actionTBI, TeamingEvents.INVOKE_RENAME_ENTITY                                         );
				configTBI.addNestedItem(actionTBI);
			}

			if (bm.testAccess(binder, BinderOperation.modifyBinder)) {
				// ...if we're not in Filr mode...
				if (!isFilr) {
					if (!isMyFilesStorage) {
						// ...then a modify ToolbarItem...
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
					}
	
					// ...then a configure ToolbarItem.
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_CONFIGURE_DEFINITIONS);
					url.setParameter(WebKeys.URL_BINDER_ID,   binderIdS                           );
					url.setParameter(WebKeys.URL_BINDER_TYPE, binderType.name()                   );
					
					String configureTitleKey;
					if (isMyFilesStorage)
					     configureTitleKey = "toolbar.menu.configuration.myFilesStorage";
					else configureTitleKey = "toolbar.menu.configuration";
					actionTBI = new ToolbarItem(CONFIGURE_DEFINITIONS);
					markTBIPopup(actionTBI                   );
					markTBITitle(actionTBI, configureTitleKey);
					markTBIUrl(  actionTBI, url              );
					
					configTBI.addNestedItem(actionTBI);
				}
			}
		}

		// Are we in other than simple Filr mode?
		if (!isFilr) {
			// Yes!  Does the user have rights to generate a report on
			// this binder?
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

			// Does the user have rights to manage the definitions on
			// this binder?
			if ((isFolder || isWorkspace) && (!isMyFilesStorage) && bm.testAccess(binder, BinderOperation.manageConfiguration)) {
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
			
			// Does the user have rights to manage the templates on
			// this binder?
			if ((isFolder || isWorkspace) && (!isMyFilesStorage) && bm.testAccess(binder, BinderOperation.manageConfiguration)) {
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
		}
		
		// Is this a binder we can possibly delete or purge?
		if ((isFolder || isWorkspace) && (!isWorkspaceReserved)) {
			// Yes!  Is the binder one the user can't delete, even if
			// they have rights?
			if (!(BinderHelper.isBinderDeleteProtected(binder))) {
				// Yes!  Is this a binder the user can delete?
				if (canDeleteEntity(bs, binder) && isBinderTrashEnabled(binder)) {
					// Yes!  Add the ToolbarItem for it.
					adminMenuCreated  =
					configMenuCreated = true;
		
					actionTBI = new ToolbarItem(DELETE);
					markTBITitle(   actionTBI, (isFolder ? "toolbar.menu.delete_folder" : "toolbar.menu.delete_workspace"));
					markTBIEvent(   actionTBI, TeamingEvents.DELETE_SELECTED_ENTITIES                                     );
					markTBIEntityId(actionTBI, binder                                                                     );
					
					configTBI.addNestedItem(actionTBI);
				}
			}
		}

		// Is this a mirrored folder?
		if (isFolder && LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder")) {
			Folder folder = ((Folder) binder);
			if (folder.isMirrored() && (null != folder.getResourceDriverName())) {
				// Yes!  Does this user have rights to manually
				// synchronize this mirrored folder that's not a net
				// folder?
				FolderModule fm = bs.getFolderModule();
				if (fm.testAccess(folder, FolderOperation.fullSynchronize) && (!(folder.isAclExternallyControlled()))) {
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
				// synchronization schedule on this mirrored folder
				// that's not a net folder?
				if (fm.testAccess(folder, FolderOperation.scheduleSynchronization) && (!(folder.isAclExternallyControlled()))) {
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
		if ((isFolder || isWorkspace) && (!isFilr) && (!isMyFilesStorage) && bm.testAccess(binder, BinderOperation.export)) {
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
		
		// Does the user have rights to configure e-mail settings on
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

		// Are we in Filr mode?
		if (!isFilr) {
			// No!  Does the user have rights to manage access controls
			// on this binder?
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
		}
		
		// Does the user have rights to view who has access to this
		// binder?
		if (!(user.isShared())) {
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
	private static ToolbarItem constructFolderViewsItems(boolean isFilr, String tbKey, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Allocate the base ToolbarItem to return.
		ToolbarItem reply = new ToolbarItem(tbKey);
		
		// Are we running in other than simple Filr mode?
		if (!isFilr) {
			// Yes!  Are there any views defined on this folder?
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
					markTBIUrl(viewTBI, url);
					viewsTBI.addNestedItem(viewTBI);
				}
			}
		}
		
		// If we get here, reply refers to the ToolbarItem requested.
		// Return it.
		return reply;
	}

	/*
	 * Constructs the ToolbarItem's for the footer on a folder entry.
	 */
	private static void constructFooterFolderEntryItems(ToolbarItem footerToolbar, AllModulesInjected bs, HttpServletRequest request, FolderEntry fe) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.constructFooterFolderEntryItems()");
		try {
			// Is the entity in the trash?
			if (!(fe.isPreDeleted())) {
				// No!  Generate the toolbar item for the permalink to the
				// entry.
				FileAttachment	fa        = GwtServerHelper.getFileEntrysFileAttachment(bs, fe);
				Binder			feBinder  = fe.getParentBinder();
				boolean			hasEvents = MiscUtil.hasItems(fe.getEvents());
	
				String	key;
				String	webDavUrl;
				boolean	hasWebDavUrl;
				boolean includeFooterWebDavUrls = (Utils.checkIfFilr() ? INCLUDE_FOOTER_WEBDAV_URLS_FILR : INCLUDE_FOOTER_WEBDAV_URLS_VIBE); 
				if (includeFooterWebDavUrls) {
					SimpleProfiler.start("GwtMenuHelper.constructFooterFolderEntryItems( INCLUDE_FOOTER_WEBDAV_URLS )");
					try {
						webDavUrl    = ((null == fa) ? null : SsfsUtil.getInternalAttachmentUrl(request, feBinder, fe, fa));
						hasWebDavUrl = MiscUtil.hasString(webDavUrl     );
						if      (hasEvents && hasWebDavUrl) key = "toolbar.menu.folderEntryPermalink.iCal.webdav";
						else if (hasEvents)                 key = "toolbar.menu.folderEntryPermalink.iCal";
						else if (hasWebDavUrl)              key = "toolbar.menu.folderEntryPermalink.webdav";
						else                                key = "toolbar.menu.folderEntryPermalink";
					}
					finally {
						SimpleProfiler.stop("GwtMenuHelper.constructFooterFolderEntryItems( INCLUDE_FOOTER_WEBDAV_URLS )");
					}
				}
				else {
					webDavUrl    = null;
					hasWebDavUrl = false;
					if (hasEvents) key = "toolbar.menu.folderEntryPermalink.iCal";
					else           key = "toolbar.menu.folderEntryPermalink";
				}
				
				SimpleProfiler.start("GwtMenuHelper.constructFooterFolderEntryItems( PERMALINK )");
				try {
					String permaLink = PermaLinkUtil.getPermalink(request, fe);
					ToolbarItem permalinkTBI = new ToolbarItem(PERMALINK);
					markTBITitle(permalinkTBI, key          );
					markTBIUrl(  permalinkTBI, permaLink    );
					footerToolbar.addNestedItem(permalinkTBI);
				}
				finally {
					SimpleProfiler.stop("GwtMenuHelper.constructFooterFolderEntryItems( PERMALINK )");
				}
				
				// If the entry has an attachment and the user has rights
				// to download it...
				boolean canDownload = AdminHelper.getEffectiveDownloadSetting(bs, GwtServerHelper.getCurrentUser());
				if ((null != fa) && canDownload) {
					// ...and it's a file entry...
					String family = GwtServerHelper.getFolderEntityFamily(bs, fe);
					if (GwtServerHelper.isFamilyFile(family)) {
						// ...and we can get it's download permalink...
						String downloadPermalink;
						try                  {downloadPermalink = GwtDesktopApplicationsHelper.getDownloadFileUrl(request, bs, feBinder.getId(), fe.getId(), true);}	// true -> Return a permalink URL.
						catch (Exception ex) {downloadPermalink = null;}
						if (MiscUtil.hasString(downloadPermalink)) {
							//...add a toolbar item for that.
							ToolbarItem downloadFileTBI = new ToolbarItem(FILE_DOWNLOAD);
							markTBITitle(downloadFileTBI, "toolbar.menu.fileDownloadPermalink");
							markTBIUrl(  downloadFileTBI, downloadPermalink);
							footerToolbar.addNestedItem(downloadFileTBI);
						}
					}
				}
				
				// Does the entry have any events defined on it?
				if (hasEvents) {
					// Yes!  Generate an iCal URL toolbar item.
					String icalUrl = org.kablink.teaming.ical.util.UrlUtil.getICalURLHttp(request, String.valueOf(fe.getParentBinder().getId()), String.valueOf(fe.getId()));
					ToolbarItem icalTBI = new ToolbarItem(ICALENDAR);
					markTBITitle(icalTBI, "toolbar.menu.iCalendar" );
					markTBIUrl(  icalTBI, icalUrl                  );
					footerToolbar.addNestedItem(icalTBI);
				}
				
				// Can we get a WebDAV URL for this entry?
				if (hasWebDavUrl) {
					// Yes!  Generate a WebDAV URL toolbar item.
					ToolbarItem webDavTBI = new ToolbarItem(WEBDAVURL);
					markTBITitle(webDavTBI, "toolbar.menu.webdavUrl" );
					markTBIUrl(  webDavTBI, webDavUrl                );
					footerToolbar.addNestedItem(webDavTBI            );
				}
			}
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Constructs the ToolbarItem's for the footer on a folder.
	 */
	private static void constructFooterFolderItems(ToolbarItem footerToolbar, AllModulesInjected bs, HttpServletRequest request, Folder folder) {
		// Construct the permalink item...
		String key;
		boolean isFilr = Utils.checkIfFilr();
		boolean includeFooterWebDavUrls = (isFilr ? INCLUDE_FOOTER_WEBDAV_URLS_FILR : INCLUDE_FOOTER_WEBDAV_URLS_VIBE); 
		if (includeFooterWebDavUrls) {
			if (isFilr)
			     key = "toolbar.menu.folderPermalink.filr";
			else key = "toolbar.menu.folderPermalink";
		}
		else {
			if (isFilr)
			     key = "toolbar.menu.folderPermalink.filr.noWebDAV";
			else key = "toolbar.menu.folderPermalink.noWebDAV";
		}
		String permaLink = PermaLinkUtil.getPermalink(request, folder);
		ToolbarItem permalinkTBI = new ToolbarItem(PERMALINK);
		markTBITitle(permalinkTBI, key                      );
		markTBIUrl(  permalinkTBI, permaLink                );
		footerToolbar.addNestedItem(permalinkTBI            );

		// ...for file folders...
		if (includeFooterWebDavUrls && folder.isLibrary()) {
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
	 * Constructs the ToolbarItems for action menu on individual groups
	 * in the Manage Groups dialog.
	 */
	private static void constructGroupManageGroupItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request, Long groupId) {
		// Access the group in question.
		Group group = ((Group) bs.getProfileModule().getEntry(groupId));
		
		// Are we in Filr mode?
		ToolbarItem manageGroupTBI;
		if (Utils.checkIfFilr()) {
			// Yes!  Add whether adHoc folders are accessible.
			Boolean adHocFlag = AdminHelper.getAdhocFolderSettingFromUserOrGroup(bs, groupId, false);
			if (null == adHocFlag) {
				adHocFlag = AdminHelper.getAdhocFolderSettingFromZone(bs);
			}
			boolean hasAdHocFolders = adHocFlag;
			boolean perGroupAdHoc   = (null != group.isAdHocFoldersEnabled());

			if (hasAdHocFolders) {
				// ...add the disable users adHoc folders item...
				manageGroupTBI = new ToolbarItem("1_disableSelectedAdHoc");
				markTBITitle(manageGroupTBI, "toolbar.disable.user.adHoc.perGroup");
				markTBIEvent(manageGroupTBI, TeamingEvents.DISABLE_SELECTED_USERS_ADHOC_FOLDERS);
				entryToolbar.addNestedItem(manageGroupTBI);
			}
			
			else {
				// ...otherwise, if the group doesn't have adHoc
				// ...folders, add the enable users adHoc folders
				// ...item...
				manageGroupTBI = new ToolbarItem("1_enableSelectedAdHoc");
				markTBITitle(manageGroupTBI, "toolbar.enable.user.adHoc.perGroup");
				markTBIEvent(manageGroupTBI, TeamingEvents.ENABLE_SELECTED_USERS_ADHOC_FOLDERS);
				entryToolbar.addNestedItem(manageGroupTBI);
			}

			// ...if the group currently has a per user adHoc folder
			// ...setting...
			if (perGroupAdHoc) {
				// ...and add the clear users adHoc folders item.
				manageGroupTBI = new ToolbarItem("1_clearSelectedAdHoc");
				markTBITitle(manageGroupTBI, "toolbar.clear.user.adHoc");
				markTBIEvent(manageGroupTBI, TeamingEvents.CLEAR_SELECTED_USERS_ADHOC_FOLDERS);
				entryToolbar.addNestedItem(manageGroupTBI);
			}

			// Add a separator after the previous item.
			entryToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
	
			// Add whether files can be downloaded.
			Boolean dlFlag = AdminHelper.getDownloadSettingFromUserOrGroup(bs, groupId);
			if (null == dlFlag) {
				dlFlag = AdminHelper.getDownloadSettingFromZone(bs);
			}
			boolean canDownload      = dlFlag;
			boolean perGroupDownload = (null != group.isDownloadEnabled());
			
			if (canDownload) {
				// ...add the disable download item...
				manageGroupTBI = new ToolbarItem("1_disableSelectedDownload");
				markTBITitle(manageGroupTBI, "toolbar.disable.user.download.perGroup");
				markTBIEvent(manageGroupTBI, TeamingEvents.DISABLE_SELECTED_USERS_DOWNLOAD);
				entryToolbar.addNestedItem(manageGroupTBI);
			}
			
			else {
				// ...otherwise, if the group doesn't have the download
				// ...setting, add the enable download item...
				manageGroupTBI = new ToolbarItem("1_enableSelectedDownload");
				markTBITitle(manageGroupTBI, "toolbar.enable.user.download.perGroup");
				markTBIEvent(manageGroupTBI, TeamingEvents.ENABLE_SELECTED_USERS_DOWNLOAD);
				entryToolbar.addNestedItem(manageGroupTBI);
			}
	
			// ...if the group currently has a per user download
			// ...setting...
			if (perGroupDownload) {
				// ...and add the clear users download item.
				manageGroupTBI = new ToolbarItem("1_clearSelectedDownload");
				markTBITitle(manageGroupTBI, "toolbar.clear.user.download");
				markTBIEvent(manageGroupTBI, TeamingEvents.CLEAR_SELECTED_USERS_DOWNLOAD);
				entryToolbar.addNestedItem(manageGroupTBI);
			}

			// Add a separator after the previous item.
			entryToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
		}

		// Add whether web access is enabled.
		Boolean waFlag = AdminHelper.getWebAccessSettingFromUserOrGroup(bs, groupId);
		if (null == waFlag) {
			waFlag = AdminHelper.getWebAccessSettingFromZone(bs);
		}
		boolean hasWebAccess      = waFlag;
		boolean perGroupWebAccess = (null != group.isWebAccessEnabled());
		
		if (hasWebAccess) {
			// ...add the disable web access item...
			manageGroupTBI = new ToolbarItem("1_disableSelectedWebAccess");
			markTBITitle(manageGroupTBI, "toolbar.disable.user.webAccess.perGroup");
			markTBIEvent(manageGroupTBI, TeamingEvents.DISABLE_SELECTED_USERS_WEBACCESS);
			entryToolbar.addNestedItem(manageGroupTBI);
		}
		
		else {
			// ...otherwise, if the group doesn't have a web access
			// ...setting, add the enable web access item...
			manageGroupTBI = new ToolbarItem("1_enableSelectedWebAccess");
			markTBITitle(manageGroupTBI, "toolbar.enable.user.webAccess.perGroup");
			markTBIEvent(manageGroupTBI, TeamingEvents.ENABLE_SELECTED_USERS_WEBACCESS);
			entryToolbar.addNestedItem(manageGroupTBI);
		}

		// ...if the group currently has a per group web access
		// ...setting...
		if (perGroupWebAccess) {
			// ...and add the clear group's web access item.
			manageGroupTBI = new ToolbarItem("1_clearSelectedWebAccess");
			markTBITitle(manageGroupTBI, "toolbar.clear.user.webAccess");
			markTBIEvent(manageGroupTBI, TeamingEvents.CLEAR_SELECTED_USERS_WEBACCESS);
			entryToolbar.addNestedItem(manageGroupTBI);
		}
	}

	/*
	 * Constructs a ToolbarItem for wipe operations against the
	 * selected mobile devices.
	 */
	private static void constructMobileDeviceWipeItems(ToolbarItem entryToolbar, AllModulesInjected bs, HttpServletRequest request) {
		// Create the wipe toolbar item...
		ToolbarItem wipeTBI = new ToolbarItem("1_wipe");
		markTBITitle(wipeTBI, "toolbar.wipe");

		// ...add the schedule wipe command...
		ToolbarItem mdTBI;
		mdTBI = new ToolbarItem("1_scheduleWipe");
		markTBITitle(mdTBI, "toolbar.mobileDevice.scheduleWipe.multi");
		markTBIEvent(mdTBI, TeamingEvents.SCHEDULE_WIPE_SELECTED_MOBILE_DEVICES);
		wipeTBI.addNestedItem(mdTBI);
		
		// ...add the clear scheduled wipe command...
		mdTBI = new ToolbarItem("1_clearWipe");
		markTBITitle(mdTBI, "toolbar.mobileDevice.clearScheduledWipe.multi");
		markTBIEvent(mdTBI, TeamingEvents.CLEAR_SCHEDULED_WIPE_SELECTED_MOBILE_DEVICES);
		wipeTBI.addNestedItem(mdTBI);

		// ...and the wipe toolbar to the entry toolbar.
		entryToolbar.addNestedItem(wipeTBI);
	}

	/*
	 * Constructs a ToolbarItem to run the send e-mail to team members
	 * (actually, contributors but I followed the naming that was used
	 * in the JSP code) dialog. 
	 */
	private static ToolbarItem constructSendEmailToItem(HttpServletRequest request, Binder binder) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.constructSendEmailToItem()");
		try {
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
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Constructs a ToolbarItem to run the share binder dialog.
	 */
	private static ToolbarItem constructShareBinderItem(HttpServletRequest request, Binder binder, String specificTitleKey) {
		if (!(MiscUtil.hasString(specificTitleKey))) {
			specificTitleKey = GwtUIHelper.buildRelevanceKey(binder, "relevance.shareThis");
		}
		ToolbarItem     shareTBI = new ToolbarItem(SHARE);
		markTBITitle(   shareTBI, specificTitleKey                 );
		markTBIEvent(   shareTBI, TeamingEvents.INVOKE_SHARE_BINDER);
		markTBIBinderId(shareTBI, binder.getId()                   );
		return shareTBI;
	}
	
	/*
	 * Constructs a ToolbarItem to run the share binder rights dialog.
	 */
	private static ToolbarItem constructShareWorkspaceRightsItem(HttpServletRequest request, Binder binder) {
		ToolbarItem     shareTBI = new ToolbarItem(WORKSPACE_SHARE_RIGHTS);
		markTBITitle(   shareTBI, "toolbar.setShareRights.Workspace");
		markTBIEvent(   shareTBI, TeamingEvents.INVOKE_WORKSPACE_SHARE_RIGHTS);
		markTBIBinderId(shareTBI, binder.getId()                             );
		return shareTBI;
	}
	
	/*
	 * Constructs a ToolbarItem to change the UI to mobile.
	 */
	private static ToolbarItem constructMobileUiItem(HttpServletRequest request, Binder binder) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.constructMobileUiItem()");
		try {
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
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns a List<ToolbarItem> of track binder items required
	 * for the binder.
	 */
	private static List<ToolbarItem> constructTrackBinderItem(AllModulesInjected bs, Binder binder) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.constructTrackBinderItem()");
		try {
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
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Constructs a ToolbarItem to view the trash on an binder.
	 */
	private static ToolbarItem constructTrashItem(HttpServletRequest request, Binder binder) {		
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.constructTrashItem()");
		try {
			// Construct a permalink URL to the trash for the binder...
			String binderPermalink = PermaLinkUtil.getPermalink(request, binder);
			String trashPermalink  = GwtUIHelper.getTrashPermalink(binderPermalink);
			
			// ...construct a ToolbarItem for it...
			ToolbarItem trashTBI = new ToolbarItem(TRASH);
			markTBITitle(trashTBI, "toolbar.menu.trash.view"       );
			markTBIEvent(trashTBI, TeamingEvents.GOTO_PERMALINK_URL);
			markTBIUrl(  trashTBI, trashPermalink                  );
	
			// ...and return it.
			return trashTBI;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Constructs the ToolbarItem's for What's New handling.
	 */
	private static ToolbarItem constructWhatsNewItems(boolean isFilr, String tbKey, AllModulesInjected bs, HttpServletRequest request, Binder binder, EntityType binderType) {
		// Allocate the base ToolbarItem to return.
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
		GwtLogHelper.debug(m_logger, dumpThis);
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
		if (GwtLogHelper.isDebugEnabled(m_logger)) {
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
	private static boolean folderSupportsPinning(AllModulesInjected bs, Folder folder) {
		FolderType ft = GwtServerHelper.getFolderTypeFromViewDef(bs, folder);
		return GwtViewHelper.getFolderTypeSupportsPinning(ft);
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
	 * Returns the AccessControlManager bean. 
	 */
	private static AccessControlManager getAccessControlManager() {
		return ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
	}

	/*
	 * Returns the CoreDao bean. 
	 */
	private static CoreDao getCoreDao() {
		return ((CoreDao) SpringContextUtil.getBean("coreDao"));
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
	 * Returns a GetToolbarItemsRpcResponseData containing the 
	 * ToolbarItem's for an entity given the current user's rights to
	 * that entity.
	 *
	 * @param bs
	 * @param request
	 * @param entityId
	 * 
	 * @return
	 */
	public static GetToolbarItemsRpcResponseData getEntityActionToolbarItems(AllModulesInjected bs, HttpServletRequest request, BinderInfo binderInfo, EntityId entityId) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.getEntityToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return.
			ToolbarItem						actionToolbar = new ToolbarItem(WebKeys.ENTITY_ACTION_TOOLBAR);
			List<ToolbarItem>				toolbarItems  = actionToolbar.getNestedItemsList();
			GetToolbarItemsRpcResponseData	reply         = new GetToolbarItemsRpcResponseData(toolbarItems);

			// Are we working on an entity from the administration
			// console's manage users dialog?
			if (binderInfo.isBinderProfilesRootWSManagement()) {
				// Yes!  Add the items for the action menu on
				// individual users.
				constructEntryManageUserItems(actionToolbar, bs, request, entityId.getEntityId());
			}
			
			// No, we aren't working on an entity from the
			// administration console's manage users dialog!  Are we
			// working with an entity from a mobile devices dialog?
			else if (binderInfo.isBinderMobileDevices()) {
				// Yes!  Add the items for the action menu on a mobile
				// device.
				MobileDevice md = GwtMobileDeviceHelper.getMobileDevice(bs, entityId);
				if (null != md) {
					ToolbarItem mdTBI;
					mdTBI = new ToolbarItem("1_deleteSelected");
					markTBITitle(mdTBI, "toolbar.mobileDevice.delete");
					markTBIEvent(mdTBI, TeamingEvents.DELETE_SELECTED_MOBILE_DEVICES);
					actionToolbar.addNestedItem(mdTBI);
					
					if (md.getWipeScheduled()) {
						mdTBI = new ToolbarItem("1_clearWipe");
						markTBITitle(mdTBI, "toolbar.mobileDevice.clearScheduledWipe");
						markTBIEvent(mdTBI, TeamingEvents.CLEAR_SCHEDULED_WIPE_SELECTED_MOBILE_DEVICES);
					}
					else {
						mdTBI = new ToolbarItem("1_scheduleWipe");
						markTBITitle(mdTBI, "toolbar.mobileDevice.scheduleWipe");
						markTBIEvent(mdTBI, TeamingEvents.SCHEDULE_WIPE_SELECTED_MOBILE_DEVICES);
					}
					actionToolbar.addNestedItem(mdTBI);
				}
			}
			
			else {
				// No, we aren't working on an entity from a mobile
				// devices dialog either!
				String eidType = entityId.getEntityType();
				if (eidType.equals(EntityType.folderEntry.name())) {
					// If the entry is a file we support edit-in-place on and
					// the user has rights to edit the file...
					FolderEntry fe      = bs.getFolderModule().getEntry(entityId.getBinderId(), entityId.getEntityId());
					ToolbarItem editTBI = buildEditInPlaceToolbarItem(bs, request, fe, Utils.checkIfFilr());
					if (null != editTBI) {
						// ...add the edit-in-place toolbar item for it.
						actionToolbar.addNestedItem(editTBI);
					}

					// If the user can share the entry...
					boolean isSWM    = (binderInfo.isBinderCollection() && binderInfo.getCollectionType().isSharedWithMe());
					boolean sharable =
						(((!isSWM) || GwtShareHelper.isShareForwardingEnabled(bs)) &&
						(GwtShareHelper.isEntitySharable(bs, fe) || GwtShareHelper.isEntityPublicLinkSharable(bs, fe)));
					if (sharable) {
						// ...add the necessary share items.
						constructEntryShareItem(
							actionToolbar,
							bs,
							request,
							fe,
							GwtServerHelper.isFamilyFile(
								GwtServerHelper.getFolderEntityFamily(
									bs,
									fe)));
						if (actionToolbar.hasNestedToolbarItems()) {
							actionToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
						}
					}
					
					constructEntryDownloadEntry(      actionToolbar, bs, request, fe                    );					
					constructEntryZipAndDownloadEntry(actionToolbar, bs, request, fe                    );					
					constructEntryDetailsItem(        actionToolbar, bs, request, "toolbar.details.view");
					constructEntryViewHtmlItem(       actionToolbar, bs, request, fe                    );
					constructEntryViewWhoHasAccess(   actionToolbar, bs, request                        );
					constructEntryRenameFile(         actionToolbar, bs, request, fe                    );
					constructEntrySubscribeItem(      actionToolbar, bs, request, fe, true              );
					
					if (sharable) {
						constructEntryManageSharesItem(actionToolbar, bs);
					}
				}
				
				else if (eidType.equals(EntityType.folder.name())) {
					Long    folderId = entityId.getEntityId();
					Folder  folder   = bs.getFolderModule().getFolder(folderId);
					boolean sharable = GwtShareHelper.isEntitySharable(bs, folder);
					if (sharable) {
						actionToolbar.addNestedItem(constructShareBinderItem(request, folder, "toolbar.shareSelected.folder"));
						actionToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
					}
					
					constructEntryZipAndDownloadFolder(           actionToolbar, bs, request, folder);
					constructEntryDownloadAsCSVFile(              actionToolbar, bs, request, folder);
					constructEntryMarkFolderContentsReadAndUnread(actionToolbar, bs, request, folder);
					if (!(Utils.checkIfFilr())) {
						boolean isFavorite = false;
						List<FavoriteInfo> favorites = GwtServerHelper.getFavorites(bs);
						for (FavoriteInfo favorite:  favorites) {
							Long favoriteId = Long.parseLong(favorite.getValue());
							if (favoriteId.equals(folderId)) {
								isFavorite = true;
								break;
							}
						}
						constructEntryFavoriteItem(actionToolbar, bs, request, isFavorite);
					}
					constructEntryViewWhoHasAccess(actionToolbar, bs, request              );
					constructEntryRenameFolder(    actionToolbar, bs, request, folder      );
					constructEntrySubscribeItem(   actionToolbar, bs, request, folder, true);
					
					if (sharable) {
						constructEntryManageSharesItem(actionToolbar, bs);
					}
				}
				
				else if (eidType.equals(EntityType.workspace.name())) {
					Long      wsId     = entityId.getEntityId();
					Workspace ws       = bs.getWorkspaceModule().getWorkspace(wsId);
					boolean   sharable = GwtShareHelper.isEntitySharable(bs, ws);
					if (sharable) {
						actionToolbar.addNestedItem(constructShareBinderItem(request, ws, "toolbar.shareSelected.workspace"));
						actionToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
					}
					
					// Are we running other than Filr?
					if (!(Utils.checkIfFilr())) {
						// Yes!  Is team management supported on this
						// workspace?
						TeamManagementInfo m_tmi = getTeamManagementInfo(bs, request, String.valueOf(wsId));
						if ((null != m_tmi) && m_tmi.isTeamManagementEnabled()) {
							// Yes!  Add the team management items...
							boolean addTeamSeparator = false;
							ToolbarItem teamTBI;
							// DRF (20141027):  I decided NOT to
							// include the View Team option since the
							// team members are displayed directly in
							// the view already.
/*
							if (m_tmi.isViewAllowed()) {
								teamTBI = new ToolbarItem(MenuIds.MANAGE_VIEW_TEAM);
								markTBITitle(teamTBI, "team.viewTeamMembership");
								markTBIEvent(teamTBI, TeamingEvents.VIEW_CURRENT_BINDER_TEAM_MEMBERS);
								actionToolbar.addNestedItem(teamTBI);
								addTeamSeparator = true;
							}
*/
							if (m_tmi.isManageAllowed()) {
								teamTBI = new ToolbarItem(MenuIds.MANAGE_EDIT_TEAM);
								markTBITitle(teamTBI, "team.editTeamMembership");
								teamTBI.setUrl(m_tmi.getManageUrl());
								teamTBI.addQualifier("popup", "true");
								teamTBI.addQualifier("popupHeight", String.valueOf(TeamManagementInfo.POPUP_HEIGHT));
								teamTBI.addQualifier("popupWidth",  String.valueOf(TeamManagementInfo.POPUP_WIDTH ));
								actionToolbar.addNestedItem(teamTBI);
								addTeamSeparator = true;
							}
							if (m_tmi.isTeamMeetingAllowed()) {
								teamTBI = new ToolbarItem(MenuIds.MANAGE_MEET_TEAM);
								markTBITitle(teamTBI, "team.startTeamConference");
								markTBIUrl(  teamTBI, m_tmi.getTeamMeetingUrl());
								teamTBI.addQualifier("popup", "true");
								teamTBI.addQualifier("popupHeight", String.valueOf(TeamManagementInfo.POPUP_HEIGHT));
								teamTBI.addQualifier("popupWidth",  String.valueOf(TeamManagementInfo.POPUP_WIDTH ));
								actionToolbar.addNestedItem(teamTBI);
								addTeamSeparator = true;
							}
							if (m_tmi.isSendMailAllowed()) {
								teamTBI = new ToolbarItem(MenuIds.MANAGE_MAIL_TEAM);
								markTBITitle(teamTBI, "team.emailTeamMembership");
								markTBIUrl(  teamTBI, m_tmi.getSendMailUrl());
								teamTBI.addQualifier("popup", "true");
								teamTBI.addQualifier("popupHeight", String.valueOf(TeamManagementInfo.POPUP_HEIGHT));
								teamTBI.addQualifier("popupWidth",  String.valueOf(TeamManagementInfo.POPUP_WIDTH ));
								actionToolbar.addNestedItem(teamTBI);
								addTeamSeparator = true;
							}
							if (addTeamSeparator) {
								// ...and add a separator after them.
								actionToolbar.addNestedItem(ToolbarItem.constructSeparatorTBI());
							}
						}
						
						boolean isFavorite = false;
						List<FavoriteInfo> favorites = GwtServerHelper.getFavorites(bs);
						for (FavoriteInfo favorite:  favorites) {
							Long favoriteId = Long.parseLong(favorite.getValue());
							if (favoriteId.equals(wsId)) {
								isFavorite = true;
								break;
							}
						}
						constructEntryFavoriteItem(actionToolbar, bs, request, isFavorite);
					}
					constructEntryViewWhoHasAccess(actionToolbar, bs, request    );
					constructEntryRenameWorkspace( actionToolbar, bs, request, ws);
					
					if (sharable) {
						constructEntryManageSharesItem(actionToolbar, bs);
					}
				}
			}
			
			// If we get here, reply refers to the 
			// GetToolbarItemsRpcResponseData containing the
			// ToolbarItem's for the entity.  Return it.
			GwtLogHelper.debug(m_logger, "GwtMenuHelper.getEntityToolbarItems():");
			dumpToolbarItems(toolbarItems, "...");
			return reply;
		}
		
		finally {
			gsp.stop();
		}
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
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.getFolderToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return...
			List<ToolbarItem> configureToolbarItems = new ArrayList<ToolbarItem>();
			List<ToolbarItem> toolbarItems          = new ArrayList<ToolbarItem>();
			GetFolderToolbarItemsRpcResponseData reply = new GetFolderToolbarItemsRpcResponseData(toolbarItems, configureToolbarItems);
			ToolbarItem entryToolbar = new ToolbarItem(WebKeys.ENTRY_TOOLBAR);
			toolbarItems.add(entryToolbar);
			
			// Access the binder/folder/workspace.
			boolean		supportsApplets = SsfsUtil.supportApplets(request);
			Long		folderId        = folderInfo.getBinderIdAsLong();
			Binder		binder          = bs.getBinderModule().getBinder(folderId);
			Folder		folder          = ((binder instanceof Folder)    ? ((Folder)    binder) : null);
			Workspace	ws              = ((binder instanceof Workspace) ? ((Workspace) binder) : null);
			boolean		isFolder        = (null != folder);
			String		viewType        = (isFolder ? DefinitionUtils.getViewType(folder) : null);

			// Construct the item for viewing pinned vs. non-pinned
			// items.
			constructEntryPinnedItem(entryToolbar, bs, request, folder);

			// Are we returning the toolbar items for other than a
			// trash, collections mobile devices or administrator
			// management view and are we in other than Filr mode?
			boolean isBinderCollection          = folderInfo.isBinderCollection();
			boolean isBinderLimitUserVisibility = folderInfo.isBinderLimitUserVisibility();
			boolean isBinderManageAdmins        = folderInfo.isBinderAdministratorManagement();
			boolean isBinderMobileDevices       = folderInfo.isBinderMobileDevices();
			boolean isBinderProxyIdentities     = folderInfo.isBinderProxyIdentities();
			boolean isBinderEmailTemplates      = folderInfo.isBinderEmailTemplates();
			boolean isBinderTrash               = folderInfo.isBinderTrash();
			if ((!isBinderTrash) && (!isBinderCollection) && (!isBinderLimitUserVisibility) && (!isBinderMobileDevices) && (!isBinderProxyIdentities) && (!isBinderEmailTemplates) && (!isBinderManageAdmins) && (!(Utils.checkIfFilr()))) {
				// Yes!  Add the configure accessories item to the
				// toolbar...
				constructEntryConfigureAccessories(
					bs,
					request,
					binder,
					configureToolbarItems);
				
				// ...add the configure user list item to the
				// ...toolbar...
				constructEntryConfigureUserList(
					bs,
					request,
					binder,
					configureToolbarItems);
				
				// ...and add the configure HTML element item to the
				// ...toolbar.
				constructEntryConfigureHtmlElement(
					bs,
					request,
					binder,
					configureToolbarItems);
			}

			// If the binder supports column configuration...
			ToolbarItem configureColumns = constructEntryConfigureColumsItem(binder, isBinderTrash);
			if (null != configureColumns) {
				// ...add the toolbar item to the configure list.
				configureToolbarItems.add(configureColumns);
			}

			// Are we returning the toolbar items for a trash view?
			if (isBinderTrash) {
				// Yes!  Construct the items for viewing the trash.
				constructEntryTrashItems(entryToolbar, bs, request, binder);
			}
			
			// No, we aren't returning the toolbar items for a trash
			// view!  Are we returning them for the limit user
			// visibility view?
			else if (isBinderLimitUserVisibility) {
				// Yes!  Construct the items for limiting user
				// visibility.
				constructEntryLimitUserVisibilityItems(entryToolbar, bs, request, ws);
			}
			
			// No, we aren't returning the toolbar items for limiting
			// user visibility either!  Are we returning them for the
			// administrator management view?
			else if (isBinderManageAdmins) {
				// Yes!  Construct the items for managing
				// administrators.
				constructEntryManageAdminsItems(entryToolbar, bs, request, ws);
			}
			
			// No, we aren't returning the toolbar items for the
			// administrator management view either!  Are we returning
			// them for the root profiles workspace view?
			else if (folderInfo.isBinderProfilesRootWS()) {
				// Yes!  Can the user access the root profiles binder?
				if (GwtServerHelper.canUserViewBinder(bs, folderInfo)) {
					// Yes!  Construct the items for viewing the root
					// profiles binder.
					constructEntryProfilesRootWSItems(entryToolbar, bs, request, ws, folderInfo.getWorkspaceType().isProfileRootManagement());
				}
			}
			
			// No, we aren't returning the toolbar items for root
			// profiles workspace view either!  Are we returning them
			// for the root global or team workspaces view?
			else if (folderInfo.isBinderTeamsRootWS() || folderInfo.isBinderGlobalRootWS()) {
				// Yes!  Can the user access the root team workspaces
				// binder?
				if (GwtServerHelper.canUserViewBinder(bs, folderInfo)) {
					// Yes!  Construct the items for viewing the root
					// global or team workspaces binder.
					constructEntryRootWSItems(entryToolbar, bs, request, ws, folderInfo.getWorkspaceType());
				}
			}
			
			// No, we aren't returning the toolbar items for the root
			// global or team workspaces view either!  Are we returning
			// them for a collection view?
			else if (isBinderCollection) {
				// Yes!  Can the user access this collection?
				CollectionType ct = folderInfo.getCollectionType();
				if (GwtServerHelper.canUserAccessCollection(bs, ct)) {
					// Yes!  Construct the appropriate menu items for
					// it.
					boolean isCollectionMyFiles      = (CollectionType.MY_FILES       == ct);
					boolean isCollectionNetFolders   = (CollectionType.NET_FOLDERS    == ct);
					boolean isCollectionSharedByMe   = (CollectionType.SHARED_BY_ME   == ct);
					boolean isCollectionSharedWithMe = (CollectionType.SHARED_WITH_ME == ct);
					boolean isCollectionSharedPublic = (CollectionType.SHARED_PUBLIC  == ct);
					boolean isCollectionShared       = (isCollectionSharedByMe || isCollectionSharedWithMe || isCollectionSharedPublic);
					if ((!(Utils.checkIfFilr())) && isCollectionShared) {
						constructEntryToggleSharedViewItem(entryToolbar, bs, request                                                                                  );
					}
					boolean useHomeAsMyFiles = GwtServerHelper.useHomeAsMyFiles(bs);
					if (isCollectionMyFiles) {
						Long homeFolderTargetId;
						if (useHomeAsMyFiles)
						     homeFolderTargetId = GwtServerHelper.getHomeFolderId(bs);
						else homeFolderTargetId = null;
						constructEntryAddFileFolderItem(   entryToolbar, bs, request,                                                  ws, homeFolderTargetId         );
					}
					if ((isCollectionMyFiles || isCollectionSharedByMe || isCollectionSharedWithMe)) {
					    constructEntryShareItems(          entryToolbar, bs, request, isCollectionSharedWithMe, true                                                  );
					}
					if (((isCollectionMyFiles && (!useHomeAsMyFiles) && (!isCollectionNetFolders))) || (isCollectionShared && (!isCollectionSharedPublic))) {
						constructEntryDeleteItem(          entryToolbar, bs, request,                           (isCollectionMyFiles ? ws : null), isCollectionMyFiles);
					}
					if (isCollectionMyFiles && supportsApplets && (null != GwtServerHelper.getMyFilesContainerId(bs)) && isAddEntryAllowed()) {
						constructEntryDropBoxItem(         entryToolbar                                                                                               );
					}
					constructEntryMoreItems(               entryToolbar, bs, request, binder, folderId, viewType, null, (isCollectionMyFiles ? ws : null), ct         );
				}
			}

			// No, we aren't returning the toolbar items for a
			// collection view either!  Are we returning them for a
			// mobile devices view?
			else if (isBinderMobileDevices) {
				// Yes!  Construct the appropriate menu items for
				// it.
				ToolbarItem deleteMobileDeviceTBI = new ToolbarItem("1_deleteSelected");
				markTBITitle(deleteMobileDeviceTBI, "toolbar.mobileDevice.delete.multi");
				markTBIEvent(deleteMobileDeviceTBI, TeamingEvents.DELETE_SELECTED_MOBILE_DEVICES);
				entryToolbar.addNestedItem(deleteMobileDeviceTBI);
				constructMobileDeviceWipeItems(entryToolbar, bs, request);
			}
			
			// No, we aren't returning the toolbar items for a
			// mobile devices view either!  Are we returning them for a
			// proxy identities view?
			else if (isBinderProxyIdentities) {
				// Yes!  Construct the appropriate menu items for
				// it.
				ToolbarItem piTBI = new ToolbarItem("1_add");
				markTBITitle(piTBI, "toolbar.proxyIdentities.add");
				markTBIEvent(piTBI, TeamingEvents.INVOKE_ADD_NEW_PROXY_IDENTITITY);
				entryToolbar.addNestedItem(piTBI);
				
				piTBI = new ToolbarItem("1_deleteSelected");
				markTBITitle(piTBI, "toolbar.proxyIdentities.delete.multi");
				markTBIEvent(piTBI, TeamingEvents.DELETE_SELECTED_PROXY_IDENTITIES);
				entryToolbar.addNestedItem(piTBI);
			}
			
			// No, we aren't returning the toolbar items for a
			// proxy identities view either!  Are we returning them for
			// an email templates view?
			else if (isBinderEmailTemplates) {
				// Yes!  Construct the delete menu item...
				ToolbarItem etTBI = new ToolbarItem("1_deleteSelected");
				markTBITitle(etTBI, "toolbar.emailTemplates.delete.multi");
				markTBIEvent(etTBI, TeamingEvents.DELETE_SELECTED_CUSTOMIZED_EMAIL_TEMPLATES);
				entryToolbar.addNestedItem(etTBI);
				
				// ...add the drag and drop files item...
				constructEntryDropBoxItem(entryToolbar);
				
				// ...and if reseting the Velocity engine is enabled...
				if (NotifyBuilderUtil.isVelocityEngineResetEnabled()) {
					// ...add a reset velocity engine item.
					etTBI = new ToolbarItem("1_resetVelocityEngine");
					markTBITitle(etTBI, "toolbar.emailTemplates.resetVelocityEngine");
					markTBIEvent(etTBI, TeamingEvents.RESET_VELOCITY_ENGINE);
					entryToolbar.addNestedItem(etTBI);
				}
			}
			
			else {
				// No, we aren't returning the toolbar items for an
				// email templates view either!  Is this is other than
				// a mirrored folder, or if its a mirrored folder, is
				// its resource driver configured?
				boolean isMirrored           = (isFolder && folder.isMirrored());
				boolean isMirroredConfigured = isMirrored && MiscUtil.hasString(folder.getResourceDriverName());
				if ((!isMirrored) || isMirroredConfigured) {
					// Yes!  Can the user can add entries to the
					// folder?
					boolean hasVT      = MiscUtil.hasString(viewType);
					boolean	addAllowed = isAddEntryAllowed(bs, folder);
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
					if (folderSupportsEntrySelection(folder, viewType) && isViewCalendar(viewType)) {
						// ...construct the details menu item.
						constructEntryDetailsItem(entryToolbar, bs, request, "toolbar.details");
					}
					
					// Constructs the items for sharing and deleting
					// the selected entries.
					boolean isFileFolder = GwtServerHelper.isFamilyFile(GwtServerHelper.getFolderEntityFamily(bs, folder));
					constructEntryShareItems(entryToolbar, bs, request, false,    isFileFolder);
					constructEntryDeleteItem(entryToolbar, bs, request, viewType, folder      );
		
					// Can we determine the folder's view type?
					if (hasVT) {
						// Yes!  Is it a blog or photo album?
						if (isViewBlog(viewType)|| isViewPhotoAlbum(viewType)) {
							// Yes!  Add the necessary 'sort by' items. 
							constructEntrySortByItems(entryToolbar, bs, request, viewType, folder);
						}
		
						// Can the user add entries to the folder and
						// are applets supported?
						if (addAllowed && supportsApplets) {
							// Yes!  Is it other than a mini-blog or a mirrored
							// file that can't be written to?
							if ((!(isViewMiniBlog(viewType))) && ((!(folder.isMirrored())) || isFolderWritableMirrored(folder))) {
								// Yes!  The the 'drop box' item.
								constructEntryDropBoxItem(entryToolbar);
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
							folder,
							folderId,
							viewType,
							folder,
							null,	// null  -> Not a Workspace.
							CollectionType.NOT_A_COLLECTION);
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
			
			// If we get here, reply refers to the
			// GetFolderToolbarItemsRpcResponseData containing the
			// ToolbarItem's for the folder.  Return it.
			GwtLogHelper.debug(m_logger, "GwtMenuHelper.getFolderToolbarItems():");
			dumpToolbarItems(configureToolbarItems, "...");
			dumpToolbarItems(toolbarItems,          "...");
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Returns a List<ToolbarItem> of the ToolbarItem's for a entity's
	 * footer given the entity type, the current user's rights to that
	 * entity, ...
	 *
	 * @param bs
	 * @param request
	 * @param entityId
	 * 
	 * @return
	 */
	public static List<ToolbarItem> getFooterToolbarItems(AllModulesInjected bs, HttpServletRequest request, EntityId entityId) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.getFooterToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return...
			List<ToolbarItem> reply = new ArrayList<ToolbarItem>();
			ToolbarItem footerToolbar = new ToolbarItem(WebKeys.FOOTER_TOOLBAR);
			reply.add(footerToolbar);

			Long itemId = entityId.getEntityId();
			if (entityId.getEntityType().equals(EntityType.folderEntry.name())) {
				FolderEntry fe = bs.getFolderModule().getEntry(null, itemId);
				constructFooterFolderEntryItems(footerToolbar, bs, request, fe);
			}
			
			else {
				// Generate the toolbar items based on the type of binder
				// this is.
				Binder binder = bs.getBinderModule().getBinder(itemId);
				if (binder instanceof Folder) {
					Folder folder = ((Folder) binder);
					constructFooterFolderItems(footerToolbar, bs, request, folder);
				}
				
				else if (binder instanceof Workspace) {
					Workspace ws = ((Workspace) binder);
					constructFooterWorkspaceItems(footerToolbar, bs, request, ws);
				}
			}
			

			// If we get here, reply refers to the List<ToolbarItem>
			// for the footer toolbar.  Return it.
			GwtLogHelper.debug(m_logger, "GwtMenuHelper.getFooterToolbarItems():");
			dumpToolbarItems(reply, "...");
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a GetToolbarItemsRpcResponseData containing the 
	 * ToolbarItem's for a group given the current user's rights to
	 * that group.
	 *
	 * @param bs
	 * @param request
	 * @param groupId
	 * 
	 * @return
	 */
	public static GetToolbarItemsRpcResponseData getGroupActionToolbarItems(AllModulesInjected bs, HttpServletRequest request, Long groupId) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.getGroupActionToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return.
			ToolbarItem						actionToolbar = new ToolbarItem(WebKeys.GROUP_ACTION_TOOLBAR);
			List<ToolbarItem>				toolbarItems  = actionToolbar.getNestedItemsList();
			GetToolbarItemsRpcResponseData	reply         = new GetToolbarItemsRpcResponseData(toolbarItems);

			// Add the per group management items.
			constructGroupManageGroupItems(actionToolbar, bs, request, groupId);
			
			// If we get here, reply refers to the 
			// GetToolbarItemsRpcResponseData containing the
			// ToolbarItem's for the group.  Return it.
			GwtLogHelper.debug(m_logger, "GwtMenuHelper.getGroupActionToolbarItems():");
			dumpToolbarItems(toolbarItems, "...");
			return reply;
		}
		
		finally {
			gsp.stop();
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
				GwtLogHelper.debug(m_logger, "GwtMenuHelper.getHostName( UnknownHostException ):  Using localhost");
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
			Definition def = DefinitionHelper.getDefinitionByName(ObjectKeys.DEFAULT_ENTRY_GUESTBOOK_NAME);
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
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtMenuHelper.getSignGuestbookUrl( SOURCE EXCEPTION ):  ");
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
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.getTeamManagementInfo()");
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
					if ((!(Utils.checkIfFilr())) || (0 < bs.getBinderModule().getTeamMemberIds(binder).size())) {
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
			gsp.stop();
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
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.getToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> to hold the ToolbarItem's
			// that we'll return...
			List<ToolbarItem> reply = new ArrayList<ToolbarItem>();

			// ...add the ToolbarItem's specific to a binder type to
			// ...the list...
			Long binderId = Long.parseLong(binderIdS);
			Binder binder = bs.getBinderModule().getBinder(binderId);
			boolean isMyFilesStorage = BinderHelper.isBinderMyFilesStorage(binder);
			EntityType binderType = binder.getEntityType();
			switch (binderType) {
			case workspace:  buildWorkspaceMenuItems(bs, request, ((Workspace)     binder),                   reply); break;
			case folder:     buildFolderMenuItems(   bs, request, ((Folder)        binder), isMyFilesStorage, reply); break;
			case profiles:   buildProfilesMenuItems( bs, request, ((ProfileBinder) binder),                   reply); break;
			}

			// ...make sure the binder is being tracked for the recent
			// ...places menu...
			addBinderToRecentPlaces(request, binder);

			// ...and add the ToolbarItem's required by all binder
			// ...types to the list.
			buildMiscMenuItems(bs, request, binder, binderType, reply);
			
			// If we get here, reply refers to the List<ToolbarItem> of
			// the ToolbarItem's for the binder.  Return it.
			GwtLogHelper.debug(m_logger, "GwtMenuHelper.getToolbarItems():");
			dumpToolbarItems(reply, "...");
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a List<ToolbarItem> containing the ToolbarItem's for a
	 * folder entry given the current user's rights to that entry.
	 *
	 * @param bs
	 * @param request
	 * @param fe
	 * 
	 * @return
	 */
	public static List<ToolbarItem> getViewEntryToolbarItems(AllModulesInjected bs, HttpServletRequest request, FolderEntry fe) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.getViewEntryToolbarItems()");
		try {
			// Allocate a List<ToolbarItem> we can return with the
			// view entry toolbar items.
			List<ToolbarItem> reply = new ArrayList<ToolbarItem>();

			// Define some variables we'll need to build the toolbar
			// items.
			User    			user        = GwtServerHelper.getCurrentUser();
			boolean				isGuest     = user.isShared();
			boolean				isFilr      = Utils.checkIfFilr();
			boolean				isFilrGuest = (isGuest && isFilr);
			boolean				isTop		= fe.isTop();
			Folder				folder      = fe.getParentFolder();
			String				feId        = String.valueOf(fe.getId());
			String				folderId    = String.valueOf(folder.getId());
			FolderModule		fm          = bs.getFolderModule();
			AdaptedPortletURL	url;
			ToolbarItem			actionTBI;

			// Can the user share this entry?
			SharingModule sm = bs.getSharingModule();
			boolean canAddShare            = (isTop && sm.testAddShareEntity(fe));
			boolean canPublicLinkShare     = (isTop && sm.testAddShareEntityPublicLinks(fe));
			boolean isVisibleWithoutShares = GwtShareHelper.visibleWithoutShares(bs, user, fe);
			if ((!isGuest) && sm.isSharingEnabled() && sm.isSharingPublicLinksEnabled() && (canAddShare || canPublicLinkShare)) {
				// Yes!  Is it a file entry?
				if (GwtServerHelper.isFamilyFile(GwtServerHelper.getFolderEntityFamily(bs, fe))) {
					// Yes!  Add a share toolbar item for it.
					ToolbarItem shareItemsTBI = new ToolbarItem("1_share");
					markTBITitle(shareItemsTBI, "toolbar.share");
	
					String keyTail;
					if (isFilr)
					     keyTail = "file";
					else keyTail = "entry";

					if (canAddShare) {
						actionTBI = new ToolbarItem(SHARE);
						markTBITitle(   actionTBI, "toolbar.shareSelected." + keyTail);
						markTBIEvent(   actionTBI, TeamingEvents.SHARE_SELECTED_ENTITIES);
						markTBIEntityId(actionTBI, fe);
						shareItemsTBI.addNestedItem(actionTBI);
					}
					
					if (canPublicLinkShare) {
						if (isFilr)
						     keyTail = "filr";
						else keyTail = "vibe";

						EntityId eid = new EntityId(fe.getParentFolder().getId(), fe.getId(), EntityType.folderEntry.name());
						if (GwtShareHelper.hasPublicLinks(bs, user.getId(), eid)) {
							actionTBI = new ToolbarItem(EDIT_PUBLIC_LINK);
							markTBITitle(   actionTBI, "toolbar.editPublicLinkSelected." + keyTail);
							markTBIEvent(   actionTBI, TeamingEvents.EDIT_PUBLIC_LINK_SELECTED_ENTITIES);
							markTBIEntityId(actionTBI, fe);
							shareItemsTBI.addNestedItem(actionTBI);
						}
						else {
							actionTBI = new ToolbarItem(COPY_PUBLIC_LINK);
							markTBITitle(   actionTBI, "toolbar.copyPublicLinkSelected." + keyTail);
							markTBIEvent(   actionTBI, TeamingEvents.COPY_PUBLIC_LINK_SELECTED_ENTITIES);
							markTBIEntityId(actionTBI, fe);
							shareItemsTBI.addNestedItem(actionTBI);
						}
						
						actionTBI = new ToolbarItem(MAILTO_PUBLIC_LINK);
						markTBITitle(   actionTBI, "toolbar.mailtoPublicLink." + keyTail);
						markTBIEvent(   actionTBI, TeamingEvents.MAILTO_PUBLIC_LINK_ENTITY);
						markTBIEntityId(actionTBI, fe);
						shareItemsTBI.addNestedItem(actionTBI);
						
						actionTBI = new ToolbarItem(EMAIL_PUBLIC_LINK);
						markTBITitle(   actionTBI, "toolbar.emailPublicLinkSelected." + keyTail);
						markTBIEvent(   actionTBI, TeamingEvents.EMAIL_PUBLIC_LINK_SELECTED_ENTITIES);
						markTBIEntityId(actionTBI, fe);
						shareItemsTBI.addNestedItem(actionTBI);
					}
					
					// ...and the share toolbar to the view toolbar.
					reply.add(shareItemsTBI);
				}
				
				else {
					// No, it isn't a file entry.  Add a simple share
					// item to the view toolbar.
					actionTBI = new ToolbarItem(SHARE);
					markTBITitle(   actionTBI, "toolbar.shareSelected.entry");
					markTBIEvent(   actionTBI, TeamingEvents.SHARE_SELECTED_ENTITIES);
					markTBIEntityId(actionTBI, fe);
					reply.add(actionTBI);
				}
			}
			
			// Can the user modify this entry?
			if ((!isFilr) && fm.testAccess(fe, FolderOperation.modifyEntry)) {
				// Yes!  Add an edit details toolbar item for it.
				url = createActionUrl(request);
				url.setParameter(WebKeys.ACTION,         WebKeys.ACTION_MODIFY_FOLDER_ENTRY);
				url.setParameter(WebKeys.URL_ENTRY_TYPE, fe.getEntityTypedId()             );
				url.setParameter(WebKeys.URL_BINDER_ID,  folderId                          );
				url.setParameter(WebKeys.URL_ENTRY_ID,   feId                              );
				
				actionTBI = new ToolbarItem(MODIFY);
				markTBIPopup(actionTBI                        );
				markTBITitle(actionTBI, "toolbar.edit.details");
				markTBIUrl(  actionTBI, url                   );
				reply.add(actionTBI);
			}

			// If the entry is a file we support edit-in-place on and
			// the user has rights to edit the file...
			actionTBI = buildEditInPlaceToolbarItem(bs, request, fe, isFilr);
			if (null != actionTBI) {
				// ...add the edit-in-place toolbar item for it.
				reply.add(actionTBI);
			}
			
			// Can the user delete this entry?
			if (canDeleteEntity(bs, fe)) {
				// Yes!  Add a delete toolbar item for it.
				actionTBI = new ToolbarItem(DELETE);
				markTBITitle(   actionTBI, "toolbar.delete"                      );
				markTBIEvent(   actionTBI, TeamingEvents.DELETE_SELECTED_ENTITIES);
				markTBIEntityId(actionTBI, fe                                    );
				reply.add(actionTBI);
			}
			
			// - - - //
			// More  //
			// - - - //
			
			// Construct the More menu drop down Toolbar menu item.
			ToolbarItem dropdownTBI = new ToolbarItem(MORE);
			markTBITitle(dropdownTBI, "toolbar.more");

			// Is this other than the Filr Guest user?
			HistoryStamp	lStamp = fe.getReservation();
			boolean			locked = ((!isFilr) && (null != lStamp));	// We ignore locking in Filr. 
			boolean isLockedByLoggedInUser = (locked && lStamp.getPrincipal().getId().equals(user.getId()));
			if (!isFilrGuest) {
				// Yes!  Can the user copy this entry?
				if (fe.isTop() && fm.testAccess(fe, FolderOperation.copyEntry)) {
					// Yes!  Add a copy toolbar item for it.
					actionTBI = new ToolbarItem(COPY);
					markTBITitle(   actionTBI, "toolbar.copy"                      );
					markTBIEvent(   actionTBI, TeamingEvents.COPY_SELECTED_ENTITIES);
					markTBIEntityId(actionTBI, fe                                  );
					dropdownTBI.addNestedItem(actionTBI);
				}

				// Can the user move this entry?
				if (((!locked) || isLockedByLoggedInUser) && fe.isTop() && fm.testAccess(fe, FolderOperation.moveEntry) && isVisibleWithoutShares) {
					// Yes!  Add a move toolbar item for it.
					actionTBI = new ToolbarItem(MOVE);
					markTBITitle(   actionTBI, "toolbar.move"                      );
					markTBIEvent(   actionTBI, TeamingEvents.MOVE_SELECTED_ENTITIES);
					markTBIEntityId(actionTBI, fe                                  );
					dropdownTBI.addNestedItem(actionTBI);
				}
			}
			
			// Is this a file entry?
			boolean canDownload = AdminHelper.getEffectiveDownloadSetting(bs, user);
			if (canDownload && (null != GwtServerHelper.getFileEntrysFileAttachment(bs, fe, true))) {
				// Yes!  Allow the user to zip and download it.
				actionTBI = new ToolbarItem(ZIP_AND_DOWNLOAD                            );
				markTBITitle(   actionTBI, "toolbar.zipAndDownload"                     );
				markTBIEvent(   actionTBI, TeamingEvents.ZIP_AND_DOWNLOAD_SELECTED_FILES);
				markTBIEntityId(actionTBI, fe                                           );
				dropdownTBI.addNestedItem(actionTBI);
			}
			
			// Are we in Filr mode?
			if (!isFilr) {
				// No!  Can the user lock this entry?
				if (fm.testAccess(fe, FolderOperation.reserveEntry)) {
					// Yes!  Is the entry currently locked?
					if (!locked) {
						// No!  Add a lock toolbar item for it.
						actionTBI = new ToolbarItem(LOCK);
						markTBITitle(   actionTBI, "toolbar.lock.entry"                );
						markTBIEvent(   actionTBI, TeamingEvents.LOCK_SELECTED_ENTITIES);
						markTBIEntityId(actionTBI, fe                                  );
						dropdownTBI.addNestedItem(actionTBI);
					}
					
					else {
						// Yes, the entry is currently locked!  If the
					    // person who has locked the entry and the
						// logged in user are the same or the person
						// who is logged in is the binder administrator
						// we allow an unlock.
						boolean	isBinderAdmin = fm.testAccess(fe, FolderOperation.overrideReserveEntry);
						if (isBinderAdmin || isLockedByLoggedInUser) {
							actionTBI = new ToolbarItem(UNLOCK);
							markTBITitle(   actionTBI, "toolbar.unlock.entry"                );
							markTBIEvent(   actionTBI, TeamingEvents.UNLOCK_SELECTED_ENTITIES);
							markTBIEntityId(actionTBI, fe                                    );
							dropdownTBI.addNestedItem(actionTBI);
						}
					}
				}
			}

			// If the user is not the Guest user...
			if (!isGuest) {
				// ...add a toolbar item for marking the item read or
				// ...unread.
				SeenMap seenMap = bs.getProfileModule().getUserSeenMap(null);
				boolean entrySeen = seenMap.checkIfSeen(fe);
				if (entrySeen) {
					actionTBI = new ToolbarItem(UNSEEN);
					markTBITitle(   actionTBI, "toolbar.markUnread.entry"                 );
					markTBIEvent(   actionTBI, TeamingEvents.MARK_UNREAD_SELECTED_ENTITIES);
					markTBIEntityId(actionTBI, fe                                         );
					dropdownTBI.addNestedItem(actionTBI);
				}
				else {
					actionTBI = new ToolbarItem(SEEN);
					markTBITitle(   actionTBI, "toolbar.markRead.entry"                 );
					markTBIEvent(   actionTBI, TeamingEvents.MARK_READ_SELECTED_ENTITIES);
					markTBIEntityId(actionTBI, fe                                       );
					dropdownTBI.addNestedItem(actionTBI);
				}
			}
			
			boolean needSeparator = dropdownTBI.hasNestedToolbarItems();

			// Are we in Filr mode?
			if (!isFilr) {
				// No!  Can the user manage access controls on this
				// entry?
				if (fm.testAccess(fe, FolderOperation.readEntry) && fe.isTop()) {
					// Yes!  Add a separator if necessary...
					needSeparator = addNestedSeparatorIfNeeded(dropdownTBI, needSeparator);
					
					// ...and add an access control toolbar item for
					// ...it.
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION,            WebKeys.ACTION_ACCESS_CONTROL);
					url.setParameter(WebKeys.URL_WORKAREA_ID,   feId                         );
					url.setParameter(WebKeys.URL_WORKAREA_TYPE, fe.getWorkAreaType()         );
					
					actionTBI = new ToolbarItem(ACCESS_CONTROL);
					markTBIPopup(actionTBI                              );
					markTBITitle(actionTBI, "toolbar.menu.accessControl");
					markTBIUrl(  actionTBI, url                         );
					dropdownTBI.addNestedItem(actionTBI);
				}

				// Can the user change entry types of this entry?
				if (fm.testAccess(fe, FolderOperation.changeEntryType)) {
					// Yes!  Add a separator if necessary...
					needSeparator = addNestedSeparatorIfNeeded(dropdownTBI, needSeparator);
					
					// ...and add a change entry type toolbar item for
					// ...it.
					actionTBI = new ToolbarItem(CHANGE_ENTRY_TYPE);
					markTBITitle(   actionTBI, "toolbar.changeEntryType"                        );
					markTBIEvent(   actionTBI, TeamingEvents.CHANGE_ENTRY_TYPE_SELECTED_ENTITIES);
					markTBIEntityId(actionTBI, fe                                               );
					dropdownTBI.addNestedItem(actionTBI);
				}
			}

			// Is this a non-comment entry for other than the guest
			// user?
			if (isTop && (!isGuest)) {
				// No!  Add a separator if necessary...
				needSeparator = addNestedSeparatorIfNeeded(dropdownTBI, needSeparator);
				
				// ...and if the user has any email addresses
				// ...defined...
				if (GwtEmailHelper.userHasEmailAddress(user)) {
					// ...add a subscribe toolbar item.
					actionTBI = new ToolbarItem(SUBSCRIBE);
					markTBITitle(   actionTBI, "toolbar.menu.subscribeToEntrySelected"  );
					markTBIEvent(   actionTBI, TeamingEvents.SUBSCRIBE_SELECTED_ENTITIES);
					markTBIEntityId(actionTBI, fe                                       );
					dropdownTBI.addNestedItem(actionTBI);
				}

				// Does the user have an e-mail address?
				if (MiscUtil.hasString(user.getEmailAddress())) {
					// Yes!  Add an e-mail contributors toolbar item
					// for it.
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_SEND_ENTRY_EMAIL                      );
					url.setParameter(WebKeys.URL_BINDER_ID,   folderId                                             );
					url.setParameter(WebKeys.URL_ENTRY_ID,    feId                                                 );
					url.setParameter(WebKeys.USER_IDS_TO_ADD, InvokeSendEmailToTeamEvent.CONTRIBUTOR_IDS_PLACEHOLER);
					
					actionTBI = new ToolbarItem(SEND_EMAIL                         );
					markTBIPopup(actionTBI                                         );
					markTBITitle(actionTBI, "toolbar.menu.sendMail"                );
					markTBIEvent(actionTBI, TeamingEvents.INVOKE_SEND_EMAIL_TO_TEAM);
					markTBIUrl(  actionTBI, url                                    );
					dropdownTBI.addNestedItem(actionTBI);
				}

				// Can the user export this entry?
				if ((!isFilr) && bs.getBinderModule().testAccess(folder, BinderOperation.export)) {
					// Yes!  Add an export toolbar item for it.
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION,        WebKeys.ACTION_EXPORT_IMPORT);
					url.setParameter(WebKeys.OPERATION,     WebKeys.OPERATION_EXPORT    );
					url.setParameter(WebKeys.URL_BINDER_ID, folderId                    );
					url.setParameter(WebKeys.URL_ENTRY_ID,  feId                        ); 
					url.setParameter(WebKeys.URL_SHOW_MENU, "false"                     );
					
					actionTBI = new ToolbarItem(IMPORT_EXPORT);
					markTBIPopup(actionTBI                            );
					markTBITitle(actionTBI, "toolbar.menu.exportEntry");
					markTBIUrl(  actionTBI, url                       );
					dropdownTBI.addNestedItem(actionTBI);
				}
			}
			
			// Add a separator if necessary...
			needSeparator = addNestedSeparatorIfNeeded(dropdownTBI, needSeparator);
			actionTBI = new ToolbarItem(PERMALINK);
			markTBITitle(   actionTBI, "toolbar.menu.showPermalinks"     );
			markTBIEvent(   actionTBI, TeamingEvents.SHOW_VIEW_PERMALINKS);
			markTBIEntityId(actionTBI, fe                                );
			dropdownTBI.addNestedItem(actionTBI);
			
			// If we added anything to the more toolbar...
			if (!(dropdownTBI.getNestedItemsList().isEmpty())) {
				// ...and it to the view entry toolbar.
				reply.add(dropdownTBI);
			}
			
			// - - - - //
			// Reports //
			// - - - - //
			
			// Are we not in Filr and can the user perform reports on
			// this entry?
			if ((!isFilr) && fm.testAccess(fe, FolderOperation.report)) {
				// Yes!  Construct the Reports menu drop down Toolbar
				// menu items.
				dropdownTBI = new ToolbarItem(REPORTS);
				markTBITitle(dropdownTBI, "toolbar.reports");

				// Add an activity reports toolbar item.
				String servletUrl =
					(WebUrlUtil.getServletRootURL(request)         +
					WebKeys.SERVLET_DOWNLOAD_REPORT                +
					"?" + WebKeys.URL_BINDER_ID   + "=" + folderId +
					"&" + WebKeys.URL_ENTRY_ID    + "=" + feId     +
					"&" + WebKeys.URL_REPORT_TYPE + "=entry"       +
					"&forumOkBtn=OK"); 
			
				actionTBI = new ToolbarItem(ACTIVITY_REPORT);
				markTBIPopup(actionTBI                            );
				markTBITitle(actionTBI, "toolbar.reports.activity");
				markTBIUrl(  actionTBI, servletUrl                );
				dropdownTBI.addNestedItem(actionTBI);

				// If we're not in Filr mode...
				if (!isFilr) {
					// ...add a workflow history report toolbar item.
					url = createActionUrl(request);
					url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WORKFLOW_HISTORY);
					url.setParameter(WebKeys.URL_ENTITY_ID, feId);
					url.setParameter(WebKeys.URL_FOLDER_ID, folderId);
					
					actionTBI = new ToolbarItem(WORKFLOW_HISTORY_REPORT);
					markTBIPopup(actionTBI                                   );
					markTBITitle(actionTBI, "toolbar.reports.workflowHistory");
					markTBIUrl(  actionTBI, url                              );
					dropdownTBI.addNestedItem(actionTBI);
				}
				
				// Add the reports drop down toolbar item to the view
				// entry toolbar.
				reply.add(dropdownTBI);
			}

			// Is this admin viewing a file that's locked?
			boolean isAdmin = (user.isSuper() && user.isPerson());
			if (isAdmin && isFileLocked(fe)) {
				// Yes!  Give them the option to forcibly unlock the
				// file.
				actionTBI = new ToolbarItem(FORCE_FILE_UNLOCK);
				markTBITitle(   actionTBI, "toolbar.force.file.unlock"     );
				markTBIEvent(   actionTBI, TeamingEvents.FORCE_FILES_UNLOCK);
				markTBIEntityId(actionTBI, fe                              );
				reply.add(actionTBI);
			}
			
			// If we get here, reply refers to the List<ToolbarItem>
			// containing the toolbar items for the entry viewer.
			// Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Returns true if we were given a folder AND the user has rights
	 * to add entries to that folder AND if we're not running with an
	 * expired license.
	 */
	private static boolean isAddEntryAllowed(AllModulesInjected bs, Folder folder) {
		boolean reply = ((null == folder) || bs.getFolderModule().testAccess(folder, FolderOperation.addEntry));
		if (reply) {
			if (Utils.checkIfFilr() || ReleaseInfo.isLicenseRequiredEdition()) {
				reply = (!(LicenseChecker.isLicenseExpired()));
			}
		}
		return reply;
	}
	
	private static boolean isAddEntryAllowed() {
		// Always use the initial form of the method.
		return isAddEntryAllowed(null, null);
	}
	
	/*
	 * Returns true if the given binder supports trash operations
	 * and false otherwise.
	 */
	private static boolean isBinderTrashEnabled(Binder binder) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtMenuHelper.isBinderTrashEnabled()");
		try {
			boolean reply = true;
			if (binder instanceof Folder) {
				reply = true;
			}
			else if (binder instanceof Workspace) {
				reply =
					((!(BinderHelper.isBinderProfilesRootWS(  binder))) &&
					 (!(BinderHelper.isBinderNetFoldersRootWS(binder))));
			}
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Returns true if the entry has a primary file that's locked and
	 * false otherwise.
	 */
	private static boolean isFileLocked(FolderEntry fe) {
		FileAttachment fa = MiscUtil.getPrimaryFileAttachment(fe);
		return ((null != fa) && (null != fa.getFileLock()));
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
	 * Returns true if a view type is a calendar and false otherwise.
	 */
	private static boolean isViewCalendar(String viewType) {
		return MiscUtil.hasString(viewType) && viewType.equals(Definition.VIEW_STYLE_CALENDAR);
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
	 * Marks a ToolbarItem with a boolean.
	 */
	private static void markTBIBoolean(ToolbarItem tbi, String key, boolean value) {
		tbi.addQualifier(key, String.valueOf(value));
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
	 * Marks a ToolbarItem with qualifiers for edit-in-place editing.
	 */
	private static void markTBIEditInPlace(ToolbarItem tbi, String operatingSystem, String openInEditor, String editorType, String attachmentId, String attachmentUrl) {
		tbi.addQualifier("operatingSystem", operatingSystem);
		tbi.addQualifier("openInEditor",    openInEditor   );
		tbi.addQualifier("editorType",      editorType     );
		tbi.addQualifier("attachmentId",    attachmentId   );
		tbi.addQualifier("attachmentUrl",   attachmentUrl  );
	}
	
	/*
	 * Marks a ToolbarItem with an EntityId for a binder.
	 */
	private static void markTBIEntityId(ToolbarItem tbi, Binder binder) {
		EntityId binderEID = new EntityId(binder.getParentBinder().getId(), binder.getId(), binder.getEntityType().name());
		tbi.addEntityId("entityId", binderEID);
	}
	
	/*
	 * Marks a ToolbarItem with an EntityId for a folder entry.
	 */
	private static void markTBIEntityId(ToolbarItem tbi, FolderEntry fe) {
		EntityId feEID = new EntityId(fe.getParentBinder().getId(), fe.getId(), EntityId.FOLDER_ENTRY);
		tbi.addEntityId("entityId", feEID);
	}
	
	/*
	 * Marks a ToolbarItem's event.
	 */
	private static void markTBIEvent(ToolbarItem tbi, TeamingEvents event) {
		tbi.setTeamingEvent(event);
	}
	
	/*
	 * Marks a ToolbarItem with a folder target ID qualifier.
	 */
	private static void markTBIFolderTargetId(ToolbarItem tbi, Long homeFolderTargetId) {
		if (null != homeFolderTargetId) {
			tbi.addQualifier("folderTargetId", String.valueOf(homeFolderTargetId));
		}
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
	 * Marks a ToolbarItem as being the default of some sort.
	 */
	private static void markTBIMakeFavorite(ToolbarItem tbi, boolean makeFavorite) {
		tbi.addQualifier("makeFavorite", String.valueOf(makeFavorite));
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
		// Always use the initial form of the method.
		markTBIPopup(tbi, null, null);
	}
	
	/*
	 * Marks a ToolbarItem with a recursive flag.
	 */
	private static void markTBIRecursive(ToolbarItem tbi, boolean recursive) {
		tbi.addQualifier("recursive", String.valueOf(recursive));
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
		// Always use the initial form of the method.
		markTBIUrl(tbi, url.toString());
	}
	
	/*
	 * Marks a ToolbarItem's URL based on an AdaptedPortletURL using an
	 * anchor with a target clause.
	 */
	private static void markTBIUrlAsTargetedAnchor(ToolbarItem tbi, String url, String anchorTarget) {
		markTBIUrl(tbi, url);
		tbi.addQualifier("anchorTarget", (MiscUtil.hasString(anchorTarget) ? anchorTarget : "_blank"));
	}
	
	private static void markTBIUrlAsTargetedAnchor(ToolbarItem tbi, String url) {
		// Always use the initial form of the method.
		markTBIUrlAsTargetedAnchor(tbi, url.toString(), null);
	}
	
	@SuppressWarnings("unused")
	private static void markTBIUrlAsTargetedAnchor(ToolbarItem tbi, AdaptedPortletURL url, String anchorTarget) {
		// Always use the initial form of the method.
		markTBIUrlAsTargetedAnchor(tbi, url.toString(), anchorTarget);
	}
	
	@SuppressWarnings("unused")
	private static void markTBIUrlAsTargetedAnchor(ToolbarItem tbi, AdaptedPortletURL url) {
		// Always use the initial form of the method.
		markTBIUrlAsTargetedAnchor(tbi, url.toString(), null);
	}
	
	/*
	 * Marks a ToolbarItem's URL based on an AdaptedPortletURL using an
	 * anchor with a target clause.
	 */
	private static void markTBIUrlAsForcedAnchor(ToolbarItem tbi, String url) {
		markTBIUrl(tbi, url);
		tbi.addQualifier("forcedAnchor", "true");
	}
	
	private static void markTBIUrlAsForcedAnchor(ToolbarItem tbi, AdaptedPortletURL url) {
		// Always use the initial form of the method.
		markTBIUrlAsForcedAnchor(tbi, url.toString());
	}
}
