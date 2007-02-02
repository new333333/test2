
package com.sitescape.team.jobs;
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
	public void enable(boolean enable, Long zoneId);
	public ScheduleInfo getScheduleInfo(Long zoneId);
	public void setScheduleInfo(ScheduleInfo schedulerInfo);
	
}
