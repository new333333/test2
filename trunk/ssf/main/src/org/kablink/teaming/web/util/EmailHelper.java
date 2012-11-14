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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
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
		// If the user doesn't have a Locale defined...
		Locale locale = user.getLocale();
		if (null == locale) {
			// ...and the current user doesn't have one either...
			locale = RequestContextHolder.getRequestContext().getUser().getLocale();
			if (null == locale){
				// ...use the system's default.
				locale = Locale.getDefault();
			}
		}

		// What locale list do we store the email addresses into?
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
	 * Adds an error to a List regarding an Address[] of email
	 * addresses that could not be sent to.
	 */
	@SuppressWarnings("unchecked")
	public static void addMailFailures(List errors, Address[] errorAddrs, String resourceKey) {
		// Do we have anything to based the mail failures on?
		int errorCount = ((null == errorAddrs) ? 0 : errorAddrs.length);
		if ((null != errors) && (0 < errorCount) && MiscUtil.hasString(resourceKey)) {
			// Yes!  Add the failed addresses to a string...
			StringBuffer s = new StringBuffer("");
			for (Address errorAddr: errorAddrs) {
				if (0 < s.length()) {
					s.append(", ");
				}
				s.append(errorAddr.toString());
			}

			// ...and log the error.
			errors.add(
				NLT.get(
					resourceKey,
					new String[]{s.toString()},
					Locale.getDefault()));
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

	/**
	 * Returns the string to use to inform about mail sending errors.
	 * 
	 * @param ex
	 * 
	 * @return
	 */
	public static String getMailExceptionMessage(Exception ex) {
		String reply = ex.getLocalizedMessage();
		if (!(MiscUtil.hasString(reply))) {
			reply = ex.getMessage();
		}
		return reply;
	}
	
    /**
     * Returns the localized string to display for a share expiration.
     * 
     * @param locale
     * @param share
     * 
     * @return
     */
    public static String getShareExpiration(Locale locale, ShareItem share) {
    	// Does the share have an expiration date?
    	String reply;
		Date expiration = share.getEndDate();
		if (null == expiration) {
			// No!  It never expires.
			reply = NLT.get("share.expires.never", locale);
		}
		
		else {
			// Yes, there's an expiration date!  Is the an expires
			// after a number of days?
			int days = share.getDaysToExpire();
			if (0 < days) {
				// Yes!  Generate the appropriate string.
				reply = NLT.get("share.expires.after", new Object[]{days}, locale);
			}
			
			else {
				// No, there's no days!  It expires explicitly on the
				// specified date.
				DateFormat dateFmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
				String dateText = dateFmt.format(expiration);
				reply = NLT.get("share.expires.on", new Object[]{dateText}, locale);
			}
		}
		
		// If we get here, reply refers to the share expiration message
		// in the given locale.  Return it.
		return reply;
    }
    
	/**
	 * Returns a List<Locale> of the unique Locale's stored in a
	 * collection of Map<Locale, List<InternetAddress>>'s.
	 * 
	 * @param localeMaps
	 * 
	 * @return
	 */
	public static List<Locale> getTargetLocales(Map<Locale, List<InternetAddress>> ... localeMaps) {
		// Allocate a List<Locale> to return.
		List<Locale> reply = new ArrayList<Locale>();

		// Scan the locale maps.
		for (Map<Locale, List<InternetAddress>> localeMap:  localeMaps) {
			// Scan the Locale keys of this locale map.
			for (Locale locale:  localeMap.keySet()) {
				// If this Locale is not in the reply list...
				if (!(isLocaleInList(reply, locale))) {
					// ...add it.
					reply.add(locale);
				}
			}
		}
		
		// If we get here, reply refers to the unique Locale's referred
		// to by a collection of Map<Locale, List<InternetAddress>>'s.
		// Return it. 
		return reply;
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
	 * Returns true if a List<Locale> contains a given locale and false
	 * otherwise.
	 */
	private static boolean isLocaleInList(List<Locale> localeList, Locale locale) {
		// Scan the List<Locale>.
		for (Locale localeScan:  localeList) {
			// Is this the Locale in question?
			if (localeScan.equals(locale)) {
				// Yes!  Return true.
				return true;
			}
		}
		
		// If we get here, the List<Locale> did not contain the Locale
		// in question.  Return false.
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
	 * Scans the IDs in the given collection for any that resolve to an
	 * all users group and returns a collection without them.
	 */
	@SuppressWarnings("unchecked")
	private static Collection<Long> removeAllUserGroups(Collection<Long> principalIds) {
		// Are there any IDs in the collection we were given?
		if (MiscUtil.hasItems(principalIds)) {
			// Yes!  Are there any that resolve?
			List<Principal> principalList = ResolveIds.getPrincipals(principalIds);
			if (MiscUtil.hasItems(principalList)) {
				// Yes!  Scan them.
				List<Long> allUsersList = new ArrayList<Long>();
				for (Principal p:  principalList) {
					// Is this Principal a group?
					if (p instanceof GroupPrincipal) {
						// Yes!  Is it an all users group?
						String internalId = p.getInternalId();
						if ((null != internalId) &&
								(internalId.equalsIgnoreCase(ObjectKeys.ALL_USERS_GROUP_INTERNALID) ||
								 internalId.equalsIgnoreCase(ObjectKeys.ALL_EXT_USERS_GROUP_INTERNALID))) {
							// Yes!  Add its ID to the list of those
							// we're tracking.
							allUsersList.add(p.getId());
						}
					}
				}

				// Are we tracking any all user groups that are being
				// sent to?
				if (!(allUsersList.isEmpty())) {
					// Yes!  Scan the original principal IDs...
					List<Long> nonAllUserIds = new ArrayList<Long>();
					for (Long pId:  principalIds) {
						// ...tracking those that aren't all user
						// ...groups...
						if (!(allUsersList.contains(pId))) {
							nonAllUserIds.add(pId);
						}
					}
					
					// ...and use the new collection.  We do this to
					// ...avoid any side affects related to changing
					// ...the initial collection we were given.
					principalIds = nonAllUserIds;
				}
			}
		}
		
		// If we get here, principalIds now refers to a collection
		// without any all user groups.  Return it.
		return principalIds;
	}
	
	/**
	 * Sends a confirmation mail message to an external user.
	 * 
	 * @param bs					- Access to modules.
	 * @param externalUserId		- ID of external user confirmation is being sent to.
	 * @param entityPermalinkUrl	- Permalink URL to the entity the confirmation is in regards to.
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Map<String, Object> sendConfirmationToExternalUser(
		AllModulesInjected	bs,					//
		Long				externalUserId,		//
		String				entityPermalinkUrl)	//
			throws Exception
	{
		try {
			// Send the confirmation.
			Map<String, Object> reply = bs.getAdminModule().sendConfirmationMailToExternalUser(
				externalUserId,
				entityPermalinkUrl);
			
			// If we get here, reply contains a map of the results of
			// the email.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			m_logger.debug("EmailHelper.sendConfirmationToExternalUser( SOURCE EXCEPTION ):  ", ex);
			throw ex;
		}
	}
	
	/**
	 * Sends a share invitation mail message to an external user.
	 * 
	 * @param bs				- Access to modules.
	 * @param share				- Describes the share.
	 * @param sharedEntity		- Entity (folder or folder entry) being shared.
	 * @param externalUserId	- ID of external user invitation is being sent to.
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Map<String, Object> sendShareInviteToExternalUser(
		AllModulesInjected	bs,					//
		ShareItem			share,				//
		DefinableEntity		sharedEntity,		//
		Long				externalUserIdId)	//
			throws Exception
	{
		try {
			// Send the invitation.
			Map<String, Object> reply = bs.getAdminModule().sendShareInviteMailToExternalUser(
				share,
				sharedEntity,
				externalUserIdId);
			
			// If we get here, reply contains a map of the results of
			// the email.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			m_logger.debug("EmailHelper.sendShareInviteToExternalUser( SOURCE EXCEPTION ):  ", ex);
			throw ex;
		}
	}
	
	/**
	 * Send a share notification mail message to a collection of users
	 * and/or explicit email addresses.
	 * 
	 * @param bs				- Access to modules.
	 * @param share				- Share item.
	 * @param sharedEntity		- Entity (folder or folder entry) being shared.
	 * @param principalIds		- toList,  users and groups
	 * @param teamIds			- toList,  teams.
	 * @param emailAddresses	- toList,  stand alone email address.
	 * @param ccIds				- ccList,  users and groups
	 * @param bccIds			- bccList, users and groups
	 * 
	 * @return
	 * 
	 * @throws Exception
	 */
	public static Map<String, Object> sendShareNotification(
		AllModulesInjected	bs,				//
		ShareItem			share,			//
		DefinableEntity		sharedEntity,	//
		Collection<Long>	principalIds,	//
		Collection<Long>	teamIds,		//
		Collection<String>	emailAddresses,	//
		Collection<Long>	ccIds, 			//
		Collection<Long>	bccIds)			//
			throws Exception
	{
		try {
			// Is sending email to an all user group allowed?
			if (!(SPropsUtil.getBoolean("mail.allowSendToAllUsers", false))) {
				// No!  Remove any that we're being asked to send to.
				principalIds = removeAllUserGroups(principalIds);
				ccIds        = removeAllUserGroups(ccIds      );
				bccIds       = removeAllUserGroups(bccIds     );
			}

			// Are there any actual targets for the email notification?
			boolean hasTargets = (
				MiscUtil.hasItems(principalIds)   ||
				MiscUtil.hasItems(teamIds)        ||
				MiscUtil.hasItems(emailAddresses) ||
				MiscUtil.hasItems(ccIds)          ||
				MiscUtil.hasItems(bccIds));

			Map<String, Object> reply;
			if (hasTargets) {
				// Yes!  Send it.
				reply = bs.getAdminModule().sendMail(
					share,
					sharedEntity,
					principalIds,
					teamIds,
					emailAddresses,
					ccIds,
					bccIds);
			}
			else {
				// No, there aren't any targets!  Return an empty
				// reply.
				reply = new HashMap<String, Object>();
			}
			
			// If we get here, reply contains a map of the results of
			// the email notification.  Return it.
			return reply;
		}
		
		catch (Exception ex) {
			m_logger.debug("EmailHelper.sendShareNotification( SOURCE EXCEPTION ):  ", ex);
			throw ex;
		}
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
