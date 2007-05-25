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
package com.sitescape.team.util;

import com.sitescape.team.exception.UncheckedCodedException;

public class ConfigPropertyNotFoundException extends UncheckedCodedException {
 	private static final long serialVersionUID = 1L;
	private static final String ConfigPropertyNotFoundException_ErrorCode = "errorcode.property.not.found";

	public ConfigPropertyNotFoundException(String keyName) {
		super(ConfigPropertyNotFoundException_ErrorCode, new String[]{keyName});
	}
}
