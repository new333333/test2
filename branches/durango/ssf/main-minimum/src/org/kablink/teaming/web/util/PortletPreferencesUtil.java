/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import javax.portlet.PortletPreferences;

public class PortletPreferencesUtil {

	/**
	 * Returns the first String value associated with the specified key of the
	 * specified preference. If there is one or more preference values associated
	 * with the given key it returns the first associated value. If there are 
	 * no preference values associated with the given key, or the backing
	 * preference database is unavailable, it returns the given default value.
	 * 
	 * @param prefs preferences object
	 * @param key key for which the associated value is to be returned
	 * @param def the value to be returned in the event that there is no
	 * value available associated with this <code>key</code>.
	 * @return the value associated with <code>key</code>, or <code>def</code>
	 * if no value is associated with <code>key</code>, or the backing store
	 * is inaccessible.
	 */
	public static String getValue(PortletPreferences prefs, String key, String def) {
		// The sole purpose of this method is to provide a workaround for a bug 
		// in some portal (specifically JBoss Portal 2.6DR1). Unfortunately,
		// this makes each invocation a bit more expensive. 
		
		try {
			return prefs.getValue(key, def);
		}
		catch(IllegalArgumentException e) {
			throw e; // rethrow
		}
		catch(Exception e2) { // workaround for a bug in portal
			String[] value = (String[]) prefs.getMap().get(key);
			
			if(value != null && value.length > 0)
				return value[0];
			else
				return def;
		}
	}
	
	/**
	 * Returns the String array value associated with the specified key in this 
	 * preference. Returns the specified default if there is no value associated 
	 * with the key, or if the backing store is inaccessible. If the implementation
	 * supports stored defaults and such a default exists and is accessible, 
	 * it is used in favor of the specified default.
	 * 
	 * @param prefs preferences object
	 * @param key key for which associated value is to be returned.
	 * @param def the value to be returned in the event that this preference 
	 * node has no value associated with key or the associated value cannot be
	 * interpreted as a String array, or the backing store is inaccessible.
	 * @return the String array value associated with key, or def if the 
	 * associated value does not exist.
	 */
	public static String[] getValues(PortletPreferences prefs, String key, String[] def) {
		// The sole purpose of this method is to provide a workaround for a bug 
		// in some portal (specifically JBoss Portal 2.6DR1). Unfortunately,
		// this makes each invocation a bit more expensive. 
		
		try {
			return prefs.getValues(key, def);
		}
		catch(IllegalArgumentException e) {
			throw e; // rethrow
		}
		catch(Exception e2) { // workaround for a bug in portal
			String[] value = (String[]) prefs.getMap().get(key);
			
			if(value != null)
				return value;
			else
				return def;
		}
	}
}
