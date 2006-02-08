
package com.sitescape.ef.jobs;
import java.util.TimeZone;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.naming.NamingException;

import com.sitescape.ef.ldap.LdapModule;
import com.sitescape.ef.util.SessionUtil;
import com.sitescape.ef.util.SpringContextUtil;

import com.sitescape.ef.util.DirtyInterceptor;
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
			logger.error("Ldap Syncronization error:" + ne.getLocalizedMessage());
			context.setResult("Failed");
		}
	}
	protected void setupSession() {
		//provide an entity interceptor to index only entries that are modified
		SessionUtil.sessionStartup(new DirtyInterceptor());

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
