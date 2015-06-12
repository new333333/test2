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
package org.kablink.teaming.gwt.server;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.HttpSessionContext;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.dao.util.NetFolderSelectSpec;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.gwt.client.AdminConsoleInfo.AdminConsoleDialogMode;
import org.kablink.teaming.gwt.client.GwtADLdapObject;
import org.kablink.teaming.gwt.client.AdminConsoleInfo;
import org.kablink.teaming.gwt.client.BlogArchiveInfo;
import org.kablink.teaming.gwt.client.BlogPages;
import org.kablink.teaming.gwt.client.GroupMembershipInfo;
import org.kablink.teaming.gwt.client.GwtDatabasePruneConfiguration;
import org.kablink.teaming.gwt.client.GwtEmailPublicLinkResults;
import org.kablink.teaming.gwt.client.GwtKeyShieldConfig;
import org.kablink.teaming.gwt.client.GwtNetFolderGlobalSettings;
import org.kablink.teaming.gwt.client.GwtLdapConfig;
import org.kablink.teaming.gwt.client.GwtLdapSyncResults;
import org.kablink.teaming.gwt.client.GwtLocales;
import org.kablink.teaming.gwt.client.GwtNameCompletionSettings;
import org.kablink.teaming.gwt.client.GwtSendShareNotificationEmailResults;
import org.kablink.teaming.gwt.client.GwtTimeZones;
import org.kablink.teaming.gwt.client.NetFolderSyncStatistics;
import org.kablink.teaming.gwt.client.RequestResetPwdRpcResponseData;
import org.kablink.teaming.gwt.client.SendForgottenPwdEmailRpcResponseData;
import org.kablink.teaming.gwt.client.GwtPrincipalMobileAppsConfig;
import org.kablink.teaming.gwt.client.GwtPrincipalFileSyncAppConfig;
import org.kablink.teaming.gwt.client.GwtZoneMobileAppsConfig;
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
import org.kablink.teaming.gwt.client.GwtSearchResults;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingItem;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.ZoneShareRights;
import org.kablink.teaming.gwt.client.admin.ExtensionDefinitionInUseException;
import org.kablink.teaming.gwt.client.admin.ExtensionFiles;
import org.kablink.teaming.gwt.client.admin.ExtensionInfoClient;
import org.kablink.teaming.gwt.client.admin.GwtAdminCategory;
import org.kablink.teaming.gwt.client.admin.GwtEnterProxyCredentialsTask;
import org.kablink.teaming.gwt.client.admin.GwtSelectNetFolderServerTypeTask;
import org.kablink.teaming.gwt.client.admin.GwtUpgradeInfo;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.lpe.ConfigData;
import org.kablink.teaming.gwt.client.lpe.GwtLandingPageProperties;
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
import org.kablink.teaming.gwt.client.rpc.shared.FindUserByEmailAddressCmd.UsersToFind;
import org.kablink.teaming.gwt.client.rpc.shared.GetGroupMembershipCmd.MembershipFilter;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailAddressCmd.AddressField;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailRpcResponseData.EmailAddressStatus;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.util.ActivityStreamData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.PagingData;
import org.kablink.teaming.gwt.client.util.ActivityStreamData.SpecificFolderData;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.BinderStats;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.FolderSortSetting;
import org.kablink.teaming.gwt.client.util.GwtFolderEntryType;
import org.kablink.teaming.gwt.client.util.GwtShareLists;
import org.kablink.teaming.gwt.client.util.GwtSharingInfo;
import org.kablink.teaming.gwt.client.util.HistoryInfo;
import org.kablink.teaming.gwt.client.util.PerEntityShareRightsInfo;
import org.kablink.teaming.gwt.client.util.ProjectInfo;
import org.kablink.teaming.gwt.client.util.SelectedUsersDetails;
import org.kablink.teaming.gwt.client.util.SelectionDetails;
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
import org.kablink.teaming.gwt.client.util.TreeMode;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;
import org.kablink.teaming.gwt.client.util.ViewInfo;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.whatsnew.EventValidation;
import org.kablink.teaming.gwt.server.LdapBrowser.LdapBrowserHelper;
import org.kablink.teaming.gwt.server.util.GwtActivityStreamHelper;
import org.kablink.teaming.gwt.server.util.GwtBlogHelper;
import org.kablink.teaming.gwt.server.util.GwtCalendarHelper;
import org.kablink.teaming.gwt.server.util.GwtDeleteHelper;
import org.kablink.teaming.gwt.server.util.GwtEmailHelper;
import org.kablink.teaming.gwt.server.util.GwtFolderEntryTypeHelper;
import org.kablink.teaming.gwt.server.util.GwtHistoryHelper;
import org.kablink.teaming.gwt.server.util.GwtHtml5Helper;
import org.kablink.teaming.gwt.server.util.GwtKeyShieldSSOHelper;
import org.kablink.teaming.gwt.server.util.GwtLdapHelper;
import org.kablink.teaming.gwt.server.util.GwtLogHelper;
import org.kablink.teaming.gwt.server.util.GwtMobileDeviceHelper;
import org.kablink.teaming.gwt.server.util.GwtNetFolderHelper;
import org.kablink.teaming.gwt.server.util.GwtMenuHelper;
import org.kablink.teaming.gwt.server.util.GwtPersonalWorkspaceHelper;
import org.kablink.teaming.gwt.server.util.GwtPhotoAlbumHelper;
import org.kablink.teaming.gwt.server.util.GwtProfileHelper;
import org.kablink.teaming.gwt.server.util.GwtProxyIdentityHelper;
import org.kablink.teaming.gwt.server.util.GwtReportsHelper;
import org.kablink.teaming.gwt.server.util.GwtSearchHelper;
import org.kablink.teaming.gwt.server.util.GwtServerHelper;
import org.kablink.teaming.gwt.server.util.GwtShareHelper;
import org.kablink.teaming.gwt.server.util.GwtTaskHelper;
import org.kablink.teaming.gwt.server.util.GwtUserVisibilityHelper;
import org.kablink.teaming.gwt.server.util.GwtViewHelper;
import org.kablink.teaming.gwt.server.util.GwtWikiHelper;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.presence.PresenceInfo;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.FileIconsHelper;
import org.kablink.teaming.util.IconSize;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.EnterProxyCredentialsTask;
import org.kablink.teaming.web.util.Favorites;
import org.kablink.teaming.web.util.FavoritesLimitExceededException;
import org.kablink.teaming.web.util.FilrAdminTasks;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.teaming.web.util.PasswordPolicyHelper;
import org.kablink.teaming.web.util.PermaLinkUtil;
import org.kablink.teaming.web.util.SelectNetFolderServerTypeTask;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.teaming.web.util.WorkspaceTreeHelper;
import org.kablink.util.search.Constants;

import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.util.tools.shared.Md5Utils;
import com.google.gwt.util.tools.shared.StringUtils;

/**
 * Collection of methods used to implement the various GWT RPC
 * commands.
 * 
 * @author jwootton@novell.com
 */
public class GwtRpcServiceImpl extends AbstractAllModulesInjected
	implements GwtRpcService, XsrfTokenService
{
	/*
	 * Inner class used to encapsulate a GwtTeamingException and a
	 * VibeRpcResponse through a RunasCallback interface.
	 */
	private static class AdminVibeRpcResponseWrapper
	{
		private GwtTeamingException m_teamingException;	//
		private VibeRpcResponse		m_rpcResponse;		//

		/**
		 * Constructor method.
		 */
		public AdminVibeRpcResponseWrapper()
		{
			// Initialize the super class.
			super();
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public GwtTeamingException getTeamingException() { return m_teamingException; }
		public VibeRpcResponse     getRpcResponse()      { return m_rpcResponse;      }
		
		/**
		 * Set'er methods.
		 * 
		 * @param teamingException
		 */
		public void setTeamingException( GwtTeamingException teamingException ) { m_teamingException = teamingException; }
		public void setRpcResponse(      VibeRpcResponse     rpcResponse )      { m_rpcResponse      = rpcResponse;      }
		
	}

	/**
	 * Returns the XsrfToken to use for GWT RPC commands.  As per GWT
	 * documentation, we use the ID from the session as the token.
	 * 
	 * See:
	 *    http://www.gwtproject.org/doc/latest/DevGuideSecurityRpcXsrf.html and
	 *    XsrfProtectedServiceServlet.validateXsrfToken() (for actual validation.)
	 * 
	 * @return
	 */
	@Override
	public XsrfToken getNewXsrfToken()
	{
		SessionContext sc = RequestContextHolder.getRequestContext().getSessionContext();
		String token;
		if ( sc instanceof HttpSessionContext )
		     token = StringUtils.toHexString( Md5Utils.getMd5Digest( ((HttpSessionContext) sc).getHttpSession().getId().getBytes() ) );
		else token = null;
		return new VibeXsrfToken( token );
	}
	
	/**
	 * Execute the given command.
	 * 
	 * @param ri
	 * @param cmd
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	@Override
	public VibeRpcResponse executeCommand( final HttpRequestInfo ri, final VibeRpcCmd cmd ) throws GwtTeamingException
	{
		User user = GwtServerHelper.getCurrentUser();
		Boolean runAsAdmin = cmd.isRunAsAdmin();
		if ( ( ( null != runAsAdmin ) && runAsAdmin ) &&						// Is this command supposed to be run as Admin               and
				( ! ( user.isAdmin() ) )              &&						//    is it being requested by other than the built-in Admin and
				getAdminModule().testAccess( AdminOperation.manageFunction ) )	//    does that user have zoneAdministration rights?
		{
			// Yes!  Run the command as the built-in Admin.
			VibeRpcCmdType cmdEnum = VibeRpcCmdType.getEnum( cmd.getCmdType() );
			logger.info( "Administration Console:  User '" + user.getTitle() + "' executed the GWT RPC command " + cmdEnum.name() + " as the built-in admin user." );
			RequestContext rc = RequestContextHolder.getRequestContext();
			AdminVibeRpcResponseWrapper reply = ((AdminVibeRpcResponseWrapper) RunasTemplate.runasAdmin(
				new RunasCallback()
				{
					@Override
					public Object doAs()
					{
						AdminVibeRpcResponseWrapper reply = new AdminVibeRpcResponseWrapper();
						try                            { reply.setRpcResponse( executeCommandImpl( ri, cmd ) ); }
						catch (GwtTeamingException ex) { reply.setTeamingException( ex );                       }
						return reply;
					}
				},
				rc.getZoneName(),
				rc.getSessionContext() ) );
			
			GwtTeamingException ex = reply.getTeamingException();
			if ( null != ex )
			{
				throw ex;
			}
			return reply.getRpcResponse();
		}

		// No, the conditions aren't met to run it as the built-in
		// Admin!  Run it as the requesting user.
		return executeCommandImpl( ri, cmd );
	}
	
	/*
	 * Execute the given command.
	 */
	private VibeRpcResponse executeCommandImpl( HttpRequestInfo ri, VibeRpcCmd cmd ) throws GwtTeamingException
	{
		VibeRpcResponse response = null;
		HttpServletRequest req = getRequest( ri );

		VibeRpcCmdType cmdEnum = VibeRpcCmdType.getEnum( cmd.getCmdType() );
		switch ( cmdEnum )
		{
		case ABORT_FILE_UPLOAD:
		{
			AbortFileUploadCmd afuCmd = ((AbortFileUploadCmd) cmd);
			BooleanRpcResponseData result = GwtHtml5Helper.abortFileUpload( this, req, afuCmd.getFolderInfo(), afuCmd.getFileBlob() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case ADD_FAVORITE:
		{
			AddFavoriteCmd afCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			afCmd = (AddFavoriteCmd) cmd;
			result = GwtServerHelper.addFavorite( this, req, Long.parseLong( afCmd.getBinderId() ) );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case ADD_NEW_FOLDER:  {
			AddNewFolderCmd afCmd = ((AddNewFolderCmd) cmd);
			CreateFolderRpcResponseData responseData = GwtViewHelper.addNewFolder(
				this,
				req,
				afCmd.getBinderId(),
				afCmd.getFolderTemplateId(),
				afCmd.getFolderName(),
				afCmd.getCloudFolderType()); 
			response = new VibeRpcResponse(responseData);
			return response;
		}

		case ADD_NEW_PROXY_IDENTITY:  {
			AddNewProxyIdentityCmd anpiCmd = ((AddNewProxyIdentityCmd) cmd);
			ProxyIdentityRpcResponseData responseData = GwtProxyIdentityHelper.addNewProxyIdentity(this, req, anpiCmd.getProxyIdentity()); 
			response = new VibeRpcResponse(responseData);
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
			ErrorListRpcResponseData result = GwtViewHelper.changeEntryTypes( this, req, cetCmd.getDefId(), cetCmd.getEntryIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case CHANGE_FAVORITE_STATE:
		{
			ChangeFavoriteStateCmd cfsCmd = ((ChangeFavoriteStateCmd) cmd);
			BooleanRpcResponseData result = GwtServerHelper.changeFavoriteState( this, req, cfsCmd.getBinderId(), cfsCmd.getMakeFavorite() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case CHANGE_PASSWORD:
		{
			ChangePasswordCmd cpCmd;
			ErrorListRpcResponseData result;
			
			cpCmd = (ChangePasswordCmd) cmd;
			result = GwtServerHelper.changePassword( this, req, cpCmd.getOldPassword(), cpCmd.getNewPassword(), cpCmd.getUserId() );
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
		
		case CHECK_NET_FOLDER_SERVERS_STATUS:
		{
			CheckNetFolderServerStatusCmd csCmd;
			Set<NetFolderRoot> listOfNetFolderServers;
			
			csCmd = (CheckNetFolderServerStatusCmd) cmd;
			listOfNetFolderServers = GwtNetFolderHelper.checkNetFolderServerStatus(
																	this,
																	req,
																	csCmd.getListOfNetFolderServersToCheck() );
			response = new VibeRpcResponse( new CheckNetFolderServerStatusRpcResponseData( listOfNetFolderServers ) );
			return response;
		}
		
		case CLEAR_HISTORY:
		{
			BooleanRpcResponseData responseData = GwtHistoryHelper.clearHistory( req );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case COLLAPSE_SUBTASKS:
		{
			CollapseSubtasksCmd csCmd = ((CollapseSubtasksCmd) cmd);
			Boolean result = collapseSubtasks( req, csCmd.getBinderId(), csCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case COMPLETE_EXTERNAL_USER_SELF_REGISTRATION:
		{
			CompleteExternalUserSelfRegistrationCmd srCmd;
			ErrorListRpcResponseData result;
			
			srCmd = (CompleteExternalUserSelfRegistrationCmd) cmd;
			result = GwtServerHelper.completeExternalUserSelfRegistration(
																		this,
																		srCmd.getExtUserId(),
																		srCmd.getFirstName(),
																		srCmd.getLastName(),
																		srCmd.getPwd(),
																		srCmd.getInvitationUrl() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case COPY_ENTRIES:
		{
			CopyEntriesCmd ceCmd = ((CopyEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.copyEntries( this, req, ceCmd.getTargetFolderId(), ceCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case CREATE_CHANGE_LOG_REPORT:
		{
			CreateChangeLogReportCmd cclrCmd = ((CreateChangeLogReportCmd) cmd);
			ChangeLogReportRpcResponseData responseData = GwtReportsHelper.createChangeLogReport(
				this,
				req,
				cclrCmd.getBinderId(),
				cclrCmd.getEntityId(),
				cclrCmd.getEntityType(),
				cclrCmd.getOperation() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case CREATE_DUMMY_MOBILE_DEVICES:
		{
			CreateDummyMobileDevicesCmd cdmdCmd = ((CreateDummyMobileDevicesCmd) cmd);
			BooleanRpcResponseData responseData = GwtMobileDeviceHelper.createDummyMobileDevices( this, req, cdmdCmd.getUserId(), cdmdCmd.getCount() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case CREATE_EMAIL_REPORT:
		{
			CreateEmailReportCmd cerCmd = ((CreateEmailReportCmd) cmd);
			EmailReportRpcResponseData responseData = GwtReportsHelper.createEmailReport( this, req, cerCmd.getBegin(), cerCmd.getEnd(), cerCmd.getEmailType() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case CREATE_GROUP:
		{
			CreateGroupCmd cgCmd;
			GroupInfo groupInfo;
			Group group;
			
			cgCmd = (CreateGroupCmd) cmd;
			group = GwtServerHelper.createGroup(
											this,
											cgCmd.getName(),
											cgCmd.getTitle(),
											cgCmd.getDesc(),
											cgCmd.getIsMembershipDynamic(),
											cgCmd.getExternalMembersAllowed(),
											cgCmd.getMembershipCriteria() );
			groupInfo = new GroupInfo();
			if ( group != null )
			{
				Description desc;
				boolean allowExternal;
				
				groupInfo.setId( group.getId() );
				groupInfo.setName( group.getName() );
				groupInfo.setTitle( group.getTitle() );
				allowExternal = true;	// JW:  Get the group the group object?
				groupInfo.setMembershipInfo( group.isDynamic(), allowExternal );

				groupInfo.setDn( group.getForeignName() );
				
				desc = group.getDescription();
				if ( desc != null )
					groupInfo.setDesc( desc.getText() );
			}
			response = new VibeRpcResponse( groupInfo );
			
			return response;
		}
		
		case CREATE_LICENSE_REPORT:
		{
			CreateLicenseReportCmd clrCmd = ((CreateLicenseReportCmd) cmd);
			LicenseReportRpcResponseData responseData = GwtReportsHelper.createLicenseReport( this, req, clrCmd.getBegin(), clrCmd.getEnd() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case CREATE_LOGIN_REPORT:
		{
			CreateLoginReportCmd clrCmd = ((CreateLoginReportCmd) cmd);
			StringRpcResponseData responseData = GwtReportsHelper.createLoginReport( this, req, clrCmd.getBegin(), clrCmd.getEnd(), clrCmd.getUserIds(), clrCmd.getReportType(), clrCmd.getLongSortBy(), clrCmd.getShortSortBy() );
			response = new VibeRpcResponse( responseData );
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
		
		case CREATE_USER_ACCESS_REPORT:
		{
			CreateUserAccessReportCmd cuarCmd = ((CreateUserAccessReportCmd) cmd);
			UserAccessReportRpcResponseData responseData = GwtReportsHelper.createUserAccessReport( this, req, cuarCmd.getUserId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case CREATE_USER_ACTIVITY_REPORT:
		{
			CreateUserActivityReportCmd cuarCmd = ((CreateUserActivityReportCmd) cmd);
			StringRpcResponseData responseData = GwtReportsHelper.createUserActivityReport( this, req, cuarCmd.getBegin(), cuarCmd.getEnd(), cuarCmd.getUserIds(), cuarCmd.getReportType() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DELETE_NET_FOLDERS:
		{
			DeleteNetFolderRpcResponseData result;
			DeleteNetFoldersCmd dnfCmd;
			
			dnfCmd = (DeleteNetFoldersCmd) cmd;
			result = GwtNetFolderHelper.deleteNetFolders( this, dnfCmd.getListOfNetFoldersToDelete() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case DELETE_NET_FOLDER_ROOTS:
		{
			DeleteNetFolderServersRpcResponseData result;
			DeleteNetFolderRootsCmd dnfrCmd;
			
			dnfrCmd = (DeleteNetFolderRootsCmd) cmd;
			result = GwtNetFolderHelper.deleteNetFolderRoots( this, dnfrCmd.getListOfNetFolderRootsToDelete() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case DELETE_GROUPS:
		{
			Boolean result;
			DeleteGroupsCmd dgCmd;
			
			dgCmd = (DeleteGroupsCmd) cmd;
			result = GwtDeleteHelper.deleteGroups( this, dgCmd.getListOfGroupsToDelete() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case DELETE_MOBILE_DEVICES:
		{
			DeleteMobileDevicesCmd dmdCmd = ((DeleteMobileDevicesCmd) cmd);
			DeleteMobileDevicesRpcResponseData responseData = GwtMobileDeviceHelper.deleteMobileDevices( this, req, dmdCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DELETE_PROXY_IDENTITIES:  {
			DeleteProxyIdentitiesCmd dpiCmd = ((DeleteProxyIdentitiesCmd) cmd);
			DeleteProxyIdentitiesRpcResponseData responseData = GwtProxyIdentityHelper.deleteProxyIdentities(this, req, dpiCmd.getEntityIds());
			response = new VibeRpcResponse(responseData);
			return response;
		}
		
		case DELETE_SELECTED_USERS:
		{
			DeleteSelectedUsersCmd dsuCmd = ((DeleteSelectedUsersCmd) cmd);
			ErrorListRpcResponseData responseData = GwtDeleteHelper.deleteSelectedUsers( this, req, dsuCmd.getUserIds(), dsuCmd.getDeleteSelectedUsersMode(), dsuCmd.getPurgeUsersWithWorkspace() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DELETE_SELECTIONS:
		{
			DeleteSelectionsCmd dsCmd = ((DeleteSelectionsCmd) cmd);
			ErrorListRpcResponseData responseData = GwtDeleteHelper.deleteSelections( this, req, dsCmd.getEntityIds(), dsCmd.getDeleteSelectionsMode() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DELETE_SHARES:
		{
			DeleteSharesCmd dslCmd = ((DeleteSharesCmd) cmd);
			ErrorListRpcResponseData result = GwtShareHelper.deleteShares( this, req, dslCmd.getShareIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case DELETE_TASKS:
		{
			DeleteTasksCmd dtCmd = ((DeleteTasksCmd) cmd);
			ErrorListRpcResponseData responseData = deleteTasks( req, dtCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DISABLE_USERS:
		{
			DisableUsersCmd duCmd = ((DisableUsersCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.disableUsers( this, req, duCmd.getUserIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case DUMP_HISTORY_INFO:
		{
			DumpHistoryInfoCmd dhiCmd = ((DumpHistoryInfoCmd) cmd);
			GwtHistoryHelper.dumpHistoryInfo( req, dhiCmd.getMethod(), dhiCmd.getHistoryToken(), dhiCmd.getHistoryInfo() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( true ) );
			return response;
		}
		
		case EDIT_ENTRY:  {
			EditEntryCmd eeCmd = ((EditEntryCmd) cmd);
			ActivityStreamEntry result = editEntry(
				req,
				eeCmd.getEntryId(),
				eeCmd.getTitle(),
				eeCmd.getDescription());
			ActivityStreamEntryRpcResponseData responseData = new ActivityStreamEntryRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case EMAIL_PUBLIC_LINK:
		{
			EmailPublicLinkCmd eplCmd;
			GwtEmailPublicLinkResults results;

			eplCmd = (EmailPublicLinkCmd) cmd;
			results = GwtShareHelper.emailPublicLink( this, req, eplCmd.getEmailPublicLinkData() );
			response = new VibeRpcResponse( results );
			return response;
		}
		
		case ENABLE_USERS:
		{
			EnableUsersCmd euCmd = ((EnableUsersCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.enableUsers( this, req, euCmd.getUserIds() );
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
				// Construct the full path to the jsp
				jspPath =  "/WEB-INF/jsp/custom_jsps/" + elpjCmd.getJspName();
				
				// Is the path trying to go above the custom_jsps directory?
				if ( jspPath.contains( "./" ) || jspPath.contains( ".\\" ) || jspPath.contains( "~" ) )
				{
					// Yes
					String errMsg;
					
					errMsg = NLT.get( "mashup.customJspNotInCustomJspDir" );
					result = NLT.get( "mashup.customJspError", new Object[]{errMsg} );
				}
				else
				{
					result = GwtServerHelper.executeLandingPageJsp( this, req, resp, servletContext, elpjCmd.getBinderId(), jspPath, elpjCmd.getConfigStr() );
				}
			}
			
			response = new VibeRpcResponse( new StringRpcResponseData( result ) );
			return response;
		}
		
		case EXECUTE_SEARCH:  {
			ExecuteSearchCmd esCmd = ((ExecuteSearchCmd) cmd);
			if (ExecuteSearchCmd.DEBUG_SEARCH_SEQUENCE) { 
				// This try/catch is used for debug purposes to
				// randomize how long a search might take.  It invokes
				// a random delay between 0 and 5 seconds before
				// performing the search.
				try {Thread.sleep(new Random().nextInt(5000));}
				catch (Exception e) {}
			}
			
			GwtSearchResults searchResults;
			try {
				searchResults = GwtSearchHelper.executeSearch(this, req, esCmd.getSearchCriteria());
				if (null == searchResults) {
					// Protects against NPEs.
					searchResults = new GwtSearchResults();
					searchResults.setResults(new ArrayList<GwtTeamingItem>());
				}
				searchResults.setSearchSequence(esCmd.getSearchSequence());
			}
			catch (Exception ex) {
				GwtTeamingException gtEx = GwtLogHelper.getGwtClientException(ex);
				throw gtEx;				
			}
			response = new VibeRpcResponse(searchResults);
			return response;
		}
		
		case EXPAND_HORIZONTAL_BUCKET:
		{
			ExpandHorizontalBucketCmd ehbCmd;
			TreeInfo result;
			
			ehbCmd = (ExpandHorizontalBucketCmd) cmd;
			result = expandHorizontalBucket( req, ehbCmd.getBucketInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case EXPAND_SUBTASKS:
		{
			ExpandSubtasksCmd csCmd = ((ExpandSubtasksCmd) cmd);
			Boolean result = expandSubtasks( req, csCmd.getBinderId(), csCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case EXPAND_VERTICAL_BUCKET:
		{
			ExpandVerticalBucketCmd evbCmd = ((ExpandVerticalBucketCmd) cmd);
			TreeInfo result = expandVerticalBucket( req, evbCmd.isFindBrowser(), evbCmd.getBucketInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case FIND_USER_BY_EMAIL_ADDRESS:
		{
			final FindUserByEmailAddressCmd fuCmd;
			final HttpServletRequest request;
			final AllModulesInjected ami;
			Object retValue;
			RunasCallback callback;

			fuCmd = (FindUserByEmailAddressCmd) cmd;
			request = req;
			ami = this;
			
			callback = new RunasCallback()
			{
				@Override
				public Object doAs()
				{
					FindUserByEmailAddressRpcResponseData responseData;
					ArrayList<String> listOfEmailAddresses;
					
					responseData = new FindUserByEmailAddressRpcResponseData();
					
					listOfEmailAddresses = fuCmd.getListOfEmailAddresses();
					if ( listOfEmailAddresses != null )
					{
						for ( String ema : listOfEmailAddresses )
						{
							GwtUser gwtUser;

							gwtUser = GwtSearchHelper.findUserByEmailAddress( ami, request, ema );
							
							// Did we find a user?
							if ( gwtUser != null )
							{
								// Yes
								// Is the user disabled?
								if ( gwtUser.isDisabled() )
								{
									// Yes
									gwtUser = null;
									responseData.addEmailStatus( ema, EmailAddressStatus.disabledUser );
								}
								else
								{
									// No
									// Did we find an external user?
									if ( gwtUser.getPrincipalType().isExternal() )
									{
										// Yes
										// Are we looking for external users
										if ( fuCmd.getUsersToFind() == UsersToFind.ALL_USERS ||
											 fuCmd.getUsersToFind() == UsersToFind.EXTERNAL_USERS_ONLY )
										{
											// Yes
											// Are we supposed to validate the email address?
											if ( fuCmd.isValidateExternalEMA() )
											{
												EmailAddressStatus emaStatus = null;
		
												// Yes
												// Is the email address valid?
												emaStatus = GwtServerHelper.validateEmailAddressImpl(
																								ami,
																								ema,
																								true,
																								AddressField.MAIL_TO );
												if ( emaStatus != null && emaStatus.isInvalid() )
												{
													// No
													gwtUser = null;
													responseData.addEmailStatus( ema, emaStatus );
												}
											}
										}
										else
										{
											// No
											gwtUser = null;
										}
									}
									else
									{
										// We found an internal user.
										// Are we looking for internal users?
										if ( fuCmd.getUsersToFind() != UsersToFind.ALL_USERS &&
											 fuCmd.getUsersToFind() != UsersToFind.INTERNAL_USERS_ONLY )
										{
											// No
											gwtUser = null;
										}
									}
								}
							}
							else
							{
								// We didn't find a user with the given email address
								// Are we supposed to validate the email address?
								if ( fuCmd.isValidateExternalEMA() )
								{
									EmailAddressStatus emaStatus = null;

									// Yes
									// Is the email address valid?
									emaStatus = GwtServerHelper.validateEmailAddressImpl(
																						ami,
																						ema,
																						true,
																						AddressField.MAIL_TO );
									if ( emaStatus != null )
									{
										// No
										responseData.addEmailStatus( ema, emaStatus );
									}
								}
							}
							
							if ( gwtUser != null )
							{
								responseData.addUser( ema, gwtUser );
							}
						}
					}
					
					return new VibeRpcResponse( responseData );
				}
			}; 

			// Do the necessary work as the admin user.
			retValue = RunasTemplate.runasAdmin(
											callback,
											RequestContextHolder.getRequestContext().getZoneName() );
			
			return (VibeRpcResponse) retValue;
		}

		case FORCE_FILES_UNLOCK:
		{
			ForceFilesUnlockCmd ffuCmd = ((ForceFilesUnlockCmd) cmd);
			BooleanRpcResponseData result = GwtViewHelper.forceFilesUnlock( this, req, ffuCmd.getEntityIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case FORCE_USERS_TO_CHANGE_PASSWORD:
		{
			ForceUsersToChangePasswordCmd fcpCmd = ((ForceUsersToChangePasswordCmd) cmd);
			List<String> errList = PasswordPolicyHelper.forceUsersToChangePassword( this, req, fcpCmd.getUserIds() );
			ErrorListRpcResponseData result = new ErrorListRpcResponseData();
			result.addErrors(errList);
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ACCESSORY_STATUS:
		{
			GetAccessoryStatusCmd gasCmd = ((GetAccessoryStatusCmd) cmd);
			Boolean responseData = GwtViewHelper.getAccessoryStatus(
				this,
				req,
				gasCmd.getBinderId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( responseData ) );
			return response;
		}
		
		case GET_ACTIVITY_STREAM_DATA:
		{
			GetActivityStreamDataCmd gasdCmd = ((GetActivityStreamDataCmd) cmd);
			ActivityStreamData asData = getActivityStreamData(
				req,
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
			
			asParams = getActivityStreamParams( req );
			response = new VibeRpcResponse( asParams );
			return response;
		}
		
		case GET_ADD_MEETING_URL:
		{
			GetAddMeetingUrlCmd gamuCmd;
			String result;
			StringRpcResponseData responseData;
			
			gamuCmd = (GetAddMeetingUrlCmd) cmd;
			result = GwtServerHelper.getAddMeetingUrl( this, req, gamuCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ADHOC_FOLDER_SETTING:
		{
			GetAdhocFolderSettingCmd gafsCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			Long upId;
			
			gafsCmd = (GetAdhocFolderSettingCmd) cmd;
			upId = gafsCmd.getUserPrincipalId();
			if ( upId != null )
				result = GwtUIHelper.getAdhocFolderSettingFromUserOrGroup( this, upId, true );
			else
				result = GwtUIHelper.getAdhocFolderSettingFromZone( this );
			
			if ( result == null )
				result = Boolean.FALSE;
			
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ADMIN_ACTIONS:  {
			String mode = SPropsUtil.getString("admin.console.dialog.mode", "modal");
			if (null == mode) {
				mode = "";
			}
			AdminConsoleDialogMode acDlgMode;
			if      (mode.equalsIgnoreCase(AdminConsoleDialogMode.MIXED.name()))    acDlgMode = AdminConsoleDialogMode.MIXED; 
			else if (mode.equalsIgnoreCase(AdminConsoleDialogMode.MODELESS.name())) acDlgMode = AdminConsoleDialogMode.MODELESS;
			else                                                                    acDlgMode = AdminConsoleDialogMode.MODAL;
			
			AdminConsoleInfo adminConsoleInfo = new AdminConsoleInfo(acDlgMode);
			String binderId = ((GetAdminActionsCmd)cmd).getBinderId();
			ArrayList<GwtAdminCategory> adminActions = getAdminActions(req, binderId);
			adminConsoleInfo.setCategories(adminActions);
			
			// Get the url for the administration console "home page"
			AdaptedPortletURL adaptedUrl = new AdaptedPortletURL(req, "ss_forum", false);
			adaptedUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_ADMIN_CONSOLE_HOME_PAGE);
			adminConsoleInfo.setHomePageUrl(adaptedUrl.toString());
			
			response = new VibeRpcResponse(adminConsoleInfo);
			return response;
		}
		
		case GET_ALL_NET_FOLDERS:
		{
			List<NetFolder> result;
			int numNetFolders;
			GetNetFoldersRpcResponseData responseData;
			GetNetFoldersCmd gnfCmd;
			NetFolderSelectSpec selectSpec;
			
			gnfCmd = (GetNetFoldersCmd) cmd;
			selectSpec = new NetFolderSelectSpec();
			selectSpec.setFilter( gnfCmd.getFilter() );
			selectSpec.setIncludeHomeDirNetFolders( gnfCmd.getIncludeHomeDirNetFolders() );
			if(gnfCmd.getRootName() != null) {
				ResourceDriverConfig rdc = NetFolderUtil.getNetFolderServerByName(gnfCmd.getRootName());
				selectSpec.setRootId( rdc.getId() );
			}
			selectSpec.setStartIndex( gnfCmd.getStartIndex() );
			selectSpec.setPageSize( gnfCmd.getPageSize() );
			result = GwtNetFolderHelper.getAllNetFolders( this, selectSpec, true );
			numNetFolders = GwtNetFolderHelper.getNumberOfNetFolders( this, selectSpec );
			responseData = new GetNetFoldersRpcResponseData( result );
			responseData.setTotalCount( numNetFolders );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ALL_NET_FOLDER_ROOTS:
		{
			List<NetFolderRoot> result;
			GetNetFolderRootsRpcResponseData responseData;
			
			result = GwtNetFolderHelper.getAllNetFolderRoots( this );
			responseData = new GetNetFolderRootsRpcResponseData( result );
			
			// Get the flag that indicates whether the admin has seen the oes warning
			{
				UserProperties userProperties;
				String value;
				boolean hasSeenWarning = false;

				userProperties = getProfileModule().getUserProperties( null );
				value = (String) userProperties.getProperty( ObjectKeys.USER_PROPERTY_HAS_SEEN_OES_WARNING );
				if ( MiscUtil.hasString( value ) )
				     hasSeenWarning = Boolean.parseBoolean( value );
				
				responseData.setHasSeenOesNetFolderServerWarning( hasSeenWarning );
			}
			
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ALL_GROUPS:
		{
			GetAllGroupsCmd gagCmd;
			List<GroupInfo> result;
			GetGroupsRpcResponseData responseData;
			
			gagCmd = (GetAllGroupsCmd) cmd;
			result = GwtServerHelper.getAllGroups( this, gagCmd.getFilter() );
			responseData = new GetGroupsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_BINDER_BRANDING:
		{
			GwtBrandingData brandingData;
			GetBinderBrandingCmd gbbCmd;
			
			gbbCmd = (GetBinderBrandingCmd) cmd;
			brandingData = GwtServerHelper.getBinderBrandingData(
																this,
																gbbCmd.getBinderId(),
																gbbCmd.getUseInheritance(),
																req,
																getServletContext( ri ) );

			response = new VibeRpcResponse( brandingData );
			return response;
		}
		
		case GET_BINDER_DESCRIPTION:
		{
			GetBinderDescriptionCmd gbdCmd = ((GetBinderDescriptionCmd) cmd);
			BinderDescriptionRpcResponseData responseData = GwtViewHelper.getBinderDescription(
				this,
				req,
				gbdCmd.getBinderId() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_BINDER_FILTERS:
		{
			GetBinderFiltersCmd gbfCmd = ((GetBinderFiltersCmd) cmd);
			BinderFiltersRpcResponseData responseData = GwtViewHelper.getBinderFilters(
				this,
				req,
				gbfCmd.getBinderId() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_BINDER_INFO:
		{
			BinderInfo binderInfo;
			String binderId;
			
			binderId = ((GetBinderInfoCmd) cmd).getBinderId();
			binderInfo = getBinderInfo( req, binderId );
			response = new VibeRpcResponse( binderInfo );
			return response;
		}
		
		case GET_BINDER_REGION_STATE:
		{
			GetBinderRegionStateCmd gbrsCmd = ((GetBinderRegionStateCmd) cmd);
			StringRpcResponseData responseData = GwtViewHelper.getBinderRegionState(
				this,
				req,
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
			ViewInfo vi = getViewInfo( req, ((GetViewInfoCmd) cmd) );
			response = new VibeRpcResponse( vi );
			return response;
		}
		
		case GET_BINDER_OWNER_AVATAR_INFO:
		{
			GetBinderOwnerAvatarInfoCmd gboaiCmd = ((GetBinderOwnerAvatarInfoCmd) cmd);
			AvatarInfoRpcResponseData responseData = GwtViewHelper.getBinderOwnerAvatarInfo( this, req, gboaiCmd.getBinderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_BINDER_PERMALINK:
		{
			GetBinderPermalinkCmd gbpCmd;
			String permalink;
			StringRpcResponseData responseData;
			
			gbpCmd = (GetBinderPermalinkCmd) cmd;
			permalink = getBinderPermalink( req, gbpCmd.getBinderId() );
			responseData = new StringRpcResponseData( permalink );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_BINDER_SHARING_RIGHTS_INFO:
		{
			GetBinderSharingRightsInfoCmd gbsrCmd = ((GetBinderSharingRightsInfoCmd) cmd);
			BinderSharingRightsInfoRpcResponseData result = GwtServerHelper.getBinderSharingRightsInfo( this, req, gbsrCmd.getBinderIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_BINDER_TAGS:
		{
			GetBinderTagsCmd gbtCmd;
			ArrayList<TagInfo> result;
			GetTagsRpcResponseData responseData;
			
			gbtCmd = (GetBinderTagsCmd) cmd;
			result = getBinderTags( req, gbtCmd.getBinderId() );
			responseData = new GetTagsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_CALENDAR_APPOINTMENTS:
		{
			GetCalendarAppointmentsCmd gcaCmd = ((GetCalendarAppointmentsCmd) cmd);
			CalendarAppointmentsRpcResponseData responseData = GwtCalendarHelper.getCalendarAppointments(
				this,
				req,
				gcaCmd.getBrowserTZOffset(),
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
				req,
				gcddCmd.getBrowserTZOffset(),
				gcddCmd.getFolderInfo() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CALENDAR_DISPLAY_DATE:
		{
			GetCalendarDisplayDateCmd gcddCmd = ((GetCalendarDisplayDateCmd) cmd);
			CalendarDisplayDataRpcResponseData responseData = GwtCalendarHelper.getCalendarDisplayDate(
				this,
				req,
				gcddCmd.getBrowserTZOffset(),
				gcddCmd.getFolderId(),
				gcddCmd.getCalendarDisplayData() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CALENDAR_NEXT_PREVIOUS_PERIOD:
		{
			GetCalendarNextPreviousPeriodCmd gcnppCmd = ((GetCalendarNextPreviousPeriodCmd) cmd);
			CalendarDisplayDataRpcResponseData responseData = GwtCalendarHelper.getCalendarNextPreviousPeriod(
				this,
				req,
				gcnppCmd.getBrowserTZOffset(),
				gcnppCmd.getFolderId(),
				gcnppCmd.getCalendarDisplayData(),
				gcnppCmd.getNext() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CAN_ADD_ENTITIES:
		{
			GetCanAddEntitiesCmd gcaeCmd = ((GetCanAddEntitiesCmd) cmd);
			CanAddEntitiesRpcResponseData responseData = GwtViewHelper.getCanAddEntities( this, req, gcaeCmd.getBinderInfo() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CAN_ADD_ENTITIES_TO_BINDERS:
		{
			GetCanAddEntitiesToBindersCmd gcaetbCmd = ((GetCanAddEntitiesToBindersCmd) cmd);
			CanAddEntitiesToBindersRpcResponseData responseData = GwtViewHelper.getCanAddEntitiesToBinders( this, req, gcaetbCmd.getBinderIds() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_CLICK_ON_TITLE_ACTION:
		{
			GetClickOnTitleActionCmd gcotaCmd = ((GetClickOnTitleActionCmd) cmd);
			ClickOnTitleActionRpcResponseData result = GwtViewHelper.getClickOnTitleAction( this, req, gcotaCmd.getEntityId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_CLIPBOARD_TEAM_USERS:
		{
			GetClipboardTeamUsersCmd gctuCmd = ((GetClipboardTeamUsersCmd) cmd);
			ClipboardUsersRpcResponseData result = GwtServerHelper.getClipboardTeamUsers( this, req, gctuCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_CLIPBOARD_USERS:
		{
			@SuppressWarnings("unused")
			GetClipboardUsersCmd gcuCmd = ((GetClipboardUsersCmd) cmd);
			ClipboardUsersRpcResponseData result = GwtServerHelper.getClipboardUsers( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_CLIPBOARD_USERS_FROM_LIST:
		{
			GetClipboardUsersFromListCmd gcuflCmd = ((GetClipboardUsersFromListCmd) cmd);
			ClipboardUsersRpcResponseData result = GwtServerHelper.getClipboardUsersFromList( this, req, gcuflCmd.getBinderId(), gcuflCmd.getUserIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_COLLECTION_POINT_DATA:
		{
			CollectionPointData collectionPointData;
			
			collectionPointData= GwtServerHelper.getCollectionPointData(
																	this,
																	req );
			response = new VibeRpcResponse( collectionPointData );
			return response;
		}
		
		case GET_COLUMN_WIDTHS:
		{
			GetColumnWidthsCmd gcwCmd = ((GetColumnWidthsCmd) cmd);
			ColumnWidthsRpcResponseData result = GwtViewHelper.getColumnWidths(
				this,
				req,
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
		
		case GET_DATE_STR:
		{
			GetDateStrCmd gdCmd;
			String result = "";
			
			gdCmd = (GetDateStrCmd) cmd;
			if ( gdCmd.getDate() != null )
			{
				Date date;
				
				date = new Date( gdCmd.getDate() );
				result = GwtServerHelper.getDateString( date, gdCmd.getFormat() );
			}
			
			response = new VibeRpcResponse( new StringRpcResponseData( result ) );
			return response;
		}
		
		case GET_DATE_TIME_STR:
		{
			GetDateTimeStrCmd gdtCmd;
			String result = "";
			
			gdtCmd = (GetDateTimeStrCmd) cmd;
			if ( gdtCmd.getDateTime() != null )
			{
				Date date;
				
				date = new Date( gdtCmd.getDateTime() );
				result = GwtServerHelper.getDateTimeString( date, gdtCmd.getDateFormat(), gdtCmd.getTimeFormat() );
			}
			
			response = new VibeRpcResponse( new StringRpcResponseData( result ) );
			return response;
		}
		
		case GET_DEFAULT_ACTIVITY_STREAM:
		{
			GetDefaultActivityStreamCmd gdasCmd = ((GetDefaultActivityStreamCmd) cmd);
			ActivityStreamInfo asi = GwtActivityStreamHelper.getDefaultActivityStream( req, this, gdasCmd.getBinderId(), gdasCmd.getOverrideActivityStream(), gdasCmd.getOverrideActivityStreamId() );
			response = new VibeRpcResponse( asi );
			return response;
		}
		
		case GET_DEFAULT_FOLDER_DEFINITION_ID:
		{
			GetDefaultFolderDefinitionIdCmd gdfdiCmd;
			StringRpcResponseData responseData;
			String result;
			
			gdfdiCmd = (GetDefaultFolderDefinitionIdCmd) cmd;
			result = getDefaultFolderDefinitionId( req, gdfdiCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_DEFAULT_STORAGE_ID:
		{
			String result = getDefaultStorageId();
			StringRpcResponseData responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_DESKTOP_APP_DOWNLOAD_INFO:
		{
			DesktopAppDownloadInfoRpcResponseData result = GwtServerHelper.getDesktopAppDownloadInformation( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_DISK_USAGE_INFO:
		{
			GetDiskUsageInfoCmd gduiCmd;
			DiskUsageInfo result;
			
			gduiCmd = (GetDiskUsageInfoCmd) cmd;
			result = getDiskUsageInfo( req, gduiCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_DOCUMENT_BASE_URL:
		{
			String binderId = ((GetDocBaseUrlCmd) cmd).getBinderId();
			String result = getDocumentBaseUrl(req, binderId);
			response = new VibeRpcResponse(new StringRpcResponseData(result));
			return response;
		}
		
		case GET_DOWNLOAD_FILE_URL:
		{
			GetDownloadFileUrlCmd gdfuCmd = ((GetDownloadFileUrlCmd) cmd);
			String result = GwtServerHelper.getDownloadFileUrl( req, this, gdfuCmd.getBinderId(), gdfuCmd.getEntryId() );
			StringRpcResponseData responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_DOWNLOAD_FOLDER_AS_CSV_FILE_URL:
		{
			GetDownloadFolderAsCSVFileUrlCmd gdfacfuCmd = ((GetDownloadFolderAsCSVFileUrlCmd) cmd);
			DownloadFolderAsCSVFileUrlRpcResponseData result = GwtViewHelper.getDownloadFolderAsCSVFileUrl( this, req, gdfacfuCmd.getFolderId(), gdfacfuCmd.getCSVDelim() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_DOWNLOAD_SETTING:
		{
			GetDownloadSettingCmd gdsCmd = ((GetDownloadSettingCmd) cmd);
			Long                  upId   = gdsCmd.getUserPrincipalId();
			Boolean               result;
			if (null != upId)
			     result = GwtUIHelper.getDownloadSettingFromUserOrGroup( this, upId );
			else result = GwtUIHelper.getDownloadSettingFromZone(        this       );
			if (null == result) {
				result = Boolean.FALSE;
			}
			
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
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
			EmailNotificationInfoRpcResponseData result = GwtEmailHelper.getEmailNotificationInfo( this, req, geniCmd.getEntityId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ENTITY_ACTION_TOOLBAR_ITEMS:
		{
			GetEntityActionToolbarItemsCmd geatbiCmd = ((GetEntityActionToolbarItemsCmd) cmd);
			GetToolbarItemsRpcResponseData responseData = GwtMenuHelper.getEntityActionToolbarItems( this, req, geatbiCmd.getBinderInfo(), geatbiCmd.getEntityId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ENTITY_ID:
		{
			GetEntityIdCmd geidCmd = ((GetEntityIdCmd) cmd);
			EntityIdRpcResponseData responseData = GwtServerHelper.getEntityId( this, req, geidCmd.getBinderId(), geidCmd.getEntityId(), geidCmd.getEntityIdType(), geidCmd.getMobileDeviceId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ENTITY_ID_LIST:
		{
			GetEntityIdListCmd geidlCmd = ((GetEntityIdListCmd) cmd);
			EntityIdListRpcResponseData responseData = GwtServerHelper.getEntityIdList( this, req, geidlCmd.getEntityIdCmdList() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ENTITY_PERMALINK:
		{
			GetEntityPermalinkCmd gepCmd;
			String permalink;
			StringRpcResponseData responseData;
			
			gepCmd = (GetEntityPermalinkCmd) cmd;
			permalink = GwtServerHelper.getEntityPermalink( this, req, gepCmd.getEntityId() );
			responseData = new StringRpcResponseData( permalink );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ENTITY_RIGHTS:
		{
			GetEntityRightsCmd gerCmd = ((GetEntityRightsCmd) cmd);
			EntityRightsRpcResponseData responseData = GwtViewHelper.getEntityRights( this, req, gerCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ENTRY:
		{
			GetEntryCmd geCmd = ((GetEntryCmd) cmd);
			GwtFolderEntry result = getEntry(req, geCmd.getZoneUUId(), geCmd.getEntryId(), geCmd.getNumReplies(), geCmd.getFileAttachmentsValue());
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ENTRY_COMMENTS:
		{
			GetEntryCommentsCmd gecCmd;
			List<ActivityStreamEntry> listOfComments;
			ActivityStreamEntryListRpcResponseData result;
			
			gecCmd = (GetEntryCommentsCmd) cmd;
			listOfComments = getEntryComments( req, gecCmd.getEntryId() );
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
			result = getEntryTags( req, getCmd.getEntryId() );
			responseData = new GetTagsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ENTRY_TYPES:
		{
			GetEntryTypesCmd getCmd = ((GetEntryTypesCmd) cmd);
			EntryTypesRpcResponseData result = GwtViewHelper.getEntryTypes( this, req, getCmd.getEntityId(), getCmd.getBinderIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_EXECUTE_JSP_URL:
		{
			GetExecuteJspUrlCmd gejuCmd;
			StringRpcResponseData responseData;
			String result;
			
			gejuCmd = (GetExecuteJspUrlCmd) cmd;
			result = GwtServerHelper.getExecuteJspUrl( req, gejuCmd.getBinderId(), gejuCmd.getJspName() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			
			return response;
		}
		
		case GET_EXTENSION_FILES:
		{
			GetExtensionFilesCmd gefCmd;
			ExtensionFiles result;
			
			gefCmd = (GetExtensionFilesCmd) cmd;
			result = getExtensionFiles( req, gefCmd.getId(), gefCmd.getZoneName() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_EXTENSION_INFO:
		{
			ExtensionInfoClient[] result;
			GetExtensionInfoRpcResponseData responseData;
			
			result = getExtensionInfo( req );
			responseData = new GetExtensionInfoRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FAVORITES:
		{
			GetFavoritesRpcResponseData responseData;
			List<FavoriteInfo> result;
			
			result = getFavorites( req );
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
			result = getFileAttachments( req, gfaCmd.getBinderId() );
			responseData = new GetFileAttachmentsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}


		case GET_FILE_CONFLICTS_INFO:
		{
			GetFileConflictsInfoCmd gfciCmd = ((GetFileConflictsInfoCmd) cmd);
			FileConflictsInfoRpcResponseData responseData = GwtViewHelper.getFileConflictsInfo( this, req, gfciCmd.getFolderInfo(), gfciCmd.getFileConflicts() );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case GET_DATABASE_PRUNE_CONFIGURATION:
		{
			GwtDatabasePruneConfiguration databasePruneConfiguration; 

			databasePruneConfiguration = GwtServerHelper.getDatabasePruneConfiguration( this );
			response = new VibeRpcResponse( databasePruneConfiguration );
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
			url = GwtServerHelper.getFileUrl( this, req, gfuCmd.getBinderId(), gfuCmd.getFileName() );
			response = new VibeRpcResponse( new StringRpcResponseData( url ) );
			return response;
		}

		case GET_FOLDER:
		{
			GetFolderCmd gfCmd;
			GwtFolder result;
			
			gfCmd = (GetFolderCmd) cmd;
			result = getFolder( req, gfCmd.getZoneUUId(), gfCmd.getFolderId(), gfCmd.isExtendedTitle() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_FOLDER_COLUMNS:
		{
			GetFolderColumnsCmd gfcCmd = ((GetFolderColumnsCmd) cmd);
			FolderColumnsRpcResponseData responseData = GwtViewHelper.getFolderColumns(
				this,
				req,
				gfcCmd.getFolderInfo(),
				gfcCmd.isIncludeConfigurationInfo() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_FOLDER_DISPLAY_DATA:
		{
			GetFolderDisplayDataCmd gfddCmd = ((GetFolderDisplayDataCmd) cmd);
			FolderDisplayDataRpcResponseData responseData = GwtViewHelper.getFolderDisplayData(
				this,
				req,
				gfddCmd.getFolderInfo() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_FOLDER_ENTRIES:
		{
			GetFolderEntriesCmd gfeCmd = (GetFolderEntriesCmd) cmd;
			ArrayList<GwtFolderEntry> result = getFolderEntries(req, gfeCmd.getZoneUUID(), gfeCmd.getFolderId(), gfeCmd.getNumEntries(), gfeCmd.getNumReplies(), gfeCmd.getFileAttachmentsValue());
			GetFolderEntriesRpcResponseData responseData = new GetFolderEntriesRpcResponseData(result);
			response = new VibeRpcResponse(responseData);
			return response;
		}
		
		case GET_FOLDER_ENTRY_DETAILS:
		{
			GetFolderEntryDetailsCmd			gfeCmd       = ((GetFolderEntryDetailsCmd) cmd);
			FolderEntryDetails					fed          = GwtViewHelper.getFolderEntryDetails( this, req, gfeCmd.getEntityId(), gfeCmd.isMarkRead() );
			FolderEntryDetailsRpcResponseData	responseData = new FolderEntryDetailsRpcResponseData( fed );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FOLDER_ENTRY_TYPE:
		{
			GetFolderEntryTypeCmd gfetCmd;
			GetFolderEntryTypeRpcResponseData responseData;
			HashMap<Long,GwtFolderEntryType> listOfEntryTypes;
			
			gfetCmd = (GetFolderEntryTypeCmd) cmd;
			listOfEntryTypes = GwtFolderEntryTypeHelper.getFolderEntryTypes( this, gfetCmd.getListOfEntryIds() );
			responseData = new GetFolderEntryTypeRpcResponseData();
			responseData.setListOfTypes( listOfEntryTypes );
			
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FOLDER_FILTERS:
		{
			GetFolderFiltersCmd gffCmd = ((GetFolderFiltersCmd) cmd);
			GwtFolder gwtFolder = gffCmd.getFolder();
			FolderFiltersRpcResponseData result = GwtServerHelper.getFolderFilters( this, req, Long.parseLong( gwtFolder.getFolderId() ) );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_FOLDER_HAS_USER_LIST:
		{
			GetFolderHasUserListCmd gfhulCmd = ((GetFolderHasUserListCmd) cmd);
			boolean result = GwtViewHelper.getFolderHasUserList( this, req , gfhulCmd.getFolderInfo() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case GET_FOLDER_ROWS:
		{
			GetFolderRowsCmd gfrCmd = ((GetFolderRowsCmd) cmd);
			FolderRowsRpcResponseData responseData = GwtViewHelper.getFolderRows(
				this,
				req,
				gfrCmd.getFolderInfo(),
				gfrCmd.getFolderDisplayData(),
				gfrCmd.getFolderColumns(),
				gfrCmd.getStart(),
				gfrCmd.getLength(),
				gfrCmd.getQuickFilter(),
				gfrCmd.getAuthenticationGuid() );
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
			GetFolderToolbarItemsRpcResponseData responseData = GwtMenuHelper.getFolderToolbarItems( this, req, gftiCmd.getFolderInfo() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_FOOTER_TOOLBAR_ITEMS:
		{
			GetFooterToolbarItemsCmd gftiCmd;
			List<ToolbarItem> result;
			GetToolbarItemsRpcResponseData responseData;
			
			gftiCmd = ((GetFooterToolbarItemsCmd) cmd);
		    result = GwtMenuHelper.getFooterToolbarItems( this, req, gftiCmd.getEntityId() );
			responseData = new GetToolbarItemsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_GROUP_ACTION_TOOLBAR_ITEMS:
		{
			GetGroupActionToolbarItemsCmd ggatbiCmd = ((GetGroupActionToolbarItemsCmd) cmd);
			GetToolbarItemsRpcResponseData responseData = GwtMenuHelper.getGroupActionToolbarItems( this, req, ggatbiCmd.getGroupId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_GROUP_ASSIGNEE_MEMBERSHIP:
		{
			GetGroupAssigneeMembershipCmd ggamCmd = ((GetGroupAssigneeMembershipCmd) cmd);
			List<AssignmentInfo> results = getGroupAssigneeMembership( req, ggamCmd.getGroupId() );
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
												req,
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
		
		case GET_GROUP_MEMBERSHIP_INFO:
		{
			GetGroupMembershipInfoCmd ggmtCmd;
			GroupMembershipInfo result;
			
			ggmtCmd = (GetGroupMembershipInfoCmd) cmd;
			result = GwtServerHelper.getGroupMembershipInfo( this, ggmtCmd.getGroupId() );
			return new VibeRpcResponse( result );
		}
		
		case GET_GROUPS:
		{
			GetGroupsCmd ggCmd;
			List<GroupInfo> result;
			GetGroupsRpcResponseData responseData;
			
			ggCmd = (GetGroupsCmd) cmd;
			result = getGroups( req, ggCmd.getBinderId() );
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
			result = getHorizontalNode( req, ghnCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}

		case GET_HORIZONTAL_TREE:
		{
			GetHorizontalTreeCmd ghtCmd;
			List<TreeInfo> result;
			GetHorizontalTreeRpcResponseData responseData;
			
			ghtCmd = (GetHorizontalTreeCmd) cmd;
			result = getHorizontalTree( req, ghtCmd.getBinderId(), ghtCmd.getTreeMode() );
			responseData = new GetHorizontalTreeRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_HISTORY_INFO:
		{
			GetHistoryInfoCmd ghiCmd = ((GetHistoryInfoCmd) cmd);
			HistoryInfo responseData = GwtHistoryHelper.getHistoryInfo( req, ghiCmd.getHistoryToken() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_HTML5_SPECS:
		{
			@SuppressWarnings( "unused" )
			GetHtml5SpecsCmd gcwCmd = ((GetHtml5SpecsCmd) cmd);
			Html5SpecsRpcResponseData result = GwtHtml5Helper.getHtml5UploadSpecs( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_IM_URL:
		{
			GetImUrlCmd giuCmd;
			String result;
			StringRpcResponseData responseData;
			
			giuCmd = (GetImUrlCmd) cmd;
			result = getImUrl( req, giuCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_INHERITED_LANDING_PAGE_PROPERTIES:
		{
			GetInheritedLandingPagePropertiesCmd gilppCmd;
			GwtLandingPageProperties lpProperties;
			
			gilppCmd = (GetInheritedLandingPagePropertiesCmd) cmd;
			lpProperties = GwtServerHelper.getInheritedLandingPageProperties( this, gilppCmd.getBinderId(), req );
			response = new VibeRpcResponse( lpProperties );
			return response;
		}
		
		case GET_IS_USER_EXTERNAL:
		{
			GetIsUserExternalCmd giueCmd = ((GetIsUserExternalCmd) cmd);
			boolean result = GwtViewHelper.isUserExternal( this, req, giueCmd.getUserId() );
			BooleanRpcResponseData responseData = new BooleanRpcResponseData( result );
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
		
		case GET_NET_FOLDER_GLOBAL_SETTINGS:
		{
			GwtNetFolderGlobalSettings nfGlobalSettings; 

			nfGlobalSettings = GwtServerHelper.getNetFolderGlobalSettings( this );
			response = new VibeRpcResponse( nfGlobalSettings );
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
		
		case GET_KEYSHIELD_CONFIG:
		{
			GwtKeyShieldConfig config = GwtKeyShieldSSOHelper.getKeyShieldConfig(this);
			return new VibeRpcResponse(config);
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
		
		case GET_LDAP_CONFIG:
		{
			GwtLdapConfig ldapConfig;
			
			ldapConfig = GwtLdapHelper.getLdapConfig( this );
			response = new VibeRpcResponse( ldapConfig );
			return response;
		}
		
		case GET_LDAP_OBJECT_FROM_AD:
		{
			GetLdapObjectFromADCmd gloCmd;
			GwtADLdapObject ldapObject;
			
			gloCmd = (GetLdapObjectFromADCmd) cmd;
			ldapObject = GwtLdapHelper.getLdapObjectFromAD(
														this,
														gloCmd.getFQDN() );
			response = new VibeRpcResponse( ldapObject );
			
			return response;
		}
		
		case GET_LDAP_SERVER_DATA:
		{
			GetLdapServerDataCmd gcwCmd = ((GetLdapServerDataCmd) cmd);
			LdapServerDataRpcResponseData result = LdapBrowserHelper.getLdapServerData( this, req, gcwCmd.getDirectoryServer(), gcwCmd.getSearchInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_LDAP_SYNC_RESULTS:
		{
			GetLdapSyncResultsCmd glsrCmd;
			GwtLdapSyncResults ldapSyncResults;
			
			glsrCmd = (GetLdapSyncResultsCmd) cmd;
			ldapSyncResults = GwtLdapHelper.getLdapSyncResults( req, glsrCmd.getSyncId() );
			response = new VibeRpcResponse( ldapSyncResults );
			return response;
		}
		
		case GET_LIMIT_USER_VISIBILITY_INFO:
		{
			LimitUserVisibilityInfoRpcResponseData result = GwtUserVisibilityHelper.getLimitUserVisibilityInfo( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_LIST_OF_CHILD_BINDERS:
		{
			GetListOfChildBindersCmd glocbCmd;
			GetListOfChildBindersRpcResponseData responseData;
			ArrayList<TreeInfo> listOfChildBinders;
			
			glocbCmd = (GetListOfChildBindersCmd) cmd;
			listOfChildBinders = GwtServerHelper.getListOfChildBinders( req, this, false, glocbCmd.getBinderId() );
			responseData = new GetListOfChildBindersRpcResponseData( listOfChildBinders );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_LIST_OF_FILES:
		{
			// Get a list of files from the given folder.
			GetListOfFilesCmd glofCmd = ((GetListOfFilesCmd) cmd);
			ArrayList<GwtAttachment> listOfFiles = getListOfFiles(req, glofCmd.getZoneUUID(), glofCmd.getFolderId(), glofCmd.getNumFiles());
			GetListOfFilesRpcResponseData responseData = new GetListOfFilesRpcResponseData(listOfFiles);
			response = new VibeRpcResponse(responseData);
			return response;
		}
	
		case GET_LOCALES:
		{
			GwtLocales responseData;
			
			responseData = GwtServerHelper.getLocales();
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_LOGIN_INFO:
		{
			GwtLoginInfo loginInfo;
			
			loginInfo = getLoginInfo( req );
			response = new VibeRpcResponse( loginInfo );
			return response;
		}
		
		case GET_MANAGE_ADMINISTRATORS_INFO:
		{
			ManageAdministratorsInfoRpcResponseData result = GwtServerHelper.getManageAdministratorsInfo( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_MANAGE_MOBILE_DEVICES_INFO:  {
			GetManageMobileDevicesInfoCmd gmmdiCmd = ((GetManageMobileDevicesInfoCmd) cmd); 
			ManageMobileDevicesInfoRpcResponseData result = GwtMobileDeviceHelper.getManageMobileDevicesInfo(this, req, gmmdiCmd.getUserId());
			response = new VibeRpcResponse(result);
			return response;
		}
		
		case GET_MANAGE_PROXY_IDENTITIES_INFO:  {
			ManageProxyIdentitiesInfoRpcResponseData result = GwtProxyIdentityHelper.getManageProxyIdentitiesInfo(this, req);
			response = new VibeRpcResponse(result);
			return response;
		}
		
		case GET_MANAGE_TEAMS_INFO:
		{
			ManageTeamsInfoRpcResponseData result = GwtServerHelper.getManageTeamsInfo( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_MANAGE_USERS_INFO:
		{
			ManageUsersInfoRpcResponseData result = GwtServerHelper.getManageUsersInfo( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_MANAGE_USERS_STATE:
		{
			ManageUsersStateRpcResponseData result = GwtServerHelper.getManageUsersState( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_MAILTO_PUBLIC_LINKS:
		{
			GetMailToPublicLinksCmd gmtplCmd = ((GetMailToPublicLinksCmd) cmd);
			MailToPublicLinksRpcResponseData result = GwtShareHelper.getMailToPublicLinks( this, req, gmtplCmd.getEntityId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_MAIN_PAGE_INFO:
		{
			GetMainPageInfoCmd gcwCmd = ((GetMainPageInfoCmd) cmd);
			MainPageInfoRpcResponseData result = GwtServerHelper.getMainPageInfo( this, req, gcwCmd.getBinderId() );
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
			result = getMicrBlogUrl( req, gmbuCmd.getBinderId() );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_MOBILE_APPS_CONFIG:
		{
			GwtZoneMobileAppsConfig mobileAppsConfiguration; 

			mobileAppsConfiguration = GwtServerHelper.getMobileAppsConfiguration( this );
			response = new VibeRpcResponse( mobileAppsConfiguration );
			return response;
		}
		
		case GET_MODIFY_BINDER_URL:
		{
			GetModifyBinderUrlCmd gmbuCmd;
			String url;
			StringRpcResponseData responseData;
			
			gmbuCmd = (GetModifyBinderUrlCmd) cmd;
			url = getModifyBinderUrl( req, gmbuCmd.getBinderId() );
			responseData = new StringRpcResponseData( url );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_MY_FILES_CONTAINER_INFO:
		{
			BinderInfo binderInfo = GwtServerHelper.getMyFilesContainerInfo( this, req );
			response = new VibeRpcResponse( binderInfo );
			return response;
		}
		
		case GET_MY_TASKS:
		{
			List<TaskInfo> results;
			TaskInfoListRpcResponseData responseData;

			results = getMyTasks( req );
			responseData = new TaskInfoListRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_MY_TEAMS:
		{
			GetMyTeamsRpcResponseData responseData;
			List<TeamInfo> result;
			
			result = getMyTeams(req);
			responseData = new GetMyTeamsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_NAME_COMPLETION_SETTINGS:
		{
			GwtNameCompletionSettings settings;
			
			settings = GwtServerHelper.getNameCompletionSettings( this );
			response = new VibeRpcResponse( settings );
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
		
		case GET_NET_FOLDER_SYNC_STATISTICS:
		{
			GetNetFolderSyncStatisticsCmd gnfssCmd;
			NetFolderSyncStatistics syncStatistics;
			
			gnfssCmd = (GetNetFolderSyncStatisticsCmd) cmd; 
			syncStatistics = GwtNetFolderHelper.getNetFolderSyncStatistics( gnfssCmd.getNetFolderId() );
			response = new VibeRpcResponse( syncStatistics );
			return response;
		}
		
		case GET_NEXT_PREVIOUS_FOLDER_ENTRY_INFO:
		{
			GetNextPreviousFolderEntryInfoCmd gnpfeiCmd = ((GetNextPreviousFolderEntryInfoCmd) cmd);
			ViewFolderEntryInfoRpcResponseData result = GwtViewHelper.getNextPreviousFolderInfo( this, req, gnpfeiCmd.getEntityId(), gnpfeiCmd.isPrevious() );
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
		
		case GET_PARENT_BINDER_PERMALINK:
		{
			GetParentBinderPermalinkCmd gpbpCmd = ((GetParentBinderPermalinkCmd) cmd);
			String permalink = getParentBinderPermalink( req, gpbpCmd.getBinderId(), gpbpCmd.isShowCollectionOnUserWS() );
			StringRpcResponseData responseData = new StringRpcResponseData( permalink );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_PASSWORD_EXPIRATION:
		{
			// Return the current user's password expiration status.
			response = new VibeRpcResponse( getPasswordExpirationInfo( GwtServerHelper.getCurrentUser() ) );
			return response;
		}
		
		case GET_PASSWORD_POLICY_CONFIG:
		{
			PasswordPolicyConfig ppConfig = GwtServerHelper.getPasswordPolicyConfig( this, req );
			response = new VibeRpcResponse( ppConfig );
			return response;
		}
		
		case GET_PASSWORD_POLICY_INFO:
		{
			PasswordPolicyInfoRpcResponseData ppInfo = GwtServerHelper.getPasswordPolicyInfo( this, req );
			response = new VibeRpcResponse( ppInfo );
			return response;
		}
		
		case GET_PERSONAL_PREFERENCES:
		{
			GwtPersonalPreferences prefs;
			
			prefs = GwtServerHelper.getPersonalPreferences( this, req );
			response = new VibeRpcResponse( prefs );
			return response;
		}
		
		case GET_PHOTO_ALBUM_DISPLAY_DATA:
		{
			GetPhotoAlbumDisplayDataCmd gpaddCmd = ((GetPhotoAlbumDisplayDataCmd) cmd);
			PhotoAlbumDisplayDataRpcResponseData responseData = GwtPhotoAlbumHelper.getPhotoAlbumDisplayData( req, this, gpaddCmd.getBinderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_PERSONAL_WORKSPACE_DISPLAY_DATA:
		{
			GetPersonalWorkspaceDisplayDataCmd gpwsddCmd = ((GetPersonalWorkspaceDisplayDataCmd) cmd);
			PersonalWorkspaceDisplayDataRpcResponseData responseData = GwtPersonalWorkspaceHelper.getPersonalWorkspaceDisplayData( req, this, gpwsddCmd.getBinderInfo() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_PRESENCE_INFO:
		{
			GetPresenceInfoCmd gpiCmd = ((GetPresenceInfoCmd) cmd);
			GwtPresenceInfo result = getPresenceInfo(req, gpiCmd.getUserId(), gpiCmd.getBinderId());
			response = new VibeRpcResponse(result);
			return response;
		}
		
		case GET_PRINCIPAL_FILE_SYNC_APP_CONFIG:
		{
			GetPrincipalFileSyncAppConfigCmd gpfsacCmd = ((GetPrincipalFileSyncAppConfigCmd) cmd);
			GwtPrincipalFileSyncAppConfig config = GwtServerHelper.getPrincipalFileSyncAppConfig(this, gpfsacCmd.getPrincipalId());
			response = new VibeRpcResponse(config);
			return response;
		}
		
		case GET_PRINCIPAL_INFO:
		{
			GetPrincipalInfoCmd gpiCmd = ((GetPrincipalInfoCmd) cmd);
			PrincipalInfoRpcResponseData pInfo = GwtServerHelper.getPrincipalInfo(this, req, gpiCmd.getPrincipalId());
			response = new VibeRpcResponse(pInfo);
			return response;
		}
		
		case GET_PRINCIPAL_MOBILE_APPS_CONFIG:
		{
			GwtPrincipalMobileAppsConfig config;
			GetPrincipalMobileAppsConfigCmd gpmacCmd;

			gpmacCmd = (GetPrincipalMobileAppsConfigCmd) cmd;
			config = GwtServerHelper.getPrincipalMobileAppsConfig( this, gpmacCmd.getPrincipalId() );
			response = new VibeRpcResponse( config );
			return response;
		}
		
		case GET_PROFILE_AVATARS:
		{
			GetProfileAvatarsCmd gpaCmd;
			ProfileAttribute result;
			String binderId;
			
			gpaCmd = (GetProfileAvatarsCmd) cmd;
			
			// Were we given the user's workspace id?
			binderId = gpaCmd.getBinderId();
			if ( binderId == null )
			{
				Long userId;
				
				// No
				// Were we given the user's id?
				userId = gpaCmd.getUserId();
				if ( userId != null )
				{
					User user;
					Long wsId;
					
					// Yes, get the user's workspace id.
					try
					{
						user = (User) getProfileModule().getEntry( userId );
						wsId = user.getWorkspaceId();
						if ( wsId != null )
							binderId = String.valueOf( wsId );
					}
					catch ( Exception ex )
					{
						logger.error( "Servicing GetProfileAvatarsCmd, ex: " + ex.toString() );
					}
				}
			}
			
			if ( binderId != null )
				result = getProfileAvatars( req, binderId );
			else
				result = new ProfileAttribute();

			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_PROFILE_ENTRY_INFO:
		{
			GetProfileEntryInfoCmd gcwCmd = ((GetProfileEntryInfoCmd) cmd);
			ProfileEntryInfoRpcResponseData result = GwtViewHelper.getProfileEntryInfo(
				this,
				req,
				gcwCmd.getUserId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_PROFILE_INFO:
		{
			GetProfileInfoCmd gpiCmd;
			ProfileInfo result;
			
			gpiCmd = (GetProfileInfoCmd) cmd;
			result = getProfileInfo( req, gpiCmd.getBinderId() );
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
			result = getProfileStats( req, gpsCmd.getBinderId(), gpsCmd.getUserId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_PUBLIC_LINKS:
		{
			GetPublicLinksCmd gplCmd = ((GetPublicLinksCmd) cmd);
			PublicLinksRpcResponseData result = GwtShareHelper.getPublicLinks( this, req, gplCmd.getEntityIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_QUICK_VIEW_INFO:
		{
			GetQuickViewInfoCmd gqviCmd = ((GetQuickViewInfoCmd) cmd);
			ProfileInfo result = getQuickViewInfo(req, gqviCmd.getUserId(), gqviCmd.getBinderId());
			response = new VibeRpcResponse(result);
			return response;
		}

		case GET_RECENT_PLACES:
		{
			List<RecentPlaceInfo> result = GwtMenuHelper.getRecentPlaces( this, req, ((GetRecentPlacesCmd) cmd).getBinderId() );
			GetRecentPlacesRpcResponseData responseData = new GetRecentPlacesRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_REPORTS_INFO:
		{
			ReportsInfoRpcResponseData result = GwtReportsHelper.getReportsInfo( this, req );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ROOT_WORKSPACE_ID:
		{
			GetRootWorkspaceIdCmd grwiCmd = ((GetRootWorkspaceIdCmd) cmd);
			Long result = getRootWorkspaceId( req, grwiCmd.getCurrentRootBinderId(), grwiCmd.getBinderId() );
			LongRpcResponseData responseData = new LongRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_SAVED_SEARCHES:
		{
			GetSavedSearchesRpcResponseData responseData;
			List<SavedSearchInfo> result;
			
			result = getSavedSearches(req);
			responseData = new GetSavedSearchesRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case GET_SELECTED_USERS_DETAILS:
		{
			GetSelectedUsersDetailsCmd gsudCmd = ((GetSelectedUsersDetailsCmd) cmd);
			SelectedUsersDetails result = GwtViewHelper.getSelectedUsersDetails( this, req, gsudCmd.getUserIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_SELECTION_DETAILS:
		{
			GetSelectionDetailsCmd gsdCmd = ((GetSelectionDetailsCmd) cmd);
			SelectionDetails result = GwtViewHelper.getSelectionDetails( this, req, gsdCmd.getEntityIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_SEND_TO_FRIEND_URL:
		{
			GetSendToFriendUrlCmd gstfuCmd;
			String url;
			StringRpcResponseData responseData;
			
			gstfuCmd = (GetSendToFriendUrlCmd) cmd;
			url = getSendToFriendUrl( req, gstfuCmd.getEntryId() );
			responseData = new StringRpcResponseData( url );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_UPDATE_LOGS_CONFIG:
		{
			UpdateLogsConfig ulConfig = GwtServerHelper.getUpdateLogsConfig( this, req );
			response = new VibeRpcResponse( ulConfig );
			return response;
		}
		
		case GET_WIKI_DISPLAY_DATA:
		{
			GetWikiDisplayDataCmd gwddCmd = ((GetWikiDisplayDataCmd) cmd);
			WikiDisplayDataRpcResponseData responseData = GwtWikiHelper.getWikiDisplayData( req, this, gwddCmd.getBinderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_ZIP_DOWNLOAD_FILES_URL:
		{
			GetZipDownloadFilesUrlCmd gzdfuCmd = ((GetZipDownloadFilesUrlCmd) cmd);
			ZipDownloadUrlRpcResponseData result = GwtViewHelper.getZipDownloadUrl( this, req, gzdfuCmd.getEntityIds(), gzdfuCmd.isRecursive() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ZIP_DOWNLOAD_FOLDER_URL:
		{
			GetZipDownloadFolderUrlCmd gzdfuCmd = ((GetZipDownloadFolderUrlCmd) cmd);
			ZipDownloadUrlRpcResponseData result = GwtViewHelper.getZipDownloadUrl( this, req, gzdfuCmd.getFolderId(), gzdfuCmd.isRecursive() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_ZONE_SHARE_RIGHTS:
		{
			ZoneShareRights shareSettings;
			
			shareSettings = GwtShareHelper.getZoneShareRights( this );
			response = new VibeRpcResponse( shareSettings );
			return response;
		}
		
		case GET_SHARE_LISTS:
		{
			@SuppressWarnings( "unused" )
			GetShareListsCmd gslCmd = ((GetShareListsCmd) cmd);
			GwtShareLists shareLists = GwtShareHelper.getShareLists( this, req);
			ShareListsRpcResponseData result = new ShareListsRpcResponseData( shareLists );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_SHARED_VIEW_STATE:
		{
			GetSharedViewStateCmd gsvsCmd = ((GetSharedViewStateCmd) cmd);
			SharedViewStateRpcResponseData result = GwtViewHelper.getSharedViewState( this, req, gsvsCmd.getCollectionType() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_SHARING_INFO:
		{
			GetSharingInfoCmd gsiCmd;
			GwtSharingInfo sharingInfo;
			
			gsiCmd = (GetSharingInfoCmd) cmd;
			sharingInfo = GwtShareHelper.getSharingInfo(
													this,
													gsiCmd.getListOfEntities(),
													gsiCmd.getSharedById() );
			response = new VibeRpcResponse( sharingInfo );
			return response;
		}
		
		case GET_SIGN_GUESTBOOK_URL:
		{
			GetSignGuestbookUrlCmd gsgbUrlCmd = ((GetSignGuestbookUrlCmd) cmd);
			StringRpcResponseData responseData = GwtMenuHelper.getSignGuestbookUrl(
				this,
				req,
				gsgbUrlCmd.getFolderId() );
			return new VibeRpcResponse( responseData );
		}
		
		case GET_SITE_ADMIN_URL:
		{
			String url;
			String binderId;
			StringRpcResponseData responseData;
			
			binderId = ((GetSiteAdminUrlCmd) cmd).getBinderId();
			url = getSiteAdministrationUrl( req, binderId );
			responseData = new StringRpcResponseData( url );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_SITE_BRANDING:
		{
			GwtBrandingData brandingData = getSiteBrandingData(req, getServletContext(ri));
			response = new VibeRpcResponse(brandingData);
			return response;
		}
		
		case GET_SUBSCRIPTION_DATA:
		{
			GetSubscriptionDataCmd gsdCmd = ((GetSubscriptionDataCmd) cmd);
			SubscriptionData result = getSubscriptionData( req, gsdCmd.getEntryId() );
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
														req,
														gsbpCmd.getSystemBinderType() );
			responseData = new StringRpcResponseData( permalink );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_SYSTEM_ERROR_LOG_URL:
		{
			String systemErrorLogUrl = GwtReportsHelper.getSystemErrorLogUrl( this, req );
			return new VibeRpcResponse( new StringRpcResponseData( systemErrorLogUrl ));
		}
		
		case GET_TAG_RIGHTS_FOR_BINDER:
		{
			GetTagRightsForBinderCmd gtrfbCmd;
			ArrayList<Boolean> result;
			GetTagRightsRpcResponseData responseData;
			
			gtrfbCmd = (GetTagRightsForBinderCmd) cmd;
			result = getTagRightsForBinder( req, gtrfbCmd.getBinderId() );
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
			result = getTagRightsForEntry( req, gtrfeCmd.getEntryId() );
			responseData = new GetTagRightsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TAG_SORT_ORDER:
		{
			TagSortOrder result;
			
			result = getTagSortOrder(req);
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_TASK_BUNDLE:
		{
			GetTaskBundleCmd gtbCmd = ((GetTaskBundleCmd) cmd);
			TaskBundle results = getTaskBundle( req, gtbCmd.isEmbeddedInJSP(), gtbCmd.getBinderId(), gtbCmd.getFilterType(), gtbCmd.getModeType() );
			TaskBundleRpcResponseData responseData = new TaskBundleRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TASK_DISPLAY_DATA:
		{
			GetTaskDisplayDataCmd gtddCmd = ((GetTaskDisplayDataCmd) cmd);
			TaskDisplayDataRpcResponseData responseData = GwtTaskHelper.getTaskDisplayData( req, this, gtddCmd.getBinderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TASK_LINKAGE:
		{
			GetTaskLinkageCmd gtlCmd = ((GetTaskLinkageCmd) cmd);
			TaskLinkage results = getTaskLinkage( req, gtlCmd.getBinderId() );
			TaskLinkageRpcResponseData responseData = new TaskLinkageRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TASK_LIST:
		{
			GetTaskListCmd gtlCmd = ((GetTaskListCmd) cmd);
			List<TaskListItem> results = getTaskList( req, gtlCmd.getApplyUsersFilter(), gtlCmd.getZoneUUID(), gtlCmd.getBinderId(), gtlCmd.getFilterType(), gtlCmd.getModeType() );
			TaskListItemListRpcResponseData responseData = new TaskListItemListRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TEAM_ASSIGNEE_MEMBERSHIP:
		{
			GetTeamAssigneeMembershipCmd gtamCmd = ((GetTeamAssigneeMembershipCmd) cmd);
			List<AssignmentInfo> results = getTeamAssigneeMembership( req, gtamCmd.getBinderId() );
			AssignmentInfoListRpcResponseData responseData = new AssignmentInfoListRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TEAM_MANAGEMENT_INFO:
		{
			GetTeamManagementInfoCmd gtmiCmd;
			TeamManagementInfo result;
			
			gtmiCmd = (GetTeamManagementInfoCmd) cmd;
			result = GwtMenuHelper.getTeamManagementInfo( this, req, gtmiCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_TEAMS:
		{
			GetTeamsCmd gtCmd;
			List<TeamInfo> result;
			GetMyTeamsRpcResponseData responseData;
			
			gtCmd = (GetTeamsCmd) cmd;
			result = getTeams( req, gtCmd.getBinderId() );
			responseData = new GetMyTeamsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TIME_ZONES:
		{
			GwtTimeZones responseData;
			
			responseData = GwtServerHelper.getTimeZones( req );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TOOLBAR_ITEMS:
		{
			GetToolbarItemsCmd gtiCmd = ((GetToolbarItemsCmd) cmd);
			List<ToolbarItem> result = GwtMenuHelper.getToolbarItems( this, req, gtiCmd.getBinderId() );
			GetToolbarItemsRpcResponseData responseData = new GetToolbarItemsRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TOP_RANKED:
		{
			List<TopRankedInfo> result = GwtServerHelper.getTopRankedFromCache( this, req);
			GetTopRankedRpcResponseData responseData = new GetTopRankedRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_TRASH_URL:
		{
			GetTrashUrlCmd gtuCmd = ((GetTrashUrlCmd) cmd);
			StringRpcResponseData responseData = GwtDeleteHelper.getTrashUrl( this, req, gtuCmd.getBinderInfo() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_UPGRADE_INFO:
		{
			GwtUpgradeInfo upgradeInfo;
			
			upgradeInfo = getUpgradeInfo(req);
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
		
		case GET_USER_AVATAR:
		{
			GetUserAvatarCmd guaCmd;
			StringRpcResponseData responseData;
			String url = null;
			Long userId;
			
			guaCmd = (GetUserAvatarCmd) cmd;
			userId = guaCmd.getUserId();
			if ( userId != null )
			{
				try
				{
					User user;
					
					// Yes, get the user object
					user = (User) getProfileModule().getEntry( userId );

					url = GwtServerHelper.getUserAvatarUrl( this, req, user );
				}
				catch ( Exception ex )
				{
					logger.error( "Servicing GetUserAvatarCmd, ex: " + ex.toString() );
				}
			}

			responseData = new StringRpcResponseData( url );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_USER_LIST_INFO:
		{
			GetUserListInfoCmd guliCmd = ((GetUserListInfoCmd) cmd);
			UserListInfoRpcResponseData result = GwtViewHelper.getFolderUserListInfo( this, req , guliCmd.getFolderInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_USER_PERMALINK:
		{
			GetUserPermalinkCmd gupCmd;
			String permalink;
			StringRpcResponseData responseData;
			
			gupCmd = (GetUserPermalinkCmd) cmd;
			permalink = getUserPermalink( req, gupCmd.getUserId() );
			responseData = new StringRpcResponseData( permalink );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case GET_USER_PROPERTIES:
		{
			GetUserPropertiesCmd gmuiCmd = ((GetUserPropertiesCmd) cmd);
			UserPropertiesRpcResponseData result = GwtViewHelper.getUserProperties( this, req, gmuiCmd.getUserId(), gmuiCmd.getIncludeLastLogin() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_USER_SHARING_RIGHTS_INFO:
		{
			GetUserSharingRightsInfoCmd gusrCmd = ((GetUserSharingRightsInfoCmd) cmd);
			UserSharingRightsInfoRpcResponseData result = GwtServerHelper.getUserSharingRightsInfo( this, req, gusrCmd.getUserIds() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_USER_STATUS:
		{
			GetUserStatusCmd gusCmd = ((GetUserStatusCmd) cmd);
			UserStatus result = getUserStatus(req, gusCmd.getUserId(), gusCmd.getBinderId());
			response = new VibeRpcResponse(result);
			return response;
		}
		
		case GET_USER_WORKSPACE_INFO:
		{
			GetUserWorkspaceInfoCmd gusCmd = ((GetUserWorkspaceInfoCmd) cmd);
			UserWorkspaceInfoRpcResponseData result = getUserWorkspaceInfo( req, gusCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_USER_ZONE_SHARE_SETTINGS:
		{
			GetUserZoneShareSettingsCmd gussCmd = (GetUserZoneShareSettingsCmd) cmd;
			PerEntityShareRightsInfo shareSettings = GwtShareHelper.getUserZoneShareSettings( this, gussCmd.getPrincipalId() );
			response = new VibeRpcResponse( shareSettings );
			return response;
		}
		
		case GET_VERTICAL_ACTIVITY_STREAMS_TREE:
		{
			GetVerticalActivityStreamsTreeCmd gvastCmd;
			TreeInfo result;
			
			gvastCmd = (GetVerticalActivityStreamsTreeCmd) cmd;
			result = getVerticalActivityStreamsTree( req, gvastCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_VERTICAL_NODE:
		{
			GetVerticalNodeCmd gvnCmd = ((GetVerticalNodeCmd) cmd);
			TreeInfo result = getVerticalNode( req, gvnCmd.isFindBrowser(), gvnCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_VERTICAL_TREE:
		{
			GetVerticalTreeCmd gvtCmd = ((GetVerticalTreeCmd) cmd);
			TreeInfo result = getVerticalTree( req, gvtCmd.isFindBrowser(), gvtCmd.getBinderId() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case GET_VIEW_FILE_URL:
		{
			GetViewFileUrlCmd gvfuCmd = ((GetViewFileUrlCmd) cmd);
			String result = getViewFileUrl(req, gvfuCmd.getViewFileInfo() );
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
			result = GwtServerHelper.getViewFolderEntryUrl( this, req, binderId, entryId );
			responseData = new StringRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case GET_WEBACCESS_SETTING:
		{
			GetWebAccessSettingCmd gwasCmd = ((GetWebAccessSettingCmd) cmd);
			Long                   upId    = gwasCmd.getUserPrincipalId();
			Boolean                result;
			if (null != upId)
			     result = GwtUIHelper.getWebAccessSettingFromUserOrGroup( this, upId );
			else result = GwtUIHelper.getWebAccessSettingFromZone(        this       );
			if (null == result) {
				result = Boolean.FALSE;
			}
			
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case GET_WHO_HAS_ACCESS:
		{
			GetWhoHasAccessCmd gwhaCmd = ((GetWhoHasAccessCmd) cmd);
			WhoHasAccessInfoRpcResponseData result = GwtViewHelper.getWhoHasAccess( this, req, gwhaCmd.getEntityId() );
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
			listOfIds = getWorkspaceContributorIds( req,gwciCmd.getWorkspaceId() );
			responseData = new GetWorkspaceContributorIdsRpcResponseData( listOfIds );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case HAS_ACTIVITY_STREAM_CHANGED:
		{
			ActivityStreamInfo asi = ((HasActivityStreamChangedCmd) cmd).getActivityStreamInfo();
			Boolean result = hasActivityStreamChanged(req, asi);
			response = new VibeRpcResponse(new BooleanRpcResponseData(result));
			return response;
		}

		case HIDE_SHARES:
		{
			HideSharesCmd hsCmd = ((HideSharesCmd) cmd);
			Boolean result = GwtViewHelper.hideShares( this, req, hsCmd.getCollectionType(), hsCmd.getEntityIds() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case IMPORT_ICAL_BY_URL:
		{
			ImportIcalByUrlCmd iiUrlCmd = ((ImportIcalByUrlCmd) cmd);
			ImportIcalByUrlRpcResponseData result = GwtServerHelper.importIcalByUrl( this, req, iiUrlCmd.getFolderId(), iiUrlCmd.getUrl() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case IS_ALL_USERS_GROUP:
		{
			String groupId = ((IsAllUsersGroupCmd) cmd).getGroupId();
			IsAllUsersGroupRpcResponseData result = GwtServerHelper.isAllUsersGroup( this, Long.parseLong( groupId ));	//Note, this checks for either allUsers or allExtUsers
			response = new VibeRpcResponse( result );
			return response;
		}

		case IS_PERSON_TRACKED:
		{
			IsPersonTrackedCmd iptCmd;
			iptCmd = (IsPersonTrackedCmd) cmd;
			Boolean result = isPersonTracked(req, iptCmd.getBinderId());
			BooleanRpcResponseData responseData = new BooleanRpcResponseData(result);
			response = new VibeRpcResponse(responseData);
			return response;
		}
		
		case IS_SEEN:
		{
			Long entryId = ((IsSeenCmd) cmd).getEntryId();
			Boolean result = isSeen( req, entryId );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}

		case LDAP_AUTHENTICATE_USER:
		{
			LdapAuthenticateUserCmd gcwCmd = ((LdapAuthenticateUserCmd) cmd);
			StringRpcResponseData responseData = LdapBrowserHelper.authenticateUser( this, req, gcwCmd.getDirectoryServer() );
			response = new VibeRpcResponse( responseData );
		}
		
		case LOCK_ENTRIES:
		{
			LockEntriesCmd leCmd = ((LockEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.lockEntries( this, req, leCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case MARK_FOLDER_CONTENTS_READ:
		{
			MarkFolderContentsReadCmd mfcrCmd = ((MarkFolderContentsReadCmd) cmd);
			BooleanRpcResponseData responseData = GwtViewHelper.markFolderContentsRead( this, req, mfcrCmd.getFolderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case MARK_FOLDER_CONTENTS_UNREAD:
		{
			MarkFolderContentsUnreadCmd mfcurCmd = ((MarkFolderContentsUnreadCmd) cmd);
			BooleanRpcResponseData responseData = GwtViewHelper.markFolderContentsUnread( this, req, mfcurCmd.getFolderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case MARKUP_STRING_REPLACEMENT:
		{
			MarkupStringReplacementCmd msr = ((MarkupStringReplacementCmd) cmd); 
			String binderId = msr.getBinderId();
			String html     = msr.getHtml();
			String type     = msr.getType();
			
			String newHtml = markupStringReplacement( req, binderId, html, type );
			StringRpcResponseData responseData = new StringRpcResponseData( newHtml );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case MODIFY_GROUP:
		{
			ModifyGroupCmd mgCmd;
			
			mgCmd = (ModifyGroupCmd) cmd;
			GwtServerHelper.modifyGroup(
									this,
									mgCmd.getId(),
									mgCmd.getTitle(),
									mgCmd.getDesc(),
									mgCmd.getIsMembershipDynamic(),
									mgCmd.getMembershipCriteria() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( Boolean.TRUE ) );
			
			return response;
		}
		
		case MODIFY_GROUP_MEMBERSHIP:
		{
			ModifyGroupMembershipCmd mgmCmd;
			
			mgmCmd = (ModifyGroupMembershipCmd) cmd;
			GwtServerHelper.modifyGroupMembership(
												this,
												mgmCmd.getId(),
												mgmCmd.getIsMembershipDynamic(),
												mgmCmd.getMembership(),
												mgmCmd.getMembershipCriteria() );
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
		
		case MODIFY_PROXY_IDENTITY:  {
			ModifyProxyIdentityCmd mpiCmd = ((ModifyProxyIdentityCmd) cmd);
			ProxyIdentityRpcResponseData responseData = GwtProxyIdentityHelper.modifyProxyIdentity(this, req, mpiCmd.getProxyIdentity()); 
			response = new VibeRpcResponse(responseData);
			return response;
		}

		case MOVE_ENTRIES:
		{
			MoveEntriesCmd meCmd = ((MoveEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.moveEntries( this, req, meCmd.getTargetFolderId(), meCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PERSIST_ACTIVITY_STREAM_SELECTION:
		{
			ActivityStreamInfo asi = ((PersistActivityStreamSelectionCmd) cmd).getActivityStreamInfo();
			Boolean result = persistActivityStreamSelection(req, asi);
			BooleanRpcResponseData responseData = new BooleanRpcResponseData(result);
			response = new VibeRpcResponse(responseData);
			return response;
		}
		
		case PERSIST_NODE_COLLAPSE:
		{
			PersistNodeCollapseCmd pncCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			pncCmd = (PersistNodeCollapseCmd) cmd;
			result = persistNodeCollapse( req, pncCmd.getBinderId() );
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
			result = persistNodeExpand( req,pneCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PIN_ENTRY:
		{
			PinEntryCmd peCmd = ((PinEntryCmd) cmd);
			Boolean result = GwtServerHelper.pinEntry( this, req, peCmd.getFolderId(), peCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case PURGE_TASKS:
		{
			PurgeTasksCmd ptCmd = ((PurgeTasksCmd) cmd);
			ErrorListRpcResponseData responseData = purgeTasks( req, ptCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case PUSH_HISTORY_INFO:
		{
			PushHistoryInfoCmd phiCmd = ((PushHistoryInfoCmd) cmd);
			StringRpcResponseData responseData = GwtHistoryHelper.pushHistoryInfo( req, phiCmd.getHistoryInfo() );
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
				result = removeExtension( req, reCmd.getId() );
			}
			catch (Exception ex)
			{
				GwtTeamingException gtEx;
				
				gtEx = GwtLogHelper.getGwtClientException( ex );
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
			result = GwtServerHelper.removeFavorite( this, req, rfCmd.getFavoriteId() );
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
			result = removeSavedSearch( req, rssCmd.getSavedSearchInfo() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}

		case REMOVE_TASK_LINKAGE:
		{
			RemoveTaskLinkageCmd rtlCmd = ((RemoveTaskLinkageCmd) cmd);
			Boolean result = removeTaskLinkage( req, rtlCmd.getBinderId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case RENAME_ENTITY:
		{
			RenameEntityCmd reCmd = ((RenameEntityCmd) cmd);
			StringRpcResponseData responseData = GwtViewHelper.renameEntity( this, req, reCmd.getEntityId(), reCmd.getEntityName() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case REPLY_TO_ENTRY:  {
			ReplyToEntryCmd reCmd = ((ReplyToEntryCmd) cmd);
			ActivityStreamEntry result = replyToEntry(
				req,
				reCmd.getEntryId(),
				reCmd.getTitle(),
				reCmd.getDescription());
			ActivityStreamEntryRpcResponseData responseData = new ActivityStreamEntryRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case REQUEST_RESET_PASSWORD:
		{
			RequestResetPwdCmd rpCmd;
			RequestResetPwdRpcResponseData result;
			
			rpCmd = (RequestResetPwdCmd) cmd;
			result = GwtServerHelper.requestResetPwd( this, req, rpCmd.getExtUserId(), rpCmd.getPwd() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_ACCESSORY_STATUS:
		{
			SaveAccessoryStatusCmd sasCmd = ((SaveAccessoryStatusCmd) cmd);
			Boolean responseData = GwtViewHelper.saveAccessoryStatus(
				this,
				req,
				sasCmd.getBinderId(),
				sasCmd.getShowAccessoryPanel() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( responseData ) );
			return response;
		}
		
		case SAVE_ADHOC_FOLDER_SETTING:
		{
			SaveAdhocFolderSettingCmd safsCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			safsCmd = (SaveAdhocFolderSettingCmd) cmd;
			result = GwtServerHelper.saveAdhocFolderSetting( this, safsCmd.getUserPrincipalId(), safsCmd.getAllowAdhocFolders() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_BINDER_REGION_STATE:
		{
			SaveBinderRegionStateCmd sbrsCmd = ((SaveBinderRegionStateCmd) cmd);
			BooleanRpcResponseData responseData = GwtViewHelper.saveBinderRegionState(
				this,
				req,
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
			result = saveBrandingData( req, cmd2.getBinderId(), cmd2.getBrandingData() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_CALENDAR_DAY_VIEW:
		{
			SaveCalendarDayViewCmd scdvCmd = ((SaveCalendarDayViewCmd) cmd);
			CalendarDisplayDataRpcResponseData result = GwtCalendarHelper.saveCalendarDayView(
				this,
				req,
				scdvCmd.getBrowserTZOffset(),
				scdvCmd.getFolderInfo(),
				scdvCmd.getDayView(),
				scdvCmd.getDate() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_CALENDAR_HOURS:
		{
			SaveCalendarHoursCmd schCmd = ((SaveCalendarHoursCmd) cmd);
			CalendarDisplayDataRpcResponseData result = GwtCalendarHelper.saveCalendarHours(
				this,
				req,
				schCmd.getBrowserTZOffset(),
				schCmd.getFolderInfo(), schCmd.getHours() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_CALENDAR_SETTINGS:
		{
			SaveCalendarSettingsCmd scsCmd = ((SaveCalendarSettingsCmd) cmd);
			Boolean result = GwtCalendarHelper.saveCalendarSettings( this, req, scsCmd.getFolderId(), scsCmd.getWeekStart(), scsCmd.getWorkDayStart() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SAVE_CALENDAR_SHOW:
		{
			SaveCalendarShowCmd scsCmd = ((SaveCalendarShowCmd) cmd);
			CalendarDisplayDataRpcResponseData result = GwtCalendarHelper.saveCalendarShow(
				this,
				req,
				scsCmd.getBrowserTZOffset(),
				scsCmd.getFolderInfo(),
				scsCmd.getShow() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_CLIPBOARD_USERS:
		{
			SaveClipboardUsersCmd scuCmd = ((SaveClipboardUsersCmd) cmd);
			BooleanRpcResponseData result = GwtServerHelper.saveClipboardUsers( this, req, scuCmd.getClipboardUserList() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_COLUMN_WIDTHS:
		{
			SaveColumnWidthsCmd scwCmd = ((SaveColumnWidthsCmd) cmd);
			BooleanRpcResponseData result = GwtViewHelper.saveColumnWidths(
				this,
				req,
				scwCmd.getFolderInfo(),
				scwCmd.getColumnWidths() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_DOWNLOAD_SETTING:
		{
			SaveDownloadSettingCmd sdsCmd = ((SaveDownloadSettingCmd) cmd);
			Boolean result = GwtServerHelper.saveDownloadSetting( this, sdsCmd.getUserId(), sdsCmd.isAllowDownload() );
			BooleanRpcResponseData responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_EMAIL_NOTIFICATION_INFORMATION:
		{
			SaveEmailNotificationInfoCmd seniCmd = ((SaveEmailNotificationInfoCmd) cmd);
			BooleanRpcResponseData result = GwtEmailHelper.saveEmailNotificationInfo(
				this,
				req,
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
			Boolean result = saveFolderColumns( req, sfcCmd.getFolderId(), sfcCmd.getFolderColumns(), sfcCmd.isFolderColumnsDefault() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_FOLDER_ENTRY_DLG_POSITION:
		{
			SaveFolderEntryDlgPositionCmd sfedpCmd = ((SaveFolderEntryDlgPositionCmd) cmd);
			GwtViewHelper.saveFolderEntryDlgPosition( this, req, sfedpCmd.getX(), sfedpCmd.getY(), sfedpCmd.getCX(), sfedpCmd.getCY() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( true ));
			return response;
		}
		
		case SAVE_FOLDER_FILTERS:
		{
			SaveFolderFiltersCmd sffCmd = ((SaveFolderFiltersCmd) cmd);
			ErrorListRpcResponseData result = GwtServerHelper.saveFolderFilters( this, req, sffCmd.getFolderInfo(), sffCmd.getGlobalFilters(), sffCmd.getPersonalFilters(), sffCmd.getSourceFolder() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_FOLDER_PINNING_STATE:
		{
			SaveFolderPinningStateCmd sfpsCmd = ((SaveFolderPinningStateCmd) cmd);
			GwtViewHelper.saveUserViewPinnedEntries( req, sfpsCmd.getFolderId(), sfpsCmd.getViewPinnedEntries() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( true ));
			return response;
		}
		
		case SAVE_FOLDER_SORT:
		{
			SaveFolderSortCmd sfsCmd = ((SaveFolderSortCmd) cmd);
			Boolean result = saveFolderSort( req, sfsCmd.getBinderInfo(), sfsCmd.getSortKey(), sfsCmd.getSortAscending() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_KEYSHIELD_CONFIG:
		{
			SaveKeyShieldConfigCmd skcCmd = ((SaveKeyShieldConfigCmd) cmd);
			SaveKeyShieldConfigRpcResponseData responseData = GwtKeyShieldSSOHelper.saveKeyShieldConfig(this, skcCmd.getConfig());
			response = new VibeRpcResponse(responseData);
			return response;
		}
		
		case SAVE_NET_FOLDER_GLOBAL_SETTINGS:
		{
			SaveNetFolderGlobalSettingsCmd snfgsCmd;
			Boolean result;
			
			snfgsCmd = ((SaveNetFolderGlobalSettingsCmd) cmd);
			result = GwtServerHelper.saveNetFolderGlobalSettings( this, snfgsCmd.getGlobalSettings() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SAVE_DATABASE_PRUNE_CONFIGURATION:
		{
			SaveDatabasePruneConfigurationCmd sdpcCmd;
			Boolean result;
			
			sdpcCmd = ((SaveDatabasePruneConfigurationCmd) cmd);
			result = GwtServerHelper.executeDatabasePruneCommand( this, sdpcCmd.getDatabasePruneConfiguration() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SAVE_LDAP_CONFIG:
		{
			SaveLdapConfigCmd slcCmd;
			SaveLdapConfigRpcResponseData responseData;
			
			slcCmd = (SaveLdapConfigCmd) cmd;
			responseData = GwtLdapHelper.saveLdapConfig( this, slcCmd.getLdapConfig() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_MANAGE_USERS_STATE:
		{
			SaveManageUsersStateCmd smusCmd = ((SaveManageUsersStateCmd) cmd);
			Boolean result = GwtServerHelper.saveManageUsersState( this, req, smusCmd.getManageUsersState() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SAVE_MOBILE_APPS_CONFIGURATION:
		{
			SaveMobileAppsConfigurationCmd smacCmd;
			Boolean result;
			
			smacCmd = ((SaveMobileAppsConfigurationCmd) cmd);
			result = GwtServerHelper.saveMobileAppsConfiguration( this, smacCmd.getMobileAppsConfiguration() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SAVE_MULTIPLE_ADHOC_FOLDER_SETTINGS:
		{
			SaveMultipleAdhocFolderSettingsCmd smafsCmd = ((SaveMultipleAdhocFolderSettingsCmd) cmd);
			ErrorListRpcResponseData result = GwtServerHelper.saveMultipleAdHocFolderSettings( this, smafsCmd.getUserIds(), smafsCmd.getAllowAdHocFolders() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_MULTIPLE_DOWNLOAD_SETTINGS:
		{
			SaveMultipleDownloadSettingsCmd smdsCmd = ((SaveMultipleDownloadSettingsCmd) cmd);
			ErrorListRpcResponseData result = GwtServerHelper.saveMultipleDownloadSettings( this, smdsCmd.getUserIds(), smdsCmd.getAllowDownload() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_MULTIPLE_WEBACCESS_SETTINGS:
		{
			SaveMultipleWebAccessSettingsCmd smwasCmd = ((SaveMultipleWebAccessSettingsCmd) cmd);
			ErrorListRpcResponseData result = GwtServerHelper.saveMultipleWebAccessSettings( this, smwasCmd.getUserIds(), smwasCmd.getAllowWebAccess() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_NAME_COMPLETION_SETTINGS:
		{
			SaveNameCompletionSettingsCmd sncsCmd;
			SaveNameCompletionSettingsRpcResponseData result;
			
			sncsCmd = ((SaveNameCompletionSettingsCmd) cmd);
			result = GwtServerHelper.saveNameCompletionSettings( this, sncsCmd.getSettings() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_PASSWORD_POLICY_CONFIG:
		{
			SavePasswordPolicyConfigCmd sppcCmd = ((SavePasswordPolicyConfigCmd) cmd);
			Boolean result = GwtServerHelper.savePasswordPolicyConfig( this, sppcCmd.getPasswordPolicyConfig(), sppcCmd.isForcePasswordChange() );
			BooleanRpcResponseData responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_SHARE_EXPIRATION_VALUE:
		{
			SaveShareExpirationValueCmd ssevCmd = ((SaveShareExpirationValueCmd) cmd);
			BooleanRpcResponseData result = GwtShareHelper.saveShareExpirationValue( this, req, ssevCmd.getShareId(), ssevCmd.getExpirationValue() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_SHARE_LISTS:
		{
			SaveShareListsCmd sslCmd = ((SaveShareListsCmd) cmd);
			BooleanRpcResponseData result = GwtShareHelper.saveShareLists( this, req, sslCmd.getShareLists() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_SHARED_FILES_STATE:
		{
			SaveSharedFilesStateCmd ssfsCmd = ((SaveSharedFilesStateCmd) cmd);
			GwtViewHelper.saveUserViewSharedFiles( req, ssfsCmd.getCollectionType(), ssfsCmd.getViewSharedFiles() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( true ));
			return response;
		}
		
		case SAVE_SHARED_VIEW_STATE:
		{
			SaveSharedViewStateCmd ssvsCmd = ((SaveSharedViewStateCmd) cmd);
			Boolean result = GwtViewHelper.saveSharedViewState( this, req, ssvsCmd.getCollectionType(), ssvsCmd.getSharedViewState() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SAVE_SUBSCRIPTION_DATA:
		{
			SaveSubscriptionDataCmd ssdCmd = ((SaveSubscriptionDataCmd) cmd);
			Boolean result = saveSubscriptionData( req, ssdCmd.getEntryId(), ssdCmd.getSubscriptionData() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_COMPLETED:
		{
			SaveTaskCompletedCmd stcCmd = ((SaveTaskCompletedCmd) cmd);
			String result = saveTaskCompleted( req, stcCmd.getTaskIds(), stcCmd.getCompleted() );
			response = new VibeRpcResponse( new StringRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_DUE_DATE:
		{
			SaveTaskDueDateCmd stddCmd = ((SaveTaskDueDateCmd) cmd);
			TaskEventRpcResponseData result = saveTaskDueDate( req, stddCmd.getTaskId(), stddCmd.getDueDate() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_TASK_GRAPH_STATE:
		{
			SaveTaskGraphStateCmd stgsCmd = ((SaveTaskGraphStateCmd) cmd);
			BooleanRpcResponseData responseData = GwtTaskHelper.saveTaskGraphState( req, this, stgsCmd.getFolderId(), stgsCmd.getExpandGraphs() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_TASK_LINKAGE:
		{
			SaveTaskLinkageCmd stlCmd = ((SaveTaskLinkageCmd) cmd);
			Boolean result = saveTaskLinkage( req, stlCmd.getBinderId(), stlCmd.getLinkage() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_PRIORITY:
		{
			SaveTaskPriorityCmd stpCmd = ((SaveTaskPriorityCmd) cmd);
			Boolean result = saveTaskPriority( req, stpCmd.getBinderId(), stpCmd.getEntryId(), stpCmd.getPriority() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_SORT:
		{
			SaveTaskSortCmd stsCmd = ((SaveTaskSortCmd) cmd);
			Boolean result = saveFolderSort( req, stsCmd.getBinderInfo(), stsCmd.getSortKey(), stsCmd.getSortAscending() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case SAVE_TASK_STATUS:
		{
			SaveTaskStatusCmd stsCmd = ((SaveTaskStatusCmd) cmd);
			String result = saveTaskStatus( req, stsCmd.getTaskIds(), stsCmd.getStatus() );
			response = new VibeRpcResponse( new StringRpcResponseData( result ));
			return response;
		}
		
		case SAVE_PERSONAL_PREFERENCES:
		{
			BooleanRpcResponseData responseData;
			Boolean result;
			
			result = savePersonalPreferences( req, ((SavePersonalPrefsCmd) cmd).getPersonalPrefs() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_PRINCIPAL_FILE_SYNC_APP_CONFIG:
		{
			SavePrincipalFileSyncAppConfigCmd spfsacCmd;
			SavePrincipalFileSyncAppConfigRpcResponseData result;
			
			spfsacCmd = ((SavePrincipalFileSyncAppConfigCmd) cmd);
			result = GwtServerHelper.savePrincipalFileSyncAppConfig(
															this,
															spfsacCmd.getConfig(),
															spfsacCmd.getPrincipalIds(),
															spfsacCmd.getPrincipalsAreUsers() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_PRINCIPAL_MOBILE_APPS_CONFIGURATION:
		{
			SavePrincipalMobileAppsConfigCmd spmacCmd;
			SavePrincipalMobileAppsConfigRpcResponseData result;
			
			spmacCmd = ((SavePrincipalMobileAppsConfigCmd) cmd);
			result = GwtServerHelper.savePrincipalMobileAppsConfig(
															this,
															spmacCmd.getConfig(),
															spmacCmd.getPrincipalIds(),
															spmacCmd.getPrincipalsAreUsers());
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_SEARCH:
		{
			SaveSearchCmd ssCmd = ((SaveSearchCmd) cmd);
			SavedSearchInfo result = GwtServerHelper.saveSearch( this, req, ssCmd.getSearchTabId(), ssCmd.getSavedSearchInfo() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SAVE_TAG_SORT_ORDER:
		{
			SaveTagSortOrderCmd stsoCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			stsoCmd = (SaveTagSortOrderCmd) cmd;
			result = saveTagSortOrder( req, stsoCmd.getSortOrder() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_UPDATE_LOGS_CONFIG:
		{
			SaveUpdateLogsConfigCmd sulcCmd = ((SaveUpdateLogsConfigCmd) cmd);
			Boolean result = GwtServerHelper.saveUpdateLogsConfig( this, sulcCmd.getUpdateLogsConfig() );
			BooleanRpcResponseData responseData = new BooleanRpcResponseData( result );
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
		
		case SAVE_USER_LIST_STATUS:
		{
			SaveUserListStatusCmd sulsCmd = ((SaveUserListStatusCmd) cmd);
			Boolean responseData = GwtViewHelper.saveUserListStatus(
				this,
				req,
				sulsCmd.getBinderId(),
				sulsCmd.getShowUserListPanel() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( responseData ) );
			return response;
		}
		
		case SAVE_USER_STATUS:
		{
			SaveUserStatusCmd susCmd;
			SaveUserStatusRpcResponseData responseData;
			
			susCmd = ((SaveUserStatusCmd) cmd);
			responseData = saveUserStatus( req, susCmd.getStatus() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_WEBACCESS_SETTING:
		{
			SaveWebAccessSettingCmd swasCmd = ((SaveWebAccessSettingCmd) cmd);
			Boolean result = GwtServerHelper.saveWebAccessSetting( this, swasCmd.getUserId(), swasCmd.isAllowWebAccess() );
			BooleanRpcResponseData responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_WHATS_NEW_SETTINGS:
		{
			BooleanRpcResponseData responseData;
			Boolean result;
			
			result = saveWhatsNewShowSetting( req, ((SaveWhatsNewSettingsCmd) cmd).getSettings() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case SAVE_ZONE_SHARE_RIGHTS:
		{
			SaveZoneShareRightsCmd szsrCmd;
			Boolean result;
			
			szsrCmd = (SaveZoneShareRightsCmd) cmd;
			result = GwtShareHelper.saveZoneShareRights( this, szsrCmd.getRights() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SEND_FORGOTTEN_PWD_EMAIL:
		{
			SendForgottenPwdEmailCmd sfpeCmd;
			SendForgottenPwdEmailRpcResponseData result;
			
			sfpeCmd = (SendForgottenPwdEmailCmd) cmd;
			result = GwtServerHelper.sendForgottenPwdEmail(
														this,
														req,
														sfpeCmd.getGwtUser(),
														sfpeCmd.getEmailAddress() );
			
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SEND_SHARE_NOTIFICATION_EMAIL:
		{
			SendShareNotificationEmailCmd ssneCmd;
			GwtSendShareNotificationEmailResults results;
			
			ssneCmd = (SendShareNotificationEmailCmd) cmd;
			results = GwtShareHelper.sendShareNotificationEmail( this, ssneCmd.getShareItemIds() );
			
			response = new VibeRpcResponse( results );
			return response;
		}

		case SET_BINDER_SHARING_RIGHTS_INFO:
		{
			SetBinderSharingRightsInfoCmd sbsrCmd = ((SetBinderSharingRightsInfoCmd) cmd);
			ErrorListRpcResponseData result = GwtServerHelper.setBinderSharingRightsInfo(
				this,
				req,
				sbsrCmd.getBinderIds(),
				sbsrCmd.isSetAllUsersRights(),
				sbsrCmd.isSetTeamMemberRights(),
				sbsrCmd.getSharingRights() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SET_DESKTOP_APP_DOWNLOAD_VISIBILITY:
		{
			SetDesktopAppDownloadVisibilityCmd sdadvCmd = ((SetDesktopAppDownloadVisibilityCmd) cmd);
			Boolean result = GwtServerHelper.setDesktopAppDownloadVisibility( this, req, sdadvCmd.isVisible() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SET_ENTRIES_PIN_STATE:
		{
			SetEntriesPinStateCmd sepsCmd = ((SetEntriesPinStateCmd) cmd);
			Boolean result = GwtServerHelper.setEntriesPinState( this, req, sepsCmd.getEntryIds(), sepsCmd.getPinned() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SET_HAS_SEEN_OES_WARNING:
		{
			Boolean result;
			SetHasSeenOesWarningCmd shsowCmd;
			
			shsowCmd = (SetHasSeenOesWarningCmd) cmd;
			result = GwtServerHelper.setHasSeenOesWarning( this, shsowCmd.hasSeen() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SET_MOBILE_DEVICES_WIPE_SCHEDULED_STATE:
		{
			SetMobileDevicesWipeScheduledStateCmd smdwssCmd = ((SetMobileDevicesWipeScheduledStateCmd) cmd);
			BooleanRpcResponseData result = GwtMobileDeviceHelper.saveMobileDevicesWipeScheduledState( this, req, smdwssCmd.getEntityIds(), smdwssCmd.isWipeScheduled() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SET_PRINCIPALS_ADMIN_RIGHTS:
		{
			SetPrincipalsAdminRightsCmd sparCmd = ((SetPrincipalsAdminRightsCmd) cmd);
			SetPrincipalsAdminRightsRpcResponseData result = GwtServerHelper.setPrincipalsAdminRights( this, req, sparCmd.getPrincipalIds(), sparCmd.isSetRights() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SET_SEEN:
		{
			List<Long> entryIds = ((SetSeenCmd) cmd).getEntryIds();
			Boolean result = setSeen( req, entryIds );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SET_UNSEEN:
		{
			List<Long> entryIds = ((SetUnseenCmd) cmd).getEntryIds();
			Boolean result = setUnseen( req, entryIds );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case SET_USER_SHARING_RIGHTS_INFO:
		{
			SetUserSharingRightsInfoCmd susrCmd = ((SetUserSharingRightsInfoCmd) cmd);
			ErrorListRpcResponseData result = GwtServerHelper.setUserSharingRightsInfo( this, req, susrCmd.getUserIds(), susrCmd.getSharingRights() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case SET_USER_VISIBILITY:
		{
			SetUserVisibilityCmd suvCmd = ((SetUserVisibilityCmd) cmd);
			SetLimitedUserVisibilityRpcResponseData result = GwtUserVisibilityHelper.setUserVisibility( this, req, suvCmd.getPrincipalIds(), suvCmd.getLimited(), suvCmd.getOverride() );
			response = new VibeRpcResponse( result );
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
		
		case SHOW_SHARES:
		{
			ShowSharesCmd ssCmd = ((ShowSharesCmd) cmd);
			Boolean result = GwtViewHelper.showShares( this, req, ssCmd.getCollectionType(), ssCmd.getEntityIds() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case START_LDAP_SYNC:
		{
			StartLdapSyncCmd slsCmd;
			StartLdapSyncRpcResponseData responseData;
			
			slsCmd = (StartLdapSyncCmd) cmd;
			responseData = GwtLdapHelper.startLdapSync(
													this,
													req,
													slsCmd.getSyncId(),
													slsCmd.getSyncAll(),
													slsCmd.getListOfLdapConfigsToSyncGuid(),
													null,
													slsCmd.getSyncMode() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case STOP_SYNC_NET_FOLDERS:
		{
			StopSyncNetFoldersCmd ssnfCmd;
			Set<NetFolder> listOfNetFolders;
			
			ssnfCmd = (StopSyncNetFoldersCmd) cmd;
			listOfNetFolders = GwtNetFolderHelper.stopSyncNetFolders( this, req, ssnfCmd.getListOfNetFoldersToStopSync() );
			response = new VibeRpcResponse( new StopSyncNetFoldersRpcResponseData( listOfNetFolders ) );
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
		
		case SYNC_NET_FOLDER_SERVER:
		{
			SyncNetFolderServerCmd snfsCmd;
			SyncNetFolderRootsRpcResponseData responseData;
			Set<NetFolderRoot> listOfNetFolderServers;
			
			snfsCmd = (SyncNetFolderServerCmd) cmd;
			listOfNetFolderServers = GwtNetFolderHelper.syncNetFolderServers( this, req, snfsCmd.getNetFolderServers() );
			responseData = new SyncNetFolderRootsRpcResponseData( listOfNetFolderServers );
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
		
		case TEST_KEYSHIELD_CONNECTION:
		{
			TestKeyShieldConnectionCmd tkcCmd = ((TestKeyShieldConnectionCmd) cmd);
			TestKeyShieldConnectionResponse responseData = GwtKeyShieldSSOHelper.testKeyShieldConnection(this, tkcCmd.getKeyShieldConfig());
			response = new VibeRpcResponse(responseData);
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
			result = trackBinder( req, tbCmd.getBinderId() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TRASH_PURGE_ALL:
		{
			TrashPurgeAllCmd tpaCmd = ((TrashPurgeAllCmd) cmd);
			StringRpcResponseData responseData = GwtDeleteHelper.trashPurgeAll( 
				this,
				req,
				tpaCmd.getBinderId(),
				tpaCmd.getPurgeMirroredSources() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TRASH_PURGE_SELECTED_ENTITIES:
		{
			TrashPurgeSelectedEntitiesCmd tpseCmd = ((TrashPurgeSelectedEntitiesCmd) cmd);
			StringRpcResponseData responseData = GwtDeleteHelper.trashPurgeSelectedEntities(
				this,
				req,
				tpseCmd.getBinderId(),
				tpseCmd.getPurgeMirroredSources(),
				tpseCmd.getTrashSelectionData() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TRASH_RESTORE_ALL:
		{
			TrashRestoreAllCmd traCmd = ((TrashRestoreAllCmd) cmd);
			StringRpcResponseData responseData = GwtDeleteHelper.trashRestoreAll(
				this,
				req,
				traCmd.getBinderId() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case TRASH_RESTORE_SELECTED_ENTITIES:
		{
			TrashRestoreSelectedEntitiesCmd trseCmd = ((TrashRestoreSelectedEntitiesCmd) cmd);
			StringRpcResponseData responseData = GwtDeleteHelper.trashRestoreSelectedEntities(
				this,
				req,
				trseCmd.getBinderId(),
				trseCmd.getTrashSelectionData() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UNLOCK_ENTRIES:
		{
			UnlockEntriesCmd uleCmd = ((UnlockEntriesCmd) cmd);
			ErrorListRpcResponseData responseData = GwtViewHelper.unlockEntries( this, req, uleCmd.getEntityIds() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UPDATE_CALCULATED_DATES:
		{
			UpdateCalculatedDatesCmd ucdCmd = ((UpdateCalculatedDatesCmd) cmd);
			Map<Long, TaskDate> results = updateCalculatedDates( req, ucdCmd.getBinderId(), ucdCmd.getEntryId() );
			UpdateCalculatedDatesRpcResponseData responseData = new UpdateCalculatedDatesRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UPDATE_CALENDAR_EVENT:
		{
			UpdateCalendarEventCmd uceCmd = ((UpdateCalendarEventCmd) cmd);
			Boolean result = GwtCalendarHelper.updateCalendarEvent( this, req, uceCmd.getBrowserTZOffset(), uceCmd.getFolderId(), uceCmd.getEvent() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ));
			return response;
		}
		
		case UPDATE_BINDER_TAGS:
		{
			UpdateBinderTagsCmd ubtCmd;
			Boolean result;
			BooleanRpcResponseData responseData;
			
			ubtCmd = (UpdateBinderTagsCmd) cmd;
			result = updateBinderTags( req, ubtCmd.getBinderId(), ubtCmd.getToBeDeleted(), ubtCmd.getToBeAdded() );
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
			result = updateEntryTags( req, uetCmd.getEntryId(), uetCmd.getToBeDeleted(), uetCmd.getToBeAdded() );
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
			result = updateFavorites( req, ufCmd.getFavoritesList() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UPLOAD_FILE_BLOB:
		{
			UploadFileBlobCmd ufbCmd = ((UploadFileBlobCmd) cmd);
			StringRpcResponseData result = GwtHtml5Helper.uploadFileBlob( this, req, ufbCmd.getFolderInfo(), ufbCmd.getFileBlob(), ufbCmd.isLastBlob() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case UNPIN_ENTRY:
		{
			UnpinEntryCmd upeCmd = ((UnpinEntryCmd) cmd);
			Boolean result = GwtServerHelper.unpinEntry( this, req, upeCmd.getFolderId(), upeCmd.getEntryId() );
			response = new VibeRpcResponse( new BooleanRpcResponseData( result ) );
			return response;
		}
		
		case UNTRACK_BINDER:
		{			
			UntrackBinderCmd ubCmd = (UntrackBinderCmd) cmd;
			Boolean result = untrackBinder( req, ubCmd.getBinderId() );
			BooleanRpcResponseData responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case UNTRACK_PERSON:
		{
			UntrackPersonCmd upCmd = ((UntrackPersonCmd) cmd);
			Boolean result = untrackPerson( req, upCmd.getBinderId(), upCmd.getUserId() );
			BooleanRpcResponseData responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case VALIDATE_CAPTCHA:
		{
			ValidateCaptchaCmd vcCmd;
			BooleanRpcResponseData responseData;
			Boolean result;
			
			vcCmd = (ValidateCaptchaCmd) cmd;
			result = GwtServerHelper.validateCaptcha( this, req, vcCmd.getText() );
			responseData = new BooleanRpcResponseData( result );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case VALIDATE_EMAIL_ADDRESS:
		{
			ValidateEmailAddressCmd vemCmd;
			ValidateEmailRpcResponseData result;
			
			vemCmd = (ValidateEmailAddressCmd) cmd;
			result = GwtServerHelper.validateEmailAddress( this, vemCmd.getEmailAddress(), vemCmd.isExternalEmailAddress(), vemCmd.getAddressField() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case VALIDATE_ENTRY_EVENTS:
		{
			ValidateEntryEventsCmd veaCmd  = ((ValidateEntryEventsCmd) cmd);
			String entryId = veaCmd.getEntryId();
			List<EventValidation> eventValidations = veaCmd.getEventsToBeValidated();
			List<EventValidation> results = validateEntryEvents( req, eventValidations, entryId );
			EventValidationListRpcResponseData responseData = new EventValidationListRpcResponseData( results );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		case VALIDATE_SHARE_LISTS:
		{
			ValidateShareListsCmd vslCmd = ((ValidateShareListsCmd) cmd);
			ValidateShareListsRpcResponseData result = GwtShareHelper.validateShareLists( this, req, vslCmd.getShareLists() );
			response = new VibeRpcResponse( result );
			return response;
		}
		
		case VALIDATE_UPLOADS:
		{
			ValidateUploadsCmd veaCmd  = ((ValidateUploadsCmd) cmd);
			ValidateUploadsRpcResponseData responseData = GwtHtml5Helper.validateUploads( this, req, veaCmd.getFolderInfo(), veaCmd.getUploads() );
			response = new VibeRpcResponse( responseData );
			return response;
		}
		
		}
		
		String details = ("Unknown command: " + cmdEnum.name() + " (" +cmd.getClass().getName() + ")");
		GwtLogHelper.warn( logger, "In GwtRpcServiceImpl.executeCommandImpl():  " + details );
		throw new GwtTeamingException(ExceptionType.NO_RPC_HANDLER, details);
	}
	
	
	/*
	 * Marks the given task in the given binder as having its subtask
	 * display collapsed.
	 */
	private Boolean collapseSubtasks( HttpServletRequest req, Long binderId, Long entryId ) throws GwtTeamingException {
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
	private ErrorListRpcResponseData deleteTasks(HttpServletRequest req, List<EntityId> taskIds ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.deleteTasks()");
		try {
			return GwtTaskHelper.deleteTasks(req, this, taskIds);
		}
		
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.deleteTasks()");
		}
	}

	/*
	 * Marks the given task in the given binder as having its subtask
	 * display expanded.
	 */
	private Boolean expandSubtasks( HttpServletRequest req, Long binderId, Long entryId ) throws GwtTeamingException {
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
	private List<AssignmentInfo> getGroupAssigneeMembership( HttpServletRequest req, Long groupId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.getGroupMembership()");
		try {
			return GwtTaskHelper.getGroupMembership( this, groupId );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getGroupMembership()");
		}
	}

	/*
	 * Return the rights the user has for modifying tags on the given
	 * binder.
	 */
	private ArrayList<Boolean> getTagRightsForBinder( HttpServletRequest req, String binderId )
	{
		ArrayList<Boolean> tagRights;
		
		tagRights = new ArrayList<Boolean>();
		
		tagRights.add( 0, canManagePersonalBinderTags( req, binderId ) );
		tagRights.add( 1, canManagePublicBinderTags( req, binderId ) );
		
		return tagRights;
	}
	
	/*
	 * Return the rights the user has for modifying personal tags on
	 * the given entry.
	 */
	private ArrayList<Boolean> getTagRightsForEntry( HttpServletRequest req, String entryId )
	{
		ArrayList<Boolean> tagRights;
		
		tagRights = new ArrayList<Boolean>();
		
		tagRights.add( 0, canManagePersonalEntryTags( req, entryId ) );
		tagRights.add( 1, canManagePublicEntryTags( req, entryId ) );
		
		return tagRights;
	}
	
    /*
     */
    private TagSortOrder getTagSortOrder( HttpServletRequest req )
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
	private List<AssignmentInfo> getTeamAssigneeMembership( HttpServletRequest req, Long binderId ) throws GwtTeamingException {
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
	private ErrorListRpcResponseData purgeTasks(HttpServletRequest req, List<EntityId> taskIds) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.purgeTasks()");
		try {
			return GwtTaskHelper.purgeTasks(req, this, taskIds);
		}
		
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.purgeTasks()");
		}
	}
	
	/*
	 * Returns a ActivityStreamData of corresponding to activity stream
	 * parameters, paging data and info provided.
	 */
	private ActivityStreamData getActivityStreamData(HttpServletRequest req, ActivityStreamParams asp, ActivityStreamInfo asi, PagingData pagingData, ActivityStreamDataType asdt, SpecificFolderData sfData) {
		return GwtActivityStreamHelper.getActivityStreamData(req, this, asp, asi, pagingData, asdt, sfData);
	}
	
	/*
	 * Returns an ActivityStreamParams object containing information
	 * the current activity stream setup.
	 */
	private ActivityStreamParams getActivityStreamParams(HttpServletRequest req) {
		return GwtActivityStreamHelper.getActivityStreamParams(this);
	}
	
	/*
	 * Return a list of contributor IDs for the given workspace.
	 * The list of contributors will include
	 * 1. The creator of the workspace
	 * 2. The owner of the workspace
	 * 3. The last user to modify the workspace.
	 */
	private ArrayList<Long> getWorkspaceContributorIds( HttpServletRequest req, Long workspaceId )
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
	
	/*
	 * Returns true if the data for an activity stream has changed (or
	 * has never been cached) and false otherwise.
	 */
	private Boolean hasActivityStreamChanged(HttpServletRequest req, ActivityStreamInfo asi) {
		return GwtActivityStreamHelper.hasActivityStreamChanged(req, this, asi);
	}
	
	/*
	 * Stores an ActivityStreamIn as the current user's default
	 * activity stream in their user profile.
	 */
	private Boolean persistActivityStreamSelection(HttpServletRequest req, ActivityStreamInfo asi) {
		return GwtActivityStreamHelper.persistActivityStreamSelection(req, this, asi);
	}
	
	/*
	 * Return the 'document base URL' that is used in tinyMCE
	 * configuration.
	 */
	private String getDocumentBaseUrl(HttpServletRequest req, String binderId) throws GwtTeamingException {
		String baseUrl = null;
		try {
			BinderModule binderModule = getBinderModule();
			Long binderIdL = new Long(binderId);
			
			if (binderIdL != null) {
				
				Binder binder = binderModule.getBinder(binderIdL);
				String webPath = WebUrlUtil.getServletRootURL(req);
				baseUrl = WebUrlUtil.getFileUrl(webPath, WebKeys.ACTION_READ_FILE, binder, "");
			}
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
		
		return baseUrl;
	}
	
	
	/*
	 * Return an Entry object for the given zone and entry ID.
	 */
	@SuppressWarnings({ "unchecked" })
	private GwtFolderEntry getEntry(HttpServletRequest req, String zoneUUID, String entryId, int numRepliesToGet, boolean getFileAttachments) throws GwtTeamingException {
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
				
				// Get the entry's type
				{
					GwtFolderEntryType entryType;
					
					entryType = GwtFolderEntryTypeHelper.getFolderEntryType( this, entryIdL );
					folderEntry.setEntryType( entryType );
				}
				
				// Get the entry's description
				{
					Description desc;
					
					desc = entry.getDescription();
					if ( desc != null )
					{
						String descStr;
						
						descStr = desc.getText();
						
						// Perform any fixups needed on the entry's description
						descStr = markupStringReplacement( req, entry, descStr, "view" );
						
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
				url = GwtServerHelper.getViewFolderEntryUrl( this, req, parentBinderId, entryIdL );
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
					    	
					    	fileUrl = WebUrlUtil.getFileUrl( req, WebKeys.ACTION_READ_FILE, fatt, false, true );
					    	gwtAttachment.setViewFileUrl( fileUrl );
					    	
					    	folderEntry.addAttachment( gwtAttachment );
			    		}
			    	}
				}
				
				// Is this entry a file?
				if ( GwtServerHelper.isFamilyFile( Definition.FAMILY_FILE ) )
				{
					SortedSet<FileAttachment> fileAttachments;
					
					// Yes
					fileAttachments = entry.getFileAttachments();
					if ( fileAttachments != null && fileAttachments.size() > 0 )
					{
						String fileName = null;
						
						fileName = fileAttachments.first().getFileItem().getName();
						if ( fileName != null && fileName.length() > 0 )
						{
							String fileImgUrl;
							
							fileImgUrl = FileIconsHelper.getFileIconFromFileName(
																			fileName,
																			IconSize.LARGE );
							folderEntry.setFileImgUrl( fileImgUrl );
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			throw GwtLogHelper.getGwtClientException( e );
		}
		
		
		return folderEntry;
	}
	
	
	/*
	 * Return a view file URL that can be used to view an entry's file
	 * as HTML.
	 */
	private String getViewFileUrl(HttpServletRequest req, ViewFileInfo vfi) throws GwtTeamingException {
		return GwtServerHelper.getViewFileUrl(req, vfi);
	}
	
	/*
	 * Return a list of comments for the given entry.
	 */
	private List<ActivityStreamEntry> getEntryComments( HttpServletRequest req, String entryId )
	{
		return GwtActivityStreamHelper.getEntryComments( this, req, Long.parseLong( entryId ) );
	}
	
	
	/*
	 * Return a list of tags associated with the given entry.
	 */
	private ArrayList<TagInfo> getEntryTags( HttpServletRequest req, String entryId )
	{
		return GwtServerHelper.getEntryTags( this, entryId );
	}
	
	
	/*
	 * Return a list of the names of the files that are attachments for
	 * the given binder.
	 */
	private ArrayList<String> getFileAttachments( HttpServletRequest req, String binderId ) throws GwtTeamingException
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
			}
		}
		catch (Exception e)
		{
			throw GwtLogHelper.getGwtClientException( e );
		}

        return fileNames;
	}
	
	/*
	 * Return a Folder object for the given folder id.
	 */
	private GwtFolder getFolder( HttpServletRequest req, String zoneUUID, String folderId, boolean extendedTitle ) throws GwtTeamingException
	{
		return GwtServerHelper.getFolderImpl( this, req, zoneUUID, folderId, null, extendedTitle );
	}
	
	/*
	 * Return a list of the first n entries in the given folder.
	 */
	@SuppressWarnings({ "unchecked" })
	private ArrayList<GwtFolderEntry> getFolderEntries( HttpServletRequest req, String zoneUUID, String folderId, int numEntriesToRead, int numReplies, boolean getFileAttachments ) throws GwtTeamingException
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
					folderEntry = getEntry( req, null, entryId, numReplies, getFileAttachments );
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
			throw GwtLogHelper.getGwtClientException( e );
		}
		
		return entries;
	}
	
	/*
	 * Return the sort setting for the given folder.
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
	
	/*
	 * Return the "binder permalink" URL.
	 */
	private String getBinderPermalink( HttpServletRequest req, String binderId )
	{
		String reply = "";
		
		if ( binderId != null && binderId.length() > 0 )
		{
			Binder binder = GwtUIHelper.getBinderSafely( getBinderModule(), binderId );
			if (null != binder)
			{
				reply = PermaLinkUtil.getPermalink( req, binder );
			}
		}
		
		return reply;
	}
	
	/*
	 * Return the "modify binder" URL.
	 */
	private String getModifyBinderUrl( HttpServletRequest req, String binderId )
	{
		AdaptedPortletURL adapterUrl;
		Binder binder;
		Long binderIdL;

		// Create a URL that can be used to modify a binder.
		adapterUrl = new AdaptedPortletURL( req, "ss_forum", true );
		adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_MODIFY_BINDER );
		adapterUrl.setParameter( WebKeys.URL_OPERATION, WebKeys.OPERATION_MODIFY );
		adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
		
		binderIdL = new Long( binderId );
		binder = getBinderModule().getBinder( binderIdL );
		adapterUrl.setParameter( WebKeys.URL_BINDER_TYPE, binder.getEntityType().name() );
		
		return adapterUrl.toString();
	}
	
	/*
	 * Returns the "binder permalink" URL of a binder's parent.
	 */
	private String getParentBinderPermalink( HttpServletRequest req, Long binderId, boolean showCollectionOnUserWS )
	{
		// Can we access this binder?
		String			reply  = null;
		BinderModule	bm     = getBinderModule();
		Binder			binder = GwtUIHelper.getBinderSafely( bm, binderId );
		if ( null != binder )
		{
			// Yes!  Scan its parentage.
			binder = binder.getParentBinder();
			while ( null != binder )
			{
				// Can we read the contents of this binder?
				if ( bm.testAccess( binder, BinderOperation.readEntries ) ) {
					// Yes!  Break out of the loop.  We'll use it as
					// the parent.
					break;
				}
				
				// No, we can't read that one!  Step up a level of
				// parentage and keep checking.
				binder = binder.getParentBinder();
			}

			// Did we find a binder to use as a parent?
			if ( null == binder )
			{
				// No!  Default to the user's personal workspace.
				binder = GwtServerHelper.getUserWorkspace( GwtServerHelper.getCurrentUser() );
			}
			
			// Did we find a binder to use as a parent?
			if ( null != binder )
			{
				// Yes!  Return a permalink to it.
				reply = getBinderPermalink( req, String.valueOf( binder.getId() ) );
				if ( showCollectionOnUserWS && BinderHelper.isBinderCurrentUsersWS( binder ) )
				{
					CollectionType ct;
					if ( SearchUtils.userCanAccessMyFiles( this, RequestContextHolder.getRequestContext().getUser() ) )
					{
						ct = CollectionType.MY_FILES;
					}
					else if ( GwtServerHelper.getCurrentUser().isShared() )
					{
						ct = CollectionType.SHARED_PUBLIC;
					}
					else
					{
						ct = CollectionType.SHARED_WITH_ME;
					}
					reply = GwtUIHelper.appendUrlParam( reply, WebKeys.URL_SHOW_COLLECTION, String.valueOf( ct.ordinal() ) );
				}
			}
		}

		// If we get here, reply is null or refers to the permalink of
		// the nearest containing parent we have access to from the
		// binder we were give.  Return it.
		return reply;
	}
	
	/*
	 * Returns the HttpServletRequest from an HttpRequestInfo object.
	 */
	private static HttpServletRequest getRequest(HttpRequestInfo ri) {
		return ((HttpServletRequest) ri.getRequestObj());
	}
	
	/*
	 * Return the HttpServletResponse from an HttpRequestInfo object.
	 */
	private static HttpServletResponse getResponse( HttpRequestInfo ri )
	{
		return (HttpServletResponse) ri.getResponseObj();
	}
	
	/*
	 * Return the ServletContext from an HttpRequestInfo object.
	 */
	private static ServletContext getServletContext( HttpRequestInfo ri )
	{
		return (ServletContext) ri.getServletContext();
	}
	
	/*
	 * Return the url needed to invoke the "Send to friend" page.
	 */
	private String getSendToFriendUrl( HttpServletRequest req, String entryId ) throws GwtTeamingException
	{
		AdaptedPortletURL url;
		
		try
		{
			FolderEntry entry;
			Binder parentBinder;
			Long entryIdL;
			Long parentBinderId;

			url = new AdaptedPortletURL( req, "ss_forum", true );
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
			throw GwtLogHelper.getGwtClientException( ex );
		}		


		return url.toString();
	}

	/*
	 * Return the URL needed to invoke the "site administration" page.
	 * If the user does not have rights to run the "site
	 * administration" page we will throw an exception.
	 */
	private String getSiteAdministrationUrl( HttpServletRequest req, String binderId ) throws GwtTeamingException
	{
		// Does the user have rights to run the "site administration" page?
		if ( getAdminModule().testAccess( AdminOperation.manageFunction ) )
		{
			AdaptedPortletURL adapterUrl;
			
			// Yes
			adapterUrl = new AdaptedPortletURL( req, "ss_forum", true );
			adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_SITE_ADMINISTRATION );
			adapterUrl.setParameter( WebKeys.URL_BINDER_ID, binderId );
			
			return adapterUrl.toString();
		}
		
		GwtTeamingException ex;
		
		ex = GwtLogHelper.getGwtClientException();
		ex.setExceptionType( ExceptionType.ACCESS_CONTROL_EXCEPTION );
		throw ex;
	}
	
	/*
	 * Return a GwtBrandingData object for the home workspace.
	 */
	private GwtBrandingData getSiteBrandingData(final HttpServletRequest req, final ServletContext ctx) throws GwtTeamingException
	{
		GwtBrandingData brandingData;
		final AbstractAllModulesInjected allModules;
		RunasCallback callback;
		
		allModules = this;
		
		// We need to read the site branding as admin.
		callback = new RunasCallback()
		{
			@Override
			public Object doAs()
			{
				Binder topWorkspace;
				GwtBrandingData siteBrandingData;

				try
				{
					String binderId;
					
					// Get the top workspace.
					topWorkspace = getWorkspaceModule().getTopWorkspace();				
				
					// Get the branding data from the top workspace.
					binderId = topWorkspace.getId().toString();
					siteBrandingData = GwtServerHelper.getBinderBrandingData(
																			allModules,
																			binderId,
																			false,
																			req,
																			ctx);
				}
				catch (Exception e)
				{
					siteBrandingData = new GwtBrandingData();
				}

				return siteBrandingData;
			}
		};
		brandingData = (GwtBrandingData) RunasTemplate.runasAdmin(
																callback,
																WebHelper.getRequiredZoneName( req ) ); 
		
		brandingData.setIsSiteBranding( true );

		return brandingData;
	}

	/*
	 * Reads the task information from the specified binder.
	 */
	private List<TaskListItem> getTaskList( HttpServletRequest req, boolean applyUsersFilter, String zoneUUID, Long binderId, String filterType, String modeType ) throws GwtTeamingException
	{
		SimpleProfiler.start("GwtRpcServiceImpl.getTaskList()");
		try {
			return GwtTaskHelper.getTaskList( req, this, applyUsersFilter, false, GwtTaskHelper.getTaskBinder( this, zoneUUID, binderId ), filterType, modeType );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getTaskList()");
		}
	}

	/*
	 * Returns a TaskBundle object for the specified binder.
	 */
	private TaskBundle getTaskBundle( HttpServletRequest req, boolean embeddedInJSP, Long binderId, String filterType, String modeType ) throws GwtTeamingException
	{
		SimpleProfiler.start("GwtRpcServiceImpl.getTaskBundle()");
		try {
			return
				GwtTaskHelper.getTaskBundle(
					req,
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
	}

	/*
	 * Returns a TaskLinkage object for the specified binder.
	 */
	private TaskLinkage getTaskLinkage( HttpServletRequest req, Long binderId ) throws GwtTeamingException
	{
		SimpleProfiler.start("GwtRpcServiceImpl.getTaskLinkage()");
		try {
			return GwtTaskHelper.getTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, null, binderId ) );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.getTaskLinkage()");
		}
	}

	/*
	 * Removes the TaskLinkage object from the specified binder.
	 */
	private Boolean removeTaskLinkage( HttpServletRequest req, Long binderId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.removeTaskLinkage()");
		try {
			return GwtTaskHelper.removeTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, null, binderId ) );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.removeTaskLinkage()");
		}
	}

	/*
	 * Save the given tag sort order to the user's properties
	 */
	private Boolean saveTagSortOrder( HttpServletRequest req, TagSortOrder sortOrder )
	{
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_TAG_SORT_ORDER, sortOrder );
		
		return Boolean.TRUE;
	}
	
	/*
	 * Stores a completed value on the specified tasks.
	 */
	private String saveTaskCompleted( HttpServletRequest req, List<EntityId> taskIds, String completed ) throws GwtTeamingException {
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
	private TaskEventRpcResponseData saveTaskDueDate( HttpServletRequest req, EntityId taskId, TaskEvent taskEvent ) throws GwtTeamingException {
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
	private Boolean saveTaskLinkage( HttpServletRequest req, Long binderId, TaskLinkage taskLinkage ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.saveTaskLinkage()");
		try {
			return GwtTaskHelper.saveTaskLinkage( this, GwtTaskHelper.getTaskBinder( this, null, binderId ), taskLinkage );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.saveTaskLinkage()");
		}
	}

	/*
	 * Stores a priority value on the specified task.
	 */
	private Boolean saveTaskPriority( HttpServletRequest req, Long binderId, Long entryId, String priority ) throws GwtTeamingException {
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
	private Boolean saveFolderColumns( HttpServletRequest req, String binderId, List<FolderColumn> fcList, 
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
	private Boolean saveFolderSort( HttpServletRequest req, BinderInfo binderInfo, String sortKey, boolean sortAscending ) throws GwtTeamingException {
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
	private String saveTaskStatus( HttpServletRequest req, List<EntityId> taskIds, String status ) throws GwtTeamingException {
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
	private Map<Long, TaskDate> updateCalculatedDates( HttpServletRequest req, Long binderId, Long entryId ) throws GwtTeamingException {
		SimpleProfiler.start("GwtRpcServiceImpl.updateCalculatedDates()");
		try {
			return GwtTaskHelper.updateCalculatedDates( req, this, GwtTaskHelper.getTaskBinder( this, null, binderId ), entryId );
		}
		finally {
			SimpleProfiler.stop("GwtRpcServiceImpl.updateCalculatedDates()");
		}
	}

	/*
	 * Return a GwtUpgradeInfo object.
	 */
	private GwtUpgradeInfo getUpgradeInfo( HttpServletRequest req ) throws GwtTeamingException
	{
		GwtUpgradeInfo upgradeInfo;
		User user;
		boolean isAdminUser;
		
		user = GwtServerHelper.getCurrentUser();
		isAdminUser = ObjectKeys.SUPER_USER_INTERNALID.equals( user.getInternalId() );
		
		upgradeInfo = new GwtUpgradeInfo();
		
		// Get the Teaming version and build information
		{
			String releaseStr;
			
			releaseStr = ReleaseInfo.getLocalizedReleaseInfo( GwtServerHelper.getCurrentUser().getLocale() );
			upgradeInfo.setReleaseInfo( releaseStr );
			upgradeInfo.setBuild( ReleaseInfo.getBuildAsStr( GwtServerHelper.getCurrentUser().getLocale() ) );
			upgradeInfo.setNameAndVersion( ReleaseInfo.getNameAndVersion() );
			
			if ( Utils.checkIfFilr() )
			{
				releaseStr = ReleaseInfo.getFilrApplianceReleaseInfo();
				upgradeInfo.setFilrApplianceReleaseInfo( releaseStr );
			}
		}
		
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
 	 		if ( isAdminUser )
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
 		 				 "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_TEMPLATES ) ) &&
 		 				 "true".equals( adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_IMPORT_TYPELESS_DN ) ) )
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
		 			if ( Utils.checkIfFilr() == false &&
		 				 (property == null || property.length() == 0 || "false".equalsIgnoreCase( property ) ) )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_DEFINITIONS );
		 			}

		 			// Do the templates need to be reset?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_TEMPLATES );
		 			if ( Utils.checkIfFilr() == false &&
		 				 (property == null || property.length() == 0 || "false".equalsIgnoreCase( property ) ) )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_TEMPLATES );
		 			}

		 			// Does re-indexing need to be performed?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_SEARCH_INDEX );
		 			if ( property == null || property.length() == 0 || "false".equalsIgnoreCase( property ) )
		 			{
		 				// Yes
		 				upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_SEARCH_INDEX );
		 			}

		 			// Does an ldap sync need to be run to import typeless DNs?
		 			property = (String) adminUserProperties.getProperty( ObjectKeys.USER_PROPERTY_UPGRADE_IMPORT_TYPELESS_DN );
		 			if ( property == null || property.length() == 0 || "false".equalsIgnoreCase( property ) )
		 			{
		 				// Maybe!  There's no need to do so if there
		 				// aren't any LDAP connections defined.  Are
		 				// there any?
			    		List<LdapConnectionConfig> ldapConnections = getAuthenticationModule().getLdapConnectionConfigs();
			    		int ldapConnectionCount = ((null == ldapConnections) ? 0 : ldapConnections.size());
						if (0 < ldapConnectionCount) {
							// Yes!  Then the sync needs to be run.
							upgradeInfo.addUpgradeTask( GwtUpgradeInfo.UpgradeTask.UPGRADE_IMPORT_TYPELESS_DN );
						}
						else {
							// No, there aren't any LDAP connections!
							// Mark the user so we never check this
							// again.
							getProfileModule().setUserProperty( user.getId(), ObjectKeys.USER_PROPERTY_UPGRADE_IMPORT_TYPELESS_DN, String.valueOf( Boolean.TRUE ) );
						}
		 			}
		 		}
	 		}
		}
		
		// Get any Filr admin tasks that need to be done
		if ( Utils.checkIfFilr() && isAdminUser )
		{
			UserProperties userProperties;
			String xmlStr;
			FilrAdminTasks filrAdminTasks;
			ArrayList<EnterProxyCredentialsTask> listOfTasks;
			ArrayList<SelectNetFolderServerTypeTask> listOfTasks2;
			boolean saveNeeded = false;
			List<ResourceDriverConfig> drivers;

			// Get the FilrAdminTasks from the administrator's user properties
			userProperties = getProfileModule().getUserProperties( user.getId() );
			xmlStr = (String)userProperties.getProperty( ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS );
			filrAdminTasks = new FilrAdminTasks( xmlStr );

			// Get a list of the currently defined Net Folder Roots
			drivers = getResourceDriverModule().getAllNetFolderResourceDriverConfigs();
			
			// Go through the list of net folder servers and see if there are any who are still using
			// famt as their net folder server type.
			if ( drivers != null )
			{
				for ( ResourceDriverConfig driver : drivers )
				{
					DriverType driverType;
					
					driverType = driver.getDriverType();
					if ( driverType == null || driverType == DriverType.famt )
					{
						filrAdminTasks.addSelectNetFolderServerTypeTask( driver.getId() );
					}
					else
					{
						String name;
						String pwd;

						name = driver.getAccountName();
						pwd = driver.getPassword();
						if ( name == null || name.length() == 0 || pwd == null || pwd.length() == 0 )
						{
							filrAdminTasks.addEnterNetFolderServerProxyCredentialsTask( driver.getId() );
						}
					}
				}
			}
			
			// Get the list of "enter proxy credentials" tasks
			listOfTasks = filrAdminTasks.getAllEnterProxyCredentialsTasks();
			if ( listOfTasks != null )
			{
				for ( EnterProxyCredentialsTask nextTask : listOfTasks )
				{
					GwtEnterProxyCredentialsTask gwtTask;
					ResourceDriverConfig rdConfig;
					boolean removeTask;
					
					removeTask = false;
					
					// Get the net folder server object with the given id
					rdConfig = NetFolderHelper.findNetFolderRootById( drivers, nextTask.getNetFolderServerId() ); 
					if ( rdConfig != null )
					{
						String name;
						String pwd;

						name = rdConfig.getAccountName();
						pwd = rdConfig.getPassword();
						if ( name == null || name.length() == 0 || pwd == null || pwd.length() == 0 )
						{
							String serverName;

							serverName = rdConfig.getName();
						
							gwtTask = new GwtEnterProxyCredentialsTask();
							gwtTask.setServerId( nextTask.getNetFolderServerId() );
							gwtTask.setServerName( serverName );
							
							upgradeInfo.addFilrAdminTask( gwtTask );
						}
						else
							removeTask = true;
					}
					else
						removeTask = true;
					
					if ( removeTask == true )
					{
						Long netFolderServerId;
						
						// Remove the task for the administrator to enter the proxy credentials for this net folder server.
						netFolderServerId = new Long( nextTask.getNetFolderServerId() );
						filrAdminTasks.deleteEnterNetFolderServerProxyCredentialsTask( netFolderServerId );
						
						saveNeeded = true;
					}
				}
			}

			// Get the list of "Select net folder server type" tasks
			listOfTasks2 = filrAdminTasks.getAllSelectNetFolderServerTypeTasks();
			if ( listOfTasks2 != null )
			{
				for ( SelectNetFolderServerTypeTask nextTask : listOfTasks2 )
				{
					GwtSelectNetFolderServerTypeTask gwtTask;
					ResourceDriverConfig rdConfig;
					boolean removeTask;
					
					removeTask = false;
					
					// Get the net folder server object with the given id
					rdConfig = NetFolderHelper.findNetFolderRootById( drivers, nextTask.getNetFolderServerId() ); 
					if ( rdConfig != null )
					{
						DriverType driverType;
						
						driverType = rdConfig.getDriverType();
						if ( driverType == null || driverType == DriverType.famt )
						{
							String serverName;
	
							serverName = rdConfig.getName();
						
							gwtTask = new GwtSelectNetFolderServerTypeTask();
							gwtTask.setServerId( nextTask.getNetFolderServerId() );
							gwtTask.setServerName( serverName );
							
							upgradeInfo.addFilrAdminTask( gwtTask );
						}
						else
							removeTask = true;
					}
					else
						removeTask = true;
					
					if ( removeTask == true )
					{
						Long netFolderServerId;
						
						// Remove the task for the administrator to enter the proxy credentials for this net folder server.
						netFolderServerId = new Long( nextTask.getNetFolderServerId() );
						filrAdminTasks.deleteEnterNetFolderServerProxyCredentialsTask( netFolderServerId );
						
						saveNeeded = true;
					}
				}
			}

			// Save the FilrAdminTasks to the administrator's user properties
			if ( saveNeeded )
			{
				RunasCallback callback;
				final String tmpXmlStr;
				final Long adminId;
				
				tmpXmlStr = filrAdminTasks.toString();
				adminId = user.getId();
				
				callback = new RunasCallback()
				{
					@Override
					public Object doAs()
					{
						getProfileModule().setUserProperty(
													adminId,
													ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS,
													tmpXmlStr );
						return null;
					}
				};
				RunasTemplate.runasAdmin( callback, RequestContextHolder.getRequestContext().getZoneName() );
			}
		}
		
		// Has the license expired?
		if ( LicenseChecker.isLicenseExpired() )
		{
			// Yes, add a "license expired" task.
			upgradeInfo.addLicenseExpiredTask();
		}
		
		return upgradeInfo;
	}
	
	
	/*
	 * ? 
	 */
    private ExtensionInfoClient[] getExtensionInfo( HttpServletRequest req )
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

    /*
     * ? 
     */
    private ExtensionInfoClient[] removeExtension(HttpServletRequest req, String id) throws ExtensionDefinitionInUseException
    {
    	AdminModule adminModule;
    	adminModule = getAdminModule();
    	
    	if( adminModule.getExtensionManager().checkDefinitionsInUse(id) )
    	{
    		throw new ExtensionDefinitionInUseException(NLT.get("definition.errror.inUse"));
    	}
    	adminModule.getExtensionManager().removeExtensions(id);

    	return getExtensionInfo(req);
    }


    /*
     * ?
     */
	private ExtensionFiles getExtensionFiles(HttpServletRequest req, String id, String zoneName) {
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


	/*
	 * Returns a TreeInfo containing the display information for the
	 * Binder hierarchy referred to by a BucketInfo
	 * 
	 * The information returned is typically used for driving a
	 * horizontal WorkspaceTreeControl widget.
	 */
	private TreeInfo expandHorizontalBucket( HttpServletRequest req, BucketInfo bucketInfo )
	{
		// Expand the bucket list without regard to persistent Binder
		// expansions.
		return GwtServerHelper.expandBucket( req, this, false, bucketInfo, null );
	}
	
	/*
	 * Returns a TreeInfo containing the display information for the
	 * Binder hierarchy referred to by a BucketInfo.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 */
	private TreeInfo expandVerticalBucket( HttpServletRequest req, boolean findBrowser, BucketInfo bucketInfo ) {
		// Expand the bucket list taking any persistent Binder
		// expansions into account.
		return GwtServerHelper.expandBucket( req, this, findBrowser, bucketInfo, new ArrayList<Long>() );
	}

	/*
	 * Returns a List<TreeInfo> containing the display information for
	 * the Binder hierarchy referred to by binderId from the
	 * perspective of the currently logged in user.
	 */
	private List<TreeInfo> getHorizontalTree( HttpServletRequest req, Long binderId, TreeMode treeMode ) {
		Binder binder;
		List<TreeInfo> reply;
		
		// Can we access the Binder?
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, String.valueOf( binderId ), true );
		if (null == binder) {
			// No!  Then we can't build any TreeInfo objects for it.
			reply = new ArrayList<TreeInfo>();
		}
		else {
			// Yes, we can access the Binder!  Access the Binder's
			// nearest containing Workspace...
			boolean isFilr = Utils.checkIfFilr();
			List<Long> bindersList = new ArrayList<Long>();
			while (true)
			{
				bindersList.add( 0, binder.getId() );
				binder = binder.getParentBinder();
				if ( null == binder )
				{
					break;
				}

				// If we're displaying folder bread crumbs in Filr...
				if ( isFilr && treeMode.equals( TreeMode.HORIZONTAL_BINDER ) && binder.isReserved() )
				{
					String biid = binder.getInternalId();
					if ( MiscUtil.hasString( biid ))
					{
						if ( biid.equals( ObjectKeys.TOP_WORKSPACE_INTERNALID ) || biid.equals( ObjectKeys.PROFILE_ROOT_INTERNALID ) )
						{
							// ...we ignore and stop at the top (home)
							// ...or profile root workspaces.
							break;
						}
					}
				}
			}
	
			// ...and build the TreeInfo for the request Binder.
			reply = GwtServerHelper.buildTreeInfoFromBinderList(
				req,
				this,
				bindersList );
		}


		// If we get here, reply refers to the TreeInfo for the Binder
		// requested.  Return it.
		return reply;
	}
	
	/*
	 * Builds a TreeInfo for a Binder being expanded.
	 */
	private TreeInfo getHorizontalNode( HttpServletRequest req, String binderIdS )
	{
		Binder binder;
		TreeInfo reply;
		
		// Access the Binder...
		binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS );

		// ...and build the TreeInfo for it.
		reply = ((null == binder) ? null : GwtServerHelper.buildTreeInfoFromBinder( req, this, false, binder ));
		return reply;
	}

	/*
	 * Returns the ID of the nearest containing workspace of a given
	 * Binder.
	 */
	private Long getRootWorkspaceId( HttpServletRequest req, Long currentRootBinderId, Long binderId )
	{
		Long reply;
		
		// Can we access the target binder?
		Binder binder = GwtUIHelper.getBinderSafely( getBinderModule(), binderId );
		if (null != binder)
		{
			// Yes!  Is it's workspace other than the current root, if
			// we were given one?
			Binder binderWS = BinderHelper.getBinderWorkspace( binder );
			if ((null != binderWS) && (null != currentRootBinderId) && (!(currentRootBinderId.equals(binderWS.getId())))) {
				// Yes!  Walk up that workspace's parentage.
				Binder parent = binderWS;
				do {
					// Does this workspace have a parent?
					parent = parent.getParentBinder();
					if (null == parent) {
						// No!  We'll just return the workspace we
						// already found.
						break;
					}
					
					// Is this parent the current root?
					if (currentRootBinderId.equals(parent.getId())) {
						// Yes!  We'll just return that then (i.e., the
						// current root.)
						binderWS = ((Workspace) parent);
						break;
					}
				} while (true);
			}
			reply = binderWS.getId();
		}
		else
		{
			Long topWSId = getWorkspaceModule().getTopWorkspaceId();
			reply = topWSId;
		}
		
		return reply;
	} 

	/*
	 * Returns a TreeInfo object containing the display information for
	 * and activity streams tree using the current Binder referred to
	 * by binderId from the perspective of the currently logged in
	 * user.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget when in activity streams
	 * mode.
	 */
	private TreeInfo getVerticalActivityStreamsTree(HttpServletRequest req, String binderIdS) {
		return GwtActivityStreamHelper.getVerticalActivityStreamsTree(req, this, binderIdS);
	}
	
	/*
	 * Returns a TreeInfo object containing the display information for
	 * the Binder referred to by binderId from the perspective of the
	 * currently logged in user.  Information about the Binder
	 * expansion states for the current user is integrated into the
	 * TreeInfo returned.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 */
	private TreeInfo getVerticalTree(HttpServletRequest req, boolean findBrowser, String binderIdS) throws GwtTeamingException {
		// Can we access the Binder?
		TreeInfo reply;
		Binder binder = GwtServerHelper.getBinderForWorkspaceTree( this, binderIdS, true );
		if (null == binder) {
			// No!  We can't build a TreeInfo for it.
			reply = new TreeInfo();
		}
		
		else {
			// Yes, we can access the Binder!  Access the Binder's
			// nearest containing Workspace...
			Workspace binderWS;
			if (findBrowser)
			     binderWS = getWorkspaceModule().getTopWorkspace();
			else binderWS = BinderHelper.getBinderWorkspace( binder );
	
			// ...note that the Workspace should always be expanded...
			Long binderWSId = binderWS.getId();
			ArrayList<Long> expandedBindersList = new ArrayList<Long>();
			expandedBindersList.add( binderWSId );
	
			// ...calculate which additional Binder's that must be expanded
			// ...to show the requested Binder...
			long binderId      = Long.parseLong( binderIdS );
			long binderWSIdVal = binderWSId.longValue();
			if ( binderId != binderWSIdVal )
			{
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
				req,
				this,
				findBrowser,
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
			if ( allowTrash && ( !(binder.isMirrored()) ) && ( !findBrowser ))
			{
				// ...add a TreeInfo to the reply's children for it.
				GwtServerHelper.addTrashFolder( this, reply, binder );
			}
			
			// Finally, if we're rooted on the current user's workspace...
			if ( (!findBrowser) && binderWS.getId().equals( GwtServerHelper.getCurrentUser().getWorkspaceId() ) )
			{
				// ...add the TreeInfo's for the collections we display
				// ...at the top of the tree.
				GwtServerHelper.addCollections( this, req, reply );
			}
		}

		// If we get here, reply refers to the TreeInfo for the Binder
		// requested.  Return it.
		return reply;
	}
	
	/*
	 * Builds a TreeInfo for the Binder being expanded and stores the
	 * fact that it has been expanded.
	 * 
	 * The information returned is typically used for driving a
	 * vertical WorkspaceTreeControl widget.
	 */
	private TreeInfo getVerticalNode(HttpServletRequest req, boolean findBrowser, String binderIdS) {
		// Access the Binder...
		TreeInfo reply;
		Binder binder = GwtServerHelper.getBinderForWorkspaceTree(this, binderIdS);
		if ( null != binder )
		{
			// ...note that the Binder will now be expanded...
			ArrayList<Long> expandedBindersList = new ArrayList<Long>();
			expandedBindersList.add( Long.parseLong( binderIdS ));
	
			// ...and build the TreeInfo folist.toArray(infoArray);r it.
			reply = GwtServerHelper.buildTreeInfoFromBinder( req, this, findBrowser, binder, expandedBindersList );
		}
		else
		{
			reply = null;
		}
		return reply;
	}
	
	
	/*
	 * Parse the given html and replace any markup with the appropriate url.  For example,
	 * replace {{attachmentUrl: somename.png}} with a url that looks like http://somehost/ssf/s/readFile/.../somename.png
	 */
	private String markupStringReplacement( HttpServletRequest req, String binderId, String html, String type ) throws GwtTeamingException
	{
		return GwtServerHelper.markupStringReplacementImpl( this, req, binderId, html, type );
	}
	
	
	/*
	 * Parse the given HTML and replace any markup with the appropriate
	 * URL.
	 * 
	 * For example:
	 *    replace {{attachmentUrl: somename.png}}
	 *    with a URL that looks like http://somehost/ssf/s/readFile/.../somename.png
	 */
	private String markupStringReplacement(HttpServletRequest req, FolderEntry entry, String html, String type) throws GwtTeamingException {
		String newHtml = "";
		if (html != null && html.length() > 0) {
			try {
				// Parse the given HTML and replace any markup with the appropriate URL.
				newHtml = MarkupUtil.markupStringReplacement(null, null, req, null, entry, html, type);

				// Perform fixups of wiki markup.
				newHtml = MarkupUtil.markupSectionsReplacement(newHtml);
			}
			
			catch (Exception e) {
				throw GwtLogHelper.getGwtClientException(e);
			}
		}
		return newHtml;
	}
	
	
	/*
	 * Saves the fact that the Binder for the given ID should be
	 * collapsed for the current User.
	 */
	private Boolean persistNodeCollapse(HttpServletRequest req, String binderId) {
		GwtServerHelper.persistNodeCollapse(this, Long.parseLong(binderId));
		return Boolean.TRUE;
	}

	/*
	 * Saves the fact that the Binder for the given ID should be
	 * expanded for the current User.
	 */
	private Boolean persistNodeExpand(HttpServletRequest req, String binderId) {
		GwtServerHelper.persistNodeExpand(this, Long.parseLong(binderId));
		return Boolean.TRUE;
	}
	
	/*
	 * Sets the user's list of favorites to favoritesList.
	 */
	private Boolean updateFavorites(HttpServletRequest req, List<FavoriteInfo> favoritesList) {
		Favorites f = new Favorites();
		for (Iterator<FavoriteInfo> fiIT = favoritesList.iterator(); fiIT.hasNext(); ) {
			FavoriteInfo fi = fiIT.next();
			try {
				f.addFavorite(fi.getName(), fi.getHover(), fi.getType(), fi.getValue(), fi.getAction(), fi.getCategory());
			}
			catch(FavoritesLimitExceededException flee) {
				//There are already too many favorites, skip the rest 
				//  (This should never happen when editing existing favorites)
			}
		}
		
		getProfileModule().setUserProperty( null, ObjectKeys.USER_PROPERTY_FAVORITES, f.toString() );
		return Boolean.TRUE;
	}

	/*
	 * Returns a List<TagInfo> of the tags defined on a binder.
	 */
	private ArrayList<TagInfo> getBinderTags(HttpServletRequest req, String binderId) {
		return GwtServerHelper.getBinderTags(this, binderId);
	}
	
	/*
	 * Returns true if the user can manage personal tags on the binder.
	 */
	private Boolean canManagePersonalBinderTags(HttpServletRequest req, String binderId) {
		return GwtServerHelper.canManagePersonalBinderTags(this, binderId);
	}
	
	/*
	 * Returns true if the user can manager personal tags on the entry.
	 */
	private Boolean canManagePersonalEntryTags(HttpServletRequest req, String entryId) {
		return GwtServerHelper.canManagePersonalEntryTags(this, entryId);
	}
	
	/*
	 * Returns true if the user can manage public tags on the binder
	 * and false otherwise.
	 */
	private Boolean canManagePublicBinderTags(HttpServletRequest req, String binderId) {
		return GwtServerHelper.canManagePublicBinderTags(this, binderId);
	}

	/*
	 * Returns true if the user can manage public tags on the given entry
	 * and false otherwise.
	 */
	private Boolean canManagePublicEntryTags(HttpServletRequest req, String entryId) {
		return GwtServerHelper.canManagePublicEntryTags(this, entryId);
	}
	
	/*
	 * Return the administration options the user has rights to run.
	 */
	private ArrayList<GwtAdminCategory> getAdminActions(HttpServletRequest req, String binderId) throws GwtTeamingException {
		try {
			Binder binder = getBinderModule().getBinder(Long.parseLong(binderId));
			ArrayList<GwtAdminCategory> adminActions = GwtServerHelper.getAdminActions(req, binder, this);
			return adminActions;
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
	}

	
	/*
	 * Returns a BinderInfo describing a binder.
	 */
	private BinderInfo getBinderInfo(HttpServletRequest req, String binderId) {
		return GwtServerHelper.getBinderInfo(this, req, binderId);
	}

	/*
	 * Returns a ViewInfo used to control folder views based on a URL.
	 */
	private ViewInfo getViewInfo(HttpServletRequest req, GetViewInfoCmd viCmd) throws GwtTeamingException {
		return GwtViewHelper.getViewInfo(this, req, viCmd.getUrl());
	}

	/*
	 * Returns the ID of the default view definition of a folder.
	 */
	private String getDefaultFolderDefinitionId(HttpServletRequest req, String binderId) {
		return GwtServerHelper.getDefaultFolderDefinitionId(this, binderId);
	}

	/*
	 * Returns the ID of the current user's default storage area.
	 */
	private String getDefaultStorageId() {
		Long	mfRootId;
		User	user          = RequestContextHolder.getRequestContext().getUser();
		Long	userWSId      = user.getWorkspaceId();
		boolean	usingHomeAsMF = SearchUtils.useHomeAsMyFiles(this);
        if (usingHomeAsMF)
             mfRootId = SearchUtils.getHomeFolderId(   this             );
        else mfRootId = SearchUtils.getMyFilesFolderId(this, user, false);
        String defaultStorageId;
        if (null == mfRootId)
        	 defaultStorageId = String.valueOf(userWSId);
        else defaultStorageId = String.valueOf(mfRootId);
        return defaultStorageId;
	}

	/*
	 * Returns information about the current user's favorites.
	 */
	private List<FavoriteInfo> getFavorites(HttpServletRequest req) {
		return GwtServerHelper.getFavorites(this);
	}
	
	/*
	 * Return a list of all the tasks assigned to the logged in user.
	 */
	private List<TaskInfo> getMyTasks( HttpServletRequest req )
	{
		SimpleProfiler.start( "GwtRpcServiceImpl.getMyTasks()" );
		try
		{
			return GwtTaskHelper.getMyTasks( this, req );
		}
		finally
		{
			SimpleProfiler.stop( "GwtRpcServiceImpl.getMyTasks()" );
		}
	}

	/*
	 * Returns information about the teams the current user is a member
	 * of.
	 */
	private List<TeamInfo> getMyTeams(HttpServletRequest req) {
		return GwtServerHelper.getMyTeams(req, this);
	}
	
	/*
	 * Return the url needed to invoke the user's "micro-blog" page.  
 	 */
	private String getMicrBlogUrl(HttpServletRequest req, String binderId) throws GwtTeamingException {
		try {
			// Does the user have rights to run the "site administration" page?
			Principal p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			if (p != null) {
				// Yes
				Long random = System.currentTimeMillis();
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL( req, "ss_forum", true );
				adapterUrl.setParameter( WebKeys.ACTION, WebKeys.ACTION_AJAX_REQUEST );
				adapterUrl.setParameter( WebKeys.URL_OPERATION, WebKeys.OPERATION_VIEW_MINIBLOG );
				adapterUrl.setParameter( WebKeys.URL_USER_ID, p.getId().toString() );
				adapterUrl.setParameter( WebKeys.URL_PAGE, "0" );
				adapterUrl.setParameter( "randomNumber", Long.toString(random) );
				
				return adapterUrl.toString();
			}
		
			GwtTeamingException ex = GwtLogHelper.getGwtClientException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
		} 
	}
	
	/*
	 * Return the URL needed to start an Instant Message with the user.
 	 */
	private String getImUrl(HttpServletRequest req, String binderId) throws GwtTeamingException {
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

			ex = GwtLogHelper.getGwtClientException();
			ex.setExceptionType( ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION );
			throw ex;
			
		} catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException( ex );
		} 
	}

	/*
	 * ? 
	 */
	private GwtPresenceInfo getPresenceInfo(HttpServletRequest req, String userIdS, String binderIdS) throws GwtTeamingException {
		try {
			User userAsking = GwtServerHelper.getCurrentUser();
			GwtPresenceInfo gwtPresence = new GwtPresenceInfo();

			// Can't get presence if we are a guest
			if (userAsking != null && !(ObjectKeys.GUEST_USER_INTERNALID.equals(userAsking.getInternalId()))) {
				Principal p;
				if (MiscUtil.hasString(binderIdS))
				     p = GwtProfileHelper.getPrincipalByBinderId(this,              binderIdS);
				else p = GwtServerHelper.getUserFromId(          this, Long.valueOf(userIdS) );

				if (p != null) {
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
			throw GwtLogHelper.getGwtClientException( e );
		}
	}

	/*
	 * Returns information about the saved search the current user has
	 * defined.
	 */
	private List<SavedSearchInfo> getSavedSearches(HttpServletRequest req) {
		return GwtServerHelper.getSavedSearches(this);
	}
	
	/*
	 * Return a list of files from the given folder.
	 */
	private ArrayList<GwtAttachment> getListOfFiles(HttpServletRequest req, String zoneUUID, String folderId, int numFiles) throws GwtTeamingException {
		// Get a list of the first n entries in the folder.
		ArrayList<GwtAttachment>  listOfFiles = new ArrayList<GwtAttachment>();
		ArrayList<GwtFolderEntry> entries     = getFolderEntries(req, zoneUUID, folderId, numFiles, 0, true);
		if (entries != null) {
			for (GwtFolderEntry entry: entries) {
				// Get the files attached to this entry
				ArrayList<GwtAttachment> files = entry.getFiles();
				if (files != null) {
					for (GwtAttachment file: files) {
						// Have we reached the max number of files to return?
						if (listOfFiles.size() >= numFiles) {
							// Yes
							break;
						}
						
						// Add this file to the list.
						listOfFiles.add( file );
					}
				}
				
				// Have we reached the max number of files to return?
				if (listOfFiles.size() >= numFiles) {
					// Yes
					break;
				}
			}
		}
		
		return listOfFiles;
	}
	
	/*
	 * Return login information such as self registration and auto complete.
	 */
	private GwtLoginInfo getLoginInfo(HttpServletRequest req) {
		return GwtServerHelper.getLoginInfo(req, this);
	}
	
	/*
	 * Removes a search based on its SavedSearchInfo.
	 */
	private Boolean removeSavedSearch(HttpServletRequest req, SavedSearchInfo ssi) {
		return GwtServerHelper.removeSavedSearch(this, ssi);
	}
	
	/*
	 * Save the given subscription data for the given entry id.
	 */
	private Boolean saveSubscriptionData( HttpServletRequest req, String entryId, SubscriptionData subscriptionData )
		throws GwtTeamingException
	{
		return GwtServerHelper.saveSubscriptionData( this, entryId, subscriptionData );
	}
	
	/*
	 * Called to mark that the current user is tracking the specified
	 * binder.
	 */
	private Boolean trackBinder(HttpServletRequest req, String binderId) {
		Boolean reply;
		try {
			BinderHelper.trackThisBinder( this, Long.parseLong(binderId), "add" );
			reply = Boolean.TRUE;
		}
		catch (Exception e) {
			reply = Boolean.FALSE;
		}
		return reply;
	}
	
	/*
	 * Called to mark that the current user is no longer tracking the
	 * specified binder.
	 */
	private Boolean untrackBinder( HttpServletRequest req, Long binderId )
	{
		Boolean reply;
		try {
			BinderHelper.trackThisBinder( this, binderId, "delete" );
			reply = Boolean.TRUE;
		}
		catch (Exception e) {
			reply = Boolean.FALSE;
		}
		return reply;
	}
	
	/*
	 * Called to mark that the current user is no longer tracking the
	 * specified person.  The person can be specified by a binder ID
	 * (uses the owner of the binder) or a user ID.
	 */
	private Boolean untrackPerson( HttpServletRequest req, Long binderId, Long userId )
	{
		// If we weren't giving a user ID...
		if ( null == userId )
		{
			// ...extract it from the binder.
			Binder binder = getBinderModule().getBinderWithoutAccessCheck( binderId );
			userId = binder.getOwnerId();
		}
		
		Boolean reply;
		try {
			BinderHelper.trackThisBinder( this, userId, "deletePerson" );
			reply = Boolean.TRUE;
		}
		catch (Exception e) {
			reply = Boolean.FALSE;
		}
		return reply;
	}

	/*
	 * Called to check if the current user is tracking the
	 * person whose workspace is the specified binder.
	 */
	private Boolean isPersonTracked(HttpServletRequest req, String binderId) {
		Boolean reply;
		if (MiscUtil.hasString(binderId))
		     reply = BinderHelper.isPersonTracked(this, Long.parseLong(binderId));
		else reply = Boolean.FALSE;
		return reply;
	}
	
	/*
	 * Get the subscription data for the given entry id.
	 */
	private SubscriptionData getSubscriptionData( HttpServletRequest req, String entryId )
	{
		SubscriptionData subscriptionData;
		
		subscriptionData = GwtServerHelper.getSubscriptionData( this, entryId );
		
		return subscriptionData;
	}
	
	/*
	 * Edits the given entry.  Returns an object that can be used by
	 * the What's New page.
	 */
	private ActivityStreamEntry editEntry(HttpServletRequest req, Long entryId, String title, String desc) throws GwtTeamingException {
		ActivityStreamEntry reply;
		try {
			// Edit the given reply.
			FolderEntry entry = GwtServerHelper.editReply(this, entryId, title, desc);
			
			// Get an ActivityStreamEntry from the reply's FolderEntry.
			reply = GwtActivityStreamHelper.getActivityStreamEntry(req, this, entry);
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
		}
		
		return reply;
	}
	
	/*
	 * Add a reply to the given entry.  Return an object that can be used by the What's New page.
	 */
	private ActivityStreamEntry replyToEntry( HttpServletRequest req, String entryId, String title, String desc ) throws GwtTeamingException
	{
		ActivityStreamEntry asEntry;
		
		try
		{
			FolderEntry entry;

			// Add the reply to the given entry.
			entry = GwtServerHelper.addReply( this, entryId, title, desc );
			
			// Get an ActivityStreamEntry from the reply's FolderEntry.
			asEntry = GwtActivityStreamHelper.getActivityStreamEntry( req, this, entry );
		}
		catch ( Exception ex )
		{
			throw GwtLogHelper.getGwtClientException( ex );
		}
		
		return asEntry;
	}
	
	/*
	 * Save the given branding data to the given binder.
	 */
	private Boolean saveBrandingData(HttpServletRequest req, String binderId, GwtBrandingData brandingData) throws GwtTeamingException {
		try {
			// Get the binder object.
			BinderModule binderModule = getBinderModule();
			Long binderIdL = new Long( binderId );
			if (binderIdL != null) {
				// Create a Map that holds the branding and extended branding.
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				
				// Add the old-style branding to the map.
				// JW:  Do we need to do something with the HTML found
				//      in the branding?
				String branding = brandingData.getBranding();
				if (branding == null) {
					branding = "";
				}
				
				// Remove mce_src as an attribute from all <img> tags.  See bug 766415.
				// There was a bug that caused the mce_src attribute to be included in the <img>
				// tag and written to the db.  We want to remove it.
				branding = MarkupUtil.removeMceSrc( branding );

				hashMap.put( "branding", branding );

				// Add the extended branding data to the map.
				branding = brandingData.getBrandingAsXmlString();
				if ( branding == null )
					branding = "";

				hashMap.put( "brandingExt", branding );
				
				// Update the binder with the new branding data.
				MapInputData dataMap = new MapInputData( hashMap );
				binderModule.modifyBinder( binderIdL, dataMap, null, null, null );
			}
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
		
		return Boolean.TRUE;
	}

	/*
	 * Save the given personal preferences for the logged in user.
	 */
	private Boolean savePersonalPreferences(HttpServletRequest req, GwtPersonalPreferences personalPrefs) throws GwtTeamingException {
		try
		{
			// Are we dealing with the guest user?
			User user = GwtServerHelper.getCurrentUser();
			if (!(ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId()))) {
				ProfileModule profileModule = getProfileModule();
				
				// No
				// Save the "display style" preference
				{
					Map<String,Object> updates = new HashMap<String,Object>();
					String newDisplayStyle = personalPrefs.getDisplayStyle();
					
					// Only allow "word" characters (such as a-z_0-9 )
					if (newDisplayStyle.equals("") || !newDisplayStyle.matches("^.*[\\W]+.*$")) {
						updates.put( ObjectKeys.USER_PROPERTY_DISPLAY_STYLE, newDisplayStyle );
						profileModule.modifyEntry( user.getId(), new MapInputData( updates ) );
					}
				}
				
				// Save the "number of entries per page" preference
				{
					profileModule.setUserProperty(
						user.getId(),
						ObjectKeys.PAGE_ENTRIES_PER_PAGE,
						String.valueOf( personalPrefs.getNumEntriesPerPage() ) );
				}
				
				// Save the "file link action" preference
				{
					profileModule.setUserProperty(
						user.getId(),
						ObjectKeys.FILE_LINK_ACTION,
						String.valueOf( personalPrefs.getFileLinkAction().ordinal() ) );
				}
				
				// Save the "Hide Public Collection" preference.
				{
					// If public shares are active and this is not an
					// external user...
					if (personalPrefs.publicSharesActive() && ( user.getIdentityInfo().isFromLocal() || user.getIdentityInfo().isFromLdap())) {
						// ...store the user's hide/show public collection value.
						profileModule.setUserProperty(
							user.getId(),
							ObjectKeys.HIDE_PUBLIC_COLLECTION,
							personalPrefs.getHidePublicCollection() );
					}
				}
			}
			
			else {
				GwtLogHelper.warn(logger, "GwtRpcServiceImpl.getPersonalPreferences(), user is guest.");
			}
		}
		
		catch (Exception e) {
			if (e instanceof AccessControlException)
				 GwtLogHelper.warn( logger, "GwtRpcServiceImpl.savePersonalPreferences() AccessControlException" );
			else GwtLogHelper.warn( logger, "GwtRpcServiceImpl.savePersonalPreferences() unknown exception" );
			throw GwtLogHelper.getGwtClientException( e );
		}
		
		return Boolean.TRUE;
	}

	/*
	 * Get the profile information based on the binder Id passed in.
	 */
	private ProfileInfo getProfileInfo(HttpServletRequest req, String binderId) throws GwtTeamingException {
		try {
			//get the binder
			ProfileInfo profile = GwtProfileHelper.buildProfileInfo(req, this, Long.valueOf(binderId));
			return profile;
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
	}
	
	/*
	 * Get the profile information for the quick view based on the
	 * binder Id passed in.
	 */
	private ProfileInfo getQuickViewInfo(HttpServletRequest req, String userId, String binderId) throws GwtTeamingException {
		try {
			// Get the ProfileInfo.
			Long binderIdL = (MiscUtil.hasString(binderId) ? Long.valueOf(binderId) : null);
			Long userIdL   =                                 Long.valueOf(userId  );
			ProfileInfo profile = GwtProfileHelper.buildQuickViewProfileInfo(req, this, userIdL, binderIdL);
			return profile;
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
	}

	/*
	 * Return the membership of the given group.
	 */
	private int getGroupMembership(
			HttpServletRequest req,
			ArrayList<GwtTeamingItem> retList,
			String groupId,
			int offset,
			int numResults,
			MembershipFilter filter ) throws GwtTeamingException
	{
		return GwtServerHelper.getGroupMembership( this, retList, groupId, offset, numResults, filter );
	}
	
	
	/*
	 * Returns Look up the user and return the list of groups they belong to.
	 */
	private List<GroupInfo> getGroups(HttpServletRequest req, String binderId) throws GwtTeamingException {
		try {
			Long userId = null;
			Principal p = null;

			if (binderId != null) {
				p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			}
			
			if (p != null){
				userId = p.getId();
			}
			
			return GwtServerHelper.getGroups( req, this, userId );
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
		} 
	}

	/*
	 * Returns Look up the workspace owner and return the list of teams they belong to.
	 */
	private List<TeamInfo> getTeams(HttpServletRequest req, String binderId) throws GwtTeamingException {
		try {
			Long userId = null;
			Principal p = null;

			if (binderId != null) {
				p = GwtProfileHelper.getPrincipalByBinderId(this, binderId);
			}
			
			if (p != null){
				userId = p.getId();
			}
			
			return GwtServerHelper.getTeams( req, this, userId );
		}
		
		catch (Exception ex) {
			throw GwtLogHelper.getGwtClientException(ex);
		} 
	}

	/*
	 * Save the User Status
	 */
	private SaveUserStatusRpcResponseData saveUserStatus(HttpServletRequest req, String status) throws GwtTeamingException {
		try {
			BinderHelper.MiniBlogInfo mbi = BinderHelper.addMiniBlogEntryDetailed(this, status);
			return new SaveUserStatusRpcResponseData(mbi.getEntryId(), mbi.getFolderId(), mbi.isNewMiniBlogFolder());
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
	}

	/*
	 * Return the "user permalink" URL.
	 */
	private String getUserPermalink(HttpServletRequest req, String userId) {
		if (userId != null && userId.length() > 0) {
			Long userIdL = new Long(userId);
			User u = ((User) getProfileModule().getEntry(userIdL));
			return PermaLinkUtil.getPermalink(req, u);
		}
		return "";
	}
	
	/*
	 * Get the User Status from their Micro Blog
	 */
	private UserStatus getUserStatus(HttpServletRequest req, String sUserId, String sbinderId) throws GwtTeamingException {
		return GwtProfileHelper.getUserStatus(this, sUserId, sbinderId);
	}

	/*
	 * Get the User Workspace Information from a user's Workspace ID.
	 */
	private UserWorkspaceInfoRpcResponseData getUserWorkspaceInfo(HttpServletRequest req, Long binderId) throws GwtTeamingException {
		boolean canAccessUserWorkspace;
		Long userId = null;
		Binder binder;
		try {
			binder = getBinderModule().getBinder( binderId );
			canAccessUserWorkspace = ( null != binder );
		}
		catch (Exception e) {
			canAccessUserWorkspace = false;
		}
		
		if (!canAccessUserWorkspace) {
			try {
				binder = getBinderModule().getBinderWithoutAccessCheck( binderId );
				if (null != binder) {
					Principal owner = binder.getCreation().getPrincipal(); //creator is user
					owner = Utils.fixProxy(owner);
					userId = owner.getId();
				}
			}
			
			catch (Exception e) {
				throw GwtLogHelper.getGwtClientException(e);
			}
		}
		
		return new UserWorkspaceInfoRpcResponseData(canAccessUserWorkspace, userId);
	}

	/*
	 * Get the stats for the user.
	 */
	private ProfileStats getProfileStats(HttpServletRequest req, String binderId, String userId) throws GwtTeamingException {
		try {
			User binderCreator = GwtServerHelper.getBinderCreator(this, binderId);
			ProfileStats stats = GwtProfileHelper.getStats(req, this, String.valueOf(binderCreator.getId()));
			return stats;
		}
		
		catch (Exception e) {
			if ((e instanceof AccessControlException) ||
				(e instanceof NoUserByTheIdException)) {
				throw GwtLogHelper.getGwtClientException(e);
			}
			
			// Log other errors.
			logger.error("Error getting stats for user with binderId " + userId, e);
		}
		
		return null;
	}

	/*
	 * Constructs and returns a PasswordExpirationRpcResponseData
	 * describing the given user's password status.
	 */
	private static PasswordExpirationRpcResponseData getPasswordExpirationInfo( User user )
	{
		// Can the user's password expire?
		Date    expirationDate = PasswordPolicyHelper.getUsersPasswordExpiration( user );
		boolean aboutToExpire;
		String  expirationString;
		if ( null == expirationDate )
		{
			// No!
			expirationString = null;
			aboutToExpire    = false;
		}
		else
		{
			// Yes, the user's password can expire!  Is it expired
			// now?
			long expirationTime = expirationDate.getTime();
			if ( expirationTime < new Date().getTime() ) {
				// Yes!
				expirationString = NLT.get("general.password.expired");
				aboutToExpire    = true;
			}
			else {
				// No, it not expired yet!
				expirationString = GwtServerHelper.getDateString( expirationDate, DateFormat.MEDIUM, user.getTimeZone());
				long warningTime = PasswordPolicyHelper.getPasswordWarningDate().getTime();
				aboutToExpire    = (expirationTime < warningTime);
			}
		}
		
		// Return a PasswordExpirationRpcResponseData with the
		// settings.
		return new PasswordExpirationRpcResponseData( expirationString, aboutToExpire );
	}

	/*
	 * Get the avatars for the user profile.
	 */
	private ProfileAttribute getProfileAvatars(HttpServletRequest req, String binderId) {
		ProfileAttribute attr = GwtProfileHelper.getProfileAvatars(req, this, Long.valueOf(binderId));
		return attr;
	}
	
	/*
	 * Get disk usage information per user
	 */
	private  DiskUsageInfo getDiskUsageInfo(HttpServletRequest req, String binderId) throws GwtTeamingException {
		DiskUsageInfo diskUsageInfo = null;
		try {
			diskUsageInfo = new DiskUsageInfo();
			GwtProfileHelper.getDiskUsageInfo(req, this, binderId, diskUsageInfo);
			
			return diskUsageInfo;
		}
		
		catch (Exception e) {
			throw GwtLogHelper.getGwtClientException(e);
		}
	}
	
	/*
	 * Update the tags for the given binder.
	 */
	private Boolean updateBinderTags(HttpServletRequest req, String binderId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded) {
		return GwtServerHelper.updateBinderTags(this, binderId, tagsToBeDeleted, tagsToBeAdded);
	}

	/*
	 * Update the tags for the given entry.
	 */
	private Boolean updateEntryTags(HttpServletRequest req, String entryId, ArrayList<TagInfo> tagsToBeDeleted, ArrayList<TagInfo> tagsToBeAdded) {
		return GwtServerHelper.updateEntryTags(this, entryId, tagsToBeDeleted, tagsToBeAdded);
	}

	/*
	 * Validates the list of TeamingEvents to see if the user has rights to perform the events
	 * for the given entry id.
	 */
	private List<EventValidation> validateEntryEvents( HttpServletRequest req, List<EventValidation> eventValidations, String entryId )
	{
		// Validate the given events.
		GwtServerHelper.validateEntryEvents( this, req, eventValidations, entryId );
		
		return eventValidations;
	}

	/*
	 * Return true if an entry has been seen and false otherwise.
	 */
	private Boolean isSeen( HttpServletRequest req, Long entryId ) throws GwtTeamingException
	{
		try
		{
			SeenMap seenMap = getProfileModule().getUserSeenMap( null );
			FolderEntry fe = getFolderModule().getEntry( null, entryId );
			return seenMap.checkIfSeen( fe );
		}
		
		catch ( Exception ex )
		{
			throw GwtLogHelper.getGwtClientException( ex );
		}
	}
	
	/*
	 * Save the given show setting (show all, show unread) for the What's New page
	 * to the user's properties.
	 */
	private Boolean saveWhatsNewShowSetting(HttpServletRequest req, ActivityStreamDataType showSetting) {
		return GwtServerHelper.saveWhatsNewShowSetting(this, showSetting);
	}
	
	/*
	 * Marks a list of entries as having been seen.
	 */
	private Boolean setSeen(HttpServletRequest req, List<Long> entryIds) throws GwtTeamingException {
		getProfileModule().setSeenIds(GwtServerHelper.getCurrentUserId(), entryIds);
		return Boolean.TRUE;
	}
	
	/*
	 * Marks a list of entries as having been unseen.
	 */
	private Boolean setUnseen(HttpServletRequest req, List<Long> entryIds) throws GwtTeamingException {
		getProfileModule().setUnseen(GwtServerHelper.getCurrentUserId(), entryIds);
		return Boolean.TRUE;
	}
}
