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

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SPropsUtil;
import org.springframework.security.core.Authentication;



/**
 * This class is used to hold the history of a user's failed authentication attempts.
 * 
 * @author jwootton@novell.com
 *
 */
public class FailedUserAuthenticationHistory
{
	private Log m_logger = LogFactory.getLog( getClass() );

	private String m_userName;	// Name of the user we are tracking
	private int m_maxHistorySize;
	private ConcurrentLinkedDeque<FailedUserAuthentication> m_listOfFailures;
	
	/**
	 * 
	 */
	private FailedUserAuthenticationHistory( String name)
	{
		m_userName = name;
		m_maxHistorySize = SPropsUtil.getInt( "failed.user.authentication.history.max.user.size", 20 );
		m_listOfFailures = new ConcurrentLinkedDeque<FailedUserAuthentication>();
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
		
		if ( m_listOfFailures.size() == m_maxHistorySize )
			m_listOfFailures.removeLast();
		
		name = authentication.getName();
		if ( name != null && name.equalsIgnoreCase( m_userName ) )
		{
			FailedUserAuthentication failedUserAuth;
			
			failedUserAuth = FailedUserAuthentication.getFailedUserAuthentication();
			failedUserAuth.setIpAddr( ipAddr );
			failedUserAuth.setName( name );
			failedUserAuth.setTime( time );
			
			m_listOfFailures.push( failedUserAuth );
			
			m_logger.debug( "Recording failed authentication for user: " + name + " from ip address: " + ipAddr );
		}
	}
	
	/**
	 * 
	 */
	public void clearHistory()
	{
		m_listOfFailures.clear();
	}
	
	/**
	 * 
	 */
	public String getName()
	{
		return m_userName;
	}
	
	/**
	 * Given the history of failed authentications for this user, do we think a brute-force attack
	 * is happening?
	 */
	public boolean isBruteForceAttackInProgress(
		int inLastNumSeconds,
		int maxNumFailedLogins )
	{
		Date now;
		int numFailures = 0;
		
		if ( m_listOfFailures == null || m_listOfFailures.size() == 0 )
			return false;

		// Get the number of failed authentications that have happened in the last x seconds
		now = new Date();
		numFailures = getNumberOfFailedAuthentications( now.getTime(), inLastNumSeconds );
		
		if ( numFailures >= maxNumFailedLogins )
		{
			m_logger.debug( "brute-force attack detected for user: " + m_userName );
			return true;
		}
		
		// If we get here we do not think a brute-force attack is happening.
		return false;
	}
	
	/**
	 * Return the number of failed authentications that have happened in the past number of seconds
	 */
	public int getNumberOfFailedAuthentications( long startTime, int inLastNumSeconds )
	{
		long past;
		int count = 0;
		Iterator<FailedUserAuthentication> iter;
		
		if ( m_listOfFailures == null || m_listOfFailures.size() == 0 )
			return 0;
		
		past = startTime - (inLastNumSeconds * 1000);

		iter = m_listOfFailures.iterator();
		while ( iter.hasNext() )
		{
			FailedUserAuthentication nextFailedAuth;
			Long authFailureTime;
			
			nextFailedAuth = iter.next();
			authFailureTime = nextFailedAuth.getTime();
			if ( authFailureTime != null )
			{
				if ( past < authFailureTime )
					++count;
			}
		}
		
		return count;
	}
	
	/**
	 * 
	 */
	public static FailedUserAuthenticationHistory getFailedUserAuthenticationHistory( String userName )
	{
		return new FailedUserAuthenticationHistory( userName );
	}
}
