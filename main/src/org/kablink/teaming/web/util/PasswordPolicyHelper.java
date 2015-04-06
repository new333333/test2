/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;

/**
 * This class contains a collection of methods for dealing with
 * password policy handling.
 *
 * @author drfoster@novell.com
 */
public final class PasswordPolicyHelper {
	protected static Log m_logger = LogFactory.getLog(PasswordPolicyHelper.class);

	// Constants defining various aspects of password policy enablement
	// loaded from the ssf*.properites files.
	public  static final boolean		PASSWORD_POLICY_ENABLED						=  SPropsUtil.getBoolean("password.policy.enabled",                 true                             );
	public  static final boolean		PASSWORD_EXPIRATION_ENABLED					= (SPropsUtil.getBoolean("password.policy.expiration",              false) && PASSWORD_POLICY_ENABLED);
	private static final int			PASSWORD_EXPIRATION_CHANGE_USER_MAX_HITS	=  SPropsUtil.getInt(    "password.policy.expiration.user.maxHits", 1000                             );
	public  static final int			PASSWORD_EXPIRATION_DAYS					=  SPropsUtil.getInt(    "password.policy.expiration.days",         90                               );
	public  static final int			PASSWORD_EXPIRATION_WARNING_DAYS			=  SPropsUtil.getInt(    "password.policy.expiration.warning.days", 5                                );
	public  static final int			PASSWORD_MINIMUM_LENGTH						=  SPropsUtil.getInt(    "password.policy.minimum.password.length", 8                                );

	// The following defines the characters recognized as being a
	// symbol when evaluating a password for password policy
	// violations.
	public  static final char[]			PASSWORD_SYMBOLS							= new char[]{'~', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '{', '}', '[', ']', '|', '\\', '?', '/', ',', '.', '<', '>'};
	private static final StringBuffer	PASSWORD_SYMBOLS_BUFFER 					= new StringBuffer();
	static {
		// Stored in a StringBuffer to facilitate return password
		// policy violation errors.
		for (int i = 0; i < PASSWORD_SYMBOLS.length; i += 1) {
			if (0 < i) {
				PASSWORD_SYMBOLS_BUFFER.append(" ");
			}
			PASSWORD_SYMBOLS_BUFFER.append(PASSWORD_SYMBOLS[i]);
		}
	}
	
	// Constants used to calculate password expirations.
	private static final int		HOURS_PER_DAY						= 24;
	private static final int		SECONDS_PER_MINUTE					= 60;
	private static final int		MINUTES_PER_HOUR					= 60;
	  
	private static final int		MILLIS_PER_SECOND					= 1000;
	private static final int		MILLIS_PER_MINUTE					= (SECONDS_PER_MINUTE * MILLIS_PER_SECOND);
	private static final long		MILLIS_PER_HOUR						= (MINUTES_PER_HOUR   * MILLIS_PER_MINUTE);
	private static final long		MILLIS_PER_DAY						= (HOURS_PER_DAY      * MILLIS_PER_HOUR  );

	/**
	 * Enumeration that represents a User's current password status.
	 * 
	 */
	public enum PasswordStatus {
		EXPIRED,
		VALID,
	}
	
	/*
	 * Class constructor.
	 * 
	 * Private to prevent this class from being instantiated.
	 */
	private PasswordPolicyHelper() {
		// Nothing to do.
	}

	/*
	 * Returns the password violation string for when a password
	 * doesn't contain any symbols.
	 */
	private static String buildNoSymbolsMessage(Locale locale) {
		return NLT.get("password.validation.noSymbols", new String[]{PASSWORD_SYMBOLS_BUFFER.toString()}, locale);
	}
	
	/*
	 * Returns true of a password contains lower case characters and
	 * false otherwise.
	 */
	private static boolean containsLowerCase(String pwd, int pwdLength) {
		boolean reply = false;
		for (int i = 0; i < pwdLength; i += 1) {
			char c = pwd.charAt(i);
			if (('a' <= c) && ('z' >= c)) {
				reply = true;
				break;
			}
		}
		return reply;
	}	

	/*
	 * Returns true if a password contains the given name and false
	 * otherwise.
	 */
	private static boolean containsName(String pwdLC, int pwdLength, String name) {
		// If we don't have a password or name...
		if ((0 == pwdLength) || (null == name) || (0 == name.length())) {
			// ...it can't contain the name.
			return false;
		}
		
		// Does it contain the name?
		return ((0 < pwdLength) && pwdLC.contains(name.toLowerCase()));
	}
	/*
	 * Returns true of a password contains numeric characters and false
	 * otherwise.
	 */
	private static boolean containsNumeric(String pwd, int pwdLength) {
		boolean reply = false;
		for (int i = 0; i < pwdLength; i += 1) {
			char c = pwd.charAt(i);
			if (('0' <= c) && ('9' >= c)) {
				reply = true;
				break;
			}
		}
		return reply;
	}
	
	/*
	 * Returns true of a password contains symbol characters and false
	 * otherwise.
	 */
	private static boolean containsSymbols(String pwd, int pwdLength) {
		boolean reply = false;
		for (int i = 0; i < pwdLength; i += 1) {
			char c = pwd.charAt(i);
			for (char sc:  PASSWORD_SYMBOLS) {
				if (sc == c) {
					reply = true;
					break;
				}
			}
		}
		return reply;
	}	
	
	/*
	 * Returns true of a password contains upper case characters and
	 * false otherwise.
	 */
	private static boolean containsUpperCase(String pwd, int pwdLength) {
		boolean reply = false;
		for (int i = 0; i < pwdLength; i += 1) {
			char c = pwd.charAt(i);
			if (('A' <= c) && ('Z' >= c)) {
				reply = true;
				break;
			}
		}
		return reply;
	}	
	
	/**
	 * Forces all non-LDAP person User's to change their password on
	 * their next login.
	 * 
	 * @param bs
	 */
	@SuppressWarnings("unchecked")
	public static void forceAllUsersToChangePassword(AllModulesInjected bs) {
		// Setup a filter for person's...
		SearchFilter searchTermFilter = new SearchFilter();
		searchTermFilter.addPersonFlagFilter(true);

		// ...setup the search options...
		Map options = new HashMap();
		options.put(ObjectKeys.SEARCH_MAX_HITS,      new Integer(PASSWORD_EXPIRATION_CHANGE_USER_MAX_HITS));
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
		
		// ...and process the User's, page by page.
		int		count;
		Integer	offset = 0;
		do {
			options.put(ObjectKeys.SEARCH_OFFSET, offset);
			count   = forceAllUsersToChangePasswordImpl(bs, options);
			offset += count;
		} while (count == PASSWORD_EXPIRATION_CHANGE_USER_MAX_HITS);
	}

	/*
	 * Implementation method that actually forces a group of User's to
	 * change their password on their next login.
	 */
	@SuppressWarnings("unchecked")
	private static int forceAllUsersToChangePasswordImpl(AllModulesInjected bs, Map options) {
		// Perform the search.
		ProfileModule pm = bs.getProfileModule();
		Map userMaps = pm.getUsers(options);
		int reply = 0;

		// Did we get any entries back?
		if (MiscUtil.hasItems(userMaps)) {
			List<Map<String, String>> userEntryMapList = ((List<Map<String, String>>) userMaps.get(ObjectKeys.SEARCH_ENTRIES));
			reply = ((null == userEntryMapList) ? 0 : userEntryMapList.size());
			if (0 < reply) {
				// Yes!  Scan them...
				List<Long> userIds = new ArrayList<Long>();
				for (Map<String, String> userEntryMap:  userEntryMapList) {
					// ...collecting their user IDs so we can resolved
					// ...them all at once.
					String userId = userEntryMap.get("_docId");
					if (MiscUtil.hasString(userId)) {
						userIds.add(Long.parseLong(userId));
					}
				}

				// Force these users to change their password.
				forceUsersToChangePasswordImpl(
					bs,
					userIds,
					null);	// null -> We don't want any errors to be returned since we tightly control what's selected.
			}
		}
		
		// If we get here, reply contains a count of the User's we
		// processed.  Return it.
		return reply;
	}

	/**
	 * Forces the selected users to change their password after their
	 * next successful login.  Returns an ErrorListRpcResponseData
	 * containing any errors that occur.
	 * 
	 * @param bs
	 * @param request
	 * @param userIds
	 * 
	 * @return
	 */
	public static List<String> forceUsersToChangePassword(AllModulesInjected bs, HttpServletRequest request, List<Long> userIds) {
		SimpleProfiler.start("PasswordPolicyHelper.forceUsersToChangePassword()");
		try {
			// Allocate an error list response we can return.
			List<String> reply = new ArrayList<String>();

			// Force the users to change their password.
			forceUsersToChangePasswordImpl(bs, userIds, reply);
			
			// If we get here, reply refers to an
			// ErrorListRpcResponseData containing any errors we
			// encountered.  Return it.
			return reply;
		}
		
		catch (Exception e) {
			m_logger.error("PasswordPolicyHelper.forceUsersToChangePassword( SOURCE EXCEPTION ):  ", e);
			throw e;
		}
		
		finally {
			SimpleProfiler.stop("PasswordPolicyHelper.forceUsersToChangePassword()");
		}
	}

	/*
	 * Forces a collection of users, based on their ID, to change their
	 * password after their next successful login.
	 */
	@SuppressWarnings("unchecked")
	private static void forceUsersToChangePasswordImpl(AllModulesInjected bs, List<Long> userIds, List<String> errList) {
		// Do we have any user IDs?
		if (MiscUtil.hasItems(userIds)) {
			// Can we resolved them?
			List<Principal> pList = ResolveIds.getPrincipals(userIds, false);
			if (MiscUtil.hasItems(pList)) {
				// Yes!  Scan them.
				ProfileModule pm = bs.getProfileModule();
				for (Principal p:  pList) {
					// If it's a Group...
					String pTitle = p.getTitle();
					if (p instanceof GroupPrincipal) {
						// ...ignore it.
						if (null != errList) {
							errList.add(NLT.get("forceUserPasswordChangeError.Group", new String[]{pTitle}));
						}
						continue;
					}

					// No, it's not a Group!  Is it a User?
					if (p instanceof UserPrincipal) {
						// Yes!  If it's not a person...
						User u = ((User) p);
						if (!(u.isPerson())) {
							// ...ignore it.
							if (null != errList) {
								errList.add(NLT.get("forceUserPasswordChangeError.BuiltInUser", new String[]{pTitle}));
							}
							continue;
						}
						
						// If it's from LDAP...
						if (u.getIdentityInfo().isFromLdap()) {
							// ...ignore it.
							if (null != errList) {
								errList.add(NLT.get("forceUserPasswordChangeError.LDAP", new String[]{pTitle}));
							}
							continue;
						}
						
						// If it's the Guest account...
						if (u.isShared()) {
							// ...ignore it.
							if (null != errList) {
								errList.add(NLT.get("forceUserPasswordChangeError.Guest", new String[]{pTitle}));
							}
							continue;
						}
						
						// ...otherwise, clear out their last
						// ...password changed setting.
						pm.setLastPasswordChange(u, null);
					}
				}
			}
		}
	}
	
	/**
	 * Validates a given password against the password policy.  If the
	 * password is valid null is returned.  If it's not valid, a
	 * List<String> containing one or more reasons why is returned.
	 * 
	 * @param localeUser	User changing the password.            (May not be whose password is being changed.)
	 * @param pwdUser		User whose password is being changed.  (May not be who is changing it.)
	 * @param newPassword	The new password to validate against the policy.
	 * 
	 * @return
	 */
	public static List<String> getPasswordPolicyViolations(User localeUser, User pwdUser, String newPassword) {
		// If password policy is not enabled...
		if (!(passwordPolicyEnabled())) {
			// ...there can be no violations.
			return null;
		}

		// Allocate a List<String> we can return with any password
		// policy violations we detect.
		List<String> reply = new ArrayList<String>();

		// What locale should we generate any violations in?
		Locale locale = localeUser.getLocale();
		if (null == locale) {
			locale = NLT.getDefaultLocale();
		}
		
		// Is the password long enough?
		int pwdLength = ((null == newPassword) ? 0 : newPassword.length());
		if (PASSWORD_MINIMUM_LENGTH > pwdLength) {
			// No!  That's a violation.
			reply.add(NLT.get("password.validation.tooShort", new String[]{String.valueOf(PASSWORD_MINIMUM_LENGTH)}, locale));
		}
		
		// Does it contain lower case, upper case and numeric characters?
		int hasCount = 0;
		boolean hasLower   = containsLowerCase(newPassword, pwdLength); if (hasLower)   hasCount += 1;
		boolean hasUpper   = containsUpperCase(newPassword, pwdLength); if (hasUpper)   hasCount += 1;
		boolean hasNumeric = containsNumeric(  newPassword, pwdLength); if (hasNumeric) hasCount += 1;
		if (3 > hasCount) {
			// No!  It's missing one or more of them.  Does it contain
			// any symbol characters?
			boolean hasSymbols = containsSymbols(newPassword, pwdLength);
			if (hasSymbols) {
				hasCount += 1;
			}

			// Have we found 3 of the 3 required items?
			if (3 > hasCount) {
				// No!  Then it's in violation of policy.  List the
				// reasons for failure.
				                 reply.add(NLT.get("password.validation.needs3",    locale));
				if (!hasLower)   reply.add(NLT.get("password.validation.noLower",   locale));
				if (!hasUpper)   reply.add(NLT.get("password.validation.noUpper",   locale));
				if (!hasNumeric) reply.add(NLT.get("password.validation.noNumeric", locale));
				if (!hasSymbols) reply.add(buildNoSymbolsMessage(                   locale));
			}
		}

		// If the password contains any part of the user's name, that's
		// a violation.
		String pwdLC = newPassword.toLowerCase();
		if (containsName(pwdLC, pwdLength, pwdUser.getName()))      reply.add(NLT.get("password.validation.userId",    locale));
		if (containsName(pwdLC, pwdLength, pwdUser.getFirstName())) reply.add(NLT.get("password.validation.firstName", locale));
		if (containsName(pwdLC, pwdLength, pwdUser.getLastName()))  reply.add(NLT.get("password.validation.lastName",  locale));

		// If where are any violations, we need to insert the violation
		// header at the top of the list.
		if (reply.isEmpty())
		     reply = null;
		else reply.add(0, NLT.get("password.validation.header", locale));
			
		// If we get here, reply refers to the List<String> containing
		// any password violations.  Return it.
		return reply;
	}
	
	/**
	 * Returns a date/time stamp of when the given user's password
	 * expires.
	 * 
	 * Returns null if the user's password never expires (i.e., if
	 * password policy or password expiration is disabled, ...)
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static Date getUsersPasswordExpiration(User user) {
		// If password expiration is not enabled...
		if (!(passwordExpirationEnabled())) {
			// ...it can never expire.  Return null.
			return null;
		}

		// If the user is a built-in system account, Guest or from
		// LDAP...
		if ((!(user.isPerson())) || user.isShared() || user.getIdentityInfo().isFromLdap()) {
			// ...their password never expires.
			return null;
		}
		
		// If the user has never changed their password (or the admin
		// is forcing them to change it)..
		Date lastChange = user.getLastPasswordChange();
		if (null == lastChange) {
			// ...we consider it expired since the beginning of time.
			return new Date(0);
		}

		// Return the date/time stamp that the user's password expires.
		long lcTime = lastChange.getTime();
		return new Date(lcTime + (MILLIS_PER_DAY * PASSWORD_EXPIRATION_DAYS));
	}

	/**
	 * Returns a PasswordStatus enumeration value specifying the given
	 * user's current password status.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static PasswordStatus getUsersPasswordStatus(User user) {
		PasswordStatus reply;
		if (isUsersPasswordExpired(user))
		     reply = PasswordStatus.EXPIRED;
		else reply = PasswordStatus.VALID;
		return reply;
	}
	
	/**
	 * Returns the date before which users should be warned their
	 * password is about to expire.
	 *
	 * Returns:  The current date/time plus the configured number of
	 *           of warning days.
	 *
	 * @return
	 */
	public static Date getPasswordWarningDate() {
		return new Date(new Date().getTime() + (PASSWORD_EXPIRATION_WARNING_DAYS * PasswordPolicyHelper.MILLIS_PER_DAY));
	}

	/**
	 * Returns true if a user's password is expired and false otherwise.
	 * 
	 * @param user
	 * 
	 * @return
	 */
	public static boolean isUsersPasswordExpired(User user) {
		// If the user's password can't expire...
		Date expirationDate = getUsersPasswordExpiration(user);
		if (null == expirationDate) {
			// ...it can't be expired.
			return false;
		}

		// Is the user's password expired?
		long expirationTime = expirationDate.getTime();
		return (expirationTime < new Date().getTime());
	}
	
	/**
	 * Returns true if password expiration is currently enabled and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean passwordExpirationEnabled() {
		return (PASSWORD_EXPIRATION_ENABLED && passwordPolicyEnabled()); 
	}

	/**
	 * Returns true if password policy is currently enabled and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean passwordPolicyEnabled() {
		return (PASSWORD_POLICY_ENABLED && MiscUtil.getAdminModule().isPasswordPolicyEnabled());
	}
}
