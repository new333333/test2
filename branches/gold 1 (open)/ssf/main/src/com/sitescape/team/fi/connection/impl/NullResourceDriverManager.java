package com.sitescape.team.fi.connection.impl;

import java.util.ArrayList;
import java.util.List;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;
import com.sitescape.team.fi.connection.ResourceDriver;
import com.sitescape.team.fi.connection.ResourceDriverManager;
import com.sitescape.team.fi.connection.ResourceSession;

public class NullResourceDriverManager implements ResourceDriverManager {

	public List<ResourceDriver> getResourceDrivers() {
		return new ArrayList<ResourceDriver>();
	}

	public List<ResourceDriver> getAllowedResourceDrivers() {
		return new ArrayList<ResourceDriver>();
	}

	public String normalizedResourcePath(String driverName, String parentResourcePath, String resourceName) throws FIException {
		return null;
	}

	public String normalizedResourcePath(String driverName, String resourcePath) throws FIException {
		return null;
	}

	public ResourceSession getSession(String driverName) throws FIException, UncheckedIOException {
		return null;
	}

	public ResourceSession getSession(String driverName, String initialResourcePath) throws FIException, UncheckedIOException {
		return null;
	}

	public String getName(String driverName, String resourcePath) throws FIException {
		return null;
	}
}
