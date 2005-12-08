
package com.sitescape.ef.module.ldap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.liferay.portal.auth.AuthException;
import com.liferay.portal.auth.Authenticator;

import com.liferay.portal.util.SpringUtil;
import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.domain.NoUserByTheNameException;

import org.springframework.context.ApplicationContext;

/**
 * @author Janet McCann
 *
 */
public class LdapPreAuth implements Authenticator {


	/* (non-Javadoc)
	 * @see com.liferay.portal.auth.Authenticator#authenticateByEmailAddress(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int authenticateByEmailAddress(String companyId,
			String emailAddress, String password) throws AuthException {
		// TODO Auto-generated method stub
		throw new ConfigurationException("authenticateByEmailAddress Not implemented");
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.auth.Authenticator#authenticateByUserId(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int authenticateByUserId(String companyId, String loginName,
			String password) throws AuthException {
		ApplicationContext ctx = SpringUtil.getContext();
    	LdapModule ldap = (LdapModule)ctx.getBean("ldapModule");
 		try {
			if (ldap.authenticate(companyId, loginName, password) == true) return Authenticator.SUCCESS;
			return Authenticator.FAILURE;
		} catch (NoUserByTheNameException nu) {
			return Authenticator.DNE;
		} catch (Exception e) {
			logger.error("Ldap authentication exception: " + e);
			throw new AuthException(e);
		}
		
	}

	private static final Log logger = LogFactory.getLog(LdapPreAuth.class);
}
