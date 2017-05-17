/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;



import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtRole.GwtRoleType;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used in GWT RPC calls to hold zone share rights.
 * 
 * @author jwootton@novell.com
 */
public class ZoneShareRights 
	implements IsSerializable, VibeRpcResponseData
{
	private boolean m_allowShareWithLdapGroups;
	private ArrayList<GwtRole> m_roles;
	
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ZoneShareRights()
	{
		// Nothing to do.
	}	
	
	/**
	 * See if the given principal id has the right to reshare  
	 */
	public boolean canReshare( Long principalId )
	{
		return hasRights( GwtRoleType.EnableShareForward, principalId );
	}
	
	/**
	 * See if the given principal id has the right to share with external users.
	 */
	public boolean canShareExternal( Long principalId )
	{
		return hasRights( GwtRoleType.EnableShareExternal, principalId );
	}
	
	/**
	 * See if the given principal id has the right to share with internal users.
	 */
	public boolean canShareInternal( Long principalId )
	{
		return hasRights( GwtRoleType.EnableShareInternal, principalId );
	}
	
	/**
	 * See if the given principal id has the right to share with the public
	 */
	public boolean canSharePublic( Long principalId )
	{
		return hasRights( GwtRoleType.EnableSharePublic, principalId );
	}
	
	/**
	 * 
	 */
	public boolean getAllowShareWithLdapGroups()
	{
		return m_allowShareWithLdapGroups;
	}
	
	/**
	 * 
	 */
	public ArrayList<GwtRole> getRoles()
	{
		return m_roles;
	}
	
	/**
	 * See if the given principal id has the given right
	 */
	private boolean hasRights( GwtRoleType roleType, Long principalId )
	{
		if ( m_roles != null && roleType != null && principalId != null )
		{
			for ( GwtRole nextRole : m_roles )
			{
				if ( nextRole.getType() == roleType )
				{
					ArrayList<Long> listOfMemberIds;
					
					listOfMemberIds = nextRole.getMemberIds();
					if ( listOfMemberIds != null )
					{
						for ( Long nextMemberId : listOfMemberIds )
						{
							if ( nextMemberId.equals( principalId ) )
								return true;
						}
					}
				}
			}
		}
		
		return false;
	}

	/**
	 * 
	 */
	public void setAllowShareWithLdapGroups( boolean allow )
	{
		m_allowShareWithLdapGroups = allow;
	}
	
	/**
	 * Set the roles
	 */
	public void setRoles( ArrayList<GwtRole> roles )
	{
		m_roles = roles;
	}
}
