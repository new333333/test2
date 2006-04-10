package com.sitescape.ef.ssfs.wck;

public class AlreadyExistsException extends RuntimeException {

	public AlreadyExistsException() {
	}
	
	public AlreadyExistsException(String msg) {
		super(msg);
	}
}
