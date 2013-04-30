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
package org.kablink.teaming.gwt.client;


import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to hold the criteria used to define a group's dynamic membership
 * @author jwootton
 *
 */
public class GwtDynamicGroupMembershipCriteria
	implements IsSerializable, VibeRpcResponseData
{
	private String m_baseDn;
	private String m_ldapFilter;
	private boolean m_searchSubtree;
	private boolean m_updateDuringLdapSync;
	
	/**
	 * 
	 */
	public GwtDynamicGroupMembershipCriteria()
	{
		m_baseDn = null;
		m_ldapFilter = null;
		m_searchSubtree = false;
		m_updateDuringLdapSync = false;
	}
	
	/**
	 * Return the dynamic group criteria as an xml string.
	 * The xml will look like the following:
	 * 	<searches updateMembershipDuringLdapSync="true|false">
	 * 		<search searchSubtree="true|false">
	 * 			<baseDn><![CDATA[some dn]]></baseDn>
	 * 			<filter><![CDATA[some filter]]></filter>
	 * 		</search>
	 *	</searches>
	 */
	public String getAsXml()
	{
		StringBuffer xml;
		
		xml = new StringBuffer();
		
		// Add the <searches updateMembershipDuringLdapSync="true|false"> element
		{
			xml.append( "<searches updateMembershipDuringLdapSync=\"" );
			
			// Add whether to update the group membership during ldap sync
			xml.append( Boolean.toString( m_updateDuringLdapSync ) );
			xml.append( "\"");
			
			// Close the element.
			xml.append( " >" );
		}
		
		// Add the <search searchSubtree="true|false"> element.
		{
			xml.append( "<search searchSubtree=\"" );
			
			// Add whether to search the subtree.
			xml.append( Boolean.toString( m_searchSubtree ) );
			xml.append( "\"");
			
			// Close the element
			xml.append( " >" );
		}
		
		// Add the <baseDn><![CDATA[some dn]]></baseDn> element.
		if ( m_baseDn != null && m_baseDn.length() > 0 )
		{
			xml.append( "<baseDn><![CDATA[" );
			xml.append( m_baseDn );
			xml.append( "]]></baseDn>" );
		}
		
		// Add the <filter><![CDATA[some filter]]></filter> element.
		if ( m_ldapFilter != null && m_ldapFilter.length() > 0 )
		{
			xml.append( "<filter><![CDATA[" );
			xml.append( m_ldapFilter );
			xml.append( "]]></filter>" );
		}
		
		// Add the </search> tag
		xml.append( "</search>" );
		
		// Add the </searches> tag
		xml.append( "</searches>" );
		
		return xml.toString();
	}
	
	/**
	 * 
	 */
	public String getBaseDn()
	{
		return m_baseDn;
	}
	
	/**
	 * 
	 */
	public String getLdapFilter()
	{
		return m_ldapFilter;
	}
	
	/**
	 * 
	 */
	public String getLdapFilterWithoutCRLF()
	{
		String filter;
		
		filter = null;
		if ( m_ldapFilter != null && m_ldapFilter.length() > 0 )
		{
			filter = m_ldapFilter.replaceAll( "\r", "" );
			filter = filter.replaceAll( "\n", "" );
		}
		
		return filter;
	}
	
	/**
	 * 
	 */
	public boolean getSearchSubtree()
	{
		return m_searchSubtree;
	}
	
	/**
	 * 
	 */
	public boolean getUpdateDuringLdapSync()
	{
		return m_updateDuringLdapSync;
	}
	
	/**
	 * 
	 */
	public void setBaseDn( String baseDn )
	{
		m_baseDn = baseDn;
	}
	
	/**
	 * 
	 */
	public void setLdapFilter( String ldapFilter )
	{
		m_ldapFilter = ldapFilter;
	}
	
	/**
	 * 
	 */
	public void setSearchSubtree( boolean searchSubtree )
	{
		m_searchSubtree = searchSubtree;
	}
	
	/**
	 * 
	 */
	public void setUpdateDuringLdapSync( boolean update )
	{
		m_updateDuringLdapSync = update;
	}
}
