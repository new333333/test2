/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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


/**
 * Class used to communicate information about an Activity Stream
 * between the MainMenuControl and its registered ActionHandler's.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamInfo {
	/**
	 * Enumeration describing the type of an Activity Stream.
	 * 
	 * @author drfoster@novell.com
	 */
	public enum ActivityStream {
		UNKNOWN(0),
		CURRENT_BINDER(1),
		FOLLOWED_PEOPLE(2),
		FOLLOWED_PLACES(3),
		MY_FAVORITES(4),
		MY_TEAMS(5),
		SITE_WIDE(6);

		private int m_asValue;
		
		/*
		 * Constructor method.
		 */
		private ActivityStream(int asValue) {
			m_asValue = asValue;
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
			case 0:  reply = UNKNOWN;         break;
			case 1:  reply = CURRENT_BINDER;  break;
			case 2:  reply = FOLLOWED_PEOPLE; break;
			case 3:  reply = FOLLOWED_PLACES; break;
			case 4:  reply = MY_FAVORITES;    break;
			case 5:  reply = MY_TEAMS;        break;
			case 6:  reply = SITE_WIDE;       break;
			}
			return reply;
		}
	}

	private ActivityStream m_as;
	private String[] m_binderIds;
	
	/**
	 * Constructor methods.
	 * 
	 * @param as
	 * @param binderIds
	 */
	public ActivityStreamInfo(ActivityStream as, String[] binderIds) {
		// Simply store the parameters.
		m_as = as;
		m_binderIds = binderIds;
	}
	
	public ActivityStreamInfo(ActivityStream as) {
		// Always use the initial form of the constructor.
		this(as, new String[0]);
	}
	
	public ActivityStreamInfo(ActivityStream as, String binderId) {
		// Always use the initial form of the constructor.
		this(as, new String[]{binderId});
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
		for (int i = 0; i < m_binderIds.length; i += 1) {
			if (0 < i) {
				reply.append(",");
			}
			reply.append(m_binderIds[i]);
		}
		
		return reply.toString();
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
		ActivityStreamInfo reply = new ActivityStreamInfo(ActivityStream.UNKNOWN);
		
		// Do we have a string to parse?
		if (GwtClientHelper.hasString(s)) {
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
				if (GwtClientHelper.hasString(binderIds)) {
					// Yes!  Does it contain more than one binder ID?
					if (0 > binderIds.indexOf(',')) {
						// No!  Construct the appropriate
						// ActivityStreamInfo object.
						reply = new ActivityStreamInfo(as, binderIds);
					}
					
					else {
						// Yes, it contains more than one binder ID!
						// Split them and construct the appropriate
						// ActivityStreamInfo object.
						reply = new ActivityStreamInfo(as, binderIds.split(","));
					}
				}
				
				else {
					// No, it doesn't contain any binder IDs!
					// Construct the appropriate ActivityStreamInfo
					// object.
					reply = new ActivityStreamInfo(as);
				}
			}
		}
		
		// If we get here, reply refers to the ActivityStreamInfo
		// object that matches the string received.  Return it. 
		return reply;
	}
}
