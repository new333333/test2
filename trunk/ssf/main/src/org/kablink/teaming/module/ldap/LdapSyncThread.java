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

import java.net.UnknownHostException;

import javax.naming.CommunicationException;
import javax.naming.NamingException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.LdapConnectionConfig;
import org.kablink.teaming.domain.LdapSyncException;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.ldap.LdapModule.LdapSyncMode;
import org.kablink.teaming.module.ldap.LdapSchedule;
import org.kablink.teaming.module.ldap.LdapSyncResults;
import org.kablink.teaming.module.ldap.LdapSyncResults.SyncStatus;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.WebHelper;

/**
 * This class is used to start a thread which will perform an ldap sync.
 * @author jwootton
 *
 */
public class LdapSyncThread
	extends Thread
{
	private static Log m_logger = LogFactory.getLog( LdapSyncThread.class );

	private LdapSyncResults	m_ldapSyncResults;	// The results of the sync will be stored here as the sync progresses.
	private HttpSession		m_session = null;	// The session we stored this object in.
	private String				m_id;
	private LdapModule			m_ldapModule;
	private boolean			m_syncUsersAndGroups;
	private String[]		m_listOfLdapConfigsToSyncGuid;
	private LdapSyncMode	m_syncMode;

	/**
	 * 
	 */
	private static LdapSyncThread createLdapSyncThread(
		HttpSession session,
		String id,
		LdapModule ldapModule,
		boolean syncUsersAndGroups,
		String[] listOfLdapConfigsToSyncGuid,
		LdapSyncMode syncMode )
	{
		LdapSyncThread ldapSyncThread;
		
		if( session == null )
			return null; // unable to allocate a new LdapSyncThread object.

		ldapSyncThread = new LdapSyncThread(
										session,
										id,
										ldapModule,
										syncUsersAndGroups,
										listOfLdapConfigsToSyncGuid,
										syncMode );
		
		// Set the priority of the thread to be the lowest.
		ldapSyncThread.setPriority( Thread.MIN_PRIORITY );
		
		// Store this LdapSyncThread object in the session object.
		session.setAttribute( id, ldapSyncThread );
		
		return ldapSyncThread;
	}
	
	/**
	 * Create an LdapSyncThread object.
	 */
	public static LdapSyncThread createLdapSyncThread(
		RenderRequest	request,
		String			id,				// Create the thread using this id
		LdapModule		ldapModule,
		boolean			syncUsersAndGroups,
		String[]		listOfLdapConfigsToSyncGuid,
		LdapSyncMode	syncMode )
	{
		LdapSyncThread	ldapSyncThread;
		HttpSession 	session;
		
		session = WebHelper.getRequiredSession( WebHelper.getHttpServletRequest( request ) );

		ldapSyncThread = createLdapSyncThread(
											session,
											id,
											ldapModule,
											syncUsersAndGroups,
											listOfLdapConfigsToSyncGuid,
											syncMode );
	
		return ldapSyncThread;
	}// end createLdapSyncThread()
	
	
	/**
	 * Create an LdapSyncThread object.
	 */
	public static LdapSyncThread createLdapSyncThread(
		HttpServletRequest	request,
		String				id,				// Create the thread using this id
		LdapModule			ldapModule,
		boolean				syncUsersAndGroups,
		String[]			listOfLdapConfigsToSyncGuid,
		LdapSyncMode		syncMode )
	{
		LdapSyncThread	ldapSyncThread;
		HttpSession 	session;
		
		session = WebHelper.getRequiredSession( request );
		
		ldapSyncThread = createLdapSyncThread(
											session,
											id,
											ldapModule,
											syncUsersAndGroups,
											listOfLdapConfigsToSyncGuid,
											syncMode );
	
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
		HttpServletRequest servletRequest;

		if ( id == null )
			return null;
		
		servletRequest = WebHelper.getHttpServletRequest( request );
		if ( servletRequest == null )
			return null;
	
		ldapSyncThread = getLdapSyncThread( servletRequest, id );
		
		return ldapSyncThread;

	}// end getLdapSyncThread()
	
	
	/**
	 * Return the LdapSyncThread object with the given id.
	 */
	public static LdapSyncThread getLdapSyncThread(
		HttpServletRequest	servletRequest,
		String			id )
	{
		LdapSyncThread	ldapSyncThread	= null;
		HttpSession	session;

		if ( id == null )
		{
			m_logger.info( "in LdapSyncThread.getLdapSyncThread(), id is null" );
			return null;
		}
		
		if ( servletRequest == null )
		{
			m_logger.info( "in LdapSyncThread.getLdapSyncThread(), servletRequest is null" );
			return null;
		}
		
		session = servletRequest.getSession( false );
		if ( session == null )
		{
			m_logger.info( "in LdapSyncThread.getLdapSyncThread(), session is null" );
			return null;
		}

		// Get the LdapSyncThread object that was stored on the session.
		ldapSyncThread = (LdapSyncThread) session.getAttribute( id );
		
		return ldapSyncThread;
	}
	
	
	/**
	 * Return the LdapSyncResults object for the given LdapSyncThread id.
	 */
	public static LdapSyncResults getLdapSyncResults(
		PortletRequest	request,
		String			id )
	{
		LdapSyncResults	ldapSyncResults	= null;
		HttpServletRequest servletRequest;
		
		servletRequest = WebHelper.getHttpServletRequest( request );
		if ( servletRequest == null )
			return null;
		
		ldapSyncResults = getLdapSyncResults( servletRequest, id );
		
		return ldapSyncResults;
	}// end getLdapSyncResults()	
	
	
	/**
	 * Return the LdapSyncResults object for the given LdapSyncThread id.
	 */
	public static LdapSyncResults getLdapSyncResults(
		HttpServletRequest	request,
		String			id )
	{
		LdapSyncThread	ldapSyncThread;
		LdapSyncResults	ldapSyncResults	= null;
		
		// Get the LdapSyncThread object for the given id.
		ldapSyncThread = getLdapSyncThread( request, id );
		
		if ( ldapSyncThread != null )
			ldapSyncResults = ldapSyncThread.getLdapSyncResults();
		else
			m_logger.info( "-------> in LdapSyncThread.getLdapSyncResults(), getLdapSyncThread() returned null" );
		
		return ldapSyncResults;
	}	
	
	
    /**
	 * Class constructor. (1 of 1)
	 */
	private LdapSyncThread(
		HttpSession		session,
		String			id,
		LdapModule		ldapModule,
		boolean			syncUsersAndGroups,
		String[]		listOfLdapConfigsToSyncGuid,
		LdapSyncMode	syncMode )
	{
		// Initialize this object's super class.
		super( id );
	
		m_id = id;
		m_session = session;
		m_ldapModule = ldapModule;
		m_syncUsersAndGroups = syncUsersAndGroups;
		m_listOfLdapConfigsToSyncGuid = listOfLdapConfigsToSyncGuid;
		m_syncMode = syncMode;
		
		// Create an LdapSyncResults object to hold the results of the sync.
		m_ldapSyncResults = new LdapSyncResults( id );
	}// end LdapSyncThread()
	
	
	/**
	 * Execute the code that will perform the ldap sync.
	 */
	public LdapSyncResults doLdapSync()
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
			m_ldapModule.syncAll( m_syncUsersAndGroups, m_listOfLdapConfigsToSyncGuid, m_syncMode, syncResults );
			
			// Did syncAll() return because a sync was already in progress?
			if ( syncResults.getStatus() != SyncStatus.STATUS_SYNC_ALREADY_IN_PROGRESS )
			{
				// No
				// Signal that the sync has completed normally.
				syncResults.completed();
			}
		}
		catch ( Exception ex )
		{
			String errorDesc = null;
			String ldapConfigId = null;

			if ( ex instanceof LdapSyncException )
			{
				LdapConnectionConfig	ldapConfig;
				NamingException			ne;
				LdapSyncException ldapSyncEx;
				
				ldapSyncEx = (LdapSyncException) ex;
				
				// Get the ldap connection configuration that had the problem.
				ldapConfig = ldapSyncEx.getLdapConfig();
				if ( ldapConfig != null )
					ldapConfigId = ldapConfig.getId();

				ne = ldapSyncEx.getNamingException();
				if ( ne instanceof CommunicationException )
				{
					if ( ne.getCause() != null && ne.getCause() instanceof UnknownHostException )
					{
						UnknownHostException uhEx;
						
						uhEx = (UnknownHostException) ne.getCause();
						errorDesc = NLT.get( "errorcode.ldap.unknown.host", new Object[] { uhEx.getMessage() } );
					}
				}
				
				if ( errorDesc == null )
				{
					if (ne.getCause() != null)
						errorDesc = ne.getCause().getLocalizedMessage() != null ? ne.getCause().getLocalizedMessage() : ne.getCause().getMessage();
					else
						errorDesc = ne.getLocalizedMessage() != null ? ne.getLocalizedMessage() : ne.getMessage();
				}
			}
			else
			{
				if ( ex.getCause() != null )
				{
					if ( ex.getCause().getLocalizedMessage() != null )
						errorDesc = ex.getCause().getLocalizedMessage();
					else
						errorDesc = ex.getCause().getMessage(); 
				}
				else
				{
					if ( ex.getLocalizedMessage() != null )
						errorDesc = ex.getLocalizedMessage();
					else
						errorDesc = ex.getMessage();
				}
			}

			syncResults.error( errorDesc, ldapConfigId );
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
		
		return syncResults;
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
