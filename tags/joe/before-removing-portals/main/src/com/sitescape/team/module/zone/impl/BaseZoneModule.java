package com.sitescape.team.module.zone.impl;

import java.util.Arrays;
import java.util.LinkedList;
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

	public Long addZone(String zoneName, String virtualHost, String mailDomain) {
		logger.info("Cannot add zone " + zoneName + " under portal - Open source edition does not support multi zone");
		return null;
	}

	public void modifyZone(String zoneName, String virtualHost, String mailDomain) {
		logger.info("Cannot modify zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

	public void deleteZone(String zoneName) {
		logger.info("Cannot remove zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

	public List<ZoneInfo> getZoneInfos() {
		ZoneInfo info = getZoneInfo(getZoneIdByZoneName(SZoneConfig.getDefaultZoneName()));
		return Arrays.asList(new ZoneInfo[] {info});
	}

	public ZoneInfo getZoneInfo(Long zoneId) {
		ZoneInfo info = new ZoneInfo();
		info.setZoneName(SZoneConfig.getDefaultZoneName());
		if(!zoneId.equals(getZoneIdByZoneName(info.getZoneName()))) {
			return null;
		}
		info.setZoneId(zoneId);
		info.setVirtualHost(getVirtualHost(info.getZoneName()));
		return info;
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
