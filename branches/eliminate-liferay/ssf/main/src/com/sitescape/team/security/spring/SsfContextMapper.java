package com.sitescape.team.security.spring;

import java.util.HashMap;

import org.springframework.security.userdetails.ldap.UserDetailsContextMapper;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.security.GrantedAuthority;

import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.util.SZoneConfig;

public class SsfContextMapper  implements UserDetailsContextMapper {

	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
											GrantedAuthority[] authority)
	{
		AuthenticationManagerUtil.authenticate(SZoneConfig.getDefaultZoneName(), username, null, true, false, true, new HashMap(), null);
		return new SsfUserDetails(username, authority);
	}
	
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx)
	{
	}

	static class SsfUserDetails implements UserDetails
	{
		String username;
		GrantedAuthority[] authorities;
		
		public SsfUserDetails(String username, GrantedAuthority[] authorities)
		{
			this.username = username;
			this.authorities = authorities;
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
