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
package org.kablink.teaming.domain;

import org.kablink.util.VibeRuntimeException;
import org.kablink.util.api.ApiErrorCode;

import javax.naming.NamingException;


/**
 * This class is used when an error happens during an ldap sync.  The m_ldapConfig property
 * holds the ldap configuration that was being used when the problem occurred.
 * 
 * @author jwootton
 *
 */
public class LdapSyncException extends VibeRuntimeException
{
	private LdapConnectionConfig	m_ldapConfig	= null;	// The ldap connection that was being used when the problem happened.
	private NamingException		m_namingEx		= null;	// The NamingException that was thrown when during the ldap sync.
	
	/**
	 * Constructor
	 */
    public LdapSyncException(
    	LdapConnectionConfig	ldapConfig,
    	NamingException			namingException )
    {
    	super(namingException.getMessage(), namingException);
    	
    	m_ldapConfig = ldapConfig;
    	m_namingEx = namingException;
    }// end LdapSyncException()

    
    /**
     * Return the LdapConnectionConfig object that was being used when the sync had a problem. 
     */
    public LdapConnectionConfig getLdapConfig()
    {
    	return m_ldapConfig;
    }// end getLdapConfig()
    
    
    /**
     * Return the NamingException that was thrown during the ldap sync.
     */
    public NamingException getNamingException()
    {
    	return m_namingEx;
    }// end getNamingException()

    @Override
    public ApiErrorCode getApiErrorCode() {
        return ApiErrorCode.LDAP_SYNC_ERROR;
    }

    @Override
    public int getHttpStatusCode() {
        return 500;
    }
}// end LdapSyncException
