package com.sitescape.ef.repository;

import com.sitescape.ef.exception.UncheckedException;

public class RepositoryException extends UncheckedException {
	
	public RepositoryException() {
        super();
    }
    public RepositoryException(String message) {
        super(message);
    }
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
    public RepositoryException(Throwable cause) {
        super(cause);
    }

}
