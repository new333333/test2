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
			mail.sendNotifications(new Long(jobDataMap.getLong("folder")));
		} catch (NoFolderByTheIdException nf) {
			removeJobOnError(context,nf);
		} catch (ConfigurationException cf) {
			throw new JobExecutionException(cf);
		}
    }
	protected void removeJobOnError(JobExecutionContext context, Exception e) throws JobExecutionException {
		if (e instanceof NoUserByTheIdException) {
			AdminModule admin = (AdminModule)SpringContextUtil.getBean("adminModule");
			admin.disableNotification(new Long(jobDataMap.getLong("folder")));
		}
		super.removeJobOnError(context,e);	
	}
	public void checkSchedule(Scheduler scheduler, Binder folder) {
		JobDescription job = new MailJobDescription(folder);
		verifySchedule(scheduler, job);
	}
	public class MailJobDescription implements JobDescription {
		private Binder folder;
		public MailJobDescription(Binder folder) {
			this.folder = folder;
		}
		public  String getSchedule() {
			return folder.getNotificationDef().getSchedule().getQuartzSchedule();
		}
    	public  String getDescription() {
    		return SSStatefulJob.trimDescription(folder.toString());
    	}
    	public  JobDataMap getData() {
			JobDataMap data = new JobDataMap();
			data.put("folder",folder.getId());
			data.put("zoneName",folder.getZoneName());
			return data;
    	}
    	public  boolean isEnabled() {
    		return folder.getNotificationDef().isEnabled();
    	}
    	public String getName() {
    		return folder.getId().toString();
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
