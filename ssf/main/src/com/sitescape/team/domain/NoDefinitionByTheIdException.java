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

import com.sitescape.team.NoObjectByTheIdException;

/**
 * @author Janet McCann
 *
 */
public class NoDefinitionByTheIdException extends NoObjectByTheIdException {
	   private static final String NoDefinitionByTheIdException_ErrorCode = "errorcode.no.definition.by.the.id";
	    
	    public NoDefinitionByTheIdException(String defId) {
	        super(NoDefinitionByTheIdException_ErrorCode, defId);
	    }
	    public NoDefinitionByTheIdException(String defId, String message) {
	        super(NoDefinitionByTheIdException_ErrorCode, defId, message);
	    }
	    public NoDefinitionByTheIdException(String defId, String message, Throwable cause) {
	        super(NoDefinitionByTheIdException_ErrorCode,defId, message, cause);
	    }
	    public NoDefinitionByTheIdException(String defId, Throwable cause) {
	        super(NoDefinitionByTheIdException_ErrorCode, defId, cause);
	    }
}
