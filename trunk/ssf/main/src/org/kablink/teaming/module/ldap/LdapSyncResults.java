/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

package org.kablink.teaming.module.ldap;

import java.util.ArrayList;



/**
 * This class is used to keep track of the results of an ldap sync.
 * @author jwootton
 *
 */
public class LdapSyncResults
{
	public enum SyncStatus
	{
		STATUS_COLLECT_RESULTS,
		STATUS_COMPLETED,
		STATUS_STOP_COLLECTING_RESULTS,
		STATUS_ABORTED_BY_ERROR,
		STATUS_SYNC_ALREADY_IN_PROGRESS
	}
	
	// Define the maximum number of results we can collect for each area.
	private static final int MAX_RESULTS = 2000;
	
	private PartialLdapSyncResults		m_usersAddedToTeaming;		// List of users added to Teaming
	private PartialLdapSyncResults		m_usersModified;			// List of users modified in Teaming
	private PartialLdapSyncResults		m_usersDeletedFromTeaming;	// List of users deleted from Teaming
	private PartialLdapSyncResults		m_usersDisabled;			// List of users that were disabled.
	private PartialLdapSyncResults		m_groupsAddedToTeaming;		// List of groups added to Teaming
	private PartialLdapSyncResults		m_groupsModified;			// List of groups modified in Teaming
	private PartialLdapSyncResults		m_groupsDeletedFromTeaming;	// List of groups deleted from Teaming
	private String						m_id;
	private SyncStatus					m_status;
	private String						m_errorDesc;				// If an error happened, its description will be stored here.
	private String						m_errorLdapConfigId;		// The id of the ldap configuration being used when an error happened.
	

	/**
	 * Private Constructor
	 */
    public LdapSyncResults( String id )
    {
    	super();
    	
    	m_id = id;
    	m_status = SyncStatus.STATUS_COLLECT_RESULTS;
    	m_errorDesc = null;
    	m_errorLdapConfigId = null;
    	
    	m_usersAddedToTeaming = new PartialLdapSyncResults();
    	m_usersModified = new PartialLdapSyncResults();
    	m_usersDeletedFromTeaming = new PartialLdapSyncResults();
    	m_usersDisabled = new PartialLdapSyncResults();
    	m_groupsAddedToTeaming = new PartialLdapSyncResults();
    	m_groupsModified = new PartialLdapSyncResults();
    	m_groupsDeletedFromTeaming = new PartialLdapSyncResults();
    }// end LdapSyncResults()
    
    
    /**
     * Clear all sync results we have collected so far.
     */
    public void clearResults()
    {
    	m_usersAddedToTeaming.clearResults();
    	m_usersModified.clearResults();
    	m_usersDeletedFromTeaming.clearResults();
    	m_usersDisabled.clearResults();
    	m_groupsAddedToTeaming.clearResults();
    	m_groupsModified.clearResults();
    	m_groupsDeletedFromTeaming.clearResults();
    }// end clearResults()
    
    
    /**
     * Calling this method will stop all further collection of sync results.
     */
    public void completed()
    {
    	m_status = SyncStatus.STATUS_COMPLETED;
    	
    }// end completed()
    
    
    /**
     * This method is used to record that an error happened during the sync.
     */
    public void error( String errorDesc, String ldapConfigId )
    {
    	m_status = SyncStatus.STATUS_ABORTED_BY_ERROR;
    	m_errorDesc = errorDesc;
    	m_errorLdapConfigId = ldapConfigId;
    }// end error()
    
    
    /**
     * Return the object that holds the list of users that have been added to Teaming. 
     */
    public PartialLdapSyncResults getAddedUsers()
    {
    	return m_usersAddedToTeaming;
    }// end getAddedUsers()


    /**
     * Return the object that holds the list of groups that have been added to Teaming. 
     */
    public PartialLdapSyncResults getAddedGroups()
    {
    	return m_groupsAddedToTeaming;
    }// end getAddedGroups()


    /**
     * Return the object that holds the list of users that have been deleted from Teaming. 
     */
    public PartialLdapSyncResults getDeletedUsers()
    {
    	return m_usersDeletedFromTeaming;
    }// end getDeletedUsers()

    
    /**
     * Return the object that holds the list of groups that have been deleted from Teaming. 
     */
    public PartialLdapSyncResults getDeletedGroups()
    {
    	return m_groupsDeletedFromTeaming;
    }// end getDeletedGroups()

    
    /**
     * Return the object that holds the list of users that have been disabled. 
     */
    public PartialLdapSyncResults getDisabledUsers()
    {
    	return m_usersDisabled;
    }

    
    /**
     * Return the description of the error that happened.
     */
    public String getErrorDesc()
    {
    	return m_errorDesc;
    }// end getErrorDesc()
    
    
    /**
     * Return the id of the ldap configuration being used when an error happened.
     */
    public String getErrorLdapConfigId()
    {
    	return m_errorLdapConfigId;
    }// end getErrorLdapConfigId()
    
    
    /**
     * Return the id of the object.
     */
    public String getId()
    {
    	return m_id;
    }// end getId()
    
    
    /**
     * Return the object that holds the list of users that have been modified in Teaming. 
     */
    public PartialLdapSyncResults getModifiedUsers()
    {
    	return m_usersModified;
    }// end getModifiedUsers()
    

    /**
     * Return the object that holds the list of groups that have been modified in Teaming. 
     */
    public PartialLdapSyncResults getModifiedGroups()
    {
    	return m_groupsModified;
    }// end getModifiedGroups()
    
    
    /**
     * Return the status.
     */
    public SyncStatus getStatus()
    {
    	return m_status;
    }// end getStatus()
    
    
    /**
     * 
     */
    public void setStatus( SyncStatus status )
    {
    	m_status = status;
    }

    /**
     * Calling this method will stop all further collection of sync results.
     */
    public void stopCollectingResults()
    {
    	m_status = SyncStatus.STATUS_STOP_COLLECTING_RESULTS;
    	
    	// Clear all sync results we have collected so far.
    	clearResults();
    }// end abort()
    
    
    
    /**
     * This class is used to collect partial sync results.
     */
    public class PartialLdapSyncResults
    {
    	private ArrayList<String>	m_results;
    	
    	/**
    	 * Constructor
    	 */
    	public PartialLdapSyncResults()
    	{
    		m_results = new ArrayList<String>();
    	}// end PartialLdapSyncResults()
    	
    	
    	/**
    	 * Add a result to our list.
    	 */
    	public boolean addResult( String value )
    	{
    		// Should we collect this result?
    		if ( getStatus() == SyncStatus.STATUS_COLLECT_RESULTS )
    		{
    			// Yes
    			// Have we reached the max results we want to store?
    			if ( m_results.size() < MAX_RESULTS )
    			{
    				// No
        			m_results.add( value );
        			return true;
    			}
    		}
    		
    		return false;
    	}// end addResult()
    	
    	
    	/**
    	 * Clear any results we may have collected so far.
    	 */
    	public void clearResults()
    	{
    		m_results.clear();
    	}// end clearResults()
    	
    	
    	/**
    	 * Return the list of names we have collected so far.
    	 */
    	public ArrayList<String> getResults()
    	{
    		return new ArrayList<String>( m_results );
    	}// end getResults()
    }// end PartialLdapSyncResults
}// end LdapSyncResults
