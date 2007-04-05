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

package com.sitescape.team.module.ldap;

import javax.naming.NamingException;

import com.sitescape.team.domain.NoUserByTheNameException;

/**
 * @author Janet McCann
 *
 */
public interface LdapModule {
	public boolean testAccess(String operation);

	public LdapConfig getLdapConfig();
	public void setLdapConfig(LdapConfig config);

	public void syncAll() throws NamingException;
	public void syncUser(Long userId) throws NoUserByTheNameException,NamingException;
}
