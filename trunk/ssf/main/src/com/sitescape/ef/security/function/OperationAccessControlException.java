package com.sitescape.ef.security.function;

import com.sitescape.ef.security.AccessControlException;

public class OperationAccessControlException extends AccessControlException {
    
	private static final String OperationAccessControlException_ErrorCode = "errorcode.operation.denied";
	
	public OperationAccessControlException(String username, String operationName, 
			Long workAreaId) {
		super(OperationAccessControlException_ErrorCode, new Object[] {username, operationName, workAreaId});
	}
}
