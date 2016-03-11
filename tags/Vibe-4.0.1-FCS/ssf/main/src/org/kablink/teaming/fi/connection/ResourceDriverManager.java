/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.util.List;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.acl.AclItemPrincipalMappingException;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;

/**
 * ?
 * 
 * @author ?
 */
public interface ResourceDriverManager {
	public enum FileOperation {
		CREATE_FILE,
		CREATE_FOLDER,
		READ,
		UPDATE,
		DELETE,
		MOVE_FILE,
		MOVE_FOLDER
	}
	
	public List<ResourceDriver> getAllowedResourceDrivers();
	public List<ResourceDriverConfig> getAllResourceDriverConfigs();
	public List<ResourceDriverConfig> getAllNetFolderResourceDriverConfigs();
	public List<ResourceDriverConfig> getAllCloudFolderResourceDriverConfigs();
	
	/**
	 * Load and initialize resource driver list. Used during server start.
	 */
	public void initializeResourceDriverList();
	
	public ResourceDriver getDriver(String driverName) throws FIException;
	public ResourceDriver getDriver(Long driverId) throws FIException;
	public ResourceDriver getStaticDriverByNameHash(Long nameHash) throws FIException;
	
	public ResourceDriverConfig getDriverConfig(String driverName);
	public ResourceDriverConfig getDriverConfig(Long id);

	public ResourceSession getSession(ResourceDriver driver, FileOperation fileOperation, DefinableEntity ... entitiesToCheckPermissionOn)
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

	/**
	 * Create a resource driver from the config object.
	 * This is used for testing purpose only.
	 * 
	 * @param config
	 * @return
	 */
	public ResourceDriver createResourceDriver(ResourceDriverConfig config);

	public ResourceDriver createResourceDriverWithoutInitialization(ResourceDriverConfig config);

	public AclResourceSession openSessionWithAuth(AclResourceDriver authSupportingAclResourceDriver, Long netFolderOwnerDbId);
	
	public AclResourceSession openSessionUserMode(AclResourceDriver driver)  throws AclItemPrincipalMappingException;

	/**
	 * Inform system cluster-wide of the fact that some sort of resource driver change was made 
	 * from the node executing this method.
	 * Any code that adds/deletes/modifies resource driver (= net folder server) MUST call this method.
	 */
	public void informResourceDriverChangeFromThisNode();
}
