/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.context.request;

import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.SpringContextUtil;


/**
 *
 * @author Jong Kim
 */
public class RequestContextUtil {

	public static RequestContext setThreadContext(String zoneName, String userName) {
		return setThreadContext(zoneName, userName, new BaseSessionContext());
	}
	public static RequestContext setThreadContext(String zoneName, String userName, SessionContext ctx) {
		if(zoneName == null)
			throw new IllegalArgumentException("Zone name must be specified");
		if(userName == null)
			throw new IllegalArgumentException("User name must be specified");
		
		RequestContext rc = new RequestContext(zoneName, userName, ctx);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}
	public static RequestContext setThreadContext(String zoneName, Long userId) {
		return setThreadContext(zoneName, userId, new BaseSessionContext());
	}
	public static RequestContext setThreadContext(String zoneName, Long userId, SessionContext ctx) {
		if(zoneName == null)
			throw new IllegalArgumentException("Zone name must be specified");
		if(userId == null)
			throw new IllegalArgumentException("User ID must be specified");
		
		RequestContext rc = new RequestContext(zoneName, userId, ctx);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}
	public static RequestContext setThreadContext(Long zoneId, Long userId) {
		return setThreadContext(zoneId, userId, new BaseSessionContext());
	}
	public static RequestContext setThreadContext(Long zoneId, Long userId, SessionContext ctx) {
		if(zoneId == null)
			throw new IllegalArgumentException("Zone id must be specified");
		if(userId == null)
			throw new IllegalArgumentException("User id must be specified");
		
		RequestContext rc = new RequestContext(zoneId, userId, ctx);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}	
	public static RequestContext setThreadContext(Long zoneId, String userName) {
		return setThreadContext(zoneId, userName, new BaseSessionContext());
	}
	public static RequestContext setThreadContext(Long zoneId, String userName, SessionContext ctx) {
		if(zoneId == null)
			throw new IllegalArgumentException("Zone id must be specified");
		if(userName == null)
			throw new IllegalArgumentException("User name must be specified");
		
		RequestContext rc = new RequestContext(zoneId, userName, ctx);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}	
	
	public static RequestContext setThreadContext(User user) {
		if(user == null)
			throw new IllegalArgumentException("User must be specified");
		
		RequestContext rc = new RequestContext(user, new BaseSessionContext());
		RequestContextHolder.setRequestContext(rc);

		return rc;		
	}
	
	public static void clearThreadContext() {
		RequestContextHolder.clear();
	}
	
	public static void setThreadContext(RequestContext rc) {
		RequestContextHolder.setRequestContext(rc);
	}
	
	public static RequestContext getThreadContext() {
		return RequestContextHolder.getRequestContext();
	}
	
	public static User resolveToUser() {
		RequestContext rc = RequestContextHolder.getRequestContext();
		
		if(rc == null)
			throw new IllegalStateException("Request context must be created first");
		
		User user = rc.getUser();
		
		if(user == null) {
			if(rc.getUserId() != null) {
				if(rc.getZoneId() != null) {
					user = getProfileDao().loadUser(rc.getUserId(), rc.getZoneId());
				}
				else if(rc.getZoneName() != null) {
					user = getProfileDao().loadUser(rc.getUserId(), rc.getZoneName());					
				}
				else {
					throw new IllegalStateException("Either zone id or zone name must be specified first");
				}
			}
			else if(rc.getUserName() != null) {
				if(rc.getZoneId() != null) {
					user = getProfileDao().findUserByName(rc.getUserName(), rc.getZoneId());
				}
				else if(rc.getZoneName() != null) {
					user = getProfileDao().findUserByName(rc.getUserName(), rc.getZoneName());					
				}
				else {
					throw new IllegalStateException("Either zone id or zone name must be specified first");
				}				
			}
			else {
				throw new IllegalStateException("Either user id or user name must be specified first");				
			}
			rc.setUser(user);
		}
		
		return user;
	}
	
	private static ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}

}
