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
	 * Returns normalized resource path of the child.
	 *  
	 * @param driverName
	 * @param parentResourcePath parent's normalized resource path
	 * @param resourceName resource name of the child
	 * @return normalized path of the child
	 * @throws FIException
	 */
	public String normalizedResourcePath(String driverName, String parentResourcePath, 
			String resourceName) throws FIException;
	
	/**
	 * Returns normalized path of the specified resource.
	 * 
	 * @param driverName
	 * @param resourcePath resource path that isn't necessarily normalized
	 * @return normalized path of the resource
	 * @throws FIException
	 */
	public String normalizedResourcePath(String driverName, String resourcePath) throws FIException;
	
	/**
	 * Return the last element name of the path.
	 * @param driverName
	 * @param resourcePath 
	 * @return
	 */
	public String getName(String driverName, String resourcePath) throws FIException;
}
