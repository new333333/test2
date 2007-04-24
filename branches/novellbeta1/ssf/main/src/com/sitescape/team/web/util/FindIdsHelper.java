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
import java.util.Set;
import java.util.HashSet;

public class FindIdsHelper {

	public static Set getIdsAsLongSet(String []sIds) {
		Set memberIds = new HashSet();
		if (sIds != null) {
			for (int i = 0; i < sIds.length; i++) {
				String[] ids = sIds[i].split(" ");
				for (int j = 0; j < ids.length; j++) {
					if (ids[j].length() > 0) memberIds.add(Long.valueOf(ids[j]));
				}
			}
		}
		return memberIds;		
	}
	
	public static Set getIdsAsLongSet(String ids) {
		return getIdsAsLongSet(ids, " ");
	}
	public static Set getIdsAsLongSet(String ids, String separator) {
		String [] sIds = ids.split(separator);
		Set<Long> idSet = new HashSet<Long>();
		for (int i = 0; i < sIds.length; i++) {
			try  {
				idSet.add(new Long(sIds[i]));
			} catch (Exception ex) {};
		}
		return idSet;
	}
	public static String getIdsAsString(String []ids) {
		StringBuffer buf = new StringBuffer();
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				String[] uIds = ids[i].split(" ");
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
	
}
