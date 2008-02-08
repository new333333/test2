/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.license.impl;

import java.util.Collection;
import java.util.List;

import org.dom4j.Document;
import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.Restrictions;
import com.sitescape.team.domain.LicenseStats;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.LicenseMonitor;
import com.sitescape.team.jobs.ZoneSchedule;
import com.sitescape.team.license.LicenseException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.license.LicenseModule;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.util.Validator;

abstract public class AbstractLicenseModule extends CommonDependencyInjection
implements LicenseModule, ZoneSchedule {


	protected LicenseMonitor getProcessor(Workspace zone) {
	   	String jobClass = SZoneConfig.getString(zone.getName(), "licenseConfiguration/property[@name='" + LicenseMonitor.LICENSE_JOB + "']");
    	if (Validator.isNull(jobClass)) jobClass = "com.sitescape.team.jobs.DefaultLicenseMonitor";
    	try {
    		Class processorClass = ReflectHelper.classForName(jobClass);
    		LicenseMonitor job = (LicenseMonitor)processorClass.newInstance();
    		return job;
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
	//called on zone startup
    public void startScheduledJobs(Workspace zone) {
 	   	if (zone.isDeleted()) return;
 	   	LicenseMonitor job =getProcessor(zone);
   		String hrString = (String)SZoneConfig.getString(zone.getName(), "licenseConfiguration/property[@name='" + LicenseMonitor.LICENSE_HOUR + "']");
    	int hour = 6;
    	try {
    		hour = Integer.parseInt(hrString);
    	} catch (Exception ex) {};
    	job.schedule(zone.getId(), hour);

	}

	//called on zone delete
	public void stopScheduledJobs(Workspace zone) {
 	   	LicenseMonitor job =getProcessor(zone);
   		job.remove(zone.getId());
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
										 	.add(Restrictions.eq("type", "user"))
										 	.add(Restrictions.eq("disabled", Boolean.FALSE))
										 	.add(Restrictions.eq("deleted", Boolean.FALSE))
										 	.add(Restrictions.notNull("password")), zoneId);
	}

	public void recordCurrentUsage()
	{
		LicenseStats stats = createSnapshot();
		getReportModule().addLicenseStats(stats);

		if(getLicenseManager().validLicense()) {
			stats = getReportModule().getLicenseHighWaterMark(getLicenseManager().getEffectiveDate(),
					getLicenseManager().getExpirationDate());
			getLicenseManager().recordUserCount(stats.getInternalUserCount(),
					stats.getExternalUserCount());
		}
	}

	abstract protected LicenseStats createSnapshot();
	
	public void updateLicense() throws AccessControlException, LicenseException
	{
		checkAccess(LicenseOperation.manageLicense);
		getLicenseManager().loadLicense();
		LicenseStats stats = getReportModule().getLicenseHighWaterMark(getLicenseManager().getEffectiveDate(),
											      getLicenseManager().getExpirationDate());
		getLicenseManager().recordUserCount(stats.getInternalUserCount(),
											stats.getExternalUserCount());
	}
	
	public void validateLicense() throws LicenseException
	{
		getLicenseManager().validate();
	}

	public Collection<Document> getLicenses()
	{
		return getLicenseManager().getLicenses();
	}
	
	public long getRegisteredUsers()
	{
			return getLicenseManager().getRegisteredUsers();
	}

	public long getExternalUsers()
	{
			return getLicenseManager().getExternalUsers();
	}

	public boolean testAccess(LicenseOperation operation)
	{
   		try {
   			checkAccess(operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}		
	}
	
	protected void checkAccess(LicenseOperation operation) throws AccessControlException
	{
		switch (operation) {
		case manageLicense:
			getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(),
					WorkAreaOperation.SITE_ADMINISTRATION);
			break;
		default:
			throw new NotSupportedException(operation.toString(), "checkAccess");
		}
	}
}
