package com.sitescape.team.security.jaas.jboss;

import javax.security.auth.login.LoginException;

import com.sitescape.team.security.jaas.BasicLoginModule;
import com.sitescape.team.security.jaas.SiteScapeGroup;
import com.sitescape.team.security.jaas.SiteScapePrincipal;

public class SiteScapeLoginModule extends BasicLoginModule {

	public boolean commit() throws LoginException {
		boolean commitValue = super.commit();

		if (commitValue) {
			SiteScapeGroup group = new SiteScapeGroup("Roles");

			group.addMember(new SiteScapePrincipal("users"));

			getSubject().getPrincipals().add(group);
		}

		return commitValue;
	}
}
