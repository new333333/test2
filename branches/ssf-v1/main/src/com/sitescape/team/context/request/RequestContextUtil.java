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

import com.sitescape.team.domain.User;

/**
 *
 * @author Jong Kim
 */
public class RequestContextUtil {

	public static RequestContext setThreadContext(String zoneName, String userName) {
		if(userName == null)
			throw new IllegalArgumentException("User name must be specified");
		if(zoneName == null)
			throw new IllegalArgumentException("Zone name must be specified");
		
		RequestContext rc = new RequestContext(zoneName, userName);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}
	public static RequestContext setThreadContext(Long zoneId, Long userId) {
		if(zoneId == null)
			throw new IllegalArgumentException("User id must be specified");
		if(userId == null)
			throw new IllegalArgumentException("Zone id must be specified");
		
		RequestContext rc = new RequestContext(zoneId, userId);
		RequestContextHolder.setRequestContext(rc);
		
		return rc;
	}	
	
	public static RequestContext setThreadContext(User user) {
		RequestContext rc = setThreadContext(user.getParentBinder().getParentBinder().getName(), user.getName());
		rc.setUser(user);
		return rc;		
	}
	
	public static void clearThreadContext() {
		RequestContextHolder.clear();
	}
}
