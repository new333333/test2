package com.sitescape.ef.portletadapter.support;

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
