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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;

/**
 * This class contains a collection of methods for dealing with
 * password policy handling.
 *
 * @author drfoster@novell.com
 */
public final class PasswordPolicyHelper {
	protected static Log m_logger = LogFactory.getLog(PasswordPolicyHelper.class);

	// Static flags defining various aspects of password policy
	// enablement.
	public static final boolean PASSWORD_POLICY_ENABLED			=  SPropsUtil.getBoolean("password.policy.enabled",      false                            );
	public static final boolean PASSWORDS_CAN_EXPIRE			= (SPropsUtil.getBoolean("password.policy.expiration",   true ) && PASSWORD_POLICY_ENABLED);
	public static final int     PASSWORD_CHANGE_USER_MAX_HITS	=  SPropsUtil.getInt(    "password.policy.user.maxHits", 1000                             );
	
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
				
				// Do we have any user IDs?
				if (!(userIds.isEmpty())) {
					// Can we resolved them?
					List<Principal> pList = ResolveIds.getPrincipals(userIds, false);
					if (MiscUtil.hasItems(pList)) {
						// Yes!  Scan them.
						for (Principal p:  pList) {
							// If it's a Group?
							if (p instanceof GroupPrincipal) {
								// ...ignore it.
								continue;
							}
	
							// No, it's not a Group!  Is it a User?
							if (p instanceof UserPrincipal) {
								// Yes!  If it's not a person or it's
								// from LDAP...
								User u = ((User) p);
								if ((!(u.isPerson())) || u.getIdentityInfo().isFromLdap()) {
									// ...ignore it.
									continue;
								}
								
								// ...otherwise, clear out it's last
								// ...password changed setting.
								pm.setLastPasswordChange(u, null);
							}
						}
					}
				}
			}
		}
		
		// If we get here, reply contains a count of the User's we
		// processed.  Return it.
		return reply;
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
		if ((!PASSWORD_POLICY_ENABLED) || (!(MiscUtil.getAdminModule().isPasswordPolicyEnabled()))) {
			// ...there can be no violations.
			return null;
		}
		
//!		...this needs to be implemented...
		return null;
	}
}
