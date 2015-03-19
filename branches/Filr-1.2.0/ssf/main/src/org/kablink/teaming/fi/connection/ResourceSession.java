/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.fi.connection;

import java.io.InputStream;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.fi.FIException;


public interface ResourceSession {

	/**
	 * Return path string, or <code>null</code> if path is not set.
	 * 
	 * @return
	 */
	public String getPath();
	
	/**
	 * Returns handle, or <code>null</code> if handle is not set.
	 * 
	 * @return
	 */
	public String getHandle();
	
	/**
	 * Return the last element name of the path, or <code>null</code> if path is not set.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Set the path.
	 * 
	 * @param resourcePath (required) path information according to conventional hierarchical file system view
	 * @param resourceHandle (optional) a opaque handle uniquely identifying the resource
	 * @param isDir (optional) whether the resource refers to a directory or a file
	 * @return
	 * @throws IOException 
	 */
	public ResourceSession setPath(String resourcePath, String resourceHandle, Boolean isDir) throws UncheckedIOException;
	
	/**
	 * Set the path.
	 * 
	 * @param parentResourcePath (required) path of the parent resource according to conventional hierarchical file system view,
	 * parent resource always refers to a directory
	 * @param parentResourceHandle (optional) a opaque handle uniquely identifying the parent resource
	 * @param childResourceName (required) name of the child resource
	 * @param childResourceHandle (optional) a opaque handle uniquely identifying the child resource
	 * @param isDir (optional) whether the child resource refers to a directory or a file
	 * @return
	 */
	public ResourceSession setPath(String parentResourcePath, String parentResourceHandle, String childResourceName, String childResourceHandle, Boolean isDir) throws UncheckedIOException;
	
	/**
	 * Close the session.
	 */
	public void close();
	
	/**
	 * Return last modified time associated with the resource.
	 * 
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 * @throws IllegalStateException
	 */
	public long lastModified() throws FIException, UncheckedIOException, IllegalStateException;

	/**
	 * Return whether the resource exists or not. 
	 */
	public boolean exists() throws FIException, UncheckedIOException, IllegalStateException;

	/**
	 * Return whether the resource is a directory or a file.
	 * 
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 * @throws IllegalStateException
	 */
	public boolean isDirectory() throws FIException, UncheckedIOException, IllegalStateException;
	
	/**
	 * Return <code>InputStream</code> on the content of the file resource.
	 * 
	 * @return
	 * @throws FIException
	 * @throws UncheckedIOException
	 * @throws IllegalStateException
	 */
	public InputStream readFile() throws FIException, UncheckedIOException, IllegalStateException;
	
	/**
	 * Write new content of the file resource. 
	 * Create the resource if it doesn't already exist. 
	 * 
	 * @param in input stream
	 * @param size content size in byte
	 * @param lastModTime last modification time or <code>null</code>
	 * @throws FIException
	 * @throws UncheckedIOException
	 * @throws IllegalStateException
	 */
	public void writeFile(InputStream in, long size, Long lastModTime) throws FIException, UncheckedIOException, IllegalStateException;
	
	/**
	 * Create a directory. 
	 * 
	 * @throws FIException
	 * @throws UncheckedIOException
	 * @throws IllegalStateException
	 */
	public void createDirectory() throws FIException, UncheckedIOException, IllegalStateException;
	
	public void delete() throws FIException, UncheckedIOException, IllegalStateException;
	
	/**
	 * Return the names of the resources (directories and files) in the 
	 * current directory. Each string represents only the name part rather than
	 * a complete path for the resource. The array will be empty if the directory 
	 * is empty. 
	 * Returns null if the path does not denote a directory, or an I/O error occurs. 
	 * 
	 * @return
	 * @throws FIException
	 * @throws IllegalStateException If the path is not set, etc.
	 */
	public String[] listNames() throws FIException, IllegalStateException;

	/**
	 * Return the resource driver that allocated this session. 
	 * Convenience method.
	 * 
	 * @return
	 */
	public ResourceDriver getResourceDriver();
	
	/**
	 * Return the length (in byte) of the content of the file resource.
	 * 
	 * @return
	 * @throws FIException
	 * @throws IllegalStateException
	 */
	public long getContentLength() throws FIException, IllegalStateException;
	
	/**
	 * Move current resource (which may be either directory or file) into the
	 * specified <code>targetDirectoryResourcePath</code> target directory. 
	 * The <code>targetName</code> defines new name of the resource being moved.
	 * 
	 * @param targetDirectoryResourcePath
	 * @param targetDirectoryResourceHandle
	 * @param targetName
	 * @param targetName
	 * @throws FIException
	 * @throws IllegalStateException
	 */
	public void move(String targetDirectoryResourcePath, String targetDirectoryResourceHandle, String targetName) throws FIException, IllegalStateException;
}
