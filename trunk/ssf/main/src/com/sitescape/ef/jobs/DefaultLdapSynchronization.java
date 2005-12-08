
package com.sitescape.ef.jobs;
import java.util.Date;
import java.util.TimeZone;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Workspace;
import javax.naming.NamingException;

import com.sitescape.ef.jobs.DefaultEmailNotification.MailJobDescription;
import com.sitescape.ef.jobs.SSStatefulJob.JobDescription;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.util.SpringContextUtil;
/**
 * @author Janet McCann
 *
 */
public class DefaultLdapSynchronization extends SSStatefulJob implements LdapSynchronization {

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
    	LdapModule ldap = (LdapModule)SpringContextUtil.getBean("ldapModule");
		try {
			ldap.syncAll(zoneName);
		} catch (NamingException ne) {
			logger.error("Error synchronizing with ldap " + ne.getMessage());
		}
	}
	public ScheduleInfo getScheduleInfo(String zoneName) {
		return getScheduleInfo(new LdapJobDescription(zoneName));
	}
	public void setScheduleInfo(ScheduleInfo info) {
		setScheduleInfo(new LdapJobDescription(info.getZoneName()), info);
	}

	public void enable(boolean enable, String zoneName) {
		enable(enable, new LdapJobDescription(zoneName));
 	}
	public class LdapJobDescription implements JobDescription {
		String zoneName;
		public LdapJobDescription(String zoneName) {
			this.zoneName = zoneName;
		}
	    public  String getDescription() {
	    	return SSStatefulJob.trimDescription(zoneName);
	    }
	    public String getZoneName() {
	    	return zoneName;
	    }
	    public String getName() {
	    	return zoneName;
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
    		ScheduleInfo info = new ScheduleInfo(zoneName);
    		//seconds minutes hours dayOfMonth months days year"
    		info.setSchedule(new Schedule("0 15 2 ? * mon-fri *"));
    		return info;
    	}
       	
	}


}
