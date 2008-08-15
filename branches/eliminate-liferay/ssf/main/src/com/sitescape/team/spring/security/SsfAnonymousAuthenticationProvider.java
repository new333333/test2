package com.sitescape.team.spring.security;

import java.util.HashMap;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationProvider;

import com.sitescape.team.asmodule.zonecontext.ZoneContextHolder;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.security.authentication.AuthenticationManagerUtil;

public class SsfAnonymousAuthenticationProvider extends
		AnonymousAuthenticationProvider {

	protected String zoneName;
	public SsfAnonymousAuthenticationProvider(String zoneName)
	{
		this.zoneName = zoneName;
	}
	@Override
	public Authentication authenticate(Authentication authentication)
		throws AuthenticationException
	{
		authentication = super.authenticate(authentication);
		if(authentication != null) {
			AuthenticationManagerUtil.authenticate(zoneName, authentication.getName(), "", true, false, true, new HashMap(), null);
		}
		return authentication;
	}
}
