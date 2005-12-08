package com.sitescape.ef.web.servlet.filter;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.servlet.PrincipalServletRequest;

public class PrincipalInjectionFilter implements Filter {

	private FilterConfig filterConfig;
	
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		
		Principal principal = req.getUserPrincipal();
		
		HttpSession ses = req.getSession();
		
		if(principal == null) {
			// The request object has no information about authenticated user.
			// Note: It means that this is not a request made by the portal
			// through cross-context dispatch targeted to a SSF portlet. 
			principal = (Principal) ses.getAttribute(WebKeys.USER_PRINCIPAL);
			
			if (principal != null) {
				// Cached principal object is found in the session. 
				// Wrap the request object with the information about the
				// principal in it.
				PrincipalServletRequest reqWithPrincipal = new PrincipalServletRequest(req, principal);
				
				chain.doFilter(reqWithPrincipal, response);
			}
			else {
				// No principal object is cached in the session.
				// Note: This occurs when a SSF web component (either a servlet
				// or an adapted portlet) is accessed BEFORE at least one SSF
				// portlet is invoked  by the portal through regular cross-context
				// dispatch. 
				throw new ServletException("No user information available - Illegal request sequence.");
			}
		}
		else {
			// Actually, this condition is never met, hence never executed. 
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
	}

}
