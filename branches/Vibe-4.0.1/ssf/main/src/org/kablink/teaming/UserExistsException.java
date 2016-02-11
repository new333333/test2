package org.kablink.teaming;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.kablink.util.api.ApiErrorCode;

public class UserExistsException extends ObjectExistsException {
	   
    private static final String userExistsException_ErrorCode = "errorcode.user.exists";

	public UserExistsException() {
        super(userExistsException_ErrorCode);
	}

	public UserExistsException(Throwable cause) {
        super(userExistsException_ErrorCode, null, cause);
	}

    public UserExistsException(Object[] errorArgs, Throwable cause) {
        super(userExistsException_ErrorCode, errorArgs, cause);    
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
    
	/* (non-Javadoc)
	 * @see org.kablink.teaming.exception.UncheckedCodedException#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		return ApiErrorCode.USER_EXISTS;
	}
}
