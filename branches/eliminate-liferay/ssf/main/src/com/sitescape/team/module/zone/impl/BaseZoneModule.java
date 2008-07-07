package com.sitescape.team.module.zone.impl;

import java.util.List;

import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.ZoneInfo;
import com.sitescape.team.module.zone.ZoneException;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.SPropsUtil;
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
		return SPropsUtil.getString(SPropsUtil.SSF_DEFAULT_HOST);
	}
	protected void removeZone(String zoneName) {
		logger.info("Cannot remove zone " + zoneName + " - Open source edition does not support multi zone");
	}

	public void addZoneUnderPortal(String zoneName, String virtualHost, String mailDomain) {
		logger.info("Cannot add zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

	public void modifyZoneUnderPortal(String zoneName, String virtualHost, String mailDomain) {
		logger.info("Cannot modify zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

	public void deleteZoneUnderPortal(String zoneName) {
		logger.info("Cannot remove zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

	public List<ZoneInfo> getZoneInfos() {
		return null; // meaningless in open edition
	}

	public void checkAccess() throws AccessControlException {
		throw new AccessControlException();
	}

	public boolean testAccess() {
		return false;
	}

	public void modifyZoneUnderPortal(String zoneName, String virtualHost) throws ZoneException {
		logger.info("Cannot modify zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

}
