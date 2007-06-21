package com.sitescape.team.module.license.impl;

import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.Restrictions;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.license.LicenseModule;
import com.sitescape.team.module.report.ReportModule;

abstract public class AbstractLicenseModule extends CommonDependencyInjection
implements LicenseModule, InitializingBean {

	public void afterPropertiesSet() throws Exception {
		// TODO whatever you need to do to initialize the module... such as
		// initializing a background job, etc.
		createSnapshot();
	}

	private ReportModule reportModule;
	/**
	 * @return the reportModule
	 */
	public ReportModule getReportModule() {
		return reportModule;
	}

	/**
	 * @param reportModule the reportModule to set
	 */
	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}

	protected long countInternalUsers()
	{
		return getCoreDao().countObjects(Principal.class,
										 (new FilterControls())
										 	.add(Restrictions.notNull("password")));
	}

	abstract public void createSnapshot();
}
