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
package org.kablink.teaming.domain;
//Used to encapsalate authentication information.  A component of zoneConfig
public class AuthenticationConfig  {

	private boolean allowLocalLogin = true;
	private boolean allowAnonymousAccess = true;
	private boolean allowSelfRegistration = false;
	private Long lastUpdate;
	
	private Boolean openidEnabled; // OpenID authentication enabled
	private Boolean openidSelfProvisioningEnabled; // OpenID self-provisioning enabled
	
	public AuthenticationConfig()
	{
		lastUpdate = new Long(System.currentTimeMillis());
	}
	
	public boolean isAllowAnonymousAccess() {
		return allowAnonymousAccess;
	}
	public void setAllowAnonymousAccess(boolean allowAnonymousAccess) {
		this.allowAnonymousAccess = allowAnonymousAccess;
	}
	public boolean isAllowLocalLogin() {
		return allowLocalLogin;
	}
	public void setAllowLocalLogin(boolean allowLocalLogin) {
		this.allowLocalLogin = allowLocalLogin;
	}
	public boolean isAllowSelfRegistration() {
		return allowSelfRegistration;
	}
	public void setAllowSelfRegistration(boolean allowSelfRegistration) {
		this.allowSelfRegistration = allowSelfRegistration;
	}
	public Long getLastUpdate() {
		return lastUpdate;
	}
	protected void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	public void markAsUpdated()
	{
		setLastUpdate(System.currentTimeMillis());
	}
	
	public boolean getOpenidEnabled() {
		if(openidEnabled == null)
			return true; // OpenID is enabled by default
		else 
			return openidEnabled.booleanValue();
	}
	public void setOpenidEnabled(boolean openidEnabled) {
		this.openidEnabled = openidEnabled;
	}
	
	public boolean getOpenidSelfProvisioningEnabled() {
		if(openidSelfProvisioningEnabled == null)
			return false; // OpenID self provisioning is disabled by default
		else
			return openidSelfProvisioningEnabled.booleanValue();
	}
	public void setOpenidSelfProvisioningEnabled(
			boolean openidSelfProvisioningEnabled) {
		this.openidSelfProvisioningEnabled = openidSelfProvisioningEnabled;
	}

}
