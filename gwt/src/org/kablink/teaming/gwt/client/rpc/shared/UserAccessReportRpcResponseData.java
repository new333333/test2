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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return a
 * license report.
 * 
 * @author drfoster@novell.com
 */
public class UserAccessReportRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<UserAccessItem>	m_userAccessItems;	//
	private String					m_modifyACLsUrl;	//

	/**
	 * Inner class used to represent an instance of a user's access.
	 */
	public static class UserAccessItem implements IsSerializable {
		private Long	m_binderId;			// 
		private String	m_entityEntityPath;	//
		private String	m_entityEntityType;	//
		
		/**
		 * Constructor method. 
		 * 
		 * For GWT serialization, must have a zero parameter
		 * constructor.
		 */
		public UserAccessItem() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method. 
		 * 
		 * @param entityEntityPath
		 * @param entityEntityType
		 * @param binderId
		 */
		public UserAccessItem(String entityEntityPath, String entityEntityType, Long binderId) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setEntityPath(entityEntityPath);
			setEntityType(entityEntityType);
			setBinderId(  binderId        );
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Long   getBinderId()   {return m_binderId;        }
		public String getEntityPath() {return m_entityEntityPath;}
		public String getEntityType() {return m_entityEntityType;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setBinderId(  Long   binderId)         {m_binderId         = binderId;        }
		public void setEntityPath(String entityEntityPath) {m_entityEntityPath = entityEntityPath;}
		public void setEntityType(String entityEntityType) {m_entityEntityType = entityEntityType;}
	}

	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public UserAccessReportRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		setUserAccessItems(new ArrayList<UserAccessItem>());
	}
	
	/**
	 * Constructor method. 
	 * 
	 * @param modifyACLsUrl
	 */
	public UserAccessReportRpcResponseData(String modifyACLsUrl) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setModifyACLsUrl(modifyACLsUrl);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<UserAccessItem> getUserAccessItems() {return m_userAccessItems;}
	public String               getModifyACLsUrl()   {return m_modifyACLsUrl;  }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setUserAccessItems(List<UserAccessItem> userAccessItems) {m_userAccessItems = userAccessItems;}
	public void setModifyACLsUrl(  String               modifyACLsUrl)   {m_modifyACLsUrl   = modifyACLsUrl;  }

	/**
	 * Adds a UserAccessItem to the list of them being tracked.
	 * 
	 * @param uai
	 */
	public void addUserAccessItem(UserAccessItem uai) {
		m_userAccessItems.add(uai);
	}
}
