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
package org.kablink.teaming.fi.connection.impl;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.fi.connection.ResourceDriverManager;
import org.kablink.teaming.fi.connection.ResourceSession;
import org.kablink.teaming.fi.connection.acl.AclItemPrincipalMappingException;
import org.kablink.teaming.fi.connection.acl.AclResourceDriver;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;

/**
 * ?
 * 
 * @author ?
 */
public class NullResourceDriverManager implements ResourceDriverManager {
	@Override
	public List<ResourceDriverConfig> getAllResourceDriverConfigs() {
		return new ArrayList<ResourceDriverConfig>();
	}
	@Override
	public List<ResourceDriverConfig> getAllNetFolderResourceDriverConfigs() {
		return new ArrayList<ResourceDriverConfig>();
	}
	@Override
	public List<ResourceDriverConfig> getAllCloudFolderResourceDriverConfigs() {
		return new ArrayList<ResourceDriverConfig>();
	}
	@Override
	public void initializeResourceDriverList() {}

	@Override
	public String normalizedResourcePath(String driverName, String parentResourcePath, String resourceName) throws FIException {
		return null;
	}

	@Override
	public String normalizedResourcePath(String driverName, String resourcePath) throws FIException {
		return null;
	}

	public ResourceSession getSession(DefinableEntity entity, String driverName) throws FIException, UncheckedIOException {
		return null;
	}

	@Override
	public String getName(String driverName, String resourcePath) throws FIException {
		return null;
	}
	
	@Override
	public boolean isReadonly(String driverName) throws FIException {
		return true; // It doesn't matter what we return
	}

	@Override
	public ResourceDriver getDriver(String driverName) throws FIException {
		return null;
	}
	@Override
	public ResourceDriverConfig getDriverConfig(String driverName) {
		return null;
	}

    @Override
    public ResourceDriverConfig getDriverConfig(Long id) {
        return null;
    }

    @Override
	public ResourceSession getSession(ResourceDriver driver, FileOperation fileOperation, DefinableEntity ... entitiesToCheckPermissionOn)
			throws FIException, UncheckedIOException {
		return null;
	}

	@Override
	public ResourceDriver createResourceDriver(ResourceDriverConfig config) {
		return null;
	}

	@Override
	public AclResourceSession openSessionWithAuth(
			AclResourceDriver authSupportingAclResourceDriver,
			Long netFolderOwnerDbId) {
		return null;
	}

	@Override
	public AclResourceSession openSessionUserMode(AclResourceDriver driver)
			throws AclItemPrincipalMappingException {
		return null;
	}

	@Override
	public ResourceDriver createResourceDriverWithoutInitialization(
			ResourceDriverConfig config) {
		return null;
	}

	@Override
	public void informResourceDriverChangeFromThisNode() {
	}
	
	@Override
	public List<ResourceDriver> getAllowedResourceDrivers() {
		return null;
	}
	
	@Override
	public ResourceDriver getDriver(Long driverId) throws FIException {
		return null;
	}

	@Override
	public ResourceDriver getStaticDriverByNameHash(Long nameHash)
			throws FIException {
		return null;
	}
}
