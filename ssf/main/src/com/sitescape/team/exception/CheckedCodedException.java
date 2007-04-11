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
package com.sitescape.team.exception;

import com.sitescape.team.util.NLT;

/**
 * @author Jong Kim
 *
 */
public abstract class CheckedCodedException extends CheckedException implements ErrorCodeSupport {
    private String errorCode;
    private Object[] errorArgs;
    
    public CheckedCodedException(String errorCode) {
    	super();
    	setErrorCode(errorCode);
    }
    
    public CheckedCodedException(String errorCode, Object[] errorArgs) {
        super();
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public CheckedCodedException(String errorCode, Object[] errorArgs, String message) {
        super(message);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public CheckedCodedException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
        super(message, cause);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public CheckedCodedException(String errorCode, Object[] errorArgs, Throwable cause) {
        super(cause);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }

    public String getLocalizedMessage() {
    	return NLT.get(getErrorCode(), getErrorArgs());
    }
    
    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getErrorArgs() {
        return errorArgs;
    }
    
    public void setErrorArgs(Object[] errorArgs) {
        this.errorArgs = errorArgs;
    }

    protected void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
