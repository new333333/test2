package com.sitescape.ef.ssfs.wck;

public class AlreadyExistsException extends Exception {

	public AlreadyExistsException() {
	}
	
	public AlreadyExistsException(String msg) {
		super(msg);
	}
}
