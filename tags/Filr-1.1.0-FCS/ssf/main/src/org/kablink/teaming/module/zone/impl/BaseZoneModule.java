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
package org.kablink.teaming.module.zone.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.module.zone.ZoneException;
import org.kablink.teaming.module.zone.ZoneUtil;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;


public class BaseZoneModule extends AbstractZoneModule {

	public void writeZone(String zoneName, String virtualHost) {
		logger.info("Cannot write zone " + zoneName + " - Open source edition does not support multi zone");
	}

	public boolean zoneExists(String zoneName) {
		if(SZoneConfig.getDefaultZoneName().equals(zoneName))
			return true;
		else
			return false;
	}

	public Long getZoneIdByVirtualHost(String virtualHost) {
		String zoneName = SZoneConfig.getDefaultZoneName();
		Workspace top = getCoreDao().findTopWorkspace(zoneName);
		return top.getId();
	}

	public String getZoneNameByVirtualHost(String virtualHost) {
		return SZoneConfig.getDefaultZoneName();
	}

	public String getVirtualHost(String zoneName) {
		return SPropsUtil.getDefaultHost();
	}
	protected boolean removeZone(String zoneName) {
		logger.info("Cannot remove zone " + zoneName + " - Open source edition does not support multi zone");
		return true;
	}

	public Long addZone(String zoneName, String virtualHost, String mailDomain) {
		logger.info("Cannot add zone " + zoneName + " under portal - Open source edition does not support multi zone");
		return null;
	}

	public void modifyZone(String zoneName, String virtualHost, String mailDomain) {
		logger.info("Cannot modify zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

	public void deleteZone(String zoneName) {
		logger.info("Cannot remove zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

	public List<ZoneInfo> getZoneInfos() {
		try {
			ZoneInfo info = getZoneInfo(ZoneUtil.getZoneIdByZoneName(SZoneConfig.getDefaultZoneName()));
			return Arrays.asList(new ZoneInfo[] {info});
		}
		catch(NoWorkspaceByTheNameException e) {
			return new ArrayList<ZoneInfo>();
		}
	}

	public ZoneInfo getZoneInfo(Long zoneId) {
		ZoneInfo info = new ZoneInfo();
		info.setZoneName(SZoneConfig.getDefaultZoneName());
		if(!zoneId.equals(ZoneUtil.getZoneIdByZoneName(info.getZoneName()))) {
			return null;
		}
		info.setZoneId(zoneId);
		info.setVirtualHost(getVirtualHost(info.getZoneName()));
		return info;
	}


	public void checkAccess() throws AccessControlException {
		throw new AccessControlException();
	}

	public boolean testAccess() {
		return false;
	}

	public void modifyZoneUnderPortal(String zoneName, String virtualHost) throws ZoneException {
		logger.info("Cannot modify zone " + zoneName + " under portal - Open source edition does not support multi zone");
	}

	protected void setupInitialOpenIDProviderList() {
		// Noop
	}
}
