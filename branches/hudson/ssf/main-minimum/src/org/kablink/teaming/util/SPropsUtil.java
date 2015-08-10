/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;

import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.SingletonViolationException;
import org.kablink.util.PropsUtil;

import org.springframework.beans.factory.InitializingBean;

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
	public static final String WIDEN_ENTRY_OWNER_ACCESS="entryacl.widens.owner.access";
	public static final String SIMPLEURL_CTX = "simpleurl.ctx";
	public static final String FROM_EMAIL_GLOBAL_OVERRIDE		= "ssf.outgoing.from.address";
	public static final String FROM_EMAIL_GLOBAL_OVERRIDE_ALL	= "ssf.outgoing.from.address.all";

	protected static Log logger = LogFactory.getLog(SPropsUtil.class);

	public SPropsUtil() {
		if(instance != null)
			throw new SingletonViolationException(SPropsUtil.class);
		
		instance = this;
	}
	
    public void setConfig(PropertiesSource config) {
    	setProperties(config.getProperties());
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		if(logger.isInfoEnabled()) {
			logger.info("System properties" + Constants.NEWLINE + toStringML(System.getProperties()));
			logger.info("System environment" + Constants.NEWLINE + toStringML(System.getenv()));
		}
		else {
			System.out.println("System properties" + Constants.NEWLINE + toStringML(System.getProperties()));
			System.out.println("System environment" + Constants.NEWLINE + toStringML(System.getenv()));		
		}	
	}

	public static String getDefaultHost() {
		String host = getString(SSF_DEFAULT_HOST);
		if(host != null && host.equalsIgnoreCase("_dynamic")) {
			try {
				host = java.net.InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				logger.warn(e.toString());
				host = "localhost";
			}
		}
		return host;
	}
}
