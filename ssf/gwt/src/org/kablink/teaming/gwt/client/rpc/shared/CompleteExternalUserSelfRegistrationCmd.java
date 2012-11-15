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

/**
 * This class holds all of the information necessary to execute the
 * "Complete external user self registration" command.
 * @author jwootton
 *
 */
public class CompleteExternalUserSelfRegistrationCmd extends VibeRpcCmd
{
	private Long m_extUserId;
	private String m_firstName;
	private String m_lastName;
	private String m_pwd;
	private String m_invitationUrl;
	
	/**
	 * For GWT serialization, must have a zero param contructor
	 */
	public CompleteExternalUserSelfRegistrationCmd()
	{
		super();
	}
	
	/**
	 * 
	 */
	public CompleteExternalUserSelfRegistrationCmd(
		Long extUserId,
		String firstName,
		String lastName,
		String pwd,
		String permaLink )
	{
		m_extUserId = extUserId;
		m_firstName = firstName;
		m_lastName = lastName;
		m_pwd = pwd;
		m_invitationUrl = permaLink;
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
		return VibeRpcCmdType.COMPLETE_EXTERNAL_USER_SELF_REGISTRATION.ordinal();
	}
	
	/**
	 * 
	 */
	public Long getExtUserId()
	{
		return m_extUserId;
	}
	
	/**
	 * 
	 */
	public String getFirstName()
	{
		return m_firstName;
	}
	
	/**
	 * 
	 */
	public String getLastName()
	{
		return m_lastName;
	}
	
	/**
	 * 
	 */
	public String getInvitationUrl()
	{
		return m_invitationUrl;
	}
	
	/**
	 * 
	 */
	public String getPwd()
	{
		return m_pwd;
	}
}
