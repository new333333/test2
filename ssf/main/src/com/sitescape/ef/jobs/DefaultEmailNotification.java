package com.sitescape.ef.jobs;
import java.util.TimeZone;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.util.SpringContextUtil;
/**
 *
 * @author Jong Kim
 */
public class DefaultFolderEmailNotification extends SSStatefulJob implements FolderEmailNotification {
	 
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		try {
			mail.sendNotifications(new Long(jobDataMap.getLong("forum")));
		} catch (NoFolderByTheIdException nf) {
			removeJobOnError(context,nf);
		} catch (ConfigurationException cf) {
			throw new JobExecutionException(cf);
		}
    }
	protected void removeJobOnError(JobExecutionContext context, Exception e) throws JobExecutionException {
		if (e instanceof NoUserByTheIdException) {
			AdminModule admin = (AdminModule)SpringContextUtil.getBean("adminModule");
			admin.disableNotification(new Long(jobDataMap.getLong("forum")));
		}
		super.removeJobOnError(context,e);	
	}
	public void checkSchedule(Scheduler scheduler, Binder forum) {
		JobDescription job = new MailJobDescription(forum);
		verifySchedule(scheduler, job);
	}
	public class MailJobDescription implements JobDescription {
		private Binder forum;
		public MailJobDescription(Binder forum) {
			this.forum = forum;
		}
		public  String getSchedule() {
			return forum.getNotificationDef().getSchedule();
		}
    	public  String getDescription() {
    		return "Email notification for " + forum;
    	}
    	public  JobDataMap getData() {
			JobDataMap data = new JobDataMap();
			data.put("forum",forum.getId());
			data.put("zoneName",forum.getZoneName());
			return data;
    	}
    	public  boolean isEnabled() {
    		return !forum.getNotificationDef().isDisabled();
    	}
    	public String getName() {
    		return forum.getZoneName() + ":" + forum.getName() + ":" + forum.getId();
    	}
    	public String getGroup() {
    		return FolderEmailNotification.NOTIFICATION_GROUP;
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
