/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.license.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.Restrictions;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.FolderDelete;
import com.sitescape.team.jobs.LicenseMonitor;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.license.LicenseModule;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Validator;

abstract public class AbstractLicenseModule extends CommonDependencyInjection
implements LicenseModule, InitializingBean {

	public void afterPropertiesSet() throws Exception {
		// TODO whatever you need to do to initialize the module... such as
		// initializing a background job, etc.
 		List companies = getCoreDao().findCompanies();
 		for (int i=0; i<companies.size(); ++i) {
 			Workspace zone = (Workspace)companies.get(i);
 			startScheduledJobs(zone);
	   }
	}
    protected void startScheduledJobs(Workspace zone) {
    	String jobClass = SZoneConfig.getString(zone.getName(), "licenseConfiguration/property[@name='" + LicenseMonitor.LICENSE_JOB + "']");
    	if (Validator.isNull(jobClass)) jobClass = "com.sitescape.team.jobs.DefaultLicenseMonitor";
    	try {
    		Class processorClass = ReflectHelper.classForName(jobClass);
    		LicenseMonitor job = (LicenseMonitor)processorClass.newInstance();
    		String hrString = (String)SZoneConfig.getString(zone.getName(), "licenseConfiguration/property[@name='" + LicenseMonitor.LICENSE_HOUR + "']");
    		int hour = 6;
    		try {
    			hour = Integer.parseInt(hrString);
    		} catch (Exception ex) {};
    		job.schedule(zone.getId(), hour);

    	} catch (ClassNotFoundException e) {
    		throw new ConfigurationException(
    				"Invalid LicenseMontior class name '" + jobClass + "'",
    				e);
    	} catch (InstantiationException e) {
    		throw new ConfigurationException(
    				"Cannot instantiate LicenseMonitor of type '"
    				+ jobClass + "'");
    	} catch (IllegalAccessException e) {
    		throw new ConfigurationException(
    				"Cannot instantiate LicenseMonitor of type '"
    				+ jobClass + "'");
    	} 
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

	protected long countInternalUsers(Long zoneId)
	{
		return getCoreDao().countObjects(Principal.class,
										 (new FilterControls())
										 	.add(Restrictions.eq("parentBinder.zoneId", zoneId))
										 	.add(Restrictions.eq("type", "user"))
										 	.add(Restrictions.eq("disabled", Boolean.FALSE))
										 	.add(Restrictions.eq("deleted", Boolean.FALSE))
										 	.add(Restrictions.notNull("password")));
	}

	abstract public void createSnapshot();
}
