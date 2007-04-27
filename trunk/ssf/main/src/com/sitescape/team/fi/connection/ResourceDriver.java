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
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the type of the driver.
	 *  
	 * @return
	 */
	public String getType();
	
	/**
	 * Open a session.
	 * 
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public ResourceSession openSession() throws FIException, UncheckedIOException;
	
	public String getResourcePath(String parentResourcePath, String resourceName);
}
