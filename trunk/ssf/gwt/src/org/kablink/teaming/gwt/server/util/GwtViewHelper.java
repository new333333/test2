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

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;

import javax.portlet.PortletRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.SortField;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.BinderQuotaException;
import org.kablink.teaming.IllegalCharacterInNameException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.comparator.StringComparator;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileAttachment.FileLock;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.FileNotFoundException;
import org.kablink.teaming.fi.PathTooLongException;
import org.kablink.teaming.fi.auth.AuthException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.gwt.client.binderviews.folderdata.DescriptionHtml;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow.PrincipalInfoId;
import org.kablink.teaming.gwt.client.binderviews.folderdata.GuestInfo;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.rpc.shared.AvatarInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BinderDescriptionRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CanAddEntitiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CanAddEntitiesToBindersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClickOnTitleActionRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ColumnWidthsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.CreateFolderRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DownloadFolderAsCSVFileUrlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntityRightsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClickOnTitleActionRpcResponseData.ClickAction;
import org.kablink.teaming.gwt.client.rpc.shared.EntryTypesRpcResponseData.EntryType;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.BinderFiltersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd.MembershipFilter;
import org.kablink.teaming.gwt.client.rpc.shared.HasOtherComponentsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.IntegerRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.JspHtmlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ProfileEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SharedViewStateRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData.AccountInfo;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData.HomeInfo;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData.NetFoldersInfo;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData.QuotaInfo;
import org.kablink.teaming.gwt.client.rpc.shared.UserSharingRightsInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeJspHtmlType;
import org.kablink.teaming.gwt.client.rpc.shared.ViewFolderEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.WhoHasAccessInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.WhoHasAccessInfoRpcResponseData.AccessInfo;
import org.kablink.teaming.gwt.client.rpc.shared.ZipDownloadUrlRpcResponseData;
import org.kablink.teaming.gwt.client.util.*;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.gwt.client.util.EntityRights.ShareRight;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails.ShareInfo;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails.UserInfo;
import org.kablink.teaming.gwt.server.util.GwtPerShareInfo.PerShareInfoComparator;
import org.kablink.teaming.gwt.server.util.GwtSharedMeItem.SharedMeEntriesMapComparator;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.FilesErrors;
import org.kablink.teaming.module.file.FilesErrors.Problem;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FilesLockedByOtherUsersException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.mobiledevice.MobileDeviceModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.portlet.binder.AccessControlController;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.portletadapter.portlet.RenderRequestImpl;
import org.kablink.teaming.portletadapter.portlet.RenderResponseImpl;
import org.kablink.teaming.portletadapter.support.AdaptedPortlets;
import org.kablink.teaming.portletadapter.support.KeyNames;
import org.kablink.teaming.portletadapter.support.PortletInfo;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.security.runwith.RunWithCallback;
import org.kablink.teaming.security.runwith.RunWithTemplate;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.task.TaskHelper;
import org.kablink.teaming.task.TaskHelper.FilterType;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DateComparer;
import org.kablink.teaming.util.FileIconsHelper;
import org.kablink.teaming.util.FileLinkAction;
import org.kablink.teaming.util.GangliaMonitoring;
import org.kablink.teaming.util.IconSize;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.SearchTreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.AdminHelper;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.CloudFolderHelper;
import org.kablink.teaming.web.util.DashboardHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.EventHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.ListFolderHelper;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.Toolbar;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.ListFolderHelper.ModeType;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;
import org.kablink.util.search.Order;

import static org.kablink.util.search.Restrictions.eq;
import static org.kablink.util.search.Restrictions.in;
import static org.kablink.util.search.Restrictions.like;

/**
 * Helper methods for the GWT binder views.
 *
 * @author drfoster@novell.com
 */
public class GwtViewHelper {
	protected static Log m_logger = LogFactory.getLog(GwtViewHelper.class);
	
	// The following controls whether the My Files Storage folder will
	// appear in the Vibe UI as a normal folder or will be hidden as
	// much as possible as it is in Filr.
	//
	// See GwtViewHelper.showMyFilesStorageAsFolder() for usage.
	private static final boolean SHOW_MY_FILES_STORAGE_IN_VIBE	= true;
	
	// Attribute names used for items related to milestones.
	public static final String RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME	= "responsible_groups";
	public static final String RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME			= "responsible";
	public static final String RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME		= "responsible_teams";

	// Attribute names used to store things in the session cache.
	private static final String CACHED_VIEW_PINNED_ENTRIES_BASE = "viewPinnedEntries_";
	private static final String CACHED_VIEW_SHARED_FILES_BASE	= "viewSharedFiles_";
	
	// Base keys used to store shared view state in the session cache.
	private static final String CACHED_SHARED_VIEW_SHOW_HIDDEN_BASE		= "sharedViewShowHidden_";
	private static final String CACHED_SHARED_VIEW_SHOW_NON_HIDDEN_BASE	= "sharedViewShowNonHidden_";

	// The following control whether profiling information is tracked
	// per row and/or per column and per user (i.e., author.)
	private final static boolean PROFILE_PER_ROW	= SPropsUtil.getBoolean("gwt.profile.listview.per.row",    false);
	private final static boolean PROFILE_PER_COLUMN	= SPropsUtil.getBoolean("gwt.profile.listview.per.column", false);
	private final static boolean PROFILE_PER_USER	= SPropsUtil.getBoolean("gwt.profile.listview.per.user",   false);
	
	// The following controls whether the 'last login' is displayed in
	// the user properties dialog in the administration console.  By
	// default, it is (the value defaults to true.)
	private final static boolean SHOW_LAST_LOGIN_IN_USER_PROPERTIES	= SPropsUtil.getBoolean("show.last.login.in.user.properties", true);
	
	/*
	 * Inner class used to compare two AccessInfo's.
	 * 
	 * Two AccessInfo's are sorted by their name.
	 */
	private static class AccessInfoComparator implements Comparator<AccessInfo> {
		/**
		 * Constructor method.
		 */
		public AccessInfoComparator() {
			// Initialize the super class.
			super();
		}

		/**
		 * Compares two AccessInfo objects.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param ai1
		 * @param ai2
		 * 
		 * @return
		 */
		@Override
		public int compare(AccessInfo ai1, AccessInfo ai2) {
			return MiscUtil.safeSColatedCompare(ai1.getName(), ai2.getName());
		}
	}

	/*
	 * Inner class used to track the target binder's for entries and
	 * binders given the ID of an initial target binder.
	 * 
	 * The logic here is appropriate for copy/move operations where
	 * folder and/or entries are binder copied/moved to a target.  If
	 * that target is a user's 'My Files Storage' folder, the entries
	 * should be targeted there, but folders should be targeted to the
	 * containing workspace.  Conversely, if the target is a user's
	 * workspace, the folders should be targeted there, but entries
	 * should be targeted to the contained 'My Files Storage' folder.
	 */
	private static class CopyMoveTarget {
		private boolean	m_targetIsUserWorkspace;			// true -> The target is a user's workspace.                false -> It isn't.
		private boolean	m_targetIsMyFilesStorage;			// true -> The target is a user's My Files Storage folder.  false -> It isn't.
		private boolean m_targetMyFilesStorageDisabled;		// true -> The target is a My Files Storage folder where there user doesn't have personal storage.  false -> That's not the case.
		private boolean m_targetUserWorkspaceUnavailable;	// true -> The target is a user workspace that's unavailable for use as a target.                   false -> It's not or user workspace or it's available for use.
		private Long	m_binderTargetId;					// Target binder ID for binders being copied/moved.
		private Long	m_entryTargetId;					// Target binder ID for entries being copied/moved.

		/**
		 * Constructor method.
		 * 
		 * @param bs
		 * @param targetBinderId
		 */
		public CopyMoveTarget(AllModulesInjected bs, Long targetBinderId) {
			// Initialize the super class...
			super();

			// ...and store the initial target binder as both the
			// ...binder and entry target.
			setBinderTargetId(targetBinderId);
			setEntryTargetId( targetBinderId);
			
			// Is the initial target binder a 'My Files Storage'
			// folder?
			Binder targetBinder = bs.getBinderModule().getBinderWithoutAccessCheck(targetBinderId);
			if (BinderHelper.isBinderMyFilesStorage(targetBinder)) {
				// Yes! Then the binder target should be that folder's
				// parent workspace.
				setBinderTargetId(targetBinder.getParentBinder().getId());
				setTargetIsMyFilesStorage(true);
				setTargetMyFilesStorageDisabled(
					isMyFilesStorageDisabled(
						bs,
						((Folder) targetBinder)));
			}
			
			// No, the initial target binder isn't a 'My Files Storage'
			// folder.  Is it a user's workspace?
			else if (BinderHelper.isBinderUserWorkspace(targetBinder)) {
				// Yes!  Can we find any 'My Files Storage' folders
				// within that?
				setTargetIsUserWorkspace(true);
				User wsUser = getWorkspaceUser(bs, targetBinderId);
				Long mfId = SearchUtils.getMyFilesFolderId(bs, wsUser, false);	// false -> Don't create if not there.
				if (null != mfId) {
					// Yes!  Use it for the target for entries.
					setEntryTargetId(mfId);
					setTargetIsMyFilesStorage(
						isMyFilesStorageDisabled(
							bs,
							((Folder) bs.getBinderModule().getBinderWithoutAccessCheck(
								mfId))));
				}
				
				// This user workspace is unavailable as a target if... 
				boolean targetUserWorkspaceUnavailable = (
					(!(AdminHelper.getEffectiveAdhocFolderSetting(bs, wsUser))) ||	// ...adHoc storage is disabled for this user or...
					(Utils.checkIfFilr() &&											// ...we're running as Filr and...
						(   wsUser.isShared()                       ||				// ......this is the Guest user or...
						(!( wsUser.getIdentityInfo().isInternal())) ||				// ......this is an external user or...
						(!( wsUser.isPerson())))));									// ......this user is not a person.
				setTargetUserWorkspaceUnavailable(targetUserWorkspaceUnavailable);
			}
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isTargetMyFilesStorage()           {return m_targetIsMyFilesStorage;        }
		public boolean isTargetMyFilesStorageDisabled()   {return m_targetMyFilesStorageDisabled;  }
		public boolean isTargetUserWorkspace()            {return m_targetIsUserWorkspace;         }
		public boolean isTargetUserWorkspaceUnavailable() {return m_targetUserWorkspaceUnavailable;}
		public Long    getBinderTargetId()                {return m_binderTargetId;                }
		public Long    getEntryTargetId()                 {return m_entryTargetId;                 }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setTargetIsMyFilesStorage(        boolean targetIsMyFilesStorage)         {m_targetIsMyFilesStorage         = targetIsMyFilesStorage;        }
		public void setTargetMyFilesStorageDisabled(  boolean targetMyFilesStorageDisabled)   {m_targetMyFilesStorageDisabled   = targetMyFilesStorageDisabled;  }
		public void setTargetIsUserWorkspace(         boolean targetIsUserWorkspace)          {m_targetIsUserWorkspace          = targetIsUserWorkspace;         }
		public void setTargetUserWorkspaceUnavailable(boolean targetUserWorkspaceUnavailable) {m_targetUserWorkspaceUnavailable = targetUserWorkspaceUnavailable;}
		public void setBinderTargetId(                Long    binderTargetId)                 {m_binderTargetId                 = binderTargetId;                }
		public void setEntryTargetId(                 Long    entryTargetId)                  {m_entryTargetId                  = entryTargetId;                 }
	}

	/**
	 * Inner class that encapsulated a User with their Workspace.
	 */
	public static class UserWorkspacePair {
		private Long		m_wsId;	//
		private User		m_user;	//
		private Workspace	m_ws;	//

		/*
		 * Constructor method.
		 */
		private UserWorkspacePair(User user, Long wsId, Workspace ws) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			setUser(       user  );
			setWorkspaceId(wsId  );
			setWorkspace(  ws    );
		}

		/**
		 * Constructor method.
		 * 
		 * @param user
		 * @param wsId
		 */
		public UserWorkspacePair(User user, Long wsId) {
			// Initialize this object.
			this(user, wsId, null);
		}

		/**
		 * Constructor method.
		 * 
		 * @param user
		 * @param ws
		 */
		public UserWorkspacePair(User user, Workspace ws) {
			// Initialize this object.
			this(user, ((null == ws) ? null : ws.getId()), ws);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Long      getWorkspaceId() {return m_wsId;}
		public User      getUser()        {return m_user;}
		public Workspace getWorkspace()   {return m_ws;  }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setWorkspaceId(Long      wsId)   {m_wsId = wsId;}
		public void setUser(       User      user)   {m_user = user;}
		public void setWorkspace(  Workspace ws)     {m_ws   = ws;  }
	}
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtViewHelper() {
		// Nothing to do.
	}

	/*
	 * If there's an attribute value, constructs a ProfileAttribute for
	 * it and adds it to a ProfileEntryInfoRpcResponseData object.
	 */
	private static void addProfileAttribute(ProfileEntryInfoRpcResponseData paData, String attributeName, String attributeValue) {
		if (MiscUtil.hasString(attributeValue)) {
			paData.addProfileAttribute(attributeName, new ProfileEntryInfoRpcResponseData.ProfileAttribute(NLT.get("__" + attributeName), attributeValue));
		}
	}

	/**
	 * Creates a new folder in the given binder based on a folder
	 * definition ID and the name of the new folder.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param folderTemplateId
	 * @param folderName
	 * @param cft
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CreateFolderRpcResponseData addNewFolder(AllModulesInjected bs, HttpServletRequest request, Long binderId, Long folderTemplateId, String folderName, CloudFolderType cft) throws GwtTeamingException {
		try {
			// Allocate a response we can return.
			CreateFolderRpcResponseData reply = new CreateFolderRpcResponseData(new ArrayList<ErrorInfo>());

			try {
				// Are we to create a Cloud folder?
				if (null == cft) {
					// No!  Are we creating a new folder in a mirrored
					// or net folder?
					final BinderModule bm = bs.getBinderModule();
					Binder parentBinder = bm.getBinder(binderId);
					if (parentBinder.isMirrored() || parentBinder.isAclExternallyControlled()) {
						// Yes!  Then we check for duplicates rather
						// than allowing the back end to do it.  The
						// reason for this is that the exception thrown
						// by the back end for mirrored or net folders
						// is too generic to recognize easily as being
						// a naming conflict.  This lets the error
						// handling take the same path regardless of
						// the parent folder type.  Does a folder by the
						// given name already exist?
						String path = (parentBinder.getPathName() + "/" + folderName);
						Binder existingFolder = bm.getBinderByPathName(path);
						if (null != existingFolder) {
							// Yes!  Throw a TitleException to generate
							// the appropriate error.
							throw new TitleException(folderName);
						}
					}
					
					// Can we create the new folder?
					final Long newId = bs.getTemplateModule().addBinder(folderTemplateId, binderId, folderName, null).getId();
					if ((null != newId) && (null != bm.getBinder(newId))) {
						reply.setFolderId(  newId     );
						reply.setFolderName(folderName);
						if (!bm.getBinder(newId).isTeamMembershipInherited()) {
							//Make sure team memberships are inherited
							RunWithTemplate.runWith(
								new RunWithCallback() {
									@Override
									public Object runWith() {
										bm.setTeamMembershipInherited(newId, true);			
										return null;
									}
								},
								new WorkAreaOperation[]{WorkAreaOperation.BINDER_ADMINISTRATION},
								null);
						}
					}
				}
				
				else {
					// Yes, we're to create a Cloud folder!  Synthesize
					// a unique name for its Cloud Folder root...
					User                 owner    = GwtServerHelper.getCurrentUser();
					Long                 ownerId  = owner.getId();
					String               rootName = (cft.name() + "." + ownerId);
					int                  tries    = 0;
					ResourceDriverConfig cfRoot;
					while (true) {
						String thisTry = ((0 == tries) ? rootName : (rootName + "." + tries));
						tries += 1;
						cfRoot = CloudFolderHelper.findCloudFolderRootByName(bs, thisTry);
						if (null == cfRoot) {
							rootName = thisTry;
							break;
						}
					}
					
					// ...create its Cloud Folder root...					
					Set<Long> memberIds = new HashSet<Long>();
					memberIds.add(ownerId);
					String uncPath = GwtCloudFolderHelper.getBaseUNCPathForService(cft);
					CloudFolderHelper.createCloudFolderRoot(
						bs,
						rootName,
						uncPath,
						memberIds);
					
					// ...and if we can create the Cloud Folder itself...
					Binder cfBinder = CloudFolderHelper.createCloudFolder(bs, owner, folderName, rootName, binderId);
					if (null != cfBinder) {
						// ...return it's ID and name.
						reply.setFolderId(  cfBinder.getId());
						reply.setFolderName(folderName      );
					}
				}
			}

			catch (Exception e) {
				// No!  Add an error to the error list...
				String messageKey;
				if      (e instanceof AccessControlException)          messageKey =  "addNewFolderError.AccssControlException";
				else if (e instanceof IllegalCharacterInNameException) messageKey =  "addNewFolderError.IllegalCharacterInNameException";
				else if (e instanceof PathTooLongException)		       messageKey =  "addNewFolderError.PathTooLongException";
				else if (e instanceof TitleException)		           messageKey = ("addNewFolderError.TitleException." + (Utils.checkIfFilr() ? "filr" : "vibe"));
				else if (e instanceof WriteFilesException)             messageKey =  "addNewFolderError.WriteFilesException";
				else                                                   messageKey =  "addNewFolderError.OtherException";
				reply.addError(NLT.get(messageKey, new String[]{folderName}));
				
				// ...and log it.
				GwtLogHelper.error(m_logger, "GwtViewHelper.addNewFolder( Name:  '" + folderName + "', EXCEPTION ):  ", e);
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.addNewFolder( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Adds a quick filter, if present, to a Criteria object.
	 */
	private static void addQuickFilterToCriteria(String quickFilter, Criteria crit) {
		// Do we have a quick filter?
		if (null != quickFilter) {
			quickFilter = quickFilter.trim();
			if (0 < quickFilter.length()) {
				// Yes!  Add it to the Criteria.
				if (!(quickFilter.endsWith("*"))) {
					quickFilter += "*";
				}
				crit.add(like(Constants.TITLE_FIELD, quickFilter));
			}
		}
	}
	
	/*
	 * Builds a model Map for running a custom JSP.
	 */
	private static Map<String, Object> buildCustomJspModelMap(Map<String, Object> baseModel) {
		// Construct the base Map...
		Map<String, Object> reply = new HashMap<String, Object>();
		if (null != baseModel) {
			reply.putAll(baseModel);
		}

		// ...and return it.
		return reply;
	}
	
	/*
	 * Returns an entry map that represents no entries available.
	 */
	@SuppressWarnings("unchecked")
	private static Map buildEmptyEntryMap() {
		Map reply = new HashMap();
		reply.put(ObjectKeys.SEARCH_ENTRIES,     new ArrayList<Map>());
		reply.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(0)      );
		return reply;
	}
	
	/**
	 * Returns a FolderRowsRpcResponseData that represents no entries
	 * available.
	 * 
	 * @return
	 */
	public static FolderRowsRpcResponseData buildEmptyFolderRows(Binder binder) {
		FolderRowsRpcResponseData reply = 
			new FolderRowsRpcResponseData(
				new ArrayList<FolderRow>(),	// FolderRows.
				0,							// Start index.
				0,							// Total count.
				TotalCountType.EXACT,		// How the total row count should be interpreted.
				new ArrayList<Long>());		// Errors that occurred.
		
		if (GwtLogHelper.isDebugEnabled(m_logger)) {
			dumpFolderRowsRpcResponseData(binder, reply);
		}
		
		return reply;
	}

	/*
	 * Returns the display string to use for a file size.
	 */
	private static String buildFileSizeDisplayFromKBSize(User user, String fileSizeInKB) {
		// Trim any leading 0's from the value.
		fileSizeInKB = trimFileSize(fileSizeInKB);
		try {
			long vl = Long.parseLong(fileSizeInKB);
			fileSizeInKB = NumberFormat.getInstance(user.getLocale()).format(vl);
		}
		catch (Exception ex) {}

		if (MiscUtil.hasString(fileSizeInKB)) {
			fileSizeInKB += (" " + NLT.get("file.sizeKB"));
		}
		return fileSizeInKB;
	}
	
	private static String buildFileSizeDisplayFromKBSize(User user, long fileSizeInKB) {
		// Always use the initial form of the method.
		return buildFileSizeDisplayFromKBSize(user, String.valueOf(fileSizeInKB));
	}
	
	/*
	 * Constructs and returns a UserInfo object using a Date an a user
	 * ID.
	 */
	private static UserInfo buildFolderEntryUser(AllModulesInjected bs, HttpServletRequest request, Date hsDate, Long hsUserId) {
		// Create the UserInfo to return...
		UserInfo reply = new UserInfo();

		// ...set the Date when the user performed the action...
		reply.setDate(GwtServerHelper.getDateTimeString(hsDate, DateFormat.MEDIUM, DateFormat.SHORT));
		
		// ...and set the PrincipalInfo about how performed the action.
		reply.setPrincipalInfo(getPIFromPId(bs, request, hsUserId));
		
		// If we get here, reply refers to the UserInfo that describes
		// the user from the given HistoryStamp.  Return it.
		return reply;
	}
	
	private static UserInfo buildFolderEntryUser(AllModulesInjected bs, HttpServletRequest request, HistoryStamp hs) {
		// If we don't have a HistoryStamp...
		if (null == hs) {
			// ...there is no user.  Return null.
			return null;
		}
		
		// Always use the initial form of the method.
		return buildFolderEntryUser(bs, request, hs.getDate(), hs.getPrincipal().getId());
	}

	/*
	 * Returns a JSP file name, ensuring it has a .jsp extension.
	 */
	private static String buildJspName(String baseJsp) {
		// If we weren't given base name...
		if (null == baseJsp) {
			// ...return null.
			return null;
		}

		// If the base name only contains white space...
		baseJsp = baseJsp.trim();
		if (0 == baseJsp.length()) {
			// ...return a 0 length string.
			return baseJsp;
		}

		// If the trimmed base name already ends with .jsp...
		if (baseJsp.toLowerCase().endsWith(".jsp")) {
			// ...simply return it.
			return baseJsp;
		}

		// Otherwise, return the base name with .jsp appended.
		return (baseJsp + ".jsp");
	}
	
	/*
	 * Builds a model Map for running a report JSP.
	 */
	private static Map<String, Object> buildReportModelMap(Map<String, Object> baseModel) {
		// Construct the base Map...
		Map<String, Object> reply = new HashMap<String, Object>();
		if (null != baseModel) {
			reply.putAll(baseModel);
		}
		reply.put(WebKeys.URL_GWT_REPORT, Boolean.TRUE);

		// ...and add the default start/end dates.
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, -1);
		Date startDate = cal.getTime();
		Date endDate = new Date();
		reply.put(WebKeys.REPORT_START_DATE, startDate);
		reply.put(WebKeys.REPORT_END_DATE, endDate);

		// If we get here, reply refers to the model Map for running a
		// report.  Return it.
		return reply;
	}
	
	/*
	 * Returns an entry map that represents a List<GwtSharedMeItem>.
	 */
	@SuppressWarnings("unchecked")
	private static Map buildSearchMapFromSharedMeList(AllModulesInjected bs, List<GwtSharedMeItem> shareItems, boolean sortDescend, String sortBy) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.buildSearchMapFromSharedMeList()");
		try {
			List<Map> searchEntries = new ArrayList<Map>();
			for (GwtSharedMeItem si:  shareItems) {
				// Create an entry Map for this GwtSharedMeItem.
				Map entryMap = new HashMap();
				searchEntries.add(entryMap);
	
				// Are we processing an entry?
				DefinableEntity	entity = si.getEntity();
				Description entityDesc = entity.getDescription();
				if (null != entityDesc) {
					entryMap.put(Constants.DESC_FIELD,        entityDesc.getText());
					entryMap.put(Constants.DESC_FORMAT_FIELD, entityDesc.getFormat());
				}
				entryMap.put(Constants.DOCID_FIELD,  String.valueOf(entity.getId()));
				entryMap.put(Constants.ENTITY_FIELD, entity.getEntityType().name());
				entryMap.put(Constants.TITLE_FIELD,  entity.getTitle());
				String binderIdField;
				if (entity instanceof FolderEntry) {
					// Yes!  Can we find an attachment for this entity?
					FileAttachment fa = GwtServerHelper.getFileEntrysFileAttachment(bs, ((FolderEntry) entity), false);
					if (null != fa) {
						// Yes!  Store the information we need about
						// its file in the Map.
						entryMap.put(Constants.FILENAME_FIELD,                  fa.getFileItem().getName()               );
						entryMap.put(Constants.FILE_ID_FIELD,                   fa.getId()                               );
				        entryMap.put(Constants.FILE_TIME_FIELD,  String.valueOf(fa.getModification().getDate().getTime()));
				        entryMap.put(Constants.IS_LIBRARY_FIELD, String.valueOf(Boolean.TRUE)                            );
						entryMap.put(Constants.DOC_TYPE_FIELD,                  Constants.DOC_TYPE_ATTACHMENT            );
					}
	
			        // Store the total replies, last activity and
			        // modification date for this entry in the Map.
			        FolderEntry fe = ((FolderEntry) entity);
			        entryMap.put(Constants.TOTALREPLYCOUNT_FIELD,   String.valueOf(fe.getTotalReplyCount()));
			        entryMap.put(Constants.LASTACTIVITY_FIELD,      fe.getLastActivity()                   );
			        entryMap.put(Constants.MODIFICATION_DATE_FIELD, fe.getModificationDate()               );
					
					// Store the entry's parent binder's ID in the Map.
					binderIdField = Constants.BINDER_ID_FIELD;
				}
				
				else {
					// No, we aren't processing an entry!  It must be a
					// binder!  Store the binder's path and
					// modification date...
					Binder binder = ((Binder) entity);
					entryMap.put(Constants.ENTITY_PATH,             binder.getPathName()                                  );
			        entryMap.put(Constants.MODIFICATION_DATE_FIELD, binder.getModificationDate()                          );
			        entryMap.put(Constants.ICON_NAME_FIELD,         binder.getIconName()                                  );
			        entryMap.put(Constants.IS_HOME_DIR_FIELD,      (binder.isHomeDir() ? Constants.TRUE : Constants.FALSE));
			        
					// ...and store its parent's ID in the Map.
					binderIdField = Constants.BINDERS_PARENT_ID_FIELD;
				}
				
				// Store the binder ID in the Map.
				entryMap.put(
					binderIdField,
					String.valueOf(entity.getParentBinder().getId()));
	
				// If the entity has a family...
				String family = si.getEntityFamily();
				if (MiscUtil.hasString(family)) {
					// ...store it in the entry map.
					entryMap.put(Constants.FAMILY_FIELD, family);
				}
			}
			
			// Finally, apply the sorting to the search entries...
			searchEntries = postProcessSharedMeMap(
				bs,
				searchEntries,
				shareItems,
				sortDescend,
				sortBy);
			
			// ...and use them to construct the search results Map and
			// ...return that.
			Map reply = new HashMap();
			reply.put(ObjectKeys.SEARCH_ENTRIES,                 searchEntries        );
			reply.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Integer(searchEntries.size()));
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Constructs a ViewFileInfo from a FileAttachment known to have a
	 * filename.
	 */
	private static ViewFileInfo buildViewFileInfo(HttpServletRequest request, FolderEntry fe, FileAttachment fa) {
		// Is the entry a file entry with an attachment known to have a
		// filename?
		ViewFileInfo reply = null;
		if (null != fa) {
			// Yes!  Do we support viewing that type of file as HTML?
			String fName = fa.getFileItem().getName();
    		if (supportsViewAsHtml(fName)) {
				try {
	        		// Yes!  Generate a ViewFileInfo for it.
					reply = new ViewFileInfo();
					reply.setFileId(     fa.getId());
					reply.setEntityId(   new EntityId(fe.getParentFolder().getId(), fe.getId(), EntityId.FOLDER_ENTRY));
					reply.setFileTime(   String.valueOf(fa.getModification().getDate().getTime()));
					reply.setViewFileUrl(GwtServerHelper.getViewFileUrl(request, reply));
				}
				catch (GwtTeamingException ex) {
					// Log the error.
					GwtLogHelper.error(m_logger, "GwtViewHelper.buildViewFileInfo( Entry title:  '" + fe.getTitle() + "', EXCEPTION ):  ", ex);
					reply = null;
				}
    		}
    	}
		
		// If we get here, reply refers to the ViewFileInfo for the
		// attachment if it can be viewed as HTML or null.  Return it.
		return reply;
	}
	
	/*
	 * Constructs and returns a ViewFolderEntryInfo that wraps the
	 * given binderId/entryId pair.
	 */
	@SuppressWarnings("unchecked")
	private static ViewFolderEntryInfo buildViewFolderEntryInfo(AllModulesInjected bs, HttpServletRequest request, Long binderId, Long entryId) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.buildViewFolderEntryInfo()");
		try {
			// Create the ViewFolderEntryInfo to return...
			ViewFolderEntryInfo	reply  = new ViewFolderEntryInfo(binderId, entryId);
			
			// ...set the user's selected view style... 
			reply.setViewStyle(GwtServerHelper.getCurrentUser().getCurrentDisplayStyle());
	
			// ...if the user has a position for the dialog saved...
			UserProperties	userProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUserId());
			if (null != userProperties) {
				Map upMap = userProperties.getProperties();
				if (null != upMap) {
					String packedPosition = ((String) upMap.get(ObjectKeys.USER_PROPERTY_FOLDER_ENTRY_DLG_POSITION));
					if (MiscUtil.hasString(packedPosition)) {
						String[] position = StringUtil.unpack(packedPosition);
						if ((null != position) && (4 == position.length)) {
							// ...set it into the reply...
							reply.setX( Integer.parseInt(position[0]));
							reply.setY( Integer.parseInt(position[1]));
							reply.setCX(-1);	// Integer.parseInt(position[2]));		// Saved height and width are ignored until we support...
							reply.setCY(-1);	// Integer.parseInt(position[3]));		// ...the user actually adjusting the size of the dialog.
						}
					}
				}
			}
			
			// ...set the entry's family... 
			FolderEntry fe    = bs.getFolderModule().getEntry(binderId, entryId);
			FolderEntry feTop = fe.getTopEntry();
			if (null == feTop) {
				feTop = fe;
			}
			reply.setFamily(GwtServerHelper.getFolderEntityFamily(bs, feTop));
	
			// ...set the entry's title... 
			reply.setTitle(GwtServerHelper.getFolderEntryTitle(fe));
			
			// If we get here, reply refers to the ViewFolderEntryInfo
			// for the user to view the entry.  Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Change the entry types for a collection of entries.
	 *
	 * @param bs
	 * @param request
	 * @param defId
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData changeEntryTypes(AllModulesInjected bs, HttpServletRequest request, String defId, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
			
			// Were we given the IDs of any entries to change their
			// entry types and the entry type to change them to?
			if (MiscUtil.hasItems(entityIds) && MiscUtil.hasString(defId)) {
				// Yes!  Scan them.
				FolderModule fm = bs.getFolderModule();
				for (EntityId entityId:  entityIds) {
					// If this entity is a binder...
					if (entityId.isBinder()) {
						// ...skip it.
						continue;
					}
					
					try {
						// Can we change this entry's entry type?
						fm.changeEntryType(entityId.getEntityId(), defId);
					}
					catch (Exception e) {
						// No!  Add an error to the error list...
						String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
						String messageKey;
						if (e instanceof AccessControlException) messageKey = "changeEntryTypeError.AccssControlException";
						else                                     messageKey = "changeEntryTypeError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtViewHelper.changeEntryTypes( Entry title:  '" + entryTitle + "', EXCEPTION ):  ", e);
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.changeEntryTypes( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Extracts the ID's of the entry contributors and adds them to the
	 * contributor ID's list if they're not already there. 
	 */
	@SuppressWarnings("unchecked")
	private static void collectContributorIds(Map entryMap, List<Long> contributorIds) {
		ListUtil.addLongToListLongIfUnique(contributorIds, getLongFromMap(entryMap, Constants.CREATORID_FIELD)     );
		ListUtil.addLongToListLongIfUnique(contributorIds, getLongFromMap(entryMap, Constants.MODIFICATIONID_FIELD));
	}
	
	/*
	 * When initially built, the AssignmentInfo's in the folder rows
	 * only contain the assignee IDs.  We need to complete them with
	 * each assignee's title, ...
	 */
	private static void completeAIs(AllModulesInjected bs, HttpServletRequest request, List<FolderRow> folderRows) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.completeAIs()");
		try {
			// If we don't have any FolderRows's to complete...
			if (!(MiscUtil.hasItems(folderRows))) {
				// ..bail.
				return;
			}
	
			// Allocate List<Long>'s to track the assignees that need
			// to be completed.
			List<Long> principalIds = new ArrayList<Long>();
			List<Long> teamIds      = new ArrayList<Long>();
	
			// Scan the List<FolderRow>'s.
			for (FolderRow fr:  folderRows) {
				// Scan this FolderRow's individual assignees tracking
				// each unique ID.
				for (AssignmentInfo ai:  getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique((ai.getAssigneeType().isTeam() ? teamIds : principalIds), ai.getId());
				}
				for (AssignmentInfo ai:  getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique((ai.getAssigneeType().isTeam() ? teamIds : principalIds), ai.getId());
				}
				for (AssignmentInfo ai:  getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique((ai.getAssigneeType().isTeam() ? teamIds : principalIds), ai.getId());
				}
				
				// Scan this FolderRow's group assignees tracking each
				// unique ID.
				for (AssignmentInfo ai:  getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
				}
				for (AssignmentInfo ai:  getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
				}
				for (AssignmentInfo ai:  getAIListFromFR(fr, RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
				}
				
				// Scan this FolderRow's team assignees tracking each
				// unique ID.
				for (AssignmentInfo ai:  getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique(teamIds, ai.getId());
				}
				for (AssignmentInfo ai:  getAIListFromFR(fr, EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique(teamIds, ai.getId());
				}
				for (AssignmentInfo ai:  getAIListFromFR(fr, RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME)) {
					ListUtil.addLongToListLongIfUnique(teamIds, ai.getId());
				}
				
				// Scan this FolderRow's shared by/with's tracking each
				// unique ID.
				for (AssignmentInfo ai:  getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_BY)) {
					ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
				}
				for (AssignmentInfo ai:  getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_WITH)) {
					ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
				}
				
				// Scan this FolderRow's team memberships tracking each unique ID.
				for (AssignmentInfo ai:  getAIListFromFR(fr, FolderColumn.COLUMN_TEAM_MEMBERS)) {
					ListUtil.addLongToListLongIfUnique(principalIds, ai.getId());
				}
			}
	
			// Construct Maps, mapping the IDs to their titles,
			// membership counts, ...
			Map<Long, String>			avatarUrls        = new HashMap<Long, String>();
			Map<Long, String>			principalEMAs     = new HashMap<Long, String>();
			Map<Long, String>			principalTitles   = new HashMap<Long, String>();
			Map<Long, Integer>			groupCounts       = new HashMap<Long, Integer>();
			Map<Long, GwtPresenceInfo>	userPresence      = new HashMap<Long, GwtPresenceInfo>();
			Map<Long, Boolean>			userExternal      = new HashMap<Long, Boolean>();
			Map<Long, Long>				presenceUserWSIds = new HashMap<Long, Long>();
			Map<Long, String>			teamTitles        = new HashMap<Long, String>();
			Map<Long, Integer>			teamCounts        = new HashMap<Long, Integer>();
			GwtEventHelper.readEventStuffFromDB(
				// Uses these...
				bs,
				request,
				principalIds,
				teamIds,
	
				// ...to complete these.
				principalEMAs,
				principalTitles,
				groupCounts,
				userPresence,
				userExternal,
				presenceUserWSIds,
				
				teamTitles,
				teamCounts,
				
				avatarUrls);
	
			// Scan the List<FolderRow>'s again...
			for (FolderRow fr:  folderRows) {
				// ...this time, fixing the assignee lists.
				fixupAIUsers(      getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             principalTitles, principalEMAs, userPresence, userExternal, presenceUserWSIds, avatarUrls);
				fixupAIGroups(     getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             principalTitles, groupCounts                                                             );
				fixupAITeams(      getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             teamTitles,      teamCounts                                                              );
				fixupAIUsers(      getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        principalTitles, principalEMAs, userPresence, userExternal, presenceUserWSIds, avatarUrls);
				fixupAIGroups(     getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        principalTitles, groupCounts                                                             );
				fixupAITeams(      getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        teamTitles,      teamCounts                                                              );
				fixupAIUsers(      getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME),                  principalTitles, principalEMAs, userPresence, userExternal, presenceUserWSIds, avatarUrls);
				fixupAIGroups(     getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME),                  principalTitles, groupCounts                                                             );
				fixupAITeams(      getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME),                  teamTitles,      teamCounts                                                              );
				
				fixupAIGroups(     getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME),      principalTitles, groupCounts                                                             );
				fixupAIGroups(     getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME), principalTitles, groupCounts                                                             );
				fixupAIGroups(     getAIListFromFR(fr, RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME),           principalTitles, groupCounts                                                             );
				
				fixupAITeams(      getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME),       teamTitles,      teamCounts                                                              );
				fixupAITeams(      getAIListFromFR(fr, EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME),  teamTitles,      teamCounts                                                              );
				fixupAITeams(      getAIListFromFR(fr, RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME),            teamTitles,      teamCounts                                                              );
				
				fixupAIUsers(      getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_BY),                         principalTitles, principalEMAs, userPresence, userExternal, presenceUserWSIds, avatarUrls);
				fixupAIUsers(      getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_WITH),                       principalTitles, principalEMAs, userPresence, userExternal, presenceUserWSIds, avatarUrls);
				fixupAIGroups(     getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_WITH),                       principalTitles, groupCounts                                                             );
				fixupAIPublics(    getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_BY)                                                                                                                   );
				fixupAIPublics(    getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_WITH)                                                                                                                 );
				fixupAIPublicLinks(getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_BY)                                                                                                                   );
				fixupAIPublicLinks(getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_WITH)                                                                                                                 );
				fixupAITeams(      getAIListFromFR(fr, FolderColumn.COLUMN_SHARE_SHARED_WITH),                       teamTitles,      teamCounts                                                              );
				
				fixupAIUsers(      getAIListFromFR(fr, FolderColumn.COLUMN_TEAM_MEMBERS),                            principalTitles, principalEMAs, userPresence, userExternal, presenceUserWSIds, avatarUrls);
				fixupAIGroups(     getAIListFromFR(fr, FolderColumn.COLUMN_TEAM_MEMBERS),                            principalTitles, groupCounts                                                             );
			}		
	
			// Finally, one last scan through the List<FolderRow>'s...
			Comparator<AssignmentInfo> comparator = new GwtServerHelper.AssignmentInfoComparator(true);
			for (FolderRow fr:  folderRows) {
				// ...this time, to sort the assignee lists.
				Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME),             comparator);
				Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME),        comparator);
				Collections.sort(getAIListFromFR(fr, RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME),                  comparator);
				
				Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME),      comparator);
				Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME), comparator);
				Collections.sort(getAIListFromFR(fr, RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME),           comparator);
				
				Collections.sort(getAIListFromFR(fr, TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME),       comparator);
				Collections.sort(getAIListFromFR(fr, EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME),  comparator);
				Collections.sort(getAIListFromFR(fr, RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME),            comparator);
				
				Collections.sort(getAIListFromFR(fr, FolderColumn.COLUMN_TEAM_MEMBERS),                            comparator);
			}
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * When initially built, the CommentsInfo's contained false for
	 * whether comments are disabled on FolderEntry's.  We need to
	 * complete these values correctly.
	 */
	private static void completeCIs(AllModulesInjected bs, HttpServletRequest request, List<FolderRow> folderRows) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.completeCIs()");
		try {
			// If we don't have any FolderRows's to complete...
			if (!(MiscUtil.hasItems(folderRows))) {
				// ..bail.
				return;
			}
	
			// Allocate a map to track the comments that need to be
			// completed.
			Map<Long, Map<String, CommentsInfo>> ciMap = new HashMap<Long, Map<String, CommentsInfo>>();
	
			// Scan the List<FolderRow>'s.
			for (FolderRow fr:  folderRows) {
				// Does this row correspond to a FolderEntry?
				EntityId frId = fr.getEntityId(); 
				if (frId.isFolderEntry()) {
					// Yes!  Does it have a comments map?
					Map<String, CommentsInfo> ciMapFromRow = fr.getRowCommentsMap();
					if (MiscUtil.hasItems(ciMapFromRow)) {
						// Yes!  Track it.
						ciMap.put(frId.getEntityId(), ciMapFromRow);
					}
				}
			}

			// Are we tracking any comments to that need to be
			// completed?
			if (!(ciMap.isEmpty())) {
				// Yes!  Can we access any of the corresponding
				// FolderEntry's?
				SortedSet<FolderEntry> entries = bs.getFolderModule().getEntries(ciMap.keySet());
				if (!(entries.isEmpty())) {
					// Yes!  Scan them...
					for (FolderEntry fe:  entries) {
						// ...setting their CommentInfo's
						// ...appropriately.
						Map<String, CommentsInfo> ciMapFromRow = ciMap.get(fe.getId());
						if (MiscUtil.hasItems(ciMapFromRow)) {
							boolean commentsDisabled = (!(GwtServerHelper.canEntryHaveAComment(fe)));
							for (String colKey:  ciMapFromRow.keySet()) {
								CommentsInfo ci = ciMapFromRow.get(colKey);
								if (null != ci) {
									ci.setCommentsDisabled(commentsDisabled);
								}
							}
						}
					}
				}
			}
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * When initially built, the EntryTitleInfo's in the rows don't
	 * contain the user's rights to add to any nested folders.  We need
	 * to add that information to support drag and drop into nested
	 * folders.
	 */
	private static void completeNFRights(AllModulesInjected bs, HttpServletRequest request, List<FolderRow> frList) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.completeNFRights()");
		try {
			// If we don't have any FolderRow's to complete...
			if (!(MiscUtil.hasItems(frList))) {
				// ...bail.
				return;
			}
	
			Map<Long, EntryTitleInfo> etiMap = new HashMap<Long, EntryTitleInfo>();
			SimpleProfiler.start("GwtViewHelper.completeNFRights(Collect folder IDs)");
			try {
				// Scan the rows.
				for (FolderRow fr:  frList) {
					// Is this row a nested folder?
					EntityId eid = fr.getEntityId();
					if (eid.isFolder()) {
						// Yes!  Can we find its EntryTitleInfo?
						EntryTitleInfo ft = fr.getRowEntryTitlesMap().get("title");
						if (null != ft) {
							// Yes!  Map the folder's ID to its
							// EntryTitleInfo.
							etiMap.put(eid.getEntityId(), ft);
						}
					}
				}
			}
			
			finally {
				SimpleProfiler.stop("GwtViewHelper.completeNFRights(Collect folder IDs)");
			}
			
			// If we don't have any folder's whose rights need to be
			// completed...
			if (!(MiscUtil.hasItems(etiMap.keySet()))) {
				// ...bail.
				return;
			}
	
			try {
				SimpleProfiler.start("GwtViewHelper.completeNFRights(Complete rights)");
				try {
					// Resolve the folder IDs...
					BinderModule bm      = bs.getBinderModule();
					FolderModule fm      = bs.getFolderModule();
					Set<Folder>  folders = fm.getFolders(etiMap.keySet());
					
					// ...scan the folders... 
					for (Folder folder:  folders) {
						// ...and add information about the user's
						// ...rights to add to them to the folder's
						// ...EntryTitleInfo.
						boolean canAddEntries = fm.testAccess((folder), FolderOperation.addEntry );
						boolean canAddFolders = bm.testAccess( folder,  BinderOperation.addFolder);
						CanAddEntitiesRpcResponseData folderRights = new CanAddEntitiesRpcResponseData(canAddEntries, canAddFolders);
						etiMap.get(folder.getId()).setCanAddFolderEntities(folderRights);
					}
				}
				
				finally {
					SimpleProfiler.stop("GwtViewHelper.completeNFRights(Complete rights)");
				}
			}
			
			catch (Exception ex) {/* Ignored. */}
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * When initially built, the AssignmentInfo's in the
	 * List<ShareInfo>'s in the FolderEntryDetails only contain the
	 * assignee IDs.  We need to complete them with each assignee's
	 * title, ...
	 */
	private static void completeShareAIs(AllModulesInjected bs, HttpServletRequest request, FolderEntryDetails fed) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.completeShareAIs()");
		try {
			// If we don't have any shares to complete...
			if (!(fed.hasShares())) {
				// ..bail.
				return;
			}

			// Condense all the shares into a single list so we can
			// complete them all at the same time.
			List<ShareInfo> shares = new ArrayList<ShareInfo>();
			if (fed.hasShareBys())   shares.addAll(fed.getSharedByItems());
			if (fed.hasShareWiths()) shares.addAll(fed.getSharedWithItems());
	
			// Allocate List<Long>'s to track the assignees that need
			// to be completed.
			List<Long> principalIds = new ArrayList<Long>();
			List<Long> teamIds      = new ArrayList<Long>();
	
			// Extract the assignee IDs from the List<ShareInfo>.
			for (ShareInfo si:  shares) {
				AssignmentInfo ai = si.getUser();
				ListUtil.addLongToListLongIfUnique((ai.getAssigneeType().isTeam() ? teamIds : principalIds), ai.getId());
			}
	
			// Construct Maps, mapping the IDs to their titles,
			// membership counts, ...
			Map<Long, String>			avatarUrls        = new HashMap<Long, String>();
			Map<Long, String>			principalEMAs     = new HashMap<Long, String>();
			Map<Long, String>			principalTitles   = new HashMap<Long, String>();
			Map<Long, Integer>			groupCounts       = new HashMap<Long, Integer>();
			Map<Long, GwtPresenceInfo>	userPresence      = new HashMap<Long, GwtPresenceInfo>();
			Map<Long, Boolean>			userExternal      = new HashMap<Long, Boolean>();
			Map<Long, Long>				presenceUserWSIds = new HashMap<Long, Long>();
			Map<Long, String>			teamTitles        = new HashMap<Long, String>();
			Map<Long, Integer>			teamCounts        = new HashMap<Long, Integer>();
			GwtEventHelper.readEventStuffFromDB(
				// Uses these...
				bs,
				request,
				principalIds,
				teamIds,
	
				// ...to complete these.
				principalEMAs,
				principalTitles,
				groupCounts,
				userPresence,
				userExternal,
				presenceUserWSIds,
				
				teamTitles,
				teamCounts,
				
				avatarUrls);
	
			// Scan the List<ShareInfo> again...
			for (ShareInfo si:  shares) {
				// ...this time, completing the information in each
				// ...one's AssignmentInfo.
				AssignmentInfo ai = si.getUser();
				switch (ai.getAssigneeType()) {
				case INDIVIDUAL:
					if (GwtEventHelper.setAssignmentInfoTitle(           ai, principalTitles )) {
						GwtEventHelper.setAssignmentInfoPresence(        ai, userPresence     );
						GwtEventHelper.setAssignmentInfoExternal(        ai, userExternal     );
						GwtEventHelper.setAssignmentInfoPresenceUserWSId(ai, presenceUserWSIds);
						GwtEventHelper.setAssignmentInfoAvatarUrl(       ai, avatarUrls       );
						GwtEventHelper.setAssignmentInfoHover(           ai, principalTitles  );
					}
					break;
					
				case GROUP:
					if (GwtEventHelper.setAssignmentInfoTitle(  ai, principalTitles)) {
						GwtEventHelper.setAssignmentInfoMembers(ai, groupCounts     );
						GwtEventHelper.setAssignmentInfoHover(  ai, principalTitles );
						ai.setPresenceDude("pics/group_20.png");
					}
					break;
					
				case TEAM:
					if (GwtEventHelper.setAssignmentInfoTitle(  ai, teamTitles)) {
						GwtEventHelper.setAssignmentInfoMembers(ai, teamCounts );
						GwtEventHelper.setAssignmentInfoHover(  ai, teamTitles );
						ai.setPresenceDude("pics/team_16.png");
					}
					break;
					
				case PUBLIC:
					ai.setTitle(NLT.get("share.recipientType.title.public"));
					ai.setHover(NLT.get("share.recipientType.hover.public"));
					break;
					
				case PUBLIC_LINK:
					ai.setTitle(NLT.get("share.recipientType.title.productLink"));
					ai.setHover(NLT.get("share.recipientType.hover.productLink"));
					break;
				}
				
			}
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Scans the List<FolderRow> and completes user information
	 * including the PrincipalInfo's and MobileDevicesInfo's for each
	 * row.
	 */
	@SuppressWarnings("unchecked")
	private static void completeUserInfo(AllModulesInjected bs, HttpServletRequest request, List<FolderColumn> fcList, List<FolderRow> frList, boolean isProfilesRootWS, boolean isManageUsers, boolean isFilr) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.completeUserInfo()");
		try {
			// If we don't have any FolderRow's to complete...
			if (!(MiscUtil.hasItems(frList))) {
				// ..bail.
				return;
			}
	
			List<Long> principalIds = new ArrayList<Long>();
			SimpleProfiler.start("GwtViewHelper.completeUserInfo(Collect principal IDs)");
			try {
				// Collect the principal IDs of the rows from the
				// List<FolderRow>.
				for (FolderRow fr:  frList) {
					Map<String, PrincipalInfoId> pIdsMap = fr.getRowPrincipalIdsMap();
					for (String k:  pIdsMap.keySet()) {
						ListUtil.addLongToListLongIfUnique(principalIds, pIdsMap.get(k).getId());
					}
				}
			}
			
			finally {
				SimpleProfiler.stop("GwtViewHelper.completeUserInfo(Collect principal IDs)");
			}
			
			try {
				SimpleProfiler.start("GwtViewHelper.completeUserInfo(Fixup principals)");
				try {
					// Build a List<PrincipalInfo> from the List<Long> of
					// principal IDs.
					List<Principal>			principals = ResolveIds.getPrincipals(principalIds, false);
					List<PrincipalInfo>     piList     =                  new ArrayList<PrincipalInfo>();
					List<MobileDevicesInfo> mdList     = (isManageUsers ? new ArrayList<MobileDevicesInfo>() : null);
					List<UserWorkspacePair> uwsPairs   = getUserWorkspacePairs(principalIds, principals, false);
					getUserInfoFromPIds(bs, request, piList, mdList, uwsPairs);
					for (PrincipalInfo pi:  piList) {
						boolean includeIt = 							// Include it in...
							((!isFilr)                              ||	// ...Vibe...
							isManageUsers                           ||	// ...or the administration console's Manage User's page...
							(isProfilesRootWS && pi.isUserPerson()) ||	// ...or if it's a person in the View User's page...
							(!isProfilesRootWS));						// ...or it is's any non-user's page.
						if (includeIt) {
							continue;
						}
						piList.remove(pi);
					}
					
					// Scan the List<FolderRow> again.
					for (FolderRow fr:  frList) {
						// Scan this row's PrincipalInfoId's.
						Map<String, PrincipalInfo>   piMap   = fr.getRowPrincipalsMap();
						Map<String, PrincipalInfoId> pIdsMap = fr.getRowPrincipalIdsMap();
						for (String k:  pIdsMap.keySet()) {
							// Scan the List<PrincipalInfo> map.
							PrincipalInfoId pId = pIdsMap.get(k);
							for (PrincipalInfo pi:  piList) {
								// Is this the PrincipalInfo for the
								// PrincipalInfoId?
								if (pId.getId().equals(pi.getId())) {
									// Yes!  Add it to the principals map
									// and break out of the inner loop.
									piMap.put(k, pi);
									break;
								}
							}
						}
						
						// Once we've processed all the
						// PrincipalInfoId's in the row, we clear the
						// map of them since we won't need them again. 
						pIdsMap.clear();

						// Are we completing the user information for
						// the manage users page?
						if (isManageUsers) {
							// Yes!  Scan the row's MobileDevicesInfo
							// map.
							Map<String, MobileDevicesInfo> mdMap = fr.getRowMobileDevicesMap();
							for (String k: mdMap.keySet()) {
								// Scan the List<MobileDevicesInfo> we
								// read.
								MobileDevicesInfo mdi       = mdMap.get(k);
								Long              mdiUserId = mdi.getUserId();
								for (MobileDevicesInfo mdiScan: mdList) {
									// Is this the MobileDevicesInfo
									// for the one from row?
									if (mdiScan.getUserId().equals(mdiUserId)) {
										// Yes!  Transfer the count and
										// stop looking.
										mdi.setMobileDevicesCount(mdiScan.getMobileDevicesCount());
										break;
									}
								}
							}

							// Can we find the User associated with
							// this row?
							Long rowUserId = fr.getEntityId().getEntityId();
							User rowUser   = null;
							for (UserWorkspacePair uwsPair:  uwsPairs) {
								User uwsUser = uwsPair.getUser();
								if (uwsUser.getId().equals(rowUserId)) {
									rowUser = uwsUser;
									break;
								}
							}
							String adminRights = NLT.get("general.NA");
							if (null != rowUser) {
								// Yes!  Get the display string for
								// their admin rights.
								adminRights = getPrincipalAdminRightsString(bs, rowUser);
							}
							
							// If we can find the columns for them...
							FolderColumn rightsCol = FolderColumn.getFolderColumnByEleName(fcList, FolderColumn.COLUMN_ADMIN_RIGHTS);
							if (null != rightsCol) {
								// ...store the administrator rights
								// ...settings in the row.
								fr.setColumnValue(rightsCol, adminRights);
							}
						}
						
						// Does this row have an PrincipalAdminType's that
						// need fixing?
						Map<String, PrincipalAdminType> patMap = fr.getRowPrincipalAdminTypesMap();
						if (!(patMap.isEmpty())) {
							// Yes!  Can we find the row's Principal in
							// the Principal's we read above?
							Long      rowEntityId  = fr.getEntityId().getEntityId();
							Principal rowPrincipal = null;
							for (Principal p:  principals) {
								if (p.getId().equals(rowEntityId)) {
									rowPrincipal = p;
									break;
								}
							}
							if (null != rowPrincipal) {
								// Yes!  Scan the PrincipalAdminTYpe's
								// (should only be one)...
								for (String key:  patMap.keySet()) {
									// ...ensuring each has the correct
									// ...admin flag.
									boolean admin;
									PrincipalAdminType pat = patMap.get(key);
									if (pat.getPrincipalType().isUser())
									     admin = bs.getAdminModule().testUserAccess(((User) rowPrincipal), AdminOperation.manageFunction);
									else admin = AdminHelper.isSiteAdminMember(rowEntityId);
									pat.setAdmin(admin);
								}
							}
						}
					}
				}
				
				finally {
					SimpleProfiler.stop("GwtViewHelper.completeUserInfo(Fixup principals)");
				}
			}
			
			catch (Exception ex) {/* Ignored. */}
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Converts a List<ShareItem> into a List<GwtShareMeItem>
	 * representing the 'Shared by Me' items.
	 */
	private static List<GwtSharedMeItem> convertItemListToByMeList(AllModulesInjected bs, HttpServletRequest request, List<ShareItem> shareItems, Long userId, String sortBy, boolean sortDescend) throws Exception {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.convertItemListToByMeList()");
		try {
			// Allocate a List<GwtSharedMeItem> to hold the converted
			// List<ShareItem> information.
			List<GwtSharedMeItem> reply = new ArrayList<GwtSharedMeItem>();
			
			// If we don't have any share items to convert...
			if (!(MiscUtil.hasItems(shareItems))) {
				// ...return the empty reply list.
				return reply;
			}
			
			// Get the state of the shared view.
			boolean         sharedFiles;
			SharedViewState svs;
			SimpleProfiler.start("GwtViewHelper.convertItemListToByMeList(Setup)");
			try {
				try                  {svs = getSharedViewState(bs, request, CollectionType.SHARED_BY_ME).getSharedViewState();}
				catch (Exception ex) {svs = new SharedViewState(true, false);                                                 }
				sharedFiles = getUserViewSharedFiles(request, CollectionType.SHARED_BY_ME);
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.convertItemListToByMeList(Setup)");
			}
	
			// Scan the share items.
			SharingModule	      sm             = bs.getSharingModule();
			List<DefinableEntity> sharedEntities = new ArrayList<DefinableEntity>();
			sm.validateShareItems(shareItems, sharedEntities);
			SimpleProfiler.start("GwtViewHelper.convertItemListToByMeList(Processing shares)");
			try {
				for (ShareItem si:  shareItems) {
					// Is the shared entity still accessible?
					EntityIdentifier eid = si.getSharedEntityIdentifier();
					DefinableEntity	siEntity = sm.findSharedEntityInList(sharedEntities, eid); 
					if ((null == siEntity) || GwtDeleteHelper.isEntityPreDeleted(siEntity)) {
						// No!  We'll simply skip it.  This can happen if
						// somebody revokes the user's right to an item they
						// previously shared.
						continue;
					}
		
					// Are we showing everything?
					boolean showHidden     = svs.isShowHidden();
					boolean showNonHidden  = svs.isShowNonHidden();
					boolean isEntityHidden = sm.isSharedEntityHidden(siEntity, false);	// false -> Check hidden in Shared by Me list.
					if ((!showHidden) || (!showNonHidden)) {
						// No!  Are we supposed to show entities in this hide
						// state?
						boolean showIt = ((isEntityHidden && showHidden) || ((!isEntityHidden) && showNonHidden));
						if (!showIt) {
							// Yes!  Skip it.
							continue;
						}
					}
		
					// Is this entity other than a file entity while we're only
					// showing files in the collection?
					String siEntityFamily = GwtServerHelper.getFolderEntityFamily(bs, siEntity);
					if (sharedFiles && (!(GwtServerHelper.isFamilyFile(siEntityFamily)))) {
						// Yes!  Skip it.
						continue;
					}
		
					// Is this an additional share item on an existing
					// GwtSharedMeItem?
					GwtSharedMeItem meItem = GwtSharedMeItem.findShareMeInList(siEntity, reply);
					if (null == meItem) {
						// No!  Create a new GwtSharedMeItem for it...
						meItem = new GwtSharedMeItem(
							isEntityHidden,		// true -> The entity is hidden.  false -> It isn't.
							siEntity,			// The entity being shared.
							siEntityFamily);	// The family of the entity.
						
						// ...and add it to the reply list.
						reply.add(meItem);
					}
		
					// Add information about this share item as a
					String recipientTitle;
					if (si.getIsPartOfPublicShare())
					     recipientTitle = NLT.get("share.recipientType.title.public");
					else recipientTitle = getRecipientTitle(bs, si.getRecipientType(), si.getRecipientId());
					meItem.addPerShareInfo(
						si,
						recipientTitle,
						getRecipientTitle(bs, RecipientType.user, si.getSharerId()));
				}
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.convertItemListToByMeList(Processing shares)");
			}
	
			// Sort the GwtPerShareInfo's attached to the
			// List<GwtSharedMeItem> we're going to return.
			SimpleProfiler.start("GwtViewHelper.convertItemListToByMeList(Sorting shares)");
			try {
				PerShareInfoComparator.sortPerShareInfoLists(bs, CollectionType.SHARED_BY_ME, reply, sortBy, sortDescend);
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.convertItemListToByMeList(Sorting shares)");
			}
			
			// If we get here, reply refers to the List<GwtSharedMeItem>
			// built from condensing the List<ShareItem>.  Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Converts a List<ShareItem> into a List<GwtShareMeItem>
	 * representing the 'Shared with Me' items.
	 */
	private static List<GwtSharedMeItem> convertItemListToWithMeList(AllModulesInjected bs, HttpServletRequest request, List<ShareItem> shareItems, Long userId, List<Long> teams, List<Long> groups, String sortBy, boolean sortDescend, boolean isPublic) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.convertItemListToWithMeList()");
		try {
			// Allocate a List<GwtSharedMeItem> to hold the converted
			// List<ShareItem> information.
			List<GwtSharedMeItem> reply = new ArrayList<GwtSharedMeItem>();
			
			// If we don't have any share items to convert...
			if (!(MiscUtil.hasItems(shareItems))) {
				// ...return the empty reply list.
				return reply;
			}
	
			boolean			sharedFiles;
			CollectionType	ct;
			SharedViewState	svs;
			SimpleProfiler.start("GwtViewHelper.convertItemListToWithMeList(Setup)");
			try {
				// Get the state of the shared view.
				ct = (isPublic ? CollectionType.SHARED_PUBLIC : CollectionType.SHARED_WITH_ME);
				try                  {svs = getSharedViewState(bs, request, ct).getSharedViewState();}
				catch (Exception ex) {svs = new SharedViewState(true, false);                        }
				sharedFiles = getUserViewSharedFiles(request, ct);
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.convertItemListToWithMeList(Setup)");
			}
	
			// Scan the share items.
			SharingModule	      sm             = bs.getSharingModule();
			List<DefinableEntity> sharedEntities = new ArrayList<DefinableEntity>();
			sm.validateShareItems(shareItems, sharedEntities);
			SimpleProfiler.start("GwtViewHelper.convertItemListToWithMeList(Processing shares)");
			try {
				for (ShareItem si:  shareItems) {
					// Are we looking for public shares and this isn't
					// public or vice versa?
					if (isPublic != si.getIsPartOfPublicShare()) {
						// Yes!  Skip it.
						continue;
					}
		
					// Did user somehow create a non-public share of the
					// item with themselves?
					if ((!isPublic) && si.getSharerId().equals(userId)) {
						// Yes!  Skip it.
						continue;
					}
					
					// Is this share item's entity still accessible?
					EntityIdentifier eid = si.getSharedEntityIdentifier();
					DefinableEntity	siEntity = sm.findSharedEntityInList(sharedEntities, eid); 
					if ((null == siEntity) || GwtDeleteHelper.isEntityPreDeleted(siEntity)) {
						// No!  Skip it.
						continue;
					}
		
					// Are we showing everything?
					boolean showHidden     = svs.isShowHidden();
					boolean showNonHidden  = svs.isShowNonHidden();
					boolean isEntityHidden = sm.isSharedEntityHidden(siEntity, true);	// true -> Check hidden in Shared with Me list.
					if ((!showHidden) || (!showNonHidden)) {
						// No!  Are we supposed to show entities in this
						// hide state?
						boolean showIt = ((isEntityHidden && showHidden) || ((!isEntityHidden) && showNonHidden));
						if (!showIt) {
							// Yes!  Skip it.
							continue;
						}
					}
		
					// Is this entity other than a file entity while we're
					// only showing files in the collection?
					String siEntityFamily = GwtServerHelper.getFolderEntityFamily(bs, siEntity);
					if (sharedFiles && (!(GwtServerHelper.isFamilyFile(siEntityFamily)))) {
						// Yes!  Skip it.
						continue;
					}
					
					// Create a new GwtSharedMeItem?
					GwtSharedMeItem meItem = GwtSharedMeItem.findShareMeInList(siEntity, reply);
					boolean newMeItem = (null == meItem);
					if (newMeItem) {
						meItem = new GwtSharedMeItem(
							isEntityHidden,		// true -> The entity is hidden.  false -> It isn't.
							siEntity,			// The entity being shared.
							siEntityFamily);	// The family of the entity.
					}
		
					// Is this share directed to this user, one of the
					// user's groups or one of the user's teams?  Well use
					// the item (break) if it does and skip it (continue)
					// if it doesn't.
					Long rId = si.getRecipientId();
					switch (si.getRecipientType()) {
					case user:   if (userId.equals(  rId)) break; continue;	// Checks the user...
					case group:  if (groups.contains(rId)) break; continue;	// ...check the user's groups...
					case team:   if (teams.contains( rId)) break; continue;	// ...and check the user's teams.
					default:                                      continue;
					}
							
					// The share recipient belongs with this user!  Add the
					// information about it to the GwtSharedMeItem.
					String recipientTitle;
					if (si.getIsPartOfPublicShare())
					     recipientTitle = NLT.get("share.recipientType.title.public");
					else recipientTitle = getRecipientTitle(bs, si.getRecipientType(), si.getRecipientId());
					meItem.addPerShareInfo(
						si,
						recipientTitle,
						getRecipientTitle(bs, RecipientType.user, si.getSharerId()));
		
					// Has the GwtSharedMeItem actually been shared with
					// the current user?
					if (meItem.isShared() && newMeItem) {
						// Yes!  Add it to the reply
						// List<GwtSharedMeItem> we're building to return.
						reply.add(meItem);
					}
				}
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.convertItemListToWithMeList(Processing shares)");
			}
			
			// Sort the GwtPerShareInfo's attached to the
			// List<GwtSharedMeItem> we're going to return.
			SimpleProfiler.start("GwtViewHelper.convertItemListToWithMeList(Sorting shares)");
			try {
				PerShareInfoComparator.sortPerShareInfoLists(bs, ct, reply, sortBy, sortDescend);
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.convertItemListToWithMeList(Sorting shares)");
			}
			
			// If we get here, reply refers to the
			// List<GwtSharedMeItem> built from condensing the
			// List<ShareItem>.  Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Copies the entries.
	 * 
	 * @param bs
	 * @param request
	 * @param targetFolderId
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static ErrorListRpcResponseData copyEntries(AllModulesInjected bs, HttpServletRequest request, Long targetFolderId, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
			
			// Were we given the IDs of any entries to copy?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Decide on the actual target for the copy...
				CopyMoveTarget cmt = new CopyMoveTarget(bs, targetFolderId);
				if (cmt.isTargetMyFilesStorage() && cmt.isTargetMyFilesStorageDisabled()) {
					reply.addError(NLT.get("copyEntryError.cantTargetMFS"));
					return reply;
				}
				
				// ...and scan the entries to be copied.
				BinderModule bm = bs.getBinderModule();
				FolderModule fm = bs.getFolderModule();
				for (EntityId entityId:  entityIds) {
					try {
						// Can we copy this entity?
						if (entityId.isBinder()) {
							if (cmt.isTargetUserWorkspace() && cmt.isTargetUserWorkspaceUnavailable()) {
								String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
								reply.addError(NLT.get("copyEntryError.cantTargetUserWS", new String[]{entryTitle}));
							}
							else if (BinderHelper.isBinderHomeFolder(bm.getBinder(entityId.getEntityId()))) {
								String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
								reply.addError(NLT.get("copyEntryError.cantCopyHome", new String[]{entryTitle}));
							}
							else {
								bm.copyBinder(entityId.getEntityId(), cmt.getBinderTargetId(), true, null);
							}
						}
						else {
							// Bug: 859044 (pmh) - when copying, start
							//      the workflow at the same state.
							Map options = new HashMap();
							options.put(ObjectKeys.WORKFLOW_START_WORKFLOW, ObjectKeys.WORKFLOW_START_WORKFLOW_COPY);
							fm.copyEntry(entityId.getBinderId(), entityId.getEntityId(), cmt.getEntryTargetId(), null, options);
						}
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
						String messageKey;
						String messageText;
						NotSupportedException nse = null;
						if (e instanceof WriteFilesException) {
							String details = ((WriteFilesException) e).getLocalizedMessage();
							if (MiscUtil.hasString(details)) {
								messageKey  = null;
								messageText = details;
							}
							else {
								messageKey  = "copyEntryError.WriteFilesException";
								messageText = null;
							}
						}
						
						else if (e instanceof AccessControlException) {messageKey = "copyEntryError.AccssControlException"; messageText = null;                                   }
						else if (e instanceof BinderQuotaException)   {messageKey = "copyEntryError.BinderQuotaException";  messageText = null;                                   }
						else if (e instanceof IllegalStateException)  {messageKey = "copyEntryError.IllegalStateException"; messageText = null;                                   }
						else if (e instanceof NotSupportedException)  {messageKey = "copyEntryError.NotSupportedException"; messageText = null; nse = ((NotSupportedException) e);}
						else if (e instanceof TitleException)         {messageKey = "copyEntryError.TitleException";        messageText = null;                                   }
						else if (e instanceof UncheckedIOException)   {messageKey = "copyEntryError.UncheckedIOException";  messageText = null;                                   }
						else                                          {messageKey = "copyEntryError.OtherException";        messageText = null;                                   }
						
						String[] messageArgs;
						if (null == nse) {
							messageArgs = new String[]{entryTitle};
						}
						else {
							String messagePatch;
							String messageCode = nse.getErrorCode();
							if      (messageCode.equals("errorcode.notsupported.copyEntry.mirroredSource"))      messagePatch = NLT.get("copyEntryError.notsupported.Net.to.adHoc");
							else if (messageCode.equals("errorcode.notsupported.copyEntry.mirroredDestination")) messagePatch = NLT.get("copyEntryError.notsupported.adHoc.to.Net");
							else                                                                                 messagePatch = nse.getLocalizedMessage(); 
							messageArgs = new String[]{entryTitle, messagePatch};
						}
						if (null == messageText) {
							messageText = NLT.get(messageKey, messageArgs);
						}
						reply.addError(messageText);
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtViewHelper.copyEntries( Entry title:  '" + entryTitle + "', EXCEPTION ):  ", e);
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.copyEntries( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Disables the users.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData disableUsers(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Were we given the IDs of any users to disable?
			Long currentUserId = GwtServerHelper.getCurrentUserId(); 
			if (MiscUtil.hasItems(userIds)) {
				// Yes!  Scan them.
				boolean isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being disabled?
					User	user      = GwtServerHelper.getResolvedUser(userId);
					String	userTitle = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), ((null == user) ? "" : user.getTitle()));
					if (null != user) {
						// Yes!  Is it the user that's logged in?
						if (user.getId().equals(currentUserId)) {
							// Yes!  They can't disable themselves.
							// Ignore it.
							reply.addError(NLT.get("disableUserError.self"));
							continue;
						}

						// Is it a reserved user?
						if (user.isReserved()) {
							// Yes!  They can't do that.  Ignore it.
							reply.addError(NLT.get("disableUserError.reserved", new String[]{userTitle}));
							continue;
						}
						
						// Was this user provisioned from an LDAP
						// source?
						if (user.getIdentityInfo().isFromLdap()) {
							// Yes!  They can't be disabled this way.
							// Ignore it.
							reply.addError(NLT.get("disableUserError.fromLdap", new String[]{userTitle}));
							continue;
						}
					}
					
					try {
						// Can we disable this user?
						bs.getProfileModule().disableEntry(userId, true);	// true -> Disable.
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String messageKey;
						if (e instanceof AccessControlException) messageKey = "disableUserError.AccssControlException";
						else                                     messageKey = "disableUserError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtViewHelper.disableUsers( User:  '" + user.getTitle() + "', EXCEPTION ):  ", e);
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.disableUsers( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Dumps the contents of a List<FolderColumn>.
	 */
	private static void dumpFolderColumns(String start1, String start2, List<FolderColumn> fcList) {
		if ((null == fcList) || fcList.isEmpty()) {
			GwtLogHelper.debug(m_logger, (start1 + "No Column Data"));
		}
		else {
			GwtLogHelper.debug(m_logger, (start1 + "Column Data:"));
			int colIndex = 0;
			for (FolderColumn fc:  fcList) {
				GwtLogHelper.debug(m_logger, start2 + "...Index:             " + colIndex                                           );
				GwtLogHelper.debug(m_logger, start2 + "......Shown:          " + GwtLogHelper.dumpBoolean(fc.isColumnShown())       );
				GwtLogHelper.debug(m_logger, start2 + "......Sortable:       " + fc.isColumnSortable()                              );
				GwtLogHelper.debug(m_logger, start2 + "......Name:           " + GwtLogHelper.dumpString(fc.getColumnName())        );
				GwtLogHelper.debug(m_logger, start2 + "......Title:          " + GwtLogHelper.dumpString(fc.getColumnTitle())       );
				GwtLogHelper.debug(m_logger, start2 + "......Default title:  " + GwtLogHelper.dumpString(fc.getColumnDefaultTitle()));
				GwtLogHelper.debug(m_logger, start2 + "......Custom title:   " + GwtLogHelper.dumpString(fc.getColumnCustomTitle()) );
				GwtLogHelper.debug(m_logger, start2 + "......Search key:     " + GwtLogHelper.dumpString(fc.getColumnSearchKey())   );
				GwtLogHelper.debug(m_logger, start2 + "......Sort key:       " + GwtLogHelper.dumpString(fc.getColumnSortKey())     );
				GwtLogHelper.debug(m_logger, start2 + "......Def ID:         " + GwtLogHelper.dumpString(fc.getColumnDefId())       );
				GwtLogHelper.debug(m_logger, start2 + "......Ele name:       " + GwtLogHelper.dumpString(fc.getColumnEleName())     );
				GwtLogHelper.debug(m_logger, start2 + "......Type:           " + GwtLogHelper.dumpString(fc.getColumnType())        );
				
				colIndex += 1;
			}
		}
	}
	
	/*
	 * Dumps the contents of a FolderColumnsRpcResponseData object.
	 */
	private static void dumpFolderColumnsRpcResponseData(Binder binder, FolderColumnsRpcResponseData fcData) {
		// If debug tracing isn't enabled...
		if (!(GwtLogHelper.isDebugEnabled(m_logger))) {
			// ...bail.
			return;
		}

		// If we weren't given a FolderColumnsRpcResponseData to
		// dump...
		if (null == fcData) {
			// ...trace that fact and bail.
			GwtLogHelper.debug(m_logger, "...dumpFolderColumnsRpcResponseData( null ):  No FolderColumnsRpcResponseData to dump.");
			return;
		}

		// Dump the FolderColumnsRpcResponseData.
		GwtLogHelper.debug(m_logger, "...dumpFolderColumnsRpcResponseData():"        );
		if (null != binder) {
			GwtLogHelper.debug(m_logger, "......Binder is folder:  " + (binder instanceof Folder));
			GwtLogHelper.debug(m_logger, "......Binder ID:         " +  binder.getId()           );
			GwtLogHelper.debug(m_logger, "......Binder Title:      " +  binder.getTitle()        );
		}
		else {
			GwtLogHelper.debug(m_logger, "......Binder is null.");
		}
		GwtLogHelper.debug(m_logger, "......"                                        );
		GwtLogHelper.debug(m_logger, "......Folder admin:  " + fcData.isFolderAdmin());
		dumpFolderColumns("......List:  ",     "......", fcData.getFolderColumns()   );
		dumpFolderColumns("......List all:  ", "......", fcData.getFolderColumnsAll());
	}
	
	/**
	 * Dumps the contents of a FolderRowsRpcResponseData object.
	 * 
	 * @param binder
	 * @param frData
	 */
	public static void dumpFolderRowsRpcResponseData(Log logger, Binder binder, FolderRowsRpcResponseData frData) {
		// If debug tracing isn't enabled...
		if (!(GwtLogHelper.isDebugEnabled(logger))) {
			// ...bail.
			return;
		}

		// If we weren't given a FolderRowsRpcResponseData to dump...
		if (null == frData) {
			// ...trace that fact and bail.
			GwtLogHelper.debug(logger, "...dumpFolderRowsRpcResponseData( null ):  No FolderRowsRpcResponseData to dump.");
			return;
		}
		
		// Dump the contents of the FolderRowsRpcResponseData.
		GwtLogHelper.debug(logger, "...dumpFolderRowsRpcResponseData():");
		if (null != binder) {
			GwtLogHelper.debug(logger, "......Binder is folder:  " + (binder instanceof Folder));
			GwtLogHelper.debug(logger, "......Binder ID:         " +  binder.getId()           );
			GwtLogHelper.debug(logger, "......Binder Title:      " +  binder.getTitle()        );
		}
		else {
			GwtLogHelper.debug(logger, "......Binder is null.");
		}
		GwtLogHelper.debug(logger, "......");
		GwtLogHelper.debug(logger, "......Start offset:      " + frData.getStartOffset()                                );
		GwtLogHelper.debug(logger, "......Total rows:        " + frData.getTotalRows()                                  );
		GwtLogHelper.debug(logger, "......Total count type:  " + frData.getTotalCountType().name()                      );
		GwtLogHelper.debug(logger, "......Contributor IDs:   " + GwtLogHelper.dumpLLAsString(frData.getContributorIds()));
		
		List<FolderRow> frList = frData.getFolderRows();
		if ((null == frList) || frList.isEmpty()) {
			GwtLogHelper.debug(logger, "......No Row Data");
		}
		else {
			GwtLogHelper.debug(logger, "......Row data:");
			int rowIndex = 0;
			for (FolderRow fr:  frList) {
				GwtLogHelper.debug(logger, ".........Index:               " + rowIndex                                      );
				GwtLogHelper.debug(logger, "............Entity ID:        " + GwtLogHelper.dumpEIDAsString(fr.getEntityId()));
				GwtLogHelper.debug(logger, "............Family:           " + GwtLogHelper.dumpString(fr.getRowFamily())    );
				GwtLogHelper.debug(logger, "............Is Home dir:      " + fr.isHomeDir()                                );
				GwtLogHelper.debug(logger, "............Is My Files dir:  " + fr.isMyFilesDir()                             );
				GwtLogHelper.debug(logger, "............Is pinned:        " + fr.isPinned()                                 );
				
				GwtLogHelper.dumpMapStringStringToDebug(            logger, "............String map:",               "...............", fr.getRowStringsMap()            );
				GwtLogHelper.dumpMapStringCommentsInfoTooDebug(     logger, "............CommentsInfo map:",         "...............", fr.getRowCommentsMap()           );
				GwtLogHelper.dumpMapStringDescriptionHtmlToDebug(   logger, "............DescriptionHtml map:",      "...............", fr.getRowDescriptionHtmlMap()    );
				GwtLogHelper.dumpMapStringEmailAddressInfoToDebug(  logger, "............EmailAddressInfo map:",     "...............", fr.getRowEmailAddressMap()       );
				GwtLogHelper.dumpMapStringEntryEventInfoToDebug(    logger, "............EntryEventInfo map:",       "...............", fr.getRowEntryEventMap()         );
				GwtLogHelper.dumpMapStringEntryLinkInfoToDebug(     logger, "............EntryLinkInfo map:",        "...............", fr.getRowEntryLinkMap()          );
				GwtLogHelper.dumpMapStringEntryTitleInfoToDebug(    logger, "............EntryTitleInfo map:",       "...............", fr.getRowEntryTitlesMap()        );
				GwtLogHelper.dumpMapStringGuestInfoToDebug(         logger, "............GuestInfo map:",            "...............", fr.getRowGuestsMap()             );
				GwtLogHelper.dumpMapStringListAssignmentInfoToDebug(logger, "............List<AssignmentInfo> map:", "...............", fr.getRowAssigneeInfoListsMap()  );
				GwtLogHelper.dumpMapStringMobileDevicesInfoToDebug( logger, "............MobileDevicesInfo map:",    "...............", fr.getRowMobileDevicesMap()      );
				GwtLogHelper.dumpMapStringPrincipalInfoToDebug(     logger, "............PrincipalInfo map:",        "...............", fr.getRowPrincipalsMap()         );
				GwtLogHelper.dumpMapStringPrincipalInfoIdToDebug(   logger, "............PrincipalInfoId map:",      "...............", fr.getRowPrincipalIdsMap()       );
				GwtLogHelper.dumpMapStringListTaskFolderInfoToDebug(logger, "............List<TaskFolderInfo> map:", "...............", fr.getRowTaskFolderInfoListsMap());
				GwtLogHelper.dumpMapStringViewFileInfoToDebug(      logger, "............ViewFileInfo map:",         "...............", fr.getRowViewFilesMap()          );
				GwtLogHelper.dumpMapStringPrincipalAdminTypeToDebug(logger, "............PrincipalAdminType map:",   "...............", fr.getRowPrincipalAdminTypesMap());
				GwtLogHelper.dumpMobileDeviceToDebug(               logger, "............MobileDevice:",             "...............", fr.getServerMobileDevice()       );
				GwtLogHelper.dumpMapStringBooleanToDebug(           logger, "............OverdueDates map:",         "...............", fr.getRowOverdueDates()          );
				GwtLogHelper.dumpMapStringBooleanToDebug(           logger, "............WipesScheduled map:",       "...............", fr.getRowWipesScheduled()        );

				rowIndex += 1;
			}
		}
	}

	private static void dumpFolderRowsRpcResponseData(Binder binder, FolderRowsRpcResponseData frData) {
		// Always use the initial form of the method.
		dumpFolderRowsRpcResponseData(m_logger, binder, frData);
	}
	
	/*
	 * Dumps the contents of a SelectedUsersDetails object.
	 */
	private static void dumpSelectedUsersDetails(SelectedUsersDetails sd) {
		// If debug tracing isn't enabled...
		if (!(GwtLogHelper.isDebugEnabled(m_logger))) {
			// ...bail.
			return;
		}

		// If we weren't given a SelectionDetails to dump...
		if (null == sd) {
			// ...trace that fact and bail.
			GwtLogHelper.debug(m_logger, "...dumpSelectionDetails( null ):  No SelectionDetails to dump.");
			return;
		}

		// Dump the contents of the SelectionDetails.
		GwtLogHelper.debug(m_logger, "...dumpSelectionDetails():");
		GwtLogHelper.debug(m_logger, "......Has AdHoc User Workspaces:               " + sd.hasAdHocUserWorkspaces()         );
		GwtLogHelper.debug(m_logger, "......Has AdHoc User Workspaces (Purge Only):  " + sd.hasAdHocUserWorkspacesPurgeOnly());
		GwtLogHelper.debug(m_logger, "......Has Purge Confirmatiions:                " + sd.hasPurgeConfirmations()          );
		GwtLogHelper.debug(m_logger, "......Has Purge Only Selections:               " + sd.hasPurgeOnlySelections()         );
		GwtLogHelper.debug(m_logger, "......Has Purge Only Workspaces:               " + sd.hasPurgeOnlyWorkspaces()         );
		GwtLogHelper.debug(m_logger, "......Has Unclassified:                        " + sd.hasUnclassified()                );
		GwtLogHelper.debug(m_logger, "......Has User Workspaces:                     " + sd.hasUserWorkspaces()              );
		GwtLogHelper.debug(m_logger, "......User Workspace Count:                    " + sd.getUserWorkspaceCount()          );
		GwtLogHelper.debug(m_logger, "......Total Count:                             " + sd.getTotalCount()                  );
	}
	
	/*
	 * Dumps the contents of a SelectionDetails object.
	 */
	private static void dumpSelectionDetails(SelectionDetails sd) {
		// If debug tracing isn't enabled...
		if (!(GwtLogHelper.isDebugEnabled(m_logger))) {
			// ...bail.
			return;
		}

		// If we weren't given a SelectionDetails to dump...
		if (null == sd) {
			// ...trace that fact and bail.
			GwtLogHelper.debug(m_logger, "...dumpSelectionDetails( null ):  No SelectionDetails to dump.");
			return;
		}

		// Dump the contents of the SelectionDetails.
		GwtLogHelper.debug(m_logger, "...dumpSelectionDetails():");
		GwtLogHelper.debug(m_logger, "......Has AdHoc Binders:                        " + sd.hasAdHocBinders()                   );
		GwtLogHelper.debug(m_logger, "......Has AdHoc Entries:                        " + sd.hasAdHocEntries()                   );
		GwtLogHelper.debug(m_logger, "......Has AdHoc Folders:                        " + sd.hasAdHocFolders()                   );
		GwtLogHelper.debug(m_logger, "......Has AdHoc Folders With Nested Remote:     " + sd.hasAdHocFoldersWithNestedRemote()   );
		GwtLogHelper.debug(m_logger, "......Has AdHoc Workspaces:                     " + sd.hasAdHocWorkspaces()                );
		GwtLogHelper.debug(m_logger, "......Has AdHoc Workspaces With Nested Remote:  " + sd.hasAdHocWorkspacesWithNestedRemote());
		GwtLogHelper.debug(m_logger, "......Has Cloud Folders:                        " + sd.hasCloudFolders()                   );
		GwtLogHelper.debug(m_logger, "......Has Mirrored Folders:                     " + sd.hasMirroredFolders()                );
		GwtLogHelper.debug(m_logger, "......Has Net Folders:                          " + sd.hasNetFolders()                     );
		GwtLogHelper.debug(m_logger, "......Has Purge Confirmatiions:                 " + sd.hasPurgeConfirmations()             );
		GwtLogHelper.debug(m_logger, "......Has Remote Entries:                       " + sd.hasRemoteEntries()                  );
		GwtLogHelper.debug(m_logger, "......Has Remote Folders:                       " + sd.hasRemoteFolders()                  );
		GwtLogHelper.debug(m_logger, "......Has Remote Selections:                    " + sd.hasRemoteSelections()               );
		GwtLogHelper.debug(m_logger, "......Has Remote Workspaces:                    " + sd.hasRemoteWorkspaces()               );
		GwtLogHelper.debug(m_logger, "......Has Binders:                              " + sd.hasBinders()                        );
		GwtLogHelper.debug(m_logger, "......Has Entries:                              " + sd.hasEntries()                        );
		GwtLogHelper.debug(m_logger, "......Has Folders:                              " + sd.hasFolders()                        );
		GwtLogHelper.debug(m_logger, "......Has Unclassified:                         " + sd.hasUnclassified()                   );
		GwtLogHelper.debug(m_logger, "......Has Workspaces:                           " + sd.hasWorkspaces()                     );
		GwtLogHelper.debug(m_logger, "......Binder    Count:                          " + sd.getBinderCount()                    );
		GwtLogHelper.debug(m_logger, "......Entry     Count:                          " + sd.getEntryCount()                     );
		GwtLogHelper.debug(m_logger, "......Folder    Count:                          " + sd.getFolderCount()                    );
		GwtLogHelper.debug(m_logger, "......Workspace Count:                          " + sd.getWorkspaceCount()                 );
		GwtLogHelper.debug(m_logger, "......Total     Count:                          " + sd.getTotalCount()                     );
	}

	/*
	 * Dumps the contents of a ViewInfo object.
	 */
	private static void dumpViewInfo(ViewInfo vi) {
		// If debug tracing isn't enabled...
		if (!(GwtLogHelper.isDebugEnabled(m_logger))) {
			// ...bail.
			return;
		}

		// If we weren't given a ViewInfo to dump...
		if (null == vi) {
			// ...trace that fact and bail.
			GwtLogHelper.debug(m_logger, "...dumpViewInfo( null ):  No ViewInfo to dump.");
			return;
		}
		
		ViewType vt = vi.getViewType();
		GwtLogHelper.debug(m_logger, "...dumpViewInfo( " + vt.name() + " )");
		switch (vt) {
		case BINDER:
			BinderInfo bi = vi.getBinderInfo();
			BinderType bt = bi.getBinderType();
			GwtLogHelper.debug(m_logger, ".....dumpViewInfo( BINDER ):  " + bt.name());
			switch (bt) {
			case COLLECTION:
				GwtLogHelper.debug(m_logger, "........dumpViewInfo( BINDER:COLLECTION  ):  " + bi.getCollectionType().name());
				break;
				
			case FOLDER:
				GwtLogHelper.debug(m_logger, "........dumpViewInfo( BINDER:FOLDER      ):  " + bi.getFolderType().name());
				break;
				
			case WORKSPACE:
				GwtLogHelper.debug(m_logger, "........dumpViewInfo( BINDER:WORKSPACE   ):  " + bi.getWorkspaceType().name());
				break;
			
			case OTHER:
				GwtLogHelper.debug(m_logger, "........dumpViewInfo( BINDER:OTHER       )");
				break;
				
			default:
				GwtLogHelper.debug(m_logger, "........dumpViewInfo( BINDER:Not Handled ):  This BinderType is not implemented by the dumper.");
				break;
			}
			
			GwtLogHelper.debug(m_logger, "........dumpViewInfo( BINDER:Id         ):  " + bi.getBinderId());
			GwtLogHelper.debug(m_logger, "........dumpViewInfo( BINDER:Title      ):  " + bi.getBinderTitle());
			GwtLogHelper.debug(m_logger, "........dumpViewInfo( BINDER:EntityType ):  " + bi.getEntityType());
			
			break;
			
		case ADD_BINDER:
		case ADD_FOLDER_ENTRY:
		case ADD_PROFILE_ENTRY:
		case ADVANCED_SEARCH:
		case BUILD_FILTER:
		case VIEW_PROFILE_ENTRY:
		case OTHER:
			break;
			
		default:
			GwtLogHelper.debug(m_logger, "......dumpViewInfo( Not Handled ):  This ViewType is not implemented by the dumper.");
			break;
		}
	}

	/**
	 * Enables the users.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData enableUsers(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Were we given the IDs of any users to enable?
			if (MiscUtil.hasItems(userIds)) {
				// Yes!  Scan them.
				FolderModule  fm                          = bs.getFolderModule();
				ProfileModule pm                          = bs.getProfileModule();
				boolean       isOtherUserAccessRestricted = Utils.canUserOnlySeeCommonGroupMembers();
				for (Long userId:  userIds) {
					// Can we resolve the user being disabled?
					User	user         = GwtServerHelper.getResolvedUser(userId);
					boolean userResolved = (null != user);
					String	userTitle    = GwtServerHelper.getUserTitle(bs.getProfileModule(), isOtherUserAccessRestricted, String.valueOf(userId), (userResolved ? user.getTitle() : ""));
					
					try {
						// Did we resolve the user?
						if (userResolved) {
							// Yes!  Was this user provisioned from an
							// LDAP source?
							if (user.getIdentityInfo().isFromLdap()) {
								// Yes!  They can't be enabled this way.
								// Ignore it.
								reply.addError(NLT.get("enableUserError.fromLdap", new String[]{userTitle}));
								continue;
							}
							
							// Is this user's workspace in the trash?
							if (pm.getUserWorkspacePreDeleted(userId)) {
								// Yes!  Then they can't be enabled.
								// Ignore it.
								reply.addError(NLT.get("enableUserError.WorkspaceTrashed", new String[]{userTitle}));
								continue;
							}
							
							// Is this user's My Files Storage folder in
							// the trash?
							Long mfId = SearchUtils.getMyFilesFolderId(bs, user, false);
							if (null != mfId) {
								Folder mf = fm.getFolder(mfId);
								if ((null != mf) && mf.isPreDeleted()) {
									// Yes!  Then they can't be
									// enabled.  Ignore it.
									reply.addError(NLT.get("enableUserError.MyFilesTrashed", new String[]{userTitle}));
									continue;
								}
							}
						}

						// Can we enable this user?
						pm.disableEntry(userId, false);	// false -> Enable.
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String messageKey;
						if (e instanceof AccessControlException) messageKey = "enableUserError.AccssControlException";
						else                                     messageKey = "enableUserError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{userTitle}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtViewHelper.enableUsers( User:  '" + user.getTitle() + "', EXCEPTION ):  ", e);
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.enableUsers( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Checks for assignment column search key in the folder columns.
	 * If there's a column for it, simply returns.  If there's not a
	 * column for it, checks the entry map for assignments and if any
	 * are found, adds them to the assignment list.
	 */
	@SuppressWarnings("unchecked")
	private static void factorInAssignments(Map entryMap, List<FolderColumn> folderColumns, String csk, AssigneeType assigneeType, List<AssignmentInfo> assignmentList) {
		// Scan the columns.
		for (FolderColumn fc:  folderColumns) {
			// Is this column for the given search key?
			if (fc.getColumnSearchKey().equals(csk)) {
				// Yes!  Then we don't handle it's assignments
				// separately.  Bail.
				return;
			}
		}
		
		// Are there any assignments for the given column search key?
		List<AssignmentInfo> addList = GwtEventHelper.getAssignmentInfoListFromEntryMap(entryMap, csk, assigneeType);
		if (MiscUtil.hasItems(addList)) {
			// Yes!  Copy them into the assignment list we were given.
			for (AssignmentInfo ai:  addList) {
				assignmentList.add(ai);
			}
		}
	}
	
	/*
	 * Checks for the group assignment corresponding to the individual
	 * column search key in the folder columns.  If there's a column
	 * for it, simply returns.  If there's not a column for it, checks
	 * the entry map for assignments and if any are found, adds them to
	 * the assignment list.
	 */
	@SuppressWarnings("unchecked")
	private static void factorInGroupAssignments(Map entryMap, List<FolderColumn> folderColumns, String csk, List<AssignmentInfo> assignmentList) {
		// Can we determine the group assignment attribute?
		String groupCSK;
		if (csk.equals(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME))           groupCSK = TaskHelper.ASSIGNMENT_GROUPS_TASK_ENTRY_ATTRIBUTE_NAME;
		else if (csk.equals(EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME)) groupCSK = EventHelper.ASSIGNMENT_GROUPS_CALENDAR_ENTRY_ATTRIBUTE_NAME;
		else if (csk.equals(RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME))           groupCSK = RESPONSIBLE_GROUPS_MILESTONE_ENTRY_ATTRIBUTE_NAME;
		else                                                                       groupCSK = null;
		if (null != groupCSK) {
			// Yes!  Factor in any group assignments using it.
			factorInAssignments(entryMap, folderColumns, groupCSK, AssigneeType.GROUP, assignmentList);
		}
	}
	
	/*
	 * Checks for the team assignment corresponding to the individual
	 * column search key in the folder columns.  If there's a column
	 * for it, simply returns.  If there's not a column for it, checks
	 * the entry map for assignments and if any are found, adds them to
	 * the assignment list.
	 */
	@SuppressWarnings("unchecked")
	private static void factorInTeamAssignments(Map entryMap, List<FolderColumn> folderColumns, String csk, List<AssignmentInfo> assignmentList) {
		// Can we determine the team assignment attribute?
		String teamCSK;
		if (csk.equals(TaskHelper.ASSIGNMENT_TASK_ENTRY_ATTRIBUTE_NAME))           teamCSK = TaskHelper.ASSIGNMENT_TEAMS_TASK_ENTRY_ATTRIBUTE_NAME;
		else if (csk.equals(EventHelper.ASSIGNMENT_CALENDAR_ENTRY_ATTRIBUTE_NAME)) teamCSK = EventHelper.ASSIGNMENT_TEAMS_CALENDAR_ENTRY_ATTRIBUTE_NAME;
		else if (csk.equals(RESPONSIBLE_MILESTONE_ENTRY_ATTRIBUTE_NAME))           teamCSK = RESPONSIBLE_TEAMS_MILESTONE_ENTRY_ATTRIBUTE_NAME;
		else                                                                       teamCSK = null;
		if (null != teamCSK) {
			// Yes!  Factor in any team assignments using it.
			factorInAssignments(entryMap, folderColumns, teamCSK, AssigneeType.TEAM, assignmentList);
		}
	}

	/*
	 * Applies a quick filter to a List<FolderRow> of
	 * 'Shared by/with Me' rows.  A List<FolderRow> of the the
	 * FolderRow's from the input list that matches the filter is
	 * returned.
	 */
	public static List<FolderRow> filterSharedMeFolderRows(List<FolderColumn> folderColumns, List<FolderRow> folderRows, String quickFilter) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.filterSharedMeFolderRows()");
		try {
			// Do we have a string to filter with and some FolderRow's
			// to be filtered?
			if (null != quickFilter) {
				quickFilter = quickFilter.trim().toLowerCase();
			}
			if (MiscUtil.hasString(quickFilter) && MiscUtil.hasItems(folderRows)) {
				// Yes!  Yes!  Scan the rows.
				List<FolderRow> reply = new ArrayList<FolderRow>();
				for (FolderRow fr:  folderRows) {
					// Scan the columns.
					for (FolderColumn fc:  folderColumns) {
						// What column is this?
						String cName = fc.getColumnName();
						if (FolderColumn.isColumnTitle(cName)) {
							// The title column!  If the title contains
							// the quick filter...
							EntryTitleInfo eti = fr.getColumnValueAsEntryTitle(fc);
							if (null != eti) {
								if (valueContainsQuickFilter(eti.getTitle(), quickFilter)) {
									// ...add it to the reply list.
									reply.add(fr);
									break;
								}
							}
						}
							
						else if (FolderColumn.isColumnSharedBy(cName) || FolderColumn.isColumnSharedWith(cName)) {
							// A shared by/with column!  Are there any
							// values for that?
							List<AssignmentInfo> aiList = fr.getColumnValueAsAssignmentInfos(fc);
							if (MiscUtil.hasItems(aiList)) {
								// Yes!  Scan them...
								boolean found = false;
								for (AssignmentInfo ai:  aiList) {
									// ...if this value contains the
									// ...quick filter...
									if (valueContainsQuickFilter(ai.getTitle(), quickFilter)) {
										// ...add it to the reply list.
										reply.add(fr);
										found = true;
										break;
									}
								}
								
								// Once we move the row to the reply...
								if (found) {
									// ...stop scanning the columns.
									break;
								}
							}
						}
						
						else if (FolderColumn.isColumnShareMessage(cName)) {
							// The share message column!  Are there any
							// values for that?
							List<ShareStringValue> svList = fr.getColumnValueAsShareMessageInfos(fc);
							if (MiscUtil.hasItems(svList)) {
								// Yes!  Scan them...
								boolean found = false;
								for (ShareStringValue sv:  svList) {
									// ...if this value contains the
									// ...quick filter...
									if (valueContainsQuickFilter(sv.getValue(), quickFilter)) {
										// ...add it to the reply list.
										reply.add(fr);
										found = true;
										break;
									}
								}
								
								// Once we move the row to the reply...
								if (found) {
									// ...stop scanning the columns.
									break;
								}
							}
						}
					}
				}
				folderRows = reply;
			}
			
			// If we get here, filterRows refers to the filtered list
			// of rows.  Return it. 
			return folderRows;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Forces the selected files to be unlocked.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData forceFilesUnlock(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Are there any entities to unlock?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them...
				FileModule   fim = bs.getFileModule();
				FolderModule fom = bs.getFolderModule();
				for (EntityId eid:  entityIds) {
					// ...forcing their primary file to be unlocked.
					FolderEntry    fe = fom.getEntry(eid.getBinderId(), eid.getEntityId());
					FileAttachment fa = MiscUtil.getPrimaryFileAttachment(fe);
					if ((null != fa) && (null != fa.getFileLock())) {
						fim.forceUnlock(fe.getParentBinder(), fe, fa);
					}
				}
			}
			
			// If we get here, the unlocks were successful.  Return
			// true.
			return new BooleanRpcResponseData(Boolean.TRUE);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.forceFilesUnlock( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Return true of the accessory panel should be visible on the
	 * given binder and false otherwise.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static Boolean getAccessoryStatus(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			Binder			binder               = bs.getBinderModule().getBinder(binderId);
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			
			// Has the user saved the status of the accessories panel
			// on this binder?
			Boolean accessoryStatus = ((Boolean) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_BINDER_SHOW_ACCESSORIES));
			if (null == accessoryStatus) {
				// No!  If they have any accessories defined, we show
				// the panel.
				UserProperties	userProperties = bs.getProfileModule().getUserProperties(user.getId());
				Map<String, Object> model = new HashMap<String,Object>();
				DashboardHelper.getDashboardMap(binder, userProperties.getProperties(), model);
				Map ssDashboard = ((Map) model.get(WebKeys.DASHBOARD));
				accessoryStatus = DashboardHelper.checkIfAnyContentExists(ssDashboard);

				// Save this status so we don't have to read the
				// accessories again.
				saveAccessoryStatus(bs, request, binderId, accessoryStatus);
			}
			
			// If we get here, accessoryStatus contains true if we
			// should show the accessory panel and false otherwise.
			// Return it.
			return accessoryStatus;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getAccessoryStatus( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Return a CanAddEntitiesRpcResponseData object containing what
	 * the user has rights to add to the given binder.
	 * 
	 * @param bs
	 * @param request
	 * @param binderInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CanAddEntitiesRpcResponseData getCanAddEntities(AllModulesInjected bs, HttpServletRequest request, BinderInfo binderInfo) throws GwtTeamingException {
		try {
			// Based on the binder type, determine what the user can do.
			BinderModule bm     = bs.getBinderModule();
			FolderModule fm     = bs.getFolderModule();
			Binder       binder = bm.getBinder(binderInfo.getBinderIdAsLong());
			boolean      canAddEntries = false;
			boolean      canAddFolders = false;
			switch (binderInfo.getBinderType()) {
			case COLLECTION:
				// The binder's a collection!  Is it their My Files?
				if (CollectionType.MY_FILES.equals(binderInfo.getCollectionType())) {
					// Yes!  Can we get their My Files Storage folder
					// ID?
					Long mfId = SearchUtils.getMyFilesFolderId(bs, false);
					if (null != mfId) {
						// Yes!  Check what they can add to it.
						Folder mf = fm.getFolder(mfId);
						canAddEntries = fm.testAccess(mf, FolderOperation.addEntry );
						canAddFolders = bm.testAccess(mf,  BinderOperation.addFolder);
					}
				}
				break;
				
			case FOLDER:
				// The binder's a folder!  Check what they can add to
				// it.
				canAddEntries = fm.testAccess(((Folder) binder), FolderOperation.addEntry );
				canAddFolders = bm.testAccess(          binder,  BinderOperation.addFolder);
				break;
				
			case WORKSPACE:
				// The binder's a workspace!  Is it the email templates
				// view?
				if (binderInfo.isBinderEmailTemplates()) {
					// Yes!  To enable the HTML5 uploader, we need to
					// allow entries to be added.
					canAddEntries = true;
				}
				
				else {
					// No, it's not the email templates view!  If they
					// can add anything, it would only be folders.
					canAddFolders = bm.testAccess(binder, BinderOperation.addFolder);
				}
				break;
			}
			
			// Return a CanAddEntitiesRpcResponseData with the user's
			// rights to add to the given binder.
			return new CanAddEntitiesRpcResponseData(canAddEntries, canAddFolders);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getCanAddEntities( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Return a CanAddEntitiesRpcToBindersResponseData object
	 * containing what the user has rights to add to the given
	 * binders.
	 * 
	 * @param bs
	 * @param request
	 * @param binderIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static CanAddEntitiesToBindersRpcResponseData getCanAddEntitiesToBinders(AllModulesInjected bs, HttpServletRequest request, List<Long> binderIds) throws GwtTeamingException {
		try {
			// Allocate a CanAddEntitiesToBindersRpcResponseData we can
			// return.
			CanAddEntitiesToBindersRpcResponseData reply = new CanAddEntitiesToBindersRpcResponseData();
			
			//  Were we given any binder IDs? 
			if (MiscUtil.hasItems(binderIds)) {
				// Yes!  Access the binders for them.
				FolderModule fm      = bs.getFolderModule();
				BinderModule bm      = bs.getBinderModule();
				Set<Binder>  binders = bm.getBinders(binderIds);
				
				// Scan the binders.
				for (Binder binder:  binders) {
					// Is this binder a folder?
					boolean canAddEntries = false;
					boolean canAddFolders = false;
					EntityType binderType = binder.getEntityType();
					if (binderType.equals(EntityType.folder)) {
						// Yes!  Check what they can add to it.
						canAddEntries = fm.testAccess(((Folder) binder), FolderOperation.addEntry );
						canAddFolders = bm.testAccess(          binder,  BinderOperation.addFolder);
					}
					
					// No, it's not a folder!  Is it a Workspace?
					else if (binderType.equals(EntityType.workspace)) {
						// Yes!  If they can add anything, it would
						// only be folders.
						canAddFolders = bm.testAccess(binder, BinderOperation.addFolder);
					}
					
					// Add the user's add rights for this binder to the
					// reply.
					reply.addCanAddEntities(binder.getId(), canAddEntries, canAddFolders);
				}
			}
			
			// If we get here, reply refers to a
			// CanAddEntitiesToBindersRpcResponseData with the user's
			// rights to add to the given binders.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getCanAddEntitiesToBinders( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Return a ClickOnTitleActionRpcResponseData object containing
	 * what should happen when the user clicks on an entity's title.
	 * 
	 * @param bs
	 * @param request
	 * @param entityId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ClickOnTitleActionRpcResponseData getClickOnTitleAction(AllModulesInjected bs, HttpServletRequest request, EntityId entityId) throws GwtTeamingException {
		try {
			// Are we getting the click action on a folder entry?
			ClickOnTitleActionRpcResponseData reply = null;
			if (entityId.isEntry()) {
				// Yes!  Is it a file entry that we can get the primary
				// file attachment from?  
				boolean        doViewDetails = false;
				boolean        doDownload    = false;
				Long           binderId      = entityId.getBinderId();
				Long           entryId       = entityId.getEntityId();
				FolderEntry    fe            = bs.getFolderModule().getEntry(binderId, entryId);
				FileAttachment fa            = GwtServerHelper.getFileEntrysFileAttachment(bs, fe, true);
				if (null != fa) {
					// Yes!  What's the user's preference to as to the
					// action to perform?
					FileLinkAction fla = AdminHelper.getEffectiveFileLinkAction(bs);
					switch (fla) {
					case DOWNLOAD:
						// Download the file!
						doDownload = true;
						break;
						
					case VIEW_HTML_ELSE_DETAILS:
					case VIEW_HTML_ELSE_DOWNLOAD:
						// View as HTML, if we can!  Can we?
						ViewFileInfo vfi = buildViewFileInfo(request, fe, fa);
						if (null != vfi) {
							// Yes!  Generate the appropriate reply.
							reply = new ClickOnTitleActionRpcResponseData(ClickAction.VIEW_AS_HTML, vfi.getViewFileUrl());
							break;
						}

						// No, we can't view it as HTML!  Do we view
						// details or download it?
						if (fla.isViewHtmlElseDetails())
							 doViewDetails = true;
						else doDownload    = true;
						break;

					default:
					case VIEW_DETAILS:
						// View details on the file!
						doViewDetails = true;
						break;
					}
				}
				
				else {
					// No, it's not a file entry or it doesn't have a
					// primary file attachment!  For these, we do a
					// view details.
					doViewDetails = true;
				}

				// If we need to view details...
				if (doViewDetails) {
					// ...generate the appropriate response...
					String url = GwtServerHelper.getViewFolderEntryUrl(bs, request, binderId, entryId);
					reply = new ClickOnTitleActionRpcResponseData(ClickAction.VIEW_DETAILS, url);
				}

				// ...otherwise, if we need to download the file...
				else if (doDownload) {
					// ...generate the appropriate response.
					String url = GwtDesktopApplicationsHelper.getDownloadFileUrl(request, bs, binderId, entryId);
					reply = new ClickOnTitleActionRpcResponseData(ClickAction.DOWNLOAD_FILE, url);
				}
			}
			
			else {
				// No, we aren't getting the click action on a folder
				// entry!  It must be on a binder.  For those, we
				// always descend into them.
				reply = new ClickOnTitleActionRpcResponseData(ClickAction.DESCEND_INTO_BINDER);				
			}

			// If we get here, reply refers to a
			// ClickOnTitleActionRpcResponseData with what should
			// happen when the user clicks on the entity's title.
			// Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getClickOnTitleAction( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Returns the days duration value if only days were found. 
	 * Otherwise returns -1.
	 */
	@SuppressWarnings("unchecked")
	private static int getDurDaysFromEntryMap(String colName, Map entryMap) {
		int days = (-1);
		int seconds = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#S")), (-1));
		int minutes = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#M")),  (-1));
		int hours   = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#H")),  (-1));
		int weeks   = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#W")),  (-1));
		if ((0 >= seconds) && (0 >= minutes) && (0 >= hours) && (0 >= weeks)) {
			days = MiscUtil.safeSToInt(((String) entryMap.get(colName + "#Duration#D")),  (-1));
		}		
		return days;
	}
	
	/*
	 * Fixes up the group assignees in an List<AssignmentInfo>'s.
	 */
	private static void fixupAIGroups(List<AssignmentInfo> aiGroupsList, Map<Long, String> principalTitles, Map<Long, Integer> groupCounts) {
		// If don't have a list to fixup...
		if (!(MiscUtil.hasItems(aiGroupsList))) {
			// ...bail.
			return;
		}
		
		// The removeList is used to handle cases where an ID could
		// not be resolved (e.g., an 'Assigned To' group has been
		// deleted.)
		List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
		
		// Scan this AssignmentInfo's group assignees...
		for (AssignmentInfo ai:  aiGroupsList) {
			// ...skipping those that aren't really groups...
			if (!(ai.getAssigneeType().isGroup())) {
				continue;
			}
			
			// ...and setting each one's title and membership count.
			if (GwtEventHelper.setAssignmentInfoTitle(  ai, principalTitles)) {
				GwtEventHelper.setAssignmentInfoMembers(ai, groupCounts     );
				ai.setPresenceDude("pics/group_20.png");
			}
			else {
				removeList.add(ai);
			}
		}
		GwtServerHelper.removeUnresolvedAssignees(aiGroupsList, removeList);
	}
	
	/*
	 * Fixes up the team assignees in an List<AssignmentInfo>'s.
	 */
	private static void fixupAITeams(List<AssignmentInfo> aiTeamsList, Map<Long, String> teamTitles, Map<Long, Integer> teamCounts) {
		// If don't have a list to fixup...
		if (!(MiscUtil.hasItems(aiTeamsList))) {
			// ...bail.
			return;
		}
		
		// The removeList is used to handle cases where an ID could
		// not be resolved (e.g., an 'Assigned To' team has been
		// deleted.)
		List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
		
		// Scan this AssignmentInfo's team assignees...
		for (AssignmentInfo ai:  aiTeamsList) {
			// ...skipping those that aren't really teams...
			if (!(ai.getAssigneeType().isTeam())) {
				continue;
			}
			
			// ...and setting each one's title and membership count.
			if (GwtEventHelper.setAssignmentInfoTitle(  ai, teamTitles)) {
				GwtEventHelper.setAssignmentInfoMembers(ai, teamCounts );
				ai.setPresenceDude("pics/team_16.png");
			}
			else {
				removeList.add(ai);
			}
		}
		GwtServerHelper.removeUnresolvedAssignees(aiTeamsList, removeList);
	}
	
	/*
	 * Fixes up the public link assignees in an List<AssignmentInfo>'s.
	 */
	private static void fixupAIPublicLinks(List<AssignmentInfo> aiPublicLinksList) {
		// If don't have a list to fixup...
		if (!(MiscUtil.hasItems(aiPublicLinksList))) {
			// ...bail.
			return;
		}
		
		// Scan this AssignmentInfo's team assignees...
		for (AssignmentInfo ai:  aiPublicLinksList) {
			// ...skipping those that aren't really public link...
			if (!(ai.getAssigneeType().isPublicLink())) {
				continue;
			}
			
			// ...and setting each one's title and hover.
			ai.setTitle(NLT.get("share.recipientType.title.productLink"));
			ai.setHover(NLT.get("share.recipientType.hover.productLink"));
		}
	}
	
	/*
	 * Fixes up the public assignees in an List<AssignmentInfo>'s.
	 */
	private static void fixupAIPublics(List<AssignmentInfo> aiPublicList) {
		// If don't have a list to fixup...
		if (!(MiscUtil.hasItems(aiPublicList))) {
			// ...bail.
			return;
		}
		
		// Scan this AssignmentInfo's team assignees...
		for (AssignmentInfo ai:  aiPublicList) {
			// ...skipping those that aren't really public...
			if (!(ai.getAssigneeType().isPublic())) {
				continue;
			}
			
			// ...and setting each one's title and hover.
			ai.setTitle(NLT.get("share.recipientType.title.public"));
			ai.setHover(NLT.get("share.recipientType.hover.public"));
		}
	}
	
	/*
	 * Fixes up the individual assignees in an List<AssignmentInfo>'s.
	 */
	private static void fixupAIUsers(List<AssignmentInfo> aiList, Map<Long, String> principalTitles, Map<Long, String> principalEMAs, Map<Long, GwtPresenceInfo> userPresence, Map<Long, Boolean> userExternal, Map<Long, Long> presenceUserWSIds, Map<Long, String> avatarUrls) {
		// If don't have a list to fixup...
		if (!(MiscUtil.hasItems(aiList))) {
			// ...bail.
			return;
		}
		
		// The removeList is used to handle cases where an ID could
		// not be resolved (e.g., an 'Assigned To' user has been
		// deleted.)
		List<AssignmentInfo> removeList = new ArrayList<AssignmentInfo>();
		
		// Scan this AssignmentInfo's individual assignees...
		for (AssignmentInfo ai:  aiList) {
			// ...skipping those that aren't really individuals...
			if (!(ai.getAssigneeType().isIndividual())) {
				continue;
			}
			
			// ...and setting each one's title.
			if (GwtEventHelper.setAssignmentInfoTitle(           ai, principalTitles )) {
				GwtEventHelper.setAssignmentInfoPresence(        ai, userPresence     );
				GwtEventHelper.setAssignmentInfoExternal(        ai, userExternal     );
				GwtEventHelper.setAssignmentInfoPresenceUserWSId(ai, presenceUserWSIds);
				GwtEventHelper.setAssignmentInfoAvatarUrl(       ai, avatarUrls       );
				GwtEventHelper.setAssignmentInfoHover(           ai, principalTitles  );
			}
			else {
				removeList.add(ai);
			}
		}
		GwtServerHelper.removeUnresolvedAssignees(aiList, removeList);
	}
	
	/*
	 * Walks the List<FolderColumn>'s setting the search and sort keys
	 * appropriately for each.
	 * 
	 * Note:  The algorithm used in this method for columns whose names
	 *    contain multiple parts was reverse engineered from that used
	 *    by folder_view_common2.jsp or view_trash.jsp.
	 */
	private static void fixupFCs(List<FolderColumn> fcList, boolean isTrash, boolean isCollection, CollectionType ct, boolean isFileFolder, boolean isManageAdmins) {
		// We need to handle the columns that were added for
		// collections.
		String dateCSK;
		if (isFileFolder || (isCollection && (ct.isMyFiles() || ct.isNetFolders())))
		     dateCSK = Constants.FILE_TIME_FIELD; 
		else dateCSK = Constants.MODIFICATION_DATE_FIELD;
		@SuppressWarnings("unused")
		String adminRightsCSK;
		for (FolderColumn fc:  fcList) {
			String colName = fc.getColumnName();
			if      (colName.equals("administrator"))         {fc.setColumnSearchKey(FolderColumn.COLUMN_ADMINISTRATOR);                                                                            }
			else if (colName.equals("author"))                {fc.setColumnSearchKey(Constants.PRINCIPAL_FIELD);                 fc.setColumnSortKey(Constants.SORT_CREATOR_TITLE_FIELD);           }
			else if (colName.equals("comments"))              {fc.setColumnSearchKey(Constants.TOTALREPLYCOUNT_FIELD);                                                                              }
			else if (colName.equals("date"))                  {fc.setColumnSearchKey(dateCSK);                                                                                                      }
			else if (colName.equals("description"))           {fc.setColumnSearchKey(Constants.DESC_FIELD);                                                                                         }
			else if (colName.equals("descriptionHtml"))       {fc.setColumnSearchKey(Constants.DESC_FIELD);                                                                                         }
			else if (colName.equals("deviceDescription"))     {fc.setColumnSearchKey(FolderColumn.COLUMN_DEVICE_DESCRIPTION);    fc.setColumnSortKey(ObjectKeys.FIELD_MOBILE_DEVICE_DESCRIPTION);   }
			else if (colName.equals("deviceLastLogin"))       {fc.setColumnSearchKey(FolderColumn.COLUMN_DEVICE_LAST_LOGIN);     fc.setColumnSortKey(ObjectKeys.FIELD_MOBILE_DEVICE_LAST_LOGIN);    }
			else if (colName.equals("deviceUser"))            {fc.setColumnSearchKey(FolderColumn.COLUMN_DEVICE_USER);           fc.setColumnSortKey(ObjectKeys.FIELD_MOBILE_DEVICE_USER_TITLE);    }
			else if (colName.equals("deviceWipeDate"))        {fc.setColumnSearchKey(FolderColumn.COLUMN_DEVICE_WIPE_DATE);      fc.setColumnSortKey(ObjectKeys.FIELD_MOBILE_DEVICE_WIPE_DATE);     }
			else if (colName.equals("deviceWipeScheduled"))   {fc.setColumnSearchKey(FolderColumn.COLUMN_DEVICE_WIPE_SCHEDULED); fc.setColumnSortKey(ObjectKeys.FIELD_MOBILE_DEVICE_WIPE_SCHEDULED);}
			else if (colName.equals("docNum"))                {fc.setColumnSearchKey(Constants.DOCNUMBER_FIELD);                 fc.setColumnSortKey(Constants.SORTNUMBER_FIELD);                   }
			else if (colName.equals("download"))              {fc.setColumnSearchKey(Constants.FILENAME_FIELD);                                                                                     }
			else if (colName.equals("dueDate"))               {fc.setColumnSearchKey(Constants.DUE_DATE_FIELD);                                                                                     }
			else if (colName.equals("emailAddress"))          {fc.setColumnSearchKey(Constants.EMAIL_FIELD);                                                                                        }
			else if (colName.equals("emailTemplateName"))     {fc.setColumnSearchKey(FolderColumn.COLUMN_EMAIL_TEMPLATE_NAME);                                                                      }
			else if (colName.equals("emailTemplateType"))     {fc.setColumnSearchKey(FolderColumn.COLUMN_EMAIL_TEMPLATE_TYPE);                                                                      }
			else if (colName.equals("family"))                {fc.setColumnSearchKey(Constants.FAMILY_FIELD);                    fc.setColumnSortable(false);                                       }
			else if (colName.equals("fullName"))              {fc.setColumnSearchKey(Constants.PRINCIPAL_FIELD);                 fc.setColumnSortKey(Constants.SORT_TITLE_FIELD);                   }
			else if (colName.equals("guest"))                 {fc.setColumnSearchKey(Constants.PRINCIPAL_FIELD);                 fc.setColumnSortKey(Constants.SORT_CREATOR_TITLE_FIELD);           }
			else if (colName.equals("html"))                  {fc.setColumnSearchKey(Constants.FILE_ID_FIELD);                                                                                      }
			else if (colName.equals("limitedVisibilityUser")) {fc.setColumnSearchKey(FolderColumn.COLUMN_LIMITED_VISIBILITY_USER);                                                                  }
			else if (colName.equals("location"))              {fc.setColumnSearchKey(Constants.PRE_DELETED_FIELD);                                                                                  }
			else if (colName.equals("loginId"))               {fc.setColumnSearchKey(Constants.LOGINNAME_FIELD);                                                                                    }
			else if (colName.equals("mobileDevices"))         {fc.setColumnSearchKey(FolderColumn.COLUMN_MOBILE_DEVICES);        fc.setColumnSortable(false);                                       }
			else if (colName.equals("netfolder_access"))      {fc.setColumnSearchKey(FolderColumn.COLUMN_NETFOLDER_ACCESS);      fc.setColumnSortable(false);                                       }
			else if (colName.equals("number"))                {fc.setColumnSearchKey(Constants.DOCNUMBER_FIELD);                 fc.setColumnSortKey(Constants.SORTNUMBER_FIELD);                   }
			else if (colName.equals("principalType"))         {fc.setColumnSearchKey(Constants.IDENTITY_INTERNAL_FIELD);         fc.setColumnSortKey(Constants.IDENTITY_INTERNAL_FIELD);            }
			else if (colName.equals("proxyName"))             {fc.setColumnSearchKey(FolderColumn.COLUMN_PROXY_NAME);            fc.setColumnSortKey(ObjectKeys.FIELD_PROXY_IDENTITY_NAME);         }
			else if (colName.equals("proxyTitle"))            {fc.setColumnSearchKey(FolderColumn.COLUMN_PROXY_TITLE);           fc.setColumnSortKey(ObjectKeys.FIELD_PROXY_IDENTITY_TITLE);        }
			else if (colName.equals("rating"))                {fc.setColumnSearchKey(Constants.RATING_FIELD);                                                                                       }
			else if (colName.equals("responsible"))           {fc.setColumnSearchKey(Constants.RESPONSIBLE_FIELD);                                                                                  }
			else if (colName.equals("size"))                  {fc.setColumnSearchKey(Constants.FILE_SIZE_FIELD);                                                                                    }
			else if (colName.equals("share_access"))          {fc.setColumnSearchKey(FolderColumn.COLUMN_SHARE_ACCESS);                                                                             }
			else if (colName.equals("share_date"))            {fc.setColumnSearchKey(FolderColumn.COLUMN_SHARE_DATE);                                                                               }
			else if (colName.equals("share_expiration"))      {fc.setColumnSearchKey(FolderColumn.COLUMN_SHARE_EXPIRATION);                                                                         }
			else if (colName.equals("share_message"))         {fc.setColumnSearchKey(FolderColumn.COLUMN_SHARE_MESSAGE);                                                                            }
			else if (colName.equals("share_sharedBy"))        {fc.setColumnSearchKey(FolderColumn.COLUMN_SHARE_SHARED_BY);                                                                          }
			else if (colName.equals("share_sharedWith"))      {fc.setColumnSearchKey(FolderColumn.COLUMN_SHARE_SHARED_WITH);                                                                        }
			else if (colName.equals("state"))                 {fc.setColumnSearchKey(Constants.WORKFLOW_STATE_CAPTION_FIELD);    fc.setColumnSortKey(Constants.WORKFLOW_STATE_FIELD);               }
			else if (colName.equals("status"))                {fc.setColumnSearchKey(Constants.STATUS_FIELD);                                                                                       }
			else if (colName.equals("tasks"))                 {fc.setColumnSearchKey(Constants.TASKS_FIELD);                                                                                        }
			else if (colName.equals("teamMembers"))           {fc.setColumnSearchKey(FolderColumn.COLUMN_TEAM_MEMBERS);          fc.setColumnSortable(false);                                       }
			else if (colName.equals("title"))                 {fc.setColumnSearchKey(Constants.TITLE_FIELD);                     fc.setColumnSortKey(Constants.SORT_TITLE_FIELD);                   }
			else if (colName.equals("adminRights"))           {
				fc.setColumnSearchKey(FolderColumn.COLUMN_ADMIN_RIGHTS);
				if (isManageAdmins)
				     fc.setColumnSortKey(Constants.SORT_CREATOR_TITLE_FIELD);
				else fc.setColumnSortable(false);
			}
			else {
				// Does the column name contain multiple parts wrapped
				// in a single value?
				String defId      = null;
				String eleType    = null;
				String eleName    = null;
				String eleCaption = null;
				if (colName.contains(",")) {
					String[] temp = colName.split(",");
					if (4 <= temp.length) {
						defId   = temp[0];
						eleType = temp[1];
						eleName = temp[2];
						
						// Since the caption may have commas in it, we
						// need to get everything past the third comma.
						eleCaption = colName.substring(colName.indexOf(",")+1);
						eleCaption = eleCaption.substring(eleCaption.indexOf(",")+1);
						eleCaption = eleCaption.substring(eleCaption.indexOf(",")+1);
					}
				}
				if (MiscUtil.hasString(defId)) {
					// Yes!  Update the FolderColumn components based
					// on the information extracted from the field.
					fc.setColumnDefId(  defId  );
					fc.setColumnType(   eleType);
					fc.setColumnEleName(eleName);
					
					if (!(MiscUtil.hasString(fc.getColumnTitle()))) {
						fc.setColumnTitle(eleCaption);
					}
					
					String eleSortName;
					if      (eleType.equals("selectbox") || eleType.equals("radio"))  eleSortName = ("_caption_" + eleName);
					else if (eleType.equals("text")      || eleType.equals("hidden")) eleSortName = ("_sort_"    + eleName);
					else if (eleType.equals("event"))                                 eleSortName = (eleName + "#LogicalStartDate");
					else                                                              eleSortName = eleName;
					fc.setColumnSortKey(eleSortName);
				} 
				
				else {
					// No, the name doesn't have multiple parts wrapped
					// in a single value!  Just use the name for the
					// search and sort keys.
					fc.setColumnSearchKey(colName);
					fc.setColumnEleName(  colName);	//If not a custom attribute, the element name is the colName
				}
			}

			// If we're dealing with a trash view...
			if (isTrash) {
				// ...some of the fields are managed using a different search key. 
				if      (colName.equals("author"))   {fc.setColumnSearchKey(Constants.PRE_DELETED_BY_ID_FIELD); fc.setColumnSortKey("");}
				else if (colName.equals("date"))     {fc.setColumnSearchKey(Constants.PRE_DELETED_WHEN_FIELD);  fc.setColumnSortKey("");}
				else if (colName.equals("location")) {fc.setColumnSearchKey(Constants.PRE_DELETED_FROM_FIELD);  fc.setColumnSortKey("");}
			}
		}
	}

	/*
	 * Constructs a List<AssignmentInfo>'s for the team membership of a
	 * Binder.
	 */
	private static List<AssignmentInfo> getAIListForTeamMembership(AllModulesInjected bs, Long binderId) throws GwtTeamingException {
		// Allocate the List<AssignmentInfo>'s to return.
		ArrayList<AssignmentInfo> reply = new ArrayList<AssignmentInfo>();

		// Does the binder have a team group?
		Binder binder = bs.getBinderModule().getBinder(binderId);
		Long teamGroupId = binder.getTeamGroupId();
		if (null != teamGroupId) {
			// Yes!  Does the team group containing any members?
			ArrayList<GwtTeamingItem> groupMembers = new ArrayList<GwtTeamingItem>();
			int count = GwtServerHelper.getGroupMembership(
				bs,
				groupMembers,
				String.valueOf(teamGroupId),
				0,
				Integer.MAX_VALUE,
				MembershipFilter.RETRIEVE_ALL_MEMBERS);
	
			if (0 < count) {
				// Yes!  Scan them...
				for (GwtTeamingItem member:  groupMembers) {
					// ...adding an appropriate AssignmentInfo to the
					// ...list for users and nested groups.
					AssignmentInfo ai;
					if (member instanceof GwtUser) {
						GwtUser user = ((GwtUser) member);
						ai = AssignmentInfo.construct(user.getIdLong(), AssigneeType.INDIVIDUAL);
					}
					else if (member instanceof GwtGroup) {
						GwtGroup group = ((GwtGroup) member); 
						ai = AssignmentInfo.construct(group.getIdLong(), AssigneeType.GROUP);
					}
					
					else {
						continue;
					}
					reply.add(ai); 
				}
			}
		}

		// If we get here, reply refers to a List<AssignmentInfo> for
		// the members of group.  Return it.
		return reply;
	}
	
	/*
	 * Returns a non-null List<AssignmentInfo> from a folder row for a
	 * given attribute.
	 */
	private static List<AssignmentInfo> getAIListFromFR(FolderRow fr, String attrName) {
		Map<String, List<AssignmentInfo>> aiMap = fr.getRowAssigneeInfoListsMap();
		List<AssignmentInfo> reply = aiMap.get(attrName.toLowerCase());
		return ((null == reply) ? new ArrayList<AssignmentInfo>() : reply);
	}

	/*
	 * Returns a non-null List<AssignmentInfo> build from a
	 * sharer's ID.
	 */
	private static List<AssignmentInfo> getAIListFromRecipients(List<GwtPerShareInfo> perShareInfos) {
		// Allocate a List<AssignmentInfo> to return.
		List<AssignmentInfo> reply = new ArrayList<AssignmentInfo>();

		// Scan the shares...
		for (GwtPerShareInfo psi:  perShareInfos) {
			// ...creating an AssignmentInfo for each recipient.
			AssigneeType assigneeType;
			if (psi.isRecipientPublic()) {
				assigneeType = AssigneeType.PUBLIC;
			}
			else {
				switch (psi.getRecipientType()) {
				default:          assigneeType = null;                     continue;
				case user:        assigneeType = AssigneeType.INDIVIDUAL;  break;
				case group:       assigneeType = AssigneeType.GROUP;       break;
				case publicLink:  assigneeType = AssigneeType.PUBLIC_LINK; break;
				case team:        assigneeType = AssigneeType.TEAM;        break;
				}
			}
			AssignmentInfo ai = AssignmentInfo.construct(
				psi.getRecipientId(),
				assigneeType);
			reply.add(ai);
		}

		// If we get here, reply refers to the List<AssignmentInfo>
		// corresponding to the given List<GwtPerShareInfo>.  Return
		// it.
		return reply;
	}
	
	/*
	 * Returns a non-null List<AssignmentInfo> build from a
	 * sharer's ID.
	 */
	private static List<AssignmentInfo> getAIListFromSharers(List<GwtPerShareInfo> perShareInfos) {
		// Allocate a List<AssignmentInfo> to return.
		List<AssignmentInfo> reply = new ArrayList<AssignmentInfo>();

		// Scan the shares...
		for (GwtPerShareInfo psi:  perShareInfos) {
			// ...creating an AssignmentInfo for each sharer.
			AssignmentInfo ai = AssignmentInfo.construct(
				psi.getSharerId(),
				AssigneeType.INDIVIDUAL);
			reply.add(ai);
		}

		// If we get here, reply refers to the List<AssignmentInfo>
		// corresponding to the given sharer ID.  Return it.
		return reply;
	}
	
	/**
	 * Reads information for rendering a binder's description in a
	 * BinderDescriptionRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 */
	public static BinderDescriptionRpcResponseData getBinderDescription(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			// Access the BinderInfo for the binder's description
			// information...
			BinderInfo binderInfo = GwtServerHelper.getBinderInfo(bs, request, String.valueOf(binderId));

			// ...and use it to construct and return a
			// ...BinderDescriptionRpcResponseData object.
			return
				new BinderDescriptionRpcResponseData(
					binderInfo.getBinderDesc(),
					binderInfo.isBinderDescHTML(),
					binderInfo.isBinderDescExpanded());
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getBinderDescription( SOURCE EXCEPTION ):  ");
		}
	}

	/**
	 * Reads the current user's filters for a binder and returns them
	 * as a BinderFiltersRpcResponseData.
	 * 
	 * The algorithm used in this method was reverse engineered from
	 * that used by BinderHelper.getSearchFilter() and
	 * view_forum_user_filters.jsp.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BinderFiltersRpcResponseData getBinderFilters(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			Binder				binder = bs.getBinderModule().getBinderWithoutAccessCheck(binderId);  //Not giving any secrets away, so do it fast
			List<BinderFilter>	ffList = new ArrayList<BinderFilter>();
			User				user   = GwtServerHelper.getCurrentUser();

			// Are there any global filters defined?
			TreeMap<String, String>	filterMap = new TreeMap<String, String>(new StringComparator(user.getLocale()));
			Map searchFilters = ((Map) binder.getProperty(ObjectKeys.BINDER_PROPERTY_FILTERS));
			if (MiscUtil.hasItems(searchFilters)) {
				// Yes!  Add them to the sort map.
				Set<String> keySet = searchFilters.keySet();
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					filterMap.put(ksIT.next(), ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL);
				}
			}
			
			// Does the user have any personal filters defined?
			UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			searchFilters = ((Map) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_SEARCH_FILTERS));
			if (MiscUtil.hasItems(searchFilters)) {
				// Yes!  Add them to the sort map.
				Set<String> keySet = searchFilters.keySet();
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					filterMap.put(ksIT.next(), ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL);
				}
			}

			// Based on the binder type, define the appropriate action
			// to use to view this type of binder.
			String viewAction;
			String binderType = binder.getEntityType().name();
			if      (binderType.equals(EntityIdentifier.EntityType.folder.name()))    viewAction = WebKeys.ACTION_VIEW_FOLDER_LISTING;
			else if (binderType.equals(EntityIdentifier.EntityType.workspace.name())) viewAction = WebKeys.ACTION_VIEW_WS_LISTING;
			else if (binderType.equals(EntityIdentifier.EntityType.profiles.name()))  viewAction = WebKeys.ACTION_VIEW_PROFILE_LISTING;
			else {
				throw new IllegalArgumentException("Unknown binderType" + binderType);
			}
			
			// Did we find any filters?
			AdaptedPortletURL url;
			if (!(filterMap.isEmpty())) {
				// ...scan the sorted set...
				Set<String> keySet = filterMap.keySet();
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					// ...and add a BinderFilter for each to the list.
					String filterName  = ksIT.next();
					String filterScope = filterMap.get(filterName);
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,            viewAction               );
					url.setParameter(WebKeys.URL_BINDER_ID,     binder.getId().toString());
					url.setParameter(WebKeys.URL_OPERATION,     WebKeys.URL_SELECT_FILTER);
					url.setParameter(WebKeys.URL_OPERATION2,    filterScope              );
					url.setParameter(WebKeys.URL_SELECT_FILTER, filterName               );
					String filterAddUrl = url.toString();
					
					url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,           viewAction               );
					url.setParameter(WebKeys.URL_BINDER_ID,    binder.getId().toString());
					url.setParameter(WebKeys.URL_OPERATION,    WebKeys.URL_CLEAR_FILTER );
					url.setParameter(WebKeys.URL_OPERATION2,   filterScope              );
					url.setParameter(WebKeys.URL_CLEAR_FILTER, filterName               );
					String filterClearUrl = url.toString();
					
					ffList.add(
						new BinderFilter(
							filterName,
							filterScope,
							filterAddUrl,
							filterClearUrl));
				}
			}
			
			// Use the data we obtained to create a
			// BinderFiltersRpcResponseData.
			BinderFiltersRpcResponseData reply = new BinderFiltersRpcResponseData(ffList);
			
			// Store the current filters, if any, that the user
			// currently has selected on this binder.
			reply.setCurrentFilters(BinderHelper.getCurrentUserFilters(userBinderProperties, true));

			// Store a URL to turn off filtering on the binder.
			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,            viewAction               );
			url.setParameter(WebKeys.URL_BINDER_ID,     binder.getId().toString());
			url.setParameter(WebKeys.URL_OPERATION,     WebKeys.URL_SELECT_FILTER);
			url.setParameter(WebKeys.URL_SELECT_FILTER, ""                       );
			reply.setFiltersOffUrl(url.toString());
			
			// Store a URL to edit the filters on the binder.
			url = new AdaptedPortletURL(request, "ss_forum", true);
			url.setParameter(WebKeys.ACTION,          WebKeys.ACTION_BUILD_FILTER);
			url.setParameter(WebKeys.URL_BINDER_ID,   binder.getId().toString()  );
			url.setParameter(WebKeys.URL_BINDER_TYPE, binderType                 );
			reply.setFilterEditUrl(url.toString());
			
			// Finally, return the BinderFiltersRpcResponseData we just
			// constructed.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getBinderFilters( SOURCE EXCEPTION ):  ");
		}
	}

	/**
	 * Collects information about other components in a binder view and
	 * returns it in an HasOtherComponentsRpcResponseData object.
	 * 
	 * @param bs
	 * @param request
	 * @param binderInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static HasOtherComponentsRpcResponseData getBinderHasOtherComponents(AllModulesInjected bs, HttpServletRequest request, BinderInfo binderInfo) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getBinderHasOtherComponents()");
		try {
			Binder binder = bs.getBinderModule().getBinder(binderInfo.getBinderIdAsLong());
			boolean showUserList    = ((binder instanceof Folder) ? GwtUserListHelper.getFolderHasUserList((Folder) binder) : false);
			boolean showHtmlElement = GwtHtmlElementHelper.getBinderHasHtmlElement(binder);
			return new HasOtherComponentsRpcResponseData(showUserList, showHtmlElement);
		}
		
		catch (Exception e) {
			// Log the error and assume there are no user_list's.
			GwtLogHelper.error(m_logger, "GwtViewHelper.getBinderHasOtherComponents( SOURCE EXCEPTION ):  ", e);
			return new HasOtherComponentsRpcResponseData(false, false);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Returns an AvatarInfoRpcResponseData object containing the
	 * information about a binder owner's avatar.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static AvatarInfoRpcResponseData getBinderOwnerAvatarInfo(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			// Construct a GuestInfo from the binder's owner...
			Binder binder = bs.getBinderModule().getBinder(Long.valueOf(binderId), Boolean.TRUE);
			Principal p = binder.getCreation().getPrincipal(); //creator is user
			GuestInfo gi = getGuestInfoFromPrincipal(bs, request, Utils.fixProxy(p));
			
			// ...and use that to construct an AvatarInfoRpcResponseData.
			AvatarInfoRpcResponseData reply = new AvatarInfoRpcResponseData(gi);

			// If we get here, reply refers to the
			// AvatarInfoRpcResponseData for the binder's owner.
			// Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getBinderOwnerAvatarInfo( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Reads the current user's region state for a binder and returns
	 * it as a StringRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param regionId
	 * 
	 * @return
	 */
	public static StringRpcResponseData getBinderRegionState(AllModulesInjected bs, HttpServletRequest request, Long binderId, String regionId) throws GwtTeamingException {
		try {
			// Does the user have this region state defined?
			UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUserId(), binderId);
			String regionState;
			if (null == userBinderProperties)
			     regionState = null;
			else regionState = ((String) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_REGION_VIEW + "." + regionId));

			// Use the data we obtained to create a
			// StringRpcResponseData and return it.
			return new StringRpcResponseData(MiscUtil.hasString(regionState) ? regionState : "expanded");
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getBinderRegionState( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Returns the requested List<ShareItem> removing those that are
	 * not the latest, have been deleted and those that are expired
	 * when requested.
	 */
	private static List<ShareItem> getCleanShareList(SharingModule sm, ShareItemSelectSpec spec, boolean removeExpired) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getCleanShareList()");
		try {
			// Query the shares...
			List<ShareItem> reply = sm.getShareItems(spec);
			int c = ((null == reply) ? 0 : reply.size());
			
			// ...and scan them.
			int droppedDeleted   = 0;
			int droppedExpired   = 0;
			int droppedNotLatest = 0;
			for (int i = (c - 1); i >= 0; i -= 1) {
				// Should this share be returned?
				ShareItem si = reply.get(i);
				if ((!(si.isLatest())) || si.isDeleted() || (si.isExpired() && removeExpired)) {
					// No!  Remove it from the list...
					reply.remove(i);
					
					// ...and count it.
					if    (!(si.isLatest()))                  droppedNotLatest += 1;
					else if (si.isDeleted())                  droppedDeleted   += 1;
					else if (si.isExpired() && removeExpired) droppedExpired   += 1;
				}
			}
	
			// If we're debug logging...
			if (m_logger.isDebugEnabled()) {
				// ...trace how many we've dropped.
				m_logger.debug("GwtViewHelper.getCleanShareList( DROPS ):  Not latest=" + droppedNotLatest + ", Deleted=" + droppedDeleted + ", Expired=" + droppedExpired);
			}
			
			// If we get here, reply refers to the cleaned list.  Return
			// it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns a String[] of the names of the columns to display in a
	 * given collection type.
	 */
	private static String[] getCollectionColumnNames(CollectionType ct) {
		String[] reply;
		switch (ct) {
		case MY_FILES:        reply = pruneColumnNames(ct, "title", "comments", "size",   "date"                                                                     ); break;
		case NET_FOLDERS:     reply = pruneColumnNames(ct, "title", "date",     "netfolder_access", "descriptionHtml"                                                ); break;
		case SHARED_BY_ME:    reply = pruneColumnNames(ct, "title", "comments", "share_sharedWith", "share_date", "share_expiration", "share_access", "share_message"); break;
		case SHARED_PUBLIC:
		case SHARED_WITH_ME:  reply = pruneColumnNames(ct, "title", "comments", "share_sharedBy",   "share_date", "share_expiration", "share_access", "share_message"); break;
		default:              reply = new String[0];                                                                                                                    break;
		}
		return reply;
	}

	/*
	 * Returns the entries for the given collection.
	 */
	@SuppressWarnings("unchecked")
	private static Map getCollectionEntries(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options, CollectionType ct, List<GwtSharedMeItem> shareItems) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getCollectionEntries()");
		try {
			// Construct the search Criteria...
			Criteria crit;
			switch (ct) {
			default:
			case MY_FILES:
				// Use the common criteria builder for My Files.
	            crit = SearchUtils.getMyFilesSearchCriteria(bs, binder.getId());
				break;
	
			case NET_FOLDERS:
				// Create the criteria for top level mirrored file
				// folders that have been configured.
				crit = SearchUtils.getNetFoldersSearchCriteria(bs, false);	// false -> Don't default to the top workspace.
				
				// Factor in any quick filter we've got.
				addQuickFilterToCriteria(quickFilter, crit);
	
				// Add in the sort information...
				boolean sortAscend = (!(GwtUIHelper.getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false                   )));
				String  sortBy     =    GwtUIHelper.getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      Constants.SORT_TITLE_FIELD);
				crit.addOrder(new Order(Constants.ENTITY_FIELD, sortAscend));
				crit.addOrder(new Order(sortBy,                 sortAscend));
				
				// ...and issue the query and return the entries.
				Map netFolderResults = bs.getBinderModule().searchFolderOneLevelWithInferredAccess(
					crit,
					Constants.SEARCH_MODE_SELF_CONTAINED_ONLY,
					GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_OFFSET,   0),
					GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS),
					SearchUtils.getNetFoldersRootBinder());
				
				// Remove any results where the current user does not
				// have AllowNetFolderAccess rights
				SearchUtils.removeNetFoldersWithNoRootAccess(netFolderResults);
				return netFolderResults;
				
			case SHARED_BY_ME:
			case SHARED_WITH_ME:
			case SHARED_PUBLIC:
				// Do we have any shares to analyze?
				if (!(MiscUtil.hasItems(shareItems))) {
					// No!  Bail.
					return buildEmptyEntryMap();
				}
				
				// Scan the items that have been shared by/with the
				// current user...
				List<String>	sharedBinderIds = new ArrayList<String>();
				List<String>	sharedEntryIds  = new ArrayList<String>();
				for (GwtSharedMeItem si:  shareItems) {
					// ...tracking each as a binder or entry.
					DefinableEntity	entity   = si.getEntity();
					String			entityId = String.valueOf(entity.getId());
					if (entity.getEntityType().equals(EntityType.folderEntry))
					     sharedEntryIds.add( entityId);
					else sharedBinderIds.add(entityId);
				}
				
				// Do we have any binders or entries that have been
				// shared by/with the current user?
				boolean hasSharedBinders = (!(sharedBinderIds.isEmpty()));
				boolean hasSharedEntries = (!(sharedEntryIds.isEmpty()));
				if ((!hasSharedBinders) && (!hasSharedEntries)) {
					// No!  Bail.
					return buildEmptyEntryMap();
				}
				
				return
					buildSearchMapFromSharedMeList(
						bs,
						shareItems,
						GwtUIHelper.getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false),
						GwtUIHelper.getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      Constants.SORT_TITLE_FIELD));
			}
	
			// Factor in any quick filter we've got.
			addQuickFilterToCriteria(quickFilter, crit);
	
			// Add in the sort information...
			boolean sortAscend = (!(GwtUIHelper.getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false                   )));
			String  sortBy     =    GwtUIHelper.getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      Constants.SORT_TITLE_FIELD);
			crit.addOrder(new Order(Constants.ENTITY_FIELD, sortAscend));
			crit.addOrder(new Order(sortBy,                 sortAscend));
			
			// ...and issue the query and return the entries.
			return
				bs.getBinderModule().executeSearchQuery(
					crit,
					Constants.SEARCH_MODE_NORMAL,
					GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_OFFSET,   0),
					GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS),
					null);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns a LinkedHashMap of the column names from a String[]
	 * of them.
	 */
	@SuppressWarnings("unchecked")
	private static Map getColumnsLHMFromAS(String[] columnNames) {
		Map reply = new LinkedHashMap();
		for (String columnName:  columnNames) {
			reply.put(columnName, columnName);
		}
		return reply;
	}

	/**
	 * Returns the column widths for a user on a folder.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static ColumnWidthsRpcResponseData getColumnWidths(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) throws GwtTeamingException {
		try {
			// Read the column widths stored in the folder properties...
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderInfo.getBinderIdAsLong());
			String          propKey              = (ObjectKeys.USER_PROPERTY_COLUMN_WIDTHS + (folderInfo.isBinderTrash() ? ".Trash" : ""));
			Map<String, String> columnWidths = ((Map<String, String>) userFolderProperties.getProperty(propKey));
			if ((null != columnWidths) && columnWidths.isEmpty()) {
				columnWidths = null;
			}
			
			// ...and return a ColumnWidthsRpcResponseData containing
			// ...them.
			return new ColumnWidthsRpcResponseData(columnWidths);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getColumnWidths( SOURCE EXCEPTION ):  ");
		}
	}

	/**
	 * Returns the comment count for the given entry.
	 * 
	 * @param bs
	 * @param request
	 * @param entityId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static IntegerRpcResponseData getCommentCount(AllModulesInjected bs, HttpServletRequest request, EntityId entityId) throws GwtTeamingException {
		try {
			// Is the entity a folder entry?
			int commentCount = (-1);
			if ((null != entityId) && entityId.isFolderEntry()) {
				// Yes!  Extract its comment count...
				FolderEntry fe = bs.getFolderModule().getEntry(entityId.getBinderId(), entityId.getEntityId());
				commentCount = fe.getReplyCount();
			}
			
			// ...and return it wrapped in an IntegerRpcResponseData.
			return new IntegerRpcResponseData(commentCount);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getCommentCount( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Use Spring to access a CoreDao object. 
	 */
	private static CoreDao getCoreDao() {
		return ((CoreDao) SpringContextUtil.getBean("coreDao"));
	}
	
	/**
	 * Returns a DownloadFolderAsCSVFileUrlRcpResponseData object
	 * containing the URL to use to download the listed files or all
	 * the files from a folder in a zip.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param csvDelim
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static DownloadFolderAsCSVFileUrlRpcResponseData getDownloadFolderAsCSVFileUrl(AllModulesInjected bs, HttpServletRequest request, Long folderId, String csvDelim) throws GwtTeamingException {
		try {
			// Allocate a DownloadFolderAsCSVFileUrlRpcResponseData to
			// return the URL to request downloading the folder as a
			// CSV file.
			DownloadFolderAsCSVFileUrlRpcResponseData reply = new DownloadFolderAsCSVFileUrlRpcResponseData();

			// Generate a URL to download the folder as a CSV file.
			String url = WebUrlUtil.getFolderAsCSVFileUrl(request, folderId, csvDelim);
			
			// Add whatever URL we built to the reply.
			reply.setUrl(url);
			
			// If we get here, reply refers to the
			// DownloadFolderAsCSVFileUrlRpcResponseData containing the
			// URL to download the requested folder as a CSV file.
			// Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getDownloadFolderAsCSVFileUrl( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Returns a Map<String, EntityRights> of the current users rights
	 * to the specified entities.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 */
	public static EntityRightsRpcResponseData getEntityRights(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getEntityRights()");
		try {
			EntityRightsRpcResponseData reply = new EntityRightsRpcResponseData();
			
			// If we don't have any entities whose rights are being
			// requested...
			if (!(MiscUtil.hasItems(entityIds))) {
				// ..bail.
				return reply;
			}
	
			List<Long> entryIds  = new ArrayList<Long>();
			List<Long> binderIds = new ArrayList<Long>();
			SimpleProfiler.start("GwtViewHelper.getEntityRights(Collect IDs)");
			try {
				// Collect the entity IDs of the entities from the
				// List<EntityIds>.
				for (EntityId eid:  entityIds) {
					Long id = eid.getEntityId();
					if (eid.isBinder())
					     binderIds.add(id);
					else entryIds.add( id);
				}
			}
			
			finally {
				SimpleProfiler.stop("GwtViewHelper.getEntityRights(Collect IDs)");
			}

			try {
				// Do we have any FolderEntry rights to query?
				User user = GwtServerHelper.getCurrentUser();
				if (!(entryIds.isEmpty())) {
					SimpleProfiler.start("GwtViewHelper.getEntityRights(Get entry rights)");
					try {
						// Yes!  Read the FolderEntry's for the rows...
						FolderModule fm = bs.getFolderModule();
						SortedSet<FolderEntry> entries = fm.getEntries(entryIds);
						
						// ...mapping each FolderEntry to its ID.
						Map<Long, FolderEntry> entryMap = new HashMap<Long, FolderEntry>();
						for (FolderEntry entry: entries) {
							entryMap.put(entry.getId(), entry);
						}
						
						// Scan the List<EntityId> again.
						for (EntityId eid:  entityIds) {
							// Skipping any binders.
							if (eid.isBinder()) {
								continue;
							}
							
							// Do we have the FolderEntry for this row?
							FolderEntry entry = entryMap.get(eid.getEntityId());
							if (null != entry) {
								// Yes!  Create the EntityRights for the
								// entry.  Is the entry in the trash? 
								EntityRights entryRights = new EntityRights();
								if (entry.isPreDeleted()) {
									// Yes!  Then the user can't
									// interact with it.
									entryRights.setShareRight(ShareRight.NOT_SHARABLE_RIGHTS_VIOLATION);
								}
								else {
									// No, the entry isn't in the
									// trash!  Determine the user's
									// rights to it.
									entryRights.setCanAddReplies( fm.testAccess(entry, FolderOperation.addReply      ));
									entryRights.setCanModify(     fm.testAccess(entry, FolderOperation.modifyEntry   ));
									entryRights.setCanPurge(      fm.testAccess(entry, FolderOperation.deleteEntry   ));
									entryRights.setCanTrash(      fm.testAccess(entry, FolderOperation.preDeleteEntry));
									entryRights.setCanSubscribe(GwtShareHelper.visibleWithoutShares(bs, user, entry));
									
									ShareRight entryShareRight;
									if (GwtShareHelper.isEntitySharable(bs, entry))
									     entryShareRight = ShareRight.SHARABLE;
									else entryShareRight = ShareRight.NOT_SHARABLE_RIGHTS_VIOLATION;
									entryRights.setShareRight(entryShareRight);
									entryRights.setCanPublicLink(GwtShareHelper.isEntityPublicLinkSharable(bs, entry));
								}
								
								reply.setEntityRights(eid, entryRights);
							}
						}
					}
					
					finally {
						SimpleProfiler.stop("GwtViewHelper.getEntityRights(Get entry rights)");
					}
				}
	
				// Do we have any Binder rights to query?
				if (!(binderIds.isEmpty())) {
					SimpleProfiler.start("GwtViewHelper.getEntityRights(Get binder rights)");
					try {
						// Yes!  Read the Binder's for the rows
						// (including those intermediate sub-binders
						// that might be inaccessible)...
						BinderModule bm = bs.getBinderModule();
						SortedSet<Binder> binders = bm.getBinders(binderIds, Boolean.FALSE);
			
						// ...mapping each Binder to its ID.
						Map<Long, Binder> binderMap = new HashMap<Long, Binder>();
						for (Binder binder:  binders) {
							binderMap.put(binder.getId(), binder);
						}
			
						// Scan the List<EntityId> again.
						for (EntityId eid:  entityIds) {
							// Skipping any entries.
							if (!(eid.isBinder())) {
								continue;
							}
			
							// Do we have the Binder for this row?
							Binder binder = binderMap.get(eid.getEntityId());
							if (null != binder) {
								// Yes!  Create the EntityRights for
								// the binder. 
								EntityRights binderRights = new EntityRights();
								binderRights.setCanModify(bm.testAccess(binder, BinderOperation.modifyBinder   ));
								binderRights.setCanPurge( bm.testAccess(binder, BinderOperation.deleteBinder   ));
								binderRights.setCanTrash( bm.testAccess(binder, BinderOperation.preDeleteBinder));
								binderRights.setCanSubscribe(GwtShareHelper.visibleWithoutShares(bs, user, binder));
								
								ShareRight binderShareRight;
								if (GwtShareHelper.isEntitySharable(bs, binder)) {
									binderShareRight = ShareRight.SHARABLE;
								}
								else {
									// Net folders outside of a Home
									// folder are never sharable.
									// Otherwise, its a rights
									// violation.
									if ((binder instanceof Folder) && binder.isAclExternallyControlled()) {
										Folder  folder    = ((Folder) binder);
										Folder  topFolder = folder.getTopFolder();
										boolean isHome    = (folder.isHomeDir() || ((null != topFolder) && topFolder.isHomeDir()));
										if (isHome)
										     binderShareRight = ShareRight.NOT_SHARABLE_RIGHTS_VIOLATION;
										else binderShareRight = ShareRight.NOT_SHARABLE_NET_FOLDER;
									}
									else {
										binderShareRight = ShareRight.NOT_SHARABLE_RIGHTS_VIOLATION;
									}
								}
								binderRights.setShareRight(   binderShareRight);
								binderRights.setCanPublicLink(false           );	// Public links to a binder are not supported.
								
								reply.setEntityRights(eid, binderRights);
							}
						}
					}
					
					finally {
						SimpleProfiler.stop("GwtViewHelper.getEntityRights(Get binder rights)");
					}
				}
			}
			
			catch (Exception ex) {/* Ignored. */}
			
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getEntityRights( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Returns the EntityRights for an individual entity.
	 */
	private static EntityRights getEntityRights(AllModulesInjected bs, HttpServletRequest request, EntityId eid) throws GwtTeamingException {
		List<EntityId> eidList = new ArrayList<EntityId>();
		eidList.add(eid);
		EntityRightsRpcResponseData erData = getEntityRights(bs, request, eidList);
		return erData.getEntityRights(eid);
	}
	
	/**
	 * Returns the entry description from a search results Map.
	 * 
	 * @param httpReq
	 * @param entryMap
	 * @param de
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getEntryDescriptionFromMap(HttpServletRequest httpReq, Map entryMap, DefinableEntity de) {
		String reply = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DESC_FIELD);
		if (MiscUtil.hasString(reply)) {
			if (null == de)
			     reply = MarkupUtil.markupStringReplacement(null, null, httpReq, null, entryMap, reply, WebKeys.MARKUP_VIEW, false);
			else reply = MarkupUtil.markupStringReplacement(null, null, httpReq, null, de,       reply, WebKeys.MARKUP_VIEW       );
			reply = MarkupUtil.markupSectionsReplacement(reply);
		}
		return reply;
	}
	
	@SuppressWarnings("unchecked")
	public static String getEntryDescriptionFromMap(HttpServletRequest httpReq, Map entryMap) {
		// Always use the initial form of the method.
		return getEntryDescriptionFromMap(httpReq, entryMap, null);	// null -> No DefinableEntity.
	}

	/**
	 * Returns the collected entry types defined for a collection of binders.
	 *
	 * @param bs
	 * @param request
	 * @param entityId
	 * @param binderIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static EntryTypesRpcResponseData getEntryTypes(AllModulesInjected bs, HttpServletRequest request, EntityId entityId, List<Long> binderIds) throws GwtTeamingException {
		try {
			// Allocate an EntryTypesRpcResponseData to track the entry
			// types for the requested binders.
			EntryTypesRpcResponseData reply = new EntryTypesRpcResponseData();
			
			// Scan the binders whose entry types are being requested.
			for (Long binderId:  binderIds) {
				// Scan this binder's entry definitions...
				SortedMap<String, Definition> binderDefs = DefinitionHelper.getAvailableDefinitions(binderId, Definition.FOLDER_ENTRY);
				for (String defKey:  binderDefs.keySet()) {
					// ...adding an EntryType for each unique one to
					// ...the reply.
					Definition binderDef = binderDefs.get(defKey);
					String defId = binderDef.getId();
					if (!(reply.isEntryTypeInList(defId))) {
						boolean localDef = ((-1) != binderDef.getBinderId());
						EntryType et = new EntryType(defId, defKey, localDef);
						reply.addEntryType(et);
					}
				}
			}

			// Was the entry type of a specific entry requested?
			if (null != entityId) {
				// Yes!  Get its definition ID...
				FolderEntry fe = bs.getFolderModule().getEntry(entityId.getBinderId(), entityId.getEntityId());
				String feDefId = fe.getEntryDefId();
				
				// ...can the definition IDs we found...
				for (EntryType et:  reply.getEntryTypes()) {
					// ...and when one matches...
					if (feDefId.equals(et.getDefId())) {
						// ...use its EntryType for the requested entry.
						reply.setBaseEntryType( et           );
						reply.setBaseEntryTitle(fe.getTitle());
						break;
					}
				}
			}

			// If we get here, reply refers to the
			// EntryTypesRpcResponseData of the entry types for the
			// requested binders.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getEntryTypes( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Returns a String[] of the names of the columns to display in a
	 * given folder type.
	 * 
	 * Uses the default as setup in folder_column_defaults.jsp.
	 */
	private static String[] getFolderColumnNames(FolderType ft) {
		String[] reply;
		switch (ft) {
		case FILE:
		case MIRROREDFILE:  reply = pruneColumnNames(ft, "title",  "comments",    "size",     "state",  "author", "date"           ); break;
		case GUESTBOOK:     reply = pruneColumnNames(ft, "guest",  "title",       "date",     "descriptionHtml"                    ); break;
		case MILESTONE:     reply = pruneColumnNames(ft, "title",  "responsible", "tasks",    "status", "dueDate"                  ); break;
		case MINIBLOG:      reply = pruneColumnNames(ft, "title",  "description"                                                   ); break;
		case SURVEY:        reply = pruneColumnNames(ft, "title",  "author",      "dueDate"                                        ); break;
		default:            reply = pruneColumnNames(ft, "docNum", "title",       "comments", "state",  "author", "date",  "rating"); break;
		}
		return reply;
	}
	
	/**
	 * Reads the current user's columns for a folder and returns them
	 * as a FolderColumnsRpcResponseData.
	 * 
	 * The algorithm used in this method was reverse engineered from
	 * that used by folder_view_common2.jsp or view_trash.jsp.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param includeConfigurationInfo
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FolderColumnsRpcResponseData getFolderColumns(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, Boolean includeConfigurationInfo) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getFolderColumns()");
		try {
			Long			folderId             = folderInfo.getBinderIdAsLong();
			Binder			binder               = bs.getBinderModule().getBinder(folderId);
			Folder			folder               = ((binder instanceof Folder) ? ((Folder) binder) : null);
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			
			Map    columnNames;
			Map    columnTitles      = null;
			List<String> columnOrderList = null;
			String columnOrderString = null;
			List columnsAll = new ArrayList();

			// Are we showing the trash on this folder?
			String baseNameKey;
			CollectionType collectionType = folderInfo.getCollectionType();
			boolean        isLimitUserVisibility = folderInfo.isBinderLimitUserVisibility();
			boolean        isManageAdmins        = folderInfo.isBinderAdministratorManagement();
			boolean        isMobileDevicesView   = folderInfo.isBinderMobileDevices();
			boolean        isProxyIdentitiesView = folderInfo.isBinderProxyIdentities();
			boolean        isEmailTemplatesView  = folderInfo.isBinderEmailTemplates();
			boolean        isFileFolder          = ((null != folder) && GwtServerHelper.isFamilyFile(GwtServerHelper.getFolderEntityFamily(bs, folder)));
			boolean        isCollection          = folderInfo.isBinderCollection();
			boolean        isTrash               = folderInfo.isBinderTrash();
			if (isTrash) {
				// Yes!  The columns in a trash view are not
				// configurable.  Use the default trash columns.
				baseNameKey = "trash.column.";
				columnNames = getColumnsLHMFromAS(TrashHelper.trashColumns);
			}

			// No, we aren't showing the trash on this folder!  Are we
			// looking at mobile devices view? 
			else if (isMobileDevicesView) {
				// Yes!
				baseNameKey = "mobileDevice.column.";
				if (folderInfo.getMobileDevicesViewSpec().isSystem())
				     columnNames = getColumnsLHMFromAS(new String[]{"deviceDescription", "deviceUser", "deviceLastLogin", "deviceWipeScheduled", "deviceWipeDate"});
				else columnNames = getColumnsLHMFromAS(new String[]{"deviceDescription",               "deviceLastLogin", "deviceWipeScheduled", "deviceWipeDate"});
			}
			
			// No, we aren't showing a mobile devices view either!  Are
			// we looking at proxy identities view? 
			else if (isProxyIdentitiesView) {
				// Yes!
				baseNameKey = "proxyIdentities.column.";
				columnNames = getColumnsLHMFromAS(new String[]{"proxyTitle", "proxyName"});
			}
			
			// No, we aren't showing a proxy identities view either!
			// Are we looking at an email templates view? 
			else if (isEmailTemplatesView) {
				// Yes!
				baseNameKey = "emailTemplates.column.";
				columnNames = getColumnsLHMFromAS(new String[]{"emailTemplateName", "emailTemplateType"});
			}
			
			// No, we aren't showing an email templates view either!
			// Are we looking at the root binder in limit user
			// visibility mode?
			else if (isLimitUserVisibility) {
				baseNameKey = "limitUserVisibility.column.";
				columnNames = getColumnsLHMFromAS(new String[]{"limitedVisibilityUser", "principalType", "canOnlySeeMembers"});
			}
			
			// No, we aren't showing the limit user visibility view
			// either!  Are we looking at the root profiles binder in
			// administrators mode?
			else if (isManageAdmins) {
				baseNameKey = "administrators.column.";
				columnNames = getColumnsLHMFromAS(new String[]{"administrator", "principalType", "adminRights", "emailAddress", "loginId"});
			}
			
			// No, we aren't showing an administrators view either!
			// Are we looking at the root profiles binder? 
			else if (folderInfo.isBinderProfilesRootWS()) {
				// Yes!
				baseNameKey = "profiles.column.";
				if (folderInfo.isBinderProfilesRootWSManagement()) {
					if (ReleaseInfo.isLicenseRequiredEdition() && LicenseChecker.showFilrFeatures())
					     columnNames = getColumnsLHMFromAS(new String[]{"fullName", "principalType", "adminRights", "emailAddress", "mobileDevices", "loginId"});
					else columnNames = getColumnsLHMFromAS(new String[]{"fullName", "principalType", "adminRights", "emailAddress",                  "loginId"});
				}
				else {
					columnNames = getColumnsLHMFromAS(new String[]{"fullName", "emailAddress", "loginId"});
				}
			}
			
			// No, we aren't showing the root profiles binder view
			// either!  Are we looking at the root team workspaces
			// binder? 
			else if (folderInfo.isBinderTeamsRootWS()) {
				// Yes!
				baseNameKey = "teams.column.";
				columnNames = getColumnsLHMFromAS(new String[]{"title", "teamMembers"});
			}
			
			// No, we aren't showing the root team workspaces binder
			// view either!  Are we looking at the root global
			// workspaces binder? 
			else if (folderInfo.isBinderGlobalRootWS()) {
				// Yes!
				baseNameKey = "globals.column.";
				columnNames = getColumnsLHMFromAS(new String[]{"title"});
			}
			
			// No, we aren't showing the root global workspaces binder
			// either!  Are we viewing a collection?
			else if (isCollection) {
				// Yes!  Generate the base key to use for accessing
				// column name in the string resource...
				baseNameKey = "collections.column.";
				switch (collectionType) {
				default:
				case MY_FILES:        baseNameKey += "myfiles.";      break;
				case NET_FOLDERS:     baseNameKey += "netfolders.";   break;
				case SHARED_BY_ME:    baseNameKey += "sharedByMe.";   break;
				case SHARED_WITH_ME:
				case SHARED_PUBLIC:   baseNameKey += "sharedWithMe."; break;
				}
				
				// ...and generate the column names for the collection.
				columnNames =
					getColumnsLHMFromAS(
						getCollectionColumnNames(
							collectionType));
			}
			
			else {
				// No, we aren't viewing a collection either!  If we
				// weren't given a folder...
				if (null == folder) {
					// ...we can't do anything with it.
            		throw
            			new GwtTeamingException(
            				GwtTeamingException.ExceptionType.FOLDER_EXPECTED,
            				"GwtViewHelper.getFolderColumns( *Internal Error* ):  The ID could not be resolved to a folder.");
				}
				
				// Are there user defined columns on this folder?
				FolderType folderType = folderInfo.getFolderType();
				switch (folderType) {
				case GUESTBOOK:  baseNameKey = "guestbook.column."; break;
				case MILESTONE:  baseNameKey = "milestone.";        break;
				case MINIBLOG:   baseNameKey = "miniblog.column.";  break;
				case SURVEY:     baseNameKey = "survey.";           break;
				default:         baseNameKey = "folder.column.";    break;
				}
				columnNames = ((Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS));
				if (null == columnNames) {
					// No!  Are there defaults stored on the binder?
					columnNames = ((Map) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS));
					if (null == columnNames) {
						// No!  Generate a list of default column names
						// for the folder.
						columnNames =
							getColumnsLHMFromAS(
								getFolderColumnNames(
									folderType));

						// Are we configuring columns for a file or
						// mirrored file folder in Vibe?
						if (includeConfigurationInfo && (!(Utils.checkIfFilr())) && (folderType.equals(FolderType.FILE) || folderType.equals(FolderType.MIRROREDFILE))) {
							// Yes!  Is the 'rating' column available?
							Set<String> keySet = columnNames.keySet();
							if (!(keySet.contains("rating"))) {
								// No!  Add it. 
								columnNames.put("rating", null);	// null -> The column is not currently visible.
							}
						}
					}
					
					else {
						// Yes, there are defaults from the binder!
						// Read and names and sort order from there as
						// well.
						columnTitles      = ((Map)    folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_TITLES    ));
						
						columnOrderList   = ((List<String>) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_SORT_ORDER_LIST));
						columnOrderString = ((String) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_SORT_ORDER));
					}
				}
				
				else {
					// Yes, there are user defined columns on the
					// folder!  Read and names and sort order from
					// there as well.
					columnTitles      = ((Map)    userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_TITLES    ));
					columnOrderList   = ((List<String>) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_SORT_ORDER_LIST));
					columnOrderString = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_SORT_ORDER));
				}
			}

			// If we don't have any column names...
			if (null == columnTitles) {
				// ...just use an empty map.
				columnTitles = new HashMap();
			}
			
			if (columnOrderList == null && MiscUtil.hasString(columnOrderString)) {
				//Convert the old format to the new format
				columnOrderList = new ArrayList<String>();
				String[] sortOrder = columnOrderString.split("\\|");
				for (String columnName:  sortOrder) {
					if (MiscUtil.hasString(columnName)) {
						columnOrderList.add(columnName);
					}
				}
			}
			
			// If we don't have any column sort order...
			if (columnOrderList == null) {
				columnOrderList = new ArrayList<String>();
				// ...define one based on the column names.
				Set<String> keySet = columnNames.keySet();
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					columnOrderList.add(ksIT.next());
				}
			}
			
			// ...and ensure all the columns are accounted for in it.
			Set<String> keySet = columnNames.keySet();
			for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
				String columnName = ksIT.next();
				if (!(columnOrderList.contains(columnName))) {
					columnOrderList.add(columnName);
				}
			}

			// If we get here, we've got all the data we need to define
			// the List<FolderColumn> for this folder.  Allocate the
			// list that we can fill from that data.
			List<FolderColumn> fcList = new ArrayList<FolderColumn>();
			List<FolderColumn> fcListAll = new ArrayList<FolderColumn>();
			for (String colName:  columnOrderList) {
				if (!columnsAll.contains(colName)) {
					FolderColumn fc = new FolderColumn(colName);
					if (colName.contains(",")) {
						String[] cnParts = colName.split(",");
						fc.setColumnDefId(cnParts[0]);
						fc.setColumnType(cnParts[1]);
						fc.setColumnEleName(cnParts[2]);
						String caption = colName.substring(colName.indexOf(",")+1);
						caption = caption.substring(caption.indexOf(",")+1);
						caption = caption.substring(caption.indexOf(",")+1);
						fc.setColumnDefaultTitle(caption);
					}
					// Is this column is not to be shown, skip it.
					// No!  Skip it.
					String columnValue = ((String) columnNames.get(colName));
					if (!(MiscUtil.hasString(columnValue)))
					     fc.setColumnShown(Boolean.FALSE);
					else fc.setColumnShown(Boolean.TRUE );
	
					// Is there a custom title for this column?
					String colTitle = ((String) columnTitles.get(colName));
					String colTitleDefault = "";
					if (!MiscUtil.hasString(fc.getColumnDefId())) {
						colTitleDefault = NLT.get(
							(baseNameKey + colName),	// Key to find the resource.
							colName,					// Default if not defined.
							true);						// true -> Silent.  Don't generate an error if undefined.
					} else {
						colTitleDefault = fc.getColumnDefaultTitle();
					}
					fc.setColumnDefaultTitle(colTitleDefault);
					if (!(MiscUtil.hasString(colTitle))) {
						// There is no custom title,  use the default.
						colTitle = colTitleDefault;
					} else {
						fc.setColumnCustomTitle(colTitle);
					}
					fc.setColumnTitle(colTitle);
	
					// Add a FolderColumn for this to the list we're
					// going to return.
					if (fc.isColumnShown()) {
						// This column is being shown.
						fcList.add(fc);
					}
					fcListAll.add(fc);
					columnsAll.add(colName);
				}
			}

			// Walk the List<FolderColumn>'s performing fixups on each
			// as necessary.
			fixupFCs(fcList, isTrash, isCollection, collectionType, isFileFolder, isManageAdmins);
			

			if (includeConfigurationInfo && (!isTrash) && (!isCollection)) {
				//Build a list of all possible columns
				Map<String,Definition> entryDefs = DefinitionHelper.getEntryDefsAsMap(((Folder) bs.getBinderModule().getBinder(folderId)));
				for (Definition def :  entryDefs.values()) {
					@SuppressWarnings("unused")
					Document defDoc = def.getDefinition();
					Map<String,Map> elementData = bs.getDefinitionModule().getEntryDefinitionElements(def.getId());
					for (Map.Entry me : elementData.entrySet()) {
						String eleName = (String)me.getKey();
						String type = (String)((Map)me.getValue()).get("type");
						String caption = (String)((Map)me.getValue()).get("caption");
						String colName = def.getId()+","+type+","+eleName+","+caption;
						if (!columnsAll.contains(colName)) {
							if (type.equals(    "selectbox"        ) ||
									type.equals("radio"            ) ||
									type.equals("checkbox"         ) ||
									type.equals("date"             ) ||
									type.equals("date_time"        ) ||
									type.equals("event"            ) ||
									type.equals("text"             ) ||
									type.equals("number"           ) ||
									type.equals("url"              ) ||
									type.equals("hidden"           ) ||
									type.equals("user_list"        ) ||
									type.equals("userListSelectbox")) {
								FolderColumn fc = new FolderColumn(colName);
								fc.setColumnDefId(def.getId());
								fc.setColumnType(type);
								// Is this column to be shown?
								String columnValue = ((String) columnNames.get(colName));
								if (!(MiscUtil.hasString(columnValue)))
								     fc.setColumnShown(Boolean.FALSE);
								else fc.setColumnShown(Boolean.TRUE );
			
								// Is there a custom title for this column?
								String colTitleDefault = (String)((Map)me.getValue()).get("caption");
								if (!(MiscUtil.hasString(fc.getColumnDefId()))) {
									colTitleDefault = NLT.get(
										(baseNameKey + colName),	// Key to find the resource.
										colTitleDefault,			// Default if not defined.
										true);						// true -> Silent.  Don't generate an error if undefined.
								}
								fc.setColumnDefaultTitle(colTitleDefault);
								String colTitle = (String) columnTitles.get(colName);
								if (!(MiscUtil.hasString(colTitle)))
								     // There is no custom title,  use the default.
								     colTitle = colTitleDefault;
								else fc.setColumnCustomTitle(colTitle);
								fc.setColumnTitle(colTitle);
			
								// Add a FolderColumn for this to the
								// list of all columns if it isn't
								// already there.
								fcListAll.add(fc);
								columnsAll.add(colName);
							}
						}
					}
				}
			}

			// Finally, use the data we obtained to create a
			// FolderColumnsRpcResponseData and return that. 
			FolderColumnsRpcResponseData reply = new FolderColumnsRpcResponseData(fcList, fcListAll);
			reply.setFolderAdmin(bs.getBinderModule().testAccess(binder, BinderOperation.manageConfiguration));
			
			// If we get here, reply refers to a
			// FolderRowsRpcResponseData containing the rows from the
			// requested binder.  Return it.
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				dumpFolderColumnsRpcResponseData(binder, reply);
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
					"GwtViewHelper.getFolderColumns( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}
	
	public static FolderColumnsRpcResponseData getFolderColumns(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) throws GwtTeamingException {
		// Always use the initial form of the method.
		return getFolderColumns(bs, request, folderInfo, Boolean.FALSE);
	}

	/**
	 * Reads the current user's display data for a folder and returns
	 * them as a FolderDisplayDataRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * 
	 * @return
	 */
	public static FolderDisplayDataRpcResponseData getFolderDisplayData(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getFolderDisplayData()");
		try {
			Long			folderId             = folderInfo.getBinderIdAsLong();
			User			user                 = GwtServerHelper.getCurrentUser();
			Long			userId               = user.getId();
			UserProperties	userProperties       = bs.getProfileModule().getUserProperties(userId);
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(userId, folderId);
			
			// Allow for collection sort information being stored on
			// the same binder.
			String propSortBy      = ObjectKeys.SEARCH_SORT_BY;
			String propSortDescend = ObjectKeys.SEARCH_SORT_DESCEND;
			if (folderInfo.isBinderCollection()) {
				String cName     = (".collection." + String.valueOf(folderInfo.getCollectionType().ordinal()));
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			else if (folderInfo.isBinderMobileDevices()) {
				String cName     = (".devices." + String.valueOf(folderInfo.getMobileDevicesViewSpec().getMode().ordinal()));
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			else if (folderInfo.isBinderProxyIdentities()) {
				String cName     = ".proxyIdentities";
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			else if (folderInfo.isBinderEmailTemplates()) {
				String cName     = ".emailTemplates";
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			else if (folderInfo.isBinderAdministratorManagement()) {
				String cName     = ".administrators.";
				propSortBy      += cName;
				propSortDescend += cName;
			}
			
			// How should the folder be sorted?
			String	sortBy = ((String) userFolderProperties.getProperty(propSortBy));
			boolean sortDescend;
			if (MiscUtil.hasString(sortBy)) {
				String sortDescendS = ((String) userFolderProperties.getProperty(propSortDescend));
				sortDescend = (("true").equalsIgnoreCase(sortDescendS));
			}
			else {
				sortDescend = false;
				if (folderInfo.isBinderAdministratorManagement()) {
					sortBy = FolderColumn.COLUMN_ADMIN_RIGHTS;
				}
				else if (folderInfo.isBinderProfilesRootWS() || folderInfo.isBinderGlobalRootWS() || folderInfo.isBinderTeamsRootWS() || folderInfo.isBinderCollection()) {
					sortBy = Constants.SORT_TITLE_FIELD;
				}
				else if (folderInfo.isBinderMobileDevices()) {
					sortBy = ObjectKeys.FIELD_MOBILE_DEVICE_DESCRIPTION;
				}
				else if (folderInfo.isBinderProxyIdentities()) {
					sortBy = ObjectKeys.FIELD_PROXY_IDENTITY_TITLE;
				}
				else if (folderInfo.isBinderEmailTemplates()) {
					sortBy = FolderColumn.COLUMN_EMAIL_TEMPLATE_NAME;
				}
				else {
					switch (folderInfo.getFolderType()) {
					case FILE:
					case MILESTONE:
					case MINIBLOG:
					case MIRROREDFILE:
					case SURVEY:     sortBy = Constants.SORT_TITLE_FIELD;                     break;
					case TASK:       sortBy = Constants.SORT_ORDER_FIELD;                     break;
					case GUESTBOOK:  sortBy = Constants.SORT_CREATOR_TITLE_FIELD;             break;
					default:         sortBy = Constants.SORTNUMBER_FIELD; sortDescend = true; break;
					}
				}
			}

			// How many entries per page should the folder display?
			int pageSize;
			try                  {pageSize = Integer.parseInt(MiscUtil.entriesPerPage(userProperties));}
			catch (Exception ex) {pageSize = 25;                                                       }
			
			// What's the action to take when a file link is activated?
			GwtFileLinkAction fla  = GwtServerHelper.gwtFileLinkActionFromFileLinkAction(
				AdminHelper.getEffectiveFileLinkAction(
					bs,
					user));
			
			// Has the user defined any column widths on this folder?
			ColumnWidthsRpcResponseData cwData = getColumnWidths(bs, request, folderInfo);

			// What do we know about pinning of entries on this folder?
			boolean folderSupportsPinning = getFolderSupportsPinning(folderInfo);
			boolean viewPinnedEntries     = (folderSupportsPinning && getUserViewPinnedEntries(request, folderId));

			// What do we know about the view state on the
			// 'Shared by/with Me' collections?
			boolean viewSharedFiles;
			CollectionType collectionType = folderInfo.getCollectionType();
			switch (collectionType) {
			case SHARED_BY_ME:
			case SHARED_WITH_ME:
			case SHARED_PUBLIC:  viewSharedFiles = getUserViewSharedFiles(request, collectionType); break;
			default:             viewSharedFiles = false;                                           break;
			}
			
			// Does the current user own this Binder?
			boolean folderOwnedByCurrentUser; 
			Binder binder;
			try {
				binder = bs.getBinderModule().getBinder(folderId);
				folderOwnedByCurrentUser = binder.getOwnerId().equals(userId);
			}
			catch (Exception ex) {
				binder = null;
				folderOwnedByCurrentUser = false;
			}
			
			// Is the Binder a Folder with a user list or a My Files
			// Storage folder? 
			boolean showUserList;
			boolean folderIsMyFilesStorage;
			if ((null != binder) && (binder instanceof Folder)) {
				showUserList           = (GwtUserListHelper.getFolderHasUserList((Folder) binder) && GwtUserListHelper.getUserListStatus(bs, request, folderId));
				folderIsMyFilesStorage = BinderHelper.isBinderMyFilesStorage(binder);
			}
			else {
				showUserList           =
				folderIsMyFilesStorage = false;
			}

			// Does the Binder have any HTML elements? 
			boolean showHtmlElement = (
				GwtHtmlElementHelper.getBinderHasHtmlElement(binder) &&
				GwtHtmlElementHelper.getHtmlElementStatus(bs, request, folderId));

			// Finally, use the data we obtained to create a
			// FolderDisplayDataRpcResponseData and return that. 
			return
				new FolderDisplayDataRpcResponseData(
					folderIsMyFilesStorage,
					sortBy,
					sortDescend,
					pageSize,
					cwData.getColumnWidths(),
					folderSupportsPinning,
					showUserList,
					showHtmlElement,
					viewPinnedEntries,
					viewSharedFiles,
					folderOwnedByCurrentUser,
					fla);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getFolderDisplayData( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns a count of the number of entries in the given folder.
	 */
	@SuppressWarnings("unchecked")
	private static int getFolderEntryCount(AllModulesInjected bs, Long folderId, boolean recursive) {
		// How many entries are in this folder?
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_OFFSET,   new Integer(0));
		options.put(ObjectKeys.SEARCH_MAX_HITS, new Integer(1));	// All we're looking for is the total.
		Map folderEntries = bs.getFolderModule().getEntries(folderId, options);
		int entryCount = ((Integer) folderEntries.get(ObjectKeys.SEARCH_COUNT_TOTAL));
		
		// Are we counting this recursively?
		if (recursive) {
			// Yes!  Get the the IDs of the sub-folders...
			List<String> folderIds = new ArrayList<String>();
			folderIds.add(String.valueOf(folderId));
			Criteria crit = new Criteria();
			crit.add(in(Constants.DOC_TYPE_FIELD, new String[] {Constants.DOC_TYPE_BINDER}))
				.add(in(Constants.BINDERS_PARENT_ID_FIELD, folderIds));
			crit.addOrder(Order.asc(Constants.BINDER_ID_FIELD));
			Map sfMap = bs.getBinderModule().executeSearchQuery(
				crit,
				org.kablink.util.search.Constants.SEARCH_MODE_SELF_CONTAINED_ONLY,
				0,
				(Integer.MAX_VALUE - 1),	// We process all the sub-folders regardless of how many.
				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.DOCID_FIELD));

			// ...scan the sub-folders...
			List sfMaps = ((List) sfMap.get(ObjectKeys.SEARCH_ENTRIES));
	      	for (Iterator iter = sfMaps.iterator(); iter.hasNext();) {
	      		// ...adding the files they contain to the count.
	      		Map nextSFMap = ((Map) iter.next());
      			entryCount += getFolderEntryCount(bs, Long.parseLong((String) nextSFMap.get(Constants.DOCID_FIELD)), recursive);
	      	}
		}

		// If we get here, fileCount contains the count of files in the folder.
		return entryCount;
	}
	
	/**
	 * Constructs and returns a FolderEntryDetails that wraps the given
	 * entityId.
	 * 
	 * @param bs
	 * @param request
	 * @param entityId
	 * @param markRead
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static FolderEntryDetails getFolderEntryDetails(AllModulesInjected bs, HttpServletRequest request, EntityId entityId, boolean markRead) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getFolderEntryDetails()");
		try {
			// Create the ViewFolderEntryInfo to return...
			User                user   = GwtServerHelper.getCurrentUser();
			Long				userId = user.getId();
			FolderEntryDetails	reply  = new FolderEntryDetails(
				entityId,
				getEntityRights(
					bs,
					request,
					entityId));
			
			// ...set whether the user has seen this entry...
			Long			folderId = entityId.getBinderId();
			FolderModule	fm       = bs.getFolderModule();
			FolderEntry 	fe       = fm.getEntry(folderId, entityId.getEntityId());
			ProfileModule	pm       = bs.getProfileModule();
			boolean			feSeen;
			if (user.isShared()) {
				feSeen = true;
			}
			else {
				SeenMap seenMap = pm.getUserSeenMap(userId);
				feSeen = seenMap.checkIfSeen(fe);
			}
			reply.setSeenPrevious(feSeen);
			if ((!feSeen) && markRead) {
				// ...and if it hasn't, it has now...
				feSeen = true;
				pm.setSeen(null, fe);
			}
			reply.setSeen(feSeen);
			
			// ...set whether the entry is in the trash...
			boolean trashed = fe.isPreDeleted();
			reply.setTrashed(trashed);
			
			// ...set the entry's path... 
			FolderEntry feTop = fe.getTopEntry();
			boolean		isTop = (null == feTop);
			if (isTop) {
				feTop = fe;
			}
			reply.setTop( isTop                                );
			reply.setPath(feTop.getParentBinder().getPathName());
			
			// ...family and file size...
			String  family = GwtServerHelper.getFolderEntityFamily(bs, feTop);
			reply.setFamily(family);
			String fileSizeDisplay = "";
			FileAttachment fa;
			if (GwtServerHelper.isFamilyFile(family)) {
				fa = MiscUtil.getPrimaryFileAttachment(feTop);
				if (null != fa) {
					fileSizeDisplay = buildFileSizeDisplayFromKBSize(user, fa.getFileItem().getLengthKB());
				}
			}
			else {
				fa = null;
			}
			reply.setFileSizeDisplay(fileSizeDisplay);
	
			// ...set the entry's creator...
			HistoryStamp cStamp = fe.getCreation();
			reply.setCreator(buildFolderEntryUser(bs, request, cStamp));
			
			// ...set the entry's modifier...
			HistoryStamp mStamp = fe.getModification();
			reply.setModifier(buildFolderEntryUser(bs, request, mStamp));
			reply.setModifierIsCreator(cStamp.getPrincipal().getId().equals(mStamp.getPrincipal().getId()));
			
			// ...set the entry's locker...
			HistoryStamp lStamp = fe.getReservation();
			reply.setEntryLocker(buildFolderEntryUser(bs, request, lStamp));
			reply.setEntryLockedByLoggedInUser((null != lStamp) && lStamp.getPrincipal().getId().equals(userId));

			// ...if it's a file...
			if (null != fa) {
				// ...that's locked with a still valid lock...
				FileLock fl = fa.getFileLock();
				if ((null != fl) && fa.isCurrentlyLocked()) {
					// ...set the file's locker...
					Long lockerId = fl.getOwner().getId();
					reply.setFileLocker(buildFolderEntryUser(bs, request, fl.getExpirationDate(), lockerId));
					reply.setFileLockedByLoggedInUser(lockerId.equals(userId));
				}
			}

			// ...set the contributor's to this entry...
			reply.setContributors(ListFolderHelper.collectContributorIds(fe));
			
			// ...set the entry's title...
			String feTitle = GwtServerHelper.getFolderEntryTitle(fe);
			reply.setTitle(feTitle);
			
			// ...set information about the entry's comments...
			CommentsInfo ci = new CommentsInfo(
				entityId,
				feTitle,
				(isTop                      ?
					fe.getTotalReplyCount() :	// For top level entries, we show all the replies.
					fe.getReplyCount()));		// For replies themselves, we only show their direct replies.
			ci.setCommentsDisabled(!(GwtServerHelper.canEntryHaveAComment(fe)));
			reply.setComments(ci);

			// ...if this is a comment...
			if (!isTop) {
				// ...store a List<ViewFolderEntryInfo> for the bread
				// ...crumb links leading up to it...
				List<ViewFolderEntryInfo> peBCList = new ArrayList<ViewFolderEntryInfo>();
				FolderEntry pe = fe.getParentEntry();
				while (null != pe) {
					peBCList.add(0, buildViewFolderEntryInfo(bs, request, folderId, pe.getId()));
					pe = pe.getParentEntry();
				}
				reply.setCommentBreadCrumbs(peBCList);
			}
	
			// ...if this is a file entry with a filename...
			FileAttachment faForEntryIcon;
			fa = GwtServerHelper.getFileEntrysFileAttachment(bs, fe, isTop);
			if (null != fa) {
				// ...store it for using for the entry icon...
				faForEntryIcon = fa;
				
				// ...and set the ViewFileInfo for an HTML view of the
				// ...file if it supports it...
				ViewFileInfo vfi = buildViewFileInfo(request, fe, fa);
				if (null == vfi) {
					GwtImageHelper.setImageContentDetails(bs, request, reply, fe, fa);
					GangliaMonitoring.incrementFilePreviewRequests();
				}
				else {
					reply.setHtmlView(vfi);
				}
				
				// ...if the user has rights to download the file...
				if (AdminHelper.getEffectiveDownloadSetting(bs, user)) {
					// ...add a download URL...
					reply.setDownloadUrl(
							GwtDesktopApplicationsHelper.getDownloadFileUrl(
							request,
							bs,
							fe.getParentBinder().getId(),
							fe.getId()));
				}
			}
			
			else {
				// ...if the entry itself didn't have a file for an
				// ...entry icon, try its top most parent...
				faForEntryIcon = GwtServerHelper.getFileEntrysFileAttachment(bs, feTop);
			}

			// ...if we have a FileAttachment with a file to use for
			// ...the entry icon...
			if (null != faForEntryIcon) {
				// ...store the icons for that file...
				String fName = faForEntryIcon.getFileItem().getName();
				reply.setEntryIcon(FileIconsHelper.getFileIconFromFileName(fName, mapBISToIS(BinderIconSize.SMALL)),  BinderIconSize.SMALL );
				reply.setEntryIcon(FileIconsHelper.getFileIconFromFileName(fName, mapBISToIS(BinderIconSize.MEDIUM)), BinderIconSize.MEDIUM);
				reply.setEntryIcon(FileIconsHelper.getFileIconFromFileName(fName, mapBISToIS(BinderIconSize.LARGE)),  BinderIconSize.LARGE );
			}
	
			// ...set the entry's description...
			Description	feDesc = fe.getDescription();
			if (null != feDesc) {
				boolean feDescHtml = (Description.FORMAT_HTML == feDesc.getFormat());
				reply.setDescIsHtml(feDescHtml              );
				reply.setDescTxt(   feDesc.getStrippedText());
				
				String desc = feDesc.getText();
				if (MiscUtil.hasString(desc)) {
					desc = MarkupUtil.markupStringReplacement(
						null,
						null,
						request,
						null,
						fe,
						desc,
						WebKeys.MARKUP_VIEW);
				}
				reply.setDesc(desc);
			}
			else {
				reply.setDescIsHtml(true);
				reply.setDesc(      ""  );
				reply.setDescTxt(   ""  );
			}
			
			// ...set the view's toolbar items....
			List<ToolbarItem> tbItems;
			if (trashed)
			     tbItems = new ArrayList<ToolbarItem>();	// No possible interaction if the entry is in the trash.
			else tbItems = GwtMenuHelper.getViewEntryToolbarItems(bs, request, fe);
			reply.setToolbarItems(tbItems);

			// ...for non-Guest internal users...
			if ((!(user.isShared())) && user.getIdentityInfo().isInternal()) {
				// ...set the view's sharing information...
				getFolderEntrySharedByInfo(  bs, request, reply);
				getFolderEntrySharedWithInfo(bs, request, reply);
				
				// ...and if there is any sharing information...
				if (reply.hasShares()) {
					completeShareAIs(bs, request, reply);
				}
			}
			
			// If we get here, reply refers to the ViewFolderEntryInfo
			// for the user to view the entry.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getFolderEntryDetails( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Fills in the 'Shared by' List<ShareInfo> in a
	 * FolderEntryDetails. 
	 */
	private static void getFolderEntrySharedByInfo(AllModulesInjected bs, HttpServletRequest request, FolderEntryDetails fed) {
		// Get the List<ShareItem> of the shares of this item with the
		// current user.
		ShareItemSelectSpec	spec = new ShareItemSelectSpec();
		spec.setLatest(true);
		spec.setSharedEntityIdentifier(new EntityIdentifier(fed.getEntityId().getEntityId(), EntityType.folderEntry));
		Long userId = GwtServerHelper.getCurrentUserId();
		List<Long>	groups = GwtServerHelper.getGroupIds(request, bs, userId);
		List<Long>	teams  = GwtServerHelper.getTeamIds( request, bs, userId);
		List<Long>	users  = new ArrayList<Long>(); users.add(userId);
		spec.setRecipients(users, groups, teams);
		
		// Can we find any shares?
		List<ShareItem> shareItems = getCleanShareList(bs.getSharingModule(), spec, true);
		if (MiscUtil.hasItems(shareItems)) {
			// Yes!  Scan them.
			for (ShareItem si:  shareItems) {
				// If this share was created by the current user...
				if (si.getSharerId().equals(userId)) {
					// ...skip it.  We don't show these.  (Will
					// ...typically happen with public shares.)
					continue;
				}
				
				// Create the ShareInfo for this share and add it to
				// the 'Shared by' list.
				ShareInfo feSI = new ShareInfo();
				fed.addSharedByItem(feSI);
				
				// Add information about the sharer.
				Long sId = si.getSharerId();
				feSI.setUser(AssignmentInfo.construct(sId, AssigneeType.INDIVIDUAL));
				feSI.setTitle(getRecipientTitle(bs, RecipientType.user, sId));

				// Add when it was shared.
				Date	date       = si.getStartDate();
				String	dateString = ((null == date) ? "" : GwtServerHelper.getDateTimeString(date, DateFormat.MEDIUM, DateFormat.SHORT));
				feSI.setShareDate(dateString);
				
				// If the share expires...
				date = si.getEndDate();
				if (null != date) {
					// ...add when it expires.
					dateString = ((null == date) ? "" : GwtServerHelper.getDateTimeString(date, DateFormat.MEDIUM, DateFormat.SHORT));
					feSI.setExpiresDate(dateString);
				}
				feSI.setExpired(si.isExpired());

				// Add the rights granted with the share.
				feSI.setRights(GwtShareHelper.getShareRightsFromRightSet(si.getRightSet()));

				// Add any comments from the share.
				feSI.setComment(si.getComment());
			}
		}
	}
	
	/*
	 * Fills in the 'Shared with' List<ShareInfo> in a
	 * FolderEntryDetails. 
	 */
	private static void getFolderEntrySharedWithInfo(AllModulesInjected bs, HttpServletRequest request, FolderEntryDetails fed) {
		// Get the List<ShareItem> of the shares of this item the
		// current user has issued.
		ShareItemSelectSpec	spec = new ShareItemSelectSpec();
		spec.setLatest(true);
		spec.setSharedEntityIdentifier(new EntityIdentifier(fed.getEntityId().getEntityId(), EntityType.folderEntry));
		spec.setSharerId(GwtServerHelper.getCurrentUserId());

		// Can we find any shares?  (Should we show shares the user
		// made that have expired?  I think so.  Hence, the false to
		// not skip them like in the 'Shared by' handler above.)
		List<ShareItem> shareItems = getCleanShareList(bs.getSharingModule(), spec, false);
		if (MiscUtil.hasItems(shareItems)) {
			// Yes!  Scan them.
			for (ShareItem si:  shareItems) {
				// Is this a public share?
				boolean isPublic = si.getIsPartOfPublicShare();
				if (isPublic) {
					// Yes!  Are we tracking any shared with items?
					List<ShareInfo> swItems = fed.getSharedWithItems();
					if (MiscUtil.hasItems(swItems)) {
						// Yes!  Scan them.
						boolean skipThis = false;
						for (ShareInfo siScan:  fed.getSharedWithItems()) {
							// Is this share a public share?
							if (siScan.isPublic()) {
								// Yes!  Then we don't need another.
								skipThis = true;
								break;
							}
						}
						
						// If we should skip this share...
						if (skipThis) {
							// ...skip it.
							continue;
						}
					}
				}
				
				// Create the ShareInfo for this share and add it to
				// the 'Shared with' list.
				ShareInfo feSI = new ShareInfo();
				fed.addSharedWithItem(feSI);
				feSI.setPublic(isPublic);

				// For public shares...
				if (isPublic) {
					// ...add a public assignee and generic title...
					feSI.setUser(AssignmentInfo.construct(si.getRecipientId(), AssigneeType.PUBLIC));
					feSI.setTitle(NLT.get("share.recipientType.title.public"));
				}
				else {
					// ...and for non-public shares, add information
					// ...about the share recipient.
					AssigneeType at;
					switch (si.getRecipientType()) {
					default:
					case user:        at = AssigneeType.INDIVIDUAL;  break;
					case group:       at = AssigneeType.GROUP;       break;
					case publicLink:  at = AssigneeType.PUBLIC_LINK; break;
					case team:        at = AssigneeType.TEAM;        break;
					}
					feSI.setUser(AssignmentInfo.construct(si.getRecipientId(), at));
					feSI.setTitle(getRecipientTitle(bs, si.getRecipientType(), si.getRecipientId()));
				}
				
				// Add when it was shared.
				Date	date       = si.getStartDate();
				String	dateString = ((null == date) ? "" : GwtServerHelper.getDateTimeString(date, DateFormat.MEDIUM, DateFormat.SHORT));
				feSI.setShareDate(dateString);

				// If the share expires...
				date = si.getEndDate();
				if (null != date) {
					// ...add when it expires.
					dateString = ((null == date) ? "" : GwtServerHelper.getDateTimeString(date, DateFormat.MEDIUM, DateFormat.SHORT));
					feSI.setExpiresDate(dateString);
				}
				feSI.setExpired(si.isExpired());

				// Add the rights granted with the share.
				feSI.setRights(GwtShareHelper.getShareRightsFromRightSet(si.getRightSet()));
				
				// Add any comments from the share.
				feSI.setComment(si.getComment());
			}
		}
	}

	/**
	 * Reads the row data from a folder and returns it as a
	 * FolderRowsRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param folderInfo
	 * @param folderDisplayData
	 * @param folderColumns
	 * @param start
	 * @param length
	 * @param quickFilter
	 * @param authenticationGuid
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FolderRowsRpcResponseData getFolderRows(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, FolderDisplayDataRpcResponseData folderDisplayData, List<FolderColumn> folderColumns, int start, int length, String quickFilter, String authenticationGuid) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getFolderRows()");
		try {
			// Is this a binder the user can view?
			if (!(GwtServerHelper.canUserViewBinder(bs, folderInfo))) {
				// No!  Return an empty set of rows.
				return buildEmptyFolderRows(null);
			}
			
			// Access the binder/folder.
			boolean	isFilr   = Utils.checkIfFilr();
			Long	folderId = folderInfo.getBinderIdAsLong();
			Binder	binder   = bs.getBinderModule().getBinder(folderId);
			Folder	folder   = ((binder instanceof Folder) ? ((Folder) binder) : null);
			boolean	isFolder = (null != folder);
			
			// If we're reading from a mirrored file folder...
			if (FolderType.MIRROREDFILE == folderInfo.getFolderType()) {
				// ...whose driver is not configured...
				String rdn = binder.getResourceDriverName();
				if (!(MiscUtil.hasString(rdn))) {
					// ...we don't read anything.
					return buildEmptyFolderRows(binder);
				}
			}
			
			// What type of folder are we dealing with?
			boolean isGuestbook             = false;
			boolean isMilestone             = false;
			boolean isSurvey                = false;
			boolean isGlobalRootWS          = folderInfo.isBinderGlobalRootWS();
			boolean isMobileDevicesView     = folderInfo.isBinderMobileDevices();
			boolean isProfilesRootWS        = folderInfo.isBinderProfilesRootWS();
			boolean isProxyIdentitiesView   = folderInfo.isBinderProxyIdentities();
			boolean isEmailTemplatesView    = folderInfo.isBinderEmailTemplates();
			boolean isTeamsRootWS           = folderInfo.isBinderTeamsRootWS();
			boolean isLimitUserVisibility   = folderInfo.isBinderLimitUserVisibility();
			boolean isManageAdministrators  = folderInfo.isBinderAdministratorManagement();
			@SuppressWarnings("unused")
			boolean isManageTeams           = folderInfo.isBinderTeamsRootWSManagement();
			boolean isManageUsers           = folderInfo.isBinderProfilesRootWSManagement();
			boolean isTrash                 = folderInfo.isBinderTrash();
			switch (folderInfo.getFolderType()) {
			case GUESTBOOK:  isGuestbook = true; break;
			case MILESTONE:  isMilestone = true; break;
			case SURVEY:     isSurvey    = true; break;
			}

			// Access any other information we need to read the data.
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			SeenMap			seenMap              = bs.getProfileModule().getUserSeenMap(null);

			// Setup the current search filter the user has selected
			// on the folder.
			Map options;
			if (isFolder)
			     options = getFolderSearchFilter(bs, folder, userFolderProperties, null);
			else options = new HashMap();
			GwtServerHelper.addQuickFilterToSearch(options, quickFilter, folderInfo.isBinderProfilesRootWS());
			options.put(ObjectKeys.SEARCH_OFFSET,   start );
			options.put(ObjectKeys.SEARCH_MAX_HITS, length);

			// Are we populating the profiles root binder?
			if (isProfilesRootWS) {
				// Yes!  Is it for the manage users feature of the
				// administration console?
				if (isManageUsers) {
					// Yes!  If the filters are such that we wouldn't
					// get any results...
					ManageUsersState mus = GwtServerHelper.getManageUsersState(bs, request).getManageUsersState();
					boolean disabled      = mus.isShowDisabled();
					boolean enabled       = mus.isShowEnabled();
					boolean external      = mus.isShowExternal();
					boolean internal      = mus.isShowInternal();
					boolean siteAdmins    = mus.isShowSiteAdmins();
					boolean nonSiteAdmins = mus.isShowNonSiteAdmins();
					if (((!external)   && (!internal)) ||
						((!enabled)    && (!disabled)) ||
						((!siteAdmins) && (!nonSiteAdmins))) {
						// ...simply return an empty list.
						return buildEmptyFolderRows(binder);
						
					}
					
					// Apply the internal/external filtering...
					if      (internal && external) /* Default includes both.*/ ;
					else if (internal)             options.put(ObjectKeys.SEARCH_IS_INTERNAL,            Boolean.TRUE);
					else if (external)             options.put(ObjectKeys.SEARCH_IS_EXTERNAL,            Boolean.TRUE);

					// ...apply the enabled/disabled filtering...
					if      (enabled  && disabled) /* Default includes both. */ ;
					else if (enabled)              options.put(ObjectKeys.SEARCH_IS_ENABLED_PRINCIPALS,  Boolean.TRUE);
					else if (disabled)             options.put(ObjectKeys.SEARCH_IS_DISABLED_PRINCIPALS, Boolean.TRUE);
					
					// ...and apply the admin/non-admin filtering.
					if      (siteAdmins  && nonSiteAdmins) /* Default includes both. */ ;
					else if (siteAdmins)           options.put(ObjectKeys.SEARCH_IS_SITE_ADMINS,         Boolean.TRUE);
					else if (nonSiteAdmins)        options.put(ObjectKeys.SEARCH_IS_NON_SITE_ADMINS,     Boolean.TRUE);
				}
				
				else if (isLimitUserVisibility || isManageAdministrators) {
					// No options.
				}
				
				else {
					// No, it isn't for the manage users feature of the
					// administration console!  Eliminate the
					// non-person, external and disabled users.
					options.put(ObjectKeys.SEARCH_IS_PERSON,             Boolean.TRUE);
					options.put(ObjectKeys.SEARCH_IS_INTERNAL,           Boolean.TRUE);
					options.put(ObjectKeys.SEARCH_IS_ENABLED_PRINCIPALS, Boolean.TRUE);
				}
			}
			
			// Factor in the user's sorting selection.
			String	sortBy           = folderDisplayData.getFolderSortBy();
			boolean	sortDescend      = folderDisplayData.getFolderSortDescend();
			options.put(ObjectKeys.SEARCH_SORT_BY,      sortBy     );
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, sortDescend);

			// What do we know about pinning of entries on this folder?
			boolean folderSupportsPinning = getFolderSupportsPinning(folderInfo);
			boolean viewPinnedEntries     = (folderSupportsPinning && getUserViewPinnedEntries(request, folderId));
			
			// Does this folder support pinning?
			List<Map>  pinnedEntrySearchMaps;
			List<Long> pinnedEntryIds = new ArrayList<Long>();
			if (folderSupportsPinning) {
				// Yes!  Are there any entries pinned in the folder?
				pinnedEntrySearchMaps = getPinnedEntries(
					bs,
					folder,
					userFolderProperties,
					pinnedEntryIds,
					viewPinnedEntries);
			}
			else {
				// No, this folder doesn't support pinning!  We don't
				// have (or need) the pinned entry maps.
				pinnedEntrySearchMaps = null;
			}

			// If we're working with a 'Shared by/with Me' collection,
			// get the shared items.
			CollectionType			collectionType           = folderInfo.getCollectionType();
			boolean					isCollection             = folderInfo.isBinderCollection();
			boolean					isCollectionSharedByMe   = (isCollection && CollectionType.SHARED_BY_ME.equals(  collectionType));
			boolean					isCollectionSharedWithMe = (isCollection && CollectionType.SHARED_WITH_ME.equals(collectionType));
			boolean					isCollectionSharedPublic = (isCollection && CollectionType.SHARED_PUBLIC.equals( collectionType));
			List<GwtSharedMeItem> 	shareItems;
			if      (isCollectionSharedByMe)                               shareItems = getSharedByMeItems(  bs, request, sortBy, sortDescend                          );
			else if (isCollectionSharedWithMe || isCollectionSharedPublic) shareItems = getSharedWithMeItems(bs, request, sortBy, sortDescend, isCollectionSharedPublic);
			else                                                           shareItems = null;

			// Is the user currently viewing pinned entries?
			List<Map>  searchEntries;
			int        totalRecords;
			boolean    totalIsApproximate = false;
			if (viewPinnedEntries) {
				// Yes!  Use the pinned entries as the search entries.
				searchEntries = pinnedEntrySearchMaps;
				totalRecords  = searchEntries.size();
			}
			
			else {
				// No, the user isn't currently viewing pinned entries!
				// Read the entries based on a search.
				Map searchResults;
				if      (isMobileDevicesView)             return          GwtMobileDeviceHelper.getMobileDeviceRows(         bs, request, binder, quickFilter, options, folderInfo,     folderColumns);
				else if (isProxyIdentitiesView)           return          GwtProxyIdentityHelper.getProxyIdentityRows(       bs, request, binder, quickFilter, options, folderInfo,     folderColumns);
				else if (isEmailTemplatesView)            return          GwtEmailTemplatesHelper.getEmailTemplatesRows(     bs, request, binder, quickFilter, options, folderInfo,     folderColumns);
				else if (isLimitUserVisibility)           return          GwtUserVisibilityHelper.getLimitUserVisibilityRows(bs, request, binder, quickFilter, options, folderInfo,     folderColumns);
				else if (isManageAdministrators)          return          GwtAdministratorsHelper.getAdministratorsRows(     bs, request, binder, quickFilter, options, folderInfo,     folderColumns);
				else if (isTrash)                         searchResults = TrashHelper.getTrashEntities(                      bs,          binder,              options                               );
				else if (isProfilesRootWS)                searchResults = getUserEntries(                                    bs, request, binder, quickFilter, options                               );
				else if (isGlobalRootWS || isTeamsRootWS) searchResults = getRootWorkspaceEntries(                           bs, request, binder, quickFilter, options                               );
				else if (isCollection)                    searchResults = getCollectionEntries(                              bs, request, binder, quickFilter, options, collectionType, shareItems   );
				else {
					options.put(ObjectKeys.SEARCH_INCLUDE_NESTED_BINDERS, Boolean.TRUE          );	// Include nested folders.
					options.put(ObjectKeys.SEARCH_SORT_BY,                Constants.ENTITY_FIELD);	// Sort folders separately from entries.
					options.put(ObjectKeys.SEARCH_SORT_DESCEND,           sortDescend           );	// Sort direction, folders vs. entries.
					options.put(ObjectKeys.SEARCH_SORT_BY_SECONDARY,      sortBy                );	// After sorting folders from entries, what's sorted by next?
					options.put(ObjectKeys.SEARCH_SORT_DESCEND_SECONDARY, sortDescend           );	// Same direction for entries as for folders vs. entries.

					// If we're sorting on a custom column...
					FolderColumn sortColumn = FolderColumn.getFolderColumnByEleName(folderColumns, sortBy);
					if ((null != sortColumn) && sortColumn.isCustomColumn()) {
						// ...whose value is a number...
						String colType = sortColumn.getColumnType();
						if ((MiscUtil.hasString(colType)) && colType.equals("number")) {
							// ...we want Lucene to sort it as a
							// ...double.
							options.put(
								ObjectKeys.SEARCH_SORT_FIELD_TYPE_SECONDARY,
								new Integer(SortField.DOUBLE));
						}
					}

					// If we have an authentication GUID for the
					// folder...
					boolean hasAuthenticationGuid = MiscUtil.hasString(authenticationGuid);
					if (hasAuthenticationGuid) {
						// ...store it so it can be utilized.
						org.kablink.teaming.fi.auth.AuthUtil.setUuid(authenticationGuid);
					}
					
					// Issue the search.
					SimpleProfiler.start("GwtViewHelper.getFolderRows(Basic folder search)");
					try {
						searchResults = bs.getFolderModule().getEntries(folderId, options);
					}
					
					catch (Exception e) {
						// Did we catch an authentication exception?
						if (e instanceof AuthException) {
							// Yes!  This folder requires
							// authentication!  Return an appropriate
							// response.
							AuthException ae = ((AuthException) e);
							CloudFolderAuthentication cfAuth = new CloudFolderAuthentication(
								GwtCloudFolderHelper.getCloudFolderTypeFromRoot(
									folderInfo.getCloudFolderRoot()),
								ae.getUrl(),
								ae.getUuid());
							FolderRowsRpcResponseData reply = new FolderRowsRpcResponseData(cfAuth);
							if (GwtLogHelper.isDebugEnabled(m_logger)) {
								dumpFolderRowsRpcResponseData(binder, reply);
							}
							return reply;
						}
						
						// We caught something other than an
						// authentication exception.  Propagate it.
						throw e;
					}
					
					finally {
						// If we stored an authentication GUID for the
						// search...
						if (hasAuthenticationGuid) {
							// ...remove it.
							org.kablink.teaming.fi.auth.AuthUtil.clearUuid();
						}
						SimpleProfiler.stop("GwtViewHelper.getFolderRows(Basic folder search)");
					}
				}
				searchEntries       = ((List<Map>) searchResults.get(ObjectKeys.SEARCH_ENTRIES                ));
				Integer total       = ((Integer)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL            ));
				Boolean approx      = ((Boolean)   searchResults.get(ObjectKeys.SEARCH_COUNT_TOTAL_APPROXIMATE));
				Boolean more        = ((Boolean)   searchResults.get(ObjectKeys.SEARCH_THERE_IS_MORE          ));
				totalRecords        = ((null != total)  ? total : (start + searchEntries.size()));
				boolean thereIsMore = ((null != more) && more);
				if (thereIsMore && (null == total)) {
					totalRecords += 1;
				}
				totalIsApproximate  = ((null != approx) && approx && thereIsMore);
			}

			// Scan the entries we read.
			boolean         completeAIsRequired      = false;
			boolean         completeCIsRequired      = false;
			boolean         completeNFRightsRequired = false;
			List<ErrorInfo> errorList                = new ArrayList<ErrorInfo>();
			List<FolderRow> folderRows               = new ArrayList<FolderRow>();
			List<Long>      contributorIds           = new ArrayList<Long>();
			for (Map entryMap:  searchEntries) {
				// Initiate per row profiling.
				String docIdS = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DOCID_FIELD);
				Long   docId  = Long.parseLong(docIdS);
				String perRowProfile;
				if (PROFILE_PER_ROW) {
					perRowProfile = ("GwtViewHelper.getFolderRows(Row:  " + docIdS + ")");
					SimpleProfiler.start(perRowProfile);
				}
				else {
					perRowProfile = null;
				}
				
				try {
					// Is this an entry or folder?
					String  entityType          = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.ENTITY_FIELD);
					boolean isEntityFolder      = EntityType.folder.name().equals(     entityType);
					boolean isEntityFolderEntry = EntityType.folderEntry.name().equals(entityType);
					String  locationBinderId    = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.BINDER_ID_FIELD);
					if (!(MiscUtil.hasString(locationBinderId))) {
						locationBinderId = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.BINDERS_PARENT_ID_FIELD);
					}
					
					// Have we already process this entry's ID?
					EntityId entityId = new EntityId(Long.parseLong(locationBinderId), docId, entityType);
					if ((!isCollectionSharedWithMe) && isEntityInList(entityId, folderRows)) {
						// Yes!  Skip it now.  Note that we may have
						// duplicates because of pinning.
						continue;
					}
					
					// Extract the contributors from this entry.
					collectContributorIds(entryMap, contributorIds);
					
					// Create a FolderRow for each entry.
					FolderRow fr = new FolderRow(entityId, folderColumns);
					if (isEntityFolderEntry && pinnedEntryIds.contains(entityId.getEntityId())) {
						fr.setPinned(true);
					}

					// Set this entity's family type in the row.
					String entityFileFamily = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.FAMILY_FIELD);
					fr.setRowFamily(entityFileFamily);

					// If we working with a binder...
					if (!isEntityFolderEntry) {
						// ...store it's icon name...
						String iconName = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.ICON_NAME_FIELD);
						fr.setBinderIcon(Utils.getIconNameTranslated(iconName),                  BinderIconSize.SMALL );
						fr.setBinderIcon(Utils.getIconNameTranslated(iconName, IconSize.MEDIUM), BinderIconSize.MEDIUM);
						fr.setBinderIcon(Utils.getIconNameTranslated(iconName, IconSize.LARGE ), BinderIconSize.LARGE );

						// ...whether it's a user's home directory
						// ...folder...
			            String homeDirStr = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.IS_HOME_DIR_FIELD);
						fr.setHomeDir(Constants.TRUE.equals(homeDirStr));
						
						// ...and whether it's a user's my file storage
						// ...directory folder in the row.
			            String myFilesDirStr = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.IS_MYFILES_DIR_FIELD);
						fr.setMyFilesDir(Constants.TRUE.equals(myFilesDirStr));
					}
					
					// Scan the columns.
					for (FolderColumn fc:  folderColumns) {
						// Initiate per column profiling.
						String perColProfile;
						String perRowColProfile;
						if (PROFILE_PER_COLUMN) {
							perColProfile = ("GwtViewHelper.getFolderRows(Col:  " + fc.getColumnName() + ")");
							SimpleProfiler.start(perColProfile);
							
							perRowColProfile = ("GwtViewHelper.getFolderRows(Row:  " + docIdS + ", Col:  " + fc.getColumnName() + ")");
							SimpleProfiler.start(perRowColProfile);
						}
						else {
							perColProfile    =
							perRowColProfile = null;
						}
						
						try {
							// Is this a custom column?
							if (fc.isCustomColumn()) {
								// Yes!  Generate a value for it.
								setValueForCustomColumn(bs, entryMap, fr, fc);
							}
							
							else {
								// No, this isn't a custom column!
								String	cn      = fc.getColumnName();
								String	csk     = fc.getColumnSearchKey();
								Object	emValue = GwtServerHelper.getValueFromEntryMap(entryMap, csk);

								// Is it a file time column?
								if (csk.equals(Constants.FILE_TIME_FIELD)) {
									// Yes!  Does it have a string value?
									if (emValue instanceof String) {
										// Yes!  Then we use that as the modification time.
										emValue   = new Date(Long.parseLong((String) emValue));
									}
									
									// No, it's not a single string value!  Is it a
									// SearchFieldResult?
									else if (emValue instanceof SearchFieldResult) {
										// Yes!  Then it's multi-valued (more than one file
										// attachment.)  If we can get the file entry's primary
										// attachment...
										FileAttachment fa = GwtServerHelper.getFileEntrysFileAttachment(bs, entityId);
										if (null != fa) {
											// ...then we'll use it modification time.
											emValue = fa.getModification().getDate();
										}
										else {
											// No, we can't get the file entry's primary
											// attachment!  Is there a first string value in the
											// SearchFieldResult?
											String[] emValues = ((SearchFieldResult) emValue).getValueArray().toArray(new String[0]);
											int emCount = ((null == emValues) ? 0 : emValues.length);
											String emStrValue;
											if (0 < emCount)
											     emStrValue = emValues[0];
											else emStrValue = null;
											if (MiscUtil.hasString(emStrValue)) {
												// Yes!  Can we parse it as a Long?
												Long time;
												try                  {time = Long.valueOf(emStrValue);}
												catch (Exception ex) {time = null;                    }
												if (null != time) {
													// Yes!  Use it as the modification time.
													emValue = new Date(time);
												}
											}
										}
									}
								}
								
								// Are we working on a 'Shared by/with Me' or 'Public' collection?
								GwtSharedMeItem smItem;
								if (isCollectionSharedByMe || isCollectionSharedWithMe || isCollectionSharedPublic) {
									// Yes!  Find the GwtSharedMeItem for this row.
									smItem = GwtSharedMeItem.findShareMeInList(
										docId,
										entityType,
										shareItems);
									
									// Is this the sharedBy/With column?
									if (csk.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_SHARED_BY) ||
										csk.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_SHARED_WITH)) {
										// Yes!  Build a List<AssignmentInfo> for this row...
										List<AssignmentInfo> aiList;
										if (null == smItem) {
											aiList = new ArrayList<AssignmentInfo>();
										}
										else {
											if (csk.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_SHARED_BY))
											     aiList = getAIListFromSharers(   smItem.getPerShareInfos());
											else aiList = getAIListFromRecipients(smItem.getPerShareInfos());
										}
										completeAIsRequired = (!(aiList.isEmpty()));
										fr.setColumnValue_AssignmentInfos(fc, aiList);
			
										// ...and continue with the next column.
										continue;
									}
									
									// No, this isn't the sharedBy/With column!  Is it the
									// sharedMessage column?
									else if (csk.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_MESSAGE)) {
										// Yes!  Build a List<ShareMessageInfo> for this row...
										List<ShareMessageInfo> smiList;
										if (null == smItem)
										     smiList = new ArrayList<ShareMessageInfo>();
										else smiList = getShareMessageListFromShares(smItem.getPerShareInfos());
										fr.setColumnValue_ShareMessageInfos(fc, smiList);
										
										// ...and continue with the next column.
										continue;
									}
									
									// No, this isn't the sharedMessage column either!  Is it the
									// sharedDate column?
									else if (csk.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_DATE)) {
										// Yes!  Build a List<ShareDateInfo> for this row...
										List<ShareDateInfo> sdiList;
										if (null == smItem)
										     sdiList = new ArrayList<ShareDateInfo>();
										else sdiList = getShareDateListFromShares(smItem.getPerShareInfos());
										fr.setColumnValue_ShareDateInfos(fc, sdiList);
										
										// ...and continue with the next column.
										continue;
									}
									
									// No, this isn't the sharedDate column either!  Is it the
									// sharedExpiration column?
									else if (csk.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_EXPIRATION)) {
										// Yes!  Build a List<ShareExpirationInfo> for this row...
										List<ShareExpirationInfo> seiList;
										if (null == smItem)
										     seiList = new ArrayList<ShareExpirationInfo>();
										else seiList = getShareExpirationListFromShares(smItem.getPerShareInfos());
										fr.setColumnValue_ShareExpirationInfos(fc, seiList);
										
										// ...and continue with the next column.
										continue;
									}
									
									// No, this isn't the sharedExpiration column either!  Is it
									// the sharedAccess column?
									else if (csk.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_ACCESS)) {
										// Yes!  Build a List<ShareAccessInfo> for this row...
										List<ShareAccessInfo> saiList;
										if (null == smItem)
										     saiList = new ArrayList<ShareAccessInfo>();
										else saiList = getShareAccessListFromShares(smItem.getPerShareInfos());
										fr.setColumnValue_ShareAccessInfos(fc, saiList);
										
										// ...and continue with the next column.
										continue;
									}
								}
								
								else {
									// No, we aren't working on a
									// 'Shared by/with Me' collection!
									smItem = null;
								}

								// Is this the mobileDevices column in
								// the manage users page?
								if (isManageUsers && csk.equalsIgnoreCase(FolderColumn.COLUMN_MOBILE_DEVICES)) {
									// Yes!  Store a MobileDevicesInfo
									// object that will fill in with
									// the correct count before we
									// return.
									fr.setColumnValue(
										fc,
										new MobileDevicesInfo(
											entityId.getEntityId(),
											(-1)));
								}
								
								GuestInfo       gi  = null;
								PrincipalInfoId pId = null;
								if (emValue instanceof Principal) {
									// Yes!  Are we looking at the 'guest' column in a guest book
									// folder?
									Principal p = ((Principal) emValue);
									if (isGuestbook && cn.equals("guest")) {
										// Yes!  If the entity is a folder entry...
										if (isEntityFolderEntry) {
											// ...use the principal to generate a GuestInfo for the
											// ...column...
											gi = getGuestInfoFromPrincipal(bs, request, p);
											fr.setColumnValue(fc, gi);
										}
										else {
											// ...otherwise, don't store a value for the column.
											fr.setColumnValue(fc, "");
										}
									}
									
									else {
										// No, we aren't looking at the 'guest' column in a guest
										// book folder!  Simply track the principal's ID.  We'll
										// resolve it later to a PrincipalInfo in completeUserInfo().
										pId = new PrincipalInfoId(p.getId());
										fr.setColumnValue(fc, pId);
									}
								}
							
								if ((null == pId) && (null == gi)) {
									// No!  Does the column contain assignment information?
									if (AssignmentInfo.isColumnAssigneeInfo(csk)) {
										// Yes!  Read its List<AssignmentInfo>'s.
										AssigneeType ait = AssignmentInfo.getColumnAssigneeType(csk);
										List<AssignmentInfo> assignmentList = GwtEventHelper.getAssignmentInfoListFromEntryMap(entryMap, csk, ait);
										
										// Is this column for an individual assignee?
										if (ait.isIndividual()) {
											// Yes!  If we don't have columns for group or team
											// assignments, factor those in as well.
											factorInGroupAssignments(entryMap, folderColumns, csk, assignmentList);
											factorInTeamAssignments( entryMap, folderColumns, csk, assignmentList);
										}
										
										// Add the column data to the list.
										completeAIsRequired = true;
										fr.setColumnValue_AssignmentInfos(fc, assignmentList);
									}
									
									// No, the column doesn't contain assignment information
									// either!  Does it contain a collection of task folders?
									else if (csk.equals("tasks")) {
										// Yes!  Create a List<TaskFolderInfo> from the IDs it
										// contains and set that as the column value.
										List<TaskFolderInfo> taskFolderList = GwtServerHelper.getTaskFolderInfoListFromEntryMap(bs, request, entryMap, csk, errorList);
										fr.setColumnValue_TaskFolderInfos(fc, taskFolderList);
									}
									
									// No, the column doesn't contain a collection of task folders
									// either!  Does it contain an e-mail address?
									else if (csk.equals("emailAddress")) {
										// Yes!  Construct an EmailAddressInfo from the entry map.
										EmailAddressInfo emai = GwtServerHelper.getEmailAddressInfoFromEntryMap(bs, entryMap);
										fr.setColumnValue(fc, emai);
									}
									
									// No, the column doesn't contain an e-mail address either!
									// Does it contain team membership?
									else if (csk.equals("teamMembers")) {
										// Yes!  Construct the List<AssignmentInfo>'s from the
										// binder and add the column data to the list.
										List<AssignmentInfo> assignmentList = getAIListForTeamMembership(bs, docId);
										completeAIsRequired = true;
										fr.setColumnValue_AssignmentInfos(fc, assignmentList);
									}
									
									else {
										// No, the column doesn't contain team membership either!
										// Extract its String value.
										String value = GwtServerHelper.getStringFromEntryMapValue(
											emValue,
											DateFormat.MEDIUM,
											DateFormat.SHORT);
										
										// Are we working on a title field?
										if (csk.equals(Constants.TITLE_FIELD)) {
											// Yes!  Construct an EntryTitleInfo for it.
											EntryTitleInfo  eti = new EntryTitleInfo();
											eti.setHidden((null != smItem) && smItem.isHidden());
											eti.setSeen((isEntityFolderEntry && (!(user.isShared()))) ? seenMap.checkIfSeen(entryMap) : true);
											eti.setTrash(isTrash);
											eti.setTitle(MiscUtil.hasString(value) ? value : ("--" + NLT.get("entry.noTitle") + "--"));
											eti.setEntityId(entityId);
											String description = getEntryDescriptionFromMap(request, entryMap, ((null == smItem) ? null : smItem.getEntity()));
											if (MiscUtil.hasString(description)) {
												eti.setDescription(description);
												String descriptionFormat = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DESC_FORMAT_FIELD);
												eti.setDescriptionIsHtml(MiscUtil.hasString(descriptionFormat) && descriptionFormat.equals(String.valueOf(Description.FORMAT_HTML)));
											}
											else if (!isEntityFolderEntry) {
												description = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.ENTITY_PATH);
												if (MiscUtil.hasString(description)) {
													eti.setDescription(      description);
													eti.setDescriptionIsHtml(false      );
												}
												if (isEntityFolder) {
													// - - - - -
													// DRF:  Commented out as we always complete
													//    the rights with another RPC call AFTER
													//    the view has been populated.  See:
													//    DataTableFolderViewBase.getNestedFolderRightsNow().
													// - - - - -
													// completeNFRightsRequired = true;
												}
											}
											if (isEntityFolderEntry && GwtServerHelper.isFamilyFile(entityFileFamily)) {
												// Extract the base file information from the map.
												String fName = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.FILENAME_FIELD );
												String fId   = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.FILE_ID_FIELD  );
												String fTime = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.FILE_TIME_FIELD);

												// Does this entry have multiple files associated
												// with it?
												String   primaryId        = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.PRIMARY_FILE_ID_FIELD);
												boolean  hasPrimaryId     = MiscUtil.hasString(primaryId);
												String[] fileNameIds      = (hasPrimaryId ? GwtServerHelper.getStringsFromEntryMap(entryMap, Constants.FILENAME_AND_ID_FIELD ) : null);
												String[] fileTimeIds      = (hasPrimaryId ? GwtServerHelper.getStringsFromEntryMap(entryMap, Constants.FILE_TIME_AND_ID_FIELD) : null);
												int      fileNameIdsCount = ((null == fileNameIds) ? 0 : fileNameIds.length);
												int      fileTimeIdsCount = ((null == fileTimeIds) ? 0 : fileTimeIds.length);
												if ((fileNameIdsCount == fileTimeIdsCount) && (1 < fileNameIdsCount)) {
													// Yes!  Use the primary ID as the file's ID.
													// (Note that we should only ever hit this
													// branch with Vibe.)
													fId = primaryId;
													int primaryIdLen       = primaryId.length();
													int uniquePrefixLength = Constants.UNIQUE_PREFIX.length();
													
													// Scan the filename/IDs list.
													for (int i = 0; i < fileNameIdsCount; i += 1) {
														// Is this the primary file's name?
														String fileNameId   = fileNameIds[i];
														int    primaryIdPos = fileNameId.indexOf(primaryId);
														if (uniquePrefixLength == primaryIdPos) {
															// Yes!  Extract it from the
															// filename/ID.
															fName = fileNameId.substring(primaryIdPos + primaryIdLen);
															break;
														}
													}
													
													// Scan the file time/IDs list.
													for (int i = 0; i < fileTimeIdsCount; i += 1) {
														// Is this the primary file's time?
														String fileTimeId = fileTimeIds[i];
														int primaryIdPos = fileTimeId.indexOf(primaryId);
														if (uniquePrefixLength == primaryIdPos) {
															// Yes!  Extract it from the file
															// time/ID.
															fTime = fileTimeId.substring(primaryIdPos + primaryIdLen);
															break;
														}
													}
												}
												
												// Do we have a filename?
												if (MiscUtil.hasString(fName)) {
													// Yes!  Setup the view information
													// appropriately.
													eti.setFile(true);
										    		if (supportsViewAsHtml(fName)) {
														ViewFileInfo vfi = new ViewFileInfo();
														vfi.setFileId(fId);
														vfi.setEntityId(entityId);
														vfi.setFileTime(fTime);
														eti.setFileViewAsHtmlUrl(GwtServerHelper.getViewFileUrl(request, vfi));
										    		}
													eti.setFileDownloadUrl(GwtDesktopApplicationsHelper.getDownloadFileUrl(request, entryMap));
													eti.setFileIcon(FileIconsHelper.getFileIconFromFileName(fName, mapBISToIS(BinderIconSize.getListViewIconSize())));
												}
											}
											fr.setColumnValue(fc, eti);
										}
										
										// No, we aren't working on a title field!  Are we working
										// on a file ID field?
										else if (csk.equals(Constants.FILE_ID_FIELD)) {
											// Yes!  Do we have a single file ID?
											if ((!(MiscUtil.hasString(value))) || ((-1) != value.indexOf(','))) {
												// No!  Ignore the value.
												value = null;
											}
											
											else {
												// Yes, we have a single file ID!  Do we have a
												// file path that we support viewing of?
												String relativeFilePath = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.FILENAME_FIELD);
												if ((!(MiscUtil.hasString(relativeFilePath))) || (!(SsfsUtil.supportsViewAsHtml(relativeFilePath)))) {
													// No!  Ignore the value.
													value = null;
												}
											}
											
											// Do we have a file ID to work with?
											if (MiscUtil.hasString(value)) {
												// Yes!  Construct a ViewFileInfo for it.
												ViewFileInfo vfi = new ViewFileInfo();
												vfi.setFileId(     value);
												vfi.setEntityId(   entityId);
												vfi.setFileTime(   GwtServerHelper.getStringFromEntryMap(entryMap, Constants.FILE_TIME_FIELD));
												vfi.setViewFileUrl(GwtServerHelper.getViewFileUrl(request, vfi));
												fr.setColumnValue(fc, vfi);
											}
										}
		
										// No, we aren't working on a file ID field either!  Are we
										// working on an HTML description field?
										else if (csk.equals(Constants.DESC_FIELD) && cn.equals("descriptionHtml")) {
											// Yes!  Check if the description is in HTML format and
											// store it.
											String descFmt = GwtServerHelper. getStringFromEntryMap(entryMap, Constants.DESC_FORMAT_FIELD);
											boolean isHtml = ((null != descFmt) && "1".equals(descFmt));
											fr.setColumnValue(fc, new DescriptionHtml(value, isHtml));
										}
										
										// No, we aren't working on an HTML description field
										// either!  Are we working on a family specification
										// field?
										else if (csk.equals(Constants.FAMILY_FIELD)) {
											// Yes!  Do we have a value for the column?
											if (MiscUtil.hasString(value)) {
												// Yes!  Load any localized name we might have for
												// it.
												String nltKeyBase;
												if (entityType.equals(EntityType.folderEntry.name()))
												     nltKeyBase = "__entry_";
												else nltKeyBase = "__folder_";
												value = NLT.get((nltKeyBase + value), value);
											}
											
											// Use what ever String value we arrived at.
											fr.setColumnValue(fc, (null == (value) ? "" : value));
										}
										
										// No, we aren't working on a family specification field
										// either!  Are we working on a comments count field?
										else if (csk.equals(Constants.TOTALREPLYCOUNT_FIELD)) {
											// Yes!  Store a CommentsInfo for it.
											String commentCount = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.TOTALREPLYCOUNT_FIELD);
											if (!(MiscUtil.hasString(commentCount))) {
												commentCount = "0";
											}
											String entityTitle = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.TITLE_FIELD);
											if (!(MiscUtil.hasString(entityTitle))) {
												entityTitle = ("--" + NLT.get("entry.noTitle") + "--");
											}
											completeCIsRequired = true;
											fr.setColumnValue(
												fc,
												new CommentsInfo(
													entityId,
													entityTitle,
													Integer.parseInt(commentCount)));
										}
										
										// No, we aren't working a comments count field either!
										// Are we working on the internal/external flag of a user?
										else if (csk.equals(Constants.IDENTITY_INTERNAL_FIELD)) {
											// Yes!  Store a principal type for it.
											PrincipalType pt = getPrincipalType(bs, request, entityId);
											fr.setColumnValue(fc, new PrincipalAdminType(pt, false));	// Will correct in the completeUserInfo() call below.
										}
										
										else {
											// No, we aren't working on an internal/external flag
											// field either! Are we working on a field whose value
											// is a Date?
											if (emValue instanceof Date) {
												// Yes!  Is that Date overdue?
												if (DateComparer.isOverdue((Date) emValue)) {
													// Yes!  Mark that column as being an overdue
													// date, and if this a due date...
													if (csk.equals(Constants.DUE_DATE_FIELD)) {
														// ...that's not completed...
														String status = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.STATUS_FIELD);
														boolean completed = (MiscUtil.hasString(status) && status.equals("completed"));
														if (!completed) {
															// ...show it as being overdue.
															fr.setColumnOverdueDate(fc, Boolean.TRUE);
															if      (isSurvey)    value += (" " + NLT.get("survey.overdue"   ));
															else if (isMilestone) value += (" " + NLT.get("milestone.overdue"));
														}
													}
												}
												fr.setColumnValue(fc, (null == (value) ? "" : value));
											}
											
											// No, we aren't working on a Date field!  Are we
											// working on a file size field?
											else if (csk.equals(Constants.FILE_SIZE_FIELD)) {
												// Yes!  Generate a
												// display string for
												// it.
												if (isEntityFolderEntry)
												     value = buildFileSizeDisplayFromKBSize(user, value);
												else value = "";
											}
		
											// No, we aren't working on a file size field!  Are we
											// working on the status field of a milestone?
											else if (csk.equals(Constants.STATUS_FIELD) && isMilestone) {
												// Yes!  Do we have a status value for it?
												if (MiscUtil.hasString(value)) {
													// Yes!  Pull its localized string from the
													// resources.
													value = NLT.get(("__milestone_status_" + value), value);
												}
											}
											
											// Use what ever String value we arrived at.
											fr.setColumnValue(fc, (null == (value) ? "" : value));
										}
									}
								}
							}
						}
						
						finally {
							if (PROFILE_PER_COLUMN) {
								SimpleProfiler.stop(perColProfile   );
								SimpleProfiler.stop(perRowColProfile);
							}
						}
					}
					
					// Add the FolderRow we just built to the
					// List<FolderRow> of them.
					folderRows.add(fr);
				}
				
				finally {
					if (PROFILE_PER_ROW) {
						SimpleProfiler.stop(perRowProfile);
					}
				}
			}

			// Did we add any rows with assignment information that
			// needs to be completed?
			if (completeAIsRequired) {
				// Yes!  We need to complete the definition of the
				// AssignmentInfo objects.
				//
				// When initially built, the AssignmentInfo's in the
				// List<AssignmentInfo>'s only contain the assignee
				// IDs.  We need to complete them with each assignee's
				// title, ...
				completeAIs(bs, request, folderRows);
			}

			// Did we add any rows with comment information that
			// needs to be completed?
			if (completeCIsRequired) {
				// Yes!  We need to complete the definition of the
				// CommentsInfo objects.
				//
				// When initially built, the CommentsInfo's contained
				// false for whether comments are disabled on folder
				// entries.  We need to complete these values
				// correctly.
				completeCIs(bs, request, folderRows);
			}

			// Did we add any rows that were nested folders whose
			// rights needs to be completed?
			if (completeNFRightsRequired) {
				// Yes!  We need to complete the definition of the
				// EntryTitleInfo objects with the user's rights to
				// add to the nested folders.
				//
				// When initially built, the EntryTitleInfo's in the
				// rows don't contain that information.  We need it
				// to support drag and drop  into nested folders.
				completeNFRights(bs, request, folderRows);
			}
			
			// Walk the List<FolderRow>'s performing any remaining
			// fixups on each as necessary.
			completeUserInfo(bs, request, folderColumns, folderRows, isProfilesRootWS, isManageUsers, isFilr);

			// Is the user viewing pinned entries?
			if (viewPinnedEntries) {
				// Yes!  Then we need to sort the rows using the user's
				// current sort criteria.
				Comparator<FolderRow> comparator =
					new FolderRowComparator(
						folderDisplayData.getFolderSortBy(),
						folderDisplayData.getFolderSortDescend(),
						folderColumns);
				
				Collections.sort(folderRows, comparator);
			}

			// If we have a quick filter and we processing a
			// 'Shared by/with Me' collection and we have some rows...
			if (MiscUtil.hasString(quickFilter) && (isCollectionSharedByMe || isCollectionSharedWithMe || isCollectionSharedPublic) && (!(folderRows.isEmpty()))) {
				// ...we need to apply the quick filter to the
				// ...List<FolderRow>.
				folderRows = filterSharedMeFolderRows(
					folderColumns,
					folderRows,
					quickFilter);
				totalRecords = folderRows.size();
			}
			
			// Finally, return the List<FolderRow> wrapped in a
			// FolderRowsRpcResponseData.
			FolderRowsRpcResponseData reply =
				new FolderRowsRpcResponseData(
					folderRows,
					start,
					totalRecords,
					(totalIsApproximate ? TotalCountType.APPROXIMATE : TotalCountType.EXACT),
					contributorIds,
					errorList);
			
			// If we get here, reply refers to a
			// FolderRowsRpcResponseData containing the rows from the
			// requested binder.  Return it.
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				dumpFolderRowsRpcResponseData(binder, reply);
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
					"GwtViewHelper.getFolderRows( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Constructs and returns a GuestInfo from a Principal.
	 */
	private static GuestInfo getGuestInfoFromPrincipal(AllModulesInjected bs, HttpServletRequest request, Principal p) {
		Long pId = p.getId();
		GuestInfo reply = new GuestInfo(
			pId,
			p.getTitle(),
			PermaLinkUtil.getUserPermalink(request, String.valueOf(pId)));
		reply.setAvatarUrl(GwtServerHelper.getPrincipalAvatarUrl(bs, request, p));
		reply.setEmailAddress(      p.getEmailAddress()      );
		reply.setMobileEmailAddress(p.getMobileEmailAddress());
		reply.setTextEmailAddress(  p.getTxtEmailAddress()   );
		if (p instanceof User) {
			reply.setPhone(((User) p).getPhone());
		}
		return reply;
	}

	/*
	 * Returns a map containing the search filter to use to read the
	 * rows from a folder.
	 */
	@SuppressWarnings("unchecked")
	private static Map getFolderSearchFilter(AllModulesInjected bs, Binder binder, UserProperties userFolderProperties, String searchTitle) {
		Map result = new HashMap();
		BinderHelper.addSearchFiltersToOptions(bs, binder, userFolderProperties, true, result);
		if (MiscUtil.hasString(searchTitle)) {
			result.put(ObjectKeys.SEARCH_TITLE, searchTitle);
		}
		return result;
	}

	/*
	 * Returns true if a folder supports pinning and folder otherwise.
	 */
	private static boolean getFolderSupportsPinning(BinderInfo folderInfo) {
		return getFolderTypeSupportsPinning(folderInfo.getFolderType());
	}
	
	/**
	 * Returns true if a FolderType is one that supports pinning and
	 * false otherwise.
	 * 
	 * @param ft
	 */
	public static boolean getFolderTypeSupportsPinning(FolderType ft) {
		boolean reply;
		switch (ft) {
		default:          reply = false; break;
		case DISCUSSION:  reply = true;  break;
		}
		return reply;
	}
	
	/**
	 * Returns the HTML from the executing a JSP.
	 * 
	 * @param bs
	 * @param request
	 * @param response
	 * @param servletContext
	 * @param jspType
	 * @param model
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static JspHtmlRpcResponseData getJspHtml(AllModulesInjected bs, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, VibeJspHtmlType jspType, Map<String, Object> model) throws GwtTeamingException {
		String html    = "";
		String jspPath = null;
		
		try {
			// The following are the supported JSP types.
			AdminModule   am = bs.getAdminModule();
			BinderModule  bm = bs.getBinderModule();
			ProfileModule pm = bs.getProfileModule();
			switch (jspType) {
			case ACCESSORY_PANEL: {
				try {
					// Build request and render objects needed to build
					// the toolbar.
					String		portletName = "ss_forum";
					PortletInfo	portletInfo = ((PortletInfo) AdaptedPortlets.getPortletInfo(portletName));
					
					RenderRequestImpl renderReq = new RenderRequestImpl(request, portletInfo, AdaptedPortlets.getPortletContext());
					
					Map<String, Object> params = new HashMap<String, Object>();
					params.put(KeyNames.PORTLET_URL_PORTLET_NAME, new String[]{portletName});
					renderReq.setRenderParameters(params);
					
					RenderResponseImpl renderRes = new RenderResponseImpl(renderReq, response, portletName);
					String charEncoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
					renderRes.setContentType("text/html; charset=" + charEncoding);
					renderReq.defineObjects(portletInfo.getPortletConfig(), renderRes);
					
					renderReq.setAttribute(PortletRequest.LIFECYCLE_PHASE, PortletRequest.RENDER_PHASE);

					// Display the whole accessory panel.
					User user = RequestContextHolder.getRequestContext().getUser();
					String s_binderId = (String) model.get("binderId");
					Binder binder = bm.getBinder(Long.valueOf(s_binderId));

					UserProperties userProperties = new UserProperties(user.getId());
					Map userProps = new HashMap();
		    		if (null != userProperties.getProperties()) {
		    			userProps = userProperties.getProperties();
		    		}

		    		if (null != user) {
		    			userProperties = pm.getUserProperties(user.getId());
		    		}
		    		
					// Build the 'Add Accessory' toolbar.
		    		Map<String,Object> panelModel = new HashMap<String,Object>();
					DashboardHelper.getDashboardMap(binder, userProps, panelModel);
					Toolbar dashboardToolbar = new Toolbar();
					BinderHelper.buildDashboardToolbar(renderReq, renderRes, bs, binder, dashboardToolbar, panelModel);

					// Set up the beans used by the jsp.
					panelModel.put(WebKeys.BINDER_ID, binder.getId());
					panelModel.put(WebKeys.BINDER, binder);
					panelModel.put(WebKeys.USER_PROPERTIES, userProps);
					panelModel.put(WebKeys.SNIPPET, true);	// Signal that <html> and <head> should not be output.
					panelModel.put(WebKeys.DASHBOARD_TOOLBAR, dashboardToolbar.getToolbar());
					
					jspPath = "definition_elements/view_dashboard_canvas.jsp";
					html = GwtServerHelper.executeJsp(bs, request, response, servletContext, jspPath, panelModel);
					break;
				}
				
				catch(Exception e) {
					// Return an error back to the user...
					String[] args = new String[]{e.getMessage()};
					html = NLT.get("errorcode.dashboardComponentViewFailure", args);
					
					// ...and log the error.
					GwtLogHelper.error(m_logger, "GwtViewHelper.getJspHtml( EXCEPTION:1 ):  ", e);
				}
			}
			
			case ACCESSORY: {
				try {
					// Set up bean used by the dashboard component (aka
					// accessory).
					User	user        = RequestContextHolder.getRequestContext().getUser();
					String	s_binderId  = ((String) model.get("binderId"));
					Binder	binder      = bm.getBinder(Long.valueOf(s_binderId));
					String	componentId = ((String) model.get("ssComponentId"));
					String	scope       = componentId.split("_")[0];

					UserProperties	userProperties = new UserProperties(user.getId());
					Map				userProps      = new HashMap();
		    		if (null != userProperties.getProperties()) {
		    			userProps = userProperties.getProperties();
		    		}
		    		if (null != user) {
		    			userProperties = pm.getUserProperties(user.getId());
		    		}
		    		if ((null != componentId) && (!(componentId.equals("")))) {
			    		Map<String,Object> componentModel = new HashMap<String,Object>();
						DashboardHelper.getDashboardMap(binder, userProps, componentModel, scope, componentId, false);
						Map<String,Object> componentDashboard = (Map<String,Object>) componentModel.get("ssDashboard");
						componentDashboard.put("ssComponentId", componentId);
						componentModel.put(WebKeys.BINDER_ID, binder.getId());
						componentModel.put(WebKeys.BINDER, binder);
						jspPath = "definition_elements/view_dashboard_component.jsp";
						html = GwtServerHelper.executeJsp(bs, request, response, servletContext, jspPath, componentModel);
		    		}
					break;
				}
				
				catch(Exception e) {
					// Return an error back to the user...
					String[] args = new String[]{e.getMessage()};
					html = NLT.get("errorcode.dashboardComponentViewFailure", args);
					
					// ...and log the error.
					GwtLogHelper.error(m_logger, "GwtViewHelper.getJspHtml( EXCEPTION:2 ):  ", e);
				}
			}
			
			case ADMIN_REPORT_CHANGELOG: {
				// Create a model Map for the change log report JSP
				// page...
				Map<String, Object> changeLogModel = buildReportModelMap(model);
	    		
	    		// ...and run the JSP to produce the target HTML.
				html = GwtServerHelper.executeJsp(
					bs,
					request,
					response,
					servletContext,
					buildJspName(WebKeys.VIEW_ADMIN_CHANGELOG),
					changeLogModel);

				break;
			}
				
			case ADMIN_REPORT_CREDITS: {
				// Create a model Map for the credits JSP page...
				Map<String, Object> creditsModel = buildReportModelMap(model);
	    		
	    		// ...and run the JSP to produce the target HTML.
				html = GwtServerHelper.executeJsp(
					bs,
					request,
					response,
					servletContext,
					buildJspName(WebKeys.VIEW_ADMIN_CREDITS),
					creditsModel);
				
				break;
			}

			case ADMIN_REPORT_DATA_QUOTA_EXCEEDED:
			case ADMIN_REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED:
			case ADMIN_REPORT_DISK_USAGE: {
				// Create a model Map for the report's JSP page...
				Map<String, Object> reportModel = buildReportModelMap(model);
				
				// ...add the specific beans required by these report
				// ...pages...
				Map aclMap = BinderHelper.getAccessControlMapBean(reportModel);
				aclMap.put("generateQuotaReport", am.testAccess(AdminOperation.report));

				// ...decided on the appropriate JSP view for the
				// ...report...
				String jspView;
				switch (jspType) {
				default:
				case ADMIN_REPORT_DISK_USAGE:                     jspView = WebKeys.VIEW_QUOTA_REPORT;                    break;
				case ADMIN_REPORT_DATA_QUOTA_EXCEEDED:            jspView = WebKeys.VIEW_QUOTA_EXCEEDED_REPORT;           break;
				case ADMIN_REPORT_DATA_QUOTA_HIGHWATER_EXCEEDED:  jspView = WebKeys.VIEW_QUOTA_HIGHWATER_EXCEEDED_REPORT; break;
				}
	    		
	    		// ...and run that JSP to produce the target HTML.
				html = GwtServerHelper.executeJsp(
					bs,
					request,
					response,
					servletContext,
					buildJspName(jspView),
					reportModel);
				
				break;
			}
			
			case ADMIN_REPORT_XSS: {
				// Create a model Map for the XSS JSP page...
				Map<String, Object> xssModel = buildReportModelMap(model);

				// ...add the specific beans required by the XSS
				// ...page...
	    		Map accessControlMap = BinderHelper.getAccessControlMapBean(xssModel);
	    		accessControlMap.put("generateReport", am.testAccess(AdminOperation.report));
	    		
	    		Document pTree = DocumentHelper.createDocument();
	        	Element rootElement = pTree.addElement(DomTreeBuilder.NODE_ROOT);
	        	Document wsTree = bm.getDomBinderTree(RequestContextHolder.getRequestContext().getZoneId(), new WsDomTreeBuilder(null, true, bs, new SearchTreeHelper()), 1);
	        	rootElement.appendAttributes(wsTree.getRootElement());
	        	rootElement.appendContent(wsTree.getRootElement());
	     		xssModel.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
	     		xssModel.put(WebKeys.WORKSPACE_DOM_TREE, pTree);		
	    		
	    		// ...and run the JSP to produce the target HTML.
				html = GwtServerHelper.executeJsp(
					bs,
					request,
					response,
					servletContext,
					buildJspName(WebKeys.VIEW_XSS_REPORT),
					xssModel);
				
				break;
			}
			
			case CUSTOM_JSP:  {
				// Create a model Map for the custom JSP...
				Map<String, Object> htmlElementModel = buildCustomJspModelMap(model);
	    		
				jspPath = ("/WEB-INF/jsp/custom_jsps/" + buildJspName((String) model.get("customJsp")));
				try {
		    		// ...and run the JSP to produce the target HTML.
					html = GwtServerHelper.executeJsp(
						bs,
						request,
						response,
						servletContext,
						null,		// null -> Not the JSP name...
						jspPath,	// ...just it's path.
						htmlElementModel);
				}
				
				catch (Exception e) {
					// ...simply log any errors and assuming there
					// ...wasn't a custom JSP.
					GwtLogHelper.error(m_logger, "GwtViewHelper.getJspHtml( " + jspType.name() + ":" + jspPath + ", EXCEPTION ):  ", e);
					html = "";
				}
				
				break;
			}
			
			default: 
				// Log an error that we encountered an unhandled command.
				m_logger.error("JspHtmlRpcResponseData( Unknown jsp type ):  " + jspType.name());
				break;
			}
			
			return new JspHtmlRpcResponseData(html, model);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getJspHtml( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Returns a date/time stamp of the last time the user logged in or
	 * null if it can't be determined.
	 */
	private static Date getLastUserLogin(AllModulesInjected bs, User user) {
		Date reply = null;
		
		try {
			// Has this user ever logged in?
			Set<Long> memberIds = new HashSet<Long>();
			memberIds.add(user.getId());
			Date start = user.getFirstLoginDate();
			if (null == start) {
				// No!  Then they can't have a last login.
				return null;
			}
			
			// Can we get a login report for this user?
			List<Map<String, Object>> loginReport = bs.getReportModule().generateLoginReport(
				start,		// Start the report with their first login...
				new Date(),	// ...and end it at the current time.
				WebKeys.URL_REPORT_OPTION_TYPE_SHORT,
				ReportModule.LAST_LOGIN_SORT,
				ReportModule.LOGIN_DATE_SORT,
				memberIds);
			if (MiscUtil.hasItems(loginReport)) {
				// Yes!  Does it contain their last login?
				Map<String, Object> loginMap = loginReport.get(0);
				String lastLogin = ((String) loginMap.get(ReportModule.LAST_LOGIN));	// Example:  '2012-12-17 02:24:01 PM'
				if (MiscUtil.hasString(lastLogin)) {
					// Yes!  Parse and return it.
					SimpleDateFormat fmt = new SimpleDateFormat(ReportModule.LOGIN_REPORT_DATE_FORMAT);
					reply = fmt.parse(lastLogin);
				}
			}
		}
		catch (ParseException e) {
			GwtLogHelper.error(m_logger, "GwtViewHelper.getLastUserLogin( SOURCE EXCEPTION ):  ", e);
			reply = null;
		}

		// If we get here, reply refers to a date/time stamp of the
		// last time the user logged in or is null if it couldn't be
		// determined.  Return it.
		return reply;
	}

	/*
	 * Extracts a Long value from a entry Map.
	 */
	@SuppressWarnings("unchecked")
	public static Long getLongFromMap(Map entryMap, String key) {
		Object v = entryMap.get(key);
		Long reply;
		if      (v instanceof String) reply = Long.parseLong((String) v);
		else if (v instanceof Long)   reply = ((Long) v);
		else                          reply = -1L;
		return reply;
	}

	/**
	 * Returns a ViewFolderEntryInfoRpcResponseData corresponding to
	 * the previous/next folder entry to the given EntityId.
	 * 
	 * @param bs
	 * @param request
	 * @param entityId
	 * @param previous
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static ViewFolderEntryInfoRpcResponseData getNextPreviousFolderInfo(AllModulesInjected bs, HttpServletRequest request, EntityId entityId, boolean previous) throws GwtTeamingException {
		try {
			// Allocate a ViewFolderEntryInfoRpcResponseData we can
			// return.
			ViewFolderEntryInfoRpcResponseData reply = new ViewFolderEntryInfoRpcResponseData();

			// Setup an options Map with the sorting information
			// current in effect for the folder.
			Long								folderId   = entityId.getBinderId();
			BinderInfo							folderInfo = GwtServerHelper.getBinderInfo(bs, request, folderId  );
			FolderDisplayDataRpcResponseData	fdd        = getFolderDisplayData(         bs, request, folderInfo);
			Map options = new HashMap();
			options.put(ObjectKeys.SEARCH_SORT_BY,                     fdd.getFolderSortBy()      );
			options.put(ObjectKeys.SEARCH_SORT_DESCEND, String.valueOf(fdd.getFolderSortDescend()));
			
			// Can we get the ID of the previous/next folder entry?
			Folder	folder        = bs.getFolderModule().getFolder(folderId);
			Long	targetEntryId = BinderHelper.getNextPrevEntry(bs, folder, entityId.getEntityId(), (!previous), options);
			if (null != targetEntryId) {
				// Yes!  Create a ViewFolderEntryInfo for it and store
				// it in the reply.
				reply.setViewFolderEntryInfo(
					buildViewFolderEntryInfo(
						bs,
						request,
						folderId,
						targetEntryId));
			}
			
			// If we get here, reply refers to the
			// ViewFolderEntryInfoRpcResponseData for the previous/next
			// folder entry.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getNextPreviousFolderInfo( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Get the parent container's DN.  May be null.
	 * 
	 * Copied the logic for this from
	 * LdapModuleImpl.getParentContainerDn().
	 */
	private static String getParentContainerDN(String baseDN) {
		// If we weren't given a base DN to parse...
		if (null == baseDN) {
			// ...return null.
			return null;
		}
		
		// Find the first ','
		int index = baseDN.indexOf( ',' );
		if ((0 < index) && ((index + 1) < baseDN.length())) {
			// Does the parent DN start with 'ou=' or 'o='?
			String parentDN = baseDN.substring(index + 1);
			if (parentDN.startsWith("ou=") || parentDN.startsWith("o=")) {
				// Yes!  Return it.
				return parentDN;
			}
		}

		// If we get here, we couldn't determine the parent DN.  Return
		// null.
		return null;
	}
	
	/*
	 * Returns a List<Long> of the entity ID's of the entries that are
	 * pinned in the given folder. 
	 */
	@SuppressWarnings("unchecked")
	private static List<Map> getPinnedEntries(AllModulesInjected bs, Folder folder, UserProperties userFolderProperties, List<Long> pinnedEntryIds, boolean returnEntries) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getPinnedEntries()");
		try {
			// Allocate a List<Map> for the search results for the
			// entries pinned in this folder.
			List<Map> pinnedEntrySearchMaps = new ArrayList<Map>();
	
			// Are there any pinned entries stored in the user's folder
			// properties on this folder?
			Map properties = userFolderProperties.getProperties();
			String pinnedEntries;
			if ((null != properties) && properties.containsKey(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES))
			     pinnedEntries = ((String) properties.get(ObjectKeys.USER_PROPERTY_PINNED_ENTRIES));
			else pinnedEntries = null;
			if (MiscUtil.hasString(pinnedEntries)) {
				// Yes!  Parse them converting the String ID's to
				// Long's.
				if (pinnedEntries.lastIndexOf(",") == (pinnedEntries.length() - 1)) { 
					pinnedEntries = pinnedEntries.substring(0, (pinnedEntries.length() - 1));
				}
				String[] peArray = pinnedEntries.split(",");
				List<Long> peSet = new ArrayList();
				for (int i = 0; i < peArray.length; i += 1) {
					String pe = peArray[i];
					if (MiscUtil.hasString(pe)) {
						peSet.add(Long.valueOf(pe));
					}
				}
	
				// Scan the pinned entries.
				FolderModule fm = bs.getFolderModule();
				List<org.apache.lucene.document.Document> pinnedFolderEntriesList = new ArrayList<org.apache.lucene.document.Document>();
				SortedSet<FolderEntry> pinnedFolderEntriesSet = fm.getEntries(peSet);
				for (FolderEntry entry:  pinnedFolderEntriesSet) {
					// Is this entry still viable in this folder?
					if (!(entry.isPreDeleted()) && entry.getParentBinder().equals(folder)) {
						// Yes!  Track its ID.
						pinnedEntryIds.add(entry.getId());
	
						// Are we returning the entries too?
						if (returnEntries) {
							// Yes!  Save its indexDoc.
							org.apache.lucene.document.Document indexDoc = fm.buildIndexDocumentFromEntry(entry.getParentBinder(), entry, null);
							pinnedFolderEntriesList.add(indexDoc);
						}
					}
				}
	
				// Are we returning the entries too?
				if (returnEntries) {
					// Construct search Map's from the indexDoc's for
					// the pinned entries.
					pinnedEntrySearchMaps = org.kablink.teaming.module.shared.SearchUtils.getSearchEntries(pinnedFolderEntriesList);
					bs.getFolderModule().getEntryPrincipals(pinnedEntrySearchMaps);
				}
			}
			
			// If we get here, pinnedEntrySearchMaps refers to a
			// List<Map> of search Map's for the pinned entries.
			// Return it.
			return pinnedEntrySearchMaps;
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Given a List<Long> of principal IDs read from entry maps,
	 * returns an equivalent List<PrincipalInfo> object.
	 * 
	 * @param bs
	 * @param request
	 * @param piList
	 * @param mdList
	 * @param uwsPairs
	 */
	public static void getUserInfoFromPIds(AllModulesInjected bs, HttpServletRequest request, List<PrincipalInfo> piList, List<MobileDevicesInfo> mdList, List<UserWorkspacePair> uwsPairs) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getUserInfoFromPIds()");
		try {
			// Can we map the List<Long> of principal IDs to any
			// UserWorkspacePair's?
			if (MiscUtil.hasItems(uwsPairs)) {
				// Yes!  Scan them.
				MobileDeviceModule mdm = bs.getMobileDeviceModule();
				for (UserWorkspacePair uwsPair:  uwsPairs) {
					User user = uwsPair.getUser();
					Long pId  = user.getId();
					String perUserProfile;
					if (PROFILE_PER_USER) {
						perUserProfile = "GwtViewHelper.getUserInfoFromPIds(Process user:  " + pId + ")";
						SimpleProfiler.start(perUserProfile);
					}
					else {
						perUserProfile = null;
					}
					PrincipalInfo pi;
					try {
						// Construct a PrincipalInfo for each...
						pi = getPIFromUser(bs, request, user);
					}
					finally {
						if (PROFILE_PER_USER) {
							SimpleProfiler.stop(perUserProfile);
						}
					}
					
					if (PROFILE_PER_USER) {
						perUserProfile = "GwtViewHelper.getUserInfoFromPIds(Process user presence:  " + pId + ")";
						SimpleProfiler.start(perUserProfile);
					}
					try {
						// ...setup an appropriate GwtPresenceInfo...
						GwtPresenceInfo presenceInfo;
						if (GwtServerHelper.isPresenceEnabled())
						     presenceInfo = GwtServerHelper.getPresenceInfo(user);
						else presenceInfo = null;
						if (null == presenceInfo) {
							presenceInfo = GwtServerHelper.getPresenceInfoDefault();
						}
						if (null != presenceInfo) {
							pi.setPresence(presenceInfo);
							pi.setPresenceDude(GwtServerHelper.getPresenceDude(presenceInfo));
						}
					}
					finally {
						if (PROFILE_PER_USER) {
							SimpleProfiler.stop(perUserProfile);
						}
					}

					// Is the user's mobile device information being
					// requested?
					if (null != mdList) {
						// Yes!  How many MobileDevice's do they have?
						List<MobileDevice> mds = mdm.getMobileDeviceList(user.getId());
						int mdCount = mds.size();
						
						// Add a MobileDevicesInfo to the
						// List<MobileDevicesInfo> we were given.
						mdList.add(new MobileDevicesInfo(user.getId(), mdCount));
					}
					
					// ...and add the PrincipalInfo to the reply list.
					piList.add(pi);
				}
			}
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Given a Principal's ID read from an entry map, returns an
	 * equivalent PrincipalInfo object.
	 */
	private static PrincipalInfo getPIFromPId(AllModulesInjected bs, HttpServletRequest request, Long pId) {
		List<Long> pIds = new ArrayList<Long>();
		pIds.add(pId);
		List<PrincipalInfo>     piList   = new ArrayList<PrincipalInfo>();
		List<UserWorkspacePair> uwsPairs = getUserWorkspacePairs(pIds, null, false);
		getUserInfoFromPIds(bs, request, piList, null, uwsPairs);
		PrincipalInfo reply;
		if (MiscUtil.hasItems(piList))
		     reply = piList.get(0);	// There will only ever be one.
		else reply = null;
		return reply;
	}

	/**
	 * Constructs and returns a ProfileInfo object for the given user.f
	 * 
	 * @param bs
	 * @param request
	 * @param user
	 * 
	 * @return
	 */
	public static PrincipalInfo getPIFromUser(AllModulesInjected bs, HttpServletRequest request, User user) {
		Long userId = user.getId();
		PrincipalInfo pi = PrincipalInfo.construct(userId);
		pi.setTitle(user.getTitle());
		Long    userWSId      = user.getWorkspaceId();
		boolean userHasWS     = (null != userWSId);
		boolean userWSInTrash = (userHasWS && user.isWorkspacePreDeleted());
		pi.setUserDisabled(  user.isDisabled()                   );
		pi.setUserExternal(!(user.getIdentityInfo().isInternal()));
		pi.setUserHasWS(     userHasWS                           );
		pi.setUserPerson(    user.isPerson()                     );
		pi.setUserWSInTrash( userWSInTrash                       );
		pi.setEmailAddress(  user.getEmailAddress()              );
		pi.setViewProfileEntryUrl(getViewProfileEntryUrl(bs, request, userId, bs.getProfileModule().getProfileBinderId()));
		pi.setPresenceUserWSId(user.getWorkspaceId());
		pi.setAvatarUrl(GwtServerHelper.getUserAvatarUrl(bs, request, user));
		return pi;
	}

	/**
	 * Returns the display string to use for the 'Admin' column in a
	 * view for a Principal's admin rights.
	 * 
	 * @param bs
	 * @param p
	 * 
	 * @return
	 */
	public static String getPrincipalAdminRightsString(AllModulesInjected bs, Principal p) {
		Long pId = p.getId();
		boolean hasAdminRights = AdminHelper.isSiteAdminMember(pId);
		
		String reply = "";
		if ((p instanceof GroupPrincipal) || (p instanceof Group)) {
			if (hasAdminRights)
			     reply = NLT.get( "siteAdmin.group" );
			else reply = "";
		}
		
		else if (p instanceof UserPrincipal) {
			User user = ((User) p );
			reply = NLT.get("general.NA");
			if ((!(user.isAdmin())) && user.isPerson() && user.getIdentityInfo().isInternal()) {
				// Yes!  Check their administrator
				// rights assignment...
				boolean isSiteAdmin = bs.getAdminModule().testUserAccess(user, AdminOperation.manageFunction);
				String  resKey;
				if      (hasAdminRights) resKey = "siteAdmin.direct";
				else if (isSiteAdmin)    resKey = "siteAdmin.group";
				else                     resKey = null;
				if (null == resKey)
				     reply = "";
				else reply   = NLT.get(resKey);
			}
			
			// No, this isn't somebody that may
			// have administrator rights set on
			// them!  Is it the built-in admin
			// user?
			else if (user.isAdmin()) {
				// Yes!  Show them as being the
				// built-in admin.
				reply = NLT.get("siteAdmin.builtIn");
			}
		}
		
		return reply;
	}

	/**
	 * Returns a ProfileEntryInfoRpcRequestData containing information
	 * about a user's profile.
	 * 
	 * @param bs
	 * @param request
	 * @param userId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static ProfileEntryInfoRpcResponseData getProfileEntryInfo(AllModulesInjected bs, HttpServletRequest request, Long userId) throws GwtTeamingException {
		try {
			// Allocate an profile entry info response we can return.
			ProfileEntryInfoRpcResponseData reply = new ProfileEntryInfoRpcResponseData();

			// Can we access the user in question?
			List<String> userIdList = new ArrayList<String>();
			String userIdS = String.valueOf(userId);
			userIdList.add(userIdS);
			List resolvedList = ResolveIds.getPrincipals(userIdList, false);
			if (MiscUtil.hasItems(resolvedList)) {
				// Yes!  Extract the profile information we need to
				// display.
				User user = ((User) resolvedList.get(0));
				Locale userLocale = GwtServerHelper.getCurrentUser().getLocale();
				addProfileAttribute(reply, "title",        user.getTitle());
				addProfileAttribute(reply, "jobTitle",     GwtProfileHelper.getJobTitle(user));
				addProfileAttribute(reply, "emailAddress", user.getEmailAddress());
				addProfileAttribute(reply, "phone",        user.getPhone());
				addProfileAttribute(reply, "timeZone",     user.getTimeZone().getDisplayName(userLocale));
				addProfileAttribute(reply, "locale",       user.getLocale().getDisplayName(  userLocale));
				if (!(Utils.checkIfFilr())) {
					addProfileAttribute(reply, "mobileEmailAddress", user.getMobileEmailAddress());
					addProfileAttribute(reply, "txtEmailAddress",    user.getTxtEmailAddress());
				}

				// Does the user have an 'About Me' defined?
				CustomAttribute ca = user.getCustomAttribute("aboutMe");
				if (null != ca) {
					// Yes!  Does it have a value?
					Object		aboutMeO = ca.getValue();
					Description	aboutMeDesc;
					if      (aboutMeO instanceof String)      aboutMeDesc = new Description((String)aboutMeO);
					else if (aboutMeO instanceof Description) aboutMeDesc = ((Description) ca.getValue());
					else                                      aboutMeDesc = null;
					if (null != aboutMeDesc ) {
						// Yes!  Replace the mark up...
						String aboutMeHtml = MarkupUtil.markupStringReplacement(null, null, request, null, user, aboutMeDesc.getText(), WebKeys.MARKUP_VIEW);
						if (null != aboutMeHtml){
							// Added a length of one to skip over a
							// return characters that are in somehow in
							// the value of the attribute.
							if (1 < aboutMeHtml.length()){
								// ...and store anything left in the
								// ...reply.
								reply.setAboutMeHtml(aboutMeHtml);
							}
						}
					}
				}
				
				// Store the URL for the user's avatar, if they have
				// one.
				reply.setAvatarUrl(GwtServerHelper.getUserAvatarUrl(bs, request, user));

				// Does the current user have rights to modify users?
				ProfileModule pm = bs.getProfileModule();
				String profilesWSIdS = String.valueOf(pm.getProfileBinderId());
				if (pm.testAccess(user, ProfileOperation.modifyEntry)) {
					// Yes!  Store the modify URL for this user.
					AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
					url.setParameter(WebKeys.ACTION,         WebKeys.ACTION_MODIFY_PROFILE_ENTRY);
					url.setParameter(WebKeys.URL_BINDER_ID,  profilesWSIdS                      );
					url.setParameter(WebKeys.URL_ENTRY_ID,   userIdS                            );
					reply.setModifyUrl(url.toString());
				}
			}

			// If we get here, reply refers to an
			// ProfileEntryInfoRpcResponseData containing the user's
			// profile information.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getProfileEntryInfo( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Returns a Map<String, String> for the query parameters from a
	 * URL.
	 */
	private static Map<String, String> getQueryParameters(String url) {
		URI uri;
		try {
			// Can we parse the URL?
			uri = new URI(url);
		} catch (URISyntaxException e) {
			// No!  Log the error and bail.
			GwtLogHelper.error(m_logger, "GwtViewHelper.getQueryParameters( URL:  '" + url + "', URL Parsing Exception ):  ", e);
			return null;
		}
		
		// Allocate a Map<String, String> to return the parameters.
		Map<String, String> reply = new HashMap<String, String>();

		// Does the URL contain a query?
		String query = uri.getQuery();
		if (null != query) {
			// Yes!  Split it at the &'s.
			String[] parameters = query.split("&");
			
			// Scan the parameters.
			for (String p:  parameters) {
				// Does this parameter contain an '='?
				int eq = p.indexOf('=');
				if ((-1) != eq) {
					// Yes!  Does it have a name part?
					String name = p.substring(0, eq);
					if (MiscUtil.hasString(name)) {
						// Yes!  Add it to the map.
						reply.put(name.toLowerCase(), p.substring(eq + 1));
					}
				}
			}
		}
		
		else {
			// No, the URL didn't contain a query!  Does it contain a
			// path?
			String path = uri.getPath();
			if (null != path) {
				// Yes!  Split it at the /'s.
				String[] parameters = path.split("/");
				
				// Scan the parameters by 2's.
				int c = (parameters.length - 1);
				for (int i = 0; i < c; i += 2) {
					// Does this parameter have a name?
					String name = parameters[i];
					if (MiscUtil.hasString(name)) {
						// Yes!  Add it to the map.
						reply.put(name.toLowerCase(), parameters[i + 1]);
					}
				}
			}
		}
		
		// If we get here, reply refers to a Map<String, String> of the
		// parameters from the URL.  Return it.
		return reply;
	}

	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Boolean value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	private static boolean getQueryParameterBoolean(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			boolean reply;
			try                 {reply = Boolean.parseBoolean(v);}
			catch (Exception e) {reply = false;                  }
			return reply;
		}		
		return false;
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Boolean value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	@SuppressWarnings("unused")
	private static int getQueryParameterInt(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			int reply;
			try                 {reply = Integer.parseInt(v);}
			catch (Exception e) {reply = (-1);               }
			return reply;
		}		
		return (-1);
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Long value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	private static Long getQueryParameterLong(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			Long reply;
			try                 {reply = Long.parseLong(v);}
			catch (Exception e) {reply = null;             }
			return reply;
		}		
		return null;
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter.
	 * 
	 * If the value can't be found, null is returned. 
	 */
	private static String getQueryParameterString(Map<String, String> nvMap, String name) {
		String reply;
		if (MiscUtil.hasItems(nvMap))
		     reply = nvMap.get(name.toLowerCase());
		else reply = null;
		return ((null == reply) ? "" : reply);
	}

	/*
	 * Returns the title to use for comparisons of a sharer or
	 * share recipient.
	 */
	@SuppressWarnings("unchecked")
	private static String getRecipientTitle(AllModulesInjected bs, RecipientType rt, Long sId) {
		String reply = "";
		try {
			switch (rt) {
			case user:
			case group:
				// Resolve the user/group...
				List<Long> principalIds = new ArrayList<Long>();
				principalIds.add(sId);
				List<Principal> principals = ResolveIds.getPrincipals(principalIds, false);
				if (MiscUtil.hasItems(principalIds)) {
					// ...and return its title.
					reply = ((Principal) principals.get(0)).getTitle();
				}
				break;
				
			case team:
				// Return the title of the binder.
				Binder binder = bs.getBinderModule().getBinder(sId);
				reply = binder.getTitle();
				break;
				
			case publicLink:
				// Return a localized public link string.
				reply = NLT.get("share.recipientType.title.productLink");
				break;
			}
		}
		catch (Exception ex) {/* Ignore. */}
		return reply;
	}
	
	/*
	 * Returns a Map of the search results for root workspaces based on
	 * the criteria in the options Map.
	 */
	@SuppressWarnings("unchecked")
	private static Map getRootWorkspaceEntries(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getRootWorkspaceEntries()");
		try {
			// Construct the base criteria for finding the child
			// binders of the given Binder...
			Criteria crit = new Criteria();
			crit.add(eq(Constants.DOC_TYPE_FIELD,          Constants.DOC_TYPE_BINDER));
			crit.add(eq(Constants.BINDERS_PARENT_ID_FIELD, String.valueOf(binder.getId())));

			// ...factor in the appropriate sorting for the search...
			boolean sortAscend = (!(GwtUIHelper.getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false                   )));
			String  sortBy     =    GwtUIHelper.getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      Constants.SORT_TITLE_FIELD);
			crit.addOrder(new Order(Constants.ENTITY_FIELD, sortAscend));
			crit.addOrder(new Order(sortBy,                 sortAscend));

			// ...factor in any quick filter in affect...
			addQuickFilterToCriteria(quickFilter, crit);

			// ...and finally, initiate the search.
			return
				bs.getBinderModule().executeSearchQuery(
					crit,
					Constants.SEARCH_MODE_NORMAL,
					GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_OFFSET,   0),
					GwtUIHelper.getOptionInt(options, ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_SUB_BINDERS),
					null);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Returns a SelectedUsersDetails object containing information
	 * about the selected users in a List<Long>.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static SelectedUsersDetails getSelectedUsersDetails(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getSelectedUsersDetails()");
		try {
			// Allocate a SelectedUsersDetails we can return.
			SelectedUsersDetails reply = new SelectedUsersDetails();

			// Are there any user ID's to check?
			int totalCount = ((null == userIds) ? 0 : userIds.size());
			if (0 < totalCount) {
				// Yes!  Store the total count and scan those we can
				// resolve.
				reply.setTotalCount(totalCount);
				List resolvedList = ResolveIds.getPrincipals(userIds, false);
				if (MiscUtil.hasItems(resolvedList)) {
					for (Object userO: resolvedList) {
						// Does this user have a workspace?
						User user = ((User) userO);
						if (null != user.getWorkspaceId()) {
							// Yes!  Process whether we can trash this
							// user's workspace or not.
							if (TrashHelper.canTrashUserWorkspace(bs, user)) {
								reply.setHasAdHocUserWorkspaces(true);
							}
							else {
								reply.setHasAdHocUserWorkspacesPurgeOnly(true);
								reply.addPurgeConfirmation(
									NLT.get(
										"purgeConfirmation.user",
										new String[] {
											user.getTitle()
										}));
							}
							
							// Increment the count of user workspaces.
							reply.incrUserWorkspaceCount();
						}
					}
				}
			}

			// If we get here, reply refers to a SelectedUsersDetails
			// containing the information about what's in the
			// List<Long>.  Return it.
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				dumpSelectedUsersDetails(reply);
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
					"GwtViewHelper.getSelectedUsersDetails( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Returns a SelectionDetails object containing information about
	 * the selections in a List<EntityId>.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static SelectionDetails getSelectionDetails(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getSelectionDetails()");
		try {
			// Allocate a SelectionDetails we can return.
			SelectionDetails reply = new SelectionDetails();

			// Are there any EntityId's to check?
			int totalCount = ((null == entityIds) ? 0 : entityIds.size());
			if (0 < totalCount) {
				// Yes!  Store the total count and scan them.
				reply.setTotalCount(totalCount);
				FolderModule    fm = bs.getFolderModule();
				WorkspaceModule wm = bs.getWorkspaceModule();
				for (EntityId eid:  entityIds) {
					try {
						// Is this entry?
						if (eid.isEntry()) {
							// Yes!  Can we access it?
							FolderEntry fe = fm.getEntry(eid.getBinderId(), eid.getEntityId());
							if (null != fe) {
								// Yes!  Is it in personal storage?
								Folder pf = fe.getParentFolder();
								if (pf.isMirrored() || pf.isAclExternallyControlled()) {
									// No!  Set the reply's has remote
									// entries flag...
									reply.setHasRemoteEntries(true);
									
									// ...and add a purge confirmation
									// ...about it.
									String key;
									if (Utils.checkIfFilr())
									     key = "purgeConfirmation.entry.filr";
									else key = "purgeConfirmation.entry.vibe";
									reply.addPurgeConfirmation(
										NLT.get(
											key,
											new String[] {
												fe.getTitle()
											}));
								}
								else {
									reply.setHasAdHocEntries(true);
								}
								
								// Increment the count of entries.
								reply.incrEntryCount();
							}
						}
						
						// No, it isn't an entry!  Is it a folder?
						else if (eid.isFolder()) {
							// Yes!  Can we access it?
							Folder f = fm.getFolder(eid.getEntityId());
							if (null != f) {
								// Yes!  Process whether it's from
								// personal storage or not.
								String purgeConfirmationKey;
								if (f.isAclExternallyControlled()) {
									purgeConfirmationKey = "purgeConfirmation.folder";
									if (CloudFolderHelper.isCloudFolder(f))
									     reply.setHasCloudFolders(true);
									else reply.setHasNetFolders(  true);
								}
								else if (f.isMirrored()) {
									purgeConfirmationKey = "purgeConfirmation.folder";
									reply.setHasMirroredFolders(true);
								}
								else if (SearchUtils.binderHasNestedRemoteFolders(bs, eid.getEntityId())) {
									purgeConfirmationKey = "purgeConfirmation.folder.nested";
									reply.setHasAdHocFoldersWithNestedRemote(true);
								}
								else {
									purgeConfirmationKey = null;
									reply.setHasAdHocFolders(true);
								}
								if (null != purgeConfirmationKey) {
									reply.addPurgeConfirmation(
										NLT.get(
											purgeConfirmationKey,
											new String[] {
												f.getTitle()
											}));
								}
								
								// Increment the count of folders.
								reply.incrFolderCount();
							}
						}
						
						// No, it isn't a folder either!  Is it a
						// workspace?
						else if (eid.isWorkspace()) {
							// Yes!  Can we access it.?
							Workspace ws = wm.getWorkspace(eid.getEntityId());
							if (null != ws) {
								// Yes!  Process whether it's from
								// personal storage or not.
								if (SearchUtils.binderHasNestedRemoteFolders(bs, eid.getEntityId())) {
									reply.setHasAdHocWorkspacesWithNestedRemote(true);
									reply.addPurgeConfirmation(
										NLT.get(
											"purgeConfirmation.workspace.nested",
											new String[] {
												ws.getTitle()
											}));
								}
								else {
									reply.setHasAdHocWorkspaces(true);
								}
								
								// Increment the count of workspaces.
								reply.incrWorkspaceCount();
							}
						}
					}
					
					catch (Exception e) {
						// Log the exception, but otherwise ignore.
						GwtLogHelper.debug(m_logger, "GwtViewHelper.getSelectionDetails( PER ENTITY EXCEPTION ):  ", e);
					}
				}
			}

			// If we get here, reply refers to a SelectionDetails
			// containing the information about what's in the
			// List<EntityId>.  Return it.
			if (GwtLogHelper.isDebugEnabled(m_logger)) {
				dumpSelectionDetails(reply);
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
					"GwtViewHelper.getSelectionDetails( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/*
	 * Returns a List<GwtSharedMeItem> of the items shared by the
	 * current user.
	 */
	private static List<GwtSharedMeItem> getSharedByMeItems(AllModulesInjected bs, HttpServletRequest request, String sortBy, boolean sortDescend) throws Exception {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getSharedByMeItems()");
		try {
			// Construct a list containing just this user's ID...
			Long		userId = GwtServerHelper.getCurrentUserId();
			List<Long>	users  = new ArrayList<Long>();
			users.add(userId);
	
			// ...get the List<ShareItem> of those things shared by the
			// ...user...
			ShareItemSelectSpec	spec = new ShareItemSelectSpec();
			spec.setLatest(true);
			spec.setSharerIds(users);
			List<ShareItem> shareItems = getCleanShareList(bs.getSharingModule(), spec, false);
	
			// ...and finally, convert the List<ShareItem> into a
			// ...List<GwtShareMeItem> and return that.
			List<GwtSharedMeItem> siList = convertItemListToByMeList(bs, request, shareItems, userId, sortBy, sortDescend);
			return siList;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns a List<GwtSharedMeItem> of the items shared with the
	 * current user.
	 */
	private static List<GwtSharedMeItem> getSharedWithMeItems(AllModulesInjected bs, HttpServletRequest request, String sortBy, boolean sortDescend, boolean isPublic) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getSharedWithMeItems()");
		try {
			// Construct a list containing just this user's ID...
			Long		userId = GwtServerHelper.getCurrentUserId();
			List<Long>	users  = new ArrayList<Long>();
			users.add(userId);
	
			// ...get ID's of the groups and teams the user is a member
			// ...of...
			List<Long>	groups = GwtServerHelper.getGroupIds(request, bs, userId, true );
			
			// We don't need to get team ids because team membership is now stored in a "team group"
			List<Long> teams = new ArrayList<Long>();
			//~JW:  List<Long>	teams  = GwtServerHelper.getTeamIds(request, bs, userId);
			
			// ...get the List<ShareItem> of those things shared with
			// ...the user...
			ShareItemSelectSpec	spec = new ShareItemSelectSpec();
			spec.setLatest(true);
			spec.setRecipients(users, groups, teams);
			List<ShareItem> shareItems = getCleanShareList(bs.getSharingModule(), spec, true);
	
			// ...and finally, convert the List<ShareItem> into a
			// ...List<GwtShareMeItem> and return that.
			List<GwtSharedMeItem> siList = convertItemListToWithMeList(bs, request, shareItems, userId, teams, groups, sortBy, sortDescend, isPublic);
			return siList;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns a non-null List<ShareAccessInfo> build from a share
	 * list.
	 */
	private static List<ShareAccessInfo> getShareAccessListFromShares(List<GwtPerShareInfo> perShareInfos) {
		// Allocate a List<ShareAccessInfo> to return.
		List<ShareAccessInfo> reply = new ArrayList<ShareAccessInfo>();

		// Scan the shares...
		for (GwtPerShareInfo psi:  perShareInfos) {
			// ...creating a ShareAccessInfo for each share.
			String access;
			ShareRights	rights = psi.getRights();
			if (null == rights) {
				access = "";
			}
			else {
				switch (rights.getAccessRights()) {
				default:
				case NONE:      access = "";                                        break;
				case CONTRIBUTOR:  access = NLT.get("collections.access.contributor"); break;
				case EDITOR:       access = NLT.get("collections.access.editor");      break;
				case VIEWER:       access = NLT.get("collections.access.viewer");      break;
				}
			}
			if (rights.getCanShareForward()) {
				if (0 < access.length()) {
					access += " + ";
				}
				access += NLT.get("collections.access.sharer");
			}
			reply.add(new ShareAccessInfo(access));
		}

		// If we get here, reply refers to the List<ShareAccessInfo>
		// corresponding to the share list.  Return it.
		return reply;
	}
	
	/*
	 * Returns a non-null List<ShareDateInfo> build from a share
	 * list.
	 */
	private static List<ShareDateInfo> getShareDateListFromShares(List<GwtPerShareInfo> perShareInfos) {
		// Allocate a List<ShareDateInfo> to return.
		List<ShareDateInfo> reply = new ArrayList<ShareDateInfo>();

		// Scan the shares...
		for (GwtPerShareInfo psi:  perShareInfos) {
			// ...creating an ShareDateInfo for each share.
			Date	shareDate  = psi.getShareDate();
			String	dateString = ((null == shareDate) ? "" : GwtServerHelper.getDateTimeString(shareDate, DateFormat.MEDIUM, DateFormat.SHORT));
			reply.add(new ShareDateInfo(shareDate, dateString));
		}

		// If we get here, reply refers to the List<ShareDateInfo>
		// corresponding to the share list.  Return it.
		return reply;
	}
	
	/*
	 * Returns a non-null List<ShareExpirationInfo> build from a share
	 * list.
	 */
	private static List<ShareExpirationInfo> getShareExpirationListFromShares(List<GwtPerShareInfo> perShareInfos) {
		// Allocate a List<ShareExpirationInfo> to return.
		List<ShareExpirationInfo> reply = new ArrayList<ShareExpirationInfo>();

		// Scan the shares...
		for (GwtPerShareInfo psi:  perShareInfos) {
			// ...creating an ShareExpirationInfo for each share.
			boolean	isExpired        = psi.isRightsExpired();
			Date	expirationDate   = psi.getRightsExpire();
			String	expirationString = ((null == expirationDate) ? "" : GwtServerHelper.getDateTimeString(expirationDate, DateFormat.MEDIUM, DateFormat.SHORT));
			ShareExpirationInfo	sei = new ShareExpirationInfo(expirationDate, isExpired, expirationString);
			if (isExpired) {
				sei.setAddedStyle("vibe-dataTableShareStringValue-expired");
			}
			reply.add(sei);
		}

		// If we get here, reply refers to the List<ShareExpirationInfo>
		// corresponding to the share list.  Return it.
		return reply;
	}
	
	/**
	 * Returns a SharedViewStateRpcResponseData object
	 * containing the information for shared views.
	 * 
	 * @param bs
	 * @param request
	 * @param ct
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static SharedViewStateRpcResponseData getSharedViewState(AllModulesInjected bs, HttpServletRequest request, CollectionType ct) throws GwtTeamingException {
		try {
			// For guest...
			Boolean showHidden;
			Boolean showNonHidden;
			if (GwtServerHelper.getCurrentUser().isShared()) {
				// ...we show everything since hiding shares is not
				// ...supported...
				showHidden    =
				showNonHidden = Boolean.TRUE;
			}
			
			else {
				// ...otherwise, we look in the session cache as to
				// ...whether to show hidden or non-hidden shares.
				HttpSession hSession = GwtServerHelper.getCurrentHttpSession();
				showHidden    = ((Boolean) hSession.getAttribute(CACHED_SHARED_VIEW_SHOW_HIDDEN_BASE     + ct.name()));
				showNonHidden = ((Boolean) hSession.getAttribute(CACHED_SHARED_VIEW_SHOW_NON_HIDDEN_BASE + ct.name()));
			}

			// Construct the SharedViewStateRpcResponseData
			// object to return.
			SharedViewStateRpcResponseData reply =
				new SharedViewStateRpcResponseData(
					((null == showNonHidden) || showNonHidden),	// Default is true.
					((null != showHidden)    && showHidden));	// Default is false.

			// If we get here, reply refers to the
			// SharedViewStateRpcResponseData object
			// containing the information about shared views.  Return
			// it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getSharedViewState( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Returns a non-null List<ShareMessageInfo> build from a share
	 * list.
	 */
	private static List<ShareMessageInfo> getShareMessageListFromShares(List<GwtPerShareInfo> perShareInfos) {
		// Allocate a List<ShareMessageInfo> to return.
		List<ShareMessageInfo> reply = new ArrayList<ShareMessageInfo>();

		// Scan the shares...
		for (GwtPerShareInfo psi:  perShareInfos) {
			// ...creating an ShareMessageInfo for each share.
			reply.add(new ShareMessageInfo(psi.getComment()));
		}

		// If we get here, reply refers to the List<ShareMessageInfo>
		// corresponding to the share list.  Return it.
		return reply;
	}

	/*
	 * Returns a Map of the search results for users based on the
	 * criteria in the options Map.
	 */
	@SuppressWarnings("unchecked")
	private static Map getUserEntries(AllModulesInjected bs, HttpServletRequest request, Binder binder, String quickFilter, Map options) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getUserEntries()");
		try {
			return bs.getProfileModule().getUsers(options);
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Returns a UserPropertiesRpcResponseData containing information
	 * managing a user.
	 * 
	 * @param bs
	 * @param request
	 * @param userId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static UserPropertiesRpcResponseData getUserProperties(final AllModulesInjected bs, final HttpServletRequest request, final Long userId, boolean includeLastLogin) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getUserProperties()");
		try {
			SimpleProfiler.start("GwtViewHelper.getUserProperties(Get profile information)");
			final UserPropertiesRpcResponseData reply;
			try {
				// Construct a user properties response containing the
				// user's profile that we can return...
				reply = new UserPropertiesRpcResponseData(
					getProfileEntryInfo(
						bs,
						request,
						userId));
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get profile information)");
			}

			SimpleProfiler.start("GwtViewHelper.getUserProperties(Get basic account information)");
			final User   user;
			IdentityInfo userII;
			AccountInfo  ai;
			try {
				// ...add information about what the user's account...
				user   = ((User) bs.getProfileModule().getEntry(userId));
				userII = user.getIdentityInfo();
				ai     = new AccountInfo();
				reply.setAccountInfo(ai);
				ai.setLoginId(user.getName());
				ai.setFromOpenId(userII.isFromOpenid());
				ai.setPrincipalType(getPrincipalType(user));
				ai.setAdmin(bs.getAdminModule().testUserAccess(user, AdminOperation.manageFunction));
				ai.setUserHasLoggedIn(null != user.getFirstLoginDate());
				ai.setTermsAndConditionsAcceptDate(user.getTermsAndConditionsAcceptDate());
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.getUserProperties(Gather basic account information)");
			}

			// ...if the user's from LDAP...
			if (userII.isFromLdap()) {
				// ...add their LDAP DN and eDirectory container, if
				// ...available...
				SimpleProfiler.start("GwtViewHelper.getUserProperties(Get LDAP information)");
				try {
					String ldapDN = user.getForeignName();
					ai.setLdapDN(                            ldapDN );
					ai.setLdapContainer(getParentContainerDN(ldapDN));
				}
				finally {
					SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get LDAP information)");
				}
			}

			// ...if we need to include the last login information...
			ai.setShowLastLogin(includeLastLogin && SHOW_LAST_LOGIN_IN_USER_PROPERTIES);
			if (ai.isShowLastLogin() && ai.isUserHasLoggedIn()) {
				SimpleProfiler.start("GwtViewHelper.getUserProperties(Get last login)");
				try {
					// ...and if we can determine the last time the
					// ...user logged in...
					Date lastLogin = getLastUserLogin(bs, user);
					if (null != lastLogin) {
						// ...add that to the reply.
						ai.setLastLogin(GwtServerHelper.getDateTimeString(lastLogin));
					}
				}
				finally {
					SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get last login)");
				}
			}

			SimpleProfiler.start("GwtViewHelper.getUserProperties(Get adHoc setting)");
			try {
				// ...add whether the user has adHoc folder access...
				boolean hasAdHocFolders = ((!user.isShared()) && user.getIdentityInfo().isInternal() && user.isPerson());
				boolean perUserAdHoc    = false;
				if (hasAdHocFolders) {
					hasAdHocFolders = (!(GwtServerHelper.useHomeAsMyFiles(bs, user)));
					perUserAdHoc    = (null != user.isAdHocFoldersEnabled());
				}
				ai.setHasAdHocFolders(hasAdHocFolders);
				ai.setPerUserAdHoc(   perUserAdHoc   );
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get adHoc setting)");
			}

			SimpleProfiler.start("GwtViewHelper.getUserProperties(Get download setting)");
			try {
				// ...add whether the user can download files...
				boolean canDownload     = user.isPerson();
				boolean perUserDownload = false;
				if (canDownload) {
					canDownload     = AdminHelper.getEffectiveDownloadSetting(bs, user);
					perUserDownload = (null != user.isDownloadEnabled());
				}
				ai.setCanDownload(    canDownload    );
				ai.setPerUserDownload(perUserDownload);
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get download setting)");
			}

			SimpleProfiler.start("GwtViewHelper.getUserProperties(Get web access setting)");
			try {
				// ...add whether the user can use web access...
				boolean hasWebAccess = user.isPerson();
				boolean perUserWebAccess = false;
				if (hasWebAccess) {
					hasWebAccess     = AdminHelper.getEffectiveWebAccessSetting(bs, user);
					perUserWebAccess = (null != user.isWebAccessEnabled());
				}
				ai.setHasWebAccess(    hasWebAccess    );
				ai.setPerUserWebAccess(perUserWebAccess);
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get web access setting)");
			}

			SimpleProfiler.start("GwtViewHelper.getUserProperties(Get workspace sharing information)");
			try {
				// ...add the user's current workspace sharing rights...
				List<Long> userIds = new ArrayList<Long>();
				userIds.add(userId);
				UserSharingRightsInfoRpcResponseData sharingRights = GwtServerHelper.getUserSharingRightsInfo(bs, request, userIds);
				reply.setSharingRights(sharingRights.getUserRights(userId));
			}
			finally {
				SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get workspace sharing information)");
			}

			// ...if quotas are enabled...
			final AdminModule am = bs.getAdminModule();
			if (am.isQuotaEnabled()) {
				SimpleProfiler.start("GwtViewHelper.getUserProperties(Get quota information)");
				try {
					QuotaInfo qi = new QuotaInfo();
					if (am.testAccess(AdminOperation.manageFunction)) {
						// ...add the manage quotas URL if the user has
						// ...rights to manage them...
						AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", false);
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_MANAGE_QUOTAS);
						qi.setManageQuotasUrl(url.toString());
					}
					
					long userQuota = user.getDiskQuota();
					if (0 == userQuota) {
						userQuota = user.getMaxGroupsQuota();
						if (0 == userQuota) {
							ZoneConfig zc = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
							userQuota = zc.getDiskQuotaUserDefault();
							if (0 < userQuota) {
								qi.setZoneQuota(true);	// true -> The user quota came from the default zone assignment.
							}
						}
						else {
							qi.setGroupQuota(true);	// true -> The user quota came from a group assignment.
						}
					}
					
					// ...add information about their quota...
					qi.setUserQuota(userQuota);
					reply.setQuotaInfo(qi);
				}
				finally {
					SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get quota information)");
				}
			}

			// Are we exposing Filr features?
			if (LicenseChecker.showFilrFeatures()) {
				// Yes!  Then we need to include net and home folder
				// information.
				SimpleProfiler.start("GwtViewHelper.getUserProperties(Get manage net folder information)");
				final NetFoldersInfo nfi = new NetFoldersInfo();
				try {
					// ...add a NetFolderInfo...
					reply.setNetFoldersInfo(nfi);
					if (am.testAccess(AdminOperation.manageFunction)) {
						// ...including whether the user can manage net
						// ...folders...
						try {
							Binder netFoldersParentBinder = SearchUtils.getNetFoldersRootBinder();
							nfi.setCanManageNetFolders(
								(null != netFoldersParentBinder) &&
								LicenseChecker.isAuthorizedByLicense("com.novell.teaming.module.folder.MirroredFolder") &&
								bs.getBinderModule().testAccess(netFoldersParentBinder, BinderOperation.modifyBinder));
						}
						catch (Exception ex) {
							// Ignore.  If we can't access it, the user
							// can't manage net folders.
						}
					}
				}
				finally {
					SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get manage net folder information)");
				}

				// ...if the user has logged in...
				if (ai.isUserHasLoggedIn()) {
					// ...some of the information required has to be
					// ...done as the target user...
					RunasTemplate.runas(
						new RunasCallback() {
							@Override
							public Object doAs() {
								SimpleProfiler.start("GwtViewHelper.getUserProperties(Get home folder information)");
								try {
									// ...if the user has a home
									// ...folder...
									Long homeId = GwtServerHelper.getHomeFolderId(bs, user);
									if (null != homeId) {
										String			rootPath = null;
										Folder			home     = bs.getFolderModule().getFolderWithoutAccessCheck(homeId);
										ResourceDriver	rd       = home.getResourceDriver();
										if (null != rd) {
											String dc = rd.getClass().getName();
											if (MiscUtil.hasString(dc) && dc.equals("com.novell.teaming.fi.connection.file.FileResourceDriver")) {
												rootPath = rd.getRootPath();
											}
											else {
												ResourceDriverConfig rdConfig = rd.getConfig();
												if (null != rdConfig) {
													rootPath = rdConfig.getRootPath();
												}
											}
										}
			
										// ...add information about the
										// ...home folder...
										HomeInfo hi = new HomeInfo();
										hi.setId(          homeId                );
										hi.setRelativePath(home.getResourcePath());
										hi.setRootPath(    rootPath              );
										reply.setHomeInfo(hi);
									}
								}
								finally {
									SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get home folder information)");
								}
		
								SimpleProfiler.start("GwtViewHelper.getUserProperties(Get per net folder information)");
								try {
									Map	nfSearch;
									try {
										// Can we search for this
										// user's Net Folders?
										nfSearch = getCollectionEntries(
											bs,
											request,
											null,
											null,
											new
											HashMap(),
											CollectionType.NET_FOLDERS,
											null);
									}
									catch (Exception ex) {
										// No!  Log the exception...
										GwtLogHelper.error(m_logger, "GwtViewHelper.getUserProperties( Error querying Net Folders - SOURCE EXCEPTION ):  ", ex);
										
										// ...and return why we
										// ...coulnd't.
										nfSearch = null;
										String nfAccessError = null;
										if (ex instanceof FIException) {
											String ec = ((FIException) ex).getErrorCode();
											if ((null != ec) && ec.equals(FIException.CREDENTIAL_UNAVAILABLE_FOR_USER)) {
												nfAccessError = NLT.get("userProperties.error.credentialUnavailble");
											}
										}
										if (null == nfAccessError) {
											nfAccessError = NLT.get("userProperties.error.other", new String[]{MiscUtil.exToString(ex)});
										}
										nfi.setNetFolderAccessError(nfAccessError);
									}
									if (null != nfSearch ) {
										// Yes, we have results from
										// the Net Folder search!
										List<Map>	nfList = ((List<Map>) nfSearch.get(ObjectKeys.SEARCH_ENTRIES));
										if (MiscUtil.hasItems(nfList)) {
											Long nfBinderId = SearchUtils.getNetFoldersRootBinder().getId();
											for (Map nfMap:  nfList) {
												// ...adding an EntryTitleInfo
												// ...for each to the reply.
												Long   docId = Long.parseLong(GwtServerHelper.getStringFromEntryMap(nfMap, Constants.DOCID_FIELD));
												String title = GwtServerHelper.getStringFromEntryMap(nfMap, Constants.TITLE_FIELD);
												SimpleProfiler.start("GwtViewHelper.getUserProperties(Get per net folder " + title + " information)");
												try {
													EntryTitleInfo	eti = new EntryTitleInfo();
													eti.setSeen(true);
													eti.setTitle(MiscUtil.hasString(title) ? title : ("--" + NLT.get("entry.noTitle") + "--"));
													eti.setEntityId(new EntityId(nfBinderId, docId, EntityId.FOLDER));
													String description = getEntryDescriptionFromMap(request, nfMap);
													if (MiscUtil.hasString(description)) {
														eti.setDescription(description);
														String descriptionFormat = GwtServerHelper.getStringFromEntryMap(nfMap, Constants.DESC_FORMAT_FIELD);
														eti.setDescriptionIsHtml(MiscUtil.hasString(descriptionFormat) && descriptionFormat.equals(String.valueOf(Description.FORMAT_HTML)));
													}
													else {
														description = GwtServerHelper.getStringFromEntryMap(nfMap, Constants.ENTITY_PATH);
														if (MiscUtil.hasString(description)) {
															eti.setDescription(      description);
															eti.setDescriptionIsHtml(false      );
														}
													}
													nfi.addNetFolder(eti);
												}
												finally {
													SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get per net folder " + title + " information)");
												}
											}
										}
									}
								}
								finally {
									SimpleProfiler.stop("GwtViewHelper.getUserProperties(Get per net folder information)");
								}
								return null;	// Not used.  Doesn't matter what we return.
							}
						},
						WebHelper.getRequiredZoneName(request),
						userId);
				}
			}
			
			// If we get here, reply refers to a
			// UserPropertiesRpcResponseData containing the properties
			// for managing the user.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getUserProperties( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}

	/**
	 * Given a User, returns the type of that user.
	 * 
	 * @param principal
	 * 
	 * @return
	 */
	public static PrincipalType getUserType(User user) {
		return getPrincipalType(user);
	}
	
	public static PrincipalType getPrincipalType(Principal principal) {
		// Is the Principal a User?
		PrincipalType reply = PrincipalType.UNKNOWN;
		IdentityInfo ui = principal.getIdentityInfo();
		boolean isUser = (principal instanceof User);
		if (isUser) {
			// Yes!  Are they an internal user?
			User user = ((User) principal);
			if (ui.isInternal()) {
				// Yes!  Are they from LDAP?
				if (ui.isFromLdap()) {
					// Yes!
					reply = PrincipalType.INTERNAL_LDAP;
				}
				else {
					// No, they're not from LDAP!  Is it a person?
					if (isUser && user.isPerson()) {
						// Yes!
						if (principal.isReserved() && principal.getInternalId().equalsIgnoreCase(ObjectKeys.SUPER_USER_INTERNALID))
						     reply = PrincipalType.INTERNAL_PERSON_ADMIN;
						else reply = PrincipalType.INTERNAL_PERSON_OTHERS;
					}
					else {
						// No, it's not a person!
						reply = (isUser ? PrincipalType.INTERNAL_SYSTEM : PrincipalType.INTERNAL_PERSON_OTHERS);
					}
				}
			}
			else {
				// No, it's not an internal user!  Is it the Guest user?
				if      (user.isShared())                            reply = PrincipalType.EXTERNAL_GUEST;
				else if (ui.isFromOpenid() && (!(ui.isFromLocal()))) reply = PrincipalType.EXTERNAL_OPEN_ID;
				else if (ui.isFromLdap())                            reply = PrincipalType.EXTERNAL_LDAP;
				else                                                 reply = PrincipalType.EXTERNAL_OTHERS;
			}
		}
		
		else {
			// No, it's not a User!  It must be a Group!  Is it from
			// LDAP?
			if (ui.isFromLdap()) {
				// Yes!
				if (ui.isInternal())
				     reply = PrincipalType.INTERNAL_LDAP_GROUP;
				else reply = PrincipalType.EXTERNAL_LDAP_GROUP;
			}
			else {
				// No, it's not from LDAP!  Is it a system or local
				// group.
				if (principal.isReserved()) {
				     reply = PrincipalType.SYSTEM_GROUP;
				}
				else {
					if (ui.isInternal())
					     reply = PrincipalType.INTERNAL_LOCAL_GROUP;
					else reply = PrincipalType.EXTERNAL_LOCAL_GROUP;
				}
			}
		}

		// If we get here, reply refers to the PrincipalType of the
		// Principal.  Return it.
		return reply;
	}
	
	/*
	 * Given an EntityId that describes a Principal (user or group),
	 * returns the type of Principal.
	 */
	private static PrincipalType getPrincipalType(AllModulesInjected bs, HttpServletRequest request, EntityId entityId) {
		PrincipalType reply = PrincipalType.UNKNOWN;
		
		try {
			// Can we access the User?
			Principal p = bs.getProfileModule().getEntry(entityId.getEntityId());
			if (null != p) {
				// Yes!  What type are they?
				reply = getPrincipalType(p);
			}
		}
		
		catch (Exception ex) {
			// Log the exception.
			GwtLogHelper.debug(m_logger, "GwtViewHelper.getPrincipalType( SOURCE EXCEPTION ):  ", ex);
		}
		
		// If we get here, reply refers to the PrincipalType of the
		// EntityId.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if the user is viewing pinned entries on a given
	 * folder and false otherwise.
	 * 
	 * @param request
	 * @param folderId
	 * 
	 * @return
	 */
	public static boolean getUserViewPinnedEntries(HttpServletRequest request, Long folderId) {
		HttpSession session = WebHelper.getRequiredSession(request);
		Boolean viewPinnedEntries = ((Boolean) session.getAttribute(CACHED_VIEW_PINNED_ENTRIES_BASE + folderId));
		return ((null != viewPinnedEntries) && viewPinnedEntries);
	}
	
	/**
	 * Returns true if the user is viewing shared files (vs. all
	 * entries) on a given collection and false otherwise.
	 * 
	 * @param request
	 * @param collectionType
	 * 
	 * @return
	 */
	public static boolean getUserViewSharedFiles(HttpServletRequest request, CollectionType collectionType) {
		HttpSession session = WebHelper.getRequiredSession(request);
		Boolean viewSharedFiles = ((Boolean) session.getAttribute(CACHED_VIEW_SHARED_FILES_BASE + collectionType.ordinal()));
		boolean reply;
		if (null == viewSharedFiles)
		     reply = (!LicenseChecker.showVibeFeatures());
		else reply = viewSharedFiles;
		return reply;
	}

	/**
	 * Given a List<Long> of userIds, returns the corresponding
	 * List<UserWorkspacePair> of the User/Workspace pairs for the IDs.
	 * 
	 * @param principalIds
	 * @param principals
	 * @param resolveWorkspaces
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<UserWorkspacePair> getUserWorkspacePairs(Collection <Long> principalIds, List<Principal> principals, boolean resolveWorkspaces) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtViewHelper.getUserWorkspacePairs()");
		try {
			SimpleProfiler.start("GwtViewHelper.getUserWorkspacePairs(Resolve users)");
			List<User> users = new ArrayList<User>();
			List<Long> wsIds = new ArrayList<Long>();
			try {
				// If we weren't given one...
				if (null == principals) {
					// ...can we resolve the List<Long> to a list of
					// ...Principal objects?
					try                  {principals = ResolveIds.getPrincipals(principalIds, false);}
					catch (Exception ex) {principals = null;                                         }
				}
				if (MiscUtil.hasItems(principals)) {
					// Yes!  Scan them.
					for (Object o:  principals) {
						// Is this Principal a User?
						Principal p = ((Principal) o);
						boolean isUser = (p instanceof UserPrincipal);
						if (isUser) {
							// Yes!  Add it to the List<User>.
							User user = ((User) p);
							users.add(user);
							
							// Does this User have a Workspace ID?
							Long wsId = user.getWorkspaceId();
							if (null != wsId) {
								// Yes!  Added it to the List<Long> of
								// Workspace IDs.
								wsIds.add(wsId);
							}
						}
					}
				}
			}
			
			finally {
				SimpleProfiler.stop("GwtViewHelper.getUserWorkspacePairs(Resolve users)");
			}
	
			Map<Long, Workspace> wsMap = new HashMap<Long, Workspace>();
			if (resolveWorkspaces) {
				SimpleProfiler.start("GwtViewHelper.getUserWorkspacePairs(Resolve workspaces)");
				try {
					// Do we have any Workspace IDs?
					if (MiscUtil.hasItems(wsIds)) {
						// Yes!  Can we resolve them to a Set of Workspace
						// objects?
						Set userWSSet = ResolveIds.getBinders(wsIds);
						if (MiscUtil.hasItems(userWSSet)) {
							// Yes!  Scan them...
							for (Object o:  userWSSet) {
								// ...adding each to the Workspace map.
								Workspace ws = ((Workspace) o);
								wsMap.put(ws.getId(), ws);
							}
						}
					}
				}
				
				finally {
					SimpleProfiler.stop("GwtViewHelper.getUserWorkspacePairs(Resolve workspaces)");
				}
			}
	
			List<UserWorkspacePair> reply = new ArrayList<UserWorkspacePair>();
			SimpleProfiler.start("GwtViewHelper.getUserWorkspacePairs(Collate results)");
			try {
				// Scan the List<User> we collected...
				for (User user:  users) {
					// ...adding a UserWorkspacePair object for each to
					// ...the reply list.
					Workspace ws;
					Long wsId = user.getWorkspaceId();
					UserWorkspacePair uwsPair;
					if (resolveWorkspaces) {
						if (null != wsId)
						     ws = wsMap.get(wsId);
						else ws = null;
						uwsPair = new UserWorkspacePair(user, ws);
					}
					else {
						uwsPair = new UserWorkspacePair(user, wsId);
					}
					reply.add(uwsPair);
				}
			}
			
			finally {
				SimpleProfiler.stop("GwtViewHelper.getUserWorkspacePairs(Collate results)");
			}
			
			// If we get here, reply refers to a
			// List<UserWorkspacePair> corresponding to the List<Long>
			// of user IDs we were given.  Return it.
			return reply;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Returns a ViewInfo used to control folder views based on a URL.
	 * 
	 * @param bs
	 * @param request
	 * @param url
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	@SuppressWarnings("unchecked")
	public static ViewInfo getViewInfo(AllModulesInjected bs, HttpServletRequest request, String url) throws GwtTeamingException {
		// Trace the URL we're working with.
		GwtLogHelper.debug(m_logger, "GwtViewHelper.getViewInfo():  " + url);

		// Can we parse the URL?
		Map<String, String> nvMap = getQueryParameters(url);
		if (!(MiscUtil.hasItems(nvMap))) {
			// No!  Then we can't get a ViewInfo.
			GwtLogHelper.debug(m_logger, "GwtViewHelper.getViewInfo():  1:Could not determine a view.");
			return null;
		}

		// Construct a ViewInfo we can setup with the information for
		// viewing this URL.
		ViewInfo vi = new ViewInfo();
		vi.setBaseBinderId(getQueryParameterLong(nvMap, WebKeys.URL_BINDER_ID));

		// Is there an operation on the URL?
		String op = getQueryParameterString(nvMap, WebKeys.URL_OPERATION);
		if (null == op) op = "";
		
		// What action is the URL requesting?
		String action = getQueryParameterString(nvMap, WebKeys.URL_ACTION).toLowerCase();		
		if (action.equals(WebKeys.ACTION_VIEW_PERMALINK)) {
			// A view on a permalink!  What type of entity is being
			// viewed?
			String entityType = getQueryParameterString(nvMap, WebKeys.URL_ENTITY_TYPE).toLowerCase();
			if (entityType.equals(EntityType.user.name().toLowerCase())) {
				// A user!  Can we access the user?
				Long entryId = getQueryParameterLong(nvMap, WebKeys.URL_ENTRY_ID);			
				if (!(initVIFromUser(request, bs, GwtServerHelper.getUserFromId(bs, entryId), vi))) {
					GwtLogHelper.debug(m_logger, "GwtViewHelper.getViewInfo():  2:Could not determine a view.");
					return null;
				}
			}
			else {
				boolean isProfiles = entityType.equals(EntityType.profiles.name().toLowerCase());
				if (entityType.equals(EntityType.folder.name().toLowerCase()) || entityType.equals(EntityType.workspace.name().toLowerCase()) || isProfiles) {
					// A folder, workspace or the profiles binder!  Is
					// this a request to show team members on this binder?
					if ((!isProfiles) && op.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
						// Yes!  Simply mark the ViewInfo as such.
						vi.setViewType(ViewType.ADD_FOLDER_ENTRY);
					}
					
					// Setup a binder view based on the binder ID.
					else if (!(initVIFromBinderId(request, bs, nvMap, WebKeys.URL_BINDER_ID, vi))) {
						GwtLogHelper.debug(m_logger, "GwtViewHelper.getViewInfo():  3:Could not determine a view.");
						return null;
					}
				}
				
				else if (entityType.equals(EntityType.folderEntry.name().toLowerCase())) {
					// A folder entry!  Construct a ViewFolderEntryInfo
					// for it...
					FolderEntry	fe = null;
					Long		entryId = getQueryParameterLong(nvMap, WebKeys.URL_ENTRY_ID);
					if (null == entryId) {
						String entryTitle = getQueryParameterString(nvMap, WebKeys.URL_ENTRY_TITLE);
						if (MiscUtil.hasString(entryTitle)) {
							String zoneUUID = getQueryParameterString(nvMap, WebKeys.URL_ZONE_UUID);
							Set entries = bs.getFolderModule().getFolderEntryByNormalizedTitle(getQueryParameterLong(nvMap, WebKeys.URL_BINDER_ID), entryTitle, zoneUUID);
							if (MiscUtil.hasItems(entries)) {
								fe      = ((FolderEntry) entries.iterator().next());
								entryId = fe.getId();
							}
						}
					}
					else {
						fe = GwtUIHelper.getEntrySafely(bs.getFolderModule(), null, entryId);
					}
					
					boolean				hasAccess = (null != fe);
					Long				binderId  = (hasAccess ? fe.getParentBinder().getId() : getQueryParameterLong(nvMap, WebKeys.URL_BINDER_ID));
					ViewFolderEntryInfo	vfei      = buildViewFolderEntryInfo(bs, request, binderId, entryId);
					
					// ...and mark the ViewInfo as such.
					vi.setViewFolderEntryInfo(vfei                 );
					vi.setViewType(           ViewType.FOLDER_ENTRY);
				}
			}
		}
		
		else if (action.equals(WebKeys.ACTION_VIEW_WS_LISTING)     ||
		         action.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING) ||
		         action.equals(WebKeys.ACTION_VIEW_PROFILE_LISTING)) {
			// A view workspace, folder or profiles listing!  Is this a
			// request to show team members on this binder?
			if (op.equals(WebKeys.OPERATION_SHOW_TEAM_MEMBERS)) {
				// Yes!  Simply mark the ViewInfo as such.
				vi.setViewType(ViewType.ADD_FOLDER_ENTRY);
			}
			
			else {
				// No, it's not a request to show team members!  Are we
				// viewing a folder while changing its view?
				boolean			viewFolderListing = action.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING);
				ProfileModule	pm                = bs.getProfileModule();
				User			user              = GwtServerHelper.getCurrentUser();
				Long			userId            = user.getId();
				if (viewFolderListing && op.equals(WebKeys.OPERATION_SET_DISPLAY_DEFINITION)) {
					// Yes!  Do we have both the view definition and
					// binder ID's?
					String defId    = getQueryParameterString(nvMap, WebKeys.URL_VALUE    );
					Long   binderId = getQueryParameterLong(  nvMap, WebKeys.URL_BINDER_ID);
					if (MiscUtil.hasString(defId) && (null != binderId)) {
						// Yes!  Put the view into affect.
						pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_DISPLAY_DEFINITION, defId);
					}
				}
				
				// Setup a binder view based on the binder ID.
				if (!(initVIFromBinderId(request, bs, nvMap, WebKeys.URL_BINDER_ID, vi))) {
					GwtLogHelper.debug(m_logger, "GwtViewHelper.getViewInfo():  4:Could not determine a view.");
					return null;
				}
	
				// Is this a view folder listing?
				BinderInfo	viBI     = vi.getBinderInfo();
				Long		binderId = viBI.getBinderIdAsLong();
				if (viewFolderListing) {
					// Yes!  Does it also contain changes to the folder
					// sorting?
					String sortBy       = getQueryParameterString(nvMap, WebKeys.FOLDER_SORT_BY);		
					String sortDescendS = getQueryParameterString(nvMap, WebKeys.FOLDER_SORT_DESCEND);
					if (MiscUtil.hasString(sortBy) && MiscUtil.hasString(sortDescendS)) {
						// Yes!  Apply the sort changes.
						Boolean sortDescend = Boolean.parseBoolean(sortDescendS);
						GwtServerHelper.saveFolderSort(
							bs,
							viBI,
							sortBy,
							(!sortDescend));
					}
					
					// If the request contain changes to the task
					// filtering...
					String taskFilterType = getQueryParameterString(nvMap, WebKeys.TASK_FILTER_TYPE);
					if (MiscUtil.hasString(taskFilterType)) {
						// ...put them into effect.
						TaskHelper.setTaskFilterType(bs, userId, binderId, FilterType.valueOf(taskFilterType));
					}
					String folderModeType = getQueryParameterString(nvMap, WebKeys.FOLDER_MODE_TYPE);
					if (MiscUtil.hasString(folderModeType)) {
						// ...put them into effect.
						ListFolderHelper.setFolderModeType(bs, userId, binderId, ModeType.valueOf(folderModeType));
					}
					
					// Is the URL to view a specific entry within the
					// folder?
					String  entryViewStyle = getQueryParameterString(nvMap, WebKeys.URL_ENTRY_VIEW_STYLE);
					String  entryId        = getQueryParameterString(nvMap, WebKeys.URL_ENTRY_ID        );
					if (MiscUtil.hasString(entryViewStyle) && MiscUtil.hasString(entryId)) {
						// Yes!  If we're not ignoring the entry
						// view...
						boolean entryViewIgnore = GwtClientHelper.hasString(getQueryParameterString(nvMap, WebKeys.URL_ENTRY_VIEW_IGNORE));
						if (!entryViewIgnore) {
							// ...adjust the ViewInfo accordingly.  If
							// ...the binder ID we're tracking doesn't
							// ...match that from the entry...
							Long entryIdL = Long.parseLong(entryId);
							FolderEntry fe = bs.getFolderModule().getEntry(null, entryIdL);
							Long feBinderId = fe.getParentBinder().getId();
							if ((null == binderId) || (!(binderId.equals(feBinderId)))) {
								// ...use the one from the entry.
								binderId = feBinderId;
								BinderInfo actualBI = GwtServerHelper.getBinderInfo(bs, request, String.valueOf(binderId));
								if (null != actualBI) {
									vi.setBinderInfo(actualBI);
								}
							}
							vi.setViewType(ViewType.BINDER_WITH_ENTRY_VIEW);
							vi.setEntryViewUrl(
								GwtServerHelper.getViewFolderEntryUrl(
									bs,
									request,
									binderId,
									entryIdL,
									isQueryParamSet(nvMap, WebKeys.URL_INVOKE_SHARE,     "1"),
									isQueryParamSet(nvMap, WebKeys.URL_INVOKE_SUBSCRIBE, "1")));
							vi.setInvokeShare(       false);	// We'll invoke any share     on the entry, not the binder.
							vi.setInvokeShareEnabled(false);	// We'll invoke any share     on the entry, not the binder.
							vi.setInvokeSubscribe(   false);	// We'll invoke any subscribe on the entry, not the binder.
						}

						// Is the folder this entry is located in
						// accessible to the user?
						//
						// Checks:
						// - Is workspace (initVIFromBinderId() will
						//   have overridden if the folder was
						//   inaccessible) or
						// - Can't access the folder.
						viBI = vi.getBinderInfo();
						if (viBI.isBinderWorkspace() || (null == GwtUIHelper.getBinderSafely(bs.getBinderModule(), viBI.getBinderIdAsLong()))) {
							// No!  Set the entry view's underlying
							// context to the user's Shared With Me
							// view.
							CollectionType ct = GwtServerHelper.getDefaultCollectionType(user);
							vi.setBinderInfo(GwtServerHelper.buildCollectionBI(ct, user.getWorkspaceId()));							
							vi.setOverrideUrl(GwtServerHelper.getCollectionPointUrl(request, GwtServerHelper.getUserWorkspace(user), ct));
						}
					}
				}
				
				// Does it contain a filter operation?
				if (op.equals(WebKeys.URL_SELECT_FILTER)) {
					// Yes, a filter selection!  Apply the selection.
					String filterName = MiscUtil.replace(getQueryParameterString(nvMap, WebKeys.URL_SELECT_FILTER), "+", " ");
					String op2 = getQueryParameterString(nvMap, WebKeys.URL_OPERATION2);
					String filterScope;
					if (MiscUtil.hasString(op2) && op2.equals(ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL))
					     filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL;
					else filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL;
					pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER,       filterName );
					pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE, filterScope);
	
					UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUserId(), binderId);
					List<String> currentFilters = ((List<String>) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTERS));
					if (null == currentFilters) {
						currentFilters = new ArrayList<String>();
					}
					if (MiscUtil.hasString(filterName)) {
						String filterSpec = BinderFilter.buildFilterSpec(filterName, filterScope);
						if (!(currentFilters.contains(filterSpec))) {
							currentFilters.add(filterSpec);
						}
					}
					else {
						currentFilters.clear();
					}
					pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTERS, currentFilters);
				}
				
				if (op.equals(WebKeys.URL_CLEAR_FILTER)) {
					// Yes, a filter clear!  Apply the selection.
					String op2 = getQueryParameterString(nvMap, WebKeys.URL_OPERATION2);
					String filterScope;
					if (MiscUtil.hasString(op2) && op2.equals(ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL))
					     filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_GLOBAL;
					else filterScope = ObjectKeys.USER_PROPERTY_USER_FILTER_PERSONAL;
					pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER,       ""         );
					pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTER_SCOPE, filterScope);
	
					String filterName = MiscUtil.replace(getQueryParameterString(nvMap, WebKeys.URL_CLEAR_FILTER), "+", " ");
					String filterSpec = BinderFilter.buildFilterSpec(filterName, filterScope);
					UserProperties userBinderProperties = bs.getProfileModule().getUserProperties(GwtServerHelper.getCurrentUserId(), binderId);
					List<String> currentFilters = ((List<String>) userBinderProperties.getProperty(ObjectKeys.USER_PROPERTY_USER_FILTERS));
					if (null == currentFilters) {
						currentFilters = new ArrayList<String>();
					}
					currentFilters.remove(filterSpec);
					pm.setUserProperty(userId, binderId, ObjectKeys.USER_PROPERTY_USER_FILTERS, currentFilters);
				}
			}
		}

		else if (action.equals(WebKeys.ACTION_ADD_FOLDER_ENTRY)) {
			// An add folder entry!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADD_FOLDER_ENTRY);
		}
		
		else if (action.equals(WebKeys.ACTION_ADD_BINDER)) {
			// An add binder!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADD_BINDER);
		}
		
		else if (action.equals(WebKeys.ACTION_ADD_PROFILE_ENTRY)) {
			// An add profile entry!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADD_PROFILE_ENTRY);
		}
		
		else if (action.equals(WebKeys.ACTION_ADVANCED_SEARCH)) {
			// An advanced search!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADVANCED_SEARCH);
		}
		
		else if (action.equals(WebKeys.ACTION_BUILD_FILTER)) {
			// A build filter!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.BUILD_FILTER);
		}
		
		else if (action.equals(WebKeys.ACTION_MODIFY_BINDER)) {
			// A modify binder!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.MODIFY_BINDER);
		}
		
		else if (action.equals(WebKeys.ACTION_VIEW_PROFILE_ENTRY)) {
			// A view profile entry!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.VIEW_PROFILE_ENTRY);
		}
		
		else if (action.equals(WebKeys.ACTION_VIEW_FOLDER_ENTRY)) {
			// A view folder entry!  Construct a ViewFolderEntryInfo
			// for it...
			FolderEntry	fe                 = null;
			Long		entryId            = getQueryParameterLong(nvMap, WebKeys.URL_ENTRY_ID );
			boolean		createFromWikiLink = false;
			if (null == entryId) {
				String entryTitle = getQueryParameterString(nvMap, WebKeys.URL_ENTRY_TITLE);
				if (MiscUtil.hasString(entryTitle)) {
					String zoneUUID = getQueryParameterString(nvMap, WebKeys.URL_ZONE_UUID);
					Set entries = bs.getFolderModule().getFolderEntryByNormalizedTitle(getQueryParameterLong(nvMap, WebKeys.URL_BINDER_ID), entryTitle, zoneUUID);
					if (MiscUtil.hasItems(entries)) {
						fe      = ((FolderEntry) entries.iterator().next());
						entryId = fe.getId();
					}
					else {
						vi.setViewType(ViewType.CREATE_FROM_WIKI_LINK);
						createFromWikiLink = true;
					}
				}
			}
			else {
				fe = GwtUIHelper.getEntrySafely(bs.getFolderModule(), null, entryId);
			}

			// ...and if it isn't a request to create a WIKI page...
			if (!createFromWikiLink) {
				boolean				hasAccess = (null != fe);
				Long				binderId  = (hasAccess ? fe.getParentBinder().getId() : getQueryParameterLong(nvMap, WebKeys.URL_BINDER_ID));
				ViewFolderEntryInfo	vfei      = buildViewFolderEntryInfo(bs, request, binderId, entryId);
				
				// ...mark the ViewInfo as such...
				vi.setViewFolderEntryInfo(vfei                 );
				vi.setViewType(           ViewType.FOLDER_ENTRY);
				
				// ...if we're supposed to invoke the share dialog on this
				// ...entry...
				boolean invokeShare = isQueryParamSet(nvMap, WebKeys.URL_INVOKE_SHARE, "1");
				if (invokeShare) {
					// ...mark the ViewInfo accordingly...
					boolean invokeShareEnabled = (hasAccess && (GwtShareHelper.isEntitySharable(bs, fe) || GwtShareHelper.isEntityPublicLinkSharable(bs, fe)));
					vi.setInvokeShare(       hasAccess         );
					vi.setInvokeShareEnabled(invokeShareEnabled);
				}
				
				// ...if we're supposed to invoke the subscribe dialog on
				// ...this entry...
				boolean invokeSubscribe = isQueryParamSet(nvMap, WebKeys.URL_INVOKE_SUBSCRIBE, "1");
				if (invokeSubscribe) {
					// ...mark the ViewInfo accordingly.
					vi.setInvokeSubscribe(true);
				}
			}
		}
		
		// If we get here reply refers to the BinderInfo requested or
		// is null.  Return it.
		if (GwtLogHelper.isDebugEnabled(m_logger)) {
			dumpViewInfo(vi);
		}
		return vi;
	}

	/*
	 * Returns the URL to use to view a user's profile entry.
	 */
	private static String getViewProfileEntryUrl(AllModulesInjected bs, HttpServletRequest request, Long userId, Long profileBinderId) {
		AdaptedPortletURL url = new AdaptedPortletURL(request, "ss_forum", true);
		url.setParameter(WebKeys.URL_BINDER_ID,        String.valueOf(profileBinderId)  );
		url.setParameter(WebKeys.URL_ACTION,           WebKeys.ACTION_VIEW_PROFILE_ENTRY);
		url.setParameter(WebKeys.URL_ENTRY_VIEW_STYLE, WebKeys.URL_ENTRY_VIEW_STYLE_FULL);
		url.setParameter(WebKeys.URL_NEW_TAB,          "1"                              );
		url.setParameter(WebKeys.URL_ENTRY_ID,         String.valueOf(userId)           );
		return url.toString();
	}
	
	@SuppressWarnings("unused")
	private static String getViewProfileEntryUrl(AllModulesInjected bs, HttpServletRequest request, Long userId) {
		// Always use the initial form ofthe method.
		return getViewProfileEntryUrl(bs, request, userId, bs.getProfileModule().getProfileBinderId());
	}

	/**
	 * Returns a WhoHasAccessInfoRcpResponseData object containing the
	 * names of the users, groups and teams that have access to an
	 * entity.
	 * 
	 * @param bs
	 * @param request
	 * @param entityId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static WhoHasAccessInfoRpcResponseData getWhoHasAccess(AllModulesInjected bs, HttpServletRequest request, EntityId entityId) throws GwtTeamingException {
		try {
			// Allocate an WhoHasAccessInfoRpcResponseData to track the entry
			// types for the requested binders.
			WhoHasAccessInfoRpcResponseData reply = new WhoHasAccessInfoRpcResponseData();
			
			// Is the entity a binder?
			WorkArea workArea;
			if (entityId.isBinder()) {
				// Yes!  We use it directly as the work are. 
				Binder binder = bs.getBinderModule().getBinderWithoutAccessCheck(entityId.getEntityId());
				reply.setEntityTitle(     binder.getTitle()                                         );
				reply.setEntityIcon(      binder.getIconName(),                BinderIconSize.SMALL );
				reply.setEntityIcon(      binder.getIconName(IconSize.MEDIUM), BinderIconSize.MEDIUM);
				reply.setEntityIcon(      binder.getIconName(IconSize.LARGE ), BinderIconSize.LARGE );
				reply.setEntityHomeFolder(binder.isHomeDir()                                        );
				workArea = binder;
			}
			
			else {
				// No, the entity isn't a binder, it must be a folder
				// entry!  Access the top entry in the chain (in case
				// this is a comment, ...)
				FolderEntry	fe = bs.getFolderModule().getEntry(entityId.getBinderId(), entityId.getEntityId());
				reply.setEntityTitle(fe.getTitle());
				FolderEntry	feTop = fe.getTopEntry();
				if (null != feTop) {
					fe = feTop;
				}

				// Is this file entry we can determine a filename for?
				String fName = GwtServerHelper.getFileEntrysFilename(bs, fe);
				if (MiscUtil.hasString(fName)) {
					// Yes!  Store the appropriate icons for it.
					reply.setEntityIcon(FileIconsHelper.getFileIconFromFileName(fName, mapBISToIS(BinderIconSize.SMALL)),  BinderIconSize.SMALL );
					reply.setEntityIcon(FileIconsHelper.getFileIconFromFileName(fName, mapBISToIS(BinderIconSize.MEDIUM)), BinderIconSize.MEDIUM);
					reply.setEntityIcon(FileIconsHelper.getFileIconFromFileName(fName, mapBISToIS(BinderIconSize.LARGE)),  BinderIconSize.LARGE );
				}
				
				// If the entry has its own ACLs, we use it as the work
				// area, otherwise we use the containing binder.
				if (fe.hasEntryAcl())
				     workArea = fe;
				else workArea = bs.getBinderModule().getBinderWithoutAccessCheck(entityId.getBinderId());
			}

			// Get the access control information for the work area.
			Map model = new HashMap();
			model.put(WebKeys.ACCESS_HONOR_INHERITANCE, Boolean.TRUE);
			AccessControlController.setupAccess(bs, workArea, model);
			
			// Get the read access that's been granted from that access
			// control information.
			Map opsMap  = ((Map) model.get( WebKeys.OPERATION_MAP                   )); if (null == opsMap)  opsMap  = new HashMap();
			Map readMap = ((Map) opsMap.get(WorkAreaOperation.READ_ENTRIES.getName())); if (null == readMap) readMap = new HashMap();
			
			// If there any groups with any kind of access...
			List groups = ((List) model.get(WebKeys.ACCESS_SORTED_GROUPS));
			if (MiscUtil.hasItems(groups)) {
				/// ...and if there are any groups with read access....
				Map groupReads = ((Map) readMap.get(WebKeys.GROUPS));
				if (MiscUtil.hasItems(groupReads)) {
					// ...scan the groups...
					for (Object gO:  groups) {
						// ...and if this group has read access...
						Group group = ((Group) gO);
						if (null != groupReads.get(group.getId())) {
							// ...add an AccessInfo for it to the
							// ...reply.
							reply.addGroup(new AccessInfo(group.getId(), group.getTitle()));
						}
					}
				}
			}
			
			// If there are any users with any kind of access...
			List users = ((List) model.get(WebKeys.ACCESS_SORTED_USERS));
			if (MiscUtil.hasItems(users)) {
				/// ...and if there are any users with read access....
				Map userReads = ((Map) readMap.get(WebKeys.USERS));
				if (MiscUtil.hasItems(userReads)) {
					// ...scan the users.
					BinderModule bm = bs.getBinderModule();
					FolderModule fm = bs.getFolderModule();
					for (Object uO:  users) {
						// Does this user have read access?
						User user = ((User) uO);
						if (null != userReads.get(user.getId())) {
							// Yes!  Is it in a Filr Net Folder?
							boolean addUser = true;
							if (workArea.isAclExternallyControlled()) {
								// Yes!  Does the user have access to
								// the Net folder?  (Note that they may
								// have ACL access to the entity but
								// NOT access to the Net Folder.)
								try {
									if      (workArea instanceof FolderEntry) fm.checkAccess(user, ((FolderEntry) workArea), FolderOperation.allowAccessNetFolder);
									else if (workArea instanceof Binder)      bm.checkAccess(user, ((Binder)      workArea), BinderOperation.allowAccessNetFolder);
								}
								catch (AccessControlException ace) {
									addUser = false;
								}
							}
							
							// If the user has access to the entity...
							if (addUser) {
								// ...add an AccessInfo for it to the reply.
								reply.addUser(
									new AccessInfo(
										user.getId(),
										user.getTitle(),
										"",
										GwtServerHelper.getUserAvatarUrl(
											bs,
											request,
											user)));
							}
						}
					}
				}
			}

			// Has this entity been shared?
			ShareItemSelectSpec	spec = new ShareItemSelectSpec();
			spec.setLatest(true);
			spec.setSharedEntityIdentifier(GwtShareHelper.getEntityIdentifierFromEntityId(entityId));
			spec.setAccountForInheritance(true);
			List<ShareItem> shareItems = getCleanShareList(bs.getSharingModule(), spec, true);
			if (MiscUtil.hasItems(shareItems)) {
				// Yes!  Scan the shares.
				List<Long> shareGroupIds = new ArrayList<Long>();
				List<Long> shareTeamIds  = new ArrayList<Long>();
				List<Long> shareUserIds  = new ArrayList<Long>();
				for (ShareItem si:  shareItems) {
					// If this share is expired or obsolete...
					if (si.isExpired() || (!(si.isLatest()))) {
						// ...skip it.
						continue;
					}

					// Add the recipient ID to the appropriate list.
					Long recipientId = si.getRecipientId();
					switch (si.getRecipientType()) {
					case group:  shareGroupIds.add(recipientId); break;
					case team:   shareTeamIds.add( recipientId); break;
					case user:   shareUserIds.add( recipientId); break;
					}
				}

				// Are there any users that the item was shared with?
				if (!(shareUserIds.isEmpty())) {
					// Yes!  Can we resolve them?
					List shareUsers = ResolveIds.getPrincipals(shareUserIds, true);
					if (MiscUtil.hasItems(shareUsers)) {
						// Yes!  Scan them...
						for (Object u:  shareUsers) {
							if (u instanceof UserPrincipal) {
								// ...and add an AccessInfo for each to
								// ...the reply.
								User shareUser = ((User) u);
								reply.addUser(
									new AccessInfo(
										shareUser.getId(),
										shareUser.getTitle(),
										"",
										GwtServerHelper.getUserAvatarUrl(
											bs,
											request,
											shareUser)));
							}
						}
					}
				}
				
				// Are there any groups that the item was shared with?
				if (!(shareGroupIds.isEmpty())) {
					// Yes!  Can we resolve them?
					List shareGroups = ResolveIds.getPrincipals(shareGroupIds, true);
					if (MiscUtil.hasItems(shareGroups)) {
						// Yes!  Scan them...
						for (Object g:  shareGroups) {
							if (g instanceof GroupPrincipal) {
								// ...and add an AccessInfo for each to
								// ...the reply.
								Group shareGroup = ((Group) g);
								reply.addGroup(new AccessInfo(shareGroup.getId(), shareGroup.getTitle()));
							}
						}
					}
				}
				
				// Are there any teams that the item was shared with?
				if (!(shareTeamIds.isEmpty())) {
					// Yes!  Scan them.
					for (Long teamId:  shareTeamIds) {
						// Does this team have any members?
						Set<Long> teamMemberIds = GwtServerHelper.getTeamMemberIds(bs, teamId, false);
						if (MiscUtil.hasItems(teamMemberIds)) {
							// Yes!  Can we resolve them?
							List sharePrincipals = ResolveIds.getPrincipals(teamMemberIds);
							if (MiscUtil.hasItems(sharePrincipals)) {
								// Yes!  Scan them...
								for (Object p:  sharePrincipals) {
									// ...and for any users...
									if (p instanceof UserPrincipal) {
										// ...and add an AccessInfo for
										// ...each to the reply.
										User shareUser = ((User) p);
										reply.addUser(
											new AccessInfo(
												shareUser.getId(),
												shareUser.getTitle(),
												"",
												GwtServerHelper.getUserAvatarUrl(
													bs,
													request,
													shareUser)));
									}
									
									// ...and for any groups...
									else if (p instanceof GroupPrincipal) {
										// ...and add an AccessInfo for
										// ...each to the reply.
										Group shareGroup = ((Group) p);
										reply.addGroup(new AccessInfo(shareGroup.getId(), shareGroup.getTitle()));
									}
								}
							}
						}
					}
				}
			}

			// Sort the List<AccessInfo>'s in the object we're
			// returning.
			Comparator<AccessInfo> aiComparator = new AccessInfoComparator();
			Collections.sort(reply.getGroups(), aiComparator);
			Collections.sort(reply.getUsers(),  aiComparator);

			// If we get here, reply refers to the
			// WhoHasAccessInfoRpcResponseData of the entry types for
			// the requested binders.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getWhoHasAccess( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Returns the User associated with the given Workspace.
	 */
	private static User getWorkspaceUser(AllModulesInjected bs, Workspace ws) {
		Principal wsOwner = ws.getOwner();
		if (wsOwner.isReserved() && ObjectKeys.SUPER_USER_INTERNALID.equals(wsOwner.getInternalId())) {
			User guest = bs.getProfileModule().getGuestUser();
			Long guestWSId = guest.getWorkspaceId();
			if ((null != guestWSId) && guestWSId.equals(ws.getId())) {
				return guest;
			}
		}
		return ((User) Utils.fixProxy(wsOwner));
	}
	
	private static User getWorkspaceUser(AllModulesInjected bs, Long workspaceId) {
		Binder binder = bs.getBinderModule().getBinderWithoutAccessCheck(workspaceId);
		if ((null != binder) && (binder instanceof Workspace)) {
			return getWorkspaceUser(bs, ((Workspace) binder));
		}
		return null;
	}
	
	/*
	 * Returns a ZipDownloadUrlRcpResponseData object containing the
	 * URL to use to download the listed files or all the files from a
	 * folder in a zip.
	 */
	private static ZipDownloadUrlRpcResponseData getZipDownloadUrlImpl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, Long folderId, boolean recursive) throws GwtTeamingException {
		try {
			// What's the maximum number of files in folders that we
			// can download in a zip?
			int maxFolderFiles = SPropsUtil.getInt("folder.zip.max.files", ObjectKeys.SEARCH_MAX_ZIP_FOLDER_FILES);	// Default is 1000.
			
			// Allocate a ZipDownloadUrlRpcResponseData to return the
			// URL to request downloading the files.
			ZipDownloadUrlRpcResponseData reply = new ZipDownloadUrlRpcResponseData();

			// Are we downloading a folder or selected files?
			String url = null;
			if (null == folderId) {
				// Selected Files!  Extract the entry and folder IDs of
				// the selections.
				List<Long> entryIds  = new ArrayList<Long>();
				List<Long> folderIds = new ArrayList<Long>();
				for (EntityId eid:  entityIds) {
					Long id = eid.getEntityId();
					if      (eid.isEntry())  entryIds.add( id);
					else if (eid.isFolder()) folderIds.add(id);
				}
				
				// Do we have anything to download?
				boolean hasEntries = (!(entryIds.isEmpty()));
				boolean hasFolders = (!(folderIds.isEmpty()));
				if (hasEntries || hasFolders) {
					// Yes!  Can we get the entries for the IDs?
					Map<FolderEntry, String> feAttachmentMap = new HashMap<FolderEntry, String>();
					Set<FolderEntry>         feSet           = bs.getFolderModule().getEntries(entryIds);
					if (MiscUtil.hasItems(feSet)) {
						// Yes!  Scan them.
						for (FolderEntry fe:  feSet) {
							// Can we access this entry's primary file
							// attachment?
							FileAttachment fa = GwtServerHelper.getFileEntrysFileAttachment(bs, fe, true);
							if (null != fa) {
								// Yes!  Add it to the attachment map.
								feAttachmentMap.put(fe, fa.getId());
							}
							else {
								// No, we can't get this entry's primary file
								// attachment!  Add a warning to the reply.
								reply.addWarning(NLT.get("zipDownloadUrlError.NoFile", fe.getTitle()));
							}
						}
					}
					
					// Can we get the folders for the IDs?
					Set<Folder> folderSet = bs.getFolderModule().getFolders(folderIds);
					
					// Are we only downloading a single file? 
					int fileDownloads   = feAttachmentMap.size();
					int folderDownloads = folderSet.size();
					if ((1 == fileDownloads) && (0 == folderDownloads)) {
						// Yes!  Generate a URL to download just that file.
						FolderEntry fe = feAttachmentMap.keySet().iterator().next();
						url = WebUrlUtil.getFileZipUrl(
							request,
							WebKeys.ACTION_READ_FILE,
							fe,
							feAttachmentMap.get(fe));
					}
					
					// No, we aren't downloading a single file!  Are we
					// downloading a single folder?
					else if ((0 == fileDownloads) && (1 == folderDownloads)) {
						// Yes!  Generate a URL to download just that
						// folder.
						url = WebUrlUtil.getFolderZipUrl(
							request,
							folderSet.iterator().next().getId(),
							recursive);
					}
					
					// No, we're downloading other than a single file
					// or folder!  Are we downloading anything?
					else if ((0 < fileDownloads) || (0 < folderDownloads)) {
						// Yes!  Generate a URL to download that list
						// of files.
						url = WebUrlUtil.getFileListZipUrl(
							request,
							feAttachmentMap.keySet(),
							folderSet,
							recursive);
					}
					
					else {
						// No, we don't have anything to download!  Add
						// an error to the reply.
						reply.addError(NLT.get("zipDownloadUrlError.NoEntities"));
					}

					// Are there any folders selected to download?
					if (0 < folderDownloads) {
						// Yes!  Scan them...
						int totalFileCount = 0;
						for (Folder folder:  folderSet) {
							// ...counting their files...
							totalFileCount += getFolderEntryCount(bs, folder.getId(), recursive);
							
							// ...and if there are too many...
							if (totalFileCount > maxFolderFiles) {
								// ...generate an error.
								reply.addError(NLT.get("zipDownloadUrlError.TooManyFilesInFolderToZip"));
								break;
							}
						}
					}
				}
				
				else {
					// No, we don't have anything to download!  Add an
					// error to the reply.
					reply.addError(NLT.get("zipDownloadUrlError.NoEntities"));
				}
			}
			
			else {
				// A folder!  If there are too many files in that
				// folder to download...
				if (getFolderEntryCount(bs, folderId, recursive) > maxFolderFiles) {
					// ...generate an error.
					reply.addError(NLT.get("zipDownloadUrlError.TooManyFilesInFolderToZip"));
				}
				
				// ...generate a URL to download the files from that
				// ...folder.
				url = WebUrlUtil.getFolderZipUrl(
					request,
					folderId,
					recursive);
			}
			
			// Add whatever URL we built to the reply.
			reply.setUrl(url);
			
			// If we get here, reply refers to the
			// ZipDownloadUrlRpcResponseData containing the URL to
			// download the requested files.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.getZipDownloadUrlImpl( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Returns a ZipDownloadUrlRcpResponseData object containing the
	 * URL to use to download the listed entities in a zip.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ZipDownloadUrlRpcResponseData getZipDownloadUrl(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds, boolean recursive) throws GwtTeamingException {
		// Always use the implementation form of the method.
		return getZipDownloadUrlImpl(bs, request, entityIds, null, recursive);
	}

	/**
	 * Returns a ZipDownloadUrlRcpResponseData object containing the
	 * URL to use to download all the files from a folder in a zip.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param recursive
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ZipDownloadUrlRpcResponseData getZipDownloadUrl(AllModulesInjected bs, HttpServletRequest request, Long folderId, boolean recursive) throws GwtTeamingException {
		// Always use the implementation form of the method.
		return getZipDownloadUrlImpl(bs, request, null, folderId, recursive);
	}

	/**
	 * Marks the selected shared entities as being hidden.
	 * 
	 * @param bs
	 * @param request
	 * @param ct
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean hideShares(AllModulesInjected bs, HttpServletRequest request, CollectionType ct, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Are there any entities to hide?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them...
				SharingModule sm = bs.getSharingModule();
                boolean isSharedWithMe = ct.equals(CollectionType.SHARED_WITH_ME);
                boolean isSharedPublic = ct.equals(CollectionType.SHARED_PUBLIC );
                List<EntityIdentifier> ids = new ArrayList<EntityIdentifier>();
				for (EntityId eid:  entityIds) {
                    ids.add(toEntityIdentifier(eid));
				}
                sm.hideSharedEntitiesForCurrentUser(ids, (isSharedWithMe || isSharedPublic));
			}
			
			// If we get here, the hide was successful.  Return true.
			return Boolean.TRUE;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.hideShares( SOURCE EXCEPTION ):  ");
		}
	}
	
	/*
	 * Initializes a ViewInfo based on a binder ID.
	 * 
	 * Returns true if the ViewInfo was initialized and false
	 * otherwise.
	 */
	@SuppressWarnings("unchecked")
	private static boolean initVIFromBinderId(HttpServletRequest request, AllModulesInjected bs, Map<String, String> nvMap, String binderIdName, ViewInfo vi) throws GwtTeamingException {
		// Initialize as a binder based on the user's workspace.
		Long binderId = getQueryParameterLong(nvMap, binderIdName);
		BinderInfo bi = GwtServerHelper.getBinderInfo(bs, request, String.valueOf(binderId));
		if (null == bi) {
			return false;
		}
		
		// Is the binder a workspace?
		User user = GwtServerHelper.getCurrentUser();
		vi.setViewType(ViewType.BINDER);
		if (bi.isBinderWorkspace()) {
			// Yes!  Does it have the showCollection parameter?
			String showCollection = getQueryParameterString(nvMap, WebKeys.URL_SHOW_COLLECTION);
			if (MiscUtil.hasString(showCollection)) {
				// Yes!  If it's other than My Files or it's My Files
				// and we aren't mapping it to their Home, setup a
				// BinderInfo for the collection.  Otherwise, setup a
				// BinderInfo for their Home. 
				Long homeId;
				CollectionType ct = CollectionType.getEnum(showCollection);
				if (CollectionType.MY_FILES.equals(ct) && SearchUtils.useHomeAsMyFiles(bs))
				     homeId = SearchUtils.getHomeFolderId(bs);
				else homeId = null;
				if (null == homeId)
				     bi = GwtServerHelper.buildCollectionBI(ct, user.getWorkspaceId());
				else bi = GwtServerHelper.getBinderInfo(bs, request, String.valueOf(homeId));
			}
		}

		// No, the binder isn't a workspace!  Is it a folder?
		else if (bi.isBinderFolder()) {
			// Yes!  Is it a 'My Files Storage' folder?
			Binder binder = bs.getBinderModule().getBinderWithoutAccessCheck(binderId);
			if ((null != binder) && BinderHelper.isBinderMyFilesStorage(binder) && (!(showMyFilesStorageAsFolder()))) {
				// Yes!  Is it the current user's?
				Binder userWS          = binder.getParentBinder();
				Long   currentUserWSId = user.getWorkspaceId();
				if (currentUserWSId.equals(userWS.getId())) {
					// Yes!  Are we supposed to redirect requests by
					// the owner of a 'My Files Storage' folder to
					// their 'My Files' collection instead?
					if (SPropsUtil.getBoolean("redirect.owner.myFilesStorage", true)) {
						// Yes!  Redirect.
						bi = GwtServerHelper.buildCollectionBI(
							CollectionType.MY_FILES,
							currentUserWSId);
					}
				}
				else  {
					// No, this isn't the current user's workspace!  It
					// must be somebody else's.  Are we supposed to
					// redirect requests to somebody else's 'My Files
					// Storage' folder to their profile instead?
					if (SPropsUtil.getBoolean("redirect.other.myFilesStorage", true)) {
						// Yes!  Redirect.
						vi.setViewType(ViewType.BINDER);
						vi.setOverrideUrl(GwtProfileHelper.getUserProfileUrl(request, userWS));
						bi = GwtServerHelper.getBinderInfo(bs, request, userWS.getId());
					}
				}
			} else if (GwtFolderViewHelper.hasCustomView(bs, binder)) {
				vi.setViewLayout(GwtFolderViewHelper.buildBinderViewLayout(binder));
				vi.setCustomLayout(true);
			}

		}
		
		// Store any BinderInfo change we made in the ViewInfo.
		vi.setBinderInfo(bi);

		// Are we showing the trash on a this binder?
		boolean showTrash = getQueryParameterBoolean(nvMap, WebKeys.URL_SHOW_TRASH);
		if (showTrash) {
			// Yes!  Update the folder/workspace type
			// accordingly.
			if      (bi.isBinderFolder())    bi.setFolderType(   FolderType.TRASH   );
			else if (bi.isBinderWorkspace()) bi.setWorkspaceType(WorkspaceType.TRASH);
		}

		// Are we supposed to invoke the share dialog on this binder?
		if (isQueryParamSet(nvMap, WebKeys.URL_INVOKE_SHARE, "1")) {
			// Yes!  Mark the ViewInfo accordingly.
			Binder	binder             = GwtUIHelper.getBinderSafely(bs.getBinderModule(), binderId);
			boolean	invokeShare        = (null != binder);
			boolean	invokeShareEnabled = (invokeShare && GwtShareHelper.isEntitySharable(bs, binder));
			vi.setInvokeShare(       invokeShare       );
			vi.setInvokeShareEnabled(invokeShareEnabled);
		}
		
		// Are we supposed to invoke the subscribe dialog on this binder?
		if (isQueryParamSet(nvMap, WebKeys.URL_INVOKE_SUBSCRIBE, "1")) {
			// Yes!  Mark the ViewInfo accordingly.
			vi.setInvokeSubscribe(true);
		}

		// Are we actually to view an entry in the binder?
		String entryTitle = getQueryParameterString(nvMap, WebKeys.URL_ENTRY_TITLE);
		if (MiscUtil.hasString(entryTitle)) {
			// Yes!  Can we access it?
			String zoneUUID = getQueryParameterString(nvMap, WebKeys.URL_ZONE_UUID);
			Set entries = bs.getFolderModule().getFolderEntryByNormalizedTitle(bi.getBinderIdAsLong(), entryTitle, zoneUUID);
			if (MiscUtil.hasItems(entries)) {
				// Yes!  Mark the ViewInfo as such.
				FolderEntry         fe   = ((FolderEntry) entries.iterator().next());
				Long                feId = fe.getId();
				ViewFolderEntryInfo	vfei = buildViewFolderEntryInfo(bs, request, fe.getParentBinder().getId(), feId);
				vi.setViewFolderEntryInfo(vfei);
				vi.setEntryViewUrl(
					GwtServerHelper.getViewFolderEntryUrl(
						bs,
						request,
						binderId,
						feId,
						isQueryParamSet(nvMap, WebKeys.URL_INVOKE_SHARE,     "1"),
						isQueryParamSet(nvMap, WebKeys.URL_INVOKE_SUBSCRIBE, "1")));
				vi.setViewType(ViewType.BINDER_WITH_ENTRY_VIEW);
				vi.setInvokeShare(       false);	// We'll invoke any share     on the entry, not the binder.
				vi.setInvokeShareEnabled(false);	// We'll invoke any share     on the entry, not the binder.
				vi.setInvokeSubscribe(   false);	// We'll invoke any subscribe on the entry, not the binder.
			}
		}

		// Finally, return true since if we get here, the view has been
		// initialized.
		return true;
	}
	
	/*
	 * Initializes a ViewInfo based on a User.
	 * 
	 * Returns true if the ViewInfo was initialized and false
	 * otherwise.
	 */
	private static boolean initVIFromUser(HttpServletRequest request, AllModulesInjected bs, User user, ViewInfo vi) {
		// Were we given a User object to initialize from?
		if (null == user) {
			// No!  Bail.
			return false;
		}

		// Initialize as a binder based on the user's workspace.
		Long wsId = user.getWorkspaceId();
		if (null == wsId) {
			return false;
		}
		BinderInfo bi = GwtServerHelper.getBinderInfo(bs, request, String.valueOf(wsId));
		if (null == bi) {
			return false;
		}
		vi.setViewType(ViewType.BINDER);
		vi.setBinderInfo(bi);
		
		return true;
	}

	/*
	 * Returns true if a FolderRow with the specified entity ID is in
	 * a List<FolderRow> or false otherwise.
	 */
	private static boolean isEntityInList(EntityId entityId, List<FolderRow> folderRows) {
		// Scan the List<FolderRow>.
		for (FolderRow fr:  folderRows) {
			// Do entry entity ID's and entity type's match?
			EntityId rowEID = fr.getEntityId();
			if (rowEID.getBinderId().equals(  entityId.getBinderId()) &&
				rowEID.getEntityId().equals(  entityId.getEntityId()) &&
				rowEID.getEntityType().equals(entityId.getEntityType())) {
				// Yes!  Then we found it.  Return true.
				return true;
			}
		}
		
		// If we get here, we couldn't find the entry in question.
		// Return false.
		return false;
	}

	/*
	 * Returns true if a query parameter is set to a specific value and
	 * false otherwise.
	 */
	private static boolean isQueryParamSet(Map<String, String> nvMap, String name, String value) {
		String qp = getQueryParameterString(nvMap, name);
		return (MiscUtil.hasString(qp) && qp.trim().equalsIgnoreCase(value.trim()));
	}

	/*
	 * Returns true if the given folder is a My Files Storage folder
	 * that should be disabled because the user's access to personal
	 * storage has been turned off and returns false otherwise.
	 */
	private static boolean isMyFilesStorageDisabled(AllModulesInjected bs, Binder myFilesStorage) {
		// Is the binder a My Files Storage folder?
		boolean reply = ((null != myFilesStorage) && myFilesStorage.isMyFilesDir());
		if (reply) {
			// Yes!  Can we resolve the owner of the My Files Storage?
			User mfsOwner = GwtServerHelper.getResolvedUser(myFilesStorage.getOwnerId(), true);
			reply = (null != mfsOwner);
			if (reply) {
				// Yes!  Does that user have personal storage access?
				reply = SearchUtils.useHomeAsMyFiles(bs, mfsOwner);
			}
		}
		
		// If we get here, reply is true if the binder is a My Files
		// Storage folder that the user shouldn't have access to and
		// false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Returns true if the specified user is external and false otherwise.
	 * 
	 * @param bs
	 * @param request
	 * @param userId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static boolean isUserExternal(AllModulesInjected bs, HttpServletRequest request, Long userId) throws GwtTeamingException {
		try {
			User user = ((User) bs.getProfileModule().getEntry(userId));
			return (!(user.getIdentityInfo().isInternal()));
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.isUserExternal( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Locks the entries.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData lockEntries(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Were we given the IDs of any entries to lock?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them.
				for (EntityId entityId:  entityIds) {
					// If the entity is a binder...
					if (entityId.isBinder()) {
						// ...skip it.
						continue;
					}
					
					try {
						// Can we lock this entry?
						bs.getFolderModule().reserveEntry(entityId.getBinderId(), entityId.getEntityId());
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
						String messageKey;
						if      (e instanceof AccessControlException)           messageKey = "lockEntryError.AccssControlException";
						else if (e instanceof ReservedByAnotherUserException)   messageKey = "lockEntryError.ReservedByAnotherUserException";
						else if (e instanceof FilesLockedByOtherUsersException) messageKey = "lockEntryError.FilesLockedByOtherUsersException";
						else                                                    messageKey = "lockEntryError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtViewHelper.lockEntryEntries( Entry title:  '" + entryTitle + "', EXCEPTION ):  ", e);
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.lockEntries( SOURCE EXCEPTION ):  ");
		}
	}

	/**
	 * Maps a BinderIconSize enumeration value to its equivalent
	 * IconSize.
	 * 
	 * @param bis
	 * 
	 * @return
	 */
	public static IconSize mapBISToIS(BinderIconSize bis) {
		IconSize reply;
		switch (bis) {
		case SMALL:   reply = IconSize.SMALL;     break;
		case MEDIUM:  reply = IconSize.MEDIUM;    break;
		case LARGE:   reply = IconSize.LARGE;     break;
		default:      reply = IconSize.UNDEFINED; break;
		}
		return reply;
	}

	/**
	 * Marks the contents of a folder as having been read.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData markFolderContentsRead(AllModulesInjected bs, HttpServletRequest request, Long folderId) throws GwtTeamingException {
		try {
			// Mark the contents of the folder as having been read.
			markFolderContentsReadUnreadImpl(bs, folderId, true);	// true -> Mark as read.
			
			// If we get here, we marked the entries has having been
			// read.  Return true.
			return new BooleanRpcResponseData(true);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.markFolderContentsRead( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Implementation method that actually marks the entries of a
	 * folder as having been read or unread.
	 */
	@SuppressWarnings("unchecked")
	private static void markFolderContentsReadUnreadImpl(AllModulesInjected bs, Long folderId, boolean markRead) {
		// Can we get any entries from the folder?
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES_READ_UNREAD);
		Map folderEntries = bs.getFolderModule().getEntries(folderId, options);
		if (null != folderEntries) {
			List<Map> feList = ((List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES));
			if (MiscUtil.hasItems(feList)) {
				// Yes!  Scan them.
				List<Long> entryIds = new ArrayList<Long>();
				for (Map feMap:  feList) {
					// Can we get this entry's ID?
					String entryIdStr = ((String) feMap.get(Constants.DOCID_FIELD));
					if (MiscUtil.hasString(entryIdStr)) {
						// Yes!  Add it to the entry ID list.
						entryIds.add(Long.parseLong(entryIdStr));
					}
				}
				
				// Are we tracking any entry IDs?
				if (!(entryIds.isEmpty())) {
					// Yes!  Mark them has having been seen/unseen.
					ProfileModule	pm     = bs.getProfileModule();
					Long			userId = GwtServerHelper.getCurrentUserId();
					if (markRead)
					     pm.setSeenIds(userId, entryIds);
					else pm.setUnseen( userId, entryIds);
				}
			}
		}
	}
	
	/**
	 * Marking the contents of a folder as having been unread.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData markFolderContentsUnread(AllModulesInjected bs, HttpServletRequest request, Long folderId) throws GwtTeamingException {
		try {
			// Mark the contents of the folder as having been unread.
			markFolderContentsReadUnreadImpl(bs, folderId, false);	// false -> Mark as unread.
			
			// If we get here, we marked the entries has having been
			// unread.  Return true.
			return new BooleanRpcResponseData(true);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.markFolderContentsUnread( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Moves the entries.
	 * 
	 * @param bs
	 * @param request
	 * @param targetFolderId
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static ErrorListRpcResponseData moveEntries(AllModulesInjected bs, HttpServletRequest request, Long targetFolderId, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Were we given the IDs of any entries to move?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Decide on the actual target for the move...
				CopyMoveTarget cmt = new CopyMoveTarget(bs, targetFolderId);
				if (cmt.isTargetMyFilesStorage() && cmt.isTargetMyFilesStorageDisabled()) {
					reply.addError(NLT.get("moveEntryError.cantTargetMFS"));
					return reply;
				}
				
				// ...and scan the entries to be moved.
				BinderModule bm = bs.getBinderModule();
				FolderModule fm = bs.getFolderModule();
				for (EntityId entityId:  entityIds) {
					try {
						// Can we move this entity?
						if (entityId.isBinder()) {
							if (cmt.isTargetUserWorkspace() && cmt.isTargetUserWorkspaceUnavailable()) {
								String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
								reply.addError(NLT.get("moveEntryError.cantTargetUserWS", new String[]{entryTitle}));
							}
							else if (BinderHelper.isBinderHomeFolder(bm.getBinder(entityId.getEntityId()))) {
								String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
								reply.addError(NLT.get("moveEntryError.cantMoveHome", new String[]{entryTitle}));
							}
							else {
								bm.moveBinder(entityId.getEntityId(), cmt.getBinderTargetId(), null);
							}
						}
						else {
							Map options = new HashMap();
							options.put(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS, Boolean.TRUE);	// We want any errors from the move propagate back up to us.
							fm.moveEntry(entityId.getBinderId(), entityId.getEntityId(), cmt.getEntryTargetId(), null, options);
						}
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
						String messageKey;
						NotSupportedException nse = null;
						if (e instanceof WriteFilesException) {
							messageKey = "moveEntryError.WriteFilesException";
							WriteFilesException wfe = ((WriteFilesException) e);
							FilesErrors errors = wfe.getErrors();
							if (null != errors) {
								List<Problem> problems = errors.getProblems();
								if (MiscUtil.hasItems(problems)) {
									Problem problem = problems.get(0);
									switch (problem.getType()) {
									default:
									case Problem.OTHER_PROBLEM:
									case Problem.PROBLEM_FILTERING:
									case Problem.PROBLEM_STORING_PRIMARY_FILE:
									case Problem.PROBLEM_CANCELING_LOCK:
									case Problem.PROBLEM_ARCHIVING:
									case Problem.PROBLEM_MIRRORED_FILE_IN_REGULAR_FOLDER:
									case Problem.PROBLEM_MIRRORED_FILE_MULTIPLE:
									case Problem.PROBLEM_REGULAR_FILE_IN_MIRRORED_FOLDER:
									case Problem.PROBLEM_MIRRORED_FILE_READONLY_DRIVER:
									case Problem.PROBLEM_ENCRYPTION_FAILED:
									case Problem.PROBLEM_CHECKSUM_MISMATCH:
							        case Problem.PROBLEM_ILLEGAL_CHARACTER:
										// Use the default
							        	// WriteFilesException message
							        	// setup above.
										break;
										
									case Problem.PROBLEM_FILE_EXISTS:
										messageKey = "moveEntryError.TitleException";
										break;
										
									case Problem.PROBLEM_DELETING_PRIMARY_FILE:
										// The WriteFilesException
										// error is inappropriate in
										// this case since it refers
										// to not being able to write
										// to the destination.
							        	messageKey = "moveEntryError.UncheckedIOException";
							        	break;									
									}
								}
							}
						}
						else if (e instanceof AccessControlException)         messageKey = "moveEntryError.AccssControlException";
						else if (e instanceof BinderQuotaException)           messageKey = "moveEntryError.BinderQuotaException";
						else if (e instanceof IllegalStateException)          messageKey = "moveEntryError.IllegalStateException";
						else if (e instanceof NotSupportedException)         {messageKey = "moveEntryError.NotSupportedException"; nse = ((NotSupportedException) e);}
						else if (e instanceof ReservedByAnotherUserException) messageKey = "moveEntryError.ReservedByAnotherUserException";
						else if (e instanceof TitleException)                 messageKey = "moveEntryError.TitleException";
						else if (e instanceof UncheckedIOException)           messageKey = "moveEntryError.UncheckedIOException";
						else                                                  messageKey = "moveEntryError.OtherException";
						String[] messageArgs;
						if (null == nse) {
							messageArgs = new String[]{entryTitle};
						}
						else {
							String messagePatch;
							String messageCode = nse.getErrorCode();
							if      (messageCode.equals("errorcode.notsupported.moveEntry.mirroredSource"))      messagePatch = NLT.get("moveEntryError.notsupported.Net.to.adHoc");
							else if (messageCode.equals("errorcode.notsupported.moveEntry.mirroredDestination")) messagePatch = NLT.get("moveEntryError.notsupported.adHoc.to.Net");
							else                                                                                 messagePatch = nse.getLocalizedMessage(); 
							messageArgs = new String[]{entryTitle, messagePatch};
						}
						reply.addError(NLT.get(messageKey, messageArgs));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtViewHelper.moveEntries( Entry title:  '" + entryTitle + "', EXCEPTION ):  ", e);
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.moveEntries( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Performs any post processing on the search entries being
	 * returned for a 'Shared by/with Me' collection view.
	 */
	@SuppressWarnings("unchecked")
	private static List<Map> postProcessSharedMeMap(AllModulesInjected bs, List<Map> searchEntries, List<GwtSharedMeItem> shareItems, boolean sortDescend, String sortBy) {
		// Do we have any search entries to process?
		if (MiscUtil.hasItems(searchEntries)) {
			// Yes!  Sort the list, based on the the information we
			// have for sorting it.
			Collections.sort(
				searchEntries,
				new SharedMeEntriesMapComparator(
					shareItems,
					sortBy,
					sortDescend));
		}

		// If we get here, searchEntries refers to the post processed
		// search entries Map.  Return it. 
		return searchEntries;
	}
	
	/*
	 * Given a list of column names, return those columns applicable to
	 * the collection type for the current license mode, ...
	 */
	private static String[] pruneColumnNames(CollectionType collection, String ... columnList) {
		// Scan the columns.
		List<String> columns = new ArrayList<String>();
		for (String column:  columnList) {
			// Add the column to the list we'll return.
			columns.add(column);
		}
		
		// Return a String[] of the columns that are applicable for the
		// current license, ... in the current collection type.
		return columns.toArray(new String[0]);
	}
	
	/*
	 * Given a list of column names, returns those column applicable to
	 * the folder type for the current license mode, ...
	 */
	private static String[] pruneColumnNames(FolderType folder, String ... columnList) {
		// If we're not in Filr mode...
		if (!(Utils.checkIfFilr())) {
			// ...we always use all the columns.
			return columnList;
		}

		// Scan the columns.
		List<String> columns = new ArrayList<String>();
		for (String column:  columnList) {
			// What type of folder are we working on?
			switch (folder) {
			case FILE:
				// File!  We show all but state.
				if (column.equals("state")) {
					continue;
				}
				break;
				
			case MIRROREDFILE:
				// MirroredFile!  We show all but state.
				if (column.equals("state")) {
					continue;
				}
				break;
				
			default:
				// For all others, we show all the columns.
				break;
			}
			
			// Add the column to the list we'll return.
			columns.add(column);
		}
		
		// Return a String[] of the columns that are applicable for the
		// current license, ... in the current folder type.
		return columns.toArray(new String[0]);
	}
	
	/**
	 * Renames an entity.
	 * 
	 * @param bs
	 * @param request
	 * @param eid
	 * @param entityName
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@SuppressWarnings("unchecked")
	public static StringRpcResponseData renameEntity(AllModulesInjected bs, HttpServletRequest request, EntityId eid, String entityName) throws GwtTeamingException {
		try {
			// Allocate a string response we can return any error in.
			StringRpcResponseData reply = new StringRpcResponseData();

			// Are we renaming a binder?
			if (eid.isBinder()) {
				// Yes!  Is this a 'Home' folder?
				BinderModule bm = bs.getBinderModule();
				if (BinderHelper.isBinderHomeFolder(bm.getBinder(eid.getEntityId()))) {
					// Yes!  They can't be renamed.
					String entryTitle = GwtServerHelper.getEntityTitle(bs, eid);
					reply.setStringValue(NLT.get("renameEntityError.cantRenameHome", new String[]{entryTitle}));
				}
				
				else {
					// No, this isn't a 'Home' folder!  Can we perform
					// the rename?
					HashMap rdMap = new HashMap();
					rdMap.put(ObjectKeys.FIELD_ENTITY_TITLE, entityName);
					MapInputData mid = new MapInputData(rdMap);
					try {
						bm.modifyBinder(
							eid.getEntityId(),
							mid,			// Input data.
							new HashMap(),	// No file items.
							new HashSet(),	// No delete attachments.
							null);			// No options.
					}
					
					catch (Exception e) {
						// No!  Return the reason why in the string response...
						String messageKey;
						if      (e instanceof AccessControlException)          messageKey = "renameEntityError.AccssControlException.";
						else if (e instanceof IllegalCharacterInNameException) messageKey = "renameEntityError.IllegalCharacterInNameException.";
						else if (e instanceof TitleException)		           messageKey = "renameEntityError.TitleException.";
						else if (e instanceof WriteFilesException)             messageKey = "renameEntityError.WriteFilesException.";
						else                                                   messageKey = "renameEntityError.OtherException.";
						if (eid.isFolder())
						     messageKey += "folder";
						else messageKey += "workspace";
						reply.setStringValue(NLT.get(messageKey, new String[]{entityName}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtViewHelper.renameEntity( Entity title:  '" + entityName + "', EXCEPTION:1 ):  ", e);
					}
				}
			}
			
			else {
				// No, we aren't renaming a binder!
				//
				// Note:  The logic I use here to rename the file
				//        and/or entry was copied from
				//        org.kablink.teaming.webdav.FileResource.renameFile()
				// 
				// Access the entry and attachment we're renaming.
				FolderModule fm = bs.getFolderModule();
				FolderEntry  fe;
				try {
					fe = fm.getEntry(eid.getBinderId(), eid.getEntityId());
				}
				catch (Exception e) {
					String messageKey;
					if (e instanceof FileNotFoundException) messageKey = "renameEntityError.DoesNotExist.file";
					else                                    messageKey = "renameEntityError.OtherException.file";
					reply.setStringValue(NLT.get(messageKey, new String[]{entityName}));
					return reply;
				}
				
				FileAttachment fa      = GwtServerHelper.getFileEntrysFileAttachment(bs, fe, true);
				String         faName  = ((null == fa) ? "" : fa.getFileItem().getName());
				String         feTitle = fe.getTitle();
				
				// If the current entry title is identical to the name
				// of the file, it's reasonable to change the title to
				// match the new name as well.  Do we have to rename
				// the entry?
				InputDataAccessor inputData;
				if ((null == fa) || faName.equals(feTitle)) {
					// Yes!  Setup the appropriate input data.
					Map data = new HashMap();
					data.put(ObjectKeys.FIELD_ENTITY_TITLE, entityName);
					inputData = new MapInputData(data);
				}
				else {
					// No, we don't have to rename the entry!
					inputData = new EmptyInputData();
				}

				// Setup the appropriate  file rename.
				Map<FileAttachment,String> renamesTo;
				if ((null == fa) || faName.equals(entityName)) {
					if (feTitle.equals(entityName)) {
						// Everything already matches the new name!  No
						// renaming is necessary;
						return reply;
					}
					renamesTo = null;
				}
				else {
					renamesTo = new HashMap<FileAttachment,String>();
					renamesTo.put(fa, entityName);
				}

				// Has the user already seen this entry?
				SeenMap seenMap = bs.getProfileModule().getUserSeenMap(null);
				boolean feSeen  = seenMap.checkIfSeen(fe);
				
				// Can we perform the rename of the entry and/or file?
				try {
					// If the name is bogus...
					if (Validator.containsPathCharacters(entityName)) {
						// ...throw an appropriate exception.
						throw new IllegalCharacterInNameException("errorcode.title.pathCharacters", new Object[]{entityName});
					}

					fm.modifyEntry(
						fe.getParentBinder().getId(),	//
						fe.getId(),						//
						inputData,						//
						null,							// null -> No file items.
						null,							// null -> No delete attachments.
						renamesTo,						//
						null);							// null -> No options.
					
					// If the user saw the entry before we modified it...
					if (feSeen) {
						// ...retain that seen state.
						bs.getProfileModule().setSeen(null, fe);
					}
				}
				
				catch (Exception e) {
					// No!  Did we get a WriteEntryDataException?
					String messageKey;
					boolean handledEx = false;
					if (e instanceof WriteEntryDataException) {
						// Yes!  Is it because a file with that name
						// already exists?
						if (null != fm.getLibraryFolderEntryByFileName(fe.getParentFolder(), entityName)) {
							// Yes!  Return a more specific error for
							// this condition than the code below would
							// result in. 
							messageKey = "entry.duplicateFileInLibrary3";
							reply.setStringValue(NLT.get(messageKey));
							handledEx = true;
						}
					}

					// Did we handle the exception above?
					if (!handledEx) {
						// No!  Return a generic reason why in the
						// string response.
						if      (e instanceof AccessControlException)          messageKey = "renameEntityError.AccssControlException.file";
						else if (e instanceof IllegalCharacterInNameException) messageKey = "renameEntityError.IllegalCharacterInNameException.file";
						else if (e instanceof ReservedByAnotherUserException)  messageKey = "renameEntityError.ReservedByAnotherUserException.file";
						else if (e instanceof WriteEntryDataException)         messageKey = "renameEntityError.WriteEntryDataException.file";
						else if (e instanceof WriteFilesException)             messageKey = "renameEntityError.WriteFilesException.file";
						else                                                   messageKey = "renameEntityError.OtherException.file";
						reply.setStringValue(NLT.get(messageKey, new String[]{entityName}));
					}
					
					// Whatever the exception was, log it.
					GwtLogHelper.error(m_logger, "GwtViewHelper.renameEntity( Entity title:  '" + entityName + ", EXCEPTION:2 ):  ", e);
				}
			}

			// If we get here, reply refers to a
			// StringRpcResponseData containing any error we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.renameEntity( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Maps an EntityId to its equivalent EntityIdentifier and returns it.
	 * 
	 * @param eid
	 * 
	 * @return
	 */
    public static EntityIdentifier toEntityIdentifier(EntityId eid) {
        if (eid.isEntry())     return new EntityIdentifier(eid.getEntityId(), EntityIdentifier.EntityType.folderEntry);
        if (eid.isFolder())    return new EntityIdentifier(eid.getEntityId(), EntityIdentifier.EntityType.folder     );
        if (eid.isWorkspace()) return new EntityIdentifier(eid.getEntityId(), EntityIdentifier.EntityType.workspace  );
        
        throw new UnsupportedOperationException("Can't convert EntityId to EntityIdentifier.  Unknown entity type: " + eid.getEntityType());
    }
    
	/**
	 * Unlocks the entries.
	 * 
	 * @param bs
	 * @param request
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static ErrorListRpcResponseData unlockEntries(AllModulesInjected bs, HttpServletRequest request, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Allocate an error list response we can return.
			ErrorListRpcResponseData reply = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());

			// Were we given the IDs of any entries to unlock?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them.
				for (EntityId entityId:  entityIds) {
					// If this entity is a binder...
					if (entityId.isBinder()) {
						// ...skip it.
						continue;
					}
					
					try {
						// Can we unlock this entry?
						bs.getFolderModule().unreserveEntry(entityId.getBinderId(), entityId.getEntityId());
					}

					catch (Exception e) {
						// No!  Add an error to the error list...
						String entryTitle = GwtServerHelper.getEntityTitle(bs, entityId);
						String messageKey;
						if      (e instanceof AccessControlException)         messageKey = "unlockEntryError.AccssControlException";
						else if (e instanceof ReservedByAnotherUserException) messageKey = "unlockEntryError.ReservedByAnotherUserException";
						else                                                  messageKey = "unlockEntryError.OtherException";
						reply.addError(NLT.get(messageKey, new String[]{entryTitle}));
						
						// ...and log it.
						GwtLogHelper.error(m_logger, "GwtViewHelper.unlockEntries( Entry title:  '" + entryTitle + "', EXCEPTION ):  ", e);
					}
				}
			}

			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.unlockEntries( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Saves whether the accessory panel should be visible on the
	 * given binder.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param showAccessoryPanel
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveAccessoryStatus(AllModulesInjected bs, HttpServletRequest request, Long binderId, boolean showAccessoryPanel) throws GwtTeamingException {
		try {
			// 7/8/2015 JK (bug #935487) To reduce the chance of getting optimistic locking error 
			// during the transaction, clear the session before the attempt to make the changes.
			getCoreDao().clear();
			
			// Save the accessory status...
			bs.getProfileModule().setUserProperty(
				GwtServerHelper.getCurrentUserId(),
				binderId,
				ObjectKeys.USER_PROPERTY_BINDER_SHOW_ACCESSORIES,
				new Boolean(showAccessoryPanel));
			
			// ...and return true.
			return Boolean.TRUE;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.saveAccessoryStatus( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Stores a region state for the current user on a binder.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param regionId
	 * @param regionState
	 * 
	 * @return
	 */
	public static BooleanRpcResponseData saveBinderRegionState(AllModulesInjected bs, HttpServletRequest request, Long binderId, String regionId, String regionState) throws GwtTeamingException {
		try {
			// Store the new region state and return true.
			bs.getProfileModule().setUserProperty(GwtServerHelper.getCurrentUserId(), binderId, ObjectKeys.USER_PROPERTY_REGION_VIEW + "." + regionId, regionState);
			return new BooleanRpcResponseData(Boolean.TRUE);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.saveBinderRegionState( SOURCE EXCEPTION ):  ");
		}
	}

	/**
	 * Stores the column widths for a user on a folder.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param columnWidths
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static BooleanRpcResponseData saveColumnWidths(AllModulesInjected bs, HttpServletRequest request, BinderInfo folderInfo, Map<String, String> columnWidths) throws GwtTeamingException {
		try {
			// Store the column widths...
			String propKey = ObjectKeys.USER_PROPERTY_COLUMN_WIDTHS;
			if (folderInfo.isBinderTrash()) {
				propKey += ".Trash";
			}
			bs.getProfileModule().setUserProperty(
				GwtServerHelper.getCurrentUserId(),
				folderInfo.getBinderIdAsLong(),
				propKey,
				columnWidths);
			
			// ...and return true.
			return new BooleanRpcResponseData(Boolean.TRUE);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.saveColumnWidths( SOURCE EXCEPTION ):  ");
		}
	}

	/**
	 * Saves the position of the View Details dialog on a FolderEntry
	 * to the user's properties.
	 * 
	 * @param bs
	 * @param request
	 * @param x
	 * @param y
	 * @param cx
	 * @param cy
	 * 
	 * @throws GwtTeamingException
	 */
	public static void saveFolderEntryDlgPosition(AllModulesInjected bs, HttpServletRequest request, int x, int y, int cx, int cy) throws GwtTeamingException {
		try {
			// Is this the guest user?
			User	user    = GwtServerHelper.getCurrentUser();
			boolean	isGuest = user.isShared();
			if (isGuest) {
				// Yes!  Then we don't save the dialog's position.
				return;
			}

			// Pack the dialog position values into a string...
			String packedPosition = StringUtil.pack(new String[]{
				String.valueOf(x),	
				String.valueOf(y),	
				String.valueOf(cx),	
				String.valueOf(cy),	
			});

			// ...and store them in the user's properties.
			bs.getProfileModule().setUserProperty(
				user.getId(),
				ObjectKeys.USER_PROPERTY_FOLDER_ENTRY_DLG_POSITION,
				packedPosition);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.saveFolderEntryDlgPosition( SOURCE EXCEPTION ):  ");
		}
	}

	/*
	 * Generates a value for a custom column in a row.
	 * 
	 * The algorithm used in this method was reverse engineered from
	 * that used by folder_view_common2.jsp or view_trash.jsp.
	 */
	@SuppressWarnings("unchecked")
	private static void setValueForCustomColumn(AllModulesInjected bs, Map entryMap, FolderRow fr, FolderColumn fc) throws ParseException {
		try {
			// If we don't have a column element name...
			String colEleName = fc.getColumnEleName();
			if (!MiscUtil.hasString(colEleName)) {
				// ...just render it as an empty string.
				fr.setColumnValue(fc, "");
				return;
			}

			// Do we have a value or event for this column?
			Object colValue = entryMap.get(colEleName);
			String colType = fc.getColumnType();
			if (null == colType) colType = "";
			if ((null != colValue) || colType.equals("event")) {
				// Yes!  Handles those that are a simple list of values.
				Definition entryDef = bs.getDefinitionModule().getDefinition(fc.getColumnDefId());
				if (colType.equals("selectbox")           ||
					    colType.equals("radio")           ||
					    colType.equals("checkbox")        ||
					    colType.equals("text")            ||
					    colType.equals("entryAttributes") ||
					    colType.equals("hidden")          ||
					    colType.equals("number")) {
					String strValue = DefinitionHelper.getCaptionsFromValues(entryDef, colEleName, colValue.toString());
					int    strLen   = ((null == strValue) ? 0 : strValue.length());
					if (colType.equals("number") && (3 <= strLen) && strValue.endsWith(".0")) {	//  3 so we have something before the '.0'.
						strValue = strValue.substring(0, (strLen - 2));							// -2 to strip off the '.0'.
					}
					fr.setColumnValue(fc, strValue);
				}

				// Handle those that are a URL.
				else if (colType.equals("url")) {
					// Can we find the definition of the column?
		         	Element colElement = ((Element) DefinitionHelper.findAttribute(colEleName, entryDef.getDefinition()));
		         	if (null == colElement) {
		         		// No!  Just render the column as an empty string.
						fr.setColumnValue(fc, "");
		         	}
		         	
		         	else {
						// Yes, we found the definition of the column!
		         		// Extract what we need to render it.
			         	String linkText = DefinitionHelper.getItemProperty(colElement, "linkText");
			         	String target   = DefinitionHelper.getItemProperty(colElement, "target"  );
			         	
			         	String  strValue   = DefinitionHelper.getCaptionsFromValues(entryDef, colEleName, colValue.toString());
			         	if (!(MiscUtil.hasString(linkText))) linkText = strValue;
			         	if ((null == target) || target.equals("false")) target = "";
			         	if ((null != target) && target.equals("true" )) target = "_blank";
			         	
			         	// Construct an EntryLinkInfo for the data.
			         	EntryLinkInfo linkValue = new EntryLinkInfo(strValue, target, linkText);
						fr.setColumnValue(fc, linkValue);
		         	}
				}

				// Handle date stamps.
				else if (colType.equals("date")) {
					if (null != colValue) {
						String tdStamp = ((String) colValue);
					    String year    = tdStamp.substring(0, 4);
						String month   = tdStamp.substring(4, 6);
						String day     = tdStamp.substring(6, 8);
						String strValue;
						if (8 < tdStamp.length()) {
							String time = tdStamp.substring(8);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd:HHmm");
							Date date = formatter.parse(year + "-" + month + "-" + day + ":" + time);
							strValue = GwtServerHelper.getDateString(date, DateFormat.SHORT);
						}
						else {
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							Date date = formatter.parse(year + "-" + month + "-" + day);
							strValue = GwtServerHelper.getDateString(date, DateFormat.SHORT);
						}
						fr.setColumnValue(fc, strValue);
					}
				}

				// Handle time/date stamps.
				else if (colType.equals("date_time")) {
					if (null != colValue) {
						String tdStamp = ((String) colValue);
					    String year    = tdStamp.substring(0, 4);
						String month   = tdStamp.substring(4, 6);
						String day     = tdStamp.substring(6, 8);
						String strValue;
						if (8 < tdStamp.length()) {
							String time = tdStamp.substring(8, 12);
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd:HHmm");
							Date date = formatter.parse(year + "-" + month + "-" + day + ":" + time);
							strValue = GwtServerHelper.getDateTimeString(date, DateFormat.SHORT, DateFormat.SHORT);
						}
						else {
							SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
							Date date = formatter.parse(year + "-" + month + "-" + day);
							strValue = GwtServerHelper.getDateString(date, DateFormat.SHORT);
						}
						fr.setColumnValue(fc, strValue);
					}
				}

				// Handle events.
				else if (colType.equals("event")) {
					String tzId = ((String) entryMap.get(colEleName + "#TimeZoneID"));
					boolean showTimes = false;
					Date logicalEnd   = ((Date) entryMap.get(colEleName + "#LogicalEndDate"));
					Date logicalStart = ((Date) entryMap.get(colEleName + "#LogicalStartDate"));
					if ((null != logicalStart) && (null != logicalEnd)) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						if (sdf.format(logicalStart).equals(sdf.format(logicalEnd))) {
							// The two dates are the same, so show the
							// time field.
							showTimes = true;
						}
					}
					
					String logicalEndS   = null;
					String logicalEtartS = null;
					boolean allDayEvent  = (null == tzId);
					if (allDayEvent) {
						//All day event.
						if (null != logicalStart) logicalEtartS = GwtServerHelper.getDateString(logicalStart, DateFormat.SHORT);
						if (null != logicalEnd)   logicalEndS   = GwtServerHelper.getDateString(logicalEnd,   DateFormat.SHORT);
					}
					
					else {
						// Regular event.
						if (null != logicalStart) {
							if (showTimes)
							     logicalEtartS = GwtServerHelper.getDateTimeString(logicalStart, DateFormat.SHORT, DateFormat.SHORT);
							else logicalEtartS = GwtServerHelper.getDateString(    logicalStart, DateFormat.SHORT                  );
							
						}
						if (null != logicalEnd) {
							if (showTimes)
							     logicalEndS = GwtServerHelper.getDateTimeString(logicalEnd, DateFormat.SHORT, DateFormat.SHORT);
							else logicalEndS = GwtServerHelper.getDateString(    logicalEnd, DateFormat.SHORT                  );
						}
					}
					
					EntryEventInfo eventValue = new EntryEventInfo(allDayEvent, logicalEtartS, logicalEndS);
					Date actualEnd   = ((Date) entryMap.get(colEleName + "#EndDate"));
					int  durDays     = getDurDaysFromEntryMap(colEleName, entryMap);
					boolean daysOnly = ((null == actualEnd) && (0 < durDays));
					if (daysOnly) {
						// Duration only.
						eventValue.setDurationDaysOnly(true   );
						eventValue.setDurationDays(    durDays);
					}
		         	
					// Construct an EntryEventInfo for the data.
					fr.setColumnValue(fc, eventValue);
				}

				// Handle lists of users.
				else if (colType.equals("user_list") || colType.equals("userListSelectbox")) {
					String       idList     = colValue.toString();
					Set          ids        = LongIdUtil.getIdsAsLongSet(idList, ","  );
					List         principals = ResolveIds.getPrincipals(  ids,    false);
					StringBuffer strValue   = new StringBuffer("");
					boolean      firstP     = true;
					for (Object pO:  principals) {
						Principal p = ((Principal) pO);
						if (!firstP) {
							strValue.append(", ");
						}
						strValue.append(p.getTitle());
						firstP = false;
					}
					fr.setColumnValue(fc, strValue.toString());
				}
			}
			
			else {
				// No, we don't have a value or event for this column!
				// Display blank for it.
				fr.setColumnValue(fc, "");
			}
		}
		
		catch (Exception ex) {
			// Log the exception...
			GwtLogHelper.debug(m_logger, "GwtViewHelper.setValueForCustomColumn( EXCEPTION ):  ", ex);
			GwtLogHelper.debug(m_logger, "...Element:  " + fc.getColumnEleName());
			GwtLogHelper.debug(m_logger, "...Column:  "  + fc.getColumnName());
			GwtLogHelper.debug(m_logger, "...Row:  "     + fr.getEntityId().getEntityId());

			// ...and store something for the column to display.
			fr.setColumnValue(
				fc,
				(GwtUIHelper.isVibeUiDebug()         ?
					("Exception:  " + ex.toString()) :
					""));
		}
	}

	/**
	 * Stores the values from a SharedViewState object in the session
	 * cache.
	 * 
	 * @param bs
	 * @param request
	 * @param ct
	 * @param svs
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveSharedViewState(AllModulesInjected bs, HttpServletRequest request, CollectionType ct, SharedViewState svs) throws GwtTeamingException {
		try {
			// Store/remove the values from the cache and return true.
			HttpSession hSession = GwtServerHelper.getCurrentHttpSession();
			if (svs.isShowHidden())    hSession.setAttribute(   CACHED_SHARED_VIEW_SHOW_HIDDEN_BASE     + ct.name(), Boolean.TRUE); else hSession.removeAttribute(CACHED_SHARED_VIEW_SHOW_HIDDEN_BASE     + ct.name()               );
			if (svs.isShowNonHidden()) hSession.removeAttribute(CACHED_SHARED_VIEW_SHOW_NON_HIDDEN_BASE + ct.name()              ); else hSession.setAttribute(   CACHED_SHARED_VIEW_SHOW_NON_HIDDEN_BASE + ct.name(), Boolean.FALSE);
			return Boolean.TRUE;
		}
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.saveSharedViewState( SOURCE EXCEPTION ):  ");
		}		
	}

	/**
	 * Stores whether the user is viewing pinned entries on a given
	 * folder.
	 * 
	 * @param request
	 * @param folderId
	 * @param viewingPinnedEntries
	 */
	public static void saveUserViewPinnedEntries(HttpServletRequest request, Long folderId, boolean viewingPinnedEntries) {
		HttpSession session = WebHelper.getRequiredSession(request);
		session.setAttribute((CACHED_VIEW_PINNED_ENTRIES_BASE + folderId), new Boolean(viewingPinnedEntries));
	}
	
	/**
	 * Stores whether the user is viewing shared files on a given
	 * collection.
	 * 
	 * @param request
	 * @param collectionType
	 * @param viewingSharedFiles
	 */
	public static void saveUserViewSharedFiles(HttpServletRequest request, CollectionType collectionType, boolean viewingSharedFiles) {
		HttpSession session = WebHelper.getRequiredSession(request);
		session.setAttribute((CACHED_VIEW_SHARED_FILES_BASE + collectionType.ordinal()), new Boolean(viewingSharedFiles));
	}

	/**
	 * Controls whether the My Files Storage folder will appear in the
	 * UI as a normal folder or will be hidden as much as possible, as
	 * it is in Filr.
	 *  
	 * @return
	 */
	public static boolean showMyFilesStorageAsFolder() {
		return (SHOW_MY_FILES_STORAGE_IN_VIBE && LicenseChecker.showVibeFeatures());
	}
	
	/**
	 * Marks the selected shared entities as no longer being hidden.
	 * 
	 * @param bs
	 * @param request
	 * @param ct
	 * @param entityIds
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean showShares(AllModulesInjected bs, HttpServletRequest request, CollectionType ct, List<EntityId> entityIds) throws GwtTeamingException {
		try {
			// Are there any entities to show?
			if (MiscUtil.hasItems(entityIds)) {
				// Yes!  Scan them.
                SharingModule sm = bs.getSharingModule();
				boolean isSharedWithMe = ct.equals(CollectionType.SHARED_WITH_ME);
				boolean isSharedPublic = ct.equals(CollectionType.SHARED_PUBLIC );
                List<EntityIdentifier> ids = new ArrayList<EntityIdentifier>();
                for (EntityId eid:  entityIds) {
                    ids.add(toEntityIdentifier(eid));
                }
                sm.unhideSharedEntitiesForCurrentUser(ids, (isSharedWithMe || isSharedPublic));
			}
			
			// If we get here, the show was successful.  Return true.
			return Boolean.TRUE;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtViewHelper.showShares( SOURCE EXCEPTION ):  ");
		}
	}

	/**
	 * Returns true if the given filename supports viewing as HTML and
	 * false otherwise.
	 * 
	 * @param fName
	 * 
	 * @return
	 */
	public static boolean supportsViewAsHtml(String fName) {
		if (!(MiscUtil.hasString(fName))) {
			return false;
		}

        return SsfsUtil.supportsViewAsHtml(fName) || MiscUtil.isPdf(fName);
	}
	
	/*
	 * Strips the leading 0's and any trying decimal information off a
	 * String value and returns it.
	 */
	private static String trimFileSize(String value) {
		if (null != value) {
	        while (value.startsWith("0")) {
	        	value = value.substring(1, value.length());
	        }
	        value = trimFollowing(value, ".");	// Cleans any decimal...
	        value = trimFollowing(value, ",");	// information.
	        if (value.equals("")) {
	        	value = "0";
	        }
		}
		return value;
	}

	/*
	 * Strips everything in a string followingThis.
	 */
	private static String trimFollowing(String value, String followingThis) {
		if (null != value) {
	        int dPos = value.indexOf(followingThis);
	        while ((-1) != dPos) {
	        	value = value.substring(0, dPos);
		        dPos = value.indexOf(followingThis);
	        }
		}
		return value;
	}

	/**
	 * Returns true if a string values contains a quick filter and
	 * false otherwise.
	 * 
	 * @param value
	 * @param quickFilter
	 * 
	 * @return
	 */
	public static boolean valueContainsQuickFilter(String value, String quickFilter) {
		if (null != value) {
			value = value.trim().toLowerCase();
		}
		
		if (MiscUtil.hasString(value))
		     return value.contains(quickFilter);
		else return false;
	}
}
