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
package com.sitescape.team.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

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
/**
 * XXX singletons should be managed by Spring
 */
public class SPropsUtil extends PropsUtil implements InitializingBean {
	// This is a singleton class. 
	
	private static SPropsUtil instance; // A singleton instance
	
	public static final String DEBUG_WEB_REQUEST_ENV_PRINT = "debug.web.request.env.print";
	
	public static final String SSF_CTX ="ssf.ctx";
	public static final String SSF_DEFAULT_HOST = "ssf.default.host";
	public static final String SSF_PORT = "ssf.port";
	public static final String SSF_SECURE_PORT = "ssf.secure.port";
	public static final String SSFS_CTX = "ssfs.ctx";
	public static final String WIDEN_ACCESS="entryacl.widens.folderacl";
	public static final String SIMPLEURL_CTX = "simpleurl.ctx";

	protected Log logger = LogFactory.getLog(getClass());

	public SPropsUtil() {
		if(instance != null)
			throw new SingletonViolationException(SPropsUtil.class);
		
		instance = this;
	}
	
    public void setConfig(PropertiesClassPathConfigFiles config) {
    	setProperties(config.getProperties());
    }

	public void afterPropertiesSet() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info(ReleaseInfo.getReleaseInfo());
		} if (logger.isDebugEnabled()) {
			logger.debug("System properties" + Constants.NEWLINE + Utils.toStringML(System.getProperties()));
			logger.debug("System environment" + Constants.NEWLINE + Utils.toStringML(System.getenv()));
		}	
	}
}
