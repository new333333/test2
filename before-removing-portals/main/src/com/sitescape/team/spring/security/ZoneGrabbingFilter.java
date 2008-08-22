package com.sitescape.team.spring.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.ui.SpringSecurityFilter;

import com.sitescape.team.asmodule.zonecontext.ZoneContextHolder;

public class ZoneGrabbingFilter extends SpringSecurityFilter {
	public ZoneGrabbingFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void doFilterHttp(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		
		ZoneContextHolder.setServerName(request.getServerName());
		chain.doFilter(request, response);
	}

	public int getOrder() {
		return 0;
	}
}
