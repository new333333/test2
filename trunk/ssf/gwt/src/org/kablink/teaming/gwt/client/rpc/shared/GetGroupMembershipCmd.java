/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class holds all of the information necessary to execute the
 * 'get group membership' command.
 * 
 * @author drfoster@novell.com
 */
public class GetGroupMembershipCmd extends VibeRpcCmd {
	private String m_groupId;
	private int m_offset;
	private int m_numResults;	// Number of results to return
	private MembershipFilter m_filter;
	
	/**
	 * This defines what we want returned when we get the list of group members.
	 * 
	 * @author jwootton
	 */
	public enum MembershipFilter implements IsSerializable
	{
		RETRIEVE_ALL_MEMBERS,
		RETRIEVE_GROUPS_ONLY,
		RETRIEVE_USERS_ONLY
	}
	
	/**
	 * Class constructor.
	 * 
	 * For GWT serialization, must have a zero parameter
	 * constructor.
	 */
	public GetGroupMembershipCmd() {
		super();
		
		m_groupId = null;
		m_offset = 0;
		m_numResults = Integer.MAX_VALUE - 1;
		m_filter = MembershipFilter.RETRIEVE_ALL_MEMBERS;
	}

	/**
	 * Class constructor.
	 * 
	 * @param groupId
	 */
	public GetGroupMembershipCmd(String groupId) {
		this();
		
		m_groupId = groupId;
		m_offset = 0;
		m_numResults = Integer.MAX_VALUE - 1;
		m_filter = MembershipFilter.RETRIEVE_ALL_MEMBERS;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public String getGroupId() {return m_groupId;}	
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.GET_GROUP_MEMBERSHIP.ordinal();
	}
	
	/**
	 * 
	 */
	public MembershipFilter getFilter()
	{
		return m_filter;
	}
	
	/**
	 * 
	 */
	public int getNumResults()
	{
		return m_numResults;
	}
	
	/**
	 * 
	 */
	public int getOffset()
	{
		return m_offset;
	}
	
	/**
	 * 
	 */
	public void setFilter( MembershipFilter filter )
	{
		m_filter = filter;
	}
	
	/**
	 * 
	 */
	public void setNumResults( int numResults )
	{
		m_numResults = numResults;
	}
	
	/**
	 * 
	 */
	public void setOffset( int offset )
	{
		m_offset = offset;
	}
}
