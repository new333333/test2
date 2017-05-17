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
package org.kablink.teaming.module.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.license.LicenseManager;
import org.kablink.teaming.modelprocessor.ProcessorManager;
import org.kablink.teaming.presence.PresenceManager;
import org.kablink.teaming.relevance.RelevanceManager;
import org.kablink.teaming.runasync.RunAsyncManager;
import org.kablink.teaming.search.LuceneSessionFactory;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;
import org.kablink.teaming.security.acl.AclManager;
import org.kablink.teaming.security.dao.SecurityDao;
import org.kablink.teaming.security.function.FunctionManager;
import org.kablink.teaming.security.function.WorkAreaFunctionMembershipManager;
import org.quartz.Scheduler;


/**
 * This abstract class provides a central place where dependent
 * components are injected. 
 * 
 * Warning: Do NOT inject module components in this class because it
 * could cause recursive dependencies among modules. Only managers
 * and services can be added here.
 * 
 * @author jong
 *
 */
public abstract class CommonDependencyInjection {

	protected Log logger = LogFactory.getLog(getClass());
	protected boolean traceEnabled = logger.isTraceEnabled();
	protected boolean debugEnabled = logger.isDebugEnabled();
	protected boolean infoEnabled = logger.isInfoEnabled();

	protected CoreDao coreDao;
	protected ProfileDao profileDao;
	protected FolderDao folderDao;
	protected SecurityDao securityDao;
	protected FunctionManager functionManager;
	protected AccessControlManager accessControlManager;
	protected AclManager aclManager;
	protected ProcessorManager processorManager;
	protected Scheduler scheduler;
	protected LuceneSessionFactory luceneSessionFactory;
	protected PresenceManager presenceService;
	protected WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
	protected ResourceDriverManager resourceDriverManager;
	protected LicenseManager licenseManager;
	protected AccessTokenManager accessTokenManager;
    protected RelevanceManager relevanceManager;
	protected RunAsyncManager runAsyncManager;

	
	public void setAccessControlManager(AccessControlManager accessControlManager) {
		this.accessControlManager = accessControlManager;
	}
	public void setAclManager(AclManager aclManager) {
		this.aclManager = aclManager;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}
	public void setFolderDao(FolderDao folderDao) {
		this.folderDao = folderDao;
	}
	public void setSecurityDao(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}
	public void setProfileDao(ProfileDao profileDao) {
		this.profileDao = profileDao;
	}
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
	public void setProcessorManager(ProcessorManager processorManager) {
		this.processorManager = processorManager;
	}
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}
	public void setLuceneSessionFactory(LuceneSessionFactory luceneSessionFactory) {
		this.luceneSessionFactory = luceneSessionFactory;
	}
	public void setPresenceService(PresenceManager presenceService) {
		this.presenceService = presenceService;
	}
	public LuceneSessionFactory getLuceneSessionFactory() {
		return luceneSessionFactory;
	}
	public AccessControlManager getAccessControlManager() {
		return accessControlManager;
	}
	public AclManager getAclManager() {
		return aclManager;
	}
	public CoreDao getCoreDao() {
		return coreDao;
	}
	public FolderDao getFolderDao() {
		return folderDao;
	}
	public SecurityDao getSecurityDao() {
		return securityDao;
	}
	public ProfileDao getProfileDao() {
		return profileDao;
	}
	public FunctionManager getFunctionManager() {
		return functionManager;
	}
	public ProcessorManager getProcessorManager() {
		return processorManager;
	}
	public Scheduler getScheduler() {
		return scheduler;
	}
	public PresenceManager getPresenceService() {
		return presenceService;
	}
	public void setWorkAreaFunctionMembershipManager(WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager) {
		this.workAreaFunctionMembershipManager=workAreaFunctionMembershipManager;
	}
	public WorkAreaFunctionMembershipManager getWorkAreaFunctionMembershipManager() {
		return workAreaFunctionMembershipManager;
	}
	public ResourceDriverManager getResourceDriverManager() {
		return resourceDriverManager;
	}
	public void setResourceDriverManager(ResourceDriverManager resourceDriverManager) {
		this.resourceDriverManager = resourceDriverManager;
	}
	public LicenseManager getLicenseManager() {
		return licenseManager;
	}
	public void setLicenseManager(LicenseManager licenseManager) {
		this.licenseManager = licenseManager;
	}
	public AccessTokenManager getAccessTokenManager() {
		return accessTokenManager;
	}
	public void setAccessTokenManager(AccessTokenManager accessTokenManager) {
		this.accessTokenManager = accessTokenManager;
	}
    public RelevanceManager getRelevanceManager() {
    	return relevanceManager;
    }
    public void setRelevanceManager(RelevanceManager relevanceManager) {
    	this.relevanceManager = relevanceManager;
    }
	public RunAsyncManager getRunAsyncManager() {
		return runAsyncManager;
	}
	public void setRunAsyncManager(RunAsyncManager runAsyncManager) {
		this.runAsyncManager = runAsyncManager;
	}

	protected void end(long beginInNanoseconds, String methodName) {
		if(debugEnabled) {
			double diff = (System.nanoTime() - beginInNanoseconds)/1000000.0;
			logger.debug(diff + " ms, " + methodName);
		}	
	}

	protected void end(long beginInNanoseconds, String methodName, String extra) {
		if(debugEnabled) {
			double diff = (System.nanoTime() - beginInNanoseconds)/1000000.0;
			logger.debug(diff + " ms, " + ((extra == null)? methodName : methodName + "/" + extra));
		}	
	}

}
