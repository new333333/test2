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
package com.sitescape.team.domain;
import com.sitescape.team.exception.UncheckedCodedException;
/**
  *
 * @author  Peter Hurley
 * @version $Revision: 1.0 $
 *
 */
public class DefinitionInvalidException extends UncheckedCodedException {
    public DefinitionInvalidException(String errorCode) {
    	super(errorCode);
    }
    public DefinitionInvalidException(String errorCode, Object[] errorArgs) {
        super(errorCode, errorArgs);
    }
    public DefinitionInvalidException(String errorCode, Object[] errorArgs, String message) {
        super(errorCode, errorArgs, message);
    }
    public DefinitionInvalidException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
        super(errorCode, errorArgs, message, cause);
    }
    public DefinitionInvalidException(String errorCode, Object[] errorArgs, Throwable cause) {
        super(errorCode, errorArgs, cause);
    }
}