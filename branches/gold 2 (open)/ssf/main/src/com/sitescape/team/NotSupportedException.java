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
package com.sitescape.team;
import com.sitescape.team.exception.UncheckedCodedException;

public class NotSupportedException extends UncheckedCodedException {
 	private static final long serialVersionUID = 1L;
	private static final String NotSupportedException_ErrorCode = "errorcode.not.supported";

	public NotSupportedException(String operation, String target) {
		this(NotSupportedException_ErrorCode, new String[] {operation, target}); // Use default code
	}
    public NotSupportedException(String errorCode) {
    	super(errorCode);
    }
    public NotSupportedException(String errorCode, Object[] errorArgs) {
        super(errorCode, errorArgs);
    }
}