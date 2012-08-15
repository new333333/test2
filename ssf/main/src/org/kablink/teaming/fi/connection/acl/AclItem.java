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

package org.kablink.teaming.fi.connection.acl;

import java.util.Set;

/**
 * A item/entry in an access control list (ACL).
 * 
 * @author jong
 *
 */
public class AclItem {
	
	private String principalId;
	private Set<AclItemPermission> permissions;
	
	/**
	 * Constructor
	 * 
	 * @param principalId
	 * @param permissions
	 */
	public AclItem(String principalId, Set<AclItemPermission> permissions) {
		if(principalId == null)
			throw new IllegalArgumentException("Principal ID must be specified");
		if(permissions == null)
			throw new IllegalArgumentException("Permissions must be specified");
		this.principalId = principalId;
		this.permissions = permissions;
	}
	
	/**
	 * Returns the ID of the file system principal. 
	 * 
	 * @return
	 */
	public String getPrincipalId() {
		return principalId;
	}
	
	/**
	 * Returns the set of file system permissions.
	 * 
	 * @return
	 */
	public Set<AclItemPermission> getPermissions() {
		return permissions;
	}

	/**
	 * Compares the specified object with this ACL item for equality.
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj == null || !(obj instanceof AclItem))
			return false;
		AclItem other = (AclItem)obj;
		if(!principalId.equals(other.principalId))
			return false;
		if(!permissions.equals(other.permissions))
			return false;
		return true;
	}
	
	/**
	 * Returns the hash-code value for this ACL item.
	 */
	@Override 
	public int hashCode() {
		int h = principalId.hashCode();
		h = h * 127 + permissions.hashCode();
		return h;
	}
	
}
