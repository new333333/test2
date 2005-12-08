
package com.sitescape.ef.jobs;

import org.quartz.Scheduler;
/**
 * @author Janet McCann
 *
 */
public interface EmailPosting  {
    
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_emailPostingJob";
	public static final String POSTING_GROUP="email-posting";
	public static final String POSTING_NAME = "email-posting";
	public void enable(Scheduler scheduler, Schedule schedule);
	public void disable(Scheduler scheduler);
	
}
