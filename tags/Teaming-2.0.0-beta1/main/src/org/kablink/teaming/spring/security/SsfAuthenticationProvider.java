package org.kablink.teaming.spring.security;

import java.util.HashMap;

import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.security.authentication.AuthenticationManagerUtil;
import org.kablink.teaming.security.authentication.PasswordDoesNotMatchException;
import org.kablink.teaming.security.authentication.UserDoesNotExistException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;


public class SsfAuthenticationProvider implements AuthenticationProvider {
	
	protected String zoneName;
	
	public SsfAuthenticationProvider(String zoneName)
	{
		this.zoneName = zoneName;
	}

	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		try {
			User user = AuthenticationManagerUtil.authenticate(zoneName,
					(String) authentication.getName(), (String) authentication.getCredentials(),
					false, false, false, new HashMap(), null);

			UserDetails details = new SsfContextMapper.SsfUserDetails(user.getName());
			UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(details, authentication.getCredentials(), new GrantedAuthority[0]);
			result.setDetails(details);
			return result;
		} catch(PasswordDoesNotMatchException e) {
			throw new BadCredentialsException("Bad credentials", e);
		} catch(UserDoesNotExistException e) {
			throw new UsernameNotFoundException("No such user", e);
		}
	}

	public boolean supports(Class authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
