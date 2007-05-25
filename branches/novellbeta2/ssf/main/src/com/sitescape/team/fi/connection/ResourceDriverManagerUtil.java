package com.sitescape.team.fi.connection;

import java.util.List;

import com.sitescape.team.util.SpringContextUtil;

public class ResourceDriverManagerUtil {

	public static List<ResourceDriver> getResourceDrivers() {
		return getResourceDriverManager().getResourceDrivers();
	}
	
	public static List<ResourceDriver> getAllowedResourceDrivers() {
		return getResourceDriverManager().getAllowedResourceDrivers();
	}
	
	private static ResourceDriverManager getResourceDriverManager() {
		return (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
	}
}
