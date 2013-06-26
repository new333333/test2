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
package org.kablink.teaming.context.request;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.domain.User;
import org.kablink.teaming.web.util.WebHelper;


/**
 *
 * @author Jong Kim
 */
public class RequestContextUtil {

	public static RequestContext setThreadContext(String zoneName, Long zoneId, String userName, Long userId) {
		return setThreadContext(zoneName, zoneId, userName, userId, new BaseSessionContext());
	}
	public static RequestContext setThreadContext(String zoneName, Long zoneId, String userName, Long userId, SessionContext ctx) {
		if(zoneName == null)
			throw new IllegalArgumentException("Zone name must be specified");
		if(zoneId == null)
			throw new IllegalArgumentException("Zone id must be specified");
		if(userName == null)
			throw new IllegalArgumentException("User name must be specified");
		if(userId == null)
			throw new IllegalArgumentException("User id must be specified");
		
		RequestContext rc = new RequestContext(zoneName, zoneId, userName, userId, ctx);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}
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
		return setThreadContext(user, new BaseSessionContext());
	}
	public static RequestContext setThreadContext(User user, SessionContext ctx) {
		if(user == null)
			throw new IllegalArgumentException("User must be specified");
		
		RequestContext rc = new RequestContext(user, ctx);
		RequestContextHolder.setRequestContext(rc);

		return rc;		
	}
	
	public static RequestContext setThreadContext(PortletRequest request) {
		return setThreadContext(request, new BaseSessionContext());
	}
	public static RequestContext setThreadContext(PortletRequest request, SessionContext ctx) {
		String zoneName = WebHelper.getRequiredZoneName(request);
		Long zoneId = WebHelper.getRequiredZoneId(request);
		String userName = WebHelper.getRequiredUserName(request);
		Long userId = WebHelper.getRequiredUserId(request);

		return setThreadContext(zoneName, zoneId, userName, userId, ctx);
	}
	
	public static RequestContext setThreadContext(HttpServletRequest request) {
		return setThreadContext(request, new BaseSessionContext());
	}
	public static RequestContext setThreadContext(HttpServletRequest request, SessionContext ctx) {
		String zoneName = WebHelper.getRequiredZoneName(request);
		Long zoneId = WebHelper.getRequiredZoneId(request);
		String userName = WebHelper.getRequiredUserName(request);
		Long userId = WebHelper.getRequiredUserId(request);

		return setThreadContext(zoneName, zoneId, userName, userId, ctx);
	}
}
