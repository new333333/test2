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

import com.sitescape.team.exception.UncheckedException;

/**
 * Same as <code>AccessControlException</code> except that this is not a
 * <i>coded</i> exception.
 * 
 * @author Jong Kim
 */
public class AccessControlNonCodedException extends UncheckedException {
    
    public AccessControlNonCodedException() {
        super();
    }
    public AccessControlNonCodedException(String message) {
        super(message);
    }
    public AccessControlNonCodedException(String message, Throwable cause) {
        super(message, cause);
    }
    public AccessControlNonCodedException(Throwable cause) {
        super(cause);
    }
}
