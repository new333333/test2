/*
 * Created on Nov 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

/**
 * @author janet
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NoPrincipalByTheIdException extends NoObjectByTheIdException {
    private static final String NoPrincipalByTheIdException_ErrorCode = "errorcode.no.principal.by.the.id";
    
    public NoPrincipalByTheIdException(Long userId) {
        super(NoPrincipalByTheIdException_ErrorCode, userId);
    }
    public NoPrincipalByTheIdException(Long userId, String message) {
        super(NoPrincipalByTheIdException_ErrorCode, userId, message);
    }
    public NoPrincipalByTheIdException(Long userId, String message, Throwable cause) {
        super(NoPrincipalByTheIdException_ErrorCode,userId, message, cause);
    }
    public NoPrincipalByTheIdException(Long userId, Throwable cause) {
        super(NoPrincipalByTheIdException_ErrorCode, userId, cause);
    }

}

