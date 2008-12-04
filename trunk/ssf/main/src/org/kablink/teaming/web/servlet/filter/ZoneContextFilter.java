package org.kablink.teaming.web.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;


public class ZoneContextFilter  implements Filter {

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		ZoneContextHolder.setServerName(request.getServerName().toLowerCase());
		try {
			chain.doFilter(request, response);
		}
		finally {
			ZoneContextHolder.clear();
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

}
