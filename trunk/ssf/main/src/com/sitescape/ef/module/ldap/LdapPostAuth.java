
package com.sitescape.ef.module.ldap;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import com.liferay.portal.auth.AuthException;
import com.liferay.portal.auth.Authenticator;
import com.liferay.portal.util.SpringUtil;
import com.sitescape.ef.ConfigurationException;

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
		ApplicationContext ctx = SpringUtil.getContext();
    	LdapModule ldap = (LdapModule)ctx.getBean("ldapModule");
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
