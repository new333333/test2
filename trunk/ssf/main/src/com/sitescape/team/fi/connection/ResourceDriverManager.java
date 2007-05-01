package com.sitescape.team.fi.connection;

import java.util.List;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;

public interface ResourceDriverManager {

	public List<ResourceDriver> getResourceDrivers();

	public ResourceSession getSession(String driverName)
	throws FIException, UncheckedIOException;
	
	public ResourceSession getSession(String driverName, String initialResourcePath) 
	throws FIException, UncheckedIOException;
	
	/**
	 * 
	 * @param driverName
	 * @param parentResourcePath may be <code>null</code>
	 * @param resourceName
	 * @return
	 * @throws FIException
	 */
	public String getResourcePath(String driverName, String parentResourcePath, 
			String resourceName) throws FIException;
}
