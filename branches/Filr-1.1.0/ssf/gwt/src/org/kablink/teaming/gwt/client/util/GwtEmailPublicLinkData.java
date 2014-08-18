/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represents the data needed to email a public link
 */
public class GwtEmailPublicLinkData
	implements IsSerializable
{
	private List<EntityId> m_listOfEntityIds;
	private ArrayList<String> m_listOfEmailAddresses;
	private String m_message;
	private ShareExpirationValue m_expirationValue;
	
	/**
	 * 
	 */
	public GwtEmailPublicLinkData()
	{
		m_listOfEmailAddresses = null;
		m_listOfEntityIds = null;
		m_message = null;
		m_expirationValue = null;
	}
	
	/**
	 * Return the expiration value
	 */
	public ShareExpirationValue getExpirationValue()
	{
		return m_expirationValue;
	}
	
	/**
	 * Return the list of email addresses
	 */
	public ArrayList<String> getListOfEmailAddresses()
	{
		return m_listOfEmailAddresses;
	}
	
	/**
	 * Return the list of entities we are sharing
	 */
	public List<EntityId> getListOfEntities()
	{
		return m_listOfEntityIds;
	}
	
	/**
	 * 
	 */
	public String getMessage()
	{
		return m_message;
	}
	
	/**
	 * 
	 */
	public void setExpirationValue( ShareExpirationValue value )
	{
		m_expirationValue = value;
	}
	
	/**
	 * 
	 */
	public void setListOfEmailAddresses( ArrayList<String> listOfEmailAddresses )
	{
		m_listOfEmailAddresses = listOfEmailAddresses;
	}
	
	/**
	 * 
	 */
	public void setListOfEntityIds( List<EntityId> listOfEntityIds )
	{
		m_listOfEntityIds = listOfEntityIds;
	}
	
	/**
	 * 
	 */
	public void setMessage( String message )
	{
		m_message = message;
	}
}

