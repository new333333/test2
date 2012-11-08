/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.extuser;

import java.util.HashMap;
import java.util.Map;

import org.jasypt.encryption.StringEncryptor;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.StringUtil;

/**
 * @author jong
 *
 */
public class ExternalUserUtil {
	
	public static final String QUERY_FIELD_NAME_EXTERNAL_USER_ENCODED_TOKEN = "euet";
	
	public static final String SESSION_KEY_EXTERNAL_USER_ENCODED_TOKEN = ExternalUserUtil.class.getSimpleName() + "_" + QUERY_FIELD_NAME_EXTERNAL_USER_ENCODED_TOKEN;

	public static final String SESSION_KEY_FOR_OPENID_PROVIDER_NAME = ExternalUserUtil.class.getSimpleName() + "_openidprovidername";
	
	public static final String OPENID_PROVIDER_NAME_GOOGLE = "google";
	public static final String OPENID_PROVIDER_NAME_YAHOO = "yahoo";
	public static final String OPENID_PROVIDER_NAME_AOL = "aol";
	public static final String OPENID_PROVIDER_NAME_MYOPENID = "myopenid";
	
	public static String encodeUserToken(User user) {
		return Long.toHexString(user.getId().longValue()) + "_" + user.getPrivateDigest();
	}
	
	public static Long getUserId(String encodedUserToken) {
		return Long.parseLong(encodedUserToken.substring(0, encodedUserToken.indexOf('_')), 16);
	}
	
	public static String getPrivateDigest(String encodedUserToken) {
		return encodedUserToken.substring(encodedUserToken.indexOf('_')+1);
	}
	
	public static Map<String, String> getQueryParams(String url) {  
	    Map<String, String> map = new HashMap<String, String>();  
		int index = url.indexOf('?');
		if(index < 0)
			return map;
		String query = url.substring(index+1);
	    String[] params = StringUtil.split(query, "&");  
	    for (String param : params) {  
	    	String[] elem = StringUtil.split(param, "=");
	        if(elem.length == 2)
	        	map.put(elem[0], elem[1]);  
	    }  
	    return map;  
	}  
	
	private static StringEncryptor getStringEncryptor() {
		return (StringEncryptor) SpringContextUtil.getBean("encryptor");
	}

	
	public static void main(String[] args) throws Exception {
		long l = 209;
		String hex = Long.toHexString(l);
		System.out.println(hex);
		long l2 = Long.parseLong(hex, 16);
		System.out.println(l2);
	}
}
