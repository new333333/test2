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
package org.kablink.teaming.spring.security.openid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.spring.security.SsfContextMapper;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.security.openid.OpenIDAuthenticationToken;

/**
 * @author jong
 *
 */
public class OpenIDAuthenticationUserDetailsService implements AuthenticationUserDetailsService<OpenIDAuthenticationToken> {
	
	private static Log logger = LogFactory.getLog(OpenIDAuthenticationUserDetailsService.class);
	
	private static Map<String,String> attributeMapping = null;
	
	private static AttributesPostProcessing attributesPostProcessing;
	
	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.AuthenticationUserDetailsService#loadUserDetails(org.springframework.security.core.Authentication)
	 */
	@Override
	public UserDetails loadUserDetails(OpenIDAuthenticationToken token)
			throws UsernameNotFoundException {
		Map<String, String> vibeAttributes = convertOpenIDAttributesToVibeAttributes(token.getAttributes());
		
		getAttributesPostProcessing().postProcess(token.getAttributes(), vibeAttributes);
		
		String emailAddress = vibeAttributes.get("emailAddress");
		
		if(logger.isDebugEnabled())
			logger.debug("Processing OpenID identity [" + token.getName() + "] with email address [" + emailAddress + "]");
		
		if(emailAddress == null || emailAddress.isEmpty())
			throw new UsernameNotFoundException("User " + token.getName() + " has no email address");
		
		UserDetails details = new SsfContextMapper.SsfUserDetails(emailAddress, vibeAttributes);

		return details;
	}

	private Map<String,String> convertOpenIDAttributesToVibeAttributes(List<OpenIDAttribute> attributes) {
		Map<String,String> vibeAttributes = new HashMap<String,String>();
		Map<String,String> mapping = getAttributeMapping();
		for(OpenIDAttribute openidAttribute:attributes) {
			if(mapping.containsKey(openidAttribute.getName()) && openidAttribute.getCount() > 0) {
				String vibeAttributeName = mapping.get(openidAttribute.getName());
				if(vibeAttributeName != null) {
					String vibeAttributeValue = openidAttribute.getValues().get(0);
					if(vibeAttributeValue != null) {
						// Normalize email address to lower case.
						if(vibeAttributeName.equals("emailAddress"))
							vibeAttributeValue = vibeAttributeValue.toLowerCase();
						vibeAttributes.put(vibeAttributeName, vibeAttributeValue);
					}
				}
			}
		}
		return vibeAttributes;
	}
	
	private ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}
	
	private ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
	
	private CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
	private Map<String,String> getAttributeMapping() {
		if(attributeMapping == null) {
			int count = SPropsUtil.getInt("openid.attribute.mapping.count", 0);
			Map<String,String> map = new HashMap<String,String>(count);
			String key, value;
			for(int i = 0; i < count; i++) {
				key = SPropsUtil.getString("openid.attribute.mapping." + i + ".from");
				value = SPropsUtil.getString("openid.attribute.mapping." + i + ".to");
				map.put(key, value);
			}
			attributeMapping = map;
		}
		return attributeMapping;
	}
	
    private AttributesPostProcessing getAttributesPostProcessing() {
    	if(attributesPostProcessing == null) {
    		String className = SPropsUtil.getString("openid.attribute.mapping.postprocessing.class", 
    				"org.kablink.teaming.spring.security.openid.DefaultAttributesPostProcessing");
    		attributesPostProcessing = (AttributesPostProcessing) ReflectHelper.getInstance(className);
    	}
    	return attributesPostProcessing;
    }

}
