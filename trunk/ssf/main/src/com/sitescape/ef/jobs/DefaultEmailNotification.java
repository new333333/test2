package com.sitescape.ef.jobs;

import java.util.TimeZone;
import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitescape.ef.mail.MailManager;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.NoBinderByTheIdException;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.util.SpringContextUtil;
/**
 *
 * @author Jong Kim
 */
public class DefaultEmailNotification extends SSStatefulJob implements EmailNotification {
	 
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailManager mail = (MailManager)SpringContextUtil.getBean("mailManager");
		try {
			Date end = mail.sendNotifications(new Long(jobDataMap.getLong("binder")), (Date)jobDataMap.get("lastNotification") );
			jobDataMap.put("lastNotification", end);
		} catch (NoBinderByTheIdException nf) {
			removeJobOnError(context,nf);
		} 
    }


	public ScheduleInfo getScheduleInfo(String zoneName, Binder binder) {
		return getScheduleInfo(new MailJobDescription(zoneName, binder));
	}
	public void setScheduleInfo(ScheduleInfo info, String zoneName,  Binder binder) {
		info.getDetails().put("binder", binder.getId());
		setScheduleInfo(new MailJobDescription(zoneName, binder), info);
	}

	public void enable(boolean enable, String zoneName, Binder binder) {
		enable(enable, new MailJobDescription(zoneName, binder));
 	}
	public class MailJobDescription implements JobDescription {
		private Binder binder;
		private String zoneName;
		public MailJobDescription(String zoneName, Binder binder) {
			this.binder = binder;
		}
    	public  String getDescription() {
    		return SSStatefulJob.trimDescription(binder.toString());
    	}
    	public String getZoneName() {
    		return zoneName;
    	}
    	public String getName() {
    		return binder.getId().toString();
    	}
    	public String getGroup() {
    		return EmailNotification.NOTIFICATION_GROUP;
    	}		
       	public TimeZone getTimeZone() {
    		return getDefaultTimeZone();
     	}
       	public String getCleanupListener() {
    		return getDefaultCleanupListener();
    	}
    	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = new ScheduleInfo(zoneName);
    		info.getDetails().put("binder", binder.getId());
    		info.getDetails().put("lastNotification", new Date());
    		return info;
    	}
       	
	}

}