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

import com.sitescape.team.dao.CoreDao;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.SimpleName;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.web.WebKeys;

public class SimpleNameUtil {

	private static final int SIMPLE_NAME_MAX_LENGTH = 128;
	
	private static final char[] ALLOWED_CHARS_FOR_URL = new char[] {'_', '-', '!', '.', '~', '\'', '(', ')', '*', '/' };
	
	/**
	 * Resolve the simple name to a full adapter URL.
	 * 
	 * @param isSecure
	 * @param hostname
	 * @param port
	 * @param simpleName
	 * @return
	 */
	public static String resolveURL(boolean isSecure, String hostname, int port, String simpleName) {
		hostname = hostname.toLowerCase();
		simpleName = simpleName.toLowerCase();
        Long zoneId = getZoneModule().getZoneIdByVirtualHost(hostname);
        
        SimpleName sn = getCoreDao().loadSimpleName(simpleName, SimpleName.TYPE_URL, zoneId);
		
        if(sn != null)        
        	return getURL(isSecure, hostname, port, sn.getBinderId(), sn.getBinderType());
        else
        	return null;
	}

	/**
	 * Validate the simple name for use in URL. 
	 * Returns <code>true</code> if it is safe to use the name in URL, <code>false</code> otherwise.
	 * The valid characters are<br>
	 * lower-case letters<br>
	 * digits<br>
	 * The following characters: _-!.~'()*
	 * 
	 * @param simpleName
	 * @return
	 */
	public static boolean validateForURL(String simpleName) {
		if(simpleName.length() > SIMPLE_NAME_MAX_LENGTH)
			return false;
		
		if(!simpleName.equals(simpleName.toLowerCase()))
			return false;
		
		char[] c = simpleName.toCharArray();
		outer:
		for(int i = 0; i < c.length; i++) {
			if(!Character.isLetterOrDigit(c[i]) && !Character.isWhitespace(c[i])) {
				for(int j = 0; j < ALLOWED_CHARS_FOR_URL.length; j++) {
					if(c[i] == ALLOWED_CHARS_FOR_URL[j])
						continue outer;
				}
				return false;
			}
		}
		
		return true;
	}
	
	protected static String getURL(boolean isSecure, String hostname, int port, Long binderId, String entityType) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true, isSecure, hostname, port);
		if (entityType.equals(EntityIdentifier.EntityType.folder.name())) {
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_FOLDER_LISTING);
		} else if (entityType.equals(EntityIdentifier.EntityType.workspace.name())) {
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_WS_LISTING);
		} else if (entityType.equals(EntityIdentifier.EntityType.profiles.name())) {
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
		} else {
			throw new IllegalArgumentException("Unsupported entity type " + entityType);
		}
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		return adapterUrl.toString();
	}
	
	protected static ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
	
	protected static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
}
