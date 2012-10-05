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
package org.kablink.teaming.web.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ldap.impl.LdapModuleImpl.HomeDirInfo;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.Utils;

/**
 * Helper class dealing with net folders and net folder roots
 * @author jwootton
 */
public class NetFolderHelper
{
	protected static Log m_logger = LogFactory.getLog( NetFolderHelper.class );

	/**
	 * Create a net folder and if needed a net folder root for the given home directory information
	 */
	public static void createHomeDirNetFolder(
		ProfileModule profileModule,
		TemplateModule templateModule,
		BinderModule binderModule,
		FolderModule folderModule,
		AdminModule adminModule,
		ResourceDriverModule resourceDriverModule,
		HomeDirInfo homeDirInfo,
		User user ) throws WriteFilesException, WriteEntryDataException
	{
		Long workspaceId;
		String serverAddr = null;
		String volume = null;
		String path = null;
		String serverUNC;
		ResourceDriverConfig rdConfig;		

		// Are we running Filr?
		if ( Utils.checkIfFilr() == false )
		{
			// No
			return;
		}
		
		if ( homeDirInfo != null )
		{
			serverAddr = homeDirInfo.getServerAddr();
			volume = homeDirInfo.getVolume();
			path = homeDirInfo.getPath();
		}
		
		// Do we have all the information we need?
		if ( serverAddr == null || serverAddr.length() == 0 ||
			 volume == null || volume.length() == 0 ||
			 path == null || path.length() == 0 )
		{
			// No
			m_logger.error( "In NetFolderHelper.createHomeDirNetFolder(), invalid server information" );
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
		
		// Does a net folder root exists with a unc path to the given server address / volume?
		serverUNC = "\\\\" + serverAddr + "\\" + volume;
		rdConfig = findNetFolderRootByUNC( adminModule, resourceDriverModule, serverUNC );
		if ( rdConfig == null )
		{
			String rootName;
			
			// No
			// Create a net folder root.  The administrator will need to fill in credentials.
			rootName = serverAddr + "-" + volume;
			m_logger.info( "About to create a net folder server called: " + rootName  );
			
			rdConfig = NetFolderHelper.createNetFolderRoot(
														adminModule,
														resourceDriverModule,
														rootName,
														serverUNC,
														DriverType.famt,
														null,
														null,
														null,
														null,
														false,
														false );
		}
		
		if ( rdConfig != null )
		{
			Binder netFolderBinder;
			
			// Does a net folder already exist for this user's home directory
			netFolderBinder = NetFolderHelper.findNetFolder( binderModule, user, rdConfig.getName(), path );
			if ( netFolderBinder == null )
			{
				String folderName;
	
				folderName = NLT.get( "netfolder.default.homedir.name" );
				m_logger.info( "About to create a net folder called: " + folderName + ", for the users home directory for user: " + user.getName() );
				
				// Create a net folder in the user's workspace
				NetFolderHelper.createNetFolder(
											templateModule,
											binderModule,
											folderModule,
											folderName,
											rdConfig.getName(),
											path,
											null,
											workspaceId,
											true );
			}
		}
	}
	
	/**
	 * Create a net folder from the given data
	 */
	@SuppressWarnings({ "unchecked" })
	public static Binder createNetFolder(
		TemplateModule templateModule,
		BinderModule binderModule,
		FolderModule folderModule,
		String name,
		String rootName,
		String path,
		ScheduleInfo scheduleInfo,
		Long parentBinderId,
		boolean isHomeDir ) throws WriteFilesException, WriteEntryDataException
	{
		Binder binder = null;
		Long templateId = null;
		List<TemplateBinder> listOfTemplateBinders;
		
		// Find the template binder for mirrored folders.
		listOfTemplateBinders = templateModule.getTemplates( Boolean.TRUE );
		if ( listOfTemplateBinders != null )
		{
			for ( TemplateBinder nextTemplateBinder : listOfTemplateBinders )
			{
				String internalId;
				
				internalId = nextTemplateBinder.getInternalId();
				if ( internalId != null && internalId.equalsIgnoreCase( ObjectKeys.DEFAULT_FOLDER_FILR_ROOT_CONFIG ) )
				{
					templateId = nextTemplateBinder.getId();
					break;
				}
			}
		}

		if ( templateId != null )
		{
			// Create the binder
			binder = templateModule.addBinder(
											templateId,
											parentBinderId,
											name,
											name );
			
			// Modify the binder with the additional net folder information.
			{
				Set deleteAtts;
				Map fileMap = null;
				MapInputData mid;
   				Map formData = null;
				
				deleteAtts = new HashSet();
				fileMap = new HashMap();
   				formData = new HashMap();
		   		formData.put( ObjectKeys.FIELD_BINDER_LIBRARY, "true" );
		   		formData.put( ObjectKeys.FIELD_BINDER_MIRRORED, "true" );
		   		formData.put( ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, rootName );
		   		formData.put( ObjectKeys.FIELD_BINDER_RESOURCE_PATH, path );
		   		formData.put( ObjectKeys.FIELD_IS_HOME_DIR, Boolean.toString( isHomeDir ) );
   				mid = new MapInputData( formData );

	   			binderModule.modifyBinder( binder.getId(), mid, fileMap, deleteAtts, null );				
			}
			
			// Set the net folder's sync schedule
			if ( scheduleInfo != null )
			{
				scheduleInfo.setFolderId( binder.getId() );
				folderModule.setSynchronizationSchedule( scheduleInfo, binder.getId() );
			}
		}
		else
			m_logger.error( "Could not find the template binder for a mirrored folder" );
		
		return binder;
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
		Set<Long> memberIds,
		String hostUrl,
		boolean allowSelfSignedCerts,
		boolean isSharePointServer )
	{
		Map options;
		ResourceDriverConfig rdConfig = null;
		
		adminModule.checkAccess( AdminOperation.manageResourceDrivers );

		options = new HashMap();
		options.put( ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE );
		options.put( ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME, proxyName ); 
		options.put( ObjectKeys.RESOURCE_DRIVER_PASSWORD, proxyPwd );
		
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
		{
			rdConfig = resourceDriverModule.addResourceDriver(
															name,
															driverType, 
															path,
															memberIds,
															options );
		}
		
		return rdConfig;
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static Binder findNetFolder(
		BinderModule binderModule,
		User user,
		String rootName,
		String path )
	{
		Binder binder;
		List<Binder> childBinders;
		
		//!!! Finish
		if ( user != null )
			return null;
		
		binder = binderModule.getBinder( user.getWorkspaceId() );
		childBinders = binder.getBinders();
		if ( childBinders != null )
		{
			for ( Binder nextBinder : childBinders )
			{
				nextBinder.getCreatedWithDefinitionId();
			}
		}
		
		// If we get here we did not find a net folder.
		return null;
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
		drivers = resourceDriverModule.getAllResourceDriverConfigs();
		for ( ResourceDriverConfig driver : drivers )
		{
			if ( serverUNC.equalsIgnoreCase( driver.getRootPath() ) )
				return driver;
		}
		
		// If we get here we did not find a net folder root with the given unc.
		return null;
	}
}
