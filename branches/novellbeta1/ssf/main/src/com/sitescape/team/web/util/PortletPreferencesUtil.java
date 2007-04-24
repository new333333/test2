/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.web.util;

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
