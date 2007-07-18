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
public class NoConfigurationByTheIdException extends NoObjectByTheIdException {
	   private static final String NoConfigurationByTheIdException_ErrorCode = "errorcode.no.configuration.by.the.id";
	    
	    public NoConfigurationByTheIdException(String defId) {
	        super(NoConfigurationByTheIdException_ErrorCode, defId);
	    }
	    public NoConfigurationByTheIdException(String defId, String message) {
	        super(NoConfigurationByTheIdException_ErrorCode, defId, message);
	    }
	    public NoConfigurationByTheIdException(String defId, String message, Throwable cause) {
	        super(NoConfigurationByTheIdException_ErrorCode,defId, message, cause);
	    }
	    public NoConfigurationByTheIdException(String defId, Throwable cause) {
	        super(NoConfigurationByTheIdException_ErrorCode, defId, cause);
	    }
}
