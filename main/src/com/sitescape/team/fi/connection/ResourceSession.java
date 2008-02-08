/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
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
	 * @throws IOException 
	 */
	public ResourceSession setPath(String resourcePath) throws UncheckedIOException;
	
	/**
	 * Set the path.
	 * @param parentResourcePath
	 * @param childName
	 * @return
	 */
	public ResourceSession setPath(String parentResourcePath, String childName) throws UncheckedIOException;
	
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
	
	//public String[] listPaths() throws FIException, IllegalStateException;
	
	public ResourceDriver getDriver();
	
	public long getContentLength() throws FIException, IllegalStateException;
	
	/**
	 * Move current resource (which may be either directory or file) into the
	 * specified target directory. The <code>targetName</code> defines new
	 * name of the resource being moved.
	 * @param targetDirectoryResourcePath
	 * @param targetName
	 * @param targetName
	 * @throws FIException
	 * @throws IllegalStateException
	 */
	public void move(String targetDirectoryResourcePath, String targetName) throws FIException, IllegalStateException;
}
