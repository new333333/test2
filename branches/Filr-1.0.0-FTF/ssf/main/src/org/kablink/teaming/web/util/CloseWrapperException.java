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
package org.kablink.teaming.web.util;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.kablink.util.VibeRuntimeException;
import org.kablink.util.api.ApiErrorCode;

/**
 * This class is used to wrap another exception within the web tier so that the 
 * framework can direct rendering of the exception object to defCodedErrorClose.jsp 
 * view instead of the regular defCodedError.jsp view. 
 * Therefore this class is specifically designed for use by controllers only, and
 * should never be used for any other usage or in any other tiers.   
 * 
 * @author Jong Kim
 *
 */
public class CloseWrapperException extends VibeRuntimeException {
	private Exception wrappedExc;
	
	public CloseWrapperException(Exception wrappedExc) {
		super();
		this.wrappedExc = wrappedExc;
	}
		
	public Throwable getCause() {
		return wrappedExc.getCause();
	}
	
	public String getLocalizedMessage() {
		return wrappedExc.getLocalizedMessage();
	}
	
	public String getMessage() {
		return wrappedExc.getMessage();
	}
	
	public StackTraceElement[] getStackTrace() {
		return wrappedExc.getStackTrace();
	}
	
	public Throwable initCause(Throwable cause) {
		return wrappedExc.initCause(cause);
	}
	
	public void printStackTrace() {
		wrappedExc.printStackTrace();
	}
	
	public void printStackTrace(PrintStream s) {
		wrappedExc.printStackTrace(s);
	}
	
	public void printStackTrace(PrintWriter s) {
		wrappedExc.printStackTrace(s);
	}
	
	public void setStackTrace(StackTraceElement[] stackTrace) {
		wrappedExc.setStackTrace(stackTrace);
	}
	
	public String toString() {
		return wrappedExc.toString();
	}

	/* (non-Javadoc)
	 * @see org.kablink.util.VibeRuntimeException#getHttpStatusCode()
	 */
	@Override
	public int getHttpStatusCode() {
		if(wrappedExc instanceof VibeRuntimeException) {
			return ((VibeRuntimeException) wrappedExc).getHttpStatusCode();
		}
		else {
			return 500; // Internal Server Error
		}
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.exception.ApiErrorCodeSupport#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		if(wrappedExc instanceof VibeRuntimeException) {
			return ((VibeRuntimeException) wrappedExc).getApiErrorCode();
		}
		else {
			return ApiErrorCode.SERVER_ERROR;
		}
	}
}
