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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration used to communicate the type of a principal (user or
 * group) between the client and the server as part of a GWT RPC request.
 * 
 * @author drfoster@novell.com
 */
public enum PrincipalType implements IsSerializable {
	// User classifications.
	EXTERNAL_GUEST,			// Guest.
	EXTERNAL_OPEN_ID,		// External user who has authenticated via OpenID
	EXTERNAL_OTHERS,		// All other externals
							//
	INTERNAL_LDAP,			// Users imported from LDAP.
	INTERNAL_PERSON_ADMIN,	// System defined admin.
	INTERNAL_PERSON_OTHERS,	// All other non-LDAP person users.
	INTERNAL_SYSTEM,		// All systems users (e.g., File Sync Agent, ...)

	// Group classifications.
	LDAP_GROUP,				// Groups imported from LDAP.
	LOCAL_GROUP,			// All non-system, non-LDAP groups. 
	SYSTEM_GROUP,			// System groups.
							//
	UNKNOWN;				// Could not be classified.

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isExternal() {
		boolean reply;
		switch (this) {
		case EXTERNAL_GUEST:
		case EXTERNAL_OPEN_ID:
		case EXTERNAL_OTHERS:  reply = true;  break;
		default:               reply = false; break;
		}
		return reply;
	}

	public boolean isGroup() {
		return
			(isLdapGroup() ||
			isLocalGroup() ||
			isSystemGroup());
	}
	
	public boolean isGuest() {
		return this.equals(EXTERNAL_GUEST);
	}
	
	public boolean isInternal() {
		boolean reply;
		switch (this) {
		case INTERNAL_LDAP:
		case INTERNAL_PERSON_ADMIN:
		case INTERNAL_PERSON_OTHERS:
		case INTERNAL_SYSTEM:  reply = true;  break;
		default:               reply = false; break;
		}
		return reply;
	}
	
	public boolean isInternalLdap() {
		return INTERNAL_LDAP.equals(this);
	}
	
	public boolean isLocal() {
		return (!(INTERNAL_LDAP.equals(this)));
	}
	
	public boolean isLdapGroup() {
		return LDAP_GROUP.equals(this);
	}
	
	public boolean isLocalGroup() {
		return LOCAL_GROUP.equals(this);
	}
	
	public boolean isSystemGroup() {
		return SYSTEM_GROUP.equals(this);
	}
	
	public boolean isUser() {
		return ((!(isGroup())) && (!(UNKNOWN.equals(this))));
	}
}
