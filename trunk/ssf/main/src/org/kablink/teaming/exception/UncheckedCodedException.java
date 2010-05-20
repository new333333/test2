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

import org.kablink.teaming.util.NLT;

/**
 * @author Jong Kim
 *
 */
public abstract class UncheckedCodedException extends RuntimeException implements ErrorCodeSupport {
    private String errorCode;
    private Object[] errorArgs;
    protected String msg;
    
    public UncheckedCodedException(String errorCode) {
    	super();
    	setErrorCode(errorCode);
    }
    public UncheckedCodedException(String errorCode, Object[] errorArgs) {
        super();
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public UncheckedCodedException(String errorCode, Object[] errorArgs, String message) {
        super(message);
        setMessage(message);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public UncheckedCodedException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
        super(message, cause);
        setMessage(message);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public UncheckedCodedException(String errorCode, Object[] errorArgs, Throwable cause) {
        super(cause);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }

    public String getLocalizedMessage() {
    	try {
    		String str = NLT.get(getErrorCode(), getErrorArgs());
    		if(str == null)
    			str = "";
    		if(msg != null) {
    			if(str.length() > 0)
    				str = str + ": " + msg;
    			else
    				str = msg;
    		}
    		return str;
    	}
    	catch(Exception e) {
    		return super.getLocalizedMessage();
    	}
    }
    public String getMessage() {
    	if (msg != null) return msg;
    	try {
    		return NLT.get(getErrorCode(), getErrorArgs());
    	}
    	catch(Exception e) {
    		return super.getMessage();
    	}
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
    private void setMessage(String message) {
    	this.msg = message;
    }
}
