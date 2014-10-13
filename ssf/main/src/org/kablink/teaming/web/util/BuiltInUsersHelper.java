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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.util.SZoneConfig;

/**
 * This class contains a collection of methods for dealing with
 * built-in user accounts.
 * 
 * @author drfoster@novell.com
 */
public final class BuiltInUsersHelper {
	protected static Log m_logger = LogFactory.getLog(BuiltInUsersHelper.class);
	
	private final static Collection<String> RESERVED_USER_IDS = new ArrayList<String>();
	static {
		RESERVED_USER_IDS.add(ObjectKeys.SUPER_USER_INTERNALID            );
		RESERVED_USER_IDS.add(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID);
		RESERVED_USER_IDS.add(ObjectKeys.FILE_SYNC_AGENT_INTERNALID       );
		RESERVED_USER_IDS.add(ObjectKeys.GUEST_USER_INTERNALID            );
		RESERVED_USER_IDS.add(ObjectKeys.JOB_PROCESSOR_INTERNALID         );
		RESERVED_USER_IDS.add(ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID );
	}
	
	/*
	 * Returns the name of the system user corresponding to the given
	 * internal ID.
	 */
	private static String getSystemUserName(String internalId, String defaultName, Long zoneId) {
		User systemUser = MiscUtil.getProfileModule().getReservedUser(internalId, zoneId);
		return ((null == systemUser) ? defaultName : systemUser.getName());
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

	public static String getAdminName()                {return getAdminName(               RequestContextHolder.getRequestContext().getZoneId());}
	public static String getEmailPostingAgentName()    {return getEmailPostingAgentName(   RequestContextHolder.getRequestContext().getZoneId());}
	public static String getFileSyncAgentName()        {return getFileSyncAgentName(       RequestContextHolder.getRequestContext().getZoneId());}
	public static String getGuestUserName()            {return getGuestUserName(           RequestContextHolder.getRequestContext().getZoneId());}
	public static String getJobProcessingAgentName()   {return getJobProcessingAgentName(  RequestContextHolder.getRequestContext().getZoneId());}
	public static String getSynchronizationAgentName() {return getSynchronizationAgentName(RequestContextHolder.getRequestContext().getZoneId());}

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

		// Load current instances of the various reserved User's.
		Collection<User> reservedUsers = MiscUtil.getProfileModule().getReservedUsers(RESERVED_USER_IDS);
		if (MiscUtil.hasItems(reservedUsers)) {
			// Scan them.
			for (User reservedUser:  reservedUsers) {
				// Is this User in question?
				if (name.equalsIgnoreCase(reservedUser.getName())) {
					// Yes!  Return true.
					return true;
				}
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
}
