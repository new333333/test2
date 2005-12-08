
package com.sitescape.ef.jobs;

import com.sitescape.ef.domain.Binder;
import org.quartz.Scheduler;
/**
 * @author Janet McCann
 *
 */
public interface FolderEmailNotification  {
    
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_emailNotificationJob";
	public static final String NOTIFICATION_GROUP="email-notifications";

    public void checkSchedule(Scheduler scheduler, Binder forum);

}
