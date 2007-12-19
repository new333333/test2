package com.sitescape.team.module.zone.impl;

import com.sitescape.team.domain.Workspace;
import com.sitescape.team.util.SZoneConfig;

public class BaseZoneModule extends AbstractZoneModule {
	public void writeZone(String zoneName, String virtualHost) {
		logger.info("Cannot write zone " + zoneName + " - Open source edition does not support multi zone");
	}

	public boolean zoneExists(String zoneName) {
		if(SZoneConfig.getDefaultZoneName().equals(zoneName))
			return true;
		else
			return false;
	}

	public Long getZoneIdByVirtualHost(String virtualHost) {
		String zoneName = SZoneConfig.getDefaultZoneName();
		Workspace top = getCoreDao().findTopWorkspace(zoneName);
		return top.getId();
	}

	public String getZoneNameByVirtualHost(String virtualHost) {
		return SZoneConfig.getDefaultZoneName();
	}

	public String getVirtualHost(String zoneName) {
		return null;
	}
	public void removeZone(String zoneName) {
		logger.info("Cannot remove zone " + zoneName + " - Open source edition does not support multi zone");
	}

	public void addZoneUnderPortal(String zoneName, String virtualHost, String mailDomain) {
		logger.info("Cannot add zone " + zoneName + " - Open source edition does not support multi zone");
	}

}
