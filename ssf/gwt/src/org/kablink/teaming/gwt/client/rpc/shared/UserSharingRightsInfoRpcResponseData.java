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

import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.util.PerEntityShareRightsInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return
 * information about user sharing rights.
 * 
 * @author drfoster@novell.com
 */
public class UserSharingRightsInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean 							m_externalEnabled;		//
	private boolean 							m_forwardingEnabled;	//
	private boolean 							m_internalEnabled;		//
	private boolean								m_publicEnabled;		//
	private boolean								m_publicLinksEnabled;	//
	private Map<Long, PerEntityShareRightsInfo>	m_userRightsMap;		//
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public UserSharingRightsInfoRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else requiring it.
		m_userRightsMap = new HashMap<Long, PerEntityShareRightsInfo>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                             isExternalEnabled()        {return m_externalEnabled;          }
	public boolean                             isForwardingEnabled()      {return m_forwardingEnabled;        }
	public boolean                             isInternalEnabled()        {return m_internalEnabled;          }
	public boolean                             isPublicEnabled()          {return m_publicEnabled;            }
	public boolean                             isPublicLinksEnabled()     {return m_publicLinksEnabled;       }
	public Map<Long, PerEntityShareRightsInfo> getUserRightsMap()         {return m_userRightsMap;            }
	public PerEntityShareRightsInfo            getUserRights(Long userId) {return m_userRightsMap.get(userId);}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void addUserRights(        Long userId, PerEntityShareRightsInfo userRights)         {m_userRightsMap.put(userId, userRights);  }
	public void setExternalEnabled(   boolean                               externalEnabled)    {m_externalEnabled    = externalEnabled;   }
	public void setForwardingEnabled( boolean                               forwardingEnabled)  {m_forwardingEnabled  = forwardingEnabled; }
	public void setInternalEnabled(   boolean                               internalEnabled)    {m_internalEnabled    = internalEnabled;   }
	public void setPublicEnabled(     boolean                               publicEnabled)      {m_publicEnabled      = publicEnabled;     }
	public void setPublicLinksEnabled(boolean                               publicLinksEnabled) {m_publicLinksEnabled = publicLinksEnabled;}
	public void setUserRightsMap(     Map<Long, PerEntityShareRightsInfo>   userRightsMap)      {m_userRightsMap      = userRightsMap;     }
	
	/**
	 * Returns true if all of the flags are set and false otherwise.
	 * 
	 * @return
	 */
	public boolean allFlagsSet() {
		return (
			m_externalEnabled   &&
			m_forwardingEnabled &&
			m_internalEnabled   &&
			m_publicEnabled     &&
			m_publicLinksEnabled);
	}
	
	/**
	 * Returns true if any of the flags are set and false otherwise.
	 * 
	 * @return
	 */
	public boolean anyFlagsSet() {
		return (
			m_externalEnabled   ||
			m_forwardingEnabled ||
			m_internalEnabled   ||
			m_publicEnabled     ||
			m_publicLinksEnabled);
	}
}
