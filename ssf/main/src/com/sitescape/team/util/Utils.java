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
package com.sitescape.team.util;

import java.util.Iterator;
import java.util.Map;

import com.sitescape.team.domain.Workspace;
import com.sitescape.team.InternalException;
import com.sitescape.team.context.request.RequestContextHolder;

public class Utils {

	public static String toStringML(Map map) {
		StringBuffer buf = new StringBuffer();
		buf.append("{");

		Iterator<Map.Entry> i = map.entrySet().iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			Map.Entry e = i.next();
			Object key = e.getKey();
			Object value = e.getValue();
			if (key == map)
				buf.append("(this Map)");
			else
				buf.append(key);
			buf.append("=");
			if (value == map)
				buf.append("(this Map)");
			else
				buf.append(value);
			hasNext = i.hasNext();
			if (hasNext)
				buf.append(Constants.NEWLINE);
		}

		buf.append("}");
		return buf.toString();
	}
	
	public static String getZoneKey() {
		// If default zone
		//		zoneKey = zoneName
		// else
		//		zoneKey = zoneName + "_" + zoneId
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		String zoneKey;
		if(!zoneName.equals(SZoneConfig.getDefaultZoneName())) {
			Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
			if(zoneId == null)
				throw new InternalException("Zone id is missing from the request context");
			zoneKey = zoneName + "_" + zoneId;
		}
		else {
			zoneKey = zoneName;
		}
		return zoneKey;
	}
	//handle cases where request context not set
	public static String getZoneKey(Workspace zone) {
		// If default zone
		//		zoneKey = zoneName
		// else
		//		zoneKey = zoneName + "_" + zoneId
		String zoneKey;
		if(!zone.getName().equals(SZoneConfig.getDefaultZoneName())) {
			zoneKey = zone.getName() + "_" + zone.getId();
		}
		else {
			zoneKey = zone.getName();
		}
		return zoneKey;
	}
	
	public static RuntimeException launderUncheckedException(Throwable uncheckedException) {
		if(uncheckedException instanceof Error)
			throw (Error) uncheckedException;
		else if(uncheckedException instanceof RuntimeException)
			return (RuntimeException) uncheckedException;
		else
			throw new IllegalArgumentException(uncheckedException);
	}
}
