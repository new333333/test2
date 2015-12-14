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

package org.kablink.teaming.module.authentication;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.kablink.teaming.util.SPropsUtil;
import org.springframework.security.core.Authentication;



/**
 * This class is used to hold the history of all users failed authentication attempts.
 * 
 * @author jwootton@novell.com
 *
 */
public class FailedAuthenticationHistory
{
	private int m_inLastNumSeconds;
	private int m_numFailedLogins;
	
	// The key is the user name
	private Hashtable<String,FailedUserAuthenticationHistory> m_listOfFailures;
	
	/**
	 * 
	 */
	private FailedAuthenticationHistory()
	{
		m_inLastNumSeconds = SPropsUtil.getInt( "brute.force.attack.in.last.num.seconds", 1800 );
		m_numFailedLogins = SPropsUtil.getInt( "brute.force.attack.num.failed.logins", 3 );
		m_listOfFailures = new Hashtable<String,FailedUserAuthenticationHistory>();
	}
	
	/**
	 * 
	 */
	public void addFailure(
		Authentication authentication,
		String ipAddr,
		long time )
	{
		String name;
		FailedUserAuthenticationHistory userHistory;
		
		name = authentication.getName();
		if ( name == null || name.length() == 0 )
			return;

		// Are we already tracking this user?
		userHistory = m_listOfFailures.get( name );
		if ( userHistory == null )
		{
			// No, create a history object for this user
			userHistory = FailedUserAuthenticationHistory.getFailedUserAuthenticationHistory( name );
			m_listOfFailures.put( name, userHistory );
		}
		
		userHistory.addFailure( authentication, ipAddr, time );
	}
	
	/**
	 * Clear the failed authentication history for the given user. 
	 */
	public void clearHistory( String userId )
	{
		FailedUserAuthenticationHistory userHistory;
		
		if ( m_listOfFailures == null || m_listOfFailures.size() == 0 )
			return;

		// Are we already tracking this user?
		userHistory = m_listOfFailures.get( userId );
		if ( userHistory != null )
			userHistory.clearHistory();
	}
	
	/**
	 * For the given user see if the user is under brute-force attack.
	 */
	public boolean isBruteForceAttackInProgress( String userId )
	{
		FailedUserAuthenticationHistory userHistory;
		
		if ( m_listOfFailures == null || m_listOfFailures.size() == 0 )
			return false;

		// Are we already tracking this user?
		userHistory = m_listOfFailures.get( userId );
		if ( userHistory != null )
			return userHistory.isBruteForceAttackInProgress( m_inLastNumSeconds, m_numFailedLogins );
		
		// If we get here we don't have any record of failed authentications for this user.
		return false;
	}
	
	/**
	 * Check all users that have a failed authentication history and see if a brute-force attack
	 * is happening for any of them.
	 */
	public boolean isBruteForceAttackInProgress()
	{
		Collection<FailedUserAuthenticationHistory> listOfFailedUserAuthentications;
		
		if ( m_listOfFailures == null || m_listOfFailures.size() == 0 )
			return false;

		try
		{
			// Go through our list of authentication failures and see if any user is under
			// a brute-force attack.
			listOfFailedUserAuthentications = m_listOfFailures.values();
			if ( listOfFailedUserAuthentications != null )
			{
				Iterator<FailedUserAuthenticationHistory> iter;
				
				iter = listOfFailedUserAuthentications.iterator();
				while ( iter.hasNext() )
				{
					FailedUserAuthenticationHistory nextUserHistory;
					
					nextUserHistory = iter.next();
					if ( nextUserHistory.isBruteForceAttackInProgress( m_inLastNumSeconds, m_numFailedLogins ) )
						return true;
				}
			}
		}
		catch ( Exception ex )
		{
		}
		
		// If we get here we could not detect a brute-force attack
		return false;
	}
	
	/**
	 * 
	 */
	public static FailedAuthenticationHistory getFailedAuthenticationHistory()
	{
		return new FailedAuthenticationHistory();
	}
}
