package com.sitescape.team.security.acl;

import com.sitescape.team.security.AccessControlException;

public class AclAccessControlException extends AccessControlException {
    
	private static final String AclAccessControlException_ErrorCode = "errorcode.acl.denied";
	
	public AclAccessControlException(String username, String accessType) {
		super(AclAccessControlException_ErrorCode, new Object[] {username, accessType});
	}
}
