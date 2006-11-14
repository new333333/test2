package com.sitescape.ef.web.portlet.handler;

import java.io.PrintWriter;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.util.WebHelper;

public class LoginCheckInterceptor implements HandlerInterceptor {

	public boolean preHandle(PortletRequest request, PortletResponse response, Object handler) throws Exception {
		if(!WebHelper.isUserLoggedIn(request)) {
			// User not logged in. 
			// In this case we simply display a friendly message (if possible) 
			// to the user instead of throwing an exception. In other words we 
			// treat this as a normal circumstance rather than an error, because 
			// portals can allow user to view certain pages without logging in, 
			// and we must deal graciously with the situation where one or more
			// Aspen portlets are configured on that page. 
			if(response instanceof RenderResponse) {
				String message = NLT.get("portlet.requires.login", "Please log in to view this portlet.");
				RenderResponse res = (RenderResponse) response;
			    res.setContentType("text/html");
			    PrintWriter writer = res.getWriter();
				writer.print(message);
				writer.close();
			}
			return false;
		}
		else {
			return true;
		}
	}

	public void postHandle(RenderRequest request, RenderResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	public void afterCompletion(PortletRequest request, PortletResponse response, Object handler, Exception ex) throws Exception {
	}

}
