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
			// User is not logged in. Redirect the user to the portal login page. 
			// Note: The JSP file below is a wrong one. Replace it!!!
			RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/forum/portal_login.jsp");
			dispatcher.forward(request, response);
		}
	}

	public void destroy() {
	}

}
