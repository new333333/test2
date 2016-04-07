/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to track rights on a per user basis. 
 * 
 * @author jwootton@novell.com
 */
public class PerUserRightsInfo
	implements IsSerializable
{
	private boolean						m_canAccess;		//
	private PerEntityShareRightsInfo	m_shareRightsInfo;	//

	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public PerUserRightsInfo()
	{
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method. 
	 */
	public PerUserRightsInfo(
		PerEntityShareRightsInfo shareRightsInfo,
		boolean canAccess )
	{
		this();

		m_shareRightsInfo = shareRightsInfo;
		setCanAccess( canAccess );
	}

	/**
	 * 
	 */
	public boolean canAccess()
	{
		return m_canAccess;
	}
	
	/**
	 * 
	 */
	public boolean canReshare()
	{
		if ( m_shareRightsInfo != null )
			return m_shareRightsInfo.isAllowForwarding();

		return false;
	}
	
	/**
	 * 
	 */
	public boolean canShareExternal()
	{
		if ( m_shareRightsInfo != null )
			return m_shareRightsInfo.isAllowExternal();

		return false;
	}
	
	public boolean canShareFolderExternal(){
		if( m_shareRightsInfo != null )
			return m_shareRightsInfo.isAllowFolderExternal();
		return false;
	}
	
	/**
	 * 
	 */
	public boolean canShareInternal()
	{
		if ( m_shareRightsInfo != null )
			return m_shareRightsInfo.isAllowInternal();

		return false;
	}
	
	public boolean canShareFolderInternal(){
		if ( m_shareRightsInfo != null )
			return m_shareRightsInfo.isAllowFolderInternal();
		return false;
	}
	
	/**
	 * 
	 */
	public boolean canSharePublic()
	{
		if ( m_shareRightsInfo != null )
			return m_shareRightsInfo.isAllowPublic();

		return false;
	}
	
	public boolean canShareFolderPublic(){
		if ( m_shareRightsInfo != null )
			return m_shareRightsInfo.isAllowFolderPublic();
		return false;
	}
	
	/**
	 * 
	 */
	public boolean canSharePublicLink()
	{
		if ( m_shareRightsInfo != null )
			return m_shareRightsInfo.isAllowPublicLinks();
		
		return false;
	}

	/**
	 * 
	 */
	public String getRightsAsString()
	{
		StringBuffer rights;
		
		rights = new StringBuffer();
		if ( m_shareRightsInfo != null )
		{
			if ( m_shareRightsInfo.isAllowInternal() )
				rights.append( GwtTeaming.getMessages().internalRights() );
			
			if ( m_shareRightsInfo.isAllowFolderInternal() ){
				if ( rights.length() > 0 )
					rights.append( " / " );
				rights.append( GwtTeaming.getMessages().folderInternalRights() );
			}
			
			if ( m_shareRightsInfo.isAllowExternal() )
			{
				if ( rights.length() > 0 )
					rights.append( " / " );
				rights.append( GwtTeaming.getMessages().externalRights() );
			}
			
			if ( m_shareRightsInfo.isAllowFolderExternal() ){
				if ( rights.length() > 0 )
					rights.append(" / ");
				rights.append( GwtTeaming.getMessages().folderExternalRights() );
			}
			
			if ( m_shareRightsInfo.isAllowPublic() )
			{
				if ( rights.length() > 0 )
					rights.append( " / " );
				rights.append( GwtTeaming.getMessages().publicRights() );
			}
			
			if ( m_shareRightsInfo.isAllowFolderPublic() ){
				if ( rights.length() > 0)
					rights.append(" / ");
				rights.append( GwtTeaming.getMessages().folderPublicRights() );
			}
			
			if ( m_shareRightsInfo.isAllowForwarding() )
			{
				if ( rights.length() > 0 )
					rights.append( " / " );
				rights.append( GwtTeaming.getMessages().forwardingRights() );
			}
			
			if ( m_shareRightsInfo.isAllowPublicLinks() )
			{
				if ( rights.length() > 0 )
					rights.append( " / " );
				rights.append( GwtTeaming.getMessages().shareLinkRights() );
			}
		}
		
		if ( m_canAccess )
		{
			if ( rights.length() > 0 )
				rights.append( " / " );
			rights.append( GwtTeaming.getMessages().allowAccess() );
		}

		if ( rights.length() == 0 )
			return GwtTeaming.getMessages().noRights();
			
		return rights.toString();
	}
	
	/**
	 * 
	 */
	public void setCanAccess( boolean canAccess )
	{
		m_canAccess = canAccess;
	}

	/**
	 * 
	 */
	public void setCanReshare( boolean allow )
	{
		if ( m_shareRightsInfo == null )
			m_shareRightsInfo = new PerEntityShareRightsInfo();
		
		m_shareRightsInfo.setAllowForwarding( allow );
	}
	
	/**
	 * 
	 */
	public void setCanShareExternal( boolean allow )
	{
		if ( m_shareRightsInfo == null )
			m_shareRightsInfo = new PerEntityShareRightsInfo();
		
		m_shareRightsInfo.setAllowExternal( allow );
	}
	
	public void setCanShareFolderExternal( boolean allow ){
		if (m_shareRightsInfo == null )
			m_shareRightsInfo = new PerEntityShareRightsInfo();
		m_shareRightsInfo.setAllowFolderExternal( allow );
	}
	
	/**
	 * 
	 */
	public void setCanShareInternal( boolean allow )
	{
		if ( m_shareRightsInfo == null )
			m_shareRightsInfo = new PerEntityShareRightsInfo();
		
		m_shareRightsInfo.setAllowInternal( allow );
	}
	
	public void setCanShareFolderInternal( boolean allow ){
		if (m_shareRightsInfo == null )
			m_shareRightsInfo = new PerEntityShareRightsInfo();
		m_shareRightsInfo.setAllowFolderInternal( allow );
	}
	
	/**
	 * 
	 */
	public void setCanSharePublic( boolean allow )
	{
		if ( m_shareRightsInfo == null )
			m_shareRightsInfo = new PerEntityShareRightsInfo();
		
		m_shareRightsInfo.setAllowPublic( allow );
	}
	
	public void setCanShareFolderPublic( boolean allow ){
		if (m_shareRightsInfo == null )
			m_shareRightsInfo = new PerEntityShareRightsInfo();
		m_shareRightsInfo.setAllowFolderPublic( allow );
	}
	
	/**
	 * 
	 */
	public void setCanSharePublicLink( boolean allow )
	{
		if ( m_shareRightsInfo == null )
			m_shareRightsInfo = new PerEntityShareRightsInfo();
		
		m_shareRightsInfo.setAllowPublicLinks( allow );
	}
}
