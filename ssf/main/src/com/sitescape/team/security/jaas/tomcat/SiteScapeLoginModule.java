/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.security.jaas.tomcat;

import javax.security.auth.login.LoginException;

import com.sitescape.team.security.jaas.BasicLoginModule;
import com.sitescape.team.security.jaas.SiteScapeRole;

public class SiteScapeLoginModule extends BasicLoginModule {

	public boolean commit() throws LoginException {
		boolean commitValue = super.commit();

		if (commitValue) {
			SiteScapeRole role = new SiteScapeRole("users");

			getSubject().getPrincipals().add(role);
		}

		return commitValue;
	}

}
