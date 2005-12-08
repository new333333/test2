
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 *
 */
public class NoDefinitionByTheIdException extends NoObjectByTheIdException {
	   private static final String NoDefinitionByTheIdException_ErrorCode = "errorcode.no.definition.by.the.id";
	    
	    public NoDefinitionByTheIdException(String defId) {
	        super(NoDefinitionByTheIdException_ErrorCode, defId);
	    }
	    public NoDefinitionByTheIdException(String defId, String message) {
	        super(NoDefinitionByTheIdException_ErrorCode, defId, message);
	    }
	    public NoDefinitionByTheIdException(String defId, String message, Throwable cause) {
	        super(NoDefinitionByTheIdException_ErrorCode,defId, message, cause);
	    }
	    public NoDefinitionByTheIdException(String defId, Throwable cause) {
	        super(NoDefinitionByTheIdException_ErrorCode, defId, cause);
	    }
}
