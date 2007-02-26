package com.sitescape.team.jobs;

import java.util.Date;
import java.util.TimeZone;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.mail.MailManager;
import com.sitescape.team.util.SpringContextUtil;
/**
 *
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


	public ScheduleInfo getScheduleInfo(Binder binder) {
		return getScheduleInfo(new MailJobDescription(binder));
	}
	public void setScheduleInfo(ScheduleInfo info, Binder binder) {
		info.getDetails().put("binder", binder.getId());
		setScheduleInfo(new MailJobDescription(binder), info);
	}

	public void enable(boolean enable, Binder binder) {
		enable(enable, new MailJobDescription(binder));
 	}
	public class MailJobDescription implements JobDescription {
		private Binder binder;
		private Long zoneId;
		public MailJobDescription(Binder binder) {
			this.binder = binder;
			this.zoneId = binder.getZoneId();
		}
    	public  String getDescription() {
    		return SSStatefulJob.trimDescription(binder.toString());
    	}
    	public Long getZoneId() {
    		return zoneId;
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
    		ScheduleInfo info = new ScheduleInfo(zoneId);
    		info.getDetails().put("binder", binder.getId());
    		info.getDetails().put("lastNotification", new Date());
    		return info;
    	}
       	
	}

}