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
package com.sitescape.team.security.authentication;

public class DigestDoesNotMatchException extends AuthenticationException {

	private static final long serialVersionUID = 1L;
	
	public DigestDoesNotMatchException() {
        super();
    }
    public DigestDoesNotMatchException(String message) {
        super(message);
    }
    public DigestDoesNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
    public DigestDoesNotMatchException(Throwable cause) {
        super(cause);
    }

}
