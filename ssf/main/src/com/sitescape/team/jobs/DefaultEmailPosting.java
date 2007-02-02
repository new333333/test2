package com.sitescape.team.jobs;

import java.util.TimeZone;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.team.mail.MailManager;
import com.sitescape.team.util.SpringContextUtil;

public class DefaultEmailPosting extends SSStatefulJob implements EmailPosting {
	public void doExecute(final JobExecutionContext context) throws JobExecutionException {
    	MailManager mail = (MailManager)SpringContextUtil.getBean("mailManager");
		mail.receivePostings();
    }
	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new MailJobDescription(zoneId));
	}
	public void setScheduleInfo(ScheduleInfo info) {
		setScheduleInfo(new MailJobDescription(info.getZoneId()), info);

	}

	public void enable(boolean enable, Long zoneId) {
		enable(enable, new MailJobDescription(zoneId));
 	}

	public class MailJobDescription implements JobDescription {
		private Long zoneId;
		public MailJobDescription(Long zoneId) {
			this.zoneId = zoneId;
		}
    	public  String getDescription() {
    		return SSStatefulJob.trimDescription(zoneId.toString());
    	}
    	public Long getZoneId() {
    		return zoneId;
    	}
    	public String getName() {
    		return zoneId.toString();
    	}
    	public String getGroup() {
    		return POSTING_GROUP;
    	}		
    	public ScheduleInfo getDefaultScheduleInfo() {
    		ScheduleInfo info = new ScheduleInfo(zoneId);
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
