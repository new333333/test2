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
package org.kablink.teaming.util;

import java.util.Iterator;
import java.util.Map;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Workspace;


public class Utils {

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
