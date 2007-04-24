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

import com.sitescape.team.NoObjectByTheIdException;

/**
 * @author Janet McCann
 *
 */
public class NoGroupByTheIdException extends NoObjectByTheIdException {
	   private static final String NoGroupByTheIdException_ErrorCode = "errorcode.no.group.by.the.Id";
	    
	    public NoGroupByTheIdException(Long groupId) {
	        super(NoGroupByTheIdException_ErrorCode, groupId);
	    }
	    public NoGroupByTheIdException(Long groupId, String message) {
	        super(NoGroupByTheIdException_ErrorCode, groupId, message);
	    }
	    public NoGroupByTheIdException(Long groupId, String message, Throwable cause) {
	        super(NoGroupByTheIdException_ErrorCode,groupId, message, cause);
	    }
	    public NoGroupByTheIdException(Long groupId, Throwable cause) {
	        super(NoGroupByTheIdException_ErrorCode, groupId, cause);
	    }
}
