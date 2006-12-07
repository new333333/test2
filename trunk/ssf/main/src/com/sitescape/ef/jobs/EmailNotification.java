
package com.sitescape.ef.jobs;

import com.sitescape.ef.domain.Binder;
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

	public ScheduleInfo getScheduleInfo(String zoneName, Binder binder);
	public void setScheduleInfo(ScheduleInfo info, String zoneName, Binder binder);
	public void enable(boolean enable, String zoneName, Binder binder);
}
