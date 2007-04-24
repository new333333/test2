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
package com.sitescape.team.modelprocessor;

import com.sitescape.team.ConfigurationException;

/**
 *
 * @author Jong Kim
 */
public class ProcessorNotFoundException extends ConfigurationException {
    public ProcessorNotFoundException() {
        super();
    }
    public ProcessorNotFoundException(String message) {
        super(message);
    }
    public ProcessorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public ProcessorNotFoundException(Throwable cause) {
        super(cause);
    }
}
