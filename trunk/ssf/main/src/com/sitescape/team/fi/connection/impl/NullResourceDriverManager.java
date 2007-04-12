package com.sitescape.team.fi.connection.impl;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;
import com.sitescape.team.fi.connection.ResourceDriverManager;
import com.sitescape.team.fi.connection.ResourceSession;

public class NullResourceDriverManager implements ResourceDriverManager {

	public String getResourcePath(String driverName, String parentResourcePath, String resourceName) throws FIException {
		return null;
	}

	public ResourceSession getSession(String driverName) throws FIException, UncheckedIOException {
		return null;
	}

	public ResourceSession getSession(String driverName, String initialResourcePath) throws FIException, UncheckedIOException {
		return null;
	}

}
