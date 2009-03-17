/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.util;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Iterator;

public class LongIdUtil {
	//different services used different values
	private static String SPACE_SEPARATOR = " ";
	private static String COMMA_SEPARATOR = ",";
	
	public static Set<Long> getIdsAsLongSet(String []sIds) {
		Set<Long> memberIds = new HashSet();
		if (sIds != null) {
			for (int i = 0; i < sIds.length; i++) {
				memberIds.addAll(getIdsAsLongSet(sIds[i]));
			}
		}
		return memberIds;		
	}
	
	public static Set<Long> getIdsAsLongSet(Collection ids) {
		Set<Long> memberIds = new HashSet();
		if (ids != null) {
			Iterator it = ids.iterator();
			while (it.hasNext()) {
				Object id = it.next();
				if (id instanceof Long) {
					memberIds.add((Long)id);
				} else {
					try  {
						memberIds.add(Long.parseLong((String)id));
					} catch (NumberFormatException e) {}
				}
			}
		}
		return memberIds;		
	}
	public static Set<String> getIdsAsStringSet(Collection ids) {
		Set<String> memberIds = new HashSet();
		if (ids != null) {
			Iterator it = ids.iterator();
			while (it.hasNext()) {
				Object id = it.next();
				memberIds.add(id.toString());
			}
		}
		return memberIds;		
	}
	public static Set<Long> getIdsAsLongSet(String ids) {
		if(ids == null) return new HashSet<Long>();
		if (ids.contains(",")) return getIdsAsLongSet(ids, COMMA_SEPARATOR);
		return getIdsAsLongSet(ids, SPACE_SEPARATOR);
	}
	public static Set<Long> getIdsAsLongSet(String ids, String separator) {
		Set<Long> idSet = new HashSet<Long>();
		if (ids == null) return idSet;
		String [] sIds = ids.split(separator);
		for (int i = 0; i < sIds.length; i++) {
			try  {
				idSet.add(Long.parseLong(sIds[i].trim()));
			} catch (Exception ex) {};
		}
		return idSet;
	}
	public static Set<String> getIdsAsStringSet(String ids) {
		if (ids.contains(",")) return getIdsAsStringSet(ids, COMMA_SEPARATOR);
		return getIdsAsStringSet(ids, SPACE_SEPARATOR);
	}
	public static Set<String> getIdsAsStringSet(String ids, String separator) {
		Set<String> idSet = new HashSet<String>();
		if (ids == null) return idSet;
		String [] sIds = ids.split(separator);
		for (int i = 0; i < sIds.length; i++) {
			try  {
				Long.parseLong(sIds[i].trim());
				//continue if valid long
				idSet.add(sIds[i].trim());
			} catch (Exception ex) {};
		}
		return idSet;
	}
	public static String getIdsAsString(String []ids) {
		StringBuffer buf = new StringBuffer();
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				String[] uIds = ids[i].split(SPACE_SEPARATOR);
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
			Object id = iter.next();
			if (id == null) continue;
			buf.append(id.toString());
			buf.append(SPACE_SEPARATOR);
		}
		return buf.toString();
	}

}
