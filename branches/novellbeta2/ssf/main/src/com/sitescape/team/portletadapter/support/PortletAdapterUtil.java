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
package com.sitescape.team.portletadapter.support;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

public class PortletAdapterUtil {
	
	public static String getPortletNamespace(String portletName) {
		return "_" + portletName + "_";		
	}
	
	public static boolean isRunByAdapter(HttpServletRequest req) {
		if(req.getAttribute(KeyNames.CTX) == null)
			return false;
		else
			return true;
	}
	
	public static boolean isRunByAdapter(PortletRequest req) {
		if(req.getAttribute(KeyNames.CTX) == null)
			return false;
		else
			return true;
	}
}
