package com.sitescape.team.fi.connection;

import java.io.InputStream;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.fi.FIException;

public interface ResourceSession {

	/**
	 * Return path string, or <code>null</code> if path is not set.
	 * @return
	 */
	public String getPath();
	
	/**
	 * Return the last element name of the path, or <code>null</code> 
	 * if path is not set.
	 * @return
	 */
	public String getName();
	
	/**
	 * Set the path.
	 * @param resourcePath
	 * @return
	 */
	public ResourceSession setPath(String resourcePath);
	
	/**
	 * Set the path.
	 * @param parentResourcePath
	 * @param childName
	 * @return
	 */
	public ResourceSession setPath(String parentResourcePath, String childName);
	
	/**
	 * Close the session.
	 */
	public void close();
	
	/**
	 * Return last modified time.
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 * @throws IllegalStateException
	 */
	public long lastModified() throws FIException, UncheckedIOException, IllegalStateException;

	public boolean exists() throws FIException, UncheckedIOException, IllegalStateException;

	public boolean isDirectory() throws FIException, UncheckedIOException, IllegalStateException;
	
	public InputStream readFile() throws FIException, UncheckedIOException, IllegalStateException;
	
	public void writeFile(InputStream in) throws FIException, UncheckedIOException, IllegalStateException;
	
	public void createDirectory() throws FIException, UncheckedIOException, IllegalStateException;
	
	public void delete() throws FIException, UncheckedIOException, IllegalStateException;
	
	/**
	 * Returns an array of strings naming the files and directories in the 
	 * current directory. Each string is a file name rather than a complete 
	 * path. The array will be empty if the directory is empty. Returns null 
	 * if this does not denote a directory, or an I/O error occurs. 
	 * 
	 * @return
	 * @throws FIException
	 * @throws IllegalStateException
	 */
	public String[] listNames() throws FIException, IllegalStateException;
	
	public String[] listPaths() throws FIException, IllegalStateException;
	
	public ResourceDriver getDriver();
	
	public boolean makeDirectory() throws FIException, IllegalStateException;
	
	public long getContentLength() throws FIException, IllegalStateException;
	
	public void moveFile(String targetDirectoryResourcePath, String targetFileName) throws FIException, IllegalStateException;
}
