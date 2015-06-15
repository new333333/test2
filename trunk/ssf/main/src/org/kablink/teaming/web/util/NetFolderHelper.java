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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.dao.util.NetFolderSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NetFolderConfig.SyncScheduleOption;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.AuthenticationType;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver.ConnectionTestStatus;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver.ConnectionTestStatusCode;
import org.kablink.teaming.jobs.MirroredFolderSynchronization;
import org.kablink.teaming.jobs.NetFolderServerSynchronization;
import org.kablink.teaming.jobs.Schedule;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ldap.impl.LdapModuleImpl.HomeDirInfo;
import org.kablink.teaming.module.netfolder.NetFolderModule;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;

/**
 * Helper class dealing with net folders and net folder roots
 * 
 * @author jwootton
 */
public class NetFolderHelper
{
	protected static Log m_logger = LogFactory.getLog( NetFolderHelper.class );

	/**
	 * Create a net folder server for the given home dir info
	 */
	public static ResourceDriverConfig createHomeDirNetFolderServer(
		ProfileModule profileModule,
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		HomeDirInfo homeDirInfo )
	{
		String hostName = null;
		String serverAddr = null;
		String volume = null;
		String serverUNC;
		ResourceDriverConfig rdConfig;
		String rootName;
		ScheduleInfo scheduleInfo;
		Schedule schedule;
		Long zoneId;
		
		// Are we running Filr?
		if ( Utils.checkIfFilr() == false )
		{
			// No
			return null;
		}

		if ( homeDirInfo != null )
		{
			hostName = homeDirInfo.getServerHostName();
			serverAddr = homeDirInfo.getServerAddr();
			volume = homeDirInfo.getVolume();
		}
		
		// Do we have all the information we need?
		if ( serverAddr == null || serverAddr.length() == 0 ||
			 volume == null || volume.length() == 0 )
		{
			// No
			m_logger.error( "In NetFolderHelper.createHomeDirNetFolderServer(), invalid server information" );
			return null;
		}

		// Does a net folder server already exist with a unc using the server's ip address?
		serverUNC = "\\\\" + serverAddr + "\\" + volume;
		rdConfig = findNetFolderRootByUNC( adminModule, resourceDriverModule, serverUNC );
		if ( rdConfig != null )
		{
			// Yes
			m_logger.info( "In NetFolderHelper.createHomeDirNetFolderServer(), net folder server already exists" );
			return rdConfig;
		}
		
		if ( hostName != null && hostName.length() > 0 )
		{
			// Does a net folder server already exist with a unc using the server's host name?
			serverUNC = "\\\\" + hostName + "\\" + volume;
			rdConfig = findNetFolderRootByUNC( adminModule, resourceDriverModule, serverUNC );
			if ( rdConfig != null )
			{
				// Yes
				m_logger.info( "In NetFolderHelper.createHomeDirNetFolderServer(), net folder server already exists" );
				return rdConfig;
			}
		}

		// Create a net folder root.  The administrator will need to fill in credentials.
		if ( hostName != null && hostName.length() > 0 )
			rootName = hostName + "-" + volume;
		else
			rootName = serverAddr + "-" + volume;
		m_logger.info( "About to create a net folder server called: " + rootName  );
		
		// Create a default schedule for syncing the net folders associated with this net folder server
		// The schedule is configured to run every day at midnight
		zoneId = RequestContextHolder.getRequestContext().getZoneId();
		scheduleInfo = new ScheduleInfo( zoneId );
		scheduleInfo.setEnabled( true );
		schedule = new Schedule( "" );
		schedule.setDaily( true );
		// Set the schedule for midnight gmt
		{
			User currentUser;
			String hourStr;
			
			currentUser = RequestContextHolder.getRequestContext().getUser();
			if ( currentUser != null )
			{
				TimeZone tz;
				Date now = new Date();
				int offset;
				int offsetHour;
				int hour;

				tz = currentUser.getTimeZone();
				offset = tz.getOffset( now.getTime() );
				offsetHour = offset / (1000*60*60);
				hour = (0 - offsetHour) % 24;
				hourStr = String.valueOf( hour );
			}
			else
				hourStr = "0";
			
			schedule.setHours( hourStr );
		}
		schedule.setMinutes( "0" );
		scheduleInfo.setSchedule( schedule );
		
		rdConfig = NetFolderHelper.createNetFolderRoot(
													adminModule,
													resourceDriverModule,
													rootName,
													serverUNC,
													DriverType.famt,
													null,
													null,
													false,
													null,
													null,
													null,
													false,
													false,
													null,
													null,
													false,
													new Boolean( true ),
													NetFolderHelper.getDefaultJitsResultsMaxAge(),
													NetFolderHelper.getDefaultJitsAclMaxAge(),
													Boolean.TRUE,
													scheduleInfo );
		
		// Add a task for the administrator to enter the proxy credentials for this server.
		{
			UserProperties userProperties;
			FilrAdminTasks filrAdminTasks;
			String xmlStr;
			User adminUser;
			String adminUserName;
			String zoneName;

			// Get the admin user so we can add an administrative task to his user properties.
			zoneName = RequestContextHolder.getRequestContext().getZoneName();
			adminUserName = SZoneConfig.getAdminUserName( zoneName );
			adminUser = profileModule.getUser( adminUserName );
			
			// Get the FilrAdminTasks from the administrator's user properties
			userProperties = profileModule.getUserProperties( adminUser.getId() );
			xmlStr = (String)userProperties.getProperty( ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS );
			filrAdminTasks = new FilrAdminTasks( xmlStr );
			
			// Add a task for the administrator to enter the proxy credentials for this net folder server.
			filrAdminTasks.addEnterNetFolderServerProxyCredentialsTask( rdConfig.getId() );
			
			// Save the FilrAdminTasks to the administrator's user properties
			profileModule.setUserProperty(
										adminUser.getId(),
										ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS,
										filrAdminTasks.toString() );
		}

		return rdConfig;
	}
	
	/**
	 * Create a net folder and if needed a net folder root for the given home directory information
	 */
	public static void createHomeDirNetFolder(
		ProfileModule profileModule,
		TemplateModule templateModule,
		BinderModule binderModule,
		final FolderModule folderModule,
		final NetFolderModule netFolderModule,
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		RunAsyncManager asyncManager,
		HomeDirInfo homeDirInfo,
		User user ) throws WriteFilesException, WriteEntryDataException
	{
		createHomeDirNetFolder(
							profileModule,
							templateModule,
							binderModule,
							folderModule,
							netFolderModule,
							adminModule,
							resourceDriverModule,
							asyncManager,
							homeDirInfo,
							user,
							true );
	}
	
	/**
	 * Create a net folder and if needed a net folder root for the
	 * given home directory information.
	 * 
	 * @param profileModule
	 * @param templateModule
	 * @param binderModule
	 * @param folderModule
	 * @param netFolderModule
	 * @param adminModule
	 * @param resourceDriverModule
	 * @param asyncManager
	 * @param homeDirInfo
	 * @param user
	 * @param updateExistingNetFolder
	 * 
	 * @throws WriteFilesException
	 * @throws WriteEntryDataException
	 */
	public static void createHomeDirNetFolder(
		final ProfileModule profileModule,
		TemplateModule templateModule,
		BinderModule binderModule,
		final FolderModule folderModule,
		final NetFolderModule netFolderModule,
		final AdminModule adminModule,
		final ResourceDriverModule resourceDriverModule,
		RunAsyncManager asyncManager,
		final HomeDirInfo homeDirInfo,
		User user,
		boolean updateExistingNetFolder ) throws WriteFilesException, WriteEntryDataException
	{
		Long workspaceId;
		String netFolderServerName = null;
		String serverName = null;
		String serverAddr = null;
		String volume = null;
		String path = null;
		String serverUNC = null;
		ResourceDriverConfig rdConfig;
		boolean notEnoughInfo = false;
		boolean canSyncNetFolder = false;

		// Are we running Filr?
		if ( Utils.checkIfFilr() == false )
		{
			// No
			return;
		}
		
		if ( homeDirInfo != null )
		{
			netFolderServerName = homeDirInfo.getNetFolderServerName();
            serverName = homeDirInfo.getServerHostName();
			serverAddr = homeDirInfo.getServerAddr();
			volume = homeDirInfo.getVolume();
			path = homeDirInfo.getPath();
		}
		
		// Do we have all the information we need?
		if ( path == null || path.length() == 0 )
			notEnoughInfo = true;
		
		if ( (netFolderServerName == null || netFolderServerName.length() == 0) &&
             (((serverAddr == null || serverAddr.length() == 0) &&
                     (serverName == null || serverName.length() == 0)) ||
			 volume == null || volume.length() == 0) )
		{
			notEnoughInfo = true;
		}
		
		if ( notEnoughInfo )
		{
			// No
			m_logger.debug( "In NetFolderHelper.createHomeDirNetFolder(), not enough information to create home dir net folder" );
			return;
		}
		
		// Does the user have a workspace?
		workspaceId = user.getWorkspaceId();
		if ( workspaceId == null )
		{
			Workspace workspace;

			// No, create one
			workspace = profileModule.addUserWorkspace( user, null );
			workspaceId = workspace.getId();
		}
		
		// Are we looking for a specific net folder server that should already exists
		if ( netFolderServerName != null && netFolderServerName.length() > 0 )
		{
			// Yes, find the net folder server being referenced.
			rdConfig = NetFolderHelper.findNetFolderRootByName(
															adminModule,
															resourceDriverModule,
															netFolderServerName );
			serverUNC = rdConfig.getRootPath();
		}
		else
		{
			// Does a net folder root exists with a unc path to the given server address / volume?
			rdConfig = findNetFolderRootByHostAndVolume( adminModule, resourceDriverModule, serverName, serverAddr, volume);
			if ( rdConfig == null )
			{
                // No, create one
                rdConfig = (ResourceDriverConfig) RunasTemplate.runasAdmin(
                        new RunasCallback() {
                            @Override
                            public Object doAs() {
                                return NetFolderHelper.createHomeDirNetFolderServer(
                                        profileModule,
                                        adminModule,
                                        resourceDriverModule,
                                        homeDirInfo );
                            }
                        },
                        RequestContextHolder.getRequestContext().getZoneName()
                );
			}
            serverUNC = rdConfig.getRootPath();
		}
		
		// Is the net folder server configured?
		if ( rdConfig != null && isNetFolderServerConfigured( rdConfig ) )
		{
			ConnectionTestStatus status = null;
			Binder homeDirNetFolderBinder;
			NetFolderConfig nfc = null;
			boolean syncNeeded = false;
			
			// Yes
			canSyncNetFolder = true;
			
			// Does a net folder already exist for this user's home directory
			homeDirNetFolderBinder = NetFolderHelper.findHomeDirNetFolder(
																binderModule,
																user.getWorkspaceId() );
			if ( homeDirNetFolderBinder == null )
			{
				// No, create one.
				// Only create the net folder if we can successfully make a connection
				status = testNetFolderConnectionForHomeDirCreation(
																rdConfig.getName(),
																rdConfig.getDriverType(),
																rdConfig.getRootPath(),
																path,
																rdConfig.getAccountName(),
																rdConfig.getPassword() );
				
				if ( status != null && status.getCode() == ConnectionTestStatusCode.NORMAL )
				{
					String folderName;
					Long zoneId;
					ScheduleInfo scheduleInfo;
					Schedule schedule;
		
					folderName = NLT.get( "netfolder.default.homedir.name", user.getLocale() );
					m_logger.info( "About to create a net folder called: " + folderName + ", for the users home directory for user: " + user.getName() );
					
					// Create a default schedule for syncing the net folder
					// The schedule is disabled and configured to run every day at midnight
					zoneId = RequestContextHolder.getRequestContext().getZoneId();
					scheduleInfo = new ScheduleInfo( zoneId );
					scheduleInfo.setEnabled( false );
					schedule = new Schedule( "" );
					schedule.setDaily( true );
					// Set the schedule for midnight gmt
					{
						User currentUser;
						String hourStr;
						
						currentUser = RequestContextHolder.getRequestContext().getUser();
						if ( currentUser != null )
						{
							TimeZone tz;
							Date now = new Date();
							int offset;
							int offsetHour;
							int hour;

							tz = currentUser.getTimeZone();
							offset = tz.getOffset( now.getTime() );
							offsetHour = offset / (1000*60*60);
							hour = (0 - offsetHour) % 24;
							hourStr = String.valueOf( hour );
						}
						else
							hourStr = "0";
						
						schedule.setHours( hourStr );
					}
					schedule.setMinutes( "0" );
					scheduleInfo.setSchedule( schedule );

					// Create a net folder in the user's workspace
					nfc = NetFolderHelper.createNetFolder(
																templateModule,
																binderModule,
																folderModule,
																netFolderModule,
																adminModule,
																user,
																folderName,
																rdConfig.getName(),
																path,
																scheduleInfo,
																SyncScheduleOption.useNetFolderServerSchedule,
																workspaceId,
																true,
																false,
																new Boolean( true ),
																null,
																Boolean.FALSE,
																Boolean.TRUE );

					// As the fix for bug 831849 we must call getCoreDao().clear() before we call
					// NetFolderHelper.saveJitsSettings().  If we don't, saveJitsSettings() throws
					// a DuplicateKeyException.
					{
						CoreDao coreDao;
						
						coreDao = getCoreDao();
						if ( coreDao != null )
							coreDao.clear();
					}
					
					// Save the jits settings
					{
						NetFolderHelper.saveJitsSettings(
													netFolderModule,
													nfc,
													true,
													true,
													getDefaultJitsAclMaxAge(),
													getDefaultJitsResultsMaxAge() );
					}
					
					syncNeeded = false;
				}
			}
			else
			{
				nfc = NetFolderUtil.getNetFolderConfig( homeDirNetFolderBinder.getNetFolderConfigId() );

				// A home dir net folder already exists for this user.
				// Are we supposed to try and update an existing net folder?
				if ( updateExistingNetFolder )
				{
					String currentServerUNC = null;
					String normalizedPath = null;
					String currentBinderPath = null;
					ResourceDriver driver = null;
					
					// Yes
					// Get the server unc path that is currently being used by the user's home dir net folder.
					{
						driver = nfc.getResourceDriver();
						if ( driver != null )
						{
							ResourceDriverConfig currentRdConfig;
							
							currentRdConfig = driver.getConfig();
							if ( currentRdConfig != null )
								currentServerUNC = currentRdConfig.getRootPath();
						}
					}
							
					if ( driver != null )
					{
						// Normalize the path the net folder will use
						normalizedPath = driver.normalizedResourcePath( path );
					}
					
					currentBinderPath = nfc.getResourcePath();
					
					// Did any information about the home directory change?
					if ( (serverUNC != null && serverUNC.equalsIgnoreCase( currentServerUNC ) == false) ||
						 normalizedPath != null && normalizedPath.equalsIgnoreCase( currentBinderPath ) == false )
					{
						// Yes
						status = testNetFolderConnectionForHomeDirCreation(
																		rdConfig.getName(),
																		rdConfig.getDriverType(),
																		rdConfig.getRootPath(),
																		normalizedPath,
																		rdConfig.getAccountName(),
																		rdConfig.getPassword() );

						if ( status != null && status.getCode() == ConnectionTestStatusCode.NORMAL )
						{
							if ( currentServerUNC == null )
								currentServerUNC = "";
							
							if ( currentBinderPath == null )
								currentBinderPath = "";
							
							m_logger.info( "-----> Home-dir net folder info has changed!!!!" );
							m_logger.info( "-----> Current server unc: " + currentServerUNC );
							m_logger.info( "-----> New server unc: " + serverUNC );
							m_logger.info( "-----> Current net folder path: " + currentBinderPath );
							m_logger.info( "-----> New net folder path: " + normalizedPath );
							
			   				nfc.setNetFolderServerId(rdConfig.getId());
			   				nfc.setResourcePath(normalizedPath);
							// Update the folder's resource driver name and relative path.
			   				netFolderModule.modifyNetFolder(nfc);
						}
						
			   			syncNeeded = false;
					}
				}
			}
			
			// Do we need to sync the home directory net folder?
			if ( syncNeeded )
			{
				// Yes
				// Can we sync the home directory net folder?
				if ( canSyncNetFolder )
				{
					// Yes, sync it.
					try
					{
						final Long binderId;
						
						binderId = nfc.getTopFolderId();

						m_logger.info( "About to sync home directory net folder: " + binderId );
						folderModule.enqueueFullSynchronize( binderId );
					}
					catch ( Exception e )
					{
						m_logger.error( "Error syncing next net folder: " + nfc.getName() + ", " + e.toString() );
					}
				}
			}
		}
	}
	
	/**
	 * Create a net folder from the given data
	 */
	public static NetFolderConfig createNetFolder(
		TemplateModule templateModule,
		BinderModule binderModule,
		FolderModule folderModule,
		NetFolderModule netFolderModule,
		AdminModule adminModule,
		User owner,
		String name,
		String rootName,
		String path,
		ScheduleInfo scheduleInfo,
		SyncScheduleOption syncScheduleOption,
		Long parentBinderId,
		boolean isHomeDir,
		boolean indexContent,
		Boolean inheritIndexContentOption,
		Boolean fullSyncDirOnly,
		Boolean allowDesktopAppToTriggerSync,
		Boolean inheritAllowDesktopAppToTriggerSync ) throws WriteFilesException, WriteEntryDataException
	{
		NetFolderConfig nfc = null;
		Long templateId = null;
		List<TemplateBinder> listOfTemplateBinders;
		String templateInternalId;
		
		// Are we going to create a "home directory" net folder?
		if ( isHomeDir )
		{
			// Yes
			templateInternalId = ObjectKeys.DEFAULT_FOLDER_FILR_HOME_FOLDER_CONFIG;
		}
		else
		{
			// No
			templateInternalId = ObjectKeys.DEFAULT_FOLDER_FILR_ROOT_CONFIG ;
		}

		// Find the template binder for mirrored folders.
		listOfTemplateBinders = templateModule.getTemplates( Boolean.TRUE );
		if ( listOfTemplateBinders != null )
		{
			for ( TemplateBinder nextTemplateBinder : listOfTemplateBinders )
			{
				String internalId;
				
				internalId = nextTemplateBinder.getInternalId();
				if ( internalId != null && internalId.equalsIgnoreCase( templateInternalId) )
				{
					templateId = nextTemplateBinder.getId();
					break;
				}
			}
		}

		if ( templateId != null )
		{			
			nfc = netFolderModule.createNetFolder(
											templateId,
											parentBinderId,
											name,
											owner,
											rootName,
											path,
											isHomeDir,
											indexContent,
											inheritIndexContentOption,
											syncScheduleOption,
											fullSyncDirOnly,
											allowDesktopAppToTriggerSync,
											inheritAllowDesktopAppToTriggerSync );
			
			// Set the net folder's sync schedule
			if ( scheduleInfo != null )
			{
				// If the sync schedule option is to use the net folder server's schedule then
				// disable the schedule on the net folder.
				if ( syncScheduleOption == SyncScheduleOption.useNetFolderServerSchedule )
					scheduleInfo.setEnabled( false );
				
				scheduleInfo.setFolderId( nfc.getTopFolderId() );
				folderModule.setSynchronizationSchedule( scheduleInfo, nfc.getTopFolderId() );
			}
		}
		else
			m_logger.error( "Could not find the template binder for a mirrored folder" );
		
		return nfc;
	}
	
	/**
	 * Create a net folder root from the given data
	 */
	@SuppressWarnings({ "unchecked" })
	public static ResourceDriverConfig createNetFolderRoot(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		String name,
		String path,
		DriverType driverType,
		String proxyName,
		String proxyPwd,
		boolean useProxyIdentity,
		Long proxyIdentityId,
		Set<Long> memberIds,
		String hostUrl,
		boolean allowSelfSignedCerts,
		boolean isSharePointServer,
		Boolean fullSyncDirOnly,
		AuthenticationType authType,
		Boolean indexContent,
		Boolean enableJits,
		Long jitsResultsMaxAge,
		Long jitsAclMaxAge,
		Boolean allowDesktopAppToTriggerInitialHomeFolderSync,
		ScheduleInfo scheduleInfo ) throws RDException
	{
		Map options;
		ResourceDriverConfig rdConfig = null;
		
		adminModule.checkAccess( AdminOperation.manageResourceDrivers );
		
		// Does a net folder root already exist with the give name?
		if ( findNetFolderRootByName( adminModule, resourceDriverModule, name ) != null )
		{
			// Yes, do not allow this.
			throw new RDException(
								NLT.get( RDException.DUPLICATE_RESOURCE_DRIVER_NAME, new String[] {name}),
								name );
		}

		options = new HashMap();
		options.put( ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE );
		options.put( ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, proxyName ); 
		options.put( ObjectKeys.RESOURCE_DRIVER_PASSWORD, proxyPwd );
		options.put( ObjectKeys.RESOURCE_DRIVER_USE_PROXY_IDENTITY, ( useProxyIdentity ? Boolean.TRUE : null ));
		options.put( ObjectKeys.RESOURCE_DRIVER_PROXY_IDENTITY_ID, proxyIdentityId );
		options.put( ObjectKeys.RESOURCE_DRIVER_FULL_SYNC_DIR_ONLY, fullSyncDirOnly );
		options.put( ObjectKeys.RESOURCE_DRIVER_AUTHENTICATION_TYPE, authType );
		options.put( ObjectKeys.RESOURCE_DRIVER_INDEX_CONTENT, indexContent );
		options.put( ObjectKeys.RESOURCE_DRIVER_JITS_ENABLED, enableJits );
		options.put( ObjectKeys.RESOURCE_DRIVER_JITS_RESULTS_MAX_AGE, jitsResultsMaxAge );
		options.put( ObjectKeys.RESOURCE_DRIVER_JITS_ACL_MAX_AGE, jitsAclMaxAge );
		options.put( ObjectKeys.RESOURCE_DRIVER_ALLOW_DESKTOP_APP_TO_TRIGGER_HOME_FOLDER_SYNC, allowDesktopAppToTriggerInitialHomeFolderSync );
		
		// Is the root type WebDAV?
		if ( driverType == DriverType.webdav )
		{
			// Yes, get the WebDAV specific values
			options.put(
					ObjectKeys.RESOURCE_DRIVER_HOST_URL,
					hostUrl );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE,
					allowSelfSignedCerts );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH,
					isSharePointServer );
		}

		// Always prevent the top level folder from being deleted
		// This is forced so that the folder could not accidentally be deleted if the 
		// external disk was offline
		options.put( ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE );

		//Add this resource driver
		rdConfig = resourceDriverModule.addResourceDriver(
														name,
														driverType, 
														path,
														memberIds,
														options );

		// Set the net folder server's sync schedule
		if ( scheduleInfo != null )
		{
			scheduleInfo.setFolderId( rdConfig.getId() );
			resourceDriverModule.setSynchronizationSchedule( scheduleInfo, rdConfig.getId() );
		}

		return rdConfig;
	}

	/**
	 * Delete the given net folder
	 */
	public static void deleteNetFolder(
		NetFolderModule netFolderModule,
		Long id,
		boolean deleteSource )
	{
		netFolderModule.deleteNetFolder( id, deleteSource );
	}
	
	/**
	 * See if a "home directory" net folder exists with the given rootName and path for the given user.
	 */
	@SuppressWarnings("unchecked")
	public static Binder findHomeDirNetFolder(
		BinderModule binderModule,
		Long workspaceId )
	{
		Binder binder;
		List<Binder> childBinders;
		
		if ( workspaceId == null )
			return null;
		
		//~JW:  Ask Dennis how to do a search so I don't have to read the list of binders.
		//~JW:  Maybe it is OK to enumerate through the list of binders.
		
		binder = binderModule.getBinder( workspaceId );
		childBinders = binder.getBinders();
		if ( childBinders != null )
		{
			for ( Binder nextBinder : childBinders )
			{
				if ( nextBinder.isMirrored() && nextBinder.isHomeDir() )
					return nextBinder;
			}
		}
		
		// If we get here we did not find a net folder.
		return null;
	}

	/**
	 * 
	 */
	public static ResourceDriverConfig findNetFolderRootById(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		String id )
	{
		List<ResourceDriverConfig> drivers;

		if ( id == null )
			return null;
		
		// Get a list of the currently defined Net Folder Roots
		drivers = resourceDriverModule.getAllNetFolderResourceDriverConfigs();
		
		return findNetFolderRootById( drivers, id );
	}

	/**
	 * 
	 */
	public static ResourceDriverConfig findNetFolderRootById(
		List<ResourceDriverConfig> drivers,
		String id )
	{
		if ( id == null )
			return null;
		
		if ( drivers == null )
			return null;
		
		for ( ResourceDriverConfig driver : drivers )
		{
			String driverId;
			
			driverId = String.valueOf( driver.getId() );
			if ( id.equalsIgnoreCase( driverId ) )
				return driver;
		}
		
		// If we get here we did not find a net folder root with the given id.
		return null;
	}

    public static ResourceDriverConfig findNetFolderRootByHostAndVolume(
            AdminModule adminModule,
            ResourceDriverModule resourceDriverModule,
            String hostName,
            String hostAddress,
            String volume)
    {
        ResourceDriverConfig config = findNetFolderRootByUNC(adminModule, resourceDriverModule, "\\\\" + hostName + "\\" + volume);
        if (config==null) {
            config = findNetFolderRootByUNC(adminModule, resourceDriverModule, "\\\\" + hostAddress + "\\" + volume);
        }
        return config;
    }

	/**
	 * 
	 */
	public static ResourceDriverConfig findNetFolderRootByUNC(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		String serverUNC )
	{
		List<ResourceDriverConfig> drivers;

		if ( serverUNC == null )
			return null;
		
		// Get a list of the currently defined Net Folder Roots
		drivers = resourceDriverModule.getAllNetFolderResourceDriverConfigs();
		for ( ResourceDriverConfig driver : drivers )
		{
			if ( serverUNC.equalsIgnoreCase( driver.getRootPath() ) )
				return driver;
		}
		
		// If we get here we did not find a net folder root with the given unc.
		return null;
	}
	
	/**
	 * 
	 */
	public static ResourceDriverConfig findNetFolderRootByName(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		String name )
	{
		List<ResourceDriverConfig> drivers;

		if ( name == null )
			return null;
		
		// Get a list of the currently defined Net Folder Roots
		drivers = resourceDriverModule.getAllNetFolderResourceDriverConfigs();
		for ( ResourceDriverConfig driver : drivers )
		{
			if ( name.equalsIgnoreCase( driver.getName() ) )
				return driver;
		}
		
		// If we get here we did not find a net folder root with the given name.
		return null;
	}

	/**
	 * 
	 */
	private static CoreDao getCoreDao()
	{
		return (CoreDao) SpringContextUtil.getBean( "coreDao" );
	}
	

	/**
	 * 
	 */
	private static FolderDao getFolderDao()
	{
		return (FolderDao) SpringContextUtil.getBean( "folderDao" );
	}
	
	/**
	 * 
	 */
	public static long getDefaultJitsAclMaxAge()
	{
		return SPropsUtil.getLong( "nf.jits.acl.max.age", 3600000L );
	}
	
	/**
	 * 
	 */
	public static long getDefaultJitsResultsMaxAge()
	{
		return SPropsUtil.getLong( "nf.jits.max.age", 60000L );
	}
	
	/**
	 * Return all the net folders that are associated with the given net folder server
	 */
	public static List<Long> getAllNetFolders(
		BinderModule binderModule,
		WorkspaceModule workspaceModule,
		NetFolderSelectSpec selectSpec )
	{
		List<NetFolderConfig> results;
		List<Long> listOfNetFolderConfigIds;
		Workspace zone;
		Long zoneId;
		
		zone = RequestContextHolder.getRequestContext().getZone();
		zoneId = zone.getId();
		
		listOfNetFolderConfigIds = new ArrayList<Long>();
		
		// Get the list of net folders for the given criteria.
		results = getFolderDao().findNetFolderConfigs( selectSpec, zoneId );
		
		if ( results != null )
		{
			// We only want to return top-level net folders.
			for ( NetFolderConfig nextFolderConfig: results )
			{
				listOfNetFolderConfigIds.add( nextFolderConfig.getId() );
			}
		}

		return listOfNetFolderConfigIds;
	}
	

	/**
	 * Return all the net folders that are associated with the given net folder server
	 */
	public static List<NetFolderConfig> getAllNetFolders2(
		BinderModule binderModule,
		WorkspaceModule workspaceModule,
		NetFolderSelectSpec selectSpec )
	{
		List<NetFolderConfig> results;
		Workspace zone;
		Long zoneId;
		
		zone = RequestContextHolder.getRequestContext().getZone();
		zoneId = zone.getId();
		
		// Get the list of net folders for the given criteria.
		results = getFolderDao().findNetFolderConfigs( selectSpec, zoneId );
		
		return results;
	}
	
	/**
	 * Return the number of net folders that match the given criteria
	 */
	public static int getNumberOfNetFolders(
		BinderModule binderModule,
		WorkspaceModule workspaceModule,
		NetFolderSelectSpec selectSpec )
	{
		int count;
		Workspace zone;
		Long zoneId;
		
		zone = RequestContextHolder.getRequestContext().getZone();
		zoneId = zone.getId();
		
		// Get the number of net folders for the given criteria.
		count = getFolderDao().getNumberOfNetFolders( selectSpec, zoneId );
		
		return count;
	}
	

	/**
	 * Determine if the given net folder server is fully configured
	 */
	private static boolean isNetFolderServerConfigured( ResourceDriverConfig rdConfig )
	{
		String path;
		String name;
		String pwd;
		
		if ( rdConfig == null )
			return false;
		
		// Is the driver configured
		path = rdConfig.getRootPath();
		name = rdConfig.getAccountName();
		pwd = rdConfig.getPassword();
		if ( path != null && path.length() > 0 &&
			 name != null && name.length() > 0 &&
			 pwd != null && pwd.length() > 0 )
		{
			// Yes
			return true;
		}
		
		// If we get here the net folder server is not fully configured
		return false;
	}

	/**
	 * 
	 */
	public static void modifyNetFolder(
		BinderModule binderModule,
		FolderModule folderModule,
		NetFolderModule netFolderModule,
		Long id,
		String netFolderName,
		String netFolderRootName,
		String relativePath,
		ScheduleInfo scheduleInfo,
		SyncScheduleOption syncScheduleOption,
		boolean indexContent,
		Boolean inheritIndexContent,
		Boolean fullSyncDirOnly,
		Boolean allowDesktopAppToTriggerSync,
		Boolean inheritAllowDesktopAppToTriggerSync ) throws AccessControlException, WriteFilesException, WriteEntryDataException
	{
		Binder binder = binderModule.getBinder( id );
		NetFolderConfig nfc = binder.getNetFolderConfig();
		nfc.setName(netFolderName);
		ResourceDriverConfig rdc = NetFolderUtil.getNetFolderServerByName(netFolderRootName);
		nfc.setNetFolderServerId(rdc.getId());
		nfc.setResourcePath(relativePath);
		nfc.setIndexContent(indexContent);
		nfc.setUseInheritedIndexContent(inheritIndexContent);
		nfc.setSyncScheduleOption(syncScheduleOption);
		nfc.setFullSyncDirOnly(fullSyncDirOnly);
		nfc.setAllowDesktopAppToTriggerInitialHomeFolderSync(allowDesktopAppToTriggerSync);
		nfc.setUseInheritedDesktopAppTriggerSetting(inheritAllowDesktopAppToTriggerSync);
		
		netFolderModule.modifyNetFolder(nfc);

		// Set the net folder's sync schedule
		if ( scheduleInfo != null )
		{
			// If the sync schedule option is to use the net folder server's schedule then
			// disable the schedule on the net folder.
			if ( syncScheduleOption == SyncScheduleOption.useNetFolderServerSchedule )
				scheduleInfo.setEnabled( false );
			
			scheduleInfo.setFolderId( id );
			folderModule.setSynchronizationSchedule( scheduleInfo, id );
		}
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static ResourceDriverConfig modifyNetFolderRoot(
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		final ProfileModule profileModule,
		BinderModule binderModule,
		WorkspaceModule workspaceModule,
		FolderModule folderModule,
		String rootName,
		String rootPath,
		String proxyName,
		String proxyPwd,
		boolean useProxyIdentity,
		Long proxyIdentityId,
		DriverType driverType,
		String hostUrl,
		boolean allowSelfSignedCerts,
		boolean isSharePointServer,
		Set<Long> listOfPrincipals,
		Boolean fullSyncDirOnly,
		AuthenticationType authType,
		Boolean indexContent,
		Boolean enableJits,
		Long jitsResultsMaxAge,
		Long jitsAclMaxAge,
		Boolean allowDesktopAppToTriggerInitialHomeFolderSync,
		ScheduleInfo scheduleInfo )
	{
		Map options;
		User adminUser;
		String adminUserName;
		String zoneName;
		String xmlStr;
		UserProperties userProperties;
		FilrAdminTasks filrAdminTasks;
		ResourceDriverConfig rdConfig;
		boolean isConfigured1;
		boolean isConfigured2;
		boolean reIndexNeeded = false;
		Boolean indexContentOld;

		adminModule.checkAccess( AdminOperation.manageResourceDrivers );

		// Is the driver configured
		rdConfig = ResourceDriverManagerUtil.getResourceDriverManager().getDriverConfig( rootName );
		isConfigured1 = isNetFolderServerConfigured( rdConfig );
		indexContentOld = rdConfig.getIndexContent();
		
		options = new HashMap();
		options.put( ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE );
		options.put( ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, proxyName ); 
		options.put( ObjectKeys.RESOURCE_DRIVER_PASSWORD, proxyPwd );
		options.put( ObjectKeys.RESOURCE_DRIVER_USE_PROXY_IDENTITY, ( useProxyIdentity ? Boolean.TRUE : null ));
		options.put( ObjectKeys.RESOURCE_DRIVER_PROXY_IDENTITY_ID, proxyIdentityId );
		options.put( ObjectKeys.RESOURCE_DRIVER_FULL_SYNC_DIR_ONLY, fullSyncDirOnly );
		options.put( ObjectKeys.RESOURCE_DRIVER_AUTHENTICATION_TYPE, authType );
		options.put( ObjectKeys.RESOURCE_DRIVER_INDEX_CONTENT, indexContent );
		options.put( ObjectKeys.RESOURCE_DRIVER_JITS_ENABLED, enableJits );
		options.put( ObjectKeys.RESOURCE_DRIVER_JITS_RESULTS_MAX_AGE, jitsResultsMaxAge );
		options.put( ObjectKeys.RESOURCE_DRIVER_JITS_ACL_MAX_AGE, jitsAclMaxAge );
		options.put( ObjectKeys.RESOURCE_DRIVER_ALLOW_DESKTOP_APP_TO_TRIGGER_HOME_FOLDER_SYNC, allowDesktopAppToTriggerInitialHomeFolderSync );

		// Always prevent the top level folder from being deleted
		// This is forced so that the folder could not accidentally be deleted if the 
		// external disk was offline
		options.put( ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE );

		// Is the root type WebDAV?
		if ( driverType == DriverType.webdav )
		{
			// Yes, get the WebDAV specific values
			options.put(
					ObjectKeys.RESOURCE_DRIVER_HOST_URL,
					hostUrl );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE,
					allowSelfSignedCerts );
			options.put(
					ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH,
					isSharePointServer );
		}

		// Modify the resource driver
		rdConfig = resourceDriverModule.modifyResourceDriver(
															rootName,
															driverType, 
															rootPath,
															listOfPrincipals,
															options );

		// Set the net folder server's sync schedule
		if ( scheduleInfo != null )
		{
			scheduleInfo.setFolderId( rdConfig.getId() );
			resourceDriverModule.setSynchronizationSchedule( scheduleInfo, rdConfig.getId() );
		}

		// Is the configuration complete?
		rdConfig = ResourceDriverManagerUtil.getResourceDriverManager().getDriverConfig( rootName );
		isConfigured2 = isNetFolderServerConfigured( rdConfig );

		// Update the Filr admin tasks
		{
			// Get the admin user so we can remove an administrative task to his user properties.
			zoneName = RequestContextHolder.getRequestContext().getZoneName();
			adminUserName = SZoneConfig.getAdminUserName( zoneName );
			adminUser = profileModule.getUser( adminUserName );
			
			// Get the FilrAdminTasks from the administrator's user properties
			userProperties = profileModule.getUserProperties( adminUser.getId() );
			xmlStr = (String)userProperties.getProperty( ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS );
			filrAdminTasks = new FilrAdminTasks( xmlStr );
			
			// Is the net folder server configured?
			if ( isConfigured2 )
			{
				// Yes
				// Remove the task for the administrator to enter the proxy credentials for this net folder server.
				filrAdminTasks.deleteEnterNetFolderServerProxyCredentialsTask( rdConfig.getId() );
			}
			else
			{
				// No
				// Add a task for the administrator to configure the net folder server.
				filrAdminTasks.addEnterNetFolderServerProxyCredentialsTask( rdConfig.getId() );
			}
			
			// Save the FilrAdminTasks to the administrator's user properties
			{
				RunasCallback callback;
				final String tmpXmlStr;
				final Long adminId;
				
				tmpXmlStr = filrAdminTasks.toString();
				adminId = adminUser.getId();
				
				callback = new RunasCallback()
				{
					@Override
					public Object doAs()
					{
						profileModule.setUserProperty(
													adminId,
													ObjectKeys.USER_PROPERTY_FILR_ADMIN_TASKS,
													tmpXmlStr );
						return null;
					}
				};
				RunasTemplate.runasAdmin( callback, RequestContextHolder.getRequestContext().getZoneName() );
			}
		}
		
		if ( isConfigured1 != isConfigured2 )
			reIndexNeeded = true;
		
		// Re-index all of the net folders that reference this net folder server.  We need to do
		// this so "home directory" net folders will show up in "My files" after the net folder server
		// has been configured fully.
		// We only need to re-index if the configuration completeness changed.
		if ( reIndexNeeded )
		{
			List<Long> listOfNetFolderIds;
			NetFolderSelectSpec selectSpec;

			// Find all of the net folders that reference this net folder server.
			selectSpec = new NetFolderSelectSpec();
			selectSpec.setRootId( rdConfig.getId() );
			selectSpec.setIncludeHomeDirNetFolders( true );
			selectSpec.setFilter( null );
			listOfNetFolderIds = NetFolderHelper.getAllNetFolders(
														binderModule,
														workspaceModule,
														selectSpec );

			if ( listOfNetFolderIds != null )
			{
				for ( Long binderId:  listOfNetFolderIds )
				{
					// Index this binder.
					binderModule.indexBinder( binderId, false );
				}
			}
		}
		
		// If the file content indexing flag has changed on the net folder server,
		// we may need to create new background jobs for member net folders.
		if(Boolean.TRUE.equals(indexContent)) {
			if(Boolean.TRUE.equals(indexContentOld)) {
				// This setting hasn't changed. No adjustment to make.
			}
			else {
				// The file content indexing was not on previously, but is on now.
				// This affects all member net folders that inherit this setting from the parent net folder server.
				List<NetFolderConfig> listOfNetFolders;
				NetFolderSelectSpec selectSpec;

				// Find all of the net folders that reference this net folder server.
				selectSpec = new NetFolderSelectSpec();
				selectSpec.setRootId( rdConfig.getId() );
				selectSpec.setIncludeHomeDirNetFolders( true );
				selectSpec.setFilter( null );
				listOfNetFolders = NetFolderHelper.getAllNetFolders2(
															binderModule,
															workspaceModule,
															selectSpec );

				if ( listOfNetFolders != null )
				{
					for ( NetFolderConfig netFolder:  listOfNetFolders )
					{
						if(netFolder.getUseInheritedIndexContent()) {
							// This net folder inherits file content indexing setting from the net folder server.
							// Make sure that a job exists for this net folder.
							try {
								folderModule.netFolderContentIndexingJobSchedule(netFolder.getTopFolderId());
							}
							catch(Exception e) {
								m_logger.error("Error scheduling file content indexing job for net folder " + netFolder.getId(), e);
								continue; // continue to the next net folder
							}
						}
					}
				}
			}
		}
		else {
			// File content indexing is either off or unspecified (which is equivalent to off)
			// on this net folder server. This affects all member net folders that inherit
			// this setting from the parent net folder server. However, we will not try to
			// unschedule jobs for those affected net folders at this point, since it has
			// the danger of failing because some of the jobs may be running currently.
			// Instead, those jobs will voluntarily check appropriate settings at the next run
			// and self-destruct them as necessary.
		}
		
		return rdConfig;
	}

	/**
	 * Save the JITS settings for the given net folder binder.
	 * 
	 * @param netFolderModule
	 * @param nfc
	 * @param inheritJitsSettings
	 * @param jitsEnabled
	 * @param aclmaxAge
	 * @param resultsMaxAge
	 */
	public static void saveJitsSettings(
			NetFolderModule netFolderModule,
			NetFolderConfig nfc,
			boolean inheritJitsSettings,
			boolean jitsEnabled,
			long aclMaxAge,
			long resultsMaxAge )
		{
			nfc.setUseInheritedJitsSettings(inheritJitsSettings);
			nfc.setJitsEnabled(jitsEnabled);
			nfc.setJitsAclMaxAge(aclMaxAge);
			nfc.setJitsMaxAge(resultsMaxAge);

			try
			{
				netFolderModule.modifyNetFolder(nfc);
			}
			catch ( Exception ex )
			{
				m_logger.error( "In saveJitsSettings(), call to modifyBinder() failed. " + ex.toString() );
			}
		}
	

	// This method is moved from FolderModule which is transactional proxy that automatically defines
	// read transaction around this method boundary, which can cause deadlock situation as the internal
	// quartz code attempts to obtain database connection under extremely loaded situation.
	public static ScheduleInfo getMirroredFolderSynchronizationSchedule(Long folderId) {
  		return getMirroredFolderSynchronizationScheduleObject().getScheduleInfo(folderId);
	}
	
	public static MirroredFolderSynchronization getMirroredFolderSynchronizationScheduleObject() {
		String className = SPropsUtil.getString("job.mirrored.folder.synchronization.class", "org.kablink.teaming.jobs.DefaultMirroredFolderSynchronization");
		return (MirroredFolderSynchronization)ReflectHelper.getInstance(className);
    }    

	/**
	 * Get the sync schedule for the given driver.
	 */
	// This method is moved from ResourceDriverModule which is transactional proxy that automatically 
	// defines read transaction around this method boundary, which can cause deadlock situation as the 
	// internal quartz code attempts to obtain database connection under extremely loaded situation.
	public static ScheduleInfo getNetFolderServerSynchronizationSchedule( Long driverId )
	{
  		return getNetFolderServerSynchronizationScheduleObject().getScheduleInfo( driverId );
	}

	public static NetFolderServerSynchronization getNetFolderServerSynchronizationScheduleObject() 
	{
		String className = SPropsUtil.getString("job.net.folder.server.synchronization.class", "org.kablink.teaming.jobs.DefaultNetFolderServerSynchronization");

		return (NetFolderServerSynchronization)ReflectHelper.getInstance(className);
    }
	
    public static List<AssignedRole> getNetFolderRights(
            AllModulesInjected ami,
            Binder binder )
    {
        List<AssignedRole.RoleType> listOfRoles;
        listOfRoles = new ArrayList<AssignedRole.RoleType>();
        listOfRoles.add( AssignedRole.RoleType.AllowAccess );
        listOfRoles.add(AssignedRole.RoleType.ShareExternal);
        listOfRoles.add(AssignedRole.RoleType.ShareForward);
        listOfRoles.add(AssignedRole.RoleType.ShareInternal);
        listOfRoles.add( AssignedRole.RoleType.SharePublic );

        return AdminHelper.getAssignedRights(ami, binder, listOfRoles);
    }

    public static void setNetFolderRights(AllModulesInjected ami, Long binderId, List<AssignedRole> roles) {
        // Get the binder's work area
        List<AssignedRole.RoleType> roleTypes = new ArrayList<AssignedRole.RoleType>();
        for ( AssignedRole.RoleType role : AssignedRole.RoleType.values()) {
            if (role.isApplicableToNetFolders()) {
                roleTypes.add(role);
            }
        }
        Binder binder = ami.getBinderModule().getBinder( binderId );
        AdminHelper.setAssignedRights(ami, binder, roleTypes, roles);
    }

    /**
     * 
     */
    private static ConnectionTestStatus testNetFolderConnectionForHomeDirCreation(
		String driverName,
		DriverType driverType,
		String rootPath,
		String subPath,
		String proxyName,
		String proxyPwd )
    {
    	boolean doTestConnection;
    	ConnectionTestStatus status;
    	
		m_logger.info( "In testNetFolderConnectionForHomeDirCreation() rootPath: " + rootPath + " subPath: " + subPath );
		
		doTestConnection = SPropsUtil.getBoolean( "test.connection.on.homedir.creation", true );
		
		// Test the connection
		if ( doTestConnection )
		{
			status = testNetFolderConnection(
											driverName,
											driverType,
											rootPath,
											subPath,
											proxyName,
											proxyPwd );
		}
		else
		{
			// Pretend the connection was ok
			status = new ConnectionTestStatus( ConnectionTestStatusCode.NORMAL, "artificial result" );
		}
    	
		return status;
    }
    
    /**
     * 
     */
    public static ConnectionTestStatus testNetFolderConnection(
		String driverName,
		DriverType driverType,
		String rootPath,
		String subPath,
		String proxyName,
		String proxyPwd )
    {
    	ConnectionTestStatus status = null;
		String name;
		ResourceDriverConfig rdConfig = null;
		ResourceDriver resourceDriver;
		ResourceDriverManager rdManager;
    	
		name = driverName;
		if ( name != null )
			name = "test-connection-" + name + "test-connection";
		else
			name = "test-connection-net-folder-root-test-connection";
		
		rdConfig = new ResourceDriverConfig();
		rdConfig.setName( name );
		rdConfig.setDriverType( driverType );
		rdConfig.setZoneId( RequestContextHolder.getRequestContext().getZoneId() );
		rdConfig.setRootPath( rootPath );
   		rdConfig.setReadOnly( false );
   		rdConfig.setSynchTopDelete( false );
   		rdConfig.setAccountName( proxyName );
   		rdConfig.setPassword( proxyPwd );
		
   		rdManager = ResourceDriverManagerUtil.getResourceDriverManager();
		// Do not call initialize() method on the driver when we create a temporary one
		// just for the purpose of testing a connection. Specifically, if we call initialize()
		// on a FAMT resource driver, it may trigger building a rights cache which can take
		// significant time and system resources which we do not need for this test.
   		resourceDriver = rdManager.createResourceDriverWithoutInitialization( rdConfig );
   		if ( resourceDriver != null && resourceDriver instanceof AclResourceDriver )
   		{
   			AclResourceDriver aclDriver;
   			
   			aclDriver = (AclResourceDriver) resourceDriver;
   			subPath = aclDriver.normalizedResourcePath( subPath );
   			status = aclDriver.testConnection(
		   								proxyName,
		   								proxyPwd,
		   								subPath );

   			// Do not call shutdown() on this temporary driver instance, since we don't call initialize() on it.
   			// Otherwise, the ref count FAMT maintains can go incorrect.
   		}

   		return status;
    }
}
