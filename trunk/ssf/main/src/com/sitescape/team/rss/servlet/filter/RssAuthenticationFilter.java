package com.sitescape.team.rss.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.RequestUtils;

import com.sitescape.ef.context.request.RequestContextUtil;
import com.sitescape.team.domain.User;
import com.sitescape.team.security.authentication.AuthenticationManagerUtil;
import com.sitescape.team.security.authentication.PasswordDoesNotMatchException;
import com.sitescape.team.security.authentication.UserDoesNotExistException;

public class RssAuthenticationFilter implements Filter {

	protected final Log logger = LogFactory.getLog(getClass());

	public void init(FilterConfig arg0) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain chain) throws IOException, ServletException {
		String zoneName = RequestUtils.getRequiredStringParameter((HttpServletRequest) request, "zn");
		Long userId = RequestUtils.getRequiredLongParameter((HttpServletRequest) request, "ui");
		String passwordDigest = RequestUtils.getRequiredStringParameter((HttpServletRequest) request, "pd"); 
		
		try {
			User user = AuthenticationManagerUtil.authenticate(zoneName, userId, passwordDigest);
			//don't set user, session is not currently active
			RequestContextUtil.setThreadContext(user.getZoneId(), user.getId());
			
			chain.doFilter(request, response); // Proceed
			
			//RequestContextUtil.setThreadContext(user);
		}
		catch(UserDoesNotExistException e) {
			logger.warn(e);
			throw new ServletException(e);
		}
		catch(PasswordDoesNotMatchException e) {
			logger.warn(e);
			throw new ServletException(e);
		}
	}

	public void destroy() {
	}
}
