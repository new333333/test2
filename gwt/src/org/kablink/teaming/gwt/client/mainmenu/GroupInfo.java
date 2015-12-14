/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.mainmenu;

import org.kablink.teaming.gwt.client.GroupMembershipInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate group information between the client
 * (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.getMyGroups().)
 * 
 * @author drfoster@novell.com
 */
public class GroupInfo implements IsSerializable, VibeRpcResponseData {
	private boolean				m_admin;			// true -> The group has admin rights.  false -> It doesn't.
	private boolean				m_fromLdap;			//
	private GroupMembershipInfo	m_membershipInfo;	//
	private Long				m_id;				// The group's id.
	private String				m_desc;				// The group's description
	private String				m_fqdn;				// If the group came from ldap, the group's fully qualified dn
	private String				m_name;				// The group's name
	private String				m_title;			// The group's title.
	

	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public GroupInfo() {
		super();
		
		m_membershipInfo = new GroupMembershipInfo();
		m_membershipInfo.setMembershipInfo(false, false);
		
		setIsFromLdap(false);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isAdmin()                {return m_admin;                                  }
	public boolean getIsExternalAllowed()   {return m_membershipInfo.getIsExternalAllowed();  }
	public boolean getIsFromLdap()          {return m_fromLdap;                               }
	public boolean getIsMembershipDynamic() {return m_membershipInfo.getIsMembershipDynamic();}
	public Long    getId()                  {return m_id;                                     }
	public String  getDesc()                {return m_desc;                                   }
	public String  getDn()                  {return m_fqdn;                                   }
	public String  getName()                {return m_name;                                   }
	public String  getTitle()               {return m_title;                                  }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAdmin(     boolean admin)    {m_admin    = admin;   }
	public void setIsFromLdap(boolean fromLdap) {m_fromLdap = fromLdap;}
	public void setId(        Long    id)       {m_id       = id;      }
	public void setDesc(      String  desc)     {m_desc     = desc;    }
	public void setDn(        String  dn)       {m_fqdn     = dn;      }
	public void setName(      String  name)     {m_name     = name;    }
	public void setTitle(     String  title)    {m_title    = title   ;}
	
	/**
	 * Return the secondary display text for this group.  The value
	 * returned is generally used as the text displayed on a mouse
	 * over.
	 * 
	 * @return
	 */
	public String getSecondaryDisplayText() {
		if ((null != m_fqdn) && (0 < m_fqdn.length())) {
			return m_fqdn;
		}
		
		if ((null != m_desc) && (0 < m_desc.length())) {
			return m_desc;
		}
		
		return m_name;
	}

	/**
	 * ?
	 *  
	 * @param dynamic
	 * @param externalAllowed
	 */
	public void setMembershipInfo(boolean dynamic, boolean externalAllowed) {
		m_membershipInfo.setMembershipInfo(dynamic, externalAllowed);
	}
}
