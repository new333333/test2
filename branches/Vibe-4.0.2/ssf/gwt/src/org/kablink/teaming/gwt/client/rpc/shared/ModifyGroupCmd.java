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


import org.kablink.teaming.gwt.client.GwtDynamicGroupMembershipCriteria;



/**
 * This class holds all of the information necessary to execute the "modify group" command.
 * 
 * @author jwootton
 *
 */
public class ModifyGroupCmd extends VibeRpcCmd
{
	private Long m_id;
	private String m_title;
	private String m_desc;
	private boolean m_isMembershipDynamic;
	private GwtDynamicGroupMembershipCriteria m_membershipCriteria;
	
	/**
	 * For GWT serialization, must have a zero param contructor
	 */
	public ModifyGroupCmd()
	{
		super();
	}
	
	/**
	 * 
	 */
	public ModifyGroupCmd(
		Long id,
		String title,
		String desc,
		boolean isMembershipDynamic,
		GwtDynamicGroupMembershipCriteria membershipCriteria )
	{
		m_id = id;
		m_title = title;
		m_desc = desc;
		m_isMembershipDynamic = isMembershipDynamic;
		m_membershipCriteria = membershipCriteria;
	}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType()
	{
		return VibeRpcCmdType.MODIFY_GROUP.ordinal();
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
	public Long getId()
	{
		return m_id;
	}
	
	/**
	 * 
	 */
	public boolean getIsMembershipDynamic()
	{
		return m_isMembershipDynamic;
	}
	
	/**
	 * 
	 */
	public GwtDynamicGroupMembershipCriteria getMembershipCriteria()
	{
		return m_membershipCriteria;
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
	public void setDesc( String desc )
	{
		m_desc = desc;
	}
	
	/**
	 * 
	 */
	public void setIsMembershipDynamic( boolean isMembershipDynamic )
	{
		m_isMembershipDynamic = isMembershipDynamic;
	}
	
	/**
	 * 
	 */
	public void setTitle( String title )
	{
		m_title = title;
	}
}
