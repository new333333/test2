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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.util.PrincipalInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return
 * information about folder view's user_list <item>'s.
 * 
 * @author drfoster@novell.com
 */
public class UserListInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<UserListInfo>	m_userListInfoList;	//
	
	/**
	 * Inner class used to represent an instance of a user_list <item>.
	 */
	public static class UserListInfo implements IsSerializable {
		private List<PrincipalInfo>	m_users;	//
		private String				m_caption;	//
		private String				m_dataName;	//
		
		/*
		 * Zero parameter constructor method required for GWT
		 * serialization.
		 */
		private UserListInfo() {
			// Initialize the super class...
			super();
			
			// ...and allocate the List<PrincipalInfo>'s.
			setUsers(new ArrayList<PrincipalInfo>());
		}

		/**
		 * Constructor method.
		 * 
		 * @param caption
		 * @param dataName
		 */
		public UserListInfo(String caption, String dataName) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setCaption( caption );
			setDataName(dataName);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public List<PrincipalInfo> getUsers()    {return m_users;   }
		public String              getCaption()  {return m_caption; }
		public String              getDataName() {return m_dataName;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setUsers(   List<PrincipalInfo> users)    {m_users    = users;   }
		public void setCaption( String              caption)  {m_caption  = caption; }
		public void setDataName(String              dataName) {m_dataName = dataName;}
		
		/**
		 * Adds a PrincipalInfo object to the List<PrincipalInfo>'s.
		 * 
		 * @param user
		 */
		public void addUser(PrincipalInfo user) {
			m_users.add(user);
		}
	}
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public UserListInfoRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and allocate the List<UserListInfo>.
		setUserListInfoList(new ArrayList<UserListInfo>());
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int                getUserListInfoListCount() {return m_userListInfoList.size();}
	public List<UserListInfo> getUserListInfoList()      {return m_userListInfoList;       }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setUserListInfoList(List<UserListInfo> userListInfoList) {m_userListInfoList = userListInfoList;}
	
	/**
	 * Adds a UserListInfo object to the List<UserListInfo>'s.
	 * 
	 * @param userListInfo
	 */
	public void addUserListInfo(UserListInfo userListInfo) {
		m_userListInfoList.add(userListInfo);
	}
}
