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

/**
 * @author Jong Kim
 *
 */
public class ConfigurationException extends UncheckedCodedException {
 	private static final long serialVersionUID = 1L;
	private static final String ConfigurationException_ErrorCode = "errorcode.configuration";

	public ConfigurationException(String detailMessage) {
		super(ConfigurationException_ErrorCode, new String[]{detailMessage}); // Use default code
	}
	public ConfigurationException(String detailMessage, Throwable cause) {
		super(ConfigurationException_ErrorCode, new String[]{detailMessage}, cause); // Use default code
	}
    public ConfigurationException(String errorCode, Object[] errorArgs) {
        super(errorCode, errorArgs);
    }
}
