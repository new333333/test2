package org.kablink.teaming;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.kablink.teaming.exception.UncheckedCodedException;
import org.kablink.teaming.remoting.ApiErrorCode;

public class UserExistsException extends UncheckedCodedException {
	   
    private static final String userExistsException_ErrorCode = "errorcode.user.alreadyExists";

	public UserExistsException() {
	        super(userExistsException_ErrorCode);
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
    
    public int getHttpStatusCode() {
    	return 409; // Conflict
    }

	/* (non-Javadoc)
	 * @see org.kablink.teaming.exception.UncheckedCodedException#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		return ApiErrorCode.USER_EXISTS;
	}
}
