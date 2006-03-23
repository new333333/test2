
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 *
 */
public class NoTagByTheIdException extends NoObjectByTheIdException {
	   private static final String NoTagByTheIdException_ErrorCode = "errorcode.no.tag.by.the.id";
	    
	    public NoTagByTheIdException(String defId) {
	        super(NoTagByTheIdException_ErrorCode, defId);
	    }
	    public NoTagByTheIdException(String defId, String message) {
	        super(NoTagByTheIdException_ErrorCode, defId, message);
	    }
	    public NoTagByTheIdException(String defId, String message, Throwable cause) {
	        super(NoTagByTheIdException_ErrorCode,defId, message, cause);
	    }
	    public NoTagByTheIdException(String defId, Throwable cause) {
	        super(NoTagByTheIdException_ErrorCode, defId, cause);
	    }
}
