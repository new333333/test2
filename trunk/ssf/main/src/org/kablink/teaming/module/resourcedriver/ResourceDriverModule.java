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
package org.kablink.teaming.module.resourcedriver;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.security.AccessControlException;

/**
 * ?
 * 
 * @author hurley
 */
@SuppressWarnings("unchecked")
public interface ResourceDriverModule {
	/**
	 * Routines to support the creation and management of resource drivers.
	 * These resource drivers are primarily used by mirrored folders
	 */
	public static String ReservedItemNames = " id type name owner popularity branding deleted ";
	
	public enum ResourceDriverOperation {
		manageResourceDrivers,
		createFilespace
	}

	/**
	 * This call checks the zone level access for resource drivers.
	 * @param operation
	 * @return
	 */
   	public boolean testAccess(ResourceDriverOperation operation);
   	public void checkAccess(ResourceDriverOperation operation) throws AccessControlException;

	/**
	 * This call checks the access rights for a specific resource driver
	 * @param resourceDriver
	 * @param operation
	 * @return
	 */
   	public boolean testAccess(ResourceDriverConfig resourceDriver, ResourceDriverOperation operation);
   	public void checkAccess(ResourceDriverConfig resourceDriver, ResourceDriverOperation operation) throws AccessControlException;

    /**
     * Get all <code>ResourceDriverConfig</code> 
     * 
     */
   	public List<ResourceDriverConfig> getAllResourceDriverConfigs();
   	public List<ResourceDriverConfig> getAllNetFolderResourceDriverConfigs();
   	public List<ResourceDriverConfig> getAllCloudFolderResourceDriverConfigs();

    public ResourceDriverConfig getResourceDriverConfig(Long id);
    /**
     * Create a <code>ResourceDriver</code> 
     * 
     * @param driverName
     * @param driverType
     * @param options
     * @throws AccessControlException
     */
      public ResourceDriverConfig addResourceDriver(String name, DriverType type, String rootPath,
    		  Set<Long> memberIds, Map options) 
     	throws AccessControlException, RDException;
   	
      /**
       * Create a <code>ResourceDriver</code> 
       * 
       * @param driverName
       * @param driverType
       * @param options
       * @throws AccessControlException
       * returns null if driver not found
       */
        public ResourceDriverConfig modifyResourceDriver(String name, DriverType type, String rootPath,
      		  Set<Long> memberIds, Map options) 
       	throws AccessControlException, RDException;
     	
        /**
         * Create a <code>ResourceDriver</code> 
         * 
         * @param driverName
         * @param driverType
         * @param options
         * @throws AccessControlException
         * returns null if driver not found
         */
          public void deleteResourceDriver(String name) 
         	throws AccessControlException, RDException;

        /**
         * Delete a <code>ResourceDriverConfig</code>
         *
         * @param id
         * @throws AccessControlException
         * returns null if driver not found
         */
          public void deleteResourceDriverConfig(Long id)
         	throws AccessControlException, RDException;

    /**
     * Set the sync schedule
     * @param config
     * @param driverId
     */
    public void setSynchronizationSchedule( ScheduleInfo config, Long driverId );

	/**
	 * Synchronize all of the net folders associated with the given net folder server
	 * as soon as system resources become available.
	 * This submits the work and returns immediately without waiting for the work to finish,
	 * hence working asynchronously. 
	 * 
	 * @param netFolderServerId this should be a ResourceDriverConfig
	 * @throws AccessControlException
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public boolean enqueueSynchronize( String netFolderServerName, boolean excludeFoldersWithSchedule )
		throws AccessControlException, FIException, UncheckedIOException, ConfigurationException;

	/**
	 * Synchronize all of the net folders associated with the given net folder server
	 * as soon as system resources become available. 
	 * This submits the work and returns immediately without waiting for the work to finish,
	 * hence working asynchronously. 
	 * 
	 * @param netFolderServerId this should be a ResourceDriverConfig
	 * @param statusTicket
	 * @throws AccessControlException
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public boolean enqueueSynchronize( Long netFolderServerId, boolean excludeFoldersWithSchedule )
		throws AccessControlException, FIException, UncheckedIOException, ConfigurationException;
}
