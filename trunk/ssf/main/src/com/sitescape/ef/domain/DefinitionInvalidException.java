package com.sitescape.ef.domain;
import com.sitescape.ef.exception.UncheckedCodedException;
/**
  *
 * @author  Peter Hurley
 * @version $Revision: 1.0 $
 *
 */
public class DefinitionInvalidException extends UncheckedCodedException {
    public DefinitionInvalidException(String errorCode) {
    	super(errorCode);
    }
    public DefinitionInvalidException(String errorCode, Object[] errorArgs) {
        super(errorCode, errorArgs);
    }
    public DefinitionInvalidException(String errorCode, Object[] errorArgs, String message) {
        super(errorCode, errorArgs, message);
    }
    public DefinitionInvalidException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
        super(errorCode, errorArgs, message, cause);
    }
    public DefinitionInvalidException(String errorCode, Object[] errorArgs, Throwable cause) {
        super(errorCode, errorArgs, cause);
    }
}