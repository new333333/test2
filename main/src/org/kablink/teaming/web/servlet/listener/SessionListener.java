/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import java.io.Serializable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.bridge.BridgeUtil;
import org.kablink.teaming.module.keyshield.KShieldHelper;
import org.kablink.teaming.module.keyshield.KShieldContextHolder;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.Tabs;

public class SessionListener implements HttpSessionListener {

	private static Log logger = LogFactory.getLog(SessionListener.class);
	
	// Singleton instance (to be precise, there might be multiple instances of
	// this in JVM due to those created from deserialization during session
	// re-activation).
	public static ActiveSessionCounter activeSessionCounter = new ActiveSessionCounter();
	
	public void sessionCreated(HttpSessionEvent se) {
		if(logger.isDebugEnabled())
			logger.debug("Creating session: " + se.getSession().getId());
		
		se.getSession().setAttribute(activeSessionCounter.getClass().getName(), activeSessionCounter);
		
		ActiveSessionCounter.incrementWebSessionCount();
		
		// Transfer the state from the KeyShield request/thread context into the guest session
		// so that we can consult the information across multiple requests within current session.
		KShieldHelper.transferStateFromRequestContextToHttpSession(se.getSession());
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		if(logger.isDebugEnabled())
			logger.debug("Destroying session: " + se.getSession().getId());

		ActiveSessionCounter.decrementWebSessionCount();

		// This listener is invoked in the same thread executing portal-side code
		// as side effect of the portal session being invalidated. Consequently,
		// the context class loader is that of the portal web application, which is
		// inappropriate for executing Teaming side code. Therefore, we need
		// to switch the context class loader for the duration of this call.
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
		final HttpSession ses = se.getSession();
		
		final String infoId = (String) ses.getAttribute(WebKeys.TOKEN_INFO_ID);
		final Long   userId = (Long)   ses.getAttribute(WebKeys.USER_ID);
		final Long   zoneId = (Long)   ses.getAttribute(WebKeys.ZONE_ID);
		
		if(userId != null && zoneId != null) {
			// Make sure to run it in the user's context.	
			RunasTemplate.runas(new RunasCallback() {
				public Object doAs() {
					Tabs.saveUserTabs(ses, userId);
					if(infoId != null) {
						AccessTokenManager accessTokenManager = (AccessTokenManager) SpringContextUtil.getBean("accessTokenManager");
						accessTokenManager.destroyTokenInfoSession(infoId);
					}
					return null;
				}
			}, zoneId, userId);
		}
	}
	
	public static class ActiveSessionCounter implements Serializable, HttpSessionActivationListener {

		private static final long serialVersionUID = 1L;
		
		// The following two variables are used to keep the counters and guarded by the class.
		// Since there might be multiple instances of this class due to session passivation/
		// activation, it is important NOT to use instance level variables to keep JVM wide
		// counters.
		private static int activeSessionCount = 0;
		private static int peakActiveSessionCount = 0;

		@Override
		public void sessionDidActivate(HttpSessionEvent se) {
			if(logger.isDebugEnabled())
				logger.debug("Activating session: " + se.getSession().getId());
			
			incrementWebSessionCount();
		}

		@Override
		public void sessionWillPassivate(HttpSessionEvent se) {
			if(logger.isDebugEnabled())
				logger.debug("Passivating session: " + se.getSession().getId());
			
			decrementWebSessionCount();
		}
		
		public static synchronized int incrementWebSessionCount() {
			if(++activeSessionCount > peakActiveSessionCount)
				peakActiveSessionCount = activeSessionCount;
			return activeSessionCount;
		}
		
		public static synchronized int decrementWebSessionCount() {
			return --activeSessionCount;
		}
		
		public static synchronized int getActiveSessionCount() {
			return activeSessionCount;
		}
		
		public static synchronized int getPeakActiveSessionCount() {
			return peakActiveSessionCount;
		}
		
	}
	
}
