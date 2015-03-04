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
package org.kablink.teaming.runas;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.zone.ZoneUtil;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * ?
 * 
 * @author ?
 */
public class RunasTemplate {
	public static Object runas(RunasCallback action, User user) {
		RequestContext runasRC = new RequestContext(user, null);
		return doRunas(action, runasRC);
	}
	public static Object runas(RunasCallback action, String zoneName, String userName) {
		RequestContext runasRC = new RequestContext(zoneName, userName, null);
		return doRunas(action, runasRC);
	}
	public static Object runas(RunasCallback action, String zoneName, Long userId) {
		RequestContext runasRC = new RequestContext(zoneName, userId, null);
		return doRunas(action, runasRC);
	}
	public static Object runas(RunasCallback action, Long zoneId, String userName) {
		RequestContext runasRC = new RequestContext(zoneId, userName, null);
		return doRunas(action, runasRC);
	}
	public static Object runas(RunasCallback action, Long zoneId, Long userId) {
		RequestContext runasRC = new RequestContext(zoneId, userId, null);
		return doRunas(action, runasRC);
	}
	
	/**
	 * Runs the action as the built-in admin user.
	 *
	 * Note:  This was rewritten to load the built-in-admin user by
	 *    internal ID instead of by name to facilitate renaming the
	 *    admin user.
	 * 
	 * @param action
	 * @param zoneName
	 * @param sessionCtx
	 * 
	 * @return
	 */
	public static Object runasAdmin(RunasCallback action, String zoneName, SessionContext sessionCtx) {
		Long zoneId = ZoneUtil.getZoneIdByZoneName(zoneName);
		User adminUser = MiscUtil.getProfileModule().getReservedUser(ObjectKeys.SUPER_USER_INTERNALID, zoneId);
		RequestContext runasRC = new RequestContext(zoneId, adminUser.getId(), sessionCtx);
		
//!		String adminUserName = SZoneConfig.getAdminUserName(zoneName);
//!		RequestContext runasRC = new RequestContext(zoneName, adminUserName, null);
		
		return doRunas(action, runasRC);
	}
	
	public static Object runasAdmin(RunasCallback action, String zoneName) {
		// Always use the initial form of the method.
		return runasAdmin(action, zoneName, null);
	}
	
	/**
	 * Runs the action as the Guest user.
	 * 
	 * Note:  This was rewritten to load the Guest user by internal ID
	 *    instead of by name to facilitate renaming the Guest user.
	 * 
	 * @param action
	 * @param zoneName
	 * 
	 * @return
	 */
	public static Object runasGuest(RunasCallback action, String zoneName) {
		Long zoneId = ZoneUtil.getZoneIdByZoneName(zoneName);
		User guestUser = MiscUtil.getProfileModule().getReservedUser(ObjectKeys.GUEST_USER_INTERNALID, zoneId);
		RequestContext runasRC = new RequestContext(zoneId, guestUser.getId(), null);
		
//!		String guestUserName = SZoneConfig.getGuestUserName(zoneName);
//!		RequestContext runasRC = new RequestContext(zoneName, guestUserName, null);
		
		return doRunas(action, runasRC);
	}
	
	protected static Object doRunas(RunasCallback action, RequestContext runasRC) {
		RequestContext origContext = RequestContextHolder.getRequestContext(); 
		
		if(origContext != null)
			runasRC.setParentUser(origContext.getUser());
		
       	RequestContextHolder.setRequestContext(runasRC.resolve());
       	
       	try {
       		return action.doAs();
       	}
       	finally {
       		if(origContext != null) {
       			// Restore original context
       			RequestContextHolder.setRequestContext(origContext);
       		}
       		else {
       			// Clear runas context
       			RequestContextHolder.clear();
       		}
       	}
	}
}
