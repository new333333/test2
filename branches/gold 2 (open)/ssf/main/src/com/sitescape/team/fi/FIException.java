package com.sitescape.team.fi;

public class FIException extends RuntimeException {

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
