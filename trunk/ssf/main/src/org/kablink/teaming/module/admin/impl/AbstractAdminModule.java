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
package org.kablink.teaming.module.admin.impl;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;

import org.apache.velocity.VelocityContext;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderQuota;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.HomePageConfig;
import org.kablink.teaming.domain.MailConfig;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WeekendsAndHolidaysConfig;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.EmailLog.EmailLogType;
import org.kablink.teaming.extension.ExtensionManager;
import org.kablink.teaming.extuser.ExternalUserUtil;
import org.kablink.teaming.jobs.EmailNotification;
import org.kablink.teaming.jobs.EmailPosting;
import org.kablink.teaming.jobs.FileVersionAging;
import org.kablink.teaming.jobs.IndexOptimization;
import org.kablink.teaming.jobs.LogTablePurge;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.IndexOptimizationSchedule;
import org.kablink.teaming.module.admin.ManageIndexException;
import org.kablink.teaming.module.admin.SendMailErrorWrapper;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.binder.processor.EntryProcessor;
import org.kablink.teaming.module.dashboard.DashboardModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.notify.Notify;
import org.kablink.teaming.module.definition.notify.NotifyVisitor;
import org.kablink.teaming.module.definition.notify.Notify.NotifyType;
import org.kablink.teaming.module.definition.notify.NotifyBuilderUtil;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.mail.EmailUtil;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.mail.MailSentStatus;
import org.kablink.teaming.module.mail.MimeSharePreparator;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.shared.ObjectBuilder;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.search.LuceneWriteSession;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.accesstoken.AccessToken;
import org.kablink.teaming.security.function.Condition;
import org.kablink.teaming.security.function.ConditionalClause;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.FunctionExistsException;
import org.kablink.teaming.security.function.RemoteAddrCondition;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.RuntimeStatistics;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.EmailHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Html;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * ?
 * 
 * @author Janet McCann
 */
@SuppressWarnings("unchecked")
public abstract class AbstractAdminModule extends CommonDependencyInjection implements AdminModule {
	
	protected static String INDEX_OPTIMIZATION_JOB = "index.optimization.job"; // properties in xml file need a unique name
	protected static String FILE_VERSION_AGING_JOB = "file.version.aging.job"; // properties in xml file need a unique name
	protected static String LOG_TABLE_PURGE_JOB = "log.table.purge.job"; // properties in xml file need a unique name
	
	protected MailModule mailModule;
	/**
	 * Setup by spring
	 * @param mailModule
	 */
	public void setMailModule(MailModule mailModule) {
    	this.mailModule = mailModule;
    }
	protected MailModule getMailModule() {
		return mailModule;
	}
    protected DefinitionModule definitionModule;
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
   	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	protected WorkspaceModule workspaceModule;
	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
   	protected WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}

	protected FolderModule folderModule;
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
   	protected FolderModule getFolderModule() {
		return folderModule;
	}
 
	protected BinderModule binderModule;
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
   	protected BinderModule getBinderModule() {
		return binderModule;
	}
	protected DashboardModule dashboardModule;
	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
   	protected DashboardModule getDashboardModule() {
		return dashboardModule;
	}
   	protected FileModule fileModule;
   	public void setFileModule(FileModule fileModule) {
   		this.fileModule = fileModule;
   	}
   	protected FileModule getFileModule() {
   		return fileModule;
   	}	
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	private ReportModule reportModule;
   	protected ReportModule getReportModule() {
		return reportModule;
	}
	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}
	private ExtensionManager extensionManager;
	@Override
	public ExtensionManager getExtensionManager() {
		return extensionManager;
	}
	public void setExtensionManager(ExtensionManager extensionManager) {
		this.extensionManager = extensionManager;
	}

	@Override
	public void deleteExtension(String id){
		checkAccess(AdminOperation.manageExtensions);
		
		Object obj = getCoreDao().load(ExtensionInfo.class, id);
		if(obj != null && obj instanceof ExtensionInfo)
		{
			coreDao.delete(obj);
		}
	}
	
	@Override
	public void addExtension(ExtensionInfo extension) {
		checkAccess(AdminOperation.manageExtensions);
		coreDao.save(extension);
	}
	@Override
	public void modifyExtension(ExtensionInfo extension) {
		checkAccess(AdminOperation.manageExtensions);
		
		coreDao.update(extension);
	}
	/**
   	 * Use operation so we can keep the logic out of application
	 * and easisly change the required rights
	 */
   	@Override
	public boolean testAccess(WorkArea workArea, AdminOperation operation) {
   		try {
   			checkAccess(workArea, operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
   	@Override
	public void checkAccess(WorkArea workArea, AdminOperation operation) {
   		if (workArea instanceof TemplateBinder) {
			getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
   		} else if (workArea instanceof ZoneConfig) {
			getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
   		} else {
   			switch (operation) {
   			case manageFunctionMembership:
   				if (workArea instanceof Entry && ((Entry)workArea).hasEntryAcl()) {
					try {
						getAccessControlManager().checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);
					} catch(AccessControlException ex) {
						if (((Entry)workArea).isIncludeFolderAcl()) {
							try {
								getAccessControlManager().checkOperation(((Entry)workArea).getParentBinder(), WorkAreaOperation.CREATE_ENTRY_ACLS);
							} catch(AccessControlException ex2) {
		   		        		User user = RequestContextHolder.getRequestContext().getUser();
		   		        		if (user.getId().equals(((Entry)workArea).getCreation().getPrincipal().getId())) {
									getAccessControlManager().checkOperation(((Entry)workArea).getParentBinder(), WorkAreaOperation.CREATOR_CREATE_ENTRY_ACLS);
								} else {
									throw ex;
								}
							}
						} else {
							throw ex;
						}
					}
   				} else if (workArea instanceof Entry && !((Entry)workArea).hasEntryAcl()) {
   					getAccessControlManager().checkOperation(((Entry)workArea).getParentBinder(), WorkAreaOperation.CREATE_ENTRY_ACLS);
   				} else {
   					getAccessControlManager().checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);
   				}
				break;
   			default:
   				throw new NotSupportedException(operation.toString(), "checkAccess");
  					
   			}
   		}		
	}
	private BinderProcessor loadBinderProcessor(Binder binder) {
		return (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
	}

	private EntryProcessor loadEntryProcessor(Entry entry) {
		return (EntryProcessor)getProcessorManager().getProcessor(entry.getParentBinder(), entry.getParentBinder().getProcessorKey(EntryProcessor.PROCESSOR_KEY));
	}

   	/**
	 * Use operation so we can keep the logic out of application
	 * and easily change the required rights
   	 * 
   	 */
  	@Override
	public boolean testAccess(AdminOperation operation) {
   		try {
   			checkAccess(operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
  	@Override
	public void checkAccess(AdminOperation operation) {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
		switch (operation) {
			case manageFunction:
			case manageFunctionCondition:
			case manageMail:
			case manageFileVersionAging:
			case manageLogTablePurge:
			case manageFileSizeLimit:
			case manageTemplate:
			case manageErrorLogs:
  			case manageFunctionMembership:
  			case manageRuntime:
  			case manageResourceDrivers:
  			case manageFileSynchApp:
  			case manageOpenID:
  			case manageExternalUser:
  			case manageMobileApps:
  				getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
   				break;
			case report:
   				if (getAccessControlManager().testOperation(top, WorkAreaOperation.GENERATE_REPORTS)) break;
 				getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
   				break;
			case manageExtensions:
  				getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);

  				//is this featured disabled
    			if( !SPropsUtil.getBoolean("extensions.manage.enabled", true) ) {
					throw new AccessControlException();
				}

  				break;
  			case manageIndex: 
  				// Or should we allow only 'admin' to be able to manage index since we display
  				// the UI only for admin??
  				getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
   				break;
			default:
   				throw new NotSupportedException(operation.toString(), "checkAccess");
		}
   	}
  	@Override
	public boolean isQuotaEnabled() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.isDiskQuotaEnabled(); 		
  	}
  	@Override
	public void setQuotaEnabled(boolean quotaEnabled) {
  		boolean resetUsage = false;
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (isQuotaEnabled() == quotaEnabled) return;  // if the setting hasn't changed, then do nothing
  		// if quotas are currently turned off, and the setting is now true, then reset the usage statistics
  		if (!isQuotaEnabled() && quotaEnabled) resetUsage = true;
  		zoneConfig.setDiskQuotasEnabled(quotaEnabled);
  		if (resetUsage)
  			getProfileDao().resetDiskUsage(RequestContextHolder.getRequestContext().getZoneId());
  	}
  	@Override
	public Integer getQuotaDefault() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getDiskQuotaUserDefault();
  	}
  	@Override
	public Integer getQuotaHighWaterMark() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getDiskQuotasHighwaterPercentage();
  	}
  	@Override
	public void setQuotaDefault(Integer quotaDefault) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (zoneConfig.getDiskQuotaUserDefault() == quotaDefault) return; // if no change, do nothing
  		zoneConfig.setDiskQuotaUserDefault(quotaDefault);
  	}
  	@Override
	public void setQuotaHighWaterMark(Integer quotaHighWaterMark) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (zoneConfig.getDiskQuotasHighwaterPercentage() == quotaHighWaterMark) return; // if no change, do nothing
  		zoneConfig.setDiskQuotasHighwaterPercentage(quotaHighWaterMark);
  	}
    @Override
	public BinderQuota getBinderQuota(Binder binder) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	BinderQuota bq = null;
    	try {
    		bq = getCoreDao().loadBinderQuota(zoneId, binder.getId());
    	} catch(NoObjectByTheIdException e) {
    		bq = new BinderQuota();
    		bq.setZoneId(zoneId);
    		bq.setBinderId(binder.getId());
    		Long diskSpaceUsed = getCoreDao().computeDiskSpaceUsed(zoneId, binder.getId());
    		bq.setDiskSpaceUsed(diskSpaceUsed);
    		bq.setDiskSpaceUsedCumulative(diskSpaceUsed);
    	}
    	return bq;
    }
    @Override
	public void setBinderQuota(Binder binder, BinderQuota binderQuota) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	BinderQuota bq = null;
    	try {
    		bq = getCoreDao().loadBinderQuota(zoneId, binder.getId());
    		bq = binderQuota;
    		getCoreDao().save(bq);
    	} catch(NoObjectByTheIdException e) {
    		getCoreDao().save(binderQuota);
    	}
    }
    @Override
	public void setBinderQuotasInitialized(boolean binderQuotaInitialized) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setBinderQuotasInitialized(binderQuotaInitialized);
    }
    @Override
	public void setBinderQuotasEnabled(boolean binderQuotaEnabled, boolean allowBinderOwner) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setBinderQuotasEnabled(binderQuotaEnabled, allowBinderOwner);
    }
    @Override
	public boolean isBinderQuotaInitialized() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.isBinderQuotaInitialized(); 		
    }
    @Override
	public boolean isBinderQuotaEnabled() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (zoneConfig.isBinderQuotaInitialized()) {
  			return zoneConfig.isBinderQuotaEnabled(); 		
  		} else {
  			return false;
  		}
    }
    @Override
	public boolean isBinderQuotaAllowBinderOwnerEnabled() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.isBinderQuotaAllowBinderOwnerEnabled(); 		
    }

  	@Override
	public void setFileVersionsMaxAge(Long fileVersionsMaxAge) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (zoneConfig.getFileVersionsMaxAge() == fileVersionsMaxAge) return; // if no change, do nothing
  		zoneConfig.setFileVersionsMaxAge(fileVersionsMaxAge);
  	}

  	/**
  	 * 
  	 */
  	@Override
	public boolean isAdHocFoldersEnabled()
  	{
  		ZoneConfig zoneConfig;

  		zoneConfig = getCoreDao().loadZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
  		return zoneConfig.isAdHocFoldersEnabled(); 		
  	}
  	
  	/**
  	 * 
  	 */
  	@Override
	public void setAdHocFoldersEnabled( boolean enabled ) 
  	{
  		ZoneConfig zoneConfig;

  		zoneConfig = getCoreDao().loadZoneConfig( RequestContextHolder.getRequestContext().getZoneId() );
  		zoneConfig.setAdHocFoldersEnabled( enabled );
  	}

  	@Override
	public boolean isMobileAccessEnabled() {
  		if (Utils.checkIfFilr()) {
  			//Filr does not support the old jsp based mobile UI
  			return false;
  		}
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.isMobileAccessEnabled(); 		
  	}
  	@Override
	public void setMobileAccessEnabled(boolean mobileAccessEnabled) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setMobileAccessEnabled(mobileAccessEnabled);
  	}
  	@Override
	public HomePageConfig getHomePageConfig() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return new HomePageConfig(zoneConfig.getHomePageConfig()); 		
  	}
  	@Override
	public void setHomePageConfig(HomePageConfig homePageConfig) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setHomePageConfig(homePageConfig); 		
  	}
  	@Override
	public WeekendsAndHolidaysConfig getWeekendsAndHolidaysConfig() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return new WeekendsAndHolidaysConfig(zoneConfig.getWeekendsAndHolidaysConfig()); 		
  	}
  	@Override
	public void setWeekendsAndHolidaysConfig(WeekendsAndHolidaysConfig weekendsAndHolidaysConfig) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setWeekendsAndHolidaysConfig(weekendsAndHolidaysConfig); 		
  	}
  	@Override
	public Long getFileVersionsMaxAge() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getFileVersionsMaxAge();
  	}
  	public void setFileVersionAgingDays(Long fileVersionAge) {
  	   	checkAccess(AdminOperation.manageFileVersionAging);
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setFileVersionsMaxAge(fileVersionAge);
  	}
  	@Override
	public Long getFileSizeLimitUserDefault() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getFileSizeLimitUserDefault();
  	}
  	@Override
	public void setFileSizeLimitUserDefault(Long fileSizeLimitUserDefault) {
  	   	checkAccess(AdminOperation.manageFileSizeLimit);
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setFileSizeLimitUserDefault(fileSizeLimitUserDefault);
  	}
  	@Override
	public Long getUserFileSizeLimit() {
		User user = RequestContextHolder.getRequestContext().getUser();
		//Check the system default and the user limits
		Long userMaxFileSize = user.getFileSizeLimit();
		Long userMaxGroupsFileSize = user.getMaxGroupsFileSizeLimit();
		Long fileSizeLimit = null;
		if (userMaxGroupsFileSize != null) {
			//Start with the group setting (if any)
			fileSizeLimit = userMaxGroupsFileSize;
		}
		if (userMaxFileSize != null) {
			//If there is a user setting, use that (even if it is less than the group setting)
			fileSizeLimit = userMaxFileSize;
		}
		if (fileSizeLimit == null) {
			//There aren't any per-user or per-group settings, so see if there is a site default
			fileSizeLimit = getFileSizeLimitUserDefault();
		}
		return fileSizeLimit;
  	}
  	@Override
	public MailConfig getMailConfig() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return new MailConfig(zoneConfig.getMailConfig()); 		
  	}
  	//try to keep these in sync using one call
  	@Override
	public void setMailConfigAndSchedules(MailConfig mailConfig, ScheduleInfo notification, ScheduleInfo posting) {
  	   	checkAccess(AdminOperation.manageMail);
  		//even if schedules are running, these settings should stop the processing in the job
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		boolean wasPostingEnabled = zoneConfig.getMailConfig().isPostingEnabled();
  		zoneConfig.getMailConfig().setPostingEnabled(mailConfig.isPostingEnabled());
 		zoneConfig.getMailConfig().setSimpleUrlPostingEnabled(mailConfig.isSimpleUrlPostingEnabled());
 		zoneConfig.getMailConfig().setSendMailEnabled(mailConfig.isSendMailEnabled());
 		if (notification != null) {
 			notification.setEnabled(mailConfig.isSendMailEnabled());
 			getNotificationObject().setScheduleInfo(notification);
 		}
  		zoneConfig.getMailConfig().setOutgoingAttachmentSizeLimit(mailConfig.getOutgoingAttachmentSizeLimit());
  		zoneConfig.getMailConfig().setOutgoingAttachmentSumLimit(mailConfig.getOutgoingAttachmentSumLimit());
 		//this is being phased out
 		if (posting != null) {
 			posting.setEnabled(posting.isEnabled());
 			getPostingObject().setScheduleInfo(posting);
 		} else if (wasPostingEnabled && !zoneConfig.getMailConfig().isPostingEnabled()) {
 			//remove it
 			getPostingObject().enable(false, zoneConfig.getZoneId());
 		}
  	}
	@Override
	public List<PostingDef> getPostings() {
    	return coreDao.loadPostings(RequestContextHolder.getRequestContext().getZoneId());
    }
    @Override
	public void modifyPosting(String postingId, Map updates) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	ObjectBuilder.updateObject(post, updates);
    }
    @Override
	public void addPosting(Map updates) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = new PostingDef();
    	post.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
       	ObjectBuilder.updateObject(post, updates);
       	post.setEmailAddress(post.getEmailAddress().toLowerCase());
      	coreDao.save(post);   	
    }
    @Override
	public void deletePosting(String postingId) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	if (post.getBinder() != null) {
    		post.getBinder().setPosting(null);
    	}
       	coreDao.delete(post);
    }
    @Override
	public ScheduleInfo getPostingSchedule() {
      	//let anyone get it;
    	ScheduleInfo info = getPostingObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
    	return info;
    }
     private EmailPosting getPostingObject() {
    	String jobClass = getMailModule().getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.POSTING_JOB);
       	if (Validator.isNotNull(jobClass)) {
 		   try {
 			   return  (EmailPosting)ReflectHelper.getInstance(jobClass);
 		   } catch (Exception ex) {
 			   logger.error("Cannot instantiate EmailPosting custom class", ex);
 		   }
       	}
       	return (EmailPosting)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultEmailPosting.class);
     }

	/**
     * Do actual work to either enable or disable digest notification.
     * @param id
     * @param value
     */
	@Override
	public ScheduleInfo getNotificationSchedule() {
		ScheduleInfo info = getNotificationObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
    	return info;
	}
	    
    private EmailNotification getNotificationObject() {
    	String jobClass = getMailModule().getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.NOTIFICATION_JOB);
    	if (Validator.isNotNull(jobClass)) {
		   try {
			   return  (EmailNotification)ReflectHelper.getInstance(jobClass);
		   } catch (Exception ex) {
			   logger.error("Cannot instantiate EmailNotification custom class", ex);
		   }
   		}
   		return (EmailNotification)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultEmailNotification.class);
     }    

    /**
     * Do actual work to either enable or disable file version aging.
     * @param id
     * @param value
     */
	@Override
	public ScheduleInfo getFileVersionAgingSchedule() {
		ScheduleInfo info = getFileVersionAgingObject().getScheduleInfo(
				RequestContextHolder.getRequestContext().getZoneId());
		User user = RequestContextHolder.getRequestContext().getUser();
		Date now = new Date();
		int offsetHour = user.getTimeZone().getOffset(now.getTime()) / (60 * 60 * 1000);
		String hours = SPropsUtil.getString("version.aging.schedule.hours", "0");
		String minutes = SPropsUtil.getString("version.aging.schedule.minutes", "30");
		try {
			int iHours = Integer.valueOf(hours);
			iHours -= offsetHour;
			hours = String.valueOf((iHours + 24) % 24);
		} catch(Exception e) {
			//This must be trying to set "*" or some other fancy value, so just leave "hours" as it was
		}
		info.getSchedule().setDaily(true);
		info.getSchedule().setHours(hours);
		info.getSchedule().setMinutes(minutes);
    	return info;
	}
	    
    private FileVersionAging getFileVersionAgingObject() {
    	String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		String jobClass = getFileVersionAgingProperty(zoneName, FILE_VERSION_AGING_JOB);
    	if (Validator.isNotNull(jobClass)) {
		   try {
			   return  (FileVersionAging)ReflectHelper.getInstance(jobClass);
		   } catch (Exception ex) {
			   logger.error("Cannot instantiate FileVersionAging custom class", ex);
		   }
   		}
   		return (FileVersionAging)ReflectHelper.getInstance(
   				org.kablink.teaming.jobs.DefaultFileVersionAgingDelete.class);
     }    

	//See if there is a custom scheduling job being specified
    protected String getFileVersionAgingProperty(String zoneName, String name) {
		return SZoneConfig.getString(zoneName, "fileVersionAgingConfiguration/property[@name='" + name + "']");
	}

  	@Override
	public void setFileVersionAgingSchedule(ScheduleInfo info) {
  	   	checkAccess(AdminOperation.manageFileVersionAging);
  		//even if schedules are running, these settings should stop the processing in the job
  	   	long zoneId = RequestContextHolder.getRequestContext().getZoneId();
  		@SuppressWarnings("unused")
		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
 		if (info != null) {
			getFileVersionAgingObject().setScheduleInfo(info);
			getFileVersionAgingObject().enable(Boolean.TRUE, zoneId);
 		}
  	}

	@Override
	public void updateDefaultDefinitions(Long topId, Boolean newDefinitionsOnly) {
		Workspace top = (Workspace)getCoreDao().loadBinder(topId, topId);
		
		//default definitions stored in separate config file
		String startupConfig = SZoneConfig.getString(top.getName(), "property[@name='startupConfig']", "config/startup.xml");
		SAXReader reader = new SAXReader(false);  
		InputStream in=null;
		try {
			in = new ClassPathResource(startupConfig).getInputStream();
			Document cfg = reader.read(in);
			in.close();
			List<Element> elements = cfg.getRootElement().selectNodes("definitionFile");
			List<String> defs = new ArrayList();
			for (Element element:elements) {
				String file = element.getTextTrim();
				reader = new SAXReader(false);  
				try {
					in = new ClassPathResource(file).getInputStream();
					boolean replace = true;
					if (newDefinitionsOnly) replace = false;
					Definition newDef = getDefinitionModule().addDefinition(in, null, null, null, replace);
					defs.add(newDef.getId());
					getCoreDao().flush();
				} catch (Exception ex) {
					logger.error("Cannot read definition from file: " + file + " " + ex.getMessage());
					return; //cannot continue, rollback is enabled
				} finally {
					if (in!=null) in.close();
				}
			}
			for (String id:defs) {
				getDefinitionModule().updateDefinitionReferences(id);
			}

		} catch (Exception ex) {
			logger.error("Cannot read startup configuration:", ex);
		}
	}	
	@Override
	public void updateDefaultDefinitions(AllModulesInjected bs, Long topId, Boolean newDefinitionsOnly, Collection ids) {
		Workspace top = (Workspace)getCoreDao().loadBinder(topId, topId);
		@SuppressWarnings("unused")
		List currentDefinitions = new ArrayList();
		currentDefinitions = DefinitionHelper.getDefaultDefinitions(bs);
		
		//default definitions stored in separate config file
		String startupConfig = SZoneConfig.getString(top.getName(), "property[@name='startupConfig']", "config/startup.xml");
		SAXReader reader = new SAXReader(false);  
		InputStream in=null;
		try {
			in = new ClassPathResource(startupConfig).getInputStream();
			Document cfg = reader.read(in);
			in.close();
			List<Element> elements = cfg.getRootElement().selectNodes("definitionFile");
			List<String> defs = new ArrayList();
			for (Element element:elements) {
				String file = element.getTextTrim();
				//Get the definition name from the file name
				Pattern nameP = Pattern.compile("/([^/\\.]*)\\.xml$");
				Matcher m = nameP.matcher(file);
				if (m.find()) {
					String name = m.group(1);
					if (name != null && !name.equals("")) {
						Definition def = null;
						try {
							def = getDefinitionModule().getDefinitionByName(null, false, name);
						} catch(NoDefinitionByTheIdException e) {
							//This definition doesn't exist yet; always read it in
						}
						//If this definition is not on the list, don't read it in
						if (def != null && !ids.contains(def.getId())) continue;
					}
				}
				reader = new SAXReader(false);  
				try {
					in = new ClassPathResource(file).getInputStream();
					boolean replace = true;
					if (newDefinitionsOnly) replace = false;
					Definition newDef = getDefinitionModule().addDefinition(in, null, null, null, replace);
					if (newDef != null) defs.add(newDef.getId());
					getCoreDao().flush();
				} catch (Exception ex) {
					logger.error("Cannot read definition from file: " + file + " " + ex.getMessage());
					return; //cannot continue, rollback is enabled
				} finally {
					if (in!=null) in.close();
				}
			}
			for (String id:defs) {
				getDefinitionModule().updateDefinitionReferences(id);
			}

		} catch (Exception ex) {
			logger.error("Cannot read startup configuration:", ex);
		}
	}
	@Override
	public Function addFunction(String name, Set<WorkAreaOperation> operations, String scope, 
			List<ConditionalClause> conditions) {
		checkAccess(AdminOperation.manageFunction);
		Function function = new Function();
		function.setName(name);
		function.setScope(scope);
		function.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		function.setOperations(operations);
		function.setConditionalClauses(conditions);
		
		List zoneFunctions = functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
		if (zoneFunctions.contains(function)) {
			//Role already exists
			throw new FunctionExistsException(function.getName());
		}
		functionManager.addFunction(function);
		return function;
    }
    @Override
	public Function modifyFunction(Long id, Map updates) {
		checkAccess(AdminOperation.manageFunction);
		Function function = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		if (function.isReserved()) throw new NotSupportedException("errorcode.role.reserved", new Object[]{function.getName()});       	
		if (updates.containsKey("name") && !function.getName().equals(updates.get("name"))) {
			List zoneFunctions = functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
			//make sure unqiue - do after find or hibernate will update
			zoneFunctions.remove(function);
			function.setName((String)updates.get("name"));
			if (zoneFunctions.contains(function)) {
				//Role already exists
				throw new FunctionExistsException(function.getName());
			}
			
		}
		ObjectBuilder.updateObject(function, updates);
		functionManager.updateFunction(function);	
		return function;
    }
    
    @Override
	public List deleteFunction(Long id) {
		checkAccess(AdminOperation.manageFunction);
		Function f = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		if (f.isReserved()) throw new NotSupportedException("errorcode.role.reserved", new Object[]{f.getName()});       	

		List result = functionManager.deleteFunction(f);
		if(result != null) {
			result.add(f);
			return result;
		}
		else
			return null;
    }
    @Override
	public Function getFunction(Long functionId) {
    	// let anyone read it
    	return functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), functionId);
    }
    @Override
	public List<Function> getFunctions() {
		//let anyone read them			
    	return functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
    }
    @Override
	public List<Function> getFunctions(String scope) {
		//let anyone read them			
    	List<Function> functions = functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
    	List<Function> functionsPruned = new ArrayList<Function>();
    	for (Function f : functions) {
    		if (scope.equals(f.getScope()) || 
    				(f.getScope() == null && scope.equals(ObjectKeys.ROLE_TYPE_ZONE) && f.isZoneWide())) {
    			functionsPruned.add(f);
    		} else if (f.getScope() == null && scope.equals(ObjectKeys.ROLE_TYPE_BINDER) && !f.isZoneWide()) {
    			functionsPruned.add(f);
    		}
    	}
    	return functionsPruned;
    }
	//no transaction
    @Override
	public void setWorkAreaFunctionMemberships(final WorkArea workArea, 
    		final Map<Long, Set<Long>> functionMemberships) {
    	setWorkAreaFunctionMemberships(workArea, functionMemberships, Boolean.TRUE, Boolean.FALSE, 
    			ObjectKeys.ROLE_TYPE_FILR);
    }
    @Override
	public void setWorkAreaFunctionMemberships(final WorkArea workArea, final Map<Long, 
    		Set<Long>> functionMemberships, boolean doCheckAccess) {
    	setWorkAreaFunctionMemberships(workArea, functionMemberships, doCheckAccess, Boolean.FALSE, 
    			ObjectKeys.ROLE_TYPE_FILR);
    }
    /*
     * Routine to set the functions (roles) and function memberships for a workarea.
     * 
     * Depending on the "justThisScope" flag, this routine will skip any function with the 
     * specified scope or it will only change functions with the specified scope. The other 
     * functions are left unchanged. This capability is used to support workareas that have 
     * functions (roles) that are being controlled externally, such as with Filr ACLs. 
     * 
     * If "justThisScope" is true, then modify only those functions with the specified scope
     * If "justThisScope" is false, then modify all functions except the ones with the specified scope
     */
    @Override
	public void setWorkAreaFunctionMemberships(final WorkArea workArea, 
    		final Map<Long, Set<Long>> functionMemberships, boolean doCheckAccess, 
    		final boolean justThisScope, final String scope) {
    	if (doCheckAccess) {
    		checkAccess(workArea, AdminOperation.manageFunctionMembership);
    	}
		checkAccess(workArea, AdminOperation.manageFunctionMembership);
		final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		
		//get list of current readers to compare for indexing
		List<WorkAreaFunctionMembership>wfmsRead = 
	       		getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.READ_ENTRIES);
       	TreeSet<Long> originalRead = new TreeSet();
        for (WorkAreaFunctionMembership wfm:wfmsRead) {
        	originalRead.addAll(wfm.getMemberIds());
    	}
		List<WorkAreaFunctionMembership> wfmsVBT = 
       		getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.VIEW_BINDER_TITLE);
		TreeSet<Long> originalVBT = new TreeSet();
	    for (WorkAreaFunctionMembership wfm:wfmsVBT) {
	    	originalVBT.addAll(wfm.getMemberIds());
		}
	    List<WorkAreaFunctionMembership> wfmsNFA = 
   		getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER);
		TreeSet<Long> originalNFA = new TreeSet();
		for (WorkAreaFunctionMembership wfm:wfmsNFA) {
			originalNFA.addAll(wfm.getMemberIds());
		}

      	boolean conditionsExistInOrigianl = checkIfConditionsExist(workArea);
        //first remove any that are not in the new list
        getTransactionTemplate().execute(new TransactionCallback() {
        	@Override
			public Object doInTransaction(TransactionStatus status) {
        		User guest = null;
       			try {
       				guest = getProfileDao().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zoneId);
       			} catch (NoObjectByTheIdException noexist) {};
        		//get list of current memberships
        		List<WorkAreaFunctionMembership>wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(zoneId, workArea);
        		for( WorkAreaFunctionMembership wfm:wfms) {
        			if (!functionMemberships.containsKey(wfm.getFunctionId())) {
        				//Also check if limiting the deletions to a scope
        				if (Validator.isNotNull(scope)) {
        					Function f = getWorkAreaFunctionMembershipManager().getFunction(zoneId, wfm.getFunctionId());
        					if (f != null) {
        						if ((scope.equals(f.getScope()) && justThisScope) || 
        								(!scope.equals(f.getScope()) && !justThisScope)) {
        							//We are either limiting to a scope or limiting to all but the specified scope
        							getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);  
        						}
        					}
        				} else {
        					getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);  
        				}
        			}
        		}
        		for (Map.Entry<Long, Set<Long>> fm : functionMemberships.entrySet()) {
        			WorkAreaFunctionMembership membership=null;
        			//find in current list
        			for (int i=0; i<wfms.size(); ++i) {
        				WorkAreaFunctionMembership wfm = wfms.get(i);
        				if (wfm.getFunctionId().equals(fm.getKey())) {
        					membership = wfm;
        					break;	        	
        				}
        			}
        			//See if this is a function that should be modified
        			if (Validator.isNotNull(scope) && membership != null) {
        				Function f = getWorkAreaFunctionMembershipManager().getFunction(zoneId, membership.getFunctionId());
    					if (f != null) {
    						if ((scope.equals(f.getScope()) && !justThisScope) || 
    								(!scope.equals(f.getScope()) && justThisScope)) {
    							//This combination indicates to not allow modifications
    							continue;
    						}
    					}
        			}
        			Set members = fm.getValue();
        			if (membership == null) { 
            			if (guest != null && members.contains(guest.getId())) {
            				//check user can add guest access
            				getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(zoneId), WorkAreaOperation.ADD_GUEST_ACCESS);
            			}
            			membership = new WorkAreaFunctionMembership();
        				membership.setZoneId(zoneId);
        				membership.setWorkAreaId(workArea.getWorkAreaId());
        				membership.setWorkAreaType(workArea.getWorkAreaType());
        				membership.setFunctionId(fm.getKey());
        				membership.setMemberIds(members);
        				getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
        			} else if (members == null || members.isEmpty()) {
        				getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(membership);       	
        			} else {
        				Set mems = membership.getMemberIds();
        				if (!mems.equals(members)) {
                   			if (guest != null && members.contains(guest.getId()) && !mems.contains(guest.getId())) {
                				//check user can add guest access
                				getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(zoneId), WorkAreaOperation.ADD_GUEST_ACCESS);
                			}

        					mems.clear();
        					mems.addAll(members);
        					membership.setMemberIds(mems);
        				}
        			}
        		}
        		if(dealingWithExternalAcl(justThisScope, scope))
        			workArea.setExtFunctionMembershipInherited(false);
        		else
        			workArea.setFunctionMembershipInherited(false);
				processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
				return null;
        	}});
		//get new list of readers
      	wfmsRead = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.READ_ENTRIES);
      	TreeSet<Long> currentRead = new TreeSet();
      	for (WorkAreaFunctionMembership wfm:wfmsRead) {
      		currentRead.addAll(wfm.getMemberIds());
      	}
      	wfmsVBT = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.VIEW_BINDER_TITLE);
      	TreeSet<Long> currentVBT = new TreeSet();
      	for (WorkAreaFunctionMembership wfm:wfmsVBT) {
      		currentVBT.addAll(wfm.getMemberIds());
      	}
      	wfmsNFA = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.ALLOW_ACCESS_NET_FOLDER);
      	TreeSet<Long> currentNFA = new TreeSet();
      	for (WorkAreaFunctionMembership wfm:wfmsNFA) {
      		currentNFA.addAll(wfm.getMemberIds());
      	}
      	//only re-index if readers were affected.  Do outside transaction
      	boolean conditionsExist = checkIfConditionsExist(workArea);
		if ((!originalRead.equals(currentRead) || !originalVBT.equals(currentVBT) || !originalNFA.equals(currentNFA) ||
				conditionsExist || conditionsExistInOrigianl) && (workArea instanceof Binder)) {
			Binder binder = (Binder)workArea;
			loadBinderProcessor(binder).indexFunctionMembership(binder, true, null, true);
		} else if (!originalRead.equals(currentRead) && workArea instanceof Entry) {
			Entry entry = (Entry)workArea;
			List entries = new ArrayList();
			entries.add(entry);
			Set<Entry> children = entry.getChildWorkAreas();
			for (Entry e : children) entries.add(e);
			loadEntryProcessor(entry).indexEntries(entries);
		}
	}


	//no transaction
    @Override
	public void setWorkAreaOwner(final WorkArea workArea, final Long userId, final boolean propagate) {
    	 checkAccess(workArea, AdminOperation.manageFunctionMembership);
    	 final List<Binder>binders = new ArrayList();
       	 if (propagate) {
			if (workArea instanceof Binder) {
				Binder binder = (Binder)workArea;
				binders.addAll(binder.getBinders());
				for (int i=0; i<binders.size();) {
					Binder child = binders.get(i);
					if (!userId.equals(child.getOwnerId())) {
						if (!testAccess(child, AdminOperation.manageFunctionMembership)) {
							//The user doesn't have the access rights to change this one, so skip it
							binders.remove(i);
						} else {
							++i;
						}
					} else {
						binders.remove(i);  //already set, get out of list
					}
					//Add in all of the children of this binder to see if there are binders further down the tree to be changed
					binders.addAll(child.getBinders());
				}
			}
    	 }
    	if (!binders.isEmpty() || !workArea.getOwnerId().equals(userId)) {
    		getTransactionTemplate().execute(new TransactionCallback() {
   		        	@Override
					public Object doInTransaction(TransactionStatus status) {
   		        		User user = getProfileDao().loadUser(userId, RequestContextHolder.getRequestContext().getZoneId());
   		        		workArea.setOwner(user);
   		        		for (Binder child:binders) {
   		        			child.setOwner(user);
   		        		}
   		        		return null;
   		       }});
    		//do outside transaction
    		//need to update access, since owner has changed - assume read access is effected
			if (workArea instanceof Binder) {
				Binder binder = (Binder)workArea;
				binders.add(binder);
				loadBinderProcessor(binder).indexOwner(binders, userId);
			}
    	}
    }
    
	/**
	 * Adds or removes a collection of members from a WorkArea Function
	 * (i.e., role.)
	 * 
	 * @param wa			- The WorkArea to update.
	 * @param functionId	- The ID of function to update on the WorkArea.
	 * @param add			- true -> Add the members to the WorkArea for the function.  false -> Remove them.
	 * @param memberIds		- Collection of member IDs to be added or removed.
	 */
	@Override
	public void updateWorkAreaFunctionMemberships(WorkArea wa, Long functionId, boolean add, Collection<Long> memberIds) {
		// Ensure that this WorkArea is NOT inheriting membership.
		setWorkAreaFunctionMembershipInherited(wa, false);
		
		// Is there a WorkAreaFunctionMembership on this WorkArea?
		WorkAreaFunctionMembership wafm = getWorkAreaFunctionMembership(wa, functionId);
		final boolean finalNewWAFM = (null == wafm);
		if (finalNewWAFM) {
			// No!  If we're clearing the members...
			if (!add) {
				// ...simply bail as there's nothing we need to do.
				return;
			}
			
			// Create a WorkAreaFunctionMembership for it now.
			wafm = new WorkAreaFunctionMembership();
			wafm.setFunctionId(functionId);
			wafm.setWorkAreaId(wa.getWorkAreaId());
			wafm.setWorkAreaType(wa.getWorkAreaType());
			wafm.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		}
		
		// Does the Function have any members on this WorkArea?
		Set<Long> wafmMemberIds = wafm.getMemberIds();
		if (null == wafmMemberIds) {
			// No!  Create an empty set to store them in.
			wafmMemberIds = new HashSet<Long>();
			wafm.setMemberIds(wafmMemberIds);
		}

		// We're we given any member IDs to set?
		int changeCount = 0;
		if (MiscUtil.hasItems(memberIds)) {
			// Yes!  Scan them.
			for (Long memberId:  memberIds) {
				// Are we setting the member on this function?
				boolean setHasMember = wafmMemberIds.contains(memberId);
				if (add) {
					// Yes!  If it's already a member... 
					if (setHasMember) {
						// ...then nothing is changing...skip it...
						continue;
					}
					
					// ...otherwise, add it to the set.
					wafmMemberIds.add(memberId);
					changeCount += 1;
				}
				
				else {
					// No, we must be clearing the member!  If it isn't
					// a member...
					if (!setHasMember) {
						// ...then nothing is changing...skip it...
						continue;
					}
					
					// ...otherwise, from it from the set.
					wafmMemberIds.remove(memberId);
					changeCount += 1;
				}
			}
		}
		
		// If we aren't making any changes to the function
		// membership...
		if (0 == changeCount) {
			// ...simply bail as there's nothing we need to do.
			return;
		}

		// Finally, add/update membership on the WorkArea Function.
		final boolean						finalHasMembers = MiscUtil.hasItems(wafmMemberIds);
		final WorkAreaFunctionMembership	finalWAFM       = wafm;
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				if      ( finalNewWAFM)    getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(   finalWAFM);
				else if (!finalHasMembers) getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(finalWAFM);
				else                       getWorkAreaFunctionMembershipManager().updateWorkAreaFunctionMembership(finalWAFM);
				return null;
			}
		});
	}

	@Override
	public void updateWorkAreaFunctionMembership(WorkArea wa, Long functionId, boolean set, Long memberId) {
		// Always use the initial form of the method.
		Collection<Long> memberIds = new ArrayList<Long>();
		memberIds.add(memberId);
		updateWorkAreaFunctionMemberships(wa, functionId, set, memberIds);
	}
	
	/**
	 * Resets the member list on a WorkArea Function (i.e., role.)
	 * 
	 * @param wa			- The WorkArea whose function membership is to be reset.
	 * @param functionId	- The ID of the function whose membership is to be reset.
	 * @param memberIds		- Collection of member IDs to be stored.  If empty, the function will be removed. 
	 */
	@Override
	public void resetWorkAreaFunctionMemberships(WorkArea wa, Long functionId, Collection<Long> memberIds) {
		// Ensure that this WorkArea is NOT inheriting membership.
		setWorkAreaFunctionMembershipInherited(wa, false);
		
		// Is there a WorkAreaFunctionMembership on this WorkArea?
		WorkAreaFunctionMembership wafm = getWorkAreaFunctionMembership(wa, functionId);
		final boolean finalNewWAFM = (null == wafm);
		if (finalNewWAFM) {
			// No!  If there aren't any members to store...
			if (!(MiscUtil.hasItems(memberIds))) {
				// ...simply bail as there's nothing we need to do.
				return;
			}
			
			// Create a WorkAreaFunctionMembership for it now.
			wafm = new WorkAreaFunctionMembership();
			wafm.setFunctionId(functionId);
			wafm.setWorkAreaId(wa.getWorkAreaId());
			wafm.setWorkAreaType(wa.getWorkAreaType());
			wafm.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		}
		
		// Ensure we have a non-null collection of member IDs.
		if (null == memberIds) {
			memberIds = new HashSet<Long>();
		}
		
		// Is there currently a member set stored on the function?
		Set<Long> wafmMemberIds = wafm.getMemberIds();
		if (null == wafmMemberIds) {
			// No!  Create an empty set for them with the members we
			// were given.
			wafmMemberIds = new HashSet<Long>(memberIds);
			wafm.setMemberIds(wafmMemberIds);
		}
		
		else {
			// Yes, there is a member set on the function!  Clear its
			// contents and store the new member list.
			wafmMemberIds.clear();
			wafmMemberIds.addAll(memberIds);
		}
		
		// Finally, reset the membership on the WorkArea Function.
		final boolean						finalHasMembers = MiscUtil.hasItems(wafmMemberIds);
		final WorkAreaFunctionMembership	finalWAFM       = wafm;
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				if      ( finalNewWAFM)    getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(   finalWAFM);
				else if (!finalHasMembers) getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(finalWAFM);
				else                       getWorkAreaFunctionMembershipManager().updateWorkAreaFunctionMembership(finalWAFM);
				return null;
			}
		});
	}

	@Override
	public void resetWorkAreaFunctionMembership(WorkArea wa, Long functionId, Long memberId) {
		// Always use the initial form of the method.
		Collection<Long> memberIds = new ArrayList<Long>();
		memberIds.add(memberId);
		resetWorkAreaFunctionMemberships(wa, functionId, memberIds);
	}
	
    @Override
	public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
		// open to anyone - only way to get parentMemberships
    	// checkAccess(workArea, "getWorkAreaFunctionMembership");

        return getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
       		(RequestContextHolder.getRequestContext().getZoneId(), workArea, functionId);
    }
    
	@Override
	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMemberships(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		//checkAccess(workArea, "getWorkAreaFunctionMemberships");

		List<WorkAreaFunctionMembership> memberships = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
        		RequestContextHolder.getRequestContext().getZoneId(), workArea);

	    //See if this is a workarea with external ACLs
	    List<WorkAreaFunctionMembership> extMemberships = new ArrayList<WorkAreaFunctionMembership>();
	    List<WorkAreaFunctionMembership> filteredExtMemberships = new ArrayList<WorkAreaFunctionMembership>();
		if (workArea.isAclExternallyControlled()) {
			WorkArea sourceExt = workArea;
			if (workArea.isExtFunctionMembershipInherited()) {
			    while (sourceExt.isExtFunctionMembershipInherited()) {
			    	sourceExt = sourceExt.getParentWorkArea();
			    	if (sourceExt == null) break;
			    }
			}
			if (sourceExt != null) {
				extMemberships = getWorkAreaFunctionMembershipManager()
						.findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), sourceExt);
			}
			//Filter out any functions that are not the external role type
			for (WorkAreaFunctionMembership wfm : extMemberships) {
				Function f = getFunction(wfm.getFunctionId());
				if (f.getScope().equals(workArea.getRegisteredRoleType())) filteredExtMemberships.add(wfm);
			}
		}
        
        //Merge the two sets of memberships with deference to the external ACLs
        for (WorkAreaFunctionMembership wfm : filteredExtMemberships) {
        	if (memberships.contains(wfm)) {
        		//Remove the wrong membership 
        		memberships.remove(wfm);
        	}
        	//Add in the setting from the external ACL
        	memberships.add(wfm);
        }
        return memberships;
}

	@Override
	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMembershipsInherited(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		// checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
		List<WorkAreaFunctionMembership> memberships = new ArrayList<WorkAreaFunctionMembership>();
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return new ArrayList();
	    while (source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    	//template binders have this problem, cause they are not connected to a 
	    	//root until instanciated, but want to inherit from a future parent
	    	if (source == null) return new ArrayList();
	    }
	    
	    //See if this is a workarea with external ACLs
	    List<WorkAreaFunctionMembership> extMemberships = new ArrayList<WorkAreaFunctionMembership>();
	    List<WorkAreaFunctionMembership> filteredExtMemberships = new ArrayList<WorkAreaFunctionMembership>();
		if (workArea.isAclExternallyControlled()) {
			WorkArea sourceExt = workArea;
			if (workArea.isExtFunctionMembershipInherited()) {
			    while (sourceExt.isExtFunctionMembershipInherited()) {
			    	sourceExt = sourceExt.getParentWorkArea();
			    	if (sourceExt == null) break;
			    }
			}
			if (sourceExt != null) {
				extMemberships = getWorkAreaFunctionMembershipManager()
						.findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), sourceExt);
			}
			//Filter out any functions that are not the external role type
			for (WorkAreaFunctionMembership wfm : extMemberships) {
				Function f = getFunction(wfm.getFunctionId());
				if (f.getScope().equals(workArea.getRegisteredRoleType())) filteredExtMemberships.add(wfm);
			}
		}
        memberships = getWorkAreaFunctionMembershipManager()
        		.findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), source);
        
        //Merge the two sets of memberships with deference to the external ACLs
        for (WorkAreaFunctionMembership wfm : filteredExtMemberships) {
        	if (memberships.contains(wfm)) {
        		//Remove the wrong membership 
        		memberships.remove(wfm);
        	}
        	//Add in the setting from the external ACL
        	memberships.add(wfm);
        }
        return memberships;
	}

	//Routine to return the workarea that access control is being inherited from
	public WorkArea getWorkAreaFunctionInheritance(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		// checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return source;
	    while (source != null && source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    }
        return source;
	}
	//no transaction
	@Override
	public void setWorkAreaFunctionMembershipInherited(final WorkArea workArea, final boolean inherit) 
    		throws AccessControlException {
		setWorkAreaFunctionMembershipInherited(workArea, inherit, Boolean.FALSE, ObjectKeys.ROLE_TYPE_FILR);
	}

    /*
     * Routine to set whether a workarea inherits from its parent folder or not.
     * 
     * Depending on the "justThisScope" flag, this routine will skip any function with the 
     * specified scope or it will only change functions with the specified scope. The other 
     * functions are left unchanged. This capability is used to support workareas that have 
     * functions (roles) that are being controlled externally, such as with Filr ACLs. 
     * 
     * If "justThisScope" is true, then modify only those functions with the specified scope
     * If "justThisScope" is false, then modify all functions except the ones with the specified scope
     */
	@Override
	public void setWorkAreaFunctionMembershipInherited(final WorkArea workArea, final boolean inherit,
			final boolean justThisScope, final String scope) throws AccessControlException {
    	checkAccess(workArea, AdminOperation.manageFunctionMembership);
    	final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
        Boolean index = (Boolean) getTransactionTemplate().execute(new TransactionCallback() {
        	@Override
			public Object doInTransaction(TransactionStatus status) {
        		if (inherit) {
        			//remove them
        			List current = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), workArea);
        			for (int i=0; i<current.size(); ++i) {
		    	        WorkAreaFunctionMembership wfm = (WorkAreaFunctionMembership)current.get(i);
		    	        if (Validator.isNotNull(scope)) {
		    	        	Function f = getWorkAreaFunctionMembershipManager().getFunction(zoneId, wfm.getFunctionId());
		    	        	if (f != null) {
	      						if ((scope.equals(f.getScope()) && justThisScope) || 
	    								(!scope.equals(f.getScope()) && !justThisScope)) {
	      							//Delete the functions according to the scope
	      							//  This check is used to skip functions that are externally controlled
	      							getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
	      						}
		    	        	}
		    	        } else {
		    	        	//Delete all wfm if no scope specified
		    	        	getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
		    	        }
        			}
    		
        		} else if (workArea.isFunctionMembershipInherited() && !inherit) {
        			//copy parent values as beginning values
        			if (workArea.getParentWorkArea() != null) {
        				getWorkAreaFunctionMembershipManager().copyWorkAreaFunctionMemberships(
        						RequestContextHolder.getRequestContext().getZoneId(),
        						getWorkAreaFunctionInheritance(workArea), 
        						workArea,
        						justThisScope,
        						scope);
        			}
        		}
        	   	//see if there is a real change
            	if (!dealingWithExternalAcl(justThisScope, scope) && workArea.isFunctionMembershipInherited() != inherit) {
            		workArea.setFunctionMembershipInherited(inherit);
             		processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
             		//just changed from not inheritting to inherit = need to update index
            		//if changed from inherit to not, index remains the same
              		if (inherit) return Boolean.TRUE;
            	}
            	else if(dealingWithExternalAcl(justThisScope, scope) && workArea.isExtFunctionMembershipInherited() != inherit) {
            		workArea.setExtFunctionMembershipInherited(inherit);
             		processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
             		//just changed from not inheritting to inherit = need to update index
            		//if changed from inherit to not, index remains the same
              		if (inherit) return Boolean.TRUE;
            	}
        		return Boolean.FALSE;
        	}});
        //index outside of transaction
        if (index && (workArea instanceof Binder)) {
			Binder binder = (Binder)workArea;
			loadBinderProcessor(binder).indexFunctionMembership(binder, true, null, true);
		}
     }
	
	private void processAccessChangeLog(WorkArea workArea, String operation) {
		if ((workArea instanceof Binder) && !(workArea instanceof TemplateBinder)) {
        	Binder binder = (Binder)workArea;
        	User user = RequestContextHolder.getRequestContext().getUser();
        	binder.incrLogVersion();
        	binder.setModification(new HistoryStamp(user));
        	loadBinderProcessor(binder).processChangeLog(binder, operation);
       	}
	}
	
	@Override
	public void setEntryHasAcl(final WorkArea workArea, final Boolean hasAcl, final Boolean checkFolderAcl) {
        //Make sure this user is allowed to do this
		if (workArea instanceof Entry && ((Entry)workArea).isTop()) {
			try {
				if (((Entry)workArea).hasEntryAcl()) {
					getAccessControlManager().checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);
				} else {
					getAccessControlManager().checkOperation(((Entry)workArea).getParentBinder(), WorkAreaOperation.CREATE_ENTRY_ACLS);
				}
			} catch(AccessControlException ex) {
				if (!((Entry)workArea).hasEntryAcl() || ((Entry)workArea).isIncludeFolderAcl()) {
					try {
						getAccessControlManager().checkOperation(((Entry)workArea).getParentBinder(), WorkAreaOperation.CREATOR_CREATE_ENTRY_ACLS);
					} catch(AccessControlException ex2) {
						return;
					}
				} else {
					return;
				}
			}
		
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	@Override
				public Object doInTransaction(TransactionStatus status) {
	           		if (workArea instanceof Entry) {
	        			//Set the entry acl flag
	           			((Entry)workArea).setHasEntryAcl(hasAcl);
	        	        //Set the entry checkFolderAcl flag
	           			((Entry)workArea).setCheckFolderAcl(checkFolderAcl);
	           		}
					return null;
	        	}});
		}
	}

	@Override
	public void setEntryHasExternalAcl(final WorkArea workArea, final Boolean hasExternalAcl) {
        //Make sure this user is allowed to do this
		if (workArea instanceof Entry && ((Entry)workArea).isTop()) {
			try {
				if (((Entry)workArea).hasEntryExternalAcl()) {
					getAccessControlManager().checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);
				} else {
					getAccessControlManager().checkOperation(((Entry)workArea).getParentBinder(), WorkAreaOperation.CREATE_ENTRY_ACLS);
				}
			} catch(AccessControlException ex) {
				if (!((Entry)workArea).hasEntryExternalAcl()) {
					try {
						getAccessControlManager().checkOperation(((Entry)workArea).getParentBinder(), WorkAreaOperation.CREATOR_CREATE_ENTRY_ACLS);
					} catch(AccessControlException ex2) {
						return;
					}
				} else {
					return;
				}
			}
		
			//Set the entry external acl flag
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	@Override
				public Object doInTransaction(TransactionStatus status) {
	           		if (workArea instanceof Entry) ((Entry)workArea).setHasEntryExternalAcl(hasExternalAcl);
					return null;
	        	}});
		}
	}

	/**
	 * Send a mail message to a collection of users and/or explicit email addresses.  Includes attachments from entries if specified.
	 *   
	 * @param entry - may be null
	 * @param ids - toList
	 * @param emailAddresses
	 * @param ccIds - ccoList
	 * @param bccIds - bccList
	 * @param subject
	 * @param body
	 * @param sendAttachments
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
    @Override
	public Map<String, Object> sendMail(Entry entry, Collection<Long> ids, Collection<Long> teamIds, Collection<String> emailAddresses, Collection<Long> ccIds, 
    		Collection<Long> bccIds, String subject, Description body, boolean sendAttachments) throws Exception {
		if (!getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()).getMailConfig().isSendMailEnabled()) {
			throw new ConfigurationException(NLT.get("errorcode.sendmail.disabled"));
		}
    	User user = RequestContextHolder.getRequestContext().getUser();
   		String userName = Utils.getUserTitle(user);
		List errors = new ArrayList();
		Map result = new HashMap();
		result.put(ObjectKeys.SENDMAIL_ERRORS, errors);
		//add email address listed 
		Set<Long> userIds = new HashSet(ids);
		//get team members
		if (teamIds != null && !teamIds.isEmpty()) {
			List<Binder> teams = getCoreDao().loadObjects(teamIds, Binder.class, user.getZoneId());
			for (Binder t:teams) {
				userIds.addAll(t.getTeamMemberIds());
			}
		}
		boolean removedAllUsersGroup = checkIfRemovedSendToAllUsers(userIds);
		Set emailSet = getEmail(userIds, errors);
		if (emailAddresses != null) {
			if (emailSet == null) emailSet = new HashSet();
			for (String e: emailAddresses) {
				if (!Validator.isNull(e)) {
					try {
						InternetAddress ia = new InternetAddress(e.trim());
						ia.validate();
						emailSet.add(ia);
					} catch (Exception ex) {
						errors.add(NLT.get("errorcode.badToAddress", new Object[] {userName, e, ex.getLocalizedMessage()}));						
					}
				}
			}
		}

		if (removedAllUsersGroup) {
			errors.add(0, NLT.get("errorcode.noSendToAllUsers"));
		}
		if (emailSet == null || emailSet.isEmpty()) {
			//no-one to send to
			errors.add(0, NLT.get("errorcode.noRecipients"));
			return result;			
		}
    	Map message = new HashMap();
   		String fromEMA = null;
       	try {
       		fromEMA = MiscUtil.getFromOverride();
       		if (!(MiscUtil.hasString(fromEMA))) {
       			fromEMA = user.getEmailAddress();
       		}
       		InternetAddress ia = new InternetAddress(fromEMA);
       		if ((null != userName) && (0 < userName.length())) {
       			ia.setPersonal(userName);
       		}
    		message.put(MailModule.FROM, ia);
    	} catch (Exception ex) {
			String errorMsg = ex.getLocalizedMessage();
			String emailAddr = fromEMA;
			if (emailAddr == null || emailAddr.equals("")) {
				emailAddr = "";
				errorMsg = NLT.get("sendMail.noEmailAddress");
			}
			errors.add(0, NLT.get("errorcode.badFromAddress", new Object[] {Utils.getUserTitle(user), emailAddr, errorMsg})); 
			//cannot send without valid from address
			return result;
    	}
   		EmailUtil.putHTML(message, MailModule.HTML_MSG, body.getText());
   		EmailUtil.putText(message, MailModule.TEXT_MSG, (Html.stripHtml(body.getText()) + "\r\n"));
   		
    	message.put(MailModule.SUBJECT, subject);
 		message.put(MailModule.TO, emailSet);
 		message.put(MailModule.CC, getEmail(ccIds, errors));
		message.put(MailModule.BCC, getEmail(bccIds, errors));
		message.put(MailModule.LOG_TYPE, EmailLogType.sendMail);
 		MailSentStatus results;
 		if (entry != null) {
 			results = getMailModule().sendMail(entry, message, Utils.getUserTitle(user) + " email", sendAttachments);    		
 		} else {
 			results = getMailModule().sendMail(RequestContextHolder.getRequestContext().getZone(), message, Utils.getUserTitle(user) + " email");    		
    	}
		result.put(ObjectKeys.SENDMAIL_STATUS, results);
		return result;
    }
    
    /**
     * Send a mail message to a collection of users and/or explicit email addresses.
     *  
     * @param ids - toList
     * @param emailAddresses
 	 * @param ccIds - ccoList
	 * @param bccIds - bccList
     * @param subject
     * @param body
     * 
     * @return
     * 
     * @throws Exception
     */
    @Override
	public Map<String, Object> sendMail(Collection<Long> ids, Collection<Long> teamIds, Collection<String> emailAddresses, Collection<Long> ccIds, 
    		Collection<Long> bccIds, String subject, Description body) throws Exception {
    	// Always use the initial form of the method.
    	return sendMail(null, ids, teamIds, emailAddresses, ccIds, bccIds, subject, body, false); 
    }
    
    /**
     * Send a share notification mail message to a collection of users
     * and/or explicit email addresses.
     * 
     * @param share
     * @param sharedEntity
     * @param principalIds
     * @param teamIds
     * @param emailAddresses
     * @param ccIds
     * @param bccIds
     * 
     * @return
     * 
     * @throws Exception
     */
	@Override
    public Map<String, Object> sendMail(ShareItem share, DefinableEntity sharedEntity, Collection<Long> principalIds, Collection<Long> teamIds,
    		Collection<String> emailAddresses, Collection<Long> ccIds, Collection<Long> bccIds) throws Exception {
    	// If sending email is not enabled in the system...
		if (!(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()).getMailConfig().isSendMailEnabled())) {
			// ...throw an appropriate exception.
			throw new ConfigurationException(NLT.get("errorcode.sendmail.disabled"));
		}

		// Allocate the error tracking/reply objects.
		List<SendMailErrorWrapper>	errors = new ArrayList<SendMailErrorWrapper>();
		Map							result = new HashMap();
		result.put(ObjectKeys.SENDMAIL_ERRORS, errors);
		
		// Allocate the maps of email addresses to locales we'll
		// use for sending the notifications in the appropriate
		// locale(s).
		Map<Locale, List<InternetAddress>> toIAsMap  = new HashMap<Locale, List<InternetAddress>>();
		Map<Locale, List<InternetAddress>> ccIAsMap  = new HashMap<Locale, List<InternetAddress>>();
		Map<Locale, List<InternetAddress>> bccIAsMap = new HashMap<Locale, List<InternetAddress>>();

		// Process the recipient collections we received into the
		// appropriate email address to locale map.
		EmailHelper.addPrincipalsToLocaleMap(              MailModule.TO,  toIAsMap,  MiscUtil.validateCL(principalIds  ));
		EmailHelper.addTeamsToLocaleMap(getBinderModule(), MailModule.TO,  toIAsMap,  MiscUtil.validateCL(teamIds       ));
		EmailHelper.addEMAsToLocaleMap(                    MailModule.TO,  toIAsMap,  MiscUtil.validateCS(emailAddresses));
		EmailHelper.addPrincipalsToLocaleMap(              MailModule.CC,  ccIAsMap,  MiscUtil.validateCL(ccIds         ));
		EmailHelper.addPrincipalsToLocaleMap(              MailModule.BCC, bccIAsMap, MiscUtil.validateCL(bccIds        ));

		// - - - - - - - - - - - - - - - - - - - - - - - - - - //
		// Once we get here, we have maps containing the valid //
		// email addresses we need to send notifications to    //
		// mapped with the locale to use to send them.         //
		// - - - - - - - - - - - - - - - - - - - - - - - - - - //

		// Get what we need from the zone for sending email.
		Workspace		zone       = RequestContextHolder.getRequestContext().getZone();
		MailModule		mm         = getMailModule();
		String			mailSender = mm.getNotificationMailSenderName(zone);
		String			defaultEMA = mm.getNotificationDefaultFrom(   zone); 

		// Get what we need about the sending user for sending email.
		Date			now         = new Date();
		User			sendingUser = RequestContextHolder.getRequestContext().getUser();
		TimeZone		tz          = sendingUser.getTimeZone();
		InternetAddress	sendingIA   = new InternetAddress();
		String			sendersEMA  = sendingUser.getEmailAddress();
		sendingIA.setAddress(MiscUtil.hasString(sendersEMA) ? sendersEMA : defaultEMA);

		// What Velocity template should we use for this share?
		String template;
		if (sharedEntity instanceof FolderEntry)
			 template = "sharedEntryNotification.vm";
		else template = "sharedFolderNotification.vm";
		
		// Scan the unique Locale's we need to localize the share
		// notification email into.
		boolean			notifyAsBCC   = SPropsUtil.getBoolean("mail.notifyAsBCC", true);
		List<Locale>	targetLocales = MiscUtil.validateLL(EmailHelper.getTargetLocales(toIAsMap, ccIAsMap, bccIAsMap));
		for (Locale locale:  targetLocales) {
			// Extract the TO:, CC: and BCC: lists for this Locale.
			List<InternetAddress> toIAs  = MiscUtil.validateIAL(toIAsMap.get( locale));
			List<InternetAddress> ccIAs  = MiscUtil.validateIAL(ccIAsMap.get( locale));
			List<InternetAddress> bccIAs = MiscUtil.validateIAL(bccIAsMap.get(locale));
			
			// If we we're supposed to send notifications as BCC:s...
			if (notifyAsBCC && (!(toIAs.isEmpty()))) {
				// ...move the TO:s to the BCC:s.
				bccIAs.addAll(toIAs);
				toIAs.clear();
			}

			// Allocate a Map to hold the email components for building
			// the mime...
			Map mailMap = new HashMap();
			
			// ...add the from...
			mailMap.put(MailModule.FROM, sendingIA);

			// ...add the recipients...
			mailMap.put(MailModule.TO,  toIAs );
			mailMap.put(MailModule.CC,  ccIAs );
			mailMap.put(MailModule.BCC, bccIAs);

			// ...add the subject...
			String shareTitle = sharedEntity.getTitle();
			if (!(MiscUtil.hasString(shareTitle))) {
				shareTitle = ("--" + NLT.get("entry.noTitle", locale) + "--");
			}
			String subject = NLT.get("relevance.mailShared", new Object[]{Utils.getUserTitle(sendingUser), shareTitle}, locale);
			mailMap.put(MailModule.SUBJECT, subject);
			
			// ...generate and add the HTML variant...
			StringWriter	writer  = new StringWriter();
			Notify			notify  = new Notify(NotifyType.summary, locale, tz, now);
           	NotifyVisitor	visitor = new NotifyVisitor(sharedEntity, notify, null, writer, NotifyVisitor.WriterType.HTML, null);
		    VelocityContext	ctx     = getShareVelocityContext(visitor, share, sharedEntity);
			visitor.processTemplate(template, ctx);
			EmailUtil.putHTML(mailMap, MailModule.HTML_MSG, writer.toString());
			
			// ...generate and add the TEXT variant...
			writer  = new StringWriter();
			notify  = new Notify(NotifyType.summary, locale, tz, now);
           	visitor = new NotifyVisitor(sharedEntity, notify, null, writer, NotifyVisitor.WriterType.TEXT, null);
		    ctx     = getShareVelocityContext(visitor, share, sharedEntity);
			visitor.processTemplate(template, ctx);
			EmailUtil.putText(mailMap, MailModule.TEXT_MSG, writer.toString());

			// ...create the mime helper... 
			MimeSharePreparator helper = new MimeSharePreparator(mailMap, logger);
			helper.setDefaultFrom(sendingUser.getEmailAddress());
			
			try {
				// ...and send the email.
				mm.sendMail(mailSender, helper);
			}
			
	 		catch (MailSendException ex) {
	 			// The send failed!  Log the exception...
	 			String exMsg = EmailHelper.getMailExceptionMessage(ex);
	 			logger.error("EXCEPTION:  Error sending share notification:" + exMsg);
				logger.debug("EXCEPTION", ex);
				errors.add(new SendMailErrorWrapper(ex, exMsg));

				// ...and if there were any send failed
				// ...sub-exceptions...
				Exception[] exceptions = ex.getMessageExceptions();
				int exCount = ((null == exceptions) ? 0 : exceptions.length);
	 			if ((0 < exCount) && exceptions[0] instanceof SendFailedException) {
	 				// ...return them in the error list too.
	 				SendFailedException sf = ((SendFailedException) exceptions[0]);
	 				EmailHelper.addMailFailures(errors, sf.getInvalidAddresses(),     "share.notify.invalidAddresses"    );
	 				EmailHelper.addMailFailures(errors, sf.getValidUnsentAddresses(), "share.notify.validUnsentAddresses");
	 				
	 			}
	 	   	}
	 		
	 		catch (MailAuthenticationException ex) {
	 			// The send failed because we couldn't authenticate to
	 			// the email server!  Log the exception.
	 			String exMsg = EmailHelper.getMailExceptionMessage(ex);
	       		logger.error("EXCEPTION:  Authentication Exception:" + exMsg);				
				logger.debug("EXCEPTION", ex);
				errors.add(new SendMailErrorWrapper(ex, exMsg));
	 		} 
		}

		// If we get here, result refers to a map of any errors, ...
		// Return it.
    	return result;
    }

    /**
     * Sends a confirmation mail message to an external user.
     * 
     * @param externalUserId
     * @param entityPermalinkUrl
     * 
     * @return
     * 
     * @throws Exception
     */
	@Override
    public Map<String, Object> sendConfirmationMailToExternalUser(Long externalUserId, String entityPermalinkUrl) throws Exception {
    	// If sending email is not enabled in the system...
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		if (!(getCoreDao().loadZoneConfig(zoneId).getMailConfig().isSendMailEnabled())) {
			// ...throw an appropriate exception.
			throw new ConfigurationException(NLT.get("errorcode.sendmail.disabled"));
		}

		// Allocate the error tracking/reply objects.
		List<SendMailErrorWrapper>	errors = new ArrayList<SendMailErrorWrapper>();
		Map							result = new HashMap();
		result.put(ObjectKeys.SENDMAIL_ERRORS, errors);
		
		// Allocate the maps of email addresses to locales we'll
		// use for sending the notifications in the appropriate
		// locale(s).
		Map<Locale, List<InternetAddress>> toIAsMap  = new HashMap<Locale, List<InternetAddress>>();
		Map<Locale, List<InternetAddress>> ccIAsMap  = new HashMap<Locale, List<InternetAddress>>();
		Map<Locale, List<InternetAddress>> bccIAsMap = new HashMap<Locale, List<InternetAddress>>();
		
		// Process the external user we received into the appropriate
		// email address to locale map.
		List<Long> externalUserIds = new ArrayList<Long>();
		externalUserIds.add(externalUserId);
		boolean notifyAsBCC = SPropsUtil.getBoolean("mail.notifyAsBCC", true);
		if (notifyAsBCC)
		     EmailHelper.addPrincipalsToLocaleMap(MailModule.BCC, bccIAsMap, MiscUtil.validateCL(externalUserIds));
		else EmailHelper.addPrincipalsToLocaleMap(MailModule.TO,  toIAsMap,  MiscUtil.validateCL(externalUserIds));

		// - - - - - - - - - - - - - - - - - - - - - - - - - - //
		// Once we get here, we have maps containing the valid //
		// email addresses we need to send notifications to    //
		// mapped with the locale to use to send them.         //
		// - - - - - - - - - - - - - - - - - - - - - - - - - - //

		// Get what we need from the zone for sending email.
		Workspace		zone       = RequestContextHolder.getRequestContext().getZone();
		MailModule		mm         = getMailModule();
		String			mailSender = mm.getNotificationMailSenderName(zone);
		String			defaultEMA = mm.getNotificationDefaultFrom(   zone); 

		// Get what we need about the sending user for sending email.
		Date			now         = new Date();
		User			sendingUser = RequestContextHolder.getRequestContext().getUser();
		TimeZone		tz          = sendingUser.getTimeZone();
		InternetAddress	sendingIA   = new InternetAddress();
		String			sendersEMA  = sendingUser.getEmailAddress();
		sendingIA.setAddress(MiscUtil.hasString(sendersEMA) ? sendersEMA : defaultEMA);

		// Scan the unique Locale's we need to localize the share
		// notification email into.
		List<Locale>	targetLocales = MiscUtil.validateLL(EmailHelper.getTargetLocales(toIAsMap, ccIAsMap, bccIAsMap));
		for (Locale locale:  targetLocales) {
			// Extract the TO:, CC: and BCC: lists for this Locale.
			List<InternetAddress> toIAs  = MiscUtil.validateIAL(toIAsMap.get( locale));
			List<InternetAddress> ccIAs  = MiscUtil.validateIAL(ccIAsMap.get( locale));
			List<InternetAddress> bccIAs = MiscUtil.validateIAL(bccIAsMap.get(locale));
			
			// Allocate a Map to hold the email components for building
			// the mime...
			Map mailMap = new HashMap();
			
			// ...add the from...
			mailMap.put(MailModule.FROM, sendingIA);

			// ...add the recipients...
			mailMap.put(MailModule.TO,  toIAs );
			mailMap.put(MailModule.CC,  ccIAs );
			mailMap.put(MailModule.BCC, bccIAs);

			// ...add the subject...
			mailMap.put(MailModule.SUBJECT, NLT.get("relevance.mailConfirm", locale));
			
			// ...generate and add the HTML variant...
			StringWriter	writer  = new StringWriter();
			Notify			notify  = new Notify(NotifyType.summary, locale, tz, now);
           	NotifyVisitor	visitor = new NotifyVisitor(null, notify, null, writer, NotifyVisitor.WriterType.HTML, null);
		    VelocityContext	ctx     = getConfirmationVelocityContext(visitor, entityPermalinkUrl);
			visitor.processTemplate("externalConfirmation.vm", ctx);
			EmailUtil.putHTML(mailMap, MailModule.HTML_MSG, writer.toString());
			
			// ...generate and add the TEXT variant...
			writer  = new StringWriter();
			notify  = new Notify(NotifyType.summary, locale, tz, now);
           	visitor = new NotifyVisitor(null, notify, null, writer, NotifyVisitor.WriterType.TEXT, null);
		    ctx     = getConfirmationVelocityContext(visitor, entityPermalinkUrl);
			visitor.processTemplate("externalConfirmation.vm", ctx);
			EmailUtil.putText(mailMap, MailModule.TEXT_MSG, writer.toString());

			// ...create the mime helper... 
			MimeSharePreparator helper = new MimeSharePreparator(mailMap, logger);
			helper.setDefaultFrom(sendingUser.getEmailAddress());
			
			try {
				// ...and send the email.
				mm.sendMail(mailSender, helper);
			}
			
	 		catch (MailSendException ex) {
	 			// The send failed!  Log the exception...
	 			String exMsg = EmailHelper.getMailExceptionMessage(ex);
	 			logger.error("EXCEPTION:  Error sending confirmation mail:" + exMsg);
				logger.debug("EXCEPTION", ex);
				errors.add(new SendMailErrorWrapper(ex, exMsg));

				// ...and if there were any send failed
				// ...sub-exceptions...
				Exception[] exceptions = ex.getMessageExceptions();
				int exCount = ((null == exceptions) ? 0 : exceptions.length);
	 			if ((0 < exCount) && exceptions[0] instanceof SendFailedException) {
	 				// ...return them in the error list too.
	 				SendFailedException sf = ((SendFailedException) exceptions[0]);
	 				EmailHelper.addMailFailures(errors, sf.getInvalidAddresses(),     "share.notify.invalidAddresses"    );
	 				EmailHelper.addMailFailures(errors, sf.getValidUnsentAddresses(), "share.notify.validUnsentAddresses");
	 				
	 			}
	 	   	}
	 		
	 		catch (MailAuthenticationException ex) {
	 			// The send failed because we couldn't authenticate to
	 			// the email server!  Log the exception.
	 			String exMsg = EmailHelper.getMailExceptionMessage(ex);
	       		logger.error("EXCEPTION:  Authentication Exception:" + exMsg);				
				logger.debug("EXCEPTION", ex);
				errors.add(new SendMailErrorWrapper(ex, exMsg));
	 		} 
		}

		// If we get here, result refers to a map of any errors, ...
		// Return it.
    	return result;
    }

    /**
     * Sends a share invitation mail message to an external user.
     * 
     * @param share
     * @param sharedEntity
     * @param externalUserId
     * 
     * @return
     * 
     * @throws Exception
     */
	@Override
    public Map<String, Object> sendShareInviteMailToExternalUser(ShareItem share, DefinableEntity sharedEntity, Long externalUserId) throws Exception {
    	// If sending email is not enabled in the system...
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		if (!(getCoreDao().loadZoneConfig(zoneId).getMailConfig().isSendMailEnabled())) {
			// ...throw an appropriate exception.
			throw new ConfigurationException(NLT.get("errorcode.sendmail.disabled"));
		}

		// Allocate the error tracking/reply objects.
		List<SendMailErrorWrapper>	errors = new ArrayList<SendMailErrorWrapper>();
		Map							result = new HashMap();
		result.put(ObjectKeys.SENDMAIL_ERRORS, errors);
		
		// Allocate the maps of email addresses to locales we'll
		// use for sending the notifications in the appropriate
		// locale(s).
		Map<Locale, List<InternetAddress>> toIAsMap  = new HashMap<Locale, List<InternetAddress>>();
		Map<Locale, List<InternetAddress>> ccIAsMap  = new HashMap<Locale, List<InternetAddress>>();
		Map<Locale, List<InternetAddress>> bccIAsMap = new HashMap<Locale, List<InternetAddress>>();
		
		// Process the external user we received into the appropriate
		// email address to locale map.
		List<Long> externalUserIds = new ArrayList<Long>();
		externalUserIds.add(externalUserId);
		boolean notifyAsBCC = SPropsUtil.getBoolean("mail.notifyAsBCC", true);
		if (notifyAsBCC)
		     EmailHelper.addPrincipalsToLocaleMap(MailModule.BCC, bccIAsMap, MiscUtil.validateCL(externalUserIds));
		else EmailHelper.addPrincipalsToLocaleMap(MailModule.TO,  toIAsMap,  MiscUtil.validateCL(externalUserIds));

		// - - - - - - - - - - - - - - - - - - - - - - - - - - //
		// Once we get here, we have maps containing the valid //
		// email addresses we need to send notifications to    //
		// mapped with the locale to use to send them.         //
		// - - - - - - - - - - - - - - - - - - - - - - - - - - //

		// Get what we need from the zone for sending email.
		Workspace		zone       = RequestContextHolder.getRequestContext().getZone();
		MailModule		mm         = getMailModule();
		String			mailSender = mm.getNotificationMailSenderName(zone);
		String			defaultEMA = mm.getNotificationDefaultFrom(   zone); 

		// Get what we need about the sending user for sending email.
		Date			now         = new Date();
		User			sendingUser = RequestContextHolder.getRequestContext().getUser();
		TimeZone		tz          = sendingUser.getTimeZone();
		InternetAddress	sendingIA   = new InternetAddress();
		String			sendersEMA  = sendingUser.getEmailAddress();
		sendingIA.setAddress(MiscUtil.hasString(sendersEMA) ? sendersEMA : defaultEMA);

		// What Velocity template should we use for this mail?
		String template;
		if (sharedEntity instanceof FolderEntry)
			 template = "sharedEntryInvite.vm";
		else template = "sharedFolderInvite.vm";
		
		// Scan the unique Locale's we need to localize the share
		// notification email into.
		List<Locale>	targetLocales = MiscUtil.validateLL(EmailHelper.getTargetLocales(toIAsMap, ccIAsMap, bccIAsMap));
		for (Locale locale:  targetLocales) {
			// Extract the TO:, CC: and BCC: lists for this Locale.
			List<InternetAddress> toIAs  = MiscUtil.validateIAL(toIAsMap.get( locale));
			List<InternetAddress> ccIAs  = MiscUtil.validateIAL(ccIAsMap.get( locale));
			List<InternetAddress> bccIAs = MiscUtil.validateIAL(bccIAsMap.get(locale));
			
			// Allocate a Map to hold the email components for building
			// the mime...
			Map mailMap = new HashMap();
			
			// ...add the from...
			mailMap.put(MailModule.FROM, sendingIA);

			// ...add the recipients...
			mailMap.put(MailModule.TO,  toIAs );
			mailMap.put(MailModule.CC,  ccIAs );
			mailMap.put(MailModule.BCC, bccIAs);

			// ...add the subject...
			String shareTitle = sharedEntity.getTitle();
			if (!(MiscUtil.hasString(shareTitle))) {
				shareTitle = ("--" + NLT.get("entry.noTitle", locale) + "--");
			}
			String subject = NLT.get("relevance.mailInvite", new Object[]{Utils.getUserTitle(sendingUser)}, locale);
			subject       += " (" + shareTitle +")";
			mailMap.put(MailModule.SUBJECT, subject);
			
			// ...generate the encrypted ID for the the targeted
			// ...external user...
			User shareRecipient = getProfileDao().loadUser(share.getRecipientId(), zoneId);
			String encodedExternalUserId = ExternalUserUtil.encodeUserTokenWithNewSeed(shareRecipient);
			
			// ...generate and add the HTML variant...
			StringWriter	writer  = new StringWriter();
			Notify			notify  = new Notify(NotifyType.summary, locale, tz, now);
           	NotifyVisitor	visitor = new NotifyVisitor(sharedEntity, notify, null, writer, NotifyVisitor.WriterType.HTML, null);
		    VelocityContext	ctx     = getShareVelocityContext(visitor, share, sharedEntity, encodedExternalUserId);
			visitor.processTemplate(template, ctx);
			EmailUtil.putHTML(mailMap, MailModule.HTML_MSG, writer.toString());
			
			// ...generate and add the TEXT variant...
			writer  = new StringWriter();
			notify  = new Notify(NotifyType.summary, locale, tz, now);
           	visitor = new NotifyVisitor(sharedEntity, notify, null, writer, NotifyVisitor.WriterType.TEXT, null);
		    ctx     = getShareVelocityContext(visitor, share, sharedEntity, encodedExternalUserId);
			visitor.processTemplate(template, ctx);
			EmailUtil.putText(mailMap, MailModule.TEXT_MSG, writer.toString());

			// ...create the mime helper... 
			MimeSharePreparator helper = new MimeSharePreparator(mailMap, logger);
			helper.setDefaultFrom(sendingUser.getEmailAddress());
			
			try {
				// ...and send the email.
				mm.sendMail(mailSender, helper);
			}
			
	 		catch (MailSendException ex) {
	 			// The send failed!  Log the exception...
	 			String exMsg = EmailHelper.getMailExceptionMessage(ex);
	 			logger.error("EXCEPTION:  Error sending share invitation mail:" + exMsg);
				logger.debug("EXCEPTION", ex);
				errors.add(new SendMailErrorWrapper(ex, exMsg));

				// ...and if there were any send failed
				// ...sub-exceptions...
				Exception[] exceptions = ex.getMessageExceptions();
				int exCount = ((null == exceptions) ? 0 : exceptions.length);
	 			if ((0 < exCount) && exceptions[0] instanceof SendFailedException) {
	 				// ...return them in the error list too.
	 				SendFailedException sf = ((SendFailedException) exceptions[0]);
	 				EmailHelper.addMailFailures(errors, sf.getInvalidAddresses(),     "share.notify.invalidAddresses"    );
	 				EmailHelper.addMailFailures(errors, sf.getValidUnsentAddresses(), "share.notify.validUnsentAddresses");
	 				
	 			}
	 	   	}
	 		
	 		catch (MailAuthenticationException ex) {
	 			// The send failed because we couldn't authenticate to
	 			// the email server!  Log the exception.
	 			String exMsg = EmailHelper.getMailExceptionMessage(ex);
	       		logger.error("EXCEPTION:  Authentication Exception:" + exMsg);				
				logger.debug("EXCEPTION", ex);
				errors.add(new SendMailErrorWrapper(ex, exMsg));
	 		} 
		}

		// If we get here, result refers to a map of any errors, ...
		// Return it.
    	return result;
    }

	/*
	 * Returns a VelocityContext to use for confirmation notification
	 * emails. 
	 */
	private static VelocityContext getConfirmationVelocityContext(NotifyVisitor visitor, String entityPermalinkUrl) {
		// Create the context...
	    VelocityContext	reply = NotifyBuilderUtil.getVelocityContext();
	    
	    // ...initialize it...
		reply.put("ssVisitor",            visitor                                           );
		reply.put("user",                 RequestContextHolder.getRequestContext().getUser());
		reply.put("ssEntityPermalinkurl", entityPermalinkUrl                                );
		
		// ...and return it.
		return reply;
	}
	
	/*
	 * Returns a VelocityContext to use for share notification emails. 
	 */
	private static VelocityContext getShareVelocityContext(NotifyVisitor visitor, ShareItem share, DefinableEntity sharedEntity, String encodedExternalUserId) {
		// Create the context...
	    VelocityContext	reply = NotifyBuilderUtil.getVelocityContext();
	    
	    // ...initialize it...
	    User user = RequestContextHolder.getRequestContext().getUser();
		reply.put("ssVisitor",         visitor                                                                                                     );
		reply.put("ssShare",           share                                                                                                       );
		reply.put("ssSharedEntity",    sharedEntity                                                                                                );
		reply.put("ssShareExpiration", EmailHelper.getShareExpiration(visitor.getNotifyDef().getLocale(), share)                                   );
		reply.put("ssSharer",          NLT.get("share.notify.sharer", new String[]{visitor.getUserTitle(user)}, visitor.getNotifyDef().getLocale()));
		reply.put("ssProduct",         (Utils.checkIfFilr() ? "Filr" : "Vibe")                                                                     );
		reply.put("user",              user                                                                                                        );
		if (MiscUtil.hasString(encodedExternalUserId)) {
			reply.put("ssShareEncodedExternalUserId", encodedExternalUserId);
		}
		
		// ...and return it.
		return reply;
	}
	
	private static VelocityContext getShareVelocityContext(NotifyVisitor visitor, ShareItem share, DefinableEntity sharedEntity) {
		// Always use the initial form of the method.
		return getShareVelocityContext(visitor, share, sharedEntity, null);
	}
	
    private Set<InternetAddress> getEmail(Collection<Long>ids, List errors) {
    	Set<InternetAddress> addrs=null;
    	if (ids != null && !ids.isEmpty()) {
    		boolean sendingToAllUsersIsAllowed = EmailHelper.canSendToAllUsers();
    		Long allUsersGroupId = Utils.getAllUsersGroupId();
    		if (!sendingToAllUsersIsAllowed && ids.contains(allUsersGroupId)) {
    			ids.remove(allUsersGroupId);
    		}
    		Long allExtUsersGroupId = Utils.getAllExtUsersGroupId();
    		if (!sendingToAllUsersIsAllowed && ids.contains(allExtUsersGroupId)) {
    			ids.remove(allExtUsersGroupId);
    		}
			addrs = new HashSet();
 			Set<Long> cc = getProfileDao().explodeGroups(ids, 
 					RequestContextHolder.getRequestContext().getZoneId(), sendingToAllUsersIsAllowed);
 			List<User> users = getCoreDao().loadObjects(cc, User.class, RequestContextHolder.getRequestContext().getZoneId());
 			for (User e:users) {
 				try {
 					if (!e.isDeleted() && !e.isDisabled()) addrs.add(new InternetAddress(e.getEmailAddress().trim()));
 				} catch (Exception ex) {
 					String errorMsg = ex.getLocalizedMessage();
 					String emailAddr = e.getEmailAddress();
 					if (emailAddr == null || emailAddr.equals("")) {
 						emailAddr = "";
 						errorMsg = NLT.get("sendMail.noEmailAddress");
 					}
 					errors.add(NLT.get("errorcode.badToAddress", new Object[] {Utils.getUserTitle(e), emailAddr, errorMsg})); 
 				}
 			}
 		}
    	return addrs;
    }
    private boolean checkIfRemovedSendToAllUsers(Collection<Long>ids) {
    	if (ids != null && !ids.isEmpty()) {
    		boolean sendingToAllUsersIsAllowed = EmailHelper.canSendToAllUsers();
    		Long allUsersGroupId = Utils.getAllUsersGroupId();
    		Long allExtUsersGroupId = Utils.getAllExtUsersGroupId();
    		if (!sendingToAllUsersIsAllowed && 
    				(ids.contains(allUsersGroupId) || ids.contains(allExtUsersGroupId))) {
    			return true;
    		}
    	}
    	return false;
    }
    
   @Override
public List<ChangeLog> getChanges(Long binderId, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("owningBinderId", binderId);
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   List<ChangeLog> changeLogs = getCoreDao().loadObjects(ChangeLog.class, filter, RequestContextHolder.getRequestContext().getZoneId());
		  return filterChangeLogs(changeLogs, false);
	   //need to filter for access
   }
   @Override
public List<ChangeLog> getChanges(EntityIdentifier entityIdentifier, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("entityId", entityIdentifier.getEntityId());
	   filter.add("entityType", entityIdentifier.getEntityType().name());
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   List<ChangeLog> changeLogs = getCoreDao().loadObjects(ChangeLog.class, filter, RequestContextHolder.getRequestContext().getZoneId());
	   return filterChangeLogs(changeLogs, false); 
   	
   }
   
   @Override
public List<ChangeLog> getEntryHistoryChanges(EntityIdentifier entityIdentifier) {
	   FilterControls filter = new FilterControls();
	   filter.add("entityId", entityIdentifier.getEntityId());
	   filter.add("entityType", entityIdentifier.getEntityType().name());
	   OrderBy order = new OrderBy();
	   order.addColumn("version", OrderBy.DESCENDING);	   
	   filter.setOrderBy(order);
	   
	   List<ChangeLog> changeLogs = getCoreDao().loadObjects(ChangeLog.class, filter, RequestContextHolder.getRequestContext().getZoneId()); 
	   
	   List<ChangeLog> entryChangeLogs = filterEntryHistoryChanges(changeLogs);
	   
	   return filterChangeLogs(entryChangeLogs, true); 
   	
   }
   private List<ChangeLog> filterEntryHistoryChanges(List<ChangeLog> changeLogs) {
	   List<ChangeLog> entryChangeLogs = new ArrayList<ChangeLog>();
	   for (ChangeLog log: changeLogs) {
		   if(log.getOperation().equals(ChangeLog.ADDENTRY)
	               || log.getOperation().equals(ChangeLog.MODIFYENTRY)
               || log.getOperation().equals(ChangeLog.MODIFYWORKFLOWSTATE)
               || log.getOperation().equals(ChangeLog.MODIFYWORKFLOWSTATEONREPLY)
               || log.getOperation().equals(ChangeLog.ADDWORKFLOWRESPONSE)
			   || log.getOperation().equals(ChangeLog.FILEADD)
			   || log.getOperation().equals(ChangeLog.FILEMODIFY)
			   || log.getOperation().equals(ChangeLog.FILEMODIFY_INCR_MAJOR_VERSION)
			   || log.getOperation().equals(ChangeLog.FILEMODIFY_ENCRYPT)
			   || log.getOperation().equals(ChangeLog.FILEMODIFY_REVERT)
			   || log.getOperation().equals(ChangeLog.FILEMODIFY_SET_COMMENT)
			   || log.getOperation().equals(ChangeLog.FILEMODIFY_SET_STATUS)
			   || log.getOperation().equals(ChangeLog.FILERENAME))
			   entryChangeLogs.add(log);
	   }
	   return entryChangeLogs;
   }
   @Override
public List<ChangeLog> getWorkflowChanges(EntityIdentifier entityIdentifier, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("entityId", entityIdentifier.getEntityId());
	   filter.add("entityType", entityIdentifier.getEntityType().name());
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   List<ChangeLog> changeLogs = getCoreDao().loadObjects(ChangeLog.class, filter, RequestContextHolder.getRequestContext().getZoneId()); 
	   
	   List<ChangeLog> wfChangeLogs = filterWorkflowChanges(changeLogs);
	   
	   return filterChangeLogs(wfChangeLogs, true); 
   	
   }
   
   private List<ChangeLog> filterWorkflowChanges(List<ChangeLog> changeLogs) {
	   List<ChangeLog> wfChangeLogs = new ArrayList<ChangeLog>();
	   for (ChangeLog log: changeLogs) {
		   if(log.getOperation().equals(ChangeLog.STARTWORKFLOW)
               || log.getOperation().equals(ChangeLog.MODIFYWORKFLOWSTATEONREPLY)
			   || log.getOperation().equals(ChangeLog.MODIFYWORKFLOWSTATE)
			   || log.getOperation().equals(ChangeLog.ENDWORKFLOW)
			   || log.getOperation().equals(ChangeLog.ADDWORKFLOWRESPONSE)
			   || log.getOperation().equals(ChangeLog.WORKFLOWTIMEOUT)
			   || log.getOperation().equals(ChangeLog.ADDENTRY))
			   		wfChangeLogs.add(log);
	   }
	   return wfChangeLogs;
   }
   private List<ChangeLog> filterChangeLogs(List<ChangeLog> changeLogs, Boolean addLogsWithNoAcls) {
	   User user = RequestContextHolder.getRequestContext().getUser();
	   if (user.isSuper()) return changeLogs;
	   // get the current users acl set
	   Set<Long> userAclSet = getProfileDao().getApplicationLevelPrincipalIds(user);
	   Set userStringIds = new HashSet();
	   for (Long id:userAclSet) {
		   userStringIds.add(id.toString());
	   }
	   List<ChangeLog> result = new ArrayList();
	   for (ChangeLog log: changeLogs) {
		   try {
			   Document doc = log.getDocument();
			   if (doc == null) continue;
			   Element root = doc.getRootElement();
			   if (root == null) continue;
			   Element acl = (Element)root.selectSingleNode(Constants.FOLDER_ACL_FIELD);
			   if (acl == null && addLogsWithNoAcls) {
				   //If there is no acl info, add the log entry anyway
				   result.add(log);
			   } else {
				   if (AccessUtils.checkAccess(root, userStringIds)) result.add(log);
			   }
		   } catch (Exception ex) {
			   logger.error("Error processing change log: " + log.getId() + " " + ex.getLocalizedMessage());
		   }
	   }
	   return result;

   }
   
	@Override
	public String obtainApplicationScopedToken(long applicationId, long userId) {
		RequestContext rc = RequestContextHolder.getRequestContext();

		// check caller has right
		getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(rc.getZoneId()), WorkAreaOperation.TOKEN_REQUEST);
		
		// check application exists
		@SuppressWarnings("unused")
		Application application = getProfileDao().loadApplication(applicationId, rc.getZoneId());
		
		// check user exists
		User user = getProfileDao().loadUser(userId, rc.getZoneId());

		String result = getAccessTokenManager().getApplicationScopedToken
		(applicationId, userId, RequestContextHolder.getRequestContext().getUserId())
		.toStringRepresentation();
		
		getReportModule().addTokenInfo(rc.getUser(), user, applicationId);
		
		return result;
	}
	
	@Override
	public void destroyApplicationScopedToken(String token) {
		// check caller has right - we simply check the same right needed for token request 
		// (that is, no separate right for destroying it)
		getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.TOKEN_REQUEST);
		
		getAccessTokenManager().destroyApplicationScopedToken(new AccessToken(token));
	}
	
	@Override
	public void optimizeIndex(String[] nodeNames) throws ManageIndexException, AccessControlException {
		checkAccess(AdminOperation.manageIndex);
		
		LuceneWriteSession luceneSession = getLuceneSessionFactory()
				.openWriteSession(nodeNames);
		try {
			luceneSession.optimize();
		}
		catch(Exception e) {
			throw new ManageIndexException("errorcode.optimize.index", null, e);
		} finally {
			luceneSession.close();
		}
	}
	
	@Override
	public void addFunctionCondition(Condition functionCondition) {
		checkAccess(AdminOperation.manageFunctionCondition);
		getSecurityDao().save(functionCondition);
	}
	
	@Override
	public void modifyFunctionCondition(Condition functionCondition) {
		checkAccess(AdminOperation.manageFunctionCondition);
		getSecurityDao().update(functionCondition);
	}
	
	@Override
	public void deleteFunctionCondition(Long functionConditionId) {
		checkAccess(AdminOperation.manageFunctionCondition);
		try {
			Condition functionCondition = getSecurityDao().loadFunctionCondition(RequestContextHolder.getRequestContext().getZoneId(), functionConditionId);
			getSecurityDao().delete(functionCondition);
		}
		catch(NoObjectByTheIdException e) {
			// already gone - no problem
		}
	}
	
	@Override
	public Condition getFunctionCondition(Long functionConditionId) {
		// let anyone read it?
		return getSecurityDao().loadFunctionCondition(RequestContextHolder.getRequestContext().getZoneId(), functionConditionId);
	}
	
	@Override
	public List<Condition> getFunctionConditions() {
		// let anyone read them - is this right?
		return getSecurityDao().findFunctionConditions(RequestContextHolder.getRequestContext().getZoneId());
	}

	@SuppressWarnings("unused")
	private void testFunctionCondition() {
		AdminModule am = (AdminModule) SpringContextUtil.getBean("adminModule");
		int i = 0;
		if(i == 1) {
			// Add conditions
			am.addFunctionCondition(new RemoteAddrCondition("First condition", new String[] {"127.0.0.1", "127.0.0.2"}, new String[] {"127.0.0.3"}));
			am.addFunctionCondition(new RemoteAddrCondition("Second condition", new String[] {"192.168.0.*"}, new String[] {"192.168.0.1", "192.168.0.2"}));
		}
		else if(i == 2) {
			// Modify conditions
			List<Condition> conditions = am.getFunctionConditions();
			for(Condition cond:conditions) {
				cond.setTitle(cond.getTitle() + " - modified");
				am.modifyFunctionCondition(cond);
			}
		}
		else if(i == 3) {
			// Delete condition
			Long functionConditionId = new Long(0); // set this
			am.deleteFunctionCondition(functionConditionId);
		}
		
		if(i == 4) {
			// Add function
			Set<WorkAreaOperation> operations = new HashSet<WorkAreaOperation>();
			operations.add(WorkAreaOperation.ADD_COMMUNITY_TAGS);
			operations.add(WorkAreaOperation.ADD_REPLIES);
			Function f = am.addFunction("jong_function", operations, ObjectKeys.ROLE_TYPE_BINDER, new ArrayList());
		}
		else if(i == 5) {
			// Add conditions to function
			List<Function> functions = am.getFunctions(ObjectKeys.ROLE_TYPE_BINDER);
			String name = "jong_function"; // set this properly
			for(Function function:functions) {
				if(function.getName().equals(name)) {
					List<ConditionalClause> cc = new ArrayList<ConditionalClause>();
					List<Condition> conditions = am.getFunctionConditions();
					for(Condition condition:conditions) {
						cc.add(new ConditionalClause(condition, ConditionalClause.Meet.MUST));
					}
					Map updates = new HashMap();
					updates.put("conditionalClauses", cc);
					am.modifyFunction(function.getId(), updates);
				}
			}
		}
		else if(i == 6) {
			// Modify conditions in function
			long functionId = 0; // set this properly
			Function function = am.getFunction(functionId);
			List<ConditionalClause> cc = function.getConditionalClauses();
			cc.get(0).setMeet(ConditionalClause.Meet.SHOULD);
			cc.remove(1);
			Map updates = new HashMap();
			updates.put("conditionalClauses", cc);
			am.modifyFunction(functionId, updates);
		}
		else if(i == 7) {
			// Delete function
			long functionId = 0; // set this properly
			Function function = am.getFunction(functionId);
			am.deleteFunction(function.getId());
		}
	}
	
	//Check if a workarea has conditions that affect the read right
	private boolean checkIfConditionsExist(WorkArea workArea) {
		List<WorkAreaFunctionMembership> membership;
		if (workArea.isFunctionMembershipInherited()) {
			membership = getWorkAreaFunctionMembershipsInherited(workArea);
		} else {
			membership = getWorkAreaFunctionMemberships(workArea);
		}
		for (WorkAreaFunctionMembership wfm : membership) {
			//Get the function (aka role)
			Function f = getFunction(wfm.getFunctionId());
			if (!f.getConditionalClauses().isEmpty()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public IndexOptimizationSchedule getIndexOptimizationSchedule() {
		checkAccess(AdminOperation.manageIndex);
		ScheduleInfo si = getIndexOptimizationObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
		return new IndexOptimizationSchedule(si);
	}
	
	@Override
	public void setIndexOptimizationSchedule(IndexOptimizationSchedule schedule) {
		checkAccess(AdminOperation.manageIndex);
		getIndexOptimizationObject().setScheduleInfo(schedule.getScheduleInfo());
	}
	
	protected IndexOptimization getIndexOptimizationObject() {
		return getIndexOptimizationObject(RequestContextHolder.getRequestContext().getZoneName());
	}
	
	protected IndexOptimization getIndexOptimizationObject(String zoneName) {
		String jobClass = getIndexProperty(zoneName, INDEX_OPTIMIZATION_JOB);
		if(Validator.isNotNull(jobClass)) {
			try {
				return (IndexOptimization) ReflectHelper.getInstance(jobClass);
			}
			catch(Exception e) {
				logger.error("Cannot instantiate IndexOptimization custom class", e);
			}
		}
		return (IndexOptimization) ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultIndexOptimization.class);
	}
	
	protected String getIndexProperty(String zoneName, String name) {
		return SZoneConfig.getString(zoneName, "indexConfiguration/property[@name='" + name + "']");
	}
	
	@Override
	public String dumpRuntimeStatisticsAsString() {
		checkAccess(AdminOperation.manageRuntime);
		RuntimeStatistics rs = (RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics");
		return rs.dumpAllAsString();
	}
	
	@Override
	public void dumpRuntimeStatisticsToLog() {
		checkAccess(AdminOperation.manageRuntime);
		RuntimeStatistics rs = (RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics");
		rs.dumpAllToLog();
	}
	
	@Override
	public void enableSimpleProfiler() {
		checkAccess(AdminOperation.manageRuntime);
		RuntimeStatistics rs = (RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics");
		rs.setSimpleProfilerEnabled(true);
	}
	
	@Override
	public void disableSimpleProfiler() {
		checkAccess(AdminOperation.manageRuntime);
		RuntimeStatistics rs = (RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics");
		rs.setSimpleProfilerEnabled(false);
	}

	private boolean dealingWithExternalAcl(boolean justThisScope, String scope) {
		return (justThisScope && ObjectKeys.ROLE_TYPE_FILR.equals(scope));
	}
	
  	@Override
	public int getAuditTrailKeepDays() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getAuditTrailKeepDays(); 		
  	}
  	@Override
	public int getChangeLogsKeepDays() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getChangeLogsKeepDays(); 		
  	}
  	@Override
	public void setLogTableKeepDays(int auditTrailKeepDays, int changeLogsKeepDays) {
  	   	checkAccess(AdminOperation.manageLogTablePurge);
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setAuditTrailKeepDays(auditTrailKeepDays);
  		zoneConfig.setChangeLogsKeepDays(changeLogsKeepDays);
  	}

    @Override
	public ScheduleInfo getLogTablePurgeSchedule() {
    	return getLogTablePurgeObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
    }
    
    @Override
	public void setLogTablePurgeSchedule(ScheduleInfo info) {
  	   	checkAccess(AdminOperation.manageLogTablePurge);
  	   	LogTablePurge obj = getLogTablePurgeObject();
    	obj.setScheduleInfo(info);
    	obj.enable(true, RequestContextHolder.getRequestContext().getZoneId());
    }

    private LogTablePurge getLogTablePurgeObject() {
    	String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		String jobClass = getLogTablePurgeProperty(zoneName, LOG_TABLE_PURGE_JOB);
    	if (Validator.isNotNull(jobClass)) {
		   try {
			   return  (LogTablePurge)ReflectHelper.getInstance(jobClass);
		   } catch (Exception ex) {
			   logger.error("Cannot instantiate LogTablePurge custom class", ex);
		   }
   		}
   		return (LogTablePurge)ReflectHelper.getInstance(
   				org.kablink.teaming.jobs.DefaultLogTablePurge.class);
    }
    
	//See if there is a custom scheduling job being specified
    protected String getLogTablePurgeProperty(String zoneName, String name) {
		return SZoneConfig.getString(zoneName, "logTablePurgeConfiguration/property[@name='" + name + "']");
	}
}
