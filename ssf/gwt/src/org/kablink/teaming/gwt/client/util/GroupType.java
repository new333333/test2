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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration used to communicate the type of a group between the
 * client and the server as part of a GWT RPC request.
 * 
 * @author drfoster@novell.com
 */
public class GroupType implements IsSerializable {
	private boolean 	m_admin;		//
	private GroupClass	m_groupClass;	//
	
	/**
	 * Enumeration that defines the type of group.
	 */
	public enum GroupClass implements IsSerializable {
		EXTERNAL_LDAP,			// External users imported from LDAP.
		EXTERNAL_LOCAL,			// External users, not from ldap
		INTERNAL_LDAP,			// Internal users imported from LDAP.
		INTERNAL_LOCAL,			// Internal users, not from ldap
		INTERNAL_SYSTEM,		// Systems group (e.g., File Sync Agent, ...)
		UNKNOWN;				// Could not be classified.
	
		/**
		 */
		public boolean isExternalLdap() {
			return EXTERNAL_LDAP.equals(this);
		}
		
		/**
		 */
		public boolean isExternalLocal() {
			return EXTERNAL_LOCAL.equals(this);
		}
		
		/**
		 */
		public boolean isInternalLdap() {
			return INTERNAL_LDAP.equals(this);
		}
		
		/**
		 */
		public boolean isInternalLocal() {
			return INTERNAL_LOCAL.equals(this);
		}
		
		/**
		 */
		public boolean isLdap() {
			return (isExternalLdap() || isInternalLdap());
		}
		
		/**
		 */
		public boolean isLocal() {
			return (isExternalLocal() || isInternalLocal());
		}
		
		/**
		 */
		public boolean isSystem() {
			return INTERNAL_SYSTEM.equals(this);
		}
	}
	
	/*
	 * Constructor method.
	 * 
	 * Zero parameter constructor required for GWT serialization.
	 */
	private GroupType()
	{
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param groupClass
	 * @param admin
	 */
	public GroupType(GroupClass groupClass, boolean admin) {
		this();
		
		setGroupClass(groupClass);
		setAdmin(     admin     );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean    isAdmin()       {return m_admin;     }
	public GroupClass getGroupClass() {return m_groupClass;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAdmin(     boolean    admin)      {m_admin      = admin;     }
	public void setGroupClass(GroupClass groupClass) {m_groupClass = groupClass;}
}
