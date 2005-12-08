
package com.sitescape.ef.jobs;
import org.quartz.Scheduler;
import com.sitescape.ef.domain.Workspace;
/**
 * @author Janet McCann
 *
 */
public interface LdapSynchronization {
	
    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_wsLdapSynchronization";
	public static final String LDAP_GROUP="ldap-synchronization";
	public void checkSchedule(Scheduler scheduler, Workspace workspace);
}