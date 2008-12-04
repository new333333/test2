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
package org.kablink.teaming.fi.connection;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.fi.FIException;


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
	
	public String getTitleAndMode();
	
	public String getZoneName();
	
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
