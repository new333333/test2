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
package org.kablink.teaming.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.Element;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.MailConfig;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.FileUtil;
import org.kablink.util.Validator;
import org.kablink.util.cache.HashMapCache;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class Utils {
	private static Log m_logger = LogFactory.getLog( Utils.class );
	
	// The following controls whether we can switch UI modes without
	// having to install a license.  It MUST be set false in the
	// shipping code.
	public final static boolean ENABLE_SSF_UI_OVERRIDE	= false;

	/*
	 * Enumeration used to communicate the type of license we're running
	 * with.
	 */
	@SuppressWarnings("unused")
	private enum LicenseOverride {
		FILR,
		FILR_AND_VIBE,
		VIBE,
		IPRINT,
		
		NO_OVERRIDE;
		
		/*
		 * Get'er methods.
		 */
		boolean isFilr()              {return    this.equals(FILR);         }
		boolean isFilrAndVibe()       {return    this.equals(FILR_AND_VIBE);}
		boolean isFilrEnabled()       {return (isFilr() || isFilrAndVibe());}
		boolean isLicenseOverridden() {return (!(this.equals(NO_OVERRIDE)));}
		boolean isVibe()              {return    this.equals(VIBE);         }
		boolean isIPrint()            {return    this.equals(IPRINT);       }
		boolean isVibeEnabled()       {return (isVibe() || isFilrAndVibe());}
		
		/*
		 * Checks for the current UI mode being overridden by an
		 * ssf*.properties setting.
		 */
		static LicenseOverride getLicenseOverride() {
			// Do we allow the license to be overridden?
			LicenseOverride reply = LicenseOverride.NO_OVERRIDE; 
			if (ENABLE_SSF_UI_OVERRIDE) {
				// Yes!  Check the setting.
				String uiType = SPropsUtil.getString("UI.type", "");
				if      (uiType.equalsIgnoreCase("Filr"))        reply = LicenseOverride.FILR;
				else if (uiType.equalsIgnoreCase("FilrAndVibe")) reply = LicenseOverride.FILR_AND_VIBE;
				else if (uiType.equalsIgnoreCase("Vibe"))        reply = LicenseOverride.VIBE;
				else if (uiType.equalsIgnoreCase("iPrint"))      reply = LicenseOverride.IPRINT;
			}
			
			// If we get here, reply refers to the ssf*.properties, if
			// enabled or an indication of no override otherwise.
			return reply;
		}
		
	}

	//Return the account name of the super user (i.e., 'admin')
	public static String getAdminName() {
		return SZoneConfig.getAdminUserName(RequestContextHolder.getRequestContext().getZoneName());
	}

	//Return the account name of the guest user (i.e., 'guest')
	public static String getGuestName() {
		return SZoneConfig.getGuestUserName(RequestContextHolder.getRequestContext().getZoneName());
	}

	public static String getZoneKey() {
		// If default zone
		//		zoneKey = zoneName
		// else
		//		zoneKey = zoneName + "_" + zoneId
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		String zoneKey;
		if(!zoneName.equals(SZoneConfig.getDefaultZoneName())) {
			Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			if(zoneId == null)
				throw new InternalException("Zone id is missing from the request context");
			zoneKey = zoneName + "_" + zoneId;
		}
		else {
			zoneKey = zoneName;
		}
		return zoneKey;
	}
	//handle cases where request context not set
	public static String getZoneKey(Workspace zone) {
		// If default zone
		//		zoneKey = zoneName
		// else
		//		zoneKey = zoneName + "_" + zoneId
		String zoneKey;
		if(!zone.getName().equals(SZoneConfig.getDefaultZoneName())) {
			zoneKey = zone.getName() + "_" + zone.getZoneId();
		}
		else {
			zoneKey = zone.getName();
		}
		return zoneKey;
	}
	//handle cases where request context not set
	public static String getZoneKey(ZoneInfo zone) {
		String zoneKey;
		if(!zone.getZoneName().equals(SZoneConfig.getDefaultZoneName())) {
			zoneKey = zone.getZoneName() + "_" + zone.getZoneId();
		}
		else {
			zoneKey = zone.getZoneName();
		}
		return zoneKey;
	}
	
	public static RuntimeException launderUncheckedException(Throwable uncheckedException) {
		if(uncheckedException instanceof Error)
			throw (Error) uncheckedException;
		else if(uncheckedException instanceof RuntimeException)
			return (RuntimeException) uncheckedException;
		else
			throw new IllegalArgumentException(uncheckedException);
	}
	
	public static Principal fixProxy(Principal p) {
		//See if this is a proxy object for the user. If so, get the real object so it can be redacted if necessary.
		if (EntityType.user.equals(p.getEntityType())) {
			ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
			User user = (User)profileDao.loadUserPrincipal(p.getId(), RequestContextHolder.getRequestContext().getZoneId(), false);
			return user;
		} else {
			return p;
		}
	}
	
	public static String getUserName(Principal p) {
		if (p instanceof User) {
			return p.getName();
		} else {
			//See if this is a proxy object for the user
			if (EntityType.user.equals(p.getEntityType())) {
				ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
				User user = (User)profileDao.loadUserPrincipal(p.getId(), RequestContextHolder.getRequestContext().getZoneId(), false);
				return user.getName();
			}
			return p.getName();
		}
	}
	public static String getUserName(User user) {
		return user.getName();
	}
	
	public static String getUserTitle(DefinableEntity entity) {
		if (entity instanceof Principal) {
			return getUserTitle((Principal) entity);
		} else {
			return entity.getTitle();
		}
	}
	public static String getUserTitle(Principal p) {
		if (EntityType.user.equals(p.getEntityType())) {
			if (!(p instanceof User)) {
				try {
					//this will remove the proxy and return a real user or group
					//currently looks like this code is expecting a User
					//get user even if deleted.
					ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
					User user = (User)profileDao.loadUserPrincipal(p.getId(), RequestContextHolder.getRequestContext().getZoneId(), false);
					return getUserTitle(user, false);
				} catch (Exception e) {}
			} 
			return getUserTitle((User) p);
		} else {
			return p.getTitle();
		}
	}
	public static String getUserTitle(User user) {
		return getUserTitle(user, true);
	}
	public static String getUserTitle(User user, boolean loadPrincipal) {
		if (loadPrincipal && canUserOnlySeeCommonGroupMembers()) {
			try {
				//this will remove the proxy and return a real user or group
				//currently looks like this code is expecting a User
				//get user even if deleted.
				ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
				user = (User)profileDao.loadUserPrincipal(user.getId(), RequestContextHolder.getRequestContext().getZoneId(), false);
			} catch (Exception e) {}
		}
		List values = new ArrayList();
		String result = "";
		
		String fn = user.getFirstName();
		if(Validator.isNotNull(fn)){
			values.add(fn.trim());
		} else {
			values.add("");
		}

		String mn = user.getMiddleName();
		if(Validator.isNotNull(mn)){
			values.add(mn.trim());
		} else {
			values.add("");
		}

		String ln = user.getLastName();
		if(Validator.isNotNull(ln)){
			values.add(ln.trim());
		} else {
			values.add("");
		}

		String title = user.getTitle();
		if(Validator.isNotNull(title) && (Validator.isNotNull(fn) || Validator.isNotNull(mn) || Validator.isNotNull(ln)))
		{
			result = NLT.get("user.title", values.toArray(), title);
			result = result.trim().replaceAll("  ", " ");
		}
		if (user.isDisabled()) {
			List ta = new ArrayList();
			ta.add(result.trim());
			result =  NLT.get("user.title.disabled", ta.toArray(), result.trim());
		}
		return result.trim();
	}
	
	public static boolean isSunVM() {
		String vmVendor = System.getProperty("java.vm.vendor");
		if(vmVendor != null && vmVendor.toLowerCase().contains("sun"))
			return true;
		else
			return false;
	}
	
  	public static boolean canUserOnlySeeCommonGroupMembers() {
	  	User user = RequestContextHolder.getRequestContext().getUser();
	  	return canUserOnlySeeCommonGroupMembers(user);
  	}

	public static boolean canUserOnlySeeCommonGroupMembers(User user) {
		if (user == null) return false;
		HashMapCache<Long, Boolean> onlySeeCache = null;
		HttpSession session = ZoneContextHolder.getHttpSession();
		int canOnlySeeTimeout = SPropsUtil.getInt("cache.SESSION_CAN_ONLY_SEE_CACHE_TIMEOUT", ObjectKeys.SESSION_CAN_ONLY_SEE_CACHE_TIMEOUT);
		if (canOnlySeeTimeout > 0) {
			if (session != null) {
				onlySeeCache = (HashMapCache<Long, Boolean>)session.getAttribute(ObjectKeys.SESSION_CAN_ONLY_SEE_CACHE);
				if (onlySeeCache == null) {
					onlySeeCache = new HashMapCache<Long, Boolean>(canOnlySeeTimeout);
					session.setAttribute(ObjectKeys.SESSION_CAN_ONLY_SEE_CACHE, onlySeeCache);
				}
			} else {
				//This must be a REST call. So use the request context to hold the cache
				RequestContext context = RequestContextHolder.getRequestContext();
				if (context != null) {
					onlySeeCache = (HashMapCache<Long, Boolean>)context.getCacheEntry(ObjectKeys.SESSION_CAN_ONLY_SEE_CACHE);
					if (onlySeeCache == null) {
						onlySeeCache = new HashMapCache<Long, Boolean>(canOnlySeeTimeout);
						context.setCacheEntry(ObjectKeys.SESSION_CAN_ONLY_SEE_CACHE, onlySeeCache);
					}
				}
			}
		}
		if (onlySeeCache != null) {
			Boolean value = onlySeeCache.get(user.getId());
			if (value != null) {
				//The value existed in the cache, so return it
				return value;
			}
		} else {
			//This shouldn't ever happen, but to prevent an NPE, ...
			onlySeeCache = new HashMapCache<Long, Boolean>(canOnlySeeTimeout);
		}
		
		CoreDao coreDao = (CoreDao) SpringContextUtil.getBean("coreDao");
		AccessControlManager accessControlManager = (AccessControlManager)SpringContextUtil.getBean("accessControlManager");
		WorkArea zone = coreDao.loadZoneConfig(user.getZoneId());
		Boolean reply;
		try {
			// DRF (20141211):  Reworked this check to only test
			// OVERRIDE_ONLY_SEE_GROUP_MEMBERS if it has to (i.e.,
			// if canOnlySeeGroupMembers is true) and not check it
			// all the time.
			boolean canOnlySeeGroupMembers = accessControlManager.testOperation(user, zone, WorkAreaOperation.ONLY_SEE_GROUP_MEMBERS);
			if (canOnlySeeGroupMembers && !accessControlManager.testOperation(user, zone, WorkAreaOperation.OVERRIDE_ONLY_SEE_GROUP_MEMBERS)) {
				reply = Boolean.TRUE;
				onlySeeCache.put(user.getId(), reply);
			} else {
				reply = Boolean.FALSE;
				onlySeeCache.put(user.getId(), reply);
			}
		} catch(Exception e) {
			//If any error occurs, assume limited
			return true;
		}
		return reply;
	}

	public static boolean isWorkareaInProfilesTree(WorkArea workArea) {
		WorkArea parent = workArea.getParentWorkArea();
		if (workArea instanceof FolderEntry) {
			parent = ((FolderEntry)workArea).getParentFolder();
		}
		while (parent != null) {
			if (parent.getWorkAreaType().equals(EntityType.profiles.name())) return true;
			parent = parent.getParentWorkArea();
		}
		return false;
	}
	
	public static Long getAllUsersGroupId() {
		ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
		try {
			Long allUsersGroupId = profileDao.getReservedGroupId(ObjectKeys.ALL_USERS_GROUP_INTERNALID, RequestContextHolder.getRequestContext().getZoneId());
			return allUsersGroupId;
		} catch(Exception e) {
			//Can't find the All Users group, return null
			return null;
		}
	}
	
	public static Long getAllExtUsersGroupId() {
		ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
		try {
			Long allExtUsersGroupId = profileDao.getReservedGroupId(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID, RequestContextHolder.getRequestContext().getZoneId());
			return allExtUsersGroupId;
		} catch(Exception e) {
			//Can't find the All External Users group, return null
			return null;
		}
	}
	
	/**
	 * 
	 */
	public static Long getGuestId( ProfileModule profileModule )
	{
		Long id;
		User guestUser;
		
		guestUser = profileModule.getGuestUser();
		id = guestUser.getId();
		
		return id;
	}
	
	/**
	 * 
	 */
	public static Long getGuestId( AllModulesInjected ami )
	{
		return getGuestId( ami.getProfileModule() );
	}
	
	public static void end(Log logger, long startTimeInNanoseconds, String methodName) {
		if(logger.isDebugEnabled()) {
			logger.debug((System.nanoTime()-startTimeInNanoseconds)/1000000.0 + " ms, " + methodName);
		}
	}

	public static void end(Log logger, long startTimeInNanoseconds, String methodName, String arg) {
		if(logger.isDebugEnabled()) {
			logger.debug((System.nanoTime()-startTimeInNanoseconds)/1000000.0 + " ms, " + methodName + " [" + arg + "]");
		}
	}

	public static void end(Log logger, long startTimeInNanoseconds, String methodName, String arg1, String arg2) {
		if(logger.isDebugEnabled()) {
			logger.debug((System.nanoTime()-startTimeInNanoseconds)/1000000.0 + " ms, " + methodName + " [" + arg1 + "," + arg2 + "]");
		}
	}

	public static UserPrincipal redactUserPrincipalIfNecessary(UserPrincipal p) {
		if (canUserOnlySeeCommonGroupMembers()) {
			ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
			return profileDao.loadUserPrincipal(p.getId(), RequestContextHolder.getRequestContext().getZoneId(), false);
		}
		else {
			return p;
		}
	}
	
	public static UserPrincipal redactUserPrincipalIfNecessary(Long userPrincipalId) {
		if (canUserOnlySeeCommonGroupMembers()) {
			ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
			return profileDao.loadUserPrincipal(userPrincipalId, RequestContextHolder.getRequestContext().getZoneId(), false);
		}
		else {
			return null;
		}
	}
	
	//Routine to check send mail quota on attachments
	public static boolean testSendMailAttachmentSize(FileAttachment fAtt) {
		//Get the quota (if any)
		User user = RequestContextHolder.getRequestContext().getUser();
		CoreDao coreDao = (CoreDao) SpringContextUtil.getBean("coreDao");
		ZoneConfig zoneConfig = coreDao.loadZoneConfig(user.getZoneId());
		MailConfig mailConfig = zoneConfig.getMailConfig();

		Long maxSize = mailConfig.getOutgoingAttachmentSizeLimit();
		if (maxSize == null) return true;		//If no value has been set, then any size is OK
		
		if (maxSize < 0 || maxSize >= fAtt.getFileItem().getLength()) {
			return true;
		} else {
			return false;
		}
	}
	
	//Routine to check send mail quota on a set of attachments
	//  The sum of the attachments must not exceed the limit
	public static boolean testSendMailAttachmentsSize(Collection<FileAttachment> fileAttachments) {
		//Get the quota (if any)
		User user = RequestContextHolder.getRequestContext().getUser();
		CoreDao coreDao = (CoreDao) SpringContextUtil.getBean("coreDao");
		ZoneConfig zoneConfig = coreDao.loadZoneConfig(user.getZoneId());
		MailConfig mailConfig = zoneConfig.getMailConfig();

		Long sum = 0L;
		for (FileAttachment fAtt : fileAttachments) {
			sum += fAtt.getFileItem().getLength();
		}
		
		Long maxSize = mailConfig.getOutgoingAttachmentSumLimit();
		if (maxSize == null) return true;		//If no value has been set, then any size is OK

		if (maxSize < 0 || maxSize >= sum) {
			return true;
		} else {
			return false;
		}
	}
	
	//Routine to get the maximum number of allowable search results (maxHits)
	public static int getSearchDefaultMaxHits() {
		return SPropsUtil.getInt("search.maxNumberOfRequestedResults", ObjectKeys.SEARCH_MAX_HITS_LIMIT);
	}


	/**
	 * Do a reindex on all the principals in the given list
	 */
	public static void reIndexPrincipals( ProfileModule profileModule, Map<Long,Principal> principalsToIndex )
	{
		List<Principal> users;
		List<Principal> groups;
		List<Long> gIds;
		Set<Principal> groupUsers;
		Set<Long> groupUserIds;
		ProfileDao profileDao;

		if ( principalsToIndex != null && profileModule != null )
		{
			m_logger.debug( "Utils.reIndexPrincipals() - principalsToIndex.size(): " + String.valueOf( principalsToIndex.size() ) );
			SimpleProfiler.start( "Utils.reIndexPrincipals() - get list of users" );
			try
			{
				profileDao = (ProfileDao) SpringContextUtil.getBean( "profileDao" );
	
				users = new ArrayList<Principal>();
				groups = new ArrayList<Principal>();
				gIds = new ArrayList<Long>();
				for (Map.Entry<Long,Principal> me : principalsToIndex.entrySet())
				{
					Principal nextPrincipal;
					
					nextPrincipal = me.getValue();
					if ( (nextPrincipal instanceof User) || (nextPrincipal instanceof UserPrincipal) )
						users.add( nextPrincipal );
					
					if ( (nextPrincipal instanceof Group) || (nextPrincipal instanceof GroupPrincipal) )
					{
						groups.add( nextPrincipal );
						gIds.add( nextPrincipal.getId() );
					}
				}
			}
			finally
			{
				SimpleProfiler.stop( "Utils.reIndexPrincipals() - get list of users" );
			}
	
			// Re-index the list of users and all binders "owned" by them
			// reindex the profile entry for each user
			{
				String tmp;
				
				groupUsers = new HashSet<Principal>();
				groupUserIds = new HashSet<Long>();

				SimpleProfiler.start( "Utils.reIndexPrincipals() - call profileDao.explodeGroups()" );
				try
				{
					groupUserIds.addAll( profileDao.explodeGroups( gIds, RequestContextHolder.getRequestContext().getZoneId() ) );
				}
				finally
				{
					SimpleProfiler.stop( "Utils.reIndexPrincipals() - call profileDao.explodeGroups()" );
				}
				
				SimpleProfiler.start( "Utils.reIndexPrincipals() - call getPrincipals()" );
				try
				{
					groupUsers.addAll( profileModule.getPrincipals( groupUserIds ) );
					groupUsers.addAll( users );
				}
				finally
				{
					SimpleProfiler.stop( "Utils.reIndexPrincipals() - call getPrincipals()" );
				}
				
				tmp = "Utils.reIndexPrincipals() - call indexEntries() size: " + String.valueOf( groupUsers.size() );
				SimpleProfiler.start( tmp );
				try
				{
					profileModule.indexEntries( groupUsers );
				}
				finally
				{
					SimpleProfiler.stop( tmp );
				}
			}
			
			// set up a background job that will reindex all of the binders owned by all of these users.				
			//Re-index all "personal" binders owned by this user (i.e., binders under the profiles binder)
			String tmp = "Utils.reIndexPrincipals() - call reindexPersonalUserOwnedBinders() size: " + String.valueOf( groupUsers.size() );
			SimpleProfiler.start( tmp );
			try
			{
				profileModule.reindexPersonalUserOwnedBinders( groupUsers );
			}
			finally
			{
				SimpleProfiler.stop( tmp );
			}
		}
	}

	
	/**
	 * Update the disk quotas and file size limits for the users/groups that have
	 * been added/removed from the given group.
	 * 
	 * @param profileModule
	 * @param group
	 * @param usersAddedToGroup
	 * @param usersRemovedFromGroup
	 * @param groupsAddedToGroup
	 * @param groupsRemovedFromGroup
	 */
	public static void updateDiskQuotasAndFileSizeLimits(
			ProfileModule profileModule,
			Group group,
			ArrayList<Long> usersAddedToGroup,
			ArrayList<Long> usersRemovedFromGroup,
			ArrayList<Long> groupsAddedToGroup,
			ArrayList<Long> groupsRemovedFromGroup )
	{
		if ( group == null )
			return;
		
		m_logger.debug( "Utils.updateDiskQuotasAndFileSizeLimits(), group: " + group.getTitle() );
		
		// Update the disk quotas for users that were added to the group.
		SimpleProfiler.start( "Utils.updateDiskQuotasAndFileSizeLimits() - setUserGroupDiskQuotas()." );
		try
		{
			profileModule.setUserGroupDiskQuotas( usersAddedToGroup, group );
		}
		finally
		{
			SimpleProfiler.stop( "Utils.updateDiskQuotasAndFileSizeLimits() - setUserGroupDiskQuotas()." );
		}
		
		// Update the disk quotas for users that were removed from the group.
		SimpleProfiler.start( "Utils.updateDiskQuotasAndFileSizeLimits() - deleteUserGroupDiskQuotas()." );
		try
		{
			profileModule.deleteUserGroupDiskQuotas( usersRemovedFromGroup, group );
		}
		finally
		{
			SimpleProfiler.stop( "Utils.updateDiskQuotasAndFileSizeLimits() - deleteUserGroupDiskQuotas()." );
		}
		
		// Update the file size limits for users that were added to the group.
		SimpleProfiler.start( "Utils.updateDiskQuotasAndFileSizeLimits() - setUserGroupFileSizeLimits()." );
		try
		{
			profileModule.setUserGroupFileSizeLimits( usersAddedToGroup, group );
		}
		finally
		{
			SimpleProfiler.stop( "Utils.updateDiskQuotasAndFileSizeLimits() - setUserGroupFileSizeLimits()." );
		}
		
		// Update the file size limits for users that were removed from the group.
		SimpleProfiler.start( "Utils.updateDiskQuotasAndFileSizeLimits() - deleteUserGroupFileSizeLimits()." );
		try
		{
			profileModule.deleteUserGroupFileSizeLimits( usersRemovedFromGroup, group );
		}
		finally
		{
			SimpleProfiler.stop( "Utils.updateDiskQuotasAndFileSizeLimits() - deleteUserGroupFileSizeLimits()." );
		}
	}
	
	//Routines that support Filr
	
	/**
	 * Check if this is a Filr only license
	 * 
	 * @return
	 */
	public static boolean checkIfFilr() {
		// If we have an ssf*.properties license override...
		LicenseOverride lo = LicenseOverride.getLicenseOverride();
		if (lo.isLicenseOverridden()) {
			// ...that's all we look at.
			return lo.isFilr();
		}
		
		// No ssf*.properties override!  Check the license.
		if (ObjectKeys.LICENSE_TYPE_FILR.equals(LicenseChecker.getLicenseType())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if this is a Filr and Vibe license
	 *
	 * @return
	 */
	public static boolean checkIfFilrAndVibe() {
		// If we have an ssf*.properties license override...
		LicenseOverride lo = LicenseOverride.getLicenseOverride();
		if (lo.isLicenseOverridden()) {
			// ...that's all we look at.
			return lo.isFilrAndVibe();
		}
		
		// No ssf*.properties override!  Check the license.
		if (LicenseChecker.isAuthorizedByLicense(ObjectKeys.LICENSE_OPTION_FILR) &&
				LicenseChecker.isAuthorizedByLicense(ObjectKeys.LICENSE_OPTION_VIBE)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if this is a Vibe license
	 * Note: This is vibe only, It does not include Kablink. Use checkIfKablink() if you need to.
	 * 
	 * @return
	 */
	public static boolean checkIfVibe() {
		// If we have an ssf*.properties license override...
		LicenseOverride lo = LicenseOverride.getLicenseOverride();
		if (lo.isLicenseOverridden()) {
			// ...that's all we look at.
			return lo.isVibe();
		}
		
		// No ssf*.properties override!  Check the license. 
		if (!checkIfFilrAndVibe() && ObjectKeys.LICENSE_TYPE_VIBE.equals(LicenseChecker.getLicenseType())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if this is a iPrint only license
	 * 
	 * @return
	 */
	public static boolean checkIfIPrint() {
		// If we have an ssf*.properties license override...
		LicenseOverride lo = LicenseOverride.getLicenseOverride();
		if (lo.isLicenseOverridden()) {
			// ...that's all we look at.
			return lo.isIPrint();
		}
		
		// No ssf*.properties override!  Check the license.
		if (ObjectKeys.LICENSE_TYPE_IPRINT.equals(LicenseChecker.getLicenseType())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if this is a Kablink (i.e., no license)
	 * 
	 * @return
	 */
	public static boolean checkIfKablink() {
		//See if no product licenses exist (vibe or filr)
		if (ObjectKeys.LICENSE_TYPE_KABLINK.equals(LicenseChecker.getLicenseType())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if this is a valid definition for this license
	 * 
	 */
	public static boolean checkIfValidDefinition(Definition def) {
		if (Utils.checkIfFilr()) {
			return checkIfFilrDefinition(def);
		} else {
			return checkIfVibeDefinition(def);
		}
	}

	/**
	 * Check if this is a Vibe definition
	 * Vibe definitions include everything except the two Filr folder and file definitions
	 * (This works since Filr doesn't allow new definitions to be made.)
	 * 
	 */
	public static boolean checkIfVibeDefinition(Definition def) {
		if (ObjectKeys.DEFAULT_MIRRORED_FILR_FILE_FOLDER_DEF.equals(def.getInternalId()) ||
				ObjectKeys.DEFAULT_MIRRORED_FILR_FILE_ENTRY_DEF.equals(def.getInternalId())) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Check if this is Filr definition
	 * 
	 */
	public static boolean checkIfFilrDefinition(Definition def) {
		Document doc = def.getDefinition();
		int defType = def.getType();
		Element familyProperty = (Element) doc.getRootElement().selectSingleNode("//properties/property[@name='family']");
		if (familyProperty != null) {
			String family = familyProperty.attributeValue("value", "");
			return checkIfFilrFamily(defType, family);
		}
		return false;
	}

	public static boolean checkIfFilrFamily(String type, String family) {
		if (type.equals("familySelectboxFolder")) {
			return Utils.checkIfFilrFamily(Definition.FOLDER_VIEW, family);
		} else if (type.equals("familySelectboxWorkspace")) {
			return Utils.checkIfFilrFamily(Definition.WORKSPACE_VIEW, family);
		} else if (type.equals("familySelectboxEntry")) {
			return Utils.checkIfFilrFamily(Definition.FOLDER_ENTRY, family);
		} else if (type.equals("familySelectboxUserWorkspace")) {
			return Utils.checkIfFilrFamily(Definition.USER_WORKSPACE_VIEW, family);
		} else if (type.equals("familySelectboxUserProfile")) {
			return Utils.checkIfFilrFamily(Definition.PROFILE_ENTRY_VIEW, family);
		} else if (type.equals("familySelectboxExternalUserWorkspace")) {
			return Utils.checkIfFilrFamily(Definition.EXTERNAL_USER_WORKSPACE_VIEW, family);
		}
		return false;
	}

	public static boolean checkIfFilrFamily(int defType, String family) {
		if (defType == Definition.FOLDER_VIEW && family.equals(Definition.FAMILY_FILE)) {
			return true;
		} else if (defType == Definition.WORKSPACE_VIEW && family.equals(Definition.FAMILY_WORKSPACE)) {
			return true;
		} else if (defType == Definition.FOLDER_ENTRY) {
			if (family.equals(Definition.FAMILY_FILE) || family.equals(Definition.FAMILY_FILE_COMMENT)) {
				return true;
			}
		} else if (defType == Definition.USER_WORKSPACE_VIEW && family.equals(Definition.FAMILY_USER_WORKSPACE)) {
			return true;
		} else if (defType == Definition.PROFILE_ENTRY_VIEW && family.equals(Definition.FAMILY_USER_PROFILE)) {
			return true;
		} else if (defType == Definition.EXTERNAL_USER_WORKSPACE_VIEW && 
				family.equals(Definition.FAMILY_EXTERNAL_USER_WORKSPACE)) {
			return true;
		}
		return false;
	}
	
	//Validate a definition to see if it is allowed to be used
	public static boolean validateDefinition(Definition def, Binder binder) {
		List<Definition> binderDefs = new ArrayList<Definition>();
		if (binder != null) binderDefs = binder.getDefinitions();
		
		//Check if def allowed 
		if (binderDefs.contains(def) || checkIfValidDefinition(def)) {
			//This template is allowed
			return true;
		} else {
			return false;
		}
	}
		
   	//Validate which definitions are allowed to be used
	public static List<Definition> validateDefinitions(List<Definition> defs, Binder binder) {
		//Filter out any definitions that are not allowed
		List<Definition> filteredList = new ArrayList<Definition>();
		for (Definition def : defs) {
			if (validateDefinition(def, binder)) {
				//This template is allowed
				filteredList.add(def);
			}
		}
		return filteredList;
	}
		
   	//Validate which definitions by family type are allowed to be used
	public static List<Definition> validateDefinitions(List<Definition> defs, Binder binder, Integer definitionType) {
		List<Definition> binderDefs = new ArrayList<Definition>();
		if (binder != null) binderDefs = binder.getDefinitions();
		
		//Filter out any definitions that are not allowed
		List<Definition> filteredList = new ArrayList<Definition>();
		for (Definition def : defs) {
			if (binderDefs.contains(def)) {
				//This template is allowed
				filteredList.add(def);
			} else {
			  	@SuppressWarnings("unused")
				Document doc = def.getDefinition();
				if (def.getType() == definitionType && checkIfValidDefinition(def)) {
					//This template is allowed
					filteredList.add(def);
				}
			}
		}
		return filteredList;
	}
		
   	//Validate that which templates are allowed to be used
	public static List<TemplateBinder> validateTemplateBinders(List<TemplateBinder> binders) {
		return validateTemplateBinders(binders, Boolean.FALSE);
	}
	public static List<TemplateBinder> validateTemplateBinders(List<TemplateBinder> binders, 
			boolean includeHiddenTemplates) {
		List<TemplateBinder> filteredList = new ArrayList<TemplateBinder>();
		//We must first filter out any hidden templates
		for (TemplateBinder t : binders) {
			if (includeHiddenTemplates || !t.isTemplateHidden()) filteredList.add(t);
		}
		//Filter out any templates that are not allowed
		List<TemplateBinder> finalList = new ArrayList<TemplateBinder>();
		for (TemplateBinder binder : filteredList) {
			if (validateTemplateBinder(binder) != null) {
				//This template is allowed
				finalList.add(binder);
			}
		}
		return finalList;
	}
	
   	//Validate that a template is allowed to be used
	public static TemplateBinder validateTemplateBinder(TemplateBinder binder) {
		if (binder == null) return null;
		List<Definition> defs = binder.getDefinitions();
		
		//Make sure the template is allowed
		//First, check the definitions used by the template
		if (defs.isEmpty() || binder.isDefinitionsInherited()) {
			Definition def = binder.getEntryDef();
			if (def != null) {
				if (!checkIfValidDefinition(def)) return null;
			}
			
		} else {
			for (Definition def:defs) {
				if (!checkIfValidDefinition(def)) return null;
			}
		}
		return binder;
	}
	
	/**
	 * Routine to translate an old icon name into a new one
	 * (.gif --> .png).
	 * 
	 * @param iconName
	 * 
	 * @return
	 */
	public static String getIconNameTranslated(String iconName) {
		if(iconName == null)
			return null;
			
		// Does the icon name have an extension?
		int i = iconName.lastIndexOf(".");
		if (i >= 0) {
			// Yes!  Is that extension '.gif'?
			String root = iconName.substring(0, i);
			String ext  = iconName.substring(i, iconName.length());
			if (ext.equalsIgnoreCase(".gif")) {
				// Yes!  Change it to '.png'.
				iconName = (root + ".png");
			}
		}
		return iconName;
	}
	
	/**
	 * Routine to translate an old icon name and icon size into a new
	 * one icon name.
	 *  
	 * @param iconName
	 * @param size
	 * 
	 * @return
	 */
	public static String getIconNameTranslated(String iconName, IconSize size) {
		String name = getIconNameTranslated(iconName);
		String sizePart;
		switch (size) {
		default:
		case SMALL:
			// Small icons use no name extension.
			return name;
			
		case MEDIUM:  sizePart = "_36"; break;
		case LARGE:   sizePart = "_48"; break;
		}
		
		int i = name.lastIndexOf(".");
		if (i >= 0) {
			String root = name.substring(0, i);
			String ext  = name.substring(i, name.length());
			return (root + sizePart + ext);
		} else if (MiscUtil.hasString(name)) {
			return (name + sizePart);
		} else {
			return name;
		}
	}
	
	public static boolean outputImageAsDataUrl(DefinableEntity entity, String fileName, String mimeType, ServletOutputStream out) {
		boolean result = true;
		FileAttachment fa = (FileAttachment) entity.getFileAttachment(fileName);
		if (fa == null) {
			result = false;
		} else {
			InputStream in = null;
			OutputStream out64 = null;
			try {
				out.print("data:" + mimeType + ";base64,");
				FileModule fileModule = (FileModule) SpringContextUtil.getBean("fileModule");
				in = fileModule.readFile(entity.getParentBinder(), entity, fa);
				out64 = new Base64OutputStream(out);
				FileUtil.copy(in, out64);
			} catch (IOException e) {
				m_logger.info("Utils.outputImageAsDataUrl error: " + e.getMessage());
				result = false;
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						m_logger.info("Utils.outputImageAsDataUrl error: " + e.getMessage());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns true if the given text matches the captcha
	 */
	public static boolean isCaptchaValid(
		HttpServletRequest httpServletRequest,
		String text )
	{
		String kaptchaExpected;
		
		if ( text == null || text.length() == 0 || httpServletRequest == null )
			return false;
		
		// Get the text used to create the kaptcha image.  It is stored in the http session.
		kaptchaExpected = (String) httpServletRequest.getSession().getAttribute( com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY );
		
		if ( kaptchaExpected == null || !kaptchaExpected.equalsIgnoreCase( text  ) )
		{
			// The text entered by the user did not match the text used to create the kaptcha image.
			return false;
		}
		
		return true;
	}
	
}
