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
package org.kablink.teaming.module.zone;

import java.util.List;

import org.kablink.teaming.IllegalCharacterInNameException;
import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.ObjectExistsException;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.security.AccessControlException;


public interface ZoneModule {
	/**
	 * Creates or updates zone.
	 * 
	 * @param zoneName zone name
	 * @param virtualHost virtual host
	 */
	// This method used to be called when zone modification (ie, create or update) is 
	// triggered from portal side. Now that Teaming doesn't run inside portal, this
	// method is no longer needed. Commenting out.
	// public void writeZone(String zoneName, String virtualHost);
	
	/**
	 * Returns whether or not the zone exists.
	 * 
	 * @param zoneName
	 * @return 
	 */
	public boolean zoneExists(String zoneName);
	
	/**
	 * Adds new zone under the portal in which ICEcore runs 
	 * 
	 * @param zoneName zone name
	 * @param virtualHost virtual host
	 * @param mailDomian mail domain or null
	 */
	public Long addZone(String zoneName, String virtualHost, String mailDomian) throws ZoneException, ObjectExistsException, IllegalCharacterInNameException;
	
	/**
	 * Modifies new zone under the portal in which ICEcore runs 
	 * 
	 * @param zoneName zone name
	 * @param virtualHost virtual host
	 * @param mailDomian mail domain or null
	 */
	public void modifyZone(String zoneName, String virtualHost, String mailDomain) throws ZoneException, ObjectExistsException;
		
	/**
	 * Deletes the zone under the portal in which ICEcore runts
	 * 
	 * @param zoneName
	 */
	public void deleteZone(String zoneName) throws ZoneException;
	
	/**
	 * Returns zone configuration
	 * 
	 * @param zoneId
	 * @return
	 */
	public ZoneConfig getZoneConfig(Long zoneId) throws NoObjectByTheIdException;
	/**
	 * Returns the name of the zone corresponding to the specified virtual host.
	 * It returns the default zone name if <code>virtualHost</code> is 
	 * <code>null</code>, or no match is found, or the system does not 
	 * support/honor multiple zones.
	 * 
	 * @param virtualHost virtual host name or <code>null</code>
	 * @return zone name
	 */
	public String getZoneNameByVirtualHost(String virtualHost);
	
	/**
	 * Returns the ID of the zone corresponding to the specified virtual host.
	 * It returns the default zone ID if <code>virtualHost</code> is 
	 * <code>null</code>, or no match is found, or the system does not 
	 * support/honor multiple zones.
	 * 
	 * @param virtualHost
	 * @return
	 */
	public Long getZoneIdByVirtualHost(String virtualHost);
	
	/**
	 * Returns the virtual host name associated with the specified zone.
	 * It returns <code>null</code> if the specified zone represents the default
	 * zone in the system, or the zone does not exist, or the system does not
	 * support/honor multi zones. 
	 * 
	 * @param zoneName
	 * @return
	 */
	public String getVirtualHost(String zoneName);
	
	/**
	 * Returns a list of <code>ZoneInfo</code> objects. 
	 * 
	 * @return
	 */
	public List<ZoneInfo> getZoneInfos();
	
	/**
	 * Returns a specific <code>ZoneInfo</code> object. 
	 * 
	 * @return
	 */
	public ZoneInfo getZoneInfo(Long zoneId);
	
	/**
	 * Tests whether the user has a right to add/update/delete zone.
	 * 
	 * @return
	 */
   	public boolean testAccess();
   	
   	/**
   	 * Checks whether the user has a right to add/update/delete zone.
   	 * 
   	 * @throws AccessControlException
   	 */
   	public void checkAccess() throws AccessControlException;
   	
   	/**
   	 * WARNING: Used only by system. Not to be used by application.
   	 * 
   	 * Performs necessary initialization. Should be invoked every time system is restarted.
   	 */
   	public void initZones();
   	
   	/**
   	 * WARNING: Used only by system. Not to be used by application.
   	 * 
   	 * Performs necessary post initialization work. Should be invoked every time system is restarted.
   	 */
   	public void initZonesPostProcessing();
}
