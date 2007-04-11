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
package com.sitescape.team.security.function;

import com.sitescape.team.security.AccessControlException;

public class OperationAccessControlException extends AccessControlException {
    
	private static final String OperationAccessControlException_ErrorCode = "errorcode.operation.denied";
	
	public OperationAccessControlException(String username, String operationName, 
			String workAreaName) {
		super(OperationAccessControlException_ErrorCode, new Object[] {username, operationName, workAreaName});
	}
}
