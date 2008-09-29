package com.sitescape.team.module.zone;

public class ZoneException extends RuntimeException {

	private static final long serialVersionUID = -2679874893730279688L;

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

