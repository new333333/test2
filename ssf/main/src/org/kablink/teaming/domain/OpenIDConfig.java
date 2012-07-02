/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.util.SPropsUtil;

/**
 * @author jong
 *
 */
public class OpenIDConfig {

	private Boolean selfProvisioningEnabled; // OpenID self-provisioning enabled

	private Boolean authenticationEnabled; // OpenID authentication enabled
	
	private String[] allowedClaimedIdentityRegexList;
	
	private static String[] allowedClaimedIdentityRegexListDefault;
	
	private Boolean synchronizeProfilesOnSelfProvision;
	
	private Boolean synchronizeProfilesOnLogin;
	
	public boolean isSelfProvisioningEnabled() {
		if(selfProvisioningEnabled == null)
			return SPropsUtil.getBoolean("openid.self.provisioning.enabled.default", false); // OpenID self provisioning is disabled by default
		else
			return selfProvisioningEnabled.booleanValue();
	}
	public void setSelfProvisioningEnabled(
			boolean selfProvisioningEnabled) {
		this.selfProvisioningEnabled = selfProvisioningEnabled;
	}
	
	public boolean isAuthenticationEnabled() {
		if(authenticationEnabled == null) // not set yet
			return SPropsUtil.getBoolean("openid.authentication.enabled.default", false); // OpenID authentication is disabled by default
		else 
			return authenticationEnabled.booleanValue();
	}
	public void setAuthenticationEnabled(boolean openidAuthenticationEnabled) {
		this.authenticationEnabled = openidAuthenticationEnabled;
	}
	
	public boolean isSynchronizeProfilesOnSelfProvision() {
		if(synchronizeProfilesOnSelfProvision == null)
			return SPropsUtil.getBoolean("openid.synchronize.profiles.on.self.provision.default", true);
		else
			return synchronizeProfilesOnSelfProvision.booleanValue();
	}
	public void setSynchronizeProfilesOnSelfProvision(boolean synchronizeProfilesOnSelfProvision) {
		this.synchronizeProfilesOnSelfProvision = synchronizeProfilesOnSelfProvision;
	}
	
	public boolean isSynchronizeProfilesOnLogin() {
		if(synchronizeProfilesOnLogin == null)
			return SPropsUtil.getBoolean("openid.synchronize.profiles.on.login.default", false);
		else
			return synchronizeProfilesOnLogin.booleanValue();
	}
	public void setSynchronizeProfilesOnLogin(boolean synchronizeProfilesOnLogin) {
		this.synchronizeProfilesOnLogin = synchronizeProfilesOnLogin;
	}
	
	public String[] getAllowedClaimedIdentityRegexList() {
		if(allowedClaimedIdentityRegexList == null) {
			// Use default settings from configuration file.
			if(allowedClaimedIdentityRegexListDefault == null) {
				loadAllowedClaimedIdentityRegexListDefault();
			}
			return allowedClaimedIdentityRegexListDefault;
		}
		return allowedClaimedIdentityRegexList;
	}
	public void setAllowedClaimedIdentityRegexList(
			String[] allowedClaimedIdentityRegexList) {
		this.allowedClaimedIdentityRegexList = allowedClaimedIdentityRegexList;
	}

	private void loadAllowedClaimedIdentityRegexListDefault() {
		int count = SPropsUtil.getInt("openid.allowed.claimed.identity.regex.default.count", 0);
		String[] list = new String[count];
		for(int i = 0; i < count; i++) {
			list[i] = SPropsUtil.getString("openid.allowed.claimed.identity.regex.default." + i);
		}
		allowedClaimedIdentityRegexListDefault = list;
	}
}
