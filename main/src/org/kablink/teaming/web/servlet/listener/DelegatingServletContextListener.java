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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;

public class DelegatingServletContextListener implements ServletContextListener {

	ServletContextListener[] contextListeners;
	Log logger = LogFactory.getLog(getClass());
	
	public void contextDestroyed(ServletContextEvent sce) {
		for(ServletContextListener listener : contextListeners) {
			try {
				if(logger.isDebugEnabled())
					logger.debug("Invoking contextDestroyed on delegate " + listener.getClass().getName());
				listener.contextDestroyed(sce);
			}
			catch(Exception e) {
				logger.error("Error while executing delegate " + listener.getClass().getSimpleName(), e);
				// Continue to the next delegate
			}
		}
	}

	public void contextInitialized(ServletContextEvent sce) {
		init();
		for(ServletContextListener listener : contextListeners) {
			try {
				if(logger.isDebugEnabled())
					logger.debug("Invoking contextInitialized on delegate " + listener.getClass().getName());
				listener.contextInitialized(sce);
			}
			catch(Exception e) {
				logger.error("Error while executing delegate " + listener.getClass().getSimpleName(), e);
				// Continue to the next delegate
			}
		}
	}

	private void init() {
		String[] classNames = SPropsUtil.getStringArray("servlet.context.listener.classes", ",");
		contextListeners = new ServletContextListener[classNames.length];
		for(int i = 0; i < classNames.length; i++) {
			try {
				if(logger.isDebugEnabled())
					logger.debug("Creating delegate " + classNames[i]);
				contextListeners[i] = (ServletContextListener) ReflectHelper.getInstance(classNames[i]);
			}
			catch(RuntimeException e) {
				logger.error("Error creating delegate " + classNames[i], e);
				throw e;
			}
			catch(Error e) {
				logger.error("Error creating delegate " + classNames[i], e);
				throw e;
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("Number of delegates is " + classNames.length);		
	}
}
