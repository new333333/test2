/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class contains a collection of list management utilities.
 * 
 * @author drfoster@novell.com
 */
public final class ListUtil {
	protected static Log m_logger = LogFactory.getLog(ListUtil.class);
	
	/*
	 * Constructor method.
	 * 
	 * Private to prevent the class from being instantiated.
	 */
	private ListUtil() {
		// Nothing to do.
		super();
	}
	
	/**
	 * Adds a Collection<Long> to a List<Long> if the values are not already
	 * there.
	 * 
	 * @param lListDest
	 * @param lCollectionSrc
	 */
	public static void addCollectionLongToListLongIfUnique(List<Long> lListDest, Collection<Long> lCollectionSrc) {
		// Do we have a Collection<Long> to add and a list to add it
		// to?
		if ((null != lCollectionSrc) && (null != lListDest)) {
			// Yes!  Scan the Collection<Long>.
			for (Long l:  lCollectionSrc) {
				// If the List<Long> doesn't contain this Long...
				if (!(lListDest.contains(l))) {
					// ...add it.
					lListDest.add(l);
				}
			}
		}
	}
	
	/**
	 * Adds a Long to a List<Long> if it's not already there.
	 * 
	 * @param lList
	 * @param l
	 */
	public static void addLongToListLongIfUnique(List<Long> lList, Long l) {
		// Do we have a Long to add and a list to add it to?
		if ((null != l) && (null != lList)) {
			// Yes!  If the List<Long> doesn't contain the Long...
			if (!(lList.contains(l))) {
				// ...add it.
				lList.add(l);
			}
		}
	}
	
	public static void addLongToListLongIfUnique(List<Long> lList, String l) {
		if (MiscUtil.hasString(l)) {
			// Always use the initial form of the method.
			addLongToListLongIfUnique(lList, Long.parseLong(l));
		}
	}
	
	/**
	 * Adds a String to a List<String> if it's not already there.
	 * 
	 * @param sList
	 * @param s
	 */
	public static void addStringToListStringIfUnique(List<String> sList, String s) {
		// Do we have a String to add and a list to add it to?
		if (MiscUtil.hasString(s) && (null != sList)) {
			// Yes!  If the List<String> doesn't contain the String...
			if (!(sList.contains(s))) {
				// ...add it.
				sList.add(s);
			}
		}
	}
	
	/**
	 * Adds a String to a List<String> if it's not already there,
	 * ignoring case.
	 * 
	 * @param sList
	 * @param s
	 */
	public static void addStringToListStringIfUniqueIgnoreCase(List<String> sList, String s) {
		// Do we have a String to add and a list to add it to?
		if (MiscUtil.hasString(s) && (null != sList)) {
			// Yes!  If the List<String> doesn't contain the String...
			boolean found = false;
			for (String scan:  sList) {
				if (scan.equalsIgnoreCase(s)) {
					found = true;
					break;
				}
			}
			if (!found) {
				// ...add it.
				sList.add(s);
			}
		}
	}
	
	/**
	 * Stores the strings from a String[] into a List<String>.
	 * 
	 * @param sA
	 * @param sL
	 */
	public static void arrayStringToListString(String[] sA, List<String> sL) {
		int c = ((null == sA) ? 0 : sA.length);
		for (int i = 0; i < c; i += 1) {
			sL.add(sA[i]);
		}
	}
	
	/**
	 * Converts the strings from a List<String> into a List<Long>.
	 * 
	 * @param sL
	 */
	public static List<Long> listStringToListLong(List<String> sL) {
		List<Long> reply = new ArrayList<Long>();
		int c = ((null == sL) ? 0 : sL.size());
		if (0 < c) {
			for (String s: sL) {
				reply.add(Long.parseLong(s));
			}
		}
		return reply;
	}
}
