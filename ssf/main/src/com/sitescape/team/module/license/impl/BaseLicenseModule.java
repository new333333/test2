package com.sitescape.team.module.license.impl;

import java.util.Date;

import com.sitescape.team.domain.LicenseStats;

public class BaseLicenseModule extends AbstractLicenseModule {
	public void createSnapshot()
	{
		getReportModule().addLicenseStats(new LicenseStats(new Date(), countInternalUsers(), 0, 0));
	}
}
