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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.webdav.milton;

import javax.servlet.http.HttpServletRequest;

import com.bradmcevoy.http.Auth;

/**
 * This class extends com.bradmcevoy.http.ServletRequest to customize the behavior
 * around authorization object handling. Specifically, the Milton class assumes
 * that authorization object is obtainable only if the client request contains
 * an authorization HTTP header (e.g. with Digest or Basic). Otherwise, it returns
 * null, which subsequently causes problem with lock management. 
 * This class alters the behavior, and returns an appropriate authorization object
 * as long as the original HTTP request is authenticated and the user information
 * is available from it. So, this should be able to handle all authentication
 * scenarios (including pre-authentication and SSO), not just the ones that come
 * with HTTP authorization header.
 * 
 * @author Jong
 *
 */
public class WebdavServletRequest extends com.bradmcevoy.http.ServletRequest {

	private HttpServletRequest httpServletRequest;
	
	/**
	 * @param r
	 */
	public WebdavServletRequest(HttpServletRequest r) {
		super(r);
		this.httpServletRequest = r;
	}

	@Override
	public Auth getAuthorization() {
		Auth auth = super.getAuthorization();
		if(auth == null) {
			String userName = httpServletRequest.getRemoteUser();
			if(userName != null && !userName.equals("")) {
				auth = new Auth(userName, null);
				super.setAuthorization(auth);
			}
		}
		return auth;
	}
}
