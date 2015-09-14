/**
 * Copyright (c) 2008-2014 Novell, Inc. All Rights Reserved. THIS WORK IS AN
 * UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL PROPRIETARY AND TRADE SECRET
 * INFORMATION OF NOVELL, INC. ACCESS TO THIS WORK IS RESTRICTED TO NOVELL,INC.
 * EMPLOYEES WHO HAVE A NEED TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE
 * OF THEIR ASSIGNMENTS AND ENTITIES OTHER THAN NOVELL, INC. WHO HAVE
 * ENTERED INTO APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE
 * USED, PRACTICED, PERFORMED COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED,
 * RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF NOVELL,
 * INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 */
package org.kablink.teaming.module.keyshield;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jong
 *
 */
public class KShieldContextHolder {

	private static final ThreadLocal<Map<String,Object>> context = new ThreadLocal<Map<String,Object>>();
	
	public static final String HARDWARE_TOKEN_MISSING = "kshield.hardware.token.missing";
	public static final String CLIENT_TYPE = "kshield.client.type";
	
	public static final String CLIENT_TYPE_WEB = "Web";
	public static final String CLIENT_TYPE_WEBDAV = "WebDAV";

	public static void init() {
		Map<String,Object> contextMap = context.get();
		if(contextMap != null)
			contextMap.clear();
	}
	
	public static void destroy() {
		context.set(null);
	}
	
	public static void put(String key, Object value) {
		Map<String,Object> contextMap = context.get();
		if(contextMap == null) {
			contextMap = new HashMap<String,Object>();
			context.set(contextMap);
		}
		contextMap.put(key, value);
	}
	
	public static Object get(String key) {
		Map<String,Object> contextMap = context.get();
		if(contextMap != null)
			return contextMap.get(key);
		else
			return null;
	}
}
