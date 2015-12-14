/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.kablink.util.VibeRuntimeException;
import org.kablink.util.api.ApiErrorCode;

/**
 * This class overrides the default behavior of <code>VibeRuntimeException</code> such that
 * handling of stack trace is ignored.
 * 
 * This wrapper is used to work around the situation where some framework code that we don't have
 * direct control over attempts to print (big and ugly) stack trace when it is not desirable in
 * our application.
 * 
 * @author jong
 *
 */
public class NoStackTraceWrapperRuntimeException extends VibeRuntimeException {

	private Exception wrappedException;
	
	public NoStackTraceWrapperRuntimeException(Exception wrappedException) {
		this.wrappedException = wrappedException;
	}

    public String getMessage() {
        return wrappedException.getMessage();
    }

    public String getLocalizedMessage() {
        return wrappedException.getLocalizedMessage();
    }

    public Throwable getCause() {
        return wrappedException.getCause();
    }

    public Throwable initCause(Throwable cause) {
    	return wrappedException.initCause(cause);
    }

    public String toString() {
    	return wrappedException.toString();
    }

    public void printStackTrace() { 
    	// This is noop!
    }

    public void printStackTrace(PrintStream s) {
    	// This is noop!
    }

    public void printStackTrace(PrintWriter s) { 
    	// This is noop!
    }

    public Throwable fillInStackTrace() {
    	// Don't fill in, since this is merely to wrap a real one.
    	return this;
    }

    public StackTraceElement[] getStackTrace() {
    	return wrappedException.getStackTrace();
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
    	wrappedException.setStackTrace(stackTrace);
    }

	/* (non-Javadoc)
	 * @see org.kablink.util.VibeRuntimeException#getHttpStatusCode()
	 */
	@Override
	public int getHttpStatusCode() {
		if(wrappedException instanceof VibeRuntimeException)
			return ((VibeRuntimeException) wrappedException).getHttpStatusCode();
		else
			return 500; // Internal Server Error
	}

	protected Exception getWrappedException() {
		return wrappedException;
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.exception.ApiErrorCodeSupport#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		if(wrappedException instanceof VibeRuntimeException)
			return ((VibeRuntimeException) wrappedException).getApiErrorCode();
		else
			return ApiErrorCode.SERVER_ERROR;
	}
}
