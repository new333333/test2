/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.security;

import java.io.PrintStream;
import java.io.PrintWriter;
import com.sitescape.team.exception.UncheckedCodedException;

/**
 * Thrown to indicate that access is denied.
 * 
 * @author Jong Kim
 */
public abstract class AccessControlException extends UncheckedCodedException {
    public AccessControlException(String errorCode, Object[] errorArgs) {
    	super(errorCode, errorArgs);
    }
    //overload to remove stack trace filling log files
    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
    public void printStackTrace(PrintStream s) {
    	
    }
    //overload to remove stack trace filling log files
    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
    public void printStackTrace(PrintWriter s) {
    	
    }

}
