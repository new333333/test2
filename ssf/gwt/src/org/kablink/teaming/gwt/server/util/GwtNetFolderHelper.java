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
	 * 
	 * @param bs
	 * @param req
	 * @param netFolders
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Set<NetFolder> checkNetFoldersStatus(AllModulesInjected bs, HttpServletRequest req, Set<NetFolder> netFolders) throws GwtTeamingException {
		for (NetFolder nextNetFolder:  netFolders) {
			nextNetFolder.setStatus(getNetFolderSyncStatus(nextNetFolder.getId()));
		}
		return netFolders;
	}
	
	/**
	 * Check the status of each net folder root in the list.
	 * 
	 * @param bs
	 * @param req
	 * @param netFolderServers
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Set<NetFolderRoot> checkNetFolderServerStatus(AllModulesInjected bs, HttpServletRequest req, Set<NetFolderRoot> netFolderServers) throws GwtTeamingException {
		for (NetFolderRoot nextNetFolderServer:  netFolderServers) {
			String statusTicketId = nextNetFolderServer.getStatusTicketId();
			if (statusTicketId != null) {
				StatusTicket statusTicket = GwtWebStatusTicket.findStatusTicket(statusTicketId, req);
				if (statusTicket != null) {
					if (statusTicket.isDone()) {
						nextNetFolderServer.setStatus(NetFolderRootStatus.READY);
					}
					
					// Currently, when we sync a net folder server, we
					// add all of the net folders associated with the
					// net folder server to a queue of net folders
					// waiting to be sync'd.  Given this fact, there
					// isn't really a 'status' of on a net folder
					// server.
					nextNetFolderServer.setStatus(NetFolderRootStatus.READY);
				}
				
				else {
					nextNetFolderServer.setStatus(NetFolderRootStatus.READY);
				}
			}
		}
		return netFolderServers;
	}
	
	/**
	 * Create a net folder in the default location from the given data.
	 * 
	 * @param bs
	 * @param netFolder
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static NetFolder createNetFolder(AllModulesInjected bs, NetFolder netFolder) throws GwtTeamingException {
		NetFolder newNetFolder = null;
		try {
			// Create the net folder in the global 'net folder roots'
			// workspace.  Get the binder where all net folders are
			// created.
			Binder parentBinder = getCoreDao().loadReservedBinder(
				ObjectKeys.NET_FOLDERS_ROOT_INTERNALID, 
				RequestContextHolder.getRequestContext().getZoneId());

			// Get the schedule information.
			ScheduleInfo       scheduleInfo       = null;
			SyncScheduleOption syncScheduleOption = null;
			GwtNetFolderSyncScheduleConfig config = netFolder.getSyncScheduleConfig();
			if (config != null) {
				scheduleInfo       = GwtServerHelper.getScheduleInfoFromGwtSchedule(config.getSyncSchedule()      );
				syncScheduleOption = getSyncScheduleOptionFromGwtSyncScheduleOption(config.getSyncScheduleOption());
			}
			
			NetFolderDataSyncSettings dataSyncSettings = netFolder.getDataSyncSettings();
			NetFolderConfig nfc = NetFolderHelper.createNetFolder(
				bs.getTemplateModule(),
				bs.getBinderModule(),
				bs.getFolderModule(),
				bs.getNetFolderModule(),
				bs.getAdminModule(),
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
				dataSyncSettings.getInheritAllowDesktopAppToTriggerSync());
			
			// Set the rights on the net folder.
			setNetFolderRights(bs, nfc.getTopFolderId(), netFolder.getRoles());
			
			// Set the data sync settings on the net folder.
			saveDataSyncSettings(bs, nfc, dataSyncSettings);
			
			// Save the JITS settings
			GwtJitsNetFolderConfig settings = netFolder.getJitsConfig();
			if (settings != null) {
				NetFolderHelper.saveJitsSettings(
					bs.getNetFolderModule(),
					nfc,
					netFolder.getInheritJitsSettings(),
					settings.getJitsEnabled(),
					settings.getAclMaxAge(),
					settings.getResultsMaxAge());
			}

			newNetFolder = new NetFolder();
			newNetFolder.setName(                         netFolder.getName()                      );
			newNetFolder.setDisplayName(                  netFolder.getName()                      );
			newNetFolder.setNetFolderRootName(            netFolder.getNetFolderRootName()         );
			newNetFolder.setRelativePath(                 netFolder.getRelativePath()              );
			newNetFolder.setId(                           nfc.getTopFolderId()                     );
			newNetFolder.setStatus(getNetFolderSyncStatus(newNetFolder.getId())                    );
			newNetFolder.setSyncScheduleConfig(           netFolder.getSyncScheduleConfig()        );
			newNetFolder.setRoles(                        netFolder.getRoles()                     );
			newNetFolder.setDataSyncSettings(             netFolder.getDataSyncSettings()          );
			newNetFolder.setJitsConfig(                   netFolder.getJitsConfig()                );
			newNetFolder.setFullSyncDirOnly(              netFolder.getFullSyncDirOnly()           );
			newNetFolder.setIndexContent(                 netFolder.getIndexContent()              );
			newNetFolder.setInheritIndexContentSetting(   netFolder.getInheritIndexContentSetting());
			newNetFolder.setInheritJitsSettings(          netFolder.getInheritJitsSettings()       );
			newNetFolder.setDataSyncSettings(             dataSyncSettings                         );
		}
		
		catch (Exception ex) {
			GwtTeamingException gtEx = GwtLogHelper.getGwtClientException();
			if (ex instanceof TitleException) {
				String[] args = new String[] {netFolder.getName()};
				gtEx.setAdditionalDetails( NLT.get("netfolder.duplicate.name", args));
			}
			
			else if (ex instanceof IllegalCharacterInNameException) {
				gtEx.setAdditionalDetails(NLT.get("netfolder.name.illegal.characters"));
			}
			
			else {
				gtEx.setAdditionalDetails(NLT.get("netfolder.cant.load.parent.binder"));
			}
			
			GwtLogHelper.error(m_logger, "Error creating net folder: " + netFolder.getName(), ex);
			throw gtEx;				
		}
		
		return newNetFolder;
	}
	
	/**
	 * Create a new net folder root from the given data.
	 * 
	 * @param bs
	 * @param netFolderRoot
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static NetFolderRoot createNetFolderRoot(AllModulesInjected bs, NetFolderRoot netFolderRoot) throws GwtTeamingException {
		ResourceDriverConfig rdConfig;
		NetFolderRoot newRoot = null;
		DriverType driverType = getDriverType(netFolderRoot.getRootType());
		ScheduleInfo scheduleInfo = GwtServerHelper.getScheduleInfoFromGwtSchedule( netFolderRoot.getSyncSchedule() );
		try {
			AuthenticationType authType = getAuthType(netFolderRoot.getAuthType());
			GwtProxyIdentity pi = netFolderRoot.getProxyIdentity();
			Long proxyIdentityId = ((null == pi) ? null : pi.getId());
			rdConfig = NetFolderHelper.createNetFolderRoot(
				bs.getAdminModule(),
				bs.getResourceDriverModule(),
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
				scheduleInfo);
		}
		
		catch (RDException ex) {
			GwtTeamingException gwtEx = GwtLogHelper.getGwtClientException(ex);
			throw gwtEx;
		}

		if (rdConfig != null) {
			newRoot = new NetFolderRoot();
			newRoot.setRootType(getRootTypeFromDriverType(driverType));
			newRoot.setId(       rdConfig.getId()         );
			newRoot.setName(     rdConfig.getName()       );
			newRoot.setProxyName(rdConfig.getAccountName());
			newRoot.setProxyPwd( rdConfig.getPassword()   );
			
			newRoot.setUseProxyIdentity(rdConfig.getUseProxyIdentity());
			Long proxyIdentityId = rdConfig.getProxyIdentityId();
			if (null != proxyIdentityId) {
				ProxyIdentity pi = GwtProxyIdentityHelper.getProxyIdentity(bs, proxyIdentityId);
				if (null != pi) {
					newRoot.setProxyIdentity(GwtProxyIdentityHelper.convertPIToGwtPI(pi));
				}
			}
			
			newRoot.setRootPath(            rdConfig.getRootPath()                 );
			newRoot.setHostUrl(             rdConfig.getHostUrl()                  );
			newRoot.setAllowSelfSignedCerts(rdConfig.isAllowSelfSignedCertificate());
			newRoot.setIsSharePointServer(  rdConfig.isPutRequiresContentLength()  );
			newRoot.setSyncSchedule(        netFolderRoot.getSyncSchedule()        );
			newRoot.setFullSyncDirOnly(     rdConfig.getFullSyncDirOnly()          );
			newRoot.setIndexContent(        rdConfig.getIndexContent()             );
			newRoot.setJitsEnabled(         rdConfig.isJitsEnabled()               );
			newRoot.setJitsResultsMaxAge(   rdConfig.getJitsMaxAge()               );
			newRoot.setJitsAclMaxAge(       rdConfig.getJitsAclMaxAge()            );
			
			AuthenticationType authType = rdConfig.getAuthenticationType();
			if (null != authType) {
				newRoot.setAuthType(GwtAuthenticationType.getType(authType.getValue()));
			}

			// Get the list of principals that can use the net folder
			// root.
			getListOfPrincipals(bs, rdConfig, newRoot);
		}
		
		return newRoot;
	}
	
	/**
	 * Delete the given list of net folders.
	 * 
	 * @param bs
	 * @param netFolders
	 * 
	 * @return
	 */
	public static DeleteNetFolderRpcResponseData deleteNetFolders(AllModulesInjected bs, Set<NetFolder> netFolders) {
		DeleteNetFolderRpcResponseData results = new DeleteNetFolderRpcResponseData();
		for (NetFolder nextNetFolder:  netFolders) {
			DeleteNetFolderResult result = new DeleteNetFolderResult();
			try {
				NetFolderHelper.deleteNetFolder(bs.getNetFolderModule(), nextNetFolder.getId(), false);
				result.setStatus(DeleteNetFolderStatus.SUCCESS, null);
			}
			
			catch (CannotDeleteSyncingNetFolderException nfEx) {
				result.setStatus(
					DeleteNetFolderStatus.DELETE_FAILED_SYNC_IN_PROGRESS,
					NLT.get("netfolder.cant.delete.sync.in.progress"));
			}
			
			catch (Exception e) {
				GwtLogHelper.error(m_logger, "Error deleting next net folder: " + nextNetFolder.getName(), e);
				result.setStatus(DeleteNetFolderStatus.DELETE_FAILED, e.toString());
			}
			
			results.addResult(nextNetFolder, result);
		}
		return results;
	}

	/**
	 * Delete the given list of net folder roots.
	 * 
	 * @param bs
	 * @param netFolderRoots
	 * 
	 * @return
	 */
	public static DeleteNetFolderServersRpcResponseData deleteNetFolderRoots(AllModulesInjected bs, Set<NetFolderRoot> netFolderRoots) {
		bs.getAdminModule().checkAccess(AdminOperation.manageResourceDrivers);

		DeleteNetFolderServersRpcResponseData result = new DeleteNetFolderServersRpcResponseData();
		ResourceDriverModule rdModule = bs.getResourceDriverModule();
		
		for (NetFolderRoot nextRoot:  netFolderRoots) {
			try {
				// Get a list of net folders that are referencing this
				// net folder server.
				NetFolderSelectSpec selectSpec = new NetFolderSelectSpec();
				selectSpec.setIncludeHomeDirNetFolders(true);
				selectSpec.setRootId(nextRoot.getId());
				List<NetFolder> listOfNetFolders = getAllNetFolders(bs, selectSpec, true);
				
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
	 * @param bs
	 * @param selectSpec
	 * @param getMinimalInfo
	 * 
	 * @return
	 */
	public static List<NetFolder> getAllNetFolders(AllModulesInjected bs, NetFolderSelectSpec selectSpec, boolean getMinimalInfo) {
		List<NetFolderConfig> listOfNetFolderConfig = NetFolderHelper.getAllNetFolders2(
			bs.getBinderModule(),
			bs.getWorkspaceModule(),
			selectSpec);

		ArrayList<NetFolder> listOfNetFolders = new ArrayList<NetFolder>();
		if (MiscUtil.hasItems(listOfNetFolderConfig)) {
			for (NetFolderConfig nextNetFolderConfig:  listOfNetFolderConfig) {
				NetFolder netFolder;
				if (getMinimalInfo)
				     netFolder = getNetFolderWithMinimalInfo(bs, nextNetFolderConfig                 );
				else netFolder = getNetFolder(               bs, nextNetFolderConfig.getTopFolderId());
				listOfNetFolders.add(netFolder);
			}
		}
		
		return listOfNetFolders;
	}
	
	/**
	 * Return a list of all the net folder roots.
	 * 
	 * @param bs
	 * 
	 * @return
	 */
	public static List<NetFolderRoot> getAllNetFolderRoots(AllModulesInjected bs) {
		List<NetFolderRoot> listOfNetFolderRoots = new ArrayList<NetFolderRoot>();
		AdminModule adminModule = bs.getAdminModule();
		if (adminModule.testAccess(AdminOperation.manageResourceDrivers)) {
			//Get a list of the currently defined Net Folder Roots.
			List<ResourceDriverConfig> drivers = bs.getResourceDriverModule().getAllNetFolderResourceDriverConfigs();
			for (ResourceDriverConfig driver: drivers) {
				GwtSchedule gwtSchedule;
				
				NetFolderRoot nfRoot = new NetFolderRoot();
				nfRoot.setId(  driver.getId()  );
				nfRoot.setName(driver.getName());
				
				DriverType driverType = driver.getDriverType();
				nfRoot.setRootType( getRootTypeFromDriverType(driverType));
				nfRoot.setRootPath( driver.getRootPath()                 );
				nfRoot.setProxyName(driver.getAccountName()              );
				nfRoot.setProxyPwd( driver.getPassword()                 );
				
				nfRoot.setUseProxyIdentity(driver.getUseProxyIdentity());
				Long proxyIdentityId = driver.getProxyIdentityId();
				if (null != proxyIdentityId) {
					ProxyIdentity pi = GwtProxyIdentityHelper.getProxyIdentity(bs, proxyIdentityId);
					if (null != pi) {
						nfRoot.setProxyIdentity(GwtProxyIdentityHelper.convertPIToGwtPI(pi));
					}
				}
				
				nfRoot.setHostUrl(                                      driver.getHostUrl()                                      );
				nfRoot.setAllowSelfSignedCerts(                         driver.isAllowSelfSignedCertificate()                    );
				nfRoot.setIsSharePointServer(                           driver.isPutRequiresContentLength()                      );
				nfRoot.setFullSyncDirOnly(                              driver.getFullSyncDirOnly()                              );
				nfRoot.setIndexContent(                                 driver.getIndexContent()                                 );
				nfRoot.setJitsEnabled(                                  driver.isJitsEnabled()                                   );
				nfRoot.setJitsResultsMaxAge(                            driver.getJitsMaxAge()                                   );
				nfRoot.setJitsAclMaxAge(                                driver.getJitsAclMaxAge()                                );
				nfRoot.setAllowDesktopAppToTriggerInitialHomeFolderSync(driver.getAllowDesktopAppToTriggerInitialHomeFolderSync());
				nfRoot.setAllowDesktopAppToTriggerInitialHomeFolderSync(driver.getAllowDesktopAppToTriggerInitialHomeFolderSync());
				
				AuthenticationType authType = driver.getAuthenticationType();
				if (null != authType) {
					nfRoot.setAuthType(GwtAuthenticationType.getType(authType.getValue()));
				}

				// Get the list of principals that can use the net
				// folder root.
				getListOfPrincipals(bs, driver, nfRoot);

				// Get the net folder's sync schedule.
				gwtSchedule = getGwtSyncSchedule(bs, driver);
				nfRoot.setSyncSchedule(gwtSchedule);

				listOfNetFolderRoots.add(nfRoot);
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

	/*
	 * For the given Binder, return a GwtSchedule object that
	 * represents the binder's sync schedule.
	 */
	private static GwtSchedule getGwtSyncSchedule(AllModulesInjected bs, Binder binder) {
		if (binder == null) {
			return null;
		}
		
		// Get the ScheduleInfo for the given binder.
		ScheduleInfo scheduleInfo = NetFolderHelper.getMirroredFolderSynchronizationSchedule(binder.getId());
		GwtSchedule gwtSchedule = GwtServerHelper.getGwtSyncSchedule(scheduleInfo);
		return gwtSchedule;
	}
	
	/*
	 * For the given ResourceDriverConfig, return a GwtSchedule object
	 * that represents the net folder server's sync schedule.
	 */
	private static GwtSchedule getGwtSyncSchedule(AllModulesInjected bs, ResourceDriverConfig rdConfig) {
		if (rdConfig == null) {
			return null;
		}
		
		// Get the ScheduleInfo for the given net folder server.
		ScheduleInfo scheduleInfo = NetFolderHelper.getNetFolderServerSynchronizationSchedule(rdConfig.getId());
		GwtSchedule gwtSchedule = GwtServerHelper.getGwtSyncSchedule(scheduleInfo);
		return gwtSchedule;
	}
	
	/*
	 */
	private static GwtJitsNetFolderConfig getJitsSettings(Binder binder) {
		GwtJitsNetFolderConfig jitsSettings = new GwtJitsNetFolderConfig();
		if (binder != null) {
			jitsSettings.setJitsEnabled(  binder.isJitsEnabled()   );
			jitsSettings.setResultsMaxAge(binder.getJitsMaxAge()   );
			jitsSettings.setAclMaxAge(    binder.getJitsAclMaxAge());
		}
		return jitsSettings;
	}
	
	/*
	 */
	@SuppressWarnings("unchecked")
	private static void getListOfPrincipals(AllModulesInjected bs, ResourceDriverConfig driver, NetFolderRoot nfRoot) {
		AdminModule am = bs.getAdminModule();
		List<Function> functions = am.getFunctions( ObjectKeys.ROLE_TYPE_ZONE );
		List<WorkAreaFunctionMembership> memberships = am.getWorkAreaFunctionMemberships(driver);
		WorkAreaFunctionMembership membership = null;
		for (Function f: functions) {
			if (ObjectKeys.FUNCTION_CREATE_FILESPACES_INTERNALID.equals(f.getInternalId())) {
				for (WorkAreaFunctionMembership m:  memberships) {
					if (f.getId().equals(m.getFunctionId())) {
						membership = m;
						break;
					}
				}
			}
		}
		
		List<Principal> members = new ArrayList<Principal>();
		if (membership != null) {
			members = ResolveIds.getPrincipals(membership.getMemberIds());
		}
		
		for (Principal p: members) {
			if (p instanceof User) {
				User u = ((User) p);
				GwtUser gwtUser = new GwtUser();
				gwtUser.setInternal(      u.getIdentityInfo().isInternal());
				gwtUser.setUserId(        p.getId()                       );
				gwtUser.setName(          p.getName()                     );
				gwtUser.setTitle(         Utils.getUserTitle(p)           );
				gwtUser.setWorkspaceTitle(u.getWSTitle()                  );
				gwtUser.setEmail(         p.getEmailAddress()             );
				nfRoot.addPrincipal(gwtUser);
			}
			
			else if (p instanceof Group) {
				Group g = ((Group) p);
				GwtGroup gwtGroup = new GwtGroup();
				gwtGroup.setInternal(g.getIdentityInfo().isInternal());
				gwtGroup.setId(      p.getId().toString()            );
				gwtGroup.setName(    p.getName()                     );
				gwtGroup.setTitle(   p.getTitle()                    );
				gwtGroup.setDn(      p.getForeignName()              );
				Description desc = p.getDescription();
				if (desc != null) {
					gwtGroup.setDesc(desc.getText());
				}
				gwtGroup.setGroupType(GwtServerHelper.getGroupType(p));
				nfRoot.addPrincipal(gwtGroup);
			}
		}
	}
	
	/*
	 * Return a NetFolder object for the given net folder ID.  We
	 * won't get all of the information about the net folder.  Just a
	 * basic set of info.
	 */
	private static NetFolder getNetFolderWithMinimalInfo(AllModulesInjected bs, NetFolderConfig nfc) {
		NetFolder netFolder = new NetFolder();
		netFolder.setId(nfc.getTopFolderId());
		
		netFolder.setNetFolderRootName((nfc.getResourceDriver() != null)? nfc.getResourceDriver().getName() : null);
		netFolder.setRelativePath(nfc.getResourcePath());
		netFolder.setStatus(getNetFolderSyncStatus(netFolder.getId()));
		netFolder.setIsHomeDir(nfc.isHomeDir());
		
		// Is this a home directory net folder?
		String name = nfc.getName();
		String displayName = name;
		if (nfc.isHomeDir()) {
			// Yes!
			Binder nfb = NetFolderUtil.getNetFolderTopBinder(nfc);
			Principal owner = nfb.getOwner();
			if (owner != null) {
				String title = owner.getTitle();
				if (MiscUtil.hasString(title)) {
					displayName = (nfb.getTitle() + " (" + title + ")");
				}
			}
		}
		
		netFolder.setName(name);
		netFolder.setDisplayName(displayName);
		
		netFolder.setIndexContent(nfc.getIndexContent());
		netFolder.setInheritIndexContentSetting(nfc.getUseInheritedIndexContent());
		netFolder.setInheritJitsSettings(nfc.getUseInheritedJitsSettings());
		netFolder.setFullSyncDirOnly(nfc.getFullSyncDirOnly());
		
		// Get the data sync settings.
		NetFolderDataSyncSettings dataSyncSettings = getDataSyncSettings(bs, nfc);
		netFolder.setDataSyncSettings(dataSyncSettings);

		return netFolder;
	}
	
	/**
	 * Return a NetFolder object for the given net folder ID.
	 * 
	 * @param bs
	 * @param netFolderId
	 * 
	 * @return
	 */
	public static NetFolder getNetFolder(AllModulesInjected bs, Long netFolderId) {
		Binder binder = bs.getBinderModule().getBinder(netFolderId);
		NetFolderConfig nfc = binder.getNetFolderConfig();
		NetFolder netFolder = getNetFolderWithMinimalInfo(bs, nfc);
		
		// Get the net folder's sync schedule configuration.
		GwtNetFolderSyncScheduleConfig config = new GwtNetFolderSyncScheduleConfig();
		GwtSchedule gwtSchedule = getGwtSyncSchedule(bs, binder);
		config.setSyncSchedule(gwtSchedule);
		
		// Get the sync schedule option
		NetFolderSyncScheduleOption nfSyncScheduleOption = NetFolderSyncScheduleOption.USE_NET_FOLDER_SERVER_SCHEDULE;
		SyncScheduleOption syncScheduleOption = binder.getSyncScheduleOption();
		if (syncScheduleOption != null) {
			switch (syncScheduleOption) {
			case useNetFolderServerSchedule:  nfSyncScheduleOption = NetFolderSyncScheduleOption.USE_NET_FOLDER_SERVER_SCHEDULE; break;
			case useNetFolderSchedule:        nfSyncScheduleOption = NetFolderSyncScheduleOption.USE_NET_FOLDER_SCHEDULE;        break;
			}
		}
		
		else {
			// The binder doesn't have a value for the
			// syncScheduleOption field.  Determine what the value is
			// based on whether or not the net folder has a schedule
			// defined.
			if (gwtSchedule != null && gwtSchedule.getEnabled()) {
				nfSyncScheduleOption = NetFolderSyncScheduleOption.USE_NET_FOLDER_SCHEDULE;
			}
		}
		
		config.setSyncScheduleOption(nfSyncScheduleOption);
		netFolder.setSyncScheduleConfig(config);
		
		// Get the rights associated with this net folder.
		ArrayList<GwtRole> listOfRoles = getNetFolderRights(bs, binder);
		netFolder.setRoles(listOfRoles);
		
		// Get the JITS settings
		GwtJitsNetFolderConfig jitsSettings = getJitsSettings(binder);
		netFolder.setJitsConfig(jitsSettings);

		// Get the full sync directory only setting.
		netFolder.setFullSyncDirOnly(binder.getFullSyncDirOnly());
		
		return netFolder;
	}
	
	/*
	 * Return the roles (rights) that have been set on this net folder.
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<GwtRole> getNetFolderRights(AllModulesInjected bs, Binder binder) {
		ArrayList<GwtRole> listOfRoles = new ArrayList<GwtRole>();
		GwtRole role = new GwtRole();
		role.setType(GwtRoleType.AllowAccess);
		listOfRoles.add(role);
		role = new GwtRole();
		role.setType(GwtRoleType.ShareExternal);
		listOfRoles.add(role);
		role = new GwtRole();
		role.setType(GwtRoleType.ShareForward);
		listOfRoles.add(role);
		role = new GwtRole();
		role.setType(GwtRoleType.ShareInternal);
		listOfRoles.add(role);
		role = new GwtRole();
		role.setType(GwtRoleType.SharePublic);
		listOfRoles.add(role);
		role = new GwtRole();
		role.setType(GwtRoleType.SharePublicLinks);
		listOfRoles.add(role);
		
		AdminModule am = bs.getAdminModule();
		for (GwtRole nextRole:  listOfRoles) {
			// Can we find the function for the given role?
			Long fnId = GwtServerHelper.getFunctionIdFromRole(bs, nextRole);
			if (null == fnId) {
				// No!
				GwtLogHelper.error(m_logger, "In GwtNetFolderHelper.getNetFolderRights(), could not find function for role: " + nextRole.getType());
				continue;
			}

			// Get the role's membership.
			WorkAreaFunctionMembership membership = am.getWorkAreaFunctionMembership(binder, fnId);
			if (membership == null) {
				continue;
			}
			
			// Get the member IDs.
			Set<Long> memberIds = membership.getMemberIds();
			if (memberIds == null) {
				continue;
			}
			
			List principals = null;
			try {
				principals = ResolveIds.getPrincipals(memberIds);
			}
			catch (Exception ex) {
				// Nothing to do
			}
			
			if (!(MiscUtil.hasItems(principals))) {
				continue;
			}

			for (Object nextObj:  principals) {
				if (nextObj instanceof Principal) {
					Principal nextPrincipal = ((Principal) nextObj);
					if (nextPrincipal instanceof Group) {
						Group nextGroup = ((Group) nextPrincipal);
						GwtGroup gwtGroup = new GwtGroup();
						gwtGroup.setInternal(nextGroup.getIdentityInfo().isInternal());
						gwtGroup.setId(nextGroup.getId().toString());
						gwtGroup.setName(nextGroup.getName());
						gwtGroup.setTitle(nextGroup.getTitle());
						gwtGroup.setDn(nextGroup.getForeignName());
						Description desc = nextGroup.getDescription();
						if (desc != null) {
							gwtGroup.setDesc(desc.getText());
						}
						gwtGroup.setGroupType(GwtServerHelper.getGroupType(nextGroup));
						nextRole.addMember(gwtGroup);
					}
					
					else if (nextPrincipal instanceof User) {
						User user = ((User) nextPrincipal);
						GwtUser gwtUser = new GwtUser();
						gwtUser.setInternal(user.getIdentityInfo().isInternal());
						gwtUser.setUserId(user.getId());
						gwtUser.setName(user.getName());
						gwtUser.setTitle(Utils.getUserTitle(user));
						gwtUser.setWorkspaceTitle(user.getWSTitle());
						gwtUser.setEmail(user.getEmailAddress());
						nextRole.addMember(gwtUser);
					}
				}
			}
		}

		return listOfRoles;
	}
	
	
	/**
	 * Get the sync statistics for the given net folder.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public static NetFolderSyncStatistics getNetFolderSyncStatistics(Long binderId) {
		NetFolderSyncStatistics syncStatistics = new NetFolderSyncStatistics();
		if (binderId != null) {
			BinderState binderState = ((BinderState) getCoreDao().load(BinderState.class, binderId));
            if (binderState != null) {
    			FullSyncStats syncStats = binderState.getFullSyncStats();
    			if (syncStats != null) {
    				syncStatistics.setCountEntryExpunge(      syncStats.getCountEntryExpunge()      );
    				syncStatistics.setCountFailure(           syncStats.getCountFailure()           );
    				syncStatistics.setCountFileAdd(           syncStats.getCountFileAdd()           );
    				syncStatistics.setCountFileExpunge(       syncStats.getCountFileExpunge()       );
    				syncStatistics.setCountFileModify(        syncStats.getCountFileModify()        );
    				syncStatistics.setCountFiles(             syncStats.getCountFiles()             );
    				syncStatistics.setCountFileSetAcl(        syncStats.getCountFileSetAcl()        );
    				syncStatistics.setCountFileSetOwnership(  syncStats.getCountFileSetOwnership()  );
    				syncStatistics.setCountFolderAdd(         syncStats.getCountFolderAdd()         );
    				syncStatistics.setCountFolderExpunge(     syncStats.getCountFolderExpunge()     );
    				syncStatistics.setCountFolderMaxQueue(    syncStats.getCountFolderMaxQueue()    );
    				syncStatistics.setCountFolderProcessed(   syncStats.getCountFolderProcessed()   );
    				syncStatistics.setCountFolders(           syncStats.getCountFolders()           );
    				syncStatistics.setCountFolderSetAcl(      syncStats.getCountFolderSetAcl()      );
    				syncStatistics.setCountFolderSetOwnership(syncStats.getCountFolderSetOwnership());
    				syncStatistics.setDirOnly(                syncStats.getDirOnly()                );
    				syncStatistics.setEnumerationFailed(      syncStats.getEnumerationFailed()      );
    				syncStatistics.setStatusIpv4Address(      syncStats.getStatusIpv4Address()      );
    				
    				Long value = null;
    				Date date = syncStats.getEndDate();
    				if (date != null) {
    					value = new Long(date.getTime());
    				}
    				syncStatistics.setEndDate(value);
    				
    				value = null;
    				date = syncStats.getStartDate();
    				if (date != null) {
    					value = new Long(date.getTime());
    				}
    				syncStatistics.setStartDate(value);
    				
    				value = null;
    				date = syncStats.getStatusDate();
    				if (date != null) {
    					value = new Long(date.getTime());
    				}
    				syncStatistics.setStatusDate(value);
    			}
            }
		}
		
		return syncStatistics;
	}
	
	/**
	 * Get the sync status of the given net folder by converting a
	 * FullSyncStatus object into a NetFolderSyncStatus object.
	 * 
	 * @param binderId
	 * 
	 * @return
	 */
	public static NetFolderSyncStatus getNetFolderSyncStatus(Long binderId) {
		NetFolderSyncStatus status = NetFolderSyncStatus.SYNC_NEVER_RUN;
		if (binderId != null) {
			BinderState binderState = ((BinderState) getCoreDao().load(BinderState.class, binderId));
            if (binderState != null) {
    			FullSyncStats syncStats = binderState.getFullSyncStats();
    			if (syncStats != null) {
    				FullSyncStatus syncStatus = syncStats.getStatus();
    				if (syncStatus != null) {
    					switch (syncStatus) {
    					case ready:
    					case taken:     status = NetFolderSyncStatus.WAITING_TO_BE_SYNCD; break;
    					case canceled:  status = NetFolderSyncStatus.SYNC_CANCELED;       break;
    					case finished:  status = NetFolderSyncStatus.SYNC_COMPLETED;      break;
    					case started:   status = NetFolderSyncStatus.SYNC_IN_PROGRESS;    break;
    					case stopped:   status = NetFolderSyncStatus.SYNC_STOPPED;        break;
    					case deleting:  status = NetFolderSyncStatus.DELETE_IN_PROGRESS;  break;
    					case aborted:   status = NetFolderSyncStatus.UNKNOWN;             break;
    					}
    				}
    			}
            }
		}
		
		return status;
	}
	
	/**
	 * Return the number of net folders that match the given criteria.
	 * 
	 * @param bs
	 * @param selectSpec
	 * 
	 * @return
	 */
	public static int getNumberOfNetFolders(AllModulesInjected bs, NetFolderSelectSpec selectSpec) {
		int numNetFolders = NetFolderHelper.getNumberOfNetFolders(
			bs.getBinderModule(),
			bs.getWorkspaceModule(),
			selectSpec);

		return numNetFolders;
	}
	
	/**
	 * ?
	 * 
	 * @param driverType
	 * 
	 * @return
	 */
	public static NetFolderRootType getRootTypeFromDriverType(DriverType driverType) {
		switch (driverType) {
		case windows_server:    return NetFolderRootType.WINDOWS;
		case cloud_folders:     return NetFolderRootType.CLOUD_FOLDERS;
		case famt:              return NetFolderRootType.FAMT;
		case filesystem:        return NetFolderRootType.FILE_SYSTEM;
		case netware:           return NetFolderRootType.NETWARE;
		case oes:               return NetFolderRootType.OES;
		case oes2015:           return NetFolderRootType.OES2015;
		case share_point_2010:  return NetFolderRootType.SHARE_POINT_2010;
		case share_point_2013:  return NetFolderRootType.SHARE_POINT_2013;
		case webdav:            return NetFolderRootType.WEB_DAV;
		default:                return NetFolderRootType.UNKNOWN;
		}
	}
	
	/**
	 * For the given NetFolderSyncScheduleOption, return a
	 * SyncScheduleOption.
	 * 
	 * @param options
	 * 
	 * @return
	 */
	public static SyncScheduleOption getSyncScheduleOptionFromGwtSyncScheduleOption(NetFolderSyncScheduleOption option) {
		if (option != null) {
			switch (option) {
			default:
			case USE_NET_FOLDER_SCHEDULE:         return SyncScheduleOption.useNetFolderSchedule;
			case USE_NET_FOLDER_SERVER_SCHEDULE:  return SyncScheduleOption.useNetFolderServerSchedule;
			}
		}
		
		else {
			return null;
		}
	}
	
	/**
	 * Modify the net folder from the given data.
	 * 
	 * @param bs
	 * @param netFolder
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static NetFolder modifyNetFolder(AllModulesInjected bs, NetFolder netFolder) throws GwtTeamingException {
		try {
			ScheduleInfo       scheduleInfo       = null;
			SyncScheduleOption syncScheduleOption = null;
			
			// Get the schedule information.
			GwtNetFolderSyncScheduleConfig config = netFolder.getSyncScheduleConfig();
			if (config != null) {
				scheduleInfo       = GwtServerHelper.getScheduleInfoFromGwtSchedule(config.getSyncSchedule()      );
				syncScheduleOption = getSyncScheduleOptionFromGwtSyncScheduleOption(config.getSyncScheduleOption());
			}
			
			NetFolderDataSyncSettings dataSyncSettings = netFolder.getDataSyncSettings();
			
			NetFolderHelper.modifyNetFolder(
				bs.getBinderModule(),
				bs.getFolderModule(),
				bs.getNetFolderModule(),
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
				dataSyncSettings.getInheritAllowDesktopAppToTriggerSync());

			// Set the rights on the net folder
			if (!(netFolder.getIsHomeDir())) {
				setNetFolderRights(bs, netFolder.getId(), netFolder.getRoles());
			}
			
			// Save the data sync settings.
			Binder binder = bs.getBinderModule().getBinder(netFolder.getId());
			saveDataSyncSettings(bs, binder.getNetFolderConfig(), dataSyncSettings);
			
			// Save the JITS settings.
			GwtJitsNetFolderConfig settings = netFolder.getJitsConfig();
			if (settings != null) {
				NetFolderHelper.saveJitsSettings(
					bs.getNetFolderModule(),
					binder.getNetFolderConfig(),
					netFolder.getInheritJitsSettings(),
					settings.getJitsEnabled(),
					settings.getAclMaxAge(),
					settings.getResultsMaxAge());
			}
		}
		
		catch (Exception ex) {
			GwtTeamingException gtEx;
			if (ex instanceof TitleException) {
				String[] args = new String[] {netFolder.getName()};
				gtEx = GwtLogHelper.getGwtClientException();
				gtEx.setAdditionalDetails(NLT.get("netfolder.duplicate.name", args));
			}
			
			else if (ex instanceof IllegalCharacterInNameException) {
				gtEx = GwtLogHelper.getGwtClientException();
				gtEx.setAdditionalDetails(NLT.get("netfolder.name.illegal.characters"));
			}
			
			else {
				gtEx = GwtLogHelper.getGwtClientException(ex);
			}
			
			GwtLogHelper.error(m_logger, "Error modifying net folder: " + netFolder.getName(), ex);
			throw gtEx;				
		}
		
		return netFolder;
	}
	
	
	/**
	 * Modify the net folder root from the given data.
	 * 
	 * @param bs
	 * @param netFolderRoot
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingExcepiton
	 */
	public static NetFolderRoot modifyNetFolderRoot(AllModulesInjected bs, NetFolderRoot netFolderRoot) throws GwtTeamingException {
		try {
			ScheduleInfo scheduleInfo = GwtServerHelper.getScheduleInfoFromGwtSchedule(netFolderRoot.getSyncSchedule());
			DriverType driverType = getDriverType(netFolderRoot.getRootType());
			AuthenticationType authType = getAuthType(netFolderRoot.getAuthType());
			
			GwtProxyIdentity pi = netFolderRoot.getProxyIdentity();
			Long proxyIdentityId = ((null == pi) ? null : pi.getId());
			NetFolderHelper.modifyNetFolderRoot(
				bs.getAdminModule(),
				bs.getResourceDriverModule(),
				bs.getProfileModule(),
				bs.getBinderModule(),
				bs.getWorkspaceModule(),
				bs.getFolderModule(),
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
				scheduleInfo);
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
	private static void saveDataSyncSettings(AllModulesInjected bs, NetFolderConfig nfc, NetFolderDataSyncSettings settings) {
		if ((null != nfc) && (null != settings)) {
			nfc.setAllowDesktopAppToSyncData(settings.getAllowDesktopAppToSyncData());
			nfc.setAllowDesktopAppToTriggerInitialHomeFolderSync(settings.getAllowDesktopAppToTriggerSync());
			nfc.setUseInheritedDesktopAppTriggerSetting(settings.getInheritAllowDesktopAppToTriggerSync());
			
	   		if (false) {
	   			// Not writing anything as per bug 816823.
	   			nfc.setAllowMobileAppsToSyncData(settings.getAllowMobileAppsToSyncData());
	   		}

			try {
				bs.getNetFolderModule().modifyNetFolder(nfc);
			}
			
			catch (Exception ex) {
				GwtLogHelper.error(m_logger, "In saveDataSyncSettings(), call to modifyNetFolder() failed. " + ex.toString());
			}
		}
	}

	/*
	 */
	private static void setNetFolderRights(AllModulesInjected bs, Long binderId, ArrayList<GwtRole> roles) {
		if (binderId == null && roles == null) {
			GwtLogHelper.error(m_logger, "In GwtNetFolderHelper.setNetFolderRights(), invalid parameters");
		}
		
		// Get the binder's work area
		Binder binder = bs.getBinderModule().getBinder(binderId);
		
		AdminModule am = bs.getAdminModule();
		for (GwtRole nextRole : roles) {
			// Can we find the function for the given role?
			Long fnId = GwtServerHelper.getFunctionIdFromRole(bs, nextRole);
			if (null == fnId) {
				// No!
				GwtLogHelper.error(m_logger, "In GwtNetFolderHelper.setNetFolderRights(), could not find function for role: " + nextRole.getType());
				continue;
			}

			// Reset the function's membership.
			am.resetWorkAreaFunctionMemberships(binder, fnId, nextRole.getMemberIds());
		}
		
		// Re-index this binder.
		// bs.getBinderModule().indexBinder(binderId, false);	// 20130122:  Commented out with the fix for bug#799512 as per Jong.
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
				if (bs.getFolderModule().enqueueFullSynchronize(nextNetFolder.getId())) {
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
