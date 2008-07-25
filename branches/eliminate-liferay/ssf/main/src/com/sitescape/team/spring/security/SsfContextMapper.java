package com.sitescape.team.spring.security;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.security.GrantedAuthority;

import com.sitescape.team.asmodule.zonecontext.ZoneContextHolder;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.util.SZoneConfig;

public class SsfContextMapper  implements UserDetailsContextMapper {
	protected Log logger = LogFactory.getLog(getClass());

	protected Map<String,String> attributeMap;
	
	public SsfContextMapper()
	{
		this.attributeMap = new HashMap<String,String>();
	}
	
	/* For programmatic creation */
	public SsfContextMapper(ZoneModule zoneModule, String mappings)
	{
		this();
		setZoneModule(zoneModule);
		setMappings(mappings);
	}
	
	private ZoneModule zoneModule;
	public ZoneModule getZoneModule() { return zoneModule; }
	public void setZoneModule(ZoneModule zoneModule) { this.zoneModule = zoneModule; }

	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
											GrantedAuthority[] authority)
	{
		SsfUserDetails details =new SsfUserDetails(ctx, attributeMap, username, authority); 
		AuthenticationManagerUtil.authenticate(getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName()), username, "", true, false, true, details, null);
		return details;
	}
	
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx)
	{
	}

	public void setMappings(String mappings)
	{
		Document doc = null;
		
		try {
			doc = DocumentHelper.parseText(mappings);
		} catch(Exception e) {
			logger.warn("Unable to parse attribute mapping: " + mappings);
			return;
		}
		
		for(Object o : doc.selectNodes("//mapping")) {
			Node node = (Node) o;
			String attr = node.selectSingleNode("@from").getText();
			String field = node.selectSingleNode("@to").getText();
			attributeMap.put(attr, field);
		}
	}
	static class SsfUserDetails extends HashMap<String,String> implements UserDetails
	{
		String username;
		GrantedAuthority[] authorities;
		
		public SsfUserDetails(DirContextOperations ctx, Map<String,String> mapping, String username, GrantedAuthority[] authorities)
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

		public GrantedAuthority[] 	getAuthorities() {
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
