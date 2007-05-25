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
package com.sitescape.team.pipeline;

import com.sitescape.team.exception.UncheckedException;

public class PipelineException extends UncheckedException {

	public PipelineException() {
        super();
    }
    public PipelineException(String message) {
        super(message);
    }
    public PipelineException(String message, Throwable cause) {
        super(message, cause);
    }
    public PipelineException(Throwable cause) {
        super(cause);
    }

}
