
package com.sitescape.team.jobs;

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

	public ScheduleInfo getScheduleInfo(Long zoneId);
	public void setScheduleInfo(ScheduleInfo info);
	public void enable(boolean enable, Long zoneId);
}