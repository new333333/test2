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

import java.util.Date;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.InitializeException;
import org.kablink.teaming.fi.ShutdownException;


/**
 * Resource driver interface for mirrored folders.
 * 
 * @author jong
 *
 */
public interface ResourceDriver {
	
	/**
	 * Initialize the driver.
	 * 
	 * @throws InitializeException
	 * @throws UncheckedIOException
	 */
	public void initialize() throws InitializeException, UncheckedIOException;

	/**
	 * Shutdown the driver. 
	 * 
	 * All system resources and network connections must be closed and released.
	 *
	 */
	public void shutdown() throws ShutdownException, UncheckedIOException;
	
	/**
	 * Return the name of the driver instance. This name must be unique within a Vibe installation.
	 * 
	 * Note that Vibe resource driver manager uses this name not the name of the corresponding 
	 * Spring bean when locating drivers in the system.
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * Return the display title of the driver instance.
	 * 
	 * The title may or may not be localized depending on the driver implementation.
	 * 
	 * @return
	 */
	public String getTitle();

	/**
	 * Return title and mode as a single string.
	 * 
	 * @return
	 */
	public String getTitleAndMode();

	/**
	 * Return the ID of the zone in which this driver belongs and is configured.
	 * 
	 * @return
	 */
	public String getZoneId();
	
	/**
	 * Return the type of the driver.
	 *  
	 * @return
	 */
	public String getType();
	
	/**
	 * Return the resource root path configured for the driver.
	 * 
	 * The actual syntax of the root path will depend on the driver implementation.
	 * Vibe resource driver manager does not interpret the path specification.
	 * The value is meaningful only to the resource implementation.
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
	 * @param parentResourceNormalizedPath normalized parent path, must be non-null.
	 * @param resourceName name of the resource
	 * @return normalized path of the resource
	 * @throws FIException
	 */
	public String normalizedResourcePath(String parentResourceNormalizedPath, String resourceName) throws FIException;
	
	/**
	 * Returns normalized path of the specified resource.
	 * 
	 * @param resourcePath resource path that isn't necessarily normalized
	 * @return normalized path of the resource
	 * @throws FIException
	 */
	public String normalizedResourcePath(String resourcePath) throws FIException;
	
	/**
	 * Returns normalized path of the parent of the specified resource or <code>null</code>
	 * if the resource doesn't have a parent.
	 * 
	 * @param normalizedResourcePath normalized resource path
	 * @return
	 * @throws FIException
	 */
	public String getParentResourcePath(String normalizedResourcePath) throws FIException;
	
	/**
	 * Return the last element name of the resource identified by the path.
	 * 
	 * @param normalizedResourcePath 
	 * @return
	 */
	public String getResourceName(String normalizedResourcePath) throws FIException;
	
	/**
	 * Returns whether the driver is read-only.
	 * 
	 * If the driver is read-only, Vibe can only read resources through the driver
	 * but can not make any modifications.
	 * 
	 * @return
	 */
	public boolean isReadonly();
	
	/**
	 * Returns whether the top-level deletion should be synchronized.
	 * 
	 * @return
	 */
	public boolean getSynchTopDelete();
	
	/**
	 * Returns the date that the driver config was last set.
	 * This is used to figure out if the driver needs to be re-initialized.
	 * 
	 * @return
	 */
	public Date getModifiedOn();

	/**
	 * Returns resource driver config object associated with this driver instance
	 * or <code>null</code> if this driver instance was not created from a config object.
	 * 
	 * @return
	 */
	public ResourceDriverConfig getConfig();
}
