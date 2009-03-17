package org.kablink.teaming.portletadapter.support;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.springframework.web.servlet.LocaleResolver;



public class UserLocaleResolver implements LocaleResolver {

	public Locale resolveLocale(PortletRequest portletRequest) {
		Locale userLocale = getUserLocale();
		if (userLocale == null) {
			return portletRequest.getLocale();
		}
		return userLocale;
	}

	public Locale resolveLocale(HttpServletRequest httpRequest) {
		Locale userLocale = getUserLocale();
		if (userLocale == null) {
			return httpRequest.getLocale();
		}
		return userLocale;
	}
	
	private Locale getUserLocale() {
		RequestContext rc = RequestContextHolder.getRequestContext();
		if (rc != null) {
			User user = rc.getUser();
			if (user != null) {
				return user.getLocale();
			}
		}
		return null;
	}

	public void setLocale(HttpServletRequest arg0, HttpServletResponse arg1, Locale locale) {
//		 unsupported
	}
	
	public void setLocale(PortletRequest arg0, PortletResponse arg1, Locale locale) {
		// unsupported
	}
	

}
