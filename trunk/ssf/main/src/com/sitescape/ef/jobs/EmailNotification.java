
package com.sitescape.ef.jobs;

import java.text.ParseException;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.jobs.DefaultEmailNotification.MailJobDescription;

import org.quartz.Scheduler;
/**
 * @author Janet McCann
 *
 */
public interface EmailNotification  {
    
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_emailNotificationJob";
	public static final String NOTIFICATION_GROUP="email-notifications";

	public ScheduleInfo getScheduleInfo(Binder binder);
	public void setScheduleInfo(ScheduleInfo info, Binder binder);
	public void enable(boolean enable, Binder binder);
}
