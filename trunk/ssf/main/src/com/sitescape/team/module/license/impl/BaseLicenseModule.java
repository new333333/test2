package com.sitescape.team.module.license.impl;

import java.util.Date;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.LicenseStats;

public class BaseLicenseModule extends AbstractLicenseModule {
	public void createSnapshot()
	{
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		getReportModule().addLicenseStats(new LicenseStats(zoneId, new Date(), countInternalUsers(zoneId), 0, 0));
	}
}
