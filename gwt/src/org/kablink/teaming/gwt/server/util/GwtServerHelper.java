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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.portlet.PortletRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;

import org.kablink.teaming.GroupExistsException;
import org.kablink.teaming.IllegalCharacterInNameException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UserExistsException;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.HttpSessionContext;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.GroupSelectSpec;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CommaSeparatedValue;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.NameCompletionSettings;
import org.kablink.teaming.domain.NameCompletionSettings.NCDisplayField;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.OpenIDConfig;
import org.kablink.teaming.domain.OpenIDProvider;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.User.ExtProvState;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.extuser.ExternalUserUtil;
import org.kablink.teaming.gwt.client.GroupMembershipInfo;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtDatabasePruneConfiguration;
import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtKeyShieldConfig;
import org.kablink.teaming.gwt.client.GwtNetFolderGlobalSettings;
import org.kablink.teaming.gwt.client.GwtLocales;
import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtNameCompletionSettings;
import org.kablink.teaming.gwt.client.GwtNameCompletionSettings.GwtDisplayField;
import org.kablink.teaming.gwt.client.GwtPrincipalFileSyncAppConfig;
import org.kablink.teaming.gwt.client.GwtRole;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtTimeZones;
import org.kablink.teaming.gwt.client.GwtUser.ExtUserProvState;
import org.kablink.teaming.gwt.client.GwtOpenIDAuthenticationProvider;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtSchedule.DayFrequency;
import org.kablink.teaming.gwt.client.GwtSchedule.TimeFrequency;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.SendForgottenPwdEmailRpcResponseData;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.lpe.GwtLandingPageProperties;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.rpc.shared.BinderSharingRightsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClipboardUsersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClipboardUsersRpcResponseData.ClipboardUser;
import org.kablink.teaming.gwt.client.rpc.shared.CollectionPointData;
import org.kablink.teaming.gwt.client.rpc.shared.CreateGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.EditEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.EntityIdListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntityIdRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FolderFilter;
import org.kablink.teaming.gwt.client.rpc.shared.FolderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntityIdCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd.MembershipFilter;
import org.kablink.teaming.gwt.client.rpc.shared.GetSystemBinderPermalinkCmd.SystemBinderType;
import org.kablink.teaming.gwt.client.rpc.shared.GetJspHtmlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetTopLevelEntryIdRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ImportIcalByUrlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ImportIcalByUrlRpcResponseData.FailureReason;
import org.kablink.teaming.gwt.client.rpc.shared.IsAllUsersGroupRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageAdministratorsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageTeamsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.PasswordPolicyConfig;
import org.kablink.teaming.gwt.client.rpc.shared.PasswordPolicyInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.PrincipalInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveNameCompletionSettingsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SavePrincipalFileSyncAppConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageUsersStateRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.MainPageInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageUsersInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.MarkupStringReplacementCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ReplyToEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetPrincipalsAdminRightsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetPrincipalsAdminRightsRpcResponseData.AdminRights;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TelemetrySettingsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UpdateLogsConfig;
import org.kablink.teaming.gwt.client.rpc.shared.UserAccessConfig;
import org.kablink.teaming.gwt.client.rpc.shared.UserSharingRightsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailAddressCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailRpcResponseData.EmailAddressStatus;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmdType;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderStats;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.CombinedPerEntityShareRightsInfo;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntityId.EntityIdType;
import org.kablink.teaming.gwt.client.util.FolderSortSetting;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GroupType;
import org.kablink.teaming.gwt.client.util.GroupType.GroupClass;
import org.kablink.teaming.gwt.client.util.GwtFileLinkAction;
import org.kablink.teaming.gwt.client.util.ManageUsersState;
import org.kablink.teaming.gwt.client.util.MilestoneStats;
import org.kablink.teaming.gwt.client.util.PerEntityShareRightsInfo;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.ProjectInfo;
import org.kablink.teaming.gwt.client.util.SubscriptionData;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TagType;
import org.kablink.teaming.gwt.client.util.TaskFolderInfo;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TaskStats;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.util.TopRankedInfo.TopRankedType;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.whatsnew.EventValidation;
import org.kablink.teaming.jobs.Schedule;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.jobs.TelemetryProcessUtil;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.SendMailErrorWrapper;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionModule.DefinitionOperation;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.ical.AttendedEntries;
import org.kablink.teaming.module.keyshield.KShieldHelper;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapModule.LdapOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.license.LicenseModule.LicenseOperation;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.sharing.SharingModule.ExternalAddressStatus;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portlet.administration.ManageSearchIndexController;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.RenderRequestImpl;
import org.kablink.teaming.portletadapter.portlet.RenderResponseImpl;
import org.kablink.teaming.portletadapter.support.AdaptedPortlets;
import org.kablink.teaming.portletadapter.support.KeyNames;
import org.kablink.teaming.portletadapter.support.PortletInfo;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.task.TaskHelper.FilterType;
import org.kablink.teaming.telemetry.TelemetryService;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.FileLinkAction;
import org.kablink.teaming.util.IconSize;
import org.kablink.teaming.util.LandingPageHelper;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.PrincipalDesktopAppsConfig;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.SearchTreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.BuiltInUsersHelper;
import org.kablink.teaming.web.util.BrandingUtil;
import org.kablink.teaming.web.util.Clipboard;
import org.kablink.teaming.web.util.EmailHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.FavoritesLimitExceededException;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.PasswordPolicyHelper;
import org.kablink.teaming.web.util.EmailHelper.UrlNotificationType;
import org.kablink.teaming.web.util.LandingPageProperties;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.teaming.web.util.CloudFolderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.WorkspaceTreeHelper;
import org.kablink.teaming.web.util.WorkspaceTreeHelper.Counter;
import org.kablink.util.StringUtil;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.servlet.StringServletResponse;

/**
 * Helper methods for the GWT UI server code.
 *
 * @author drfoster@novell.com
 */
@SuppressWarnings("unchecked")
public class GwtServerHelper {
	protected static Log m_logger = LogFactory.getLog(GwtServerHelper.class);
	
	private static final boolean GROUP_DEBUG_ENABLED = SPropsUtil.getBoolean("groups.debug.enabled", false);
	private static final String  GROUP_MAGIC_TITLE   = "create.groups.";
	
	// The following are used to classify various binders based on
	// their default view definition.  See getFolderType() and
	// getWorkspaceType().
	private static final String VIEW_FOLDER_GUESTBOOK      		= "_guestbookFolder";
	private static final String VIEW_FOLDER_MIRRORED_FILE  		= "_mirroredFileFolder";	
	private static final String VIEW_FOLDER_MIRRORED_FILR_FILE  = "_mirroredFilrFileFolder";	
	private static final String VIEW_WORKSPACE_DISCUSSIONS 		= "_discussions";
	private static final String VIEW_WORKSPACE_PROJECT     		= "_projectWorkspace";
	private static final String VIEW_WORKSPACE_TEAM        		= "_team_workspace";
	private static final String VIEW_WORKSPACE_USER        		= "_userWorkspace";
	private static final String VIEW_WORKSPACE_WELCOME     		= "_welcomeWorkspace";
	private static final String VIEW_WORKSPACE_GENERIC     		= "_workspace";

	// String used to recognize an '&' formatted URL vs. a '/'
	// formatted permalink URL.
	private final static String AMPERSAND_FORMAT_MARKER = "a/do?";

	// Keys used to store user management state in the session cache.
	private static final String CACHED_MANAGE_USERS_SHOW_EXTERNAL			= "manageUsersShowExternal";
	private static final String CACHED_MANAGE_USERS_SHOW_ENABLED			= "manageUsersShowEnabled";
	private static final String CACHED_MANAGE_USERS_SHOW_DISABLED			= "manageUsersShowDisabled";
	private static final String CACHED_MANAGE_USERS_SHOW_INTERNAL			= "manageUsersShowInternal";
	private static final String CACHED_MANAGE_USERS_SHOW_SITE_ADMINS		= "manageUsersShowSiteAdmins";
	private static final String CACHED_MANAGE_USERS_SHOW_NON_SITE_ADMINS	= "manageUsersShowNonSiteAdmins";

	// landing_page_calendar.jsp and landing_page_my_calendar_events.jsp require a unique prefix
	// so there can be more than one calendar on a landing page.
	// m_landingPageCalendarPrefix is used to maintain a unique value
	private static int m_landingPageCalendarPrefix = 10;
	
	/**
	 * Inner class used to compare two AssignmentInfo's.
	 */
	public static class AssignmentInfoComparator implements Comparator<AssignmentInfo> {
		private boolean m_ascending;	//
		
		private final static int LESS		= (-1);
		private final static int EQUAL		=   0;
		private final static int GREATER	=   1;

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public AssignmentInfoComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two AssignmentInfo's by their assignee's name.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param ai1
		 * @param ai2
		 * 
		 * @return
		 */
		@Override
		public int compare(AssignmentInfo ai1, AssignmentInfo ai2) {
			int reply = EQUAL;

			// Are the assignee types equal?
			AssigneeType ait1 = ai1.getAssigneeType();
			AssigneeType ait2 = ai2.getAssigneeType();
			if ((ait1 != ait2) && (null != ait1) && (null != ait2)) {
				// No!  That's all we compare as we sort individuals
				// before groups and groups before teams.
				switch (ait1) {
				case INDIVIDUAL:  reply =                                              LESS;  break;	// 1 < 2
				case GROUP:       reply = (AssigneeType.INDIVIDUAL == ait2 ? GREATER : LESS); break;	//
				case TEAM:        reply =                                    GREATER;         break;	// 1 > 2
				}
				if (!m_ascending) {
					reply = -reply;
				}
			}
			
			else {
				// Yes, the assignee types are equal!  Compare their
				// titles.
				String assignee1 = ai1.getTitle();
				String assignee2 = ai2.getTitle();
				if (m_ascending)
				     reply = MiscUtil.safeSColatedCompare(assignee1, assignee2);
				else reply = MiscUtil.safeSColatedCompare(assignee2, assignee1);
			}

			// If we get here, reply contains the appropriate value for
			// the compare.  Return it.
			return reply;
		}
	}

	/*
	 * Inner class used assist in the construction of the
	 * List<TreeInfo> for buckets workspace trees.
	 */
	private static class BinderData {
		private BucketInfo	m_bucketInfo;	//
		private Long		m_binderId;		//
		
		/**
		 * Class constructor.
		 */
		public BinderData() {
			// Nothing to do.
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		private BucketInfo getBucketInfo() {return m_bucketInfo;}
		private Long       getBinderId()   {return m_binderId;  }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		private void setBucketInfo(BucketInfo bucketInfo) {m_bucketInfo  = bucketInfo;}
		private void setBinderId(  Long       binderId)   {m_binderId    = binderId;  }
	}
	   
	/*
	 * Inner class used to compare two Principal object.
	 */
	private static class PrincipalComparator implements Comparator<Principal> {
		private boolean	m_ascending;	//
		
		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public PrincipalComparator(boolean ascending) {
			m_ascending = ascending;
		}

		/**
		 * Compares two Principal objects by their title
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @return
		 */
		@Override
		public int compare(Principal principal1, Principal principal2) {
			String title1 = principal1.getTitle();
			String title2 = principal2.getTitle();
			int reply;
			if (m_ascending)
			     reply = MiscUtil.safeSColatedCompare(title2, title1);
			else reply = MiscUtil.safeSColatedCompare(title1, title2);

			// If we get here, reply contains the appropriate value for
			// the compare.  Return it.
			return reply;
		}
	}

	/*
	 * Inner class used compare two SavedSearchInfo objects.
	 */
	private static class SavedSearchInfoComparator implements Comparator<SavedSearchInfo> {
		/**
		 * Class constructor.
		 */
		public SavedSearchInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * SavedSearchInfo's.
		 *
		 * Returns:
		 *    -1 if ssi1 <  ssi2;
		 *     0 if ssi1 == ssi2; and
		 *     1 if ssi1 >  ssi2.
		 *     
		 * @param ssi1
		 * @param ssi2
		 * 
		 * @return
		 */
		@Override
		public int compare(SavedSearchInfo ssi1, SavedSearchInfo ssi2) {
			return MiscUtil.safeSColatedCompare(ssi1.getName(), ssi2.getName());
		}
	}	   
	   
	/**
	 * Inner class used compare two TeamInfo objects.
	 */
	private static class TeamInfoComparator implements Comparator<TeamInfo> {
		/**
		 * Class constructor.
		 */
		public TeamInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * TeamInfo's.
		 *
		 * Returns:
		 *    -1 if ti1 <  ti2;
		 *     0 if ti1 == ti2; and
		 *     1 if ti1 >  ti2.
		 *     
		 * @param ti1
		 * @param ti2
		 * 
		 * @return
		 */
		@Override
		public int compare(TeamInfo ti1, TeamInfo ti2) {
			return MiscUtil.safeSColatedCompare(ti1.getTitle(), ti2.getTitle());
		}
	}	   
	   
	private static class GroupInfoComparator implements Comparator<GroupInfo> {
		/**
		 * Class constructor.
		 */
		public GroupInfoComparator() {
			// Nothing to do.
		}

	      
		/**
		 * Implements the Comparator.compare() method on two
		 * GroupInfo's.
		 *
		 * Returns:
		 *    -1 if ti1 <  ti2;
		 *     0 if ti1 == ti2; and
		 *     1 if ti1 >  ti2.
		 *     
		 * @param ti1
		 * @param ti2
		 * 
		 * @return
		 */
		@Override
		public int compare(GroupInfo ti1, GroupInfo ti2) {
			return MiscUtil.safeSColatedCompare(ti1.getTitle(), ti2.getTitle());
		}
	}	   
	   
	/*
	 * Inner class used to compare two TreeInfo's.
	 */
	private static class TreeInfoComparator implements Comparator<TreeInfo> {
		private boolean				m_ascending;	//
		private RuleBasedCollator	m_collator;		//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TreeInfoComparator(boolean ascending) {
			m_ascending = ascending;
			
			Collator baseCollator = Collator.getInstance(RequestContextHolder.getRequestContext().getUser().getLocale());
			RuleBasedCollator defaultCollator = ((RuleBasedCollator) baseCollator);
			String rules = defaultCollator.getRules();
			try {
				m_collator = new RuleBasedCollator(rules.replaceAll("<'\u005f'", "<' '<'\u005f'"));
			}
			catch (ParseException e) {
				m_collator = defaultCollator;
			}
		}

		/**
		 * Compares two TreeInfo's by their assignee's name.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param ti1
		 * @param ti2
		 * 
		 * @return
		 */
		@Override
		public int compare(TreeInfo ti1, TreeInfo ti2) {
			BucketInfo bi1 = ti1.getBucketInfo();
			BucketInfo bi2 = ti2.getBucketInfo();
			
			String title1 = ((null == bi1) ? ti1.getBinderTitle() : bi1.getBucketPageTuple());
			String title2 = ((null == bi2) ? ti2.getBinderTitle() : bi2.getBucketPageTuple());

			int reply;
			if (m_ascending)
			     reply = MiscUtil.safeSColatedCompare(title1, title2, m_collator);
			else reply = MiscUtil.safeSColatedCompare(title2, title1, m_collator);
			return reply;
		}
	}

	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtServerHelper() {
		// Nothing to do.
	}

	/**
	 * Add a tag to the given binder.
	 * 
	 * @param bm
	 * @param binder
	 * @param tagInfo
	 */
	public static void addBinderTag(BinderModule bm, Binder binder, TagInfo tagInfo) {
		// Define the new tag.
		boolean	community = tagInfo.isCommunityTag();
		Long	binderId  = binder.getId();
		String	tagName   = tagInfo.getTagName();
		bm.setTag(binderId, tagName, community);
	}
	
	/**
	 * Adds TreeInfo's for the collections we display at the top
	 * workspace tree.
	 * 
	 * @param bs
	 * @param request
	 * @param ti
	 * 
	 * @throws GwtTeamingException
	 */
	public static void addCollections(AllModulesInjected bs, HttpServletRequest request, TreeInfo ti) throws GwtTeamingException {
		// Can we access the current user's workspace?
		User		user     = getCurrentUser();
		Long		userWSId = user.getWorkspaceId();
		Workspace	userWS;
		try {
			userWS = bs.getWorkspaceModule().getWorkspace(userWSId);
		}
		
		catch (Exception e) {
			// No!  If this is the guest user...
			if (user.isShared()) {
				// ...simply ignore the error and bail.
				return;
			}
			
			// For all other users, convert this to a
			// GwtTeamingExcepton and throw that.
			throw GwtLogHelper.getGwtClientException(m_logger, e);
		}
		
		// If we get here, we have access to the user's workspace!  Add
		// TreeInfo's for the various collections. 
		if (!(user.isShared() && Utils.checkIfFilr())) {
			addCollection(    bs, request, userWS, ti, CollectionType.MY_FILES,       false);
			addCollection(    bs, request, userWS, ti, CollectionType.SHARED_WITH_ME, false);
			addCollection(    bs, request, userWS, ti, CollectionType.SHARED_BY_ME,   true );
			if (LicenseChecker.showFilrFeatures()) {
				addCollection(bs, request, userWS, ti, CollectionType.NET_FOLDERS,    false);
			}
		}
		if (AdminHelper.getEffectivePublicCollectionSetting(bs, user)) {
			addCollection(bs, request, userWS, ti, CollectionType.SHARED_PUBLIC,  false);
		}
	}
	
	/*
	 * Adds a TreeInfo for one of the collections we display at the top
	 * workspace tree.
	 */
	private static void addCollection(AllModulesInjected bs, HttpServletRequest request, Workspace userWS, TreeInfo ti, CollectionType ct, boolean binderBorderTop) throws GwtTeamingException {
		try {
			// Allocate a TreeInfo for the collection's information...
			TreeInfo collectionTI = new TreeInfo();
			collectionTI.setBinderBorderTop(binderBorderTop);
	
			// ...add a BinderInfo to the TreeInfo...
			BinderInfo bi = buildCollectionBI(ct, userWS.getId());
			collectionTI.setBinderInfo(bi);
	
			// ...store the collection's title...
			collectionTI.setBinderTitle(bi.getBinderTitle());
	
			// ...store the various required links...
			String wsPL = PermaLinkUtil.getPermalink(request, userWS);
			collectionTI.setBinderPermalink(GwtUIHelper.appendUrlParam(wsPL, WebKeys.URL_SHOW_COLLECTION, String.valueOf(ct.ordinal())));
			collectionTI.setBinderTrashPermalink(GwtUIHelper.getTrashPermalink(wsPL));
			
			// ...and add the collection TreeInfo to the collection list.
			ti.addCollection(collectionTI);
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(m_logger, e);
		}
	}
	
	/**
	 * Add a tag to the given entry.
	 * 
	 * @param fm
	 * @param entryId
	 * @param tagInfo
	 */
	public static void addEntryTag(FolderModule fm, Long entryId, TagInfo tagInfo) {
		String	tagName   = tagInfo.getTagName();
		TagType	tagType   = tagInfo.getTagType();
		boolean	community = (tagType == TagType.COMMUNITY);
		fm.setTag(null, entryId, tagName, community);
	}
	
	/**
	 * Adds binderId to the user's list of favorites.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean addFavorite(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		Binder binder = bs.getBinderModule().getBinder(binderId);
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		Favorites f = new Favorites((String) userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES));
		String title = binder.getTitle();
		if (binder instanceof Folder) {
			title += (" (" + ((Folder)binder).getParentBinder().getTitle() + ")");
		}
		
		String viewAction;
		switch (binder.getEntityType())
		{
		case folder:     viewAction = "view_folder_listing";  break;
		case profiles:   viewAction = "view_profile_listing"; break;
		default:         viewAction = "";                     break;
		}
		try {
			f.addFavorite(
				title,
				binder.getPathName(),
				Favorites.FAVORITE_BINDER,
				binderId.toString(),
				viewAction,
				"");
		} catch(FavoritesLimitExceededException flee) {
			// There are already too many favorites, some must be
			// deleted first Construct a GwtTeamingException for this
			// error condition.
			throw GwtLogHelper.getGwtClientException(m_logger, flee);
		}
		
		bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
		return Boolean.TRUE;
	}
	
	/**
	 * Adds a quick filter to the search filter in the options map.
	 * 
	 * @param options
	 * @param quickFilter
	 * @param filterUserList
	 */
	public static void addQuickFilterToSearch(Map options, String quickFilter, boolean filterUserList) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.addQuickFilterToSearch()");
		try {
			// If we weren't given a quick filter to add...
		    quickFilter = ((null == quickFilter) ? "" : quickFilter.trim());
			if (0 == quickFilter.length()) {
				// ...there's nothing to do.  Bail.
				return;
			}
			
			// If the quick filter doesn't contain a '*', add one.
			String quickFilter_WC;
			boolean hasWC = quickFilter.contains("*");
			if (hasWC)
			     quickFilter_WC =  quickFilter;
			else quickFilter_WC = (quickFilter + "*");
			
			String quickFilter_NoWC;
			if (hasWC)
			     quickFilter_NoWC = StringUtil.replace(quickFilter, "*", "");
			else quickFilter_NoWC = quickFilter;
	
			// Create a SearchFilter from whatever filter is already in
			// affect...
			SearchFilter sf = new SearchFilter(true);
			Document sfDoc = ((Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER));
			if (null != sfDoc) {
				sf.appendFilter(sfDoc);
			}
	
			// ...add in the quick filter...
			SearchFilter sfQF = new SearchFilter(false);	// false -> These filter terms...
	    	sfQF.newCurrentFilterTermsBlock(     false);	// ...should be or'ed together. 
	    	if (filterUserList) {
	    		SearchFilter sfUserQF = new SearchFilter(false);
	    		if (quickFilter.startsWith("@")) {
	        		sfUserQF.addEmailDomainFilter(quickFilter_WC.substring(  1), true );
	        		sfUserQF.addEmailDomainFilter(quickFilter_NoWC.substring(1), false);
	    		}
	    		else {
		    		sfUserQF.addTitleFilter(      quickFilter_WC, true); sfUserQF.addTitleFilter(      quickFilter_NoWC, false);
		    		sfUserQF.addEmailFilter(      quickFilter_WC, true); sfUserQF.addEmailFilter(      quickFilter_NoWC, false);
		    		sfUserQF.addEmailDomainFilter(quickFilter_WC, true); sfUserQF.addEmailDomainFilter(quickFilter_NoWC, false);
		    		sfUserQF.addLoginNameFilter(  quickFilter_WC, true); sfUserQF.addLoginNameFilter(  quickFilter_NoWC, false);
	    		}
	    		sfQF.appendFilter(sfUserQF.getFilter());
	    	}
	    	else {
	    		sfQF.addTitleFilter(quickFilter_WC,   true );
	    		sfQF.addTitleFilter(quickFilter_NoWC, false);
	    	}
	    	sf.appendFilter(sfQF.getFilter());
	
			// ...store the new filter's XML Document in the options
	    	// ...Map...
			sfDoc = sf.getFilter();
			options.put(ObjectKeys.SEARCH_SEARCH_FILTER, sfDoc);
	
			// ...and if we logging debug messages...
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				// ...dump the search filter XML.
				GwtLogHelper.debug(m_logger, "GwtServerHelper.addQuickFilterToSearch( '" + quickFilter + "'):  Search Filter:");
				GwtLogHelper.debug(m_logger, "\n" + getXmlString(sfDoc));
			}
		}
		
		finally {
			gsp.stop();
		}
	}
	
	public static void addQuickFilterToSearch(Map options, String quickFilter) {
		// Always use the initial form of the method.
		addQuickFilterToSearch(options, quickFilter, false);	// false -> Don't filter for users.
	}
	
	/**
	 * Add a reply to the given entry.
	 * 
	 * @param bs
	 * @param entryId
	 * @param title
	 * @param desc
	 */
	public static FolderEntry addReply(AllModulesInjected bs, String entryId, String title, String desc) throws WriteEntryDataException, WriteFilesException {
		// Get the id of the binder the given entry lives in.
		FolderModule folderModule = bs.getFolderModule();
		Long entryIdL = new Long(entryId);
		FolderEntry entry = folderModule.getEntry(null, entryIdL);
		Long binderIdL = entry.getParentBinder().getId();
		
		// Get the entry's reply definition id.
		String replyDefId = null;
		Document entryDefDoc = entry.getEntryDefDoc();
		if (null != entryDefDoc) {
			// Do we have any reply styles?
			List replyStyles = DefinitionUtils.getPropertyValueList(entryDefDoc.getRootElement(), "replyStyle");
			if (MiscUtil.hasItems(replyStyles)) {
				// Yes, find the one whose name is "_comment"
				for (int i = 0; ((i < replyStyles.size()) && (null == replyDefId)); i += 1) {
					String replyStyleId = ((String) replyStyles.get(i));
					
					try {
						Definition replyDef = bs.getDefinitionModule().getDefinition(replyStyleId);
						String replyName = replyDef.getName();
						if ((null != replyName) && replyName.equalsIgnoreCase("_comment")) {
							replyDefId = replyStyleId;
						}
					}
					catch (NoDefinitionByTheIdException e) {
						continue;
					}
				}
				
				if (null == replyDefId) {
					// Use the first one.
					replyDefId = ((String) replyStyles.get(0));
				}
			}
		}

		if ((null == replyDefId) || (0 == replyDefId.length())) {
			replyDefId = entry.getEntryDefId();
		}
		
		if (null == title) {
			title = NLT.get("reply.re.title", new String[]{getFolderEntryTitle(entry)});
		}
		
		Map<String, String> inputMap = new HashMap<String, String>();
		inputMap.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
		inputMap.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc);
		inputMap.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf(Description.FORMAT_HTML));
		inputMap.put(ObjectKeys.FIELD_ENTITY_GWT_COMMENT_ENTRY, String.valueOf(Boolean.TRUE));
		MapInputData inputData = new MapInputData(inputMap);

    	return folderModule.addReply(binderIdL, entryIdL, replyDefId, inputData, new HashMap(), null);
	}

	/*
	 * Converts a String to a Long, if possible, and uses it as the ID
	 * of a task folder to construct a TaskFolderInfo to add to a
	 * List<TaskFolderInfo>.
	 */
	private static void addTFIFromStringToList(AllModulesInjected bs, HttpServletRequest request, String s, List<TaskFolderInfo> tfiList, List<ErrorInfo> errorList) {
		try {
			// Can we access the folder?
			Long folderId = Long.parseLong(s);
			Folder folder;
			try {
				folder = bs.getFolderModule().getFolder(folderId);
			}
			catch (Exception ex) {
				// No!  Log the error.
				GwtLogHelper.debug(m_logger, "GwtServerHelper.addTFIFromStringToList( Can't Access Folder ): " + s, ex);
				
				// Generate an error message for the user.
				String key;
				if (ex instanceof NoFolderByTheIdException)
				     key = "errorcode.no.task.folder.missing";
				else key = "errorcode.no.task.folder.access";
				String error = NLT.get(key, new String[]{String.valueOf(folderId)});
				
				// If we're not already tracking this exact error
				// message...
				boolean errorIsDup = false;
				for (ErrorInfo ei:  errorList) {
					if (ei.getMessage().equals(error)) {
						errorIsDup = true;
						break;
					}
				}
				if (!errorIsDup) {
					// ...add it to the error list.
					errorList.add(ErrorInfo.createError(error));
				}
				
				// If we get here, we can't get the folder.
				folder = null;
			}
			
			// Do we obtain the folder we need?
			if (null == folder) {
				// No!  Bail.
				return;
			}

			// Can we pull the statistics from the folder's custom
			// attributes?
			TaskStats ts = GwtTaskHelper.getTaskStatistics(folder);
			if (null == ts) {
				// No!  Read the tasks from the folder...
				List<TaskInfo> tiList = GwtTaskHelper.readTasks(
					request,					//
					bs,							//
					false,						// false -> Don't apply user's filter.
					false,						// false -> Not embedded in JSP.
					folder,						//
					FilterType.ALL.name(),		// Read all the tasks.
					ModeType.PHYSICAL.name(),	// Read the tasks that are physically in the folder.
					false);						// false -> We don't need all the client information for the tasks (i.e., location, assignments, ...)
	
				// ...construct a List<TaskListItem> from the tasks...
				List<TaskListItem> tliList = new ArrayList<TaskListItem>();
				if (MiscUtil.hasItems(tiList)) {
					for (TaskInfo task:  tiList) {
						tliList.add(new TaskListItem(task));
					}
				}

				// ...and use that to construct a TaskStats object.
				ts = new TaskStats(tliList);
			}
			
			// Finally, use what we've got to construct a
			// TaskFolderInfo and add it to the list.
			tfiList.add(
				new TaskFolderInfo(
					folderId,
					PermaLinkUtil.getPermalink(request, folder),
					folder.getTitle(),
					ts));
		}
		
		catch (GwtTeamingException   fte) {/* Ignored. */}
		catch (NumberFormatException nfe) {/* Ignored. */}
	}

	/**
	 * Adds a Trash folder to the TreeInfo.
	 * 
	 * Note:  At the point this gets called, it is assumed that the
	 *        caller has done whatever checks need to be done to
	 *        validate the user's access to the trash on this folder.
	 * 
	 * @param bs
	 * @param ti
	 * @param binder
	 */
	public static void addTrashFolder(AllModulesInjected bs, TreeInfo ti, Binder binder) {
		// Find the TreeInfo in question and copy it so we can make a
		// trash TreeInfo out of it.
		TreeInfo binderTI  = TreeInfo.findBinderTI(ti, String.valueOf(binder.getId()));
		TreeInfo trashTI   = binderTI.copyBaseTI();
		trashTI.clearBinderIcons();
		BinderInfo trashBI = trashTI.getBinderInfo();
		
		// Change the copy to a trash TreeInfo.
		if (BinderType.FOLDER == trashBI.getBinderType())
			 trashBI.setFolderType(   FolderType.TRASH   );
		else trashBI.setWorkspaceType(WorkspaceType.TRASH);
		trashTI.setBinderExpanded(false);
		trashTI.setBinderTitle(NLT.get("profile.abv.element.trash"));
		String trashUrl = GwtUIHelper.getTrashPermalink(trashTI.getBinderPermalink());
		trashTI.setBinderPermalink(     trashUrl);
		trashTI.setBinderTrashPermalink(trashUrl);

		// Finally, add the trash TreeInfo to the base TreeInfo's list
		// of children.
		List<TreeInfo> childBindersList = ti.getChildBindersList();
		if (null == childBindersList) {
			childBindersList = new ArrayList<TreeInfo>();
		}
		childBindersList.add(trashTI);
		ti.setChildBindersList(childBindersList);
	}

	/*
	 * Appends a parameter to to a URL.
	 */
	private static String appendUrlParam(String urlString, String pName, String pValue) {
		String param;
		boolean useAmpersand = (0 < urlString.indexOf(AMPERSAND_FORMAT_MARKER));
		if (useAmpersand)
			 param = ("&" + pName + "=" + pValue);
		else param = ("/" + pName + "/" + pValue);
		if (0 > urlString.indexOf(param)) {
			urlString += param;
		}
		return urlString;
	}

	/*
	 * Constructs a TagInfo from a Tag.
	 */
	private static TagInfo buildTIFromTag(TagType tagType, Tag tag) {
		TagInfo reply = new TagInfo();
		
		reply.setTagType(tagType);
		reply.setTagId(tag.getId());
		reply.setTagName(tag.getName());
		
		EntityIdentifier ei = tag.getEntityIdentifier();
		if (null != ei) {
			reply.setTagEntity(ei.toString());
		}
		ei = tag.getOwnerIdentifier();
		if (null != ei) {
			reply.setTagOwnerEntity(ei.toString());
		}
		
		return reply;
	}

	/*
	 * Builds a TreeInfo object for a BucketInfo.
	 */
	private static void buildChildBucketTI(HttpServletRequest request, AllModulesInjected bs, List<TreeInfo> childTIList, BucketInfo bi, int depth) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.buildChildBucketTI()");
		try {
			TreeInfo bucketTI = new TreeInfo();
			bucketTI.setBucketInfo(bi);
			childTIList.add(bucketTI);
		}
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Builds the TreeInfo objects for a list of child Binder IDs.
	 */
	private static void buildChildTIs(HttpServletRequest request, AllModulesInjected bs, boolean findBrowser, List<TreeInfo> childTIList, List<Long> childBinderList, List<Long> expandedBindersList, int depth) {
		SortedSet<Binder> binders = null;
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.buildChildTIs( READ )");
		try {
			try {
				binders = bs.getBinderModule().getBinders(childBinderList, Boolean.FALSE);
			} catch(AccessControlException ace) {
			} catch(NoBinderByTheIdException nbe) {}
		}
		finally {
			gsp.stop();
		}
		
		gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.buildChildTIs( PROCESS )");
		boolean showMFStorage = GwtViewHelper.showMyFilesStorageAsFolder();
		try {
			if (null != binders) {
				for (Long subBinderId:  childBinderList) {
					long sbi = subBinderId.longValue();
					for (Binder subBinder:  binders) {
						if (subBinder.getId().longValue() == sbi) {
							// For Filr, drop any My Files Storage folders.
							if (findBrowser || showMFStorage || (!(BinderHelper.isBinderMyFilesStorage(subBinder)))) {
								try {
									TreeInfo subWsTI = buildTreeInfoFromBinder(request, bs, findBrowser, subBinder, expandedBindersList, false, depth);
									childTIList.add(subWsTI);
								}
								catch(AccessControlException   e) {/* Ignore. */}
								catch(NoBinderByTheIdException e) {/* Ignore. */}
							}
							
							break;
						}
					}
				}				
				
				if (!(childTIList.isEmpty())) {
					Collections.sort(childTIList, new TreeInfoComparator(true));
				}
			}
		}
		finally {
			gsp.stop();
		}
	}

	/**
	 * Constructs and returns a BinderInfo for a collection.
	 * 
	 * @param ct
	 * @param userWSId
	 * 
	 * @return
	 */
	public static BinderInfo buildCollectionBI(CollectionType ct, Long userWSId) {
		// Get the string to use for the title of this collection.
		String titleKey;
		switch (ct) {
		default:
		case MY_FILES:        titleKey = "collection.myFiles";      break;
		case NET_FOLDERS:     titleKey = "collection.netFolders";   break;
		case SHARED_BY_ME:    titleKey = "collection.sharedByMe";   break;
		case SHARED_WITH_ME:  titleKey = "collection.sharedWithMe"; break;
		case SHARED_PUBLIC:   titleKey = "collection.sharedPublic"; break;
		}
		String title = NLT.get(titleKey);
		
		// Construct a BinderInfo to return...
		BinderInfo bi = new BinderInfo();
		bi.setBinderType(BinderType.COLLECTION);
		bi.setCollectionType(ct);
		bi.setBinderId(userWSId);
		bi.setBinderTitle(title);
		
		// ...and return it.
		return bi;
	}
	
	/**
	 * Builds a TreeInfo object for a given Binder.
	 *
	 * @param bs
	 * @param findBrowser
	 * @param binder
	 * @param expandedBindersList
	 * 
	 * @return
	 */
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, boolean findBrowser, Binder binder, List<Long> expandedBindersList) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinder(request, bs, findBrowser, binder, expandedBindersList, ((!findBrowser) && (null != expandedBindersList)), 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, boolean findBrowser, Binder binder) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinder(request, bs, findBrowser, binder, null, false, 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, boolean findBrowser, Binder binder, List<Long> expandedBindersList, boolean mergeUsersExpansions, int depth) {
		// Construct the base TreeInfo for the Binder.
		TreeInfo reply = new TreeInfo();
		reply.setBinderInfo(getBinderInfo(request, bs, binder));
		reply.setBinderTitle(GwtUIHelper.getTreeBinderTitle(binder));
		reply.setBinderChildren(binder.getBinderCount());
		String binderPermalink;
		boolean isFilr = Utils.checkIfFilr();
		if (isFilr && binder.isReserved() && binder.getInternalId().equals(ObjectKeys.NET_FOLDERS_ROOT_INTERNALID)) {
			// In Filr, if the user navigates to the root Net Folders
			// binder, we simply sent them to the Net Folders
			// collection instead.
			binderPermalink = PermaLinkUtil.getPermalink(request, bs.getBinderModule().getBinder(getCurrentUser().getWorkspaceId()));
			reply.setBinderPermalink(GwtUIHelper.appendUrlParam(binderPermalink, WebKeys.URL_SHOW_COLLECTION, String.valueOf(CollectionType.NET_FOLDERS.ordinal())));
			
		}
		else {
			binderPermalink = PermaLinkUtil.getPermalink(request, binder);
			if (isFilr && BinderHelper.isBinderPersonalWorkspace(binder)) {
				binderPermalink = appendUrlParam(binderPermalink, WebKeys.URL_OPERATION, WebKeys.ACTION_SHOW_PROFILE);
			}
			reply.setBinderPermalink(binderPermalink);
		}
		reply.setBinderTrashPermalink(GwtUIHelper.getTrashPermalink(binderPermalink));
		reply.setBinderIcon(binder.getIconName(), BinderIconSize.SMALL);
		reply.setBinderIcon(binder.getIconName(IconSize.MEDIUM), BinderIconSize.MEDIUM);
		reply.setBinderIcon(binder.getIconName(IconSize.LARGE ), BinderIconSize.LARGE );

		// When requested to do so...
		if (mergeUsersExpansions) {
			// ...merge any User Binder expansions with those we were
			// ...given.
			mergeBinderExpansions(bs, expandedBindersList);
		}

		// Should this Binder should be expanded?
		boolean expandBinder = ((0 == depth) || isLongInList(binder.getId(), expandedBindersList));
		reply.setBinderExpanded(expandBinder);
		if (expandBinder) {
			// Yes!  Get the sort list of the binder's children.
			List<Long> childBinderIds = new ArrayList<Long>();
			List<TreeInfo> childTIList = reply.getChildBindersList(); 
			List<BinderData> childBinderData = getChildBinderData(bs, binder);
			for (BinderData bd:  childBinderData) {			
				// Do we need to display the list in buckets?
				BucketInfo bi = bd.getBucketInfo(); 
				if (null != bi) {
					// Yes!  Build the TreeInfo objects for putting the
					// Binder's children into buckets.
					buildChildBucketTI(
						request,
						bs,
						childTIList,
						bi,
						(depth + 1));
				}
				else {
					childBinderIds.add(bd.getBinderId());
				}
			}

			// Do we have any non-bucketed binders?
			if (!(childBinderIds.isEmpty())) {
				// Yes!  Generate the TreeInfo's for them.
				buildChildTIs(
					request,
					bs,
					findBrowser,
					childTIList,
					childBinderIds,
					expandedBindersList,
					(depth + 1));
			}
			
			// Update the count of Binder children as it may have
			// changed based on moving into buckets, ...
			reply.setBinderChildren(childTIList.size());
		}

		// If we get here, reply refers to the TreeInfo object for this
		// Binder.  Return it.
		return reply;
	}

	/**
	 * Builds a TreeInfo object for a given Binder, using a List<Long>
	 * of Binder IDs for its children.
	 *
	 * @param bs
	 * @param bindersList
	 * 
	 * @return
	 */
	public static List<TreeInfo> buildTreeInfoFromBinderList(HttpServletRequest request, AllModulesInjected bs, List<Long> bindersList) {
		ArrayList<TreeInfo> reply = new ArrayList<TreeInfo>();
		for (Iterator<Long> lIT = bindersList.iterator(); lIT.hasNext(); ) {
			Binder binder = getBinderForWorkspaceTree(bs, lIT.next());
			if (null != binder) {
				reply.add(buildTreeInfoFromBinder(request, bs, false, binder, null, false, (-1)));
			}
		}
		return reply;
	}

	/**
	 * See if the user has rights to add a folder to the given binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static Boolean canAddFolder(AllModulesInjected bs, String binderId) {
		try {
			Binder binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			boolean results = bs.getBinderModule().testAccess(binder, BinderOperation.addFolder);
			return new Boolean(results);
		}
		catch (NoFolderEntryByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException        acEx) {/* Ignore. */}
		catch (Exception                     e   ) {/* Ignore. */}
		
		// If we get here the user does not have rights to modify the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the given entry can have a comment on it.
	 * 
	 * @param entry
	 * 
	 * @return
	 */
	public static boolean canEntryHaveAComment(FolderEntry entry) {
		// Get the entry's definition document.
		boolean canHaveComment = false;
		Document entryDefDoc = entry.getEntryDefDoc();
		if (null != entryDefDoc) {
			// Do we have any reply styles?
			List replyStyles = DefinitionUtils.getPropertyValueList(entryDefDoc.getRootElement(), "replyStyle");
			if (MiscUtil.hasItems(replyStyles)) {
				canHaveComment = true;
			}
		}
		return canHaveComment;
	}
	
	/**
	 * See if the user has rights to manage personal tags on the given
	 * binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static Boolean canManagePersonalBinderTags(AllModulesInjected bs, String binderId) {
		try {
			Binder binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			return new Boolean(bs.getBinderModule().testAccess(binder, BinderOperation.modifyBinder));
		}
		catch (NoFolderEntryByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException        acEx) {/* Ignore. */}
		catch (Exception                     e   ) {/* Ignore. */}
		
		// If we get here the user does not have rights to manage
		// personal tags on the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage personal tags on the given
	 * entry.
	 * 
	 * @param bs
	 * @param entryId
	 * 
	 * @return
	 */
	public static Boolean canManagePersonalEntryTags(AllModulesInjected bs, String entryId) {
		try {
			Long entryIdL = new Long(entryId);
			FolderModule fm = bs.getFolderModule();
			FolderEntry folderEntry = fm.getEntry(null, entryIdL);
	        
			// Check to see if the user can manage personal tags on
			// this entry.
			fm.checkAccess(folderEntry, FolderOperation.readEntry);

			// If we get here the action is valid.
			return Boolean.TRUE;
		}
		catch (NoFolderEntryByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException        acEx) {/* Ignore. */}
		catch (Exception                     e   ) {/* Ignore. */}
		
		// If we get here the user does not have rights to manage
		// personal tags on the entry.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage public tags on the given
	 * binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static Boolean canManagePublicBinderTags(AllModulesInjected bs, String binderId) {
		try {
			Binder binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			return new Boolean(bs.getBinderModule().testAccess(binder, BinderOperation.manageTag));
		}
		catch (NoFolderEntryByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException        acEx) {/* Ignore. */}
		catch (Exception                     e   ) {/* Ignore. */}
		
		// If we get here the user does not have rights to manage
		// public tags on the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage public tags on the given
	 * entry.
	 * 
	 * @param bs
	 * @param entryId
	 * 
	 * @return
	 */
	public static Boolean canManagePublicEntryTags(AllModulesInjected bs, String entryId) {
		try {
			Long entryIdL = new Long( entryId );
			FolderModule fm = bs.getFolderModule();
			FolderEntry folderEntry = fm.getEntry(null, entryIdL);
	        
			// Check to see if the user can manage public tags on this
			// entry.
			fm.checkAccess(folderEntry, FolderOperation.manageTag);

			// If we get here the action is valid.
			return Boolean.TRUE;
		}
		catch (NoFolderEntryByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException        acEx) {/* Ignore. */}
		catch (Exception                     e   ) {/* Ignore. */}
		
		// If we get here the user does not have rights to manage
		// public tags on the entry.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to modify the given binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static Boolean canModifyBinder(AllModulesInjected bs, String binderId) {
		try {
			Binder binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			boolean results = bs.getBinderModule().testAccess(binder, BinderOperation.modifyBinder);
			return new Boolean(results);
		}
		catch (NoFolderEntryByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException        acEx) {/* Ignore. */}
		catch (Exception                     e   ) {/* Ignore. */}
		
		// If we get here the user does not have rights to modify the
		// binder.
		return Boolean.FALSE;
	}

	/**
	 * Returns true if the user supports the give collection type in
	 * the current environment or false otherwise.
	 * 
	 * @param bs
	 * @param user
	 * @param ct
	 * 
	 * @return
	 */
	public static boolean canUserAccessCollection(AllModulesInjected bs, User user, CollectionType ct) {		
		boolean reply             = true;
		switch (ct) {
		default:
			break;
			
		case MY_FILES:
            reply = SearchUtils.userCanAccessMyFiles(bs, user);
			break;
			
		case NET_FOLDERS:
		case SHARED_BY_ME:
            boolean isGuestOrExternal = (user.isShared() || (!(user.getIdentityInfo().isInternal())));
			// We never support Net Folders or Shared By Me for the
			// guest or external users.
			if (isGuestOrExternal) {
				reply = false;
			}
			break;
		}

		// If we get here, reply is true if the user can access the
		// given collection type and false otherwise.  Return it.
		return reply;
	}
	
	public static boolean canUserAccessCollection(AllModulesInjected bs, CollectionType ct) {
		// Always use the initial form of the method.
		return canUserAccessCollection(bs, getCurrentUser(), ct);
	}
	
	public static boolean canUserAccessCollection(AllModulesInjected bs, Long userId, CollectionType ct) {
		// Always use the initial form of the method.
		User user = ((User) bs.getProfileModule().getEntry(userId));
		return canUserAccessCollection(bs, user, ct);
	}
	
	/**
	 * Returns true if the user can view the binder referenced by a
	 * BinderInfo and false otherwise.
	 * 
	 * @param bs
	 * @param user
	 * @param bi
	 * 
	 * @return
	 */
	public static boolean canUserViewBinder(AllModulesInjected bs, User user, BinderInfo bi) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.canUserViewBinder()");
		try {
			boolean	reply;
			if (bi.isBinderCollection()) {
				reply = canUserAccessCollection(bs, user, bi.getCollectionType());
			}
			else if (bi.isBinderProfilesRootWS()) {
				boolean isGuestOrExternal = (user.isShared() || (!(user.getIdentityInfo().isInternal())));
				reply = (!isGuestOrExternal);
			}
			else {
				reply = true;
			}
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	public static boolean canUserViewBinder(AllModulesInjected bs, Long userId, BinderInfo bi) {
		// Always use the initial form of the method.
		User user = ((User) bs.getProfileModule().getEntry(userId));
		return canUserViewBinder(bs, user, bi);
	}
	
	public static boolean canUserViewBinder(AllModulesInjected bs, BinderInfo bi) {
		// Always use the initial form of the method.
		return canUserViewBinder(bs, getCurrentUser(), bi);
	}
	
	/**
	 * Changes the favorite state of the given binder.  If
	 * makeFavoriate is true, the binder is made a favorite.
	 * Otherwise, it is removed from the user's favorites list.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param makeFavorite
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData changeFavoriteState(AllModulesInjected bs, HttpServletRequest request, Long binderId, boolean makeFavorite) throws GwtTeamingException {
		try {
			// Are we making this binder a favorite?
			if (makeFavorite) {
				// Yes!  Add it to the user's favorites list.
				addFavorite(bs, request, binderId);
			}
			
			else {
				// No, we aren't making this binder a favorite!  Can we
				// determine the ID of the favorite to remove?
				String favoriteId = null;
				List<FavoriteInfo> favorites = getFavorites(bs);
				for (FavoriteInfo favorite:  favorites) {
					Long favoriteBinderId = Long.parseLong(favorite.getValue());
					if (favoriteBinderId.equals(binderId)) {
						favoriteId = favorite.getId();
						break;
					}
				}
				
				if (null != favoriteId) {
					// Yes!  Remove it.
					removeFavorite(bs, request, favoriteId);
				}
			}

			// If we get here, we changed the favorite state, as
			// requested.  Return a true Boolean response data.
			return new BooleanRpcResponseData(Boolean.TRUE);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}
	
	/**
	 * Complete the self registration of an external user.
	 * 
	 * @param bs
	 * @param extUserId
	 * @param firstName
	 * @param lastName
	 * @param pwd
	 * @param invitationUrl
	 * 
	 * @return
	 */
	public static ErrorListRpcResponseData completeExternalUserSelfRegistration(AllModulesInjected bs, Long extUserId, String firstName, String lastName, String pwd, String invitationUrl,Boolean hasAcceptedTermsAndConditions) {
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		try {
			// Get the external user.
			ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
			User extUser = profileDao.loadUser(extUserId, RequestContextHolder.getRequestContext().getZoneId());

			// Does the given password violate policy?
			List<String> ppViolations = PasswordPolicyHelper.getPasswordPolicyViolations(extUser, extUser, pwd);
			if (MiscUtil.hasItems(ppViolations)) {
				// Yes!  Copy the violations to the response.
				for (String ppViolation:  ppViolations) {
					reply.addError(ppViolation);
				}
			}
			
			else {
				// No, this password is valid!
				Map updates = new HashMap();
				updates.put(ObjectKeys.FIELD_USER_PASSWORD,  pwd      );
				updates.put(ObjectKeys.FIELD_USER_FIRSTNAME, firstName);
				updates.put(ObjectKeys.FIELD_USER_LASTNAME,  lastName );
	
				ProfileModule pm = bs.getProfileModule();
				pm.modifyUserFromPortal( extUser.getId(), updates, null);
				pm.setLastPasswordChange(extUser,         new Date()   );	// Consider the user's password as having just been changed.
				if(hasAcceptedTermsAndConditions){
					pm.setTermsAndConditionsAcceptDate(extUser.getId(), new Date());
				}
				
				ExternalUserUtil.markAsCredentialed(extUser);
				
				// invitationUrl is the original URL the user was sent
				// in the first share e-mail.  We need to replace
				// 'euet=xxx' with 'euet=some new token value'.
				String confirmationUrl = null;
				if (MiscUtil.hasString(invitationUrl)) {
				    // Create a new token.
	    			String newToken = ExternalUserUtil.encodeUserTokenWithNewSeed(extUser);
					confirmationUrl = ExternalUserUtil.replaceTokenInUrl(invitationUrl, newToken);
				}
	
				// Send an e-mail informing the user that their
				// registration is complete.
				EmailHelper.sendConfirmationToExternalUser(bs, extUserId, confirmationUrl);
			}
		}
		catch (Exception ex) {
			reply.addError(NLT.get("relevance.selfRegistration.Exception"));
			return reply;
		}
		
		return reply;
	}
	
	/**
	 * Create a group from the given information.
	 * 
	 * @param bs
	 * @param name
	 * @param title
	 * @param desc
	 * @param isMembershipDynamic
	 * @param externalMembersAllowed
	 * @param membership
	 * @param membershipCriteria
	 * 
	 * @throws GwtTeamingException 
	 */
	public static Group createGroup(AllModulesInjected bs, String name, String title, String desc, boolean isMembershipDynamic, boolean externalMembersAllowed, GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException {
		HashMap<String, Object> inputMap = new HashMap<String, Object>();
		
		String ldapQuery;
		if (isMembershipDynamic && (null != membershipCriteria))
		     ldapQuery = membershipCriteria.getAsXml();
		else ldapQuery=null;

		inputMap.put(ObjectKeys.FIELD_PRINCIPAL_NAME,            name                                    );
		inputMap.put(ObjectKeys.FIELD_ENTITY_TITLE,              title                                   );
		inputMap.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION,        desc                                    );
		inputMap.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_NONE));  
		inputMap.put(ObjectKeys.FIELD_GROUP_DYNAMIC,             Boolean.valueOf(isMembershipDynamic)    );
		inputMap.put(ObjectKeys.FIELD_GROUP_LDAP_QUERY,          ldapQuery                               );

		// Add the identity information.
		IdentityInfo identityInfo = new IdentityInfo();
		identityInfo.setFromLocal(true);
		identityInfo.setInternal(!externalMembersAllowed);
		inputMap.put(ObjectKeys.FIELD_USER_PRINCIPAL_IDENTITY_INFO, identityInfo);
	
		MapInputData inputData = new MapInputData(inputMap);

		HashMap<String, Object> emptyFileMap = new HashMap<String, Object>();
		Group newGroup = null;
		try {
			// Create the new group.  Is this a debug request to create multiple groups?
			boolean createSingleGroup = true;
			ProfileModule pm = bs.getProfileModule();
			if (GROUP_DEBUG_ENABLED && MiscUtil.hasString(title) && title.startsWith(GROUP_MAGIC_TITLE)) {
				// Yes!  We recognize names of the form:
				//		create.groups.nnn
				//			Where nnn is the count of groups to create.
				//			In this case, we create nnn groups named
				//			group.1, group.2, ... group.nnn; and
				//		create.groups.abc.nnn
				//			Where nnn is the count of groups to create.
				//			In this case, we create nnn groups named
				//			abc.group.1, abc.group.2, ... abc.group.nnn.
				// Is there a name component to it besides the count?
				String magicInfo = title.substring(GROUP_MAGIC_TITLE.length());
				int    pPos      = magicInfo.indexOf('.');
				String countS;
				String namePart;
				if (0 > pPos) {
					// No!  Use the magicInfo as the count.
					namePart = "";
					countS   = magicInfo;
				}
				else {
					// Yes, there's a name component!  Parse it...
					namePart = magicInfo.substring(0, pPos);
					if (0 < pPos) {
						namePart += ".";
					}
					
					// ...and the count out from the magicInfo.
					countS = magicInfo.substring(pPos + 1);
				}
				
				// Are we being asked to create 1 or more groups?
				int count;
				try                  {count = Integer.parseInt(countS);}
				catch (Exception ex) {count = 0;}
				if (0 < count) {
					// Yes!  Create them.
					createSingleGroup = false;
					m_logger.info("GwtServerHelper.createGroup():  Creating " + count + " groups.");
					for (int i = 1; i <= count; i += 1) {
						m_logger.info("...creating group " + i + " of " + count);
						String titleAndName = (namePart + "group." + i);
						inputMap.put(ObjectKeys.FIELD_PRINCIPAL_NAME, titleAndName);
						inputMap.put(ObjectKeys.FIELD_ENTITY_TITLE, titleAndName);
						newGroup = pm.addGroup(null, inputData, emptyFileMap, null);	// The last group created will be the one we'll return.
					}
				}
			}
			
			if (createSingleGroup) {
				// No, this isn't a debug request to create multiple
				// groups!  Simply create the single group.
				newGroup = pm.addGroup(null, inputData, emptyFileMap, null);
			}
		}
		
		catch(Exception ex) {
			GwtTeamingException gtEx = GwtLogHelper.getGwtClientException();
			if (ex instanceof GroupExistsException) {
				gtEx.setExceptionType(ExceptionType.GROUP_ALREADY_EXISTS);
			}
			else if (ex instanceof UserExistsException) {
				gtEx.setExceptionType(ExceptionType.USER_ALREADY_EXISTS);
			}
			else if (ex instanceof IllegalCharacterInNameException) {
				gtEx.setAdditionalDetails(NLT.get("group.name.illegal.characters"));
			}
			else {
				String[] args = new String[] {ex.toString()};
				gtEx.setAdditionalDetails(NLT.get("group.create.unknown.error", args));
			}
			GwtLogHelper.error(m_logger, "GwtServerHelper.createGroup():  Error creating group: " + name, ex);
			throw gtEx;				
		}
		
		return newGroup;
	}

	/**
	 * Edits the given reply.
	 * 
	 * @param bs
	 * @param entryId
	 * @param title
	 * @param desc
	 */
	public static FolderEntry editReply(AllModulesInjected bs, Long entryId, String title, String desc) throws WriteEntryDataException, WriteFilesException {
		// Get the ID of the binder the given entry lives in.
		FolderModule folderModule = bs.getFolderModule();
		FolderEntry entry = folderModule.getEntry(null, entryId);
		Long binderId = entry.getParentBinder().getId();
		
		if (null == title) {
			title = NLT.get("reply.re.title", new String[]{getFolderEntryTitle(entry)});
		}
		
		Map<String, String> inputMap = new HashMap<String, String>();
		inputMap.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
		inputMap.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc);
		inputMap.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf(Description.FORMAT_HTML));
		inputMap.put(ObjectKeys.FIELD_ENTITY_GWT_COMMENT_ENTRY,  String.valueOf(Boolean.TRUE)           );
		MapInputData inputData = new MapInputData(inputMap);

    	folderModule.modifyEntry(binderId, entryId, inputData, new HashMap(), new ArrayList(), new HashMap(), new HashMap());
    	return entry;
	}

	/**
	 * Execute the given enhanced view jsp and return the resulting
	 * HTML.
	 * 
	 * @param bs
	 * @param request
	 * @param response
	 * @param servletContext
	 * @param binderId
	 * @param jspName
	 * @param configStr
	 * 
	 * @return
	 */
	public static String executeLandingPageJsp(AllModulesInjected bs, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, String binderId, String jspName, String configStr) {
		return executeLandingPageJsp(bs, request, response, servletContext, binderId, jspName, configStr, null);
	}

	public static String executeLandingPageJsp(AllModulesInjected bs, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, String binderId, String jspName, String configStr, Map<String, Object> model) {
		RequestDispatcher     reqDispatcher = request.getRequestDispatcher(jspName);
		StringServletResponse ssResponse    = new StringServletResponse(response);

		String results;
		try {
			// Gather up all the data required by the JSP.  Create the
			// objects needed to call
			// WorkspaceTreeHelper.setupWorkspaceBeans().
			String portletName = "ss_forum";
			PortletInfo portletInfo = ((PortletInfo) AdaptedPortlets.getPortletInfo(portletName));

			RenderRequestImpl renderReq = new RenderRequestImpl(request, portletInfo, AdaptedPortlets.getPortletContext());

			Map<String, Object> params = new HashMap<String, Object>();
			params.put(KeyNames.PORTLET_URL_PORTLET_NAME, new String[] {portletName});
			params.put(WebKeys.URL_BINDER_ID, binderId);
			renderReq.setRenderParameters(params);

			RenderResponseImpl renderRes = new RenderResponseImpl(renderReq, response, portletName);
			String charEncoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
			renderRes.setContentType("text/html; charset=" + charEncoding);
			renderReq.defineObjects(portletInfo.getPortletConfig(), renderRes);

			renderReq.setAttribute(PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE);

			if (model==null) {
				model = new HashMap<String, Object>();
			}
			Long binderIdL = Long.valueOf(binderId);

			WorkspaceTreeHelper.setupWorkspaceBeans(bs, binderIdL, renderReq, renderRes, model, false);

			// Put the data that setupWorkspaceBeans() put in model
			// into the request.
			for (String key:  model.keySet()) {
				Object value = model.get(key);
				request.setAttribute(key, value);
			}

			// Add the data that normally would have been added by
			// PortletAdapterServlet.java.  This attribute is used to
			// distinguish adapter request from regular request.
	    	request.setAttribute(KeyNames.CTX, servletContext);

	    	// Add the data that normally would have been added by
	    	// PortletAdapterController.java.
			request.setAttribute("javax.portlet.config",   portletInfo.getPortletConfig());
			request.setAttribute("javax.portlet.request",  renderReq                     );
			request.setAttribute("javax.portlet.response", renderRes                     );
			request.setAttribute(PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE);

			// Add the data that normally would have been added by
			// mashup_canvas_view.jsp.
			Map map1 = new HashMap();
			Map map2 = new HashMap();
			map1.put(0, "");
			map2.put(0, Long.valueOf(0));

			request.setAttribute("ss_mashupTableDepth",      Long.valueOf(0));
			request.setAttribute("ss_mashupTableNumber",     Long.valueOf(0));
			request.setAttribute("ss_mashupTableItemCount",  map1           );
			request.setAttribute("ss_mashupTableItemCount2", map2           );
			request.setAttribute("ss_mashupListDepth",       Long.valueOf(0));

			// Add the data that normally would have been added by
			// MashupTag.java.
			if (null != configStr) {
				String[] mashupItemValues = configStr.split(",");
				if (mashupItemValues.length > 0) {
					// Build a map of attributes.
					Map mashupItemAttributes = new HashMap();
					for (int i = 1; i < mashupItemValues.length; i += 1) {
						int k = mashupItemValues[i].indexOf("=");
						if (k > 0) {
							String a = mashupItemValues[i].substring(0, k);
							String v = mashupItemValues[i].substring((k + 1), mashupItemValues[i].length());
							String value1 = v;
							try {
								value1 = URLDecoder.decode(v.replaceAll("\\+", "%2B"), "UTF-8");
							}
							catch(Exception e) {/* Ignore. */}

							if ((null != a) && (!(a.equalsIgnoreCase("width"))) && (!(a.equalsIgnoreCase("height"))) && (!(a.equalsIgnoreCase("overflow")))) {
								mashupItemAttributes.put(a, value1);
							}
						}
					}

					request.setAttribute("mashup_id",         0                   );
					request.setAttribute("mashup_type",       "enhancedView"      );
					request.setAttribute("mashup_values",     mashupItemValues    );
					request.setAttribute("mashup_attributes", mashupItemAttributes);
					request.setAttribute("mashup_view",       "view"              );
				}
			}

			// landing_page_calendar.jsp and
			// landing_page_my_calendar_events.jsp require a unique
			// prefix so there can be more than one calendar on a
			// landing page.
			request.setAttribute("landingPageCalendarPrefix", String.valueOf(m_landingPageCalendarPrefix));
			m_landingPageCalendarPrefix += 1;
			
			// Execute the JSP.
			reqDispatcher.include(request, ssResponse);
			results = ssResponse.getString().trim();
		}
		catch (Exception e) {
			String[] errorArgs = new String[] {e.getLocalizedMessage()};
			results = NLT.get("errorcode.unexpectedError", errorArgs);
		}
		
		return results;
	}


	/**
	 * Execute the given JSP and return the resultant HTML.
	 * 
	 * @param bs
	 * @param request
	 * @param response
	 * @param servletContext
	 * @param jspName
	 * @param jspPath
	 * @param model
	 * 
	 * @return
	 */
	public static String executeJsp(AllModulesInjected bs, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, String jspName, String jspPath, Map<String,Object> model) {
		// If we weren't given the full path to the JSP...
		if (!(MiscUtil.hasString(jspPath))) {
			// ...construct it using the JSP's name...
			jspPath = ("/WEB-INF/jsp/" + jspName);
		}
		
		// ...and pass the request to the implementation method.
		return executeJspImpl(bs, request, response, servletContext, jspPath, model);
	}
	
	public static String executeJsp(AllModulesInjected bs, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, String jspName, Map<String,Object> model) {
		// Always use the initial form of the method.
		return executeJsp(bs, request, response, servletContext, jspName, null, model);
	}

	/*
	 * Implements executing the given JSP and returning the resultant HTML.
	 */
	private static String executeJspImpl(AllModulesInjected bs, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, String jspPath, Map<String,Object> model) {
		RequestDispatcher     reqDispatcher = request.getRequestDispatcher(jspPath);
		StringServletResponse ssResponse    = new StringServletResponse(response);

		String results;
		try {
			// Gather up all the data required by the JSP.  Create the
			// objects needed to call setupStandardBeans.
			String portletName = "ss_forum";
			PortletInfo portletInfo = ((PortletInfo) AdaptedPortlets.getPortletInfo(portletName));
			
			RenderRequestImpl renderReq = new RenderRequestImpl(request, portletInfo, AdaptedPortlets.getPortletContext());
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(KeyNames.PORTLET_URL_PORTLET_NAME, new String[] {portletName});
			renderReq.setRenderParameters(params);
			
			RenderResponseImpl renderRes = new RenderResponseImpl(renderReq, response, portletName);
			String charEncoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
			renderRes.setContentType("text/html; charset=" + charEncoding);
			renderReq.defineObjects(portletInfo.getPortletConfig(), renderRes);
			
			renderReq.setAttribute(PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE);
			Object binderIdObj = model.get(WebKeys.BINDER_ID);
			if (binderIdObj==null) {
				binderIdObj = model.get("binderId");
			}
			if (binderIdObj!=null) {
				Long binderIdL;
				if (binderIdObj instanceof Number) {
					binderIdL = ((Number) binderIdObj).longValue();
				} else {
					binderIdL = Long.parseLong(binderIdObj.toString());
				}
				BinderHelper.setupStandardBeansForCustomJsp(bs, renderReq, renderRes, model, binderIdL);
			}
			else {
				BinderHelper.setupStandardBeans(bs, renderReq, renderRes, model);
			}

			// Put the data that setupWorkspaceBeans() put in model
			// into the request.
			for (String key:  model.keySet()) {
				Object value = model.get(key);
				request.setAttribute(key, value);
			}
			
			// Add the data that normally would have been added by
			// PortletAdapterServlet.java.  This attribute is used to
			// distinguish adapter request from regular request.
	    	request.setAttribute(KeyNames.CTX, servletContext);

	    	// Add the data that normally would have been added by
	    	// PortletAdapterController.java.
			request.setAttribute("javax.portlet.config",   portletInfo.getPortletConfig());
			request.setAttribute("javax.portlet.request",  renderReq                     );
			request.setAttribute("javax.portlet.response", renderRes                     );
			request.setAttribute(PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE);
				
			// Execute the jsp
			reqDispatcher.include(request, ssResponse);
			results = ssResponse.getString().trim();
		}
		
		catch (Exception e) {
			String[] errorArgs = new String[] { e.getLocalizedMessage() };
			results = NLT.get("errorcode.unexpectedError", errorArgs);
			
			GwtLogHelper.error(m_logger, "GwtServerHelper.executeJsp( EXCEPTION ):  ", e);
		}
		
		return results;
	}

	/**
	 * Returns a TreeInfo containing the display information for the
	 * Binder hierarchy referred to by a List<Long> of Binder IDs
	 * (i.e., a bucket list.)
	 * 
	 * @param request
	 * @param bs
	 * @param findBrowser
	 * @param bucketInfo
	 * @param expandedBindersList
	 * 
	 * @return
	 */
	public static TreeInfo expandBucket(HttpServletRequest request, AllModulesInjected bs, boolean findBrowser, BucketInfo bi, List<Long> expandedBindersList) {
		TreeInfo reply = new TreeInfo();
		List<TreeInfo> childTIList = reply.getChildBindersList(); 
		List<BinderData> childBinderData = getChildBinderData(bs, bi);
		List<Long> childBinderIds = new ArrayList<Long>();
		for (BinderData bd:  childBinderData) {			
			// Do we need to display the list in buckets?
			BucketInfo childBI = bd.getBucketInfo(); 
			if (null != childBI) {
				// Yes!  Build the TreeInfo objects for putting the
				// Binder's children into buckets.
				buildChildBucketTI(
					request,
					bs,
					childTIList,
					childBI,
					(-1));
			}
			else {
				childBinderIds.add(bd.getBinderId());
			}
		}

		// Do we have any non-bucketed binders?
		if (!(childBinderIds.isEmpty())) {
			// Yes!  Generate the TreeInfo's for them.
			buildChildTIs(
				request,
				bs,
				findBrowser,
				childTIList,
				childBinderIds,
				expandedBindersList,
				(-1));
		}
		
		// Update the count of Binder children as it may have
		// changed based on moving into buckets, ...
		reply.setBinderChildren(childTIList.size());
		
		// If we get here, reply refers to the TreeInfo for the
		// expanded bucket list.  Return it.
		return reply;
	}

	/*
	 * Constructs a FavoriteInfo object from a JSONObject.
	 */
	private static FavoriteInfo fiFromJSON(AllModulesInjected bs, JSONObject jso) {
		// Construct a FavoriteInfo based on the JSONObject.
		FavoriteInfo reply = new FavoriteInfo();
		
		reply.setAction(  getSFromJSO(jso, "action"  ));
		reply.setCategory(getSFromJSO(jso, "category"));
		reply.setEletype( getSFromJSO(jso, "eletype" ));
		reply.setHover(   getSFromJSO(jso, "hover"   ));
		reply.setId(      getSFromJSO(jso, "id"      ));
		reply.setName(    getSFromJSO(jso, "name"    ));
		reply.setType(    getSFromJSO(jso, "type"    ));
		reply.setValue(   getSFromJSO(jso, "value"   ));

		// Is this favorite a binder?  (I'm not sure if it's every
		// anything else.)
		if (reply.getType().equalsIgnoreCase("binder")) {
			// Yes!  Does it have a binder ID?
			String idS = reply.getValue();
			if (MiscUtil.hasString(idS)) {
				// Yes!  Can access the binder?
				Long id = Long.parseLong(idS);
				Binder binder;
				try {
					binder = bs.getBinderModule().getBinder(id);
				}
				catch (Exception e) {
					binder = null;
				}
				if (binder == null) {
					// No!  Ignore the favorite.
					reply = null;
				}
				else {
					// Yes, we can access the binder!  Is it in the
					// trash?
					if (GwtUIHelper.isBinderPreDeleted(binder)) {
						// Yes!  Ignore the favorite.
						reply = null;
					}
					else {
						// No, it's not in the trash!  Valid the string
						// we use for the mouse hover.  If this binder
						// got moved, what's there may be wrong.
						reply.setHover(binder.getPathName());
					}
				}
			}
		}

		// If we get here, reply refers to the FavoriteInfo for the
		// JSONObject or is null.  Return it.
		return reply;
	}

	/*
	 * Fixes the folder referenced by a filter.  References to the
	 * source folder are changed to references to the target folder.
	 * 
	 * Sample XML of a filter:
	 *		<?xml version="1.0" encoding="UTF-8"?>
	 *		<searchFilter>
	 *			<filterName>Filter for Foo</filterName>
	 * 			<filterTerms filterTermsAnd="true">
	 * 				<filterTerm filterType="text">Foo</filterTerm>
	 * 				<filterTerm filterType="foldersList">
	 * 					<filterFolderId>26411</filterFolderId>		<- This is what needs to be fixed!
	 * 				</filterTerm>
	 * 			</filterTerms>
	 * 		</searchFilter>
	 */
	private static void fixupFilterFolderId(FolderFilter filter, Long targetFolderId, Long sourceFolderId) {
		// Load the filter XML into a ByteArrayInputStream...
		SAXReader reader = XmlUtil.getSAXReader(false);
		byte[] filterXmlBytes;
		try {
			filterXmlBytes = filter.getFilterData().getBytes("UTF8");
		}
		catch (UnsupportedEncodingException x) {
			m_logger.error("fixupFilterFolderId( UnsupportEncodingException ):  Can't parse filter XML.");
			filterXmlBytes = null;
		}
		if ((null == filterXmlBytes) || (0 == filterXmlBytes.length)) {
			return;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(filterXmlBytes);

		// ...and parse it into an XML document.
		Document filterXml;
		try {
			filterXml = reader.read(bais);
		}
		catch (DocumentException e) {
			m_logger.error("fixupFilterFolderId( DocumentException ):  Can't parse filter XML.");
			filterXml = null;
		}
		if (null == filterXml) {
			return;
		}

		// Are there any <filterFolderId> Node's that may need fixing?
		boolean modified = false;
		List<Node> filterFolderIdNodes = filterXml.selectNodes("//searchFilter//filterTerms//filterTerm[@filterType='foldersList']//filterFolderId");
		if (MiscUtil.hasItems(filterFolderIdNodes)) {
			// Yes!  Scan them.
			for (Node filterFolderIdNode:  filterFolderIdNodes) {
				// Does this Node reference the source folder?
				String id = filterFolderIdNode.getText();
				if (MiscUtil.hasString(id) && (sourceFolderId == Long.parseLong(id))) {
					// Yes!  Change it to the target folder.
					modified = true;
					filterFolderIdNode.setText(String.valueOf(targetFolderId));
				}
			}
		}
		
		// If we modified the filter's XML...
		if (modified) {
			// ...store it back in the filter.
			filter.setFilterData(filterXml.asXML());
		}
	}
	
	/**
	 * Return the URL needed to invoke the start/schedule meeting dialog.
	 *
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static String getAddMeetingUrl(AllModulesInjected bs, HttpServletRequest request, String binderId ) throws GwtTeamingException {
		// Store the team meeting URL.
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION,        WebKeys.ACTION_ADD_MEETING);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId                  );

		if (getWorkspaceType(GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId)) == WorkspaceType.USER) {
			// This is a User Workspace so add the owner in and don't append team members
			Principal p = GwtProfileHelper.getPrincipalByBinderId(bs, binderId);
			if (p != null) {
				Long id = p.getId();
				String [] ids = new String[1];
				ids[0] = id.toString();
				adapterUrl.setParameter(WebKeys.USER_IDS_TO_ADD, ids);
			}
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.FALSE.toString());
		}
		
		else {
			adapterUrl.setParameter(WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString());
	    }

		return adapterUrl.toString();
	}
	
	/**
	 * Return a list of administration actions the user has rights to
	 * perform.
	 * 
	 * @param request
	 * @param bs
	 */
	@SuppressWarnings("unused")
	public static ArrayList<GwtAdminCategory> getAdminActions(HttpServletRequest request, AbstractAllModulesInjected bs) {
		ArrayList<GwtAdminCategory> adminCategories;
		GwtAdminCategory managementCategory;
		GwtAdminCategory systemCategory;
		GwtAdminCategory reportsCategory;
		GwtAdminAction adminAction;
		String title;
		String url;
		AdaptedPortletURL adaptedUrl;
		User user;
 		Workspace top = null;
		Workspace teamWorkspaceBinder = null;
		ProfileBinder profilesBinder;
		DefinitionModule definitionModule;
		LdapModule ldapModule;
		AdminModule adminModule;
		ProfileModule profileModule;
		WorkspaceModule workspaceModule;
		BinderModule binderModule;
		LicenseModule licenseModule;
		ZoneModule zoneModule;
		boolean isFilr;
		boolean	userHasAdminRights;
		
		definitionModule = bs.getDefinitionModule();
		ldapModule = bs.getLdapModule();
		adminModule = bs.getAdminModule();
		profileModule = bs.getProfileModule();
		workspaceModule = bs.getWorkspaceModule();
		binderModule = bs.getBinderModule();
		licenseModule = bs.getLicenseModule();
		zoneModule = bs.getZoneModule();
		
		user = getCurrentUser();
		userHasAdminRights = adminModule.testAccess(AdminOperation.manageFunction);

		isFilr = LicenseChecker.showFilrFeatures();

 		try
 		{
			top = workspaceModule.getTopWorkspace();
 		}
 		catch( Exception e )
 		{}
 		
 		profilesBinder = profileModule.getProfileBinder();
 		
 		try
 		{
	 		teamWorkspaceBinder = ((Workspace) getCoreDao().loadReservedBinder(
				ObjectKeys.TEAM_ROOT_INTERNALID,
				RequestContextHolder.getRequestContext().getZoneId() ));
 		}
 		catch( Exception e )
 		{}
		
		// Create an ArrayList that will hold the GwtAdminCategory objects.
		adminCategories = new ArrayList<GwtAdminCategory>();
		
		// Create a "Management" category
		{
	 		managementCategory = new GwtAdminCategory();
	 		managementCategory.setLocalizedName( NLT.get( "administration.category.management" ) );
	 		managementCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.MANAGEMENT );
			adminCategories.add( managementCategory );
			
			// Does the user have rights to "Manage User Accounts"?
			//   This function covers: Add Account, Disable/Delete Accounts, Import Profiles.
			try
			{
				if ( userHasAdminRights || profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
				{
					List defaultEntryDefinitions;

					defaultEntryDefinitions = profilesBinder.getEntryDefinitions();
					
					if ( !defaultEntryDefinitions.isEmpty() )
					{
						Definition def = (Definition) defaultEntryDefinitions.get( 0 );
						title = NLT.get( "administration.manage.userAccounts" );

						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", true );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY );
						adaptedUrl.setParameter( WebKeys.URL_BINDER_ID, profilesBinder.getId().toString() );
						adaptedUrl.setParameter( WebKeys.URL_ENTRY_TYPE, def.getId() );
						adaptedUrl.setParameter(WebKeys.URL_CONTEXT, "adminMenu");
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.ADD_USER );
						
						// Add this action to the "Management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
			}
			catch( AccessControlException e )
			{}
			
			// Does the user have rights to "Manage groups"?
			try
			{
				if ( userHasAdminRights || profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
				{
					// Yes
					title = NLT.get( "administration.manage.groups" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_GROUPS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_GROUPS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch( AccessControlException e ) {}

			// Are we exposing Vibe features?
			if ( LicenseChecker.showVibeFeatures() )
			{
				// DRF (20150302)
				//    Commented out until we have a full Team
				//    management solution.  What's there simply
				//    manages the contents of the root Team Workspaces
				//    binder.
/*
				// Yes!  Does the user have rights to "Manage teams"?
				try
				{
					if ( ( ( null != teamWorkspaceBinder) && binderModule.testAccess( teamWorkspaceBinder, BinderOperation.manageConfiguration ) ) )
					{
						// Yes
						title = NLT.get( "administration.manage.teams" );
	
						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_TEAMS );
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.MANAGE_TEAMS );
						
						// Add this action to the "management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
				catch( AccessControlException e ) {}
*/
			}
			
			// Is this the built-in administrator?
			if ( user.isAdmin() )
			{
				try
				{
					// Add a Manage Administrators action to the
					// "management" category
					title = NLT.get( "administration.manage.administrators" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_ADMINISTRATORS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_ADMINISTRATORS );
					
					managementCategory.addAdminOption( adminAction );
				}
				catch( AccessControlException e ) {}
			}
			
			// Does the user have rights to "Limit User Visibility"?
			try
			{
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.manage.limitUserVisibility" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_USER_VISIBILITY );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_USER_VISIBILITY );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

			// Does the user have rights to manage default user
			// settings?
			try
			{
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.manage.defaultUserSettings" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFAULT_USER_SETTINGS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_DEFAULT_USER_SETTINGS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

			// Does the user have rights to "Manage shares"?
			try
			{
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.manage.shareItems" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_SHARE_ITEMS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_SHARE_ITEMS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

			// DRF (20131105):
			//    As part of Lynn's redesign of the Management and
			//    System categories for Filr, I folded the single item
			//    in the GWT based 'Personal Storage' dialog into the
			//    JSP based 'Personal Storage Quotas' dialog and
			//    renamed that 'Personal Storage'.
			//
			//    At some point we're going to want to rewrite that JSP
			//    dialog into GWT so I left the GWT dialog with the
			//    single item in place so that we can resurrect it for
			//    that purpose.
/*
			// Does the user have rights to "manage adhoc folder?"?
			if ( isFilr && userIsAdmin )
			{
				// Yes
				title = NLT.get( "administration.configure_adhocFolders" );

				adminAction = new GwtAdminAction();
				adminAction.init( title, "", AdminAction.CONFIGURE_ADHOC_FOLDERS );
				
				// Add this action to the "management" category
				managementCategory.addAdminOption( adminAction );
			}
*/

			// Does the user have rights to "Manage quotas"?
			try
			{
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.manage.quotas" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_QUOTAS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_QUOTAS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

			// Does the user have rights to "Manage file upload limits"?
			try
			{
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.manage.fileUploadLimits" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_FILE_UPLOAD_LIMITS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_FILE_UPLOAD_LIMITS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			catch(AccessControlException e) {}

			// Should we show the Filr features?
			if ( isFilr )
			{
				// Yes
				// Add the Filr specific features
				
				// Does the user have rights to "Manage Mobile Devices"?
				try
				{
					if ( userHasAdminRights || profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
					{
						title = NLT.get( "administration.manage.mobileDevices" );
						
						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_MOBILE_DEVICES );
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.MANAGE_MOBILE_DEVICES );
						
						// Add this action to the "Management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
				catch( AccessControlException e )
				{}

				// Does the user have rights to "Manage net folders"?
				try
				{
					Binder netFoldersParentBinder = null;
					
					try
					{
						netFoldersParentBinder = SearchUtils.getNetFoldersRootBinder();
					}
					catch ( Exception ex )
					{
					}
	
					if ( userHasAdminRights &&
						 LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder") &&
						 netFoldersParentBinder != null )
					{
						// Yes
						title = NLT.get( "administration.manage.netFolders" );
	
						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_NET_FOLDERS );
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.MANAGE_NET_FOLDERS );
						
						// Add this action to the "management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
				catch(AccessControlException e) {}

				// Does the user have rights to "Manage resource drivers"?
				try
				{
					if ( userHasAdminRights &&
						 LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder") &&
						 adminModule.testAccess( AdminOperation.manageResourceDrivers ) )
					{
						// Yes
						title = NLT.get( "administration.manage.resourceDrivers" );
	
						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_RESOURCE_DRIVERS );
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.MANAGE_RESOURCE_DRIVERS );
						
						// Add this action to the "management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
				catch(AccessControlException e) {}

				// Does the user have rights to "manage JITS configuration"?
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.configure_jits_zone_config" );

					adminAction = new GwtAdminAction();
					adminAction.init( title, "", AdminAction.NET_FOLDER_GLOBAL_SETTINGS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
				
				// Does the user have rights to "manage proxy identities"?
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.configure_proxy_identities" );

					adminAction = new GwtAdminAction();
					adminAction.init( title, "", AdminAction.MANAGE_PROXY_IDENTITIES );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}
			else
			{
				// No, show the Vibe specific features.
				// The following are the Vibe specific 'Management' actions
				// not addressed by Lynn's Filr redesign of this category.
				// He said to leave them in the order they appear below.
				//     Workspace and Folder Templates
				//     Zones
				//     Applications
				//     Application Groups
				//     Extensions
				//     Database Logs and File Archiving
				
				// Does the user have rights to "Manage workspace and folder templates"?
				if ( adminModule.testAccess( AdminOperation.manageTemplate ) )
				{
					// Yes
					title = NLT.get( "administration.configure_configurations" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURATION );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_WORKSPACE_AND_FOLDER_TEMPLATES );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}

				// Does the user have rights to "Manage zones"?
				if ( LicenseChecker.isAuthorizedByLicense( "com.novell.teaming.module.zone.MultiZone" ) &&
						zoneModule.testAccess() )
				{
					// Yes
					title = NLT.get( "administration.manage.zones" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_ZONES );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_ZONES );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}

				// Does the user have rights to "Manage applications"?
				try
				{
					if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
					{
						// Yes
						title = NLT.get( "administration.manage.applications" );

						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_APPLICATIONS );
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.MANAGE_APPLICATIONS );
						
						// Add this action to the "management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
				catch(AccessControlException e) {}

				// Does the user have rights to "Manage application groups"?
				try
				{
					if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
					{
						// Yes
						title = NLT.get( "administration.manage.application.groups" );

						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_APPLICATION_GROUPS );
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.MANAGE_APPLICATION_GROUPS );
						
						// Add this action to the "management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
				catch(AccessControlException e) {}

				// Does the user have rights to "Manage Extensions"?
				if ( adminModule.testAccess( AdminOperation.manageExtensions ) )
				{
					// Yes
					title = NLT.get( "administration.manage.extensions" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_EXTENSIONS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_EXTENSIONS );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}

			// Does the user have rights to "manage database pruning"? 
			if ( userHasAdminRights )
			{
				// Yes
				String key;
				if (Utils.checkIfVibe() || Utils.checkIfFilrAndVibe())
				     key = "administration.configure_database_prune.vibe";
				else key = "administration.configure_database_prune.filr";
				title = NLT.get( key );

				adminAction = new GwtAdminAction();
				adminAction.init( title, "", AdminAction.MANAGE_DATABASE_PRUNE );
				
				// Add this action to the "management" category
				managementCategory.addAdminOption( adminAction );
			}
			
			// Does the user have rights to "Manage the search index"?
			if ( top != null )
			{
				boolean allowIndexing = (
					ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() ) && 
					binderModule.testAccess( top, BinderOperation.indexBinder ) );
				
				if ( ( ! allowIndexing ) && ManageSearchIndexController.INDEX_AS_BUILT_IN_ADMIN && userHasAdminRights )
				{
					User builtInAdmin = BuiltInUsersHelper.getZoneSuperUser();
					allowIndexing = (
						( null != builtInAdmin ) &&
						binderModule.testAccess( builtInAdmin, top, BinderOperation.indexBinder, Boolean.FALSE ) );
				}
				
				if ( allowIndexing )
				{
					// Yes
					if ( ( adminModule.retrieveIndexNodesHA() != null ) || SPropsUtil.getBoolean( "force.ha.index.config", false ) )
					{
						GwtAdminCategory manageSearchIndexCategory;
						
						// Create a "manage search index category.
				 		manageSearchIndexCategory = new GwtAdminCategory();
				 		manageSearchIndexCategory.setLocalizedName( NLT.get( "administration.configure_search_index" ) );
				 		manageSearchIndexCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.MANAGE_SEARCH_INDEX );
						adminCategories.add( manageSearchIndexCategory );
						
						// Add a "Folder Index" action
						{
							title = NLT.get( "administration.search.title.index" );
	
							adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
							adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE );
							url = adaptedUrl.toString();
							
							adminAction = new GwtAdminAction();
							adminAction.init( title, url, AdminAction.CONFIGURE_FOLDER_INDEX );
							
							// Add this action to the "manage search index" category
							manageSearchIndexCategory.addAdminOption( adminAction );
						}

						// Add a "Folder Search Nodes" action
						{
							title = NLT.get( "administration.search.title.nodes" );
	
							adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
							adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FOLDER_SEARCH_NODES_CONFIGURE );
							url = adaptedUrl.toString();
							
							adminAction = new GwtAdminAction();
							adminAction.init( title, url, AdminAction.CONFIGURE_FOLDER_SEARCH_NODES );
							
							// Add this action to the "manage search index" category
							manageSearchIndexCategory.addAdminOption( adminAction );
						}
						
						// Add an "Update Logs" action
						{
							title = NLT.get( "administration.search.title.updateLogs" );
							
							adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
							adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FOLDER_SEARCH_UPDATE_LOGS_CONFIGURE );
							url = adaptedUrl.toString();
							
							adminAction = new GwtAdminAction();
							adminAction.init( title, url, AdminAction.CONFIGURE_FOLDER_UPDATE_LOGS );
							
							// Add this action to the "manage search index" category
							manageSearchIndexCategory.addAdminOption( adminAction );
						}
					}
					else
					{
						// Add a "Manage the search index" action.
						title = NLT.get( "administration.configure_search_index" );

						adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
						adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FOLDER_INDEX_CONFIGURE );
						url = adaptedUrl.toString();
						
						adminAction = new GwtAdminAction();
						adminAction.init( title, url, AdminAction.CONFIGURE_SEARCH_INDEX );
						
						// Add this action to the "management" category
						managementCategory.addAdminOption( adminAction );
					}
				}
			}

			// Does the user have rights to "Manage license"?
			if( ReleaseInfo.isLicenseRequiredEdition() )
			{
				if ( licenseModule.testAccess( LicenseOperation.manageLicense ) )
				{
					// Yes
					title = NLT.get( "administration.manage.license" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_LICENSE );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.MANAGE_LICENSE );
					
					// Add this action to the "management" category
					managementCategory.addAdminOption( adminAction );
				}
			}

			// Does the current user have admin rights?
			if (userHasAdminRights) {
				// Yes!  Is this the default zone?
				Long defaultZoneId = zoneModule.getZoneIdByVirtualHost(null);	// null -> Returns default zone ID.
				boolean defaultZone = RequestContextHolder.getRequestContext().getZoneId().equals(defaultZoneId);
				if (defaultZone) {
					// Yes!  Add a configure telemetry option.
					adaptedUrl = new AdaptedPortletURL(request, "ss_forum", false);
					adaptedUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_TELEMETRY);
					
					adminAction = new GwtAdminAction();
					adminAction.init(
						NLT.get("administration.configure_cfg.telemetry"),
						adaptedUrl.toString(),
						AdminAction.CONFIGURE_TELEMETRY);
					
					// Add this action to the 'management' category.
					managementCategory.addAdminOption(adminAction);
				}
			}
		}
		
		// Create a "System" category
		{
			systemCategory = new GwtAdminCategory();
			systemCategory.setLocalizedName( NLT.get( "administration.category.system" ) );
			systemCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.SYSTEM );
			adminCategories.add( systemCategory );

			// Does the user have rights to "Share settings"?
			if ( adminModule.testAccess( AdminOperation.manageFunctionMembership ) )
			{
				title = NLT.get( "administration.configure_shareSettings" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_SHARE_SETTINGS );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_SHARE_SETTINGS );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "configure user access"?
			if ( userHasAdminRights )
			{
				// Yes
				title = NLT.get( "administration.configure_userAccess" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_USER_ACCESS );
				url = adaptedUrl.toString();

				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_USER_ACCESS );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Yes, are we running the Enterprise version of Teaming?
			if ( ReleaseInfo.isLicenseRequiredEdition() == true )
			{
				// Yes
				// Does the user have rights to "Configure File Sync App"?
				if ( adminModule.testAccess( AdminOperation.manageFileSynchApp ) )
				{
					// Yes
					title = NLT.get( "administration.configureVibeDesktop" );
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, "", AdminAction.CONFIGURE_FILE_SYNC_APP );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
			}
			
			if ( isFilr )
			{
				// Does the user have rights to "Configure Mobile Apps"?
				if ( adminModule.testAccess( AdminOperation.manageMobileApps ) )
				{
					// Yes
					title = NLT.get( "administration.configureMobileApps" );
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, "", AdminAction.CONFIGURE_MOBILE_APPS );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
			}

			// Does the user have rights to "manage password policies"?
			if ( userHasAdminRights )
			{
				// Yes
				title = NLT.get( "administration.configure_passwordPolicy" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_PASSWORD_POLICY );
				url = adaptedUrl.toString();

				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_PASSWORD_POLICY );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}
			
			if ( ! isFilr )
			{
				// The following are the Vibe specific 'System' actions not
				// addressed by Lynn's Filr redesign of this category.  He
				// said to leave them in the order they appear below.
				//     Form/View Designers
				//     Mobile Access
				//     Default Landing Pages
				//     Role Definitions
				//     Weekends and Holidays
				//     File Version Aging
				//     Access Control

				// Does the user have rights to "Form/View Designers"?
				if ( ( null != top ) &&
				     (definitionModule.testAccess( top, Definition.FOLDER_ENTRY, DefinitionOperation.manageDefinition ) ||
					  definitionModule.testAccess( top, Definition.WORKFLOW,     DefinitionOperation.manageDefinition )) )
				{
					// Yes
					title = NLT.get( "administration.definition_builder_designers" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MANAGE_DEFINITIONS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.FORM_VIEW_DESIGNER );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
				
				// Does the user have rights to "configure mobile access"?
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.configure_mobileAccess" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_MOBILE_ACCESS );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.CONFIGURE_MOBILE_ACCESS );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}

				// Does the user have rights to "configure home page"?
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.configure_homePage" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_HOME_PAGE );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.CONFIGURE_HOME_PAGE );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}

				// Does the user have rights to "Configure Role Definitions"?
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.configure_roles" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ADMIN_ACTION_CONFIGURE_ROLES );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.CONFIGURE_ROLE_DEFINITIONS );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
				
				// Does the user have rights to "Configure Weekends and Holidays"?
				if ( userHasAdminRights )
				{
					// Yes
					title = NLT.get( "administration.configure.schedule.action" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ADMIN_ACTION_CONFIGURE_SCHEDULE );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.CONFIGURE_SCHEDULE );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
				
				// Does the user have rights to "Configure File Version Aging"?
				if ( adminModule.testAccess( AdminOperation.manageFileVersionAging ) )
				{
					// Yes
					title = NLT.get( "administration.configure_file_version_aging" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_FILE_VERSION_AGING_JOB_CONFIGURE);
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.CONFIGURE_FILE_VERSION_AGING );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}

				// Does the user have rights to "Access Control for Zone Administration functions"?
				if ( adminModule.testAccess( AdminOperation.manageFunctionMembership ) )
				{
					ZoneConfig zoneConfig;
					
					// Yes
					zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
					title = NLT.get( "administration.manage.accessControl" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ACCESS_CONTROL );
					adaptedUrl.setParameter( WebKeys.URL_WORKAREA_ID, zoneConfig.getWorkAreaId().toString() );
					adaptedUrl.setParameter( WebKeys.URL_WORKAREA_TYPE, zoneConfig.getWorkAreaType() );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.ACCESS_CONTROL_FOR_ZONE_ADMIN_FUNCTIONS );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
			}

			// Does the user have rights to "Configure E-Mail"?
			if ( adminModule.testAccess( AdminOperation.manageMail ) )
			{
				// Yes
				title = NLT.get( "administration.configure_mail" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_POSTINGJOB_CONFIGURE );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.CONFIGURE_EMAIL );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}
			
			// Does the user have rights to 'Configure Email
			// Templates'?
			if (userHasAdminRights) {
				// Yes!  Create an AdminAction for it...
				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_EMAIL_TEMPLATES_CONFIGURE);
				url = adaptedUrl.toString();
				
				title = NLT.get("administration.configure_mailTemplates");
				adminAction = new GwtAdminAction();
				adminAction.init(title, url, AdminAction.CONFIGURE_EMAIL_TEMPLATES);
				
				// ...and add it to the 'system' category.
				systemCategory.addAdminOption(adminAction);
			}
			
			// Are we running the Enterprise version of Teaming or Filr?
			if ( ReleaseInfo.isLicenseRequiredEdition() == true || isFilr )
			{
				// Yes
				// Does the user have the rights to manage KeyShield SSO?
				if ( userHasAdminRights )
				{
					title = NLT.get( "administration.configure_keyshield" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_KEYSHIELD_CONFIGURE );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.KEYSHIELD_CONFIG );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
			}

			// Does the current user have admin rights and is this a
			// licensed product version?
			if (userHasAdminRights && ReleaseInfo.isLicenseRequiredEdition() && SPropsUtil.getBoolean("show.anti.virus.in.admin.console", false)) {
				// Yes!  Add a configure anti virus option.
				adaptedUrl = new AdaptedPortletURL(request, "ss_forum", false);
				adaptedUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_CONFIGURE_ANTIVIRUS);
				
				adminAction = new GwtAdminAction();
				adminAction.init(
					NLT.get("administration.configure_cfg.antivirus"),
					adaptedUrl.toString(),
					AdminAction.CONFIGURE_ANTIVIRUS);
				
				// Add this action to the 'system' category.
				systemCategory.addAdminOption(adminAction);
			}
			
			// Does the user have the rights to "ldap configuration"?
			if ( ldapModule.testAccess(LdapOperation.manageLdap ) )
			{
				// Yes
				if ( ldapModule.getLdapSchedule() != null )
				{
					title = NLT.get( "administration.configure_ldap" );

					adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
					adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_LDAP_CONFIGURE );
					url = adaptedUrl.toString();
					
					adminAction = new GwtAdminAction();
					adminAction.init( title, url, AdminAction.LDAP_CONFIG );
					
					// Add this action to the "system" category
					systemCategory.addAdminOption( adminAction );
				}
			}
			
			// Does the user have rights to "configure name completion settings"?
			if ( userHasAdminRights )
			{
				// Yes
				title = NLT.get( "administration.configure_nameCompletion" );

				adminAction = new GwtAdminAction();
				adminAction.init( title, "", AdminAction.CONFIGURE_NAME_COMPLETION );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to run reports?
			if ( adminModule.testAccess( AdminOperation.report ) )
			{
				// Yes
				title = NLT.get( "administration.category.reports" );
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, "", AdminAction.RUN_A_REPORT );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "Site Branding"
			if ( top != null && binderModule.testAccess( top, BinderOperation.modifyBinder ) )
			{
				// Yes
				title = NLT.get( "administration.modifySiteBranding" );

				adminAction = new GwtAdminAction();
				adminAction.init( title, "", AdminAction.SITE_BRANDING );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}

			// Is this Filr and does the user have system admin rights
			// and should we show the Mobile Site Branding option?
			if (userHasAdminRights && org.kablink.teaming.util.SiteBrandingHelper.isMobileBrandingSupported()) {
				// Yes!  Create the Mobile Site branding option...
				title = NLT.get("administration.modifySiteBranding.mobile");

				adminAction = new GwtAdminAction();
				adminAction.init(title, "", AdminAction.MOBILE_SITE_BRANDING);
				
				// ...and add it to the 'system' category
				systemCategory.addAdminOption(adminAction);
			}
			
			// Is this Filr and does the user have system admin rights
			// and should we show the Desktop Site Branding option?
			if (userHasAdminRights && org.kablink.teaming.util.SiteBrandingHelper.isDesktopBrandingSupported()) {
				// Yes!  Create the Desktop Site branding option...
				title = NLT.get("administration.modifySiteBranding.desktop");

				adminAction = new GwtAdminAction();
				adminAction.init(title, "", AdminAction.DESKTOP_SITE_BRANDING);
				
				// ...and add it to the 'system' category
				systemCategory.addAdminOption(adminAction);
			}
		}
		
		// Create a "Reports" category
		reportsCategory = new GwtAdminCategory();
		reportsCategory.setLocalizedName( NLT.get( "administration.category.reports" ) );
		reportsCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.REPORTS );
		adminCategories.add( reportsCategory );

		// Does the user have rights to run reports?
		if ( adminModule.testAccess( AdminOperation.report ) )
		{
			// Yes
			// Create an "e-mail report"
			{
				title = NLT.get( "administration.report.title.email" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_EMAIL_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_EMAIL );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Create a "login report"
			{
				title = NLT.get( "administration.report.title.login" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_LOGIN_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_LOGIN );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "License Report"
			//License report
			if( ReleaseInfo.isLicenseRequiredEdition() )
			{
				title = NLT.get( "administration.report.title.license" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_LICENSE_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_LICENSE );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "Activity by User"
			{
				title = NLT.get( "administration.report.title.activityByUser" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ACTIVITY_REPORT_BY_USER );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_ACTIVITY_BY_USER );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "External User"
			{
				title = NLT.get( "administration.report.title.externalUser" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_EXTERNAL_USER_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_EXTERNAL_USER );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "Disk usage report"
			{
				title = NLT.get( "administration.report.title.quota" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_QUOTA_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_DISK_USAGE );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "Data quota exceeded report"
			{
				title = NLT.get( "administration.report.title.disk_quota_exceeded" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_QUOTA_EXCEEDED_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_DATA_QUOTA_EXCEEDED );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "Data quota highwater exceeded report"
			{
				title = NLT.get( "administration.report.title.highwater_exceeded" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_QUOTA_HIGHWATER_EXCEEDED_REPORT );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "User access report"
			{
				title = NLT.get( "administration.report.title.user_access" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_USER_ACCESS_REPORT  );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_USER_ACCESS );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
			
			// Add a "XSS report"
			{
				title = NLT.get( "administration.report.title.xss", "XSS Report" );

				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_XSS_REPORT  );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_XSS );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
		}

		// Add reports that everyone can run.
		{
			// Add a "Credits report" action.
			{
				title = NLT.get( "administration.credits" );
	
				adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
				adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_VIEW_CREDITS );
				url = adaptedUrl.toString();
				
				adminAction = new GwtAdminAction();
				adminAction.init( title, url, AdminAction.REPORT_VIEW_CREDITS );
				
				// Add this action to the "reports" category
				reportsCategory.addAdminOption( adminAction );
			}
		}

		// Does the user have rights to run "Content Modification Log Report"?
		if ( isFilr == false && userHasAdminRights )
		{
			// Yes
			title = NLT.get( "administration.view_change_log" );
			
			adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
			adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_VIEW_CHANGELOG );
			url = adaptedUrl.toString();
			
			adminAction = new GwtAdminAction();
			adminAction.init( title, url, AdminAction.REPORT_VIEW_CHANGELOG );
			
			// Add this action to the "reports" category
			reportsCategory.addAdminOption( adminAction );
		}

		// Does the user have rights to run the "System error logs" report?
		if ( adminModule.testAccess( AdminOperation.manageErrorLogs ) )
		{
			// Yes
			title = NLT.get( "administration.system_error_logs" );
			
			adaptedUrl = new AdaptedPortletURL( request, "ss_forum", false );
			adaptedUrl.setParameter( WebKeys.ACTION, WebKeys.ADMIN_ACTION_GET_LOG_FILES );
			url = adaptedUrl.toString();
			
			adminAction = new GwtAdminAction();
			adminAction.init( title, url, AdminAction.REPORT_VIEW_SYSTEM_ERROR_LOG );
			
			// Add this action to the "reports" category
			reportsCategory.addAdminOption( adminAction );
		}

		return adminCategories;
	}// end getAdminActions()
	
	
	/**
	 * Return a list of all the groups in Vibe.
	 * 
	 * @param bs
	 * @param filter
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static List<GroupInfo> getAllGroups(AllModulesInjected bs, String filter) throws GwtTeamingException {
		ArrayList<GroupInfo> reply = new ArrayList<GroupInfo>();

		GroupSelectSpec groupSelectSpec = new GroupSelectSpec();
		groupSelectSpec.setFilter(filter);
		groupSelectSpec.setExcludeAllExternalUsersGroup(true);
		groupSelectSpec.setExcludeAllUsersGroup(true);
		groupSelectSpec.setExcludeTeamGroups(true);
		
		List<Group> listOfGroups = bs.getProfileModule().getGroups(groupSelectSpec);
		if (null != listOfGroups) {
			for (Group nextGroup:  listOfGroups) {
				GroupInfo groupInfo = new GroupInfo();
				Long groupId = nextGroup.getId();
				groupInfo.setId(groupId);
				groupInfo.setTitle(nextGroup.getTitle());
				groupInfo.setName(nextGroup.getName());
				groupInfo.setDesc(nextGroup.getDescription().getText());
				groupInfo.setIsFromLdap(nextGroup.getIdentityInfo().isFromLdap());
				groupInfo.setDn(nextGroup.getForeignName());
				groupInfo.setAdmin(AdminHelper.isSiteAdminMember(groupId));
				groupInfo.setMembershipInfo(nextGroup.isDynamic(), (!nextGroup.getIdentityInfo().isInternal()));
				
				reply.add(groupInfo);
			}
		}
		
		return reply;
	}
	
	/**
	 * Returns sharing rights information about the system and a list
	 * of binders, based on their IDs.
	 * 
	 * @param bs
	 * @param request
	 * @param binderIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BinderSharingRightsInfoRpcResponseData getBinderSharingRightsInfo(AllModulesInjected bs, HttpServletRequest request, List<Long> binderIds) throws GwtTeamingException {
		try {
			// Create the BinderSharingRightsInfoRpcResponseData to return.
			BinderSharingRightsInfoRpcResponseData reply = new BinderSharingRightsInfoRpcResponseData();

			// Are there any work area function memberships configured
			// on the zone?
	    	ZoneConfig							zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			AdminModule							am         = bs.getAdminModule();
			BinderModule						bm         = bs.getBinderModule();
			List<WorkAreaFunctionMembership>	wafmList   = am.getWorkAreaFunctionMemberships(zoneConfig);
			if (MiscUtil.hasItems(wafmList)) {
				// Yes!  Scan them.
				for (WorkAreaFunctionMembership wafm:  wafmList) {
					// Is this an internal function that has members?
					String fiId = am.getFunction(wafm.getFunctionId()).getInternalId();
					if (MiscUtil.hasString(fiId) && MiscUtil.hasItems(wafm.getMemberIds())) {
						// Yes!  Check it for being one of the sharing
						// functions.
						if      (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_EXTERNAL_SHARING_INTERNALID)) reply.setExternalEnabled(   true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_FORWARD_SHARING_INTERNALID))  reply.setForwardingEnabled( true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_INTERNAL_SHARING_INTERNALID)) reply.setInternalEnabled(   true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_PUBLIC_SHARING_INTERNALID))   reply.setPublicEnabled(     true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_LINK_SHARING_INTERNALID))     reply.setPublicLinksEnabled(true);

						// Once we set all the flags...
						if (reply.allFlagsSet()) {
							// ...we can quit looking.
							break;
						}
					}
				}
			}
			
			// We're we given a single binder whose sharing rights are
			// being requested?
			if ((null != binderIds) && (1 == binderIds.size())) {
				// Yes!  Can we resolve that to a Binder?
				Set<Binder>					bList = bm.getBinders(binderIds);
				PerEntityShareRightsInfo	psri  = new PerEntityShareRightsInfo();
				if (MiscUtil.hasItems(bList)) {
					Binder b = bList.iterator().next();
					// Yes!  Are there any work area function
					// memberships configured on that binder?
					wafmList = am.getWorkAreaFunctionMemberships(b);
					if (MiscUtil.hasItems(wafmList)) {
						// Yes!  Scan them.
						for (WorkAreaFunctionMembership wafm:  wafmList) {
							// Is this an internal function
							// that has owner as a member?
							String		fiId      = am.getFunction(wafm.getFunctionId()).getInternalId();
							Set<Long>	memberIds = wafm.getMemberIds();
							if (MiscUtil.hasString(fiId) && MiscUtil.hasItems(memberIds) && memberIds.contains(ObjectKeys.OWNER_USER_ID)) {
								// Yes!  Check for it being one
								// of the sharing functions.
								if      (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID))     psri.setAllowExternal(   true);
								else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID))      psri.setAllowForwarding( true);
								else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID))     psri.setAllowInternal(   true);
								else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID))       psri.setAllowPublic(     true);
								else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_LINKS_INTERNALID)) psri.setAllowPublicLinks(true);
								
								// Once we set all the flags...
								if (psri.allFlagsSet()) {
									// ...we can quit looking.
									break;
								}
							}
						}
					}
				}

				// Add the per binder sharing rights to the reply.
				reply.addBinderRights(binderIds.get(0), psri);
			}

			// If we get here, reply refers to the
			// BinderSharingRightsInfoRpcResponseData containing the
			// requested sharing rights information.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	/**
	 * Returns a string for a value out of the entry map from a search
	 * results.
	 * 
	 * @param em
	 * @param key
	 * @param dateStyle
	 * @param timeStyle
	 * 
	 * @return
	 */
	public static boolean getBooleanFromEntryMap(Map em, String key) {
		boolean	reply = false;
		Object	v     = getValueFromEntryMap(em, key);
		if (null != v) {
			if      (v instanceof Boolean) reply = ((Boolean) v);
			else if (v instanceof String)  reply = ((String) v).trim().toLowerCase().equals("true");
		}
		return reply;
	}

	/**
	 * Return the 'raw' branding data for the given binder.  We will
	 * not fixup any image URLs.
	 * 
	 * @param bs
	 * @param binderId
	 * @param request
	 * @param servletContext
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GwtBrandingData getRawBinderBrandingData(AbstractAllModulesInjected bs, String binderId, HttpServletRequest request, ServletContext servletContext) throws GwtTeamingException {
		GwtBrandingData brandingData = new GwtBrandingData();
		brandingData.setBrandingType(GwtBrandingDataExt.BRANDING_TYPE_IMAGE);
		
		try {
			// Get the binder object.
			BinderModule bm = bs.getBinderModule();
			Long binderIdL = new Long(binderId);
			if (null != binderIdL) {
				// Does the user have rights to the binder where the branding is coming from?
				Binder binder = bm.getBinder(binderIdL);
				if (!(bm.testAccess(binder, BinderOperation.readEntries))) {
					return brandingData;
				}
				brandingData.setBinderId(binderId);

				// Get the advanced branding that should be applied for
				// this binder.
				String branding = binder.getBranding();
				
				// For some unknown reason, if there is no branding in
				// the database the string we get back will contain
				// only a '\n'.  We don't want that.
				if ((null != branding) && (1 == branding.length()) && ('\n' == branding.charAt(0))) {
					branding = "";
				}
				
				// Remove mce_src as an attribute from all <IMG> tags.
				// See bug 766415.  There was a bug that caused the
				// mce_src attribute to be included in the <IMG> tag
				// and written to the database.  We want to remove it.
				branding = MarkupUtil.removeMceSrc(branding);
				brandingData.setBranding(branding);
				
				// Get the additional branding information.
				GwtBrandingDataExt brandingExt = new GwtBrandingDataExt();
				
				// Get the XML that represents the branding data.  The
				// following is an example of what the XML should look
				// like:
				//
				//		<brandingData fontColor="" brandingImgName="some name" brandingType="image/advanced">
				// 			<background color="" imgName="" />
				// 		</brandingData>
				String xmlStr = binder.getBrandingExt();
				
				// Is there old-style branding?
				if (MiscUtil.hasString(branding)) {
					// Yes!
					brandingExt.setBrandingType(GwtBrandingDataExt.BRANDING_TYPE_ADVANCED);
					
					// Is there additional branding data?
					if ((null == xmlStr) || (0 == xmlStr.length())) {
						// Yes, We are dealing with branding that was
						// created before the Durango release.  In
						// order to have existing branding still look
						// good even though we aren't using the old
						// background image, we will set the background
						// color to white and the font color to black.
						brandingExt.setBackgroundColor("white");
						brandingExt.setFontColor(      "black");
					}
				}
				
				else {
					// No!
					brandingExt.setBrandingType(GwtBrandingDataExt.BRANDING_TYPE_IMAGE);
				}

				if (MiscUtil.hasString(xmlStr)) {
					try {
						// Parse the XML string into an XML document.
		    			Document doc = XmlUtil.parseText(xmlStr);
		    			
		    			// Get the root element.
		    			Node node = doc.getRootElement();
		    			
		    			// Get the font color.
		    			Node attrNode = node.selectSingleNode("@fontColor");
		    			if (null != attrNode) {
		        			String fontColor = attrNode.getText();
		        			brandingExt.setFontColor(fontColor);
		    			}
		    			
		    			// Get the name of the branding image
	        			String imgName;
		    			attrNode = node.selectSingleNode("@brandingImgName");
		    			if (null != attrNode) {
		        			imgName = attrNode.getText();
		    				if (MiscUtil.hasString(imgName)) {
		    					brandingExt.setBrandingImgName(imgName);
		    				}
		    			}
		    			
		    			// Get the name of the branding image for the
		    			// login dialog.
		    			attrNode = node.selectSingleNode("@loginDlgImgName");
		    			if (null != attrNode) {
		        			imgName = attrNode.getText();
		    				if (MiscUtil.hasString(imgName)) {
		    					brandingExt.setLoginDlgImgName(imgName);
		    				}
		    			}
		    			
		    			// Get the type of branding, 'advanced' or
		    			// 'image'.
		    			attrNode = node.selectSingleNode("@brandingType");
		    			if (null != attrNode) {
		    				String type = attrNode.getText();
		    				if ((null != type) && type.equalsIgnoreCase(GwtBrandingDataExt.BRANDING_TYPE_IMAGE))
		    				     brandingExt.setBrandingType(GwtBrandingDataExt.BRANDING_TYPE_IMAGE   );
		    				else brandingExt.setBrandingType(GwtBrandingDataExt.BRANDING_TYPE_ADVANCED);
		    			}
		    			
		    			// Get the branding rule.
		    			attrNode = node.selectSingleNode("@brandingRule");
		    			if (null != attrNode) {
		    				String ruleName = attrNode.getText();
		    				if (null != ruleName) {
		    					if      (ruleName.equalsIgnoreCase(GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING.toString())) brandingExt.setBrandingRule(GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING);
		    					else if (ruleName.equalsIgnoreCase(GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING.toString()))   brandingExt.setBrandingRule(GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING  );
		    					else if (ruleName.equalsIgnoreCase(GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY.toString()))              brandingExt.setBrandingRule(GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY             );
		    				}
		    			}
		    			
		    			// Get the <background color="" imgName="" stretchImg="" />
		    			// node.
		    			node = node.selectSingleNode("background");
		    			if (null != node) {
		    				// Get the background color.
		    				attrNode = node.selectSingleNode("@color");
		    				if (null != attrNode) {
		        				String bgColor = attrNode.getText();
		        				brandingExt.setBackgroundColor(bgColor);
		    				}
		    				
		    				// Get the name of the background image.
		    				attrNode = node.selectSingleNode("@imgName");
		    				if (null != attrNode) {
		        				imgName = attrNode.getText();
			    				if (MiscUtil.hasString(imgName)) {
			    					brandingExt.setBackgroundImgName(imgName);
			    				}
		    				}

		    				// Get the value of whether or not to
		    				// stretch the background image.
        					brandingExt.setBackgroundImgStretchValue(true);
		    				attrNode = node.selectSingleNode("@stretchImg");
		    				if (null != attrNode) {
		        				String stretch = attrNode.getText();
		        				if ((null != stretch) && stretch.equalsIgnoreCase("false")) {
		        					brandingExt.setBackgroundImgStretchValue(false);
		        				}
		    				}
		    			}
		    		}
		    		catch (Exception e) {
		    			GwtLogHelper.warn(m_logger, "Unable to parse branding ext " + xmlStr, e);
		    		}
				}
				
				brandingData.setBrandingExt(brandingExt);
				
				// Are we dealing with site branding?  Get the top
				// workspace ID.
				Long topWorkspaceId = bs.getWorkspaceModule().getTopWorkspaceId();				
				
				// Are we dealing with the site branding.
				if (0 == binderIdL.compareTo(topWorkspaceId)) {
					// Yes!
					brandingData.setIsSiteBranding(true);
				}
			}
		}
		catch (NoBinderByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException   acEx) {/* Ignore. */}
		catch (Exception                e   ) {/* Ignore. */}
		
		return brandingData;
	}

	/**
	 * Return the branding data for the given binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param useInheritance
	 * @param request
	 * @param servletContext
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GwtBrandingData getBinderBrandingData(AbstractAllModulesInjected bs, String binderId, boolean useInheritance, HttpServletRequest request, ServletContext servletContext) throws GwtTeamingException {
		GwtBrandingData brandingData = new GwtBrandingData();
		brandingData.setBrandingType(GwtBrandingDataExt.BRANDING_TYPE_IMAGE);
		
		try {
			// Get the binder object.
			BinderModule bm = bs.getBinderModule();
			Long binderIdL = new Long(binderId);
			if (null != binderIdL) {
				// Are we supposed to use inheritance to get the branding?
				Binder binder = bm.getBinder(binderIdL);
				Binder brandingSourceBinder;
				String brandingSourceBinderId;
				if (useInheritance) {
					// Yes!  Get the binder where branding comes from.
					brandingSourceBinder = binder.getBrandingSource();
					
					// Does the user have rights to the binder where
					// the branding is coming from?
					if (!(bm.testAccess(brandingSourceBinder, BinderOperation.readEntries))) {
						// No, don't use inherited branding.
						brandingSourceBinderId = binderId;
						brandingSourceBinder   = binder;
					}
					else {
						brandingSourceBinderId = brandingSourceBinder.getId().toString();
					}
				}
				else {
					// No!  Get the branding from the given binder.
					brandingSourceBinderId = binderId;
					brandingSourceBinder   = binder;
				}
				
				brandingData.setBinderId(brandingSourceBinderId);
				brandingData = getRawBinderBrandingData(
					bs,
					brandingSourceBinderId,
					request,
					servletContext);
				
				// Get the advanced branding that should be applied for
				// this binder.
				String branding = brandingSourceBinder.getBranding();
				
				// Parse the advanced branding and replace all
				// <IMG src="{{attachmentUrl: image-name}}" /> with a
				// URL to the image that is visible to all users
				// (including the guest user.)  For example:
				//		<IMGg src="http://somehost/ssf/branding/binder/binderId/imgName" />
				branding = MarkupUtil.fixupImgUrls(
					bs,
					request,
					servletContext,
					brandingSourceBinder,
					branding);
												
				// Parse the advanced branding and replace any mark up
				// with the appropriate URL.  For example, replace
				//		{{atachmentUrl: somename.png}}
				// with a URL that looks like:
				// 		http://somehost/ssf/s/readFile/.../somename.png
				branding = MarkupUtil.markupStringReplacement(
					null,
					null,
					request,
					null,
					brandingSourceBinder,
					branding,
					"view");
				brandingData.setBranding(branding);
				
				// Do we have a branding image?
				String imgName = brandingData.getBrandingImageName();
				if (MiscUtil.hasString(imgName)) {
					// Yes!  Is the image name '__no image__' or
					// '__default teaming image__'?  (These are special
					// names that don't represent a real image file
					// name.)
					if ((!(imgName.equalsIgnoreCase("__no image__"))) && (!(imgName.equalsIgnoreCase("__default teaming image__")))) {
						// No, Get a url to the file.
						String fileUrl = BrandingUtil.getUrlToBinderBrandingImg(
							bs,
			    			request,
			    			servletContext,
			    			brandingSourceBinder,
			    			imgName);
						brandingData.setBrandingImageUrl(fileUrl);
					}
				}
				
				// Do we have a background image?
				imgName = brandingData.getBgImageName();
				if (MiscUtil.hasString(imgName)) {
					// Yes!  Get a url to the file.
					String fileUrl;
					fileUrl = BrandingUtil.getUrlToBinderBrandingImg(
						bs,
		    			request,
		    			servletContext,
		    			brandingSourceBinder,
		    			imgName);
					brandingData.setBgImageUrl(fileUrl);
				}
				
				// Do we have a branding image for the login dialog?
				imgName = brandingData.getLoginDlgImageName();
				if (MiscUtil.hasString(imgName)) {
					// Yes!  Is the image name '__no image__' or
					// '__default teaming image__'?  (These are special
					// names that don't represent a real image file
					// name.)
					if ((!(imgName.equalsIgnoreCase("__no image__"))) && (!(imgName.equalsIgnoreCase("__default teaming image__")))) {
						// No, get a url to the file.
						String fileUrl = BrandingUtil.getUrlToLoginBrandingImg(
							bs,
			    			request,
			    			servletContext,
			    			brandingSourceBinder,
			    			imgName);
    					brandingData.setLoginDlgImageUrl(fileUrl);
					}
				}
			}
		}
		catch (NoBinderByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException   acEx) {/* Ignore. */}
		catch (Exception                e   ) {/* Ignore. */}
		
		return brandingData;
	}

	/**
	 * Returns the User object that's the creator of a Binder.
	 * 
	 * Note that the creator is not always the owner (i.e.,
	 * binder.getOwner() may return something different.)  This will
	 * happen, for instance, with the guest user workspace.  For that
	 * binder, binder.getOwner() returns the admin user where this
	 * method will return the guest user.
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	public static User getBinderCreator(AllModulesInjected bs, Binder binder) {
		User         reply         = null;
		HistoryStamp creationStamp = ((null == binder)        ? null : binder.getCreation());
		Principal    owner         = ((null == creationStamp) ? null : creationStamp.getPrincipal());
		if (null != owner) {
			owner = Utils.fixProxy(owner);
			if (owner instanceof User) {
				reply = ((User) owner);
			}
		}
		return reply;
	}
	
	public static User getBinderCreator(AllModulesInjected bs, String binderId) {
		return getBinderCreator(bs, Long.parseLong(binderId));
	}
	
	public static User getBinderCreator(AllModulesInjected bs, Long binderId) {
		// Always use the initial form of the method.
		return getBinderCreator(bs, bs.getBinderModule().getBinder(binderId));
	}
	
	/**
	 * Returns the entity type of a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static String getBinderEntityType(AllModulesInjected bs, String binderId) {
		return getBinderEntityType(bs.getBinderModule().getBinder(Long.parseLong(binderId)));
	}
	
	public static String getBinderEntityType(Binder binder) {
		return binder.getEntityType().toString();
	}
	
	/**
	 * Returns an accessible Binder for a given ID.  If the Binder
	 * cannot be accessed for any reason, null is returned.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static Binder getBinderForWorkspaceTree(AllModulesInjected bs, String binderId) {
		return getBinderForWorkspaceTree(bs, binderId, false);
	}
	
	public static Binder getBinderForWorkspaceTree(AllModulesInjected bs, String binderId, boolean defaultToTop) {
		Binder reply;
		try {
			Long binderIdL = Long.parseLong(binderId);
			reply = getBinderForWorkspaceTree(bs, binderIdL, defaultToTop);
		}
		catch (NumberFormatException nfe) {
			GwtLogHelper.debug(m_logger, "GwtServerHelper.getBinderForWorkspaceTree( Can't Access Binder (NumberFormatException) ):  '" + ((null == binderId) ? "<nul>" : binderId) + "'");
			reply = null;
		}
		
		// If we get here, reply refers to the Binder if it could be
		// accessed and null otherwise.  Return it.
		return reply;
	}
	
	public static Binder getBinderForWorkspaceTree(AllModulesInjected bs, Long binderId) {
		return getBinderForWorkspaceTree(bs, binderId, false);
	}
	
	public static Binder getBinderForWorkspaceTree(AllModulesInjected bs, Long binderId, boolean defaultToTop) {
		Binder reply;
		try {
			// Bugzilla 944829:  In Vibe, we only allow access if the
			// user has direct access to the binder.  In Filr, we allow
			// access if they have direct or inferred access.
			boolean thisLevelOnly = (!(Utils.checkIfFilr()));	// true for Vibe, false for Filr.
			reply = bs.getBinderModule().getBinder(binderId, thisLevelOnly);
		}
		catch (Exception e) {
			GwtLogHelper.debug(m_logger, "GwtServerHelper.getBinderForWorkspaceTree( Can't Access Binder (AccessControlException) ):  '" + String.valueOf(binderId) + "'");
			reply = null;
		}

		// Do we have a Binder that's in the trash...
		if ((null != reply) && GwtUIHelper.isBinderPreDeleted(reply)) {
			// ...we want to ignore it.
			reply = null;
		}
		
		// If we couldn't access the binder and we're supposed to
		// default to the top workspace...
		if ((null == reply) && defaultToTop) {
			// ...default to it.
			try {
				reply = bs.getWorkspaceModule().getTopWorkspace();
			}
			catch (Exception e) {
				GwtLogHelper.debug(m_logger, "GwtServerHelper.getBinderForWorkspaceTree( Can't Default to Top Workspace ) ");
				reply = null;
			}
		}
		
		// If we get here, reply refers to the Binder if it could be
		// accessed and null otherwise.  Return it.
		return reply;
	}

	/**
	 * Returns a BinderInfo describing a binder.
	 *
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 */
	public static BinderInfo getBinderInfo(AllModulesInjected bs, HttpServletRequest request, String binderId) {
		BinderInfo reply;
		Binder binder = GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId);
		if (null == binder) {
			reply = new BinderInfo();
			reply.setBinderId(binderId);
		}
		else {
			reply = getBinderInfo(request, bs, binder);
		}
		return reply;
	}
	
	public static BinderInfo getBinderInfo(AllModulesInjected bs, HttpServletRequest request, Long binderId) {
		// Always use the initial form of the method.
		return getBinderInfo(bs, request, String.valueOf(binderId));
	}

	/**
	 * Returns a BinderInfo describing a binder.
	 * 
	 * @param request
	 * @param binder
	 * 
	 * @return
	 */
	public static BinderInfo getBinderInfo(HttpServletRequest request, AllModulesInjected bs, Binder binder) {
		// Allocate a BinderInfo and store the core binder information.
		BinderInfo reply = new BinderInfo();
		                                    reply.setBinderId(            binder.getId()                                 );
		                                    reply.setBinderTitle(         binder.getTitle()                              );
		                                    reply.setFolderHome(          binder.isHomeDir()                             );
		                                    reply.setFolderMyFilesStorage(BinderHelper.isBinderMyFilesStorage(   binder ));
		                                    reply.setLibrary(             binder.isLibrary()                             );
		                                    reply.setEntityType(          getBinderEntityType(                   binder ));
		                                    reply.setBinderType(          getBinderType(                         binder ));
		                                    reply.setCloudFolderRoot(     CloudFolderHelper.getCloudFolderRoot(  binder ));
		if      (reply.isBinderFolder())    reply.setFolderType(          getFolderTypeFromViewDef(bs, ((Folder) binder)));
		else if (reply.isBinderWorkspace()) reply.setWorkspaceType(       getWorkspaceType(                      binder ));
		
		try {
			Binder binderParent = binder.getParentBinder();
			if (null != binderParent) {
				reply.setParentBinderId(binderParent.getId());
			}
		}
		catch (Exception e) {/* Ignore. */}

		// If this is a mirrored file...
		if (FolderType.MIRROREDFILE.equals(reply.getFolderType())) {
			// ...mark whether it has a driver configured.
			String rdn = binder.getResourceDriverName();
			reply.setMirroredDriverConfigured(MiscUtil.hasString(rdn));
		}

		// If the binder has a description...
		Description binderDesc = binder.getDescription();
		if (null != binderDesc) {
			String desc = binderDesc.getText();
			if (MiscUtil.hasString(desc)) {
				// ...store it.
				int descFmt = binderDesc.getFormat();
				boolean descIsHTML = (Description.FORMAT_HTML == descFmt); 
				if (descIsHTML) {
					desc = MarkupUtil.markupStringReplacement(null, null, request, null, binder, desc, WebKeys.MARKUP_VIEW, false);
					desc = MarkupUtil.markupSectionsReplacement(desc);
				}
				reply.setBinderDesc(    desc      );
				reply.setBinderDescHTML(descIsHTML);
			}
		}
		
		try {
			// Store whether the binder's description should be
			// expanded in a description tool panel.
			StringRpcResponseData regionStateRpcData = GwtViewHelper.getBinderRegionState(bs, request, binder.getId(), "descriptionRegion");
			String regionState = regionStateRpcData.getStringValue();
			boolean expanded = (MiscUtil.hasString(regionState) ? regionState.equals("expanded") : true);
			reply.setBinderDescExpanded(expanded);
		}
		catch (GwtTeamingException gte) {
			// Ignore.  The exception has already been logged by
			// GwtViewHelper.getBinderRegionState().
		}
		
		return reply;
	}
	
	/**
	 * Return a BinderStats object that holds all of the statistical
	 * information for the given binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static BinderStats getBinderStats(AllModulesInjected bs, String binderId) throws GwtTeamingException {
		BinderStats binderStats = new BinderStats();
		
		// Get the statistics for the tasks that may be in the given
		// binder.
		Long binderIdL = Long.valueOf(binderId);
		TaskStats taskStats = GwtTaskHelper.getTaskStatistics(bs, binderIdL);
		binderStats.setTaskStats(taskStats);
		
		// Get the statistics for the milestones that may be in the
		// given binder.
		MilestoneStats milestoneStats = GwtStatisticsHelper.getMilestoneStatistics(bs, binderIdL);
		binderStats.setMilestoneStats(milestoneStats);
		
		return binderStats; 
	}
	
	/**
	 * Return a list of tags associated with the given binder.
	 * 
	 * @param bs
	 * @param binder
	 * @param includeCommunity
	 * @param includePersonal
	 * 
	 * @return
	 */
	public static ArrayList<TagInfo> getBinderTags(AllModulesInjected bs, Binder binder, boolean includeCommunity, boolean includePersonal) {
		// Allocate an ArrayList to return the TagInfo's in.
		ArrayList<TagInfo> reply = new ArrayList<TagInfo>();

		// Are there any Tags defined on the Binder?
		Map<String, SortedSet<Tag>> tagsMap = TagUtil.uniqueTags(bs.getBinderModule().getTags(binder));
		if (null != tagsMap) {
			// Yes!  Extract the community and personal tags, as
			// requested.
			Set<Tag> communityTagsSet = (includeCommunity ? tagsMap.get(ObjectKeys.COMMUNITY_ENTITY_TAGS) : null);
			Set<Tag> personalTagsSet  = (includePersonal  ? tagsMap.get(ObjectKeys.PERSONAL_ENTITY_TAGS ) : null);

			// If we have any community tags...
			if (MiscUtil.hasItems(communityTagsSet)) {
				// ...iterate through them...
				for (Tag tag:  communityTagsSet) {
					// ...adding each to the reply list.
					reply.add(buildTIFromTag(TagType.COMMUNITY, tag));
				}
			}
			
			// If we have any personal tags...
			if (MiscUtil.hasItems(personalTagsSet)) {
				// ...iterate through them...
				for (Tag tag:  personalTagsSet) {
					// ...adding each to the reply list.
					reply.add(buildTIFromTag(TagType.PERSONAL, tag));
				}
			}
		}

		// ...and finally, return the List<TagInfo> of the tags defined
		// ...on the Binder.
		return reply;
	}
	
	public static ArrayList<TagInfo> getBinderTags(AllModulesInjected bs, Binder binder) {
		// Always use the previous form of the method.
		return getBinderTags(bs, binder, true, true);	// true, true -> Include both community and personal tags.
	}
	
	public static ArrayList<TagInfo> getBinderTags(AllModulesInjected bs, Long binderId) {
		// Always use the previous form of the method.
		Binder binder = bs.getBinderModule().getBinder(binderId);
		return getBinderTags(bs, binder);
	}

	public static ArrayList<TagInfo> getBinderTags(AllModulesInjected bs, String binderId) {
		// Always use the previous form of the method.
		Long binderIdL = Long.parseLong(binderId);
		return getBinderTags(bs, binderIdL);
	}

	/**
	 * Returns a BinderType describing a binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static BinderType getBinderType(AllModulesInjected bs, String binderId) {
		return getBinderType(bs.getBinderModule().getBinder(Long.parseLong(binderId)));
	}
	
	public static BinderType getBinderType(Binder binder) {
		BinderType reply;
		if      (binder instanceof Workspace) reply = BinderType.WORKSPACE;
		else if (binder instanceof Folder)    reply = BinderType.FOLDER;
		else                                  reply = BinderType.OTHER;

		if (BinderType.OTHER == reply) {
			GwtLogHelper.debug(m_logger, "GwtServerHelper.getBinderType( 'Could not determine binder type' ):  " + binder.getPathName());
		}
		return reply;
	}

	/**
	 * Returns a ClipboardUsersRpcResponseData object containing the
	 * user's on a team.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ClipboardUsersRpcResponseData getClipboardTeamUsers(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			// Read the IDs of the team members...
			Set<Long> teamMemberIds = getTeamMemberIds(bs, binderId, true);
			
			// ...create a ClipboardUsersRpcResponseData object using them...
			SortedSet<User> cbUsers = bs.getProfileModule().getUsersFromPrincipals(teamMemberIds);
			ClipboardUsersRpcResponseData reply = new ClipboardUsersRpcResponseData();
			for (User cbUser:  cbUsers) {
				reply.addUser(cbUser.getId(), cbUser.getTitle());
			}
			
			// ...and return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}
	
	/**
	 * Returns a ClipboardUsersRpcResponseData object containing the
	 * user's currently on the clipboard.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ClipboardUsersRpcResponseData getClipboardUsers(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Read the IDs of the users off the clipboard...
			Clipboard clipboard = new Clipboard(request);
			Set cbUserIds = clipboard.get(Clipboard.USERS);

			// ...create a ClipboardUsersRpcResponseData object using them...
			SortedSet<User> cbUsers = bs.getProfileModule().getUsersFromPrincipals(cbUserIds);
			ClipboardUsersRpcResponseData reply = new ClipboardUsersRpcResponseData();
			for (User cbUser:  cbUsers) {
				reply.addUser(cbUser.getId(), cbUser.getTitle());
			}
			
			// ...and return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}
	
	/**
	 * Returns a ClipboardUsersRpcResponseData object containing the
	 * user's referenced by a binder clipboard.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ClipboardUsersRpcResponseData getClipboardUsersFromList(AllModulesInjected bs, HttpServletRequest request, Long binderId, List<Long> userIds) throws GwtTeamingException {
		try {
			// If we were given a null list of user IDs...
			if (null == userIds) {
				// ...substitute an empty list.
				userIds = new ArrayList<Long>();
			}
			
			// Create a ClipboardUsersRpcResponseData object using the
			// user ID list provided...
			SortedSet<User> cbUsers = bs.getProfileModule().getUsersFromPrincipals(userIds);
			ClipboardUsersRpcResponseData reply = new ClipboardUsersRpcResponseData();
			for (User cbUser:  cbUsers) {
				reply.addUser(cbUser.getId(), cbUser.getTitle());
			}
			
			// ...and return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}

	/**
	 * Return the URL needed to display the given collection point
	 * 
	 * @param request
	 * @param userWS
	 * @param ct
	 * 
	 * @return
	 */
	public static String getCollectionPointUrl(HttpServletRequest request, Workspace userWS, CollectionType ct) {
		String url = PermaLinkUtil.getPermalink(request, userWS);
		url = GwtUIHelper.appendUrlParam(url, WebKeys.URL_SHOW_COLLECTION, String.valueOf(ct.ordinal()));
		return url;
	}

	/**
	 * Returns the data about all the collection points.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CollectionPointData getCollectionPointData(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		CollectionPointData results = new CollectionPointData();
		User user = getCurrentUser();
		Long userWSId = user.getWorkspaceId();
		try {
			CollectionType collectionType;
			String url;
			
			Workspace userWS = bs.getWorkspaceModule().getWorkspace(userWSId);

			collectionType = CollectionType.MY_FILES;
			url = getCollectionPointUrl(request, userWS, collectionType);
			results.setUrl(collectionType, url);

			collectionType = CollectionType.NET_FOLDERS;
			url = getCollectionPointUrl(request, userWS, collectionType);
			results.setUrl(collectionType, url);

			collectionType = CollectionType.SHARED_BY_ME;
			url = getCollectionPointUrl(request, userWS, collectionType);
			results.setUrl(collectionType, url);

			collectionType = CollectionType.SHARED_WITH_ME;
			url = getCollectionPointUrl(request, userWS, collectionType);
			results.setUrl(collectionType, url);

			collectionType = CollectionType.SHARED_PUBLIC;
			url = getCollectionPointUrl(request, userWS, collectionType);
			results.setUrl(collectionType, url);
		}
		
		catch (Exception e) {
			// If this is the guest user...
			if (user.isShared()) {
				// ...simply ignore the error and bail.
				return null;
			}
			
			// For all other users, convert this to a
			// GwtTeamingExcepton and throw that.
			throw GwtLogHelper.getGwtClientException(m_logger, e );
		}
		
		return results;
	}
	
	/*
	 */
	private static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
	/**
	 * Returns the User object of the currently logged in user.
	 * 
	 * @return
	 */
	public static User getCurrentUser() {
		return RequestContextHolder.getRequestContext().getUser();
	}

	/**
	 * Returns the ID of User object of the currently logged in user.
	 * 
	 * @return
	 */
	public static Long getCurrentUserId() {
		return getCurrentUser().getId();
	}

	/**
	 * Returns the current HttpSession, if accessible.
	 * 
	 * @return
	 */
	public static HttpSession getCurrentHttpSession() {
		HttpSession reply = null;
		RequestContext rc = RequestContextHolder.getRequestContext();
		SessionContext sc = rc.getSessionContext();
		if (sc instanceof HttpSessionContext) {
			reply = ((HttpSessionContext) sc).getHttpSession();
		}
		return reply;
	}

	/**
	 * Returns the default CollectionType for the given user.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static CollectionType getDefaultCollectionType(User user) {
		CollectionType reply =
			((user.isShared() && Utils.checkIfFilr()) ?
				CollectionType.SHARED_PUBLIC          :
				CollectionType.SHARED_WITH_ME);
		return reply;
	}
	
	public static CollectionType getDefaultCollectionType() {
		// Always use the initial form of the method.
		return getDefaultCollectionType(GwtServerHelper.getCurrentUser());
	}
	
	/**
	 * Return the groups LDAP query.
	 * 
	 * @param bs
	 * @param groupId
	 * 
	 * @return
	 */
	public static GwtDynamicGroupMembershipCriteria getDynamicMembershipCriteria(AllModulesInjected bs, Long groupId) {
		GwtDynamicGroupMembershipCriteria membershipCriteria = new GwtDynamicGroupMembershipCriteria();
		Principal principal = bs.getProfileModule().getEntry(groupId);
		if ((null != principal) && (principal instanceof Group)) {
			// Get the XML that defines the membership criteria.
			Group group = ((Group) principal);
			String ldapQueryXml = group.getLdapQuery();

			if (MiscUtil.hasString(ldapQueryXml)) {
				try {
					// Parse the XML string into an XML document.
	    			Document doc = XmlUtil.parseText(ldapQueryXml);
	    			
	    			// Get the root element.
	    			Node node = doc.getRootElement();
	    			
	    			// Get the "updateMembershipDuringLdapSync" attribute value.
	    			String value;
	    			Node attrNode = node.selectSingleNode("@updateMembershipDuringLdapSync");
	    			if (attrNode != null) {
	        			value = attrNode.getText();
	        			if ((value != null) && value.equalsIgnoreCase("true"))
	        			     membershipCriteria.setUpdateDuringLdapSync(true );
	        			else membershipCriteria.setUpdateDuringLdapSync(false);
	    			}
	    			
	    			// Get the <search ...> element.
	    			Node searchNode = node.selectSingleNode("search");
	    			if (searchNode != null) {
	    				// Get the 'searchSubtree' attribute.
	    				attrNode = searchNode.selectSingleNode("@searchSubtree");
	    				if (attrNode != null) {
	    					value = attrNode.getText();
	    					if ((value != null) && value.equalsIgnoreCase("true"))
	    					     membershipCriteria.setSearchSubtree(true );
	    					else membershipCriteria.setSearchSubtree(false);
	    				}
	    				
	    				// Get the <baseDn> element.
    					Node baseDnNode = searchNode.selectSingleNode("baseDn");
	    				if (baseDnNode != null) {
	    					value = baseDnNode.getText();
	    					membershipCriteria.setBaseDn(value);
	    				}
	    				
	    				// Get the <filter> element.
    					Node filterNode = searchNode.selectSingleNode("filter");
	    				if (filterNode != null) {
	    					value = filterNode.getText();
	    					membershipCriteria.setLdapFilter(value);
	    				}
	    			}
	    		}
				
	    		catch(Exception e) {
	    			GwtLogHelper.warn(m_logger, "Unable to parse dynamic group membership criteria" + ldapQueryXml, e);
	    		}
			}
		}

		return membershipCriteria;
	}
	
	/**
	 * Returns a user's EmailAddressInfo from the data in an entry map.
	 * 
	 * @param bs
	 * @param entryMap
	 * 
	 * @return
	 */
	public static EmailAddressInfo getEmailAddressInfoFromEntryMap(AllModulesInjected bs, Map entryMap) {
		User user = ((User) getValueFromEntryMap(entryMap, Constants.PRINCIPAL_FIELD));
		return getEmailAddressInfoFromUser(user);
	}
	
	/**
	 * Returns a user's EmailAddressInfo.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static EmailAddressInfo getEmailAddressInfoFromUser(User user) {
		String    userEMA       = ((null == user) ? null : user.getEmailAddress());
		Workspace userWS        = getUserWorkspace(user);
		boolean   userHasWS     = (null != userWS); 
		boolean   userWSInTrash = (userHasWS && userWS.isPreDeleted());
		return new EmailAddressInfo(userEMA, userHasWS, userWSInTrash);
	}
	
	/**
	 * Returns an EntityIdRpcResponseData containing the EntityId based
	 * on the given parameters.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param entityId
	 * @param eidType
	 * @param mobileDeviceId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static EntityIdRpcResponseData getEntityId(AllModulesInjected bs, HttpServletRequest request, Long binderId, Long entityId, EntityIdType eidType, String mobileDeviceId) throws GwtTeamingException {
		try {
			EntityId eid = new EntityId(binderId, entityId, eidType, mobileDeviceId);
			return new EntityIdRpcResponseData(eid);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtServerHelper.getEntityId( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Returns an EntityIdListRpcResponseData containing the EntityId's
	 * from the given List<GetEntityIdCmd>.
	 * 
	 * @param bs
	 * @param request
	 * @param eidCmdList
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static EntityIdListRpcResponseData getEntityIdList(AllModulesInjected bs, HttpServletRequest request, List<GetEntityIdCmd> eidCmdList) throws GwtTeamingException {
		try {
			EntityIdListRpcResponseData reply = new EntityIdListRpcResponseData();
			if (null != eidCmdList) {
				for (GetEntityIdCmd eidCmd:  eidCmdList) {
					EntityIdRpcResponseData eid = getEntityId(bs, request, eidCmd.getBinderId(), eidCmd.getEntityId(), eidCmd.getEntityIdType(), eidCmd.getMobileDeviceId());
					reply.addEntityId(eid.getEntityId());
				}
			}
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtServerHelper.getEntityIdList( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Returns the string to use as the title of a folder entry.
	 * 
	 * @param fe
	 * 
	 * @return
	 */
	public static String getFolderEntryTitle(FolderEntry fe) {
		String reply = fe.getTitle();
		if (!(MiscUtil.hasString(reply))) {
			FolderEntry feParent = fe.getParentEntry();
			if (null == feParent)
			     reply = ("--" + NLT.get("entry.noTitle") + "--");
			else reply = NLT.get("reply.re.title", new String[]{getFolderEntryTitle(feParent)});
		}
		return reply;
	}

	/**
	 * Returns a FolderFiltersRpcResponseData containing information
	 * about the filters defined on the given folder.
	 * 
	 * @param bs
	 * @param request
	 * @param folder
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static FolderFiltersRpcResponseData getFolderFilters(AllModulesInjected bs, HttpServletRequest request, Folder folder) throws GwtTeamingException {
		try {
			// Allocate a FolderFiltersRpcResponseData to return the
			// folder's filters in.
			FolderFiltersRpcResponseData reply = new FolderFiltersRpcResponseData();

			// Are there any global filters defined on the folder?
			Map<String, String> searchFilters = ((Map<String, String>) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS));
			if (MiscUtil.hasItems(searchFilters)) {
				// Yes!  Scan them and add a FolderFilter for each to
				// the reply.
				for (Entry<String, String> mapEntry:  searchFilters.entrySet()) {
					FolderFilter globalFilter = new FolderFilter(mapEntry.getKey(), mapEntry.getValue());
					reply.addGlobalFilter(globalFilter);
				}
			}
			
			// Are there any personal filters defined in the user's
			// properties for this folder?
			UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUserId(), folder.getId());
			searchFilters = ((Map<String, String>) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS));
			if (MiscUtil.hasItems(searchFilters)) {
				// Yes!  Scan them and add a FolderFilter for each to
				// the reply.
				for (Entry<String, String> mapEntry:  searchFilters.entrySet()) {
					FolderFilter personalFilter = new FolderFilter(mapEntry.getKey(), mapEntry.getValue());
					reply.addPersonalFilter(personalFilter);
				}
			}

			// If we get here, reply refers to a
			// FolderFilterRpcResponse data containing the filters
			// defined on the given folder.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtServerHelper.getFolderFiltersImpl( SOURCE EXCEPTION ):  ");
		}
	}
	
	public static FolderFiltersRpcResponseData getFolderFilters(AllModulesInjected bs, HttpServletRequest request, Long folderId) throws GwtTeamingException {
		try {
			// Access the folder and read its filters, using the
			// initial form of the method.
			Folder folder = bs.getFolderModule().getFolder(folderId);
			return getFolderFilters(bs, request, folder);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtServerHelper.getFolderFilters( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * For the given role, find the corresponding function ID.
	 * 
	 * @param bs
	 * @param role
	 * 
	 * @return
	 */
	public static Long getFunctionIdFromRole(AllModulesInjected bs, GwtRole role) {
		if (null == role) {
			GwtLogHelper.error(m_logger, "In GwtNetFolderHelper.getFunctionIdFromRole(), invalid parameter");
			return null;
		}
		
		// Did we find the function's internal ID?
		String fnInternalId = getFunctionInternalIdFromRole(bs, role);
		if (null == fnInternalId) {
			// No
			GwtLogHelper.error(m_logger, "In GwtServerHelper.getFunctionIdFromRole(), could not find internal function id for role: " + role.getType());
			return null;
		}

		// Get a list of all the functions.
		List<Function> listOfFunctions = bs.getAdminModule().getFunctions();
		
		// For the given internal function id, get the function's real id.
		Long fnId = null;
		for (Function nextFunction:  listOfFunctions) {
			String nextInternalId = nextFunction.getInternalId();
			if (fnInternalId.equalsIgnoreCase(nextInternalId)) {
				fnId = nextFunction.getId();
				break;
			}
		}
		
		return fnId;
	}

	/**
	 * For the given role, find the corresponding function internal ID.
	 * 
	 * @param bs
	 * @param role
	 * 
	 * @return
	 */
	public static String getFunctionInternalIdFromRole(AllModulesInjected bs, GwtRole role) {
		if (null == role) {
			GwtLogHelper.error(m_logger, "In GwtNetFolderHelper.getFunctionInternalIdFromRole(), invalid parameter");
			return null;
		}
		
		// Get the internal id of the appropriate function
		String fnInternalId = null;
		switch (role.getType()) {
		case ShareExternal:               fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID;      break;
		case ShareFolderExternal:		  fnInternalId = ObjectKeys.FUNCTION_ALLOW_FOLDER_SHARING_EXTERNAL_INTERNALID; break;
		case ShareForward:                fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID;       break;
		case ShareFolderForward:		  fnInternalId = ObjectKeys.FUNCTION_ALLOW_FOLDER_SHARING_FORWARD_INTERNALID; break;
		case ShareInternal:               fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID;      break;
		case ShareFolderInternal:		  fnInternalId = ObjectKeys.FUNCTION_ALLOW_FOLDER_SHARING_INTERNAL_INTERNALID; break;
		case SharePublic:                 fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID;        break;
		case ShareFolderPublic:			  fnInternalId = ObjectKeys.FUNCTION_ALLOW_FOLDER_SHARING_PUBLIC_INTERNALID; break;
		case SharePublicLinks:            fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_LINKS_INTERNALID;  break;
		case AllowAccess:                 fnInternalId = ObjectKeys.FUNCTION_ALLOW_ACCESS_NET_FOLDER_INTERNALID;     break;
		case EnableShareExternal:         fnInternalId = ObjectKeys.FUNCTION_ENABLE_EXTERNAL_SHARING_INTERNALID;     break;
		case EnableShareForward:          fnInternalId = ObjectKeys.FUNCTION_ENABLE_FORWARD_SHARING_INTERNALID;      break;
		case EnableShareInternal:         fnInternalId = ObjectKeys.FUNCTION_ENABLE_INTERNAL_SHARING_INTERNALID;     break;
		case EnableSharePublic:           fnInternalId = ObjectKeys.FUNCTION_ENABLE_PUBLIC_SHARING_INTERNALID;       break;
		case EnableShareWithAllExternal:  fnInternalId = ObjectKeys.FUNCTION_ENABLE_SHARING_ALL_EXTERNAL_INTERNALID; break;
		case EnableShareWithAllInternal:  fnInternalId = ObjectKeys.FUNCTION_ENABLE_SHARING_ALL_INTERNAL_INTERNALID; break;
		case EnableShareLink:             fnInternalId = ObjectKeys.FUNCTION_ENABLE_LINK_SHARING_INTERNALID;         break;
		}
		
		// Did we find the function's internal ID?
		if (null == fnInternalId) {
			// No!
			GwtLogHelper.error(m_logger, "In GwtServerHelper.getFunctionInternalIdFromRole(), could not find internal function id for role: " + role.getType());
		}

		return fnInternalId;
	}

	/**
	 * Returns a string that can be used as an binder's title in an
	 * error message.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 */
	public static String getBinderTitle(AllModulesInjected bs, Long binderId) {
		String reply;
		try {
			Binder binder = bs.getBinderModule().getBinder(binderId);
			reply = binder.getTitle();
		}
		catch (Exception e) {
			reply = String.valueOf(binderId);
		}
		return reply;
	}
	
	/**
	 * Returns a string that can be used as an entry's title in an
	 * error message.
	 * 
	 * @param bs
	 * @param folderId
	 * @param entryId
	 * 
	 * @return
	 */
	public static String getEntryTitle(AllModulesInjected bs, Long folderId, Long entryId) {
		String reply;
		try {
			FolderEntry fe = bs.getFolderModule().getEntry(folderId, entryId);
			reply = fe.getTitle();
		}
		catch (Exception e) {
			reply = String.valueOf(entryId);
		}
		return reply;
	}

	/**
	 * Return the 'entity permalink' URL.
	 * 
	 * @param bs
	 * @param req
	 * @param entityId
	 * 
	 * @return
	 */
	public static String getEntityPermalink(AllModulesInjected bs, HttpServletRequest req, EntityId entityId) {
		String reply = "";
		if (null != entityId) {
			DefinableEntity de;
			if (entityId.isBinder())
			     de = bs.getBinderModule().getBinder(                       entityId.getEntityId());
			else de = bs.getFolderModule().getEntry(entityId.getBinderId(), entityId.getEntityId());
			
			if (null != de) {
				reply = PermaLinkUtil.getPermalink(req, de);
			}
		}
		return reply;
	}
	
	/**
	 * Returns a string that can be used as an entity's title in an
	 * error message.
	 * 
	 * @param bs
	 * @param entityId
	 * 
	 * @return
	 */
	public static String getEntityTitle(AllModulesInjected bs, EntityId entityId) {
		String reply;
		if (entityId.isBinder())
		     reply = getBinderTitle(bs,                         entityId.getEntityId());
		else reply = getEntryTitle( bs, entityId.getBinderId(), entityId.getEntityId());
		return reply;
	}

	/**
	 * Return the URL for the given binder and file.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param fileName
	 * 
	 * @return
	 */
	public static String getFileUrl(AllModulesInjected bs, HttpServletRequest request, String binderId, String fileName) {
		String url = null;
		try {
			Binder binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			String webPath = WebUrlUtil.getServletRootURL(request);
			url = WebUrlUtil.getFileUrl(webPath, WebKeys.ACTION_READ_FILE, binder, fileName);
		}
		catch (NoFolderEntryByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException        acEx) {/* Ignore. */}
		catch (Exception                     e   ) {/* Ignore. */}
		
		return url;
	}
	
	
	/**
	 * Return a list of tags associated with the given entry.
	 * 
	 * @param bs
	 * @param entry
	 * @param includeCommunity
	 * @param includePersonal
	 * 
	 * @return
	 */
	public static ArrayList<TagInfo> getEntryTags(AllModulesInjected bs, FolderEntry entry, boolean includeCommunity, boolean includePersonal) {
		// Allocate an ArrayList to return the TagInfo's in.
		ArrayList<TagInfo> reply = new ArrayList<TagInfo>();
		
		// Are there any Tags defined on the entry?
		Map<String, SortedSet<Tag>>	tagsMap = TagUtil.uniqueTags(bs.getFolderModule().getTags(entry));
		if (null != tagsMap) {
			// Yes!  Extract the community and personal tags, as
			// requested.
			Set<Tag> communityTagsSet = (includeCommunity ? tagsMap.get(ObjectKeys.COMMUNITY_ENTITY_TAGS) : null);
			Set<Tag> personalTagsSet  = (includePersonal  ? tagsMap.get(ObjectKeys.PERSONAL_ENTITY_TAGS ) : null);

			// If we have any community tags...
			if (MiscUtil.hasItems(communityTagsSet)) {
				// ...iterate through them...
				for (Tag tag:  communityTagsSet) {
					// ...adding each to the reply list.
					reply.add(buildTIFromTag(TagType.COMMUNITY, tag));
				}
			}

			// If we have any personal tags...
			if (MiscUtil.hasItems(personalTagsSet)) {
				// ...iterate through them...
				for (Tag tag:  personalTagsSet) {
					// ...adding each to the reply list...
					reply.add(buildTIFromTag(TagType.PERSONAL, tag));
				}
			}
		}

		// ...and finally, return the List<TagInfo> of the tags defined
		// ...on the entry.
		return reply;
	}
	
	public static ArrayList<TagInfo> getEntryTags(AllModulesInjected bs, FolderEntry entry) {
		// Always use the previous form of the method.
		return getEntryTags(bs, entry, true, true);	// true, true -> Include both community and personal tags.
	}
	
	public static ArrayList<TagInfo> getEntryTags(AllModulesInjected bs, Long entryId) {
		// Always use the previous form of the method.
		FolderEntry entry = bs.getFolderModule().getEntry(null, entryId);
		return getEntryTags(bs, entry);
	}
	
	public static ArrayList<TagInfo> getEntryTags(AllModulesInjected bs, String entryId) {
		// Always use the previous form of the method.
		Long entryIdL = Long.parseLong(entryId);
		return getEntryTags(bs, entryIdL);
	}
	
	
	/**
	 * Return the URL needed to execute the given JSP.
	 * 
	 * @param request
	 * @param binderId
	 * @param jspName
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static String getExecuteJspUrl(HttpServletRequest request, String binderId, String jspName) throws GwtTeamingException {
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION,        WebKeys.ACTION_EXECUTE_JSP);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId                  );
		adapterUrl.setParameter(WebKeys.JSP_NAME,      jspName                   );
		return adapterUrl.toString();
	}
	
	/*
	 * Returns a cloned copy of the expanded Binder's list from the
	 * UserProperties.
	 */
	private static List<Long> getExpandedBindersList(AllModulesInjected bs) {
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		List<Long> reply= ((List<Long>) userProperties.getProperty(ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST));
		return reply;
	}

	/**
	 * Returns a formatted date/time string for the current user's
	 * locale and time zone.
	 * 
	 * @param date
	 * @param dateStyle
	 * @param timeStyle
	 * @param tz
	 * 
	 * @return
	 */
	public static String getDateTimeString(Date date, int dateStyle, int timeStyle, TimeZone tz) {
		DateFormat df = DateFormat.getDateTimeInstance(dateStyle, timeStyle, getCurrentUser().getLocale());
		df.setTimeZone(tz);
		return df.format(date);
	}
	
	public static String getDateTimeString(Date date, int dateStyle, int timeStyle) {
		// Always use the initial form of the method.
		return getDateTimeString(date, dateStyle, timeStyle, getCurrentUser().getTimeZone());
	}
	
	public static String getDateTimeString(Date date) {
		// Always use the initial form of the method.
		return getDateTimeString(date, DateFormat.MEDIUM, DateFormat.LONG, getCurrentUser().getTimeZone());
	}
	
	/**
	 * Returns a formatted date string for the current user's locale
	 * and time zone.
	 * 
	 * @param date
	 * @param dateStyle
	 * @param tz
	 * 
	 * @return
	 */
	public static String getDateString(Date date, int dateStyle, TimeZone tz) {
		DateFormat df = DateFormat.getDateInstance(dateStyle, getCurrentUser().getLocale());
		df.setTimeZone(tz);
		return df.format(date);
	}
	
	public static String getDateString(Date date, int dateStyle) {
		// Always use the initial form of the method.
		return getDateString(date, dateStyle, getCurrentUser().getTimeZone());
	}
	
	public static String getDateString(Date date) {
		// Always use the initial form of the method.
		return getDateString(date, DateFormat.MEDIUM, getCurrentUser().getTimeZone());
	}
	
	/**
	 * Returns the ID of the default view definition of a folder.
	 * 
	 * @param bs
	 * @param binderIdS
	 * 
	 * @return
	 */
	public static String getDefaultFolderDefinitionId(AllModulesInjected bs, String binderIdS) {
		// Read the folder's default definition...
		Definition def = BinderHelper.getFolderDefinitionFromView(bs, Long.parseLong(binderIdS));

		// ...and return it's ID or if we can't find it, return an
		// ...empty string.
		String reply = ((null == def) ? "" : def.getId());
		GwtLogHelper.debug(m_logger, "GwtServerHelper.getDefaultFolderDefinitionId( binderId:  '" + binderIdS + "' ):  '" + reply);
		return reply;
	}
	
	/**
	 * Returns information about the current user's favorites.
	 * 
	 * @return
	 */
	public static List<FavoriteInfo> getFavorites(AllModulesInjected bs) {
		// Allocate an ArrayList<FavoriteInfo> to hold the favorites.
		ArrayList<FavoriteInfo> reply = new ArrayList<FavoriteInfo>();

		// Read the user's favorites.
		UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
		Object userFavorites = userProperties.getProperty(ObjectKeys.USER_PROPERTY_FAVORITES);
		Favorites favorites;
		if (userFavorites instanceof Document) {
			favorites = new Favorites((Document)userFavorites);
			bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, favorites.toString());
		} else {		
			favorites = new Favorites((String)userFavorites);
		}
		
		// Scan the user's favorites...
		List favoritesList = favorites.getFavoritesList();
		for (Iterator flIT = favoritesList.iterator(); flIT.hasNext(); ) {
			// ...constructing a FavoriteInfor object for each.
			FavoriteInfo fi = fiFromJSON(bs, ((JSONObject) flIT.next()));
			if (null != fi) {
				reply.add(fi);
			}
		}

		// If we get here, reply refers to an ArrayList<FavoriteInfo>
		// of the user's defined favorites.  Return it.
		return reply;
	}

	/**
	 * Returns a file entry's FileAttachment or null if the entry isn't
	 * a file entry or a FileAttachment with a name can't be found.
	 * 
	 * @param bs
	 * @param fileEntry
	 * @param validateAsFileEntry
	 */
	public static FileAttachment getFileEntrysFileAttachment(AllModulesInjected bs, FolderEntry fileEntry, boolean validateAsFileEntry) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.getFileEntrysFileAttachment()");
		try {
			// Is the FolderEntry a file entry?
			FileAttachment reply = null;
			if ((!validateAsFileEntry) || isFamilyFile(getFolderEntityFamily(bs, fileEntry))) {
				// Yes!  Can we find its primary file attachment?
				reply = MiscUtil.getPrimaryFileAttachment(fileEntry);
			}
			
			// If we get here, reply refers to the to the file entry's
			// FileAttachment or null if the entry isn't a file entry or an
			// attachment can't be found.  Return it.
	        return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	public static FileAttachment getFileEntrysFileAttachment(AllModulesInjected bs, FolderEntry fileEntry) {
		// Always use the initial form of the method.
		return getFileEntrysFileAttachment(bs, fileEntry, true);
	}
	
	/**
	 * Returns a file entry's FileAttachment or null if the entry isn't
	 * a file entry or a FileAttachment with a name can't be found.
	 * 
	 * @param bs
	 * @param eid
	 */
	public static FileAttachment getFileEntrysFileAttachment(AllModulesInjected bs, EntityId eid) {
		FileAttachment reply = null;
		if (eid.isEntry()) {
			FolderEntry fe;
			try                 {fe = bs.getFolderModule().getEntry(eid.getBinderId(), eid.getEntityId());}
			catch (Exception e) {fe = null;                                                               }
			if (null != fe) {
				// Always use the initial form of the method.
				reply = getFileEntrysFileAttachment(bs, fe, true);
			}
		}
		return reply;
	}
	
	/**
	 * Returns a file entry's filename or null if the entry isn't a
	 * file entry or a name can't be determined.
	 * 
	 * @param bs
	 * @param fileEntry
	 * @param validateAsFileEntry
	 */
	public static String getFileEntrysFilename(AllModulesInjected bs, FolderEntry fileEntry, boolean validateAsFileEntry) {
		FileAttachment fa = getFileEntrysFileAttachment(bs, fileEntry, validateAsFileEntry);
		return ((null == fa) ? null : fa.getFileItem().getName());
	}
	
	public static String getFileEntrysFilename(AllModulesInjected bs, FolderEntry fileEntry) {
		// Always use the initial form of the method.
		return getFileEntrysFilename(bs, fileEntry, true);
	}

	/**
	 * Determines the family of a FolderEntry or Folder entity and
	 * returns it.  If the family can't be determined, null is
	 * returned.
	 * 
	 * @param bs
	 * @param entity
	 */
	public static String getFolderEntityFamily(AllModulesInjected bs, DefinableEntity entity) {
		// Is the entity a folder entry?
		Definition entityDef = null;
		if (entity instanceof FolderEntry) {
			// Yes!  Does it have a definition ID?
			FolderEntry fe = ((FolderEntry) entity);
			String defId = fe.getEntryDefId();
			if (MiscUtil.hasString(defId)) {
				// Yes!  Use that to get its definition.
				entityDef = DefinitionHelper.getDefinition(defId);
			}
		}

		// No, the entity is not a folder entry!  Is it a folder?
		else if (entity instanceof Folder) {
			// Yes!  Can we get its definition?
			Folder folder = ((Folder) entity);
			entityDef = BinderHelper.getFolderDefinitionFromView(bs, folder);
			if (null == entityDef) {
				// No!  Use the default from the folder.
				entityDef = folder.getDefaultViewDef();
			}
		}
		
		// If we have a definition for the entity...
		if (null != entityDef) {
			// ...and we can determine the family from it...
			String family = BinderHelper.getFamilyNameFromDef(entityDef);
			if (MiscUtil.hasString(family)) {
				// ...return it.
				return family;
			}
		}
		
		// If we get here, we couldn't determine the family of the
		// entity.  Return null.
		return null;
	}
	
	/**
	 * ?
	 *
	 * @param bs
	 * @param request
	 * @param zoneUUID
	 * @param folderId
	 * @param folderTitle
	 * @param extendedTitle
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GwtFolder getFolderImpl(AllModulesInjected bs, HttpServletRequest request, String zoneUUID, String folderId, String folderTitle, boolean extendedTitle) throws GwtTeamingException {
		GwtFolder folder = new GwtFolder();
		
		try {
			// Get the ID of the zone we are running in.
			ZoneInfo	zoneInfo   = MiscUtil.getCurrentZone();
			String		zoneInfoId = zoneInfo.getId();
			if (null == zoneInfoId) {
				zoneInfoId = "";
			}

			BinderModule	binderModule = bs.getBinderModule();
			Long			folderIdL    = new Long(folderId);
			
			// Are we looking for a folder that was imported from
			// another zone?
			if ((null != zoneUUID) && (0 < zoneUUID.length()) && (!(zoneInfoId.equals(zoneUUID)))) {
				// Yes!  Get the folder ID for the folder in this zone.
				folderIdL = binderModule.getZoneBinderId(folderIdL, zoneUUID, EntityType.folder.name());
			}

			// Get the binder object.
			Binder binder;
			if (null != folderIdL)
			     binder = binderModule.getBinder(folderIdL);
			else binder = null;
			
			// Initialize the data members of the GwtFolder object.
			folder = new GwtFolder();
			if (null != folderIdL) {
				folder.setFolderId(folderIdL.toString());
			}
			if (null != binder) {
				String title;
				if (MiscUtil.hasString(folderTitle)) {
					title = folderTitle;
				}
				else {
					title = binder.getTitle();
					if (extendedTitle) {
	                	if (!binder.isRoot() && !binder.getParentBinder().getEntityType().equals(EntityType.profiles)) {
	                		title += (" (" + binder.getParentBinder().getTitle() + ")");
	                	} 
					}
				}
				folder.setFolderName(title);
				Binder parentBinder = binder.getParentBinder();
				if (null != parentBinder) {
					folder.setParentBinderName(parentBinder.getPathName());
				}

				// Create a URL that can be used to view this folder.
				String url = PermaLinkUtil.getPermalink(request, binder);
				folder.setViewFolderUrl(url);
				
				Description desc = binder.getDescription();
				if (null != desc) {
					// Perform any fixups needed on the entry's description
					String descStr = desc.getText();
					descStr = markupStringReplacementImpl(bs, request, folderId, descStr, "view");
					folder.setFolderDesc(descStr);
				}
			}
		}
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(m_logger, e);
		}
		
		return folder;
	}
	
	public static GwtFolder getFolderImpl(AllModulesInjected bs, HttpServletRequest request, String zoneUUID, String folderId, String folderTitle) throws GwtTeamingException {
		// Always use the initial form of the method.
		return getFolderImpl(bs, request, zoneUUID, folderId, folderTitle, false);
	}
	
	/**
	 * Get the folder sort settings on the specified binder.
	 * 
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static FolderSortSetting getFolderSortSetting(AllModulesInjected bs, Long binderId) throws GwtTeamingException {
		try {
			FolderSortSetting folderSortSetting = new FolderSortSetting();
			Long userId = getCurrentUserId();
			ProfileModule pm = bs.getProfileModule();
			UserProperties userFolderProperties = pm.getUserProperties(userId, binderId);
			Map properties = userFolderProperties.getProperties();
			
			if (null != properties) {
				if (properties.containsKey(ObjectKeys.SEARCH_SORT_BY)) {
					folderSortSetting.setSortKey((String) properties.get(ObjectKeys.SEARCH_SORT_BY));
				}
				
				if (properties.containsKey(ObjectKeys.SEARCH_SORT_DESCEND)) {
					String value = ((String) properties.get(ObjectKeys.SEARCH_SORT_DESCEND));
					folderSortSetting.setSortDescending(Boolean.valueOf(value).booleanValue());
				}
			}
			
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				GwtLogHelper.debug(m_logger, "GwtServerHelper.getFolderSortSetting( Retrieved folder sort for binder ):  Binder:  " + binderId.longValue() + ", Sort Key:  '" + folderSortSetting.getSortKey() + "', Sort Descending:  " + folderSortSetting.getSortDescending());
			}
			
			return folderSortSetting;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}

	/**
	 * Returns the FolderType of a folder.
	 *
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	public static FolderType getFolderType(AllModulesInjected bs, Binder binder) {
		FolderType reply;
		if (binder instanceof Folder)
			 reply = getFolderTypeFromViewDef(bs, ((Folder) binder));
		else reply = FolderType.NOT_A_FOLDER;

		// If we get here, reply refers to the type of folder the
		// binder is.  Return it.
		return reply;
	}
	
	public static FolderType getFolderType(AllModulesInjected bs, String binderId) {
		// Always use the initial form of the method.
		return getFolderType(bs, bs.getBinderModule().getBinder(Long.parseLong(binderId)));
	}

	/*
	 * Returns the FolderType of a folder based on the family from its
	 * definition.
	 */
	private static FolderType getFolderTypeFromDefFamily(Folder folder, String defFamily) {
		// Do we have the family from the folder's definition?
		FolderType reply = FolderType.OTHER;
		if (MiscUtil.hasString(defFamily)) {
			// Yes!  Classify the folder based on it.
			defFamily = defFamily.toLowerCase();
			if      (defFamily.equals("blog"))       reply = FolderType.BLOG;
			else if (defFamily.equals("calendar"))   reply = FolderType.CALENDAR;
			else if (defFamily.equals("discussion")) reply = FolderType.DISCUSSION;
			else if (defFamily.equals("file" ))      reply = FolderType.FILE;
			else if (defFamily.equals("guestbook"))  reply = FolderType.GUESTBOOK;
			else if (defFamily.equals("milestone"))  reply = FolderType.MILESTONE;
			else if (defFamily.equals("miniblog"))   reply = FolderType.MINIBLOG;
			else if (defFamily.equals("photo"))      reply = FolderType.PHOTOALBUM;
			else if (defFamily.equals("task"))       reply = FolderType.TASK;
			else if (defFamily.equals("survey"))     reply = FolderType.SURVEY;
			else if (defFamily.equals("wiki"))       reply = FolderType.WIKI;
		}

		// For certain folder types, we need to special case the
		// classification for one reason or another.  Is this one
		// of them?
		String view = BinderHelper.getBinderDefaultViewName(folder);
		switch (reply) {
		case OTHER:
			// We need to special case guest book folders
			// because its definition does not contain a family
			// name.
			if (MiscUtil.hasString(view) && view.equals(VIEW_FOLDER_GUESTBOOK)) {
				reply = FolderType.GUESTBOOK;
			}				
			else {
				GwtLogHelper.debug(m_logger, "GwtServerHelper.getFolderTypeFromDefFamily( 'Could not determine folder type' ):  " + folder.getPathName());
			}				
			break;
		
		case FILE:
			// We need to special case files because both a
			// normal file folder and a mirrored file
			// folder use 'file' for their family name.
			if (MiscUtil.hasString(view) &&
					(view.equals(VIEW_FOLDER_MIRRORED_FILE) || view.equals(VIEW_FOLDER_MIRRORED_FILR_FILE))) {
				reply = FolderType.MIRROREDFILE;
			}				
			break;
		}
		return reply;
	}
	
	/**
	 * Returns the FolderType of a folder based on its current view.
	 * 
	 * @param bs
	 * @param folder
	 */
	public static FolderType getFolderTypeFromViewDef(AllModulesInjected bs, Folder folder) {
		// Does the user have a view definition selected for this
		// folder?
		Definition def = getDefinitionfromView(bs, folder);

		// Return the FolderType from view definition.
		String defFamily = BinderHelper.getFamilyNameFromDef(def);
		return getFolderTypeFromDefFamily(folder, defFamily);
	}

	public static Definition getDefinitionfromView(AllModulesInjected bs, Folder folder) {
		// Does the user have a view definition selected for this
		// folder?
		Definition def = BinderHelper.getFolderDefinitionFromView(bs, folder);
		if (null == def) {
			// No!  Just use it's default view.
			def = folder.getDefaultViewDef();
		}
		return def;
	}

	/**
	 * Returns a count of the members of a group.
	 * 
	 * @param group
	 * 
	 * @return
	 */
	public static int getGroupCount(GroupPrincipal group) {
		Set<Long> groupMemberIds = getGroupMemberIds(group);
		return ((null == groupMemberIds) ? 0 : groupMemberIds.size());
	}

	/**
	 * Returns a Set<Long> of the IDs of the members of a group.
	 * 
	 * @param group
	 * 
	 * @return
	 */
	public static Set<Long> getGroupMemberIds(GroupPrincipal group) {
		List<Long> groupIds = new ArrayList<Long>();
		groupIds.add(group.getId());
		Set<Long> groupMemberIds = null;
		try {
			ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
			groupMemberIds = profileDao.explodeGroups(groupIds, group.getZoneId());
		}
		catch (Exception ex) {/* Ignored. */}
		return validatePrincipalIds(groupMemberIds);
	}
	
	/**
	 * Return the membership of the given group.
	 * 
	 * @param bs
	 * @param retList
	 * @param groupId
	 * @param offset
	 * @param numResults
	 * @param filter
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static int getGroupMembership(AllModulesInjected bs, ArrayList<GwtTeamingItem> retList, String groupId, int offset, int numResults, MembershipFilter filter) throws GwtTeamingException {
		if (null == retList) {
			return 0;
		}
		
		int totalNumberOfMembers = 0;
		try {
			// Get the group object.
			Long groupIdL = Long.valueOf(groupId);
			Principal group = bs.getProfileModule().getEntry(groupIdL);
			if ((null != group) && (group instanceof Group)) {
				// Get the members of the group.
				List<Principal> memberList = ((Group) group).getMembers();
				
				// Get a list of the IDs of all the members of this group.
				List<Long> membership = new ArrayList<Long>();
				Iterator<Principal> itMembers = memberList.iterator();
				while (itMembers.hasNext()) {
					Principal member = ((Principal) itMembers.next());
					membership.add(member.getId());
				}
				
				// Get all the members of the group.  We call
				// ResolveIDs.getPrincipals() because it handles users
				// the logged-in user has rights to see.
				memberList = ResolveIds.getPrincipals(membership, false);
				
				// Sort the list of users/groups.
				Collections.sort(memberList, new PrincipalComparator(true));
				totalNumberOfMembers = memberList.size();

				// For each member of the group create a GwtUser or GwtGroup object.
				for (int i = offset; i < memberList.size() && retList.size() < numResults; i += 1) {
					Principal member = ((Principal) memberList.get(i));
					if (member.isDeleted()) {
						continue;
					}
					
					if (member instanceof Group) {
						if ((filter == MembershipFilter.RETRIEVE_ALL_MEMBERS) || (filter == MembershipFilter.RETRIEVE_GROUPS_ONLY)) {
							Group nextGroup = ((Group) member);
							GwtGroup gwtGroup = new GwtGroup();
							gwtGroup.setInternal(nextGroup.getIdentityInfo().isInternal());
							gwtGroup.setId(nextGroup.getId().toString());
							gwtGroup.setName(nextGroup.getName());
							gwtGroup.setTitle(nextGroup.getTitle());
							gwtGroup.setDn(nextGroup.getForeignName());
							Description desc = nextGroup.getDescription();
							if (null != desc) {
								gwtGroup.setDesc(desc.getText());
							}
							gwtGroup.setGroupType(GwtServerHelper.getGroupType(nextGroup));
							retList.add(gwtGroup);
						}
					}
					
					else if (member instanceof User) {
						if ((filter == MembershipFilter.RETRIEVE_ALL_MEMBERS) || (filter == MembershipFilter.RETRIEVE_USERS_ONLY)) {
							User user = ((User) member);
							GwtUser gwtUser = new GwtUser();
							gwtUser.setInternal(user.getIdentityInfo().isInternal());
							gwtUser.setUserId(user.getId());
							gwtUser.setName(user.getName());
							gwtUser.setTitle(Utils.getUserTitle(user));
							gwtUser.setWorkspaceTitle(user.getWSTitle());
							gwtUser.setEmail(user.getEmailAddress());
							gwtUser.setDisabled(user.isDisabled());
							retList.add(gwtUser);
						}
					}
				}
			}
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		return totalNumberOfMembers;
	}
	
	/**
	 * Get the information about the given group's membership.  Is the
	 * membership dynamic or static.  Are external users/groups
	 * allowed.
	 * 
	 * @param bs
	 * @param groupId
	 * 
	 * @return
	 */
	public static GroupMembershipInfo getGroupMembershipInfo(AllModulesInjected bs, Long groupId) {
		GroupMembershipInfo info = new GroupMembershipInfo();
		boolean externalAllowed = isExternalMembersAllowed(bs, groupId);
		boolean isDynamic       = isGroupMembershipDynamic(bs, groupId);
		info.setMembershipInfo(isDynamic, externalAllowed);
		return info;
	}

	/*
	 * Creates an HttpClient from an HttpURL.
	 */
	private static HttpClient getHttpClient(HttpURL hrl) throws URIException {
		HttpClient client = new HttpClient();
		HostConfiguration hc = client.getHostConfiguration();
		hc.setHost(hrl);
		return client;
	}

	/*
	 * Creates an HttpURL from a URL string.
	 */
	private static HttpURL getHttpURL(String urlStr) throws URIException  {
		HttpURL reply;
		if(urlStr.startsWith("https"))
			 reply = new HttpsURL(urlStr);
		else reply = new HttpURL(urlStr);
		return reply;
	}
	
	/*
	 * Returns a List<Long> of the current user's home folder IDs.
	 */
	private static List<Long> getHomeFolderIds(AllModulesInjected bs, User user) {
		return SearchUtils.getHomeFolderIds(bs, user);
	}
	
	@SuppressWarnings("unused")
	private static List<Long> getHomeFolderIds(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return getHomeFolderIds(bs, getCurrentUser());
	}
	
	/**
	 * Returns the current user's home folder ID.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static Long getHomeFolderId(AllModulesInjected bs, User user) {
		List<Long> homeFolderIds = getHomeFolderIds(bs, user);
		Long reply;
		if ((null != homeFolderIds) && (!(homeFolderIds.isEmpty())))
		     reply = homeFolderIds.get(0);
		else reply = null;
		return reply;
	}
	
	public static Long getHomeFolderId(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return getHomeFolderId(bs, getCurrentUser());
	}
	
	/**
	 * Get the inherited landing page properties (background color,
	 * background image, etc...) for the given binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static GwtLandingPageProperties getInheritedLandingPageProperties(AllModulesInjected bs, String binderId, HttpServletRequest request) throws GwtTeamingException {
		GwtLandingPageProperties lpProperties = new GwtLandingPageProperties();
		lpProperties.setInheritProperties(true);
		
		try {
			// Get the binder's parent.
			Binder binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			Binder parentBinder = binder.getParentBinder();
			if (null != parentBinder) {
				binder = parentBinder;
			}
			
			lpProperties = getLandingPageProperties(bs, String.valueOf(binder.getId()), request);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		return lpProperties;
	}
	
	/**
	 * Return a GwtJitsZoneConfig object that holds the jits zone config data.
	 *
	 * @param bs
	 * 
	 * @return
	 */
	public static GwtNetFolderGlobalSettings getNetFolderGlobalSettings(AllModulesInjected bs) {
		ZoneModule zm = bs.getZoneModule();
		ZoneConfig zc = zm.getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
		GwtNetFolderGlobalSettings nfGlobalSettings = new GwtNetFolderGlobalSettings();
		
		// Get the whether JITS enabled.
		nfGlobalSettings.setJitsEnabled(zc.getJitsEnabled());
		
		// Get the max wait time.
		nfGlobalSettings.setMaxWaitTime(zc.getJitsWaitTimeout() / 1000);
		
		// Get the setting for 'use directory rights'.
		nfGlobalSettings.setUseDirectoryRights(zc.getUseDirectoryRights());
		
		// Get the 'cached rights refresh interval'.
		nfGlobalSettings.setCachedRightsRefreshInterval(zc.getCachedRightsRefreshInterval());
		
		return nfGlobalSettings;
	}
	
	/**
	 * Return a GwtDatabasePruneConfig object that holds the pruning
	 * config data.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static GwtDatabasePruneConfiguration getDatabasePruneConfiguration(AllModulesInjected bs) {
		ZoneConfig zoneConfig = bs.getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
		
		GwtDatabasePruneConfiguration reply = new GwtDatabasePruneConfiguration();
		reply.setAuditTrailEnabled(     zoneConfig.isAuditTrailEnabled()   );
		reply.setAuditTrailPruneAgeDays(zoneConfig.getAuditTrailKeepDays() );
		reply.setChangeLogEnabled(      zoneConfig.isChangeLogEnabled()    );
		reply.setChangeLogPruneAgeDays( zoneConfig.getChangeLogsKeepDays() );
		reply.setFileArchivingEnabled(  zoneConfig.isFileArchivingEnabled());
		
		return reply;
	}

	/**
	 * Parses a JSON data string and if valid, returns a JSONObject.
	 * Otherwise, returns null.
	 * 
	 * @param jsonData
	 * 
	 * @return
	 */
	public static JSONObject getJSOFromS(String jsonData) {
		// If we don't have any JSON data to parse...
		jsonData = ((null == jsonData) ? "" : jsonData.trim());
		if (0 == jsonData.length()) {
			// ...return null.
			return null;
		}

		// Return the parsed JSONObject or null if the parse fails.
		JSONObject reply;
		try                  {reply = JSONObject.fromObject(jsonData);}
		catch (Exception ex) {reply = null;                           }
		return reply;
	}
	
	/**
	 * Get the landing page data for the given binder.
	 * 
	 * @param request
	 * @param bs
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ConfigData getLandingPageData(HttpServletRequest request, AllModulesInjected bs, String binderId) throws GwtTeamingException {
		ConfigData configData = new ConfigData();
		configData.setBinderId(binderId);

		try {
			// The landing page configuration data is stored as a
			// custom attribute.
			CustomAttribute customAttr = LandingPageHelper.getLandingPageMashupAttribute(bs, binderId);
    		if ((customAttr != null) && (customAttr.getValueType() == CustomAttribute.STRING)) {
    			String configStr = ((String) customAttr.getValue());
				configStr = MarkupUtil.fixupAllV2Urls(configStr);
    			configData.setConfigStr(configStr);
    		}
    		
			// Get the other settings that are stored in the
    		// 'mashup__properties' custom attribute.
   			getLandingPageProperties(bs, binderId, configData, request);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		return configData;
	}
	
	/**
	 * Get the landing page properties (background color, background
	 * image, etc, ...) for the given binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static GwtLandingPageProperties getLandingPageProperties(AllModulesInjected bs, String binderId, HttpServletRequest request) throws GwtTeamingException {
		GwtLandingPageProperties gwtLpProperties = new GwtLandingPageProperties();
		gwtLpProperties.setInheritProperties(true);
		
		try {
			// Landing page properties can be inherited.  Get the
			// binder that holds the landing page properties.
			Binder binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			Binder sourceBinder = binder.getLandingPagePropertiesSourceBinder();
			
			// Does the user have rights to the binder where the
			// landing page properties are coming from?
			if (!(bs.getBinderModule().testAccess(sourceBinder, BinderOperation.readEntries))) {
				// No, don't use inherited landing page properties.
				sourceBinder = binder;
			}
			
			// Get the landing page properties from the binder we
			// inherit from.
			LandingPageProperties lpProps = DefinitionHelper.getLandingPageProperties(request, sourceBinder);
			if (lpProps != null) {
				// Did we inherit the properties from another landing
				// page.
				if (sourceBinder == binder) {
					// No!
					gwtLpProperties.setInheritProperties(false);
				}
				
				gwtLpProperties.setBackgroundColor(  lpProps.getBackgroundColor()    );
				gwtLpProperties.setBackgroundImgName(lpProps.getBackgroundImageName());
				gwtLpProperties.setBackgroundImgUrl( lpProps.getBackgroundImageUrl() );
				gwtLpProperties.setBackgroundRepeat( lpProps.getBackgroundRepeat()   );
				gwtLpProperties.setBorderColor(      lpProps.getBorderColor()        );
				gwtLpProperties.setBorderWidth(      lpProps.getBorderWidth()        );
				gwtLpProperties.setContentTextColor( lpProps.getContentTextColor()   );
				gwtLpProperties.setHeaderBgColor(    lpProps.getHeaderBgColor()      );
				gwtLpProperties.setHeaderTextColor(  lpProps.getHeaderTextColor()    );
				gwtLpProperties.setHideFooter(       lpProps.getHideFooter()         );
				gwtLpProperties.setHideMasthead(     lpProps.getHideMasthead()       );
				gwtLpProperties.setHideMenu(         lpProps.getHideMenu()           );
				gwtLpProperties.setHideSidebar(      lpProps.getHideSidebar()        );
				gwtLpProperties.setStyle(            lpProps.getStyle()              );
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		return gwtLpProperties;
	}
	
	/**
	 * Get the landing page properties (background color, background
	 * image, etc, ...) for the given binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param lpConfigData
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	public static void getLandingPageProperties(AllModulesInjected bs, String binderId, ConfigData lpConfigData, HttpServletRequest request) throws GwtTeamingException {
		// Get the landing page properties for the given binder.
		GwtLandingPageProperties lpProperties = getLandingPageProperties(bs, binderId, request);
		if (null != lpProperties) {
			lpConfigData.initLandingPageProperties(lpProperties);
		}
	}

	/**
	 * Return a list of child binders for the given binder.
	 * 
	 * @param request
	 * @param bs
	 * @param findBrowser
	 * @param binderId
	 * 
	 * @return
	 */
	public static ArrayList<TreeInfo> getListOfChildBinders(HttpServletRequest request, AllModulesInjected bs, boolean findBrowser, String binderId) {
    	List<Map> entries;
		SeenMap seen;
		
		// Get the count of unseen items in the given binder and sub binders
    	Map<String, Counter> unseenCounts = new HashMap();
		List binderIds = new ArrayList();
		binderIds.add(binderId);
	    
		// Get entries created within last 30 days
		Date creationDate = new Date();
		creationDate.setTime(creationDate.getTime() - (ObjectKeys.SEEN_TIMEOUT_DAYS * 24 * 60 * 60 * 1000));
		String startDate = DateTools.dateToString(creationDate, DateTools.Resolution.SECOND);
		String now = DateTools.dateToString(new Date(), DateTools.Resolution.SECOND);
		Criteria crit = SearchUtils.newEntriesDescendants(binderIds);
		crit.add(org.kablink.util.search.Restrictions.between(Constants.MODIFICATION_DATE_FIELD, startDate, now));
		Map results = bs.getBinderModule().executeSearchQuery(
			crit,
			Constants.SEARCH_MODE_NORMAL,
			0,
			ObjectKeys.MAX_BINDER_ENTRIES_RESULTS,
			org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(
				Constants.ENTRY_ANCESTRY,
				Constants.DOCID_FIELD,
				Constants.LASTACTIVITY_FIELD,
				Constants.MODIFICATION_DATE_FIELD));
    	entries = ((List) results.get(ObjectKeys.SEARCH_ENTRIES));

		// Get the count of unseen entries.
		seen = bs.getProfileModule().getUserSeenMap(null);
    	for (Map entry:  entries) {
    		SearchFieldResult entryAncestors = ((SearchFieldResult) entry.get(Constants.ENTRY_ANCESTRY));
			if (null == entryAncestors) {
				continue;
			}
			
			String entryIdString = ((String) entry.get(Constants.DOCID_FIELD));
			if ((null == entryIdString) || seen.checkIfSeen(entry)) {
				continue;
			}
			
			// Count up the unseen counts for all ancestor binders.
			Iterator itAncestors = entryAncestors.getValueSet().iterator();
			while (itAncestors.hasNext()) {
				String binderIdString = ((String) itAncestors.next());
				if (binderIdString.equals("")) {
					continue;
				}
				
				Counter cnt = unseenCounts.get(binderIdString);
				if (null == cnt) {
					cnt = new WorkspaceTreeHelper.Counter();
					unseenCounts.put(binderIdString, cnt);
				}
				
				cnt.increment();
			}
    	}

		ArrayList<TreeInfo> listOfChildBinders = new ArrayList<TreeInfo>();
		Binder binder = GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId);
		if (null != binder) {
			// This is needed by buildTreeInfoFromBinder().
			ArrayList<Long> expandedBindersList;
			expandedBindersList = new ArrayList<Long>();
			
			// Get all of the child binders.
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_SORT_BY, org.kablink.util.search.Constants.SORT_TITLE_FIELD);
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, new Boolean(false));
			options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS);
			Map searchResults = bs.getBinderModule().getBinders(binder, options);
			List<Map> children = ((List) searchResults.get(ObjectKeys.SEARCH_ENTRIES));
			if (null != children) {
				for (Map child:  children) {
					// Get the next child binder.
					String childBinderId = ((String) child.get(Constants.DOCID_FIELD));
					Binder childBinder   = GwtUIHelper.getBinderSafely(bs.getBinderModule(), childBinderId);
					if (null != childBinder) {
						// Set the number of unseen entries for this
						// binder.
						TreeInfo treeInfo = buildTreeInfoFromBinder(
							request,
							bs,
							findBrowser,
							childBinder,
							expandedBindersList,
							false,
							1);
						Counter counter = unseenCounts.get(childBinderId);
						if (null != counter) {
							treeInfo.getBinderInfo().setNumUnread(counter.getCount());
						}
						listOfChildBinders.add(treeInfo);
					}
				}
			}
		}
		
		// Sort the list of child binders.
		if (!(listOfChildBinders.isEmpty())) {
			Collections.sort(listOfChildBinders, new TreeInfoComparator(true));
		}

		return listOfChildBinders;
	}
	
	/**
	 * Return a list of all the locales.
	 * 
	 * @return
	 */
	public static GwtLocales getLocales() {
		GwtLocales locales = new GwtLocales();
		TreeMap<String,String> localeMap2 = new TreeMap<String,String>();
		TreeMap<String,Locale> localeMap = NLT.getSortedLocaleList(getCurrentUser());
		
		if (null != localeMap) {
			for (Map.Entry<String,Locale> mapEntry:  localeMap.entrySet()) {
				Locale locale = mapEntry.getValue();
				localeMap2.put(mapEntry.getKey(), locale.toString());
			}
		}
		
		locales.setListOfLocales(localeMap2);
		return locales;
	}
	
	
	/**
	 * Return login information such as self registration and auto
	 * complete.
	 * 
	 * @param request
	 * @param bs
	 * 
	 * @return
	 */
	public static GwtLoginInfo getLoginInfo(HttpServletRequest request, AllModulesInjected bs) {
		// Get self-registration info.
		GwtSelfRegistrationInfo selfRegInfo = getSelfRegistrationInfo(request, bs);
		GwtLoginInfo loginInfo = new GwtLoginInfo();
		loginInfo.setSelfRegistrationInfo(selfRegInfo);
		
		// Read the value of the enable.login.autocomplete key from
		// ssf.properties.
		boolean allowAutoComplete = SPropsUtil.getBoolean("enable.login.autocomplete", false);
		loginInfo.setAllowAutoComplete(allowAutoComplete);
		
		// Are we running the Enterprise version of Vibe?
		if (ReleaseInfo.isLicenseRequiredEdition()) {
			// Yes!  Does the license allow external users?
			if (LicenseChecker.isAuthorizedByLicense("com.novell.teaming.ExtUsers")) {
				// Yes!
				ZoneModule zm = bs.getZoneModule();
				ZoneConfig zc = zm.getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
				boolean allowOpenIdAuth = zc.isExternalUserEnabled() && zc.getOpenIDConfig().isAuthenticationEnabled();
				loginInfo.setAllowOpenIdAuthentication(allowOpenIdAuth); 
				
				// Is OpenID authentication enabled?
				if (allowOpenIdAuth) {
					// Yes!  Get a list of the OpenID authentication
					// providers supported by Vibe.
					ArrayList<GwtOpenIDAuthenticationProvider> listOfProviders = getOpenIDAuthenticationProviders(bs);
					loginInfo.setListOfOpenIDAuthProviders(listOfProviders);
				}
			}
		}

		// Get the KeyShield SSO information.
		boolean ksHardwareTokenMissing = KShieldHelper.isHardwareTokenMissing();
		loginInfo.setKeyShieldHardwareTokenMissing(ksHardwareTokenMissing);
		if (ksHardwareTokenMissing) {
			GwtKeyShieldConfig gwtKSConfig = GwtKeyShieldSSOHelper.getKeyShieldConfig(bs);
			loginInfo.setKeyShieldErrorMessagesForWeb(gwtKSConfig.getSsoErrorMessageForWeb());
		}
		
		return loginInfo;
	}

	/**
	 * Returns a MainPageInfoRpcResponseData object containing the
	 * information necessary for GwtMainPage to run.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static MainPageInfoRpcResponseData getMainPageInfo(AllModulesInjected bs, HttpServletRequest request, String binderId) throws GwtTeamingException {
		try {
			// Construct a BinderInfo for the binder...
			BinderInfo bi = getBinderInfo(bs, request, binderId);
			
			// ...get the URL to the current user's avatar...
			String userAvatarUrl = getUserAvatarUrl(bs, request, getCurrentUser());

			// ...if the current user is not Guest and the zone
			// ...configuration has an auto update URL or it can be
			// ...deployed from the local server...
			boolean desktopAppEnabled        = false;
			boolean showDesktopAppDownloader = false;
			ZoneModule zm = bs.getZoneModule();
			if (!(GwtServerHelper.getCurrentUser().isShared())) {
				ZoneConfig zc = zm.getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
				String baseUrl = zc.getFsaAutoUpdateUrl();
				baseUrl = ((null == baseUrl) ? "" : baseUrl.trim());
				if ((0 < baseUrl.length()) || zc.getFsaDeployLocalApps()) {
					// ...get what we know about desktop application
					// ...deployment...
					GwtFileSyncAppConfiguration fsaConfig = GwtDesktopApplicationsHelper.getFileSyncAppConfiguration(bs, false);
					desktopAppEnabled = fsaConfig.getIsDeploymentEnabled();
					if (desktopAppEnabled) {
						UserProperties userProperties = bs.getProfileModule().getUserProperties(null);
						String s = ((String) userProperties.getProperty(ObjectKeys.USER_PROPERTY_SHOW_DESKTOP_APP_DOWNLOAD));
						if (MiscUtil.hasString(s))
						     showDesktopAppDownloader = Boolean.parseBoolean(s);
						else showDesktopAppDownloader = true;
					}
				}
				
				else {
					GwtLogHelper.debug(m_logger, "GwtServerHelper.getMainPageInfo():  The file synchronization application auto update URL is not available.");
				}
			}
			
			// Does the current user's Home folder serve as their My Files repository?
			boolean useHomeAsMyFiles =
				(SearchUtils.useHomeAsMyFiles(bs) &&
				(null != SearchUtils.getHomeFolderId(bs)));

			User user;
			boolean firstLogin = false;
			boolean superUser = false;
			
			user = getCurrentUser();
			if (null != user) {
				// Is this the super user (admin)?
				if (ObjectKeys.SUPER_USER_INTERNALID.equals(user.getInternalId())) {
					superUser = true;
				}
				
				// Is this the first time the admin has logged in?
				if (null == user.getFirstLoginDate()) {
					// Yes!
					firstLogin = true;
				}
			}
			
			// Is this the default zone?
			Long defaultZoneId = zm.getZoneIdByVirtualHost(null);	// null -> Returns default zone ID.
			boolean defaultZone = RequestContextHolder.getRequestContext().getZoneId().equals(defaultZoneId);
			
			// Has the telemetry tier 2 been set on the default zone?
			Boolean telemetryTier2Enabled = zm.getZoneConfig(defaultZoneId).getTelemetryTier2Enabled();
			boolean telemetryTier2Set = (null != telemetryTier2Enabled);

			// Should we close activity stream mode when running a view
			// details on an entry?
			boolean closeActivityStreamOnViewDetails = Utils.checkIfFilr();
			if (!closeActivityStreamOnViewDetails) {
				closeActivityStreamOnViewDetails = SPropsUtil.getBoolean("vibe.close.activity.stream.on.view.details", false);
			}
			
			// ...and use this all to construct a
			// ...MainPageInfoRpcResponseData to return.
			return
				new MainPageInfoRpcResponseData(
					bi,
					userAvatarUrl,
					MiscUtil.isNPAPISupported(request),
					desktopAppEnabled,
					showDesktopAppDownloader,
					useHomeAsMyFiles,
					firstLogin,
					superUser,
					defaultZone,
					telemetryTier2Set,
					closeActivityStreamOnViewDetails);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}
	
	/**
	 * Returns a ManageAdministratorsInfoRpcResponseData object
	 * containing the information for managing administrators.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ManageAdministratorsInfoRpcResponseData getManageAdministratorsInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the ManageAdministratorsInfoRpcResponseData
			// object we'll fill in and return.
			BinderInfo bi = getBinderInfo(bs, request, bs.getProfileModule().getProfileBinderId());
			if (!(bi.getWorkspaceType().isProfileRoot())) {
				GwtLogHelper.error(m_logger, "GwtServerHelper.getManageAdministratorsInformation():  The workspace type of the profile root binder was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.PROFILE_ROOT.name());
			}
			bi.setWorkspaceType(WorkspaceType.ADMINISTRATOR_MANAGEMENT);
			ManageAdministratorsInfoRpcResponseData reply =
				new ManageAdministratorsInfoRpcResponseData(
					bi,
					NLT.get("administration.manage.administrators"));

			// If we get here, reply refers to the
			// ManageAdministratorsInfoRpcResponseData object
			// containing the information about managing
			// administrators.  Return it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/**
	 * Returns a ManageTeamsInfoRpcResponseData object
	 * containing the information for managing teams.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ManageTeamsInfoRpcResponseData getManageTeamsInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the ManageTeamsInfoRpcResponseData
			// object we'll fill in and return.
	 		Workspace teamWorkspaceBinder = ((Workspace) getCoreDao().loadReservedBinder(
				ObjectKeys.TEAM_ROOT_INTERNALID,
				RequestContextHolder.getRequestContext().getZoneId()));
			BinderInfo bi = getBinderInfo(bs, request, teamWorkspaceBinder.getId());
			if (!(bi.getWorkspaceType().isTeamRoot())) {
				GwtLogHelper.error(m_logger, "GwtServerHelper.getManageTeamsInformation():  The workspace type of the team workspaces root binder was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.TEAM_ROOT.name());
			}
			bi.setWorkspaceType(WorkspaceType.TEAM_ROOT_MANAGEMENT);
			ManageTeamsInfoRpcResponseData reply =
				new ManageTeamsInfoRpcResponseData(
					bi,
					NLT.get("administration.manage.teams"));

			// If we get here, reply refers to the
			// ManageTeamsInfoRpcResponseData object
			// containing the information about managing teams.  Return
			// it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/**
	 * Returns a ManageUsersInfoRpcResponseData object
	 * containing the information for managing users.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ManageUsersInfoRpcResponseData getManageUsersInfo(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the ManageUsersInfoRpcResponseData
			// object we'll fill in and return.
			BinderInfo bi = getBinderInfo(bs, request, bs.getProfileModule().getProfileBinderId());
			if (!(bi.getWorkspaceType().isProfileRoot())) {
				GwtLogHelper.error(m_logger, "GwtServerHelper.getManageUsersInformation():  The workspace type of the profile root binder was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.PROFILE_ROOT.name());
			}
			bi.setWorkspaceType(WorkspaceType.PROFILE_ROOT_MANAGEMENT);
			ManageUsersInfoRpcResponseData reply =
				new ManageUsersInfoRpcResponseData(
					bi,
					NLT.get("administration.manage.userAccounts"));

			// If we get here, reply refers to the
			// ManageUsersInfoRpcResponseData object
			// containing the information about managing users.  Return
			// it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/**
	 * Returns a ManageUsersStateRpcResponseData object
	 * containing the information for managing users.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ManageUsersStateRpcResponseData getManageUsersState(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			HttpSession hSession = getCurrentHttpSession();
			Boolean showExternal = ((Boolean) hSession.getAttribute(CACHED_MANAGE_USERS_SHOW_EXTERNAL));
			Boolean showEnabled  = ((Boolean) hSession.getAttribute(CACHED_MANAGE_USERS_SHOW_ENABLED));
			Boolean showDisabled = ((Boolean) hSession.getAttribute(CACHED_MANAGE_USERS_SHOW_DISABLED));
			Boolean showInternal = ((Boolean) hSession.getAttribute(CACHED_MANAGE_USERS_SHOW_INTERNAL));
			Boolean showSiteAdmins = ((Boolean) hSession.getAttribute(CACHED_MANAGE_USERS_SHOW_SITE_ADMINS));
			Boolean showNonSiteAdmins = ((Boolean) hSession.getAttribute(CACHED_MANAGE_USERS_SHOW_NON_SITE_ADMINS));

			// Construct the ManageUsersStateRpcResponseData
			// object to return.
			ManageUsersStateRpcResponseData reply =
				new ManageUsersStateRpcResponseData(
					((null == showInternal)      || showInternal),
					((null == showExternal)      || showExternal),
					((null == showDisabled)      || showDisabled),
					((null == showEnabled)       || showEnabled),
					((null == showSiteAdmins)    || showSiteAdmins),
					((null == showNonSiteAdmins) || showNonSiteAdmins));

			// If we get here, reply refers to the
			// ManageUsersStateRpcResponseData object
			// containing the information about managing user.  Return
			// it.
			return reply;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/**
	 * Returns the ID of the folder that a user will use as their My
	 * Files container.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static Long getMyFilesContainerId(AllModulesInjected bs, User user) {
		Long reply;
		if (useHomeAsMyFiles(bs, user))
		     reply = getHomeFolderId(bs, user);
		else reply = null;
		if (null == reply) {
			reply = SearchUtils.getMyFilesFolderId(bs, user, true);	// true -> Create it if it doesn't exist.
		}
		return reply;
	}
	
	public static Long getMyFilesContainerId(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return getMyFilesContainerId(bs, getCurrentUser());
	}

	/**
	 * Returns a BinderInfo object for the user's My Files container.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	public static BinderInfo getMyFilesContainerInfo(AllModulesInjected bs, HttpServletRequest request) {
		BinderInfo reply;
		Long myId = getMyFilesContainerId(bs);
		if (null == myId)
		     reply = null;
		else reply = getBinderInfo(bs, request, String.valueOf(myId));
		return reply;
	}
		
	/**
	 * Returns information about the groups the current user is a member of.
	 * 
	 * @return
	 */
	public static List<GroupInfo> getMyGroups(HttpServletRequest request, AllModulesInjected bs) {
		User user = getCurrentUser();
		return getGroups(request, bs, user.getId());
	}

	/**
	 * Returns information about the teams the current user is a member of.
	 * 
	 * @return
	 */
	public static List<TeamInfo> getMyTeams(HttpServletRequest request, AllModulesInjected bs) {
		User user = getCurrentUser();
		return getTeams(request, bs, user.getId());
	}

	/**
	 * Return the Name Completion Settings.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static GwtNameCompletionSettings getNameCompletionSettings(AllModulesInjected bs) {
		GwtNameCompletionSettings gwtSettings = new GwtNameCompletionSettings();

		ZoneModule zm = bs.getZoneModule();
		ZoneConfig zc = zm.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
		NameCompletionSettings settings = zc.getNameCompletionSettings();
		if (settings != null) {
			// Get the field used for the group's primary display.
			NCDisplayField  fld    = settings.getGroupPrimaryFld();
			GwtDisplayField gwtFld = GwtDisplayField.TITLE;
			if      (fld == NCDisplayField.NAME)  gwtFld = GwtDisplayField.NAME;
			else if (fld == NCDisplayField.TITLE) gwtFld = GwtDisplayField.TITLE;
			gwtSettings.setGroupPrimaryDisplayField(gwtFld);
			
			// Get the field used for the groups' secondary display.
			fld    = settings.getGroupSecondaryFld();
			gwtFld = GwtDisplayField.DESCRIPTION;
			if      (fld == NCDisplayField.DESCRIPTION) gwtFld = GwtDisplayField.DESCRIPTION;
			else if (fld == NCDisplayField.FQDN)        gwtFld = GwtDisplayField.FQDN;
			gwtSettings.setGroupSecondaryDisplayField(gwtFld);
		}
		
		return gwtSettings;
	}
	
	/**
	 * Return the number of members in the given group.
	 * 
	 * @param bs
	 * @param groupIdL
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static int getNumberOfMembers(AllModulesInjected bs, Long groupIdL ) throws GwtTeamingException {
		int count = 0;
		try {
			// Get the group object.
			Principal group = bs.getProfileModule().getEntry(groupIdL);
			if (group != null && group instanceof Group) {
				// Get the members of the group.
				List<Principal> memberList = ((Group) group).getMembers();
				if (memberList != null) {
					count = memberList.size();
				}
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		GwtLogHelper.debug(m_logger, "number of users in the group: " + String.valueOf( count ) );
		return count;
	}

	/**
	 * Return a list of OpenID Authentication providers supported by
	 * Vibe.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static ArrayList<GwtOpenIDAuthenticationProvider> getOpenIDAuthenticationProviders(AllModulesInjected bs) {
		// Get a list of the OpenID providers
		ArrayList<GwtOpenIDAuthenticationProvider> listOfProviders = new ArrayList<GwtOpenIDAuthenticationProvider>();
		List<OpenIDProvider> providers = bs.getAdminModule().getOpenIDProviders();
		if (providers != null && providers.size() > 0) {
			Iterator<OpenIDProvider> iterator = providers.iterator();
			while (iterator.hasNext()) {
				OpenIDProvider provider = iterator.next();
				GwtOpenIDAuthenticationProvider gwtProvider = new GwtOpenIDAuthenticationProvider();
				gwtProvider.setName(provider.getName());
				gwtProvider.setUrl( provider.getUrl() );
				listOfProviders.add(gwtProvider);
			}
		}
		return listOfProviders;
	}
	
	/**
	 * Return the password policy configuration
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	public static PasswordPolicyConfig getPasswordPolicyConfig(AllModulesInjected bs, HttpServletRequest request) {
		PasswordPolicyConfig config = new PasswordPolicyConfig(bs.getAdminModule().isPasswordPolicyEnabled());
		return config;
	}
	
	/**
	 * Return information about what password policy entails.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	public static PasswordPolicyInfoRpcResponseData getPasswordPolicyInfo(AllModulesInjected bs, HttpServletRequest request) {
		PasswordPolicyInfoRpcResponseData ppInfo = new PasswordPolicyInfoRpcResponseData();
		
		ppInfo.setExpirationEnabled(    PasswordPolicyHelper.PASSWORD_EXPIRATION_ENABLED     );
		ppInfo.setExpirationDays(       PasswordPolicyHelper.PASSWORD_EXPIRATION_DAYS        );
		ppInfo.setExpirationWarningDays(PasswordPolicyHelper.PASSWORD_EXPIRATION_WARNING_DAYS);
		ppInfo.setMinimumLength(        PasswordPolicyHelper.PASSWORD_MINIMUM_LENGTH         );
		ppInfo.setSymbols(              PasswordPolicyHelper.PASSWORD_SYMBOLS                );
		
		return ppInfo;
	}
	
	/**
	 * Returns a GwtPresenceInfo object for a User.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static GwtPresenceInfo getPresenceInfo(User user) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.getPresenceInfo()");
		try {
			GwtPresenceInfo gwtPresence = new GwtPresenceInfo();
			try {
				// Guest user can't get presence information.
				User userAsking = getCurrentUser();
				if (!(ObjectKeys.GUEST_USER_INTERNALID.equals(userAsking.getInternalId()))) {
					PresenceManager presenceService = ((PresenceManager) SpringContextUtil.getBean("presenceService"));
					if (null != presenceService) {
						PresenceInfo pi = null;
						int userStatus = PresenceInfo.STATUS_UNKNOWN;
	
						CustomAttribute attr = user.getCustomAttribute("presenceID");
						String userID = ((null != attr) ? ((String) attr.getValue()) : null);
						  
						attr = userAsking.getCustomAttribute("presenceID");
						String userIDAsking = ((null != attr) ? ((String) attr.getValue()) : null);
	
						if (MiscUtil.hasString(userID) && MiscUtil.hasString(userIDAsking)) {
							pi = presenceService.getPresenceInfo(userIDAsking, userID);
							if (null != pi) {
								gwtPresence.setStatusText(pi.getStatusText());
								userStatus = pi.getStatus();
							}	
						}
						
						switch (userStatus) {
						case PresenceInfo.STATUS_AVAILABLE:  gwtPresence.setStatus(GwtPresenceInfo.STATUS_AVAILABLE); break;
						case PresenceInfo.STATUS_AWAY:       gwtPresence.setStatus(GwtPresenceInfo.STATUS_AWAY);      break;
						case PresenceInfo.STATUS_IDLE:       gwtPresence.setStatus(GwtPresenceInfo.STATUS_IDLE);      break;
						case PresenceInfo.STATUS_BUSY:       gwtPresence.setStatus(GwtPresenceInfo.STATUS_BUSY);      break;
						case PresenceInfo.STATUS_OFFLINE:    gwtPresence.setStatus(GwtPresenceInfo.STATUS_OFFLINE);   break;
						default:                             gwtPresence.setStatus(GwtPresenceInfo.STATUS_UNKNOWN);   break;
						}
					}
				}
			}
			catch (Exception e) {}
			return gwtPresence;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a default GwtPresence info object that contains a
	 * localized status text string.
	 * 
	 * @return
	 */
	public static GwtPresenceInfo getPresenceInfoDefault() {
		GwtPresenceInfo reply = new GwtPresenceInfo();		
		setPresenceText(reply);
		return reply;
	}

	/**
	 * Returns the image for the presence dude to display for a
	 * GwtPresenceInfo.
	 * 
	 * @param pi
	 * 
	 * @return
	 */
	public static String getPresenceDude(GwtPresenceInfo pi) {
		String dudeGif;
		switch (pi.getStatus()) {
		case PresenceInfo.STATUS_AVAILABLE:  dudeGif = "pics/presence/online_16.png";  break;
		case PresenceInfo.STATUS_AWAY:       dudeGif = "pics/presence/away_16.png";    break;
		case PresenceInfo.STATUS_IDLE:       dudeGif = "pics/presence/away_16.png";    break;
		case PresenceInfo.STATUS_BUSY:       dudeGif = "pics/presence/busy_16.png";    break;
		case PresenceInfo.STATUS_OFFLINE:    dudeGif = "pics/presence/offline_16.png"; break;
		default:                             dudeGif = "pics/presence/unknown_16.png"; break;
		}
		return dudeGif;
	}

	/**
	 * Construct a URL the user can click on that will invoke the
	 * 'change password' dialog.  Then send an e-mail that includes
	 * that URL to the given e-mail address.
	 * 
	 * @param bs
	 * @param request
	 * @param gwtuser
	 * @param emailAddress
	 * 
	 * @return
	 */
	public static SendForgottenPwdEmailRpcResponseData sendForgottenPwdEmail(final AllModulesInjected bs, final HttpServletRequest request, final GwtUser gwtUser, final String emailAddress) {
		final SendForgottenPwdEmailRpcResponseData responseData = new SendForgottenPwdEmailRpcResponseData();
		if (gwtUser == null || emailAddress == null || emailAddress.length() == 0 || bs == null) {
			responseData.addError("Invalid parameters passed to sendForgottenPwdEmail()");
			return responseData;
		}
		
		// Do the necessary work as the admin user.
		RunasTemplate.runasAdmin(
			new RunasCallback() {
				@Override
				public Object doAs() {
					try {
						// Is this an external user?
						User user = ((User) bs.getProfileModule().getEntry(gwtUser.getIdLong()));
						IdentityInfo identityInfo = user.getIdentityInfo();
						if (!(identityInfo.isInternal())) {
							// Yes!  Get a URL to the user's workspace.
							Boolean oldUseRTContext = ZoneContextHolder.getUseRuntimeContext();
							ZoneContextHolder.setUseRuntimeContext(Boolean.FALSE);
							AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
							adapterUrl.setParameter(WebKeys.ACTION,          WebKeys.ACTION_VIEW_PERMALINK          );
							adapterUrl.setParameter(WebKeys.URL_ENTRY_ID,    String.valueOf(user.getId())           );
							adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.user.name());

							// If we are running Filr, take the user to
							// 'my files'.
							if (Utils.checkIfFilr()) {
								adapterUrl.setParameter(WebKeys.URL_SHOW_COLLECTION, "0");	// 0 -> CollectionType.MY_FILES
							}

							// Append the encoded user token to the
							// URL.
							String token = ExternalUserUtil.encodeUserToken(user);
							adapterUrl.setParameter( ExternalUserUtil.QUERY_FIELD_NAME_EXTERNAL_USER_ENCODED_TOKEN, token );
							String url = adapterUrl.toString();
							ZoneContextHolder.setUseRuntimeContext(oldUseRTContext);
							if (PermaLinkUtil.forceSecureLinksInEmail()) {
								url = PermaLinkUtil.forceHTTPSInUrl(url);
							}

							// Has this external user already
							// self-registered?
							UrlNotificationType notificationType;
							if (user.getExtProvState() == ExtProvState.verified          ||
								user.getExtProvState() == ExtProvState.pwdResetRequested ||
								user.getExtProvState() == ExtProvState.pwdResetWaitingForVerification) {
								// Yes
								notificationType = UrlNotificationType.FORGOTTEN_PASSWORD;
							}
							else if (identityInfo.isFromOpenid() && (!(identityInfo.isFromLocal()))) {
								notificationType = UrlNotificationType.SELF_REGISTRATION_REQUIRED;
							}
							else {
								String err = NLT.get("request.pwd.reset.invalid.user.state");
								responseData.addError(err);
								return null;
							}

							Map<String, Object> errorMap = EmailHelper.sendUrlNotification(
								bs,
								url,
								notificationType,
								user.getId());

							if (errorMap != null) {
								List<SendMailErrorWrapper> emailErrors = ((List<SendMailErrorWrapper>) errorMap.get(ObjectKeys.SENDMAIL_ERRORS));
								if (emailErrors != null && emailErrors.size() > 0) {
									responseData.addErrors(SendMailErrorWrapper.getErrorMessages(emailErrors));
								}
								
								else {
									// Sending the e-mail worked.
									// Change the users 'external user
									// provisioned state' to 'password
									// reset requested'.
									if (user.getExtProvState() == ExtProvState.verified ||
										user.getExtProvState() == ExtProvState.pwdResetRequested ||
										user.getExtProvState() == ExtProvState.pwdResetWaitingForVerification) {
										ExternalUserUtil.markAsPwdResetRequested(user);
									}
								}
							}
						}
					}
					
					catch (Exception ex) {
						String error = NLT.get("send.forgotten.pwd.send.email.failed", new String[]{ex.toString()});
						responseData.addError(error);
						GwtLogHelper.error(m_logger, "GwtServerHelper.SendForgottenPwdEmail( EXCEPTION ):  ", ex);
					}

					return null;
				}
			},
			RequestContextHolder.getRequestContext().getZoneName());
		
		return responseData;
	}
	
	/**
	 * ?
	 * 
	 * @param gwtUser
	 * @param user
	 */
	public static void setExtUserProvState(GwtUser gwtUser, User user) {
		// Are we dealing with an external user?
		IdentityInfo idInfo = user.getIdentityInfo();
		if (idInfo != null && (!(idInfo.isInternal()))) {
			// Yes.
			gwtUser.setExtUserProvState(ExtUserProvState.UNKNOWN);
			ExtProvState extProvState = user.getExtProvState();
			if (extProvState != null) {
				switch (extProvState) {
				case credentialed:                    gwtUser.setExtUserProvState(ExtUserProvState.CREDENTIALED);                       break;
				case initial:                         gwtUser.setExtUserProvState(ExtUserProvState.INITIAL);                            break;
				case pwdResetRequested:               gwtUser.setExtUserProvState(ExtUserProvState.PWD_RESET_REQUESTED);                break;
				case pwdResetWaitingForVerification:  gwtUser.setExtUserProvState(ExtUserProvState.PWD_RESET_WAITING_FOR_VERIFICATION); break;
				case verified:                        gwtUser.setExtUserProvState(ExtUserProvState.VERIFIED);                           break;
				default:                              gwtUser.setExtUserProvState(ExtUserProvState.UNKNOWN);                            break;
				}
			}
		}
	}
	
	/**
	 * Stores a localized status text in a GwtPresenceInfo base on its
	 * status.
	 * 
	 * @param pi
	 */
	public static void setPresenceText(GwtPresenceInfo pi) {
		String statusText;
		switch (pi.getStatus()) {
		case PresenceInfo.STATUS_AVAILABLE:  statusText = NLT.get("presence.online");  break;
		case PresenceInfo.STATUS_AWAY:       statusText = NLT.get("presence.away");    break;
		case PresenceInfo.STATUS_IDLE:       statusText = NLT.get("presence.idle");    break;
		case PresenceInfo.STATUS_BUSY:       statusText = NLT.get("presence.busy");    break;
		case PresenceInfo.STATUS_OFFLINE:    statusText = NLT.get("presence.offline"); break;
		default:                             statusText = NLT.get("presence.none");    break;
		}
		pi.setStatusText(statusText);
	}

//!	...DRF (20150617):  Left off reformatting here!
	
	/**
	 * Get the project information for the given binder
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 */
	public static ProjectInfo getProjectInfo( AllModulesInjected allModules, String binderId ) throws GwtTeamingException
	{
		try
		{
			ProjectInfo projectInfo;
			Binder binder;
			CustomAttribute customAttr;
			
			projectInfo = new ProjectInfo();
			projectInfo.setBinderId( binderId );
			
			binder = allModules.getBinderModule().getBinder( Long.parseLong( binderId ) );
			
			// Get the project status
			customAttr = binder.getCustomAttribute( "status" );
    		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.STRING )
    		{
    			String status;

    			status = (String) customAttr.getValue();
    			projectInfo.setStatus( status );
    		}
    		
    		// Get the list of managers for this project.
			customAttr = binder.getCustomAttribute( "manager" );
    		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.COMMASEPARATEDSTRING )
    		{
				CommaSeparatedValue ids;
				List<Principal> principals;

				ids = (CommaSeparatedValue) customAttr.getValue();
				
				principals = ResolveIds.getPrincipals( ids.getValueSet() );
				
				for (Principal nextManager : principals)
				{
					if ( nextManager instanceof UserPrincipal )
					{
						PrincipalInfo principalInfo;
						GwtPresenceInfo presenceInfo;
						User user;
						
						principalInfo = PrincipalInfo.construct( nextManager.getId(), nextManager.getTitle() );
						
						user = (User) nextManager;
						principalInfo.setPresenceUserWSId( user.getWorkspaceId() );
						principalInfo.setUserPerson(       user.isPerson()       );

						if ( isPresenceEnabled() )
						     presenceInfo = getPresenceInfo( user );
						else
							presenceInfo = null;
						if ( null == presenceInfo )
						{
							presenceInfo = getPresenceInfoDefault();
						}
						if ( presenceInfo != null )
						{
							principalInfo.setPresence( presenceInfo );
							principalInfo.setPresenceDude( getPresenceDude( presenceInfo ) );
						}

						projectInfo.addManager( principalInfo );
					}
				}// end for()
    		}
    		
    		// Get the project due date
			customAttr = binder.getCustomAttribute( "due_date" );
    		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.DATE )
    		{
				Date dueDate;
				DateFormat df;
				User user;

				user = getCurrentUser();
				dueDate = (Date) customAttr.getValue();

    			df = DateFormat.getDateTimeInstance( DateFormat.MEDIUM, DateFormat.SHORT, user.getLocale() );
		    	df.setTimeZone( user.getTimeZone() );

		    	projectInfo.setDueDate( df.format( dueDate ) );
    		}
    		
			return projectInfo;
		}
		catch (Exception ex)
		{
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}
	
	/**
	 * Returns information about the saved searches the current user
	 * as defined.
	 * 
	 * @return
	 */
	public static List<SavedSearchInfo> getSavedSearches(AllModulesInjected bs) {
		// Allocate an ArrayList to return the saved searches in.
		List<SavedSearchInfo> ssList = new ArrayList<SavedSearchInfo>();

		// Does the user have any saved searches defined?
		UserProperties userProperties = bs.getProfileModule().getUserProperties(getCurrentUserId());
		Map properties = userProperties.getProperties();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			// Yes!  Scan them...
			Map userQueries = ((Map) properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES));
			Set queryKeys = userQueries.keySet();
			for (Iterator qkIT = queryKeys.iterator(); qkIT.hasNext(); ) {
				// ...added a SavedSearchInfo for each to the list.
				String key = ((String) qkIT.next());
				SavedSearchInfo ssi = new SavedSearchInfo();
				ssi.setName(key);
				ssList.add(ssi);
			}
		}

		// If there are any saved searches being returned...
		if (!(ssList.isEmpty())) {
			// ...sort them.
			Collections.sort(ssList, new SavedSearchInfoComparator());
		}
		
		// If we get here, ssList refers to a List<SavedSearchInfo> of
		// the user's saved searches.  Return it.
		return ssList;
	}
	
	/**
	 * Return information about self registration.
	 */
	public static GwtSelfRegistrationInfo getSelfRegistrationInfo( HttpServletRequest request, AllModulesInjected ami )
	{
		GwtSelfRegistrationInfo selfRegInfo;
		boolean selfRegAllowed;
		
		selfRegInfo = new GwtSelfRegistrationInfo();
		
		// Set the flag that indicates whether self registration is allowed.
		selfRegAllowed = MiscUtil.canDoSelfRegistration( ami );
		selfRegInfo.setSelfRegistrationAllowed( selfRegAllowed );
		if ( selfRegAllowed )
		{
			ProfileModule			profileModule;
			Map<String, Definition>	entryDefsMap;
			Definition				def;
			Collection<Definition>	entryDefsCollection;
			Iterator<Definition>	entryDefsIterator;

			profileModule = ami.getProfileModule();
			
			// There is only 1 entry definition for a Profile binder.  Get it.
			entryDefsMap = profileModule.getProfileBinderEntryDefsAsMap();
			entryDefsCollection = entryDefsMap.values();
			entryDefsIterator = entryDefsCollection.iterator();
			if ( entryDefsIterator.hasNext() )
			{
				AdaptedPortletURL	adapterUrl;

				def = (Definition) entryDefsIterator.next();

				// Create the url needed to invoke the "Add User" page.
				adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
				adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_PROFILE_ENTRY );
				adapterUrl.setParameter( WebKeys.URL_BINDER_ID, profileModule.getProfileBinderId().toString() );
				adapterUrl.setParameter( WebKeys.URL_ENTRY_TYPE, def.getId() );
				
				selfRegInfo.setCreateUserUrl( adapterUrl.toString() );
			}
		}
		
		return selfRegInfo;
	}// end getSelfRegistrationInfo()
	
	/**
	 * Extracts a non-null string from a JSONObject.
	 * 
	 * @param jso
	 * @param key
	 * 
	 * @return
	 */
	public static String getSFromJSO(JSONObject jso, String key) {
		String reply;
		if ((null == jso) || (!(jso.has(key)))) {
			reply = "";
		}
		else {
			reply = jso.getString(key);
			if (null == reply) {
				reply = "";
			}
		}
		return reply;
	}

	/**
	 * Parses and extracts a non-null string from a JSON data string.
	 * 
	 * @param jsonData
	 * @param key
	 * 
	 * @return
	 */
	public static String getSFromJSO(String jsonData, String key) {
		return getSFromJSO(getJSOFromS(jsonData), key);
	}
	
	/**
	 * Returns information about the groups of a specific user.
	 * 
	 * @param bs
	 * @param userId 
	 * 
	 * @return
	 */
	public static List<GroupInfo> getGroups(
		HttpServletRequest request,
		AllModulesInjected bs,
		Long userId,
		boolean includeTeamGroups )
	{
		// Allocate an List<GroupInfo> to hold the groups.
		List<GroupInfo> reply = new ArrayList<GroupInfo>();
		
		// Is the user a member of any groups?
		List<Group> groups = GwtUIHelper.getGroups(userId);
		if (MiscUtil.hasItems(groups)) {
			// Yes!  Scan them...
			for (Group myGroup : groups) {
				
				// Don't include "team groups" in the results if they are not asked for.
				if ( myGroup.getGroupType() == Group.GroupType.team && includeTeamGroups == false )
					continue;
				
				// ...adding a GroupInfo for each to the reply list.
				GroupInfo gi = new GroupInfo();
				gi.setId(myGroup.getId());
				gi.setTitle(myGroup.getTitle());
				reply.add(gi);
			}
		}
		
		// If there any groups being returned...
		if (!(reply.isEmpty())) {
			// ...sort them.
			Collections.sort(reply, new GroupInfoComparator());
		}
		
		// If we get here, reply refers to a List<GroupInfo> of the
		// groups the user is a member of.  Return it.
		return reply;
	}
	
	/**
	 * Returns information about the groups of a specific user.
	 * 
	 * @param bs
	 * @param userId 
	 * 
	 * @return
	 */
	public static List<GroupInfo> getGroups(HttpServletRequest request, AllModulesInjected bs, Long userId ) {
		return getGroups( request, bs, userId, false );
	}
	
	/**
	 * Returns information about the of a specific user
	 * 
	 * @param bs
	 * @param userId 
	 * 
	 * @return
	 */
	public static List<Long> getGroupIds(
		HttpServletRequest request,
		AllModulesInjected bs,
		Long userId,
		boolean includeTeamGroups )
	{
		// Allocate an ArrayList<GroupInfo> to hold the groups.
		List<Long> reply = new ArrayList<Long>();
		
		List<GroupInfo> groups = getGroups(request, bs, userId, includeTeamGroups );
		for (GroupInfo group:  groups) {
			reply.add(group.getId());
		}

		// If we get here, reply refers to the List<Long> of the groups
		// the user is a member of.  Return it.
		return reply;
	}
	
	/**
	 * Returns information about the of a specific user
	 * 
	 * @param bs
	 * @param userId 
	 * 
	 * @return
	 */
	public static List<Long> getGroupIds(HttpServletRequest request, AllModulesInjected bs, Long userId) {
		return getGroupIds( request, bs, userId, false );
	}
	
	/**
	 * Return a GroupType from the given group.
	 * 
	 * @param group
	 * 
	 * @return
	 */
	public static GroupType getGroupType(Principal group) {
		GroupClass gc;
		if (null == group) {
			gc = GroupClass.UNKNOWN;
		}
		
		else if ( group.isReserved() ) {
			gc = GroupClass.INTERNAL_SYSTEM;
		}
		
		else {
			IdentityInfo identityInfo = group.getIdentityInfo();
			if (identityInfo == null) {
				gc = GroupClass.UNKNOWN;
			}
			
			else if (identityInfo.isFromLdap()) {
				if (identityInfo.isInternal())
				     gc = GroupClass.INTERNAL_LDAP;
				else gc = GroupClass.EXTERNAL_LDAP;
			}
			
			else {
				if (identityInfo.isInternal())
				     gc= GroupClass.INTERNAL_LOCAL;
				else gc= GroupClass.EXTERNAL_LOCAL;
			}
		}
		
		boolean admin = ((null != group) ? AdminHelper.isSiteAdminMember(group.getId()) : false);
		return new GroupType(gc, admin);
	}
	
	/**
	 * For the given ScheduleInfo object return a a GwtSchedule object that represents the data in
	 * the ScheduleInfo object.
	 */
	public static GwtSchedule getGwtSyncSchedule( ScheduleInfo scheduleInfo )
	{
		GwtSchedule gwtSchedule;

		if ( scheduleInfo == null )
			return null;
		
		gwtSchedule = new GwtSchedule();

		Schedule schedule;

		gwtSchedule.setEnabled( scheduleInfo.isEnabled() );
		
		schedule = scheduleInfo.getSchedule();
		if ( schedule != null )
		{
			if ( schedule.isDaily() )
			{
				gwtSchedule.setDayFrequency( DayFrequency.EVERY_DAY );
			}
			else
			{
				gwtSchedule.setDayFrequency( DayFrequency.ON_SELECTED_DAYS );
				gwtSchedule.setOnMonday( schedule.isOnMonday() );
				gwtSchedule.setOnTuesday( schedule.isOnTuesday() );
				gwtSchedule.setOnWednesday( schedule.isOnWednesday() );
				gwtSchedule.setOnThursday( schedule.isOnThursday() );
				gwtSchedule.setOnFriday( schedule.isOnFriday() );
				gwtSchedule.setOnSaturday( schedule.isOnSaturday() );
				gwtSchedule.setOnSunday( schedule.isOnSunday() );
				
				if ( schedule.areAllDaysDisabled() )
				{
					gwtSchedule.setDayFrequency( DayFrequency.EVERY_DAY );
				}
			}
			
			if ( schedule.isRepeatMinutes() )
			{
				int minutes;
				
				gwtSchedule.setTimeFrequency( TimeFrequency.REPEAT_EVERY_MINUTE );
				minutes = Integer.valueOf( schedule.getMinutesRepeat() );
				gwtSchedule.setRepeatEveryValue( minutes );
			}
			else if ( schedule.isRepeatHours() )
			{
				int hours;
				
				gwtSchedule.setTimeFrequency( TimeFrequency.REPEAT_EVERY_HOUR );
				hours = Integer.valueOf( schedule.getHoursRepeat() );
				gwtSchedule.setRepeatEveryValue( hours );
			}
			else
			{
				int minutes;
				int hours;
				
				gwtSchedule.setTimeFrequency( TimeFrequency.AT_SPECIFIC_TIME );
				
				minutes = Integer.valueOf( schedule.getMinutes() );
				gwtSchedule.setAtMinutes( minutes );
				
				hours = Integer.valueOf( schedule.getHours() );
				gwtSchedule.setAtHours( hours );
			}
		}

		return gwtSchedule;
	}

	/**
	 * For the given GwtSchedule, return a ScheduleInfo that represents the GwtSchedule.
	 * This code is patterned after the code in ScheduleHelper.getSchedule()
	 */
	public static ScheduleInfo getScheduleInfoFromGwtSchedule( GwtSchedule gwtSchedule )
	{
		Long zoneId;
		ScheduleInfo scheduleInfo;
		
		// Get the ScheduleInfo for this net folder.
		zoneId = RequestContextHolder.getRequestContext().getZoneId();
		scheduleInfo = new ScheduleInfo( zoneId );
		scheduleInfo.setSchedule( new Schedule( "" ) );
		
		// Do we have a GwtSchedule that we need to take data from and
		// update the ScheduleInfo?
		if ( gwtSchedule != null )
		{
			Schedule schedule;
			DayFrequency dayFrequency;
			TimeFrequency timeFrequency;
			Random randomMinutes;
			
			// Yes
			randomMinutes = new Random();
			
			scheduleInfo.setEnabled( gwtSchedule.getEnabled() );
			
			schedule = scheduleInfo.getSchedule();
			
			dayFrequency = gwtSchedule.getDayFrequency(); 
			if (  dayFrequency == DayFrequency.EVERY_DAY )
			{
				schedule.setDaily( true );
			}
			else if ( dayFrequency == DayFrequency.ON_SELECTED_DAYS )
			{
				schedule.setDaily( false );
				schedule.setOnMonday( gwtSchedule.getOnMonday() );
				schedule.setOnTuesday( gwtSchedule.getOnTuesdy() );
				schedule.setOnWednesday( gwtSchedule.getOnWednesday() );
				schedule.setOnThursday( gwtSchedule.getOnThursday() );
				schedule.setOnFriday( gwtSchedule.getOnFriday() );
				schedule.setOnSaturday( gwtSchedule.getOnSaturday() );
				schedule.setOnSunday( gwtSchedule.getOnSunday() );
			}
			
			timeFrequency = gwtSchedule.getTimeFrequency(); 
			if ( timeFrequency == TimeFrequency.AT_SPECIFIC_TIME )
			{
				schedule.setHours( gwtSchedule.getAtHoursAsString() );
				schedule.setMinutes( gwtSchedule.getAtMinutesAsString() );
			}
			else if ( timeFrequency == TimeFrequency.REPEAT_EVERY_MINUTE )
			{
				int repeatValue;
				
				schedule.setHours( "*" );
				
				repeatValue = gwtSchedule.getRepeatEveryValue();
				if ( repeatValue == 15 || repeatValue == 30 )
				{
					schedule.setMinutes( randomMinutes.nextInt( repeatValue ) + "/" + repeatValue );
				}
				else if ( repeatValue == 45 )
				{
					schedule.setMinutes( "0/45" );
				}
			}
			else if ( timeFrequency == TimeFrequency.REPEAT_EVERY_HOUR )
			{
				schedule.setMinutes( Integer.toString( randomMinutes.nextInt( 60 ) ) );
				schedule.setHours( "0/" + gwtSchedule.getRepeatEveryValue() );
			}
		}
		
		return scheduleInfo;
	}
	
	/**
	 * Return a GwtPersonalPreferences object that holds the personal
	 * preferences for the logged in user.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	public static GwtPersonalPreferences getPersonalPreferences(AllModulesInjected bs, HttpServletRequest request) {
		GwtPersonalPreferences personalPrefs = new GwtPersonalPreferences();
		try {
			// Store the user's display style preference.
			User user = getCurrentUser();
			personalPrefs.setDisplayStyle(user.getCurrentDisplayStyle());
			
			// Are we dealing with the guest user?
			if (!(user.isShared())) {
				// No!  Get the number of entries per page that should
				// be displayed when a folder is selected.
				Integer numEntriesPerPage = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
				UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
				String value = ((String) userProperties.getProperty(ObjectKeys.PAGE_ENTRIES_PER_PAGE));
				if (MiscUtil.hasString(value)) {
					try {
						numEntriesPerPage = Integer.parseInt(value);
					}
					catch (NumberFormatException nfe) {
						GwtLogHelper.warn(m_logger, "GwtServerHelper.getPersonalPreferences():  num entries per page is not an integer.", nfe);
					}
				}
				personalPrefs.setNumEntriesPerPage(numEntriesPerPage);

				// Set whether the user can download files.
				personalPrefs.setCanDownload(AdminHelper.getEffectiveDownloadSetting(bs, user));
				
				// Get the action to take when a file link is
				// activated.
				personalPrefs.setFileLinkAction(gwtFileLinkActionFromFileLinkAction(AdminHelper.getEffectiveFileLinkAction(bs, user)));
				
				// Set the flag that indicates whether 'editor
				// overrides; are supported.
				personalPrefs.setEditorOverrideSupported(SsfsUtil.supportAttachmentEdit());

				// Set the flags dealing with the public collection.
				boolean publicSharesActive = bs.getSharingModule().arePublicSharesActive();
				personalPrefs.setPublicSharesActive(publicSharesActive);
				Boolean hidePublicCollection;
				if (publicSharesActive)
				     hidePublicCollection = ((Boolean) userProperties.getProperty(ObjectKeys.HIDE_PUBLIC_COLLECTION));
				else hidePublicCollection = null;
				personalPrefs.setHidePublicCollection(hidePublicCollection);
			}
			
			else {
				GwtLogHelper.warn(m_logger, "GwtServerHelper.getPersonalPreferences():  User is guest.");
			}
		}
		
		catch (AccessControlException acEx) {
			// Nothing to do.
			GwtLogHelper.warn(m_logger, "GwtServerHelper.getPersonalPreferences():  AccessControlException", acEx);
		}
		
		catch (Exception e) {
			// Nothing to do.
			GwtLogHelper.warn(m_logger, "GwtServerHelper.getPersonalPreferences():  Unknown exception", e);
		}
		
		return personalPrefs;
	}
	
	/**
	 * Resolves, if possible, a user ID to a User object.
	 * 
	 * @param userId
	 * @param checkActive
	 * 
	 * @return
	 */
	public static User getResolvedUser(Long userId, boolean checkActive) {
		return ResolveIds.getResolvedUser(userId, checkActive);
	}
	
	public static User getResolvedUser(Long userId) {
		// Always use the initial form of the method.
		return getResolvedUser(userId, false);
	}

	/**
	 * Return subscription data for the given entry id.
	 */
	public static SubscriptionData getSubscriptionData( AllModulesInjected bs, String entryId )
	{
		SubscriptionData subscriptionData;
		User user;
		String address;
		
		subscriptionData = new SubscriptionData();

		user = getCurrentUser();
		
		// Get the user's primary e-mail address.
		address = user.getEmailAddress( Principal.PRIMARY_EMAIL );
		subscriptionData.setPrimaryEmailAddress( address );
		
		// Get the user's mobile e-mail address.
		address = user.getEmailAddress( Principal.MOBILE_EMAIL );
		subscriptionData.setMobileEmailAddress( address );
		
		// Get the user's text message address
		address = user.getEmailAddress( Principal.TEXT_EMAIL );
		subscriptionData.setTextMessagingAddress( address );

		// Get the user's subscription data for the given entry.
		{
			FolderEntry entry;
			Subscription sub;
			FolderModule folderModule;
			Long entryIdL;
			
			// Get the subscription data for the given entry.
			entryIdL = new Long( entryId );
			folderModule = bs.getFolderModule();
			entry = folderModule.getEntry( null, entryIdL );
			sub = folderModule.getSubscription( entry );			
			
			if ( sub != null )
			{
				Map<Integer,String[]> subMap;
				String[] values;

				// Get the map that holds the subscription data.  The following is an example of what the data may look like.
				// 2:_primary:3:_primary,_text:5:_primary,_text,_mobile:
				subMap = sub.getStyles();
				
				// Get the subscription values for which address should be sent an e-mail.
				values = (String[]) subMap.get( Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION );
				subscriptionData.setSendEmailTo( values );
				
				// Get the subscription values for which address should be sent an e-mail without an attachment.
				values = (String[]) subMap.get( Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION );
				subscriptionData.setSendEmailToWithoutAttachment( values );
				
				// Get the subscription values for which address should be sent a text.
				values = (String[]) subMap.get( Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION );
				subscriptionData.setSendTextTo( values );
			}
		}
		
		return subscriptionData;
	}
		
	/**
	 * Returns a string for a value out of the entry map from a search
	 * results.
	 * 
	 * @param em
	 * @param key
	 * @param dateStyle
	 * @param timeStyle
	 * 
	 * @return
	 */
	public static String getStringFromEntryMap(Map em, String key, int dateStyle, int timeStyle) {
		return
			getStringFromEntryMapValue(
				getValueFromEntryMap(
					em,
					key),
				dateStyle,
				timeStyle);
	}
	
	public static String getStringFromEntryMap(Map em, String key) {
		// Always use the initial form of the method.
		return getStringFromEntryMap(em, key, DateFormat.MEDIUM, DateFormat.LONG);		
	}

	/**
	 * Returns a String[] for the values out of the entry map from a
	 * search results.
	 * 
	 * @param em
	 * @param key
	 * @param dateStyle
	 * @param timeStyle
	 * 
	 * @return
	 */
	public static String[] getStringsFromEntryMap(Map em, String key, int dateStyle, int timeStyle) {
		return
			getStringsFromEntryMapValue(
				getValueFromEntryMap(
					em,
					key),
				dateStyle,
				timeStyle);
	}
	
	public static String[] getStringsFromEntryMap(Map em, String key) {
		// Always use the initial form of the method.
		return getStringsFromEntryMap(em, key, DateFormat.MEDIUM, DateFormat.LONG);		
	}

	/**
	 * Returns a string for a value out of the entry map from a search
	 * results.
	 * 
	 * @param emValue
	 * @param dateStyle
	 * @param timeStyle
	 * 
	 * @return
	 */
	public static String getStringFromEntryMapValue(Object emValue, int dateStyle, int timeStyle) {
		String reply = "";
		if (null != emValue) {
			// Yes!  Is it a string?
			if (emValue instanceof String) {
				// Yes!  Return it directly.
				reply = ((String) emValue);
			}
			
			// No, it isn't a string!  Is it a date?
			else if (emValue instanceof Date) {
				// Yes!  Format it for the current user and return
				// that.
				reply = getDateTimeString(((Date) emValue), dateStyle, timeStyle);
			}
			
			// No, it isn't a date either!  Is it a search field
			// result?
			else if (emValue instanceof SearchFieldResult) {
				// Yes!  Take the first string value.
				String[] emValues = ((SearchFieldResult) emValue).getValueArray().toArray(new String[0]);
				int emCount = ((null == emValues) ? 0 : emValues.length);
				if (0 < emCount){
				     // reply = emValues[0]; //Modified by Lokesh - Not sure why we are returning only the first value.
					// Modifying to fix bug 983052.
					reply="";
					for(int j=0;j<emValues.length;j++){						
						reply+=emValues[j];
						if(j<emValues.length-1){
							reply+=",";
						}
					}
				}
				else reply = "";
			}
			else {
				// No, it isn't a search field result either!  Let the
				// object convert itself to a string and return that.
				reply = emValue.toString();
			}
		}
		
		// If we get here, reply refers to an empty string or the
		// appropriate string value for the key from the entry map.
		// Return it.
		return reply;
	}
	
	/**
	 * Returns a String[] for the values out of the entry map from a
	 * search results.
	 * 
	 * @param emValue
	 * @param dateStyle
	 * @param timeStyle
	 * 
	 * @return
	 */
	public static String[] getStringsFromEntryMapValue(Object emValue, int dateStyle, int timeStyle) {
		List<String> reply = new ArrayList<String>();
		if (null != emValue) {
			// Yes!  Is it a string?
			if (emValue instanceof String) {
				// Yes!  Return it directly.
				reply.add((String) emValue);
			}
			
			// No, it isn't a string!  Is it a date?
			else if (emValue instanceof Date) {
				// Yes!  Format it for the current user and return
				// that.
				reply.add(getDateTimeString(((Date) emValue), dateStyle, timeStyle));
			}
			
			// No, it isn't a date either!  Is it a search field
			// result?
			else if (emValue instanceof SearchFieldResult) {
				// Yes!  Take the first string value.
				String[] emValues = ((SearchFieldResult) emValue).getValueArray().toArray(new String[0]);
				int emCount = ((null == emValues) ? 0 : emValues.length);
				for (int i = 0; i < emCount; i += 1) {
					reply.add(emValues[i]);
				}
			}
			else {
				// No, it isn't a search field result either!  Let the
				// object convert itself to a string and return that.
				reply.add(emValue.toString());
			}
		}
		
		// If we get here, reply refers to an empty List<String> or the
		// appropriate string values for the key from the entry map.
		// Return it.
		return reply.toArray(new String[0]);
	}
	
	/**
	 * Return the permalink for the given system binder URL.
	 */
	public static String getSystemBinderPermalink( HttpServletRequest req, SystemBinderType sysBinderType )
	{
		String reply = "";
		
		try
		{
			Binder binder;
			String internalId;
			
			switch ( sysBinderType )
			{
			case GLOBAL_ROOT:
				internalId = ObjectKeys.GLOBAL_ROOT_INTERNALID;
				break;
				
			case NET_FOLDER_ROOT:
				internalId = ObjectKeys.NET_FOLDERS_ROOT_INTERNALID;
				break;
			
			case PROFILE_ROOT:
				internalId = ObjectKeys.PROFILE_ROOT_INTERNALID;
				break;
				
			case TEAM_ROOT:
				internalId = ObjectKeys.TEAM_ROOT_INTERNALID;
				break;
				
			case TOP_WORKSPACE:
				internalId = ObjectKeys.TOP_WORKSPACE_INTERNALID;
				break;
			
			case UNKNOWN:
			default:
				internalId = null;
				break;
			}
			
			if ( internalId != null )
			{
				binder = getCoreDao().loadReservedBinder(
														internalId,
														RequestContextHolder.getRequestContext().getZoneId() );
	
				reply = PermaLinkUtil.getPermalink( req, binder );
			}
		}
		catch ( Exception ex )
		{
		}
		
		return reply;
	}
	
	/**
	 * Reads a List<TaskFolderInfo> from a Map.
	 * 
	 * @param bs
	 * @param request
	 * @param m
	 * @param key
	 * @param errorList
	 * 
	 * @return
	 */
	public static List<TaskFolderInfo> getTaskFolderInfoListFromEntryMap(AllModulesInjected bs, HttpServletRequest request, Map m, String key, List<ErrorInfo> errorList) {
		// Is there value for the key?
		List<TaskFolderInfo> reply = new ArrayList<TaskFolderInfo>();
		Object o = m.get(key);
		if (null != o) {
			// Yes!  Is the value is a String?
			if (o instanceof String) {
				// Yes!  Use it as the folder ID to create a
				// TaskFolderInfo to add to the List<TaskFolderInfo>. 
				addTFIFromStringToList(bs, request, ((String) o), reply, errorList);
			}

			// No, the value isn't a String!  Is it a String[]?
			else if (o instanceof String[]) {
				// Yes!  Scan them and use each as the folder ID to
				// create a TaskFolderInfo to add to the
				// List<TaskFolderInfo>. 
				String[] strLs = ((String[]) o);
				int c = strLs.length;
				for (int i = 0; i < c; i += 1) {
					addTFIFromStringToList(bs, request, strLs[i], reply, errorList);
				}
			}

			// No, the value isn't a String[] either!  Is it a
			// SearchFieldResult?
			else if (o instanceof SearchFieldResult) {
				// Yes!  Scan the value set from it and use each as the
				// folder ID to create a TaskFolderInfo to add to the
				// List<TaskFolderInfo>. 
				SearchFieldResult sfr = ((SearchFieldResult) m.get(key));
				Set<String> strLs = ((Set<String>) sfr.getValueSet());
				for (String strL:  strLs) {
					addTFIFromStringToList(bs, request, strL, reply, errorList);
				}
			}
		}
		
		// If we get here, reply refers to the List<TaskFolderInfo> of
		// values from the Map.  Return it.
		return reply;
	}
	
	/**
	 * Returns a count of the members of a team.
	 * 
	 * @param bs
	 * @param binder
	 * 
	 * @return
	 */
	public static int getTeamCount(AllModulesInjected bs, Binder binder) {
		Set<Long> teamMemberIds = getTeamMemberIds(bs, binder.getId());
		return ((null == teamMemberIds) ? 0 : teamMemberIds.size());
	}

	/**
	 * Returns a Set<Long> of the member IDs of a team.
	 * 
	 * @param bm
	 * @param binderId
	 * @param explodeGroups
	 * 
	 * @return
	 */
	public static Set<Long> getTeamMemberIds(BinderModule bm, Long binderId, boolean explodeGroups) {
		Set<Long> teamMemberIds = null;
		try {teamMemberIds = bm.getTeamMemberIds(binderId, explodeGroups);}
		catch (Exception ex) {/* Ignored. */}
		return validatePrincipalIds(teamMemberIds);
	}
	
	public static Set<Long> getTeamMemberIds(AllModulesInjected bs, Long binderId, boolean explodeGroups) {
		// Always use the initial form of the method.
		return getTeamMemberIds(bs.getBinderModule(), binderId, explodeGroups);
	}
	
	public static Set<Long> getTeamMemberIds(AllModulesInjected bs, Long binderId) {
		// Always use the initial form of the method.
		return getTeamMemberIds(bs, binderId, false);
	}
	
	/*
	 * If the user has a workspace, their ID is returned.  If they
	 * don't have a workspace, once is created for them and its ID is
	 * returned.
	 */
	private static Long getUserWorkspaceId(AllModulesInjected bs, HttpServletRequest request, User user) {
		// Does the user have a workspace?
		Long reply = user.getWorkspaceId();
		if (null == reply) {
			try {
				// No!  Can we create one for them?
				Workspace ws = bs.getProfileModule().addUserWorkspace(user, null);	// null -> No add options.
				if (null != ws) {
					// Yes!  Return its ID.
					reply = ws.getId();
				}
			}
			
			catch (Exception ex) {
				// No, we couldn't create their workspace!  Log the
				// exception.
				GwtLogHelper.error(m_logger, "GwtServerHelper.getUserWorkspaceId( SOURCE EXCEPTION ):  ", ex);
			}
		}
		
		// If we get here, reply refers the the user's workspace ID or
		// is null.  Return it.
		return reply;
	}
	
	/**
	 * Return the update logs configuration
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	public static UpdateLogsConfig getUpdateLogsConfig(AllModulesInjected bs, HttpServletRequest request) {
		UpdateLogsConfig config = new UpdateLogsConfig(bs.getAdminModule().isAutoApplyDeferredUpdateLogs());
		return config;
	}
	
	/**
	 * Returns the Object from an entry map.
	 * 
	 * @param em
	 * @param key
	 * 
	 * @return
	 */
	public static Object getValueFromEntryMap(Map em, String key) {
		// Is one of the trash fields being requested?
		if (key.startsWith(Constants.PRE_DELETED_FIELD)) {
			// Yes!  Is it a request for who moved the item to the
			// trash?
			if (key.equals(Constants.PRE_DELETED_BY_ID_FIELD)) {
				// Yes!  Do we have an ID?
				Object v = em.get(key);
				if (null != v) {
					// Yes!  Can we obtain the matching user?
					try {
						String deletedById  = v.toString();
						List deletedByUsers = ResolveIds.getPrincipals(deletedById, false);
						User deletedByUser  = ((User) deletedByUsers.get(0));
						if (null != deletedByUser) {
							// Yes!  Return it.
							return deletedByUser;
						}
					}
					catch (Exception e) {/* Ignore. */}
				}
				
				// No ID or we couldn't obtain the matching user.
				// Return their title, if there.
				key = Constants.PRE_DELETED_BY_TITLE_FIELD;
			}
			
			// No, it isn't a request for who moved the item to the
			// trash!  Is it a request for when the item was moved
			// there?
			else if (key.equals(Constants.PRE_DELETED_WHEN_FIELD)) {
				// Yes!  Is the raw date for when there?
				Object v = em.get(Constants.PRE_DELETED_WHEN_FIELD + "_raw");
				if (null != v) {
					// Yes!  Return it.
					Date deletedWhen = ((Date) v);
					return deletedWhen;
				}

				try {
					v = em.get(key);
					long l = Long.parseLong((String) v);
					Date deletedWhen = new Date(l);
					return deletedWhen;
				}
				catch (Exception e) {/* Ignore. */}
			}
		}

		// If we get here, we didn't handle this as a trash field.
		// Simply return what's there.
		return em.get(key);
	}

	/**
	 * Returns information about the teams of a specific user.
	 * 
	 * @param bs
	 * @param userId 
	 * 
	 * @return
	 */
	public static List<TeamInfo> getTeams(HttpServletRequest request, AllModulesInjected bs, Long userId) {
		// Allocate an ArrayList<TeamInfo> to hold the teams.
		ArrayList<TeamInfo> reply = new ArrayList<TeamInfo>();

		// Scan the teams the current user is a member of...
		List<Map> myTeams = bs.getBinderModule().getTeamMemberships( userId,
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD,Constants.ENTITY_PATH,Constants.TITLE_FIELD,Constants.ENTITY_FIELD));
		for (Iterator<Map> myTeamsIT = myTeams.iterator(); myTeamsIT.hasNext(); ) {
			// ...adding a TeamInfo for each to the reply list.
			Map myTeam = myTeamsIT.next();
			TeamInfo ti = new TeamInfo();
			ti.setBinderId(   ((String) myTeam.get(      Constants.DOCID_FIELD        )));
			ti.setEntityPath( ((String) myTeam.get(      Constants.ENTITY_PATH   )));
			ti.setPermalink(  PermaLinkUtil.getPermalink( request, myTeam ));
			ti.setTitle(      ((String) myTeam.get(      Constants.TITLE_FIELD         )));
			reply.add(ti);
		}
		
		// If there any teams being returned...
		if (!(reply.isEmpty())) {
			// ...sort them.
			Collections.sort(reply, new TeamInfoComparator());
		}
		
		// If we get here, reply refers to the ArrayList<TeamInfo> of
		// the teams the current user is a member of.  Return it.
		return reply;
	}
	
	/**
	 * Returns information about the teams of a specific user
	 * 
	 * @param bs
	 * @param userId 
	 * 
	 * @return
	 */
	public static List<Long> getTeamIds(HttpServletRequest request, AllModulesInjected bs, Long userId) {
		// Allocate an List<Long> to hold the team IDs.
		List<Long> reply = new ArrayList<Long>();
		
		List<TeamInfo> tiList = getTeams(request, bs, userId);
		for (TeamInfo ti:  tiList) {
			reply.add(Long.parseLong(ti.getBinderId()));
		}
		
		// If we get here, reply refers to the List<Long> of the teams
		// the user is a member of.  Return it.
		return reply;
	}
	
	/**
	 * Returns a TelemetrySettingsRpcResponseData object with the
	 * current telemetry settings.
	 *  
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static TelemetrySettingsRpcResponseData getTelemetrySettings(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			ZoneModule zm = bs.getZoneModule();
			Long defaultZoneId = zm.getZoneIdByVirtualHost(null);	// null -> Returns default zone ID.
			ZoneConfig zc = zm.getZoneConfig(defaultZoneId);
			boolean telemetryTier1Enabled = zc.getTelemetryEnabled();
			Boolean telemetryTier2Enabled = zc.getTelemetryTier2Enabled();
			
			TelemetryService ts = ((TelemetryService) SpringContextUtil.getBean("telemetryService"));
			byte[] telemetryData = ts.getLatestTelemetryData(true);
			String telemetryDataUrl;
			if (null == telemetryData)
			     telemetryDataUrl = null;	// null -> Nothing to download.
			else telemetryDataUrl = WebUrlUtil.getTelemetryDataUrl(request, WebKeys.ACTION_READ_FILE);
			
			return
				new TelemetrySettingsRpcResponseData(
					telemetryTier1Enabled,
					((null == telemetryTier2Enabled) ?
						true                         :
						telemetryTier2Enabled.booleanValue()),
					telemetryDataUrl);
		}
		
		catch(Exception ex) {
			GwtLogHelper.error(m_logger, "GwtServerHelper.getTelemetrySettings( SOURCE EXCEPTION ):  ", ex);
			throw GwtLogHelper.getGwtClientException(ex);				
		}
	}
	
	/**
	 * Return a list of all the time zones
	 */
	public static GwtTimeZones getTimeZones( HttpServletRequest request )
	{
		GwtTimeZones timeZones;
		
		timeZones = new GwtTimeZones();
		timeZones.setListOfTimeZones( TimeZoneHelper.getTimeZoneIdDisplayStrings( getCurrentUser() ) );
		
		return timeZones;
	}
	
	/**
	 * Returns a List<String> of the user ID's of the people the
	 * current user is tracking.
	 * 
	 * @return
	 */
	public static List<String> getTrackedPeople(AllModulesInjected bs) {
		// Return the IDs of the people the current user is tracking.
		Long userId = getCurrentUserId();
		return SearchUtils.getTrackedPeopleIds(bs, userId);
	}
	
	/**
	 * Returns a List<String> of the binder ID's of the places the
	 * current user is tracking.
	 * 
	 * @return
	 */
	public static List<String> getTrackedPlaces(AllModulesInjected bs) {
		Long userId = getCurrentUserId();
		return SearchUtils.getTrackedPlacesIds(bs, userId);
	}

	/**
	 * Returns a BooleanRpcResponseData contain true if the logged in
	 * user has rights to manage tags on the given binder and false
	 * otherwise.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData getCanManageBinderTags(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			BinderModule bm = bs.getBinderModule();
			Binder binder = bm.getBinder(binderId);
			return new BooleanRpcResponseData(bm.testAccess(binder, BinderOperation.manageTag));
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtServerHelper.getCanManageBinderTags( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Using a search query, returns a List<BinderData> containing
	 * the sorted list the children of a given binder.
	 */
	private static List<BinderData> getChildBinderData(AllModulesInjected bs, Binder binder, BucketInfo bi) {
		String pageTuple;
		if (null == bi) {
			pageTuple = "";
		}
		else {
			pageTuple = (bi.getBucketPage() + "//" + bi.getBucketPageTuple());
			String binderIdText = bi.getBucketId();
			Long binderId;
			int i = binderIdText.indexOf(".");
			if (0 <= i) {
				binderId = Long.valueOf(binderIdText.substring(0, i));
			}
			else {
				binderId = Long.valueOf(binderIdText);
			}
			binder = bs.getBinderModule().getBinder(binderId);
		}
		
		List<BinderData> reply = new ArrayList<BinderData>();
		Document wsTree = bs.getBinderModule().getDomBinderTree(
			binder.getId(), 
			// Binder bottom, boolean checkChildren, AllModulesInjected bs, String key, String page
			new WsDomTreeBuilder(
				binder,	//
				true,	// true -> childChildren
				bs,		//
				"",		//   "" -> No key
				pageTuple),	//
			1);			//    1 -> Levels.
		
		Element wsRoot = wsTree.getRootElement();
		Iterator childIT = wsRoot.selectNodes("./" + DomTreeBuilder.NODE_CHILD).iterator();
		while (childIT.hasNext()) {
			BinderData bbd = new BinderData();
			Element childElement = ((Element) childIT.next());
			String type = childElement.attributeValue("type");
			if (type.equals("range")) {
				BucketInfo childBI = new BucketInfo();
				childBI.setBucketId(       childElement.attributeValue("id"       ));
				childBI.setBucketPage(     childElement.attributeValue("page"     ));
				childBI.setBucketPageTuple(childElement.attributeValue("pageTuple"));
				childBI.setBucketTitle(    childElement.attributeValue("title"    ));
				childBI.setBucketTuple1(   childElement.attributeValue("tuple1"   ));
				childBI.setBucketTuple2(   childElement.attributeValue("tuple2"   ));				
				bbd.setBucketInfo(childBI);
			}
			else {
				bbd.setBinderId(Long.parseLong(childElement.attributeValue("id")));
			}
			reply.add(bbd);
		}
		return reply;
	}
	
	private static List<BinderData> getChildBinderData(AllModulesInjected bs, Binder binder) {
		return getChildBinderData(bs, binder, null);
	}
	
	private static List<BinderData> getChildBinderData(AllModulesInjected bs, BucketInfo bi) {
		return getChildBinderData(bs, null, bi);
	}

	/**
	 * Returns a GetTopLevelEntryIdRpcResponseData object contain the
	 * top level EntityId of a folder entry.  If the given EntityId is
	 * not a folder or is not a comment, the top level EntityId
	 * returned will be null.
	 * 
	 * @param bs
	 * @param request
	 * @param eid
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GetTopLevelEntryIdRpcResponseData getTopLevelEntryId(AllModulesInjected bs, HttpServletRequest request, EntityId feEID) throws GwtTeamingException {
		try {
			// Is the given EntityId that of a folder entry?
			EntityId reply = null;
			if ((null != feEID) && feEID.isFolderEntry()) {
				// Yes!  Is it a comment entry?
				FolderEntry fe = bs.getFolderModule().getEntry(feEID.getBinderId(), feEID.getEntityId());
				if (!(fe.isTop())) {
					// Yes!  Return the EntityId of its top entry.
					fe = fe.getTopEntry();
					reply = new EntityId(fe.getParentBinder().getId(), fe.getId(), EntityId.FOLDER_ENTRY);
				}
			}
			
			// If we get here, reply refers to the EntityId of the
			// given entry's top level entry if it was a comment and is
			// false otherwise.  Wrap it in a
			// GetTopLevelEntryIdRpcResponseData object and return
			// that.
			return new GetTopLevelEntryIdRpcResponseData(reply);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(
				m_logger,
				ex,
				"GwtServerHelper.getTopLevelEntryId( SOURCE EXCEPTION ):  ");
			}
	}
	
	/**
	 * Returns a List<TopRankedInfo> of the top ranked items from the
	 * most recent search.
	 *
	 * @param bs,
	 * @param request
	 * 
	 * @return
	 */
	public static List<TopRankedInfo> getTopRankedFromCache(AllModulesInjected bs, HttpServletRequest request) {
		// Allocate an ArrayList to return the top ranked items in.
		ArrayList<TopRankedInfo> triList = new ArrayList<TopRankedInfo>();
		
		// If we can't access the HttpSession...
		HttpSession hSession = getCurrentHttpSession();
		if (null == hSession) {
			// ...we can't access the cached top ranked items to build
			// ...the list from.  Bail.
			GwtLogHelper.debug(m_logger, "GwtServerHelper.getTopRanked( 'Could not access the current HttpSession' )");
			return triList;
		}

		// If there aren't any cached top ranked items... 
		GwtUISessionData tabsObj = ((GwtUISessionData) hSession.getAttribute(GwtUIHelper.CACHED_TOP_RANKED_PEOPLE_KEY));
		List<Map> peopleList = ((List<Map>) tabsObj.getData());
		int people = ((null == peopleList) ? 0 : peopleList.size());
		
		tabsObj = ((GwtUISessionData) hSession.getAttribute(GwtUIHelper.CACHED_TOP_RANKED_PLACES_KEY));
		List<Map> placesList = ((List<Map>) tabsObj.getData());
		int places = ((null == placesList) ? 0 : placesList.size());
		
		if (0 == (people + places)) {
			// ...we can't build a list.  Bail.
			GwtLogHelper.debug(m_logger, "GwtServerHelper.getTopRanked( 'Could not access any cached items' )");
			return triList;
		}
		
		// Scan the top ranked people...
		TopRankedInfo tri;
		for (int i = 0; i < people; i += 1) {
			// ...extract the Principal and reference count from this
			// ...person... 
			Map       person = peopleList.get(i);
			Principal user   = ((Principal) person.get(WebKeys.USER_PRINCIPAL));
			if (null == user) {
				continue;
			}
			Integer refCount = ((Integer) person.get(WebKeys.SEARCH_RESULTS_COUNT     ));
			String  css      = ((String)  person.get(WebKeys.SEARCH_RESULTS_RATING_CSS));
			
			// ...use them to construct a TopRankedInfo object...
			tri = new TopRankedInfo();
			tri.setTopRankedType(TopRankedType.PERSON);
			tri.setTopRankedName(Utils.getUserTitle(user));
			tri.setTopRankedPermalinkUrl(PermaLinkUtil.getPermalink(request, user));
			tri.setTopRankedRefCount((null == refCount) ? 0 : refCount.intValue());
			tri.setTopRankedCSS(css);

			// ...and add it to the list.
			triList.add(tri);
		}
		
		// Scan the top ranked places...
		for (int i = 0; i < places; i += 1) {
			// ...extract the Binder and reference count from this
			// ...place... 
			Map    place  = placesList.get(i);
			Binder binder = ((Binder) place.get(WebKeys.BINDER));
			if (null == binder) {
				continue;
			}
			Integer refCount = ((Integer) place.get(WebKeys.SEARCH_RESULTS_COUNT     ));
			String  css      = ((String)  place.get(WebKeys.SEARCH_RESULTS_RATING_CSS));

			// ...use them to construct a TopRankedInfo object...
			tri = new TopRankedInfo();
			tri.setTopRankedType(TopRankedType.PLACE);
			tri.setTopRankedName(binder.getTitle());
			tri.setTopRankedHoverText(binder.getPathName());
			tri.setTopRankedPermalinkUrl(PermaLinkUtil.getPermalink(request, binder));
			tri.setTopRankedRefCount((null == refCount) ? 0 : refCount.intValue());
			tri.setTopRankedCSS(css);

			// ...and add it to the list.
			triList.add(tri);
		}

		// If we get here, triList refers to an List<TopRankedInfo> of
		// the top ranked items.  Return it.
		return triList;
	}
	
	/**
	 * Return the user access configuration
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 */
	public static UserAccessConfig getUserAccessConfig(AllModulesInjected bs, HttpServletRequest request) {
		// Check for guest access.
		UserAccessConfig     config     = new UserAccessConfig();
		AuthenticationConfig authConfig = bs.getAuthenticationModule().getAuthenticationConfig();
		config.setAllowGuestAccess(authConfig.isAllowAnonymousAccess());
		config.setGuestReadOnly(   authConfig.isAnonymousReadOnly()   );

		// Check for download and web access.
		AdminModule adminModule = bs.getAdminModule();
		config.setAllowDownload( adminModule.isDownloadEnabled() );
		config.setAllowWebAccess(adminModule.isWebAccessEnabled());

		// Are we running with a Novell or Kablink product?
		if (ReleaseInfo.isLicenseRequiredEdition()) {
			// Novell!  Does the license allow external users?
			if (LicenseChecker.isAuthorizedByLicense("com.novell.teaming.ExtUsers")) {
				// Yes!
				ZoneModule zoneModule = bs.getZoneModule();
				ZoneConfig zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
				OpenIDConfig openIdConfig = zoneConfig.getOpenIDConfig();
				boolean allowOpenIdAuth = zoneConfig.isExternalUserEnabled() && openIdConfig.isAuthenticationEnabled();
				config.setAllowExternalUsersViaOpenID( allowOpenIdAuth ); 
				config.setAllowExternalUsersSelfReg( openIdConfig.isSelfProvisioningEnabled() );
			}
		}
		else {
			// Kablink!  Self registration of internal users is only
			// found in the Kablink version.  Is self registration allowed?
			config.setAllowSelfReg(authConfig.isAllowSelfRegistration());
		}
		
		return config;
	}
	
	/**
	 * Returns the URL for a principal's avatar.
	 * 
	 * @param bs
	 * @param request
	 * @param p
	 * 
	 * @return
	 */
	public static String getPrincipalAvatarUrl(AllModulesInjected bs, HttpServletRequest request, Principal p) {
		return getAvatarUrlImpl(bs, request, p, null);
	}

	/**
	 * Returns the URL for a user's avatar.
	 * 
	 * @param bs
	 * @param request
	 * @param u
	 * 
	 * @return
	 */
	public static String getUserAvatarUrl(AllModulesInjected bs, HttpServletRequest request, User u) {
		return getAvatarUrlImpl(bs, request, null, u);
	}

	/*
	 * Returns the URL for a principal's avatar.
	 */
	private static String getAvatarUrlImpl(AllModulesInjected bs, HttpServletRequest request, Principal p, User u) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.getAvatarUrlImpl()");
		try {
			// Can we access any avatars for the Principal or User?
			String reply = null;
			ProfileAttribute pa;
			try {
				if (null == p)
				     pa = GwtProfileHelper.getProfileAvatars(request,     u, true);	// true -> Return the...
				else pa = GwtProfileHelper.getProfileAvatars(request, bs, p, true);	// ...first value only.
			}
			catch (Exception ex) {
				pa = null;
			}
			List<ProfileAttributeListElement> paValue = ((null == pa) ? null : ((List<ProfileAttributeListElement>) pa.getValue()));
			if (MiscUtil.hasItems(paValue)) {
				// Yes!  We'll use the first one as the URL.  Does it
				// have a URL?
				ProfileAttributeListElement paValueItem = paValue.get(0);
				reply = GwtProfileHelper.fixupAvatarUrl(paValueItem.getValue().toString());
			}
			
			// If we get here, reply refers to the user's avatar URL or is
			// null.  Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Return a GwtPrincipalFileSyncAppConfig object that holds the
	 * file sync app configuration data
	 * 
	 * @param bs
	 * @param principalId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static GwtPrincipalFileSyncAppConfig getPrincipalFileSyncAppConfig(AllModulesInjected bs, Long principalId) throws GwtTeamingException {
		try {
			PrincipalDesktopAppsConfig pConfig = bs.getProfileModule().getPrincipalDesktopAppsConfig(principalId);
			
			GwtPrincipalFileSyncAppConfig reply = new GwtPrincipalFileSyncAppConfig();
			boolean useDefault = pConfig.getUseDefaultSettings();
			reply.setUseGlobalSettings(useDefault);
			if (!useDefault) {
				reply.setIsFileSyncAppEnabled(pConfig.getIsFileSyncAppEnabled());
				reply.setAllowCachePwd(       pConfig.getAllowCachePwd()       );
			}
			
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					ex,
					"GwtServerHelper.getPrincipalFileSyncAppConfig( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Return a PrincipalInfoRpcResponseData object that holds
	 * information about the given principal.
	 * 
	 * @param bs
	 * @param request
	 * @param principalId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static PrincipalInfoRpcResponseData getPrincipalInfo(AllModulesInjected bs, HttpServletRequest request, Long principalId) throws GwtTeamingException {
		try {
			// Can we resolve the Principal?
			PrincipalInfoRpcResponseData reply;
			List<Long> pIds = new ArrayList<Long>();
			pIds.add(principalId);
			List<Principal> principals = ResolveIds.getPrincipals(pIds);
			if ((null != principals) && (!(principals.isEmpty()))) {
				// Yes!  Use it to construct a
				// PrincipalInfoRpcResponseData to return.
				String pName;
				EntityId eId;
				Principal p = principals.get(0);
				if (p instanceof GroupPrincipal) {
					pName = p.getTitle();
					eId   = new EntityId(principalId, EntityId.GROUP);
				}
				else {
					User pUser = ((User) p);
					pName = Utils.getUserTitle(pUser);
					if (!(MiscUtil.hasString(pName))) {
						pName = pUser.getName(); 
					}
					eId   = new EntityId(principalId, EntityId.USER);
				}
				reply = new PrincipalInfoRpcResponseData(pName, eId);
			}
			
			else {
				// No, we couldn't resolve the principal!  Return an
				// error to that affect.
				String[] patch = new String[]{String.valueOf(principalId)};
				reply = new PrincipalInfoRpcResponseData(NLT.get("get.principal.info.error.unresolved", patch));
			}

			// If we get here, reply refers to an
			// PrincipalInfoRpcResponseData with information about the
			// Principal or an error.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					ex,
					"GwtServerHelper.getPrincipalInfo( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Given a user ID, returns the corresponding User object.
	 * 
	 * @param bs
	 * @param userId
	 * 
	 * @return
	 */
	public static User getUserFromId(AllModulesInjected bs, Long userId) {
		User reply = null;
		Principal p = bs.getProfileModule().getEntry(userId);
		if (null != p) {
			p = Utils.fixProxy(p);
			if (p instanceof User) {
				reply = ((User) p);
			}
		}
		return reply;
	}

	/**
	 * Returns sharing rights information about the system and a list
	 * of users, based on their IDs.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static UserSharingRightsInfoRpcResponseData getUserSharingRightsInfo(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) throws GwtTeamingException {
		try {
			// Create the UserSharingRightsInfoRpcResponseData to return.
			UserSharingRightsInfoRpcResponseData reply = new UserSharingRightsInfoRpcResponseData();

			// Are there any work area function memberships configured
			// on the zone?
	    	ZoneConfig							zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			AdminModule							am         = bs.getAdminModule();
			WorkspaceModule						wm         = bs.getWorkspaceModule();
			List<WorkAreaFunctionMembership>	wafmList   = am.getWorkAreaFunctionMemberships(zoneConfig);
			if (MiscUtil.hasItems(wafmList)) {
				// Yes!  Scan them.
				for (WorkAreaFunctionMembership wafm:  wafmList) {
					// Is this an internal function that has members?
					String fiId = am.getFunction(wafm.getFunctionId()).getInternalId();
					if (MiscUtil.hasString(fiId) && MiscUtil.hasItems(wafm.getMemberIds())) {
						// Yes!  Check it for being one of the sharing
						// functions.
						if      (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_EXTERNAL_SHARING_INTERNALID)) reply.setExternalEnabled(   true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_FORWARD_SHARING_INTERNALID))  reply.setForwardingEnabled( true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_INTERNAL_SHARING_INTERNALID)) reply.setInternalEnabled(   true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_PUBLIC_SHARING_INTERNALID))   reply.setPublicEnabled(     true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_LINK_SHARING_INTERNALID))     reply.setPublicLinksEnabled(true);

						// Once we set all the flags...
						if (reply.allFlagsSet()) {
							// ...we can quit looking.
							break;
						}
					}
				}
			}
			
			// We're we given a single user whose sharing rights are
			// being requested?
			if ((null != userIds) && (1 == userIds.size())) {
				// Yes!  Can we resolve that to a User?
				Long						wsId  = null;
				List<Principal>				pList = ResolveIds.getPrincipals(userIds);
				PerEntityShareRightsInfo	psri  = new PerEntityShareRightsInfo();
				if (MiscUtil.hasItems(pList)) {
					Principal p = pList.get(0);
					if (p instanceof UserPrincipal) {
						// Yes!  Does that user have a workspace ID?
						User user = ((User) p);
						wsId = user.getWorkspaceId();
						if (null == wsId) {
							// No!  By default, everybody will have all
							// the rights set from the create user
							// template.
							psri.setAllowExternal(   true);
							psri.setAllowForwarding( true);
							psri.setAllowInternal(   true);
							psri.setAllowPublic(     true);
							psri.setAllowPublicLinks(true);
						}
						
						else {
							// Yes, that user has a workspace ID!  Are
							// there any work area function memberships
							// configured on that workspace?
							wafmList = am.getWorkAreaFunctionMemberships(wm.getWorkspace(wsId));
							if (MiscUtil.hasItems(wafmList)) {
								// Yes!  Scan them.
								for (WorkAreaFunctionMembership wafm:  wafmList) {
									// Is this an internal function
									// that has owner as a member?
									String		fiId      = am.getFunction(wafm.getFunctionId()).getInternalId();
									Set<Long>	memberIds = wafm.getMemberIds();
									if (MiscUtil.hasString(fiId) && MiscUtil.hasItems(memberIds) && memberIds.contains(ObjectKeys.OWNER_USER_ID)) {
										// Yes!  Check for it being one
										// of the sharing functions.
										if      (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID))     psri.setAllowExternal(   true);
										else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID))      psri.setAllowForwarding( true);
										else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID))     psri.setAllowInternal(   true);
										else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID))       psri.setAllowPublic(     true);
										else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_LINKS_INTERNALID)) psri.setAllowPublicLinks(true);
										
										// Once we set all the flags...
										if (psri.allFlagsSet()) {
											// ...we can quit looking.
											break;
										}
									}
								}
							}
						}
					}
				}

				// Add the per user sharing rights to the reply.
				reply.addUserRights(userIds.get(0), psri);
			}

			// If we get here, reply refers to the
			// UserSharingRightsInfoRpcResponseData containing the
			// requested sharing rights information.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	/**
	 * Returns the title for a target user, based on their ID, that we
	 * should display to the current user based on their access to the
	 * target user.
	 * 
	 * @param pm
	 * @param isOtherUserAccessRestricted
	 * @param targetUserId
	 * @param defaultTitle
	 * 
	 * @return
	 */
	public static String getUserTitle(ProfileModule pm, boolean isOtherUserAccessRestricted, String targetUserId, String defaultTitle) {
		// Can the current user only see other users that are common
		// group members?
		String reply = defaultTitle;
		if (isOtherUserAccessRestricted) {
			// Yes!  Can we resolve the target user's ID?  (This will
			// take care of securing their title, if necessary.)
			List<String> targetUserIdList = new ArrayList<String>();
			targetUserIdList.add(targetUserId);
			List targetUsersList = ResolveIds.getPrincipals(targetUserIdList, false);
			if (MiscUtil.hasItems(targetUsersList)) {
				// Yes!  Return the title from the user we resolved to.
				// This will either be the actual title, if the current
				// user has rights to see it, or the secured title.
				User targetUser = ((User) targetUsersList.get(0));
				reply = targetUser.getTitle();
			}
			
			else {
				// No, we couldn't resolve the target user ID!  Display
				// the default secured title.
				reply = NLT.get("user.redacted.title");
			}
		}
		
		// If we get here, reply refers the title the current user
		// should display for the target user in question.  Return it.
		return reply;
	}
	
	/**
	 * Returns a user's Workspace, if one exists.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static Workspace getUserWorkspace(User user) {
		// If we can access this user's workspace ID...
		Workspace reply = null;
		Long userWSId = ((null == user) ? null : user.getWorkspaceId());
		if (null != userWSId) {
			// ...and we can access the Workspace from that...
			Set userWSSet = ResolveIds.getBinders(String.valueOf(userWSId));
			if (!(userWSSet.isEmpty())) {
				// ...return it.
				reply = ((Workspace) (userWSSet.iterator().next()));
			}
		}
		
		// If we get here, reply refers to the user's workspace if it
		// exists or is null.  Return it.
		return reply;
	}
	
	/**
	 * Return a view file URL that can be used to view an entry's file
	 * as HTML.
	 * 
	 * @param request
	 * @param vfi
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static String getViewFileUrl(HttpServletRequest request, ViewFileInfo vfi) throws GwtTeamingException {
		try {
			StringBuffer webUrl = new StringBuffer(WebUrlUtil.getServletRootURL(request) + WebKeys.ACTION_VIEW_FILE);
			webUrl.append(org.kablink.teaming.util.Constants.QUESTION  + WebKeys.URL_BINDER_ID   + org.kablink.teaming.util.Constants.EQUAL + vfi.getEntityId().getBinderId()  );
			webUrl.append(org.kablink.teaming.util.Constants.AMPERSAND + WebKeys.URL_ENTRY_ID    + org.kablink.teaming.util.Constants.EQUAL + vfi.getEntityId().getEntityId()  );
			webUrl.append(org.kablink.teaming.util.Constants.AMPERSAND + WebKeys.URL_ENTITY_TYPE + org.kablink.teaming.util.Constants.EQUAL + vfi.getEntityId().getEntityType());
			webUrl.append(org.kablink.teaming.util.Constants.AMPERSAND + WebKeys.URL_FILE_ID     + org.kablink.teaming.util.Constants.EQUAL + vfi.getFileId()                  );
			webUrl.append(org.kablink.teaming.util.Constants.AMPERSAND + WebKeys.URL_FILE_TIME   + org.kablink.teaming.util.Constants.EQUAL + vfi.getFileTime()                );
			webUrl.append(org.kablink.teaming.util.Constants.AMPERSAND + WebKeys.URL_VIEW_TYPE   + org.kablink.teaming.util.Constants.EQUAL + vfi.getViewType()                );
			return webUrl.toString();
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	/**
	 * Return a base view folder entry URL that can be used directly in the
	 * content panel.
	 * 
	 * @param bs
	 * @param ri
	 * @param binderId
	 * @param entryId
	 * @param invokeShare
	 * @param invokeSubscribe
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static String getViewFolderEntryUrl(AllModulesInjected bs, HttpServletRequest request, Long binderId, Long entryId, boolean invokeShare, boolean invokeSubscribe) throws GwtTeamingException {
		try {
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY);
			if (null == binderId) {
				FolderEntry entry = bs.getFolderModule().getEntry(null, entryId);
				binderId = entry.getParentBinder().getId();
			}
			
			adapterUrl.setParameter(WebKeys.URL_BINDER_ID, String.valueOf(binderId));
			adapterUrl.setParameter(WebKeys.URL_ENTRY_ID,  String.valueOf(entryId ));
			
			if (invokeShare)     adapterUrl.setParameter(WebKeys.URL_INVOKE_SHARE,     "1");
			if (invokeSubscribe) adapterUrl.setParameter(WebKeys.URL_INVOKE_SUBSCRIBE, "1");
			
			return adapterUrl.toString();
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	public static String getViewFolderEntryUrl(AllModulesInjected bs, HttpServletRequest request, Long binderId, Long entryId) throws GwtTeamingException {
		// Always use the initial form of the method.
		return
			getViewFolderEntryUrl(
				bs,
				request,
				binderId,
				entryId,
				false,	// false -> Don't invoke the share     dialog on the entry.
				false);	// false -> Don't invoke the subscribe dialog on the entry.
	}

	/**
	 * Return the "show setting" (show all or show unread) for the "What's new" page.
	 */
	public static ActivityStreamDataType getWhatsNewShowSetting( UserProperties userProperties )
	{
		ActivityStreamDataType showSetting;
		
		showSetting = ActivityStreamDataType.ALL;
		
		// Do we have a user properties?
		if ( userProperties != null )
		{
			Integer property;
			
			property = (Integer) userProperties.getProperty( ObjectKeys.USER_PROPERTY_WHATS_NEW_SHOW_SETTING );
			if ( property != null )
			{
				int value;
				
				value = property.intValue();
				
				if ( value == ActivityStreamDataType.ALL.ordinal() )
					showSetting = ActivityStreamDataType.ALL;
				else if ( value == ActivityStreamDataType.UNREAD.ordinal() )
					showSetting = ActivityStreamDataType.UNREAD;
			}
		}
		
		return showSetting;
	}

	/**
	 * Returns a formatted XML document for displaying somewhere.
	 * 
	 * @param document
	 * 
	 * @return
	 */
    public static String getXmlString(Document document) {
    	String xmlString;
    	try {
    		OutputFormat format = OutputFormat.createPrettyPrint();
			format.setSuppressDeclaration(true);
    		xmlString = XmlFileUtil.writeString(document, format);
    	} catch (Exception ex) {
    		xmlString = document.asXML();
    	}
    	return xmlString;
    }
    
	/**
	 * Imports an iCal into a folder using a URL.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param iCalURL
	 * 
	 * @return
	 */
	public static ImportIcalByUrlRpcResponseData importIcalByUrl(AllModulesInjected bs, HttpServletRequest request, Long folderId, String iCalURL) {
		// Construct the RPC response data to return.
		ImportIcalByUrlRpcResponseData reply = new ImportIcalByUrlRpcResponseData();

		// Can we parse the URL?
		GetMethod getMethod = null;
		try {
			HttpURL hrl = getHttpURL(iCalURL);
			HttpClient httpClient = getHttpClient(hrl);
			getMethod = new GetMethod(hrl.getPathQuery());
			
			// Can we perform the import using the URL?
			int statusCode = httpClient.executeMethod(getMethod);
			if (200 == statusCode) {
				// Yes!  Get the response to the URL as an InputStream.
				InputStream icalInputStream = getMethod.getResponseBodyAsStream();
				
				// Can we parse the data as iCal entries?
				try {
					AttendedEntries attendedEntries = bs.getIcalModule().parseToEntries(folderId, icalInputStream);
					reply.setAddedEntryIds(   attendedEntries.added   );
					reply.setModifiedEntryIds(attendedEntries.modified);
				}
				
				catch (net.fortuna.ical4j.data.ParserException e) {
					// No, we couldn't parse the data as iCal entries!
					reply.setError(FailureReason.PARSE_EXCEPTION, e.getLocalizedMessage());
					GwtLogHelper.error(m_logger, "GwtServerHelper.importIcalByUrl( EXCEPTION:1 ):  ", e);
				}
				
				// Close the input string.
				icalInputStream.close();
			}
			else {
				// No, we couldn't perform the import!
				reply.setError(FailureReason.IMPORT_FAILED, "InvalidUrl");
			}
		}
		
		catch (Exception e) {
			// No, we couldn't parse the URL!
			reply.setError(FailureReason.URL_EXCEPTION, e.getLocalizedMessage());
			GwtLogHelper.error(m_logger, "GwtServerHelper.importIcalByUrl( EXCEPTION:2 ):  ", e);
		}
		
		finally {
			// If we're connected to the URL...
			if (null != getMethod) {
				// ...release the connection.
				getMethod.releaseConnection();
			}
		}

		// If we get here, reply refers to an
		// ImportIcalByUrlRpcResponseData object that contains the
		// results of the import.  Return it.
		return reply;
	}
	
	/**
	 * Returns the WorkspaceType of a binder.
	 * 
	 * @param bs
	 * @param wsId
	 * 
	 * @return
	 */
	public static WorkspaceType getWorkspaceType(AllModulesInjected bs, String wsId) {
		return getWorkspaceType(bs. getBinderModule().getBinder(Long.parseLong(wsId)));
	}
	
	public static WorkspaceType getWorkspaceType(Binder binder) {
		// Is this binder a workspace?
		WorkspaceType reply;
		if (binder instanceof Workspace) {
			// Yes!  Is it a reserved workspace?
			reply = WorkspaceType.OTHER;
			Workspace ws      = ((Workspace) binder);
			String    view    = BinderHelper.getBinderDefaultViewName(binder);
			boolean   hasView = MiscUtil.hasString(view);
			if (ws.isReserved()) {
				// Yes!  Then we can determine its type based on its
				// internal ID.
				if      (hasView && view.equals(VIEW_WORKSPACE_WELCOME))                    reply = WorkspaceType.LANDING_PAGE;
				else if (ws.getInternalId().equals(ObjectKeys.TOP_WORKSPACE_INTERNALID))    reply = WorkspaceType.TOP;
				else if (ws.getInternalId().equals(ObjectKeys.TEAM_ROOT_INTERNALID))        reply = WorkspaceType.TEAM_ROOT;
				else if (ws.getInternalId().equals(ObjectKeys.GLOBAL_ROOT_INTERNALID))      reply = WorkspaceType.GLOBAL_ROOT;
				else if (ws.getInternalId().equals(ObjectKeys.PROFILE_ROOT_INTERNALID))     reply = WorkspaceType.PROFILE_ROOT;
				else if (ws.getInternalId().equals(ObjectKeys.NET_FOLDERS_ROOT_INTERNALID)) reply = WorkspaceType.NET_FOLDERS_ROOT;
			}
			else {
				// No, it isn't a reserved workspace!  Is it a user workspace?
				if (BinderHelper.isBinderUserWorkspace(binder)) {
					// Yes!  Mark it as such.
					reply = WorkspaceType.USER;
				}

				else {
					// No, it isn't a user workspace either!  Can we
					// determine the name of its default view?
					if (hasView) {
						// Yes!  Check for those that we know.
						GwtLogHelper.debug(m_logger, "GwtServerHelper.getWorkspaceType( " + binder.getTitle() + "'s:  'Workspace View' ):  " + view);
						if      (view.equals(VIEW_WORKSPACE_DISCUSSIONS)) reply = WorkspaceType.DISCUSSIONS;
						else if (view.equals(VIEW_WORKSPACE_PROJECT))     reply = WorkspaceType.PROJECT_MANAGEMENT;
						else if (view.equals(VIEW_WORKSPACE_TEAM))        reply = WorkspaceType.TEAM;
						else if (view.equals(VIEW_WORKSPACE_USER))        reply = WorkspaceType.USER;
						else if (view.equals(VIEW_WORKSPACE_WELCOME))     reply = WorkspaceType.LANDING_PAGE;
						else if (view.equals(VIEW_WORKSPACE_GENERIC))     reply = WorkspaceType.WORKSPACE;
						else {
							HashMap model = new HashMap();
							DefinitionHelper.getDefinitions(binder, model, null);		
							Definition entityDef = ((Definition) model.get(WebKeys.DEFAULT_FOLDER_DEFINITION));
							String family = BinderHelper.getFamilyNameFromDef(entityDef);
							if (null != family) {
								if      (family.equals("discussion"))  reply = WorkspaceType.DISCUSSIONS;
								else if (family.equals("landingpage")) reply = WorkspaceType.LANDING_PAGE;
								else if (family.equals("project"))     reply = WorkspaceType.PROJECT_MANAGEMENT;
								else if (family.equals("team"))        reply = WorkspaceType.TEAM;
								else if (family.equals("workspace"))   reply = WorkspaceType.WORKSPACE;
							}
						}
					}					
				}
			}
		}
		else {
			reply = WorkspaceType.NOT_A_WORKSPACE;
		}
		
		if (WorkspaceType.OTHER == reply) {
			GwtLogHelper.debug(m_logger, "GwtServerHelper.getWorkspaceType( 'Could not determine workspace type' ):  " + binder.getPathName());
		}
		return reply;
	}

	/**
	 * Returns true if the given group is the 'All External Users'
	 * group and false otherwise.
	 * 
	 * @param bs
	 * @param group
	 * 
	 * @return
	 */
	public static boolean isAllExternalUsersGroup(AllModulesInjected bs, Group group) {
		String  internalId = group.getInternalId();
		return (MiscUtil.hasString(internalId) && internalId.equalsIgnoreCase(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID));
	}
	
	public static boolean isAllExternalUsersGroup(AllModulesInjected bs, Long groupId) throws GwtTeamingException {
		boolean reply = false;
		try {
			Long allUsersGroupId = Utils.getAllExtUsersGroupId();
			if ((null != allUsersGroupId) && allUsersGroupId.equals(groupId)) {
				reply = true;
			}
		}
		
		catch (Exception ex) {
			GwtLogHelper.error(m_logger, "GwtServerHelper.isAllExternalUsersGroup( 'Could not access the group' ):  " + groupId);
			reply = false;
		}
		
		// If we get here the group is not the "all external users"
		// group.
		return reply;
	}
	
	/**
	 * Returns true if the given ID is the 'all users' or 'all external
	 * user' group and false otherwise.
	 * 
	 * @param bs
	 * @param groupId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static boolean isAllInternalUsersGroup(AllModulesInjected bs, Long groupId) throws GwtTeamingException {
		try {
			Long allUsersGroupId = Utils.getAllUsersGroupId();
			if ((null != allUsersGroupId) && allUsersGroupId.equals(groupId)) {
				return true;
			}
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		// If we get here the group is not the 'all internal users'
		// group.  Return false.
		return false;
	}
	
	/**
	 * Returns true if the given ID is the 'all users' or 'all external
	 * user' group and false otherwise.
	 * 
	 * @param bs
	 * @param groupId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static IsAllUsersGroupRpcResponseData isAllUsersGroup(AllModulesInjected bs, Long groupId) throws GwtTeamingException {
		return new IsAllUsersGroupRpcResponseData(isAllExternalUsersGroup(bs, groupId), isAllInternalUsersGroup(bs, groupId));
	}
	
	/**
	 * Return whether dynamic group membership is allowed.  It is allowed if the ldap
	 * configuration has a value set for "LDAP attribute that uniquely identifies a user or group"
	 */
	public static boolean isDynamicGroupMembershipAllowed( AllModulesInjected ami )
	{
		return ami.getLdapModule().isGuidConfigured();
	}
	
	/*
	 * Returns true if the given entity is remote (i.e., located in
	 * other than personal/adHoc storage) and false otherwise.
	 */
	public static boolean isEntityRemote(AllModulesInjected bs, EntityId eid, ErrorListRpcResponseData errList) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.isEntityRemote()");
		try {
			// Is this an entry?
			FolderModule fm    = bs.getFolderModule();
			boolean      reply = true;
			if (eid.isEntry()) {
				// Yes!  Can we access it?
				FolderEntry fe = fm.getEntry(eid.getBinderId(), eid.getEntityId());
				if (null != fe) {
					// Yes!  Is it in remote storage?
					Folder f = fe.getParentFolder();
					reply = (f.isMirrored() || f.isAclExternallyControlled());
				}
			}
			
			// No, it isn't an entry!  Is it a folder?
			else if (eid.isFolder()) {
				// Yes!  Can we access it?
				Folder f = fm.getFolder(eid.getEntityId());
				if (null != f) {
					// Yes!  Is it in remote storage?
					reply = (f.isMirrored() || f.isAclExternallyControlled());
					if (!reply) {
						// Does it contain nested folders that are in
						// remote storage?
						reply = SearchUtils.binderHasNestedRemoteFolders(
							bs,
							eid.getEntityId());
					}
				}
			}
			
			else {
				// No, it isn't a folder either!  Everything else is in
				// personal storage.
				reply = false;
			}

			// If we get here, reply is true if the entity is in remote
			// storage and false otherwise.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Return true if the given group can have external users/groups as members of the group
	 */
	public static Boolean isExternalMembersAllowed( AllModulesInjected ami, Long groupId )
	{
		Principal principal;
		
		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			Group group;
			
			group = (Group) principal;
			return !group.getIdentityInfo().isInternal();
		}

		return Boolean.FALSE;
	}

	/**
	 * Returns true if a family string represents a 'file' entity or
	 * false otherwise.
	 * 
	 * @param family
	 * 
	 * @return
	 */
	public static boolean isFamilyFile(String family) {
		// Do we have a family string?
		if (MiscUtil.hasString(family)) {
			// Yes!  Is it file?
			if (family.equals(Definition.FAMILY_FILE)) {
				// Yes!  Return true.
				return true;
			}

			// No, it's not file!  If we're not in simple Filr mode,
			// is it photo?
			if ((!(Utils.checkIfFilr())) &&
					family.equals(Definition.FAMILY_PHOTO)) {
				// Yes!  Return true.
				return true;
			}
		}
		
		// If we get here, the family string doesn't refer to what we
		// consider a file entity.  Return false.
		return false;
	}

	/**
	 * Return true if the given group's membership is dynamic.
	 */
	public static Boolean isGroupMembershipDynamic( AllModulesInjected ami, Long groupId )
	{
		Principal principal;
		
		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			Group group;
			
			group = (Group) principal;
			return group.isDynamic();
		}

		return Boolean.FALSE;
	}

	/*
	 * Returns true if a Long is in a List<Long> and false otherwise.
	 */
	private static boolean isLongInList(Long l, List<Long> lList) {
		if ((null != l) && (null != lList)) {
			long lv = l.longValue();
			for (Iterator<Long> lIT = lList.iterator(); lIT.hasNext(); ) {
				if (lv == lIT.next().longValue()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Return true if the presence service is enabled and false
	 * otherwise.
	 * 
	 * @param
 	 */
	public static boolean isPresenceEnabled()
	{
		PresenceManager presenceService = ((PresenceManager) SpringContextUtil.getBean("presenceService"));
		boolean reply = ((null != presenceService) ? presenceService.isEnabled() : false);
		return reply;
	}

	/*
	 * Returns true if two List<Long>'s contain different values and
	 * false otherwise.
	 */
	private static boolean longListsDiffer(List<Long> list1, List<Long> list2) {
		// If the lists differ in size...
		int c1 = ((null == list1) ? 0 : list1.size());
		int c2 = ((null == list2) ? 0 : list2.size());
		if (c1 != c2) {
			// ...they're different.
			return true;
		}
		
		// If they're both empty...
		if (0 == c1) {
			// ...they're the same.
			return false;
		}

		// Scan the Long's in the first list.
		for (Iterator<Long> l1IT = list1.iterator(); l1IT.hasNext(); ) {
			long l1 = l1IT.next().longValue();
			boolean found1in2 = false;
			
			// Scan the Long's in the second list.
			for (Iterator<Long> l2IT = list2.iterator(); l2IT.hasNext(); ) {
				// If we find a Long from the first list in the second
				// list...
				long l2 = l2IT.next().longValue();
				found1in2 = (l1 == l2);
				if (found1in2) {
					// ...quit looking.
					break;
				}
			}
			
			// If we failed to find a Long from the first list in the
			// second list...
			if (!found1in2) {
				// ...the lists are different.
				return true;
			}
		}
		
		// If we get here, the lists are the same.  Return
		// false.
		return false;
	}
	
	/*
	 * Parse the given html and replace any markup with the appropriate url.  For example,
	 * replace {{attachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
	 */
	public static String markupStringReplacementImpl( AllModulesInjected ami, HttpServletRequest request, String binderId, String html, String type ) throws GwtTeamingException
	{
		String newHtml;
		
		newHtml = "";
		if ( html != null && html.length() > 0 )
		{
			try
			{
				Long binderIdL;
				
				binderIdL = new Long( binderId );
				
				if ( binderIdL != null )
				{
					BinderModule binderModule;
					Binder binder;
					
					binderModule = ami.getBinderModule();
					binder = binderModule.getBinder( binderIdL );

					// Parse the given html and replace any markup with the appropriate url.  For example,
					// replace {{atachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
					newHtml = MarkupUtil.markupStringReplacement( null, null, request, null, binder, html, type );
				}
			}
			catch (Exception e)
			{
				throw GwtLogHelper.getGwtClientException(m_logger, e);
			}
		}
		
		return newHtml;
	}
	
	
	/*
	 * Merges the expanded Binder list from the current User's
	 * preferences into those in expandedBindersList.  If the resultant
	 * lists differ, they are written back to the User's preferences
	 */
	private static void mergeBinderExpansions(AllModulesInjected bs, List<Long> expandedBindersList) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// Make sure we have two non-null lists to work with.
		if (null == expandedBindersList)      expandedBindersList      = new ArrayList<Long>();
		if (null == usersExpandedBindersList) usersExpandedBindersList = new ArrayList<Long>();

		// Scan the Binder ID's in the User's expanded Binder's list.
		for (Iterator<Long> lIT = usersExpandedBindersList.iterator(); lIT.hasNext(); ) {
			// Is this Binder ID in the list we were given?
			Long l = lIT.next();
			if (!(isLongInList(l, expandedBindersList))) {
				// No!  Added it to it and force the changes to be
				// written to the User's list.
				expandedBindersList.add(0, l);
			}
		}

		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (longListsDiffer(usersExpandedBindersList, expandedBindersList)) {
			// Yes!  Write it.
			setExpandedBindersList(bs, expandedBindersList);
		}
	}

	/*
	 * Validates that the names of the filters in the
	 * List<FolderFilter> are unique within the
	 * FolderFiltersRpcResponseData.  If they're not, they're made
	 * unique by appending a number to the name until they are.  The
	 * unique FolderFilter's are then added to the appropriate list in
	 * currentFilters.
	 * 
	 * Returns true if any FolderFilter's are added to the
	 * currentFilters and false otherwise.
	 */
	private static boolean mergeFolderFilters(FolderFiltersRpcResponseData currentFilters, List<FolderFilter> filterList, boolean addToGlobal, Long targetFolderId, Long sourceFolderId, ErrorListRpcResponseData errors) {
		// Do we have any filters to process?
		boolean reply = MiscUtil.hasItems(filterList);
		if (reply) {
			// Yes!  Scan them.
			for (FolderFilter filter:  filterList) {
				// What's the unique name for this filter?
				int    tryCount       = 0;
				String baseFilterName = filter.getFilterName();
				String filterName     = baseFilterName;
				while (true) {
					tryCount += 1;
					if (!(currentFilters.filterExists(filterName))) {
						break;
					}
					filterName = (baseFilterName + " (" + tryCount + ")");
				}

				// Do we have to rename this filter?
				if (1 < tryCount) {
					// Yes!  Add a warning to the error list so we can
					// inform the user...
					String key = "saveFilters.warning.duplicateRenamed.";
					if (addToGlobal)
					     key += "global";
					else key += "personal";
					errors.addWarning(NLT.get(key, new String[]{baseFilterName, filterName}));
					
					// ...and save its new name.
					filter.setFilterName(filterName);
				}
		
				// Finally, fix the folder so that it references the
				// target folder instead of the source...
				fixupFilterFolderId(filter, targetFolderId, sourceFolderId);
				
				// ...and add the filter to the appropriate list.
				if (addToGlobal)
				     currentFilters.addGlobalFilter(  filter);
				else currentFilters.addPersonalFilter(filter);
			}
		}
		
		// If we get here, reply is true if we added any filters
		// currentFilters and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Modify the given group
	 */
	public static void modifyGroup(
		AllModulesInjected ami,
		Long groupId,
		String title,
		String desc,
		boolean isMembershipDynamic,
		GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException
	{
		Principal principal;
		GwtServerProfiler gsp;

		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			Map updates;
			String ldapQuery = null;

			if ( isMembershipDynamic && membershipCriteria != null )
				ldapQuery = membershipCriteria.getAsXml();

			updates = new HashMap();
			updates.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
			updates.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
			updates.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_NONE ) );  
			updates.put( ObjectKeys.FIELD_GROUP_DYNAMIC, Boolean.valueOf( isMembershipDynamic ) );
			updates.put( ObjectKeys.FIELD_GROUP_LDAP_QUERY, ldapQuery );
			
			gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - getProfileModule().modifyEntry()" );
			try
			{
				ami.getProfileModule().modifyEntry( groupId, new MapInputData( updates ) );
			}
   			catch ( Exception ex )
   			{
   				throw GwtLogHelper.getGwtClientException(m_logger, ex);
   			}
   			finally
   			{
   				gsp.stop();
   			}
		}
	}

	/**
	 * Modify the membership of the given group
	 */
	public static void modifyGroupMembership(
		AllModulesInjected ami,
		Long groupId,
		boolean isMembershipDynamic,
		List<GwtTeamingItem> membership,
		GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException
	{
		Principal principal;
		GwtServerProfiler gsp;

		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			List<Principal> currentMembers;
			SortedSet<Principal> principals;
			String ldapQuery;
			Group group;

			group = (Group) principal;
			
			// Capture the current membership of the group before it gets modified
			{
				ArrayList<Principal> tmpMembers;
				
				// It seems that group.getMembers() changed and is returning a list of
				// UserPrincipal objects instead of a list of User and Group objects.
				// This is causing problems when we re-index.  See bug 768094.
				currentMembers = new ArrayList<Principal>();
				tmpMembers = new ArrayList<Principal>( group.getMembers() );
				for (Principal nextMember : tmpMembers)
				{
					currentMembers.add( ami.getProfileModule().getEntry( nextMember.getId() ) );
				}
				GwtLogHelper.debug( m_logger, "GwtServerHelper.modifyGroupMembership(), number of members in current group membership: " + String.valueOf( currentMembers.size() ) );
			}
			
			// Is the group membership dynamic?
			if ( isMembershipDynamic )
			{
				// Yes
				
				Set<User> membershipUsers = new HashSet<User>();

				if ( membershipCriteria != null )
				{
					// Execute the ldap query and get the list of members from the results.
					gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroupMembership() - get list of dynamic group members" );
					try
					{
						int maxCount;
						HashSet<User> potentialMemberUsers;
						
						// Get a list of all the potential members.
						potentialMemberUsers = ami.getLdapModule().getDynamicGroupMembers(
																			membershipCriteria.getBaseDn(),
																			membershipCriteria.getLdapFilterWithoutCRLF(),
																			membershipCriteria.getSearchSubtree() );
						
						// Get the maximum number of users that can be in a group.
						maxCount = SPropsUtil.getInt( "dynamic.group.membership.limit", 50000 ); 					
						
						// Is the number of potential members greater than the max number of group members?
						if ( potentialMemberUsers.size() > maxCount )
						{
							int count;

							// Yes, only take the max number of users.
							count = 0;
							for (User user : potentialMemberUsers)
							{
								membershipUsers.add( user );
								++count;
								
								if ( count >= maxCount )
								{
									break;
								}
							}
						}
						else
						{
							// No
							membershipUsers = potentialMemberUsers;
						}
					}
					catch (Exception ex)
					{
						// !!! What to do.
						membershipUsers = new HashSet<User>();
					}
					finally
					{
						gsp.stop();
					}
				}
				else
					membershipUsers = new HashSet<User>();
				
				ldapQuery = membershipCriteria.getAsXml();

		 	    User user = RequestContextHolder.getRequestContext().getUser();
		        Comparator c = new org.kablink.teaming.comparator.PrincipalComparator(user.getLocale());
		       	principals = new TreeSet<Principal>(c);
				principals.addAll(membershipUsers);
			}
			else
			{
				// No
				Set<Long> membershipIds = new HashSet<Long>();

				ldapQuery = null;
				
				// Get a list of all the new membership ids.
				for (GwtTeamingItem nextMember : membership)
				{
					Long id;

					id = null;
					if ( nextMember instanceof GwtUser )
						id = Long.valueOf( ((GwtUser) nextMember).getUserId() );
					else if ( nextMember instanceof GwtGroup )
						id = Long.valueOf( ((GwtGroup) nextMember).getId() );
								
					if ( id != null )
						membershipIds.add( id );
				}
				
				principals = ami.getProfileModule().getPrincipals( membershipIds );
			}

			// Modify the group's membership.
			{
				Map updates;

				GwtLogHelper.debug(m_logger, "GwtServerHelper.modifyGroupMembership(), number of members in new group membership: " + String.valueOf( principals.size() ) );
			
				updates = new HashMap();
				updates.put( ObjectKeys.FIELD_GROUP_DYNAMIC, isMembershipDynamic );
				updates.put( ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, principals );
				updates.put( ObjectKeys.FIELD_GROUP_LDAP_QUERY, ldapQuery );
				
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroupMembership() - getProfileModule().modifyEntry()" );
				try
				{
					ami.getProfileModule().modifyEntry( groupId, new MapInputData( updates ) );
				}
	   			catch ( Exception ex )
	   			{
	   				throw GwtLogHelper.getGwtClientException(m_logger, ex);
	   			}
	   			finally
	   			{
	   				gsp.stop();
	   			}
			}
			
			// Now deal with everyone who was affected
			{
				ArrayList<Long> usersRemovedFromGroup;
				ArrayList<Long> groupsRemovedFromGroup;
				ArrayList<Long> usersAddedToGroup;
				ArrayList<Long> groupsAddedToGroup;
				Map<Long,Principal> changes;

				usersRemovedFromGroup = new ArrayList<Long>();
				usersAddedToGroup = new ArrayList<Long>();
				groupsRemovedFromGroup = new ArrayList<Long>();
				groupsAddedToGroup = new ArrayList<Long>();
				changes = new HashMap<Long,Principal>();

				// Get a list of the users and groups that were removed from the group.
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroupMembership() - create list of users removed from the group." );
				try
				{
					for (Principal p : currentMembers)
					{
						Long oldMemberId;
						boolean found;

						// See if the old member is in the list of new members
						oldMemberId = p.getId();
						found = false;
						for (Principal newMember : principals)
						{
							Long newMemberId;
							
							newMemberId = newMember.getId();
							if ( oldMemberId != null && oldMemberId.equals( newMemberId ) )
							{
								found = true;
								break;
							}
						}
						
						// This principal used to be a member of the group.  Is he still a member?
						if ( found == false )
						{
							// No
							if ( (p instanceof UserPrincipal) || (p instanceof User) )
								usersRemovedFromGroup.add( oldMemberId );
							else if ( (p instanceof GroupPrincipal) || (p instanceof Group) )
								groupsRemovedFromGroup.add( oldMemberId );
							
							changes.put( oldMemberId, p );
						}
					}
				}
				finally
				{
					gsp.stop();
					GwtLogHelper.debug(m_logger, "GwtServerHelper.modifyGroupMembership(), number of users removed from group: " + String.valueOf( usersRemovedFromGroup.size() ) );
					GwtLogHelper.debug(m_logger, "GwtServerHelper.modifyGroupMembership(), number of groups removed from group: " + String.valueOf( groupsRemovedFromGroup.size() ) );
				}
				
				// Get a list of the users and groups that were added to the group.
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroupMembership() - create list of users added to the group." );
				try
				{
					for (Principal p : principals)
					{
						Long newMemberId;
						boolean found;
						
						// See if the new member was already a member of the group.
						newMemberId = p.getId();
						found = false;
						for (Principal oldMember : currentMembers)
						{
							Long oldMemberId;
							
							oldMemberId = oldMember.getId();
							if ( newMemberId != null && newMemberId.equals( oldMemberId ) )
							{
								found = true;
								break;
							}
						}
						
						// Was this principal already a member of the group?
						if ( found == false )
						{
							// No
							if ( (p instanceof UserPrincipal) || (p instanceof User) )
								usersAddedToGroup.add( newMemberId );
							else if ( (p instanceof GroupPrincipal) || (p instanceof Group) )
								groupsAddedToGroup.add( newMemberId );
							
							changes.put( newMemberId, p );
						}
					}
				}
				finally
				{
					gsp.stop();
					GwtLogHelper.debug( m_logger, "GwtServerHelper.modifyGroupMembership(), number of users added to group: " + String.valueOf( usersAddedToGroup.size() ) );
					GwtLogHelper.debug( m_logger, "GwtServerHelper.modifyGroupMembership(), number of groups added to group: " + String.valueOf( groupsAddedToGroup.size() ) );
				}

				GwtLogHelper.debug( m_logger, "GwtServerHelper.modifyGroupMembership(), number of changes to the group: " + String.valueOf( changes.size() ) );

				// Update the disk quotas and file size limits for users that were added
				// or removed from the group.
				Utils.updateDiskQuotasAndFileSizeLimits(
														ami.getProfileModule(),
														group,
														usersAddedToGroup,
														usersRemovedFromGroup,
														groupsAddedToGroup,
														groupsRemovedFromGroup );

				// After changing the group membership, re-index any user that was added or deleted
				Utils.reIndexPrincipals( ami.getProfileModule(), changes );
			}
		}
	}
	
	/**
	 * Runs the XSS checker on the GWT RPC commands that require
	 * checking.
	 * 
	 * Note:  Although we don't check everything we checked in
	 *   Evergreen and earlier, we do check what matters.  Things
	 *   we aren't checking that we used to include IDs (e.g.,
	 *   binderId's, entryId's, ...)
	 * 
	 * @param cmd
	 */
	public static void performXSSCheckOnRpcCmd(VibeRpcCmd cmd) {
		// What RPC command are we XSS checking?
		VibeRpcCmdType cmdEnum = VibeRpcCmdType.getEnum( cmd.getCmdType() );
		switch (cmdEnum) {
		// The following commands require XSS checks.
		case CREATE_GROUP: {
			CreateGroupCmd cgCmd = (CreateGroupCmd) cmd;
			String value = cgCmd.getName();
			if (MiscUtil.hasString(value)) {
				cgCmd.setName(StringCheckUtil.check(value));
			}
			value = cgCmd.getDesc();
			if (MiscUtil.hasString(value)) {
				cgCmd.setDesc(StringCheckUtil.checkHtml(value));
			}
			value = cgCmd.getTitle();
			if (MiscUtil.hasString(value)) {
				cgCmd.setTitle( StringCheckUtil.check(value));
			}
			break;
		}
			
		case EDIT_ENTRY: {
			EditEntryCmd eeCmd = ((EditEntryCmd) cmd);
			String title = eeCmd.getTitle();
			if (MiscUtil.hasString(title)) {
				eeCmd.setTitle(StringCheckUtil.check(title));
			}
			String desc = eeCmd.getDescription();
			if (MiscUtil.hasString(desc)) {
				eeCmd.setDescription(StringCheckUtil.checkHtml(desc));
			}
			break;
		}
			
		case MARKUP_STRING_REPLACEMENT: {
			MarkupStringReplacementCmd msrCmd = ((MarkupStringReplacementCmd) cmd);
			String html = msrCmd.getHtml();
			if (MiscUtil.hasString(html)) {
				msrCmd.setHtml(StringCheckUtil.checkHtml(html));
			}
			break;
		}
			
			
		case MODIFY_GROUP: {
			ModifyGroupCmd mgCmd = ((ModifyGroupCmd) cmd);
			String value = mgCmd.getDesc();
			if (MiscUtil.hasString(value)) {
				mgCmd.setDesc(StringCheckUtil.checkHtml(value));
			}
			value = mgCmd.getTitle();
			if (MiscUtil.hasString(value)) {
				mgCmd.setTitle(StringCheckUtil.check(value));
			}
			break;
		}
			
		case SAVE_BRANDING: {
			SaveBrandingCmd sbCmd = ((SaveBrandingCmd) cmd);
			GwtBrandingData bd = sbCmd.getBrandingData(); 
			String html = bd.getBranding();
			if (MiscUtil.hasString(html)) {
				bd.setBranding(StringCheckUtil.checkHtml(html));
			}
			break;
		}
			
		case SAVE_USER_STATUS: {
			SaveUserStatusCmd susCmd = ((SaveUserStatusCmd) cmd);
			String status = susCmd.getStatus();
			if (MiscUtil.hasString(status)) {
				susCmd.setStatus(StringCheckUtil.check(status));
			}
			break;
		}
			
		case REPLY_TO_ENTRY: {
			ReplyToEntryCmd rteCmd = ((ReplyToEntryCmd) cmd);
			String title = rteCmd.getTitle();
			if (MiscUtil.hasString(title)) {
				rteCmd.setTitle(StringCheckUtil.check(title));
			}
			String desc = rteCmd.getDescription();
			if (MiscUtil.hasString(desc)) {
				rteCmd.setDescription(StringCheckUtil.checkHtml(desc));
			}
			break;
		}
			
		case SAVE_FOLDER_COLUMNS: {
			SaveFolderColumnsCmd saveFCCmd = ((SaveFolderColumnsCmd) cmd);
			for (FolderColumn fc : saveFCCmd.getFolderColumns()) {
				fc.setColumnCustomTitle(StringCheckUtil.check(fc.getColumnCustomTitle()));
			}
			break;
		}
		
		case GET_JSP_HTML: {
			GetJspHtmlCmd jspHtmlCmd = ((GetJspHtmlCmd) cmd);
			Map<String,Object> model = jspHtmlCmd.getModel();
			StringCheckUtil.check(model, Boolean.TRUE);
		}
			
		// The following commands do not require XSS checks.
		case ABORT_FILE_UPLOAD:
		case ADD_FAVORITE:
		case ADD_NEW_FOLDER:
		case ADD_NEW_PROXY_IDENTITY:
		case CAN_ADD_FOLDER:
		case CAN_MODIFY_BINDER:
		case CHANGE_ENTRY_TYPES:
		case CHANGE_FAVORITE_STATE:
		case CHANGE_PASSWORD:
		case CHECK_NET_FOLDERS_STATUS:
		case CHECK_NET_FOLDER_SERVERS_STATUS:
		case CLEAR_HISTORY:
		case COLLAPSE_SUBTASKS:
		case COMPLETE_EXTERNAL_USER_SELF_REGISTRATION:
		case COPY_ENTRIES:
		case CREATE_CHANGE_LOG_REPORT:
		case CREATE_DUMMY_MOBILE_DEVICES:
		case CREATE_EMAIL_REPORT:
		case CREATE_LICENSE_REPORT:
		case CREATE_LOGIN_REPORT:
		case CREATE_NET_FOLDER:
		case CREATE_USER_ACCESS_REPORT:
		case CREATE_USER_ACTIVITY_REPORT:
		case CREATE_NET_FOLDER_ROOT:
		case DELETE_CUSTOMIZED_EMAIL_TEMPLATES:
		case DELETE_MOBILE_DEVICES:
		case DELETE_NET_FOLDERS:
		case DELETE_NET_FOLDER_ROOTS:
		case DELETE_PROXY_IDENTITIES:
		case DELETE_GROUPS:
		case DELETE_SELECTED_USERS:
		case DELETE_SELECTIONS:
		case DELETE_SHARES:
		case DELETE_TASKS:
		case DISABLE_USERS:
		case DUMP_HISTORY_INFO:
		case EMAIL_PUBLIC_LINK:
		case ENABLE_USERS:
		case EXECUTE_ENHANCED_VIEW_JSP:
		case EXECUTE_LANDING_PAGE_CUSTOM_JSP:
		case EXECUTE_SEARCH:
		case EXPAND_HORIZONTAL_BUCKET:
		case EXPAND_SUBTASKS:
		case EXPAND_VERTICAL_BUCKET:
		case FIND_USER_BY_EMAIL_ADDRESS:
		case FORCE_USERS_TO_CHANGE_PASSWORD:
		case GET_ACCESSORY_STATUS:
		case GET_ACTIVITY_STREAM_DATA:
		case GET_ACTIVITY_STREAM_PARAMS:
		case GET_ADD_MEETING_URL:
		case GET_ADHOC_FOLDER_SETTING:
		case GET_ADMIN_ACTIONS:
		case GET_ALL_NET_FOLDERS:
		case GET_ALL_NET_FOLDER_ROOTS:
		case GET_ALL_GROUPS:
		case GET_ANTIVIRUS_SETTINGS:
		case GET_BINDER_BRANDING:
		case GET_BINDER_DESCRIPTION:
		case GET_BINDER_FILTERS:
		case GET_BINDER_HAS_OTHER_COMPONENTS:
		case GET_BINDER_INFO:
		case GET_BINDER_OWNER_AVATAR_INFO:
		case GET_BINDER_PERMALINK:
		case GET_BINDER_REGION_STATE:
		case GET_BINDER_SHARING_RIGHTS_INFO:
		case GET_BINDER_STATS:
		case GET_BINDER_TAGS:
		case GET_BLOG_ARCHIVE_INFO:
		case GET_BLOG_PAGES:
		case GET_CALENDAR_APPOINTMENTS:
		case GET_CALENDAR_DISPLAY_DATA:
		case GET_CALENDAR_DISPLAY_DATE:
		case GET_CALENDAR_NEXT_PREVIOUS_PERIOD:
		case GET_CAN_ADD_ENTITIES:
		case GET_CAN_ADD_ENTITIES_TO_BINDERS:
		case GET_CAN_MANAGE_BINDER_TAGS:
		case GET_CLICK_ON_TITLE_ACTION:
		case GET_CLIPBOARD_TEAM_USERS:
		case GET_CLIPBOARD_USERS:
		case GET_CLIPBOARD_USERS_FROM_LIST:
		case GET_COLLECTION_POINT_DATA:
		case GET_COLUMN_WIDTHS:
		case GET_COMMENT_COUNT:
		case GET_DATABASE_PRUNE_CONFIGURATION:
		case GET_DATE_STR:
		case GET_DATE_TIME_STR:
		case GET_DEFAULT_ACTIVITY_STREAM:
		case GET_DEFAULT_FOLDER_DEFINITION_ID:
		case GET_DEFAULT_STORAGE_ID:
		case GET_DEFAULT_USER_SETTINGS_INFO:
		case GET_DESKTOP_APP_DOWNLOAD_INFO:
		case GET_DOCUMENT_BASE_URL:
		case GET_DOWNLOAD_FILE_URL:
		case GET_DOWNLOAD_FOLDER_AS_CSV_FILE_URL:
		case GET_DOWNLOAD_SETTING:
		case GET_DISK_USAGE_INFO:
		case GET_DYNAMIC_MEMBERSHIP_CRITERIA:
		case GET_EMAIL_NOTIFICATION_INFORMATION:
		case GET_ENTITY_ACTION_TOOLBAR_ITEMS:
		case GET_ENTITY_ID:
		case GET_ENTITY_ID_LIST:
		case GET_ENTITY_PERMALINK:
		case GET_ENTITY_RIGHTS:
		case GET_ENTRY:
		case GET_ENTRY_COMMENTS:
		case GET_ENTRY_TAGS:
		case GET_ENTRY_TYPES:
		case GET_EXECUTE_JSP_URL:
		case GET_EXTENSION_FILES:
		case GET_EXTENSION_INFO:
		case GET_FAVORITES:
		case GET_FILE_ATTACHMENTS:
		case GET_FILE_CONFLICTS_INFO:
		case GET_FILE_SYNC_APP_CONFIGURATION:
		case GET_FILE_URL:
		case GET_FOLDER:
		case GET_FOLDER_COLUMNS:
		case GET_FOLDER_DISPLAY_DATA:
		case GET_FOLDER_ENTRIES:
		case GET_FOLDER_ENTRY_DETAILS:
		case GET_FOLDER_ENTRY_TYPE:
		case GET_FOLDER_FILTERS:
		case GET_FOLDER_ROWS:
		case GET_FOLDER_SORT_SETTING:
		case GET_FOLDER_TOOLBAR_ITEMS:
		case GET_FOOTER_TOOLBAR_ITEMS:
		case GET_GROUP_ACTION_TOOLBAR_ITEMS:
		case GET_GROUP_ASSIGNEE_MEMBERSHIP:
		case GET_GROUP_MEMBERSHIP:
		case GET_GROUP_MEMBERSHIP_INFO:
		case GET_GROUPS:
		case GET_HELP_URL:
		case GET_HISTORY_INFO:
		case GET_HORIZONTAL_NODE:
		case GET_HORIZONTAL_TREE:
		case GET_HTML_ELEMENT_INFO:
		case GET_HTML5_SPECS:
		case GET_IM_URL:
		case GET_INHERITED_LANDING_PAGE_PROPERTIES:
		case GET_IS_USER_EXTERNAL:
		case GET_IS_DYNAMIC_GROUP_MEMBERSHIP_ALLOWED:
		case GET_KEYSHIELD_CONFIG:
		case GET_NET_FOLDER_GLOBAL_SETTINGS:
		case GET_LANDING_PAGE_DATA:
		case GET_LDAP_CONFIG:
		case GET_LDAP_OBJECT_FROM_AD:
		case GET_LDAP_SERVER_DATA:
		case GET_LDAP_SUPPORTS_EXTERNAL_USER_IMPORT:
		case GET_LDAP_SYNC_RESULTS:
		case GET_LIMIT_USER_VISIBILITY_INFO:
		case GET_LIMITED_USER_VISIBILITY_DISPLAY:
		case GET_LIST_OF_CHILD_BINDERS:
		case GET_LIST_OF_FILES:
		case GET_LOCALES:
		case GET_LOGGED_IN_USER_PERMALINK:
		case GET_LOGIN_INFO:
		case GET_MAILTO_PUBLIC_LINKS:
		case GET_MAIN_PAGE_INFO:
		case GET_MANAGE_ADMINISTRATORS_INFO:
		case GET_MANAGE_EMAIL_TEMPLATES_INFO:
		case GET_MANAGE_MOBILE_DEVICES_INFO:
		case GET_MANAGE_PROXY_IDENTITIES_INFO:
		case GET_MANAGE_TEAMS_INFO:
		case GET_MANAGE_USERS_INFO:
		case GET_MANAGE_USERS_STATE:
		case GET_MICRO_BLOG_URL:
		case GET_MOBILE_APPS_CONFIG:
		case GET_MODIFY_BINDER_URL:
		case GET_MY_FILES_CONTAINER_INFO:
		case GET_MY_TASKS:
		case GET_MY_TEAMS:
		case GET_NAME_COMPLETION_SETTINGS:
		case GET_NET_FOLDER:
		case GET_NET_FOLDER_SYNC_STATISTICS:
		case GET_NEXT_PREVIOUS_FOLDER_ENTRY_INFO:
		case GET_NUMBER_OF_MEMBERS:
		case GET_PARENT_BINDER_PERMALINK:
		case GET_PASSWORD_EXPIRATION:
		case GET_PASSWORD_POLICY_CONFIG:
		case GET_PASSWORD_POLICY_INFO:
		case GET_PERSONAL_PREFERENCES:
		case GET_PERSONAL_WORKSPACE_DISPLAY_DATA:
		case GET_PHOTO_ALBUM_DISPLAY_DATA:
		case GET_PRESENCE_INFO:
		case GET_PRINCIPAL_FILE_SYNC_APP_CONFIG:
		case GET_PRINCIPAL_INFO:
		case GET_PRINCIPAL_MOBILE_APPS_CONFIG:
		case GET_PROFILE_AVATARS:
		case GET_PROFILE_ENTRY_INFO:
		case GET_PROFILE_INFO:
		case GET_PROFILE_STATS:
		case GET_PROJECT_INFO:
		case GET_PUBLIC_LINKS:
		case GET_QUICK_VIEW_INFO:
		case GET_RECENT_PLACES:
		case GET_REPORTS_INFO:
		case GET_ROOT_WORKSPACE_ID:
		case GET_SAVED_SEARCHES:
		case GET_SELECTED_USERS_DETAILS:
		case GET_SELECTION_DETAILS:
		case GET_SEND_TO_FRIEND_URL:
		case GET_ZIP_DOWNLOAD_FILES_URL:
		case GET_ZIP_DOWNLOAD_FOLDER_URL:
		case GET_ZONE_SHARE_RIGHTS:
		case GET_ZONE_SHARE_TERMS:
		case GET_SHARED_VIEW_STATE:
		case GET_SHARE_LISTS:
		case GET_SHARING_INFO:
		case GET_SIGN_GUESTBOOK_URL:
		case GET_SITE_ADMIN_URL:
		case GET_SITE_BRANDING:
		case GET_MOBILE_SITE_BRANDING:
		case GET_DESKTOP_SITE_BRANDING:
		case GET_SUBSCRIPTION_DATA:
		case GET_SYSTEM_BINDER_PERMALINK:
		case GET_SYSTEM_ERROR_LOG_URL:
		case GET_TAG_RIGHTS_FOR_BINDER:
		case GET_TAG_RIGHTS_FOR_ENTRY:
		case GET_TAG_SORT_ORDER:
		case GET_TEAM_ASSIGNEE_MEMBERSHIP:
		case GET_TEAM_MANAGEMENT_INFO:
		case GET_TEAMS:
		case GET_TASK_BUNDLE:
		case GET_TASK_DISPLAY_DATA:
		case GET_TASK_LINKAGE:
		case GET_TASK_LIST:
		case GET_TELEMETRY_SETTINGS:
		case GET_TIME_ZONES:
		case GET_TOOLBAR_ITEMS:
		case GET_TOP_LEVEL_ENTRY_ID:
		case GET_TOP_RANKED:
		case GET_TRASH_URL:
		case GET_UPDATE_LOGS_CONFIG:
		case GET_UPGRADE_INFO:
		case GET_USER_ACCESS_CONFIG:
		case GET_USER_AVATAR:
		case GET_USER_LIST_INFO:
		case GET_USER_PERMALINK:
		case GET_USER_PROPERTIES:
		case GET_USER_SHARING_RIGHTS_INFO:
		case GET_USER_STATUS:
		case GET_USER_WORKSPACE_INFO:
		case GET_USER_ZONE_SHARE_SETTINGS:
		case GET_VERTICAL_ACTIVITY_STREAMS_TREE:
		case GET_VERTICAL_NODE:
		case GET_VERTICAL_TREE:
		case GET_VIEW_FILE_URL:
		case GET_VIEW_FOLDER_ENTRY_URL:
		case GET_VIEW_INFO:
		case GET_WEBACCESS_SETTING:
		case GET_WHO_HAS_ACCESS:
		case GET_WIKI_DISPLAY_DATA:
		case GET_WORKSPACE_CONTRIBUTOR_IDS:
		case HAS_ACTIVITY_STREAM_CHANGED:
		case IMPORT_ICAL_BY_URL:
		case INVALIDATE_SESSION:
		case IS_ALL_USERS_GROUP:
		case IS_PERSON_TRACKED:
		case IS_SEEN:
		case HIDE_SHARES:
		case LDAP_AUTHENTICATE_USER:
		case LOCK_ENTRIES:
		case MARK_FOLDER_CONTENTS_READ:
		case MARK_FOLDER_CONTENTS_UNREAD:
		case MODIFY_GROUP_MEMBERSHIP:
		case MODIFY_NET_FOLDER:
		case MODIFY_NET_FOLDER_ROOT:
		case MODIFY_PROXY_IDENTITY:
		case MOVE_ENTRIES:
		case PERSIST_ACTIVITY_STREAM_SELECTION:
		case PERSIST_NODE_COLLAPSE:
		case PERSIST_NODE_EXPAND:
		case PIN_ENTRY:
		case PURGE_TASKS:
		case PUSH_HISTORY_INFO:
		case REMOVE_DESKTOP_SITE_BRANDING:
		case REMOVE_EXTENSION:
		case REMOVE_FAVORITE:
		case REMOVE_MOBILE_SITE_BRANDING:
		case REMOVE_TASK_LINKAGE:
		case REMOVE_SAVED_SEARCH:
		case RENAME_ENTITY:
		case REQUEST_RESET_PASSWORD:
		case RESET_VELOCITY_ENGINE:
		case SAVE_ACCESSORY_STATUS:
		case SAVE_ADHOC_FOLDER_SETTING:
		case SAVE_BINDER_REGION_STATE:
		case SAVE_CALENDAR_DAY_VIEW:
		case SAVE_CALENDAR_HOURS:
		case SAVE_CALENDAR_SETTINGS:
		case SAVE_CALENDAR_SHOW:
		case SAVE_CLIPBOARD_USERS:
		case SAVE_COLUMN_WIDTHS:
		case SAVE_DATABASE_PRUNE_CONFIGURATION:
		case SAVE_DOWNLOAD_SETTING:
		case SAVE_EMAIL_NOTIFICATION_INFORMATION:
		case SAVE_FILE_SYNC_APP_CONFIGURATION:
		case SAVE_FOLDER_ENTRY_DLG_POSITION:
		case SAVE_FOLDER_FILTERS:
		case SAVE_FOLDER_PINNING_STATE:
		case SAVE_FOLDER_SORT:
		case SAVE_HTML_ELEMENT_STATUS:
		case SAVE_KEYSHIELD_CONFIG:
		case SAVE_NET_FOLDER_GLOBAL_SETTINGS:
		case SAVE_LDAP_CONFIG:
		case SAVE_MANAGE_USERS_STATE:
		case SAVE_MOBILE_APPS_CONFIGURATION:
		case SAVE_MULTIPLE_ADHOC_FOLDER_SETTINGS:
		case SAVE_MULTIPLE_DOWNLOAD_SETTINGS:
		case SAVE_MULTIPLE_WEBACCESS_SETTINGS:
		case SAVE_NAME_COMPLETION_SETTINGS:
		case SAVE_PASSWORD_POLICY_CONFIG:
		case SAVE_PERSONAL_PREFERENCES:
		case SAVE_PRINCIPAL_FILE_SYNC_APP_CONFIG:
		case SAVE_PRINCIPAL_MOBILE_APPS_CONFIGURATION:
		case SAVE_SHARE_EXPIRATION_VALUE:
		case SAVE_SHARE_LISTS:
		case SAVE_SHARED_FILES_STATE:
		case SAVE_SHARED_VIEW_STATE:
		case SAVE_SUBSCRIPTION_DATA:
		case SAVE_TASK_COMPLETED:
		case SAVE_TASK_DUE_DATE:
		case SAVE_TASK_GRAPH_STATE:
		case SAVE_TASK_LINKAGE:
		case SAVE_TASK_PRIORITY:
		case SAVE_TASK_SORT:
		case SAVE_TASK_STATUS:
		case SAVE_SEARCH:
		case SAVE_TAG_SORT_ORDER:
		case SAVE_UPDATE_LOGS_CONFIG:
		case SAVE_USER_ACCESS_CONFIG:
		case SAVE_USER_LIST_STATUS:
		case SAVE_WEBACCESS_SETTING:
		case SAVE_WHATS_NEW_SETTINGS:
		case SAVE_ZONE_SHARE_RIGHTS:
		case SAVE_ZONE_SHARE_TERMS:
		case SEND_FORGOTTEN_PWD_EMAIL:
		case SEND_SHARE_NOTIFICATION_EMAIL:
		case SET_ANTIVIRUS_SETTINGS:
		case SET_BINDER_SHARING_RIGHTS_INFO:
		case SET_DEFAULT_USER_SETTINGS:
		case SET_DESKTOP_APP_DOWNLOAD_VISIBILITY:
		case SET_ENTRIES_PIN_STATE:
		case SET_HAS_SEEN_OES_WARNING:
		case SET_MOBILE_DEVICES_WIPE_SCHEDULED_STATE:
		case SET_PRINCIPALS_ADMIN_RIGHTS:
		case SET_SEEN:
		case SET_TELEMETRY_SETTINGS:
		case SET_TELEMETRY_TIER2_ENABLED:
		case SET_UNSEEN:
		case SET_USER_SHARING_RIGHTS_INFO:
		case SET_USER_VISIBILITY:
		case SHARE_ENTRY:
		case SHOW_SHARES:
		case START_LDAP_SYNC:
		case STOP_SYNC_NET_FOLDERS:
		case SYNC_NET_FOLDERS:
		case SYNC_NET_FOLDER_SERVER:
		case TEST_ANTIVIRUS_SETTINGS:
		case TEST_GROUP_MEMBERSHIP_LDAP_QUERY:
		case TEST_KEYSHIELD_CONNECTION:
		case TEST_NET_FOLDER_CONNECTION:
		case TRACK_BINDER:
		case TRASH_PURGE_ALL:
		case TRASH_PURGE_SELECTED_ENTITIES:
		case TRASH_RESTORE_ALL:
		case TRASH_RESTORE_SELECTED_ENTITIES:
		case UPDATE_BINDER_TAGS:
		case UPDATE_CALCULATED_DATES:
		case UPDATE_CALENDAR_EVENT:
		case UPDATE_ENTRY_TAGS:
		case UPDATE_FAVORITES:
		case UNLOCK_ENTRIES:
		case UNPIN_ENTRY:
		case UNTRACK_BINDER:
		case UNTRACK_PERSON:
		case UPLOAD_FILE_BLOB:
		case VALIDATE_CAPTCHA:
		case VALIDATE_EMAIL_ADDRESS:
		case VALIDATE_ENTRY_EVENTS:
		case VALIDATE_SHARE_LISTS:
		case VALIDATE_UPLOADS:
			break;
			
		default:
			// Log an error that we encountered an unhandled command.
			GwtLogHelper.error(m_logger, "GwtServerHelper.performXSSCheckOnRpcCmd( Unhandled Command ):  " + cmd.getClass().getName());
			break;
		}
	}
	
	/**
	 * Saves the fact that the Binder for the given ID should be
	 * collapsed for the current User.
	 *
	 * @param bs
	 * @param binderId
	 */
	public static void persistNodeCollapse(AllModulesInjected bs, Long binderId) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// If there's no list...
		if (null == usersExpandedBindersList) {
			// ...we don't have to do anything since we only store
			// ...the IDs of expanded binders.  Bail.
			return;
		}

		// Scan the User's expanded Binder's list.
		boolean updateUsersList = false;
		long binderIdVal = binderId.longValue();
		for (Iterator<Long> lIT = usersExpandedBindersList.iterator(); lIT.hasNext(); ) {
			// Is this the Binder in question?
			Long l = lIT.next();
			if (l.longValue() == binderIdVal) {
				// Yes, remove it from the list, force the list to be
				// written back out and quit looking.
				usersExpandedBindersList.remove(l);
				updateUsersList = true;
				break;
			}
		}
		
		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (updateUsersList) {
			// Yes!  Write it.
			setExpandedBindersList(bs, usersExpandedBindersList);
		}
	}

	/**
	 * Saves the fact that the Binder for the given ID should be
	 * expanded for the current User.
	 * 
	 * @param bs
	 * @param binderId
	 */
	public static void persistNodeExpand(AllModulesInjected bs, Long binderId) {
		// Access the current User's expanded Binder's list.
		List<Long> usersExpandedBindersList = getExpandedBindersList(bs);

		// If there's no list...
		boolean updateUsersList = (null == usersExpandedBindersList);
		if (updateUsersList) {
			// ...we need to create one...
			usersExpandedBindersList = new ArrayList<Long>();
		}
		
		else {
			// ...otherwise, we look for the Binder ID in that list.
			updateUsersList = (!(isLongInList(binderId, usersExpandedBindersList)));
		}
		
		// Do we need to write an expanded Binder's list to the User's
		// properties?
		if (updateUsersList) {
			// Yes!  Add this Binder ID to it and write it.
			usersExpandedBindersList.add(0, binderId);
			setExpandedBindersList(bs, usersExpandedBindersList);
		}
	}

	/**
	 * Marks an entry as being pinned.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param entryId
	 * 
	 * @return
	 */
	public static Boolean pinEntry(AllModulesInjected bs, HttpServletRequest request, Long folderId, Long entryId) throws GwtTeamingException {
		EntityId eid = new EntityId(folderId, entryId, EntityId.FOLDER_ENTRY);
		return setEntryPinState(bs, request, eid, true);
	}
	
	/**
	 * Removes favoriteId from the user's list of favorites.
	 * 
	 * @param bs
	 * @param request
	 * @param favoriteId
	 * 
	 * @return
	 */
	public static Boolean removeFavorite(AllModulesInjected bs, HttpServletRequest request, String favoriteId) {
		UserProperties userProperties = bs. getProfileModule().getUserProperties(null);
		Favorites f = new Favorites((String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_FAVORITES));
		f.deleteFavorite(favoriteId);
		bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString());
		return Boolean.TRUE;
	}
	
	/**
	 * Removes a search based on its SavedSearchInfo.
	 *
	 * @param bs
	 * @param ssi
	 * 
	 * @return
	 */
	public static Boolean removeSavedSearch(AllModulesInjected bs, SavedSearchInfo ssi) {
		// Does the user contain any saved searches?
		UserProperties userProperties = bs.getProfileModule().getUserProperties(getCurrentUserId());
		Map properties = userProperties.getProperties();		
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			// Yes!  Can we find a saved search by the given name?
			Map userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
			String queryName = ssi.getName();
			if (userQueries.containsKey(queryName)) {
				// Yes!  Remove it and save the modified search saved
				// list.
				userQueries.remove(queryName);
				bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, userQueries);
			}
		}

		// If we get here, the removal was successful.  Return true.
		return Boolean.TRUE;
	}
	
	/**
	 * Removes the AssignmentInfo's in a remove list from an assignee
	 * list and clears the remove list.
	 * 
	 * @param assigneeList
	 * @param removeList
	 */
	public static void removeUnresolvedAssignees(List<AssignmentInfo> assigneeList, List<AssignmentInfo> removeList) {
		// Scan the remove list...
		for (AssignmentInfo ai: removeList) {
			// ...removing the assignments from the assignee list...
			assigneeList.remove(ai);
		}
		
		// ...and clearing the remove list.
		removeList.clear();
	}
	
	/**
	 * Save the users from a List<ClipboardUser> as the user's
	 * clipboard contents. 
	 * 
	 * @param bs
	 * @param request
	 * @param cbUserList
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData saveClipboardUsers(AllModulesInjected bs, HttpServletRequest request, List<ClipboardUser> cbUserList) throws GwtTeamingException {
		try {
			// Extract the user ID's from the List<CliboardUser>...
			List<Long> userIds = new ArrayList<Long>();
			if (MiscUtil.hasItems(cbUserList)) {
				for (ClipboardUser cbUser:  cbUserList) {
					userIds.add(cbUser.getUserId());
				}
			}
			
			// ...store them in the clipboard...
			Clipboard clipboard = new Clipboard(request);
			clipboard.set(Clipboard.USERS, userIds);				

			// ...and return true.
			return new BooleanRpcResponseData(Boolean.TRUE);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}
	
	/**
	 * Saves the folder columns configuration on the specified binder.
	 * 
	 * @param bs
	 * @param binderId
	 * @param fcList
	 * @param isDefault
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveFolderColumns(AllModulesInjected bs, String binderId, 
			List<FolderColumn> fcList, Boolean isDefault) throws GwtTeamingException {
		try {
			if (fcList.isEmpty()) {
				//This is a request to restore the defaults
				BinderHelper.saveFolderColumnSettings(bs, Long.valueOf(binderId), null, null, 
						null, Boolean.TRUE);
			} else {
				//Build a map of columns and column texts
				Map columns = new HashMap();
				Map columnsText = new HashMap();
				List<String> columnSortOrderList = new ArrayList<String>();
				for (FolderColumn fc : fcList) {
					columnSortOrderList.add(fc.getColumnName());
					if (fc.isColumnShown())
					     columns.put(fc.getColumnName(), "on");
					else columns.put(fc.getColumnName(), ""  );
					String columnTitle = fc.getColumnCustomTitle();
					if (columnTitle != null && !columnTitle.equals("")) {
						columnsText.put(fc.getColumnName(), columnTitle);
					} else {
						columnsText.put(fc.getColumnName(), null);
					}
				}
				//Save the column settings
				BinderHelper.saveFolderColumnSettings(bs, Long.valueOf(binderId), columns, columnsText, 
						columnSortOrderList, isDefault);
			}
			
			return Boolean.FALSE;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}

	/**
	 * Returns an ErrorListRpcResponseData containing any errors from
	 * saving filters on a folder.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param globalFilters
	 * @param personalFilters
	 * @param sourceFolder
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData saveFolderFilters(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, List<FolderFilter> globalFilters, List<FolderFilter> personalFilters, GwtFolder sourceFolder) throws GwtTeamingException {
		try {
			// Access the destination folder that we're saving the
			// filters to.
			Long   folderId = folderInfo.getBinderIdAsLong();
			Folder folder   = bs.getFolderModule().getFolder(folderId);

			// Allocate an ErrorListRpcResponseData that we can return any
			// errors in.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData();

			// Get the current filters from the destination folder.
			FolderFiltersRpcResponseData currentFilters = getFolderFilters(bs, request, folder);

			// Merge the filters to be saved into the currentFilters
			// while validating that the names we've got to save will
			// be unique with those we got from the folder.
			Long sourceFolderId = Long.parseLong(sourceFolder.getFolderId());
			boolean changedGlobalFilters   = mergeFolderFilters(currentFilters, globalFilters,   true,  folderId, sourceFolderId, reply);
			boolean changedPersonalFilters = mergeFolderFilters(currentFilters, personalFilters, false, folderId, sourceFolderId, reply);
			
			// Do we need to save the global filters?
			if (changedGlobalFilters) {
				// Yes!  Add the current global filters to a filter
				// map...
				Map<String, String> globalFiltersMap = new HashMap<String, String>();
				for (FolderFilter globalFilter:  currentFilters.getGlobalFilters()) {
					globalFiltersMap.put(globalFilter.getFilterName(), globalFilter.getFilterData());
				}
				
				// ...and write it to the folder.
				folder.setProperty(
					ObjectKeys.BINDER_PROPERTY_FILTERS,
					globalFiltersMap);
			}

			// Do we need to save the personal filters?
			if (changedPersonalFilters) {
				// Yes!  Add the current personal filters to a filter
				// map...
				Map<String, String> personalFiltersMap = new HashMap<String, String>();
				for (FolderFilter personalFilter:  currentFilters.getPersonalFilters()) {
					personalFiltersMap.put(personalFilter.getFilterName(), personalFilter.getFilterData());
				}
				
				// ...and write it to the user's properties for the
				// ...folder.
				bs.getProfileModule().setUserProperty(
					GwtServerHelper.getCurrentUserId(),
					folderId,
					ObjectKeys.USER_PROPERTY_SEARCH_FILTERS, personalFiltersMap);
			}

			// If we get here, reply refers to the
			// ErrorListRpcResponseData containing any problems we
			// found during the save.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtServerHelper.saveFolderFilters( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Saves the folder sort options on the specified binder.
	 * 
	 * @param bs
	 * @param binderInfo
	 * @param sortKey
	 * @param sortAscending
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveFolderSort(AllModulesInjected bs, BinderInfo binderInfo, String sortKey, boolean sortAscending) throws GwtTeamingException {
		try {
			// Allow for collection sort information being stored on
			// the same binder.
			String propSortBy      = ObjectKeys.SEARCH_SORT_BY;
			String propSortDescend = ObjectKeys.SEARCH_SORT_DESCEND;
			if (binderInfo.isBinderCollection()) {
				String cName     = (".collection." + String.valueOf(binderInfo.getCollectionType().ordinal()));
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			else if (binderInfo.isBinderMobileDevices()) {
				String cName     = (".devices." + String.valueOf(binderInfo.getMobileDevicesViewSpec().getMode().ordinal()));
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			else if (binderInfo.isBinderAdministratorManagement()) {
				String cName     = ".administrators.";
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			Long			binderId = binderInfo.getBinderIdAsLong();
			Long			userId = getCurrentUserId();
			ProfileModule	pm     = bs.getProfileModule();
			pm.setUserProperty(userId, binderId, propSortBy,                      sortKey       );
			pm.setUserProperty(userId, binderId, propSortDescend, String.valueOf(!sortAscending));
			
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				GwtLogHelper.debug(m_logger, "GwtServerHelper.saveFolderSort( Stored folder sort for binder ):  Binder:  " + binderId.longValue() + ", Sort Key:  '" + sortKey + "', Sort Ascending:  " + sortAscending);
			}
			return Boolean.FALSE;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}

	/**
	 * Save the given Jits zone config
	 */
	public static Boolean saveNetFolderGlobalSettings(
		AllModulesInjected allModules,
		GwtNetFolderGlobalSettings nfGlobalSettings ) throws GwtTeamingException
	{
		AdminModule adminModule;
		
		adminModule = allModules.getAdminModule();
		adminModule.setJitsConfig( nfGlobalSettings.getJitsEnabled(), nfGlobalSettings.getMaxWaitTime() );

		adminModule.setUseDirectoryRightsEnabled( nfGlobalSettings.getUseDirectoryRights() );
		
		adminModule.setCachedRightsRefreshInterval( nfGlobalSettings.getCachedRightsRefreshInterval() );
		
		return Boolean.TRUE;
	}
	
	/**
	 * Execute the database prune command
	 * 
	 * @param bs
	 * @param dbPruneConfig
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean executeDatabasePruneCommand(AllModulesInjected bs, GwtDatabasePruneConfiguration dbPruneConfig) throws GwtTeamingException {
		try {
			AdminModule am = bs.getAdminModule();
			
			am.setFileArchivingEnabled(dbPruneConfig.isFileArchivingEnabled()                                             );
			am.setAuditTrailEnabled(   dbPruneConfig.isAuditTrailEnabled()                                                );
			am.setChangeLogEnabled(    dbPruneConfig.isChangeLogEnabled()                                                 );
			am.setLogTableKeepDays(    dbPruneConfig.getAuditTrailPruneAgeDays(), dbPruneConfig.getChangeLogPruneAgeDays());
			am.purgeLogTablesImmediate();
			
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}
	}	
	
	/**
	 * Stores the values from a ManageUsersState object in the session cache.
	 * 
	 * @param bs
	 * @param request
	 * @param mus
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveManageUsersState(AllModulesInjected bs, HttpServletRequest request, ManageUsersState mus) throws GwtTeamingException {
		try {
			// Store/remove the values from the cache and return true.
			HttpSession hSession = getCurrentHttpSession();
			if (mus.isShowDisabled())      hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_DISABLED);        else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_DISABLED,        Boolean.FALSE);
			if (mus.isShowEnabled())       hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_ENABLED);         else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_ENABLED,         Boolean.FALSE);
			if (mus.isShowExternal())      hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_EXTERNAL);        else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_EXTERNAL,        Boolean.FALSE);
			if (mus.isShowInternal())      hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_INTERNAL);        else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_INTERNAL,        Boolean.FALSE);
			if (mus.isShowSiteAdmins())    hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_SITE_ADMINS);     else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_SITE_ADMINS,     Boolean.FALSE);
			if (mus.isShowNonSiteAdmins()) hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_NON_SITE_ADMINS); else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_NON_SITE_ADMINS, Boolean.FALSE);
			return Boolean.TRUE;
		}
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}

	/**
	 * Save the PasswordPolicyConfig information.
	 * 
	 * @param bs
	 * @param passwordPolicyConfig
	 * @param forcePasswordChange
	 * 
	 * @return
	 */
	public static Boolean savePasswordPolicyConfig(AllModulesInjected bs, PasswordPolicyConfig passwordPolicyConfig, boolean forcePasswordChange) {
		// Save the password policy configuration.
		boolean passwordPolicyEnabled = passwordPolicyConfig.isPasswordPolicyEnabled();
		bs.getAdminModule().setPasswordPolicyEnabled(passwordPolicyEnabled);
		
		// If we just enabled password policy, are we supposed for
		// force User's to change the password?
		if (passwordPolicyEnabled && forcePasswordChange) {
			// Yes!  Do it.
			PasswordPolicyHelper.forceAllUsersToChangePassword(bs);
		}
		
		// If we get here, we simply always return true.
		return Boolean.TRUE;
	}
	
	/**
	 * Saves a search based on its tab ID and SavedSearchInfo.
	 *
	 * @param bs
	 * @param request
	 * @param searchTabId
	 * @param ssi
	 * 
	 * @return
	 */
	public static SavedSearchInfo saveSearch(AllModulesInjected bs, HttpServletRequest request, String searchTabId, SavedSearchInfo ssi) {
		// If we can't find the tab to save...
		Integer tabId = Integer.parseInt(searchTabId);
		Tabs tabs = Tabs.getTabs(request);
		Tabs.TabEntry tab = tabs.findTab(Tabs.SEARCH, tabId);
		if (null == tab) {
			// ...we can't save anything.  Bail.
			return null;
		}

		// Get the user's currently defined saved searches...
		UserProperties userProperties = bs.getProfileModule().getUserProperties(getCurrentUserId());
		Map properties = userProperties.getProperties();
		Map userQueries = new HashMap();
		if (properties.containsKey(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES)) {
			userQueries = (Map)properties.get(ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES);
		}

		// ...store the tab as a saved search into them...
		String queryName = ssi.getName();
		userQueries.put(queryName, tab.getQuery());
		bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SAVED_SEARCH_QUERIES, userQueries);
		Map tabOptions = tab.getData();
		tabOptions.put(Tabs.TITLE, queryName);
		tab.setData(tabOptions);

		// ...and return a SavedSearchInfo for the newly saved search.
		SavedSearchInfo reply = new SavedSearchInfo();
		reply.setName(ssi.getName());
		return reply;
	}

	/**
	 * Save the given subscription data for the given entry id.
	 */
	@SuppressWarnings("unused")
	public static Boolean saveSubscriptionData( AllModulesInjected bs, String entryId, SubscriptionData subscriptionData ) throws GwtTeamingException
	{
		try
		{
			FolderEntry entry;
			FolderModule folderModule;
			Long entryIdL;
			Map<Integer, String[]> subscriptionSettings;
			int sendEmailTo;
			int sendEmailToWithoutAttachment;
			int sendTextTo;
			
			entryIdL = new Long( entryId );
			folderModule = bs.getFolderModule();
			entry = folderModule.getEntry( null, entryIdL );
			
			subscriptionSettings = new HashMap<Integer, String[]>();
			
			// Get the 3 notification settings.
			sendEmailTo = subscriptionData.getSendEmailTo();
			sendEmailToWithoutAttachment = subscriptionData.getSendEmailToWithoutAttachment();
			sendTextTo = subscriptionData.getSendTextTo();
			
			// Are all notifications turned off?
			if ( sendEmailTo == SubscriptionData.SEND_TO_NONE && sendEmailToWithoutAttachment == SubscriptionData.SEND_TO_NONE && sendTextTo == SubscriptionData.SEND_TO_NONE )
			{
				// Yes
				subscriptionSettings.put( Subscription.DISABLE_ALL_NOTIFICATIONS, null );
			}
			else
			{
				String[] setting;
				
				// Get the setting for "send e-mail to"
				setting = subscriptionData.getSendEmailToAsString();
				if ( setting != null && setting.length > 0 )
					subscriptionSettings.put( Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION, setting );
				
				// Get the setting for "send e-mail to without an attachment"
				setting = subscriptionData.getSendEmailToWithoutAttachmentAsString();
				if ( setting != null && setting.length > 0 )
					subscriptionSettings.put( Subscription.MESSAGE_STYLE_NO_ATTACHMENTS_EMAIL_NOTIFICATION, setting );
				
				// Get the setting for "send text to"
				setting = subscriptionData.getSendTextToAsString();
				if ( setting != null && setting.length > 0 )
					subscriptionSettings.put( Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION, setting );
			}
			
			// Save the subscription settings to the db.
			folderModule.setSubscription( null, entryIdL, subscriptionSettings );
		}
		catch (Exception e)
		{
			if (e instanceof AccessControlException)
			     GwtLogHelper.warn( m_logger, "GwtServerHelper.saveSubscriptionData() AccessControlException", e );
			else GwtLogHelper.warn( m_logger, "GwtServerHelper.saveSubscriptionData() unknown exception",      e );
			
			throw GwtLogHelper.getGwtClientException( m_logger, e );
		}
		
		return Boolean.TRUE;
	}

	/**
	 * Save the UpdateLogsConfig information.
	 * 
	 * @param bs
	 * @param updateLogsConfig
	 * 
	 * @return
	 */
	public static Boolean saveUpdateLogsConfig(AllModulesInjected bs, UpdateLogsConfig updateLogsConfig) {
		// Save the update logs configuration.
		boolean autoApplyDeferredUpdateLogs = updateLogsConfig.isAutoApplyDeferredUpdateLogs();
		bs.getAdminModule().setAutoApplyDeferredUpdateLogs(autoApplyDeferredUpdateLogs);
		
		// If we get here, we simply always return true.
		return Boolean.TRUE;
	}
	
	/**
	 * Save the UserAccessConfig information.  This includes 'allow
	 * guest access', 'allow self registration', 'allow external users'
	 * and 'allow external user to perform self registration'.
	 * 
	 * @param bs
	 * @param config
	 * 
	 * @return
	 */
	public static Boolean saveUserAccessConfig(AllModulesInjected bs, UserAccessConfig config) {
		// Set 'guest access'.
		AuthenticationConfig authConfig = bs.getAuthenticationModule().getAuthenticationConfig();
		authConfig.setAllowAnonymousAccess(config.getAllowGuestAccess());
		
		// Set 'guest read only'.
		authConfig.setAnonymousReadOnly(config.getGuestReadOnly());
		
		// Set 'download' and 'web access'.
		AdminModule am = bs.getAdminModule();
		am.setDownloadEnabled( config.getAllowDownload() );
		am.setWebAccessEnabled(config.getAllowWebAccess());

		// Is this a Novell or Kablink version?
		if (ReleaseInfo.isLicenseRequiredEdition()) {
			// Novell!
			ZoneModule zoneModule = bs.getZoneModule();
			ZoneConfig zoneConfig = zoneModule.getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			
			// Set 'allow external users to self register'.
			OpenIDConfig openIdConfig = zoneConfig.getOpenIDConfig();
			openIdConfig.setAuthenticationEnabled(  config.getAllowExternalUsersViaOpenID()       );
			openIdConfig.setSelfProvisioningEnabled(config.getAllowExternalUsersSelfReg());
			
			// Set 'allow external users'.
			am.setExternalUserEnabled(config.getAllowExternalUsersViaOpenID());
			am.setOpenIDConfig(openIdConfig);
		}
		
		else {
			// Kablink!  Self registration is only found in Kablink.
			authConfig.setAllowSelfRegistration(config.getAllowSelfReg());
		}

		// Finally, store the user access configuration.
		bs.getAuthenticationModule().setAuthenticationConfig(authConfig);
		
		return Boolean.TRUE;
	}
	
	/**
	 * Save the given GwtPrincipalFileSyncAppConfig settings for the given
	 * users or groups.
	 * 
	 * @param bs
	 * @param config
	 * @param principalIds
	 * @param principalsAreUsers
	 */
	public static SavePrincipalFileSyncAppConfigRpcResponseData savePrincipalFileSyncAppConfig(AllModulesInjected bs, GwtPrincipalFileSyncAppConfig config, List<Long> principalIds, boolean principalsAreUsers) {
		SavePrincipalFileSyncAppConfigRpcResponseData responseData = new SavePrincipalFileSyncAppConfigRpcResponseData();
		if ((null == config) || (!(MiscUtil.hasItems(principalIds)))) {
			responseData.addError( "Invalid parameters passed to savePrincipalFileSyncAppConfig()" );
			return responseData;
		}

		// Map the GWT based configuration to a non-GWT one.
		PrincipalDesktopAppsConfig pConfig = new PrincipalDesktopAppsConfig();
		boolean useDefault = config.getUseGlobalSettings();
		pConfig.setUseDefaultSettings(useDefault);
		if (!useDefault) {
			pConfig.setIsFileSyncAppEnabled(config.getIsFileSyncAppEnabled());
			pConfig.setAllowCachePwd(       config.getAllowCachePwd()       );
		}
		
		ProfileModule pm = bs.getProfileModule();
		for (Long userId : principalIds) {
			try {
				// We write them individually so that we can capture
				// errors individually.
				pm.savePrincipalDesktopAppsConfig(userId, principalsAreUsers, pConfig);
			}
			
			catch (Exception ex) {
				// Save the error in the response...
				User user = ((User) pm.getEntry(userId));
				String cause;
				if (user.isDisabled())
				     cause = NLT.get( "save.user.file.sync.app.config.error.disabled.user" );
				else cause = ex.getLocalizedMessage();
				String[] errorArgs = new String[] {user.getTitle(), cause};
				String errMsg = NLT.get("save.user.file.sync.app.config.error", errorArgs);
				responseData.addError(errMsg);

				// ...and log it.
				GwtLogHelper.error(
					m_logger,
					"GwtServerHelper.savePrincipalFileSyncAppConfig( EXCEPTION ):  ",
					ex);
			}
		}
		
		return responseData;
	}
	
	/**
	 * Save the show setting (show all or show unread) for the What's New page to
	 * the user's properties.
	 */
	public static Boolean saveWhatsNewShowSetting( AllModulesInjected bs, ActivityStreamDataType showSetting )
	{
		ProfileModule profileModule;
		Integer setting;
		
		// Save the show setting to the user's properties.
		profileModule = bs.getProfileModule();
		setting = new Integer( showSetting.ordinal() );
		profileModule.setUserProperty( null, ObjectKeys.USER_PROPERTY_WHATS_NEW_SHOW_SETTING , setting );
		
		return Boolean.TRUE;
	}

	/**
	 * Save the 'AdHoc folder' setting.  If upId is not null saves
	 * the value in the user or group.  Otherwise, saves the setting in
	 * the zone.
	 * 
	 * @param bs
	 * @param upId
	 * @param allowAdHoc
	 * @param errList
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static Boolean saveAdhocFolderSetting(AllModulesInjected bs, Long upId, Boolean allowAdHoc, ErrorListRpcResponseData errList) {
		// Are we dealing with a user or group?
		if (null != upId) {
			// Yes!  Save the setting to their UserPrincipal object.
			Principal p    = bs.getProfileModule().getEntry(upId);
			User      user = ((p instanceof User) ? ((User) p) : null);
			if ((null == user) || user.getIdentityInfo().isFromLdap()) {
				// We don't allow this to be set for non-LDAP users.
				bs.getProfileModule().setAdHocFoldersEnabled(upId, allowAdHoc);
			}
			else if (null != errList) {
				String key;
				if (null != user) key = "saveAdHocFolderSetting.invalidUser";
				else              key = "saveAdHocFolderSetting.invalidGroup";
				errList.addError(NLT.get(key, new String[]{p.getTitle()}));
			}
		}
		else {
			// No, we aren't running with a user!  Save as a zone
			// setting.
			bs.getAdminModule().setAdHocFoldersEnabled(
				((null == allowAdHoc) ?
					Boolean.FALSE     :	//     null -> Default to false.
					allowAdHoc));		// non-null -> Store value directly.
		}
		
		return Boolean.TRUE;
	}
	
	public static Boolean saveAdhocFolderSetting(AllModulesInjected bs, Long upId, Boolean allow) {
		// Always use the initial form of the method.
		return saveAdhocFolderSetting(bs, upId, allow, null);
	}
	
	/**
	 * Save the 'Download' setting.  If upId is not null saves the
	 * value in the UserPrincipal object.  Otherwise, saves the setting
	 * in the zone.
	 * 
	 * @param bs
	 * @param upId
	 * @param allowDownload
	 * @param errList
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static Boolean saveDownloadSetting(AllModulesInjected bs, Long upId, Boolean allowDownload, ErrorListRpcResponseData errList) {
		// Are we dealing with a user?
		if (null != upId) {
			// Yes!  Save the setting to the UserPrincipal.
			//     null -> Remove the setting and revert to the zone's setting.
			// non-null -> Specific value to set.
			Principal p    = bs.getProfileModule().getEntry(upId);
			User      user = ((p instanceof User) ? ((User) p) : null);
			if ((null == user) || (user.isPerson() && (!(user.isSuper())))) {
				// We don't allow this to be set for non-person users
				// (e.g., E-Mail Posting Agent) or admin.
				bs.getProfileModule().setDownloadEnabled(upId, allowDownload);
			}
			else if (null != errList) {
				String key;
				if (null != user) key = "saveDownloadSetting.invalidUser";
				else              key = "saveDownloadSetting.invalidGroup";
				errList.addError(NLT.get(key, new String[]{p.getTitle()}));
			}
		}
		else {
			// No, we aren't running with a user!  Save as a zone
			// setting.
			bs.getAdminModule().setDownloadEnabled(
				((null == allowDownload) ?
					Boolean.TRUE         :	//     null -> Default to true.
					allowDownload));		// non-null -> Store value directly.
		}
		
		return Boolean.TRUE;
	}
	
	public static Boolean saveDownloadSetting(AllModulesInjected bs, Long upId, Boolean allowDownload) {
		// Always use the initial form of the method.
		return saveDownloadSetting(bs, upId, allowDownload, null);
	}
	
	/**
	 * Saves the 'AdHoc folder' settings for multiple users.
	 * 
	 * @param bs
	 * @param userIds
	 * @param allow
	 * 
	 * @return
	 */
	public static ErrorListRpcResponseData saveMultipleAdHocFolderSettings(AllModulesInjected bs, List<Long> userIds, Boolean allow) {
		// Do we have any user IDs to save from?
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		if (MiscUtil.hasItems(userIds)) {
			// Yes!  Scan them...
			for (Long userId:  userIds) {
				// ...saving the allow flag for each.
				saveAdhocFolderSetting(bs, userId, allow, reply);
			}
		}
		return reply;
	}
	
	/**
	 * Saves the 'Download' settings for multiple users.
	 * 
	 * @param bs
	 * @param userIds
	 * @param allowDownload
	 * 
	 * @return
	 */
	public static ErrorListRpcResponseData saveMultipleDownloadSettings(AllModulesInjected bs, List<Long> userIds, Boolean allowDownload) {
		// Do we have any user IDs to save from?
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		if (MiscUtil.hasItems(userIds)) {
			// Yes!  Scan them...
			for (Long userId:  userIds) {
				// ...saving the allow flag for each.
				saveDownloadSetting(bs, userId, allowDownload, reply);
			}
		}
		return reply;
	}
	
	/**
	 * Saves the 'WebAccess' settings for multiple users.
	 * 
	 * @param bs
	 * @param userIds
	 * @param allowWebAccess
	 * 
	 * @return
	 */
	public static ErrorListRpcResponseData saveMultipleWebAccessSettings(AllModulesInjected bs, List<Long> userIds, Boolean allowWebAccess) {
		// Do we have any user IDs to save from?
		ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		if (MiscUtil.hasItems(userIds)) {
			// Yes!  Scan them...
			for (Long userId:  userIds) {
				// ...saving the allow flag for each.
				saveWebAccessSetting(bs, userId, allowWebAccess, reply);
			}
		}
		return reply;
	}
	
	/**
	 * 
	 */
	public static SaveNameCompletionSettingsRpcResponseData saveNameCompletionSettings(
		AllModulesInjected ami,
		GwtNameCompletionSettings gwtSettings )
	{
		SaveNameCompletionSettingsRpcResponseData responseData;
		ZoneConfig zoneConfig;
		ZoneModule zoneModule;
		NameCompletionSettings settings;
		GwtDisplayField fld;
		NCDisplayField primaryFld;
		NCDisplayField secondaryFld;
		
		responseData = new SaveNameCompletionSettingsRpcResponseData();

		if ( gwtSettings == null )
		{
			responseData.addError( "Invalid parameters passed to saveNameCompletionSettings()" );
			return responseData;
		}
		
		// Get the selected primary display field.
		primaryFld = NCDisplayField.NAME;
		fld = gwtSettings.getGroupPrimaryDisplayField();
		if ( fld == GwtDisplayField.NAME )
			primaryFld = NCDisplayField.NAME;
		else if ( fld == GwtDisplayField.TITLE )
			primaryFld = NCDisplayField.TITLE;
		
		// Get the selected secondary display field
		secondaryFld = NCDisplayField.DESCRIPTION;
		fld = gwtSettings.getGroupSecondaryDisplayField();
		if ( fld == GwtDisplayField.DESCRIPTION )
			secondaryFld = NCDisplayField.DESCRIPTION;
		else if ( fld == GwtDisplayField.FQDN )
			secondaryFld = NCDisplayField.FQDN;
		
		zoneModule = ami.getZoneModule();
		zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
		
		settings = zoneConfig.getNameCompletionSettings();
		if ( settings == null )
			settings = new NameCompletionSettings();
		
		// Update the settings and save them to the db.
		settings.setGroupPrimaryFld( primaryFld );
		settings.setGroupSecondaryFld( secondaryFld );
		ami.getAdminModule().setNameCompletionSettings( settings );

		return responseData;
	}
	
	/**
	 * Save the 'WebAccess' setting.  If upId is not null saves the
	 * value in the UserPrincipal object.  Otherwise, saves the setting
	 * in the zone.
	 * 
	 * @param bs
	 * @param upId
	 * @param allowWebAccess
	 * @param errList
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	public static Boolean saveWebAccessSetting(AllModulesInjected bs, Long upId, Boolean allowWebAccess, ErrorListRpcResponseData errList) {
		// Are we dealing with a user?
		if (null != upId) {
			// Yes!  Save the setting to the UserProperties.
			//     null -> Remove the setting and revert to the zone's setting.
			// non-null -> Specific value to set.
			Principal p    = bs.getProfileModule().getEntry(upId);
			User      user = ((p instanceof User) ? ((User) p) : null);
			if ((null == user) || (user.isPerson() && (!(user.isSuper())) && (!(user.isShared())))) {
				// We don't allow this to be set for non-person users
				// (e.g., E-Mail Posting Agent), admin or guest.
				bs.getProfileModule().setWebAccessEnabled(upId, allowWebAccess);
			}
			else if (null != errList) {
				String key;
				if (null != user) key = "saveWebAccessSetting.invalidUser";
				else              key = "saveWebAccessSetting.invalidGroup";
				errList.addError(NLT.get(key, new String[]{p.getTitle()}));
			}
		}
		else {
			// No, we aren't running with a user!  Save as a zone
			// setting.
			bs.getAdminModule().setWebAccessEnabled(
				((null == allowWebAccess) ?
					Boolean.TRUE          :	//     null -> Default to true.
					allowWebAccess));		// non-null -> Store value directly.
		}
		
		return Boolean.TRUE;
	}
	
	public static Boolean saveWebAccessSetting(AllModulesInjected bs, Long upId, Boolean allowWebAccess) {
		// Always use the initial form of the method.
		return saveWebAccessSetting(bs, upId, allowWebAccess, null);
	}
	
	/**
	 * Sets the sharing rights information on a collection of binders. 
	 * 
	 * @param bs
	 * @param request
	 * @param binderIds
	 * @param setAllUsersRights
	 * @param setTeamMemberRights
	 * @param sharingRights
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData setBinderSharingRightsInfo(AllModulesInjected bs, HttpServletRequest request, List<Long> binderIds, boolean setAllUsersRights, boolean setTeamMemberRights, CombinedPerEntityShareRightsInfo sharingRights) throws GwtTeamingException {
		try {
			// Create the ErrorListRpcResponseData to return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Access the modules we'll need to set the rights.
			AdminModule  am = bs.getAdminModule();
			BinderModule bm = bs.getBinderModule();
			
			// We're we given any share rights to set?
			if (MiscUtil.hasItems(binderIds) && (null != sharingRights)) {
				// Yes!  Are there any rights actually being set?
				PerEntityShareRightsInfo setFlags = sharingRights.getSetFlags();
				if (!(setFlags.anyFlagsSet())) {
					// No!  Bail, there's nothing to do.
					return reply;
				}
				
				// Get the right values to be set.
				PerEntityShareRightsInfo valueFlags = sharingRights.getValueFlags();
				
				// Access the Function's we may need to set/clear
				// on the selected binders...
				Long allowExternal    = null;
				Long allowForwarding  = null;
				Long allowInternal    = null;
				Long allowPublic      = null;
				Long allowPublicLinks = null;
				List<Function> fs = am.getFunctions();
				for (Function f:  fs) {
					String fId = f.getInternalId();
					if (MiscUtil.hasString(fId)) {
						if      (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID))     allowExternal    = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID))      allowForwarding  = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID))     allowInternal    = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID))       allowPublic      = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_LINKS_INTERNALID)) allowPublicLinks = f.getId();
					}
				}

				// Can we resolve the binder ID's we're to set the
				// rights for?
				Set<Binder> pList = bm.getBinders(binderIds);
				if (MiscUtil.hasItems(pList)) {
					// Yes!  Scan them.
					for (Binder binder: pList) {
						try {
							// Set/clear the various sharing rights on
							// it. 
							List<Long> memberIds = new ArrayList<Long>();
							memberIds.add(ObjectKeys.OWNER_USER_ID);
							if (setTeamMemberRights) {
								memberIds.add(ObjectKeys.TEAM_MEMBER_ID);
							}
							if (setAllUsersRights) {
								Long allUsersGroupId = Utils.getAllUsersGroupId();
								if (null != allUsersGroupId) {
									memberIds.add(allUsersGroupId);
								}
							}
							if (setFlags.isAllowExternal())    am.updateWorkAreaFunctionMemberships(binder, allowExternal,    valueFlags.isAllowExternal(),    memberIds);
							if (setFlags.isAllowForwarding())  am.updateWorkAreaFunctionMemberships(binder, allowForwarding,  valueFlags.isAllowForwarding(),  memberIds);
							if (setFlags.isAllowInternal())    am.updateWorkAreaFunctionMemberships(binder, allowInternal,    valueFlags.isAllowInternal(),    memberIds);
							if (setFlags.isAllowPublic())      am.updateWorkAreaFunctionMemberships(binder, allowPublic,      valueFlags.isAllowPublic(),      memberIds);
							if (setFlags.isAllowPublicLinks()) am.updateWorkAreaFunctionMemberships(binder, allowPublicLinks, valueFlags.isAllowPublicLinks(), memberIds);
						}
						
						catch (Exception e) {
							// Track everything we couldn't set.
							String binderTitle = binder.getTitle();
							String messageKey;
							if (e instanceof AccessControlException) messageKey = "setBinderSharingRightsError.AccssControlException";
							else                                     messageKey = "setBinderSharingRightsError.OtherException";
							reply.addError(NLT.get(messageKey, new String[]{binderTitle}));
							
							GwtLogHelper.error(m_logger, "GwtServerHelper.setBinderSharingRightsInfo( Binder:  '" + binderTitle + "', EXCEPTION ):  ", e);
						}
					}
				}
			}
			
			// If we get here, reply refers to the
			// ErrorListRpcResponseData containing any errors detected
			// in setting the binder sharing rights.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	/**
	 * Sets the visibility state of the desktop application download
	 * control for the current user.
	 * 
	 * @param bs
	 * @param request
	 * @param visible
	 * 
	 * @return
	 */
	public static Boolean setDesktopAppDownloadVisibility(AllModulesInjected bs, HttpServletRequest request, boolean visible) {
		bs.getProfileModule().setUserProperty(null, ObjectKeys.USER_PROPERTY_SHOW_DESKTOP_APP_DOWNLOAD, String.valueOf(visible));
		return Boolean.TRUE;
	}
	
	/*
	 * Sets an entry's pin state.
	 */
	private static Boolean setEntryPinState(AllModulesInjected bs, HttpServletRequest request, EntityId entityId, boolean pin) throws GwtTeamingException {
		try {
			// Is the entity an entry?
			if (entityId.isEntry()) {
				// Yes!  If it doesn't contain a folder ID...
				Long folderId = entityId.getBinderId();
				Long entryId  = entityId.getEntityId();
				if (null == folderId) {
					// ...extract it from the entry.
					FolderEntry fe = bs.getFolderModule().getEntry(null, entryId);
					folderId = fe.getParentBinder().getId();
				}
	
				// Read the user's folder properties for the folder.
				Long userId = getCurrentUserId();
				ProfileModule pm = bs.getProfileModule();
				UserProperties userFolderProperties = pm.getUserProperties(userId, folderId);
				Map properties = userFolderProperties.getProperties();
	
				// Parse the pinned entries from it.
				String pinnedEntries;
				if (properties.containsKey(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES))
				     pinnedEntries = (String)properties.get(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES);
				else pinnedEntries = "";
				List<Long> peList = new ArrayList<Long>();
				String[] peArray = pinnedEntries.split(",");
				for (int i = 0; i < peArray.length; i += 1) {
					String pe = peArray[i];
					if (MiscUtil.hasString(pe)) {
						peList.add(Long.valueOf(peArray[i]));
					}
				}
				
				// Add (pin)/remove (unpin) the entry as requested.
				boolean isPinned = peList.contains(entryId);
				if (pin != isPinned) {
					if (pin)
					     peList.add(   entryId);
					else peList.remove(entryId);
				}
				
				// Scan the entries in the pinned list.
				StringBuffer finalPinnedEntries = new StringBuffer("");
				SortedSet<FolderEntry> pinnedFolderEntriesSet = bs.getFolderModule().getEntries(peList);
				for (FolderEntry entry:  pinnedFolderEntriesSet) {
					// Is the entry still viable in this folder?
					if (entry.getParentBinder().getId().equals(folderId) && (!(entry.isPreDeleted()))) {
						// Yes!  Add it to the final pinned entries string.
						if (0 < finalPinnedEntries.length()) {
							finalPinnedEntries.append(",");
						}
						finalPinnedEntries.append(entry.getId().toString());
					}
				}
				
				// Write the final pinned entries list to the user's folder
				// properties.
				pm.setUserProperty(userId, folderId, ObjectKeys.USER_PROPERTY_PINNED_ENTRIES, finalPinnedEntries.toString());
			}

			// If we get here, setting the pin state was successful!
			// Return true.
			return Boolean.TRUE;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	/**
	 * Sets the pin state of the given entries.
	 * 
	 * @param bs
	 * @param request
	 * @param entryIds
	 * @param pinned
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean setEntriesPinState(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entryIds, boolean pinned) throws GwtTeamingException {
		// Scan the EntityId's we were given...
		for (EntityId entryId:  entryIds) {
			// ...and pin/unpin each.
		    setEntryPinState(bs, request, entryId, pinned);
		}

		// If we get here, we did what we came to do!  Return true.
		return Boolean.TRUE;
	}
	
	/*
	 * Stores the expanded Binder's List in a UserProperties.
	 */
	private static void setExpandedBindersList(AllModulesInjected bs, List<Long> expandedBindersList) {
		// Do we have an expanded binders list to save?
		if (null == expandedBindersList) {
			// No!  Simply save an empty list.
			expandedBindersList = new ArrayList<Long>();
		}
		
		else {
			// Yes, we have an expanded binders list to save!  Is there
			// a limit on how many entries that list may contain?
			int maxExpandedBinders = SPropsUtil.getInt("maxExpandedBinders", ObjectKeys.MAX_EXPANDED_BINDERS);
			if ((-1) != maxExpandedBinders) {
				// Yes!  If it's contains too many, prune it to the
				// maximum.
				int c = expandedBindersList.size();
				for (int i = (c - 1); i >= maxExpandedBinders; i -= 1) {
					expandedBindersList.remove(i);
				}
			}
		}		

		// Finally, store the expanded binders list in the user's
		// properties.
		bs.getProfileModule().setUserProperty(
			null,
			ObjectKeys.USER_PROPERTY_EXPANDED_BINDERS_LIST,
			expandedBindersList);
	}

	/**
	 * Sets the value of the user property for has the user seen the oes warning.
	 * 
	 * @return
	 */
	public static Boolean setHasSeenOesWarning( AllModulesInjected ami, boolean hasSeen )
	{
		ami.getProfileModule().setUserProperty(
											null,
											ObjectKeys.USER_PROPERTY_HAS_SEEN_OES_WARNING,
											String.valueOf( hasSeen ) );
		
		return Boolean.TRUE;
	}
	
	/**
	 * Sets or clears the admin rights on collection of users or
	 * groups.
	 * 
	 * @param bs
	 * @param request
	 * @param principalIds
	 * @param setRights
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static SetPrincipalsAdminRightsRpcResponseData setPrincipalsAdminRights(AllModulesInjected bs, HttpServletRequest request, List<Long> principalIds, boolean setRights) throws GwtTeamingException {
		try {
			// Create the SetPrincipalsAdminsRightsRpcResponseData to
			// return.
			SetPrincipalsAdminRightsRpcResponseData reply = new SetPrincipalsAdminRightsRpcResponseData(new ArrayList<ErrorInfo>());
			List<Long> validPIDs = new ArrayList<Long>();
			Map<Long, AdminRights> adminRightsChangeMap = new HashMap<Long, AdminRights>();
			reply.setAdminRightsChangeMap(adminRightsChangeMap);

			// We're we given any Principal IDs to set or clear the
			// rights from?
			if (MiscUtil.hasItems(principalIds)) {
				// Yes!  What's the current user's ID?
				Long currentUserId = getCurrentUserId();
				
				// Can we resolved the Principal IDs?
				List<Principal> pList = ResolveIds.getPrincipals(principalIds, false);	// false -> Don't check for active users.
				if (MiscUtil.hasItems(pList)) {
					// Yes!  Scan them.
					for (Principal p:  pList) {
						// If this Principal has been deleted...
						if (p.isDeleted()) {
							// ...skip it.
							continue;
						}
						
						// Is this Principal a Group?
						boolean pInternal = p.getIdentityInfo().isInternal();
						String  errKey    = null;
						String  pTitle    = null;
						if (p instanceof GroupPrincipal) {
							// Yes!  Can we set its admin rights?
							Group g = ((Group) p);
							pTitle = g.getTitle();
							if (g.isDisabled() && setRights) {
								// You can clear rights from a disabled
								// group, but not set them.
								errKey = "setAdminRightsError.GroupDisabled";
							}
							else if (g.isLdapContainer() && setRights) {
								// You can't set admin rights on an
								// LDAP container group.
								errKey = "setAdminRightsError.GroupLdapContainer";
							}
							else if ((!pInternal)  && setRights) {
								// You can't set admin rights on a
								// group that can contain external
								// users.
								errKey = "setAdminRightsError.GroupExternal";
							}
						}
						
						// No, this Principal is not a Group!  Is it a
						// User?
						else if (p instanceof UserPrincipal) {
							// Yes!  Can we set its admin rights?
							User u = ((User) p);
							pTitle = Utils.getUserTitle(u);
							if (u.isDisabled() && setRights) {
								// You can clear rights from a disabled
								// user, but not set them.
								errKey = "setAdminRightsError.UserDisabled";
							}
							else if ((!(u.isPerson())) && setRights) {
								// You can't set admin rights on
								// built-in system users.
								errKey = "setAdminRightsError.NotAPerson";
							}
							else if ((!pInternal) && setRights) {
								// You can't set admin rights on
								// external users.
								errKey = "setAdminRightsError.UserExternal";
							}
							else if (u.isAdmin()) {
								// The built-in admin account can't
								// have its admin rights changed.
								if (setRights)
								     errKey = "setAdminRightsError.UserAdmin.set";
								else errKey = "setAdminRightsError.UserAdmin.clear";
							}
							else if (currentUserId.equals(u.getId())) {
								// A user cannot change their own admin
								// rights.
								errKey = "setAdminRightsError.UserSelf";
							}
						}
						
						else {
							// No, it wasn't a Group either!  What ever
							// it was, we can't handle it.
							pTitle  = p.getTitle();
							errKey = "setAdminRightsError.UnknownPrincipal";
						}

						// Is there a problem with setting admin rights
						// on this Principal?
						if (null != errKey) {
							// Yes!  Add the error to the reply and
							// skip it.
							reply.addError(NLT.get(errKey, new String[]{pTitle}));
							continue;
						}
						
						// If we get here, this Principal can have its
						// admin rights set!  Track its ID.
						validPIDs.add(p.getId());
					}
					
					// Do we have any Principal IDs that we can set the
					// admin rights on?
					if (!(validPIDs.isEmpty())) {
						// Yes!  Can we find the site admin role so we 
						// can grant or remove them?
						Long siteAdminRole = MiscUtil.getSiteAdminRoleId();
						if (null == siteAdminRole) {
							// No!  Tell the user about the problem.
							reply.addError(NLT.get("setAdminRightsError.UnknownSiteAdminRole"));
							validPIDs.clear();
						}
						
						else {
							// Yes, we have the site admin role so
							// we can grant or remove them!  Set/clear
							// it from the valid Principal IDs...
					    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
					    	bs.getAdminModule().updateWorkAreaFunctionMemberships(zoneConfig, siteAdminRole, setRights, validPIDs);

					    	// ...setup the adminRightsChangeMap with
					    	// ...the new admin rights display string
					    	// ...for each item...
					    	boolean addedGroupRights = false;
					    	for (Long id:  validPIDs) {
					    		for (Principal p:  pList) {
					    			if (id.equals(p.getId())) {
					    				boolean admin;
					    				if (p instanceof GroupPrincipal)
					    				     {admin = AdminHelper.isSiteAdminMember(p.getId()); addedGroupRights = true;}
					    				else {admin = bs.getAdminModule().testUserAccess(((User) p), AdminOperation.manageFunction);}
					    				AdminRights ar = new AdminRights(GwtViewHelper.getPrincipalAdminRightsString(bs, p), admin);
					    				adminRightsChangeMap.put(id, ar);
					    				break;
					    			}
					    		}
					    	}
					    	
					    	// ...and force the them to be re-indexed
					    	// ...so that we correctly pickup the
					    	// ...current siteAdminUser field in the index...
					    	List<Principal> validPs = ResolveIds.getPrincipals(validPIDs, false);
					    	if (addedGroupRights) {
					    		// ...including for group members.
					    		List<Long> memberIds = new ArrayList<Long>();
					    		for (Principal validP:  validPs) {
					    			if (validP instanceof GroupPrincipal) {
					    				Set<Long> membership = getGroupMemberIds((GroupPrincipal) validP);
					    				if (null != membership) {
					    					for (Long memberId: membership) {
					    						ListUtil.addLongToListLongIfUnique(memberIds, memberId);
					    					}
					    				}
					    			}
					    		}
						    	List<Principal> memberPs = ResolveIds.getPrincipals(memberIds, false);
						    	if (MiscUtil.hasItems(memberPs)) {
						    		validPs.addAll(memberPs);
						    	}
					    	}
					    	bs.getProfileModule().indexEntries(
					    		validPs,	//
					    		true);		// true  -> Skip file content indexing.  All we really care about is getting the siteAdminUser indexing correct on User's and Group's.
						}
					}
				}
			}
			
			// If we get here, reply refers to the
			// ErrorListRpcResponseData containing any errors detected
			// in setting the admin rights.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	/**
	 * Stores the telemetryTier1Enabled and telemetryTier2Enabled
	 * settings in the ZoneConfig.
	 *  
	 * @param bs
	 * @param request
	 * @param telemetryTier1Enabled
	 * @param telemetryTier2Enabled
	 * 
	 * @throws GwtTeamingException
	 */
	public static void setTelemetrySettings(AllModulesInjected bs, HttpServletRequest request, boolean telemetryTier1Enabled, boolean telemetryTier2Enabled) throws GwtTeamingException {
		try {
			bs.getAdminModule().setTelemetrySettings(telemetryTier1Enabled, telemetryTier2Enabled);
			
			TelemetryProcessUtil.manageTelemetryProcess(telemetryTier1Enabled);
		}
		
		catch(Exception ex) {
			GwtLogHelper.error(m_logger, "GwtServerHelper.setTelemetrySettings( SOURCE EXCEPTION ):  Error saving telemetry settings.", ex);
			throw GwtLogHelper.getGwtClientException(ex);				
		}
	}
	
	/**
	 * Stores the telemetryTier2Enabled setting in the ZoneConfig.
	 *  
	 * @param bs
	 * @param request
	 * @param telemetryTier2Enabled
	 * 
	 * @throws GwtTeamingException
	 */
	public static void setTelemetryTier2Enabled(AllModulesInjected bs, HttpServletRequest request, boolean telemetryTier2Enabled) throws GwtTeamingException {
		try {
			bs.getAdminModule().setTelemetryTier2Enabled(telemetryTier2Enabled);
		}
		
		catch(Exception ex) {
			GwtLogHelper.error(m_logger, "GwtServerHelper.setTelemetryTier2Enabled( SOURCE EXCEPTION ):  Error saving telemetry tier 2 enabled.", ex);
			throw GwtLogHelper.getGwtClientException(ex);				
		}
	}
	
	/**
	 * Sets the sharing rights information on user workspaces for a
	 * collection of users. 
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * @param sharingRights
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData setUserSharingRightsInfo(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, CombinedPerEntityShareRightsInfo sharingRights) throws GwtTeamingException {
		try {
			// Create the ErrorListRpcResponseData to return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Access the modules we'll need to set the rights.
			AdminModule		am  = bs.getAdminModule();
			WorkspaceModule	wm  = bs.getWorkspaceModule();
			
			// We're we given any share rights to set?
			if (MiscUtil.hasItems(userIds) && (null != sharingRights)) {
				// Yes!  Are there any rights actually being set?
				PerEntityShareRightsInfo setFlags = sharingRights.getSetFlags();
				if (!(setFlags.anyFlagsSet())) {
					// No!  Bail, there's nothing to do.
					return reply;
				}
				
				// Get the right values to be set.
				PerEntityShareRightsInfo valueFlags = sharingRights.getValueFlags();
				
				// Access the Function's we may need to set/clear
				// on the selected user workspaces...
				Long allowExternal    = null;
				Long allowForwarding  = null;
				Long allowInternal    = null;
				Long allowPublic      = null;
				Long allowPublicLinks = null;
				List<Function> fs = am.getFunctions();
				for (Function f:  fs) {
					String fId = f.getInternalId();
					if (MiscUtil.hasString(fId)) {
						if      (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID))     allowExternal    = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID))      allowForwarding  = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID))     allowInternal    = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID))       allowPublic      = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_LINKS_INTERNALID)) allowPublicLinks = f.getId();
					}
				}
				
				// Can we resolve the user ID's of the users we're to
				// set the rights for?
				List<Principal> pList = ResolveIds.getPrincipals(userIds);
				if (MiscUtil.hasItems(pList)) {
					// Yes!  Scan them.
					for (Principal p: pList) {
						User user = null;
						try {
							// Can we access this Principal as a User?
							if (p instanceof UserPrincipal) {
								// Yes!  Is this user a person?
								user = ((User) p);
								if (!(user.isPerson())) {
									// No!  Then we don't allow their
									// workspace sharing rights to be
									// set.
									String userTitle = ((null == user) ? NLT.get("setUserSharingRightsError.NoUser") : Utils.getUserTitle(user));
									reply.addWarning(NLT.get("setUserSharingRightsError.NotAPerson", new String[]{userTitle}));
									continue;
								}
								
								// Can we get the ID of this user's
								// s workspace, creating it if
								// necessary?
								Long wsId = getUserWorkspaceId(bs, request, user);
								if (null == wsId) {
									// No!  Add a warning that this
									// user's sharing rights could not
									// be set.
									reply.addWarning(
										NLT.get("setUserSharingRightsError.NoWorkspace",
										new String[]{Utils.getUserTitle(user)}));
								}
								else {
									// Yes, this user has a workspace!
									// Set/clear the various sharing
									// rights on it. 
									Workspace ws = wm.getWorkspace(wsId);
									if (setFlags.isAllowExternal())    am.updateWorkAreaFunctionMembership(ws, allowExternal,    valueFlags.isAllowExternal(),    ObjectKeys.OWNER_USER_ID);
									if (setFlags.isAllowForwarding())  am.updateWorkAreaFunctionMembership(ws, allowForwarding,  valueFlags.isAllowForwarding(),  ObjectKeys.OWNER_USER_ID);
									if (setFlags.isAllowInternal())    am.updateWorkAreaFunctionMembership(ws, allowInternal,    valueFlags.isAllowInternal(),    ObjectKeys.OWNER_USER_ID);
									if (setFlags.isAllowPublic())      am.updateWorkAreaFunctionMembership(ws, allowPublic,      valueFlags.isAllowPublic(),      ObjectKeys.OWNER_USER_ID);
									if (setFlags.isAllowPublicLinks()) am.updateWorkAreaFunctionMembership(ws, allowPublicLinks, valueFlags.isAllowPublicLinks(), ObjectKeys.OWNER_USER_ID);
								}
							}
						}
						
						catch (Exception e) {
							// Track everything we couldn't set.
							String userTitle = ((null == user) ? NLT.get("setUserSharingRightsError.NoUser") : Utils.getUserTitle(user));
							String messageKey;
							if (e instanceof AccessControlException) messageKey = "setUserSharingRightsError.AccssControlException";
							else                                     messageKey = "setUserSharingRightsError.OtherException";
							reply.addError(NLT.get(messageKey, new String[]{userTitle}));
							
							GwtLogHelper.error(m_logger, "GwtServerHelper.setUserSharingRightsInfo( User:  '" + userTitle + "', EXCEPTION ):  ", e);
						}
					}
				}
			}
			
			// If we get here, reply refers to the
			// ErrorListRpcResponseData containing any errors detected
			// in setting the user sharing rights.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(m_logger, ex);
		}		
	}
	
	/**
	 * Set the user's time zone based on the given time zone offset.
	 * The offset value came from the browser's time zone
	 */
	public static void setUserTimezone(
		AllModulesInjected ami,
		long tzOffset )
	{
		String[] tzIds;
		User user;
		long now;
		Date nowDate;
		TimeZone userTz;
		
		if ( Utils.checkIfFilr() == false )
			return;
		
		user = getCurrentUser();
		
		// Don't set the time zone for the guest user or admin user
		if ( BuiltInUsersHelper.isSystemUserAccount( user ) )
			return;

		nowDate = new Date();
		now = nowDate.getTime();
		
		// If the user's current time zone's offset matches the browser's time zone offset
		// we are done.
		userTz = user.getTimeZone();
		if ( userTz != null && userTz.getOffset( now ) == tzOffset )
			return;
		
		// Get the time zones with the given offset.
		tzIds = TimeZone.getAvailableIDs();
		if ( tzIds != null && tzIds.length > 0 )
		{
			for ( String nextId : tzIds )
			{
				TimeZone tz;
				
				tz = TimeZoneHelper.getTimeZone( nextId );
				
				// Are the timezone offsets equal?  getOffset() adjusts the offset for daylight savings.
				if ( tz.getOffset( now ) == tzOffset )
				{
					Map updates;
					String tzId;

					updates = new HashMap();
					tzId = tz.getID();
					updates.put( ObjectKeys.FIELD_USER_TIMEZONE, tzId );
					try
					{
						ami.getProfileModule().modifyEntry( user.getId(), new MapInputData( updates ) );
						return;
					}
					catch ( Exception ex )
					{
						GwtLogHelper.error( m_logger, "Unable to set the user's timezone: " + ex );
					}
				}
			}
		}
	}

	/**
	 * Execute the given ldap query and return the number of users/groups it found.
	 */
	public static Integer testGroupMembershipCriteria(
												AllModulesInjected ami,
												GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException
	{
		LdapModule ldapModule;
		Integer count = null;
		
		ldapModule = ami.getLdapModule();
		
		// Is the guid attribute configured the the ldap configuration?
		if ( ldapModule.isGuidConfigured() )
		{
			// Yes
			try
			{
				count = ldapModule.testGroupMembershipCriteria(
															membershipCriteria.getBaseDn(),
															membershipCriteria.getLdapFilterWithoutCRLF(),
															membershipCriteria.getSearchSubtree() );
			}
			catch (Exception ex)
			{
				throw GwtLogHelper.getGwtClientException( m_logger, ex );
			}
		}
		else
		{
			GwtTeamingException ex;
			
			// No, throw an exception
			ex = GwtLogHelper.getGwtClientException(m_logger);
			ex.setExceptionType( ExceptionType.LDAP_GUID_NOT_CONFIGURED );
			throw ex;
		}
		
		return count;
	}
	
	/**
	 * Marks an entry as being unpinned.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param entryId
	 * 
	 * @return
	 */
	public static Boolean unpinEntry(AllModulesInjected bs, HttpServletRequest request, Long folderId, Long entryId) throws GwtTeamingException {
		EntityId eid = new EntityId(folderId, entryId, EntityId.FOLDER_ENTRY);
		return setEntryPinState(bs, request, eid, false);
	}
	
	/**
	 * Update the tags for the given binder.
	 * 
	 * @param bs
	 * @param entryId
	 * @param tagsToBeDeleted (may be null)
	 * @param tagsToBeAdded   (may be null)
	 * 
	 * @return (always returns true)
	 */
	public static Boolean updateBinderTags( AllModulesInjected bs, String binderId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded )
	{
		BinderModule bm;
		Binder binder;

		bm = bs.getBinderModule();
		binder = bm.getBinder( Long.parseLong( binderId ) );
		
		// Go through the list of tags to be deleted and delete them.
		if ( MiscUtil.hasItems( tagsToBeDeleted ))
		{
			for ( TagInfo nextTag : tagsToBeDeleted )
			{
				GwtDeleteHelper.deleteBinderTag( bm, binder, nextTag );
			}
		}
		
		// Go through the list of tags to be added and add them.
		if ( MiscUtil.hasItems( tagsToBeAdded ) )
		{
			for ( TagInfo nextTag : tagsToBeAdded )
			{
				addBinderTag( bm, binder, nextTag );
			}
		}
		
		// If we get here, everything worked.
		return Boolean.TRUE;
	}
	
	/**
	 * Update the tags for the given entry.
	 * 
	 * @param bs
	 * @param entryId
	 * @param tagsToBeDeleted (may be null)
	 * @param tagsToBeAdded   (may be null)
	 * 
	 * @return (always returns true)
	 */
	public static Boolean updateEntryTags( AllModulesInjected bs, String entryId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded )
	{
		FolderModule fm;
		Long entryIdL;
		
		fm = bs.getFolderModule();
		entryIdL = Long.parseLong( entryId );
		
		// Go through the list of tags to be deleted and delete them.
		if ( MiscUtil.hasItems( tagsToBeDeleted ) )
		{
			for ( TagInfo nextTag : tagsToBeDeleted )
			{
				GwtDeleteHelper.deleteEntryTag( fm, entryIdL, nextTag );
			}
		}
		
		// Go through the list of tags to be added and add them.
		if ( MiscUtil.hasItems( tagsToBeAdded ) )
		{
			for ( TagInfo nextTag : tagsToBeAdded )
			{
				addEntryTag( fm, entryIdL, nextTag );
			}
		}

		// If we get here, everything worked.
		return Boolean.TRUE;
	}

	/**
	 * Returns true if the current user should have their My Files area
	 * mapped to their home directory and false otherwise.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static boolean useHomeAsMyFiles(AllModulesInjected bs, User user) {
		return SearchUtils.useHomeAsMyFiles(bs, user);
	}
	
	public static boolean useHomeAsMyFiles(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return useHomeAsMyFiles(bs, getCurrentUser());
	}

	/**
	 * Returns true if the current user has a home folder and false otherwise.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static boolean userHasHomeFolder(AllModulesInjected bs, User user) {
		return (null != getHomeFolderId(bs, user));
	}
	
	public static boolean userHasHomeFolder(AllModulesInjected bs) {
		return userHasHomeFolder(bs, getCurrentUser());
	}

	/**
	 * Returns true if the given text matches the captcha
	 */
	public static boolean validateCaptcha(
		AllModulesInjected ami,
		HttpServletRequest httpServletRequest,
		String text )
	{
		return Utils.isCaptchaValid( httpServletRequest, text );
	}
	
	/**
	 * Validate the given e-mail address.
	 * 
	 * @param emailAddress
	 * @param externalEMA
	 * @param addressField
	 * 
	 * @return
	 */
	public static ValidateEmailRpcResponseData validateEmailAddress(
		AllModulesInjected bs,
		String emailAddress,
		boolean externalEMA,
		ValidateEmailAddressCmd.AddressField addressField) {
		EmailAddressStatus emaStatus;

		emaStatus = validateEmailAddressImpl( bs, emailAddress, externalEMA, addressField );

		return new ValidateEmailRpcResponseData(emaStatus);
	}
	
	/**
	 * Validate the given e-mail address.
	 * 
	 * @param emailAddress
	 * @param externalEMA
	 * @param addressField
	 * 
	 * @return
	 */
	public static EmailAddressStatus validateEmailAddressImpl(
		AllModulesInjected bs,
		String emailAddress,
		boolean externalEMA,
		ValidateEmailAddressCmd.AddressField addressField )
	{
		EmailAddressStatus emaStatus = null;
		if (externalEMA) {
			ExternalAddressStatus extEMAStatus = bs.getSharingModule().getExternalAddressStatus(emailAddress);
			switch (extEMAStatus) {
			case failsBlacklistDomain: emaStatus = EmailAddressStatus.failsBlacklistDomain; break;
			case failsBlacklistEMA:    emaStatus = EmailAddressStatus.failsBlacklistEMA;    break;
			case failsWhitelist:       emaStatus = EmailAddressStatus.failsWhitelist;       break;
				
			default:
			case valid:
				break;
			}
		}

		if (null == emaStatus) {
			String usedAs;
			switch (addressField) {
			default:
			case MAIL_TO:  usedAs = MailModule.TO;  break;
			case MAIL_BC:  usedAs = MailModule.BCC; break;
			case MAIL_CC:  usedAs = MailModule.CC;  break;
			}
			
			if (MiscUtil.isEmailAddressValid(usedAs, emailAddress))
			     emaStatus = EmailAddressStatus.valid;
			else emaStatus = EmailAddressStatus.failsFormat;
		}
		
		return emaStatus;
	}
	
	/**
	 * Validate the list of TeamingEvents to see if the user has rights
	 * to perform the events.
	 * 
	 * @param bs
	 * @param req
	 * @param eventValidations
	 * @param entryId
	 */
	public static void validateEntryEvents(AllModulesInjected bs, HttpServletRequest req, List<EventValidation> eventValidations, String entryId) {
		// Initialize all events as invalid.
		for (EventValidation nextValidation:  eventValidations) {
			nextValidation.setIsValid(false);
		}

		try {
			FolderModule fm = bs.getFolderModule();
			Long entryIdL = Long.parseLong(entryId);
			FolderEntry fe = fm.getEntry(null, entryIdL);
			boolean isGuest = getCurrentUser().isShared();
			for (EventValidation nextValidation:  eventValidations) {
				// Validate the next event.
				try {
					TeamingEvents teamingEvent = TeamingEvents.getEnum(nextValidation.getEventOrdinal());
					switch (teamingEvent) {
					case DELETE_ACTIVITY_STREAM_UI_ENTRY:
						// The Guest user...
						if (isGuest) {
							// ...can't delete an entry here.
							throw new AccessControlException();
						}
						
						fm.checkAccess(fe, FolderOperation.deleteEntry);
						break;
					
					case EDIT_ACTIVITY_STREAM_UI_ENTRY:
						// The Guest user...
						if (isGuest) {
							// ...can't edit an entry here.
							throw new AccessControlException();
						}
						
						// If this isn't a reply...
						if (fe.isTop()) {
							// ...it can't be edited this way.
							throw new AccessControlException();
						}
						
						// When applying changes from an edit, we store
						// both the title and description.  Because we
						// store both, we need both rename and modify
						// rights for it to work.
						fm.checkAccess(fe, FolderOperation.renameEntry);
						fm.checkAccess(fe, FolderOperation.modifyEntry);
						break;
					
					case INVOKE_REPLY:
						if (!(canEntryHaveAComment(fe))) {
							continue;
						}
						fm.checkAccess(fe, FolderOperation.addReply);
						break;
					
					case INVOKE_TAG:
						// Tag is valid if the user can manage public
						// tags or can read the entry.
						if (canManagePublicEntryTags(bs, entryId)) {
							// Nothing to do.
						}
						else if (canManagePersonalEntryTags(bs, entryId)) {
							// Nothing to do.
						}
						else {
							throw new AccessControlException();
						}
						break;
					
					case INVOKE_SHARE:
						if (!(bs.getSharingModule().testAddShareEntity(fe))) {
							throw new AccessControlException();
						}
						break;
					
					case INVOKE_SUBSCRIBE:
						fm.checkAccess(fe, FolderOperation.readEntry);
						break;
						
					case INVOKE_SEND_TO_FRIEND: {
						// Does the user have an e-mail address and is
						// the user not guest?
						User user = getCurrentUser();
						if ((!(user.getEmailAddresses().isEmpty())) && (!(user.isShared()))) { 
							// Yes, nothing to do
						}
						else {
							throw new AccessControlException();
						}
						break;
					}
					
					case MARK_ENTRY_READ:
					case MARK_ENTRY_UNREAD:
						// Nothing to do.
						break;
					
					case VIEW_SELECTED_ENTRY:
						// Nothing to do.
						break;
						
					default:
						GwtLogHelper.info(m_logger, "GwtServerHelper.validateEntryEvents( Unknown event ):  "  + teamingEvent.name());
						break;
					}

					// If we get here the action is valid.
					nextValidation.setIsValid(true);
				}
				
				catch (AccessControlException acEx) {/* Ignore. */}
			}
		}
		
		catch (NoFolderEntryByTheIdException nbEx) {/* Ignore. */}
		catch (AccessControlException        acEx) {/* Ignore. */}
		catch (Exception                     e)    {/* Ignore. */}
	}

	/*
	 * Validates that the Long's in a Set<Long> are valid principal
	 * IDs.
	 */
	private static Set<Long> validatePrincipalIds(Set<Long> principalIds) {
		Set<Long> reply = new HashSet<Long>();
		if (MiscUtil.hasItems(principalIds)) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(principalIds);}
			catch (Exception ex) {/* Ignored. */}
			if (MiscUtil.hasItems(principals)) {
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					reply.add(p.getId());
				}
			}
		}
		return reply;
	}
	
	/**
	 * Get the number of days since the installation
	 * This is used with trial licenses
	 * 
	 * @return
	 */
	public static int getDaysSinceInstallation() {
		return getCoreDao().daysSinceInstallation();
	}

	/**
	 * Converts a FileLinkAction (ssf/main enumeration) to a
	 * GwtFileLinkAction (GWT UI enumeration.)
	 * 
	 * @param fla
	 * 
	 * @return
	 */
	public static GwtFileLinkAction gwtFileLinkActionFromFileLinkAction(FileLinkAction fla) {
		GwtFileLinkAction reply;
		switch (fla) {
		default:
		case DOWNLOAD:                 reply = GwtFileLinkAction.DOWNLOAD;                break;
		case VIEW_DETAILS:             reply = GwtFileLinkAction.VIEW_DETAILS;            break;
		case VIEW_HTML_ELSE_DETAILS:   reply = GwtFileLinkAction.VIEW_HTML_ELSE_DETAILS;  break;
		case VIEW_HTML_ELSE_DOWNLOAD:  reply = GwtFileLinkAction.VIEW_HTML_ELSE_DOWNLOAD; break;
		}
		return reply;
	}
	
	/**
	 * Converts a GwtFileLinkAction (GWT UI enumeration) to a
	 * FileLinkAction (ssf/main enumeration.)
	 * 
	 * @param fla
	 * 
	 * @return
	 */
	public static FileLinkAction fileLinkActionFromGwtFileLinkAction(GwtFileLinkAction fla) {
		FileLinkAction reply;
		switch (fla) {
		default:
		case DOWNLOAD:                 reply = FileLinkAction.DOWNLOAD;                break;
		case VIEW_DETAILS:             reply = FileLinkAction.VIEW_DETAILS;            break;
		case VIEW_HTML_ELSE_DETAILS:   reply = FileLinkAction.VIEW_HTML_ELSE_DETAILS;  break;
		case VIEW_HTML_ELSE_DOWNLOAD:  reply = FileLinkAction.VIEW_HTML_ELSE_DOWNLOAD; break;
		}
		return reply;
	}
}
