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

import org.kablink.teaming.gwt.client.util.GroupType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used in GWT RPC calls to represent a Group.
 * 
 * @author jwootton@novell.com
 */
public class GwtGroup extends GwtPrincipal implements IsSerializable
{
	private String m_name;
	private String m_title;
	private String m_id;
	private String m_dn;
	private String m_desc;
	private GroupType m_groupType;
	
	/**
	 * Constructor method. 
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GwtGroup()
	{
		// Nothing to do.
	}	
	
	/**
	 * 
	 */
	public String getDesc()
	{
		return m_desc;
	}
	
	/**
	 * 
	 */
	public String getDn()
	{
		return m_dn;
	}
	
	/**
	 * 
	 */
	public GroupType getGroupType()
	{
		return m_groupType;
	}
	
	/**
	 * 
	 */
	@Override
	public Long getIdLong()
	{
		if ( m_id != null )
			return Long.valueOf( m_id );
		
		return null;
	}
	
	/**
	 * 
	 */
	@Override
	public String getImageUrl()
	{
		ImageResource imgResource;

		if ( m_groupType != null )
			imgResource = GwtClientHelper.getGroupTypeImage( m_groupType );
		else
			imgResource = GwtClientHelper.getGroupTypeImage( GroupType.UNKNOWN );
		
		return imgResource.getSafeUri().asString();
	}
	
	/**
	 * Returns the group's name.
	 */
	@Override
	public String getName()
	{
		return m_name;
	}
	
	/**
	 * Returns the group's title.
	 */
	@Override
	public String getTitle()
	{
		return m_title;
	}
	
	/**
	 * Returns the group's ID. 
	 */
	public String getId()
	{
		return m_id;
	}
	
	/**
	 * 
	 */
	@Override
	public String getSecondaryDisplayText()
	{
		return getDn();
	}
	
	/**
	 * 
	 */
	@Override
	public PrincipalType getType()
	{
		return PrincipalType.GROUP;
	}

	/**
	 * Return the name that should be displayed when this group is
	 * displayed.
	 * 
	 * Implements the GwtTeamingItem.getShortDisplayName() abstract
	 * method.
	 */
	@Override
	public String getShortDisplayName()
	{
		return getName();
	}
	
	/**
	 * 
	 */
	public void setDesc( String desc )
	{
		m_desc = desc;
	}
	
	/**
	 * 
	 */
	public void setDn( String dn )
	{
		m_dn = dn;
	}
	
	/**
	 * 
	 */
	public void setGroupType( GroupType groupType )
	{
		m_groupType = groupType;
	}
	
	/**
	 * Stores the group's name.
	 * 
	 * @param name
	 */
	public void setName( String name )
	{
		m_name = name;
	}
	
	/**
	 * Stores the group's ID. 
	 * 
	 * @param groupId
	 */
	public void setId( String groupId )
	{
		m_id = groupId;
	}
	
	
	/**
	 * Stores the group's title. 
	 * 
	 * @param title
	 */
	public void setTitle( String title )
	{
		m_title = title;
	}
}
