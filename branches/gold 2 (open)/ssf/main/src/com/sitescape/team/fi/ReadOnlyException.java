package com.sitescape.team.fi;

public class ReadOnlyException extends FIException {

	private static final long serialVersionUID = 1L;

	public ReadOnlyException(String driverName, String operationName) {
		super("Cannot execute " + operationName + ": Driver " + driverName + " is read-only");
	}
}
