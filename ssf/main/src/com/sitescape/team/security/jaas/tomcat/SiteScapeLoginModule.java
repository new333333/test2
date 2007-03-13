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
