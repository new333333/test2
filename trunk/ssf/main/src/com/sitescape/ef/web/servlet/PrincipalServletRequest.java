package com.sitescape.ef.web.servlet;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PrincipalServletRequest extends HttpServletRequestWrapper {

	private Principal principal;
	
	public PrincipalServletRequest(HttpServletRequest request, Principal principal) {
		super(request);
		this.principal = principal;
	}

	public Principal getUserPrincipal() {
		return principal;
	}
}
