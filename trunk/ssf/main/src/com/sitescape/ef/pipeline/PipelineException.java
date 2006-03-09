package com.sitescape.ef.pipeline;

import com.sitescape.ef.exception.UncheckedException;

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
