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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.IllegalCharacterInNameException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.NetFolderSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderState;
import org.kablink.teaming.domain.BinderState.FullSyncStats;
import org.kablink.teaming.domain.BinderState.FullSyncStatus;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NetFolderConfig;
import org.kablink.teaming.domain.NetFolderConfig.SyncScheduleOption;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProxyIdentity;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.AuthenticationType;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.domain.TitleException;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver.ConnectionTestStatus;
import org.kablink.teaming.gwt.client.GwtGroup;
import org.kablink.teaming.gwt.client.GwtJitsNetFolderConfig;
import org.kablink.teaming.gwt.client.GwtNetFolderSyncScheduleConfig;
import org.kablink.teaming.gwt.client.GwtNetFolderSyncScheduleConfig.NetFolderSyncScheduleOption;
import org.kablink.teaming.gwt.client.GwtProxyIdentity;
import org.kablink.teaming.gwt.client.GwtRole;
import org.kablink.teaming.gwt.client.GwtSchedule;
import org.kablink.teaming.gwt.client.GwtRole.GwtRoleType;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.NetFolder;
import org.kablink.teaming.gwt.client.NetFolder.NetFolderSyncStatus;
import org.kablink.teaming.gwt.client.NetFolderDataSyncSettings;
import org.kablink.teaming.gwt.client.NetFolderRoot;
import org.kablink.teaming.gwt.client.NetFolderRoot.GwtAuthenticationType;
import org.kablink.teaming.gwt.client.NetFolderRoot.NetFolderRootStatus;
import org.kablink.teaming.gwt.client.NetFolderSyncStatistics;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFolderResult;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFolderResult.DeleteNetFolderStatus;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFolderRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteNetFolderServersRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionResponse;
import org.kablink.teaming.gwt.client.rpc.shared.TestNetFolderConnectionResponse.GwtConnectionTestStatusCode;
import org.kablink.teaming.gwt.client.widgets.ModifyNetFolderRootDlg.NetFolderRootType;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.folder.CannotDeleteSyncingNetFolderException;
import org.kablink.teaming.module.netfolder.NetFolderUtil;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.NetFolderHelper;

/**
 * Helper methods for the GWT UI server code that services requests
 * dealing with Net Folder Roots and Net Folders.
 *
 * @author drfoster@novell.com
 */
public class GwtNetFolderHelper {
	protected static Log m_logger = LogFactory.getLog(GwtNetFolderHelper.class);

	
	/**
	 * Check the status of each net folder in the list.
	 */
	public static Set<NetFolder> checkNetFoldersStatus(
		AllModulesInjected ami,
		HttpServletRequest req,
		Set<NetFolder> netFolders ) throws GwtTeamingException
	{
		for ( NetFolder nextNetFolder : netFolders )
		{
			nextNetFolder.setStatus( getNetFolderSyncStatus( nextNetFolder.getId() ) );
		}
		
		return netFolders;
	}
	
	/**
	 * Check the status of each net folder root in the list.
	 */
	public static Set<NetFolderRoot> checkNetFolderServerStatus(
		AllModulesInjected ami,
		HttpServletRequest req,
		Set<NetFolderRoot> netFolderServers ) throws GwtTeamingException
	{
		for ( NetFolderRoot nextNetFolderServer : netFolderServers )
		{
			String statusTicketId;

			statusTicketId = nextNetFolderServer.getStatusTicketId();
			if ( statusTicketId != null )
			{
				StatusTicket statusTicket;
				
				statusTicket = GwtWebStatusTicket.findStatusTicket( statusTicketId, req );
				if ( statusTicket != null )
				{
					if ( statusTicket.isDone() )
						nextNetFolderServer.setStatus( NetFolderRootStatus.READY );
					
					// Currently, when we sync a net folder server, we add all of the net folders
					// associated with the net folder server to a queue of net folders waiting to
					// be sync'd.  Given this fact, there isn't really a "status" of on a
					// net folder server.
					nextNetFolderServer.setStatus( NetFolderRootStatus.READY );
				}
				else
				{
					nextNetFolderServer.setStatus( NetFolderRootStatus.READY );
				}
			}
		}
		
		return netFolderServers;
	}
	
	/**
	 * Create a net folder in the default location from the given data
	 */
	public static NetFolder createNetFolder(
		AllModulesInjected ami,
		NetFolder netFolder ) throws GwtTeamingException
	{
		Binder parentBinder;
		NetFolder newNetFolder = null;
		
		// Get the binder where all net folders are created
		try
		{
			NetFolderConfig nfc;
			ScheduleInfo scheduleInfo = null;
			SyncScheduleOption syncScheduleOption = null;
			NetFolderDataSyncSettings dataSyncSettings;
			
			// Create the net folder in the global "net folder roots" workspace.
			parentBinder = getCoreDao().loadReservedBinder(
													ObjectKeys.NET_FOLDERS_ROOT_INTERNALID, 
													RequestContextHolder.getRequestContext().getZoneId() );

			dataSyncSettings = netFolder.getDataSyncSettings();
			
			// Get the schedule information
			{
				GwtNetFolderSyncScheduleConfig config;
				
				config = netFolder.getSyncScheduleConfig();
				if ( config != null )
				{
					scheduleInfo = GwtServerHelper.getScheduleInfoFromGwtSchedule( config.getSyncSchedule() );
					
					syncScheduleOption = getSyncScheduleOptionFromGwtSyncScheduleOption( config.getSyncScheduleOption() );
				}
			}
			
			nfc = NetFolderHelper.createNetFolder(
												ami.getTemplateModule(),
												ami.getBinderModule(),
												ami.getFolderModule(),
												ami.getNetFolderModule(),
												ami.getAdminModule(),
												GwtServerHelper.getCurrentUser(),
												netFolder.getName(),
												netFolder.getNetFolderRootName(),
												netFolder.getRelativePath(),
												scheduleInfo,
												syncScheduleOption,
												parentBinder.getId(),
												false,
												netFolder.getIndexContent(),
												netFolder.getInheritIndexContentSetting(),
												netFolder.getFullSyncDirOnly(),
												dataSyncSettings.getAllowDesktopAppToTriggerSync(),
												dataSyncSettings.getInheritAllowDesktopAppToTriggerSync() );
			
			// Set the rights on the net folder
			setNetFolderRights( ami, nfc.getTopFolderId(), netFolder.getRoles() );
			
			// Set the data sync settings on the net folder
			saveDataSyncSettings( ami, nfc, dataSyncSettings );
			
			// Save the jits settings
			{
				GwtJitsNetFolderConfig settings;
				
				settings = netFolder.getJitsConfig();
				if ( settings != null )
				{
					NetFolderHelper.saveJitsSettings(
												ami.getNetFolderModule(),
												nfc,
												netFolder.getInheritJitsSettings(),
												settings.getJitsEnabled(),
												settings.getAclMaxAge(),
												settings.getResultsMaxAge() );
				}
			}

			newNetFolder = new NetFolder();
			newNetFolder.setName( netFolder.getName() );
			newNetFolder.setDisplayName( netFolder.getName() );
			newNetFolder.setNetFolderRootName( netFolder.getNetFolderRootName() );
			newNetFolder.setRelativePath( netFolder.getRelativePath() );
			newNetFolder.setId( nfc.getTopFolderId() );
			newNetFolder.setStatus( getNetFolderSyncStatus( newNetFolder.getId() ) );
			newNetFolder.setSyncScheduleConfig( netFolder.getSyncScheduleConfig() );
			newNetFolder.setRoles( netFolder.getRoles() );
			newNetFolder.setDataSyncSettings( netFolder.getDataSyncSettings() );
			newNetFolder.setJitsConfig( netFolder.getJitsConfig() );
			newNetFolder.setFullSyncDirOnly( netFolder.getFullSyncDirOnly() );
			newNetFolder.setIndexContent( netFolder.getIndexContent() );
			newNetFolder.setInheritIndexContentSetting( netFolder.getInheritIndexContentSetting() );
			newNetFolder.setInheritJitsSettings( netFolder.getInheritJitsSettings() );
			newNetFolder.setDataSyncSettings( dataSyncSettings );
		}
		catch ( Exception ex )
		{
			GwtTeamingException gtEx;
			
			gtEx = GwtLogHelper.getGwtClientException();
			
			if ( ex instanceof TitleException )
			{
				String[] args;
				
				args = new String[] { netFolder.getName() };
				gtEx.setAdditionalDetails( NLT.get( "netfolder.duplicate.name", args ) );
			}
			else if ( ex instanceof IllegalCharacterInNameException )
			{
				gtEx = GwtLogHelper.getGwtClientException();
				gtEx.setAdditionalDetails( NLT.get( "netfolder.name.illegal.characters" ) );
			}
			else
			{
				gtEx.setAdditionalDetails( NLT.get( "netfolder.cant.load.parent.binder" ) );
			}
			
			GwtLogHelper.error(m_logger, "Error creating net folder: " + netFolder.getName(), ex);
			throw gtEx;				
		}
		
		return newNetFolder;
	}
	
	/**
	 * Create a new net folder root from the given data
	 */
	public static NetFolderRoot createNetFolderRoot(
		AllModulesInjected ami,
		NetFolderRoot netFolderRoot ) throws GwtTeamingException
	{
		ResourceDriverConfig rdConfig;
		DriverType driverType;
		NetFolderRoot newRoot = null;
		ScheduleInfo scheduleInfo;
		
		driverType = getDriverType( netFolderRoot.getRootType() );
		scheduleInfo = GwtServerHelper.getScheduleInfoFromGwtSchedule( netFolderRoot.getSyncSchedule() );
		try
		{
			AuthenticationType authType = null;
			
			authType = getAuthType( netFolderRoot.getAuthType() );
			
			GwtProxyIdentity pi = netFolderRoot.getProxyIdentity();
			Long proxyIdentityId = ((null == pi) ? null : pi.getId());
			rdConfig = NetFolderHelper.createNetFolderRoot(
													ami.getAdminModule(),
													ami.getResourceDriverModule(),
													netFolderRoot.getName(),
													netFolderRoot.getRootPath(),
													driverType,
													netFolderRoot.getProxyName(),
													netFolderRoot.getProxyPwd(),
													netFolderRoot.getUseProxyIdentity(),
													proxyIdentityId,
													netFolderRoot.getListOfPrincipalIds(),
													netFolderRoot.getHostUrl(),
													netFolderRoot.getAllowSelfSignedCerts(),
													netFolderRoot.getIsSharePointServer(),
													netFolderRoot.getFullSyncDirOnly(),
													authType,
													netFolderRoot.getIndexContent(),
													netFolderRoot.getJitsEnabled(),
													netFolderRoot.getJitsResultsMaxAge(),
													netFolderRoot.getJitsAclMaxAge(),
													netFolderRoot.getAllowDesktopAppToTriggerInitialHomeFolderSync(),
													scheduleInfo );
		}
		catch ( RDException ex )
		{
			GwtTeamingException gwtEx;
			
			gwtEx = GwtLogHelper.getGwtClientException( ex );
			throw gwtEx;
		}

		if ( rdConfig != null )
		{
			newRoot = new NetFolderRoot();
			newRoot.setRootType( getRootTypeFromDriverType( driverType ) );
			newRoot.setId(        rdConfig.getId()          );
			newRoot.setName(      rdConfig.getName()        );
			newRoot.setProxyName( rdConfig.getAccountName() );
			newRoot.setProxyPwd(  rdConfig.getPassword()    );
			
			newRoot.setUseProxyIdentity(rdConfig.getUseProxyIdentity());
			Long proxyIdentityId = rdConfig.getProxyIdentityId();
			if (null != proxyIdentityId) {
				ProxyIdentity pi = GwtProxyIdentityHelper.getProxyIdentity(ami, proxyIdentityId);
				if (null != pi) {
					newRoot.setProxyIdentity(GwtProxyIdentityHelper.convertPIToGwtPI(pi));
				}
			}
			
			newRoot.setRootPath( rdConfig.getRootPath() );
			newRoot.setHostUrl( rdConfig.getHostUrl() );
			newRoot.setAllowSelfSignedCerts( rdConfig.isAllowSelfSignedCertificate() );
			newRoot.setIsSharePointServer( rdConfig.isPutRequiresContentLength() );
			newRoot.setSyncSchedule( netFolderRoot.getSyncSchedule() );
			newRoot.setFullSyncDirOnly( rdConfig.getFullSyncDirOnly() );
			newRoot.setIndexContent( rdConfig.getIndexContent() );
			newRoot.setJitsEnabled( rdConfig.isJitsEnabled() );
			newRoot.setJitsResultsMaxAge( rdConfig.getJitsMaxAge() );
			newRoot.setJitsAclMaxAge( rdConfig.getJitsAclMaxAge() );
			
			AuthenticationType authType = rdConfig.getAuthenticationType();
			if (null != authType) {
				newRoot.setAuthType(GwtAuthenticationType.getType(authType.getValue()));
			}

			// Get the list of principals that can use the net folder root
			getListOfPrincipals( ami, rdConfig, newRoot );
		}
		
		return newRoot;
	}
	
	/**
	 * Delete the given list of net folders
	 */
	public static DeleteNetFolderRpcResponseData deleteNetFolders(
		AllModulesInjected ami,
		Set<NetFolder> netFolders )
	{
		DeleteNetFolderRpcResponseData results;
		
		results = new DeleteNetFolderRpcResponseData();
		
		for ( NetFolder nextNetFolder : netFolders )
		{
			DeleteNetFolderResult result;

			result = new DeleteNetFolderResult();

			try
			{
				NetFolderHelper.deleteNetFolder( ami.getNetFolderModule(), nextNetFolder.getId(), false );
				result.setStatus( DeleteNetFolderStatus.SUCCESS, null );
			}
			catch ( CannotDeleteSyncingNetFolderException nfEx )
			{
				result.setStatus(
							DeleteNetFolderStatus.DELETE_FAILED_SYNC_IN_PROGRESS,
							NLT.get( "netfolder.cant.delete.sync.in.progress" ) );
			}
			catch ( Exception e )
			{
				GwtLogHelper.error(m_logger, "Error deleting next net folder: " + nextNetFolder.getName(), e);
				result.setStatus(DeleteNetFolderStatus.DELETE_FAILED, e.toString());
			}
			
			results.addResult( nextNetFolder, result );
		}
		
		return results;
	}

	/**
	 * Delete the given list of net folder roots.
	 * 
	 * @param ami
	 * @param netFolderRoots
	 * 
	 * @return
	 */
	public static DeleteNetFolderServersRpcResponseData deleteNetFolderRoots(AllModulesInjected ami, Set<NetFolderRoot> netFolderRoots) {
		ami.getAdminModule().checkAccess(AdminOperation.manageResourceDrivers);

		DeleteNetFolderServersRpcResponseData result = new DeleteNetFolderServersRpcResponseData();
		ResourceDriverModule rdModule = ami.getResourceDriverModule();
		
		for (NetFolderRoot nextRoot:  netFolderRoots) {
			try {
				// Get a list of net folders that are referencing this
				// net folder server.
				NetFolderSelectSpec selectSpec = new NetFolderSelectSpec();
				selectSpec.setIncludeHomeDirNetFolders(true);
				selectSpec.setRootId(nextRoot.getId());
				List<NetFolder> listOfNetFolders = getAllNetFolders(ami, selectSpec, true);
				
				// Is this net folder server being referenced by a net folder?
				if (!(MiscUtil.hasItems(listOfNetFolders))) {
					// No, go ahead and delete it.
					rdModule.deleteResourceDriver(   nextRoot.getName());
					result.addDeletedNetFolderServer(nextRoot          );
				}
				else {
					// Yes, add it to the list of net folder servers
					// that can't be deleted.
					result.addCouldNotBeDeletedNetFolderServer(nextRoot);
				}
			}
			
			catch (Exception ex) {
				GwtLogHelper.error(m_logger, "Error deleting next folder root: " + nextRoot.getName(), ex);
				result.addCouldNotBeDeletedNetFolderServer(nextRoot);
			}
		}
		
		return result;
	}

	/**
	 * Return a list of all the net folders.
	 * 
	 * If selectSpec.m_includeHomeDirNetFolders is true we will include
	 * 'home directory' net folders in our list.
	 * 
	 * If selectSpec.m_rootid is not null, we will only return net
	 * folders associated with the given net folder root.
	 * 
	 * @param ami
	 * @param selectSpec
	 * @param getMinimalInfo
	 * 
	 * @return
	 */
	public static List<NetFolder> getAllNetFolders(AllModulesInjected ami, NetFolderSelectSpec selectSpec, boolean getMinimalInfo) {
		List<NetFolderConfig> listOfNetFolderConfig = NetFolderHelper.getAllNetFolders2(
			ami.getBinderModule(),
			ami.getWorkspaceModule(),
			selectSpec);

		ArrayList<NetFolder> listOfNetFolders = new ArrayList<NetFolder>();
		if (MiscUtil.hasItems(listOfNetFolderConfig)) {
			for (NetFolderConfig nextNetFolderConfig:  listOfNetFolderConfig) {
				NetFolder netFolder;
				if (getMinimalInfo)
				     netFolder = getNetFolderWithMinimalInfo(ami, nextNetFolderConfig                 );
				else netFolder = getNetFolder(               ami, nextNetFolderConfig.getTopFolderId());
				listOfNetFolders.add(netFolder);
			}
		}
		
		return listOfNetFolders;
	}
	
	/**
	 * Return a list of all the net folder roots
	 */
	public static List<NetFolderRoot> getAllNetFolderRoots( AllModulesInjected ami )
	{
		List<NetFolderRoot> listOfNetFolderRoots;
		AdminModule adminModule;
		
		listOfNetFolderRoots = new ArrayList<NetFolderRoot>();
		adminModule = ami.getAdminModule();
		
		if ( adminModule.testAccess( AdminOperation.manageResourceDrivers ) )
		{
			List<ResourceDriverConfig> drivers;

			//Get a list of the currently defined Net Folder Roots
			drivers = ami.getResourceDriverModule().getAllNetFolderResourceDriverConfigs();
			for ( ResourceDriverConfig driver : drivers )
			{
				NetFolderRoot nfRoot;
				DriverType driverType;
				GwtSchedule gwtSchedule;
				
				nfRoot = new NetFolderRoot();
				nfRoot.setId(   driver.getId()   );
				nfRoot.setName( driver.getName() );
				
				driverType = driver.getDriverType();
				nfRoot.setRootType( getRootTypeFromDriverType( driverType ) );
				
				nfRoot.setRootPath(  driver.getRootPath()    );
				nfRoot.setProxyName( driver.getAccountName() );
				nfRoot.setProxyPwd(  driver.getPassword()    );
				
				nfRoot.setUseProxyIdentity(driver.getUseProxyIdentity());
				Long proxyIdentityId = driver.getProxyIdentityId();
				if (null != proxyIdentityId) {
					ProxyIdentity pi = GwtProxyIdentityHelper.getProxyIdentity(ami, proxyIdentityId);
					if (null != pi) {
						nfRoot.setProxyIdentity(GwtProxyIdentityHelper.convertPIToGwtPI(pi));
					}
				}
				
				nfRoot.setHostUrl( driver.getHostUrl() );
				nfRoot.setAllowSelfSignedCerts( driver.isAllowSelfSignedCertificate() );
				nfRoot.setIsSharePointServer( driver.isPutRequiresContentLength() );
				nfRoot.setFullSyncDirOnly( driver.getFullSyncDirOnly() );
				nfRoot.setIndexContent( driver.getIndexContent() );
				nfRoot.setJitsEnabled( driver.isJitsEnabled() );
				nfRoot.setJitsResultsMaxAge( driver.getJitsMaxAge() );
				nfRoot.setJitsAclMaxAge( driver.getJitsAclMaxAge() );
				nfRoot.setAllowDesktopAppToTriggerInitialHomeFolderSync( driver.getAllowDesktopAppToTriggerInitialHomeFolderSync() );
				nfRoot.setAllowDesktopAppToTriggerInitialHomeFolderSync( driver.getAllowDesktopAppToTriggerInitialHomeFolderSync() );
				
				AuthenticationType authType = driver.getAuthenticationType();
				if (null != authType) {
					nfRoot.setAuthType(GwtAuthenticationType.getType(authType.getValue()));
				}

				// Get the list of principals that can use the net folder root
				getListOfPrincipals( ami, driver, nfRoot );

				// Get the net folder's sync schedule.
				gwtSchedule = getGwtSyncSchedule( ami, driver );
				nfRoot.setSyncSchedule( gwtSchedule );

				listOfNetFolderRoots.add( nfRoot );
			}
		}
		
		return listOfNetFolderRoots;
	}
	
	/**
	 * Return a list of the names of the Net Folder Root's that reference the given proxy identity ID.
	 * 
	 * @param bs
	 * @param proxyIdentityId
	 * 
	 * @return
	 */
	public static List<String> getNetFolderRootNamesReferencingProxyIdentityId(AllModulesInjected bs, Long proxyIdentityId) {
		// Allocate a List<String> we can return with the names of the
		// Net Folder Roots that are referencing the give proxy
		// identity.
		List<String> reply = new ArrayList<String>();

		// Are there a any Net Folder Roots defined?
		List<ResourceDriverConfig> drivers = bs.getResourceDriverModule().getAllNetFolderResourceDriverConfigs();
		if (MiscUtil.hasItems(drivers)) {
			// Yes!  Scan them.
			for (ResourceDriverConfig driver:  drivers) {
				// Is this Net Folder Root referencing the given proxy
				// identity?
				Long driverProxyIdentityId = driver.getProxyIdentityId();
				if ((null != driverProxyIdentityId) && driverProxyIdentityId.equals(proxyIdentityId)) {
					// Yes!  Add it's name to the List<String> we're
					// returning.
					reply.add(driver.getName());
				}
			}
		}

		// If we get here, reply refers to a List<String> containing
		// the names of the Net Folder roots that are referencing the
		// given proxy identity ID.  Return it.
		return reply;
	}
	
	/*
	 */
	private static AuthenticationType getAuthType(GwtAuthenticationType gwtAuthType) {
		if (null == gwtAuthType) {
			return null;
		}
		
		switch(gwtAuthType) {
		case KERBEROS:            return AuthenticationType.kerberos;
		case KERBEROS_THEN_NTLM:  return AuthenticationType.kerberos_then_ntlm;
		case NMAS:                return AuthenticationType.nmas;
		case NTLM:                return AuthenticationType.ntlm;
		
		default:
			return AuthenticationType.kerberos;
		}
	}
	
	/*
	 */
	private static CoreDao getCoreDao() {
		return ((CoreDao) SpringContextUtil.getBean("coreDao"));
	}
	
	/*
	 * Get the data sync settings for the given net folder binder and
	 * store them in the given NetFolder.
	 */
	private static NetFolderDataSyncSettings getDataSyncSettings(AllModulesInjected bs, NetFolderConfig nfc) {
		NetFolderDataSyncSettings settings = new NetFolderDataSyncSettings();
		
		settings.setAllowDesktopAppToSyncData(          nfc.getAllowDesktopAppToSyncData()                    );
		settings.setAllowMobileAppsToSyncData(          nfc.getAllowMobileAppsToSyncData()                    );
		settings.setAllowDesktopAppToTriggerSync(       nfc.getAllowDesktopAppToTriggerInitialHomeFolderSync());
		settings.setInheritAllowDesktopAppToTriggerSync(nfc.getUseInheritedDesktopAppTriggerSetting()         );
		
		return settings;
	}
	
	/*
	 * Return the appropriate DriverType from the given
	 * NetFolderRootType.
	 */
	private static DriverType getDriverType(NetFolderRootType type) {
		switch (type)
		{
		case CLOUD_FOLDERS:     return DriverType.cloud_folders;
		case FAMT:              return DriverType.famt;
		case FILE_SYSTEM:       return DriverType.filesystem;
		case NETWARE:           return DriverType.netware;
		case OES:               return DriverType.oes;
		case OES2015:           return DriverType.oes2015;
		case SHARE_POINT_2010:  return DriverType.share_point_2010;
		case SHARE_POINT_2013:  return DriverType.share_point_2013;
		case WEB_DAV:           return DriverType.webdav;
		case WINDOWS:           return DriverType.windows_server;
		
		default:
		case UNKNOWN:
			return DriverType.famt;
		}
	}

	/**
	 * For the given Binder, return a GwtSchedule object that represents the binder's
	 * sync schedule.
	 */
	private static GwtSchedule getGwtSyncSchedule(
		AllModulesInjected ami,
		Binder binder )
	{
		ScheduleInfo scheduleInfo;
		GwtSchedule gwtSchedule;
		
		if ( binder == null )
			return null;
		
		// Get the ScheduleInfo for the given binder.
		scheduleInfo = NetFolderHelper.getMirroredFolderSynchronizationSchedule( binder.getId() );
		
		gwtSchedule = GwtServerHelper.getGwtSyncSchedule( scheduleInfo );
		
		return gwtSchedule;
	}
	
	/**
	 * For the given ResourceDriverConfig, return a GwtSchedule object that represents the net folder server's
	 * sync schedule.
	 */
	private static GwtSchedule getGwtSyncSchedule(
		AllModulesInjected ami,
		ResourceDriverConfig rdConfig )
	{
		ScheduleInfo scheduleInfo;
		GwtSchedule gwtSchedule;
		
		if ( rdConfig == null )
			return null;
		
		// Get the ScheduleInfo for the given net folder server.
		scheduleInfo = NetFolderHelper.getNetFolderServerSynchronizationSchedule( rdConfig.getId() );

		gwtSchedule = GwtServerHelper.getGwtSyncSchedule( scheduleInfo );
		
		return gwtSchedule;
	}
	
	/**
	 * 
	 */
	private static GwtJitsNetFolderConfig getJitsSettings( Binder binder )
	{
		GwtJitsNetFolderConfig jitsSettings;
		
		jitsSettings = new GwtJitsNetFolderConfig();
		
		if ( binder != null )
		{
			jitsSettings.setJitsEnabled( binder.isJitsEnabled() );
			jitsSettings.setResultsMaxAge( binder.getJitsMaxAge() );
			jitsSettings.setAclMaxAge( binder.getJitsAclMaxAge() );
		}

		return jitsSettings;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static void getListOfPrincipals(
		AllModulesInjected ami,
		ResourceDriverConfig driver,
		NetFolderRoot nfRoot )
	{
		List<Function> functions;
		List<WorkAreaFunctionMembership> memberships;
		WorkAreaFunctionMembership membership = null;
		List<Principal> members;
		AdminModule adminModule;

		adminModule = ami.getAdminModule();
		functions = adminModule.getFunctions( ObjectKeys.ROLE_TYPE_ZONE );
		memberships = adminModule.getWorkAreaFunctionMemberships( driver );
		membership = null;
		for ( Function f : functions )
		{
			if ( ObjectKeys.FUNCTION_CREATE_FILESPACES_INTERNALID.equals( f.getInternalId() ) )
			{
				for ( WorkAreaFunctionMembership m : memberships )
				{
					if ( f.getId().equals( m.getFunctionId() ) )
					{
						membership = m;
						break;
					}
				}
			}
		}
		
		members = new ArrayList<Principal>();

		if ( membership != null )
		{
			members = ResolveIds.getPrincipals( membership.getMemberIds() );
		}
		
		for ( Principal p : members ) 
		{
			if ( p instanceof User )
			{
				GwtUser gwtUser;
				
				gwtUser = new GwtUser();
				gwtUser.setInternal( ((User)p).getIdentityInfo().isInternal() );
				gwtUser.setUserId( p.getId() );
				gwtUser.setName( p.getName() );
				gwtUser.setTitle( Utils.getUserTitle( p ) );
				gwtUser.setWorkspaceTitle( ((User)p).getWSTitle() );
				gwtUser.setEmail( p.getEmailAddress() );
				
				nfRoot.addPrincipal( gwtUser );
			}
			else if ( p instanceof Group )
			{
				GwtGroup gwtGroup;
				Description desc;
				
				gwtGroup = new GwtGroup();
				gwtGroup.setInternal( ((Group)p).getIdentityInfo().isInternal() );
				gwtGroup.setId( p.getId().toString() );
				gwtGroup.setName( p.getName() );
				gwtGroup.setTitle( p.getTitle() );
				gwtGroup.setDn( p.getForeignName() );
				desc = p.getDescription();
				if ( desc != null )
					gwtGroup.setDesc( desc.getText() );
				gwtGroup.setGroupType( GwtServerHelper.getGroupType( p ) );
				
				nfRoot.addPrincipal( gwtGroup );
			}
		}
	}
	
	/**
	 * Return a NetFolder object for the given net folder id.  We won't get all of the information
	 * about the net folder.  Just a basic set of info.
	 */
	private static NetFolder getNetFolderWithMinimalInfo(
		AllModulesInjected ami,
		NetFolderConfig nfc )
	{
		NetFolder netFolder;
		String name;
		String displayName;
		
		netFolder = new NetFolder();
		netFolder.setId( nfc.getTopFolderId() );
		
		netFolder.setNetFolderRootName((nfc.getResourceDriver() != null)? nfc.getResourceDriver().getName() : null);
		netFolder.setRelativePath( nfc.getResourcePath() );
		netFolder.setStatus( getNetFolderSyncStatus( netFolder.getId() ) );
		netFolder.setIsHomeDir( nfc.isHomeDir() );
		
		// Is this a home dir net folder?
		name = nfc.getName();
		displayName = name;
		if ( nfc.isHomeDir() )
		{
			Principal owner;
			
			Binder nfb = NetFolderUtil.getNetFolderTopBinder( nfc );
			
			// Yes
			owner = nfb.getOwner();
			if ( owner != null )
			{
				String title;
				
				title = owner.getTitle();
				if ( title != null && title.length() > 0 )
					displayName = nfb.getTitle() + " (" + title + ")";
			}
		}
		
		netFolder.setName( name );
		netFolder.setDisplayName( displayName );
		
		netFolder.setIndexContent( nfc.getIndexContent() );
		netFolder.setInheritIndexContentSetting( nfc.getUseInheritedIndexContent() );
		netFolder.setInheritJitsSettings( nfc.getUseInheritedJitsSettings() );
		netFolder.setFullSyncDirOnly( nfc.getFullSyncDirOnly() );
		
		// Get the data sync settings.
		{
			NetFolderDataSyncSettings dataSyncSettings;

			dataSyncSettings = getDataSyncSettings( ami, nfc );
			netFolder.setDataSyncSettings( dataSyncSettings );
		}

		return netFolder;
	}
	
	/**
	 * Return a NetFolder object for the given net folder id 
	 */
	public static NetFolder getNetFolder(
		AllModulesInjected ami,
		Long netFolderId )
	{
		NetFolder netFolder;
		Binder binder;
		ArrayList<GwtRole> listOfRoles;
		GwtJitsNetFolderConfig jitsSettings;
		
		binder = ami.getBinderModule().getBinder( netFolderId );
		
		NetFolderConfig nfc = binder.getNetFolderConfig();

		netFolder = getNetFolderWithMinimalInfo( ami, nfc );
		
		// Get the net folder's sync schedule configuration.
		{
			GwtNetFolderSyncScheduleConfig config;
			GwtSchedule gwtSchedule;
			NetFolderSyncScheduleOption nfSyncScheduleOption;
			SyncScheduleOption syncScheduleOption;
			
			config = new GwtNetFolderSyncScheduleConfig();
			
			gwtSchedule = getGwtSyncSchedule( ami, binder );
			config.setSyncSchedule( gwtSchedule );
			
			// Get the sync schedule option
			nfSyncScheduleOption = NetFolderSyncScheduleOption.USE_NET_FOLDER_SERVER_SCHEDULE;
			syncScheduleOption = binder.getSyncScheduleOption();
			if ( syncScheduleOption != null )
			{
				switch ( syncScheduleOption )
				{
				case useNetFolderServerSchedule:
					nfSyncScheduleOption = NetFolderSyncScheduleOption.USE_NET_FOLDER_SERVER_SCHEDULE;
					break;
					
				case useNetFolderSchedule:
					nfSyncScheduleOption = NetFolderSyncScheduleOption.USE_NET_FOLDER_SCHEDULE;
					break;
				}
			}
			else
			{
				// The binder doesn't have a value for the syncScheduleOption field.
				// Determine what the value is based on whether or not the net folder has a schedule defined.
				if ( gwtSchedule != null && gwtSchedule.getEnabled() == true )
					nfSyncScheduleOption = NetFolderSyncScheduleOption.USE_NET_FOLDER_SCHEDULE;
			}
			config.setSyncScheduleOption( nfSyncScheduleOption );
			
			netFolder.setSyncScheduleConfig( config );
		}
		
		// Get the rights associated with this net folder.
		listOfRoles = getNetFolderRights( ami, binder );
		netFolder.setRoles( listOfRoles );
		
		// Get the jits settings
		jitsSettings = getJitsSettings( binder );
		netFolder.setJitsConfig( jitsSettings );

		// Get the full sync dir only setting
		netFolder.setFullSyncDirOnly( binder.getFullSyncDirOnly() );
		
		return netFolder;
	}
	
	/**
	 * Return the roles (rights) that have been set on this net folder
	 */
	@SuppressWarnings("rawtypes")
	private static ArrayList<GwtRole> getNetFolderRights(
		AllModulesInjected ami,
		Binder binder )
	{
		ArrayList<GwtRole> listOfRoles;
		GwtRole role;
		AdminModule adminModule;
		
		listOfRoles = new ArrayList<GwtRole>();
		role = new GwtRole();
		role.setType( GwtRoleType.AllowAccess );
		listOfRoles.add( role );
		role = new GwtRole();
		role.setType( GwtRoleType.ShareExternal );
		listOfRoles.add( role );
		role = new GwtRole();
		role.setType( GwtRoleType.ShareForward );
		listOfRoles.add( role );
		role = new GwtRole();
		role.setType( GwtRoleType.ShareInternal );
		listOfRoles.add( role );
		role = new GwtRole();
		role.setType( GwtRoleType.SharePublic );
		listOfRoles.add( role );
		role = new GwtRole();
		role.setType( GwtRoleType.SharePublicLinks );
		listOfRoles.add( role );
		
		adminModule = ami.getAdminModule();
		
		for ( GwtRole nextRole : listOfRoles )
		{
			Long fnId = null;
			WorkAreaFunctionMembership membership;
			Set<Long> memberIds;
			List principals = null;
			
			// Get the Function id for the given role
			fnId = GwtServerHelper.getFunctionIdFromRole( ami, nextRole );

			// Did we find the function for the given role?
			if (null == fnId) {
				// No
				GwtLogHelper.error(m_logger, "In GwtNetFolderHelper.getNetFolderRights(), could not find function for role: " + nextRole.getType());
				continue;
			}

			// Get the role's membership
			membership = adminModule.getWorkAreaFunctionMembership( binder, fnId );
			if ( membership == null )
				continue;
			
			// Get the member ids
			memberIds = membership.getMemberIds();
			if ( memberIds == null )
				continue;
			
			try 
			{
				principals = ResolveIds.getPrincipals( memberIds );
			}
			catch ( Exception ex )
			{
				// Nothing to do
			}
			
			if ( MiscUtil.hasItems( principals ) == false )
				continue;

			for ( Object nextObj :  principals )
			{
				if ( nextObj instanceof Principal )
				{
					Principal nextPrincipal;
					
					nextPrincipal = (Principal) nextObj;

					if ( nextPrincipal instanceof Group )
					{
						Group nextGroup;
						GwtGroup gwtGroup;
						Description desc;
						
						nextGroup = (Group) nextPrincipal;
						
						gwtGroup = new GwtGroup();
						gwtGroup.setInternal( nextGroup.getIdentityInfo().isInternal() );
						gwtGroup.setId( nextGroup.getId().toString() );
						gwtGroup.setName( nextGroup.getName() );
						gwtGroup.setTitle( nextGroup.getTitle() );
						gwtGroup.setDn( nextGroup.getForeignName() );
						desc = nextGroup.getDescription();
						if ( desc != null )
							gwtGroup.setDesc( desc.getText() );
						gwtGroup.setGroupType( GwtServerHelper.getGroupType( nextGroup ) );
						
						nextRole.addMember( gwtGroup );
					}
					else if ( nextPrincipal instanceof User )
					{
						User user;
						GwtUser gwtUser;
						
						user = (User) nextPrincipal;
	
						gwtUser = new GwtUser();
						gwtUser.setInternal( user.getIdentityInfo().isInternal() );
						gwtUser.setUserId( user.getId() );
						gwtUser.setName( user.getName() );
						gwtUser.setTitle( Utils.getUserTitle( user ) );
						gwtUser.setWorkspaceTitle( user.getWSTitle() );
						gwtUser.setEmail( user.getEmailAddress() );
	
						nextRole.addMember( gwtUser );
					}
				}
			}
		}// end for

		return listOfRoles;
	}
	
	
	/**
	 * Get the sync statistics for the given net folder.
	 */
	public static NetFolderSyncStatistics getNetFolderSyncStatistics( Long binderId )
	{
		NetFolderSyncStatistics syncStatistics;
		
		syncStatistics = new NetFolderSyncStatistics();

		if ( binderId != null )
		{
			BinderState binderState;

            binderState = (BinderState) getCoreDao().load( BinderState.class, binderId );
            if ( binderState != null )
            {
    			FullSyncStats syncStats;
            	
    			syncStats = binderState.getFullSyncStats();
    			if ( syncStats != null )
    			{
    				Date date;
    				Long value;
    				
    				syncStatistics.setCountEntryExpunge( syncStats.getCountEntryExpunge() );
    				syncStatistics.setCountFailure( syncStats.getCountFailure() );
    				syncStatistics.setCountFileAdd( syncStats.getCountFileAdd() );
    				syncStatistics.setCountFileExpunge( syncStats.getCountFileExpunge() );
    				syncStatistics.setCountFileModify( syncStats.getCountFileModify() );
    				syncStatistics.setCountFiles( syncStats.getCountFiles() );
    				syncStatistics.setCountFileSetAcl( syncStats.getCountFileSetAcl() );
    				syncStatistics.setCountFileSetOwnership( syncStats.getCountFileSetOwnership() );
    				syncStatistics.setCountFolderAdd( syncStats.getCountFolderAdd() );
    				syncStatistics.setCountFolderExpunge( syncStats.getCountFolderExpunge() );
    				syncStatistics.setCountFolderMaxQueue( syncStats.getCountFolderMaxQueue() );
    				syncStatistics.setCountFolderProcessed( syncStats.getCountFolderProcessed() );
    				syncStatistics.setCountFolders( syncStats.getCountFolders() );
    				syncStatistics.setCountFolderSetAcl( syncStats.getCountFolderSetAcl() );
    				syncStatistics.setCountFolderSetOwnership( syncStats.getCountFolderSetOwnership() );
    				syncStatistics.setDirOnly( syncStats.getDirOnly() );
    				syncStatistics.setEnumerationFailed( syncStats.getEnumerationFailed() );
    				syncStatistics.setStatusIpv4Address( syncStats.getStatusIpv4Address() );
    				
    				value = null;
    				date = syncStats.getEndDate();
    				if ( date != null )
    					value = new Long( date.getTime() );
    				syncStatistics.setEndDate( value );
    				
    				value = null;
    				date = syncStats.getStartDate();
    				if ( date != null )
    					value = new Long( date.getTime() );
    				syncStatistics.setStartDate( value );
    				
    				value = null;
    				date = syncStats.getStatusDate();
    				if ( date != null )
    					value = new Long( date.getTime() );
    				syncStatistics.setStatusDate( value );
    			}
            }
		}
		
		return syncStatistics;
	}
	
	/**
	 * Get the sync status of the given net folder by converting a FullSyncStatus object
	 * into a NetFolderSyncStatus object.
	 */
	public static NetFolderSyncStatus getNetFolderSyncStatus( Long binderId )
	{
		NetFolderSyncStatus status = NetFolderSyncStatus.SYNC_NEVER_RUN;
		
		if ( binderId != null )
		{
			BinderState binderState;

            binderState = (BinderState) getCoreDao().load( BinderState.class, binderId );
            if ( binderState != null )
            {
    			FullSyncStats syncStats;
            	
    			syncStats = binderState.getFullSyncStats();
    			if ( syncStats != null )
    			{
    				FullSyncStatus syncStatus;
    				
    				syncStatus = syncStats.getStatus();
    				if ( syncStatus != null )
    				{
    					switch ( syncStatus )
    					{
    					case canceled:
    						status = NetFolderSyncStatus.SYNC_CANCELED;
    						break;
    						
    					case finished:
    						status = NetFolderSyncStatus.SYNC_COMPLETED;
    						break;
    						
    					case ready:
    					case taken:
    						status = NetFolderSyncStatus.WAITING_TO_BE_SYNCD;
    						break;
    						
    					case started:
    						status = NetFolderSyncStatus.SYNC_IN_PROGRESS;
    						break;
    						
    					case stopped:
    						status = NetFolderSyncStatus.SYNC_STOPPED;
    						break;
    						
    					case deleting:
    						status = NetFolderSyncStatus.DELETE_IN_PROGRESS;
    						break;

    					case aborted:
    						status = NetFolderSyncStatus.UNKNOWN;
    						break;
    					}
    				}
    			}
            }
		}
		
		return status;
	}
	
	/**
	 * Return the number of net folders that match the given criteria
	 */
	public static int getNumberOfNetFolders(
		AllModulesInjected ami,
		NetFolderSelectSpec selectSpec )
	{
		int numNetFolders;
		
		numNetFolders = NetFolderHelper.getNumberOfNetFolders(
													ami.getBinderModule(),
													ami.getWorkspaceModule(),
													selectSpec );

		return numNetFolders;
	}
	
	/**
	 * 
	 */
	public static NetFolderRootType getRootTypeFromDriverType( DriverType driverType )
	{
		switch ( driverType )
		{
		case windows_server:
			return NetFolderRootType.WINDOWS;
			
		case cloud_folders:
			return NetFolderRootType.CLOUD_FOLDERS;
			
		case famt:
			return NetFolderRootType.FAMT;
		
		case filesystem:
			return NetFolderRootType.FILE_SYSTEM;

		case netware:
			return NetFolderRootType.NETWARE;
			
		case oes:
			return NetFolderRootType.OES;
			
		case oes2015:
			return NetFolderRootType.OES2015;
			
		case share_point_2010:
			return NetFolderRootType.SHARE_POINT_2010;

		case share_point_2013:
			return NetFolderRootType.SHARE_POINT_2013;
			
		case webdav:
			return NetFolderRootType.WEB_DAV;

		default:
			return NetFolderRootType.UNKNOWN;
		}
	}
	
	/**
	 * For the given NetFolderSyncScheduleOption, return a SyncScheduleOption
	 */
	public static SyncScheduleOption getSyncScheduleOptionFromGwtSyncScheduleOption( NetFolderSyncScheduleOption option )
	{
		if ( option != null )
		{
			switch ( option )
			{
			case USE_NET_FOLDER_SERVER_SCHEDULE:
				return SyncScheduleOption.useNetFolderServerSchedule;
				
			case USE_NET_FOLDER_SCHEDULE:
			default:
				return SyncScheduleOption.useNetFolderSchedule;
			}
		}
		else
			return null;
	}
	
	/**
	 * Modify the net folder from the given data
	 */
	public static NetFolder modifyNetFolder(
		AllModulesInjected ami,
		NetFolder netFolder ) throws GwtTeamingException
	{
		try
		{
			ScheduleInfo scheduleInfo = null;
			SyncScheduleOption syncScheduleOption = null;
			NetFolderDataSyncSettings dataSyncSettings;
			
			// Get the schedule information
			{
				GwtNetFolderSyncScheduleConfig config;
				
				config = netFolder.getSyncScheduleConfig();
				if ( config != null )
				{
					scheduleInfo = GwtServerHelper.getScheduleInfoFromGwtSchedule( config.getSyncSchedule() );
					
					syncScheduleOption = getSyncScheduleOptionFromGwtSyncScheduleOption( config.getSyncScheduleOption() );
				}
			}
			
			dataSyncSettings = netFolder.getDataSyncSettings();
			
			NetFolderHelper.modifyNetFolder(
										ami.getBinderModule(),
										ami.getFolderModule(),
										ami.getNetFolderModule(),
										netFolder.getId(),
										netFolder.getName(),
										netFolder.getNetFolderRootName(),
										netFolder.getRelativePath(),
										scheduleInfo,
										syncScheduleOption,
										netFolder.getIndexContent(),
										netFolder.getInheritIndexContentSetting(),
										netFolder.getFullSyncDirOnly(),
										dataSyncSettings.getAllowDesktopAppToTriggerSync(),
										dataSyncSettings.getInheritAllowDesktopAppToTriggerSync() );

			// Set the rights on the net folder
			if ( netFolder.getIsHomeDir() == false )
				setNetFolderRights( ami, netFolder.getId(), netFolder.getRoles() );
			
			Binder binder = ami.getBinderModule().getBinder(netFolder.getId());

			// Save the data sync settings.
			saveDataSyncSettings( ami, binder.getNetFolderConfig(), dataSyncSettings );
			
			// Save the jits settings
			{
				GwtJitsNetFolderConfig settings;
				
				settings = netFolder.getJitsConfig();
				if ( settings != null )
				{
					NetFolderHelper.saveJitsSettings(
												ami.getNetFolderModule(),
												binder.getNetFolderConfig(),
												netFolder.getInheritJitsSettings(),
												settings.getJitsEnabled(),
												settings.getAclMaxAge(),
												settings.getResultsMaxAge() );
				}
			}
		}
		catch ( Exception ex )
		{
			GwtTeamingException gtEx;
			
			if ( ex instanceof TitleException )
			{
				String[] args;
				
				args = new String[] { netFolder.getName() };
				gtEx = GwtLogHelper.getGwtClientException();
				gtEx.setAdditionalDetails( NLT.get( "netfolder.duplicate.name", args ) );
			}
			else if ( ex instanceof IllegalCharacterInNameException )
			{
				gtEx = GwtLogHelper.getGwtClientException();
				gtEx.setAdditionalDetails( NLT.get( "netfolder.name.illegal.characters" ) );
			}
			else
			{
				gtEx = GwtLogHelper.getGwtClientException( ex );
			}
			
			GwtLogHelper.error(m_logger, "Error modifying net folder: " + netFolder.getName(), ex);
			throw gtEx;				
		}
		
		return netFolder;
	}
	
	
	/**
	 * Modify the net folder root from the given data
	 */
	public static NetFolderRoot modifyNetFolderRoot(
		AllModulesInjected ami,
		NetFolderRoot netFolderRoot ) throws GwtTeamingException
	{
		DriverType driverType;

		try
		{
			ScheduleInfo scheduleInfo;
			AuthenticationType authType;
			
			scheduleInfo = GwtServerHelper.getScheduleInfoFromGwtSchedule( netFolderRoot.getSyncSchedule() );

			driverType = getDriverType( netFolderRoot.getRootType() );
			
			authType = getAuthType( netFolderRoot.getAuthType() );
			
			GwtProxyIdentity pi = netFolderRoot.getProxyIdentity();
			Long proxyIdentityId = ((null == pi) ? null : pi.getId());
			NetFolderHelper.modifyNetFolderRoot(
											ami.getAdminModule(),
											ami.getResourceDriverModule(),
											ami.getProfileModule(),
											ami.getBinderModule(),
											ami.getWorkspaceModule(),
											ami.getFolderModule(),
											netFolderRoot.getName(),
											netFolderRoot.getRootPath(),
											netFolderRoot.getProxyName(),
											netFolderRoot.getProxyPwd(),
											netFolderRoot.getUseProxyIdentity(),
											proxyIdentityId,
											driverType,
											netFolderRoot.getHostUrl(),
											netFolderRoot.getAllowSelfSignedCerts(),
											netFolderRoot.getIsSharePointServer(),
											netFolderRoot.getListOfPrincipalIds(),
											netFolderRoot.getFullSyncDirOnly(),
											authType,
											netFolderRoot.getIndexContent(),
											netFolderRoot.getJitsEnabled(),
											netFolderRoot.getJitsResultsMaxAge(),
											netFolderRoot.getJitsAclMaxAge(),
											netFolderRoot.getAllowDesktopAppToTriggerInitialHomeFolderSync(),
											scheduleInfo );
		}
		
		catch (Exception ex) {
			GwtTeamingException gtEx = GwtLogHelper.getGwtClientException(ex);
			GwtLogHelper.error(m_logger, "Error modifying net folder root: " + netFolderRoot.getName(), ex);
			throw gtEx;				
		}
		
		return netFolderRoot;
	}

	/*
	 * Save the data sync settings for the given net folder binder.
	 */
	@SuppressWarnings("unused")
	private static void saveDataSyncSettings(AllModulesInjected ami, NetFolderConfig nfc, NetFolderDataSyncSettings settings) {
		if ((null != nfc) && (null != settings)) {
			nfc.setAllowDesktopAppToSyncData(settings.getAllowDesktopAppToSyncData());
			nfc.setAllowDesktopAppToTriggerInitialHomeFolderSync(settings.getAllowDesktopAppToTriggerSync());
			nfc.setUseInheritedDesktopAppTriggerSetting(settings.getInheritAllowDesktopAppToTriggerSync());
			
	   		if (false) {
	   			// Not writing anything as per bug 816823.
	   			nfc.setAllowMobileAppsToSyncData(settings.getAllowMobileAppsToSyncData());
	   		}

			try {
				ami.getNetFolderModule().modifyNetFolder(nfc);
			}
			
			catch (Exception ex) {
				GwtLogHelper.error(m_logger, "In saveDataSyncSettings(), call to modifyNetFolder() failed. " + ex.toString());
			}
		}
	}

	/**
	 * 
	 */
	private static void setNetFolderRights(
		AllModulesInjected ami,
		Long binderId,
		ArrayList<GwtRole> roles )
	{
		AdminModule adminModule;
		Binder binder;

		if ( binderId == null && roles == null )
		{
			GwtLogHelper.error(m_logger, "In GwtNetFolderHelper.setNetFolderRights(), invalid parameters");
		}
		
		adminModule = ami.getAdminModule();
		
		// Get the binder's work area
		binder = ami.getBinderModule().getBinder( binderId );
		
		for ( GwtRole nextRole : roles )
		{
			Long fnId = null;
			
			// Get the Function id for the given role
			fnId = GwtServerHelper.getFunctionIdFromRole( ami, nextRole );

			// Did we find the function for the given role?
			if (null == fnId) {
				// No!
				GwtLogHelper.error(m_logger, "In GwtNetFolderHelper.setNetFolderRights(), could not find function for role: " + nextRole.getType());
				continue;
			}

			// Reset the function's membership.
			adminModule.resetWorkAreaFunctionMemberships( binder, fnId, nextRole.getMemberIds() );
		}
		
		// Re-index this binder.
		// ami.getBinderModule().indexBinder( binderId, false );	// 20130122:  Commented out with the fix for bug#799512 as per Jong.
	}
	
	/**
	 * Stop the sync of the given list of Net Folders.
	 * 
	 * @param bs
	 * @param req
	 * @param netFolders
	 * 
	 * @return
	 */
	public static Set<NetFolder> stopSyncNetFolders(AllModulesInjected bs, HttpServletRequest req, Set<NetFolder> netFolders) {
		for (NetFolder nextNetFolder: netFolders) {
			try {
				if (!(bs.getFolderModule().requestNetFolderFullSyncStop(nextNetFolder.getId()))) {
					// The net folder was not in the 'started' state.
					// Make a request to remove the net folder from the
					// 'waiting to be sync'd' state.
					bs.getFolderModule().dequeueFullSynchronize(nextNetFolder.getId());
				}
				
				nextNetFolder.setStatus(getNetFolderSyncStatus(nextNetFolder.getId()));
			}
			
			catch (Exception e) {
				GwtLogHelper.error(m_logger, "Error trying to stop the syncing of the net folder: " + nextNetFolder.getName() + ", " + e.toString());
			}
		}
		
		return netFolders;
	}
	
	/**
	 * Sync the given list of Net Folders.
	 * 
	 * @param bs
	 * @param req
	 * @param netFolders
	 * 
	 * @return
	 */
	public static Set<NetFolder> syncNetFolders(AllModulesInjected bs, HttpServletRequest req, Set<NetFolder> netFolders) {
		for (NetFolder nextNetFolder:  netFolders) {
			try {
				if(bs.getFolderModule().enqueueFullSynchronize(nextNetFolder.getId())) {
					nextNetFolder.setStatus(getNetFolderSyncStatus(nextNetFolder.getId()));
				}
			}
			
			catch (Exception e) {
				GwtLogHelper.error(m_logger, "Error trying to sync the net folder: " + nextNetFolder.getName() + ", " + e.toString());
			}
		}
		
		return netFolders;
	}
	
	/**
	 * Sync the given Net Folder Servers by sync'ing all the Net
	 * Folders associated with it.
	 * 
	 * @param bs
	 * @param req
	 * @param netFolderServers
	 */
	public static Set<NetFolderRoot> syncNetFolderServers(AllModulesInjected bs, HttpServletRequest req, Set<NetFolderRoot> netFolderServers) {
		for (NetFolderRoot nextServer: netFolderServers) {
			String statusTicketId = ("sync_net_folder_server" + nextServer.getName());
			GwtWebStatusTicket.newStatusTicket(statusTicketId, req);
			if (bs.getResourceDriverModule().enqueueSynchronize(nextServer.getName(), false)) {
				// The binder was not deleted (typical situation).
				nextServer.setStatus(NetFolderRootStatus.SYNC_IN_PROGRESS);
				nextServer.setStatusTicketId(statusTicketId);
			}
			
			else {
				nextServer.setStatus(NetFolderRootStatus.SYNC_FAILURE);
			}
		}

		return netFolderServers;
	}
	
	/**
	 * Test the connection for the given net folder root.
	 * 
	 * @param rootName
	 * @param rootType
	 * @param rootPath
	 * @param subPath
	 * @param proxyName
	 * @param proxyPwd
	 * 
	 * @return
	 */
	public static TestNetFolderConnectionResponse testNetFolderConnection(String rootName, NetFolderRootType rootType, String rootPath, String subPath, String proxyName, String proxyPwd) {
		TestNetFolderConnectionResponse response = new TestNetFolderConnectionResponse();
		GwtConnectionTestStatusCode statusCode = GwtConnectionTestStatusCode.UNKNOWN;
		ConnectionTestStatus status = NetFolderHelper.testNetFolderConnection(
			rootName,
			getDriverType(rootType),
			rootPath,
			subPath,
			proxyName,
			proxyPwd);
		
		if (null != status) {
			switch (status.getCode()) {
			case NETWORK_ERROR:            statusCode = GwtConnectionTestStatusCode.NETWORK_ERROR;           break;
			case NORMAL:                   statusCode = GwtConnectionTestStatusCode.NORMAL;                  break;
			case PROXY_CREDENTIALS_ERROR:  statusCode = GwtConnectionTestStatusCode.PROXY_CREDENTIALS_ERROR; break;
			default:                       statusCode = GwtConnectionTestStatusCode.UNKNOWN;                 break;
			}
		}
		
		response.setStatusCode(statusCode);
		return response;
	}
	
	public static TestNetFolderConnectionResponse testNetFolderConnection(String rootName, NetFolderRootType rootType, String rootPath, String subPath, GwtProxyIdentity proxyIdentity) {
		// Always use the initial form of the method.
		return testNetFolderConnection(
			rootName,
			rootType,
			rootPath,
			subPath,
			proxyIdentity.getProxyName(),
			proxyIdentity.getPassword());
	}
}
