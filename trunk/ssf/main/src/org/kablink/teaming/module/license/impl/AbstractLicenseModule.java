/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.license.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.Restrictions;
import org.kablink.teaming.domain.LicenseStats;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.jobs.LicenseMonitor;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.license.LicenseException;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.util.Validator;
import org.springframework.beans.factory.InitializingBean;


abstract public class AbstractLicenseModule extends CommonDependencyInjection
implements LicenseModule, ZoneSchedule {


	protected LicenseMonitor getProcessor(Workspace zone) {
	   	String jobClass = SZoneConfig.getString(zone.getName(), "licenseConfiguration/property[@name='" + LicenseMonitor.LICENSE_JOB + "']");
    	if (Validator.isNotNull(jobClass)) {
    		try {
    			return (LicenseMonitor)ReflectHelper.getInstance(jobClass);
    		} catch (Exception e) {
 			   logger.error("Cannot instantiate LicenseMonitor custom class", e);
    		}
    	}
    	return (LicenseMonitor)ReflectHelper.getInstance(org.kablink.teaming.jobs.DefaultLicenseMonitor.class);
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

	protected long countActiveUsers(Long zoneId)
	{
		// Find all users that have logged in during the past year
		Calendar cal = Calendar.getInstance();
		Date startDate = new Date();
		cal.setTime(startDate);
		cal.add(Calendar.YEAR, -1);
		return getCoreDao().getLoginCount(cal.getTime());
	}
	
	protected long countInternalUsers(Long zoneId)
	{
		// Find all users that are not disabled and not deleted and who have a password.
		// If a user has been sync'd from an ldap source they won't have a password.
		return getCoreDao().countObjects(Principal.class,
										 (new FilterControls())
										 	.add(Restrictions.eq("type", "user"))
										 	.add(Restrictions.eq("disabled", Boolean.FALSE))
										 	.add(Restrictions.eq("deleted", Boolean.FALSE))
										 	.add(Restrictions.notNull("password")), zoneId);
	}
	
	
	/**
	 * Count the number of users that have been sync'd from an ldap source.
	 */
	protected long countSyncdUsers( Long zoneId )
	{
		FilterControls	filterControls;
		
		// Find all users that are not disabled and not deleted and who do NOT have a password.
		// If a user has been sync'd from an ldap source they won't have a password.
		filterControls = new FilterControls();
	 	filterControls.add( Restrictions.eq( "type", "user" ) );
	 	filterControls.add( Restrictions.eq( "disabled", Boolean.FALSE ) );
	 	filterControls.add( Restrictions.eq( "deleted", Boolean.FALSE ) );
	 	filterControls.add( Restrictions.isNull( "password" ) );;
		return getCoreDao().countObjects( Principal.class, filterControls, zoneId );
	}// end countUsersSyncdFromLdapSource()
	

	public void recordCurrentUsage()
	{
		LicenseStats stats = createSnapshot();
		getReportModule().addLicenseStats(stats);

		if(getLicenseManager().validLicense()) {
			stats = getReportModule().getLicenseHighWaterMark(getLicenseManager().getEffectiveDate(),
					getLicenseManager().getExpirationDate());
			getLicenseManager().recordUserCount(stats.getInternalUserCount(),
					stats.getExternalUserCount(), stats.getActiveUserCount());
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
											stats.getExternalUserCount(), stats.getActiveUserCount());
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
			getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
			break;
		default:
			throw new NotSupportedException(operation.toString(), "checkAccess");
		}
	}
}
