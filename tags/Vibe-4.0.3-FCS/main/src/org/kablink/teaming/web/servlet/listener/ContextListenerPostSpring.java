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

import java.util.Date;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.security.accesstoken.AccessTokenManager;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.springframework.transaction.support.TransactionTemplate;


public class ContextListenerPostSpring implements ServletContextListener {

	private static volatile boolean shutdownInProgress = false;
	private static volatile boolean startupInProgress = true;
	
	private static Log logger = LogFactory.getLog(ContextListenerPostSpring.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			// This should be the first thing in this method. Don't place anything before this.
			initZones();
			
			// Do any post processing tasks after the zones have been properly initialized
			initZonesPostProcessing();
	
			initAccessTokens();
			
			/// For simple profiler ///
			boolean simpleProfilerEnable = SPropsUtil.getBoolean("simple.profiler.enable", false);
			if(simpleProfilerEnable)
				SimpleProfiler.enable();
			else
				SimpleProfiler.disable();
			
			startupInProgress = false;
		}
		catch(Throwable t) {
			logger.error("Error during startup", t);
			throw t;
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		shutdownInProgress = true;
		/// For simple profiler ///
		SimpleProfiler.dumpToLog();
		SimpleProfiler.clear();
	}

	public static boolean isShutdownInProgress() {
		return shutdownInProgress;
	}
	
	public static boolean isStartupInProgress() {
		return startupInProgress;
	}
	
	private void initAccessTokens() {
		/// For access token manager ///
		
		// Do not destroy all tokens when a node starts up because -
		// (a) Currently we have no way to identify only those tokens that belong to this particular node.
		// Blindly destroying all tokens within the installation can end up wiping out all active tokens 
		// that belong to other nodes within the cluster.
		// (b) By default, Tomcat tries to serialize and save user sessions when a node shuts down,
		// which means that a previous interactive session may still be good after node restart.
		// In that case, it makes sense to allow previous tokens associated with the resurrected session
		// to be also reusable. To prevent stale tokens from being used indefinitely, the runtime needs
		// to check and validate the association between the token and the currently-active session
		// in the context of which the remote app is being executed.
		// (c) If Teaming is ever configured to support replicated sessions (i.e., a configuration
		// where a session is not exclusively owned by a node, but rather shared among them), then
		// it becomes even harder or impossible to identity ownership of tokens. In that scenario, 
		// token cleanup at the individual node level isn't quite plausible.
		/*
		AccessTokenManager accessTokenManager = (AccessTokenManager) SpringContextUtil.getBean("accessTokenManager");
		
		accessTokenManager.destroyAllTokenInfoSession();
		accessTokenManager.destroyAllTokenInfoRequest();
		accessTokenManager.destroyAllTokenInfoApplication();
		*/
		
		// Instead, we will only purge those tokens that we consider "stale", which is determined by
		// how long the token has existed since the time of its creation or last use.
		AccessTokenManager accessTokenManager = (AccessTokenManager) SpringContextUtil.getBean("accessTokenManager");

		long timeoutDays = SPropsUtil.getInt("token.info.startup.purge.timeout", 30);
		
		Date thisDate = new Date(System.currentTimeMillis() - timeoutDays * 24 * 60 * 60 * 1000);

		accessTokenManager.destroyTokenInfoOlderThan(thisDate);
	}
	
	private void initZones() {
		getZoneModule().initZones();
	}
	
	private void initZonesPostProcessing() {
		getZoneModule().initZonesPostProcessing();
	}
	
	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
	
	private CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
	private TransactionTemplate getTransactionTemplate() {
		return (TransactionTemplate) SpringContextUtil.getBean("transactionTemplate");
	}
}
