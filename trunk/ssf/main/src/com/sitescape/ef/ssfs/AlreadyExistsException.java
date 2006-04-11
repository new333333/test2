package com.sitescape.ef.ssfs;

public class AlreadyExistsException extends RuntimeException {

	public AlreadyExistsException() {
	}
	
	public AlreadyExistsException(String msg) {
		super(msg);
	}
}
