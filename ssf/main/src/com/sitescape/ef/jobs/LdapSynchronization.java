
package com.sitescape.ef.jobs;

/**
 * @author Janet McCann
 *
 */
public interface LdapSynchronization {
	
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_wsLdapSynchronizationJob";
	public static final String LDAP_GROUP="ldap-synchronization";

	public ScheduleInfo getScheduleInfo(String zoneName);
	public void setScheduleInfo(ScheduleInfo info);
	public void enable(boolean enable, String zoneName);
}