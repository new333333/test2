package com.sitescape.team.ssfs;

public class NoAccessException extends RuntimeException {

	public NoAccessException() {
	}
	
	public NoAccessException(String msg) {
		super(msg);
	}
}
