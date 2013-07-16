/**
 * Copyright (c) 2000-2007 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.kablink.teaming.liferay.security.auth;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.UserPasswordException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.log.LogUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.AuthException;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.security.ldap.PortalLDAPUtil;
import com.liferay.portal.security.pwd.PwdEncryptor;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsUtil;
import com.liferay.portlet.admin.util.OmniadminUtil;
import com.liferay.util.GetterUtil;
import com.liferay.util.StringUtil;
import com.liferay.util.Validator;
import com.liferay.util.ldap.LDAPUtil;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.kablink.teaming.util.NLT;

/**
 * <a href="LDAPAuth.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class LDAPAuth implements Authenticator {

	protected enum AuthenticatedBy {
		EMAIL_ADDRESS,
		SCREEN_NAME,
		USER_ID
	}
	
	public static final String AUTH_METHOD_BIND = "bind";

	public static final String AUTH_METHOD_PASSWORD_COMPARE =
		"password-compare";

	public int authenticateByEmailAddress(
			long companyId, String emailAddress, String password, Map headerMap,
			Map parameterMap)
		throws AuthException {

		try {
			return authenticate(
				companyId, emailAddress, StringPool.BLANK, 0, password, AuthenticatedBy.EMAIL_ADDRESS);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new AuthException(e);
		}
	}

	public int authenticateByScreenName(
			long companyId, String screenName, String password, Map headerMap,
			Map parameterMap)
		throws AuthException {

		try {
			return authenticate(
				companyId, StringPool.BLANK, screenName, 0, password, AuthenticatedBy.SCREEN_NAME);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new AuthException(e);
		}
	}

	public int authenticateByUserId(
			long companyId, long userId, String password, Map headerMap,
			Map parameterMap)
		throws AuthException {

		try {
			return authenticate(
				companyId, StringPool.BLANK, StringPool.BLANK, userId,
				password, AuthenticatedBy.USER_ID);
		}
		catch (Exception e) {
			_log.error(e, e);

			throw new AuthException(e);
		}
	}

	protected int authenticate(
			long companyId, String emailAddress, String screenName, long userId,
			String password, AuthenticatedBy authBy)
		throws Exception {

		if (!PortalLDAPUtil.isAuthEnabled(companyId)) {
			if (_log.isDebugEnabled()) {
				_log.debug("Authenticator is not enabled");
			}

			return SUCCESS;
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Authenticator is enabled");
		}

		// Make exceptions for omniadmins so that if they break the LDAP
		// configuration, they can still login to fix the problem

		if (authenticateOmniadmin(companyId, emailAddress, screenName, userId) == SUCCESS) {
			return SUCCESS;
		}

		Properties env = new Properties();

		String baseProviderURL = PrefsPropsUtil.getString(
			companyId, PropsUtil.LDAP_BASE_PROVIDER_URL);

		String baseDN = PrefsPropsUtil.getString(
			companyId, PropsUtil.LDAP_BASE_DN);

		env.put(
			Context.INITIAL_CONTEXT_FACTORY,
			PrefsPropsUtil.getString(
				companyId, PropsUtil.LDAP_FACTORY_INITIAL));
		env.put(
			Context.PROVIDER_URL,
			LDAPUtil.getFullProviderURL(baseProviderURL, baseDN));
		env.put(
			Context.SECURITY_PRINCIPAL,
			PrefsPropsUtil.getString(
				companyId, PropsUtil.LDAP_SECURITY_PRINCIPAL));
		env.put(
			Context.SECURITY_CREDENTIALS,
			PrefsPropsUtil.getString(
				companyId, PropsUtil.LDAP_SECURITY_CREDENTIALS));

		LogUtil.debug(_log, env);

		LdapContext ctx = null;

		try {
			ctx = new InitialLdapContext(env, null);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug("Failed to bind to the LDAP server");
			}

			return SUCCESS;
		}

		String filter = PrefsPropsUtil.getString(
			companyId, PropsUtil.LDAP_AUTH_SEARCH_FILTER);

		if (_log.isDebugEnabled()) {
			_log.debug("Search filter before transformation " + filter);
		}

		filter = StringUtil.replace(
			filter,
			new String[] {
				"@company_id@", "@email_address@", "@screen_name@", "@user_id@"
			},
			new String[] {
				String.valueOf(companyId), emailAddress, screenName,
				String.valueOf(userId)
			});

		if (_log.isDebugEnabled()) {
			_log.debug("Search filter after transformation " + filter);
		}

		try {
			SearchControls cons = new SearchControls(
				SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);

			NamingEnumeration enu = ctx.search(StringPool.BLANK, filter, cons);

			if (enu.hasMore()) {
				if (_log.isDebugEnabled()) {
					_log.debug("Search filter returned at least one result");
				}

				Binding binding = (Binding)enu.next();

				Attributes attrs = ctx.getAttributes(binding.getName());

				Properties userMappings =
					PortalLDAPUtil.getUserMappings(companyId);

				LogUtil.debug(_log, userMappings);

				Attribute userPassword = attrs.get("userPassword");

				boolean authenticated = authenticate(
					ctx, env, binding, baseDN, userPassword, companyId,
					emailAddress, screenName, userId, password);

				if (!authenticated) {
					return authenticateRequired(
						companyId, userId, emailAddress, FAILURE);
				}

				processUser(
					attrs, userMappings, companyId, emailAddress, screenName,
					userId, password, authBy);
			}
			else {
				if (_log.isDebugEnabled()) {
					_log.debug("Search filter did not return any results");
				}

				return authenticateRequired(
					companyId, userId, emailAddress, DNE);
			}
		}
		catch (Exception e) {
			_log.error("Problem accessing LDAP server " + e.getMessage());

			return authenticateRequired(
				companyId, userId, emailAddress, FAILURE);
		}

		return SUCCESS;
	}

	protected boolean authenticate(
			LdapContext ctx, Properties env, Binding binding, String baseDN,
			Attribute userPassword, long companyId, String emailAddress,
			String screenName, long userId, String password)
		throws Exception {

		// If the system is configured to deletate authentication to SSO middleware,
		// the mere fact that we're here already means that the user is authenticated.
		// Skip the password checking here, since it will be either unsuccessful
		// (when we don't have access to real password) or will be redundant (when
		// we do have access to real password).
		if("true".equalsIgnoreCase(PropsUtil.get("ss.sso.type1.enabled")))
			return true;
		
		boolean authenticated = false;

		// Check passwords by either doing a comparison between the passwords or
		// by binding to the LDAP server

		String authMethod = PrefsPropsUtil.getString(
			companyId, PropsUtil.LDAP_AUTH_METHOD);

		if (authMethod.equals(AUTH_METHOD_BIND)) {
			try {
				String userDN = binding.getName() + StringPool.COMMA + baseDN;

				env.put(Context.SECURITY_PRINCIPAL, userDN);
				env.put(Context.SECURITY_CREDENTIALS, password);

				ctx = new InitialLdapContext(env, null);

				authenticated = true;
			}
			catch (Exception e) {
				_log.warn(
					"Failed to bind to the LDAP server with " + userId +
						" " + e.getMessage());

				authenticated = false;
			}
		}
		else if (authMethod.equals(AUTH_METHOD_PASSWORD_COMPARE)) {
			if (userPassword != null) {
				String ldapPassword = new String((byte[])userPassword.get());

				String encryptedPassword = password;

				String algorithm = PrefsPropsUtil.getString(
					companyId,
					PropsUtil.LDAP_AUTH_PASSWORD_ENCRYPTION_ALGORITHM);

				if (Validator.isNotNull(algorithm)) {
					encryptedPassword =
						"{" + algorithm + "}" +
							PwdEncryptor.encrypt(
								algorithm, password, ldapPassword);
				}

				if (ldapPassword.equals(encryptedPassword)) {
					authenticated = true;
				}
				else {
					authenticated = false;

					_log.warn(
						"LDAP password does not match with given password for user id " + userId);
				}
			}
		}

		return authenticated;
	}

	protected int authenticateOmniadmin(
			long companyId, String emailAddress, String screenName, long userId)
		throws Exception {

		// Only allow omniadmin if Liferay password checking is enabled

		if (GetterUtil.getBoolean(PropsUtil.get(
				PropsUtil.AUTH_PIPELINE_ENABLE_LIFERAY_CHECK))) {

			if (userId > 0) {
				if (OmniadminUtil.isOmniadmin(userId)) {
					return SUCCESS;
				}
			}
			else if (Validator.isNotNull(screenName)) {
				try {
					User user = UserLocalServiceUtil.getUserByScreenName(
						companyId, screenName);

					if (OmniadminUtil.isOmniadmin(user.getUserId())) {
						return SUCCESS;
					}
				}
				catch (NoSuchUserException nsue) {
				}
			}
			else if (Validator.isNotNull(emailAddress)) {
				try {
					User user = UserLocalServiceUtil.getUserByEmailAddress(
						companyId, emailAddress);

					if (OmniadminUtil.isOmniadmin(user.getUserId())) {
						return SUCCESS;
					}
				}
				catch (NoSuchUserException nsue) {
				}
			}
		}

		return FAILURE;
	}

	protected int authenticateRequired(
			long companyId, long userId, String emailAddress, int failureCode)
		throws Exception {

		if (PrefsPropsUtil.getBoolean(
				companyId, PropsUtil.LDAP_AUTH_REQUIRED)) {

			return failureCode;
		}
		else {
			return SUCCESS;
		}
	}

	protected void processUser(
			Attributes attrs, Properties userMappings, long companyId,
			String emailAddress, String screenName, long userId,
			String password, AuthenticatedBy authBy)
		throws Exception {

		try {
			doProcessUser(attrs, userMappings, companyId, emailAddress, screenName, userId, password, authBy);
		}
		catch(UserPasswordException e) {
			// This workaround is necessary to get around the bug in the original
			// class, where the code attempting to update user password in the 
			// portal database following each and every successful login gets 
			// this ugly exception because the new password and the old password 
			// are identical (which is normal case except for the very first 
			// login). Therefore we needed to add this handler here.
		}
	}

	protected void doProcessUser(
			Attributes attrs, Properties userMappings, long companyId,
			String emailAddress, String screenName, long userId,
			String password, AuthenticatedBy authBy)
		throws Exception {

		long creatorUserId = 0;

		boolean autoPassword = false;
		String password1 = password;
		String password2 = password;
		boolean passwordReset = false;

		boolean autoScreenName = false;

		if (Validator.isNull(screenName)) {
			screenName = LDAPUtil.getAttributeValue(
				attrs, userMappings.getProperty("screenName")).toLowerCase();
		}

		if (Validator.isNull(emailAddress)) {
			emailAddress = LDAPUtil.getAttributeValue(
				attrs, userMappings.getProperty("emailAddress"));
		}

		Locale locale = NLT.getTeamingLocale();
		String firstName = LDAPUtil.getAttributeValue(
			attrs, userMappings.getProperty("firstName"));
		String middleName = LDAPUtil.getAttributeValue(
			attrs, userMappings.getProperty("middleName"));
		String lastName = LDAPUtil.getAttributeValue(
			attrs, userMappings.getProperty("lastName"));

		if (Validator.isNull(firstName) || Validator.isNull(lastName)) {
			String fullName = LDAPUtil.getAttributeValue(
				attrs, userMappings.getProperty("fullName"));

			String[] names = LDAPUtil.splitFullName(fullName);

			firstName = names[0];
			middleName = names[1];
			lastName = names[2];
		}

		int prefixId = 0;
		int suffixId = 0;
		boolean male = true;
		int birthdayMonth = Calendar.JANUARY;
		int birthdayDay = 1;
		int birthdayYear = 1970;
		String jobTitle = LDAPUtil.getAttributeValue(
			attrs, userMappings.getProperty("jobTitle"));
		long organizationId = 0;
		long locationId = 0;
		boolean sendEmail = false;
		boolean checkExists = true;
		boolean updatePassword = true;

		importFromLDAP(
			creatorUserId, companyId, autoPassword, password1, password2,
			passwordReset, autoScreenName, screenName, emailAddress, locale,
			firstName, middleName, lastName, prefixId, suffixId, male,
			birthdayMonth, birthdayDay, birthdayYear, jobTitle, organizationId,
			locationId, sendEmail, checkExists, updatePassword, authBy);
	}

	protected User importFromLDAP(
			long creatorUserId, long companyId, boolean autoPassword,
			String password1, String password2, boolean passwordReset,
			boolean autoScreenName, String screenName, String emailAddress,
			Locale locale, String firstName, String middleName, String lastName,
			int prefixId, int suffixId, boolean male, int birthdayMonth,
			int birthdayDay, int birthdayYear, String jobTitle,
			long organizationId, long locationId, boolean sendEmail,
			boolean checkExists, boolean updatePassword,
			AuthenticatedBy authBy)
		throws PortalException, SystemException {

		User user = null;

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Screen name " + screenName + " and email address " +
					emailAddress);
		}

		if (Validator.isNull(screenName) || Validator.isNull(emailAddress)) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Cannot add user because screen name and email address " +
						"are required");
			}

			return user;
		}

		boolean create = true;

		if (checkExists) {
			try {
				if(authBy == AuthenticatedBy.EMAIL_ADDRESS) {
					user = UserLocalServiceUtil.getUserByEmailAddress(
							companyId, emailAddress);
				}
				else {
					user = UserLocalServiceUtil.getUserByScreenName(companyId, screenName);
				}
				
				if (updatePassword) {
					UserLocalServiceUtil.updatePassword(
						user.getUserId(), password1, password2, passwordReset);
				}

				create = false;
			}
			catch (NoSuchUserException nsue) {

				// User does not exist so create

			}
		}

		if (create) {
			try {
				user = UserLocalServiceUtil.addUser(
					creatorUserId, companyId, autoPassword, password1,
					password2, autoScreenName, screenName, emailAddress, locale,
					firstName, middleName, lastName, prefixId, suffixId, male,
					birthdayMonth, birthdayDay, birthdayYear, jobTitle,
					organizationId, locationId, sendEmail);
			}
			catch (Exception e){
				_log.error(
					"Problem adding user with screen name " + screenName +
						" and email address " + emailAddress,
					e);
			}
		}

		return user;
	}
	
	protected static Log _log = LogFactoryUtil.getLog(LDAPAuth.class);

}