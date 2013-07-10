/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.InternetAddress;

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
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.HomePageConfig;
import org.kablink.teaming.domain.MailConfig;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WeekendsAndHolidaysConfig;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.EmailLog.EmailLogType;
import org.kablink.teaming.extension.ExtensionManager;
import org.kablink.teaming.jobs.EmailNotification;
import org.kablink.teaming.jobs.EmailPosting;
import org.kablink.teaming.jobs.FileVersionAging;
import org.kablink.teaming.jobs.IndexOptimization;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.IndexOptimizationSchedule;
import org.kablink.teaming.module.admin.ManageIndexException;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.binder.processor.EntryProcessor;
import org.kablink.teaming.module.dashboard.DashboardModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.mail.EmailUtil;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.mail.MailSentStatus;
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
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Html;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * @author Janet McCann
 *
 */
public abstract class AbstractAdminModule extends CommonDependencyInjection implements AdminModule {
	
	protected static String INDEX_OPTIMIZATION_JOB = "index.optimization.job"; // properties in xml file need a unique name
	protected static String FILE_VERSION_AGING_JOB = "file.version.aging.job"; // properties in xml file need a unique name
	
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
	public ExtensionManager getExtensionManager() {
		return extensionManager;
	}
	public void setExtensionManager(ExtensionManager extensionManager) {
		this.extensionManager = extensionManager;
	}

	public void deleteExtension(String id){
		checkAccess(AdminOperation.manageExtensions);
		
		Object obj = getCoreDao().load(ExtensionInfo.class, id);
		if(obj != null && obj instanceof ExtensionInfo)
		{
			coreDao.delete(obj);
		}
	}
	
	public void addExtension(ExtensionInfo extension) {
		checkAccess(AdminOperation.manageExtensions);
		coreDao.save(extension);
	}
	public void modifyExtension(ExtensionInfo extension) {
		checkAccess(AdminOperation.manageExtensions);
		
		coreDao.update(extension);
	}
	/**
   	 * Use operation so we can keep the logic out of application
	 * and easisly change the required rights
	 */
   	public boolean testAccess(WorkArea workArea, AdminOperation operation) {
   		try {
   			checkAccess(workArea, operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
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
	 * and easisly change the required rights
   	 * 
   	 */
  	public boolean testAccess(AdminOperation operation) {
   		try {
   			checkAccess(operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
  	public void checkAccess(AdminOperation operation) {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
		switch (operation) {
			case manageFunction:
			case manageFunctionCondition:
			case manageMail:
			case manageFileVersionAging:
			case manageFileSizeLimit:
			case manageTemplate:
			case manageErrorLogs:
  			case manageFunctionMembership:
  			case manageRuntime:
  			case manageFileSynchApp:
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
  	public boolean isQuotaEnabled() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.isDiskQuotaEnabled(); 		
  	}
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
  	public Integer getQuotaDefault() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getDiskQuotaUserDefault();
  	}
  	public Integer getQuotaHighWaterMark() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getDiskQuotasHighwaterPercentage();
  	}
  	public void setQuotaDefault(Integer quotaDefault) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (zoneConfig.getDiskQuotaUserDefault() == quotaDefault) return; // if no change, do nothing
  		zoneConfig.setDiskQuotaUserDefault(quotaDefault);
  	}
  	public void setQuotaHighWaterMark(Integer quotaHighWaterMark) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (zoneConfig.getDiskQuotasHighwaterPercentage() == quotaHighWaterMark) return; // if no change, do nothing
  		zoneConfig.setDiskQuotasHighwaterPercentage(quotaHighWaterMark);
  	}
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
    public void setBinderQuotasInitialized(boolean binderQuotaInitialized) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setBinderQuotasInitialized(binderQuotaInitialized);
    }
    public void setBinderQuotasEnabled(boolean binderQuotaEnabled, boolean allowBinderOwner) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setBinderQuotasEnabled(binderQuotaEnabled, allowBinderOwner);
    }
    public boolean isBinderQuotaInitialized() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.isBinderQuotaInitialized(); 		
    }
    public boolean isBinderQuotaEnabled() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (zoneConfig.isBinderQuotaInitialized()) {
  			return zoneConfig.isBinderQuotaEnabled(); 		
  		} else {
  			return false;
  		}
    }
    public boolean isBinderQuotaAllowBinderOwnerEnabled() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.isBinderQuotaAllowBinderOwnerEnabled(); 		
    }

  	public void setFileVersionsMaxAge(Long fileVersionsMaxAge) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		if (zoneConfig.getFileVersionsMaxAge() == fileVersionsMaxAge) return; // if no change, do nothing
  		zoneConfig.setFileVersionsMaxAge(fileVersionsMaxAge);
  	}

  	public boolean isMobileAccessEnabled() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.isMobileAccessEnabled(); 		
  	}
  	public void setMobileAccessEnabled(boolean mobileAccessEnabled) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setMobileAccessEnabled(mobileAccessEnabled);
  	}
  	public HomePageConfig getHomePageConfig() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return new HomePageConfig(zoneConfig.getHomePageConfig()); 		
  	}
  	public void setHomePageConfig(HomePageConfig homePageConfig) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setHomePageConfig(homePageConfig); 		
  	}
  	public WeekendsAndHolidaysConfig getWeekendsAndHolidaysConfig() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return new WeekendsAndHolidaysConfig(zoneConfig.getWeekendsAndHolidaysConfig()); 		
  	}
  	public void setWeekendsAndHolidaysConfig(WeekendsAndHolidaysConfig weekendsAndHolidaysConfig) {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setWeekendsAndHolidaysConfig(weekendsAndHolidaysConfig); 		
  	}
  	public Long getFileVersionsMaxAge() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getFileVersionsMaxAge();
  	}
  	public void setFileVersionAgingDays(Long fileVersionAge) {
  	   	checkAccess(AdminOperation.manageFileVersionAging);
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setFileVersionsMaxAge(fileVersionAge);
  	}
  	public Long getFileSizeLimitUserDefault() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return zoneConfig.getFileSizeLimitUserDefault();
  	}
  	public void setFileSizeLimitUserDefault(Long fileSizeLimitUserDefault) {
  	   	checkAccess(AdminOperation.manageFileSizeLimit);
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setFileSizeLimitUserDefault(fileSizeLimitUserDefault);
  	}
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
  	public MailConfig getMailConfig() {
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		return new MailConfig(zoneConfig.getMailConfig()); 		
  	}
  	//try to keep these in sync using one call
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
 		//this is being phased out
 		if (posting != null) {
 			posting.setEnabled(posting.isEnabled());
 			getPostingObject().setScheduleInfo(posting);
 		} else if (wasPostingEnabled && !zoneConfig.getMailConfig().isPostingEnabled()) {
 			//remove it
 			getPostingObject().enable(false, zoneConfig.getZoneId());
 		}
  	}
    public List<PostingDef> getPostings() {
    	return coreDao.loadPostings(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void modifyPosting(String postingId, Map updates) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	ObjectBuilder.updateObject(post, updates);
    }
    public void addPosting(Map updates) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = new PostingDef();
    	post.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
       	ObjectBuilder.updateObject(post, updates);
       	post.setEmailAddress(post.getEmailAddress().toLowerCase());
      	coreDao.save(post);   	
    }
    public void deletePosting(String postingId) {
    	checkAccess(AdminOperation.manageMail);
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	if (post.getBinder() != null) {
    		post.getBinder().setPosting(null);
    	}
       	coreDao.delete(post);
    }
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

  	public void setFileVersionAgingSchedule(ScheduleInfo info) {
  	   	checkAccess(AdminOperation.manageFileVersionAging);
  		//even if schedules are running, these settings should stop the processing in the job
  	   	long zoneId = RequestContextHolder.getRequestContext().getZoneId();
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
 		if (info != null) {
 			getFileVersionAgingObject().setScheduleInfo(info);
 			getFileVersionAgingObject().enable(Boolean.TRUE, zoneId);
 		}
  	}

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
	public void updateDefaultDefinitions(AllModulesInjected bs, Long topId, Boolean newDefinitionsOnly, Collection ids) {
		Workspace top = (Workspace)getCoreDao().loadBinder(topId, topId);
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
    public Function getFunction(Long functionId) {
    	// let anyone read it
    	return functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), functionId);
    }
    public List<Function> getFunctions() {
		//let anyone read them			
    	return functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
    }
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
    public void setWorkAreaFunctionMemberships(final WorkArea workArea, 
    		final Map<Long, Set<Long>> functionMemberships) {
    	setWorkAreaFunctionMemberships(workArea, functionMemberships, Boolean.TRUE);
    }
    public void setWorkAreaFunctionMemberships(final WorkArea workArea, final Map<Long, 
    		Set<Long>> functionMemberships, boolean doCheckAccess) {
    	if (doCheckAccess) {
    		checkAccess(workArea, AdminOperation.manageFunctionMembership);
    	}
		final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		
		//get list of current readers to compare for indexing
		List<WorkAreaFunctionMembership>wfms = 
	       		getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.READ_ENTRIES);
       	TreeSet<Long> original = new TreeSet();
        for (WorkAreaFunctionMembership wfm:wfms) {
        	original.addAll(wfm.getMemberIds());
    	}
      	boolean conditionsExistInOrigianl = checkIfConditionsExist(workArea);
        //first remove any that are not in the new list
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		User guest = null;
       			try {
       				guest = getProfileDao().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zoneId);
       			} catch (NoObjectByTheIdException noexist) {};
        		//get list of current memberships
        		List<WorkAreaFunctionMembership>wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(zoneId, workArea);
        		for( WorkAreaFunctionMembership wfm:wfms) {
        			if (!functionMemberships.containsKey(wfm.getFunctionId()))
        				getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);       	
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
           		workArea.setFunctionMembershipInherited(false);
				processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
				return null;
        	}});
		//get new list of readers
      	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.READ_ENTRIES);
      	TreeSet<Long> current = new TreeSet();
      	for (WorkAreaFunctionMembership wfm:wfms) {
      		current.addAll(wfm.getMemberIds());
      	}
      	//only re-index if readers were affected.  Do outside transaction
      	boolean conditionsExist = checkIfConditionsExist(workArea);
		if ((!original.equals(current) || conditionsExist || conditionsExistInOrigianl) && (workArea instanceof Binder)) {
			Binder binder = (Binder)workArea;
			loadBinderProcessor(binder).indexFunctionMembership(binder, true);
		} else if (workArea instanceof Entry) {
			Entry entry = (Entry)workArea;
			List entries = new ArrayList();
			entries.add(entry);
			Set<Entry> children = entry.getChildWorkAreas();
			for (Entry e : children) entries.add(e);
			loadEntryProcessor(entry).indexEntries(entries);
		}
	}


	//no transaction
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
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
		// open to anyone - only way to get parentMemberships
    	// checkAccess(workArea, "getWorkAreaFunctionMembership");

        return getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
       		(RequestContextHolder.getRequestContext().getZoneId(), workArea, functionId);
    }
    
	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMemberships(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		//checkAccess(workArea, "getWorkAreaFunctionMemberships");

        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
        		RequestContextHolder.getRequestContext().getZoneId(), workArea);
	}

	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMembershipsInherited(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		// checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return new ArrayList();
	    while (source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    	//template binders have this problem, cause they are not connected to a 
	    	//root until instanciated, but want to inherit from a future parent
	    	if (source == null) return new ArrayList();
	    }
 
        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), source);
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
	public void setWorkAreaFunctionMembershipInherited(final WorkArea workArea, final boolean inherit) 
    throws AccessControlException {
    	checkAccess(workArea, AdminOperation.manageFunctionMembership);
        Boolean index = (Boolean) getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		if (inherit) {
        			//remove them
        			List current = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), workArea);
        			for (int i=0; i<current.size(); ++i) {
	    	        WorkAreaFunctionMembership wfm = (WorkAreaFunctionMembership)current.get(i);
	    	        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
        			}
    		
        		} else if (workArea.isFunctionMembershipInherited() && !inherit) {
        			//copy parent values as beginning values
        			if (workArea.getParentWorkArea() != null) getWorkAreaFunctionMembershipManager().copyWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(),getWorkAreaFunctionInheritance(workArea), workArea);
        		}
        	   	//see if there is a real change
            	if (workArea.isFunctionMembershipInherited()  != inherit) {
            		workArea.setFunctionMembershipInherited(inherit);
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
			loadBinderProcessor(binder).indexFunctionMembership(binder, true);
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
		
			//Set the entry acl flag
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	           		if (workArea instanceof Entry) ((Entry)workArea).setHasEntryAcl(hasAcl);
					return null;
	        	}});
	        //Set the entry checkFolderAcl flag
	        getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	           		if (workArea instanceof Entry) ((Entry)workArea).setCheckFolderAcl(checkFolderAcl);
					return null;
	        	}});
		}
	}

    public Map<String, Object> sendMail(Collection<Long> ids, Collection<Long> teamIds, Collection<String> emailAddresses, Collection<Long> ccIds, 
    		Collection<Long> bccIds, String subject, Description body) throws Exception {
    	return sendMail(null, ids, teamIds, emailAddresses, ccIds, bccIds, subject, body, false); 
    }
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

    private Set<InternetAddress> getEmail(Collection<Long>ids, List errors) {
    	Set<InternetAddress> addrs=null;
    	if (ids != null && !ids.isEmpty()) {
    		boolean sendingToAllUsersIsAllowed = SPropsUtil.getBoolean("mail.allowSendToAllUsers", false);
    		Long allUsersGroupId = Utils.getAllUsersGroupId();
    		if (!sendingToAllUsersIsAllowed && ids.contains(allUsersGroupId)) {
    			ids.remove(allUsersGroupId);
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
    		boolean sendingToAllUsersIsAllowed = SPropsUtil.getBoolean("mail.allowSendToAllUsers", false);
    		Long allUsersGroupId = Utils.getAllUsersGroupId();
    		if (!sendingToAllUsersIsAllowed && ids.contains(allUsersGroupId)) {
    			return true;
    		}
    	}
    	return false;
    }
    
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
	   Set<Long> userAclSet = getProfileDao().getPrincipalIds(user);
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
   
	public String obtainApplicationScopedToken(long applicationId, long userId) {
		RequestContext rc = RequestContextHolder.getRequestContext();

		// check caller has right
		getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(rc.getZoneId()), WorkAreaOperation.TOKEN_REQUEST);
		
		// check application exists
		Application application = getProfileDao().loadApplication(applicationId, rc.getZoneId());
		
		// check user exists
		User user = getProfileDao().loadUser(userId, rc.getZoneId());

		String result = getAccessTokenManager().getApplicationScopedToken
		(applicationId, userId, RequestContextHolder.getRequestContext().getUserId())
		.toStringRepresentation();
		
		getReportModule().addTokenInfo(rc.getUser(), user, applicationId);
		
		return result;
	}
	
	public void destroyApplicationScopedToken(String token) {
		// check caller has right - we simply check the same right needed for token request 
		// (that is, no separate right for destroying it)
		getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.TOKEN_REQUEST);
		
		getAccessTokenManager().destroyApplicationScopedToken(new AccessToken(token));
	}
	
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
	
	public void addFunctionCondition(Condition functionCondition) {
		checkAccess(AdminOperation.manageFunctionCondition);
		getSecurityDao().save(functionCondition);
	}
	
	public void modifyFunctionCondition(Condition functionCondition) {
		checkAccess(AdminOperation.manageFunctionCondition);
		getSecurityDao().update(functionCondition);
	}
	
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
	
	public Condition getFunctionCondition(Long functionConditionId) {
		// let anyone read it?
		return getSecurityDao().loadFunctionCondition(RequestContextHolder.getRequestContext().getZoneId(), functionConditionId);
	}
	
	public List<Condition> getFunctionConditions() {
		// let anyone read them - is this right?
		return getSecurityDao().findFunctionConditions(RequestContextHolder.getRequestContext().getZoneId());
	}

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
	
	public IndexOptimizationSchedule getIndexOptimizationSchedule() {
		checkAccess(AdminOperation.manageIndex);
		ScheduleInfo si = getIndexOptimizationObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
		return new IndexOptimizationSchedule(si);
	}
	
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
	
	public String dumpRuntimeStatisticsAsString() {
		checkAccess(AdminOperation.manageRuntime);
		RuntimeStatistics rs = (RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics");
		return rs.dumpAllAsString();
	}
	
	public void dumpRuntimeStatisticsToLog() {
		checkAccess(AdminOperation.manageRuntime);
		RuntimeStatistics rs = (RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics");
		rs.dumpAllToLog();
	}
	
	public void enableSimpleProfiler() {
		checkAccess(AdminOperation.manageRuntime);
		RuntimeStatistics rs = (RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics");
		rs.setSimpleProfilerEnabled(true);
	}
	
	public void disableSimpleProfiler() {
		checkAccess(AdminOperation.manageRuntime);
		RuntimeStatistics rs = (RuntimeStatistics) SpringContextUtil.getBean("runtimeStatistics");
		rs.setSimpleProfilerEnabled(false);
	}

}
