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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderIcons;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the GetWhoHasAccessCmd.
 * 
 * @author drfoster@novell.com
 */
public class WhoHasAccessInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean				m_entityHomeFolder;	//
	private BinderIcons			m_entityIcons;		//
	private List<AccessInfo>	m_groups;			//
	private List<AccessInfo>	m_users;			//
	private String				m_entityTitle;		//

	/**
	 * Inner class used to track information about an entity that has
	 * access.
	 */
	public static class AccessInfo implements IsSerializable {
		private Long	m_id;			//
		private String	m_avatarUrl;	//
		private String	m_hover;		//
		private String	m_name;			//
		
		/**
		 * Constructor method. 
		 * 
		 * For GWT serialization, must have a zero parameter constructor.
		 */
		public AccessInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param id
		 * @param name
		 * @param hover
		 * @param avatarUrl
		 */
		public AccessInfo(Long id, String name, String hover, String avatarUrl) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setId(       id       );
			setName(     name     );
			setHover(    hover    );
			setAvatarUrl(avatarUrl);
		}

		/**
		 * Constructor method.
		 * 
		 * @param id
		 * @param name
		 * @param hover
		 */
		public AccessInfo(Long id, String name, String hover) {
			// Initialize this object.
			this(id, name, hover, null);
		}

		/**
		 * Constructor method.
		 * 
		 * @param id
		 * @param name
		 */
		public AccessInfo(Long id, String name) {
			// Initialize this object.
			this(id, name, "", null);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Long   getId()        {return m_id;       }
		public String getAvatarUrl() {return m_avatarUrl;}
		public String getHover()     {return m_hover;    }
		public String getName()      {return m_name;     }

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setId(       Long   id)        {m_id        = id;       }
		public void setAvatarUrl(String avatarUrl) {m_avatarUrl = avatarUrl;} 
		public void setHover(    String hover)     {m_hover     = hover;    }
		public void setName(     String name)      {m_name      = name;     }
		
		/**
		 * Returns true if an AccessInfo is in a List<AccessInfo>, based on
		 * its ID and false otherwise.
		 * 
		 * @param accessList
		 * @param access
		 * 
		 * @return
		 */
		public static boolean isAccessInList(List<AccessInfo> accessList, AccessInfo access) {
			if ((null == accessList) || accessList.isEmpty() || (null == access)) {
				return false;
			}
			
			for (AccessInfo ai:  accessList) {
				if (ai.getId().equals(access.getId())) {
					return true;
				}
			}
			
			return false;
		}

		/**
		 * Returns true if any of the AccessInfo's in a List<AccessInfo>
		 * reference an avatar and false otherwise.
		 * 
		 * @param accessList
		 * 
		 * @return
		 */
		public static boolean listContainsAvatars(List<AccessInfo> accessList) {
			if ((null != accessList) && (!(accessList.isEmpty()))) {
				for (AccessInfo ai:  accessList) {
					String avatarUrl = ai.getAvatarUrl();
					if ((null != avatarUrl) && (0 < avatarUrl.length())) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public WhoHasAccessInfoRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_entityIcons = new BinderIcons();
	}
	
	/**
	 * Constructor method. 
	 * 
	 * @param entityTitle
	 */
	public WhoHasAccessInfoRpcResponseData(String entityTitle) {
		// Initialize the this object...
		this();
		
		// ...and store the parameter.
		setEntityTitle(entityTitle);
	}
	
	/**
	 * List add'er methods.
	 * 
	 * @param
	 */
	public void addGroup(AccessInfo group) {validateGroupList(); if (!(AccessInfo.isAccessInList(m_groups, group))) m_groups.add(group);}
	public void addUser( AccessInfo user)  {validateUserList();  if (!(AccessInfo.isAccessInList(m_users,  user ))) m_users.add( user );}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean          isEntityHomeFolder()                         {                     return m_entityHomeFolder;    }
	public List<AccessInfo> getGroups()                                  {validateGroupList(); return m_groups;              }
	public List<AccessInfo> getUsers()                                   {validateUserList();  return m_users;               }
	public String           getEntityIcon(BinderIconSize entityIconSize) {return m_entityIcons.getBinderIcon(entityIconSize);}
	public String           getEntityTitle()                             {                     return m_entityTitle;         }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEntityHomeFolder(boolean          entityHomeFolder)                          {m_entityHomeFolder = entityHomeFolder;                  }
	public void setGroupList(       List<AccessInfo> groups)                                    {m_groups           = groups;                            }
	public void setUserList(        List<AccessInfo> users)                                     {m_users            = users;                             }
	public void setEntityIcon(      String           entityIcon, BinderIconSize entityIconSize) {m_entityIcons.setBinderIcon(entityIcon, entityIconSize);}
	public void setEntityTitle(     String           entityTitle)                               {m_entityTitle      = entityTitle;                       }

	/*
	 * List validation.  Used to guard against null pointer references
	 * when accessing the various lists.
	 */
	private void validateGroupList() {if (null == m_groups) m_groups = new ArrayList<AccessInfo>();}
	private void validateUserList()  {if (null == m_users)  m_users = new ArrayList<AccessInfo>(); }
}
