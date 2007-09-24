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
package com.sitescape.team.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.SingletonViolationException;
import com.sitescape.util.PropsUtil;

/**
 * This class provides unified access to the SSF properties loaded from the 
 * properties file(s). Normally system has one such properties file, i.e., 
 * ssf.properties. But the system allows overriding of certain properties
 * without modifying the factory-shipped config file by creating additional
 * properties file and adding it to the appropriate list in the application
 * context config file (i.e., applicationContext.xml). This makes it easy
 * to upgrade system when it is expected that there will be large amount of
 * customizations performed. In fact, the system allows for any number of
 * such properties files to be chained.
 * 
 * @author jong
 *
 */
public class SPropsUtil extends PropsUtil {
	// This is a singleton class. 
	
	private static SPropsUtil instance; // A singleton instance
	
	public static final String DEBUG_WEB_REQUEST_ENV_PRINT = "debug.web.request.env.print";
	
	public static final String SSF_CTX ="ssf.ctx";
	public static final String SSF_HOST = "ssf.host";
	public static final String SSF_PORT = "ssf.port";
	public static final String SSF_SECURE_PORT = "ssf.secure.port";
	public static final String SSFS_CTX = "ssfs.ctx";
	public static final String WIDEN_ACCESS="entryacl.widens.folderacl";

	protected Log logger = LogFactory.getLog(getClass());

	public SPropsUtil() {
		if(instance != null)
			throw new SingletonViolationException(SPropsUtil.class);
		
		instance = this;
		
		logger.info("System properties" + Constants.NEWLINE + Utils.toStringML(System.getProperties()));
		logger.info("System environment" + Constants.NEWLINE + Utils.toStringML(System.getenv()));
	}
	
    public void setConfig(PropertiesClassPathConfigFiles config) {
    	setProperties(config.getProperties());
    }
}
