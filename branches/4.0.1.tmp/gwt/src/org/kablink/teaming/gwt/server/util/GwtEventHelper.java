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
package org.kablink.teaming.gwt.server.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.search.BasicIndexUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.DateComparer;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.search.Constants;

/**
 * Helper methods for the GWT event (tasks and appointments) handling.
 *
 * @author drfoster@novell.com
 */
public class GwtEventHelper {
	protected static Log m_logger = LogFactory.getLog(GwtEventHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtEventHelper() {
		// Nothing to do.
	}
	
	/*
	 * Converts a String to a Long, if possible, and adds it as the ID
	 * of an AssignmentInfo to a List<AssignmentInfo>.
	 */
	private static void addAIFromStringToList(String s, List<AssignmentInfo> l, AssigneeType assigneeType) {
		try {
			Long lVal = Long.parseLong(s);
			l.add(AssignmentInfo.construct(lVal, assigneeType));
		}
		catch (NumberFormatException nfe) {/* Ignored. */}
	}

	/**
	 * Generates a search index field name reference for an
	 * appointment's event field.
	 * 
	 * @param fieldName
	 * 
	 * @return
	 */
	public static String buildAppointmentEventFieldName(String fieldName) {
		return buildEventFieldName("event", fieldName);
	}
	
	/**
	 * Generates a String to write to the log for a boolean.
	 * 
	 * @param label
	 * @param v
	 * 
	 * @return
	 */
	public static String buildDumpString(String label, boolean v) {
		return (label + ": " + String.valueOf(v));
	}

	/**
	 * Generates a String to write to the log for a Date.
	 * 
	 * @param label
	 * @param v
	 * 
	 * @return
	 */
	public static String buildDumpString(String label, Date v) {
		String dateS;
		if (null == v) {
			dateS = "null";
		}
		
		else {
			User user = GwtServerHelper.getCurrentUser();			
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, user.getLocale());
			df.setTimeZone(user.getTimeZone());			
			dateS = df.format(v);
		}
		
		return (label + ": " + dateS);
	}

	/**
	 * Generates a String to write to the log for an integer.
	 * 
	 * @param label
	 * @param v
	 * 
	 * @return
	 */
	public static String buildDumpString(String label, int v) {
		return (label + ": " + String.valueOf(v));
	}
	
	/**
	 * Generates a String to write to the log for a Long.
	 * 
	 * @param label
	 * @param v
	 * 
	 * @return
	 */
	public static String buildDumpString(String label, Long v) {
		return (label + ": " + ((null == v) ? "null" : String.valueOf(v)));
	}
	
	/**
	 * Generates a String to write to the log for a List<AssignmentInfo>.
	 * 
	 * @param label
	 * @param v
	 * @param teeamOrGroup
	 * 
	 * @return
	 */
	public static String buildDumpString(String label, List<AssignmentInfo> v, boolean teamOrGroup) {
		if (null == v) {
			v = new ArrayList<AssignmentInfo>();
		}
		
		StringBuffer buf = new StringBuffer(label);
		buf.append(": ");
		if (v.isEmpty()) {
			buf.append("EMPTY");
		}
		else {
			int c = 0;
			for (AssignmentInfo ai: v) {
				if (0 < c++) {
					buf.append(", ");
				}
				buf.append(String.valueOf(ai.getId()));
				buf.append("(" + ai.getTitle());
				if (teamOrGroup)
				     buf.append(":" + String.valueOf(ai.getMembers()));
				else buf.append(":" + ai.getPresence().getStatusText());
				buf.append(":" + ai.getPresenceDude() + ")");
			}
		}
		return buf.toString();
	}
	
	/**
	 * Generates a String to write to the log for a String.
	 * 
	 * @param label
	 * @param v
	 * 
	 * @return
	 */
	public static String buildDumpString(String label, String v) {
		return (label + ": '" + ((null == v) ? "null" : v) + "'");
	}

	/**
	 * Generates a search index field name reference for an event
	 * field.
	 * 
	 * @param fieldName
	 * 
	 * @return
	 */
	public static String buildEventFieldName(String baseName, String fieldName) {
		return (baseName + BasicIndexUtils.DELIMITER + fieldName);
	}
	
	/**
	 * Generates a search index field name reference for a task's event
	 * field.
	 * 
	 * @param fieldName
	 * 
	 * @return
	 */
	public static String buildTaskEventFieldName(String fieldName) {
		return buildEventFieldName(Constants.EVENT_FIELD_START_END, fieldName);
	}
	
	/**
	 * Reads a List<AssignmentInfo> from a Map.
	 * 
	 * @param m
	 * @param key
	 * @param assigneeType
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<AssignmentInfo> getAssignmentInfoListFromEntryMap(Map m, String key, AssigneeType assigneeType) {
		// Is there value for the key?
		List<AssignmentInfo> reply = new ArrayList<AssignmentInfo>();
		Object o = m.get(key);
		if (null != o) {
			// Yes!  Is the value is a String?
			if (o instanceof String) {
				// Yes!  Use it as Long to create an AssignmentInfo to
				// add to the List<AssignmentInfo>. 
				addAIFromStringToList(((String) o), reply, assigneeType);
			}

			// No, the value isn't a String!  Is it a String[]?
			else if (o instanceof String[]) {
				// Yes!  Scan them and use each as Long to create an
				// AssignmentInfo to add to the List<AssignmentInfo>. 
				String[] strLs = ((String[]) o);
				int c = strLs.length;
				for (int i = 0; i < c; i += 1) {
					addAIFromStringToList(strLs[i], reply, assigneeType);
				}
			}

			// No, the value isn't a String[] either!  Is it a
			// SearchFieldResult?
			else if (o instanceof SearchFieldResult) {
				// Yes!  Scan the value set from it and use each as
				// Long to create an AssignmentInfo to add to the
				// List<AssignmentInfo>. 
				SearchFieldResult sfr = ((SearchFieldResult) m.get(key));
				Set<String> strLs = ((Set<String>) sfr.getValueSet());
				for (String strL:  strLs) {
					addAIFromStringToList(strL, reply, assigneeType);
				}
			}
		}
		
		// If we get here, reply refers to the List<AssignmentInfo> of
		// values from the Map.  Return it.
		return reply;
	}
	
	/**
	 * Reads a Date from a Map.
	 * 
	 * @param m
	 * @param key
	 * @param adjustToMidnight
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Date getDateFromEntryMap(Map m, String key) {
		Date reply;
		
		Object data = m.get(key);
		if (data instanceof Date) {
			reply = ((Date) data);
		}
		else if (data instanceof String) {
			try {
				reply = DateTools.stringToDate((String) data);
			}
			catch (ParseException pe) {
				reply = null;
			}
		}
		else {
			reply = null;
		}
		
		return reply;
	}

	/**
	 * Reads an integer from a Map.
	 * 
	 * @param m
	 * @param key
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static int getIntFromEntryMap(Map m, String key) {
		int reply = 0;
		String i = getStringFromEntryMapRaw(m, key);
		if (0 < i.length()) {
			reply = Integer.parseInt(i);
		}
		return reply;
	}
	
	/**
	 * Reads a Long from a Map.
	 * 
	 * @param m
	 * @param key
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Long getLongFromEntryMap(Map m, String key) {
		Long reply = null;
		String l = getStringFromEntryMapRaw(m, key);
		if (0 < l.length()) {
			reply = Long.parseLong(l);
		}
		return reply;
	}
	
	/**
	 * Reads a date from a Map and determines if it's overdue.
	 * 
	 * @param m
	 * @param key
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean getOverdueFromEntryMap(Map m, String key) {
		Date endDate = getDateFromEntryMap(m, key);
		return ((null == endDate) ? false : DateComparer.isOverdue(endDate));
	}
	
	/**
	 * Reads a String from a Map.
	 * 
	 * @param m
	 * @param key
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getStringFromEntryMapRaw(Map m, String key) {
		String reply = ((String) m.get(key));
		if (null == reply) {
			reply = "";
		}
		return reply;
	}

	/**
	 * Given the List<Long>'s of a collection of principal and team
	 * IDs, reads the database entries for them and stores the
	 * information in a series of Map's.
	 * 
	 * @param bs
	 * @param request
	 * @param principalIds
	 * @param teamIds
	 * 
	 * @param principalEMAs
	 * @param principalTitles
	 * @param groupCounts
	 * @param userPresence
	 * @param userExternal
	 * @param presenceUserWSIds
	 * @param teamTitles
	 * @param teamCounts
	 * @param avatarUrls
	 */
	@SuppressWarnings("unchecked")
	public static void readEventStuffFromDB(
			// Uses these...
			AllModulesInjected			bs,
			HttpServletRequest			request,
			List<Long>					principalIds,
			List<Long>					teamIds,

			// ...to complete these.
			Map<Long, String>			principalEMAs,
			Map<Long, String>			principalTitles,
			Map<Long, Integer>			groupCounts,
			Map<Long, GwtPresenceInfo>	userPresence,
			Map<Long, Boolean>			userExternal,
			Map<Long, Long>				presenceUserWSIds,
			
			Map<Long, String>			teamTitles,
			Map<Long, Integer>			teamCounts,
			
			Map<Long, String>			avatarUrls)
	{
		// If we don't have any principal or team IDs...
		boolean hasPrincipals = ((null != principalIds) && (!(principalIds.isEmpty())));
		boolean hasTeams      = ((null != teamIds)      && (!(teamIds.isEmpty())));
		if ((!hasPrincipals) && (!hasTeams)) {
			// ...we don't have anything to read.  Bail.
			return;
		}
		
		// Construct Maps, mapping the principal IDs to their titles
		// and membership counts.
		if (hasPrincipals) {
			List principals = null;
			try {principals = ResolveIds.getPrincipals(principalIds, false);}	// false -> Don't check active (i.e., allow disabled) users.
			catch (Exception ex) {/* Ignored. */}
			if ((null != principals) && (!(principals.isEmpty()))) {
				boolean isPresenceEnabled = GwtServerHelper.isPresenceEnabled();
				for (Object o:  principals) {
					Principal p = ((Principal) o);
					Long pId = p.getId();
					boolean isUser = (p instanceof UserPrincipal);
					principalTitles.put(pId, p.getTitle());					
					if (p instanceof GroupPrincipal) {
						groupCounts.put(pId, GwtServerHelper.getGroupCount((GroupPrincipal) p));						
					}
					else if (isUser) {
						User user = ((User) p);
						presenceUserWSIds.put(pId, user.getWorkspaceId());
						if (isPresenceEnabled) {
							userPresence.put(pId, GwtServerHelper.getPresenceInfo(user));
						}
						String ema = user.getEmailAddress();
						if (MiscUtil.hasString(ema)) {
							principalEMAs.put(pId, ema);
						}
				    	try {
				    		AccessUtils.readCheck(user);
							String avatarUrl = GwtServerHelper.getUserAvatarUrl(bs, request, user);
							if (MiscUtil.hasString(avatarUrl)) {
								avatarUrls.put(pId, avatarUrl);
							}
				    	}
				    	catch (Exception e) {
				    		// No access to user -> No avatar URL.
				    	}
						userExternal.put(pId, new Boolean(!(user.getIdentityInfo().isInternal())));
					}
				}
			}
		}
		
		// Construct Maps, mapping the team IDs to their titles and
		// membership counts.
		if (hasTeams) {
			SortedSet<Binder> binders = null;
			try {binders = bs.getBinderModule().getBinders(teamIds);}
			catch (Exception ex) {/* Ignored. */}
			if ((null != binders) && (!(binders.isEmpty()))) {
				for (Binder b:  binders) {
					Long bId = b.getId();
					teamTitles.put(bId, b.getTitle());
					teamCounts.put(bId, GwtServerHelper.getTeamCount(bs, b));
				}
			}
		}
	}
	
	/**
	 * Stores the avatar URL of an AssignmentInfo based on Map lookup
	 * using its ID.
	 * 
	 * Returns true if an avatar URL was stored and false otherwise.
	 * 
	 * @param ai
	 * @param avatarUrls
	 * 
	 * @return
	 */
	public static boolean setAssignmentInfoAvatarUrl(AssignmentInfo ai, Map<Long, String> avatarUrls) {
		String avatarUrl = avatarUrls.get(ai.getId());
		boolean reply = MiscUtil.hasString(avatarUrl);
		if (reply) {
			ai.setAvatarUrl(avatarUrl);
		}
		return reply;
	}
	
	/**
	 * Stores the email address of an AssignmentInfo based on Map
	 * lookup using its ID.
	 * 
	 * Returns true if an email address was stored and false otherwise.
	 * 
	 * @param ai
	 * @param emaMap
	 * 
	 * @return
	 */
	public static boolean setAssignmentInfoEmailAddress(AssignmentInfo ai, Map<Long, String> emaMap) {
		String ema = emaMap.get(ai.getId());
		boolean reply = MiscUtil.hasString(ema);
		if (reply) {
			ai.setEmailAddress(ema);
		}
		return reply;
	}

	/**
	 * Stores the external flag of an AssignmentInfo based on Map
	 * lookup using its ID.
	 * 
	 * Returns true if an external flag was stored and false otherwise.
	 * 
	 * @param ai
	 * @param userExternalMap
	 * 
	 * @return
	 */
	public static boolean setAssignmentInfoExternal(AssignmentInfo ai, Map<Long, Boolean> userExternalMap) {
		Boolean userExternal = userExternalMap.get(ai.getId());
		boolean reply = (null != userExternal);
		if (reply) {
			ai.setUserExternal(userExternal);
		}
		return reply;
	}

	/**
	 * Stores the hover text of an AssignmentInfo based on Map lookup
	 * using its ID.
	 * 
	 * Returns true if a hover text was stored and false otherwise.
	 * 
	 * @param ai
	 * @param hoverMap
	 * 
	 * @return
	 */
	public static boolean setAssignmentInfoHover(AssignmentInfo ai, Map<Long, String> hoverMap) {
		String hover = hoverMap.get(ai.getId());
		boolean reply = MiscUtil.hasString(hover);
		if (reply) {
			ai.setHover(hover);
		}
		return reply;
	}

	/**
	 * Stores the membership count of an AssignmentInfo based on Map
	 * lookup using its ID.
	 * 
	 * @param ai
	 * @param countMap
	 * 
	 * @return
	 */
	public static void setAssignmentInfoMembers(AssignmentInfo ai, Map<Long, Integer> countMap) {
		Integer count = countMap.get(ai.getId());
		ai.setMembers((null == count) ? 0 : count.intValue());
	}

	/**
	 * Stores the title of an AssignmentInfo based on Map lookup using
	 * its ID.
	 * 
	 * Returns true if a title was stored and false otherwise.
	 * 
	 * @param ai
	 * @param titleMap
	 * 
	 * @return
	 */
	public static boolean setAssignmentInfoTitle(AssignmentInfo ai, Map<Long, String> titleMap) {
		String title = titleMap.get(ai.getId());
		boolean reply = MiscUtil.hasString(title);
		if (reply) {
			ai.setTitle(title);
		}
		return reply;
	}

	/**
	 * Stores a GwtPresenceInfo of an AssignmentInfo based on Map
	 * lookup using its ID.
	 * 
	 * @param ai
	 * @param presenceMap
	 */
	public static void setAssignmentInfoPresence(AssignmentInfo ai, Map<Long, GwtPresenceInfo> presenceMap) {
		GwtPresenceInfo pi = presenceMap.get(ai.getId());
		if (null == pi) pi = GwtServerHelper.getPresenceInfoDefault();
		ai.setPresence(pi);
		ai.setPresenceDude(GwtServerHelper.getPresenceDude(pi));
	}

	/**
	 * Stores a user's workspace ID of an AssignmentInfo based on a Map
	 * lookup using its ID.
	 * 
	 * @param ai
	 * @param presenceUserWSIdsMap
	 */
	public static void setAssignmentInfoPresenceUserWSId(AssignmentInfo ai, Map<Long, Long> presenceUserWSIdsMap) {
		Long presenceUserWSId = presenceUserWSIdsMap.get(ai.getId());
		ai.setPresenceUserWSId(presenceUserWSId);
	}
}
