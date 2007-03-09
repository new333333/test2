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
