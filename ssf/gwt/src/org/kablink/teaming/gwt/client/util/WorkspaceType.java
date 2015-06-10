/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration used to communicate the type of a workspace between the
 * client and the server as part of a GWT RPC command.
 * 
 * @author drfoster@novell.com
 */
public enum WorkspaceType implements IsSerializable {
	ADMINISTRATOR_MANAGEMENT,	// When used within          the administration console.
	DISCUSSIONS,
	GLOBAL_ROOT,
	LANDING_PAGE,
	LIMIT_USER_VISIBILITY,		// When used within          the administration console.
	MOBILE_DEVICES,
	NET_FOLDERS_ROOT,
	PROFILE_ROOT,				// When used anywhere except the administration console.
	PROFILE_ROOT_MANAGEMENT,	// When used within          the administration console.
	PROJECT_MANAGEMENT,
	PROXY_IDENTITIES,
	TEAM,
	TEAM_ROOT,					// When used anywhere except the administration console.
	TEAM_ROOT_MANAGEMENT,		// When used within          the administration console.
	TOP,
	TRASH,
	USER,
	WORKSPACE,
	
	OTHER,
	NOT_A_WORKSPACE;
	
	/**
	 * Returns true if this WorkspaceType value represents a profile
	 * root as used for administrator management by the administration
	 * console and false otherwise.
	 * 
	 * @return
	 */
	public boolean isAdministratorManagement() {
		return this.equals(ADMINISTRATOR_MANAGEMENT);
	}
	
	/**
	 * Returns true if this WorkspaceType value represents a global
	 * root workspace and false otherwise.
	 * 
	 * @return
	 */
	public boolean isGlobalRoot() {
		return this.equals(GLOBAL_ROOT);
	}

	/**
	 * Returns true if this WorkspaceType value represents a landing
	 * page and false otherwise.
	 * 
	 * @return
	 */
	public boolean isLandingPage() {
		return this.equals(LANDING_PAGE);
	}
	
	/**
	 * Returns true if this WorkspaceType value represents a root
	 * workspace as used for limiting user visibility by the
	 * administration console and false otherwise.
	 * 
	 * @return
	 */
	public boolean isLimitUserVisibility() {
		return this.equals(LIMIT_USER_VISIBILITY);
	}
	
	/**
	 * Returns true if this WorkspaceType value represents a mobile
	 * devices view, as used by the administration console and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isMobileDevices() {
		return this.equals(MOBILE_DEVICES);
	}
	
	/**
	 * Returns true if this WorkspaceType value represents a profile
	 * root and false otherwise.
	 * 
	 * @return
	 */
	public boolean isProfileRoot() {
		return (this.equals(PROFILE_ROOT) || this.equals(PROFILE_ROOT_MANAGEMENT)  || this.equals(ADMINISTRATOR_MANAGEMENT));
	}

	/**
	 * Returns true if this WorkspaceType value represents a profile
	 * root as used by the administration console and false otherwise.
	 * 
	 * @return
	 */
	public boolean isProfileRootManagement() {
		return this.equals(PROFILE_ROOT_MANAGEMENT);
	}
	
	/**
	 * Returns true if this WorkspaceType value represents a proxy
	 * identities view, as used by the administration console and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isProxyIdentities() {
		return this.equals(PROXY_IDENTITIES);
	}
	
	/**
	 * Returns true if this WorkspaceType value represents a team
	 * workspace and false otherwise.
	 * 
	 * @return
	 */
	public boolean isTeam() {
		return this.equals(TEAM);
	}

	/**
	 * Returns true if this WorkspaceType value represents a team
	 * root workspace and false otherwise.
	 * 
	 * @return
	 */
	public boolean isTeamRoot() {
		return (this.equals(TEAM_ROOT) || this.equals(TEAM_ROOT_MANAGEMENT));
	}

	/**
	 * Returns true if this WorkspaceType value represents a team
	 * root workspace as used by the administration console and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isTeamRootManagement() {
		return this.equals(TEAM_ROOT_MANAGEMENT);
	}
	
	/**
	 * Returns true if this WorkspaceType value represents the top
	 * workspace in the system and false otherwise.
	 * 
	 * @return
	 */
	public boolean isTop() {
		return (this.equals(TOP) || this.equals(LIMIT_USER_VISIBILITY));
	}

	/**
	 * Returns true if this WorkspaceType value represents the top
	 * workspace in the system and false otherwise.
	 * 
	 * @return
	 */
	public boolean isTopWS() {
		return this.equals(TOP);
	}

	/**
	 * Returns true if this WorkspaceType value represents a workspace
	 * and false otherwise.
	 * 
	 * @return
	 */
	public boolean isWorkspace() {
		return (!(this.equals(NOT_A_WORKSPACE)));
	}
}
