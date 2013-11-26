/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.resourcedriver.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.jobs.DefaultNetFolderServerSynchronization;
import org.kablink.teaming.jobs.NetFolderServerSynchronization;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionManager;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaFunctionMembershipManager;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.web.servlet.listener.ContextListenerPostSpring;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.util.search.Constants;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


public class ResourceDriverModuleImpl implements ResourceDriverModule {
	
	private Log logger = LogFactory.getLog(getClass());
	
	private CoreDao coreDao;
    private FunctionManager functionManager;
    private WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
    private ResourceDriverManager resourceDriverManager;
    private AccessControlManager accessControlManager;
	private TransactionTemplate transactionTemplate;
	private FolderModule folderModule;
	private BinderModule binderModule;
	private WorkspaceModule workspaceModule;
	
    protected CoreDao getCoreDao() {
		return coreDao;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	protected FunctionManager getFunctionManager() {
		return functionManager;
	}
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
	protected WorkAreaFunctionMembershipManager getWorkAreaFunctionMembershipManager() {
		return workAreaFunctionMembershipManager;
	}
	public void setWorkAreaFunctionMembershipManager(
			WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager) {
		this.workAreaFunctionMembershipManager = workAreaFunctionMembershipManager;
	}
	protected ResourceDriverManager getResourceDriverManager() {
		return resourceDriverManager;
	}
	public void setResourceDriverManager(ResourceDriverManager resourceDriverManager) {
		this.resourceDriverManager = resourceDriverManager;
	}
	protected AccessControlManager getAccessControlManager() {
		return accessControlManager;
	}
	public void setAccessControlManager(AccessControlManager accessControlManager) {
		this.accessControlManager = accessControlManager;
	}
	protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	/**
	 * 
	 */
	protected FolderModule getFolderModule()
	{
		return folderModule;
	}
	
	/**
	 * 
	 */
	public void setFolderModule( FolderModule folderModule )
	{
		this.folderModule = folderModule;
	}

	/**
	 * 
	 */
	protected BinderModule getBinderModule()
	{
		return binderModule;
	}
	
	/**
	 * 
	 */
	public void setBinderModule( BinderModule binderModule )
	{
		this.binderModule = binderModule;
	}

	/**
	 * 
	 */
	protected WorkspaceModule getWorkspaceModule()
	{
		return workspaceModule;
	}
	
	/**
	 * 
	 */
	public void setWorkspaceModule( WorkspaceModule workspaceModule )
	{
		this.workspaceModule = workspaceModule;
	}

	@Override
	public boolean testAccess(ResourceDriverOperation operation) {
		try {
			checkAccess(operation);
			return true;
		} catch(AccessControlException ace) {}
		return false;
	}

	@Override
	public void checkAccess(ResourceDriverOperation operation)
			throws AccessControlException {
		switch (operation) {
		case manageResourceDrivers:
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			if (getAccessControlManager().testOperation(zoneConfig, WorkAreaOperation.MANAGE_RESOURCE_DRIVERS)) {
				return;
			}
			getAccessControlManager().checkOperation(zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION);
			break;
		default:
			throw new NotSupportedException(operation.toString(),
					"checkAccess");
		}
	}
	
	@Override
	public boolean testAccess(ResourceDriverConfig resourceDriver, ResourceDriverOperation operation) {
		try {
			checkAccess(resourceDriver, operation);
			return true;
		} catch(AccessControlException ace) {}
		return false;
	}

	@Override
	public void checkAccess(ResourceDriverConfig resourceDriver, ResourceDriverOperation operation)
			throws AccessControlException {
		switch (operation) {
		case createFilespace:
			if (getAccessControlManager().testOperation(resourceDriver, WorkAreaOperation.CREATE_FILESPACE)) {
				return;
			}
			ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
			getAccessControlManager().checkOperation(zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION);
			break;
		default:
			throw new NotSupportedException(operation.toString(),
					"checkAccess");
		}
	}
	
	public List<ResourceDriverConfig> getAllResourceDriverConfigs() {
		return getResourceDriverManager().getAllResourceDriverConfigs();
	}
	
	public ResourceDriverConfig addResourceDriver(final String name, final DriverType type, final String rootPath,
			final Set<Long> memberIds, final Map options) 
 			throws AccessControlException, RDException {
		//Check that the user has the right to do this operation
		checkAccess(ResourceDriverOperation.manageResourceDrivers);
		
		//Look to see if there is a driver by this name already
		ResourceDriver d = null;
		try {
			d = getResourceDriverManager().getDriver(name);
			throw new RDException(NLT.get(RDException.DUPLICATE_RESOURCE_DRIVER_NAME, new String[] {name}), name);
		} catch(FIException fie) {}
		
	   	//Create the new resource driver
    	SimpleProfiler.start("addResourceDriverConfig");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
				Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
				ResourceDriverConfig newResourceDriver = new ResourceDriverConfig();
			   	newResourceDriver.setName(name);
			   	newResourceDriver.setDriverType(type);
			   	newResourceDriver.setZoneId(zoneId);
			   	newResourceDriver.setRootPath(rootPath);
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_READ_ONLY)) {
			   		newResourceDriver.setReadOnly((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_READ_ONLY));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_HOST_URL)) {
			   		newResourceDriver.setHostUrl((String)options.get(ObjectKeys.RESOURCE_DRIVER_HOST_URL));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE)) {
			   		newResourceDriver.setAllowSelfSignedCertificate((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE)) {
			   		newResourceDriver.setSynchTopDelete((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH)) {
			   		newResourceDriver.setPutRequiresContentLength((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME)) {
			   		newResourceDriver.setAccountName((String)options.get(ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_PASSWORD)) {
			   		newResourceDriver.setPassword((String)options.get(ObjectKeys.RESOURCE_DRIVER_PASSWORD));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SERVER_NAME)) {
			   		newResourceDriver.setServerName((String)options.get(ObjectKeys.RESOURCE_DRIVER_SERVER_NAME));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SERVER_IP)) {
			   		newResourceDriver.setServerIP((String)options.get(ObjectKeys.RESOURCE_DRIVER_SERVER_IP));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SHARE_NAME)) {
			   		newResourceDriver.setShareName((String)options.get(ObjectKeys.RESOURCE_DRIVER_SHARE_NAME));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_VOLUME)) {
			   		newResourceDriver.setVolume((String)options.get(ObjectKeys.RESOURCE_DRIVER_VOLUME));
			   	}
			   	newResourceDriver.setModifiedOn(new Date());	//Set the date of last modification to "now"
				getCoreDao().save(newResourceDriver);
				
				//Set up the access controls for this new resource driver
				List functions = getFunctionManager().findFunctions(zoneId);
				Function manageResourceDriversFunction = null;
				for (int i = 0; i < functions.size(); i++) {
					Function function = (Function)functions.get(i);
					if (function.getInternalId() != null && 
							ObjectKeys.FUNCTION_CREATE_FILESPACES_INTERNALID.equals(function.getInternalId())) {
						//We have found the pseudo role
						manageResourceDriversFunction = function;
						break;
					}
				}
				
				if (manageResourceDriversFunction != null) {
					WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
					membership.setZoneId(zoneId);
					membership.setWorkAreaId(newResourceDriver.getWorkAreaId());
					membership.setWorkAreaType(newResourceDriver.getWorkAreaType());
					membership.setFunctionId(manageResourceDriversFunction.getId());
					membership.setMemberIds(memberIds);
					getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
				}
				return null;
    		}
    	});
    	SimpleProfiler.stop("addResourceDriverConfig");

		//Add this new resource driver to the list of drivers
		getResourceDriverManager().resetResourceDriverList();
		
		ResourceDriverConfig rdc = getResourceDriverManager().getDriverConfig(name);
		return rdc;
	}
	
	public ResourceDriverConfig modifyResourceDriver(final String name, final DriverType type, final String rootPath,
			final Set<Long> memberIds, final Map options) 
 			throws AccessControlException, RDException {
		//Check that the user has the right to do this operation
		checkAccess(ResourceDriverOperation.manageResourceDrivers);
		
		//Find the ResourceDriver
		final ResourceDriverConfig rdc = getResourceDriverManager().getDriverConfig(name);
		if (rdc == null) {
			throw new RDException(NLT.get(RDException.NO_SUCH_RESOURCE_DRIVER_NAME, new String[] {name}), name);
		}
		
	   	//Modify this resource driver config
    	SimpleProfiler.start("modifyResourceDriverConfig");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
				rdc.setDriverType(type);
				rdc.setRootPath(rootPath);
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_READ_ONLY)) {
			   		rdc.setReadOnly((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_READ_ONLY));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_HOST_URL)) {
			   		rdc.setHostUrl((String)options.get(ObjectKeys.RESOURCE_DRIVER_HOST_URL));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE)) {
			   		rdc.setAllowSelfSignedCertificate((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_ALLOW_SELF_SIGNED_CERTIFICATE));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE)) {
			   		rdc.setSynchTopDelete((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH)) {
			   		rdc.setPutRequiresContentLength((Boolean)options.get(ObjectKeys.RESOURCE_DRIVER_PUT_REQUIRES_CONTENT_LENGTH));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME)) {
			   		rdc.setAccountName((String)options.get(ObjectKeys.RESOURCE_DRIVER_ACCOUNT_NAME));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_PASSWORD)) {
			   		rdc.setPassword((String)options.get(ObjectKeys.RESOURCE_DRIVER_PASSWORD));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SERVER_NAME)) {
			   		rdc.setServerName((String)options.get(ObjectKeys.RESOURCE_DRIVER_SERVER_NAME));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SERVER_IP)) {
			   		rdc.setServerIP((String)options.get(ObjectKeys.RESOURCE_DRIVER_SERVER_IP));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_SHARE_NAME)) {
			   		rdc.setShareName((String)options.get(ObjectKeys.RESOURCE_DRIVER_SHARE_NAME));
			   	}
			   	if (options.containsKey(ObjectKeys.RESOURCE_DRIVER_VOLUME)) {
			   		rdc.setVolume((String)options.get(ObjectKeys.RESOURCE_DRIVER_VOLUME));
			   	}
			   	rdc.setModifiedOn(new Date());	//Set the date of last modification to "now"
				getCoreDao().save(rdc);
    			return null;
    		}
    	});
    	SimpleProfiler.stop("modifyResourceDriverConfig");
				
    	SimpleProfiler.start("deleteResourceDriverConfigAcl");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
    	     	//Delete the membership ACL to this driver config object
    	    	getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMemberships(
    	    			RequestContextHolder.getRequestContext().getZoneId(), rdc);

    			return null;
    		}
    	});
    	SimpleProfiler.stop("deleteResourceDriverConfigAcl");

    	SimpleProfiler.start("addResourceDriverConfigAcl");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
				//Then add in the new ACL
				Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
				List functions = getFunctionManager().findFunctions(zoneId);
				Function createFilespacesFunction = null;
				for (int i = 0; i < functions.size(); i++) {
					Function function = (Function)functions.get(i);
					if (function.getInternalId() != null && 
							ObjectKeys.FUNCTION_CREATE_FILESPACES_INTERNALID.equals(function.getInternalId())) {
						//We have found the pseudo role
						createFilespacesFunction = function;
						break;
					}
				}
    	    	if (createFilespacesFunction != null) {
					WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
					membership.setZoneId(zoneId);
					membership.setWorkAreaId(rdc.getWorkAreaId());
					membership.setWorkAreaType(rdc.getWorkAreaType());
					membership.setFunctionId(createFilespacesFunction.getId());
					membership.setMemberIds(memberIds);
					getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
				}
    			return null;
    		}
    	});
    	SimpleProfiler.stop("addResourceDriverConfigAcl");
		

		//Add this new resource driver to the list of drivers
		getResourceDriverManager().resetResourceDriverList();
		
		return rdc;
	}
	
	public void deleteResourceDriver(final String name) 
			throws AccessControlException, RDException {
		//Check that the user has the right to do this operation
		checkAccess(ResourceDriverOperation.manageResourceDrivers);
		
		//Find the ResourceDriver
		final ResourceDriverConfig rdc = getResourceDriverManager().getDriverConfig(name);
		if (rdc == null) {
			throw new RDException(NLT.get(RDException.NO_SUCH_RESOURCE_DRIVER_NAME, new String[] {name}), name);
		}
		
		Long driverId = rdc.getId();
		
	   	//Modify this resource driver config
    	SimpleProfiler.start("deleteResourceDriverConfig");
    	// 	The following part requires update database transaction.
    	getTransactionTemplate().execute(new TransactionCallback() {
    		@Override
			public Object doInTransaction(TransactionStatus status) {
    	     	//remove ACLs to this driver config object
    	    	getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMemberships(
    	    			RequestContextHolder.getRequestContext().getZoneId(), rdc);

				//Now delete the actual config object
    	    	getCoreDao().delete(rdc);
    			return null;
    		}
    	});
    	SimpleProfiler.stop("deleteResourceDriverConfig");
		
		//Remove this resource driver from the list of drivers
		getResourceDriverManager().resetResourceDriverList();
		
		// Finally, delete the background job associated with this driver
		
		NetFolderServerSynchronization job = NetFolderHelper.getNetFolderServerSynchronizationScheduleObject();
		job.deleteJob(driverId);
	}

	/**
	 * Set the sync schedule for this driver.
	 */
	@Override
	public void setSynchronizationSchedule( ScheduleInfo config, Long driverId )
	{
		checkAccess( ResourceDriverOperation.manageResourceDrivers );
    
    	// data is stored with job
		NetFolderHelper.getNetFolderServerSynchronizationScheduleObject().setScheduleInfo( config, driverId );
    }    

	/**
	 * Synchronize all of the net folders associated with the given net folder server.
	 * If a net folder has a sync schedule enabled, that net folder will not be synchronized.
	 */
	@SuppressWarnings("rawtypes")
	private boolean doSynchronize(
		ResourceDriverConfig rdConfig,
		boolean excludeFoldersWithSchedule,
		StatusTicket statusTicket )
	{
		FolderModule folderModule;
		List<Long> listOfNetFolderIds;
		
		if ( rdConfig == null )
			return false;
		
		folderModule = getFolderModule();

		// Find all of the net folders that reference this net folder server.
		listOfNetFolderIds = NetFolderHelper.getAllNetFolders(
													getBinderModule(),
													getWorkspaceModule(),
													rdConfig.getName(),
													true );

		if ( listOfNetFolderIds != null )
		{
			for ( Long binderId:  listOfNetFolderIds )
			{
				ScheduleInfo scheduleInfo;
				
				// Does this net folder have a sync schedule that is enabled?
				scheduleInfo = NetFolderHelper.getMirroredFolderSynchronizationSchedule( binderId );
				if ( excludeFoldersWithSchedule == false || scheduleInfo == null || scheduleInfo.isEnabled() == false )
				{
					try {
						// No, sync this net folder ... only if system shutdown is not in progress
						if(!ContextListenerPostSpring.isShutdownInProgress()) {
							folderModule.fullSynchronize( binderId, statusTicket );
						}
						else {
							// System shutting down. Abort the remaining work and return.
							logger.info("System shutting down. Skipping full sync of net folder '" + binderId + "' and the rest.");
							break;
						}
					}
					catch(Exception e) {
						logger.error("Error during synchronization of net folder '" + binderId + "'", e);
						continue; // Continue to the next net folder to sync.
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Synchronize all of the net folders associated with the given net folder server.
	 * If a net folder has a sync schedule enabled, that net folder will not be synchronized.
	 */
	@Override
	public boolean synchronize(
		String netFolderServerName,
		boolean excludeFoldersWithSchedule, // Should we exclude net folders that have a schedule defined.
		StatusTicket statusTicket ) throws FIException, UncheckedIOException, ConfigurationException
	{
		ResourceDriverConfig rdConfig;

		rdConfig = getResourceDriverManager().getDriverConfig( netFolderServerName );
		if ( rdConfig != null )
		{
			return synchronize( rdConfig.getId(), excludeFoldersWithSchedule, statusTicket );
		}
		
		return false;
	}
	
	/**
	 * Synchronize all of the net folders associated with the given net folder server.
	 * If a net folder has a sync schedule enabled, that net folder will not be synchronized.
	 */
	@Override
	public boolean synchronize(
		Long netFolderServerId,
		boolean excludeFoldersWithSchedule, // Should we exclude net folders that have a schedule defined.
		StatusTicket statusTicket ) throws FIException, UncheckedIOException, ConfigurationException
	{
		try
		{
			ResourceDriverConfig rdConfig = null;
			String rootPath;
			String proxyName;
			String proxyPwd;
			
			try
			{
				rdConfig = (ResourceDriverConfig) getCoreDao().load( ResourceDriverConfig.class, netFolderServerId );
			}
			catch ( Exception e )
			{
				logger.warn( e.toString() );
				return false;
			}
			
			if ( rdConfig == null )
				return false;
			
			// Is everything configured?
			rootPath = rdConfig.getRootPath();
			proxyName = rdConfig.getAccountName();
			proxyPwd = rdConfig.getPassword();
			if ( rootPath != null && rootPath.length() > 0 &&
				 proxyName != null && proxyName.length() > 0 &&
				 proxyPwd != null && proxyPwd.length() > 0 )
			{
				// Yes
				return doSynchronize( rdConfig, excludeFoldersWithSchedule, statusTicket );
			}
			else
			{
				logger.warn( "Did not start synchronization of net folder server, " + rdConfig.getName() + ", because it is not configured completely." );
				return false;
			}
		}
		finally
		{
			// It is important to call this at the end of the processing no matter how it went.
			if( statusTicket != null )
				statusTicket.done();
		}
	}
}
