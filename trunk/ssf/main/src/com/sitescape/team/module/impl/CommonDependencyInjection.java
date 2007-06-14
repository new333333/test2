/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;

import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.dao.FolderDao;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.fi.connection.ResourceDriverManager;
import com.sitescape.team.modelprocessor.ProcessorManager;
import com.sitescape.team.presence.PresenceService;
import com.sitescape.team.search.LuceneSessionFactory;
import com.sitescape.team.security.AccessControlManager;
import com.sitescape.team.security.acl.AclManager;
import com.sitescape.team.security.function.FunctionManager;
import com.sitescape.team.security.function.WorkAreaFunctionMembershipManager;

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
	protected boolean debugEnabled = logger.isDebugEnabled();
	protected boolean infoEnabled = logger.isInfoEnabled();

	protected CoreDao coreDao;
	protected ProfileDao profileDao;
	protected FolderDao folderDao;
	protected FunctionManager functionManager;
	protected AccessControlManager accessControlManager;
	protected AclManager aclManager;
	protected ProcessorManager processorManager;
	protected Scheduler scheduler;
	protected LuceneSessionFactory luceneSessionFactory;
	protected PresenceService presenceService;
	protected WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager;
	protected ResourceDriverManager resourceDriverManager;
	
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
	public void setPresenceService(PresenceService presenceService) {
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
	public PresenceService getPresenceService() {
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

}
