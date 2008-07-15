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
package com.sitescape.team.module.zone;

import java.util.List;

import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.ZoneInfo;
import com.sitescape.team.security.AccessControlException;

public interface ZoneModule {
	/**
	 * Creates or updates zone.
	 * 
	 * @param zoneName zone name
	 * @param virtualHost virtual host
	 */
	public void writeZone(String zoneName, String virtualHost);
	
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
	public void addZoneUnderPortal(String zoneName, String virtualHost, String mailDomian) throws ZoneException;
	
	/**
	 * Modifies new zone under the portal in which ICEcore runs 
	 * 
	 * @param zoneName zone name
	 * @param virtualHost virtual host
	 * @param mailDomian mail domain or null
	 */
	public void modifyZoneUnderPortal(String zoneName, String virtualHost, String mailDomain) throws ZoneException;
		
	/**
	 * Deletes the zone under the portal in which ICEcore runts
	 * 
	 * @param zoneName
	 */
	public void deleteZoneUnderPortal(String zoneName) throws ZoneException;
	
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
	 * Returns the ID of the zone.
	 * 
	 * @param zoneName
	 * @return
	 */
	public Long getZoneIdByZoneName(String zoneName);
	
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
	 * Returns the zone with identified by <code>name</code> as a
	 * {@link Workspace}
	 * 
	 * @param name -
	 *            the unique {@link String} identifier for the desired zone.
	 * @return the zone with identified by <code>name</code> as a
	 *         {@link Workspace}
	 */
	public Workspace getZoneByName(String name);

	/**
	 * Returns the default zone as a {@link Workspace}.
	 * 
	 * @return the default zone as a {@link Workspace}.
	 */
	public Workspace getDefaultZone();
}
