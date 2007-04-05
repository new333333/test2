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

import com.sitescape.team.ConfigurationException;

public class ConfigPropertyNotFoundException extends ConfigurationException {
    public ConfigPropertyNotFoundException() {
        super();
    }
    public ConfigPropertyNotFoundException(String message) {
        super(message);
    }
    public ConfigPropertyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ConfigPropertyNotFoundException(Throwable cause) {
        super(cause);
    }

}
