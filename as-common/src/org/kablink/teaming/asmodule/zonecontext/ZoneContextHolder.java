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
package org.kablink.teaming.asmodule.zonecontext;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ZoneContextHolder {
    
    private static final ThreadLocal<ZoneContext> ZONE_CONTEXT = new ThreadLocal<ZoneContext>();

    public static ZoneContext getZoneContext() {
    	return getZoneContext(false);
    }
    
    public static void setZoneContext(ZoneContext context) {
    	ZONE_CONTEXT.set(context);
    }
    
    public static void setServerName(String serverName) {
    	getZoneContext(true).setServerName(serverName.toLowerCase());
    }
    
    public static String getServerName() {
    	return ZONE_CONTEXT.get().getServerName();
    }
    
    public static void setServerPort(Integer serverPort) {
    	getZoneContext(true).setServerPort(serverPort);
    }
    
    public static Integer getServerPort() {
    	return ZONE_CONTEXT.get().getServerPort();
    }
    
    public static void setSecure(Boolean secure) {
    	getZoneContext(true).setSecure(secure);
    }
    
    public static Boolean isSecure() {
    	return ZONE_CONTEXT.get().getSecure();
    }
    
    public static void setClientAddr(String clientAddr) {
    	getZoneContext(true).setClientAddr(clientAddr);
    }
    
    public static String getClientAddr() {
    	return ZONE_CONTEXT.get().getClientAddr();
    }
    
    public static void setWebappName(String webappName) {
    	getZoneContext(true).setWebappName(webappName);
    }
    
    public static String getWebappName() {
    	return ZONE_CONTEXT.get().getWebappName();
    }
    
    public static void setUseRuntimeContext(Boolean useRuntimeContext) {
    	getZoneContext(true).setUseRuntimeContext(useRuntimeContext);
    }
    
    public static Boolean getUseRuntimeContext() {
    	return ZONE_CONTEXT.get().getUseRuntimeContext();
    }
    
    public static Object getProperty(Object key) {
    	Map<Object,Object> properties = ZONE_CONTEXT.get().getProperties();
    	if(properties == null)
    		return null;
    	else
    		return properties.get(key);
    }
    
    public static void setProperty(Object key, Object value) {
    	Map<Object,Object> properties = getZoneContext(true).getProperties();
    	if(properties == null) {
    		properties = new HashMap<Object,Object>();
    		getZoneContext(false).setProperties(properties);
    	}
    	properties.put(key, value);
    }
    
    public static HttpServletRequest getHttpServletRequest() {
    	return ZONE_CONTEXT.get().getHttpServletRequest();
    }
    
    public static void setHttpServletRequest(HttpServletRequest httpServletRequest) {
    	getZoneContext(true).setHttpServletRequest(httpServletRequest);
    }
    
    public static HttpSession getHttpSession() {
    	HttpServletRequest request = getHttpServletRequest();
    	if(request != null)
    		return request.getSession(false);
    	return null;
    }
    
    public static void clear() {
    	setZoneContext(null);
    }
    
    private static ZoneContext getZoneContext(boolean create) {
    	ZoneContext zc = ZONE_CONTEXT.get();
    	if(zc == null && create) {
    		zc = new ZoneContext();
    		setZoneContext(zc);
    	}
    	return zc;
    }

}
