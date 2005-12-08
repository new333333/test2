
package com.sitescape.ef.jobs;
import java.util.TimeZone;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

import com.sitescape.ef.domain.Workspace;
import javax.naming.NamingException;
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
	public void checkSchedule(Scheduler scheduler, Workspace workspace) {
		JobDescription job = new LdapJobDescription(workspace);
		verifySchedule(scheduler, job);
	}
	public class LdapJobDescription implements JobDescription {
		private Workspace workspace;
		public LdapJobDescription(Workspace workspace) {
			this.workspace = workspace;
		}
		public  String getSchedule() {
			return workspace.getLdapConfig().getSchedule().getQuartzSchedule();
		}
    	public  String getDescription() {
    		return "Ldap synchronization " + workspace;
    	}
    	public  JobDataMap getData() {
			JobDataMap data = new JobDataMap();
			data.put("workspace",workspace.getId());
			data.put("zoneName",workspace.getZoneName());
			return data;
    	}
    	public  boolean isEnabled() {
    		return workspace.getLdapConfig().isScheduleEnabled();
    	}
    	public String getName() {
    		return workspace.getZoneName() + ":" + workspace.getName() + ":" + workspace.getId();
    	}
    	public String getGroup() {
    		return LdapSynchronization.LDAP_GROUP;
    	}		
    	public TimeZone getTimeZone() {
    		try {
    			return RequestContextHolder.getRequestContext().getUser().getTimeZone();
    		} catch (Exception e) {
    			return TimeZone.getDefault();
    		}
    	}
	}
}
