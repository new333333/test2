
package com.sitescape.ef.jobs;

import com.sitescape.team.domain.Binder;
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
