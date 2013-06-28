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

import javax.naming.NamingException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;

import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LdapSyncException;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.module.ldap.LdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncResults.SyncStatus;
import org.kablink.teaming.web.util.WebHelper;

/**
 * This class is used to start a thread which will perform an ldap sync.
 * @author jwootton
 *
 */
public class LdapSyncThread
	extends Thread
{
	private LdapSyncResults	m_ldapSyncResults;	// The results of the sync will be stored here as the sync progresses.
	private PortletSession		m_session = null;	// The session we stored this object in.
	private String				m_id;
	private LdapModule			m_ldapModule;
	private boolean			m_syncUsersAndGroups;
	private String[]		m_listOfLdapConfigsToSyncGuid;
	
	/**
	 * Create an LdapSyncThread object.
	 */
	public static LdapSyncThread createLdapSyncThread(
		RenderRequest	request,
		String			id,				// Create the thread using this id
		LdapModule		ldapModule,
		boolean			syncUsersAndGroups,
		String[]		listOfLdapConfigsToSyncGuid )
	{
		LdapSyncThread	ldapSyncThread;
		PortletSession 	session;
		
		session = WebHelper.getRequiredPortletSession( request );
		
		if( session == null )
			return null; // unable to allocate a new LdapSyncThread object.
	
		ldapSyncThread = new LdapSyncThread( session, id, ldapModule, syncUsersAndGroups, listOfLdapConfigsToSyncGuid );
		
		// Set the priority of the thread to be the lowest.
		ldapSyncThread.setPriority( Thread.MIN_PRIORITY );
		
		// Store this LdapSyncThread object in the session object.
		session.setAttribute( id, ldapSyncThread, PortletSession.APPLICATION_SCOPE );
	
		return ldapSyncThread;
	}// end createLdapSyncThread()
	
	
	/**
	 * Return the LdapSyncThread object with the given id.
	 */
	public static LdapSyncThread getLdapSyncThread(
		PortletRequest	request,
		String			id )
	{
		LdapSyncThread	ldapSyncThread	= null;
		PortletSession	session;

		if ( id == null )
			return null;
		
		session = request.getPortletSession( false );
		if ( session == null )
			return null;

		// Get the LdapSyncThread object that was stored on the session.
		ldapSyncThread = (LdapSyncThread) session.getAttribute( id, PortletSession.APPLICATION_SCOPE );
		
		return ldapSyncThread;

	}// end getLdapSyncThread()
	
	
	/**
	 * Return the LdapSyncResults object for the given LdapSyncThread id.
	 */
	public static LdapSyncResults getLdapSyncResults(
		PortletRequest	request,
		String			id )
	{
		LdapSyncThread	ldapSyncThread;
		LdapSyncResults	ldapSyncResults	= null;
		
		// Get the LdapSyncThread object for the given id.
		ldapSyncThread = getLdapSyncThread( request, id );
		
		if ( ldapSyncThread != null )
			ldapSyncResults = ldapSyncThread.getLdapSyncResults();
		
		return ldapSyncResults;
	}// end getLdapSyncResults()	
	
	
    /**
	 * Class constructor. (1 of 1)
	 */
	private LdapSyncThread(
		PortletSession	session,
		String			id,
		LdapModule		ldapModule,
		boolean			syncUsersAndGroups,
		String[]		listOfLdapConfigsToSyncGuid )
	{
		// Initialize this object's super class.
		super( id );
	
		m_id = id;
		m_session = session;
		m_ldapModule = ldapModule;
		m_syncUsersAndGroups = syncUsersAndGroups;
		m_listOfLdapConfigsToSyncGuid = listOfLdapConfigsToSyncGuid;
		
		// Create an LdapSyncResults object to hold the results of the sync.
		m_ldapSyncResults = new LdapSyncResults( id );
	}// end LdapSyncThread()
	
	
	/**
	 * Execute the code that will perform the ldap sync.
	 */
	public void doLdapSync()
	{
		boolean 		enabled;
		LdapSyncResults	syncResults;
		LdapSchedule 	schedule;
		
		// Get the LdapSyncResults object to store the results of the sync.
		syncResults = getLdapSyncResults();

		// Get the current ldap sync schedule
		schedule = m_ldapModule.getLdapSchedule();
		enabled = schedule.getScheduleInfo().isEnabled();
		
		// Disable the schedule
		schedule.getScheduleInfo().setEnabled(false);
		m_ldapModule.setLdapSchedule( schedule );
		try
		{
			// Perform the sync.
			m_ldapModule.syncAll( m_syncUsersAndGroups, m_listOfLdapConfigsToSyncGuid, syncResults );
			
			// Did syncAll() return because a sync was already in progress?
			if ( syncResults.getStatus() != SyncStatus.STATUS_SYNC_ALREADY_IN_PROGRESS )
			{
				// No
				// Signal that the sync has completed normally.
				syncResults.completed();
			}
		}
		catch (LdapSyncException ldapSyncEx)
		{
			LdapConnectionConfig	ldapConfig;
			NamingException			ne;
			String					errorDesc;
			
			// Get the ldap connection configuration that had the problem.
			ldapConfig = ldapSyncEx.getLdapConfig();

			ne = ldapSyncEx.getNamingException();
			if (ne.getCause() != null)
				errorDesc = ne.getCause().getLocalizedMessage() != null ? ne.getCause().getLocalizedMessage() : ne.getCause().getMessage();
			else
				errorDesc = ne.getLocalizedMessage() != null ? ne.getLocalizedMessage() : ne.getMessage();
			
			syncResults.error( errorDesc, ldapConfig.getId() );
		}
		finally
		{
			// If the schedule was originally enabled, set it back to enabled because we disabled it.
			if ( enabled )
			{
				schedule.getScheduleInfo().setEnabled( enabled );
				m_ldapModule.setLdapSchedule( schedule );
			}
		}
	}// end doLdapSync()
	
	
	/**
	 * Return the LdapSyncResults object associated with this thread.
	 */
	public LdapSyncResults getLdapSyncResults()
	{
		return m_ldapSyncResults;
	}// end getLdapSyncResults()


    /**
     * Remove this LdapSyncThread object from the session.
     */
    public void removeFromSession()
    {
    	// Remove this object from the session object.
		if ( m_session != null )
    		m_session.removeAttribute( m_id );

    }// end removeFromSession()
    
    
    /**
     * Implement the Thread::run method.  Do the work of the ldap sync.
     */
    @Override
	public void run()
    {
    	// Run the ldap sync.
    	doLdapSync();
    }// end run()

}// end LdapSyncThread
