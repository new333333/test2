
package com.sitescape.ef.module.ldap;

import com.sitescape.ef.domain.NoUserByTheNameException;
import javax.naming.NamingException;

/**
 * @author Janet McCann
 *
 */
public interface LdapModule {
	public static String SYNC_JOB="sync.job";
	public static String USER_DOMAIN="userDomain";
	public static String GROUP_DOMAIN="groupDomain";
	public static String OBJECT_CLASS="objectClass";
	public LdapConfig getLdapConfig();
	public void setLdapConfig(LdapConfig config);

	public void syncAll(String companyId) throws NamingException;
	public boolean authenticate(String companyId, String LoginName,String password) throws NamingException;
	public void syncUser(String companyId, String loginName) throws NoUserByTheNameException,NamingException;
}
