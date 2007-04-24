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

package com.sitescape.team.domain;

import com.sitescape.team.NoObjectByTheNameException;

/**
 * @author Janet McCann
 *
 */
public class NoGroupByTheNameException extends NoObjectByTheNameException {
	   private static final String NoGroupByTheNameException_ErrorCode = "errorcode.no.group.by.the.name";
	    
	    public NoGroupByTheNameException(String userName) {
	        super(NoGroupByTheNameException_ErrorCode, userName);
	    }
	    public NoGroupByTheNameException(String userName, String message) {
	        super(NoGroupByTheNameException_ErrorCode, userName, message);
	    }
	    public NoGroupByTheNameException(String userName, String message, Throwable cause) {
	        super(NoGroupByTheNameException_ErrorCode,userName, message, cause);
	    }
	    public NoGroupByTheNameException(String userName, Throwable cause) {
	        super(NoGroupByTheNameException_ErrorCode, userName, cause);
	    }
}
