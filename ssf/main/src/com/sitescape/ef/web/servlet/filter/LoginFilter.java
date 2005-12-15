package com.sitescape.ef.web.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebHelper;

public class LoginFilter  implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;

		if(WebHelper.isUserLoggedIn(req)) {
			// User is logged in. Proceed as normal. 
			chain.doFilter(request, response);
		}
		else {
			// User is not logged in.
			String actionValue = request.getParameter("action");
			if(actionValue != null && actionValue.startsWith("__")) {
				// The action value indicates that the framework should allow
				// execution of the controller corresponding to the action value
				// even when the user is not authenticated (or previous 
				// authentication has expired). 
				// Note that this does NOT open up a security hole as one might
				// be tempted to think, because the action value to controller
				// mapping is done with the special prefix (i.e., "__") value
				// already taken into consideration. Therefore, user's attempt
				// to prefix the action value with the special characters to
				// bypass the filter's security check will bear no fruit since
				// the mapping doesn't exist. 
				
				request.setAttribute(WebKeys.UNAUTHENTICATED_REQUEST, Boolean.TRUE);
				
				chain.doFilter(request, response); // Proceed
			}
			else {
				// Unauthenticated access is not allowed. So don't bother 
				// invoking the servlet. Instead simply redirect the user to 
				// the portal login page.
				RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/forum/portal_login.jsp");
				dispatcher.forward(request, response);
			}
		}
	}

	public void destroy() {
	}

}
