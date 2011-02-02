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
package org.kablink.teaming.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.kablink.teaming.InternalException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.util.Validator;


public class Utils {
	
	//Return the account name of the super user (e.g., "admin")
	public static String getAdminName() {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		return SZoneConfig.getString(zoneName, "property[@name='adminUser']", ObjectKeys.ADMIN);
	}

	public static String getGuestName() {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		return SZoneConfig.getString(zoneName, "property[@name='guestUser']", ObjectKeys.GUEST);
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
		if(Validator.isNotNull(title))
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
		Map onlySeeMap = (Map) RequestContextHolder.getRequestContext().getCacheEntry("onlySeeMap");
		if (onlySeeMap == null) onlySeeMap = new HashMap();
		if (onlySeeMap.containsKey(user.getId())) return (Boolean)onlySeeMap.get(user.getId());
		
		CoreDao coreDao = (CoreDao) SpringContextUtil.getBean("coreDao");
		AccessControlManager accessControlManager = (AccessControlManager)SpringContextUtil.getBean("accessControlManager");
		WorkArea zone = coreDao.loadZoneConfig(user.getZoneId());
		try {
			boolean canOnlySeeGroupMembers = accessControlManager.testOperation(user, zone, WorkAreaOperation.ONLY_SEE_GROUP_MEMBERS);
			boolean overrideCanOnlySeeGroupMembers = accessControlManager.testOperation(user, zone, WorkAreaOperation.OVERRIDE_ONLY_SEE_GROUP_MEMBERS);
			if (canOnlySeeGroupMembers && !overrideCanOnlySeeGroupMembers) {
				onlySeeMap.put(user.getId(), Boolean.TRUE);
			} else {
				onlySeeMap.put(user.getId(), Boolean.FALSE);
			}
			RequestContextHolder.getRequestContext().setCacheEntry("onlySeeMap", onlySeeMap);
		} catch(Exception e) {
			//If any error occurs, assume limited
			return true;
		}
		return (Boolean)onlySeeMap.get(user.getId());
	}

	public static boolean isWorkareaInProfilesTree(WorkArea workArea) {
		WorkArea parent = workArea.getParentWorkArea();
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
	
	public static void end(Log logger, long startTime, String methodName) {
		if(logger.isDebugEnabled()) {
			logger.debug((System.currentTimeMillis()-startTime) + " ms, " + methodName);
		}
	}

	public static void end(Log logger, long startTime, String methodName, String arg) {
		if(logger.isDebugEnabled()) {
			logger.debug((System.currentTimeMillis()-startTime) + " ms, " + methodName + " [" + arg + "]");
		}
	}

	public static void end(Log logger, long startTime, String methodName, String arg1, String arg2) {
		if(logger.isDebugEnabled()) {
			logger.debug((System.currentTimeMillis()-startTime) + " ms, " + methodName + " [" + arg1 + "," + arg2 + "]");
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
}
