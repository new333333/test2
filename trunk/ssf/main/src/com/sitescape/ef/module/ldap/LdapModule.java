
package com.sitescape.ef.module.ldap;
import java.util.Map;
import com.sitescape.ef.domain.NoUserByTheNameException;
import javax.naming.NamingException;

/**
 * @author Janet McCann
 *
 */
public interface LdapModule {
	public void syncAll(String companyId) throws NamingException;
	public boolean authenticate(String companyId, String LoginName,String password) throws NamingException;
	public void syncUser(String companyId, String loginName) throws NoUserByTheNameException,NamingException;
	public void setLdapConfig(Map props);
}
