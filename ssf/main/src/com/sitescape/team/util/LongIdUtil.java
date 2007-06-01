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
package com.sitescape.team.util;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

public class LongIdUtil {
	public static final String DEFAULT_SEPARATOR=" ";

	public static Set getIdsAsLongSet(String []sIds) {
		Set memberIds = new HashSet();
		if (sIds != null) {
			for (int i = 0; i < sIds.length; i++) {
				String[] ids = sIds[i].split(DEFAULT_SEPARATOR);
				for (int j = 0; j < ids.length; j++) {
					if (ids[j].length() > 0) memberIds.add(Long.valueOf(ids[j]));
				}
			}
		}
		return memberIds;		
	}
	
	public static Set getIdsAsLongSet(String ids) {
		return getIdsAsLongSet(ids, DEFAULT_SEPARATOR);
	}
	public static Set getIdsAsLongSet(String ids, String separator) {
		Set<Long> idSet = new HashSet<Long>();
		if (ids == null) return idSet;
		String [] sIds = ids.split(separator);
		for (int i = 0; i < sIds.length; i++) {
			try  {
				idSet.add(new Long(sIds[i]));
			} catch (Exception ex) {};
		}
		return idSet;
	}
	public static Set getIdsAsStringSet(String ids) {
		return getIdsAsStringSet(ids, DEFAULT_SEPARATOR);
	}
	public static Set getIdsAsStringSet(String ids, String separator) {
		Set<String> idSet = new HashSet<String>();
		if (ids == null) return idSet;
		String [] sIds = ids.split(separator);
		for (int i = 0; i < sIds.length; i++) {
			idSet.add(sIds[i]);
		}
		return idSet;
	}
	public static String getIdsAsString(String []ids) {
		StringBuffer buf = new StringBuffer();
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				String[] uIds = ids[i].split(DEFAULT_SEPARATOR);
				for (int j = 0; j < uIds.length; j++) {
					if (uIds[j].length() > 0) {
						buf.append(uIds[j].trim());
						buf.append(" ");
					}
				}
			}
		}
		return buf.toString();
	}
	public static String getIdsAsString(Collection ids) {
		StringBuffer buf = new StringBuffer();
		if (ids == null) return "";
		for (Iterator iter=ids.iterator(); iter.hasNext();) {
			buf.append(iter.next().toString());
			buf.append(DEFAULT_SEPARATOR);
		}
		return buf.toString();
	}
	
}
