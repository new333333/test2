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
	
	public static ResourceDriver findResourceDriver(String driverName) {
		for(ResourceDriver driver : getResourceDrivers()) {
			if(driver.getName().equals(driverName))
				return driver;
		}
		return null;
	}
	
	private static ResourceDriverManager getResourceDriverManager() {
		return (ResourceDriverManager) SpringContextUtil.getBean("resourceDriverManager");
	}
}
