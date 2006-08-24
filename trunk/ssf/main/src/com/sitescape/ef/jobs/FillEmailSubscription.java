package com.sitescape.ef.jobs;
import java.util.Date;
public interface FillEmailSubscription {
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_fillEmailSubscriptionJob";
	public static final String ENTRY_SUBSCRIPTION_GROUP="fill-entry-subscription";
	
    public void schedule(Long folderId, Long entryId, Date changeDate);

}
