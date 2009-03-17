package org.kablink.teaming.spring.security;

import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.domain.AuthenticationConfig;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.util.SZoneConfig;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.anonymous.AnonymousAuthenticationToken;
import org.springframework.security.providers.anonymous.AnonymousProcessingFilter;


public class ZoneAwareAnonymousProcessingFilter extends
		AnonymousProcessingFilter {

	private ZoneModule zoneModule;
	public ZoneModule getZoneModule() { return zoneModule; }
	public void setZoneModule(ZoneModule zoneModule) { this.zoneModule = zoneModule; }

	private AuthenticationModule authenticationModule;
	public AuthenticationModule getAuthenticationModule() {
		return authenticationModule;
	}
	public void setAuthenticationModule(AuthenticationModule authenticationModule) {
		this.authenticationModule = authenticationModule;
	}

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
	
	@Override
	protected boolean applyAnonymousForThisRequest(HttpServletRequest request) {
		if(SecurityContextHolder.getContext().getAuthentication() == null) {
	    	Long zoneId = getZoneModule().getZoneIdByVirtualHost(ZoneContextHolder.getServerName());
	    	AuthenticationConfig config = getAuthenticationModule().getAuthenticationConfigForZone(zoneId);
	    	return config.isAllowAnonymousAccess();
		} else {
			return false;
		}
	}
}
