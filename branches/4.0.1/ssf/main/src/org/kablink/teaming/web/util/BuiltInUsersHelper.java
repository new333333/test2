/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * This class contains a collection of methods for dealing with
 * built-in user accounts.
 *
 * --- *WARNING* --- *WARNING* --- *WARNING* --- *WARNING* ---
 * 
 * The implementation contained here is NOT designed for the built-in
 * user account names to be changed dynamically.  If/when the feature
 * is implemented that allows them to change dynamically, the server(s)
 * MUST be restarted to pick up the change for these methods to work.
 * 
 * See Bugzilla bug#899531, specifically comment#1 for a detailed
 * discussion of the issue.  The current implementation in this module
 * is based on option#1 from that comment.
 *  
 * --- *WARNING* --- *WARNING* --- *WARNING* --- *WARNING* ---
 * 
 * @author drfoster@novell.com
 */
public final class BuiltInUsersHelper {
	// The following contains a List<String> of the internal IDs of
	// those users we recognize as system users.
	private final static List<String> INTERNAL_SYSTEM_USER_IDS = new ArrayList<String>();
	static {
		INTERNAL_SYSTEM_USER_IDS.add(ObjectKeys.SUPER_USER_INTERNALID            );
		INTERNAL_SYSTEM_USER_IDS.add(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID);
		INTERNAL_SYSTEM_USER_IDS.add(ObjectKeys.FILE_SYNC_AGENT_INTERNALID       );
		INTERNAL_SYSTEM_USER_IDS.add(ObjectKeys.GUEST_USER_INTERNALID            );
		INTERNAL_SYSTEM_USER_IDS.add(ObjectKeys.JOB_PROCESSOR_INTERNALID         );
		INTERNAL_SYSTEM_USER_IDS.add(ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID );
	}

	// The first time they're required, this Map<String, String> will
	// be populated with a mapping of the various internal user IDs of
	// the system user accounts with their current names.
	private static Map<String, String> m_builtInUserNameCache = new HashMap<String, String>();
	
	/*
	 * Returns the name of the system user corresponding to the given
	 * internal ID.
	 */
	private static String getSystemUserName(String internalId, String defaultName, Long zoneId) {
		// Validate that the built-in user name cache has been
		// populated.  Note that this may be called BEFORE these users
		// have actually been created (e.g., for a new install.)
		// That's fine and the code is designed to handle that
		// situation.
		validateBuiltInUserNameCache();
		
		// If we haven't cached the built-in user names yes...
		String reply;
		if (m_builtInUserNameCache.isEmpty()) {
			// ...try loading the User object and pulling the name from
			// ...that.
			User systemUser = MiscUtil.getProfileModule().getReservedUser(internalId, zoneId);
			reply = ((null == systemUser) ? defaultName : systemUser.getName());
		}
		
		else {
			// ...otherwise, extract the name from the built-in user
			// ...name cache.
			reply = m_builtInUserNameCache.get(internalId);
			reply = ((null == reply) ? defaultName : reply);
		}
		
		// If we get here, reply refers to the system user name
		// requested or the supplied default if the requested name
		// could not be found.  Return it.
		return reply;
	}
	
	private static String getZoneDefaultAdminName(Long zoneId) {
		ZoneInfo zi = MiscUtil.getZoneModule().getZoneInfo(zoneId);
		return SZoneConfig.getZoneDefaultAdminUserName(zi.getZoneName());
	}
	
	private static String getZoneDefaultGuestUserName(Long zoneId) {
		ZoneInfo zi = MiscUtil.getZoneModule().getZoneInfo(zoneId);
		return SZoneConfig.getZoneDefaultGuestUserName(zi.getZoneName());
	}
	
	/**
	 * Returns the built-in admin user for the given zone.
	 * 
	 * @param zoneId
	 * 
	 * @return
	 */
	public static User getZoneSuperUser(Long zoneId) {
		return AccessUtils.getZoneSuperUser(zoneId);
	}
	
	/**
	 * Returns the built-in admin user for the current zone.
	 * 
	 * @return
	 */
	public static User getZoneSuperUser() {
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		return getZoneSuperUser(zoneId);
	}
	
	/**
	 * Get'er methods for the names of the various built-in system
	 * users.
	 * 
	 * Currently there are 6 system user accounts:  admin, guest, the
	 * email posting agent, the job processing agent, the
	 * synchronization agent, and the file sync agent.
	 * 
	 * @return
	 */
	public static String getAdminName(               Long zoneId) {return getSystemUserName(ObjectKeys.SUPER_USER_INTERNALID,             getZoneDefaultAdminName(    zoneId), zoneId);}
	public static String getEmailPostingAgentName(   Long zoneId) {return getSystemUserName(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, "_postingAgent",                     zoneId);}
	public static String getFileSyncAgentName(       Long zoneId) {return getSystemUserName(ObjectKeys.FILE_SYNC_AGENT_INTERNALID,        "_fileSyncAgent",                    zoneId);}
	public static String getGuestUserName(           Long zoneId) {return getSystemUserName(ObjectKeys.GUEST_USER_INTERNALID,             getZoneDefaultGuestUserName(zoneId), zoneId);}
	public static String getJobProcessingAgentName(  Long zoneId) {return getSystemUserName(ObjectKeys.JOB_PROCESSOR_INTERNALID,          "_jobProcessingAgent",               zoneId);}
	public static String getSynchronizationAgentName(Long zoneId) {return getSystemUserName(ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID,  "_synchronizationAgent",             zoneId);}

	public static String getAdminName()                {return getAdminName(               getZoneId());}
	public static String getEmailPostingAgentName()    {return getEmailPostingAgentName(   getZoneId());}
	public static String getFileSyncAgentName()        {return getFileSyncAgentName(       RequestContextHolder.getRequestContext().getZoneId());}
	public static String getGuestUserName()            {return getGuestUserName(           getZoneId());}
	public static String getJobProcessingAgentName()   {return getJobProcessingAgentName(  getZoneId());}
	public static String getSynchronizationAgentName() {return getSynchronizationAgentName(getZoneId());}
	
	public static Long getZoneId() {
		if(RequestContextHolder.getRequestContext() != null)
			return RequestContextHolder.getRequestContext().getZoneId();
		else
			return getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
	}

	/**
	 * This method will return true if the given name is the name of a
	 * system user account and false otherwise.
	 * 
	 * @param name
	 * 
	 * @return
	 */
	public static boolean isSystemUserAccount(String name) {
		// If we weren't given a name...
		if (!(MiscUtil.hasString(name))) {
			// ...it can't be a system user account.
			return false;
		}

		// Validate that the built-in user name cache has been
		// populated...
		validateBuiltInUserNameCache();

		// ...and scan the names in the cache.
		Set<String> biuKeys = m_builtInUserNameCache.keySet();
		for (String biuKey:  biuKeys) {
			// Is this the name in question?
			String biuName = m_builtInUserNameCache.get(biuKey);
			if (biuName.equalsIgnoreCase(name)) {
				// Yes!  It must be a system user account.  Return
				// true.
				return true;
			}
		}
		
		// If we get here the name is not a system user account.
		return false;
	}

	/**
	 * This method will return true if the given User is a system user
	 * account.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static boolean isSystemUserAccount(User user) {
		// If we don't have a User...
		if (null == user) {
			// ...it can't be a system user account.
			return false;
		}

		// Simply check if the user is a reserved user or not. The
		// reserved users are the built-in system users (i.e., 'admin',
		// 'guest', ...)
		return user.isReserved();
	}

	/*
	 * If it hasn't been populated yet, populates the built-in user
	 * name cache.
	 */
	private static void validateBuiltInUserNameCache() {
		// Have we cached the built in user names yet?
		if (m_builtInUserNameCache.isEmpty()) {
			// No!  Are we far enough along in the initialization
			// process to have a request and know our zone yet?
			RequestContext rc = RequestContextHolder.getRequestContext();
			Long zoneId = ((null == rc) ? null : rc.getZoneId());
			if (null != zoneId) {
				// Yes!  Load current instances of the various reserved
				// User's.
				Collection<User> reservedUsers = MiscUtil.getProfileModule().getReservedUsers(INTERNAL_SYSTEM_USER_IDS);
				if (MiscUtil.hasItems(reservedUsers)) {
					// Scan them...
					for (User reservedUser:  reservedUsers) {
						// ...and add the name's to the cache, indexed by their
						// ...internal ID.
						m_builtInUserNameCache.put(reservedUser.getInternalId(), reservedUser.getName());
					}
				}
			}
		}
	}
	
	private static ZoneModule getZoneModule() {
		return (ZoneModule)SpringContextUtil.getBean("zoneModule");
	}
}
