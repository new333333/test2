package com.sitescape.ef.ssfs.wck;

public class NoSuchObjectException extends RuntimeException {

	public NoSuchObjectException() {
	}
	
	public NoSuchObjectException(String msg) {
		super(msg);
	}
}
