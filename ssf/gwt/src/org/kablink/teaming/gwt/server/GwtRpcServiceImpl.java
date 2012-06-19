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
package org.kablink.teaming.gwt.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchCriteria.SearchScope;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTag;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.mainmenu.FavoriteInfo;
import org.kablink.teaming.gwt.client.mainmenu.GroupInfo;
import org.kablink.teaming.gwt.client.mainmenu.RecentPlaceInfo;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.mainmenu.TeamManagementInfo;
import org.kablink.teaming.gwt.client.mainmenu.ToolbarItem;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.profile.DiskUsageInfo;
import org.kablink.teaming.gwt.client.profile.ProfileAttribute;
import org.kablink.teaming.gwt.client.profile.ProfileInfo;
import org.kablink.teaming.gwt.client.profile.ProfileStats;
import org.kablink.teaming.gwt.client.profile.UserStatus;
import org.kablink.teaming.gwt.client.rpc.shared.*;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.TagSortOrder;
import org.kablink.teaming.gwt.client.util.TaskListItem.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.ShowSetting;
import org.kablink.teaming.gwt.client.util.SubscriptionData;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskId;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.whatsnew.EventValidation;
import org.kablink.teaming.gwt.server.util.GwtActivityStreamHelper;
import org.kablink.teaming.gwt.server.util.GwtProfileHelper;
import org.kablink.teaming.gwt.server.util.GwtServerHelper;
import org.kablink.teaming.gwt.server.util.GwtTaskHelper;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.search.filter.SearchFilterKeys;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.ssfs.util.SsfsUtil;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.FavoritesLimitExceededException;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.GwtUISessionData;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.search.Constants;

/**
 * 
 * @author jwootton
 */
public class GwtRpcServiceImpl extends AbstractAllModulesInjected
	implements GwtRpcService
{
	protected static Log m_logger = LogFactory.getLog(GwtRpcServiceImpl.class);

	/**
	 * Execute the given command.
	 */
	public VibeRpcResponse executeCommand( HttpRequestInfo ri, VibeRpcCmd cmd ) throws GwtTeamingException
	{
		VibeRpcResponse response = null;
		HttpServletRequest req;
		
		req = getRequest( ri );

		VibeRpcCmdType cmdEnum = VibeRpcCmdType.getEnum( cmd.getCmdType() );
		switch ( cmdEnum )
		{
		case ADD_FAVORITE:
		{
			AddFavoriteCmd afCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			afCmd = (AddFavoriteCmd) cmd;
			result = addFavorite( ri, afCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case CAN_MODIFY_BINDER:
		{
			CanModifyBinderCmd cmbCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			cmbCmd = (CanModifyBinderCmd) cmd;
			result = GwtServerHelper.canModifyBinder( this, cmbCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case COLLAPSE_SUBTASKS:
		{
			CollapseSubtasksCmd csCmd = ((CollapseSubtasksCmd) cmd);
			Boolean result = collapseSubtasks( ri, csCmd.getBinderId(), csCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case CREATE_GROUP:
		{
			CreateGroupCmd cgCmd;
			GroupInfo groupInfo;
			Group group;
			
			cgCmd = (CreateGroupCmd) cmd;
			group = GwtServerHelper.createGroup( this, cgCmd.getName(), cgCmd.getTitle(), cgCmd.getDesc(), cgCmd.getIsMembershipDynamic(), cgCmd.getMembership(), cgCmd.getMembershipCriteria() );
			groupInfo = new GroupInfo();
			if ( group != null )
			{
				Description desc;
				
				groupInfo.setId( group.getId() );
				groupInfo.setName( group.getName() );
				groupInfo.setTitle( group.getTitle() );
				groupInfo.setIsMembershipDynamic( group.isDynamic() );

				desc = group.getDescription();
				if ( desc != null )
					groupInfo.setDesc( desc.getText() );
			}
			response = new VibeRpcResponse( groupInfo );
			
			return response;
		}
		
		case DELETE_GROUPS:
		{
			Boolean result;
			DeleteGroupsCmd dgCmd;
			
			dgCmd = (DeleteGroupsCmd) cmd;
			result = GwtServerHelper.deleteGroups( this, dgCmd.getListOfGroupsToDelete() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case DELETE_TASKS:
		{
			DeleteTasksCmd dtCmd = ((DeleteTasksCmd) cmd);
			Boolean result = deleteTasks( ri, dtCmd.getTaskIds() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case EXECUTE_SEARCH:
		{
			GwtSearchResults searchResults;
			GwtSearchCriteria searchCriteria;
			
			searchCriteria = ((ExecuteSearchCmd) cmd).getSearchCriteria();
			
			try
			{
				searchResults = executeSearch( ri, searchCriteria );
			}
			catch (Exception ex)
			{
				GwtTeamingException gtEx;
				
				gtEx = GwtServerHelper.getGwtTeamingException( ex );
				throw gtEx;				
			}
			
			response = new VibeRpcResponse( searchResults );
			return response;
		}
		
		case EXPAND_HORIZONTAL_BUCKET:
		{
			ExpandHorizontalBucketCmd ehbCmd;
			TreeInfo result;
			
			ehbCmd = (ExpandHorizontalBucketCmd) cmd;
			result = expandHorizontalBucket( ri, ehbCmd.getBucketInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case EXPAND_SUBTASKS:
		{
			ExpandSubtasksCmd csCmd = ((ExpandSubtasksCmd) cmd);
			Boolean result = expandSubtasks( ri, csCmd.getBinderId(), csCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case EXPAND_VERTICAL_BUCKET:
		{
			ExpandVerticalBucketCmd evbCmd;
			TreeInfo result;
			
			evbCmd = (ExpandVerticalBucketCmd) cmd;
			result = expandVerticalBucket( ri, evbCmd.getBucketInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}

		case GET_ACTIVITY_STREAM_DATA:
		{
			GetActivityStreamDataCmd gasdCmd = ((GetActivityStreamDataCmd) cmd);
			ActivityStreamData asData = getActivityStreamData(
				ri,
				gasdCmd.getActivityStreamParams(),
				gasdCmd.getActivityStreamInfo(),
				gasdCmd.getPagingData(),
				gasdCmd.getActivityStreamDataType() );
			response = new VibeRpcResponse( new ActivityStreamDataRpcResponseData( asData ) );
			return response;
		}
		
		case GET_ACTIVITY_STREAM_PARAMS:
		{
			ActivityStreamParams asParams;
			
			asParams = getActivityStreamParams( ri );
			response = new VibeRpcResponse( asParams );
			return response;
		}
		
		case GET_ADD_MEETING_URL:
		{
			GetAddMeetingUrlCmd gamuCmd;
			String result;
			StringRpcResponseData responseData;
			
			gamuCmd = (GetAddMeetingUrlCmd) cmd;
			result = getAddMeetingUrl( ri, gamuCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ADMIN_ACTIONS:
		{
			ArrayList<GwtAdminCategory> adminActions;
			String binderId;
			AdminActionsRpcResponseData responseData;
			
			binderId = ((GetAdminActionsCmd)cmd).getBinderId();
			adminActions = getAdminActions( ri, binderId );
			
			responseData = new AdminActionsRpcResponseData( adminActions );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ALL_GROUPS:
		{
			List<GroupInfo> result;
			GetGroupsRpcResponseData responseData;
			
			result = GwtServerHelper.getAllGroups( this );
			responseData = new GetGroupsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_BINDER_BRANDING:
		{
			GwtBrandingData brandingData;
			
			brandingData = GwtServerHelper.getBinderBrandingData( this, ((GetBinderBrandingCmd) cmd).getBinderId(), req );

			response = new VibeRpcResponse( brandingData );
			return response;
		}
		
		case GET_BINDER_INFO:
		{
			BinderInfo binderInfo;
			String binderId;
			
			binderId = ((GetBinderInfoCmd) cmd).getBinderId();
			binderInfo = getBinderInfo( ri, binderId );
			response = new VibeRpcResponse( binderInfo );
			return response;
		}
		
		case GET_BINDER_PERMALINK:
		{
			GetBinderPermalinkCmd gbpCmd;
			String permalink;
			StringRpcResponseData responseData;
			
			gbpCmd = (GetBinderPermalinkCmd) cmd;
			permalink = getBinderPermalink( ri, gbpCmd.getBinderId() );
			responseData = new StringRpcResponseData( permalink );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_BINDER_TAGS:
		{
			GetBinderTagsCmd gbtCmd;
			ArrayList<TagInfo> result;
			GetTagsRpcResponseData responseData;
			
			gbtCmd = (GetBinderTagsCmd) cmd;
			result = getBinderTags( ri, gbtCmd.getBinderId() );
			responseData = new GetTagsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_DEFAULT_ACTIVITY_STREAM:
		{
			ActivityStreamInfo asi;
			String binderId;
			
			binderId = ((GetDefaultActivityStreamCmd) cmd).getBinderId();
			asi = getDefaultActivityStream( ri, binderId );
			response = new VibeRpcResponse( asi );
			return response;
		}
		
		case GET_DEFAULT_FOLDER_DEFINITION_ID:
		{
			GetDefaultFolderDefinitionIdCmd gdfdiCmd;
			StringRpcResponseData responseData;
			String result;
			
			gdfdiCmd = (GetDefaultFolderDefinitionIdCmd) cmd;
			result = getDefaultFolderDefinitionId( ri, gdfdiCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_DISK_USAGE_INFO:
		{
			GetDiskUsageInfoCmd gduiCmd;
			DiskUsageInfo result;
			
			gduiCmd = (GetDiskUsageInfoCmd) cmd;
			result = getDiskUsageInfo( ri, gduiCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_DOCUMENT_BASE_URL:
		{
			String binderId;
			String result;
			
			binderId = ((GetDocBaseUrlCmd) cmd).getBinderId();
			result = getDocumentBaseUrl( ri, binderId );
			response = new VibeRpcResponse( new StringRpcResponseData( result ) );
			return response;
		}
		
		case GET_DYNAMIC_MEMBERSHIP_CRITERIA:
		{
			GetDynamicMembershipCriteriaCmd gglqCmd;
			GwtDynamicGroupMembershipCriteria membershipCriteria;
			
			gglqCmd = (GetDynamicMembershipCriteriaCmd) cmd;
			membershipCriteria = GwtServerHelper.getDynamicMembershipCriteria( this, gglqCmd.getGroupId() );
			response = new VibeRpcResponse( membershipCriteria );
			return response;
		}
		
		case GET_ENTRY:
		{
			GetEntryCmd geCmd;
			GwtFolderEntry result;
			
			geCmd = (GetEntryCmd) cmd;
			result = getEntry( ri, geCmd.getZoneUUId(), geCmd.getEntryId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ENTRY_TAGS:
		{
			GetEntryTagsCmd getCmd;
			ArrayList<TagInfo> result;
			GetTagsRpcResponseData responseData;
			
			getCmd = (GetEntryTagsCmd) cmd;
			result = getEntryTags( ri, getCmd.getEntryId() );
			responseData = new GetTagsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_EXTENSION_FILES:
		{
			GetExtensionFilesCmd gefCmd;
			ExtensionFiles result;
			
			gefCmd = (GetExtensionFilesCmd) cmd;
			result = getExtensionFiles( ri, gefCmd.getId(), gefCmd.getZoneName() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_EXTENSION_INFO:
		{
			ExtensionInfoClient[] result;
			GetExtensionInfoRpcResponseData responseData;
			
			result = getExtensionInfo( ri );
			responseData = new GetExtensionInfoRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FAVORITES:
		{
			GetFavoritesRpcResponseData responseData;
			List<FavoriteInfo> result;
			
			result = getFavorites( ri );
			responseData = new GetFavoritesRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FILE_ATTACHMENTS:
		{
			GetFileAttachmentsRpcResponseData responseData;
			GetFileAttachmentsCmd gfaCmd;
			ArrayList<String> result;
			
			gfaCmd = (GetFileAttachmentsCmd) cmd;
			result = getFileAttachments( ri, gfaCmd.getBinderId() );
			responseData = new GetFileAttachmentsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FILE_SYNC_APP_CONFIGURATION:
		{
			GwtFileSyncAppConfiguration fileSyncAppConfiguration; 

			fileSyncAppConfiguration = GwtServerHelper.getFileSyncAppConfiguration( this );
			response = new VibeRpcResponse( fileSyncAppConfiguration );
			return response;
		}

		case GET_FOLDER:
		{
			GetFolderCmd gfCmd;
			GwtFolder result;
			
			gfCmd = (GetFolderCmd) cmd;
			result = getFolder( ri, gfCmd.getZoneUUId(), gfCmd.getFolderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_GROUP_ASSIGNEE_MEMBERSHIP:
		{
			GetGroupAssigneeMembershipCmd ggamCmd = ((GetGroupAssigneeMembershipCmd) cmd);
			List<AssignmentInfo> results = getGroupAssigneeMembership( ri, ggamCmd.getGroupId() );
			AssignmentInfoListRpcResponseData responseData = new AssignmentInfoListRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_GROUP_MEMBERSHIP:
		{
			String groupId = ((GetGroupMembershipCmd) cmd).getGroupId();
			List<GwtTeamingItem> members = getGroupMembership( ri, groupId );
			GetGroupMembershipRpcResponseData responseData = new GetGroupMembershipRpcResponseData( members );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_GROUP_MEMBERSHIP_TYPE:
		{
			GetGroupMembershipTypeCmd ggmtCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			ggmtCmd = (GetGroupMembershipTypeCmd) cmd;
			result = GwtServerHelper.isGroupMembershipDynamic( this, ggmtCmd.getGroupId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_GROUPS:
		{
			GetGroupsCmd ggCmd;
			List<GroupInfo> result;
			GetGroupsRpcResponseData responseData;
			
			ggCmd = (GetGroupsCmd) cmd;
			result = getGroups( ri, ggCmd.getBinderId() );
			responseData = new GetGroupsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_HORIZONTAL_NODE:
		{
			GetHorizontalNodeCmd ghnCmd;
			TreeInfo result;
			
			ghnCmd = (GetHorizontalNodeCmd) cmd;
			result = getHorizontalNode( ri, ghnCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}

		case GET_HORIZONTAL_TREE:
		{
			GetHorizontalTreeCmd ghtCmd;
			List<TreeInfo> result;
			GetHorizontalTreeRpcResponseData responseData;
			
			ghtCmd = (GetHorizontalTreeCmd) cmd;
			result = getHorizontalTree( ri, ghtCmd.getBinderId() );
			responseData = new GetHorizontalTreeRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_IM_URL:
		{
			GetImUrlCmd giuCmd;
			String result;
			StringRpcResponseData responseData;
			
			giuCmd = (GetImUrlCmd) cmd;
			result = getImUrl( ri, giuCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_IS_DYNAMIC_GROUP_MEMBERSHIP_ALLOWED:
		{
			boolean isAllowed;
			BooleanRpcResponseData responseData;
			
			isAllowed = GwtServerHelper.isDynamicGroupMembershipAllowed( this );
			responseData = new BooleanRpcResponseData( isAllowed );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_LOGIN_INFO:
		{
			GwtLoginInfo loginInfo;
			
			loginInfo = getLoginInfo( ri );
			response = new VibeRpcResponse( loginInfo );
			return response;
		}
		
		case GET_MICRO_BLOG_URL:
		{
			GetMicroBlogUrlCmd gmbuCmd;
			String result;
			StringRpcResponseData responseData;
			
			gmbuCmd = (GetMicroBlogUrlCmd) cmd;
			result = getMicrBlogUrl( ri, gmbuCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_MODIFY_BINDER_URL:
		{
			GetModifyBinderUrlCmd gmbuCmd;
			String url;
			StringRpcResponseData responseData;
			
			gmbuCmd = (GetModifyBinderUrlCmd) cmd;
			url = getModifyBinderUrl( ri, gmbuCmd.getBinderId() );
			responseData = new StringRpcResponseData( url );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_MY_TEAMS:
		{
			GetMyTeamsRpcResponseData responseData;
			List<TeamInfo> result;
			
			result = getMyTeams( ri );
			responseData = new GetMyTeamsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_NUMBER_OF_MEMBERS:
		{
			GetNumberOfMembersCmd gnmCmd;
			IntegerRpcResponseData responseData;
			int numMembers;
			
			gnmCmd = (GetNumberOfMembersCmd) cmd;
			numMembers = GwtServerHelper.getNumberOfMembers( this, gnmCmd.getGroupId() );
			responseData = new IntegerRpcResponseData( numMembers );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_PERSONAL_PREFERENCES:
		{
			GwtPersonalPreferences prefs;
			
			prefs = getPersonalPreferences( ri );
			response = new VibeRpcResponse( prefs );
			return response;
		}
		
		case GET_PRESENCE_INFO:
		{
			GetPresenceInfoCmd gpiCmd;
			GwtPresenceInfo result;
			
			gpiCmd = (GetPresenceInfoCmd) cmd;
			result = getPresenceInfo( ri, gpiCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_PROFILE_AVATARS:
		{
			GetProfileAvatarsCmd gpaCmd;
			ProfileAttribute result;
			
			gpaCmd = (GetProfileAvatarsCmd) cmd;
			result = getProfileAvatars( ri, gpaCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_PROFILE_INFO:
		{
			GetProfileInfoCmd gpiCmd;
			ProfileInfo result;
			
			gpiCmd = (GetProfileInfoCmd) cmd;
			result = getProfileInfo( ri, gpiCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_PROFILE_STATS:
		{
			GetProfileStatsCmd gpsCmd;
			ProfileStats result;
			
			gpsCmd = (GetProfileStatsCmd) cmd;
			result = getProfileStats( ri, gpsCmd.getBinderId(), gpsCmd.getUserId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_QUICK_VIEW_INFO:
		{
			GetQuickViewInfoCmd gqviCmd;
			ProfileInfo result;
			
			gqviCmd = (GetQuickViewInfoCmd) cmd;
			result = getQuickViewInfo( ri, gqviCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}

		case GET_RECENT_PLACES:
		{
			GetRecentPlacesRpcResponseData responseData;
			List<RecentPlaceInfo> result;
			
			result = getRecentPlaces( ri );
			responseData = new GetRecentPlacesRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ROOT_WORKSPACE_ID:
		{
			GetRootWorkspaceIdCmd grwiCmd;
			String result;
			StringRpcResponseData responseData;
			
			grwiCmd = (GetRootWorkspaceIdCmd) cmd;
			result = getRootWorkspaceId( ri, grwiCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_SAVED_SEARCHES:
		{
			GetSavedSearchesRpcResponseData responseData;
			List<SavedSearchInfo> result;
			
			result = getSavedSearches( ri );
			responseData = new GetSavedSearchesRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case GET_SITE_ADMIN_URL:
		{
			String url;
			String binderId;
			StringRpcResponseData responseData;
			
			binderId = ((GetSiteAdminUrlCmd) cmd).getBinderId();
			url = getSiteAdministrationUrl( ri, binderId );
			responseData = new StringRpcResponseData( url );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_SITE_BRANDING:
		{
			GwtBrandingData brandingData;
			
			brandingData = getSiteBrandingData( ri );
			response = new VibeRpcResponse( brandingData );
			return response;
		}
		
		case GET_SUBSCRIPTION_DATA:
		{
			GetSubscriptionDataCmd gsdCmd = ((GetSubscriptionDataCmd) cmd);
			SubscriptionData result = getSubscriptionData( ri, gsdCmd.getEntryId() );
			response = new VibeRpcResponse( new SubscriptionDataRpcResponseData( result ));
			return response;
		}
		
		case GET_TAG_RIGHTS_FOR_BINDER:
		{
			GetTagRightsForBinderCmd gtrfbCmd;
			ArrayList<Boolean> result;
			GetTagRightsRpcResponseData responseData;
			
			gtrfbCmd = (GetTagRightsForBinderCmd) cmd;
			result = getTagRightsForBinder( ri, gtrfbCmd.getBinderId() );
			responseData = new GetTagRightsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TAG_RIGHTS_FOR_ENTRY:
		{
			GetTagRightsForEntryCmd gtrfeCmd;
			ArrayList<Boolean> result;
			GetTagRightsRpcResponseData responseData;
			
			gtrfeCmd = (GetTagRightsForEntryCmd) cmd;
			result = getTagRightsForEntry( ri, gtrfeCmd.getEntryId() );
			responseData = new GetTagRightsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TAG_SORT_ORDER:
		{
			TagSortOrder result;
			
			result = getTagSortOrder( ri );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_TASK_BUNDLE:
		{
			GetTaskBundleCmd gtbCmd = ((GetTaskBundleCmd) cmd);
			TaskBundle results = getTaskBundle( ri, gtbCmd.getBinderId(), gtbCmd.getFilterType(), gtbCmd.getModeType() );
			TaskBundleRpcResponseData responseData = new TaskBundleRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TASK_LINKAGE:
		{
			GetTaskLinkageCmd gtlCmd = ((GetTaskLinkageCmd) cmd);
			TaskLinkage results = getTaskLinkage( ri, gtlCmd.getBinderId() );
			TaskLinkageRpcResponseData responseData = new TaskLinkageRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TASK_LIST:
		{
			GetTaskListCmd gtlCmd = ((GetTaskListCmd) cmd);
			List<TaskListItem> results = getTaskList( ri, gtlCmd.getBinderId(), gtlCmd.getFilterType(), gtlCmd.getModeType() );
			TaskListItemListRpcResponseData responseData = new TaskListItemListRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TEAM_ASSIGNEE_MEMBERSHIP:
		{
			GetTeamAssigneeMembershipCmd gtamCmd = ((GetTeamAssigneeMembershipCmd) cmd);
			List<AssignmentInfo> results = getTeamAssigneeMembership( ri, gtamCmd.getBinderId() );
			AssignmentInfoListRpcResponseData responseData = new AssignmentInfoListRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TEAM_MANAGEMENT_INFO:
		{
			GetTeamManagementInfoCmd gtmiCmd;
			TeamManagementInfo result;
			
			gtmiCmd = (GetTeamManagementInfoCmd) cmd;
			result = getTeamManagementInfo( ri, gtmiCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_TEAMS:
		{
			GetTeamsCmd gtCmd;
			List<TeamInfo> result;
			GetMyTeamsRpcResponseData responseData;
			
			gtCmd = (GetTeamsCmd) cmd;
			result = getTeams( ri, gtCmd.getBinderId() );
			responseData = new GetMyTeamsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		case GET_TOOLBAR_ITEMS:
		{
			GetToolbarItemsCmd gtiCmd;
			List<ToolbarItem> result;
			GetToolbarItemsRpcResponseData responseData;
			
			gtiCmd = (GetToolbarItemsCmd) cmd;
			result = getToolbarItems( ri, gtiCmd.getBinderId() );
			responseData = new GetToolbarItemsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TOP_RANKED:
		{
			List<TopRankedInfo> result;
			GetTopRankedRpcResponseData responseData;
			
			result = getTopRanked( ri );
			responseData = new GetTopRankedRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_UPGRADE_INFO:
		{
			GwtUpgradeInfo upgradeInfo;
			
			upgradeInfo = getUpgradeInfo( ri );
			response = new VibeRpcResponse( upgradeInfo );
			return response;
		}
		
		case GET_USER_PERMALINK:
		{
			GetUserPermalinkCmd gupCmd;
			String permalink;
			StringRpcResponseData responseData;
			
			gupCmd = (GetUserPermalinkCmd) cmd;
			permalink = getUserPermalink( ri, gupCmd.getUserId() );
			responseData = new StringRpcResponseData( permalink );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_USER_STATUS:
		{
			GetUserStatusCmd gusCmd;
			UserStatus result;
			
			gusCmd = (GetUserStatusCmd) cmd;
			result = getUserStatus( ri, gusCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_VERTICAL_ACTIVITY_STREAMS_TREE:
		{
			GetVerticalActivityStreamsTreeCmd gvastCmd;
			TreeInfo result;
			
			gvastCmd = (GetVerticalActivityStreamsTreeCmd) cmd;
			result = getVerticalActivityStreamsTree( ri, gvastCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_VERTICAL_NODE:
		{
			GetVerticalNodeCmd gvnCmd;
			TreeInfo result;
			
			gvnCmd = (GetVerticalNodeCmd) cmd;
			result = getVerticalNode( ri, gvnCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_VERTICAL_TREE:
		{
			GetVerticalTreeCmd gvtCmd;
			TreeInfo result;
			
			gvtCmd = (GetVerticalTreeCmd) cmd;
			result = getVerticalTree( ri, gvtCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_VIEW_FOLDER_ENTRY_URL:
		{
			Long binderId;
			Long entryId;
			String result;
			StringRpcResponseData responseData;
			
			binderId = ((GetViewFolderEntryUrlCmd) cmd).getBinderId();
			entryId = ((GetViewFolderEntryUrlCmd) cmd).getEntryId();
			result = getViewFolderEntryUrl( ri, binderId, entryId );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case HAS_ACTIVITY_STREAM_CHANGED:
		{
			ActivityStreamInfo asi;
			Boolean result;
			
			asi = ((HasActivityStreamChangedCmd) cmd).getActivityStreamInfo();
			result = hasActivityStreamChanged( ri, asi );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}

		case IS_ALL_USERS_GROUP:
		{
			String groupId = ((IsAllUsersGroupCmd) cmd).getGroupId();
			Boolean result = isAllUsersGroup( ri, groupId );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}

		case IS_PERSON_TRACKED:
		{
			IsPersonTrackedCmd iptCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			iptCmd = (IsPersonTrackedCmd) cmd;
			result = isPersonTracked( ri, iptCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case IS_SEEN:
		{
			Long entryId = ((IsSeenCmd) cmd).getEntryId();
			Boolean result = isSeen( ri, entryId );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case MARKUP_STRING_REPLACEMENT:
		{
			MarkupStringReplacementCmd msr = ((MarkupStringReplacementCmd) cmd); 
			String binderId = msr.getBinderId();
			String html     = msr.getHtml();
			String type     = msr.getType();
			
			String newHtml = markupStringReplacement( ri, binderId, html, type );
			StringRpcResponseData responseData = new StringRpcResponseData( newHtml );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case MODIFY_GROUP:
		{
			ModifyGroupCmd mgCmd;
			
			mgCmd = (ModifyGroupCmd) cmd;
			GwtServerHelper.modifyGroup( this, mgCmd.getId(), mgCmd.getTitle(), mgCmd.getDesc(), mgCmd.getIsMembershipDynamic(), mgCmd.getMembership(), mgCmd.getMembershipCriteria() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( Boolean.TRUE ) );
			
			return response;
		}
		
		case PERSIST_ACTIVITY_STREAM_SELECTION:
		{
			ActivityStreamInfo asi;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			asi = ((PersistActivityStreamSelectionCmd) cmd).getActivityStreamInfo();
			result = persistActivityStreamSelection( ri, asi );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PERSIST_NODE_COLLAPSE:
		{
			PersistNodeCollapseCmd pncCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			pncCmd = (PersistNodeCollapseCmd) cmd;
			result = persistNodeCollapse( ri, pncCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PERSIST_NODE_EXPAND:
		{
			PersistNodeExpandCmd pneCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			pneCmd = (PersistNodeExpandCmd) cmd;
			result = persistNodeExpand( ri,pneCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PURGE_TASKS:
		{
			PurgeTasksCmd dtCmd = ((PurgeTasksCmd) cmd);
			Boolean result = purgeTasks( ri, dtCmd.getTaskIds() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case REMOVE_EXTENSION:
		{
			RemoveExtensionCmd reCmd;
			ExtensionInfoClient[] result;
			RemoveExtensionRpcResponseData responseData;
			
			reCmd = (RemoveExtensionCmd) cmd;
			try
			{
				result = removeExtension( ri, reCmd.getId() );
			}
			catch (Exception ex)
			{
				GwtTeamingException gtEx;
				
				gtEx = GwtServerHelper.getGwtTeamingException( ex );
				throw gtEx;				
			}

			responseData = new RemoveExtensionRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case REMOVE_FAVORITE:
		{
			RemoveFavoriteCmd rfCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			rfCmd = (RemoveFavoriteCmd) cmd;
			result = removeFavorite( ri, rfCmd.getFavoriteId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case REMOVE_SAVED_SEARCH:
		{
			RemoveSavedSearchCmd rssCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			rssCmd = (RemoveSavedSearchCmd) cmd;
			result = removeSavedSearch( ri, rssCmd.getSavedSearchInfo() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case REMOVE_TASK_LINKAGE:
		{
			RemoveTaskLinkageCmd rtlCmd = ((RemoveTaskLinkageCmd) cmd);
			Boolean result = removeTaskLinkage( ri, rtlCmd.getBinderId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case REPLY_TO_ENTRY:
		{
			ReplyToEntryCmd reCmd = ((ReplyToEntryCmd) cmd);
			ActivityStreamEntry result = replyToEntry(
					ri, reCmd.getEntryId(), reCmd.getTitle(), reCmd.getDescription() );
			ActivityStreamEntryRpcResponseData responseData = new ActivityStreamEntryRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_BRANDING:
		{
			BooleanRpcResponseData responseData;
			Boolean result;
			SaveBrandingCmd cmd2;
			
			cmd2 = (SaveBrandingCmd) cmd; 
			result = saveBrandingData( ri, cmd2.getBinderId(), cmd2.getBrandingData() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_FILE_SYNC_APP_CONFIGURATION:
		{
			SaveFileSyncAppConfigurationCmd sfsacCmd;
			Boolean result;
			
			sfsacCmd = ((SaveFileSyncAppConfigurationCmd) cmd);
			result = GwtServerHelper.saveFileSyncAppConfiguration( this, sfsacCmd.getFileSyncAppConfiguration() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SAVE_SUBSCRIPTION_DATA:
		{
			SaveSubscriptionDataCmd ssdCmd = ((SaveSubscriptionDataCmd) cmd);
			Boolean result = saveSubscriptionData( ri, ssdCmd.getEntryId(), ssdCmd.getSubscriptionData() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_COMPLETED:
		{
			SaveTaskCompletedCmd stcCmd = ((SaveTaskCompletedCmd) cmd);
			String result = saveTaskCompleted( ri, stcCmd.getTaskIds(), stcCmd.getCompleted() );
			response = new VibeRpcResponse( new StringRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_DUE_DATE:
		{
			SaveTaskDueDateCmd stddCmd = ((SaveTaskDueDateCmd) cmd);
			TaskEvent result = saveTaskDueDate( ri, stddCmd.getTaskId(), stddCmd.getDueDate() );
			response = new VibeRpcResponse( new TaskEventRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_LINKAGE:
		{
			SaveTaskLinkageCmd stlCmd = ((SaveTaskLinkageCmd) cmd);
			Boolean result = saveTaskLinkage( ri, stlCmd.getBinderId(), stlCmd.getLinkage() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_PRIORITY:
		{
			SaveTaskPriorityCmd stpCmd = ((SaveTaskPriorityCmd) cmd);
			Boolean result = saveTaskPriority( ri, stpCmd.getBinderId(), stpCmd.getEntryId(), stpCmd.getPriority() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_SORT:
		{
			SaveTaskSortCmd stsCmd = ((SaveTaskSortCmd) cmd);
			Boolean result = saveTaskSort( ri, stsCmd.getBinderId(), stsCmd.getSortKey(), stsCmd.getSortAscending() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_STATUS:
		{
			SaveTaskStatusCmd stsCmd = ((SaveTaskStatusCmd) cmd);
			String result = saveTaskStatus( ri, stsCmd.getTaskIds(), stsCmd.getStatus() );
			response = new VibeRpcResponse( new StringRpcResponseData( result ));
			return response;
		}
		
		case SAVE_PERSONAL_PREFERENCES:
		{
			BooleanRpcResponseData responseData;
			Boolean result;
			
			result = savePersonalPreferences( ri, ((SavePersonalPrefsCmd) cmd).getPersonalPrefs() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_SEARCH:
		{
			SaveSearchCmd ssCmd;
			SavedSearchInfo result;
			
			ssCmd = (SaveSearchCmd) cmd;
			result = saveSearch( ri, ssCmd.getSearchTabId(), ssCmd.getSavedSearchInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_TAG_SORT_ORDER:
		{
			SaveTagSortOrderCmd stsoCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			stsoCmd = (SaveTagSortOrderCmd) cmd;
			result = saveTagSortOrder( ri, stsoCmd.getSortOrder() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_USER_STATUS:
		{
			SaveUserStatusCmd susCmd;
			SaveUserStatusRpcResponseData responseData;
			
			susCmd = ((SaveUserStatusCmd) cmd);
			responseData = saveUserStatus( ri, susCmd.getStatus() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_WHATS_NEW_SETTINGS:
		{
			BooleanRpcResponseData responseData;
			Boolean result;
			
			result = saveWhatsNewShowSetting( ri, ((SaveWhatsNewSettingsCmd) cmd).getSettings() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case SET_SEEN:
		{
			List<Long> entryIds = ((SetSeenCmd) cmd).getEntryIds();
			Boolean result = setSeen( ri, entryIds );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SET_UNSEEN:
		{
			List<Long> entryIds = ((SetUnseenCmd) cmd).getEntryIds();
			Boolean result = setUnseen( ri, entryIds );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SHARE_ENTRY:
		{
			ShareEntryCmd seCmd = ((ShareEntryCmd) cmd);
			GwtShareEntryResults results = shareEntry(
					ri, seCmd.getEntryId(), seCmd.getComment(), seCmd.getPrincipalIds(), seCmd.getTeamIds() );
			ShareEntryResultsRpcResponseData responseData = new ShareEntryResultsRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TEST_GROUP_MEMBERSHIP_LDAP_QUERY:
		{
			TestGroupMembershipCriteriaCmd tgmlqCmd;
			Integer count;
			IntegerRpcResponseData responseData;
			
			// Execute the ldap query and see how many users/groups are found
			tgmlqCmd = (TestGroupMembershipCriteriaCmd) cmd;
			count = GwtServerHelper.testGroupMembershipCriteria( this, tgmlqCmd.getMembershipCriteria() );
			responseData = new IntegerRpcResponseData( count );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TRACK_BINDER:
		{
			TrackBinderCmd tbCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			tbCmd = (TrackBinderCmd) cmd;
			result = trackBinder( ri, tbCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case UPDATE_CALCULATED_DATES:
		{
			UpdateCalculatedDatesCmd ucdCmd = ((UpdateCalculatedDatesCmd) cmd);
			Map<Long, TaskDate> results = updateCalculatedDates( ri, ucdCmd.getBinderId(), ucdCmd.getEntryId() );
			UpdateCalculatedDatesRpcResponseData responseData = new UpdateCalculatedDatesRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UPDATE_BINDER_TAGS:
		{
			UpdateBinderTagsCmd ubtCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			ubtCmd = (UpdateBinderTagsCmd) cmd;
			result = updateBinderTags( ri, ubtCmd.getBinderId(), ubtCmd.getToBeDeleted(), ubtCmd.getToBeAdded() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UPDATE_ENTRY_TAGS:
		{
			UpdateEntryTagsCmd uetCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			uetCmd = (UpdateEntryTagsCmd) cmd;
			result = updateEntryTags( ri, uetCmd.getEntryId(), uetCmd.getToBeDeleted(), uetCmd.getToBeAdded() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UPDATE_FAVORITES:
		{
			UpdateFavoritesCmd ufCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			ufCmd = (UpdateFavoritesCmd) cmd;
			result = updateFavorites( ri, ufCmd.getFavoritesList() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UNTRACK_BINDER:
		{
			UntrackBinderCmd ubCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			ubCmd = (UntrackBinderCmd) cmd;
			result = untrackBinder( ri, ubCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UNTRACK_PERSON:
		{
			UntrackPersonCmd upCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			upCmd = (UntrackPersonCmd) cmd;
			result = untrackPerson( ri, upCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case VALIDATE_ENTRY_EVENTS:
		{
			ValidateEntryEventsCmd veaCmd  = ((ValidateEntryEventsCmd) cmd);
			String entryId = veaCmd.getEntryId();
			List<EventValidation> eventValidations = veaCmd.getEventsToBeValidated();
			List<EventValidation> results = validateEntryEvents( ri, eventValidations, entryId );
			EventValidationListRpcResponseData responseData = new EventValidationListRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		}
		
		m_logger.warn( "In GwtRpcServiceImpl.executeCommand(), unknown command: " + cmdEnum.name() + " (" +cmd.getClass().getName() + ")" );
		return null;
	}
	
	
	/**
	 * Execute a search based on the given search criteria.
	 * 
	 * @param ri
	 * @param searchCriteria
	 * 
	 * @return
	 * 
	 * @throws Exception 
	 */
	private GwtSearchResults executeSearch( HttpRequestInfo ri, GwtSearchCriteria searchCriteria ) throws Exception
	{
		GwtSearchResults searchResults;
		
		switch ( searchCriteria.getSearchType() )
		{
		case APPLICATION:
		case APPLICATION_GROUP:
		case COMMUNITY_TAGS:
		case ENTRIES:
		case GROUP:
		case PERSON:
		case PERSONAL_TAGS:
		case PLACES:
		case TAG:
		case TEAMS:
		case USER:
			searchResults = doSearch( getRequest( ri ), searchCriteria );
			break;
				
		default:
			//!!! Finish.
			searchResults = null;
			break;
		}
		
		return searchResults;
	}// end executeSearch()
	
	/*
	 * Marks the given task in the given binder as having its subtask
	 * display collapsed.
	 */
	private Boolean collapseSubtasks( HttpRequestInfo ri, Long binderId, Long entryId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.collapseSubtasks()");
		try {
			return GwtTaskHelper.collapseSubtasks( this, binderId, entryId );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.collapseSubtasks()");
		}
	}

	/*
	 * Deletes the specified tasks.
	 */
	private Boolean deleteTasks( HttpRequestInfo ri, List<TaskId> taskIds ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.deleteTasks()");
		try {
			return GwtTaskHelper.deleteTasks( getRequest( ri ), this, taskIds );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.deleteTasks()");
		}
	}

	/*
	 * Marks the given task in the given binder as having its subtask
	 * display expanded.
	 */
	private Boolean expandSubtasks( HttpRequestInfo ri, Long binderId, Long entryId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.expandSubtasks()");
		try {
			return GwtTaskHelper.expandSubtasks( this, binderId, entryId );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.expandSubtasks()");
		}
	}

	/*
	 * Returns a List<AssignmentInfo> containing information about the
	 * membership of a group.
	 */
	private List<AssignmentInfo> getGroupAssigneeMembership( HttpRequestInfo ri, Long groupId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.getGroupMembership()");
		try {
			return GwtTaskHelper.getGroupMembership( this, groupId );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getGroupMembership()");
		}
	}

	/**
	 * Return the rights the user has for modifying tags on the given binder.
	 */
	private ArrayList<Boolean> getTagRightsForBinder( HttpRequestInfo ri, String binderId )
	{
		ArrayList<Boolean> tagRights;
		
		tagRights = new ArrayList<Boolean>();
		
		tagRights.add( 0, canManagePersonalBinderTags( ri, binderId ) );
		tagRights.add( 1, canManagePublicBinderTags( ri, binderId ) );
		
		return tagRights;
	}
	
	/**
	 * Return the rights the user has for modifying personal tags on the given entry.
	 */
	private ArrayList<Boolean> getTagRightsForEntry( HttpRequestInfo ri, String entryId )
	{
		ArrayList<Boolean> tagRights;
		
		tagRights = new ArrayList<Boolean>();
		
		tagRights.add( 0, canManagePersonalEntryTags( ri, entryId ) );
		tagRights.add( 1, canManagePublicEntryTags( ri, entryId ) );
		
		return tagRights;
	}
	
    /**
     * 
     */
    private TagSortOrder getTagSortOrder( HttpRequestInfo ri )
    {
    	UserProperties userProperties;
    	ProfileModule profileModule;
    	TagSortOrder sortOrder;
    	Object value;
    	
    	profileModule = getProfileModule();

    	sortOrder = TagSortOrder.SORT_BY_TYPE_ASCENDING;
    	
    	userProperties = profileModule.getUserProperties( null );
		value = userProperties.getProperty( ObjectKeys.USER_PROPERTY_TAG_SORT_ORDER );
		if ( value != null && value instanceof TagSortOrder )
			sortOrder = (TagSortOrder) value;

    	return sortOrder;
    }
    
    
	/*
	 * Returns a List<AssignmentInfo> containing information about the
	 * membership of a team.
	 */
	private List<AssignmentInfo> getTeamAssigneeMembership( HttpRequestInfo ri, Long binderId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.getTeamMembership()");
		try {
			return GwtTaskHelper.getTeamMembership( this, binderId );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getTeamMembership()");
		}
	}

	/*
	 * Purges the specified tasks.
	 */
	private Boolean purgeTasks( HttpRequestInfo ri, List<TaskId> taskIds ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.purgeTasks()");
		try {
			return GwtTaskHelper.purgeTasks( getRequest( ri ), this, taskIds );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.purgeTasks()");
		}
	}
	
	/*
	 * This method is meant to search for applications or entries or groups or places or tags or teams or users.
	 */
	@SuppressWarnings({"unchecked", "unused"})
	private GwtSearchResults doSearch( HttpServletRequest request, GwtSearchCriteria searchCriteria ) throws Exception
	{
		Map options;
		List searchTerms;
		SearchFilter searchTermFilter;
		String searchText;
		Integer startingCount;
		Integer maxResults;
		GwtSearchResults searchResults = null;
		
		// Make sure we are dealing with the right search type.
		GwtSearchCriteria.SearchType searchType = searchCriteria.getSearchType();
		switch ( searchType )
		{
		case APPLICATION:
		case APPLICATION_GROUP:
		case COMMUNITY_TAGS:
		case ENTRIES:
		case GROUP:
		case PERSON:
		case PERSONAL_TAGS:
		case PLACES:
		case TAG:
		case TEAMS:
		case USER:
			break;
			
		default:
			return null;
		}
		
		maxResults = new Integer( searchCriteria.getMaxResults() );
		startingCount = new Integer( searchCriteria.getPageNumber() ) * maxResults;
		
		options = new HashMap();
		options.put( ObjectKeys.SEARCH_MAX_HITS, maxResults );
		options.put( ObjectKeys.SEARCH_OFFSET, startingCount );
		options.put( ObjectKeys.SEARCH_SORT_BY, Constants.SORT_TITLE_FIELD );
		options.put( ObjectKeys.SEARCH_SORT_DESCEND, new Boolean( false ) );
		
		if ( searchCriteria.getSearchScope() == SearchScope.SEARCH_LOCAL )
		{
			String binderId;
			
			binderId = searchCriteria.getBinderId();
			if ( binderId != null && binderId.length() > 0 )
				options.put( ObjectKeys.SEARCH_ANCESTRY, binderId );;
		}
		
		searchText = searchCriteria.getSearchText();
	    searchText = searchText.replaceAll(" \\*", "\\*").trim();

		searchTermFilter = new SearchFilter();
		
	    // Set up the search filter.
		switch ( searchCriteria.getSearchType() )
		{
		case ENTRIES:
			String binderId = null;
			
			//Add the title term
			if ( searchText.length() > 0 )
				searchTermFilter.addTitleFilter( searchText );

			searchTerms = new ArrayList();
			searchTerms.add( EntityIdentifier.EntityType.folderEntry.name() );
			searchTermFilter.addAndNestedTerms( SearchFilterKeys.FilterTypeEntityTypes, SearchFilterKeys.FilterEntityType, searchTerms );
			searchTermFilter.addAndFilter( SearchFilterKeys.FilterTypeTopEntry );
			
			break;

		case PLACES:
			searchTermFilter.addPlacesFilter( searchText, searchCriteria.getFoldersOnly() );
			break;
			
		case COMMUNITY_TAGS:
		case PERSONAL_TAGS:
		case TAG:
			// this has been replaced by a getTags method in the search engine.
			// searchTermFilter.addTagsFilter( null, searchText );
			break;
		
		case TEAMS:
			searchTermFilter.addTeamFilter( searchText );
			break;

		case PERSON:
		case USER:
			searchTermFilter.addTitleFilter( searchText );
			searchTermFilter.addLoginNameFilter( searchText );
			if ( GwtSearchCriteria.SearchType.PERSON == searchType ) {
				searchTermFilter.addAndPersonFlagFilter( String.valueOf( Boolean.TRUE ) );
			}
			break;
			
		default:
			//Add the login name term
			if ( searchText.length() >0 ) {
				searchTermFilter.addTitleFilter(searchText);
				searchTermFilter.addLoginNameFilter(searchText);
			}
			break;
		}// end switch()

		try
		{
			Map retMap;

			searchResults = new GwtSearchResults();
		
			options.put( ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter() );
			
			// Perform the search based on the search type.
			switch ( searchType )
			{
			case APPLICATION:
				//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case APPLICATION_GROUP:
				//!!! Get code from ajaxFind() in TypeToFindAjaxController.java
				break;
				
			case ENTRIES:
			{
				List placesWithCounters;
				List entries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Map foldersMap;
				Integer count;
				
				retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options );
				entries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				placesWithCounters = BinderHelper.sortPlacesInEntriesSearchResults( getBinderModule(), entries );
				foldersMap = BinderHelper.prepareFolderList( placesWithCounters, false );
				BinderHelper.extendEntriesInfo( entries, foldersMap );
				
				// Add the search results to the GwtSearchResults object.
				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				searchResults.setCountTotal( count.intValue() );
				
				// Create a GwtFolderEntry item for each search result.
				results = new ArrayList( entries.size() );
				it = entries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;
					GwtFolderEntry folderEntry;
					String entryId;
					String entryName;
					String parentBinderName;

					// Get the next entry in the search results.
					entry = (Map) it.next();

					// Pull information about this entry from the search results.
					folderEntry = new GwtFolderEntry();
					entryId = entry.get( "_docId" );
					folderEntry.setEntryId( entryId );
					entryName = entry.get( "title" );
					folderEntry.setEntryName( entryName );
					parentBinderName = entry.get( WebKeys.BINDER_PATH_NAME );
					folderEntry.setParentBinderName( parentBinderName );
					results.add( folderEntry );
				}
				searchResults.setResults( results);
				break;
			}

			case GROUP:
			{
				Map entries;
		    	List searchEntries;
		    	Integer searchHits;
		    	ArrayList<GwtTeamingItem> results = null;
				
				entries = getProfileModule().getGroups( options );
				
				// Do we get any search hits?
		    	searchEntries = (List)entries.get( ObjectKeys.SEARCH_ENTRIES );
		    	searchHits = (Integer)entries.get( ObjectKeys.SEARCH_COUNT_TOTAL );
		    	if ( (null != searchEntries) && (null != searchHits) && (0 < searchHits) )
		    	{
					boolean sendingEmail;
					boolean allowSendToAllUsers;
					int size;

					// Yes
					results = new ArrayList( searchEntries.size() );

					// Is this search a part of sending an email ui?
					// And are we supposed to disallow sending email to the "all users" group?
					sendingEmail = searchCriteria.getIsSendingEmail();
					allowSendToAllUsers = SPropsUtil.getBoolean( "mail.allowSendToAllUsers", false );

					// Create a GwtGroup object for each group returned by the search.
					size = searchEntries.size();
					for (int i = (size - 1); i >= 0; i -= 1)
					{
			    		Map entry;
						String id;
						boolean useGroup;

						useGroup = true;
			    		entry = (Map)searchEntries.get(i);
			    		id = (String)entry.get(Constants.RESERVEDID_FIELD);
						
						// Is this search part of a sending email ui?
						if ( sendingEmail )
						{
							// Is sending an email to all users allowed?
							if ( allowSendToAllUsers == false )
							{
								// No
								// Is this group the "all users" group?
								if ( (null != id) && id.equalsIgnoreCase( ObjectKeys.ALL_USERS_GROUP_INTERNALID ) )
								{
									// Yes, skip it.
									useGroup = false;
									--searchHits;
								}
							}
						}
						
						// Should we add this group to the search results?
						if ( useGroup )
						{
							ProfileModule profileModule;
							List<Long> groupId = new ArrayList<Long>();
							SortedSet<Principal> groupPrincipals;

							id = (String)entry.get( "_docId" );
							groupId.add( Long.valueOf( id ) );
							profileModule = getProfileModule();
							groupPrincipals = profileModule.getPrincipals( groupId );
							
							if ( groupPrincipals.size() > 0  )
							{
								GwtGroup gwtGroup;
								Principal group;
								
								group = groupPrincipals.first();
								
								// Create a GwtGroup item for this group.
								gwtGroup = new GwtGroup();
								gwtGroup.setId( id );
								gwtGroup.setName( group.getName() );
								gwtGroup.setTitle( group.getTitle() );
								
								results.add( gwtGroup );
							}
						}
			    	}
		    	}
						
				searchResults.setCountTotal( searchHits.intValue() );
				searchResults.setResults( results );
				break;
			}
				
			case PLACES:
			{
				List placesEntries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Integer count;
				
				retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options );

				// Add the search results to the GwtSearchResults object.
				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				searchResults.setCountTotal( count.intValue() );
				
				// Create a GwtFolder item for each search result.
				placesEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				results = new ArrayList( placesEntries.size() );
				it = placesEntries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;
					GwtFolder folder;
					String folderId;
					String folderTitle;

					// Get the next folder in the search results.
					entry = (Map) it.next();

					// Pull information about this folder from the search results.
					folderId = entry.get( "_docId" );
					folderTitle = entry.get( "_extendedTitle" );
					folder = getFolderImpl( request, null, folderId, folderTitle );
					if ( folder != null )
						results.add( folder );
				}
				searchResults.setResults( results);
				break;
			}

			case COMMUNITY_TAGS:
			case PERSONAL_TAGS:
			case TAG:
			{
				ArrayList<GwtTeamingItem> results;
				int count;
				int i;
				Iterator it;
				List tags;
				String tagType;
				String searchRoot;
				
				searchRoot = searchText;
				i = searchRoot.indexOf( "*" );
				if ( i > 0 )
				{
					searchRoot = searchRoot.substring( 0, i );
				}
				
				switch ( searchCriteria.getSearchType() )
				{
				default:
				case TAG:             tagType = WebKeys.FIND_TYPE_TAGS;           break;
				case COMMUNITY_TAGS:  tagType = WebKeys.FIND_TYPE_COMMUNITY_TAGS; break;
				case PERSONAL_TAGS:   tagType = WebKeys.FIND_TYPE_PERSONAL_TAGS;  break;
				}
				
				tags = getBinderModule().getSearchTags( searchRoot, tagType );
				count = ((null == tags) ? 0 : tags.size());
				searchResults.setCountTotal( count );
				results = new ArrayList( count );
				for ( it = tags.iterator(); it.hasNext(); )
				{
					GwtTag tag;
					Map<String,String> tagInfo;
					
					tagInfo = (Map) it.next();
					tag = new GwtTag();
					tag.setTagName( tagInfo.get( "ssTag" ));
					results.add( tag );
				}
				searchResults.setResults( results );
				break;
			}
			
			case TEAMS:
			{
				List teamEntries;
				ArrayList<GwtTeamingItem> results;
				Integer count;
				Iterator it;

				// Search for teams
				retMap = getBinderModule().executeSearchQuery( searchTermFilter.getFilter(), options );
				
				// Add the search results to the GwtSearchResults object.
				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				searchResults.setCountTotal( count.intValue() );
				
				// Create a GwtTeam item for each search result.
				teamEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				results = new ArrayList( teamEntries.size() );
				it = teamEntries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;
					String teamId;

					// Get the next team in the search results.
					entry = (Map) it.next();

					// Pull information about this team from the search results.
					teamId = entry.get( "_docId" );
					
					//!!! Finish
				}
				searchResults.setResults( results );
				break;
			}

			case PERSON:
			case USER:
			{
				List userEntries;
				ArrayList<GwtTeamingItem> results;
				Iterator it;
				Integer count;
				
				retMap = getProfileModule().getUsers(options);

				// Add the search results to the GwtSearchResults object.
				count = (Integer) retMap.get( ObjectKeys.SEARCH_COUNT_TOTAL );
				searchResults.setCountTotal( count.intValue() );
				
				// Create a GwtUser item for each search result.
				userEntries = (List)retMap.get( ObjectKeys.SEARCH_ENTRIES );
				results = new ArrayList( userEntries.size() );
				it = userEntries.iterator();
				while ( it.hasNext() )
				{
					Map<String,String> entry;
					GwtUser gwtUser;
					String userId;

					// Get the next user in the search results.
					entry = (Map) it.next();

					// Pull information about this user from the search results.
					userId = entry.get( "_docId" );
					gwtUser = getGwtUser( request, searchType, userId );
					if ( gwtUser != null )
						results.add( gwtUser );
				}
				searchResults.setResults( results);
				break;
			}
				
			default:
				searchResults = null;
			}// end switch()
		}
		catch( AccessControlException e )
		{
			//!!! What to do here?
			searchResults = null;
		}
		
		return searchResults;
	}// end doSearch()
	
	
	/*
	 * Returns a ActivityStreamData of corresponding to activity stream
	 * parameters, paging data and info provided.
	 */
	private ActivityStreamData getActivityStreamData( HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pagingData, ActivityStreamDataType asdt )
	{
		return GwtActivityStreamHelper.getActivityStreamData( getRequest( ri ), this, asp, asi, pagingData, asdt );
	}// end getActivityStreamData()
	
	/**
	 * Returns an ActivityStreamParams object containing information
	 * the current activity stream setup.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	private ActivityStreamParams getActivityStreamParams( HttpRequestInfo ri )
	{
		return GwtActivityStreamHelper.getActivityStreamParams(this);
	}// end getActivityStreamParams()
	
	/**
	 * Returns the current user's default activity stream.  If they
	 * don't have one set in their user profile, null is returned.
	 * 
	 * @param ri
	 * @param currentBinderId
	 * 
	 * @return
	 */
	private ActivityStreamInfo getDefaultActivityStream( HttpRequestInfo ri, String currentBinderId )
	{
		return GwtActivityStreamHelper.getDefaultActivityStream( getRequest( ri ), this, currentBinderId );
	}// end getDefaultActivityStream()
	
	/**
	 * Returns true if the data for an activity stream has changed (or
	 * has never been cached) and false otherwise.
	 * 
	 * @param ri
	 * @param asi
	 * 
	 * @return
	 */
	private Boolean hasActivityStreamChanged( HttpRequestInfo ri, ActivityStreamInfo asi )
	{
		return GwtActivityStreamHelper.hasActivityStreamChanged( getRequest( ri ), this, asi );
	}// end hasActivityStreamChanged()
	
	/**
	 * Stores an ActivityStreamIn as the current user's default
	 * activity stream in their user profile.
	 * 
	 * @param ri
	 * @param asi
	 * 
	 * @return
	 */
	private Boolean persistActivityStreamSelection( HttpRequestInfo ri, ActivityStreamInfo asi )
	{
		return GwtActivityStreamHelper.persistActivityStreamSelection( getRequest( ri ), this, asi );
	}// end persistActivityStreamSelection()
	
	/**
	 * Return the "document base url" that is used in tinyMCE configuration
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private String getDocumentBaseUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		String baseUrl = null;
		BinderModule binderModule;
		Binder binder;
		Long binderIdL;
		
		try
		{
			binderModule = getBinderModule();
	
			binderIdL = new Long( binderId );
			
			if ( binderIdL != null )
			{
				String webPath;
				
				binder = binderModule.getBinder( binderIdL );
				webPath = WebUrlUtil.getServletRootURL( getRequest( ri ) );
				baseUrl = WebUrlUtil.getFileUrl( webPath, WebKeys.ACTION_READ_FILE, binder, "" );
			}
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
		
		return baseUrl;
	}// end getDocumentBaseUrl()
	
	
	/**
	 * Return an Entry object for the given zone and entry id
	 * 
	 * @param ri
	 * @param zoneUUID
	 * @param entryId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private GwtFolderEntry getEntry( HttpRequestInfo ri, String zoneUUID, String entryId ) throws GwtTeamingException
	{
		FolderModule folderModule;
		FolderEntry entry = null;
		GwtFolderEntry folderEntry = null;
		Binder parentBinder;
		
		try
		{
			ZoneInfo zoneInfo;
			String zoneInfoId;
			Long entryIdL;

			// Get the id of the zone we are running in.
			zoneInfo = MiscUtil.getCurrentZone();
			zoneInfoId = zoneInfo.getId();
			if ( zoneInfoId == null )
				zoneInfoId = "";

			folderModule = getFolderModule();

			entryIdL = new Long( entryId );
			
			// Are we looking for an entry that was imported from another zone?
			if ( zoneUUID != null && zoneUUID.length() > 0 && !zoneInfoId.equals( zoneUUID ) )
			{
				// Yes, get the entry id for the entry in this zone.
				entryIdL = folderModule.getZoneEntryId( entryIdL, zoneUUID );
			}

			// Get the entry object.
			if ( entryIdL != null )
				entry = folderModule.getEntry( null, entryIdL );
			
			// Initialize the data members of the GwtFolderEntry object.
			folderEntry = new GwtFolderEntry();
			if ( entryIdL != null )
				folderEntry.setEntryId( entryIdL.toString() );
			if ( entry != null )
			{
				Long parentBinderId;
				String url;
				
				folderEntry.setEntryName( entry.getTitle() );
			
				parentBinder = entry.getParentBinder();
				if ( parentBinder != null )
				{
					parentBinderId = parentBinder.getId();
					folderEntry.setParentBinderName( parentBinder.getPathName() );
					folderEntry.setParentBinderId( parentBinderId );
				}
				
				// Create a url that can be used to view this entry.
				url = PermaLinkUtil.getPermalink( getRequest( ri ), entry );
				folderEntry.setViewEntryUrl( url );
			}
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
		
		
		return folderEntry;
	}// end getEntry()
	
	
	/**
	 * Return a base view folder entry URL that can be used directly in the
	 * content panel.
	 * 
	 * @param ri
	 * @param binderId
	 * @param entryId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	private String getViewFolderEntryUrl( HttpRequestInfo ri, Long binderId, Long entryId ) throws GwtTeamingException {
		try {
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL( getRequest( ri ), "ss_forum", true );
			adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_ENTRY );
			if ( binderId == null )
			{
				FolderEntry entry;
				
				entry = getFolderModule().getEntry( null, entryId );
				binderId = entry.getParentBinder().getId();
			}
			adapterUrl.setParameter( WebKeys.URL_BINDER_ID, String.valueOf( binderId ) );
			adapterUrl.setParameter( WebKeys.URL_ENTRY_ID,  String.valueOf( entryId  ) );			
			return adapterUrl.toString();
		}
		catch ( Exception ex )
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}		
	}
	
	/**
	 * Return a list of tags associated with the given entry.
	 */
	private ArrayList<TagInfo> getEntryTags( HttpRequestInfo ri, String entryId )
	{
		return GwtServerHelper.getEntryTags( this, entryId );
	}
	
	
	/**
	 * Return a list of the names of the files that are attachments for the given binder
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private ArrayList<String> getFileAttachments( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		ArrayList<String> fileNames;
		
		fileNames = new ArrayList<String>();

		try
		{
			Long binderIdL;
			BinderModule binderModule;
			Binder binder = null;
			SortedSet<FileAttachment> attachments;

			binderModule = getBinderModule();

			// Get the binder object.
			binderIdL = new Long( binderId );
			binder = binderModule.getBinder( binderIdL );
			
			attachments = binder.getFileAttachments();
	        for(FileAttachment fileAttachment : attachments)
	        {
	        	String fileName;
	    		FileItem fileItem;
	        	
	           	fileItem = fileAttachment.getFileItem();
				fileName = fileItem.getName();
				fileNames.add( fileName );
			}// end for()
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}

        return fileNames;
	}// end getFileAttachments()
	
	
	/**
	 * Return a Folder object for the given folder id.
	 * 
	 * @param ri
	 * @param zoneUUID
	 * @param folderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private GwtFolder getFolder( HttpRequestInfo ri, String zoneUUID, String folderId ) throws GwtTeamingException
	{
		return getFolderImpl( getRequest( ri ), zoneUUID, folderId, null );
	}
	
	private GwtFolder getFolderImpl( HttpServletRequest request, String zoneUUID, String folderId, String folderTitle ) throws GwtTeamingException
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

			binderModule = getBinderModule();

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

				folder.setFolderName( MiscUtil.hasString( folderTitle ) ? folderTitle : binder.getTitle() );
			
				parentBinder = binder.getParentBinder();
				if ( parentBinder != null )
					folder.setParentBinderName( parentBinder.getPathName() );

				// Create a url that can be used to view this folder.
				url = PermaLinkUtil.getPermalink( request, binder );
				folder.setViewFolderUrl( url );
			}
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
		
		return folder;
	}// end getFolder()
	
	
	/*
	 * Return a GwtUser object for the given user id
	 */
	private GwtUser getGwtUser( HttpServletRequest request, GwtSearchCriteria.SearchType searchType, String userId ) throws GwtTeamingException
	{
		Binder binder = null;
		BinderModule bm = getBinderModule();
		GwtUser reply = null;
		ProfileModule pm = getProfileModule();
		User user = null;
		
		try
		{
			Long userIdL;

			// Do we have an ID we can access as a person?
			userIdL = new Long( userId );
			if ( userIdL != null )
			{
				ArrayList<Long> userAL;
				Set<User> userSet;
				User[] users;
				
				userAL = new ArrayList<Long>();
				userAL.add( userIdL );
				userSet = pm.getUsers( userAL );
				users = userSet.toArray( new User[0] );
				if ( 1 <= users.length )
				{
					// If we are searching for a person and this user
					// is not a person...
					user = users[0];
					if ( ( searchType == GwtSearchCriteria.SearchType.PERSON ) && ( ! ( user.isPerson() ) ) )
					{
						// ...ignore it.
						user = null;
					}
					
					else {
						Long wsId;

						// Does this user have a workspace ID?
						wsId = user.getWorkspaceId();
						if ( null != wsId )
						{
							try
							{
								// Yes!  Can we access the workspace?
								binder = bm.getBinder( user.getWorkspaceId() );
							}
							catch ( Exception ex )
							{
								// No!  Simply ignore it as this is a
								// permissible condition if the user
								// performing the search does NOT have
								// access to the workspace in question.
								binder = null;
							}
						}
						
						// Note:  Cases where a user won't have a workspace
						//    ID include special user IDs such as the email
						//    posting agent and others as well as users
						//    that have never logged in.
					}
				}
			}
			
			// Do we have access to a user?
			if ( null != user )
			{
				// Yes!  Construct a GwtUser object for it.
				reply = new GwtUser();
				reply.setUserId( user.getId() );
				reply.setName( user.getName() );
				reply.setTitle( Utils.getUserTitle( user ) );
				reply.setWorkspaceTitle( user.getWSTitle() );
				
				// Do we have access to this user's workspace?
				if ( null == binder ) {
					// No!  Provide a permalink to the user's profile.
					reply.setViewWorkspaceUrl( PermaLinkUtil.getPermalink( request, user ) );
				}
				else {
					// Yes, we have access to this user's workspace!
					// Store the workspace's ID and a permalink to it. 
					reply.setWorkspaceId( binder.getId() );
					reply.setViewWorkspaceUrl( PermaLinkUtil.getPermalink( request, binder ) );
				}
			}
		}
		catch ( Exception e )
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
	
		return reply;
	}// end getGwtUser()
	
	
	/**
	 * Return the "binder permalink" URL.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private String getBinderPermalink( HttpRequestInfo ri, String binderId )
	{
		String reply = "";
		
		if ( binderId != null && binderId.length() > 0 )
		{
			Binder binder = GwtUIHelper.getBinderSafely( getBinderModule(), binderId );
			if (null != binder)
			{
				reply = PermaLinkUtil.getPermalink( getRequest( ri ), binder );
			}
		}
		
		return reply;
	}
	
	/**
	 * Return the "modify binder" URL.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private String getModifyBinderUrl( HttpRequestInfo ri, String binderId )
	{
		AdaptedPortletURL adapterUrl;
		Binder binder;
		Long binderIdL;

		// Create a URL that can be used to modify a binder.
		adapterUrl = new AdaptedPortletURL( getRequest( ri ), "ss_forum", true );
		adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER );
		adapterUrl.setParameter( WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY );
		adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
		
		binderIdL = new Long( binderId );
		binder = getBinderModule().getBinder( binderIdL );
		adapterUrl.setParameter( WebKeys.URL_BINDER_TYPE, binder.getEntityType().name() );
		
		return adapterUrl.toString();
	}// end getModifyBinderUrl()
	
	/*
	 * Returns the HttpServletRequest from an HttpRequestInfo object.
	 */
	private static HttpServletRequest getRequest(HttpRequestInfo ri) {
		return ((HttpServletRequest) ri.getRequestObj());
	}// end getRequest()
	
	/**
	 * Return a GwtPersonalPreferences object that holds the personal preferences for the logged in user.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	private GwtPersonalPreferences getPersonalPreferences( HttpRequestInfo ri )
	{
		GwtPersonalPreferences personalPrefs;
		
		personalPrefs = new GwtPersonalPreferences();
		
		try
		{
			User user;
			
			// Is the current user the guest user?
			user = GwtServerHelper.getCurrentUser();
			
			// Are we dealing with the guest user?
			if ( !(ObjectKeys.GUEST_USER_INTERNALID.equals( user.getInternalId() ) ) )
			{
				String displayStyle;
				
				// No
				// Get the user's display style preference
				displayStyle = user.getDisplayStyle();
				personalPrefs.setDisplayStyle( displayStyle );
				
				// Get the tutorial panel state.
				{
					String tutorialPanelState;

					tutorialPanelState = getTutorialPanelState( ri );

					// Is the tutorial panel open?
					if ( tutorialPanelState != null && tutorialPanelState.equalsIgnoreCase( "1" ) )
					{
						// No
						personalPrefs.setShowTutorialPanel( false );
					}
					else
						personalPrefs.setShowTutorialPanel( true );
				}
				
				// Get the number of entries per page that should be displayed when a folder is selected.
				{
					UserProperties userProperties;
					String value;
					Integer numEntriesPerPage = Integer.valueOf(SPropsUtil.getString("folder.records.listed"));
					
					userProperties = getProfileModule().getUserProperties( user.getId() );
					value = (String) userProperties.getProperty( ObjectKeys.PAGE_ENTRIES_PER_PAGE );
					if ( value != null && value.length() > 0 )
					{
						try
						{
							numEntriesPerPage = Integer.parseInt( value );
						}
						catch (NumberFormatException nfe)
						{
							m_logger.warn( "In GwtRpcServiceImpl.getPersonalPreferences(), num entries per page is not an integer." );
						}
					}
					
					personalPrefs.setNumEntriesPerPage( numEntriesPerPage );
				}
				
				// Set the flag that indicates whether "editor overrides" are supported.
				personalPrefs.setEditorOverrideSupported( SsfsUtil.supportAttachmentEdit() );
			}
			else
			{
				m_logger.warn( "GwtRpcServiceImpl.getPersonalPreferences(), user is guest." );
			}
		}
		catch (AccessControlException acEx)
		{
			// Nothing to do
			m_logger.warn( "GwtRpcServiceImpl.getPersonalPreferences() AccessControlException" );
		}
		catch (Exception e)
		{
			// Nothing to do
			m_logger.warn( "GwtRpcServiceImpl.getPersonalPreferences() unknown exception" );
		}
		
		return personalPrefs;
	}// end getPersonalPreferences()
	
	
	/**
	 * Return the URL needed to invoke the "site administration" page.  If the user does not
	 * have rights to run the "site administration" page we will throw an exception.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private String getSiteAdministrationUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		// Does the user have rights to run the "site administration" page?
		if ( getAdminModule().testAccess( AdminOperation.manageFunction ) )
		{
			AdaptedPortletURL adapterUrl;
			
			// Yes
			adapterUrl = new AdaptedPortletURL( getRequest( ri ), "ss_forum", true );
			adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_SITE_ADMINISTRATION );
			adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
			
			return adapterUrl.toString();
		}
		
		GwtTeamingException ex;
		
		ex = GwtServerHelper.getGwtTeamingException();
		ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
		throw ex;
	}// end getSiteAdministrationUrl()
	
	/**
	 * Return the URL needed to invoke the start/schedule meeting dialog.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	private String getAddMeetingUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		AdaptedPortletURL adapterUrl;

		// ...store the team meeting URL.
		adapterUrl = new AdaptedPortletURL( getRequest(ri), "ss_forum", true );
		adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_MEETING );
		adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );

		if (GwtServerHelper.getWorkspaceType(GwtUIHelper.getBinderSafely(getBinderModule(), binderId)) == WorkspaceType.USER) {
			// This is a User Workspace so add the owner in and don't append team members
			Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
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
	}// end getAddMeetingUrl()
	
	/**
	 * Return a GwtBrandingData object for the home workspace.
	 * 
	 * @param ri
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private GwtBrandingData getSiteBrandingData( HttpRequestInfo ri ) throws GwtTeamingException
	{
		Binder topWorkspace;
		GwtBrandingData brandingData;
		
		try
		{
			String binderId;
			
			// Get the top workspace.
			topWorkspace = getWorkspaceModule().getTopWorkspace();				
		
			// Get the branding data from the top workspace.
			binderId = topWorkspace.getId().toString();
			brandingData = GwtServerHelper.getBinderBrandingData( this, binderId, getRequest( ri ) );
		}
		catch (Exception e)
		{
			brandingData = new GwtBrandingData();
		}

		brandingData.setIsSiteBranding( true );

		return brandingData;
	}// end getSiteBrandingData()

	/*
	 * Reads the task information from the specified binder.
	 */
	private List<TaskListItem> getTaskList( HttpRequestInfo ri, Long binderId, String filterType, String modeType ) throws GwtTeamingException
	{
		SimpleProfiler.start("GwtRpcServiceImpl.getTaskList()");
		try {
			return GwtTaskHelper.getTaskList( getRequest( ri ), this, GwtTaskHelper.getTaskBinder( this, binderId ), filterType, modeType );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getTaskList()");
		}
	}// end getTaskList()

	/*
	 * Returns a TaskBundle object for the specified binder.
	 */
	private TaskBundle getTaskBundle( HttpRequestInfo ri, Long binderId, String filterType, String modeType ) throws GwtTeamingException
	{
		SimpleProfiler.start("GwtRpcServiceImpl.getTaskBundle()");
		try {
			return
				GwtTaskHelper.getTaskBundle(
					getRequest( ri ),
					this,
					GwtTaskHelper.getTaskBinder( this, binderId ),
					filterType,
					modeType );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getTaskBundle()");
		}
	}// end getTaskBundle()

	/*
	 * Returns a TaskLinkage object for the specified binder.
	 */
	private TaskLinkage getTaskLinkage( HttpRequestInfo ri, Long binderId ) throws GwtTeamingException
	{
		SimpleProfiler.start("GwtRpcServiceImpl.getTaskLinkage()");
		try {
			return GwtTaskHelper.getTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, binderId ) );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getTaskLinkage()");
		}
	}// end getTaskLinkage()

	/*
	 * Removes the TaskLinkage object from the specified binder.
	 */
	private Boolean removeTaskLinkage( HttpRequestInfo ri, Long binderId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.removeTaskLinkage()");
		try {
			return GwtTaskHelper.removeTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, binderId ) );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.removeTaskLinkage()");
		}
	}// end removeTaskLinkage()

	/**
	 * Save the given tag sort order to the user's properties
	 */
	private Boolean saveTagSortOrder( HttpRequestInfo ri, TagSortOrder sortOrder )
	{
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_TAG_SORT_ORDER, sortOrder );
		
		return Boolean.TRUE;
	}
	
	/*
	 * Stores a completed value on the specified tasks.
	 */
	private String saveTaskCompleted( HttpRequestInfo ri, List<TaskId> taskIds, String completed ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveTaskCompleted()");
		try {
			return GwtTaskHelper.saveTaskCompleted( this, taskIds, completed );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveTaskCompleted()");
		}
	}
	
	/*
	 * Stores a due date on the specified task.
	 */
	private TaskEvent saveTaskDueDate( HttpRequestInfo ri, TaskId taskId, TaskEvent taskEvent ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveTaskDueDate()");
		try {
			return GwtTaskHelper.saveTaskDueDate( this, taskId, taskEvent );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveTaskDueDate()");
		}
	}
	
	/*
	 * Stores a TaskLinkage object on the specified binder.
	 */
	private Boolean saveTaskLinkage( HttpRequestInfo ri, Long binderId, TaskLinkage taskLinkage ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveTaskLinkage()");
		try {
			return GwtTaskHelper.saveTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, binderId ), taskLinkage );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveTaskLinkage()");
		}
	}// end saveTaskLinkage()

	/*
	 * Stores a priority value on the specified task.
	 */
	private Boolean saveTaskPriority( HttpRequestInfo ri, Long binderId, Long entryId, String priority ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveTaskPriority()");
		try {
			return GwtTaskHelper.saveTaskPriority( this, binderId, entryId, priority );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveTaskPriority()");
		}
	}
	
	/*
	 * Save a task folder sort options on the specified binder.
	 */
	private Boolean saveTaskSort( HttpRequestInfo ri, Long binderId, String sortKey, boolean sortAscending ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveTaskSort()");
		try {
			return GwtTaskHelper.saveTaskSort( this, binderId, sortKey, sortAscending );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveTaskSort()");
		}
	}
	
	/*
	 * Stores a status value on the specified tasks.
	 */
	private String saveTaskStatus( HttpRequestInfo ri, List<TaskId> taskIds, String status ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveTaskStatus()");
		try {
			return GwtTaskHelper.saveTaskStatus( this, taskIds, status );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveTaskStatus()");
		}
	}

	/*
	 * Updates the calculated dates on a given task.
	 * 
	 * If the updating required changes to this task or others, the
	 * Map<Long, TaskDate> returned will contain a mapping between the
	 * task IDs and the new calculated end date.  Otherwise, the map
	 * returned will be empty.
	 */
	private Map<Long, TaskDate> updateCalculatedDates( HttpRequestInfo ri, Long binderId, Long entryId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.updateCalculatedDates()");
		try {
			return GwtTaskHelper.updateCalculatedDates( getRequest( ri ), this, GwtTaskHelper.getTaskBinder( this, binderId ), entryId );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.updateCalculatedDates()");
		}
	}

    /**
     * 
	 * @param ri
	 * 
	 * @return
     */
    private String getTutorialPanelState( HttpRequestInfo ri )
    {
    	UserProperties	userProperties;
    	ProfileModule	profileModule;
    	String			tutorialPanelState;
    	
    	profileModule = getProfileModule();

    	userProperties = profileModule.getUserProperties( null );
		tutorialPanelState = (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE );

		// Do we have a tutorial panel state?
		if ( tutorialPanelState == null || tutorialPanelState.length() == 0 )
		{
			// No, default to expanded.
			tutorialPanelState = "2";
		}
		
    	return tutorialPanelState;
    }// end getTutorialPanelState()
    
    
	/**
	 * Return a GwtUpgradeInfo object.
	 *
	 * @param ri
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private GwtUpgradeInfo getUpgradeInfo( HttpRequestInfo ri ) throws GwtTeamingException
	{
		GwtUpgradeInfo upgradeInfo;
		User user;
		
		user = GwtServerHelper.getCurrentUser();

		upgradeInfo = new GwtUpgradeInfo();
		
		// Get the Teaming version and build information
		upgradeInfo.setReleaseInfo( ReleaseInfo.getLocalizedReleaseInfo( GwtServerHelper.getCurrentUser().getLocale() ) );
		
		// Identify any upgrade tasks that may need to be performed.
		{
	 		Workspace top = null;
	 		String upgradeVersionCurrent;

	 		try
	 		{
				top = getWorkspaceModule().getTopWorkspace();
	 		}
	 		catch( Exception e )
	 		{
	 		}

	 		// Note:  See AbstractZoneModule.resetZoneUpgradeTasks()
	 		// for the conditions under which this version and the
	 		// admin upgrade task flags get cleared.
	 		if ( top != null )
	 			upgradeVersionCurrent = (String)top.getProperty( ObjectKeys.BINDER_PROPERTY_UPGRADE_VERSION );
	 		else
	 			upgradeVersionCurrent = ObjectKeys.PRODUCT_UPGRADE_VERSION;

 			// Are we dealing with the "admin" user?
 	 		if ( ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() ) )
 	 		{
 	 			// Yes, Were there upgrade tasks to be performed?
 		 		if ( upgradeVersionCurrent == null || !upgradeVersionCurrent.equals( ObjectKeys.PRODUCT_UPGRADE_VERSION ) )
 		 		{
 					UserProperties adminUserProperties;

 					// Yes.
 		 			adminUserProperties = getProfileModule().getUserProperties( user.getId() );

 		 			// See if all upgrade tasks have been done
 		 			if ( "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_SEARCH_INDEX ) ) &&
 		 				 "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_DEFINITIONS ) ) &&
 		 				 "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_TEMPLATES ) ) )
 		 			{
 		 				// All upgrade tasks are done, mark the upgrade complete
 		 				if ( top != null )
 		 				{
 		 					getBinderModule().setProperty( top.getId(), ObjectKeys.BINDER_PROPERTY_UPGRADE_VERSION, ObjectKeys.PRODUCT_UPGRADE_VERSION );
 		 					upgradeVersionCurrent = ObjectKeys.PRODUCT_UPGRADE_VERSION;
 		 				}
 		 			}
 		 		}
	 		}
	 		
	 		// Are there upgrade tasks to be performed?
	 		if ( upgradeVersionCurrent == null || upgradeVersionCurrent.equalsIgnoreCase( ObjectKeys.PRODUCT_UPGRADE_VERSION ) == false )
	 		{
	 			// Yes.
	 			upgradeInfo.setUpgradeTasksExist( true );
	 			
		 		// Are we dealing with the "admin" user?
		 		if ( ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() ) )
		 		{
		 			String property;
		 			UserProperties adminUserProperties;
		 			
		 			// Yes
 		 			adminUserProperties = getProfileModule().getUserProperties( user.getId() );
 		 			upgradeInfo.setIsAdmin( true );

 		 			// Do the definitions need to be reset?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_DEFINITIONS );
		 			if ( property == null || property.length() == 0 )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_DEFINITIONS );
		 			}

		 			// Do the templates need to be reset?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_TEMPLATES );
		 			if ( property == null || property.length() == 0 )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_TEMPLATES );
		 			}

		 			// Does re-indexing need to be performed?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_SEARCH_INDEX );
		 			if ( property == null || property.length() == 0 )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_SEARCH_INDEX );
		 			}
		 		}
	 		}
		}
		
		return upgradeInfo;
	}
	
	
	/**
	 * 
	 * @param ri
	 * 
	 * @return
	 */
    private ExtensionInfoClient[] getExtensionInfo( HttpRequestInfo ri )
    {
    	List<ExtensionInfo> extList =  new ArrayList<ExtensionInfo>(); 
    	AdminModule adminModule;
    	
    	adminModule = getAdminModule();
    	if(adminModule == null)
    	{
    		ExtensionInfoClient[] infoArray = new ExtensionInfoClient[0];
        	return infoArray;
    	}

    	extList = adminModule.getExtensionManager().getExtensions();
    	ArrayList<ExtensionInfoClient> list = new ArrayList<ExtensionInfoClient>();

    	for(ExtensionInfo info: extList){
        	ExtensionInfoClient client = new ExtensionInfoClient();
        	
        	client.setAuthor(info.getAuthor());
        	client.setAuthorEmail(info.getAuthorEmail());
        	client.setAuthorSite(info.getAuthorSite());
        	client.setDateCreated(info.getDateCreated());
        	client.setDateDeployed(info.getDateDeployed());
        	client.setDescription(info.getDescription());
        	client.setId(info.getId());
        	client.setName(info.getName());
        	client.setZoneId(info.getZoneId());
        	client.setTitle(info.getTitle());
        	client.setVersion(info.getVersion());
        	
        	ZoneInfo zoneInfo = getZoneModule().getZoneInfo(info.getZoneId());
        	if(zoneInfo != null){
            	client.setZoneName(zoneInfo.getZoneName());
        	} 
        	
        	list.add(client);
    	}

    	ExtensionInfoClient[] infoArray = new ExtensionInfoClient[list.size()];
    	list.toArray(infoArray);
    	
    	return infoArray;
    	
    }

    /**
     * 
	 * @param ri
	 * @param id
	 * 
	 * @return
	 * 
	 * @throws ExtensionDefinitionInUseException
     */
    private ExtensionInfoClient[] removeExtension(HttpRequestInfo ri, String id) throws ExtensionDefinitionInUseException
    {
    	AdminModule adminModule;
    	adminModule = getAdminModule();
    	
    	if( adminModule.getExtensionManager().checkDefinitionsInUse(id) )
    	{
    		throw new ExtensionDefinitionInUseException(NLT.get("definition.errror.inUse"));
    	}
    	adminModule.getExtensionManager().removeExtensions(id);

    	return getExtensionInfo(ri);
    }


    /**
     * 
	 * @param ri
	 * @param id
	 * @param zoneName
	 * 
	 * @return
     */
	private ExtensionFiles getExtensionFiles(HttpRequestInfo ri, String id, String zoneName) {
    	ExtensionFiles extFiles = new ExtensionFiles();
    	try 
    	{
    		AdminModule adminModule;
        	adminModule = getAdminModule();

        	ArrayList<String> results = adminModule.getExtensionManager().getExtensionFiles(id, zoneName);
        	extFiles.setResults(results);
        	extFiles.setCountTotal(results.size());
    	} catch (Exception e)
    	{
    	}
    	
    	return extFiles;
	}


	/**
	 * Returns a TreeInfo containing the display information for the
	 * Binder hierarchy referred to by a BucketInfo
	 * 
	 * The information returned is typically used for driving a
	 * horizontal WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param bucketInfo
	 * 
	 * @return
	 */
	private TreeInfo expandHorizontalBucket( HttpRequestInfo ri, BucketInfo bucketInfo )
	{
		// Expand the bucket list without regard to persistent Binder
		// expansions.
		return GwtServerHelper.expandBucket( getRequest( ri ), this, bucketInfo, null );
	}//end expandHorizontalBucket()
	
	/**
	 * Returns a TreeInfo containing the display information for the
	 * Binder hierarchy referred to by a BucketInfo.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param bucketInfo
	 * 
	 * @return
	 */
	private TreeInfo expandVerticalBucket( HttpRequestInfo ri, BucketInfo bucketInfo ) {
		// Expand the bucket list taking any persistent Binder
		// expansions into account.
		return GwtServerHelper.expandBucket( getRequest( ri ), this, bucketInfo, new ArrayList<Long>() );
	}

	/**
	 * Returns a List<TreeInfo> containing the display information for
	 * the Binder hierarchy referred to by binderId from the
	 * perspective of the currently logged in user.
	 * 
	 * The information returned is typically used for driving a
	 * horizontal WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param binderIdS
	 * 
	 * @return
	 */
	private List<TreeInfo> getHorizontalTree( HttpRequestInfo ri, String binderIdS ) {
		Binder binder;
		List<TreeInfo> reply;
		
		// Can we access the Binder?
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS, true );
		if (null == binder) {
			// No!  Then we can't build any TreeInfo objects for it.
			reply = new ArrayList<TreeInfo>();
		}
		else {
			// Yes, we can access the Binder!  Access the Binder's
			// nearest containing Workspace...
			ArrayList<Long> bindersList = new ArrayList<Long>();
			while (true)
			{
				bindersList.add( 0, binder.getId() );
				binder = binder.getParentBinder();
				if ( null == binder )
				{
					break;
				}
			}
	
			// ...and build the TreeInfo for the request Binder.
			reply = GwtServerHelper.buildTreeInfoFromBinderList(
				getRequest( ri ),
				this,
				bindersList );
		}


		// If we get here, reply refers to the TreeInfo for the Binder
		// requested.  Return it.
		return reply;
	}// end getHorizontalTree()
	
	/**
	 * Builds a TreeInfo for a Binder being expanded.
	 * 
	 * The information returned is typically used for driving a
	 * horizontal WorkspaceTreeControl widget.
	 * list.toArray(infoArray);
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private TreeInfo getHorizontalNode( HttpRequestInfo ri, String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Access the Binder...
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS );

		// ...and build the TreeInfo for it.
		reply = ((null == binder) ? null : GwtServerHelper.buildTreeInfoFromBinder( getRequest( ri ), this, binder ));
		return reply;
	}// end getHorizontalNode()

	/**
	 * Returns the ID of the nearest containing workspace of a given
	 * Binder.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private String getRootWorkspaceId( HttpRequestInfo ri, String binderId )
	{
		String reply;
		
		Binder binder = GwtUIHelper.getBinderSafely( getBinderModule(), binderId );
		if (null != binder)
		{
			Workspace binderWS = BinderHelper.getBinderWorkspace( binder );
			reply = String.valueOf( binderWS.getId() );
		}
		else
		{
			binder = getWorkspaceModule().getTopWorkspace();
			reply = String.valueOf( binder.getId() );
		}
		
		return reply;
	}// end getRootWorkspaceId() 

	/**
	 * Returns a TreeInfo object containing the display information for
	 * and activity streams tree using the current Binder referred to
	 * by binderId from the perspective of the currently logged in
	 * user.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget when in activity streams
	 * mode.
	 * 
	 * @param ri
	 * @param binderIdS
	 * 
	 * @return
	 */
	private TreeInfo getVerticalActivityStreamsTree( HttpRequestInfo ri, String binderIdS )
	{
		return GwtActivityStreamHelper.getVerticalActivityStreamsTree( getRequest( ri ), this, binderIdS );
	}// end getVerticalActivityStreamsTree()
	
	/**
	 * Returns a TreeInfo object containing the display information for
	 * the Binder referred to by binderId from the perspective of the
	 * currently logged in user.  Information about the Binder
	 * expansion states for the current user is integrated into the
	 * TreeInfo returned.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param binderIdS
	 * 
	 * @return
	 */
	private TreeInfo getVerticalTree( HttpRequestInfo ri, String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Can we access the Binder?
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS, true );
		if (null == binder) {
			// No!  We can't build a TreeInfo for it.
			reply = new TreeInfo();
		}
		
		else {
			// Yes, we can access the Binder!  Access the Binder's
			// nearest containing Workspace...
			Workspace binderWS = BinderHelper.getBinderWorkspace( binder );
	
			// ...note that the Workspace should always be expanded...
			Long binderWSId = binderWS.getId();
			ArrayList<Long> expandedBindersList = new ArrayList<Long>();
			expandedBindersList.add( binderWSId );
	
			// ...calculate which additional Binder's that must be expanded
			// ...to show the requested Binder...
			long binderId      = Long.parseLong( binderIdS );
			long binderWSIdVal = binderWSId.longValue();
			if ( binderId != binderWSIdVal ) {
				Binder parentBinder = binder.getParentBinder();
				if ( null != parentBinder )
				{
					binder = parentBinder;
					while ( binder.getId().longValue() != binderWSIdVal )
					{
						expandedBindersList.add( binder.getId() );
						binder = binder.getParentBinder();
					}
				}
			}
			
			// ...build the TreeInfo for the request Binder...
			reply = GwtServerHelper.buildTreeInfoFromBinder(
				getRequest( ri ),
				this,
				binderWS,
				expandedBindersList );
	
	
			// ...and if the Binder supports Trash access...
			boolean allowTrash = TrashHelper.allowUserTrashAccess( GwtServerHelper.getCurrentUser() );
			if ( allowTrash && ( !(binder.isMirrored()) ) )
			{
				// ...add a TreeInfo to the reply's children for it.
				GwtServerHelper.addTrashFolder( this, reply, binder );
			}
		}
		
		// If we get here, reply refers to the TreeInfo for the Binder
		// requested.  Return it.
		return reply;
	}// end getVerticalTree()
	
	/**
	 * Builds a TreeInfo for the Binder being expanded and stores the
	 * fact that it has been expanded.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private TreeInfo getVerticalNode( HttpRequestInfo ri, String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Access the Binder...
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS );
		if ( null != binder )
		{
			// ...note that the Binder will now be expanded...
			ArrayList<Long> expandedBindersList = new ArrayList<Long>();
			expandedBindersList.add( Long.parseLong( binderIdS ));
	
			// ...and build the TreeInfo folist.toArray(infoArray);r it.
			reply = GwtServerHelper.buildTreeInfoFromBinder( getRequest( ri ), this, binder, expandedBindersList );
		}
		else
		{
			reply = null;
		}
		return reply;
	}// end getVerticalNode()
	
	
	/*
	 * Parse the given html and replace any markup with the appropriate url.  For example,
	 * replace {{attachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
	 */
	private String markupStringReplacement( HttpRequestInfo ri, String binderId, String html, String type ) throws GwtTeamingException
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
					
					binderModule = getBinderModule();
					binder = binderModule.getBinder( binderIdL );

					// Parse the given html and replace any markup with the appropriate url.  For example,
					// replace {{atachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
					newHtml = MarkupUtil.markupStringReplacement( null, null, getRequest( ri ), null, binder, html, type );
				}
			}
			catch (Exception e)
			{
				throw GwtServerHelper.getGwtTeamingException( e );
			}
		}
		
		return newHtml;
	}
	
	
	/**
	 * Saves the fact that the Binder for the given ID should be
	 * collapsed for the current User.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private Boolean persistNodeCollapse( HttpRequestInfo ri, String binderId )
	{
		GwtServerHelper.persistNodeCollapse( this, Long.parseLong( binderId ) );
		return Boolean.TRUE;
	}// end persistNodeCollapse()

	/**
	 * Saves the fact that the Binder for the given ID should be
	 * expanded for the current User.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private Boolean persistNodeExpand( HttpRequestInfo ri, String binderId )
	{
		GwtServerHelper.persistNodeExpand( this, Long.parseLong( binderId ) );
		return Boolean.TRUE;
	}// end persistNodeExpand()

	
	/**
	 * Adds binderId to the user's list of favorites.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private Boolean addFavorite( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		Binder binder;
		Favorites f;
		String viewAction;
		String title;
		UserProperties userProperties;
		
		binder = getBinderModule().getBinder( Long.parseLong( binderId ) );
		userProperties = getProfileModule().getUserProperties( null );
		f = new Favorites( (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_FAVORITES ) );
		title = binder.getTitle();
		if ( binder instanceof Folder )
		{
			title += " (" + ((Folder)binder).getParentBinder().getTitle() + ")";
		}
		switch ( binder.getEntityType() )
		{
		case folder:     viewAction = "view_folder_listing";  break;
		case profiles:   viewAction = "view_profile_listing"; break;
		default:         viewAction = ""; break;
		}
		try {
			f.addFavorite( title, binder.getPathName(), Favorites.FAVORITE_BINDER, binderId.toString(), viewAction, "" );
		} catch(FavoritesLimitExceededException flee) {
			//There are already too many favorites, some must be deleted first
			// Construct a GwtTeamingException for this error condition.
			throw GwtServerHelper.getGwtTeamingException( flee );
		}
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString() );
		
		return Boolean.TRUE;
	}// end addFavorite()
	
	/**
	 * Removes favoriteId from the user's list of favorites.
	 * 
	 * @param ri
	 * @param favoriteId
	 * 
	 * @return
	 */
	private Boolean removeFavorite( HttpRequestInfo ri, String favoriteId )
	{
		Favorites f;
		UserProperties userProperties;
		
		userProperties = getProfileModule().getUserProperties( null );
		f = new Favorites( (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_FAVORITES ) );
		f.deleteFavorite( favoriteId );
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString() );
		return Boolean.TRUE;
	}//end removeFavorite()
	
	/**
	 * Sets the user's list of favorites to favoritesList.
	 * 
	 * @param ri
	 * @param favoritesList
	 * 
	 * @return
	 */
	private Boolean updateFavorites( HttpRequestInfo ri, List<FavoriteInfo> favoritesList )
	{
		Favorites f;
		Iterator<FavoriteInfo> fiIT;
		
		f = new Favorites();
		for ( fiIT = favoritesList.iterator(); fiIT.hasNext(); )
		{
			FavoriteInfo fi;
			
			fi = fiIT.next();
			try {
				f.addFavorite(fi.getName(), fi.getHover(), fi.getType(), fi.getValue(), fi.getAction(), fi.getCategory());
			} catch(FavoritesLimitExceededException flee) {
				//There are already too many favorites, skip the rest 
				//  (This should never happen when editing existing favorites)
			}
		}
		
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString() );
		return Boolean.TRUE;
	}//end updateFavorites()

	/**
	 * Returns a List<TagInfo> of the tags defined on a binder.
	 *
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private ArrayList<TagInfo> getBinderTags( HttpRequestInfo ri, String binderId )
	{
		return GwtServerHelper.getBinderTags( this, binderId );
	}//end getBinderTags()
	
	/**
	 * Returns true if the user can manage personal tags on the binder.
	 */
	private Boolean canManagePersonalBinderTags( HttpRequestInfo ri, String binderId )
	{
		return GwtServerHelper.canManagePersonalBinderTags( this, binderId );
	}
	
	/**
	 * Returns true if the user can manager personal tags on the entry.
	 */
	private Boolean canManagePersonalEntryTags( HttpRequestInfo ri, String entryId )
	{
		return GwtServerHelper.canManagePersonalEntryTags( this, entryId );
	}
	
	/**
	 * Returns true if the user can manage public tags on the binder
	 * and false otherwise.
	 *
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private Boolean canManagePublicBinderTags( HttpRequestInfo ri, String binderId )
	{
		return GwtServerHelper.canManagePublicBinderTags( this, binderId );
	}//end canManagePublicBinderTags()
	

	/**
	 * Returns true if the user can manage public tags on the given entry
	 * and false otherwise.
	 *
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private Boolean canManagePublicEntryTags( HttpRequestInfo ri, String entryId )
	{
		return GwtServerHelper.canManagePublicEntryTags( this, entryId );
	}
	
	/**
	 * Return the administration options the user has rights to run.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	@SuppressWarnings("unused")
	private ArrayList<GwtAdminCategory> getAdminActions( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try
		{
			ArrayList<GwtAdminCategory> adminActions;
			BinderModule binderModule;
			Long binderIdL;
			
			binderModule = getBinderModule();
	
			binderIdL = new Long( binderId );
			
			if ( binderIdL != null )
			{
				Binder binder;
				
				binder = binderModule.getBinder( binderIdL );

				adminActions = GwtServerHelper.getAdminActions( getRequest( ri ), binder, this );
			}
			else
			{
				m_logger.warn( "In GwtRpcServiceImpl.getAdminActions(), binderIdL is null" );
				adminActions = new ArrayList<GwtAdminCategory>();
			}
			
			return adminActions;
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
		
	}// end getAdminActions()

	
	/**
	 * Returns a BinderInfo describing a binder.
	 *
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private BinderInfo getBinderInfo( HttpRequestInfo ri, String binderId )
	{
		return GwtServerHelper.getBinderInfo( this, binderId );
	}//end getBinderInfo()

	/**
	 * Returns the ID of the default view definition of a folder.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	private String getDefaultFolderDefinitionId( HttpRequestInfo ri, String binderId ) {
		return GwtServerHelper.getDefaultFolderDefinitionId( this, binderId );
	}

	/**
	 * Returns information about the current user's favorites.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	private List<FavoriteInfo> getFavorites( HttpRequestInfo ri )
	{
		return GwtServerHelper.getFavorites( this );
	}// end getFavorites()
	
	/**
	 * Returns information about the teams the current user is a member
	 * of.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	private List<TeamInfo> getMyTeams( HttpRequestInfo ri )
	{
		return GwtServerHelper.getMyTeams( getRequest( ri ), this );
	}// end getMyTeams()
	
	/**
	 * Return the url needed to invoke the user's "micro-blog" page.  
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
 	 */
	private String getMicrBlogUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try {
			
			Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			
			// Does the user have rights to run the "site administration" page?
			if ( p != null )
			{
				AdaptedPortletURL adapterUrl;
				
				Long random = System.currentTimeMillis();
				
				// Yes
				adapterUrl = new AdaptedPortletURL( getRequest( ri ), "ss_forum", true );
				adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST );
				adapterUrl.setParameter( WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_MINIBLOG );
				adapterUrl.setParameter( WebKeys.URL_USER_ID, p.getId().toString() );
				adapterUrl.setParameter( WebKeys.URL_PAGE, "0" );
				adapterUrl.setParameter( "randomNumber", Long.toString(random) );
				
				return adapterUrl.toString();
			}
		
			GwtTeamingException ex;
			
			ex = GwtServerHelper.getGwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
			
		} catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException( ex );
		} 
		
	}// end getSiteAdministrationUrl()
	
	/**
	 * Return the URL needed to start an Instant Message with the user.
	 *
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
 	 */
	private String getImUrl( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try {
			Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			if ( p != null )
			{
				//Get a user object from the principal
				User user = null;
				if (p != null) {
					if (user instanceof User) {
						user = (User) p;
					} else {
						ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
						try {
							user = profileDao.loadUser(p.getId(), p.getZoneId());
						}
						catch(Exception e) {}
					}
				}

				if (user != null) {
					PresenceManager presenceService = (PresenceManager)SpringContextUtil.getBean("presenceService");
					if (presenceService != null)
					{
						String userID = "";
						CustomAttribute attr = user.getCustomAttribute("presenceID");
						if (attr != null)
						{
							userID = (String)attr.getValue();
						}
						if (userID != null && userID.length() > 0)
						{
							return presenceService.getIMProtocolString(userID);
						}
					}
				}
				
				return "";
			}
			
			GwtTeamingException ex;

			ex = GwtServerHelper.getGwtTeamingException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
			
		} catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException( ex );
		} 
	}// end getImUrl

	/**
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	private GwtPresenceInfo getPresenceInfo( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try {
			User userAsking = GwtServerHelper.getCurrentUser();
			GwtPresenceInfo gwtPresence = new GwtPresenceInfo();

			// Can't get presence if we are a guest
			if ( userAsking != null && !(ObjectKeys.GUEST_USER_INTERNALID.equals( userAsking.getInternalId() ) ) )
			{
				Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);

				if ( p != null )
				{
					// Get a user object from the principal
					User user = null;
					if (p != null) {
						if (user instanceof User) {
							user = (User) p;
						} else {
							ProfileDao profileDao = (ProfileDao)SpringContextUtil.getBean("profileDao");
							try {
								user = profileDao.loadUser(p.getId(), p.getZoneId());
							}
							catch(Exception e) {}
						}
					}

					if (user != null) {
						PresenceManager presenceService = (PresenceManager)SpringContextUtil.getBean("presenceService");
						if (presenceService != null)
						{
							PresenceInfo pi = null;
							int userStatus =  PresenceInfo.STATUS_UNKNOWN;

							String userID = "";
							CustomAttribute attr = user.getCustomAttribute("presenceID");
							if (attr != null)
							{
								userID = (String)attr.getValue();
							}
							  
							String userIDAsking = "";
							attr = userAsking.getCustomAttribute("presenceID");
							if (attr != null)
							{
								userIDAsking = (String)attr.getValue();
							}

							if (userID != null && userID.length() > 0 && userIDAsking != null && userIDAsking.length() > 0)
							{
								pi = presenceService.getPresenceInfo(userIDAsking, userID);
								if (pi != null) {
									gwtPresence.setStatusText(pi.getStatusText());
									userStatus = pi.getStatus();
								}	
							}
							switch (userStatus) {
								case PresenceInfo.STATUS_AVAILABLE:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_AVAILABLE);
									break;
								case PresenceInfo.STATUS_AWAY:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_AWAY);
									break;
								case PresenceInfo.STATUS_IDLE:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_IDLE);
									break;
								case PresenceInfo.STATUS_BUSY:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_BUSY);
									break;
								case PresenceInfo.STATUS_OFFLINE:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_OFFLINE);
									break;
								default:
									gwtPresence.setStatus(GwtPresenceInfo.STATUS_UNKNOWN);
							}
						}
					}
				}
			}

			return gwtPresence;
		} catch (Exception e) {
			throw GwtServerHelper.getGwtTeamingException( e );
		}
	}// end getPresenceInfo

	/**
	 * Returns information about the recent place the current user has
	 * visited.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	private List<RecentPlaceInfo> getRecentPlaces( HttpRequestInfo ri )
	{
		return GwtServerHelper.getRecentPlaces( getRequest( ri ), this );
	}// end getRecentPlaces()
	
	/**
	 * Returns information about the saved search the current user has
	 * defined.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	private List<SavedSearchInfo> getSavedSearches( HttpRequestInfo ri )
	{
		return GwtServerHelper.getSavedSearches( this );
	}// end getSavedSearches()
	
	
	/**
	 * Return login information such as self registration and auto complete.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	private GwtLoginInfo getLoginInfo( HttpRequestInfo ri )
	{
		return GwtServerHelper.getLoginInfo( getRequest( ri ), this );
	}// end getSelfRegistrationInfo()
	
	
	/**
	 * Returns a List<ToolbarItem> of the ToolbarItem's
	 * applicable for the given context.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ToolbarItem> getToolbarItems( HttpRequestInfo ri, String binderId )
	{
		// Construct an ArrayList<ToolbarItem> to hold the toolbar
		// items.
		ArrayList<ToolbarItem> tmiList = new ArrayList<ToolbarItem>();

		// If we can't access the HttpSession...
		HttpSession hSession = GwtServerHelper.getCurrentHttpSession();
		if (null == hSession) {
			// ...we can't access the cached toolbar beans to build the
			// ...toolbar items from.  Bail.
			m_logger.debug("GwtRpcServiceImpl.getToolbarItems( 'Could not access the current HttpSession' )");
			return tmiList;
		}

		// If we can't access the cached toolbar beans... 
		GwtUISessionData tabsObj = ((GwtUISessionData) hSession.getAttribute(GwtUIHelper.CACHED_TOOLBARS_KEY));
		Map<String, Map> tbMaps = ((null == tabsObj) ? null : ((Map<String, Map>) tabsObj.getData()));
		if (null == tbMaps) {
			// ...we can't build any toolbar items.  Bail.
			m_logger.debug("GwtRpcServiceImpl.getToolbarItems( 'Could not access any cached toolbars' )");
			return tmiList;
		}
		
		// Scan the toolbars...
		m_logger.debug("GwtRpcServiceImpl.getToolbarItems():");
		Set<String> tbKeySet = tbMaps.keySet();
		for (Iterator<String> tbKeyIT = tbKeySet.iterator(); tbKeyIT.hasNext(); ) {
			// ...constructing a ToolbarItem for each.
			String tbKey = tbKeyIT.next();
			tmiList.add(buildToolbarItemFromToolbar("...", tbKey, tbMaps.get(tbKey)));
		}

		// If we get here, tmiList refers to the
		// List<ToolbarItem>'s to construct the GWT UI based toolbar
		// from.  Return it.
		return tmiList;
	}

	/**
	 * Returns a List<TopRankedInfo> of the top ranked items from the
	 * most recent search.
	 * 
	 * @param ri
	 * 
	 * @return
	 */
	private List<TopRankedInfo> getTopRanked( HttpRequestInfo ri )
	{
		return GwtServerHelper.getTopRanked( getRequest( ri ), this );
	}

	/**
	 * Removes a search based on its SavedSearchInfo.
	 * 
	 * @param ri
	 * @param ssi
	 * 
	 * @return
	 */
	private Boolean removeSavedSearch( HttpRequestInfo ri, SavedSearchInfo ssi ) {
		return GwtServerHelper.removeSavedSearch( this, ssi );
	}// end removeSavedSearch()
	
	/**
	 * Saves a search based on its tab ID and SavedSearchInfo.
	 * 
	 * @param ri
	 * @param searchTabId
	 * @param ssi
	 * 
	 * @return
	 */
	private SavedSearchInfo saveSearch( HttpRequestInfo ri, String searchTabId, SavedSearchInfo ssi ) {
		return GwtServerHelper.saveSearch( this, searchTabId, ssi );
	}// end saveSearch()
	
	
	/*
	 * Save the given subscription data for the given entry id.
	 * @throws GwtTeamingException 
	 */
	private Boolean saveSubscriptionData( HttpRequestInfo ri, String entryId, SubscriptionData subscriptionData )
		throws GwtTeamingException
	{
		return GwtServerHelper.saveSubscriptionData( this, entryId, subscriptionData );
	}
	

	/*
	 * Send an email notification to the given recipients for the given entry.
	 */
	private GwtShareEntryResults shareEntry( HttpRequestInfo ri, String entryId, String comment, List<String> principalIds, List<String> teamIds )
		throws GwtTeamingException
	{
		GwtShareEntryResults retValue;
		
		try
		{
			retValue = GwtServerHelper.shareEntry( this, entryId, comment, principalIds, teamIds );
		}
		catch ( Exception ex )
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}
		
		return retValue;
	}

	/**
	 * Called to mark that the current user is tracking the specified
	 * binder.
	 * 
	 * @param ri
	 * @param binderId
	 */
	private Boolean trackBinder( HttpRequestInfo ri, String binderId )
	{
		BinderHelper.trackThisBinder( this, Long.parseLong(binderId), "add" );
		return Boolean.TRUE;
	}// endtrackBinder()
	
	/**
	 * Called to mark that the current user is no longer tracking the
	 * specified binder.
	 * 
	 * @param ri
	 * @param binderId
	 */
	private Boolean untrackBinder( HttpRequestInfo ri, String binderId )
	{
		BinderHelper.trackThisBinder( this, Long.parseLong(binderId), "delete" );
		return Boolean.TRUE;
	}//end untrackBinder()
	
	/**
	 * Called to mark that the current user is no longer tracking the
	 * person whose workspace is the specified binder.
	 * 
	 * @param ri
	 * @param binderId
	 */
	private Boolean untrackPerson( HttpRequestInfo ri, String binderId )
	{
		Binder binder = getBinderModule().getBinder( Long.parseLong( binderId ) );
		BinderHelper.trackThisBinder( this, binder.getOwnerId(), "deletePerson" );
		return Boolean.TRUE;
	}//end untrackPerson()

	/*
	 * Return whether the given group is the "all users" group.
	 */
	private Boolean isAllUsersGroup( HttpRequestInfo ri, String groupId ) throws GwtTeamingException
	{
		try
		{
			Long groupIdL;
			Principal group;

			// Get the group object.
			groupIdL = Long.valueOf(groupId);
			group = getProfileModule().getEntry(groupIdL);
			if ( group != null && group instanceof Group )
			{
				String internalId;
				
				internalId = group.getInternalId();
				if ( internalId != null && internalId.equalsIgnoreCase( ObjectKeys.ALL_USERS_GROUP_INTERNALID ) )
					return Boolean.TRUE;
			}
		}
		catch (Exception ex)
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}
		
		// If we get here the group is not the "all users" group.
		return Boolean.FALSE;
	}
	
	/**
	 * Called to check if the current user is tracking the
	 * person whose workspace is the specified binder.
	 * 
	 * @param ri
	 * @param binderId
	 */
	private Boolean isPersonTracked( HttpRequestInfo ri, String binderId )
	{
		return BinderHelper.isPersonTracked( this, Long.parseLong( binderId ) );
	}//end isPersonTracked()
	
	/*
	 * Constructs a ToolbarItem based on a toolbar.
	 */
	@SuppressWarnings("unchecked")
	private ToolbarItem buildToolbarItemFromToolbar(String traceStart, String tbKey, Map tbMap) {
		// Log the name of the toolbar that we're building a toolbar
		// item for...
		m_logger.debug(traceStart + ":toolbar=" + tbKey);

		// ...and create its toolbar item.
		ToolbarItem toolbarItem = new ToolbarItem();
		toolbarItem.setName(tbKey);

		// Scan the items in this toolbar's map.
		Set kSet = tbMap.keySet();
		for (Iterator kIT = kSet.iterator(); kIT.hasNext(); ) {
			// Is this item a nested map?
			String k = ((String) kIT.next());
			Object o = tbMap.get(k);
			if (o instanceof Map) {
				// Yes!  Is it a map of qualifiers?
				if (k.equalsIgnoreCase("qualifiers")) {
					// Yes!  Add them to the current toolbar.
					Map m = ((Map) o);
					Set qSet = m.keySet();
					for (Iterator qIT = qSet.iterator(); qIT.hasNext(); ) {
						String name  = ((String) qIT.next());
						Object value = m.get(name);
						String sValue;
						if      (value instanceof Boolean) sValue = String.valueOf((Boolean) value);
						else if (value instanceof String)  sValue = ((String) value);
						else                               sValue = null;
						if (null == sValue) {
							m_logger.debug(traceStart + "...:name:<unknown>:IGNORED QUALIFIER=" + name + ":" + ((null == value) ? "null" : value.getClass()));
						}
						else {
							if (name.equalsIgnoreCase(GwtUIHelper.GWTUI_TEAMING_EVENT)) {
								m_logger.debug(traceStart + "...:name:value:TEAMING_EVENT=" + name + ":" + sValue);
								TeamingEvents te = TeamingEvents.valueOf(sValue);
								toolbarItem.setTeamingEvent(te);
							}
							else {
								m_logger.debug(traceStart + "...:name:value:QUALIFIER=" + name + ":" + sValue);
								toolbarItem.addQualifier(name, sValue);
							}
						}
					}
				}
				else {
					// No, it's not a map of qualifiers!  Construct a
					// nested toolbar item for it.
					toolbarItem.addNestedItem(buildToolbarItemFromToolbar((traceStart + "..."), k, ((Map) o)));
				}
			}
			
			// No, the item isn't a nested map!  Is it a string?
			else if (o instanceof String) {
				// Yes!  Handle the values we know about...
				String s = ((String) o);
				if (k.equalsIgnoreCase("title")) {
					m_logger.debug(traceStart + "...:key:string:TITLE=" + k + ":" + s);
					toolbarItem.setTitle(s);
				}
				else if (k.equalsIgnoreCase("url") || k.endsWith(GwtUIHelper.URLFIXUP_PATCH)) {
					m_logger.debug(traceStart + "...:key:string:URL=" + k + ":" + s);
					toolbarItem.setUrl(  s);
				}
				else {
					// ...and ignore the rest.
					m_logger.debug(traceStart + "...:key:string:IGNORED=" + k + ":" + s);
				}
			}

			// No, the item isn't a string either!  Is it an adapted
			// portlet URL?
			else if (o instanceof AdaptedPortletURL) {
				// Yes!  Then we ignore it as it will have been handled
				// as a string above.  (See the URLFIXUP_PATCH
				// reference above.)
			}
			
			else {
				// No, the item isn't an adapted portlet URL either!
				// We don't know how to handle it!
				m_logger.debug(traceStart + "...:key:<unknown>:IGNORED=" + k + ":" + ((null == o) ? "null" : o.getClass()));
			}
		}

		// If we get here, toolbarItem refers to the ToolbarItem for
		// this toolbar.  Return it.
		return toolbarItem;
	}

	/*
	 * Get the subscription data for the given entry id.
	 */
	private SubscriptionData getSubscriptionData( HttpRequestInfo ri, String entryId )
	{
		SubscriptionData subscriptionData;
		
		subscriptionData = GwtServerHelper.getSubscriptionData( this, entryId );
		
		return subscriptionData;
	}
	
	/**
	 * Returns a TeamManagementInfo object regarding the current user's
	 * team management capabilities.
	 * 
	 * @param ri
	 * @binderId
	 * 
	 * @return
	 */
	private TeamManagementInfo getTeamManagementInfo( HttpRequestInfo ri, String binderId )
	{
		TeamManagementInfo tmi;
		User user;
		
		// Construct a base TeamManagementInfo object.
		tmi = new TeamManagementInfo();
		
		// Is the current user the guest user?
		user = GwtServerHelper.getCurrentUser();
		if ( !(ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) )
		{
			Binder binder;
			
			// No!  Is the binder other than the profiles container?
			binder = GwtUIHelper.getBinderSafely( getBinderModule(), binderId );
			if ( ( null != binder ) && ( EntityIdentifier.EntityType.profiles != binder.getEntityType() ) )
			{
				AdaptedPortletURL adapterUrl;
				HttpServletRequest request = getRequest( ri ); 
			
				// Yes!  Then the user is allowed to view team membership.
				tmi.setViewAllowed( true );
	
				// If the user can manage the team...
				if ( getBinderModule().testAccess( binder, BinderOperation.manageTeamMembers ) )
				{
					// ...store the team management URL...
					adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
					adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_ADD_TEAM_MEMBER );
					adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
					adapterUrl.setParameter( WebKeys.URL_BINDER_TYPE, binder.getEntityType().name() );
					tmi.setManageUrl( adapterUrl.toString() );
				}
	
				// ...if the user can send mail to the team...
				if ( MiscUtil.hasString( user.getEmailAddress() ) )
				{
					// ...store the send mail URL...
					adapterUrl = new AdaptedPortletURL( request, "ss_forum", true );
					adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_SEND_EMAIL );
					adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
					adapterUrl.setParameter( WebKeys.URL_APPEND_TEAM_MEMBERS, Boolean.TRUE.toString() );
					tmi.setSendMailUrl( adapterUrl.toString() );
				}
	
				// ...if the user can start a team meeting...
				if ( getConferencingModule().isEnabled())
				{
					CustomAttribute ca = user.getCustomAttribute("conferencingID");
					if (ca != null && MiscUtil.hasString((String)ca.getValue())) {		
						// ...store the team meeting URL.
						try {
							tmi.setTeamMeetingUrl( getAddMeetingUrl(ri, binderId) );
						} catch (GwtTeamingException e) {
							// Nothing to do...
						}
					}
				}
			}
		}

		// If we get here, tmi refers to a TeamManagementInfo object
		// containing the user's team management capabilities.  Return
		// it.
		return tmi;
	}//end getTeamManagementInfo()
	
	/*
	 * Add a reply to the given entry.  Return an object that can be used by the What's New page.
	 */
	private ActivityStreamEntry replyToEntry( HttpRequestInfo ri, String entryId, String title, String desc ) throws GwtTeamingException
	{
		ActivityStreamEntry asEntry;
		
		try
		{
			FolderEntry entry;

			// Add the reply to the given entry.
			entry = GwtServerHelper.addReply( this, entryId, title, desc );
			
			// Get an ActivityStreamEntry from the reply's FolderEntry.
			asEntry = GwtActivityStreamHelper.getActivityStreamEntry( getRequest( ri ), this, entry );
		}
		catch ( Exception ex )
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}
		
		return asEntry;
	}
	
	/**
	 * Save the given branding data to the given binder.
	 *
	 * @param ri
	 * @param binderId
	 * @param brandingData
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private Boolean saveBrandingData( HttpRequestInfo ri, String binderId, GwtBrandingData brandingData ) throws GwtTeamingException
	{
		BinderModule binderModule;
		Long binderIdL;
		
		try
		{
			binderModule = getBinderModule();
	
			binderIdL = new Long( binderId );
			
			// Get the binder object.
			if ( binderIdL != null )
			{
				String branding;
				HashMap<String, Object> hashMap;
				MapInputData dataMap;
				
				// Create a Map that holds the branding and extended branding.
				hashMap = new HashMap<String, Object>();
				
				// Add the old-style branding to the map.
				//!!! Do we need to do something with the html found in the branding?
				branding = brandingData.getBranding();
				if ( branding == null )
					branding = "";
			
				// Remove mce_src as an attribute from all <img> tags.  See bug 766415.
				// There was a bug that caused the mce_src attribute to be included in the <img>
				// tag and written to the db.  We want to remove it.
				branding = MarkupUtil.removeMceSrc( branding );
	
				hashMap.put( "branding", branding );

				// Add the exteneded branding data to the map.
				branding = brandingData.getBrandingAsXmlString();
				if ( branding == null )
					branding = "";
				hashMap.put( "brandingExt", branding );
				
				// Update the binder with the new branding data.
				dataMap = new MapInputData( hashMap );
				binderModule.modifyBinder( binderIdL, dataMap, null, null, null );
			}
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
		
		return Boolean.TRUE;
	}// end saveBrandingData()


	/**
	 * Save the given personal preferences for the logged in user.
	 *
	 * @param ri
	 * @param personalPrefs
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private Boolean savePersonalPreferences( HttpRequestInfo ri, GwtPersonalPreferences personalPrefs ) throws GwtTeamingException
	{
		try
		{
			User user;
			
			// Is the current user the guest user?
			user = GwtServerHelper.getCurrentUser();
			
			// Are we dealing with the guest user?
			if ( !(ObjectKeys.GUEST_USER_INTERNALID.equals( user.getInternalId() ) ) )
			{
				ProfileModule profileModule;
				
				profileModule = getProfileModule();
				
				// No
				// Save the "display style" preference
				{
					Map<String,Object> updates;
					String newDisplayStyle;
					
					updates = new HashMap<String,Object>();
					
					newDisplayStyle = personalPrefs.getDisplayStyle();
					
					// Only allow "word" characters (such as a-z_0-9 )
					if ( newDisplayStyle.equals("") || !newDisplayStyle.matches( "^.*[\\W]+.*$" ) )
					{
						updates.put( ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, newDisplayStyle );
						profileModule.modifyEntry( user.getId(), new MapInputData( updates ) );
					}
				}
				
				// Save the "show tutorial panel" preference
				{
					@SuppressWarnings("unused")
					String tutorialPanelState;
					
					if ( personalPrefs.getShowTutorialPanel() )
						tutorialPanelState = "2";
					else
						tutorialPanelState = "1";
					
					// We don't have a tutorial panel any more (as of Durango).
					// profileModule.setUserProperty( null, ObjectKeys.USER_PROPERTY_TUTORIAL_PANEL_STATE, tutorialPanelState );
				}
				
				// Save the "number of entries per page" preference
				{
					profileModule.setUserProperty(
												user.getId(),
												ObjectKeys.PAGE_ENTRIES_PER_PAGE,
												String.valueOf( personalPrefs.getNumEntriesPerPage() ) );
				}
			}
			else
			{
				m_logger.warn( "GwtRpcServiceImpl.getPersonalPreferences(), user is guest." );
			}
		}
		catch (Exception e)
		{
			if (e instanceof AccessControlException)
				 m_logger.warn( "GwtRpcServiceImpl.savePersonalPreferences() AccessControlException" );
			else m_logger.warn( "GwtRpcServiceImpl.savePersonalPreferences() unknown exception" );
			throw GwtServerHelper.getGwtTeamingException( e );
		}
		
		return Boolean.TRUE;
	}// end savePersonalPreferences()


	/**
	 * Get the profile information based on the binder Id passed in.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	private ProfileInfo getProfileInfo(HttpRequestInfo ri, String binderId) throws GwtTeamingException {
		try
		{
			//get the binder
			ProfileInfo profile = GwtProfileHelper.buildProfileInfo(getRequest( ri ), this, Long.valueOf(binderId));
			return profile;
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
	}
	
	
	/**
	 * Get the profile information for the Quick View based on the binder Id passed in.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private ProfileInfo getQuickViewInfo(HttpRequestInfo ri, String binderId) throws GwtTeamingException {
		
		try {
			Long binderIdL = Long.valueOf(binderId);
			
			//get the binder
			ProfileInfo profile = GwtProfileHelper.buildQuickViewProfileInfo(getRequest( ri ), this, binderIdL);
			
			return profile;
		} 
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
	}

	/*
	 * Return the membership of the given group.
	 */
	private ArrayList<GwtTeamingItem> getGroupMembership( HttpRequestInfo ri, String groupId ) throws GwtTeamingException
	{
		return GwtServerHelper.getGroupMembership( this, groupId );
	}
	
	
	/**
	 * Returns Look up the user and return the list of groups they belong to.
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private List<GroupInfo> getGroups( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try {
			Long userId = null;
			Principal p = null;

			if (binderId != null) {
				p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			}
			
			if (p != null){
				userId = p.getId();
			}
			
			return GwtServerHelper.getGroups( getRequest( ri ), this, userId );
		} catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException( ex );
		} 
		
	}

	/**
	 * Returns Look up the workspace owner and return the list of teams they belong to.
	 *
	 * @param ri
	 * @param binderId  The binderId of the workspace being viewed
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private List<TeamInfo> getTeams( HttpRequestInfo ri, String binderId ) throws GwtTeamingException
	{
		try {
			Long userId = null;
			Principal p = null;

			if (binderId != null) {
				p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			}
			
			if (p != null){
				userId = p.getId();
			}
			
			return GwtServerHelper.getTeams( getRequest( ri ), this, userId );
		} catch (Exception ex) {
			throw GwtServerHelper.getGwtTeamingException( ex );
		} 
		
	}// end getMyTeams()

	/**
	 * Save the User Status
	 * 
	 * @param ri
	 * @param status The text to store in the Micro-Blog
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException 
	 */
	private SaveUserStatusRpcResponseData saveUserStatus( HttpRequestInfo ri, String status ) throws GwtTeamingException
	{
		try
		{
			BinderHelper.MiniBlogInfo mbi = BinderHelper.addMiniBlogEntryDetailed(this, status);
			return new SaveUserStatusRpcResponseData(mbi.getEntryId(), mbi.getFolderId(), mbi.isNewMiniBlogFolder());
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
	}

	/**
	 * Return the "user permalink" URL.
	 * 
	 * @param ri
	 * @param userId
	 * 
	 * @return
	 */
	private String getUserPermalink( HttpRequestInfo ri, String userId )
	{
		if ( userId != null && userId.length() > 0 )
		{
			Long userIdL = new Long( userId );
			
			User u = (User) getProfileModule().getEntry(userIdL);
			return PermaLinkUtil.getPermalink( getRequest( ri ), u );
		}
		
		return "";
	}
	
	/**
	 * Get the User Status from their Micro Blog
	 * 
	 * @param ri
	 * @param binderId This is the binderId of the workspace we are loading
	 * 
	 * @return UserStatus This object contains information about the user status.
	 * 
	 * @throws GwtTeamingException 
	 */
	private UserStatus getUserStatus(HttpRequestInfo ri, String sbinderId)
			throws GwtTeamingException {
	
		return GwtProfileHelper.getUserStatus(this, sbinderId);
	}

	/**
	 * Get the stats for the user
	 * 
	 * @param ri
	 * @param binderId This is the binderId of the workspace you want to get stats on.
	 * @param userId   This is the userId   of the currently logged in user.
	 * 
	 * @return ProfileStats This object contains the stat info to display
	 * 
	 * @throws GwtTeamingException
	 */
	private ProfileStats getProfileStats(HttpRequestInfo ri, String binderId, String userId) throws GwtTeamingException {
		try
		{
			User binderCreator = GwtServerHelper.getBinderCreator(this, binderId);
			ProfileStats stats = GwtProfileHelper.getStats(getRequest(ri), this, String.valueOf(binderCreator.getId()));
			return stats;
		}
		catch ( Exception e )
		{
			if ( ( e instanceof AccessControlException ) ||
				 ( e instanceof NoUserByTheIdException ) )
			{
				throw GwtServerHelper.getGwtTeamingException( e );
			}
			
			//Log other errors
			logger.error("Error getting stats for user with binderId "+userId, e);
		}
		
		return null;
	}

	/**
	 * Get the avatars for the user profile.
	 * 
	 * @param ri
	 * @param binderId  This is the binderId of the user.
	 * 
	 * @return ProfileAttribute  The ProfileAttribute contains the information needed to populate the avatars
	 */
	private ProfileAttribute getProfileAvatars(HttpRequestInfo ri, String binderId) {
		ProfileAttribute attr = GwtProfileHelper.getProfileAvatars(getRequest( ri ), this, Long.valueOf(binderId));
		return attr;
	}
	
	
	/**
	 * Get disk usage information per user
	 * 
	 * @param ri
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	private  DiskUsageInfo getDiskUsageInfo(HttpRequestInfo ri, String binderId) throws GwtTeamingException {
		
		DiskUsageInfo diskUsageInfo = null;
		try {
			
			
			diskUsageInfo = new DiskUsageInfo();
			GwtProfileHelper.getDiskUsageInfo(getRequest( ri ), this, binderId, diskUsageInfo);
			
			return diskUsageInfo;
		} 
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
	}
	
	
	/**
	 * Update the tags for the given binder.
	 */
	private Boolean updateBinderTags( HttpRequestInfo ri, String binderId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded )
	{
		return GwtServerHelper.updateBinderTags( this, binderId, tagsToBeDeleted, tagsToBeAdded );
	}


	/**
	 * Update the tags for the given entry.
	 */
	private Boolean updateEntryTags( HttpRequestInfo ri, String entryId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded )
	{
		return GwtServerHelper.updateEntryTags( this, entryId, tagsToBeDeleted, tagsToBeAdded );
	}


	/*
	 * Validates the list of TeamingEvents to see if the user has rights to perform the events
	 * for the given entry id.
	 */
	private List<EventValidation> validateEntryEvents( HttpRequestInfo ri, List<EventValidation> eventValidations, String entryId )
	{
		// Validate the given events.
		GwtServerHelper.validateEntryEvents( this, ri, eventValidations, entryId );
		
		return eventValidations;
	}

	/*
	 * Return true if an entry has been seen and false otherwise.
	 */
	private Boolean isSeen( HttpRequestInfo ri, Long entryId ) throws GwtTeamingException
	{
		try
		{
			SeenMap seenMap = getProfileModule().getUserSeenMap( null );
			FolderEntry fe = getFolderModule().getEntry( null, entryId );
			return seenMap.checkIfSeen( fe );
		}
		
		catch ( Exception ex )
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}
	}//end isSeen()
	
	/**
	 * Save the given show setting (show all, show unread) for the What's New page
	 * to the user's properties.
	 */
	private Boolean saveWhatsNewShowSetting( HttpRequestInfo ri, ShowSetting showSetting )
	{
		return GwtServerHelper.saveWhatsNewShowSetting( this, showSetting );
	}
	
	/*
	 * Marks a list of entries as having been seen.
	 */
	private Boolean setSeen( HttpRequestInfo ri, List<Long> entryIds ) throws GwtTeamingException
	{
		getProfileModule().setSeenIds( GwtServerHelper.getCurrentUser().getId(), entryIds );
		return Boolean.TRUE;
	}//end setSeen()
	
	/*
	 * Marks a list of entries as having been unseen.
	 */
	private Boolean setUnseen( HttpRequestInfo ri, List<Long> entryIds ) throws GwtTeamingException
	{
		getProfileModule().setUnseen( GwtServerHelper.getCurrentUser().getId(), entryIds );
		return Boolean.TRUE;
	}//end setUnseen()
}// end GwtRpcServiceImpl
