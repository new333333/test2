package com.sitescape.team.module.zone;

public class ZoneException extends RuntimeException {

	public ZoneException(String message) {
		super(message);
	}
	
	public ZoneException(Throwable cause) {
		super(cause);
	}
	
	public ZoneException(String message, Throwable cause) {
		super(message, cause);
	}
}

