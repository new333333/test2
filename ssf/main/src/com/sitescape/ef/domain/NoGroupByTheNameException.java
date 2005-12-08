
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 *
 */
public class NoGroupByTheNameException extends NoObjectByTheNameException {
	   private static final String NoGroupByTheNameException_ErrorCode = "error.no.group.by.the.name";
	    
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
