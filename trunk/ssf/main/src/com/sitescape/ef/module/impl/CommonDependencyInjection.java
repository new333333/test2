package com.sitescape.ef.module.impl;

import org.quartz.Scheduler;
import org.springframework.mail.MailSender;

import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.dao.FolderDao;
import com.sitescape.ef.modelprocessor.ProcessorManager;
import com.sitescape.ef.security.AccessControlManager;
import com.sitescape.ef.security.acl.AclManager;
import com.sitescape.ef.security.function.FunctionManager;

/**
 * This abstract class provides a central place where dependent
 * components are injected. 
 * 
 * @author jong
 *
 */
public class AbstractModuleImpl {

	protected CoreDao coreDao;
	protected FolderDao folderDao;
	protected FunctionManager functionManager;
	protected AccessControlManager accessControlManager;
	protected AclManager aclManager;
	protected ProcessorManager processorManager;
	protected Scheduler scheduler;
	
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
	public void setFunctionManager(FunctionManager functionManager) {
		this.functionManager = functionManager;
	}
	public void setProcessorManager(ProcessorManager processorManager) {
		this.processorManager = processorManager;
	}
	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
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
	protected FunctionManager getFunctionManager() {
		return functionManager;
	}
	protected ProcessorManager getProcessorManager() {
		return processorManager;
	}
	protected Scheduler getScheduler() {
		return scheduler;
	}
}
