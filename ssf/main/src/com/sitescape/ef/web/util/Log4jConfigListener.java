package com.sitescape.ef.util;

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
