/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;

/**
 * Helper methods for email handling.
 * 
 * @author drfoster@novell.com
 */
public class EmailHelper {
	protected static Log m_logger = LogFactory.getLog(EmailHelper.class);
	
	/**
	 * Adds the valid email addresses from a collection to a
	 * Map<Locale, List<InternetAddress>> based on the current user's
	 * locale.
	 * 
	 * @param usedAs
	 * @param localeMap
	 * @param emas
	 */
	public static void addEMAsToLocaleMap(String usedAs, Map<Locale, List<InternetAddress>> localeMap, Collection<String> emas) {
		// Scan the email addresses...
		User user = RequestContextHolder.getRequestContext().getUser();
		for (String ema:  emas) {
			// ...adding them to the current user's locale map.
			addEMAToUsersLocaleMap(usedAs, localeMap, user, ema);
		}
	}

	/**
	 * Adds the valid email addresses from a collection of principals
	 * to a Map<Locale, List<InternetAddress>> based on their locales.
	 * 
	 * @param usedAs
	 * @param localeMap
	 * @param principalIds
	 */
	@SuppressWarnings("unchecked")
	public static void addPrincipalsToLocaleMap(String usedAs, Map<Locale, List<InternetAddress>> localeMap, Collection<Long> principalIds) {
		// Resolve the principal IDs and scan them.
		List<Principal> principals = ResolveIds.getPrincipals(principalIds);
		for (Principal principal:  principals) {
			if (null == principal) {
				continue;
			}

			// Is this principal a group?
			if (principal instanceof GroupPrincipal) {
				// Yes!  Should this group be included in the recipient
				// list?
				if (includeGroup(principal.getId())) {
					// Yes!  Explode it, resolve its members and scan
					// them.
					Collection<Long> userIds = MiscUtil.validateCL(getGroupMemberIds((GroupPrincipal) principal));
					List<Principal> users = ResolveIds.getPrincipals(userIds);
					for (Principal userP:  users) {
						// Is this principal a user?
						if ((null != userP) && (userP instanceof UserPrincipal)) {
							// Yes!  Add its email address to the
							// appropriate locale list.
							User user = ((User) userP);
							addEMAToUsersLocaleMap(usedAs, localeMap, user, user.getEmailAddress());
						}
					}
				}
			}
			
			// No, this principal isn't a group!  Is it a user?
			else if (principal instanceof UserPrincipal) {
				// Yes!  Add its email address to the appropriate
				// locale list.
				User user = ((User) principal);
				addEMAToUsersLocaleMap(usedAs, localeMap, user, user.getEmailAddress());
			}
		}
	}
	
	/**
	 * Adds the valid email addresses from a collection of teams
	 * to a Map<Locale, List<InternetAddress>> based on the team
	 * member's locales.
	 *
	 * @param bm
	 * @param usedAs
	 * @param localeMap
	 * @param teamIds
	 */
	public static void addTeamsToLocaleMap(BinderModule bm, String usedAs, Map<Locale, List<InternetAddress>> localeMap, Collection<Long> teamIds) {
		// Scan the team IDs.
		for (Long teamId:  teamIds) {
			// Expand each team into a collection of principal IDs and
			// add them to the appropriate locale map.
			Collection<Long> userIds = MiscUtil.validateCL(getTeamMemberIds(bm, teamId, true));
			addPrincipalsToLocaleMap(usedAs, localeMap, userIds);
		}
	}
	
	/**
	 * Adds a valid email address to a
	 * Map<Locale, List<InternetAddress>> based on the given User's
	 * locale.
	 * 
	 * @param usedAs
	 * @param localeMap
	 * @param user
	 * @param ema
	 */
	public static void addEMAToUsersLocaleMap(String usedAs, Map<Locale, List<InternetAddress>> localeMap, User user, String ema) {
		Locale					locale     = user.getLocale();
		List<InternetAddress>	localeList = getLocaleList(localeMap, locale);
		boolean					newLocale  = (null == localeList);
		if (newLocale) {
			localeList = new ArrayList<InternetAddress>();
		}
		
		// If the email address is valid...
		InternetAddress ia = MiscUtil.validateEmailAddress(usedAs, ema);
		if (null != ia) {
			// ...and it's not already in the list...
			if (!(isIAInList(localeList, ia))) {
				// ...add it.
				localeList.add(ia);
			}
		}
		
		// If this is a new locale list that we put something into
		// it...
		if (newLocale && (!(localeList.isEmpty()))) {
			// ...store it in the map.
			localeMap.put(locale, localeList);
		}
	}

	/*
	 * Returns a Set<Long> of the IDs of the members of a group.
	 */
	private static Set<Long> getGroupMemberIds(GroupPrincipal group) {
		List<Long> groupIds = new ArrayList<Long>();
		groupIds.add(group.getId());
		Set<Long> groupMemberIds = null;
		try {
			ProfileDao profileDao = ((ProfileDao) SpringContextUtil.getBean("profileDao"));
			groupMemberIds = profileDao.explodeGroups(groupIds, group.getZoneId());
		}
		catch (Exception ex) {/* Ignored. */}
		return validatePrincipalIds(groupMemberIds);
	}

	/*
	 * Scans a Map<Locale, List<InternetAddress>> for a specific
	 * locale.  If it is found, its List<InternetAddress> is returned,
	 * otherwise, null is returned.
	 */
	private static List<InternetAddress> getLocaleList(Map<Locale, List<InternetAddress>> localeMap, Locale locale2Find) {
		for (Locale locale:  localeMap.keySet()) {
			if (locale.equals(locale2Find)) {
				return localeMap.get(locale);
			}
		}
		return null;
	}
	
	/*
	 * Returns a Set<Long> of the member IDs of a team.
	 */
	private static Set<Long> getTeamMemberIds(BinderModule bm, Long binderId, boolean explodeGroups) {
		Set<Long> teamMemberIds = null;
		try {teamMemberIds = bm.getTeamMemberIds(binderId, explodeGroups);}
		catch (Exception ex) {/* Ignored. */}
		return validatePrincipalIds(teamMemberIds);
	}

	/*
	 * Returns true if a List<InternetAddress> contains a given
	 * InternetAddress and false otherwise.
	 */
	private static boolean isIAInList(List<InternetAddress> iaList, InternetAddress ia) {
		// Scan the List<InternetAddress>.
		for (InternetAddress iaScan:  iaList) {
			// Is this the InternetAddress in question?
			if (iaScan.equals(ia)) {
				// Yes!  Return true.
				return true;
			}
		}
		
		// If we get here, the List<InternetAddress> did not contain
		// the InternetAddress in question.  Return false.
		return false;
	}
	
	/*
	 * Returns false if a group, based on its ID, should be excluded
	 * as the target of an email and true otherwise.
	 */
	private static boolean includeGroup(Long groupId) {
		// If we're not allowed to send email to all users...
		boolean	sendingToAllUsersIsAllowed = SPropsUtil.getBoolean("mail.allowSendToAllUsers", false);
		if (!sendingToAllUsersIsAllowed) {
			// ...and this is one of the all users groups...
			if (groupId.equals(Utils.getAllUsersGroupId()) || groupId.equals(Utils.getAllExtUsersGroupId())) {
				// ...exclude it.
				return false;
			}
		}
		
		// If we get here, the group should be included.  Return true.
		return true;
	}
	
	/*
	 * Validates that the Long's in a Set<Long> are valid principal
	 * IDs.
	 */
	@SuppressWarnings("unchecked")
	private static Set<Long> validatePrincipalIds(Set<Long> principalIds) {
		Set<Long> reply = new HashSet<Long>();
		if (MiscUtil.hasItems(principalIds)) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(principalIds);}
			catch (Exception ex) {/* Ignored. */}
			if (MiscUtil.hasItems(principals)) {
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					reply.add(p.getId());
				}
			}
		}
		return reply;
	}
}
