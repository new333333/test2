package org.kablink.teaming;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.kablink.teaming.exception.UncheckedCodedException;

public class UserExistsException extends UncheckedCodedException {
	   
	public UserExistsException(String errorCode) {
	        super(errorCode);
	    }
	    public UserExistsException(String errorCode, Object[] errorArgs) {
	        super(errorCode, errorArgs);
	    }
	    public UserExistsException(String errorCode, Object[] errorArgs, String message) {
	        super(errorCode, errorArgs, message);
	    }
	    public UserExistsException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
	        super(errorCode, errorArgs, message, cause);
	    }
	    public UserExistsException(String errorCode, Object[] errorArgs, Throwable cause) {
	        super(errorCode, errorArgs, cause);
	    	
	    }
	    //overload to remove stack trace filling log files
	    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
	    public void printStackTrace(PrintStream s) {
	        synchronized (s) {
	            s.println(toString());
	        }
	    }
	    //overload to remove stack trace filling log files
	    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
	    public void printStackTrace(PrintWriter s) {
	        synchronized (s) {
	            s.println(toString());
	        }	
	    }    
}
