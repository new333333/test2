
package com.sitescape.ef.jobs;
import java.text.ParseException;
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
	public void enable(boolean enable);
	public ScheduleInfo getScheduleInfo();
	public void setScheduleInfo(ScheduleInfo schedulerInfo) throws ParseException;
	
}
