package com.sitescape.ef.domain;
import com.sitescape.ef.exception.UncheckedCodedException;
/**
  *
 * @author  Peter Hurley
 * @version $Revision: 1.0 $
 *
 */
public class DefinitionInvalidException extends UncheckedCodedException {
	private static final String DefinitionInvalidException_ErrorCode = "error.definition.invalid";
	 
	   public DefinitionInvalidException(String id) {
	        super(DefinitionInvalidException_ErrorCode, new Object[]{id});
	    }
	    public DefinitionInvalidException(String id, String message) {
	        super(DefinitionInvalidException_ErrorCode,  new Object[]{id}, message);
	    }
	    public DefinitionInvalidException(String id, String message, Throwable cause) {
	        super(DefinitionInvalidException_ErrorCode, new Object[]{id}, message, cause);
	    }
	    public DefinitionInvalidException(String id, Throwable cause) {
	        super(DefinitionInvalidException_ErrorCode,  new Object[]{id}, cause);
	    }

}