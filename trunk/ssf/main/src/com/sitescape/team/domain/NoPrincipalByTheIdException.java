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
/*
 * Created on Nov 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import com.sitescape.team.NoObjectByTheIdException;

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

