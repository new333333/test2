package com.sitescape.ef.ssfs.wck;

public class NoAccessException extends RuntimeException {

	public NoAccessException() {
	}
	
	public NoAccessException(String msg) {
		super(msg);
	}
}
