/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about an activity stream
 * between the MainMenuControl and its event handlers.
 * 
 * Notes:
 * 1. The string representation of an ActivityStreamInfo object as
 *    obtained using the getStringValue() method is stored in a user's
 *    preferences as part of persisting their most recently visited
 *    activity stream.
 * 2. When read out of a user's preferences, the parse() method is used
 *    to re-construct the ActivityStreamInfo object.
 *    
 * Please be aware before making any changes that may break this.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamInfo
	implements
		ClientActionParameter,
		ClientEventParameter,
		IsSerializable,
		VibeRpcResponseData {
	/**
	 * Enumeration describing the type of an activity stream.
	 */
	public enum ActivityStream implements IsSerializable {
		// *** WARNING *** WARNING *** WARNING *** WARNING ***
		// ***
		// *** The integer representation of these enumeration values
		// *** are stored in a user's preferences as part of persisting
		// *** their most recently visited activity stream.
		// ***
		// *** Changing the integer values assigned will BREAK that.
		// *** Please, consider any changes made CAREFULLY!!!
		// ***
		// *** WARNING *** WARNING *** WARNING *** WARNING ***
		UNKNOWN(               0),
		CURRENT_BINDER(        1),
		FOLLOWED_PEOPLE(       2),
		FOLLOWED_PERSON(       3),
		FOLLOWED_PLACES(       4),
		FOLLOWED_PLACE(        5),
		MY_FAVORITES(          6),
		MY_FAVORITE(           7),
		MY_TEAMS(              8),
		MY_TEAM(               9),
		SITE_WIDE(            10),
		SPECIFIC_BINDER(      11),
		SPECIFIC_FOLDER(      12),
		MY_FILES(             13),
		MY_FILE(              14),
		NET_FOLDERS(          15),
		NET_FOLDER(           16),
		SHARED_BY_ME(         17),
		SHARED_BY_ME_FOLDER(  18),
		SHARED_WITH_ME(       19),
		SHARED_WITH_ME_FOLDER(20),
		SHARED_PUBLIC(        21),
		SHARED_PUBLIC_FOLDER( 22);

		private int m_asValue;
		
		/*
		 * Constructor method.
		 */
		private ActivityStream(int asValue) {
			m_asValue = asValue;
		}

		/**
		 * Return the localized name of this stream.
		 */
		public String getStreamName()
		{
			return toString();
		}
		
		
		/**
		 * Returns the integer value of the ActivityStream.
		 * 
		 * @return
		 */
		public int getValue() {
			return m_asValue;
		}

		/**
		 * Returns an ActivityStream enumeration value based on its
		 * integer equivalent.
		 * 
		 * @param asValue
		 * 
		 * @return
		 */
		public static ActivityStream valueOf(int asValue) {
			ActivityStream reply;
			switch (asValue) {
			default:
			case  0:  reply = UNKNOWN;               break;
			case  1:  reply = CURRENT_BINDER;        break;
			case  2:  reply = FOLLOWED_PEOPLE;       break;
			case  3:  reply = FOLLOWED_PERSON;       break;
			case  4:  reply = FOLLOWED_PLACES;       break;
			case  5:  reply = FOLLOWED_PLACE;        break;
			case  6:  reply = MY_FAVORITES;          break;
			case  7:  reply = MY_FAVORITE;           break;
			case  8:  reply = MY_TEAMS;              break;
			case  9:  reply = MY_TEAM;               break;
			case 10:  reply = SITE_WIDE;             break;
			case 11:  reply = SPECIFIC_BINDER;       break;
			case 12:  reply = SPECIFIC_FOLDER;       break;
			case 13:  reply = MY_FILES;              break;
			case 14:  reply = MY_FILE;               break;
			case 15:  reply = NET_FOLDERS;           break;
			case 16:  reply = NET_FOLDER;            break;
			case 17:  reply = SHARED_BY_ME;          break;
			case 18:  reply = SHARED_BY_ME_FOLDER;   break;
			case 19:  reply = SHARED_WITH_ME;        break;
			case 20:  reply = SHARED_WITH_ME_FOLDER; break;
			case 21:  reply = SHARED_PUBLIC;         break;
			case 22:  reply = SHARED_PUBLIC_FOLDER;  break;
			}
			return reply;
		}
	}

	private ActivityStream	m_as;
	private String			m_title = "";
	private String[]		m_binderIds;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ActivityStreamInfo() {
		// Nothing to do.
	}
	
	/**
	 * Returns the ActivityStreamInfo object's ActivityStream
	 * enumeration value.
	 * 
	 * @return
	 */
	public ActivityStream getActivityStream() {
		return m_as;
	}
	
	/**
	 * Returns the array of binder IDs this ActvitityStreamInfo object
	 * corresponds to.
	 * 
	 * @return
	 */
	public String[] getBinderIds() {
		return m_binderIds;
	}
	
	/**
	 * Return the name of this stream, ie "My Teams", "My Favorites"...
	 */
	public String getStreamName()
	{
		return m_as.getStreamName();
	}
	
	
	/**
	 * Returns a String representation of an ActivityStreamInfo object.
	 * 
	 * @return
	 */
	public String getStringValue() {
		StringBuffer reply = new StringBuffer();
		
		reply.append(String.valueOf(m_as.getValue()));
		reply.append(":");
		int count = ((null == m_binderIds) ? 0 : m_binderIds.length);
		for (int i = 0; i < count; i += 1) {
			if (0 < i) {
				reply.append(",");
			}
			reply.append(m_binderIds[i]);
		}
		
		return reply.toString();
	}

	/**
	 * Returns the title attached to this ActivityStreamInfo.
	 * 
	 * @return
	 */
	public String getTitle() {
		return m_title;
	}
	
	/**
	 * Returns an ActivityStreamInfo object based on the String
	 * representation returned by a call to getStringValue().
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static ActivityStreamInfo parse(String s) {
		// Setup a default unknown ActivityStreamInfo to make it easy
		// to bail in the event of an error.
		ActivityStreamInfo reply = new ActivityStreamInfo();
		reply.setActivityStream(ActivityStream.UNKNOWN);
		
		// Do we have a string to parse?
		if ((null != s) && (0 < s.length())) {
			// Yes!  Does it have the required 2 parts, split by a ':'?
			String[] asParts = s.split(":");
			int parts = asParts.length;
			switch (parts) {
			default:
				// No!  We'll return the default we constructed above.
				break;
				
			case 1:				
			case 2:
				// Yes!  Does it contain any binder IDs?
				ActivityStream as = ActivityStream.valueOf(Integer.parseInt(asParts[0]));
				String binderIds = ((1 == parts) ? null : asParts[1]);
				if ((null != binderIds) && (0 < binderIds.length())) {
					// Yes!  Does it contain more than one binder ID?
					if (0 > binderIds.indexOf(',')) {
						// No!  Construct the appropriate
						// ActivityStreamInfo object.
						reply.setActivityStream(as);
						reply.setBinderId(binderIds);
					}
					
					else {
						// Yes, it contains more than one binder ID!
						// Split them and construct the appropriate
						// ActivityStreamInfo object.
						reply.setActivityStream(as);
						reply.setBinderIds(binderIds.split(","));
					}
				}
				
				else {
					// No, it doesn't contain any binder IDs!
					// Construct the appropriate ActivityStreamInfo
					// object.
					reply.setActivityStream(as);
				}
			}
		}
		
		// If we get here, reply refers to the ActivityStreamInfo
		// object that matches the string received.  Return it. 
		return reply;
	}

	/**
	 * Returns true if two ActivityStreams are equals and false
	 * otherwise.
	 */
	public boolean isEqual(ActivityStreamInfo asi2) {
		// If we've got nothing to compare...
		if (null == asi2) {
			// ...they're not equal.
			return false;
		}

		// If the ActivityStream enumeration values don't match...
		ActivityStream as1 =      getActivityStream();
		ActivityStream as2 = asi2.getActivityStream();
		if (!(as1.equals(as2))) {
			// ...they don't match.
			switch (as1) {
			default:
				return false;
				
			case CURRENT_BINDER:
			case SPECIFIC_BINDER:
			case SPECIFIC_FOLDER:
				switch (as2) {
				default:
					return false;
					
				case CURRENT_BINDER:
				case SPECIFIC_BINDER:
				case SPECIFIC_FOLDER:
					break;
				}
				break;
			}
		}

		// Do we need to check binder IDs for this activity stream?
		boolean checkBinderIds;
		switch (as1) {
		case MY_FILES:
		case NET_FOLDERS:
		case SHARED_BY_ME:
		case SHARED_WITH_ME:
		case SHARED_PUBLIC:  checkBinderIds = false; break;
		default:             checkBinderIds = true;  break;
		}
		if (checkBinderIds) {
			// Yes!  If they don't contain the same number of binder
			// IDs...
			String[] bIds1 =      getBinderIds(); int c1 = ((null == bIds1) ? (-1) : bIds1.length);
			String[] bIds2 = asi2.getBinderIds(); int c2 = ((null == bIds2) ? (-1) : bIds2.length);
			if (c1 != c2) {
				// ...they don't match.
				return false;
			}
	
			// If both lists were null or empty...
			if (0 >= c1) {
				// ...they match.
				return true;
			}
	
			// Scan the binder IDs...
			for (int i = 0; i < c1; i += 1) {
				// ...if any don't match...
				String id1 = bIds1[i]; if (null == id1) id1 = "";
				String id2 = bIds2[i]; if (null == id2) id2 = "";
				if (!(id1.equals(id2))) {
					// ...they're not equal.
					return false;
				}
			}
		}

		// If we get here, the two ActivityStreamInfo's match.  Return true.
		return true;
	}

	/**
	 * Stores the ActivityStream enumeration associated with this
	 * ActivityStreamInfo object.
	 * 
	 * @param as
	 */
	public void setActivityStream(ActivityStream as) {
		m_as = as;
	}

	/**
	 * Stores a binderId to be associated with this ActivityStreamInfo
	 * object.
	 * 
	 * @param binderId
	 */
	public void setBinderId(String binderId) {
		setBinderIds(new String[]{binderId});
	}

	/**
	 * Stores the binderIds to be associated with this
	 * ActivityStreamInfo object.
	 * 
	 * @param binderIds
	 */
	public void setBinderIds(String[] binderIds) {
		m_binderIds = binderIds;
	}

	/**
	 * Stores the title for this ActivityStreamInfo object.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		m_title = ((null == title) ? "" : title);
	}
}
