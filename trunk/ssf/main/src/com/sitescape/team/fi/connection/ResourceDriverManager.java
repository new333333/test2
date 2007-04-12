package com.sitescape.team.fi.connection;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;

public interface ResourceDriverManager {

	public ResourceSession getSession(String driverName)
	throws FIException, UncheckedIOException;
	
	public ResourceSession getSession(String driverName, String initialResourcePath) 
	throws FIException, UncheckedIOException;
	
	public String getResourcePath(String driverName, String parentResourcePath, 
			String resourceName) throws FIException;
}
