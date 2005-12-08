package com.sitescape.ef.portletadapter;

public class PortletAdapterUtil {
	
	public static String getPortletNamespace(String portletName) {
		return "_" + portletName + "_";		
	}
}
