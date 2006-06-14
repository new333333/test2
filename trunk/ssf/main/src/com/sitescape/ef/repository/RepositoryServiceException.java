package com.sitescape.ef.repository;

import com.sitescape.ef.exception.UncheckedException;

public class RepositoryServiceException extends UncheckedException {
	
	public RepositoryServiceException() {
        super();
    }
    public RepositoryServiceException(String message) {
        super(message);
    }
    public RepositoryServiceException(String message, Throwable cause) {
        super(message, cause);
    }
    public RepositoryServiceException(Throwable cause) {
        super(cause);
    }

}
