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
package org.kablink.teaming.spring.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.module.zone.ZoneModule;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.security.core.GrantedAuthority;


public class SsfContextMapper  implements UserDetailsContextMapper {
	protected Log logger = LogFactory.getLog(getClass());

	protected Map<String,String> attributeMap;
	
	public SsfContextMapper()
	{
		this.attributeMap = new HashMap<String,String>();
	}
	
	/* For programmatic creation */
	public SsfContextMapper(ZoneModule zoneModule, Map<String,String> mappings)
	{
		this();
		setZoneModule(zoneModule);
		setMappings(mappings);
	}
	
	private ZoneModule zoneModule;
	public ZoneModule getZoneModule() { return zoneModule; }
	public void setZoneModule(ZoneModule zoneModule) { this.zoneModule = zoneModule; }

	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities)
	{
		SsfUserDetails details =new SsfUserDetails(ctx, attributeMap, username, authorities); 
		return details;
	}
	
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx)
	{
	}

	public void setMappings(Map<String,String> mappings)
	{
		this.attributeMap = mappings;
	}

	public static class SsfUserDetails extends HashMap<String,String> implements UserDetails
	{
		String username;
		Collection<? extends GrantedAuthority> authorities;
		
		public SsfUserDetails(DirContextOperations ctx, Map<String,String> mapping, String username, Collection<? extends GrantedAuthority> authorities)
		{
			this.username = username;
			this.authorities = authorities;
			for(String attr : mapping.keySet()) {
				String value = ctx.getStringAttribute(attr);
				if(value != null) {
					this.put(mapping.get(attr), value);
				}
			}
		}
		
		public SsfUserDetails(String username)
		{
			this.username = username;
			this.authorities = new ArrayList<GrantedAuthority>();
		}

		public SsfUserDetails(String username, Map<String, String> attributes) {
			this.username = username;
			this.putAll(attributes);
		}
		
		public Collection<? extends GrantedAuthority> 	getAuthorities() {
			 	return authorities;
		}
		public String getPassword()
		{
			 return "";
		}
		public String getUsername()
		 {
			 return username;
		 }
		public boolean isAccountNonExpired()
		 {
			 return true;
		 }
		public boolean isAccountNonLocked()
		 {
			 return true;
		 }
		public boolean isCredentialsNonExpired()
		 {
			 return true;
		 }
		public boolean	isEnabled()
		 {
			 return true;
		 }
	}

}
