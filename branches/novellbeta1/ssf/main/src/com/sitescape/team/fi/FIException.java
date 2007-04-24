package com.sitescape.team.fi;

import com.sitescape.team.exception.UncheckedException;

public class FIException extends UncheckedException {

	private static final long serialVersionUID = 1L;
	
	public FIException() {
        super();
    }
    public FIException(String message) {
        super(message);
    }
    public FIException(String message, Throwable cause) {
        super(message, cause);
    }
    public FIException(Throwable cause) {
        super(cause);
    }

}
