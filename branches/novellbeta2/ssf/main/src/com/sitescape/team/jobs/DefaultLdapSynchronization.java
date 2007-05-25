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

package com.sitescape.team.jobs;
import java.util.TimeZone;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.naming.NamingException;

import com.sitescape.team.module.ldap.LdapModule;
import com.sitescape.team.util.SpringContextUtil;

/**
 * @author Janet McCann
 *
 */
public class DefaultLdapSynchronization extends SSStatefulJob implements LdapSynchronization {

	/* (non-Javadoc)
	 * @see com.sitescape.team.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
    	LdapModule ldap = (LdapModule)SpringContextUtil.getBean("ldapModule");
		try {
			ldap.syncAll();
		} catch (NamingException ne) {
			if (ne.getCause() != null)
				logger.error("Ldap Syncronization error:" + ne.getCause().getLocalizedMessage());
			else
				logger.error("Ldap Syncronization error:" + ne.getExplanation());
				
			context.setResult("Failed");
		}
	}
	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new LdapJobDescription(zoneId));
	}
	public void setScheduleInfo(ScheduleInfo info) {
		setScheduleInfo(new LdapJobDescription(info.getZoneId()), info);
	}

	public void enable(boolean enable, Long zoneId) {
		enable(enable, new LdapJobDescription(zoneId));
 	}
	public class LdapJobDescription implements JobDescription {
		Long zoneId;
		public LdapJobDescription(Long zoneId) {
			this.zoneId = zoneId;
		}
	    public  String getDescription() {
	    	return zoneId.toString();
	    }
	    public Long getZoneId() {
	    	return zoneId;
	    }
	    public String getName() {
	    	return zoneId.toString();
	    }
	    public String getGroup() {
	    	return LdapSynchronization.LDAP_GROUP;
	    }		
       	public TimeZone getTimeZone() {
    		return getDefaultTimeZone();
    	}
       	public String getCleanupListener() {
    		return getDefaultCleanupListener();
    	}
    	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = new ScheduleInfo(zoneId);
    		//seconds minutes hours dayOfMonth months days year"
    		info.setSchedule(new Schedule("0 15 2 ? * mon-fri *"));
    		return info;
    	}
       	
	}



}
