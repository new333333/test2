
package com.sitescape.ef.module.ldap;

import com.sitescape.ef.domain.NoUserByTheNameException;
import javax.naming.NamingException;

/**
 * @author Janet McCann
 *
 */
public interface LdapModule {

	public LdapConfig getLdapConfig();
	public void setLdapConfig(LdapConfig config);

	public void syncAll() throws NamingException;
	public void syncUser(Long userId) throws NoUserByTheNameException,NamingException;
}
