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
package org.kablink.teaming.gwt.client.rpc.shared;



import java.util.HashMap;

import org.kablink.teaming.gwt.client.GwtUser;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateEmailRpcResponseData.EmailAddressStatus;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the "find user by email address" rpc command
 * @author jwootton
 *
 */
public class FindUserByEmailAddressRpcResponseData
	implements IsSerializable, VibeRpcResponseData
{
	private HashMap<String,GwtUser> m_listOfUsers = null;
	private HashMap<String,EmailAddressStatus> m_listOfEmailAddrStatus = null;
	
	/**
	 * 
	 */
	public FindUserByEmailAddressRpcResponseData()
	{
	}

	/**
	 * 
	 */
	public void addEmailStatus( String emailAddr, EmailAddressStatus emailAddrStatus )
	{
		if ( emailAddr == null || emailAddrStatus == null )
			return;
		
		if ( m_listOfEmailAddrStatus == null )
			m_listOfEmailAddrStatus = new HashMap<String,EmailAddressStatus>();
		
		m_listOfEmailAddrStatus.put( emailAddr, emailAddrStatus );
	}
	
	/**
	 * 
	 */
	public void addUser( String emailAddr, GwtUser gwtUser )
	{
		if ( emailAddr == null || gwtUser == null )
			return;
		
		if ( m_listOfUsers == null )
			m_listOfUsers = new HashMap<String,GwtUser>();
		
		m_listOfUsers.put( emailAddr, gwtUser );
	}
	
	/**
	 * 
	 */
	public HashMap<String,EmailAddressStatus> getEmailAddrMap()
	{
		return m_listOfEmailAddrStatus;
	}
	
	/**
	 * 
	 */
	public HashMap<String,GwtUser> getListOfUsers()
	{
		return m_listOfUsers;
	}
}
