/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import org.kablink.teaming.exception.UncheckedCodedException;
import org.kablink.util.api.ApiErrorCode;

/**
 * ?
 * 
 * @author ?
 */
public class PasswordMismatchException extends UncheckedCodedException {
	private List<String> m_passwordPolicyViolations;	// If the exception is because of a password policy violation, contains the reasons why.
	
    public PasswordMismatchException(String errorCode) {
        super(errorCode);
    }
    public PasswordMismatchException(String errorCode, List<String> passwordPolicyViolations) {
        super(errorCode);
        setPasswordPolicyViolations(passwordPolicyViolations);
    }
    
    public PasswordMismatchException(String errorCode, Object[] errorArgs) {
        super(errorCode, errorArgs);
    }
    public PasswordMismatchException(String errorCode, Object[] errorArgs, List<String> passwordPolicyViolations) {
        super(errorCode, errorArgs);
        setPasswordPolicyViolations(passwordPolicyViolations);
    }
    
    public PasswordMismatchException(String errorCode, Object[] errorArgs, String message) {
        super(errorCode, errorArgs, message);
    }
    public PasswordMismatchException(String errorCode, Object[] errorArgs, String message, List<String> passwordPolicyViolations) {
        super(errorCode, errorArgs, message);
        setPasswordPolicyViolations(passwordPolicyViolations);
    }
    
    public PasswordMismatchException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
        super(errorCode, errorArgs, message, cause);
    }
    public PasswordMismatchException(String errorCode, Object[] errorArgs, String message, Throwable cause, List<String> passwordPolicyViolations) {
        super(errorCode, errorArgs, message, cause);
        setPasswordPolicyViolations(passwordPolicyViolations);
    }
    
    public PasswordMismatchException(String errorCode, Object[] errorArgs, Throwable cause) {
        super(errorCode, errorArgs, cause);
    }
    public PasswordMismatchException(String errorCode, Object[] errorArgs, Throwable cause, List<String> passwordPolicyViolations) {
        super(errorCode, errorArgs, cause);
        setPasswordPolicyViolations(passwordPolicyViolations);
    }

    /**
     * Set'er methods.
     * 
     * @return
     */
    public boolean      isPasswordPolicyViolation()   {return ((null != m_passwordPolicyViolations) && (!(m_passwordPolicyViolations.isEmpty())));}
    public List<String> getPasswordPolicyViolations() {return m_passwordPolicyViolations;                                                         }

    /**
     * Set'er methods.
     * 
     * @param
     */
    public void setPasswordPolicyViolations(List<String> passwordPolicyViolations) {m_passwordPolicyViolations = passwordPolicyViolations;}
    
    //overload to remove stack trace filling log files
    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
    @Override
	public void printStackTrace(PrintStream s) {
        synchronized (s) {
            s.println(toString());
        }
    }
    
    //overload to remove stack trace filling log files
    //This is because springs DispatcherPortlet calls the logger.warn method with the exception
    @Override
	public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            s.println(toString());
        }	

    }    
    @Override
	public int getHttpStatusCode() {
    	return 400; // Bad data
    }
    
	/* (non-Javadoc)
	 * @see org.kablink.teaming.exception.ApiErrorCodeSupport#getApiErrorCode()
	 */
	@Override
	public ApiErrorCode getApiErrorCode() {
		return ApiErrorCode.BAD_INPUT;
	}
}
