/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.util;

import javax.servlet.ServletContextEvent;

import com.sitescape.util.ServerDetector;

/**
 * 
 * @author jong
 *
 */
public class Log4jConfigListener extends org.springframework.web.util.Log4jConfigListener {

	public void contextInitialized(ServletContextEvent event) {
		if(!ServerDetector.isJBoss()) {
			super.contextInitialized(event);
		}
	}

	public void contextDestroyed(ServletContextEvent event) {
		if(!ServerDetector.isJBoss()) {
			super.contextDestroyed(event);
		}
	}

}
