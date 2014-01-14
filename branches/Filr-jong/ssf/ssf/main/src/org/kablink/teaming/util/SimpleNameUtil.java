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

import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.web.WebKeys;


public class SimpleNameUtil {

	private static final int SIMPLE_NAME_MAX_LENGTH = 128;
	
	private static final char[] ALLOWED_CHARS_FOR_URL = new char[] {'_', '-', '!', '.', '~', '\'', '(', ')', '*', '/' };
	private static final char[] ALLOWED_CHARS_FOR_EMAIL = new char[] {'_','-','!','.','~','*','#','$','%','/','?','|','^','{','}','`','&','+','='};
	
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
        
        SimpleName sn = getCoreDao().loadSimpleName(simpleName, zoneId);
		
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
	 * whitespace<br>
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
	
	/**
	 * Validate the simple name for use as the local part of an email address. 
	 * Returns <code>true</code> if it is safe to use the name as the local part
	 *   of an email address, <code>false</code> otherwise.
	 * The valid characters (per RFC 2822) are<br>
	 * upper- and lower-case letters<br>
	 * digits<br>
	 * The following characters: _-!.~'*#$%/?|^{}`&+=<br>
	 *   with the exception that . not appear as the first or last character,
	 *   nor twice in a row.
	 *   
	 * RFC 2822 also allows "quoted string" email addresses (like "quoted string"@foo.bar),
	 *  but we are not allowing those.
	 * 
	 * @param simpleName
	 * @return
	 */
	public static boolean validateForEmail(String simpleName)
	{
		if(simpleName.length() > SIMPLE_NAME_MAX_LENGTH)
			return false;
		
		char[] c = simpleName.toCharArray();
		outer:
		for(int i = 0; i < c.length; i++) {
			if(!Character.isLetterOrDigit(c[i])) {
				for(int j = 0; j < ALLOWED_CHARS_FOR_EMAIL.length; j++) {
					if(c[i] == ALLOWED_CHARS_FOR_EMAIL[j])
						continue outer;
				}
				return false;
			}
		}
		
		return true;
	}
	
	protected static String getURL(boolean isSecure, String hostname, int port, Long binderId, String entityType) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true, isSecure, hostname, port);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entityType);
		return adapterUrl.toString();
	}
	
	protected static ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
	
	protected static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
}
