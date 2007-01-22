
package com.sitescape.ef.jobs;
import java.util.TimeZone;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.naming.NamingException;

import com.sitescape.ef.module.ldap.LdapModule;
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
