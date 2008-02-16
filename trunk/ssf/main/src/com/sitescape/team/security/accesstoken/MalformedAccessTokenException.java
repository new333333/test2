package com.sitescape.team.security.accesstoken;

public class MalformedAccessTokenException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MalformedAccessTokenException() {
		super();
	}
	
	public MalformedAccessTokenException(String msg) {
		super(msg);
	}

}
