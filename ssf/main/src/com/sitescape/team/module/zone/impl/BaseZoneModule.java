package com.sitescape.team.module.zone.impl;

import com.sitescape.team.domain.Workspace;
import com.sitescape.team.util.SZoneConfig;

public class BaseZoneModule extends AbstractZoneModule {
	public void writeZone(String zoneName, String virtualHost) {}

	public Long getZoneIdByVirtualHost(String virtualHost) {
		String zoneName = getZoneNameByVirtualHost(virtualHost);
		Workspace top = getCoreDao().findTopWorkspace(zoneName);
		return top.getId();
	}

	public String getZoneNameByVirtualHost(String virtualHost) {
		return SZoneConfig.getDefaultZoneName();
	}

}
