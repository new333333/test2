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
import com.sitescape.team.ical.IcalGenerator;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.modelprocessor.ProcessorManager;
import com.sitescape.team.presence.PresenceService;
import com.sitescape.team.rss.RssGenerator;
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
	protected RssGenerator rssGenerator;
	protected IcalGenerator icalGenerator;
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
	public void setRssGenerator(RssGenerator rssGenerator) {
		this.rssGenerator = rssGenerator;
	}
	public void setIcalGenerator(IcalGenerator icalGenerator) {
		this.icalGenerator = icalGenerator;
	}
	protected LuceneSessionFactory getLuceneSessionFactory() {
		return luceneSessionFactory;
	}
	protected AccessControlManager getAccessControlManager() {
		return accessControlManager;
	}
	protected AclManager getAclManager() {
		return aclManager;
	}
	protected CoreDao getCoreDao() {
		return coreDao;
	}
	protected FolderDao getFolderDao() {
		return folderDao;
	}
	protected ProfileDao getProfileDao() {
		return profileDao;
	}
	protected FunctionManager getFunctionManager() {
		return functionManager;
	}
	protected ProcessorManager getProcessorManager() {
		return processorManager;
	}
	protected Scheduler getScheduler() {
		return scheduler;
	}
	protected PresenceService getPresenceService() {
		return presenceService;
	}
	public RssGenerator getRssGenerator() {
		return rssGenerator;
	}
	public IcalGenerator getIcalGenerator() {
		return icalGenerator;
	}
	public void setWorkAreaFunctionMembershipManager(WorkAreaFunctionMembershipManager workAreaFunctionMembershipManager) {
		this.workAreaFunctionMembershipManager=workAreaFunctionMembershipManager;
	}
	protected WorkAreaFunctionMembershipManager getWorkAreaFunctionMembershipManager() {
		return workAreaFunctionMembershipManager;
	}
	protected ResourceDriverManager getResourceDriverManager() {
		return resourceDriverManager;
	}
	public void setResourceDriverManager(ResourceDriverManager resourceDriverManager) {
		this.resourceDriverManager = resourceDriverManager;
	}

}
