package com.sitescape.team.security.function;

import com.sitescape.team.security.AccessControlException;

public class OperationAccessControlException extends AccessControlException {
    
	private static final String OperationAccessControlException_ErrorCode = "errorcode.operation.denied";
	
	public OperationAccessControlException(String username, String operationName, 
			Long workAreaId) {
		super(OperationAccessControlException_ErrorCode, new Object[] {username, operationName, workAreaId});
	}
}
