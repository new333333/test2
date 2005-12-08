
package com.sitescape.ef.domain;

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
