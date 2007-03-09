package com.sitescape.team.ssfs;

public class AlreadyExistsException extends RuntimeException {

	public AlreadyExistsException() {
	}
	
	public AlreadyExistsException(String msg) {
		super(msg);
	}
}
