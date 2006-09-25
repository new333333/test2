
package com.sitescape.ef.module.ldap;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.liferay.portal.security.auth.AuthException;
import com.liferay.portal.security.auth.Authenticator;
import com.sitescape.ef.util.SpringContextUtil;

/**
 * @author Janet McCann
 *
 */
public class LdapPostAuth implements Authenticator {
	/* (non-Javadoc)
	 * @see com.liferay.portal.auth.Authenticator#authenticateByEmailAddress(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int authenticateByEmailAddress(String companyId,
			String emailAddress, String password) throws AuthException {
		//not supported, but don't fail authentication cause of it for post processing
		return Authenticator.SUCCESS;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.auth.Authenticator#authenticateByUserId(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int authenticateByUserId(String companyId, String loginName,
			String password) throws AuthException {
    	LdapModule ldap = (LdapModule)SpringContextUtil.getBean("ldapModule");
		try {
			ldap.syncUser(companyId, loginName);
		} catch (Exception e) {
			logger.error("Ldap synchronziation exception: " + e);
		}
		//don't reject if cannot talk to ldap
		return Authenticator.SUCCESS;
		
	}

	private static final Log logger = LogFactory.getLog(LdapPostAuth.class);
}
