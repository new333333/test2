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
