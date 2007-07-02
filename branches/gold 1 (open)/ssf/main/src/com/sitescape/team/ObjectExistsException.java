package com.sitescape.team;

import com.sitescape.team.exception.UncheckedCodedException;

public class ObjectExistsException extends UncheckedCodedException {
	   public ObjectExistsException(String errorCode) {
	        super(errorCode);
	    }
	    public ObjectExistsException(String errorCode, Object[] errorArgs) {
	        super(errorCode, errorArgs);
	    }
	    public ObjectExistsException(String errorCode, Object[] errorArgs, String message) {
	        super(errorCode, errorArgs, message);
	    }
	    public ObjectExistsException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
	        super(errorCode, errorArgs, message, cause);
	    }
	    public ObjectExistsException(String errorCode, Object[] errorArgs, Throwable cause) {
	        super(errorCode, errorArgs, cause);
	    	
	    }

}
