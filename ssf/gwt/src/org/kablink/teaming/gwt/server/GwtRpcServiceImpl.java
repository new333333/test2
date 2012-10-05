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
package org.kablink.teaming.gwt.server;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
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
import org.kablink.teaming.gwt.client.BlogArchiveInfo;
import org.kablink.teaming.gwt.client.BlogPages;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;
import org.kablink.teaming.gwt.client.GwtAttachment;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtLoginInfo;
import org.kablink.teaming.gwt.client.GwtPersonalPreferences;
import org.kablink.teaming.gwt.client.GwtSearchCriteria;
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.lpe.LandingPageProperties;
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
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd.MembershipFilter;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.SpecificFolderData;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.BinderStats;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderSortSetting;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.ProjectInfo;
import org.kablink.teaming.gwt.client.util.TagSortOrder;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamParams;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BucketInfo;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.SubscriptionData;
import org.kablink.teaming.gwt.client.util.TagInfo;
import org.kablink.teaming.gwt.client.util.TaskBundle;
import org.kablink.teaming.gwt.client.util.TaskDate;
import org.kablink.teaming.gwt.client.util.TaskLinkage;
import org.kablink.teaming.gwt.client.util.TaskListItem;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskEvent;
import org.kablink.teaming.gwt.client.util.TaskListItem.TaskInfo;
import org.kablink.teaming.gwt.client.util.TopRankedInfo;
import org.kablink.teaming.gwt.client.util.TreeInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.util.ViewInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.whatsnew.EventValidation;
import org.kablink.teaming.gwt.server.util.GwtActivityStreamHelper;
import org.kablink.teaming.gwt.server.util.GwtBlogHelper;
import org.kablink.teaming.gwt.server.util.GwtCalendarHelper;
import org.kablink.teaming.gwt.server.util.GwtEmailHelper;
import org.kablink.teaming.gwt.server.util.GwtNetFolderHelper;
import org.kablink.teaming.gwt.server.util.GwtMenuHelper;
import org.kablink.teaming.gwt.server.util.GwtProfileHelper;
import org.kablink.teaming.gwt.server.util.GwtSearchHelper;
import org.kablink.teaming.gwt.server.util.GwtServerHelper;
import org.kablink.teaming.gwt.server.util.GwtShareHelper;
import org.kablink.teaming.gwt.server.util.GwtTaskHelper;
import org.kablink.teaming.gwt.server.util.GwtViewHelper;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.FavoritesLimitExceededException;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.WorkspaceTreeHelper;
import org.kablink.util.search.Constants;

/**
 * Collection of methods used to implement the various GWT RPC
 * commands.
 * 
 * @author jwootton@novell.com
 */
public class GwtRpcServiceImpl extends AbstractAllModulesInjected
	implements GwtRpcService
{
	protected static Log m_logger = LogFactory.getLog(GwtRpcServiceImpl.class);

	/**
	 * Execute the given command.
	 */
	@Override
	public VibeRpcResponse executeCommand( HttpRequestInfo ri, VibeRpcCmd cmd ) throws GwtTeamingException
	{
		VibeRpcResponse response = null;
		HttpServletRequest req;
		
		req = getRequest( ri );

		VibeRpcCmdType cmdEnum = VibeRpcCmdType.getEnum( cmd.getCmdType() );
		switch ( cmdEnum )
		{
		case ABORT_FILE_UPLOAD:
		{
			AbortFileUploadCmd afuCmd = ((AbortFileUploadCmd) cmd);
			BooleanRpcResponseData result = GwtViewHelper.abortFileUpload( this, getRequest( ri ), afuCmd.getFolderInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case ADD_FAVORITE:
		{
			AddFavoriteCmd afCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			afCmd = (AddFavoriteCmd) cmd;
			result = GwtServerHelper.addFavorite( this, getRequest( ri ), Long.parseLong( afCmd.getBinderId() ) );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case ADD_NEW_FOLDER:
		{
			AddNewFolderCmd afCmd = ((AddNewFolderCmd) cmd);
			CreateFolderRpcResponseData responseData = GwtViewHelper.addNewFolder( this, getRequest( ri ), afCmd.getBinderId(), afCmd.getFolderTemplateId(), afCmd.getFolderName() ); 
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case CAN_ADD_FOLDER:
		{
			CanAddFolderCmd cafCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			cafCmd = (CanAddFolderCmd) cmd;
			result = GwtServerHelper.canAddFolder( this, cafCmd.getBinderId() );
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
		
		case CHANGE_ENTRY_TYPES:
		{
			ChangeEntryTypesCmd cetCmd = ((ChangeEntryTypesCmd) cmd);
			ErrorListRpcResponseData result = GwtViewHelper.changeEntryTypes( this, getRequest( ri ), cetCmd.getDefId(), cetCmd.getEntryIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case CHANGE_FAVORITE_STATE:
		{
			ChangeFavoriteStateCmd cfsCmd = ((ChangeFavoriteStateCmd) cmd);
			BooleanRpcResponseData result = GwtServerHelper.changeFavoriteState( this, getRequest( ri ), cfsCmd.getBinderId(), cfsCmd.getMakeFavorite() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case CHECK_NET_FOLDERS_STATUS:
		{
			CheckNetFoldersStatusCmd csCmd;
			Set<NetFolder> listOfNetFolders;
			
			csCmd = (CheckNetFoldersStatusCmd) cmd;
			listOfNetFolders = GwtNetFolderHelper.checkNetFoldersStatus(
																	this,
																	req,
																	csCmd.getListOfNetFoldersToCheck() );
			response = new VibeRpcResponse( new CheckNetFoldersStatusRpcResponseData( listOfNetFolders ) );
			return response;
		}
		
		case COLLAPSE_SUBTASKS:
		{
			CollapseSubtasksCmd csCmd = ((CollapseSubtasksCmd) cmd);
			Boolean result = collapseSubtasks( ri, csCmd.getBinderId(), csCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case COPY_ENTRIES:
		{
			CopyEntriesCmd ceCmd = ((CopyEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.copyEntries( this, getRequest( ri ), ceCmd.getTargetFolderId(), ceCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
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
		
		case CREATE_NET_FOLDER:
		{
			CreateNetFolderCmd cnfCmd;
			NetFolder netFolder;
			
			cnfCmd = (CreateNetFolderCmd) cmd;
			netFolder = GwtNetFolderHelper.createNetFolder(
														this,
														cnfCmd.getNetFolder() );
			response = new VibeRpcResponse( netFolder );
			
			return response;
		}
		
		case CREATE_NET_FOLDER_ROOT:
		{
			CreateNetFolderRootCmd cnfrCmd;
			NetFolderRoot netFolderRoot;
			
			cnfrCmd = (CreateNetFolderRootCmd) cmd;
			netFolderRoot = GwtNetFolderHelper.createNetFolderRoot( this, cnfrCmd.getNetFolderRoot() ); 
			response = new VibeRpcResponse( netFolderRoot );
			
			return response;
		}
		
		case DELETE_NET_FOLDERS:
		{
			Boolean result;
			DeleteNetFoldersCmd dnfCmd;
			
			dnfCmd = (DeleteNetFoldersCmd) cmd;
			result = GwtNetFolderHelper.deleteNetFolders( this, dnfCmd.getListOfNetFoldersToDelete() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case DELETE_NET_FOLDER_ROOTS:
		{
			Boolean result;
			DeleteNetFolderRootsCmd dnfrCmd;
			
			dnfrCmd = (DeleteNetFolderRootsCmd) cmd;
			result = GwtNetFolderHelper.deleteNetFolderRoots( this, dnfrCmd.getListOfNetFolderRootsToDelete() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
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
		
		case DELETE_FOLDER_ENTRIES:
		{
			DeleteFolderEntriesCmd dfeCmd = ((DeleteFolderEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtServerHelper.deleteFolderEntries( this, getRequest( ri ), dfeCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DELETE_TASKS:
		{
			DeleteTasksCmd dtCmd = ((DeleteTasksCmd) cmd);
			ErrorListRpcResponseData responseData = deleteTasks( ri, dtCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DELETE_USER_WORKSPACES:
		{
			DeleteUserWorkspacesCmd duwCmd = ((DeleteUserWorkspacesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.deleteUserWorkspaces( this, getRequest( ri ), duwCmd.getUserIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DISABLE_USERS:
		{
			DisableUsersCmd duCmd = ((DisableUsersCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.disableUsers( this, getRequest( ri ), duCmd.getUserIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case ENABLE_USERS:
		{
			EnableUsersCmd euCmd = ((EnableUsersCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.enableUsers( this, getRequest( ri ), euCmd.getUserIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case EXECUTE_ENHANCED_VIEW_JSP:
		{
			HttpServletResponse resp;
			ServletContext servletContext;
			ExecuteEnhancedViewJspCmd eevjCmd;
			String jspPath;
			String result;
			
			resp = getResponse( ri );
			servletContext = getServletContext( ri );
			eevjCmd = (ExecuteEnhancedViewJspCmd) cmd;

			// Construct the full path to the jsp
			jspPath = "/WEB-INF/jsp/landing_page_enhanced_views/" + eevjCmd.getJspName();
			
			result = GwtServerHelper.executeLandingPageJsp( this, req, resp, servletContext, eevjCmd.getBinderId(), jspPath, eevjCmd.getConfigStr() );
			response = new VibeRpcResponse( new StringRpcResponseData( result ) );
			return response;
		}
		
		case EXECUTE_LANDING_PAGE_CUSTOM_JSP:
		{
			HttpServletResponse resp;
			ServletContext servletContext;
			ExecuteLandingPageCustomJspCmd elpjCmd;
			String jspPath;
			String result;
			
			resp = getResponse( ri );
			servletContext = getServletContext( ri );
			elpjCmd = (ExecuteLandingPageCustomJspCmd) cmd;

			// We need to ensure that we are executing a jsp from the /WEB-INF/jsp/custom_jsps
			// directory.  If the user entered "./../../logs/ssf.log" for the custom jsp
			// name we don't want to allow them to do that.
			{
				File parentDir;
				File customJspFile;
				
				// Construct the full path to the jsp
				jspPath =  "/WEB-INF/jsp/custom_jsps/" + elpjCmd.getJspName();
				
				// Get the directory that holds the given jsp
				customJspFile = new File( elpjCmd.getJspName() );
				parentDir = customJspFile.getParentFile();
				
				// The parentDir of the custom jsp should be null.  If it is not then
				// that means the name contains directory paths which is a no, no.
				if ( parentDir == null )
				{
					// Yes
					result = GwtServerHelper.executeLandingPageJsp( this, req, resp, servletContext, elpjCmd.getBinderId(), jspPath, elpjCmd.getConfigStr() );
					
				}
				else
				{
					String errMsg;
					
					errMsg = NLT.get( "mashup.customJspNotInCustomJspDir" );
					result = NLT.get( "mashup.customJspError", new Object[]{errMsg} );
				}
			}
			
			response = new VibeRpcResponse( new StringRpcResponseData( result ) );
			return response;
		}
		
		case EXECUTE_SEARCH:
		{
			GwtSearchResults searchResults;
			GwtSearchCriteria searchCriteria;
			
			searchCriteria = ((ExecuteSearchCmd) cmd).getSearchCriteria();
			
			try
			{
				searchResults = GwtSearchHelper.executeSearch( this, req, searchCriteria );
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
		
		case FIND_USER_BY_EMAIL_ADDRESS:
		{
			FindUserByEmailAddressCmd fuCmd;
			GwtUser gwtUser;
			
			fuCmd = (FindUserByEmailAddressCmd) cmd;
			gwtUser = GwtSearchHelper.findUserByEmailAddress( this, req, fuCmd.getEmailAddress() );
			response = new VibeRpcResponse( gwtUser );
			return response;
		}

		case GET_ACCESSORY_STATUS:
		{
			GetAccessoryStatusCmd gasCmd = ((GetAccessoryStatusCmd) cmd);
			Boolean responseData = GwtViewHelper.getAccessoryStatus(
				this,
				getRequest( ri ),
				gasCmd.getBinderId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( responseData ) );
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
				gasdCmd.getActivityStreamDataType(),
				gasdCmd.getSpecificFolderData() );
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
			result = GwtServerHelper.getAddMeetingUrl( this, getRequest( ri ), gamuCmd.getBinderId() );
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
		
		case GET_ALL_NET_FOLDERS:
		{
			List<NetFolder> result;
			GetNetFoldersRpcResponseData responseData;
			
			result = GwtNetFolderHelper.getAllNetFolders( this );
			responseData = new GetNetFoldersRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ALL_NET_FOLDER_ROOTS:
		{
			List<NetFolderRoot> result;
			GetNetFolderRootsRpcResponseData responseData;
			
			result = GwtNetFolderHelper.getAllNetFolderRoots( this );
			responseData = new GetNetFolderRootsRpcResponseData( result );
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
		
		case GET_BINDER_DESCRIPTION:
		{
			GetBinderDescriptionCmd gbdCmd = ((GetBinderDescriptionCmd) cmd);
			BinderDescriptionRpcResponseData responseData = GwtViewHelper.getBinderDescription(
				this,
				getRequest( ri ),
				gbdCmd.getBinderId() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_BINDER_FILTERS:
		{
			GetBinderFiltersCmd gbfCmd = ((GetBinderFiltersCmd) cmd);
			BinderFiltersRpcResponseData responseData = GwtViewHelper.getBinderFilters(
				this,
				getRequest( ri ),
				gbfCmd.getBinderId() );
			return new VibeRpcResponse( responseData );
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
		
		case GET_BINDER_REGION_STATE:
		{
			GetBinderRegionStateCmd gbrsCmd = ((GetBinderRegionStateCmd) cmd);
			StringRpcResponseData responseData = GwtViewHelper.getBinderRegionState(
				this,
				getRequest( ri ),
				gbrsCmd.getBinderId(),
				gbrsCmd.getRegionId() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_BINDER_STATS:
		{
			GetBinderStatsCmd gbsCmd;
			BinderStats responseData;
			
			gbsCmd = (GetBinderStatsCmd) cmd;
			responseData = GwtServerHelper.getBinderStats( this, gbsCmd.getBinderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_VIEW_INFO:
		{
			ViewInfo vi = getViewInfo( ri, ((GetViewInfoCmd) cmd) );
			response = new VibeRpcResponse( vi );
			return response;
		}
		
		case GET_BINDER_OWNER_AVATAR_INFO:
		{
			GetBinderOwnerAvatarInfoCmd gboaiCmd = ((GetBinderOwnerAvatarInfoCmd) cmd);
			AvatarInfoRpcResponseData responseData = GwtViewHelper.getBinderOwnerAvatarInfo( this, getRequest( ri ), gboaiCmd.getBinderId() );
			response = new VibeRpcResponse( responseData );
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
		
		case GET_CALENDAR_APPOINTMENTS:
		{
			GetCalendarAppointmentsCmd gcaCmd = ((GetCalendarAppointmentsCmd) cmd);
			CalendarAppointmentsRpcResponseData responseData = GwtCalendarHelper.getCalendarAppointments(
				this,
				getRequest( ri ),
				gcaCmd.getFolderId(),
				gcaCmd.getCalendarDisplayData(),
				gcaCmd.getQuickFilter() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CALENDAR_DISPLAY_DATA:
		{
			GetCalendarDisplayDataCmd gcddCmd = ((GetCalendarDisplayDataCmd) cmd);
			CalendarDisplayDataRpcResponseData responseData = GwtCalendarHelper.getCalendarDisplayData(
				this,
				getRequest( ri ),
				gcddCmd.getFolderInfo() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CALENDAR_DISPLAY_DATE:
		{
			GetCalendarDisplayDateCmd gcddCmd = ((GetCalendarDisplayDateCmd) cmd);
			CalendarDisplayDataRpcResponseData responseData = GwtCalendarHelper.getCalendarDisplayDate(
				this,
				getRequest( ri ),
				gcddCmd.getFolderId(),
				gcddCmd.getCalendarDisplayData() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CALENDAR_NEXT_PREVIOUS_PERIOD:
		{
			GetCalendarNextPreviousPeriodCmd gcnppCmd = ((GetCalendarNextPreviousPeriodCmd) cmd);
			CalendarDisplayDataRpcResponseData responseData = GwtCalendarHelper.getCalendarNextPreviousPeriod(
				this,
				getRequest( ri ),
				gcnppCmd.getFolderId(),
				gcnppCmd.getCalendarDisplayData(),
				gcnppCmd.getNext() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CLIPBOARD_TEAM_USERS:
		{
			GetClipboardTeamUsersCmd gctuCmd = ((GetClipboardTeamUsersCmd) cmd);
			ClipboardUsersRpcResponseData result = GwtServerHelper.getClipboardTeamUsers( this, getRequest( ri ), gctuCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_CLIPBOARD_USERS:
		{
			@SuppressWarnings("unused")
			GetClipboardUsersCmd gcuCmd = ((GetClipboardUsersCmd) cmd);
			ClipboardUsersRpcResponseData result = GwtServerHelper.getClipboardUsers( this, getRequest( ri ) );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_CLIPBOARD_USERS_FROM_LIST:
		{
			GetClipboardUsersFromListCmd gcuflCmd = ((GetClipboardUsersFromListCmd) cmd);
			ClipboardUsersRpcResponseData result = GwtServerHelper.getClipboardUsersFromList( this, getRequest( ri ), gcuflCmd.getBinderId(), gcuflCmd.getUserIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_COLLECTION_POINT_DATA:
		{
			CollectionPointData collectionPointData;
			
			collectionPointData= GwtServerHelper.getCollectionPointData(
																	this,
																	getRequest( ri ) );
			response = new VibeRpcResponse( collectionPointData );
			return response;
		}
		
		case GET_COLUMN_WIDTHS:
		{
			GetColumnWidthsCmd gcwCmd = ((GetColumnWidthsCmd) cmd);
			ColumnWidthsRpcResponseData result = GwtViewHelper.getColumnWidths(
				this,
				getRequest( ri ),
				gcwCmd.getFolderInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_BLOG_ARCHIVE_INFO:
		{
			GetBlogArchiveInfoCmd gbaiCmd;
			BlogArchiveInfo info;
			
			gbaiCmd = (GetBlogArchiveInfoCmd) cmd;
			info = GwtBlogHelper.getBlogArchiveInfo( this, gbaiCmd.getFolderId() );
			response = new VibeRpcResponse( info );
			return response;
		}
		
		case GET_BLOG_PAGES:
		{
			GetBlogPagesCmd gbpCmd;
			BlogPages blogPages;
			
			gbpCmd = (GetBlogPagesCmd) cmd;
			blogPages = GwtBlogHelper.getBlogPages( this, gbpCmd.getFolderId() );
			response = new VibeRpcResponse( blogPages );
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
		
		case GET_DOWNLOAD_FILE_URL:
		{
			GetDownloadFileUrlCmd gdfuCmd = ((GetDownloadFileUrlCmd) cmd);
			String result = GwtServerHelper.getDownloadFileUrl( getRequest( ri ), this, gdfuCmd.getBinderId(), gdfuCmd.getEntryId() );
			StringRpcResponseData responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
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
		
		case GET_EMAIL_NOTIFICATION_INFORMATION:
		{
			GetEmailNotificationInfoCmd geniCmd = ((GetEmailNotificationInfoCmd) cmd);
			EmailNotificationInfoRpcResponseData result = GwtEmailHelper.getEmailNotificationInfo( this, getRequest( ri ), geniCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ENTITY_ACTION_TOOLBAR_ITEMS:
		{
			GetEntityActionToolbarItemsCmd gftiCmd = ((GetEntityActionToolbarItemsCmd) cmd);
			GetToolbarItemsRpcResponseData responseData = GwtMenuHelper.getEntityActionToolbarItems( this, getRequest( ri ), gftiCmd.getEntityId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ENTRY:
		{
			GetEntryCmd geCmd;
			GwtFolderEntry result;
			
			geCmd = (GetEntryCmd) cmd;
			result = getEntry( ri, geCmd.getZoneUUId(), geCmd.getEntryId(), geCmd.getNumReplies(), geCmd.getFileAttachmentsValue() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ENTRY_COMMENTS:
		{
			GetEntryCommentsCmd gecCmd;
			List<ActivityStreamEntry> listOfComments;
			ActivityStreamEntryListRpcResponseData result;
			
			gecCmd = (GetEntryCommentsCmd) cmd;
			listOfComments = getEntryComments( ri, gecCmd.getEntryId() );
			result = new ActivityStreamEntryListRpcResponseData( listOfComments );
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
		
		case GET_ENTRY_TYPES:
		{
			GetEntryTypesCmd getCmd = ((GetEntryTypesCmd) cmd);
			EntryTypesRpcResponseData result = GwtViewHelper.getEntryTypes( this, getRequest( ri ), getCmd.getEntityId(), getCmd.getBinderIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_EXECUTE_JSP_URL:
		{
			GetExecuteJspUrlCmd gejuCmd;
			StringRpcResponseData responseData;
			String result;
			
			gejuCmd = (GetExecuteJspUrlCmd) cmd;
			result = GwtServerHelper.getExecuteJspUrl( getRequest( ri ), gejuCmd.getBinderId(), gejuCmd.getJspName() );
			responseData = new StringRpcResponseData( result );
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
		
		case GET_FILE_URL:
		{
			GetFileUrlCmd gfuCmd;
			String url;
			
			gfuCmd = (GetFileUrlCmd) cmd;
			url = GwtServerHelper.getFileUrl( this, getRequest( ri ), gfuCmd.getBinderId(), gfuCmd.getFileName() );
			response = new VibeRpcResponse( new StringRpcResponseData( url ) );
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
		
		case GET_FOLDER_COLUMNS:
		{
			GetFolderColumnsCmd gfcCmd = ((GetFolderColumnsCmd) cmd);
			FolderColumnsRpcResponseData responseData = GwtViewHelper.getFolderColumns(
				this,
				getRequest( ri ),
				gfcCmd.getFolderInfo(),
				gfcCmd.isIncludeConfigurationInfo() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_FOLDER_DISPLAY_DATA:
		{
			GetFolderDisplayDataCmd gfddCmd = ((GetFolderDisplayDataCmd) cmd);
			FolderDisplayDataRpcResponseData responseData = GwtViewHelper.getFolderDisplayData(
				this,
				getRequest( ri ),
				gfddCmd.getFolderInfo() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_FOLDER_ENTRIES:
		{
			GetFolderEntriesRpcResponseData responseData;
			GetFolderEntriesCmd gfeCmd;
			ArrayList<GwtFolderEntry> result;
			
			gfeCmd = (GetFolderEntriesCmd) cmd;
			result = getFolderEntries( ri, gfeCmd.getZoneUUID(), gfeCmd.getFolderId(), gfeCmd.getNumEntries(), gfeCmd.getNumReplies(), gfeCmd.getFileAttachmentsValue() );
			responseData = new GetFolderEntriesRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FOLDER_ROWS:
		{
			GetFolderRowsCmd gfrCmd = ((GetFolderRowsCmd) cmd);
			FolderRowsRpcResponseData responseData = GwtViewHelper.getFolderRows(
				this,
				getRequest( ri ),
				gfrCmd.getFolderInfo(),
				gfrCmd.getFolderColumns(),
				gfrCmd.getStart(),
				gfrCmd.getLength(),
				gfrCmd.getQuickFilter() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_FOLDER_SORT_SETTING:
		{
			GetFolderSortSettingCmd gfssCmd;
			FolderSortSetting folderSortSetting;
			GetFolderSortSettingRpcResponseData responseData;
			
			gfssCmd = (GetFolderSortSettingCmd) cmd;
			folderSortSetting = getFolderSortSetting( gfssCmd.getBinderId() );
			responseData = new GetFolderSortSettingRpcResponseData( folderSortSetting );
			response = new VibeRpcResponse( responseData );
			return response;
	
		}
		
		case GET_FOLDER_TOOLBAR_ITEMS:
		{
			GetFolderToolbarItemsCmd gftiCmd = ((GetFolderToolbarItemsCmd) cmd);
			GetFolderToolbarItemsRpcResponseData responseData = GwtMenuHelper.getFolderToolbarItems( this, getRequest( ri ), gftiCmd.getFolderInfo() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FOOTER_TOOLBAR_ITEMS:
		{
			GetFooterToolbarItemsCmd gftiCmd;
			List<ToolbarItem> result;
			GetToolbarItemsRpcResponseData responseData;
			
			gftiCmd = ((GetFooterToolbarItemsCmd) cmd);
		    result = GwtMenuHelper.getFooterToolbarItems( this, getRequest( ri ), gftiCmd.getEntityId() );
			responseData = new GetToolbarItemsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
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
			GetGroupMembershipCmd ggmCmd;
			int totalNumberOfMembers;
			ArrayList<GwtTeamingItem> members;
			
			ggmCmd = (GetGroupMembershipCmd) cmd;
			String groupId = ggmCmd.getGroupId();
			members = new ArrayList<GwtTeamingItem>();
			totalNumberOfMembers = getGroupMembership(
												ri,
												members,
												groupId,
												ggmCmd.getOffset(),
												ggmCmd.getNumResults(),
												ggmCmd.getFilter() );
			GetGroupMembershipRpcResponseData responseData = new GetGroupMembershipRpcResponseData( members );
			responseData.setTotalNumberOfMembers( totalNumberOfMembers );
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
		
		case GET_HELP_URL:
		{
			GetHelpUrlCmd ghuCmd = ((GetHelpUrlCmd) cmd);
			String helpUrl = MiscUtil.getHelpUrl(ghuCmd.getGuideName(), ghuCmd.getPageId(), ghuCmd.getSectionId());
			return new VibeRpcResponse( new StringRpcResponseData( helpUrl ));
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
		
		case GET_INHERITED_LANDING_PAGE_PROPERTIES:
		{
			GetInheritedLandingPagePropertiesCmd gilppCmd;
			LandingPageProperties lpProperties;
			
			gilppCmd = (GetInheritedLandingPagePropertiesCmd) cmd;
			lpProperties = GwtServerHelper.getInheritedLandingPageProperties( this, gilppCmd.getBinderId(), getRequest( ri ) );
			response = new VibeRpcResponse( lpProperties );
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
		
		case GET_JSP_HTML:
		{
			HttpServletResponse resp;
			ServletContext servletContext;
			
			resp = getResponse( ri );
			servletContext = getServletContext( ri );
			GetJspHtmlCmd gjhCmd = ((GetJspHtmlCmd) cmd);
			JspHtmlRpcResponseData responseData = GwtViewHelper.getJspHtml(
				this,
				req,
				resp,
				servletContext,
				gjhCmd.getJspType(),
				gjhCmd.getModel() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_LANDING_PAGE_DATA:
		{
			GetLandingPageDataCmd glpdCmd;
			ConfigData lpConfigData;
			
			glpdCmd = (GetLandingPageDataCmd) cmd;
			lpConfigData = GwtServerHelper.getLandingPageData( req, this, glpdCmd.getBinderId() );
			response = new VibeRpcResponse( lpConfigData );
			return response;
		}
		
		case GET_LIST_OF_CHILD_BINDERS:
		{
			GetListOfChildBindersCmd glocbCmd;
			GetListOfChildBindersRpcResponseData responseData;
			ArrayList<TreeInfo> listOfChildBinders;
			
			glocbCmd = (GetListOfChildBindersCmd) cmd;
			listOfChildBinders = GwtServerHelper.getListOfChildBinders( req, this, glocbCmd.getBinderId() );
			responseData = new GetListOfChildBindersRpcResponseData( listOfChildBinders );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_LIST_OF_FILES:
		{
			GetListOfFilesCmd glofCmd;
			GetListOfFilesRpcResponseData responseData;
			ArrayList<GwtAttachment> listOfFiles;
			
			// Get a list of files from the given folder.
			glofCmd = (GetListOfFilesCmd) cmd;
			listOfFiles = getListOfFiles( ri, glofCmd.getZoneUUID(), glofCmd.getFolderId(), glofCmd.getNumFiles() );
			responseData = new GetListOfFilesRpcResponseData( listOfFiles );
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
		
		case GET_MAIN_PAGE_INFO:
		{
			GetMainPageInfoCmd gcwCmd = ((GetMainPageInfoCmd) cmd);
			MainPageInfoRpcResponseData result = GwtServerHelper.getMainPageInfo( this, getRequest( ri ), gcwCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			
			// The GetMainPageInfoCmd should only be called once when we start up.  Set the user's
			// timezone to the timezone being used by the browser
			GwtServerHelper.setUserTimezone(
											this,
											gcwCmd.getTimeZoneOffset() );
			
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
		
		case GET_MY_TASKS:
		{
			List<TaskInfo> results;
			TaskInfoListRpcResponseData responseData;

			results = getMyTasks( ri );
			responseData = new TaskInfoListRpcResponseData( results );
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
		
		case GET_NET_FOLDER:
		{
			GetNetFolderCmd gnfCmd;
			NetFolder netFolder;
			
			gnfCmd = (GetNetFolderCmd) cmd;
			netFolder = GwtNetFolderHelper.getNetFolder( this, gnfCmd.getId() );
			response = new VibeRpcResponse( netFolder );
			return response;
		}
		
		case GET_NEXT_PREVIOUS_FOLDER_ENTRY_INFO:
		{
			GetNextPreviousFolderEntryInfoCmd gnpfeiCmd = ((GetNextPreviousFolderEntryInfoCmd) cmd);
			ViewFolderEntryInfoRpcResponseData result = GwtViewHelper.getNextPreviousFolderInfo( this, getRequest( ri ), gnpfeiCmd.getEntityId(), gnpfeiCmd.isPrevious() );
			response = new VibeRpcResponse( result );
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
			
			prefs = GwtServerHelper.getPersonalPreferences( this, getRequest( ri ) );
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
		
		case GET_PROFILE_ENTRY_INFO:
		{
			GetProfileEntryInfoCmd gcwCmd = ((GetProfileEntryInfoCmd) cmd);
			ProfileEntryInfoRpcResponseData result = GwtViewHelper.getProfileEntryInfo(
				this,
				getRequest( ri ),
				gcwCmd.getUserId() );
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
		
		case GET_PROJECT_INFO:
		{
			GetProjectInfoCmd gpiCmd;
			ProjectInfo result;
			
			gpiCmd = (GetProjectInfoCmd) cmd;
			result = GwtServerHelper.getProjectInfo( this, gpiCmd.getBinderId() );
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
			List<RecentPlaceInfo> result = GwtMenuHelper.getRecentPlaces( this, getRequest( ri ), ((GetRecentPlacesCmd) cmd).getBinderId() );
			GetRecentPlacesRpcResponseData responseData = new GetRecentPlacesRpcResponseData( result );
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

		case GET_SEND_TO_FRIEND_URL:
		{
			GetSendToFriendUrlCmd gstfuCmd;
			String url;
			StringRpcResponseData responseData;
			
			gstfuCmd = (GetSendToFriendUrlCmd) cmd;
			url = getSendToFriendUrl( ri, gstfuCmd.getEntryId() );
			responseData = new StringRpcResponseData( url );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_SHARING_INFO:
		{
			GetSharingInfoCmd gsiCmd;
			GwtSharingInfo sharingInfo;
			
			gsiCmd = (GetSharingInfoCmd) cmd;
			sharingInfo = GwtShareHelper.getSharingInfo( this, gsiCmd.getListOfEntities() );
			response = new VibeRpcResponse( sharingInfo );
			return response;
		}
		
		case GET_SIGN_GUESTBOOK_URL:
		{
			GetSignGuestbookUrlCmd gsgbUrlCmd = ((GetSignGuestbookUrlCmd) cmd);
			StringRpcResponseData responseData = GwtMenuHelper.getSignGuestbookUrl(
				this,
				getRequest( ri ),
				gsgbUrlCmd.getFolderId() );
			return new VibeRpcResponse( responseData );
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
		
		case GET_SYSTEM_BINDER_PERMALINK:
		{
			GetSystemBinderPermalinkCmd gsbpCmd;
			String permalink;
			StringRpcResponseData responseData;
			
			gsbpCmd = (GetSystemBinderPermalinkCmd) cmd;
			permalink = GwtServerHelper.getSystemBinderPermalink(
														getRequest( ri ),
														gsbpCmd.getSystemBinderType() );
			responseData = new StringRpcResponseData( permalink );
			response = new VibeRpcResponse( responseData );
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
			TaskBundle results = getTaskBundle( ri, gtbCmd.isEmbeddedInJSP(), gtbCmd.getBinderId(), gtbCmd.getFilterType(), gtbCmd.getModeType() );
			TaskBundleRpcResponseData responseData = new TaskBundleRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TASK_DISPLAY_DATA:
		{
			GetTaskDisplayDataCmd gtddCmd = ((GetTaskDisplayDataCmd) cmd);
			TaskDisplayDataRpcResponseData responseData = GwtTaskHelper.getTaskDisplayData( getRequest( ri ), this, gtddCmd.getBinderId() );
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
			List<TaskListItem> results = getTaskList( ri, gtlCmd.getApplyUsersFilter(), gtlCmd.getZoneUUID(), gtlCmd.getBinderId(), gtlCmd.getFilterType(), gtlCmd.getModeType() );
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
			result = GwtMenuHelper.getTeamManagementInfo( this, getRequest( ri ), gtmiCmd.getBinderId() );
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
			GetToolbarItemsCmd gtiCmd = ((GetToolbarItemsCmd) cmd);
			List<ToolbarItem> result = GwtMenuHelper.getToolbarItems( this, getRequest( ri ), gtiCmd.getBinderId() );
			GetToolbarItemsRpcResponseData responseData = new GetToolbarItemsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TOP_RANKED:
		{
			List<TopRankedInfo> result = GwtServerHelper.getTopRankedFromCache( this, getRequest( ri ));
			GetTopRankedRpcResponseData responseData = new GetTopRankedRpcResponseData( result );
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
		
		case GET_USER_ACCESS_CONFIG:
		{
			UserAccessConfig config;
			
			config = GwtServerHelper.getUserAccessConfig( this, req );
			response = new VibeRpcResponse( config );
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
		
		case GET_USER_WORKSPACE_INFO:
		{
			GetUserWorkspaceInfoCmd gusCmd = ((GetUserWorkspaceInfoCmd) cmd);
			UserWorkspaceInfoRpcResponseData result = getUserWorkspaceInfo( ri, gusCmd.getBinderId() );
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
		
		case GET_VIEW_FILE_URL:
		{
			GetViewFileUrlCmd gvfuCmd = ((GetViewFileUrlCmd) cmd);
			String result = getViewFileUrl( ri, gvfuCmd.getViewFileInfo() );
			StringRpcResponseData responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
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
			result = GwtServerHelper.getViewFolderEntryUrl( this, getRequest( ri ), binderId, entryId );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case GET_WHO_HAS_ACCESS:
		{
			GetWhoHasAccessCmd gwhaCmd = ((GetWhoHasAccessCmd) cmd);
			WhoHasAccessInfoRpcResponseData result = GwtViewHelper.getWhoHasAccess( this, getRequest( ri ), gwhaCmd.getEntityId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_WORKSPACE_CONTRIBUTOR_IDS:
		{
			GetWorkspaceContributorIdsCmd gwciCmd;
			GetWorkspaceContributorIdsRpcResponseData responseData;
			ArrayList<Long> listOfIds;
			
			// Get a list of contributor ids for the given workspace.
			gwciCmd = (GetWorkspaceContributorIdsCmd) cmd;
			listOfIds = getWorkspaceContributorIds( ri,gwciCmd.getWorkspaceId() );
			responseData = new GetWorkspaceContributorIdsRpcResponseData( listOfIds );
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

		case IMPORT_ICAL_BY_URL:
		{
			ImportIcalByUrlCmd iiUrlCmd = ((ImportIcalByUrlCmd) cmd);
			ImportIcalByUrlRpcResponseData result = GwtServerHelper.importIcalByUrl( this, getRequest( ri ), iiUrlCmd.getFolderId(), iiUrlCmd.getUrl() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case IS_ALL_USERS_GROUP:
		{
			String groupId = ((IsAllUsersGroupCmd) cmd).getGroupId();
			Boolean result = isAllUsersGroup( ri, groupId );	//Note, this checks for either allUsers or allExtUsers
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

		case LOCK_ENTRIES:
		{
			LockEntriesCmd leCmd = ((LockEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.lockEntries( this, getRequest( ri ), leCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
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
		
		case MODIFY_NET_FOLDER:
		{
			ModifyNetFolderCmd mnfCmd;
			
			mnfCmd = (ModifyNetFolderCmd) cmd;
			GwtNetFolderHelper.modifyNetFolder( this, mnfCmd.getNetFolder() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( Boolean.TRUE ) );
			
			return response;
		}
		
		case MODIFY_NET_FOLDER_ROOT:
		{
			ModifyNetFolderRootCmd mnfrCmd;
			
			mnfrCmd = (ModifyNetFolderRootCmd) cmd;
			GwtNetFolderHelper.modifyNetFolderRoot( this, mnfrCmd.getNetFolderRoot() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( Boolean.TRUE ) );
			
			return response;
		}
		
		case MOVE_ENTRIES:
		{
			MoveEntriesCmd meCmd = ((MoveEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.moveEntries( this, getRequest( ri ), meCmd.getTargetFolderId(), meCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
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
		
		case PIN_ENTRY:
		{
			PinEntryCmd peCmd = ((PinEntryCmd) cmd);
			Boolean result = GwtServerHelper.pinEntry( this, getRequest( ri ), peCmd.getFolderId(), peCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case PURGE_FOLDER_ENTRIES:
		{
			PurgeFolderEntriesCmd pfeCmd = ((PurgeFolderEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtServerHelper.purgeFolderEntries( this, getRequest( ri ), pfeCmd.getEntityIds(), pfeCmd.getDeleteMirroredSource() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PURGE_TASKS:
		{
			PurgeTasksCmd dtCmd = ((PurgeTasksCmd) cmd);
			ErrorListRpcResponseData responseData = purgeTasks( ri, dtCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PURGE_USERS:
		{
			PurgeUsersCmd puCmd = ((PurgeUsersCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.purgeUsers( this, getRequest( ri ), puCmd.getUserIds(), puCmd.getPurgeMirrored() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PURGE_USER_WORKSPACES:
		{
			PurgeUserWorkspacesCmd puwCmd = ((PurgeUserWorkspacesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.purgeUserWorkspaces( this, getRequest( ri ), puwCmd.getUserIds(), puwCmd.getPurgeMirrored() );
			response = new VibeRpcResponse( responseData );
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
			result = GwtServerHelper.removeFavorite( this, getRequest( ri ), rfCmd.getFavoriteId() );
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
		
		case SAVE_ACCESSORY_STATUS:
		{
			SaveAccessoryStatusCmd sasCmd = ((SaveAccessoryStatusCmd) cmd);
			Boolean responseData = GwtViewHelper.saveAccessoryStatus(
				this,
				getRequest( ri ),
				sasCmd.getBinderId(),
				sasCmd.getShowAccessoryPanel() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( responseData ) );
			return response;
		}
		
		case SAVE_BINDER_REGION_STATE:
		{
			SaveBinderRegionStateCmd sbrsCmd = ((SaveBinderRegionStateCmd) cmd);
			BooleanRpcResponseData responseData = GwtViewHelper.saveBinderRegionState(
				this,
				getRequest( ri ),
				sbrsCmd.getBinderId(),
				sbrsCmd.getRegionId(),
				sbrsCmd.getRegionState() );
			return new VibeRpcResponse( responseData );
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
		
		case SAVE_CALENDAR_DAY_VIEW:
		{
			SaveCalendarDayViewCmd scdvCmd = ((SaveCalendarDayViewCmd) cmd);
			CalendarDisplayDataRpcResponseData result = GwtCalendarHelper.saveCalendarDayView(
				this,
				getRequest( ri ),
				scdvCmd.getFolderInfo(),
				scdvCmd.getDayView(),
				scdvCmd.getDate() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_CALENDAR_HOURS:
		{
			SaveCalendarHoursCmd schCmd = ((SaveCalendarHoursCmd) cmd);
			CalendarDisplayDataRpcResponseData result = GwtCalendarHelper.saveCalendarHours( this, getRequest( ri ), schCmd.getFolderInfo(), schCmd.getHours() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_CALENDAR_SETTINGS:
		{
			SaveCalendarSettingsCmd scsCmd = ((SaveCalendarSettingsCmd) cmd);
			Boolean result = GwtCalendarHelper.saveCalendarSettings( this, getRequest( ri ), scsCmd.getFolderId(), scsCmd.getWeekStart(), scsCmd.getWorkDayStart() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SAVE_CALENDAR_SHOW:
		{
			SaveCalendarShowCmd scsCmd = ((SaveCalendarShowCmd) cmd);
			CalendarDisplayDataRpcResponseData result = GwtCalendarHelper.saveCalendarShow( this, getRequest( ri ), scsCmd.getFolderInfo(), scsCmd.getShow() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_CLIPBOARD_USERS:
		{
			SaveClipboardUsersCmd scuCmd = ((SaveClipboardUsersCmd) cmd);
			BooleanRpcResponseData result = GwtServerHelper.saveClipboardUsers( this, getRequest( ri ), scuCmd.getClipboardUserList() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_COLUMN_WIDTHS:
		{
			SaveColumnWidthsCmd scwCmd = ((SaveColumnWidthsCmd) cmd);
			BooleanRpcResponseData result = GwtViewHelper.saveColumnWidths(
				this,
				getRequest( ri ),
				scwCmd.getFolderInfo(),
				scwCmd.getColumnWidths() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_EMAIL_NOTIFICATION_INFORMATION:
		{
			SaveEmailNotificationInfoCmd seniCmd = ((SaveEmailNotificationInfoCmd) cmd);
			BooleanRpcResponseData result = GwtEmailHelper.saveEmailNotificationInfo(
				this,
				getRequest( ri ),
				seniCmd.getBinderId(),	// null -> Entry subscription mode.
				seniCmd.getEntityIds(),	// null -> Binder email notification mode.
				seniCmd.getOverridePresets(),
				seniCmd.getDigestAddressTypes(),
				seniCmd.getMsgAddressTypes(),
				seniCmd.getMsgNoAttAddressTypes(),
				seniCmd.getTextAddressTypes() );
			response = new VibeRpcResponse( result );
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
		
		case SAVE_FOLDER_COLUMNS:
		{
			SaveFolderColumnsCmd sfcCmd = ((SaveFolderColumnsCmd) cmd);
			Boolean result = saveFolderColumns( ri, sfcCmd.getFolderId(), sfcCmd.getFolderColumns(), sfcCmd.isFolderColumnsDefault() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_FOLDER_PINNING_STATE:
		{
			SaveFolderPinningStateCmd sfpsCmd = ((SaveFolderPinningStateCmd) cmd);
			GwtViewHelper.saveUserViewPinnedEntries( getRequest( ri ), sfpsCmd.getFolderId(), sfpsCmd.getViewPinnedEntries() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( true ));
			return response;
		}
		
		case SAVE_FOLDER_SORT:
		{
			SaveFolderSortCmd sfsCmd = ((SaveFolderSortCmd) cmd);
			Boolean result = saveFolderSort( ri, sfsCmd.getBinderInfo(), sfsCmd.getSortKey(), sfsCmd.getSortAscending() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_SHARED_FILES_STATE:
		{
			SaveSharedFilesStateCmd ssfsCmd = ((SaveSharedFilesStateCmd) cmd);
			GwtViewHelper.saveUserViewSharedFiles( getRequest( ri ), ssfsCmd.getCollectionType(), ssfsCmd.getViewSharedFiles() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( true ));
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
		
		case SAVE_TASK_GRAPH_STATE:
		{
			SaveTaskGraphStateCmd stgsCmd = ((SaveTaskGraphStateCmd) cmd);
			BooleanRpcResponseData responseData = GwtTaskHelper.saveTaskGraphState( getRequest( ri ), this, stgsCmd.getFolderId(), stgsCmd.getExpandGraphs() );
			response = new VibeRpcResponse( responseData );
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
			Boolean result = saveFolderSort( ri, stsCmd.getBinderInfo(), stsCmd.getSortKey(), stsCmd.getSortAscending() );
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
			SaveSearchCmd ssCmd = ((SaveSearchCmd) cmd);
			SavedSearchInfo result = GwtServerHelper.saveSearch( this, getRequest( ri ), ssCmd.getSearchTabId(), ssCmd.getSavedSearchInfo() );
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
		
		case SAVE_USER_ACCESS_CONFIG:
		{
			SaveUserAccessConfigCmd suacCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			suacCmd = (SaveUserAccessConfigCmd) cmd;
			result = GwtServerHelper.saveUserAccessConfig( this, suacCmd.getConfig() );
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

		case SET_ENTRIES_PIN_STATE:
		{
			SetEntriesPinStateCmd sepsCmd = ((SetEntriesPinStateCmd) cmd);
			Boolean result = GwtServerHelper.setEntriesPinState( this, getRequest( ri ), sepsCmd.getEntryIds(), sepsCmd.getPinned() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
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
			ShareEntryCmd seCmd;
			GwtShareEntryResults results;

			seCmd = ((ShareEntryCmd) cmd);
			results = GwtShareHelper.shareEntry( this, seCmd.getSharingInfo() );
			ShareEntryResultsRpcResponseData responseData = new ShareEntryResultsRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SYNC_NET_FOLDERS:
		{
			SyncNetFoldersCmd snfCmd;
			Set<NetFolder> listOfNetFolders;
			
			snfCmd = (SyncNetFoldersCmd) cmd;
			listOfNetFolders = GwtNetFolderHelper.syncNetFolders( this, req, snfCmd.getListOfNetFoldersToSync() );
			response = new VibeRpcResponse( new SyncNetFoldersRpcResponseData( listOfNetFolders ) );
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
		
		case TEST_NET_FOLDER_CONNECTION:
		{
			TestNetFolderConnectionCmd tcCmd;
			TestNetFolderConnectionResponse responseData;
			
			tcCmd = (TestNetFolderConnectionCmd) cmd;
			responseData = GwtNetFolderHelper.testNetFolderConnection(
																tcCmd.getRootName(),
																tcCmd.getRootType(),
																tcCmd.getRootPath(),
																tcCmd.getSubPath(),
																tcCmd.getProxyName(),
																tcCmd.getProxyPwd() );
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
		
		case TRASH_PURGE_ALL:
		{
			TrashPurgeAllCmd tpaCmd = ((TrashPurgeAllCmd) cmd);
			StringRpcResponseData responseData = GwtViewHelper.trashPurgeAll( 
				this,
				getRequest( ri ),
				tpaCmd.getBinderId(),
				tpaCmd.getPurgeMirroredSources() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TRASH_PURGE_SELECTED_ENTRIES:
		{
			TrashPurgeSelectedEntriesCmd tpseCmd = ((TrashPurgeSelectedEntriesCmd) cmd);
			StringRpcResponseData responseData = GwtViewHelper.trashPurgeSelectedEntries(
				this,
				getRequest( ri ),
				tpseCmd.getBinderId(),
				tpseCmd.getPurgeMirroredSources(),
				tpseCmd.getTrashSelectionData() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TRASH_RESTORE_ALL:
		{
			TrashRestoreAllCmd traCmd = ((TrashRestoreAllCmd) cmd);
			StringRpcResponseData responseData = GwtViewHelper.trashRestoreAll(
				this,
				getRequest( ri ),
				traCmd.getBinderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TRASH_RESTORE_SELECTED_ENTRIES:
		{
			TrashRestoreSelectedEntriesCmd trseCmd = ((TrashRestoreSelectedEntriesCmd) cmd);
			StringRpcResponseData responseData = GwtViewHelper.trashRestoreSelectedEntries(
				this,
				getRequest( ri ),
				trseCmd.getBinderId(),
				trseCmd.getTrashSelectionData() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UNLOCK_ENTRIES:
		{
			UnlockEntriesCmd uleCmd = ((UnlockEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.unlockEntries( this, getRequest( ri ), uleCmd.getEntityIds() );
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
		
		case UPDATE_CALENDAR_EVENT:
		{
			UpdateCalendarEventCmd uceCmd = ((UpdateCalendarEventCmd) cmd);
			Boolean result = GwtCalendarHelper.updateCalendarEvent( this, getRequest( ri ), uceCmd.getFolderId(), uceCmd.getEvent() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
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
		
		case UPLOAD_FILE_BLOB:
		{
			UploadFileBlobCmd ufbCmd = ((UploadFileBlobCmd) cmd);
			StringRpcResponseData result = GwtViewHelper.uploadFileBlob( this, getRequest( ri ), ufbCmd.getFolderInfo(), ufbCmd.getFileBlob(), ufbCmd.isLastBlob() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case UNPIN_ENTRY:
		{
			UnpinEntryCmd upeCmd = ((UnpinEntryCmd) cmd);
			Boolean result = GwtServerHelper.unpinEntry( this, getRequest( ri ), upeCmd.getFolderId(), upeCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
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
		
		case VALIDATE_EMAIL_ADDRESS:
		{
			ValidateEmailAddressCmd vemCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			vemCmd = (ValidateEmailAddressCmd) cmd;
			result = GwtServerHelper.validateEmailAddress( vemCmd.getEmailAddress(), vemCmd.getAddressField() );
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
		
		case VALIDATE_UPLOADS:
		{
			ValidateUploadsCmd veaCmd  = ((ValidateUploadsCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.validateUploads( this, getRequest( ri ), veaCmd.getFolderInfo(), veaCmd.getUploads() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		}
		
		String details = ("Unknown command: " + cmdEnum.name() + " (" +cmd.getClass().getName() + ")");
		m_logger.warn( "In GwtRpcServiceImpl.executeCommand():  " + details);
		throw new GwtTeamingException(ExceptionType.NO_RPC_HANDLER, details);
	}
	
	
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
	private ErrorListRpcResponseData deleteTasks( HttpRequestInfo ri, List<EntityId> taskIds ) throws GwtTeamingException {
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
	private ErrorListRpcResponseData purgeTasks( HttpRequestInfo ri, List<EntityId> taskIds ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.purgeTasks()");
		try {
			return GwtTaskHelper.purgeTasks( getRequest( ri ), this, taskIds );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.purgeTasks()");
		}
	}
	
	/*
	 * Returns a ActivityStreamData of corresponding to activity stream
	 * parameters, paging data and info provided.
	 */
	private ActivityStreamData getActivityStreamData( HttpRequestInfo ri, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pagingData, ActivityStreamDataType asdt, SpecificFolderData sfData )
	{
		return GwtActivityStreamHelper.getActivityStreamData( getRequest( ri ), this, asp, asi, pagingData, asdt, sfData );
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
	 * Return a list of contributor ids for the given workspace.
	 * The list of contributors will include
	 * 1. The creator of the workspace
	 * 2. The owner of the workspace
	 * 3. The last user to modify the workspace.
	 */
	private ArrayList<Long> getWorkspaceContributorIds( HttpRequestInfo ri, Long workspaceId )
	{
		String[] ids;
		ArrayList<Long> listOfIds;
		Workspace workspace = null;
		
		listOfIds = new ArrayList<Long>();
		
		try
		{
			workspace = getWorkspaceModule().getWorkspace( workspaceId );
		}
		catch (Exception ex)
		{
			// Nothing to do
		}
		
		if ( workspace != null )
		{
			// Get the list of contributor ids for the given workspace.
			ids = WorkspaceTreeHelper.collectContributorIds( workspace );
			if ( ids != null )
			{
				for (String id: ids)
				{
					listOfIds.add( Long.valueOf( id ) );
				}
			}
		}
		
		return listOfIds;
	}
	
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
	@SuppressWarnings({ "unchecked" })
	private GwtFolderEntry getEntry( HttpRequestInfo ri, String zoneUUID, String entryId, int numRepliesToGet, boolean getFileAttachments ) throws GwtTeamingException
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
				
				// Get the entry's description
				{
					Description desc;
					
					desc = entry.getDescription();
					if ( desc != null )
					{
						String descStr;
						
						descStr = desc.getText();
						
						// Perform any fixups needed on the entry's description
						descStr = markupStringReplacement( ri, entry, descStr, "view" );
						
						folderEntry.setEntryDesc( descStr );
					}
				}

				parentBinderId = null;
				parentBinder = entry.getParentBinder();
				if ( parentBinder != null )
				{
					parentBinderId = parentBinder.getId();
					folderEntry.setParentBinderName( parentBinder.getPathName() );
					folderEntry.setParentBinderId( parentBinderId );
				}
				
				// Create a url that can be used to view this entry.
				url = GwtServerHelper.getViewFolderEntryUrl( this, getRequest( ri ), parentBinderId, entryIdL );
				folderEntry.setViewEntryUrl( url );
				
				// Get the entry's author information
				{
					Long authorId;
					
					authorId = entry.getOwnerId();
					if ( authorId != null )
					{
						// Get the author's name
						{
							String authorName;
							String authorIdS;
							ArrayList<String> authorIdList;
							List resolvedList;
							
							authorIdS = authorId.toString();
							folderEntry.setAuthorId( authorIdS );
	
							// Does the user have rights to see the name of the author?
							authorIdList = new ArrayList<String>();
							authorIdList.add( authorIdS );
							resolvedList = ResolveIds.getPrincipals( authorIdList );
							if ( MiscUtil.hasItems( resolvedList ) )
							{
								User author;
								
								// Yes
								author = (User) resolvedList.get( 0 );
								authorName = author.getTitle();
							}
							else
							{
								// No
								authorName = NLT.get( "user.redacted.title" );
							}
							
							folderEntry.setAuthor( authorName );
						}
						
						// Get the author's workspace id
						{
							SortedSet<Principal> authorPrincipals;
							ArrayList<Long> authorIds;

							authorIds = new ArrayList<Long>();
							authorIds.add( authorId );
							try
							{
								authorPrincipals = getProfileModule().getPrincipals( authorIds );
								
								if ( MiscUtil.hasItems( authorPrincipals ) )
								{
									Principal authorPrincipal;
									
									authorPrincipal = authorPrincipals.first();
									if ( authorPrincipal != null )
									{
										Long workspaceId;
										
										workspaceId = authorPrincipal.getWorkspaceId();
										if ( workspaceId != null )
											folderEntry.setAuthorWorkspaceId( workspaceId.toString() );
									}
								}
							}
							catch ( Exception ex )
							{
								// Nothing to do.
							}
						}
					}
				}
				
				// Get the entry's modification date
				{
					Date date;
					String dateStr;
					
					date = entry.getLastActivity();
					dateStr = GwtServerHelper.getDateTimeString( date );
					
					folderEntry.setModificationDate( dateStr );
				}
				
				// Do we need to get the ids of any replies to this entry?
				if ( numRepliesToGet > 0 )
				{
					Map replies;
					
					// Get the replies to this entry.
					replies = folderModule.getEntryTree( parentBinderId, entryIdL, false );
					if ( replies != null )
					{
						List<FolderEntry> replyList;

						replyList = (List<FolderEntry>) replies.get( ObjectKeys.FOLDER_ENTRY_DESCENDANTS );
						if ( replyList != null && replyList.size() > 0 )
						{
							int i;
							
							for (i = 0; i < replyList.size() && i < numRepliesToGet; ++i)
							{
								FolderEntry replyEntry;
								Long replyId;
							
								// Get the next reply entry.
								replyEntry = replyList.get( i );
								replyId = replyEntry.getId();
								if ( replyId != null )
									folderEntry.addReplyId( replyId.toString() );
							}
						}
					}
				}
				
				// Do we need to get file attachment information?
				if ( getFileAttachments )
				{
					Set<Attachment> attachments;
					
					// Yes
					attachments = entry.getAttachments();
			    	for (Iterator iter=attachments.iterator(); iter.hasNext();)
			    	{
				    	Attachment att;
				    	
			    		att = (Attachment)iter.next();
			    		if ( att instanceof FileAttachment )
			    		{
					    	FileAttachment fatt;
					    	GwtAttachment gwtAttachment;
					    	String fileUrl;

					    	gwtAttachment = new GwtAttachment();
					    	
					    	fatt = (FileAttachment) att;
					    	
					    	gwtAttachment.setFileName( fatt.getFileItem().getName() );
					    	
					    	fileUrl = WebUrlUtil.getFileUrl( getRequest( ri ), WebKeys.ACTION_READ_FILE, fatt, false, true );
					    	gwtAttachment.setViewFileUrl( fileUrl );
					    	
					    	folderEntry.addAttachment( gwtAttachment );
			    		}
			    	}
				}
			}
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
		
		
		return folderEntry;
	}// end getEntry()
	
	
	/**
	 * Return a view file URL that can be used to view an entry's file
	 * as HTML.
	 * 
	 * @param ri
	 * @param vfi
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	private String getViewFileUrl( HttpRequestInfo ri, ViewFileInfo vfi ) throws GwtTeamingException {
		return GwtServerHelper.getViewFileUrl( getRequest( ri ), vfi );
	}// end getViewFileUrl()
	
	/*
	 * Return a list of comments for the given entry.
	 */
	private List<ActivityStreamEntry> getEntryComments( HttpRequestInfo ri, String entryId )
	{
		return GwtActivityStreamHelper.getEntryComments( this, getRequest( ri ), Long.parseLong( entryId ) );
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
		return GwtServerHelper.getFolderImpl( this, getRequest( ri ), zoneUUID, folderId, null );
	}
	
	/**
	 * Return a list of the first n entries in the given folder.
	 * 
	 * @throws GwtTeamingException 
	 */
	@SuppressWarnings({ "unchecked" })
	private ArrayList<GwtFolderEntry> getFolderEntries( HttpRequestInfo ri, String zoneUUID, String folderId, int numEntriesToRead, int numReplies, boolean getFileAttachments ) throws GwtTeamingException
	{
		ArrayList<GwtFolderEntry> entries;
		
		entries = new ArrayList<GwtFolderEntry>();
		
		try
		{
			ZoneInfo zoneInfo;
			String zoneInfoId;
			Long folderIdL;
			FolderModule folderModule;
			Map options;
			Map searchResults;
			List<Map> folderEntries;
			int totalEntries;
			
			// Get the id of the zone we are running in.
			zoneInfo = MiscUtil.getCurrentZone();
			zoneInfoId = zoneInfo.getId();
			if ( zoneInfoId == null )
				zoneInfoId = "";

			folderModule = getFolderModule();

			folderIdL = new Long( folderId );
			
			// Are we looking for a folder that was imported from another zone?
			if ( zoneUUID != null && zoneUUID.length() > 0 && !zoneInfoId.equals( zoneUUID ) )
			{
				// Yes, get the folder id for the folder in this zone.
				folderIdL = getBinderModule().getZoneBinderId( folderIdL, zoneUUID, EntityType.folder.name() );
			}

			// Get the ids of the first n entries in the given folder.
			options = new HashMap();
			options.put( ObjectKeys.SEARCH_SORT_DESCEND, Boolean.TRUE );
			options.put( ObjectKeys.SEARCH_SORT_BY, Constants.LASTACTIVITY_FIELD );
			options.put( ObjectKeys.SEARCH_MAX_HITS, Integer.valueOf( numEntriesToRead ) );
			searchResults = folderModule.getEntries( folderIdL, options );
			
			// Scan the entries we read
			folderEntries = (List<Map>) searchResults.get( ObjectKeys.SEARCH_ENTRIES );
			totalEntries = 0;
			for (Map entryMap: folderEntries)
			{
				GwtFolderEntry folderEntry;
				String entryId;

				// Have we reached the max number of entries to return?
				if ( totalEntries >= numEntriesToRead )
					break;
				
				// Get the entry id
				entryId = (String) entryMap.get( Constants.DOCID_FIELD );
				
				try
				{
					// Get a GwtFolderEntry from the given entry id.
					folderEntry = getEntry( ri, null, entryId, numReplies, getFileAttachments );
					entries.add( folderEntry );
					
					++totalEntries;
				}
				catch (Exception e)
				{
					// Nothing to do.
				}
			}
		}
		catch (Exception e)
		{
			throw GwtServerHelper.getGwtTeamingException( e );
		}
		
		return entries;
	}
	
	/**
	 * Return the sort setting for the given folder
	 */
	private FolderSortSetting getFolderSortSetting( Long binderId ) throws GwtTeamingException 
	{
		SimpleProfiler.start( "GwtRpcServiceImpl.getFolderSortSetting()" );
		try 
		{
			return GwtServerHelper.getFolderSortSetting( this, binderId );
		}
		finally 
		{
			SimpleProfiler.stop( "GwtRpcServiceImpl.getFolderSortSetting()" );
		}
	}
	
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
	 * Return the HttpServletResponse from an HttpRequestInfo object.
	 */
	private static HttpServletResponse getResponse( HttpRequestInfo ri )
	{
		return (HttpServletResponse) ri.getResponseObj();
	}
	
	/**
	 * Return the ServletContext from an HttpRequestInfo object.
	 */
	private static ServletContext getServletContext( HttpRequestInfo ri )
	{
		return (ServletContext) ri.getServletContext();
	}
	
	/**
	 * Return the url needed to invoke the "Send to friend" page.
	 */
	private String getSendToFriendUrl( HttpRequestInfo ri, String entryId ) throws GwtTeamingException
	{
		AdaptedPortletURL url;
		
		try
		{
			FolderEntry entry;
			Binder parentBinder;
			Long entryIdL;
			Long parentBinderId;

			url = new AdaptedPortletURL( getRequest( ri ), "ss_forum", true );
			url.setParameter( WebKeys.ACTION, WebKeys.ACTION_SEND_ENTRY_EMAIL );
			url.setParameter( WebKeys.URL_ENTRY_ID, entryId );

			entryIdL = new Long( entryId );
			entry = getFolderModule().getEntry( null, entryIdL );

			parentBinder = entry.getParentBinder();
			if ( parentBinder != null )
			{
				parentBinderId = parentBinder.getId();
				url.setParameter( WebKeys.URL_BINDER_ID, String.valueOf( parentBinderId ) );
			}
		}
		catch ( Exception ex )
		{
			throw GwtServerHelper.getGwtTeamingException( ex );
		}		


		return url.toString();
	}

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
		GwtBrandingData brandingData;
		
		try
		{
			Long topWorkspaceId;
			String binderId;
			
			// Get the top workspace.
			topWorkspaceId = getWorkspaceModule().getTopWorkspaceId();				
		
			// Get the branding data from the top workspace.
			binderId = topWorkspaceId.toString();
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
	private List<TaskListItem> getTaskList( HttpRequestInfo ri, boolean applyUsersFilter, String zoneUUID, Long binderId, String filterType, String modeType ) throws GwtTeamingException
	{
		SimpleProfiler.start("GwtRpcServiceImpl.getTaskList()");
		try {
			return GwtTaskHelper.getTaskList( getRequest( ri ), this, applyUsersFilter, false, GwtTaskHelper.getTaskBinder( this, zoneUUID, binderId ), filterType, modeType );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getTaskList()");
		}
	}// end getTaskList()

	/*
	 * Returns a TaskBundle object for the specified binder.
	 */
	private TaskBundle getTaskBundle( HttpRequestInfo ri, boolean embeddedInJSP, Long binderId, String filterType, String modeType ) throws GwtTeamingException
	{
		SimpleProfiler.start("GwtRpcServiceImpl.getTaskBundle()");
		try {
			return
				GwtTaskHelper.getTaskBundle(
					getRequest( ri ),
					this,
					(!embeddedInJSP),	// true -> Apply the user's filter directly.  From JSP, it's applied from the options cached by the controller.
					embeddedInJSP,
					GwtTaskHelper.getTaskBinder( this, null, binderId ),
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
			return GwtTaskHelper.getTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, null, binderId ) );
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
			return GwtTaskHelper.removeTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, null, binderId ) );
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
	private String saveTaskCompleted( HttpRequestInfo ri, List<EntityId> taskIds, String completed ) throws GwtTeamingException {
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
	private TaskEvent saveTaskDueDate( HttpRequestInfo ri, EntityId taskId, TaskEvent taskEvent ) throws GwtTeamingException {
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
			return GwtTaskHelper.saveTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, null, binderId ), taskLinkage );
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
	 * Save a folders sort options on the specified binder.
	 */
	private Boolean saveFolderColumns( HttpRequestInfo ri, String binderId, List<FolderColumn> fcList, 
			Boolean isDefault ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveFolderColumns()");
		try {
			return GwtServerHelper.saveFolderColumns( this, binderId, fcList, isDefault );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveFolderColumns()");
		}
	}
	
	/*
	 * Save a folders sort options on the specified binder.
	 */
	private Boolean saveFolderSort( HttpRequestInfo ri, BinderInfo binderInfo, String sortKey, boolean sortAscending ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveFolderSort()");
		try {
			return GwtServerHelper.saveFolderSort( this, binderInfo, sortKey, sortAscending );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveFolderSort()");
		}
	}
	
	/*
	 * Stores a status value on the specified tasks.
	 */
	private String saveTaskStatus( HttpRequestInfo ri, List<EntityId> taskIds, String status ) throws GwtTeamingException {
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
			return GwtTaskHelper.updateCalculatedDates( getRequest( ri ), this, GwtTaskHelper.getTaskBinder( this, null, binderId ), entryId );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.updateCalculatedDates()");
		}
	}

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
			List<Long> bindersList = new ArrayList<Long>();
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
			Long topWSId = getWorkspaceModule().getTopWorkspaceId();
			reply = String.valueOf( topWSId );
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
	private TreeInfo getVerticalTree( HttpRequestInfo ri, String binderIdS ) throws GwtTeamingException
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
			
			// ...and build the TreeInfo for the requested Binder.
			reply = GwtServerHelper.buildTreeInfoFromBinder(
				getRequest( ri ),
				this,
				binderWS,
				expandedBindersList );

			// Is the root binder a user workspace?
			BinderInfo bi = reply.getBinderInfo();
			if ((BinderType.WORKSPACE == bi.getBinderType()) && (WorkspaceType.USER == bi.getWorkspaceType())) {
				// Yes!  Can we get the title for the owner?
				String title = BinderHelper.getUserWorkspaceOwnerTitle(bi.getBinderIdAsLong());
				if (MiscUtil.hasString(title)) {
					// Yes!  Store it in the root TreeInfo.
					reply.setBinderTitle(title);
				}
			}
	
	
			// If the Binder supports Trash access...
			boolean allowTrash = TrashHelper.allowUserTrashAccess( GwtServerHelper.getCurrentUser() );
			if ( allowTrash && ( !(binder.isMirrored()) ) )
			{
				// ...add a TreeInfo to the reply's children for it.
				GwtServerHelper.addTrashFolder( this, reply, binder );
			}
			
			// Finally, if we're rooted on the current user's workspace...
			if (binderWS.getId().equals(GwtServerHelper.getCurrentUser().getWorkspaceId())) {
				// ...add the TreeInfo's for the collections we display
				// ...at the top of the tree.
				GwtServerHelper.addCollections( this, getRequest( ri ), reply );
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
		return GwtServerHelper.markupStringReplacementImpl( this, getRequest( ri ), binderId, html, type );
	}
	
	
	/*
	 * Parse the given html and replace any markup with the appropriate url.  For example,
	 * replace {{attachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
	 */
	private String markupStringReplacement( HttpRequestInfo ri, FolderEntry entry, String html, String type ) throws GwtTeamingException
	{
		String newHtml;
		
		newHtml = "";
		if ( html != null && html.length() > 0 )
		{
			try
			{
				// Parse the given html and replace any markup with the appropriate url.  For example,
				// replace {{atachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
				newHtml = MarkupUtil.markupStringReplacement( null, null, getRequest( ri ), null, entry, html, type );
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
		return GwtServerHelper.getBinderInfo( this, getRequest( ri ), binderId );
	}//end getBinderInfo()

	/**
	 * Returns a ViewInfo used to control folder views based on a URL.
	 *
	 * @param ri
	 * @param viCmd
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	private ViewInfo getViewInfo( HttpRequestInfo ri, GetViewInfoCmd viCmd ) throws GwtTeamingException
	{
		return GwtViewHelper.getViewInfo( this, getRequest( ri ), viCmd.getUrl() );
	}//end getViewInfo()

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
	
	/*
	 * Return a list of all the tasks assigned to the logged in user.
	 */
	private List<TaskInfo> getMyTasks( HttpRequestInfo ri )
	{
		SimpleProfiler.start( "GwtRpcServiceImpl.getMyTasks()" );
		try
		{
			return GwtTaskHelper.getMyTasks( this, getRequest( ri ) );
		}
		finally
		{
			SimpleProfiler.stop( "GwtRpcServiceImpl.getMyTasks()" );
		}
	}

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
	 * Return a list of files from the given folder.
	 */
	private ArrayList<GwtAttachment> getListOfFiles( HttpRequestInfo ri, String zoneUUID, String folderId, int numFiles ) throws GwtTeamingException
	{
		ArrayList<GwtAttachment> listOfFiles;
		ArrayList<GwtFolderEntry> entries;
		
		listOfFiles = new ArrayList<GwtAttachment>();
		
		// Get a list of the first n entries in the folder.
		entries = getFolderEntries( ri, zoneUUID, folderId, numFiles, 0, true );
		if ( entries != null )
		{
			for (GwtFolderEntry entry: entries)
			{
				ArrayList<GwtAttachment> files;
				
				// Get the files attached to this entry
				files = entry.getFiles();
				if ( files != null )
				{
					for (GwtAttachment file: files)
					{
						// Have we reached the max number of files to return?
						if ( listOfFiles.size() >= numFiles )
						{
							// Yes
							break;
						}
						
						// Add this file to the list.
						listOfFiles.add( file );
					}
				}
				
				// Have we reached the max number of files to return?
				if ( listOfFiles.size() >= numFiles )
				{
					// Yes
					break;
				}
			}
		}
		
		return listOfFiles;
	}
	
	
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

	
	/*
	 * Save the given subscription data for the given entry id.
	 * @throws GwtTeamingException 
	 */
	private Boolean saveSubscriptionData( HttpRequestInfo ri, String entryId, SubscriptionData subscriptionData )
		throws GwtTeamingException
	{
		return GwtServerHelper.saveSubscriptionData( this, entryId, subscriptionData );
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
		Boolean reply;
		try {
			BinderHelper.trackThisBinder( this, Long.parseLong(binderId), "add" );
			reply = Boolean.TRUE;
		}
		catch (Exception e) {
			reply = Boolean.FALSE;
		}
		return reply;
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
		Boolean reply;
		try {
			BinderHelper.trackThisBinder( this, Long.parseLong(binderId), "delete" );
			reply = Boolean.TRUE;
		}
		catch (Exception e) {
			reply = Boolean.FALSE;
		}
		return reply;
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
		Binder binder = getBinderModule().getBinderWithoutAccessCheck( Long.parseLong( binderId ) );
		Boolean reply;
		try {
			BinderHelper.trackThisBinder( this, binder.getOwnerId(), "deletePerson" );
			reply = Boolean.TRUE;
		}
		catch (Exception e) {
			reply = Boolean.FALSE;
		}
		return reply;
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
				if ( internalId != null && (internalId.equalsIgnoreCase( ObjectKeys.ALL_USERS_GROUP_INTERNALID ) ||
						internalId.equalsIgnoreCase( ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID )))
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
	 * Get the subscription data for the given entry id.
	 */
	private SubscriptionData getSubscriptionData( HttpRequestInfo ri, String entryId )
	{
		SubscriptionData subscriptionData;
		
		subscriptionData = GwtServerHelper.getSubscriptionData( this, entryId );
		
		return subscriptionData;
	}
	
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
				hashMap.put( "branding", branding );

				// Add the exteneded branding data to the map.
				branding = brandingData.getBrandingAsXmlString();
				if ( branding == null )
					branding = "";

				// Remove mce_src as an attribute from all <img> tags.  See bug 766415.
				// There was a bug that caused the mce_src attribute to be included in the <img>
				// tag and written to the db.  We want to remove it.
				branding = MarkupUtil.removeMceSrc( branding );

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
	private int getGroupMembership(
			HttpRequestInfo ri,
			ArrayList<GwtTeamingItem> retList,
			String groupId,
			int offset,
			int numResults,
			MembershipFilter filter ) throws GwtTeamingException
	{
		return GwtServerHelper.getGroupMembership( this, retList, groupId, offset, numResults, filter );
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
	 * Get the User Workspace Information from a user's Workspace ID.
	 * 
	 * @param ri
	 * @param binderId This is the binderId of the user's workspace.
	 * 
	 * @return UserWorkspaceInfoRpcResponseData This object contains information about the user's workspace.
	 * 
	 * @throws GwtTeamingException 
	 */
	private UserWorkspaceInfoRpcResponseData getUserWorkspaceInfo(HttpRequestInfo ri, Long binderId) throws GwtTeamingException
	{
		boolean canAccessUserWorkspace;
		Long userId = null;
		Binder binder;
		try
		{
			binder = getBinderModule().getBinder( binderId );
			canAccessUserWorkspace = ( null != binder );
		}
		catch ( Exception e )
		{
			canAccessUserWorkspace = false;
		}
		
		if ( ! canAccessUserWorkspace )
		{
			try
			{
				binder = getBinderModule().getBinderWithoutAccessCheck( binderId );
				if ( null != binder )
				{
					Principal owner = binder.getCreation().getPrincipal(); //creator is user
					owner = Utils.fixProxy( owner );
					userId = owner.getId();
				}
			}
			catch ( Exception e )
			{
				throw GwtServerHelper.getGwtTeamingException( e );
			}
		}
		return new UserWorkspaceInfoRpcResponseData( canAccessUserWorkspace, userId );
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
	private Boolean saveWhatsNewShowSetting( HttpRequestInfo ri, ActivityStreamDataType showSetting )
	{
		return GwtServerHelper.saveWhatsNewShowSetting( this, showSetting );
	}
	
	/*
	 * Marks a list of entries as having been seen.
	 */
	private Boolean setSeen( HttpRequestInfo ri, List<Long> entryIds ) throws GwtTeamingException
	{
		getProfileModule().setSeenIds( GwtServerHelper.getCurrentUserId(), entryIds );
		return Boolean.TRUE;
	}//end setSeen()
	
	/*
	 * Marks a list of entries as having been unseen.
	 */
	private Boolean setUnseen( HttpRequestInfo ri, List<Long> entryIds ) throws GwtTeamingException
	{
		getProfileModule().setUnseen( GwtServerHelper.getCurrentUserId(), entryIds );
		return Boolean.TRUE;
	}//end setUnseen()
}// end GwtRpcServiceImpl
