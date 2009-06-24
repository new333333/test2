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
package org.kablink.teaming.web.servlet.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.kablink.teaming.asmodule.bridge.BridgeUtil;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;


public class SessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent se) {
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		// This listener is invoked in the same thread executing portal-side code
		// as side effect of the portal session being invalidated. Consequently,
		// the context classloader is that of the portal web app, which is
		// inappropriate for executing Teaming side code. Therefore, we need
		// to switch the context classloader for the duration of this call.
		ClassLoader clSave = Thread.currentThread().getContextClassLoader();
		
		try {
			Thread.currentThread().setContextClassLoader(BridgeUtil.getClassLoader());
			doSessionDestroyed(se);
		}
		finally {
			Thread.currentThread().setContextClassLoader(clSave);
		}
	}
		
	private void doSessionDestroyed(HttpSessionEvent se) {
		HttpSession ses = se.getSession();
		
		final String infoId = (String) ses.getAttribute(WebKeys.TOKEN_INFO_ID);
		
		if(infoId != null) {
			Long userId = (Long) ses.getAttribute(WebKeys.USER_ID);
			final AccessTokenManager accessTokenManager = (AccessTokenManager) SpringContextUtil.getBean("accessTokenManager");
			Long zoneId = (Long) ses.getAttribute(WebKeys.ZONE_ID);
			// Make sure to run it in the user's context.	
			RunasTemplate.runas(new RunasCallback() {
				public Object doAs() {
					accessTokenManager.destroyTokenInfoSession(infoId);
					return null;
				}
			}, zoneId, userId);
		}
	}

}
