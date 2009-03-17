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

import java.util.List;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.fi.FIException;


public interface ResourceDriverManager {

	public List<ResourceDriver> getAllowedResourceDrivers();
	
	public ResourceDriver getDriver(String driverName) throws FIException;

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
	
	/**
	 * Returns whether the specified driver is read-only.
	 * @param driverName
	 * @return
	 * @throws FIException
	 */
	public boolean isReadonly(String driverName) throws FIException;
}
