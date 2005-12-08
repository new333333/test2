
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 *
 */
public class NoEmailAliasByTheIdException extends NoObjectByTheIdException {
	   private static final String NoEmailAliasByTheIdException_ErrorCode = "errorcode.no.emailalias.by.the.id";
	    
	    public NoEmailAliasByTheIdException(String aliasId) {
	        super(NoEmailAliasByTheIdException_ErrorCode, aliasId);
	    }
	    public NoEmailAliasByTheIdException(String aliasId, String message) {
	        super(NoEmailAliasByTheIdException_ErrorCode, aliasId, message);
	    }
	    public NoEmailAliasByTheIdException(String aliasId, String message, Throwable cause) {
	        super(NoEmailAliasByTheIdException_ErrorCode,aliasId, message, cause);
	    }
	    public NoEmailAliasByTheIdException(String aliasId, Throwable cause) {
	        super(NoEmailAliasByTheIdException_ErrorCode, aliasId, cause);
	    }
}
