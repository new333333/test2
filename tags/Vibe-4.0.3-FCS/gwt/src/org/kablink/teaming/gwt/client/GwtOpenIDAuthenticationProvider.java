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
 * This class holds information about an OpenID authentication provider.
 */
public class GwtOpenIDAuthenticationProvider 
	implements IsSerializable, VibeRpcResponseData
{
	private String m_name;
	private String m_title;
	private String m_url;
	private OpenIDAuthProviderType m_type;
	
	public enum OpenIDAuthProviderType
		implements IsSerializable
	{
		GOOGLE,
		YAHOO,
		AOL,
		MYOPENID,
		VERISIGN,
		UNKNOWN
	}
	
	/**
	 * 
	 */
	public GwtOpenIDAuthenticationProvider()
	{
	}
	
	/**
	 * 
	 */
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * 
	 */
	public String getTitle()
	{
		return m_title;
	}
	
	/**
	 * 
	 */
	public OpenIDAuthProviderType getType()
	{
		return m_type;
	}
	
	/**
	 * 
	 */
	public String getUrl()
	{
		return m_url;
	}
	
	/**
	 * 
	 */
	public void setName( String name )
	{
		m_name = name;
		m_type = OpenIDAuthProviderType.UNKNOWN;

		if ( name != null )
		{
			if ( name.equalsIgnoreCase( "aol" ) )
				m_type = OpenIDAuthProviderType.AOL;
			else if ( name.equalsIgnoreCase( "google" ) )
				m_type = OpenIDAuthProviderType.GOOGLE;
			else if ( name.equalsIgnoreCase( "myopenid" ) )
				m_type = OpenIDAuthProviderType.MYOPENID;
			else if ( name.equalsIgnoreCase( "verisign" ) )
				m_type = OpenIDAuthProviderType.VERISIGN;
			else if ( name.equalsIgnoreCase( "yahoo" ) )
				m_type = OpenIDAuthProviderType.YAHOO;
		}
	}
	
	/**
	 * 
	 */
	public void setTitle( String title )
	{
		m_title = title;
	}
	
	/**
	 * 
	 */
	public void setUrl( String url )
	{
		m_url = url;
	}
}
