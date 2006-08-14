
package com.sitescape.ef.domain;

/**
 * @author Janet McCann
 *
 */
public class NoConfigurationByTheIdException extends NoObjectByTheIdException {
	   private static final String NoConfigurationByTheIdException_ErrorCode = "errorcode.no.configuration.by.the.id";
	    
	    public NoConfigurationByTheIdException(String defId) {
	        super(NoConfigurationByTheIdException_ErrorCode, defId);
	    }
	    public NoConfigurationByTheIdException(String defId, String message) {
	        super(NoConfigurationByTheIdException_ErrorCode, defId, message);
	    }
	    public NoConfigurationByTheIdException(String defId, String message, Throwable cause) {
	        super(NoConfigurationByTheIdException_ErrorCode,defId, message, cause);
	    }
	    public NoConfigurationByTheIdException(String defId, Throwable cause) {
	        super(NoConfigurationByTheIdException_ErrorCode, defId, cause);
	    }
}
