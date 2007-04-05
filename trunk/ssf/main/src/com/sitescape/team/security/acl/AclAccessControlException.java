/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.security.acl;

import com.sitescape.team.security.AccessControlException;

public class AclAccessControlException extends AccessControlException {
    
	private static final String AclAccessControlException_ErrorCode = "errorcode.acl.denied";
	
	public AclAccessControlException(String username, String accessType) {
		super(AclAccessControlException_ErrorCode, new Object[] {username, accessType});
	}
}
