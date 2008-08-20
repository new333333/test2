package com.sitescape.team.spring.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.Authentication;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;
import org.springframework.security.providers.anonymous.AnonymousProcessingFilter;

import com.sitescape.team.asmodule.zonecontext.ZoneContextHolder;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.util.SZoneConfig;

public class ZoneAwareAnonymousProcessingFilter extends
		AnonymousProcessingFilter {

	private ZoneModule zoneModule;
	public ZoneModule getZoneModule() { return zoneModule; }
	public void setZoneModule(ZoneModule zoneModule) { this.zoneModule = zoneModule; }

	@Override
    protected Authentication createAuthentication(HttpServletRequest request) {
        AnonymousAuthenticationToken auth = (AnonymousAuthenticationToken) super.createAuthentication(request);
        return new AnonymousAuthenticationToken(getKeyForZone(), auth.getPrincipal(), auth.getAuthorities());
    }
	
	protected String getKeyForZone()
	{
    	String zoneName = getZoneModule().getZoneNameByVirtualHost(ZoneContextHolder.getServerName());
    	return SZoneConfig.getGuestUserName(zoneName);
	}
}
