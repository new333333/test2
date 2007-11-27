package com.sitescape.team.module.zone.impl;

import com.sitescape.team.domain.Workspace;
import com.sitescape.team.util.SZoneConfig;

public class BaseZoneModule extends AbstractZoneModule {
	public void writeZone(String zoneName, String virtualHost) {
		logger.info("Cannot write zone " + zoneName + " - Open source edition does not support multi zone");
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

}
