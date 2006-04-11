package com.sitescape.ef.ssfs;

public class NoAccessException extends RuntimeException {

	public NoAccessException() {
	}
	
	public NoAccessException(String msg) {
		super(msg);
	}
}
