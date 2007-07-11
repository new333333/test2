package com.sitescape.team.fi.connection;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;

public interface ResourceDriver {

	/**
	 * Initialize the driver. 
	 * 
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public void initialize() throws FIException, UncheckedIOException;
	
	/**
	 * Shutdown the driver.
	 *
	 */
	public void shutdown();
	
	/**
	 * Return the name of the driver instance.
	 * Note: Driver manager uses this name not the name of the corresponding
	 * bean to locate drivers in the system. 
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the title of the driver instance. 
	 * The title may or may not be localized depending on the driver implementation.
	 * 
	 * @return
	 */
	public String getTitle();
	
	/**
	 * Return the type of the driver.
	 * This is informational purpose only.
	 *  
	 * @return
	 */
	public String getType();
	
	/**
	 * Return the root path configured for the driver.
	 * 
	 * @return
	 */
	public String getRootPath();
	
	/**
	 * Open a session.
	 * 
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public ResourceSession openSession() throws FIException, UncheckedIOException;
	
	/**
	 * Returns normalized resource path given its parent path and the resource name.
	 * 
	 * @param parentResourcePath normalized parent path, must be non-null.
	 * @param resourceName
	 * @return normalized path of the resource
	 * @throws FIException
	 */
	public String normalizedResourcePath(String parentResourcePath, String resourceName) throws FIException;
	
	/**
	 * Returns normalized path of the specified resource.
	 * 
	 * @param resourcePath resource path that isn't necessarily normalized
	 * @return normalized path of the resource
	 * @throws FIException
	 */
	public String normalizedResourcePath(String resourcePath) throws FIException;
	
	/**
	 * Return the last element name of the path.
	 * @param resourcePath 
	 * @return
	 */
	public String getName(String resourcePath) throws FIException;
	
	/**
	 * Returns whether the driver is read-only.
	 * @return
	 */
	public boolean isReadonly();
}
