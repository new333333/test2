package com.sitescape.ef.jobs;

import java.util.TimeZone;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.mail.MailModule;
import com.sitescape.ef.util.SpringContextUtil;

public class DefaultEmailPosting extends SSStatefulJob implements EmailPosting {
	public void doExecute(final JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		ScheduleInfo config = new ScheduleInfo((Map)(context.getJobDetail().getJobDataMap()));
		mail.receivePostings(config);
    }
	public ScheduleInfo getScheduleInfo(String zoneName) {
		return getScheduleInfo(new MailJobDescription(zoneName));
	}
	public void setScheduleInfo(ScheduleInfo info) {
		setScheduleInfo(new MailJobDescription(info.getZoneName()), info);

	}

	public void enable(boolean enable, String zoneName) {
		enable(enable, new MailJobDescription(zoneName));
 	}

	public class MailJobDescription implements JobDescription {
		private String zoneName;
		public MailJobDescription(String zoneName) {
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
    		return POSTING_GROUP;
    	}		
    	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = new ScheduleInfo(zoneName);
    		return info;
    	}
      	public TimeZone getTimeZone() {
    		return getDefaultTimeZone();
   	}
    	public String getCleanupListener() {
    		return getDefaultCleanupListener();
    	}
   	}

}
