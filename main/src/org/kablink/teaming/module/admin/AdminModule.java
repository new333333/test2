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
package org.kablink.teaming.module.admin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.domain.BasicAudit;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.BinderQuota;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.ExtensionInfo;
import org.kablink.teaming.domain.HomePageConfig;
import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.domain.MailConfig;
import org.kablink.teaming.domain.MobileAppsConfig;
import org.kablink.teaming.domain.MobileOpenInWhiteLists;
import org.kablink.teaming.domain.NameCompletionSettings;
import org.kablink.teaming.domain.NoApplicationByTheIdException;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.OpenIDConfig;
import org.kablink.teaming.domain.OpenIDProvider;
import org.kablink.teaming.domain.PostingDef;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WeekendsAndHolidaysConfig;
import org.kablink.teaming.extension.ExtensionManager;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.Condition;
import org.kablink.teaming.security.function.ConditionalClause;
import org.kablink.teaming.security.function.Function;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaFunctionMembership;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DesktopApplicationsLists;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.web.util.EmailHelper.UrlNotificationType;

/**
 * ?
 * 
 * @author Janet McCann
 */
@SuppressWarnings("unchecked")
public interface AdminModule {
	public enum AdminOperation {
		manageFunction,
		manageMail,
		manageFileVersionAging,
		manageLogTablePurge,
		manageFileSizeLimit,
		manageTemplate,
		report,
		manageFunctionMembership,
		manageErrorLogs,
		manageExtensions,
		manageIndex,
		manageFunctionCondition,
		manageRuntime,
		manageResourceDrivers,
		manageFileSynchApp,
		manageOpenID,
		manageExternalUser,
		manageMobileApps
	}
	/**
	 * The method name to be called is used as the operation.   This
	 * allows the adminModule to check for multiple rights or change requirments in the future.
	 * @param operation
	 * @return
	 */
   	public boolean testUserAccess(User user, AdminOperation operation);
   	public void checkUserAccess(User user, AdminOperation operation) throws AccessControlException;
	/**
	 * The method name to be called is used as the operation.   This
	 * allows the adminModule to check for multiple rights or change requirments in the future.
	 * @param operation
	 * @return
	 */
   	public boolean testAccess(AdminOperation operation);
   	public void checkAccess(AdminOperation operation) throws AccessControlException;
   	/**
   	 * Same as <code>testAccess</code> 
   	 * @param workArea
   	 * @param operation
   	 * @return
   	 */
   	public boolean testAccess(WorkArea workArea, AdminOperation operation);
   	public void checkAccess(WorkArea workArea, AdminOperation operation) throws AccessControlException;
   	/**
	 * Add a new function
	 * @param name
	 * @param operations
	 * @throws AccessControlException
	 */
	public Function addFunction(String name, Set<WorkAreaOperation>operations, 
			String scope, List<ConditionalClause> conditions) throws AccessControlException;
	/**
	 * Add a posting definition, used to receive incoming mail
	 * @param updates
	 * @throws AccessControlException
	 */
	public void addPosting(Map updates) throws AccessControlException;
	/**
	 * Delete an existing function
	 * @param functionId
	 * @return List of workspace function memberships
	 */
    public List deleteFunction(Long functionId);
    /**
     * Delete a email posting definition
     * @param postingId
     * @throws AccessControlException
     */
    public void deletePosting(String postingId) throws AccessControlException;
    /**
     * Get change logs for a binder and its entries.  Null operation returns all changes.  Must have had access to 
     * the entity at the time of the change to see the change log.
     * @param binderId
     * @param operation
     * @return ChangeLogs ordered by entity and operationDate
     */
    public List<ChangeLog> getChanges(Long binderId, String operation);
    /**
     * Same as <code>getChanges</code> but only return changes for one entity. 
     * @param entityIdentifier
     * @param operation
     * @return ChangeLogs ordered by entity and operationDate
     */
    public List<ChangeLog> getChanges(EntityIdentifier entityIdentifier, String operation);
    
    public boolean isQuotaEnabled();
    public Integer getQuotaDefault();
    public Integer getQuotaHighWaterMark();
    public void setQuotaEnabled(boolean quotaEnabled);
    public void setQuotaDefault(Integer quotaDefault);
    public void setQuotaHighWaterMark(Integer quotaHighWaterMark);
    public BinderQuota getBinderQuota(Binder binder);
    public void setBinderQuota(Binder binder, BinderQuota binderQuota);
    public void setBinderQuotasInitialized(boolean binderQuotaInitialized);
    public boolean isBinderQuotaInitialized();
    public boolean isBinderQuotaEnabled();
    public boolean isBinderQuotaAllowBinderOwnerEnabled();
    public void setBinderQuotasEnabled(boolean quotaEnabled, boolean allowBinderOwner);
    
    public boolean isMobileAccessEnabled();
    public void setMobileAccessEnabled(boolean mobileAccessEnabled);
    public HomePageConfig getHomePageConfig();
    public void setHomePageConfig(HomePageConfig homePageConfig);
    public WeekendsAndHolidaysConfig getWeekendsAndHolidaysConfig();
    public void setWeekendsAndHolidaysConfig(WeekendsAndHolidaysConfig weekendsAndHolidaysConfig);
    public Long getFileVersionsMaxAge();
    public void setFileVersionsMaxAge(Long fileVersionAge);
    public Long getFileSizeLimitUserDefault();
    public Long getUserFileSizeLimit();
    public void setFileSizeLimitUserDefault(Long fileSizeLimitUserDefault);
    public MailConfig getMailConfig();
    public List<ChangeLog> getEntryHistoryChanges(EntityIdentifier entityIdentifier);
    public List<ChangeLog> getWorkflowChanges(EntityIdentifier entityIdentifier, String operation);
    public boolean isAdHocFoldersEnabled();
    public void setAdHocFoldersEnabled( boolean enabled );
    public boolean isDownloadEnabled();
    public void setDownloadEnabled( boolean enabled );
    public boolean isPasswordPolicyEnabled();
    public void setPasswordPolicyEnabled( boolean enabled );
    public boolean isAutoApplyDeferredUpdateLogs();
    public void setAutoApplyDeferredUpdateLogs( boolean autoApplyDeferredUpdateLogs );
    public boolean isWebAccessEnabled();
    public void setWebAccessEnabled( boolean enabled );
    public boolean isFileArchivingEnabled();
    public void setFileArchivingEnabled(boolean fileArchivingEnabled);
    public boolean isSharingWithLdapGroupsEnabled();
    public void setAllowShareWithLdapGroups( boolean allow );
    public int getAuditTrailKeepDays();
    public int getChangeLogsKeepDays();
    public boolean isAuditTrailEnabled();
    public boolean isChangeLogEnabled();
    public void setAuditTrailEnabled(boolean auditTrailEnabled);
    public void setChangeLogEnabled(boolean changeLogEnabled);
    public void setLogTableKeepDays(int auditTrailKeepDays, int changeLogsKeepDays);

    public NameCompletionSettings getNameCompletionSettings();
    public void setNameCompletionSettings( NameCompletionSettings settings );

    public boolean isUseDirectoryRightsEnabled();
    public void setUseDirectoryRightsEnabled( Boolean enabled );
    public Integer getCachedRightsRefreshInterval();
    public void setCachedRightsRefreshInterval( Integer value );

    /**
     * Get system functions
     * @return
     */
    public Function getFunction(Long functionId);
    public Function getFunctionByInternalId(String internalId);
    public List<Function> getFunctions();
    public List<Function> getFunctions(String scope);
    /**
     * Get the current schedule information for digest email notifications.
    * @return
     */
    public ScheduleInfo getNotificationSchedule();
    /**
     * Get system posting definitions
     * @return
     */
    public List<PostingDef> getPostings();
    /**
     * Get schedule information for receiving incoming email
     * @return
     */
    public ScheduleInfo getPostingSchedule();
    
    public ScheduleInfo getFileVersionAgingSchedule();
    public void setFileVersionAgingSchedule(ScheduleInfo info);
	
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId); 
    public List<WorkAreaFunctionMembership> getWorkAreaFunctionMemberships(WorkArea workArea);
	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMembershipsInherited(WorkArea workArea);
 
    public Function modifyFunction(Long functionId, Map<String, Object> updates) throws AccessControlException;
    public void modifyPosting(String postingId, Map<String, Object> updates)throws AccessControlException;
    
    public void setEntryHasAcl(final WorkArea workArea, final Boolean hasAcl, final Boolean checkFolderAcl);
    
    public void setEntryHasExternalAcl(final WorkArea workArea, final Boolean hasExternalAcl);
    public void setEntryHasExternalAcl(final WorkArea workArea, final Boolean hasExternalAcl, boolean skipFileContentIndexing);
    
	/**
	 * Send a mail message to a collection of users and/or explicit email addresses.  Includes attachments from entries if specified.
	 *   
	 * @param entry - may be null
	 * @param userIds - toList
	 * @param teamIds
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
    public Map<String, Object> sendMail(Entry entry, Collection<Long> userIds, Collection<Long> teamIds, Collection<String> emailAddresses, Collection<Long> ccIds, 
    		Collection<Long> bccIds, String subject, Description body,  boolean sendAttachments) throws Exception;
    
    /**
     * Send a mail message to a collection of users and/or explicit email addresses.
     *  
     * @param ids - toList
     * @param teamIds - toList
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
    public Map<String, Object> sendMail(Collection<Long> ids, Collection<Long> teamIds, Collection<String> emailAddresses, Collection<Long> ccIds, 
    		Collection<Long> bccIds, String subject, Description body) throws Exception;

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
    public Map<String, Object> sendMail(ShareItem share, DefinableEntity sharedEntity, Collection<Long> principalIds, Collection<Long> teamIds,
    		Collection<String> emailAddresses, Collection<Long> ccIds, Collection<Long> bccIds) throws Exception;
    	 
    /**
	 * Send a public link notification mail message to a collection of
	 * email addresses.
	 * 
	 * @param share				- Share item.
	 * @param sharedEntity		- Entity (folder or folder entry) being shared.
	 * @param emas				- toList,  stand alone email address.
	 * @param bccEMAs			- bccList
	 * @param viewUrl			- The public link view URL.
	 * @param downloadUrl		- The public link download URL.
	 * 
	 * @return
	 * 
	 * @throws Exception
     */
    public Map<String, Object> sendPublicLinkMail(ShareItem share, DefinableEntity sharedEntity, Collection<String> ems,
    		Collection<String> bccEMAs, String viewUrl, String downloadUrl) throws Exception;
    	 
    /**
	 * Sends a URL notification mail message to a collection of users
	 * and/or explicit email addresses.
	 * 
	 * @param url					- The URL embedded in the notification.
	 * @param urlNotificationType	- Type of notification to send.
	 * @param principalIds			- toList,  users and groups
	 * @param teamIds				- toList,  teams.
	 * @param emailAddresses		- toList,  stand alone email address.
	 * @param ccIds					- ccList,  users and groups
	 * @param bccIds				- bccList, users and groups
     * 
     * @return
     * 
     * @throws Exception
     */
    public Map<String, Object> sendUrlNotification(String url, UrlNotificationType urlNotificationType, Collection<Long> principalIds, Collection<Long> teamIds,
    		Collection<String> emailAddresses, Collection<Long> ccIds, Collection<Long> bccIds) throws Exception;
    	 
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
    public Map<String, Object> sendConfirmationMailToExternalUser(Long externalUserId, String entityPermalinkUrl) throws Exception;
    	 
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
    public Map<String, Object> sendShareInviteMailToExternalUser(ShareItem share, DefinableEntity sharedEntity, Long externalUserId) throws Exception;
    	 
    public void setMailConfigAndSchedules(MailConfig mailConfig, ScheduleInfo notifications, ScheduleInfo postings) throws AccessControlException;  
 	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map<Long, Set<Long>> functionMemberships) throws AccessControlException;
 	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map<Long, Set<Long>> functionMemberships, boolean doCheckAccess) throws AccessControlException;
 	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map<Long, Set<Long>> functionMemberships, boolean doCheckAccess, boolean justThisScope, String scope) throws AccessControlException;
 	public void setWorkAreaFunctionMemberships(WorkArea workArea, Map<Long, Set<Long>> functionMemberships, boolean doCheckAccess, boolean justThisScope, String scope, boolean skipFileContentIndexing) throws AccessControlException;
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) throws AccessControlException;
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit, boolean justThisScope, String scope) throws AccessControlException;
    public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit, boolean justThisScope, String scope, boolean skipFileContentIndexing) throws AccessControlException;
	public void updateWorkAreaFunctionMemberships(WorkArea wa, Long functionId, boolean add, Collection<Long> memberIds);
	public void updateWorkAreaFunctionMembership( WorkArea wa, Long functionId, boolean add, Long             memberId );
	public void resetWorkAreaFunctionMemberships( WorkArea wa, Long functionId,              Collection<Long> memberIds);
	public void resetWorkAreaFunctionMemberships( WorkArea wa, Long functionId,              Collection<Long> memberIds, boolean skipFileContentIndexing);
	public void resetWorkAreaFunctionMembership(  WorkArea wa, Long functionId,              Long             memberId );
    public void setWorkAreaOwner(WorkArea workArea, Long userId, boolean propagate) throws AccessControlException;
    public void updateDefaultDefinitions(Long topId, Boolean newDefinitionsOnly);
	public void updateDefaultDefinitions(AllModulesInjected bs, Long topId, Boolean newDefinitionsOnly, Collection ids);
	
	/**
	 * Get a list of <code>IndexNode</code>. Each IndexNode represents the state
	 * of the corresponding Lucene node in the context of current zone.
	 * @return
	 */
	public List<IndexNode> retrieveIndexNodesHA();
	
	/**
	 * Update the index node with the state information.
	 * 
	 * @param indexNodeId
	 * @param accessMode
	 * @param noDeferredUpdateLogRecords
	 */
	public void updateIndexNodeHA(String indexNodeId, String userModeAccess, Boolean enableDeferredUpdateLog, Boolean noDeferredUpdateLogRecords);

	/**
	 * Apply the deferred update log records to the index node and remove them.
	 * 
	 * @param indexNode
	 */
	public void applyDeferredUpdateLogRecordsHA(IndexNode indexNode);
	
	/**
	 * Discard and remove the deferred update log records without applying them.
	 *  
	 * @param indexNode
	 */
	public void discardDeferredUpdateLogRecordsHA(IndexNode indexNode);
	
	/**
	 * Obtain an application-scoped token on behalf of the specified user.
	 * The caller must have appropriate right to perform this operation.
	 * 
	 * @param applicationId ID of the application
	 * @param userId ID of the user on whose behalf the token is being requested. 
	 * @return
	 */
	public String obtainApplicationScopedToken(long applicationId, long userId)
	throws AccessControlException, NoApplicationByTheIdException, NoUserByTheIdException;

	/**
	 * Destroy/invalidate the application-scoped token. 
	 * This call requires the same access right as <code>getApplicationScopedToken</code> method.
	 * 
	 * @param token
	 */
	public void destroyApplicationScopedToken(String token);
	
	public ExtensionManager getExtensionManager();
	public void deleteExtension(String extensionId);
	public void addExtension(ExtensionInfo extension);
	public void modifyExtension(ExtensionInfo extension);
	
	public void optimizeIndex(String[] nodeNames) throws ManageIndexException, AccessControlException;
	
	public void addFunctionCondition(Condition functionCondition);
	
	public void modifyFunctionCondition(Condition functionCondition);
	
	public void deleteFunctionCondition(Long functionConditionId);
	
	public Condition getFunctionCondition(Long functionConditionId);
	
	public List<Condition> getFunctionConditions();
	
	public IndexOptimizationSchedule getIndexOptimizationSchedule();
	
	public void setIndexOptimizationSchedule(IndexOptimizationSchedule schedule);
	
	public String dumpRuntimeStatisticsAsString();
	
	public void dumpRuntimeStatisticsToLog();
	
	public void enableSimpleProfiler();
	
	public void disableSimpleProfiler();
	
	public void clearSimpleProfiler();
	
	public String dumpFileSyncStatsAsString();
	
	public void dumpFileSyncStatsToLog();
	
	public void enableFileSyncStats();
	
	public void disableFileSyncStats();
	
	public void setFileSynchAppSettings(Boolean enabled, Integer synchInterval, String autoUpdateUrl, Boolean deployEnabled, Boolean deployLocalApps, Boolean allowCachePwd, Integer maxFileSize, DesktopApplicationsLists daLists, Boolean allowCachedFiles, Boolean overrideCachedFilesSetting, Integer cachedFilesLifeTime );
	
	public void addOpenIDProvider(OpenIDProvider openIDProvider);
	
	public void modifyOpenIDProvider(OpenIDProvider openIDProvider);
	
	public void deleteOpenIDProvider(String openIDProviderId);
	
	public OpenIDProvider getOpenIDProvider(String openIDProviderId);
	
	public List<OpenIDProvider> getOpenIDProviders();
	
	public boolean isExternalUserEnabled();
	
	public void setExternalUserEnabled(boolean enabled);

	public OpenIDConfig getOpenIDConfig();
	
	public void setOpenIDConfig(OpenIDConfig openIDConfig);
	
	public void purgeLogTablesImmediate();
	
	public boolean writeAuditTrailLogFile(List<BasicAudit> entriesToBeDeleted);
	public boolean writeChangeLogLogFile(List<ChangeLog> entriesToBeDeleted);
	
    public ScheduleInfo getLogTablePurgeSchedule();
    
    public void setLogTablePurgeSchedule(ScheduleInfo info);
    
    public ScheduleInfo getTextConversionFilePurgeSchedule();
    public void setTextConversionFilePurgeSchedule(ScheduleInfo info);
    
    public ScheduleInfo getTempFileCleanupSchedule();
    public void setTempFileCleanupSchedule(ScheduleInfo info);
    
    public MobileAppsConfig getMobileAppsConfig();
    public MobileOpenInWhiteLists getMobileOpenInWhiteLists();
    
    public void setMobileAppsConfig( MobileAppsConfig mobileAppsConfig );
    
    public void setJitsConfig( boolean enabled, long maxWait );
    
    /**
     * Perform reindexing. This starts by first deleting from the search index the entire 
     * documents corresponding to the binder and everything in it (as opposed to replacing
     * one item at a time), hence destructive.
     * 
     * @param binderIds
     * @param statusTicket
     * @param nodeNames
     * @param errors
     * @param includeUsersAndGroups
     * @throws AccessControlException
     */
    public void reindexDestructive(Collection<Long> binderIds, StatusTicket statusTicket, String[] nodeNames, IndexErrors errors, boolean includeUsersAndGroups) throws AccessControlException;
    
    /**
     * Clear/reset reindexing states associated with the nodes (H/A if specified, non-H/A if unspecified).
     * 
     * @param nodeNames
     */
    public void clearReindexState(String[] nodeNames);
    
    /**
     * Return if "unsafe" reindexing is currently in progress. Used specifically by Desktop client.
     * 
     * @return
     */
    public boolean isUnsafeReindexingInProgress();
    
    /**
     * Load the index node associated with the system configured with non-H/A Lucene service. 
     * If the system is set up with H/A Lucene service, this method will return null.
     * 
     * @return
     */
    public IndexNode loadNonHAIndexNode();
    
    /**
     * Stores telemetry optin enabled flag in the zone config.
     * 
     * @param telemetryTier2Enabled
     */
    public void setTelemetryTier2Enabled(boolean telemetryTier2Enabled);
    
    /**
     * Stores telemetry settings flags in the zone config.
     * 
     * @param telemetryEnabled
     * @param telemetryTier2Enabled
     */
    public void setTelemetrySettings(boolean telemetryTier2Enabled, boolean telemetryEnabled);
    
    /**
     * Sets external user terms and conditions text in the zone config.
     */
    public void setExtUserTermsAndConditions(String extUserTermsAndConditions);
    
    /**
     * Enable/disable the use of terms and conditions for external user registration.
     */
    public void setExtUserTermsAndConditionsEnabled(boolean extUserTermsAndConditionsEnabled);
    
    /**
     * Stores the settings associated with the terms and conditions used for external user registration.
     */
    public void setExtUserTermsAndConditionsSettings(boolean extUserTermsAndConditionsEnabled, String extUserTermsAndConditions);
 }
