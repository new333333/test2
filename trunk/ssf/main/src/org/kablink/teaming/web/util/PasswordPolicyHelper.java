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
	public  static final boolean	PASSWORD_POLICY_ENABLED				=  SPropsUtil.getBoolean("password.policy.enabled",                 false                            );
	public  static final boolean	PASSWORDS_CAN_EXPIRE				= (SPropsUtil.getBoolean("password.policy.expiration",              true ) && PASSWORD_POLICY_ENABLED);
	private static final int		PASSWORD_CHANGE_USER_MAX_HITS		=  SPropsUtil.getInt(    "password.policy.user.maxHits",            1000                             );
	private static final int		PASSWORD_EXPIRATION_DAYS			=  SPropsUtil.getInt(    "password.policy.expiration.days",         90                               );
	private static final int		PASSWORD_EXPIRATION_WARNING_DAYS	=  SPropsUtil.getInt(    "password.policy.expiration.warning.days", 5                                );

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
		options.put(ObjectKeys.SEARCH_MAX_HITS,      new Integer(PASSWORD_CHANGE_USER_MAX_HITS));
		options.put(ObjectKeys.SEARCH_SEARCH_FILTER, searchTermFilter.getFilter());
		
		// ...and process the User's, page by page.
		int		count;
		Integer	offset = 0;
		do {
			options.put(ObjectKeys.SEARCH_OFFSET, offset);
			count   = forceAllUsersToChangePasswordImpl(bs, options);
			offset += count;
		} while (count == PASSWORD_CHANGE_USER_MAX_HITS);
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
	 * @param user			User changing the password, not the user whose password is being changed.
	 * @param newPassword	The new password to validate against the policy.
	 * 
	 * @return
	 */
	public static List<String> getPasswordPolicyViolations(User user, String newPassword) {
		// If password policy is not enabled...
		if (!(passwordPolicyEnabled())) {
			// ...there can be no violations.
			return null;
		}
		
//!		...this needs to be implemented...
		return null;

/*
		List<String> reply = new ArrayList<String>();
		reply.add("This is the 1st violation.");
		reply.add("This is the 2nd violation.");
		reply.add("This is the 3rd violation.");
		return reply;
*/
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
		return (PASSWORDS_CAN_EXPIRE && passwordPolicyEnabled()); 
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
