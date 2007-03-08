package com.sitescape.team.module.ldap.impl;

import javax.naming.NamingException;

import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.ldap.LdapConfig;
import com.sitescape.team.module.ldap.LdapModule;

public class NullLdapModuleImpl extends CommonDependencyInjection implements LdapModule {
	public boolean testAccess(String operation) {
		return false;
	}

	public LdapConfig getLdapConfig() {
		return null;
	}

	public void setLdapConfig(LdapConfig config) {
	}

	public void syncAll() throws NamingException {
	}

	public void syncUser(Long userId) throws NoUserByTheNameException, NamingException {
	}

}
