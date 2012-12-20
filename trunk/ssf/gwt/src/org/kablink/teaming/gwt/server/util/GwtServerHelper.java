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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.Collator;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;

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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;

import org.kablink.teaming.GroupExistsException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.HttpSessionContext;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
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
import org.kablink.teaming.domain.MobileAppsConfig;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.OpenIDConfig;
import org.kablink.teaming.domain.OpenIDProvider;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
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
import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtRole;
import org.kablink.teaming.gwt.client.GwtUserFileSyncAppConfig;
import org.kablink.teaming.gwt.client.GwtUserMobileAppsConfig;
import org.kablink.teaming.gwt.client.GwtZoneMobileAppsConfig;
import org.kablink.teaming.gwt.client.GwtOpenIDAuthenticationProvider;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtSelfRegistrationInfo;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.lpe.LandingPageProperties;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileAttributeListElement;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClipboardUsersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClipboardUsersRpcResponseData.ClipboardUser;
import org.kablink.teaming.gwt.client.rpc.shared.CollectionPointData;
import org.kablink.teaming.gwt.client.rpc.shared.CreateGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.DesktopAppDownloadInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DesktopAppDownloadInfoRpcResponseData.FilenameUrlPair;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd.MembershipFilter;
import org.kablink.teaming.gwt.client.rpc.shared.GetSystemBinderPermalinkCmd.SystemBinderType;
import org.kablink.teaming.gwt.client.rpc.shared.GetJspHtmlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ImportIcalByUrlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ImportIcalByUrlRpcResponseData.FailureReason;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserFileSyncAppConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserMobileAppsConfigRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageUsersStateRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SetUserSharingRightsInfoCmd.CombinedPerUserShareRightsInfo;
import org.kablink.teaming.gwt.client.rpc.shared.MainPageInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ManageUsersInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.MarkupStringReplacementCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ModifyGroupCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ReplyToEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveBrandingCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFolderColumnsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveUserStatusCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserAccessConfig;
import org.kablink.teaming.gwt.client.rpc.shared.UserSharingRightsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailAddressCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmdType;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.gwt.client.util.BinderFilter;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderStats;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderSortSetting;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.ManageUsersState;
import org.kablink.teaming.gwt.client.util.MilestoneStats;
import org.kablink.teaming.gwt.client.util.PerUserShareRightsInfo;
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
import org.kablink.teaming.module.admin.AdminModule;
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
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapModule.LdapOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.license.LicenseModule.LicenseOperation;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.RenderRequestImpl;
import org.kablink.teaming.portletadapter.portlet.RenderResponseImpl;
import org.kablink.teaming.portletadapter.support.AdaptedPortlets;
import org.kablink.teaming.portletadapter.support.KeyNames;
import org.kablink.teaming.portletadapter.support.PortletInfo;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.runwith.RunWithCallback;
import org.kablink.teaming.security.runwith.RunWithTemplate;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.task.TaskHelper.FilterType;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.IconSize;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlFileUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.Clipboard;
import org.kablink.teaming.web.util.EmailHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.FavoritesLimitExceededException;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Tabs;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.WorkspaceTreeHelper;
import org.kablink.teaming.web.util.WorkspaceTreeHelper.Counter;
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

	// The following are used to classify various binders based on
	// their default view definition.  See getFolderType() and
	// getWorkspaceType().
	private static final String VIEW_FOLDER_GUESTBOOK      = "_guestbookFolder";
	private static final String VIEW_FOLDER_MIRRORED_FILE  = "_mirroredFileFolder";	
	private static final String VIEW_WORKSPACE_DISCUSSIONS = "_discussions";
	private static final String VIEW_WORKSPACE_PROJECT     = "_projectWorkspace";
	private static final String VIEW_WORKSPACE_TEAM        = "_team_workspace";
	private static final String VIEW_WORKSPACE_USER        = "_userWorkspace";
	private static final String VIEW_WORKSPACE_WELCOME     = "_welcomeWorkspace";
	private static final String VIEW_WORKSPACE_GENERIC     = "_workspace";

	// String used to recognize an '&' formatted URL vs. a '/'
	// formatted permalink URL.
	private final static String AMPERSAND_FORMAT_MARKER = "a/do?";

	// The following are used as URL components when constructing the
	// URLs for accessing the desktop download application information.
	private static final String JSON_TAIL		= "version.json";
	private static final String MACOS_TAIL_FILR	= "novellfilr/osx/x64/";
	private static final String MACOS_TAIL_VIBE	= "novellvibedesktop/osx/x64/";
	private static final String WIN32_TAIL_FILR	= "novellfilr/windows/x86/";
	private static final String WIN32_TAIL_VIBE	= "novellvibedesktop/windows/x86/";
	private static final String WIN64_TAIL_FILR	= "novellfilr/windows/x64/";
	private static final String WIN64_TAIL_VIBE	= "novellvibedesktop/windows/x64/";
	
	// Keys used to store user management state in the session cache.
	private static final String CACHED_MANAGE_USERS_SHOW_EXTERNAL	= "manageUsersShowExternal";
	private static final String CACHED_MANAGE_USERS_SHOW_ENABLED	= "manageUsersShowEnabled";
	private static final String CACHED_MANAGE_USERS_SHOW_DISABLED	= "manageUsersShowDisabled";
	private static final String CACHED_MANAGE_USERS_SHOW_INTERNAL	= "manageUsersShowInternal";
	
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
	public static class PrincipalComparator implements Comparator<Principal> {
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
			     reply = MiscUtil.safeSColatedCompare(title1, title2);
			else reply = MiscUtil.safeSColatedCompare(title2, title1);

			// If we get here, reply contains the appropriate value for
			// the compare.  Return it.
			return reply;
		}
	}

	/*
	 * Inner class used compare two GwtAdminAction objects.
	 */
	private static class GwtAdminActionComparator implements Comparator<GwtAdminAction> {
		private Collator	m_collator;	//
		
		/**
		 * Class constructor.
		 */
		public GwtAdminActionComparator() {
			m_collator = Collator.getInstance();
			m_collator.setStrength(Collator.IDENTICAL);
		}

	      
		/**
		 * Implements the Comparator.compare() method on two GwtAdminAction objects.
		 *
		 * Returns:
		 *    -1 if adminAction1 <  adminAction2;
		 *     0 if adminAction1 == adminAction2; and
		 *     1 if adminAction1 >  adminAction2.
		 */
		@Override
		public int compare(GwtAdminAction adminAction1, GwtAdminAction adminAction2) {
			String s1 = adminAction1.getLocalizedName();
			if (null == s1) {
				s1 = "";
			}

			String s2 = adminAction2.getLocalizedName();
			if (null == s2) {
				s2 = "";
			}

			return 	m_collator.compare(s1, s2);
		}
	}

	/**
	 * Inner class used within the GWT server code to dump profiling
	 * information to the system log.
	 */
	public static class GwtServerProfiler {
		private boolean m_debugEnabled;	// true m_debugLogger has debugging enabled.  false -> It doesn't.
		private Log     m_debugLogger;	// The Log that profiling information is written to.
		private long    m_debugBegin;	// If m_debugEnabled is true, contains the system time in MS that start() was called.
		private String  m_debugFrom;	// Contains information about where the profile is being used from.

		/*
		 * Class constructor.
		 */
		private GwtServerProfiler(Log logger) {
			m_debugLogger  = logger;
			m_debugEnabled = m_debugLogger.isDebugEnabled();
		}
		
		/**
		 * If the logger has debugging enabled, dumps a message
		 * to the log regarding how long an operation took.
		 */
		public void end() {			
			if (m_debugEnabled) {
				long diff = System.currentTimeMillis() - m_debugBegin;
				m_debugLogger.debug(m_debugFrom + ":  Ended in " + diff + "ms");
			}
			
			SimpleProfiler.stop(m_debugFrom);
		}
		
		/**
		 * Creates a GwtServerProfiler object based on a Log and called
		 * from string.  If the logger has debugging enabled, dumps a
		 * message to the log regarding the profiling being started.
		 * 
		 * @param logger
		 * @param from
		 * 
		 * @return
		 */
		public static GwtServerProfiler start(Log logger, String from) {			
			GwtServerProfiler reply = new GwtServerProfiler(logger);
			reply.m_debugFrom = from;
			
			if (reply.m_debugEnabled) {
				reply.m_debugBegin = System.currentTimeMillis();
				
				reply.m_debugLogger.debug(from + ":  Starting...");
			}
			
			SimpleProfiler.start(reply.m_debugFrom);
			return reply;
		}
		
		public static GwtServerProfiler start(String from) {
			// Always use the initial form of the method.
			return start(m_logger, from);
		}
	}
	
	/**
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
		private boolean m_ascending;	//

		/**
		 * Class constructor.
		 * 
		 * @param ascending
		 */
		public TreeInfoComparator(boolean ascending) {
			m_ascending = ascending;
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
			     reply = MiscUtil.safeSColatedCompare(title1, title2);
			else reply = MiscUtil.safeSColatedCompare(title2, title1);
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
			throw getGwtTeamingException(e);
		}
		
		// If we get here, we have access to the user's workspace!  Add
		// TreeInfo's for the various collections. 
		addCollection(bs, request, userWS, ti, CollectionType.MY_FILES,       false);
		addCollection(bs, request, userWS, ti, CollectionType.SHARED_WITH_ME, false);
		addCollection(bs, request, userWS, ti, CollectionType.NET_FOLDERS,    false);
		addCollection(bs, request, userWS, ti, CollectionType.SHARED_BY_ME,   true );
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
			throw getGwtTeamingException(e);
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
			throw getGwtTeamingException(flee);
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
		// If we weren't given a quick filter to add...
	    quickFilter = ((null == quickFilter) ? "" : quickFilter.trim());
		if (0 == quickFilter.length()) {
			// ...there's nothing to do.  Bail.
			return;
		}
		
		// If the quick filter doesn't end with an '*'...
		if (!(quickFilter.endsWith("*"))) {
			// ...add one.
			quickFilter += "*";
		}

		// Create a SearchFilter from whatever filter is already in
		// affect...
		SearchFilter sf = new SearchFilter(true);
		Document sfDoc = ((Document) options.get(ObjectKeys.SEARCH_SEARCH_FILTER));
		if (null != sfDoc) {
			sf.appendFilter(sfDoc);
		}

		// ...add in the quick filter...
		SearchFilter sfQF = new SearchFilter(true);
    	sfQF.newCurrentFilterTermsBlock(true);
    	if (filterUserList) {
    		SearchFilter sfUserQF = new SearchFilter(false);
    		if (quickFilter.startsWith("@")) {
        		sfUserQF.addEmailDomainFilter(quickFilter.substring(1), true);
    		}
    		else {
	    		sfUserQF.addTitleFilter(      quickFilter,              true);
	    		sfUserQF.addEmailFilter(      quickFilter,              true);
	    		sfUserQF.addEmailDomainFilter(quickFilter,              true);
	    		sfUserQF.addLoginNameFilter(  quickFilter,              true);
    		}
    		sfQF.appendFilter(sfUserQF.getFilter());
    	}
    	else {
    		sfQF.addTitleFilter(quickFilter, true);
    	}
    	sf.appendFilter(sfQF.getFilter());

		// ...store the new filter's XML Document in the options Map...
		sfDoc = sf.getFilter();
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, sfDoc);

		// ...and if we logging debug messages...
		if (m_logger.isDebugEnabled()) {
			// ...dump the search filter XML.
			m_logger.debug("GwtServerHelper.addQuickFilterToSearch( '" + quickFilter + "'):  Search Filter:");
			m_logger.debug("\n" + getXmlString(sfDoc));
		}
	}
	
	public static void addQuickFilterToSearch(Map options, String quickFilter) {
		// Always use the initial form of the method.
		addQuickFilterToSearch(options, quickFilter, false);	// false -> Don't filter for users.
	}
	
	/**
	 * Add a reply to the given entry.
	 */
	public static FolderEntry addReply( AllModulesInjected bs, String entryId, String title, String desc )
		throws WriteEntryDataException, WriteFilesException
	{
		FolderModule folderModule;
		FolderEntry entry;
		Long entryIdL;
		Long binderIdL;
		String replyDefId;
		Map fileMap;
		Map<String, String> inputMap;
		MapInputData inputData;

		folderModule = bs.getFolderModule();
		entryIdL = new Long( entryId );
		
		// Get the id of the binder the given entry lives in.
		entry = folderModule.getEntry( null, entryIdL );
		binderIdL = entry.getParentBinder().getId();
		
		// Get the entry's reply definition id.
		{
			Document entryDefDoc = null;

			replyDefId = null;
			entryDefDoc = entry.getEntryDefDoc();
			if ( entryDefDoc != null )
			{
				List replyStyles = null;

				// Get the reply styles for this entry.
				replyStyles = DefinitionUtils.getPropertyValueList( entryDefDoc.getRootElement(), "replyStyle" );

				// Do we have any reply styles?
				if ( MiscUtil.hasItems( replyStyles ) )
				{
					int i;

					// Yes, find the one whose name is "_comment"
					for (i = 0; i < replyStyles.size() && replyDefId == null; i++)
					{
						String replyStyleId;

						replyStyleId = (String)replyStyles.get(i);
						
						try
						{
							Definition replyDef;
							String replyName;

							replyDef = bs.getDefinitionModule().getDefinition( replyStyleId );
							replyName = replyDef.getName();
							if ( replyName != null && replyName.equalsIgnoreCase( "_comment" ) )
								replyDefId = replyStyleId;
						}
						catch ( NoDefinitionByTheIdException e )
						{
							continue;
						}
					}
					
					if ( replyDefId == null )
					{
						// use the first one.
						replyDefId = (String) replyStyles.get( 0 );
					}
				}
			}

			if ( replyDefId == null || replyDefId.length() == 0 )
				replyDefId = entry.getEntryDefId();
		}
		
		// Create an empty file map.
		fileMap = new HashMap();

		inputMap = new HashMap<String, String>();
		inputMap.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
		inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
		inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_HTML ) );
		inputData = new MapInputData( inputMap );

    	return folderModule.addReply( binderIdL, entryIdL, replyDefId, inputData, fileMap, null );
	}

	/**
	 * Adds the user's search filters as a single Document to an
	 * options Map.
	 * 
	 * @param bs
	 * @param binder
	 * @param userFolderProperties
	 * @param unescapeName
	 * @param options
	 */
	public static void addSearchFiltersToOptions(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties, boolean unescapeName, Map options) {
		// Does the user have any search filters defined on this binder?
		Document searchFilters = getBinderSearchFilter(bs, binder, userFolderProperties, unescapeName);
		if (null != searchFilters) {
			// Yes!  Stuff them into the options Map.
			options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchFilters);
		}
	}
	
	/*
	 * Converts a String to a Long, if possible, and uses it as the ID
	 * of a task folder to construct a TaskFolderInfo to add to a
	 * List<TaskFolderInfo>.
	 */
	private static void addTFIFromStringToList(AllModulesInjected bs, HttpServletRequest request, String s, List<TaskFolderInfo> tfiList) {
		try {
			// Access the folder.
			Long folderId = Long.parseLong(s);
			Folder folder = bs.getFolderModule().getFolder(folderId);

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
	 * Constructs a desktop application URL.
	 */
	private static FilenameUrlPair buildDesktopAppUrl(String baseUrl, String platformTail) {
		String jsonData = doHTTPGet((baseUrl + platformTail + JSON_TAIL));
		String fName    = getSFromJSO(jsonData, "filename");
		String url;
		if (MiscUtil.hasString(fName))
		     url = (baseUrl + platformTail + fName);
		else url = null;
		
		FilenameUrlPair reply;
		if (MiscUtil.hasString(url))
		     reply = new FilenameUrlPair(fName, url);
		else reply = null;
		return reply;
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
			gsp.end();
		}
	}
	
	/*
	 * Builds the TreeInfo objects for a list of child Binder IDs.
	 */
	private static void buildChildTIs(HttpServletRequest request, AllModulesInjected bs, List<TreeInfo> childTIList, List<Long> childBinderList, List<Long> expandedBindersList, int depth) {
		SortedSet<Binder> binders = null;
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.buildChildTIs( READ )");
		try {
			try {
				binders = bs.getBinderModule().getBinders(childBinderList, Boolean.FALSE);
			} catch(AccessControlException ace) {
			} catch(NoBinderByTheIdException nbe) {}
		}
		finally {
			gsp.end();
		}
		
		gsp = GwtServerProfiler.start(m_logger, "GwtServerHelper.buildChildTIs( PROCESS )");
		try {
			if (null != binders) {
				for (Long subBinderId:  childBinderList) {
					long sbi = subBinderId.longValue();
					for (Binder subBinder:  binders) {
						if (subBinder.getId().longValue() == sbi) {
							if (!(BinderHelper.isBinderMyFilesStorage(subBinder))) {	// Drop 'My Files Storage' folders.
								try {
									TreeInfo subWsTI = buildTreeInfoFromBinder(request, bs, subBinder, expandedBindersList, false, depth);
									childTIList.add(subWsTI);
								} catch(AccessControlException ace) {
								} catch(NoBinderByTheIdException nbe) {}
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
			gsp.end();
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
	 * @param binder
	 * @param expandedBindersList
	 * 
	 * @return
	 */
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, Binder binder, List<Long> expandedBindersList) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinder(request, bs, binder, expandedBindersList, (null != expandedBindersList), 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, Binder binder) {
		// Always use the private implementation of this method.
		return buildTreeInfoFromBinder(request, bs, binder, null, false, 0);
	}
	
	public static TreeInfo buildTreeInfoFromBinder(HttpServletRequest request, AllModulesInjected bs, Binder binder, List<Long> expandedBindersList, boolean mergeUsersExpansions, int depth) {
		// Construct the base TreeInfo for the Binder.
		TreeInfo reply = new TreeInfo();
		reply.setBinderInfo(getBinderInfo(request, bs, binder));
		reply.setBinderTitle(GwtUIHelper.getTreeBinderTitle(binder));
		reply.setBinderChildren(binder.getBinderCount());
		String binderPermalink = PermaLinkUtil.getPermalink(request, binder);
		if (Utils.checkIfFilr() && BinderHelper.isBinderPersonalWorkspace(binder)) {
			binderPermalink = appendUrlParam(binderPermalink, WebKeys.URL_OPERATION, WebKeys.ACTION_SHOW_PROFILE);
		}
		reply.setBinderPermalink(binderPermalink);
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
				reply.add(buildTreeInfoFromBinder(request, bs, binder, null, false, (-1)));
			}
		}
		return reply;
	}

	/**
	 * See if the user has rights to add a folder to the given binder.
	 */
	public static Boolean canAddFolder( AllModulesInjected bs, String binderId )
	{
		try
		{
			Binder binder;
			boolean results;
			
			binder = bs.getBinderModule().getBinder( Long.parseLong( binderId ) );
			results = bs.getBinderModule().testAccess( binder, BinderOperation.addFolder );
			return new Boolean( results );
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to modify the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage personal tags on the given binder.
	 */
	public static Boolean canManagePersonalBinderTags( AllModulesInjected bs, String binderId )
	{
		try
		{
			Binder binder;
			
			binder = bs.getBinderModule().getBinder( Long.parseLong( binderId ) );
			return new Boolean( bs.getBinderModule().testAccess( binder, BinderOperation.modifyBinder ) );
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to manage personal tags on the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage personal tags on the given entry.
	 */
	public static Boolean canManagePersonalEntryTags( AllModulesInjected bs, String entryId )
	{
		try
		{
			FolderModule folderModule;
			FolderEntry folderEntry;
			Long entryIdL;
			
			folderModule = bs.getFolderModule();
			entryIdL = new Long( entryId );

			folderEntry = folderModule.getEntry( null, entryIdL );
	        
			// Check to see if the user can manage personal tags on this entry.
			folderModule.checkAccess( folderEntry, FolderOperation.readEntry );

			// If we get here the action is valid.
			return Boolean.TRUE;
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to manage personal tags on the entry.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage public tags on the given binder.
	 */
	public static Boolean canManagePublicBinderTags( AllModulesInjected bs, String binderId )
	{
		try
		{
			Binder binder;
			
			binder = bs.getBinderModule().getBinder(Long.parseLong(binderId));
			return new Boolean( bs.getBinderModule().testAccess( binder, BinderOperation.manageTag ) );
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to manage public tags on the binder.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to manage public tags on the given entry.
	 */
	public static Boolean canManagePublicEntryTags( AllModulesInjected bs, String entryId )
	{
		try
		{
			FolderModule folderModule;
			FolderEntry folderEntry;
			Long entryIdL;
			
			folderModule = bs.getFolderModule();
			entryIdL = new Long( entryId );

			folderEntry = folderModule.getEntry( null, entryIdL );
	        
			// Check to see if the user can manage public tags on this entry.
			folderModule.checkAccess( folderEntry, FolderOperation.manageTag );

			// If we get here the action is valid.
			return Boolean.TRUE;
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to manage public tags on the entry.
		return Boolean.FALSE;
	}
	
	/**
	 * See if the user has rights to modify the given binder.
	 */
	public static Boolean canModifyBinder( AllModulesInjected bs, String binderId )
	{
		try
		{
			Binder binder;
			boolean results;
			
			binder = bs.getBinderModule().getBinder( Long.parseLong( binderId ) );
			results = bs.getBinderModule().testAccess( binder, BinderOperation.modifyBinder );
			return new Boolean( results );
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
		// If we get here the user does not have rights to modify the binder.
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
		boolean isGuestOrExternal = (user.isShared() || (!(user.getIdentityInfo().isInternal())));
		boolean reply             = true;
		switch (ct) {
		default:
			break;
			
		case MY_FILES:
			// For Filr, we don't support My Files for the guest or
			// external users. 
			if (Utils.checkIfFilr() && isGuestOrExternal) {
				reply = false;
			}
			else {
				// The user can access My Files if adHoc folders are
				// not allowed and the user doesn't have a home folder.
				if (useHomeAsMyFiles(bs, user) && (!(userHasHomeFolder(bs, user)))) {
					reply = false;
				}
			}
			break;
			
		case NET_FOLDERS:
		case SHARED_BY_ME:
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
			throw getGwtTeamingException(ex);
		}
	}
	
	/**
	 * Complete the self registration of an external user
	 */
	public static Boolean completeExternalUserSelfRegistration(
		AllModulesInjected ami,
		Long extUserId,
		String firstName,
		String lastName,
		String pwd,
		String invitationUrl )
	{
		try
		{
			ProfileDao profileDao;
			Map updates;
			User extUser;
			String confirmationUrl = null;
			
			// Get the external user.
			profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
			extUser = profileDao.loadUser( extUserId, RequestContextHolder.getRequestContext().getZoneId() );
			
			updates = new HashMap();
			updates.put( ObjectKeys.FIELD_USER_PASSWORD, pwd );
			updates.put( ObjectKeys.FIELD_USER_FIRSTNAME, firstName );
			updates.put( ObjectKeys.FIELD_USER_LASTNAME, lastName );

			ami.getProfileModule().modifyUserFromPortal( extUser, updates, null );
			
			ExternalUserUtil.markAsCredentialed( extUser );
			
			// invitationUrl is the original url the user was sent in the first share email.
			// We need to replace "euet=xxx" with "euet=some new token value".
			if ( invitationUrl != null && invitationUrl.length() > 0 )
			{
    			String newToken;

			    // Create a new token
				newToken = ExternalUserUtil.encodeUserTokenWithNewSeed( extUser );

				confirmationUrl = ExternalUserUtil.replaceTokenInUrl(invitationUrl, newToken);
			}

			// Send an email informing the user that their registration is complete.
			EmailHelper.sendConfirmationToExternalUser( ami, extUserId, confirmationUrl );
		}
		catch ( Exception ex )
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Create a group from the given information
	 * @throws WriteEntryDataException 
	 * @throws WriteFilesException 
	 * @throws AccessControlException 
	 */
	public static Group createGroup(
								AllModulesInjected ami,
								String name,
								String title,
								String desc,
								boolean isMembershipDynamic,
								boolean externalMembersAllowed,
								List<GwtTeamingItem> membership,
								GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException
	{
		MapInputData inputData;
		HashMap<String, Object> inputMap;
		HashMap<String, Object> fileMap;
		Group newGroup = null;
		String ldapQuery = null;
		HashSet<Long> membershipIds;
		SortedSet<Principal> principals;

		// Create an empty file map.
		fileMap = new HashMap<String, Object>();
		
		membershipIds = new HashSet<Long>();
		
		inputMap = new HashMap<String, Object>();
		inputMap.put( ObjectKeys.FIELD_PRINCIPAL_NAME, name );
		inputMap.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
		inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
		inputMap.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_NONE ) );  
		inputMap.put( ObjectKeys.FIELD_GROUP_DYNAMIC, isMembershipDynamic );
		
		// Add identity information
		{
			IdentityInfo identityInfo;
			
			identityInfo = new IdentityInfo();
			identityInfo.setFromLocal( true );
			identityInfo.setInternal( !externalMembersAllowed );
			inputMap.put( ObjectKeys.FIELD_USER_PRINCIPAL_IDENTITY_INFO, identityInfo );
		}
	
		// Is group membership dynamic?
		if ( isMembershipDynamic == false )
		{
			// No
			// Get a list of all the membership ids
			if ( membership != null )
			{
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
			}
		}
		else
		{
			// Yes
			if ( membershipCriteria != null )
			{
				// Execute the ldap query and get the list of members from the results.
				try
				{
					int maxCount;
					HashSet<Long> potentialMemberIds;
					
					potentialMemberIds = ami.getLdapModule().getDynamicGroupMembers(
																		membershipCriteria.getBaseDn(),
																		membershipCriteria.getLdapFilterWithoutCRLF(),
																		membershipCriteria.getSearchSubtree() );

					// Get the maximum number of users that can be in a group.
					maxCount = SPropsUtil.getInt( "dynamic.group.membership.limit", 50000 ); 					
					
					// Is the number of potential members greater than the max number of group members?
					if ( potentialMemberIds.size() > maxCount )
					{
						int count;

						// Yes, only take the max number of users.
						count = 0;
						for (Long userId : potentialMemberIds)
						{
							membershipIds.add( userId );
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
						membershipIds = potentialMemberIds;
					}
				}
				catch (Exception ex)
				{
					// !!! What to do.
					membershipIds = new HashSet<Long>();
				}

				ldapQuery = membershipCriteria.getAsXml();
			}
		}
		
		principals = ami.getProfileModule().getPrincipals( membershipIds );
		inputMap.put( ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, principals );

		inputMap.put( ObjectKeys.FIELD_GROUP_LDAP_QUERY, ldapQuery );

		inputData = new MapInputData( inputMap );

		try
		{
			// Create the new group
			newGroup = ami.getProfileModule().addGroup( null, inputData, fileMap, null );
			
			// Reindex all the members of this group.
			if ( principals != null )
			{
				Map<Long, Principal> principalsToIndex;
				
				// Create a list of the members of the group.
				principalsToIndex = new HashMap<Long,Principal>();
				for (Principal principal : principals)
				{
					principalsToIndex.put( principal.getId(), principal );
				}
				
				Utils.reIndexPrincipals( ami.getProfileModule(), principalsToIndex );
			}
		}
		catch( Exception ex)
		{
			throw getGwtTeamingException( ex );
		}
		
		return newGroup;
	}

	/*
	 * Creates a user's My Files container and returns its ID.
	 */
	private static Long createMyFilesFolder(AllModulesInjected bs, User user) {
		// Can we determine the template to use for the My Files
		// folder?
		TemplateBinder	mfFolderTemplate   = bs.getTemplateModule().getTemplateByName(ObjectKeys.DEFAULT_TEMPLATE_NAME_LIBRARY);
		Long			mfFolderTemplateId = ((null == mfFolderTemplate) ? null : mfFolderTemplate.getId());
		if (null == mfFolderTemplateId) {
			// No!  Then we can't create it.
			return null;
		}

		// Generate a unique name for the folder.
		Long				reply       = null;
		final String		mfTitleBase = NLT.get("collection.myFiles.folder");
		final BinderModule	bm          = bs.getBinderModule();
		for (int tries = 0; true; tries += 1) {
			try {
				// For tries beyond the first, we simply bump a counter
				// until we find a name to use.
				String mfTitle = mfTitleBase;
				if (0 < tries) {
					mfTitle += ("-" + tries);
				}
				
				// Can we create a folder with this name?
				final Long		mfFolderId = bs.getTemplateModule().addBinder(mfFolderTemplateId, user.getWorkspaceId(), mfTitle, null).getId();
				final Binder	mfFolder   = bm.getBinder(mfFolderId); 
				if (null != mfFolder) {
					// Yes!  Mark it as being the My Files folder...
					bm.setProperty(mfFolderId, ObjectKeys.BINDER_PROPERTY_MYFILES_DIR, Boolean.TRUE);
					bm.indexBinder(mfFolderId                                                      );
					
					// ...and to inherit its team membership.
					RunWithTemplate.runWith(new RunWithCallback() {
							@Override
							public Object runWith() {
								bm.setTeamMembershipInherited(mfFolderId, true);			
								return null;
							}
						},
						new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION},
						null);
					
					// Return the ID of the folder we created.
					reply = mfFolderId;
					break;
				}
			}
			
			catch (Exception e) {
				// If the create fails because of a naming conflict...
				if (e instanceof TitleException) {
					// ...simply try again with a new name.
					continue;
				}
				break;
			}
		}

		// If we get here, reply is null or refers to the ID of the
		// newly created folder.  Return it.
		return reply;
	}
	
	@SuppressWarnings("unused")
	private static Long createMyFilesFolder(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return createMyFilesFolder(bs, getCurrentUser());
	}
	
	/**
	 * Delete the given tag from the given entry.
	 */
	public static void deleteEntryTag( FolderModule fm, Long entryId, TagInfo tagInfo )
	{
		String tagId;
		
		// Does this tag already exist?
		tagId = tagInfo.getTagId();
		if ( tagId != null && tagId.length() > 0 )
		{
			// Yes, delete it from the given entry.
			fm.deleteTag( null, entryId, tagId );
		}
	}
	
	/*
	 * Remove the given tag from the given binder.
	 * are deleted from the binder.
	 */
	private static void deleteBinderTag( BinderModule bm, Binder binder, TagInfo tag )
	{
		String tagId;
		
		tagId = tag.getTagId();
		if ( tagId != null && tagId.length() > 0 )
			bm.deleteTag( binder.getId(), tagId );
	}
	
	/**
	 * Deletes the specified folder entries.
	 *
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData deleteFolderEntries(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Scan the entry IDs...
			for (EntityId entityId:  entityIds) {
				try {
					// ...deleting each entity...
					if (entityId.isBinder())
					     TrashHelper.preDeleteBinder(bs,                         entityId.getEntityId());
					else TrashHelper.preDeleteEntry( bs, entityId.getBinderId(), entityId.getEntityId());
				}

				catch (Exception e) {
					// ...tracking any that we couldn't delete.
					String entryTitle = getEntityTitle(bs, entityId);
					String messageKey;
					if (e instanceof AccessControlException) messageKey = "deleteEntryError.AccssControlException";
					else                                     messageKey = "deleteEntryError.OtherException";
					reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
		}
	}

	/**
	 * 
	 */
	public static Boolean deleteGroups( AllModulesInjected ami, ArrayList<GroupInfo> listOfGroups ) throws GwtTeamingException
	{
		if ( listOfGroups != null && listOfGroups.size() > 0 )
		{
	   		List<Long> grpIds;
	   		
	   		grpIds = new ArrayList<Long>();
	   		for (GroupInfo nextGroup : listOfGroups)
	   		{
	   			grpIds.add( nextGroup.getId() );
	   		}

       		// Remove each group's quota (by setting it to 0) before deleting it
			// This will fix up all of the user quotas that may have been influenced by this group
	   		ami.getProfileModule().setGroupDiskQuotas( grpIds, new Long( 0L ) );
	   		ami.getProfileModule().setGroupFileSizeLimits( grpIds, null );
	       		
			// Delete each groups
	   		for (Long nextGroupId : grpIds)
	   		{
	   			try
	   			{
	   				ami.getProfileModule().deleteEntry( nextGroupId, null );
	   			}
	   			catch ( Exception ex )
	   			{
	   				throw getGwtTeamingException( ex );
	   			} 
	   		}
		}
		
		return Boolean.TRUE;
	}

	/*
	 * Does an HTTP get on the given URL and returns what's read.
	 */
	private static String doHTTPGet(String httpUrl) {
		BufferedReader		reader        = null;
		HttpURLConnection	urlConnection = null;
		String				reply         = null;
		
		try {
			// Open the HTTP connection.
			urlConnection = ((HttpURLConnection) new URL(httpUrl).openConnection());
			urlConnection.setRequestMethod("GET");
			urlConnection.setInstanceFollowRedirects(false);
			urlConnection.connect();

			// Read the content from the HTTP connection.
			String			line;
			StringBuffer	result = new StringBuffer();
			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			while (null != (line = reader.readLine())) {
			    result.append(line);
			}
			reply = result.toString();
		}
		
		catch (Exception ex) {
			m_logger.error("GwtServerHelper.doHTTPGet( '" + httpUrl + "' )", ex);
			reply = null;
		}
		
		finally {
			// If we have a reader...
			if (null != reader) {
				// ...make sure it gets closed...
				try {reader.close();}
				catch (Exception e) {}
			}

			// ...and if we have an HTTP connection...
			if (null != urlConnection) {
				// ...make sure it gets disconnected.
				urlConnection.disconnect();
			}
		}

		// If we get here, reply is null or refers to the content of
		// the HTTP GET.  Return it.
		return reply;
	}
	
	/**
	 * Execute the given enhanced view jsp and return the resulting html.
	 */
	public static String executeLandingPageJsp( AllModulesInjected ami, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, String binderId, String jspName, String configStr )
	{
		String results;
		RequestDispatcher reqDispatcher;
		StringServletResponse ssResponse;
		
		reqDispatcher = request.getRequestDispatcher( jspName );
		ssResponse = new StringServletResponse( response );

		try
		{
			RenderRequestImpl renderReq;
			RenderResponseImpl renderRes;
			PortletInfo portletInfo;

			// Gather up all the data required by the jsp
			{
				Long binderIdL;
				Map<String, Object> params;
				Map<String, Object> model;

				// Create the objects needed to call WorkspaceTreeHelper.setupWorkspaceBeans()
				{
					String portletName;
					String charEncoding;

					portletName = "ss_forum";
					portletInfo = (PortletInfo) AdaptedPortlets.getPortletInfo( portletName );
					
					renderReq = new RenderRequestImpl( request, portletInfo, AdaptedPortlets.getPortletContext() );
					
					params = new HashMap<String, Object>();
					params.put( KeyNames.PORTLET_URL_PORTLET_NAME, new String[] {portletName} );
					params.put( WebKeys.URL_BINDER_ID, binderId );
					renderReq.setRenderParameters( params );
					
					renderRes = new RenderResponseImpl( renderReq, response, portletName );
					charEncoding = SPropsUtil.getString( "web.char.encoding", "UTF-8" );
					renderRes.setContentType( "text/html; charset=" + charEncoding );
					renderReq.defineObjects( portletInfo.getPortletConfig(), renderRes );
					
					renderReq.setAttribute( PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE );
					
					model = new HashMap<String, Object>();
					binderIdL = Long.valueOf( binderId );
				}
				
				WorkspaceTreeHelper.setupWorkspaceBeans( ami, binderIdL, renderReq, renderRes, model, false );
				
				// Put the data that setupWorkspaceBeans() put in model into the request.
				for (String key: model.keySet())
				{
					Object value;
					
					value = model.get( key );
					request.setAttribute( key, value );
				}
				
				// Add the data that normally would have been added by PortletAdapterServlet.java
		    	// This attribute is used to distinguish adapter request from regular request
		    	request.setAttribute( KeyNames.CTX, servletContext );

		    	// Add the data that normally would have been added by PortletAdapterController.java
				{
					request.setAttribute( "javax.portlet.config", portletInfo.getPortletConfig() );
					request.setAttribute( "javax.portlet.request", renderReq );
					request.setAttribute( "javax.portlet.response", renderRes );
					request.setAttribute( PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE );
				}
				
				// Add the data that normally would have been added by mashup_canvas_view.jsp
				{
					Map map1;
					Map map2;
					
					map1 = new HashMap();
					map2 = new HashMap();
					map1.put( 0, "" );
					map2.put( 0, Long.valueOf( 0 ) );

					request.setAttribute( "ss_mashupTableDepth", Long.valueOf( 0 ) );
					request.setAttribute( "ss_mashupTableNumber", Long.valueOf( 0 ) );
					request.setAttribute( "ss_mashupTableItemCount", map1 );
					request.setAttribute( "ss_mashupTableItemCount2", map2 );
					request.setAttribute( "ss_mashupListDepth", Long.valueOf( 0 ) );
				}
				
				// Add the data that normally would have been added by MashupTag.java
				if ( configStr != null )
				{
					String[] mashupItemValues;
					
					mashupItemValues = configStr.split(",");
					if ( mashupItemValues.length > 0 )
					{
						Map mashupItemAttributes;

						//Build a map of attributes
						mashupItemAttributes = new HashMap();
						for (int i = 1; i < mashupItemValues.length; i++)
						{
							int k = mashupItemValues[i].indexOf("=");
							if ( k > 0 )
							{
								String a = mashupItemValues[i].substring(0, k);
								String v = mashupItemValues[i].substring(k+1, mashupItemValues[i].length());
								String value1 = v;
								try
								{
									value1 = URLDecoder.decode(v.replaceAll("\\+", "%2B"), "UTF-8");
								}
								catch(Exception e)
								{
								}
								
								if ( a != null && !a.equalsIgnoreCase( "width" ) && !a.equalsIgnoreCase( "height" ) && !a.equalsIgnoreCase( "overflow" ) )
									mashupItemAttributes.put(a, value1);
							}
						}

						request.setAttribute( "mashup_id", 0 );
						request.setAttribute( "mashup_type", "enhancedView" );
						request.setAttribute( "mashup_values", mashupItemValues );
						request.setAttribute( "mashup_attributes", mashupItemAttributes );
						request.setAttribute( "mashup_view", "view" );
					}
				}
			}
			
			// Execute the jsp
			reqDispatcher.include( request, ssResponse );
			
			results = ssResponse.getString().trim();
			
			/*
			// Put the results of executing the jsp into a temporary file
			{
				File tempFile;
				FileOutputStream fo;
				
				tempFile = TempFileUtil.createTempFile( "landing_page_", ".html", null, true );
				fo = new FileOutputStream( tempFile );
				fo.write( results.getBytes() );
				fo.close();
				
				// Get a url to the file.
				results = WebUrlUtil.getServletRootURL( request ) + WebKeys.SERVLET_VIEW_FILE + "?viewType=executeJspResults&fileId=" + tempFile.getName() + "&fullPath=" + tempFile.getAbsolutePath();
			}
			*/
		}
		catch ( Exception e )
		{
			String[] errorArgs;
			String errorTag = "errorcode.unexpectedError";
			
			errorArgs = new String[] { e.getLocalizedMessage() };
			results = NLT.get( errorTag, errorArgs );
		}
		
		return results;
	}
	
	
	/**
	 * Execute the given enhanced view jsp and return the resulting html.
	 */
	public static String executeJsp( AllModulesInjected bs, HttpServletRequest request, 
			HttpServletResponse response, ServletContext servletContext, 
			String jspName, Map<String,Object> model )
	{
		String results;
		String path;
		RequestDispatcher reqDispatcher;
		StringServletResponse ssResponse;
		
		// Construct the full path to the jsp
		path = "/WEB-INF/jsp/" + jspName;
		
		reqDispatcher = request.getRequestDispatcher( path );
		ssResponse = new StringServletResponse( response );

		try
		{
			RenderRequestImpl renderReq;
			RenderResponseImpl renderRes;
			PortletInfo portletInfo;

			// Gather up all the data required by the jsp
			{
				Long binderIdL;
				Map<String, Object> params;

				// Create the objects needed to call setupStandardBeans
				{
					String portletName;
					String charEncoding;

					portletName = "ss_forum";
					portletInfo = (PortletInfo) AdaptedPortlets.getPortletInfo( portletName );
					
					renderReq = new RenderRequestImpl( request, portletInfo, AdaptedPortlets.getPortletContext() );
					
					params = new HashMap<String, Object>();
					params.put( KeyNames.PORTLET_URL_PORTLET_NAME, new String[] {portletName} );
					renderReq.setRenderParameters( params );
					
					renderRes = new RenderResponseImpl( renderReq, response, portletName );
					charEncoding = SPropsUtil.getString( "web.char.encoding", "UTF-8" );
					renderRes.setContentType( "text/html; charset=" + charEncoding );
					renderReq.defineObjects( portletInfo.getPortletConfig(), renderRes );
					
					renderReq.setAttribute( PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE );
				}
				if (model.containsKey(WebKeys.BINDER_ID)) {
					binderIdL = (Long)model.get(WebKeys.BINDER_ID);
					Binder binder = bs.getBinderModule().getBinder(binderIdL);
					model.put(WebKeys.BINDER, binder);
					BinderHelper.setupStandardBeans( bs, renderReq, renderRes, model, binderIdL );
				} else {
					BinderHelper.setupStandardBeans( bs, renderReq, renderRes, model );
				}
				
				// Put the data that setupWorkspaceBeans() put in model into the request.
				for (String key: model.keySet())
				{
					Object value;
					
					value = model.get( key );
					request.setAttribute( key, value );
				}
				
				// Add the data that normally would have been added by PortletAdapterServlet.java
		    	// This attribute is used to distinguish adapter request from regular request
		    	request.setAttribute( KeyNames.CTX, servletContext );

		    	// Add the data that normally would have been added by PortletAdapterController.java
				{
					request.setAttribute( "javax.portlet.config", portletInfo.getPortletConfig() );
					request.setAttribute( "javax.portlet.request", renderReq );
					request.setAttribute( "javax.portlet.response", renderRes );
					request.setAttribute( PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE );
				}
			}
				
			// Execute the jsp
			reqDispatcher.include( request, ssResponse );
			
			results = ssResponse.getString().trim();
		}
		catch ( Exception e )
		{
			String[] errorArgs;
			String errorTag = "errorcode.unexpectedError";
			
			errorArgs = new String[] { e.getLocalizedMessage() };
			results = NLT.get( errorTag, errorArgs );
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
	 * @param bucketInfo
	 * @param expandedBindersList
	 * 
	 * @return
	 */
	public static TreeInfo expandBucket(HttpServletRequest request, AllModulesInjected bs, BucketInfo bi, List<Long> expandedBindersList) {
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
	public static String getAddMeetingUrl( AllModulesInjected bs, HttpServletRequest request, String binderId ) throws GwtTeamingException
	{
		AdaptedPortletURL adapterUrl;

		// ...store the team meeting URL.
		adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
		adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING );
		adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );

		if (getWorkspaceType(GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId)) == WorkspaceType.USER) {
			// This is a User Workspace so add the owner in and don't append team members
			Principal p = GwtProfileHelper.getPrincipalByBinderId(bs, binderId);
			if (p != null) {
				Long id = p.getId();
				String [] ids = new String[1];
				ids[0] = id.toString();
				adapterUrl.setParameter(WebKeys.USER_IDS_TO_ADD, ids);
			}
			adapterUrl.setParameter( WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.FALSE.toString() );
		} else {
			adapterUrl.setParameter( WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString() );
	    }

		return adapterUrl.toString();
	}
	
	/**
	 * Return a list of administration actions the user has rights to perform. 
	 */
	public static ArrayList<GwtAdminCategory> getAdminActions( HttpServletRequest request, Binder binder, AbstractAllModulesInjected allModules )
	{
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
		
		definitionModule = allModules.getDefinitionModule();
		ldapModule = allModules.getLdapModule();
		adminModule = allModules.getAdminModule();
		profileModule = allModules.getProfileModule();
		workspaceModule = allModules.getWorkspaceModule();
		binderModule = allModules.getBinderModule();
		licenseModule = allModules.getLicenseModule();
		zoneModule = allModules.getZoneModule();
		
		user = getCurrentUser();
		
		isFilr = Utils.checkIfFilr();
		
 		try
 		{
			top = workspaceModule.getTopWorkspace();
 		}
 		catch( Exception e )
 		{}
 		
 		profilesBinder = profileModule.getProfileBinder();
		
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
				if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
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

			// Does the user have rights to "Manage the search index"?
			if ( top != null )
			{
				if ( ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() ) && 
					  binderModule.testAccess( top, BinderOperation.indexBinder ) )
				{
					// Yes
					if ( adminModule.retrieveIndexNodes() != null )
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

			// Does the user have rights to "Manage groups"?
			try
			{
				if ( profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
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

			// Does the user have rights to "Manage quotas"?
			try
			{
				if ( adminModule.testAccess( AdminOperation.manageFunction ) )
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

			// Does the user have rights to "Manage resource drivers"?
			try
			{
				if ( adminModule.testAccess( AdminOperation.manageFunction ) &&
					 LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder") &&
					 adminModule.testAccess( AdminOperation.manageResourceDrivers ) &&
					 binderModule.testAccess( top, BinderOperation.indexBinder ) )
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

				if ( adminModule.testAccess( AdminOperation.manageFunction ) &&
					 LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder") &&
					 netFoldersParentBinder != null &&
					 binderModule.testAccess( netFoldersParentBinder, BinderOperation.modifyBinder ) )
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

			// Does the user have rights to "manage adhoc folder?"?
			if ( isFilr && adminModule.testAccess( AdminOperation.manageFunction ) )
			{
				// Yes
				title = NLT.get( "administration.configure_adhocFolders" );

				adminAction = new GwtAdminAction();
				adminAction.init( title, "", AdminAction.CONFIGURE_ADHOC_FOLDERS );
				
				// Add this action to the "management" category
				managementCategory.addAdminOption( adminAction );
			}

			// Does the user have rights to "Manage workspace and folder templates"?
			if ( isFilr == false && adminModule.testAccess( AdminOperation.manageTemplate ) )
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

			// Does the user have rights to "Manage zones"?
			if ( isFilr == false && LicenseChecker.isAuthorizedByLicense( "com.novell.teaming.module.zone.MultiZone" ) &&
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
				if ( isFilr == false && profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
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
				if ( isFilr == false && profileModule.testAccess( profilesBinder, ProfileOperation.manageEntries ) )
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
			if ( isFilr == false && adminModule.testAccess( AdminOperation.manageExtensions ) )
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
		
		// Create a "System" category
		{
			systemCategory = new GwtAdminCategory();
			systemCategory.setLocalizedName( NLT.get( "administration.category.system" ) );
			systemCategory.setCategoryType( GwtAdminCategory.GwtAdminCategoryType.SYSTEM );
			adminCategories.add( systemCategory );

			// Does the user have rights to "Form/View Designers"?
			if ( isFilr == false && ( null != top ) &&
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
			
			// Does the user have rights to "configure user access"?
			if ( adminModule.testAccess( AdminOperation.manageFunction ) )
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

			// Does the user have rights to "configure mobile access"?
			if ( isFilr == false && adminModule.testAccess( AdminOperation.manageFunction ) )
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
			if ( isFilr == false && adminModule.testAccess( AdminOperation.manageFunction ) )
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
			if ( isFilr == false && adminModule.testAccess( AdminOperation.manageFunction ) )
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
			if ( isFilr == false && adminModule.testAccess( AdminOperation.manageFunction ) )
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

			// Does the user have rights to "Configure File Version Aging"?
			if ( isFilr == false && adminModule.testAccess( AdminOperation.manageFileVersionAging ) )
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

			// Does the user have rights to "Site Branding"
			if ( isFilr == false && top != null && binderModule.testAccess( top, BinderOperation.modifyBinder ) )
			{
				// Yes
				title = NLT.get( "administration.modifySiteBranding" );

				adminAction = new GwtAdminAction();
				adminAction.init( title, "", AdminAction.SITE_BRANDING );
				
				// Add this action to the "system" category
				systemCategory.addAdminOption( adminAction );
			}
			
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
			// Create an "email report"
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
		if ( isFilr == false && adminModule.testAccess( AdminOperation.manageFunction ) )
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

		// Sort the administration actions in each category
		GwtAdminActionComparator comparator;
		comparator = new GwtAdminActionComparator();
		for ( GwtAdminCategory category : adminCategories )
		{
			ArrayList<GwtAdminAction> adminActions;
			
			// Do we have more than 1 administration action in this category?
			adminActions = category.getActions();
			if ( adminActions != null && adminActions.size() > 1 )
			{
				// Yes, sort them.
				Collections.sort( adminActions, comparator );
			}
		}
		
		return adminCategories;
	}// end getAdminActions()
	
	
	/**
	 * Return a list of all the groups in Vibe
	 */
	public static List<GroupInfo> getAllGroups( AllModulesInjected ami ) throws GwtTeamingException
	{
		ArrayList<GroupInfo> reply;
		
		reply = new ArrayList<GroupInfo>();
		
		try
		{
			Map options;
			Map searchResults;
			List groups;
			
			options = new HashMap();
			options.put( ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD );
			options.put( ObjectKeys.SEARCH_SORT_DESCEND, Boolean.FALSE );
			options.put( ObjectKeys.SEARCH_MAX_HITS, Integer.MAX_VALUE-1 );
			
			// Exclude allUsers and allExtUsers from the search
			{
				Document searchFilter;
				Element rootElement, orElement;
				Element field;
		    	Element child;
	
				searchFilter = DocumentHelper.createDocument();
				rootElement = searchFilter.addElement( Constants.NOT_ELEMENT );
				orElement = rootElement.addElement(Constants.OR_ELEMENT);
				
				field = orElement.addElement( Constants.FIELD_ELEMENT );
		    	field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE, Constants.GROUPNAME_FIELD );
		    	child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
		    	child.setText( "allUsers" );
		    	
		    	field = orElement.addElement( Constants.FIELD_ELEMENT );
		    	field.addAttribute( Constants.FIELD_NAME_ATTRIBUTE, Constants.GROUPNAME_FIELD );
		    	child = field.addElement( Constants.FIELD_TERMS_ELEMENT );
		    	child.setText( "allExtUsers" );
		    	
		    	options.put( ObjectKeys.SEARCH_FILTER_AND, searchFilter );
			}
	
			// Get the list of all the groups.
			searchResults = ami.getProfileModule().getGroups( options );
	
			groups = (List) searchResults.get( ObjectKeys.SEARCH_ENTRIES );
			
			if ( groups != null )
			{
				int i;
				
				for (i = 0; i < groups.size(); ++i)
				{
					HashMap nextMap;
					GroupInfo grpInfo;
					Long id;
					
					if ( groups.get( i ) instanceof HashMap )
					{
						Object value;
						
						nextMap = (HashMap) groups.get( i );
					
						grpInfo = new GroupInfo();
						id = Long.valueOf( (String) nextMap.get( "_docId" ) );
						grpInfo.setId( id );
						grpInfo.setTitle( (String) nextMap.get( "title" ) );
						grpInfo.setName( (String) nextMap.get( "_groupName" ) );
						
						value = nextMap.get( "_desc" );
						if ( value != null && value instanceof String )
							grpInfo.setDesc( (String) value );
						
						reply.add( grpInfo );
					}
				}
			}
		}
		catch ( Exception ex )
		{
			throw getGwtTeamingException( ex );
		} 
		
		return reply;
	}
	
	/**
	 * Return the branding data for the given binder.
	 */
	public static GwtBrandingData getBinderBrandingData( AbstractAllModulesInjected allModules, String binderId, HttpServletRequest request ) throws GwtTeamingException
	{
		BinderModule binderModule;
		Binder binder;
		Long binderIdL;
		GwtBrandingData brandingData;
		
		brandingData = new GwtBrandingData();
		brandingData.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_IMAGE );
		
		try
		{
			binderModule = allModules.getBinderModule();
	
			binderIdL = new Long( binderId );
			
			// Get the binder object.
			if ( binderIdL != null )
			{
				String branding;
				GwtBrandingDataExt brandingExt;
				Binder brandingSourceBinder;
				String brandingSourceBinderId;
				
				binder = binderModule.getBinder( binderIdL );
				
				// Get the binder where branding comes from.
				brandingSourceBinder = binder.getBrandingSource();
				
				// Does the user have rights to the binder where the branding is coming from?
				if ( !binderModule.testAccess( brandingSourceBinder, BinderOperation.readEntries ) )
				{
					// No, don't use inherited branding.
					brandingSourceBinderId = binderId;
					brandingSourceBinder = binder;
				}
				else
				{
					brandingSourceBinderId = brandingSourceBinder.getId().toString();
				}
				
				brandingData.setBinderId( brandingSourceBinderId );

				// Get the branding that should be applied for this binder.
				branding = brandingSourceBinder.getBranding();
				
				// For some unknown reason, if there is no branding in the db the string we get back
				// will contain only a \n.  We don't want that.
				if ( branding != null && branding.length() == 1 && branding.charAt( 0 ) == '\n' )
					branding = "";
				
				// Parse the branding and replace any markup with the appropriate url.  For example,
				// replace {{atachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
				branding = MarkupUtil.markupStringReplacement( null, null, request, null, brandingSourceBinder, branding, "view" );
	
				// Remove mce_src as an attribute from all <img> tags.  See bug 766415.
				// There was a bug that caused the mce_src attribute to be included in the <img>
				// tag and written to the db.  We want to remove it.
				branding = MarkupUtil.removeMceSrc( branding );

				brandingData.setBranding( branding );
				
				// Get the additional branding information.
				{
					String xmlStr;
					
					brandingExt = new GwtBrandingDataExt();
					
					// Get the xml that represents the branding data.  The following is an example of what the xml should look like.
					// 	<brandingData fontColor="" brandingImgName="some name" brandingType="image/advanced">
					// 		<background color="" imgName="" />
					// 	</brandingData>
					xmlStr = brandingSourceBinder.getBrandingExt();
					
					// Is there old-style branding?
					if ( branding != null && branding.length() > 0 )
					{
						// Yes
						brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_ADVANCED );
						
						// Is there additional branding data?
						if ( xmlStr == null || xmlStr.length() == 0 )
						{
							// Yes, We are dealing with branding that was created before the Durango release.
							// In order to have existing branding still look good even though we aren't
							// using the old background image, we will set the background color to white
							// and the font color to black.
							brandingExt.setBackgroundColor( "white" );
							brandingExt.setFontColor( "black" );
						}
					}
					else
					{
						// No
						brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_IMAGE );
					}

					if ( xmlStr != null )
					{
						try
			    		{
			    			Document doc;
			    			Node node;
			    			Node attrNode;
		        			String imgName;
							String fileUrl;
							String webPath;
							
							webPath = WebUrlUtil.getServletRootURL( request );

							// Parse the xml string into an xml document.
							doc = DocumentHelper.parseText( xmlStr );
			    			
			    			// Get the root element.
			    			node = doc.getRootElement();
			    			
			    			// Get the font color.
			    			attrNode = node.selectSingleNode( "@fontColor" );
			    			if ( attrNode != null )
			    			{
			        			String fontColor;
			
			        			fontColor = attrNode.getText();
			        			brandingExt.setFontColor( fontColor );
			    			}
			    			
			    			// Get the name of the branding image
			    			attrNode = node.selectSingleNode( "@brandingImgName" );
			    			if ( attrNode != null )
			    			{
			        			imgName = attrNode.getText();

			    				if ( imgName != null && imgName.length() > 0 )
			    				{
			    					brandingExt.setBrandingImgName( imgName );

			    					// Is the image name "__no image__" or "__default teaming image__"?
			    					// These are special names that don't represent a real image file name.
			    					if ( !imgName.equalsIgnoreCase( "__no image__" ) && !imgName.equalsIgnoreCase( "__default teaming image__" ) )
			    					{
			    						// No, Get a url to the file.
				    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, brandingSourceBinder, imgName );
				    					brandingExt.setBrandingImgUrl( fileUrl );
			    					}
			    				}
			    			}
			    			
			    			// Get the type of branding, "advanced" or "image"
			    			attrNode = node.selectSingleNode( "@brandingType" );
			    			if ( attrNode != null )
			    			{
			    				String type;
			    				
			    				type = attrNode.getText();
			    				if ( type != null && type.equalsIgnoreCase( GwtBrandingDataExt.BRANDING_TYPE_IMAGE ) )
			    					brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_IMAGE );
			    				else
			    					brandingExt.setBrandingType( GwtBrandingDataExt.BRANDING_TYPE_ADVANCED );
			    			}
			    			
			    			// Get the branding rule.
			    			attrNode = node.selectSingleNode( "@brandingRule" );
			    			if ( attrNode != null )
			    			{
			    				String ruleName;
			    				
			    				ruleName = attrNode.getText();
			    				if ( ruleName != null )
			    				{
			    					if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING );
			    					else if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING );
			    					else if ( ruleName.equalsIgnoreCase( GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY.toString() ) )
			    						brandingExt.setBrandingRule( GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY );
			    				}
			    			}
			    			
			    			// Get the <background color="" imgName="" stretchImg="" /> node
			    			node = node.selectSingleNode( "background" );
			    			if ( node != null )
			    			{
			    				// Get the background color.
			    				attrNode = node.selectSingleNode( "@color" );
			    				if ( attrNode != null )
			    				{
			        				String bgColor;
			
			        				bgColor = attrNode.getText();
			        				brandingExt.setBackgroundColor( bgColor );
			    				}
			    				
			    				// Get the name of the background image.
			    				attrNode = node.selectSingleNode( "@imgName" );
			    				if ( attrNode != null )
			    				{
			        				imgName = attrNode.getText();

				    				if ( imgName != null && imgName.length() > 0 )
				    				{
				    					// Get a url to the file.
				    					fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, brandingSourceBinder, imgName );
				    					brandingExt.setBackgroundImgUrl( fileUrl );
				    					
				    					brandingExt.setBackgroundImgName( imgName );
				    				}
			    				}

			    				// Get the value of whether or not to stretch the background image.
	        					brandingExt.setBackgroundImgStretchValue( true );
			    				attrNode = node.selectSingleNode( "@stretchImg" );
			    				if ( attrNode != null )
			    				{
			        				String stretch;
			
			        				stretch = attrNode.getText();
			        				if ( stretch != null && stretch.equalsIgnoreCase( "false" ) )
			        					brandingExt.setBackgroundImgStretchValue( false );
			    				}
			    			}
			    		}
			    		catch(Exception e)
			    		{
			    			m_logger.warn( "Unable to parse branding ext " + xmlStr );
			    		}
					}
					
					brandingData.setBrandingExt( brandingExt );
				}
				
				// Are we dealing with site branding?
				{
					Long topWorkspaceId;
					
					// Get the top workspace ID.
					topWorkspaceId = allModules.getWorkspaceModule().getTopWorkspaceId();				
					
					// Are we dealing with the site branding.
					if ( binderIdL.compareTo( topWorkspaceId ) == 0 )
					{
						// Yes
						brandingData.setIsSiteBranding( true );
					}
				}
			}
		}
		catch (NoBinderByTheIdException nbEx)
		{
			// Nothing to do
		}
		catch (AccessControlException acEx)
		{
			// Nothing to do
		}
		catch (Exception e)
		{
			// Nothing to do
		}
		
		return brandingData;
	}// end getBinderBrandingData()

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
			m_logger.debug("GwtServerHelper.getBinderForWorkspaceTree( Can't Access Binder (NumberFormatException) ):  '" + ((null == binderId) ? "<nul>" : binderId) + "'");
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
			reply = bs.getBinderModule().getBinder(binderId);
		}
		catch (Exception e) {
			m_logger.debug("GwtServerHelper.getBinderForWorkspaceTree( Can't Access Binder (AccessControlException) ):  '" + String.valueOf(binderId) + "'");
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
				m_logger.debug("GwtServerHelper.getBinderForWorkspaceTree( Can't Default to Top Workspace ) ");
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
		                                    reply.setBinderId(     binder.getId()                                 );
		                                    reply.setBinderTitle(  binder.getTitle()                              );
		                                    reply.setFolderHome(   binder.isHomeDir()                             );
		                                    reply.setLibrary(      binder.isLibrary()                             );
		                                    reply.setEntityType(   getBinderEntityType(                   binder ));
		                                    reply.setBinderType(   getBinderType(                         binder ));
		if      (reply.isBinderFolder())    reply.setFolderType(   getFolderTypeFromViewDef(bs, ((Folder) binder)));
		else if (reply.isBinderWorkspace()) reply.setWorkspaceType(getWorkspaceType(                      binder ));
		try
		{
			Binder binderParent = binder.getParentBinder();
			if ( null != binderParent )
			{
				reply.setParentBinderId( binderParent.getId() );
			}
		}
		catch ( Exception e )
		{
			// Ignore.
		}

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
					desc = MarkupUtil.markupStringReplacement( null, null, request, null, binder, desc, WebKeys.MARKUP_VIEW, false );
					desc = MarkupUtil.markupSectionsReplacement( desc );
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
	 * Returns the user's search filters as a single Document.
	 * 
	 * @param bs
	 * @param binder
	 * @param userFolderProperties
	 * @param unescapeName
	 */
	public static Document getBinderSearchFilter(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties, boolean unescapeName) {
		// Convert any existing V1 filters.
		BinderHelper.convertV1Filters(bs, userFolderProperties);

		// Does the user have any filters selected on this folder?
		List<String> currentFilters = getCurrentUserFilters(userFolderProperties);
		if (!(currentFilters.isEmpty())) {
			// Yes!  Get the personal and global filters from the
			// binder properties.
			Map personalFilters = ((Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS));
			Map globalFilters   = ((Map) binder.getProperty(              ObjectKeys.BINDER_PROPERTY_FILTERS)     );

			// Scan the user's filters...
			SearchFilter searchFilter = new SearchFilter(true);
			for (String filterSpec: currentFilters) {
				// ...extracting the name...
				String filterName  = BinderFilter.getFilterNameFromSpec( filterSpec);
				if (unescapeName) {
					filterName = MiscUtil.replace(filterName, "+", " ");
				}
				
				// ...scope...
				String  filterScope = BinderFilter.getFilterScopeFromSpec(filterSpec);
				boolean isGlobal    = filterScope.equals(ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL);
				
				// ...and filter XML for each.
				String searchFilterXml;
				if (isGlobal)
				     searchFilterXml = ((String) globalFilters.get(  filterName));
				else searchFilterXml = ((String) personalFilters.get(filterName));

				// Do we have XML for this filter?
				if (MiscUtil.hasString(searchFilterXml)) {
					Document searchFilterDoc;
					try {
						// Yes!  Parse it and append it to the search
						// filter.
						searchFilterDoc = DocumentHelper.parseText(searchFilterXml);
						searchFilter.appendFilter(searchFilterDoc);
					}
					
					catch (Exception ignore) {
						// Log the exception...
						m_logger.debug("GwtServerHelperHelper.addSearchFiltersToOptions(Exception:  '" + MiscUtil.exToString(ignore) + "')");
						
						// ...get rid of the bogus filter.
						if (isGlobal) {
							globalFilters.remove(  searchFilterXml);
							bs.getBinderModule().setProperty(
								binder.getId(),
								ObjectKeys.BINDER_PROPERTY_FILTERS,
								globalFilters);
						}
						
						else {
							personalFilters.remove(searchFilterXml);
							bs.getProfileModule().setUserProperty(
								userFolderProperties.getId().getPrincipalId(),
								userFolderProperties.getId().getBinderId(),
								ObjectKeys.USER_PROPERTY_SEARCH_FILTERS,
								personalFilters);
						}
					}
				}
			}

			// If we get here, searchFilter contains the combined
			// filters that the user has selected.  Stuff the Document
			// into the options Map.
			return searchFilter.getFilter();
		}
		
		else {
			return null;
		}
	}
	
	/**
	 * Return a BinderStats object that holds all of the statistical information for
	 * the given binder.
	 * @throws GwtTeamingException 
	 * @throws  
	 */
	public static BinderStats getBinderStats( AllModulesInjected ami, String binderId ) throws GwtTeamingException
	{
		BinderStats binderStats;
		TaskStats taskStats;
		MilestoneStats milestoneStats;
		Long binderIdL;
		
		binderStats = new BinderStats();
		binderIdL = Long.valueOf( binderId );
		
		// Get the statistics for the tasks that may be in the given binder.
		taskStats = GwtTaskHelper.getTaskStatistics( ami, binderIdL );
		binderStats.setTaskStats( taskStats );
		
		// Get the statistics for the milestones that may be in the given binder.
		milestoneStats = GwtStatisticsHelper.getMilestoneStatistics( ami, binderIdL );
		binderStats.setMilestoneStats( milestoneStats );
		
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
			m_logger.debug("GwtServerHelper.getBinderType( 'Could not determine binder type' ):  " + binder.getPathName());
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
			throw getGwtTeamingException(ex);
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
			throw getGwtTeamingException(ex);
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
			throw getGwtTeamingException(ex);
		}
	}

	/**
	 * Return the url needed to display the given collection point
	 */
	private static String getCollectionPointUrl(
		HttpServletRequest request,
		Workspace userWS,
		CollectionType collectionType )
	{
		String url;
		
		url = PermaLinkUtil.getPermalink( request, userWS );
		url = GwtUIHelper.appendUrlParam(
										url,
										WebKeys.URL_SHOW_COLLECTION,
										String.valueOf( collectionType.ordinal() ) );
		return url;
	}

	/**
	 * Returns the data about all the collection points.
	 */
	public static CollectionPointData getCollectionPointData(
		AllModulesInjected ami,
		HttpServletRequest request ) throws GwtTeamingException
	{
		CollectionPointData results;
		User user;
		Long userWSId;
		Workspace userWS;

		results = new CollectionPointData();
		
		user = getCurrentUser();
		userWSId = user.getWorkspaceId();
		try 
		{
			CollectionType collectionType;
			String url;
			
			userWS = ami.getWorkspaceModule().getWorkspace(userWSId);

			collectionType = CollectionType.MY_FILES;
			url = getCollectionPointUrl( request, userWS, collectionType );
			results.setUrl( collectionType, url );

			collectionType = CollectionType.NET_FOLDERS;
			url = getCollectionPointUrl( request, userWS, collectionType );
			results.setUrl( collectionType, url );

			collectionType = CollectionType.SHARED_BY_ME;
			url = getCollectionPointUrl( request, userWS, collectionType );
			results.setUrl( collectionType, url );

			collectionType = CollectionType.SHARED_WITH_ME;
			url = getCollectionPointUrl( request, userWS, collectionType );
			results.setUrl( collectionType, url );
		}
		catch ( Exception e ) 
		{
			// If this is the guest user...
			if ( user.isShared() )
			{
				// ...simply ignore the error and bail.
				return null;
			}
			
			// For all other users, convert this to a
			// GwtTeamingExcepton and throw that.
			throw getGwtTeamingException( e );
		}
		
		return results;
	}
	
	/**
	 * 
	 */
	private static CoreDao getCoreDao()
	{
		return (CoreDao) SpringContextUtil.getBean( "coreDao" );
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
	 * Returns a List<String> of the user's current filters from their
	 * properties on a folder.
	 * 
	 * @param userFolderProperties
	 * 
	 * @return
	 */
	public static List<String> getCurrentUserFilters(UserProperties userFolderProperties, boolean unescapeName) {
		List<String> currentFilters = ((List<String>) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTERS));
		if (null == currentFilters) {
			currentFilters = new ArrayList<String>();
		}
		String filterName = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER));
		if (MiscUtil.hasString(filterName)) {
			String filterScope = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE));
			if (!(MiscUtil.hasString(filterScope))) {
				filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL;
			}
			if (unescapeName) {
				filterName = MiscUtil.replace(filterName, "+", " ");
			}
			String filterSpec = BinderFilter.buildFilterSpec(filterName, filterScope);
			if (!(currentFilters.contains(filterSpec))) {
				currentFilters.add(filterSpec);
			}
		}
		return currentFilters;
	}
	
	public static List<String> getCurrentUserFilters(UserProperties userFolderProperties) {
		// Always use the initial form of the method.
		return getCurrentUserFilters(userFolderProperties, false);
	}

	/**
	 * Returns a DesktopAppDownloadInfoRpcResponseData object
	 * containing the information for downloading the desktop
	 * applications.
	 * 
	 * @param bs
	 * @param request
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static DesktopAppDownloadInfoRpcResponseData getDesktopAppDownloadInformation(AllModulesInjected bs, HttpServletRequest request) throws GwtTeamingException {
		try {
			// Construct the DesktopAppDownloadInfoRpcResponseData
			// object we'll fill in and return.
			DesktopAppDownloadInfoRpcResponseData reply = new DesktopAppDownloadInfoRpcResponseData();

			// Extract the base desktop application update URL and
			// validate it for any redirects, ...
			ZoneConfig	zc      = bs.getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			String		baseUrl = getFinalHttpUrl(zc.getFsaAutoUpdateUrl(), "From getDesktopAppDownloadInformation()");
			if (MiscUtil.hasString(baseUrl)) {
				// ...and construct and store the desktop
				// ...application information.
				boolean isFilr = Utils.checkIfFilr();
				reply.setMac(  buildDesktopAppUrl(baseUrl, (isFilr ? MACOS_TAIL_FILR : MACOS_TAIL_VIBE)));
				reply.setWin32(buildDesktopAppUrl(baseUrl, (isFilr ? WIN32_TAIL_FILR : WIN32_TAIL_VIBE)));
				reply.setWin64(buildDesktopAppUrl(baseUrl, (isFilr ? WIN64_TAIL_FILR : WIN64_TAIL_VIBE)));
			}
			
			// If we get here, reply refers to the
			// DesktopAppDownloadInfoRpcResponseData object
			// containing the information about downloading the desktop
			// application.  Return it.
			return reply;
		}
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
		}		
	}

	/**
	 * Return a download file URL that can be used to download an
	 * entry's file.
	 * 
	 * @param request
	 * @param bs
	 * @param binderId
	 * @param entryId
	 * @param asPermalink
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static String getDownloadFileUrl(HttpServletRequest request, AllModulesInjected bs, Long binderId, Long entryId, boolean asPermalink) throws GwtTeamingException {
		try {
			// Does the entry have the name of a primary file attribute
			// stored? 
			FolderEntry		entry  = bs.getFolderModule().getEntry(null, entryId);
			FileAttachment	dlAttr = null;
			Map				model  = new HashMap();
			DefinitionHelper.getPrimaryFile(entry, model);
			String attrName = ((String) model.get(WebKeys.PRIMARY_FILE_ATTRIBUTE));
			if (MiscUtil.hasString(attrName)) {
				// Yes!  Can we access any custom attribute values for
				// that attribute?
				CustomAttribute ca = entry.getCustomAttribute(attrName);
				if (null != ca) {
					Set values = ca.getValueSet();
					if (MiscUtil.hasItems(values)) {
						// Yes!  Use the first one in the set as the
						// one to download.
						dlAttr = ((FileAttachment) values.iterator().next());
					}
				}
			}

			// Do we have the file attachment for the file to download?
			if (null == dlAttr) {
				// No!  Does the entry have any file attachments?
				Set<FileAttachment> atts = entry.getFileAttachments();
				if (MiscUtil.hasItems(atts)) {
					// Yes!  Download the first one. 
					dlAttr = atts.iterator().next();
				}
			}

			// If we have a file attribute to download, generate a URL
			// to download it.  Otherwise, return null.
			String reply;
			if (null != dlAttr)
				 reply = WebUrlUtil.getFileUrl(request, WebKeys.ACTION_READ_FILE, dlAttr, false, true);
			else reply = null;
			return reply;
		}
		
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
		}		
	}
	
	public static String getDownloadFileUrl(HttpServletRequest request, AllModulesInjected bs, Long binderId, Long entryId) throws GwtTeamingException {
		// Always use the initial form of the method.
		return getDownloadFileUrl(request, bs, binderId, entryId, false);
	}
	
	/**
	 * Return the groups ldap query
	 */
	public static GwtDynamicGroupMembershipCriteria getDynamicMembershipCriteria( AllModulesInjected ami, Long groupId )
	{
		Principal principal;
		GwtDynamicGroupMembershipCriteria membershipCriteria;
		
		membershipCriteria = new GwtDynamicGroupMembershipCriteria();
		
		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			Group group;
			String ldapQueryXml;

			// Get the xml that defines the membership criteria
			group = (Group) principal;
			ldapQueryXml = group.getLdapQuery();

			if ( ldapQueryXml != null && ldapQueryXml.length() > 0 )
			{
				try
	    		{
	    			Document doc;
	    			Node node;
	    			Node searchNode;
	    			Node attrNode;
	    			String value;
					
					// Parse the xml string into an xml document.
					doc = DocumentHelper.parseText( ldapQueryXml );
	    			
	    			// Get the root element.
	    			node = doc.getRootElement();
	    			
	    			// Get the "updateMembershipDuringLdapSync" attribute value.
	    			attrNode = node.selectSingleNode( "@updateMembershipDuringLdapSync" );
	    			if ( attrNode != null )
	    			{
	        			value = attrNode.getText();
	        			if ( value != null && value.equalsIgnoreCase( "true" ) )
	        				membershipCriteria.setUpdateDuringLdapSync( true );
	        			else
	        				membershipCriteria.setUpdateDuringLdapSync( false );
	    			}
	    			
	    			// Get the <search ...> element.
	    			searchNode = node.selectSingleNode( "search" );
	    			if ( searchNode != null )
	    			{
    					Node baseDnNode;
    					Node filterNode;
    					
	    				// Get the "searchSubtree" attribute.
	    				attrNode = searchNode.selectSingleNode( "@searchSubtree" );
	    				if ( attrNode != null )
	    				{
	    					value = attrNode.getText();
	    					if ( value != null && value.equalsIgnoreCase( "true" ) )
	    						membershipCriteria.setSearchSubtree( true );
	    					else
	    						membershipCriteria.setSearchSubtree( false );
	    				}
	    				
	    				// Get the <baseDn> element.
	    				baseDnNode = searchNode.selectSingleNode( "baseDn" );
	    				if ( baseDnNode != null )
	    				{
	    					value = baseDnNode.getText();
	    					membershipCriteria.setBaseDn( value );
	    				}
	    				
	    				// Get the <filter> element.
	    				filterNode = searchNode.selectSingleNode( "filter" );
	    				if ( filterNode != null )
	    				{
	    					value = filterNode.getText();
	    					membershipCriteria.setLdapFilter( value );
	    				}
	    			}
	    		}
	    		catch(Exception e)
	    		{
	    			m_logger.warn( "Unable to parse dynamic group membership criteria" + ldapQueryXml );
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
		User      user          = ((User) getValueFromEntryMap(entryMap, Constants.PRINCIPAL_FIELD));
		String    userEMA       = ((null == user) ? null : user.getEmailAddress());
		Workspace userWS        = getUserWorkspace(user);
		boolean   userHasWS     = (null != userWS); 
		boolean   userWSInTrash = (userHasWS && userWS.isPreDeleted());
		return new EmailAddressInfo(userEMA, userHasWS, userWSInTrash);
	}
	
	/**
	 * For the given role, find the corresponding function id
	 */
	public static Long getFunctionIdFromRole(
		AllModulesInjected ami,
		GwtRole role )
	{
		Long fnId = null;
		String fnInternalId = null;
		List<Function> listOfFunctions;

		if ( role == null )
		{
			m_logger.error( "In GwtNetFolderHelper.getFunctionIdFromRole(), invalid parameter" );
			return null;
		}
		
		fnInternalId = getFunctionInternalIdFromRole( ami, role );
		
		// Did we find the function's internal id?
		if ( fnInternalId == null )
		{
			// No
			m_logger.error( "In GwtServerHelper.getFunctionIdFromRole(), could not find internal function id for role: " + role.getType() );
			return null;
		}

		// Get a list of all the functions;
		listOfFunctions = ami.getAdminModule().getFunctions();
		
		// For the given internal function id, get the function's real id.
		for ( Function nextFunction : listOfFunctions )
		{
			String nextInternalId;
			
			nextInternalId = nextFunction.getInternalId();
			if ( fnInternalId.equalsIgnoreCase( nextInternalId ) )
			{
				fnId = nextFunction.getId();
				break;
			}
		}
		
		return fnId;
	}

	/**
	 * For the given role, find the corresponding function internal id
	 */
	public static String getFunctionInternalIdFromRole(
		AllModulesInjected ami,
		GwtRole role )
	{
		String fnInternalId = null;

		if ( role == null )
		{
			m_logger.error( "In GwtNetFolderHelper.getFunctionInternalIdFromRole(), invalid parameter" );
			return null;
		}
		
		// Get the internal id of the appropriate function
		switch ( role.getType() )
		{
		case ShareExternal:
			fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID;
			break;
			
		case ShareForward:
			fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID;
			break;
			
		case ShareInternal:
			fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID;
			break;
			
		case SharePublic:
			fnInternalId = ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID;
			break;
			
		case AllowAccess:
			fnInternalId = ObjectKeys.FUNCTION_ALLOW_ACCESS_NET_FOLDER_INTERNALID;
			break;
			
		case EnableShareExternal:
			fnInternalId = ObjectKeys.FUNCTION_ENABLE_EXTERNAL_SHARING_INTERNALID;
			break;
		
		case EnableShareForward:
			fnInternalId = ObjectKeys.FUNCTION_ENABLE_FORWARD_SHARING_INTERNALID;
			break;
			
		case EnableShareInternal:
			fnInternalId = ObjectKeys.FUNCTION_ENABLE_INTERNAL_SHARING_INTERNALID;
			break;
		
		case EnableSharePublic:
			fnInternalId = ObjectKeys.FUNCTION_ENABLE_PUBLIC_SHARING_INTERNALID;
			break;
			
		case EnableShareWithAllExternal:
			fnInternalId = ObjectKeys.FUNCTION_ENABLE_SHARING_ALL_EXTERNAL_INTERNALID;
			break;
		
		case EnableShareWithAllInternal:
			fnInternalId = ObjectKeys.FUNCTION_ENABLE_SHARING_ALL_INTERNAL_INTERNALID;
			break;
		}
		
		// Did we find the function's internal id?
		if ( fnInternalId == null )
		{
			// No
			m_logger.error( "In GwtServerHelper.getFunctionInternalIdFromRole(), could not find internal function id for role: " + role.getType() );
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
		     reply = getBinderTitle(bs,                        entityId.getEntityId());
		else reply = getEntryTitle(bs, entityId.getBinderId(), entityId.getEntityId());;
		return reply;
	}

	/**
	 * Return a GwtFileSyncAppConfiguration object that holds the File Sync App configuration data
	 * 
	 * @return
	 */
	public static GwtFileSyncAppConfiguration getFileSyncAppConfiguration( AllModulesInjected allModules )
	{
		GwtFileSyncAppConfiguration fileSyncAppConfiguration;
		ZoneConfig zoneConfig;
		ZoneModule zoneModule;
		
		zoneModule = allModules.getZoneModule();
		zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
		
		fileSyncAppConfiguration = new GwtFileSyncAppConfiguration();
		
		// Get the whether the File Sync App is enabled.
		fileSyncAppConfiguration.setIsFileSyncAppEnabled( zoneConfig.getFsaEnabled() );
		
		// Get the setting that determines whether the desktop app can remember the password
		fileSyncAppConfiguration.setAllowCachePwd( zoneConfig.getFsaAllowCachePwd() );
		
		// Get the max file size the desktop app can download
		fileSyncAppConfiguration.setMaxFileSize( zoneConfig.getFsaMaxFileSize() );
		
		// Get the File Sync App sync interval.
		fileSyncAppConfiguration.setSyncInterval( zoneConfig.getFsaSynchInterval() );
		
		// Get the auto-update url.
		fileSyncAppConfiguration.setAutoUpdateUrl( zoneConfig.getFsaAutoUpdateUrl() );
		
		// Get whether deployment of the file sync app is enabled.
		fileSyncAppConfiguration.setIsDeploymentEnabled( zoneConfig.getFsaDeployEnabled() );
		
		return fileSyncAppConfiguration;
	}
	
	
	/**
	 * Return the url for the given binder and file
	 */
	public static String getFileUrl( AllModulesInjected ami, HttpServletRequest request, String binderId, String fileName )
	{
		String webPath;
		String url = null;
		
		try
		{
			Binder binder;
			
			binder = ami.getBinderModule().getBinder( Long.parseLong( binderId ) );

			webPath = WebUrlUtil.getServletRootURL( request );
			url = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, binder, fileName );
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
		
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
	
	public static ArrayList<TagInfo> getEntryTags( AllModulesInjected bs, String entryId ) {
		// Always use the previous form of the method.
		Long entryIdL = Long.parseLong(entryId);
		return getEntryTags(bs, entryIdL);
	}
	
	
	/**
	 * Return the URL needed to execute the given jsp
	 */
	public static String getExecuteJspUrl( HttpServletRequest request, String binderId, String jspName ) throws GwtTeamingException
	{
		AdaptedPortletURL adapterUrl;

		adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
		adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_EXECUTE_JSP );
		adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
		adapterUrl.setParameter( WebKeys.JSP_NAME, jspName );

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
		m_logger.debug("GwtServerHelper.getDefaultFolderDefinitionId( binderId:  '" + binderIdS + "' ):  '" + reply);
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
		// Is the FolderEntry a file entry?
		FileAttachment reply = null;
		if ((!validateAsFileEntry) || isFamilyFile(getFolderEntityFamily(bs, fileEntry))) {
			// Yes!  Does it have the name of the primary file
			// attachment attribute?
			Map model  = new HashMap();
			DefinitionHelper.getPrimaryFile(fileEntry, model);
			String attrName = ((String) model.get(WebKeys.PRIMARY_FILE_ATTRIBUTE));
			if (MiscUtil.hasString(attrName)) {
				// Yes!  Can we access any custom attribute values for
				// that attribute?
				CustomAttribute ca = fileEntry.getCustomAttribute(attrName);
				if (null != ca) {
					Collection values = ca.getValueSet();
					if (MiscUtil.hasItems(values)) {
						// Yes!  Scan them.
						Iterator vi = values.iterator();
						while (vi.hasNext()) {
							// Does this attachment have a filename?
							FileAttachment fa = ((FileAttachment) vi.next());
							String fName = fa.getFileItem().getName();
							if (MiscUtil.hasString(fName)) {
				        		// Return it as the filename.
								reply = fa;
								break;
							}
						}
					}
				}
			}

			// Do we have the file attribute for the file entry yet?
			if (null == reply) {
				// No!  Does the entry have any attachments?
				Collection<FileAttachment> atts = fileEntry.getFileAttachments();
				if (MiscUtil.hasItems(atts)) {
					// Yes!  Scan them.
			        for (FileAttachment fa : atts) {
			        	// Does this attachment have a filename?
			        	String fName = fa.getFileItem().getName();
			        	if (MiscUtil.hasString(fName)) {
			        		// Return it as the filename.
							reply = fa;
							break;
			        	}
			        }
				}
			}
		}
		
		// If we get here, reply refers to the to the file entry's
		// FileAttachment or null if the entry isn't a file entry or an
		// attachment with a named file can't be found.  Return it.
        return reply;
	}
	
	public static FileAttachment getFileEntrysFileAttachment(AllModulesInjected bs, FolderEntry fileEntry) {
		// Always use the initial form of the method.
		return getFileEntrysFileAttachment(bs, fileEntry, true);
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

	/*
	 * Establishes the connection to an HTTP URL and follows any
	 * redirect, returning the final URL resolved to.
	 */
	private static String getFinalHttpUrl(String httpUrl, String usageForLog) {
		// Do we have a URL to finalize?
		httpUrl = ((null == httpUrl) ? "" : httpUrl.trim());
		if (0 == httpUrl.length()) {
			// No!  Return null.
			return null;
		}
		
		// Ensure the URL ends with a '/'.
		if ('/' != httpUrl.charAt(httpUrl.length() - 1)) {
			httpUrl += "/";
		}
		
		HttpURLConnection	urlConnection = null;
		String				reply         = null;
		
		try {
			// Open the HTTP connection.
			urlConnection = ((HttpURLConnection) new URL(httpUrl).openConnection());
			urlConnection.setRequestMethod("GET");
			urlConnection.setInstanceFollowRedirects(false);
			urlConnection.connect();

			// Is the connection being redirected?
			int status = urlConnection.getResponseCode();
			switch (status) {
			case HttpURLConnection.HTTP_MOVED_PERM:
			case HttpURLConnection.HTTP_MOVED_TEMP:
				// Yes!  Get the redirected URL..
				reply = urlConnection.getHeaderField("Location");
				if (MiscUtil.hasString(reply)) {
					if ('/' != reply.charAt(reply.length() - 1)) {
						reply += "/";
					}
				}
				
				// ...and log the fact that it's being redirected.
				m_logger.debug("GwtServerHelper.getFinalHttpUrl( '" + httpUrl + "' ):  " + usageForLog + "...");
				m_logger.debug("...is being redirected to:  '" + reply + "'");
				
				break;
				
			case HttpURLConnection.HTTP_OK:
				// No, the connection isn't being redirected!
				// Everything is fine.  Return it.
				reply = httpUrl;
				break;
				
			default:
				// Log any other connection status as an error and
				// return null.
				m_logger.error("GwtServerHelper.getFinalHttpUrl( '" + httpUrl + "' ):  Connection status:  " + status);
				reply = null;
				break;
			}
		}
		
		catch (Exception ex) {
			// Log any exceptions as an error and return null.
			m_logger.error("GwtServerHelper.getFinalHttpUrl( '" + httpUrl + "' )", ex);
			reply = null;
		}
		
		finally {
			// If we have an HTTP connection...
			if (null != urlConnection) {
				// ...make sure it gets disconnected.
				urlConnection.disconnect();
			}
		}

		// If we get here, reply is null or refers to the final URL
		// after following all redirects, ...  Return it.
		return reply;
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
	 * 
	 * @param request
	 * @param zoneUUID
	 * @param folderId
	 * @param folderTitle
	 * @return
	 * @throws GwtTeamingException
	 */
	public static GwtFolder getFolderImpl( AllModulesInjected ami, HttpServletRequest request, String zoneUUID, String folderId, String folderTitle ) throws GwtTeamingException
	{
		BinderModule binderModule;
		Binder binder = null;
		GwtFolder folder = null;
		Binder parentBinder;
		
		try
		{
			ZoneInfo zoneInfo;
			String zoneInfoId;
			Long folderIdL;

			// Get the id of the zone we are running in.
			zoneInfo = MiscUtil.getCurrentZone();
			zoneInfoId = zoneInfo.getId();
			if ( zoneInfoId == null )
				zoneInfoId = "";

			binderModule = ami.getBinderModule();

			folderIdL = new Long( folderId );
			
			// Are we looking for a folder that was imported from another zone?
			if ( zoneUUID != null && zoneUUID.length() > 0 && !zoneInfoId.equals( zoneUUID ) )
			{
				// Yes, get the folder id for the folder in this zone.
				folderIdL = binderModule.getZoneBinderId( folderIdL, zoneUUID, EntityType.folder.name() );
			}

			// Get the binder object.
			if ( folderIdL != null )
				binder = binderModule.getBinder( folderIdL );
			
			// Initialize the data members of the GwtFolder object.
			folder = new GwtFolder();
			if ( folderIdL != null )
				folder.setFolderId( folderIdL.toString() );
			if ( binder != null )
			{
				String url;
				Description desc;

				folder.setFolderName( MiscUtil.hasString( folderTitle ) ? folderTitle : binder.getTitle() );
			
				parentBinder = binder.getParentBinder();
				if ( parentBinder != null )
					folder.setParentBinderName( parentBinder.getPathName() );

				// Create a url that can be used to view this folder.
				url = PermaLinkUtil.getPermalink( request, binder );
				folder.setViewFolderUrl( url );
				
				desc = binder.getDescription();
				if ( desc != null )
				{
					String descStr;
					
					descStr = desc.getText();
					
					// Perform any fixups needed on the entry's description
					descStr = markupStringReplacementImpl( ami, request, folderId, descStr, "view" );
					
					folder.setFolderDesc( descStr );
				}
			}
		}
		catch (Exception e)
		{
			throw getGwtTeamingException( e );
		}
		
		return folder;
	}// end getFolderImpl()
	
	
	/**
	 * Get the folder sort settings on the specified binder.
	 * 
	 */
	public static FolderSortSetting getFolderSortSetting( AllModulesInjected ami, Long binderId ) throws GwtTeamingException 
	{
		try 
		{
			Long userId;
			ProfileModule pm;
			UserProperties userFolderProperties;
			Map properties;
			FolderSortSetting folderSortSetting;
			
			folderSortSetting = new FolderSortSetting();

			userId = getCurrentUserId();
			pm = ami.getProfileModule();
			
			userFolderProperties = pm.getUserProperties( userId, binderId );
			properties = userFolderProperties.getProperties();
			
			if ( properties.containsKey( ObjectKeys.SEARCH_SORT_BY ) )
				folderSortSetting.setSortKey( (String)properties.get( ObjectKeys.SEARCH_SORT_BY ) );
			
			if ( properties.containsKey( ObjectKeys.SEARCH_SORT_DESCEND ) )
			{
				String value;
				
				value = (String) properties.get( ObjectKeys.SEARCH_SORT_DESCEND );
				folderSortSetting.setSortDescending( Boolean.valueOf( value ).booleanValue() );
			}
			
			if ( m_logger.isDebugEnabled() )
			{
				m_logger.debug( "GwtServerHelper.getFolderSortSetting( Retrieved folder sort for binder ):  Binder:  " + binderId.longValue() + ", Sort Key:  '" + folderSortSetting.getSortKey() + "', Sort Descending:  " + folderSortSetting.getSortDescending() );
			}
			
			return folderSortSetting;
		}
		catch ( Exception ex )
		{
			throw getGwtTeamingException(ex);
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
				m_logger.debug("GwtServerHelper.getFolderTypeFromDefFamily( 'Could not determine folder type' ):  " + folder.getPathName());
			}				
			break;
		
		case FILE:
			// We need to special case files because both a
			// normal file folder and a mirrored file
			// folder use 'file' for their family name.
			if (MiscUtil.hasString(view) && view.equals(VIEW_FOLDER_MIRRORED_FILE)) {
				reply = FolderType.MIRROREDFILE;
			}				
			break;
		}
		return reply;
	}
	
	/*
	 * Returns the FolderType of a folder based on its current view.
	 */
	private static FolderType getFolderTypeFromViewDef(AllModulesInjected bs, Folder folder) {
		// Does the user have a view definition selected for this
		// folder?
		Definition def = BinderHelper.getFolderDefinitionFromView(bs, folder);
		if (null == def) {
			// No!  Just use it's default view.
			def = folder.getDefaultViewDef();
		}

		// Return the FolderType from view definition.
		String defFamily = BinderHelper.getFamilyNameFromDef(def);
		return getFolderTypeFromDefFamily(folder, defFamily);
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
	 */
	public static int getGroupMembership(
			AllModulesInjected ami,
			ArrayList<GwtTeamingItem> retList,
			String groupId,
			int offset,
			int numResults,
			MembershipFilter filter ) throws GwtTeamingException
	{
		int totalNumberOfMembers = 0;
		
		if ( retList == null )
			return 0;
		
		try
		{
			Long groupIdL;
			Principal group;

			// Get the group object.
			groupIdL = Long.valueOf(groupId);
			group = ami.getProfileModule().getEntry(groupIdL);
			if ( group != null && group instanceof Group )
			{
				Iterator<Principal> itMembers;
				List<Long> membership;
				List<Principal> memberList;
				int i;
				
				// Get the members of the group.
				memberList = ((Group) group).getMembers();
				
				// Get a list of the ids of all the members of this group.
				membership = new ArrayList<Long>();
				itMembers = memberList.iterator();
				while (itMembers.hasNext())
				{
					Principal member;
					
					member = (Principal) itMembers.next();
					membership.add( member.getId() );
				}
				
				// Get all the memembers of the group.  We call ResolveIDs.getPrincipals()
				// because it handles deleted users and users the logged-in user has
				// rights to see.
				memberList = ResolveIds.getPrincipals( membership );
				
				// Sort the list of users/groups
				Collections.sort( memberList, new PrincipalComparator( true ) );
				
				totalNumberOfMembers = memberList.size();

				// For each member of the group create a GwtUser or GwtGroup object.
				for (i = offset; i < memberList.size() && retList.size() < numResults; ++i)
				{
					Principal member;
	
					member = (Principal) memberList.get( i );
					if (member instanceof Group)
					{
						if ( filter == MembershipFilter.RETRIEVE_ALL_MEMBERS || filter == MembershipFilter.RETRIEVE_GROUPS_ONLY )
						{
							Group nextGroup;
							GwtGroup gwtGroup;
							
							nextGroup = (Group) member;
							
							gwtGroup = new GwtGroup();
							gwtGroup.setInternal( nextGroup.getIdentityInfo().isInternal() );
							gwtGroup.setId( nextGroup.getId().toString() );
							gwtGroup.setName( nextGroup.getName() );
							gwtGroup.setTitle( nextGroup.getTitle() );
							
							retList.add( gwtGroup );
						}
					}
					else if (member instanceof User)
					{
						if ( filter == MembershipFilter.RETRIEVE_ALL_MEMBERS || filter == MembershipFilter.RETRIEVE_USERS_ONLY )
						{
							User user;
							GwtUser gwtUser;
							
							user = (User) member;
		
							gwtUser = new GwtUser();
							gwtUser.setInternal( user.getIdentityInfo().isInternal() );
							gwtUser.setUserId( user.getId() );
							gwtUser.setName( user.getName() );
							gwtUser.setTitle( Utils.getUserTitle( user ) );
							gwtUser.setWorkspaceTitle( user.getWSTitle() );
		
							retList.add( gwtUser );
						}
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw getGwtTeamingException( ex );
		}
		
		return totalNumberOfMembers;
	}
	
	/**
	 * Get the information about the given group's membership.  Is the membership dynamic or static.
	 * Are external users/groups allowed.
	 */
	public static GroupMembershipInfo getGroupMembershipInfo(
		AllModulesInjected ami,
		Long groupId )
	{
		GroupMembershipInfo info;
		boolean isDynamic;
		boolean externalAllowed;
		
		info = new GroupMembershipInfo();
		
		externalAllowed = isExternalMembersAllowed( ami, groupId );
		isDynamic = isGroupMembershipDynamic( ami, groupId );
		info.setMembershipInfo( isDynamic, externalAllowed );
		
		return info;
	}

	/**
	 * Returns a GwtTeamingException from a generic Exception.
	 * 
	 * Note:  The mappings between an instance of an exception and the
	 *    exception type of the GwtTeamingException returned was based
	 *    on the code originally constructing these in
	 *    GwtRpcServiceImpl.
	 * 
	 * @param ex
	 * 
	 * @return
	 */
	public static GwtTeamingException getGwtTeamingException(Exception ex) {
		// If we were given a GwtTeamingException...
		GwtTeamingException reply;
		if ((null != ex) && (ex instanceof GwtTeamingException)) {
			// ...simply return it.
			reply = ((GwtTeamingException) ex);
		}
		
		else {
			// Otherwise, construct an appropriate GwtTeamingException.
			reply = new GwtTeamingException();
			if (null != ex) {
				ExceptionType exType;
				
				if      (ex instanceof AccessControlException               ) exType = ExceptionType.ACCESS_CONTROL_EXCEPTION;
				else if (ex instanceof ExtensionDefinitionInUseException    ) exType = ExceptionType.EXTENSION_DEFINITION_IN_USE;
				else if (ex instanceof FavoritesLimitExceededException      ) exType = ExceptionType.FAVORITES_LIMIT_EXCEEDED;
				else if (ex instanceof NoBinderByTheIdException             ) exType = ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION;
				else if (ex instanceof NoFolderEntryByTheIdException        ) exType = ExceptionType.NO_FOLDER_ENTRY_BY_THE_ID_EXCEPTION;
				else if (ex instanceof NoUserByTheIdException               ) exType = ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION;
				else if (ex instanceof OperationAccessControlExceptionNoName) exType = ExceptionType.ACCESS_CONTROL_EXCEPTION;
				else if ( ex instanceof GroupExistsException )
				{
					exType = ExceptionType.GROUP_ALREADY_EXISTS;
				}
				else if ( ex instanceof RDException )
				{
					exType = ExceptionType.NET_FOLDER_ROOT_ALREADY_EXISTS;
				}
				else                                                          exType = ExceptionType.UNKNOWN;
				
				reply.setExceptionType(exType);
			}
		}

		// If debug logging is enabled...
		if (m_logger.isDebugEnabled()) {
			// ...log the exception that got us here.
			if (null != ex)
			     m_logger.debug("GwtServerHelper.getGwtTeamingException( SOURCE EXCEPTION ):  ", ex   );
			else m_logger.debug("GwtServerHelper.getGwtTeamingException( GWT EXCEPTION ):  ",    reply);
		}

		// If we get here, reply refers to the GwtTeamingException that
		// was requested.  Return it.
		return reply;
	}
	
	public static GwtTeamingException getGwtTeamingException() {
		// Always use the initial form of the method.
		return getGwtTeamingException(null);
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
	 * Get the inherited landing page properties (background color, background image, etc) for
	 * the given binder.
	 * @throws GwtTeamingException 
	 */
	public static LandingPageProperties getInheritedLandingPageProperties( AllModulesInjected ami, String binderId, HttpServletRequest request ) throws GwtTeamingException
	{
		LandingPageProperties lpProperties = null;
		
		lpProperties = new LandingPageProperties();
		lpProperties.setInheritProperties( true );
		
		try
		{
			Binder binder;
			Binder parentBinder;
			
			binder = ami.getBinderModule().getBinder( Long.parseLong( binderId ) );
			
			// Get the binder's parent.
			parentBinder = binder.getParentBinder();
			if ( parentBinder != null )
				binder = parentBinder;
			
			lpProperties = getLandingPageProperties( ami, String.valueOf( binder.getId() ), request );
		}
		catch (Exception ex)
		{
			throw getGwtTeamingException( ex );
		}
		
		return lpProperties;
	}
	
	/*
	 * Parses a JSON data string and if valid, returns a JSONObject.
	 * Otherwise, returns null.
	 */
	private static JSONObject getJSOFromS(String jsonData) {
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
	 */
	public static ConfigData getLandingPageData( HttpServletRequest request, AllModulesInjected allModules, String binderId ) throws GwtTeamingException
	{
		ConfigData configData;
		
		configData = new ConfigData();
		configData.setBinderId( binderId );

		try
		{
			Binder binder;
			CustomAttribute customAttr;
			String style;
			
			binder = allModules.getBinderModule().getBinder( Long.parseLong( binderId ) );
			
			// The landing page configuration data is stored as a custom attribute with the name "mashup"
			customAttr = binder.getCustomAttribute( "mashup" );
    		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.STRING )
    		{
    			String configStr;

    			configStr = (String) customAttr.getValue();
    			configData.setConfigStr( configStr );
    		}
    		
			// Get the value of the "hide the masthead" setting.
    		customAttr = binder.getCustomAttribute( "mashup" + DefinitionModule.MASHUP_HIDE_MASTHEAD );
    		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.BOOLEAN )
    			configData.setHideMasthead( ((Boolean) customAttr.getValue()).booleanValue() );
			
			// Get the value of the "hide the navigation panel" setting.
    		customAttr = binder.getCustomAttribute( "mashup" + DefinitionModule.MASHUP_HIDE_SIDEBAR );
    		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.BOOLEAN )
    			configData.setHideNavPanel( ((Boolean) customAttr.getValue()).booleanValue() );
			
			// Get the value of the "hide the footer" setting.
    		customAttr = binder.getCustomAttribute( "mashup" + DefinitionModule.MASHUP_HIDE_FOOTER );
    		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.BOOLEAN )
    			configData.setHideFooter( ((Boolean) customAttr.getValue()).booleanValue() );
    		
    		// Get the value of the "landing page style" setting.
    		style = "mashup_dark.css";
    		customAttr = binder.getCustomAttribute( "mashup" + DefinitionModule.MASHUP_STYLE );
    		if ( customAttr != null && customAttr.getValueType() == CustomAttribute.STRING )
    		{
    			style = (String) customAttr.getValue();
    			if ( style == null || style.length() == 0 )
    				style = "mashup_dark.css";
    		}
    		configData.setLandingPageStyle( style );
			
			// Get the other settings that are stored in the "mashup__properties" custom attribute
   			getLandingPageProperties( allModules, binderId, configData, request );
		}
		catch (Exception ex)
		{
			throw getGwtTeamingException( ex );
		}
		
		return configData;
	}
	
	/**
	 * Get the landing page properties (background color, background image, etc) for
	 * the given binder.
	 * @throws GwtTeamingException 
	 */
	public static LandingPageProperties getLandingPageProperties( AllModulesInjected ami, String binderId, HttpServletRequest request ) throws GwtTeamingException
	{
		LandingPageProperties lpProperties = null;
		
		lpProperties = new LandingPageProperties();
		lpProperties.setInheritProperties( true );
		
		try
		{
			Binder binder;
			Binder sourceBinder;
			Document doc;
			
			binder = ami.getBinderModule().getBinder( Long.parseLong( binderId ) );
			
			// Landing page properties can be inherited.  Get the binder that holds the landing
			// page properties.
			sourceBinder = binder.getLandingPagePropertiesSourceBinder();
			
			// Does the user have rights to the binder where the landing page properties are coming from?
			if ( !ami.getBinderModule().testAccess( sourceBinder, BinderOperation.readEntries ) )
			{
				// No, don't use inherited landing page properties.
				sourceBinder = binder;
			}
			
			// Get the landing page properties from the binder we inherit from.
			doc = sourceBinder.getLandingPageProperties();
			if ( doc != null )
			{
				Element bgElement;
				Element pgLayoutElement;
				Element headerElement;
				Element contentElement;
				Element borderElement;
				
				// Did we inherit the properties from another landing page.
				if ( sourceBinder == binder )
				{
					// No
					lpProperties.setInheritProperties( false );
				}
				
				// Get the <background ...> element.
				bgElement = (Element) doc.selectSingleNode( "//landingPageData/background" );
				if ( bgElement != null )
				{
					String bgColor;
					String bgImgName;
					
					bgColor = bgElement.attributeValue( "color" );
					if ( bgColor != null )
						lpProperties.setBackgroundColor( bgColor );
					
					bgImgName = bgElement.attributeValue( "imgName");
					if ( bgImgName != null && bgImgName.length() > 0 )
					{
						String fileUrl;
						String webPath;
						
						webPath = WebUrlUtil.getServletRootURL( request );
						fileUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, sourceBinder, bgImgName );
						lpProperties.setBackgroundImgUrl( fileUrl );
						
						// Get the background image repeat value.
						{
							String repeat;
							
							repeat = bgElement.attributeValue( "repeat" );
							if ( repeat != null )
								lpProperties.setBackgroundRepeat( repeat );
						}
					}
				}
				
				// Get the <pageLayout hideMenu="true | false" /> element.
				pgLayoutElement = (Element) doc.selectSingleNode( "//landingPageData/pageLayout" );
				if ( pgLayoutElement != null )
				{
					String hideMenu;
					
					hideMenu = pgLayoutElement.attributeValue( "hideMenu" );
					if ( hideMenu != null )
					{
						boolean value;
						
						value = Boolean.parseBoolean( hideMenu );
						lpProperties.setHideMenu( value );
					}
				}

				// Get the <header bgColor="" textColor="" /> element.
				headerElement = (Element) doc.selectSingleNode( "//landingPageData/header" );
				if ( headerElement != null )
				{
					String bgColor;
					String textColor;
					
					bgColor = headerElement.attributeValue( "bgColor" );
					if ( bgColor != null )
						lpProperties.setHeaderBgColor( bgColor );
					
					textColor = headerElement.attributeValue( "textColor" );
					if ( textColor != null )
						lpProperties.setHeaderTextColor( textColor );
				}
				
				// Get the <content textColor="" /> element.
				contentElement = (Element) doc.selectSingleNode( "//landingPageData/content" );
				if ( contentElement != null )
				{
					String textColor;
					
					textColor = contentElement.attributeValue( "textColor" );
					if ( textColor != null )
						lpProperties.setContentTextColor( textColor );
				}
				
				// Get the <border color="" width="" /> element
				borderElement = (Element) doc.selectSingleNode( "//landingPageData/border" );
				if ( borderElement != null )
				{
					String borderColor;
					String width;
					
					borderColor = borderElement.attributeValue( "color" );
					if ( borderColor != null )
						lpProperties.setBorderColor( borderColor );
					
					width = borderElement.attributeValue( "width" );
					if ( width != null )
						lpProperties.setBorderWidth( width );
				}
			}
		}
		catch (Exception ex)
		{
			throw getGwtTeamingException( ex );
		}
		
		return lpProperties;
	}
	
	/**
	 * Get the landing page properties (background color, background image, etc) for
	 * the given binder.
	 * @throws GwtTeamingException 
	 */
	public static void getLandingPageProperties( AllModulesInjected ami, String binderId, ConfigData lpConfigData, HttpServletRequest request ) throws GwtTeamingException
	{
		LandingPageProperties lpProperties;
		
		// Get the landing page properties for the given binder.
		lpProperties = getLandingPageProperties( ami, binderId, request );
		if ( lpProperties != null )
		{
			lpConfigData.initLandingPageProperties( lpProperties );
		}
	}
	
	/**
	 * Return a list of child binders for the given binder.
	 */
	public static ArrayList<TreeInfo> getListOfChildBinders( HttpServletRequest request, AllModulesInjected ami, String binderId )
	{
		Binder binder;
		ArrayList<TreeInfo> listOfChildBinders;
    	Map<String, Counter> unseenCounts;

		// Get the count of unseen items in the given binder and sub binders
		{
			@SuppressWarnings("unused")
			HashMap options;
			List binderIds;
			Date creationDate;
			String startDate;
			String now;
			Criteria crit;
			Map results;
	    	List<Map> entries;
			SeenMap seen;
			
	    	unseenCounts = new HashMap();
	    	
			options = new HashMap();
			binderIds = new ArrayList();
			binderIds.add( binderId );
		    
			// Get entries created within last 30 days
			creationDate = new Date();
			creationDate.setTime( creationDate.getTime() - ObjectKeys.SEEN_TIMEOUT_DAYS*24*60*60*1000 );
			startDate = DateTools.dateToString( creationDate, DateTools.Resolution.SECOND );
			now = DateTools.dateToString( new Date(), DateTools.Resolution.SECOND );
			crit = SearchUtils.newEntriesDescendants( binderIds );
			crit.add( org.kablink.util.search.Restrictions.between( Constants.MODIFICATION_DATE_FIELD, startDate, now ) );
			results = ami.getBinderModule().executeSearchQuery( crit, Constants.SEARCH_MODE_NORMAL, 0, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS );
	    	entries = (List) results.get( ObjectKeys.SEARCH_ENTRIES );

			// Get the count of unseen entries
			seen = ami.getProfileModule().getUserSeenMap( null );
	    	for (Map entry : entries)
	    	{
	    		SearchFieldResult entryAncestors;
				String entryIdString;
				Iterator itAncestors;

	    		entryAncestors = (SearchFieldResult) entry.get( Constants.ENTRY_ANCESTRY );
				if ( entryAncestors == null )
					continue;
				
				entryIdString = (String) entry.get( Constants.DOCID_FIELD );
				if ( entryIdString == null || ( seen.checkIfSeen( entry ) ) )
					continue;
				
				// Count up the unseen counts for all ancestor binders
				itAncestors = entryAncestors.getValueSet().iterator();
				while ( itAncestors.hasNext() )
				{
					String binderIdString;
					Counter cnt;

					binderIdString = (String)itAncestors.next();
					if ( binderIdString.equals("") )
						continue;
					
					cnt = unseenCounts.get( binderIdString );
					if ( cnt == null )
					{
						cnt = new WorkspaceTreeHelper.Counter();
						unseenCounts.put( binderIdString, cnt );
					}
					cnt.increment();
				}
	    	}
		}

		listOfChildBinders = new ArrayList<TreeInfo>();
		binder = GwtUIHelper.getBinderSafely( ami.getBinderModule(), binderId );
		if ( binder != null )
		{
			ArrayList<Long> expandedBindersList;
			TreeInfo treeInfo;
			List<Map> children;

			// This is needed by buildTreeInfoFromBinder()
			expandedBindersList = new ArrayList<Long>();
			
			// Get all of the child binders.
			{
				Map options;
				Map searchResults;

				options = new HashMap();
				options.put( ObjectKeys.SEARCH_SORT_BY, org.kablink.util.search.Constants.SORT_TITLE_FIELD );
				options.put( ObjectKeys.SEARCH_SORT_DESCEND, new Boolean( false ) );
				options.put( ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.MAX_BINDER_ENTRIES_RESULTS );
				searchResults = ami.getBinderModule().getBinders( binder, options );
				children = (List)searchResults.get( ObjectKeys.SEARCH_ENTRIES );
				
			}
			
			if ( children != null )
			{
				for (Map child : children)
				{
					String childBinderId;
					Binder childBinder;
					
					// Get the next child binder
					childBinderId = (String) child.get( Constants.DOCID_FIELD );
					childBinder = GwtUIHelper.getBinderSafely( ami.getBinderModule(), childBinderId );
					
					if ( childBinder != null )
					{
						treeInfo = buildTreeInfoFromBinder( request, ami, childBinder, expandedBindersList, false, 1 );
	
						// Set the number of unseen entries for this binder
						{
							Counter counter;
							
							counter = unseenCounts.get( childBinderId );
							if ( counter != null )
								treeInfo.getBinderInfo().setNumUnread( counter.getCount() );
						}
						
						listOfChildBinders.add( treeInfo );
					}
				}
			}
		}
		
		// Sort the list of child binders.
		if ( listOfChildBinders.isEmpty() == false )
		{
			Collections.sort( listOfChildBinders, new TreeInfoComparator( true ) );
		}

		return listOfChildBinders;
	}
	
	
	/**
	 * Return login information such as self registration and auto complete.
	 */
	public static GwtLoginInfo getLoginInfo( HttpServletRequest request, AllModulesInjected ami )
	{
		GwtLoginInfo loginInfo;
		GwtSelfRegistrationInfo selfRegInfo;
		boolean allowAutoComplete;
		
		loginInfo = new GwtLoginInfo();
		
		// Get self-registration info.
		selfRegInfo = getSelfRegistrationInfo( request, ami );
		loginInfo.setSelfRegistrationInfo( selfRegInfo );
		
		// Read the value of the enable.login.autocomplete key from ssf.properties
		allowAutoComplete = SPropsUtil.getBoolean( "enable.login.autocomplete", false );
		loginInfo.setAllowAutoComplete( allowAutoComplete );
		
		// Are we running the Enterprise version of Vibe?
		if ( ReleaseInfo.isLicenseRequiredEdition() )
		{
			// Yes
			// Does the license allow external users?
			if ( LicenseChecker.isAuthorizedByLicense( "com.novell.teaming.ExtUsers" ) )
			{
				ZoneConfig zoneConfig;
				ZoneModule zoneModule;
				boolean allowOpenIdAuth;
				
				// Yes
				zoneModule = ami.getZoneModule();
				zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
				allowOpenIdAuth = zoneConfig.isExternalUserEnabled() && zoneConfig.getOpenIDConfig().isAuthenticationEnabled();
				loginInfo.setAllowOpenIdAuthentication( allowOpenIdAuth ); 
				
				// Is openid authentication enabled?
				if ( allowOpenIdAuth )
				{
					ArrayList<GwtOpenIDAuthenticationProvider> listOfProviders;
					
					// Yes
					// Get a list of the openid authentication providers supported by Vibe
					listOfProviders = getOpenIDAuthenticationProviders( ami );
					loginInfo.setListOfOpenIDAuthProviders( listOfProviders );
				}
			}
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

			// ...if the zone configuration has an auto update URL...
			boolean desktopAppEnabled        = false;
			boolean showDesktopAppDownloader = false;
			ZoneConfig zc = bs.getZoneModule().getZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			String baseUrl = zc.getFsaAutoUpdateUrl();
			baseUrl = ((null == baseUrl) ? "" : baseUrl.trim());
			if (0 < baseUrl.length()) {
				// ...get what we know about desktop application
				// ...deployment...
				GwtFileSyncAppConfiguration fsaConfig = getFileSyncAppConfiguration(bs);
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
				m_logger.debug("GwtServerHelper.getMainPageInfo():  The file synchronization application auto update URL is not available.");
			}
			
			// ...and use this all to construct a
			// ...MainPageInfoRpcResponseData to return.
			return
				new MainPageInfoRpcResponseData(
					bi,
					userAvatarUrl,
					desktopAppEnabled,
					showDesktopAppDownloader);
		}
		
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
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
				m_logger.error("GwtServerHelper.getManageUsersInformation():  The workspace type of the profile root binder was incorrect.  Found:  " + bi.getWorkspaceType().name() + ", Expected:  " + WorkspaceType.PROFILE_ROOT.name());
			}
			bi.setWorkspaceType(WorkspaceType.PROFILE_ROOT_MANAGEMENT);
			ManageUsersInfoRpcResponseData reply =
				new ManageUsersInfoRpcResponseData(
					bi,
					NLT.get("administration.manage.userAccounts"));

			// If we get here, reply refers to the
			// ManageUsersInfoRpcResponseData object
			// containing the information about managing user.  Return
			// it.
			return reply;
		}
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
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

			// Construct the ManageUsersStateRpcResponseData
			// object to return.
			ManageUsersStateRpcResponseData reply =
				new ManageUsersStateRpcResponseData(
					((null == showInternal) || showInternal),
					((null == showExternal) || showExternal),
					((null == showDisabled) || showDisabled),
					((null == showEnabled)  || showEnabled));

			// If we get here, reply refers to the
			// ManageUsersStateRpcResponseData object
			// containing the information about managing user.  Return
			// it.
			return reply;
		}
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
		}		
	}

	/**
	 * Return a GwtMobileAppsConfiguration object that holds the mobile apps configuration data
	 * 
	 * @return
	 */
	public static GwtZoneMobileAppsConfig getMobileAppsConfiguration( AllModulesInjected allModules )
	{
		GwtZoneMobileAppsConfig gwtMobileAppsConfig;
		MobileAppsConfig mobileAppsConfig;
		ZoneConfig zoneConfig;
		ZoneModule zoneModule;
		
		zoneModule = allModules.getZoneModule();
		zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
		mobileAppsConfig = zoneConfig.getMobileAppsConfig();
		
		gwtMobileAppsConfig = new GwtZoneMobileAppsConfig();
		
		// Get the whether mobile apps are enabled.
		gwtMobileAppsConfig.setMobileAppsEnabled( mobileAppsConfig.getMobileAppsEnabled() );
		
		// Get the setting that determines whether the mobile apps can remember the password
		gwtMobileAppsConfig.setAllowCachePwd( mobileAppsConfig.getMobileAppsAllowCachePwd() );
		
		// Get the setting that determines if mobile apps can cache content.
		gwtMobileAppsConfig.setAllowCacheContent( mobileAppsConfig.getMobileAppsAllowCacheContent() );

		// Get the setting that determines if the mobile apps can play with other apps.
		gwtMobileAppsConfig.setAllowPlayWithOtherApps( mobileAppsConfig.getMobileAppsAllowPlayWithOtherApps() );

		// Get the Mobile Apps sync interval.
		gwtMobileAppsConfig.setSyncInterval( mobileAppsConfig.getMobileAppsSyncInterval() );
		
		return gwtMobileAppsConfig;
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
			reply = getMyFilesFolderId(bs, user, true);	// true -> Create it if it doesn't exist.
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
	 * Returns a List<Long> of the current user's My Files folder IDs.
	 * 
	 * @param bs
	 * @param user
	 */
	public static List<Long> getMyFilesFolderIds(AllModulesInjected bs, User user) {
		return SearchUtils.getMyFilesFolderIds(bs, user);
	}
	
	public static List<Long> getMyFilesFolderIds(AllModulesInjected bs) {
		// Always use the initial form of the method.
		return getMyFilesFolderIds(bs, getCurrentUser());
	}
	
	/**
	 * If the user has a folder that's recognized as their My Files
	 * folder, it's ID is returned.  Otherwise, null is returned.
	 * 
	 * @param bs
	 * @param user
	 * 
	 * @return
	 */
	public static Long getMyFilesFolderId(AllModulesInjected bs, User user, boolean createIfNeccessary) {
		Long reply;
		List<Long> mfFolderIds = getMyFilesFolderIds(bs, user);
		if ((null != mfFolderIds) && (!(mfFolderIds.isEmpty()))) {
			reply = mfFolderIds.get(0);
		}
		else if (createIfNeccessary) {
			reply = createMyFilesFolder(bs, user);
		}
		else {
			reply = null;
		}
		return reply;
	}
	
	public static Long getMyFilesFolderId(AllModulesInjected bs, boolean createIfNeccessary) {
		// Always use the initial form of the method.
		return getMyFilesFolderId(bs, getCurrentUser(), createIfNeccessary);
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
	 * Return the number of members in the given group
	 */
	public static int getNumberOfMembers( AllModulesInjected ami, Long groupIdL ) throws GwtTeamingException
	{
		int count = 0;
		
		try
		{
			Principal group;

			// Get the group object.
			group = ami.getProfileModule().getEntry( groupIdL );
			if ( group != null && group instanceof Group )
			{
				List<Principal> memberList;
				
				// Get the members of the group.
				memberList = ((Group) group).getMembers();
				if ( memberList != null )
					count = memberList.size();
			}
		}
		catch (Exception ex)
		{
			throw getGwtTeamingException( ex );
		}
		
		m_logger.debug( "number of users in the group: " + String.valueOf( count ) );
		
		return count;
	}

	/**
	 * Return a list of OpenID Authentication providers supported by Vibe
	 */
	public static ArrayList<GwtOpenIDAuthenticationProvider> getOpenIDAuthenticationProviders( AllModulesInjected ami )
	{
		ArrayList<GwtOpenIDAuthenticationProvider> listOfProviders;
		List<OpenIDProvider> providers; 
		
		listOfProviders = new ArrayList<GwtOpenIDAuthenticationProvider>();

		// Get a list of the OpenID providers
		providers = ami.getAdminModule().getOpenIDProviders();
		if ( providers != null && providers.size() > 0 )
		{
			Iterator<OpenIDProvider> iterator;
			
			iterator = providers.iterator();
			while ( iterator.hasNext() )
			{
				OpenIDProvider provider;
				GwtOpenIDAuthenticationProvider gwtProvider;
				
				provider = iterator.next();
				
				gwtProvider = new GwtOpenIDAuthenticationProvider();
				gwtProvider.setName( provider.getName() );
				gwtProvider.setUrl( provider.getUrl() );
				
				listOfProviders.add( gwtProvider );
			}
		}
		
		return listOfProviders;
	}
	
	/**
	 * Returns a GwtPresenceInfo object for a User.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static GwtPresenceInfo getPresenceInfo(User user)
	{
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
		case PresenceInfo.STATUS_AVAILABLE:  dudeGif = "pics/sym_s_green_dude_14.png";  break;
		case PresenceInfo.STATUS_AWAY:       dudeGif = "pics/sym_s_yellow_dude_14.png"; break;
		case PresenceInfo.STATUS_IDLE:       dudeGif = "pics/sym_s_yellow_dude_14.png"; break;
		case PresenceInfo.STATUS_BUSY:       dudeGif = "pics/sym_s_red_dude_14.png";    break;
		case PresenceInfo.STATUS_OFFLINE:    dudeGif = "pics/sym_s_gray_dude_14.png";   break;
		default:                             dudeGif = "pics/sym_s_white_dude_14.png";  break;
		}
		return dudeGif;
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
			throw getGwtTeamingException( ex );
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
	
	/*
	 * Extracts a non-null string from a JSONObject.
	 */
	private static String getSFromJSO(JSONObject jso, String key) {
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

	/*
	 * Parses and extracts a non-null string from a JSON data string. 
	 */
	private static String getSFromJSO(String jsonData, String key) {
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
	public static List<GroupInfo> getGroups(HttpServletRequest request, AllModulesInjected bs, Long userId) {
		// Allocate an ArrayList<GroupInfo> to hold the groups.
		ArrayList<GroupInfo> reply = new ArrayList<GroupInfo>();
		
		// Scan the groups the current user is a member of...
		ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		List users = ResolveIds.getPrincipals(userIds, true);
		if (!users.isEmpty()) {
			Principal p = (Principal)users.get(0);
		    Set<Long> groupIds = profileDao.getApplicationLevelPrincipalIds(p);
		    groupIds.remove(userId);
			List<Group> groups = profileDao.loadGroups(groupIds, RequestContextHolder.getRequestContext().getZoneId());
			for (Group myGroup : groups) {
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
		
		// If we get here, reply refers to the ArrayList<GroupInfo> of
		// the groups the current user is a member of.  Return it.
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
		// Allocate an ArrayList<GroupInfo> to hold the groups.
		List<Long> reply = new ArrayList<Long>();
		
		List<GroupInfo> groups = getGroups(request, bs, userId);
		for (GroupInfo group:  groups) {
			reply.add(group.getId());
		}

		// If we get here, reply refers to the List<Long> of the groups
		// the user is a member of.  Return it.
		return reply;
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
			// Are we dealing with the guest user?
			User user = getCurrentUser();
			if (!(ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()))) {
				// No!  Get the user's display style preference.
				String displayStyle = user.getDisplayStyle();
				personalPrefs.setDisplayStyle(displayStyle);
				
				// Is the tutorial panel open?
				String tutorialPanelState = getTutorialPanelState(bs, request);
				if ((null != tutorialPanelState) && tutorialPanelState.equalsIgnoreCase("1"))
				     personalPrefs.setShowTutorialPanel(false);
				else personalPrefs.setShowTutorialPanel(true );
				
				// Get the number of entries per page that should be
				// displayed when a folder is selected.
				Integer numEntriesPerPage = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
				UserProperties userProperties = bs.getProfileModule().getUserProperties(user.getId());
				String value = ((String) userProperties.getProperty(ObjectKeys.PAGE_ENTRIES_PER_PAGE));
				if (MiscUtil.hasString(value)) {
					try {
						numEntriesPerPage = Integer.parseInt(value);
					}
					catch (NumberFormatException nfe) {
						m_logger.warn("GwtServerHelper.getPersonalPreferences():  num entries per page is not an integer.");
					}
				}
				personalPrefs.setNumEntriesPerPage(numEntriesPerPage);
				
				// Set the flag that indicates whether 'editor
				// overrides; are supported.
				personalPrefs.setEditorOverrideSupported(SsfsUtil.supportAttachmentEdit());
			}
			
			else {
				m_logger.warn("GwtServerHelper.getPersonalPreferences():  User is guest.");
			}
		}
		
		catch (AccessControlException acEx) {
			// Nothing to do.
			m_logger.warn("GwtServerHelper.getPersonalPreferences():  AccessControlException");
		}
		
		catch (Exception e) {
			// Nothing to do.
			m_logger.warn("GwtServerHelper.getPersonalPreferences():  Unknown exception");
		}
		
		return personalPrefs;
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
		
		// Get the user's primary email address.
		address = user.getEmailAddress( Principal.PRIMARY_EMAIL );
		subscriptionData.setPrimaryEmailAddress( address );
		
		// Get the user's mobile email address.
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
				
				// Get the subscription values for which address should be sent an email.
				values = (String[]) subMap.get( Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION );
				subscriptionData.setSendEmailTo( values );
				
				// Get the subscription values for which address should be sent an email without an attachment.
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
	 * @param emValue
	 * 
	 * @return
	 */
	public static String[] getStringArrayFromEntryMapValue(Object emValue) {
		ArrayList<String>	strings;
		if (emValue instanceof SearchFieldResult) {
			strings = ((SearchFieldResult) emValue).getValueArray();
		}
		else {
			strings = new ArrayList<String>();
			String string = getStringFromEntryMapValue(emValue, DateFormat.MEDIUM, DateFormat.SHORT);
			if (MiscUtil.hasString(string)) {
				strings.add(string);
			}
		}
		return strings.toArray(new String[0]);
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
			
			else {
				// No, it isn't a date either!  Let the object convert
				// itself to a string and return that.
				reply = emValue.toString();
			}
		}
		
		// If we get here, reply refers to an empty string or the
		// appropriate string value for the key from the entry map.
		// Return it.
		return reply;
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
	 * 
	 * @return
	 */
	public static List<TaskFolderInfo> getTaskFolderInfoListFromEntryMap(AllModulesInjected bs, HttpServletRequest request, Map m, String key) {
		// Is there value for the key?
		List<TaskFolderInfo> reply = new ArrayList<TaskFolderInfo>();
		Object o = m.get(key);
		if (null != o) {
			// Yes!  Is the value is a String?
			if (o instanceof String) {
				// Yes!  Use it as the folder ID to create a
				// TaskFolderInfo to add to the List<TaskFolderInfo>. 
				addTFIFromStringToList(bs, request, ((String) o), reply);
			}

			// No, the value isn't a String!  Is it a String[]?
			else if (o instanceof String[]) {
				// Yes!  Scan them and use each as the folder ID to
				// create a TaskFolderInfo to add to the
				// List<TaskFolderInfo>. 
				String[] strLs = ((String[]) o);
				int c = strLs.length;
				for (int i = 0; i < c; i += 1) {
					addTFIFromStringToList(bs, request, strLs[i], reply);
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
					addTFIFromStringToList(bs, request, strL, reply);
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
	
    /**
     * 
	 * @param ri
	 * 
	 * @return
     */
    public static String getTutorialPanelState(AllModulesInjected bs, HttpServletRequest request) {
    	ProfileModule	profileModule = bs.getProfileModule();
    	UserProperties	userProperties = profileModule.getUserProperties( null );
    	String			tutorialPanelState = (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE );

		// Do we have a tutorial panel state?
		if (!(MiscUtil.hasString(tutorialPanelState))) {
			// No, default to expanded.
			tutorialPanelState = "2";
		}
		
    	return tutorialPanelState;
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
		List<Map> myTeams = bs.getBinderModule().getTeamMemberships( userId );
		for (Iterator<Map> myTeamsIT = myTeams.iterator(); myTeamsIT.hasNext(); ) {
			// ...adding a TeamInfo for each to the reply list.
			Map myTeam = myTeamsIT.next();
			TeamInfo ti = new TeamInfo();
			ti.setBinderId(   ((String) myTeam.get(      "_docId"        )));
			ti.setEntityPath( ((String) myTeam.get(      "_entityPath"   )));
			ti.setPermalink(  PermaLinkUtil.getPermalink( request, myTeam ));
			ti.setTitle(      ((String) myTeam.get(      "title"         )));
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
			m_logger.debug("GwtServerHelper.getTopRanked( 'Could not access the current HttpSession' )");
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
			m_logger.debug("GwtServerHelper.getTopRanked( 'Could not access any cached items' )");
			return triList;
		}
		
		// Scan the top ranked people...
		TopRankedInfo tri;
		for (int i = 0; i < people; i += 1) {
			// ...extract the Principal and reference count from this
			// ...person... 
			Map person = peopleList.get(i);
			Principal user     = ((Principal) person.get(WebKeys.USER_PRINCIPAL));
			Integer   refCount = ((Integer)   person.get(WebKeys.SEARCH_RESULTS_COUNT));
			String    css      = ((String)    person.get(WebKeys.SEARCH_RESULTS_RATING_CSS));
			
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
			Map place = placesList.get(i);
			Binder  binder   = ((Binder)  place.get(WebKeys.BINDER));
			Integer refCount = ((Integer) place.get(WebKeys.SEARCH_RESULTS_COUNT));
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
	 */
	public static UserAccessConfig getUserAccessConfig(
		AllModulesInjected ami,
		HttpServletRequest request )
	{
		UserAccessConfig config;
		AuthenticationConfig authConfig;
		
		authConfig = ami.getAuthenticationModule().getAuthenticationConfig();

		config = new UserAccessConfig();
		
		// Check for guest access
		config.setAllowGuestAccess( authConfig.isAllowAnonymousAccess() );
		config.setGuestReadOnly( authConfig.isAnonymousReadOnly() );
		
		if ( ReleaseInfo.isLicenseRequiredEdition() )
		{
			// Yes
			// Does the license allow external users?
			if ( LicenseChecker.isAuthorizedByLicense( "com.novell.teaming.ExtUsers" ) )
			{
				ZoneConfig zoneConfig;
				ZoneModule zoneModule;
				OpenIDConfig openIdConfig;
				boolean allowOpenIdAuth;
				
				// Yes
				zoneModule = ami.getZoneModule();
				zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
				openIdConfig = zoneConfig.getOpenIDConfig();
				allowOpenIdAuth = zoneConfig.isExternalUserEnabled() && openIdConfig.isAuthenticationEnabled();
				config.setAllowExternalUsers( allowOpenIdAuth ); 
				config.setAllowExternalUsersSelfReg( openIdConfig.isSelfProvisioningEnabled() );
			}
		}
		else
		{
			// Self registration of internal users is only found in the Kablink version.
			// Is self registration allowed?
			config.setAllowSelfReg( authConfig.isAllowSelfRegistration() );
		}
		
		return config;
	}
	
	/**
	 * Returns the URL for a user's avatar.
	 * 
	 * @param bs
	 * @param request
	 * @param user
	 * 
	 * @return
	 */
	public static String getUserAvatarUrl(AllModulesInjected bs, HttpServletRequest request, Principal user) {
		// Can we access any avatars for the user?
		String reply = null;
		ProfileAttribute pa;
		try                  {pa = GwtProfileHelper.getProfileAvatars(request, bs, user);}
		catch (Exception ex) {pa = null;                                                 }
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

	/**
	 * Return a GwtUserFileSyncAppConfig object that holds the file sync app configuration data
	 * 
	 * @return
	 */
	public static GwtUserFileSyncAppConfig getUserFileSyncAppConfig(
		AllModulesInjected ami,
		Long userId )
	{
		GwtUserFileSyncAppConfig config;
		UserProperties userProperties;
		
		config = new GwtUserFileSyncAppConfig();
		
		userProperties = ami.getProfileModule().getUserProperties( userId );
		if ( userProperties != null )
		{
			Object value;
			
			value = userProperties.getProperty( ObjectKeys.USER_PROPERTY_DESKTOP_APP_ACCESS_FILR );
			if ( value != null && value instanceof String )
			{
				config.setIsFileSyncAppEnabled( Boolean.valueOf( (String) value ) );
			}

			value = userProperties.getProperty( ObjectKeys.USER_PROPERTY_DESKTOP_APP_CACHE_PWD );
			if ( value != null && value instanceof String )
			{
				config.setAllowCachePwd( Boolean.valueOf( (String) value ) );
			}
		}
		
		return config;
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
		List<Long> userIds = new ArrayList<Long>();
		userIds.add(userId);
		Set<User> userSet = bs.getProfileModule().getUsers(userIds);
		User[] users = userSet.toArray(new User[0]);
		
		User reply;
		if ((null != users) && (1 == users.length))
		     reply = users[0];
		else reply = null;
		return reply;
	}

	/**
	 * Return a GwtUserMobileAppsConfig object that holds the mobile apps configuration data
	 * 
	 * @return
	 */
	public static GwtUserMobileAppsConfig getUserMobileAppsConfig(
		AllModulesInjected ami,
		Long userId )
	{
		GwtUserMobileAppsConfig gwtUserMobileAppsConfig;
		UserProperties userProperties;
		
		gwtUserMobileAppsConfig = new GwtUserMobileAppsConfig();
		
		userProperties = ami.getProfileModule().getUserProperties( userId );
		if ( userProperties != null )
		{
			Object value;
			
			value = userProperties.getProperty( ObjectKeys.USER_PROPERTY_MOBILE_APPS_ACCESS_FILR );
			if ( value != null && value instanceof String )
			{
				gwtUserMobileAppsConfig.setMobileAppsEnabled( Boolean.valueOf( (String) value ) );
			}

			value = userProperties.getProperty( ObjectKeys.USER_PROPERTY_MOBILE_APPS_CACHE_PWD );
			if ( value != null && value instanceof String )
			{
				gwtUserMobileAppsConfig.setAllowCachePwd( Boolean.valueOf( (String) value ) );
			}
			
			value = userProperties.getProperty( ObjectKeys.USER_PROPERTY_MOBILE_APPS_CACHE_CONTENT );
			if ( value != null && value instanceof String )
			{
				gwtUserMobileAppsConfig.setAllowCacheContent( Boolean.valueOf( (String) value ) );
			}
			
			value = userProperties.getProperty( ObjectKeys.USER_PROPERTY_MOBILE_APPS_PLAY_WITH_OTHER_APPS );
			if ( value != null && value instanceof String )
			{
				gwtUserMobileAppsConfig.setAllowPlayWithOtherApps( Boolean.valueOf( (String) value ) );
			}
		}
		
		return gwtUserMobileAppsConfig;
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
			List<WorkAreaFunctionMembership>	wafmList   = am.getWorkAreaFunctionMemberships(zoneConfig);
			if (MiscUtil.hasItems(wafmList)) {
				// Yes!  Scan them.
				for (WorkAreaFunctionMembership wafm:  wafmList) {
					// Is this an internal function that has members?
					String fiId = am.getFunction(wafm.getFunctionId()).getInternalId();
					if (MiscUtil.hasString(fiId) && MiscUtil.hasItems(wafm.getMemberIds())) {
						// Yes!  Check it for being one of the sharing
						// functions.
						if      (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_EXTERNAL_SHARING_INTERNALID)) reply.setExternalEnabled(  true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_FORWARD_SHARING_INTERNALID))  reply.setForwardingEnabled(true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_INTERNAL_SHARING_INTERNALID)) reply.setInternalEnabled(  true);
						else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ENABLE_PUBLIC_SHARING_INTERNALID))   reply.setPublicEnabled(    true);

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
				Long					wsId  = null;
				List<Principal>			pList = ResolveIds.getPrincipals(userIds);
				PerUserShareRightsInfo	psri  = new PerUserShareRightsInfo();
				if (MiscUtil.hasItems(pList)) {
					Principal p = pList.get(0);
					if (p instanceof UserPrincipal) {
						// Yes!  Does that user have a workspace ID?
						User user = ((User) p);
						wsId = user.getWorkspaceId();
						if (null != wsId) {
							// Yes!  Are there any work area function
							// memberships configured on that
							// workspace?
							wafmList = am.getWorkAreaFunctionMemberships(bs.getWorkspaceModule().getWorkspace(wsId));
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
										if      (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID)) psri.setAllowExternal(  true);
										else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID))  psri.setAllowForwarding(true);
										else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID)) psri.setAllowInternal(  true);
										else if (fiId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID))   psri.setAllowPublic(    true);
										
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

				// If this user has a user workspace...
				if (null != wsId) {
					// ...add their per user sharing rights to the
					// ...reply.
					reply.addUserRights(userIds.get(0), psri);
				}
			}

			// If we get here, reply refers to the
			// UserSharingRightsInfoRpcResponseData containing the
			// requested sharing rights information.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
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
			throw getGwtTeamingException(ex);
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
			throw getGwtTeamingException(ex);
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
			Workspace ws = ((Workspace) binder);
			if (ws.isReserved()) {
				// Yes!  Then we can determine its type based on its
				// internal ID.
				if      (ws.getInternalId().equals(ObjectKeys.TOP_WORKSPACE_INTERNALID))    reply = WorkspaceType.TOP;
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
					String view = BinderHelper.getBinderDefaultViewName(binder);
					if (MiscUtil.hasString(view)) {
						// Yes!  Check for those that we know.
						m_logger.debug("GwtServerHelper.getWorkspaceType( " + binder.getTitle() + "'s:  'Workspace View' ):  " + view);
						if      (view.equals(VIEW_WORKSPACE_DISCUSSIONS)) reply = WorkspaceType.DISCUSSIONS;
						else if (view.equals(VIEW_WORKSPACE_PROJECT))     reply = WorkspaceType.PROJECT_MANAGEMENT;
						else if (view.equals(VIEW_WORKSPACE_TEAM))        reply = WorkspaceType.TEAM;
						else if (view.equals(VIEW_WORKSPACE_USER))        reply = WorkspaceType.USER;
						else if (view.equals(VIEW_WORKSPACE_WELCOME))     reply = WorkspaceType.LANDING_PAGE;
						else if (view.equals(VIEW_WORKSPACE_GENERIC))     reply = WorkspaceType.WORKSPACE;
					}					
				}
			}
		}
		else {
			reply = WorkspaceType.NOT_A_WORKSPACE;
		}
		
		if (WorkspaceType.OTHER == reply) {
			m_logger.debug("GwtServerHelper.getWorkspaceType( 'Could not determine workspace type' ):  " + binder.getPathName());
		}
		return reply;
	}
	
	/**
	 * Returns whether the given ID is an 'all users' group.
	 * 
	 * @param bs
	 * @param groupId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static boolean isAllUsersGroup(AllModulesInjected bs, Long groupId) throws GwtTeamingException {
		try {
			// Get the group object.
			Principal group = bs.getProfileModule().getEntry(groupId);
			if ((null != group) && (group instanceof Group)) {
				String internalId = group.getInternalId();
				if ((null != internalId) &&
						(internalId.equalsIgnoreCase(ObjectKeys.ALL_USERS_GROUP_INTERNALID) ||
						 internalId.equalsIgnoreCase(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID))) {
					return true;
				}
			}
		}
		
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
		}
		
		// If we get here the group is not the "all users" group.
		return false;
	}
	
	/**
	 * Return whether dynamic group membership is allowed.  It is allowed if the ldap
	 * configuration has a value set for "LDAP attribute that uniquely identifies a user or group"
	 */
	public static boolean isDynamicGroupMembershipAllowed( AllModulesInjected ami )
	{
		return ami.getLdapModule().isGuidConfigured();
	}
	
	/**
	 * Returns true if a DefinableEntity is in the trash and false
	 * otherwise.
	 * 
	 * @param item
	 * 
	 * @return
	 */
	public static boolean isEntityPreDeleted(DefinableEntity item) {
		boolean reply;
		if      (item instanceof Binder)      reply = GwtUIHelper.isBinderPreDeleted((Binder) item);
		else if (item instanceof FolderEntry) reply = ((FolderEntry) item).isPreDeleted();
		else                                  reply = false;
		return reply;
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

	/**
	 * Returns true if a user's workspace is in the trash and false
	 * otherwise.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static boolean isUserWSInTrash(User user) {
		Workspace userWS = getUserWorkspace(user);
		return ((null != userWS) && userWS.isPreDeleted());
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
				throw getGwtTeamingException( e );
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

	/**
	 * Modify the given group
	 */
	public static void modifyGroup( AllModulesInjected ami, Long groupId, String title, String desc, boolean isMembershipDynamic, List<GwtTeamingItem> membership, GwtDynamicGroupMembershipCriteria membershipCriteria ) throws GwtTeamingException
	{
		Principal principal;
		GwtServerProfiler gsp;

		principal = ami.getProfileModule().getEntry( groupId );
		if ( principal != null && principal instanceof Group )
		{
			List<Principal> currentMembers;
			Set<Long> membershipIds;
			SortedSet<Principal> principals;
			String ldapQuery;
			Group group;

			group = (Group) principal;
			
			membershipIds = new HashSet<Long>();
			
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
				m_logger.debug( "GwtServerHelper.modifyGroup(), number of members in current group membership: " + String.valueOf( currentMembers.size() ) );
			}
			
			// Is the group membership dynamic?
			if ( isMembershipDynamic )
			{
				// Yes
				
				if ( membershipCriteria != null )
				{
					// Execute the ldap query and get the list of members from the results.
					gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - get list of dynamic group members" );
					try
					{
						int maxCount;
						HashSet<Long> potentialMemberIds;
						
						// Get a list of all the potential members.
						potentialMemberIds = ami.getLdapModule().getDynamicGroupMembers(
																			membershipCriteria.getBaseDn(),
																			membershipCriteria.getLdapFilterWithoutCRLF(),
																			membershipCriteria.getSearchSubtree() );
						
						// Get the maximum number of users that can be in a group.
						maxCount = SPropsUtil.getInt( "dynamic.group.membership.limit", 50000 ); 					
						
						// Is the number of potential members greater than the max number of group members?
						if ( potentialMemberIds.size() > maxCount )
						{
							int count;

							// Yes, only take the max number of users.
							count = 0;
							for (Long userId : potentialMemberIds)
							{
								membershipIds.add( userId );
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
							membershipIds = potentialMemberIds;
						}
					}
					catch (Exception ex)
					{
						// !!! What to do.
						membershipIds = new HashSet<Long>();
					}
					finally
					{
						gsp.end();
					}
				}
				else
					membershipIds = new HashSet<Long>();
				
				ldapQuery = membershipCriteria.getAsXml();
			}
			else
			{
				// No
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
			}

			// Modify the group.
			{
				Map updates;

				principals = ami.getProfileModule().getPrincipals( membershipIds );

				m_logger.debug( "GwtServerHelper.modifyGroup(), number of members in new group membership: " + String.valueOf( principals.size() ) );
			
				updates = new HashMap();
				updates.put( ObjectKeys.FIELD_ENTITY_TITLE, title );
				updates.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION, desc );
				updates.put( ObjectKeys.FIELD_ENTITY_DESCRIPTION_FORMAT, String.valueOf( Description.FORMAT_NONE ) );  
				updates.put( ObjectKeys.FIELD_GROUP_DYNAMIC, isMembershipDynamic );
				updates.put( ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, principals );
				updates.put( ObjectKeys.FIELD_GROUP_LDAP_QUERY, ldapQuery );
				
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - getProfileModule().modifyEntry()" );
				try
				{
					ami.getProfileModule().modifyEntry( groupId, new MapInputData( updates ) );
				}
	   			catch ( Exception ex )
	   			{
	   				throw getGwtTeamingException( ex );
	   			}
	   			finally
	   			{
	   				gsp.end();
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
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - create list of users removed from the group." );
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
					gsp.end();
					m_logger.debug( "GwtServerHelper.modifyGroup(), number of users removed from group: " + String.valueOf( usersRemovedFromGroup.size() ) );
					m_logger.debug( "GwtServerHelper.modifyGroup(), number of groups removed from group: " + String.valueOf( groupsRemovedFromGroup.size() ) );
				}
				
				// Get a list of the users and groups that were added to the group.
				gsp = GwtServerProfiler.start( m_logger, "GwtServerHelper.modifyGroup() - create list of users added to the group." );
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
					gsp.end();
					m_logger.debug( "GwtServerHelper.modifyGroup(), number of users added to group: " + String.valueOf( usersAddedToGroup.size() ) );
					m_logger.debug( "GwtServerHelper.modifyGroup(), number of groups added to group: " + String.valueOf( groupsAddedToGroup.size() ) );
				}

				m_logger.debug( "GwtServerHelper.modifyGroup(), number of changes to the group: " + String.valueOf( changes.size() ) );

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
		case CREATE_GROUP:
		{
			CreateGroupCmd cgCmd;
			String value;
			
			cgCmd = (CreateGroupCmd) cmd;
			value = cgCmd.getName();
			if ( MiscUtil.hasString( value ) )
				cgCmd.setName( StringCheckUtil.check( value ) );
			
			value = cgCmd.getDesc();
			if ( MiscUtil.hasString( value ) )
				cgCmd.setDesc( StringCheckUtil.check( value ) );
			
			value = cgCmd.getTitle();
			if ( MiscUtil.hasString( value ) )
				cgCmd.setTitle( StringCheckUtil.check( value ) );
			
			break;
		}
			
		// The following commands require XSS checks.
		case MARKUP_STRING_REPLACEMENT:
		{
			MarkupStringReplacementCmd msrCmd = ((MarkupStringReplacementCmd) cmd);
			String html = msrCmd.getHtml();
			if (MiscUtil.hasString(html)) {
				msrCmd.setHtml(StringCheckUtil.check(html));
			}
			break;
		}
			
			
		case MODIFY_GROUP:
		{
			ModifyGroupCmd mgCmd;
			String value;
			
			mgCmd = (ModifyGroupCmd) cmd;
			value = mgCmd.getDesc();
			if ( MiscUtil.hasString( value ) )
				mgCmd.setDesc( StringCheckUtil.check( value ) );
			
			value = mgCmd.getTitle();
			if ( MiscUtil.hasString( value ) )
				mgCmd.setTitle( StringCheckUtil.check( value ) );
			
			break;
		}
			
		case SAVE_BRANDING:
		{
			SaveBrandingCmd sbCmd = ((SaveBrandingCmd) cmd);
			GwtBrandingData bd = sbCmd.getBrandingData(); 
			String html = bd.getBranding();
			if (MiscUtil.hasString(html)) {
				bd.setBranding(StringCheckUtil.check(html));
			}
			break;
		}
			
		case SAVE_USER_STATUS:
		{
			SaveUserStatusCmd susCmd = ((SaveUserStatusCmd) cmd);
			String status = susCmd.getStatus();
			if (MiscUtil.hasString(status)) {
				susCmd.setStatus(StringCheckUtil.check(status));
			}
			break;
		}
			
		case REPLY_TO_ENTRY:
		{
			ReplyToEntryCmd rteCmd = ((ReplyToEntryCmd) cmd);
			String title = rteCmd.getTitle();
			if (MiscUtil.hasString(title)) {
				rteCmd.setTitle(StringCheckUtil.check(title));
			}
			String desc = rteCmd.getDescription();
			if (MiscUtil.hasString(desc)) {
				rteCmd.setDescription(StringCheckUtil.check(desc));
			}
			break;
		}
			
		case SAVE_FOLDER_COLUMNS:
		{
			SaveFolderColumnsCmd saveFCCmd = ((SaveFolderColumnsCmd) cmd);
			for (FolderColumn fc : saveFCCmd.getFolderColumns()) {
				fc.setColumnCustomTitle(StringCheckUtil.check(fc.getColumnCustomTitle()));
			}
			break;
		}
		
		case GET_JSP_HTML:
		{
			GetJspHtmlCmd jspHtmlCmd = ((GetJspHtmlCmd) cmd);
			Map<String,Object> model = jspHtmlCmd.getModel();
			StringCheckUtil.check(model, Boolean.TRUE);
		}
			
		// The following commands do not require XSS checks.
		case ABORT_FILE_UPLOAD:
		case ADD_FAVORITE:
		case ADD_NEW_FOLDER:
		case CAN_ADD_FOLDER:
		case CAN_MODIFY_BINDER:
		case CHANGE_ENTRY_TYPES:
		case CHANGE_FAVORITE_STATE:
		case CHECK_NET_FOLDERS_STATUS:
		case CHECK_NET_FOLDER_SERVERS_STATUS:
		case COLLAPSE_SUBTASKS:
		case COMPLETE_EXTERNAL_USER_SELF_REGISTRATION:
		case COPY_ENTRIES:
		case CREATE_EMAIL_REPORT:
		case CREATE_LICENSE_REPORT:
		case CREATE_LOGIN_REPORT:
		case CREATE_NET_FOLDER:
		case CREATE_USER_ACCESS_REPORT:
		case CREATE_USER_ACTIVITY_REPORT:
		case CREATE_NET_FOLDER_ROOT:
		case DELETE_NET_FOLDERS:
		case DELETE_NET_FOLDER_ROOTS:
		case DELETE_FOLDER_ENTRIES:
		case DELETE_GROUPS:
		case DELETE_TASKS:
		case DELETE_USER_WORKSPACES:
		case DISABLE_USERS:
		case ENABLE_USERS:
		case EXECUTE_ENHANCED_VIEW_JSP:
		case EXECUTE_LANDING_PAGE_CUSTOM_JSP:
		case EXECUTE_SEARCH:
		case EXPAND_HORIZONTAL_BUCKET:
		case EXPAND_SUBTASKS:
		case EXPAND_VERTICAL_BUCKET:
		case FIND_USER_BY_EMAIL_ADDRESS:
		case GET_ACCESSORY_STATUS:
		case GET_ACTIVITY_STREAM_DATA:
		case GET_ACTIVITY_STREAM_PARAMS:
		case GET_ADD_MEETING_URL:
		case GET_ADHOC_FOLDER_SETTING:
		case GET_ADMIN_ACTIONS:
		case GET_ALL_NET_FOLDERS:
		case GET_ALL_NET_FOLDER_ROOTS:
		case GET_ALL_GROUPS:
		case GET_BINDER_BRANDING:
		case GET_BINDER_DESCRIPTION:
		case GET_BINDER_FILTERS:
		case GET_BINDER_INFO:
		case GET_BINDER_OWNER_AVATAR_INFO:
		case GET_BINDER_PERMALINK:
		case GET_BINDER_REGION_STATE:
		case GET_BINDER_STATS:
		case GET_BINDER_TAGS:
		case GET_BLOG_ARCHIVE_INFO:
		case GET_BLOG_PAGES:
		case GET_CALENDAR_APPOINTMENTS:
		case GET_CALENDAR_DISPLAY_DATA:
		case GET_CALENDAR_DISPLAY_DATE:
		case GET_CALENDAR_NEXT_PREVIOUS_PERIOD:
		case GET_CLIPBOARD_TEAM_USERS:
		case GET_CLIPBOARD_USERS:
		case GET_CLIPBOARD_USERS_FROM_LIST:
		case GET_COLLECTION_POINT_DATA:
		case GET_COLUMN_WIDTHS:
		case GET_DEFAULT_ACTIVITY_STREAM:
		case GET_DEFAULT_FOLDER_DEFINITION_ID:
		case GET_DESKTOP_APP_DOWNLOAD_INFO:
		case GET_DOCUMENT_BASE_URL:
		case GET_DOWNLOAD_FILE_URL:
		case GET_DISK_USAGE_INFO:
		case GET_DYNAMIC_MEMBERSHIP_CRITERIA:
		case GET_EMAIL_NOTIFICATION_INFORMATION:
		case GET_ENTITY_ACTION_TOOLBAR_ITEMS:
		case GET_ENTRY:
		case GET_ENTRY_COMMENTS:
		case GET_ENTRY_TAGS:
		case GET_ENTRY_TYPES:
		case GET_EXECUTE_JSP_URL:
		case GET_EXTENSION_FILES:
		case GET_EXTENSION_INFO:
		case GET_FAVORITES:
		case GET_FILE_ATTACHMENTS:
		case GET_FILE_SYNC_APP_CONFIGURATION:
		case GET_FILE_URL:
		case GET_FOLDER:
		case GET_FOLDER_COLUMNS:
		case GET_FOLDER_DISPLAY_DATA:
		case GET_FOLDER_ENTRIES:
		case GET_FOLDER_ENTRY_DETAILS:
		case GET_FOLDER_ROWS:
		case GET_FOLDER_SORT_SETTING:
		case GET_FOLDER_TOOLBAR_ITEMS:
		case GET_FOOTER_TOOLBAR_ITEMS:
		case GET_GROUP_ASSIGNEE_MEMBERSHIP:
		case GET_GROUP_MEMBERSHIP:
		case GET_GROUP_MEMBERSHIP_INFO:
		case GET_GROUPS:
		case GET_HELP_URL:
		case GET_HORIZONTAL_NODE:
		case GET_HORIZONTAL_TREE:
		case GET_IM_URL:
		case GET_INHERITED_LANDING_PAGE_PROPERTIES:
		case GET_IS_DYNAMIC_GROUP_MEMBERSHIP_ALLOWED:
		case GET_LANDING_PAGE_DATA:
		case GET_LIST_OF_CHILD_BINDERS:
		case GET_LIST_OF_FILES:
		case GET_LOGGED_IN_USER_PERMALINK:
		case GET_LOGIN_INFO:
		case GET_MAIN_PAGE_INFO:
		case GET_MANAGE_USERS_INFO:
		case GET_MANAGE_USERS_STATE:
		case GET_MICRO_BLOG_URL:
		case GET_MOBILE_APPS_CONFIG:
		case GET_MODIFY_BINDER_URL:
		case GET_MY_FILES_CONTAINER_INFO:
		case GET_MY_TASKS:
		case GET_MY_TEAMS:
		case GET_NET_FOLDER:
		case GET_NEXT_PREVIOUS_FOLDER_ENTRY_INFO:
		case GET_NUMBER_OF_MEMBERS:
		case GET_PARENT_BINDER_PERMALINK:
		case GET_PERSONAL_PREFERENCES:
		case GET_PRESENCE_INFO:
		case GET_PROFILE_AVATARS:
		case GET_PROFILE_ENTRY_INFO:
		case GET_PROFILE_INFO:
		case GET_PROFILE_STATS:
		case GET_PROJECT_INFO:
		case GET_QUICK_VIEW_INFO:
		case GET_RECENT_PLACES:
		case GET_REPORTS_INFO:
		case GET_ROOT_WORKSPACE_ID:
		case GET_SAVED_SEARCHES:
		case GET_SEND_TO_FRIEND_URL:
		case GET_ZONE_SHARE_RIGHTS:
		case GET_SHARING_INFO:
		case GET_SIGN_GUESTBOOK_URL:
		case GET_SITE_ADMIN_URL:
		case GET_SITE_BRANDING:
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
		case GET_TOOLBAR_ITEMS:
		case GET_TOP_RANKED:
		case GET_UPGRADE_INFO:
		case GET_USER_ACCESS_CONFIG:
		case GET_USER_FILE_SYNC_APP_CONFIG:
		case GET_USER_MOBILE_APPS_CONFIG:
		case GET_USER_PERMALINK:
		case GET_USER_PROPERTIES:
		case GET_USER_SHARING_RIGHTS_INFO:
		case GET_USER_STATUS:
		case GET_USER_WORKSPACE_INFO:
		case GET_VERTICAL_ACTIVITY_STREAMS_TREE:
		case GET_VERTICAL_NODE:
		case GET_VERTICAL_TREE:
		case GET_VIEW_FILE_URL:
		case GET_VIEW_FOLDER_ENTRY_URL:
		case GET_VIEW_INFO:
		case GET_WHO_HAS_ACCESS:
		case GET_WORKSPACE_CONTRIBUTOR_IDS:
		case HAS_ACTIVITY_STREAM_CHANGED:
		case IMPORT_ICAL_BY_URL:
		case IS_ALL_USERS_GROUP:
		case IS_PERSON_TRACKED:
		case IS_SEEN:
		case LOCK_ENTRIES:
		case MODIFY_NET_FOLDER:
		case MODIFY_NET_FOLDER_ROOT:
		case MOVE_ENTRIES:
		case PERSIST_ACTIVITY_STREAM_SELECTION:
		case PERSIST_NODE_COLLAPSE:
		case PERSIST_NODE_EXPAND:
		case PIN_ENTRY:
		case PURGE_FOLDER_ENTRIES:
		case PURGE_TASKS:
		case PURGE_USER_WORKSPACES:
		case PURGE_USERS:
		case REMOVE_EXTENSION:
		case REMOVE_FAVORITE:
		case REMOVE_TASK_LINKAGE:
		case REMOVE_SAVED_SEARCH:
		case SAVE_ACCESSORY_STATUS:
		case SAVE_ADHOC_FOLDER_SETTING:
		case SAVE_BINDER_REGION_STATE:
		case SAVE_CALENDAR_DAY_VIEW:
		case SAVE_CALENDAR_HOURS:
		case SAVE_CALENDAR_SETTINGS:
		case SAVE_CALENDAR_SHOW:
		case SAVE_CLIPBOARD_USERS:
		case SAVE_COLUMN_WIDTHS:
		case SAVE_EMAIL_NOTIFICATION_INFORMATION:
		case SAVE_FILE_SYNC_APP_CONFIGURATION:
		case SAVE_FOLDER_ENTRY_DLG_POSITION:
		case SAVE_FOLDER_PINNING_STATE:
		case SAVE_FOLDER_SORT:
		case SAVE_MANAGE_USERS_STATE:
		case SAVE_MOBILE_APPS_CONFIGURATION:
		case SAVE_PERSONAL_PREFERENCES:
		case SAVE_SHARED_FILES_STATE:
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
		case SAVE_USER_ACCESS_CONFIG:
		case SAVE_USER_FILE_SYNC_APP_CONFIG:
		case SAVE_USER_MOBILE_APPS_CONFIGURATION:
		case SAVE_WHATS_NEW_SETTINGS:
		case SAVE_ZONE_SHARE_RIGHTS:
		case SET_DESKTOP_APP_DOWNLOAD_VISIBILITY:
		case SET_ENTRIES_PIN_STATE:
		case SET_SEEN:
		case SET_UNSEEN:
		case SET_USER_SHARING_RIGHTS_INFO:
		case SHARE_ENTRY:
		case SYNC_NET_FOLDERS:
		case SYNC_NET_FOLDER_SERVER:
		case TEST_GROUP_MEMBERSHIP_LDAP_QUERY:
		case TEST_NET_FOLDER_CONNECTION:
		case TRACK_BINDER:
		case TRASH_PURGE_ALL:
		case TRASH_PURGE_SELECTED_ENTRIES:
		case TRASH_RESTORE_ALL:
		case TRASH_RESTORE_SELECTED_ENTRIES:
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
		case VALIDATE_EMAIL_ADDRESS:
		case VALIDATE_ENTRY_EVENTS:
		case VALIDATE_UPLOADS:
			break;
			
		default:
			// Log an error that we encountered an unhandled command.
			m_logger.error("GwtServerHelper.performXSSCheckOnRpcCmd( Unhandled Command ):  " + cmd.getClass().getName());
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
	 * Purges the specified folder entries.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * @param deleteMirroredSource
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData purgeFolderEntries(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, boolean deleteMirroredSource) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Scan the entry IDs...
			BinderModule bm = bs.getBinderModule();
			FolderModule fm = bs.getFolderModule();
			for (EntityId entityId:  entityIds) {
				try {
					// ...purging each entity...
					if (entityId.isBinder())
					     bm.deleteBinder(                       entityId.getEntityId(), deleteMirroredSource, null);
					else fm.deleteEntry(entityId.getBinderId(), entityId.getEntityId()                            );
				}
				
				catch (Exception e) {
					// ...tracking any that we couldn't purge.
					String entryTitle = getEntityTitle(bs, entityId);
					String messageKey;
					if (e instanceof AccessControlException) messageKey = "purgeEntryError.AccssControlException";
					else                                     messageKey = "purgeEntryError.OtherException";
					reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
				}
			}
			
			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
		}
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
			throw getGwtTeamingException(ex);
		}
	}
	
	/**
	 * Save the given File Sync App configuration
	 */
	public static Boolean saveFileSyncAppConfiguration(
		AllModulesInjected allModules,
		GwtFileSyncAppConfiguration fsaConfiguration ) throws GwtTeamingException
	{
		AdminModule adminModule;
		Boolean enabled;
		Boolean deployEnabled;
		Boolean allowCachePwd;
		Integer interval;
		Integer maxFileSize;
		String autoUpdateUrl;
		
		adminModule = allModules.getAdminModule();
		enabled = new Boolean( fsaConfiguration.getIsFileSyncAppEnabled() );
		interval = new Integer( fsaConfiguration.getSyncInterval() );
		deployEnabled = new Boolean( fsaConfiguration.getIsDeploymentEnabled() );
		allowCachePwd = new Boolean( fsaConfiguration.getAllowCachePwd() );
		maxFileSize = new Integer( fsaConfiguration.getMaxFileSize() );
		
		// Did the user enter an auto update url?
		autoUpdateUrl = fsaConfiguration.getAutoUpdateUrl();
		if ( autoUpdateUrl != null && autoUpdateUrl.length() > 0 )
		{
			// Yes, is it valid?
			if ( validateDesktopAppDownloadUrl( autoUpdateUrl ) == false )
			{
				GwtTeamingException gtEx;
				
				// No
				gtEx = getGwtTeamingException();
				gtEx.setExceptionType( ExceptionType.INVALID_AUTO_UPDATE_URL );
				throw gtEx;				
			}
		}
		
		adminModule.setFileSynchAppSettings(
										enabled,
										interval,
										autoUpdateUrl,
										deployEnabled,
										allowCachePwd,
										maxFileSize );

		return Boolean.TRUE;
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
				String columnSortOrder = "";
				for (FolderColumn fc : fcList) {
					if (!columnSortOrder.equals("")) columnSortOrder += "|";
					columnSortOrder += fc.getColumnName();
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
						columnSortOrder, isDefault);
			}
			
			return Boolean.FALSE;
		}
		
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
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
				String cName     = ("." + String.valueOf(binderInfo.getCollectionType().ordinal()));
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			Long			binderId = binderInfo.getBinderIdAsLong();
			Long			userId = getCurrentUserId();
			ProfileModule	pm     = bs.getProfileModule();
			pm.setUserProperty(userId, binderId, propSortBy,                      sortKey       );
			pm.setUserProperty(userId, binderId, propSortDescend, String.valueOf(!sortAscending));
			
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("GwtServerHelper.saveFolderSort( Stored folder sort for binder ):  Binder:  " + binderId.longValue() + ", Sort Key:  '" + sortKey + "', Sort Ascending:  " + sortAscending);
			}
			return Boolean.FALSE;
		}
		
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
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
			if (mus.isShowDisabled()) hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_DISABLED); else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_DISABLED, Boolean.FALSE);
			if (mus.isShowEnabled())  hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_ENABLED);  else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_ENABLED,  Boolean.FALSE);
			if (mus.isShowExternal()) hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_EXTERNAL); else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_EXTERNAL, Boolean.FALSE);
			if (mus.isShowInternal()) hSession.removeAttribute(CACHED_MANAGE_USERS_SHOW_INTERNAL); else hSession.setAttribute(CACHED_MANAGE_USERS_SHOW_INTERNAL, Boolean.FALSE);
			return Boolean.TRUE;
		}
		catch (Exception ex) {
			throw getGwtTeamingException(ex);
		}		
	}

	/**
	 * Save the given Mobile Apps configuration
	 */
	public static Boolean saveMobileAppsConfiguration(
		AllModulesInjected allModules,
		GwtZoneMobileAppsConfig gwtMobileAppsConfig ) throws GwtTeamingException
	{
		AdminModule adminModule;
		MobileAppsConfig mobileAppsConfig;
		
		adminModule = allModules.getAdminModule();
		
		mobileAppsConfig = new MobileAppsConfig();
		mobileAppsConfig.setMobileAppsAllowCacheContent( gwtMobileAppsConfig.getAllowCacheContent() );
		mobileAppsConfig.setMobileAppsAllowCachePwd( gwtMobileAppsConfig.getAllowCachePwd() );
		mobileAppsConfig.setMobileAppsAllowPlayWithOtherApps( gwtMobileAppsConfig.getAllowPlayWithOtherApps() );
		mobileAppsConfig.setMobileAppsEnabled( gwtMobileAppsConfig.getMobileAppsEnabled() );
		mobileAppsConfig.setMobileAppsSyncInterval( gwtMobileAppsConfig.getSyncInterval() );
		
		adminModule.setMobileAppsConfig( mobileAppsConfig );

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
				
				// Get the setting for "send email to"
				setting = subscriptionData.getSendEmailToAsString();
				if ( setting != null && setting.length > 0 )
					subscriptionSettings.put( Subscription.MESSAGE_STYLE_EMAIL_NOTIFICATION, setting );
				
				// Get the setting for "send email to without an attachment"
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
			     m_logger.warn( "GwtServerHelper.saveSubscriptionData() AccessControlException" );
			else m_logger.warn( "GwtServerHelper.saveSubscriptionData() unknown exception"      );
			
			throw getGwtTeamingException( e );
		}
		
		return Boolean.TRUE;
	}

	/**
	 * Save the UserAccessConfig information.  This includeds "allow guest access", "allow self registration",
	 * "allow external users" and "allow external user to perform self registration"
	 */
	public static Boolean saveUserAccessConfig(
		AllModulesInjected ami,
		UserAccessConfig config )
	{
		AuthenticationConfig authConfig;
		
		authConfig = ami.getAuthenticationModule().getAuthenticationConfig();
		
		// Set "guest access"
		authConfig.setAllowAnonymousAccess( config.getAllowGuestAccess() );
		
		// Set "guest read only"
		authConfig.setAnonymousReadOnly( config.getGuestReadOnly() );
		
		if ( ReleaseInfo.isLicenseRequiredEdition() )
		{
			OpenIDConfig openIdConfig;
			ZoneConfig zoneConfig;
			ZoneModule zoneModule;
			AdminModule adminModule;
			
			adminModule = ami.getAdminModule();
			zoneModule = ami.getZoneModule();
			zoneConfig = zoneModule.getZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
			
			// Set "allow external users to self register"
			openIdConfig = zoneConfig.getOpenIDConfig();
			openIdConfig.setAuthenticationEnabled( config.getAllowExternalUsers() );
			openIdConfig.setSelfProvisioningEnabled( config.getAllowExternalUsersSelfReg() );
			
			// Set "allow external users"
			adminModule.setExternalUserEnabled( config.getAllowExternalUsers() );
			adminModule.setOpenIDConfig( openIdConfig );
		}
		else
		{
			// Self registration is only found in the Kablink version
			authConfig.setAllowSelfRegistration( config.getAllowSelfReg() );
		}
		
		ami.getAuthenticationModule().setAuthenticationConfig( authConfig );
		
		return Boolean.TRUE;
	}
	
	/**
	 * Save the given GwtUserFileSyncAppConfig settings for the given users.
	 * 
	 */
	public static SaveUserFileSyncAppConfigRpcResponseData saveUserFileSyncAppConfig(
		AllModulesInjected ami,
		GwtUserFileSyncAppConfig config,
		List<Long> userIds )
	{
		ProfileModule profileModule;
		SaveUserFileSyncAppConfigRpcResponseData responseData;
		
		responseData = new SaveUserFileSyncAppConfigRpcResponseData();
		
		if ( config == null || userIds == null )
		{
			responseData.addError( "Invalid parameters passed to saveUserFileSyncAppConfig()" );
			return responseData;
		}
		
		profileModule = ami.getProfileModule();
		
		for ( Long userId : userIds )
		{
			try
			{
				profileModule.setUserProperty(
											userId,
											ObjectKeys.USER_PROPERTY_DESKTOP_APP_ACCESS_FILR,
											String.valueOf( config.getIsFileSyncAppEnabled() ) );
	
				profileModule.setUserProperty(
											userId,
											ObjectKeys.USER_PROPERTY_DESKTOP_APP_CACHE_PWD,
											String.valueOf( config.getAllowCachePwd() ) );
			}
			catch ( Exception ex )
			{
				User user;
				String errMsg;
				String cause;
				String[] errorArgs;
				String errorTag = "save.user.file.sync.app.config.error";
				
				user = (User) profileModule.getEntry( userId );

				if ( user.isDisabled() == true )
					cause = NLT.get( "save.user.file.sync.app.config.error.disabled.user" );
				else
					cause = ex.getLocalizedMessage();
				errorArgs = new String[] { user.getTitle(), cause };
				errMsg = NLT.get( errorTag, errorArgs );

				responseData.addError( errMsg );
			}
		}
		
		return responseData;
	}
	
	/**
	 * Save the given GwtUserMobileAppsConfig settings for the given users.
	 * 
	 */
	public static SaveUserMobileAppsConfigRpcResponseData saveUserMobileAppsConfig(
		AllModulesInjected ami,
		GwtUserMobileAppsConfig config,
		List<Long> userIds )
	{
		ProfileModule profileModule;
		SaveUserMobileAppsConfigRpcResponseData responseData;
		
		responseData = new SaveUserMobileAppsConfigRpcResponseData();
		
		if ( config == null || userIds == null )
		{
			responseData.addError( "Invalid parameters passed to saveUserMobileAppsConfig()" );
			return responseData;
		}
		
		profileModule = ami.getProfileModule();
		
		for ( Long userId : userIds )
		{
			try
			{
				profileModule.setUserProperty(
											userId,
											ObjectKeys.USER_PROPERTY_MOBILE_APPS_ACCESS_FILR,
											String.valueOf( config.getMobileAppsEnabled() ) );
	
				profileModule.setUserProperty(
											userId,
											ObjectKeys.USER_PROPERTY_MOBILE_APPS_CACHE_PWD,
											String.valueOf( config.getAllowCachePwd() ) );
	
				profileModule.setUserProperty(
											userId,
											ObjectKeys.USER_PROPERTY_MOBILE_APPS_CACHE_CONTENT,
											String.valueOf( config.getAllowCacheContent() ) );
	
				profileModule.setUserProperty(
											userId,
											ObjectKeys.USER_PROPERTY_MOBILE_APPS_PLAY_WITH_OTHER_APPS,
											String.valueOf( config.getAllowPlayWithOtherApps() ) );
			}
			catch ( Exception ex )
			{
				User user;
				String errMsg;
				String cause;
				String[] errorArgs;
				String errorTag = "save.user.mobile.app.config.error";
				
				user = (User) profileModule.getEntry( userId );

				if ( user.isDisabled() == true )
					cause = NLT.get( "save.user.mobile.app.config.error.disabled.user" );
				else
					cause = ex.getLocalizedMessage();
				errorArgs = new String[] { user.getTitle(), cause };
				errMsg = NLT.get( errorTag, errorArgs );

				responseData.addError( errMsg );
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
			throw getGwtTeamingException(ex);
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
	public static ErrorListRpcResponseData setUserSharingRightsInfo(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds, CombinedPerUserShareRightsInfo sharingRights) throws GwtTeamingException {
		try {
			// Create the ErrorListRpcResponseData to return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// We're we given any share rights to set?
			if (MiscUtil.hasItems(userIds) && (null != sharingRights)) {
				// Yes!  Are there any rights actually being set?
				PerUserShareRightsInfo setFlags = sharingRights.getSetFlags();
				if (!(setFlags.anyFlagsSet())) {
					// No!  Bail, there's nothing to do.
					return reply;
				}
				
				// Get the right values to be set.
				PerUserShareRightsInfo valueFlags = sharingRights.getValueFlags();
				
				// Access the Function's we may need to set/clear
				// on the selected user workspaces...
				Long allowExternal   = null;
				Long allowForwarding = null;
				Long allowInternal   = null;
				Long allowPublic     = null;
				List<Function> fs = bs.getAdminModule().getFunctions();
				for (Function f:  fs) {
					String fId = f.getInternalId();
					if (MiscUtil.hasString(fId)) {
						if      (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_EXTERNAL_INTERNALID)) allowExternal   = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_FORWARD_INTERNALID))  allowForwarding = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_INTERNAL_INTERNALID)) allowInternal   = f.getId();
						else if (fId.equalsIgnoreCase(ObjectKeys.FUNCTION_ALLOW_SHARING_PUBLIC_INTERNALID))   allowPublic     = f.getId();
					}
				}

				// ...and access the modules we'll need to set them.
				WorkspaceModule	wm  = bs.getWorkspaceModule();
				AdminModule		am  = bs.getAdminModule();
				
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
								// Yes!  Does this user have a
								// workspace ID?
								user = ((User) p);
								Long wsId = user.getWorkspaceId();
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
									if (setFlags.isAllowExternal())   am.updateWorkAreaFunctionMembership(ws, allowExternal,   valueFlags.isAllowExternal(),   ObjectKeys.OWNER_USER_ID);
									if (setFlags.isAllowForwarding()) am.updateWorkAreaFunctionMembership(ws, allowForwarding, valueFlags.isAllowForwarding(), ObjectKeys.OWNER_USER_ID);
									if (setFlags.isAllowInternal())   am.updateWorkAreaFunctionMembership(ws, allowInternal,   valueFlags.isAllowInternal(),   ObjectKeys.OWNER_USER_ID);
									if (setFlags.isAllowPublic())     am.updateWorkAreaFunctionMembership(ws, allowPublic,     valueFlags.isAllowPublic(),     ObjectKeys.OWNER_USER_ID);
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
			throw getGwtTeamingException(ex);
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
		if ( MiscUtil.isSystemUserAccount( user.getName() ) )
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
						m_logger.error( "Unable to set the user's timezone: " + ex.toString() );
					}
				}
			}
		}
	}

	/**
	 * Send an email notification to the given recipients for the given
	 * entities.
	 * 
	 * This code was taken from RelevanceAjaxController.java,
	 * ajaxSaveShareThisBinder() and modified.
	 * 
	 * @param ami
	 * @param entityIds
	 * @param addedComments
	 * @param principalIds
	 * @param teamIds
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static GwtShareEntryResults shareEntry( AllModulesInjected ami, List<EntityId> entityIds, String addedComments, List<String> principalIds, List<String> teamIds )
		throws Exception
	{
		AdminModule adminModule;
		BinderModule binderModule;
		FolderModule folderModule;
		ProfileModule profileModule;
		Set<Long> principalIdsL;
		Set<Long> teamIdsL;
		List emailErrors;
		List<User> noAccessPrincipals;
		GwtShareEntryResults results;
		
		// Convert all of the user and groups ids to Longs
		principalIdsL = new HashSet<Long>();
		if ( principalIds != null )
		{
			for ( String nextId : principalIds )
			{
				principalIdsL.add( Long.valueOf( nextId ) );
			}
		}
		
		// Convert all of the team ids to Longs
		teamIdsL = new HashSet<Long>();
		if ( teamIds != null )
		{
			for ( String nextId : teamIds )
			{
				teamIdsL.add( Long.valueOf( nextId ) );
			}
		}
		
		emailErrors        = null;
		noAccessPrincipals = new ArrayList<User>();

		adminModule   = ami.getAdminModule();
		binderModule  = ami.getBinderModule();
		folderModule  = ami.getFolderModule();
		profileModule = ami.getProfileModule();

		for ( EntityId entityId:  entityIds )
		{
			DefinableEntity de;
			if ( entityId.isBinder() )
			     de = binderModule.getBinder(                        entityId.getEntityId() );
			else de = folderModule.getEntry( entityId.getBinderId(), entityId.getEntityId() );
			
			profileModule.setShares( de, principalIdsL, teamIdsL );
	
			// Send an email to the given recipients.
			{
				String title;
				String shortTitle;
				String desc;
				Description body;
		        User user;
				String mailTitle;
				Set<String> emailAddress;
				String bccEmailAddress;
				List entityEmailErrors;
	
		        user = RequestContextHolder.getRequestContext().getUser();
	
				title = de.getTitle();
				shortTitle = title;
				
				if ( de.getParentBinder() != null )
					title = de.getParentBinder().getPathName() + "/" + title;
	
				// Do NOT use interactive context when constructing permalink for email. See Bug 536092.
				desc = "<a href=\"" + PermaLinkUtil.getPermalinkForEmail( de ) + "\">" + title + "</a><br/><br/>" + addedComments;
				body = new Description( desc );
	
				mailTitle = NLT.get( "relevance.mailShared", new Object[]{Utils.getUserTitle( user )} );
				mailTitle += " (" + shortTitle +")";
				
				emailAddress = new HashSet();
				
				//See if this user wants to be BCC'd on all mail sent out
				bccEmailAddress = user.getBccEmailAddress();
				if ( bccEmailAddress != null && !bccEmailAddress.equals("") )
				{
					if ( !emailAddress.contains( bccEmailAddress.trim() ) )
					{
						//Add the user's chosen bcc email address
						emailAddress.add( bccEmailAddress.trim() );
					}
				}
				
				// Send the email notification
				entityEmailErrors = (List)adminModule.sendMail( principalIdsL, teamIdsL, emailAddress, null, null, mailTitle, body ).get( ObjectKeys.SENDMAIL_ERRORS );
				if ( null == emailErrors )
				{
					emailErrors = entityEmailErrors;
				}
				else
				{
					if ( null != entityEmailErrors )
					{
						emailErrors.addAll( entityEmailErrors );
					}
				}
			}
				
			// Check to see if the recipients have rights to see the given entry.
			{
				Set<Long> totalIds;
				Set<Principal> totalUsers;
	
				totalIds = new HashSet<Long>();
				totalIds.addAll( principalIdsL );
				
				totalUsers = profileModule.getPrincipals( totalIds );
				for ( Principal p : totalUsers )
				{
					if ( p instanceof User )
					{
						try
						{
							AccessUtils.readCheck( (User)p, de );
						}
						catch( AccessControlException e )
						{
							noAccessPrincipals.add( (User) p );
						}
					}
				}
			}
		}

		results = new GwtShareEntryResults();
		
		// Package up the results.
		{
			// Do we have any users who were sent an email who don't have rights to read the entry?
			if ( noAccessPrincipals != null && noAccessPrincipals.size() > 0 )
			{
				// Yes, add them to the results
				for ( User user : noAccessPrincipals )
				{
					GwtUser gwtUser;
					
					// Add this user to the results.
					gwtUser = new GwtUser();
					gwtUser.setInternal( user.getIdentityInfo().isInternal() );
					gwtUser.setUserId( user.getId() );
					gwtUser.setName( user.getName() );
					gwtUser.setTitle( Utils.getUserTitle( user ) );
					gwtUser.setWorkspaceTitle( user.getWSTitle() );
					
					results.addUser( gwtUser );
				}
			}
			
			// Add any errors that happened to the results.
			if ( null != emailErrors )
			{
				results.addErrors( emailErrors );
			}
		}
			
		return results;
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
				throw getGwtTeamingException( ex );
			}
		}
		else
		{
			GwtTeamingException ex;
			
			// No, throw an exception
			ex = getGwtTeamingException();
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
	 */
	public static Boolean updateBinderTags( AllModulesInjected bs, String binderId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded )
	{
		BinderModule bm;
		Binder binder;

		bm = bs.getBinderModule();
		binder = bm.getBinder( Long.parseLong( binderId ) );
		
		// Go through the list of tags to be deleted and delete them.
		for ( TagInfo nextTag : tagsToBeDeleted )
		{
			deleteBinderTag( bm, binder, nextTag );
		}
		
		// Go through the list of tags to be added and add them.
		for ( TagInfo nextTag : tagsToBeAdded )
		{
			addBinderTag( bm, binder, nextTag );
		}
		
		// If we get here, everything worked.
		return Boolean.TRUE;
	}
	
	/**
	 * Update the tags for the given entry.
	 */
	public static Boolean updateEntryTags( AllModulesInjected bs, String entryId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded )
	{
		FolderModule fm;
		Long entryIdL;
		
		fm = bs.getFolderModule();
		entryIdL = Long.parseLong( entryId );
		
		// Go through the list of tags to be deleted and delete them.
		for ( TagInfo nextTag : tagsToBeDeleted )
		{
			deleteEntryTag( fm, entryIdL, nextTag );
		}
		
		// Go through the list of tags to be added and add them.
		for ( TagInfo nextTag : tagsToBeAdded )
		{
			addEntryTag( fm, entryIdL, nextTag );
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
	 * Returns true if baseUrl is a valid desktop application download
	 * URL and false otherwise.
	 * 
	 * @param baseUrl
	 * 
	 * @return
	 */
	public static boolean validateDesktopAppDownloadUrl(String baseUrl) {
		// Validate the URL for any redirects, ...
		baseUrl = getFinalHttpUrl(baseUrl, "From validateDesktopAppDownloadUrl()");
		if (!(MiscUtil.hasString(baseUrl))) {
			return false;
		}
		
		// ...and test it.
		String platformTail = (Utils.checkIfFilr() ? WIN32_TAIL_FILR : WIN32_TAIL_VIBE);
		String jsonData = doHTTPGet((baseUrl + platformTail + JSON_TAIL));
		return (null != getJSOFromS(jsonData));
	}
	
	/**
	 * Validate the given email address.
	 * 
	 * @param emailAddress
	 * @param addressField
	 * 
	 * @return
	 */
	public static Boolean validateEmailAddress(String emailAddress, ValidateEmailAddressCmd.AddressField addressField) {
		String usedAs;
		
		switch (addressField) {
		default:
		case MAIL_TO:  usedAs = MailModule.TO;  break;
		case MAIL_BC:  usedAs = MailModule.BCC; break;
		case MAIL_CC:  usedAs = MailModule.CC;  break;
		}
		
		return MiscUtil.isEmailAddressValid(usedAs, emailAddress);
	}
	
	/**
	 * Validate the list of TeamingEvents to see if the user has rights to perform the events
	 */
	public static void validateEntryEvents( AllModulesInjected bs, HttpRequestInfo ri, List<EventValidation> eventValidations, String entryId )
	{
		// Initialize all events as invalid.
		for ( EventValidation nextValidation : eventValidations )
		{
			// Validate this event.
			nextValidation.setIsValid( false );
		}

		try
		{
			FolderModule folderModule;
			FolderEntry folderEntry;
			Long entryIdL;
			
			folderModule = bs.getFolderModule();
			entryIdL = new Long( entryId );

			folderEntry = folderModule.getEntry( null, entryIdL );
	        
			for ( EventValidation nextValidation : eventValidations )
			{
				TeamingEvents teamingEvent;
				
				// Validate the next event.
				try
				{
					teamingEvent = TeamingEvents.getEnum(nextValidation.getEventOrdinal());
					
					switch (teamingEvent)
					{
						case DELETE_ENTRY:
							folderModule.checkAccess( folderEntry, FolderOperation.deleteEntry );
							break;
						
						case INVOKE_REPLY:
							folderModule.checkAccess( folderEntry, FolderOperation.addReply );
							break;
						
						case INVOKE_TAG:
						{
							// Tag is valid if the user can manage public tags or can read the entry.
							if ( canManagePublicEntryTags( bs, entryId ) == true )
							{
								// Nothing to do.
							}
							else if ( canManagePersonalEntryTags( bs, entryId ) )
							{
								// Nothing to do.
							}
							else
								throw new AccessControlException();
							break;
						}
						
						case INVOKE_SHARE:
							if ( bs.getSharingModule().testAddShareEntity( folderEntry ) == false )
								throw new AccessControlException();
							
							break;
						
						case INVOKE_SUBSCRIBE:
							folderModule.checkAccess( folderEntry, FolderOperation.readEntry );
							break;
							
						case INVOKE_SEND_TO_FRIEND:
						{
							User user;
							
							user = getCurrentUser();
	
							// Does the user have an email address and is the user not guest?
							if ( !user.getEmailAddresses().isEmpty() && 
									!ObjectKeys.GUEST_USER_INTERNALID.equals( user.getInternalId() ) )
							{
								// Yes, nothing to do
							}
							else
							{
								throw new AccessControlException();
							}
							break;
						}
						
						case MARK_ENTRY_READ:
						case MARK_ENTRY_UNREAD:
							// Nothing to do.
							break;
						
						default:
							m_logger.info( "Unknown event in GwtServerHelper.validateEntryEvents() - " + teamingEvent.toString() );
							break;
					}

					// If we get here the action is valid.
					nextValidation.setIsValid( true );
				}
				catch (AccessControlException acEx)
				{
				}
			}
		}
		catch (NoFolderEntryByTheIdException nbEx)
		{
		}
		catch (AccessControlException acEx)
		{
		}
		catch (Exception e)
		{
		}
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
}
